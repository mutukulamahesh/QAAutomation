package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

/**
 * 
 * @author kankur
 *
 */

public class TravelAgentLookup extends WSSetUp
{
	
		public void setOWSHeader() 
		{
			try 
			{
				String resort = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String channel = OWSLib.getChannel();
			    String pwd = OPERALib.getPassword();
			    String channelType = OWSLib.getChannelType(channel);
			    String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			    OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			}
			catch(Exception e) 
			{
				WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
			}
		}
	
		//Travel Agent Lookup Sanity
		@Test(groups = { "sanity", "Name", "TravelAgentLookup", "OWS" })
		/* Method to fetch Travel Agent Profile by passing type as INTERNAL and name id*/
		public void travelAgentLookup_38405()
		{
			try
			{
				String testName = "travelAgentLookup_38405";
				WSClient.startTest(testName, "Verify that the travel agent profile is looked up when only name id is passed", "sanity");
				
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				//String channelType = OWSLib.getChannelType(channel);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
				String companyName = WSClient.getKeywordData("{KEYWORD_FNAME}")+" Travels Limited";
    			WSClient.setData("{var_companyName}", companyName);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
				String operaProfileID = CreateProfile.createProfile("DS_17");
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
				
		        if(!operaProfileID.equals("error")) 
		        {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					
					setOWSHeader();
		            //OWSLib.setOWSHeader(uname, pwd, owsresort, channelType, channelCarrier);
		            
//		            WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching Travel Agent Profile"+"</b>");
					String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_01");
					String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
					
					
					//Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
					{
	
						String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
					}
					
					//Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
					{
						String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The error code displayed in the response is :" + code);
					}
					
					
					//Checking For Fault Schema
					if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
					{
						String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
					}
					
					//Checking For Result Flag
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
					{
						if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) 
						{
							if(WSAssert.assertIfElementExists(travelAgentLookupRes, "ProfileDetails_ProfileIDs_UniqueID", false))
							{
								
								WSClient.setData("{var_profileID}", operaProfileID);
								
								String QS_01 = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);
								
								// Validating the fetched travel agent profile values from RES with the
								// Database
//								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Comparing Data populated in DB and Data in Response"+"</b>");
								
								String actualCompanyName = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyName", XMLType.RESPONSE);
								if(WSAssert.assertEquals(actualCompanyName, expectedRecord.get("COMPANY"), true))
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
								}
								else
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
								}
	
								String actualNameType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyType", XMLType.RESPONSE);
								if(WSAssert.assertEquals(actualNameType,expectedRecord.get("NAME TYPE"), true))
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
								}
								else
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
							}
						}
						
						//Checking if ResultstatusFlag is FAIL
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Travel Agent Lookup Failed!");
						}
						
						//Checking for GDSError
						if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
						{
							String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
	                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
						}
						
						
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
					}
						
		        }
		        else
		        {
		        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed------ Create Profile -----Blocked");
		        }
				
			}
	        catch (Exception ex) 
			{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
			}
			finally
			{
				
			}
			
		}
			
		//Travel Agent Lookup MR-1
		@Test(groups = { "minimumRegression", "Name", "TravelAgentLookup", "OWS" })
		/* Method to fetch Travel Agent Profile by passing all fields with valid data*/
		public void travelAgentLookup_38545()
		{
			try
			{
				String testName = "travelAgentLookup_38545";
				WSClient.startTest(testName, "Verify that the travel agent profile is looked up when all fields with valid data are passed", "minimumRegression");
				
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				//String channelType = OWSLib.getChannelType(channel);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
				String companyName = WSClient.getKeywordData("{KEYWORD_FNAME}")+" Travels Limited";
    			WSClient.setData("{var_companyName}", companyName);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
				String operaProfileID = CreateProfile.createProfile("DS_17");
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
				
		        if(!operaProfileID.equals("")) 
		        {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					
					setOWSHeader();
		            //OWSLib.setOWSHeader(uname, pwd, owsresort, channelType, channelCarrier);
		            
//		            WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching Travel Agent Profile"+"</b>");
					String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_02");
					String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
					
					
					//Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
					{
	
						String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
					}
					
					//Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
					{
						String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The error code displayed in the response is :" + code);
					}
					
					
					//Checking For Fault Schema
					if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
					{
						String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
					}
					
					//Checking For Result Flag
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
					{
						if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) 
						{
							if(WSAssert.assertIfElementExists(travelAgentLookupRes, "ProfileDetails_ProfileIDs_UniqueID", false))
							{
								
								WSClient.setData("{var_profileID}", operaProfileID);
								
								String QS_01 = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);
								
								// Validating the fetched travel agent profile values from RES with the
								// Database
//								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Comparing Data populated in DB and Data in Response"+"</b>");
								
								String actualCompanyName = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyName", XMLType.RESPONSE);
								if(WSAssert.assertEquals(actualCompanyName, expectedRecord.get("COMPANY"), true))
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
								}
								else
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
								}
	
								String actualNameType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyType", XMLType.RESPONSE);
								if(WSAssert.assertEquals(actualNameType,expectedRecord.get("NAME TYPE"), true))
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
								}
								else
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
							}
						}
						
						//Checking if ResultstatusFlag is FAIL
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Travel Agent Lookup Failed!");
						}
						
						//Checking for GDSError
						if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
						{
							String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
	                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
						}
						
						
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
					}
						
		        }
		        else
		        {
		        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed------ Create Profile -----Blocked");
		        }
				
			}
	        catch (Exception ex) 
			{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
			}
			finally
			{
				
			}
			
		}
	
		//Travel Agent Lookup MR-2
		@Test(groups = { "minimumRegression", "Name", "TravelAgentLookup", "OWS" })
		/* Method to fetch Travel Agent Profile when NO name_id is passed*/
		public void travelAgentLookup_38549()
		{
			try
			{
				String testName = "travelAgentLookup_38549";
				WSClient.startTest(testName, "Verify that the travel agent profile is not looked up when no NAME ID is passed", "minimumRegression");
				
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				//String channelType = OWSLib.getChannelType(channel);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
				String companyName = WSClient.getKeywordData("{KEYWORD_FNAME}")+" Travels Limited";
    			WSClient.setData("{var_companyName}", companyName);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
				String operaProfileID = CreateProfile.createProfile("DS_17");
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
				
		        if(!operaProfileID.equals("")) 
		        {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					
					setOWSHeader();
		            //OWSLib.setOWSHeader(uname, pwd, owsresort, channelType, channelCarrier);
		            
//		            WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching Travel Agent Profile"+"</b>");
					String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_03");
					String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
					
					
					//Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
					{
	
						String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
					
					//Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
					{
						String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
					}
					
					
					//Checking For Fault Schema
					if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
					{
						String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
					}
					
					//Checking For Result Flag
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
					{
						if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "FAIL", false)) 
						{
							WSClient.writeToReport(LogStatus.PASS, "Travel Agent Lookup Failed!");
						}
						
						//Checking if ResultstatusFlag is SUCCESS
						else
						{
							if(WSAssert.assertIfElementExists(travelAgentLookupRes, "ProfileDetails_ProfileIDs_UniqueID", false))
							{
								
								WSClient.setData("{var_profileID}", operaProfileID);
								
								String QS_01 = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);
								
								// Validating the fetched travel agent profile values from RES with the
								// Database
//								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Comparing Data populated in DB and Data in Response"+"</b>");
								
								String actualCompanyName = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyName", XMLType.RESPONSE);
								if(WSAssert.assertEquals(actualCompanyName, expectedRecord.get("COMPANY"), true))
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
								}
								else
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
								}
	
								String actualNameType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyType", XMLType.RESPONSE);
								if(WSAssert.assertEquals(actualNameType,expectedRecord.get("NAME TYPE"), true))
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
								}
								else
								{
									WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
							}
						}
						
						//Checking for GDSError
						if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
						{
							String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
	                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
						}
						
						
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
					}
						
		        }
		        else
		        {
		        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed------ Create Profile -----Blocked");
		        }
				
			}
	        catch (Exception ex) 
			{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
			}
			finally
			{
				
			}
			
		}
	
		//Travel Agent Lookup MR-3
		@Test(groups = { "minimumRegression", "Name", "TravelAgentLookup", "OWS" })
		/* Method to fetch Travel Agent Profile when a RANDOM name_id is passed*/
		public void travelAgentLookup_38551()
		{
			try
			{
				String testName = "travelAgentLookup_38551";
				WSClient.startTest(testName, "Verify that the travel agent profile is not looked up when an invalid NAME ID is passed", "minimumRegression");
				
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				//String channelType = OWSLib.getChannelType(channel);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
				String companyName = WSClient.getKeywordData("{KEYWORD_FNAME}")+" Travels Limited";
    			WSClient.setData("{var_companyName}", companyName);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
				String operaProfileID = CreateProfile.createProfile("DS_17");
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
				
		        if(!operaProfileID.equals("")) 
		        {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileID}", operaProfileID);
					
					setOWSHeader();
		            //OWSLib.setOWSHeader(uname, pwd, owsresort, channelType, channelCarrier);
		            
//		            WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching Travel Agent Profile"+"</b>");
					String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_04");
					String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
					
					
					//Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
					{
	
						String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
					
					//Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
					{
						String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
					}
					
					
					//Checking For Fault Schema
					if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
					{
						String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
					}
					
					//Checking For Result Flag
					if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
					{
						if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "FAIL", false)) 
						{
							WSClient.writeToReport(LogStatus.PASS, "Travel Agent Lookup Failed as expected!");
						}
						
						//Checking if ResultstatusFlag is SUCCESS
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Travel Agent Lookup passed instead of failing!");
						}
						
						//Checking for GDSError
						if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
						{
							String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
	                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
						}
						
						
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
					}
						
		        }
		        else
		        {
		        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed------ Create Profile -----Blocked");
		        }
				
			}
	        catch (Exception ex) 
			{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
			}
			finally
			{
				
			}
			
		}
		
//		//Travel Agent Lookup MR-4
//		@Test(groups = { "minimumRegression", "Name", "TravelAgentLookup", "OWS" })
//		/* Method to fetch Travel Agent Profile with address details*/
//		public void travelAgentLookup_44444()
//		{
//			try
//			{
//				String testName = "travelAgentLookup_44444";
//				WSClient.startTest(testName, "Verify that multiple address details of a Travel Agent Profile is fetched correctly", "minimumRegression");
//				String prerequisite[] = {"AddressType"};
//				if(OperaPropConfig.getPropertyConfigResults(prerequisite))
//				{
//					
//					//Means Required prerequisite are fulfilled
//					//Getting Required values for the required profile creation
//					String resortOperaValue = OPERALib.getResort();
//					String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
//					String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
//					String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
//					String addressType1 = OperaPropConfig.getDataSetForCode("AddressType", "DS_02");
//					HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();
//					HashMap<String, String> fullAddress1 = OPERALib.fetchAddressLOV();
//					
//					//Setting Variables
//					WSClient.setData("{var_resort}", resortOperaValue);
//					WSClient.setData("{var_fname}", fname);
//					WSClient.setData("{var_lname}", lname);
//					WSClient.setData("{var_addressType}", addressType);
//					WSClient.setData("{var_addressType1}", addressType1);
//					WSClient.setData("{var_city}", fullAddress.get("City"));
//					WSClient.setData("{var_zip}", fullAddress.get("Zip"));
//					WSClient.setData("{var_state}", fullAddress.get("State"));
//					WSClient.setData("{var_country}", fullAddress.get("Country"));
//					WSClient.setData("{var_city1}", fullAddress1.get("City"));
//					WSClient.setData("{var_zip1}", fullAddress1.get("Zip"));
//					WSClient.setData("{var_state1}", fullAddress1.get("State"));
//					WSClient.setData("{var_country1}", fullAddress1.get("Country"));
//					
//					
//					String chain=OPERALib.getChain();
//					String resort = OPERALib.getResort();
//					String channel = OWSLib.getChannel();
//					String owsresort = OWSLib.getChannelResort(resort, channel);
//					String uname = OPERALib.getUserName();
//					//String pwd = OPERALib.getPassword();
//					//String channelType = OWSLib.getChannelType(channel);
//					//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
//					String companyName = WSClient.getKeywordData("{KEYWORD_FNAME}")+" Travels Limited";
//	    			WSClient.setData("{var_companyName}", companyName);
//					WSClient.setData("{var_resort}", resort);
//					WSClient.setData("{var_owsresort}", owsresort);
//					WSClient.setData("{var_chain}", chain);
//					OPERALib.setOperaHeader(uname);
//					
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
//					String operaProfileID = CreateProfile.createProfile("DS_39");
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
//					
//			        if(!operaProfileID.equals("error")) 
//			        {
//						System.out.println(operaProfileID);
//						WSClient.setData("{var_profileID}", operaProfileID);
//						
//						String QS_03 = WSClient.getQuery("QS_03");
//						String addressid1=WSClient.getDBRow(QS_03).get("ADDRESS_ID");
//						if(!addressid1.equals(""))
//						{
//							WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully created Profile with one Address"+"</b>");
//							//Prerequisite 2: Change Profile 
//				        	WSClient.setData("{var_profileID}", operaProfileID);
//							String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_14");
//							String changeProfileRes = WSClient.processSOAPMessage(changeProfileReq);	
//							
//							if(WSAssert.assertIfElementExists(changeProfileRes,	"ChangeProfileRS_Success", false))
//							{
//								String QS_02 = WSClient.getQuery("QS_02");
//								String addressidCount=WSClient.getDBRow(QS_02).get("ADDRESS_ID");
//								if(addressidCount.equals("2"))
//								{
//									WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully added 2nd Address to the Profile"+"</b>");
//						
//									setOWSHeader();
//						            //OWSLib.setOWSHeader(uname, pwd, owsresort, channelType, channelCarrier);
//						            
//				//		            WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching Travel Agent Profile"+"</b>");
//									String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_01");
//									String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
//									
//									
//									//Checking for Text Element in Result
//									if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
//									{
//					
//										String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
//										WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//									}
//									
//									//Checking For OperaErrorCode
//									if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
//									{
//										String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//										WSClient.writeToReport(LogStatus.INFO, "The error code displayed in the response is :" + code);
//									}
//									
//									
//									//Checking For Fault Schema
//									if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
//									{
//										String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
//										WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
//									}
//									
//									//Checking For Result Flag
//									if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
//									{
//										if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) 
//										{
//											if(WSAssert.assertIfElementExists(travelAgentLookupRes, "ProfileDetails_ProfileIDs_UniqueID", false))
//											{
//												
//												WSClient.setData("{var_profileID}", operaProfileID);
//												
//												String QS_01 = WSClient.getQuery("QS_01");
//												LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);
//												
//												// Validating the fetched travel agent profile values from RES with the
//												// Database
//				//								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Comparing Data populated in DB and Data in Response"+"</b>");
//												
//												String actualCompanyName = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyName", XMLType.RESPONSE);
//												if(WSAssert.assertEquals(actualCompanyName, expectedRecord.get("COMPANY"), true))
//												{
//													WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//												}
//												else
//												{
//													WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//												}
//					
//												String actualNameType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyType", XMLType.RESPONSE);
//												if(WSAssert.assertEquals(actualNameType,expectedRecord.get("NAME TYPE"), true))
//												{
//													WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//												}
//												else
//												{
//													WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//												}
//												
//												WSClient.writeToReport(LogStatus.INFO, "<b>"+"Validating Address details of the two Addresses"+"</b>");
//												//Comparing Address Details
//												//Fetching Record from DB
//												String QS06 = WSClient.getQuery("QS_06");
//												List<LinkedHashMap<String, String>> expectedRecords = WSClient.getDBRows(QS06);
//												List<LinkedHashMap<String, String>> actualRecords = new ArrayList<LinkedHashMap<String, String>>();
//												//Fetching Record from Response
//												HashMap<String,String> xpath=new HashMap<String,String>();
//												xpath.put("Addresses_NameAddress_AddressLine","ProfileDetails_Addresses_NameAddress");
//												xpath.put("Addresses_NameAddress_cityName","ProfileDetails_Addresses_NameAddress");
//												xpath.put("Addresses_NameAddress_stateProv","ProfileDetails_Addresses_NameAddress");
//												xpath.put("Addresses_NameAddress_countryCode","ProfileDetails_Addresses_NameAddress");
//												xpath.put("Addresses_NameAddress_postalCode","ProfileDetails_Addresses_NameAddress");
//												xpath.put("ProfileDetails_Addresses_NameAddress_addressType","ProfileDetails_Addresses_NameAddress");
//												xpath.put("ProfileDetails_Addresses_NameAddress_primary","ProfileDetails_Addresses_NameAddress");
//												actualRecords=WSClient.getMultipleNodeList(travelAgentLookupRes, xpath, false, XMLType.RESPONSE);
//												// Validating the fetched profile values from RES with the
////												// Database
//												WSAssert.assertEquals(actualRecords, expectedRecords, false);
//												
//												
////												WSClient.writeToReport(LogStatus.INFO, "<b>"+"Validating Address Lines of the two Addresses"+"</b>");
////												//Comparing Address Lines
////												String QS05 = WSClient.getQuery("QS_05");
////												List<LinkedHashMap<String, String>> expAddLine = WSClient.getDBRows(QS05);
////												List<LinkedHashMap<String, String>> actAddLine = new ArrayList<LinkedHashMap<String, String>>();
////												//Fetching Address Line from Response
////												HashMap<String,String> path=new HashMap<String,String>();
////												path.put("Addresses_NameAddress_AddressLine","ProfileDetails_Addresses_NameAddress");
////												actAddLine=WSClient.getMultipleNodeList(travelAgentLookupRes, path, false, XMLType.RESPONSE);
////												// Validating the Address Line values from RES with the
//////												// Database
////												WSAssert.assertEquals(actAddLine, expAddLine, false);
////												
////												
////												WSClient.writeToReport(LogStatus.INFO, "<b>"+"Validating Other Address details of the two Addresses"+"</b>");
////												//Comparing Rest of Address Details
////												//Fetching Record from DB
////												String QS02 = WSClient.getQuery("QS_02");
////												List<LinkedHashMap<String, String>> expectedRecords = WSClient.getDBRows(QS02);
////												List<LinkedHashMap<String, String>> actualRecords = new ArrayList<LinkedHashMap<String, String>>();
////												//Fetching Record from Response
////												HashMap<String,String> xpath=new HashMap<String,String>();
////												xpath.put("Addresses_NameAddress_cityName","ProfileDetails_Addresses_NameAddress");
////												xpath.put("Addresses_NameAddress_stateProv","ProfileDetails_Addresses_NameAddress");
////												xpath.put("Addresses_NameAddress_countryCode","ProfileDetails_Addresses_NameAddress");
////												xpath.put("Addresses_NameAddress_postalCode","ProfileDetails_Addresses_NameAddress");
////												xpath.put("ProfileDetails_Addresses_NameAddress_addressType","ProfileDetails_Addresses_NameAddress");
////												xpath.put("ProfileDetails_Addresses_NameAddress_primary","ProfileDetails_Addresses_NameAddress");
////												actualRecords=WSClient.getMultipleNodeList(travelAgentLookupRes, xpath, false, XMLType.RESPONSE);
//////												// Validating the fetched profile values from RES with the
//////												// Database
////												WSAssert.assertEquals(actualRecords, expectedRecords, false);
//												
//											}
//											else
//											{
//												WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
//											}
//										}
//										
//										//Checking if ResultstatusFlag is FAIL
//										else
//										{
//											WSClient.writeToReport(LogStatus.FAIL, "Travel Agent Lookup Failed!");
//										}
//										
//										//Checking for GDSError
//										if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
//										{
//											String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
//					                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
//										}
//
//									}
//									else
//									{
//										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
//									}
//								}
//								else
//								{
//									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Address failed!------ Create Profile -----Blocked");
//								}
//							}
//							else
//							{
//								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites  failed!------ Change Profile -----Blocked");
//							}
//						}
//						else
//						{
//							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Address failed!------ Create Profile -----Blocked");
//						}
//			        }
//			        else
//			        {
//			        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed------ Create Profile -----Blocked");
//			        }
//				}
//				else
//				{
//					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Address Type not available -----Blocked");
//				}
//			}
//	        catch (Exception ex) 
//			{
//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
//			}
//			finally
//			{
//				
//			}
//		}
//		
//		//Travel Agent Lookup MR-5
//		@Test(groups = { "minimumRegression", "Name", "TravelAgentLookup", "OWS" })
//		/* Method to fetch Travel Agent Profile with address details*/
//		public void travelAgentLookup_44445()
//		{
//			try
//			{
//				String testName = "travelAgentLookup_44445";
//				WSClient.startTest(testName, "Verify that phone details of a Travel Agent Profile is fetched correctly", "minimumRegression");
//				String prerequisite[] = {"CommunicationType"};
//				if(OperaPropConfig.getPropertyConfigResults(prerequisite))
//				{
//					
//					//Means Required prerequisite are fulfilled
//					//Getting Required values for the required profile creation
//					String resortOperaValue = OPERALib.getResort();
//					String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
//					String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
//					String phoneType = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
//					String phoneType2 = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03");
//					
//					//Setting Variables
//					WSClient.setData("{var_resort}", resortOperaValue);
//					WSClient.setData("{var_fname}", fname);
//					WSClient.setData("{var_lname}", lname);
//					WSClient.setData("{var_phoneType}", phoneType);
//					WSClient.setData("{var_phoneType2}", phoneType2);
//					
//					
//					String chain=OPERALib.getChain();
//					String resort = OPERALib.getResort();
//					String channel = OWSLib.getChannel();
//					String owsresort = OWSLib.getChannelResort(resort, channel);
//					String uname = OPERALib.getUserName();
//					//String pwd = OPERALib.getPassword();
//					//String channelType = OWSLib.getChannelType(channel);
//					//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
//					String companyName = WSClient.getKeywordData("{KEYWORD_FNAME}")+" Travels Limited";
//	    			WSClient.setData("{var_companyName}", companyName);
//					WSClient.setData("{var_resort}", resort);
//					WSClient.setData("{var_owsresort}", owsresort);
//					WSClient.setData("{var_chain}", chain);
//					OPERALib.setOperaHeader(uname);
//					
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
//					String operaProfileID = CreateProfile.createProfile("DS_40");
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
//					
//					if(!operaProfileID.equals("error")) 
//			        {
//						System.out.println(operaProfileID);
//						WSClient.setData("{var_profileID}", operaProfileID);
//						
//						String QS_04 = WSClient.getQuery("QS_04");
//						List<LinkedHashMap<String,String>> phn=WSClient.getDBRows(QS_04);
//						if(phn.size()>=2)
//						{
//							setOWSHeader();
//				            
//							String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_01");
//							String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
//							
//							//Checking for Text Element in Result
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
//							{
//			
//								String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//							}
//							
//							//Checking For OperaErrorCode
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
//							{
//								String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.INFO, "The error code displayed in the response is :" + code);
//							}
//							
//							
//							//Checking For Fault Schema
//							if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
//							{
//								String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
//							}
//							
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
//							{
//								if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) 
//								{
//									if(WSAssert.assertIfElementExists(travelAgentLookupRes, "ProfileDetails_ProfileIDs_UniqueID", false))
//									{
//										
//										WSClient.setData("{var_profileID}", operaProfileID);
//										
//										String QS_01 = WSClient.getQuery("QS_01");
//										LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);
//										
//										// Validating the fetched travel agent profile values from RES with the
//										// Database
//		//								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Comparing Data populated in DB and Data in Response"+"</b>");
//										
//										String actualCompanyName = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyName", XMLType.RESPONSE);
//										if(WSAssert.assertEquals(actualCompanyName, expectedRecord.get("COMPANY"), true))
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//										}
//										else
//										{
//											WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//										}
//			
//										String actualNameType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyType", XMLType.RESPONSE);
//										if(WSAssert.assertEquals(actualNameType,expectedRecord.get("NAME TYPE"), true))
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//										}
//										else
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//										}
//										
//										//Fetching Record from DB
//										String QS_03 = WSClient.getQuery("QS_03");
//										List<LinkedHashMap<String, String>> expectedRecords = WSClient.getDBRows(QS_03);
//	
//										//Fetching Record from Response
//										HashMap<String, String> xPath = new HashMap<String, String>();
//										xPath.put("Phones_NamePhone_PhoneNumber","ProfileDetails_Phones_NamePhone");
//										xPath.put("ProfileDetails_Phones_NamePhone_phoneType","ProfileDetails_Phones_NamePhone");
//										xPath.put("ProfileDetails_Phones_NamePhone_primary","ProfileDetails_Phones_NamePhone");
//										List<LinkedHashMap<String, String>> actualRecords = WSClient.getMultipleNodeList(travelAgentLookupRes, xPath, false, XMLType.RESPONSE);
//										actualRecords = WSClient.getMultipleNodeList(travelAgentLookupRes, xPath, false, XMLType.RESPONSE);
//										
//										// Validating the fetched profile values from RES with the
//										// Database
//										
//										WSAssert.assertEquals(actualRecords, expectedRecords, false);
//										
//									}
//									else
//									{
//										WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
//									}
//								}
//								//Checking if ResultstatusFlag is FAIL
//								else
//								{
//									WSClient.writeToReport(LogStatus.FAIL, "Travel Agent Lookup Failed!");
//								}
//								
//								//Checking for GDSError
//								if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
//								{
//									String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
//			                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
//								}
//							}
//							else
//							{
//								WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
//							}
//						}
//						else
//						{
//							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for attaching to phone numbers to a profile failed! -----Blocked");
//						}
//			        }
//			        else 
//			        {
//						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
//					}
//				}
//				else
//				{
//					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Communication Type not available -----Blocked");
//				}
//				
//			}
//	        catch (Exception ex) 
//			{
//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
//			}
//			finally
//			{
//				
//			}
//		}
//		
//		//Travel Agent Lookup MR-6
//		@Test(groups = { "minimumRegression", "Name", "TravelAgentLookup", "OWS" })
//		/* Method to fetch Travel Agent Profile with address details*/
//		public void travelAgentLookup_44446()
//		{
//			try
//			{
//				String testName = "travelAgentLookup_44446";
//				WSClient.startTest(testName, "Verify that email details of a Travel Agent Profile is fetched correctly", "minimumRegression");
//				String prerequisite[] = {"CommunicationType", "CommunicationMethod"};
//				if(OperaPropConfig.getPropertyConfigResults(prerequisite))
//				{
//					
//					//Means Required prerequisite are fulfilled
//					//Getting Required values for the required profile creation
//					String resortOperaValue = OPERALib.getResort();
//					String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
//					String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
//					String email = fname+"."+lname+"@ZMAIL.COM";
//					String phoneType = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02");		// Email Type
//					String phoneRole = OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02");	// Email Role
//					
//					//Setting Variables
//					WSClient.setData("{var_resort}", resortOperaValue);
//					WSClient.setData("{var_fname}", fname);
//					WSClient.setData("{var_lname}", lname);
//					WSClient.setData("{var_primary}", "true");
//					WSClient.setData("{var_email}", email);
//					WSClient.setData("{var_emailType}", phoneType);			//Email Type
//					WSClient.setData("{var_phoneRole}", phoneRole);			//Email Role
//					
//					
//					String chain=OPERALib.getChain();
//					String resort = OPERALib.getResort();
//					String channel = OWSLib.getChannel();
//					String owsresort = OWSLib.getChannelResort(resort, channel);
//					String uname = OPERALib.getUserName();
//					//String pwd = OPERALib.getPassword();
//					//String channelType = OWSLib.getChannelType(channel);
//					//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
//					String companyName = fname +" Travels Limited";
//	    			WSClient.setData("{var_companyName}", companyName);
//					WSClient.setData("{var_resort}", resort);
//					WSClient.setData("{var_owsresort}", owsresort);
//					WSClient.setData("{var_chain}", chain);
//					OPERALib.setOperaHeader(uname);
//					
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
//					String operaProfileID = CreateProfile.createProfile("DS_41");
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
//					
//					if(!operaProfileID.equals("error")) 
//			        {
//						System.out.println(operaProfileID);
//						WSClient.setData("{var_profileID}", operaProfileID);
//						
//						String QS_04 = WSClient.getQuery("QS_04");
//						List<LinkedHashMap<String,String>> eml=WSClient.getDBRows(QS_04);
//						if(eml.size()>=1)
//						{
//							setOWSHeader();
//				            
//							String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_01");
//							String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
//							
//							//Checking for Text Element in Result
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
//							{
//			
//								String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//							}
//							
//							//Checking For OperaErrorCode
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
//							{
//								String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.INFO, "The error code displayed in the response is :" + code);
//							}
//							
//							
//							//Checking For Fault Schema
//							if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
//							{
//								String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
//							}
//							
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
//							{
//								if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) 
//								{
//									if(WSAssert.assertIfElementExists(travelAgentLookupRes, "ProfileDetails_ProfileIDs_UniqueID", false))
//									{
//										
//										WSClient.setData("{var_profileID}", operaProfileID);
//										
//										String QS_01 = WSClient.getQuery("QS_01");
//										LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);
//										
//										// Validating the fetched travel agent profile values from RES with the
//										// Database
//		//								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Comparing Data populated in DB and Data in Response"+"</b>");
//										
//										String actualCompanyName = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyName", XMLType.RESPONSE);
//										if(WSAssert.assertEquals(actualCompanyName, expectedRecord.get("COMPANY"), true))
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//										}
//										else
//										{
//											WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//										}
//			
//										String actualNameType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyType", XMLType.RESPONSE);
//										if(WSAssert.assertEquals(actualNameType,expectedRecord.get("NAME TYPE"), true))
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//										}
//										else
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//										}
//										
//										//Fetching Record from DB
//										String QS04 = WSClient.getQuery("QS_04");
//										LinkedHashMap<String, String> expRecord = WSClient.getDBRow(QS04);
//	
//										//Fetching Record from Response
//										LinkedHashMap<String, String> actRecord = new LinkedHashMap<String, String>();
//										String NameEmail = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_EMails_NameEmail", XMLType.RESPONSE);
//										String operaId = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_EMails_NameEmail_operaId", XMLType.RESPONSE);
//										String emailType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_EMails_NameEmail_emailType", XMLType.RESPONSE);
//										String primary = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_EMails_NameEmail_primary", XMLType.RESPONSE);
//										actRecord.put("NameEmail", NameEmail);
//										actRecord.put("operaId", operaId);
//										actRecord.put("emailType", emailType);
//										actRecord.put("primary", primary);
//										
//										// Validating the fetched profile values from RES with the
//										// Database
//										WSAssert.assertEquals(expRecord, actRecord, false);
//									}
//									else
//									{
//										WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
//									}
//								}
//								//Checking if ResultstatusFlag is FAIL
//								else
//								{
//									WSClient.writeToReport(LogStatus.FAIL, "Travel Agent Lookup Failed!");
//								}
//								
//								//Checking for GDSError
//								if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
//								{
//									String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
//			                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
//								}
//							}
//							else
//							{
//								WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
//							}
//						}
//						else
//						{
//							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for attaching to phone numbers to a profile failed! -----Blocked");
//						}
//			        }
//			        else 
//			        {
//						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
//					}
//				}
//				else
//				{
//					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Communication Type not available -----Blocked");
//				}
//				
//			}
//	        catch (Exception ex) 
//			{
//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
//			}
//			finally
//			{
//				
//			}
//		}
//		
//		//Travel Agent Lookup MR-7
//		@Test(groups = { "minimumRegression", "Name", "TravelAgentLookup", "OWS" })
//		/* Method to fetch Travel Agent Profile with address details*/
//		public void travelAgentLookup_44447()
//		{
//			try
//			{
//				String testName = "travelAgentLookup_44447";
//				WSClient.startTest(testName, "Verify that address details of a Travel Agent Profile is fetched correctly", "minimumRegression");
//				String prerequisite[] = {"AddressType"};
//				if(OperaPropConfig.getPropertyConfigResults(prerequisite))
//				{
//					
//					//Means Required prerequisite are fulfilled
//					//Getting Required values for the required profile creation
//					String resortOperaValue = OPERALib.getResort();
//					String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
//					String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
//					String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
//					HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();
//					
//					//Setting Variables
//					WSClient.setData("{var_resort}", resortOperaValue);
//					WSClient.setData("{var_fname}", fname);
//					WSClient.setData("{var_lname}", lname);
//					WSClient.setData("{var_addressType}", addressType);
//					WSClient.setData("{var_city}", fullAddress.get("City"));
//					WSClient.setData("{var_zip}", fullAddress.get("Zip"));
//					WSClient.setData("{var_state}", fullAddress.get("State"));
//					WSClient.setData("{var_country}", fullAddress.get("Country"));
//					
//					String chain=OPERALib.getChain();
//					String resort = OPERALib.getResort();
//					String channel = OWSLib.getChannel();
//					String owsresort = OWSLib.getChannelResort(resort, channel);
//					String uname = OPERALib.getUserName();
//					//String pwd = OPERALib.getPassword();
//					//String channelType = OWSLib.getChannelType(channel);
//					//String channelCarrier = OWSLib.getChannelCarier(resort, channelType);
//					String companyName = WSClient.getKeywordData("{KEYWORD_FNAME}")+" Travels Limited";
//	    			WSClient.setData("{var_companyName}", companyName);
//					WSClient.setData("{var_resort}", resort);
//					WSClient.setData("{var_owsresort}", owsresort);
//					WSClient.setData("{var_chain}", chain);
//					OPERALib.setOperaHeader(uname);
//					
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Travel Agent Profile"+"</b>");
//					String operaProfileID = CreateProfile.createProfile("DS_39");
//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID:- " + operaProfileID+"</b>");
//					
//			        if(!operaProfileID.equals("error")) 
//			        {
//						System.out.println(operaProfileID);
//						WSClient.setData("{var_profileID}", operaProfileID);
//						
//						String QS_03 = WSClient.getQuery("QS_03");
//						String addressid1=WSClient.getDBRow(QS_03).get("ADDRESS_ID");
//						if(!addressid1.equals(""))
//						{
//							WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully created Profile with Address"+"</b>");
//							
//							setOWSHeader();
//		//		            WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching Travel Agent Profile"+"</b>");
//							String travelAgentLookupReq = WSClient.createSOAPMessage("OWSTravelAgentLookup", "DS_01");
//							String travelAgentLookupRes = WSClient.processSOAPMessage(travelAgentLookupReq);
//							
//							
//							//Checking for Text Element in Result
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes, "Result_Text_TextElement", true)) 
//							{
//			
//								String message = WSAssert.getElementValue(travelAgentLookupRes, "Result_Text_TextElement", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//							}
//							
//							//Checking For OperaErrorCode
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes,"TravelAgentLookupResponse_Result_OperaErrorCode", true)) 
//							{
//								String code = WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.INFO, "The error code displayed in the response is :" + code);
//							}
//							
//							
//							//Checking For Fault Schema
//							if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_faultcode", true))
//							{
//								String message=WSClient.getElementValue(travelAgentLookupRes,"TravelAgentLookupResponse_faultstring", XMLType.RESPONSE);
//								WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Fault Schema in Response with message: "+"</b>"+message);
//							}
//							
//							//Checking For Result Flag
//							if (WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", true)) 
//							{
//								if(WSAssert.assertIfElementValueEquals(travelAgentLookupRes, "TravelAgentLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) 
//								{
//									if(WSAssert.assertIfElementExists(travelAgentLookupRes, "ProfileDetails_ProfileIDs_UniqueID", false))
//									{
//										
//										WSClient.setData("{var_profileID}", operaProfileID);
//										
//										String QS_01 = WSClient.getQuery("QS_01");
//										LinkedHashMap<String, String> expectedRecord = WSClient.getDBRow(QS_01);
//										
//										// Validating the fetched travel agent profile values from RES with the
//										// Database
//		//								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Comparing Data populated in DB and Data in Response"+"</b>");
//										
//										String actualCompanyName = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyName", XMLType.RESPONSE);
//										if(WSAssert.assertEquals(actualCompanyName, expectedRecord.get("COMPANY"), true))
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//										}
//										else
//										{
//											WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Values for Company Name Expected: "+ expectedRecord.get("COMPANY") + " , Actual: " + actualCompanyName+"</b>");
//										}
//			
//										String actualNameType = WSClient.getElementValue(travelAgentLookupRes, "ProfileDetails_Company_CompanyType", XMLType.RESPONSE);
//										if(WSAssert.assertEquals(actualNameType,expectedRecord.get("NAME TYPE"), true))
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//										}
//										else
//										{
//											WSClient.writeToReport(LogStatus.PASS, "<b>"+"Values for Name Type Expected: "+ expectedRecord.get("NAME TYPE") + " , Actual: " + actualNameType+"</b>");
//										}
//										
//										
//										WSClient.writeToReport(LogStatus.INFO, "<b>"+"Validating Address Details"+"</b>");
//										//Comparing Address Details
//										String QS06 = WSClient.getQuery("QS_06");
//										LinkedHashMap<String, String> expAdd = WSClient.getDBRow(QS06);
//										LinkedHashMap<String, String> actAdd = new LinkedHashMap<String, String>();
//										
//										//Fetching Address Details from Response
//										HashMap<String,String> xpath=new HashMap<String,String>();
//										xpath.put("Addresses_NameAddress_AddressLine","ProfileDetails_Addresses_NameAddress");
//										xpath.put("Addresses_NameAddress_cityName","ProfileDetails_Addresses_NameAddress");
//										xpath.put("Addresses_NameAddress_stateProv","ProfileDetails_Addresses_NameAddress");
//										xpath.put("Addresses_NameAddress_countryCode","ProfileDetails_Addresses_NameAddress");
//										xpath.put("Addresses_NameAddress_postalCode","ProfileDetails_Addresses_NameAddress");
//										xpath.put("ProfileDetails_Addresses_NameAddress_addressType","ProfileDetails_Addresses_NameAddress");
//										xpath.put("ProfileDetails_Addresses_NameAddress_primary","ProfileDetails_Addresses_NameAddress");
//										actAdd=WSClient.getMultipleNodeList(travelAgentLookupRes, xpath, false, XMLType.RESPONSE).get(0);
//										
//										// Validating the Address Line values from RES with the
////										// Database
//										WSAssert.assertEquals(expAdd, actAdd, false);
//										
//									}
//									else
//									{
//										WSClient.writeToReport(LogStatus.WARNING, "Unique ID not available in response");
//									}
//								}
//								
//								//Checking if ResultstatusFlag is FAIL
//								else
//								{
//									WSClient.writeToReport(LogStatus.FAIL, "Travel Agent Lookup Failed!");
//								}
//								
//								//Checking for GDSError
//								if(WSAssert.assertIfElementExists(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError",true)) 
//								{
//									String message=WSAssert.getElementValue(travelAgentLookupRes, "TravelAgentLookupResponse_Result_GDSError", XMLType.RESPONSE);
//			                        WSClient.writeToReport(LogStatus.FAIL, "The error displayed in the Travel Agent response is :"+ message);
//								}
//
//							}
//							else
//							{
//								WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
//							}
//						}
//						else
//						{
//							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Address failed!------ Create Profile -----Blocked");
//						}
//			        }
//			        else
//			        {
//			        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique ID's failed------ Create Profile -----Blocked");
//			        }
//				}
//				else
//				{
//					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ Address Type not available -----Blocked");
//				}
//			}
//	        catch (Exception ex) 
//			{
//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
//			}
//			finally
//			{
//				
//			}
//		}
//		
}
