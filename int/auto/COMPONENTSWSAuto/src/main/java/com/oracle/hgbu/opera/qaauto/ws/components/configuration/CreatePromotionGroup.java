package com.oracle.hgbu.opera.qaauto.ws.components.configuration;


import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreatePromotionGroup extends WSSetUp{
	public boolean createPromotionGroup(String dataset){
		try{
			
			String fetchPromoGroupReq=WSClient.createSOAPMessage("FetchPromotionGroups", dataset);
			String fetchPromoGroupRes=WSClient.processSOAPMessage(fetchPromoGroupReq);
			if(WSAssert.assertIfElementExists(fetchPromoGroupRes, "FetchPromotionGroupsRS_Success", false)) {
				if(WSAssert.assertIfElementExists(fetchPromoGroupRes, "PromotionGroups_PromotionGroups_PromotionGroup_PromotionGroup", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Promotion Group already exists");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Promotion Group doesnot exist!!");
					String createPromoGroupReq=WSClient.createSOAPMessage("CreatePromotionGroup", dataset);
					String createPromoGroupRes=WSClient.processSOAPMessage(createPromoGroupReq);
					if(WSAssert.assertIfElementExists(createPromoGroupRes, "CreatePromotionGroupRS_Success", false)){
						WSClient.writeToReport(LogStatus.INFO, "Successfully Created Promotion Group");
						
						//DB Validation
						
						return true;
					}
					else
						return false;
				}
			
			}
			else
				return false;
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
			return false;
		} 
	}

	@Test(groups= {"createPromotionGroup"})
	public void createMultiple_PromotionGroups() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName="PromotionGroup";
		WSClient.startTest(testName, "Create Promotion Group", "createPromotionGroup");
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("PromotionGroup") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("PromotionGroup" , dataset);
			WSClient.setData("{var_PromoGroup}", value);
			flag = flag && createPromotionGroup(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("PromotionGroup", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("PromotionGroup", "N");
}

}
