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

public class CreateChannelGuaranteeCodeMappingOWS extends WSSetUp {
	public boolean createReservationTypeMapping(String dataset) {
		try {
			
					String req_createreservationtype = WSClient.createSOAPMessage("CreateChannelGuaranteeCodeMapping", dataset);
					String res_createreservationtype = WSClient.processSOAPMessage(req_createreservationtype);
					if(WSAssert.assertIfElementExists(res_createreservationtype, "CreateChannelGuaranteeCodeMappingRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Reservation Type Mapping");
						return true;
					}
					else {
						if(WSAssert.assertIfElementExists(res_createreservationtype, "CreateChannelGuaranteeCodeMappingRS_Errors_Error_ShortText", false)) {
							String actual_msg = WSAssert.getElementValue(res_createreservationtype, "CreateChannelGuaranteeCodeMappingRS_Errors_Error_ShortText", XMLType.RESPONSE);
							if(actual_msg.contains("exists")) {
								WSClient.writeToReport(LogStatus.INFO, "Reservation Type Mapping Already Exists");
								return true;
							}
							else
								return false;
						}
					
					}
					return false;
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}	
	@Test(groups= {"OWS"}, dependsOnGroups = {"createGuaranteeMapping"})
	public void createMultiple_ReservationTypeMapping() {
		int i;
		boolean flag = true;
		String testName = "createReservationTypeMappingOWS";
		WSClient.startTest(testName, "Create Reservation Type Mapping OWS", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_ChannelCode}", OWSLib.getChannel());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCodeOWS("ReservationType") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OWS","ReservationType");
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
			String loc_value = OperaPropConfig.getDataSetForCode("ReservationType" , dependencies.get("ReservationType"));
			//WSClient.writeToReport(LogStatus.INFO, loc_value);
			String value = OperaPropConfig.getChannelCodeForDataSet("ReservationType" , dataset);
			WSClient.setData("{var_PaymentMethod}", OperaPropConfig.getDataSetForCode("PaymentMethod", dataset));
			WSClient.setData("{var_RevType}", loc_value);
			WSClient.setData("{var_ExtRevType}", value);
			flag = flag && createReservationTypeMapping(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ReservationTypeMappingOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ReservationTypeMappingOWS", "N");
	}

}

