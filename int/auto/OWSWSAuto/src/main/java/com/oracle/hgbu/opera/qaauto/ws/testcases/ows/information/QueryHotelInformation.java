package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.information;

import java.util.ArrayList;
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
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class QueryHotelInformation extends WSSetUp {

	boolean hotelUpdated = false;
	String resort = "";
	LinkedHashMap<String, String> generalInformation = new LinkedHashMap<>();
	HashMap<String, String> resortAddress = new HashMap<String, String>();
	//	LinkedHashMap<String, String> hotelNotes = new LinkedHashMap<String, String>();
	//	LinkedHashMap<String, String> hotelContacts = new LinkedHashMap<>();
	//	LinkedHashMap<String, String> hotelRestaurants = new LinkedHashMap<String, String>();
	//	LinkedHashMap<String, String> accommodationDetails = new LinkedHashMap<>();
	//	LinkedHashMap<String, String> communication = new LinkedHashMap<>();
	//	LinkedHashMap<String, String> address = new LinkedHashMap<>();
	//	LinkedHashMap<String, String> propertyControls = new LinkedHashMap<>();
	//	LinkedHashMap<String, String> baseDetails = new LinkedHashMap<>();
	//	LinkedHashMap<String, String> flagMap = new LinkedHashMap<>();
	//	 private String getVariableData(String keyword) throws Exception {
	//	 String dataStr = "";
	//
	//	 switch (keyword) {
	//	 case "{var_RESORT_TYPE}":
	//	 dataStr = "3STAR";
	//	 break;
	//	 case "{var_TIMEZONE_REGION}":
	//	 dataStr = "America/Los_Angeles";
	//	 break;
	//	 case "{var_CHECK_IN_TIME}":
	//	 String date =
	//	 WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_3000}");
	//	 dataStr = date;
	//	 break;
	//	 case "{var_CHECK_OUT_TIME}":
	//	 date =
	//	 WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_3000}");
	//	 dataStr = date;
	//	 break;
	//	 case "{var_PROPINFO_URL}":
	//	 dataStr = "URL info text";
	//	 break;
	//	 case "{var_LATITUDE}":
	//	 dataStr = "-40.019222";
	//	 break;
	//	 case "{var_LONGITUDE}":
	//	 dataStr = "10.527778";
	//	 break;
	//	 case "{var_CITY}":
	//	 dataStr = resortAddress.get("City");
	//	 break;
	//	 case "{var_POST_CODE}":
	//	 dataStr = resortAddress.get("Zip");
	//	 break;
	//	 case "{var_STREET}":
	//	 dataStr = "1001 Evelyn Street";
	//	 break;
	//	 case "{var_COUNTRY_CODE}":
	//	 dataStr = resortAddress.get("Country");
	//	 break;
	//	 case "{var_STATE}":
	//	 dataStr = resortAddress.get("State");
	//	 break;
	//	 case "{var_TELEPHONE}":
	//	 dataStr = "458908";
	//	 break;
	//	 case "{var_FAX}":
	//	 dataStr = "787878787";
	//	 break;
	//	 case "{var_EMAIL}":
	//	 dataStr = resort + "@property.com";
	//	 break;
	//	 case "{var_WEBADDRESS}":
	//	 dataStr = "the welcome text in web";
	//	 break;
	//	 case "{var_restaurant_RESTAURANT_NAME}":
	//	 dataStr = "Panda Express";
	//	 break;
	//	 case "{var_restaurant_COMMENTS}":
	//	 dataStr = "Chinese Restuarant";
	//	 break;
	//	 case "{var_restaurant_RESTAURANT_TYPE}":
	//	 dataStr = "POS";
	//	 break;
	//	 case "{var_restaurant_RESTAURANT_CODE}":
	//	 dataStr = "PNEXP";
	//	 break;
	//	 case "{var_restaurant_BEGIN_DATE}":
	//	 dataStr =
	//	 WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_100}");
	//	 break;
	//	 case "{var_attraction_ATTRACTION_CODE}":
	//	 dataStr = "DISNEYTP";
	//	 break;
	//	 case "{var_attraction_ORDER_BY}":
	//	 dataStr = "1";
	//	 break;
	//	 case "{var_attraction_DISTANCE}":
	//	 dataStr = "90";
	//	 break;
	//	 case "{var_attraction_DISTANCE_TYPE}":
	//	 dataStr = "KM";
	//	 break;
	//	 case "{var_attraction_GENERAL_DIRECTIONS}":
	//	 dataStr = "Walk 10 meters for main street";
	//	 break;
	//	 case "{var_attraction_ATTRACTION_TYPE}":
	//	 dataStr = "ThemePark";
	//	 break;
	//	 case "{var_attraction_PRICE_RANGE}":
	//	 dataStr = "10";
	//	 break;
	//	 case "{var_attraction_HOURS_OPERATION}":
	//	 dataStr = "3";
	//	 break;
	//	 case "{var_attraction_NAME}":
	//	 dataStr = "Disney Theme Park";
	//	 break;
	//	 case "{var_attraction_CITY}":
	//	 dataStr = resortAddress.get("City");
	//	 break;
	//	 case "{var_attraction_STATE}":
	//	 dataStr = resortAddress.get("State");
	//	 break;
	//	 case "{var_attraction_ZIP_CODE}":
	//	 dataStr = resortAddress.get("Zip");
	//	 break;
	//	 case "{var_attraction_DRIVING_TIME}":
	//	 dataStr = "10";
	//	 break;
	//	 case "{var_NOTE_CODE}":
	//	 dataStr = "GEN";
	//	 break;
	//	 case "{var_NOTE_TITLE}":
	//	 dataStr = "General Notes";
	//	 break;
	//	 case "{var_NOTES}":
	//	 dataStr = "General Notes for resort";
	//	 break;
	//	 case "{var_INTERNAL_YN}":
	//	 dataStr = "true";
	//	 break;
	//	 default:
	//	 dataStr = "";
	//	 }
	//	 return dataStr;
	//	 }
	//
	//	 private void checkAndSetVariables(LinkedHashMap<String, String> data,
	//	 String extra) throws Exception {
	//	 for (String key : data.keySet()) {
	//	 String value = data.get(key);
	//	 String mapkey = "{var_" + extra;
	//	 mapkey = mapkey + key + "}";
	//
	//	 if (StringUtils.isBlank(value))
	//	 WSClient.setData(mapkey, getVariableData(mapkey));
	//	 else
	//	 WSClient.setData(mapkey, data.get(key));
	//	 }
	//	 }
	//
	//	 private void populateGeneralInformation() throws Exception {
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_01");
	//	 LinkedHashMap<String, String> generalInfo =
	//	 WSClient.getDBRowWithNulls(query);
	//
	//	 if (generalInfo.size() != 0)
	//	 checkAndSetVariables(generalInfo, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 generalInfo.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(generalInfo, "");
	//	 }
	//	 }
	//
	//	 private void populateHotelNotes() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_09");
	//	 LinkedHashMap<String, String> notes = WSClient.getDBRowWithNulls(query);
	//	 if (notes.size() != 0)
	//	 checkAndSetVariables(notes, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 String key = keywords.nextToken().toUpperCase();
	//	 if (key.contains("AS"))
	//	 key = key.substring(key.indexOf('"') + 1, key.length() - 1);
	//	 notes.put(key, null);
	//	 }
	//	 checkAndSetVariables(notes, "");
	//	 }
	//	 }
	//
	//	 private void populateHotelContacts() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_08");
	//	 LinkedHashMap<String, String> contacts =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (contacts.size() != 0)
	//	 checkAndSetVariables(contacts, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 String key = keywords.nextToken().toUpperCase();
	//	 if (key.contains("CASE"))
	//	 key = key.substring(key.indexOf("AS ") + 3, key.length());
	//	 else if (key.contains("AS "))
	//	 key = key.substring(key.indexOf('"') + 1, key.length() - 1);
	//	 contacts.put(key, null);
	//	 }
	//	 checkAndSetVariables(contacts, "");
	//	 }
	//	 }
	//
	//	 private void populateAttractions() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_10");
	//	 LinkedHashMap<String, String> attractions =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (attractions.size() != 0)
	//	 checkAndSetVariables(attractions, "attraction_");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 attractions.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(attractions, "attraction_");
	//	 }
	//	 }
	//
	//	 private void populateHotelRestaurants() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_07");
	//	 LinkedHashMap<String, String> restaurants =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (restaurants.size() != 0)
	//	 checkAndSetVariables(restaurants, "restaurant_");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 restaurants.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(restaurants, "restaurant_");
	//	 }
	//	 }
	//
	//	 private void populateAddress() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_06");
	//	 LinkedHashMap<String, String> address =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (address.size() != 0)
	//	 checkAndSetVariables(address, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 address.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(address, "");
	//	 }
	//	 }
	//
	//	 private void populateCommunication() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_05");
	//	 LinkedHashMap<String, String> communication =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (communication.size() != 0)
	//	 checkAndSetVariables(communication, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 communication.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(communication, "");
	//	 }
	//	 }
	//
	//	 private void populatePropertyControls() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_02");
	//	 LinkedHashMap<String, String> propControls =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (propControls.size() != 0)
	//	 checkAndSetVariables(propControls, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 propControls.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(propControls, "");
	//	 }
	//	 }
	//
	//	 private void populateAccommodationDetails() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_03");
	//	 LinkedHashMap<String, String> accomodationDetails =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (accomodationDetails.size() != 0)
	//	 checkAndSetVariables(accomodationDetails, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 accomodationDetails.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(accomodationDetails, "");
	//	 }
	//
	//	 }
	//
	//	 private void populateBaseDetails() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_04");
	//	 LinkedHashMap<String, String> baseDetails =
	//	 WSClient.getDBRowWithNulls(query);
	//	 if (baseDetails.size() != 0)
	//	 checkAndSetVariables(baseDetails, "");
	//	 else {
	//	 query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
	//	 StringTokenizer keywords = new StringTokenizer(query, ",");
	//	 while (keywords.hasMoreTokens()) {
	//	 baseDetails.put(keywords.nextToken().toUpperCase(), null);
	//	 }
	//	 checkAndSetVariables(baseDetails, "");
	//	 }
	//	 }
	//
	//	 private void populateQueryDetails() throws Exception {
	//
	//	 String query = WSClient.getQuery("ChangeHotel", "QS_11");
	//	 LinkedHashMap<String, String> details =
	//	 WSClient.getDBRowWithNulls(query);
	//
	//	 for (String key : details.keySet()) {
	//	 String value = details.get(key);
	//	 String mapkey = key.toUpperCase();
	//
	//	 if (StringUtils.isBlank(value)) {
	//	 OperaPropConfig.setPropertyConfigResults(mapkey, "N");
	//	 } else {
	//	 OperaPropConfig.setPropertyConfigResults(mapkey, "Y");
	//	 }
	//	 }
	//	 for (int i = 3; i < 10; i++) {
	//	 query = WSClient.getQuery("OWSQueryHotelInformation", "QS_0" + i);
	//	 details = WSClient.getDBRow(query);
	//	 String key = "QS_0" + i;
	//	 switch (key) {
	//	 case "QS_03":
	//	 key = "OWSRoomTypes";
	//	 break;
	//	 case "QS_04":
	//	 key = "OWSNotes";
	//	 break;
	//	 case "QS_05":
	//	 key = "OWSAttractions";
	//	 break;
	//	 case "QS_06":
	//	 key = "OWSRestaurants";
	//	 break;
	//	 case "QS_07":
	//	 key = "OWSPropertyFeatures";
	//	 break;
	//	 case "QS_08":
	//	 key = "OWSRoomFeatures";
	//	 break;
	//	 case "QS_09":
	//	 key = "OWSPaymentMethods";
	//	 break;
	//	 }
	//	 if (details.size() == 0) {
	//	 OperaPropConfig.setPropertyConfigResults(key, "N");
	//	 } else {
	//	 OperaPropConfig.setPropertyConfigResults(key, "Y");
	//	 }
	//	 }
	//	 }
	//
	//	 @Test
	//	 public void populateHotel() throws Exception {
	//	 try {
	//	 resort = OPERALib.getResort();
	//	 //resort = "IDCQAP1";
	//	 resortAddress = OPERALib.fetchAddressLOV();
	//	 WSClient.setData("{var_resort}", resort);
	//	 WSClient.setData("{var_chain}", OPERALib.getChain());
	//	 WSClient.setData("{var_RESORT}", resort);
	//	 WSClient.setData("{var_CONTEXT}", "OPERA");
	//	 WSClient.setData("{var_COMMENTTYPE}", "COMMENT");
	//
	//	 WSClient.setData("{var_line1}", "8th Street West");
	//	 WSClient.setData("{var_line2}", "Latern lane");
	//	 WSClient.setData("{var_line3}", "Circle drive");
	//
	//	 String testName = "changeHotel";
	//	 WSClient.startTest(testName, "Change the hotel details", "sanity");
	//	 OPERALib.setOperaHeader(OPERALib.getUserName());
	//	 populateBaseDetails();
	//	 populateGeneralInformation();
	//	 populateAccommodationDetails();
	//	 populatePropertyControls();
	//	 populateCommunication();
	//	 populateAddress();
	//	 populateHotelRestaurants();
	//	 populateHotelContacts();
	//	 populateHotelNotes();
	//	 populateAttractions();
	//
	//	 String changeHotelReq = WSLib.createSOAPMessage("ChangeHotel", "DS_01");
	//	 System.out.println(changeHotelReq);
	//	 String changeHotelRes = WSClient.processSOAPMessage(changeHotelReq);
	//
	//	 String createResReq =
	//	 WSClient.createSOAPMessage("CreateHotelRestaurants", "DS_01");
	//	 String createResRes = WSClient.processSOAPMessage(createResReq);
	//	 String createAttTReq =
	//	 WSClient.createSOAPMessage("CreateAttractionTemplates", "DS_02");
	//	 String createAttTRes = WSClient.processSOAPMessage(createAttTReq);
	//	 String createAttReq = WSClient.createSOAPMessage("CreateAttractions",
	//	 "DS_02");
	//	 String createAttRes = WSClient.processSOAPMessage(createAttReq);
	//	 String notesReq = WSClient.createSOAPMessage("CreateHotelNotes",
	//	 "DS_01");
	//	 String notesRes = WSClient.processSOAPMessage(notesReq);
	//
	//	 WSClient.setData("{var_channel}", OWSLib.getChannel());
	//	 populateQueryDetails();
	//
	//	 } catch (Exception e) {
	//	 WSClient.writeToReport(LogStatus.ERROR, "Exception occured is :" +
	//	 e.toString());
	//	 e.printStackTrace();
	//	 }
	//	 }

	/**
	 *
	 * Method to check if the OWS Information Query Hotel Information is working
	 * i.e., fetching hotel details such as hotel code,chain code,country,name
	 * of the hotel for a given resort id.
	 *
	 * Pre-requisites: Resort configured for the channel
	 */
	@Test(groups = { "sanity", "QueryHotelInformation", "Information", "OWS" })

	public void queryHotelInformation_38602() {
		try {
			String testName = "queryHotelInformation_38602";
			WSClient.startTest(testName,
					"Verify that the basic hotel information is retrieved correctly on the response", "sanity");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSLib.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryHotelInfoReq = WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
			String queryHotelInfoRes = WSClient.processSOAPMessage(queryHotelInfoReq);

			if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					String query = WSClient.getQuery("QS_01");
					HashMap<String, String> DBResults = WSClient.getDBRow(query);
					WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
							"HotelInformationResponse_HotelInformation_HotelInformation_chainCode",
							DBResults.get("CHAIN_CODE"), false);
					WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
							"HotelInformationResponse_HotelInformation_HotelInformation", DBResults.get("NAME"), false);
					WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "Addresses_Address_countryCode",
							DBResults.get("COUNTRY_CODE"), false);

					query = WSClient.getQuery("QS_10");
					DBResults = WSClient.getDBRow(query);
					WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
							"HotelInformationResponse_HotelInformation_HotelInformation_hotelCode",
							DBResults.get("GDS_RESORT"), false);
				} else if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformationResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Error code on response is "
										+ WSClient.getElementValue(queryHotelInfoRes,
												"HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE)
										+ "</b>");
					} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Error text on response is " + WSClient.getElementValue(queryHotelInfoRes,
										"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
				}
			} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_faultcode", true)) {
				WSClient.writeToReport(LogStatus.FAIL, WSClient.getElementValue(queryHotelInfoRes,
						"HotelInformationResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * Method to check if the OWS Information Query Hotel Information is working
	 * i.e., it should fail when given an invalid resort id ( resort that is not
	 * configured for the channel ).
	 *
	 */
	@Test(groups = { "minimumRegression", "QueryHotelInformation", "Information", "OWS" })

	public void queryHotelInformation_38603() {
		try {
			String testName = "queryHotelInformation_38603";
			WSClient.startTest(testName, "Verify that hotel details are not fetched when given invalid resort id",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			WSClient.setData("{var_owsresort}", "INVALIDPROP");
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryHotelInfoReq = WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
			String queryHotelInfoRes = WSClient.processSOAPMessage(queryHotelInfoReq);

			if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_Result", true)) {

				if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformationResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Resort details are not fetched as given "
										+ WSClient.getElementValue(queryHotelInfoRes,
												"HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE)
										+ "</b>");
					} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Resort details are not fetched as given " + WSClient.getElementValue(
										queryHotelInfoRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}
				} else if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.writeToReport(LogStatus.FAIL,
							"Resort details are fetched when given an invalid resort id");
				}
			} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_faultcode", true)) {
				WSClient.writeToReport(LogStatus.FAIL, WSClient.getElementValue(queryHotelInfoRes,
						"HotelInformationResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * Method to check if the OWS Information Query Hotel Information is working
	 * i.e., fetching hotel details such as such as
	 * addresses,email's,phones,URI's are retrieved correctly for a given resort
	 * id.
	 *
	 * Pre-requisites: Resort configured for the channel
	 */
	@Test(groups = { "minimumRegression", "QueryHotelInformation", "Information", "OWS" })

	public void queryHotelInformation_38610() {
		try {
			String testName = "queryHotelInformation_38610";
			WSClient.startTest(testName,
					"Verify that hotel details such as addresses,emails,phones,URI's are retrieved correctly",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSLib.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryHotelInfoReq = WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
			String queryHotelInfoRes = WSClient.processSOAPMessage(queryHotelInfoReq);

			if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					String query = WSClient.getQuery("QS_01");
					HashMap<String, String> DBResults = WSClient.getDBRow(query);

					WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "Addresses_Address_AddressLine",
							DBResults.get("STREET"), false);
					WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "Addresses_Address_countryCode",
							DBResults.get("COUNTRY_CODE"), false);
					WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "Addresses_Address_stateProv",
							DBResults.get("STATE"), false);
					WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "Addresses_Address_cityName",
							DBResults.get("CITY"), false);
					if(WSAssert.assertIfElementExists(queryHotelInfoRes, "Addresses_Address_postalCode", true)) {
						WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "Addresses_Address_postalCode",
								DBResults.get("POST_CODE"), false);
					}

					/*************
					 * If contact email's exists then validate it else do not
					 * validate
					 **********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformation_HotelContactInformation_ContactEmails", true)) {
						WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
								"HotelContactInformation_ContactEmails_ContactEmail", DBResults.get("EMAIL"), false);
					}

					/*************
					 * If contact phones exists then validate it else do not
					 * validate
					 **********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformation_HotelContactInformation_ContactPhones", true)) {
						WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "ContactPhones_Phone_PhoneNumber",
								DBResults.get("TELEPHONE"), false);
						WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "ContactPhones_Phone_PhoneNumber_Fax",
								DBResults.get("FAX"), false);
					}

					/*************
					 * If URI's exists then validate it else do not validate
					 **********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformation_HotelContactInformation_URIs", true)) {
						WSAssert.assertIfElementValueEquals(queryHotelInfoRes, "HotelContactInformation_URIs_URI",
								DBResults.get("PROPINFO_URL"), false);
					}
				} else if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformationResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"The error code that is generated is " + WSClient.getElementValue(queryHotelInfoRes,
										"HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.FAIL, "The error text that is generated is " + WSClient
								.getElementValue(queryHotelInfoRes, "Result_Text_TextElement", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_faultcode", true)) {
				WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated is " + WSClient
						.getElementValue(queryHotelInfoRes, "HotelInformationResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * Method to check if the OWS Information Query Hotel Information is working
	 * i.e., fetching hotel details such as payment methods,web
	 * address,latitude,longitude,notes etc .
	 *
	 * Pre-requisites: Resort configured for the channel
	 */
	@Test(groups = { "minimumRegression", "QueryHotelInformation", "Information", "OWS" })

	public void queryHotelInformation_38620() {
		try {
			String testName = "queryHotelInformation_38620";
			WSClient.startTest(testName, "Verify that hotel extended information is retreived correctly",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSLib.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryHotelInfoReq = WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
			String queryHotelInfoRes = WSClient.processSOAPMessage(queryHotelInfoReq);

			if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					String query = WSClient.getQuery("QS_01");
					HashMap<String, String> DBResults = WSClient.getDBRow(query);

					/*************
					 * If position exists then validate it else do not validate
					 **********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformation_HotelExtendedInformation_Position", true)) {
						if (WSAssert.assertIfElementExists(queryHotelInfoRes,
								"HotelInformation_HotelExtendedInformation_Position_latitude", true)) {
							WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
									"HotelInformation_HotelExtendedInformation_Position_latitude",
									DBResults.get("LATITUDE"), false);
						}
						if (WSAssert.assertIfElementExists(queryHotelInfoRes,
								"HotelInformation_HotelExtendedInformation_Position_longitude", true)) {
							WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
									"HotelInformation_HotelExtendedInformation_Position_longitude",
									DBResults.get("LONGITUDE"), false);
						}
					}

					/*************
					 * If Guest rooms exists then validate it else do not
					 * validate
					 **********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelExtendedInformation_FacilityInfo_GuestRooms", true)) {
						if (WSAssert.assertIfElementExists(queryHotelInfoRes,
								"HotelExtendedInformation_FacilityInfo_GuestRooms_totalRooms", true)) {
							WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
									"HotelExtendedInformation_FacilityInfo_GuestRooms_totalRooms",
									DBResults.get("TOT_ROOMS"), false);
						}
						if (WSAssert.assertIfElementExists(queryHotelInfoRes, "FacilityInfo_GuestRooms_GuestRoom",
								true)) {
							HashMap<String, String> xPath = new HashMap<>();
							xPath.put("FacilityInfo_GuestRooms_GuestRoom_code", "FacilityInfo_GuestRooms_GuestRoom");
							xPath.put("RoomDescription_Text_TextElement", "FacilityInfo_GuestRooms_GuestRoom");

							query = WSClient.getQuery("QS_03");
							ArrayList<LinkedHashMap<String, String>> roomCategories = WSClient.getDBRows(query);
							List<LinkedHashMap<String, String>> resValues = WSAssert
									.getMultipleNodeList(queryHotelInfoRes, xPath, false, XMLType.RESPONSE);
							WSAssert.assertEquals(resValues, roomCategories, false);

						}
					}

					/**************
					 * Verify the payment methods
					 ***********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformation_HotelExtendedInformation_PaymentMethods", true)) {
						HashMap<String, String> xPath = new HashMap<>();
						xPath.put("PaymentMethods_PaymentType_OtherPayment_value",
								"HotelExtendedInformation_PaymentMethods_PaymentType");

						query = WSClient.getQuery("QS_09");
						ArrayList<LinkedHashMap<String, String>> payments = WSClient.getDBRows(query);
						List<LinkedHashMap<String, String>> resValues = WSAssert.getMultipleNodeList(queryHotelInfoRes,
								xPath, false, XMLType.RESPONSE);
						System.out.println(resValues);
						WSAssert.assertEquals(resValues, payments, false);
					}

					/*******
					 * Validate info such as Web address,resort type,notes,check
					 * in time,check out time,time zone region
					 *********/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformation_HotelExtendedInformation_HotelInformation", true)) {
						HashMap<String, String> xPath = new HashMap<>();
						xPath.put("HotelExtendedInformation_HotelInformation_HotelInfo_hotelInfoType",
								"HotelExtendedInformation_HotelInformation_HotelInfo");
						xPath.put("HotelExtendedInformation_HotelInformation_HotelInfo_otherHotelInfoType",
								"HotelExtendedInformation_HotelInformation_HotelInfo");
						xPath.put("HotelInfo_Text_TextElement_2",
								"HotelExtendedInformation_HotelInformation_HotelInfo");
						xPath.put("HotelInformation_HotelInfo_Url_2",
								"HotelExtendedInformation_HotelInformation_HotelInfo");

						List<LinkedHashMap<String, String>> resValues = WSAssert.getMultipleNodeList(queryHotelInfoRes,
								xPath, false, XMLType.RESPONSE);

						for (int index = 0; index < resValues.size(); index++) {
							// WSClient.writeToReport(LogStatus.INFO, "Loop
							// index : "+index);
							HashMap<String, String> hotelInfo = resValues.get(index);
							if (hotelInfo.containsValue("PROPERTY_WEBADDRESS")) {
								if (WSAssert.assertEquals(DBResults.get("WEBADDRESS"), hotelInfo.get("Url1"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "WEBADDRESS -  Expected : "
											+ DBResults.get("WEBADDRESS") + "   Actual : " + hotelInfo.get("Url1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "WEBADDRESS -  Expected : "
											+ DBResults.get("WEBADDRESS") + "   Actual : " + hotelInfo.get("Url1"));
								}
							}
							if (hotelInfo.containsValue("GRADE")) {
								if (WSAssert.assertEquals(DBResults.get("RESORT_TYPE"),
										hotelInfo.get("TextTextElement1"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"RESORT TYPE -  Expected : " + DBResults.get("RESORT_TYPE") + "   Actual : "
													+ hotelInfo.get("TextTextElement1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"RESORT TYPE -  Expected : " + DBResults.get("RESORT_TYPE") + "   Actual : "
													+ hotelInfo.get("TextTextElement1"));
								}
							}
							if (hotelInfo.containsValue("CHECKININFO")) {
								String checkin = DBResults.get("CHECK_IN_TIME");
								if (WSAssert.assertEquals(
										checkin.substring(checkin.indexOf(" ") + 1, checkin.lastIndexOf(':')),
										hotelInfo.get("TextTextElement1"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"CHECK-IN TIME -  Expected : "
													+ checkin.substring(checkin.indexOf(" ") + 1,
															checkin.lastIndexOf(':'))
													+ "   Actual : " + hotelInfo.get("TextTextElement1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"CHECK-IN TIME -  Expected : "
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
											"CHECK-OUT TIME -  Expected : "
													+ checkout.substring(checkout.indexOf(" ") + 1,
															checkout.lastIndexOf(':'))
													+ "   Actual : " + hotelInfo.get("TextTextElement1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"CHECK-OUT TIME -  Expected : "
													+ checkout.substring(checkout.indexOf(" ") + 1,
															checkout.lastIndexOf(':'))
													+ "   Actual : " + hotelInfo.get("TextTextElement1"));
								}
							}
							if (hotelInfo.containsValue("TextTextElement1")) {
								if (WSAssert.assertEquals(DBResults.get("TIMEZONE_REGION"),
										hotelInfo.get("TextTextElement1"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"TIMEZONE REGION -  Expected : " + DBResults.get("TIMEZONE_REGION")
											+ "   Actual : " + hotelInfo.get("TextTextElement1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"TIMEZONE REGION -  Expected : " + DBResults.get("TIMEZONE_REGION")
											+ "   Actual : " + hotelInfo.get("TextTextElement1"));
								}
							}
							if (!hotelInfo.containsValue("PROPERTY_WEBADDRESS") && !hotelInfo.containsValue("GRADE")
									&& !hotelInfo.containsValue("CHECKOUTINFO")
									&& !hotelInfo.containsValue("CHECKININFO")
									&& !hotelInfo.containsValue("PROPERTY_TIMEZONE")
									&& hotelInfo.containsValue("OTHER")) {
								WSClient.setData("{var_title}", hotelInfo.get("otherHotelInfoType1"));
								System.out.println(hotelInfo);
								query = WSClient.getQuery("QS_04");
								LinkedHashMap<String, String> resortNotes = WSClient.getDBRow(query);
								System.out.println(resortNotes);
								if (WSAssert.assertEquals(resortNotes.get("NOTE_TITLE"),
										hotelInfo.get("otherHotelInfoType1"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"NOTE TITLE -  Expected : " + resortNotes.get("NOTE_TITLE") + "   Actual : "
													+ hotelInfo.get("otherHotelInfoType1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"NOTE TITLE -  Expected : " + resortNotes.get("NOTE_TITLE") + "   Actual : "
													+ hotelInfo.get("otherHotelInfoType1"));

								}
								if (WSAssert.assertEquals(resortNotes.get("NOTES"), hotelInfo.get("TextTextElement1"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"NOTES -  Expected : " + resortNotes.get("NOTES") + "   Actual : "
													+ hotelInfo.get("TextTextElement1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"NOTES -  Expected : " + resortNotes.get("NOTES") + "   Actual : "
													+ hotelInfo.get("TextTextElement1"));
								}
							}
						}

					}
				} else if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformationResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"The error code that is generated is " + WSClient.getElementValue(queryHotelInfoRes,
										"HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.FAIL, "The error text that is generated is " + WSClient
								.getElementValue(queryHotelInfoRes, "Result_Text_TextElement", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_faultcode", true)) {
				WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated is " + WSClient
						.getElementValue(queryHotelInfoRes, "HotelInformationResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// /**
	// * Method to check if the OWS Information Query Hotel Information is
	// working i.e., fetching
	// * hotel details such as such as alternate resorts are retrieved correctly
	// * for a given resort id.
	// *
	// * Pre-requisites: Resort configured for the channel
	// */
	// @Test(groups = { "targetedRegression", "QueryHotelInformation",
	// "Information", "OWS" })
	//
	// public void queryHotelInformation_38621()
	// {
	// try {
	// String testName = "queryHotelInformation_38621";
	// WSClient.startTest(testName, "Verify that hotel's alternate resorts
	// information is retrieved correctly", "targetedRegression");
	//
	// String resortOperaValue=OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// WSLib.setData("{var_channel}", channel);
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//
	// String queryHotelInfoReq =
	// WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
	// String queryHotelInfoRes =
	// WSClient.processSOAPMessage(queryHotelInfoReq);
	//
	// if(WSAssert.assertIfElementExists(queryHotelInfoRes,
	// "HotelInformationResponse_Result", true))
	// {
	// if(WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
	// "HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", true))
	// {
	// /************* Validate alternate resorts **********************/
	//
	// if(WSAssert.assertIfElementExists(queryHotelInfoRes,
	// "HotelInformation_HotelExtendedInformation_AlternateProperties", true))
	// {
	// HashMap<String, String> xPath=new HashMap<>();
	// xPath.put("HotelExtendedInformation_AlternateProperties_HotelReference",
	// "HotelExtendedInformation_AlternateProperties_HotelReference");
	// xPath.put("HotelExtendedInformation_AlternateProperties_HotelReference_chainCode",
	// "HotelExtendedInformation_AlternateProperties_HotelReference");
	// xPath.put("HotelExtendedInformation_AlternateProperties_HotelReference_hotelCode",
	// "HotelExtendedInformation_AlternateProperties_HotelReference");
	//
	// String query=WSClient.getQuery("QS_02");
	// ArrayList<LinkedHashMap<String, String>>
	// alternateProp=WSClient.getDBRows(query);
	// List<LinkedHashMap<String, String>>
	// resValues=WSAssert.getMultipleNodeList(queryHotelInfoRes, xPath, false,
	// XMLType.RESPONSE);
	// WSAssert.assertEquals(resValues, alternateProp, false);
	//
	// }
	// }
	// else if(WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
	// "HotelInformationResponse_Result_resultStatusFlag", "FAIL", true))
	// {
	// if(WSAssert.assertIfElementExists(queryHotelInfoRes,
	// "HotelInformationResponse_Result_OperaErrorCode", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated
	// is "+WSClient.getElementValue(queryHotelInfoRes,
	// "HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE));
	// }
	// else if(WSAssert.assertIfElementExists(queryHotelInfoRes,
	// "Result_Text_TextElement", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated
	// is "+WSClient.getElementValue(queryHotelInfoRes,
	// "Result_Text_TextElement", XMLType.RESPONSE));
	// }
	// }
	// }
	// else if(WSAssert.assertIfElementExists(queryHotelInfoRes,
	// "HotelInformationResponse_faultcode", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated
	// is "+WSClient.getElementValue(queryHotelInfoRes,
	// "HotelInformationResponse_faultstring", XMLType.RESPONSE));
	// }
	//
	// }catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// }
	//
	/**
	 * Method to check if the OWS Information Query Hotel Information is working
	 * i.e., fetching hotel details such as such as attractions are retrieved
	 * correctly for a given resort id.
	 *
	 * Pre-requisites: Resort configured for the channel
	 */
	@Test(groups = { "minimumRegression", "QueryHotelInformation", "Information", "OWS" })

	public void queryHotelInformation_38622() {
		try {
			String testName = "queryHotelInformation_38622";
			WSClient.startTest(testName, "Verify that attractions of the hotel are retrieved correctly",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSLib.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryHotelInfoReq = WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
			String queryHotelInfoRes = WSClient.processSOAPMessage(queryHotelInfoReq);

			if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					/*************
					 * If attractions exists then validate it else do not
					 * validate
					 **********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes, "FacilityInfo_Attractions_Attraction",
							true)) {
						HashMap<String, String> xPath = new HashMap<>();
						xPath.put("Attractions_Attraction_AttractionCode", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_AttractionName", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_AttractionType", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_Distance", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_DistanceType", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_DrivingTime", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_City", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_State", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_ZipCode", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_HoursOfOperation", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_Latitude", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_Longitude", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_PriceRange", "FacilityInfo_Attractions_Attraction");
						xPath.put("Attractions_Attraction_DisplaySequence", "FacilityInfo_Attractions_Attraction");

						String query = WSClient.getQuery("QS_05");
						ArrayList<LinkedHashMap<String, String>> attractions = WSClient.getDBRows(query);
						List<LinkedHashMap<String, String>> resValues = WSAssert.getMultipleNodeList(queryHotelInfoRes,
								xPath, false, XMLType.RESPONSE);
						WSAssert.assertEquals(resValues, attractions, false);

					}
				} else if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformationResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"The error code that is generated is " + WSClient.getElementValue(queryHotelInfoRes,
										"HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.FAIL, "The error text that is generated is " + WSClient
								.getElementValue(queryHotelInfoRes, "Result_Text_TextElement", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_faultcode", true)) {
				WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated is " + WSClient
						.getElementValue(queryHotelInfoRes, "HotelInformationResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * Method to check if the OWS Information Query Hotel Information is working
	 * i.e., fetching hotel details such as such as restaurants are retrieved
	 * correctly for a given resort id.
	 *
	 * Pre-requisites: Resort configured for the channel
	 */
	@Test(groups = { "minimumRegression", "QueryHotelInformation", "Information", "OWS" })

	public void queryHotelInformation_38921() {
		try {
			String testName = "queryHotelInformation_38921";
			WSClient.startTest(testName, "Verify that hotel's restaurants information is retrieved correctly",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSLib.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryHotelInfoReq = WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
			String queryHotelInfoRes = WSClient.processSOAPMessage(queryHotelInfoReq);

			if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					/************* Validate restaurants **********************/

					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelExtendedInformation_FacilityInfo_Restaurants", true)) {
						HashMap<String, String> xPath = new HashMap<>();
						xPath.put("FacilityInfo_Restaurants_Restaurant_RestaurantName",
								"FacilityInfo_Restaurants_Restaurant");
						xPath.put("Restaurant_RestaurantDescription_Text", "FacilityInfo_Restaurants_Restaurant");
						xPath.put("Restaurant_Cuisines_Cuisine_Code", "FacilityInfo_Restaurants_Restaurant");
						xPath.put("Restaurant_Cuisines_Cuisine_Description", "FacilityInfo_Restaurants_Restaurant");

						String query = WSClient.getQuery("QS_06");
						ArrayList<LinkedHashMap<String, String>> restaurants = WSClient.getDBRows(query);
						List<LinkedHashMap<String, String>> resValues = WSAssert.getMultipleNodeList(queryHotelInfoRes,
								xPath, false, XMLType.RESPONSE);
						WSAssert.assertEquals(resValues, restaurants, false);

					}
				} else if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformationResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"The error code that is generated is " + WSClient.getElementValue(queryHotelInfoRes,
										"HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.FAIL, "The error text that is generated is " + WSClient
								.getElementValue(queryHotelInfoRes, "Result_Text_TextElement", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_faultcode", true)) {
				WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated is " + WSClient
						.getElementValue(queryHotelInfoRes, "HotelInformationResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * Method to check if the OWS Information Query Hotel Information is working
	 * i.e., fetching hotel amenities are retrieved correctly for a given resort
	 * id.
	 *
	 * Pre-requisites: Resort configured for the channel
	 */
	@Test(groups = { "minimumRegression", "QueryHotelInformation", "Information", "OWS" })

	public void queryHotelInformation_38720() {
		try {
			String testName = "queryHotelInformation_38720";
			WSClient.startTest(testName, "Verify that hotel amenties are retreived correctly", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSLib.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryHotelInfoReq = WSClient.createSOAPMessage("OWSQueryHotelInformation", "DS_01");
			String queryHotelInfoRes = WSClient.processSOAPMessage(queryHotelInfoReq);

			if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_Result", true)) {
				if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes, "AmenityInfo_Amenities_Amenity", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Property features</b>");
						HashMap<String, String> xPath = new HashMap<>();
						xPath.put("AmenityInfo_Amenities_Amenity_amenityCode", "AmenityInfo_Amenities_Amenity");
						xPath.put("Amenities_Amenity_amenityDescription", "AmenityInfo_Amenities_Amenity");
						xPath.put("AmenityInfo_Amenities_Amenity_amenityType", "AmenityInfo_Amenities_Amenity");

						String query = WSClient.getQuery("QS_07");
						ArrayList<LinkedHashMap<String, String>> amenities = WSClient.getDBRows(query);
						List<LinkedHashMap<String, String>> resValues = WSAssert.getMultipleNodeList(queryHotelInfoRes,
								xPath, false, XMLType.RESPONSE);
						WSAssert.assertEquals(resValues, amenities, false);
					}
					if (WSAssert.assertIfElementExists(queryHotelInfoRes, "GuestRooms_GuestRoom_AmenityInfo", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room features</b>");
						HashMap<String, String> xPath = new HashMap<>();
						xPath.put("AmenityInfo_Amenities_Amenity_amenityCode_2", "FacilityInfo_GuestRooms_GuestRoom");
						xPath.put("Amenities_Amenity_amenityDescription_2", "FacilityInfo_GuestRooms_GuestRoom");
						xPath.put("AmenityInfo_Amenities_Amenity_amenityType_2", "FacilityInfo_GuestRooms_GuestRoom");
						xPath.put("FacilityInfo_GuestRooms_GuestRoom_code", "FacilityInfo_GuestRooms_GuestRoom");

						String query = WSClient.getQuery("QS_08");
						ArrayList<LinkedHashMap<String, String>> amenities = WSClient.getDBRows(query);
						List<LinkedHashMap<String, String>> resValues = WSAssert.getMultipleNodeList(queryHotelInfoRes,
								xPath, true, XMLType.RESPONSE);
						WSAssert.assertEquals(resValues, amenities, false);
					}
				} else if (WSAssert.assertIfElementValueEquals(queryHotelInfoRes,
						"HotelInformationResponse_Result_resultStatusFlag", "FAIL", true)) {
					if (WSAssert.assertIfElementExists(queryHotelInfoRes,
							"HotelInformationResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"The error code that is generated is " + WSClient.getElementValue(queryHotelInfoRes,
										"HotelInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.FAIL, "The error text that is generated is " + WSClient
								.getElementValue(queryHotelInfoRes, "Result_Text_TextElement", XMLType.RESPONSE));
					}
				}
			} else if (WSAssert.assertIfElementExists(queryHotelInfoRes, "HotelInformationResponse_faultcode", true)) {
				WSClient.writeToReport(LogStatus.FAIL, "The error code that is generated is " + WSClient
						.getElementValue(queryHotelInfoRes, "HotelInformationResponse_faultstring", XMLType.RESPONSE));
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

}
