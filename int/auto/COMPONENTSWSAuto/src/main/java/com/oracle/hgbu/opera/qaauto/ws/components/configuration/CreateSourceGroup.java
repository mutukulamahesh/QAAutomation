package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateSourceGroup extends WSSetUp {
	
	

	public boolean createSourceGroup(String dataset) {

		try {
			
			String createSourceGroupReq = WSClient.createSOAPMessage("CreateSourceGroup", dataset);
			String createSourceGroupRes = WSClient.processSOAPMessage(createSourceGroupReq);
				if (WSAssert.assertIfElementExists(createSourceGroupRes, "CreateSourceGroupRS_Success",true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateSourceGroupRS/Success exists on the response message");
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Source Group");
						return true;
						
				} 
				else {
						if(WSAssert.assertIfElementExists(createSourceGroupRes, "CreateSourceGroupRS_Errors_Error_ShortText", false)) {
							String hotel_code = WSAssert.getElementValue(createSourceGroupReq, "CreateSourceGroupRQ_SourceGroup_HotelCode", XMLType.REQUEST);
							String group_code = WSAssert.getElementValue(createSourceGroupReq, "CreateSourceGroupRQ_SourceGroup_Code", XMLType.REQUEST);
							String expected_msg = group_code + " in " + hotel_code + " already exists";
							String actual_msg = WSAssert.getElementValue(createSourceGroupRes, "CreateSourceGroupRS_Errors_Error_ShortText", XMLType.RESPONSE);
							if(WSAssert.assertEquals(expected_msg, actual_msg, false)) {
								WSClient.writeToReport(LogStatus.INFO, "Source Group Already Exists");
								return true;
							}
							else
								return false;
						}
						else 
							return false;
						
					}
			
			}
		 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	@Test(groups= {"createSourceGroup","bat"})
	public void createMultiple_SourceGroup() {
		int i;
		boolean flag = true;
		String testName = "CreateSourceGroup";
		WSClient.startTest(testName, "Create Source Group", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("SourceGroup") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("SourceGroup" , dataset);
			WSClient.setData("{var_SourceGroup}", value);
			flag = flag && createSourceGroup(dataset);
		}
		

		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("SourceGroup", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("SourceGroup", "N");
	}
}