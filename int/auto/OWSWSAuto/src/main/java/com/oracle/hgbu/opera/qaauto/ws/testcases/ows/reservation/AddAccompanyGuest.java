package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class AddAccompanyGuest extends WSSetUp {

	public void setOwsHeader() throws Exception{
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}

	public String changeParameter="";
	public boolean changeParameterFlag=false;

	public void accompanyGuestParameterCheck() throws Exception {

		OPERALib.setOperaHeader(OPERALib.getUserName());
		WSClient.setData("{var_chain}", OPERALib.getChain());
		WSClient.setData("{var_parameter}","ACCOMPANYING_GUEST");
		String sd = FetchApplicationParameters.getApplicationParameter("DS_01");

		WSClient.writeToReport(LogStatus.INFO,"<b>Accompany Guest Parameter : "+sd+"</b>");

		if(!sd.equals("Y")) {
			changeParameter=sd;
			WSClient.setData("{var_settingValue}", "Y");
			String sd1=ChangeApplicationParameters.changeApplicationParameter("DS_01", "");
			WSClient.writeToReport(LogStatus.INFO,"<b>Accompany Guest Parameter : "+sd1+"</b>");
			changeParameterFlag=true;
		}
	}

	public void revertaccompanyGuestParameter() throws Exception {
		if(changeParameterFlag) {
			WSClient.setData("{var_settingValue}", changeParameter);
			ChangeApplicationParameters.changeApplicationParameter("DS_01", "");
			changeParameterFlag=false;
		}
	}


	@Test(groups = { "sanity", "AddAccompanyGuest", "OWS", "Reservation" })
	public void AddAccompanyGuest_39561() {
		try {
			String testName = "AddAccompanyGuest_39561";
			WSClient.startTest(testName, "Verify that Accompany guest is added to a reservation when confirmation number is passed", "sanity");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				accompanyGuestParameterCheck();

				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
					WSClient.setData("{var_profileId}", profileId);
					HashMap<String, String> reservationHashMap = CreateReservation.createReservation("DS_01");
					String reservationId = reservationHashMap.get("reservationId");
					String confirmationId = reservationHashMap.get("confirmationId");

					if(!reservationId.equals("error")){

						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_resvId}", reservationId);

						profileId = CreateProfile.createProfile("DS_01");
						if(!profileId.equals("error")){
							WSClient.setData("{var_profileId}", profileId);

							setOwsHeader();

							WSClient.writeToReport(LogStatus.INFO, "<b>Creating Accompany Guest profile.</b>");
							profileId = CreateProfile.createProfile("DS_01");
							WSClient.setData("{var_profileId}", profileId);
							
							String accompanyGuestReq=WSClient.createSOAPMessage("OWSAddAccompanyGuest", "DS_01");

							String accompanyGuestRes=WSClient.processSOAPMessage(accompanyGuestReq);

							if(WSAssert.assertIfElementValueEquals(accompanyGuestRes, "AddAccompanyGuestResponse_Result_resultStatusFlag", "SUCCESS", false)){

								String db_name_id=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("NAME_ID");

								if (WSAssert.assertEquals(db_name_id,
										WSClient.getData("{var_profileId}"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Accompany Guest is added successfully, Expected : "+WSClient.getData("{var_profileId}")+" Actual :"+db_name_id);
								} else {

									WSClient.writeToReport(LogStatus.FAIL,
											"Accompany Guest is not added successfully, Expected : "+WSClient.getData("{var_profileId}")+" Actual :"+db_name_id);
								}
							}
						}
					}

				}
			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR, e.getMessage());
		// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
		// to:"+e);
		} finally {
			try {
				revertaccompanyGuestParameter();
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "AddAccompanyGuest", "OWS","Reservation" })
	public void AddAccompanyGuest_38692() {
		try {
			String testName = "AddAccompanyGuest_38692";
			WSClient.startTest(testName, "Verify that Accompany guest is added to a reservation for a given confirmation number with multiple leg number", "minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				accompanyGuestParameterCheck();

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
					WSClient.setData("{var_profileId}", profileId);

					HashMap<String, String> reservationHashMap = CreateReservation.createReservation("DS_01");

					String reservationId = reservationHashMap.get("reservationId");

					String confirmationId = reservationHashMap.get("confirmationId");

					if(!reservationId.equals("error")){
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_resvId}", reservationId);

						profileId = CreateProfile.createProfile("DS_01");

						if(!profileId.equals("error")) {
							WSClient.setData("{var_profileId}", profileId);

							reservationId = CreateReservation.createReservation("DS_10").get("reservationId");

							if(!reservationId.equals("error")) {
								WSClient.setData("{var_resvId}", reservationId);

								WSClient.writeToReport(LogStatus.INFO,WSClient.getQuery("OWSAddAccompanyGuest","QS_02"));

								WSClient.setData("{var_legNum}", WSClient.getDBRow(WSClient.getQuery("OWSAddAccompanyGuest","QS_02")).get("CONFIRMATION_LEG_NO"));

								setOwsHeader();
								String accompanyGuestReq=WSClient.createSOAPMessage("OWSAddAccompanyGuest", "DS_02");

								String accompanyGuestRes=WSClient.processSOAPMessage(accompanyGuestReq);

								if(WSAssert.assertIfElementValueEquals(accompanyGuestRes, "AddAccompanyGuestResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									String db_name_id=WSClient.getDBRow(WSClient.getQuery("QS_06")).get("NAME_ID");

									if (WSAssert.assertEquals(db_name_id, WSClient.getData("{var_profileId}"), true)) {
										System.out.println("Expected : " + db_name_id + " Actual :" + WSClient.getData("{var_profileId}"));
										WSClient.writeToReport(LogStatus.PASS, "Accompany Guest is added successfully, Expected : "+db_name_id+" Actual :"+WSClient.getData("{var_profileId}"));
									} else {
										System.out.println("Expected : " + db_name_id + " Actual :" + WSClient.getData("{var_profileId}"));
										WSClient.writeToReport(LogStatus.FAIL, "Accompany Guest is not added successfully, Expected : "+db_name_id+" Actual :"+WSClient.getData("{var_profileId}"));
									}

								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.getMessage());
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			try {
				revertaccompanyGuestParameter();
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	//@Test(groups = { "minimumRegression", "AddAccompanyGuest", "OWS" }) --> Invalid scenario 
	public void AddAccompanyGuest_38693() {
		try {
			String testName = "AddAccompanyGuest_38693";
			WSClient.startTest(testName, "Verify that FAIL result Status Flag has been populated when the profile is being attached as an accompany guest to itself", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				accompanyGuestParameterCheck();

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")) {
					WSClient.setData("{var_profileId}", profileId);

					HashMap<String, String> reservationHashMap = CreateReservation.createReservation("DS_01");

					String reservationId = reservationHashMap.get("reservationId");

					String confirmationId = reservationHashMap.get("confirmationId");

					if(!reservationId.equals("error")) {

						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_resvId}", reservationId);

						setOwsHeader();

						String accompanyGuestReq = WSClient.createSOAPMessage("OWSAddAccompanyGuest", "DS_01");

						String accompanyGuestRes = WSClient.processSOAPMessage(accompanyGuestReq);

						WSAssert.assertIfElementValueEquals(accompanyGuestRes, "AddAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.getMessage());
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		}
		finally {
			// ExtentReport.endExtentTest();
			try {
				revertaccompanyGuestParameter();
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "AddAccompanyGuest", "OWS", "Reservation" })
	public void AddAccompanyGuest_38694() {
		try {
			String testName = "AddAccompanyGuest_38694";
			WSClient.startTest(testName, "Verify that Accompany Guest is created and added to a reservation by sending profile details in AddAccompany guest request itself.", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				accompanyGuestParameterCheck();

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
					WSClient.setData("{var_profileId}", profileId);
					HashMap<String, String> reservationHashMap = CreateReservation.createReservation("DS_01");

					String reservationId = reservationHashMap.get("reservationId");

					String confirmationId = reservationHashMap.get("confirmationId");

					if(!reservationId.equals("error")){

						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_resvId}", reservationId);
						setOwsHeader();

						String accompanyGuestReq=WSClient.createSOAPMessage("OWSAddAccompanyGuest", "DS_03");

						String accompanyGuestRes=WSClient.processSOAPMessage(accompanyGuestReq);

						if(WSAssert.assertIfElementValueEquals(accompanyGuestRes, "AddAccompanyGuestResponse_Result_resultStatusFlag", "SUCCESS", false)){

							String db_resv_count=WSClient.getDBRow(WSClient.getQuery("QS_03")).get("COUNT");
							String db_name_id=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("NAME_ID");
							WSClient.setData("{var_profileId}", db_name_id);
							String db_name_count=WSClient.getDBRow(WSClient.getQuery("QS_04")).get("COUNT");

							if (WSAssert.assertEquals(db_resv_count,
									"1", true) && WSAssert.assertEquals(db_name_count,
											"1", true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Accompany Guest is createrd with profile Id ("+db_name_id+") and successfully added to the reservation with reservationID ("+reservationId+").");
							} else {

								WSClient.writeToReport(LogStatus.FAIL, "Failure in creating and adding a Accompany Guest");
							}
						}
					}
				}
			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR, e.getMessage());
		// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
		// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			try {
				revertaccompanyGuestParameter();
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	@Test(groups = { "minimumRegression","AddAccompanyGuest" ,"Reservation","OWS"})
	public void AddAccompanyGuest_38695() {
		try{
			String testName = "AddAccompanyGuest_38695";
			WSClient.startTest(testName, "Verify that FAIL result Status Flag has been populated when Accompany Guest is added against a canceled reservation", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String username = OPERALib.getUserName();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			accompanyGuestParameterCheck();

			OPERALib.setOperaHeader(username);

			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
				WSClient.setData("{var_profileId}", operaProfileID);
				if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					HashMap<String, String> reservationHashMap = CreateReservation.createReservation("DS_01");

					String reservationId = reservationHashMap.get("reservationId");
					String confirmationId = reservationHashMap.get("confirmationId");
					String ConfirmationId = reservationHashMap.get("confirmationId");

					if(!reservationId.equals("error")){
						WSClient.setData("{var_confirmationId}", ConfirmationId);
						WSClient.setData("{var_reservation_id}",reservationId);

						if (reservationId != null && reservationId != "") {
							String cancelReservationReq = WSClient.createSOAPMessage("CancelReservation", "DS_01");
							String cancelReservationRes = WSClient.processSOAPMessage(cancelReservationReq);

							if (WSAssert.assertIfElementExists(cancelReservationRes, "CancelReservationRS_Success", true)) {
								WSClient.setData("{var_confirmationId}", confirmationId);
								WSClient.setData("{var_resvId}", reservationId);

								String profileId = CreateProfile.createProfile("DS_01");
								if(!profileId.equals("error")) {
									WSClient.setData("{var_profileId}", profileId);

									setOwsHeader();
									String accompanyGuestReq=WSClient.createSOAPMessage("OWSAddAccompanyGuest", "DS_01");

									String accompanyGuestRes=WSClient.processSOAPMessage(accompanyGuestReq);

									WSAssert.assertIfElementValueEquals(accompanyGuestRes, "AddAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

								}
							}
							else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation is not cancelled");
							}
						}
					}
				}
			}
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		finally{
			try {
				revertaccompanyGuestParameter();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}


	@Test(groups = { "minimumRegression", "AddAccompanyGuest", "OWS","Reservation" })
	public void AddAccompanyGuest_38696() {
		try {
			String testName = "AddAccompanyGuest_38696";
			WSClient.startTest(testName, "Verify that FAIL result Status Flag has been populated when Invalid Accompany Guest is added to reservation", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				accompanyGuestParameterCheck();

				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
					WSClient.setData("{var_profileId}", profileId);

					String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
					String createResvRes = WSClient.processSOAPMessage(createResvReq);

					if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) {

						String reservationId = WSClient.getElementValueByAttribute(createResvRes, "Reservation_ReservationIDList_UniqueID_ID",
								"Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);

						String confirmationId = WSClient.getElementValueByAttribute(createResvRes, "Reservation_ReservationIDList_UniqueID_ID",
								"Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);

						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_resvId}", reservationId);

						String invalidProfileId=WSClient.getDBRow(WSClient.getQuery("OWSAddAccompanyGuest", "QS_05")).get("PROFILEID");

						WSClient.writeToReport(LogStatus.INFO, "Testing with profile Id : "+invalidProfileId);

						WSClient.setData("{var_profileId}", invalidProfileId);

						setOwsHeader();

						String accompanyGuestReq=WSClient.createSOAPMessage("OWSAddAccompanyGuest", "DS_01");

						String accompanyGuestRes=WSClient.processSOAPMessage(accompanyGuestReq);


						if(WSAssert.assertIfElementValueEquals(accompanyGuestRes, "AddAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false)){

							WSClient.writeToReport(LogStatus.PASS, WSClient.getElementValue(accompanyGuestRes, "AddAccompanyGuestResponse_Result_GDSError", XMLType.RESPONSE));

						}
					}
					else{
						WSClient.writeToReport(LogStatus.WARNING, "Create Reservation Pre Requisite Blocked");
					}
				}
			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR, e.getMessage());
		// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
		// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			try {
				revertaccompanyGuestParameter();
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "AddAccompanyGuest", "OWS","Reservation" })
	public void AddAccompanyGuest_38697() {
		try {
			String testName = "AddAccompanyGuest_38697";
			WSClient.startTest(testName, "Verify that FAIL result Status Flag has been populated when Accompany Guest profile Id is not sent in the reques", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				accompanyGuestParameterCheck();

				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
					WSClient.setData("{var_profileId}", profileId);

					String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
					String createResvRes = WSClient.processSOAPMessage(createResvReq);

					if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) {

						String reservationId = WSClient.getElementValueByAttribute(createResvRes, "Reservation_ReservationIDList_UniqueID_ID",
								"Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);

						String confirmationId = WSClient.getElementValueByAttribute(createResvRes, "Reservation_ReservationIDList_UniqueID_ID",
								"Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);

						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_resvId}", reservationId);

						setOwsHeader();

						String accompanyGuestReq=WSClient.createSOAPMessage("OWSAddAccompanyGuest", "DS_04");

						String accompanyGuestRes=WSClient.processSOAPMessage(accompanyGuestReq);

						if(WSAssert.assertIfElementValueEquals(accompanyGuestRes, "AddAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false)){
							WSClient.writeToReport(LogStatus.PASS, WSClient.getElementValue(accompanyGuestRes, "AddAccompanyGuestResponse_Result_GDSError", XMLType.RESPONSE));
						}
					}
					else{
						WSClient.writeToReport(LogStatus.WARNING, "Create Reservation Pre Requisite Blocked");
					}
				}

			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR, e.getMessage());
		// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
		// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			try {
				revertaccompanyGuestParameter();
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
