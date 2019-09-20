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

public class InsertGuestCard extends WSSetUp {
	
	
	
	//Sanity: Membership getting attached to profile with ows insert guest card operation
			@Test(groups = { "sanity", "InsertGuestCard", "Name","OWS"})

			public void insertGuestCard_38621() {
				try {
					String testName = "insertGuestCard_38621";
					WSClient.startTest(testName,
							"Verify that membership is attached to profile when minimum required data is passed", "sanity");
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType" })) {
						String resortOperaValue = OPERALib.getResort();
						String chain = OPERALib.getChain();
						WSClient.setData("{var_chain}", chain);
						WSClient.setData("{var_resort}", resortOperaValue);

						String uname = OPERALib.getUserName();
						String pwd = OPERALib.getPassword();
						String channel = OWSLib.getChannel();
						String channelType = OWSLib.getChannelType(channel);
						String resort = OWSLib.getChannelResort(resortOperaValue, channel);
						String channelCarrier = OWSLib.getChannelCarier(resort, channel);
						OPERALib.setOperaHeader(uname);
						String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);
		                
		                //prerequisite1--Create profile 
		                
		                String profileID1 = CreateProfile.createProfile("DS_06");
						if (!profileID1.equals("error")) {
							
							    WSClient.writeToReport(LogStatus.INFO, "<b>Profile created------</b>"+"<b>"+profileID1+"</b>");
		                        WSClient.setData("{var_profileID}", profileID1);
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
								WSClient.setData("{var_nameOnCard}",memName);
								WSClient.setData("{var_memType}",
										OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
								
								
								//Operation:Insert Guest Card to attach membership
								
								String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_06");
								String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
								if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(insertGuestCardRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(insertGuestCardRes,
										"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(insertGuestCardRes,
											"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(insertGuestCardRes,
										"InsertGuestCardResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(insertGuestCardRes,
											"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
									
								}

								if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
										"InsertGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_IDs_IDPair_operaId",
											false)) {
										 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
										
										String operaid = WSClient.getElementValue(insertGuestCardRes,
												"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
										WSClient.setData("{var_operaID}", operaid);
										
										
										WSClient.writeToReport(LogStatus.INFO,"<b>Validating membership details");
										
								
										String query=WSClient.getQuery("QS_02");
										
										//getting values from database
										
										LinkedHashMap<String, String> db = WSClient.getDBRow(query);
										
										//getting element values passed in request
										
										LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
										String profileID  = WSClient.getElementValue(insertGuestCardReq,
												"InsertGuestCardRequest_NameID", XMLType.REQUEST);

										String mem_Number = WSClient.getElementValue(insertGuestCardReq,
												"InsertGuestCardRequest_NameMembership_membershipNumber", XMLType.REQUEST);
										String mem_Name = WSClient.getElementValue(insertGuestCardReq,
												"InsertGuestCardRequest_NameMembership_memberName", XMLType.REQUEST);
										String mem_Type = WSClient.getElementValue(insertGuestCardReq,
												"InsertGuestCardRequest_NameMembership_membershipType", XMLType.REQUEST);
									
										expected.put("NAME_ID", profileID);
										expected.put("MEMBERSHIP_TYPE", mem_Type);
										expected.put("MEMBERSHIP_CARD_NO",mem_Number);
										
										expected.put("NAME_ON_CARD",mem_Name);
										//Comparing values in database with values passed in request
										
										WSAssert.assertEquals(expected, db, false);
									}

								}
						
							
							
						}
						else
							WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
					}
					else
						WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
					
				} 
				catch (Exception e) {
								WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
			}

		}
	
	//MinimumRegression: Membership getting attached to profile with ows insert guest card operation

	@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})

	public void insertGuestCard_41527() {
		try {
			String testName = "insertGuestCard_41527";
			WSClient.startTest(testName,
					"Verify that membership is attached to profile when mandatory as well as optional fields are provided", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				OPERALib.setOperaHeader(uname);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
                WSClient.setData("{var_fname}", fname);
                WSClient.setData("{var_lname}", lname);
                
                //prerequisite1--Create profile 
                
                String profileID1 = CreateProfile.createProfile("DS_06");
				if (!profileID1.equals("error")) {
					
					    WSClient.writeToReport(LogStatus.INFO, "<b>Profile created------</b>"+"<b>"+profileID1+"</b>");
                        WSClient.setData("{var_profileID}", profileID1);
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						WSClient.setData("{var_memType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						
						//Operation:Insert Guest Card to attach membership
						
						String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_01");
						String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
						if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
								true)) {

							/*
							 * Verifying that the error message is populated on
							 * the response
							 */

							String message = WSAssert.getElementValue(insertGuestCardRes,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
						}
						if (WSAssert.assertIfElementExists(insertGuestCardRes,
								"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

							/*
							 * Verifying whether the error Message is populated
							 * on the response
							 */
							String message1 = WSAssert.getElementValue(insertGuestCardRes,
									"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
							

							
						}
						if (WSAssert.assertIfElementExists(insertGuestCardRes,
								"InsertGuestCardResponse_Result_GDSError", true)) {

							/*
							 * Verifying whether the error Message is populated
							 * on the response
							 */
							

							String message = WSAssert.getElementValue(insertGuestCardRes,
									"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
		
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
							
						}

						if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
								"InsertGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_IDs_IDPair_operaId",
									false)) {
								 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
								
								String operaid = WSClient.getElementValue(insertGuestCardRes,
										"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
								WSClient.setData("{var_operaID}", operaid);
								
								WSClient.writeToReport(LogStatus.INFO,"<b>Validating membership details");
								
								String query=WSClient.getQuery("QS_01");
								
								//getting values from database
								
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);
								
								//getting element values passed in request
								
								LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
								String profileID  = WSClient.getElementValue(insertGuestCardReq,
										"InsertGuestCardRequest_NameID", XMLType.REQUEST);
								String mem_Level = WSClient.getElementValue(insertGuestCardReq,
										"InsertGuestCardRequest_NameMembership_membershipLevel", XMLType.REQUEST);
								String mem_Number = WSClient.getElementValue(insertGuestCardReq,
										"InsertGuestCardRequest_NameMembership_membershipNumber", XMLType.REQUEST);
								String mem_Name = WSClient.getElementValue(insertGuestCardReq,
										"InsertGuestCardRequest_NameMembership_memberName", XMLType.REQUEST);
								String mem_Type = WSClient.getElementValue(insertGuestCardReq,
										"InsertGuestCardRequest_NameMembership_membershipType", XMLType.REQUEST);
							
								expected.put("NAME_ID", profileID);
								expected.put("MEMBERSHIP_TYPE", mem_Type);
								expected.put("MEMBERSHIP_CARD_NO",mem_Number);
								expected.put("MEMBERSHIP_LEVEL", mem_Level);
								expected.put("NAME_ON_CARD",mem_Name);
								//Comparing values in database with values passed in request
								
								WSAssert.assertEquals(expected, db, false);
							}

						}
				
					
					
				}
				else
					WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
			}
			else
				WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
			
		} 
		catch (Exception e) {
						WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
	}

}
	
	//MinimumRegression case1:ProfileID not passed in request
	
	@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})

	public void insertGuestCard_38624() {
		try {
			String testName = "insertGuestCard_38624";
			WSClient.startTest(testName,
					"Verify that fail result status flag exists when ProfileID is not provided in InsertGuestCard", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);

				
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
                WSClient.setData("{var_fname}", fname);
                WSClient.setData("{var_lname}", lname);
				
				
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						WSClient.setData("{var_memType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						
						//operation:Insert Guest Card to attach membership
						
						String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_05");
						String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
						if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
								true)) {

							/*
							 * Verifying that the error message is populated on
							 * the response
							 */

							String message = WSAssert.getElementValue(insertGuestCardRes,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
						}
						if (WSAssert.assertIfElementExists(insertGuestCardRes,
								"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

							/*
							 * Verifying whether the error Message is populated
							 * on the response
							 */
							String message1 = WSAssert.getElementValue(insertGuestCardRes,
									"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OPERA error displayed in the InsertGuestCard response is---</b> : " + message1);
							

							
						}
						if (WSAssert.assertIfElementExists(insertGuestCardRes,
								"InsertGuestCardResponse_Result_GDSError", true)) {

							/*
							 * Verifying whether the error Message is populated
							 * on the response
							 */
							

							String message = WSAssert.getElementValue(insertGuestCardRes,
									"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
		
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDSError displayed in the InsertGuestCard response is :----</b>" + message);
							
						}
						if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
								"InsertGuestCardResponse_Result_resultStatusFlag", "FAIL", false)) {
							WSClient.writeToReport(LogStatus.PASS, "Can't insert guest card without profileID");
							
						}

						else
							WSClient.writeToReport(LogStatus.FAIL, "Membbership is created!!ERROR!!!!!");

						
				
					
			}
			else
				WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
			
		} catch (Exception e) {
						WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
					}

				} 
	
	//MinimumRegression case2:When membershipType is not passed
	
	
	@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})

	public void insertGuestCard_38625() {
		try {
			String testName = "insertGuestCard_38625";
			WSClient.startTest(testName,
					"Verify that fail result status flag exists when membershipType is not provided in InsertGuestCard", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
                WSClient.setData("{var_fname}", fname);
                WSClient.setData("{var_lname}", lname);
                String profileID = CreateProfile.createProfile("DS_06");
				if (!profileID.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile created------</b>"+"<b>"+profileID+"</b>");
						WSClient.setData("{var_profileID}", profileID);
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						
						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						
						
						
						//Operation:Insert Guest Card to attach membership
						
						String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_04");
						String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
						if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
								true)) {

							/*
							 * Verifying that the error message is populated on
							 * the response
							 */

							String message = WSAssert.getElementValue(insertGuestCardRes,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
						}
						if (WSAssert.assertIfElementExists(insertGuestCardRes,
								"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

							/*
							 * Verifying whether the error Message is populated
							 * on the response
							 */
							String message1 = WSAssert.getElementValue(insertGuestCardRes,
									"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
							

							
						}
						if (WSAssert.assertIfElementExists(insertGuestCardRes,
								"InsertGuestCardResponse_Result_GDSError", true)) {

							/*
							 * Verifying whether the error Message is populated
							 * on the response
							 */
							

							String message = WSAssert.getElementValue(insertGuestCardRes,
									"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
		
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
							
						}
						if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
								"InsertGuestCardResponse_Result_resultStatusFlag", "FAIL", false)) {
							WSClient.writeToReport(LogStatus.PASS, "Can't insert guest card without MembershipType");
							
						}

						else
							WSClient.writeToReport(LogStatus.FAIL, "Membership is created!!ERROR!!!!!");
					
				}
				else
					WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
			}
			else
				WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
			
		} catch (Exception e) {
						WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
					}

				}
	
	//MinimumRegression case3:When CardNumber is not passed
	
	
		@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})

		public void insertGuestCard_38623() {
			try {
				String testName = "insertGuestCard_38623";
				WSClient.startTest(testName,
						"Verify that fail result status flag exists  when cardNumber is not provided in InsertGuestCard", "minimumRegression");
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
					String resortOperaValue = OPERALib.getResort();
					String chain = OPERALib.getChain();
					WSClient.setData("{var_chain}", chain);
					WSClient.setData("{var_resort}", resortOperaValue);

					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channel = OWSLib.getChannel();
					String channelType = OWSLib.getChannelType(channel);
					String resort = OWSLib.getChannelResort(resortOperaValue, channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel); 
					OPERALib.setOperaHeader(uname);
					String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	                WSClient.setData("{var_fname}", fname);
	                WSClient.setData("{var_lname}", lname);
	                
	             // Prerequisite 1 - create profile
	                
	                String profileID = CreateProfile.createProfile("DS_06");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile created-----</b>"+"<b>"+profileID+"</b>");
                            WSClient.setData("{var_profileID}", profileID);
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
							WSClient.setData("{var_nameOnCard}",memName);
							WSClient.setData("{var_memType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
							WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
							
							//operation:Insert Guest Card to attach membership
							
							String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_02");
							String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
							if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
									true)) {

								/*
								 * Verifying that the error message is populated on
								 * the response
								 */

								String message = WSAssert.getElementValue(insertGuestCardRes,
										"Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
							}
							if (WSAssert.assertIfElementExists(insertGuestCardRes,
									"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

								/*
								 * Verifying whether the error Message is populated
								 * on the response
								 */
								String message1 = WSAssert.getElementValue(insertGuestCardRes,
										"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
								

								
							}
							if (WSAssert.assertIfElementExists(insertGuestCardRes,
									"InsertGuestCardResponse_Result_GDSError", true)) {

								/*
								 * Verifying whether the error Message is populated
								 * on the response
								 */
								

								String message = WSAssert.getElementValue(insertGuestCardRes,
										"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
			
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
								
							}
							if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
									"InsertGuestCardResponse_Result_resultStatusFlag", "FAIL", false)) {
								WSClient.writeToReport(LogStatus.PASS, "Can't insert guest card without CardNumber");
								
							}

							else
								WSClient.writeToReport(LogStatus.FAIL, "Membership is created!!ERROR!!!!!");
					}
					else
						WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
				}
				else
					WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
				
			} catch (Exception e) {
							WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
						}

					} 
//		@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})
//
//		public void insertGuestCard_() {
//			try {
//				String testName = "insertGuestCard_38621";
//				WSClient.startTest(testName,
//						"Verify that expiration date required", "minimumRegression");
//				if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType" })) {
//					String resortOperaValue = OPERALib.getResort();
//					String chain = OPERALib.getChain();
//					WSClient.setData("{var_chain}", chain);
//					WSClient.setData("{var_resort}", resortOperaValue);
//
//					String uname = OPERALib.getUserName();
//					String pwd = OPERALib.getPassword();
//					String channel = OWSLib.getChannel();
//					String channelType = OWSLib.getChannelType(channel);
//					String resort = OWSLib.getChannelResort(resortOperaValue, channel);
//					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
//					OPERALib.setOperaHeader(uname);
//					String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
//	                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
//	                WSClient.setData("{var_fname}", fname);
//	                WSClient.setData("{var_lname}", lname);
//	                
//	                //prerequisite1--Create profile 
//	                
//	                String profileID1 = CreateProfile.createProfile("DS_06");
//					if (!profileID1.equals("error")) {
//						
//						    WSClient.writeToReport(LogStatus.INFO, "<b>Profile created------</b>"+"<b>"+profileID1+"</b>");
//	                        WSClient.setData("{var_profileID}", profileID1);
//							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
//							String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
//							WSClient.setData("{var_nameOnCard}",memName);
//							WSClient.setData("{var_memType}",
//									OperaPropConfig.getDataSetForCode("MembershipType", "DS_06"));
//							
//							
//							//Operation:Insert Guest Card to attach membership
//							
//							String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_06");
//							String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
//							if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
//									true)) {
//
//								/*
//								 * Verifying that the error message is populated on
//								 * the response
//								 */
//
//								String message = WSAssert.getElementValue(insertGuestCardRes,
//										"Result_Text_TextElement", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.INFO,
//										"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
//							}
//							if (WSAssert.assertIfElementExists(insertGuestCardRes,
//									"InsertGuestCardResponse_Result_OperaErrorCode", true)) {
//
//								/*
//								 * Verifying whether the error Message is populated
//								 * on the response
//								 */
//								String message1 = WSAssert.getElementValue(insertGuestCardRes,
//										"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//								
//									WSClient.writeToReport(LogStatus.INFO,
//											"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
//								
//
//								
//							}
//							if (WSAssert.assertIfElementExists(insertGuestCardRes,
//									"InsertGuestCardResponse_Result_GDSError", true)) {
//
//								/*
//								 * Verifying whether the error Message is populated
//								 * on the response
//								 */
//								
//
//								String message = WSAssert.getElementValue(insertGuestCardRes,
//										"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
//			
//									WSClient.writeToReport(LogStatus.INFO,
//											"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
//								
//							}
//
//							if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
//									"InsertGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//								if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_IDs_IDPair_operaId",
//										false)) {
//									 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
//									
//									String operaid = WSClient.getElementValue(insertGuestCardRes,
//											"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
//									WSClient.setData("{var_operaID}", operaid);
//									
//									
//									WSClient.writeToReport(LogStatus.INFO,"<b>Validating membership details");
//									
//							
//									String query=WSClient.getQuery("QS_02");
//									
//									//getting values from database
//									
//									LinkedHashMap<String, String> db = WSClient.getDBRow(query);
//									
//									//getting element values passed in request
//									
//									LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
//									String profileID  = WSClient.getElementValue(insertGuestCardReq,
//											"InsertGuestCardRequest_NameID", XMLType.REQUEST);
//
//									String mem_Number = WSClient.getElementValue(insertGuestCardReq,
//											"InsertGuestCardRequest_NameMembership_membershipNumber", XMLType.REQUEST);
//									String mem_Name = WSClient.getElementValue(insertGuestCardReq,
//											"InsertGuestCardRequest_NameMembership_memberName", XMLType.REQUEST);
//									String mem_Type = WSClient.getElementValue(insertGuestCardReq,
//											"InsertGuestCardRequest_NameMembership_membershipType", XMLType.REQUEST);
//								
//									expected.put("NAME_ID", profileID);
//									expected.put("MEMBERSHIP_TYPE", mem_Type);
//									expected.put("MEMBERSHIP_CARD_NO",mem_Number);
//									
//									expected.put("NAME_ON_CARD",mem_Name);
//									//Comparing values in database with values passed in request
//									
//									WSAssert.assertEquals(expected, db, false);
//								}
//
//							}
//					
//						
//						
//					}
//					else
//						WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
//				}
//				else
//					WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
//				
//			} 
//			catch (Exception e) {
//							WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
//		}
//
//	}
		@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})

		public void insertGuestCard_41923() {
			try {
				String testName = "insertGuestCard_41923";
				WSClient.startTest(testName,
						"Verify that error message populates when trying to attach a duplicate membership to the profile", "minimumRegression");
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType" })) {
					String resortOperaValue = OPERALib.getResort();
					String chain = OPERALib.getChain();
					WSClient.setData("{var_chain}", chain);
					WSClient.setData("{var_resort}", resortOperaValue);

					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channel = OWSLib.getChannel();
					String channelType = OWSLib.getChannelType(channel);
					String resort = OWSLib.getChannelResort(resortOperaValue, channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OPERALib.setOperaHeader(uname);
					String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	                WSClient.setData("{var_fname}", fname);
	                WSClient.setData("{var_lname}", lname);
	                
	                //prerequisite1--Create profile 
	                
	                String profileID1 = CreateProfile.createProfile("DS_06");
					if (!profileID1.equals("error")) {
						
						    WSClient.writeToReport(LogStatus.INFO, "<b>Profile created------</b>"+"<b>"+profileID1+"</b>");
	                        WSClient.setData("{var_profileID}", profileID1);
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
							WSClient.setData("{var_nameOnCard}",memName);
							WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
							WSClient.setData("{var_memType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
							/*
							 * Inserting the first membership using CreateMembership
							 */
							String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
				               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
				               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
										true)) {
									 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
									 
							//Operation:Insert Guest Card to attach membership
									 
				               WSClient.setData("{var_nameOnCard}",memName+"234");
							String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_06");
							String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
							if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
									true)) {

								/*
								 * Verifying that the error message is populated on
								 * the response
								 */

								String message = WSAssert.getElementValue(insertGuestCardRes,
										"Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
							}
							if (WSAssert.assertIfElementExists(insertGuestCardRes,
									"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

								/*
								 * Verifying whether the error Message is populated
								 * on the response
								 */
								String message1 = WSAssert.getElementValue(insertGuestCardRes,
										"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
								

								
							}
							if (WSAssert.assertIfElementExists(insertGuestCardRes,
									"InsertGuestCardResponse_Result_GDSError", true)) {

								/*
								 * Verifying whether the error Message is populated
								 * on the response
								 */
								

								String message = WSAssert.getElementValue(insertGuestCardRes,
										"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
			
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
								
							}

							if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
									"InsertGuestCardResponse_Result_resultStatusFlag", "FAIL", false)) {
								WSClient.writeToReport(LogStatus.PASS,"Cant attach a duplicate membership");
							
							}
							else
								WSClient.writeToReport(LogStatus.FAIL,"Duplicate membership attached!!!ERROR");
					
				               }
								else
									WSClient.writeToReport(LogStatus.WARNING, "Problem in creating membership");
						
					}
					else
						WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
				}
				else
					WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
				
			} 
			catch (Exception e) {
							WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		}
			@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})

			public void insertGuestCard_41924() {
				try {
					String testName = "insertGuestCard_41924";
					WSClient.startTest(testName,
							"Verify that error message populates when trying to give same membership cardNumber to another profile ", "minimumRegression");
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType" })) {
						String resortOperaValue = OPERALib.getResort();
						String chain = OPERALib.getChain();
						WSClient.setData("{var_chain}", chain);
						WSClient.setData("{var_resort}", resortOperaValue);

						String uname = OPERALib.getUserName();
						String pwd = OPERALib.getPassword();
						String channel = OWSLib.getChannel();
						String channelType = OWSLib.getChannelType(channel);
						String resort = OWSLib.getChannelResort(resortOperaValue, channel);
						String channelCarrier = OWSLib.getChannelCarier(resort, channel);
						OPERALib.setOperaHeader(uname);
						String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);
		                
		                //prerequisite1--Create profile 
		                
		                String profileID1 = CreateProfile.createProfile("DS_06");
						if (!profileID1.equals("error")) {
							
							    WSClient.writeToReport(LogStatus.INFO, "<b>First Profile created------</b>"+"<b>"+profileID1+"</b>");
		                        WSClient.setData("{var_profileID}", profileID1);
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
								WSClient.setData("{var_nameOnCard}",memName);
								WSClient.setData("{var_memType}",
										OperaPropConfig.getDataSetForCode("MembershipType", "DS_05"));
								String cardno=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
								WSClient.setData("{var_cardno}",
										cardno);
								
								//Operation:Insert Guest Card to attach membership
								
								String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_07");
								String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
								if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(insertGuestCardRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(insertGuestCardRes,
										"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(insertGuestCardRes,
											"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(insertGuestCardRes,
										"InsertGuestCardResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(insertGuestCardRes,
											"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
									
								}

								if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
										"InsertGuestCardResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_IDs_IDPair_operaId",
											false)) {
										 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership to first profile</b>");
										
										String operaid = WSClient.getElementValue(insertGuestCardRes,
												"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
										WSClient.setData("{var_operaID}", operaid);
										
//										
//										WSClient.writeToReport(LogStatus.INFO,"<b>Validating membership details");
//										
//								
//										String query=WSClient.getQuery("QS_02");
//										
//										//getting values from database
//										
//										LinkedHashMap<String, String> db = WSClient.getDBRow(query);
//										
//										//getting element values passed in request
//										
//										LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
//										String profileID  = WSClient.getElementValue(insertGuestCardReq,
//												"InsertGuestCardRequest_NameID", XMLType.REQUEST);
//
//										String mem_Number = WSClient.getElementValue(insertGuestCardReq,
//												"InsertGuestCardRequest_NameMembership_membershipNumber", XMLType.REQUEST);
//										String mem_Name = WSClient.getElementValue(insertGuestCardReq,
//												"InsertGuestCardRequest_NameMembership_memberName", XMLType.REQUEST);
//										String mem_Type = WSClient.getElementValue(insertGuestCardReq,
//												"InsertGuestCardRequest_NameMembership_membershipType", XMLType.REQUEST);
//									
//										expected.put("NAME_ID", profileID);
//										expected.put("MEMBERSHIP_TYPE", mem_Type);
//										expected.put("MEMBERSHIP_CARD_NO",mem_Number);
//										
//										expected.put("NAME_ON_CARD",mem_Name);
//										//Comparing values in database with values passed in request
//										
//										WSAssert.assertEquals(expected, db, false);
										OPERALib.setOperaHeader(uname);
									    fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
						                lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
						                WSClient.setData("{var_fname}", fname);
						                WSClient.setData("{var_lname}", lname);
						                
						                //prerequisite1--Create profile 
						                
						                profileID1 = CreateProfile.createProfile("DS_06");
										if (!profileID1.equals("error")) {
											
											    WSClient.writeToReport(LogStatus.INFO, "<b>Second Profile created------</b>"+"<b>"+profileID1+"</b>");
						                        WSClient.setData("{var_profileID}", profileID1);
												OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
												memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
												WSClient.setData("{var_nameOnCard}",memName);
												WSClient.setData("{var_memType}",
														OperaPropConfig.getDataSetForCode("MembershipType", "DS_05"));
												
												
												//Operation:Insert Guest Card to attach membership
												
												 insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_07");
												 insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
												if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
														true)) {

													/*
													 * Verifying that the error message is populated on
													 * the response
													 */

													String message = WSAssert.getElementValue(insertGuestCardRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
												}
												if (WSAssert.assertIfElementExists(insertGuestCardRes,
														"InsertGuestCardResponse_Result_OperaErrorCode", true)) {

													/*
													 * Verifying whether the error Message is populated
													 * on the response
													 */
													String message1 = WSAssert.getElementValue(insertGuestCardRes,
															"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
													
														WSClient.writeToReport(LogStatus.INFO,
																"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
													

													
												}
												if (WSAssert.assertIfElementExists(insertGuestCardRes,
														"InsertGuestCardResponse_Result_GDSError", true)) {

													/*
													 * Verifying whether the error Message is populated
													 * on the response
													 */
													

													String message = WSAssert.getElementValue(insertGuestCardRes,
															"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
								
														WSClient.writeToReport(LogStatus.INFO,
																"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
													
												}

												if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
														"InsertGuestCardResponse_Result_resultStatusFlag", "FAIL", false)) {
													WSClient.writeToReport(LogStatus.PASS,"Cant attach same membership card number to another profile");
												
												}
												else
													WSClient.writeToReport(LogStatus.FAIL,"Same cardnumber attached to another profile!!!ERROR");
										
										
											
											
										}
										else
											WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
									}

								}
						
							
							
						}
						else
							WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
					}
					else
						WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
					
				} 
				catch (Exception e) {
								WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
			}

		}
	
//			@Test(groups = { "minimumRegression", "InsertGuestCard", "Name","OWS"})
//
//			public void insertGuestCard_2222222() {
//				try {
//					String testName = "insertGuestCard_2222222";
//					WSClient.startTest(testName,
//							"Verify that fail result status flag exists  when cardNumber exceeds the defined length in InsertGuestCard", "minimumRegression");
//					if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
//						String resortOperaValue = OPERALib.getResort();
//						String chain = OPERALib.getChain();
//						WSClient.setData("{var_chain}", chain);
//						WSClient.setData("{var_resort}", resortOperaValue);
//
//						String uname = OPERALib.getUserName();
//						String pwd = OPERALib.getPassword();
//						String channel = OWSLib.getChannel();
//						String channelType = OWSLib.getChannelType(channel);
//						String resort = OWSLib.getChannelResort(resortOperaValue, channel);
//						String channelCarrier = OWSLib.getChannelCarier(resort, channel); 
//						OPERALib.setOperaHeader(uname);
//						String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
//		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
//		                WSClient.setData("{var_fname}", fname);
//		                WSClient.setData("{var_lname}", lname);
//		                
//		             // Prerequisite 1 - create profile
//		                
//		                String profileID = CreateProfile.createProfile("DS_06");
//						if (!profileID.equals("error")) {
//							WSClient.writeToReport(LogStatus.INFO, "<b>Profile created-----</b>"+"<b>"+profileID+"</b>");
//	                            WSClient.setData("{var_profileID}", profileID);
//								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
//								String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
//								WSClient.setData("{var_nameOnCard}",memName);
//								WSClient.setData("{var_memType}",
//										OperaPropConfig.getDataSetForCode("MembershipType", "DS_07"));
//								WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_07"));
//								WSClient.setData("{var_cardno}",WSClient.getKeywordData("{KEYWORD_RANDNUM_9}"));
//								//operation:Insert Guest Card to attach membership
//								
//								String insertGuestCardReq = WSClient.createSOAPMessage("OWSInsertGuestCard", "DS_07");
//								String insertGuestCardRes = WSClient.processSOAPMessage(insertGuestCardReq);
//								if (WSAssert.assertIfElementExists(insertGuestCardRes, "Result_Text_TextElement",
//										true)) {
//
//									/*
//									 * Verifying that the error message is populated on
//									 * the response
//									 */
//
//									String message = WSAssert.getElementValue(insertGuestCardRes,
//											"Result_Text_TextElement", XMLType.RESPONSE);
//									WSClient.writeToReport(LogStatus.INFO,
//											"<b>The text displayed in the InsertGuestCard response is----</b> :" + message);
//								}
//								if (WSAssert.assertIfElementExists(insertGuestCardRes,
//										"InsertGuestCardResponse_Result_OperaErrorCode", true)) {
//
//									/*
//									 * Verifying whether the error Message is populated
//									 * on the response
//									 */
//									String message1 = WSAssert.getElementValue(insertGuestCardRes,
//											"InsertGuestCardResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//									
//										WSClient.writeToReport(LogStatus.INFO,
//												"<b>The OPERA error displayed in the InsertGuestCard response is :---</b> " + message1);
//									
//
//									
//								}
//								if (WSAssert.assertIfElementExists(insertGuestCardRes,
//										"InsertGuestCardResponse_Result_GDSError", true)) {
//
//									/*
//									 * Verifying whether the error Message is populated
//									 * on the response
//									 */
//									
//
//									String message = WSAssert.getElementValue(insertGuestCardRes,
//											"InsertGuestCardResponse_Result_GDSError", XMLType.RESPONSE);
//				
//										WSClient.writeToReport(LogStatus.INFO,
//												"<b>The GDSError displayed in the InsertGuestCard response is :---</b> " + message);
//									
//								}
//								if (WSAssert.assertIfElementValueEquals(insertGuestCardRes,
//										"InsertGuestCardResponse_Result_resultStatusFlag", "FAIL", false)) {
//									WSClient.writeToReport(LogStatus.PASS, "Can't insert guest card without CardNumber");
//									
//								}
//
//								else
//									WSClient.writeToReport(LogStatus.FAIL, "Membership is created!!ERROR!!!!!");
//						}
//						else
//							WSClient.writeToReport(LogStatus.WARNING,"*****BLOCKED  Pre_requisite****Create Profile ");
//					}
//					else
//						WSClient.writeToReport(LogStatus.WARNING,"Pre_requisites MembershipType,MembershipLevel not available ");
//					
//				} catch (Exception e) {
//								WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
//							}
//
//						} 
	}
		
			
						