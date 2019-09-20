package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class QueryChainInformation extends WSSetUp{

	LinkedHashMap<String, String> singlenode;
	List<LinkedHashMap<String, String>> multiplenodes;
	String dataStr="";
	Random randomGenerator;

	public int randomGeneration(int size)
	{
		randomGenerator = new Random();
		int index = randomGenerator.nextInt(size);
		return index;

	}
	public void setData()
	{
		WSClient.setData("{VAR_DESCRIPTION}", "Ind development centre");
		WSClient.setData("{VAR_BEGIN_DATE}", "2016-10-26");
		WSClient.setData("{VAR_NAME}", OPERALib.getChain());
		WSClient.setData("{VAR_END_DATE}", "2020-12-31");
		WSClient.setData("{VAR_CHAIN_CODE}", OPERALib.getChain());
		WSClient.setData("{VAR_SHARE_PROFILES}", "Y");
		WSClient.setData("{VAR_ASP_YN}", "Y");
		WSClient.setData("{VAR_HO_STREET}", getData("{VAR_HO_STREET}"));
		WSClient.setData("{VAR_HO_CITY}", getData("{VAR_HO_CITY}"));
		WSClient.setData("{VAR_HO_POST_CODE}", getData("{VAR_HO_POST_CODE}"));
		WSClient.setData("{VAR_HO_COUNTRY}", getData("{VAR_HO_COUNTRY}"));
		WSClient.setData("{VAR_HO_STATE}", getData("{VAR_HO_STATE}"));
		WSClient.setData("{VAR_HO_TELEPHONE}", getData("{VAR_HO_TELEPHONE}"));
		WSClient.setData("{VAR_HO_FAX}", "341-879");
		WSClient.setData("{VAR_HO_EMAIL}","banglr@oracle.com");
		WSClient.setData("{VAR_BOOKING_CONDITIONS}", "VALID FOR DAY AND NIGHT");
		WSClient.setData("{VAR_LOYALTY_PROGRAM}", "FOR GUESTS WHO VISITED MORE THAN THRICE");
		WSClient.setData("{VAR_MARKETING_TEXT}", "FEEL LIKE YOUR HOME");
		WSClient.setData("{VAR_FREQUENT_FLIER_CARDS_ACCEPT_YN}", "Y");
	}
	public String getData(String keyword) {
		try{
			switch (keyword) {
			case "{VAR_DESCRIPTION}":
				dataStr = "Ind development centre";
				break;
			case "{VAR_BEGIN_DATE}":
				dataStr = "2016-10-26";
				break;
			case "{VAR_NAME}":
				dataStr = OPERALib.getChain();
				break;
			case "{VAR_END_DATE}":
				dataStr = "2020-12-31";
				break;
			case "{VAR_CHAIN_CODE}":
				dataStr = OPERALib.getChain();
				break;
			case "{VAR_SHARE_PROFILES}":
				dataStr = "Y";
				break;
			case "{VAR_ASP_YN}":
				dataStr = "Y";
				break;
			case "{VAR_HO_STREET}":
				ArrayList<String> street = new ArrayList<String>();
				street.add("1");
				street.add("2");
				street.add("3");
				street.add("4");
				street.add("5");
				street.add("6");
				street.add("7");
				street.add("8");
				street.add("9");
				dataStr=street.get(randomGeneration(street.size()));
				break;
			case "{VAR_HO_CITY}":
				ArrayList<String> Citylist = new ArrayList<String>();
				Citylist.add("Bangalore");
				Citylist.add("Indore");
				Citylist.add("Kolkata");
				Citylist.add("Mumbai");
				Citylist.add("Goa");
				Citylist.add("Trivendrum");
				Citylist.add("Chennai");
				Citylist.add("Pune");
				Citylist.add("Hyderabad");
				Citylist.add("NewDelhi");
				Citylist.add("Cochin");
				dataStr=Citylist.get(randomGeneration(Citylist.size()));
				break;
			case "{VAR_HO_POST_CODE}":
				String code = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
				dataStr = code;
				break;
			case "{VAR_HO_COUNTRY}":
				dataStr = "IND";
				break;

			case "{VAR_HO_STATE}":
				ArrayList<String> Statelist = new ArrayList<String>();
				Statelist.add("AndhraPradesh");
				Statelist.add("TamilNadu");
				Statelist.add("Karnataka");
				Statelist.add("Maharashtra");
				Statelist.add("Kerala");
				Statelist.add("Bihar");
				Statelist.add("Delhi");
				Statelist.add("MadhyaPradesh");
				Statelist.add("Rajasthan");
				dataStr=Statelist.get(randomGeneration(Statelist.size()));
				//dataStr="Karnataka";
				break;
			case "{VAR_HO_TELEPHONE}":
				String phone = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
				dataStr = phone;
				break;
			case "{VAR_HO_FAX}":
				dataStr = "341-879";
				break;
			case "{VAR_HO_EMAIL}":
				dataStr = "banglr@oracle.com";
				break;
			case "{VAR_BOOKING_CONDITIONS}":
				dataStr = "VALID FOR DAY AND NIGHT";
				break;
			case "{VAR_LOYALTY_PROGRAM}":
				dataStr = "FOR GUESTS WHO VISITED MORE THAN THRICE";
				break;
			case "{VAR_MARKETING_TEXT}":
				dataStr ="FEEL LIKE YOUR HOME";
				break;
			case "{VAR_FREQUENT_FLIER_CARDS_ACCEPT_YN}":
				dataStr = "Y";
				break;
			default:
				dataStr = "default text";
				break;

			}
			return dataStr;
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			return dataStr;
		}
	}


	//Method to check if the hashmap contains the key which we need

	public String getKey(LinkedHashMap<String, String> map, String key) {
		if (map.containsKey(key)) {
			return key;
		}
		return null;
	}

	public void informationLine(String query1) {

		try{

			setData();

			//String query = WSClient.getQuery("ChangeChain", "QS_04");
			WSClient.writeToReport(LogStatus.INFO, "<b>Query to get chain information from database</b>" );
			String query = WSClient.getQuery("ChangeChain", query1);
			//WSClient.writeToReport(LogStatus.INFO, query);
			singlenode = WSClient.getDBRow(query);
			for (String key : singlenode.keySet()) {
				String value = singlenode.get(key);
				String mapkey = "{VAR_";
				mapkey = mapkey + key + "}";


				if (StringUtils.isBlank(value)) {

					WSClient.setData(mapkey, getData(mapkey));


				} else
					WSClient.setData(mapkey, singlenode.get(key));
			}


		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally{
			singlenode.clear();
		}

	}
	public boolean changeChain(String dataset,String query1)
	{
		try{
			String resort = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resort);
			//String changeChainReq = WSClient.createSOAPMessage("ChangeChain", "DS_02");
			String changeChainReq = WSClient.createSOAPMessage("ChangeChain", dataset);
			String changeChainRes = WSClient.processSOAPMessage(changeChainReq);
			if (WSAssert.assertIfElementExists(changeChainRes, "ChangeChainRS_Success", true)) {

				//String query = WSClient.getQuery("ChangeChain", "QS_04");

				WSClient.writeToReport(LogStatus.INFO, "<b>Successfully executed ChangeChain</b>" );
				WSClient.writeToReport(LogStatus.INFO, "<b>Query to get the information from database to validate with query chain response</b>" );
				String query = WSClient.getQuery("ChangeChain", query1);

				singlenode = WSClient.getDBRow(query);
				for (String key : singlenode.keySet()) {
					String value = singlenode.get(key);
					if (StringUtils.isBlank(value)) {
						return false;

					}
				}
				return true;
			}
			else{
				WSClient.writeToReport(LogStatus.WARNING, "Problem in executing CHANGE CHAIN operation,unable to proceed");
				return false;
			}
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			return false;
		}


	}

	/**
	 * Method to check if the OWS Information Query Chain Information is working i.e., fetching
	 * chain details such as Chain code,country,name of the hotel

	 * chain should be configured in channel
	 */
	@Test(groups = { "sanity", "QueryChainInformation", "Information", "OWS" })

	public void queryChainInformation_39600()
	{
		try {
			String testName = "queryChainInformation_39600";
			WSClient.startTest(testName, "Verify that chain address information is successfully fetched ", "sanity");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain_code}",chain);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);


			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);

			informationLine("QS_05");


			if(changeChain("DS_01","QS_05")){

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

				String queryChainInfoReq = WSClient.createSOAPMessage("OWSQueryChainInformation", "DS_01");
				String queryChainInfoRes = WSClient.processSOAPMessage(queryChainInfoReq);

				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_OperaErrorCode", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */
					String message1 = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The OPERA error displayed in the Response is----:"+message1+"</b> ");



				}
				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_GDSError", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */


					String message = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_GDSError", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The GDSError displayed in the  response is :---" + message+"</b> ");

				}

				if(WSAssert.assertIfElementValueEquals(queryChainInfoRes, "ChainInformationResponse_Result_resultStatusFlag", "SUCCESS", false)){

					String addessLine=WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_AddressLine", XMLType.RESPONSE);
					String city=WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_cityName", XMLType.RESPONSE);
					String postCode=WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_postalCode", XMLType.RESPONSE);
					String state=WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_stateProv", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched chain information</b>");

					LinkedHashMap<String, String> expected =new LinkedHashMap<String, String>();
					expected.put("AddressLine1",singlenode.get("HO_STREET") );
					expected.put("cityName1",singlenode.get("HO_CITY") );
					expected.put("postalCode1",singlenode.get("HO_POST_CODE") );
					expected.put("stateProv1",singlenode.get("HO_STATE") );


					HashMap<String,String> xPath = new HashMap<String,String>();
					xPath.put("Addresses_Address_AddressLine", "ChainInformation_Addresses_Address");
					xPath.put("Addresses_Address_cityName", "ChainInformation_Addresses_Address");
					xPath.put("Addresses_Address_postalCode","ChainInformation_Addresses_Address");
					xPath.put("Addresses_Address_stateProv","ChainInformation_Addresses_Address");


					LinkedHashMap<String, String> actualValue=WSClient.getSingleNodeList(queryChainInfoRes, xPath, false, XMLType.RESPONSE);

					//comparing response and database values
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating chain address details </b>");

					//WSAssert.assertEquals(expected,actualValue ,false);
					if (WSAssert.assertEquals(singlenode.get("HO_STREET"), addessLine,
							true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>AddressLine-->Expected : </b>"
										+ singlenode.get("HO_STREET")
										+ "   <b>  Actual : </b>" + addessLine);

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>AddressLine-->Expected : </b>"
										+ singlenode.get("HO_STREET")
										+ "   <b>  Actual : </b>" + addessLine);

					}
					if (WSAssert.assertEquals(singlenode.get("HO_CITY"), city,
							true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>City-->Expected : </b>"
										+ singlenode.get("HO_CITY")
										+ "   <b>  Actual : </b>" + city);

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>City-->Expected : </b>"
										+ singlenode.get("HO_CITY")
										+ "   <b>  Actual : </b>" + city);

					}
					if (WSAssert.assertEquals(singlenode.get("HO_POST_CODE"), postCode,
							true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>PostalCode-->Expected : </b>"
										+ singlenode.get("HO_POST_CODE")
										+ "   <b>  Actual : </b>" + postCode);

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>PostalCode-->Expected : </b>"
										+ singlenode.get("HO_POST_CODE")
										+ "   <b>  Actual : </b>" + postCode);

					}
					if (WSAssert.assertEquals(singlenode.get("HO_STATE"), state,
							true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>State-->Expected : </b>"
										+ singlenode.get("HO_STATE")
										+ "   <b>  Actual : </b>" + state);

					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>State-->Expected : </b>"
										+ singlenode.get("HO_STATE")
										+ "   <b>  Actual : </b>" + state);

					}


				}

			}
			else{
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite----Change Chain blocked,Can't proceed further");
			}

		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


	//MinimumRegression:case1:  Chain address and contact details being fetched correctly

	@Test(groups = { "minimumRegression", "QueryChainInformation", "Information", "OWS" })

	public void queryChainInformation_40084()
	{
		try {
			String testName = "queryChainInformation_40084";
			WSClient.startTest(testName, "Verify that chain address and contact information is successfully fetched ", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain_code}",chain);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);

			informationLine("QS_05");


			if(changeChain("DS_01","QS_05")){

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

				String queryChainInfoReq = WSClient.createSOAPMessage("OWSQueryChainInformation", "DS_01");
				String queryChainInfoRes = WSClient.processSOAPMessage(queryChainInfoReq);

				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_OperaErrorCode", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */
					String message1 = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The OPERA error displayed in the Response is----:"+message1+"</b> ");



				}
				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_GDSError", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */


					String message = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_GDSError", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The GDSError displayed in the  response is :---" + message+"</b> ");

				}

				if(WSAssert.assertIfElementValueEquals(queryChainInfoRes, "ChainInformationResponse_Result_resultStatusFlag", "SUCCESS", false)){

					WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched chain information</b>");

					LinkedHashMap<String, String> expected =new LinkedHashMap<String, String>();
					expected.put("AddressLine",singlenode.get("HO_STREET") );
					expected.put("cityName",singlenode.get("HO_CITY") );
					expected.put("postalCode",singlenode.get("HO_POST_CODE") );
					expected.put("stateProv",singlenode.get("HO_STATE") );

					expected.put("email",singlenode.get("HO_EMAIL") );
					expected.put("phnNumber",singlenode.get("HO_TELEPHONE") );

					LinkedHashMap<String, String> actualValue =new LinkedHashMap<String, String>();

					actualValue.put("AddressLine",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_AddressLine", XMLType.RESPONSE) );
					actualValue.put("cityName",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_cityName", XMLType.RESPONSE));
					actualValue.put("postalCode",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_postalCode", XMLType.RESPONSE) );
					actualValue.put("stateProv",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_stateProv", XMLType.RESPONSE) );

					actualValue.put("email",WSClient.getElementValue(queryChainInfoRes,
							"ChainInformation_ContactEmails_Email", XMLType.RESPONSE) );
					//				actualValue.put("phnNumber",WSClient.getElementValue(queryChainInfoRes,
					//						"ContactPhones_Phone_PhoneNumber", XMLType.RESPONSE) );
					actualValue.put("phnNumber",WSClient.getElementValueByAttribute(queryChainInfoRes, "ContactPhones_Phone_PhoneNumber", "ChainInformation_ContactPhones_Phone_phoneRole", "PHONE", XMLType.RESPONSE) );
					//comparing response and database values
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating chain address and contact details </b>");

					WSAssert.assertEquals(expected,actualValue ,false);


				}

			}
			else{
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite----Change Chain blocked,Can't proceed further");
			}

		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	//MinimumRegression:case2: error message populates when invalid chainCode
	@Test(groups = { "minimumRegression", "QueryChainInformation", "Information", "OWS" })

	public void queryChainInformation_41284()
	{
		try {
			String testName = "queryChainInformation_41284";
			WSClient.startTest(testName, "Verify that error message populates when invalid chaincode is passed ", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain_code}",chain);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);


			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String queryChainInfoReq = WSClient.createSOAPMessage("OWSQueryChainInformation", "DS_02");
			String queryChainInfoRes = WSClient.processSOAPMessage(queryChainInfoReq);


			if(WSAssert.assertIfElementValueEquals(queryChainInfoRes, "ChainInformationResponse_Result_resultStatusFlag", "FAIL", false)){


				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_OperaErrorCode", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */
					String message1 = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The OPERA error displayed in the Response is----:"+message1+"</b> ");



				}
				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_GDSError", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */


					String message = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_GDSError", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The GDSError displayed in the  response is :---" + message+"</b> ");

				}

			}
			else if(WSAssert.assertIfElementValueEquals(queryChainInfoRes, "ChainInformationResponse_Result_resultStatusFlag", "SUCCESS", false)){

				WSClient.writeToReport(LogStatus.PASS,"QueryChainInformation is successful!!ERROR");

			}


		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	//MinimumRegression:case3:  Chain information being fetched correctly

	@Test(groups = { "minimumRegression", "QueryChainInformation", "Information", "OWS" })

	public void queryChainInformation_41302()
	{
		try {
			String testName = "queryChainInformation_41302";
			WSClient.startTest(testName, "Verify that all the chain information is successfully fetched ", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain_code}",chain);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);

			informationLine("QS_05");


			if(changeChain("DS_01","QS_05")){

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

				String queryChainInfoReq = WSClient.createSOAPMessage("OWSQueryChainInformation", "DS_01");
				String queryChainInfoRes = WSClient.processSOAPMessage(queryChainInfoReq);

				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_OperaErrorCode", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */
					String message1 = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The OPERA error displayed in the Response is----:"+message1+"</b> ");



				}
				if (WSAssert.assertIfElementExists(queryChainInfoRes,
						"ChainInformationResponse_Result_GDSError", true)) {

					/*
					 * Verifying whether the error Message is populated
					 * on the response
					 */


					String message = WSAssert.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_Result_GDSError", XMLType.RESPONSE);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>"+"The GDSError displayed in the  response is :---" + message+"</b> ");

				}

				if(WSAssert.assertIfElementValueEquals(queryChainInfoRes, "ChainInformationResponse_Result_resultStatusFlag", "SUCCESS", false)){

					WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched chain information</b>");

					LinkedHashMap<String, String> expected =new LinkedHashMap<String, String>();
					expected.put("AddressLine",singlenode.get("HO_STREET") );
					expected.put("cityName",singlenode.get("HO_CITY") );
					expected.put("postalCode",singlenode.get("HO_POST_CODE") );
					expected.put("stateProv",singlenode.get("HO_STATE") );
					expected.put("Country",singlenode.get("HO_COUNTRY") );
					expected.put("email",singlenode.get("HO_EMAIL") );
					expected.put("phnNumber",singlenode.get("HO_TELEPHONE") );
					expected.put("fax",singlenode.get("HO_FAX") );
					expected.put("BookingConditions",singlenode.get("BOOKING_CONDITIONS") );
					expected.put("MarketingText",singlenode.get("MARKETING_TEXT") );
					expected.put("LoyaltyProgram",singlenode.get("LOYALTY_PROGRAM") );
					expected.put("AcceptFrequenFlyerCard",singlenode.get("FREQUENT_FLIER_CARDS_ACCEPT_YN") );

					LinkedHashMap<String, String> actualValue =new LinkedHashMap<String, String>();

					actualValue.put("AddressLine",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_AddressLine", XMLType.RESPONSE) );
					actualValue.put("cityName",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_cityName", XMLType.RESPONSE));
					actualValue.put("postalCode",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_postalCode", XMLType.RESPONSE) );
					actualValue.put("stateProv",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_stateProv", XMLType.RESPONSE) );
					actualValue.put("Country",WSClient.getElementValue(queryChainInfoRes,
							"Addresses_Address_countryCode", XMLType.RESPONSE) );
					actualValue.put("email",WSClient.getElementValue(queryChainInfoRes,
							"ChainInformation_ContactEmails_Email", XMLType.RESPONSE) );
					actualValue.put("phnNumber",WSClient.getElementValueByAttribute(queryChainInfoRes, "ContactPhones_Phone_PhoneNumber", "ChainInformation_ContactPhones_Phone_phoneRole", "PHONE", XMLType.RESPONSE) );
					actualValue.put("fax",WSClient.getElementValueByAttribute(queryChainInfoRes, "ContactPhones_Phone_PhoneNumber", "ChainInformation_ContactPhones_Phone_phoneRole", "FAX", XMLType.RESPONSE) );
					actualValue.put("BookingConditions",WSClient.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_ChainInformation_BookingConditions", XMLType.RESPONSE) );
					actualValue.put("MarketingText",WSClient.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_ChainInformation_MarketingText", XMLType.RESPONSE) );
					actualValue.put("LoyaltyProgram",WSClient.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_ChainInformation_LoyaltyProgram", XMLType.RESPONSE) );
					actualValue.put("AcceptFrequenFlyerCard",WSClient.getElementValue(queryChainInfoRes,
							"ChainInformationResponse_ChainInformation_AcceptFrequenFlyerCard", XMLType.RESPONSE) );

					//comparing response and database values

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating all the chain details </b>");


					WSAssert.assertEquals(expected,actualValue ,false);


				}

			}
			else{
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite----Change Chain blocked,Can't proceed further");
			}

		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	//MinimumRegression:case4: error message populates when resortID is not passed

	//		@Test(groups = { "minimumRegression", "QueryChainInformation", "Information", "OWS" })
	//
	//		public void queryChainInformation_41303()
	//		{
	//			try {
	//				String testName = "queryChainInformation_41303";
	//				WSClient.startTest(testName, "Verify that error message populates when resortID is not passed ", "minimumRegression");
	//
	//				String resortOperaValue = OPERALib.getResort();
	//				String chain = OPERALib.getChain();
	//				WSClient.setData("{var_chain}", chain);
	//
	//				WSClient.setData("{var_resort}", resortOperaValue);
	//				WSClient.setData("{var_chain_code}",chain);
	//				//WSClient.setData("{var_chain_code}",WSClient.getKeywordData("{KEYWORD_SPACE}"));
	//				  String uname = OPERALib.getUserName();
	//			    OPERALib.setOperaHeader(uname);
	//
	//
	//				String pwd = OPERALib.getPassword();
	//				String channel = OWSLib.getChannel();
	//				String channelType = OWSLib.getChannelType(channel);
	//				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//				WSClient.setData("{var_owsresort}", resort);
	//				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//
	//				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	//
	//				String queryChainInfoReq = WSClient.createSOAPMessage("OWSQueryChainInformation", "DS_03");
	//				String queryChainInfoRes = WSClient.processSOAPMessage(queryChainInfoReq);
	//
	//
	//				if(WSAssert.assertIfElementValueEquals(queryChainInfoRes, "ChainInformationResponse_Result_resultStatusFlag", "FAIL", false)){
	//
	//
	//					if (WSAssert.assertIfElementExists(queryChainInfoRes,
	//							"Result_Text_TextElement", true)) {
	//
	//						/*
	//						 * Verifying whether the error Message is populated
	//						 * on the response
	//						 */
	//						String message1 = WSAssert.getElementValue(queryChainInfoRes,
	//								"Result_Text_TextElement", XMLType.RESPONSE);
	//
	//							WSClient.writeToReport(LogStatus.INFO,
	//									"<b>"+"The error displayed in the Response is----:"+message1+"</b> ");
	//
	//
	//
	//					}
	//						if (WSAssert.assertIfElementExists(queryChainInfoRes,
	//								"ChainInformationResponse_Result_OperaErrorCode", true)) {
	//
	//							/*
	//							 * Verifying whether the error Message is populated
	//							 * on the response
	//							 */
	//							String message1 = WSAssert.getElementValue(queryChainInfoRes,
	//									"ChainInformationResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//
	//								WSClient.writeToReport(LogStatus.INFO,
	//										"<b>"+"The OPERA error displayed in the Response is----:"+message1+"</b> ");
	//
	//
	//
	//						}
	//						if (WSAssert.assertIfElementExists(queryChainInfoRes,
	//								"ChainInformationResponse_Result_GDSError", true)) {
	//
	//							/*
	//							 * Verifying whether the error Message is populated
	//							 * on the response
	//							 */
	//
	//
	//							String message = WSAssert.getElementValue(queryChainInfoRes,
	//									"ChainInformationResponse_Result_GDSError", XMLType.RESPONSE);
	//
	//								WSClient.writeToReport(LogStatus.INFO,
	//										"<b>"+"The GDSError displayed in the  response is :---" + message+"</b> ");
	//
	//						}
	//
	//					}
	//				else if(WSAssert.assertIfElementValueEquals(queryChainInfoRes, "ChainInformationResponse_Result_resultStatusFlag", "SUCCESS", false)){
	//
	//					WSClient.writeToReport(LogStatus.PASS,"QueryChainInformation is successful!!ERROR");
	//
	//				}
	//
	//
	//			}catch (Exception e) {
	//				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//			}
	//		}
}