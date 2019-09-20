package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class UpdatePassport extends WSSetUp {

	@Test(groups = { "sanity", "UpdatePassport", "OWS", "Name" })

	/**
	 * Method to check if the OWS UpdatePassport is working i.e., updating
	 * passport details such as Passport Number for a given profile.
	 *
	 * PreRequisites Required: -->Profile is created -->Passport is attached
	 *
	 */
	public void updatePassport_27781() {
		try {
			String testName = "updatePassport_27781";
			WSClient.startTest(testName, "Verify that passport is updated for a given profile", "sanity");

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
					String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
							+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

					WSClient.setData("{var_docnum}", documentNo);

					WSClient.setData("{var_docType}", "PASSPORT");
					OPERALib.setOperaHeader(OPERALib.getUserName());
					// *******Prerequisite : Updating a profile with a Passport
					// as a document *****

					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						// Updating the document number for the corresponding
						// document
						String documentNo2 = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
								+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

						WSClient.setData("{var_docnum}", documentNo2);

						String fetchDocumentsReq = WSClient.createSOAPMessage("OWSUpdatePassport", "DS_01");
						String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

						if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
								"UpdatePassportResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(fetchDocumentsRes, "UpdatePassportResponse_Result_Text",
									true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Fetch Response gives the error message-----></b> "
												+ WSClient.getElementValue(fetchDocumentsRes,
														"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> documentDetails = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Retrieving the Identification Type for Passport</b>");
							String query1 = WSClient.getQuery("QS_03");
							LinkedHashMap<String, String> documentDetails1 = WSClient.getDBRow(query1);
							String elem2 = WSClient.getElementValue(fetchDocumentsReq,
									"UpdatePassportRequest_Passport_documentNumber", XMLType.REQUEST);
							String elem = elem2.substring(elem2.length() - 2);
							if (WSAssert.assertEquals(documentDetails.get("ID_NUMBER"), elem, true)) {
								WSClient.writeToReport(LogStatus.PASS, "ID Number-> Expected value:XXXXX" + elem
										+ " Actual value :XXXXX" + documentDetails.get("ID_NUMBER"));
							} else
								WSClient.writeToReport(LogStatus.FAIL, "ID Number-> Expected value:" + elem
										+ " Actual value :" + documentDetails.get("ID_NUMBER") + " are different");

							if (WSAssert.assertEquals(documentDetails.get("ID_TYPE"),
									documentDetails1.get("DOCUMENT_TYPE"), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"ID Type-> Expected value: " + documentDetails1.get("DOCUMENT_TYPE")
										+ "Actual value : " + documentDetails.get("ID_TYPE"));
							} else if (WSAssert.assertEquals(documentDetails.get("ID_TYPE"), "S-PASSPORT", true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"ID Type-> Expected value: S-PASSPORT Actual value : "
												+ documentDetails.get("ID_TYPE"));
							}

						} else

							WSClient.writeToReport(LogStatus.WARNING, "Fetch Document fails");

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Update Profile Prerequisite not working");
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePassport", "OWS", "Name" })
	public void updatePassport_38608() {
		try {
			String testName = "updatePassport_38608";
			WSClient.startTest(testName, "Verify update passport when an invalid profile ID is given",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {

				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				String query = WSClient.getQuery("OWSUpdatePassport", "QS_02");
				LinkedHashMap<String, String> profileDetails = WSClient.getDBRow(query);

				String operaProfileID = profileDetails.get("PROFILEID");
				WSClient.writeToReport(LogStatus.INFO, "Testing with Profile Id : " + operaProfileID);
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = "IN121456";

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "*********Validating when invalid profile ID is given*********</b>");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));

				String req_insertUpdate = WSClient.createSOAPMessage("OWSUpdatePassport", "DS_01");
				String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

				if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
						"UpdatePassportResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(res_insertUpdate, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b>" + WSClient.getElementValue(res_insertUpdate,
								"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");

					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePassport", "OWS", "Name", "in-QA" })
	public void updatePassport_38690() {
		try {
			String testName = "updatePassport_38690";
			WSClient.startTest(testName, "Verify the operation for missing profile ID", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {
				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());
				/******
				 * Prerequisite : Creating a Profile with basic details
				 *****/
				String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
				String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

				System.out.println("Profile Creation is successful 1");
				if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success",
						true) == false) {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
				} else {
					if (WSAssert.assertIfElementExists(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {

						WSClient.setData("{var_profileId}", "");
						String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
								+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

						WSClient.setData("{var_docnum}", documentNo);

						WSClient.setData("{var_docType}", "");

						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "*********Validating when no profile ID is given*********</b>");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						String req_insertUpdate = WSClient.createSOAPMessage("OWSUpdatePassport", "DS_03");
						String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

						if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
								"UpdatePassportResponse_Result_resultStatusFlag", "FAIL", false)) {
							if (WSAssert.assertIfElementExists(res_insertUpdate, "Result_Text_TextElement", true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"<b>" + WSClient.getElementValue(res_insertUpdate, "Result_Text_TextElement",
												XMLType.RESPONSE) + "</b>");

							}

						}

					}

				}
			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePassport", "OWS", "Name", "in-QA" })

	/**
	 * Method to check if the OWS UpdatePassport is working i.e., updating
	 * passport details such as Passport Number for a given profile.
	 *
	 * PreRequisites Required: -->Profile is created -->Passport is attached
	 *
	 */
	public void updatePassport_27791() {
		try {
			String testName = "updatePassport_27791";
			WSClient.startTest(testName, "Insert a passport number for a given profile", "minimumRegression");

			;
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
					String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
							+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

					WSClient.setData("{var_docnum}", documentNo);

					WSClient.setData("{var_docType}", "PASSPORT");
					OPERALib.setOperaHeader(OPERALib.getUserName());
					// *******Prerequisite : Updating a profile with a Passport
					// as a document *****

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));

					// Updating the document number for the corresponding
					// document
					String documentNo2 = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
							+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

					WSClient.setData("{var_docnum}", documentNo2);

					String fetchDocumentsReq = WSClient.createSOAPMessage("OWSUpdatePassport", "DS_01");
					String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

					if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
							"UpdatePassportResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchDocumentsRes, "UpdatePassportResponse_Result_Text",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetch Response gives the error message-----></b> "
											+ WSClient.getElementValue(fetchDocumentsRes,
													"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data
						String query = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> documentDetails = WSClient.getDBRow(query);
						String query1 = WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> documentDetails1 = WSClient.getDBRow(query1);
						String elem2 = WSClient.getElementValue(fetchDocumentsReq,
								"UpdatePassportRequest_Passport_documentNumber", XMLType.REQUEST);
						String elem = elem2.substring(elem2.length() - 2);
						if (WSAssert.assertEquals(documentDetails.get("ID_NUMBER"), elem, true)) {
							WSClient.writeToReport(LogStatus.PASS, "ID Number Expected value:" + elem
									+ " Actual value :" + documentDetails.get("ID_NUMBER"));
						} else
							WSClient.writeToReport(LogStatus.FAIL, "ID Number Expected value:" + elem
									+ " Actual value :" + documentDetails.get("ID_NUMBER") + " are different");
						if (WSAssert.assertEquals(documentDetails.get("ID_TYPE"), documentDetails1.get("DOCUMENT_TYPE"),
								true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"ID Type Expected value: " + documentDetails1.get("DOCUMENT_TYPE")
									+ "Actual value : " + documentDetails.get("ID_TYPE"));
						} else if (WSAssert.assertEquals(documentDetails.get("ID_TYPE"), "S-PASSPORT", true)) {
							WSClient.writeToReport(LogStatus.PASS, "ID Type Expected value: S-PASSPORT Actual value : "
									+ documentDetails.get("ID_TYPE"));
						}

						else
							WSClient.writeToReport(LogStatus.FAIL,
									"ID Type Expected value: PASSPORT Actual value :" + documentDetails.get("ID_TYPE"));

					} else

						WSClient.writeToReport(LogStatus.ERROR, "Fetch Document fails");

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Update Profile Prerequisite not working");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception is " + e);

		}
	}

	public void updatePassport_27792() {
		try {
			String testName = "updatePassport_27792";
			WSClient.startTest(testName, "Invalid document type", "minumumRegression");

			;
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
					String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
							+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

					WSClient.setData("{var_docnum}", documentNo);

					WSClient.setData("{var_docType}", "INVALIDPASSPORT");
					OPERALib.setOperaHeader(OPERALib.getUserName());
					// *******Prerequisite : Updating a profile with a Passport
					// as a document *****

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));

					// Updating the document number for the corresponding
					// document
					String documentNo2 = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
							+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

					WSClient.setData("{var_docnum}", documentNo2);

					String fetchDocumentsReq = WSClient.createSOAPMessage("OWSUpdatePassport", "DS_01");
					String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

					if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
							"UpdatePassportResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchDocumentsRes, "UpdatePassportResponse_Result_Text",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetch Response gives the error message-----></b> "
											+ WSClient.getElementValue(fetchDocumentsRes,
													"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data
						String query = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> documentDetails = WSClient.getDBRow(query);
						String query1 = WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> documentDetails1 = WSClient.getDBRow(query1);
						String elem2 = WSClient.getElementValue(fetchDocumentsReq,
								"UpdatePassportRequest_Passport_documentNumber", XMLType.REQUEST);
						String elem = elem2.substring(elem2.length() - 2);
						if (WSAssert.assertEquals(documentDetails.get("ID_NUMBER"), elem, true)) {
							WSClient.writeToReport(LogStatus.PASS, "ID Number Expected value:" + elem
									+ " Actual value :" + documentDetails.get("ID_NUMBER"));
						} else
							WSClient.writeToReport(LogStatus.FAIL, "ID Number Expected value:" + elem
									+ " Actual value :" + documentDetails.get("ID_NUMBER") + " are different");
						if (WSAssert.assertEquals(documentDetails.get("ID_TYPE"), documentDetails1.get("DOCUMENT_TYPE"),
								true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"ID Type Expected value: " + documentDetails1.get("DOCUMENT_TYPE")
									+ "Actual value : " + documentDetails.get("ID_TYPE"));
						} else if (WSAssert.assertEquals(documentDetails.get("ID_TYPE"), "S-PASSPORT", true)) {
							WSClient.writeToReport(LogStatus.PASS, "ID Type Expected value: S-PASSPORT Actual value : "
									+ documentDetails.get("ID_TYPE"));
						}

						else
							WSClient.writeToReport(LogStatus.FAIL,
									"ID Type Expected value: PASSPORT Actual value :" + documentDetails.get("ID_TYPE"));

					} else

						WSClient.writeToReport(LogStatus.ERROR, "Fetch Document fails");

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Update Profile Prerequisite not working");
				}

			}
		} catch (Exception e) {
		}
	}

}
