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

public class DeletePreference extends WSSetUp{
	
	// Total Test Cases : 8
	
	//Creating Profile 
	public String createProfile(String ds) 
	{
			String profileID = "";
			try 
			{
				String resortOperaValue = OPERALib.getResort();
                String chain=OPERALib.getChain();
                
                WSClient.setData("{var_resort}", resortOperaValue);
                WSClient.setData("{var_chain}",chain);

                
                String uname = OPERALib.getUserName();
                OPERALib.setOperaHeader(uname);
                
                //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Profile"+"</b>");
                
                profileID = CreateProfile.createProfile(ds);
                WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
                
			}
			catch(Exception e) 
			{
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
			}
			return profileID;
	}
	
	public void setOWSHeader() {
				try {
				String resort = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String channel = OWSLib.getChannel();
		        String pwd = OPERALib.getPassword();
		        String channelType = OWSLib.getChannelType(channel);
		        String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		        OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				}catch(Exception e) {
					WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
				}
			}

	// Sanity Test Case :1 
	@Test(groups={"sanity","Name","DeletePreference","OWS"})
	public void deletePreference_38440() {
		try {
			String testname = "deletePreference_38440";
			WSClient.startTest(testname, "Verify that preference associated with a profile is deleted by passing valid values in mandatory fields", "sanity");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				
				String UniqueId;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
						
						WSClient.setData("{var_profileID}", UniqueId);
						WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
						WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
						WSClient.setData("{var_global}", "false");
						
						// Creating a Preference for above created profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
							
							setOWSHeader();
				            
							String resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
							
							// Creating request and processing response for OWS DeletePreference Operation
				            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_01");
							String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
				            
							
							// Validating response of OWS DeletePreference Operation
							if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "SUCCESS", false)) {
			
										// Database Validation
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query = WSClient.getQuery("QS_01");
										db = WSClient.getDBRow(query);
										if(db.isEmpty()) {
											WSClient.writeToReport(LogStatus.PASS, "Preference Deleted");
										}else {
											WSClient.writeToReport(LogStatus.FAIL, "Preference not Deleted");
										}
								}else {
									if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
										if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
										}
									}
									if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
						}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 
	
	// MR Test Case :1 
	@Test(groups={"minimumRegression","Name","DeletePreference","OWS"})
	public void deletePreference_38584() {
		try {
			String testname = "deletePreference_38584";
			WSClient.startTest(testname, "Verify that error message is obtained while passing profile id as EXTERNAL", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				
				
				String UniqueId;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{		
				
						WSClient.setData("{var_profileID}", UniqueId);
						WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
						WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
						WSClient.setData("{var_global}", "false");
						
						// Creating a Preference for above created profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
							
							setOWSHeader();
				            
							String resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
							
							// Creating request and processing response for OWS DeletePreference Operation
				            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_02");
							String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
				            
							// Validating response of OWS DeletePreference Operation
							if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "FAIL", false)) {
	
										if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", false)) {																				
											if(WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "This instance does not support EXTERNAL NameID values.", true)) {
												WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "This instance does not support EXTERNAL NameID values.", false);
											}else if(WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).indexOf("This instance")>=0 && WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).substring(WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).indexOf("This instance")).equals("This instance does not support External values")){
												WSAssert.assertIfElementContains(DeletePreferenceResponseXML, "Result_Text_TextElement", "This instance does not support External values", false);
											}
											else {
											
												WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
												String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
											}
										}
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
											}
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
												String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
											}
										
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
						}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 
	
	//MR Test Case :2
	@Test(groups={"minimumRegression","Name","DeletePreference","OWS"})
	public void deletePreference_38585() {
		try {
			String testname = "deletePreference_38585";
			WSClient.startTest(testname, "Verify that preference associated with a profile is deleted while passing invalid source", "minimumRegression");
			
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				
				
				String UniqueId;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{	
	
						WSClient.setData("{var_profileID}", UniqueId);
						WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
						WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
						WSClient.setData("{var_global}", "false");
						
						// Creating a Preference for above created profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
							
							setOWSHeader();
				            
							String resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
							
							// Creating request and processing response for OWS DeletePreference Operation
				            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_03");
							String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
				            
							// Validating response of OWS DeletePreference Operation
							if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "SUCCESS", false)) {
			
										// Database Validation
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query = WSClient.getQuery("QS_01");
										db = WSClient.getDBRow(query);
										if(db.isEmpty()) {
											WSClient.writeToReport(LogStatus.INFO, "Preference Deleted");
										}else {
											WSClient.writeToReport(LogStatus.FAIL, "Preference not Deleted");
										}
								}else {
									if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
									}
										if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
										}
									
									if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
						}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	} 

	//MR Test Case :3
	@Test(groups={"minimumRegression","Name","DeletePreference","OWS"})
	public void deletePreference_38586() {
		try {
			String testname = "deletePreference_38586";
			WSClient.startTest(testname, "Verify that error message is obtained while not passing any nameid", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				
				String UniqueId;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{	
						
						WSClient.setData("{var_profileID}", UniqueId);
						WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
						WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
						WSClient.setData("{var_global}", "false");
						
						// Creating a Preference for above created profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
							
							setOWSHeader();
				            
							String resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
							
							// Creating request and processing response for OWS DeletePreference Operation
				            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_04");
							String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
				            
							// Validating response of OWS DeletePreference Operation
							if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "FAIL", false)) {
									
										if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", false)) {
											
											
											if(WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "Name Id is null", true)) {
												WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "Name Id is null", false);
											}else if(WSAssert.assertIfElementContains(DeletePreferenceResponseXML, "Result_Text_TextElement", "UniqueID is null", false)) {
												
											}
										}
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
											}
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
												String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
											}
											
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
						}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	//MR Test Case :4
	@Test(groups={"minimumRegression","Name","DeletePreference","OWS"})
	public void deletePreference_38588() {
		try {
			String testname = "deletePreference_38588";
			WSClient.startTest(testname, "Verify that error message is obtained while not passing any unmatching preference type and preference value", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				
				String UniqueId;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{	
						
						WSClient.setData("{var_profileID}", UniqueId);
						WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
						WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
						WSClient.setData("{var_global}", "false");
						
						// Creating a Preference for above created profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
							
							setOWSHeader();
				            
							String resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
							
							// Changing Preference Value
				            WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
				            
				            // Creating request and processing response for OWS DeletePreference Operation
				            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_01");
							String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
				            
							// Validating response of OWS DeletePreference Operation
							if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "FAIL", true) || WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "WARNING", true)) {
										
										if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", false)) {
											
											if(WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "Invalid Preference Value.", true)) {
												WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "Invalid Preference Value.",false);
											}else if(WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).indexOf("QA_TUBWR")>=0 && WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).substring(0,WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).indexOf("QA_TUBWR")).equals("Unable to find preference ")) {
												WSAssert.assertIfElementContains(DeletePreferenceResponseXML, "Result_Text_TextElement", "Unable to find preference", false);
											}else {
												WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
												String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
											}
										}
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
											}
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
												String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
											}
											
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
						}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	
	//MR Test Case :5
	@Test(groups={"minimumRegression","Name","DeletePreference","OWS"})
	public void deletePreference_38589() {
		try {
			String testname = "deletePreference_38589";
			WSClient.startTest(testname, "Verify that preference associated with a profile is deleted while not passing hotels in request", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
				
				String UniqueId;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{	
						
						WSClient.setData("{var_profileID}", UniqueId);
						WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
						WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
						WSClient.setData("{var_global}", "false");

						// Creating a Preference for above created profile
						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
						
						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
							
							setOWSHeader();
							
							String resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
							
							// Creating request and processing response for OWS DeletePreference Operation
				            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_05");
							String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
				            
							// Validating response of OWS DeletePreference Operation
							if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "SUCCESS", false)) {
			
										// Database Validation
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query = WSClient.getQuery("QS_01");
										db = WSClient.getDBRow(query);
										if(db.isEmpty()) {
											WSClient.writeToReport(LogStatus.INFO, "Preference Deleted");
										}else {
											WSClient.writeToReport(LogStatus.FAIL, "Preference not Deleted");
										}
								}else {
									if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
									}
										if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
										}
									
									if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
						}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	
	//MR Test Case :6
		@Test(groups={"minimumRegression","Name","DeletePreference","OWS"})
	public void deletePreference_40262() {
			try {
				String testname = "deletePreference_40262";
				WSClient.startTest(testname, "Verify that error message is obtained while passing preference type and preference value not associated to profile", "minimumRegression");
				
				if (OperaPropConfig.getPropertyConfigResults(
	                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
					
					String UniqueId;
					
					// Creating a profile
					if(!(UniqueId = createProfile("DS_01")).equals("error"))
					{	
							
							WSClient.setData("{var_profileID}", UniqueId);
							WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
							WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
							WSClient.setData("{var_global}", "false");

							// Creating a Preference for above created profile
							String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
							String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
							
							if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
								
								setOWSHeader();
					            
								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
								
					            WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
					            WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));

					         // Creating request and processing response for OWS DeletePreference Operation
					            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_01");
								String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
					            
								// Validating response of OWS DeletePreference Operation
								if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
										if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "FAIL", false)) {
	
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", false)) {
												if(WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "Record does not exist.", true)) {
													WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "Record does not exist.", false);
												}else if(WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).indexOf("QA_SHRWR")>=0 && WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).substring(0,WSClient.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE).indexOf("QA_SHRWR")).equals("Unable to find preference ")) {
													WSAssert.assertIfElementContains(DeletePreferenceResponseXML, "Result_Text_TextElement", "Unable to find preference", false);
												}else {
													WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
													String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
												}
											}
													if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
														String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
													}
												if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
													String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
												}
											
											
												
									}else {
										WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
							}
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
				}
			} catch (Exception e) {
				
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
			}
		}
	
	//	MR Test Case :7
		@Test(groups={"minimumRegression","Name","DeletePreference","OWS"})
	public void deletePreference_40265() {
			try {
				String testname = "deletePreference_40265";
				WSClient.startTest(testname, "Verify that error message is obtained while not passing preference type and preference value", "minimumRegression");
				
				if (OperaPropConfig.getPropertyConfigResults(
	                    new String[] { "PreferenceGroup", "PreferenceCode" })) {
					
					String UniqueId;
					
					// Creating a profile
					if(!(UniqueId = createProfile("DS_01")).equals("error"))
					{	
							
							WSClient.setData("{var_profileID}", UniqueId);
							WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
							WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
							WSClient.setData("{var_global}", "false");

							// Creating a Preference for above created profile
							String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
							String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
							
							if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
								
								setOWSHeader();
					            
								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
								
								// Creating request and processing response for OWS DeletePreference Operation
					            String DeletePreferenceReq = WSClient.createSOAPMessage("OWSDeletePreference", "DS_06");
								String DeletePreferenceResponseXML = WSClient.processSOAPMessage(DeletePreferenceReq);
					            
								// Validating response of OWS DeletePreference Operation
								if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", true)) {
										if (WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML,"DeletePreferenceResponse_Result_resultStatusFlag", "FAIL", false)) {
								
											if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", true) && WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "Result_Text_TextElement", "Preference type is a required element.", false)) {
												
											}else if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true) && WSAssert.assertIfElementValueEquals(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", "Preference type is a required element.", false)) {
												
											}else {
												WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
												if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "Result_Text_TextElement", true)) {
													String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
												}
													if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", true)) {
														String operaErrorCode=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
													}
												
												if(WSAssert.assertIfElementExists(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", true)) {
													String message=WSAssert.getElementValue(DeletePreferenceResponseXML, "DeletePreferenceResponse_Result_GDSError", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
												}
											}	
									}else {
										WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
							}
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
				}
			} catch (Exception e) {
				
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
			}
		}
				
}
