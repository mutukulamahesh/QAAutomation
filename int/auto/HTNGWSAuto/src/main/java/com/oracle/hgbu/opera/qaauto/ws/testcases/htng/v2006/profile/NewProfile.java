package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2006.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
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
		// WSClient.setData("{var_extResort}", resortOperaValue);
		WSClient.setData("{var_chain}", chainValue);
	}

	// Verify that the new Profile is created with First Name and Last Name and
	// validating if the created profile is getting subscribed
	@Test(groups = { "sanity", "NewProfile", "HTNG2006", "HTNG" })

	public void newProfile_2006_20503() {
		try {
			String testName = "newProfile_2006_20503";
			WSClient.startTest(testName,
					"Verify that the new Profile is created with First Name and Last Name and validating if the created profile is getting subscribed",
					"sanity");
			String interfaceName = HTNGLib.getHTNGInterface();

			setExtVariables();

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Creating New Profile" + "</b>");
			String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_01");
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
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with only the LAST NAME
	 */
	public void newProfile_2006_20504() {
		try {
			String testName = "newProfile_2006_20504";
			WSClient.startTest(testName, "Verify that the new Profile is created with only the Last Name",
					"minimumRegression");
			// String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();

			setExtVariables();

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_02");
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
					WSAssert.assertEquals(db_result.get("DATABASE_NAME_ID"), externalID, false);

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
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with Customer Information and their document details.
	 */
	public void newProfile_2006_20505() {
		try {
			String testName = "newProfile_2006_20505";
			WSClient.startTest(testName, "Verify that the new Profile is created with document details.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "IdentificationType", "VipLevel", "Title" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
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
				WSClient.setData("{var_vipLevel}", vipLevelExt);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_03");
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

	// Verify that the new Profile is created with phone details.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with phone details.
	 */
	public void newProfile_2006_20507() {
		try {
			String testName = "newProfile_2006_20507";
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

				OPERALib.setOperaHeader(uname);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_05");
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

	// Verify that the new Profile is created with multiple address.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG", "newProfile_2006_40581" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with multiple address details.
	 */
	public void newProfile_2006_40581() {
		try {
			String testName = "newProfile_2006_40581";
			WSClient.startTest(testName, "Verify that the new Profile is created with multiple address.",
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

				OPERALib.setOperaHeader(uname);


				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_04");
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
						for(int i=0;i<expected.size();i++) {
							LinkedHashMap<String,String> mp = expected.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while(itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if(entry.getKey().equalsIgnoreCase("addressType1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", entry.getValue()));
								}
								if(entry.getKey().equalsIgnoreCase("StateProv1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", entry.getValue()));
								}
								if(entry.getKey().equalsIgnoreCase("CountryCode1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "COUNTRY_CODE", entry.getValue()));
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

	// Verify that the new Profile is created with email details.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG", "newProfile_2006_20509" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with email details.
	 */
	public void newProfile_2006_20509() {
		try {
			String testName = "newProfile_2006_20509";
			WSClient.startTest(testName, "Verify that the new Profile is created with email details.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				String phoneTypePms = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_04");
				String phoneTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE", phoneTypePms);
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

				OPERALib.setOperaHeader(uname);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_06");
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
						LinkedHashMap<String, String> expectedReqDataMap2 = new LinkedHashMap<String, String>();

						expectedReqDataMap2.put("NAME_ID", operaID);
						expectedReqDataMap2.put("PHONE_TYPE", phoneTypePms);
						expectedReqDataMap2.put("PHONE_ROLE", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_phoneRole", XMLType.REQUEST));
						expectedReqDataMap2.put("PHONE_NUMBER", WSClient.getElementValue(newProfileReq,
								"Phones_NamePhone_PhoneNumber", XMLType.REQUEST));
						expectedReqDataMap2.put("PRIMARY1", WSClient.getElementValue(newProfileReq,
								"Profile_Phones_NamePhone_primary", XMLType.REQUEST));

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_06 = WSClient.getQuery("QS_06");
						actualDBDataMap2 = WSClient.getDBRow(QS_06);

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
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites CommunicationType, CommunicationMethod not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with preferences.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with customer details and his preferences.
	 */
	public void newProfile_2006_20510() {
		try {
			String testName = "newProfile_2006_20510";
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
				System.out.println("~~~~~~Ext Pref Value: " + prefCodeExt);
				System.out.println("~~~~~~Corresponding Master Pref Value: " + prefCodePms);
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
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_07");
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
						LinkedHashMap<String, String> expectedReqDataMap2 = new LinkedHashMap<String, String>();
						expectedReqDataMap2.put("NAME_ID", operaID);
						expectedReqDataMap2.put("PREFERENCE_TYPE", pmsPrefType);
						expectedReqDataMap2.put("PREFERENCE_VALUE", prefCodePms);

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_07 = WSClient.getQuery("QS_07");
						actualDBDataMap2 = WSClient.getDBRow(QS_07);

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
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites PreferenceGroup, PreferenceCode not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with membership details.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG", "newProfile_2006_20511" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with membership details.
	 */
	public void newProfile_2006_20511() {
		try {
			String testName = "newProfile_2006_20511";
			WSClient.startTest(testName, "Verify that the new Profile is created with membership details.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// String resortExtValue =
				// HTNGLib.getExtResort(resortOperaValue,interfaceName);
				// String chainValue = OPERALib.getChain();

				String membType = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE",
						OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
				String membLevel = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL",
						OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
				String membTypePms = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE",
						membType);
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

				OPERALib.setOperaHeader(uname);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_08");
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

						LinkedHashMap<String, String> expectedReqDataMap2 = new LinkedHashMap<String, String>();
						expectedReqDataMap2.put("NAME_ID", operaID);
						expectedReqDataMap2.put("MEMBERSHIP_TYPE", membTypePms);
						expectedReqDataMap2.put("MEMBERSHIP_CARD_NO", WSClient.getElementValue(newProfileReq,
								"Memberships_NameMembership_MembershipNumber", XMLType.REQUEST));
						expectedReqDataMap2.put("MEMBERSHIP_LEVEL", membLevelPms);
						expectedReqDataMap2.put("JOINED_DATE", WSClient.getElementValue(newProfileReq,
								"Memberships_NameMembership_EffectiveDate", XMLType.REQUEST));
						String fullExpDate = WSClient.getElementValue(newProfileReq,
								"Memberships_NameMembership_ExpirationDate", XMLType.REQUEST);
						String expDate = fullExpDate.substring(0, 7);
						expectedReqDataMap2.put("EXPIRATION_DATE", expDate);

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_08 = WSClient.getQuery("QS_08");
						actualDBDataMap2 = WSClient.getDBRow(QS_08);

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
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites MembershipType, MembershipType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the new Profile is created with comments.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with comments.
	 */
	public void newProfile_2006_20512() {
		try {
			String testName = "newProfile_2006_20512";
			WSClient.startTest(testName, "Verify that the new Profile is created with comments.", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {
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

				OPERALib.setOperaHeader(uname);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_09");
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

						// Checking further customer details and comments
						LinkedHashMap<String, String> expectedReqDataMap2 = new LinkedHashMap<String, String>();

						expectedReqDataMap2.put("NAME_ID", operaID);
						expectedReqDataMap2.put("NOTE_CODE", HTNGLib.getPmsValue(resortOperaValue, interfaceName, "COMMENT_TYPE", WSClient.getElementValue(newProfileReq, "Profile_Comments_Comment_commentType", XMLType.REQUEST)));
						expectedReqDataMap2.put("NOTE_TITLE",
								WSClient.getElementValue(newProfileReq, "Comment_Text_TextElement", XMLType.REQUEST));
						expectedReqDataMap2.put("NOTES",
								WSClient.getElementValue(newProfileReq, "Comment_Text_TextElement", XMLType.REQUEST));

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_09 = WSClient.getQuery("QS_09");
						actualDBDataMap2 = WSClient.getDBRow(QS_09);
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
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites NoteType not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that the name Type conversion occurs when a new Profile is created
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG","newProfile_2006_40582" })

	public void newProfile_2006_40582() {
		try {
			String testName = "newProfile_2006_40582";
			WSClient.startTest(testName, "Verify that the name Type conversion occurs when a new Profile is created",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String nameType = "GUEST";

			setExtVariables();

			WSClient.setData("{var_nameType}", nameType);

			// Validation request being created and processed to generate
			// response
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Creating New Profile" + "</b>");
			String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_10");
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

					expectedReqDataMap.put("FIRST",
							WSClient.getElementValue(newProfileReq, "Customer_PersonName_FirstName", XMLType.REQUEST));
					expectedReqDataMap.put("LAST",
							WSClient.getElementValue(newProfileReq, "Customer_PersonName_LastName", XMLType.REQUEST));
					expectedReqDataMap.put("RESORT_REGISTERED",
							WSClient.getElementValue(newProfileReq, "NewProfileRequest_ResortId", XMLType.REQUEST));
					expectedReqDataMap.put("NAME_TYPE", HTNGLib.getPmsValue(resortOperaValue, interfaceName, "PROFILE_TYPE", nameType));
					expectedReqDataMap.put("NAME_ID", operaID);

					LinkedHashMap<String, String> actualDBDataMap = new LinkedHashMap<String, String>();
					String QS_10 = WSClient.getQuery("QS_10");
					actualDBDataMap = WSClient.getDBRow(QS_10);

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
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG","newProfile_2006_20508" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with single address details.
	 */
	public void newProfile_2006_20508() {
		try {
			String testName = "newProfile_2006_20508";
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

				OPERALib.setOperaHeader(uname);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_11");
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

						String QS_11 = WSClient.getQuery("QS_11");
						LinkedHashMap<String, String> db = WSClient.getDBRow(QS_11);
						LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();

						HashMap<String, String> xpath = new HashMap<String, String>();
						xpath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_CityName", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_StateProv", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_CountryCode", "Profile_Addresses_NameAddress");
						xpath.put("Addresses_NameAddress_PostalCode", "Profile_Addresses_NameAddress");
						xpath.put("Profile_Addresses_NameAddress_addressType", "Profile_Addresses_NameAddress");
						xpath.put("Profile_Addresses_NameAddress_primary", "Profile_Addresses_NameAddress");

						String stateOpera = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", WSClient.getElementValue(newProfileReq, "Addresses_NameAddress_StateProv", XMLType.REQUEST));
						String countryOpera = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "COUNTRY_CODE", WSClient.getElementValue(newProfileReq, "Addresses_NameAddress_CountryCode", XMLType.REQUEST));
						String adTypeOpera = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", WSClient.getElementValue(newProfileReq, "Profile_Addresses_NameAddress_addressType", XMLType.REQUEST));

						expected = WSClient.getSingleNodeList(newProfileReq, xpath, false, XMLType.REQUEST);

						if(expected.containsKey("StateProv1"))  {
							expected.put("StateProv1", stateOpera);
						}
						if(expected.containsKey("CountryCode1"))  {
							expected.put("CountryCode1", countryOpera);
						}
						if(expected.containsKey("addressType1"))  {
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

	// Verify that the new Profile is created with user defined values.
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG" })

	public void newProfile_2006_20513() {
		try {
			String testName = "newProfile_2006_20513";
			WSClient.startTest(testName, "Verify that the new Profile is created with user defined values.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "UDFLabel_P" })) {
				// String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

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
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_12");
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
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Failed---UDFName and UDFLabel not available");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that profile is staged when created with a wrong nameType
	@Test(groups = { "minimumRegression", "NewProfile", "HTNG2006", "HTNG" })
	public void newProfile_2006_41102() {
		try {
			String testName = "newProfile_2006_41102";
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
			String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_10");
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
			String QS_13 = WSClient.getQuery("QS_13");
			actualDBDataMap = WSClient.getDBRow(QS_13);

			WSAssert.assertEquals(expectedReqDataMap, actualDBDataMap, false);

			// Validating INVALID Nametype is getting populated in the
			// STAGE_PROFILE_ERRORS
			String expNType = WSClient.getData("{var_nameType}");
			String actNType = WSClient.getDBRow(WSClient.getQuery("QS_14")).get("ERROR_VALUE");

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
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2006", "HTNG" })

	public void newProfile_2006_20525() {
		try {
			String testName = "newProfile_2006_20525";
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
			String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_13");
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
			String newProfileReq2 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_14");
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
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2006", "HTNG" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with phone details.
	 */
	public void newProfile_2006_20517() {
		try {
			String testName = "newProfile_2006_20517";
			WSClient.startTest(testName,
					"Verify that multiple phone records are being stored in CRM as part of a new profile creation through NewProfile service call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				String phonePmsValue = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
				String phoneExtValue = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue);
				String phonePmsValue1 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02");
				String phoneExtValue1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						phonePmsValue1);
				// String phoneExtValue =
				// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"PHONE_TYPE");
				System.out.println("phonePmsValue: " + phonePmsValue + "phonePmsValue1: " + phonePmsValue1
						+ "phonePmsExt: " + phoneExtValue + "phonePmsExt1: " + phoneExtValue1);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String email = fname + lname + "@DMAIL.COM";
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);

				setExtVariables();

				WSClient.setData("{var_phoneType}", phoneExtValue);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
				WSClient.setData("{var_phoneType1}", phoneExtValue1);
				WSClient.setData("{var_phoneRole1}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
				WSClient.setData("{var_email}", email);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();



				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_16");
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
						String QS_15 = WSClient.getQuery("QS_15");
						actualDBDataList = WSClient.getDBRows(QS_15);

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
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2006", "HTNG","newProfile_2006_20520" })

	/**
	 * @author cbanerji Description : Method to validate if a profile is created
	 *         with comments.
	 */
	public void newProfile_2006_20520() {
		try {
			String testName = "newProfile_2006_20520";
			WSClient.startTest(testName,
					"Verify that multiple comments are being stored and associated to the profile created through NewProfile call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();

				String cmtTypePms = OperaPropConfig.getDataSetForCode("NoteType", "DS_01");
				String cmtTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", cmtTypePms);
				String cmtTypePms1 = OperaPropConfig.getDataSetForCode("NoteType", "DS_02");
				String cmtTypeExt1 = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", cmtTypePms1);

				setExtVariables();

				WSClient.setData("{var_interface}", interfaceName);
				WSClient.setData("{var_cmtType}", cmtTypeExt);
				WSClient.setData("{var_cmtType1}", cmtTypeExt1);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();


				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_17");
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
						String QS16 = WSClient.getQuery("QS_16");
						actualDBDataMap2 = WSClient.getDBRows(QS16);

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

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued by submitting the incorrect conversion code values
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2006", "HTNG" })
	public void newProfile_2006_20526() {
		try {
			String testName = "newProfile_2006_20526";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued by submitting the incorrect conversion code values",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "Title", "VipLevel", "Nationality",
					"IdentificationType", "AddressType", "CommunicationType", "MembershipType", "MembershipLevel",
					"PreferenceGroup", "PreferenceCode" })) {
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
				String addTypePms = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
				String addTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", addTypePms);
				String commTypePms = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
				String commTypeExt = HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE", commTypePms);
				String membTypepms = OperaPropConfig.getDataSetForCode("MembershipType", "DS_01");
				String membTypeext = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE",
						membTypepms);
				String membLevelpms = OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01");
				String membLevelext = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL",
						membLevelpms);
				String prefTypepms = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");
				String prefTypeext = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE",
						prefTypepms);
				String prefValuepms = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");
				String prefValueext = HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE",
						prefValuepms);
				String countrypms = OperaPropConfig.getDataSetForCode("Country", "DS_01");
				String countryext = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", countrypms);
				String statepms = OperaPropConfig.getDataSetForCode("State", "DS_01");
				String stateext = HTNGLib.getExtValue(resortOperaValue, interfaceName, "STATE", statepms);
				WSClient.setData("{var_phoneRole}",
						OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

				HashMap<String, String> address1 = new HashMap<String, String>();
				address1 = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address1.get("City"));
				WSClient.setData("{var_zip}", address1.get("Zip"));
				WSClient.setData("{var_country}", countryext);
				WSClient.setData("{var_state}", stateext);

				WSClient.setData("{var_nationality}", nationalityext);
				WSClient.setData("{var_gender}", genderExtValue);
				WSClient.setData("{var_vipLevel}", vipLevelExt);
				WSClient.setData("{var_nameTitle}", nameTitleExt);
				WSClient.setData("{var_docType}", docTypeExt);
				WSClient.setData("{var_addressType}", addTypeExt);
				WSClient.setData("{var_commType}", commTypeExt);
				WSClient.setData("{var_membType}", membTypeext);
				WSClient.setData("{var_membLevel}", membLevelext);
				WSClient.setData("{var_prefType}", prefTypeext);
				WSClient.setData("{var_prefCode}", prefValueext);

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
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_18");
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
				String newProfileReq1 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_18");
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
				String newProfileReq2 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_18");
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
				String newProfileReq3 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_18");
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
				String newProfileReq4 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_18");
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

				// 6. Validation if response is generated successfully or not
				// when and incorrect AddressType and PhoneType is passed
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "When an incorrect AddressType and PhoneType is passed" + "</b>");
				WSClient.setData("{var_phoneType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				WSClient.setData("{var_addressType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				WSClient.setData("{var_state}", stateext);
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq5 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_19");
				String newProfileResponseXML5 = WSClient.processSOAPMessage(newProfileReq5);

				if (WSAssert.assertIfElementContains(newProfileResponseXML5,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML5, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML5, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				WSClient.setData("{var_phoneType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",
						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01")));
				WSClient.setData("{var_addressType}", addTypeExt);

				// 7. Validation if response is generated successfully or not
				// when and incorrect membership details is passed
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "When an incorrect membership details is passed" + "</b>");
				WSClient.setData("{var_membType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				WSClient.setData("{var_membLevel}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq7 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_20");
				String newProfileResponseXML7 = WSClient.processSOAPMessage(newProfileReq7);

				if (WSAssert.assertIfElementContains(newProfileResponseXML7,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML7, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML7, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				WSClient.setData("{var_membType}", membTypeext);
				WSClient.setData("{var_membLevel}", membLevelext);

				// 8. Validation if response is generated successfully or not
				// when and incorrect preference details is passed
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "When an incorrect preference details is passed" + "</b>");
				WSClient.setData("{var_prefType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				WSClient.setData("{var_prefCode}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq8 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_21");
				String newProfileResponseXML8 = WSClient.processSOAPMessage(newProfileReq8);

				if (WSAssert.assertIfElementContains(newProfileResponseXML8,
						"NewProfileResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "<b>" + "Test case failed as Expected" + "</b>");
				} else {
					// When resultstatuFlag is SUCCESS
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Success Flag Populated in the response" + "</b>");
				}
				if (WSAssert.assertIfElementExists(newProfileResponseXML8, "Result_Text_TextElement", true)) {
					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/
					LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("Result_Text_TextElement", "NewProfileResponse_Result_Text");
					textList = WSClient.getSingleNodeList(newProfileResponseXML8, xPath, false, XMLType.RESPONSE);
					for (int i = 1; i <= textList.size(); i++) {
						String message = textList.get("TextElement" + i);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + "</b>" + message);
					}
				}
				WSClient.setData("{var_prefType}", prefTypeext);
				WSClient.setData("{var_prefCode}", prefValueext);

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites Title,Nationality,IdentificationType, Viplevel, Title, VipLevel, Nationality, IdentificationType, AddressType, CommunicationType, MembershipType, MembershipLevel, PreferenceGroup, PreferenceCode are not available!-----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that an error message is thrown whenever the NewProfile call is
	// issued without submitting the data for minimum required phone elements
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2006", "HTNG" })
	public void newProfile_2006_42623() {
		try {
			String testName = "newProfile_2006_42623";
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
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_22");
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
	// issued without submitting the data for minimum required
	// address,preference or membership elements.
	@Test(groups = { "fullRegression", "NewProfile", "HTNG2006", "HTNG" })
	public void newProfile_2006_20527() {
		try {
			String testName = "newProfile_2006_20527";
			WSClient.startTest(testName,
					"Verify that an error message is thrown whenever the NewProfile call is issued without submitting the data for minimum required "
							+ "address,preference or membership elements.",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType", "MembershipLevel" })) {
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String pmsaddressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
				String pmsstate = OperaPropConfig.getDataSetForCode("State", "DS_01");
				String pmscountry = OperaPropConfig.getDataSetForCode("Country", "DS_01");
				String extaddressType = HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES",
						pmsaddressType);
				String extstate = HTNGLib.getExtValue(resortOperaValue, interfaceName, "STATE", pmsstate);
				String extcountry = HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", pmscountry);

				String membLevel = HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL",
						OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();

				setExtVariables();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_zip}", address.get("Zip"));
				WSClient.setData("{var_country}", extcountry);
				WSClient.setData("{var_state}", extstate);
				WSClient.setData("{var_addressType}", extaddressType);
				WSClient.setData("{var_membLevel}", membLevel);

				// Validation request being created and processed to generate
				// response
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				// 1. Validating Error message for Address Line
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When no Address Line is passed" + "</b>");
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_23");
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

				// 2.Validating Error message for Membership Type and Number
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When no Membership Type and Number is passed" + "</b>");
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq1 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_24");
				String newProfileResponseXML1 = WSClient.processSOAPMessage(newProfileReq1);

				// Validation if response is generated successfully or not
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

				// 3. Validating Error message for Preference Type and Value
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "When no Preference Type and Value is passed" + "</b>");
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String newProfileReq2 = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_25");
				String newProfileResponseXML2 = WSClient.processSOAPMessage(newProfileReq2);

				// Validation if response is generated successfully or not
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

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites AddressType, MembershipLevel not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that multiple membership records submitted on the request message
	// are created in CRM and are associated to the profile created, through
	// NewProfile service call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2006", "HTNG" })
	public void newProfile_2006_20519() {
		try {
			String testName = "newProfile_2006_20519";
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
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_26");
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
						String QS_17 = WSClient.getQuery("QS_17");
						actualDBDataList = WSClient.getDBRows(QS_17);

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
						"The prerequisites MembershipType, MembershipType not available -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// Verify that multiple guest preference entries are being stored in CRM as
	// part of a new profile creation through NewProfile service call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2006", "HTNG" })
	public void newProfile_2006_20518() {
		try {
			String testName = "newProfile_2006_20518";
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
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_27");
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
						xPath.put("Profile_Preferences_Preference_preferenceType", "Profile_Preferences_Preference");
						xPath.put("Profile_Preferences_Preference_preferenceValue", "Profile_Preferences_Preference");
						expectedReqDataList = WSClient.getMultipleNodeList(newProfileReq, xPath, false,
								XMLType.REQUEST);

						List<LinkedHashMap<String, String>> manipReqDataList = new ArrayList<LinkedHashMap<String, String>>();
						for(int i=0;i<expectedReqDataList.size();i++) {
							LinkedHashMap<String,String> mp = expectedReqDataList.get(i);
							Iterator<Map.Entry<String, String>> itr = mp.entrySet().iterator();
							while(itr.hasNext()) {
								Map.Entry<String, String> entry = itr.next();
								if(entry.getKey().equalsIgnoreCase("preferenceType1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE", entry.getValue()));
								}
								if(entry.getKey().equalsIgnoreCase("preferenceValue1")) {
									mp.put(entry.getKey(), HTNGLib.getPmsValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE", entry.getValue()));
								}
							}
							manipReqDataList.add(mp);
						}

						List<LinkedHashMap<String, String>> actualDBDataList = new ArrayList<LinkedHashMap<String, String>>();
						String QS_18 = WSClient.getQuery("QS_18");
						actualDBDataList = WSClient.getDBRows(QS_18);

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

	// Verify that the repetition of the comment text elements are being
	// appended to one comment,
	// is being stored and associated to the profile created through NewProfile
	// call
	@Test(groups = { "targetedRegression", "NewProfile", "HTNG2006", "HTNG" })
	public void newProfile_2006_20521() {
		try {
			String testName = "newProfile_2006_20521";
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
				// WSClient.setData("{var_profileSource}", interfaceName);
				// //WSClient.setData("{var_extResort}", resortExtValue);
				// WSClient.setData("{var_extResort}", resortExtValue);
				// //WSClient.setData("{var_extResort}", resortOperaValue);
				// WSClient.setData("{var_chain}", chainValue);
				// WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_interface}", interfaceName);
				WSClient.setData("{var_cmtType}", cmtTypeExt);

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
				String newProfileReq = WSClient.createSOAPMessage("HTNG2006NewProfile", "DS_28");
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
						expectedReqDataMap.put("NOTE_CODE", HTNGLib.getPmsValue(resortOperaValue, interfaceName, "COMMENT_TYPE", WSClient.getElementValue(newProfileReq, "Profile_Comments_Comment_commentType", XMLType.REQUEST)));
						expectedReqDataMap.put("NOTES", commentPart1 + commentPart2);

						LinkedHashMap<String, String> actualDBDataMap2 = new LinkedHashMap<String, String>();
						String QS_19 = WSClient.getQuery("QS_19");
						actualDBDataMap2 = WSClient.getDBRow(QS_19);

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

}
