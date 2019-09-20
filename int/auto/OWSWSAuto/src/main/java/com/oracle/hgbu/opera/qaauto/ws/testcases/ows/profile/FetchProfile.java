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

/**
 *
 * @author kankur
 *
 */

public class FetchProfile extends WSSetUp {
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

	// Fetch Profile Sanity, Creating Basic Profile with First Name and Last
	// Name only and then Fetching it.
	@Test(groups = { "sanity", "Name", "FetchProfile", "OWS","fetchProfile_38620" })
	public void fetchProfile_38620() {
		try {
			String testName = "fetchProfile_38620";
			WSClient.startTest(testName, "Verify that the Profile is looked up when name id is passed", "sanity");

			String chain = OPERALib.getChain();
			String resort = OPERALib.getResort();
			// String channel = OWSLib.getChannel();
			String uname = OPERALib.getUserName();
			// String pwd = OPERALib.getPassword();
			// String channelType = OWSLib.getChannelType(channel);
			// String channelCarrier = OWSLib.getChannelCarier(resort,
			// channelType);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

			if (!operaProfileID.equals("error")) {
				System.out.println(operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);

				setOWSHeader();
				// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
				// channelCarrier);

				String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
				String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchProfileRes,
							"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchProfileRes, "FetchProfileResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_resultStatusFlag",
						true)) {
					if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
							"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchProfileRes, "ProfileDetails_ProfileIDs_UniqueID",
								false)) {

							WSClient.setData("{var_profileID}", operaProfileID);

							// Fetching Record from DB
							String QS_01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);

							// Fetching Record from Response
							String first = WSClient.getElementValue(fetchProfileRes, "Customer_PersonName_firstName",
									XMLType.RESPONSE);
							String last = WSClient.getElementValue(fetchProfileRes, "Customer_PersonName_lastName",
									XMLType.RESPONSE);
							String nameId = WSClient.getElementValueByAttribute(fetchProfileRes,
									"ProfileDetails_ProfileIDs_UniqueID", "ProfileDetails_ProfileIDs_UniqueID_type",
									"INTERNAL", XMLType.RESPONSE);

							LinkedHashMap<String, String> actualRecord = new LinkedHashMap<String, String>();
							actualRecord.put("FIRST NAME", first);
							actualRecord.put("LAST NAME", last);
							actualRecord.put("NAME ID", nameId);

							// Validating the fetched profile values from RES
							// with the
							// Database
							WSAssert.assertEquals(expectedRecord, actualRecord, false);
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
						}
					}

					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(fetchProfileRes,
								"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Profile MR-1, Creating Profile with First Name, Last Name, Address
	// and then Fetching it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39380() {
		try {
			String testName = "fetchProfile_39380";
			WSClient.startTest(testName, "Verify that the Address details are fetched correctly", "minimumRegression");
			String prerequisite[] = { "AddressType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for the required profile creation
				String resortOperaValue = OPERALib.getResort();
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
				HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_addressType}", addressType);
				WSClient.setData("{var_city}", fullAddress.get("City"));
				WSClient.setData("{var_zip}", fullAddress.get("Zip"));
				WSClient.setData("{var_state}", fullAddress.get("State"));
				WSClient.setData("{var_country}", fullAddress.get("Country"));

				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_04");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);

					String QS_03 = WSClient.getQuery("QS_03");
					String addressid = WSClient.getDBRow(QS_03).get("ADDRESS_ID");
					if (!addressid.equals("")) {
						setOWSHeader();
						// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
						// channelCarrier);

						String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
						String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

						// Checking for Text Element in Result
						if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

							String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is :" + message + "</b>");
						}

						// Checking For OperaErrorCode
						if (WSAssert.assertIfElementExists(fetchProfileRes,
								"FetchProfileResponse_Result_OperaErrorCode", true)) {
							String code = WSAssert.getElementValue(fetchProfileRes,
									"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The error code displayed in the response is :" + code + "</b>");
						}

						// Checking For Fault Schema
						if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
							String message = WSClient.getElementValue(fetchProfileRes,
									"FetchProfileResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
						}

						// Checking For Result Flag
						if (WSAssert.assertIfElementExists(fetchProfileRes,
								"FetchProfileResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
									"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(fetchProfileRes,
										"ProfileDetails_ProfileIDs_UniqueID", false)) {

									WSClient.setData("{var_profileID}", operaProfileID);

									// Fetching Record from DB
									String QS_02 = WSClient.getQuery("QS_02");
									LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_02);

									// Fetching Record from Response
									String first = WSClient.getElementValue(fetchProfileRes,
											"Customer_PersonName_firstName", XMLType.RESPONSE);
									String last = WSClient.getElementValue(fetchProfileRes,
											"Customer_PersonName_lastName", XMLType.RESPONSE);
									String nameId = WSClient.getElementValueByAttribute(fetchProfileRes,
											"ProfileDetails_ProfileIDs_UniqueID",
											"ProfileDetails_ProfileIDs_UniqueID_type", "INTERNAL", XMLType.RESPONSE);
									String adType = WSClient.getElementValue(fetchProfileRes,
											"ProfileDetails_Addresses_NameAddress_addressType", XMLType.RESPONSE);
									String cityName = WSClient.getElementValue(fetchProfileRes,
											"Addresses_NameAddress_cityName", XMLType.RESPONSE);
									String stateProv = WSClient.getElementValue(fetchProfileRes,
											"Addresses_NameAddress_stateProv", XMLType.RESPONSE);
									String countryCode = WSClient.getElementValue(fetchProfileRes,
											"Addresses_NameAddress_countryCode", XMLType.RESPONSE);
									String postalCode = WSClient.getElementValue(fetchProfileRes,
											"Addresses_NameAddress_postalCode", XMLType.RESPONSE);

									// Fetching AddressLines from Response into
									// a HashMap
									HashMap<String, String> xPath = new HashMap<String, String>();
									xPath.put("Addresses_NameAddress_AddressLine",
											"ProfileDetails_Addresses_NameAddress");
									// LinkedHashMap<String,String> actualRecord
									// =
									// WSClient.getSingleNodeList(fetchProfileRes,
									// xPath, false, XMLType.RESPONSE);
									LinkedHashMap<String, String> actualRecord = WSClient
											.getMultipleNodeList(fetchProfileRes, xPath, false, XMLType.RESPONSE)
											.get(0);
									// Now adding FirstName, LastName ,NameID,
									// State, City, PostalCode, AddressType into
									// above HashMap
									actualRecord.put("FirstName", first);
									actualRecord.put("LastName", last);
									actualRecord.put("NameID", nameId);
									actualRecord.put("State", stateProv);
									actualRecord.put("Country", countryCode);
									actualRecord.put("City", cityName);
									actualRecord.put("PostalCode", postalCode);
									actualRecord.put("AddressType", adType);

									// Validating the fetched profile values
									// from RES with the
									// Database
									WSAssert.assertEquals(expectedRecord, actualRecord, false);
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
								}
							}

							// Checking if ResultstatusFlag is FAIL
							else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
							}

							// Checking for GDSError
							if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError",
									true)) {
								String message = WSAssert.getElementValue(fetchProfileRes,
										"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"The error displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Result Status Flag Itself does not Exist in the response!");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Address failed!------ Create Profile -----Blocked");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Profile MR-2, Creating Profile with First Name, Last Name, Phone
	// Information and then Fetching it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39381() {
		try {
			String testName = "fetchProfile_39381";
			WSClient.startTest(testName, "Verify that the Phone Information are fetched correctly",
					"minimumRegression");
			String prerequisite[] = { "CommunicationType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for the required profile creation
				String resortOperaValue = OPERALib.getResort();
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String phoneType = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
				String phoneType2 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_phoneType}", phoneType);
				WSClient.setData("{var_phoneType2}", phoneType2);

				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_09");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);

					String QS_04 = WSClient.getQuery("QS_04");
					List<LinkedHashMap<String, String>> phn = WSClient.getDBRows(QS_04);
					if (phn.size() >= 2) {
						setOWSHeader();
						// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
						// channelCarrier);

						String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
						String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

						// Checking for Text Element in Result
						if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

							String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is :" + message + "</b>");
						}

						// Checking For OperaErrorCode
						if (WSAssert.assertIfElementExists(fetchProfileRes,
								"FetchProfileResponse_Result_OperaErrorCode", true)) {
							String code = WSAssert.getElementValue(fetchProfileRes,
									"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The error code displayed in the response is :" + code + "</b>");
						}

						// Checking For Fault Schema
						if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
							String message = WSClient.getElementValue(fetchProfileRes,
									"FetchProfileResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
						}

						// Checking For Result Flag
						if (WSAssert.assertIfElementExists(fetchProfileRes,
								"FetchProfileResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
									"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(fetchProfileRes,
										"ProfileDetails_ProfileIDs_UniqueID", false)) {

									WSClient.setData("{var_profileID}", operaProfileID);

									// Fetching Record from DB
									String QS_03 = WSClient.getQuery("QS_03");
									List<LinkedHashMap<String, String>> expectedRecords = WSClient.getDBRows(QS_03);

									// Fetching Record from Response
									HashMap<String, String> xPath = new HashMap<String, String>();
									xPath.put("Phones_NamePhone_PhoneNumber", "ProfileDetails_Phones_NamePhone");
									xPath.put("ProfileDetails_Phones_NamePhone_phoneType",
											"ProfileDetails_Phones_NamePhone");
									xPath.put("ProfileDetails_Phones_NamePhone_primary",
											"ProfileDetails_Phones_NamePhone");
									List<LinkedHashMap<String, String>> actualRecords = WSClient
											.getMultipleNodeList(fetchProfileRes, xPath, false, XMLType.RESPONSE);
									actualRecords = WSClient.getMultipleNodeList(fetchProfileRes, xPath, false,
											XMLType.RESPONSE);

									// Validating the fetched profile values
									// from RES with the
									// Database

									WSAssert.assertEquals(actualRecords, expectedRecords, false);
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
								}
							}

							// Checking if ResultstatusFlag is FAIL
							else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
							}

							// Checking for GDSError
							if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError",
									true)) {
								String message = WSAssert.getElementValue(fetchProfileRes,
										"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"The error displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Result Status Flag Itself does not Exist in the response!");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for attaching to phone numbers to a profile failed! -----Blocked");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Communication Type not available -----Blocked");
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Profile MR-3, Creating Profile with Preference and then fetching
	// it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39382() {
		try {
			String testName = "fetchProfile_39382";
			WSClient.startTest(testName,
					"Verify that the guest preference(s) are being retrieved correctly when the FetchProfile call is issued with a valid Profile Identifier",
					"minimumRegression");
			String[] prerequisites = { "PreferenceGroup", "PreferenceCode" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisites)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for the required profile creation
				String resortOperaValue = OPERALib.getResort();
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);

				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_06");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);

					// Getting PreferenceType(Group) and PreferenceValue(Code)
					// from propertySetUp file
					String prefType1 = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");
					String prefType2 = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02");
					String prefValue1 = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");
					String prefValue2 = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02");

					// Setting the Variables for the Datasheet(for
					// CreatePreference Datasheet)
					WSClient.setData("{var_prefType}", prefType1);
					WSClient.setData("{var_prefValue}", prefValue1);

					// For Create Preference; Setting variable Global as 'true'
					// means it This Created Preference will be available for
					// All the Properties and 'false' means that This Created
					// Preference will be available for the Current Property
					// QS_04 will have resort value if it is NULL means make
					// Preference as true else false
					String QS_04 = WSClient.getQuery("OWSFetchProfile", "QS_04");
					LinkedHashMap<String, String> prefGlobal = WSClient.getDBRow(QS_04);
					if (prefGlobal.size() == 0) {
						WSClient.setData("{var_global}", "true");
					} else {
						WSClient.setData("{var_global}", "false");
					}

					WSClient.setData("{var_prefDesc}", prefType1 + " " + prefValue1);

					// Creating Preference for the above operaProfileID
					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
					String createPreferenceRes = WSClient.processSOAPMessage(createPreferenceReq);

					if (WSAssert.assertIfElementExists(createPreferenceRes, "CreatePreferenceRS_Success", true)) {
						String QS01 = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> preference = WSClient.getDBRow(QS01);
						if (preference.size() >= 1) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "Successfully created 1st Preference for the Profile" + "</b>");
							// WSClient.setData("{var_prefType}",WSClient.getDataSetForCode("PreferenceGroup",
							// "DS_02"));
							// WSClient.setData("{var_prefValue}",WSClient.getDataSetForCode("PreferenceCode",
							// "DS_02"));
							WSClient.setData("{var_prefType}", prefType2);
							WSClient.setData("{var_prefValue}", prefValue2);

							String QS04 = WSClient.getQuery("OWSFetchProfile", "QS_04");
							LinkedHashMap<String, String> prefGlobal1 = WSClient.getDBRow(QS04);
							if (prefGlobal1.size() == 0) {
								WSClient.setData("{var_global}", "true");
							} else {
								WSClient.setData("{var_global}", "false");
							}
							WSClient.setData("{var_prefDesc}", prefType2 + " " + prefValue2);

							// Creating Another Preference for the above
							// operaProfileID
							createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
							createPreferenceRes = WSClient.processSOAPMessage(createPreferenceReq);

							if (WSAssert.assertIfElementExists(createPreferenceRes, "CreatePreferenceRS_Success",
									true)) {
								String QS1 = WSClient.getQuery("QS_01");
								preference = WSClient.getDBRow(QS1);
								if (preference.size() >= 1) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "Successfully created 2nd Preference for the Profile" + "</b>");
									// Validation request being created and
									// processed to generate response

									setOWSHeader();
									// OWSLib.setOWSHeader(uname, pwd, resort,
									// channelType, channelCarrier);

									String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
									String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

									// Checking For Result Flag
									if (WSAssert.assertIfElementExists(fetchProfileRes,
											"FetchProfileResponse_Result_resultStatusFlag", true)) {
										if (WSAssert.assertIfElementExists(fetchProfileRes,
												"ProfileDetails_ProfileIDs_UniqueID", false)) {
											if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
													"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
												List<LinkedHashMap<String, String>> expectedValues = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xPath = new HashMap<String, String>();

												// xPaths of the records being
												// verified and their parents
												// are being put in a hashmap
												xPath.put("ProfileDetails_Preferences_Preference_preferenceType",
														"ProfileDetails_Preferences_Preference");
												xPath.put("ProfileDetails_Preferences_Preference_preferenceValue",
														"ProfileDetails_Preferences_Preference");

												// database records are being
												// stored in a list of hashmaps
												String QS05 = WSClient.getQuery("QS_05");
												expectedValues = WSClient.getDBRows(QS05);

												// response records are being
												// stored in a list of hashmaps
												actualValues = WSClient.getMultipleNodeList(fetchProfileRes, xPath,
														false, XMLType.RESPONSE);

												// database records and response
												// records are being compared
												WSAssert.assertEquals(actualValues, expectedValues, false);
											}
											// Checking if ResultstatusFlag is
											// FAIL
											else {
												WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unique ID not available in response");
										}
									}

									// Checking For OperaErrorCode
									if (WSAssert.assertIfElementExists(fetchProfileRes,
											"FetchProfileResponse_Result_OperaErrorCode", true)) {
										String code = WSAssert.getElementValue(fetchProfileRes,
												"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"
												+ "The error code displayed in the response is :" + code + "</b>");
									}

									// Checking for Text Element in Result
									if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement",
											true)) {

										String message = WSAssert.getElementValue(fetchProfileRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>" + "The text displayed in the response is :" + message + "</b>");
									}

									// Checking For Fault Schema
									if (WSAssert.assertIfElementExists(fetchProfileRes,
											"FetchProfileResponse_faultcode", true)) {
										String message = WSClient.getElementValue(fetchProfileRes,
												"FetchProfileResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL,
												"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"The prerequisites for Preference Type and Value were not added to the profile");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"The prerequisites for Preference Type and Value failed!------ Create Preference -----Blocked");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites for Preference Type and Value were not added to the profile");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Preference Type and Value failed!------ Create Preference -----Blocked");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ PreferenceGroup and PreferenceCode not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Profile MR-4, Creating Profile with Internal Comment and then
	// fetching to see that internal comments are not fetched in the response.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39383() {
		try {
			String testName = "fetchProfile_39383";
			WSClient.startTest(testName,
					"Verify that the internal comments are not being fetched by the fetchProfile operation",
					"minimumRegression");
			// String[] prerequisites={"NoteType"};
			// if(OperaPropConfig.getPropertyConfigResults(prerequisites))
			// {
			// Means Required prerequisite are fulfilled
			// Getting Required values for the required profile creation
			// String resortOperaValue = "IDC10PRO";
			String resortOperaValue = OPERALib.getResort();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			// String commentType =
			// OperaPropConfig.getDataSetForCode("NoteType", "DS_01");
			/*
			 * The commentType should be "OWS" for comment to be considered in
			 * OWS
			 */
			// String commentType = "OWS";
			String comment = "This is Some Web Note";

			// Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			// WSClient.setData("{var_commentType}", commentType);
			WSClient.setData("{var_comment}", comment);

			String chain = OPERALib.getChain();
			// String resort = "IDC10PRO";
			String resort = OPERALib.getResort();
			// String channel = OWSLib.getChannel();
			String uname = OPERALib.getUserName();
			// String pwd = OPERALib.getPassword();
			// String channelType = OWSLib.getChannelType(channel);
			// String channelCarrier = OWSLib.getChannelCarier(resort,
			// channelType);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String operaProfileID = CreateProfile.createProfile("DS_07");
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

			if (!operaProfileID.equals("error")) {
				System.out.println(operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);

				// Checking NAME$NOTES table is populated with the above
				// profileID
				String QS11 = WSClient.getQuery("QS_11");
				String commentId = WSClient.getDBRow(QS11).get("NOTE_ID");
				if (!commentId.equals("")) {
					// Validation request being created and processed to
					// generate response

					setOWSHeader();
					// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
					// channelCarrier);

					String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
					String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(fetchProfileRes,
								"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
						String message = WSClient.getElementValue(fetchProfileRes, "FetchProfileResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
					}

					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_resultStatusFlag",
							true)) {
						if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(fetchProfileRes, "ProfileDetails_ProfileIDs_UniqueID",
									false)) {

								if (WSAssert.assertIfElementExists(fetchProfileRes,
										"ProfileDetails_Comments_Comment_commentType", true)) {
									// Validation points is being fetched from
									// response
									String commentText = WSClient.getElementValue(fetchProfileRes,
											"Comment_Text_TextElement", XMLType.RESPONSE);
									if (commentText.equals(comment)) {
										WSClient.writeToReport(LogStatus.FAIL, "Internal Comments are displayed");
									} else {
										WSClient.writeToReport(LogStatus.PASS, "Internal Comments are not displayed");
									}
								} else {
									WSClient.writeToReport(LogStatus.PASS, "Internal Comments are not displayed");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
							}
						}
						// Checking if ResultstatusFlag is FAIL
						else {
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
						}
						// Checking for GDSError
						if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError",
								true)) {
							String message = WSAssert.getElementValue(fetchProfileRes,
									"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The error displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Result Status Flag Itself does not Exist in the response!");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for comment failed!------ Create Profile -----Blocked");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
			}
			// }
			// else
			// {
			// WSClient.writeToReport(LogStatus.WARNING, "The prerequisites
			// failed!------ NoteType not available -----Blocked");
			// }
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Profile MR-5, Creating Profile with customer demographics and
	// document details and then fetching it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39384() {
		try {
			String prerequisite[] = { "Title", "IdentificationType", "VipLevel" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				String testName = "fetchProfile_39384";
				WSClient.startTest(testName,
						"Verify that the customer demographics and document details are being retrieved correctly when the FetchProfile call is issued with a valid Profile Identifier",
						"minimumRegression");

				// Means Required prerequisite are fulfilled
				// Getting Required values for the required profile creation
				String resortOperaValue = OPERALib.getResort();
				String nameTitle = OperaPropConfig.getDataSetForCode("Title", "DS_01");
				String documentType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				String vipLevel = OperaPropConfig.getDataSetForCode("VipLevel", "DS_01");
				HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();
				String countryCode = fullAddress.get("Country");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_nameTitle}", nameTitle);
				WSClient.setData("{var_documentType}", documentType);
				WSClient.setData("{var_documentNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));
				WSClient.setData("{var_documentCountry}", countryCode);
				WSClient.setData("{var_documentDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_300}"));
				WSClient.setData("{var_birthDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_8395}"));
				WSClient.setData("{var_vipCode}", vipLevel);

				// Prerequisite 1: Create Profile 1
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_26");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					// Prerequisite 2: Change Profile
					WSClient.setData("{var_profileID}", operaProfileID);
					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_10");
					String changeProfileRes = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileRes, "ChangeProfileRS_Success", false)) {

						String QS01 = WSClient.getQuery("QS_01");
						String documentId = WSClient.getDBRow(QS01).get("DOCUMENT_ID");
						if (!documentId.equals("")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "Successfully added document details to the Profile" + "</b>");

							setOWSHeader();
							// OWSLib.setOWSHeader(uname, pwd, resort,
							// channelType, channelCarrier);

							String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
							String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

							// Checking for Text Element in Result
							if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

								String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text displayed in the response is :" + message + "</b>");
							}

							// Checking For OperaErrorCode
							if (WSAssert.assertIfElementExists(fetchProfileRes,
									"FetchProfileResponse_Result_OperaErrorCode", true)) {
								String code = WSAssert.getElementValue(fetchProfileRes,
										"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The error code displayed in the response is :" + code + "</b>");
							}

							// Checking For Fault Schema
							if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(fetchProfileRes,
										"FetchProfileResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
							}

							// Checking For Result Flag
							if (WSAssert.assertIfElementExists(fetchProfileRes,
									"FetchProfileResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
										"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									if (WSAssert.assertIfElementExists(fetchProfileRes,
											"ProfileDetails_ProfileIDs_UniqueID", false)) {

										WSClient.setData("{var_profileID}", operaProfileID);

										LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
										LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
										HashMap<String, String> xPath = new HashMap<String, String>();

										// database records are being stored in
										// a hashmap
										String QS06 = WSClient.getQuery("QS_06");
										expectedValues = WSClient.getDBRow(QS06);

										// xPaths of the records being verified
										// and their parents are being put in a
										// hashmap
										xPath.put("Customer_PersonName_firstName",
												"ProfileDetails_Customer_PersonName");
										xPath.put("Customer_PersonName_middleName",
												"ProfileDetails_Customer_PersonName");
										xPath.put("Customer_PersonName_lastName", "ProfileDetails_Customer_PersonName");
										xPath.put("FetchProfileResponse_ProfileDetails_Id_documentType",
												"FetchProfileResponse_ProfileDetails_Id");
										xPath.put("FetchProfileResponse_ProfileDetails_Id_documentNumber",
												"FetchProfileResponse_ProfileDetails_Id");
										// xPath.put("Customer_GovernmentIDList_GovernmentID_effectiveDate","Customer_GovernmentIDList_GovernmentID");
										// xPath.put("Customer_GovernmentIDList_GovernmentID_countryOfIssue","Customer_GovernmentIDList_GovernmentID");
										xPath.put("FetchProfileResponse_ProfileDetails_Customer_birthDate",
												"FetchProfileResponse_ProfileDetails_Customer");
										xPath.put("Customer_PersonName_nameTitle",
												"ProfileDetails_Customer_PersonName");
										xPath.put("FetchProfileResponse_ProfileDetails_vipCode",
												"FetchProfileResponse_ProfileDetails");

										// response records are being stored in
										// a hashmap
										actualValues = WSClient.getSingleNodeList(fetchProfileRes, xPath, false,
												XMLType.RESPONSE);
										String docNo = actualValues.get("documentNumber1");
										if (actualValues.containsKey("documentNumber1")) {
											if ((actualValues.get("documentNumber1")).startsWith("//")) {

											} else {
												actualValues.put("documentNumber1",
														"XXXXXX" + docNo.substring(docNo.length() - 2));
											}
										}
										//
										// database records and response records
										// are being compared
										WSAssert.assertEquals(expectedValues, actualValues, false);

									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Unique ID not available in response");
									}
								}

								// Checking if ResultstatusFlag is FAIL
								else {
									WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
								}

								// Checking for GDSError
								if (WSAssert.assertIfElementExists(fetchProfileRes,
										"FetchProfileResponse_Result_GDSError", true)) {
									String message = WSAssert.getElementValue(fetchProfileRes,
											"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL,
											"The error displayed in the response is :" + message);
								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Result Status Flag Itself does not Exist in the response!");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites for Idenification ID's failed!------ Change Profile -----Blocked");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites  failed!------ Change Profile -----Blocked");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------Title, IdentificationType, VipLevel not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	// Fetch Profile MR-6, Creating Profile with privacy options and then
	// fetching it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39385() {
		try {
			String testName = "fetchProfile_39385";
			WSClient.startTest(testName, "Verify that privacy options are retrieved onto the response",
					"minimumRegression");

			// Getting Required values for profile creation
			String resortOperaValue = OPERALib.getResort();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			// Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			// Prerequisite 1: Create Profile 1
			String chain = OPERALib.getChain();
			String resort = OPERALib.getResort();
			// String channel = OWSLib.getChannel();
			String uname = OPERALib.getUserName();
			// String pwd = OPERALib.getPassword();
			// String channelType = OWSLib.getChannelType(channel);
			// String channelCarrier = OWSLib.getChannelCarier(resort,
			// channelType);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String operaProfileID = CreateProfile.createProfile("DS_05");
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

			if (!operaProfileID.equals("error")) {
				System.out.println(operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);

				// Validation request being created and processed to generate
				// response
				setOWSHeader();
				// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
				// channelCarrier);

				String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
				String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchProfileRes,
							"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchProfileRes, "FetchProfileResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_resultStatusFlag",
						true)) {
					if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
							"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(fetchProfileRes, "ProfileDetails_ProfileIDs_UniqueID",
								false)) {
							WSClient.setData("{var_profileID}", operaProfileID);

							// Validation points are being fetched from the
							// response
							// String
							// mailValue=WSClient.getElementValueByAttribute(fetchProfileRes,
							// "ProfileDetails_Privacy_PrivacyOption_OptionValue",
							// "ProfileDetails_Privacy_PrivacyOption_OptionType",
							// "Mail", XMLType.RESPONSE);
							String emailValue = WSClient.getElementValueByAttribute(fetchProfileRes,
									"ProfileDetails_Privacy_PrivacyOption_OptionValue",
									"ProfileDetails_Privacy_PrivacyOption_OptionType", "Email", XMLType.RESPONSE);
							// String
							// privacyValue=WSClient.getElementValueByAttribute(fetchProfileRes,
							// "ProfileDetails_Privacy_PrivacyOption_OptionValue",
							// "ProfileDetails_Privacy_PrivacyOption_OptionType",
							// "Privacy", XMLType.RESPONSE);
							String marketResearch = WSClient.getElementValueByAttribute(fetchProfileRes,
									"ProfileDetails_Privacy_PrivacyOption_OptionValue",
									"ProfileDetails_Privacy_PrivacyOption_OptionType", "MarketResearch",
									XMLType.RESPONSE);
							String phoneyn = WSClient.getElementValueByAttribute(fetchProfileRes,
									"ProfileDetails_Privacy_PrivacyOption_OptionValue",
									"ProfileDetails_Privacy_PrivacyOption_OptionType", "Phone", XMLType.RESPONSE);
							String smsyn = WSClient.getElementValueByAttribute(fetchProfileRes,
									"ProfileDetails_Privacy_PrivacyOption_OptionValue",
									"ProfileDetails_Privacy_PrivacyOption_OptionType", "SMS", XMLType.RESPONSE);
							String thirdPartyyn = WSClient.getElementValueByAttribute(fetchProfileRes,
									"ProfileDetails_Privacy_PrivacyOption_OptionValue",
									"ProfileDetails_Privacy_PrivacyOption_OptionType", "ThirdParties",
									XMLType.RESPONSE);

							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> dbValues = new LinkedHashMap<String, String>();

							// Database records are being stored in a hashmap
							String QS07 = WSClient.getQuery("QS_07");
							dbValues = WSClient.getDBRow(QS07);

							actualValues.put("EMAIL_YN", emailValue);
							actualValues.put("MARKET_RESEARCH_YN", marketResearch);
							actualValues.put("PHONE_YN", phoneyn);
							actualValues.put("SMS_YN", smsyn);
							actualValues.put("THIRD_PARTY_YN", thirdPartyyn);

							// database records and response records are being
							// compared
							WSAssert.assertEquals(dbValues, actualValues, false);
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
						}
					}

					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(fetchProfileRes,
								"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	// Fetch Profile MR-7, Creating Profile with Comments and then checking that
	// comments are fetched in the response.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39386() {
		try {
			String testName = "fetchProfile_39386";
			WSClient.startTest(testName,
					"Verify that the Comments are being retrieved correctly when the FetchProfile call is issued with a valid Profile Identifier",
					"minimumRegression");
			// String[] prerequisites={"NoteType"};
			// if(OperaPropConfig.getPropertyConfigResults(prerequisites))
			// {
			// Means Required prerequisite are fulfilled
			// Getting Required values for the required profile creation
			String resortOperaValue = OPERALib.getResort();
			// String resortOperaValue = "IDC10PRO";
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			/*
			 * Only "OWS" type external comments will be fetched in the response
			 */
			// String commentType1 =
			// OperaPropConfig.getDataSetForCode("NoteType", "DS_01");
			// String commentType2 =
			// OperaPropConfig.getDataSetForCode("NoteType", "DS_02");
			// String commentType1 = "OWS";
			// String commentType2 = "OWS";
			String comment1 = "This is first Web Note";
			String comment2 = "This is second Web Note";

			// Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			// WSClient.setData("{var_commentType}", commentType1);
			WSClient.setData("{var_comment}", comment1);
			// WSClient.setData("{var_commentType2}", commentType2);
			WSClient.setData("{var_comment2}", comment2);

			String chain = OPERALib.getChain();
			// String resort = "IDC10PRO";
			String resort = OPERALib.getResort();
			// String channel = OWSLib.getChannel();
			String uname = OPERALib.getUserName();
			// String pwd = OPERALib.getPassword();
			// String channelType = OWSLib.getChannelType(channel);
			// String channelCarrier = OWSLib.getChannelCarier(resort,
			// channelType);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String operaProfileID = CreateProfile.createProfile("DS_44");
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

			if (!operaProfileID.equals("error")) {
				System.out.println(operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);

				// Checking NAME$NOTES table is populated with the above
				// profileID
				String QS05 = WSClient.getQuery("QS_05");
				LinkedHashMap<String, String> commentId = WSClient.getDBRow(QS05);
				if (commentId.size() >= 2) {
					// Validation request being created and processed to
					// generate response

					setOWSHeader();
					// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
					// channelCarrier);

					String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
					String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(fetchProfileRes,
								"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
						String message = WSClient.getElementValue(fetchProfileRes, "FetchProfileResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
					}

					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_resultStatusFlag",
							true)) {
						if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(fetchProfileRes, "ProfileDetails_ProfileIDs_UniqueID",
									false)) {

								List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
								List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

								HashMap<String, String> xPath = new HashMap<String, String>();

								// database records are being stored in a list
								// of hashmaps
								String QS08 = WSClient.getQuery("QS_08");
								db = WSClient.getDBRows(QS08);

								// xPaths of the records being verified and
								// their parents are being put in a hashmap
								xPath.put("Comment_Text_TextElement", "ProfileDetails_Comments_Comment");
								xPath.put("ProfileDetails_Comments_Comment_commentType",
										"ProfileDetails_Comments_Comment");

								// response records are being stored in a list
								// of hashmaps
								actualValues = WSClient.getMultipleNodeList(fetchProfileRes, xPath, false,
										XMLType.RESPONSE);

								// database records and response records are
								// being compared
								if (WSAssert.assertEquals(actualValues, db, false)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "Expected Comments are fetched in the response!" + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "Expected Comments not fetched in the response!" + "</b>");
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
							}
						}
						// Checking if ResultstatusFlag is FAIL
						else {
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
						}
						// Checking for GDSError
						if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError",
								true)) {
							String message = WSAssert.getElementValue(fetchProfileRes,
									"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The error displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Result Status Flag Itself does not Exist in the response!");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for comment failed!------ Create Profile -----Blocked");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
			}
			// }
			// else
			// {
			// WSClient.writeToReport(LogStatus.WARNING, "The prerequisites
			// failed!------ NoteType not available -----Blocked");
			// }
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Profile MR-8, Creating Profile with Membership Details and Checking
	// it is fetched in the response.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39387() {
		try {
			String testName = "fetchProfile_39387";
			WSClient.startTest(testName, "Verify that the Membership details are fetched correctly",
					"minimumRegression");
			String[] prerequisites = { "MembershipType", "MembershipLevel" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisites)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for the required profile creation
				String resortOperaValue = OPERALib.getResort();
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String memNo = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_memNo}", memNo);

				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_06");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);

					WSClient.setData("{var_membershipLevel}",
							OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));
					WSClient.setData("{var_membershipType}",
							OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
					String createMembershipResXML = WSClient.processSOAPMessage(createMembershipReq);
					if (WSAssert.assertIfElementExists(createMembershipResXML, "CreateMembershipRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "Successfully created Membership for the Profile" + "</b>");

						setOWSHeader();
						// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
						// channelCarrier);

						String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
						String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

						// Checking for Text Element in Result
						if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

							String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is :" + message + "</b>");
						}

						// Checking For OperaErrorCode
						if (WSAssert.assertIfElementExists(fetchProfileRes,
								"FetchProfileResponse_Result_OperaErrorCode", true)) {
							String code = WSAssert.getElementValue(fetchProfileRes,
									"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The error code displayed in the response is :" + code + "</b>");
						}

						// Checking For Fault Schema
						if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
							String message = WSClient.getElementValue(fetchProfileRes,
									"FetchProfileResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
						}

						// Checking For Result Flag
						if (WSAssert.assertIfElementExists(fetchProfileRes,
								"FetchProfileResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
									"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(fetchProfileRes,
										"ProfileDetails_ProfileIDs_UniqueID", false)) {

									WSClient.setData("{var_profileID}", operaProfileID);

									// Fetching Record from DB
									String QS_09 = WSClient.getQuery("QS_09");
									LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_09);

									// Fetching Record from Response
									String membershipNumber = WSClient.getElementValue(fetchProfileRes,
											"Memberships_NameMembership_membershipNumber", XMLType.RESPONSE);
									String memberName = WSClient.getElementValue(fetchProfileRes,
											"Memberships_NameMembership_memberName", XMLType.RESPONSE);
									String membershipType = WSClient.getElementValue(fetchProfileRes,
											"Memberships_NameMembership_membershipType", XMLType.RESPONSE);
									String membershipLevel = WSClient.getElementValue(fetchProfileRes,
											"Memberships_NameMembership_membershipLevel", XMLType.RESPONSE);
									String expirationDate = WSClient.getElementValue(fetchProfileRes,
											"Memberships_NameMembership_expirationDate", XMLType.RESPONSE);

									LinkedHashMap<String, String> actualRecord = new LinkedHashMap<String, String>();
									actualRecord.put("Membership Number", membershipNumber);
									actualRecord.put("Member Name", memberName);
									actualRecord.put("Membership Type", membershipType);
									actualRecord.put("Membership Level", membershipLevel);
									actualRecord.put("Expiration Date", expirationDate);

									// Validating the fetched profile values
									// from RES with the
									// Database
									WSAssert.assertEquals(expectedRecord, actualRecord, false);
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
								}
							}

							// Checking if ResultstatusFlag is FAIL
							else {
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
							}

							// Checking for GDSError
							if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError",
									true)) {
								String message = WSAssert.getElementValue(fetchProfileRes,
										"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Result Status Flag Itself does not Exist in the response!");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Prerequisite for Membership failed----Membership is not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ MembershipType, MembershipLevel not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Profile MR-9, Creating Basic Profile and then passing an INVALID
	// NameID type and then Fetching it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39388() {
		try {
			String testName = "fetchProfile_39388";
			WSClient.startTest(testName,
					"Verify that the Profile is NOT looked up when an INVALID NameID type is passed",
					"minimumRegression");
			String chain = OPERALib.getChain();
			String resort = OPERALib.getResort();
			// String channel = OWSLib.getChannel();
			String uname = OPERALib.getUserName();
			// String pwd = OPERALib.getPassword();
			// String channelType = OWSLib.getChannelType(channel);
			// String channelCarrier = OWSLib.getChannelCarier(resort,
			// channelType);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

			if (!operaProfileID.equals("error")) {
				System.out.println(operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);

				setOWSHeader();
				// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
				// channelCarrier);

				String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_03");
				String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchProfileRes,
							"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking for GDSError
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError", true)) {
					String message = WSAssert.getElementValue(fetchProfileRes, "FetchProfileResponse_Result_GDSError",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
				}

				// Checking For Fault Schema
				// Actual Validation
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
						String message = WSClient.getElementValue(fetchProfileRes, "FetchProfileResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.PASS,
								"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Fault String Absent in the Response!");
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Fault Code Does not exist in the Response!");
				}
				// Checking For Result Flag
				// if (WSAssert.assertIfElementExists(fetchProfileRes,
				// "FetchProfileResponse_Result_resultStatusFlag", true))
				// {
				// if(WSAssert.assertIfElementValueEquals(fetchProfileRes,
				// "FetchProfileResponse_Result_resultStatusFlag", "FAIL",
				// false))
				// {
				// WSClient.writeToReport(LogStatus.PASS, "Fetch Profile Failed
				// as expected");
				// }
				// //Checking if ResultstatusFlag is SUCCESS
				// else
				// {
				// WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile
				// Passed!");
				// }
				// }
				// else
				// {
				// WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag
				// Itself does not Exist in the response!");
				// }

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Profile MR-10, Creating Basic Profile and then passing an INVALID
	// NameID and then Fetching it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_39389() {
		try {
			String testName = "fetchProfile_39389";
			WSClient.startTest(testName, "Verify that the Profile is NOT looked up when WRONG profileID is passed",
					"minimumRegression");

			String resort = OPERALib.getResort();
			WSClient.setData("{var_resort}", resort);
			String chain = OPERALib.getChain();
			// String channel = OWSLib.getChannel();
			String uname = OPERALib.getUserName();
			// String pwd = OPERALib.getPassword();
			// String channelType = OWSLib.getChannelType(channel);
			// String channelCarrier = OWSLib.getChannelCarier(resort,
			// channelType);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String operaProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_9}");
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

			if (!operaProfileID.equals("")) {
				System.out.println(operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);

				setOWSHeader();
				// OWSLib.setOWSHeader(uname, pwd, resort, channelType,
				// channelCarrier);

				String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
				String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchProfileRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchProfileRes,
							"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchProfileRes, "FetchProfileResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_resultStatusFlag",
						true)) {
					if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
							"FetchProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
						WSClient.writeToReport(LogStatus.PASS, "Fetch Profile Failed as expected");
					}
					// Checking if ResultstatusFlag is SUCCESS
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Passed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(fetchProfileRes,
								"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Profile MR-11, Creating Profile with First Name, Last Name, and
	// Multiple Address and then Fetching it.
	@Test(groups = { "minimumRegression", "Name", "FetchProfile", "OWS" })
	public void fetchProfile_41306() {
		try {
			String testName = "fetchProfile_41306";
			WSClient.startTest(testName, "Verify that Multiple Address records are fetched correctly",
					"minimumRegression");
			String prerequisite[] = { "AddressType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for the required profile creation
				String resortOperaValue = OPERALib.getResort();
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
				String addressType1 = OperaPropConfig.getDataSetForCode("AddressType", "DS_02");
				HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();
				HashMap<String, String> fullAddress1 = OPERALib.fetchAddressLOV();

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_addressType}", addressType);
				WSClient.setData("{var_addressType1}", addressType1);
				WSClient.setData("{var_city}", fullAddress.get("City"));
				WSClient.setData("{var_zip}", fullAddress.get("Zip"));
				WSClient.setData("{var_state}", fullAddress.get("State"));
				WSClient.setData("{var_country}", fullAddress.get("Country"));
				WSClient.setData("{var_city1}", fullAddress1.get("City"));
				WSClient.setData("{var_zip1}", fullAddress1.get("Zip"));
				WSClient.setData("{var_state1}", fullAddress1.get("State"));
				WSClient.setData("{var_country1}", fullAddress1.get("Country"));

				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_36");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {

					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);

					String QS_03 = WSClient.getQuery("QS_03");
					String addressid1 = WSClient.getDBRow(QS_03).get("ADDRESS_ID");
					if (!addressid1.equals("")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "Successfully created Profile with one Address" + "</b>");
						// Prerequisite 2: Change Profile
						WSClient.setData("{var_profileID}", operaProfileID);
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_14");
						String changeProfileRes = WSClient.processSOAPMessage(changeProfileReq);

						if (WSAssert.assertIfElementExists(changeProfileRes, "ChangeProfileRS_Success", false)) {
							String QS_02 = WSClient.getQuery("QS_02");
							String addressidCount = WSClient.getDBRow(QS_02).get("ADDRESS_ID");
							if (addressidCount.equals("2")) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "Successfully added 2nd Address to the Profile" + "</b>");

								setOWSHeader();
								// OWSLib.setOWSHeader(uname, pwd, resort,
								// channelType, channelCarrier);

								String fetchProfileReq = WSClient.createSOAPMessage("OWSFetchProfile", "DS_01");
								String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);

								// Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(fetchProfileRes, "Result_Text_TextElement", true)) {

									String message = WSAssert.getElementValue(fetchProfileRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text displayed in the response is :" + message + "</b>");
								}

								// Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(fetchProfileRes,
										"FetchProfileResponse_Result_OperaErrorCode", true)) {
									String code = WSAssert.getElementValue(fetchProfileRes,
											"FetchProfileResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The error code displayed in the response is :" + code + "</b>");
								}

								// Checking For Fault Schema
								if (WSAssert.assertIfElementExists(fetchProfileRes, "FetchProfileResponse_faultcode",
										true)) {
									String message = WSClient.getElementValue(fetchProfileRes,
											"FetchProfileResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL,
											"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
								}

								// Checking For Result Flag
								if (WSAssert.assertIfElementExists(fetchProfileRes,
										"FetchProfileResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(fetchProfileRes,
											"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										if (WSAssert.assertIfElementExists(fetchProfileRes,
												"ProfileDetails_ProfileIDs_UniqueID", false)) {

											WSClient.setData("{var_profileID}", operaProfileID);

											// Fetching Record from DB
											String QS11 = WSClient.getQuery("QS_11");
											List<LinkedHashMap<String, String>> expectedRecords = WSClient
													.getDBRows(QS11);
											List<LinkedHashMap<String, String>> actualRecords = new ArrayList<LinkedHashMap<String, String>>();

											// Fetching Record from Response
											HashMap<String, String> xpath = new HashMap<String, String>();
											xpath.put("Addresses_NameAddress_AddressLine",
													"ProfileDetails_Addresses_NameAddress");
											xpath.put("Addresses_NameAddress_cityName",
													"ProfileDetails_Addresses_NameAddress");
											xpath.put("Addresses_NameAddress_stateProv",
													"ProfileDetails_Addresses_NameAddress");
											xpath.put("Addresses_NameAddress_countryCode",
													"ProfileDetails_Addresses_NameAddress");
											xpath.put("Addresses_NameAddress_postalCode",
													"ProfileDetails_Addresses_NameAddress");
											xpath.put("ProfileDetails_Addresses_NameAddress_addressType",
													"ProfileDetails_Addresses_NameAddress");
											xpath.put("ProfileDetails_Addresses_NameAddress_primary",
													"ProfileDetails_Addresses_NameAddress");
											actualRecords = WSClient.getMultipleNodeList(fetchProfileRes, xpath, false,
													XMLType.RESPONSE);

											// // Validating the fetched profile
											// values from RES with the
											// // Database
											WSAssert.assertEquals(actualRecords, expectedRecords, false);
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unique ID not available in response");
										}
									}

									// Checking if ResultstatusFlag is FAIL
									else {
										WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Failed!");
									}

									// Checking for GDSError
									if (WSAssert.assertIfElementExists(fetchProfileRes,
											"FetchProfileResponse_Result_GDSError", true)) {
										String message = WSAssert.getElementValue(fetchProfileRes,
												"FetchProfileResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL,
												"The error displayed in the response is :" + message);
									}
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Result Status Flag Itself does not Exist in the response!");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"The prerequisites for Address failed!------ Create Profile -----Blocked");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites  failed!------ Change Profile -----Blocked");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Address failed!------ Create Profile -----Blocked");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

}
