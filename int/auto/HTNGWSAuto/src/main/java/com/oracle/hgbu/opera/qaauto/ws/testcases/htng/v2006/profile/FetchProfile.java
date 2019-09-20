package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2006.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchProfile extends WSSetUp {
	/**
	 * @author psarawag
	 */
	@Test(groups = { "sanity", "FetchProfile", "HTNG2006", "HTNG" })
	/*****
	 * Fetching the profile and validating first name, last name, name type
	 *****/
	/***** Prerequisite : Creating a profile *****/
	public void fetchProfile_2006_20578() {
		try {
			String testName = "fetchProfile_2006_20578";
			WSClient.startTest(testName, "Verify that the profile ID, First Name, Last Name and Profile Type are retrieved onto the response", "sanity");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();


			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(uname);

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
				System.out.println(operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}", resortExtValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
				String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

				if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
						"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_interfaceName}", interfaceName);
					WSClient.setData("{var_resort}", OPERALib.getResort());
					// Validating the fetched profile values with the
					// Database
					String query=WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> profileRecord = WSClient.getDBRow(query);

					LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();

					String first=WSClient.getElementValue(fetchProfileResponseXML, "Customer_PersonName_FirstName", XMLType.RESPONSE);
					String sname=WSClient.getElementValue(fetchProfileResponseXML, "Customer_PersonName_LastName", XMLType.RESPONSE);
					String nameType=WSClient.getElementValue(fetchProfileResponseXML, "FetchProfileResponse_Profile_nameType", XMLType.RESPONSE);
					actualValues.put("FIRST",first);
					actualValues.put("SNAME",sname);
					actualValues.put("NAME_TYPE",nameType.toUpperCase());

					WSAssert.assertEquals(profileRecord, actualValues, false);
				}
				if (WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"The text displayed in the response is :" + message);
				}


			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_38822(){
		try{
			String testName = "fetchProfile_2006_38822";
			WSClient.startTest(testName, "Verify the Address of the profile that are retrieved onto the response", "minimumRegression");

			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				//			String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				//			String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				//			System.out.println("address :"+state_code+" "+state);
				//			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				//			System.out.println("address_country :"+country_code);
				String state=OperaPropConfig.getDataSetForCode("State", "DS_01");
				String country_code=OperaPropConfig.getDataSetForCode("Country", "DS_01");
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));


				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID =CreateProfile.createProfile("DS_04");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);

					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressid=WSClient.getDBRow(query1);

					if(addressid.size()>0){


						//Validation request being created and processed to generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
						String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

						if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

							HashMap<String, String> xPath = new HashMap<String, String>();

							//database records are being stored in a list of hashmaps
							String query2=WSClient.getQuery("QS_02");
							db=WSClient.getDBRow(query2);

							//xPaths of the records being verified and their parents are being put in a hashmap
							xPath.put("Addresses_NameAddress_AddressLine","Profile_Addresses_NameAddress");
							xPath.put("Addresses_NameAddress_CityName","Profile_Addresses_NameAddress");
							xPath.put("Addresses_NameAddress_StateProv","Profile_Addresses_NameAddress");
							xPath.put("Addresses_NameAddress_CountryCode","Profile_Addresses_NameAddress");
							xPath.put("Addresses_NameAddress_PostalCode","Profile_Addresses_NameAddress");
							xPath.put("Profile_Addresses_NameAddress_addressType","Profile_Addresses_NameAddress");

							//response records are being stored in a list of hashmaps
							actualValues=WSClient.getSingleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

							//database records and response records are being compared
							WSAssert.assertEquals( db,actualValues, false);
						}
						if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}

					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Address failed!------ Create Profile -----Blocked");
					}
				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}

	}
	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_20345(){
		try{
			String testName = "fetchProfile_2006_20345";
			WSClient.startTest(testName, "Verify the multiple Addresses of the profile that are retrieved onto the response", "minimumRegression");

			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				//			String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				//			String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				//			System.out.println("address :"+state_code+" "+state);
				//			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				//			System.out.println("address_country :"+country_code);
				String state=OperaPropConfig.getDataSetForCode("State", "DS_01");
				String country_code=OperaPropConfig.getDataSetForCode("Country", "DS_01");
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));


				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_12");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					String query=WSClient.getQuery("QS_03");
					HashMap<String,String> addressid=WSClient.getDBRow(query);

					if(addressid.size()>0){


						addLOV=OPERALib.fetchAddressLOV(state,country_code);
						WSClient.setData("{var_state1}", state);
						WSClient.setData("{var_country1}", country_code);
						WSClient.setData("{var_addressType1}",
								OperaPropConfig.getDataSetForCode("AddressType", "DS_02"));


						WSClient.setData("{var_zip1}", addLOV.get("Zip"));
						WSClient.setData("{var_city1}", addLOV.get("City"));

						//Prerequisite 2: Change Profile
						OPERALib.setOperaHeader(uname);
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_11");
						String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

						if(WSAssert.assertIfElementExists(changeProfileResponseXML,
								"ChangeProfileRS_Success", true)){
							if(WSAssert.assertIfElementExists(changeProfileResponseXML,
									"Addresses_AddressInfo_UniqueID_ID", true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Second Address is attahed to the profile</b>");
								//Validation request being created and processed to generate response
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
								String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

								if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
										"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

									HashMap<String, String> xPath = new HashMap<String, String>();

									//database records are being stored in a list of hashmaps
									String query2=WSClient.getQuery("QS_11");
									db=WSClient.getDBRows(query2);

									//xPaths of the records being verified and their parents are being put in a hashmap
									xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
									xPath.put("Profile_Addresses_NameAddress_primary","Profile_Addresses_NameAddress");
									xPath.put("Profile_Addresses_NameAddress_addressType","Profile_Addresses_NameAddress");

									//response records are being stored in a list of hashmaps
									actualValues=WSClient.getMultipleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

									//database records and response records are being compared
									WSAssert.assertEquals( actualValues,db, false);
								}
								if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

									/****
									 * Verifying that the error message is populated on the
									 * response
									 ********/

									String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"The text displayed in the response is :" + message);
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"The prerequisites for Address failed!------ Change Profile -----Blocked");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Change Profile -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Address failed!------ Create Profile -----Blocked");
					}
				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}

	}

	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_38825(){
		try{
			String testName = "fetchProfile_2006_38825";
			WSClient.startTest(testName, "Verify that the internal comments are not being fetched by the fetchProfile operation", "minimumRegression");

			String[] prerequisites={"NoteType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisites)){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String comment="This is internal comment";
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_commentType}",
						OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_comment}", comment );

				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_07");
				if(!operaProfileID.equals("error")){
					System.out.println(operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					WSClient.setData("{var_profileID}", operaProfileID);
					String query1=WSClient.getQuery("QS_05");
					HashMap<String,String> commentId=WSClient.getDBRow(query1);
					if(commentId.size()>0){

						//Validation request being created and processed to generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
						String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

						if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							if (WSAssert.assertIfElementExists(fetchProfileResponseXML,
									"Profile_Comments_Comment_commentType", true)) {

								//Validation points is being fetched from response
								String commentText=WSClient.getElementValue(fetchProfileResponseXML, "Comment_Text_TextElement", XMLType.RESPONSE);
								if(commentText.equals(comment)){

									WSClient.writeToReport(LogStatus.FAIL, "Internal Comments are displayed");
									WSClient.writeToReport(LogStatus.FAIL, "Comment Text : "+commentText);

								}else{
									WSClient.writeToReport(LogStatus.PASS, "Internal Comments are not displayed");
								}

							}else{
								WSClient.writeToReport(LogStatus.PASS, "Internal Comments are not displayed");
							}


						}
						if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for comment failed!------ Create Profile -----Blocked");
					}
				}


			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for comment Type failed -----Blocked");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}

	}
	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG","fetchProfile_2006_38824" })
	public void fetchProfile_2006_38824(){
		try {
			String testName = "fetchProfile_2006_38824";
			WSClient.startTest(testName, "Verify that the UDFs is being retrieved correctly when the FetchProfile call "
					+ "is issued with a valid Profile Identifier", "minimumRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"UDFLabel_P"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));

				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					String charUDF = HTNGLib.getUDFLabel("C", "P");
					String numUDF = HTNGLib.getUDFLabel("N", "P");
					String dateUDF = HTNGLib.getUDFLabel("D", "P");
					String charName = HTNGLib.getUDFName("C", charUDF, "P");
					String numName = HTNGLib.getUDFName("N", numUDF, "P");
					String dateName = HTNGLib.getUDFName("D", dateUDF, "P");
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_charName}", charName);
					WSClient.setData("{var_numericName}", numName);
					WSClient.setData("{var_dateName}", dateName);
					WSClient.setData("{var_charValue}", "contact");
					WSClient.setData("{var_numericValue}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
					WSClient.setData("{var_dateValue}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1000}"));

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_08");
					String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResponseXML, "ChangeProfileRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>UDFs are attached to the Profile</b>");
						// Validation request being created and processed to
						// generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
						String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

						if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> dbValues = new LinkedHashMap<String, String>();
							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

							HashMap<String, String> xPath = new HashMap<String, String>();

							// xPaths of the records being verified and their
							// parents are being put in a hashmap
							xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue",
									"Profile_UserDefinedValues_UserDefinedValue");
							xPath.put("UserDefinedValues_UserDefinedValue_NumericValue",
									"Profile_UserDefinedValues_UserDefinedValue");
							xPath.put("UserDefinedValues_UserDefinedValue_DateValue",
									"Profile_UserDefinedValues_UserDefinedValue");

							// database records are being stored in a list of
							// hashmaps
							String query2 = WSClient.getQuery("QS_13");
							dbValues = WSClient.getDBRow(query2);
							LinkedHashMap<String, String> dbValues1 = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> dbValues2 = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> dbValues3 = new LinkedHashMap<String, String>();
							dbValues1.put("CharacterValue1", dbValues.get(charName));
							dbValues2.put("NumericValue1", dbValues.get(numName));
							dbValues3.put("DateValue1", dbValues.get(dateName)+"T00:00:00");
							db.add(dbValues1);
							db.add(dbValues2);
							db.add(dbValues3);
							// response records are being stored in a list of
							// hashmaps
							actualValues = WSClient.getMultipleNodeList(fetchProfileResponseXML, xPath, false,
									XMLType.RESPONSE);

							// database records and response records are being
							// compared
							WSAssert.assertEquals(actualValues, db, false);
						}
						if (WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchProfileResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites failed!------ ChangeProfile-----Blocked");
					}
				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Failed---UDFLabel not available");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_20347(){
		try{
			String testName = "fetchProfile_2006_20347";
			WSClient.startTest(testName, "Verify that the Comments are being retrieved correctly when the FetchProfile call is issued"
					+ " with a valid Profile Identifier", "minimumRegression");
			String[] prerequisites={"NoteType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisites)){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String comment = "This is first Comment";
				String comment2 = "This is second Comment";

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_commentType}",
						OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_comment}", comment );
				WSClient.setData("{var_commentType2}",
						OperaPropConfig.getDataSetForCode("NoteType", "DS_02"));
				WSClient.setData("{var_comment2}", comment2 );

				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_08");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					String query=WSClient.getQuery("QS_05");
					LinkedHashMap<String,String> commentId=WSClient.getDBRow(query);
					if(commentId.size()>=2){

						//Validation request being created and processed to generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
						String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

						if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

							HashMap<String, String> xPath = new HashMap<String, String>();

							//database records are being stored in a list of hashmaps
							String query1=WSClient.getQuery("QS_04");
							db=WSClient.getDBRows(query1);

							//xPaths of the records being verified and their parents are being put in a hashmap
							xPath.put("Comment_Text_TextElement","Profile_Comments_Comment");
							xPath.put("Profile_Comments_Comment_commentType","Profile_Comments_Comment");

							//response records are being stored in a list of hashmaps
							actualValues=WSClient.getMultipleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

							//database records and response records are being compared
							WSAssert.assertEquals( actualValues,db, false);

						}
						if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}

					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for comment failed!------ Create Profile -----Blocked");
					}
				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for comment Type failed -----Blocked");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG", "fetchProfile_2006_20354" })
	public void fetchProfile_2006_20354(){
		try{
			String testName = "fetchProfile_2006_20354";
			WSClient.startTest(testName, "Verify that the customer demographics and document details are being retrieved correctly "
					+ "when the FetchProfile call is issued with a valid Profile Identifier", "minimumRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String [] {"IdentificationType","Title","VipLevel"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
				WSClient.setData("{var_documentType}",
						OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
				WSClient.setData("{var_documentNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));
				WSClient.setData("{var_documentCountry}", country_code);
				WSClient.setData("{var_documentDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_300}"));
				WSClient.setData("{var_birthDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_8395}"));
				WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));

				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_22");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					//Prerequisite 2: Change Profile
					OPERALib.setOperaHeader(uname);
					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_09");
					String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

					if(WSAssert.assertIfElementExists(changeProfileResponseXML,
							"ChangeProfileRS_Success", true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>Identification Type is attached to the profile</b>");
						String query=WSClient.getQuery("QS_01");
						HashMap<String,String> documentId = WSClient.getDBRow(query);

						if(documentId.size()>0){

							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");

							String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

							if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
									"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
								HashMap<String,String> xPath=new HashMap<String,String>();

								//database records are being stored in a hashmap
								String query1=WSClient.getQuery("QS_14");
								db=WSClient.getDBRow(query1);

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("Customer_PersonName_FirstName","Profile_Customer_PersonName");
								xPath.put("Customer_PersonName_MiddleName","Profile_Customer_PersonName");
								xPath.put("Customer_PersonName_LastName","Profile_Customer_PersonName");
								xPath.put("GovernmentIDList_GovernmentID_DocumentType","Customer_GovernmentIDList_GovernmentID");
								//xPath.put("GovernmentIDList_GovernmentID_DocumentNumber","Customer_GovernmentIDList_GovernmentID");
								xPath.put("GovernmentIDList_GovernmentID_EffectiveDate","Customer_GovernmentIDList_GovernmentID");
								xPath.put("GovernmentIDList_GovernmentID_CountryOfIssue","Customer_GovernmentIDList_GovernmentID");
								//xPath.put("FetchProfileResponse_Profile_Customer_gender","FetchProfileResponse_Profile_Customer");
								//xPath.put("FetchProfileResponse_Profile_Customer_birthDate","FetchProfileResponse_Profile_Customer");
								xPath.put("Customer_PersonName_NameTitle","Profile_Customer_PersonName");
								xPath.put("FetchProfileResponse_Profile_vipCode","FetchProfileResponse_Profile");

								//response records are being stored in a hashmap
								actualValues=WSClient.getSingleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

								//						String docNo=actualValues.get("DocumentNumber1");
								//						if(actualValues.containsKey("DocumentNumber1")){
								//							actualValues.put("DocumentNumber1","XXXXXX"+docNo.substring(docNo.length() - 2));
								//						}
								//						String birthDate=actualValues.get("birthDate1");
								//						if(actualValues.containsKey("birthDate1")){
								//							actualValues.put("birthDate1","XXXXXX"+birthDate.substring(birthDate.length() - 2));
								//						}

								//database records and response records are being compared
								WSAssert.assertEquals(db, actualValues, false);
							}
							if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites for Idenification ID's failed!------ Change Profile -----Blocked");
						}

					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites  failed!------ Change Profile -----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Identification Type,title and VipLevel failed -----Blocked");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG", "fetchProfile_2006_38828" })
	public void fetchProfile_2006_38828(){
		try{
			String testName = "fetchProfile_2006_38828";
			WSClient.startTest(testName, "Verify that the emailID information is being retrieved correctly when the FetchProfile call "
					+ "is issued with a valid Profile Identifier", "minimumRegression");

			String[] prerequisite={"CommunicationType"};

			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_emailType2}",
						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
				WSClient.setData("{var_emailType}",
						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_04"));
				WSClient.setData("{var_email}", fname + "." + lname + "@oracle.com");
				WSClient.setData("{var_email2}", fname + "@oracle.com");
				WSClient.setData("{var_primary}", "true");
				WSClient.setData("{var_primary2}", "false");

				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_34");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);

					String query1=WSClient.getQuery("QS_04");
					List<LinkedHashMap<String,String>> phn=WSClient.getDBRows(query1);
					if(phn.size()>=1){
						//Validation request being created and processed to generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
						String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

						if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

							HashMap<String, String> xPath = new HashMap<String, String>();

							//xPaths of the records being verified and their parents are being put in a hashmap
							xPath.put("Phones_NamePhone_PhoneNumber","Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneType","Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_primary","Profile_Phones_NamePhone");

							//database records are being stored in a list of hashmaps
							String query2=WSClient.getQuery("QS_06");
							db=WSClient.getDBRows(query2);

							//response records are being stored in a list of hashmaps
							actualValues=WSClient.getMultipleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

							//database records and response records are being compared
							WSAssert.assertEquals(  actualValues,db,false);
						}
						if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}

					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for attaching emails to a profile failed! -----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites Communication Type not available!-----Blocked");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_20344(){
		try{
			String testName = "fetchProfile_2006_20344";
			WSClient.startTest(testName, "Verify that the phone information is being retrieved correctly when the FetchProfile call "
					+ "is issued with a valid Profile Identifier", "minimumRegression");

			String[] prerequisite={"CommunicationType"};

			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_phoneType}",
						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
				WSClient.setData("{var_phoneType2}",
						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));

				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);

				String operaProfileID = CreateProfile.createProfile("DS_09");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);

					String query=WSClient.getQuery("QS_04");
					List<LinkedHashMap<String,String>> phn=WSClient.getDBRows(query);
					if(phn.size()>=2){
						//Validation request being created and processed to generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
						String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

						if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
								"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

							HashMap<String, String> xPath = new HashMap<String, String>();

							//xPaths of the records being verified and their parents are being put in a hashmap
							xPath.put("Phones_NamePhone_PhoneNumber","Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneType","Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_primary","Profile_Phones_NamePhone");

							//database records are being stored in a list of hashmaps
							String query1=WSClient.getQuery("QS_06");
							db=WSClient.getDBRows(query1);

							//response records are being stored in a list of hashmaps
							actualValues=WSClient.getMultipleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

							//database records and response records are being compared
							WSAssert.assertEquals( actualValues,db, false);
						}
						if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}

					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for attaching to phone numbers to a profile failed! -----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites Communication Type not available!-----Blocked");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_20349(){
		try{
			String testName = "fetchProfile_2006_20349";
			WSClient.startTest(testName, "Verify that the guest preference(s) are being retrieved correctly when the "
					+ "FetchProfile call is issued with a valid Profile Identifier", "minimumRegression");
			String[] prerequisites={"PreferenceGroup","PreferenceCode"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisites)){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String chain = OPERALib.getChain();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();


				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", chain);


				//Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);


					String prefValue = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");

					String prefType = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");

					WSClient.setData("{var_prefType}", prefType);
					WSClient.setData("{var_prefValue}", prefValue);
					String query1=WSClient.getQuery("HTNG2006FetchProfile", "QS_10");
					LinkedHashMap<String,String> prefGlobal = WSClient.getDBRow(query1);
					if(prefGlobal.size()==0){
						WSClient.setData("{var_global}", "true");
					}else{
						WSClient.setData("{var_global}", "false");
					}
					WSClient.setData("{var_prefDesc}",prefType +" "+prefValue);

					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
					String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);

					if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true)) {
						String query2=WSClient.getQuery("QS_01");
						LinkedHashMap<String,String> preference=WSClient.getDBRow(query2);
						if(preference.size()>=1){
							WSClient.writeToReport(LogStatus.INFO, "<b>Preference is attached to the Profile</b>");

							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
							String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

							if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
									"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

								HashMap<String, String> xPath = new HashMap<String, String>();

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("Profile_Preferences_Preference_preferenceType","Profile_Preferences_Preference");
								xPath.put("Profile_Preferences_Preference_preferenceValue","Profile_Preferences_Preference");

								//database records are being stored in a list of hashmaps
								String query5=WSClient.getQuery("QS_07");
								db=WSClient.getDBRow(query5);

								//response records are being stored in a list of hashmaps
								actualValues=WSClient.getSingleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

								//database records and response records are being compared
								WSAssert.assertEquals( db,actualValues, false);
							}
							if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}

						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites for Preference Type and Value were not added to the profile");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Preference Type and Value failed!------ Create Preference -----Blocked");
					}
				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Preference Type and Preference Value Failed!");
			}

		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_38830(){
		try{
			String testName = "fetchProfile_2006_38830";
			WSClient.startTest(testName, "Verify that the Membership Information is being retrieved correctly when the FetchProfile"
					+ " call is issued with a valid Profile Identifier", "minimumRegression");
			String[] prerequisites={"MembershipType","MembershipLevel"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisites)){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();

				String fName=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lName=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_Resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_lname}", lName);
				WSClient.setData("{var_fname}", fName);

				OPERALib.setOperaHeader(OPERALib.getUserName());


				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					System.out.println(operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));



					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipResponseXML = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_Success", true)) {

						if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", true)){
							String memId=WSClient.getElementValue(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>MembershipId =" + memId+"</b>");

							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
							String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

							if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
									"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

								HashMap<String, String> xPath = new HashMap<String, String>();

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("Memberships_NameMembership_MembershipType","Profile_Memberships_NameMembership");
								xPath.put("Memberships_NameMembership_MembershipNumber","Profile_Memberships_NameMembership");
								xPath.put("Memberships_NameMembership_MembershipLevel","Profile_Memberships_NameMembership");
								xPath.put("Memberships_NameMembership_MemberName", "Profile_Memberships_NameMembership");
								xPath.put("Memberships_NameMembership_EffectiveDate", "Profile_Memberships_NameMembership");
								xPath.put("Memberships_NameMembership_ExpirationDate", "Profile_Memberships_NameMembership");
								xPath.put("Profile_Memberships_NameMembership_inactiveDate", "Profile_Memberships_NameMembership");

								//database records are being stored in a list of hashmaps
								String query1=WSClient.getQuery("QS_08");
								db=WSClient.getDBRow(query1);

								//response records are being stored in a list of hashmaps
								actualValues=WSClient.getSingleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

								//database records and response records are being compared
								WSAssert.assertEquals( db, actualValues,false);
							}
							if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for MembershipNo!---- Create Membership  Failed!- -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Create Membership  Failed!- -----Blocked");
					}


				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Membership Type and Membership Level Failed!");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "minimumRegression", "FetchProfile", "HTNG2006", "HTNG", "fetchProfile_2006_20353" })
	public void fetchProfile_2006_20353(){
		try{
			String testName = "fetchProfile_2006_20353";
			WSClient.startTest(testName, "Verify that multiple Memberships Information are being retrieved correctly when the FetchProfile"
					+ " call is issued with a valid Profile Identifier", "minimumRegression");
			String[] prerequisites={"MembershipType","MembershipLevel"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisites)){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();

				String fName=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lName=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_Resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_lname}", lName);
				WSClient.setData("{var_fname}", fName);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					System.out.println(operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + operaProfileID+"</b>");
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));



					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipResponseXML = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_Success", true)) {

						if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", true)){
							String memId=WSClient.getElementValue(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>MembershipId =" + memId+"</b>");
							WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
							WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
							WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));

							createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
							createMembershipResponseXML = WSClient.processSOAPMessage(createMembershipReq);

							if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_Success", true)) {

								if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", true)){
									String memId1=WSClient.getElementValue(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>MembershipId =" + memId1+"</b>");
									//Validation request being created and processed to generate response
									HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
									String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
									String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

									if (WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
											"FetchProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xPath = new HashMap<String, String>();

										//xPaths of the records being verified and their parents are being put in a hashmap
										xPath.put("Memberships_NameMembership_MembershipType","Profile_Memberships_NameMembership");
										xPath.put("Memberships_NameMembership_MembershipNumber","Profile_Memberships_NameMembership");
										xPath.put("Memberships_NameMembership_MembershipLevel","Profile_Memberships_NameMembership");

										//database records are being stored in a list of hashmaps
										String query1=WSClient.getQuery("QS_12");
										db=WSClient.getDBRows(query1);

										//response records are being stored in a list of hashmaps
										actualValues=WSClient.getMultipleNodeList(fetchProfileResponseXML, xPath, false, XMLType.RESPONSE);

										//database records and response records are being compared
										WSAssert.assertEquals(  actualValues,db,false);
									}
									if(WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)){

										/****
										 * Verifying that the error message is populated on the
										 * response
										 ********/

										String message = WSAssert.getElementValue(fetchProfileResponseXML, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The text displayed in the response is :" + message);
									}
								}else{
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for MembershipNo!---- Create Membership  Failed!- -----Blocked");
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Create Membership  Failed!- -----Blocked");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for MembershipNo!---- Create Membership  Failed!- -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Create Membership  Failed!- -----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Membership Type and Membership Level Failed!");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "fullRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_20360() {
		try {
			String testName = "fetchProfile_2006_20360";
			WSClient.startTest(testName, "Verify the error in the response when invalid profileid and invalid resort is passed in the request", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_interfaceName}", interfaceName);
			WSClient.setData("{var_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));

			// Validation request being created and processed to
			// generate response
			WSClient.writeToReport(LogStatus.INFO, "<b>Validating the error message for Wrong Profile ID</b>");
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
			String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

			WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
					"FetchProfileResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on
				 * the response
				 ********/

				String message = WSAssert.getElementValue(fetchProfileResponseXML,
						"Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
			}
			WSClient.setData("{var_extResort}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
			WSClient.writeToReport(LogStatus.INFO, "<b>Validating the error message for Wrong Resort</b>");
			fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
			fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

			WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
					"FetchProfileResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on
				 * the response
				 ********/

				String message = WSAssert.getElementValue(fetchProfileResponseXML,
						"Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "fullRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_20362() {
		try {
			String testName = "fetchProfile_2006_20362";
			WSClient.startTest(testName, "Verify the error in the response when no profileid is passed in the request", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_interfaceName}", interfaceName);

			// Validation request being created and processed to
			// generate response
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_02");
			String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

			WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
					"FetchProfileResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on
				 * the response
				 ********/

				String message = WSAssert.getElementValue(fetchProfileResponseXML,
						"Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "fullRegression", "FetchProfile", "HTNG2006", "HTNG" })
	public void fetchProfile_2006_20363() {
		try {
			String testName = "fetchProfile_2006_20363";
			WSClient.startTest(testName, "Verify the error in the response when non-numeric profileid is passed in the request", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_interfaceName}", interfaceName);
			WSClient.setData("{var_profileID}", WSClient.getKeywordData("{KEYWORD_RANDSTR_6}"));
			// Validation request being created and processed to
			// generate response
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String fetchProfileReq = WSClient.createSOAPMessage("HTNG2006FetchProfile", "DS_01");
			String fetchProfileResponseXML = WSClient.processSOAPMessage(fetchProfileReq);

			WSAssert.assertIfElementValueEquals(fetchProfileResponseXML,
					"FetchProfileResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(fetchProfileResponseXML, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on
				 * the response
				 ********/

				String message = WSAssert.getElementValue(fetchProfileResponseXML,
						"Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
}
