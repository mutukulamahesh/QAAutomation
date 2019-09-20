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

public class SetKeyData extends WSSetUp {

	
	
	public void setOwsHeader() throws Exception{
        String resortOpera = OPERALib.getResort();
       String channel = OWSLib.getChannel();
       String uname = OPERALib.getUserName();
       String pwd = OPERALib.getPassword();
       String channelType = OWSLib.getChannelType(channel);
       String channelCarrier = OWSLib.getChannelCarier(resortOpera, channel);
       OWSLib.setOWSHeader(uname, pwd, resortOpera, channelType, channelCarrier);
   }
	
	
	@Test(groups = { "sanity", "SetKeyData", "OWS","ResvAdvanced" })
	public void setKeyData_38735() {
		try {
			String testName = "setKeyData_38735";
			WSClient.startTest(testName, "Verify that keytrack2 has been added to a given reservation id.", "sanity");
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
							if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", false)) {

								if (WSAssert.assertIfElementValueEquals(res_setKeyData,
										"SetKeyDataResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String, String> keyTrackDb = WSClient
											.getDBRow(WSClient.getQuery("QS_01"));

									if (WSAssert.assertEquals(keyTrackDb.get("TRACK2"),
											WSClient.getData("{var_keyTrack2}"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Key2Track - " + "Expected: " + WSClient.getData("{var_keyTrack2}")
														+ ",Actual: " + keyTrackDb.get("TRACK2"));
									} else {

										WSClient.writeToReport(LogStatus.FAIL,
												"Key2Track - " + "Expected: " + WSClient.getData("{var_keyTrack2}")
														+ ",Actual: " + keyTrackDb.get("TRACK2"));
									}

								}
							}

						} 
					} 
			}
		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
			
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

	@Test(groups = { "minimumRegression", "SetKeyData", "OWS" ,"ResvAdvanced" })
	public void setKeyData_38736() {
		try {
			String testName = "setKeyData_38736";
			WSClient.startTest(testName,
					"Verify keytrack1,keytrack2,keytrack3 has been added to a given reservation id.",
					"minimumRegression");
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
							if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", false)) {

								if (WSAssert.assertIfElementValueEquals(res_setKeyData,
										"SetKeyDataResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String, String> keyTrackDb = WSClient
											.getDBRow(WSClient.getQuery("QS_02"));

									if (WSAssert.assertEquals(keyTrackDb.get("TRACK1"),
											WSClient.getData("{var_keyTrack1}"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Key1Track - " + "Expected: " + WSClient.getData("{var_keyTrack1}")
														+ ",Actual: " + keyTrackDb.get("TRACK1"));
									} else {

										WSClient.writeToReport(LogStatus.FAIL,
												"Key1Track - " + "Expected: " + WSClient.getData("{var_keyTrack1}")
														+ ",Actual: " + keyTrackDb.get("TRACK1"));
									}

									if (WSAssert.assertEquals(keyTrackDb.get("TRACK2"),
											WSClient.getData("{var_keyTrack2}"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Key2Track - " + "Expected: " + WSClient.getData("{var_keyTrack2}")
														+ ",Actual: " + keyTrackDb.get("TRACK2"));
									} else {

										WSClient.writeToReport(LogStatus.FAIL,
												"Key2Track - " + "Expected: " + WSClient.getData("{var_keyTrack2}")
														+ ",Actual: " + keyTrackDb.get("TRACK2"));
									}

									if (WSAssert.assertEquals(keyTrackDb.get("TRACK3"),
											WSClient.getData("{var_keyTrack3}"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Key3Track - " + "Expected: " + WSClient.getData("{var_keyTrack3}")
														+ ",Actual: " + keyTrackDb.get("TRACK3"));
									} else {

										WSClient.writeToReport(LogStatus.FAIL,
												"Key3Track - " + "Expected: " + WSClient.getData("{var_keyTrack3}")
														+ ",Actual: " + keyTrackDb.get("TRACK3"));
									}

								}
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

	@Test(groups = { "minimumRegression", "SetKeyData", "OWS","ResvAdvanced"  })
	public void setKeyData_38737() {
		try {
			String testName = "setKeyData_38737";
			WSClient.startTest(testName, "Verify that key2Track is updated to a new value when existing key2track is modified for a reservation ", "minimumRegression");
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
							if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", false)) {

								if (WSAssert.assertIfElementValueEquals(res_setKeyData,
										"SetKeyDataResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									WSClient.setData("{var_keyTrack2}", "21140021221710005");

									String req_setKeyData1 = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
									String res_setKeyData1 = WSClient.processSOAPMessage(req_setKeyData1);
									if (WSAssert.assertIfElementExists(res_setKeyData1, "SetKeyDataResponse_Result",
											false)) {

										if (WSAssert.assertIfElementValueEquals(res_setKeyData1,
												"SetKeyDataResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											LinkedHashMap<String, String> keyTrackDb = WSClient
													.getDBRow(WSClient.getQuery("QS_01"));

											if (WSAssert.assertEquals(keyTrackDb.get("TRACK2"),
													WSClient.getData("{var_keyTrack2}"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"Key2Track - " + "Expected: "
																+ WSClient.getData("{var_keyTrack2}") + ",Actual: "
																+ keyTrackDb.get("TRACK2"));
											} else {

												WSClient.writeToReport(LogStatus.FAIL,
														"Key2Track - " + "Expected: "
																+ WSClient.getData("{var_keyTrack2}") + ",Actual: "
																+ keyTrackDb.get("TRACK2"));
											}

										}
									}
								}
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

	@Test(groups = { "minimumRegression", "SetKeyData", "OWS" ,"ResvAdvanced" })
	public void setKeyData_38738() {
		try {
			String testName = "setKeyData_38738";
			WSClient.startTest(testName, "verify that FAIL result Status Flag has been populated when Invalid Profile ID is passed", "minimumRegression");
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

							String invalidProfileId = WSClient.getDBRow(WSClient.getQuery("OWSSetKeyData", "QS_03"))
									.get("PROFILEID");
							WSClient.setData("{var_resvId}", reservationId);
							WSClient.setData("{var_keyTrack2}", "21140000000000000005");
							WSClient.setData("{var_profileId}", invalidProfileId);
							setOwsHeader();

							String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
							String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
							if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", false)) {

								if (WSAssert.assertIfElementValueEquals(res_setKeyData,
										"SetKeyDataResponse_Result_resultStatusFlag", "FAIL", false)) {

									WSClient.writeToReport(LogStatus.PASS, WSClient.getElementValue(res_setKeyData,
											"Result_Text_TextElement", XMLType.RESPONSE));
								}
							}

						}}
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

	@Test(groups = { "minimumRegression", "SetKeyData", "OWS","ResvAdvanced"  })
	public void setKeyData_38739() {
		try {
			String testName = "setKeyData_38739";
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

			OPERALib.setOperaHeader(OPERALib.getUserName());

			// WSClient.setData("{var_roomNumber}",
			// WSClient.getDataSetForCode("Rooms", "DS_02"));

			String profileId = CreateProfile.createProfile("DS_01");
			if(!profileId.equals("error")){
					WSClient.setData("{var_profileId}", profileId);

					String invalidResvId = WSClient.getDBRow(WSClient.getQuery("OWSSetKeyData", "QS_04"))
							.get("RESERVATIONID");
					WSClient.setData("{var_resvId}", invalidResvId);
					WSClient.setData("{var_keyTrack2}", "21140000000000000005");
					setOwsHeader();

					String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
					String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
					if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", false)) {

						if (WSAssert.assertIfElementValueEquals(res_setKeyData,
								"SetKeyDataResponse_Result_resultStatusFlag", "FAIL", false)) {

							WSClient.writeToReport(LogStatus.PASS, WSClient.getElementValue(res_setKeyData,
									"Result_Text_TextElement", XMLType.RESPONSE));
						}
					}
				}

		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
			
		}

	}

}