package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.activity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;


public class UpdateActivity extends WSSetUp {

	@Test(groups = { "sanity", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_4537() {
		try {
			String testName = "updateActivity_2008_4537";
			WSClient.startTest(testName, "verify that the ProfileID is updated correctly through the UpdateActivity call for an existing ActivityID.", "sanity");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
							/************
							 * Prerequisite 3: Create a New Profile
							 ****************/
							OPERALib.setOperaHeader(OPERALib.getUserName());
							String operaNewProfileID = CreateProfile.createProfile("DS_01");
							if(!operaNewProfileID.equals("error")){

									WSClient.setData("{var_profileID}", operaNewProfileID);
									WSClient.setData("{var_activity_unique_id}", activityId);

									/************
									 * Main Operation: Update Activity
									 ****************/
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_01");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {

										HashMap<String, String> profileDetails = WSClient.getDBRow(WSClient.getQuery("QS_01"));
										HashMap<String, String> activityDetails = WSClient.getDBRow(WSClient.getQuery("QS_02"));

										/************
										 * Validations
										 ****************/
										
										if (WSAssert.assertEquals(operaNewProfileID, profileDetails.get("PROFILE_ID"),
												true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Profile_Id is populated as expected");
											WSClient.writeToReport(LogStatus.INFO, "Expected: " + operaNewProfileID
													+ " Actual: " + profileDetails.get("PROFILE_ID") + "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Profile_Id is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO, "Expected: " + operaNewProfileID
													+ " Actual: " + profileDetails.get("PROFILE_ID") + "");
										}

										if (WSAssert.assertEquals(activityTypeExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_TYPE", profileDetails.get("ACTIVITY_TYPE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Type is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Type is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										}

										if (WSAssert.assertEquals(activityLocExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_LOCATION", activityDetails.get("LOCATION_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS, "Location is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Location is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										}

										if (WSAssert.assertEquals(activityStatusExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_STATUS", activityDetails.get("STATUS_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Status is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Status is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										}
									}

								}
								

						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
					}
					
					
				}
				

		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}
	
	
	
	
	@Test(groups = { "minimumRegression", "UpdateActivity","HTNG2008B", "HTNG" })

	public void updateActivity_2008_3246() {
		try {
			String testName = "updateActivity_2008_3246";
			WSClient.startTest(testName, "Verify that the ReservationID is updated correctly through the UpdateActivity call for an existing ActivityID.","minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
			
			
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));
			// Create Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());
			
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
						WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode","DS_01" ));
						WSClient.setData("{VAR_ROOMTYPE}",OperaPropConfig.getDataSetForCode("RoomType","DS_01" ));
						WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode","DS_01" ));
						WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode","DS_01" ));
						
						
						// Create Reservation
						WSClient.setData("{var_profileId}", operaProfileID);

							String reservationId =CreateReservation.createReservation("DS_05").get("reservationId");
							if(!reservationId.equals("error")){

							WSClient.setData("{var_resvId}", reservationId);
							
							WSClient.setData("{var_firstResvId}", reservationId);

							WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));

							WSClient.setData("{var_extResort}", resortExtValue);
							
					
							WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
							WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
							WSClient.setData("{var_actlocation}",HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
							
							// Creating activity
							String createActivityReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_02");
							String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

							if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
									"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

								String activityId = WSClient.getElementValueByAttribute(createActivityResponseXML,
										"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
										XMLType.RESPONSE);

								WSClient.setData("{var_activity_unique_id}", activityId);
								OPERALib.setOperaHeader(OPERALib.getUserName());
								// Create New Profile
								String createNewProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
								String createNewProfileResponseXML = WSClient.processSOAPMessage(createNewProfileReq);

								if (WSAssert.assertIfElementExists(createNewProfileResponseXML,
										"CreateProfileRS_Success", true)) {

									if (WSAssert.assertIfElementExists(createNewProfileResponseXML,
											"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {

										String operaNewProfileID = WSClient.getElementValue(createNewProfileResponseXML,
												"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
										WSClient.setData("{var_profileID}", operaNewProfileID);

										// Create Reservation
										WSClient.setData("{var_profileId}", operaNewProfileID);
										String newReservationId =CreateReservation.createReservation("DS_05").get("reservationId");
										if(!reservationId.equals("error")){

											
											WSClient.writeToReport(LogStatus.INFO,
													"New Reservation Id : " + newReservationId);
											WSClient.setData("{var_resvId}", newReservationId);
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
											

											String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity",
													"DS_02");
											String updateActRes = WSClient.processSOAPMessage(updateActReq);

											if (WSAssert.assertIfElementValueEquals(updateActRes,
													"UpdateActivityResponse_Results_resultStatusFlag", "SUCCESS",
													false)) {

												HashMap<String, String> reservDetails = WSClient.getDBRow(WSClient.getQuery("QS_03"));

												/************
												 * Validations
												 ****************/

												if (WSAssert.assertEquals(newReservationId,
														reservDetails.get("RESV_ID"), true)) {

													WSClient.writeToReport(LogStatus.PASS,
															"Reservartion_ID is updated as expected for Activity_Id : "
																	+ activityId);
													WSClient.writeToReport(LogStatus.INFO,
															"Expected: " + newReservationId + " Actual: "
																	+ reservDetails.get("RESV_ID") + "");
												} else {

													WSClient.writeToReport(LogStatus.FAIL,
															"Reservation is not updated as expected for Activity_Id : "
																	+ activityId);
													WSClient.writeToReport(LogStatus.INFO,
															"Expected: " + newReservationId + " Actual: "
																	+ reservDetails.get("RESV_ID") + "");
												}

											}

										}

									}
									else {
										WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
									}
								}
								
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
								}
								
								

							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Activity not created");
							}
						} 
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Subscription not created");
					}
				} 
		}} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally{
			try {
                if(!WSClient.getData("{var_resvId}").equals(""))
                   CancelReservation.cancelReservation("DS_02");
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
			WSClient.setData("{var_resvId}", WSClient.getData("{var_firstResvId}"));
			try {
                if(!WSClient.getData("{var_resvId}").equals(""))
                   CancelReservation.cancelReservation("DS_02");
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
		}
	}
	
	
	@Test(groups = { "minimumRegression", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_3248() {
		try {
			String testName = "updateActivity_2008_3248";
			WSClient.startTest(testName, "Verify that the ActivityStatus,ActivityType, Location and TimeSpan are updated correctly for an activity using ProfileID.", "minimumRegression");

			
			
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);


			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			
			
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			
			

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);


			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);
					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{

					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
									WSClient.setData("{var_activity_unique_id}", activityId);

									String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
									String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_10}");
									
									WSClient.setData("{var_activity_start_timespan}", startDate);
									WSClient.setData("{var_activity_end_timespan}", endDate);
									
									/************
									 * Main Operation: Update Activity
									 ****************/
									activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
									activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
									activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
									
									WSClient.setData("{var_updatedActivityType}", activityTypeExtValue);
									WSClient.setData("{var_updatedActivityLocation}", activityLocExtValue);
									WSClient.setData("{var_updatedActivityStatus}", activityStatusExtValue);
									
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_03");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {

										HashMap<String, String> profileDetails = WSClient.getDBRow(WSClient.getQuery("QS_01"));
										HashMap<String, String> activityDetails = WSClient.getDBRow(WSClient.getQuery("QS_02"));

										/************
										 * Validations
										 ****************/
										


										if (WSAssert.assertEquals(activityTypeExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_TYPE", profileDetails.get("ACTIVITY_TYPE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Type is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Type is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										}
										
										
										
										if (WSAssert.assertEquals(startDate,profileDetails.get("START_DATE"), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Start TimeSpan is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + startDate + " Actual: "
															+ profileDetails.get("START_DATE")
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Start TimeSpan is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + startDate + " Actual: "
															+ profileDetails.get("START_DATE")
															+ "");
										}
										
										
										
										if (WSAssert.assertEquals(endDate,profileDetails.get("END_DATE"), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"End TimeSpan is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + endDate + " Actual: "
															+ profileDetails.get("END_DATE")
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"End TimeSpan is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + endDate + " Actual: "
															+ profileDetails.get("END_DATE")
															+ "");
										}
										

										if (WSAssert.assertEquals(activityLocExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_LOCATION", activityDetails.get("LOCATION_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS, "Location is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Location is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										}

										if (WSAssert.assertEquals(activityStatusExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_STATUS", activityDetails.get("STATUS_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Status is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Status is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										}


						}
						}
						
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
			}
				

		}}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
}
	
	@Test(groups = { "minimumRegression", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_3250() {
		try {
			String testName = "updateActivity_2008_3250";
			WSClient.startTest(testName, "Verify that the NumberOfPersons,notes, Amount, DepositeRequired and DepositRequired/collectedBy of an activity are updated correctly for the requested profileID.","minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
			
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			
			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			
			

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
									WSClient.setData("{var_activity_unique_id}", activityId);

									String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
									String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_10}");
									
									WSClient.setData("{var_activity_start_timespan}", startDate);
									WSClient.setData("{var_activity_end_timespan}", endDate);
									
									/************
									 * Main Operation: Update Activity
									 ****************/
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									
									String amount=WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
									String depositeAmount=WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
									String sampleNotes="Sample Notes";
									String numberOfPersons=WSClient.getKeywordData("{KEYWORD_RANDNUM_1}");
									WSClient.setData("{var_notes}", sampleNotes);
									WSClient.setData("{var_amount}", amount);
									WSClient.setData("{var_deposite}", depositeAmount);
									WSClient.setData("{var_deposite_owner}", "O");
									WSClient.setData("{var_number_of_persons}", numberOfPersons);
									
									
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_04");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									
									
									
									
									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {

										HashMap<String, String> activityDetails = WSClient.getDBRow(WSClient.getQuery("QS_04"));
										
										/************
										 * Validations
										 ****************/
									
										WSClient.writeToReport(LogStatus.PASS, activityDetails.toString());
										
										if (WSAssert.assertEquals(numberOfPersons, activityDetails.get("NOP"), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Number of Persons is updated as expected");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Number of Persons is not updated as expected");
										}
										WSClient.writeToReport(LogStatus.INFO,
												"Expected: " + numberOfPersons + " Actual: "
														+ activityDetails.get("NOP")
														+ "");
										
										if (WSAssert.assertEquals(sampleNotes, activityDetails.get("NOTES"), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Notes is updated as expected");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Notes is not updated as expected");
										}
										WSClient.writeToReport(LogStatus.INFO,
												"Expected: " + sampleNotes + " Actual: "
														+ activityDetails.get("NOTES")
														+ "");
										
										if (WSAssert.assertEquals(amount, activityDetails.get("ACTIVITY_AMOUNT"), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Amount is updated as expected");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Amount is not updated as expected");
										}
										WSClient.writeToReport(LogStatus.INFO,
												"Expected: " + amount + " Actual: "
														+ activityDetails.get("ACTIVITY_AMOUNT")
														+ "");
										
										if (WSAssert.assertEquals(depositeAmount, activityDetails.get("DEPOSIT_AMOUNT"), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Deposite Amount is updated as expected");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Deposite Amount is not updated as expected");
										}
										WSClient.writeToReport(LogStatus.INFO,
												"Expected: " + depositeAmount + " Actual: "
														+ activityDetails.get("DEPOSIT_AMOUNT")
														+ "");
										

						}
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
			}
				

		}}}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	
}
	
	
	
	
	
	
	@Test(groups = { "targetedRegression", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_3249() {
		try {
			String testName = "updateActivity_2008_3249";
			WSClient.startTest(testName, "verify that the Verify multiple activities can updated for the requested ProfileID.", "targetedRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
							String createActReq1 = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
							String createActRes1 = WSClient.processSOAPMessage(createActReq1);

							if (WSAssert.assertIfElementValueEquals(createActRes1,
									"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
								if (WSAssert.assertIfElementExists(createActRes1, "Results_IDs_UniqueID", true)) {

									String activityId2 = WSClient.getElementValueByAttribute(createActRes1,
											"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
											XMLType.RESPONSE);		
									
									
								
							
							/************
							 * Prerequisite 3: Create a New Profile
							 ****************/
							OPERALib.setOperaHeader(OPERALib.getUserName());
							String operaNewProfileID = CreateProfile.createProfile("DS_01");
							if(!operaNewProfileID.equals("error")){

									WSClient.setData("{var_profileID}", operaNewProfileID);
									WSClient.setData("{var_activity_unique_id}", activityId);
									
									
									WSClient.setData("{var_activity_unique_id1}",activityId2);


									/************
									 * Main Operation: Update Activity
									 ****************/
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_05");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									
									LinkedHashMap<String, Integer> xpathValues = new LinkedHashMap<String, Integer>();
									xpathValues.put("UpdateActivityResponse_Results", 2);
									xpathValues.put("UpdateActivityResponse_Results_resultStatusFlag", 1);
									
									
									
									
									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {
										
										
							if(WSAssert.assertEquals(WSClient.getElementValueByIndex(updateActRes, xpathValues, XMLType.RESPONSE),"SUCCESS",true)){
										
								WSClient.writeToReport(LogStatus.PASS, "Second Success Flag -> Expected : SUCCESS, Actual : "+WSClient.getElementValueByIndex(updateActRes, xpathValues, XMLType.RESPONSE));

										HashMap<String, String> profileDetails = WSClient.getDBRow(WSClient.getQuery("QS_01"));
										HashMap<String, String> activityDetails = WSClient.getDBRow(WSClient.getQuery("QS_02"));

										/************
										 * Validations
										 ****************/
										
										
										/* validating Activity 1*/
										
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating Activity 1</b>");
										
										if (WSAssert.assertEquals(operaNewProfileID, profileDetails.get("PROFILE_ID"),
												true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Profile_Id is populated as expected");
											WSClient.writeToReport(LogStatus.INFO, "Expected: " + operaNewProfileID
													+ " Actual: " + profileDetails.get("PROFILE_ID") + "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Profile_Id is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO, "Expected: " + operaNewProfileID
													+ " Actual: " + profileDetails.get("PROFILE_ID") + "");
										}

										if (WSAssert.assertEquals(activityTypeExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_TYPE", profileDetails.get("ACTIVITY_TYPE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Type is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Type is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										}

										if (WSAssert.assertEquals(activityLocExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_LOCATION", activityDetails.get("LOCATION_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS, "Location is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Location is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										}

										if (WSAssert.assertEquals(activityStatusExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_STATUS", activityDetails.get("STATUS_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Status is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Status is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										}
										
										
										
										/* validating Activity 2*/
										
										
										WSClient.setData("{var_activity_unique_id}",activityId2);
										
									    profileDetails = WSClient.getDBRow(WSClient.getQuery("QS_01"));
									    activityDetails = WSClient.getDBRow(WSClient.getQuery("QS_02"));
										
										
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating Activity 2</b>");
										
										if (WSAssert.assertEquals(operaNewProfileID, profileDetails.get("PROFILE_ID"),
												true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Profile_Id is populated as expected");
											WSClient.writeToReport(LogStatus.INFO, "Expected: " + operaNewProfileID
													+ " Actual: " + profileDetails.get("PROFILE_ID") + "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Profile_Id is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO, "Expected: " + operaNewProfileID
													+ " Actual: " + profileDetails.get("PROFILE_ID") + "");
										}

										if (WSAssert.assertEquals(activityTypeExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_TYPE", profileDetails.get("ACTIVITY_TYPE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Type is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Type is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityTypeExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE",
																	profileDetails.get("ACTIVITY_TYPE"))
															+ "");
										}

										if (WSAssert.assertEquals(activityLocExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_LOCATION", activityDetails.get("LOCATION_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS, "Location is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Location is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityLocExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION",
																	activityDetails.get("LOCATION_CODE"))
															+ "");
										}

										if (WSAssert.assertEquals(activityStatusExtValue, HTNGLib.getExtValue(resortOperaValue,interfaceName,
												"ACTIVITY_STATUS", activityDetails.get("STATUS_CODE")), true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Activity Status is populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										} else {

											WSClient.writeToReport(LogStatus.FAIL,
													"Activity Status is not populated as expected");
											WSClient.writeToReport(LogStatus.INFO,
													"Expected: " + activityStatusExtValue + " Actual: "
															+ HTNGLib.getExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS",
																	activityDetails.get("STATUS_CODE"))
															+ "");
										}	
										
										
									}
							else{
								
							WSClient.writeToReport(LogStatus.FAIL, "Second Success Flag - Expected : SUCCESS, Actual : "+WSClient.getElementValueByIndex(updateActRes, xpathValues, XMLType.RESPONSE));
							}
							}

								}
							
							
							
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
							}

						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
					}
					
					
				}
				

		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}	
	
	
	

	@Test(groups = { "fullRegression", "UpdateActivity","HTNG2008B", "HTNG"})
	public void updateActivity_2008_42669() {
		try {
			String testName = "updateActivity_2008_42669";
			WSClient.startTest(testName, "verify that the Error Message is Populated when an Invalid Activity Status is passed on the request.", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
							/************
							 * Prerequisite 3: Create a New Profile
							 ****************/
							OPERALib.setOperaHeader(OPERALib.getUserName());
							String operaNewProfileID = CreateProfile.createProfile("DS_01");
							if(!operaNewProfileID.equals("error")){

									WSClient.setData("{var_profileID}", operaNewProfileID);
									WSClient.setData("{var_activity_unique_id}", activityId);

									/************
									 * Main Operation: Update Activity
									 ****************/
									
									String inavalidData="INVALID_ACTIVITY_STATUS"+String.valueOf(new Random().nextInt(1000) + 1);
									
									WSClient.setData("{var_activityStatus}", inavalidData);
									
									WSClient.writeToReport(LogStatus.INFO, "<b>Invalid Activity Status : "+inavalidData+"</b>");
									
									
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_01");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

									
										WSAssert.assertIfElementValueEquals(updateActRes, "Results_Text_TextElement", "{INVALID_STATUS_CODE}--INVALID ACTIVITY STATUS CODE", false);
										
									}

								}
								

						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
					}
					
					
				}
				

		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

	
	
	

	@Test(groups = { "fullRegression", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_3252() {
		try {
			String testName = "updateActivity_2008_3252";
			WSClient.startTest(testName, "verify that the Error Message is Populated when an Invalid Activity Type is passed on the request.", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
							/************
							 * Prerequisite 3: Create a New Profile
							 ****************/
							OPERALib.setOperaHeader(OPERALib.getUserName());
							String operaNewProfileID = CreateProfile.createProfile("DS_01");
							if(!operaNewProfileID.equals("error")){

									WSClient.setData("{var_profileID}", operaNewProfileID);
									WSClient.setData("{var_activity_unique_id}", activityId);

									/************
									 * Main Operation: Update Activity
									 ****************/
									
									String inavalidData="INVALID_ACTIVITY_TYPE"+String.valueOf(new Random().nextInt(1000) + 1);
									
									WSClient.setData("{var_activityType}", inavalidData);
									
									WSClient.writeToReport(LogStatus.INFO, "<b>Invalid Activity Type : "+inavalidData+"</b>");
									
									
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_01");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

									
										WSAssert.assertIfElementValueEquals(updateActRes, "Results_Text_TextElement", "{INVALID_ACTIVITY_TYPE}--INVALID ACTIVITY TYPE", false);
										
									}

								}
								

						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
					}
					
					
				}
				

		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

	
	
	@Test(groups = { "fullRegression", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_42670() {
		try {
			String testName = "updateActivity_2008_42670";
			WSClient.startTest(testName, "verify that the Error Message is Populated when an Invalid Activity Location Code is passed on the request.", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
							/************
							 * Prerequisite 3: Create a New Profile
							 ****************/
							OPERALib.setOperaHeader(OPERALib.getUserName());
							String operaNewProfileID = CreateProfile.createProfile("DS_01");
							if(!operaNewProfileID.equals("error")){

									WSClient.setData("{var_profileID}", operaNewProfileID);
									WSClient.setData("{var_activity_unique_id}", activityId);

									/************
									 * Main Operation: Update Activity
									 ****************/
									
									String inavalidData="INVALID_ACTIVITY_LOCATION"+String.valueOf(new Random().nextInt(1000) + 1);
									
									WSClient.setData("{var_activityLocation}", inavalidData);
									
									WSClient.writeToReport(LogStatus.INFO, "<b>Invalid Activity Location Code : "+inavalidData+"</b>");
									
									
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_01");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

									
										WSAssert.assertIfElementValueEquals(updateActRes, "Results_Text_TextElement", "{INVALID_LOCATION_CODE}--INVALID LOCATION CODE", false);
										
									}

								}
								

						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
					}
					
					
				}
				

		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}
	
	
	
	
	

	@Test(groups = { "fullRegression", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_3253() {
		try {
			String testName = "updateActivity_2008_3253";
			WSClient.startTest(testName, "verify that the Error Message is Populated when a Resort Id is not passed on the request.", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
							/************
							 * Prerequisite 3: Create a New Profile
							 ****************/
							OPERALib.setOperaHeader(OPERALib.getUserName());
							String operaNewProfileID = CreateProfile.createProfile("DS_01");
							if(!operaNewProfileID.equals("error")){

									WSClient.setData("{var_profileID}", operaNewProfileID);
									WSClient.setData("{var_activity_unique_id}", activityId);

									/************
									 * Main Operation: Update Activity
									 ****************/
									
									WSClient.setData("{var_extResort}", "");
								
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_01");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

									
										WSClient.writeToReport(LogStatus.INFO, WSClient.getElementValue(updateActRes, "Results_Text_TextElement", XMLType.RESPONSE));
										
									}

								}
								

						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
					}
					
					
				}
				

		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}
	
	
	
	
	@Test(groups = { "fullRegression", "UpdateActivity","HTNG2008B", "HTNG" })
	public void updateActivity_2008_3254() {
		try {
			String testName = "updateActivity_2008_3254";
			WSClient.startTest(testName, "verify that the Error Message is Populated when an Invalid Resort Id is passed on the request.", "fullRegression");
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);

			String activityTypeExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE");
			String activityLocExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION");
			String activityStatusExtValue = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS");
			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"COUNTRY_CODE"));
			WSClient.setData("{var_addressType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PHONE_TYPE"));
			WSClient.setData("{var_profileType}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"PROFILE_TYPE"));

			/************
			 * Prerequisite 1: Create a Profile
			 ****************/
			OPERALib.setOperaHeader(OPERALib.getUserName());
					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){

					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_activitySource}", interfaceName);
					WSClient.setData("{var_activityType}", activityTypeExtValue);
					WSClient.setData("{var_activityLocation}", activityLocExtValue);
					WSClient.setData("{var_activityStatus}", activityStatusExtValue);

					
					
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
					
					/************
					 * Prerequisite 2: Create an Activity
					 ****************/
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String createActReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_04");
					String createActRes = WSClient.processSOAPMessage(createActReq);

					if (WSAssert.assertIfElementValueEquals(createActRes,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(createActRes, "Results_IDs_UniqueID", true)) {

							String activityId = WSClient.getElementValueByAttribute(createActRes,
									"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
									XMLType.RESPONSE);
							
							/************
							 * Prerequisite 3: Create a New Profile
							 ****************/
							OPERALib.setOperaHeader(OPERALib.getUserName());
							String operaNewProfileID = CreateProfile.createProfile("DS_01");
							if(!operaNewProfileID.equals("error")){

									WSClient.setData("{var_profileID}", operaNewProfileID);
									WSClient.setData("{var_activity_unique_id}", activityId);

									/************
									 * Main Operation: Update Activity
									 ****************/
									String inavalidData="INVALID_RESORT_ID"+String.valueOf(new Random().nextInt(1000) + 1);
									
									WSClient.setData("{var_extResort}", inavalidData);
									
									WSClient.writeToReport(LogStatus.INFO, "<b>Invalid Resort Id : "+inavalidData+"</b>");
									
								
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
									String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity", "DS_01");
									String updateActRes = WSClient.processSOAPMessage(updateActReq);

									if (WSAssert.assertIfElementValueEquals(updateActRes,
											"UpdateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

									
										WSClient.writeToReport(LogStatus.INFO, WSClient.getElementValue(updateActRes, "Results_Text_TextElement", XMLType.RESPONSE));
										
									}

								}
								

						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Cannot create a activity");
					}
					
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
					}
					
					
				}
				

		} catch (Exception e) {
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// ExtentReport.log(LogStatus.FAIL, "Exception occured in test due
			// to:"+e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}
	
	
	
	
	@Test(groups = { "fullRegression", "UpdateActivity","HTNG2008B", "HTNG" })

	public void updateActivity_2008_3255() {
		try {
			String testName = "updateActivity_2008_3255";
			WSClient.startTest(testName, "Verify that the Error Message is Populated when trying to update the activity with Cancelled ReservationID.","fullRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
			
			
			String resortOperaValue = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_gender}", genderExtValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_state}", HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
			WSClient.setData("{var_addressType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
			WSClient.setData("{var_phoneType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
			WSClient.setData("{var_profileType}",
					HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));
			// Create Profile
			OPERALib.setOperaHeader(OPERALib.getUserName());
			
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
					WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
				    WSClient.setData("{var_profileID}", operaProfileID);
					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
					
					 //************Prerequisite 2 - HTNG Subscription******************************************//
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) 
					{
						WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode","DS_01" ));
						WSClient.setData("{VAR_ROOMTYPE}",OperaPropConfig.getDataSetForCode("RoomType","DS_01" ));
						WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode","DS_01" ));
						WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode","DS_01" ));
						
						
						// Create Reservation
						WSClient.setData("{var_profileId}", operaProfileID);

							String reservationId =CreateReservation.createReservation("DS_05").get("reservationId");
							if(!reservationId.equals("error")){

							WSClient.setData("{var_resvId}", reservationId);
							
							WSClient.setData("{var_firstResvId}", reservationId);

							WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));

							WSClient.setData("{var_extResort}", resortExtValue);
							
					
							WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
							WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
							WSClient.setData("{var_actlocation}",HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
							
							// Creating activity
							String createActivityReq = WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_02");
							String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

							if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
									"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {

								String activityId = WSClient.getElementValueByAttribute(createActivityResponseXML,
										"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
										XMLType.RESPONSE);

								WSClient.setData("{var_activity_unique_id}", activityId);
								OPERALib.setOperaHeader(OPERALib.getUserName());
								// Create New Profile
								String createNewProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
								String createNewProfileResponseXML = WSClient.processSOAPMessage(createNewProfileReq);

								if (WSAssert.assertIfElementExists(createNewProfileResponseXML,
										"CreateProfileRS_Success", true)) {

									if (WSAssert.assertIfElementExists(createNewProfileResponseXML,
											"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {

										String operaNewProfileID = WSClient.getElementValue(createNewProfileResponseXML,
												"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
										WSClient.setData("{var_profileID}", operaNewProfileID);

										// Create Reservation
										WSClient.setData("{var_profileId}", operaNewProfileID);
										String newReservationId =CreateReservation.createReservation("DS_05").get("reservationId");
										if(!reservationId.equals("error")){

											
											WSClient.writeToReport(LogStatus.INFO,
													"New Reservation Id : " + newReservationId);
											
											
											
											WSClient.setData("{var_resvId}", newReservationId);
											if(CancelReservation.cancelReservation("DS_02")){
											
											
											
											
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
											

											String updateActReq = WSClient.createSOAPMessage("HTNG2008UpdateActivity",
													"DS_02");
											String updateActRes = WSClient.processSOAPMessage(updateActReq);

											if (WSAssert.assertIfElementValueEquals(updateActRes,
													"UpdateActivityResponse_Results_resultStatusFlag", "FAIL",
													false)) {

												WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(updateActRes, "Results_Text_TextElement", XMLType.RESPONSE)+"</b>");				
											

											}

										}}

									}
									else {
										WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
									}
								}
								
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
								}
								
								

							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Activity not created");
							}
						} 
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Subscription not created");
					}
				} 
		}} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally{
			
			WSClient.setData("{var_resvId}", WSClient.getData("{var_firstResvId}"));
			try {
                if(!WSClient.getData("{var_resvId}").equals(""))
                   CancelReservation.cancelReservation("DS_02");
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
		}
	}
	
	
	
	
	
}
