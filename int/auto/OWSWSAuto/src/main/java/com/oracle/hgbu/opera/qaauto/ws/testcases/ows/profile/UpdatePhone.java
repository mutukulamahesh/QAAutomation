package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.ArrayList;

//import static org.testng.Assert.assertEquals;

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

public class UpdatePhone extends WSSetUp{

	@Test(groups = {"sanity", "UpdatePhone", "Name", "OWS" })
	
	public void updatePhone_38341(){
		try{
			String testName = "updatePhone_38341";
			WSClient.startTest(testName,
					"Verify that phone record of a profile is getting updated when minimum required data is passed",
					"sanity");
			String prerequisite[]={"CommunicationType"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
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
			String query;

			 WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
			
			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String profileID = CreateProfile.createProfile("DS_11");
			if (!profileID.equals("error")) {
				

					
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					query=WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
					String phoneid = phn.get("PHONE_ID");
					if(!phoneid.equals("")){
					WSClient.setData("{var_phoneID}", phoneid);

					// OWS update phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_01");
					String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
					if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
					}

					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updatePhoneResponseXML,
								"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
					}
					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updatePhoneResponseXML,
								"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
					}
					if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully executed update phone request</b>");
						
						 query=WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> db = WSClient.getDBRow(query);
						LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
						
						// request records are being stored in different
						// variables
						
						String phoneID=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_operaId", XMLType.REQUEST);
						String phoneNo=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_PhoneNumber", XMLType.REQUEST);
						
					
						actualValues.put("PHONE_ID", phoneID);
						actualValues.put("PHONE_NUMBER",phoneNo);
						
						// database records and request records are being
						// compared
						
						   WSClient.writeToReport(LogStatus.INFO, "<b>Validating the phone details </b>");
						WSAssert.assertEquals(actualValues,db, false);

					}
					
					}else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
					}
				
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Profile -----Blocked");
			}
		}else{
			WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
		}
		}
		catch(Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
	//MinimumRegression case:1--invalid or already deleted phoneID is passed
	
	@Test(groups = { "minimumRegression", "UpdatePhone", "Name", "OWS" })

	public void updatePhone_38386() {
		try {
			String testName = "updatePhone_38386";
			WSClient.startTest(testName,
					"Verify that error message is being populated when an invalid opera phone id is passed in  request",
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
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			 WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));

			// OWS update phone

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_03");
			String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
			
			if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the
				 * response
				 ********/

				String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
			}

			if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
					"UpdatePhoneResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on
				 * the response
				 ****/

				String message = WSAssert.getElementValue(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
			}
			if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
					"UpdatePhoneResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on
				 * the response
				 ****/

				String message = WSAssert.getElementValue(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
			}

			if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
					"UpdatePhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
				WSClient.writeToReport(LogStatus.PASS, " update phone is Unsuccessful!");
				

			} else {
				WSClient.writeToReport(LogStatus.FAIL,
						"The phone has been updated successfully! ERROR! ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
	
	//MinimumRegression case:2---secondary made to primary then automatically primary becomes secondary
	
	@Test(groups = { "minimumRegression", "UpdatePhone", "Name", "OWS" })

    public void updatePhone_38482() {
           try {
                  String testName = "updatePhone_38482";
                  WSClient.startTest(testName,
                               "Verify when two phone numbers are attached to profile one is primary and the other is non-primary and if the non-primary phone is"
                                             + "updated to primary then the primary phone should be updated to non-primary automatically when passing mandatory fields in the Request",
                               "minimumRegression");
                  String preReq[] = { "CommunicationType" };
                  if (OperaPropConfig.getPropertyConfigResults(preReq)) {
                	  
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
                        
                        String phnType1 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03").toUpperCase();
                        String phnType2 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01").toUpperCase();
                        String query;
                        
                        WSClient.setData("{var_phoneType1}", phnType1);
                        WSClient.setData("{var_phoneType2}", phnType2); 
                       
                        WSClient.setData("{var_primary}", "true");
                        WSClient.setData("{var_primary2}", "false");
                        OPERALib.setOperaHeader(uname);
                   	 String createProfileReq = WSClient.createSOAPMessage("CreateProfile",
                   	 "DS_19");
                   	 String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
                   	
                   	 if (WSAssert.assertIfElementExists(createProfileResponseXML,
                   	 "CreateProfileRS_Success", true)) {
                   	 if (WSAssert.assertIfElementExists(createProfileResponseXML,
                   	 "CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
                   	
                   	 String operaProfileID =
                   	 WSClient.getElementValue(createProfileResponseXML,
                   	 "CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
                   	 
                   	WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+operaProfileID+"</b>");
                   	
                   	 WSClient.setData("{var_profileID}", operaProfileID);
                      
                        if (!operaProfileID.equals("")) {
                        	
                        	
                        	OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
                        	
                        	HashMap<String,String> xPath = new HashMap<String,String>();
		            		xPath.put("Telephones_TelephoneInfo_Telephone_PhoneNumber", "Profile_Telephones_TelephoneInfo");
		            		xPath.put("Telephones_TelephoneInfo_Telephone_PhoneUseType", "Profile_Telephones_TelephoneInfo");
		            		xPath.put("Telephones_TelephoneInfo_Telephone_PrimaryInd", "Profile_Telephones_TelephoneInfo");
		            	    //Telephones_TelephoneInfo_Telephone
		            		List<LinkedHashMap<String, String>> expectedValues = WSClient.getMultipleNodeList(createProfileReq, xPath, false, XMLType.REQUEST);
		            		//Values from DB Actual values 
		            		query=WSClient.getQuery("QS_07");
		            		List<LinkedHashMap<String, String>> DBactualValues=WSClient.getDBRows(query);
		            		
						   // WSClient.writeToReport(LogStatus.INFO, "Expected size : " + String.valueOf(DBactualValues.size()));
		            		
		            		
		            		
		            		//Verifying the values if both are equal 
		            	
    						if(WSAssert.assertEquals(expectedValues, DBactualValues, false)){
    							//WSClient.writeToReport(LogStatus.INFO, "Expected size : " + String.valueOf(DBactualValues.size()));
    							query=WSClient.getQuery("QS_08");
    							List<LinkedHashMap<String, String>> dbValues=WSClient.getDBRows(query);
    					
    							String phoneid_N = "";
                                String phoneid_Y = "";
                                String phoneType_N="";
                                String phoneType_Y="";
                                String phnNumber_N="";
                                String phnNumber_Y="";
                                String phoneRole_N="";
                                String phoneRole_Y="";
                                for (int i = 0; i < 2; i++) {
                                	//WSClient.writeToReport(LogStatus.PASS, "HI VALUES ARE"+dbValues);
                                	if (dbValues.get(i).get("PRIMARY_YN").equals("false")) {
                                    	   
                                              phoneid_N = dbValues.get(i).get("PHONE_ID");
                                              
                                              WSClient.setData("{var_phoneID}", phoneid_N);
                                              phoneType_N=dbValues.get(i).get("PHONE_TYPE");
                                              WSClient.setData("{var_phoneType}",phoneType_N);
                                              phoneRole_N=dbValues.get(i).get("PHONE_ROLE");
                                              phnNumber_N=dbValues.get(i).get("PHONE_NUMBER");
                                              WSClient.setData("{var_phnNumber_N}", phnNumber_N);
                                              WSClient.setData("{var_Ytrue}","true");
                                            
                                       } else {
                                              phoneid_Y = dbValues.get(i).get("PHONE_ID");
                                              phoneType_Y=dbValues.get(i).get("PHONE_TYPE");
                                              phoneRole_Y=dbValues.get(i).get("PHONE_ROLE");
                                              phnNumber_Y=dbValues.get(i).get("PHONE_NUMBER");
                                       }
                                }
                               
                               
                               if (!(phoneid_N.equals("") && phoneid_Y.equals(""))) {
                                WSClient.setData("{var_phoneID}", phoneid_N);
                            	  
                                      String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_02");
                  					String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
                  					if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {

                						/****
                						 * Verifying that the error message is populated on the
                						 * response
                						 ********/

                						String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
                								XMLType.RESPONSE);
                						WSClient.writeToReport(LogStatus.INFO,
                								"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
                					}

                					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
                							"UpdatePhoneResponse_Result_OperaErrorCode", true)) {

                						/****
                						 * Verifying whether the error Message is populated on
                						 * the response
                						 ****/

                						String message = WSAssert.getElementValue(updatePhoneResponseXML,
                								"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
                						WSClient.writeToReport(LogStatus.ERROR,
                								"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
                					}
                					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
                							"UpdatePhoneResponse_Result_GDSError", true)) {

                						/****
                						 * Verifying whether the error Message is populated on
                						 * the response
                						 ****/

                						String message = WSAssert.getElementValue(updatePhoneResponseXML,
                								"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
                						WSClient.writeToReport(LogStatus.ERROR,
                								"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
                					}
                                   
                                      if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
                                                    "UpdatePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
                                    	  WSClient.writeToReport(LogStatus.INFO, "<b>Successfully executed update phone request</b>");

                                          // database records are being stored in a list of
                                          // hashmaps
                                    	  query=WSClient.getQuery("QS_03");
                                          List<LinkedHashMap<String, String>> db = WSClient.getDBRows(query);

                                          List<LinkedHashMap<String, String>> Expected = new ArrayList<LinkedHashMap<String, String>>();

                                          // request records are being stored in different
                                          // variables
                                          String phoneID=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_operaId", XMLType.REQUEST);
                 						  //String primaryPhn=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_primary", XMLType.REQUEST);


                                          LinkedHashMap<String, String> val1 = new LinkedHashMap<String, String>();
                                          val1.put("PHONE_ID", phoneid_Y);
                                          val1.put("PHONE_TYPE", phoneType_Y);
                                          val1.put("PHONE_ROLE", phoneRole_Y);
                                          val1.put("PHONE_NUMBER", phnNumber_Y);
                                          val1.put("PRIMARY_YN","false");
                                          Expected.add(val1);
                                          LinkedHashMap<String, String> val2 = new LinkedHashMap<String, String>();
                                          val2.put("PHONE_ID", phoneID);
                                          val2.put("PHONE_TYPE", phoneType_N);
                                          val2.put("PHONE_ROLE", phoneRole_N);
                                          val2.put("PHONE_NUMBER", phnNumber_N);
                                          val2.put("PRIMARY_YN","true");
                                          Expected.add(val2);

                                          // database records and REQUEST records are being
                                          // compared
                                          
                                          WSClient.writeToReport(LogStatus.INFO, "<b>Validating the phone details </b>");
                                          WSAssert.assertEquals(db,Expected,  false);
                                      }
                               }
    						}
    						else 
    							WSClient.writeToReport(LogStatus.WARNING,"****problem in inserting phone records through create profile*****");
                        }
                   	 }
                   	 }
                  }
                  else
                	  WSClient.writeToReport(LogStatus.WARNING,"Problem in fetching pre-requisites CommunicationType ");
           }
           catch(Exception e)
   		{
   			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   		}
	}
     
	
	//MinimumRegression case:3--passing mandatory as well as optional fields to update phone
	
	
	
	@Test(groups = {"minimumRegression", "UpdatePhone", "Name", "OWS" })
	
	public void updatePhone_38605(){
		try{
			String testName = "updatePhone_38605";
			WSClient.startTest(testName,
					"Verify that phone number is getting updated when passing mandatory fields as well as optional fields ",
					"minimumRegression");
			
			String prerequisite[]={"CommunicationType","CommunicationMethod"};
			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
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
			String query;

			 WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03").toUpperCase());
			 WSClient.setData("{var_phoneRole}",OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
			
			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_11");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {

					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+operaProfileID+"</b>");
					query=WSClient.getQuery("QS_08");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
					String phoneid = phn.get("PHONE_ID");
					if(!phoneid.equals("")){
					WSClient.setData("{var_phoneID}", phoneid);
					WSClient.setData("{var_phoneType}",phn.get("PHONE_TYPE"));
					WSClient.setData("{var_phoneRole}",phn.get("PHONE_ROLE"));
					WSClient.setData("{var_primaryYn}",phn.get("PRIMARY_YN"));

					// OWS update phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_04");
					String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
					if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
					}

					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updatePhoneResponseXML,
								"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.ERROR,
								"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
					}
					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updatePhoneResponseXML,
								"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.ERROR,
								"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
					}
					if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully executed update phone request</b>");
						
						// database records are being stored in a linked hashmap
						query=WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> db = WSClient.getDBRow(query);
						LinkedHashMap<String,String> Values=new LinkedHashMap<String,String>();
						
						// request records are being stored in different
						// variables
						
						String phoneID=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_operaId", XMLType.REQUEST);
						String phoneNo=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_PhoneNumber", XMLType.REQUEST);
						String phoneType=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_phoneType", XMLType.REQUEST);
						String phoneRole=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_phoneRole", XMLType.REQUEST);
						String primaryYN=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_primary", XMLType.REQUEST);
						
					
						Values.put("PHONE_ID", phoneID);
						Values.put("PHONE_TYPE",phoneType);
						Values.put("PHONE_ROLE", phoneRole);
						Values.put("PHONE_NUMBER",phoneNo);
						Values.put("PRIMARY_YN",primaryYN);
						
						// database records and request records are being
						// compared
						
						   WSClient.writeToReport(LogStatus.INFO, "<b>Validating the phone details </b>");
						WSAssert.assertEquals( Values,db, false);

					}
					else
						WSClient.writeToReport(LogStatus.FAIL, "Problem in updating phone ");
					
					}else 
						WSClient.writeToReport(LogStatus.WARNING,
								"No phoneID is retrieved from database for the created profile");
					
				} else 
					WSClient.writeToReport(LogStatus.WARNING,
							"No profileID is returned for the created profile");
				
			} else 
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Profile -Not able to create a profile-----Blocked");
			
		}else
			WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type AND CommunicationMethod Not Available**********");
		
		}
		catch(Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
//@Test(groups = {"minimumRegression", "UpdatePhone", "Name", "OWS" })
//	
//	public void updatePhone_UPDATEEMAIL(){
//		try{
//			String testName = "updatePhone_UPDATEEMAIL";
//			WSClient.startTest(testName,
//					"Verify that phone record of a profile is getting updated when minimum required data is passed",
//					"minimumRegression");
//			String prerequisite[]={"CommunicationType"};
//			if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
//			String resortOperaValue = OPERALib.getResort();
//			String chain = OPERALib.getChain();
//			WSClient.setData("{var_chain}", chain);
//			WSClient.setData("{var_resort}", resortOperaValue);
//
//			String uname = OPERALib.getUserName();
//			String pwd = OPERALib.getPassword();
//			String channel = OWSLib.getChannel();
//			String channelType = OWSLib.getChannelType(channel);
//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
//			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
//			String query;
//
//			 WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
//			 String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
//				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
//			 WSClient.setData("{var_fname}", fname);
//				WSClient.setData("{var_lname}", lname);
//				WSClient.setData("{var_email}", fname + "." + lname + "@oracle.com");
//				WSClient.setData("{var_primary}", "true");
//				WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
//			// Prerequisite 1 - create profile
//			OPERALib.setOperaHeader(uname);
//			String profileID = CreateProfile.createProfile("DS_45");
//			if (!profileID.equals("error")) {
//				
//
//					
//				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
//					WSClient.setData("{var_profileID}", profileID);
//					query=WSClient.getQuery("QS_04");
//					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
//					String phoneid = phn.get("PHONE_ID");
//					if(!phoneid.equals("")){
//					WSClient.setData("{var_phoneID}", phoneid);
//
//					// OWS update phone
//
//					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
//					String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_01");
//					String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
//					if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {
//
//						/****
//						 * Verifying that the error message is populated on the
//						 * response
//						 ********/
//
//						String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
//								XMLType.RESPONSE);
//						WSClient.writeToReport(LogStatus.INFO,
//								"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
//					}
//
//					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
//							"UpdatePhoneResponse_Result_OperaErrorCode", true)) {
//
//						/****
//						 * Verifying whether the error Message is populated on
//						 * the response
//						 ****/
//
//						String message = WSAssert.getElementValue(updatePhoneResponseXML,
//								"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//						WSClient.writeToReport(LogStatus.INFO,
//								"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
//					}
//					if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
//							"UpdatePhoneResponse_Result_GDSError", true)) {
//
//						/****
//						 * Verifying whether the error Message is populated on
//						 * the response
//						 ****/
//
//						String message = WSAssert.getElementValue(updatePhoneResponseXML,
//								"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
//						WSClient.writeToReport(LogStatus.INFO,
//								"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
//					}
//					if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
//							"UpdatePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully executed update phone request</b>");
//						
//						 query=WSClient.getQuery("QS_01");
//						LinkedHashMap<String, String> db = WSClient.getDBRow(query);
//						LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
//						
//						// request records are being stored in different
//						// variables
//						
//						String phoneID=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_operaId", XMLType.REQUEST);
//						String phoneNo=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_PhoneNumber", XMLType.REQUEST);
//						
//					
//						actualValues.put("PHONE_ID", phoneID);
//						actualValues.put("PHONE_NUMBER",phoneNo);
//						
//						// database records and request records are being
//						// compared
//						
//						   WSClient.writeToReport(LogStatus.INFO, "<b>Validating the phone details </b>");
//						WSAssert.assertEquals(actualValues,db, false);
//
//					}
//					
//					}else {
//						WSClient.writeToReport(LogStatus.WARNING,
//								"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
//					}
//				
//			} else {
//				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Profile -----Blocked");
//			}
//		}else{
//			WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
//		}
//		}
//		catch(Exception e)
//		{
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		}
//	}
@Test(groups = {"minimumRegression", "UpdatePhone", "Name", "OWS" })

public void updatePhone_42207(){
	try{
		String testName = "updatePhone_42207";
		WSClient.startTest(testName,
				"Verify that phoneType is successfully updated",
				"minimumRegression");
		String prerequisite[]={"CommunicationType"};
		if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
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
		String query;

		 WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
		
		// Prerequisite 1 - create profile
		OPERALib.setOperaHeader(uname);
		String profileID = CreateProfile.createProfile("DS_11");
		if (!profileID.equals("error")) {
			

				
			WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
				WSClient.setData("{var_profileID}", profileID);
				query=WSClient.getQuery("QS_04");
				LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
				String phoneid = phn.get("PHONE_ID");
				if(!phoneid.equals("")){
				WSClient.setData("{var_phoneID}", phoneid);

				// OWS update phone

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
				String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_01");
				String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
				if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
				}

				if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on
					 * the response
					 ****/

					String message = WSAssert.getElementValue(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
				}
				if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on
					 * the response
					 ****/

					String message = WSAssert.getElementValue(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
				}
				if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Successfully executed update phone request</b>");
					query=WSClient.getQuery("CreateProfile","QS_08");
					LinkedHashMap<String, String> db = WSClient.getDBRow(query);
					LinkedHashMap<String, String> db2=new LinkedHashMap<String,String>(); ;
					db2.put("PHONE_ID", db.get("PHONE_ID"));
					db2.put("PHONE_NUMBER",db.get("PHONE_NUMBER"));
					db2.put("PHONE_TYPE", db.get("PHONE_TYPE"));
					LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
					
					 
					
					// request records are being stored in different
					// variables
					
					String phoneID=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_operaId", XMLType.REQUEST);
					String phoneNo=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_PhoneNumber", XMLType.REQUEST);
					String phoneType=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_phoneType", XMLType.REQUEST);
				
					actualValues.put("PHONE_ID", phoneID);
					actualValues.put("PHONE_NUMBER",phoneNo);
					actualValues.put("PHONE_TYPE",phoneType);
					// database records and request records are being
					// compared
					
					   WSClient.writeToReport(LogStatus.INFO, "<b>Validating the phone details </b>");
					WSAssert.assertEquals(actualValues,db2, false);

				}
				
				}else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
				}
			
		} else {
			WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Profile -----Blocked");
		}
	}else{
		WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
	}
	}
	catch(Exception e)
	{
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	}
}
@Test(groups = {"minimumRegression", "UpdatePhone", "Name", "OWS" })

public void updatePhone_42208(){
	try{
		String testName = "updatePhone_42208";
		WSClient.startTest(testName,
				"Verify that error message is populated when invalid phoneType is passed",
				"minimumRegression");
		String prerequisite[]={"CommunicationType"};
		if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
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
		String query;

		 WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
		
		// Prerequisite 1 - create profile
		OPERALib.setOperaHeader(uname);
		String profileID = CreateProfile.createProfile("DS_11");
		if (!profileID.equals("error")) {
			

				
			WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
				WSClient.setData("{var_profileID}", profileID);
				query=WSClient.getQuery("QS_04");
				LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
				String phoneid = phn.get("PHONE_ID");
				if(!phoneid.equals("")){
				WSClient.setData("{var_phoneID}", phoneid);

				// OWS update phone

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				WSClient.setData("{var_phoneType}",WSClient.getKeywordData("{KEYWORD_RANDSTR_6}"));
				String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_01");
				String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
				if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
				}

				if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on
					 * the response
					 ****/

					String message = WSAssert.getElementValue(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
				}
				if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on
					 * the response
					 ****/

					String message = WSAssert.getElementValue(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
				}
				if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, " update phone is Unsuccessful!invalid phoneType passed");
					

				} else {
					WSClient.writeToReport(LogStatus.FAIL,
							"The phone has been updated successfully! ERROR! ");
				}
				
				}else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
				}
			
		} else {
			WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Profile -----Blocked");
		}
	}else{
		WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
	}
	}
	catch(Exception e)
	{
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	}
}
@Test(groups = {"minimumRegression", "UpdatePhone", "Name", "OWS" })

public void updatePhone_42222(){
	try{
		String testName = "updatePhone_42222";
		WSClient.startTest(testName,
				"Verify that secondary phone is updated to primary when minimum required data is passed",
				"minimumRegression");
		String prerequisite[]={"CommunicationType"};
		if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
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
		String query;

		 WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
		
		// Prerequisite 1 - create profile
		OPERALib.setOperaHeader(uname);
		String profileID = CreateProfile.createProfile("DS_46");
		if (!profileID.equals("error")) {
			

				
			WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
				WSClient.setData("{var_profileID}", profileID);
				query=WSClient.getQuery("QS_08");
				LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
				String phoneid = phn.get("PHONE_ID");
				if(!phoneid.equals("")){
				WSClient.setData("{var_phoneID}", phoneid);
				WSClient.setData("{var_phnNumber_N}", phn.get("PHONE_NUMBER"));
				WSClient.setData("{var_Ytrue}", "true");
				// OWS update phone

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				String updatePhoneReq = WSClient.createSOAPMessage("OWSUpdatePhone", "DS_02");
				String updatePhoneResponseXML = WSClient.processSOAPMessage(updatePhoneReq);
				if (WSAssert.assertIfElementExists(updatePhoneResponseXML, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updatePhoneResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the UpdatePhone response is :----</b>" + message);
				}

				if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on
					 * the response
					 ****/

					String message = WSAssert.getElementValue(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the UpdatePhone response is :---</b>" + message);
				}
				if (WSAssert.assertIfElementExists(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on
					 * the response
					 ****/

					String message = WSAssert.getElementValue(updatePhoneResponseXML,
							"UpdatePhoneResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDSerror displayed in the UpdatePhone response is :---</b>" + message);
				}
				if (WSAssert.assertIfElementValueEquals(updatePhoneResponseXML,
						"UpdatePhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Successfully executed update phone request</b>");
					
					 query=WSClient.getQuery("CreateProfile","QS_08");
					LinkedHashMap<String, String> db = WSClient.getDBRow(query);
					LinkedHashMap<String, String> db2=new LinkedHashMap<String,String>(); ;
					db2.put("PHONE_ID", db.get("PHONE_ID"));
					db2.put("PHONE_NUMBER",db.get("PHONE_NUMBER"));
					db2.put("PRIMARY_YN", db.get("PRIMARY_YN"));
					LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
					
					// request records are being stored in different
					// variables
					
					String phoneID=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_operaId", XMLType.REQUEST);
					String phoneNo=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_PhoneNumber", XMLType.REQUEST);
					String primary_yn=WSClient.getElementValue(updatePhoneReq, "UpdatePhoneRequest_NamePhone_primary", XMLType.REQUEST);
				
					actualValues.put("PHONE_ID", phoneID);
					actualValues.put("PHONE_NUMBER",phoneNo);
					actualValues.put("PRIMARY_YN",primary_yn);
					
					// database records and request records are being
					// compared
					
					   WSClient.writeToReport(LogStatus.INFO, "<b>Validating the phone details </b>");
					WSAssert.assertEquals(actualValues,db2, false);

				}
				
				}else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Phone ID's failed!------ Create Profile -----Blocked");
				}
			
		} else {
			WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Create Profile -----Blocked");
		}
	}else{
		WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Communication Type Not Available**********");
	}
	}
	catch(Exception e)
	{
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	}
}

}