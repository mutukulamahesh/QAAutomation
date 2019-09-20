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

public class FetchPrivacyOption extends WSSetUp {

	String profileID="";
	@Test(groups = { "sanity","FetchPrivacyOption" ,"Name","OWS","in-QA"})
	public void fetchprivacyOption_38466() {
		try {

			String testName = "fetchPrivacyOption_38466";
			WSClient.startTest(testName, "Verify that the privacyOptions are fetched properly", "sanity");

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

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_02");
					String changeProfileResXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResXML, "ChangeProfileRS_Success", true)) {

						OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
						String fetchprivacyReq = WSClient.createSOAPMessage("OWSFetchPrivacyOption", "DS_01");
						String fetchprivacyRes = WSClient.processSOAPMessage(fetchprivacyReq);
						
						
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"Result_Text_TextElement", true))
						{
							
							WSClient.writeToReport(LogStatus.INFO,"<b>The error message is:</b>"+WSClient.getElementValue(fetchprivacyRes,"Result_Text_TextElement" , XMLType.RESPONSE) );
							
						}
						
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError", true))
						{
							
							WSClient.writeToReport(LogStatus.INFO,"<b>The error message is: </b>"+WSClient.getElementValue(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError" , XMLType.RESPONSE) );
							
						}
						
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_resultStatusFlag",false)){
						if (WSAssert.assertIfElementValueEquals(fetchprivacyRes,
								"FetchPrivacyOptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							
							String query=WSClient.getQuery("QS_01");
							db = WSClient.getDBRow(query);
							String marketresearch = WSClient.getElementValueByAttribute(fetchprivacyRes,
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionValue",
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionType", "MarketResearch",
									XMLType.RESPONSE);
							String thirdparty = WSClient.getElementValueByAttribute(fetchprivacyRes,
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionValue",
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionType", "ThirdParties",
									XMLType.RESPONSE);
							String loyalty = WSClient.getElementValueByAttribute(fetchprivacyRes,
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionValue",
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionType", "LoyaltyProgram",
									XMLType.RESPONSE);
							String email = WSClient.getElementValueByAttribute(fetchprivacyRes,
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionValue",
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionType", "Email",
									XMLType.RESPONSE);
							String sms = WSClient.getElementValueByAttribute(fetchprivacyRes,
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionValue",
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionType", "Phone",
									XMLType.RESPONSE);
							String phone = WSClient.getElementValueByAttribute(fetchprivacyRes,
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionValue",
									"FetchPrivacyOptionResponse_Privacy_PrivacyOption_OptionType", "SMS",
									XMLType.RESPONSE);

							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							actualValues.put("NAME_ID", profileID);
							actualValues.put("MARKET_RESEARCH_YN", marketresearch);
							actualValues.put("THIRD_PARTY_YN", thirdparty);
							actualValues.put("AUTOENROLL_MEMBER_YN", loyalty);
							actualValues.put("EMAIL_YN", email);
							actualValues.put("SMS_YN", sms);
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
							db.replace("SMS_YN", "Y", "YES");
							db.replace("SMS_YN", "N", "NO");
							db.replace("PHONE_YN", "Y", "YES");
							db.replace("PHONE_YN", "N", "NO");

							// ------------------Checking if Privacy Option is
							// Validated properly-------//
							
							WSAssert.assertEquals(db,actualValues,false);

						} else {
							
								WSClient.writeToReport(LogStatus.FAIL, "-----Success flag is not populated in the response-----");

							
						}
					} 
					
					}else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre-requisite-----------ChangeProfile is not created----------");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} 

		 catch (Exception e) {

			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	
	@Test(groups = { "minimumRegression","FetchPrivacyOption","Name","OWS","in-QA" })
	public void fetchprivacyOption_38472() {
		try {

			String testName = "fetchPrivacyOption_38472";
			WSClient.startTest(testName, "Verify that the error is displayed when name-id is not passed", "minimumRegression");

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
			
	

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_02");
					String changeProfileResXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResXML, "ChangeProfileRS_Success", true)) {

						OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
						String fetchprivacyReq = WSClient.createSOAPMessage("OWSFetchPrivacyOption", "DS_02");
						String fetchprivacyRes = WSClient.processSOAPMessage(fetchprivacyReq);
						
						WSClient.writeToReport(LogStatus.INFO,"<b>name-id is not passed on the response</b>");
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"Result_Text_TextElement", true))
						{
						
							WSClient.writeToReport(LogStatus.INFO,"<b>The error message is:</b>"+"<b>"+WSClient.getElementValue(fetchprivacyRes,"Result_Text_TextElement" , XMLType.RESPONSE)+"</b>" );
							
						}
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError", true))
						{
							
							WSClient.writeToReport(LogStatus.INFO,"<b>The error message is: </b>"+WSClient.getElementValue(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError" , XMLType.RESPONSE) );
							
						}
						
						
						WSAssert.assertIfElementValueEquals(fetchprivacyRes,
								"FetchPrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);

							
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre-requisite-----------ChangeProfile is not created----------");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			

		} catch (Exception e) {

			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
	@Test(groups = { "minimumRegression" ,"Name","FetchPrivacyOption","OWS","in-QA"})
	public void fetchprivacyOption_38470() {
		try {

			String testName = "fetchPrivacyOption_38470";
			WSClient.startTest(testName, "Verify that the error is displyed when  Type is Invalid", "minimumRegression");

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
			
			

					

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_02");
					String changeProfileResXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResXML, "ChangeProfileRS_Success", true)) {

						OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
						String fetchprivacyReq = WSClient.createSOAPMessage("OWSFetchPrivacyOption", "DS_03");
						String fetchprivacyRes = WSClient.processSOAPMessage(fetchprivacyReq);
						
						
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"Result_Text_TextElement", true))
						{
							
							WSClient.writeToReport(LogStatus.INFO,"<b>The error message is:</b>"+"<b>"+WSClient.getElementValue(fetchprivacyRes,"Result_Text_TextElement" , XMLType.RESPONSE)+"</b>" );
							
						}
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError", true))
						{
							WSClient.writeToReport(LogStatus.INFO,"<b>Error message is:</b>"+WSClient.getElementValue(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError" , XMLType.RESPONSE) );
							
						}
						
						WSClient.writeToReport(LogStatus.INFO,"<b>Passing  invalid name type</b>");
						WSAssert.assertIfElementValueEquals(fetchprivacyRes,
								"FetchPrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);
						

						
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre-requisite-----------ChangeProfile is not created----------");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			

		} catch (Exception e) {

			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	

	@Test(groups = { "minimumRegression","Name","FetchPrivacyOption","OWS" ,"in-QA"})
	public void fetchprivacyOption_38468() {
		try {

			String testName = "fetchPrivacyOption_38468";
			WSClient.startTest(testName, "Verify that the error is displayed when invalid name id is passed ", "minimumRegression");

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

			
			

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_02");
					String changeProfileResXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResXML, "ChangeProfileRS_Success", true)) {

						OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
						String query=WSClient.getQuery("OWSInsertUpdatePrivacyOption", "QS_03");
						String operaProfileID = WSClient.getDBRow(query).get("ProfileID");
						
						WSClient.setData("{var_profileID}", operaProfileID);

						String fetchprivacyReq = WSClient.createSOAPMessage("OWSFetchPrivacyOption", "DS_04");
						String fetchprivacyRes = WSClient.processSOAPMessage(fetchprivacyReq);
						
						WSClient.writeToReport(LogStatus.INFO,"<b>Passing name-id as string</b>");
						
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"Result_Text_TextElement", true))
						{
							
							WSClient.writeToReport(LogStatus.INFO,"<b>"+"Error in the response is:"+WSClient.getElementValue(fetchprivacyRes,"Result_Text_TextElement" , XMLType.RESPONSE)+"</b>" );
							
						}
						if(WSAssert.assertIfElementExists(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError", true))
						{
							WSClient.writeToReport(LogStatus.INFO,"GDS Error in the response is:"+WSClient.getElementValue(fetchprivacyRes,"FetchPrivacyOptionResponse_Result_GDSError" , XMLType.RESPONSE) );
							
						}
						
						
						WSAssert.assertIfElementValueEquals(fetchprivacyRes,
								"FetchPrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);

							
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre-requisite-----------ChangeProfile is not created----------");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			

		} catch (Exception e) {

			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	

}
