package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2006.profile;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class Subscription extends WSSetUp {

	@Test(groups = { "sanity", "Subscription", "HTNG2006", "HTNG" })
	public void subscription_2006_20591() {
		try {

			String testName = "subscription_2006_20591";
			WSClient.startTest(testName,
					"Verify that the subscription (Linking the Opera Profile with the External Profile) is done in"
							+ " CRM when the Subscription call is issued with the SUBSCRIBE action",
					"sanity");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			//
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Create new Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());

			String profileID = null;
			profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId: " + profileID + "</b>");
				String externalID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}", externalID);
				// Subscribe the profile
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				// Validating the Response
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {

					WSClient.writeToReport(LogStatus.PASS,
							"found \"Success\" in the Response. Continuing with DB Validation.");

					// Fetching Data From the DB
					LinkedHashMap<String, String> dbResultSet = new LinkedHashMap<>();
					String query1 = WSClient.getQuery("QS_01");
					dbResultSet = WSClient.getDBRow(query1);

					LinkedHashMap<String, String> expectedResultSet = new LinkedHashMap<>();
					expectedResultSet.put("NAME_ID", profileID);
					expectedResultSet.put("DATABASE_ID",
							HTNGLib.getExternalDatabase(OPERALib.getResort(), WSClient.getData("{var_profileSource}")));
					expectedResultSet.put("FORCE_YN", "N");
					expectedResultSet.put("DATABASE_NAME_ID", WSClient.getData("{var_E_profileID}"));
					expectedResultSet.put("DISTRIBUTE_YN", "Y");

					WSAssert.assertEquals(expectedResultSet, dbResultSet, false);
				} else {
					WSClient.writeToReport(LogStatus.FAIL,
							"Subscrition Operation Failed Cannot find \"Success\" in response");
				}
				if (WSAssert.assertIfElementExists(subscriptionRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(subscriptionRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Create Profile Failed");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "Subscription", "HTNG2006", "HTNG" })
	public void subscription_2006_20586() {
		try {
			String testName = "subscription_2006_20586";

			WSClient.startTest(testName,
					"Verify that the existing subscription (Linking the Opera Profile with the External Profile) is cancelled in CRM when the "
							+ "Subscription call is issued with the UNSUBSCRIBE action",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			//
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Create new Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());

			String profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				// Subscribing the Profile
				String externalID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId: " + profileID + "</b>");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}", externalID);
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId: " + externalID + "</b>");
					WSClient.writeToReport(LogStatus.INFO, "Subscription Successfull");

					// UnSubscribing the above Profile
					String unSubscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_02");
					String unSubscriptionRes = WSClient.processSOAPMessage(unSubscriptionReq);

					// Validate if subscription is cancelled
					if (WSAssert.assertIfElementValueEquals(unSubscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						WSClient.writeToReport(LogStatus.INFO, "Succesfully Unsubscribed");

						// Geting DB Value
						HashMap<String, String> dbResultSet = new HashMap<>();
						String query1 = WSClient.getQuery("QS_02");
						dbResultSet = WSClient.getDBRow(query1);

						System.out.println("Count:" + dbResultSet.get("COUNT"));
						String val = dbResultSet.get("COUNT").trim();
						if (val.contains("0")) {
							WSClient.writeToReport(LogStatus.PASS, "Db Validation Passed");
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "DB Validation Failed");
						}

					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Unsubscribing the profile failed");
					}
					if (WSAssert.assertIfElementExists(unSubscriptionRes, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(unSubscriptionRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Subscription Operation Failed");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "Subscription", "HTNG2006", "HTNG" })
	public void subscription_2006_20579() {
		try {
			String testName = "subscription_2006_20579";
			WSClient.startTest(testName,
					"Verify that an error message is displayed on the response when the Subscription call is issued with the "
							+ "SUBSCRIBE action for a profile that has an existing subscription",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			//
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Create new Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());

			String profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId: " + profileID + "</b>");
				String externalID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}", externalID);

				// Subscritpion Request and getting the Response
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully Subscribed Profile\n OperaID: " + WSClient.getData("{var_profileID}")
							+ "      ExtID: " + WSClient.getData("{var_E_profileID}") + "</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>ReSubscribing the Same Profile</b>");

					// Trying to Re-subscribe the Same profile
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));
					String reSubscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
					String reSubscriptionRes = WSClient.processSOAPMessage(reSubscriptionReq);

					WSAssert.assertIfElementValueEquals(reSubscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", false);
					HashMap<String, String> dbResultSet = new HashMap<>();
					String query1 = WSClient.getQuery("QS_02");
					dbResultSet = WSClient.getDBRow(query1);
					int count = Integer.parseInt(dbResultSet.get("COUNT"));
					System.out.println("Count : " + count);
					if (count == 1) {
						WSClient.writeToReport(LogStatus.PASS,
								"Validation is passed (Only one subscription found in DB)");
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Validation is failed (" + count + "subscription(s) found in DB)");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites failed!------ Subscription-----Blocked");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test dueto:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "subscription", "HTNG2006", "HTNG" })
	public void subscription_2006_20587() {
		try {
			String testName = "subscription_2006_20587";

			WSClient.startTest(testName,
					"Verify that an error message is displayed on the response when the Subscription call is issued with the "
							+ "UNSUBSCRIBE action for a profile that has no existing subscription",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			//
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Create new Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());

			String profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId: " + profileID + "</b>");
				String externalID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}", externalID);

				// Subscritpion Request and getting the Response
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId: " + externalID + "</b>");
					// if Subscribed the unsubscribing the profile
					String unSubscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_02");
					String unSubscriptionRes = WSClient.processSOAPMessage(unSubscriptionReq);

					// validating against the DB if profile Unsubscription
					// Passed
					HashMap<String, String> dbResultSet = new HashMap<>();
					String query1 = WSClient.getQuery("QS_02");
					dbResultSet = WSClient.getDBRow(query1);
					String count = (dbResultSet.get("COUNT"));
					WSAssert.assertEquals(count, "0", true);

					if (WSAssert.assertIfElementValueEquals(unSubscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Profile Unsubscrition is succesfull");

						// UnSubscribing the unsubscribed the profile
						String reUnSubscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_02");
						String reUnSubscriptionRes = WSClient.processSOAPMessage(reUnSubscriptionReq);

						if (WSAssert.assertIfElementExists(reUnSubscriptionRes, "SubscriptionResponse_Result_Text",
								false)) {
							WSAssert.assertIfElementContains(reUnSubscriptionRes, "SubscriptionResponse_Result_Text",
									"No matching record exists to update", false);

						}
						if (WSAssert.assertIfElementExists(reUnSubscriptionRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(reUnSubscriptionRes, "Result_Text_TextElement",
									XMLType.RESPONSE);

						}

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Cannot Find the Success in the Unsubscription Response");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Subscrition Failed Cannot Proceed for Unsbscribing");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test dueto:" + e);
		}
	}

	@Test(groups = { "fullRegression", "Subscription", "HTNG2006", "HTNG" })
	public void subscription_2006_20582() {
		try {

			String testName = "subscription_2006_20582";
			WSClient.startTest(testName, "Verify Error message when an invalid profile id is passed", "fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			//
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Create new Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());

			String profileID = null;
			profileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_8}");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId: " + profileID + "</b>");
				String externalID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}", externalID);
				// Subscribe the profile
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				// Validating the Response
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"FAIL", false)) {
				} else {
					WSClient.writeToReport(LogStatus.FAIL,
							"Subscrition Operation Failed Cannot find \"Success\" in response");
				}
				if (WSAssert.assertIfElementExists(subscriptionRes, "Result_Text_TextElement", true)) {
					String message = WSAssert.getElementValue(subscriptionRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Create Profile Failed");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author kankur
	 */
	/*
	 * @Test(groups = { "fullRegression", "Subscription", "HTNG2006", "HTNG" })
	 * public void subscription_2006_20581() { try { String testName =
	 * "subscription_2006_20581"; WSClient.startTest(testName,
	 * "Verify that an error message is displayed on the response when the Subscription call is invoked with an action SUBSCRIBE without submitting the Resort"
	 * , "fullRegression"); String interfaceName = HTNGLib.getHTNGInterface();
	 * String resortOperaValue = OPERALib.getResort(); String resortExtValue =
	 * HTNGLib.getExtResort(resortOperaValue, interfaceName);
	 *
	 *
	 * //Declaring required variables WSClient.setData("{var_profileSource}",
	 * interfaceName); WSClient.setData("{var_extResort}", resortExtValue);
	 * WSClient.setData("{var_resort}", resortOperaValue);
	 *
	 *
	 * // Creating a new Profile
	 * OPERALib.setOperaHeader(OPERALib.getUserName()); String profileID = null;
	 * profileID = CreateProfile.createProfile("DS_01");
	 *
	 * if (!profileID.equals("error")) { WSClient.writeToReport(LogStatus.INFO,
	 * "<b>ProfileId: "+profileID+"</b>"); String externalID =
	 * WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
	 * WSClient.setData("{var_profileID}", profileID);
	 * WSClient.setData("{var_E_profileID}", externalID);
	 *
	 * // Setting HTNG header and then subscribing the profile
	 * HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	 * HTNGLib.getInterfaceFromAddress()); String subscriptionReq =
	 * WSClient.createSOAPMessage("HTNG2006Subscription", "DS_06"); String
	 * subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
	 *
	 * // Validating the Response, Checking Result status flag if it is fail or
	 * not if (WSAssert.assertIfElementValueEquals(subscriptionRes,
	 * "SubscriptionResponse_Result_resultStatusFlag","FAIL", true)) {
	 * WSClient.writeToReport(LogStatus.PASS,
	 * "Found \"Fail\" in the Response. Test case failed as expected"); } //When
	 * Result status flag if it is success else {
	 * WSClient.writeToReport(LogStatus.FAIL,
	 * "Found \"Success\" in the Response!"); } if
	 * (WSAssert.assertIfElementExists(subscriptionRes,
	 * "Result_Text_TextElement", true)) {
	 *//****
	 * Verifying that the error message is populated on the response
	 ********//*
	 * String message = WSAssert.getElementValue(subscriptionRes,
	 * "Result_Text_TextElement", XMLType.RESPONSE);
	 * WSClient.writeToReport(LogStatus.INFO, "<b>"+
	 * "The text displayed in the response is :" + message +"</b>");
	 * } } else { WSClient.writeToReport(LogStatus.WARNING,
	 * "Create Profile Failed"); } } catch (Exception e) {
	 * WSClient.writeToReport(LogStatus.ERROR,
	 * "Exception occured in test due to:" + e); } }
	 */

	@Test(groups = { "fullRegression", "Subscription", "HTNG2006", "HTNG" })
	public void subscription_2006_20585() {
		try {
			String testName = "subscription_2006_20585";
			WSClient.startTest(testName,
					"Verify that an error message is displayed on the response when subscription is requested for a valid Opera profile but with an incorrect source (Not OPERA)",
					"fullRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			// Declaring required variables
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Creating a new Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());
			String profileID = null;
			profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId: " + profileID + "</b>");
				String externalID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}", externalID);

				// Setting HTNG header and then subscribing the profile
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_07");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				// Validating the Response, Checking Result status flag if it is
				// fail or not
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"FAIL", true)) {
					WSClient.writeToReport(LogStatus.PASS,
							"Found \"Fail\" in the Response. Test case failed as expected");
				}
				// When Result status flag if it is success
				else {
					WSClient.writeToReport(LogStatus.FAIL, "Found \"Success\" in the Response!");
				}
				if (WSAssert.assertIfElementExists(subscriptionRes, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(subscriptionRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Create Profile Failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "targetedRegression", "Subscription", "HTNG2006", "HTNG" })
	public void subscription_2006_20584() {
		try {
			String testName = "subscription_2006_20584";
			WSClient.startTest(testName,
					"Verify that the external system name is auto updated to default External interface in CRM even when the subscription call is issued providing an invalid source name for the external profile identifier",
					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String intfName = "SOMERANDOM";
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			// Declaring required variables
			WSClient.setData("{var_profileSource}", intfName);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Creating a new Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());
			String profileID = null;
			profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId: " + profileID + "</b>");
				String externalID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}", externalID);

				// Setting HTNG header and then subscribing the profile
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				// Validating the Response
				if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.PASS,
							"Found \"Success\" in the Response. Continuing with DB Validation.");

					// Fetching Data From the DB
					LinkedHashMap<String, String> dbResultSet = new LinkedHashMap<>();
					String QS04 = WSClient.getQuery("QS_04");
					dbResultSet = WSClient.getDBRow(QS04);

					LinkedHashMap<String, String> expectedResultSet = new LinkedHashMap<>();
					expectedResultSet.put("NAME_ID", profileID);
					expectedResultSet.put("DATABASE_ID",
							HTNGLib.getExternalDatabase(OPERALib.getResort(), HTNGLib.getHTNGInterface()));
					// Even though we are passing some invalid source name it
					// should be updated
					// to SPASOFT in DB
					expectedResultSet.put("DATABASE_NAME_ID", WSClient.getData("{var_E_profileID}"));

					// Validating
					WSAssert.assertEquals(expectedResultSet, dbResultSet, false);
				}
				// When there is FAIL ResultStatusFlag
				else {
					WSClient.writeToReport(LogStatus.FAIL,
							"Subscrition Operation Failed Cannot find \"Success\" in response");
				}
				if (WSAssert.assertIfElementExists(subscriptionRes, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(subscriptionRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Create Profile Failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


}
