package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchNameUDFs extends WSSetUp {

	// Total Test Cases : 9

	//Creating Profile
	public String createProfile(String ds)
	{
		String profileID = "";
		try
		{
			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);


			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Profile"+"</b>");

			profileID = CreateProfile.createProfile(ds);
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

		}
		catch(Exception e)
		{
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
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
		}
	}

	// Sanity Test Case :1
	@Test(groups={"sanity","Name","OWS","FetchNameUDFs"})
	public void fetchNameUDFs_41323() {
		try {
			String testname = "fetchNameUDFs_41323";
			WSClient.startTest(testname, "Verify that Character UDF type and value associated to profile are fetched correctly", "sanity");

			String UniqueId;

			// Creating a profile
			if(!(UniqueId = createProfile("DS_01")).equals("error"))
			{

				// Setting variables for inserting UDFs
				WSClient.setData("{var_profileID}", UniqueId);
				String charUDF = HTNGLib.getUDFLabel("C", "P");
				String charName = HTNGLib.getUDFName("C", charUDF, "P");

				WSClient.setData("{var_charName}", charName);
				WSClient.setData("{var_charValue}", "contact");


				// Inserting UDFs to profile
				String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_15");
				String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

				if(WSAssert.assertIfElementExists(changeProfileResponseXML,"ChangeProfileRS_Success", true)) {

					setOWSHeader();

					// Creating request and processing response for OWS FetchNameUDFs Operation
					String fetchNameReq = WSClient.createSOAPMessage("OWSFetchNameUDFs", "DS_01");
					String fetchNameResponseXML = WSClient.processSOAPMessage(fetchNameReq);

					// Validating response of OWS FetchNameUDFs Operation
					if(WSAssert.assertIfElementExists(fetchNameResponseXML,
							"FetchNameUDFsResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchNameResponseXML,
								"FetchNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							// Database Validation
							LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
							xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							xPath.put("FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue_valueName", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							actualValues = WSClient.getSingleNodeList(fetchNameResponseXML, xPath, false, XMLType.RESPONSE);
							expectedValues= WSClient.getDBRow(WSClient.getQuery("QS_01"));
							expectedValues.put("valueName1", charUDF);
							WSAssert.assertEquals( expectedValues,actualValues,false);
						}else {
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The error displayed in the FetchNameUDF response is :"+ message);
								if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

								}
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
								String FaultErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
							}
						}
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ ChangeProfile-----Blocked");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :1
	@Test(groups={"minimumRegression","Name","OWS","FetchNameUDFs"})
	public void fetchNameUDFs_41324() {
		try {
			String testname = "fetchNameUDFs_41324";
			WSClient.startTest(testname, "Verify that Numeric UDF type and value associated to profile are fetched correctly", "minimumRegression");

			String UniqueId;

			// Creating a profile
			if(!(UniqueId = createProfile("DS_01")).equals("error"))
			{

				// Setting variables for inserting UDFs
				WSClient.setData("{var_profileID}", UniqueId);
				String charUDF = HTNGLib.getUDFLabel("N", "P");
				String charName = HTNGLib.getUDFName("N", charUDF, "P");

				WSClient.setData("{var_numericName}", charName);
				WSClient.setData("{var_numericValue}",  WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));

				// Inserting UDFs to profile
				String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_16");
				String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

				if(WSAssert.assertIfElementExists(changeProfileResponseXML,"ChangeProfileRS_Success", true)) {

					setOWSHeader();

					// Creating request and processing response for OWS FetchNameUDFs Operation
					String fetchNameReq = WSClient.createSOAPMessage("OWSFetchNameUDFs", "DS_01");
					String fetchNameResponseXML = WSClient.processSOAPMessage(fetchNameReq);

					// Validating response of OWS FetchNameUDFs Operation
					if(WSAssert.assertIfElementExists(fetchNameResponseXML,
							"FetchNameUDFsResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchNameResponseXML,
								"FetchNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							// Database Validation
							LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
							xPath.put("UserDefinedValues_UserDefinedValue_NumericValue", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							xPath.put("FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue_valueName", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							actualValues = WSClient.getSingleNodeList(fetchNameResponseXML, xPath, false, XMLType.RESPONSE);
							expectedValues= WSClient.getDBRow(WSClient.getQuery("QS_03"));
							expectedValues.put("valueName1", charUDF);
							WSAssert.assertEquals(expectedValues,actualValues,false);
						}else {
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The error displayed in the FetchNameUDF response is :"+ message);
								if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

								}
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
								String FaultErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
							}
						}
					}else {
						WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ ChangeProfile-----Blocked");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :2
	@Test(groups={"minimumRegression","Name","OWS","FetchNameUDFs"})
	public void fetchNameUDFs_41329() {
		try {
			String testname = "fetchNameUDFs_41329";
			WSClient.startTest(testname, "Verify that DateValue UDF type and value associated to profile are fetched correctly", "minimumRegression");

			String UniqueId;

			// Creating a profile
			if(!(UniqueId = createProfile("DS_01")).equals("error"))
			{

				// Setting variables for inserting UDFs
				WSClient.setData("{var_profileID}", UniqueId);

				String charUDF = HTNGLib.getUDFLabel("D", "P");
				String charName = HTNGLib.getUDFName("D", charUDF, "P");

				WSClient.setData("{var_dateName}", charName);
				WSClient.setData("{var_dateValue}",   WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

				// Inserting UDFs to profile
				String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_17");
				String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

				if(WSAssert.assertIfElementExists(changeProfileResponseXML,"ChangeProfileRS_Success", true)) {

					setOWSHeader();

					// Creating request and processing response for OWS FetchNameUDFs Operation
					String fetchNameReq = WSClient.createSOAPMessage("OWSFetchNameUDFs", "DS_01");
					String fetchNameResponseXML = WSClient.processSOAPMessage(fetchNameReq);

					// Validating response of OWS FetchNameUDFs Operation
					if(WSAssert.assertIfElementExists(fetchNameResponseXML,
							"FetchNameUDFsResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchNameResponseXML,
								"FetchNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							// Database Validation
							LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
							xPath.put("UserDefinedValues_UserDefinedValue_DateValue", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							xPath.put("FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue_valueName", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							actualValues = WSClient.getSingleNodeList(fetchNameResponseXML, xPath, false, XMLType.RESPONSE);
							String query = WSClient.getQuery("QS_02");
							String date = WSClient.getDBRow(query).get("DateValue1");
							String dateValue = date.substring(0,10)+'T'+date.substring(11,19);
							expectedValues.put("DateValue1", dateValue);
							expectedValues.put("valueName1",charUDF);
							WSAssert.assertEquals( expectedValues,actualValues,false);
						}else {
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The error displayed in the FetchNameUDF response is :"+ message);
								if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

								}
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
								String FaultErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
							}
						}
					}else {
						WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ ChangeProfile-----Blocked");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :3
	@Test(groups={"minimumRegression","Name","OWS","FetchNameUDFs"})
	public void fetchNameUDFs_38444() {
		try {
			String testname = "fetchNameUDFs_38444";
			WSClient.startTest(testname, "Verify that UDFs associated to a profile are fetched by passing valid values in mandatory fields", "minimumRegression");

			String UniqueId;

			// Creating a profile
			if(!(UniqueId = createProfile("DS_01")).equals("error"))
			{

				// Setting variables for inserting UDFs
				WSClient.setData("{var_profileID}", UniqueId);

				String charUDF = HTNGLib.getUDFLabel("C", "P");
				String numUDF = HTNGLib.getUDFLabel("N", "P");
				String dateUDF = HTNGLib.getUDFLabel("D", "P");
				String charName = HTNGLib.getUDFName("C", charUDF, "P");
				String numName = HTNGLib.getUDFName("N", numUDF, "P");
				String dateName = HTNGLib.getUDFName("D", dateUDF, "P");

				WSClient.setData("{var_charName}", charName);
				WSClient.setData("{var_numericName}", numName);
				WSClient.setData("{var_dateName}", dateName);
				WSClient.setData("{var_charValue}", "contact");
				WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));


				// Inserting UDFs to profile
				String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_08");
				String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

				if(WSAssert.assertIfElementExists(changeProfileResponseXML,"ChangeProfileRS_Success", true)) {

					setOWSHeader();

					// Creating request and processing response for OWS FetchNameUDFs Operation
					String fetchNameReq = WSClient.createSOAPMessage("OWSFetchNameUDFs", "DS_01");
					String fetchNameResponseXML = WSClient.processSOAPMessage(fetchNameReq);

					// Validating response of OWS FetchNameUDFs Operation
					if(WSAssert.assertIfElementExists(fetchNameResponseXML,
							"FetchNameUDFsResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchNameResponseXML,
								"FetchNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							// Database Validation
							List<LinkedHashMap<String,String>> expectedValues = new ArrayList<LinkedHashMap<String,String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String,String>>();
							LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
							xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							xPath.put("UserDefinedValues_UserDefinedValue_NumericValue", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							xPath.put("UserDefinedValues_UserDefinedValue_DateValue", "FetchNameUDFsResponse_UserDefinedValues_UserDefinedValue");
							actualValues = WSClient.getMultipleNodeList(fetchNameResponseXML, xPath, false, XMLType.RESPONSE);
							String query = WSClient.getQuery("QS_01");
							expectedValues.add(WSClient.getDBRow(query));
							query = WSClient.getQuery("QS_02");
							String date = WSClient.getDBRow(query).get("DateValue1");
							String dateValue = date.substring(0,10)+'T'+date.substring(11,19);
							LinkedHashMap<String,String> Q_02 = new LinkedHashMap<String,String>();
							Q_02.put("DateValue1", dateValue);
							expectedValues.add(Q_02);
							query = WSClient.getQuery("QS_03");
							expectedValues.add(WSClient.getDBRow(query));
							WSAssert.assertEquals(actualValues, expectedValues,false);
						}else {
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The error displayed in the FetchNameUDF response is :"+ message);
								if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

								}
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", true)) {
								String message=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the FetchNameUDF response is :"+ message);
							}
							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
								String FaultErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
							}
						}
					}else {
						WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ ChangeProfile-----Blocked");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}


	// Minimum Regression Test Case : 6
	@Test(groups={"minimumRegression","Name","OWS","FetchNameUDFs"})
	public void fetchNameUDFs_38599() {
		try {
			String testname = "fetchNameUDFs_38599";
			WSClient.startTest(testname, "Verify that error message is obtained by keeping nameid field empty.", "minimumRegression");

			String UniqueId;

			// Creating a profile
			if(!(UniqueId = createProfile("DS_01")).equals("error"))
			{

				// Setting variables for inserting UDFs
				WSClient.setData("{var_profileID}", UniqueId);

				String charUDF = HTNGLib.getUDFLabel("C", "P");
				String numUDF = HTNGLib.getUDFLabel("N", "P");
				String dateUDF = HTNGLib.getUDFLabel("D", "P");
				String charName = HTNGLib.getUDFName("C", charUDF, "P");
				String numName = HTNGLib.getUDFName("N", numUDF, "P");
				String dateName = HTNGLib.getUDFName("D", dateUDF, "P");

				WSClient.setData("{var_charName}", charName);
				WSClient.setData("{var_numericName}", numName);
				WSClient.setData("{var_dateName}", dateName);
				WSClient.setData("{var_charValue}", "contact");
				WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));


				// Inserting UDFs to profile
				String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_08");
				String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

				if(WSAssert.assertIfElementExists(changeProfileResponseXML,"ChangeProfileRS_Success", true)) {

					setOWSHeader();

					// Creating request and processing response for OWS FetchNameUDFs Operation
					String fetchNameReq = WSClient.createSOAPMessage("OWSFetchNameUDFs", "DS_04");
					String fetchNameResponseXML = WSClient.processSOAPMessage(fetchNameReq);

					// Validation for OWS Operation
					if(WSAssert.assertIfElementExists(fetchNameResponseXML,"FetchNameUDFsResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchNameResponseXML,"FetchNameUDFsResponse_Result_resultStatusFlag", "FAIL", false)) {

							if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Result_Text_TextElement", false)) {
								if(WSAssert.assertIfElementValueEquals(fetchNameResponseXML, "Result_Text_TextElement", "Name Id is null", true)) {
									WSAssert.assertIfElementValueEquals(fetchNameResponseXML, "Result_Text_TextElement", "Name Id is null", false);
								}else if(WSAssert.assertIfElementContains(fetchNameResponseXML, "Result_Text_TextElement", "UniqueID is null", false)) {

								}
							}else {

								if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
								}
								if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the FetchNameUDF response is :"+ message);
								}
								if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
									String FaultErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
						}
					}else {
						WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ ChangeProfile-----Blocked");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 7
	@Test(groups={"minimumRegression","Name","OWS","FetchNameUDFs"})
	public void fetchNameUDFs_41202() {
		try {
			String testname = "fetchNameUDFs_41202";
			WSClient.startTest(testname, "Verify that no udfs are fetched when nameid having no associated udfs is passed", "minimumRegression");

			String UniqueId;

			// Creating a profile
			if(!(UniqueId = createProfile("DS_01")).equals("error"))
			{

				// Setting variables for inserting UDFs
				WSClient.setData("{var_profileID}", UniqueId);


				setOWSHeader();

				// Creating request and processing response for OWS FetchNameUDFs Operation
				String fetchNameReq = WSClient.createSOAPMessage("OWSFetchNameUDFs", "DS_01");
				String fetchNameResponseXML = WSClient.processSOAPMessage(fetchNameReq);

				// Validation for OWS Operation
				if(WSAssert.assertIfElementExists(fetchNameResponseXML,"FetchNameUDFsResponse_Result_resultStatusFlag", false)) {
					if (WSAssert.assertIfElementValueEquals(fetchNameResponseXML,"FetchNameUDFsResponse_Result_resultStatusFlag", "SUCCESS", false)) {


					}
				}else {

					if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", true)) {
						String operaErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
					}
					if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", true)) {
						String message=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the FetchNameUDF response is :"+ message);
					}
					if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
						String FaultErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
					}
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 8
			@Test(groups={"minimumRegression","Name","OWS","FetchNameUDFs"})
			public void fetchNameUDFs_HHOCM49_1() {
			try {
				String testname = "fetchNameUDFs_HHOCM49_1";
				WSClient.startTest(testname, "Verify that error message is obtained by passing text nameid as textvalue", "minimumRegression");
				
				String UniqueId;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
						
					// Setting variables for inserting UDFs
						WSClient.setData("{var_profileID}", UniqueId);

						String charUDF = HTNGLib.getUDFLabel("C", "P");
						String numUDF = HTNGLib.getUDFLabel("N", "P");
						String dateUDF = HTNGLib.getUDFLabel("D", "P");
						String charName = HTNGLib.getUDFName("C", charUDF, "P");
						String numName = HTNGLib.getUDFName("N", numUDF, "P");
						String dateName = HTNGLib.getUDFName("D", dateUDF, "P");

						WSClient.setData("{var_charName}", charName);
						WSClient.setData("{var_numericName}", numName);
						WSClient.setData("{var_dateName}", dateName);
						WSClient.setData("{var_charValue}", "contact");
						WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
						WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

							
						// Inserting UDFs to profile
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_08");
						String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);
						
						if(WSAssert.assertIfElementExists(changeProfileResponseXML,"ChangeProfileRS_Success", true)) {
							
							setOWSHeader();
						
							// Creating request and processing response for OWS FetchNameUDFs Operation
							String fetchNameReq = WSClient.createSOAPMessage("OWSFetchNameUDFs", "DS_05");
							String fetchNameResponseXML = WSClient.processSOAPMessage(fetchNameReq);
							
							// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(fetchNameResponseXML,"FetchNameUDFsResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(fetchNameResponseXML,"FetchNameUDFsResponse_Result_resultStatusFlag", "FAIL", false)) {
										
										if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Result_Text_TextElement", false)) {
										if(WSAssert.assertIfElementValueEquals(fetchNameResponseXML, "Result_Text_TextElement", "Name Id is null", true)) {
											WSAssert.assertIfElementValueEquals(fetchNameResponseXML, "Result_Text_TextElement", "Name Id is null", false);
										}else if(WSAssert.assertIfElementContains(fetchNameResponseXML, "Result_Text_TextElement", "UniqueID is null", false)) {
											
										}
										}else {
											
											if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
											}
											if(WSAssert.assertIfElementExists(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", true)) {
												String message=WSAssert.getElementValue(fetchNameResponseXML, "FetchNameUDFsResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the FetchNameUDF response is :"+ message);
											}
											if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
												String FaultErrorCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Fault String :"+ FaultErrorCode);
											}
										}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.INFO, "ResultFlag doesn't exist in Response Checking Fault Code!");
									if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultcode", true)) {
										String faultCode=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultcode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.PASS, "Fault Error Code :"+ faultCode);
										if(WSAssert.assertIfElementExists(fetchNameResponseXML, "Fault_faultstring", true)) {
											String faultString=WSAssert.getElementValue(fetchNameResponseXML, "Fault_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.PASS, "Fault String Code :"+ faultString);
										}
									}
								else {
									WSClient.writeToReport(LogStatus.FAIL, "Fault Code even does not exist in the Response!");
								}
							} 
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ ChangeProfile-----Blocked");
						}
					}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
			}
		}

}