package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

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
public class UpdateEmail extends WSSetUp {



	/**
	 * @author psarawag Description: Verify that Email is getting
	 *         updated.
	 */
	@Test(groups = { "sanity","Name" ,"UpdateEmail", "OWS","in-QA" })

	public void updateEmail_38289() {
		try {
			String testName = "updateEmail_38289";
			WSClient.startTest(testName,
					"Verify that Email is getting updated",
					"sanity");
			String preReq[] = { "CommunicationMethod" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {
				String resort = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String expectedPhoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_email}", (fname + "@oracle.com").toLowerCase());
				WSClient.setData("{var_primary}", "TRUE");
				WSClient.setData("{var_phoneRole}", expectedPhoneRole.toUpperCase());

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_13");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String email = (fname + "." + lname + "@oracle.com").toLowerCase();
					WSClient.setData("{var_email}", email);
					String query1=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query1);
					if(phn.size()>0){
						String phoneid = phn.get("PHONE_ID");
						WSClient.setData("{var_phoneID}", phoneid);

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						// Validation request being created and processed to
						// generate response
						String updateEmailReq = WSClient.createSOAPMessage("OWSUpdateEmail", "DS_01");
						String updateEmailResponseXML = WSClient.processSOAPMessage(updateEmailReq);
						if (WSAssert.assertIfElementValueEquals(updateEmailResponseXML,
								"UpdateEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							// database records are being stored in a linked hashmap
							String query2=WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> db = WSClient.getDBRow(query2);

							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();

							// request records are being stored in different
							// variables
							String email_ID = WSClient.getElementValue(updateEmailReq,
									"UpdateEmailRequest_NameEmail_operaId", XMLType.REQUEST);
							String emailID = WSClient.getElementValue(updateEmailReq, "UpdateEmailRequest_NameEmail",
									XMLType.REQUEST);


							actualValues.put("PHONE_ID", email_ID);
							actualValues.put("PHONE_NUMBER",emailID);

							// database records and request records are being
							// compared

							WSAssert.assertEquals( actualValues,db, false);

						}
						if (WSAssert.assertIfElementExists(updateEmailResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateEmailResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}

						if (WSAssert.assertIfElementExists(updateEmailResponseXML,
								"UpdateEmailResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated on
							 * the response
							 ****/

							String message = WSAssert.getElementValue(updateEmailResponseXML,
									"UpdateEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(updateEmailResponseXML,
								"UpdateEmailResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated on
							 * the response
							 ****/

							String message = WSAssert.getElementValue(updateEmailResponseXML,
									"UpdateEmailResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"The GDSerror displayed in the response is :" + message);
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Type Not Available**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author psarawag Description: Method to Verify that two Email addresses are
	 *         present where one is primary and the other is non-primary and if
	 *         the non-primary email is updated to primary than the primary
	 *         email should be updated to non-primary when passing
	 *         mandatory fields in the Update Email Request.
	 */

	@Test(groups = { "minimumRegression","Name", "UpdateEmail", "OWS" ,"updateEmail_38380"})

	public void updateEmail_38380() {
		try {
			String testName = "updateEmail_38380";
			WSClient.startTest(testName,
					"Verify that primary flag is successfully updated",
					"minimumRegression");
			String preReq[] = { "CommunicationMethod","CommunicationType" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {
				String resort = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String expectedPhoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				String email1 = (fname + "@oracle.com").toLowerCase();
				WSClient.setData("{var_email}", email1);
				String email2 = (fname + "." + lname + "@oracle.com").toLowerCase();
				WSClient.setData("{var_email2}", email2);
				WSClient.setData("{var_primary}", "true");
				WSClient.setData("{var_primary2}", "false");
				WSClient.setData("{var_emailType2}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
				WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_04"));
				WSClient.setData("{var_phoneRole}", expectedPhoneRole.toUpperCase());

				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_50");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				WSClient.setData("{var_profileID}", profileID);
				if (!profileID.equals("error")) {
					String query1=WSClient.getQuery("QS_06");
					List<LinkedHashMap<String, String>> phn = WSClient.getDBRows(query1);
					if(phn.size()>0){
						String phoneid_N = "";
						String phoneid_Y = "";
						for (int i = 0; i < 2; i++) {
							if (phn.get(i).get("PRIMARY_YN").equals("N")) {
								phoneid_N = phn.get(i).get("PHONE_ID");
								WSClient.setData("{var_phoneID}", phoneid_N);
							} else {
								phoneid_Y = phn.get(i).get("PHONE_ID");
							}
						}
						if (!(phoneid_N.equals("")) && !(phoneid_Y.equals(""))) {

							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

							// Validation request being created and processed to
							// generate response
							String updateEmailReq = WSClient.createSOAPMessage("OWSUpdateEmail", "DS_02");
							String updateEmailResponseXML = WSClient.processSOAPMessage(updateEmailReq);
							if (WSAssert.assertIfElementValueEquals(updateEmailResponseXML,
									"UpdateEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								// database records are being stored in a list of
								// hashmaps
								String query2=WSClient.getQuery("QS_02");
								List<LinkedHashMap<String, String>> db = WSClient.getDBRows(query2);

								List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

								// request records are being stored in different
								// variables
								String email_ID = WSClient.getElementValue(updateEmailReq,
										"UpdateEmailRequest_NameEmail_operaId", XMLType.REQUEST);
								String emailID = WSClient.getElementValue(updateEmailReq, "UpdateEmailRequest_NameEmail",
										XMLType.REQUEST);
								String primary_yn = WSClient.getElementValue(updateEmailReq,
										"UpdateEmailRequest_NameEmail_primary", XMLType.REQUEST);

								LinkedHashMap<String, String> val1 = new LinkedHashMap<String, String>();
								val1.put("Email_ID", email_ID);
								val1.put("Email", emailID);
								val1.put("Primary", primary_yn);
								actualValues.add(val1);
								LinkedHashMap<String, String> val2 = new LinkedHashMap<String, String>();
								val2.put("Email_ID", phoneid_Y);
								val2.put("Email", email1);
								val2.put("Primary", "false");
								actualValues.add(val2);

								// database records and response records are being
								// compared
								WSAssert.assertEquals(db, actualValues, false);
							}
							if (WSAssert.assertIfElementExists(updateEmailResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateEmailResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}

							if (WSAssert.assertIfElementExists(updateEmailResponseXML,
									"UpdateEmailResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is populated on
								 * the response
								 ****/

								String message = WSAssert.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"The OperaErrorCode displayed in the  response is :" + message);
							}
							if (WSAssert.assertIfElementExists(updateEmailResponseXML,
									"UpdateEmailResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is populated on
								 * the response
								 ****/

								String message = WSAssert.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"The GDSerror displayed in the response is :" + message);
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites for having two email address associated with a profile where one is primary and the other is non-primary failed!  -----Blocked");
						}
					}else{
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for having two email address attached to the profile Failed!  -----Blocked");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Type and Communication Method Not Available**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author psarawag Description: Method to Verify that the response shows
	 *         error when email address not containing @ is passed","minimum
	 *         regression.
	 */
	@Test(groups = { "minimumRegression","Name" ,"UpdateEmail", "OWS","in-QA" })
	public void updateEmail_38382() {
		try {
			String testName = "updateEmail_38382";
			WSClient.startTest(testName,
					"Verify that the response shows error when email address not containing @ is passed",
					"minimumRegression");
			String preReq[] = { "CommunicationMethod" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {
				String resort = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String expectedPhoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_email}", (fname + "@oracle.com").toLowerCase());
				WSClient.setData("{var_primary}", "TRUE");
				WSClient.setData("{var_phoneRole}", expectedPhoneRole.toUpperCase());

				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_13");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String email = (fname + "." + lname + "oracle.com").toLowerCase();
					WSClient.setData("{var_email}", email);
					String query1=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query1);

					if(phn.size()>0){
						String phoneid = phn.get("PHONE_ID");
						WSClient.setData("{var_phoneID}", phoneid);

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						// Validation request being created and processed to
						// generate response
						String updateEmailReq = WSClient.createSOAPMessage("OWSUpdateEmail", "DS_01");
						String updateEmailResponseXML = WSClient.processSOAPMessage(updateEmailReq);
						if (WSAssert.assertIfElementValueEquals(updateEmailResponseXML,
								"UpdateEmailResponse_Result_resultStatusFlag", "FAIL", false)) {

							// error codes are being checked
							if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_OperaErrorCode", true)){
								String operaErrorCode = WSClient.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Opera Code error appears on the response : " + operaErrorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_GDSError", true)){
								String GDSError=WSClient.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"GDSError appears on the response : " + GDSError);
							}
							if (WSAssert.assertIfElementExists(updateEmailResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateEmailResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}
						}

					}else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Method Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author psarawag Description: Method to Verify that the response shows
	 *         error when invalid operaID is passed","minimumRegression.
	 */
	@Test(groups = { "minimumRegression","Name" ,"UpdateEmail", "OWS" ,"in-QA"})
	public void updateEmail_38387() {
		try {
			String testName = "updateEmail_38387";
			WSClient.startTest(testName, "Verify that the response shows error when invalid operaID is passed",
					"minimumRegression");
			String preReq[] = { "CommunicationMethod" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {

				String email = "abc.xyz@oracle.com";
				WSClient.setData("{var_email}", email);
				WSClient.setData("{var_phoneID}", "");
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_owsresort}", owsresort);
				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

				// Validation request being created and processed to generate
				// response
				String updateEmailReq = WSClient.createSOAPMessage("OWSUpdateEmail", "DS_01");
				String updateEmailResponseXML = WSClient.processSOAPMessage(updateEmailReq);
				if (WSAssert.assertIfElementValueEquals(updateEmailResponseXML,
						"UpdateEmailResponse_Result_resultStatusFlag", "FAIL", false)) {

					// error codes are being checked
					if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_OperaErrorCode", true)){
						String operaErrorCode = WSClient.getElementValue(updateEmailResponseXML,
								"UpdateEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Opera Code error appears on the response : " + operaErrorCode+"</b>");
					}
					if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_GDSError", true)){
						String GDSError=WSClient.getElementValue(updateEmailResponseXML,
								"UpdateEmailResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"GDSError appears on the response : " + GDSError);
					}
					if (WSAssert.assertIfElementExists(updateEmailResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateEmailResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message+"</b>");
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Method Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	/**
	 * @author psarawag Description: Method to Verify that the response shows
	 *         error when email type is
	 *         passed as phone","minimumRegression.
	 */
	@Test(groups = { "minimumRegression","Name" ,"UpdateEmail", "OWS" ,"in-QA"})
	public void updateEmail_39140() {
		try {
			String testName = "updateEmail_39140";
			WSClient.startTest(testName,
					"Verify that the response shows error when invalid email type is passed in the request",
					"minimumRegression");
			String preReq[] = { "CommunicationMethod","CommunicationType" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				String expectedPhoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_email}", (fname + "@oracle.com").toLowerCase());
				WSClient.setData("{var_primary}", "TRUE");
				WSClient.setData("{var_phoneRole}", expectedPhoneRole.toUpperCase());

				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_13");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query1=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query1);

					if(phn.size()>0){
						String phoneid = phn.get("PHONE_ID");
						String email = (fname + "." + lname + "@oraclecom").toLowerCase();
						WSClient.setData("{var_email}", email);
						WSClient.setData("{var_phoneID}", phoneid);
						WSClient.setData("{var_emailType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						// Validation request being created and processed to
						// generate response
						String updateEmailReq = WSClient.createSOAPMessage("OWSUpdateEmail", "DS_03");
						String updateEmailResponseXML = WSClient.processSOAPMessage(updateEmailReq);
						if (WSAssert.assertIfElementValueEquals(updateEmailResponseXML,
								"UpdateEmailResponse_Result_resultStatusFlag", "FAIL", false)) {

							// error codes are being checked
							if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_OperaErrorCode", true)){
								String operaErrorCode = WSClient.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Opera Code error appears on the response : " + operaErrorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_GDSError", true)){
								String GDSError=WSClient.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"GDSError appears on the response : " + GDSError);
							}
							if (WSAssert.assertIfElementExists(updateEmailResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateEmailResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Method and Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	/**
	 * @author psarawag Description: Method to Verify that the response shows
	 *         error when phone is being updated","minimumRegression.
	 */
	@Test(groups = { "minimumRegression","Name" ,"UpdateEmail", "OWS"})
	public void updateEmail_391401() {
		try {
			String testName = "updateEmail_391401";
			WSClient.startTest(testName,
					"Verify that the response shows error when phonenumber is being updated to email address",
					"minimumRegression");
			String preReq[] = { "CommunicationMethod","CommunicationType" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_21");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query1=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query1);

					if(phn.size()>0){
						String phoneid = phn.get("PHONE_ID");
						String email = (fname + "." + lname + "@oraclecom").toLowerCase();
						WSClient.setData("{var_email}", email);
						WSClient.setData("{var_phoneID}", phoneid);

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						// Validation request being created and processed to
						// generate response
						String updateEmailReq = WSClient.createSOAPMessage("OWSUpdateEmail", "DS_01");
						String updateEmailResponseXML = WSClient.processSOAPMessage(updateEmailReq);
						if (WSAssert.assertIfElementValueEquals(updateEmailResponseXML,
								"UpdateEmailResponse_Result_resultStatusFlag", "FAIL", false)) {

							// error codes are being checked
							if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_OperaErrorCode", true)){
								String operaErrorCode = WSClient.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Opera Code error appears on the response : " + operaErrorCode+"</b>");
							}
							if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_GDSError", true)){
								String GDSError=WSClient.getElementValue(updateEmailResponseXML,
										"UpdateEmailResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"GDSError appears on the response : " + GDSError);
							}
							if (WSAssert.assertIfElementExists(updateEmailResponseXML, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(updateEmailResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Method and Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	/**
	 * @author psarawag Description: Method to Verify that the phone details is getting updated to email details","minimumRegression.
	 */
	@Test(groups = { "minimumRegression","Name" ,"UpdateEmail", "OWS"})
	public void updateEmail_391402() {
		try {
			String testName = "updateEmail_391402";
			WSClient.startTest(testName,
					"Verify that the phone details is getting updated to email details",
					"minimumRegression");
			String preReq[] = { "CommunicationMethod","CommunicationType" };
			if (OperaPropConfig.getPropertyConfigResults(preReq)) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname=WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname=WSClient.getKeywordData("{KEYWORD_LNAME}");
				String expectedPhoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				// Prerequisite 1: Create Profile 1
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_21");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query1=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query1);

					if(phn.size()>0){
						String phoneid = phn.get("PHONE_ID");
						String email = (fname + "." + lname + "@oraclecom").toLowerCase();
						WSClient.setData("{var_email}", email);
						WSClient.setData("{var_phoneID}", phoneid);
						WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
						WSClient.setData("{var_phoneRole}", expectedPhoneRole.toUpperCase());
						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						// Validation request being created and processed to
						// generate response
						String updateEmailReq = WSClient.createSOAPMessage("OWSUpdateEmail", "DS_03");
						String updateEmailResponseXML = WSClient.processSOAPMessage(updateEmailReq);
						if (WSAssert.assertIfElementValueEquals(updateEmailResponseXML,
								"UpdateEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// database records are being stored in a linked hashmap
							String query2=WSClient.getQuery("QS_03");
							LinkedHashMap<String, String> db = WSClient.getDBRow(query2);

							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();

							// request records are being stored in different
							// variables
							String email_ID = WSClient.getElementValue(updateEmailReq,
									"UpdateEmailRequest_NameEmail_operaId", XMLType.REQUEST);
							String emailID = WSClient.getElementValue(updateEmailReq, "UpdateEmailRequest_NameEmail",
									XMLType.REQUEST);
							String emailtype= WSClient.getElementValue(updateEmailReq, "UpdateEmailRequest_NameEmail_emailType", XMLType.REQUEST);

							actualValues.put("PHONE_ID", email_ID);
							actualValues.put("PHONE_NUMBER",emailID);
							actualValues.put("PHONE_TYPE", emailtype);

							WSAssert.assertEquals(actualValues, db, false);
						}
						// error codes are being checked
						if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_OperaErrorCode", true)){
							String operaErrorCode = WSClient.getElementValue(updateEmailResponseXML,
									"UpdateEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Opera Code error appears on the response : " + operaErrorCode+"</b>");
						}
						if(WSAssert.assertIfElementExists(updateEmailResponseXML, "UpdateEmailResponse_Result_GDSError", true)){
							String GDSError=WSClient.getElementValue(updateEmailResponseXML,
									"UpdateEmailResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"GDSError appears on the response : " + GDSError);
						}
						if (WSAssert.assertIfElementExists(updateEmailResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(updateEmailResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

					}else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Communication Method and Communication Type Not Available**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
}
