package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class GetPassport extends WSSetUp {

	@Test(groups = { "sanity", "GetPassport", "OWS", "Name" })

	/**
	 * Method to check if the OWS GetPassport is working i.e., fetching passport
	 * details such as Passport Number for a given profile.
	 *
	 *
	 * PreRequisites Required: -->Profile is created -->Passport is attached
	 *
	 */
	public void getPassport_26304() {
		try {
			String testName = "getPassport_38395";
			WSClient.startTest(testName, "Verify that Get Passport retrieves the passport details for a profile",
					"sanity");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			System.out.println("Profile Creation is successful 1");
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {

					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
							+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

					WSClient.setData("{var_docnum}", documentNo);

					String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
					WSClient.setData("{var_docType}", "PASSPORT");
					OPERALib.setOperaHeader(OPERALib.getUserName());
					// Prerequisite : Updating a profile with document details
					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", false)) {

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						// fetching the document list for the corresponding
						// profile
						String fetchDocumentsReq = WSClient.createSOAPMessage("OWSGetPassport", "DS_01");
						String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

						if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
								"GetPassportResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(fetchDocumentsRes, "GetPassportResponse_Result_Text",
									true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Fetch Response gives the error message-----></b> "
												+ WSClient.getElementValue(fetchDocumentsRes,
														"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
							}
							if (WSAssert.assertIfElementExists(fetchDocumentsRes,
									"GetPassportResponse_Passport_documentNumber", false)) {
								// validating the data
								String query = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> documentDetails = WSClient.getDBRow(query);
								String elem = WSClient
										.getElementValue(fetchDocumentsRes,
												"GetPassportResponse_Passport_documentNumber", XMLType.RESPONSE)
										.substring(6, 8);

								if (WSAssert.assertEquals(documentDetails.get("ID_NUMBER"), elem, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Expected value:XXXXX" + elem
											+ " Actual value :XXXXX" + documentDetails.get("ID_NUMBER"));
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:XXXXX" + elem + " Actual value :XXXXX"
													+ documentDetails.get("ID_NUMBER") + " are different");

								WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
										"GetPassportResponse_Passport_documentType", "Passport", false);
							} else

								WSClient.writeToReport(LogStatus.ERROR, "Get Passport fails");

						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);
		}
	}

	@Test(groups = { "minimumRegression", "GetPassport", "OWS", "Name" })
	/***** Fetching the document list associated to a profile *****/
	/***** Validating the documents type and document number fields *****/
	public void getPassport_38607() {
		try {
			String testName = "getPassport_38604";
			WSClient.startTest(testName,
					"Verify that GetPassport retrieves the most recent passport (ACTIVE passport) of a profile with multiple records",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			/****** Prerequisite : Creating a Profile with basic details *****/
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			System.out.println("Profile Creation is successful 1");
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {

					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "XXXXXXX32";

					String documentNo2 = "XXXXXXX89";

					WSClient.setData("{var_docnum}", documentNo);
					WSClient.setData("{var_docNum2}", documentNo2);
					WSClient.setData("{var_primary}", "true");
					WSClient.setData("{var_primary2}", "true");

					String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
					WSClient.setData("{var_docType}", "PASSPORT");

					WSClient.setData("{var_docType2}", "PASSPORT");

					OPERALib.setOperaHeader(OPERALib.getUserName());
					// Prerequisite : Updating a profile with document details
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Adding multiple records of passport for the profile</b> ");

					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_05");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", false)) {

						documentNo = "XXXXXXX89";

						WSClient.setData("{var_docnum}", documentNo);

						WSClient.setData("{var_primary}", "true");
						WSClient.setData("{var_primary2}", "true");

						docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
						WSClient.setData("{var_docType}", "PASSPORT");

						WSClient.setData("{var_docType2}", "PASSPORT");

						OPERALib.setOperaHeader(OPERALib.getUserName());
						// Prerequisite : Updating a profile with document
						// details
						updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_05");
						updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success",
								false)) {

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							// fetching the document list for the corresponding
							// profile
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Most recent passport is fetched for the profile</b> ");

							String fetchDocumentsReq = WSClient.createSOAPMessage("OWSGetPassport", "DS_01");
							String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

							if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"GetPassportResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(fetchDocumentsRes, "Result_Text_TextElement",
										true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Get Passport Response gives the error message-----></b> "
													+ WSClient.getElementValue(fetchDocumentsRes,
															"Result_Text_TextElement", XMLType.RESPONSE));
								}
								if (WSAssert.assertIfElementExists(fetchDocumentsRes, "GetPassportResponse_Passport",
										false)) {

									HashMap<String, String> xPath = new HashMap<String, String>();
									xPath.put("GetPassportResponse_Passport_documentNumber",
											"GetPassportResponse_Passport");
									xPath.put("GetPassportResponse_Passport_documentType",
											"GetPassportResponse_Passport");
									List<LinkedHashMap<String, String>> respData = WSClient
											.getMultipleNodeList(fetchDocumentsRes, xPath, false, XMLType.RESPONSE);
									String query = WSClient.getQuery("QS_02");
									ArrayList<LinkedHashMap<String, String>> dbData = WSClient.getDBRows(query);
									if (WSAssert.assertEquals(respData, dbData, false)) {
										WSClient.writeToReport(LogStatus.PASS, "Most recent passport is retrieved");
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Inactive passports are retrieved for the profile");

								}

							} else {
								WSClient.writeToReport(LogStatus.ERROR, "Get Passport fails");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Create Profile Prerequisite not working");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}
	}

	@Test(groups = { "minimumRegression", "GetPassport", "OWS", "Name" })
	/***** Fetching the document list associated to a profile *****/
	/***** Validating the documents type and document number fields *****/
	public void getPassport_38606() {
		try {
			String testName = "getPassport_38606";
			WSClient.startTest(testName, "Verify that all the fields are validated on the Get passport response",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			/****** Prerequisite : Creating a Profile with basic details *****/
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			System.out.println("Profile Creation is successful 1");
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {

					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "XXXXXXX32";
					WSClient.setData("{var_docnum}", documentNo);

					String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_02");
					WSClient.setData("{var_docType}", "PASSPORT");

					WSClient.setData("{var_countryOfIssue}", "India");
					WSClient.setData("{var_placeOfIssue}", "Hyderabad");
					WSClient.setData("{var_effectiveDate}", "2017-11-12");

					OPERALib.setOperaHeader(OPERALib.getUserName());
					// Prerequisite : Updating a profile with document details
					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_04");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", false)) {

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						// fetching the document list for the corresponding
						// profile

						WSClient.writeToReport(LogStatus.INFO,
								"<b>Validating all the optional and mandatory fields</b> ");

						String fetchDocumentsReq = WSClient.createSOAPMessage("OWSGetPassport", "DS_01");
						String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

						if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
								"GetPassportResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(fetchDocumentsRes, "Result_Text_TextElement", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Get Passport gives the error message-----></b> " + WSClient.getElementValue(
												fetchDocumentsRes, "Result_Text_TextElement", XMLType.RESPONSE));
							}
							if (WSAssert.assertIfElementExists(fetchDocumentsRes, "GetPassportResponse_Passport",
									false)) {
								// validating the data
								String query = WSClient.getQuery("QS_03");
								LinkedHashMap<String, String> documentDetails = WSClient.getDBRow(query);
								WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
										"GetPassportResponse_Passport_documentNumber", documentDetails.get("ID_NUMBER"),
										false);
								WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
										"GetPassportResponse_Passport_documentType", "Passport", false);
								WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
										"GetPassportResponse_Passport_effectiveDate", documentDetails.get("ID_DATE"),
										false);
								WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
										"GetPassportResponse_Passport_placeOfIssue", documentDetails.get("ID_PLACE"),
										false);
								WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
										"GetPassportResponse_Passport_countryOfIssue",
										documentDetails.get("ID_COUNTRY"), false);
							}
						} else {
							WSClient.writeToReport(LogStatus.ERROR, "Get Passport fails");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}

	}

	@Test(groups = { "minimumRegression", "GetPassport", "OWS", "Name" })
	/***** Fetching the document list associated to a profile *****/
	/***** Validating the documents type and document number fields *****/
	public void getPassport_386071() {
		try {
			String testName = "getPassport_38608";
			WSClient.startTest(testName, "Verify that GetPassport fails with an invalid profile", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			/****** Prerequisite : Creating a Profile with basic details *****/
			WSClient.writeToReport(LogStatus.INFO, "<b>Assigning an invalid profile</b>");
			LinkedHashMap<String, String> profileDetails = WSClient
					.getDBRow(WSClient.getQuery("OWSInsertUpdateDocument", "QS_07"));

			String operaProfileID = profileDetails.get("PROFILEID");
			WSClient.writeToReport(LogStatus.INFO, "<b>Testing with Profile Id : " + operaProfileID + "</b>");
			WSClient.setData("{var_profileId}", operaProfileID);
			String documentNo = "IN121456";

			WSClient.setData("{var_docnum}", documentNo);

			String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
			WSClient.setData("{var_docType}", docType);

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));

			// fetching the document list for the corresponding
			// profile
			String fetchDocumentsReq = WSClient.createSOAPMessage("OWSGetPassport", "DS_01");
			String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

			if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes, "GetPassportResponse_Result_resultStatusFlag",
					"FAIL", false)) {
				if (WSAssert.assertIfElementExists(fetchDocumentsRes, "Result_Text_TextElement", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Get Passport Response gives the error message-----> "
							+ WSClient.getElementValue(fetchDocumentsRes, "Result_Text_TextElement", XMLType.RESPONSE)
							+ "</b>");
				}
			} else
				WSClient.writeToReport(LogStatus.FAIL,
						"<b>Get Passport Response does not give error message when invalid Profile ID is given</b>");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}
	}

	@Test(groups = { "minimumRegression", "GetPassport", "OWS", "Name" })
	/***** Fetching the document list associated to a profile *****/
	/***** Validating the documents type and document number fields *****/
	public void getPassport_3860711() {
		try {
			String testName = "getPassport_38609";
			WSClient.startTest(testName, "Verify that GetPassport fails with a missing profile", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			/****** Prerequisite : Creating a Profile with basic details *****/
			WSClient.writeToReport(LogStatus.INFO, "<b>Populating no profile ID in the request</b>");
			WSClient.setData("{var_profileId}", "");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			// fetching the document list for the corresponding
			// profile
			String fetchDocumentsReq = WSClient.createSOAPMessage("OWSGetPassport", "DS_01");
			String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

			if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes, "GetPassportResponse_Result_resultStatusFlag",
					"FAIL", false)) {
				if (WSAssert.assertIfElementExists(fetchDocumentsRes, "Result_Text_TextElement", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Get Passport Response gives the error message-----> "
							+ WSClient.getElementValue(fetchDocumentsRes, "Result_Text_TextElement", XMLType.RESPONSE)
							+ "</b>");
				}
			} else
				WSClient.writeToReport(LogStatus.FAIL,
						"<b>Get Passport Response does not give error message when invalid Profile ID is given</b>");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}

	}

}
