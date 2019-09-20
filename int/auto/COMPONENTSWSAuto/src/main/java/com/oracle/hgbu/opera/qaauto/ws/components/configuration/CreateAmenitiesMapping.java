package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateAmenitiesMapping extends WSSetUp {

	private boolean createAmenityMapping(String dataset) {
		String query;
		LinkedHashMap<String, String> amenities;
		try {
			query = WSClient.getQuery("CreateAmenitiesMapping", "QS_01");

			String amenity = OperaPropConfig.getDataSetForCode("AmenityCode", dataset);
			WSClient.setData("{var_amenityCode}", amenity);
			String feature = OperaPropConfig.getDataSetForCode("FeatureCode", dataset);
			WSClient.setData("{var_feature}", feature);

			amenities = WSClient.getDBRow(query);
			if (amenities.get("AMENITY_CODE") == null) {
				String createAmenitiesReq = WSClient.createSOAPMessage("CreateAmenitiesMapping", dataset);
				String createAmenitiesRes = WSClient.processSOAPMessage(createAmenitiesReq);
				if (WSAssert.assertIfElementExists(createAmenitiesRes, "CreateAmenitiesMappingRS_Success", true)) {
					amenities = WSClient.getDBRow(query);
					if (amenities.get("AMENITY_CODE") == null) {
						WSClient.writeToReport(LogStatus.INFO,
								"Mapping for " + amenity + " and " + feature + " got inserted into DB");
						return true;
					} else {
						WSClient.writeToReport(LogStatus.INFO,
								"Mapping for " + amenity + " and " + feature + " did not get inserted into DB");
						return false;
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Mapping for " + amenity + " and " + feature + " failed");
					return false;
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO,
						"Mapping for " + amenity + " and " + feature + " already exists");
				return true;
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured is :" + e.toString());
			e.printStackTrace();
		}
		return false;

	}

	@Test(groups = { "OperaConfig", "CreateAmenitiesMapping" }, dependsOnGroups = { "createAmenities" })
	public void createAmentiesMapping() {
		int i;
		boolean flag = true;
		String testName = "CreateAmenitiesMapping";
		WSClient.startTest(testName, "Create Multiple Amenity Mapping", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_resort}", OPERALib.getResort());
		WSClient.setData("{var_chain}", OPERALib.getChain());
		WSClient.setData("{var_channel}", OWSLib.getChannel());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("FeatureCode") - 1;
		for (i = 1; i <= length; i++) {
			dataset = "DS_0" + i;
			String value = OperaPropConfig.getDataSetForCode("FeatureCode", dataset);
			String amenity = OperaPropConfig.getDataSetForCode("AminityCode", dataset);
			WSClient.setData("{var_feature}", value);
			WSClient.setData("{var_amenityCode}", amenity);
			flag = flag && createAmenityMapping(dataset);
		}
		if (flag == true)
			OperaPropConfig.setPropertyConfigResults("FeatureCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("FeatureCode", "N");
	}

}
