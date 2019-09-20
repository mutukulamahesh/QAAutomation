package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.HashMap;
import java.util.LinkedHashMap;

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

public class InsertUpdateDocument extends WSSetUp {


	public void setOwsHeader() throws Exception{
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}


	@Test(groups = { "sanity", "OWS", "InsertUpdateDocument", "Name" })
	public void insertDocument_382693() {
		try {
			String testName = "insertUpdateDocument_382693";
			WSClient.startTest(testName,
					"Verify that the Document has been submitted for the requested profileID with mandatory fields",
					"sanity");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {

				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "IN121456";

					WSClient.setData("{var_docnum}", documentNo);
					WSClient.setData("{var_docType}",
							OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

					setOwsHeader();

					String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_01");
					String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

					if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
							"InsertUpdateDocumentResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						String req_fetchDocumentList = WSClient.createSOAPMessage("FetchProfile", "DS_01");
						String res_fetchDocumentList = WSClient.processSOAPMessage(req_fetchDocumentList);
						if (WSAssert.assertIfElementExists(res_fetchDocumentList, "FetchProfileRS_Success", true)) {
							if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
									"IdentificationInfo_Identification_IDType",
									OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "ID Type - " + "Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDType", XMLType.RESPONSE)
										+ ", Expected : "
										+ OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "ID Type - " + "Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDType", XMLType.RESPONSE)
										+ ", Expected : "
										+ OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
							}

							if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
									"IdentificationInfo_Identification_IDNumber", documentNo, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Document No. - " + "Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDNumber", XMLType.RESPONSE)
										+ ", Expected : " + documentNo);
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Document No. - " + "Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDNumber", XMLType.RESPONSE)
										+ ", Expected : " + documentNo);
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Fetch Profile Pre-requisite Not Working");
						}

					}

				}

			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void UpdateDocument_38279() {
		try {
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {
				String testName = "insertUpdateDocument_38279";
				WSClient.startTest(testName, "Verify that the Document has been updated for the requested profileID with mandatory fields", "minimumRegression");
				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
				String Channel = OWSLib.getChannel();
				String ChannelType = OWSLib.getChannelType(Channel);
				String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
				String username = OPERALib.getUserName();
				String pswd = OPERALib.getPassword();

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "IN121456";

					WSClient.setData("{var_docnum}", documentNo);

					// OWSLib.setOWSHeader(username, pswd, resortExtValue,
					// ChannelType, ChannelCarier);
					WSClient.setData("{var_docType}",
							OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

					OPERALib.setOperaHeader(OPERALib.getUserName());
					// Prerequisite : Updating a profile with document
					// details
					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success",
							true)) {

						WSClient.writeToReport(LogStatus.INFO,
								"Checking if the inactive date is updated for the old document ");
						documentNo = "IN121498";

						WSClient.setData("{var_docnum}", documentNo);
						setOwsHeader();
						String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_01");
						String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

						if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
								"InsertUpdateDocumentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							HashMap<String, String> db = new HashMap<String, String>();

							WSClient.writeToReport(LogStatus.INFO, "Query for fetching the old document id");
							db = WSClient.getDBRow(WSClient.getQuery("QS_08"));
							String document_id = db.get("DOCUMENT_ID");
							WSClient.setData("{var_document_id}", document_id);

							WSClient.writeToReport(LogStatus.INFO,
									"Query for fetching the inactive date for old document record");
							db = WSClient.getDBRow(WSClient.getQuery("QS_09"));
							String inactive_date = db.get("INACTIVE_DATE");
							String todayDate = WSClient.getDBRow(WSClient.getQuery("QS_11")).get("CURRDATE");

							WSClient.writeToReport(LogStatus.INFO,
									"<b>Checking if the inactive date is updated for the old document </b>");
							if (WSAssert.assertEquals(todayDate, inactive_date, true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"Expected Date " + todayDate + "Actual Date" + inactive_date);

								WSClient.writeToReport(LogStatus.PASS,
										"UpdateDocument Inactive-Date is  updated for old document record ");

							} else {

								WSClient.writeToReport(LogStatus.FAIL,
										"UpdateDocument Inactive-Date is not updated  ");
							}

							WSClient.writeToReport(LogStatus.INFO, "Query for fetching the new document record");
							db = WSClient.getDBRow(WSClient.getQuery("QS_10"));
							String id = db.get("ID_NUMBER");

							WSClient.writeToReport(LogStatus.INFO,
									"<b>Checking if the  updated   document-id is populated correctly.</b> ");
							if (WSAssert.assertEquals(documentNo.substring(documentNo.length() - 2),
									id.substring(id.length() - 2), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"<b>UpdateDocument id is  successful updated for the new record  </b>");
								WSClient.writeToReport(LogStatus.PASS, "<b>Expected value: " + documentNo + "</b>");
								WSClient.writeToReport(LogStatus.PASS,
										"<b>Actual value: {ID_NUMBER=" + id + "}</b>");

							} else {
								WSClient.writeToReport(LogStatus.INFO,
										"UpdateDocument id is not successfully placed  ");

							}
						}

					}
					else{
						WSClient.writeToReport(LogStatus.WARNING, "Change Profile Pre-requisite Not Working");
					}
				}


			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void insertDocument_382692() {
		try {
			String testName = "insertUpdateDocument_382692";
			WSClient.startTest(testName,
					"Verify that the Document has been submitted for the requested profileID with all fields",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {

				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "IN1211232";

					WSClient.setData("{var_docnum}", documentNo);
					WSClient.setData("{var_docType}",
							OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

					WSClient.setData("{var_countryOfIssue}", "India");
					WSClient.setData("{var_placeOfIssue}", "Hyderabad");
					WSClient.setData("{var_effectiveDate}", "2017-11-12");

					WSClient.setData("{var_primary}", "false");

					setOwsHeader();
					String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_03");
					String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

					if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
							"InsertUpdateDocumentResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						String req_fetchDocumentList = WSClient.createSOAPMessage("FetchProfile", "DS_01");
						String res_fetchDocumentList = WSClient.processSOAPMessage(req_fetchDocumentList);
						if (WSAssert.assertIfElementExists(res_fetchDocumentList, "FetchProfileRS_Success", true)) {
							if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
									"IdentificationInfo_Identification_IDType",
									OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "ID Type - "+"Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDType", XMLType.RESPONSE)
										+ ", Expected : "
										+ OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "ID Type - "+"Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDType", XMLType.RESPONSE)
										+ ", Expected : "
										+ OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
							}

							if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
									"IdentificationInfo_Identification_IDNumber", documentNo, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Document No. - "+"Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDNumber", XMLType.RESPONSE)
										+ ", Expected : " + documentNo);
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Document No. - "+"Actual : "
										+ WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IDNumber", XMLType.RESPONSE)
										+ ", Expected : " + documentNo);
							}




							if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
									"IdentificationInfo_Identification_IssuedCountry",
									WSClient.getData("{var_countryOfIssue}"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "Issued Country - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
										"IdentificationInfo_Identification_IssuedCountry", XMLType.RESPONSE)
								+ ", Expected : " + WSClient.getData("{var_countryOfIssue}"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Issued Country - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IssuedCountry", XMLType.RESPONSE)
										+ ", Expected : " + WSClient.getData("{var_countryOfIssue}"));
							}

							if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
									"IdentificationInfo_Identification_IssuedPlace",
									WSClient.getData("{var_placeOfIssue}"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "Place of Issue - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
										"IdentificationInfo_Identification_IssuedPlace", XMLType.RESPONSE)
								+ ", Expected : " + WSClient.getData("{var_placeOfIssue}"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Place Of Issue - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
												"IdentificationInfo_Identification_IssuedPlace", XMLType.RESPONSE)
										+ ", Expected : " + WSClient.getData("{var_placeOfIssue}"));
							}

							if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
									"IdentificationInfo_Identification_IssuedDate",
									WSClient.getData("{var_effectiveDate}"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "Id Date - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
										"IdentificationInfo_Identification_IssuedDate", XMLType.RESPONSE)
								+ ", Expected : " + WSClient.getData("{var_effectiveDate}"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Id Date - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
										"IdentificationInfo_Identification_IssuedDate", XMLType.RESPONSE)
								+ ", Expected : " + WSClient.getData("{var_effectiveDate}"));
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Fetch Profile Pre-requisite Not Working");
						}

					}

				}

			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}






	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void insertDocument_382691() {
		try {
			String testName = "insertUpdateDocument_382691";
			WSClient.startTest(testName,
					"Verify that the Document,primary field have been submitted for the requested profileID",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {

				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
							+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

					WSClient.setData("{var_docnum}", documentNo);

					String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_02");
					WSClient.setData("{var_docType}", docType);
					OPERALib.setOperaHeader(OPERALib.getUserName());
					// Prerequisite : Updating a profile with document
					// details
					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success",
							true)) {

						WSClient.setData("{var_profileId}", operaProfileID);
						documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
								+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

						WSClient.setData("{var_docnum}", documentNo);
						WSClient.setData("{var_docType}",
								OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

						WSClient.setData("{var_countryOfIssue}", "India");
						WSClient.setData("{var_placeOfIssue}", "Hyderabad");
						WSClient.setData("{var_effectiveDate}", "2017-11-12");

						WSClient.setData("{var_primary}", "true");

						setOwsHeader();

						String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_03");
						String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

						if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
								"InsertUpdateDocumentResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> docDetails = WSClient
									.getDBRow(WSClient.getQuery("QS_05"));

							WSClient.writeToReport(LogStatus.INFO, docDetails.toString());

							if (WSAssert.assertEquals(docDetails.get("ID_TYPE"), WSClient.getData("{var_docType}"),
									true)) {
								WSClient.writeToReport(LogStatus.PASS, "ID Type - "+"Actual : " + docDetails.get("ID_TYPE")
								+ ", Expected : " + WSClient.getData("{var_docType}"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "ID Type - "+"Actual : " + docDetails.get("ID_TYPE")
								+ ", Expected : " + WSClient.getData("{var_docType}"));
							}

							if (WSAssert.assertEquals(docDetails.get("PRIMARY"), WSClient.getData("{var_primary}"),
									true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"primary value field - "+"Actual : " + docDetails.get("PRIMARY")
										+ ", Expected : " + WSClient.getData("{var_primary}"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"primary value field - "+"Actual : " + docDetails.get("PRIMARY")
										+ ", Expected : " + WSClient.getData("{var_primary}"));
							}

						}

					}
					else{
						WSClient.writeToReport(LogStatus.WARNING, "Change Profile Pre-requisite Not Working");
					}
				}


			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void insertDocument_382690() {
		try {
			String testName = "insertUpdateDocument_382690";
			WSClient.startTest(testName,
					"Verify that the Error Message has been populated when an invalid profileID is given",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {

				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				LinkedHashMap<String, String> profileDetails = WSClient
						.getDBRow(WSClient.getQuery("OWSInsertUpdateDocument", "QS_07"));

				String operaProfileID = profileDetails.get("PROFILEID");
				WSClient.writeToReport(LogStatus.INFO, "Testing with Profile Id : " + operaProfileID);
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = "IN121456";

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);

				setOwsHeader();

				String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_01");
				String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);
				if (!WSAssert.assertIfElementExists(res_insertUpdate,
						"InsertUpdateDocumentResponse_Result_resultStatusFlag", true)) {
					WSClient.writeToReport(LogStatus.FAIL, "Fault Schema");
				}

				if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
						"InsertUpdateDocumentResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(res_insertUpdate, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b>" + WSClient.getElementValue(res_insertUpdate,
								"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");

					}

				}

			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void insertDocument_38269() {
		try {
			String testName = "insertUpdateDocument_38269";
			WSClient.startTest(testName,
					"Verify that the Document has been updated for the requested profileID with all fields",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {

				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "IN1211232";

					WSClient.setData("{var_docnum}", documentNo);
					WSClient.setData("{var_docType}",
							OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

					OPERALib.setOperaHeader(OPERALib.getUserName());
					// Prerequisite : Updating a profile with document
					// details
					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {

						WSClient.setData("{var_countryOfIssue}", "India");
						WSClient.setData("{var_placeOfIssue}", "Hyderabad");
						WSClient.setData("{var_effectiveDate}", "2017-11-12");

						WSClient.setData("{var_primary}", "false");

						setOwsHeader();

						String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_03");
						String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

						if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
								"InsertUpdateDocumentResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							String req_fetchDocumentList = WSClient.createSOAPMessage("FetchProfile", "DS_01");
							String res_fetchDocumentList = WSClient.processSOAPMessage(req_fetchDocumentList);
							if (WSAssert.assertIfElementExists(res_fetchDocumentList, "FetchProfileRS_Success",
									true)) {
								if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
										"IdentificationInfo_Identification_IDType",
										OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "ID Type - "+"Actual : "
											+ WSClient.getElementValue(res_fetchDocumentList,
													"IdentificationInfo_Identification_IDType", XMLType.RESPONSE)
											+ ", Expected : "
											+ OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "ID Type - "+"Actual : "
											+ WSClient.getElementValue(res_fetchDocumentList,
													"IdentificationInfo_Identification_IDType", XMLType.RESPONSE)
											+ ", Expected : "
											+ OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
								}

								if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
										"IdentificationInfo_Identification_IDNumber", documentNo, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Document No. - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
											"IdentificationInfo_Identification_IDNumber", XMLType.RESPONSE)
									+ ", Expected : " + documentNo);
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Document No. - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
													"IdentificationInfo_Identification_IDNumber", XMLType.RESPONSE)
											+ ", Expected : " + documentNo);
								}

								if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
										"IdentificationInfo_Identification_IssuedCountry",
										WSClient.getData("{var_countryOfIssue}"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Issued Country - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
													"IdentificationInfo_Identification_IssuedCountry", XMLType.RESPONSE)
											+ ", Expected : " + WSClient.getData("{var_countryOfIssue}"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Issued Country - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
													"IdentificationInfo_Identification_IssuedCountry", XMLType.RESPONSE)
											+ ", Expected : " + WSClient.getData("{var_countryOfIssue}"));
								}

								if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
										"IdentificationInfo_Identification_IssuedPlace",
										WSClient.getData("{var_placeOfIssue}"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Place of Issue - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
													"IdentificationInfo_Identification_IssuedPlace", XMLType.RESPONSE)
											+ ", Expected : " + WSClient.getData("{var_placeOfIssue}"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Place Of Issue - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
													"IdentificationInfo_Identification_IssuedPlace", XMLType.RESPONSE)
											+ ", Expected : " + WSClient.getData("{var_placeOfIssue}"));
								}

								if (WSAssert.assertIfElementValueEquals(res_fetchDocumentList,
										"IdentificationInfo_Identification_IssuedDate",
										WSClient.getData("{var_effectiveDate}"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Id Date - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
											"IdentificationInfo_Identification_IssuedDate", XMLType.RESPONSE)
									+ ", Expected : " + WSClient.getData("{var_effectiveDate}"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Id Date - "+"Actual : " + WSClient.getElementValue(res_fetchDocumentList,
											"IdentificationInfo_Identification_IssuedDate", XMLType.RESPONSE)
									+ ", Expected : " + WSClient.getData("{var_effectiveDate}"));
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Fetch Profile Pre-requisite Not Working");
							}

						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Change Profile Pre-requisite Not Working");
					}
				}
			}


		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void insertDocument_HHOCM48_1() 
	{
		try 
		{
			String testName = "insertUpdateDocument_HHOCM48_1";
			WSClient.startTest(testName, 
			"Verify that an error message is populated in response, without passing both document id and document type in the request",
			"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) 
			{
				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "IN121456";
	
					WSClient.setData("{var_docnum}", documentNo);
					WSClient.setData("{var_docType}", OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
	
					setOwsHeader();
					String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_04");
					String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);
	
					if (WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_resultStatusFlag", true))
					{
						if (WSAssert.assertIfElementValueEquals(res_insertUpdate, "InsertUpdateDocumentResponse_Result_resultStatusFlag", "FAIL", true)) 
						{
							if(WSAssert.assertIfElementExists(res_insertUpdate, "Result_Text_TextElement", false)) 
							{
								String errorCode=WSAssert.getElementValue(res_insertUpdate, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Error Code is: "+errorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_OperaErrorCode", true)) 
							{
								String operaErrorCode=WSAssert.getElementValue(res_insertUpdate, "InsertUpdateDocumentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Opera Error Code :"+ operaErrorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_GDSError", true)) 
							{
								String message=WSAssert.getElementValue(res_insertUpdate, "InsertUpdateDocumentResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the FetchNameUDF response is :"+ message);
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "Fault_faultstring", true)) 
							{
								String FaultErrorCode=WSAssert.getElementValue(res_insertUpdate, "Fault_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
							}
						}
						else 
						{
							WSClient.writeToReport(LogStatus.FAIL,"Inncorrect resultStatusFlag Populated in the Response!");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL,"ResultStatusFlag missing from the Response!");
					}
				}
			}
		} 
		catch (Exception e) 
		{
			WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}
	
	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void insertDocument_HHOCM48_2() 
	{
		try 
		{
			String testName = "insertUpdateDocument_HHOCM48_2";
			WSClient.startTest(testName, 
			"Verify that an error message is populated when trying to insert document type, without passing document id",
			"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) 
			{
				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "IN121456";
	
					WSClient.setData("{var_docnum}", documentNo);
					WSClient.setData("{var_docType}", OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
	
					setOwsHeader();
					String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_05");
					String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);
	
					if (WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_resultStatusFlag", true))
					{
						if (WSAssert.assertIfElementValueEquals(res_insertUpdate, "InsertUpdateDocumentResponse_Result_resultStatusFlag", "FAIL", true)) 
						{
							if(WSAssert.assertIfElementExists(res_insertUpdate, "Result_Text_TextElement", false)) 
							{
								String errorCode=WSAssert.getElementValue(res_insertUpdate, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Error Code is: "+errorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_OperaErrorCode", true)) 
							{
								String operaErrorCode=WSAssert.getElementValue(res_insertUpdate, "InsertUpdateDocumentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Opera Error Code :"+ operaErrorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_GDSError", true)) 
							{
								String message=WSAssert.getElementValue(res_insertUpdate, "InsertUpdateDocumentResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the FetchNameUDF response is :"+ message);
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "Fault_faultstring", true)) 
							{
								String FaultErrorCode=WSAssert.getElementValue(res_insertUpdate, "Fault_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
							}
						}
						else 
						{
							WSClient.writeToReport(LogStatus.FAIL,"Inncorrect resultStatusFlag Populated in the Response!");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL,"ResultStatusFlag missing from the Response!");
					}
				}
			}
		} 
		catch (Exception e) 
		{
			WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void insertDocument_HHOCM48_3() 
	{
		try 
		{
			String testName = "insertUpdateDocument_HHOCM48_3";
			WSClient.startTest(testName, 
			"Verify that an error message is populated when trying to insert document id, without passing document type",
			"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) 
			{
				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", operaProfileID);
					String documentNo = "IN121456";
	
					WSClient.setData("{var_docnum}", documentNo);
					WSClient.setData("{var_docType}", OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
	
					setOwsHeader();
					String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_06");
					String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);
	
					if (WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_resultStatusFlag", true))
					{
						if (WSAssert.assertIfElementValueEquals(res_insertUpdate, "InsertUpdateDocumentResponse_Result_resultStatusFlag", "FAIL", true)) 
						{
							if(WSAssert.assertIfElementExists(res_insertUpdate, "Result_Text_TextElement", false)) 
							{
								String errorCode=WSAssert.getElementValue(res_insertUpdate, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Error Code is: "+errorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_OperaErrorCode", true)) 
							{
								String operaErrorCode=WSAssert.getElementValue(res_insertUpdate, "InsertUpdateDocumentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Opera Error Code :"+ operaErrorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "InsertUpdateDocumentResponse_Result_GDSError", true)) 
							{
								String message=WSAssert.getElementValue(res_insertUpdate, "InsertUpdateDocumentResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the FetchNameUDF response is :"+ message);
							}
							if(WSAssert.assertIfElementExists(res_insertUpdate, "Fault_faultstring", true)) 
							{
								String FaultErrorCode=WSAssert.getElementValue(res_insertUpdate, "Fault_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
							}
						}
						else 
						{
							WSClient.writeToReport(LogStatus.FAIL,"Inncorrect resultStatusFlag Populated in the Response!");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL,"ResultStatusFlag missing from the Response!");
					}
				}
			}
		} 
		catch (Exception e) 
		{
			WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}
	
	@Test(groups = { "minimumRegression", "OWS", "InsertUpdateDocument", "Name", "in-QA" })
	public void UpdateDocument_HHOCM_103() {
		try {
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType" })) {
				String testName = "UpdateDocument_HHOCM_103";
				WSClient.startTest(testName, "Verify that the Driving number has been updated for the requested profileID with mandatory fields", "minimumRegression");
				String interfaceName = OWSLib.getChannel();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
				String Channel = OWSLib.getChannel();
				String ChannelType = OWSLib.getChannelType(Channel);
				String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
				String username = OPERALib.getUserName();
				String pswd = OPERALib.getPassword();

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
						WSClient.setData("{var_profileId}", operaProfileID);
						String documentNo = "IN121456";

						WSClient.setData("{var_docnum}", documentNo);

						// OWSLib.setOWSHeader(username, pswd, resortExtValue,
						// ChannelType, ChannelCarier);
						WSClient.setData("{var_docType}",
								OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

						OPERALib.setOperaHeader(OPERALib.getUserName());
						// Prerequisite : Updating a profile with document
						// details
						String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_ChangeProfile_24");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success",
								true)) {

							WSClient.writeToReport(LogStatus.INFO,
									"Checking if the inactive date is updated for the old document ");
							documentNo = "IN121498";

							WSClient.setData("{var_docnum}", documentNo);
							setOwsHeader();
							String req_insertUpdate = WSClient.createSOAPMessage("OWSInsertUpdateDocument", "DS_InsertUpdateDocument_07");
							String res_insertUpdate = WSClient.processSOAPMessage(req_insertUpdate);

							if (WSAssert.assertIfElementValueEquals(res_insertUpdate,
									"InsertUpdateDocumentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> db = new HashMap<String, String>();

								WSClient.writeToReport(LogStatus.INFO, "Query for fetching the old document id");
								db = WSClient.getDBRow(WSClient.getQuery("QS_08"));
								String document_id = db.get("DOCUMENT_ID");
								WSClient.setData("{var_document_id}", document_id);

								WSClient.writeToReport(LogStatus.INFO,
										"Query for fetching the inactive date for old document record");
								db = WSClient.getDBRow(WSClient.getQuery("QS_09"));
								String inactive_date = db.get("INACTIVE_DATE");
								String todayDate = WSClient.getDBRow(WSClient.getQuery("QS_11")).get("CURRDATE");

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Checking if the inactive date is updated for the old document </b>");
								if (WSAssert.assertEquals(todayDate, inactive_date, true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"Expected Date " + todayDate + "Actual Date" + inactive_date);

									WSClient.writeToReport(LogStatus.PASS,
											"UpdateDocument Inactive-Date is  updated for old document record ");

								} else {

									WSClient.writeToReport(LogStatus.FAIL,
											"UpdateDocument Inactive-Date is not updated  ");
								}

								WSClient.writeToReport(LogStatus.INFO, "Query for fetching the new document record");
								db = WSClient.getDBRow(WSClient.getQuery("QS_10"));
								String id = db.get("ID_NUMBER");

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Checking if the  updated   document-id is populated correctly.</b> ");
								if (WSAssert.assertEquals(documentNo.substring(documentNo.length() - 2),
										id.substring(id.length() - 2), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b>UpdateDocument id is  successful updated for the new record  </b>");
									WSClient.writeToReport(LogStatus.PASS, "<b>Expected value: " + documentNo + "</b>");
									WSClient.writeToReport(LogStatus.PASS,
											"<b>Actual value: {ID_NUMBER=" + id + "}</b>");

								} else {
									WSClient.writeToReport(LogStatus.INFO,
											"UpdateDocument id is not successfully placed  ");

								}
							}

						}
						else{
							WSClient.writeToReport(LogStatus.WARNING, "Change Profile Pre-requisite Not Working");
						}
					}
				

			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}
}