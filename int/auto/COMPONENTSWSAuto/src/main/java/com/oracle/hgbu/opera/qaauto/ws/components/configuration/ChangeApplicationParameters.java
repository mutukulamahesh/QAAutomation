package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class ChangeApplicationParameters extends WSSetUp{

	public boolean changeParameter() {
		try {
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Application Parameter Value</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("ChangeApplicationSettings", "QS_02");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("VALUE");
			String param_value = WSClient.getData("{var_settingValue}");
			if(WSAssert.assertEquals(param_value, val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Application Parameter already set to desired value");
				return true;
			}
			else {
				String req_changeAppParam = WSClient.createSOAPMessage("ChangeApplicationSettings", "DS_01");
				String res_changeAppParam = WSClient.processSOAPMessage(req_changeAppParam);
				if(WSAssert.assertIfElementExists(res_changeAppParam, "ChangeApplicationSettingsRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "Success flag exists on the response message");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("VALUE");
					if(WSAssert.assertEquals(param_value, val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "Application Parameter Successfully Updated");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Application Parameter Not Updated");
						return false;
					}
				}
				else{
					if(WSAssert.assertIfElementExists(res_changeAppParam, "ChangeApplicationSettingsRS_Errors_Error_ShortText", true)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_changeAppParam, "ChangeApplicationSettingsRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Failed to update the parameter. Success flag doesn't exist on the response message");
					}
					return false;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	@Test(groups= {"OperaConfig"})
	public void change_AppParameter() throws Exception {

		int i;
		boolean flag = true;
		HashMap<String, String> temp = new HashMap<>();
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_resort}",OPERALib.getResort());
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String testName="ChangeApplicationParametersSettings";
		WSClient.startTest(testName, "Change Application Parameters Settings", "OperaConfig");
		List<HashMap<String,String>> paramData = OperaPropConfig.getAppParamHashMap();
		int hashmapSize = paramData.size();
		for(i=0;i<hashmapSize;i++) {
			temp = paramData.get(i);
			WSClient.setData("{var_parameter}", temp.get("ParameterName"));
			WSClient.setData("{var_settingValue}", temp.get("Value"));
			flag = flag && changeParameter();
		}

		if(flag == true)
			OperaPropConfig.setPropertyConfigResults("ApplicationParameters", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ApplicationParameters", "N");

	}
}
