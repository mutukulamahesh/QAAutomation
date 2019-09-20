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

public class InsertAddress extends WSSetUp {
	
	
	
	@Test(groups = { "sanity", "InsertAddress", "OWS","Name" ,"in-QA"})

	public void insertAddress_38318() {
		try {
			String testName = "insertAddress_38318";
			WSClient.startTest(testName,
					"Verify that address with minimum information is attached to the profile.", "sanity");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			
				String profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				    WSClient.setData("{var_profileID}", profileID);
				    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
					HashMap<String, String> address = new HashMap<String, String>();
					address=OPERALib.fetchAddressLOV();				
					WSClient.setData("{var_country}", address.get("Country"));
					
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_01");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					
                    
				if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_Result_resultStatusFlag", false)){
					if (WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
							"InsertAddressResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_IDs_IDPair_operaId",
								false)) {
							String addressid = WSClient.getElementValue(insertAddressResponseXML,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_addressID}", addressid);
							String query=WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> db = WSClient.getDBRow(query);
							LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
							WSClient.writeToReport(LogStatus.PASS,"Address Id :"+addressid);
							String country=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_countryCode", XMLType.REQUEST);
							
							expected.put("COUNTRY",country);
							
							WSAssert.assertEquals(expected, db, false);
						}

					}
				}

				 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
					 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
						String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
					 }
				}
				 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
					}

					if (WSAssert.assertIfElementExists(insertAddressResponseXML,
							"InsertAddressResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(insertAddressResponseXML,
								"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
					}
					if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
							true)) {
						String message = WSClient.getElementValue(insertAddressResponseXML,
								"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
					}
				
			} 
			
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
	@Test(groups = { "minimumRegression", "InsertAddress", "OWS" ,"Name","in-QA"})
	public void insertAddress_38540() {
		try {
			String testName = "insertAddress_38540";
			WSClient.startTest(testName,
					"Verify that all the information of address is attached to the profile.", "minimumRegression");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			
			String	profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				    WSClient.setData("{var_profileID}", profileID);
				    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
					HashMap<String, String> address = new HashMap<String, String>();
					address=OPERALib.fetchAddressLOV();
					WSClient.setData("{var_city}", address.get("City"));
					WSClient.setData("{var_state}", address.get("State"));
					WSClient.setData("{var_country}", address.get("Country"));
					WSClient.setData("{var_zip}", address.get("Zip"));
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_06");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					
				    if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_Result_resultStatusFlag", false)){
					if (WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
							"InsertAddressResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_IDs_IDPair_operaId",
								false)) {
							String addressid = WSClient.getElementValue(insertAddressResponseXML,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_addressID}", addressid);
							String query=WSClient.getQuery("QS_02");
							LinkedHashMap<String, String> db = WSClient.getDBRow(query);
							LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
							WSClient.writeToReport(LogStatus.PASS,"Address Id :"+addressid);
							String primary=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_primary", XMLType.REQUEST);
							String addresstype=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_addressType", XMLType.REQUEST);
							String language_code=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_languageCode", XMLType.REQUEST);
							String addressLine=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_AddressLine", XMLType.REQUEST);
							String addressLine2=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_AddressLine[2]", XMLType.REQUEST);
							String addressLine3=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_AddressLine[3]", XMLType.REQUEST);
							String addressLine4=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_AddressLine[4]", XMLType.REQUEST);
							String city=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_cityName", XMLType.REQUEST);
							String state=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_stateProv", XMLType.REQUEST);
							String country=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_countryCode", XMLType.REQUEST);
							String zip=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_postalCode", XMLType.REQUEST);
							String primary_yn="";
							if(primary.equalsIgnoreCase("true"))
								primary_yn="Y";
							else if(primary.equalsIgnoreCase("false"))
								primary_yn="N";
							else 
								primary_yn=primary;
							expected.put("PRIMARY_YN",primary_yn);
							expected.put("ADDRESS_TYPE",addresstype);
							expected.put("LANGUAGE_CODE",language_code);
							expected.put("ADDRESSLINE1",addressLine);
							expected.put("ADDRESSLINE2",addressLine2);
							expected.put("ADDRESSLINE3",addressLine3);
							expected.put("ADDRESSLINE4",addressLine4);
							expected.put("CITY",city);
							expected.put("STATE",state);
							expected.put("COUNTRY",country);
							expected.put("ZIP_CODE",zip);
							WSAssert.assertEquals(expected, db, false);
						}
					}

					}
				    

					 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
						 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
							String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
						 }
					}
					 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
							String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(insertAddressResponseXML,
								"InsertAddressResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(insertAddressResponseXML,
									"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
						}
						if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
								true)) {
							String message = WSClient.getElementValue(insertAddressResponseXML,
									"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
						}
                   
				} 
			
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
    

	@Test(groups = { "minimumRegression", "InsertAddress", "OWS","Name","in-QA","Failed"})
	public void insertAddress_38541() {
		try {
			String testName = "insertAddress_38541";
			WSClient.startTest(testName,
					"Verify that error message is coming in the response when duplicate address is given in the request", "minimumRegression");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
			HashMap<String, String> address = new HashMap<String, String>();
			address=OPERALib.fetchAddressLOV();
			WSClient.setData("{var_city}", address.get("City"));
			WSClient.setData("{var_state}", address.get("State"));
			WSClient.setData("{var_country}", address.get("Country"));
			WSClient.setData("{var_zip}", address.get("Zip"));

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			
			 String	profileID=CreateProfile.createProfile("DS_12");
			if(!profileID.equals("error"))
			{
				    WSClient.setData("{var_profileID}", profileID);

				    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					String query=WSClient.getQuery("QS_10");
					
					LinkedHashMap<String, String> add=WSClient.getDBRow(query);
					WSClient.setData("{var_address1}", add.get("ADDRESS1"));
					WSClient.setData("{var_city}", add.get("CITY"));
					WSClient.setData("{var_state}", add.get("STATE"));
					WSClient.setData("{var_country}", add.get("COUNTRY"));
					WSClient.setData("{var_zip}", add.get("ZIP_CODE"));
					
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_02");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					

					if(WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_resultStatusFlag",false))
					WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
							"InsertAddressResponse_Result_resultStatusFlag", "FAIL", false);
					

					 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
						 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
							String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
						 }
					}
					 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
							String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(insertAddressResponseXML,
								"InsertAddressResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(insertAddressResponseXML,
									"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
						}
						if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
								true)) {
							String message = WSClient.getElementValue(insertAddressResponseXML,
									"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
						}

					
					
				} 
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
	@Test(groups = { "minimumRegression", "InsertAddress", "OWS","Name","in-QA"})
	public void insertAddress_38542() {
		try {
			String testName = "insertAddress_38542";
			WSClient.startTest(testName,
					"Verify that error message is coming in the response when name id is not passed in request of InsertAddress", "minimumRegression");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
			HashMap<String, String> address = new HashMap<String, String>();
			address=OPERALib.fetchAddressLOV();
			WSClient.setData("{var_city}", address.get("City"));
			WSClient.setData("{var_state}", address.get("State"));
			WSClient.setData("{var_country}", address.get("Country"));
			WSClient.setData("{var_zip}", address.get("Zip"));
					
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_03");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					

					
						if(WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_resultStatusFlag", false))
					WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
							"InsertAddressResponse_Result_resultStatusFlag", "FAIL", false);

						 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
							 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
								String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
							 }
						}
						 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
								String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(insertAddressResponseXML,
									"InsertAddressResponse_Result_OperaErrorCode", true)) {
								String code = WSClient.getElementValue(insertAddressResponseXML,
										"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
							}
							if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
									true)) {
								String message = WSClient.getElementValue(insertAddressResponseXML,
										"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
							}
					

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
    
	
	@Test(groups = { "minimumRegression", "InsertAddress", "OWS","Name","in-QA"})
	public void insertAddress_38543() {
		try {
			String testName = "insertAddress_38543";
			WSClient.startTest(testName,
					"Verify that error message is coming in the response when invalid name id is given in request of InsertAddress", "minimumRegression");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
			HashMap<String, String> address = new HashMap<String, String>();
			address=OPERALib.fetchAddressLOV();
			WSClient.setData("{var_city}", address.get("City"));
			WSClient.setData("{var_state}", address.get("State"));
			WSClient.setData("{var_country}", address.get("Country"));
			WSClient.setData("{var_zip}", address.get("Zip"));
					
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_04");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					

					
					
						if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_Result_resultStatusFlag", false))
					WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
							"InsertAddressResponse_Result_resultStatusFlag", "FAIL", false);
						

						 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
							 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
								String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
							 }
						}
						 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
								String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(insertAddressResponseXML,
									"InsertAddressResponse_Result_OperaErrorCode", true)) {
								String code = WSClient.getElementValue(insertAddressResponseXML,
										"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
							}
							if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
									true)) {
								String message = WSClient.getElementValue(insertAddressResponseXML,
										"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
							}
					

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
	@Test(groups = { "minimumRegression", "InsertAddress", "OWS","Name","in-QA"})
	public void insertAddress_38544() {
		try {
			String testName = "insertAddress_38544";
			WSClient.startTest(testName,
					"Verify that error is coming in the response when INTERNAL is missing in the request", "minimumRegression");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			
			String	profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				    WSClient.setData("{var_profileID}", profileID);
				    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
					HashMap<String, String> address = new HashMap<String, String>();
					address=OPERALib.fetchAddressLOV();
					WSClient.setData("{var_city}", address.get("City"));
					WSClient.setData("{var_state}", address.get("State"));
					WSClient.setData("{var_country}", address.get("Country"));
					WSClient.setData("{var_zip}", address.get("Zip"));
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_05");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					
					
						
						if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_Result_resultStatusFlag", false))
						WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
								"InsertAddressResponse_Result_resultStatusFlag", "FAIL", false);
						

						 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
							 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
								String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
							 }
						}
						 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
								String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(insertAddressResponseXML,
									"InsertAddressResponse_Result_OperaErrorCode", true)) {
								String code = WSClient.getElementValue(insertAddressResponseXML,
										"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
							}
							if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
									true)) {
								String message = WSClient.getElementValue(insertAddressResponseXML,
										"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
							}
						
				} 
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
	@Test(groups = { "minimumRegression", "InsertAddress", "OWS","Name","in-QA","Failed"})
	public void insertAddress_41462() {
		try {
			String testName = "insertAddress_41462";
			WSClient.startTest(testName,
					"Verify that non primary address is attached successfully to the profile by InsertAddress.", "minimumRegression");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
			HashMap<String, String> address = new HashMap<String, String>();
			address=OPERALib.fetchAddressLOV();
			WSClient.setData("{var_city}", address.get("City"));
			WSClient.setData("{var_state}", address.get("State"));
			WSClient.setData("{var_country}", address.get("Country"));
			WSClient.setData("{var_zip}", address.get("Zip"));

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			
			 String	profileID=CreateProfile.createProfile("DS_12");
			if(!profileID.equals("error"))
			{
				    WSClient.setData("{var_profileID}", profileID);
				    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
				    HashMap<String, String> address1 = new HashMap<String, String>();
					address1=OPERALib.fetchAddressLOV();
					
					WSClient.setData("{var_city}", address1.get("City"));
					WSClient.setData("{var_state}", address1.get("State"));
					WSClient.setData("{var_country}", address1.get("Country"));
					WSClient.setData("{var_zip}", address1.get("Zip"));
					
					
					
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_07");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					

					
						  if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_Result_resultStatusFlag", false)){
								if (WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
										"InsertAddressResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_IDs_IDPair_operaId",
											false)) {
										String addressid = WSClient.getElementValue(insertAddressResponseXML,
												"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
										WSClient.setData("{var_addressID}", addressid);
										String query=WSClient.getQuery("QS_02");
										LinkedHashMap<String, String> db = WSClient.getDBRow(query);
										LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
										WSClient.writeToReport(LogStatus.PASS,"Address Id :"+addressid);
										String primary=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_primary", XMLType.REQUEST);
										String addresstype=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_addressType", XMLType.REQUEST);
										String language_code=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_languageCode", XMLType.REQUEST);
										String addressLine=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_AddressLine", XMLType.REQUEST);
										String city=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_cityName", XMLType.REQUEST);
										String state=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_stateProv", XMLType.REQUEST);
										String country=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_countryCode", XMLType.REQUEST);
										String zip=WSClient.getElementValue(insertAddressReq,"InsertAddressRequest_NameAddress_postalCode", XMLType.REQUEST);
										String primary_yn="";
										if(primary.equalsIgnoreCase("true"))
											primary_yn="Y";
										else if(primary.equalsIgnoreCase("false"))
											primary_yn="N";
										else 
											primary_yn=primary;
										expected.put("PRIMARY_YN",primary_yn);
										expected.put("ADDRESS_TYPE",addresstype);
										expected.put("LANGUAGE_CODE",language_code);
										expected.put("ADDRESSLINE1",addressLine);
										expected.put("CITY",city);
										expected.put("STATE",state);
										expected.put("COUNTRY",country);
										expected.put("ZIP_CODE",zip);
										WSAssert.assertEquals(expected, db, false);
									}
								}
						  }
								
						  if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
								 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
									String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								 }
							}
							 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
									String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
								}

								if (WSAssert.assertIfElementExists(insertAddressResponseXML,
										"InsertAddressResponse_Result_OperaErrorCode", true)) {
									String code = WSClient.getElementValue(insertAddressResponseXML,
											"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
								}
								if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
										true)) {
									String message = WSClient.getElementValue(insertAddressResponseXML,
											"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
								}
					
				} 
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	

	@Test(groups = { "minimumRegression", "InsertAddress", "OWS","Name" ,"in-QA"})

	public void insertAddress_41903() {
		try {
			String testName = "insertAddress_41903";
			WSClient.startTest(testName,
					"Verify that error message is coming when invalid address type is passed.", "minimumRegression");
			
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			
				String profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				    WSClient.setData("{var_profileID}", profileID);
				    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					WSClient.setData("{var_addressType}", "HWORK");
					HashMap<String, String> address = new HashMap<String, String>();
					address=OPERALib.fetchAddressLOV();				
					WSClient.setData("{var_country}", address.get("Country"));
					
					// OWS insert address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertAddressReq = WSClient.createSOAPMessage("OWSInsertAddress", "DS_01");
					String insertAddressResponseXML = WSClient.processSOAPMessage(insertAddressReq);
					
                    
				if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_Result_resultStatusFlag", false)){
					if (WSAssert.assertIfElementValueEquals(insertAddressResponseXML,
							"InsertAddressResponse_Result_resultStatusFlag", "FAIL", false)) {
					

					}
				}

				 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultcode", true)){
					 if(WSAssert.assertIfElementExists(insertAddressResponseXML,"InsertAddressResponse_faultstring", true)){
						String message=WSClient.getElementValue(insertAddressResponseXML,"InsertAddressResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
					 }
				}
				 if (WSAssert.assertIfElementExists(insertAddressResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(insertAddressResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message+"</b>");
					}

					if (WSAssert.assertIfElementExists(insertAddressResponseXML,
							"InsertAddressResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(insertAddressResponseXML,
								"InsertAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :" + code+"</b>");
					}
					if (WSAssert.assertIfElementExists(insertAddressResponseXML, "InsertAddressResponse_Result_GDSError",
							true)) {
						String message = WSClient.getElementValue(insertAddressResponseXML,
								"InsertAddressResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :" + message+"</b>");
					}
				
			} 
			
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	
}
