package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2006.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

public class CreateActivity extends WSSetUp {

	String profileID="";

	@Test(groups = { "sanity", "CreateActivity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20395() {
		try {
			String testName = "createActivity_2006_20395";
			WSClient.startTest(testName, "Verify that the activity is being created for the requested Profile", "sanity");
			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			//************Prerequisite 1 - Opera Create Profile******************************************//
			OPERALib.setOperaHeader(uname);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

				//************Prerequisite 2 - HTNG Subscription******************************************//
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

					//************HTNG Create Activity*************************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {

						//*****Validation 1 ******//
						WSClient.writeToReport(LogStatus.INFO,"<b>Validation 1</b>");
						LinkedHashMap<String, String> link = WSClient.getDBRow(WSClient.getQuery("QS_01"));
						LinkedHashMap<String,String> linkres=new LinkedHashMap<String,String>();
						linkres.put("LINK_TYPE","OSAPROFILE");
						linkres.put("LINK_ID",WSClient.getElementValue(createActivityReq,
								"CreateActivityRequest_ProfileID", XMLType.REQUEST));
						WSAssert.assertEquals(linkres, link, false);

						//*********Validation 2 ***********************//
						WSClient.writeToReport(LogStatus.INFO,"<b>Validation 2</b>");
						String operaId = WSClient.getElementValueByAttribute(createActivityResponseXML,
								"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						String externalId = WSClient.getElementValueByAttribute(createActivityResponseXML,
								"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
								XMLType.RESPONSE);

						LinkedHashMap<String, String> detail1 = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						LinkedHashMap<String,String> detail1res=new LinkedHashMap<String,String>();
						detail1res.put("EXTERNAL_SYSTEM_ID",externalId);
						detail1res.put("WO_NUMBER",operaId);
						detail1res.put("GUEST_NAME_ID",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_ProfileID", XMLType.REQUEST));
						detail1res.put("EXTERNAL_SYSTEM",WSClient.getElementValue(createActivityReq,
								"Activity_ActivityIDs_UniqueID_source", XMLType.REQUEST));
						detail1res.put("RESORT",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_ResortId", XMLType.REQUEST));

						WSAssert.assertEquals(detail1res, detail1, false);

						//*********Validation 3 ************************//
						WSClient.writeToReport(LogStatus.INFO,"<b>Validation 3</b>");
						LinkedHashMap<String, String> detail2 = WSClient.getDBRow(WSClient.getQuery("QS_08"));
						LinkedHashMap<String,String> detail2res=new LinkedHashMap<String,String>();
						detail2res.put("EAS_ACTIVITY_ID",externalId);
						detail2res.put("ACTIVITY_ID",operaId);
						detail2res.put("EXTERNAL_SYSTEM",WSClient.getElementValue(createActivityReq,
								"Activity_ActivityIDs_UniqueID_source", XMLType.REQUEST));
						detail2res.put("RESORT",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_ResortId", XMLType.REQUEST));

						WSAssert.assertEquals(detail2res, detail2, false);


					}

					//Checking for result  text
					if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){
						String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "Create Activity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20393() {
		try {
			String testName = "createActivity_2006_20393";
			WSClient.startTest(testName, "Verify an activity should be created for the requested ReservationID when minimum requried data is passed", "minimumRegression");

			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			//************Prerequisite 1 - Opera Create Profile******************************************//
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"})) {

				OPERALib.setOperaHeader(uname);

				if(profileID.equals(""))
					profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileID}", profileID);

					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

					//************Prerequisite 2 - HTNG Subscription*****************************************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

						//******************** Prerequisite 3 - Create Reservation****************************//

						OPERALib.setOperaHeader(uname);

						HashMap<String,String> resv=CreateReservation.createReservation("DS_05");
						String resvID = resv.get("reservationId");

						if(!resvID.equals("error"))
						{

							WSClient.setData("{var_resvId}", resvID);


							WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));

							WSClient.setData("{var_extResort}", resortExtValue);
							WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
							WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
							WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

							// **********************HTNG Creating activity*****************************//

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_02");
							String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

							if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
									"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {

								//*******************Validation 1*************//
								WSClient.writeToReport(LogStatus.INFO,"<b>Validation 1</b>");
								LinkedHashMap<String, String> link = WSClient.getDBRow(WSClient.getQuery("QS_06"));
								LinkedHashMap<String,String> linkres=new LinkedHashMap<String,String>();
								linkres.put("LINK_TYPE","OSARESERVATION");
								linkres.put("LINK_ID",WSClient.getElementValue(createActivityReq,
										"CreateActivityRequest_ReservationHeader_ReservationID", XMLType.REQUEST));
								WSAssert.assertEquals(linkres, link, false);

								//*******************Validation 2*************//
								WSClient.writeToReport(LogStatus.INFO,"<b>Validation 2</b>");
								String operaId = WSClient.getElementValueByAttribute(createActivityResponseXML,
										"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
								String externalId = WSClient.getElementValueByAttribute(createActivityResponseXML,
										"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
										XMLType.RESPONSE);
								LinkedHashMap<String, String> detail1 = WSClient.getDBRow(WSClient.getQuery("QS_04"));
								LinkedHashMap<String,String> detail1res=new LinkedHashMap<String,String>();
								detail1res.put("EXTERNAL_SYSTEM_ID",externalId);
								detail1res.put("WO_NUMBER",operaId);
								detail1res.put("EXTERNAL_SYSTEM",WSClient.getElementValue(createActivityReq,
										"Activity_ActivityIDs_UniqueID_source", XMLType.REQUEST));
								detail1res.put("RESV_NAME_ID",WSClient.getElementValue(
										createActivityReq, "CreateActivityRequest_ReservationHeader_ReservationID", XMLType.REQUEST));
								detail1res.put("RESORT",WSClient.getElementValue(
										createActivityReq, "CreateActivityRequest_ResortId", XMLType.REQUEST));
								detail1res.put("ACTIVITY_LOCATION",WSClient.getElementValue(
										createActivityReq, "Activities_Activity_Location", XMLType.REQUEST));
								detail1res.put("ACTIVITY_STATUS",WSClient.getElementValue(
										createActivityReq, "CreateActivityRequest_Activities_Activity_status", XMLType.REQUEST));
								detail1res.put("ACTIVITY_TYPE",WSClient.getElementValue(
										createActivityReq, "Activities_Activity_ActivityType", XMLType.REQUEST));
								String sDate_ = WSClient.getElementValue(createActivityReq, "Activity_TimeSpan_Start",
										XMLType.REQUEST);
								String eDate_ = WSClient.getElementValue(createActivityReq, "Activity_TimeSpan_End",
										XMLType.REQUEST);
								String sdate=sDate_.replace("T", " ").substring(0,19)+".0";
								String stime=sDate_.substring(11,16);
								String edate=eDate_.replace("T", " ").substring(0,19)+".0";
								String etime=eDate_.substring(11,16);
								detail1res.put("START_DATE",sdate);
								//detail1res.put("START_TIME",stime);
								detail1res.put("END_DATE",edate);
								//detail1res.put("END_TIME",etime);

								WSAssert.assertEquals(detail1res, detail1, false);

								//*******************Validation 3*************//
								WSClient.writeToReport(LogStatus.INFO,"<b>Validation 3</b>");
								LinkedHashMap<String, String> detail2 = WSClient.getDBRow(WSClient.getQuery("QS_05"));
								LinkedHashMap<String,String> detail2res=new LinkedHashMap<String,String>();
								detail2res.put("EAS_ACTIVITY_ID",externalId);
								detail2res.put("ACTIVITY_ID",operaId);
								detail2res.put("EXTERNAL_SYSTEM",WSClient.getElementValue(createActivityReq,
										"Activity_ActivityIDs_UniqueID_source", XMLType.REQUEST));
								detail2res.put("RESORT",WSClient.getElementValue(
										createActivityReq, "CreateActivityRequest_ResortId", XMLType.REQUEST));
								detail2res.put("ACTIVITY_LOCATION",WSClient.getElementValue(
										createActivityReq, "Activities_Activity_Location", XMLType.REQUEST));
								detail2res.put("ACTIVITY_STATUS",WSClient.getElementValue(
										createActivityReq, "CreateActivityRequest_Activities_Activity_status", XMLType.REQUEST));
								detail2res.put("ACTIVITY_TYPE",WSClient.getElementValue(
										createActivityReq, "Activities_Activity_ActivityType", XMLType.REQUEST));
								detail2res.put("START_DATE",sdate);
								//detail2res.put("START_TIME",stime);
								detail2res.put("END_DATE",edate);
								//detail2res.put("END_TIME",etime);

								WSAssert.assertEquals(detail2res, detail2, false);


							}
							//Checking for result  text
							if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){

								String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
							}
						}
					}

					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "***************Blocked : Subscription not created**************");
					}
				}
			}
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{
			// Cancel Reservation
			try
			{
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "Create Activity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20394() {
		try {
			String testName = "createActivity_2006_20394";
			WSClient.startTest(testName, "Verify an activity should be created for the requested ProfileID when NumberOfPersons, notes, Amount, DepositAmount, DepositAmount.collectedBy along with the minimum required data are passed", "minimum regression");

			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);


			//************Prerequisite 1 - Opera Create Profile******************************************//
			OPERALib.setOperaHeader(uname);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
				WSClient.setData("{var_profileID}", profileID);

				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

				//************Prerequisite 2 - HTNG Subscription******************************************//
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {


					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

					//**************HTNG Create Activity**********************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_03");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {

						//*******Validation 1**********//
						WSClient.writeToReport(LogStatus.INFO,"<b>Validation 1</b>");
						LinkedHashMap<String, String> link = WSClient.getDBRow(WSClient.getQuery("QS_01"));
						LinkedHashMap<String,String> linkres=new LinkedHashMap<String,String>();
						linkres.put("LINK_TYPE","OSAPROFILE");
						linkres.put("LINK_ID",WSClient.getElementValue(createActivityReq,
								"CreateActivityRequest_ProfileID", XMLType.REQUEST));
						WSAssert.assertEquals(linkres, link, false);

						//*******Validation 2**********//
						WSClient.writeToReport(LogStatus.INFO,"<b>Validation 2</b>");
						String operaId = WSClient.getElementValueByAttribute(createActivityResponseXML,
								"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE);
						String externalId = WSClient.getElementValueByAttribute(createActivityResponseXML,
								"Results_IDs_UniqueID", "Results_IDs_UniqueID_source", interfaceName,
								XMLType.RESPONSE);

						LinkedHashMap<String, String> detail1 = WSClient.getDBRow(WSClient.getQuery("QS_07"));

						LinkedHashMap<String,String> detail1res=new LinkedHashMap<String,String>();
						detail1res.put("EXTERNAL_SYSTEM_ID",externalId);
						detail1res.put("WO_NUMBER",operaId);
						detail1res.put("EXTERNAL_SYSTEM",WSClient.getElementValue(createActivityReq,
								"Activity_ActivityIDs_UniqueID_source", XMLType.REQUEST));
						detail1res.put("GUEST_NAME_ID",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_ProfileID", XMLType.REQUEST));
						detail1res.put("RESORT",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_ResortId", XMLType.REQUEST));
						detail1res.put("ACTIVITY_LOCATION",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_Location", XMLType.REQUEST));
						detail1res.put("ACTIVITY_STATUS",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_Activities_Activity_status", XMLType.REQUEST));
						detail1res.put("ACTIVITY_TYPE",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_ActivityType", XMLType.REQUEST));
						detail1res.put("ATTENDEES",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_NumberOfPersons", XMLType.REQUEST));
						detail1res.put("ACTIVITY_AMOUNT",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_Amount", XMLType.REQUEST));
						detail1res.put("DEPOSIT_AMOUNT",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_DepositRequired", XMLType.REQUEST));
						detail1res.put("DEPOSIT_OWNER",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_DepositRequired_collectedBy", XMLType.REQUEST));
						detail1res.put("NOTES",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_Note", XMLType.REQUEST));

						String sDate_ = WSClient.getElementValue(createActivityReq, "Activity_TimeSpan_Start",
								XMLType.REQUEST);
						String eDate_ = WSClient.getElementValue(createActivityReq, "Activity_TimeSpan_End",
								XMLType.REQUEST);

						String sdate=sDate_.replace("T", " ").substring(0,19)+".0";
						String stime=sDate_.substring(11,16);
						String edate=eDate_.replace("T", " ").substring(0,19)+".0";
						String etime=eDate_.substring(11,16);
						detail1res.put("START_DATE",sdate);
						//detail1res.put("START_TIME",stime);
						detail1res.put("END_DATE",edate);
						//detail1res.put("END_TIME",etime);
						WSAssert.assertEquals(detail1res, detail1, false);

						//*******Validation 3**********//
						WSClient.writeToReport(LogStatus.INFO,"<b>Validation 3</b>");
						LinkedHashMap<String, String> detail2 = WSClient.getDBRow(WSClient.getQuery("QS_03"));
						LinkedHashMap<String,String> detail2res=new LinkedHashMap<String,String>();

						detail2res.put("EAS_ACTIVITY_ID",externalId);
						detail2res.put("ACTIVITY_ID",operaId);
						detail2res.put("EXTERNAL_SYSTEM",WSClient.getElementValue(createActivityReq,
								"Activity_ActivityIDs_UniqueID_source", XMLType.REQUEST));
						detail2res.put("RESORT",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_ResortId", XMLType.REQUEST));
						detail2res.put("ACTIVITY_LOCATION",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_Location", XMLType.REQUEST));
						detail2res.put("ACTIVITY_STATUS",WSClient.getElementValue(
								createActivityReq, "CreateActivityRequest_Activities_Activity_status", XMLType.REQUEST));
						detail2res.put("ACTIVITY_TYPE",WSClient.getElementValue(
								createActivityReq, "Activities_Activity_ActivityType", XMLType.REQUEST));
						detail2res.put("START_DATE",sdate);
						//detail2res.put("START_TIME",stime);
						detail2res.put("END_DATE",edate);
						//detail2res.put("END_TIME",etime);
						WSAssert.assertEquals(detail2res, detail2, false);


					}
					//Checking for result  text
					if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){

						String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "****************Blocked : Subscription not created**************");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		}
	}

	@Test(groups = { "minimumRegression", "Create Activity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20397() {
		try {
			String testName = "createActivity_2006_20397";
			WSClient.startTest(testName, "Verify error message should be displayed on the response when Cancelled reservationId is passed on the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ResvCancelReason" })) {

				String uname=OPERALib.getUserName();
				String pwd=OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				//************Prerequisite 1 - Opera Create Profile******************************************//

				OPERALib.setOperaHeader(uname);
				if(profileID.equals(""))
					profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileID}", profileID);

					WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

					//************Prerequisite 2 - HTNG Subscription*****************************************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
					String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

					if (WSAssert.assertIfElementValueEquals(subscriptionRes,
							"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

						//******************** Prerequisite 3 - Create Reservation****************************//

						OPERALib.setOperaHeader(uname);
						HashMap<String,String> resv=CreateReservation.createReservation("DS_05");
						String resvID = resv.get("reservationId");

						if(!resvID.equals("error"))
						{

							WSClient.setData("{var_resvId}", resvID);


							//******************** Prerequisite 3 - Cancel Reservation****************************//

							WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));

							if(CancelReservation.cancelReservation("DS_02")){

								WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));

								WSClient.setData("{var_extResort}", resortExtValue);
								WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
								WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
								WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

								// **********************HTNG Creating activity*****************************//

								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_02");
								String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

								if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
										"CreateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {
									if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){

										String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
									}
								}

							}

							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Unable to cancel reservation***************");
							}
						}

					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "***************Blocked : Subscription not created**************");
					}
				}

			}
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{
		}
	}
	@Test(groups = { "minimumRegression", "Create Activity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20391() {
		try {
			String testName = "createActivity_2006_20391";
			WSClient.startTest(testName, "Verify an error should be thrown by the response when activity time span falls out of reservation timespan", "minimumRegression");

			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			//************Prerequisite 1 - Opera Create Profile******************************************//

			OPERALib.setOperaHeader(uname);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileID}", profileID);

				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

				//************Prerequisite 2 - HTNG Subscription*****************************************//
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					//******************** Prerequisite 3 - Create Reservation****************************//

					OPERALib.setOperaHeader(uname);
					HashMap<String,String> resv=CreateReservation.createReservation("DS_05");
					String resvID = resv.get("reservationId");

					if(!resvID.equals("error"))
					{

						WSClient.setData("{var_resvId}", resvID);

						WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));

						WSClient.setData("{var_extResort}", resortExtValue);
						WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
						WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
						WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

						// **********************HTNG Creating activity*****************************//

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_05");
						String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

						if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
								"CreateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {
							if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){

								String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
							}
						}

					}


				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "***************Blocked : Subscription not created**************");
				}
			}

		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{
			// Cancel Reservation
			try
			{
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "CreateActivity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20396() {
		try {
			String testName = "createActivity_2006_20396";
			WSClient.startTest(testName, "Verify that multiple activities are being created for the requested Profile", "targetedRegression");
			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			//************Prerequisite 1 - Opera Create Profile******************************************//
			OPERALib.setOperaHeader(uname);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

				//************Prerequisite 2 - HTNG Subscription******************************************//
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					String extID=WSClient.getKeywordData("{KEYWORD_ID}");
					WSClient.setData("{var_extID}",extID );
					WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

					String extID1=WSClient.getKeywordData("{KEYWORD_ID}");
					WSClient.setData("{var_extID1}",extID1);
					WSClient.setData("{var_actstatus1}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype1}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation1}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

					//************HTNG Create Activity*************************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_06");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", false)) {
						List<LinkedHashMap<String,String>> db = new ArrayList<LinkedHashMap<String,String>>();

						db=WSClient.getDBRows(WSClient.getQuery("QS_09"));
						HashMap<String,String> xpath=new HashMap<String,String>();
						xpath.put("Results_IDs_UniqueID", "CreateActivityResponse_Results");
						List<LinkedHashMap<String,String>> res=WSClient.getMultipleNodeList(createActivityResponseXML, xpath, false, XMLType.RESPONSE);
						WSAssert.assertEquals(res, db, false);

					}


					//Checking for result  text
					if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){
						String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}


	@Test(groups = { "fullRegression", "CreateActivity", "HTNG2006", "HTNG","createActivity_2006_20390"})

	public void createActivity_2006_20390() {
		try {
			String testName = "createActivity_2006_20390";
			WSClient.startTest(testName, "Verify error message should be displayed on the response when Invalid ResortId is passed on the request", "fullRegression");
			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			//************Prerequisite 1 - Opera Create Profile******************************************//
			OPERALib.setOperaHeader(uname);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

				//************Prerequisite 2 - HTNG Subscription******************************************//
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.setData("{var_extResort}", "INVALID"+resortOperaValue);
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

					//************HTNG Create Activity*************************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {

					}

					//Checking for result  text
					if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){
						String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "fullRegression", "CreateActivity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20389() {
		try {
			String testName = "createActivity_2006_20389";
			WSClient.startTest(testName, "Verify error message should be displayed on the response when no ResortId is passed on the request", "fullRegression");
			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			//************Prerequisite 1 - Opera Create Profile******************************************//
			OPERALib.setOperaHeader(uname);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileID}", profileID);
				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

				//************Prerequisite 2 - HTNG Subscription******************************************//
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.setData("{var_extResort}","");
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

					//************HTNG Create Activity*************************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {



					}

					//Checking for result  text
					if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){
						String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	//
	//@Test(groups = { "minimumRegression", "CreateActivity", "HTNG2006", "HTNG" })
	//
	//public void createActivity_2006_20401() {
	//	try {
	//		String testName = "createActivity_2006_20401";
	//		WSClient.startTest(testName, "Verify error message should be displayed on the response when incorrect conversion code passed for Activity Status ", "minimumRegression");
	//		String uname=OPERALib.getUserName();
	//		String pwd=OPERALib.getPassword();
	//		String fromAddress=HTNGLib.getInterfaceFromAddress();
	//		String interfaceName = HTNGLib.getHTNGInterface();
	//		String resortOperaValue = OPERALib.getResort();
	//		String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
	//		WSClient.setData("{var_profileSource}", interfaceName);
	//		WSClient.setData("{var_resort}", resortOperaValue);
	//		WSClient.setData("{var_extResort}", resortExtValue);
	//
	//        //************Prerequisite 1 - Opera Create Profile******************************************//
	//		OPERALib.setOperaHeader(uname);
	//		if(profileID.equals(""))
	//			profileID=CreateProfile.createProfile("DS_01");
	//		if(!profileID.equals("error"))
	//		{
	//			    WSClient.setData("{var_profileId}", profileID);
	//			    WSClient.setData("{var_profileID}", profileID);
	//				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
	//
	//				 //************Prerequisite 2 - HTNG Subscription******************************************//
	//				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
	//				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
	//				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
	//
	//				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
	//						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
	//
	//					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
	//					WSClient.setData("{var_actstatus}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName, "ACTIVITY_STATUS"));
	//					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
	//					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));
	//
	//					 //************HTNG Create Activity*************************//
	//					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
	//					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
	//					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);
	//
	//					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
	//							"CreateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {
	//
	//
	//
	//					}
	//
	//					//Checking for result  text
	//                    if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){
	//						String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
	//						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
	//						}
	//				} else {
	//					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
	//				}
	//			}
	//
	//	} catch (Exception e) {
	//		WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
	//	}
	//}

	@Test(groups = { "fullRegression", "CreateActivity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20392() {
		try {
			String testName = "createActivity_2006_20392";
			WSClient.startTest(testName, "Verify an error message should be thrown when an invalid profileID is passed in the request ", "fullRegression");
			String uname=OPERALib.getUserName();
			String pwd=OPERALib.getPassword();
			String fromAddress=HTNGLib.getInterfaceFromAddress();
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			//************Prerequisite 1 - Opera Create Profile******************************************//
			OPERALib.setOperaHeader(uname);
			if(profileID.equals(""))
				profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>ProfileID: "+profileID+"</b>");
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileID}",profileID);

				WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));

				//************Prerequisite 2 - HTNG Subscription******************************************//
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String subscriptionReq = WSClient.createSOAPMessage("HTNG2006Subscription", "DS_05");
				String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

				if (WSAssert.assertIfElementValueEquals(subscriptionRes,
						"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {

					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_actstatus}", HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName, "ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));
					WSClient.setData("{var_profileId}", WSClient.getKeywordData("{KEYWORD_ID}"));
					WSClient.setData("{var_profileID}",WSClient.getData("{var_profileId}"));

					//************HTNG Create Activity*************************//
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_01");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {



					}

					//Checking for result  text
					if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){
						String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked : Subscription not created***************");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "targetedRegression", "Create Activity", "HTNG2006", "HTNG" })

	public void createActivity_2006_20398() {
		try {
			String testName = "createActivity_2006_20398";
			WSClient.startTest(testName, "Verify error message should be displayed on the response when NO SHOW reservationId is passed on the request", "targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ResvCancelReason" })) {

				String uname=OPERALib.getUserName();
				String pwd=OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				/** Fetch a no show reservation***/
				WSClient.writeToReport(LogStatus.INFO,"<b>Fetching NO SHOW reservation</b>");
				WSClient.setData("{var_resvId}",WSClient.getDBRow(WSClient.getQuery("HTNG2006CreateActivity","QS_10")).get("RESV_NAME_ID"));

				if(WSClient.getData("{var_resvId}") != null){
					WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));

					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_actstatus}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
					WSClient.setData("{var_acttype}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
					WSClient.setData("{var_actlocation}", HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));

					// **********************HTNG Creating activity*****************************//

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String createActivityReq = WSClient.createSOAPMessage("HTNG2006CreateActivity", "DS_02");
					String createActivityResponseXML = WSClient.processSOAPMessage(createActivityReq);

					if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
							"CreateActivityResponse_Results_resultStatusFlag", "FAIL", false)) {
						if(WSAssert.assertIfElementExists(createActivityResponseXML,"Results_Text_TextElement", true)){

							String message=WSClient.getElementValue(createActivityResponseXML,"Results_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
						}
					}

				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Unable to fetch reservation which is NO SHOW");
				}


			}
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{
		}
	}
}
