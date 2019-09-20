package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateReservationTypeSchedule extends WSSetUp {
	public boolean createReservationTypeSchedule(String dataset) {
		try {
			
			
			String req_fetchreservationtypeSchedule = WSClient.createSOAPMessage("FetchGuaranteeCodeSchedules", "DS_01");
			String res_fetchresevationtypeSchedule = WSClient.processSOAPMessage(req_fetchreservationtypeSchedule);
			
			if(WSAssert.assertIfElementExists(res_fetchresevationtypeSchedule, "FetchGuaranteeCodeSchedulesRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchresevationtypeSchedule, "ScheduleDetail_ApplicableCodes_GuaranteeCode", true)) {
					WSClient.writeToReport(LogStatus.INFO, "ReservationType Schedule already exists");
					return true;
				}
			
				else {
					WSClient.writeToReport(LogStatus.INFO, "ReservationType Schedule does not  exist!!");
					String req_createreservationtypeSchedule = WSClient.createSOAPMessage("CreateGuaranteeCodeSchedule", dataset);
					String res_createreservationtypeSchedule = WSClient.processSOAPMessage(req_createreservationtypeSchedule);
					if(WSAssert.assertIfElementExists(res_createreservationtypeSchedule, "CreateGuaranteeCodeScheduleRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Reservation Type Schedule");
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
	
	
	@Test(groups= {"OperaConfig"}, dependsOnGroups = {"createReservationType"})
	public void createMultiple_ReservationType() {
		int i;
		boolean flag = true;
		String testName = "createReservationTypeSchedule";
		WSClient.startTest(testName, "Create Reservation Type Schedule", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("ReservationType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("ReservationType" , dataset);
			WSClient.setData("{var_RevType}", value);
			flag = flag && createReservationTypeSchedule(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ReservationTypeSchedule", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ReservationTypeSchedule", "N");
	}
}
