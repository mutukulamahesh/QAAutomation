package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.ArrayList;
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

public class InsertEmail extends WSSetUp {
	//Creating Profile to which EMAIL will be inserted
	public String createProfile(String ds) {

		String profileID = "";
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", ds);
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			profileID = CreateProfile.createProfile(ds);
			WSClient.setData("{var_profileID}", profileID);
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return profileID;
	}

	//For Inserting EMAIL into a created profile
	@Test(groups = { "sanity", "Name", "InsertEmail", "OWS" })
	public void insertEmail_31520() {

		try {
			String testName = "insertEmail_31520";
			WSClient.startTest(testName, "Verify that if email is inserted in DB when minimum required data in provided in request.", "sanity");
			String preReq[] = { "CommunicationMethod" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {
				String profileID = createProfile("DS_06");
				if (!profileID.equals("error")) {
					String email = (WSClient.getData("{var_fname}") + "." + WSClient.getData("{var_lname}") + "@oracle.com").toLowerCase();
					WSClient.setData("{var_email}", email);
					String expectedPhoneRole = (OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02")).toUpperCase();
					WSClient.setData("{var_phoneRole}", expectedPhoneRole);

					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

					String insertEmailReq = WSClient.createSOAPMessage("OWSInsertEmail", "DS_01");
					String insertEmailResponseXML = WSClient.processSOAPMessage(insertEmailReq);

					if (WSAssert.assertIfElementValueEquals(insertEmailResponseXML, "InsertEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(insertEmailResponseXML, "Result_IDs_IDPair_operaId", false)) {
							String phoneid = WSClient.getElementValue(insertEmailResponseXML, "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_phoneID}", phoneid);

							//Value from Request and Response
							WSClient.writeToReport(LogStatus.INFO, "<b>Comparing Expected Data sent in Request and Response with Data populated in DB</b>");
							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<>();
							expectedValues.put("NAME ID", profileID);
							expectedValues.put("PHONE ID", phoneid);
							expectedValues.put("PHONE ROLE", expectedPhoneRole);
							expectedValues.put("EMAIL ID", email);

							//Values from DB Actual values 
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> actualValues = WSClient.getDBRow(query);

							//Verifying the values if both are equal 
							WSAssert.assertEquals(expectedValues, actualValues, false);
						}
					}

					if (WSAssert.assertIfElementExists(insertEmailResponseXML, "InsertEmailResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(insertEmailResponseXML, "InsertEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(insertEmailResponseXML, "InsertEmailResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(insertEmailResponseXML, "InsertEmailResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(insertEmailResponseXML, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailResponseXML, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			}

			else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	//For Inserting EMAIL into a created profile
	@Test(groups = { "minimumRegression", "InsertEmail", "OWS", "Name" })
	public void insertEmail_31521() {

		try {

			String testName = "insertEmail_31521";
			WSClient.startTest(testName, "Verify if email is inserted in DB with proper Default values.", "minimumRegression");

			//Check if Pre-requisite is available for this operation
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				String profileID = "";
				profileID = createProfile("DS_06");
				//If Profile is created then 
				if (!profileID.equals("error")) {

					//Create the Email for the profile
					String email = (WSClient.getData("{var_fname}") + "." + WSClient.getData("{var_lname}") + "@oracle.com").toLowerCase();
					WSClient.setData("{var_email}", email);
					String expectedPhoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");
					WSClient.setData("{var_phoneRole}", expectedPhoneRole.toUpperCase());

					//Fetch the Security and Resort data 
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);

					//Setup header for OWS
					OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					WSClient.setData("{var_emailType}", "EMAIL");

					//Perform InsertEmail operation and capture response String.
					String insertEmailReq = WSClient.createSOAPMessage("OWSInsertEmail", "DS_02");
					String insertEmailRes = WSClient.processSOAPMessage(insertEmailReq);

					//Veryfing If the Email is attached to the Profile and the Default Elements are populated as expected 
					if (WSAssert.assertIfElementValueEquals(insertEmailRes, "InsertEmailResponse_Result_resultStatusFlag", "SUCCESS", false))
						if (WSAssert.assertIfElementExists(insertEmailRes, "Result_IDs_IDPair_operaId", false)) {

							//fetching the OperaID for the Email.
							String phoneid = WSClient.getElementValue(insertEmailRes, "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_phoneID}", phoneid);

							//Fetching Data form the DB
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<>();
							String query2 = WSClient.getQuery("QS_02");
							actualValues = WSClient.getDBRow(query2);

							//getting the Expected Values which are to be populated 
							LinkedHashMap<String, String> expectedvalues = new LinkedHashMap<>();
							expectedvalues.put("PHONE_ID", phoneid);
							expectedvalues.put("NAME_ID", profileID);
							expectedvalues.put("PHONE_TYPE", "EMAIL");
							expectedvalues.put("PHONE_ROLE", "EMAIL");
							expectedvalues.put("PHONE_NUMBER", email);
							expectedvalues.put("PRIMARY_YN", "Y");
							if (WSAssert.assertEquals(expectedvalues, actualValues, false))
								WSClient.writeToReport(LogStatus.INFO, "<b>All the default values are correctly populated into DB</b>");
							else WSClient.writeToReport(LogStatus.INFO, "<b>The default values are NOT populated as expected </b>");
						}
					
					if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(insertEmailRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
					
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	//For Inserting EMAIL into a created profile with invalid email address
	@Test(groups = { "minimumRegression", "InsertEmail", "OWS", "Name" })
	public void insertEmail_31522() {

		try {

			String testName = "insertEmail_31522";
			WSClient.startTest(testName, "Verify if error message is populated when trying to insert Invalid EmailID.", "minimumRegression");

			//Check if Pre-requisite is available for this operation
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				String profileID = "";
				profileID = createProfile("DS_06");
				//verifying if Profile is created.
				if (profileID != "") {
					//Create the Email for the profile
					String email = (WSClient.getData("{var_fname}") + "." + WSClient.getData("{var_lname}")).toLowerCase();
					WSClient.setData("{var_email}", email);
					String expectedPhoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");
					WSClient.setData("{var_phoneRole}", expectedPhoneRole);

					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					WSClient.setData("{var_emailType}", "EMAIL");

					String insertEmailReq = WSClient.createSOAPMessage("OWSInsertEmail", "DS_01");
					String insertEmailRes = WSClient.processSOAPMessage(insertEmailReq);

					if (WSAssert.assertIfElementValueEquals(insertEmailRes, "InsertEmailResponse_Result_resultStatusFlag", "FAIL", false)) {
						if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_GDSError", true)) {
							WSAssert.assertIfElementValueEquals(insertEmailRes, "InsertEmailResponse_Result_GDSError", "Invalid email address", false);
							WSClient.writeToReport(LogStatus.INFO, "<b>GDS Error Text: " + WSClient.getElementValue(insertEmailRes, "InsertEmailResponse_Result_GDSError", XMLType.RESPONSE) + "</b>");
						}
						if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_Text", true))
							WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
						if (WSAssert.assertIfElementExists(insertEmailRes, "Result_Text_TextElement", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	//For Inserting EMAIL into a Invalid profile.
	@Test(groups = { "minimumRegression", "InsertEmail", "OWS", "Name" })
	public void insertEmail_31523() {

		try {
			String testName = "insertEmail_31523";
			WSClient.startTest(testName, "Verify FAIL result status flag is populated when Invalid profileID is sent.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				//Setting profileID to some Invalid data
				WSClient.setData("{var_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
				String email = (WSClient.getKeywordData("{KEYWORD_RANDSTR_10}") + "@oracle.com").toLowerCase();
				WSClient.setData("{var_email}", email);

				//Fetch the Security and Resort data 
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);

				//Setup header for OWS
				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_emailType}", "EMAIL");
				WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02"));
				WSClient.setData("{var_primary}", "true");

				//Perform InsertEmail operation and capture response String.
				String insertEmailReq = WSClient.createSOAPMessage("OWSInsertEmail", "DS_03");
				String insertEmailRes = WSClient.processSOAPMessage(insertEmailReq);
				if (WSAssert.assertIfElementValueEquals(insertEmailRes, "InsertEmailResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_Text", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
				}
				
				if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", true)) {
					String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_GDSError", true)) {
					String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(insertEmailRes, "Result_Text_TextElement", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
				}
				

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	//primary case 

	@Test(groups = { "minimumRegression", "InsertEmail", "OWS", "Name" })
	public void insertEmail_40304() {

		try {
			String testName = "insertEmail_40304";
			WSClient.startTest(testName, "Verify that Email is set as Primary when Primary is set to Y in request.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				//Fetch the Security and Resort data 
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);

				//Setup header for OWS
				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_emailType}", "EMAIL");
				//		            Might need to change later
				//		            WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod","DS_02"));
				WSClient.setData("{var_phoneRole}", "EMAIL");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chainCode}", chain);
				//Create Profile.
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				String email = fname + "." + lname + "@oracle.com";
				WSClient.setData("{var_email}", email);
				String profileID = CreateProfile.createProfile("DS_06");
				WSClient.setData("{var_profileID}", profileID);

				//Perform InsertEmail operation and capture response String.
				String insertEmailReq = WSClient.createSOAPMessage("OWSInsertEmail", "DS_02");
				String insertEmailRes = WSClient.processSOAPMessage(insertEmailReq);
				if (WSAssert.assertIfElementValueEquals(insertEmailRes, "InsertEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					String query = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> value = WSClient.getDBRow(query);
					String primaryVal = value.get("PRIMARY_YN");

					if (primaryVal.contains("Y")) {
						WSClient.writeToReport(LogStatus.PASS, "The Email is set as Primary in the DataBase");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "The Email is NOT set to Primary in DataBase");
					}
				}
				
				if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", true)) {
					String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_GDSError", true)) {
					String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(insertEmailRes, "Result_Text_TextElement", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "InsertEmail", "OWS", "Name" })
	public void insertEmail_40307() {

		try {
			String testName = "insertEmail_40307";
			WSClient.startTest(testName, "Verify that when new primary email is added,Primary for old email is set to 'N'.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				//Fetch the Security and Resort data 
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);

				//Setup header for OWS
				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_phoneRole}", "EMAIL");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chainCode}", chain);
				//Create Profile.
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				String email = fname + "." + lname + "@oracle.com";
				WSClient.setData("{var_email}", email);
				String profileID = CreateProfile.createProfile("DS_06");
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_primary}", "false");

				//Perform InsertEmail operation and capture response String.
				String insertEmailReq = WSClient.createSOAPMessage("OWSInsertEmail", "DS_03");
				String insertEmailRes = WSClient.processSOAPMessage(insertEmailReq);

				email = fname + "." + lname + "1@oracle.com";
				WSClient.setData("{var_email}", email);
				WSClient.setData("{var_primary}", "true");

				if (WSAssert.assertIfElementValueEquals(insertEmailRes, "InsertEmailResponse_Result_resultStatusFlag", "SUCCESS", true)) {

					insertEmailReq = WSClient.createSOAPMessage("OWSInsertEmail", "DS_03");
					insertEmailRes = WSClient.processSOAPMessage(insertEmailReq);

					if (WSAssert.assertIfElementValueEquals(insertEmailRes, "InsertEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						String query = WSClient.getQuery("QS_03");

						ArrayList<LinkedHashMap<String, String>> value = WSClient.getDBRows(query);
						if (value.get(0).get("PRIMARY_YN").contains("N") && value.get(1).get("PRIMARY_YN").contains("Y")) {
							WSClient.writeToReport(LogStatus.PASS, "The Email is set as Primary in the DataBase");
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "The Email is NOT set to Primary in DataBase");
						}
					}
					
					if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(insertEmailRes, "InsertEmailResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(insertEmailRes, "InsertEmailResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(insertEmailRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(insertEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
					
				} else WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed : First Email Setup Failed**********");

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

}
