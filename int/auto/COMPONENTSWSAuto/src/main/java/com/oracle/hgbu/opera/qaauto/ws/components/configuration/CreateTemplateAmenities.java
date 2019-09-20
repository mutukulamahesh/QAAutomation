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

public class CreateTemplateAmenities extends WSSetUp {

	private boolean createAmenities(String dataset) {
		String query;
		LinkedHashMap<String, String> amenities;
		try {
			WSClient.setData("{var_feature}", OperaPropConfig.getDataSetForCode("FeatureCode", dataset));
			query = WSClient.getQuery("CreateTemplateAmenities", "QS_01");
			amenities = WSClient.getDBRow(query);
			if (amenities.get("FEATURE") == null) {
				String createTemplatesReq = WSLib.createSOAPMessage("CreateTemplateAmenities", dataset);
				String createTemplatesRes = WSClient.processSOAPMessage(createTemplatesReq);
				if (WSAssert.assertIfElementExists(createTemplatesRes, "CreateTemplateAmenitiesRS_Success", true)) {
					amenities = WSClient.getDBRow(query);
					if (amenities.get("FEATURE") != null) {
						WSClient.writeToReport(LogStatus.INFO,
								"Feature code " + amenities.get("FEATURE") + " got created successfully");
						return true;
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Feature code " + OperaPropConfig.getDataSetForCode("FeatureCode", dataset)
										+ " did not get inserted into DB");
						return false;
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Feature code "
							+ OperaPropConfig.getDataSetForCode("FeatureCode", dataset) + " creation failed");
					return false;
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Feature code " + amenities.get("FEATURE") + " already exists ");
				return true;
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured is :" + e.toString());
			e.printStackTrace();
		}
		return false;

	}

	@Test(groups = { "OperaConfig", "createTemplateAmenities" })
	public void createAmentiesMapping() {
		int i;
		boolean flag = true;
		String testName = "CreateTemplateAmenities";
		WSClient.startTest(testName, "Create Multiple Amenity Templates", "OperaConfig");
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
			flag = flag && createAmenities(dataset);
		}
		if (flag == true)
			OperaPropConfig.setPropertyConfigResults("FeatureCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("FeatureCode", "N");
	}
}
