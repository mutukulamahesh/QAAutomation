package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchSubscription extends WSSetUp {

	/**
	 * @author ketvaidy
	 */

	@Test(groups = { "sanity", "OWS", "FetchSubscription", "Name", "fetchSubscription_38360" })

	/*****
	 * Verify that the details of a particular subscription associated to a
	 * profile are correctly fetched.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with a
	 * subscription.
	 *
	 *****/

	public void fetchSubscription_38360() {
		try {
			/**
			 * Method to verify that the correct Subscription details are
			 * fetched when the required fields are supplied to the
			 * FetchSubscription request for a configured channel.
			 **/

			String testName = "fetchSubscription_38360";
			WSClient.startTest(testName, "Verify that the correct Subscription details are fetched successfully",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ExternalSystem" })) {
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String operaProfileID = "";

				// ******** Setting the OWS Header *************//

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resortOperaValue);

				// ******** *Prerequisites: Create Profile, Attach Subscription
				// *************//

				String interfaceName = HTNGLib.getHTNGInterface();

				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_extSystem}",
						HTNGLib.getExternalDatabase(resortOperaValue, interfaceName));
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// *********Prerequisite: Create Profile ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");

					// ***************Preparing Change Profile Request and
					// getting the Response***********//

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_13");
					String changeProfileRes = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileRes, "ChangeProfileRS_Success", true)) {

						// ************Preparing FetchSubscription Request and
						// getting the response************//

						// WSClient.setData("{var_extSystem}",OperaPropConfig.getDataSetForCode("ExternalSystem","DS_01"));
						WSClient.setData("{var_extSystem}",
								HTNGLib.getExternalDatabase(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Successfully attached subscription to the given profile" + "</b>");
						String fetchSubscriptionReq = WSClient.createSOAPMessage("OWSFetchSubscription", "DS_01");
						String fetchSubscriptionRes = WSClient.processSOAPMessage(fetchSubscriptionReq);
						if (WSAssert.assertIfElementExists(fetchSubscriptionRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchSubscriptionRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Subscription response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
								"FetchSubscriptionResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchSubscriptionRes,
									"FetchSubscriptionResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Subscription response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
								"FetchSubscriptionResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							if (WSAssert.assertIfElementValueEquals(fetchSubscriptionRes,
									"FetchSubscriptionResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if (!(WSAssert.assertIfElementExists(fetchSubscriptionRes,
										"FetchSubscriptionResponse_NameIDs_UniqueID", false))) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The required Subscription Details missing from the response");
								} else {

									LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("FetchSubscriptionResponse_NameIDs_UniqueID_source",
											"FetchSubscriptionResponse_NameIDs_UniqueID");
									xpath.put("FetchSubscriptionResponse_NameIDs_UniqueID",
											"FetchSubscriptionResponse_NameIDs");
									String query = WSClient.getQuery("QS_01");
									db = WSClient.getDBRow(query);
									actualValues = WSClient.getSingleNodeList(fetchSubscriptionRes, xpath, false,
											XMLType.RESPONSE);
									actualValues.put("nameId1", operaProfileID);
									WSAssert.assertEquals(db, actualValues, false);
								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Subscription Operation unsuccessful");

							}
						}

						else {
							/**
							 * The ResultStatusFlag not found.This indicates an
							 * error in the schema
							 ****/
							WSClient.writeToReport(LogStatus.FAIL, "The schema is incorrect");
						}

					}
				}

				else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchSubscription", "Name", "in-QA" })

	/*****
	 * Verify that an error message is populated in the response when the
	 * profile ID is not sent in the Fetch Subscription request for a configured
	 * channel
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with a
	 * subscription.
	 *
	 *****/

	public void fetchSubscription_38520() {
		try {

			String testName = "fetchSubscription_38520";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when the profile ID is not sent in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ExternalSystem" })) {
				String resort = OPERALib.getResort();
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);

				// ******** Setting the OWS Header *************//

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);
				String interfaceName = HTNGLib.getHTNGInterface();

				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_extSystem}", OperaPropConfig.getDataSetForCode("ExternalSystem", "DS_01"));

				// ***************Preparing FetchSubscription Request and
				// getting the response****************//

				String fetchSubscriptionReq = WSClient.createSOAPMessage("OWSFetchSubscription", "DS_02");
				String fetchSubscriptionRes = WSClient.processSOAPMessage(fetchSubscriptionReq);
				if (WSAssert.assertIfElementExists(fetchSubscriptionRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchSubscriptionRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the Fetch Subscription response is:" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
						"FetchSubscriptionResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(fetchSubscriptionRes,
							"FetchSubscriptionResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the Fetch Subscription response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
						"FetchSubscriptionResponse_Result_resultStatusFlag", false)) {
					/*** Checking for the existence of the ResultStatusFlag **/
					WSAssert.assertIfElementValueEquals(fetchSubscriptionRes,
							"FetchSubscriptionResponse_Result_resultStatusFlag", "FAIL", false);

				}

				else {
					/**
					 * The ResultStatusFlag not found.This indicates an error in
					 * the schema
					 ****/
					WSClient.writeToReport(LogStatus.FAIL, "The schema is incorrect");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchSubscription", "Name", "in-QA" })

	/*****
	 * Verify that an error message is populated in the response when an invalid
	 * profile ID is sent in the Fetch Subscription request for a configured
	 * channel
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with a
	 * subscription.
	 *
	 *****/

	public void fetchSubscription_38521() {
		try {

			String testName = "fetchSubscription_38521";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when an invalid profile ID is sent in the request ",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ExternalSystem" })) {
				String resort = OPERALib.getResort();
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);

				// ******** Setting the OWS Header *************//

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				String interfaceName = HTNGLib.getHTNGInterface();

				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String operaProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
				WSClient.setData("{var_profileID}", operaProfileID);

				WSClient.setData("{var_extSystem}", OperaPropConfig.getDataSetForCode("ExternalSystem", "DS_01"));

				// ***************Preparing FetchSubscription Request and
				// getting the response*****************//

				String fetchSubscriptionReq = WSClient.createSOAPMessage("OWSFetchSubscription", "DS_03");
				String fetchSubscriptionRes = WSClient.processSOAPMessage(fetchSubscriptionReq);
				if (WSAssert.assertIfElementExists(fetchSubscriptionRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchSubscriptionRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the Fetch Subscription response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
						"FetchSubscriptionResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(fetchSubscriptionRes,
							"FetchSubscriptionResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the Fetch Subscription response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
						"FetchSubscriptionResponse_Result_resultStatusFlag", false)) {
					/*** Checking for the existence of the ResultStatusFlag **/

					if ((WSAssert.assertIfElementValueEquals(fetchSubscriptionRes,
							"FetchSubscriptionResponse_Result_resultStatusFlag", "FAIL", false))
							|| (WSAssert.assertIfElementValueEquals(fetchSubscriptionRes,
									"FetchSubscriptionResponse_Result_resultStatusFlag", "WARNING", false)))
						;

				}

				else {
					/**
					 * The ResultStatusFlag not found.This indicates an error in
					 * the schema
					 ****/
					WSClient.writeToReport(LogStatus.FAIL, "The schema is incorrect");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchSubscription", "Name", "in-QA" })

	/*****
	 * Verify that an error message is populated in the response when the
	 * profile ID with no subscriptions attached to it is sent in the Fetch
	 * Subscription request for a configured channel
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with no
	 * subscription attached to it.
	 *
	 *****/

	public void fetchSubscription_38522() {
		try {

			String testName = "fetchSubscription_38522";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when the profile ID with no subscriptions attached to it is sent in the request ",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ExternalSystem" })) {
				String resort = OPERALib.getResort();
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);

				// ******** Setting the OWS Header *************//

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				String interfaceName = HTNGLib.getHTNGInterface();

				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String operaProfileID = "";

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				// WSClient.setData("{var_extSystem}",OperaPropConfig.getDataSetForCode("ExternalSystem","DS_01"));

				// **************Prerequisites: Create Profile
				// ****************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");

					// ******Preparing FetchSubscription Request and getting the
					// response**********//

					String fetchSubscriptionReq = WSClient.createSOAPMessage("OWSFetchSubscription", "DS_04");
					String fetchSubscriptionRes = WSClient.processSOAPMessage(fetchSubscriptionReq);
					if (WSAssert.assertIfElementExists(fetchSubscriptionRes, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(fetchSubscriptionRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Fetch Subscription response is :" + message + "</b>");
					}
					if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
							"FetchSubscriptionResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(fetchSubscriptionRes,
								"FetchSubscriptionResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Fetch Subscription response is :" + message + "</b>");
					}
					if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
							"FetchSubscriptionResponse_Result_resultStatusFlag", false)) {
						/***
						 * Checking for the existence of the ResultStatusFlag
						 **/

						WSAssert.assertIfElementValueEquals(fetchSubscriptionRes,
								"FetchSubscriptionResponse_Result_resultStatusFlag", "FAIL", false);

					}

					else {
						/**
						 * The ResultStatusFlag not found.This indicates an
						 * error in the schema
						 ****/
						WSClient.writeToReport(LogStatus.FAIL, "The schema is incorrect");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchSubscription", "Name", "in-QA" })

	/*****
	 * Verify that an error message is populated in the response when an invalid
	 * External System is sent in the Fetch Subscription request for a
	 * configured channel
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with a
	 * subscription.
	 *
	 *****/

	public void fetchSubscription_38525() {
		try {

			String testName = "fetchSubscription_38525";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when an invalid External System is sent in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ExternalSystem" })) {
				String resort = OPERALib.getResort();
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);

				// ******** Setting the OWS Header *************//

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				String operaProfileID = "";
				String interfaceName = HTNGLib.getHTNGInterface();

				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				// WSClient.setData("{var_extSystem}",OperaPropConfig.getDataSetForCode("ExternalSystem","DS_01"));

				// *********Prerequisites: Create Profile, Attach Subscription
				// ******************/

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// **************Preparing Change Profile Request and
					// getting the Response********//
					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_13");
					String changeProfileRes = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileRes, "ChangeProfileRS_Success", true)) {

						// ******************Preparing FetchSubscription Request
						// and getting the response************//

						WSClient.setData("{var_extSystem}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Successfully attached subscription to the given profile" + "</b>");
						String fetchSubscriptionReq = WSClient.createSOAPMessage("OWSFetchSubscription", "DS_01");
						String fetchSubscriptionRes = WSClient.processSOAPMessage(fetchSubscriptionReq);

						if (WSAssert.assertIfElementExists(fetchSubscriptionRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchSubscriptionRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Subscription response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
								"FetchSubscriptionResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchSubscriptionRes,
									"FetchSubscriptionResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Subscription response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
								"FetchSubscriptionResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							WSAssert.assertIfElementValueEquals(fetchSubscriptionRes,
									"FetchSubscriptionResponse_Result_resultStatusFlag", "FAIL", false);

						}

						else {
							/**
							 * The ResultStatusFlag not found.This indicates an
							 * error in the schema
							 ****/
							WSClient.writeToReport(LogStatus.FAIL, "The schema is incorrect");
						}
					}

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to make Subscription for the profile");
					}
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// @Test(groups = { "sanity","OWS","FetchSubscription","Name" ,"in-QA"})

	/*****
	 * Verify that the details of a particular subscription associated to a
	 * profile are correctly fetched.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with a
	 * subscription.
	 *
	 *****/

	public void fetchSubscription_38526() {
		try {
			/**
			 * Method to verify that the correct Subscription details are
			 * fetched for a profile with multiple Subscriptions
			 **/

			String testName = "fetchSubscription_38526";
			WSClient.startTest(testName,
					"Verify that the correct Subscription details are fetched for a profile with multiple Subscriptions",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ExternalSystem" })) {

				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String operaProfileID = "";
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// ******** Setting the OWS Header *************//

				WSClient.setData("{var_resort}", resort);

				// ******** *Prerequisites: Create Profile, Attach Subscription
				// *************//

				String resortOperaValue = OPERALib.getResort();

				// *********Prerequisite: Create Profile ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");

					WSClient.setData("{var_extSystem}", OperaPropConfig.getDataSetForCode("ExternalSystem", "DS_02"));

					// ***************Preparing Change Profile Request and
					// getting the Response***********//

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_13");
					String changeProfileRes = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileRes, "ChangeProfileRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Successfully attached the first subscription to the given profile" + "</b>");

						WSClient.setData("{var_extSystem}",
								OperaPropConfig.getDataSetForCode("ExternalSystem", "DS_01"));

						String changeProfileReq2 = WSClient.createSOAPMessage("ChangeProfile", "DS_18");
						String changeProfileRes2 = WSClient.processSOAPMessage(changeProfileReq2);
						if (WSAssert.assertIfElementExists(changeProfileRes2, "ChangeProfileRS_Success", true)) {

							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully attached the second subscription to the given profile" + "</b>");

							// ************Preparing FetchSubscription Request
							// and getting the response************//

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
									OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

							String fetchSubscriptionReq = WSClient.createSOAPMessage("OWSFetchSubscription", "DS_01");
							String fetchSubscriptionRes = WSClient.processSOAPMessage(fetchSubscriptionReq);
							if (WSAssert.assertIfElementExists(fetchSubscriptionRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(fetchSubscriptionRes,
										"Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Fetch Subscription response is :" + message
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
									"FetchSubscriptionResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(fetchSubscriptionRes,
										"FetchSubscriptionResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Fetch Subscription response is :" + message
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(fetchSubscriptionRes,
									"FetchSubscriptionResponse_Result_resultStatusFlag", false)) {
								/***
								 * Checking for the existence of the
								 * ResultStatusFlag
								 **/

								if (WSAssert.assertIfElementValueEquals(fetchSubscriptionRes,
										"FetchSubscriptionResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									if (!(WSAssert.assertIfElementExists(fetchSubscriptionRes,
											"FetchSubscriptionResponse_NameIDs_UniqueID", false))) {
										WSClient.writeToReport(LogStatus.FAIL,
												"The required Subscription Details missing from the response");
									} else {

										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
										HashMap<String, String> xpath = new HashMap<String, String>();
										xpath.put("FetchSubscriptionResponse_NameIDs_UniqueID_source",
												"FetchSubscriptionResponse_NameIDs_UniqueID");
										xpath.put("FetchSubscriptionResponse_NameIDs_UniqueID",
												"FetchSubscriptionResponse_NameIDs");
										String query = WSClient.getQuery("QS_01");
										db = WSClient.getDBRow(query);
										actualValues = WSClient.getSingleNodeList(fetchSubscriptionRes, xpath, false,
												XMLType.RESPONSE);
										actualValues.put("nameId1", operaProfileID);
										WSAssert.assertEquals(db, actualValues, false);

									}
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Fetch Subscription Operation unsuccessful");

								}
							}

							else {
								/**
								 * The ResultStatusFlag not found.This indicates
								 * an error in the schema
								 ****/
								WSClient.writeToReport(LogStatus.FAIL, "The schema is incorrect");
							}

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Unable to make the second Subscription to the profile");
						}
					} else {

						WSClient.writeToReport(LogStatus.WARNING,
								"Unable to make the first Subscription to the profile");

					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

}
