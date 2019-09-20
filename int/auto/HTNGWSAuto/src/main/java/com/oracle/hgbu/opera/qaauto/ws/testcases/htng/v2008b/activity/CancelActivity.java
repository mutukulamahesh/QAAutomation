package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.activity;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CancelActivity extends WSSetUp {

	String operaProfileID = "";

	@Test(groups = { "sanity", "CancelActivity", "HTNG2008B", "HTNG" })
	/***** Canceling the activity created for the given profile *****/
	/*****
	 * To validate the activity is cancelled or not, validate inactive date
	 * column from the database.The date should be equal to the current system
	 * date * PreRequisites Required: -->Profile is created -->Subscribe to the
	 * profile -->New Activity is created
	 *****/
	public void cancelActivity_2008_4523() {
		try {
			String testName = "cancelActivity_2008_4526";
			WSClient.startTest(testName, "Verify that the activity created in Opera by the ATS (Activity Reservation System) is cancelled when a valid Cancel Activity call is issued", "sanity");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();
			OPERALib.setOperaHeader(uname);

			// *** Setting Opera Header *********//

			// String genderExtValue =
			// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
			// "GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			OPERALib.setOperaHeader(uname);

			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));
				WSClient.setData("{var_profileSource}", interfaceName);

				// *********** Prerequisite 2: Subscribe to HTNG
				// **************//

				// *** Setting HTNG Header *********//
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

				String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementExists(subscriptionRes, "SubscriptionResponse_Result", true)) {
					if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));

						WSClient.setData("{var_profileID}", operaProfileID);
						WSClient.setData("{var_extResort}", resortExtValue);
						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));

						// ************ Prerequisite 3 : Creating a new
						// activity. ************//

						String createActXML = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_01");
						String createActRes = WSClient.processSOAPMessage(createActXML);

						if (WSAssert.assertIfElementValueEquals(createActRes, "CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

							if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

								String operaActivityID = WSClient.getElementValueByAttribute(createActRes, "Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>OPERA Activity ID: " + operaActivityID + "</b>");

								String externalActivityID = WSClient.getElementValueByAttribute(createActRes, "Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName, XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>External Activity ID: " + externalActivityID + "</b>");

								WSClient.setData("{var_extActivityID}", externalActivityID);
								WSClient.setData("{var_activitySource}", interfaceName);
								WSClient.setData("{var_extResort}", resortExtValue);
								WSClient.setData("{var_activityID}", operaActivityID);

								String cancelActXML = WSClient.createSOAPMessage("HTNG2008CancelActivity", "DS_01");
								String cancelActRes = WSClient.processSOAPMessage(cancelActXML);

								if (WSAssert.assertIfElementExists(cancelActRes, "CancelActivityResponse_Results", false)) {
									if (WSAssert.assertIfElementValueEquals(cancelActRes, "CancelActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {

										// ********* Validation of the inactive
										// date with the current system date.
										// **********//
										String query = WSClient.getQuery("QS_02");
										HashMap<String, String> inactiveRecord = WSClient.getDBRow(query);

										if (WSAssert.assertEquals(inactiveRecord.get("INACTIVEDATE"), inactiveRecord.get("SYSDATE"), true)) {
											WSClient.writeToReport(LogStatus.PASS, "Inactive Date and SysDate are Equal");
											WSClient.writeToReport(LogStatus.INFO, "<b>" + "Inactive Date :    " + inactiveRecord.get("INACTIVEDATE") + "     SysDate : " + inactiveRecord.get("SYSDATE") + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "Inactive Date and SysDate are Not Equal");
											WSClient.writeToReport(LogStatus.INFO, "<b>" + "Inactive Date :    " + inactiveRecord.get("INACTIVEDATE") + "     SysDate : " + inactiveRecord.get("SYSDATE") + "</b>");
										}
									}
								}

							} else
								// ********** When createActivity is
								// blocked.*********//
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's  failed!------ Create Activity-----Blocked");
						} else
							// ********** When CreateActivity is
							// blocked.*********//
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Activity -----Blocked");
					} else
						// ********** When createSubscription is
						// blocked.*********//
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Subscription -----Blocked");
				}

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Prerequsiste Failed : Create Profile");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "CancelActivity", "HTNG2008B", "HTNG" })
	/*****
	 * Canceling an already cancelled activity created for the given profile
	 *****/
	/*****
	 * When an cancelled activity is cancelled again,error message is populated
	 * in the response.Validate that the error message is correctly shown. * *
	 * PreRequisites Required: -->Profile is created -->Subscribe to the profile
	 * -->New Activity is created --> Cancel the activity.
	 * 
	 *****/

	public void cancelActivity_2008_3076() {
		try {
			String testName = "cancelActivity_2008_3076";
			WSClient.startTest(testName, "Verify that correct error message is populated when attempting to cancel an inactive activity", "minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			if (operaProfileID == "")
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (operaProfileID != "error") {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");

				// *********** Prerequisite 2 :Subscribe to HTNG *************//

				// *** Setting HTNG Header *********//

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

				String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementExists(subscriptionRes, "SubscriptionResponse_Result", true)) {
					if (WSAssert.assertIfElementValueEquals(subscriptionRes, "SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
						String extActId = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");

						WSClient.setData("{var_extID}", extActId);
						WSClient.setData("{var_profileID}", operaProfileID);
						WSClient.setData("{var_extResort}", resortExtValue);
						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "ACTIVITY_LOCATION"));

						// ************* Prerequisite 3 :Creating a new
						// activity. ****************//

						String createActXML = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_01");
						String createActRes = WSClient.processSOAPMessage(createActXML);

						if (WSAssert.assertIfElementValueEquals(createActRes, "CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

							if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

								String operaActivityID = WSClient.getElementValueByAttribute(createActRes, "Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>OPERA Activity ID: " + operaActivityID + "</b>");

								String externalActivityID = WSClient.getElementValueByAttribute(createActRes, "Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName, XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>External Activity ID: " + externalActivityID + "</b>");

								WSClient.setData("{var_extActivityID}", externalActivityID);
								WSClient.setData("{var_activitySource}", interfaceName);
								WSClient.setData("{var_extResort}", resortExtValue);
								WSClient.setData("{var_activityID}", operaActivityID);

								// ************* HTNG Cancel Activity
								// ***********************//

								String cancelActXML = WSClient.createSOAPMessage("HTNG2008CancelActivity", "DS_01");
								String cancelActRes = WSClient.processSOAPMessage(cancelActXML);

								if (WSAssert.assertIfElementExists(cancelActRes, "CancelActivityResponse_Results", true)) {
									if (WSAssert.assertIfElementValueEquals(cancelActRes, "CancelActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

										// *********** Canceling an already
										// inactive activity ***************//

										WSClient.setData("{var_extActivityID}", externalActivityID);
										WSClient.setData("{var_activitySource}", interfaceName);
										WSClient.setData("{var_extResort}", resortExtValue);
										WSClient.setData("{var_activityID}", operaActivityID);

										String cancelActivityXML = WSClient.createSOAPMessage("HTNG2008CancelActivity", "DS_01");
										String cancelActivityRes = WSClient.processSOAPMessage(cancelActivityXML);

										if (WSAssert.assertIfElementExists(cancelActRes, "CancelActivityResponse_Results", false)) {
											if (WSAssert.assertIfElementValueEquals(cancelActivityRes, "CancelActivityResponse_Results_resultStatusFlag", "FAIL", false)) {
												// **** Validating whether the
												// correct error message is
												// populated on
												// the
												// response ***//

												String errorMessage = WSClient.getElementValue(cancelActivityRes, "Results_Text_TextElement", XMLType.RESPONSE);
												if (WSAssert.assertEquals("{NO_MATCHING_ACTIVITY_RECORD_FOUND_FOR_UPADATE}--NO MATCHING ACTIVITY RECORD FOUND FOR UPDATE", errorMessage, true))
													WSClient.writeToReport(LogStatus.PASS, "Expected: " + "{NO_MATCHING_ACTIVITY_RECORD_FOUND_FOR_UPADATE}--NO MATCHING ACTIVITY RECORD FOUND FOR UPDATE" + "	 Actual : " + errorMessage);
												else
													WSClient.writeToReport(LogStatus.FAIL, "Expected: " + "{NO_MATCHING_ACTIVITY_RECORD_FOUND_FOR_UPADATE}--NO MATCHING ACTIVITY RECORD FOUND FOR UPDATE" + "	 Actual : " + errorMessage);

											} else
												WSClient.writeToReport(LogStatus.FAIL, "The activity is cancelled!----Not an inactive activity----------");
										}

									}
								}
							} else
								// ********** When createActivity is
								// blocked.*********//
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's  failed!------ Create Activity-----Blocked");
						} else
							// ********** When CreateActivity is
							// blocked.*********//
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Activity -----Blocked");
					} else
						// ********** When createSubscription is
						// blocked.*********//x
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Subscription -----Blocked");
				} else
					// ********** When createSubscription is blocked.*********//
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Subscription -----Blocked");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Failed :Create Profile");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "CancelActivity", "HTNG2008B", "HTNG" })
	public void cancelActivity_2008_3077() {
		/*****
		 * To pass an incorrect external Activity Id and check whether the
		 * correct error message is being populated. * * PreRequisites Required:
		 * -->The external activity Id generated should not be there in
		 * database.
		 *****/

		try {

			String testName = "cancelActivity_2008_3077";
			WSClient.startTest(testName, "Verify correct error message is populated when incorrect external activity id is sent", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String extActId = "";
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			// ** Prerequisite : The external activity id generated should not
			// be there in
			// the database **//

			extActId = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");

			WSClient.setData("{var_extActivityID}", extActId);
			WSClient.setData("{var_activitySource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);

			// ********** HTNG Cancel Activity *****************//

			// *** Setting HTNG Header *********//

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

			String cancelActXML = WSClient.createSOAPMessage("HTNG2008CancelActivity", "DS_02");
			String cancelActRes = WSClient.processSOAPMessage(cancelActXML);

			if (WSAssert.assertIfElementExists(cancelActRes, "CancelActivityResponse_Results", false)) {
				if (WSAssert.assertIfElementValueEquals(cancelActRes, "CancelActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

					// ***** Validation :Whether the correct error message is
					// populated or not
					// ******//

					String errorMessage = WSClient.getElementValue(cancelActRes, "Results_Text_TextElement", XMLType.RESPONSE);
					if (WSAssert.assertEquals("{NO_MATCHING_ACTIVITY_RECORD_FOUND_FOR_UPADATE}--NO MATCHING ACTIVITY RECORD FOUND FOR UPDATE", errorMessage, true))
						WSClient.writeToReport(LogStatus.PASS, "Expected: " + "{NO_MATCHING_ACTIVITY_RECORD_FOUND_FOR_UPADATE}--NO MATCHING ACTIVITY RECORD FOUND FOR UPDATE" + "	 Actual : " + errorMessage);
					else
						WSClient.writeToReport(LogStatus.FAIL, "Expected: " + "{NO_MATCHING_ACTIVITY_RECORD_FOUND_FOR_UPADATE}--NO MATCHING ACTIVITY RECORD FOUND FOR UPDATE" + "	 Actual : " + errorMessage);

				} else
					WSClient.writeToReport(LogStatus.FAIL, "-----------The activity Id exists !!!--------");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

	@Test(groups = { "targetedRegression", "CancelActivity", "HTNG2008B", "HTNG" })
	public void cancelActivity_2008_3078() {

		/*****
		 * To not pass an activity check whether the correct error message is
		 * being populated. * *
		 *****/

		try {

			String testName = "cancelActivity_2008_3078";
			WSClient.startTest(testName, "Verify correct error message is populated when external activity id is not sent on the request.", "targetedRegression");

			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			// ** Prerequisite : The external activity id generated should not
			// be there in
			// the database **//

			WSClient.setData("{var_activitySource}", interfaceName);
			WSClient.setData("{var_extResort}", resortExtValue);

			// ********** HTNG Cancel Activity *****************//

			// *** Setting HTNG Header *********//

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

			String cancelActXML = WSClient.createSOAPMessage("HTNG2008CancelActivity", "DS_03");
			String cancelActRes = WSClient.processSOAPMessage(cancelActXML);

			if (WSAssert.assertIfElementExists(cancelActRes, "CancelActivityResponse_Results", false)) {
				if (WSAssert.assertIfElementValueEquals(cancelActRes, "CancelActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

					// ***** Validation :Whether the correct error message is
					// populated or not
					// ******//

					String errorMessage = WSClient.getElementValue(cancelActRes, "Results_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The error displayed on the response is :  </b>" + errorMessage + "</b>");

				} else
					WSClient.writeToReport(LogStatus.FAIL, "The activity id sent on the response!");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}
}