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

public class InsertClaim extends WSSetUp{

	// Total Test Cases : 7
	
			//Creating Profile 
			
			
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
	
	
	    // Sanity Test Case : 1
		@Test(groups={"sanity","Name","OWS","InsertClaim"})
		public void insertClaim_39920() {
			try {
				String operaProfileID="";
				String testname = "insertClaim_39920";
				WSClient.startTest(testname, "Verify that a Claim is inserted to a profile by passing valid values in mandatory fields", "sanity");
				OPERALib.setOperaHeader(OPERALib.getUserName());
				
				String resortOperaValue = OPERALib.getResort();
				String chain=OPERALib.getChain();
                WSClient.setData("{var_resort}", resortOperaValue);
                WSClient.setData("{var_chain}",chain);
                WSClient.setData("{var_chaincode}", chain);
				
				// Creating a profile
				
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				
						WSClient.setData("{var_claimInfo}", "Card not issued");
						
						setOWSHeader();
					
						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
						
						// Creating request and processing response for OWS InsertClaim Operation
						String insertClaimReq = WSClient.createSOAPMessage("OWSInsertClaim", "DS_01");
						String insertClaimResponseXML = WSClient.processSOAPMessage(insertClaimReq);
						
						// Validating response of OWS InsertClaim Operation
						if(WSAssert.assertIfElementExists(insertClaimResponseXML,
								"InsertClaimResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(insertClaimResponseXML,
									"InsertClaimResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									// Database Validation
									LinkedHashMap<String,String> db = new LinkedHashMap<String,String>();
									LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
									xPath.put("InsertClaimRequest_Profile_NameID", "InsertClaimRequest_Profile");
									xPath.put("InsertClaimRequest_Claim_ClaimInformation", "InsertClaimRequest_Claim");
									expectedValues = WSClient.getSingleNodeList(insertClaimReq, xPath, false, XMLType.REQUEST);
									String query = WSClient.getQuery("QS_01");
									db = WSClient.getDBRow(query);
									WSAssert.assertEquals(expectedValues,db,false);
							}else {	
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the InsertClaim response is :"+ message+"</b>");
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode + "</b>");
									}
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The gds error displayed in the InsertClaim response is :"+ message + "</b>");
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in InsertClaim Response!");
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Fault_faultcode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Fault_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>FaultCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_errorCode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_errorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>ErrorCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_reason", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_reason", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reason : "+ message + "</b>");
								}
						}
				}	
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
			}
		}
		
		//MR Test Case : 1
		@Test(groups={"minimumRegression","Name","OWS","InsertClaim"})
		public void insertClaim_40020() {
			try {
				String testname = "insertClaim_40020";
				String operaProfileID=" ";
				WSClient.startTest(testname, "Verify that error message is obtained by passing nameid type as EXTERNAL.", "minimumRegression");
				String resortOperaValue = OPERALib.getResort();
				String chain=OPERALib.getChain();
                WSClient.setData("{var_resort}", resortOperaValue);
                WSClient.setData("{var_chain}",chain);
                WSClient.setData("{var_chaincode}", chain);
                OPERALib.setOperaHeader(OPERALib.getUserName());
				
				// Creating a profile
				
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				
						
						
						
						WSClient.setData("{var_claimInfo}", "Card not issued");
						
						setOWSHeader();
					
						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
						
						// Creating request and processing response for OWS InsertClaim Operation
						String insertClaimReq = WSClient.createSOAPMessage("OWSInsertClaim", "DS_02");
						String insertClaimResponseXML = WSClient.processSOAPMessage(insertClaimReq);
						
						// Validating response of OWS InsertClaim Operation
						if(WSAssert.assertIfElementExists(insertClaimResponseXML,
								"InsertClaimResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(insertClaimResponseXML,
									"InsertClaimResponse_Result_resultStatusFlag", "FAIL", false)) {	
								
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", false)) {
									if(WSAssert.assertIfElementValueEquals(insertClaimResponseXML, "Result_Text_TextElement", "This instance does not support EXTERNAL NameID values.", true)) {
										WSAssert.assertIfElementValueEquals(insertClaimResponseXML, "Result_Text_TextElement", "This instance does not support EXTERNAL NameID values.", false);
									}else if(WSAssert.assertIfElementContains(insertClaimResponseXML, "Result_Text_TextElement", "Invalid Name ID", false)) {
										
									}
								}else {
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(insertClaimResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the InsertClaim response is :"+ message + "</b>");
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode+"</b>");
										}
									}
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The gds error displayed in the InsertClaim response is :"+ message + "</b>");
									}
								}
							}else {	
								WSClient.writeToReport(LogStatus.FAIL, "Test Failed");	
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in InsertClaim Response!");
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Fault_faultcode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Fault_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>FaultCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_errorCode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_errorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>ErrorCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_reason", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_reason", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reason : "+ message + "</b>");
								}
						}
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
			}
		}
	
		// MR Test Case : 2
		@Test(groups={"minimumRegression","Name","OWS","InsertClaim"})
		public void insertClaim_40021() {
			try {
				String testname = "insertClaim_40021";
				String operaProfileID=" ";
				WSClient.startTest(testname, "Verify that a Claim is inserted to a profile by passing valid values in mandatory fields but passing nameid invalid source.", "minimumRegression");
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resortOperaValue = OPERALib.getResort();
				String chain=OPERALib.getChain();
                WSClient.setData("{var_resort}", resortOperaValue);
                WSClient.setData("{var_chain}",chain);
                WSClient.setData("{var_chaincode}", chain);
				
				// Creating a profile
				
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				
						
						
						
						WSClient.setData("{var_claimInfo}", "Card not issued");
						
						setOWSHeader();
					
						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
						
						// Creating request and processing response for OWS InsertClaim Operation
						String insertClaimReq = WSClient.createSOAPMessage("OWSInsertClaim", "DS_03");
						String insertClaimResponseXML = WSClient.processSOAPMessage(insertClaimReq);

						// Validating response of OWS InsertClaim Operation
						if(WSAssert.assertIfElementExists(insertClaimResponseXML,
								"InsertClaimResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(insertClaimResponseXML,
									"InsertClaimResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								// Database Validation
								LinkedHashMap<String,String> db = new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
								xPath.put("InsertClaimRequest_Profile_NameID", "InsertClaimRequest_Profile");
								xPath.put("InsertClaimRequest_Claim_ClaimInformation", "InsertClaimRequest_Claim");
								expectedValues = WSClient.getSingleNodeList(insertClaimReq, xPath, false, XMLType.REQUEST);
								String query = WSClient.getQuery("QS_01");
								db = WSClient.getDBRow(query);
								WSAssert.assertEquals(expectedValues,db,false);
							}else {	
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the InsertClaim response is :"+ message + "</b>");
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode + "</b>");
									}
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The gds error displayed in the InsertClaim response is :"+ message + "</b>");
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in InsertClaim Response!");
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Fault_faultcode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Fault_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>FaultCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_errorCode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_errorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>ErrorCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_reason", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_reason", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reason : "+ message + "</b>");
								}
						}				
					}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
			}
		}
		
		// MR Test Case : 3
		@Test(groups={"minimumRegression","Name","OWS","InsertClaim"})
		public void insertClaim_40022() {
			try {
				String testname = "insertClaim_40022";
				String operaProfileID= " ";
				WSClient.startTest(testname, "Verify that error message is obtained by passing nameid as empty.", "minimumRegression");
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resortOperaValue = OPERALib.getResort();
				String chain=OPERALib.getChain();
                WSClient.setData("{var_resort}", resortOperaValue);
                WSClient.setData("{var_chain}",chain);
                WSClient.setData("{var_chaincode}", chain);
				
				// Creating a profile
				
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				
						
						
						WSClient.setData("{var_claimInfo}", "Card not issued");
						
						setOWSHeader();
					
						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
						
						// Creating request and processing response for OWS InsertClaim Operation
						String insertClaimReq = WSClient.createSOAPMessage("OWSInsertClaim", "DS_04");
						String insertClaimResponseXML = WSClient.processSOAPMessage(insertClaimReq);
						
						// Validating response of OWS InsertClaim Operation
						if(WSAssert.assertIfElementExists(insertClaimResponseXML,
								"InsertClaimResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(insertClaimResponseXML,
									"InsertClaimResponse_Result_resultStatusFlag", "FAIL", false)) {
								if(WSAssert.assertIfElementValueEquals(insertClaimResponseXML, "Result_Text_TextElement", "User is null", false)) {
									
								}else {
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(insertClaimResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the InsertClaim response is :"+ message + "</b>");
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode + "</b>");
										}
									}
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The gds error displayed in the InsertClaim response is :"+ message + "</b>");
									}
								}
							}else {	
								WSClient.writeToReport(LogStatus.FAIL, "Test Failed");	
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in InsertClaim Response!");
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Fault_faultcode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Fault_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>FaultCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_errorCode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_errorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>ErrorCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_reason", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_reason", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reason : "+ message + "</b>");
								}
						}
					}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
			}
		} 
		
		// MR Test Case : 4
		@Test(groups={"minimumRegression","Name","OWS","InsertClaim"})
		public void insertClaim_40023() {
			try {
				String operaProfileID= " ";
				String testname = "insertClaim_40023";
				WSClient.startTest(testname, "Verify that error message is obtained by not passing claim information", "minimumRegression");
				
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resortOperaValue = OPERALib.getResort();
				String chain=OPERALib.getChain();
                WSClient.setData("{var_resort}", resortOperaValue);
                WSClient.setData("{var_chain}",chain);
                WSClient.setData("{var_chaincode}", chain);
                
				// Creating a profile
				
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				
						
						
						
						
						setOWSHeader();
					
						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
						
						// Creating request and processing response for OWS InsertClaim Operation
						String insertClaimReq = WSClient.createSOAPMessage("OWSInsertClaim", "DS_05");
						String insertClaimResponseXML = WSClient.processSOAPMessage(insertClaimReq);
						
						// Validating response of OWS InsertClaim Operation
						if(WSAssert.assertIfElementExists(insertClaimResponseXML,
								"InsertClaimResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(insertClaimResponseXML,
									"InsertClaimResponse_Result_resultStatusFlag", "FAIL", false)) {
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", false)) {
									if(WSAssert.assertIfElementValueEquals(insertClaimResponseXML, "Result_Text_TextElement", "Claim Information cannot be empty or null.", true)) {
										WSAssert.assertIfElementValueEquals(insertClaimResponseXML, "Result_Text_TextElement", "Claim Information cannot be empty or null.", false);
									}
									else if(WSAssert.assertIfElementContains(insertClaimResponseXML, "Result_Text_TextElement", "No Claim Information", false)) {
										
									}
								}else {
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(insertClaimResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertClaim response is :"+ message + "</b>");
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode + "</b>");
										}
									}
									if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the InsertClaim response is :"+ message + "</b>");
									}
								}
							}else {	
								WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in InsertClaim Response!");
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Fault_faultcode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Fault_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>FaultCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_errorCode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_errorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>ErrorCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_reason", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_reason", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reason : "+ message + "</b>");
								}
						}
					}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occurred in test case due to" + e);
			}
		}
		
		// MR Test Case : 5
				@Test(groups={"minimumRegression","Name","OWS","InsertClaim"})
		public void insertClaim_41283() {
					try {
						String testname = "insertClaim_41283";
						WSClient.startTest(testname, "Verify that claim is inserted by passing membership information", "minimumRegression");
						OPERALib.setOperaHeader(OPERALib.getUserName());
						String operaProfileID=" ";
						String resortOperaValue = OPERALib.getResort();
						String chain=OPERALib.getChain();
		                WSClient.setData("{var_resort}", resortOperaValue);
		                WSClient.setData("{var_chain}",chain);
		                WSClient.setData("{var_chaincode}", chain);
						
						// Creating a profile
						
							operaProfileID = CreateProfile.createProfile("DS_01");
						if (!operaProfileID.equals("error")) {
							WSClient.setData("{var_profileID}", operaProfileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
								
								
								String query3 = WSClient.getQuery("OWSInsertClaim","QS_03");
								
								LinkedHashMap<String,String> temp3 = new LinkedHashMap<String,String>();
								temp3=WSClient.getDBRow(query3);
								
								WSClient.setData("{var_nameOnCard}", temp3.get("first"));
								WSClient.setData("{var_memType}",OperaPropConfig.getDataSetForCode("MembershipType","DS_03"));
								WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel","DS_03"));
								String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
					               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
					               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
											false)) { 
					               
					               }
								
								setOWSHeader();
								
								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
								
								query3 = WSClient.getQuery("OWSInsertClaim","QS_04");
								temp3=WSClient.getDBRow(query3);
								WSClient.setData("{var_memId}", temp3.get("memId"));
								WSClient.setData("{var_memCardNo}", temp3.get("memCardNo"));
								
								// Creating request and processing response for OWS InsertClaim Operation
								String insertClaimReq = WSClient.createSOAPMessage("OWSInsertClaim", "DS_06");
								String insertClaimResponseXML = WSClient.processSOAPMessage(insertClaimReq);
								
								// Validating response of OWS InsertClaim Operation
								if(WSAssert.assertIfElementExists(insertClaimResponseXML,
										"InsertClaimResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(insertClaimResponseXML,
											"InsertClaimResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										
											// Database Validation
											LinkedHashMap<String,String> db = new LinkedHashMap<String,String>();
											LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
											LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
											xPath.put("InsertClaimRequest_Profile_NameID", "InsertClaimRequest_Profile");
											xPath.put("InsertClaimRequest_Claim_ClaimInformation", "InsertClaimRequest_Claim");
											expectedValues = WSClient.getSingleNodeList(insertClaimReq, xPath, false, XMLType.REQUEST);
											String query = WSClient.getQuery("QS_01");
											db = WSClient.getDBRow(query);
											WSAssert.assertEquals(expectedValues,db,false);
									}else {	
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", true)) {
											String message=WSAssert.getElementValue(insertClaimResponseXML, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the InsertClaim response is :"+ message + "</b>");
											if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode + "</b>");
											}
										}
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(insertClaimResponseXML, "InsertClaimResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The gds error displayed in the InsertClaim response is :"+ message + "</b>");
										}
									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in InsertClaim Response!");
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Fault_faultcode", true)) {
											String message=WSAssert.getElementValue(insertClaimResponseXML, "Fault_faultcode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>FaultCode : "+ message + "</b>");
										}
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_errorCode", true)) {
											String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_errorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>ErrorCode : "+ message + "</b>");
										}
										if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_reason", true)) {
											String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_reason", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>Reason : "+ message + "</b>");
										}
								}
							}
					} catch (Exception e) {
						WSClient.writeToReport(LogStatus.ERROR, "Exception occurred in test case due to" + e);
					}
				}
				
		// MR Test Case : 6
		@Test(groups={"minimumRegression","Name","OWS","InsertClaim"})
		public void insertClaim_41285() {
			try {
				String testname = "insertClaim_41285";
				WSClient.startTest(testname, "Verify that an error mesage is obtained with membershipId as EXTERNAL in the request", "minimumRegression");
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String operaProfileID=" ";
				String resortOperaValue = OPERALib.getResort();
				String chain=OPERALib.getChain();
                WSClient.setData("{var_resort}", resortOperaValue);
                WSClient.setData("{var_chain}",chain);
                WSClient.setData("{var_chaincode}", chain);
                
				// Creating a profile
				
					operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
					
						
						
						String query3 = WSClient.getQuery("OWSInsertClaim","QS_03");
						
						LinkedHashMap<String,String> temp3 = new LinkedHashMap<String,String>();
						temp3=WSClient.getDBRow(query3);
						
						WSClient.setData("{var_nameOnCard}", temp3.get("first"));
						WSClient.setData("{var_memType}",OperaPropConfig.getDataSetForCode("MembershipType","DS_03"));
						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel","DS_03")); 
						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
			               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									false)) { 
			               
			               }
						
						setOWSHeader();
						
						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
						
						query3 = WSClient.getQuery("OWSInsertClaim","QS_04");
						temp3=WSClient.getDBRow(query3);
						WSClient.setData("{var_memId}", temp3.get("memId"));
						WSClient.setData("{var_memCardNo}", temp3.get("memCardNo"));
						
						// Creating request and processing response for OWS InsertClaim Operation
						String insertClaimReq = WSClient.createSOAPMessage("OWSInsertClaim", "DS_07");
						String insertClaimResponseXML = WSClient.processSOAPMessage(insertClaimReq);
						
						// Validating response of OWS InsertClaim Operation
						if(WSAssert.assertIfElementExists(insertClaimResponseXML,
								"InsertClaimResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(insertClaimResponseXML,
									"InsertClaimResponse_Result_resultStatusFlag", "FAIL", false)) {

								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Result_Text_TextElement", false)) {
									if(WSAssert.assertIfElementValueEquals(insertClaimResponseXML, "Result_Text_TextElement", "This instance does not support EXTERNAL MembershipID values.", true)) {
										WSAssert.assertIfElementValueEquals(insertClaimResponseXML, "Result_Text_TextElement", "This instance does not support EXTERNAL MembershipID values.", false);
									}else if(WSAssert.assertIfElementContains(insertClaimResponseXML, "Result_Text_TextElement", "Membership ID not valid", false)) {
										
									}
								}
									
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in InsertClaim Response!");
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "Fault_faultcode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "Fault_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>FaultCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_errorCode", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_errorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>ErrorCode : "+ message + "</b>");
								}
								if(WSAssert.assertIfElementExists(insertClaimResponseXML, "detail_fault_reason", true)) {
									String message=WSAssert.getElementValue(insertClaimResponseXML, "detail_fault_reason", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reason : "+ message + "</b>");
								}
						}
					}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occurred in test case due to" + e);
			}
		}
		
}
