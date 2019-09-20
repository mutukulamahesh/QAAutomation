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

public class FetchAddressList extends WSSetUp {

	@Test(groups = { "sanity", "FetchAddressList", "OWS", "Name" })

	public void fetchAddressList_38385() {
		try {
			String testName = "fetchAddressList_38385";
			WSClient.startTest(testName, "verify that address is getting fetched by providing minimal data on request.",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
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

				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_state}", address.get("State"));
				WSClient.setData("{var_country}", address.get("Country"));
				WSClient.setData("{var_zip}", address.get("Zip"));

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);

				
				String profileID=CreateProfile.createProfile("DS_12");
				if(!profileID.equals("error"))
				{
					    WSClient.setData("{var_profileID}", profileID);
					    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					    // OWS fetch Address

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String fetchAddressReq = WSClient.createSOAPMessage("OWSFetchAddressList", "DS_01");
						String fetchAddressResponseXML = WSClient.processSOAPMessage(fetchAddressReq);

						
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								// ************ Validation *********//
								String query=WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);
								LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
								String addressid = WSClient.getElementValue(fetchAddressResponseXML,
										"FetchAddressListResponse_NameAddressList_NameAddress_operaId",
										XMLType.RESPONSE);
								String addressLine = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_AddressLine", XMLType.RESPONSE);
								String city = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_cityName", XMLType.RESPONSE);
								String state = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_stateProv", XMLType.RESPONSE);
								String country = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_countryCode", XMLType.RESPONSE);
								String zip = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_postalCode", XMLType.RESPONSE);
								expected.put("ADDRESS_ID", addressid);
								expected.put("ADDRESSLINE1", addressLine);
								expected.put("CITY", city);
								expected.put("STATE", state);
								expected.put("COUNTRY", country);
								expected.put("ZIP_CODE", zip);

								WSAssert.assertEquals(db, expected, false);
							}
						}
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
									"FetchAddressListResponse_faultstring", true)) {

								String message = WSClient.getElementValue(fetchAddressResponseXML,
										"FetchAddressListResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,"<b>" +message+"</b>");
							}
						}

						if (WSAssert.assertIfElementExists(fetchAddressResponseXML, "Result_Text_TextElement", true)) {

							String message = WSClient.getElementValue(fetchAddressResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>" +"The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" +"The error code displayed in the response is :" + code+"</b>");
						}
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" +"The error displayed in the response is :" + message+"</b>");
						}


					} 

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchAddressList", "OWS", "Name" })
	public void fetchAddressList_38404() {
		try {
			String testName = "fetchAddressList_38404";
			WSClient.startTest(testName,
					"verify that multiple addresses are getting fetched by providing minimal data on request.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
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

				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_state}", address.get("State"));
				WSClient.setData("{var_country}", address.get("Country"));
				WSClient.setData("{var_zip}", address.get("Zip"));

				WSClient.setData("{var_addressType1}", OperaPropConfig.getDataSetForCode("AddressType", "DS_02"));
				HashMap<String, String> address1 = new HashMap<String, String>();
				address1 = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city1}", address1.get("City"));
				WSClient.setData("{var_state1}", address1.get("State"));
				WSClient.setData("{var_country1}", address1.get("Country"));
				WSClient.setData("{var_zip1}", address1.get("Zip"));

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);
				
				String	profileID=CreateProfile.createProfile("DS_12");
				if(!profileID.equals("error"))
				{
					    WSClient.setData("{var_profileID}", profileID);
					    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						// Prerequisite 1 - change profile to add another
						// address

						OPERALib.setOperaHeader(uname);
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_06");
						String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

						if (WSAssert.assertIfElementExists(changeProfileResponseXML, "ChangeProfileRS_Success", true)) {

							if (WSAssert.assertIfElementExists(changeProfileResponseXML,
									"Addresses_AddressInfo_UniqueID_ID", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added second address</b>");
								// OWS fetch Address

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchAddressReq = WSClient.createSOAPMessage("OWSFetchAddressList", "DS_01");
								String fetchAddressResponseXML = WSClient.processSOAPMessage(fetchAddressReq);

								

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_resultStatusFlag", false)) {
									if (WSAssert.assertIfElementValueEquals(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// ************ Validation ***************//
										
										String query=WSClient.getQuery("QS_02");										
										List<LinkedHashMap<String, String>> db = WSClient.getDBRows(query);
										List<LinkedHashMap<String, String>> expected = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										xpath.put("FetchAddressListResponse_NameAddressList_NameAddress_operaId",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_AddressLine",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_cityName",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_stateProv",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_countryCode",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_postalCode",
												"FetchAddressListResponse_NameAddressList_NameAddress");

										expected = WSClient.getMultipleNodeList(fetchAddressResponseXML, xpath, false,
												XMLType.RESPONSE);
										WSAssert.assertEquals(expected, db, false);

									}
								}
								
								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_faultcode", true)) {
									if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
											"FetchAddressListResponse_faultstring", true)) {

										String message = WSClient.getElementValue(fetchAddressResponseXML,
												"FetchAddressListResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL,"<b>" +message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchAddressResponseXML,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>" +"The text displayed in the response is :" + message+"</b>");
								}

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_OperaErrorCode", true)) {
									String code = WSClient.getElementValue(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" +"The error code displayed in the response is :" + code+"</b>");
								}
								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_GDSError", true)) {
									String message = WSClient.getElementValue(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" +"The error displayed in the response is :" + message+"</b>");
								}

							}

							else {
								WSClient.writeToReport(LogStatus.WARNING,
										"************Blocked : New Address not added**********");
							}
						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : New Address not added**********");
						}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchAddressList", "OWS", "Name" })

	public void fetchAddressList_38406() {
		try {
			String testName = "fetchAddressList_38406";
			WSClient.startTest(testName,
					"verify that all the information of address attached to a profile are coming in the response of fetch address list.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
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
                
				WSClient.setData("{var_fname}",WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_lname}",WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_state}", address.get("State"));
				WSClient.setData("{var_country}", address.get("Country"));
				WSClient.setData("{var_zip}", address.get("Zip"));

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);
				
				 String	profileID=CreateProfile.createProfile("DS_04");
				if(!profileID.equals("error"))
				{
					    WSClient.setData("{var_profileID}", profileID);
					    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
                         
						// OWS fetch Address

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String fetchAddressReq = WSClient.createSOAPMessage("OWSFetchAddressList", "DS_01");
						String fetchAddressResponseXML = WSClient.processSOAPMessage(fetchAddressReq);

						
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								// ************ Validation ******//
								String query=WSClient.getQuery("QS_03");
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);
								LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
								String addressid = WSClient.getElementValue(fetchAddressResponseXML,
										"FetchAddressListResponse_NameAddressList_NameAddress_operaId",
										XMLType.RESPONSE);
								String addressLine = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_AddressLine", XMLType.RESPONSE);
								String addressLine2 = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_AddressLine_2", XMLType.RESPONSE);
								String addressLine3 = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_AddressLine_3", XMLType.RESPONSE);
								String addressLine4 = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_AddressLine_4", XMLType.RESPONSE);
								String city = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_cityName", XMLType.RESPONSE);
								String state = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_stateProv", XMLType.RESPONSE);
								String country = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_countryCode", XMLType.RESPONSE);
								String zip = WSClient.getElementValue(fetchAddressResponseXML,
										"NameAddressList_NameAddress_postalCode", XMLType.RESPONSE);
								String addresstype = WSClient.getElementValue(fetchAddressResponseXML,
										"FetchAddressListResponse_NameAddressList_NameAddress_addressType",
										XMLType.RESPONSE);
								String primary = WSClient.getElementValue(fetchAddressResponseXML,
										"FetchAddressListResponse_NameAddressList_NameAddress_primary",
										XMLType.RESPONSE);
								String primary_yn = "";
								if (primary.equalsIgnoreCase("true"))
									primary_yn = "Y";
								else if (primary.equalsIgnoreCase("false"))
									primary_yn = "N";
								else
									primary_yn = primary;

								expected.put("ADDRESS_ID", addressid);
								expected.put("ADDRESS_TYPE", addresstype);
								expected.put("PRIMARY_YN", primary_yn);
								expected.put("ADDRESSLINE1", addressLine);
								expected.put("ADDRESSLINE2", addressLine2);
								expected.put("ADDRESSLINE3", addressLine3);
								expected.put("ADDRESSLINE4", addressLine4);
								expected.put("CITY", city);
								expected.put("STATE", state);
								expected.put("COUNTRY", country);
								expected.put("ZIP_CODE", zip);
								WSAssert.assertEquals(db, expected, false);

							}
						}
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
									"FetchAddressListResponse_faultstring", true)) {

								String message = WSClient.getElementValue(fetchAddressResponseXML,
										"FetchAddressListResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,"<b>" +message+"</b>");
							}
						}

						if (WSAssert.assertIfElementExists(fetchAddressResponseXML, "Result_Text_TextElement", true)) {

							String message = WSClient.getElementValue(fetchAddressResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>" +"The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" +"The error code displayed in the response is :" + code+"</b>");
						}
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" +"The error displayed in the response is :" + message+"</b>");
						}

				}
			}
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchAddressList", "OWS", "Name" })

	public void fetchAddressList_38421() {
		try {
			String testName = "fetchAddressList_38421";
			WSClient.startTest(testName, "verify that error is coming in response when invalid name id is given in request",
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
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			WSClient.setData("{var_profileID}", WSClient.getKeywordData("{KEYWORD_ID}"));

			// OWS fetch Address

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String fetchAddressReq = WSClient.createSOAPMessage("OWSFetchAddressList", "DS_01");
			String fetchAddressResponseXML = WSClient.processSOAPMessage(fetchAddressReq);

			if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
					"FetchAddressListResponse_Result_resultStatusFlag", false)) {

				WSAssert.assertIfElementValueEquals(fetchAddressResponseXML,
						"FetchAddressListResponse_Result_resultStatusFlag", "FAIL", false);
			}

			if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
					"FetchAddressListResponse_faultcode", true)) {
				if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
						"FetchAddressListResponse_faultstring", true)) {

					String message = WSClient.getElementValue(fetchAddressResponseXML,
							"FetchAddressListResponse_faultstring", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,"<b>" +message+"</b>");
				}
			}

			if (WSAssert.assertIfElementExists(fetchAddressResponseXML, "Result_Text_TextElement", true)) {

				String message = WSClient.getElementValue(fetchAddressResponseXML,
						"Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>" +"The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
					"FetchAddressListResponse_Result_OperaErrorCode", true)) {
				String code = WSClient.getElementValue(fetchAddressResponseXML,
						"FetchAddressListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" +"The error code displayed in the response is :" + code+"</b>");
			}
			if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
					"FetchAddressListResponse_Result_GDSError", true)) {
				String message = WSClient.getElementValue(fetchAddressResponseXML,
						"FetchAddressListResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" +"The error displayed in the response is :" + message+"</b>");
			}


		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchAddressList", "OWS", "Name" })

	public void fetchAddressList_38461() {
		try {
			String testName = "fetchAddressList_38461";
			WSClient.startTest(testName,
					"verify that error is coming in response when INTERNAL is not given in request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
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

				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_state}", address.get("State"));
				WSClient.setData("{var_country}", address.get("Country"));
				WSClient.setData("{var_zip}", address.get("Zip"));

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);
				
					String profileID=CreateProfile.createProfile("DS_12");
				if(!profileID.equals("error"))
				{
					    WSClient.setData("{var_profileID}", profileID);

					    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						// OWS fetch Address

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String fetchAddressReq = WSClient.createSOAPMessage("OWSFetchAddressList", "DS_02");
						String fetchAddressResponseXML = WSClient.processSOAPMessage(fetchAddressReq);

						
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_resultStatusFlag", false)) {
							WSAssert.assertIfElementValueEquals(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_resultStatusFlag", "FAIL", false);
						}
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
									"FetchAddressListResponse_faultstring", true)) {

								String message = WSClient.getElementValue(fetchAddressResponseXML,
										"FetchAddressListResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,"<b>" +message+"</b>");
							}
						}

						if (WSAssert.assertIfElementExists(fetchAddressResponseXML, "Result_Text_TextElement", true)) {

							String message = WSClient.getElementValue(fetchAddressResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>" +"The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" +"The error code displayed in the response is :" + code+"</b>");
						}
						if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
								"FetchAddressListResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(fetchAddressResponseXML,
									"FetchAddressListResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" +"The error displayed in the response is :" + message+"</b>");
						}


					} 
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchAddressList", "OWS", "Name" })
	public void fetchAddressList_38459() {
		try {
			String testName = "fetchAddressList_38459";
			WSClient.startTest(testName,
					"verify that multiple addresses are getting fetched of all the languages in the response when returnAllLanguageAddress=Y.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
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

				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_state}", address.get("State"));
				WSClient.setData("{var_country}", address.get("Country"));
				WSClient.setData("{var_zip}", address.get("Zip"));

				WSClient.setData("{var_addressType1}", OperaPropConfig.getDataSetForCode("AddressType", "DS_02"));
				HashMap<String, String> address1 = new HashMap<String, String>();
				address1 = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city1}", address1.get("City"));
				WSClient.setData("{var_state1}", address1.get("State"));
				WSClient.setData("{var_country1}", address1.get("Country"));
				WSClient.setData("{var_zip1}", address1.get("Zip"));

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);
				
				String	profileID=CreateProfile.createProfile("DS_12");
				if(!profileID.equals("error"))
				{
					    WSClient.setData("{var_profileID}", profileID);
					    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						// Prerequisite 1 - change profile to add another
						// address

						OPERALib.setOperaHeader(uname);
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_07");
						String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

						if (WSAssert.assertIfElementExists(changeProfileResponseXML, "ChangeProfileRS_Success", true)) {

							if (WSAssert.assertIfElementExists(changeProfileResponseXML,
									"Addresses_AddressInfo_UniqueID_ID", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added second address with different language code.");
								// OWS fetch Address with return all addresses
								// as Y
								
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchAddressReq = WSClient.createSOAPMessage("OWSFetchAddressList", "DS_03");
								String fetchAddressResponseXML = WSClient.processSOAPMessage(fetchAddressReq);

								
								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_resultStatusFlag", false)) {

									if (WSAssert.assertIfElementValueEquals(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// DB Validation  
										
										String query=WSClient.getQuery("QS_04");
										List<LinkedHashMap<String, String>> db = WSClient.getDBRows(query);
										List<LinkedHashMap<String, String>> expected = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										xpath.put("FetchAddressListResponse_NameAddressList_NameAddress_operaId",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_AddressLine",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_cityName",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_stateProv",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_countryCode",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_postalCode",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("FetchAddressListResponse_NameAddressList_NameAddress_languageCode",
												"FetchAddressListResponse_NameAddressList_NameAddress");

										expected = WSClient.getMultipleNodeList(fetchAddressResponseXML, xpath, false,
												XMLType.RESPONSE);
										WSAssert.assertEquals(expected, db, false);

									}
								}
								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_faultcode", true)) {
									if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
											"FetchAddressListResponse_faultstring", true)) {

										String message = WSClient.getElementValue(fetchAddressResponseXML,
												"FetchAddressListResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL,"<b>" +message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchAddressResponseXML,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>" +"The text displayed in the response is :" + message+"</b>");
								}

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_OperaErrorCode", true)) {
									String code = WSClient.getElementValue(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" +"The error code displayed in the response is :" + code+"</b>");
								}
								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_GDSError", true)) {
									String message = WSClient.getElementValue(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" +"The error displayed in the response is :" + message+"</b>");
								}
								
								
							}

							else {
								WSClient.writeToReport(LogStatus.WARNING,
										"************Blocked : New Address not added**********");
							}
						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : New Address not added**********");
						}
					} 
				}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	@Test(groups = { "minimumRegression", "FetchAddressList", "OWS", "Name" })
	public void fetchAddressList_39038() {
		try {
			String testName = "fetchAddressList_39038";
			WSClient.startTest(testName,
					"verify that  addresses whose language=E is getting fetched in the response when returnAllLanguageAddress=N.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
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

				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				HashMap<String, String> address = new HashMap<String, String>();
				address = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_state}", address.get("State"));
				WSClient.setData("{var_country}", address.get("Country"));
				WSClient.setData("{var_zip}", address.get("Zip"));

				WSClient.setData("{var_addressType1}", OperaPropConfig.getDataSetForCode("AddressType", "DS_02"));
				HashMap<String, String> address1 = new HashMap<String, String>();
				address1 = OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city1}", address1.get("City"));
				WSClient.setData("{var_state1}", address1.get("State"));
				WSClient.setData("{var_country1}", address1.get("Country"));
				WSClient.setData("{var_zip1}", address1.get("Zip"));

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);
				
				String profileID=CreateProfile.createProfile("DS_12");
				if(!profileID.equals("error"))
				{
					    WSClient.setData("{var_profileID}", profileID);
					    WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						// Prerequisite 1 - change profile to add another
						// address

						OPERALib.setOperaHeader(uname);
						String changeProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_07");
						String changeProfileResponseXML = WSClient.processSOAPMessage(changeProfileReq);

						if (WSAssert.assertIfElementExists(changeProfileResponseXML, "ChangeProfileRS_Success", true)) {

							if (WSAssert.assertIfElementExists(changeProfileResponseXML,
									"Addresses_AddressInfo_UniqueID_ID", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added second address with different language code.</b>");
								
								// OWS fetch Address with return all addresses
								// as N
								
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchAddressReq1 = WSClient.createSOAPMessage("OWSFetchAddressList", "DS_01");
								String fetchAddressResponseXML = WSClient.processSOAPMessage(fetchAddressReq1);

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_resultStatusFlag", false)) {

									if (WSAssert.assertIfElementValueEquals(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// ************ Validation
										// ************//
                                        
										String query=WSClient.getQuery("QS_05");
										List<LinkedHashMap<String, String>> db = WSClient.getDBRows(query);
										List<LinkedHashMap<String, String>> expected = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										xpath.put("FetchAddressListResponse_NameAddressList_NameAddress_operaId",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_AddressLine",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_cityName",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_stateProv",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_countryCode",
												"FetchAddressListResponse_NameAddressList_NameAddress");
										xpath.put("NameAddressList_NameAddress_postalCode",
												"FetchAddressListResponse_NameAddressList_NameAddress");

										expected = WSClient.getMultipleNodeList(fetchAddressResponseXML, xpath, false,
												XMLType.RESPONSE);
										WSAssert.assertEquals(expected, db, false);

									}
								}

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_faultcode", true)) {
									if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
											"FetchAddressListResponse_faultstring", true)) {

										String message = WSClient.getElementValue(fetchAddressResponseXML,
												"FetchAddressListResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL,"<b>" +message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchAddressResponseXML,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>" +"The text displayed in the response is :" + message+"</b>");
								}

								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_OperaErrorCode", true)) {
									String code = WSClient.getElementValue(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" +"The error code displayed in the response is :" + code+"</b>");
								}
								if (WSAssert.assertIfElementExists(fetchAddressResponseXML,
										"FetchAddressListResponse_Result_GDSError", true)) {
									String message = WSClient.getElementValue(fetchAddressResponseXML,
											"FetchAddressListResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" +"The error displayed in the response is :" + message+"</b>");
								}

							}

							else {
								WSClient.writeToReport(LogStatus.WARNING,
										"************Blocked : New Address not added**********");
							}
						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : New Address not added**********");
						}
					} 
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}


}
