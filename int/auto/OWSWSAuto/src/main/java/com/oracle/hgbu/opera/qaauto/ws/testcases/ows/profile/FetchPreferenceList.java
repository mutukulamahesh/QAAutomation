package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;
import java.util.ArrayList;
import java.util.HashMap;
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

public class FetchPreferenceList  extends WSSetUp {
	
	/**
	 * @author ketvaidy
	 */
	
	@Test(groups={"sanity","OWS","Name","FetchPreferenceList"})
	
	/***** Verify that the list of preferences are retrieved for a profile when minimum data is passed in the request*****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with preferences attached to it
	 * 
	 *****/
	public void fetchPreferenceList_39840() {
		try {
			String testname = "fetchPreferenceList_39840";
			WSClient.startTest(testname, "Verify that the list of preferences associated to a profile ID are correctly retrieved", "sanity");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				String uname = OPERALib.getUserName();
				String resort = OPERALib.getResort();
				String operaProfileID="";
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
				WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
				WSClient.setData("{var_global}", "false");
				String prefDesc = "Get Water";
				WSClient.setData("{var_prefDesc}",prefDesc);
				String chain=OPERALib.getChain();
				
				// Creating Profile
				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
					   WSClient.setData("{var_chain}", chain);
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_channel}", channel);
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						
						// Creating a preference for the profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true)) {
							
				            WSClient.writeToReport(LogStatus.INFO,"<b>Successfully created a Preference"+"</b>");
				            // Delete preference of the profile
				            String FetchPreferenceListReq = WSClient.createSOAPMessage("OWSFetchPreferenceList", "DS_01");
							String FetchPreferenceListRes = WSClient.processSOAPMessage(FetchPreferenceListReq);
				            
							
							if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(FetchPreferenceListRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is:"+ operaErrorCode+"</b>");
								}
							
													// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(FetchPreferenceListRes,"FetchPreferenceListResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(FetchPreferenceListRes,"FetchPreferenceListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
			
										
										LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
										LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
										HashMap<String,String> xpath=new HashMap<String,String>();
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_resortCode", "FetchPreferenceListResponse_PreferenceList_Preference");
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_preferenceType", "FetchPreferenceListResponse_PreferenceList_Preference");
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_preferenceValue", "FetchPreferenceListResponse_PreferenceList_Preference");
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_updateUser", "FetchPreferenceListResponse_PreferenceList_Preference");
								       
								        String query1=WSClient.getQuery("QS_01");
								        db =WSClient.getDBRow(query1);
								        actualValues = WSClient.getSingleNodeList(FetchPreferenceListRes,xpath,false,XMLType.RESPONSE);
								       WSAssert.assertEquals(db,actualValues,false);
								        	 

								        	}
								        	else
								        	{
								        	 WSClient.writeToReport(LogStatus.FAIL, "Fetch Preference List Operation Unsuccessful");
								        	}
								        	}
								        	else {
								        	 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
								        	 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
								        	}
								        	    
								}
							
						else {
							WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a Preference**********");
						}}
					
				
			}}
				
		 catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 
	
	@Test(groups={"minimumRegression","OWS","Name","FetchPreferenceList"})
	public void fetchPreferenceList_40275() {
		
		
		try {
			String testname = "fetchPreferenceList_40275";
			WSClient.startTest(testname, "Verify that an error message is populated when no profile ID is passed in the request", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				String uname = OPERALib.getUserName();
				String resort = OPERALib.getResort();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
				WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
				WSClient.setData("{var_global}", "false");
				String prefDesc = "Get Water";
				WSClient.setData("{var_prefDesc}",prefDesc);
				
				
						String channel = OWSLib.getChannel();
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

					
				            
				            String FetchPreferenceListReq = WSClient.createSOAPMessage("OWSFetchPreferenceList", "DS_02");
							String FetchPreferenceListRes = WSClient.processSOAPMessage(FetchPreferenceListReq);
				            
							
							if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(FetchPreferenceListRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is:"+ operaErrorCode+"</b>");
								}
							
							
							// Validation for OWS Operation
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_resultStatusFlag", false))
						    {
						     /***Checking for the existence of the ResultStatusFlag**/

						    	 WSAssert.assertIfElementValueEquals(FetchPreferenceListRes,
									      "FetchPreferenceListResponse_Result_resultStatusFlag", "FAIL", false);
								        	 
								        	}
								        	else {
								        	 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
								        	 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
								        	}
								        	    
								}
							
						
					
				
			}
				
		 catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 
	
	
	@Test(groups={"minimumRegression","OWS","Name","FetchPreferenceList"})
	
	/***** Verify that an error message is populated when an invalid Profile ID is passed in the request*****/
	
	public void fetchPreferenceList_40276() {
		try {
			String testname = "fetchPreferenceList_40276";
			WSClient.startTest(testname, "Verify that an error message is populated when an invalid Profile ID is passed in the request", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				String uname = OPERALib.getUserName();
				String resort = OPERALib.getResort();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
				WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
				WSClient.setData("{var_global}", "false");
				String prefDesc = "Get Water";
				WSClient.setData("{var_prefDesc}",prefDesc);
				WSClient.setData("{var_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
				
						String channel = OWSLib.getChannel();
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						
				            String FetchPreferenceListReq = WSClient.createSOAPMessage("OWSFetchPreferenceList", "DS_01");
							String FetchPreferenceListRes = WSClient.processSOAPMessage(FetchPreferenceListReq);
				            
							
							if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(FetchPreferenceListRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is:"+ operaErrorCode+"</b>");
								}
							
							
							// Validation for OWS Operation
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_resultStatusFlag", false))
						    {
						     /***Checking for the existence of the ResultStatusFlag**/

						    WSAssert.assertIfElementValueEquals(FetchPreferenceListRes,
						      "FetchPreferenceListResponse_Result_resultStatusFlag", "FAIL", false);
								        	 
								        	}
								        	else {
								        	 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
								        	 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
								        	}
								        	    
								}}
							
		
			catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 
	
	
	
	
	@Test(groups={"minimumRegression","OWS","Name","FetchPreferenceList"})
	
	/*****Verify that the list of preferences are retrieved for a profile ID with multiple preferences in the request*****/
	/*****
	 * * * PreRequisites Required: -->There should be a profile with preferences attached to it
	 * 
	 *****/
	
	public void fetchPreferenceList_40278() {
		try {
			String testname = "fetchPreferenceList_40278";
			WSClient.startTest(testname, "Verify that the list of preferences(multiple) associated with a profile ID are correctly retrieved", "minimumRegression");
			String operaProfileID="";
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				String uname = OPERALib.getUserName();
				String resort = OPERALib.getResort();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
				WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
				WSClient.setData("{var_global}", "false");
				String prefDesc = "Get Water";
				WSClient.setData("{var_prefDesc}",prefDesc);
				
				// Creating Profile
				if (operaProfileID.equals(""))
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_channel}", channel);
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						
						// Creating a preference for the profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>Created the first preference"+ "</b>");
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true)) {
							WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
							WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
							WSClient.setData("{var_global}", "false");
							prefDesc = "Shower Water";
							WSClient.setData("{var_prefDesc}",prefDesc);
							String createPreferenceReq2 = WSClient.createSOAPMessage("CreatePreference", "DS_01");
							String createPreferenceResponseXML2 = WSClient.processSOAPMessage(createPreferenceReq2);
							if (WSAssert.assertIfElementExists(createPreferenceResponseXML2, "CreatePreferenceRS_Success", true)) {

						    WSClient.writeToReport(LogStatus.INFO, "<b>Created the second preference"+ "</b>");
				            // Delete preference of the profile
				            String FetchPreferenceListReq = WSClient.createSOAPMessage("OWSFetchPreferenceList", "DS_01");
							String FetchPreferenceListRes = WSClient.processSOAPMessage(FetchPreferenceListReq);
				            
							
							if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(FetchPreferenceListRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is:"+ operaErrorCode+"</b>");
								}
							
							
							// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(FetchPreferenceListRes,"FetchPreferenceListResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(FetchPreferenceListRes,"FetchPreferenceListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
			
										
										
										List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
										List<LinkedHashMap<String,String>> actualValues= new ArrayList<LinkedHashMap<String,String>>();
										HashMap<String,String> xpath=new HashMap<String,String>();
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_resortCode", "FetchPreferenceListResponse_PreferenceList_Preference");
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_preferenceType", "FetchPreferenceListResponse_PreferenceList_Preference");
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_preferenceValue", "FetchPreferenceListResponse_PreferenceList_Preference");
								        xpath.put("FetchPreferenceListResponse_PreferenceList_Preference_updateUser", "FetchPreferenceListResponse_PreferenceList_Preference");
								        String query1=WSClient.getQuery("QS_01");
								        db =WSClient.getDBRows(query1);
								        actualValues = WSClient.getMultipleNodeList(FetchPreferenceListRes,xpath,false,XMLType.RESPONSE);
								       WSAssert.assertEquals(actualValues,db,false);
								        	 

								        	}
								        	else
								        	{
								        	 WSClient.writeToReport(LogStatus.FAIL, "Fetch Preference List Operation Unsuccessful");
								        	}
								        	}
								        	else {
								        	 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
								        	 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
								        	}
								        	    
								}
							
						else {
							WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create the second Preference**********");
						}
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create the first Preference**********");
						}
					} }
				
			}
				
		 catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 
	
@Test(groups={"minimumRegression","OWS","Name","FetchPreferenceList"})
	
	/***** Verify that no information is displayed in the response when an invalid profile ID is sent in the request*****/
	
	public void fetchPreferenceList_HHOCM_86() {
		try {
			String testname = "fetchPreferenceList_HHOCM_86";
			WSClient.startTest(testname, "Verify that no information is displayed in the response when an invalid profile ID is sent in the request", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				String uname = OPERALib.getUserName();
				String resort = OPERALib.getResort();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
				WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
				WSClient.setData("{var_global}", "false");
				String prefDesc = "Get Water";
				WSClient.setData("{var_prefDesc}",prefDesc);
				WSClient.setData("{var_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
				
						String channel = OWSLib.getChannel();
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						
				            String FetchPreferenceListReq = WSClient.createSOAPMessage("OWSFetchPreferenceList", "DS_01");
							String FetchPreferenceListRes = WSClient.processSOAPMessage(FetchPreferenceListReq);
				            
							
							if(WSAssert.assertIfElementDoesNotExist(FetchPreferenceListRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(FetchPreferenceListRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is:"+ message+"</b>");
								//WSClient.writeToReport(LogStatus.INFO, "<b>Fetch preference text element fields are empty</b>");
								
							}
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Preference List response is:"+ operaErrorCode+"</b>");
								}
							
							
							// Validation for OWS Operation
						
						    
						    if(WSAssert.assertIfElementExists(FetchPreferenceListRes, "FetchPreferenceListResponse_Result_resultStatusFlag", false))
						    {
						     /***Checking for the existence of the ResultStatusFlag**/

						    WSAssert.assertIfElementValueEquals(FetchPreferenceListRes,
						      "FetchPreferenceListResponse_Result_resultStatusFlag", "SUCCESS", true);
								        	 
								        	}
								        	else {
								        	 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
								        	 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
								        	}
								        	    
								}}
							
		
			catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 

}
