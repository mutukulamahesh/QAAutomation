package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateAmenities extends WSSetUp {

	private boolean createAmenity(String dataset) {
		try {
			String amenity = OperaPropConfig.getDataSetForCode("AmenityCode", dataset);
			WSClient.setData("{var_amenityCode}", OperaPropConfig.getDataSetForCode("AmenityCode", dataset));
			String query = WSClient.getQuery("CreateAmenities", "QS_01");
			LinkedHashMap<String, String> amenities = WSClient.getDBRow(query);
			if (amenities.get("FEATURE") == null) {
				String createAmenitiesReq = WSLib.createSOAPMessage("CreateAmenities", dataset);
				String createAmenitiesRes = WSClient.processSOAPMessage(createAmenitiesReq);
				if (WSAssert.assertIfElementExists(createAmenitiesRes, "CreateAmenitiesRS_Success", true)) {
					amenities = WSClient.getDBRow(query);
					if (amenities.get("FEATURE") == null) {
						WSClient.writeToReport(LogStatus.FAIL,
								"Amenity code " + amenity + " did not get inserted into DB");
						return false;
					} else {
						WSClient.writeToReport(LogStatus.INFO, "Amenity code " + amenity + " got inserted into DB");
						return true;
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Amenity code " + amenity + " creation failed");
					return false;
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Amenity code " + amenity + " already exists");
				return true;
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured is :" + e.toString());
			e.printStackTrace();
		}
		return false;
	}

	@Test(groups = { "OperaConfig", "createAmenities" }, dependsOnGroups = { "createTemplateAmenities" })
	public void createAmenties() {
		int i;
		boolean flag = true;
		String testName = "CreateAmenities";
		WSClient.startTest(testName, "Create Multiple Amenities", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_resort}", OPERALib.getResort());
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("FeatureCode") - 1;

		for (i = 1; i <= length; i++) {
			dataset = "DS_0" + i;
			String value = OperaPropConfig.getDataSetForCode("FeatureCode", dataset);
			String amenity = OperaPropConfig.getDataSetForCode("AminityCode", dataset);
			WSClient.setData("{var_feature}", value);
			WSClient.setData("{var_amenityCode}", amenity);
			flag = flag && createAmenity(dataset);
		}
		if (flag == true)
			OperaPropConfig.setPropertyConfigResults("FeatureCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("FeatureCode", "N");
	}

}
