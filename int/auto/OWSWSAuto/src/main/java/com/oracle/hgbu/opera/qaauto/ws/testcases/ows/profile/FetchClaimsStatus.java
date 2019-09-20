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

public class FetchClaimsStatus extends WSSetUp {

	String operaProfileID = "";

	@Test(groups = { "minimumRegression", "OWS", "FetchClaimsStatus", "Name" })

	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel with minimum data(Claim ID)
	 * in the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/

	public void fetchClaimsStatus_39980() {
		try {
			String testName = "fetchClaimsStatus_39980";
			WSClient.startTest(testName,
					"Verify that the status of the claim submitted by a profile is correctly fetched based on Profile ID",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String chain = OPERALib.getChain();

			// ******** Setting the Opera and OWS Header *************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_claimSource}", "WEB");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//
				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");

					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");
						WSClient.setData("{var_claimSource}", "WEB");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_01");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							if (WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if (!(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
										"ClaimsList_ClaimsInfo_ClaimId", false))) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The required Claim Details are missing from the response");
								} else {
									LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ClaimsList_ClaimsInfo_ClaimId",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimStatus",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimType",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									actualValues = WSClient.getSingleNodeList(fetchClaimsStatusRes, xpath, false,
											XMLType.RESPONSE);

									String query = WSClient.getQuery("QS_01");
									db = WSClient.getDBRow(query);
									WSAssert.assertEquals(db, actualValues, false);

								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Claims Status Operation unsuccessful");

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

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchClaimsStatus", "Name" })
	/*****
	 * Method to verify if the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when Claim Type is given in
	 * the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40080() {
		try {

			String testName = "fetchClaimsStatus_40080";
			WSClient.startTest(testName,
					"Verify that the status of the claim submitted by a profile is correctly fetched when Claim Type is given in the request",
					"minimumRegression");
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);

			String operaProfileID = "";
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			// ******** Setting the Opera and OWS Header *************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);
				WSClient.setData("{var_claimSource}", "WEB");

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_03");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							if (WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if (!(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
										"ClaimsList_ClaimsInfo_ClaimId", false))) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The required Claim Details are missing from the response");
								} else {

									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ClaimsList_ClaimsInfo_ClaimId",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimType",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimStatus",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									String query2 = WSClient.getQuery("QS_03");
									db = WSClient.getDBRows(query2);
									actualValues = WSClient.getMultipleNodeList(fetchClaimsStatusRes, xpath, false,
											XMLType.RESPONSE);
									WSAssert.assertEquals(actualValues, db, false);

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Claims Status Operation unsuccessful");

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

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// @Test(groups = { "minimumRegression","OWS","FetchClaimsStatus","Name" })
	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when Claim Source is given in
	 * the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40083() {
		try {

			String testName = "fetchClaimsStatus_40083";
			WSClient.startTest(testName,
					"Verify that the status of the claim submitted by a profile is correctly fetched when Claim Source is given in the request",
					"minimumRegression");

			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String operaProfileID = "";

			// ******** Setting the Opera and OWS Header *************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.setData("{var_claimSource}", "WEB");
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_04");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							if (WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if (!(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
										"ClaimsList_ClaimsInfo_ClaimId", false))) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The required Claim Details are missing from the response");
								} else {

									WSClient.setData("{var_claimSource}", "WEB");
									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ClaimsList_ClaimsInfo_ClaimId",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimStatus",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									String query3 = WSClient.getQuery("QS_04");
									db = WSClient.getDBRows(query3);
									actualValues = WSClient.getMultipleNodeList(fetchClaimsStatusRes, xpath, false,
											XMLType.RESPONSE);
									WSAssert.assertEquals(actualValues, db, false);

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Claims Status Operation unsuccessful");

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

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create membership claim");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "sanity", "OWS", "FetchClaimsStatus", "Name" })

	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when MembershipNumber is
	 * given in the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40100() {
		try {

			String testName = "fetchClaimsStatus_40100";
			WSClient.startTest(testName,
					"Verify that the status of the claim submitted by a profile is correctly fetched when MembershipNumber is given in the request",
					"sanity");
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String operaProfileID = "";

			// ******** Setting the Opera and OWS Header *************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.setData("{var_claimSource}", "WEB");
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_05");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							if (WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if (!(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
										"ClaimsList_ClaimsInfo_ClaimId", false))) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The required Claim Details are missing from the response");
								} else {

									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ClaimsList_ClaimsInfo_ClaimId",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimType",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimStatus",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									String query4 = WSClient.getQuery("QS_05");
									db = WSClient.getDBRows(query4);
									actualValues = WSClient.getMultipleNodeList(fetchClaimsStatusRes, xpath, false,
											XMLType.RESPONSE);
									WSAssert.assertEquals(actualValues, db, false);

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Claims Status Operation unsuccessful");

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

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchClaimsStatus", "Name" })

	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when owner is specified in
	 * the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40101() {
		try {

			String testName = "fetchClaimsStatus_40101";
			WSClient.startTest(testName,
					"Verify that the status of the claim submitted by a profile is correctly fetched when owner is specified in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String operaProfileID = "";
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			// ******** Setting the Opera and OWS Header *************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");

					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.setData("{var_claimSource}", "WEB");
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_06");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							if (WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "SUCCESS", true)) {

								if (!(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
										"ClaimsList_ClaimsInfo_ClaimId", false))) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The required Claim Details are missing from the response");
								} else {

									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ClaimsList_ClaimsInfo_ClaimId",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimType",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimStatus",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									String query5 = WSClient.getQuery("QS_06");
									db = WSClient.getDBRows(query5);
									actualValues = WSClient.getMultipleNodeList(fetchClaimsStatusRes, xpath, false,
											XMLType.RESPONSE);
									WSAssert.assertEquals(actualValues, db, false);

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Claims Status Operation unsuccessful");

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

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchClaimsStatus", "Name" })

	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when all the required fields
	 * are specified in the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40102() {
		try {

			String testName = "fetchClaimsStatus_40102";
			WSClient.startTest(testName,
					"Verify that the status of the claim submitted by a profile is correctly fetched based on all claim details",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String operaProfileID = "";
			// ******** Setting the Opera and OWS Header *************//
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_02");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.setData("{var_claimSource}", "WEB");
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_07");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							if (WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if (!(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
										"ClaimsList_ClaimsInfo_ClaimId", false))) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The required Claim Details are missing from the response");
								} else {

									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ClaimsList_ClaimsInfo_ClaimId",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimType",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									xpath.put("ClaimsList_ClaimsInfo_ClaimStatus",
											"FetchClaimsStatusResponse_ClaimsList_ClaimsInfo");
									String query6 = WSClient.getQuery("QS_07");
									db = WSClient.getDBRows(query6);
									actualValues = WSClient.getMultipleNodeList(fetchClaimsStatusRes, xpath, false,
											XMLType.RESPONSE);
									WSAssert.assertEquals(actualValues, db, false);

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Claims Status Operation unsuccessful");

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

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchClaimsStatus", "Name" })

	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when invalid claimID is
	 * passed request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40120() {
		try {

			String testName = "fetchClaimsStatus_40120";
			WSClient.startTest(testName,
					"Verify that an error message is obtained when an invalid claimID is passed in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String operaProfileID = "";
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			// ******** Setting the Opera and OWS Header *************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.setData("{var_claimSource}", "WEB");
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_08");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "FAIL", false);
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
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "FetchClaimsStatus", "Name" })

	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when invalid claim Type is
	 * passed in the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40141() {
		try {

			String testName = "fetchClaimsStatus_40141";
			WSClient.startTest(testName,
					"Verify that an error message is obtained when an invalid claim Type is passed in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String operaProfileID = "";
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			// ******** Setting the Opera and OWS Header *************//
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.setData("{var_claimSource}", "WEB");
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_09");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "FAIL", false);
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
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// //@Test(groups = { "minimumRegression","OWS","FetchClaimsStatus","Name"
	// })
	//
	// /***** Method to verify that the status of the claim submitted by a
	// profile is
	// * *correctly fetched for a configured channel when invalid claim Source
	// is passed in the request*****/
	//
	// /*****
	// * * * PreRequisites Required: -->There should be a profile with a claim.
	// *
	// *****/
	// public void fetchClaimsStatus_40142() {
	// try {
	//
	// String testName = "fetchClaimsStatus_40142";
	// WSClient.startTest(testName,
	// "Verify that an error message is obtained when an invalid claim Source is
	// passed in the request", "minimumRegression");
	//
	// String resort = OPERALib.getResort();
	// String channel = OWSLib.getChannel();
	// String operaProfileID="";
	// String chain=OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	//
	//
	// //******** Setting the Opera and OWS Header *************//
	//
	// if(OperaPropConfig.getPropertyConfigResults(new String[]
	// {"MClaimType"})){
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// OPERALib.getResort(),
	// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
	// channel));
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// WSClient.setData("{var_resort}", resort);
	//
	//
	// //******** Prerequisite :Create Profile*************//
	//
	//
	// WSClient.setData("{var_claimType}",
	// OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
	// String membershipNumber=WSClient.getKeywordData("{KEYWORD_ID}");
	// WSClient.setData("{var_membershipNumber}",membershipNumber);
	// String owner=WSClient.getKeywordData("{KEYWORD_FNAME}");
	// WSClient.setData("{var_owner}",owner);
	//
	//
	// //*********Prerequisites: Create Profile, Create Membership Claim
	// ***************//
	//
	// if (operaProfileID.equals(""))
	// operaProfileID = CreateProfile.createProfile("DS_01");
	// if (!operaProfileID.equals("error")) {
	// WSClient.setData("{var_profileID}", operaProfileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" +
	// operaProfileID+"</b>");
	// WSClient.setData("{var_E_profileID}",
	// WSClient.getKeywordData("{KEYWORD_ID}"));
	//
	// //***************Preparing CreateMembershipClaim Request and fetching the
	// ClaimID of the claim created for the profile***********//
	//
	// String createMembershipClaimReq =
	// WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
	// String createMembershipClaimRes =
	// WSClient.processSOAPMessage(createMembershipClaimReq);
	// if(WSAssert.assertIfElementExists(createMembershipClaimRes,
	// "CreateMembershipClaimRS_Success",true))
	// {
	// String claimID=
	// WSClient.getElementValue(createMembershipClaimRes,"CreateMembershipClaimRS_ClaimNo_ID",XMLType.RESPONSE);
	// WSClient.setData("{var_claimID}", claimID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID+"</b>");
	//
	// //************Preparing FetchClaimsStatus Request and getting the
	// response************//
	//
	// String fetchClaimsStatusReq =
	// WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_10");
	// String fetchClaimsStatusRes =
	// WSClient.processSOAPMessage(fetchClaimsStatusReq);
	// if(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
	// "Result_Text_TextElement",true))
	// {
	//
	// /**** Verifying that the error message is populated on the response
	// ********/
	//
	// String message=WSAssert.getElementValue( fetchClaimsStatusRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the
	// Fetch Claims Status response is :"+ message+"</b>");
	// }
	// if(WSAssert.assertIfElementExists( fetchClaimsStatusRes,
	// "FetchClaimsStatusResponse_Result_OperaErrorCode",true))
	// {
	//
	// /**** Verifying whether the error Message is populated on the response
	// ****/
	//
	// String message=WSAssert.getElementValue(fetchClaimsStatusRes,
	// "FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the
	// Fetch Claims Status response is :"+ message+"</b>");
	// }
	// if(WSAssert.assertIfElementExists(fetchClaimsStatusRes,
	// "FetchClaimsStatusResponse_Result_resultStatusFlag", false))
	// {
	// /***Checking for the existence of the ResultStatusFlag**/
	//
	// WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
	// "FetchClaimsStatusResponse_Result_resultStatusFlag", "FAIL", false);
	//
	// }
	//
	// else {
	// /**The ResultStatusFlag not found.This indicates an error in the schema
	// ****/
	// WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
	// }
	//
	//
	// }
	//
	// else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for
	// the profile");
	// }
	// }
	//
	// }
	//
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.FAIL, "Exception occured in test due
	// to:" + e);
	// }
	// }

	@Test(groups = { "minimumRegression", "OWS", "FetchClaimsStatus", "Name" })

	/*****
	 * Method to verify that the status of the claim submitted by a profile is
	 * *correctly fetched for a configured channel when invalid MembershipNumber
	 * is passed in the request
	 *****/

	/*****
	 * * * PreRequisites Required: -->There should be a profile with a claim.
	 *
	 *****/
	public void fetchClaimsStatus_40143() {
		try {

			String testName = "fetchClaimsStatus_40143";
			WSClient.startTest(testName,
					"Verify that an error message is obtained when an invalid Membership Number is passed in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort = OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}", owsResort);
			String operaProfileID = "";
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			// ******** Setting the Opera and OWS Header *************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MClaimType" })) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);

				// ******** Prerequisite :Create Profile*************//

				WSClient.setData("{var_claimType}", OperaPropConfig.getDataSetForCode("MClaimType", "DS_01"));
				String membershipNumber = WSClient.getKeywordData("{KEYWORD_ID}");
				WSClient.setData("{var_membershipNumber}", membershipNumber);
				String owner = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_owner}", owner);

				// *********Prerequisites: Create Profile, Create Membership
				// Claim ***************//

				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID + "</b>");
					WSClient.setData("{var_E_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					// ***************Preparing CreateMembershipClaim Request
					// and fetching the ClaimID of the claim created for the
					// profile***********//

					String createMembershipClaimReq = WSClient.createSOAPMessage("CreateMembershipClaim", "DS_01");
					String createMembershipClaimRes = WSClient.processSOAPMessage(createMembershipClaimReq);
					if (WSAssert.assertIfElementExists(createMembershipClaimRes, "CreateMembershipClaimRS_Success",
							true)) {
						String claimID = WSClient.getElementValue(createMembershipClaimRes,
								"CreateMembershipClaimRS_ClaimNo_ID", XMLType.RESPONSE);
						WSClient.setData("{var_claimID}", claimID);
						WSClient.setData("{var_claimSource}", "WEB");
						WSClient.writeToReport(LogStatus.INFO, "<b>Claim ID :" + claimID + "</b>");

						// ************Preparing FetchClaimsStatus Request and
						// getting the response************//

						String fetchClaimsStatusReq = WSClient.createSOAPMessage("OWSFetchClaimsStatus", "DS_11");
						String fetchClaimsStatusRes = WSClient.processSOAPMessage(fetchClaimsStatusReq);
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Fetch Claims Status response is :" + message
									+ "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchClaimsStatusRes,
								"FetchClaimsStatusResponse_Result_resultStatusFlag", false)) {
							/***
							 * Checking for the existence of the
							 * ResultStatusFlag
							 **/

							WSAssert.assertIfElementValueEquals(fetchClaimsStatusRes,
									"FetchClaimsStatusResponse_Result_resultStatusFlag", "FAIL", false);

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
						WSClient.writeToReport(LogStatus.WARNING, "Unable to create a claim for the profile");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
}