package com.oracle.hgbu.opera.qaauto.ws.testcases.gaming.gaming;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.testcases.gaming.profile.NewProfile;
import com.oracle.hgbu.opera.qaauto.ws.testcases.gaming.profile.NewProfile.wsAttributes;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class AssignCardID extends WSSetUp {
	@Test(groups = { "BAT", "AssignIDCard", "Profile", "Gaming" })
	@Parameters({ "schema", "version" })
	public void assignCardID(String schema, String version) {
		WSClient.startTest("assignCardID","Verify that the assignCardID is functioning according to the GAMING_ID parameter", "bat");
		
		String sUsername = "", sPassword = "", sChain = "", sResort = "";
		try {
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			sChain = OPERALib.getChain();
			sResort = OPERALib.getResort();

			// Parameters required for the Gaming Header
			WSClient.setData("{var_userName}", sUsername);
			WSClient.setData("{var_password}", sPassword);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_resort}", sResort);
			WSClient.setData("{var_msgID}", UUID.randomUUID().toString());

			/*****************************************
			 * Check the GAMING ID Parameter Setting 
			 *****************************************/
			
			String query = WSClient.getQuery("GamingAssignCardID", "QS_01", false);
			String paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE") == null ? ""
					: WSClient.getDBRow(query).get("PARAMETER_VALUE");

			if (paramValue == "") {
				WSClient.writeToReport(LogStatus.WARNING, "GAMING_ID Parameter is not configured");
			} else if (paramValue.equalsIgnoreCase("OFF")) {
				WSClient.writeToReport(LogStatus.WARNING, "GAMING_ID Parameter is configured as OFF");
			} else if (paramValue.equalsIgnoreCase("UNICARD")) {
				WSClient.writeToReport(LogStatus.INFO, "GAMING_ID Parameter Setting>> " + paramValue);


				/**********************************
				 * Fetch Existing Reservation
				 *********************************/
				
				// Retrieve existing reservation
				LinkedHashMap<String, String> reservationMap = new LinkedHashMap<String, String>();
				String resvQry = WSClient.getQuery("GamingAssignCardID", "QS_03", false);
				System.out.println(resvQry);
				reservationMap = WSClient.getDBRow(resvQry);

				if (reservationMap.size() > 0) {
					// Set the data required for Subscription operation
					WSClient.setData("{var_operaProfileID}", reservationMap.get("NAME_ID"));
					WSClient.writeToReport(LogStatus.INFO, "Existing Reservation ID " + reservationMap.get("RESV_NAME_ID") +" and Profile ID "+reservationMap.get("NAME_ID"));
					/************************************************************************
					 * Subscribe the Name ID of the existing reservation to Gaming Interface
					 ************************************************************************/
					
					WSClient.writeToReport(LogStatus.INFO, "<b> Subscribe Profile of the existing reservation</b>");
					// Run subscription operation
					String subscriptionReq = WSClient.createSOAPMessage("GamingSubscription", "DS_01");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					//Validate if subscription function is successful
					if (WSAssert.assertIfElementContains(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String subscriptionQry = WSClient.getQuery("GamingSubscription", "QS_01",false);
						String databaseNameID = WSClient.getDBRow(subscriptionQry).get("DATABASE_NAME_ID") == null ? ""	: WSClient.getDBRow(subscriptionQry).get("DATABASE_NAME_ID");
						String expDatabaseNameID = WSClient.getElementValue(subscriptionReq,"Subscription_ProfileIDs_UniqueID[2]", XMLType.REQUEST);
						if (WSAssert.assertEquals(expDatabaseNameID, databaseNameID, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Subscription to Gaming Interface is successful, Entry found in NAME_SUBSCRIPTIONS table");
							WSClient.setData("{var_externalProfileID}", expDatabaseNameID);

							/********************************************
							 * Assign ID Card to the subscribed profile
							 *********************************************/
							WSClient.writeToReport(LogStatus.INFO, "<b> Assign Card ID to the subscribed profile </b>");
							
							// Construct Assign ID Card Request and Post it
							String assignCardIDReq = WSClient.createSOAPMessage("GamingAssignCardID", "DS_01");
							String assignCardIDRes = WSClient.processSOAPMessage(assignCardIDReq);

							// Validate if Card assignment is successful
							if (WSAssert.assertIfElementContains(assignCardIDRes, "AssignCardIDResponse_ResultStatus_resultStatusFlag","SUCCESS", false)) {
								WSClient.setData("{var_reservationNameID}", reservationMap.get("RESV_NAME_ID"));
								LinkedHashMap<String, String> uniCardMap = new LinkedHashMap<String, String>();
								String uniCardQry = WSClient.getQuery("GamingAssignCardID", "QS_04");
								uniCardMap = WSClient.getDBRow(uniCardQry);

								if (uniCardMap.size() > 0) {
									String expUniCardID = (uniCardMap.get("UNI_CARD_ID")) == null ? "": uniCardMap.get("UNI_CARD_ID");
									String actUniCardID = WSClient.getElementValue(assignCardIDReq,"AssignCardIDRequest_ptsPlayerCardID", XMLType.REQUEST);
									if (WSAssert.assertEquals(expUniCardID, actUniCardID, true)) {
										WSClient.writeToReport(LogStatus.PASS,"UNI_CARD_ID is updated correctly > Expected: " + expUniCardID+ " Actual: " + actUniCardID);
									} else {
										WSClient.writeToReport(LogStatus.FAIL,"UNI_CARD_ID is failed to be updated > Expected: " + expUniCardID	+ " Actual: " + actUniCardID);
									}
								}
							 } else {
								WSClient.writeToReport(LogStatus.FAIL, "gaming Card ID assignment is failed");
							}
						} else {
								WSClient.writeToReport(LogStatus.WARNING, "Subscription to Gaming Interface is failed");
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "No reservations found");
				}
			} else if (paramValue.equalsIgnoreCase("MEMBERSHIP")) {

				WSClient.writeToReport(LogStatus.INFO, "GAMING_ID Parameter Setting >> " + paramValue);

				// Parameters required for NewProfile Request
				WSClient.setData("{var_nameType}", "GUEST");

				/********************************************
				 * Create New Profile via Gaming API
				 *********************************************/
				
				// Construct New Profile Request and Post it
				String newProfileReq = WSClient.createSOAPMessage("GamingNewProfile", "DS_01");
				String newProfileRes = WSClient.processSOAPMessage(newProfileReq);

				// Validate if New Profile is Created successfully
				if (WSAssert.assertIfElementContains(newProfileRes, "NewProfileResponse_Result_resultStatusFlag",
						"SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(newProfileRes, "Result_IDs_IDPair_operaId", true)) {
						String profileID = WSClient.getElementValueByAttribute(newProfileRes,"Result_IDs_IDPair_operaId", "Result_IDs_IDPair_idType", "PROFILE", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,"New Profile is successfully created. " + "Opera Profile ID :" + profileID);
						String np_extProfileID = WSClient.getElementValue(newProfileReq, "Profile_ProfileIDs_UniqueID",	XMLType.REQUEST);

						// Set the data required for executing the queries
						WSClient.setData("{var_operaProfileID}", profileID);
						WSClient.setData("{var_externalProfileID}", np_extProfileID);

						/********************************************
						 * Assign ID Card to the above profile
						 *********************************************/
						
						// Construct Assign ID Card Request and Post it
						String assignCardIDReq = WSClient.createSOAPMessage("GamingAssignCardID", "DS_01");
						String assignCardIDRes = WSClient.processSOAPMessage(assignCardIDReq);

						// Validate if Card assignment is successful
						if (WSAssert.assertIfElementContains(assignCardIDRes, "AssignCardIDResponse_ResultStatus_resultStatusFlag","SUCCESS", false)) {
							LinkedHashMap<String, String> membershipMap = new LinkedHashMap<String, String>();
							String membershipsQry = WSClient.getQuery("GamingAssignCardID", "QS_02");
							membershipMap = WSClient.getDBRow(membershipsQry);

							if (membershipMap.size() > 0) {
								String expTrackData = (membershipMap.get("TRACK_DATA")) == null ? "": membershipMap.get("TRACK_DATA");
								String actTrackData = WSClient.getElementValue(assignCardIDReq,	"AssignCardIDRequest_ptsPlayerCardID", XMLType.REQUEST);
								if (WSAssert.assertEquals(actTrackData, expTrackData, true)) {
									WSClient.writeToReport(LogStatus.PASS,"TRACK_DATA is updated correctly > Expected: " + expTrackData + " Actual: "+ actTrackData);
								} else {
									WSClient.writeToReport(LogStatus.FAIL,"TRACK_DATA is failed to be updated > Expected: " + expTrackData+ " Actual: " + actTrackData);
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "No membership found with type PTS!");
							}

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "gaming Card ID assignment is failed");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"New Profile creation is failed, Profile ID is not populated on response");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"New Profile creation is failed, SUCCESS flag is not found on response");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL Error! " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test(groups = { "BAT", "AssignIDCard", "Profile", "Gaming", "assignCardID_2" })
	@Parameters({ "schema", "version" })
	public void assignCardID_2(String schema, String version) {
		WSClient.startTest("assignCardID","Verify that the assignCardID is functioning according to the GAMING_ID parameter", "bat");
		
		String sUsername = "", sPassword = "", sChain = "", sResort = "", qryAvailability="", sChannelType="", sChannelCarrier="", sExtResort="", sChannel="";
		boolean profileCreationFlag = false, createBookingFlag=false, availabilityFlag=false;
		try {
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			sChain = OPERALib.getChain();
			sResort = OPERALib.getResort();
			sChannel = OWSLib.getChannel();			
			sChannelType = OWSLib.getChannelType(sChannel);
			sChannelCarrier = OWSLib.getChannelCarier(sResort, sChannel);
			sExtResort = OWSLib.getChannelResort(sResort, sChannel);
			
			// Parameters required for the Gaming Header
			WSClient.setData("{var_userName}", sUsername);
			WSClient.setData("{var_password}", sPassword);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_resort}", sResort);
			WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
			WSClient.setData("{var_extResort}", sExtResort);
			WSClient.setData("{var_resort}", sResort);
			WSClient.setData("{var_systemType}", sChannelType);
			WSClient.setData("{var_channelCarrier}", sChannelCarrier);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_channel}", sChannel);

			/*****************************************
			 * Check the GAMING ID Parameter Setting 
			 *****************************************/
			
			String query = WSClient.getQuery("GamingAssignCardID", "QS_01", false);
			String paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE") == null ? ""
					: WSClient.getDBRow(query).get("PARAMETER_VALUE");

			if (paramValue == "") {
				WSClient.writeToReport(LogStatus.WARNING, "GAMING_ID Parameter is not configured");
			} else if (paramValue.equalsIgnoreCase("OFF")) {
				WSClient.writeToReport(LogStatus.WARNING, "GAMING_ID Parameter is configured as OFF");
			} else if (paramValue.equalsIgnoreCase("UNICARD")) {
				WSClient.writeToReport(LogStatus.INFO, "GAMING_ID Parameter Setting>> " + paramValue);


			/**********************************************************************
			 * Create New Profile via Gaming API
			 **********************************************************************/
				WSClient.writeToReport(LogStatus.INFO, "<b> Create a profile via Gaming API </b>");
				// Construct New Profile Request and Post it
				HashMap<wsAttributes, String> newProfileMap = new HashMap<wsAttributes, String>();
				newProfileMap = NewProfile.newProfileCreationGaming(schema, version, "DS_01");
				
				if(newProfileMap.get(wsAttributes.RESULT).equalsIgnoreCase("SUCCESS")) {
					String newProfileRes = newProfileMap.get(wsAttributes.RESPONSE);
					String profileID = WSClient.getElementValueByAttribute(newProfileRes,"Result_IDs_IDPair_operaId", "Result_IDs_IDPair_idType", "PROFILE", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_operaProfileID}", profileID);
					profileCreationFlag = true;
				}
				
			/***********************************************************************
			 * Prerequisite 2: Fetch Availability
			 ***********************************************************************/
				WSClient.writeToReport(LogStatus.INFO, "<b> Fetch Availability </b>");
				HashMap<String, String> availabilityMap = new HashMap<String, String>();
				String roomType = "", rateCode = "", beginDate = "", endDate = "", avResvType = "";
				// Proceed with retrieving availability if profile creation is successful
				if (profileCreationFlag == true) {
					
					// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
					WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
					
					// Retrieve the Channel Rate and Room Type that has rooms available from DB
					if(schema.equals("CENTRAL")) 
						qryAvailability = WSClient.getQuery("OWSAvailability", "QS_23");
					else 
						qryAvailability = WSClient.getQuery("OWSAvailability", "QS_24");	
					
					availabilityMap = WSClient.getDBRow(qryAvailability);

					if (availabilityMap.size() > 0) {
						roomType = availabilityMap.get("GDS_ROOM_CATEGORY");
						rateCode = availabilityMap.get("GDS_RATE_CODE");
						
						if(schema.equalsIgnoreCase("CENTRAL")) {
							beginDate = WSClient.getKeywordData("{KEYWORD_SYSTEMDATE_TFORMAT}").substring(0, 19);
							endDate = WSClient.getKeywordData("{KEYWORD_SYSTEMDATE_TFORMAT_ADD_24}").substring(0, 19);
						}
						else {
							beginDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19);
							endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19);
						}
						
						// Set the data required for Availability Request
						WSClient.setData("{var_startDate}", beginDate);
						WSClient.setData("{var_endDate}", endDate);
						WSClient.setData("{var_rateCode}", rateCode);
						WSClient.setData("{var_roomType}", roomType);

						// Fetch Availability for the given channel Room Type and
						// Rate Code
						String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_20");
						String res_availability = WSClient.processSOAPMessage(req_availability);

						// Validate if the response shows SUCCESS
						if (WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							if (WSAssert.assertIfElementExists(res_availability, "RoomStay_RatePlans_RatePlan_ratePlanCode", true) && WSAssert.assertIfElementExists(res_availability, "RoomStay_RoomTypes_RoomType_roomTypeCode", true)) {
								availabilityFlag = true;
								String avRate = WSClient.getElementValue(res_availability, "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String avRoomType = WSClient.getElementValue(res_availability, "RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.RESPONSE);
								avResvType = WSClient.getElementValue(res_availability, "RatePlan_GuaranteeDetails_Guarantee_guaranteeType", XMLType.RESPONSE);
								if(avResvType.contains("doesn't exist"))
									avResvType = null;
								WSClient.writeToReport(LogStatus.INFO, "<b>Availability exists for Rate Code -> " + avRate + " and Room Type -> " + avRoomType + "</b>");
							} 
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "No Rate Codes/Room Types are configured for the Channel "+sChannel);
					}
				}
				

				/***********************************************************************
				 * Prerequisite 3 : Create Booking
				 ***********************************************************************/
				
				WSClient.writeToReport(LogStatus.INFO, "<b> Create a reservation for the subscribed profile </b>");
				
				String reservationID = "", confirmationNumber = "";
				// Proceed with Reservation Creation if Availability Exists
				if (availabilityFlag == true) {
					
					// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
					WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
					
					// Set the data required for Create Booking Request
					if(avResvType != null)
						WSClient.setData("{var_conditional_param_resvType}", avResvType);

					// Create Reservation
					String req_createBooking = WSClient.createSOAPMessage("OWSCreateBooking", "DS_48");
					String res_createBooking = WSClient.processSOAPMessage(req_createBooking);

					// Validate if the response shows SUCCESS
					if (WSAssert.assertIfElementValueEquals(res_createBooking, "CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
						if(WSAssert.assertIfElementExists(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", true)) {
							createBookingFlag = true;
							reservationID = WSClient.getElementValueByAttribute(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							confirmationNumber = WSClient.getElementValue(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationID + " Confirmation Number: " + confirmationNumber + "</b>");
							WSClient.setData("{var_reservationNameID}", reservationID);
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Booking Creation is failed, Cannot proceed further.");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Booking Creation is failed, Cannot proceed further.");
					}
				}
				
				/********************************************
				 * Assign ID Card to the subscribed profile
				 *********************************************/
				if(createBookingFlag == true) {	
				
						WSClient.writeToReport(LogStatus.INFO, "<b> Assign Card ID to the subscribed profile </b>");
						
						// Construct Assign ID Card Request and Post it
						String assignCardIDReq = WSClient.createSOAPMessage("GamingAssignCardID", "DS_01");
						String assignCardIDRes = WSClient.processSOAPMessage(assignCardIDReq);
		
						// Validate if Card assignment is successful
						if (WSAssert.assertIfElementContains(assignCardIDRes, "AssignCardIDResponse_ResultStatus_resultStatusFlag","SUCCESS", false)) {
							
							LinkedHashMap<String, String> uniCardMap = new LinkedHashMap<String, String>();
							String uniCardQry = WSClient.getQuery("GamingAssignCardID", "QS_04");
							uniCardMap = WSClient.getDBRow(uniCardQry);
		
							if (uniCardMap.size() > 0) {
								String expUniCardID = (uniCardMap.get("UNI_CARD_ID")) == null ? "": uniCardMap.get("UNI_CARD_ID");
								String actUniCardID = WSClient.getElementValue(assignCardIDReq,"AssignCardIDRequest_ptsPlayerCardID", XMLType.REQUEST);
								if (WSAssert.assertEquals(expUniCardID, actUniCardID, true)) {
									WSClient.writeToReport(LogStatus.PASS,"UNI_CARD_ID is updated correctly > Expected: " + expUniCardID+ " Actual: " + actUniCardID);
								} else {
									WSClient.writeToReport(LogStatus.FAIL,"UNI_CARD_ID is failed to be updated > Expected: " + expUniCardID	+ " Actual: " + actUniCardID);
								}
							}
						 } else {
							WSClient.writeToReport(LogStatus.FAIL, "gaming Card ID assignment is failed");
						}
				}
				
			} else if (paramValue.equalsIgnoreCase("MEMBERSHIP")) {
				WSClient.writeToReport(LogStatus.INFO, "GAMING_ID Parameter Setting >> " + paramValue);

				// Parameters required for NewProfile Request
				WSClient.setData("{var_nameType}", "GUEST");

				/********************************************
				 * Create New Profile via Gaming API
				 *********************************************/
				
				// Construct New Profile Request and Post it
				HashMap<wsAttributes, String> newProfileMap = new HashMap<wsAttributes, String>();
				newProfileMap = NewProfile.newProfileCreationGaming(schema, version, "DS_01");
				
				if(newProfileMap.get(wsAttributes.RESULT).equalsIgnoreCase("SUCCESS")) {
						String newProfileReq = newProfileMap.get(wsAttributes.REQUEST);
						String newProfileRes = newProfileMap.get(wsAttributes.RESPONSE);
				
						String profileID = WSClient.getElementValueByAttribute(newProfileRes,"Result_IDs_IDPair_operaId", "Result_IDs_IDPair_idType", "PROFILE", XMLType.RESPONSE);
						String np_extProfileID = WSClient.getElementValue(newProfileReq, "Profile_ProfileIDs_UniqueID",	XMLType.REQUEST);

						// Set the data required for executing the queries
						WSClient.setData("{var_operaProfileID}", profileID);
						WSClient.setData("{var_externalProfileID}", np_extProfileID);

						/********************************************
						 * Assign ID Card to the above profile
						 *********************************************/
						
						// Construct Assign ID Card Request and Post it
						String assignCardIDReq = WSClient.createSOAPMessage("GamingAssignCardID", "DS_01");
						String assignCardIDRes = WSClient.processSOAPMessage(assignCardIDReq);

						// Validate if Card assignment is successful
						if (WSAssert.assertIfElementContains(assignCardIDRes, "AssignCardIDResponse_ResultStatus_resultStatusFlag","SUCCESS", false)) {
							LinkedHashMap<String, String> membershipMap = new LinkedHashMap<String, String>();
							String membershipsQry = WSClient.getQuery("GamingAssignCardID", "QS_02");
							membershipMap = WSClient.getDBRow(membershipsQry);

							if (membershipMap.size() > 0) {
								String expTrackData = (membershipMap.get("TRACK_DATA")) == null ? "": membershipMap.get("TRACK_DATA");
								String actTrackData = WSClient.getElementValue(assignCardIDReq,	"AssignCardIDRequest_ptsPlayerCardID", XMLType.REQUEST);
								if (WSAssert.assertEquals(actTrackData, expTrackData, true)) {
									WSClient.writeToReport(LogStatus.PASS,"TRACK_DATA is updated correctly > Expected: " + expTrackData + " Actual: "+ actTrackData);
								} else {
									WSClient.writeToReport(LogStatus.FAIL,"TRACK_DATA is failed to be updated > Expected: " + expTrackData+ " Actual: " + actTrackData);
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "No membership found with type PTS!");
							}

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "gaming Card ID assignment is failed");
						}
					} 
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL Error! " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
