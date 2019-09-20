package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

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

public class DeletePhone extends WSSetUp {

	@Test(groups = { "sanity","Name", "DeletePhone", "OWS","in-QA" })

	public void deletePhone_38267() {
		try {
			String testName = "deletePhone_38267";
			WSClient.startTest(testName,
					"Verify that phone record of a profile is getting deleted ",
					"sanity");
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType"})){
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String profileID = "";

				WSClient.setData("{var_phoneType}",
						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				profileID = CreateProfile.createProfile("DS_11");
				if (!profileID.equals("error")) {


					WSClient.writeToReport(LogStatus.INFO,
							"<b>Created profile with a phone record attached<b>");


					WSClient.setData("{var_profileID}", profileID);
					String query=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
					String phoneid = phn.get("PHONE_ID");
					WSClient.setData("{var_phoneID}", phoneid);

					// OWS delete phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String deletePhoneReq = WSClient.createSOAPMessage("OWSDeletePhone", "DS_01");
					String deletePhoneResponseXML = WSClient.processSOAPMessage(deletePhoneReq);

					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Delete phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_OperaErrorCode",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/
						String message1 = WSAssert.getElementValue(deletePhoneResponseXML,
								"DeletePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Delete Phone response is :----</b> " + message1);


					}
					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_GDSError",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/


						String message = WSAssert.getElementValue(deletePhoneResponseXML,
								"DeletePhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDS error displayed in the Delete Phone response is :---</b> " + message);
					}


					if (WSAssert.assertIfElementValueEquals(deletePhoneResponseXML,
							"DeletePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						query=WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> db = WSClient.getDBRow(query);
						if (WSAssert.assertEquals("0", db.get("COUNT"), true)){
							WSClient.writeToReport(LogStatus.PASS, "Deleted Phone record");
							WSClient.writeToReport(LogStatus.PASS, "COUNT-->EXPECTED:   0        ACTUAL:   "+db.get("COUNT"));
						}
						else
							WSClient.writeToReport(LogStatus.FAIL, "Error in deleting phone record");

					}


				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : New Profile not created**********");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}


	//MinimumRegression:case1---Invalid phoneId is passed in request

	@Test(groups = { "minimumRegression", "DeletePhone", "Name","OWS","in-QA" })

	public void deletePhone_38268() {
		try {
			String testName = "deletePhone_38268";
			WSClient.startTest(testName,
					"verify that error message is coming in the response when invalid phone number id is given in the request",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);

			// OWS Delete Phone
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String deletePhoneReq = WSClient.createSOAPMessage("OWSDeletePhone", "DS_02");
			String deletePhoneResponseXML = WSClient.processSOAPMessage(deletePhoneReq);
			if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text", true)) {

				/****
				 * Verifying that the error message is populated on the
				 * response
				 ********/

				String message = WSAssert.getElementValue(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The error displayed in the Delete phone response is----</b> :" + message);
			}
			if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_OperaErrorCode",
					true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/
				String message1 = WSAssert.getElementValue(deletePhoneResponseXML,
						"DeletePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OPERA error displayed in the Delete Phone response is :---</b> " + message1);


			}
			if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_GDSError",
					true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/


				String message = WSAssert.getElementValue(deletePhoneResponseXML,
						"DeletePhoneResponse_Result_GDSError", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The GDS error displayed in the Delete Phone response is :---</b> " + message);
			}

			if (WSAssert.assertIfElementValueEquals(deletePhoneResponseXML,
					"DeletePhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
				WSClient.writeToReport(LogStatus.PASS, "delete operation unsuccessful when invalid phoneID is provided");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	//MinimumRegression:case2---

	//	@Test(groups = { "minimumRegression", "DeletePhone", "Name","OWS","in-QA" })
	//
	//	public void deletePhone_EMAIL() {
	//		try {
	//			String testName = "deletePhone_EMAIL";
	//			WSClient.startTest(testName,
	//					"verify that email deleting",
	//					"minimumRegression");
	//			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType"})){
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//			 WSClient.setData("{var_phoneType}",
	//						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
	//			 String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	//				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	//			 WSClient.setData("{var_fname}", fname);
	//				WSClient.setData("{var_lname}", lname);
	//				WSClient.setData("{var_email}", fname + "." + lname + "@oracle.com");
	//				WSClient.setData("{var_primary}", "true");
	//				WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
	//						// Prerequisite 1 - create profile
	//						OPERALib.setOperaHeader(uname);
	//						String profileID = CreateProfile.createProfile("DS_47");
	//                if (!profileID.equals("error")) {
	//
	//
	//				WSClient.writeToReport(LogStatus.INFO,
	//						"<b>Created profile with a phone record attached<b>");
	//
	//
	//					WSClient.setData("{var_profileID}", profileID);
	//					String query=WSClient.getQuery("QS_04");
	//					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
	//					String phoneid = phn.get("PHONE_ID");
	//					WSClient.setData("{var_phoneID}", phoneid);
	//
	//			// OWS Delete Phone
	//			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//			String deletePhoneReq = WSClient.createSOAPMessage("OWSDeletePhone", "DS_01");
	//			String deletePhoneResponseXML = WSClient.processSOAPMessage(deletePhoneReq);
	//			if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text", true)) {
	//
	//				/****
	//				 * Verifying that the error message is populated on the
	//				 * response
	//				 ********/
	//
	//				String message = WSAssert.getElementValue(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text",
	//						XMLType.RESPONSE);
	//				WSClient.writeToReport(LogStatus.INFO,
	//						"<b>The error displayed in the Delete phone response is----</b> :" + message);
	//			}
	//			if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_OperaErrorCode",
	//					true)) {
	//
	//				/****
	//				 * Verifying whether the error Message is populated on the
	//				 * response
	//				 ****/
	//				String message1 = WSAssert.getElementValue(deletePhoneResponseXML,
	//						"DeletePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//
	//					WSClient.writeToReport(LogStatus.INFO,
	//							"<b>The OPERA error displayed in the Delete Phone response is :---</b> " + message1);
	//
	//
	//			}
	//			if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_GDSError",
	//					true)) {
	//
	//				/****
	//				 * Verifying whether the error Message is populated on the
	//				 * response
	//				 ****/
	//
	//
	//				String message = WSAssert.getElementValue(deletePhoneResponseXML,
	//						"DeletePhoneResponse_Result_GDSError", XMLType.RESPONSE);
	//
	//				WSClient.writeToReport(LogStatus.INFO,
	//						"<b>The GDS error displayed in the Delete Phone response is :---</b> " + message);
	//			}
	//			if (WSAssert.assertIfElementValueEquals(deletePhoneResponseXML,
	//					"DeletePhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
	//				WSClient.writeToReport(LogStatus.PASS, "delete operation unsuccessful when invalid phoneID is provided");
	//			}
	//}
	//
	//			}
	//		} catch (Exception e) {
	//			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
	//		}
	//	}
	@Test(groups = { "minimumRegression","Name", "DeletePhone", "OWS","in-QA" })

	public void deletePhone_42206() {
		try {
			String testName = "deletePhone_42206";
			WSClient.startTest(testName,
					"Verify that when primary phone is deleted then secondary becomes primary automatically",
					"minimumRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType"})){
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String profileID = "";


				String phnType1 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03").toUpperCase();
				String phnType2 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01").toUpperCase();


				WSClient.setData("{var_phoneType1}", phnType1);
				WSClient.setData("{var_phoneType2}", phnType2);

				WSClient.setData("{var_primary}", "true");
				WSClient.setData("{var_primary2}", "false");
				OPERALib.setOperaHeader(uname);
				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				profileID = CreateProfile.createProfile("DS_19");
				if (!profileID.equals("error")) {


					WSClient.writeToReport(LogStatus.INFO,
							"<b>Created profile with two phone records attached<b>");


					WSClient.setData("{var_profileID}", profileID);
					String query=WSClient.getQuery("QS_08");
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Query to get the primary phone details<b>");
					List<LinkedHashMap<String, String>> dbValues=WSClient.getDBRows(query);
					for (int i = 0; i < 2; i++) {
						//WSClient.writeToReport(LogStatus.PASS, "HI VALUES ARE"+dbValues);
						if (dbValues.get(i).get("PRIMARY_YN").equals("false")) {

							String phoneid_N = dbValues.get(i).get("PHONE_ID");

							WSClient.setData("{var_phoneID1}", phoneid_N);


						} else {
							String phoneid_Y = dbValues.get(i).get("PHONE_ID");
							WSClient.setData("{var_phoneID}", phoneid_Y);
						}
					}



					// OWS delete phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String deletePhoneReq = WSClient.createSOAPMessage("OWSDeletePhone", "DS_01");
					String deletePhoneResponseXML = WSClient.processSOAPMessage(deletePhoneReq);




					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Delete phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_OperaErrorCode",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/
						String message1 = WSAssert.getElementValue(deletePhoneResponseXML,
								"DeletePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Delete Phone response is :----</b> " + message1);


					}
					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_GDSError",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/


						String message = WSAssert.getElementValue(deletePhoneResponseXML,
								"DeletePhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDS error displayed in the Delete Phone response is :---</b> " + message);
					}


					if (WSAssert.assertIfElementValueEquals(deletePhoneResponseXML,
							"DeletePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						query=WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> db = WSClient.getDBRow(query);
						if (WSAssert.assertEquals("0", db.get("COUNT"), true)){
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully deleted primary phone<b>");
							query=WSClient.getQuery("CreateProfile","QS_08");
							LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Validating that secondary phone became primary after deleting primary<b>");
							if(WSAssert.assertEquals("true",phn.get("PRIMARY_YN"),true)){
								WSClient.writeToReport(LogStatus.PASS,"PRIMARY_YN--->EXPECTED:true         Actual:"+phn.get("PRIMARY_YN"));
							}
							else {
								WSClient.writeToReport(LogStatus.FAIL,"PRIMARY_YN--->EXPECTED:true         Actual:"+phn.get("PRIMARY_YN"));
							}
						}
						else
							WSClient.writeToReport(LogStatus.FAIL, "Error in deleting phone record");

					}


				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : New Profile not created**********");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	//MinimumRegression:case3---Type INTERNAL is missing in request

	@Test(groups = { "minimumRegression", "DeletePhone", "Name","OWS","in-QA" })

	public void deletePhone_42223() {
		try {
			String testName = "deletePhone_42223";
			WSClient.startTest(testName,
					"verify that error message is coming in the response when INTERNAL is missing in the request",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			String profileID = "";

			WSClient.setData("{var_phoneType}",
					OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			profileID = CreateProfile.createProfile("DS_11");
			if (!profileID.equals("error")) {


				WSClient.writeToReport(LogStatus.INFO,
						"<b>Created profile with a phone record attached<b>");


				WSClient.setData("{var_profileID}", profileID);
				String query=WSClient.getQuery("QS_04");
				LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
				String phoneid = phn.get("PHONE_ID");
				WSClient.setData("{var_phoneID}", phoneid);
				// OWS Delete Phone
				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				String deletePhoneReq = WSClient.createSOAPMessage("OWSDeletePhone", "DS_03");
				String deletePhoneResponseXML = WSClient.processSOAPMessage(deletePhoneReq);
				if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the Delete phone response is----</b> :" + message);
				}
				if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_OperaErrorCode",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/
					String message1 = WSAssert.getElementValue(deletePhoneResponseXML,
							"DeletePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OPERA error displayed in the Delete Phone response is :---</b> " + message1);


				}
				if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_GDSError",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/


					String message = WSAssert.getElementValue(deletePhoneResponseXML,
							"DeletePhoneResponse_Result_GDSError", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDS error displayed in the Delete Phone response is :---</b> " + message);
				}

				if (WSAssert.assertIfElementValueEquals(deletePhoneResponseXML,
						"DeletePhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, "delete operation unsuccessful when INTERNAL is missing in request");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression","Name", "DeletePhone", "OWS"})

	public void deletePhone_HHOCM_143() {
		try {
			String testName = "deletePhone_HHOCM_143";
			WSClient.startTest(testName,
					"Verify if a primary Phone  is deleted then one of the scondary phone number should be primary.",
					"minimumRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType"})){
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String profileID = "";

				WSClient.setData("{var_phoneType}",
						OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				profileID = CreateProfile.createProfile("DS_CreateProfile_53");
				if (!profileID.equals("error")) {


					WSClient.writeToReport(LogStatus.INFO,
							"<b>Created profile with a phone record attached<b>");


					WSClient.setData("{var_profileID}", profileID);
					WSClient.setData("{var_pflag}", "Y");
					String query=WSClient.getQuery("OWSDeletePhone","QS_02");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
					String phoneidPrimary = phn.get("PHONE_ID");


					WSClient.setData("{var_pflag}", "N");
					String query2=WSClient.getQuery("OWSDeletePhone","QS_02");
					LinkedHashMap<String, String> phn1 = WSClient.getDBRow(query2);
					String phoneidSecondary = phn1.get("PHONE_ID");

					//Before Deletion of Primary
					System.out.println("Primary  is" +phoneidPrimary);
					System.out.println("Secondary  is" +phoneidSecondary);

					WSClient.setData("{var_phoneID}", phoneidPrimary);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>Primary phone ID is </b> :" + phoneidPrimary);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Secondary Phone ID is</b> :" + phoneidSecondary);


					// OWS delete phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String deletePhoneReq = WSClient.createSOAPMessage("OWSDeletePhone", "DS_04");
					String deletePhoneResponseXML = WSClient.processSOAPMessage(deletePhoneReq);

					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(deletePhoneResponseXML, "DeletePhoneResponse_Result_Text",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Delete phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_OperaErrorCode",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/
						String message1 = WSAssert.getElementValue(deletePhoneResponseXML,
								"DeletePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Delete Phone response is :----</b> " + message1);


					}
					if (WSAssert.assertIfElementExists(deletePhoneResponseXML, "DeletePhoneResponse_Result_GDSError",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/


						String message = WSAssert.getElementValue(deletePhoneResponseXML,
								"DeletePhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDS error displayed in the Delete Phone response is :---</b> " + message);
					}

					//DB Validation
					// Additional
					if (WSAssert.assertIfElementValueEquals(deletePhoneResponseXML,
							"DeletePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.setData("{var_pflag}", "Y");
						query=WSClient.getQuery("QS_02");
						LinkedHashMap<String, String> db = WSClient.getDBRow(query);
						if (WSAssert.assertEquals("Y", db.get("PRIMARY_YN"), true)){
							WSClient.writeToReport(LogStatus.PASS, "Secondary phone number is made primary");
							WSClient.writeToReport(LogStatus.PASS, "PRIMARY-->EXPECTED:   Y        ACTUAL:   "+db.get("PRIMARY_YN"));
							WSClient.writeToReport(LogStatus.PASS, "PHONE ID-->EXPECTED:  "+ phoneidSecondary+"       ACTUAL:   "+db.get("PHONE_ID"));

						}
						else
							WSClient.writeToReport(LogStatus.FAIL, "Secondary phone number is not made to primary!");

					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Error in deleting phone record!");
					}


				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : New Profile not created**********");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
}
