package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;


public class PopulatePropertyAddlDetails extends WSSetUp{

	String resort;
	//String channel=OWSLib.getChannel();
	boolean hotelUpdated = false;
	LinkedHashMap<String, String> generalInformation = new LinkedHashMap<>();
	HashMap<String, String> resortAddress = new HashMap<String, String>();
	LinkedHashMap<String, String> hotelNotes = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> hotelContacts = new LinkedHashMap<>();
	LinkedHashMap<String, String> hotelRestaurants = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> accommodationDetails = new LinkedHashMap<>();
	LinkedHashMap<String, String> communication = new LinkedHashMap<>();
	LinkedHashMap<String, String> address = new LinkedHashMap<>();
	LinkedHashMap<String, String> propertyControls = new LinkedHashMap<>();
	LinkedHashMap<String, String> baseDetails = new LinkedHashMap<>();
	LinkedHashMap<String, String> flagMap = new LinkedHashMap<>();

	private String getVariableData(String keyword) throws Exception {
		String dataStr = "";

		switch (keyword)
		{
		case "{var_RESORT_TYPE}": dataStr = "3STAR";
		break;
		case "{var_TIMEZONE_REGION}": dataStr = "America/Los_Angeles";
		break;
		case "{var_CHECK_IN_TIME}": String date = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_3000}");
		dataStr = date;
		break;
		case "{var_CHECK_OUT_TIME}": date = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_3000}");
		dataStr = date;
		break;
		case "{var_PROPINFO_URL}": dataStr = "URL info text";
		break;
		case "{var_LATITUDE}": dataStr = "-40.019222";
		break;
		case "{var_LONGITUDE}": dataStr = "10.527778";
		break;
		case "{var_CITY}":	 dataStr = resortAddress.get("City");
		break;
		case "{var_POST_CODE}": dataStr = resortAddress.get("Zip");
		break;
		case "{var_STREET}": dataStr = "1001 Evelyn Street";
		break;
		case "{var_COUNTRY_CODE}": dataStr = resortAddress.get("Country");
		break;
		case "{var_STATE}": dataStr = resortAddress.get("State");
		break;
		case "{var_TELEPHONE}": dataStr = "458908";
		break;
		case "{var_FAX}": dataStr = "787878787";
		break;
		case "{var_EMAIL}": dataStr = resort + "@property.com";
		break;
		case "{var_WEBADDRESS}": dataStr = "the welcome text in web";
		break;
		case "{var_restaurant_RESTAURANT_NAME}":dataStr = "Panda Express";
		break;
		case "{var_restaurant_COMMENTS}": dataStr = "Chinese Restuarant";
		break;
		case "{var_restaurant_RESTAURANT_TYPE}": dataStr = "POS";
		break;
		case "{var_restaurant_RESTAURANT_CODE}": dataStr = "PNEXP";
		break;
		case "{var_restaurant_BEGIN_DATE}": dataStr = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_100}");
		break;
		case "{var_restaurant_END_DATE}":dataStr= WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_8900}");
		break;
		case "{var_attraction_ATTRACTION_CODE}": dataStr = "DISNEYTP";
		break;
		case "{var_attraction_ORDER_BY}": dataStr = "1";
		break;
		case "{var_attraction_DISTANCE}": dataStr = "90";
		break;
		case "{var_attraction_DISTANCE_TYPE}": dataStr = "KM";
		break;
		case "{var_attraction_GENERAL_DIRECTIONS}": dataStr = "Walk 10 meters for main street";
		break;
		case "{var_attraction_ATTRACTION_TYPE}": dataStr = "ThemePark";
		break;
		case "{var_attraction_PRICE_RANGE}": dataStr = "10";
		break;
		case "{var_attraction_HOURS_OPERATION}": dataStr = "3";
		break;
		case "{var_attraction_NAME}": dataStr = "Disney Theme Park";
		break;
		case "{var_attraction_CITY}": dataStr = resortAddress.get("City");
		break;
		case "{var_attraction_STATE}":	 dataStr = resortAddress.get("State");
		break;
		case "{var_attraction_ZIP_CODE}": dataStr = resortAddress.get("Zip");
		break;
		case "{var_attraction_DRIVING_TIME}": dataStr = "10";
		break;
		case "{var_NOTE_CODE}": dataStr = "GEN";
		break;
		case "{var_NOTE_TITLE}": dataStr = "General Notes";
		break;
		case "{var_NOTES}": dataStr = "General Notes for resort";
		break;
		case "{var_INTERNAL_YN}": dataStr = "true";
		break;
		default: dataStr = "";
		}
		return dataStr;
	}


	private void checkAndSetVariables(LinkedHashMap<String, String> data, String extra) throws Exception
	{
		for (String key : data.keySet())
		{
			String value = data.get(key);
			String mapkey = "{var_" + extra;
			mapkey = mapkey + key + "}";

			if (StringUtils.isBlank(value))
				WSClient.setData(mapkey, getVariableData(mapkey));
			else
				WSClient.setData(mapkey, data.get(key));
		}
	}

	private void populateGeneralInformation() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_01");
		LinkedHashMap<String, String> generalInfo =
				WSClient.getDBRowWithNulls(query);

		if (generalInfo.size() != 0)
			checkAndSetVariables(generalInfo, "");
		else {
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens())
			{
				generalInfo.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(generalInfo, "");
		}
	}

	private void populateHotelNotes() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_09");
		LinkedHashMap<String, String> notes = WSClient.getDBRowWithNulls(query);
		if (notes.size() != 0)
			checkAndSetVariables(notes, "");
		else {
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens()) {
				String key = keywords.nextToken().toUpperCase();
				if (key.contains("AS"))
					key = key.substring(key.indexOf('"') + 1, key.length() - 1);
				notes.put(key, null);
			}
			checkAndSetVariables(notes, "");
		}
	}

	private void populateHotelContacts() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_08");
		LinkedHashMap<String, String> contacts =
				WSClient.getDBRowWithNulls(query);
		if (contacts.size() != 0)
			checkAndSetVariables(contacts, "");
		else {
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens()) {
				String key = keywords.nextToken().toUpperCase();
				if (key.contains("CASE"))
					key = key.substring(key.indexOf("AS ") + 3, key.length());
				else if (key.contains("AS "))
					key = key.substring(key.indexOf('"') + 1, key.length() - 1);
				contacts.put(key, null);
			}
			checkAndSetVariables(contacts, "");
		}
	}

	private void populateAttractions() throws Exception
	{

		String query = WSClient.getQuery("ChangeHotel", "QS_10");
		LinkedHashMap<String, String> attractions =
				WSClient.getDBRowWithNulls(query);
		if (attractions.size() != 0)
			checkAndSetVariables(attractions, "attraction_");
		else {
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens())
			{
				attractions.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(attractions, "attraction_");
		}
	}

	private void populateHotelRestaurants() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_07");
		LinkedHashMap<String, String> restaurants = WSClient.getDBRowWithNulls(query);
		if (restaurants.size() != 0)
			checkAndSetVariables(restaurants, "restaurant_");
		else
		{
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens())
			{
				restaurants.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(restaurants, "restaurant_");
		}
	}

	private void populateAddress() throws Exception
	{

		String query = WSClient.getQuery("ChangeHotel", "QS_06");
		LinkedHashMap<String, String> address = WSClient.getDBRowWithNulls(query);
		if (address.size() != 0)
			checkAndSetVariables(address, "");
		else
		{
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens())
			{
				address.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(address, "");
		}
	}

	private void populateCommunication() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_05");
		LinkedHashMap<String, String> communication =
				WSClient.getDBRowWithNulls(query);
		if (communication.size() != 0)
			checkAndSetVariables(communication, "");
		else
		{
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens())
			{
				communication.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(communication, "");
		}
	}

	private void populatePropertyControls() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_02");
		LinkedHashMap<String, String> propControls =
				WSClient.getDBRowWithNulls(query);
		if (propControls.size() != 0)
			checkAndSetVariables(propControls, "");
		else
		{
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens()) {
				propControls.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(propControls, "");
		}
	}

	private void populateAccommodationDetails() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_03");
		LinkedHashMap<String, String> accomodationDetails =
				WSClient.getDBRowWithNulls(query);
		if (accomodationDetails.size() != 0)
			checkAndSetVariables(accomodationDetails, "");
		else
		{
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens()) {
				accomodationDetails.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(accomodationDetails, "");
		}

	}

	private void populateBaseDetails() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_04");
		LinkedHashMap<String, String> baseDetails =
				WSClient.getDBRowWithNulls(query);
		if (baseDetails.size() != 0)
			checkAndSetVariables(baseDetails, "");
		else
		{
			query = query.substring(query.indexOf("SELECT ") + 7, query.indexOf("FROM"));
			StringTokenizer keywords = new StringTokenizer(query, ",");
			while (keywords.hasMoreTokens()) {
				baseDetails.put(keywords.nextToken().toUpperCase(), null);
			}
			checkAndSetVariables(baseDetails, "");
		}
	}

	private void populateQueryDetails() throws Exception
	{
		String query = WSClient.getQuery("ChangeHotel", "QS_11");
		LinkedHashMap<String, String> details =
				WSClient.getDBRowWithNulls(query);

		for (String key : details.keySet()) {
			String value = details.get(key);
			String mapkey = key.toUpperCase();

			if (StringUtils.isBlank(value)) {
				OperaPropConfig.setPropertyConfigResults(mapkey, "N");
			}
			else {
				OperaPropConfig.setPropertyConfigResults(mapkey, "Y");
			}
		}
		for (int i = 3; i < 10; i++)
		{
			query = WSClient.getQuery("OWSQueryHotelInformation", "QS_0" + i);
			details = WSClient.getDBRow(query);
			String key = "QS_0" + i;
			switch (key)
			{
			case "QS_03": key = "OWSRoomTypes";
			break;
			case "QS_04": key = "OWSNotes";
			break;
			case "QS_05": key = "OWSAttractions";
			break;
			case "QS_06": key = "OWSRestaurants";
			break;
			case "QS_07": key = "OWSPropertyFeatures";
			break;
			case "QS_08": key = "OWSRoomFeatures";
			break;
			case "QS_09": key = "OWSPaymentMethods";
			break;
			}

			if (details.size() == 0)
			{
				OperaPropConfig.setPropertyConfigResults(key, "N");
			}
			else
			{
				OperaPropConfig.setPropertyConfigResults(key, "Y");
			}
		}
	}

	@Test(groups= {"OWS"}, dependsOnGroups ={"createChannel"})
	public void populateHotelDetails()
	{
		String testName = "ConfigureProperty";
		WSClient.startTest(testName, "Configure the resort with more details","OWS");
		boolean flag = true;
		resort=OPERALib.getResort();
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);

		try {
			WSClient.setData("{var_Resort}",resort);
			System.out.println(resort);
			WSClient.setData("{var_Chain}", OPERALib.getChain());
			resortAddress = OPERALib.fetchAddressLOV();
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_RESORT}", resort);
			WSClient.setData("{var_CONTEXT}", "OPERA");
			WSClient.setData("{var_COMMENTTYPE}", "COMMENT");

			WSClient.setData("{var_line1}", "8th Street West");
			WSClient.setData("{var_line2}", "Latern lane");
			WSClient.setData("{var_line3}", "Circle drive");

			populateBaseDetails();
			populateGeneralInformation();
			populateAccommodationDetails();
			populatePropertyControls();
			populateCommunication();
			populateAddress();
			populateHotelRestaurants();
			populateHotelContacts();
			populateHotelNotes();
			populateAttractions();
			WSClient.setData("{var_RESORT}", resort);
			System.out.println(WSClient.getData("{var_RESORT}"));
			String changeHotelReq = WSLib.createSOAPMessage("ChangeHotel", "DS_01");
			System.out.println(changeHotelReq);
			String changeHotelRes = WSClient.processSOAPMessage(changeHotelReq);
			if(WSAssert.assertIfElementExists(changeHotelRes, "ChangeHotelRS_Success", false))
			{
				WSClient.setData("{var_restaurant_BEGIN_DATE}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_100}"));
				WSClient.setData("{var_restaurant_END_DATE}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_8900}"));
				WSClient.setData("{var_restaurant_TYPE}","POS");
				String createResReq = WSClient.createSOAPMessage("CreateHotelRestaurants", "DS_01");
				String createResRes = WSClient.processSOAPMessage(createResReq);
				String errorText=WSClient.getElementValue(createResRes, "CreateHotelRestaurantsRS_Errors_Error", XMLType.RESPONSE);
				if(errorText.contains("already exists"))
				{
					WSClient.writeToReport(LogStatus.PASS, "Restaurants Already exists");
				}
				else if(WSAssert.assertIfElementExists(createResRes, "CreateHotelRestaurantsRS_Success", false));

				String createAttTReq = WSClient.createSOAPMessage("CreateAttractionTemplates", "DS_02");
				String createAttTRes = WSClient.processSOAPMessage(createAttTReq);
				errorText=WSClient.getElementValue(createAttTRes, "CreateAttractionTemplatesRS_Errors_Error", XMLType.RESPONSE);
				if(errorText.contains("already exist"))
				{
					WSClient.writeToReport(LogStatus.PASS, "Attraction Template Already exists");
				}
				else if(WSAssert.assertIfElementExists(createAttTRes, "CreateAttractionTemplatesRS_Success", false));

				String createAttReq = WSClient.createSOAPMessage("CreateAttractions", "DS_02");
				String createAttRes = WSClient.processSOAPMessage(createAttReq);
				errorText=WSClient.getElementValue(createAttRes, "CreateAttractionsRS_Errors_Error", XMLType.RESPONSE);
				if(errorText.contains("already exist"))
				{
					WSClient.writeToReport(LogStatus.PASS, "Attractions Already exists");
				}
				else if(WSAssert.assertIfElementExists(createAttRes, "CreateAttractionsRS_Success", false));

				String notesReq = WSClient.createSOAPMessage("CreateHotelNotes", "DS_01");
				String notesRes = WSClient.processSOAPMessage(notesReq);
				WSAssert.assertIfElementExists(notesRes, "CreateHotelNotesRS_Success", false);
			}
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			populateQueryDetails();

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured is :" + e.toString());
			e.printStackTrace();
		}
	}
}