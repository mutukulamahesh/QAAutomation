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

public class DeletePrivacyOption extends WSSetUp {

	String profileID="";
	@Test(groups = { "sanity", "DeletePrivacyOption", "OWS", "Name" ,"in-QA"})
	public void deleteprivacyOption_38473() {
		try {

			String testName = "deletePrivacyOption_38473";
			WSClient.startTest(testName, "Verify that the  privacy"
					+ " options are deleted", "sanity");

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
						String deleteprivacyReq = WSClient.createSOAPMessage("OWSDeletePrivacyOption", "DS_01");
						String deleteprivacyRes = WSClient.processSOAPMessage(deleteprivacyReq);

						if (WSAssert.assertIfElementExists(deleteprivacyRes, "Result_Text_TextElement", true)) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(deleteprivacyRes,
									"Result_Text_TextElement", XMLType.RESPONSE));

						}
						if (WSAssert.assertIfElementExists(deleteprivacyRes,
								"DeletePrivacyOptionResponse_Result_GDSError", true)) {
						
						
							WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(deleteprivacyRes,
									"DeletePrivacyOptionResponse_Result_GDSError", XMLType.RESPONSE));

						}

						if (WSAssert.assertIfElementValueEquals(deleteprivacyRes,
								"DeletePrivacyOptionResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							
							
						String query=WSClient.getQuery("QS_01");
								db = WSClient.getDBRow(query);

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
							
							WSClient.writeToReport(LogStatus.INFO,"<b>Validating if the Privacy Options are updated to NO in the database:</b>");
							if ((db.get("MARKET_RESEARCH_YN") == "NO") && (db.get("THIRD_PARTY_YN") == "NO")
									&& (db.get("AUTOENROLL_MEMBER_YN") == "NO") && (db.get("EMAIL_YN") == "NO")
									&& (db.get("SMS_YN") == "NO") && (db.get("PHONE_YN") == "NO")) {

								WSClient.writeToReport(LogStatus.PASS,"MARKET_RESEARCH:"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("MARKET_RESEARCH_YN"));
								WSClient.writeToReport(LogStatus.PASS,"THIRD_PARTY :"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("THIRD_PARTY_YN"));
								WSClient.writeToReport(LogStatus.PASS,"AUTOENROLL_MEMBER:"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("AUTOENROLL_MEMBER_YN"));
								WSClient.writeToReport(LogStatus.PASS,"EMAIL :"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("EMAIL_YN"));
								WSClient.writeToReport(LogStatus.PASS,"SMS :"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("SMS_YN"));
								WSClient.writeToReport(LogStatus.PASS,"PHONE :"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("PHONE_YN"));
								WSClient.writeToReport(LogStatus.PASS, "Updated the PrivacyOptions as NO  ");

							} else {
								WSClient.writeToReport(LogStatus.FAIL,"MARKET_RESEARCH:"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("MARKET_RESEARCH_YN"));
								WSClient.writeToReport(LogStatus.FAIL,"THIRD_PARTY :"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("THIRD_PARTY_YN"));
								WSClient.writeToReport(LogStatus.FAIL,"AUTOENROLL_MEMBER:"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("AUTOENROLL_MEMBER_YN"));
								WSClient.writeToReport(LogStatus.FAIL,"EMAIL :"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("EMAIL_YN"));
								WSClient.writeToReport(LogStatus.FAIL,"SMS"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("SMS_YN"));
								WSClient.writeToReport(LogStatus.FAIL,"PHONE"+
										"ExpectedValue :" + "NO" + " ActualValue :" + db.get("PHONE_YN"));
								WSClient.writeToReport(LogStatus.FAIL,
										"--------Did not get updated to No in the database---------");

							}
							


						} else {

							WSClient.writeToReport(LogStatus.FAIL, "------------Error in the response-----");

						}
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

//	

	@Test(groups = { "minimumRegression", "Name", "OWS", "DeletePrivacyOption","in-QA" })
	public void deleteprivacyOption_38475() {
		try {

			String testName = "deleteprivacyOption_38475";
			WSClient.startTest(testName, "Verify that the error is displayed when invalid name type is passed", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();


			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chainCode}", chain);
			
			OPERALib.setOperaHeader(username);
			
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{	
			

					WSClient.setData("{var_profileID}", profileID);			

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_02");
					String changeProfileResXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResXML, "ChangeProfileRS_Success", true)) {

						OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
						String deleteprivacyReq = WSClient.createSOAPMessage("OWSDeletePrivacyOption", "DS_03");
						String deleteprivacyRes = WSClient.processSOAPMessage(deleteprivacyReq);
						
						WSClient.writeToReport(LogStatus.INFO, "<b>Passing invalid name type:  </b>");
						
						if (WSAssert.assertIfElementExists(deleteprivacyRes, "Result_Text_TextElement", true)) {
						
							WSClient.writeToReport(LogStatus.INFO,"<b>The TextElement Found in response is:</b>"+ WSClient.getElementValue(deleteprivacyRes,
									
									"Result_Text_TextElement", XMLType.RESPONSE));

						}
						if (WSAssert.assertIfElementExists(deleteprivacyRes,
								"DeletePrivacyOptionResponse_Result_GDSError", true)) {
							
							WSClient.writeToReport(
									LogStatus.INFO, "<b>"
											+ WSClient.getElementValue(deleteprivacyRes,"<b>The GDSError Found in the response is:</b>"+
													"DeletePrivacyOptionResponse_Result_GDSError", XMLType.RESPONSE)
											+ "<b>");

						}

						WSAssert.assertIfElementValueEquals(deleteprivacyRes,
								"DeletePrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);

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

	@Test(groups = { "minimumRegression", "Name", "OWS", "DeletePrivacyOption" ,"in-QA"})
	public void deleteprivacyOption_38474() {
		try {

			String testName = "deleteprivacyOption_38474";
			WSClient.startTest(testName, "Verify that the error is displayed when invalid profileID is passed", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chainCode}", chain);

			String query=WSClient.getQuery("OWSInsertUpdatePrivacyOption", "QS_03");
			String operaProfileID = WSClient.getDBRow(query).get("ProfileID");
			
			WSClient.setData("{var_profileID}", operaProfileID);

			
				OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
				String deleteprivacyReq = WSClient.createSOAPMessage("OWSDeletePrivacyOption", "DS_04");
				String deleteprivacyRes = WSClient.processSOAPMessage(deleteprivacyReq);
				WSClient.writeToReport(LogStatus.INFO,"<b> passing invalid name-id</b>");
				if (WSAssert.assertIfElementExists(deleteprivacyRes, "Result_Text_TextElement", true)) {
					
					
					WSClient.writeToReport(LogStatus.INFO, "<b>Error Found in the response:</b>"+"<b>"
							+ WSClient.getElementValue(deleteprivacyRes, "Result_Text_TextElement", XMLType.RESPONSE)
							+ "</b>");

				}
				if (WSAssert.assertIfElementExists(deleteprivacyRes, "DeletePrivacyOptionResponse_Result_GDSError",
						true)) {
					
				
					WSClient.writeToReport(LogStatus.INFO,"<b>GDSError Found in the response:</b>"+ "<b>" + WSClient.getElementValue(deleteprivacyRes,
							"DeletePrivacyOptionResponse_Result_GDSError", XMLType.RESPONSE) + "<b>");

				}
				

				WSAssert.assertIfElementValueEquals(deleteprivacyRes,
						"DeletePrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);
			

		} catch (Exception e) {

			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "Name", "OWS", "DeletePrivacyOption","in-QA" })
	public void deleteprivacyOption_38477() {
		try {

			String testName = "deleteprivacyOption_38477";
			WSClient.startTest(testName, "Verify that the error is displayed when name id is passed as string",
					"minimumRegression");

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
			
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{	
			

					WSClient.setData("{var_profileID}", profileID);

			

					String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_02");
					String changeProfileResXML = WSClient.processSOAPMessage(changeProfileReq);

					if (WSAssert.assertIfElementExists(changeProfileResXML, "ChangeProfileRS_Success", true)) {

						OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
						String deleteprivacyReq = WSClient.createSOAPMessage("OWSDeletePrivacyOption", "DS_04");
						String deleteprivacyRes = WSClient.processSOAPMessage(deleteprivacyReq);
						WSClient.writeToReport(LogStatus.INFO,"<b>Name id is passed as String</b>");
						if (WSAssert.assertIfElementExists(deleteprivacyRes, "Result_Text_TextElement", true)) {
							

							WSClient.writeToReport(LogStatus.INFO, "<b>Error in the response is:</b>"+"<b>" + WSClient.getElementValue(deleteprivacyRes,
									"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");

						}
						if (WSAssert.assertIfElementExists(deleteprivacyRes,
								"DeletePrivacyOptionResponse_Result_GDSError", true)) {
							

							WSClient.writeToReport(
									LogStatus.INFO,"<b>Error in the response is:</b>"+ "<b>"
											+ WSClient.getElementValue(deleteprivacyRes,
													"DeletePrivacyOptionResponse_Result_GDSError", XMLType.RESPONSE)
											+ "<b>");

						}

						WSAssert.assertIfElementValueEquals(deleteprivacyRes,
								"DeletePrivacyOptionResponse_Result_resultStatusFlag", "FAIL", false);

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
