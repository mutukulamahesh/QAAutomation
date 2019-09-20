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


public class ProfileLookup extends WSSetUp {

	/**
	 * @author psarawag 
	 * Description: Verify lastname and profileid when Wildcard search is performed on the Last Name
	 */
	
	@Test(groups = { "sanity", "ProfileLookUp", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20420() {
		try {
			String testName = "profileLookup_2006_20420";
			WSClient.startTest(testName, "Verify lastname and profileid when Wildcard search is performed on the Last Name", "sanity");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String lastNamePrefix = WSClient.getKeywordData("{KEYWORD_RANDSTR_3}").toUpperCase();

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_lnameprefix}", lastNamePrefix);
			WSClient.setData("{var_lname}", lastNamePrefix + WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
			String profileId=CreateProfile.createProfile("DS_06");
			if(!profileId.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileId+"</b>");
					WSClient.setData("{var_lname}", lastNamePrefix + WSClient.getKeywordData("{KEYWORD_LNAME}"));
					
					//Prerequisite 2: Create Profile 2
					String profileId1=CreateProfile.createProfile("DS_06");
					if(!profileId1.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileId1+"</b>");
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_01");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
								
								//database records are being stored in a list of hashmaps
								String query=WSClient.getQuery("QS_01");
								db = WSClient.getDBRows(query);

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("ProfileLookup_PersonName_LastName",
										"LookupResponse_ProfileLookups_ProfileLookup");
								xPath.put("ProfileLookup_ProfileIDs_UniqueID",
										"LookupResponse_ProfileLookups_ProfileLookup");
								
								//response records are being stored in a list of hashmaps
								actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath, false,
										XMLType.RESPONSE);
								
								//database records and response records are being compared
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						} 
				}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	
	
	/**
	 * @author psarawag 
	 * Description: Verify firstname,middlename,lastname,profileid,nametitle is populated in the response
	 */
	
	

	@Test(groups = { "minimumRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20426() {
		try {
			String testName = "profileLookup_2006_20426";
			WSClient.startTest(testName, "Verify firstname,middlename,lastname,profileid,nametitle is populated in the response", "minimumRegression");
			
			String prerequisite[]={"Title"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_title}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
			String profileId=CreateProfile.createProfile("DS_16");
			if(!profileId.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileId+"</b>");
				WSClient.setData("{var_profileID}", profileId);

					//Validation request being created and processed to generate response
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_05");
					String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
					HashMap<String, String> xPath = new HashMap<String, String>();
					if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
							"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
						
						//database records are being stored in a list of hashmaps
						String query=WSClient.getQuery("QS_02");
						db = WSClient.getDBRow(query);

						//xPaths of the records being verified and their parents are being put in a hashmap
						xPath.put("ProfileLookup_PersonName_LastName", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_ProfileIDs_UniqueID", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_PersonName_FirstName", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_PersonName_MiddleName", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_PersonName_NameTitle", "LookupResponse_ProfileLookups_ProfileLookup");
						
						//response records are being stored in a list of hashmaps
						actualValues = WSClient.getSingleNodeList(profileLookUpResponseXML, xPath, false,
								XMLType.RESPONSE);
						
						//database records and response records are being compared
						WSAssert.assertEquals( db,actualValues, false);
					}
					if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The text displayed in the response is :" + message);
					}
				} }else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for Name Title failed!------ Create Profile -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * @author psarawag 
	 * Description: Verify firstname,lastname,profileid in retreived when Wildcard search is performed on the First Name
	 */

	@Test(groups = { "minimumRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_38820() {
		try {
			String testName = "profileLookup_2006_38820";
			WSClient.startTest(testName, "Verify firstname,lastname,profileid is retreived when Wildcard search is performed on the First Name", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			WSClient.setData("{var_fname}", firstName);
			WSClient.setData("{var_lname}", lastName);

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
			String profileId=CreateProfile.createProfile("DS_06");
			if(!profileId.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileId+"</b>");
					WSClient.setData("{var_fname}",
							firstName.substring(0, 2) + WSClient.getKeywordData("{KEYWORD_FNAME}"));
					WSClient.setData("{var_lname}", lastName);
					
					//Prerequisite 2: Create Profile 2
					String profileId1=CreateProfile.createProfile("DS_06");
					if(!profileId1.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileId1+"</b>");
							WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
							WSClient.setData("{var_lname}", lastName);
							
							//Prerequisite 3: Create Profile 3
							String profileId2=CreateProfile.createProfile("DS_06");
							if(!profileId2.equals("error")){
								WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileId2+"</b>");
									WSClient.setData("{var_fname}", firstName.substring(0, 2));
									WSClient.setData("{var_lname}", lastName);
									
									//Validation request being created and processed to generate response
									HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
									String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp",
											"DS_02");
									String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xPath = new HashMap<String, String>();
									if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
											"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
										
										//database records are being stored in a list of hashmaps
										String query=WSClient.getQuery("QS_03");
										db = WSClient.getDBRows(query);

										//xPaths of the records being verified and their parents are being put in a hashmap
										xPath.put("ProfileLookup_ProfileIDs_UniqueID",
												"LookupResponse_ProfileLookups_ProfileLookup");
										xPath.put("ProfileLookup_PersonName_LastName",
												"LookupResponse_ProfileLookups_ProfileLookup");
										xPath.put("ProfileLookup_PersonName_FirstName",
												"LookupResponse_ProfileLookups_ProfileLookup");
										
										//response records are being stored in a list of hashmaps
										actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath,
												false, XMLType.RESPONSE);
										
										//database records and response records are being compared
										WSAssert.assertEquals(actualValues, db, false);
									}
									if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

										/****
										 * Verifying that the error message is populated on the
										 * response
										 ********/

										String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The text displayed in the response is :" + message);
									}
								} 
						} 
				} 
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	
	
	/**
	 * @author psarawag 
	 * Description:Verify that the profileid which are populated on the response belong to the city passed on the request
	 */

	@Test(groups = { "minimumRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20425() {
		try {

			String testName = "profileLookup_2006_20425";
			WSClient.startTest(testName, "Verify that the profileid which are populated on the response belong to the city passed on the request", "minimumRegression");
			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
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
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_addressType}",
					OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
			OPERALib.setOperaHeader(uname);
			
			String profileID =CreateProfile.createProfile("DS_20");
			if(!profileID.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					String query=WSClient.getQuery("QS_03");
					HashMap<String,String> addressId=WSClient.getDBRow(query);
					if(addressId.size()>0){
						HashMap<String, String> addLOV1 = OPERALib.fetchAddressLOV(state,country_code);
						WSClient.setData("{var_city}", addLOV1.get("City"));
						WSClient.setData("{var_zip}", addLOV1.get("Zip"));
					
					WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
					//Prerequisite 2: Create Profile 2
					String profileID1 =CreateProfile.createProfile("DS_20");
					if(!profileID1.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID1+"</b>");
							WSClient.setData("{var_profileID}", profileID1);
							String query1=WSClient.getQuery("QS_03");
							HashMap<String,String> addressId2=WSClient.getDBRow(query1);
							if(addressId2.size()>0){
							
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_03");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
								
								//database records are being stored in a list of hashmaps
								String query2=WSClient.getQuery("QS_04");
								db = WSClient.getDBRows(query2);

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("ProfileLookup_ProfileIDs_UniqueID",
										"LookupResponse_ProfileLookups_ProfileLookup");
								xPath.put("ProfileLookup_Address_CityName",
										"LookupResponse_ProfileLookups_ProfileLookup");
								
								//response records are being stored in a list of hashmaps
								actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath, false,
										XMLType.RESPONSE);
								
								//database records and response records are being compared
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
							}else{
								WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
							}
						}
				}else{
					WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
				}
				} 
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * @author psarawag 
	 * Description: Verify that the profileid which are populated on the response belong to the state passed on the request
	 */

	@Test(groups = { "minimumRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20424() {
		try {
			String testName = "profileLookup_2006_20424";
			WSClient.startTest(testName, "Verify that the profileid which are populated on the response belong to the state passed on the request", "minimumRegression");
			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
//			String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
//			String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
//			System.out.println("address :"+state_code+" "+state);
//			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
//			System.out.println("address_country :"+country_code);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String state=OperaPropConfig.getDataSetForCode("State", "DS_01");
			String country_code=OperaPropConfig.getDataSetForCode("Country", "DS_01");
			
			HashMap<String, String> addLOV = new HashMap<String, String>();
			addLOV = OPERALib.fetchAddressLOV(state,country_code);

			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_addressType}",
					OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
			String profileID = CreateProfile.createProfile("DS_20");
			if(!profileID.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressId=WSClient.getDBRow(query1);
					if(addressId.size()>0){
						state=OperaPropConfig.getDataSetForCode("State", "DS_02");
						country_code=OperaPropConfig.getDataSetForCode("Country", "DS_02");
						HashMap<String, String> addLOV1 = OPERALib.fetchAddressLOV(state,country_code);
						WSClient.setData("{var_city}", addLOV1.get("City"));
						WSClient.setData("{var_state}", state);
						WSClient.setData("{var_country}", country_code);
						WSClient.setData("{var_zip}", addLOV1.get("Zip"));
					//Prerequisite 2: Create Profile 2
						String profileID1 = CreateProfile.createProfile("DS_20");
						if(!profileID1.equals("error")){
							WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID1+"</b>");
							WSClient.setData("{var_profileID}", profileID1);
							String query2=WSClient.getQuery("QS_03");
							HashMap<String,String> addressId1=WSClient.getDBRow(query2);
							if(addressId1.size()>0){
								WSClient.setData("{var_state}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "STATE", state));
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_04");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
								WSClient.setData("{var_state}", state);
								//database records are being stored in a list of hashmaps
								String query3=WSClient.getQuery("QS_05");
								db = WSClient.getDBRows(query3);

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("ProfileLookup_ProfileIDs_UniqueID",
										"LookupResponse_ProfileLookups_ProfileLookup");
								xPath.put("ProfileLookup_Address_StateProv",
										"LookupResponse_ProfileLookups_ProfileLookup");
								
								//response records are being stored in a list of hashmaps
								actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath, false,
										XMLType.RESPONSE);
								
								//database records and response records are being compared
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
							}else{
								WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
							}
						} 
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
						}
				} 
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * @author psarawag 
	 * Description: Verify that only primary Address is populated in the response
	 */

	@Test(groups = { "minimumRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20427() {
		try {
			String testName = "profileLookup_2006_20427";
			WSClient.startTest(testName, "Verify that only primary Address is populated in the response", "minimumRegression");
			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
			String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
			System.out.println("address :"+state_code+" "+state);
			String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
			System.out.println("address_country :"+country_code);
			
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			HashMap<String, String> addLOV = new HashMap<String, String>();
			addLOV = OPERALib.fetchAddressLOV(state,country_code);
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			WSClient.setData("{var_addressType}",
					OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
			
			
			// Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
			String profileID = CreateProfile.createProfile("DS_20");
			if(!profileID.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					String query=WSClient.getQuery("QS_03");
					HashMap<String,String> addressId=WSClient.getDBRow(query);
					if(addressId.size()>0){
						state_code = HTNGLib.getRandomExtValue(resortOperaValue, interfaceName, "STATE");
						state = HTNGLib.getPmsValue(resortOperaValue, interfaceName, "STATE", state_code);
						System.out.println("address :" + state_code + " " + state);
						country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
						System.out.println("address_country :" + country_code);

						addLOV = OPERALib.fetchAddressLOV(state, country_code);
						WSClient.setData("{var_state1}", state);
						WSClient.setData("{var_country1}", country_code);
						WSClient.setData("{var_addressType1}",
								OperaPropConfig.getDataSetForCode("AddressType", "DS_02"));

						WSClient.setData("{var_zip1}", addLOV.get("Zip"));
						WSClient.setData("{var_city1}", addLOV.get("City"));
						
						// Prerequisite 2: Change Profile
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_11");
						String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

						if (WSAssert.assertIfElementExists(changeProfileResponseXML, "ChangeProfileRS_Success", true)) {
							if (WSAssert.assertIfElementExists(changeProfileResponseXML,
									"Addresses_AddressInfo_UniqueID_ID", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Second address is attached to the Profile</b>");
					//Validation request being created and processed to generate response
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_05");
					String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

					HashMap<String, String> xPath = new HashMap<String, String>();
					if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
							"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
						
						//xPaths of the records being verified and their parents are being put in a hashmap
						xPath.put("ProfileLookup_ProfileIDs_UniqueID", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_Address_AddressLine", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_Address_CityName", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_Address_StateProv", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_Address_CountryCode", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_Address_PostalCode", "LookupResponse_ProfileLookups_ProfileLookup");
						
						//database records are being stored in a list of hashmaps
						String query1=WSClient.getQuery("QS_06");
						db = WSClient.getDBRow(query1);
						
						//response records are being stored in a list of hashmaps
						actualValues = WSClient.getSingleNodeList(profileLookUpResponseXML, xPath, false,
								XMLType.RESPONSE);
						
						//database records and response records are being compared
						WSAssert.assertEquals( db,actualValues, false);

					}
					if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The text displayed in the response is :" + message);
					}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"The prerequisites for Address failed!------ Change Profile -----Blocked");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Change Profile -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
					}
				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * @author psarawag 
	 * Description: Verify that PhoneNumber is populated in the response
	 */

	@Test(groups = { "minimumRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20428() {
		try {
			String testName = "profileLookup_2006_20428";
			WSClient.startTest(testName, "Verify that PhoneNumber is populated in the response", "minimumRegression");
			String prerequisite[]={"CommunicationType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
			String profileID = CreateProfile.createProfile("DS_21");
			if(!profileID.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					String query=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
					if(phn.size()>0){
					//Validation request being created and processed to generate response
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_05");
					String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
					HashMap<String, String> xPath = new HashMap<String, String>();
					if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
							"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
						
						//database records are being stored in a list of hashmaps
						String query1=WSClient.getQuery("QS_07");
						db = WSClient.getDBRow(query1);
						
						//xPaths of the records being verified and their parents are being put in a hashmap
						xPath.put("ProfileLookup_ProfileIDs_UniqueID", "LookupResponse_ProfileLookups_ProfileLookup");
						xPath.put("ProfileLookup_Phone_PhoneNumber", "LookupResponse_ProfileLookups_ProfileLookup");
						
						//response records are being stored in a list of hashmaps
						actualValues = WSClient.getSingleNodeList(profileLookUpResponseXML, xPath, false,
								XMLType.RESPONSE);
						
						//database records and response records are being compared
						WSAssert.assertEquals( db,actualValues, false);

					}
					if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The text displayed in the response is :" + message);
					}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
					}
				} 
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
//	/**
//	 * @author psarawag 
//	 * Description: Verify that EmailId is populated in the response
//	 */
//
//	@Test(groups = { "minimumRegression", "ProfileLookup", "HTNG2006", "HTNG" })
//	public void profileLookup_2006_39360() {
//		try {
//			String testName = "profileLookup_2006_39360";
//			WSClient.startTest(testName, "Verify that EmailId is populated in the response", "minimumRegression");
//			String prerequisite[]={"CommunicationType"};
//			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
//			
//			String resortOperaValue = OPERALib.getResort();
//			String interfaceName = HTNGLib.getHTNGInterface();
//			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
//			WSClient.setData("{var_chain}", OPERALib.getChain());
//			WSClient.setData("{var_extResort}", resortExtValue);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			String uname = OPERALib.getUserName();
//			String pwd = OPERALib.getPassword();
//			String fromAddress=HTNGLib.getInterfaceFromAddress();
//			String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
//			String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
//			
//			WSClient.setData("{var_fname}", fname);
//			WSClient.setData("{var_lname}", lname);
//			WSClient.setData("{var_email}", fname+"."+lname+"@oracle.com");
//			WSClient.setData("{var_primary}", "true");
//			//Prerequisite 1: Create Profile 1
//			OPERALib.setOperaHeader(uname);
//
//					String profileID = CreateProfile.createProfile("DS_13");
//					if(!profileID.equals("error")){
//						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
//					WSClient.setData("{var_profileID}", profileID);
//					String query1=WSClient.getQuery("QS_04");
//					LinkedHashMap<String, String> phn = WSClient.getDBRow(query1);
//					if(phn.size()>0){
//					//Validation request being created and processed to generate response
//					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
//					String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_05");
//					String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);
//					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
//					LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
//					HashMap<String, String> xPath = new HashMap<String, String>();
//					if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
//							"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
//						
//						//database records are being stored in a list of hashmaps
//						String query2=WSClient.getQuery("QS_07");
//						db = WSClient.getDBRow(query2);
//						
//						//xPaths of the records being verified and their parents are being put in a hashmap
//						xPath.put("ProfileLookup_ProfileIDs_UniqueID", "LookupResponse_ProfileLookups_ProfileLookup");
//						xPath.put("ProfileLookup_Phone_PhoneNumber", "LookupResponse_ProfileLookups_ProfileLookup");
//						
//						//response records are being stored in a list of hashmaps
//						actualValues = WSClient.getSingleNodeList(profileLookUpResponseXML, xPath, false,
//								XMLType.RESPONSE);
//						
//						//database records and response records are being compared
//						WSAssert.assertEquals( db,actualValues, false);
//
//					}
//					if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {
//
//						/****
//						 * Verifying that the error message is populated on the
//						 * response
//						 ********/
//
//						String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
//								XMLType.RESPONSE);
//						WSClient.writeToReport(LogStatus.INFO,
//								"The text displayed in the response is :" + message);
//					}
//					}else{
//						WSClient.writeToReport(LogStatus.WARNING,
//								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
//					}
//				} 
//			}else{
//				WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
//			}
//		} catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		} finally {
//		}
//	}
	/**
	 * @author psarawag 
	 * Description: Verify that the profileid which are populated on the response belong to the country passed on the request
	 */

	@Test(groups = { "targetedRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20452() {
		try {

			String testName = "profileLookup_2006_20452";
			WSClient.startTest(testName, "Verify that the profileid which are populated on the response belong to the country passed on the request", "targetedRegression");
			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
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
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_addressType}",
					OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
					
					String profileID =CreateProfile.createProfile("DS_20");
					if(!profileID.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressId=WSClient.getDBRow(query1);
					if(addressId.size()>0){
						state=OperaPropConfig.getDataSetForCode("State", "DS_02");
						country_code=OperaPropConfig.getDataSetForCode("Country", "DS_02");
						HashMap<String,String> addLOV1=OPERALib.fetchAddressLOV(state,country_code);
						WSClient.setData("{var_city}", addLOV1.get("City"));
						WSClient.setData("{var_state}", state);
						WSClient.setData("{var_country}", country_code);
						WSClient.setData("{var_zip}", addLOV1.get("Zip"));
					
					WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
					//Prerequisite 2: Create Profile 2
							String profileID1 =CreateProfile.createProfile("DS_20");
							if(!profileID1.equals("error")){
								WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID1+"</b>");
							WSClient.setData("{var_profileID}", profileID1);
							String query2=WSClient.getQuery("QS_03");
							HashMap<String,String> addressId1=WSClient.getDBRow(query2);
							if(addressId1.size()>0){
							WSClient.setData("{var_country}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", country_code));
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_06");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
								WSClient.setData("{var_country}", country_code);
								//database records are being stored in a list of hashmaps
								String query3=WSClient.getQuery("QS_09");
								db = WSClient.getDBRows(query3);

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("ProfileLookup_ProfileIDs_UniqueID",
										"LookupResponse_ProfileLookups_ProfileLookup");
								xPath.put("ProfileLookup_Address_CountryCode",
										"LookupResponse_ProfileLookups_ProfileLookup");
								
								//response records are being stored in a list of hashmaps
								actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath, false,
										XMLType.RESPONSE);
								
								//database records and response records are being compared
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
							}else{
								WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
							}
						} 
				}else{
					WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
				}
				} 
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	/**
	 * @author psarawag 
	 * Description: Verify that the profileid which are populated on the response belong to the zipcode passed on the request
	 */

	@Test(groups = { "targetedRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20453() {
		try {

			String testName = "profileLookup_2006_20453";
			WSClient.startTest(testName, "Verify that the profileid which are populated on the response belong to the zipcode passed on the request", "targetedRegression");
			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
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
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_city}", addLOV.get("City"));
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_zip}", addLOV.get("Zip"));
			WSClient.setData("{var_addressType}",
					OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
					
					String profileID =CreateProfile.createProfile("DS_20");
					if(!profileID.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressId=WSClient.getDBRow(query1);
					if(addressId.size()>0){
						HashMap<String,String> addLOV1=OPERALib.fetchAddressLOV(state,country_code);
						WSClient.setData("{var_city}", addLOV1.get("City"));
						WSClient.setData("{var_zip}", addLOV1.get("Zip"));
					
					WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
					//Prerequisite 2: Create Profile 2
							String profileID1 =CreateProfile.createProfile("DS_20");
							if(!profileID1.equals("error")){
								WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID1+"</b>");
							WSClient.setData("{var_profileID}", profileID1);
							String query2=WSClient.getQuery("QS_03");
							HashMap<String,String> addressId2=WSClient.getDBRow(query2);
							if(addressId2.size()>0){
							
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_07");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
								
								//database records are being stored in a list of hashmaps
								String query3=WSClient.getQuery("QS_10");
								db = WSClient.getDBRows(query3);

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("ProfileLookup_ProfileIDs_UniqueID",
										"LookupResponse_ProfileLookups_ProfileLookup");
								xPath.put("ProfileLookup_Address_PostalCode",
										"LookupResponse_ProfileLookups_ProfileLookup");
								
								//response records are being stored in a list of hashmaps
								actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath, false,
										XMLType.RESPONSE);
								
								//database records and response records are being compared
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
							}else{
								WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
							}
						} 
				}else{
					WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
				}
				} 
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "targetedRegression", "ProfileLookup", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20454() {
		try {

			String testName = "profileLookup_2006_20454";
			WSClient.startTest(testName, "Verify that the profileid which are populated when wildcard search is performed on the city", "targetedRegression");
			String prerequisite[]={"AddressType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String state="WI";
			String country_code=OperaPropConfig.getDataSetForCode("Country", "DS_01");
			//HashMap<String, String> addLOV = new HashMap<String, String>();
			
			//addLOV=OPERALib.fetchAddressLOV(state,country_code);
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_interfaceID}", HTNGLib.getHTNGInterface());
			
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_city}", "Maplewood");
			WSClient.setData("{var_state}", state);
			WSClient.setData("{var_country}", country_code);
			WSClient.setData("{var_zip}", "54226");
			WSClient.setData("{var_addressType}",
					OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

			//Prerequisite 1: Create Profile 1
			OPERALib.setOperaHeader(uname);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profiles which have starting letters of city same</b>");
					String profileID =CreateProfile.createProfile("DS_20");
					if(!profileID.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressId=WSClient.getDBRow(query1);
					if(addressId.size()>0){
					//HashMap<String, String> addLOV1 = OPERALib.fetchAddressLOV(state,country_code);
					WSClient.setData("{var_city}", "Manassas");
					WSClient.setData("{var_zip}", "20108");
					WSClient.setData("{var_state}", "VA");
					WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
					//Prerequisite 2: Create Profile 2
							String profileID1 =CreateProfile.createProfile("DS_20");
							if(!profileID1.equals("error")){
								WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID1+"</b>");
							WSClient.setData("{var_profileID}", profileID1);
							String query2=WSClient.getQuery("QS_03");
							HashMap<String,String> addressId2=WSClient.getDBRow(query2);
							if(addressId2.size()>0){
								HashMap<String, String> addLOV1 = OPERALib.fetchAddressLOV(state,country_code);
								WSClient.setData("{var_city}", addLOV1.get("City"));
								WSClient.setData("{var_zip}", addLOV1.get("Zip"));
								WSClient.setData("{var_state}", state);
								WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
								//Prerequisite 3: Create Profile 3
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile which have different starting letters of city</b>");
										String profileID2 =CreateProfile.createProfile("DS_20");
										if(!profileID2.equals("error")){
											WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileID2+"</b>");
										WSClient.setData("{var_profileID}", profileID1);
										String query3=WSClient.getQuery("QS_03");
										HashMap<String,String> addressId3=WSClient.getDBRow(query3);
										if(addressId3.size()>0){
											WSClient.setData("{var_city}", "Ma");
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_03");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag","SUCCESS", false)) {
								
								//database records are being stored in a list of hashmaps
								String query4=WSClient.getQuery("QS_11");
								db = WSClient.getDBRows(query4);

								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("ProfileLookup_ProfileIDs_UniqueID",
										"LookupResponse_ProfileLookups_ProfileLookup");
								xPath.put("ProfileLookup_Address_CityName",
										"LookupResponse_ProfileLookups_ProfileLookup");
								
								//response records are being stored in a list of hashmaps
								actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath, false,
										XMLType.RESPONSE);
								
								//database records and response records are being compared
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
										}else{
											WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
										}	}
							}else{
								WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
							}
						} 
				}else{
					WSClient.writeToReport(LogStatus.WARNING, "The prequisites for address Failed..... Address was not attached to the profile");
				}
				} 
			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "fullRegression", "ProfileLookUp", "HTNG2006", "HTNG" })
	public void profileLookup_2006_20493() {
		try {
			String testName = "profileLookup_2006_20493";
			WSClient.startTest(testName, "Verify that max 100 profiles are retrieved on the response", "fullRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String lastNamePrefix = "SA";

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_lnameprefix}", lastNamePrefix);
			OPERALib.setOperaHeader(uname);
			HashMap<String,String> profile=WSClient.getDBRow(WSClient.getQuery("HTNG2006ProfileLookUp","QS_12"));
			int count;
			if(profile.size()==0){
				 count=0;
			}else{
				count=Integer.parseInt(profile.get("COUNT"));
			}
				while(count<101){
			WSClient.setData("{var_lname}", lastNamePrefix + WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));

			//Prerequisite 1: Create Profile 1

					String profileId=CreateProfile.createProfile("DS_06");
					if(!profileId.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + profileId+"</b>");
						count++;
					}
				}
			
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_01");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							if (WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag", "SUCCESS",false)) {
								
								//xPaths of the records being verified and their parents are being put in a hashmap
								xPath.put("ProfileLookup_PersonName_LastName",
										"LookupResponse_ProfileLookups_ProfileLookup");
								xPath.put("ProfileLookup_ProfileIDs_UniqueID",
										"LookupResponse_ProfileLookups_ProfileLookup");
								
								//response records are being stored in a list of hashmaps
								actualValues = WSClient.getMultipleNodeList(profileLookUpResponseXML, xPath, false,
										XMLType.RESPONSE);
								
								if(actualValues.size()==100){
									WSClient.writeToReport(LogStatus.PASS, "100 Profiles got retrieved in the response");
								}else{
									WSClient.writeToReport(LogStatus.FAIL, "More than 100 Profiles got retrieved in the response");
								}
							}
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "minimumRegression", "ProfileLookUp", "HTNG2006", "HTNG" })
	public void profileLookup_2006_42603() {
		try {
			String testName = "profileLookup_2006_42603";
			WSClient.startTest(testName, "Verify the error message in the response when lastname is not passed in the request", "minimumRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
		
							//Validation request being created and processed to generate response
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String profileLookUpReq = WSClient.createSOAPMessage("HTNG2006ProfileLookUp", "DS_08");
							String profileLookUpResponseXML = WSClient.processSOAPMessage(profileLookUpReq);

						WSAssert.assertIfElementValueEquals(profileLookUpResponseXML,
									"LookupResponse_Result_resultStatusFlag", "FAIL",false);
								
							
							if (WSAssert.assertIfElementExists(profileLookUpResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(profileLookUpResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}
						 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
}
