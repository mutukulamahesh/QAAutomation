package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateGuaranteesMappingOWS extends WSSetUp {
	public boolean createReservationType(String dataset) {
		try {
			
			
			String req_fetchreservationtype = WSClient.createSOAPMessage("FetchGuaranteesMapping", "DS_01");
			String res_fetchresevationtype = WSClient.processSOAPMessage(req_fetchreservationtype);
			
			if(WSAssert.assertIfElementExists(res_fetchresevationtype, "FetchGuaranteesMappingRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchresevationtype, "GuaranteesMapping_Guarantee_LocalSystemCode", true)) {
					WSClient.writeToReport(LogStatus.INFO, "ReservationType already exists");
					return true;
				}
			
				else {
					WSClient.writeToReport(LogStatus.INFO, "ReservationType does not  exist!!");
					String req_createreservationtype = WSClient.createSOAPMessage("CreateGuaranteesMapping", dataset);
					String res_createreservationtype = WSClient.processSOAPMessage(req_createreservationtype);
					if(WSAssert.assertIfElementExists(res_createreservationtype, "CreateGuaranteesMappingRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Reservation Type");
						return true;
					}
					else
						return false;
			}	
		}
			else
				return false;
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	
	@Test(groups= {"OWS", "createGuaranteeMapping"}, dependsOnGroups = {"createPropertyMapping"})
	public void createMultiple_ReservationType() {
		int i;
		boolean flag = true;
		String testName = "createReservationTypeOWS";
		WSClient.startTest(testName, "Create Reservation Type OWS", "OperaConfig");
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
			WSClient.writeToReport(LogStatus.INFO, loc_value);
			String value = OperaPropConfig.getChannelCodeForDataSet("ReservationType" , dataset);
			WSClient.setData("{var_RevType}", loc_value);
			WSClient.setData("{var_ExtRevType}", value);
			flag = flag && createReservationType(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ReservationTypeOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ReservationTypeOWS", "N");
	}

}
