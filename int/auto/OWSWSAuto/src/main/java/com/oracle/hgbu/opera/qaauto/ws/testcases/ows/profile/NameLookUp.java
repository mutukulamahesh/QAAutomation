package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.sql.SQLException;
import java.util.ArrayList;
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


public class NameLookUp extends WSSetUp{

	//Profile creation Function


	public void setOwsHeader() throws SQLException{
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}
	public void setOperaHeader(){
		//Set opera header.
		String uname = OPERALib.getUserName();
		String resort = OPERALib.getResort();
		String chain = OPERALib.getChain();
		WSClient.setData("{var_chain}", chain);
		WSClient.setData("{var_resort}", resort);
		OPERALib.setOperaHeader(uname);
	}


	@Test(groups = { "sanity", "Name", "NameLookup", "OWS","nameLookup_23452" })
	public void nameLookup_23452() {

		try {
			String testName = "nameLookup_23452";
			WSClient.startTest(testName, "Verify that if all the profiles are fetched for given lastname in Namelookup criteria. ", "sanity");

			//Setting constant last name.
			setOperaHeader();
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			//				 String lname = "MASON";
			WSClient.setData("{var_lname}", lname);

			//creating First Profile.
			String fname1 = WSClient.getKeywordData("{KEYWORD_FNAME}");
			WSClient.setData("{var_fname}", fname1);
			CreateProfile.createProfile("DS_06");

			//Creating the second profile.
			String fname2 = WSClient.getKeywordData("{KEYWORD_FNAME}");
			WSClient.setData("{var_fname}", fname2);
			String profileID = "";
			profileID = CreateProfile.createProfile("DS_06");

			/****************************** Perform the nameLookup *********************************/
			if (profileID != "") {
				//Set ows Header.
				setOwsHeader();
				String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_01");
				String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

				if (WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					//Add the xpaths of the elements you want to fetch
					LinkedHashMap<String, String> xpaths = new LinkedHashMap<>();

					xpaths.put("Customer_PersonName_lastName", "NameLookupResponse_Profiles_Profile");
					xpaths.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");

					//Get all the Last names and operaIds from Response
					List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
					actualValues = WSClient.getMultipleNodeList(nameLookupRes, xpaths, false, XMLType.RESPONSE);

					//Get all DB values (FirstName, LastName, ProfileID)
					List<LinkedHashMap<String, String>> expectedValues = new ArrayList<LinkedHashMap<String, String>>();
					String query = WSClient.getQuery("QS_06");
					expectedValues = WSClient.getDBRows(query);

					if (WSAssert.assertEquals(actualValues, expectedValues, false)) {
						WSClient.writeToReport(LogStatus.INFO, "<b> All the Records are Fetched</b>");
					}
				}
				//Opera Error Code
				if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
					String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
					String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_23455() {

		try {
			String testName = "nameLookup_23455";
			WSClient.startTest(testName, "Verify that the correct profile is fetched for all the given details in Namelookup criteria", "minimumRegression");


			setOperaHeader();
			/************ creating profile1**********************/
			String lname1 = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_lname}", lname1);

			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			WSClient.setData("{var_fname}", fname);
			String profileID1 = CreateProfile.createProfile("DS_06");
			String email = (fname + "." + lname1 +"@oracle.com").toLowerCase();
			WSClient.setData("{var_email}", email);
			WSClient.setData("{var_primary}", "true");

			/************ creating profile2**********************/
			String lname2 = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_lname}", lname2);
			String fname2 = WSClient.getKeywordData("{KEYWORD_FNAME}");
			WSClient.setData("{var_fname}", fname2);
			String email2 = (fname2 + "." + lname2 +"@oracle.com").toLowerCase();
			WSClient.setData("{var_email}", email2);
			WSClient.setData("{var_primary}", "true");
			String profileID2 = CreateProfile.createProfile("DS_06");

			//			 Add membership for the Profiles Created.
			boolean membershipsAdded = false;
			if(profileID1 != "" && profileID2 != ""){

				/*************************** Add membership for profile1 ************************/
				String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
				WSClient.setData("{var_memNo}",member_num);
				WSClient.setData("{var_profileId}", profileID1);
				WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
				WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));

				String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
				String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
				membershipsAdded = WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
				if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
					WSClient.writeToReport(LogStatus.INFO,
							"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
				}

				/*************************** Add membership for profile2 ************************/
				if(membershipsAdded){
					WSClient.setData("{var_profileId}", profileID2);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));

					createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
					membershipsAdded &= WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
					if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
						WSClient.writeToReport(LogStatus.INFO,
								"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}
				}
			}
			if(membershipsAdded){

				//set OWS Header
				setOwsHeader();

				String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_09");
				String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

				if (WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					LinkedHashMap<String, String> xpaths = new LinkedHashMap<>();

					xpaths.put("Customer_PersonName_lastName", "NameLookupResponse_Profiles_Profile");
					xpaths.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");

					//Get all the Last names and operaIds from Response
					List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
					actualValues = WSClient.getMultipleNodeList(nameLookupRes, xpaths, false, XMLType.RESPONSE);

					//Get all DB values (FirstName, LastName, ProfileID)
					List<LinkedHashMap<String, String>> expectedValues = new ArrayList<LinkedHashMap<String, String>>();
					String query = WSClient.getQuery("QS_04");
					expectedValues = WSClient.getDBRows(query);

					if (WSAssert.assertEquals(actualValues, expectedValues, false)) {
						WSClient.writeToReport(LogStatus.INFO, "<b> All the Records are Fetched</b>");
					}
				}
				//Opera Error Code
				if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
					String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
					String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}


	@Test(groups = { "minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_45998() {

		try {
			String testName = "nameLookup_45998";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid email address is sent in the request ", "minimumRegression");

			//Setting constant last name.
			setOperaHeader();
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			//				 String lname = "MASON";
			WSClient.setData("{var_lname}", lname);

			//creating First Profile.
			String fname1 = WSClient.getKeywordData("{KEYWORD_FNAME}");
			WSClient.setData("{var_fname}", fname1);
			CreateProfile.createProfile("DS_06");
			String email =  (fname1 + "." + lname +"@oracle.com").toLowerCase();
			WSClient.setData("{var_email}", email);
			WSClient.setData("{var_primary}", "true");
			String profileID1 = "";
			profileID1 = CreateProfile.createProfile("DS_13");

			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
			String profileId2 = CreateProfile.createProfile("DS_13");

			if(profileID1 != "" && profileId2 != ""){

				//set OWS Header
				setOwsHeader();
				String email2 = WSClient.getKeywordData("{KEYWORD_RANDNUM_5}") ;
				WSClient.setData("{var_email}", email2);
				String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_02");
				String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);



				/****************************** Perform the nameLookup *********************************/


				//Opera Error Code
				if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
					String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
					String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}




	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_34244(){
		try{

			String testName = "nameLookup_34244";
			WSClient.startTest(testName, "Verify that correct profile is fetched for given EmailID .", "minimumRegression");
			String preReq[] = {"CommunicationMethod"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){


				// first Profile With email: fname1.lname1@oracle.com
				String lname1 = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname1);
				String fname1 = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname1);
				String email = (fname1 + "." + lname1 +"@oracle.com").toLowerCase();
				WSClient.setData("{var_email}", email);
				WSClient.setData("{var_primary}", "true");
				String profileID1 = "";
				profileID1 = CreateProfile.createProfile("DS_13");

				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				String profileId2 = CreateProfile.createProfile("DS_13");

				if(profileID1 != "" && profileId2 != ""){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_02");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

					//Validating
					if(WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)){
						String query = WSClient.getQuery("QS_02");
						ArrayList<LinkedHashMap<String, String>> expectedValues = WSClient.getDBRows(query);
						LinkedHashMap<String,String> xpath = new LinkedHashMap<>();

						xpath.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");
						xpath.put("Profile_EMails_NameEmail", "NameLookupResponse_Profiles_Profile");
						ArrayList<LinkedHashMap<String, String>> actualValues = (ArrayList<LinkedHashMap<String, String>>) WSClient.getMultipleNodeList(nameLookupRes, xpath, false, XMLType.RESPONSE);
						WSAssert.assertEquals(expectedValues, actualValues, false);
					}


					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_34298(){
		try{

			String testName = "nameLookup_34298";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid email address is sent in the request", "minimumRegression");
			String preReq[] = {"CommunicationMethod"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){



				String lname1 = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname1);
				String fname1 = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname1);
				String email = (fname1 + "." + lname1 +"@oracle.com").toLowerCase();
				WSClient.setData("{var_email}", email);
				WSClient.setData("{var_primary}", "true");
				String profileID1 = "";
				profileID1 = CreateProfile.createProfile("DS_13");


				if(profileID1 != ""){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_06");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);




					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}


	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_32325(){
		try{

			String testName = "nameLookup_32325";
			WSClient.startTest(testName, "Verify all the profiles are fetched for given Name in name Criteria.", "minimumRegression");
			String preReq[] = {"CommunicationMethod"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){

				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				//				 String lname = "DELEON";
				WSClient.setData("{var_lname}", lname);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);


				String profileID = "";
				profileID = CreateProfile.createProfile("DS_06");

				if(profileID != ""){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_03");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

					//Validating
					if(WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)){
						String query = WSClient.getQuery("QS_03");
						System.out.println(query);
						System.out.println(query);
						List<LinkedHashMap<String,String>> expectedValues = WSClient.getDBRows(query);
						LinkedHashMap<String, String> xpath = new LinkedHashMap<>();
						xpath.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");
						List<LinkedHashMap<String,String>> actualValues = WSClient.getMultipleNodeList(nameLookupRes, xpath, false, XMLType.RESPONSE);
						WSAssert.assertEquals(actualValues,expectedValues,  false);

					}
					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}



	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_34875(){
		try{

			String testName = "nameLookup_34875";
			WSClient.startTest(testName, "Verify all the profiles are fetched for given lastname with membership when profiles have same last name and membership number.", "minimumRegression");
			String preReq[] = {"MembershipType"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){

				//				 data for creating the profile1.
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);

				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);
				String profileID1 = CreateProfile.createProfile("DS_06");

				/************ creating profile2 with same lastName. **********************/
				String fname2 = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname2);
				String profileID2 = CreateProfile.createProfile("DS_06");




				//				 Add membership for the Profiles Created.
				boolean membershipsAdded = false;
				if(profileID1 != "" && profileID2 != ""){

					/*************************** Add membership for profile1 ************************/
					String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}",member_num);
					WSClient.setData("{var_profileId}", profileID1);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));

					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
					membershipsAdded = WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
					if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
						WSClient.writeToReport(LogStatus.INFO,
								"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

					/*************************** Add membership for profile1 ************************/
					if(membershipsAdded){
						WSClient.setData("{var_profileId}", profileID2);
						WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
						WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));

						createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
						createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
						membershipsAdded &= WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
						if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
							WSClient.writeToReport(LogStatus.INFO,
									"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
						}
					}
				}
				if(membershipsAdded){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_04");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

					//Validating
					if(WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)){
						String query  = WSClient.getQuery("QS_04");
						System.out.println(query);
						List<LinkedHashMap<String,String>> expectedValues = WSClient.getDBRows(query);

						LinkedHashMap<String, String> xpath = new LinkedHashMap<>();
						xpath.put("Customer_PersonName_lastName", "NameLookupResponse_Profiles_Profile");
						xpath.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");
						List<LinkedHashMap<String,String>> actualValues =
								WSClient.getMultipleNodeList(nameLookupRes, xpath, false, XMLType.RESPONSE);

						WSAssert.assertEquals(actualValues, expectedValues, false);
						System.out.println(expectedValues);
					}

					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>************ Blocked: Falied to Add Memberships ************</b>");
				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_34876(){
		try{

			String testName = "nameLookup_34876";
			WSClient.startTest(testName, "Verify that the correct profile is fetched when the required membership details are provided in the request.", "minimumRegression");
			String preReq[] = {"MembershipType"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){

				//				 data for creating the profile1.
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);

				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);
				String profileID1 = CreateProfile.createProfile("DS_06");




				//				 Add membership for the Profiles Created.
				boolean membershipsAdded = false;
				if(profileID1 != "" ){

					/*************************** Add membership for profile1 ************************/
					String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}",member_num);
					WSClient.setData("{var_profileId}", profileID1);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));

					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
					membershipsAdded = WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
					if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
						WSClient.writeToReport(LogStatus.INFO,
								"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}


				}
				if(membershipsAdded){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_04");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

					//Validating
					if(WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)){
						String query  = WSClient.getQuery("QS_04");
						System.out.println(query);
						List<LinkedHashMap<String,String>> expectedValues = WSClient.getDBRows(query);

						LinkedHashMap<String, String> xpath = new LinkedHashMap<>();
						xpath.put("Customer_PersonName_lastName", "NameLookupResponse_Profiles_Profile");
						xpath.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");
						List<LinkedHashMap<String,String>> actualValues =
								WSClient.getMultipleNodeList(nameLookupRes, xpath, false, XMLType.RESPONSE);

						WSAssert.assertEquals(actualValues, expectedValues, false);
						System.out.println(expectedValues);
					}

					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>************ Blocked: Falied to Add Memberships ************</b>");
				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	//@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_49874(){
		try{

			String testName = "nameLookup_49874";
			WSClient.startTest(testName, "Verify all the profiles are fetched for given lastname with membership when profiles have same last name and membershipType.", "minimumRegression");
			String preReq[] = {"MembershipType"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){

				//				 data for creating the profile1.
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);

				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);
				String profileID1 = CreateProfile.createProfile("DS_06");

				/************ creating profile2 with same lastName. **********************/
				String fname2 = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname2);
				String profileID2 = CreateProfile.createProfile("DS_06");

				//				 Add membership for the Profiles Created.
				boolean membershipsAdded = false;
				if(profileID1 != "" && profileID2 != ""){

					/*************************** Add membership for profile1 ************************/
					String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}",member_num);
					WSClient.setData("{var_profileId}", profileID1);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));

					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
					membershipsAdded = WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
					if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
						WSClient.writeToReport(LogStatus.INFO,
								"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

					/*************************** Add membership for profile1 ************************/
					if(membershipsAdded){
						member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}",member_num);
						WSClient.setData("{var_profileId}", profileID2);
						WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));

						createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
						createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
						membershipsAdded &= WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
						if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
							WSClient.writeToReport(LogStatus.INFO,
									"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
						}
					}
				}
				if(membershipsAdded){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_04");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

					//Validating
					if(WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)){
						String query  = WSClient.getQuery("QS_04");
						System.out.println(query);
						List<LinkedHashMap<String,String>> expectedValues = WSClient.getDBRows(query);

						LinkedHashMap<String, String> xpath = new LinkedHashMap<>();
						xpath.put("Customer_PersonName_lastName", "NameLookupResponse_Profiles_Profile");
						xpath.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");
						List<LinkedHashMap<String,String>> actualValues =
								WSClient.getMultipleNodeList(nameLookupRes, xpath, false, XMLType.RESPONSE);

						WSAssert.assertEquals(actualValues, expectedValues, false);
						System.out.println(expectedValues);
					}

					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>************ Blocked: Falied to Add Memberships ************</b>");
				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_49879(){
		try{

			String testName = "nameLookup_49879";
			WSClient.startTest(testName, "Verify that the given profile is fetched based on Email ID and Membership details", "minimumRegression");
			String preReq[] = {"MembershipType"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){

				//				 data for creating the profile1.
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);

				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);
				String email = (fname + "." + lname +"@oracle.com").toLowerCase();
				WSClient.setData("{var_email}", email);
				WSClient.setData("{var_primary}", "true");
				String profileID1 = CreateProfile.createProfile("DS_13");

				//				 Add membership for the Profiles Created.
				boolean membershipsAdded = false;
				if(profileID1 != ""){

					/*************************** Add membership for profile1 ************************/
					String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}",member_num);
					WSClient.setData("{var_profileId}", profileID1);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));

					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
					membershipsAdded = WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
					if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
						WSClient.writeToReport(LogStatus.INFO,
								"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

					/*************************** Add membership for profile1 ************************/

				}
				if(membershipsAdded){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_08");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

					//Validating
					if(WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)){
						String query  = WSClient.getQuery("QS_05");
						System.out.println(query);
						List<LinkedHashMap<String,String>> expectedValues = WSClient.getDBRows(query);

						LinkedHashMap<String, String> xpath = new LinkedHashMap<>();
						xpath.put("Customer_PersonName_lastName", "Profile_Customer_PersonName");
						xpath.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");
						xpath.put("Customer_PersonName_firstName", "Profile_Customer_PersonName");
						xpath.put("Profile_EMails_NameEmail", "Profiles_Profile_EMails");

						List<LinkedHashMap<String,String>> actualValues =
								WSClient.getMultipleNodeList(nameLookupRes, xpath, false, XMLType.RESPONSE);
						WSAssert.assertEquals(actualValues, expectedValues, false);

					}

					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>************ Blocked: Falied to Add Memberships ************</b>");
				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_49875(){
		try{

			String testName = "nameLookup_49875";
			WSClient.startTest(testName, "Verify that an error message is obtained when invalid membership details are given for a profile", "minimumRegression");
			String preReq[] = {"MembershipType"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){

				//				 data for creating the profile1.
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);

				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);
				String profileID1 = CreateProfile.createProfile("DS_06");

				WSClient.setData("{var_memTyp}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02").concat("X"));

				//				 Add membership for the Profiles Created.
				boolean membershipsAdded = false;
				if(profileID1 != ""){

					/*************************** Add membership for profile1 ************************/
					String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
					WSClient.setData("{var_memNo}",member_num);
					WSClient.setData("{var_profileId}", profileID1);
					WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));

					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
					membershipsAdded = WSAssert.assertIfElementExists(createMembershipRes,"CreateMembershipRS_Success",true);
					if(WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Errors_Error_Code", true)){
						WSClient.writeToReport(LogStatus.INFO,
								"<b> OperaErrorCode: " + WSClient.getElementValue(createMembershipRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}


				}
				if(membershipsAdded){

					//set OWS Header
					setOwsHeader();

					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_07");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);


					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>************ Blocked: Falied to Add Memberships ************</b>");
				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_58764(){
		try{
			String testName = "nameLookup_58764";
			WSClient.startTest(testName, "Verify only the Active profiles are fetched.", "minimumRegression");
			String preReq[] = {"MembershipType"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){

				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);

				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);
				String profileID = CreateProfile.createProfile("DS_33");
				if(profileID != ""){
					WSClient.setData("{var_profileId}", profileID);

					//Inactivate the Profile.
					String changeprofileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_12");
					String chanegProfileRes = WSClient.processSOAPMessage(changeprofileReq);

					if(WSAssert.assertIfElementExists(chanegProfileRes, "ChangeProfileRS_Success", true)){

						//set OWS Header
						setOwsHeader();
						String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_05");
						String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

						if (WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> xpath = new LinkedHashMap<>();
							xpath.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");
							List<LinkedHashMap<String, String>> actualValues = WSClient.getMultipleNodeList(nameLookupRes, xpath, false, XMLType.RESPONSE);
							System.out.println(actualValues);
							LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
							expectedValue.put("ProfileIDsUniqueID1", profileID);
							if (!actualValues.contains(expectedValue))
								WSClient.writeToReport(LogStatus.PASS, "Inactive Profile is NOT Fetched.");
							else WSClient.writeToReport(LogStatus.FAIL, "Inactive Profile is fetched in the response.");
						}


						//Opera Error Code
						if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
							String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
							String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "<***************** Pre-Requisite failure: Inactivating Profile Failed *****************>");
					}
				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = {"minimumRegression", "Name", "NameLookup", "OWS" })
	public void nameLookup_62345(){
		try{
			String testName = "nameLookup_62345";
			WSClient.startTest(testName, "Verify success when details of profile with no first name are provided", "minimumRegression");
			String preReq[] = {"MembershipType"};
			setOperaHeader();
			if(OperaPropConfig.getPropertyConfigResults(preReq)){
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", "");

				String profileId  = CreateProfile.createProfile("DS_06");
				if(profileId != ""){

					//set OWS Header
					setOwsHeader();
					String nameLookupReq = WSClient.createSOAPMessage("OWSNameLookup", "DS_01");
					String nameLookupRes = WSClient.processSOAPMessage(nameLookupReq);

					//Validating
					if(WSAssert.assertIfElementValueEquals(nameLookupRes, "NameLookupResponse_Result_resultStatusFlag", "SUCCESS", false)){

						//Add the xpaths of the elements you want to fetch
						LinkedHashMap<String, String> xpaths = new LinkedHashMap<>();

						xpaths.put("Customer_PersonName_lastName", "NameLookupResponse_Profiles_Profile");
						xpaths.put("Profile_ProfileIDs_UniqueID", "NameLookupResponse_Profiles_Profile");

						//Get all the Last names and operaIds from Response
						List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
						actualValues =  WSClient.getMultipleNodeList(nameLookupRes, xpaths, false, XMLType.RESPONSE);

						//Get all DB values (FirstName, LastName, ProfileID)
						List<LinkedHashMap<String, String>> expectedValues = new ArrayList<LinkedHashMap<String, String>>();
						String query = WSClient.getQuery("QS_06");
						expectedValues = WSClient.getDBRows(query);

						if(WSAssert.assertEquals(actualValues, expectedValues, false)){
							WSClient.writeToReport(LogStatus.INFO, "<b> All the Records are Fetched</b>");
						}
					}

					//Opera Error Code
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "NameLookupResponse_Result_GDSError", true)) {
						String message = WSAssert.getElementValue(nameLookupRes, "NameLookupResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(nameLookupRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(nameLookupRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}


				}
			}
		} catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}
}
