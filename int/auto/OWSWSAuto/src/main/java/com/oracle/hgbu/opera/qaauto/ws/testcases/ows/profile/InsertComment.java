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

public class InsertComment extends WSSetUp {
	String operaProfileID = null;


	@Test(groups = { "sanity", "InsertComment", "OWS", "Name", "in-QA" })
	/*****
	 *Verify that comment is attached to the given profile when request is submitted with minimum required data
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****/
	public void insertComment_38262() {
		try {
			String testName = "insertComment_38262";
			WSClient.startTest(testName,
					"Verify that comment is attached to the given profile when request is submitted with minimum required data",
					"sanity");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			/*** Setting the Opera Header ***/

			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			/*************
			 * Prerequisite 1: Creating new profile
			 ******************/
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite: Create Profile With comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.setData("{var_profileID}", operaProfileID);
			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

			/*************
			 * Operation InsertComment: Inserting Comment for the profile created
			 ******************/

			String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_01");
			String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

			if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(insertCommentRes, "Result_IDs_IDPair_operaId", false)) {

					/*** Getting the values from the Request ******/

					String commentid = WSClient.getElementValue(insertCommentRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.setData("{var_commentid}", commentid);
					WSClient.writeToReport(LogStatus.INFO, "<b> Note ID : " + commentid+"</b>");
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					String query = WSClient.getQuery("QS_01");
					db = WSClient.getDBRow(query);
					String name_id = WSClient.getElementValue(insertCommentReq, "InsertCommentRequest_NameID",
							XMLType.REQUEST);
					String notes = WSClient.getElementValue(insertCommentReq, "Comment_Text_TextElement",
							XMLType.REQUEST);

					/*** Validating the values sent in the request against the database *****/

					if (WSAssert.assertEquals(name_id, db.get("NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"ProfileID -> Expected    :  " + name_id + ",   Actual : " + db.get("NAME_ID"));

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"ProfileID  -> Expected    : " + name_id + " ,     Actual   : " + db.get("NAME_ID"));

					}
					if (WSAssert.assertEquals(notes, db.get("NOTES"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"NoteText -> Expected   : " + notes + ",        Actual   : " + db.get("NOTES"));

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"NoteText -> Expected    : " + notes + " ,      Actual    :  " + db.get("NOTES"));

					}
					if (WSAssert.assertEquals("OWS", db.get("NOTE_CODE"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"NoteType -> Expected      : " + "OWS" + " ,      Actual    :  " + db.get("NOTE_CODE"));

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"NoteType -> Expected    :  " + "OWS" + " ,      Actual     :  " + db.get("NOTE_CODE"));

					}
					if (WSAssert.assertEquals(commentid, db.get("NOTE_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"NoteID  -> Expected      :  " + commentid + " ,      Actual    :   " + db.get("NOTE_ID"));

					} else {
						WSClient.writeToReport(LogStatus.FAIL, "NoteID ->  Expected     :  " + commentid
								+ " ,           Actual     :   " + db.get("NOTE_ID"));

					}

				}

			} else {
				WSClient.writeToReport(LogStatus.FAIL, "The comment is not attached to the profile!");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

				/**** Verifying that the error message is populated on the response ********/

				String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes,
						"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				;
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	/*****
	 * Verify that all the fields (Mandatory & Optional) related to the comment are inserted correctly when given on the request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****/
	public void insertComment_38273() {
		try {
			String testName = "insertComment_38273";
			WSClient.startTest(testName,
					"Verify that all the fields (Mandatory & Optional) related to the comment are inserted correctly when given on the request",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			/*** Setting the Opera Header ***/

			OPERALib.setOperaHeader(uname);

			/*************
			 * Prerequisite 1: Creating new profile
			 ******************/

			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite: Create Profile **************//

			String operaProfileID = CreateProfile.createProfile("DS_01");

			WSClient.setData("{var_profileID}", operaProfileID);
			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

			// ****** OWS Insert Comment ******//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {
				WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_02");
				String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

				if (WSAssert.assertIfElementValueEquals(insertCommentRes,
						"InsertCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					if (WSAssert.assertIfElementExists(insertCommentRes, "Result_IDs_IDPair_operaId", false)) {

						String commentid = WSClient.getElementValue(insertCommentRes, "Result_IDs_IDPair_operaId",
								XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO, "<b> Note ID : " + commentid+"</b>");
						WSClient.setData("{var_commentid}", commentid);

						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						String query = WSClient.getQuery("QS_02");
						db = WSClient.getDBRow(query);

						/*** Getting the values from the Request ******/

						String name_id = WSClient.getElementValue(insertCommentReq, "InsertCommentRequest_NameID",
								XMLType.REQUEST);
						String notes = WSClient.getElementValue(insertCommentReq, "Comment_Text_TextElement",
								XMLType.REQUEST);
						String noteTitle = WSClient.getElementValue(insertCommentReq,
								"InsertCommentRequest_Comment_commentTitle", XMLType.REQUEST);
						String noteType = WSClient.getElementValue(insertCommentReq,
								"InsertCommentRequest_Comment_commentType", XMLType.REQUEST);
						String dueDate = WSClient.getElementValue(insertCommentReq, "InsertCommentRequest_DueDate",
								XMLType.REQUEST);

						String internalyn = WSClient.getElementValue(insertCommentReq,
								"InsertCommentRequest_Comment_internalYn", XMLType.REQUEST);

						/******
						 * Validating the values sent in the request against the database
						 **********/

						if (WSAssert.assertEquals(name_id, db.get("NAME_ID"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Name ID ->	Expected :		" + name_id
									+ " ,		Actual :	  " + db.get("NAME_ID"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Name ID ->	Expected :		" + name_id
									+ " ,		Actual :	  " + db.get("NAME_ID"));

						}
						if (WSAssert.assertEquals(commentid, db.get("NOTE_ID"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Comment ID ->	Expected :		" + commentid
									+ "	, 	Actual :	  " + db.get("NOTE_ID"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Comment ID ->	Expected :		" + commentid
									+ " , 		Actual :	  " + db.get("NOTE_ID"));

						}
						if (WSAssert.assertEquals(notes, db.get("NOTES"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Note Text -> 	Expected :		" + notes
									+ " ,		Actual :	  " + db.get("NOTES"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Note Text ->	Expected :		 " + notes
									+ "	,	Actual :	  " + db.get("NOTES"));

						}

						if (WSAssert.assertEquals(internalyn, db.get("INTERNAL_YN"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Internal Y/N ->	Expected :		 " + internalyn
									+ "	,	Actual :	 " + db.get("INTERNAL_YN"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Internal Y/N -> " + internalyn + " ,		Actual :	" + db.get("INTERNAL_YN"));

						}

						if (WSAssert.assertEquals(noteTitle, db.get("NOTE_TITLE"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Note Title ->	Expected :		" + noteTitle
									+ " ,		Actual :	" + db.get("NOTE_TITLE"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Note Title ->	Expected :		" + noteTitle
									+ " ,		Actual :	 " + db.get("NOTE_TITLE"));

						}
						if (WSAssert.assertEquals(noteType, db.get("NOTE_CODE"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Note Type ->		Expected :		" + noteType
									+ " ,		Actual :	 " + db.get("NOTE_CODE"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Note Type ->		Expected :		" + noteType
									+ "	,	Actual :	 " + db.get("NOTE_CODE"));

						}
						if (WSAssert.assertEquals(dueDate, db.get("DUE_DATE"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Due Date ->		Expected :		" + dueDate
									+ "	,	Actual :	 " + db.get("DUE_DATE"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Due Date ->		Expected :		" + dueDate
									+ " ,      Actual :      " + db.get("DUE_DATE"));

						}

					}

				} else {
					WSClient.writeToReport(LogStatus.FAIL, "************* Insert comment is not Successful! ********");
				}
				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

					/**** Verifying that the error message is populated on the response ********/

					String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode",
						true)) {

					/**** Verifying whether the error Message is populated on the response ****/

					String message = WSAssert.getElementValue(insertCommentRes,
							"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Note type does not exist!*********");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	/*****
	 * Verify that an error message is thrown when COMMENT TEXT IS MISSED on the request.",
	 * minimumRegression
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****/
	public void insertComment_38272() {
		try {
			String testName = "insertComment_38272";
			WSClient.startTest(testName,
					"Verify that an error message is thrown when COMMENT TEXT IS MISSED on the request.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			OPERALib.setOperaHeader(uname);

			/*************
			 * Prerequisite 1: Creating new profile
			 ******************/
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite: Create Profile **************//

			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

			/*************
			 * Operation InsertComment: Inserting Comment for the profile created
			 *
			 ******************/

			WSClient.setData("{var_profileID}", operaProfileID);
			String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_03");
			String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

			if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
					"FAIL", false)) {

			} else {
				WSClient.writeToReport(LogStatus.FAIL,
						"The comment has been attached to the profile successfully! ERROR! ");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

				/**** Verifying that the error message is populated on the response ********/

				String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if (message.equals("SYSTEM ERROR"))
					WSClient.writeToReport(LogStatus.INFO, "<b>The comment text is not sent on the response<b>");
				else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes,
						"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

				String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	/*****
	 * Verify that an error message is thrown when INVALID RESORT is passed on the request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****/
	public void insertComment_38271() {
		try {
			String testName = "insertComment_38271";
			WSClient.startTest(testName,
					"Verify that an error message is thrown when INVALID RESORT is passed on the request",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			/***** Setting the Opera Header ******/

			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite 1: Create Profile **************//

			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.setData("{var_profileID}", operaProfileID);
			WSClient.setData("{var_resortId}", resort.concat("X"));
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

			/***** Setting the OWS Header ******/

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			/*************
			 * Operation InsertComment: Inserting Comment for the profile created
			 ******************/

			String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_04");
			String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

			if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
					"FAIL", false)) {

			} else {

				WSClient.writeToReport(LogStatus.FAIL,
						"The comment has been attached to the profile successfully! ERROR! ");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

				/**** Verifying that the error message is populated on the response ********/

				String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes,
						"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	/*****
	 * Verify that an error message is thrown when INVALID PROFILE ID is sent on the request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****/
	public void insertComment_38392() {
		try {
			String testName = "insertComment_38392";
			WSClient.startTest(testName,
					"Verify that an error message is thrown when INVALID PROFILE ID is sent on the request",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_profileID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));
			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			/*************
			 * Operation InsertComment: Inserting Comment for the profile created
			 ******************/

			String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_01");
			String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag", false)) {
				if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
						"FAIL", false)) {
				} else {
					WSClient.writeToReport(LogStatus.FAIL,
							"The comment has been attached to the profile successfully! ERROR! ");
				}

			}else
				WSClient.writeToReport(LogStatus.FAIL,
						"Insert Comment operation has failed! ");
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

				/**** Verifying that the error message is populated on the response ********/

				String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/*@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	 *//*****
	 * Verify that all the fields (Mandatory & Optional) related to the comment are inserted correctly when given on the request
	 *****//*
	 *//*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****//*
	public void insertComment_102333() {
		try {
			String testName = "insertComment_102333";
			WSClient.startTest(testName,
					"Verify an insert comment of type OWS (comment of 3955 Length and of comment type internal) from a guest profile",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);



			OPERALib.setOperaHeader(uname);

	  *//*************
	  * Prerequisite 1: Creating new profile
	  ******************//*

			WSClient.setData("{var_resort}", OPERALib.getResort());
			//WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));



			String operaProfileID = CreateProfile.createProfile("DS_01");

			WSClient.setData("{var_profileID}", operaProfileID);
			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");



			if (OperaPropConfig.getPropertyConfigResults(new String[] { "NoteType" })) {
				//WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_commentType}", "OWS");
				System.out.println("cOMMENT tYPE: "+WSClient.getData("{var_commentType}"));
				String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_05");
				System.out.println("Request: ------------------------------\n"+insertCommentReq);
				String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);
				if (WSAssert.assertIfElementValueEquals(insertCommentRes,
						"InsertCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					if (WSAssert.assertIfElementExists(insertCommentRes, "Result_IDs_IDPair_operaId", false)) {

						String commentid = WSClient.getElementValue(insertCommentRes, "Result_IDs_IDPair_operaId",
								XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO, "<b> Note ID : " + commentid+"</b>");
						WSClient.setData("{var_commentid}", commentid);

						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						String query = WSClient.getQuery("QS_02");
						db = WSClient.getDBRow(query);

	   *//*** Getting the values from the Request ******//*

						String name_id = WSClient.getElementValue(insertCommentReq, "InsertCommentRequest_NameID",
								XMLType.REQUEST);
						String notes = WSClient.getElementValue(insertCommentReq, "Comment_Text_TextElement",
								XMLType.REQUEST);
						String noteTitle = WSClient.getElementValue(insertCommentReq,
								"InsertCommentRequest_Comment_commentTitle", XMLType.REQUEST);
						String noteType = WSClient.getElementValue(insertCommentReq,
								"InsertCommentRequest_Comment_commentType", XMLType.REQUEST);
						String dueDate = WSClient.getElementValue(insertCommentReq, "InsertCommentRequest_DueDate",
								XMLType.REQUEST);

						String internalyn = WSClient.getElementValue(insertCommentReq,
								"InsertCommentRequest_Comment_internalYn", XMLType.REQUEST);


						if (WSAssert.assertEquals(name_id, db.get("NAME_ID"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Name ID ->	Expected :		" + name_id
									+ " ,		Actual :	  " + db.get("NAME_ID"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Name ID ->	Expected :		" + name_id
									+ " ,		Actual :	  " + db.get("NAME_ID"));

						}
						if (WSAssert.assertEquals(commentid, db.get("NOTE_ID"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Comment ID ->	Expected :		" + commentid
									+ "	, 	Actual :	  " + db.get("NOTE_ID"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Comment ID ->	Expected :		" + commentid
									+ " , 		Actual :	  " + db.get("NOTE_ID"));

						}
						if (WSAssert.assertEquals(notes, db.get("NOTES"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Note Text -> 	Expected :		" + notes
									+ " ,		Actual :	  " + db.get("NOTES"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Note Text ->	Expected :		 " + notes
									+ "	,	Actual :	  " + db.get("NOTES"));

						}

						if (WSAssert.assertEquals(internalyn, db.get("INTERNAL_YN"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Internal Y/N ->	Expected :		 " + internalyn
									+ "	,	Actual :	 " + db.get("INTERNAL_YN"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Internal Y/N -> " + internalyn + " ,		Actual :	" + db.get("INTERNAL_YN"));

						}

						if (WSAssert.assertEquals(noteTitle, db.get("NOTE_TITLE"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Note Title ->	Expected :		" + noteTitle
									+ " ,		Actual :	" + db.get("NOTE_TITLE"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Note Title ->	Expected :		" + noteTitle
									+ " ,		Actual :	 " + db.get("NOTE_TITLE"));

						}
						if (WSAssert.assertEquals(noteType, db.get("NOTE_CODE"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Note Type ->		Expected :		" + noteType
									+ " ,		Actual :	 " + db.get("NOTE_CODE"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Note Type ->		Expected :		" + noteType
									+ "	,	Actual :	 " + db.get("NOTE_CODE"));

						}
						if (WSAssert.assertEquals(dueDate, db.get("DUE_DATE"), true)) {
							WSClient.writeToReport(LogStatus.PASS, "Due Date ->		Expected :		" + dueDate
									+ "	,	Actual :	 " + db.get("DUE_DATE"));

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Due Date ->		Expected :		" + dueDate
									+ " ,      Actual :      " + db.get("DUE_DATE"));

						}

					}

				} else {
					WSClient.writeToReport(LogStatus.FAIL, "************* Insert comment is not Successful! ********");
				}
				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {



					String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {



					String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {


					String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode",
						true)) {



					String message = WSAssert.getElementValue(insertCommentRes,
							"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Note type does not exist!*********");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}*/

	/*@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	 *//*****
	 * Verify that an error message is thrown when COMMENT TEXT IS MISSED on the request.",
	 * minimumRegression
	 *****//*
	 *//*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****//*
	public void insertComment_989070() {
		try {
			String testName = "insertComment_989070";
			WSClient.startTest(testName,
					"Verify an error is displayed when insert comment of type OWS has text comment more than 5000 (comment of 3955 Length and of comment type internal) from a guest profile",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", "OWS");
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));



			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");



			WSClient.setData("{var_profileID}", operaProfileID);
			String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_06");
			String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

			if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
					"FAIL", false)) {

			} else {
				WSClient.writeToReport(LogStatus.FAIL,
						"The comment has been attached to the profile successfully! ERROR! ");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {



				String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if (message.contains("exceeds the maximum length"))
					WSClient.writeToReport(LogStatus.INFO, "<b>The comment text is not sent on the response<b>");
				else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {



				String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {


				String message = WSAssert.getElementValue(insertCommentRes,
						"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

				String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}*/

	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	/*****
	 * Verify that an error message is thrown when INVALID CHANNEL is passed on the request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****/
	public void insertComment_64001() {
		try {
			String testName = "insertComment_64001";
			WSClient.startTest(testName,
					"Verify that an error message is thrown when INVALID CHANNEL is passed on the request.",
					"sanity");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			String channel1 = "INVALIDCHANNEL";

			/*** Setting the Opera Header ***/

			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			/*************
			 * Prerequisite 1: Creating new profile
			 ******************/
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel1), channel1);

			// *********Prerequisite: Create Profile With comments**************//

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.setData("{var_profileID}", operaProfileID);
			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel1));
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

			/*************
			 * Operation InsertComment: Inserting Comment for the profile created
			 ******************/

			String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_01");
			String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

			if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
					"FAIL", false)) {

			} else {
				WSClient.writeToReport(LogStatus.FAIL,
						"The comment has been attached to the profile successfully though the channel on the rerquest is invalid! ");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

				/**** Verifying that the error message is populated on the response ********/

				String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes,
						"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/*	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })
	public void insertComment_HHOCM_64_02() {
		try {
			String testName = "insertComment_HHOCM_64_02";
			WSClient.startTest(testName,
					"Verify that an error is displayed when user of one chain is able to Insert comment for another resort of another chain which are active and channel configured.",
					"sanity");
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			String channel1 = OWSLib.getChannel();


			HashMap<String,String> otherResortData = new HashMap<String,String>();
			otherResortData = WSClient.getDBRow("SELECT CHAIN_CODE, RESORT, END_DATE FROM RESORT R WHERE CHAIN_CODE != '"+chain+"' AND RESORT !='"+resort+"' AND (END_DATE IS NULL OR (END_DATE >"
					+ " (SELECT MAX(BUSINESS_DATE) FROM BUSINESSDATE WHERE RESORT= R.RESORT))) AND CHAIN_CODE IN (SELECT CHAIN_CODE FROM GDS_HOSTS WHERE CHANNEL_TYPE='WEB' AND "
					+ " (INACTIVE_DATE IS NULL OR INACTIVE_DATE > (SELECT MAX(BUSINESS_DATE) FROM BUSINESSDATE WHERE RESORT= R.RESORT)))  AND ROWNUM <2");

			if(otherResortData.size() <1) {
				WSClient.writeToReport(LogStatus.INFO, "No resorts found with active channels configured to it! Pre-requisite failure!");
			}
			else {
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_chain}", chain);

				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_gender}", "M");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel1), OWSLib.getChannelCarier(resort, channel1));

				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel1));
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

				String anotherResort ="";
						if(otherResortData.containsKey("RESORT"))
							anotherResort = otherResortData.get("RESORT");
						OPERALib.setOperaHeader(uname);
						WSClient.setData("{var_resort}", anotherResort);
						System.out.println("Other Resort: "+anotherResort);
						String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_01");
						String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

						if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
								"FAIL", false)) {

						} else {

							WSClient.writeToReport(LogStatus.FAIL,
									"The comment has been attached to the profile successfully! ERROR! ");
						}
						if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {


							String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
									XMLType.RESPONSE);
							if(message=="*null*") {

							} else
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {


							String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if(message=="*null*") {

							} else
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {


							String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
									XMLType.RESPONSE);
							if(message=="*null*") {

							} else
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {


							String message = WSAssert.getElementValue(insertCommentRes,
									"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if(message=="*null*") {

							} else
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

}

} catch (Exception e) {
	WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
}
}

	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name", "in-QA" })

	public void insertComment_HHOCM_64_03() {
		try {
			String testName = "insertComment_HHOCM_64_03";
			WSClient.startTest(testName,
					"Verify that user who have access to the properties of same chain is able to Insert comment for resort 2.",
					"sanity");
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			String channel1 = OWSLib.getChannel();



			HashMap<String,String> otherResortData = new HashMap<String,String>();
			otherResortData = WSClient.getDBRow("SELECT CHAIN_CODE, RESORT, END_DATE FROM RESORT R WHERE CHAIN_CODE = '"+chain+"' AND RESORT !='"+resort+"' AND (END_DATE IS NULL OR (END_DATE >"
					+ " (SELECT MAX(BUSINESS_DATE) FROM BUSINESSDATE WHERE RESORT= R.RESORT))) AND CHAIN_CODE IN (SELECT CHAIN_CODE FROM GDS_HOSTS WHERE CHANNEL_TYPE='WEB' AND "
					+ " (INACTIVE_DATE IS NULL OR INACTIVE_DATE > (SELECT MAX(BUSINESS_DATE) FROM BUSINESSDATE WHERE RESORT= R.RESORT)))  AND ROWNUM <2");

			if(otherResortData.size() <1) {
				WSClient.writeToReport(LogStatus.INFO, "No resorts found with active channels configured to it! Pre-requisite failure!");
			}
			else {
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_chain}", chain);

						WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_gender}", "M");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel1), OWSLib.getChannelCarier(resort, channel1));


				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel1));
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");


				String anotherResort ="";
				if(otherResortData.containsKey("RESORT"))
					anotherResort = otherResortData.get("RESORT");
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_resort}", anotherResort);
				System.out.println("Other Resort: "+anotherResort);
				String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_01");
				String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

				if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
						"FAIL", false)) {

				} else {

					WSClient.writeToReport(LogStatus.FAIL,
							"The comment has been attached to the profile successfully! ERROR! ");
				}
				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

	 *//**** Verifying whether the error Message is populated on the response ****//*

					String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

	  *//**** Verifying that the error message is populated on the response ********//*

					String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

	   *//**** Verifying whether the error Message is populated on the response ****//*

					String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
							XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {

	    *//**** Verifying whether the error Message is populated on the response ****//*

					String message = WSAssert.getElementValue(insertCommentRes,
							"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if(message=="*null*") {

					} else
						WSClient.writeToReport(LogStatus.INFO,
								"The error populated on the response is : <b>" + message + "</b>");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}*/


	@Test(groups = { "minimumRegression", "InsertComment", "OWS", "Name"})
	/*****
	 * Verify that an error message is thrown when COMMENT TEXT IS MISSED on the request.",
	 * minimumRegression
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile.
	 *
	 *****/
	public void insertComment_908681() {
		try {
			String testName = "insertComment_908681";
			WSClient.startTest(testName,
					"Verify that an error message is displayed when INVALID COMMENT TYPE is given on the request",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain = OPERALib.getChain();
			String uname = OPERALib.getUserName();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			OPERALib.setOperaHeader(uname);

			/*************
			 * Prerequisite 1: Creating new profile
			 ******************/
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
			WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// *********Prerequisite: Create Profile **************//

			WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resort,channel));
			WSClient.setData("{var_commentType}", OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
			String operaProfileID = CreateProfile.createProfile("DS_CreateProfile_51");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+ operaProfileID+"</b>");

			/*************
			 * Operation InsertComment: Inserting Comment for the profile created
			 *
			 ******************/

			WSClient.setData("{var_profileID}", operaProfileID);
			String insertCommentReq = WSClient.createSOAPMessage("OWSInsertComment", "DS_08");
			String insertCommentRes = WSClient.processSOAPMessage(insertCommentReq);

			if (WSAssert.assertIfElementValueEquals(insertCommentRes, "InsertCommentResponse_Result_resultStatusFlag",
					"FAIL", false)) {

			} else {
				WSClient.writeToReport(LogStatus.FAIL,
						"The comment has been attached to the profile successfully! ERROR! ");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "Result_Text_TextElement", true)) {

				/**** Verifying that the error message is populated on the response ********/

				String message = WSAssert.getElementValue(insertCommentRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if (message.equals("SYSTEM ERROR"))
					WSClient.writeToReport(LogStatus.INFO, "<b>The comment text is not sent on the response<b>");
				else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_GDSError", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes, "InsertCommentResponse_Result_GDSError",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_Result_OperaErrorCode", true)) {

				/**** Verifying whether the error Message is populated on the response ****/

				String message = WSAssert.getElementValue(insertCommentRes,
						"InsertCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(insertCommentRes, "InsertCommentResponse_faultcode", true)) {

				String message = WSClient.getElementValue(insertCommentRes, "InsertCommentResponse_faultstring",
						XMLType.RESPONSE);
				if(message=="*null*") {

				} else
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
}
