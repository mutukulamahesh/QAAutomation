package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class InsertUpdatePrivacyOption extends WSSetUp {
	
	String profileID="";
	
	@Test(groups = { "sanity","InsertUpdatePrivacyOption","Name","OWS","in-QA" })
	public void insertupdateprivacyOption_38455() {
		try {

			String testName = "insertUpdatePrivacyOption_38455";
			WSClient.startTest(testName, "Verify that all the privacy options are inserted ", "sanity");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chainCode}", chain);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(username);
			
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{	
			

					WSClient.setData("{var_profileID}", profileID);		
			

					OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
					String insertupdateprivacyReq = WSClient.createSOAPMessage("OWSInsertUpdatePrivacyOption", "DS_02");
					String insertupdateprivacyRes = WSClient.processSOAPMessage(insertupdateprivacyReq);
					if (WSAssert.assertIfElementValueEquals(insertupdateprivacyRes,
							"InsertUpdatePrivacyOptionResponse_Result_resultStatusFlag","SUCCESS", false)) {

						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						
						String query;
						query=WSClient.getQuery("QS_02");
						db = WSClient.getDBRow(query);

						String id = WSClient.getElementValue(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_NameID", XMLType.REQUEST);
						
						String marketresearch = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption_OptionType", "MarketResearch",
								XMLType.REQUEST);
						String thirdparty = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[2]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[2]_OptionType", "ThirdParties",
								XMLType.REQUEST);
						String loyalty = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[3]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[3]_OptionType", "LoyaltyProgram",
								XMLType.REQUEST);
						String email = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[4]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[4]_OptionType", "Email",
								XMLType.REQUEST);
						String phone = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[5]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[5]_OptionType", "Phone",
								XMLType.REQUEST);

						LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
						actualValues.put("NAME_ID", id);
						actualValues.put("MARKET_RESEARCH_YN", marketresearch);
						actualValues.put("THIRD_PARTY_YN", thirdparty);
						actualValues.put("AUTOENROLL_MEMBER_YN", loyalty);
						actualValues.put("EMAIL_YN", email);
						actualValues.put("PHONE_YN", phone);

						// Replacing the elements of response from Y to YES
						// and N to NO

						db.replace("MARKET_RESEARCH_YN", "Y", "YES");
						db.replace("MARKET_RESEARCH_YN", "N", "NO");
						db.replace("THIRD_PARTY_YN", "Y", "YES");
						db.replace("THIRD_PARTY_YN", "N", "NO");
						db.replace("AUTOENROLL_MEMBER_YN", "Y", "YES");
						db.replace("AUTOENROLL_MEMBER_YN", "N", "NO");
						db.replace("EMAIL_YN", "Y", "YES");
						db.replace("EMAIL_YN", "N", "NO");
						db.replace("PHONE_YN", "Y", "YES");
						db.replace("PHONE_YN", "N", "NO");

						// ------------------Checking if Privacy Option is
						// Validated properly-------//
						
						if(WSAssert.assertEquals(db, actualValues, false))
						{
							WSClient.writeToReport(LogStatus.PASS,"Privacy Options are validated properly");
						}

					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Privacy option is not inserted");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

//	
	
	
	@Test(groups = { "minimumRegression" ,"InsertUpdatePrivacyOption","Name","OWS","in-QA"})
	public void insertupdateprivacyOption_invalid_38464() {
		try {

			String testName = "insertUpdatePrivacyOption_38464";
			WSClient.startTest(testName, "Verify that the error message exists when invalid  privacyOption is passed ", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chainCode}", chain);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(username);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{	
			

					WSClient.setData("{var_profileID}", profileID);

			
			
					OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
					String insertupdateprivacyReq = WSClient.createSOAPMessage("OWSInsertUpdatePrivacyOption", "DS_06");
					String insertupdateprivacyRes = WSClient.processSOAPMessage(insertupdateprivacyReq);
					
					if (WSAssert.assertIfElementExists(insertupdateprivacyRes, "Result_Text_TextElement", true)) {
						
						WSClient.writeToReport(LogStatus.INFO, "<b>The error message is:</b>"+WSClient.getElementValue(insertupdateprivacyRes,
								"Result_Text_TextElement", XMLType.RESPONSE));

					}
					if (WSAssert.assertIfElementExists(insertupdateprivacyRes,
						"InsertUpdatePrivacyOptionResponse_Result_GDSError", true)) {
						WSClient.writeToReport(LogStatus.INFO,"<b>The error message is:</b>"+ WSClient.getElementValue(insertupdateprivacyRes,
								"InsertUpdatePrivacyOptionResponse_Result_GDSError", XMLType.RESPONSE));

					}
					WSClient.writeToReport(LogStatus.INFO,"<b>Passing Invalid Data</b>");
					
						
					if(WSAssert.assertIfElementExists(insertupdateprivacyRes,"Fault_faultstring",true)){
						WSClient.writeToReport(LogStatus.INFO, "<b>The error message is:</b>"+WSClient.getElementValue(insertupdateprivacyRes,
								"Fault_faultstring", XMLType.RESPONSE));
					}
					
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	

	@Test(groups = { "minimumRegression","InsertUpdatePrivacyOption","Name","OWS" ,"in-QA"})
	public void insertupdateprivacyOption_38463() {
		try {

			String testName = "insertUpdatePrivacyOption_38463";
			WSClient.startTest(testName, "Verify error message is populated when name id is passed as string", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chainCode}", chain);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(username);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{	
			

					WSClient.setData("{var_profileID}", profileID);
	
			

					OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
					String insertupdateprivacyReq = WSClient.createSOAPMessage("OWSInsertUpdatePrivacyOption", "DS_03");
					String insertupdateprivacyRes = WSClient.processSOAPMessage(insertupdateprivacyReq);
					
					
					WSAssert.assertIfElementValueEquals(insertupdateprivacyRes,"InsertUpdatePrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);
					WSClient.writeToReport(LogStatus.INFO,"<b>Name-id is passed as a string</b>");
					if (WSAssert.assertIfElementExists(insertupdateprivacyRes, "Result_Text_TextElement", true)) {
						
						WSClient.writeToReport(LogStatus.INFO, "<b>The error message is: </b>"+WSClient.getElementValue(insertupdateprivacyRes,
								"Result_Text_TextElement", XMLType.RESPONSE));

					}
					if (WSAssert.assertIfElementExists(insertupdateprivacyRes,
						"InsertUpdatePrivacyOptionResponse_Result_GDSError", true)) {
						WSClient.writeToReport(LogStatus.INFO,"<b>The error message is: </b>"+ WSClient.getElementValue(insertupdateprivacyRes,
								"DeletePrivacyOptionResponse_Result_GDSError", XMLType.RESPONSE));

					}
					
						

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
//	
	@Test(groups = { "minimumRegression","InsertUpdatePrivacyOption","Name","OWS","in-QA" })
	public void insertupdateprivacyOption_38458() {
		try {

			String testName = "insertUpdatePrivacyOption_38458";
			WSClient.startTest(testName, "Verify that error message is populated when invalid NameID is passed", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chainCode}", chain);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
					String query=WSClient.getQuery("OWSInsertUpdatePrivacyOption", "QS_03");
					String operaProfileID=WSClient.getDBRow(query).get("ProfileID");
					WSClient.setData("{var_profileID}", operaProfileID);


					OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
					String insertupdateprivacyReq = WSClient.createSOAPMessage("OWSInsertUpdatePrivacyOption", "DS_05");
					String insertupdateprivacyRes = WSClient.processSOAPMessage(insertupdateprivacyReq);
					
					WSClient.writeToReport(LogStatus.INFO,"<b>Passing invalid name-type</b>");
					if (WSAssert.assertIfElementExists(insertupdateprivacyRes, "Result_Text_TextElement", true)) {
						
						WSClient.writeToReport(LogStatus.INFO, "<b>The error message is: </b>"+WSClient.getElementValue(insertupdateprivacyRes,
								"Result_Text_TextElement", XMLType.RESPONSE));

					}
					if (WSAssert.assertIfElementExists(insertupdateprivacyRes,
						"InsertUpdatePrivacyOptionResponse_Result_GDSError", true)) {
						
						WSClient.writeToReport(LogStatus.INFO,"<b>The error message is: </b>"+ WSClient.getElementValue(insertupdateprivacyRes,
								"InsertUpdatePrivacyOptionResponse_Result_GDSError", XMLType.RESPONSE));

					}
					//WSClient.writeToReport(LogStatus.INFO,"<b>Validating if the result flag is populated as FAIL</b>");
					WSAssert.assertIfElementValueEquals(insertupdateprivacyRes,"InsertUpdatePrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);
									

			

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
	
	
	@Test(groups = { "minimumRegression","InsertUpdatedPrivacyOption","Name","OWS" ,"in-QA"})
	public void insertupdateprivacyOption_38465() {
		try {

			String testName = "insertUpdatePrivacyOption_38465";
			WSClient.startTest(testName, "Verify that the privacy options are updated properly", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chainCode}", chain);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(username);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{	
			

					WSClient.setData("{var_profileID}", profileID);

			
			
					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_03");
					String changeProfileResXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResXML, "ChangeProfileRS_Success", true)) {

					OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
					String insertupdateprivacyReq = WSClient.createSOAPMessage("OWSInsertUpdatePrivacyOption", "DS_02");
					String insertupdateprivacyRes = WSClient.processSOAPMessage(insertupdateprivacyReq);
					if (WSAssert.assertIfElementValueEquals(insertupdateprivacyRes,
							"InsertUpdatePrivacyOptionResponse_Result_resultStatusFlag","SUCCESS", false)) {

						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						
						String query=WSClient.getQuery("QS_02");
						db = WSClient.getDBRow(query);

						String id = WSClient.getElementValue(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_NameID", XMLType.REQUEST);
						
						String marketresearch = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption_OptionType", "MarketResearch",
								XMLType.REQUEST);
						String thirdparty = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[2]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[2]_OptionType", "ThirdParties",
								XMLType.REQUEST);
						String loyalty = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[3]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[3]_OptionType", "LoyaltyProgram",
								XMLType.REQUEST);
						String email = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[4]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[4]_OptionType", "Email",
								XMLType.REQUEST);
						String phone = WSClient.getElementValueByAttribute(insertupdateprivacyReq,
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[5]_OptionValue",
								"InsertUpdatePrivacyOptionRequest_Privacy_PrivacyOption[5]_OptionType", "Phone",
								XMLType.REQUEST);

						LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
						actualValues.put("NAME_ID",id);
						actualValues.put("MARKET_RESEARCH_YN", marketresearch);
						actualValues.put("THIRD_PARTY_YN", thirdparty);
						actualValues.put("AUTOENROLL_MEMBER_YN", loyalty);
						actualValues.put("EMAIL_YN", email);
						actualValues.put("PHONE_YN", phone);

						// Replacing the elements of response from Y to YES
						// and N to NO

						db.replace("MARKET_RESEARCH_YN", "Y", "YES");
						db.replace("MARKET_RESEARCH_YN", "N", "NO");
						db.replace("THIRD_PARTY_YN", "Y", "YES");
						db.replace("THIRD_PARTY_YN", "N", "NO");
						db.replace("AUTOENROLL_MEMBER_YN", "Y", "YES");
						db.replace("AUTOENROLL_MEMBER_YN", "N", "NO");
						db.replace("EMAIL_YN", "Y", "YES");
						db.replace("EMAIL_YN", "N", "NO");
						db.replace("PHONE_YN", "Y", "YES");
						db.replace("PHONE_YN", "N", "NO");

						// ------------------Checking if Privacy Option is
						// Validated properly-------//
						WSClient.writeToReport(LogStatus.INFO, "<b>Verify that the privacy options are updated</b>");
						WSAssert.assertEquals( actualValues,db, false);

					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Privacy option is not inserted");
					}
					}

				 
					else{
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre-requisite-----------ChangeProfile is not created----------");
					}
				}
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	

	
}
