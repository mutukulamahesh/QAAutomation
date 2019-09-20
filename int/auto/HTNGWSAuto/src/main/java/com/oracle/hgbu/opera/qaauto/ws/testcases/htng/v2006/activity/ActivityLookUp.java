package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2006.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class ActivityLookUp extends WSSetUp {

	/**
	 * @author heegupta Description: Verify that the activities scheduled for
	 *         the Opera profile are being returned through the Look-up activity
	 *         call upon submitting a look up request with an Opera Profile
	 *         Identifier
	 */

	@Test(groups = { "sanity", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20451() {
		try {
			String testName = "activityLookUp_2006_20451";
			WSClient.startTest(testName,
					"Verify that the activities scheduled for the Opera profile are being returned through the Look-up activity call upon submitting a look up request with an Opera Profile Identifier",
					"sanity");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

							// Activity Look Up
							String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_01");
							String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
									"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validating the data
								List<LinkedHashMap<String, String>> dbResult = new ArrayList<>();
								dbResult = WSClient.getDBRows(WSClient.getQuery("QS_01"));

								// String name_id =
								// WSAssert.getElementValue(res_activityLookUp,"ActivityLookup_ProfileID_UniqueID",
								// XMLType.RESPONSE);
								HashMap<String, String> res_values = new HashMap<>();
								res_values.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

								List<LinkedHashMap<String, String>> resResult = WSAssert
										.getMultipleNodeList(res_activityLookUp, res_values, false, XMLType.RESPONSE);

								if (WSAssert.assertIfElementExists(res_activityLookUp,
										"ActivityLookup_ProfileID_UniqueID", false)) {
									WSAssert.assertEquals(resResult, dbResult, false);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
								}
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author nilsaini Description: Verify that the activities scheduled for
	 *         the Opera profile are being returned through the Look-up activity
	 *         call upon submitting a look up request with an Opera Profile
	 *         Identifier
	 */

	@Test(groups = { "minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20429() {
		try {
			String testName = "activityLookUp_2006_20429";
			WSClient.startTest(testName,
					"Verify that the Person Name of the guest for which the scheduled activities are requested is correctly returned by the Activity Look-up call",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "STATE"));
			WSClient.setData("{var_actstatus}",
					HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
			WSClient.setData("{var_acttype}",
					HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
			WSClient.setData("{var_actlocation}",
					HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

							// Activity Look Up
							String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_01");
							String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
									"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validating the data
								List<LinkedHashMap<String, String>> dbResult = new ArrayList<>();
								dbResult = WSClient.getDBRows(WSClient.getQuery("QS_01"));

								// String name_id =
								// WSAssert.getElementValue(res_activityLookUp,
								// "ActivityLookup_ProfileID_UniqueID",
								// XMLType.RESPONSE);
								HashMap<String, String> res_values = new HashMap<>();
								res_values.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

								List<LinkedHashMap<String, String>> resResult = WSClient
										.getMultipleNodeList(res_activityLookUp, res_values, false, XMLType.RESPONSE);

								if (WSAssert.assertIfElementExists(res_activityLookUp,
										"ActivityLookup_ProfileID_UniqueID", false)) {
									WSAssert.assertEquals(resResult, dbResult, false);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
								}

								HashMap<String, String> xPath = new HashMap<>();
								xPath.put("ActivityLookup_PersonName_FirstName",
										"ActivityLookupList_ActivityLookup_PersonName");
								xPath.put("ActivityLookup_PersonName_LastName",
										"ActivityLookupList_ActivityLookup_PersonName");
								LinkedHashMap<String, String> resResult1 = WSClient
										.getSingleNodeList(res_activityLookUp, xPath, false, XMLType.RESPONSE);

								LinkedHashMap<String, String> dbResult1 = WSClient.getDBRow(WSClient.getQuery("QS_02"));

								WSAssert.assertEquals(resResult1, dbResult1, false);

							}

						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	// }

	/**
	 * @author nilsaini Description: Verify that the activities scheduled for
	 *         the Opera profile are being returned through the Look-up activity
	 *         call upon submitting a look up request with an Opera Profile
	 *         Identifier
	 */

	@Test(groups = { "minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG", "activityLookUp_2006_20430" })
	public void activityLookUp_2006_20430() {
		try {
			String testName = "activityLookUp_2006_20430";
			WSClient.startTest(testName,
					"Verify that the Address details of the guest for which the scheduled activities are requested is correctly returned by the Activity Look-up call",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));

			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE", "PHONE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_03");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);
						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

							// Activity Look Up
							String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_01");
							String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}

							if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
									"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validating the data
								List<LinkedHashMap<String, String>> dbResult = new ArrayList<>();
								dbResult = WSClient.getDBRows(WSClient.getQuery("QS_01"));

								// String name_id =
								// WSAssert.getElementValue(res_activityLookUp,
								// "ActivityLookup_ProfileID_UniqueID",
								// XMLType.RESPONSE);
								HashMap<String, String> res_values = new HashMap<>();
								res_values.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

								List<LinkedHashMap<String, String>> resResult = WSClient
										.getMultipleNodeList(res_activityLookUp, res_values, false, XMLType.RESPONSE);

								if (WSAssert.assertIfElementExists(res_activityLookUp,
										"ActivityLookup_ProfileID_UniqueID", false)) {
									WSAssert.assertEquals(resResult, dbResult, false);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
								}

								HashMap<String, String> xPath = new HashMap<>();
								xPath.put("ActivityLookup_Address_AddressLine",
										"ActivityLookupList_ActivityLookup_Address");
								xPath.put("ActivityLookup_Address_CityName",
										"ActivityLookupList_ActivityLookup_Address");
								xPath.put("ActivityLookup_Address_StateProv",
										"ActivityLookupList_ActivityLookup_Address");
								LinkedHashMap<String, String> resResult1 = WSClient
										.getSingleNodeList(res_activityLookUp, xPath, false, XMLType.RESPONSE);

								LinkedHashMap<String, String> dbResult1 = WSClient.getDBRow(WSClient.getQuery("QS_03"));
								WSAssert.assertEquals(dbResult1, resResult1, false);
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	// }

	/**
	 * @author nilsaini Description: Verify that the activities scheduled for
	 *         the Opera profile are being returned through the Look-up activity
	 *         call upon submitting a look up request with an Opera Profile
	 *         Identifier
	 */

	@Test(groups = { "minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20431() {
		try {
			String testName = "activityLookUp_2006_20431";
			WSClient.startTest(testName,
					"Verify that the Activity Information associated to the requested guest is correctly returned by the Activity Look-up call",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

							// Activity Look Up
							String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_01");
							String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
									"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validating the data
								List<LinkedHashMap<String, String>> dbResult = new ArrayList<>();
								dbResult = WSClient.getDBRows(WSClient.getQuery("QS_01"));

								// String name_id =
								// WSAssert.getElementValue(res_activityLookUp,
								// "ActivityLookup_ProfileID_UniqueID",
								// XMLType.RESPONSE);
								HashMap<String, String> res_values = new HashMap<>();
								res_values.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

								List<LinkedHashMap<String, String>> resResult = WSClient
										.getMultipleNodeList(res_activityLookUp, res_values, false, XMLType.RESPONSE);

								if (WSAssert.assertIfElementExists(res_activityLookUp,
										"ActivityLookup_ProfileID_UniqueID", false)) {
									WSAssert.assertEquals(resResult, dbResult, false);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
								}

								HashMap<String, String> xPath = new HashMap<>();
								xPath.put("Activities_Activity_ActivityType", "ActivityLookup_Activities_Activity");
								xPath.put("Activities_Activity_Location", "ActivityLookup_Activities_Activity");
								xPath.put("ActivityLookup_Activities_Activity_status",
										"ActivityLookup_Activities_Activity");
								xPath.put("Activities_Activity_Note", "ActivityLookup_Activities_Activity");
								xPath.put("Activity_TimeSpan_Start", "ActivityLookup_Activities_Activity");
								xPath.put("Activity_TimeSpan_End", "ActivityLookup_Activities_Activity");

								LinkedHashMap<String, String> resResult1 = WSClient
										.getSingleNodeList(res_activityLookUp, xPath, false, XMLType.RESPONSE);
								String QS_04 = WSClient.getQuery("QS_04");

								LinkedHashMap<String, String> dbResult1 = WSClient.getDBRow(QS_04);
								WSAssert.assertEquals(resResult1, dbResult1, false);
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/* @author ketvaidy */
	@Test(groups = { "minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG","activityLookUp_2006_20449"})
	public void activityLookUp_2006_20449() {
		try {
			String testName = "activityLookUp_2006_20449";
			WSClient.startTest(testName,
					"Verify that the activities scheduled from the given date are all retrieved onto the response upon submitting an activity-lookup call"
							+ "",
					"minimumRegression");

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			String lName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_lname}", lName);
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);

			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE", "PHONE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));
			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_03");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscription Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						String activityid_opera1 = WSClient.getElementValueByAttribute(createActivityResponseXML,
								"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_activityID1}", activityid_opera1);
						// String
						// activityid_interface1=WSClient.getElementValueByAttribute(createActivityResponseXML,"Results_IDs_UniqueID",
						// "Results_IDs_UniqueID_source",HTNGLib.getHTNGInterface(),
						// XMLType.RESPONSE);

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							String activityid_opera2 = WSClient.getElementValueByAttribute(createActivityResponseXML_2,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
							String startDate = WSClient.getElementValue(createActivityReq_2, "Activity_TimeSpan_Start",
									XMLType.REQUEST);
							WSClient.setData("{var_date}", startDate);
							List<LinkedHashMap<String, String>> db = WSClient
									.getDBRows(WSClient.getQuery("HTNG2006ActivityLookup", "QS_05"));
							if (db.isEmpty()) {
								WSClient.writeToReport(LogStatus.WARNING, "<b>Problem in creating activities</b>");
							} else {
								WSClient.setData("{var_activityID2}", activityid_opera2);
								WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

								WSClient.setData("{var_lname}", lName.substring(0, 3));
								// Activity Look Up
								String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup",
										"DS_02");
								String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
								if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement",
										true)) {
									String text = WSClient.getElementValue(res_activityLookUp,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text element in the response is" + text + "</b>");
								}
								if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
										"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									startDate = WSClient.getElementValue(req_activityLookUp, "Activity_TimeSpan_Start",
											XMLType.REQUEST);
									WSClient.setData("{var_date}", startDate);
									db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									// xpath.put("ActivityLookup_ProfileID_UniqueID",
									// "ActivityLookupList_ActivityLookup_ProfileID");

									xpath.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

									db = WSClient.getDBRows(WSClient.getQuery("QS_05"));

									actualValues = WSClient.getMultipleNodeList(res_activityLookUp, xpath, false,
											XMLType.RESPONSE);

									WSAssert.assertEquals(actualValues, db, false);
								}
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/* @author ketvaidy */
	@Test(groups = { "minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20450() {
		try {
			String testName = "activityLookUp_2006_20450";
			WSClient.startTest(testName,
					"Verify that the activities scheduled till the given date are all retrieved onto the response upon submitting an activity-lookup call"
							+ "",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();

			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			String lName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_lname}", lName);
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);

			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE", "PHONE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			/// *Prerequisites: Create Profile, Attach Subscription, Create
			/// Multiple Activities */

			String operaProfileID = CreateProfile.createProfile("DS_03");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscription Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						String activityid_opera1 = WSClient.getElementValueByAttribute(createActivityResponseXML,
								"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_activityID1}", activityid_opera1);

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

							String activityid_opera2 = WSClient.getElementValueByAttribute(createActivityResponseXML_2,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
							WSClient.setData("{var_activityID2}", activityid_opera2);

							WSClient.setData("{var_lname}", lName.substring(0, 3));

							String startDate = WSClient.getElementValue(createActivityReq_2, "Activity_TimeSpan_Start",
									XMLType.REQUEST);
							WSClient.setData("{var_date}", startDate);
							List<LinkedHashMap<String, String>> db = WSClient
									.getDBRows(WSClient.getQuery("HTNG2006ActivityLookup", "QS_05"));
							if (db.isEmpty()) {
								WSClient.writeToReport(LogStatus.WARNING, "<b>Problem in creating activities</b>!!");
							} else {
								WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");
								// Activity Look Up
								String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup",
										"DS_02");
								String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
								if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement",
										true)) {
									String text = WSClient.getElementValue(res_activityLookUp,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text element in the response is" + text + "</b>");
								}
								if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
										"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									startDate = WSClient.getElementValue(req_activityLookUp, "Activity_TimeSpan_End",
											XMLType.REQUEST);
									WSClient.setData("{var_date}", startDate);

									db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

									db = WSClient.getDBRows(WSClient.getQuery("QS_06"));

									actualValues = WSClient.getMultipleNodeList(res_activityLookUp, xpath, false,
											XMLType.RESPONSE);

									WSAssert.assertEquals(actualValues, db, false);
								}
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/* @author nkamired */
	@Test(groups = { "minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20440() {
		try {
			String testName = "activityLookUp_2006_20440";
			WSClient.startTest(testName,
					"Verify that the Address details of the guest for which the scheduled activities are requested is correctly returned by the Activity Look-up call",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));

			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE", "PHONE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_03");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");
							String act_no = WSClient.getElementValue(createActivityResponseXML_2,
									"Results_IDs_UniqueID", XMLType.RESPONSE);
							// Validating the data
							WSClient.setData("{var_act_no}", act_no);
							// Activity Look Up
							String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_05");
							String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
									"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								HashMap<String, String> res_values = new HashMap<>();

								HashMap<String, String> xPath = new HashMap<>();

								xPath.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

								LinkedHashMap<String, String> resResult = WSClient.getSingleNodeList(res_activityLookUp,
										xPath, false, XMLType.RESPONSE);

								LinkedHashMap<String, String> dbResult = WSClient.getDBRow(WSClient.getQuery("QS_10"));
								WSAssert.assertEquals(resResult, dbResult, false);
							}

						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// /* @author nilsaini */
	// @Test(groups={"minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG"})
	// public void activityLookUp_2006_20433() {
	// try {
	// String testName = "activityLookUp_2006_20433";
	// WSClient.startTest(testName, "Verify that only the lname is being
	// returned onto the response message via Activity look-up call though the
	// requested profile has more than one address records",
	// "minimumRegression");
	//
	// String interfaceName = HTNGLib.getHTNGInterface();
	// String resortOperaValue=OPERALib.getResort();
	// String resortExtValue
	// =HTNGLib.getExtResort(resortOperaValue,interfaceName);
	// String genderExtValue =
	// HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
	// String lName = "";
	// do {
	// lName = WSClient.getKeywordData("{KEYWORD_LNAME}");
	// }while(lName.length() < 5);
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_gender}",genderExtValue);
	// WSClient.setData("{var_resort}",resortOperaValue);
	// WSClient.setData("{var_extResort}",resortExtValue);
	//
	// WSClient.setData("{var_lname}", lName);
	// WSClient.setData("{var_fname}",
	// WSClient.getKeywordData("{KEYWORD_FNAME}"));
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	//
	// String state_code =
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
	// String state =
	// HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
	//
	// String country_code = HTNGLib.getMasterValue(resortOperaValue,
	// interfaceName, "STATE", state_code);
	//
	// HashMap<String, String> addLOV = new HashMap<String, String>();
	//
	// addLOV=OPERALib.fetchAddressLOV(state,country_code);
	//
	// WSClient.setData("{var_city}", addLOV.get("City"));
	// WSClient.setData("{var_zip}", addLOV.get("Zip"));
	// WSClient.setData("{var_state}", state);
	// WSClient.setData("{var_country}", country_code);
	//
	//
	// WSClient.setData("{var_addressType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
	// WSClient.setData("{var_phoneType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
	// WSClient.setData("{var_profileType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(),OPERALib.getPassword(),HTNGLib.getInterfaceFromAddress());
	//
	// /*Prerequisites: Create Profile, Attach Subscription, Create Multiple
	// Activities */
	//
	// String operaProfileID=CreateProfile.createProfile("DS_03");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID
	// + "</b>");
	// if(!operaProfileID.equals("error")) {
	// WSClient.setData("{var_profileID}", operaProfileID);
	// WSClient.setData("{var_E_profileID}",
	// WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));
	//
	// // Making Subscription Request and getting the Response
	// String subscriptionReq =
	// WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
	// String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
	// if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag",
	// "SUCCESS",true)){
	//
	//
	// // Creating Activities - Activity 1
	// WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
	// WSClient.setData("{var_actstatus}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
	// WSClient.setData("{var_acttype}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
	// WSClient.setData("{var_actlocation}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));
	// String createActivityReq =
	// WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
	// String createActivityResponseXML =
	// WSClient.processSOAPMessage(createActivityReq);
	// if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
	// "CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
	// //String
	// activityid_interface1=WSClient.getElementValueByAttribute(createActivityResponseXML,"Results_IDs_UniqueID",
	// "Results_IDs_UniqueID_source",HTNGLib.getHTNGInterface(),
	// XMLType.RESPONSE);
	//
	//
	// // Create Activity 2
	// WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
	// WSClient.setData("{var_actstatus}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
	// WSClient.setData("{var_acttype}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
	// WSClient.setData("{var_actlocation}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));
	// String createActivityReq_2 =
	// WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
	// String createActivityResponseXML_2 =
	// WSClient.processSOAPMessage(createActivityReq_2);
	//
	// if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
	// "CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
	// WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the
	// profile");
	//
	// WSClient.setData("{var_lname}", lName.substring(0, 5));
	// // Activity Look Up
	// String req_activityLookUp =
	// WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_04");
	// String res_activityLookUp =
	// WSClient.processSOAPMessage(req_activityLookUp);
	// if(WSAssert.assertIfElementExists(res_activityLookUp,
	// "Result_Text_TextElement", true)){
	// String text=WSClient.getElementValue(res_activityLookUp,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text element in the
	// response is"+text+"</b>");
	// }
	// if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
	// "ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	// // Validating the data
	// List<LinkedHashMap<String, String>> dbResult = new
	// ArrayList<LinkedHashMap<String, String>>();
	// dbResult = WSClient.getDBRows(WSClient.getQuery("QS_08"));
	//
	//
	// //String name_id = WSAssert.getElementValue(res_activityLookUp,
	// "ActivityLookup_ProfileID_UniqueID", XMLType.RESPONSE);
	//
	//
	// //***MY regex handling***
	//
	// String pattern="ActivityIDs><c:UniqueID
	// source=\"OPERA\">(.+?)</c:UniqueID>" +
	// "<c:UniqueID source=\"" + interfaceName + "\">(.+?)</c:UniqueID>";
	// Pattern r = Pattern.compile(pattern);
	// Matcher m = r.matcher(res_activityLookUp);
	// List<LinkedHashMap<String, String>> resR = new
	// ArrayList<LinkedHashMap<String, String>>();
	// while(m.find()) {
	// LinkedHashMap<String, String> tmpHash = new LinkedHashMap<String,
	// String>();
	// for (int count = 1; count <= m.groupCount(); count ++) {
	// tmpHash.put("ActivityIDsUniqueID" + count, m.group(count));
	// }
	// resR.add(tmpHash);
	// }
	// //***********************
	//
	//
	//
	//
	// HashMap<String, String> res_values = new HashMap<>();
	// //res_values.put("Activity_ActivityIDs_UniqueID",
	// "ActivityLookupResponse_ActivityLookupList_ActivityLookup");
	//
	//
	//
	//
	// List<LinkedHashMap<String, String>> resResult ;
	// if(WSAssert.assertIfElementExists(res_activityLookUp,
	// "ActivityLookup_ProfileID_UniqueID", false)){
	// WSAssert.assertEquals(resR, dbResult, false);
	// }
	// else {
	// WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
	// }
	//
	// HashMap<String, String> xPath = new HashMap<>();
	// xPath.put("ActivityLookup_ProfileID_UniqueID",
	// "ActivityLookupResponse_ActivityLookupList_ActivityLookup");
	// xPath.put("ActivityLookup_PersonName_LastName",
	// "ActivityLookupResponse_ActivityLookupList_ActivityLookup");
	//
	// resResult = WSClient.getMultipleNodeList(res_activityLookUp, xPath,
	// false, XMLType.RESPONSE);
	//
	// dbResult = WSClient.getDBRows(WSClient.getQuery("QS_07"));
	// WSAssert.assertEquals(resResult, dbResult, false);
	// }
	// } else {
	//
	// WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
	// }
	// }
	// else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription
	// for the profile");
	// }
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
	// }
	//
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// }
	//

	/* @author nkamired */
	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20432() {
		try {
			String testName = "activityLookUp_2006_20432";
			WSClient.startTest(testName,
					"Verify that only the primary address is being returned onto the response message via Activity look-up call when the requested profile has more than one address records",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			String prerequisite[] = { "AddressType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV = OPERALib.fetchAddressLOV(state, country_code);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));

				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_04");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					String query = WSClient.getQuery("QS_03");
					String addressid = WSClient.getDBRow(query).get("ADDRESS_ID");

					if (!addressid.equals("")) {
						state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
						state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

						country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

						addLOV = OPERALib.fetchAddressLOV(state, country_code);
						WSClient.setData("{var_state1}", state);
						WSClient.setData("{var_country1}", country_code);
						WSClient.setData("{var_addressType1}",
								HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));

						WSClient.setData("{var_zip1}", addLOV.get("Zip"));
						WSClient.setData("{var_city1}", addLOV.get("City"));

						// Prerequisite 2: Change Profile
						OPERALib.setOperaHeader(uname);
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_06");
						String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

						if (WSAssert.assertIfElementExists(changeProfileResponseXML, "ChangeProfileRS_Success",
								false)) {
							if (WSAssert.assertIfElementExists(changeProfileResponseXML,
									"Addresses_AddressInfo_UniqueID_ID", false)) {

								// Validation request being created and
								// processed to generate response
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								WSClient.setData("{var_profileID}", operaProfileID);
								WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
								String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
								if (WSAssert.assertIfElementValueEquals(subscriptionRes,
										"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

									// Creating Activities - Activity 1
									WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
									WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,
											interfaceName, "ACTIVITY_STATUS"));
									WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,
											interfaceName, "ACTIVITY_TYPE"));
									WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,
											interfaceName, "ACTIVITY_LOCATION"));
									String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity",
											"DS_01");
									String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
									if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
											"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

										// Create Activity 2
										WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
										WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,
												interfaceName, "ACTIVITY_STATUS"));
										WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,
												interfaceName, "ACTIVITY_TYPE"));
										WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(
												resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));

										if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
												"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

											String req_activityLookUp = WSClient
													.createSOAPMessage("HTNG2006ActivityLookup", "DS_01");
											String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
											if (WSAssert.assertIfElementExists(res_activityLookUp,
													"Result_Text_TextElement", true)) {
												String text = WSClient.getElementValue(res_activityLookUp,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text element in the response is" + text + "</b>");
											}
											if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
													"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												// Validating the data
												LinkedHashMap<String, String> dbResult;

												// String name_id =
												// WSAssert.getElementValue(res_activityLookUp,
												// "ActivityLookup_ProfileID_UniqueID",
												// XMLType.RESPONSE);

												LinkedHashMap<String, String> resResult;

												HashMap<String, String> xPath = new HashMap<>();
												xPath.put("ActivityLookup_ProfileID_UniqueID",
														"ActivityLookupList_ActivityLookup_ProfileID");
												xPath.put("ActivityLookup_Address_AddressLine",
														"ActivityLookupList_ActivityLookup_Address");
												xPath.put("ActivityLookup_Address_CityName",
														"ActivityLookupList_ActivityLookup_Address");

												resResult = WSClient.getSingleNodeList(res_activityLookUp, xPath, false,
														XMLType.RESPONSE);
												resResult.put("StateProv1", WSClient.getElementValue(res_activityLookUp,
														"ActivityLookup_Address_StateProv", XMLType.RESPONSE));

												String QS_09 = WSClient.getQuery("QS_09");

												dbResult = WSClient.getDBRow(QS_09);

												WSAssert.assertEquals(dbResult, resResult, false);
											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Unable to make Subscription for the profile");
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"The prerequisites for Address failed!------ Change Profile -----Blocked");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Change Profile -----Blocked");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Address failed!------ Create Profile -----Blocked");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Create Profile -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}

	}

	@Test(groups = { "fullRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20442() {
		try {
			String testName = "activityLookUp_2006_20442";
			WSClient.startTest(testName,
					"Verify that an error message is returned onto the response message when the activity look-up call is submitted with a Profile ID that doesn't exist in PMS",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			// WSClient.setData("{var_state}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			// WSClient.setData("{var_addressType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			// WSClient.setData("{var_phoneType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			// WSClient.setData("{var_profileType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			//
			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {

				WSClient.setData("{var_profileID}", WSClient
						.getDBRow(WSClient.getQuery("HTNG2006ActivityLookup", "QS_11")).get("PROFILE").toString());

				// Activity Look Up
				WSClient.setData("{var_extResort}", " ");

				String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_01");
				String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);
				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileID}", " ");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Verify that the error exists when profileId is not passed on the request</b>");
				req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_01");
				res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);
				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

			}

			else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "fullRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20443() {
		try {
			String testName = "activityLookUp_2006_20443";
			WSClient.startTest(testName,
					"Verify that an error message is returned onto the response message when the activity look-up call is submitted with a Name that doesn't exist in PMS",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			String lName = "";
			do {
				lName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			} while (lName.length() < 5);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_lname}", lName);
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);

			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE", "PHONE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_03");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				WSClient.setData("{var_lname}", "INVALIDNAME");
				// Activity Look Up
				String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_04");
				String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);
				WSClient.setData("{var_lname}", lName);
				WSClient.setData("{var_extResort}", " ");
				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_04");
				res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);
				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20433() {
		try {
			String testName = "activityLookUp_2006_20433";
			WSClient.startTest(testName,
					"Verify that the activities scheduled for the Opera profile are being returned through the Look-up activity call upon wildcard search on the guest's last name",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String lNameprefix = WSClient.getKeywordData("{KEYWORD_RANDSTR_5}");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_lname}", lNameprefix + WSClient.getKeywordData("{KEYWORD_LNAME}"));

			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_06");
			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_lname}", lNameprefix + WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				operaProfileID = CreateProfile.createProfile("DS_06");
				if (!operaProfileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

					// Making Subscription Request and getting the Response
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						// Creating Activities - Activity 1
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							// String
							// activityid_interface1=WSClient.getElementValueByAttribute(createActivityResponseXML,"Results_IDs_UniqueID",
							// "Results_IDs_UniqueID_source",HTNGLib.getHTNGInterface(),
							// XMLType.RESPONSE);

							// Create Activity 2
							WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
							WSClient.setData("{var_actstatus}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
							WSClient.setData("{var_acttype}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
							WSClient.setData("{var_actlocation}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
							String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
							String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

							if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
									"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
								WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

								WSClient.setData("{var_lname}", lNameprefix);
								// Activity Look Up
								String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup",
										"DS_04");
								String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
								if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement",
										true)) {
									String text = WSClient.getElementValue(res_activityLookUp,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text element in the response is" + text + "</b>");
								}
								if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
										"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// Validating the data
									List<LinkedHashMap<String, String>> dbResult = new ArrayList<LinkedHashMap<String, String>>();
									dbResult = WSClient.getDBRows(WSClient.getQuery("QS_08"));

									// String name_id =
									// WSAssert.getElementValue(res_activityLookUp,
									// "ActivityLookup_ProfileID_UniqueID",
									// XMLType.RESPONSE);

									// ***MY regex handling***

									String pattern = "ActivityIDs><c:UniqueID source=\"OPERA\">(.+?)</c:UniqueID>"
											+ "<c:UniqueID source=\"" + interfaceName + "\">(.+?)</c:UniqueID>";
									Pattern r = Pattern.compile(pattern);
									Matcher m = r.matcher(res_activityLookUp);
									List<LinkedHashMap<String, String>> resR = new ArrayList<LinkedHashMap<String, String>>();
									while (m.find()) {
										LinkedHashMap<String, String> tmpHash = new LinkedHashMap<String, String>();
										for (int count = 1; count <= m.groupCount(); count++) {
											tmpHash.put("ActivityIDsUniqueID" + count, m.group(count));
										}
										resR.add(tmpHash);
									}
									// ***********************

									HashMap<String, String> res_values = new HashMap<>();
									// res_values.put("Activity_ActivityIDs_UniqueID",
									// "ActivityLookupResponse_ActivityLookupList_ActivityLookup");

									List<LinkedHashMap<String, String>> resResult;
									if (WSAssert.assertIfElementExists(res_activityLookUp,
											"ActivityLookup_ProfileID_UniqueID", false)) {
										WSAssert.assertEquals(resR, dbResult, false);
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
									}

									HashMap<String, String> xPath = new HashMap<>();
									xPath.put("ActivityLookup_ProfileID_UniqueID",
											"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
									xPath.put("ActivityLookup_PersonName_LastName",
											"ActivityLookupResponse_ActivityLookupList_ActivityLookup");

									resResult = WSClient.getMultipleNodeList(res_activityLookUp, xPath, false,
											XMLType.RESPONSE);

									dbResult = WSClient.getDBRows(WSClient.getQuery("QS_07"));
									WSAssert.assertEquals(resResult, dbResult, false);
								}
							} else {

								WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "fullRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20444() {
		try {
			String testName = "activityLookUp_2006_20444";
			WSClient.startTest(testName,
					"Verify that an error message is returned onto the response message when the activity look-up call is submitted with the Membership information that is not associated to the guest having the given last name",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			String lName = "";
			do {
				lName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			} while (lName.length() < 5);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_lname}", lName);
			WSClient.setData("{var_membership_type}", "invalidType");
			WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);

			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_04");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_09");
				String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);

				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/*@Test(groups = { "fullRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20446() {
		try {
			String testName = "activityLookUp_2006_20446";
			WSClient.startTest(testName,
					"Verify that the profile is still returned onto the response without fetching the Activity information when an incorrect activity ID is submitted via the Activity Look-up call",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			String lName = "";
			do {
				lName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			} while (lName.length() < 5);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_lname}", lName);

			WSClient.setData("{var_activity}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());


	 * Prerequisites: Create Profile, Attach Subscription, Create
	 * Multiple Activities


			String operaProfileID = CreateProfile.createProfile("DS_04");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_10");
				String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text element in the response is" + text + "</b>");
				}
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}*/

	@Test(groups = { "fullRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20445() {
		try {
			String testName = "activityLookUp_2006_20445";
			WSClient.startTest(testName,
					"Verify that an error message is returned onto the response message when the activity look-up call is submitted with the Address & Phone information that is not associated to the guest having the given last name",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			String lName = "";
			do {
				lName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			} while (lName.length() < 5);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_lname}", lName);
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);

			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_ext_state}", state_code);

			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE", "PHONE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
			String operaProfileID = CreateProfile.createProfile("DS_04");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Activity Look Up
				WSClient.setData("{var_city}", "invalidcity");
				String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_12");
				String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);
				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				WSClient.setData("{var_city}", addLOV.get("City"));
				WSClient.setData("{var_ext_state}", "invalidState");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Verify if error exists when invalid state is paased on the request</b>");
				req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_12");
				res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);
				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
				WSClient.setData("{var_ext_state}", state_code);
				WSClient.setData("{var_zip}", "00000000");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Verify if error exists when invalid zip is passed on the request</b>");
				req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_12");
				res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
				WSAssert.assertIfElementValueEquals(res_activityLookUp,
						"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false);

				if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
					/****
					 * Verify that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(res_activityLookUp, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20435() {
		try {
			String testName = "activityLookUp_2006_20435";
			WSClient.startTest(testName,
					"Verify that the profiles having scheduled activities are filtered based on the Membership information via the Activity Lookup call",
					"targetedRegression");
			String uname = OPERALib.getUserName();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			String operaProfileID = CreateProfile.createProfile("DS_06");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));
				String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
						.toUpperCase();
				WSClient.setData("{var_nameOnCard}", memName);
				WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
				WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
				OPERALib.setOperaHeader(uname);
				String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_07");
				String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

				if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Successfully attached membership</b>");
					// String memNo=
					// WSClient.getElementValue(createMembershipRes,"CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipIDNo",
					// XMLType.RESPONSE);
					String memNo = WSClient.getElementValue(createMembershipRes,
							"CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", XMLType.RESPONSE);

					WSClient.setData("{var_memno}", memNo);
					WSClient.setData("{var_memNo}", memNo);

					// Making Subscritpion Request and getting the Response
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						// Creating Activities - Activity 1
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));

						String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

							// Create Activity 2
							WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
							WSClient.setData("{var_actstatus}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
							WSClient.setData("{var_acttype}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
							WSClient.setData("{var_actlocation}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));

							String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
							String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);
							if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
									"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
								WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

								// Activity Look Up
								String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup",
										"DS_07");
								String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
								if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement",
										true)) {
									String text = WSClient.getElementValue(res_activityLookUp,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text element in the response is" + text + "</b>");
								}

								// Validating the data
								List<LinkedHashMap<String, String>> dbResult = new ArrayList<>();
								dbResult = WSClient.getDBRows(WSClient.getQuery("QS_19"));

								String name_id = WSAssert.getElementValue(res_activityLookUp,
										"ActivityLookup_ProfileID_UniqueID", XMLType.RESPONSE);
								HashMap<String, String> res_values = new HashMap<>();
								res_values.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

								List<LinkedHashMap<String, String>> resResult = WSAssert
										.getMultipleNodeList(res_activityLookUp, res_values, false, XMLType.RESPONSE);

								if (WSAssert.assertIfElementExists(res_activityLookUp,
										"ActivityLookup_ProfileID_UniqueID", false)) {
									WSAssert.assertEquals(resResult, dbResult, false);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
								}

							} else {

								WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
						}
					} else {
						// WSClient.writeToReport(LogStatus.WARNING, "Unable to
						// make Subscription for the profile");
					}

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Membership not created");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// @Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006",
	// "HTNG" })
	// public void activityLookUp_2006_MEMCOMM() {
	// try {
	// String testName = "activityLookUp_2006_20435";
	// WSClient.startTest(testName,
	// "Verify that the profiles having scheduled activities are filtered based
	// on the Membership information via the Activity Lookup call",
	// "targetedRegression");
	// String uname = OPERALib.getUserName();
	// String interfaceName = HTNGLib.getHTNGInterface();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = HTNGLib.getExtResort(resortOperaValue,
	// interfaceName);
	// String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,
	// interfaceName, "GENDER_MF");
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_gender}", genderExtValue);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// //
	// WSClient.setData("{var_state}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
	// //
	// WSClient.setData("{var_addressType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
	// //
	// WSClient.setData("{var_phoneType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
	// //
	// WSClient.setData("{var_profileType}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
	// //
	// /*
	// * Prerequisites: Create Profile, Attach Subscription, Create
	// * Multiple Activities
	// */
	// String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	// String lname = "MALDONADO";//WSClient.getKeywordData("{KEYWORD_LNAME}");
	// WSClient.setData("{var_fname}", fname);
	// WSClient.setData("{var_lname}", lname);
	// String operaProfileID = "2204465802";
	// //CreateProfile.createProfile("DS_06");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID
	// + "</b>");
	// if (!operaProfileID.equals("error")) {
	// WSClient.setData("{var_profileID}", operaProfileID);
	// WSClient.setData("{var_E_profileID}",
	// WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));
	// String memName = (WSClient.getData("{var_fname}") + "_" +
	// WSClient.getData("{var_lname}"))
	// .toUpperCase();
	// WSClient.setData("{var_nameOnCard}", memName);
	// WSClient.setData("{var_memType}", "QAL"
	// );//OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
	// WSClient.setData("{var_memLevel}",
	// OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
	// OPERALib.setOperaHeader(uname);
	//// String createMembershipReq =
	// WSClient.createSOAPMessage("CreateMembership", "DS_05");
	//// String createMembershipRes =
	// WSClient.processSOAPMessage(createMembershipReq);
	//
	// if (true) { // WSAssert.assertIfElementExists(createMembershipRes,
	// "CreateMembershipRS_Success", true)
	// WSClient.writeToReport(LogStatus.INFO, "<b>Successfully attached
	// membership</b>");
	// // String memNo=
	// //
	// WSClient.getElementValue(createMembershipRes,"CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipIDNo",
	// // XMLType.RESPONSE);
	// String memNo =
	// "723458";//WSClient.getElementValue(createMembershipRes,"CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID",
	// XMLType.RESPONSE);
	//
	// WSClient.setData("{var_memno}", memNo);
	// WSClient.setData("{var_memNo}", memNo);
	// // WSClient.setData("{var_memType}",
	// // HTNGLib.getExtValue(resortOperaValue, interfaceName,
	// // "GENDER_MF",OperaPropConfig.getDataSetForCode("Gender",
	// // "DS_02")));
	//
	// // Making Subscritpion Request and getting the Response
	//// String subscriptionReq =
	// WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
	//// String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
	//// if (WSAssert.assertIfElementValueEquals(subscriptionRes,
	//// "SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
	//
	// // Creating Activities - Activity 1
	// WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
	// WSClient.setData("{var_actstatus}",
	// HTNGLib.getRandomExtValue(resortOperaValue, interfaceName,
	// "ACTIVITY_STATUS"));
	// WSClient.setData("{var_acttype}",
	// HTNGLib.getRandomExtValue(resortOperaValue, interfaceName,
	// "ACTIVITY_TYPE"));
	// WSClient.setData("{var_actlocation}",
	// HTNGLib.getRandomExtValue(resortOperaValue, interfaceName,
	// "ACTIVITY_LOCATION"));
	//
	//// String createActivityReq =
	// WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
	//// String createActivityResponseXML =
	// WSClient.processSOAPMessage(createActivityReq);
	//// if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
	//// "CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
	//
	// // Create Activity 2
	// WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
	// WSClient.setData("{var_actstatus}",
	// HTNGLib.getRandomExtValue(resortOperaValue, interfaceName,
	// "ACTIVITY_STATUS"));
	// WSClient.setData("{var_acttype}",
	// HTNGLib.getRandomExtValue(resortOperaValue, interfaceName,
	// "ACTIVITY_TYPE"));
	// WSClient.setData("{var_actlocation}",
	// HTNGLib.getRandomExtValue(resortOperaValue, interfaceName,
	// "ACTIVITY_LOCATION"));
	//
	//
	//// String createActivityReq_2 =
	// WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
	//// String createActivityResponseXML_2 =
	// WSClient.processSOAPMessage(createActivityReq_2);
	//
	//// if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
	//// "CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
	// WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the
	// profile");
	//
	// // Activity Look Up
	// String req_activityLookUp =
	// WSClient.createSOAPMessage("HTNG2006ActivityLookup",
	// "DS_07");
	// String res_activityLookUp =
	// WSClient.processSOAPMessage(req_activityLookUp);
	// if(WSAssert.assertIfElementExists(res_activityLookUp,
	// "Result_Text_TextElement", true)){
	// String text=WSClient.getElementValue(res_activityLookUp,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text element in the
	// response is"+text+"</b>");
	// }
	//
	// // Validating the data
	// List<LinkedHashMap<String, String>> dbResult = new ArrayList<>();
	// dbResult = WSClient.getDBRows(WSClient.getQuery("QS_19"));
	//
	// // String name_id =
	// //
	// WSAssert.getElementValue(res_activityLookUp,"ActivityLookup_ProfileID_UniqueID",
	// // XMLType.RESPONSE);
	// HashMap<String, String> res_values = new HashMap<>();
	// res_values.put("Activity_ActivityIDs_UniqueID",
	// "ActivityLookup_Activities_Activity");
	//
	// List<LinkedHashMap<String, String>> resResult = WSAssert
	// .getMultipleNodeList(res_activityLookUp, res_values, false,
	// XMLType.RESPONSE);
	//
	// if (WSAssert.assertIfElementExists(res_activityLookUp,
	// "ActivityLookup_ProfileID_UniqueID", false)) {
	// WSAssert.assertEquals(resResult, dbResult, false);
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
	// }
	//
	//// } else {
	////
	//// WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
	//// }
	//// } else {
	//// WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
	//// }
	// //} else {
	// //WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription
	// for the profile");
	// //}
	//
	// } else
	// WSClient.writeToReport(LogStatus.WARNING, "Membership not created");
	//
	// } else
	// WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// }

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20434() {
		try {
			String testName = "activityLookUp_2006_20434";
			WSClient.startTest(testName,
					"Verify that the activities scheduled for the Opera profile are being returned through the Look-up activity call upon wildcard search on the guest's last and first names",
					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String lNameprefix = WSClient.getKeywordData("{KEYWORD_RANDSTR_3}");
			String fNameprefix = WSClient.getKeywordData("{KEYWORD_RANDSTR_3}");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_lname}", lNameprefix + WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", fNameprefix + WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_06");
			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_lname}", lNameprefix + WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", fNameprefix + WSClient.getKeywordData("{KEYWORD_FNAME}"));
				operaProfileID = CreateProfile.createProfile("DS_06");
				if (!operaProfileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

					// Making Subscription Request and getting the Response
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						// Creating Activities - Activity 1
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							// String
							// activityid_interface1=WSClient.getElementValueByAttribute(createActivityResponseXML,"Results_IDs_UniqueID",
							// "Results_IDs_UniqueID_source",HTNGLib.getHTNGInterface(),
							// XMLType.RESPONSE);

							// Create Activity 2
							WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
							WSClient.setData("{var_actstatus}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
							WSClient.setData("{var_acttype}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
							WSClient.setData("{var_actlocation}",
									HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
							String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
							String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

							if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
									"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
								WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

								WSClient.setData("{var_lname}", lNameprefix);
								WSClient.setData("{var_fname}", fNameprefix);
								// Activity Look Up
								String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup",
										"DS_08");
								String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
								if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement",
										true)) {
									String text = WSClient.getElementValue(res_activityLookUp,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text element in the response is" + text + "</b>");
								}
								if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
										"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// Validating the data
									List<LinkedHashMap<String, String>> dbResult = new ArrayList<LinkedHashMap<String, String>>();
									dbResult = WSClient.getDBRows(WSClient.getQuery("QS_13"));

									// String name_id =
									// WSAssert.getElementValue(res_activityLookUp,
									// "ActivityLookup_ProfileID_UniqueID",
									// XMLType.RESPONSE);

									// ***MY regex handling***

									String pattern = "ActivityIDs><c:UniqueID source=\"OPERA\">(.+?)</c:UniqueID>"
											+ "<c:UniqueID source=\"" + interfaceName + "\">(.+?)</c:UniqueID>";
									Pattern r = Pattern.compile(pattern);
									Matcher m = r.matcher(res_activityLookUp);
									List<LinkedHashMap<String, String>> resR = new ArrayList<LinkedHashMap<String, String>>();
									while (m.find()) {
										LinkedHashMap<String, String> tmpHash = new LinkedHashMap<String, String>();
										for (int count = 1; count <= m.groupCount(); count++) {
											tmpHash.put("ActivityIDsUniqueID" + count, m.group(count));
										}
										resR.add(tmpHash);
									}
									// ***********************

									HashMap<String, String> res_values = new HashMap<>();
									// res_values.put("Activity_ActivityIDs_UniqueID",
									// "ActivityLookupResponse_ActivityLookupList_ActivityLookup");

									List<LinkedHashMap<String, String>> resResult;
									if (WSAssert.assertIfElementExists(res_activityLookUp,
											"ActivityLookup_ProfileID_UniqueID", false)) {
										WSAssert.assertEquals(resR, dbResult, false);
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
									}

									HashMap<String, String> xPath = new HashMap<>();
									xPath.put("ActivityLookup_ProfileID_UniqueID",
											"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
									xPath.put("ActivityLookup_PersonName_LastName",
											"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
									xPath.put("ActivityLookup_PersonName_FirstName",
											"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
									resResult = WSClient.getMultipleNodeList(res_activityLookUp, xPath, false,
											XMLType.RESPONSE);

									dbResult = WSClient.getDBRows(WSClient.getQuery("QS_12"));
									WSAssert.assertEquals(resResult, dbResult, false);

								}
							} else {

								WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20436() {
		try {
			String testName = "activityLookUp_2006_20436";
			WSClient.startTest(testName,
					"Verify that the profiles having scheduled activities are filetered based on the State the guest belongs to, upon issuing the Activity Look-up call",
					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String state = OperaPropConfig.getDataSetForCode("State", "DS_01");
			String country_code = OperaPropConfig.getDataSetForCode("Country", "DS_01");

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_04");

			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscription Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_STATUS", OperaPropConfig.getDataSetForCode("ActivityStatus", "DS_01")));
					WSClient.setData("{var_acttype}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_TYPE", OperaPropConfig.getDataSetForCode("ActivityType", "DS_01")));
					WSClient.setData("{var_actlocation}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_LOCATION", OperaPropConfig.getDataSetForCode("ActivityLocation", "DS_01")));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						List<LinkedHashMap<String, String>> dbResult3 = WSClient.getDBRows(WSClient.getQuery("QS_01"));
						if (dbResult3.size() > 0) {
							WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
							state = OperaPropConfig.getDataSetForCode("State", "DS_03");
							country_code = OperaPropConfig.getDataSetForCode("Country", "DS_03");
							HashMap<String, String> addLOV1 = new HashMap<String, String>();
							addLOV1 = OPERALib.fetchAddressLOV(state, country_code);

							WSClient.setData("{var_city}", addLOV1.get("City"));
							WSClient.setData("{var_zip}", addLOV1.get("Zip"));
							WSClient.setData("{var_state}", state);
							WSClient.setData("{var_country}", country_code);
							WSClient.setData("{var_addressType}",
									OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

							/*
							 * Prerequisites: Create Profile, Attach
							 * Subscription, Create Multiple Activities
							 */

							operaProfileID = CreateProfile.createProfile("DS_04");

							if (!operaProfileID.equals("error")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
								WSClient.setData("{var_profileID}", operaProfileID);
								WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

								// Making Subscription Request and getting the
								// Response
								subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
								subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
								if (WSAssert.assertIfElementValueEquals(subscriptionRes,
										"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

									// Creating Activities - Activity 1
									WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
									WSClient.setData("{var_actstatus}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS",
													OperaPropConfig.getDataSetForCode("ActivityStatus", "DS_01")));
									WSClient.setData("{var_acttype}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE",
													OperaPropConfig.getDataSetForCode("ActivityType", "DS_01")));
									WSClient.setData("{var_actlocation}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION",
													OperaPropConfig.getDataSetForCode("ActivityLocation", "DS_01")));
									createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
									createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
									if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
											"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

										List<LinkedHashMap<String, String>> dbResult = WSClient
												.getDBRows(WSClient.getQuery("QS_01"));
										if (dbResult.size() > 0) {
											// WSClient.setData("{var_state}",
											// HTNGLib.getExtValue(resortOperaValue,
											// interfaceName, "STATE", state));
											// WSClient.setData("{var_country}",
											// HTNGLib.getExtValue(resortOperaValue,
											// interfaceName, "COUNTRY_CODE",
											// country_code));
											// Activity Look Up
											String req_activityLookUp = WSClient
													.createSOAPMessage("HTNG2006ActivityLookup", "DS_13");
											String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
											if (WSAssert.assertIfElementExists(res_activityLookUp,
													"Result_Text_TextElement", true)) {
												String text = WSClient.getElementValue(res_activityLookUp,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text element in the response is" + text + "</b>");
											}
											if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
													"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												// Validating the data

												HashMap<String, String> xPath = new HashMap<>();
												xPath.put("ActivityLookup_ProfileID_UniqueID",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												xPath.put("ActivityLookup_PersonName_LastName",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												xPath.put("ActivityLookup_Address_StateProv",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												List<LinkedHashMap<String, String>> resResult1 = WSClient
														.getMultipleNodeList(res_activityLookUp, xPath, false,
																XMLType.RESPONSE);
												WSClient.setData("{var_state}", state);
												List<LinkedHashMap<String, String>> dbResult1 = WSClient
														.getDBRows(WSClient.getQuery("QS_16")); // QUERY
												// 14
												WSAssert.assertEquals(resResult1, dbResult1, false);
											}
										} else {

											WSClient.writeToReport(LogStatus.WARNING,
													"Activity didn't got attached to profile");
										}
									} else {

										WSClient.writeToReport(LogStatus.WARNING, "Activity not created");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Unable to make Subscription for the profile");
								}
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity didn't got attached to profile");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20447() {
		try {
			String testName = "activityLookUp_2006_20447";
			WSClient.startTest(testName,
					"Verify that the cancelled activities are not being retrieved onto the response message via the Activity Look-up call",

					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_01");

			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						WSClient.writeToReport(LogStatus.INFO, "Activity Created for the profile");
						if (WSAssert.assertIfElementExists(createActivityResponseXML, "Results_IDs_UniqueID", true)) {

							String operaActivityID = WSClient.getElementValueByAttribute(createActivityResponseXML,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>OPERA Activity ID: " + operaActivityID + "</b>");

							String externalActivityID = WSClient.getElementValueByAttribute(createActivityResponseXML,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>External Activity ID: " + externalActivityID + "</b>");

							WSClient.setData("{var_extActivityID}", externalActivityID);
							WSClient.setData("{var_activitySource}", interfaceName);
							WSClient.setData("{var_extResort}", resortExtValue);
							WSClient.setData("{var_activityID}", operaActivityID);

							String cancelActXML = WSClient.createSOAPMessage("HTNG2008CancelActivity", "DS_01");
							String cancelActRes = WSClient.processSOAPMessage(cancelActXML);

							if (WSAssert.assertIfElementValueEquals(cancelActRes,
									"CancelActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

								// Activity Look Up
								String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup",
										"DS_01");
								String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
								if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement",
										true)) {
									String text = WSClient.getElementValue(res_activityLookUp,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text element in the response is" + text + "</b>");
								}

								if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
										"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									if (WSAssert.assertIfElementExists(res_activityLookUp,
											"ActivityLookup_ProfileID_UniqueID", false)) {
										String name_id = WSAssert.getElementValue(res_activityLookUp,
												"ActivityLookup_ProfileID_UniqueID", XMLType.RESPONSE);
										if (WSAssert.assertEquals(operaProfileID, name_id, true))
											WSClient.writeToReport(LogStatus.PASS, "<b>ProfileID-->Expected: </b>"
													+ operaProfileID + "<b> Actual: </b>" + name_id);
										else
											WSClient.writeToReport(LogStatus.FAIL, "<b>ProfileID-->Expected: </b>"
													+ operaProfileID + "<b> Actual: </b>" + name_id);

										if (WSAssert.assertIfElementExists(res_activityLookUp,
												"Activity_ActivityIDs_UniqueID", true)) {
											WSClient.writeToReport(LogStatus.FAIL, "Cancelled activities got fetched");
										} else {
											WSClient.writeToReport(LogStatus.PASS,
													"Cancelled activities didnt get fetched");
										}

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
									}

								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Activity didnt get cancelled");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Activity ID not returned");
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20439() {
		try {
			String testName = "activityLookUp_2006_20439";
			WSClient.startTest(testName,
					"Verify that the profiles having scheduled activities are filtered based on the given Phone Number via Activity Lookup call",
					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_phone}", WSClient.getKeywordData("{KEYWORD_PHONE}"));
			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_49");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

							// Activity Look Up
							String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_14");
							String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
									"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validating the data
								List<LinkedHashMap<String, String>> dbResult = new ArrayList<>();
								dbResult = WSClient.getDBRows(WSClient.getQuery("QS_15")); // qs_15

								// String name_id =
								// WSAssert.getElementValue(res_activityLookUp,"ActivityLookup_ProfileID_UniqueID",
								// XMLType.RESPONSE);
								HashMap<String, String> res_values = new HashMap<>();
								res_values.put("Activity_ActivityIDs_UniqueID", "ActivityLookup_Activities_Activity");

								List<LinkedHashMap<String, String>> resResult = WSAssert
										.getMultipleNodeList(res_activityLookUp, res_values, false, XMLType.RESPONSE);

								if (WSAssert.assertIfElementExists(res_activityLookUp,
										"ActivityLookup_ProfileID_UniqueID", false)) {
									WSAssert.assertEquals(resResult, dbResult, false);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Profile ID not populated");
								}

							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20448() {
		try {
			String testName = "activityLookUp_2006_20448";
			WSClient.startTest(testName,
					"Verify that an error message is returned onto the response when the Activity Look-up call is issued with an inactive membership filter ",
					"targetedRegression");
			String uname = OPERALib.getUserName();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			String operaProfileID = CreateProfile.createProfile("DS_06");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));
				String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
						.toUpperCase();
				WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_04"));
				WSClient.setData("{var_memno}", WSClient.getKeywordData("{KEYWORD_RANDSTR_6}"));
				// WSClient.setData("{var_memType}",
				// HTNGLib.getExtValue(resortOperaValue, interfaceName,
				// "GENDER_MF",OperaPropConfig.getDataSetForCode("Gender",
				// "DS_02")));

				// Making Subscritpion Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}",
							HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						// Create Activity 2
						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actstatus}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}",
								HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));
						String createActivityReq_2 = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
						String createActivityResponseXML_2 = WSClient.processSOAPMessage(createActivityReq_2);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML_2,
								"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Two Activities Created for the profile");

							// Activity Look Up
							String req_activityLookUp = WSClient.createSOAPMessage("HTNG2006ActivityLookup", "DS_07");
							String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}

							if (WSAssert.assertIfElementExists(res_activityLookUp, "Result_Text_TextElement", true)) {
								String text = WSClient.getElementValue(res_activityLookUp, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text element in the response is" + text + "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
									"ActivityLookupResponse_Result_resultStatusFlag", "FAIL", false)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Cant fetch with inactive membership information");
							}

						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity 2 not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity 1 not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Unique Profile ID not found");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20437() {
		try {
			String testName = "activityLookUp_2006_20437";
			WSClient.startTest(testName,
					"Verify that the profiles having scheduled activities are filtered based on the city the guest belongs to, upon issuing the Activity Look-up call",
					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String state = OperaPropConfig.getDataSetForCode("State", "DS_01");
			String country_code = OperaPropConfig.getDataSetForCode("Country", "DS_01");

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_04");

			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscription Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_STATUS", OperaPropConfig.getDataSetForCode("ActivityStatus", "DS_01")));
					WSClient.setData("{var_acttype}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_TYPE", OperaPropConfig.getDataSetForCode("ActivityType", "DS_01")));
					WSClient.setData("{var_actlocation}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_LOCATION", OperaPropConfig.getDataSetForCode("ActivityLocation", "DS_01")));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						List<LinkedHashMap<String, String>> dbResult3 = WSClient.getDBRows(WSClient.getQuery("QS_01"));
						if (dbResult3.size() > 0) {
							WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
							state = OperaPropConfig.getDataSetForCode("State", "DS_03");
							country_code = OperaPropConfig.getDataSetForCode("Country", "DS_03");
							HashMap<String, String> addLOV1 = new HashMap<String, String>();
							addLOV1 = OPERALib.fetchAddressLOV(state, country_code);

							WSClient.setData("{var_city}", addLOV1.get("City"));
							WSClient.setData("{var_zip}", addLOV1.get("Zip"));
							WSClient.setData("{var_state}", state);
							WSClient.setData("{var_country}", country_code);
							WSClient.setData("{var_addressType}",
									OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

							/*
							 * Prerequisites: Create Profile, Attach
							 * Subscription, Create Multiple Activities
							 */

							operaProfileID = CreateProfile.createProfile("DS_04");

							if (!operaProfileID.equals("error")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
								WSClient.setData("{var_profileID}", operaProfileID);
								WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

								// Making Subscription Request and getting the
								// Response
								subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
								subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
								if (WSAssert.assertIfElementValueEquals(subscriptionRes,
										"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

									// Creating Activities - Activity 1
									WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
									WSClient.setData("{var_actstatus}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS",
													OperaPropConfig.getDataSetForCode("ActivityStatus", "DS_01")));
									WSClient.setData("{var_acttype}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE",
													OperaPropConfig.getDataSetForCode("ActivityType", "DS_01")));
									WSClient.setData("{var_actlocation}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION",
													OperaPropConfig.getDataSetForCode("ActivityLocation", "DS_01")));
									createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
									createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
									if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
											"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

										List<LinkedHashMap<String, String>> dbResult = WSClient
												.getDBRows(WSClient.getQuery("QS_01"));
										if (dbResult.size() > 0) {
											// WSClient.setData("{var_state}",
											// HTNGLib.getExtValue(resortOperaValue,
											// interfaceName, "STATE", state));
											// WSClient.setData("{var_country}",
											// HTNGLib.getExtValue(resortOperaValue,
											// interfaceName, "COUNTRY_CODE",
											// country_code));
											// Activity Look Up
											String req_activityLookUp = WSClient
													.createSOAPMessage("HTNG2006ActivityLookup", "DS_15");
											String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
											if (WSAssert.assertIfElementExists(res_activityLookUp,
													"Result_Text_TextElement", true)) {
												String text = WSClient.getElementValue(res_activityLookUp,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text element in the response is" + text + "</b>");
											}
											if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
													"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												// Validating the data

												HashMap<String, String> xPath = new HashMap<>();
												xPath.put("ActivityLookup_ProfileID_UniqueID",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												xPath.put("ActivityLookup_PersonName_LastName",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												xPath.put("ActivityLookup_Address_CityName",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												List<LinkedHashMap<String, String>> resResult1 = WSClient
														.getMultipleNodeList(res_activityLookUp, xPath, false,
																XMLType.RESPONSE);

												List<LinkedHashMap<String, String>> dbResult1 = WSClient
														.getDBRows(WSClient.getQuery("QS_17"));
												WSAssert.assertEquals(resResult1, dbResult1, false);
											}
										} else {

											WSClient.writeToReport(LogStatus.WARNING,
													"Activity didn't got attached to profile");
										}
									} else {

										WSClient.writeToReport(LogStatus.WARNING, "Activity not created");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Unable to make Subscription for the profile");
								}
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity didn't got attached to profile");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "ActivityLookUp", "HTNG2006", "HTNG" })
	public void activityLookUp_2006_20438() {
		try {
			String testName = "activityLookUp_2006_20438";
			WSClient.startTest(testName,
					"Verify that the profiles having scheduled activities are filtered based on the postal code the guest belongs to, upon issuing the Activity Look-up call",
					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String state = OperaPropConfig.getDataSetForCode("State", "DS_01");
			String country_code = OperaPropConfig.getDataSetForCode("Country", "DS_01");

			HashMap<String, String> addLOV = new HashMap<String, String>();

			addLOV = OPERALib.fetchAddressLOV(state, country_code);

			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

			/*
			 * Prerequisites: Create Profile, Attach Subscription, Create
			 * Multiple Activities
			 */

			String operaProfileID = CreateProfile.createProfile("DS_04");

			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

				// Making Subscription Request and getting the Response
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					// Creating Activities - Activity 1
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_STATUS", OperaPropConfig.getDataSetForCode("ActivityStatus", "DS_01")));
					WSClient.setData("{var_acttype}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_TYPE", OperaPropConfig.getDataSetForCode("ActivityType", "DS_01")));
					WSClient.setData("{var_actlocation}", HTNGLib.getExtValue(resortOperaValue, interfaceName,
							"ACTIVITY_LOCATION", OperaPropConfig.getDataSetForCode("ActivityLocation", "DS_01")));
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

						List<LinkedHashMap<String, String>> dbResult3 = WSClient.getDBRows(WSClient.getQuery("QS_01"));
						if (dbResult3.size() > 0) {
							WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
							state = OperaPropConfig.getDataSetForCode("State", "DS_03");
							country_code = OperaPropConfig.getDataSetForCode("Country", "DS_03");
							HashMap<String, String> addLOV1 = new HashMap<String, String>();
							addLOV1 = OPERALib.fetchAddressLOV(state, country_code);

							WSClient.setData("{var_city}", addLOV1.get("City"));
							WSClient.setData("{var_zip}", addLOV1.get("Zip"));
							WSClient.setData("{var_state}", state);
							WSClient.setData("{var_country}", country_code);
							WSClient.setData("{var_addressType}",
									OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

							/*
							 * Prerequisites: Create Profile, Attach
							 * Subscription, Create Multiple Activities
							 */

							operaProfileID = CreateProfile.createProfile("DS_04");

							if (!operaProfileID.equals("error")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
								WSClient.setData("{var_profileID}", operaProfileID);
								WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

								// Making Subscription Request and getting the
								// Response
								subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
								subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
								if (WSAssert.assertIfElementValueEquals(subscriptionRes,
										"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

									// Creating Activities - Activity 1
									WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
									WSClient.setData("{var_actstatus}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS",
													OperaPropConfig.getDataSetForCode("ActivityStatus", "DS_01")));
									WSClient.setData("{var_acttype}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE",
													OperaPropConfig.getDataSetForCode("ActivityType", "DS_01")));
									WSClient.setData("{var_actlocation}",
											HTNGLib.getExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION",
													OperaPropConfig.getDataSetForCode("ActivityLocation", "DS_01")));
									createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
									createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
									if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
											"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

										List<LinkedHashMap<String, String>> dbResult = WSClient
												.getDBRows(WSClient.getQuery("QS_01"));
										if (dbResult.size() > 0) {
											// WSClient.setData("{var_state}",
											// HTNGLib.getExtValue(resortOperaValue,
											// interfaceName, "STATE", state));
											// WSClient.setData("{var_country}",
											// HTNGLib.getExtValue(resortOperaValue,
											// interfaceName, "COUNTRY_CODE",
											// country_code));
											// Activity Look Up
											String req_activityLookUp = WSClient
													.createSOAPMessage("HTNG2006ActivityLookup", "DS_16");
											String res_activityLookUp = WSClient.processSOAPMessage(req_activityLookUp);
											if (WSAssert.assertIfElementExists(res_activityLookUp,
													"Result_Text_TextElement", true)) {
												String text = WSClient.getElementValue(res_activityLookUp,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text element in the response is" + text + "</b>");
											}

											if (WSAssert.assertIfElementValueEquals(res_activityLookUp,
													"ActivityLookupResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												// Validating the data

												HashMap<String, String> xPath = new HashMap<>();
												xPath.put("ActivityLookup_ProfileID_UniqueID",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												xPath.put("ActivityLookup_PersonName_LastName",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												xPath.put("ActivityLookup_Address_StateProv",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												xPath.put("ActivityLookup_Address_CityName",
														"ActivityLookupResponse_ActivityLookupList_ActivityLookup");
												List<LinkedHashMap<String, String>> resResult1 = WSClient
														.getMultipleNodeList(res_activityLookUp, xPath, false,
																XMLType.RESPONSE);
												WSClient.setData("{var_state}", state);
												List<LinkedHashMap<String, String>> dbResult1 = WSClient
														.getDBRows(WSClient.getQuery("QS_18"));
												WSAssert.assertEquals(resResult1, dbResult1, false);
											}
										} else {

											WSClient.writeToReport(LogStatus.WARNING,
													"Activity didn't got attached to profile");
										}
									} else {

										WSClient.writeToReport(LogStatus.WARNING, "Activity not created");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Unable to make Subscription for the profile");
								}
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING, "Activity didn't got attached to profile");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Activity not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

}
