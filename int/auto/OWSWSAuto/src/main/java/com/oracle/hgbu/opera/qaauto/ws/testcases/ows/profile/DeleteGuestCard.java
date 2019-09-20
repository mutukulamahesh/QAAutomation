package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.HashMap;
import java.util.LinkedHashMap;

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

public class DeleteGuestCard extends WSSetUp {

	/**
	 * @author psarawag
	 */

	@Test(groups = { "sanity", "DeleteGuestCard", "Name", "OWS", "deleteGuestCard_38690"})
	public void deleteGuestCard_38690() {
		try {
			String testName = "deleteGuestCard_38690";
			WSClient.startTest(testName, "Verify that the membership is deleted", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				//Setting variables with the data
				String resort = OPERALib.getResort();
				String chain =OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);

				//Prerequisite 1:Create Profile
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_06");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query;
					String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}", member_num);
					String memName = (fname + "_" + lname)
							.toUpperCase();

					WSClient.setData("{var_nameOnCard}", memName);

					WSClient.setData("{var_membershipType}",
							OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_membershipLevel}",
							OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

					//Prerequisite 2:Attach membership to the Profile
					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {


						query = WSClient.getQuery("OWSDeleteGuestCard", "QS_01");
						HashMap<String,String> mem=WSClient.getDBRow(query);
						if (mem.size()>0) {
							String memId = mem.get("MEMBERSHIP_ID");
							WSClient.writeToReport(LogStatus.INFO, "<b>Membership ID:- " + memId+"</b>");
							WSClient.setData("{var_membershipId}", memId);

							//OWS DeleteGuestCard request and response
							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

							String DeleteGuestCardReq = WSClient.createSOAPMessage("OWSDeleteGuestCard", "DS_01");
							String DeleteGuestCardRes = WSClient.processSOAPMessage(DeleteGuestCardReq);

							if (WSAssert.assertIfElementValueEquals(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								//DB Validation
								String query1 = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query1);
								if (db.size() == 0)
									WSClient.writeToReport(LogStatus.PASS, "Deleted Guest Card");
								else
									WSClient.writeToReport(LogStatus.FAIL, "Error in deleting Guest Card");
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(DeleteGuestCardRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}

							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The OperaErrorCode displayed in the  response is :" + message);
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is :" + message);
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: " + message);
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any membership attached");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Membership fails");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Membership Type and Membership Level not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "DeleteGuestCard", "Name", "OWS" })
	public void deleteGuestCard_38691() {
		try {
			String testName = "deleteGuestCard_38691";
			WSClient.startTest(testName, "Verify the error message in the response when deleted membership is again deleted",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				//Setting variables with the data
				String resort = OPERALib.getResort();
				String chain =OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);

				//Prerequisite 1:Create Profile
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_06");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query;
					String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}", member_num);
					String memName = (fname + "_" + lname)
							.toUpperCase();

					WSClient.setData("{var_nameOnCard}", memName);

					WSClient.setData("{var_membershipType}",
							OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_membershipLevel}",
							OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

					//Prerequisite 2:Attach membership to the Profile
					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {



						query = WSClient.getQuery("OWSDeleteGuestCard", "QS_01");

						HashMap<String,String> mem=WSClient.getDBRow(query);
						if (mem.size()>0) {
							String memId = mem.get("MEMBERSHIP_ID");
							WSClient.writeToReport(LogStatus.INFO, "<b>Membership ID:- " + memId+"</b>");
							WSClient.setData("{var_membershipId}", memId);

							//Prerequisite 3:Delete Membership from Profile
							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
							String DeleteGuestCardReq = WSClient.createSOAPMessage("OWSDeleteGuestCard", "DS_01");
							String DeleteGuestCardRes = WSClient.processSOAPMessage(DeleteGuestCardReq);

							if (WSAssert.assertIfElementValueEquals(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {


								String query1 = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query1);
								if (db.size() == 0) {

									//OWS DeleteGuestCard request and response
									DeleteGuestCardReq = WSClient.createSOAPMessage("OWSDeleteGuestCard", "DS_01");
									DeleteGuestCardRes = WSClient.processSOAPMessage(DeleteGuestCardReq);

									//Response Validation
									WSAssert.assertIfElementValueEquals(DeleteGuestCardRes,
											"DeleteGuestCardResponse_Result_resultStatusFlag", "FAIL", false);

								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->Unable to delete Membership");
								}

							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(DeleteGuestCardRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is :" + message);
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: " + message);
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any membership attached");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Membership fails");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Membership Type and Membership Level not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "DeleteGuestCard", "Name", "OWS" })
	public void deleteGuestCard_38692() {
		try {
			String testName = "deleteGuestCard_38692";
			WSClient.startTest(testName, "Verify the error message in the response when Membership ID type is External",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				//Setting variables with the data
				String resort = OPERALib.getResort();
				String chain =OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);

				//Prerequisite 1: Create Profile
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_06");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query;
					String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}", member_num);
					String memName = (fname + "_" + lname)
							.toUpperCase();

					WSClient.setData("{var_nameOnCard}", memName);

					WSClient.setData("{var_membershipType}",
							OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_membershipLevel}",
							OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

					//Prerequisite 2: Attach Membership to the Profile
					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {

						query = WSClient.getQuery("OWSDeleteGuestCard", "QS_01");
						HashMap<String,String> mem=WSClient.getDBRow(query);
						if (mem.size()>0) {
							String memId = mem.get("MEMBERSHIP_ID");
							WSClient.writeToReport(LogStatus.INFO, "<b>Membership ID:- " + memId+"</b>");
							WSClient.setData("{var_membershipId}", memId);

							//OWS DeleteGuestCard request and response
							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

							String DeleteGuestCardReq = WSClient.createSOAPMessage("OWSDeleteGuestCard", "DS_02");
							String DeleteGuestCardRes = WSClient.processSOAPMessage(DeleteGuestCardReq);

							//Response Validation
							WSAssert.assertIfElementValueEquals(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_resultStatusFlag", "FAIL", false);

							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(DeleteGuestCardRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is :" + message);
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: " + message);
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any membership attached");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Membership fails");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Membership Type and Membership Level not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "DeleteGuestCard", "Name", "OWS" })
	public void deleteGuestCard_38693() {
		try {
			String testName = "deleteGuestCard_38693";
			WSClient.startTest(testName, "Verify the error message in the response when No membership ID is passed", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				//Setting variables with data
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_owsresort}", owsresort);
				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

				//OWS DeleteGuestCard request and response
				String DeleteGuestCardReq = WSClient.createSOAPMessage("OWSDeleteGuestCard", "DS_03");
				String DeleteGuestCardRes = WSClient.processSOAPMessage(DeleteGuestCardReq);

				//Response Validation
				WSAssert.assertIfElementValueEquals(DeleteGuestCardRes,
						"DeleteGuestCardResponse_Result_resultStatusFlag", "FAIL", false);

				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(DeleteGuestCardRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
				}

				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_Result_OperaErrorCode",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(DeleteGuestCardRes,
							"DeleteGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_Result_GDSError",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(DeleteGuestCardRes,
							"DeleteGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}
				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_faultcode", true)) {
					String message = WSClient.getElementValue(DeleteGuestCardRes, "DeleteGuestCardResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL, "Fault Schema in Response with message: " + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Membership Type and Membership Level not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "DeleteGuestCard", "Name", "OWS" })
	public void deleteGuestCard_38694() {
		try {
			String testName = "deleteGuestCard_38694";
			WSClient.startTest(testName, "Verify the error message in the response when membership ID is passed as non_numeric",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				//Setting variables with data
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_membershipId}", WSClient.getKeywordData("{KEYWORD_RANDSTR_6}"));

				//OWS DeleteGuestCard request and response
				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

				String DeleteGuestCardReq = WSClient.createSOAPMessage("OWSDeleteGuestCard", "DS_01");
				String DeleteGuestCardRes = WSClient.processSOAPMessage(DeleteGuestCardReq);

				//Response Validation
				WSAssert.assertIfElementValueEquals(DeleteGuestCardRes,
						"DeleteGuestCardResponse_Result_resultStatusFlag", "FAIL", false);

				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(DeleteGuestCardRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
				}

				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_Result_OperaErrorCode",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(DeleteGuestCardRes,
							"DeleteGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_Result_GDSError",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(DeleteGuestCardRes,
							"DeleteGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}
				if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_faultcode", true)) {
					String message = WSClient.getElementValue(DeleteGuestCardRes, "DeleteGuestCardResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL, "Fault Schema in Response with message: " + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Membership Type and Membership Level not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "DeleteGuestCard", "Name", "OWS" })
	public void deleteGuestCard_39300() {
		try {
			String testName = "deleteGuestCard_39300";
			WSClient.startTest(testName, "Verify that the inactive membership is deleted", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				//Setting variables with data
				String resort = OPERALib.getResort();
				String chain =OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);

				//Prerequisite 1:Create Profile
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_06");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID+"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileID}", profileID);
					String query;
					String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
					WSClient.setData("{var_memNo}", member_num);
					String memName = (fname + "_" + lname)
							.toUpperCase();

					WSClient.setData("{var_nameOnCard}", memName);

					WSClient.setData("{var_membershipType}",
							OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_membershipLevel}",
							OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
					WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
					WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

					//Prerequisite 2: Attach Membership to the Profile
					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_06");
					String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

					if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {


						query = WSClient.getQuery("OWSDeleteGuestCard", "QS_01");
						HashMap<String,String> mem=WSClient.getDBRow(query);
						if (mem.size()>0) {
							String memId = mem.get("MEMBERSHIP_ID");
							WSClient.writeToReport(LogStatus.INFO, "<b>Membership ID:- " + memId+"</b>");
							WSClient.setData("{var_membershipId}", memId);

							//OWS DeleteGuestCard request and response
							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

							String DeleteGuestCardReq = WSClient.createSOAPMessage("OWSDeleteGuestCard", "DS_01");
							String DeleteGuestCardRes = WSClient.processSOAPMessage(DeleteGuestCardReq);

							if (WSAssert.assertIfElementValueEquals(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								//DB Validation
								String query1 = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query1);
								if (db.size() == 0)
									WSClient.writeToReport(LogStatus.PASS, "Deleted Guest Card");
								else
									WSClient.writeToReport(LogStatus.FAIL, "Error in deleting Guest Card");
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(DeleteGuestCardRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}

							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The OperaErrorCode displayed in the  response is :" + message);
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes,
									"DeleteGuestCardResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is :" + message);
							}
							if (WSAssert.assertIfElementExists(DeleteGuestCardRes, "DeleteGuestCardResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(DeleteGuestCardRes,
										"DeleteGuestCardResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: " + message);
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any membership attached");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Membership fails");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Membership Type and Membership Level not available -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
}
