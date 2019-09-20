package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;
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

public class FetchGuestCardList extends WSSetUp {
	//Creating Profile to which EMAIL will be inserted
			public String createProfile(String ds) 
			{
					String profileID = "";
					try 
					{
						String resortOperaValue = OPERALib.getResort();

		               
		                WSClient.setData("{var_resort}", resortOperaValue);
		                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);

		               
		                String uname = OPERALib.getUserName();
		                OPERALib.setOperaHeader(uname);
		                String createProfileReq = WSClient.createSOAPMessage("CreateProfile", ds);
		                String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
		                
		                if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true))
		                {
		                      if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_ProfileIDList_UniqueID_ID", true)) 
		                      {
		                             profileID = WSClient.getElementValue(createProfileResponseXML, "CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
		                             WSClient.setData("{var_profileID}", profileID);
		                      }
		                      else 
		                      {
		                    	  WSClient.writeToReport(LogStatus.WARNING , "No profile ID Found");
		                      }
		                }
		                else 
		                {
		                	WSClient.writeToReport(LogStatus.WARNING , "Profile creation Failed");
		                }
					}
					
					catch(Exception e) 
					{
						WSClient.writeToReport(LogStatus.FAIL, "Exception occured in test due to:" + e);  
					}
					return profileID;
			}
			
			
			@Test(groups = {"sanity", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38722() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38722";
					WSClient.startTest(testName, "Verify that membership of a profile is fetched correctly", "sanity");
					String prerequisite[]={"MembershipType","MembershipLevel"};
					if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
						String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);
		                String resort = OPERALib.getResort();
		                String chain=OPERALib.getChain();
		                WSClient.setData("{var_chain}", chain);
		                String uname = OPERALib.getUserName();
		                OPERALib.setOperaHeader(uname);
		                WSClient.setData("{var_resort}", resort);
					String profileID1 = CreateProfile.createProfile("DS_06");
					if (!profileID1.equals("error")) {

						 WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID1+"</b>");
		           	 	String channel = OWSLib.getChannel();
			            
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			           
			            String query;
			            WSClient.setData("{var_profileID}", profileID1);
			            String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						WSClient.setData("{var_memType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						 OPERALib.setOperaHeader(uname);
						  String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
			               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			            
							if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									true)) {
								 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
								
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_01");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"The OPERA error displayed in the FetchGuestCardList response is : " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"The GDSError displayed in the FetchGuestCardList response is : " + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					            HashMap<String,String> xPath = new HashMap<String,String>();
			            		xPath.put("FetchGuestCardListResponse_GuestCardList_NameMembership_operaId", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipType", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipNumber", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipLevel", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		LinkedHashMap<String, String> actual=WSClient.getSingleNodeList(fetchGuestCardListRes, xPath, false, XMLType.RESPONSE);
			            		//Values from DB Actual values 
			            		query=WSClient.getQuery("QS_01");
			            		
			            		LinkedHashMap<String, String> expected=WSClient.getDBRow(query);
							    
			            		//Verifying the values if both are equal 
			            		WSAssert.assertEquals(expected,actual,false);
								}
								
							}
							else
								WSClient.writeToReport(LogStatus.WARNING, "Problem in creating memberships");
			        } 
					else
						WSClient.writeToReport(LogStatus.WARNING, "Problem in prerequisite--Create Profile");
					} 
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			
		//minimumRegression:Case1:Inactive membership fetched	
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38724() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38724";
					WSClient.startTest(testName, "Verify that an inactive membership is fetched  when returnRecordsInactive field in FetchGuestCardList request is set to true", "minimumRegression");
					String prerequisite[]={"MembershipType","MembershipLevel"};
					if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
						String chain=OPERALib.getChain();
		                WSClient.setData("{var_chain}", chain);
		                String resortOperaValue = OPERALib.getResort();

			               
		                WSClient.setData("{var_resort}", resortOperaValue);
		                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);
                        String uname = OPERALib.getUserName();
		                OPERALib.setOperaHeader(uname);
					   String profileID = CreateProfile.createProfile("DS_06");
					   WSClient.writeToReport(LogStatus.INFO, profileID);
			          if(!profileID.equals("error")) 
			           {
			        	  WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID+"</b>");
			            String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			           
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			           
			            String query;
			            WSClient.setData("{var_profileID}", profileID);
			            String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						WSClient.setData("{var_memType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						 OPERALib.setOperaHeader(uname);
						  String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_05");
			               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			            
							if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									true)) {
								 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            WSClient.setData("{var_inactive}", "true");
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_02");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is : ---</b>" + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is :--</b> " + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					            HashMap<String,String> xPath = new HashMap<String,String>();
			            		xPath.put("FetchGuestCardListResponse_GuestCardList_NameMembership_operaId", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipType", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipNumber", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipLevel", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		LinkedHashMap<String, String> actual=WSClient.getSingleNodeList(fetchGuestCardListRes, xPath, false, XMLType.RESPONSE);
			            		//Values from DB Actual values 
			            		query=WSClient.getQuery("QS_01");
			            		
			            		LinkedHashMap<String, String> expected=WSClient.getDBRow(query);
							    
			            		//Verifying the values if both are equal 
			            		WSAssert.assertEquals(expected,actual,false);
								}
								
							}
							else
								WSClient.writeToReport(LogStatus.WARNING, "Problem in creating memberships");
			        } 
			        else
			        	WSClient.writeToReport(LogStatus.WARNING, "Problem in pre-requisite--Create Profile");
			        	
					} 
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			
			//minimumRegressionCase2 :--profile with no memberships is fetched
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38725() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38725";
					WSClient.startTest(testName, "Verify that success message but empty membershipList  when profileID with no membership attached is passed ", "minimumRegression");
					String resortOperaValue = OPERALib.getResort();
                    WSClient.setData("{var_resort}", resortOperaValue);
	                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	                WSClient.setData("{var_fname}", fname);
	                WSClient.setData("{var_lname}", lname);
                    String uname = OPERALib.getUserName();
	                OPERALib.setOperaHeader(uname);
					String profileID =CreateProfile.createProfile("DS_06");
					WSClient.writeToReport(LogStatus.INFO, profileID);
			        if(!profileID.equals("error")) 
			        {
			        	 WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID+"</b>");
			            String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			          
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			           
			           
			            WSClient.setData("{var_profileID}", profileID);
			           
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_01");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is :--</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is :---</b>" + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					              
					               if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
											"FetchGuestCardListResponse_GuestCardList_NameMembership", true)) {
					            	   WSClient.writeToReport(LogStatus.FAIL, "Trying to fetch memberships when not attached to profile");
					            	   
					               }
					               else
					            	   WSClient.writeToReport(LogStatus.PASS, "operation successful");
								}
								
							}
			        else
			        	WSClient.writeToReport(LogStatus.WARNING,"Problem in pre-requisite--Create Profile");
			          
					
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			
//			public boolean createMembership() {
//				try {
//					String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
//		               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
//		               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
//								false)) 
//		            	   return true;
//		               else 
//		            	   return false;
//				}
//				catch(Exception e) 
//				{   
//					WSClient.writeToReport(LogStatus.FAIL, "Exception occured in test due to:" + e);
//					return false;
//					  
//				}
//				}
//			//minimumRegression:Case3:Two memberships being fetched	
//			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
//			public void fetchPhoneList_38() 
//			{
//				try 
//				{
//					String testName = "fetchGuestCardList_38";
//					WSClient.startTest(testName, "Verify if Guest list with atleast 2 memberships is fetched and is the same as in DB when nameid of profile is provided in request", "minimumRegression");
//					String prerequisite[]={"MembershipType","MembershipLevel"};
//					if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
//					String profileID = createProfile("DS_06");
//					WSClient.writeToReport(LogStatus.INFO, profileID);
//			        if(profileID != "") 
//			        {
//
//			            String resort = OPERALib.getResort();
//		           	 	String channel = OWSLib.getChannel();
//			            String uname = OPERALib.getUserName();
//			            String pwd = OPERALib.getPassword();
//			            String channelType = OWSLib.getChannelType(channel);
//			            String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
//			            WSClient.setData("{var_resort}", resort);
//			            boolean flag = true;
//			            String query;
//			            WSClient.setData("{var_profileID}", profileID);
//			            String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
//						WSClient.setData("{var_nameOnCard}",memName);
//						for(int i=1;i<=2;i++) {
//							String dataset = "DS_0" + i; 
//						WSClient.setData("{var_memType}",
//								OperaPropConfig.getDataSetForCode("MembershipType", dataset));
//						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", dataset));
//						 OPERALib.setOperaHeader(uname);
//						 flag = flag && createMembership();
//						  
//						}
//							if (flag) {
//								
//					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
//					         
//					            
//								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_01");
//					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
//					            
//					            
//					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
//										true)) {
//
//									/*
//									 * Verifying that the error message is populated on
//									 * the response
//									 */
//
//									String message = WSAssert.getElementValue(fetchGuestCardListRes,
//											"Result_Text_TextElement", XMLType.RESPONSE);
//									WSClient.writeToReport(LogStatus.INFO,
//											"<b>The error displayed in the FetchGuestCardList response is----</b> :" + message);
//								}
//								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
//										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {
//
//									/*
//									 * Verifying whether the error Message is populated
//									 * on the response
//									 */
//									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
//											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//									
//										WSClient.writeToReport(LogStatus.INFO,
//												"The OPERA error displayed in the FetchGuestCardList response is : " + message1);
//									
//
//									
//								}
//								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
//										"FetchGuestCardListResponse_Result_GDSError", true)) {
//
//									/*
//									 * Verifying whether the error Message is populated
//									 * on the response
//									 */
//									
//
//									String message = WSAssert.getElementValue(fetchGuestCardListRes,
//											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
//				
//										WSClient.writeToReport(LogStatus.INFO,
//												"The GDSError displayed in the FetchGuestCardList response is : " + message);
//									
//								}
//								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
//										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//					            HashMap<String,String> xPath = new HashMap<String,String>();
//			            		xPath.put("FetchGuestCardListResponse_GuestCardList_NameMembership_operaId", "FetchGuestCardListResponse_GuestCardList_NameMembership");
//			            		xPath.put("GuestCardList_NameMembership_membershipType", "FetchGuestCardListResponse_GuestCardList_NameMembership");
//			            		xPath.put("GuestCardList_NameMembership_membershipNumber", "FetchGuestCardListResponse_GuestCardList_NameMembership");
//			            		xPath.put("GuestCardList_NameMembership_membershipLevel", "FetchGuestCardListResponse_GuestCardList_NameMembership");
//			            		LinkedHashMap<String, String> actual=WSClient.getSingleNodeList(fetchGuestCardListRes, xPath, false, XMLType.RESPONSE);
//			            		//Values from DB Actual values 
//			            		query=WSClient.getQuery("QS_01");
//			            		
//			            		LinkedHashMap<String, String> expected=WSClient.getDBRow(query);
//							    
//			            		//Verifying the values if both are equal 
//			            		WSAssert.assertEquals(expected,actual,false);
//								}
//								
//							}
//							else
//								WSClient.writeToReport(LogStatus.WARNING, "Problem in creating memberships");
//			        }  
//					} 
//				}
//					catch (Exception e) {
//									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
//				}
//
//			}
			
			//minimumRegression:Case3:Two memberships being fetched	
			
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38727() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38727";
					WSClient.startTest(testName, "Verify that memberships of a profile with 2 memberships attached, are fetched correctly ", "minimumRegression");
					String prerequisite[]={"MembershipType","MembershipLevel"};
					if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
						String chain=OPERALib.getChain();
		                WSClient.setData("{var_chain}", chain);
		                String resortOperaValue = OPERALib.getResort();
		                WSClient.setData("{var_resort}", resortOperaValue);
		                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);

		               
		                String uname = OPERALib.getUserName();
		                OPERALib.setOperaHeader(uname);
					    String profileID = CreateProfile.createProfile("DS_06");
					    WSClient.writeToReport(LogStatus.INFO, profileID);
			        if(!profileID.equals("error")) 
			        {
			        	 WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID+"</b>");
			            String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			            boolean flag = true;
			            String query;
			            WSClient.setData("{var_profileID}", profileID);
			            String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						
							 
//						WSClient.setData("{var_memType}",
//								"BOOK");
//						WSClient.setData("{var_memLevel}", "CLASSIC");
						WSClient.setData("{var_memType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
						WSClient.setData("{var_memLevel}",  OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
						 OPERALib.setOperaHeader(uname);
						 String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
			               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									true)) {
			            	   WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
			            	   flag = flag && true;
			               }
			               else
			            	   flag=false;
			               WSClient.setData("{var_memType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
							WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
			                createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
			                createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									true)) {
			            	   WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
			            	   flag = flag && true;
			               }
			               else
			            	   flag=false;
			
						  
						
							if (flag) {
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_01");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is :---</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is :---</b> " + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					            HashMap<String,String> xPath = new HashMap<String,String>();
			            		xPath.put("FetchGuestCardListResponse_GuestCardList_NameMembership_operaId", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipType", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipNumber", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipLevel", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		List<LinkedHashMap<String, String>> actual=WSClient.getMultipleNodeList(fetchGuestCardListRes, xPath, false, XMLType.RESPONSE);
			            		//Values from DB Actual values 
			            		query=WSClient.getQuery("QS_01");
			            		
			            		List<LinkedHashMap<String, String>> expected=WSClient.getDBRows(query);
							    
			            		//Verifying the values if both are equal 
			            		WSAssert.assertEquals(actual,expected,false);
								}
								
							}
							else
								WSClient.writeToReport(LogStatus.WARNING, "Problem in creating memberships");
			        }
			        else
			        	WSClient.writeToReport(LogStatus.WARNING, "Problem in prerequisite-Create Profile");
					} 
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			//minimumRegressionCase4 :--invalid profileID is passed 
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38730() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38730";
					WSClient.startTest(testName, "verify that FAIL flag is populated on response when invalid NameID is passed  ", "minimumRegression");
					

			            String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			            String uname = OPERALib.getUserName();
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			           
			           
			        
			           
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_03");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is :</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is :</b> " + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "FAIL", false)) {
									WSClient.writeToReport(LogStatus.PASS, "Cant fetch memberships for an invalid profile");
									
								}
								else if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.writeToReport(LogStatus.FAIL, "ERROR!!Fetching memberships for an invalid profile");
					              
								}
								
							}
			          
					
				
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			
			
			//minimumRegressionCase5 :-- INTERNAL is replaced with external 
			
			
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38731() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38731";
					WSClient.startTest(testName, "Verify when type is passed as external,result flag populated as FAIL", "minimumRegression");
					
					String profileID = createProfile("DS_06");
					WSClient.writeToReport(LogStatus.INFO, profileID);
					String chain=OPERALib.getChain();
	                WSClient.setData("{var_chain}", chain);

		            String resort = OPERALib.getResort();
	           	 	String channel = OWSLib.getChannel();
		            String uname = OPERALib.getUserName();
		            String pwd = OPERALib.getPassword();
		            String channelType = OWSLib.getChannelType(channel);
		            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		            WSClient.setData("{var_resort}", resort);
			        if(profileID != "") 
			        {
                        
			           
			        	 WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID+"</b>");
			        	 
			            WSClient.setData("{var_profileID}", profileID);
			           
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_04");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is :</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is : </b>" + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "FAIL", false)) {
									WSClient.writeToReport(LogStatus.PASS, "Cant fetch memberships when type is not INTERNAL"); 
									
								}
								else if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.writeToReport(LogStatus.FAIL, "ERROR!!Fetching memberships for an invalid profile");
					              
								}
								
							}
			          
					
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			//minimumRegressionCase6 :--profileID is passed as TEXT
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38732() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38732";
					WSClient.startTest(testName, "verify that FAIL flag is populated on response when NameID is passed as text ", "minimumRegression");
					

			            String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			            String uname = OPERALib.getUserName();
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			           
			            String chain=OPERALib.getChain();
		                WSClient.setData("{var_chain}", chain);
			        
			           
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_05");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is :</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is :</b> " + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "FAIL", false)) {
									WSClient.writeToReport(LogStatus.PASS, "Cant fetch memberships for an invalid profile");
									
								}
								else if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.writeToReport(LogStatus.FAIL, "ERROR!!Fetching memberships for an invalid profile");
					              
								}
								
							}
			          
					
				
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			//minimumRegression:Case7:Both inactive and active being retrieved 
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchGuestCardList_38733() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38733";
					WSClient.startTest(testName, "Verify that Guest list with  2 memberships, one is active and one is inactive both are fetched and is the same as in DB when fetchRecordsInactive field set to true in request", "minimumRegression");
					String prerequisite[]={"MembershipType","MembershipLevel"};
					if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
						String chain=OPERALib.getChain();
		                WSClient.setData("{var_chain}", chain);
						String resortOperaValue = OPERALib.getResort();
                        WSClient.setData("{var_resort}", resortOperaValue);
		                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);
                        String uname = OPERALib.getUserName();
		                OPERALib.setOperaHeader(uname);
					    String profileID = CreateProfile.createProfile("DS_06");
					    WSClient.writeToReport(LogStatus.INFO, profileID);
			           if(!profileID.equals("error"))
			           {
			        	   WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID+"</b>");
                        String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			            boolean flag = true;
			            String query;
			            WSClient.setData("{var_profileID}", profileID);
			            String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						
							 
//						WSClient.setData("{var_memType}",
//								"BOOK");
//						WSClient.setData("{var_memLevel}", "CLASSIC");
						
						
						WSClient.setData("{var_memType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_01"));
						WSClient.setData("{var_memLevel}",  OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_01"));
						
						 OPERALib.setOperaHeader(uname);
						 String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_03");
			               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									true)) {
			            	   WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
			            	   flag = flag && true;
			               }
			               else
			            	   flag=false;
			               WSClient.setData("{var_memType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
							WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
			                createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_05");
			                createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			               if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									true)) {
			            	   WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached membership</b>");
			            	   flag = flag && true;
			               }
			               else
			            	   flag=false;
						  
						
							if (flag) {
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            WSClient.setData("{var_inactive}", "true");
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_02");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is : --</b>" + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is :---</b> " + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					            HashMap<String,String> xPath = new HashMap<String,String>();
			            		xPath.put("FetchGuestCardListResponse_GuestCardList_NameMembership_operaId", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipType", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipNumber", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		xPath.put("GuestCardList_NameMembership_membershipLevel", "FetchGuestCardListResponse_GuestCardList_NameMembership");
			            		List<LinkedHashMap<String, String>> actual=WSClient.getMultipleNodeList(fetchGuestCardListRes, xPath, false, XMLType.RESPONSE);
			            		//Values from DB Actual values 
			            		query=WSClient.getQuery("QS_01");
			            		
			            		List<LinkedHashMap<String, String>> expected=WSClient.getDBRows(query);
							    
			            		//Verifying the values if both are equal 
			            		WSAssert.assertEquals(actual,expected,false);
								}
								
							}
							else
								WSClient.writeToReport(LogStatus.WARNING, "Problem in creating memberships");
			        }
			           else
			        	   WSClient.writeToReport(LogStatus.WARNING, "Problem in prerequisite-Create Profile");
					} 
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
//minimumRegressionCase5 :-- when type is passed as empty other than internal
			
			
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchPhoneList_38734() 
			{
				try 
				{
					String testName = "fetchGuestCardList_38734";
					WSClient.startTest(testName, "Verify when type is passed as empty other than INTERNAL,result flag populated as FAIL", "minimumRegression");
					String resortOperaValue = OPERALib.getResort();
                    WSClient.setData("{var_resort}", resortOperaValue);
	                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	                WSClient.setData("{var_fname}", fname);
	                WSClient.setData("{var_lname}", lname);
                    String uname = OPERALib.getUserName();
	                OPERALib.setOperaHeader(uname);
					String profileID = CreateProfile.createProfile("DS_06");
					WSClient.writeToReport(LogStatus.INFO, profileID);
			        if(!profileID.equals("error")) 
			        {
                         WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID+"</b>");
			            String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			            
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			            String chain=OPERALib.getChain();
		                WSClient.setData("{var_chain}", chain);
			           
			            WSClient.setData("{var_profileID}", profileID);
			           
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_06");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is :</b> " + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is : </b>" + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "FAIL", false)) {
									WSClient.writeToReport(LogStatus.PASS, "Cant fetch memberships when type is empty other than INTERNAL"); 
									
								}
								else if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.writeToReport(LogStatus.FAIL, "ERROR!!Fetching memberships for an invalid profile");
					              
								}
								
							}
			        else
			        	WSClient.writeToReport(LogStatus.WARNING, "Problem in pre-requisite--Create Profile");
			          
					
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			//minimumRegression:Case1:Inactive membership not fetched 	
			
			@Test(groups = {"minimumRegression", "FetchGuestCardList", "Name","OWS" })
			public void fetchGuestCardList_41905() 
			{
				try 
				{
					String testName = "fetchGuestCardList_41905";
					WSClient.startTest(testName, "Verify that an inactive membership is not getting fetched ", "minimumRegression");
					String prerequisite[]={"MembershipType","MembershipLevel"};
					if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
						String chain=OPERALib.getChain();
		                WSClient.setData("{var_chain}", chain);
		                String resortOperaValue = OPERALib.getResort();

			               
		                WSClient.setData("{var_resort}", resortOperaValue);
		                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
		                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
		                WSClient.setData("{var_fname}", fname);
		                WSClient.setData("{var_lname}", lname);
                        String uname = OPERALib.getUserName();
		                OPERALib.setOperaHeader(uname);
					   String profileID = CreateProfile.createProfile("DS_06");
					   WSClient.writeToReport(LogStatus.INFO, profileID);
			          if(!profileID.equals("error")) 
			           {
			        	  WSClient.writeToReport(LogStatus.INFO,"<b>"+"Created Profile-----"+profileID+"</b>");
			            String resort = OPERALib.getResort();
		           	 	String channel = OWSLib.getChannel();
			           
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            WSClient.setData("{var_resort}", resort);
			           
			            String query;
			            WSClient.setData("{var_profileID}", profileID);
			            String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}")).toUpperCase();
						WSClient.setData("{var_nameOnCard}",memName);
						WSClient.setData("{var_memType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						 OPERALib.setOperaHeader(uname);
						  String createMembershipReq = WSClient.createSOAPMessage("CreateMembership","DS_05");
			               String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);
			            
							if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success",
									true)) {
								 WSClient.writeToReport(LogStatus.INFO,"<b>Successfully attached inactive membership</b>");
								
					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					         
					            WSClient.setData("{var_inactive}", "true");
								String fetchGuestCardListReq = WSClient.createSOAPMessage("OWSFetchGuestCardList","DS_01");
					            String fetchGuestCardListRes = WSClient.processSOAPMessage(fetchGuestCardListReq);
					            
					            
					            if (WSAssert.assertIfElementExists(fetchGuestCardListRes, "Result_Text_TextElement",
										true)) {

									/*
									 * Verifying that the error message is populated on
									 * the response
									 */

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the FetchGuestCardList response is----</b> :" + message);
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_OperaErrorCode", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									String message1 = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the FetchGuestCardList response is : ---</b>" + message1);
									

									
								}
								if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_GDSError", true)) {

									/*
									 * Verifying whether the error Message is populated
									 * on the response
									 */
									

									String message = WSAssert.getElementValue(fetchGuestCardListRes,
											"FetchGuestCardListResponse_Result_GDSError", XMLType.RESPONSE);
				
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the FetchGuestCardList response is :--</b> " + message);
									
								}
								if (WSAssert.assertIfElementValueEquals(fetchGuestCardListRes,
										"FetchGuestCardListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									if (WSAssert.assertIfElementExists(fetchGuestCardListRes,
											"FetchGuestCardListResponse_GuestCardList_NameMembership", true)) {
					            	   WSClient.writeToReport(LogStatus.FAIL, "Trying to fetch inactive memberships!ERROR!!!");
					            	   
					               }
					               else
					            	   WSClient.writeToReport(LogStatus.PASS, "Inactive memberships not fetched,Successful");
								}
								
							}
							else
								WSClient.writeToReport(LogStatus.WARNING, "Problem in creating memberships");
			        } 
			        else
			        	WSClient.writeToReport(LogStatus.WARNING, "Problem in pre-requisite--Create Profile");
			        	
					} 
				}
					catch (Exception e) {
									WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}
			}