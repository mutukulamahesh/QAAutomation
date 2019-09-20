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

public class FetchDocumentList extends WSSetUp {



	public void setOwsHeader() throws Exception{
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}




	@Test(groups = { "sanity", "FetchDocumentList", "OWS", "Name", "fetchDocumentList_38288"})

	public void fetchDocument_38288() {
		try {
			String testName = "fetchDocumentList_38288";
			WSClient.startTest(testName,
					"Verify that the minimum information about the document (Type, Document Number) are retrieved onto the response",
					"sanity");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
						+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_02");
				WSClient.setData("{var_docType}", docType);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {

					//Now fetching the Details of document from DB
					String quFive = WSClient.getQuery("OWSFetchDocumentList", "QS_05");
					LinkedHashMap<String, String> dbMap = WSClient.getDBRow(quFive);
					WSClient.writeToReport(LogStatus.INFO, "<b>ID TYPE: " + dbMap.get("ID_TYPE")+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>ID NUMBER: " + dbMap.get("ID_NUMBER")+"</b>");

					setOwsHeader();
					// fetching the document list for the corresponding
					// profile
					String fetchDocumentsReq = WSClient.createSOAPMessage("OWSFetchDocumentList", "DS_01");
					String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

					if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
							"FetchDocumentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchDocumentsRes, "FetchDocumentListResponse_Result_Text",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetch Response gives the error message-----></b> "
											+ WSClient.getElementValue(fetchDocumentsRes,
													"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
						}
						if (WSAssert.assertIfElementExists(fetchDocumentsRes,
								"FetchDocumentListResponse_DocumentList_GovernmentID", false)) {
							// validating the data
							LinkedHashMap<String, String> documentDetails = WSClient
									.getDBRow(WSClient.getQuery("QS_01"));
							WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"FetchDocumentListResponse_DocumentList_GovernmentID_documentNumber",
									documentDetails.get("ID_NUMBER"), false);
							WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"FetchDocumentListResponse_DocumentList_GovernmentID_documentType",
									documentDetails.get("ID_TYPE"), false);
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
				}

			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchDocumentList", "OWS", "Name", "in-QA" })
	public void fetchDocument_42366() {
		try {
			String testName = "fetchDocumentList_42366";
			WSClient.startTest(testName,
					"Verify that the all information about the document (Type, Document Number,Effective Date,Place Of Issue,Country of Issue) are retrieved onto the response",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = "IN1211232";
				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_02");
				WSClient.setData("{var_docType}", docType);

				WSClient.setData("{var_countryOfIssue}", "India");
				WSClient.setData("{var_placeOfIssue}", "Hyderabad");
				WSClient.setData("{var_effectiveDate}", "2017-11-12");

				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_04");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {

					//Now fetching the Details of document from DB
					String quSix = WSClient.getQuery("OWSFetchDocumentList", "QS_06");
					LinkedHashMap<String, String> dbMap = WSClient.getDBRow(quSix);
					WSClient.writeToReport(LogStatus.INFO, "<b>ID TYPE: " + dbMap.get("ID_TYPE")+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>ID NUMBER: " + dbMap.get("ID_NUMBER")+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>ID DATE: " + dbMap.get("ID_DATE")+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>ID PLACE: " + dbMap.get("ID_PLACE")+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>ID COUNTRY: " + dbMap.get("ID_COUNTRY")+"</b>");

					setOwsHeader();
					// fetching the document list for the corresponding
					// profile
					String fetchDocumentsReq = WSClient.createSOAPMessage("OWSFetchDocumentList", "DS_01");
					String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

					if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
							"FetchDocumentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchDocumentsRes, "FetchDocumentListResponse_Result_Text",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetch Response gives the error message-----></b> "
											+ WSClient.getElementValue(fetchDocumentsRes,
													"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
						}
						if (WSAssert.assertIfElementExists(fetchDocumentsRes,
								"FetchDocumentListResponse_DocumentList_GovernmentID", false)) {
							// validating the data
							LinkedHashMap<String, String> documentDetails = WSClient
									.getDBRow(WSClient.getQuery("QS_02"));
							WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"FetchDocumentListResponse_DocumentList_GovernmentID_documentNumber",
									documentDetails.get("ID_NUMBER"), false);
							WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"FetchDocumentListResponse_DocumentList_GovernmentID_documentType",
									documentDetails.get("ID_TYPE"), false);
							WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"FetchDocumentListResponse_DocumentList_GovernmentID_effectiveDate",
									documentDetails.get("ID_DATE"), false);
							WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"FetchDocumentListResponse_DocumentList_GovernmentID_placeOfIssue",
									documentDetails.get("ID_PLACE"), false);
							WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
									"FetchDocumentListResponse_DocumentList_GovernmentID_countryOfIssue",
									documentDetails.get("ID_COUNTRY"), false);
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
				}
			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchDocumentList", "OWS", "Name", "in-QA" })
	public void fetchDocument_38310() {
		try {
			String testName = "fetchDocumentList_38310";
			WSClient.startTest(testName, "Verify Multiple Documents are populated correctly on the response",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
						+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_02");
				WSClient.setData("{var_docType}", docType);
				//	String documentNo = "IN1211232";

				String documentNo2 = "IN1211289";

				//	WSClient.setData("{var_docnum}", documentNo);
				WSClient.setData("{var_docNum2}", documentNo2);
				WSClient.setData("{var_primary}", "true");
				WSClient.setData("{var_primary2}", "true");

				//	String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				//	WSClient.setData("{var_docType}", docType);

				WSClient.setData("{var_docType2}", OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_05");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {

					//Now fetching the Details of document from DB
					String quFive = WSClient.getQuery("OWSFetchDocumentList", "QS_05");
					List<LinkedHashMap<String, String>> dbList = WSClient.getDBRows(quFive);
					WSClient.writeToReport(LogStatus.INFO, "<b>First Document-	ID TYPE: " + dbList.get(0).get("ID_TYPE")+" ,ID NUMBER: "+dbList.get(0).get("ID_NUMBER")+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Second Document-	ID TYPE: " + dbList.get(1).get("ID_TYPE")+" ,ID NUMBER: "+dbList.get(1).get("ID_NUMBER")+"</b>");

					setOwsHeader();
					// fetching the document list for the corresponding
					// profile
					String fetchDocumentsReq = WSClient.createSOAPMessage("OWSFetchDocumentList", "DS_01");
					String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

					if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
							"FetchDocumentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchDocumentsRes, "FetchDocumentListResponse_Result_Text",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetch Response gives the error message-----></b> "
											+ WSClient.getElementValue(fetchDocumentsRes,
													"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
						}
						if (WSAssert.assertIfElementExists(fetchDocumentsRes,
								"FetchDocumentListResponse_DocumentList_GovernmentID", false)) {

							HashMap<String, String> xPath = new HashMap<String, String>();
							xPath.put("FetchDocumentListResponse_DocumentList_GovernmentID_documentNumber",
									"FetchDocumentListResponse_DocumentList_GovernmentID");
							xPath.put("FetchDocumentListResponse_DocumentList_GovernmentID_documentType",
									"FetchDocumentListResponse_DocumentList_GovernmentID");
							List<LinkedHashMap<String, String>> respData = WSClient
									.getMultipleNodeList(fetchDocumentsRes, xPath, false, XMLType.RESPONSE);
							ArrayList<LinkedHashMap<String, String>> dbData = WSClient
									.getDBRows(WSClient.getQuery("QS_03"));
							WSAssert.assertEquals(respData, dbData, false);

						}

					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
				}

			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchDocumentList", "OWS", "Name", "in-QA" })

	public void fetchDocument_38289() {
		try {
			String testName = "fetchDocumentList_38289";
			WSClient.startTest(testName,
					"Verify that no documents are retrieved when not attached to a profile",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
						+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_02");
				WSClient.setData("{var_docType}", docType);
				OPERALib.setOperaHeader(OPERALib.getUserName());

				setOwsHeader();
				// fetching the document list for the corresponding
				// profile
				String fetchDocumentsReq = WSClient.createSOAPMessage("OWSFetchDocumentList", "DS_01");
				String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

				if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
						"FetchDocumentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(fetchDocumentsRes, "FetchDocumentListResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetch Response gives the error message-----></b> "
										+ WSClient.getElementValue(fetchDocumentsRes,
												"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
					}
					if (!WSAssert.assertIfElementExists(fetchDocumentsRes,
							"FetchDocumentListResponse_DocumentList_GovernmentID", true)) {
						// validating the data
						WSClient.writeToReport(LogStatus.PASS, "No document is retrieved as there is no document attached to the profile");
					}
				}

			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchDocumentList", "OWS", "Name", "in-QA" })
	public void fetchDocument_382887() {
		try {
			String testName = "fetchDocumentList_382887";
			WSClient.startTest(testName, "Verify that when Multiple Documents of the same document type are present for a profile, the most recent document is retrieved as primary",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				WSClient.setData("{var_profileId}", operaProfileID);


				String documentNo = "XXXXXXX32";

				String documentNo2 = "XXXXXXX89";

				WSClient.setData("{var_docnum}", documentNo);
				WSClient.setData("{var_docNum2}", documentNo2);
				WSClient.setData("{var_primary}", "true");
				WSClient.setData("{var_primary2}", "true");

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);
				WSClient.setData("{var_docType2}", OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));

				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_05");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {

					//Now fetching the Details of document from DB
					String quFive = WSClient.getQuery("OWSFetchDocumentList", "QS_05");
					List<LinkedHashMap<String, String>> dbList = WSClient.getDBRows(quFive);
					WSClient.writeToReport(LogStatus.INFO, "<b>First Document-	ID TYPE: " + dbList.get(0).get("ID_TYPE")+" ,ID NUMBER: "+dbList.get(0).get("ID_NUMBER")+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Second Document-	ID TYPE: " + dbList.get(1).get("ID_TYPE")+" ,ID NUMBER: "+dbList.get(1).get("ID_NUMBER")+"</b>");
					setOwsHeader();
					// fetching the document list for the corresponding
					// profile
					String fetchDocumentsReq = WSClient.createSOAPMessage("OWSFetchDocumentList", "DS_01");
					String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

					if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
							"FetchDocumentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchDocumentsRes, "FetchDocumentListResponse_Result_Text",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetch Response gives the error message-----></b> "
											+ WSClient.getElementValue(fetchDocumentsRes,
													"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
						}
						if (WSAssert.assertIfElementExists(fetchDocumentsRes,
								"FetchDocumentListResponse_DocumentList_GovernmentID", false)) {

							HashMap<String, String> xPath = new HashMap<String, String>();
							xPath.put("FetchDocumentListResponse_DocumentList_GovernmentID_documentNumber",
									"FetchDocumentListResponse_DocumentList_GovernmentID");
							xPath.put("FetchDocumentListResponse_DocumentList_GovernmentID_documentType",
									"FetchDocumentListResponse_DocumentList_GovernmentID");
							List<LinkedHashMap<String, String>> respData = WSClient
									.getMultipleNodeList(fetchDocumentsRes, xPath, false, XMLType.RESPONSE);
							ArrayList<LinkedHashMap<String, String>> dbData = WSClient
									.getDBRows(WSClient.getQuery("QS_03"));
							WSAssert.assertEquals(respData, dbData, false);
							String doc=WSClient.getElementValueByAttribute(fetchDocumentsRes,"FetchDocumentListResponse_DocumentList_GovernmentID_documentNumber",
									"FetchDocumentListResponse_DocumentList_GovernmentID_primary", "true", XMLType.RESPONSE);
							if(WSAssert.assertEquals(documentNo2, doc, true))
								WSClient.writeToReport(LogStatus.PASS, "Primary is updated as True for the most recent document : "+doc);
							else
								WSClient.writeToReport(LogStatus.FAIL, "Primary is not updated as True for the most recent document");

						}

					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
				}

			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchDocumentList", "OWS", "Name", "in-QA" })

	public void fetchDocument_17897() {
		try {
			String testName = "fetchDocument_17897";
			WSClient.startTest(testName,
					"Verify that the error-message is present in the response when invalid PROFILE ID is passed",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")
						+ WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
				WSClient.setData("{var_docnum}", documentNo);

				//Now fetching the Details of document from DB
				String quFive = WSClient.getQuery("OWSFetchDocumentList", "QS_05");
				LinkedHashMap<String, String> dbMap = WSClient.getDBRow(quFive);
				WSClient.writeToReport(LogStatus.INFO, "<b>ID TYPE: " + dbMap.get("ID_TYPE")+"</b>");
				WSClient.writeToReport(LogStatus.INFO, "<b>ID NUMBER: " + dbMap.get("ID_NUMBER")+"</b>");

				setOwsHeader();
				// fetching the document list for the corresponding
				// profile
				String fetchDocumentsReq = WSClient.createSOAPMessage("OWSFetchDocumentList", "DS_FetchDocumentList_02");
				String fetchDocumentsRes = WSClient.processSOAPMessage(fetchDocumentsReq);

				if (WSAssert.assertIfElementValueEquals(fetchDocumentsRes,
						"FetchDocumentListResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(fetchDocumentsRes, "FetchDocumentListResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetch Response gives the error message-----></b> "
										+ WSClient.getElementValue(fetchDocumentsRes,
												"FetchDocumentListResponse_Result_Text", XMLType.RESPONSE));
					}

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Change Profile Prerequisite not working");
			}


		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

}
