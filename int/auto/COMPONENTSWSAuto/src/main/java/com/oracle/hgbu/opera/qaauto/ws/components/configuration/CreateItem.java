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

public class CreateItem extends WSSetUp {
	public boolean createItem(String dataset){
		try{
			
//			String fetchItemsReq=WSClient.createSOAPMessage("FetchInventoryItems", "DS_01");
//			String fetchItemsRes=WSClient.processSOAPMessage(fetchItemsReq);
//			if(WSAssert.assertIfElementExists(fetchItemsRes, "FetchInventoryItemsRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(fetchItemsRes, "Items_Item_ItemCode", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Item already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Item Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateInventoryItems", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Item " + WSClient.getData("{var_ItemName}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Item doesnot exist!!");
					String createItemReq=WSClient.createSOAPMessage("CreateInventoryItems", dataset);
					String createItemRes=WSClient.processSOAPMessage(createItemReq);
					if(WSAssert.assertIfElementExists(createItemRes, "CreateInventoryItemsRS_Success", true)){
						WSClient.writeToReport(LogStatus.PASS, "//CreateInventoryItemsRS/Success exists on the response message");
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("CreateInventoryItems", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Item");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Item not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createItemRes, "CreateInventoryItemsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createItemRes, "CreateInventoryItemsRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
				}
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
			return false;
		} 
	}

	@Test(groups= {"OperaConfig", "createItem"}, dependsOnGroups= {"createItemClass", "createRevenueType"})
	public void createMultiple_RevenueGroup() throws Exception {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		String testName="InventoryItem";
		WSClient.startTest(testName, "Create Inventory Item", "OperaConfig");
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","ItemCode");
		
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("ItemCode") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			if(comm==null)
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			dependencies = OperaPropConfig.getDependency(comm);
			WSClient.setData("{var_ItemClass}", OperaPropConfig.getDataSetForCode("ItemClass", dependencies.get("ItemClass")));
			WSClient.setData("{var_RevenueType}", OperaPropConfig.getDataSetForCode("RevenueType", dependencies.get("RevenueType")));
			
			//get item class id
			String query = WSClient.getQuery("CreateItemClasses", "QS_02");
			String id = WSClient.getDBRow(query).get("ITEMCLASS_ID");
			WSClient.setData("{var_ItemClassID}", id);
			String item_code = OperaPropConfig.getDataSetForCode("ItemCode" ,dataset);
			WSClient.setData("{var_ItemName}", OperaPropConfig.getDataSetForCode("ItemName", dependencies.get("ItemName")));
			WSClient.setData("{var_ItemCode}", item_code);
			flag = flag && createItem(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ItemCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ItemCode", "N");
}


}
