package com.oracle.hgbu.opera.qaauto.ws.testcases.integrationprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.integrationprocessor.FetchBusinessEvents;
import com.oracle.hgbu.opera.qaauto.ws.components.reservation.FetchReservation;
import com.oracle.hgbu.opera.qaauto.ws.testcases.reservation.CreateReservationTest;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;
public class FetchBusinessEventsTest  extends WSSetUp {

	@Test(groups = { "sanity", "FetchBusinessEvents", "INT"})
	@Parameters({"runOnEntry"})
	public void dequeueBusinessEvents(String runOnEntry) {
		WSClient.startTest("DequeueBusinessEvents","Verify that the NEW RESERVATION business events that are ready to be dequeued are retrieved","sanity");
		WSClient.setResortEntry(runOnEntry);
		HashMap<XMLType, String> fetchBusinessEventsPayloads = new HashMap<XMLType, String>();
		try {
			//Calling the method from another module project for creating a basic reservation
			String reservationID = CreateReservationTest.createBasicReservation(runOnEntry);
			//Proceed to invoke FetchBusinessEvents operation only if reservation is successfully created
			if(!(reservationID.isEmpty()) && reservationID != null && reservationID !="") {

				//Calling the component defined for fetchBusinessEvents operation to get the executed payloads
				fetchBusinessEventsPayloads = FetchBusinessEvents.fetchBusinessEventsPayloads("DS_01",runOnEntry);
				if(fetchBusinessEventsPayloads.size() > 0) {
					//Read the Response XML file that was returned upon executing the component
					String fetchBusinessEventResponse = fetchBusinessEventsPayloads.get(XMLType.RESPONSE);
					//Read the Operation Keyword for fetchBusinessEvents for further use
					String fetchBusinessEventsOperationKey = fetchBusinessEventsPayloads.get(XMLType.OPERATION_KEY);

					//Validate if "Success" flag exists on the response message
					if (WSAssert.assertIfElementExists(fetchBusinessEventResponse, "FetchBusinessEventsRS_Success", false, fetchBusinessEventsOperationKey)) {

						//Print result as "Pass" if Success flag exists
						WSClient.writeToReport(LogStatus.PASS, "Dequeued events are retrieved; Result Status is SUCCESS");

						//Since there could be many reservations ready to dequeue, the below statement is written to get the node index of the reservation that was created above in this script
						//Third parameter is the parent XPath of the element that needs to be validated
						int requiredNodeIndex = WSClient.getNodeIndex(fetchBusinessEventResponse, reservationID, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_PrimaryKey", XMLType.RESPONSE, fetchBusinessEventsOperationKey);

						if(requiredNodeIndex > 0) {

							//Validate the PrimaryKey, Module Name and Action Type are correctly populated on the FetchBusinessEvents response message for the reservation created above in this script, by passing the node index of the reservation ID
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_PrimaryKey", requiredNodeIndex, reservationID, XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_ModuleName", requiredNodeIndex, "RESERVATION", XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_ActionType", requiredNodeIndex, "NEW RESERVATION", XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_HotelCode", requiredNodeIndex, WSConfig.getResort(runOnEntry), XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);

							//Prepare MAP with the elements to be validated for the above created reservation. KEY = Element XPath VALUE = Element's Parent XPath
							HashMap<String,String> inputXPaths = new HashMap<String,String>();
							inputXPaths.put("Details_Detail_DataElement", "BusinessEvent_Details_Detail");
							inputXPaths.put("Details_Detail_NewValue", "BusinessEvent_Details_Detail");

							//Pass the above MAP to the below method along with the ROOT Element's XPath and the Index (To identify the BUSINESS EVENT RECORD corresponding the above created reservation since there are multiple business events with different reservations)
							List<LinkedHashMap<String,String>> reservationDataElementsOnFBEResponse = new ArrayList<LinkedHashMap<String,String>>();
							reservationDataElementsOnFBEResponse = WSClient.getElementValuesOfARecordMatchingWithGivenIndex(fetchBusinessEventResponse, inputXPaths, XMLType.RESPONSE, fetchBusinessEventsOperationKey, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", requiredNodeIndex);

							//Invoke FetchReservation Operation to fetch the details on the above reservation ID
							HashMap<XMLType,String> fetchResvMap = new HashMap<XMLType,String>();
							WSClient.setGlobalData("{var_resvId}", reservationID);
							fetchResvMap = FetchReservation.fetchReservationPayloads("DS_01", runOnEntry);
							String fetchResvResponse = fetchResvMap.get(XMLType.RESPONSE);
							String fetchResvOperationKey = fetchResvMap.get(XMLType.OPERATION_KEY);

							//Read Data elements of the reservation from FetchBusinessEvents response and store in HashMap
							LinkedHashMap<String,String> fbeActResponseMap = new LinkedHashMap<String,String>();
							for(int i=0; i<reservationDataElementsOnFBEResponse.size();i++) {
								LinkedHashMap<String, String> tmpMap = new LinkedHashMap<String, String> ();
								tmpMap = reservationDataElementsOnFBEResponse.get(i);
								switch(tmpMap.get("DataElement1"))
								{
								case "NAME":
									fbeActResponseMap.put("NAME", tmpMap.get("NewValue1"));
									break;
								case "CONFIRMATION NO":
									fbeActResponseMap.put("CONFIRMATION NO", tmpMap.get("NewValue1"));
									break;
								case "GUEST NAME ID":
									fbeActResponseMap.put("GUEST NAME ID", tmpMap.get("NewValue1"));
									break;
								case "RESERVATION STATUS":
									fbeActResponseMap.put("RESERVATION STATUS", tmpMap.get("NewValue1"));
									break;
								case "ARRIVAL DATE":
									fbeActResponseMap.put("ARRIVAL DATE", tmpMap.get("NewValue1"));
									break;
								case "DEPARTURE DATE":
									fbeActResponseMap.put("DEPARTURE DATE", tmpMap.get("NewValue1"));
									break;
								case "NUMBER OF ROOMS":
									fbeActResponseMap.put("NUMBER OF ROOMS", tmpMap.get("NewValue1"));
									break;
								case "ROOM TYPE":
									fbeActResponseMap.put("ROOM TYPE", tmpMap.get("NewValue1"));
									break;
								case "RTC":
									fbeActResponseMap.put("RTC", tmpMap.get("NewValue1"));
									break;
								case "RATE CODE":
									fbeActResponseMap.put("RATE CODE", tmpMap.get("NewValue1"));
									break;
								case "SOURCE CODE":
									fbeActResponseMap.put("SOURCE CODE", tmpMap.get("NewValue1"));
									break;
								case "MARKET CODE":
									fbeActResponseMap.put("MARKET CODE", tmpMap.get("NewValue1"));
									break;
								default:
									//System.out.println("no match");
								}
							}

							//Read Data elements of the reservation from FetchReservation response and store in HashMap
							LinkedHashMap<String,String> fbeExpResponseMap = new LinkedHashMap<String,String>();
							fbeExpResponseMap.put("NAME", WSClient.getElementValue(fetchResvResponse, "Customer_PersonName_Surname_3", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("CONFIRMATION NO", WSClient.getAttributeValueByAttribute(fetchResvResponse, "Reservation_ReservationIDList_UniqueID", "ID", "Confirmation", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("GUEST NAME ID", WSClient.getElementValue(fetchResvResponse, "ProfileInfo_ProfileIDList_UniqueID_ID", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("RESERVATION STATUS", "PROSPECT");
							fbeExpResponseMap.put("ARRIVAL DATE", WSClient.getElementValue(fetchResvResponse, "RoomStay_TimeSpan_StartDate_3", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("DEPARTURE DATE", WSClient.getElementValue(fetchResvResponse, "RoomStay_TimeSpan_EndDate_3", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("NUMBER OF ROOMS", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_NumberOfUnits", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("ROOM TYPE", WSClient.getElementValue(fetchResvResponse, "RoomStay_CurrentRoomInfo_RoomType", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("RTC", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_RoomTypeCharged", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("RATE CODE", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_RatePlanCode", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("SOURCE CODE", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_SourceOfBusiness", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("MARKET CODE", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_MarketCode", XMLType.RESPONSE, fetchResvOperationKey));

							//Verify that the elements on the FBE response are holding correct data same as what is displayed on the FetchReservation response
							WSAssert.assertEquals(fbeExpResponseMap, fbeActResponseMap, false);
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Reservation "+reservationID+" is not found on the response message");
						}
					}
					else {
						//Print result as "Fail" if Success flag exists
						WSClient.writeToReport(LogStatus.PASS, "Dequeued events are not retrieved; Result Status is not SUCCESS");
					}
				}
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "Cannot dequeue the business events as the reservation creation is failed");
			}
		}
		catch(Exception e) {
			System.out.println("Error in verifying the dequeue process of the business event "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test(groups = { "sanity", "FetchBusinessEvents", "INT","poiuytr"})
	@Parameters({"runOnEntry"})
	public void dequeueBusinessEventsUpdateReservation(String runOnEntry) {
		WSClient.startTest("dequeueBusinessEventsUpdateReservation","Verify that the RESERVATION is updated and business events that are ready to be dequeued are retrieved","sanity");
		WSClient.setResortEntry(runOnEntry);
		HashMap<XMLType, String> fetchBusinessEventsPayloads = new HashMap<XMLType, String>();
		try {
			//Calling the method from another module project for creating a basic reservation
			String reservationID = CreateReservationTest.createBasicReservation(runOnEntry);
			
			WSClient.setGlobalData("{var_resvId}", reservationID);
//			modifyBooking();
			
			//Proceed to invoke FetchBusinessEvents operation only if reservation is successfully created
			if(!(reservationID.isEmpty()) && reservationID != null && reservationID !="") {

				//Calling the component defined for fetchBusinessEvents operation to get the executed payloads
				fetchBusinessEventsPayloads = FetchBusinessEvents.fetchBusinessEventsPayloads("DS_02",runOnEntry);
				if(fetchBusinessEventsPayloads.size() > 0) {
					//Read the Response XML file that was returned upon executing the component
					String fetchBusinessEventResponse = fetchBusinessEventsPayloads.get(XMLType.RESPONSE);
					//Read the Operation Keyword for fetchBusinessEvents for further use
					String fetchBusinessEventsOperationKey = fetchBusinessEventsPayloads.get(XMLType.OPERATION_KEY);

					//Validate if "Success" flag exists on the response message
					if (WSAssert.assertIfElementExists(fetchBusinessEventResponse, "FetchBusinessEventsRS_Success", false, fetchBusinessEventsOperationKey)) {

						//Print result as "Pass" if Success flag exists
						WSClient.writeToReport(LogStatus.PASS, "Dequeued events are retrieved; Result Status is SUCCESS");

						//Since there could be many reservations ready to dequeue, the below statement is written to get the node index of the reservation that was created above in this script
						//Third parameter is the parent XPath of the element that needs to be validated
						int requiredNodeIndex = WSClient.getNodeIndex(fetchBusinessEventResponse, reservationID, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_PrimaryKey", XMLType.RESPONSE, fetchBusinessEventsOperationKey);

						if(requiredNodeIndex > 0) {

							//Validate the PrimaryKey, Module Name and Action Type are correctly populated on the FetchBusinessEvents response message for the reservation created above in this script, by passing the node index of the reservation ID
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_PrimaryKey", requiredNodeIndex, reservationID, XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_ModuleName", requiredNodeIndex, "RESERVATION", XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_ActionType", requiredNodeIndex, "NEW RESERVATION", XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);
							WSAssert.assertIfElementValueEqualsByIndex(fetchBusinessEventResponse, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", "BusinessEvent_Header_HotelCode", requiredNodeIndex, WSConfig.getResort(runOnEntry), XMLType.RESPONSE, false, fetchBusinessEventsOperationKey);

							//Prepare MAP with the elements to be validated for the above created reservation. KEY = Element XPath VALUE = Element's Parent XPath
							HashMap<String,String> inputXPaths = new HashMap<String,String>();
							inputXPaths.put("Details_Detail_DataElement", "BusinessEvent_Details_Detail");
							inputXPaths.put("Details_Detail_NewValue", "BusinessEvent_Details_Detail");

							//Pass the above MAP to the below method along with the ROOT Element's XPath and the Index (To identify the BUSINESS EVENT RECORD corresponding the above created reservation since there are multiple business events with different reservations)
							List<LinkedHashMap<String,String>> reservationDataElementsOnFBEResponse = new ArrayList<LinkedHashMap<String,String>>();
							reservationDataElementsOnFBEResponse = WSClient.getElementValuesOfARecordMatchingWithGivenIndex(fetchBusinessEventResponse, inputXPaths, XMLType.RESPONSE, fetchBusinessEventsOperationKey, "FetchBusinessEventsRS_BusinessEventsList_BusinessEventData", requiredNodeIndex);

							//Invoke FetchReservation Operation to fetch the details on the above reservation ID
							HashMap<XMLType,String> fetchResvMap = new HashMap<XMLType,String>();
							WSClient.setGlobalData("{var_resvId}", reservationID);
							fetchResvMap = FetchReservation.fetchReservationPayloads("DS_01", runOnEntry);
							String fetchResvResponse = fetchResvMap.get(XMLType.RESPONSE);
							String fetchResvOperationKey = fetchResvMap.get(XMLType.OPERATION_KEY);

							//Read Data elements of the reservation from FetchBusinessEvents response and store in HashMap
							LinkedHashMap<String,String> fbeActResponseMap = new LinkedHashMap<String,String>();
							for(int i=0; i<reservationDataElementsOnFBEResponse.size();i++) {
								LinkedHashMap<String, String> tmpMap = new LinkedHashMap<String, String> ();
								tmpMap = reservationDataElementsOnFBEResponse.get(i);
								switch(tmpMap.get("DataElement1"))
								{
								case "NAME":
									fbeActResponseMap.put("NAME", tmpMap.get("NewValue1"));
									break;
								case "CONFIRMATION NO":
									fbeActResponseMap.put("CONFIRMATION NO", tmpMap.get("NewValue1"));
									break;
								case "GUEST NAME ID":
									fbeActResponseMap.put("GUEST NAME ID", tmpMap.get("NewValue1"));
									break;
								case "RESERVATION STATUS":
									fbeActResponseMap.put("RESERVATION STATUS", tmpMap.get("NewValue1"));
									break;
								case "ARRIVAL DATE":
									fbeActResponseMap.put("ARRIVAL DATE", tmpMap.get("NewValue1"));
									break;
								case "DEPARTURE DATE":
									fbeActResponseMap.put("DEPARTURE DATE", tmpMap.get("NewValue1"));
									break;
								case "NUMBER OF ROOMS":
									fbeActResponseMap.put("NUMBER OF ROOMS", tmpMap.get("NewValue1"));
									break;
								case "ROOM TYPE":
									fbeActResponseMap.put("ROOM TYPE", tmpMap.get("NewValue1"));
									break;
								case "RTC":
									fbeActResponseMap.put("RTC", tmpMap.get("NewValue1"));
									break;
								case "RATE CODE":
									fbeActResponseMap.put("RATE CODE", tmpMap.get("NewValue1"));
									break;
								case "SOURCE CODE":
									fbeActResponseMap.put("SOURCE CODE", tmpMap.get("NewValue1"));
									break;
								case "MARKET CODE":
									fbeActResponseMap.put("MARKET CODE", tmpMap.get("NewValue1"));
									break;
								default:
									//System.out.println("no match");
								}
							}

							//Read Data elements of the reservation from FetchReservation response and store in HashMap
							LinkedHashMap<String,String> fbeExpResponseMap = new LinkedHashMap<String,String>();
							fbeExpResponseMap.put("NAME", WSClient.getElementValue(fetchResvResponse, "Customer_PersonName_Surname_3", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("CONFIRMATION NO", WSClient.getAttributeValueByAttribute(fetchResvResponse, "Reservation_ReservationIDList_UniqueID", "ID", "Confirmation", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("GUEST NAME ID", WSClient.getElementValue(fetchResvResponse, "ProfileInfo_ProfileIDList_UniqueID_ID", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("RESERVATION STATUS", "PROSPECT");
							fbeExpResponseMap.put("ARRIVAL DATE", WSClient.getElementValue(fetchResvResponse, "RoomStay_TimeSpan_StartDate_3", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("DEPARTURE DATE", WSClient.getElementValue(fetchResvResponse, "RoomStay_TimeSpan_EndDate_3", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("NUMBER OF ROOMS", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_NumberOfUnits", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("ROOM TYPE", WSClient.getElementValue(fetchResvResponse, "RoomStay_CurrentRoomInfo_RoomType", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("RTC", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_RoomTypeCharged", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("RATE CODE", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_RatePlanCode", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("SOURCE CODE", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_SourceOfBusiness", XMLType.RESPONSE, fetchResvOperationKey));
							fbeExpResponseMap.put("MARKET CODE", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_MarketCode", XMLType.RESPONSE, fetchResvOperationKey));

							//Verify that the elements on the FBE response are holding correct data same as what is displayed on the FetchReservation response
							WSAssert.assertEquals(fbeExpResponseMap, fbeActResponseMap, false);
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Reservation "+reservationID+" is not found on the response message");
						}
					}
					else {
						//Print result as "Fail" if Success flag exists
						WSClient.writeToReport(LogStatus.PASS, "Dequeued events are not retrieved; Result Status is not SUCCESS");
					}
				}
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "Cannot dequeue the business events as the reservation creation is failed");
			}
		}
		catch(Exception e) {
			System.out.println("Error in verifying the dequeue process of the business event "+e.getMessage());
			e.printStackTrace();
		}
	}



	public void modifyBooking(){
		
		String modifyBookingReq;
		try {
			modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_37");
			String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}


