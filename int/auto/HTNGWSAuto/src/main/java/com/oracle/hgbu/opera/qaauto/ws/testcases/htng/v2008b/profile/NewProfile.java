package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
//import com.oracle.ws.businessmethods.ChangeApplicationParameters;
//import com.oracle.ws.businessmethods.FetchApplicationParameters;
import com.relevantcodes.customextentreports.LogStatus;

public class NewProfile extends WSSetUp {

	String parameter = "";

	public String futureYear(int max, int min) {
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		Integer rNum = new Integer(randomNum);
		return (rNum.toString());
	}

	public void setExtVariables() throws Exception {
		String resortOperaValue = OPERALib.getResort();
		String interfaceName = HTNGLib.getHTNGInterface();
		String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
		String chainValue = OPERALib.getChain();
		WSClient.setData("{var_profileSource}", interfaceName);
		WSClient.setData("{var_resort}", resortOperaValue);
		WSClient.setData("{var_extResort}", resortExtValue);
		WSClient.setData("{var_chain}", chainValue);
	}


	
	
	// Verify that the new Profile is created with First Name and Last Name and
	// validating if the created profile is getting subscribed
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	public void newProfile_2008_3926() {
		try {
			String testName = "newProfile_2008_3926";
			WSClient.startTest(testName,
					"Verify that the new Profile is created with First Name and Last Name and validating if the created profile is getting subscribed",
					"minimumRegression");
			String interfaceName = HTNGLib.getHTNGInterface();

			setExtVariables();

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Creating New Profile" + "</b>");

			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_parameter}", "OWS_PROFILE_MATCH");
			WSClient.setData("{var_settingValue}", "N");

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_01");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				// Validation for checking response is success then its not an
				// Empty response
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
					// Validating Details from NAME table vs REQUEST and
					// RESPONSE
					LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();
					String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML, "Result_IDs_UniqueID",
							"Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
					WSClient.setData("{var_profileID}", operaID);
					String externalID = WSClient.getElementValueByAttribute(newProfileResponseXML,
							"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", interfaceName, XMLType.RESPONSE);

					expectedReqDataMap.put("FIRST",
							WSClient.getElementValue(newProfileReq, "Customer_PersonName_FirstName", XMLType.REQUEST));
					expectedReqDataMap.put("LAST",
							WSClient.getElementValue(newProfileReq, "Customer_PersonName_LastName", XMLType.REQUEST));
					expectedReqDataMap.put("RESORT_REGISTERED",
							WSClient.getElementValue(newProfileReq, "NewProfileRequest_ResortId", XMLType.REQUEST));
					expectedReqDataMap.put("NAME_ID", operaID);

					LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
					String QS_01 = WSClient.getQuery("QS_01");
					actualDBDataMap = WSClient.getDBRow(QS_01);

					WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);

					// Checking whether created profile is getting subscribed
					// automatically or not
					String QS_02 = WSClient.getQuery("QS_02");
					HashMap<String, String> db_result = WSClient.getDBRow(QS_02);
					if (WSAssert.assertEquals(db_result.get("DATABASE_NAME_ID"), externalID, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Profile got subscribed!");
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "External ID populated in DB is: " + db_result.get("DATABASE_NAME_ID")
								+ "		External ID in Response is: " + externalID + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Profile does not got subscribed!");
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "External ID populated in DB is: " + db_result.get("DATABASE_NAME_ID")
								+ "		External ID in Response is: " + externalID + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			}
			// }
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with only the Last Name
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with only the LAST NAME
	 */
	public void newProfile_2008_519() {
		try {
			String testName = "newProfile_2008_519";
			WSClient.startTest(testName, "Verify that the new Profile is created with only the Last Name",
					"minimumRegression");

			setExtVariables();

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_02");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				// Validation for checking response is success then its not an
				// Empty response
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
					// Validating Details from NAME table vs REQUEST and
					// RESPONSE
					LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

					String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML, "Result_IDs_UniqueID",
							"Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
					WSClient.setData("{var_profileID}", operaID);

					// String externalID =
					// WSClient.getElementValueByAttribute(newProfileResponseXML,"Result_IDs_UniqueID",
					// "Result_IDs_UniqueID_source", interfaceName,
					// XMLType.RESPONSE);
					expectedReqDataMap.put("LAST",
							WSClient.getElementValue(newProfileReq, "Customer_PersonName_LastName", XMLType.REQUEST));
					expectedReqDataMap.put("RESORT_REGISTERED",
							WSClient.getElementValue(newProfileReq, "NewProfileRequest_ResortId", XMLType.REQUEST));
					expectedReqDataMap.put("NAME_ID", operaID);

					LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
					String QS_01 = WSClient.getQuery("QS_01");
					actualDBDataMap = WSClient.getDBRow(QS_01);

					WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);

				} else {
					WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			}
			// }
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with document details.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with Customer Information and their document details.
	 */
	public void newProfile_2008_520() {
		try {
			String testName = "newProfile_2008_520";
			WSClient.startTest(testName, "Verify that the new Profile is created with document details.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType", "VipLevel", "Title" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();
				String genderPmsValue = OperaPropConfig.getDataSetForCode("Gender", "DS_01");
				String genderExtValue = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GENDER_MF",
						genderPmsValue);
				String docTypePms = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				String docTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "DOCUMENT_TYPE", docTypePms);
				String vipLevelPms = OperaPropConfig.getDataSetForCode("VipLevel", "DS_01");
				String vipLevelExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "VIP_LEVEL", vipLevelPms);
				String nameTitlePms = OperaPropConfig.getDataSetForCode("Title", "DS_01");
				String nameTitleExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "TITLE", nameTitlePms);

				setExtVariables();
				WSClient.setData("{var_nameTitle}", nameTitleExt);
				WSClient.setData("{var_gender}", genderExtValue);
				WSClient.setData("{var_docType}", docTypeExt);
				// WSClient.setData("{var_nameType}", nameType);
				WSClient.setData("{var_vipLevel}", vipLevelExt);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_03");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						// expectedReqDataMap.put("NATIONALITY",WSClient.getElementValue(newProfileReq,
						// "NewProfileRequest_Profile_nationality",
						// XMLType.REQUEST));
						expectedReqDataMap.put("VIP_STATUS", vipLevelPms);
						expectedReqDataMap.put("GENDER", genderPmsValue);

						String fullDOB = WSClient.getElementValue(newProfileReq,
								"NewProfileRequest_Profile_Customer_birthDate", XMLType.REQUEST);
						String expDOB = fullDOB.substring(fullDOB.length() - 2);
						expectedReqDataMap.put("BIRTH_DATE", expDOB);
						expectedReqDataMap.put("FIRST", WSClient.getElementValue(newProfileReq,
								"Customer_PersonName_FirstName", XMLType.REQUEST));
						expectedReqDataMap.put("MIDDLE", WSClient.getElementValue(newProfileReq,
								"Customer_PersonName_MiddleName", XMLType.REQUEST));
						expectedReqDataMap.put("LAST", WSClient.getElementValue(newProfileReq,
								"Customer_PersonName_LastName", XMLType.REQUEST));
						expectedReqDataMap.put("BUSINESS_TITLE", WSClient.getElementValue(newProfileReq,
								"Profile_Customer_BusinessTitle", XMLType.REQUEST));
						expectedReqDataMap.put("ID_TYPE", docTypePms);
						String fullIDNo = WSClient.getElementValue(newProfileReq,
								"GovernmentIDList_GovernmentID_DocumentNumber", XMLType.REQUEST);
						String expIDNo = fullIDNo.substring(fullIDNo.length() - 2);
						expectedReqDataMap.put("ID_NUMBER", expIDNo);

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_03 = WSClient.getQuery("QS_03");
						actualDBDataMap2 = WSClient.getDBRow(QS_03);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap2, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites IdentificationType, VipLevel, Title not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with multiple addresss.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with multiple address details.
	 */
	public void newProfile_2008_40552() {
		try {
			String testName = "newProfile_2008_40552";
			WSClient.startTest(testName, "Verify that the new Profile is created with multiple addresss.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address :" + state_code + " " + state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				String country = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", country_code);
				System.out.println("address_country :" + country_code + " " + country);

				HashMap<String, String> addLOV = OPERALib.fetchAddressLOV(state, country_code);

				WSClient.setData("{var_state}", state_code);
				WSClient.setData("{var_country}", country);
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));
				String AddressType1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				String AddressType2 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_02"));
				state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
				state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address :" + state_code + " " + state);
				country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				country = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", country_code);
				System.out.println("address_country :" + country_code + " " + country);

				addLOV = OPERALib.fetchAddressLOV(state, country_code);

				WSClient.setData("{var_state2}", state_code);
				WSClient.setData("{var_country2}", country);
				WSClient.setData("{var_zip2}", addLOV.get("Zip"));
				WSClient.setData("{var_city2}", addLOV.get("City"));

				setExtVariables();

				WSClient.setData("{var_addressType1}", AddressType1);
				WSClient.setData("{var_addressType2}", AddressType2);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_04");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for address details
						String QS_04 = WSClient.getQuery("QS_04");
						List<LinkedHashMap<String, String>> db = WSClient.getDBRows(QS_04);
						List<LinkedHashMap<String, String>> expected = new ArrayList<LinkedHashMap<String, String>>();

						HashMap<String, String> xpath = new HashMap<String, String>();
						xpath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_CityName", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_StateProv", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_CountryCode", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_PostalCode", "Profile_Addresses_NameAddress");
						xpath.put("Profile_Addresses_NameAddress_addressType", "Profile_Addresses_NameAddress");
						xpath.put("Profile_Addresses_NameAddress_primary", "Profile_Addresses_NameAddress");
						expected = WSClient.getMultipleNodeList(newProfileReq, xpath, false, XMLType.REQUEST);
						List<LinkedHashMap<String, String>> manipReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						for (int i = 0; i < expected.size(); i++) {
							LinkedHashMap<String, String> mp = expected.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while (itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if (entry.getKey().equalsIgnoreCase("addressType1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName,
											"ADDRESS_TYPES", entry.getValue()));
								}
								if (entry.getKey().equalsIgnoreCase("StateProv1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE",
											entry.getValue()));
								}
								if (entry.getKey().equalsIgnoreCase("CountryCode1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName,
											"COUNTRY_CODE", entry.getValue()));
								}
							}
							manipReqDataList.add(mp);
						}
						WSAssert.assertEquals(db, manipReqDataList, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with phone details.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with phone details.
	 */
	public void newProfile_2008_525() {
		try {
			String testName = "newProfile_2008_525";
			WSClient.startTest(testName, "Verify that the new Profile is created with phone details.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				String phonePmsValue = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
				String phoneExtValue = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue);
				// String phoneExtValue =
				// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"PHONE_TYPE");

				setExtVariables();

				WSClient.setData("{var_phoneType}", phoneExtValue);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_05");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put("PHONE_TYPE", phonePmsValue);
						expectedReqDataMap.put("PHONE_ROLE", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_phoneRole", XMLType.REQUEST));

						String countryCode = WSClient.getElementValue(newProfileReq,
								"NamePhone_PhoneData_CountryAccessCode", XMLType.REQUEST);
						String areaCode = WSClient.getElementValue(newProfileReq, "NamePhone_PhoneData_AreaCode",
								XMLType.REQUEST);
						String phone = WSClient.getElementValue(newProfileReq, "NamePhone_PhoneData_PhoneNumber",
								XMLType.REQUEST);
						String completePhone = countryCode + areaCode + phone;
						expectedReqDataMap.put("PHONE_NUMBER", completePhone);
						expectedReqDataMap.put("PRIMARY1", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_primary", XMLType.REQUEST));

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_05 = WSClient.getQuery("QS_05");
						actualDBDataMap2 = WSClient.getDBRow(QS_05);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap2, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites CommunicationType, CommunicationMethod not available -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with email details.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with phone details.
	 */
	public void newProfile_2008_526() {
		try {
			String testName = "newProfile_2008_526";
			WSClient.startTest(testName, "Verify that the new Profile is created with email details.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				String phoneTypePms = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_04");
				System.out.println("phonetypepms" + phoneTypePms);
				String phoneTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE", phoneTypePms);
				// String phoneType=HTNGLib.getExtValue(resortOperaValue,
				// interfaceName, "PHONE_TYPE", "EMAIL");

				setExtVariables();
				WSClient.setData("{var_phoneType}", phoneTypeExt);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String Lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String emailFinal = fname + "." + Lname + "@xyzmail.com";
				WSClient.setData("{var_fName}", fname);
				WSClient.setData("{var_lName}", Lname);
				WSClient.setData("{var_emailID}", emailFinal);
				// Validation request being created and processed to generate
				// response

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_06");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);
						WSClient.setData("{var_interface}", interfaceName);
						// Checking for customer details
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put("PHONE_TYPE", phoneTypePms);
						expectedReqDataMap.put("PHONE_ROLE", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_phoneRole", XMLType.REQUEST));
						expectedReqDataMap.put("PHONE_NUMBER", WSClient.getElementValue(newProfileReq,
								"Phones_NamePhone_PhoneNumber", XMLType.REQUEST));
						expectedReqDataMap.put("PRIMARY1", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_primary", XMLType.REQUEST));

						LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
						String QS_06 = WSClient.getQuery("QS_06");
						actualDBDataMap = WSClient.getDBRow(QS_06);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites CommunicationType, CommunicationMethod not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with preferences.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with customer details and his preferences.
	 */
	public void newProfile_2008_530() {
		try {
			String testName = "newProfile_2008_530";
			WSClient.startTest(testName, "Verify that the new Profile is created with preferences.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "PreferenceGroup", "PreferenceCode" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				String prefCodePms = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");
				String prefCodeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE",
						prefCodePms);

				String pmsPrefType = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");
				String extPrefType = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE",
						pmsPrefType);
				System.out.println("~~~~~~Pms Pref Code: " + prefCodePms);
				System.out.println("~~~~~~Ext Pref Code: " + prefCodeExt);
				System.out.println("~~~~~~Pms Pref Type: " + pmsPrefType);
				System.out.println("~~~~~~Ext Pref Type: " + extPrefType);

				setExtVariables();

				WSClient.setData("{var_interface}", interfaceName);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String Lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fName}", fname);
				WSClient.setData("{var_lName}", Lname);
				WSClient.setData("{var_prefCode}", prefCodeExt);
				WSClient.setData("{var_prefType}", extPrefType);
				// Validation request being created and processed to generate
				// response

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_07");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put("PREFERENCE_TYPE", pmsPrefType);
						expectedReqDataMap.put("PREFERENCE_VALUE", prefCodePms);

						LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
						String QS_07 = WSClient.getQuery("QS_07");
						actualDBDataMap = WSClient.getDBRow(QS_07);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites PreferenceGroup, PreferenceCode not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with membership details.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with phone details.
	 */
	public void newProfile_2008_533() {
		try {
			String testName = "newProfile_2008_533";
			WSClient.startTest(testName, "Verify that the new Profile is created with membership details.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				String membType = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE",
						OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
				String membLevel = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL",
						OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));
				String membTypePms = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE", membType);
				String membLevelPms = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL",
						membLevel);

				setExtVariables();
				String futureYr = futureYear(2049, 2018);
				String expiryDate = futureYr + "-12-31";
				WSClient.setData("{var_expDate}", expiryDate);
				WSClient.setData("{var_membType}", membType);
				WSClient.setData("{var_membLevel}", membLevel);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_08");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking further customer and membership details
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put("MEMBERSHIP_TYPE", membTypePms);
						expectedReqDataMap.put("MEMBERSHIP_CARD_NO", WSClient.getElementValue(newProfileReq,
								"Memberships_NameMembership_MembershipNumber", XMLType.REQUEST));
						expectedReqDataMap.put("MEMBERSHIP_LEVEL", membLevelPms);
						expectedReqDataMap.put("JOINED_DATE", WSClient.getElementValue(newProfileReq,
								"Memberships_NameMembership_EffectiveDate", XMLType.REQUEST));
						String fullExpDate = WSClient.getElementValue(newProfileReq,
								"Memberships_NameMembership_ExpirationDate", XMLType.REQUEST);
						String expDate = fullExpDate.substring(0, 7);
						expectedReqDataMap.put("EXPIRATION_DATE", expDate);

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_08 = WSClient.getQuery("QS_08");
						actualDBDataMap2 = WSClient.getDBRow(QS_08);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap2, false);
					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites MembershipType, MembershipType not available -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with comments.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with comments.
	 */
	public void newProfile_2008_535() {
		try {
			String testName = "newProfile_2008_535";
			WSClient.startTest(testName, "Verify that the new Profile is created with comments.", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();

				// String cmtType=HTNGLib.getRandomExtValue(resortOperaValue,
				// interfaceName, "COMMENT_TYPE");
				String cmtTypePms = OperaPropConfig.getDataSetForCode("NoteType", "DS_01");
				String cmtTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", cmtTypePms);

				setExtVariables();

				WSClient.setData("{var_interface}", interfaceName);
				WSClient.setData("{var_cmtType}", cmtTypeExt);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_09");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details and comments
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put("NOTE_CODE",
								HTNGLib.getPmsValue(resortOperaValue, interfaceName, "COMMENT_TYPE",
										WSClient.getElementValue(newProfileReq, "Profile_Comments_Comment_commentType",
												XMLType.REQUEST)));
						expectedReqDataMap.put("NOTE_TITLE",
								WSClient.getElementValue(newProfileReq, "Comment_Text_TextElement", XMLType.REQUEST));
						expectedReqDataMap.put("NOTES",
								WSClient.getElementValue(newProfileReq, "Comment_Text_TextElement", XMLType.REQUEST));

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_09 = WSClient.getQuery("QS_09");
						actualDBDataMap2 = WSClient.getDBRow(QS_09);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap2, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites NoteType not available -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with privacy options.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with privacy options.
	 */
	public void newProfile_2008_538() {
		try {
			String testName = "newProfile_2008_538";
			WSClient.startTest(testName, "Verify that the new Profile is created with privacy options.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();

			String cmtTypePms = OperaPropConfig.getDataSetForCode("NoteType", "DS_01");
			String cmtTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", cmtTypePms);

			setExtVariables();

			WSClient.setData("{var_interface}", interfaceName);
			WSClient.setData("{var_cmtType}", cmtTypeExt);

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_09");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				// Validation for checking response is success then its not an
				// Empty response
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
					// Validating Details from NAME table vs REQUEST and
					// RESPONSE
					String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML, "Result_IDs_UniqueID",
							"Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
					WSClient.setData("{var_profileID}", operaID);

					// Checking further customer details and comments
					LinkedHashMap<String, String> expectedReqDataMap2 = new LinkedHashMap<String, String>();

					expectedReqDataMap2.put("NAME_ID", operaID);
					expectedReqDataMap2.put("MAIL_LIST", WSClient.getElementValue(newProfileReq,
							"Profile_PrivacyList_PrivacyOption_OptionValue", XMLType.REQUEST));
					expectedReqDataMap2.put("EMAIL_YN", WSClient.getElementValue(newProfileReq,
							"Profile_PrivacyList_PrivacyOption[2]_OptionValue", XMLType.REQUEST));
					expectedReqDataMap2.put("GUEST_PRIV_YN", WSClient.getElementValue(newProfileReq,
							"Profile_PrivacyList_PrivacyOption[3]_OptionValue", XMLType.REQUEST));

					LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
					String QS_10 = WSClient.getQuery("QS_10");
					actualDBDataMap2 = WSClient.getDBRow(QS_10);

					WSAssert.assertEquals(expectedReqDataMap2, actualDBDataMap2, false);

				} else {
					WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			}
			// }
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with user defined values.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with User defined values.
	 */
	public void newProfile_2008_540() {
		try {
			String testName = "newProfile_2008_540";
			WSClient.startTest(testName, "Verify that the new Profile is created with user defined values.",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "UDFLabel_P" })) {

				setExtVariables();

				WSClient.setData("{var_interface}", interfaceName);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				String udfLabel1 = HTNGLib.getUDFLabel("D", "P");
				String udfName1 = HTNGLib.getUDFName("D", udfLabel1, "P");
				String udfLabel2 = HTNGLib.getUDFLabel("C", "P");
				String udfName2 = HTNGLib.getUDFName("C", udfLabel2, "P");
				String udfLabel3 = HTNGLib.getUDFLabel("N", "P");
				String udfName3 = HTNGLib.getUDFName("N", udfLabel3, "P");
				WSClient.setData("{var_udfName1}", udfName1);
				WSClient.setData("{var_udfName2}", udfName2);
				WSClient.setData("{var_udfName3}", udfName3);
				WSClient.setData("{var_udfLabel1}", udfLabel1);
				WSClient.setData("{var_udfLabel2}", udfLabel2);
				WSClient.setData("{var_udfLabel3}", udfLabel3);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_10");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking further customer details and user defined
						// values
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put(udfName1, WSClient.getElementValue(newProfileReq,
								"UserDefinedValues_UserDefinedValue_DateValue", XMLType.REQUEST));
						expectedReqDataMap.put(udfName2, WSClient.getElementValue(newProfileReq,
								"UserDefinedValues_UserDefinedValue[2]_CharacterValue", XMLType.REQUEST));
						expectedReqDataMap.put(udfName3, WSClient.getElementValue(newProfileReq,
								"UserDefinedValues_UserDefinedValue[3]_NumericValue", XMLType.REQUEST));

						LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
						String QS_11 = WSClient.getQuery("QS_11");
						actualDBDataMap = WSClient.getDBRow(QS_11);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);
					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Failed---UDFName and UDFLabel not available");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the name Type conversion occurs when a new Profile is created
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_31464() {
		try {
			String testName = "newProfile_2008_31464";
			WSClient.startTest(testName, "Verify that the name Type conversion occurs when a new Profile is created",
					"minimumRegression");

			String nameType = "GUEST";
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			setExtVariables();

			WSClient.setData("{var_nameType}", nameType);

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Creating New Profile" + "</b>");
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_11");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				// Validation for checking response is success then its not an
				// Empty response
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
					// Validating Details from NAME table vs REQUEST and
					// RESPONSE
					LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();
					String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML, "Result_IDs_UniqueID",
							"Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
					WSClient.setData("{var_profileID}", operaID);
					// String externalID =
					// WSClient.getElementValueByAttribute(newProfileResponseXML,
					// "Result_IDs_UniqueID", "Result_IDs_UniqueID_source",
					// interfaceName, XMLType.RESPONSE);

					expectedReqDataMap.put("FIRST",
							WSClient.getElementValue(newProfileReq, "Customer_PersonName_FirstName", XMLType.REQUEST));
					expectedReqDataMap.put("LAST",
							WSClient.getElementValue(newProfileReq, "Customer_PersonName_LastName", XMLType.REQUEST));
					expectedReqDataMap.put("RESORT_REGISTERED",
							WSClient.getElementValue(newProfileReq, "NewProfileRequest_ResortId", XMLType.REQUEST));
					expectedReqDataMap.put("NAME_TYPE",
							HTNGLib.getPmsValue(resortOperaValue, interfaceName, "PROFILE_TYPE", nameType));
					expectedReqDataMap.put("NAME_ID", operaID);

					LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
					String QS_12 = WSClient.getQuery("QS_12");
					actualDBDataMap = WSClient.getDBRow(QS_12);

					WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			}
			// }
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with single address.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with single address details.
	 */
	public void newProfile_2008_522() {
		try {
			String testName = "newProfile_2008_522";
			WSClient.startTest(testName, "Verify that the new Profile is created with single address.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address :" + state_code + " " + state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				String country = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", country_code);
				System.out.println("address_country :" + country_code + " " + country);

				HashMap<String, String> addLOV = OPERALib.fetchAddressLOV(state, country_code);

				WSClient.setData("{var_state}", state_code);
				WSClient.setData("{var_country}", country);
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));
				String AddressType1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

				setExtVariables();

				WSClient.setData("{var_addressType1}", AddressType1);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_12");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for address details
						String QS_13 = WSClient.getQuery("QS_13");
						LinkedHashMap<String, String> db = WSClient.getDBRow(QS_13);
						LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();

						HashMap<String, String> xpath = new HashMap<String, String>();
						xpath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_CityName", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_StateProv", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_CountryCode", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_PostalCode", "Profile_Addresses_NameAddress");
						xpath.put("Profile_Addresses_NameAddress_addressType", "Profile_Addresses_NameAddress");
						xpath.put("Profile_Addresses_NameAddress_primary", "Profile_Addresses_NameAddress");
						expected = WSClient.getSingleNodeList(newProfileReq, xpath, false, XMLType.REQUEST);
						String stateOpera = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", WSClient
								.getElementValue(newProfileReq, "Addresses_NameAddress_StateProv", XMLType.REQUEST));
						String countryOpera = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "COUNTRY_CODE",
								WSClient.getElementValue(newProfileReq, "Addresses_NameAddress_CountryCode",
										XMLType.REQUEST));
						String adTypeOpera = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "ADDRESS_TYPES",
								WSClient.getElementValue(newProfileReq, "Profile_Addresses_NameAddress_addressType",
										XMLType.REQUEST));

						expected = WSClient.getSingleNodeList(newProfileReq, xpath, false, XMLType.REQUEST);

						if (expected.containsKey("StateProv1")) {
							expected.put("StateProv1", stateOpera);
						}
						if (expected.containsKey("CountryCode1")) {
							expected.put("CountryCode1", countryOpera);
						}
						if (expected.containsKey("addressType1")) {
							expected.put("addressType1", adTypeOpera);
						}

						WSAssert.assertEquals(expected, db, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites AddressType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that profile is staged when created with a wrong nameType
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_41101() {
		try {
			String testName = "newProfile_2008_41101";
			WSClient.startTest(testName, "Verify that profile is staged when created with a wrong nameType",
					"minimumRegression");

			String nameType = "BADTYPE";

			setExtVariables();

			WSClient.setData("{var_nameType}", nameType);

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Creating New Profile" + "</b>");
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_11");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
				/****
				 * Verifying that the error message is populated on the response
				 ********/
				String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "The text displayed in the response is :" + "</b>" + message);

			}
			// Validation if response is generated successfully or not
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Validating Response:" + "</b>");
			WSAssert.assertIfElementValueEquals(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"FAIL", false);

			// Validate the Profile is staged
			// Validating Details from STAGE_PROFILES table vs REQUEST and
			// RESPONSE

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Validating DB:" + "</b>");
			LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();
			String operaID = WSClient.getElementValue(newProfileReq, "Profile_IDs_UniqueID", XMLType.REQUEST);
			WSClient.setData("{var_profileID}", operaID);
			// String externalID =
			// WSClient.getElementValueByAttribute(newProfileResponseXML,
			// "Result_IDs_UniqueID", "Result_IDs_UniqueID_source",
			// interfaceName, XMLType.RESPONSE);

			expectedReqDataMap.put("FIRST",
					WSClient.getElementValue(newProfileReq, "Customer_PersonName_FirstName", XMLType.REQUEST));
			expectedReqDataMap.put("LAST",
					WSClient.getElementValue(newProfileReq, "Customer_PersonName_LastName", XMLType.REQUEST));
			expectedReqDataMap.put("NAME_TYPE", WSClient.getData("{var_nameType}"));
			expectedReqDataMap.put("RESORT_NAME_ID", operaID);

			LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
			String QS_14 = WSClient.getQuery("QS_14");
			actualDBDataMap = WSClient.getDBRow(QS_14);

			WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);

			// Validating INVALID Nametype is getting populated in the
			// STAGE_PROFILE_ERRORS
			String expNType = WSClient.getData("{var_nameType}");
			String actNType = WSClient.getDBRow(WSClient.getQuery("QS_15")).get("ERROR_VALUE");

			if (WSAssert.assertEquals(expNType, actNType, false)) {
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "Name Type Passed in the REQ is getting populated in the STAGE_PROFILE_ERRORS table"
								+ "</b>");
			} else {
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "Name Type Passed in the REQ is not getting populated in the STAGE_PROFILE_ERRORS table"
								+ "</b>");
			}
			// }
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued without submitting the data for minimum required elements
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })

	public void newProfile_2008_544() {
		try {
			String testName = "newProfile_2008_544";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued without submitting the data for minimum required elements",
					"fullRegression");

			setExtVariables();

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Creating New Profile" + "</b>");

			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_parameter}", "OWS_PROFILE_MATCH");
			WSClient.setData("{var_settingValue}", "N");

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

			// Error when no ID is provided in the request

			WSClient.writeToReport(LogStatus.INFO,
					"<b>" + "Creating profile without submitting the External Profile ID" + "</b>");
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_13");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"FAIL", false)) {
				// Validation for checking response is fail then its not an
				// Empty response
				WSClient.writeToReport(LogStatus.PASS, "Profile Creation Failed as expected!!");
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			}
			// If ResultsStatusFlag is "SUCCESS"
			else {
				WSClient.writeToReport(LogStatus.FAIL, "Profile is created!!");
			}

			// Error when no FIRST NAME AND LAST NAME is provided in the request
			WSClient.writeToReport(LogStatus.INFO,
					"<b>" + "Creating profile without submitting the Firstname or Lastname" + "</b>");
			String newProfileReq2 = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_14");
			String newProfileResponseXML2 = WSClient.processSOAPMessage(newProfileReq2);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML2, "NewProfileResponse_Result_resultStatusFlag",
					"FAIL", false)) {
				// Validation for checking response is fail then its not an
				// Empty response
				WSClient.writeToReport(LogStatus.PASS, "Profile Creation Failed as expected!!");
				if (WSAssert.assertIfElementExists(newProfileResponseXML2, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(newProfileResponseXML2, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			}
			// If ResultsStatusFlag is "SUCCESS"
			else {
				WSClient.writeToReport(LogStatus.FAIL, "Profile is created!!");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that multiple phone records are being stored in CRM as part of a
	// new profile creation through NewProfile service call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with phone details.
	 */
	public void newProfile_2008_528() {
		try {
			String testName = "newProfile_2008_528";
			WSClient.startTest(testName,
					"Verify that multiple phone records are being stored in CRM as part of a new profile creation through NewProfile service call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();
				String phonePmsValue = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
				String phoneExtValue = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue);
				String phonePmsValue1 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03");
				String phoneExtValue1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue1);
				// String phoneExtValue =
				// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"PHONE_TYPE");
				System.out.println("phonePmsValue: " + phonePmsValue + "phonePmsValue1: " + phonePmsValue1
						+ "phonePmsExt: " + phoneExtValue + "phonePmsExt1: " + phoneExtValue1);

				setExtVariables();
				// WSClient.setData("{var_resort}", resortOperaValue);
				// WSClient.setData("{var_profileSource}", interfaceName);
				// WSClient.setData("{var_extResort}", resortExtValue);
				// //WSClient.setData("{var_extResort}", resortOperaValue);
				// WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_phoneType}", phoneExtValue);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
				WSClient.setData("{var_phoneType1}", phoneExtValue1);
				WSClient.setData("{var_phoneRole1}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				// parameter = getApplicationParameter("DS_01");
				// System.out.println("~~~~"+parameter);
				// if(parameter.equals("Y"))
				// {
				// parameter =
				// ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				// }
				// if(!parameter.equals("error"))
				// {

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_16");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details
						List<LinkedHashMap<String, String>> expectedReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();

						xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
						xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
						xPath.put("Phones_NamePhone_PhoneNumber", "Profile_Phones_NamePhone");
						xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

						expectedReqDataList = WSClient.getMultipleNodeList(newProfileReq, xPath, false,
								XMLType.REQUEST);

						List<LinkedHashMap<String, String>> manipReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						for(int i=0;i<expectedReqDataList.size();i++) {
							LinkedHashMap<String,String> mp = expectedReqDataList.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while(itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if(entry.getKey().equalsIgnoreCase("phoneType1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "PHONE_TYPE", entry.getValue()));
								}
							}
							manipReqDataList.add(mp);
						}

						List<LinkedHashMap<String, String>> actualDBDataList = new ArrayList<LinkedHashMap<String, String>>();
						String QS_16 = WSClient.getQuery("QS_16");
						actualDBDataList = WSClient.getDBRows(QS_16);

						WSAssert.assertEquals(actualDBDataList, manipReqDataList, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites CommunicationMethod, CommunicationType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that multiple comments are being stored and associated to the
	// profile created through NewProfile call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with comments.
	 */
	public void newProfile_2008_536() {
		try {
			String testName = "newProfile_2008_536";
			WSClient.startTest(testName,
					"Verify that multiple comments are being stored and associated to the profile created through NewProfile call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();

				// String cmtType=HTNGLib.getRandomExtValue(resortOperaValue,
				// interfaceName, "COMMENT_TYPE");
				String cmtTypePms = OperaPropConfig.getDataSetForCode("NoteType", "DS_01");
				String cmtTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", cmtTypePms);
				String cmtTypePms1 = OperaPropConfig.getDataSetForCode("NoteType", "DS_02");
				String cmtTypeExt1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", cmtTypePms1);

				setExtVariables();
				// WSClient.setData("{var_profileSource}", interfaceName);
				// //WSClient.setData("{var_extResort}", resortExtValue);
				// WSClient.setData("{var_extResort}", resortExtValue);
				// //WSClient.setData("{var_extResort}", resortOperaValue);
				// WSClient.setData("{var_chain}", chainValue);
				// WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_interface}", interfaceName);
				WSClient.setData("{var_cmtType}", cmtTypeExt);
				WSClient.setData("{var_cmtType1}", cmtTypeExt1);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				// parameter = getApplicationParameter("DS_01");
				// System.out.println("~~~~"+parameter);
				// if(parameter.equals("Y"))
				// {
				// parameter =
				// ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				// }
				// if(!parameter.equals("error"))
				// {
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_17");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details and comments
						List<LinkedHashMap<String, String>> expectedReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();

						xPath.put("Profile_Comments_Comment_commentType", "Profile_Comments_Comment");
						xPath.put("Comment_Text_TextElement", "Profile_Comments_Comment");
						expectedReqDataList = WSClient.getMultipleNodeList(newProfileReq, xPath, false,
								XMLType.REQUEST);
						List<LinkedHashMap<String, String>> manipReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						for(int i=0;i<expectedReqDataList.size();i++) {
							LinkedHashMap<String,String> mp = expectedReqDataList.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while(itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if(entry.getKey().equalsIgnoreCase("commentType1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "COMMENT_TYPE", entry.getValue()));
								}
							}
							manipReqDataList.add(mp);
						}
						List<LinkedHashMap<String, String>> actualDBDataMap2 = new ArrayList<LinkedHashMap<String, String>>();
						String QS17 = WSClient.getQuery("QS_17");
						actualDBDataMap2 = WSClient.getDBRows(QS17);

						WSAssert.assertEquals(actualDBDataMap2, manipReqDataList, true);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites NoteType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


	// Verify that an error message is thrown when incorrect conversion code
	// values related to Membership is given in the request.
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_552() {
		try {
			String testName = "newProfile_2008_552";
			WSClient.startTest(testName,
					"Verify that an error message is thrown when incorrect conversion code values related to Membership is given in the request.",
					"fullRegression");

			String membType = WSClient.getKeywordData("{KEYWORD_RANDSTR_6}");
			String membLevel = WSClient.getKeywordData("{KEYWORD_RANDSTR_6}");

			setExtVariables();
			WSClient.setData("{var_membType}", membType);
			WSClient.setData("{var_membLevel}", membLevel);

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_19");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"FAIL", false)) {
				WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
			} else {
				// When resultstatuFlag is SUCCESS
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
			}
			if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
				/****
				 * Verifying that the error message is populated on the response
				 ********/
				String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "The text displayed in the response is :" + "</b>" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown when incorrect conversion code
	// values related to guest preferences is given in the request.
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_551() {
		try {
			String testName = "newProfile_2008_551";
			WSClient.startTest(testName,
					"Verify that an error message is thrown when incorrect conversion code values related to guest preferences is given in the request.",
					"fullRegression");
			WSClient.setData("{var_fName}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lName}", WSClient.getKeywordData("{KEYWORD_LNAME}"));

			String prefCodeExt = WSClient.getKeywordData("{KEYWORD_RANDSTR_6}");
			String extPrefType = WSClient.getKeywordData("{KEYWORD_RANDSTR_6}");

			setExtVariables();
			WSClient.setData("{var_prefCode}", prefCodeExt);
			WSClient.setData("{var_prefType}", extPrefType);

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_07");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"FAIL", false)) {
				WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
			} else {
				// When resultstatuFlag is SUCCESS
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
			}
			if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
				/****
				 * Verifying that the error message is populated on the response
				 ********/
				String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "The text displayed in the response is :" + "</b>" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued by submitting the incorrect conversion code values
	// related to communication/contact information such as addressType,
	// phoneType
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_550() {
		try {
			String testName = "newProfile_2008_550";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued by submitting the "
							+ "incorrect conversion code values related to communication/contact information such as addressType, phoneType",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				WSClient.setData("{var_fName}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_lName}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_country}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE",
						OperaPropConfig.getDataSetForCode("Country", "DS_01")));
				WSClient.setData("{var_state}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "STATE",
						OperaPropConfig.getDataSetForCode("State", "DS_01")));
				HashMap<String, String> address1 = new HashMap<String, String>();
				address1 = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address1.get("City"));
				WSClient.setData("{var_zip}", address1.get("Zip"));
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

				setExtVariables();

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_20");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites -- CommunicationMethod not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued without submitting the data for minimum required Membership data
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_548() {
		try {
			String testName = "newProfile_2008_548";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued without submitting the data "
							+ "for minimum required Membership data",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipLevel" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String membLevel = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL",
						OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

				setExtVariables();
				WSClient.setData("{var_membLevel}", membLevel);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_21");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites --MembershipLevel not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued without submitting the data for minimum required Preference data
	// elements
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_547() {
		try {
			String testName = "newProfile_2008_547";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued without submitting the data for minimum required Preference data elements",
					"fullRegression");
			WSClient.setData("{var_fName}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lName}", WSClient.getKeywordData("{KEYWORD_LNAME}"));

			setExtVariables();

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_22");
			String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

			// Validation if response is generated successfully or not
			if (WSAssert.assertIfElementContains(newProfileResponseXML, "NewProfileResponse_Result_resultStatusFlag",
					"FAIL", false)) {
				WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
			} else {
				// When resultstatuFlag is SUCCESS
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
			}
			if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
				/****
				 * Verifying that the error message is populated on the response
				 ********/
				LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
				LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
				xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
				textList = WSClient.getSingleNodeList(newProfileResponseXML, xPath, false, XMLType.RESPONSE);
				for (int i = 1; i <= textList.size(); i++) {
					String message = textList.get("TextElement" + i);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + "</b>" + message);
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued without submitting the data for minimum required phone elements
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_546() {
		try {
			String testName = "newProfile_2008_546";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued without submitting the data for minimum required phone elements",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String phonePmsValue = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
				String phoneExtValue = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue);

				setExtVariables();
				WSClient.setData("{var_phoneType}", phoneExtValue);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_23");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites CommunicationType, CommunicationMethod not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued without submitting the data for minimum required address elements
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_545() {
		try {
			String testName = "newProfile_2008_545";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued without submitting the data for minimum required address elements",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String pmsaddressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
				String pmsstate = OperaPropConfig.getDataSetForCode("State", "DS_01");
				String pmscountry = OperaPropConfig.getDataSetForCode("Country", "DS_01");
				String extaddressType = HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES",
						pmsaddressType);
				String extstate = HTNGLib.getExtValue(resortOperaValue, interfaceName, "STATE", pmsstate);
				String extcountry = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", pmscountry);

				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();

				setExtVariables();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_zip}", address.get("Zip"));
				WSClient.setData("{var_country}", extcountry);
				WSClient.setData("{var_state}", extstate);
				WSClient.setData("{var_addressType}", extaddressType);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_24");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites AddressType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that multiple membership records submitted on the request message
	// are created in CRM and are associated to the profile created, through
	// NewProfile service call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_534() {
		try {
			String testName = "newProfile_2008_534";
			WSClient.startTest(testName,
					"Verify that multiple membership records submitted on the request message are created in CRM and are associated to the profile created, through NewProfile service call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();

				String membType = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE",
						OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
				// String membLevel=HTNGLib.getExtValue(resortOperaValue,
				// interfaceName, "MEMBERSHIP_LEVEL",
				// OperaPropConfig.getDataSetForCode("MembershipLevel",
				// "DS_03"));
				String membType1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE",
						OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
				// String membLevel1=HTNGLib.getExtValue(resortOperaValue,
				// interfaceName, "MEMBERSHIP_LEVEL",
				// OperaPropConfig.getDataSetForCode("MembershipLevel",
				// "DS_01"));

				setExtVariables();
				String futureYr = futureYear(2049, 2018);
				String expiryDate = futureYr + "-12-31";
				WSClient.setData("{var_expDate}", expiryDate);
				WSClient.setData("{var_membType}", membType);
				WSClient.setData("{var_membType1}", membType1);
				// WSClient.setData("{var_membLevel}", membLevel);
				// WSClient.setData("{var_membLevel}", membLevel);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_25");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);
						WSClient.setData("{var_interface}", interfaceName);
						// Checking further customer and membership details
						List<LinkedHashMap<String, String>> expectedReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("Memberships_NameMembership_MembershipType", "Profile_Memberships_NameMembership");
						xPath.put("Memberships_NameMembership_MembershipNumber", "Profile_Memberships_NameMembership");
						expectedReqDataList = WSClient.getMultipleNodeList(newProfileReq, xPath, false,
								XMLType.REQUEST);

						List<LinkedHashMap<String, String>> manipReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						for(int i=0;i<expectedReqDataList.size();i++) {
							LinkedHashMap<String,String> mp = expectedReqDataList.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while(itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if(entry.getKey().equalsIgnoreCase("MembershipType1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE", entry.getValue()));
								}
							}
							manipReqDataList.add(mp);
						}
						List<LinkedHashMap<String, String>> actualDBDataList = new ArrayList<LinkedHashMap<String, String>>();
						String QS_19 = WSClient.getQuery("QS_19");
						actualDBDataList = WSClient.getDBRows(QS_19);

						WSAssert.assertEquals(actualDBDataList, manipReqDataList, false);
					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites MembershipType, MembershipType not available -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that multiple guest preference entries are being stored in CRM as
	// part of a new profile creation through NewProfile service call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_531() {
		try {
			String testName = "newProfile_2008_531";
			WSClient.startTest(testName,
					"Verify that multiple guest preference entries are being stored in CRM as part of a new profile creation through NewProfile service call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "PreferenceGroup", "PreferenceCode" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String prefCodePms = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");
				String prefCodeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE",
						prefCodePms);
				String pmsPrefType = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");
				String extPrefType = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE",
						pmsPrefType);
				String prefCodePms1 = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02");
				String prefCodeExt1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE",
						prefCodePms1);
				String pmsPrefType1 = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02");
				String extPrefType1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE",
						pmsPrefType1);

				setExtVariables();
				WSClient.setData("{var_interface}", interfaceName);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String Lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fName}", fname);
				WSClient.setData("{var_lName}", Lname);
				WSClient.setData("{var_prefCode}", prefCodeExt);
				WSClient.setData("{var_prefType}", extPrefType);
				WSClient.setData("{var_prefCode1}", prefCodeExt1);
				WSClient.setData("{var_prefType1}", extPrefType1);
				// Validation request being created and processed to generate
				// response

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_26");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details
						List<LinkedHashMap<String, String>> expectedReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("Preferences_Preference_Type", "Profile_Preferences_Preference");
						xPath.put("Preferences_Preference_Value", "Profile_Preferences_Preference");
						expectedReqDataList = WSClient.getMultipleNodeList(newProfileReq, xPath, false,
								XMLType.REQUEST);

						List<LinkedHashMap<String, String>> manipReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						for(int i=0;i<expectedReqDataList.size();i++) {
							LinkedHashMap<String,String> mp = expectedReqDataList.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while(itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if(entry.getKey().equalsIgnoreCase("Type1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE", entry.getValue()));
								}
								if(entry.getKey().equalsIgnoreCase("Value1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE", entry.getValue()));
								}
							}
							manipReqDataList.add(mp);
						}
						List<LinkedHashMap<String, String>> actualDBDataList = new ArrayList<LinkedHashMap<String, String>>();
						String QS_20 = WSClient.getQuery("QS_20");
						actualDBDataList = WSClient.getDBRows(QS_20);

						WSAssert.assertEquals(actualDBDataList, expectedReqDataList, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites PreferenceGroup, PreferenceCode not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued by submitting the incorrect conversion code values related to
	// customer information .
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_549() {
		try {
			String testName = "newProfile_2008_549";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued by submitting the incorrect conversion code values related to customer information ",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "Title", "VipLevel", "Nationality", "IdentificationType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				setExtVariables();
				String nationalitypms = OperaPropConfig.getDataSetForCode("Nationality", "DS_02");
				String nationalityext = HTNGLib.getExtValue(resortOperaValue, interfaceName, "NATIONALITY",
						nationalitypms);
				String genderPmsValue = OperaPropConfig.getDataSetForCode("Gender", "DS_01");
				String genderExtValue = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GENDER_MF",
						genderPmsValue);
				String docTypePms = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				String docTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "DOCUMENT_TYPE", docTypePms);
				String vipLevelPms = OperaPropConfig.getDataSetForCode("VipLevel", "DS_01");
				String vipLevelExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "VIP_LEVEL", vipLevelPms);
				String nameTitlePms = OperaPropConfig.getDataSetForCode("Title", "DS_01");
				String nameTitleExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "TITLE", nameTitlePms);

				WSClient.setData("{var_nationality}", nationalityext);
				WSClient.setData("{var_gender}", genderExtValue);
				WSClient.setData("{var_vipLevel}", vipLevelExt);
				WSClient.setData("{var_nameTitle}", nameTitleExt);
				WSClient.setData("{var_docType}", docTypeExt);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				// 1. Validation if response is generated successfully or not
				// when and incorrect Nationality is passed
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When an incorrect Nationality is passed" + "</b>");
				WSClient.setData("{var_nationality}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_27");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

				// 2. Validation if response is generated successfully or not
				// when and incorrect vipcode is passed
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When an incorrect VipCode is passed" + "</b>");
				WSClient.setData("{var_vipLevel}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				WSClient.setData("{var_nationality}", nationalityext);
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq1 = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_27");
				String newProfileResponseXML1 = WSClient.processSOAPMessage(newProfileReq1);

				if (WSAssert.assertIfElementContains(newProfileResponseXML1,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML1, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML1, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

				// 3. Validation if response is generated successfully or not
				// when and incorrect gender is passed
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When an incorrect Gender is passed" + "</b>");
				WSClient.setData("{var_nationality}", nationalityext);
				WSClient.setData("{var_vipLevel}", vipLevelExt);
				WSClient.setData("{var_gender}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq2 = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_27");
				String newProfileResponseXML2 = WSClient.processSOAPMessage(newProfileReq2);

				if (WSAssert.assertIfElementContains(newProfileResponseXML2,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML2, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML2, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

				// 4. Validation if response is generated successfully or not
				// when and incorrect nameTitle is passed
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When an incorrect NameTitle is passed" + "</b>");
				WSClient.setData("{var_nationality}", nationalityext);
				WSClient.setData("{var_vipLevel}", vipLevelExt);
				WSClient.setData("{var_gender}", genderExtValue);
				WSClient.setData("{var_nameTitle}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq3 = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_27");
				String newProfileResponseXML3 = WSClient.processSOAPMessage(newProfileReq3);

				if (WSAssert.assertIfElementContains(newProfileResponseXML3,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML3, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML3, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

				// 5. Validation if response is generated successfully or not
				// when and incorrect Document Type is passed
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When an incorrect DocumentType is passed" + "</b>");
				WSClient.setData("{var_nationality}", nationalityext);
				WSClient.setData("{var_vipLevel}", vipLevelExt);
				WSClient.setData("{var_gender}", genderExtValue);
				WSClient.setData("{var_nameTitle}", nationalityext);
				WSClient.setData("{var_docType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq4 = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_27");
				String newProfileResponseXML4 = WSClient.processSOAPMessage(newProfileReq4);

				if (WSAssert.assertIfElementContains(newProfileResponseXML4,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML4, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML4, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites Title,Nationality,IdentificationType and Viplevel are not available!-----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with email details when passed in
	// Phone Data
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })

	public void newProfile_2008_527() {
		try {
			String testName = "newProfile_2008_527";
			WSClient.startTest(testName,
					"Verify that the new Profile is created with email details when passed in Phone Data",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();

				String phoneTypePms = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_04");
				System.out.println("phonetypepms" + phoneTypePms);
				String phoneTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE", phoneTypePms);
				// String phoneType=HTNGLib.getExtValue(resortOperaValue,
				// interfaceName, "PHONE_TYPE", "EMAIL");

				setExtVariables();
				WSClient.setData("{var_phoneType}", phoneTypeExt);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String Lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String emailFinal = fname + "." + Lname + "@xyzmail.com";
				WSClient.setData("{var_fName}", fname);
				WSClient.setData("{var_lName}", Lname);
				WSClient.setData("{var_emailID}", emailFinal);
				// Validation request being created and processed to generate
				// response

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_28");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);
						WSClient.setData("{var_interface}", interfaceName);
						// Checking for customer details
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();

						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put("PHONE_TYPE", phoneTypePms);
						expectedReqDataMap.put("PHONE_ROLE", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_phoneRole", XMLType.REQUEST));
						expectedReqDataMap.put("PHONE_NUMBER", WSClient.getElementValue(newProfileReq,
								"NamePhone_PhoneData_PhoneNumber", XMLType.REQUEST));
						expectedReqDataMap.put("PRIMARY1", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_primary", XMLType.REQUEST));

						LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
						String QS_06 = WSClient.getQuery("QS_06");
						actualDBDataMap = WSClient.getDBRow(QS_06);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites CommunicationType, CommunicationMethod not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the repetition of the comment text elements are being
	// appended to one comment,
	// is being stored and associated to the profile created through NewProfile
	// call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_537() {
		try {
			String testName = "newProfile_2008_537";
			WSClient.startTest(testName,
					"Verify that the repetition of the comment text elements are being appended to one comment, is being stored and associated to the profile created through NewProfile call ",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();

				// String cmtType=HTNGLib.getRandomExtValue(resortOperaValue,
				// interfaceName, "COMMENT_TYPE");
				String cmtTypePms = OperaPropConfig.getDataSetForCode("NoteType", "DS_01");
				String cmtTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", cmtTypePms);

				setExtVariables();

				WSClient.setData("{var_interface}", interfaceName);
				WSClient.setData("{var_cmtType}", cmtTypeExt);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();


				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_29");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details and comments
						LinkedHashMap<String, String> expectedReqDataMap = new LinkedHashMap<String, String>();
						String commentPart1 = WSClient.getElementValue(newProfileReq, "Comment_Text_TextElement",
								XMLType.REQUEST);
						String commentPart2 = WSClient.getElementValue(newProfileReq, "Comment_Text_TextElement[2]",
								XMLType.REQUEST);
						expectedReqDataMap.put("NAME_ID", operaID);
						expectedReqDataMap.put("NOTE_CODE", cmtTypePms);
						expectedReqDataMap.put("NOTES", commentPart1 + commentPart2);

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_21 = WSClient.getQuery("QS_21");
						actualDBDataMap2 = WSClient.getDBRow(QS_21);

						WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap2, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				// }
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites NoteType not available -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the Guest's address record is created in CRM with the minimum
	// required address elements during the profile creation through NewProfile
	// service call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_521() {
		try {
			String testName = "newProfile_2008_521";
			WSClient.startTest(testName,
					"Verify that the Guest's address record is created in CRM with the minimum required address elements during the profile creation through NewProfile service call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
				setExtVariables();

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_30");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for address details
						String QS_22 = WSClient.getQuery("QS_22");
						LinkedHashMap<String, String> db = WSClient.getDBRow(QS_22);
						LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
						expected.put("ADDRESS1", WSClient.getElementValue(newProfileReq,
								"Addresses_NameAddress_AddressLine", XMLType.REQUEST));
						expected.put("PRIMARY_YN", "Y");
						expected.put("COUNTRY", OperaPropConfig.getDataSetForCode("Country", "DS_01"));

						WSAssert.assertEquals(expected, db, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites AddressType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that multiple phone data records are being stored in CRM as part
	// of a new profile creation through NewProfile service call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2008B", "HTNG" })
	public void newProfile_2008_529() {
		try {
			String testName = "newProfile_2008_529";
			WSClient.startTest(testName,
					"Verify that multiple phone data records are being stored in CRM as part of a new profile creation through NewProfile service call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String phonePmsValue = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
				String phoneExtValue = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue);
				String phonePmsValue1 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03");
				String phoneExtValue1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue1);
				// String phoneExtValue =
				// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"PHONE_TYPE");
				System.out.println("phonePmsValue: " + phonePmsValue + "phonePmsValue1: " + phonePmsValue1
						+ "phonePmsExt: " + phoneExtValue + "phonePmsExt1: " + phoneExtValue1);

				setExtVariables();
				WSClient.setData("{var_phoneType}", phoneExtValue);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
				WSClient.setData("{var_phoneType1}", phoneExtValue1);
				WSClient.setData("{var_phoneRole1}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_32");
				String newProfileResponseXML = WSClient.processSOAPMessage(newProfileReq);

				// Validation if response is generated successfully or not
				if (WSAssert.assertIfElementContains(newProfileResponseXML,
						"NewProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation for checking response is success then its not
					// an
					// Empty response
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_IDs_UniqueID", false)) {
						// Validating Details from NAME table vs REQUEST and
						// RESPONSE
						String operaID = WSClient.getElementValueByAttribute(newProfileResponseXML,
								"Result_IDs_UniqueID", "Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						WSClient.setData("{var_profileID}", operaID);

						// Checking for customer details
						List<LinkedHashMap<String, String>> expectedReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();

						xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
						xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
						xPath.put("NamePhone_PhoneData_PhoneNumber", "Profile_Phones_NamePhone");
						xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

						expectedReqDataList = WSClient.getMultipleNodeList(newProfileReq, xPath, false,
								XMLType.REQUEST);
						List<LinkedHashMap<String, String>> manipReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						for(int i=0;i<expectedReqDataList.size();i++) {
							LinkedHashMap<String,String> mp = expectedReqDataList.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while(itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if(entry.getKey().equalsIgnoreCase("phoneType1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "PHONE_TYPE", entry.getValue()));
								}
							}
							manipReqDataList.add(mp);
						}
						List<LinkedHashMap<String, String>> actualDBDataList = new ArrayList<LinkedHashMap<String, String>>();
						String QS_23 = WSClient.getQuery("QS_23");
						actualDBDataList = WSClient.getDBRows(QS_23);

						WSAssert.assertEquals(actualDBDataList, expectedReqDataList, false);

					} else {
						WSClient.writeToReport(LogStatus.INFO, "Empty Response!!");
					}
				} else {
					WSClient.writeToReport(LogStatus.INFO, "Profile Creation Failed!!");
					if (WSAssert.assertIfElementExists(newProfileResponseXML, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/
						String message = WSAssert.getElementValue(newProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites CommunicationMethod, CommunicationType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

}
