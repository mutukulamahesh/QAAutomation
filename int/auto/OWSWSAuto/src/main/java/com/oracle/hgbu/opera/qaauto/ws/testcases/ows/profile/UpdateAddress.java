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

public class UpdateAddress extends WSSetUp {

	public String createProfileWithAddress() {

		String profileID = "";
		try {

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
			HashMap<String, String> address = new HashMap<String, String>();
			address = OPERALib.fetchAddressLOV();
			WSClient.setData("{var_city}", address.get("City"));
			WSClient.setData("{var_state}", address.get("State"));
			WSClient.setData("{var_country}", address.get("Country"));
			WSClient.setData("{var_zip}", address.get("Zip"));
			OPERALib.setOperaHeader(uname);
			profileID = CreateProfile.createProfile("DS_12");
			WSClient.setData("{var_profileID}", profileID);
			String query = WSClient.getQuery("QS_03");
			LinkedHashMap<String, String> add = WSClient.getDBRow(query);
			String addressid = add.get("ADDRESS_ID");
			WSClient.setData("{var_addressID}", addressid);
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		return profileID;
	}

	@Test(groups = { "sanity", "UpdateAddress", "OWS", "rerun" })
	public void updateAddress_38319() {

		try {
			String testName = "updateAddress_38319";
			WSClient.startTest(testName,
					"verify that if address is getting updated by providing minimal data on request(City and ZipCode).",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
				String profileID = createProfileWithAddress();
				if (profileID != "") {
					// Setting up the header variables
					String resortOperaValue = OPERALib.getResort();
					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channel = OWSLib.getChannel();
					String channelType = OWSLib.getChannelType(channel);
					String resort = OWSLib.getChannelResort(resortOperaValue, channel);
					String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

					HashMap<String, String> addressnew = new HashMap<String, String>();
					addressnew = OPERALib.fetchAddressLOV();
					WSClient.setData("{var_city}", addressnew.get("City"));
					WSClient.setData("{var_zip}", addressnew.get("Zip"));
					String country = addressnew.get("Country");
					String state = addressnew.get("State");
					WSClient.setData("{var_state}", state);
					WSClient.setData("{var_country}", country);

					// OWS update Address
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String updateAddressReq = WSClient.createSOAPMessage("OWSUpdateAddress", "DS_01");
					String updateAddressResponseXML = WSClient.processSOAPMessage(updateAddressReq);

					if (WSAssert.assertIfElementValueEquals(updateAddressResponseXML,
							"UpdateAddressResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query1 = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> db = WSClient.getDBRow(query1);
						LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();

						String addressLine = WSClient.getElementValue(updateAddressReq,
								"UpdateAddressRequest_NameAddress_AddressLine", XMLType.REQUEST);
						String city = WSClient.getElementValue(updateAddressReq,
								"UpdateAddressRequest_NameAddress_cityName", XMLType.REQUEST);
						String zip = WSClient.getElementValue(updateAddressReq,
								"UpdateAddressRequest_NameAddress_postalCode", XMLType.REQUEST);

						expected.put("ADDRESS1", addressLine);
						expected.put("CITY", city);
						expected.put("ZIP_CODE", zip);
						expected.put("COUNTRY", country);
						expected.put("STATE", state);
						WSAssert.assertEquals(expected, db, false);
					}

					// Error Codes
					if (WSAssert.assertIfElementExists(updateAddressResponseXML,
							"UpdateAddressResponse_Result_OperaErrorCode", true)) {
						String message = WSAssert.getElementValue(updateAddressResponseXML,
								"UpdateAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(updateAddressResponseXML,
							"UpdateAddressResponse_Result_GDSErrorCode", true)) {
						String message = WSAssert.getElementValue(updateAddressResponseXML,
								"UpdateAddressResponse_Result_GDSErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
					}
					if (WSAssert.assertIfElementExists(updateAddressResponseXML, "UpdateAddressResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Text Message: " + WSClient.getElementValue(updateAddressResponseXML,
										"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"************Blocked : New Profile not created**********");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Pre Requisite Failed**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	// with invalid AddressId
	@Test(groups = { "minimumRegression", "UpdateAddress", "OWS", "Name" })
	public void updateAddress_38320() {

		try {
			String testName = "updateAddress_38320";
			WSClient.startTest(testName,
					"verify FAIL result Status Flag is populated when invalid addressID is passed.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {
				// Setting up the header variables
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_zip}", "500076");
				WSClient.setData("{var_country}", "US");
				WSClient.setData("{var_city}", "FL");
				WSClient.setData("{var_state}", "Texas");

				/*****************************
				 * Invalid Address ID
				 ***********************************/
				WSClient.setData("{var_addressID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_12}"));
				// OWS update Address
				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				String updateAddressReq = WSClient.createSOAPMessage("OWSUpdateAddress", "DS_01");
				String updateAddressResponseXML = WSClient.processSOAPMessage(updateAddressReq);

				// FAIL Status validation.
				if (WSAssert.assertIfElementValueEquals(updateAddressResponseXML,
						"UpdateAddressResponse_Result_resultStatusFlag", "FAIL", false)) {

					WSClient.writeToReport(LogStatus.INFO,
							"The Address is NOT updated as Invalid AddressID was passed");
				}
				// Error Codes
				if (WSAssert.assertIfElementExists(updateAddressResponseXML,
						"UpdateAddressResponse_Result_GDSErrorCode", true)) {
					WSAssert.assertIfElementValueEquals(updateAddressResponseXML,
							"UpdateAddressResponse_Result_GDSErrorCode", "No matching record exists to update", false);
				}
				if (WSAssert.assertIfElementExists(updateAddressResponseXML, "UpdateAddressResponse_Result_Text",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Text Message: " + WSClient.getElementValue(updateAddressResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
				}
				if (WSAssert.assertIfElementExists(updateAddressResponseXML,
						"UpdateAddressResponse_Result_OperaErrorCode", true))
					WSClient.writeToReport(LogStatus.INFO,
							"<b> Opera Error Code: " + WSClient.getElementValue(updateAddressResponseXML,
									"UpdateAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE) + "</b>");

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	// with invalid AddressType
	@Test(groups = { "minimumRegression", "UpdateAddress", "OWS", "Name" })
	public void updateAddress_40310() {

		try {
			String testName = "updateAddress_40310";
			WSClient.startTest(testName,
					"verify FAIL result Status Flag is populated when Invalid Address Type is passed.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {

				String profileID = createProfileWithAddress();
				if (profileID != "") {

					// Setting up the header variables
					String resortOperaValue = OPERALib.getResort();
					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channel = OWSLib.getChannel();
					String channelType = OWSLib.getChannelType(channel);
					String resort = OWSLib.getChannelResort(resortOperaValue, channel);
					String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
					WSClient.setData("{var_zip}", "500076");
					WSClient.setData("{var_country}", "US");
					WSClient.setData("{var_city}", "FL");
					WSClient.setData("{var_state}", "Texas");

					/*****************************
					 * Invalid Address ID
					 ***********************************/
					// WSClient.setData("{var_addressId}", );
					WSClient.setData("{var_addressType}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
					// OWS update Address
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String updateAddressReq = WSClient.createSOAPMessage("OWSUpdateAddress", "DS_02");
					String updateAddressResponseXML = WSClient.processSOAPMessage(updateAddressReq);

					// FAIL Status validation.
					if (WSAssert.assertIfElementValueEquals(updateAddressResponseXML,
							"UpdateAddressResponse_Result_resultStatusFlag", "FAIL", false)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Opera Error Code: "
										+ WSClient.getElementValue(updateAddressResponseXML,
												"UpdateAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE)
										+ "</b>");

					} else {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						String add_type = db.get("ADDRESS_TYPE");

						if (add_type.equalsIgnoreCase(WSClient.getData("{var_addressType}")))
							WSClient.writeToReport(LogStatus.FAIL, "Invalid Address Type is update in the DB.");
						else
							WSClient.writeToReport(LogStatus.PASS,
									"The Address is NOT updated as Invalid AddressID was passed");

					}
					// Error Codes
					if (WSAssert.assertIfElementExists(updateAddressResponseXML,
							"UpdateAddressResponse_Result_GDSErrorCode", true)) {
						WSAssert.assertIfElementValueEquals(updateAddressResponseXML,
								"UpdateAddressResponse_Result_GDSErrorCode", "No matching record exists to update",
								false);
					}
					if (WSAssert.assertIfElementExists(updateAddressResponseXML, "UpdateAddressResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Text Message: " + WSClient.getElementValue(updateAddressResponseXML,
										"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
					if (WSAssert.assertIfElementExists(updateAddressResponseXML,
							"UpdateAddressResponse_Result_OperaErrorCode", true))
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Opera Error Code: "
										+ WSClient.getElementValue(updateAddressResponseXML,
												"UpdateAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE)
										+ "</b>");

				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateAddress", "OWS", "Name" })
	public void updateAddress_38321() {

		try {
			String testName = "updateAddress_38321";
			WSClient.startTest(testName, "verify that CITY is deleted when \"NULL\" is passed for CITY in request.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "AddressType" })) {

				String profileID = createProfileWithAddress();
				if (profileID != "") {

					// Setting up the header variables
					String resortOperaValue = OPERALib.getResort();
					String uname = OPERALib.getUserName();
					String pwd = OPERALib.getPassword();
					String channel = OWSLib.getChannel();
					String channelType = OWSLib.getChannelType(channel);
					String resort = OWSLib.getChannelResort(resortOperaValue, channel);
					String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
					WSClient.setData("{var_zip}", "500076");
					WSClient.setData("{var_country}", "US");
					WSClient.setData("{var_city}", "NULL");
					WSClient.setData("{var_state}", "Texas");

					/*****************************
					 * Invalid Address ID
					 ***********************************/
					WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
					// OWS update Address
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String updateAddressReq = WSClient.createSOAPMessage("OWSUpdateAddress", "DS_02");
					String updateAddressResponseXML = WSClient.processSOAPMessage(updateAddressReq);

					// SUCCESS Status validation.
					if (WSAssert.assertIfElementValueEquals(updateAddressResponseXML,
							"UpdateAddressResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						String query1 = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> db = WSClient.getDBRow(query1);
						System.out.println(db);
						System.out.println(db.containsKey("CITY"));
						if (!db.containsKey("CITY")) {
							WSClient.writeToReport(LogStatus.PASS, "<b>City is updated to NULL</b>");
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "<b>City is NOT updated to NULL</b>");
						}
					}
					// Error Codes
					if (WSAssert.assertIfElementExists(updateAddressResponseXML,
							"UpdateAddressResponse_Result_GDSErrorCode", true)) {
						WSAssert.assertIfElementValueEquals(updateAddressResponseXML,
								"UpdateAddressResponse_Result_GDSErrorCode", "No matching record exists to update",
								false);
					}
					if (WSAssert.assertIfElementExists(updateAddressResponseXML, "UpdateAddressResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Text Message: " + WSClient.getElementValue(updateAddressResponseXML,
										"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
					if (WSAssert.assertIfElementExists(updateAddressResponseXML,
							"UpdateAddressResponse_Result_OperaErrorCode", true))
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Opera Error Code: "
										+ WSClient.getElementValue(updateAddressResponseXML,
												"UpdateAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE)
										+ "</b>");

				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************ Blocked : Pre Requisite Failed**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

}
