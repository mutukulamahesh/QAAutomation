package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchEmailList extends WSSetUp {
	/**
	 * @author psarawag
	 */
	@Test(groups = { "sanity", "OWS", "FetchEmailList", "Name" })
	public void fetchEmailList_38280() {
		try {

			String testName = "fetchEmailList_38280";
			WSClient.startTest(testName,
					"Verify that email is fetched when minimum required data in provided in request.", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_email}", fname + "." + lname + "@oracle.com");
				WSClient.setData("{var_primary}", "true");
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String profileID = CreateProfile.createProfile("DS_13");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID + "</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query1 = WSClient.getQuery("QS_04");
					HashMap<String, String> operaID = WSClient.getDBRow(query1);

					if (operaID.size() > 0) {
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));
						String FetchEmailReq = WSClient.createSOAPMessage("OWSFetchEmailList", "DS_01");
						String FetchEmailRes = WSClient.processSOAPMessage(FetchEmailReq);

						if (WSAssert.assertIfElementValueEquals(FetchEmailRes,
								"FetchEmailListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedEmailList = new LinkedHashMap<>();
							LinkedHashMap<String, String> actualEmailList = new LinkedHashMap<>();
							ArrayList<String> elementXpath = new ArrayList<>();

							elementXpath.add("FetchEmailListResponse_NameEmailList_NameEmail_operaId");
							elementXpath.add("FetchEmailListResponse_NameEmailList_NameEmail_insertUser");
							elementXpath.add("FetchEmailListResponse_NameEmailList_NameEmail_updateUser");
							elementXpath.add("FetchEmailListResponse_NameEmailList_NameEmail_primary");
							elementXpath.add("FetchEmailListResponse_NameEmailList_NameEmail_emailType");
							elementXpath.add("FetchEmailListResponse_NameEmailList_NameEmail");

							String val = WSClient.getElementValue(FetchEmailRes, elementXpath.get(3), XMLType.RESPONSE);

							if (val.contains("true"))
								val = "Y";
							else
								val = "N";

							expectedEmailList.put("PHONE_ID",
									WSClient.getElementValue(FetchEmailRes, elementXpath.get(0), XMLType.RESPONSE));
							expectedEmailList.put("INSERT_USER",
									WSClient.getElementValue(FetchEmailRes, elementXpath.get(1), XMLType.RESPONSE));
							expectedEmailList.put("UPDATE_USER",
									WSClient.getElementValue(FetchEmailRes, elementXpath.get(2), XMLType.RESPONSE));
							expectedEmailList.put("PRIMARY_YN", val);
							expectedEmailList.put("PHONE_TYPE",
									WSClient.getElementValue(FetchEmailRes, elementXpath.get(4), XMLType.RESPONSE));
							expectedEmailList.put("PHONE_NUMBER",
									WSClient.getElementValue(FetchEmailRes, elementXpath.get(5), XMLType.RESPONSE));

							String query2 = WSClient.getQuery("QS_01");
							actualEmailList = WSClient.getDBRow(query2);

							WSAssert.assertEquals(actualEmailList, expectedEmailList, false);
						}

						if (WSAssert.assertIfElementExists(FetchEmailRes,
								"FetchEmailListResponse_Result_OperaErrorCode", true)) {
							String message = WSAssert.getElementValue(FetchEmailRes,
									"FetchEmailListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(FetchEmailRes, "FetchEmailListResponse_Result_GDSError",
								true)) {
							String message = WSAssert.getElementValue(FetchEmailRes,
									"FetchEmailListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre Requisite Failure: Email was not attached to the profile");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author psarawag Description: Verify that multiple emails are fetched .
	 */
	@Test(groups = { "minimumRegression", "Name", "OWS", "FetchEmailList", "Name" })
	public void fetchEmailList_38401() {
		try {

			String testName = "fetchEmailList_38401";
			WSClient.startTest(testName, "Verify that multiple emails are fetched in the response",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_email}", (fname + "." + lname + "@oracle.com").toLowerCase());
				WSClient.setData("{var_primary}", "true");
				WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_04"));
				WSClient.setData("{var_email2}", (fname + "@oracle.com").toLowerCase());
				WSClient.setData("{var_primary2}", "false");
				WSClient.setData("{var_emailType2}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);

				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String profileID = CreateProfile.createProfile("DS_50");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID + "</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query1 = WSClient.getQuery("QS_04");
					List<LinkedHashMap<String, String>> phoneid = WSClient.getDBRows(query1);
					if (phoneid.size() >= 2) {

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

						// Validation request being created and processed to
						// generate response
						String FetchEmailReq = WSClient.createSOAPMessage("OWSFetchEmailList", "DS_01");
						String FetchEmailRes = WSClient.processSOAPMessage(FetchEmailReq);

						if (WSAssert.assertIfElementValueEquals(FetchEmailRes,
								"FetchEmailListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							List<LinkedHashMap<String, String>> expectedEmailList = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualEmailList = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> elementXpath = new HashMap<>();

							// xPaths of the records being verified and their
							// parents are being put in a hashmap
							elementXpath.put("FetchEmailListResponse_NameEmailList_NameEmail_operaId",
									"FetchEmailListResponse_NameEmailList_NameEmail");
							elementXpath.put("FetchEmailListResponse_NameEmailList_NameEmail_primary",
									"FetchEmailListResponse_NameEmailList_NameEmail");
							elementXpath.put("FetchEmailListResponse_NameEmailList_NameEmail_emailType",
									"FetchEmailListResponse_NameEmailList_NameEmail");
							elementXpath.put("FetchEmailListResponse_NameEmailList_NameEmail_displaySequence",
									"FetchEmailListResponse_NameEmailList_NameEmail");
							elementXpath.put("FetchEmailListResponse_NameEmailList_NameEmail",
									"FetchEmailListResponse_NameEmailList_NameEmail");

							// database records are being stored in a list of
							// hashmaps
							String query2 = WSClient.getQuery("QS_02");
							expectedEmailList = WSClient.getDBRows(query2);

							// response records are being stored in a list of
							// hashmaps
							actualEmailList = WSClient.getMultipleNodeList(FetchEmailRes, elementXpath, false,
									XMLType.RESPONSE);

							// database records and response records are being
							// compared
							WSAssert.assertEquals(actualEmailList, expectedEmailList, false);

						}
						if (WSAssert.assertIfElementExists(FetchEmailRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(FetchEmailRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(FetchEmailRes,
								"FetchEmailListResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(FetchEmailRes,
									"FetchEmailListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(FetchEmailRes, "FetchEmailListResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(FetchEmailRes,
									"FetchEmailListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The GDSerror displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre Requisite Failure: Emails were not attached to the profile");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author psarawag Description:Verify that the response throws the correct
	 *         error when invalid profileID is passed in the request.
	 */
	@Test(groups = { "minimumRegression", "Name", "OWS", "FetchEmailList", "Name", "in-QA" })
	public void fetchEmailList_38282() {
		try {

			String testName = "fetchEmailList_38282";
			WSClient.startTest(testName,
					"Verify that the response throws the correct error when invalid profileID is passed in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType" })) {
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);

				WSClient.setData("{var_owsresort}", owsresort);
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

				WSClient.setData("{var_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));

				// Validation request being created and processed to
				// generate response
				String FetchEmailReq = WSClient.createSOAPMessage("OWSFetchEmailList", "DS_01");
				String FetchEmailRes = WSClient.processSOAPMessage(FetchEmailReq);

				if (WSAssert.assertIfElementValueEquals(FetchEmailRes, "FetchEmailListResponse_Result_resultStatusFlag",
						"WARNING", true)
						|| WSAssert.assertIfElementValueEquals(FetchEmailRes,
								"FetchEmailListResponse_Result_resultStatusFlag", "FAIL", true)) {

					// error codes are checked
					if (WSAssert.assertIfElementExists(FetchEmailRes, "Result_Text_TextElement", true)) {
						String errorMsg = WSAssert.getElementValue(FetchEmailRes, "Result_Text_TextElement",
								XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>Error message appears on the response : " + errorMsg + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.INFO, "error message did not appear on the response");
					}
				}
				if (WSAssert.assertIfElementExists(FetchEmailRes, "FetchEmailListResponse_Result_OperaErrorCode",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(FetchEmailRes,
							"FetchEmailListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(FetchEmailRes, "FetchEmailListResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(FetchEmailRes, "FetchEmailListResponse_Result_GDSError",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author psarawag Description: Verify that phone records are not fetched .
	 */
	@Test(groups = { "minimumRegression", "Name", "OWS", "FetchEmailList", "Name", "in-QA" })
	public void fetchEmailList_39130() {
		try {

			String testName = "fetchEmailList_39130";
			WSClient.startTest(testName, "Verify that phone records are not fetched in the response",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String profileID = CreateProfile.createProfile("DS_11");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID + "</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query1 = WSClient.getQuery("QS_04");
					List<LinkedHashMap<String, String>> phoneid = WSClient.getDBRows(query1);
					if (phoneid.size() >= 1) {

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

						// Validation request being created and processed to
						// generate response
						String FetchEmailReq = WSClient.createSOAPMessage("OWSFetchEmailList", "DS_01");
						String FetchEmailRes = WSClient.processSOAPMessage(FetchEmailReq);

						if (WSAssert.assertIfElementValueEquals(FetchEmailRes,
								"FetchEmailListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(FetchEmailRes,
									"FetchEmailListResponse_NameEmailList_NameEmail_emailType", true)) {
								WSClient.writeToReport(LogStatus.FAIL, "Phone Information is displayed.");
							} else {
								WSClient.writeToReport(LogStatus.PASS,
										"Phone Information is not displayed as expected.");
							}

						}
						if (WSAssert.assertIfElementExists(FetchEmailRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(FetchEmailRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(FetchEmailRes,
								"FetchEmailListResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(FetchEmailRes,
									"FetchEmailListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(FetchEmailRes, "FetchEmailListResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(FetchEmailRes,
									"FetchEmailListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The GDSerror displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre Requisite Failure: Phone number is not attached to the profile");
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
}
