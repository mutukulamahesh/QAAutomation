package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

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

public class UpdateComment extends WSSetUp {

	@Test(groups = { "sanity", "UpdateComment", "OWS", "Name", "in-QA" })
	/*****
	 * Verify that comment is updated in database when minimum required data in
	 * provided in request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 * 
	 *****/
	public void updateComment_38266() {
		try {

			String testName = "updateComment_38266";
			WSClient.startTest(testName, "Verify that comment is updated in database when minimum required data in provided in request.", "sanity");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Prerequisite :Create Profile with comments.
			// *************//

			String operaProfileID = CreateProfile.createProfile("DS_10");

			if (!operaProfileID.equals("error")) {

				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + operaProfileID + "</b>");
				String query1 = WSClient.getQuery("QS_05");
				LinkedHashMap<String, String> db1 = WSClient.getDBRow(query1);

				String commentId = db1.get("NOTE_ID");
				String noteText = db1.get("NOTES");
				if (commentId == null) {
					WSClient.writeToReport(LogStatus.FAIL, " ***** Blocked : The note Id is not added *****");
				} else {

					WSClient.writeToReport(LogStatus.INFO, "<b>Note ID :" + commentId + "</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Note Text Before Update :" + noteText + "</b>");

					WSClient.setData("{var_commentID}", commentId);
					WSClient.setData("{var_profileID}", operaProfileID);

					// ******** OWS UpdateComment*************//

					String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_01");
					String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);
					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> db = WSClient.getDBRow(query);

							String notes = WSClient.getElementValue(updateCommentReq, "Comment_Text_TextElement", XMLType.REQUEST);

							// ******** Validation of the update comment request
							// against the database
							// ************//

							if (WSAssert.assertEquals(operaProfileID, db.get("NAME_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "Name ID -> Expected : 	" + operaProfileID + "   ,	Actual :	  " + db.get("NAME_ID"));

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Name ID -> Expected :	" + operaProfileID + "  ,	Actual :	 	 " + db.get("NAME_ID"));

							}
							if (WSAssert.assertEquals(commentId, db.get("NOTE_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "Comment ID -> Expected :		" + commentId + "		  ,	Actual :	 	 	 " + db.get("NOTE_ID"));

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Comment ID -> Expected :		" + commentId + "		  ,	Actual :	  		 " + db.get("NOTE_ID"));

							}
							if (WSAssert.assertEquals(notes, db.get("NOTES"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "Note Text -> 	Expected :		" + notes + "		  ,	Actual :	 	 " + db.get("NOTES"));

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Note Text ->	 Expected : 		" + notes + " 		 ,	Actual :	  	 " + db.get("NOTES"));

							}

						} else {
							WSClient.writeToReport(LogStatus.FAIL, " Update comment was unsuccessful ");
						}
					}
					if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
						if (message.equals("*null*")) {

						} else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
						if (message.equals("*null*")) {

						} else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}
					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						if (message.equals("*null*")) {

						} else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}
					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
						if (message.equals("*null*")) {

						} else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateComment", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that all given required fields sent on the update comment are
	 * updated in the database for a configured channel.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 * 
	 *****/

	public void updateComment_42475() {
		try {

			String testName = "updateComment_42475";
			WSClient.startTest(testName, "Verify all the comment fields are updated in the database when all required fields are passed on the request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Setting the Opera Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");

			// *********Prerequisite: Create Profile With
			// comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_10");
			if (!operaProfileID.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);

				String query1 = WSClient.getQuery("QS_05");
				LinkedHashMap<String, String> db1 = WSClient.getDBRow(query1);

				String commentId = db1.get("NOTE_ID");
				if (commentId == null) {
					WSClient.writeToReport(LogStatus.FAIL, " ***** Blocked : The note Id is not added *****");
				} else {
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Note ID :" + commentId + "</b>");

						WSClient.setData("{var_commentID}", commentId);
						WSClient.setData("{var_profileID}", operaProfileID);
						WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));

						// ******** OWS UpdateComment*************//

						String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_02");
						String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_02");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);

								/*** Getting the values from the Request ******/

								String name_id = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_NameID", XMLType.REQUEST);

								String noteTitle = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_Comment_commentTitle", XMLType.REQUEST);
								String noteType = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_Comment_commentType", XMLType.REQUEST);
								String dueDate = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_DueDate", XMLType.REQUEST);
								String notes = WSClient.getElementValue(updateCommentReq, "Comment_Text_TextElement", XMLType.REQUEST);

								/******
								 * Validating the values sent in the request
								 * against the database
								 **********/

								if (WSAssert.assertEquals(name_id, db.get("NAME_ID"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Name ID ->	 Expected :		" + name_id + " 	 ,	Actual :	   	" + db.get("NAME_ID"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Name ID ->	 Expected :		" + name_id + "		 ,	Actual :	  	" + db.get("NAME_ID"));

								}
								if (WSAssert.assertEquals(commentId, db.get("NOTE_ID"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Comment ID -> 	Expected :	" + commentId + "		  ,	Actual :	  		" + db.get("NOTE_ID"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Comment ID -> Expected :		" + commentId + " 	 ,	Actual :	 		 " + db.get("NOTE_ID"));

								}

								if (WSAssert.assertEquals(noteTitle, db.get("NOTE_TITLE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Note Title ->	 Expected :		" + noteTitle + " 	 ,	Actual :	  	 " + db.get("NOTE_TITLE"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Note Title -> 	Expected :		" + noteTitle + "	 ,	 ,	Actual :	 	 " + db.get("NOTE_TITLE"));

								}

								if (WSAssert.assertEquals(dueDate, db.get("DUE_DATE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Due Date ->	 Expected :		" + dueDate + " 	 ,	Actual :	  	 " + db.get("DUE_DATE"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Due Date ->	 Expected :	" + dueDate + " 	 ,	Actual :	  	 " + db.get("DUE_DATE"));

								}
								if (WSAssert.assertEquals(noteType, db.get("NOTE_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Note Type ->	 Expected :		" + noteType + " 	 ,	Actual :	  	 " + db.get("NOTE_CODE"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Note Type ->	 Expected :		" + noteType + " 	 ,	Actual :	  	 " + db.get("NOTE_CODE"));

								}
								if (WSAssert.assertEquals(notes, db.get("NOTES"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Note Text -> 	Expected :		" + notes + "		  ,	Actual :	 	 " + db.get("NOTES"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Note Text ->	 Expected : 		" + notes + " 		 ,	Actual :	  	 " + db.get("NOTES"));

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "************* Update comment was unsuccessful! ********");
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "The result flag doesnot exist on the response!");

						if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Note type does not exist!*********");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateComment", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that the error message is populated on the response when Invalid
	 * profile ID is passed on the request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 * 
	 *****/

	public void updateComment_38281() {
		try {

			String testName = "updateComment_38281";
			WSClient.startTest(testName, "Verify that the error message is populated on the response when invalid opera profile ID is sent on the request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Setting the Opera Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");

			WSClient.setData("{var_profileID}", operaProfileID);
			WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resort, channel));

			// ******** OWS UpdateComment*************//

			String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_03");
			String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);
			if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
				if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "FAIL", false)) {

				} else
					WSClient.writeToReport(LogStatus.FAIL, "Update comment is Successful ! ");
			}
			if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
				if (message.equals("*null*")) {

				} else
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
				if (message.equals("*null*")) {

				} else
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if (message.equals("*null*")) {

				} else
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
				if (message.equals("*null*")) {

				} else
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateComment", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that the error message is populated on the response when Invalid
	 * comment ID is passed on the request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 * 
	 *****/

	public void updateComment_38393() {
		try {

			String testName = "updateComment_38393";
			WSClient.startTest(testName, "Verify that the error message is populated on the response when invalid opera comment ID is sent on the request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Setting the Opera Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");

			/** Prerequisite : Create Profile With Comments **/

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {

				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + operaProfileID + "</b>");

				// ******** OWS Update Comment*************//

				WSClient.setData("{var_commentID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
				String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_01");
				String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);

				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
					if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "FAIL", false)) {

					} else
						WSClient.writeToReport(LogStatus.FAIL, "Update comment is Successful");
				}

				if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateComment", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that the error message is populated on the response when opera
	 * Profile ID is not passed on the request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 * 
	 *****/

	public void updateComment_38283() {
		try {

			String testName = "updateComment_38283";
			WSClient.startTest(testName, "Verify that the error message is populated on the response when opera Profile ID is not sent on the request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Setting the Opera Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");

			/*** Prerequisite 1 : Create Profile With comments ***/

			String operaProfileID = CreateProfile.createProfile("DS_10");
			if (!operaProfileID.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);

				String query = WSClient.getQuery("QS_05");
				LinkedHashMap<String, String> db = WSClient.getDBRow(query);

				String commentId = db.get("NOTE_ID");

				if (commentId == null) {
					WSClient.writeToReport(LogStatus.FAIL, " ***** Blocked : The note Id is not added *****");
				} else {

					WSClient.writeToReport(LogStatus.INFO, "<b>Note ID :" + commentId + "</b>");

					WSClient.setData("{var_commentID}", commentId);
					WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resort, channel));

					// ******** OWS Update Comment*************//

					String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_04");
					String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);
					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "FAIL", false)) {

						} else
							WSClient.writeToReport(LogStatus.FAIL, "Update comment is Successful ");
					}
					if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
						if (message.equals("SYSTEM ERROR") || message != "")
							WSClient.writeToReport(LogStatus.INFO, "Opera Profile ID is not sent on the response");
						else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
						if (message.equals("*null*")) {

						} else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}
					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						if (message.equals("*null*")) {

						} else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}
					if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
						if (message.equals("*null*")) {

						} else
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateComment", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that the error message is populated on the response when comment
	 * ID is not passed on the request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 * 
	 *****/

	public void updateComment_38420() {
		try {

			String testName = "updateComment_38420";
			WSClient.startTest(testName, "Verify that the error message is populated on the response when opera comment ID is not sent on the request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Setting the Opera Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");

			/*** Prerequisite 1 : Create Profile With comments ***/

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {

				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + operaProfileID + "</b>");
				WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resort, channel));

				// ******** OWS Update Comment*************//

				String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_03");
				String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);
				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
					if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "FAIL", false)) {

					} else {
						WSClient.writeToReport(LogStatus.FAIL, " Update comment is Successful ! ");
					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag doesnot exist on the response!");

				if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message.equals("SYSTEM ERROR") || message != "")
						WSClient.writeToReport(LogStatus.INFO, "Opera Profile ID is not sent on the response");
					else
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
					if (message.equals("*null*")) {

					} else
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message.equals("*null*")) {

					} else
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
					if (message.equals("*null*")) {

					} else
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateComment", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that an internal comment is updated to external successfully.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with internal
	 * comments.
	 * 
	 *****/

	public void updateComment_42477() {
		try {

			String testName = "updateComment_42477";
			WSClient.startTest(testName, " Verify that an internal comment is updated to external successfully.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Setting the Opera Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");

			// *********Prerequisite: Create Profile With
			// comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_07");
			if (!operaProfileID.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);

				String query1 = WSClient.getQuery("QS_05");
				LinkedHashMap<String, String> db1 = WSClient.getDBRow(query1);

				String commentId = db1.get("NOTE_ID");
				String internal = db1.get("INTERNAL_YN");

				if (commentId == null) {
					WSClient.writeToReport(LogStatus.FAIL, "  Blocked : The note Id is not added ");
				} else {
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Note ID :" + commentId + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b> Comment internal Y/N before Update : " + internal + "</b>");

						WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resort, channel));
						WSClient.setData("{var_commentID}", commentId);
						WSClient.setData("{var_profileID}", operaProfileID);
						WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));

						// ******** OWS UpdateComment*************//

						String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_08");
						String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);

						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);
								/*** Getting the values from the Request ******/

								String name_id = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_NameID", XMLType.REQUEST);

								String internalyn = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_Comment_internalYn", XMLType.REQUEST);

								/******
								 * Validating the values sent in the request
								 * against the database
								 **********/

								if (WSAssert.assertEquals(name_id, db.get("NAME_ID"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Name ID ->	 Expected :		" + name_id + " 	 ,	Actual :	   	" + db.get("NAME_ID"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Name ID ->	 Expected :		" + name_id + "		 ,	Actual :	  	" + db.get("NAME_ID"));

								}
								if (WSAssert.assertEquals(commentId, db.get("NOTE_ID"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Comment ID -> 	Expected :	" + commentId + "		  ,	Actual :	  		" + db.get("NOTE_ID"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Comment ID -> Expected :		" + commentId + " 	 ,	Actual :	 		 " + db.get("NOTE_ID"));

								}

								if (WSAssert.assertEquals(internalyn, db.get("INTERNAL_YN"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Internal Y/N -> 	Expected :	" + internalyn + "		 ,	Actual :	 		  " + db.get("INTERNAL_YN"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Internal Y/N -> 	Expected :" + internalyn + " 	 ,	Actual :	  	 " + db.get("INTERNAL_YN"));

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "************* Update comment was unsuccessful! ********");
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "The result flag doesnot exist on the response!");

						if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Note type does not exist!*********");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateComment", "OWS", "Name", "in-QA" })

	/*****
	 * Verify that comment type is updated when comment text is not sent on the
	 * request.
	 * 
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with comments.
	 * 
	 *****/

	public void updateComment_38284() {
		try {

			String testName = "updateComment_38284";
			WSClient.startTest(testName, "Verify that comment type is updated  when  when comment text is not sent on the request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ******** Setting the OWS Header *************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// ******** Setting the Opera Header *************//

			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");

			// *********Prerequisite: Create Profile With
			// comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_10");
			if (!operaProfileID.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + operaProfileID + "</b>");
				WSClient.setData("{var_profileID}", operaProfileID);

				String query1 = WSClient.getQuery("QS_05");
				LinkedHashMap<String, String> db1 = WSClient.getDBRow(query1);

				String commentId = db1.get("NOTE_ID");
				if (commentId == null) {
					WSClient.writeToReport(LogStatus.FAIL, " ***** Blocked : The note Id is not added *****");
				} else {
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Note ID :" + commentId + "</b>");

						WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resort, channel));
						WSClient.setData("{var_commentID}", commentId);
						WSClient.setData("{var_profileID}", operaProfileID);
						WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));

						// ******** OWS UpdateComment*************//

						String updateCommentReq = WSClient.createSOAPMessage("OWSUpdateComment", "DS_06");
						String updateCommentRes = WSClient.processSOAPMessage(updateCommentReq);
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateCommentRes, "UpdateCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_02");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);
								/*** Getting the values from the Request ******/

								String name_id = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_NameID", XMLType.REQUEST);

								String noteTitle = WSClient.getElementValue(updateCommentReq, "UpdateCommentRequest_Comment_commentTitle", XMLType.REQUEST);

								/******
								 * Validating the values sent in the request
								 * against the database
								 **********/

								if (WSAssert.assertEquals(name_id, db.get("NAME_ID"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Name ID ->	 Expected :		" + name_id + " 	 ,	Actual :	   	" + db.get("NAME_ID"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Name ID ->	 Expected :		" + name_id + "		 ,	Actual :	  	" + db.get("NAME_ID"));

								}

								if (WSAssert.assertEquals(commentId, db.get("NOTE_ID"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Comment ID -> 	Expected :	" + commentId + "		  ,	Actual :	  		" + db.get("NOTE_ID"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Comment ID -> Expected :		" + commentId + " 	 ,	Actual :	 		 " + db.get("NOTE_ID"));

								}

								if (WSAssert.assertEquals(noteTitle, db.get("NOTE_TITLE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Note Title ->	 Expected :		" + noteTitle + " 	 ,	Actual :	  	 " + db.get("NOTE_TITLE"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Note Title -> 	Expected :		" + noteTitle + "	 ,	 ,	Actual :	 	 " + db.get("NOTE_TITLE"));

								}

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "************* Update comment was unsuccessful! ********");
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "The result flag doesnot exist on the response!");

						if (WSAssert.assertIfElementExists(updateCommentRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_GDSError", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateCommentRes, "UpdateCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateCommentRes, "UpdateCommentResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateCommentRes, "UpdateCommentResponse_faultstring", XMLType.RESPONSE);
							if (message.equals("*null*")) {

							} else
								WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Note type does not exist!*********");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

}
