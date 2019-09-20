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

public class FetchPhoneList extends WSSetUp{

	//Creating Profile to which EMAIL will be inserted
		public String createProfile(String ds) 
		{
				String profileID = "";
				try 
				{
					String resortOperaValue = OPERALib.getResort();

	               
	                WSClient.setData("{var_resort}", resortOperaValue);
	                WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
	                WSClient.setData("{var_phoneType1}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
	                WSClient.setData("{var_phoneType2}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
	                

	               
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
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
				}
				return profileID;
		}
		
		
		
		//sanity case
		@Test(groups = {"sanity", "FetchPhoneList", "Name","OWS" })
		public void fetchPhoneList_38323() 
		{
			try 
			{
				String testName = "fetchPhoneList_38323";
				WSClient.startTest(testName, "Verify that phone list of a profile is fetched correctly", "sanity");
				String prerequisite[]={"CommunicationType"};
				if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
					String resort = OPERALib.getResort();
	                String uname = OPERALib.getUserName();
	                OPERALib.setOperaHeader(uname);
	                String chain = OPERALib.getChain();
	    			WSClient.setData("{var_chain}", chain);
	                WSClient.setData("{var_resort}", resort);
	                WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
	               
					String profileID = CreateProfile.createProfile("DS_11");
					WSClient.setData("{var_profileID}", profileID);
					if (!profileID.equals("error")) {

		            WSClient.writeToReport(LogStatus.INFO,"<b>Profile is created with a phone attached------"+profileID+"</b>");
	           	 	String channel = OWSLib.getChannel();
		            
		            String pwd = OPERALib.getPassword();
		            String channelType = OWSLib.getChannelType(channel);
		            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
		            String query;
		            
	           	 	
		            
		            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_01");
		            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
		            if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Fetch phone list response is----</b> :" + message);
					}

					if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
					}
		                          
		            if(WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_resultStatusFlag","SUCCESS",false))
		            {
		            	if(WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList", false)) {
		            		if(WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList_NamePhone", false)) {
		            			WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched phonelist</b>");
		            			HashMap<String,String> xPath = new HashMap<String,String>();
			            		xPath.put("NamePhoneList_NamePhone_PhoneNumber", "FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_phoneType", "FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_phoneRole","FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_primary","FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		LinkedHashMap<String, String> actualValues=WSClient.getSingleNodeList(fetchPhoneListResponseXML, xPath, false, XMLType.RESPONSE);
			            		//Values from DB Actual values 
			            		query=WSClient.getQuery("QS_01");
			            		LinkedHashMap<String, String> expectedValues=WSClient.getDBRow(query);
							    
			            		//Verifying the values if both are equal 
			            		
			            		WSClient.writeToReport(LogStatus.INFO, "<b>Validating phone details</b>");
			            		WSAssert.assertEquals(expectedValues,actualValues, false);
		            		}
		            	}
		            }  
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
		
		//Minimum Regression case:1--fetching phone list with atleast 2 phones
  
		@Test(groups = {"minimumRegression", "FetchPhoneList", "Name", "OWS" })
		public void fetchPhoneList_38363() 
		{
			try 
			{
				String testName = "fetchPhoneList_38363";
				WSClient.startTest(testName, "Verify if phones list with atleast 2 phones attached is being fetched and is the same as in DB when nameid of profile is provided in request.", "minimumRegression");
				String prerequisite[]={"CommunicationType"};
				if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
					String resortOperaValue = OPERALib.getResort();    
	                WSClient.setData("{var_resort}", resortOperaValue);
	                WSClient.setData("{var_phoneType1}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
	                WSClient.setData("{var_phoneType2}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
	                String uname = OPERALib.getUserName();
	                OPERALib.setOperaHeader(uname);
					String chain = OPERALib.getChain();
					WSClient.setData("{var_chain}", chain);
				   String profileID = CreateProfile.createProfile("DS_14");
				  WSClient.writeToReport(LogStatus.INFO, profileID);
		        if(!profileID.equals("error")) 
		        {
		        	 WSClient.writeToReport(LogStatus.INFO,"<b>"+"Profile is created with 2 phones attached-----"+profileID+"</b>");
		        	WSClient.setData("{var_profileID}", profileID);
		            String resort = OPERALib.getResort();
	           	 	String channel = OWSLib.getChannel();
		            String pwd = OPERALib.getPassword();
		            String channelType = OWSLib.getChannelType(channel);
		            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	           	 	
		            String query;
		            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_01");
		            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
		            if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the Fetch phone list response is----</b> :" + message);
					}

					if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
					}
		                          
		            if(WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_resultStatusFlag","SUCCESS",false))
		            {
		            	if(WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList", false)) {
		            		if(WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList_NamePhone", false)) {
		            			WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched phonelist</b>");
		            			HashMap<String,String> xPath = new HashMap<String,String>();
			            		xPath.put("NamePhoneList_NamePhone_PhoneNumber", "FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_phoneType", "FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_phoneRole","FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_primary","FetchPhoneListResponse_NamePhoneList_NamePhone");
			            		List<LinkedHashMap<String, String>> actualValues=WSClient.getMultipleNodeList(fetchPhoneListResponseXML, xPath, false, XMLType.RESPONSE);
			            		//Values from DB Actual values 
			            		query=WSClient.getQuery("QS_01");
			            		List<LinkedHashMap<String, String>> expectedValues=WSClient.getDBRows(query);
							  
			            		//Verifying the values if both are equal 
			            		
			            		WSClient.writeToReport(LogStatus.INFO, "<b>Validating phone details</b>");
			            		WSAssert.assertEquals(actualValues,expectedValues,  false);
		            		}
		            	}
		            }  
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
		
		
		//MininimumRegression case:2--when invalid nameID is passed
		
		@Test(groups = {"minimumRegression", "FetchPhoneList", "Name", "OWS" })
		public void fetchPhoneList_38546() 
		{
			try 
			{
				String testName = "fetchPhoneList_38546";
				WSClient.startTest(testName, "Verify that an error message is populated when an invalid nameID is passed","minimumRegression");
		            String resort = OPERALib.getResort();
	           	 	String channel = OWSLib.getChannel();
		            String uname = OPERALib.getUserName();
		            String pwd = OPERALib.getPassword();
		            String chain = OPERALib.getChain();
					WSClient.setData("{var_chain}", chain);
		            String channelType = OWSLib.getChannelType(channel);
		            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	           	 	
		            
		            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_02");
		            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
					
				if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the Fetch phone list response is----</b> :" + message);
				}

				if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
							"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
				}
				if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
							"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
				}
				if (WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,
						"FetchPhoneListResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS, " fetch  phone is Unsuccessful when invalid nameID is passed!");

			} else if (WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,
					"FetchPhoneListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				WSClient.writeToReport(LogStatus.FAIL,
						"The Fetch operation is successfull! ERROR! ");
			}
			}
	         catch(Exception e)
	         {
	        	 WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
	         }
		}
		
		
		//MinimumRegression case 3:passing profile id with no phones attached
		
		
		@Test(groups = {"minimumRegression", "FetchPhoneList", "Name", "OWS" })
		public void fetchPhoneList_38547() 
		{
			try 
			{
				String testName = "fetchPhoneList_38547";
				WSClient.startTest(testName, "Verify that the response comes with a success flag but with empty phone list is populated when an profile id with no phones attached is passsed","minimumRegression");
		            String resort = OPERALib.getResort();
	           	 	String channel = OWSLib.getChannel();
		            String uname = OPERALib.getUserName();
		            String pwd = OPERALib.getPassword();
		            String chain = OPERALib.getChain();
					WSClient.setData("{var_chain}", chain);
		            String channelType = OWSLib.getChannelType(channel);
		            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		            String resortOperaValue = OPERALib.getResort();    
	                WSClient.setData("{var_resort}", resortOperaValue);
	                OPERALib.setOperaHeader(uname);
		            String profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, profileID);
			        if(!profileID.equals("error")) 
			        {
			        	 WSClient.writeToReport(LogStatus.INFO,"<b>Profile is created </b>");
			        WSClient.setData("{var_profileID}",profileID);	
		            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	           	 	
		            
		            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_01");
		            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
		            if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the Fetch phone list response is----</b> :" + message);
					}

		            if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
							true)) {

						/****
						 * Verifying whether the error Message is populated on the
						 * response
						 ****/

						String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
					}
		            if (WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,
							"FetchPhoneListResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						         
						      if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList", true)) {
						    	  
						    	  WSClient.writeToReport(LogStatus.FAIL, "fetching phoneList for a profile with no phones attached"); 
						      }
						      else
						    	  WSClient.writeToReport(LogStatus.PASS, "operation is successful but there are no phones attached to fetch and display");
		            }
		           
				     
				 else if (WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,
							"FetchPhoneListResponse_Result_resultStatusFlag", "FAIL", true)) {
						         WSClient.writeToReport(LogStatus.FAIL, " FetchPhoneList is successful!ERROR");
			} 
			
			}
			        else
			        	WSClient.writeToReport(LogStatus.WARNING, "Problem in creating profile,No profile id is returned");
			        
			}
	         catch(Exception e)
	         {
	        	 WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
	         }
		}
		
		
		
		
		//MininimumRegression case:4--when type is passed as empty string other than "INTERNAL"
		
				@Test(groups = {"minimumRegression", "FetchPhoneList", "Name", "OWS" })
				public void fetchPhoneList_38601() 
				{
					try 
					{
						String testName = "fetchPhoneList_38601";
						WSClient.startTest(testName, "Verify that an error message is populated when type is passed empty other than INTERNAL","minimumRegression");
						
				            String resort = OPERALib.getResort();
			           	 	String channel = OWSLib.getChannel();
				            String uname = OPERALib.getUserName();
				            String pwd = OPERALib.getPassword();
				            String chain = OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
				            String channelType = OWSLib.getChannelType(channel);
				            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				            String resortOperaValue = OPERALib.getResort();    
			                WSClient.setData("{var_resort}", resortOperaValue);
			                WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
			                
			                OPERALib.setOperaHeader(uname);
				            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				            String prerequisite[]={"CommunicationType"};
							if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
							String profileID = CreateProfile.createProfile("DS_11");
							WSClient.writeToReport(LogStatus.INFO, profileID);
					        if(!profileID.equals("error")) 
					        {
				            WSClient.setData("{var_profileID}",profileID);
				            WSClient.writeToReport(LogStatus.INFO,"<b>Profile is created</b>");
				            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_03");
				            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
							
						     if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) 
						     {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the Fetch phone list response is----</b> :" + message);
						    }

						     if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
									true)) {

								/****
								 * Verifying whether the error Message is populated on the
								 * response
								 ****/

								String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
										"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
							}
							if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
									true)) {

								/****
								 * Verifying whether the error Message is populated on the
								 * response
								 ****/

								String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
										"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
							}
						 if (WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,
									"FetchPhoneListResponse_Result_resultStatusFlag", "FAIL", false)) {
								         WSClient.writeToReport(LogStatus.PASS, " Fetch  is Unsuccessful!");
							}
				         else 
						WSClient.writeToReport(LogStatus.FAIL,
								"The Fetch operation is successfull! ERROR! ");
					
							}
					        else
					        	WSClient.writeToReport(LogStatus.WARNING, "Profile ID not returned--problem in creating profile");
							}
							else
								WSClient.writeToReport(LogStatus.WARNING, "Prequisite--CommunicationType not available--can't create profile");
					}	
			         catch(Exception e)
			         {
			        	 WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
			         }
				}
				//MininimumRegression case:5--when nameID is passed as null
				
				@Test(groups = {"minimumRegression", "FetchPhoneList", "Name", "OWS" })
				public void fetchPhoneList_38548() 
				{
					try 
					{
						String testName = "fetchPhoneList_38548";
						WSClient.startTest(testName, "Verify that an error message is populated when NameID is passed as null","minimumRegression");
				            String resort = OPERALib.getResort();
			           	 	String channel = OWSLib.getChannel();
				            String uname = OPERALib.getUserName();
				            String pwd = OPERALib.getPassword();
				            String chain = OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
				            String channelType = OWSLib.getChannelType(channel);
				            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			           	 	
				            
				            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_04");
				            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
							
						if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the Fetch phone list response is----</b> :" + message);
						}

						if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
								true)) {

							/****
							 * Verifying whether the error Message is populated on the
							 * response
							 ****/

							String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
									"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated on the
							 * response
							 ****/

							String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
									"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
						}
						if (WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_resultStatusFlag", "FAIL", false)) {
							WSClient.writeToReport(LogStatus.PASS, " fetch  phone is Unsuccessful!");
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"The Fetch operation is successfull! ERROR! ");
					}
					}
			         catch(Exception e)
			         {
			        	 WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
			         }
				}
//MininimumRegression case:6--when nameID is passed as STRING other than NUMERIC
				
				@Test(groups = {"minimumRegression", "FetchPhoneList", "Name", "OWS" })
				public void fetchPhoneList_38550() 
				{
					try 
					{
						String testName = "fetchPhoneList_38550";
						WSClient.startTest(testName, "Verify that an error message is populated when NameID is passed as string other than numeric","minimumRegression");
				            String resort = OPERALib.getResort();
			           	 	String channel = OWSLib.getChannel();
				            String uname = OPERALib.getUserName();
				            String pwd = OPERALib.getPassword();
				            String chain = OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
				            String channelType = OWSLib.getChannelType(channel);
				            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			           	 	
				            
				            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_05");
				            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
							
						if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) {

							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the Fetch phone list response is----</b> :" + message);
						}

						if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
								true)) {

							/****
							 * Verifying whether the error Message is populated on the
							 * response
							 ****/

							String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
									"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated on the
							 * response
							 ****/

							String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
									"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
						}
						if (WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,
								"FetchPhoneListResponse_Result_resultStatusFlag", "FAIL", false)) {
							WSClient.writeToReport(LogStatus.PASS, " fetch  phone is Unsuccessful!");
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"The Fetch operation is successfull! ERROR! ");
					}
					}
			         catch(Exception e)
			         {
			        	 WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
			         }
				}
				
				//only phone information is coming on response when profile has both phone and email details
				
				@Test(groups = {"minimumRegression", "FetchPhoneList", "Name","OWS" })
				public void fetchPhoneList_41925() 
				{
					try 
					{
						String testName = "fetchPhoneList_41925";
						WSClient.startTest(testName, "Verify that only phone information is coming on response when profile has both phone and email details", "minimumRegression");
						String prerequisite[]={"CommunicationType"};
						if(OperaPropConfig.getPropertyConfigResults(prerequisite)){
							String resort = OPERALib.getResort();
			                String uname = OPERALib.getUserName();
			                OPERALib.setOperaHeader(uname);
			                String chain = OPERALib.getChain();
			    			WSClient.setData("{var_chain}", chain);
			                WSClient.setData("{var_resort}", resort);
			                String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			                String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			                String phnNumber = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
			                WSClient.setData("{var_fname}", fname);
			                WSClient.setData("{var_lname}", lname);
			                WSClient.setData("{var_emailType2}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
			                WSClient.setData("{var_email2}",phnNumber);
			                WSClient.setData("{var_primary2}","true");
			                WSClient.setData("{var_primary}","true");
			                WSClient.setData("{var_emailType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
			                //WSClient.setData("{var_emailType}","PHONE");
			                WSClient.setData("{var_email}",fname+lname+"@mail.com");
							String profileID = CreateProfile.createProfile("DS_43");
							WSClient.setData("{var_profileID}", profileID);
							if (!profileID.equals("error")) {

				            WSClient.writeToReport(LogStatus.INFO,"<b>Profile is created with phone and email attached------"+profileID+"</b>");
			           	 	String channel = OWSLib.getChannel();
				            
				            String pwd = OPERALib.getPassword();
				            String channelType = OWSLib.getChannelType(channel);
				            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				            String query;
				            
			           	 	
				            
				            String fetchPhoneListReq = WSClient.createSOAPMessage("OWSFetchPhoneList", "DS_01");
				            String fetchPhoneListResponseXML = WSClient.processSOAPMessage(fetchPhoneListReq);
				            if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text", true)) {

								/****
								 * Verifying that the error message is populated on the
								 * response
								 ********/

								String message = WSAssert.getElementValue(fetchPhoneListResponseXML, "FetchPhoneListResponse_Result_Text",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Fetch phone list response is----</b> :" + message);
							}

							if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_OperaErrorCode",
									true)) {

								/****
								 * Verifying whether the error Message is populated on the
								 * response
								 ****/

								String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
										"FetchPhoneListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OPERA error displayed in the Fetch PhoneList response is :---</b> " + message);
							}
							if (WSAssert.assertIfElementExists(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_GDSError",
									true)) {

								/****
								 * Verifying whether the error Message is populated on the
								 * response
								 ****/

								String message = WSAssert.getElementValue(fetchPhoneListResponseXML,
										"FetchPhoneListResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDS error displayed in the Fetch PhoneList response is :---</b> " + message);
							}
				                          
				            if(WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML,"FetchPhoneListResponse_Result_resultStatusFlag","SUCCESS",false))
				            {
				            	if(WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList", false)) {
				            		if(WSAssert.assertIfElementExists(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList_NamePhone", false)) {
				            			WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched phonelist</b>");
//				            			HashMap<String,String> xPath = new HashMap<String,String>();
//					            		xPath.put("NamePhoneList_NamePhone_PhoneNumber", "FetchPhoneListResponse_NamePhoneList_NamePhone");
//					            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_phoneType", "FetchPhoneListResponse_NamePhoneList_NamePhone");
//					            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_phoneRole","FetchPhoneListResponse_NamePhoneList_NamePhone");
//					            		xPath.put("FetchPhoneListResponse_NamePhoneList_NamePhone_primary","FetchPhoneListResponse_NamePhoneList_NamePhone");
//					            		LinkedHashMap<String, String> actualValues=WSClient.getSingleNodeList(fetchPhoneListResponseXML, xPath, false, XMLType.RESPONSE);
//					            		//Values from DB Actual values 
//					            		query=WSClient.getQuery("QS_01");
//					            		LinkedHashMap<String, String> expectedValues=WSClient.getDBRow(query);
//									    
//					            		//Verifying the values if both are equal 
//					            		
//					            		WSClient.writeToReport(LogStatus.INFO, "<b>Validating phone details</b>");
//					            		WSAssert.assertEquals(expectedValues,actualValues, false);
					            		if(WSAssert.assertIfElementValueEquals(fetchPhoneListResponseXML, "FetchPhoneListResponse_NamePhoneList_NamePhone_phoneRole", "EMAIL",true)){
											WSClient.writeToReport(LogStatus.FAIL, "Email Information is displayed.");
										}else{
											WSClient.writeToReport(LogStatus.PASS, "Email Information is not displayed as expected.");
										}
										
				            		}
				            	}
				            }  
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
