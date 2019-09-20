package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class ConversionCodesHTNG extends WSSetUp {

	List<LinkedHashMap<String,String>> data = new ArrayList<LinkedHashMap<String,String>>();


	public void getdb_data() throws Exception {
		String query = WSClient.getQuery("CreateConversionCodeMappings", "QS_01");
		//WSClient.writeToReport(LogStatus.INFO, query);
		data = WSClient.getDBRows(query);
		//WSClient.writeToReport(LogStatus.INFO, data.toString());
	}


	public int match_data(String ConvCode, String PMSVal, String EXTVal) {
		Iterator<LinkedHashMap<String, String>> it = data.iterator();
		int flag = 0;
		while(it.hasNext()) {
			HashMap<String, String> tmp = it.next();
			//WSClient.writeToReport(LogStatus.INFO, tmp.get("CONVERSION_CODE")+ " " + ConvCode + " " + tmp.get("PMS_VALUE") + " " + PMSVal + " " + tmp.get("EXT_VALUE") + " " + EXTVal );

			if(tmp.get("CONVERSION_CODE").equals(ConvCode) && tmp.get("PMS_VALUE").equals(PMSVal)) {
				if(tmp.get("EXT_VALUE").equals(EXTVal)) {
					WSClient.writeToReport(LogStatus.INFO, "Conversion Code Already Exists.");
					return 2;
				}
				else {
					//WSClient.writeToReport(LogStatus.INFO, "Return 1");
					WSClient.setData("{var_SeqNo}", tmp.get("CCD_SEQ_NO"));
					flag= 1;
				}
			}
		}
		//WSClient.writeToReport(LogStatus.INFO, "Return 0");
		return flag;
	}

	public boolean check_pmsValue(String pmsValue, String queryId) throws Exception {
		WSClient.writeToReport(LogStatus.INFO, "----------<b>Checking if the PMS Value exists</b>----------");
		LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
		String query = WSClient.getQuery("ChangeConversionCodeMappings", queryId);
		dbResult = WSClient.getDBRow(query);
		String val = dbResult.get("COUNT");
		if(WSAssert.assertEquals("1", val, false)) {
			return true;
		}
		else
			return false;
	}

	public boolean updateCodes() {
		try {
			String updateConvCodeReq = WSClient.createSOAPMessage("ChangeConversionCodeMappings", "DS_01");
			String updateConvCodeRes = WSClient.processSOAPMessage(updateConvCodeReq);
			if(WSAssert.assertIfElementExists(updateConvCodeRes, "ChangeConversionCodeMappingsRS_Success", false)){
				WSClient.writeToReport(LogStatus.INFO, "Successfully Updated Conversion Code");

				//DB Validation
				return true;
			}
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	public boolean createCodes(){
		try{
			WSClient.writeToReport(LogStatus.INFO, "Conversion Code doesnot exist!!");
			String createConvCodeReq=WSClient.createSOAPMessage("CreateConversionCodeMappings", "DS_01");
			String createConvCodeRes=WSClient.processSOAPMessage(createConvCodeReq);
			if(WSAssert.assertIfElementExists(createConvCodeRes, "CreateConversionCodeMappingsRS_Success", false)){
				WSClient.writeToReport(LogStatus.INFO, "Successfully Created Conversion Code");

				//DB Validation

				return true;
			}
			else
				return false;
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.FAIL, "Exception ocuured due to :" +e);
			e.printStackTrace();
			return false;
		}
	}


	@Test(groups= {"createConversionCodes"})
	@Parameters({"interfaceName"})
	public void create_ConversionCodes(String interfaceName) throws Exception {
		int i;
		boolean flag = true;
		HashMap<String, String> temp = new HashMap<>();
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String testName="ConversionCodes";
		WSClient.startTest(testName, "Create Conversion Codes for HTNG", "createConversionCodes");
		List<HashMap<String,String>> htngData = OperaPropConfig.getHtngHashMap();
		int hashmapSize = htngData.size();
		String htng_interface = interfaceName;
		String htng_code = htng_interface.length() < 2 ? htng_interface : htng_interface.substring(0, 2);
		WSClient.setData("{var_Interface}", htng_interface);
		//WSClient.writeToReport(LogStatus.INFO, Integer.toString(hashmapSize));
		getdb_data();
		for(i=0;i<hashmapSize;i++) {
			temp = htngData.get(i);
			//WSClient.writeToReport(LogStatus.WARNING, temp.toString());
			if(temp.get("CONVERSION_CODE").contains("GLOBAL")) {
				WSClient.setData("{var_ConvCode}", temp.get("CONVERSION_CODE").replace("_GLOBAL", ""));
			}else {
				WSClient.setData("{var_ConvCode}", temp.get("CONVERSION_CODE"));
			}
			//WSClient.writeToReport(LogStatus.WARNING, temp.get("CONVERSION_CODE"));
			if(temp.get("EXT_VALUE").equalsIgnoreCase("PASSPORT") || temp.get("EXT_VALUE").equalsIgnoreCase("MALE") || temp.get("EXT_VALUE").equalsIgnoreCase("FEMALE"))
				WSClient.setData("{var_EXTValue}", temp.get("EXT_VALUE"));
			else
				WSClient.setData("{var_EXTValue}", htng_code  + temp.get("EXT_VALUE"));

			//WSClient.writeToReport(LogStatus.WARNING, WSClient.getData("{var_EXTValue}"));
			String pmsValue = temp.get("PMS_VALUE");
			String dataset  = temp.get("DATASET_ID");
			WSClient.setData("{var_PMSValue}", OperaPropConfig.getDataSetForCode(pmsValue, dataset));
			//WSClient.writeToReport(LogStatus.WARNING, WSClient.getData("{var_PMSValue}"));
			if(temp.containsKey("MASTER_VALUE")) {
				String mValue = temp.get("MASTER_VALUE");
				WSClient.setData("{var_MasterValue}", OperaPropConfig.getDataSetForCode(mValue, dataset));
			}
			else
				WSClient.setData("{var_MasterValue}", "");
			//WSClient.writeToReport(LogStatus.WARNING, WSClient.getData("{var_MasterValue}"));
			String variable = WSClient.getData("{var_PMSValue}");
			if(!temp.get("CONVERSION_CODE").equalsIgnoreCase("GENDER_MF")){
				if(check_pmsValue(variable, temp.get("CONVERSION_CODE"))) {
					int check = match_data(WSClient.getData("{var_ConvCode}") ,WSClient.getData("{var_PMSValue}"), WSClient.getData("{var_EXTValue}"));
					if (check == 0)
						flag = flag && createCodes();
					else if(check == 1)
						flag = flag && updateCodes();
				}
				else
					WSClient.writeToReport(LogStatus.WARNING, "PMS Value " + variable + " not created");
			}else{
				int check = match_data(WSClient.getData("{var_ConvCode}") ,WSClient.getData("{var_PMSValue}"), WSClient.getData("{var_EXTValue}"));
				if (check == 0)
					flag = flag && createCodes();
				else if(check == 1)
					flag = flag && updateCodes();
			}
		}

		if(flag == true)
			OperaPropConfig.setPropertyConfigResults("ConversionCodesHTNG", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ConversionCodesHTNG", "N");

	}

}