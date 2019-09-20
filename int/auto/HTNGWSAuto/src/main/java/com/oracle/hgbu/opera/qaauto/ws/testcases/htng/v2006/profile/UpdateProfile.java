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

public class UpdateProfile extends WSSetUp {



	@Test(groups = { "sanity", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20578() {
		try {
			String testName = "updateProfile_2006_20578";
			WSClient.startTest(testName,
					"Verify that the lastname is updated",
					"sanity");

			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");

			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", firstName);
			WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
			WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			String UniqueId = CreateProfile.createProfile("DS_24");
			if(!UniqueId.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");

				String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

				WSClient.setData("{var_profileID}", UniqueId);
				WSClient.setData("{var_E_profileID}", extProfileID);
				WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
					String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
					WSClient.setData("{var_lname}", lastName);
					WSClient.setData("{var_uniqueId}", extProfileID);
					WSClient.setData("{var_gender}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "GENDER_MF",OperaPropConfig.getDataSetForCode("Gender", "DS_02")));
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_01");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
						HashMap<String, String> xPath = new HashMap<String, String>();
						xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
						xPath.put("Customer_PersonName_FirstName", "Profile_Customer_PersonName");
						xPath.put("Customer_PersonName_LastName", "Profile_Customer_PersonName");
						xPath.put("UpdateProfileRequest_Profile_Customer_gender","UpdateProfileRequest_Profile_Customer");

						String query1=WSClient.getQuery("QS_01");
						db = WSClient.getDBRow(query1);

						actualValues = WSClient.getSingleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
						WSAssert.assertEquals(actualValues, db, false);
					}
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The text displayed in the response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites failed!------ Subscription-----Blocked");
				}
			}
			//}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20528() {
		try {
			String testName = "updateProfile_2006_20528";
			WSClient.startTest(testName,
					"Verify that the customer address information is correctly updated through the UpdateProfile call for an existing guest profile",
					"minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"AddressType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				String UniqueId = CreateProfile.createProfile("DS_04");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressID=WSClient.getDBRow(query1);
					if(addressID.size()>0){
						String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if (WSAssert.assertIfElementValueEquals(subscriptionRes,
								"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
							state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
							System.out.println("address :"+state_code+" "+state);
							country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
							String country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
							System.out.println("address_country :"+country_code+ " "+country);


							addLOV=OPERALib.fetchAddressLOV(state,country_code);

							WSClient.setData("{var_state}", state_code);
							WSClient.setData("{var_country}", country);
							WSClient.setData("{var_addressType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", OperaPropConfig.getDataSetForCode("AddressType", "DS_01")));


							WSClient.setData("{var_zip}", addLOV.get("Zip"));
							WSClient.setData("{var_city}", addLOV.get("City"));
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_02");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);


							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_CityName", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_StateProv","Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_CountryCode","Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_PostalCode","Profile_Addresses_NameAddress");
								xPath.put("Profile_Addresses_NameAddress_addressType", "Profile_Addresses_NameAddress");

								String query2=WSClient.getQuery("QS_02");
								db = WSClient.getDBRow(query2);

								actualValues = WSClient.getSingleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for a address attached to the profile failed!-----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_40792() {
		try {
			String testName = "updateProfile_2006_40792";
			WSClient.startTest(testName,
					"Verify that the customer address information is correctly added through the UpdateProfile call for an existing guest profile",
					"minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"AddressType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				OPERALib.setOperaHeader(uname);

				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));


				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				//Prerequisite 1: Create Profile
				String UniqueId = CreateProfile.createProfile("DS_04");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressID=WSClient.getDBRow(query1);
					if(addressID.size()>0){
						String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if (WSAssert.assertIfElementValueEquals(subscriptionRes,
								"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
							state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
							System.out.println("address :"+state_code+" "+state);
							country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
							String country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
							System.out.println("address_country :"+country_code+ " "+country);


							addLOV=OPERALib.fetchAddressLOV(state,country_code);

							WSClient.setData("{var_state}", state_code);
							WSClient.setData("{var_country}", country);
							WSClient.setData("{var_addressType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", OperaPropConfig.getDataSetForCode("AddressType", "DS_02")));

							WSClient.setData("{var_zip}", addLOV.get("Zip"));
							WSClient.setData("{var_city}", addLOV.get("City"));
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_02");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_CityName", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_StateProv","Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_CountryCode","Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_PostalCode","Profile_Addresses_NameAddress");
								xPath.put("Profile_Addresses_NameAddress_addressType", "Profile_Addresses_NameAddress");

								WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_02"));
								String query2=WSClient.getQuery("QS_16");
								db = WSClient.getDBRow(query2);

								actualValues = WSClient.getSingleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for a address attached to the profile failed!-----Blocked");
					}
				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20537() {
		try {
			String testName = "updateProfile_2006_20537";
			WSClient.startTest(testName,
					"Verify that comments are being updated through UpdateProfile call for an existing guest profile",
					"minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				String comment="This is first comment";
				String comment2="This is second comment";

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_comment}", comment);
				WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));


				String UniqueId = CreateProfile.createProfile("DS_25");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);
					String query1=WSClient.getQuery("QS_05");
					List<LinkedHashMap<String,String>> commentID= WSClient.getDBRows(query1);

					if(commentID.size()>=1){
						String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if (WSAssert.assertIfElementValueEquals(subscriptionRes,
								"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							WSClient.setData("{var_comment}", comment2);
							WSClient.setData("{var_commentType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", OperaPropConfig.getDataSetForCode("NoteType", "DS_01")));

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_03");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("Profile_Comments_Comment_commentType", "Profile_Comments_Comment");
								xPath.put("Comment_Text_TextElement", "Profile_Comments_Comment");

								String query2=WSClient.getQuery("QS_03");
								db = WSClient.getDBRow(query2);

								actualValues = WSClient.getSingleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
								WSAssert.assertEquals(actualValues,db, false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for a Comment to be attached to the profile failed!-----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Comment Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups= {"minimumRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20544() {
		try {
			String testName = "updateProfile_2006_20544";
			WSClient.startTest(testName, "Verify that the phone number is being added in CRM via UpdateProfile service call for"
					+ " the requested existing profile", "minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));


				String UniqueId = CreateProfile.createProfile("DS_21");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_04");
					HashMap<String,String> phn=WSClient.getDBRow(query1);
					if(phn.size()>0){
						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03")));
							WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

							String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_04");
							String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

							if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
								LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
								HashMap<String,String> xPath=new HashMap<String,String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("Phones_NamePhone_PhoneNumber","Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");

								WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
								String query2=WSClient.getQuery("QS_04");
								db=WSClient.getDBRow(query2);
								actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);
								WSAssert.assertEquals(actualValues,db,false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for a Phone Number to be attached to the profile failed!-----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"minimumRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20541() {
		try {
			String testName = "updateProfile_2006_20541";
			WSClient.startTest(testName, "Verify that the submitted phone information is correctly updated in CRM through the UpdateProfile "
					+ "call for the requested existing profile", "minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));



				String UniqueId = CreateProfile.createProfile("DS_21");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");


					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_04");
					HashMap<String,String> phn=WSClient.getDBRow(query1);
					if(phn.size()>0){
						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01")));
							WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
							WSClient.setData("{var_primary}","true");
							String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_05");
							String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

							if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
								LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
								HashMap<String,String> xPath=new HashMap<String,String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("NamePhone_PhoneData_PhoneNumber","Phones_NamePhone_PhoneData");
								xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

								String query2=WSClient.getQuery("QS_05");
								db=WSClient.getDBRow(query2);
								actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

								String areaCode=WSClient.getElementValue(updateProfileReq, "NamePhone_PhoneData_AreaCode", XMLType.REQUEST);
								String countryAccessCode=WSClient.getElementValue(updateProfileReq, "NamePhone_PhoneData_CountryAccessCode", XMLType.REQUEST);


								if(actualValues.containsKey("PhoneNumber1")){
									String phoneNo=actualValues.get("PhoneNumber1");
									actualValues.put("PhoneNumber1", countryAccessCode+areaCode+phoneNo);
								}
								if(actualValues.containsKey("primary1")){
									String primary=actualValues.get("primary1");
									if(primary.equalsIgnoreCase("TRUE"))
										actualValues.put("primary1", "Y");
									else
										actualValues.put("primary1", "N");
								}
								WSAssert.assertEquals(actualValues,db,false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for a Phone Number to be attached to the profile failed!-----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"minimumRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20547() {
		try {
			String testName = "updateProfile_2006_20547";
			WSClient.startTest(testName, "Verify that the submitted email address is correctly updated in CRM for the requested existing profile "
					+ "via UpdateProfile service call", "minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_email}", fname+"@ORACLE.COM");
				WSClient.setData("{var_primary}","true");
				WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));

				String UniqueId = CreateProfile.createProfile("DS_37");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_04");
					HashMap<String,String> phn=WSClient.getDBRow(query1);
					if(phn.size()>0){
						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							WSClient.setData("{var_email}", fname+"."+lname+"@ORACLE.COM");
							WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02")));
							WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
							WSClient.setData("{var_primary}","true");


							String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_06");
							String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

							if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
								LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
								HashMap<String,String> xPath=new HashMap<String,String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("Phones_NamePhone_PhoneNumber","Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

								String query2=WSClient.getQuery("QS_06");
								db=WSClient.getDBRow(query2);
								actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

								if(actualValues.containsKey("primary1")){
									String primary=actualValues.get("primary1");
									if(primary.equalsIgnoreCase("TRUE"))
										actualValues.put("primary1", "Y");
									else
										actualValues.put("primary1", "N");
								}
								WSAssert.assertEquals(actualValues,db,false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for a Email Address to be attached to the profile failed!-----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20560() {
		try {
			String testName = "updateProfile_2006_20560";
			WSClient.startTest(testName,
					"Verify that the address record which has the Default Address Type (Address Type Configured in the Default Parameters) is updated with"
							+ " the submitted information when the UpdateProfile call is submitted with no Address Type ",
					"minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"AddressType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);


				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

				String UniqueId = CreateProfile.createProfile("DS_04");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressID=WSClient.getDBRow(query1);
					if(addressID.size()>0){
						String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if (WSAssert.assertIfElementValueEquals(subscriptionRes,
								"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
							state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
							System.out.println("address :"+state_code+" "+state);
							country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
							String country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
							System.out.println("address_country :"+country_code+ " "+country);


							addLOV=OPERALib.fetchAddressLOV(state,country_code);

							WSClient.setData("{var_state}", state_code);
							WSClient.setData("{var_country}", country);


							WSClient.setData("{var_zip}", addLOV.get("Zip"));
							WSClient.setData("{var_city}", addLOV.get("City"));
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_10");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_CityName", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_StateProv","Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_CountryCode","Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_PostalCode","Profile_Addresses_NameAddress");


								String query2=WSClient.getQuery("QS_11");
								db = WSClient.getDBRow(query2);

								actualValues = WSClient.getSingleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for a address attached to the profile failed!-----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20561() {
		try {
			String testName = "updateProfile_2006_20561";
			WSClient.startTest(testName,
					"Verify that the Country Code associated to the existing address record is updated/overriden with the Default"
							+ " Country Code that is configured in the Interface Paraemeters through the UpdateProfile call",
					"minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"AddressType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);


				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

				String UniqueId = CreateProfile.createProfile("DS_04");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressID=WSClient.getDBRow(query1);
					if(addressID.size()>0){
						String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if (WSAssert.assertIfElementValueEquals(subscriptionRes,
								"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
							state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
							System.out.println("address :"+state_code+" "+state);
							country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
							String country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
							System.out.println("address_country :"+country_code+ " "+country);


							addLOV=OPERALib.fetchAddressLOV(state,country_code);

							WSClient.setData("{var_state}", state_code);


							WSClient.setData("{var_zip}", addLOV.get("Zip"));
							WSClient.setData("{var_city}", addLOV.get("City"));
							WSClient.setData("{var_addressType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES",OperaPropConfig.getDataSetForCode("AddressType", "DS_01")));
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_11");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_CityName", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_StateProv","Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_PostalCode","Profile_Addresses_NameAddress");
								xPath.put("Profile_Addresses_NameAddress_addressType", "Profile_Addresses_NameAddress");

								String query2=WSClient.getQuery("QS_12");
								db = WSClient.getDBRow(query2);

								actualValues = WSClient.getSingleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for a address attached to the profile failed!-----Blocked");
					}

				}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	@Test(groups= {"minimumRegression","UpdateProfile","HTNG2006","HTNG","updateProfile_2006_20577"})
	public void updateProfile_2006_20577() {
		try {
			String testName = "updateProfile_2006_20577";
			WSClient.startTest(testName, "Verify that the submitted customer information is correctly updated in CRM for the "
					+ "requested profile through UpdateProfile call", "minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"Title","VipLevel"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
				WSClient.setData("{var_businessTitle}", "Dr.");
				WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
				WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));
				WSClient.setData("{var_birthDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_8395}"));
				WSClient.setData("{var_nationality}", OperaPropConfig.getDataSetForCode("Nationality", "DS_01"));

				String UniqueId = CreateProfile.createProfile("DS_28");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){

						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
						WSClient.setData("{var_nameTitle}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "TITLE",OperaPropConfig.getDataSetForCode("Title", "DS_02")));
						WSClient.setData("{var_businessTitle}", "Prop.");
						WSClient.setData("{var_gender}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "GENDER_MF",OperaPropConfig.getDataSetForCode("Gender", "DS_02")));
						WSClient.setData("{var_vipCode}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "VIP_LEVEL",OperaPropConfig.getDataSetForCode("VipLevel", "DS_02")));
						WSClient.setData("{var_birthDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_8000}"));
						WSClient.setData("{var_nationality}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "NATIONALITY", OperaPropConfig.getDataSetForCode("Nationality", "DS_02")));

						WSClient.writeToReport(LogStatus.INFO, "<b> Updating Middlename,Lastname,BusinessTitle,BirthDate </b>");
						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_13");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
							xPath.put("Customer_PersonName_FirstName","Profile_Customer_PersonName");
							xPath.put("Customer_PersonName_MiddleName","Profile_Customer_PersonName");
							xPath.put("Customer_PersonName_LastName","Profile_Customer_PersonName");
							xPath.put("Profile_Customer_BusinessTitle","UpdateProfileRequest_Profile_Customer");
							xPath.put("UpdateProfileRequest_Profile_Customer_birthDate","UpdateProfileRequest_Profile_Customer");
							xPath.put("UpdateProfileRequest_Profile_languageCode", "UpdateProfileRequest_Profile");


							String query2=WSClient.getQuery("QS_14");
							db=WSClient.getDBRow(query2);
							actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);
							String birthDate=actualValues.get("birthDate1");
							if(actualValues.containsKey("birthDate1")){
								actualValues.put("birthDate1","XXXXXX"+birthDate.substring(birthDate.length() - 2));
							}
							WSAssert.assertEquals(actualValues,db,false);

						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating NameTitle</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_16");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							String exptitle=WSClient.getElementValue(updateProfileReq, "Customer_PersonName_NameTitle", XMLType.REQUEST);
							String actualtitle=WSClient.getDBRow(WSClient.getQuery("QS_19")).get("NameTitle1");

							WSAssert.assertEquals(exptitle,actualtitle,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating Gender</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_17");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							String exp=WSClient.getElementValue(updateProfileReq, "UpdateProfileRequest_Profile_Customer_gender", XMLType.REQUEST);
							String actual=WSClient.getDBRow(WSClient.getQuery("QS_18")).get("gender1");

							WSAssert.assertEquals(exp,actual,false);

						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating Nationality</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_18");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							String exp=WSClient.getElementValue(updateProfileReq, "UpdateProfileRequest_Profile_nationality", XMLType.REQUEST);
							String actual=WSClient.getDBRow(WSClient.getQuery("QS_20")).get("nationality1");

							WSAssert.assertEquals(exp,actual,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating VipCode</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_19");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							String exp=WSClient.getElementValue(updateProfileReq, "UpdateProfileRequest_Profile_vipCode", XMLType.REQUEST);
							String actual=WSClient.getDBRow(WSClient.getQuery("QS_21")).get("vipCode1");

							WSAssert.assertEquals(exp,actual,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}


					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Title and Viplevel are not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	//	@Test(groups= {"minimumRegression","UpdateProfile","HTNG2006","HTNG"})
	//	public void updateProfile_2006_20557() {
	//		try {
	//			String testName = "updateProfile_2006_20557";
	//			WSClient.startTest(testName, "Verify that the submitted Passport Number is correctly updated for"
	//					+ " the requested profile through UpdateProfile Call", "minimumRegression");
	//			if(OperaPropConfig.getPropertyConfigResults(new String[] {"IdentificationType"})){
	//			String interfaceName = HTNGLib.getHTNGInterface();
	//			String resortOperaValue = OPERALib.getResort();
	//			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String fromAddress=HTNGLib.getInterfaceFromAddress();
	//			String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
	//			String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
	//
	//			WSClient.setData("{var_extResort}",resortExtValue);
	//			WSClient.setData("{var_resort}",resortOperaValue);
	//			WSClient.setData("{var_lname}", lname);
	//			WSClient.setData("{var_fname}", fname);
	//			WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
	//			OPERALib.setOperaHeader(uname);
	//
	//
	//			String UniqueId = CreateProfile.createProfile("DS_06");
	//			if(!UniqueId.equals("error")){
	//			WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");
	//
	//
	//
	//	            	WSClient.setData("{var_profileID}", UniqueId);
	//	            	WSClient.setData("{var_profileId}", UniqueId);
	//	            	String documentNo=(WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")).toUpperCase()+WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
	//	            	WSClient.setData("{var_docnum}", documentNo);
	//	            	WSClient.setData("{var_docType}",OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01"));
	//	            	String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
	//					String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);
	//
	//					if (WSAssert.assertIfElementExists(changeProfileResponseXML, "ChangeProfileRS_Success", true)) {
	//						WSClient.writeToReport(LogStatus.INFO, "<b>Passport is attached to the Profile</b>");
	//	            	String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
	//	                WSClient.setData("{var_E_profileID}", extProfileID);
	//	                WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
	//	                WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());
	//
	//	                HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
	//	            	String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
	//					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
	//
	//					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
	//						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
	//						String documentNo2=(WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")).toUpperCase()+WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
	//						WSClient.setData("{var_docnum}", documentNo2);
	//						WSClient.setData("{var_docType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "DOCUMENT_TYPE", OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01")));
	//
	//						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_09");
	//		            	String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
	//
	//		            	if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
	//		            		LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
	//		                    LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
	//
	//		                    String query=WSClient.getQuery("QS_10");
	//		                    db=WSClient.getDBRow(query);
	//		                    LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();
	//		                    xpath.put("GovernmentIDList_GovernmentID_DocumentType","Customer_GovernmentIDList_GovernmentID");
	//		                    xpath.put("GovernmentIDList_GovernmentID_DocumentNumber","Customer_GovernmentIDList_GovernmentID");
	//
	//		                    actualValues=WSClient.getSingleNodeList(updateProfileReq, xpath, false, XMLType.REQUEST);
	//		                    String docNo=actualValues.get("DocumentNumber1");
	//							if(actualValues.containsKey("DocumentNumber1")){
	//								actualValues.put("DocumentNumber1","XXXXXX"+docNo.substring(docNo.length() - 2));
	//							}
	//		                	WSAssert.assertEquals(actualValues,db,false);
	//		            	}
	//		            	if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {
	//
	//		            				/****
	//		            				 * Verifying that the error message is populated on the
	//		            				 * response
	//		         					 ********/
	//
	//		            				String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
	//		            								XMLType.RESPONSE);
	//		            				WSClient.writeToReport(LogStatus.INFO,
	//		            						"The text displayed in the response is :" + message);
	//		            								}
	//					}else {
	//						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
	//					}
	//				}else{
	//					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Passport to be attached to the profile failed!------ChangeProfile-----Blocked");
	//				}
	//
	//			}
	//            }
	//		} catch (Exception e) {
	//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
	//		}
	//	}
	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20534(){
		try{
			String testName = "updateProfile_2006_20534";
			WSClient.startTest(testName, "Verify that the Membership Information is being updated correctly when the UpdateProfile"
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
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
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
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
					WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));



					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipResponseXML = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_Success", true)) {

						if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", true)){
							String memId=WSClient.getElementValue(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>MembershipId =" + memId+"</b>");
							String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
							WSClient.setData("{var_E_profileID}", extProfileID);
							WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
							WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
							String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

							if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
								String membType=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE", OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
								String membLevel=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

								WSClient.setData("{var_membershipLevel}", membLevel);
								WSClient.setData("{var_membershipType}", membType);
								WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
								//Validation request being created and processed to generate response
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_14");
								String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

								if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"UpdateProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

									HashMap<String, String> xPath = new HashMap<String, String>();

									//xPaths of the records being verified and their parents are being put in a hashmap
									xPath.put("Memberships_NameMembership_MembershipType","Profile_Memberships_NameMembership");
									xPath.put("Memberships_NameMembership_MembershipNumber","Profile_Memberships_NameMembership");
									xPath.put("Memberships_NameMembership_MembershipLevel","Profile_Memberships_NameMembership");
									xPath.put("Memberships_NameMembership_EffectiveDate", "Profile_Memberships_NameMembership");
									xPath.put("Memberships_NameMembership_ExpirationDate", "Profile_Memberships_NameMembership");
									xPath.put("Profile_Memberships_NameMembership_inactiveDate", "Profile_Memberships_NameMembership");

									//database records are being stored in a list of hashmaps
									String query1=WSClient.getQuery("QS_15");
									db=WSClient.getDBRow(query1);

									//response records are being stored in a list of hashmaps
									actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

									//database records and response records are being compared
									WSAssert.assertEquals( actualValues,db,false);
								}
								if(WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)){

									/****
									 * Verifying that the error message is populated on the
									 * response
									 ********/

									String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"The text displayed in the response is :" + message);
								}}else {
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for MembershipNo!---- Create Membership  Failed!- -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Create Membership  Failed!- -----Blocked");
					}



				}}else{
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Membership Type and Membership Level Failed!");
				}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_40793() {
		try {
			String testName = "updateProfile_2006_40793";
			WSClient.startTest(testName, "Verify that the UDFs is being updated correctly when the UpdateProfile call "
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
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
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
						WSClient.setData("{var_charLabel}", charUDF);
						WSClient.setData("{var_numLabel}", numUDF);
						WSClient.setData("{var_dateLabel}", dateUDF);
						// Validation request being created and processed to
						// generate response
						String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_15");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
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
								String query2 = WSClient.getQuery("QS_17");
								dbValues = WSClient.getDBRow(query2);
								LinkedHashMap<String, String> dbValues1 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> dbValues2 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> dbValues3 = new LinkedHashMap<String, String>();
								dbValues1.put("CharacterValue1", dbValues.get(charName));
								dbValues2.put("NumericValue1", dbValues.get(numName));
								dbValues3.put("DateValue1", dbValues.get(dateName));
								db.add(dbValues1);
								db.add(dbValues2);
								db.add(dbValues3);
								// response records are being stored in a list of
								// hashmaps
								actualValues = WSClient.getMultipleNodeList(updateProfileReq, xPath, false,
										XMLType.REQUEST);

								// database records and response records are being
								// compared
								WSAssert.assertEquals( db,actualValues, false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on
								 * the response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML,
										"Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
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
	@Test(groups = { "minimumRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_41086() {
		try {
			String testName = "updateProfile_2006_41086";
			WSClient.startTest(testName,
					"Verify that the response shows error if invalid nameType is passed in the updateProfile Request",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");

			WSClient.setData("{var_extResort}", resortExtValue);
			//WSClient.setData("{var_extResort}", resortOperaValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", firstName);
			WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			String UniqueId = CreateProfile.createProfile("DS_06");
			if(!UniqueId.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");

				String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

				WSClient.setData("{var_profileID}", UniqueId);
				WSClient.setData("{var_E_profileID}", extProfileID);
				WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
					String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
					WSClient.setData("{var_lname}", lastName);
					WSClient.setData("{var_uniqueId}", extProfileID);
					WSClient.setData("{var_nameType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_3}"));
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_20");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					WSClient.writeToReport(LogStatus.INFO, "<b>Response Validation</b>");
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"UpdateProfileResponse_Result_resultStatusFlag","FAIL", false);


					WSClient.writeToReport(LogStatus.INFO, "<b>DB Validation</b>");
					String query1=WSClient.getQuery("QS_22");
					String query2=WSClient.getQuery("QS_23");
					LinkedHashMap<String,String> db1=WSClient.getDBRow(query1);
					LinkedHashMap<String,String> db2=WSClient.getDBRow(query2);
					if(db1.size()>0){
						WSClient.writeToReport(LogStatus.PASS, "Profile is added to STAGE_PROFILES Table");
					}else{
						WSClient.writeToReport(LogStatus.FAIL, "Profile is not added to STAGE_PROFILES Table");
					}
					if(db2.size()>0){
						WSClient.writeToReport(LogStatus.PASS, "Profile is added to STAGE_PROFILES_ERRORS Table");
						WSClient.writeToReport(LogStatus.PASS, "ERROR_FIELD: "+db2.get("ERROR_FIELD"));
						WSClient.writeToReport(LogStatus.PASS, "ERROR_VALUE: "+db2.get("ERROR_VALUE"));
						WSClient.writeToReport(LogStatus.PASS, "ERROR_DESC: "+db2.get("ERROR_DESC"));
					}else{
						WSClient.writeToReport(LogStatus.FAIL, "Profile is added not to STAGE_PROFILES_ERRORS Table");
					}



					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The text displayed in the response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites failed!------ Subscription-----Blocked");
				}
			}
			//}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups = { "targetedRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20530() {
		try {
			String testName = "updateProfile_2006_20530";
			WSClient.startTest(testName,
					"Verify that the multiple customer address information is correctly added through the UpdateProfile call for an existing guest profile",
					"targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"AddressType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				OPERALib.setOperaHeader(uname);

				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));


				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				//Prerequisite 1: Create Profile
				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
						state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
						System.out.println("address :"+state_code+" "+state);
						country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
						String country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
						System.out.println("address_country :"+country_code+ " "+country);


						addLOV=OPERALib.fetchAddressLOV(state,country_code);

						WSClient.setData("{var_state}", state_code);
						WSClient.setData("{var_country}", country);
						WSClient.setData("{var_addressType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", OperaPropConfig.getDataSetForCode("AddressType", "DS_02")));

						WSClient.setData("{var_zip}", addLOV.get("Zip"));
						WSClient.setData("{var_city}", addLOV.get("City"));

						state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
						state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
						System.out.println("address :"+state_code+" "+state);
						country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
						country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
						System.out.println("address_country :"+country_code+ " "+country);


						addLOV=OPERALib.fetchAddressLOV(state,country_code);

						WSClient.setData("{var_state1}", state_code);
						WSClient.setData("{var_country1}", country);
						WSClient.setData("{var_addressType2}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", OperaPropConfig.getDataSetForCode("AddressType", "DS_01")));

						WSClient.setData("{var_zip1}", addLOV.get("Zip"));
						WSClient.setData("{var_city1}", addLOV.get("City"));


						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_21");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
							xPath.put("Profile_Addresses_NameAddress_primary", "Profile_Addresses_NameAddress");
							xPath.put("Profile_Addresses_NameAddress_addressType", "Profile_Addresses_NameAddress");

							String query2=WSClient.getQuery("QS_24");
							db = WSClient.getDBRows(query2);

							actualValues = WSClient.getMultipleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
							WSAssert.assertEquals( db,actualValues, false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups= {"minimumRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20532() {
		try {
			String testName = "updateProfile_2006_20532";
			WSClient.startTest(testName, "Verify that the guest preferences are correctly added through the UpdateProfile call "
					+ "for an existing guest profile", "minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_extResort}",resortExtValue);
				//WSClient.setData("{var_extResort}",resortOperaValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);


				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					WSClient.setData("{var_profileID}", UniqueId);

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						String prefValueExt=HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
						String prefTypeExt=HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
						WSClient.setData("{var_prefValue}", prefValueExt);
						WSClient.setData("{var_prefType}",prefTypeExt);
						WSClient.setData("{var_prefDesc}", prefTypeExt+" "+prefValueExt);

						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_07");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
							xPath.put("Profile_Preferences_Preference_preferenceType","Profile_Preferences_Preference");
							xPath.put("Profile_Preferences_Preference_preferenceValue", "Profile_Preferences_Preference");

							WSClient.setData("{var_prefType}", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
							String query3=WSClient.getQuery("QS_08");
							db=WSClient.getDBRow(query3);
							actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

							WSAssert.assertEquals(actualValues,db,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Preference Type and Preference Group not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups = { "targetedRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20535(){
		try{
			String testName = "updateProfile_2006_20535";
			WSClient.startTest(testName, "Verify that the Membership Information is being added correctly when the UpdateProfile"
					+ " call is issued with a valid Profile Identifier", "targetedRegression");
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
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_Resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				//WSClient.setData("{var_extResort}", resortOperaValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_lname}", lName);
				WSClient.setData("{var_fname}", fName);
				OPERALib.setOperaHeader(OPERALib.getUserName());


				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
					WSClient.setData("{var_profileID}", operaProfileID);

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");



					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						String membType=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE", OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
						String membLevel=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

						WSClient.setData("{var_membershipLevel}", membLevel);
						WSClient.setData("{var_membershipType}", membType);
						WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
						//Validation request being created and processed to generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_14");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"UpdateProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

							HashMap<String, String> xPath = new HashMap<String, String>();

							//xPaths of the records being verified and their parents are being put in a hashmap
							xPath.put("Memberships_NameMembership_MembershipType","Profile_Memberships_NameMembership");
							xPath.put("Memberships_NameMembership_MembershipNumber","Profile_Memberships_NameMembership");
							xPath.put("Memberships_NameMembership_MembershipLevel","Profile_Memberships_NameMembership");
							xPath.put("Memberships_NameMembership_EffectiveDate", "Profile_Memberships_NameMembership");
							xPath.put("Memberships_NameMembership_ExpirationDate", "Profile_Memberships_NameMembership");
							xPath.put("Profile_Memberships_NameMembership_inactiveDate", "Profile_Memberships_NameMembership");

							//database records are being stored in a list of hashmaps
							String query1=WSClient.getQuery("QS_15");
							db=WSClient.getDBRow(query1);

							//response records are being stored in a list of hashmaps
							actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

							//database records and response records are being compared
							WSAssert.assertEquals(  actualValues,db,false);
						}
						if(WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)){

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
						}
				}
				// }
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Membership Type and Membership Level Failed!");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "targetedRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20536(){
		try{
			String testName = "updateProfile_2006_20536";
			WSClient.startTest(testName, "Verify that multiple Memberships Information are being added correctly when the UpdateProfile"
					+ " call is issued with a valid Profile Identifier", "targetedRegression");
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
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_Resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				//WSClient.setData("{var_extResort}", resortOperaValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_lname}", lName);
				WSClient.setData("{var_fname}", fName);
				OPERALib.setOperaHeader(OPERALib.getUserName());


				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
					WSClient.setData("{var_profileID}", operaProfileID);

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");



					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						String membType=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE", OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
						String membLevel=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));
						String membType2=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_TYPE", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						String membLevel2=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						WSClient.setData("{var_membershipLevel}", membLevel);
						WSClient.setData("{var_membershipType}", membType);
						WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
						WSClient.setData("{var_membershipLevel2}", membLevel2);
						WSClient.setData("{var_membershipType2}", membType2);
						WSClient.setData("{var_memNo2}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
						//Validation request being created and processed to generate response
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_22");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"UpdateProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

							HashMap<String, String> xPath = new HashMap<String, String>();

							//xPaths of the records being verified and their parents are being put in a hashmap
							xPath.put("Memberships_NameMembership_MembershipType","Profile_Memberships_NameMembership");
							xPath.put("Memberships_NameMembership_MembershipNumber","Profile_Memberships_NameMembership");
							xPath.put("Memberships_NameMembership_MembershipLevel","Profile_Memberships_NameMembership");

							//database records are being stored in a list of hashmaps
							String query1=WSClient.getQuery("QS_25");
							db=WSClient.getDBRows(query1);

							//response records are being stored in a list of hashmaps
							actualValues=WSClient.getMultipleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

							//database records and response records are being compared
							WSAssert.assertEquals( db, actualValues,false);
						}
						if(WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)){

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
						}
				}
				// }
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Membership Type and Membership Level Failed!");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "targetedRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20539() {
		try {
			String testName = "updateProfile_2006_20539";
			WSClient.startTest(testName,
					"Verify that multiple comments are being added through UpdateProfile call for an existing guest profile",
					"targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				String comment="This is first comment";
				String comment2="This is second comment";
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);


				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						WSClient.setData("{var_comment}", comment);
						WSClient.setData("{var_commentType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", OperaPropConfig.getDataSetForCode("NoteType", "DS_01")));
						WSClient.setData("{var_comment2}", comment2);
						WSClient.setData("{var_commentType2}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", OperaPropConfig.getDataSetForCode("NoteType", "DS_02")));

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_23");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							xPath.put("Profile_Comments_Comment_commentType", "Profile_Comments_Comment");
							xPath.put("Comment_Text_TextElement", "Profile_Comments_Comment");

							String query2=WSClient.getQuery("QS_26");
							db = WSClient.getDBRows(query2);

							actualValues = WSClient.getMultipleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);
							WSAssert.assertEquals(db,actualValues, false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Comment Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups = { "targetedRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20540() {
		try {
			String testName = "updateProfile_2006_20540";
			WSClient.startTest(testName,
					"Verify that an additional(New) comment text line is being appended to the existing comment line through UpdateProfile call for an existing guest profile",
					"targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				String comment="This is first comment.";
				String comment2="This is second comment";
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);


				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);
					String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");

						WSClient.setData("{var_comment}", comment);
						WSClient.setData("{var_comment2}", comment2);
						WSClient.setData("{var_commentType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COMMENT_TYPE", OperaPropConfig.getDataSetForCode("NoteType", "DS_01")));

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_24");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xPath = new HashMap<String, String>();
							xPath.put("Profile_Comments_Comment_commentType", "Profile_Comments_Comment");
							xPath.put("Comment_Text_TextElement", "Profile_Comments_Comment");

							String query2=WSClient.getQuery("QS_26");
							db = WSClient.getDBRow(query2);

							actualValues = WSClient.getSingleNodeList(updateProfileReq, xPath, false,XMLType.REQUEST);

							if(actualValues.containsKey("TextTextElement2")){
								if(actualValues.containsKey("TextTextElement1")){
									actualValues.put("TextTextElement1", actualValues.get("TextTextElement1")+ actualValues.get("TextTextElement2"));
									actualValues.remove("TextTextElement2");
								}
							}
							WSAssert.assertEquals(actualValues,db, false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Comment Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups= {"targetedRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20542() {
		try {
			String testName = "updateProfile_2006_20542";
			WSClient.startTest(testName, "Verify that the submitted phonedata information is correctly added in CRM through the UpdateProfile "
					+ "call for the requested existing profile", "targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01")));
						WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
						WSClient.setData("{var_primary}","true");
						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_05");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
							xPath.put("NamePhone_PhoneData_PhoneNumber","Phones_NamePhone_PhoneData");
							xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

							String query2=WSClient.getQuery("QS_05");
							db=WSClient.getDBRow(query2);
							actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

							String areaCode=WSClient.getElementValue(updateProfileReq, "NamePhone_PhoneData_AreaCode", XMLType.REQUEST);
							String countryAccessCode=WSClient.getElementValue(updateProfileReq, "NamePhone_PhoneData_CountryAccessCode", XMLType.REQUEST);


							if(actualValues.containsKey("PhoneNumber1")){
								String phoneNo=actualValues.get("PhoneNumber1");
								actualValues.put("PhoneNumber1", countryAccessCode+areaCode+phoneNo);
							}
							if(actualValues.containsKey("primary1")){
								String primary=actualValues.get("primary1");
								if(primary.equalsIgnoreCase("TRUE"))
									actualValues.put("primary1", "Y");
								else
									actualValues.put("primary1", "N");
							}
							WSAssert.assertEquals(actualValues,db,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"targetedRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20543() {
		try {
			String testName = "updateProfile_2006_20543";
			WSClient.startTest(testName, "Verify that the submitted multiple phonedata information is correctly added in CRM through the UpdateProfile "
					+ "call for the requested existing profile", "targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01")));
						WSClient.setData("{var_phoneType2}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03")));
						WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
						WSClient.setData("{var_primary}","true");
						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_25");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
							List<LinkedHashMap<String,String>> actualValues=new ArrayList<LinkedHashMap<String,String>>();
							HashMap<String,String> xPath=new HashMap<String,String>();

							xPath.put("NamePhone_PhoneData_PhoneNumber","Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

							String query2=WSClient.getQuery("QS_27");
							db=WSClient.getDBRows(query2);
							actualValues=WSClient.getMultipleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

							String areaCode=WSClient.getElementValue(updateProfileReq, "NamePhone_PhoneData_AreaCode", XMLType.REQUEST);
							String countryAccessCode=WSClient.getElementValue(updateProfileReq, "NamePhone_PhoneData_CountryAccessCode", XMLType.REQUEST);
							String areaCode2=WSClient.getElementValue(updateProfileReq, "NamePhone[2]_PhoneData_AreaCode", XMLType.REQUEST);
							String countryAccessCode2=WSClient.getElementValue(updateProfileReq, "NamePhone[2]_PhoneData_CountryAccessCode", XMLType.REQUEST);


							if(actualValues.get(0).containsKey("PhoneDataPhoneNumber1")){
								String phoneNo=actualValues.get(0).get("PhoneDataPhoneNumber1");
								actualValues.get(0).put("PhoneDataPhoneNumber1", countryAccessCode+areaCode+phoneNo);
							}
							if(actualValues.get(0).containsKey("primary1")){
								String primary=actualValues.get(0).get("primary1");
								if(primary.equalsIgnoreCase("TRUE"))
									actualValues.get(0).put("primary1", "Y");
								else
									actualValues.get(0).put("primary1", "N");
							}
							if(actualValues.get(1).containsKey("PhoneDataPhoneNumber1")){
								String phoneNo=actualValues.get(1).get("PhoneDataPhoneNumber1");
								actualValues.get(1).put("PhoneDataPhoneNumber1", countryAccessCode2+areaCode2+phoneNo);
							}
							if(actualValues.get(1).containsKey("primary1")){
								String primary=actualValues.get(1).get("primary1");
								if(primary.equalsIgnoreCase("TRUE"))
									actualValues.get(1).put("primary1", "Y");
								else
									actualValues.get(1).put("primary1", "N");
							}

							WSAssert.assertEquals(db,actualValues,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"targetedRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20548() {
		try {
			String testName = "updateProfile_2006_20548";
			WSClient.startTest(testName, "Verify that the submitted email address is correctly added in CRM for the requested existing profile "
					+ "via UpdateProfile service call", "targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}",resortExtValue);
				//WSClient.setData("{var_extResort}",resortOperaValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						WSClient.setData("{var_email}", fname+"."+lname+"@ORACLE.COM");
						WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02")));
						WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
						WSClient.setData("{var_primary}","true");


						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_06");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
							xPath.put("Phones_NamePhone_PhoneNumber","Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

							String query2=WSClient.getQuery("QS_06");
							db=WSClient.getDBRow(query2);
							actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

							if(actualValues.containsKey("primary1")){
								String primary=actualValues.get("primary1");
								if(primary.equalsIgnoreCase("TRUE"))
									actualValues.put("primary1", "Y");
								else
									actualValues.put("primary1", "N");
							}
							WSAssert.assertEquals(actualValues,db,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"targetedRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20550() {
		try {
			String testName = "updateProfile_2006_20550";
			WSClient.startTest(testName, "Verify that the submitted email address is correctly added under phonedata in CRM for the requested existing profile "
					+ "via UpdateProfile service call", "targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}",resortExtValue);
				//WSClient.setData("{var_extResort}",resortOperaValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						WSClient.setData("{var_email}", fname+"."+lname+"@ORACLE.COM");
						WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02")));
						WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
						WSClient.setData("{var_primary}","true");


						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_26");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
							xPath.put("NamePhone_PhoneData_PhoneNumber","Phones_NamePhone_PhoneData");
							xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
							xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

							String query2=WSClient.getQuery("QS_06");
							db=WSClient.getDBRow(query2);
							actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

							if(actualValues.containsKey("primary1")){
								String primary=actualValues.get("primary1");
								if(primary.equalsIgnoreCase("TRUE"))
									actualValues.put("primary1", "Y");
								else
									actualValues.put("primary1", "N");
							}
							WSAssert.assertEquals(actualValues,db,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"targetedRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20549() {
		try {
			String testName = "updateProfile_2006_20549";
			WSClient.startTest(testName, "Verify that the submitted email address is correctly updated under phonedata in CRM for the requested existing profile "
					+ "via UpdateProfile service call", "targetedRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}",resortExtValue);
				//WSClient.setData("{var_extResort}",resortOperaValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_email}", fname+"@ORACLE.COM");
				WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
				WSClient.setData("{var_primary}","true");
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				String UniqueId = CreateProfile.createProfile("DS_37");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_04");
					HashMap<String,String> phn=WSClient.getDBRow(query1);
					if(phn.size()>0){
						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							WSClient.setData("{var_email}", fname+"."+lname+"@ORACLE.COM");
							WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02")));
							WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
							WSClient.setData("{var_primary}","true");


							String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_26");
							String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

							if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
								LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
								HashMap<String,String> xPath=new HashMap<String,String>();
								xPath.put("Profile_IDs_UniqueID", "UpdateProfileRequest_Profile_IDs");
								xPath.put("NamePhone_PhoneData_PhoneNumber","Phones_NamePhone_PhoneData");
								xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

								String query2=WSClient.getQuery("QS_06");
								db=WSClient.getDBRow(query2);
								actualValues=WSClient.getSingleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);

								if(actualValues.containsKey("primary1")){
									String primary=actualValues.get("primary1");
									if(primary.equalsIgnoreCase("TRUE"))
										actualValues.put("primary1", "Y");
									else
										actualValues.put("primary1", "N");
								}
								WSAssert.assertEquals(actualValues,db,false);
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for a Email Address to be attached to the profile failed!-----Blocked");
					}
				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups = { "targetedRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20554() {
		try {
			String testName = "updateProfile_2006_20554";
			WSClient.startTest(testName, "Verify that the UDFs is being added correctly when the UpdateProfile call "
					+ "is issued with a valid Profile Identifier", "targetedRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"UDFLabel_P"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				//WSClient.setData("{var_extResort}", resortOperaValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
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


					WSClient.setData("{var_charLabel}", charUDF);
					WSClient.setData("{var_numLabel}", numUDF);
					WSClient.setData("{var_dateLabel}", dateUDF);
					// Validation request being created and processed to
					// generate response
					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_15");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"UpdateProfileResponse_Result_resultStatusFlag", "SUCCESS", false)) {
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
							String query2 = WSClient.getQuery("QS_17");
							dbValues = WSClient.getDBRow(query2);
							if(dbValues.size()>0){
								LinkedHashMap<String, String> dbValues1 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> dbValues2 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> dbValues3 = new LinkedHashMap<String, String>();
								dbValues1.put("CharacterValue1", dbValues.get(charName));
								dbValues2.put("NumericValue1", dbValues.get(numName));
								dbValues3.put("DateValue1", dbValues.get(dateName));
								db.add(dbValues1);
								db.add(dbValues2);
								db.add(dbValues3);}
							// response records are being stored in a list of
							// hashmaps
							actualValues = WSClient.getMultipleNodeList(updateProfileReq, xPath, false,
									XMLType.REQUEST);

							// database records and response records are being
							// compared
							WSAssert.assertEquals( db,actualValues, false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
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
	@Test(groups= {"minimumRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20558() {
		try {
			String testName = "updateProfile_2006_20558";
			WSClient.startTest(testName, "Verify that the submitted Passport Number is correctly added for"
					+ " the requested profile through UpdateProfile Call", "minimumRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"IdentificationType"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");



					WSClient.setData("{var_profileID}", UniqueId);

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
						String documentNo2=(WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")).toUpperCase()+WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
						WSClient.setData("{var_docnum}", documentNo2);
						WSClient.setData("{var_docType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "DOCUMENT_TYPE", OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01")));

						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_09");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

						if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","SUCCESS",false)) {
							LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

							String query=WSClient.getQuery("QS_10");
							db=WSClient.getDBRow(query);
							LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();
							xpath.put("GovernmentIDList_GovernmentID_DocumentType","Customer_GovernmentIDList_GovernmentID");
							xpath.put("GovernmentIDList_GovernmentID_DocumentNumber","Customer_GovernmentIDList_GovernmentID");

							actualValues=WSClient.getSingleNodeList(updateProfileReq, xpath, false, XMLType.REQUEST);
							String docNo=actualValues.get("DocumentNumber1");
							if(actualValues.containsKey("DocumentNumber1")){
								actualValues.put("DocumentNumber1","XXXXXX"+docNo.substring(docNo.length() - 2));
							}
							WSAssert.assertEquals(actualValues,db,false);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}


				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"fullRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20571() {
		try {
			String testName = "updateProfile_2006_20571";
			WSClient.startTest(testName, "Verify the error message on the response if required phoneInfo is missed in the request", "fullRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"CommunicationType","CommunicationMethod"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}",resortExtValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));

				String UniqueId = CreateProfile.createProfile("DS_21");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_04");
					HashMap<String,String> phn=WSClient.getDBRow(query1);
					if(phn.size()>0){
						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							WSClient.setData("{var_phoneType}",HTNGLib.getExtValue(resortOperaValue, interfaceName, "PHONE_TYPE",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01")));
							WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
							WSClient.setData("{var_primary}","true");
							String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_27");
							String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);

							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for a Phone Number to be attached to the profile failed!-----Blocked");
					}
				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Communication Type and Communication Method not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups= {"fullRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20572() {
		try {
			String testName = "updateProfile_2006_20572";
			WSClient.startTest(testName, "Verify the error message in the response if required preference type is missed in the request", "fullRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));

				WSClient.setData("{var_extResort}",resortExtValue);
				//WSClient.setData("{var_extResort}",resortOperaValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);


				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					WSClient.setData("{var_profileID}", UniqueId);

					String prefValue = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");

					String prefType = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");

					WSClient.setData("{var_prefType}", prefType);
					WSClient.setData("{var_prefValue}", prefValue);
					String query1=WSClient.getQuery("HTNG2006UpdateProfile", "QS_07");
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
							String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

							WSClient.setData("{var_E_profileID}", extProfileID);
							WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
							WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
							String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

							if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
								String prefValueExt=HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_CODE",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
								String prefTypeExt=HTNGLib.getExtValue(resortOperaValue, interfaceName, "GUEST_PREFERENCE_TYPE", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
								WSClient.setData("{var_prefValue}", prefValueExt);
								WSClient.setData("{var_prefType}",prefTypeExt);
								WSClient.setData("{var_prefDesc}", prefTypeExt+" "+prefValueExt);

								String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_28");
								String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);
								if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

									/****
									 * Verifying that the error message is populated on the
									 * response
									 ********/

									String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for a Preference Type and Preference Value to be attached to the profile failed!-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePrereference-----Blocked" );
					}
				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Preference Type and Preference Group not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups = { "fullRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20566(){
		try{
			String testName = "updateProfile_2006_20566";
			WSClient.startTest(testName, "Verify that the Membership Information is not updated when invalid membership conversion codes are passed"
					+ "in the request", "fullRegression");
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
				//WSClient.setData("{var_extResort}", resortOperaValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_lname}", lName);
				WSClient.setData("{var_fname}", fName);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(OPERALib.getUserName());



				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
					WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));



					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipResponseXML = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_Success", true)) {

						if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", true)){
							String memId=WSClient.getElementValue(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>MembershipId =" + memId+"</b>");
							String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");



							WSClient.setData("{var_E_profileID}", extProfileID);
							WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
							WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
							String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

							if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
								String membType="COOKING";
								String membLevel="FIRST";

								WSClient.setData("{var_membershipLevel}", membLevel);
								WSClient.setData("{var_membershipType}", membType);
								WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
								//Validation request being created and processed to generate response
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_14");
								String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"UpdateProfileResponse_Result_resultStatusFlag", "FAIL", false);
								if(WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)){

									/****
									 * Verifying that the error message is populated on the
									 * response
									 ********/

									String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}}else {
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for MembershipNo!---- Create Membership  Failed!- -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Create Membership  Failed!- -----Blocked");
					}


				}
				// }
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Membership Type and Membership Level Failed!");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "fullRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20568() {
		try {
			String testName = "updateProfile_2006_20568";
			WSClient.startTest(testName, "Verify that the UDFs are not being updated when incorrect udf"
					+ "values are passed in the request", "fullRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"UDFLabel_P"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				//WSClient.setData("{var_extResort}", resortOperaValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);


				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
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
						WSClient.setData("{var_charLabel}", charUDF);
						WSClient.setData("{var_numLabel}", numUDF);
						WSClient.setData("{var_dateLabel}", dateUDF);
						// Validation request being created and processed to
						// generate response
						String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");
						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_29");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag", "FAIL", false);


							if(WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)){

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
								xPath.put("Result_Text_TextElement", "UpdateProfileResponse_Result_Text");
								textList=WSClient.getSingleNodeList(updateProfileResponseXML, xPath, false, XMLType.RESPONSE);
								for(int i=1; i<=textList.size(); i++)
								{
									String message = textList.get("TextElement"+i);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :"+"</b>" + message);
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
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
	@Test(groups = { "fullRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20569() {
		try {
			String testName = "updateProfile_2006_20569";
			WSClient.startTest(testName,
					"verify the error in the response if profileId and resort is not passed in the request",
					"fullRegression");

			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");

			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", firstName);
			WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
			WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);
			String UniqueId = CreateProfile.createProfile("DS_24");
			if(!UniqueId.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");

				String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

				WSClient.setData("{var_profileID}", UniqueId);
				WSClient.setData("{var_E_profileID}", extProfileID);
				WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
					String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
					WSClient.setData("{var_lname}", lastName);
					WSClient.writeToReport(LogStatus.INFO, "<b>Resort is not passed in the request</b>");
					String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_30");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"UpdateProfileResponse_Result_resultStatusFlag","FAIL", false);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message+"</b>");
					}
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId is not passed in the request</b>");
					updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_31");
					updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"UpdateProfileResponse_Result_resultStatusFlag","FAIL", false);

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message+"</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites failed!------ Subscription-----Blocked");
				}
			}
			//}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups = { "fullRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20570() {
		try {
			String testName = "updateProfile_2006_20570";
			WSClient.startTest(testName,
					"Verify the error message in the response when address line is not passed while updating AddressInfo",
					"fullRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"AddressType"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				OPERALib.setOperaHeader(uname);

				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));


				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				//Prerequisite 1: Create Profile
				String UniqueId = CreateProfile.createProfile("DS_04");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressID=WSClient.getDBRow(query1);
					if(addressID.size()>0){
						String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if (WSAssert.assertIfElementValueEquals(subscriptionRes,
								"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
							state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
							System.out.println("address :"+state_code+" "+state);
							country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
							String country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
							System.out.println("address_country :"+country_code+ " "+country);


							addLOV=OPERALib.fetchAddressLOV(state,country_code);

							WSClient.setData("{var_state}", state_code);
							WSClient.setData("{var_country}", country);
							WSClient.setData("{var_addressType}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "ADDRESS_TYPES", OperaPropConfig.getDataSetForCode("AddressType", "DS_01")));

							WSClient.setData("{var_zip}", addLOV.get("Zip"));
							WSClient.setData("{var_city}", addLOV.get("City"));
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_32");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag","FAIL", false);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for a address attached to the profile failed!-----Blocked");
					}
				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups = { "fullRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20564() {
		try {
			String testName = "updateProfile_2006_20564";
			WSClient.startTest(testName,
					"Verify that the customer address information and phone information is not updated when we pass incorrect conversion codes for phonetype and addresstype",
					"fullRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"AddressType","CommunicationType","CommunicationMethod"})){
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				OPERALib.setOperaHeader(uname);

				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();

				addLOV=OPERALib.fetchAddressLOV(state,country_code);

				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));

				WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

				//Prerequisite 1: Create Profile
				String UniqueId = CreateProfile.createProfile("DS_03");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");
					WSClient.setData("{var_profileID}", UniqueId);

					String query1=WSClient.getQuery("QS_03");
					HashMap<String,String> addressID=WSClient.getDBRow(query1);
					if(addressID.size()>0){
						String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");


						WSClient.setData("{var_E_profileID}", extProfileID);
						WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
						WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
						String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

						if (WSAssert.assertIfElementValueEquals(subscriptionRes,
								"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
							state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
							state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
							System.out.println("address :"+state_code+" "+state);
							country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
							String country = HTNGLib.getExtValue(resortOperaValue,interfaceName,"COUNTRY_CODE",country_code);
							System.out.println("address_country :"+country_code+ " "+country);


							addLOV=OPERALib.fetchAddressLOV(state,country_code);

							WSClient.setData("{var_state}", state_code);
							WSClient.setData("{var_country}", country);
							WSClient.setData("{var_addressType}", "HHXHOME");

							WSClient.setData("{var_zip}", addLOV.get("Zip"));
							WSClient.setData("{var_city}", addLOV.get("City"));
							WSClient.setData("{var_phoneType}","HHXHOME");
							WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_33");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"UpdateProfileResponse_Result_resultStatusFlag","FAIL", false);

							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites failed!------ Subscription-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for a address attached to the profile failed!-----Blocked");
					}
				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Address Type,Communication Type and Communication Method are not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups= {"fullRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20565() {
		try {
			String testName = "updateProfile_2006_20565";
			WSClient.startTest(testName, "Verify that the guest preferences are not updated when incorrect preference conversion codes are passed in the request", "fullRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));

				WSClient.setData("{var_extResort}",resortExtValue);
				//WSClient.setData("{var_extResort}",resortOperaValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);


				String UniqueId = CreateProfile.createProfile("DS_06");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					WSClient.setData("{var_profileID}", UniqueId);

					String prefValue = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");

					String prefType = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");

					WSClient.setData("{var_prefType}", prefType);
					WSClient.setData("{var_prefValue}", prefValue);
					String query1=WSClient.getQuery("HTNG2006UpdateProfile", "QS_07");
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
							String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

							WSClient.setData("{var_E_profileID}", extProfileID);
							WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
							WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
							String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

							if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
								String prefValueExt="HOXP";
								String prefTypeExt="HOVAL";
								WSClient.setData("{var_prefValue}", prefValueExt);
								WSClient.setData("{var_prefType}",prefTypeExt);
								WSClient.setData("{var_prefDesc}", prefTypeExt+" "+prefValueExt);

								String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_07");
								String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);

								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);
								if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

									/****
									 * Verifying that the error message is populated on the
									 * response
									 ********/

									String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for a Preference Type and Preference Value to be attached to the profile failed!-----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePrereference-----Blocked" );
					}
				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Preference Type and Preference Group not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
	@Test(groups = { "fullRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20573(){
		try{
			String testName = "updateProfile_2006_20573";
			WSClient.startTest(testName, "Verify the error message in the response when membership type and membership no. is not passed in the request", "fullRegression");
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
				//WSClient.setData("{var_extResort}", resortOperaValue);
				WSClient.setData("{var_interfaceName}", interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_lname}", lName);
				WSClient.setData("{var_fname}", fName);
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				OPERALib.setOperaHeader(OPERALib.getUserName());



				String operaProfileID = CreateProfile.createProfile("DS_06");
				if(!operaProfileID.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+operaProfileID+"</b>");
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
					WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));



					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipResponseXML = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_Success", true)) {

						if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", true)){
							String memId=WSClient.getElementValue(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>MembershipId =" + memId+"</b>");
							String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");



							WSClient.setData("{var_E_profileID}", extProfileID);
							WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
							WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
							String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

							if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");

								String membLevel=HTNGLib.getExtValue(resortOperaValue, interfaceName, "MEMBERSHIP_LEVEL", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

								WSClient.setData("{var_membershipLevel}", membLevel);

								//Validation request being created and processed to generate response
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_34");
								String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"UpdateProfileResponse_Result_resultStatusFlag", "FAIL", false);


								if(WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)){

									/****
									 * Verifying that the error message is populated on the
									 * response
									 ********/

									LinkedHashMap<String, String> textList = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
									xPath.put("Result_Text_TextElement", "UpdateProfileResponse_Result_Text");
									textList=WSClient.getSingleNodeList(updateProfileResponseXML, xPath, false, XMLType.RESPONSE);
									for(int i=1; i<=textList.size(); i++)
									{
										String message = textList.get("TextElement"+i);
										WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :"+"</b>" + message);
									}
								}}else {
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for MembershipNo!---- Create Membership  Failed!- -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Create Membership  Failed!- -----Blocked");
					}


				}
				// }
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Membership Type and Membership Level Failed!");
			}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}
	@Test(groups = { "fullRegression", "UpdateProfile", "HTNG2006", "HTNG" })
	public void updateProfile_2006_20574() {
		try {
			String testName = "updateProfile_2006_20574";
			WSClient.startTest(testName,
					"verify the error message in the response when invalid resort is passed in the request",
					"fullRegression");

			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");

			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", firstName);
			WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
			WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);
			String UniqueId = CreateProfile.createProfile("DS_24");
			if(!UniqueId.equals("error")){
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId =" + UniqueId+"</b>");

				String extProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

				WSClient.setData("{var_profileID}", UniqueId);
				WSClient.setData("{var_E_profileID}", extProfileID);
				WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_01");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");
					String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
					WSClient.setData("{var_lname}", lastName);
					WSClient.setData("{var_extResort}", WSClient.getKeywordData("{KEYWORD_RANDSTR_6}"));
					WSClient.setData("{var_uniqueId}", extProfileID);
					WSClient.setData("{var_gender}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "GENDER_MF",OperaPropConfig.getDataSetForCode("Gender", "DS_02")));
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String updateProfileReq = WSClient.createSOAPMessage("HTNG2006UpdateProfile", "DS_01");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"UpdateProfileResponse_Result_resultStatusFlag","FAIL", false);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message+"</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites failed!------ Subscription-----Blocked");
				}
			}
			//}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	@Test(groups= {"fullRegression","UpdateProfile","HTNG2006","HTNG"})
	public void updateProfile_2006_20563() {
		try {
			String testName = "updateProfile_2006_20563";
			WSClient.startTest(testName, "Verify that the submitted customer information is not updated when incorrect conversion codes are passed in the requeat", "fullRegression");


			if(OperaPropConfig.getPropertyConfigResults(new String[] {"Title","VipLevel","Nationality","IdentificationType"})){
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extdb}", HTNGLib.getExternalDatabase(OPERALib.getResort(), interfaceName));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_extResort}",resortExtValue);
				//WSClient.setData("{var_extResort}",resortOperaValue);
				WSClient.setData("{var_resort}",resortOperaValue);
				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
				WSClient.setData("{var_businessTitle}", "Dr.");
				WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
				WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));
				WSClient.setData("{var_birthDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_8395}"));
				WSClient.setData("{var_nationality}", OperaPropConfig.getDataSetForCode("Nationality", "DS_01"));

				String UniqueId = CreateProfile.createProfile("DS_28");
				if(!UniqueId.equals("error")){
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileId ="+UniqueId+"</b>");

					String extProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_5}");

					WSClient.setData("{var_profileID}", UniqueId);

					WSClient.setData("{var_E_profileID}", extProfileID);
					WSClient.setData("{var_profileSource}", HTNGLib.getHTNGInterface());
					WSClient.setData("{var_interfaceName}", HTNGLib.getHTNGInterface());

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription" ,"DS_01" );
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if(WSAssert.assertIfElementValueEquals(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>ExtProfileId =" + extProfileID+"</b>");

						WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
						WSClient.setData("{var_nameTitle}", "XXMR");
						WSClient.setData("{var_gender}", "XXM");
						WSClient.setData("{var_vipCode}", "XXCELEB");
						WSClient.setData("{var_birthDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_8000}"));
						WSClient.setData("{var_nationality}", "XXIND");
						String documentNo2=(WSClient.getKeywordData("{KEYWORD_RANDSTR_4}")).toUpperCase()+WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
						WSClient.setData("{var_docnum}", documentNo2);
						WSClient.setData("{var_docType}", "XXPSPT");

						WSClient.writeToReport(LogStatus.INFO, "<b>Updating incorrect NameTitle</b>");
						String updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_16");
						String updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating incorrect Gender</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_17");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating incorrect Nationality</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_18");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating incorrect VipCode</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_19");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}
						WSClient.writeToReport(LogStatus.INFO, "<b>Updating incorrect DocumentType</b>");
						updateProfileReq = WSClient.createSOAPMessage( "HTNG2006UpdateProfile", "DS_09");
						updateProfileResponseXML=WSClient.processSOAPMessage(updateProfileReq);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,"UpdateProfileResponse_Result_resultStatusFlag","FAIL",false);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Subscription-----Blocked");
					}

				}
				//}
			}else{
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites Title,Nationality,IdentificationType and Viplevel are not available!-----Blocked" );
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to"+e);
		}
	}
}
