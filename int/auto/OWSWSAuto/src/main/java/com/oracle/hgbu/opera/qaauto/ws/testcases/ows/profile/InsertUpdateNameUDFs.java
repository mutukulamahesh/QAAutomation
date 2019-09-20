package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

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

public class InsertUpdateNameUDFs extends WSSetUp {

	// Total Test Cases : 6

	// Creating Profile
	public String createProfile(String ds) {
		String profileID = "";
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a
			// Profile"+"</b>");

			profileID = CreateProfile.createProfile(ds);
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return profileID;
	}

	public void setOWSHeader() {
		try {
			String resort = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
		}
	}

	// Sanity Test Case : 1
	@Test(groups = { "sanity", "Name", "OWS", "InsertUpdateNameUDFs" })
	public void insertUpdateNameUDFs_38441() {
		try {
			String testname = "insertUpdateNameUDFs_38441";
			WSClient.startTest(testname,
					"Verify that UDFs are inserted to a profile by passing valid values in mandatory fields.",
					"sanity");

			String UniqueId;
			// Creating a profile
			if (!(UniqueId = createProfile("DS_01")).equals("error")) {
				// Setting Variables
				WSClient.setData("{var_profileID}", UniqueId);

				String charUDF = HTNGLib.getUDFLabel("C", "P");
				String numUDF = HTNGLib.getUDFLabel("N", "P");
				String dateUDF = HTNGLib.getUDFLabel("D", "P");
				String charName = HTNGLib.getUDFName("C", charUDF, "P");
				String numName = HTNGLib.getUDFName("N", numUDF, "P");
				String dateName = HTNGLib.getUDFName("D", dateUDF, "P");

				WSClient.setData("{var_charName}", charUDF);
				WSClient.setData("{var_numericName}", numUDF);
				WSClient.setData("{var_dateName}", dateUDF);
				WSClient.setData("{var_charValue}", "contact");
				WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

				WSClient.setData("{var_queryCharName}", charName);
				WSClient.setData("{var_queryNumericName}", numName);
				WSClient.setData("{var_queryDateName}", dateName);

				setOWSHeader();

				// Creating request and processing response for OWS
				// InsertUpdateNameUDFs Operation
				String insertUpdateNameReq = WSClient.createSOAPMessage("OWSInsertUpdateNameUDFs", "DS_01");
				String insertUpdateNameResponseXML = WSClient.processSOAPMessage(insertUpdateNameReq);

				// Validating response of OWS InsertUpdateNameUDFs Operation
				if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
						"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
							"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						// Database Validation
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue",
								"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue");
						xPath.put("UserDefinedValues_UserDefinedValue[2]_NumericValue",
								"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue[2]");
						xPath.put("UserDefinedValues_UserDefinedValue[3]_DateValue",
								"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue[3]");
						expectedValues = WSClient.getSingleNodeList(insertUpdateNameReq, xPath, false, XMLType.REQUEST);
						String query = WSClient.getQuery("QS_03");
						db = WSClient.getDBRow(query);
						db.put("DateValue1", db.get("DateValue1").substring(0, 10));
						WSAssert.assertEquals(expectedValues, db, false);
					} else {
						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The error displayed in the InsertUpdateName response is :" + message);
							if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode = WSAssert.getElementValue(insertUpdateNameResponseXML,
										"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);
							}
						}
						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
								"InsertUpdateNameUDFsResponse_Result_GDSError", true)) {
							String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The gds error displayed in the DeletePreference response is :" + message);
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// MR Test Case : 1
	@Test(groups = { "minimumRegression", "Name", "OWS", "InsertUpdateNameUDFs" })
	public void insertUpdateNameUDFs_38591() {
		try {
			String testname = "insertUpdateNameUDFs_38591";
			WSClient.startTest(testname,
					"Verify that error message is obtained by passing valid values in mandatory fields but passing profile type as EXTERNAL",
					"minimumRegression");

			String UniqueId;
			// Creating a profile
			if (!(UniqueId = createProfile("DS_01")).equals("error")) {
				// Setting Variables
				WSClient.setData("{var_profileID}", UniqueId);

				String charUDF = HTNGLib.getUDFLabel("C", "P");
				String numUDF = HTNGLib.getUDFLabel("N", "P");
				String dateUDF = HTNGLib.getUDFLabel("D", "P");
				String charName = HTNGLib.getUDFName("C", charUDF, "P");
				String numName = HTNGLib.getUDFName("N", numUDF, "P");
				String dateName = HTNGLib.getUDFName("D", dateUDF, "P");

				WSClient.setData("{var_charName}", charUDF);
				WSClient.setData("{var_numericName}", numUDF);
				WSClient.setData("{var_dateName}", dateUDF);
				WSClient.setData("{var_charValue}", "contact");
				WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

				WSClient.setData("{var_queryCharName}", charName);
				WSClient.setData("{var_queryNumericName}", numName);
				WSClient.setData("{var_queryDateName}", dateName);

				setOWSHeader();

				// Creating request and processing response for OWS
				// InsertUpdateNameUDFs Operation
				String insertUpdateNameReq = WSClient.createSOAPMessage("OWSInsertUpdateNameUDFs", "DS_02");
				String insertUpdateNameResponseXML = WSClient.processSOAPMessage(insertUpdateNameReq);

				// Validating response of OWS InsertUpdateNameUDFs Operation
				if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
						"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
							"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", "FAIL", false)) {

						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML, "Result_Text_TextElement",
								false)) {

							if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
									"Result_Text_TextElement", "Name Id is null", true)) {
								WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
										"Result_Text_TextElement", "Name Id is null", false);
							} else if (WSAssert.assertIfElementContains(insertUpdateNameResponseXML,
									"Result_Text_TextElement", "This instance does not support External values",
									false)) {

							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
							if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode = WSAssert.getElementValue(insertUpdateNameResponseXML,
										"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);
							}
							if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_GDSError", true)) {
								String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
										"InsertUpdateNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The gds error displayed in the InsertUpdateNameUDFs response is :" + message);
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// MR Test Case : 2
	@Test(groups = { "minimumRegression", "Name", "OWS", "InsertUpdateNameUDFs" })
	public void insertUpdateNameUDFs_38593() {
		try {
			String testname = "insertUpdateNameUDFs_38593";
			WSClient.startTest(testname, "Verify that error message is obtained by not passing any nameId",
					"minimumRegression");

			String UniqueId;
			// Creating a profile
			if (!(UniqueId = createProfile("DS_01")).equals("error")) {
				// Setting Variables
				WSClient.setData("{var_profileID}", UniqueId);

				String charUDF = HTNGLib.getUDFLabel("C", "P");
				String numUDF = HTNGLib.getUDFLabel("N", "P");
				String dateUDF = HTNGLib.getUDFLabel("D", "P");
				String charName = HTNGLib.getUDFName("C", charUDF, "P");
				String numName = HTNGLib.getUDFName("N", numUDF, "P");
				String dateName = HTNGLib.getUDFName("D", dateUDF, "P");

				WSClient.setData("{var_charName}", charUDF);
				WSClient.setData("{var_numericName}", numUDF);
				WSClient.setData("{var_dateName}", dateUDF);
				WSClient.setData("{var_charValue}", "contact");
				WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

				WSClient.setData("{var_queryCharName}", charName);
				WSClient.setData("{var_queryNumericName}", numName);
				WSClient.setData("{var_queryDateName}", dateName);
				setOWSHeader();

				// Creating request and processing response for OWS
				// InsertUpdateNameUDFs Operation
				String insertUpdateNameReq = WSClient.createSOAPMessage("OWSInsertUpdateNameUDFs", "DS_04");
				String insertUpdateNameResponseXML = WSClient.processSOAPMessage(insertUpdateNameReq);

				// Validating response of OWS InsertUpdateNameUDFs Operation
				if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
						"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
							"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", "FAIL", false)) {

						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML, "Result_Text_TextElement",
								false)) {

							if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
									"Result_Text_TextElement", "Name Id is null", true)) {
								WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
										"Result_Text_TextElement", "Name Id is null", false);
							} else if (WSAssert.assertIfElementContains(insertUpdateNameResponseXML,
									"Result_Text_TextElement", "UniqueID is null", false)) {

							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
							if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode = WSAssert.getElementValue(insertUpdateNameResponseXML,
										"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);
							}
							if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_GDSError", true)) {
								String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
										"InsertUpdateNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The gds error displayed in the InsertUpdateNameUDFs response is :" + message);
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// MR Test Case : 3
	@Test(groups = { "minimumRegression", "Name", "OWS", "InsertUpdateNameUDFs" })
	public void insertUpdateNameUDFs_38594() {
		try {
			String testname = "insertUpdateNameUDFs_38594";
			WSClient.startTest(testname, "Insert UDFs for a profile while passing invalid UDF Name Mapping",
					"minimumRegression");

			String UniqueId;
			// Creating a profile
			if (!(UniqueId = createProfile("DS_01")).equals("error")) {
				// Setting Variables
				WSClient.setData("{var_profileID}", UniqueId);

				WSClient.setData("{var_charName}", OperaPropConfig.getDataSetForCode("UDFLabel_P", "DS_03"));
				WSClient.setData("{var_numericName}", OperaPropConfig.getDataSetForCode("UDFLabel_P", "DS_02"));
				WSClient.setData("{var_dateName}", OperaPropConfig.getDataSetForCode("UDFLabel_P", "DS_05"));
				WSClient.setData("{var_queryCharName}", OperaPropConfig.getDataSetForCode("UDFName", "DS_03"));
				WSClient.setData("{var_queryNumericName}", OperaPropConfig.getDataSetForCode("UDFName", "DS_02"));
				WSClient.setData("{var_queryDateName}", OperaPropConfig.getDataSetForCode("UDFName", "DS_05"));
				WSClient.setData("{var_charValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
				WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

				setOWSHeader();

				// Creating request and processing response for OWS
				// InsertUpdateNameUDFs Operation
				String insertUpdateNameReq = WSClient.createSOAPMessage("OWSInsertUpdateNameUDFs", "DS_05");
				String insertUpdateNameResponseXML = WSClient.processSOAPMessage(insertUpdateNameReq);

				// Validating response of OWS InsertUpdateNameUDFs Operation
				if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
						"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
							"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						// Database Validation
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> db2 = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("UserDefinedValues_UserDefinedValue[2]_NumericValue",
								"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue[2]");
						xPath.put("UserDefinedValues_UserDefinedValue[3]_DateValue",
								"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue[3]");
						expectedValues = WSClient.getSingleNodeList(insertUpdateNameReq, xPath, false, XMLType.REQUEST);
						String query = WSClient.getQuery("QS_04");
						db2 = WSClient.getDBRow(query);
						if (db2.get("DateValue1") != null)
							WSClient.writeToReport(LogStatus.FAIL,
									"DateValue1 -> Expected : null Actual :" + db2.get("DateValue1"));
						else
							WSClient.writeToReport(LogStatus.PASS,
									"DateValue1 -> Expected :null Actual :" + db2.get("DateValue1"));
						query = WSClient.getQuery("QS_05");
						db2 = WSClient.getDBRow(query);
						if (db2.get("NumericValue1") != null)
							WSClient.writeToReport(LogStatus.FAIL,
									"NumericValue1 -> Expected : null Actual :" + db2.get("NumericValue1"));
						else
							WSClient.writeToReport(LogStatus.PASS,
									"NumericValue1 -> Expected :null Actual :" + db2.get("NumericValue1"));
						query = WSClient.getQuery("QS_02");
						db2 = WSClient.getDBRow(query);
						if (db2.get("CharacterValue1") != null)
							WSClient.writeToReport(LogStatus.FAIL,
									"CharacterValue1 -> Expected : null Actual :" + db2.get("CharacterValue1"));
						else
							WSClient.writeToReport(LogStatus.PASS,
									"CharacterValue1 -> Expected :null Actual :" + db2.get("CharacterValue1"));

					} else {
						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The error displayed in the InsertUpdateName response is :" + message);
							if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode = WSAssert.getElementValue(insertUpdateNameResponseXML,
										"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);
							}
						}
						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
								"InsertUpdateNameUDFsResponse_Result_GDSError", true)) {
							String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The gds error displayed in the DeletePreference response is :" + message);
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	@Test(groups = { "minimumRegression", "Name", "OWS", "InsertUpdateNameUDFs" })
	public void insertUpdateNameUDFs_41922() {
		try {
			String testname = "insertUpdateNameUDFs_41922";
			WSClient.startTest(testname, "Verify that UDFs are updated by passing valid values in mandatory fields.",
					"minimumRegression");

			String UniqueId;
			// Creating a profile
			if (!(UniqueId = createProfile("DS_01")).equals("error")) {
				// Setting Variables
				WSClient.setData("{var_profileID}", UniqueId);
				String charUDF = HTNGLib.getUDFLabel("C", "P");
				String numUDF = HTNGLib.getUDFLabel("N", "P");
				String dateUDF = HTNGLib.getUDFLabel("D", "P");
				String charName = HTNGLib.getUDFName("C", charUDF, "P");
				String numName = HTNGLib.getUDFName("N", numUDF, "P");
				String dateName = HTNGLib.getUDFName("D", dateUDF, "P");

				WSClient.setData("{var_charName}", charUDF);
				WSClient.setData("{var_numericName}", numUDF);
				WSClient.setData("{var_dateName}", dateUDF);
				WSClient.setData("{var_charValue}", "contact");
				WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

				WSClient.setData("{var_queryCharName}", charName);
				WSClient.setData("{var_queryNumericName}", numName);
				WSClient.setData("{var_queryDateName}", dateName);
				setOWSHeader();

				// Creating request and processing response for OWS
				// InsertUpdateNameUDFs Operation
				String insertUpdateNameReq = WSClient.createSOAPMessage("OWSInsertUpdateNameUDFs", "DS_01");
				String insertUpdateNameResponseXML = WSClient.processSOAPMessage(insertUpdateNameReq);

				// Validating response of OWS InsertUpdateNameUDFs Operation
				if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
						"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML,
							"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						// Updated UDFs Values
						WSClient.setData("{var_charValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
						WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
						WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

						// Updating UDFs values
						String insertUpdateNameReq2 = WSClient.createSOAPMessage("OWSInsertUpdateNameUDFs", "DS_01");
						String insertUpdateNameResponseXML2 = WSClient.processSOAPMessage(insertUpdateNameReq2);

						// Validating response of OWS InsertUpdateNameUDFs
						// Operation
						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML2,
								"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(insertUpdateNameResponseXML2,
									"InsertUpdateNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								// Database Validation
								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
								xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue",
										"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue");
								xPath.put("UserDefinedValues_UserDefinedValue[2]_NumericValue",
										"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue[2]");
								xPath.put("UserDefinedValues_UserDefinedValue[3]_DateValue",
										"InsertUpdateNameUDFsRequest_UserDefinedValues_UserDefinedValue[3]");
								expectedValues = WSClient.getSingleNodeList(insertUpdateNameReq2, xPath, false,
										XMLType.REQUEST);
								String query = WSClient.getQuery("QS_03");
								db = WSClient.getDBRow(query);
								db.put("DateValue1", db.get("DateValue1").substring(0, 10));
								WSAssert.assertEquals(expectedValues, db, false);
							}
						}
					} else {
						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The error displayed in the InsertUpdateName response is :" + message);
							if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode = WSAssert.getElementValue(insertUpdateNameResponseXML,
										"InsertUpdateNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);
							}
						}
						if (WSAssert.assertIfElementExists(insertUpdateNameResponseXML,
								"InsertUpdateNameUDFsResponse_Result_GDSError", true)) {
							String message = WSAssert.getElementValue(insertUpdateNameResponseXML,
									"InsertUpdateNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The gds error displayed in the DeletePreference response is :" + message);
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

}
