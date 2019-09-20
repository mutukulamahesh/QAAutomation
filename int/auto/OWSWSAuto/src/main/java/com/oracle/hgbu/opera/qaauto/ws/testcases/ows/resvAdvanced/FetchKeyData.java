package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchKeyData extends WSSetUp {

	
	public void setOwsHeader() throws Exception{
        String resort = OPERALib.getResort();
       String channel = OWSLib.getChannel();
       String uname = OPERALib.getUserName();
       String pwd = OPERALib.getPassword();
       String channelType = OWSLib.getChannelType(channel);
       String channelCarrier = OWSLib.getChannelCarier(resort, channel);
       OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
   }
	
	
//	@Test(groups = { "minimumRegression", "FetchKeyData", "OWS" })
//	public void fetchKeyData_60004() {
//		try {
//			String testName = "fetchKeyData_60004";
//			WSClient.startTest(testName, "Verify that the minimum information about the KeyTrack (key2Track) is retrieved onto the response with channel where channel and carrier name are different", "minimumRegression");
//			if (OperaPropConfig
//					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
//
//				String resortOperaValue = OPERALib.getResort();
//				String interfaceName = OWSLib.getChannel(3);
//
//				OPERALib.setOperaHeader(OPERALib.getUserName());
//				String resort = OPERALib.getResort();
//
//				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
//
//				WSClient.setData("{var_profileSource}", interfaceName);
//				WSClient.setData("{var_resort}", resortOperaValue);
//				WSClient.setData("{var_extResort}", resortExtValue);
//				WSClient.setData("{var_profileSource}", interfaceName);
//				WSClient.setData("{var_resort}", resortOperaValue);
//				WSClient.setData("{var_extResort}", resortExtValue);
//
//				OPERALib.setOperaHeader(OPERALib.getUserName());
//
//				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//				// WSClient.setData("{var_roomNumber}",
//				// WSClient.getDataSetForCode("Rooms", "DS_02"));
//
//				String profileId = CreateProfile.createProfile("DS_01");
//				if(!profileId.equals("error")){
//						WSClient.setData("{var_profileId}", profileId);
//
//
//						String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
//						if(!reservationId.equals("error")){
//							WSClient.setData("{var_resvId}", reservationId);
//							
//							WSClient.setData("{var_keyTrack2}", "21140000000000000005");
//							
//						       String channel = OWSLib.getChannel(3);
//						       String uname = OPERALib.getUserName();
//						       String pwd = OPERALib.getPassword();
//						       String channelType = OWSLib.getChannelType(channel);
//						       String channelCarrier = OWSLib.getChannelCarier(resort, channel);
//						       OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
//							String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
//							String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
//							if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", true)) {
//
//								if (WSAssert.assertIfElementValueEquals(res_setKeyData,
//										"SetKeyDataResponse_Result_resultStatusFlag", "SUCCESS", true)) {
//
//									String req_fetchKeyData = WSClient.createSOAPMessage("OWSFetchKeyData", "DS_01");
//									String res_fetchKeyData = WSClient.processSOAPMessage(req_fetchKeyData);
//									if (WSAssert.assertIfElementValueEquals(res_fetchKeyData,
//											"FetchKeyDataResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//
//										LinkedHashMap<String, String> keyTrackDb = WSClient
//												.getDBRow(WSClient.getQuery("OWSSetKeyData", "QS_01"));
//
//										if (WSAssert.assertEquals(keyTrackDb.get("TRACK2"),
//												WSClient.getElementValue(res_fetchKeyData,
//														"FetchKeyDataResponse_KeyTrack_Key2Track", XMLType.RESPONSE),
//												true)) {
//											WSClient.writeToReport(LogStatus.PASS,
//													"Key2Track - " + "Expected: " + keyTrackDb.get("TRACK2")
//															+ ",Actual: "
//															+ WSClient.getElementValue(res_fetchKeyData,
//																	"FetchKeyDataResponse_KeyTrack_Key2Track",
//																	XMLType.RESPONSE));
//										} else {
//
//											WSClient.writeToReport(LogStatus.FAIL,
//													"Key2Track - " + "Expected: " + keyTrackDb.get("TRACK2")
//															+ ",Actual: "
//															+ WSClient.getElementValue(res_fetchKeyData,
//																	"FetchKeyDataResponse_KeyTrack_Key2Track",
//																	XMLType.RESPONSE));
//										}
//
//									}
//								}
//								else{
//									WSClient.writeToReport(LogStatus.WARNING,
//											"************Blocked : Unable to Set Key Data****************");
//								}
//							}
//							else{
//								WSClient.writeToReport(LogStatus.WARNING,
//										"************Blocked : Unable to Set Key Data****************");
//							}
//
//						} 
//					} 
//			}
//		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
//			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
//			// to:"+e);
//		} finally {
//			// ExtentReport.endExtentTest();
//			 try {
//                 if(!WSClient.getData("{var_resvId}").equals(""))
//                    CancelReservation.cancelReservation("DS_02");
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//			
//		}
//
//	}
	@Test(groups = { "sanity", "FetchKeyData", "OWS" })
	public void fetchKeyData_39560() {
		try {
			String testName = "fetchKeyData_39560";
			WSClient.startTest(testName, "Verify that the minimum information about the KeyTrack (key2Track) is retrieved onto the response", "sanity");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
						WSClient.setData("{var_profileId}", profileId);


						String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
						if(!reservationId.equals("error")){
							WSClient.setData("{var_resvId}", reservationId);
							
							WSClient.setData("{var_keyTrack2}", "21140000000000000005");
							setOwsHeader();

							String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
							String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
							if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", true)) {

								if (WSAssert.assertIfElementValueEquals(res_setKeyData,
										"SetKeyDataResponse_Result_resultStatusFlag", "SUCCESS", true)) {

									String req_fetchKeyData = WSClient.createSOAPMessage("OWSFetchKeyData", "DS_01");
									String res_fetchKeyData = WSClient.processSOAPMessage(req_fetchKeyData);
									if (WSAssert.assertIfElementValueEquals(res_fetchKeyData,
											"FetchKeyDataResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										LinkedHashMap<String, String> keyTrackDb = WSClient
												.getDBRow(WSClient.getQuery("OWSSetKeyData", "QS_01"));

										if (WSAssert.assertEquals(keyTrackDb.get("TRACK2"),
												WSClient.getElementValue(res_fetchKeyData,
														"FetchKeyDataResponse_KeyTrack_Key2Track", XMLType.RESPONSE),
												true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"Key2Track - " + "Expected: " + keyTrackDb.get("TRACK2")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key2Track",
																	XMLType.RESPONSE));
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Key2Track - " + "Expected: " + keyTrackDb.get("TRACK2")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key2Track",
																	XMLType.RESPONSE));
										}

									}
								}
								else{
									WSClient.writeToReport(LogStatus.WARNING,
											"************Blocked : Unable to Set Key Data****************");
								}
							}
							else{
								WSClient.writeToReport(LogStatus.WARNING,
										"************Blocked : Unable to Set Key Data****************");
							}

						} 
					} 
			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			 try {
                 if(!WSClient.getData("{var_resvId}").equals(""))
                    CancelReservation.cancelReservation("DS_02");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			
		}

	}

	@Test(groups = { "minimumRegression", "FetchKeyData", "OWS" })
	public void fetchKeyData_38767() {
		try {
			String testName = "fetchKeyData_38767";
			WSClient.startTest(testName, "Verify that the minimum information about the KeyTrack (key1Track,key2Track,key3Track) are retrieved onto the response", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
						WSClient.setData("{var_profileId}", profileId);


						String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
						if(!reservationId.equals("error")){

							WSClient.setData("{var_resvId}", reservationId);
							WSClient.setData("{var_keyTrack1}", "21000000000000000005");
							WSClient.setData("{var_keyTrack2}", "21140000000000000005");
							WSClient.setData("{var_keyTrack3}", "91000000000000000006");
							setOwsHeader();
							String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_02");
							String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
							if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", true)) {

								if (WSAssert.assertIfElementValueEquals(res_setKeyData,
										"SetKeyDataResponse_Result_resultStatusFlag", "SUCCESS", true)) {

									String req_fetchKeyData = WSClient.createSOAPMessage("OWSFetchKeyData", "DS_01");
									String res_fetchKeyData = WSClient.processSOAPMessage(req_fetchKeyData);
									if (WSAssert.assertIfElementValueEquals(res_fetchKeyData,
											"FetchKeyDataResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										LinkedHashMap<String, String> keyTrackDb = WSClient
												.getDBRow(WSClient.getQuery("OWSSetKeyData", "QS_02"));

										if (WSAssert.assertEquals(keyTrackDb.get("TRACK1"),
												WSClient.getElementValue(res_fetchKeyData,
														"FetchKeyDataResponse_KeyTrack_Key1Track", XMLType.RESPONSE),
												true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"Key1Track - " + "Expected: " + keyTrackDb.get("TRACK1")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key1Track",
																	XMLType.RESPONSE));
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Key1Track - " + "Expected: " + keyTrackDb.get("TRACK1")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key1Track",
																	XMLType.RESPONSE));
										}

										if (WSAssert.assertEquals(keyTrackDb.get("TRACK2"),
												WSClient.getElementValue(res_fetchKeyData,
														"FetchKeyDataResponse_KeyTrack_Key2Track", XMLType.RESPONSE),
												true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"Key2Track - " + "Expected: " + keyTrackDb.get("TRACK2")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key2Track",
																	XMLType.RESPONSE));
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Key2Track - " + "Expected: " + keyTrackDb.get("TRACK2")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key2Track",
																	XMLType.RESPONSE));
										}

										if (WSAssert.assertEquals(keyTrackDb.get("TRACK3"),
												WSClient.getElementValue(res_fetchKeyData,
														"FetchKeyDataResponse_KeyTrack_Key3Track", XMLType.RESPONSE),
												true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"Key3Track - " + "Expected: " + keyTrackDb.get("TRACK3")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key3Track",
																	XMLType.RESPONSE));
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Key3Track - " + "Expected: " + keyTrackDb.get("TRACK3")
															+ ",Actual: "
															+ WSClient.getElementValue(res_fetchKeyData,
																	"FetchKeyDataResponse_KeyTrack_Key3Track",
																	XMLType.RESPONSE));
										}

									}
									
								}
								else{
									WSClient.writeToReport(LogStatus.WARNING,
											"************Blocked : Unable to Set Key Data****************");
								}
								
							}
							else{
								WSClient.writeToReport(LogStatus.WARNING,
										"************Blocked : Unable to Set Key Data****************");
							}
							

						}
					} 
			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);

			WSClient.writeToReport(LogStatus.ERROR, e.getMessage());
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			try {
                if(!WSClient.getData("{var_resvId}").equals(""))
                   CancelReservation.cancelReservation("DS_02");
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
		}

	}

	@Test(groups = { "minimumRegression", "FetchKeyData", "OWS" })
	public void setKeyData2() {
		try {
			String testName = "fetchKeyData_38768";
			WSClient.startTest(testName, "verify that FAIL result Status Flag has been populated when No keyTrack is there for a reservation", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				String profileId = CreateProfile.createProfile("DS_01");
				if(!profileId.equals("error")){
						WSClient.setData("{var_profileId}", profileId);


						String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
						if(!reservationId.equals("error")){
							WSClient.setData("{var_resvId}", reservationId);
							setOwsHeader();
							String req_fetchKeyData = WSClient.createSOAPMessage("OWSFetchKeyData", "DS_01");
							String res_fetchKeyData = WSClient.processSOAPMessage(req_fetchKeyData);
							if (WSAssert.assertIfElementValueEquals(res_fetchKeyData,
									"FetchKeyDataResponse_Result_resultStatusFlag", "FAIL", false)) {

								WSClient.writeToReport(LogStatus.PASS, WSClient.getElementValue(res_fetchKeyData,
										"Result_Text_TextElement", XMLType.RESPONSE));

							}

						} 
					} 
			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			try {
                if(!WSClient.getData("{var_resvId}").equals(""))
                   CancelReservation.cancelReservation("DS_02");
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
		}

	}

	@Test(groups = { "minimumRegression", "FetchKeyData", "OWS" })
	public void setKeyData12() {
		try {
			String testName = "fetchKeyData_38769";
			WSClient.startTest(testName, "verify that FAIL result Status Flag has been populated when Invalid Reservation ID is passed", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String interfaceName = OWSLib.getChannel();

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String resort = OPERALib.getResort();

			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			String invalidResvId = WSClient.getDBRow(WSClient.getQuery("OWSSetKeyData", "QS_04")).get("RESERVATIONID");

			WSClient.setData("{var_resvId}", invalidResvId);
			setOwsHeader();
			String req_fetchKeyData = WSClient.createSOAPMessage("OWSFetchKeyData", "DS_01");
			String res_fetchKeyData = WSClient.processSOAPMessage(req_fetchKeyData);
			if (WSAssert.assertIfElementValueEquals(res_fetchKeyData, "FetchKeyDataResponse_Result_resultStatusFlag",
					"FAIL", false)) {

				WSClient.writeToReport(LogStatus.PASS,
						WSClient.getElementValue(res_fetchKeyData, "Result_Text_TextElement", XMLType.RESPONSE));

			}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}

	}

}
