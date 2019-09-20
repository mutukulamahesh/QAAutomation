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

public class InsertPreference extends WSSetUp{
	
	/**
	 * @author ketvaidy
	 */
	
	@Test(groups = { "sanity","OWS","InsertPreference","Name"})
	
	/***** This method is used to verify that the given Preference
	 *  is inserted for a given profile ID for a configured 
	 *  channel with minimum data.*****/
	
	/*****
	 * * * PreRequisites Required: -->There should be a valid existing profile.
	 * 
	 *****/
	
	public void insertPreference_38381() {
		try {
			//
			String testName = "insertPreference_38381";
			WSClient.startTest(testName,
					"Verify that a preference is inserted when minimum data in provided in request.", "sanity");
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
			String operaProfileID="";
           //******** Setting the OWS Header *************//
			
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
			WSClient.setData("{var_resort}", resort);
			
			
          //******** Prerequisite :Create Profile*************//
			
			if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
			
			
					WSClient.setData("{var_preferenceType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
					WSClient.setData("{var_preferenceValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));

					//******** OWS Insert Preference *************//
					
					String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_01");
					String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) {
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
						}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					if (WSAssert.assertIfElementValueEquals(insertPreferenceRes,
							"InsertPreferenceResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						
						LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
						LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
						HashMap<String, String> xpath=new HashMap<String, String>();
	                	xpath.put("InsertPreferenceRequest_Preference_preferenceType", "InsertPreferenceRequest_Preference");
	                	xpath.put("InsertPreferenceRequest_Preference_preferenceValue", "InsertPreferenceRequest_Preference");
	                	xpath.put("InsertPreferenceRequest_NameID", "_InsertPreferenceRequest");
	                	String query1=WSClient.getQuery("QS_01");
						db =WSClient.getDBRow(query1);
						actualValues = WSClient.getSingleNodeList(insertPreferenceReq,xpath,false,XMLType.REQUEST);
						WSAssert.assertEquals(actualValues, db,false);
							
							
					
						
					
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Insert Preference Operation Unsuccessful");
					}
					}
					else {
						/**The ResultStatusFlag not found.This indicates an error in the schema ****/
						WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
					}
					    /***The database validations are being performed.***/
					
					   
						
						
					
				} }
			
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
	
@Test(groups = { "minimumRegression","OWS","InsertPreference","Name" })
	
	/***** This method is used to verify if an error message is 
	 * populated in the response when the Profile ID is not passed 
	 * in the request for a configured channel*****/
	
	public void insertPreference_38447() {
		try {
			//
			String testName = "insertPreference_38447";
			WSClient.startTest(testName,
					"Verify that an error message is obtained in the response when no Profile ID is sent in the request", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
			
           //******** Setting the OWS Header *************//
			
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
			
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_preferenceType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
			WSClient.setData("{var_preferenceValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
         
					//******** OWS Insert Preference *************//
					
					String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_02");
					String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(insertPreferenceRes,
							"InsertPreferenceResponse_Result_resultStatusFlag", "FAIL",true); 
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) 
					{
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) 
					{
										
										/**** Verifying whether the error Message is populated on the response ****/
										
										String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
									
								
					}}	
					
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


	@Test(groups = { "minimumRegression","OWS","InsertPreference","Name" })
	
	/***** This method is used to verify whether an error message is 
	 * populated in the response when the Preference Type is missing in the 
	 * Insert Preference request for a configured channel.*****/
	
	/*****
	 * * * PreRequisites Required: -->There should be a valid existing profile.
	 * 
	 *****/
	
	public void insertPreference_38452() {
		try {
			//
			String testName = "insertPreference_38452";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when no Preference Type is passed in the request", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
			String operaProfileID="";
			
           //******** Setting the OWS Header *************//
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
			
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_preferenceValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
			
          //******** Prerequisite :Create Profile*************//
			
			if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");

					

					//******** OWS Insert Preference *************//
					
					String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_03");
					String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) {
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
						}
					
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) {
										
										/**** Verifying whether the error Message is populated on the response ****/
										
										String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
									}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(insertPreferenceRes,
							"InsertPreferenceResponse_Result_resultStatusFlag", "FAIL", false);
					}
					else {
						/**The ResultStatusFlag not found.This indicates an error in the schema ****/
						WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
					}
					
				} }}
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
	@Test(groups = { "minimumRegression","OWS","InsertPreference","Name" })
	
	/***** This method is used to verify whether an error message is populated in the 
	 * response when the Preference Value is missing in the 
	 * Insert Preference request for a configured channel.*****/
	
	/*****
	 * * * PreRequisites Required: -->There should be a valid existing profile.
	 * 
	 *****/
	public void insertPreference_38480() {
		try {
			//
			String testName = "insertPreference_38480";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when no Preference Value is sent in the request", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
			String operaProfileID="";
			
           //******** Setting the OWS Header *************//
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_preferenceType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
			
			
          //******** Prerequisite :Create Profile*************//
			
			if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
			

					//******** OWS Insert Preference *************//
					
					String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_04");
					String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) {
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
						}
					
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) {
										
										/**** Verifying whether the error Message is populated on the response ****/
										
										String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
									}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(insertPreferenceRes,
							"InsertPreferenceResponse_Result_resultStatusFlag", "FAIL", false); 
					}
					else {
						/**The ResultStatusFlag not found.This indicates an error in the schema ****/
						WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
					}
					
				} }}
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


	@Test(groups = { "minimumRegression","OWS","InsertPreference","Name" })

	/***** This method is used to verify whether an error message is 
	 * populated in the response when the Preference Value is missing in 
	 * the Insert Preference request for a configured channel.*****/

	public void insertPreference_38469() {
		try {
			//
			String testName = "insertPreference_38469";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when an invalid profile ID is sent in the request", "minimumRegression");
	
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
			
	       //******** Setting the OWS Header *************//
			
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_preferenceType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
			WSClient.setData("{var_preferenceValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
			
	      //******** Prerequisite :Create Profile*************//
			
			String operaProfileID=WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
	        WSClient.setData("{var_profileID}", operaProfileID);
	
					//******** OWS Insert Preference *************//
					
					String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_01");
					String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) {
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
						}
					
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) {
										
					/**** Verifying whether the error Message is populated on the response ****/
										
										String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
									}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(insertPreferenceRes,
							"InsertPreferenceResponse_Result_resultStatusFlag", "FAIL", false); 
					}
					else {
						/**The ResultStatusFlag not found.This indicates an error in the schema ****/
						WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
					}
					
				}}
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
}


	@Test(groups = { "minimumRegression","OWS","InsertPreference","Name" })
	
	/***** This method is used to verify if an error message is populated in the response when an invalid Preference Type is sent in the Insert Preference Request for a configured channel.*****/
	
	/*****
	 * * * PreRequisites Required: -->There should be a valid existing profile.
	 * 
	 *****/
	public void insertPreference_38481() {
		try {
			//
			String testName = "insertPreference_38481";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when an invalid Preference Type is sent in the request", "minimumRegression");
	
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
			String operaProfileID="";
			
	       //******** Setting the OWS Header *************//
			
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
	        WSClient.setData("{var_preferenceType}",WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
			WSClient.setData("{var_preferenceValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
	        
	        
	      //******** Prerequisite :Create Profile*************//
			
			if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
			
	
					//******** OWS Insert Preference *************//
					
					String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_05");
					String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) {
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
						}
					
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) {
										
										/**** Verifying whether the error Message is populated on the response ****/
										
										String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
									}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					WSAssert.assertIfElementValueEquals(insertPreferenceRes,
							"InsertPreferenceResponse_Result_resultStatusFlag", "FAIL", false); 
					}
					else {
						/**The ResultStatusFlag not found.This indicates an error in the schema ****/
						WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
					}
					
				} }}
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
  }



		@Test(groups = { "minimumRegression","OWS","InsertPreference","Name" })
		
		/***** This method is used to verify if an error message is populated in the response when an invalid Preference Value is sent in the Insert Preference Request for a configured channel.*****/
		
		/*****
		 * * * PreRequisites Required: -->There should be a valid existing profile.
		 * 
		 *****/
		public void insertPreference_38501() {
			try {
				//
				String testName = "insertPreference_38501";
				WSClient.startTest(testName,
						"Verify that an error message is populated in the response when an invalid Preference Value is sent in the request", "minimumRegression");
		
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsResort=OWSLib.getChannelResort(resort, channel);
				WSClient.setData("{var_owsResort}",owsResort);
				String operaProfileID="";
				
		       //******** Setting the OWS Header *************//
				
				if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_preferenceType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
				
		      //******** Prerequisite :Create Profile*************//
				
				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				
		                WSClient.setData("{var_preferenceValue}",WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
		
						//******** OWS Insert Preference *************//
						
						String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_06");
						String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
						if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) {
							
							/**** Verifying that the error message is populated on the response ********/
							
							String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
							}
						
						if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) {
											
											/**** Verifying whether the error Message is populated on the response ****/
											
											String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
										}
						if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
						{
							/***Checking for the existence of the ResultStatusFlag**/
						
						WSAssert.assertIfElementValueEquals(insertPreferenceRes,
								"InsertPreferenceResponse_Result_resultStatusFlag", "FAIL", false); 
						}
						else {
							/**The ResultStatusFlag not found.This indicates an error in the schema ****/
							WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
						}
						
					} }}
				 catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	

	@Test(groups = { "minimumRegression","OWS","InsertPreference","Name"})
	
	/***** This method is used to verify that the Preference is 
	 * inserted when all the fields are given in the InsertPreference 
	 * request for a configured channel.*****/
	
	/*****
	 * * * PreRequisites Required: -->There should be a valid existing profile.
	 * 
	 *****/
	
	public void insertPreference_38500() {
		try {
			//
			String testName = "insertPreference_38500";
			WSClient.startTest(testName,
					"Verify that the preference is inserted when all the fields are given in the request", "minimumRegression");
	
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
			String operaProfileID="";
			
	       //******** Setting the OWS Header *************//
			
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"PreferenceGroup","PreferenceCode"})){
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_preferenceType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
			WSClient.setData("{var_preferenceValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
			
			//******** Prerequisite :Create Profile*************//
			
			if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileID}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
			
	
					//******** OWS Insert Preference *************//
					
					String insertPreferenceReq = WSClient.createSOAPMessage("OWSInsertPreference", "DS_08");
					String insertPreferenceRes = WSClient.processSOAPMessage(insertPreferenceReq);
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "Result_Text_TextElement",true)) {
						
						/**** Verifying that the error message is populated on the response ********/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
						}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError",true)) {
						
						/**** Verifying whether the error Message is populated on the response ****/
						
						String message=WSAssert.getElementValue(insertPreferenceRes, "InsertPreferenceResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Insert Preference response is :"+ message+"</b>");
					}
					if(WSAssert.assertIfElementExists(insertPreferenceRes, "InsertPreferenceResponse_Result_resultStatusFlag", false))
					{
						/***Checking for the existence of the ResultStatusFlag**/
					
					if (WSAssert.assertIfElementValueEquals(insertPreferenceRes,
							"InsertPreferenceResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						
						
					
						LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
						LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
						HashMap<String, String> xpath=new HashMap<String, String>();
	                	xpath.put("InsertPreferenceRequest_Preference_preferenceType", "InsertPreferenceRequest_Preference");
	                	xpath.put("InsertPreferenceRequest_Preference_preferenceValue", "InsertPreferenceRequest_Preference");
	                	xpath.put("InsertPreferenceRequest_NameID", "_InsertPreferenceRequest");
	                	String query=WSClient.getQuery("QS_02");
						db =WSClient.getDBRow(query);
						actualValues = WSClient.getSingleNodeList(insertPreferenceReq,xpath,false,XMLType.REQUEST);
						if(WSAssert.assertEquals(actualValues, db,false)) {
							
							
						}
			           else
						{
							WSClient.writeToReport(LogStatus.FAIL, "The Preference Details were not correctly given" );
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Insert Preference Operation Unsuccessful");
					}
					}
					else {
						/**The ResultStatusFlag not found.This indicates an error in the schema ****/
						
						WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
					}
					    /***The database validations are being performed.The operation may or may not have been successful***/
					
					    
					
						
					
				} }}
			 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

		
			

}
