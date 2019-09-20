package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.availability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeChannelParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchChannelParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class Availability extends WSSetUp
{

	public void setOWSHeader() {
		try {
			String resort = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String channel=OWSLib.getChannel();
			//String channel = "IDC7TEST";
			String pwd = OPERALib.getPassword();
			String extresort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_extResort}", extresort);
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			//String channelCarrier = "IDC7TEST";
			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
		}
	}

	public int getNumberOfRooms() {
		int documentId = 0;
		try {
			String query=WSClient.getQuery("OWSAvailability","QS_02");
			System.out.println(query);
			List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
			String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
			String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
			System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
			int dId1=Integer.parseInt(docId1);
			int dId2=Integer.parseInt(docId2);
			documentId = dId1+dId2;

		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Exception occured due in getNumberOfRooms : "+e);
		}
		WSClient.writeToReport(LogStatus.INFO, documentId + "");
		return documentId;
	}

	//Sanity
	@Test(groups = { "minimumRegression","OWS","Availability","Availability", "availability_4980" })
	@Parameters({"runOnEntry"})
	public void availability_4980(String runOnEntry)
	{
		try
		{
			String testName = "availability_4980";
			WSClient.startTest(testName, "Verify that rooms are available for the given rate code and the roomtype in case of General availability", "minimumRegression");
			WSClient.setResortEntry(runOnEntry);
			//Fetching required values and setting required variables.
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			String channel=OWSLib.getChannel();			// Will fetch the first channel
			//String channel = "IDC7TEST";
			WSClient.setData("{var_channel}", channel);
			String resort = OPERALib.getResort();
			String owsresort = OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			//String channelCarrier = "IDC7TEST";
			WSClient.setData("{var_channelCarrier}", channelCarrier);

			//Fetching External value of the Rate Code, Room Type from Config file
			String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			//String exRate = "CKRC";
			//String rt = "3RT";
			WSClient.setData("{var_rt}", rt);
			WSClient.setData("{var_rate}", exRate);


			//Fetching Room Category for the Room Type
			WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
			String query4=WSClient.getQuery("OWSAvailability","QS_05");
			String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
			WSClient.setData("{var_rc}", rc);

			//Fetching the total Rooms which are already assigned
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
			String query=WSClient.getQuery("OWSAvailability","QS_02");
			System.out.println(query);
			List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
			String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
			int dId1=Integer.parseInt(docId1);
			int documentId = dId1;
			if(noOfSoldRoomsListMap.size() > 1)
			{
				String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
				System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
				int dId2=Integer.parseInt(docId2);
				documentId = dId1+dId2;
			}


			//Fetching Total Number of Rooms which are available for the property.
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
			String query1=WSClient.getQuery("OWSAvailability","QS_03");
			int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
			//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));


			//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
			if(documentId1-documentId==0)

			{
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
				String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

				if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
				{
					String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
					WSClient.setData("{var_RoomNo}", roomNumber);

				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
				}
			}
			else
			{
				WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
				WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_oresort}", owsresort);
				String req_deleteDoc = WSClient.createSOAPMessage("OWSAvailability", "DS_01");
				String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

				if(WSAssert.assertIfElementValueEquals(res_deleteDoc, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
					//Validating RatePlan Details
					String query2=WSClient.getQuery("QS_04");

					List<LinkedHashMap<String, String>>rplanL = new ArrayList<LinkedHashMap<String, String>>();
					rplanL = WSClient.getDBRows(query2);
					String intRate= rplanL.get(0).get("ratePlanCode1");

					WSClient.setData("{var_orate}", intRate);
					//String query3=WSClient.getQuery("QS_09");
					//String rPlanCode1=WSClient.getDBRow(query3).get("RATE_CODE");

					LinkedHashMap<String, String> path1 = new LinkedHashMap<>();
					path1.put("RoomStay_RatePlans_RatePlan_ratePlanCode", "RoomStay_RatePlans_RatePlan");
					List<LinkedHashMap<String, String>> rPlanList = new ArrayList<LinkedHashMap<String, String>>();
					rPlanList = WSClient.getMultipleNodeList(res_deleteDoc, path1, false, XMLType.RESPONSE);
					WSAssert.assertEquals(rPlanList, rplanL, false);
					//WSAssert.assertEquals(rplanL, rPlanList, false);



					//Validating RoomType Details
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type and Available Room Information</b>");
					LinkedHashMap<String , String> rdata = new LinkedHashMap<>();
					rdata.put("RoomStay_RoomTypes_RoomType_roomTypeCode", "RoomStay_RoomTypes_RoomType");
					rdata.put("RoomStay_RoomTypes_RoomType_numberOfUnits", "RoomStay_RoomTypes_RoomType");
					List<LinkedHashMap<String, String>>  actual_roomTypeL = new ArrayList<LinkedHashMap<String, String>>();
					actual_roomTypeL = WSClient.getMultipleNodeList(res_deleteDoc,rdata , false, XMLType.RESPONSE);
					rdata.clear();


					List<LinkedHashMap<String, String>>  expected_roomTypeL = new ArrayList<LinkedHashMap<String, String>>();
					rdata.put("roomTypeCode1", WSClient.getElementValue(req_deleteDoc, "AvailRequestSegment_RoomStayCandidates_RoomStayCandidate_roomTypeCode", XMLType.REQUEST));
					rdata.put("numberOfUnits1", Integer.toString(documentId1-documentId));
					expected_roomTypeL.add(rdata);
					WSAssert.assertEquals(actual_roomTypeL, expected_roomTypeL, false);
					//WSAssert.assertEquals(expected_roomTypeL, actual_roomTypeL, false);

					//						List<LinkedHashMap<String, String>>  expected_roomTypeL = new ArrayList<LinkedHashMap<String, String>>();
					//						rdata.put("roomTypeCode1", WSClient.getElementValue(req_deleteDoc, "AvailRequestSegment_RoomStayCandidates_RoomStayCandidate_roomTypeCode", XMLType.REQUEST));
					//						expected_roomTypeL.add(rdata);
					//						WSAssert.assertEquals(actual_roomTypeL, expected_roomTypeL, false);

					//Validating Room Number
					//String query6=WSClient.getQuery("QS_06");
					//String descrt=WSClient.getDBRow(query6).get("SHORT_DESCRIPTION");

					//						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Number of Rooms</b>");
					//						WSAssert.assertIfElementValueEquals(res_deleteDoc,"RoomStay_RoomTypes_RoomType_numberOfUnits", Integer.toString(documentId1-documentId), false);
				}
			}
		}
		catch (Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 1
	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_4981() {
		String testName = "availability_4981";
		try {

			WSClient.startTest(testName, "Verify Hotel address details are fetched correctly on the response when HotelCode details are passed on the request.", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			//Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04").substring(3) );

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_03");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					LinkedHashMap<String,String> expectedValues =new LinkedHashMap<String,String>();
					LinkedHashMap<String, String> actualValues =new  LinkedHashMap<String,String>();
					LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
					xPath.put("Addresses_Address_AddressLine", "HotelContact_Addresses_Address");
					xPath.put("Addresses_Address_cityName", "HotelContact_Addresses_Address");
					xPath.put("Addresses_Address_stateProv", "HotelContact_Addresses_Address");
					xPath.put("Addresses_Address_countryCode", "HotelContact_Addresses_Address");
					actualValues = WSClient.getSingleNodeList(AvailabilityRes, xPath, false, XMLType.RESPONSE);
					expectedValues=WSClient.getDBRow(WSClient.getQuery("QS_07"));
					WSAssert.assertEquals(expectedValues,actualValues,false);
				}
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 2
	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_22923() {
		String previousValue = "10";
		try {
			String testName = "availability_22923";
			WSClient.startTest(testName, "Verify the number of nights a channel can request for availability.", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);

			WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
			WSClient.setData("{var_settingValue}", "4");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue = Parameter1;
			WSClient.writeToReport(LogStatus.INFO, "<b>MAX_NO_OF_NIGHTS Parameter previous value "+ Parameter1  + "</b>");
			if(!Parameter1.equals("error")) {
				if (!Parameter1.equals("4")) {
					Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>MAX_NO_OF_NIGHTS Parameter Set to value "+ Parameter1  + "</b>");
				}

				WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01"));

				setOWSHeader();

				// Creating request and processing response for OWS DeletePayRouting Operation
				String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_04");
				String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);

				//Validating response of OWS Availability
				if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
					if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "FAIL", false)) {

						if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", false)) {
							if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", "NUMBER_NIGHTS_EXCEEDS_LIMIT", false)) {

							}
						}

					}
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>MAX_NO_OF_NIGHTS Parameter value is retrieved as Error "+ Parameter1  + "</b>");
			}
		}

		catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*// Minimum Regression Test Case : 3
	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_22929() {
		String previousValue = "10";
		try {
			String testName = "availability_22929";
			WSClient.startTest(testName, "Verify that user is able to see error message if availability is requested for more rooms than maximum Number of Rooms Sold per Reservation. ", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);

			WSClient.setData("{var_parameter}", "PER_RESERVATION_ROOM_LIMIT");
			WSClient.setData("{var_settingValue}", "2");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>PER_RESERVATION_ROOM_LIMIT Parameter previous value "+ Parameter1  + "</b>");
			if (!Parameter1.equals("2")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>PER_RESERVATION_ROOM_LIMIT Parameter Set to value "+ Parameter1  + "</b>");
			}
			WSClient.setData("{var_parameter}", "RESV_MAX_ROOMS");
			WSClient.setData("{var_settingValue}", "2");
			String Parameter2 = FetchApplicationParameters.getApplicationParameter("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>RESV_MAX_ROOMS Parameter previous value "+ Parameter2  + "</b>");
			if (!Parameter2.equals("2")) {
				Parameter2=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>RESV_MAX_ROOMS Parameter Set to value "+ Parameter2  + "</b>");
			}



			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01"));

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_05");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "FAIL", false)) {

					if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", false)) {
						if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", "NUMBER_ROOMS_EXCEEDS_LIMIT", false)) {

						}
					}

				}
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {

			try {
				WSClient.setData("{var_parameter}", "PER_RESERVATION_ROOM_LIMIT");
				WSClient.setData("{var_settingValue}", previousValue);
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				WSClient.setData("{var_parameter}", "RESV_MAX_ROOMS");
				WSClient.setData("{var_settingValue}", previousValue);
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/

	// Minimum Regression Test Case : 5
	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_41422() {
		String previousValue = "10";
		try {
			String testName = "availability_41422";
			WSClient.startTest(testName, "Verify that roomtypes and roomrates associated to hotel are fetched on passing alternate days.", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_channel}",OWSLib.getChannel());
			WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
			WSClient.setData("{var_settingValue}", "5");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			// previousValue = Parameter1;
			WSClient.writeToReport(LogStatus.INFO, "<b>MAX_NO_OF_NIGHTS Parameter previous value "+ Parameter1  + "</b>");
			if (!Parameter1.equals("5")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>MAX_NO_OF_NIGHTS Parameter Set to value "+ Parameter1  + "</b>");
			}

			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_02") );
			WSClient.setData("{var_roomTypeCode}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01") );

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_09");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					String ratePlanCode = WSClient.getData("{var_ratePlanCode}");
					WSClient.setData("{var_rate}", ratePlanCode);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
					WSAssert.assertIfElementValueEquals(AvailabilityRes,"RoomStay_RatePlans_RatePlan_ratePlanCode", ratePlanCode, false);

					String query2=WSClient.getQuery("QS_13");
					String intRate=WSClient.getDBRow(query2).get("ratePlanCode1");
					WSClient.setData("{var_orate}", intRate);
					String query3=WSClient.getQuery("QS_09");
					HashMap<String,String> ratedetails = WSClient.getDBRow(query3);
					String desc1=ratedetails.get("DESCRIPTION");
					String rPlanCode1=ratedetails.get("RATE_CODE");
					String sRate1 = ratedetails.get("SHOW_RATE_AMOUNT_YN");
					WSClient.writeToReport(LogStatus.INFO, sRate1);
					if(sRate1.equalsIgnoreCase("Y"))
						sRate1 = "true";
					else
						sRate1 = "false";
					WSAssert.assertIfElementValueEquals(AvailabilityRes,"RoomStay_RatePlans_RatePlan_ratePlanCode", rPlanCode1, false);
					WSAssert.assertIfElementValueEquals(AvailabilityRes,"RoomStay_RatePlans_RatePlan_suppressRate", sRate1, false);
					WSAssert.assertIfElementValueEquals(AvailabilityRes,"RatePlan_RatePlanDescription_Text", desc1, false);

					String room = WSClient.getElementValue(AvailabilityReq,"AvailRequestSegment_RoomStayCandidates_RoomStayCandidate_roomTypeCode",XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Information</b>");
					WSAssert.assertIfElementValueEquals(AvailabilityRes,"RoomStay_RoomRates_RoomRate_roomTypeCode", room, false);


					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Number of Rooms</b>");

					/*-------------------------------------------------------*/

					String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
					String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
					WSClient.setData("{var_rt}", rt);
					WSClient.setData("{var_rate}", exRate);

					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
					String query4=WSClient.getQuery("OWSAvailability","QS_05");
					String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
					WSClient.setData("{var_rc}", rc);
					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");

					WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
					String query=WSClient.getQuery("OWSAvailability","QS_02");
					System.out.println(query);
					List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
					String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
					String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
					System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
					int dId1=Integer.parseInt(docId1);
					int dId2=Integer.parseInt(docId2);
					int documentId = dId1+dId2;

					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory.</b>");

					String query1=WSClient.getQuery("OWSAvailability","QS_03");

					int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());


					WSAssert.assertIfElementValueEquals(AvailabilityRes,"RoomStay_RoomTypes_RoomType_numberOfUnits", Integer.toString(documentId1-documentId), false);


					String query6=WSClient.getQuery("QS_06");
					String descrt=WSClient.getDBRow(query6).get("SHORT_DESCRIPTION");
					WSAssert.assertIfElementValueEquals(AvailabilityRes,"RoomType_RoomTypeDescription_Text", descrt, false);

				}
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Minimum Regression Test Case : 6
	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_41423() {

		try {
			String testName = "availability_41423";
			WSClient.startTest(testName, "Verify that error message is obtained when passing date range before business date.", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);

			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01") );
			WSClient.setData("{var_roomTypeCode}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01") );

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_10");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "FAIL", false)) {

					if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", false)) {
						if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", "PRIOR_STAY", false)) {

						}
					}

				}
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// TO BE VALIDATED
	// Minimum Regression Test Case : 7
	//	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	//	public void availability_12364() {
	//
	//		try {
	//		String testname = "availability_12364";
	//		WSClient.startTest(testName, "Verify that available psuedo rooms are fetched.", "minimumRegression");
	//
	//		String resortOperaValue = OPERALib.getResort();
	//        String chain=OPERALib.getChain();
	//
	//        WSClient.setData("{var_resort}", resortOperaValue);
	//        WSClient.setData("{var_chain}",chain);
	//
	//		WSClient.setData("{var_ratePlanCode}", "SUITE15");
	//		WSClient.setData("{var_roomTypeCode}", "CH_SUK8" );
	//
	//		setOWSHeader();
	//
	//		// Creating request and processing response for OWS DeletePayRouting Operation
	//		String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_13");
	//        String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);
	//
	//        //Validating response of OWS Availability
	//        if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
	//        	if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	//
	//        		//Database Validation
	//
	//
	//
	//        	}
	//        }
	//	}catch(Exception e) {
	//		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//	}
	//}
	//
	// Passing number of rooms more than available rooms

	/***********************************
	 *
	 * KETAKI'S PART
	 *
	 ***********************************/

	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_4982() {

		try {
			String testName = "availability_4982";
			WSClient.startTest(testName, "Verify Hotel Contact email, Phone and hotel information details are fetched correctly on the response when hotelcode details are passed on the request", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			//Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01") );

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_03");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					LinkedHashMap<String,String> expectedValues =new LinkedHashMap<String,String>();
					LinkedHashMap<String, String> actualValues =new  LinkedHashMap<String,String>();
					LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
					xPath.put("RoomStayList_RoomStay_HotelReference", "RoomStayList_RoomStay_HotelReference");
					xPath.put("HotelContact_ContactEmails_ContactEmail", "RoomStay_HotelContact_ContactEmails");
					xPath.put("ContactPhones_Phone_PhoneNumber", "HotelContact_ContactPhones_Phone");
					xPath.put("HotelInfo_Text_TextElement", "HotelInformation_HotelInfo_Text");

					actualValues = WSClient.getSingleNodeList(AvailabilityRes, xPath, false, XMLType.RESPONSE);

					expectedValues=WSClient.getDBRow(WSClient.getQuery("QS_08"));
					if(WSAssert.assertEquals(expectedValues,actualValues,false))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched Hotel Contact Information"+"</b>");

					};
				}
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_41427() {

		try {
			String testName = "availability_41427";
			WSClient.startTest(testName, "Verify that an error message is obtained when no hotelcode is passed in the request", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			//Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01") );

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_17");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);
			if(WSAssert.assertIfElementExists(AvailabilityRes, "Result_Text_TextElement",true))
			{

				/**** Verifying that the error message is populated on the response ********/

				String message=WSAssert.getElementValue(AvailabilityRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode",true))
			{

				/**** Verifying whether the error Message is populated on the response ****/

				String message=WSAssert.getElementValue(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "FAIL", false));

			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_39808() {

		try {
			String testName = "availability_39808";
			WSClient.startTest(testName, "Verify that an error message is obtained when no chainCode is passed in the request", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			//Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01") );

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_14");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);
			if(WSAssert.assertIfElementExists(AvailabilityRes, "Result_Text_TextElement",true))
			{

				/**** Verifying that the error message is populated on the response ********/

				String message=WSAssert.getElementValue(AvailabilityRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode",true))
			{

				/**** Verifying whether the error Message is populated on the response ****/

				String message=WSAssert.getElementValue(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "FAIL", false));

			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_39809() {

		try {
			String testName = "availability_39809";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid enddate is passed in the request", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			//Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01") );

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_16");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);
			if(WSAssert.assertIfElementExists(AvailabilityRes, "Result_Text_TextElement",true))
			{

				/**** Verifying that the error message is populated on the response ********/

				String message=WSAssert.getElementValue(AvailabilityRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode",true))
			{

				/**** Verifying whether the error Message is populated on the response ****/

				String message=WSAssert.getElementValue(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "FAIL", false));

			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	@Test(groups={"minimumRegression","Availability","Availability","OWS"})
	public void availability_39803() {

		try {
			String testName = "availability_39803";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid RoomType Code is passed in the request", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			//Setting Variables
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01") );
			WSClient.setData("{var_rt}", WSClient.getKeywordData("{KEYWORD_RANDSTR_4}" ));

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_15");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);
			if(WSAssert.assertIfElementExists(AvailabilityRes, "Result_Text_TextElement",true))
			{

				/**** Verifying that the error message is populated on the response ********/

				String message=WSAssert.getElementValue(AvailabilityRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode",true))
			{

				/**** Verifying whether the error Message is populated on the response ****/

				String message=WSAssert.getElementValue(AvailabilityRes, "AvailabilityResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementExists(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", true)) {
				if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag", "FAIL", false));

			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}


	/***********************************
	 *
	 * ANKUR'S PART
	 *
	 ***********************************/

	// Minimum Regression Test Case : General Availability for Promotion attached to the rate code
	@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	public void availability_41788()
	{
		try
		{
			String testName = "availability_41788";
			WSClient.startTest(testName, "Verify that rooms are available for the promotion code attached to a rate code", "minimumRegression");

			//Fetching required values and setting required variables.
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			String channel=OWSLib.getChannel();
			//String channel = "7PROPC";
			WSClient.setData("{var_channel}", channel);
			String resort = OPERALib.getResort();
			String owsresort = OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			//String channelCarrier = "IDC7TEST";
			WSClient.setData("{var_channelCarrier}", channelCarrier);

			//Fetching External value of the Rate Code, Room Type and Promotion Code from Config file, Also Getting Internal Value of the Rate Code
			//String exRate = "CKRC";
			String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
			WSClient.setData("{var_rate}", exRate);
			//String rt = "3RT";
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_rt}", rt);
			//String pCode = "QA_HOL4";
			String pCode = OperaPropConfig.getDataSetForCode("PromotionCode", "DS_03");
			WSClient.setData("{var_promCode}", pCode);
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the Opera Value of the rate code</b>");
			String QS13=WSClient.getQuery("OWSAvailability", "QS_13");
			String inRate = WSClient.getDBRow(QS13).get("ratePlanCode1");

			//Fetching Room Category for the Room Type
			WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
			String QS05=WSClient.getQuery("OWSAvailability","QS_05");
			String rc=WSClient.getDBRow(QS05).get("ROOM_CATEGORY");
			WSClient.setData("{var_rc}", rc);

			//Fetching the total Rooms which are already assigned
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
			String query=WSClient.getQuery("OWSAvailability","QS_02");
			System.out.println(query);
			List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
			String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
			int dId1=Integer.parseInt(docId1);
			int documentId = dId1;
			if(noOfSoldRoomsListMap.size() > 1)
			{
				String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
				System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
				int dId2=Integer.parseInt(docId2);
				documentId = dId1+dId2;
			}

			//Fetching Total Number of Rooms which are available for the property.
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
			String query1=WSClient.getQuery("OWSAvailability","QS_03");
			int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
			//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));

			//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
			if(documentId1-documentId==0)

			{
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
				String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

				if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
				{
					String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
					WSClient.setData("{var_RoomNo}", roomNumber);

				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
				}
			}
			else
			{
				WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
				WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_oresort}", owsresort);
				String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_06");
				String res_availability = WSClient.processSOAPMessage(req_availability);

				if(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
					WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode", exRate, false);

					WSClient.setData("{var_orate}", inRate);
					String QS09=WSClient.getQuery("QS_09");
					String desc1=WSClient.getDBRow(QS09).get("DESCRIPTION");
					String rPlanCode1=WSClient.getDBRow(QS09).get("RATE_CODE");
					String QS10=WSClient.getQuery("QS_10");
					String desc2=WSClient.getDBRow(QS10).get("RATE_USE_DESC");
					String prCode = WSClient.getDBRow(QS10).get("PROMO_CODE");
					String desc=desc2+" "+desc1;
					WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode", rPlanCode1, false);
					WSAssert.assertIfElementValueEquals(res_availability,"RatePlan_RatePlanDescription_Text", desc, false);
					WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_promotionCode", prCode, false);


				}
			}
		}
		catch (Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	//	// Minimum Regression Test Case	: Detail Availability for given Rate Code and Room Type
	//	@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	//	public void availability_411789()
	//	{
	//		try
	//		{
	//			String testName = "availability_41789";
	//			WSClient.startTest(testName, "Verify that rooms are available for the given rate code and the roomtype in case of Detail availability", "minimumRegression");
	//
	//			//Fetching required values and setting required variables.
	//			String interfaceName = OWSLib.getChannel();
	//			String resortOperaValue = OPERALib.getResort();
	//			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
	//			WSClient.setData("{var_profileSource}", interfaceName);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//			WSClient.setData("{var_chain}",OPERALib.getChain());
	//			WSClient.setData("{var_extResort}", resortExtValue);
	//			String channel=OWSLib.getChannel();
	//			//String channel = "7PROPC";
	//			WSClient.setData("{var_channel}", channel);
	//			String resort = OPERALib.getResort();
	//			String owsresort = OWSLib.getChannelResort(resort, channel);
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channelType = OWSLib.getChannelType(channel);
	//			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//			//String channelCarrier = "IDC7TEST";
	//			WSClient.setData("{var_channelCarrier}", channelCarrier);
	//
	//			//Fetching External value of the Rate Code, Room Type from Config file
	//			String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
	//			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
	//			//String exRate = "CKRC";
	//			//String rt = "3RT";
	//			WSClient.setData("{var_rt}", rt);
	//			WSClient.setData("{var_rate}", exRate);
	//
	//
	//			//Fetching Room Category for the Room Type
	//			WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
	//			String query4=WSClient.getQuery("OWSAvailability","QS_05");
	//			String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
	//			WSClient.setData("{var_rc}", rc);
	//
	//			//Fetching the total Rooms which are already assigned
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
	//			String query=WSClient.getQuery("OWSAvailability","QS_02");
	//			System.out.println(query);
	//			List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
	//			String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
	//			int dId1=Integer.parseInt(docId1);
	//			int documentId = dId1;
	//			if(noOfSoldRoomsListMap.size() > 1)
	//			{
	//				String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
	//				System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
	//				int dId2=Integer.parseInt(docId2);
	//				documentId = dId1+dId2;
	//			}
	//
	//
	//			//Fetching Total Number of Rooms which are available for the property.
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
	//			String query1=WSClient.getQuery("OWSAvailability","QS_03");
	//			int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
	//			//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));
	//
	//
	//			//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
	//			if(documentId1-documentId==0)
	//
	//			{
	//				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//
	//				String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
	//				String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	//				if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
	//				{
	//					String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	//					WSClient.setData("{var_RoomNo}", roomNumber);
	//
	//				}
	//				else
	//				{
	//					WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
	//				}
	//			}
	//			else
	//			{
	//				WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
	//				WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));
	//
	//				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	//				WSClient.setData("{var_oresort}", owsresort);
	//				String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_11");
	//				String res_availability = WSClient.processSOAPMessage(req_availability);
	//
	//				if(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
	//				{
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
	//					WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode", exRate, false);
	//
	//					String query2=WSClient.getQuery("QS_13");
	//					String intRate=WSClient.getDBRow(query2).get("ratePlanCode1");
	//					WSClient.setData("{var_orate}", intRate);
	//					String query3=WSClient.getQuery("QS_09");
	//					String desc1=(WSClient.getDBRow(query3).get("DESCRIPTION"));
	//					String rPlanCode1=WSClient.getDBRow(query3).get("RATE_CODE");
	//					WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode", rPlanCode1, false);
	//					String resDesc1 = (WSClient.getElementValue(res_availability, "RatePlan_RatePlanDescription_Text", XMLType.RESPONSE)).trim();
	//					if(WSAssert.assertEquals(desc1, resDesc1, true))
	//					{
	//						WSClient.writeToReport(LogStatus.PASS, "Expected Description: "+desc1+"	Actual Description: "+resDesc1);
	//					}
	//					else
	//					{
	//						WSClient.writeToReport(LogStatus.FAIL, "Expected Description: "+desc1+"	Actual Description: "+resDesc1);
	//					}
	//
	//					String room = WSClient.getElementValue(req_availability,"AvailRequestSegment_RoomStayCandidates_RoomStayCandidate_roomTypeCode",XMLType.REQUEST);
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Information</b>");
	//					WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RoomRates_RoomRate_roomTypeCode", room, false);
	//
	//
	//					String query6=WSClient.getQuery("QS_06");
	//					String descrt=WSClient.getDBRow(query6).get("SHORT_DESCRIPTION");
	//					WSAssert.assertIfElementValueEquals(res_availability,"RoomType_RoomTypeDescription_Text", descrt, false);
	//
	//					if(WSAssert.assertIfElementExists(res_availability, "RatePlans_RatePlan_GuaranteeDetails", true))
	//					{
	//						String guaranteeType1 = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
	//						WSClient.setData("{var_gCode}", guaranteeType1);
	//						String dbDesc = WSClient.getDBRow(WSClient.getQuery("QS_11")).get("SHORT_DESCRIPTION");
	//						String channelGuaranteeType1 = OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01");
	//						String gdesc = WSClient.getElementValueByAttribute(res_availability, "Guarantee_GuaranteeDescription_Text", "RatePlan_GuaranteeDetails_Guarantee_guaranteeType", channelGuaranteeType1, XMLType.RESPONSE);
	//						if(WSAssert.assertEquals(dbDesc, gdesc, true))
	//						{
	//							WSClient.writeToReport(LogStatus.PASS, "Expected Description: "+dbDesc+"	Actual Description: "+gdesc);
	//						}
	//						else
	//						{
	//							WSClient.writeToReport(LogStatus.FAIL, "Expected Description: "+dbDesc+"	Actual Description: "+gdesc);
	//						}
	//					}
	//				}
	//			}
	//		}
	//		catch (Exception e)
	//		{
	//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//		}
	//	}
	//
	// Minimum Regression Test Case	: When an Invalid Hotel Code is passed in the request
	@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	public void availability_41790()
	{
		try
		{
			String testName = "availability_41790";
			WSClient.startTest(testName, "Verify that availability is falied when an Invalid Hotel Code is passed in the request", "minimumRegression");

			//Fetching required values and setting required variables.
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			String channel=OWSLib.getChannel();
			//String channel = "7PROPC";
			WSClient.setData("{var_channel}", channel);
			String resort = OPERALib.getResort();
			//String owsresort = OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			//String channelCarrier = "IDC7TEST";
			WSClient.setData("{var_channelCarrier}", channelCarrier);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));



			//Fetching External value of the Rate Code, Room Type from Config file
			String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_rt}", rt);
			WSClient.setData("{var_rate}", exRate);

			// Creating request and processing response for OWSAvailable Operation
			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_12");
			String res_availability = WSClient.processSOAPMessage(req_availability);

			// Validating the resultStatusFlag
			if(WSAssert.assertIfElementExists(res_availability, "AvailabilityResponse_Result_resultStatusFlag", true))
			{
				if(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag","FAIL", false))
				{
					WSClient.writeToReport(LogStatus.PASS, "Availability failed!");
				}
				else
				{
					WSClient.writeToReport(LogStatus.FAIL, "Availability should have failed!");
				}
			}

			//Checking the TextElement
			if(WSAssert.assertIfElementExists(res_availability, "Result_Text_TextElement",true))
			{
				String message=WSAssert.getElementValue(res_availability, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Guest Requests response is :"+ message+"</b>");
			}

			//Validating the Error Message
			if(WSAssert.assertIfElementExists(res_availability, "AvailabilityResponse_Result_OperaErrorCode",true))
			{
				String code = WSAssert.getElementValue(res_availability, "AvailabilityResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if((WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_OperaErrorCode", "INVALID_PROPERTY", true))||(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_OperaErrorCode", "PROPERTY_NOT_VALID", true)))
				{
					WSClient.writeToReport(LogStatus.PASS, "<b>"+"The error displayed in the response is :"+ code+"</b>");
				}
				else
				{
					WSClient.writeToReport(LogStatus.FAIL, "<b>"+"The error displayed in the response is :"+ code+"</b>");
				}
			}
		}
		catch (Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	// Minimum Regression Test Case	: Detail Availability for given Rate Code and Room Type
	@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	public void availability_41789()
	{
		try
		{
			String testName = "availability_41789";
			WSClient.startTest(testName, "Verify that rooms are available for the given rate code and the roomtype in case of Detail availability", "minimumRegression");

			//Fetching required values and setting required variables.
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_oresort}", resortExtValue);
			WSClient.setData("{var_chain}",OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_oresort}", resortExtValue);
			String channel=OWSLib.getChannel();
			//String channel = "7PROPC";
			WSClient.setData("{var_channel}", channel);
			String resort = OPERALib.getResort();
			String owsresort = OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			//String channelCarrier = "IDC7TEST";
			WSClient.setData("{var_channelCarrier}", channelCarrier);

			//Fetching External value of the Rate Code, Room Type from Config file
			String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08");
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
			//String exRate = "CKRC";
			//String rt = "3RT";
			WSClient.setData("{var_rt}", rt);
			WSClient.setData("{var_rate}", exRate);
			int blocked=0;

			//Fetching Room Category for the Room Type
			WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
			String query4=WSClient.getQuery("OWSAvailability","QS_05");
			String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
			WSClient.setData("{var_rc}", rc);

			//Fetching the total Rooms which are already assigned
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
			String query=WSClient.getQuery("OWSAvailability","QS_02");
			System.out.println(query);
			List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
			int documentId;
			if(noOfSoldRoomsListMap.size()==0)
				documentId=0;
			else{
				String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
				int dId1=Integer.parseInt(docId1);
				documentId = dId1;
				if(noOfSoldRoomsListMap.size() > 1)
				{
					String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
					System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
					int dId2=Integer.parseInt(docId2);
					documentId = dId1+dId2;
				}
			}

			//Fetching Total Number of Rooms which are available for the property.
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
			String query1=WSClient.getQuery("OWSAvailability","QS_03");
			int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
			//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));


			//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
			if(documentId1-documentId<0)

			{
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
				OPERALib.setOperaHeader(uname);
				String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
				String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

				if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
				{
					String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
					WSClient.setData("{var_RoomNo}", roomNumber);

				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
					blocked=1;
				}
			}
			if(blocked!=1)
			{
				WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
				WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_11");
				String res_availability = WSClient.processSOAPMessage(req_availability);

				if(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
				{
					String query2=WSClient.getQuery("QS_13");
					String intRate=WSClient.getDBRow(query2).get("ratePlanCode1");
					WSClient.setData("{var_orate}", intRate);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
					LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();
					xpath.put("RATE_CODE",WSClient.getElementValue(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode",XMLType.RESPONSE));
					xpath.put("RATE_CATEGORY",WSClient.getElementValue(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCategory",XMLType.RESPONSE));
					xpath.put("DESCRIPTION",WSClient.getElementValue(res_availability,"RatePlan_RatePlanDescription_Text",XMLType.RESPONSE).trim());
					WSAssert.assertEquals(WSClient.getDBRow(WSClient.getQuery("QS_20")),xpath,false);

					List<LinkedHashMap<String,String>> resValues=new ArrayList<LinkedHashMap<String,String>>();
					HashMap<String,String> xPath=new HashMap<String,String>();
					xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType", "RatePlan_AdditionalDetails_AdditionalDetail");
					xPath.put("AdditionalDetail_AdditionalDetailDescription_Text", "RatePlan_AdditionalDetails_AdditionalDetail");

					WSClient.setData("{var_businessdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
					LinkedHashMap<String,String> cancelValues=WSClient.getDBRow(WSClient.getQuery("QS_18"));
					LinkedHashMap<String,String> depositValues=WSClient.getDBRow(WSClient.getQuery("QS_19"));

					String cancelDate=WSClient.getElementValue(res_availability, "RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);

					String depositAmount=WSClient.getElementValue(res_availability, "RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);

					String dueDate=WSClient.getElementValue(res_availability, "RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
					resValues=WSClient.getMultipleNodeList(res_availability, xPath,true, XMLType.RESPONSE);
					for(int i=0;i<resValues.size();i++){
						if(resValues.get(i).get("DetailType").equals("CancelPolicy")){
							resValues.get(i).put("CancellationDate", cancelDate.substring(0,cancelDate.indexOf('T')));
							resValues.get(i).put("CancellationTime", cancelDate.substring(cancelDate.indexOf('T')+1,cancelDate.length()));
						}
						if(resValues.get(i).get("DetailType").equals("DepositPolicy")){
							resValues.get(i).put("DepositAmount", depositAmount);
							resValues.get(i).put("DueDate", dueDate);
						}
					}

					if(cancelValues.size()>0){
						cancelValues.put("DetailType", "CancelPolicy");
					}
					if(cancelValues.containsKey("Text")){
						cancelValues.put("Text", "Cancel By "+cancelValues.get("Text"));
					}
					if(depositValues.size()>0){
						depositValues.put("DetailType", "DepositPolicy");
					}
					if(depositValues.containsKey("DepositAmount")&&depositValues.containsKey("Text")){
						depositValues.put("Text", "A deposit of "+depositValues.get("DepositAmount")+".00 is due by "+depositValues.get("Text")+" in order to guarantee your reservation.");
					}

					for(int i=0;i<resValues.size();i++){
						if(resValues.get(i).get("DetailType").equals("CancelPolicy")){
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation Policy</b>");
							WSAssert.assertEquals(cancelValues, resValues.get(i), false);
						}
						if(resValues.get(i).get("DetailType").equals("DepositPolicy")){
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit Policy</b>");
							WSAssert.assertEquals(depositValues, resValues.get(i), false);
						}
					}
					String businessdate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
					WSClient.setData("{var_businessDate}", businessdate);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Details</b>");
					List<LinkedHashMap<String,String>> gCodeList=WSClient.getDBRows(WSClient.getQuery("QS_17"));

					LinkedHashMap<String , String> rdata = new LinkedHashMap<>();
					rdata.put("Guarantee_GuaranteeDescription_Text", "RatePlan_GuaranteeDetails_Guarantee");
					rdata.put("RatePlan_GuaranteeDetails_Guarantee_guaranteeType", "RatePlan_GuaranteeDetails_Guarantee");
					//					rdata.put("RatePlan_GuaranteeDetails_Guarantee_cancellationRequired", "RatePlan_GuaranteeDetails_Guarantee");
					//					rdata.put("RatePlan_GuaranteeDetails_Guarantee_creditCardRequired", "RatePlan_GuaranteeDetails_Guarantee");
					//					rdata.put("RatePlan_GuaranteeDetails_Guarantee_mandatoryDeposit", "RatePlan_GuaranteeDetails_Guarantee");
					rdata.put("RatePlan_GuaranteeDetails_Guarantee_requirementCode", "RatePlan_GuaranteeDetails_Guarantee");
					List<LinkedHashMap<String, String>>  actual_gcodeL = new ArrayList<LinkedHashMap<String, String>>();
					actual_gcodeL = WSClient.getMultipleNodeList(res_availability,rdata , false, XMLType.RESPONSE);
					WSAssert.assertEquals(actual_gcodeL, gCodeList,false);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Information</b>");
					String room = WSClient.getElementValue(req_availability,"AvailRequestSegment_RoomStayCandidates_RoomStayCandidate_roomTypeCode",XMLType.REQUEST);
					WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RoomRates_RoomRate_roomTypeCode", room, false);


					String query6=WSClient.getQuery("QS_06");
					String descrt=WSClient.getDBRow(query6).get("SHORT_DESCRIPTION");
					WSAssert.assertIfElementValueEquals(res_availability,"RoomType_RoomTypeDescription_Text", descrt, false);

				}
			}
		}
		catch (Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	//	// Minimum Regression Test Case	: Detail Availability for given Rate Code and Room Type
	//			@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	//			public void availability_41792()
	//			{
	//				try
	//				{
	//					String testName = "availability_41792";
	//					WSClient.startTest(testName, "Verify the expected charges in case of Detail availability", "minimumRegression");
	//
	//					//Fetching required values and setting required variables.
	//					String interfaceName = OWSLib.getChannel();
	//					String resortOperaValue = OPERALib.getResort();
	//					String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
	//					WSClient.setData("{var_profileSource}", interfaceName);
	//					WSClient.setData("{var_resort}", resortOperaValue);
	//					WSClient.setData("{var_chain}",OPERALib.getChain());
	//					WSClient.setData("{var_extResort}", resortExtValue);
	//					WSClient.setData("{var_oresort}", resortExtValue);
	//					String channel=OWSLib.getChannel();
	//					//String channel = "7PROPC";
	//					WSClient.setData("{var_channel}", channel);
	//					String resort = OPERALib.getResort();
	//					String owsresort = OWSLib.getChannelResort(resort, channel);
	//					String uname = OPERALib.getUserName();
	//					String pwd = OPERALib.getPassword();
	//					String channelType = OWSLib.getChannelType(channel);
	//					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//					//String channelCarrier = "IDC7TEST";
	//					WSClient.setData("{var_channelCarrier}", channelCarrier);
	//
	//					//Fetching External value of the Rate Code, Room Type from Config file
	//					String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03");
	//					String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02");
	//					//String exRate = "CKRC";
	//					//String rt = "3RT";
	//					WSClient.setData("{var_rt}", rt);
	//					WSClient.setData("{var_rate}", exRate);
	//					int blocked=0;
	//
	//					//Fetching Room Category for the Room Type
	//					WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
	//					String query4=WSClient.getQuery("OWSAvailability","QS_05");
	//					String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
	//					WSClient.setData("{var_rc}", rc);
	//
	//					//Fetching the total Rooms which are already assigned
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
	//					String query=WSClient.getQuery("OWSAvailability","QS_02");
	//					System.out.println(query);
	//					List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
	//					int documentId;
	//					if(noOfSoldRoomsListMap.size()==0)
	//						documentId=0;
	//					else{
	//					String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
	//					int dId1=Integer.parseInt(docId1);
	//					documentId = dId1;
	//					if(noOfSoldRoomsListMap.size() > 1)
	//					{
	//						String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
	//						System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
	//						int dId2=Integer.parseInt(docId2);
	//						documentId = dId1+dId2;
	//					}
	//					}
	//
	//					//Fetching Total Number of Rooms which are available for the property.
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
	//					String query1=WSClient.getQuery("OWSAvailability","QS_03");
	//					int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
	//					//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));
	//
	//
	//					//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
	//					if(documentId1-documentId==0)
	//
	//					{
	//						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
	//		                OPERALib.setOperaHeader(uname);
	//						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
	//						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	//						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
	//						{
	//							String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	//							WSClient.setData("{var_RoomNo}", roomNumber);
	//
	//						}
	//						else
	//						{
	//							WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
	//							 blocked=1;
	//						}
	//					}
	//					if(blocked!=1)
	//					{
	//						int n=3;
	//						int hours=n*24;
	//						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
	//						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_"+hours+"}").substring(0, 19));
	//
	//						OWSLib.setOWSHeader(uname, pwd,resort, channelType, channelCarrier);
	//						String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_11");
	//						String res_availability = WSClient.processSOAPMessage(req_availability);
	//
	//						if(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
	//						{
	//							String query2=WSClient.getQuery("QS_13");
	//							String intRate=WSClient.getDBRow(query2).get("ratePlanCode1");
	//							WSClient.setData("{var_orate}", intRate);
	//
	//							WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode",WSClient.getDBRow(WSClient.getQuery("QS_20")).get("RATE_CODE"), false);
	//
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Expected Charges</b>");
	//							String totalCharge=WSClient.getElementValue(res_availability, "RoomStayList_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
	//							  List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
	//							  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
	//							  db1.put("RoomRateAndPackages_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							   db1.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							    db=WSClient.getMultipleNodeList(res_availability,db1,false,XMLType.RESPONSE);
	//							  String rate=WSClient.getDBRow(WSClient.getQuery("QS_21")).get("AMOUNT_1");
	//							  for(int i=0;i<n;i++){
	//								    LinkedHashMap<String,String>  values=db.get(i);
	//								    String rateAmount=values.get("mRateAndPackagesChargesAmount1");
	//								   // String totalAmt=values.get("omRateAndPackagesTotalCharges1");
	//								    //String Desc=values.get("AndPackagesChargesDescription1").trim();
	//								    String startDate=values.get("PostingDate1");
	//								    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
	//								    if(WSAssert.assertEquals(rate,rateAmount, true)){
	//										   WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//										  }
	//										  else
	//										   WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//
	//							  }
	//							  int roomrate=Integer.parseInt(rate);
	//							  String totalexp=(roomrate*n)+"";
	//							  WSClient.writeToReport(LogStatus.INFO, "<b>Validating Total Charges</b>");
	//							  if(WSAssert.assertEquals(totalexp,totalCharge, true)){
	//								   WSClient.writeToReport(LogStatus.PASS, "Total  Charge : Expected -> "+ totalexp+" Actual -> "+totalCharge);
	//								  }
	//								  else
	//								   WSClient.writeToReport(LogStatus.FAIL, "Total Charge : Expected -> "+totalexp+" Actual -> "+totalCharge);
	//
	//
	//
	//						}
	//					}
	//				}
	//				catch (Exception e)
	//				{
	//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//				}
	//			}

	@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	public void availability_41791()
	{
		try
		{
			String testName = "availability_41791";
			WSClient.startTest(testName, "Verify that rooms are available for the given roomtype in case of General availability", "minimumRegression");

			//Fetching required values and setting required variables.
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			String channel=OWSLib.getChannel();			// Will fetch the first channel
			//String channel = "IDC7TEST";
			WSClient.setData("{var_channel}", channel);
			String resort = OPERALib.getResort();
			String owsresort = OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			//String channelCarrier = "IDC7TEST";
			WSClient.setData("{var_channelCarrier}", channelCarrier);

			//Fetching External value of the Rate Code, Room Type from Config file
			//String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			//String exRate = "CKRC";
			//String rt = "COWSRT3";
			WSClient.setData("{var_rt}", rt);
			//WSClient.setData("{var_rate}", exRate);


			//Fetching Room Category for the Room Type
			WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
			String query4=WSClient.getQuery("OWSAvailability","QS_15");
			String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
			WSClient.setData("{var_rc}", rc);

			//Fetching the total Rooms which are already assigned
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
			String query=WSClient.getQuery("OWSAvailability","QS_02");
			System.out.println(query);
			List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
			String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
			int dId1=Integer.parseInt(docId1);
			int documentId = dId1;
			if(noOfSoldRoomsListMap.size() > 1)
			{
				String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
				System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
				int dId2=Integer.parseInt(docId2);
				documentId = dId1+dId2;
			}


			//Fetching Total Number of Rooms which are available for the property.
			WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
			String query1=WSClient.getQuery("OWSAvailability","QS_03");
			int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
			//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));


			//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
			if(documentId1-documentId==0)

			{
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
				String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

				if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
				{
					String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
					WSClient.setData("{var_RoomNo}", roomNumber);

				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
				}
			}
			else
			{
				WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
				WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				String req_deleteDoc = WSClient.createSOAPMessage("OWSAvailability", "DS_18");
				String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

				if(WSAssert.assertIfElementValueEquals(res_deleteDoc, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
					//Validating RatePlan Details
					String query2=WSClient.getQuery("QS_16");

					List<LinkedHashMap<String, String>>rplanL = new ArrayList<LinkedHashMap<String, String>>();
					rplanL = WSClient.getDBRows(query2);
					//String intRate= rplanL.get(0).get("ratePlanCode1");

					//WSClient.setData("{var_orate}", intRate);
					//String query3=WSClient.getQuery("QS_09");
					//String rPlanCode1=WSClient.getDBRow(query3).get("RATE_CODE");

					LinkedHashMap<String, String> path1 = new LinkedHashMap<>();
					path1.put("RoomStay_RatePlans_RatePlan_ratePlanCode", "RoomStay_RatePlans_RatePlan");
					List<LinkedHashMap<String, String>> rPlanList = new ArrayList<LinkedHashMap<String, String>>();
					rPlanList = WSClient.getMultipleNodeList(res_deleteDoc, path1, false, XMLType.RESPONSE);
					WSAssert.assertEquals(rPlanList, rplanL, false);
					//WSAssert.assertEquals(rplanL, rPlanList, false);



					//Validating RoomType Details
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type and Available Room Information</b>");
					LinkedHashMap<String , String> rdata = new LinkedHashMap<>();
					rdata.put("RoomStay_RoomTypes_RoomType_roomTypeCode", "RoomStay_RoomTypes_RoomType");
					rdata.put("RoomStay_RoomTypes_RoomType_numberOfUnits", "RoomStay_RoomTypes_RoomType");
					List<LinkedHashMap<String, String>>  actual_roomTypeL = new ArrayList<LinkedHashMap<String, String>>();
					actual_roomTypeL = WSClient.getMultipleNodeList(res_deleteDoc,rdata , false, XMLType.RESPONSE);
					rdata.clear();


					List<LinkedHashMap<String, String>>  expected_roomTypeL = new ArrayList<LinkedHashMap<String, String>>();
					rdata.put("roomTypeCode1", WSClient.getElementValue(req_deleteDoc, "AvailRequestSegment_RoomStayCandidates_RoomStayCandidate_roomTypeCode", XMLType.REQUEST));
					rdata.put("numberOfUnits1", Integer.toString(documentId1-documentId));
					expected_roomTypeL.add(rdata);
					WSAssert.assertEquals(actual_roomTypeL, expected_roomTypeL, false);
				}
			}
		}
		catch (Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
	//
	//
	//			@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	//			public void availability_test()
	//			{
	//				try
	//				{
	//					String testName = "availability_41791";
	//					WSClient.startTest(testName, "BAR", "minimumRegression");
	//
	//					//Fetching required values and setting required variables.
	//					String interfaceName = OWSLib.getChannel();
	//					String resortOperaValue = OPERALib.getResort();
	//					String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
	//					WSClient.setData("{var_profileSource}", interfaceName);
	//					WSClient.setData("{var_resort}", resortOperaValue);
	//					WSClient.setData("{var_chain}",OPERALib.getChain());
	//					WSClient.setData("{var_extResort}", resortExtValue);
	//					String channel=OWSLib.getChannel();			// Will fetch the first channel
	//					//String channel = "IDC7TEST";
	//					WSClient.setData("{var_channel}", channel);
	//					String resort = OPERALib.getResort();
	//					String owsresort = OWSLib.getChannelResort(resort, channel);
	//					String uname = OPERALib.getUserName();
	//					String pwd = OPERALib.getPassword();
	//					String channelType = OWSLib.getChannelType(channel);
	//					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//					//String channelCarrier = "IDC7TEST";
	//					WSClient.setData("{var_channelCarrier}", channelCarrier);
	//
	//
	//
	//					//Fetching Room Category for the Room Type
	////					WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
	////					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
	////					String query4=WSClient.getQuery("OWSAvailability","QS_15");
	////					String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
	////					WSClient.setData("{var_rc}", rc);
	////
	////					//Fetching the total Rooms which are already assigned
	////					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
	////					String query=WSClient.getQuery("OWSAvailability","QS_02");
	////					System.out.println(query);
	////					List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
	////					String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
	////					int dId1=Integer.parseInt(docId1);
	////					int documentId = dId1;
	////					if(noOfSoldRoomsListMap.size() > 1)
	////					{
	////						String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
	////						System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
	////						int dId2=Integer.parseInt(docId2);
	////						documentId = dId1+dId2;
	////					}
	////
	////
	////					//Fetching Total Number of Rooms which are available for the property.
	////					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
	////					String query1=WSClient.getQuery("OWSAvailability","QS_03");
	////					int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
	////					//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));
	////
	////
	////					//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
	////					if(documentId1-documentId==0)
	////
	////					{
	////						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	////
	////						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
	////						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	////
	////						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
	////						{
	////							String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	////							WSClient.setData("{var_RoomNo}", roomNumber);
	////
	////						}
	////						else
	////						{
	////							WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
	////						}
	////					}
	//	//				else
	//					{
	//						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
	//						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));
	//
	//						OWSLib.setOWSHeader(uname, pwd, owsresort, channelType, channelCarrier);
	//						String req_deleteDoc = WSClient.createSOAPMessage("OWSAvailability", "DS_19");
	//						String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);
	//
	//						if(WSAssert.assertIfElementValueEquals(res_deleteDoc, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
	//						{
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
	//							//Validating RatePlan Details
	//							String query2=WSClient.getQuery("QS_16");
	//
	//							List<LinkedHashMap<String, String>>rplanL = new ArrayList<LinkedHashMap<String, String>>();
	//							rplanL = WSClient.getDBRows(query2);
	//							//String intRate= rplanL.get(0).get("ratePlanCode1");
	//
	//							//WSClient.setData("{var_orate}", intRate);
	//							//String query3=WSClient.getQuery("QS_09");
	//							//String rPlanCode1=WSClient.getDBRow(query3).get("RATE_CODE");
	//
	//							LinkedHashMap<String, String> path1 = new LinkedHashMap<>();
	//							path1.put("RoomStay_RatePlans_RatePlan_ratePlanCode", "RoomStay_RatePlans_RatePlan");
	//							List<LinkedHashMap<String, String>> rPlanList = new ArrayList<LinkedHashMap<String, String>>();
	//							rPlanList = WSClient.getMultipleNodeList(res_deleteDoc, path1, false, XMLType.RESPONSE);
	//							WSAssert.assertEquals(rPlanList, rplanL, false);
	//							//WSAssert.assertEquals(rplanL, rPlanList, false);
	//
	//
	//
	//							//Validating RoomType Details
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type and Available Room Information</b>");
	//							LinkedHashMap<String , String> rdata = new LinkedHashMap<>();
	//							rdata.put("RoomStay_RoomTypes_RoomType_roomTypeCode", "RoomStay_RoomTypes_RoomType");
	//							rdata.put("RoomStay_RoomTypes_RoomType_numberOfUnits", "RoomStay_RoomTypes_RoomType");
	//							List<LinkedHashMap<String, String>>  actual_roomTypeL = new ArrayList<LinkedHashMap<String, String>>();
	//							actual_roomTypeL = WSClient.getMultipleNodeList(res_deleteDoc,rdata , false, XMLType.RESPONSE);
	//							rdata.clear();
	//
	//
	//							List<LinkedHashMap<String, String>>  expected_roomTypeL = new ArrayList<LinkedHashMap<String, String>>();
	//							rdata.put("roomTypeCode1", WSClient.getElementValue(req_deleteDoc, "AvailRequestSegment_RoomStayCandidates_RoomStayCandidate_roomTypeCode", XMLType.REQUEST));
	//						//	rdata.put("numberOfUnits1", Integer.toString(documentId1-documentId));
	//							expected_roomTypeL.add(rdata);
	//							WSAssert.assertEquals(actual_roomTypeL, expected_roomTypeL, false);
	//									}
	//					}
	//				}
	//				catch (Exception e)
	//				{
	//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//				}
	//			}
	//			// Minimum Regression Test Case	: Detail Availability for given Rate Code and Room Type
	//			@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	//			public void availability_test1()
	//			{
	//				try
	//				{
	//					String testName = "availability_41789";
	//					WSClient.startTest(testName, "Verify that tax information is not coming in the response when RESV_RULES_POLICIES_FOR_AVAILABILTY=N", "minimumRegression");
	//
	//					//Fetching required values and setting required variables.
	//					String interfaceName = OWSLib.getChannel();
	//					String resortOperaValue = OPERALib.getResort();
	//					String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
	//					WSClient.setData("{var_profileSource}", interfaceName);
	//					WSClient.setData("{var_resort}", resortOperaValue);
	//					WSClient.setData("{var_oresort}", resortExtValue);
	//					WSClient.setData("{var_chain}",OPERALib.getChain());
	//					WSClient.setData("{var_extResort}", resortExtValue);
	//					WSClient.setData("{var_oresort}", resortExtValue);
	//					String channel=OWSLib.getChannel();
	//					//String channel = "7PROPC";
	//					WSClient.setData("{var_channel}", channel);
	//					String resort = OPERALib.getResort();
	//					String owsresort = OWSLib.getChannelResort(resort, channel);
	//					String uname = OPERALib.getUserName();
	//					String pwd = OPERALib.getPassword();
	//					String channelType = OWSLib.getChannelType(channel);
	//					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//					OPERALib.setOperaHeader(uname);
	//					//String channelCarrier = "IDC7TEST";
	//					WSClient.setData("{var_channelCarrier}", channelCarrier);
	//					WSClient.setData("{var_parname}", "RESV_RULES_POLICIES_FOR_AVAILABILTY");
	//					WSClient.setData("{var_param}", "RESV_RULES_POLICIES_FOR_AVAILABILTY");
	//					WSClient.setData("{var_par}", "N");
	//					WSClient.setData("{var_type}", "Boolean");
	//					if(!FetchChannelParameters.fetchChannelParameters("QS_02","PARAMETER_VALUE").equals("N"))
	//						ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","PARAMETER_VALUE");
	//					//Fetching External value of the Rate Code, Room Type from Config file
	//					//String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08");
	//					//String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
	//					String exRate = "CKRC";
	//					String rt = "3RT";
	//					WSClient.setData("{var_rt}", rt);
	//					WSClient.setData("{var_rate}", exRate);
	//					int blocked=0;
	//
	//					//Fetching Room Category for the Room Type
	//					WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
	//					String query4=WSClient.getQuery("OWSAvailability","QS_05");
	//					String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
	//					WSClient.setData("{var_rc}", rc);
	//
	//					//Fetching the total Rooms which are already assigned
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
	//					String query=WSClient.getQuery("OWSAvailability","QS_02");
	//					System.out.println(query);
	//					List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
	//					int documentId;
	//					if(noOfSoldRoomsListMap.size()==0)
	//						documentId=0;
	//					else{
	//					String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
	//					int dId1=Integer.parseInt(docId1);
	//					documentId = dId1;
	//					if(noOfSoldRoomsListMap.size() > 1)
	//					{
	//						String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
	//						System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
	//						int dId2=Integer.parseInt(docId2);
	//						documentId = dId1+dId2;
	//					}
	//					}
	//
	//					//Fetching Total Number of Rooms which are available for the property.
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
	//					String query1=WSClient.getQuery("OWSAvailability","QS_03");
	//					int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
	//					//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));
	//
	//
	//					//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
	//					if(documentId1-documentId==0)
	//
	//					{
	//						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
	//		                OPERALib.setOperaHeader(uname);
	//						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
	//						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	//						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
	//						{
	//							String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	//							WSClient.setData("{var_RoomNo}", roomNumber);
	//
	//						}
	//						else
	//						{
	//							WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
	//							 blocked=1;
	//						}
	//					}
	//					if(blocked!=1)
	//					{
	//						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
	//						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));
	//
	//						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	//						String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_01");
	//						String res_availability = WSClient.processSOAPMessage(req_availability);
	//
	//						if(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
	//						{
	//							String query2=WSClient.getQuery("QS_13");
	//							String intRate=WSClient.getDBRow(query2).get("ratePlanCode1");
	//							WSClient.setData("{var_orate}", intRate);
	//
	//							WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode",WSClient.getDBRow(WSClient.getQuery("QS_20")).get("RATE_CODE"),false);
	//
	//							List<LinkedHashMap<String,String>> resValues=new ArrayList<LinkedHashMap<String,String>>();
	//		                    HashMap<String,String> xPath=new HashMap<String,String>();
	//		                    xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType", "RatePlan_AdditionalDetails_AdditionalDetail");
	//		                    xPath.put("AdditionalDetail_AdditionalDetailDescription_Text", "RatePlan_AdditionalDetails_AdditionalDetail");
	//
	//
	//
	//		                    resValues=WSClient.getMultipleNodeList(res_availability, xPath,true, XMLType.RESPONSE);
	//
	//		                   int pass=1;
	//		                    for(int i=0;i<resValues.size();i++){
	//		                        if(resValues.get(i).get("DetailType").equals("TaxInformation")){
	//		                            WSClient.writeToReport(LogStatus.FAIL, "<b>Tax Info exist in the response</b>");
	//		                            pass=0;
	//		                        }
	//
	//		                    }
	//
	//		                    if(pass==1)
	//		                    {
	//		                    	 WSClient.writeToReport(LogStatus.PASS, "<b>Tax Info doesn'exist in the response</b>");
	//		                    }
	//
	//
	//						}
	//					}
	//				}
	//				catch (Exception e)
	//				{
	//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//				}
	//			}
	//

	// Minimum Regression Test Case	: Detail Availability for given Rate Code and Room Type
	//						@Test(groups = { "minimumRegression","OWS","Availability","Availability" })
	//						public void availability_test2()
	//						{
	//							try
	//							{
	//								String testName = "availability_41789";
	//								WSClient.startTest(testName, "Verify that tax information is coming in the response when RESV_RULES_POLICIES_FOR_AVAILABILTY=Y", "minimumRegression");
	//
	//								//Fetching required values and setting required variables.
	//								String interfaceName = OWSLib.getChannel();
	//								String resortOperaValue = OPERALib.getResort();
	//								String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
	//								WSClient.setData("{var_profileSource}", interfaceName);
	//								WSClient.setData("{var_resort}", resortOperaValue);
	//								WSClient.setData("{var_oresort}", resortExtValue);
	//								WSClient.setData("{var_chain}",OPERALib.getChain());
	//								WSClient.setData("{var_extResort}", resortExtValue);
	//								WSClient.setData("{var_oresort}", resortExtValue);
	//								String channel=OWSLib.getChannel();
	//								//String channel = "7PROPC";
	//								WSClient.setData("{var_channel}", channel);
	//								String resort = OPERALib.getResort();
	//								String owsresort = OWSLib.getChannelResort(resort, channel);
	//								String uname = OPERALib.getUserName();
	//								String pwd = OPERALib.getPassword();
	//								String channelType = OWSLib.getChannelType(channel);
	//								String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//								OPERALib.setOperaHeader(uname);
	//								//String channelCarrier = "IDC7TEST";
	//								WSClient.setData("{var_channelCarrier}", channelCarrier);
	//								WSClient.setData("{var_parname}", "RESV_RULES_POLICIES_FOR_AVAILABILTY");
	//								WSClient.setData("{var_param}", "RESV_RULES_POLICIES_FOR_AVAILABILTY");
	//								WSClient.setData("{var_par}", "Y");
	//								WSClient.setData("{var_type}", "Boolean");
	//								if(!FetchChannelParameters.fetchChannelParameters("QS_02","PARAMETER_VALUE").equals("Y"))
	//									ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","PARAMETER_VALUE");
	//								//Fetching External value of the Rate Code, Room Type from Config file
	//								//String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08");
	//								//String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
	//								String exRate = "CKRC";
	//								String rt = "3RT";
	//								WSClient.setData("{var_rt}", rt);
	//								WSClient.setData("{var_rate}", exRate);
	//								int blocked=0;
	//
	//								//Fetching Room Category for the Room Type
	//								WSClient.setData("{var_busdate2}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 10));
	//								WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the room category for a room type</b>");
	//								String query4=WSClient.getQuery("OWSAvailability","QS_05");
	//								String rc=WSClient.getDBRow(query4).get("ROOM_CATEGORY");
	//								WSClient.setData("{var_rc}", rc);
	//
	//								//Fetching the total Rooms which are already assigned
	//								WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the number of sold inventory</b>");
	//								String query=WSClient.getQuery("OWSAvailability","QS_02");
	//								System.out.println(query);
	//								List<LinkedHashMap<String, String>> noOfSoldRoomsListMap = WSClient.getDBRows(query);
	//								int documentId;
	//								if(noOfSoldRoomsListMap.size()==0)
	//									documentId=0;
	//								else{
	//								String docId1 = noOfSoldRoomsListMap.get(0).get("NUMBER_SOLD");
	//								int dId1=Integer.parseInt(docId1);
	//								documentId = dId1;
	//								if(noOfSoldRoomsListMap.size() > 1)
	//								{
	//									String docId2 = noOfSoldRoomsListMap.get(1).get("NUMBER_SOLD");
	//									System.out.println("Doc1: " + docId1+"Doc2: "+docId2);
	//									int dId2=Integer.parseInt(docId2);
	//									documentId = dId1+dId2;
	//								}
	//								}
	//
	//								//Fetching Total Number of Rooms which are available for the property.
	//								WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the total room inventory: Checking if the rooms are available as only if they are available will they be displayed on the response</b>");
	//								String query1=WSClient.getQuery("OWSAvailability","QS_03");
	//								int documentId1=Integer.parseInt(WSClient.getDBRow(query1).get("COUNT").trim());
	//								//WSClient.writeToReport(LogStatus.INFO, Integer.toString(documentId1));
	//
	//
	//								//If the Total Number of Rooms minus the already assigned rooms which are available for the property equals Zero then create a Room
	//								if(documentId1-documentId==0)
	//
	//								{
	//									WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
	//					                OPERALib.setOperaHeader(uname);
	//									String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
	//									String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	//									if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
	//									{
	//										String roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	//										WSClient.setData("{var_RoomNo}", roomNumber);
	//
	//									}
	//									else
	//									{
	//										WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to create a room****************");
	//										 blocked=1;
	//									}
	//								}
	//								if(blocked!=1)
	//								{
	//									WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
	//									WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19));
	//
	//									OWSLib.setOWSHeader(uname, pwd,resort, channelType, channelCarrier);
	//									String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_01");
	//									String res_availability = WSClient.processSOAPMessage(req_availability);
	//
	//									if(WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
	//									{
	//										String query2=WSClient.getQuery("QS_13");
	//										String intRate=WSClient.getDBRow(query2).get("ratePlanCode1");
	//										WSClient.setData("{var_orate}", intRate);
	//
	//										WSAssert.assertIfElementValueEquals(res_availability,"RoomStay_RatePlans_RatePlan_ratePlanCode",WSClient.getDBRow(WSClient.getQuery("QS_20")).get("RATE_CODE"),false);
	//										List<LinkedHashMap<String,String>> resValues=new ArrayList<LinkedHashMap<String,String>>();
	//					                    HashMap<String,String> xPath=new HashMap<String,String>();
	//					                    xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType", "RatePlan_AdditionalDetails_AdditionalDetail");
	//					                    xPath.put("AdditionalDetail_AdditionalDetailDescription_Text", "RatePlan_AdditionalDetails_AdditionalDetail");
	//
	//
	//
	//					                    resValues=WSClient.getMultipleNodeList(res_availability, xPath,true, XMLType.RESPONSE);
	//
	//					                   int pass=0;
	//					                    for(int i=0;i<resValues.size();i++){
	//					                        if(resValues.get(i).get("DetailType").equals("TaxInformation")){
	//					                            WSClient.writeToReport(LogStatus.PASS, "<b>Tax Info exist in the response</b>");
	//					                            pass=1;
	//					                        }
	//
	//					                    }
	//					                    if(pass==0)
	//					                    	 WSClient.writeToReport(LogStatus.FAIL, "<b>Tax Info doesn't exist in the response</b>");
	//									}
	//								}
	//							}
	//							catch (Exception e)
	//							{
	//								WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//							}
	//						}
	//

	@Test(groups={"sanity","Availability","Availability","OWS","availability_22430"})
	public void availability_22430() {
		String previousValue="REQUESTED_BAR_PUBLIC";
		try {
			String testName = "availability_22430";
			WSClient.startTest(testName, "Verify that the Requested Rate Code details are fetched when the channel parameter REQUESTED_BAR_PUBLIC_RATES is set to 'REQUESTED'", "sanity");
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			String channel=OWSLib.getChannel();         // Will fetch the first channel
			//String channel = "IDC7TEST";
			WSClient.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			//String channelCarrier = "IDC7TEST";
			WSClient.setData("{var_channelCarrier}", channelCarrier);
			String type="String";
			WSClient.setData("{var_type}",type);
			String exRate = OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01");


			WSClient.setData("{var_rate}", exRate);


			WSClient.setData("{var_parname}", "REQUESTED_BAR_PUBLIC_RATES");
			WSClient.setData("{var_par}", "REQUESTED_BAR_PUBLIC_RATES");
			WSClient.setData("{var_param}", "REQUESTED_BAR_PUBLIC_RATES");

			String Parameter1 = FetchChannelParameters.fetchChannelParameters("QS_02","PARAMETER_VALUE");
			previousValue=Parameter1;
			WSClient.writeToReport(LogStatus.INFO, "<b>REQUESTED_BAR_PUBLIC_RATES Parameter previous value "+ Parameter1  + "</b>");
			if (!Parameter1.equals("REQUESTED")) {
				WSClient.setData("{var_par}","REQUESTED");
				Parameter1=ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>REQUESTED_BAR_PUBLIC_RATES Parameter Set to value "+ Parameter1  + "</b>");
			}


			WSClient.setData("{var_ratePlanCode}", exRate);

			setOWSHeader();

			// Creating request and processing response for OWS DeletePayRouting Operation
			String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_05");
			String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);

			//Validating response of OWS Availability
			if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
				//Validating RatePlan Details
				String query2=WSClient.getQuery("QS_04");

				List<LinkedHashMap<String, String>>rplanL = new ArrayList<LinkedHashMap<String, String>>();
				rplanL = WSClient.getDBRows(query2);
				List<LinkedHashMap<String,String>> actualValues = new ArrayList<LinkedHashMap<String,String>>();
				HashMap<String,String> xpath=new HashMap<String,String>();
				xpath.put("RoomStay_RatePlans_RatePlan_ratePlanCode","RoomStay_RatePlans_RatePlan");
				actualValues = WSClient.getMultipleNodeList(AvailabilityRes,xpath,false,XMLType.RESPONSE);
				WSAssert.assertEquals(actualValues,rplanL,false);
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			WSClient.setData("{var_parname}", "REQUESTED_BAR_PUBLIC_RATES");
			WSClient.setData("{var_par}", "REQUESTED_BAR_PUBLIC");
			try {
				ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","REQUESTED_BAR_PUBLIC_RATES");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//
	//		    @Test(groups={"minimumRegression","Availability","Availability","OWS"})
	//		    public void availability_22431() {
	//		        String previousValue="REQUESTED_BAR_PUBLIC";
	//		        try {
	//		        String testname = "availability_22431";
	//		        WSClient.startTest(testName, "Verify that the Requested Rate Code details are fetched when the channel parameter REQUESTED_BAR_PUBLIC_RATES is set to 'REQUESTED_BAR'", "minimumRegression");
	//		         String uname = OPERALib.getUserName();
	//		            OPERALib.setOperaHeader(uname);
	//		            String interfaceName = OWSLib.getChannel();
	//		        String resortOperaValue = OPERALib.getResort();
	//		        String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
	//		        WSClient.setData("{var_profileSource}", interfaceName);
	//		        WSClient.setData("{var_resort}", resortOperaValue);
	//		        WSClient.setData("{var_chain}",OPERALib.getChain());
	//		        WSClient.setData("{var_extResort}", resortExtValue);
	//		        String channel=OWSLib.getChannel();         // Will fetch the first channel
	//		        //String channel = "IDC7TEST";
	//		        WSClient.setData("{var_channel}", channel);
	//		        String channelType = OWSLib.getChannelType(channel);
	//		        String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//		        //String channelCarrier = "IDC7TEST";
	//		        WSClient.setData("{var_channelCarrier}", channelCarrier);
	//		        String type="String";
	//		        WSClient.setData("{var_type}",type);
	//		        String exRate = "CHBAR45";
	//
	//
	//		        WSClient.setData("{var_rate}", exRate);
	//
	//
	//		        WSClient.setData("{var_parname}", "REQUESTED_BAR_PUBLIC_RATES");
	//		        WSClient.setData("{var_par}", "REQUESTED_BAR_PUBLIC_RATES");
	//		        WSClient.setData("{var_param}", "REQUESTED_BAR_PUBLIC_RATES");
	//
	//		        String Parameter1 = FetchChannelParameters.fetchChannelParameters("QS_02","PARAMETER_VALUE");
	//		        previousValue=Parameter1;
	//		        WSClient.writeToReport(LogStatus.INFO, "<b>REQUESTED_BAR_PUBLIC_RATES Parameter current value "+ Parameter1  + "</b>");
	//		        if (!Parameter1.equals("REQUESTED_BAR")) {
	//		            WSClient.setData("{var_par}","REQUESTED_BAR");
	//		            Parameter1=ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","PARAMETER_VALUE");
	//		            WSClient.writeToReport(LogStatus.INFO, "<b>REQUESTED_BAR_PUBLIC_RATES Parameter Set to value "+ Parameter1  + "</b>");
	//		        }
	//
	//
	//		        WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04").substring(3) );
	//
	//		        setOWSHeader();
	//
	//		        // Creating request and processing response for OWS DeletePayRouting Operation
	//		        String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_05");
	//		        String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);
	//
	//		        //Validating response of OWS Availability
	//		        if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
	//		        {
	//		            WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
	//		            String query2=WSClient.getQuery("QS_14");
	//
	//		            List<LinkedHashMap<String, String>>rplanL = new ArrayList<LinkedHashMap<String, String>>();
	//		            rplanL = WSClient.getDBRows(query2);
	//		            List<LinkedHashMap<String,String>> actualValues = new ArrayList<LinkedHashMap<String,String>>();
	//		            HashMap<String,String> xpath=new HashMap<String,String>();
	//		            xpath.put("RoomStay_RatePlans_RatePlan_ratePlanCode","RoomStay_RatePlans_RatePlan");
	//		             actualValues = WSClient.getMultipleNodeList(AvailabilityRes,xpath,false,XMLType.RESPONSE);
	//		             WSAssert.assertEquals(actualValues,rplanL,false);
	//		        }
	//		    }catch(Exception e) {
	//		        WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//		    }finally {
	//		        WSClient.setData("{var_parname}", "REQUESTED_BAR_PUBLIC_RATES");
	//		        WSClient.setData("{var_par}", "REQUESTED_BAR_PUBLIC");
	//		        try {
	//		            ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","REQUESTED_BAR_PUBLIC_RATES");
	//		        } catch (Exception e) {
	//		            e.printStackTrace();
	//		        }
	//		    }
	//		}
	//
	//		    @Test(groups={"minimumRegression","Availability","Availability","OWS"})
	//		    public void availability_22432() {
	//		        String previousValue="REQUESTED_BAR_PUBLIC";
	//		        try {
	//		        String testname = "availability_22432";
	//		        WSClient.startTest(testName, "Verify that the Requested Rate Code details are fetched when the channel parameter REQUESTED_BAR_PUBLIC_RATES is set to 'REQUESTED_BAR_PUBLIC'", "minimumRegression");
	//		         String uname = OPERALib.getUserName();
	//		            OPERALib.setOperaHeader(uname);
	//		            String interfaceName = OWSLib.getChannel();
	//		        String resortOperaValue = OPERALib.getResort();
	//		        String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
	//		        WSClient.setData("{var_profileSource}", interfaceName);
	//		        WSClient.setData("{var_resort}", resortOperaValue);
	//		        WSClient.setData("{var_chain}",OPERALib.getChain());
	//		        WSClient.setData("{var_extResort}", resortExtValue);
	//		        String channel=OWSLib.getChannel();         // Will fetch the first channel
	//		        //String channel = "IDC7TEST";
	//		        WSClient.setData("{var_channel}", channel);
	//		        String channelType = OWSLib.getChannelType(channel);
	//		        String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//		        //String channelCarrier = "IDC7TEST";
	//		        WSClient.setData("{var_channelCarrier}", channelCarrier);
	//		        String type="String";
	//		        WSClient.setData("{var_type}",type);
	//		        String exRate = "CHBAR45";
	//
	//
	//		        WSClient.setData("{var_rate}", exRate);
	//
	//
	//		        WSClient.setData("{var_parname}", "REQUESTED_BAR_PUBLIC_RATES");
	//		        WSClient.setData("{var_par}", "REQUESTED_BAR_PUBLIC_RATES");
	//		        WSClient.setData("{var_param}", "REQUESTED_BAR_PUBLIC_RATES");
	//
	//		        String Parameter1 = FetchChannelParameters.fetchChannelParameters("QS_02","PARAMETER_VALUE");
	//		        previousValue=Parameter1;
	//		        WSClient.writeToReport(LogStatus.INFO, "<b>REQUESTED_BAR_PUBLIC_RATES Parameter current value "+ Parameter1  + "</b>");
	//		        if (!Parameter1.equals("REQUESTED_BAR_PUBLIC")) {
	//		            WSClient.setData("{var_par}","REQUESTED_BAR_PUBLIC");
	//		            Parameter1=ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","PARAMETER_VALUE");
	//		            WSClient.writeToReport(LogStatus.INFO, "<b>REQUESTED_BAR_PUBLIC_RATES Parameter Set to value "+ Parameter1  + "</b>");
	//		        }
	//
	//
	//		        WSClient.setData("{var_ratePlanCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04").substring(3) );
	//
	//		        setOWSHeader();
	//
	//		        // Creating request and processing response for OWS DeletePayRouting Operation
	//		        String AvailabilityReq = WSClient.createSOAPMessage("OWSAvailability", "DS_05");
	//		        String AvailabilityRes = WSClient.processSOAPMessage(AvailabilityReq);
	//
	//		        //Validating response of OWS Availability
	//		        if(WSAssert.assertIfElementValueEquals(AvailabilityRes, "AvailabilityResponse_Result_resultStatusFlag","SUCCESS", false))
	//		        {
	//		            WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Information</b>");
	//		            //Validating RatePlan Details
	//		            String query2=WSClient.getQuery("QS_15");
	//
	//		            List<LinkedHashMap<String, String>>rplanL = new ArrayList<LinkedHashMap<String, String>>();
	//		            rplanL = WSClient.getDBRows(query2);
	//		            List<LinkedHashMap<String,String>> actualValues = new ArrayList<LinkedHashMap<String,String>>();
	//		            HashMap<String,String> xpath=new HashMap<String,String>();
	//		            xpath.put("RoomStay_RatePlans_RatePlan_ratePlanCode","RoomStay_RatePlans_RatePlan");
	//		             actualValues = WSClient.getMultipleNodeList(AvailabilityRes,xpath,false,XMLType.RESPONSE);
	//		             WSAssert.assertEquals(actualValues,rplanL,false);
	//		        }
	//		    }catch(Exception e) {
	//		        WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//		    }finally {
	//		        WSClient.setData("{var_parname}", "REQUESTED_BAR_PUBLIC_RATES");
	//		        WSClient.setData("{var_par}", "REQUESTED_BAR_PUBLIC");
	//		        try {
	//		            ChangeChannelParameters.changeChannelParameters("DS_01","QS_02","REQUESTED_BAR_PUBLIC_RATES");
	//		        } catch (Exception e) {
	//		            e.printStackTrace();
	//		        }
	//		    }
	//		}
}
