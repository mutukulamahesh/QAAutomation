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

public class FetchCommentList extends WSSetUp {
	String operaProfileID = null;


	@Test(groups = { "sanity", "FetchCommentList", "OWS", "Name", "in-QA" })
	/*****
	 * Verify that the comment that is attached to a given profile is retrieved
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 *
	 *****/
	public void FetchCommentList_38270() {
		try {

			String testName = "fetchCommentList_38270";
			WSClient.startTest(testName,
					"Verify that the comment that is attached to a given profile is retrieved",
					"sanity");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite: Create Profile With comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_10");
			if(!operaProfileID.equals("error")){

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));

				// ******** OWS FetchCommentList ***************//

				String FetchCommentListReq = WSClient.createSOAPMessage("OWSFetchCommentList", "DS_01");
				String FetchCommentListRes = WSClient.processSOAPMessage(FetchCommentListReq);
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_resultStatusFlag",
						false)) {

					if (WSAssert.assertIfElementValueEquals(FetchCommentListRes,
							"FetchCommentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						if (WSAssert.assertIfElementExists(FetchCommentListRes,
								"FetchCommentListResponse_CommentList_Comment_operaId", false)) {

							// ********* Validation of the response against the database *********//

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							String query = WSClient.getQuery("QS_01");
							db = WSClient.getDBRow(query);

							String notes = WSClient.getElementValue(FetchCommentListRes, "Comment_Text_TextElement",
									XMLType.RESPONSE);
							String note_id = WSClient.getElementValue(FetchCommentListRes,
									"FetchCommentListResponse_CommentList_Comment_operaId", XMLType.RESPONSE);

							if (WSAssert.assertEquals(note_id, db.get("NOTE_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "Comment ID -> 	Expected :	" + db.get("NOTE_ID")
								+ "	       ,          Actual :	 	 " + note_id);

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Comment ID -> Expected :	 " + db.get("NOTE_ID")
								+ "		 ,          Actual : 		 " + note_id);

							}
							if (WSAssert.assertEquals(notes, db.get("NOTES"), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Note Text -> 	Expected :	" + db.get("NOTES") + "		 ,          Actual :	 " + notes);

							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Note Text ->	 Expected :  " + db.get("NOTES") + " 		,          Actual : 	 " + notes);

							}

						}
					}
				}

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "Result_Text_TextElement", true)) {

					/**** Verifying that the error message is populated on the response ********/

					String message = WSAssert.getElementValue(FetchCommentListRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The error populated in the response is :<b>" + message+"</b>");
				}

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes,
							"FetchCommentListResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The error populated in the response is :<b>" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_OperaErrorCode",
						true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes,
							"FetchCommentListResponse_Result_OperaErrorCode", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO, "The error populated in the response is :<b>" + message+"</b>");
				}

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_faultcode", true)) {

					/**** Verifying that the error message is populated on the response ********/

					String message = WSClient.getElementValue(FetchCommentListRes, "FetchCommentListResponse_faultstring",
							XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO, "The error populated in the response is :<b>" + message+"</b>");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchCommentList", "OWS", "Name", "in-QA" })

	/*****
	 * Verify if all the comments (multiple comments) attached to the given profile are retrieved.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 *
	 *****/
	public void fetchCommentList_38317() {
		try {

			String testName = "fetchCommentList_38317";
			WSClient.startTest(testName,
					"Verify that all the comments (multiple comments) attached to the given profile are retrieved",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite: Create Profile With comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_18");
			if(!operaProfileID.equals("error")){

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));

				// ******** OWS FetchCommentList ***************//

				String FetchCommentListReq = WSClient.createSOAPMessage("OWSFetchCommentList", "DS_01");
				String FetchCommentListRes = WSClient.processSOAPMessage(FetchCommentListReq);

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_resultStatusFlag",
						false)) {
					if (WSAssert.assertIfElementValueEquals(FetchCommentListRes,
							"FetchCommentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						if (WSAssert.assertIfElementExists(FetchCommentListRes,
								"FetchCommentListResponse_CommentList_Comment_operaId", false)) {

							// ********* Validation of the response against the database *********//

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							String query = WSClient.getQuery("QS_02");
							db = WSClient.getDBRows(query);

							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							List<LinkedHashMap<String, String>> service;

							xPath.put("FetchCommentListResponse_CommentList_Comment_operaId",
									"FetchCommentListResponse_CommentList_Comment");
							xPath.put("FetchCommentListResponse_CommentList_Comment_commentType",
									"FetchCommentListResponse_CommentList_Comment");
							xPath.put("Comment_Text_TextElement", "FetchCommentListResponse_CommentList_Comment");

							service = WSClient.getMultipleNodeList(FetchCommentListRes, xPath, false, XMLType.RESPONSE);
							if (WSAssert.assertEquals(db, service, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"The multiple comments values are correctly fetched!");
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"The multiple comment values are not correctly fetched!");
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"The comments attached to the profile are not populated! ");
					}
				}

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "Result_Text_TextElement", true)) {

					/**** Verifying that the error message is populated on the response ********/

					String message = WSAssert.getElementValue(FetchCommentListRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					if(message.equals("SYSTEM ERROR" ) || message!="")
						WSClient.writeToReport(LogStatus.INFO, "Opera Profile ID is not sent on the response");
					else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}



				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError",
							XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_OperaErrorCode",
						true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes,
							"FetchCommentListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_faultcode", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSClient.getElementValue(FetchCommentListRes, "FetchCommentListResponse_faultstring",
							XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchCommentList", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that comment(s) are retrieved when request is submitted providing values for all mandatory/optional fields
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 *
	 *****/
	public void fetchCommentList_38312() {
		try {

			String testName = "fetchCommentList_38312";
			WSClient.startTest(testName,
					"Verify that all the fields related to comments are populated correctly on the fetchCommentList response. ",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));

			// *********Prerequisite: Create Profile With comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_10");
			if(!operaProfileID.equals("error")){

				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

				// ******** OWS FetchCommentList ***************//

				String FetchCommentListReq = WSClient.createSOAPMessage("OWSFetchCommentList", "DS_01");
				String FetchCommentListRes = WSClient.processSOAPMessage(FetchCommentListReq);

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_resultStatusFlag",
						false)) {
					if (WSAssert.assertIfElementValueEquals(FetchCommentListRes,
							"FetchCommentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						if (WSAssert.assertIfElementExists(FetchCommentListRes,
								"FetchCommentListResponse_CommentList_Comment_operaId", false)) {

							String commentId = WSClient.getElementValue(FetchCommentListRes,
									"FetchCommentListResponse_CommentList_Comment_operaId", XMLType.RESPONSE);
							String notes = WSClient.getElementValue(FetchCommentListRes, "CommentList_Comment_Text",
									XMLType.RESPONSE);
							String noteTitle = WSClient.getElementValue(FetchCommentListRes,
									"FetchCommentListResponse_CommentList_Comment_commentTitle", XMLType.RESPONSE);
							String noteType = WSClient.getElementValue(FetchCommentListRes,
									"FetchCommentListResponse_CommentList_Comment_commentType", XMLType.RESPONSE);
							String internalyn = WSClient.getElementValue(FetchCommentListRes,
									"FetchCommentListResponse_CommentList_Comment_internalYn", XMLType.RESPONSE);

							// ********* Validation of the response against the database *********//

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							String query = WSClient.getQuery("QS_03");
							db = WSClient.getDBRow(query);
							if (WSAssert.assertEquals(db.get("NOTE_CODE"), noteType, true)) {

								WSClient.writeToReport(LogStatus.PASS, "Comment Type ->  		Expected :	"
										+ db.get("NOTE_CODE") + "		 ,          Actual :	 	 " + noteType);

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Comment Type -> 		Expected :	"
										+ db.get("NOTE_CODE") + "		 ,          Actual : 		 " + noteType);

							}
							if (WSAssert.assertEquals(db.get("NOTE_ID"), commentId, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Comment ID -> 		Expected :	"
										+ db.get("NOTE_ID") + "		 ,          Actual :	 	 " + commentId);

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Comment ID -> 		Expected :	"
										+ db.get("NOTE_ID") + "		 ,          Actual : 		 " + commentId);

							}
							if (WSAssert.assertEquals(db.get("NOTES"), notes, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Note Text -> 		Expected :		" + db.get("NOTES")
								+ "		 ,          Actual :	 " + notes);

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Note Text ->		 Expected : 		"
										+ db.get("NOTES") + " 		,          Actual : 	 " + notes);

							}

							if (WSAssert.assertEquals(db.get("INTERNAL_YN"), internalyn, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Internal Y/N -> 	Expected :	" + db.get("INTERNAL_YN")
								+ "		,          Actual :		  " + internalyn);

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Internal Y/N -> 	Expected :" + db.get("INTERNAL_YN")
								+ " 	,          Actual : 	 " + internalyn);

							}

							if (WSAssert.assertEquals(db.get("NOTE_TITLE"), noteTitle, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Note Title ->	 Expected :		"
										+ db.get("NOTE_TITLE") + " 	,          Actual : 	 " + noteTitle);

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Note Title ->	Expected :		"
										+ db.get("NOTE_TITLE") + "		,          Actual :	 " + noteTitle);

							}


						}

					}
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "Result_Text_TextElement", true)) {

					/**** Verifying that the error message is populated on the response ********/

					String message = WSAssert.getElementValue(FetchCommentListRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					if(message.equals("SYSTEM ERROR" ) || message!="")
						WSClient.writeToReport(LogStatus.INFO, "Opera Profile ID is not sent on the response");
					else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}



				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError",
							XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_OperaErrorCode",
						true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes,
							"FetchCommentListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_faultcode", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSClient.getElementValue(FetchCommentListRes, "FetchCommentListResponse_faultstring",
							XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchCommentList", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that an error message is populated on the response when INVALID PROFILE ID is passed
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 *
	 *****/
	public void fetchCommentList_38286() {
		try {

			String testName = "fetchCommentList_38296";
			WSClient.startTest(testName,
					"Verify that an error message is populated on the response when INVALID PROFILE ID is passed",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));


			WSClient.setData("{var_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));
			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));

			// ******** OWS FetchCommentList ***************//

			String FetchCommentListReq = WSClient.createSOAPMessage("OWSFetchCommentList", "DS_01");
			String FetchCommentListRes = WSClient.processSOAPMessage(FetchCommentListReq);

			if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_resultStatusFlag",
					false)) {

				if (WSAssert.assertIfElementValueEquals(FetchCommentListRes,
						"FetchCommentListResponse_Result_resultStatusFlag", "FAIL", false)) {

				} else
					WSClient.writeToReport(LogStatus.FAIL, " Fetch comment is Successful  ");
			}
			if (WSAssert.assertIfElementExists(FetchCommentListRes, "Result_Text_TextElement", true)) {

				/**** Verifying that the error message is populated on the response ********/

				String message = WSAssert.getElementValue(FetchCommentListRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if(message.equals("SYSTEM ERROR" ) || message!="")
					WSClient.writeToReport(LogStatus.INFO, "Opera Profile ID is not sent on the response");
				else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message+"</b>");
			}



			if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message.equals("")) {

				}else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_OperaErrorCode",
					true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(FetchCommentListRes,
						"FetchCommentListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if(message.equals("")) {

				}else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_faultcode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSClient.getElementValue(FetchCommentListRes, "FetchCommentListResponse_faultstring",
						XMLType.RESPONSE);
				if(message.equals("")) {

				}else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message+"</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchCommentList", "OWS", "Name"})

	/*****
	 * Verify that comments of type OWS & Non-OWS (multiple comments) attached to the given profile are retrieved with only comment of type OWS
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 *
	 *****/
	public void fetchCommentList_190985() {
		try {

			String testName = "fetchCommentList_190985";
			WSClient.startTest(testName,
					"Verify that an error message is populated on the response when INVALID CHANNEL ID is passed",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = "INVALIDCHANNEL";

			// ********* Setting the OWS Header**************//
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), channel);

			// *********Prerequisite: Create Profile With comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_CreateProfile_50");
			if(!operaProfileID.equals("error")){

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));

				// ******** OWS FetchCommentList ***************//

				String FetchCommentListReq = WSClient.createSOAPMessage("OWSFetchCommentList", "DS_01");
				String FetchCommentListRes = WSClient.processSOAPMessage(FetchCommentListReq);

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_resultStatusFlag",
						true)) {

					if (WSAssert.assertIfElementValueEquals(FetchCommentListRes,
							"FetchCommentListResponse_Result_resultStatusFlag", "FAIL", false)) {

					} else
						WSClient.writeToReport(LogStatus.FAIL, " Fetch comment is Successful  ");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "Result_Text_TextElement", true)) {

					/**** Verifying that the error message is populated on the response ********/

					String message = WSAssert.getElementValue(FetchCommentListRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					if(message.equals("SYSTEM ERROR" ) || message!="")
						WSClient.writeToReport(LogStatus.INFO, "Opera Channel ID is not sent on the response");
					else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchCommentList", "OWS", "Name"})

	/*****
	 * Verify that comments of type OWS & Non-OWS (multiple comments) attached to the given profile are retrieved with only comment of type OWS
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 *
	 *****/
	public void fetchCommentList_908678() {
		try {

			String testName = "fetchCommentList_908678";
			WSClient.startTest(testName,
					"Verify that comments of type OWS & Non-OWS (multiple comments) attached to the given profile are retrieved with only comment of type OWS",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite: Create Profile With comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_CreateProfile_50");
			if(!operaProfileID.equals("error")){

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));

				// ******** OWS FetchCommentList ***************//

				String FetchCommentListReq = WSClient.createSOAPMessage("OWSFetchCommentList", "DS_01");
				String FetchCommentListRes = WSClient.processSOAPMessage(FetchCommentListReq);

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_resultStatusFlag",
						true)) {
					if (WSAssert.assertIfElementValueEquals(FetchCommentListRes,
							"FetchCommentListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						if (WSAssert.assertIfElementExists(FetchCommentListRes,
								"FetchCommentListResponse_CommentList_Comment_operaId", false)) {

							// ********* Validation of the response against the database *********//

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							String query = WSClient.getQuery("QS_04");
							db = WSClient.getDBRows(query);

							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							List<LinkedHashMap<String, String>> service;

							xPath.put("FetchCommentListResponse_CommentList_Comment_operaId",
									"FetchCommentListResponse_CommentList_Comment");
							xPath.put("FetchCommentListResponse_CommentList_Comment_commentType",
									"FetchCommentListResponse_CommentList_Comment");
							xPath.put("Comment_Text_TextElement", "FetchCommentListResponse_CommentList_Comment");

							service = WSClient.getMultipleNodeList(FetchCommentListRes, xPath, false, XMLType.RESPONSE);
							if (WSAssert.assertEquals(db, service, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"The multiple comments values are correctly fetched!");
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"The multiple comment values are not correctly fetched!");
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"The comments attached to the profile are not populated! ");
					}
				}

				if (WSAssert.assertIfElementExists(FetchCommentListRes, "Result_Text_TextElement", true)) {

					/**** Verifying that the error message is populated on the response ********/

					String message = WSAssert.getElementValue(FetchCommentListRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					if(message.equals("SYSTEM ERROR" ) || message!="")
						WSClient.writeToReport(LogStatus.INFO, "Opera Profile ID is not sent on the response");
					else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}



				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes, "FetchCommentListResponse_Result_GDSError",
							XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_Result_OperaErrorCode",
						true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(FetchCommentListRes,
							"FetchCommentListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
				if (WSAssert.assertIfElementExists(FetchCommentListRes, "FetchCommentListResponse_faultcode", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSClient.getElementValue(FetchCommentListRes, "FetchCommentListResponse_faultstring",
							XMLType.RESPONSE);
					if(message.equals("")) {

					}else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message+"</b>");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
}
