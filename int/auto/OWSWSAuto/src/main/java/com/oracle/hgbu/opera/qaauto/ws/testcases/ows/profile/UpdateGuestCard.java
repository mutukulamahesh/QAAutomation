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

public class UpdateGuestCard extends WSSetUp {

	String profileID = "";

	@Test(groups = { "sanity", "UpdateGuestCard", "Name", "OWS" })
	public void UpdateGuestCard_38703() {
		try {

			String testName = "updateGuestCard_38323";
			WSClient.startTest(testName, "Verify that the memberName,memberLevel,mebernumber is updated ", "sanity");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_06");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						OPERALib.setOperaHeader(uname);
						String query;
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", true)) {

							member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
							WSClient.setData("{var_updatedmemNo}", member_num);

							fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
							lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
							WSClient.setData("{var_fname}", fname);
							WSClient.setData("{var_lname}", lname);
							String updatememName = (WSClient.getData("{var_fname}") + "_"
									+ WSClient.getData("{var_lname}")).toUpperCase();
							WSClient.setData("{var_updatedNameOnCard}", updatememName);

							query = WSClient.getQuery("OWSUpdateGuestCard", "QS_01");

							String memId = WSClient.getDBRow(query).get("MEMBERSHIP_ID");

							WSClient.setData("{var_membershipId}", memId);
							WSClient.setData("{var_memLevel}",
									OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							String UpdateGuestCardReq = WSClient.createSOAPMessage("OWSUpdateGuestCard", "DS_01");
							String UpdateGuestCardRes = WSClient.processSOAPMessage(UpdateGuestCardReq);
							if (WSAssert.assertIfElementValueEquals(UpdateGuestCardRes,
									"UpdateGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("UpdateGuestCardRequest_NameMembership_operaId",
										"UpdateGuestCardRequest_NameMembership");
								xPath.put("UpdateGuestCardRequest_NameMembership_membershipType",
										"UpdateGuestCardRequest_NameMembership");
								xPath.put("UpdateGuestCardRequest_NameMembership_membershipLevel",
										"UpdateGuestCardRequest_NameMembership");
								xPath.put("UpdateGuestCardRequest_NameMembership_membershipNumber",
										"UpdateGuestCardRequest_NameMembership");
								xPath.put("UpdateGuestCardRequest_NameMembership_memberName",
										"UpdateGuestCardRequest_NameMembership");

								// xPath.put("CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID",
								// "CreateMembershipRS_ProfileMemberships_ProfileMembership");
								LinkedHashMap<String, String> actual = WSClient.getSingleNodeList(UpdateGuestCardReq,
										xPath, false, XMLType.REQUEST);
								// Values from DB Actual values

								query = WSClient.getQuery("QS_02");

								LinkedHashMap<String, String> expected = WSClient.getDBRow(query);

								// Verifying the values if both are equal
								WSAssert.assertEquals(expected, actual, false);

							}
						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Membership is not created");

						}
					}

					else {

						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "UpdateGuestCard", "Name", "OWS" })
	public void UpdateGuestCard_38723() {
		try {
			String testName = "updateGuestCard_38723";
			WSClient.startTest(testName,
					"Verify that the error message is present in the response when operaId is not passed in the request",
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
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				profileID = CreateProfile.createProfile("DS_06");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						String query;
						WSClient.setData("{var_profileId}", profileID);
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_memType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
							WSClient.setData("{var_memLevel}",
									OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
							String UpdateGuestCardReq = WSClient.createSOAPMessage("OWSUpdateGuestCard", "DS_04");
							String UpdateGuestCardRes = WSClient.processSOAPMessage(UpdateGuestCardReq);
							WSAssert.assertIfElementContains(UpdateGuestCardRes,
									"UpdateGuestCardResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(UpdateGuestCardRes, "Result_Text_TextElement", true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Error in response is:</b>" + WSClient.getElementValue(UpdateGuestCardRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> membership is not created");
						}
					} else {

						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "UpdateGuestCard", "Name", "OWS" })
	public void UpdateGuestCard_38727() {
		try {
			String testName = "updateGuestCard_38727";
			WSClient.startTest(testName,
					"Verify that the error message is present in the response when invalid operaId is  passed in the request",
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
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				profileID = CreateProfile.createProfile("DS_06");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						String query;
						WSClient.setData("{var_profileId}", profileID);
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);
						String member_Id = WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
						WSClient.setData("{var_InvalidmemId}", member_Id);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_memType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
							WSClient.setData("{var_memLevel}",
									OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
							String UpdateGuestCardReq = WSClient.createSOAPMessage("OWSUpdateGuestCard", "DS_02");
							String UpdateGuestCardRes = WSClient.processSOAPMessage(UpdateGuestCardReq);
							WSAssert.assertIfElementContains(UpdateGuestCardRes,
									"UpdateGuestCardResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(UpdateGuestCardRes, "Result_Text_TextElement", true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Error in response is:</b>" + WSClient.getElementValue(UpdateGuestCardRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

							}
							if (WSAssert.assertIfElementExists(UpdateGuestCardRes,
									"UpdateGuestCardResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Error in response is:</b>" + WSClient.getElementValue(UpdateGuestCardRes,
												"UpdateGuestCardResponse_Result_GDSError", XMLType.RESPONSE));
							}
							WSClient.writeToReport(LogStatus.INFO, "checking in the DB");

							String query1 = WSClient.getQuery("QS_05");
							if (Integer.parseInt(WSClient.getDBRow(query1).get("COUNT")) == 0) {
								WSClient.writeToReport(LogStatus.INFO,
										"membership details are not inserted in the database");
								WSClient.writeToReport(LogStatus.INFO, "<b>Expected: 0 </b>" + "<b>Actual :</b>"
										+ WSClient.getDBRow(query1).get("COUNT"));
							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> membership is not created");
						}
					} else {

						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "UpdateGuestCard", "Name", "OWS" })
	public void UpdateGuestCard_38726() {
		try {
			String testName = "updateGuestCard_38726";
			WSClient.startTest(testName,
					"Verify that the error message is present in the response when invalid membership-type is passed in the request ",
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
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				profileID = CreateProfile.createProfile("DS_06");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						String query;
						WSClient.setData("{var_profileId}", profileID);
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {

							query = WSClient.getQuery("OWSUpdateGuestCard", "QS_01");
							String memId = WSClient.getDBRow(query).get("MEMBERSHIP_ID");
							WSClient.setData("{var_membershipId}", memId);
							WSClient.setData("{var_membershipId}", memId);

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_memType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));

							String UpdateGuestCardReq = WSClient.createSOAPMessage("OWSUpdateGuestCard", "DS_03");
							String UpdateGuestCardRes = WSClient.processSOAPMessage(UpdateGuestCardReq);
							WSAssert.assertIfElementValueEquals(UpdateGuestCardRes,
									"UpdateGuestCardResponse_Result_resultStatusFlag", "FAIL", false);
							query = WSClient.getQuery("OWSUpdateGuestCard", "QS_04");
							String memType = WSClient.getDBRow(query).get("MEMBERSHIP_TYPE");

							if (WSAssert.assertEquals(OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"),
									memType, false)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>membership-type is not getting updated in database</b>");
							} else {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>membership-type is updated in the database</b>");
							}

							if (WSAssert.assertIfElementExists(UpdateGuestCardRes, "Result_Text_TextElement", true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Error in response is:</b>" + WSClient.getElementValue(UpdateGuestCardRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

							}
						} else {

							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >>membership is not created");
						}
					} else {

						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "UpdateGuestCard", "Name", "OWS" })
	public void UpdateGuestCard_38728() {
		try {
			String testName = "updateGuestCard_38728";
			WSClient.startTest(testName,
					"Verify that the error-message is present in the response when card length is greater than the limit is passed ",
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
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			/************
			 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
			 * Code
			 *********************************/
			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				profileID = CreateProfile.createProfile("DS_06");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						String query;
						WSClient.setData("{var_profileId}", profileID);
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_08}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_07"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_04"));
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_07"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_04"));
						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", false)) {

							query = WSClient.getQuery("OWSUpdateGuestCard", "QS_01");
							String memId = WSClient.getDBRow(query).get("MEMBERSHIP_ID");

							WSClient.setData("{var_membershipId}", memId);

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String member_num_new = "25681100394798202998";
							WSClient.setData("{var_memNo}", member_num_new);

							String UpdateGuestCardReq = WSClient.createSOAPMessage("OWSUpdateGuestCard", "DS_05");
							String UpdateGuestCardRes = WSClient.processSOAPMessage(UpdateGuestCardReq);

							WSAssert.assertIfElementValueEquals(UpdateGuestCardRes,
									"UpdateGuestCardResponse_Result_resultStatusFlag", "FAIL", false);

							String card_no = WSClient.getDBRow(WSClient.getQuery("QS_06")).get("MEMBERSHIP_CARD_NO");

							if (WSAssert.assertEquals(member_num, card_no, false)) {
								WSClient.writeToReport(LogStatus.PASS, "Card number didn't get updated in DB");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Card number get updated in DB");
							}
							if (WSAssert.assertIfElementExists(UpdateGuestCardRes, "Result_Text_TextElement", true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Error in response is:</b>" + WSClient.getElementValue(UpdateGuestCardRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

							}
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
				}
			} else {

				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> membership is not created");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

}
