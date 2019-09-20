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


public class DeleteComment extends WSSetUp {
	

	
	/**
	 * @author ketvaidy
	 */

	@Test(groups =  { "sanity","OWS","DeleteComment","Name"})
	

	public void deleteComment_38266() {
		try {
			/**
			 * Method to verify that a comment is deleted when minimum required data is provided on the request 
			 **/
			
			/*****
			 * * * PreRequisites Required: -->There should be a profile with comments
			 * 
			 *****/

			String testName = "deleteComment_38266";
			WSClient.startTest(testName,
					"Verify that the comment is deleted when minimum required data is provided on the request ", "sanity");
			String operaProfileID = "";
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String chain=OPERALib.getChain();
			
			//******** Setting the OWS Header *************//
			 WSClient.setData("{var_gender}", "M");
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_commentType}",OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_comment}", WSClient.getKeywordData("{KEYWORD_RANDSTR_8}"));

		    //******** Prerequisite :Create Profile*************//
				
				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_10");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" +operaProfileID+"</b>");
                    String query=WSClient.getQuery("QS_05");
					LinkedHashMap<String, String> db1 = WSClient.getDBRow(query);
					String commentId = db1.get("NOTE_ID");
					WSClient.writeToReport(LogStatus.INFO, "<b>Note ID :" + commentId+"</b>");
				
					WSClient.setData("{var_commentID}", commentId);
					
					//******** OWS Delete Comment *************//
					
					String deleteCommentReq = WSClient.createSOAPMessage("OWSDeleteComment", "DS_01");
					String deleteCommentRes = WSClient.processSOAPMessage(deleteCommentReq);
					if(WSAssert.assertIfElementExists(deleteCommentRes, "Result_Text_TextElement",true))
					{
			
						/**** Verifying that the error message is populated on the response ********/
			
					String message=WSAssert.getElementValue(deleteCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode",true)) 
					{
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_resultStatusFlag", false))
					{
					  
					if (WSAssert.assertIfElementValueEquals(deleteCommentRes,
							"DeleteCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						String query1=WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> inactiveRecord = WSClient.getDBRow(query1);
						/**
						 * Validation of the inactive date with the current system date.
						 **/
						
						if(WSAssert.assertEquals("TRUE", inactiveRecord.get("INACTIVE_DATE"), true)) {
							WSClient.writeToReport(LogStatus.PASS,"The comment has been successfully deleted");
						}
						else {
							WSClient.writeToReport(LogStatus.FAIL,"The comment couldnt get deleted");
						}

					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Failed to delete the comment ");
					}
					}
				else {
					WSClient.writeToReport(LogStatus.FAIL,
							"************Schema is incorrect**********");
				}}}
			
			
			}
		 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}}
		

	
   @Test(groups =  { "minimumRegression","OWS","DeleteComment","Name"})

		public void deleteComment_38510() {
			try {
				/**
				 * Method to verify that an error message is obtained in the response when the profile ID is missing in the Delete Comment request 
				 **/

				String testName = "deleteComment_38510";
				WSClient.startTest(testName,
						"Verify that an error message is obtained in the response when the profile ID is missing in the Delete Comment request", "minimumRegression");

				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				
				//******** Setting the OWS Header *************//
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());

				
				
				if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
					WSClient.setData("{var_resort}", resort);
					WSClient.setData("{var_commentType}",OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
					WSClient.setData("{var_comment}", WSClient.getKeywordData("{KEYWORD_RANDSTR_8}"));
					

					//******** OWS Delete Comment *************//
						String deleteCommentReq = WSClient.createSOAPMessage("OWSDeleteComment", "DS_02");
						String deleteCommentRes = WSClient.processSOAPMessage(deleteCommentReq);
						if(WSAssert.assertIfElementExists(deleteCommentRes, "Result_Text_TextElement",true)) 
						{
							
							/**** Verifying that the error message is populated on the response ********/
							
							String message=WSAssert.getElementValue(deleteCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
						}
						
						if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode",true)) 
						{
											
											/**** Verifying whether the error Message is populated on the response ****/
											
											String message=WSAssert.getElementValue(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_resultStatusFlag", false))
						{
							/***Checking for the existence of the ResultStatusFlag**/
						
						WSAssert.assertIfElementValueEquals(deleteCommentRes,
								"DeleteCommentResponse_Result_resultStatusFlag", "FAIL", false);
						}
						else {
							 WSClient.writeToReport(LogStatus.FAIL,"Schema is incorrect");
						}
						
										
				}			
						}	
						
				 catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	
	@Test(groups =  { "minimumRegression","OWS","DeleteComment","Name"})

	public void deleteComment_38511() {
		try {
			/**
			 * Method to verify that an error message is obtained in the 
			 * response when the comment ID is missing in the Delete Comment request 
			 **/
			
			/*****
			 * * * PreRequisites Required: -->There should be a profile with comments
			 * 
			 *****/

			String testName = "deleteComment_38511";
			WSClient.startTest(testName,
					"Verify that an error message is obtained in the response when the comment ID is missing in the Delete Comment request", "minimumRegression");

			String resort = OPERALib.getResort();
			String operaProfileID="";
			String channel = OWSLib.getChannel();
			
			//******** Setting the OWS Header *************//
			
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_commentType}",OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_comment}", WSClient.getKeywordData("{KEYWORD_RANDSTR_8}"));
				WSClient.setData("{var_gender}", "M");
				
				//******** Prerequisite :Create Profile*************//
				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_10");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.PASS, "Profile ID :" + operaProfileID);
			

				     //******** OWS Delete Comment *************//
						
					String deleteCommentReq = WSClient.createSOAPMessage("OWSDeleteComment", "DS_03");
					String deleteCommentRes = WSClient.processSOAPMessage(deleteCommentReq);
					if(WSAssert.assertIfElementExists(deleteCommentRes, "Result_Text_TextElement",true)) 
					{
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(deleteCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode",true)) 
					{
										
										/**** Verifying whether the error Message is populated on the response ****/
										
						String message=WSAssert.getElementValue(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(deleteCommentRes,
							"DeleteCommentResponse_Result_resultStatusFlag", "FAIL", false);
						
					}
					else {
						WSClient.writeToReport(LogStatus.FAIL,"Schema is incorrect");
					}
					
									
			}			
					}	
		}
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
   @Test(groups =  { "minimumRegression","OWS","DeleteComment","Name"})

	public void deleteComment_38348() {
		try {
			/**
			 * Method to verify that an error message is generated on the response when invalid 
			operaID(ProfileID) is populated on the DeleteComment request for a configured channel.
			 **/

			String testName = "deleteComment_38348";
			WSClient.startTest(testName,
					"Verify that an error message is generated on the response when invalid operaID(ProfileID) is populated on the DeleteComment request for a configured channel.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			
			
			//******** Setting the OWS Header *************//
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_commentType}",OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_comment}", WSClient.getKeywordData("{KEYWORD_RANDSTR_8}"));
                String operaProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
                WSClient.setData("{var_profileID}", operaProfileID);
                String commentID=WSClient.getKeywordData("{KEYWORD_ID}");
                WSClient.setData("{var_commentID}", commentID);
                

				//******** OWS Delete Comment *************//
                	
					String deleteCommentReq = WSClient.createSOAPMessage("OWSDeleteComment", "DS_01");
					String deleteCommentRes = WSClient.processSOAPMessage(deleteCommentReq);
					if(WSAssert.assertIfElementExists(deleteCommentRes, "Result_Text_TextElement",true)) 
					{
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(deleteCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode",true)) 
					{
										
										/**** Verifying whether the error Message is populated on the response ****/
										
					String message=WSAssert.getElementValue(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(deleteCommentRes,
							"DeleteCommentResponse_Result_resultStatusFlag", "FAIL", false);
						
						
					}
					else {
						WSClient.writeToReport(LogStatus.INFO,"Schema is incorrect");
						
					}
					
									
			}			
					}	
					
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
   @Test(groups =  { "minimumRegression","OWS","DeleteComment","Name"})

	public void deleteComment_38509() {
		try {
			/**
			 * Method to verify that the error message is generated on the response when invalid 
 			commentID is populated on the DeleteComment request for a configured channel.
			 **/

			/*****
			 * * * PreRequisites Required: -->There should be a profile with comments
			 * 
			 *****/
			
			String testName = "deleteComment_38509";
			WSClient.startTest(testName,
					"Verify that the error message is generated on the response when invalid commentID is populated on the DeleteComment request for a configured channel.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String operaProfileID="";
			
			//******** Setting the OWS Header *************//
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.setData("{var_resort}", resort);
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"NoteType"})){
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_commentType}",OperaPropConfig.getDataSetForCode("NoteType", "DS_01"));
				WSClient.setData("{var_comment}", WSClient.getKeywordData("{KEYWORD_RANDSTR_8}"));
				 WSClient.setData("{var_gender}", "M");
				
				//******** Prerequisite :Create Profile*************//
				 
				 if (operaProfileID.equals(""))
						operaProfileID = CreateProfile.createProfile("DS_10");
					if (!operaProfileID.equals("error")) {
						WSClient.setData("{var_profileID}", operaProfileID);
						WSClient.writeToReport(LogStatus.PASS, "Profile ID :" + operaProfileID);
						WSClient.setData("{var_commentID}",WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));

					
				    //******** OWS Delete Comment *************//
					String deleteCommentReq = WSClient.createSOAPMessage("OWSDeleteComment", "DS_01");
					String deleteCommentRes = WSClient.processSOAPMessage(deleteCommentReq);
                    if(WSAssert.assertIfElementExists(deleteCommentRes, "Result_Text_TextElement",true)) 
					{
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(deleteCommentRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode",true)) 
					{
										
										/**** Verifying whether the error Message is populated on the response ****/
										
										String message=WSAssert.getElementValue(deleteCommentRes, "DeleteCommentResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Delete Comment response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(deleteCommentRes, "DeleteCommentResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(deleteCommentRes,
							"DeleteCommentResponse_Result_resultStatusFlag", "FAIL", false);
					}
					
					else {
						WSClient.writeToReport(LogStatus.INFO, "The schema is incorrect");
					}
					}
									
			}			
					
		}
					
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
}
	
	
				
				
				


