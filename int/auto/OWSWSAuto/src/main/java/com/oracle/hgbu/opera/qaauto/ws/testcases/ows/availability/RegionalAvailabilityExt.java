package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.availability;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class RegionalAvailabilityExt extends WSSetUp {

	public boolean setChannelParameter(String param, String paramValue) throws Exception {
		WSClient.setData("{var_parname}", param);
		WSClient.setData("{var_par}", paramValue);

		String fetchChannelParametersReq = WSClient.createSOAPMessage("FetchChannelParameters", "DS_01");
		String fetchChannelParametersRes = WSClient.processSOAPMessage(fetchChannelParametersReq);
		if (WSAssert.assertIfElementExists(fetchChannelParametersRes, "FetchChannelParametersRS_Success", true)) {
			if (WSAssert.assertIfElementValueEquals(fetchChannelParametersRes,
					"ChannelParameters_ChannelParameter_ParameterValue", paramValue, true)) {
				return true;
			} else {
				String changeChannelParametersReq = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String changeChannelParametersRes = WSClient.processSOAPMessage(changeChannelParametersReq);
			}
			fetchChannelParametersReq = WSClient.createSOAPMessage("FetchChannelParameters", "DS_01");
			fetchChannelParametersRes = WSClient.processSOAPMessage(fetchChannelParametersReq);
			if (WSAssert.assertIfElementExists(fetchChannelParametersRes, "FetchChannelParametersRS_Success", true)) {
				if (WSAssert.assertIfElementValueEquals(fetchChannelParametersRes,
						"ChannelParameters_ChannelParameter_ParameterValue", paramValue, true)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method to check if the OWS Availability Regional Availability Ext is working
	 * i.e., all Hotels available for given hotel preference,timeline,no.of guests
	 * are populated correctly.
	 *
	 */
	@Test(groups = { "sanity", "RegionalAvailabilityExt", "Availability", "OWS" })

	public void regionalAvailabilityExt_39741() {

		try {
			String testName = "regionalAvailabilityExt_39741";
			WSClient.startTest(testName,
					"Verify that list of all Hotels available for given hotel preference,timeline,no.of guests are populated correctly",
					"sanity");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Setting OWS soap header
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			// Invoking targeted service
			String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_01");
			String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);

			if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					String startDate = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_StartDate", XMLType.REQUEST);
					String endDate = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_EndDate", XMLType.REQUEST);
					WSClient.setData("{var_startDate}", startDate);
					WSClient.setData("{var_endDate}", endDate);
					String query = WSClient.getQuery("QS_04");
					HashMap<String, String> db = WSClient.getDBRow(query);
					WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell",
							db.get("availableRooms"), false);

					query = WSClient.getQuery("QS_01");
					HashMap<String, String> DBResults = WSClient.getDBRow(query);

					// WSAssert.assertIfElementValueEquals(regionalAvailRes,
					// "RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_chainCode",
					// DBResults.get("CHAIN_CODE"), false);
					// WSAssert.assertIfElementValueEquals(regionalAvailRes,
					// "RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode",
					// DBResults.get("GDS_RESORT"), false);
					// WSAssert.assertIfElementValueEquals(regionalAvailRes,
					// "RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell",
					// DBResults.get(""), false);

					/************** Validate the address fields **********************/
					WSAssert.assertIfElementValueEquals(regionalAvailRes, "Addresses_Address_AddressLine",
							DBResults.get("STREET"), false);
					WSAssert.assertIfElementValueEquals(regionalAvailRes, "Addresses_Address_countryCode",
							DBResults.get("COUNTRY_CODE"), false);
					WSAssert.assertIfElementValueEquals(regionalAvailRes, "Addresses_Address_stateProv",
							DBResults.get("STATE"), false);
					WSAssert.assertIfElementValueEquals(regionalAvailRes, "Addresses_Address_cityName",
							DBResults.get("CITY"), false);
					if((DBResults.containsKey("POST_CODE")))
					{
						WSAssert.assertIfElementValueEquals(regionalAvailRes, "Addresses_Address_postalCode",
								DBResults.get("POST_CODE"), false);
					}

					/*************
					 * If contact email's exists then validate it else do not validate
					 **********************/

					if (WSAssert.assertIfElementExists(regionalAvailRes,
							"RegionalAvailableProperty_HotelContact_ContactEmails", true)) {
						WSAssert.assertIfElementValueEquals(regionalAvailRes, "HotelContact_ContactEmails_ContactEmail",
								DBResults.get("EMAIL"), false);
					}

					/*************
					 * If contact phones exists then validate it else do not validate
					 **********************/

					if (WSAssert.assertIfElementExists(regionalAvailRes, "HotelContact_ContactPhones_Phone", true)) {
						HashMap<String, String> xPath = new HashMap<>();
						xPath.put("HotelContact_ContactPhones_Phone_phoneType", "HotelContact_ContactPhones_Phone");
						xPath.put("ContactPhones_Phone_PhoneNumber", "HotelContact_ContactPhones_Phone");
						List<LinkedHashMap<String, String>> resValues = WSClient.getMultipleNodeList(regionalAvailRes,
								xPath, false, XMLType.RESPONSE);
						System.out.println(resValues);
						System.out.println(DBResults);
						for (int index = 0; index < resValues.size(); index++) {
							// WSClient.writeToReport(LogStatus.INFO, "Loop index : "+index);
							HashMap<String, String> hotelInfo = resValues.get(index);
							if (hotelInfo.containsValue("FAX")) {
								if (WSAssert.assertEquals(DBResults.get("FAX"), hotelInfo.get("PhoneNumber1"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "FAX ->  Expected : " + DBResults.get("FAX")
									+ "   Actual : " + hotelInfo.get("PhoneNumber1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "FAX ->  Expected : " + DBResults.get("FAX")
									+ "   Actual : " + hotelInfo.get("PhoneNumber1"));
								}
							}
							if (hotelInfo.containsValue("VOICE")) {
								if (WSAssert.assertEquals(DBResults.get("TELEPHONE"), hotelInfo.get("PhoneNumber1"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"VOICE ->  Expected : " + DBResults.get("TELEPHONE") + "   Actual : "
													+ hotelInfo.get("PhoneNumber1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"VOICE ->  Expected : " + DBResults.get("TELEPHONE") + "   Actual : "
													+ hotelInfo.get("PhoneNumber1"));
								}
							}
						}

						/*******
						 * Validate Hotel Extended Information such as Web address,resort
						 * type,notes,check in time,check out time,time zone region
						 *********/
						if (WSAssert.assertIfElementExists(regionalAvailRes,
								"RegionalAvailableProperty_HotelExtendedInformation_HotelInformation", true)) {
							xPath = new HashMap<>();
							xPath.put("HotelExtendedInformation_HotelInformation_HotelInfo_hotelInfoType",
									"HotelExtendedInformation_HotelInformation_HotelInfo");
							xPath.put("HotelExtendedInformation_HotelInformation_HotelInfo_otherHotelInfoType",
									"HotelExtendedInformation_HotelInformation_HotelInfo");
							xPath.put("HotelInfo_Text_TextElement_2",
									"HotelExtendedInformation_HotelInformation_HotelInfo");
							xPath.put("HotelInformation_HotelInfo_Url_2",
									"HotelExtendedInformation_HotelInformation_HotelInfo");

							resValues = WSAssert.getMultipleNodeList(regionalAvailRes, xPath, false, XMLType.RESPONSE);

							for (int index = 0; index < resValues.size(); index++) {
								// WSClient.writeToReport(LogStatus.INFO, "Loop index : "+index);
								HashMap<String, String> hotelInfo = resValues.get(index);
								if (hotelInfo.containsValue("PROPERTY_WEBADDRESS")) {
									if (WSAssert.assertEquals(DBResults.get("WEBADDRESS"), hotelInfo.get("Url1"),
											true)) {
										WSClient.writeToReport(LogStatus.PASS, "WEBADDRESS ->  Expected : "
												+ DBResults.get("WEBADDRESS") + "   Actual : " + hotelInfo.get("Url1"));
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "WEBADDRESS ->  Expected : "
												+ DBResults.get("WEBADDRESS") + "   Actual : " + hotelInfo.get("Url1"));
									}
								}
								if (hotelInfo.containsValue("GRADE")) {
									if (WSAssert.assertEquals(DBResults.get("RESORT_TYPE"),
											hotelInfo.get("TextTextElement1"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"RESORT TYPE ->  Expected : " + DBResults.get("RESORT_TYPE")
												+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"RESORT TYPE ->  Expected : " + DBResults.get("RESORT_TYPE")
												+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									}
								}
								if (hotelInfo.containsValue("CHECKININFO")) {
									String checkin = DBResults.get("CHECK_IN_TIME");
									if (WSAssert.assertEquals(
											checkin.substring(checkin.indexOf(" ") + 1, checkin.lastIndexOf(':')),
											hotelInfo.get("TextTextElement1"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"CHECK-IN TIME ->  Expected : "
														+ checkin.substring(checkin.indexOf(" ") + 1,
																checkin.lastIndexOf(':'))
														+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"CHECK-IN TIME ->  Expected : "
														+ checkin.substring(checkin.indexOf(" ") + 1,
																checkin.lastIndexOf(':'))
														+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									}
								}
								if (hotelInfo.containsValue("CHECKOUTINFO")) {
									String checkout = DBResults.get("CHECK_OUT_TIME");
									if (WSAssert.assertEquals(
											checkout.substring(checkout.indexOf(" ") + 1, checkout.lastIndexOf(':')),
											hotelInfo.get("TextTextElement1"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"CHECK-OUT TIME ->  Expected : "
														+ checkout.substring(checkout.indexOf(" ") + 1,
																checkout.lastIndexOf(':'))
														+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"CHECK-OUT TIME ->  Expected : "
														+ checkout.substring(checkout.indexOf(" ") + 1,
																checkout.lastIndexOf(':'))
														+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									}
								}
								if (hotelInfo.containsValue("PROPERTY_TIMEZONE")) {
									if (WSAssert.assertEquals(DBResults.get("TIMEZONE_REGION"),
											hotelInfo.get("TextTextElement1"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"TIMEZONE REGION ->  Expected : " + DBResults.get("TIMEZONE_REGION")
												+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"TIMEZONE REGION ->  Expected : " + DBResults.get("TIMEZONE_REGION")
												+ "   Actual : " + hotelInfo.get("TextTextElement1"));
									}
								}

							}
						}
					}

					WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailableProperty_HotelExtendedInformation_Position_latitude",
							DBResults.get("LATITUDE"), false);
					WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailableProperty_HotelExtendedInformation_Position_longitude",
							DBResults.get("LONGITUDE"), false);

				} else if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_GDSError", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
										"RegionalAvailabilityExtResponse_Result_GDSError", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_faultstring",
					true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
								"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}


	}

	/**
	 * Method to check if the OWS Availability Regional Availability Ext is working
	 * i.e., all Hotels available for given geographic details,timeline,no.of guests
	 * are populated correctly.
	 *
	 * PreRequisites Required: -->Atleast one of the channel configured resorts have
	 * city,country,state values set
	 *
	 */
	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })

	public void regionalAvailabilityExt_40140() {

		try {
			String testName = "regionalAvailabilityExt_40140";
			WSClient.startTest(testName,
					"Verify that the mentioned Request provides a list of all Hotels available for given city and/or region and/or location for a given timeline with a filter for a number of guests",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Setting OWS soap header
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Getting values for address tags in request
			String query = WSClient.getQuery("OWSRegionalAvailabilityExt", "QS_03");
			HashMap<String, String> address = WSClient.getDBRow(query);
			if (address.get("CITY") != null) {
				WSClient.setData("{var_city}", address.get("CITY"));
				WSClient.setData("{var_country}", address.get("COUNTRY_CODE"));
				WSClient.setData("{var_state}", address.get("STATE"));

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

				// Invoking targeted request
				String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_02");
				String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);

				if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
					if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						query = WSClient.getQuery("QS_02");
						ArrayList<LinkedHashMap<String, String>> DBResults = WSClient.getDBRows(query);

						HashMap<String, String> xpath = new HashMap<>();
						xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_chainCode",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						// xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell","RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("Addresses_Address_stateProv",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("Addresses_Address_cityName",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("Addresses_Address_countryCode",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						List<LinkedHashMap<String, String>> resValues = WSClient.getMultipleNodeList(regionalAvailRes,
								xpath, true, XMLType.RESPONSE);

						WSAssert.assertEquals(resValues, DBResults, false);


						WSClient.setData("{var_extResort}", WSClient.getElementValue(regionalAvailRes, "RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode", XMLType.RESPONSE));
						HashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_05"));
						WSClient.setData("{var_resort}", db.get("RESORT"));

						String startDate = WSClient.getElementValue(regionalAvailReq,
								"RegionalAvailabilityExtRequest_StayDateRange_StartDate", XMLType.REQUEST);
						String endDate = WSClient.getElementValue(regionalAvailReq,
								"RegionalAvailabilityExtRequest_StayDateRange_EndDate", XMLType.REQUEST);
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);
						query = WSClient.getQuery("QS_04");
						db = WSClient.getDBRow(query);
						WSAssert.assertIfElementValueEquals(regionalAvailRes,
								"RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell",
								db.get("availableRooms"), false);




					} else if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", true)) {
						if (WSAssert.assertIfElementExists(regionalAvailRes,
								"RegionalAvailabilityExtResponse_Result_GDSError", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
											"RegionalAvailabilityExtResponse_Result_GDSError", XMLType.RESPONSE));
						}
					}
				} else if (WSAssert.assertIfElementExists(regionalAvailRes,
						"RegionalAvailabilityExtResponse_faultstring", true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
									"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-Requisite Failed >> None of the channel configured resorts have city,country,state values set");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	/**
	 * Method to check if the OWS Availability Regional Availability Ext is working
	 * i.e., all Hotels available for given hotel preference,timeline,no.of guests
	 * are populated correctly.
	 *
	 */
	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })

	public void regionalAvailabilityExt_40939() {

		try {
			String testName = "regionalAvailabilityExt_40939";
			WSClient.startTest(testName,
					"Verify that the mentioned Request provides list of all Hotels available for given hotel preference for a given timeline with filter for number of guests. The response will also search for alternate hotels as well.",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Setting OWS soap header
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			// Invoking targeted service
			String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_03");
			String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);

			if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {


					String startDate = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_StartDate", XMLType.REQUEST);
					String endDate = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_EndDate", XMLType.REQUEST);
					WSClient.setData("{var_startDate}", startDate);
					WSClient.setData("{var_endDate}", endDate);
					String query = WSClient.getQuery("QS_04");
					HashMap<String, String> db = WSClient.getDBRow(query);
					WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell",
							db.get("availableRooms"), false);



					List<LinkedHashMap<String, String>> hotelCodes = new ArrayList<LinkedHashMap<String, String>>();

					LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
					temp.put("HotelReferencehotelCode1",
							WSClient.getElementValue(regionalAvailReq,
									"RegionalAvailabilityExtRequest_HotelReferences_HotelReference_hotelCode",
									XMLType.REQUEST));

					hotelCodes.add(temp);

					temp = new LinkedHashMap<String, String>();
					temp.put("HotelReferencehotelCode1",
							WSClient.getElementValue(regionalAvailReq,
									"RegionalAvailabilityExtRequest_HotelReferences_HotelReference_hotelCode_2",
									XMLType.REQUEST));
					hotelCodes.add(temp);
					LinkedHashMap<String, String> xpath = new LinkedHashMap<String, String>();
					xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode",
							"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
					List<LinkedHashMap<String, String>> resHotelCodes = WSClient.getMultipleNodeList(regionalAvailRes,
							xpath, false, XMLType.RESPONSE);
					System.out.println(resHotelCodes);
					WSAssert.assertEquals(resHotelCodes, hotelCodes, false);

				} else if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_GDSError", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
										"RegionalAvailabilityExtResponse_Result_GDSError", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_faultstring",
					true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
								"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	/**
	 * Method to check if the OWS Availability Regional Availability Ext is working
	 * i.e., all Hotels available for given geographic details,timeline,no.of guests
	 * are populated correctly.
	 *
	 * PreRequisites Required: -->Atleast one of the channel configured resorts have
	 * city,country,state values set
	 *
	 */
	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })

	public void regionalAvailabilityExt_40940() {

		try {
			String testName = "regionalAvailabilityExt_40940";
			WSClient.startTest(testName,
					"Verify that the mentioned Request provides list of all Hotels available for given city and/or region and/or location for a given timeline with filter for number of guests and number of rooms required.",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Setting OWS soap header
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// Getting values for address tags in request
			String query = WSClient.getQuery("OWSRegionalAvailabilityExt", "QS_03");
			HashMap<String, String> address = WSClient.getDBRow(query);
			if (address.get("CITY") != null) {
				WSClient.setData("{var_city}", address.get("CITY"));
				WSClient.setData("{var_country}", address.get("COUNTRY_CODE"));
				WSClient.setData("{var_state}", address.get("STATE"));

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

				// Invoking targeted request
				String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_04");
				String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);

				if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
					if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {


						query = WSClient.getQuery("QS_02");
						ArrayList<LinkedHashMap<String, String>> DBResults = WSClient.getDBRows(query);

						HashMap<String, String> xpath = new HashMap<>();
						xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_chainCode",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						// xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell","RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("Addresses_Address_stateProv",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("Addresses_Address_cityName",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						xpath.put("Addresses_Address_countryCode",
								"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
						List<LinkedHashMap<String, String>> resValues = WSClient.getMultipleNodeList(regionalAvailRes,
								xpath, true, XMLType.RESPONSE);

						WSAssert.assertEquals(resValues, DBResults, false);


						WSClient.setData("{var_extResort}", WSClient.getElementValue(regionalAvailRes, "RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode", XMLType.RESPONSE));
						HashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_05"));
						WSClient.setData("{var_resort}", db.get("RESORT"));

						String startDate = WSClient.getElementValue(regionalAvailReq,
								"RegionalAvailabilityExtRequest_StayDateRange_StartDate", XMLType.REQUEST);
						String endDate = WSClient.getElementValue(regionalAvailReq,
								"RegionalAvailabilityExtRequest_StayDateRange_EndDate", XMLType.REQUEST);
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);
						query = WSClient.getQuery("QS_04");
						db = WSClient.getDBRow(query);
						WSAssert.assertIfElementValueEquals(regionalAvailRes,
								"RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell",
								db.get("availableRooms"), false);


					} else if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", true)) {
						if (WSAssert.assertIfElementExists(regionalAvailRes,
								"RegionalAvailabilityExtResponse_Result_GDSError", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
											"RegionalAvailabilityExtResponse_Result_GDSError", XMLType.RESPONSE));
						}
					}
				} else if (WSAssert.assertIfElementExists(regionalAvailRes,
						"RegionalAvailabilityExtResponse_faultstring", true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
									"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-Requisite Failed >> None of the channel configured resorts have city,country,state values set");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	/**
	 * Method to check if the OWS Availability Regional Availability Ext is working
	 * i.e., all Hotels available for given hotel preference,timeline,no.of guests
	 * are populated correctly.
	 *
	 */
	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })

	public void regionalAvailabilityExt_41037() {

		try {
			String testName = "regionalAvailabilityExt_41037";
			WSClient.startTest(testName,
					"Verify that the mentioned Request provides list of all Hotels availablefor given hotel preferences for a given timeline with filter for number of guests and number of rooms required.",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Setting OWS soap header
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			// Invoking targeted service
			String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_05");
			String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);

			if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					String startDate = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_StartDate", XMLType.REQUEST);
					String endDate = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_EndDate", XMLType.REQUEST);
					WSClient.setData("{var_startDate}", startDate);
					WSClient.setData("{var_endDate}", endDate);
					String query = WSClient.getQuery("QS_04");
					HashMap<String, String> db = WSClient.getDBRow(query);
					WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell",
							db.get("availableRooms"), false);



					List<LinkedHashMap<String, String>> hotelCodes = new ArrayList<LinkedHashMap<String, String>>();

					LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
					temp.put("HotelReferencehotelCode1",
							WSClient.getElementValue(regionalAvailReq,
									"RegionalAvailabilityExtRequest_HotelReferences_HotelReference_hotelCode",
									XMLType.REQUEST));

					hotelCodes.add(temp);


					LinkedHashMap<String, String> xpath = new LinkedHashMap<String, String>();
					xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode",
							"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
					List<LinkedHashMap<String, String>> resHotelCodes = WSClient.getMultipleNodeList(regionalAvailRes,
							xpath, false, XMLType.RESPONSE);
					System.out.println(resHotelCodes);
					WSAssert.assertEquals(resHotelCodes, hotelCodes, false);

				} else if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_GDSError", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
										"RegionalAvailabilityExtResponse_Result_GDSError", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_faultstring",
					true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
								"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	//	// not completed bcuz min and maxrate tags are not showing up on the response
	//	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })
	//
	//	public void regionalAvailabilityExt_41038() {
	//
	//		try {
	//			String testName = "regionalAvailabilityExt_41038";
	//			WSClient.startTest(testName,
	//					"Verify that the mentioned Request provides list of all Hotels available in the given city and/or region and/or location for a given timeline with filter for number of guests, number of rooms required and rate amounts.",
	//					"minimumRegression");
	//
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			// Setting OWS soap header
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			WSClient.setData("{var_owsresort}", resort);
	//			WSClient.setData("{var_channel}", channel);
	//			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//
	//			OPERALib.setOperaHeader(uname);
	//
	//			/***** check fetch_reservation parameter will be ALL *****/
	//
	//			if (setChannelParameter("IGNORE_REQUESTED_CURRENCY_IN_RATERANGE_DISPLAY", "Y")) {
	//				if (setChannelParameter("ALTERNATE_RATES_RETURNED", "N")) {
	//					if (setChannelParameter("INCLUDE_MULTICURRENCY_RATE_CODES", "N")) {
	//
	//						// Getting values for address tags in request
	//						String query = WSClient.getQuery("OWSRegionalAvailabilityExt", "QS_03");
	//						HashMap<String, String> address = WSClient.getDBRow(query);
	//						if (address.get("CITY") != null) {
	//							WSClient.setData("{var_city}", address.get("CITY"));
	//							WSClient.setData("{var_country}", address.get("COUNTRY_CODE"));
	//							WSClient.setData("{var_state}", address.get("STATE"));
	//
	//							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//							if (OperaPropConfig.getPropertyConfigResults(new String[] { "Currency" })) {
	//
	//								WSClient.setData("{var_currencyCode}",
	//										OperaPropConfig.getDataSetForCode("Currency", "DS_01"));
	//								// Invoking targeted request
	//								String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt",
	//										"DS_06");
	//								String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);
	//
	//								if (WSAssert.assertIfElementExists(regionalAvailRes,
	//										"RegionalAvailabilityExtResponse_Result", true)) {
	//									if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
	//											"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "SUCCESS",
	//											false)) {
	//										query = WSClient.getQuery("QS_02");
	//										ArrayList<LinkedHashMap<String, String>> DBResults = WSClient.getDBRows(query);
	//
	//										HashMap<String, String> xpath = new HashMap<>();
	//										xpath.put(
	//												"RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_chainCode",
	//												"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
	//										xpath.put(
	//												"RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode",
	//												"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
	//										// xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell","RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
	//										xpath.put("Addresses_Address_stateProv",
	//												"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
	//										xpath.put("Addresses_Address_cityName",
	//												"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
	//										xpath.put("Addresses_Address_countryCode",
	//												"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
	//										List<LinkedHashMap<String, String>> resValues = WSClient
	//												.getMultipleNodeList(regionalAvailRes, xpath, true, XMLType.RESPONSE);
	//
	//										WSAssert.assertEquals(resValues, DBResults, false);
	//
	//									} else if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
	//											"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", true)) {
	//										if (WSAssert.assertIfElementExists(regionalAvailRes,
	//												"RegionalAvailabilityExtResponse_Result_GDSError", true)) {
	//											WSClient.writeToReport(LogStatus.INFO,
	//													"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
	//															"RegionalAvailabilityExtResponse_Result_GDSError",
	//															XMLType.RESPONSE));
	//										}
	//									}
	//								} else if (WSAssert.assertIfElementExists(regionalAvailRes,
	//										"RegionalAvailabilityExtResponse_faultstring", true)) {
	//									WSClient.writeToReport(LogStatus.INFO,
	//											"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
	//													"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
	//								}
	//							} else {
	//								WSClient.writeToReport(LogStatus.WARNING,
	//										"Pre-Requisite Failed >> None of the channel configured resorts have city,country,state values set");
	//							}
	//						}
	//					}
	//				}
	//			}
	//
	//		} catch (Exception e) {
	//		} finally {
	//			try {
	//				setChannelParameter("IGNORE_REQUESTED_CURRENCY_IN_RATERANGE_DISPLAY", "N");
	//			} catch (Exception e) {
	//				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//			}
	//		}
	//
	//	}

	/**
	 * Method to check if the OWS Availability Regional Availability Ext is working
	 * i.e., all Hotels available for given hotel preference,timeline,no.of guests
	 * are populated correctly.
	 *
	 */
	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })

	public void regionalAvailabilityExt_41282() {

		try {
			String testName = "regionalAvailabilityExt_41282";
			WSClient.startTest(testName,
					"Verify that the mentioned Request provides list of all Hotels available for given hotel preference for a given timeline with filter for number of guests when duration is given in the response The response will also search for alternate hotels as well.",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Setting OWS soap header
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			// Invoking targeted service
			String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_07");
			String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);

			if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					String startDate = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_StartDate", XMLType.REQUEST);
					String duration = WSClient.getElementValue(regionalAvailReq,
							"RegionalAvailabilityExtRequest_StayDateRange_Duration", XMLType.REQUEST);

					//					String endDate = str(new Date(startDate) + Integer.parseInt(duration));

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(startDate));
					c.add(Calendar.DATE, Integer.parseInt(duration));  // number of days to add
					String endDate = sdf.format(c.getTime());  // dt is now the new date


					WSClient.setData("{var_startDate}", startDate);
					WSClient.setData("{var_endDate}", endDate);
					String query = WSClient.getQuery("QS_04");
					HashMap<String, String> db = WSClient.getDBRow(query);
					WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailableProperties_RegionalAvailableProperty_NumberToSell",
							db.get("availableRooms"), false);


					List<LinkedHashMap<String, String>> hotelCodes = new ArrayList<LinkedHashMap<String, String>>();

					LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
					temp.put("HotelReferencehotelCode1",
							WSClient.getElementValue(regionalAvailReq,
									"RegionalAvailabilityExtRequest_HotelReferences_HotelReference_hotelCode",
									XMLType.REQUEST));

					hotelCodes.add(temp);


					LinkedHashMap<String, String> xpath = new LinkedHashMap<String, String>();
					xpath.put("RegionalAvailableProperties_RegionalAvailableProperty_HotelReference_hotelCode",
							"RegionalAvailabilityExtResponse_RegionalAvailableProperties_RegionalAvailableProperty");
					List<LinkedHashMap<String, String>> resHotelCodes = WSClient.getMultipleNodeList(regionalAvailRes,
							xpath, false, XMLType.RESPONSE);
					System.out.println(resHotelCodes);
					WSAssert.assertEquals(resHotelCodes, hotelCodes, false);

				} else if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_GDSError", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
										"RegionalAvailabilityExtResponse_Result_GDSError", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_faultstring",
					true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
								"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	/**
	 * Method to check if the OWS Availability Regional Availability Ext is working
	 * i.e., all Hotels available for given hotel preference,timeline,no.of guests
	 * are populated correctly.
	 *
	 */
	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })

	public void regionalAvailabilityExt_41286() {

		try {
			String testName = "regionalAvailabilityExt_41286";
			WSClient.startTest(testName,
					"Verify that Correct Error code is given when Night limit for booking is exceeded.",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			// Setting OWS soap header
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);


			if(setChannelParameter("NIGHTS_ALLOWED_FOR_AVAILABILITY", "99")) {
				// Invoking targeted service
				String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_08");
				String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);

				if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
					if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
							"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", false)) {
						if(WSAssert.assertIfElementValueEquals(regionalAvailRes, "RegionalAvailabilityExtResponse_Result_OperaErrorCode", "NUMBER_NIGHTS_EXCEEDS_LIMIT", false)) {

						}
					}
				} else if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_faultstring",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
									"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	//	/**
	//	 * Method to check if the OWS Availability Regional Availability Ext is working
	//	 * i.e., all Hotels available for given hotel preference,timeline,no.of guests
	//	 * are populated correctly.
	//	 *
	//	 */
	//	@Test(groups = { "minimumRegression", "RegionalAvailabilityExt", "Availability", "OWS" })
	//
	//	public void regionalAvailabilityExt_34567() {
	//		String Parameter1 = "";
	//		try {
	//			String testName = "regionalAvailabilityExt_34567";
	//			WSClient.startTest(testName,
	//					"Verify that Correct Error code is given when room limit for booking is exceeded.",
	//					"minimumRegression");
	//
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			// Setting OWS soap header
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			WSClient.setData("{var_owsresort}", resort);
	//			WSClient.setData("{var_channel}", channel);
	//			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//
	//
	//			WSClient.setData("{var_parameter}", "PER_RESERVATION_ROOM_LIMIT");
	//            WSClient.setData("{var_settingValue}", "2");
	//            Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
	//            WSClient.writeToReport(LogStatus.INFO, "<b>RESV_MAX_ROOMS Parameter previous value "+ Parameter1  + "</b>");
	//            if (!Parameter1.equals("2")) {
	//                Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//                WSClient.writeToReport(LogStatus.INFO, "<b>RESV_MAX_ROOMS Parameter Set to value "+ Parameter1  + "</b>");
	//            }
	//
	//
	//			// Invoking targeted service
	//			String regionalAvailReq = WSClient.createSOAPMessage("OWSRegionalAvailabilityExt", "DS_08");
	//			String regionalAvailRes = WSClient.processSOAPMessage(regionalAvailReq);
	//
	//			if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_Result", true)) {
	//				if (WSAssert.assertIfElementValueEquals(regionalAvailRes,
	//						"RegionalAvailabilityExtResponse_Result_resultStatusFlag", "FAIL", false)) {
	//					if(WSAssert.assertIfElementValueEquals(regionalAvailRes, "RegionalAvailabilityExtResponse_Result_OperaErrorCode", "NUMBER_ROOMS_EXCEEDS_LIMIT", false)) {
	//
	//					}
	//				}
	//			} else if (WSAssert.assertIfElementExists(regionalAvailRes, "RegionalAvailabilityExtResponse_faultstring",
	//					true)) {
	//				WSClient.writeToReport(LogStatus.INFO,
	//						"Error generated is : " + WSClient.getElementValue(regionalAvailRes,
	//								"RegionalAvailabilityExtResponse_faultstring", XMLType.RESPONSE));
	//			}
	//
	//		} catch (Exception e) {
	//		}
	//		finally {
	//			try {
	//				WSClient.setData("{var_parameter}", "PER_RESERVATION_ROOM_LIMIT");
	//	            WSClient.setData("{var_settingValue}", Parameter1);
	//	            ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//			}
	//			catch (Exception e) {
	//				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//			}
	//		}
	//
	//	}


}
