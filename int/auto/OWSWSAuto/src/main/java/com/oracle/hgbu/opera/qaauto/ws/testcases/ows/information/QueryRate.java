package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.information;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class QueryRate extends WSSetUp {
	@Test(groups = { "sanity", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify that correct rate category,rate code,start date,end date,rate
	 * description are fetched from the database when minimum required data is
	 * provided in QueryRate request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a rate code assigned to a
	 * channel.
	 *
	 *****/

	public void queryRate_38604() {
		try {

			String testName = "queryRate_38604";
			WSClient.startTest(testName, "Verify that  correct rate category,rate code,start date,end date are fetched from the database when minimum required data is provided in QueryRate request.", "sanity");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			WSClient.setData("{var_resort}", resort);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {

				/*************** OWS Query Rate *****************/

				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_01");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", false)) {

					if (WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						WSClient.setData("{var_channel}", channel);
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						String query = WSClient.getQuery("QS_01");
						db = WSClient.getDBRow(query);

						String rateCode = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_ratePlanCode", XMLType.RESPONSE);
						String rateCategory = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_ratePlanCategory", XMLType.RESPONSE);
						String startDate = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_effectiveDate", XMLType.RESPONSE);
						String endDate = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_expirationDate", XMLType.RESPONSE);
						String text = WSClient.getElementValue(QueryRateRes, "RateResult_RatePlanDescription_Text", XMLType.RESPONSE);

						/***
						 * Validating the values sent in the request against the
						 * database
						 *****/

						if (WSAssert.assertEquals(db.get("GDS_RATE_CODE"), rateCode, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Rate Code -> Expected     :  " + db.get("GDS_RATE_CODE") + " ,  Actual  	: " + rateCode);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Rate Code -> Expected     :  " + db.get("GDS_RATE_CODE") + " ,  Actual 	:" + rateCode);

						}
						if (WSAssert.assertEquals(db.get("RATE_CATEGORY"), rateCategory, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Rate Category -> Expected    " + db.get("RATE_CATEGORY") + " ,      Actual   : " + rateCategory);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Rate Category -> Expected    " + db.get("RATE_CATEGORY") + " ,      Actual    " + rateCategory);

						}
						if (WSAssert.assertEquals(db.get("BEGIN_BOOKING_DATE"), startDate, true)) {
							WSClient.writeToReport(LogStatus.PASS, " Start Date -> Expected     :  " + db.get("BEGIN_BOOKING_DATE") + " ,     Actual     : " + startDate);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, " Start Date -> Expected     :  " + db.get("BEGIN_BOOKING_DATE") + " ,     Actual     :  " + startDate);

						}
						if (WSAssert.assertEquals(db.get("END_BOOKING_DATE"), endDate, true)) {
							WSClient.writeToReport(LogStatus.PASS, "End date -> Expected        " + db.get("END_BOOKING_DATE") + " ,      Actual      :  " + endDate);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "End date -> Expected        " + db.get("END_BOOKING_DATE") + " ,      Actual      :  " + endDate);

						}

					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != " *null*")
						WSClient.writeToReport(LogStatus.INFO, "The error text populated on the response is : <b>" + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "*** Blocked : Rate Code doesnot Exist! ****");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify error message is populated when rate code not assigned to
	 * configured channel is sent on the response.
	 *
	 *****/

	public void queryRate_38706() {
		try {

			String testName = "queryRate_38706";
			WSClient.startTest(testName, "Verify error message is populated when rate code not assigned to configured channel is sent on the response.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {

				/*************** OWS Query Rate *****************/

				WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_02");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", false)) {

					if (!WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "FAIL", false)) {

					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != " *null*")
						WSClient.writeToReport(LogStatus.INFO, "The error text populated on the response is : <b>" + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, " *** Blocked : Rate Code Doesnot Exists ***** ");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify that the rate details are not fetched on the response when the
	 * start date sent on the request is less than the effective date of rate
	 * code.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a rate code assigned to a
	 * channel.
	 *
	 *****/

	public void queryRate_38707() {
		try {

			String testName = "queryRate_38707";
			WSClient.startTest(testName, "Verify that the rate details are not fetched on the response when the start date sent on the request is less than the effective date of rate code.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {
				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
				WSClient.setData("{var_resort}", resort);

				WSClient.setData("{var_channel}", channel);
				LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
				String query = WSClient.getQuery("OWSQueryRate", "QS_03");
				db = WSClient.getDBRow(query);
				String startDate = db.get("BEGIN_BOOKING_DATE");
				WSClient.setData("{var_startDate}", startDate);
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				/*************** OWS Query Rate *****************/

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_04");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", false)) {

					if (WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "FAIL", false)) {

					} else {
						WSClient.writeToReport(LogStatus.FAIL, "The query rate operation is successful ! ERROR");
					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != " *null*")
						WSClient.writeToReport(LogStatus.INFO, "The error text populated on the response is : <b>" + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "***** Blocked : Rate Code doesnot exist! ********");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify error message is populated when hotel code is not sent on the
	 * response.
	 *
	 *****/

	public void queryRate_38708() {
		try {

			String testName = "queryRate_38708";
			WSClient.startTest(testName, "Verify error message is populated when hotel code is not sent on the request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {

				/*************** OWS Query Rate *****************/

				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_03");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", true)) {

					if (!WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "FAIL", true)) {

					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != " *null*")
						WSClient.writeToReport(LogStatus.INFO, "The error text populated on the response is : <b>" + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed : Rate Code");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify that correct guarantee text, guarantee type ,requirement code are
	 * fetched from the database when minimum required data in provided in
	 * QueryRate request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a rate code assigned to a
	 * channel.
	 *
	 *****/

	public void queryRate_38709() {
		try {

			String testName = "queryRate_38709";
			WSClient.startTest(testName, "  Verify that correct guarantee text, guarantee type ,requirement code are fetched from the database when minimum required data in provided in QueryRate request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			WSClient.setData("{var_resort}", resort);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {

				/*************** OWS Query Rate *****************/

				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_01");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", true)) {

					if (WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						WSClient.setData("{var_channel}", channel);
						List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
						String query = WSClient.getQuery("QS_02");
						db = WSClient.getDBRows(query);

						LinkedHashMap<String, String> guarantee = new LinkedHashMap<String, String>();

						guarantee.put("Guarantee_GuaranteeDescription_Text", "RateResult_GuaranteeDetails_Guarantee");
						guarantee.put("RateResult_GuaranteeDetails_Guarantee_guaranteeType", "RateResult_GuaranteeDetails_Guarantee");
						guarantee.put("RateResult_GuaranteeDetails_Guarantee_requirementCode", "RateResult_GuaranteeDetails_Guarantee");

						List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();

						list = WSClient.getMultipleNodeList(QueryRateRes, guarantee, false, XMLType.RESPONSE);

						/***
						 * Validating the values sent in the request against the
						 * database
						 *****/
						System.out.println(list);
						System.out.println(db);
						WSAssert.assertEquals( list,db, false);

					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != " *null*")
						WSClient.writeToReport(LogStatus.INFO, "The error text populated on the response is : <b>" + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "**** Blocked : Rate Code Doesnot Exist");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify that correct rate category,rate code,start date,end date,rate
	 * description promotion code,commission y/n are fetched from the database
	 * when all required data in provided in QueryRate request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a rate code assigned to a
	 * channel.
	 *
	 *****/

	public void queryRate_38711() {
		try {

			String testName = "queryRate_38711";
			WSClient.startTest(testName, "Verify that  correct rate category,rate code,start date,end date,rate description,promotion code,commission y/n  are fetched from the database when all required data in provided in QueryRate request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {
				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_channel}", channel);

				String queryDate = WSClient.getQuery("OWSQueryRate", "QS_05");
				LinkedHashMap<String, String> dbDate = WSClient.getDBRow(queryDate);

				String start = dbDate.get("BEGIN");
				String end = dbDate.get("END");
				WSClient.setData("{var_startDate}", start);
				WSClient.setData("{var_endDate}", end);
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				/*************** OWS Query Rate *****************/

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_05");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", true)) {

					if (WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						String query = WSClient.getQuery("QS_01");
						db = WSClient.getDBRow(query);

						String rateCode = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_ratePlanCode", XMLType.RESPONSE);
						String rateCategory = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_ratePlanCategory", XMLType.RESPONSE);
						String startDate = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_effectiveDate", XMLType.RESPONSE);
						String endDate = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_expirationDate", XMLType.RESPONSE);
						String text = WSClient.getElementValue(QueryRateRes, "RateResult_RatePlanDescription_Text", XMLType.RESPONSE);
						String promoCode = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_promotionCode", XMLType.RESPONSE);
						String commission = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_commissionYn", XMLType.RESPONSE);

						/***
						 * Validating the values sent in the request against the
						 * database
						 *****/

						if (WSAssert.assertEquals(db.get("GDS_RATE_CODE"), rateCode, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Rate Code -> Expected     :  " + db.get("GDS_RATE_CODE") + " ,  Actual  	: " + rateCode);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Rate Code -> Expected     :  " + db.get("GDS_RATE_CODE") + " ,  Actual 	:" + rateCode);

						}
						if (WSAssert.assertEquals(db.get("RATE_CATEGORY"), rateCategory, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Rate Category -> Expected    " + db.get("RATE_CATEGORY") + "  ,     Actual   : " + rateCategory);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Rate Category -> Expected    " + db.get("RATE_CATEGORY") + " ,      Actual   : " + rateCategory);

						}

						if (WSAssert.assertEquals(db.get("PROMO_CODE"), promoCode, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Promo Code -> Expected     :  " + db.get("PROMO_CODE") + " ,  Actual  	: " + promoCode);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Promo Code -> Expected     :  " + db.get("PROMO_CODE") + " ,  Actual 	:" + promoCode);

						}
						if (WSAssert.assertEquals(db.get("COMMISSION_YN"), commission, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Commission Y/N -> Expected    " + db.get("COMMISSION") + "  ,     Actual   : " + commission);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Commission Y/N -> Expected    " + db.get("COMMISSION_YN") + " ,      Actual    " + commission);

						}

						if (WSAssert.assertEquals(db.get("BEGIN_BOOKING_DATE"), startDate, true)) {
							WSClient.writeToReport(LogStatus.PASS, " Start Date -> Expected     :  " + db.get("BEGIN_BOOKING_DATE") + " ,     Actual     : " + startDate);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, " Start Date -> Expected     :  " + db.get("BEGIN_BOOKING_DATE") + " ,     Actual     :  " + startDate);

						}
						if (WSAssert.assertEquals(db.get("END_BOOKING_DATE"), endDate, true)) {
							WSClient.writeToReport(LogStatus.PASS, "End date -> Expected        " + db.get("END_BOOKING_DATE") + "  ,     Actual      :  " + endDate);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "End date -> Expected        " + db.get("END_BOOKING_DATE") + "  ,     Actual      :  " + endDate);

						}
						if (WSAssert.assertEquals(db.get("DESCRIPTION"), text, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Description -> Expected      :   " + db.get("DESCRIPTION") + ",       Actual     :" + text);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Description -> Expected      :   " + db.get("DESCRIPTION") + ",       Actual     :" + text);

						}
					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != " *null*")
						WSClient.writeToReport(LogStatus.INFO, "The error text populated on the response is : <b>" + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "*** Blocked : Rate Code doesnot Exist! ****");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify that correct rate category,rate code,start date,end date,rate
	 * description are fetched from the database when channel rate code with no
	 * channel rate category mapping is provided in QueryRate request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a rate code assigned to a
	 * channel.
	 *
	 *****/

	public void queryRate_42468() {
		try {

			String testName = "queryRate_42468";
			WSClient.startTest(testName, "Verify that  correct rate category,rate code,start date,end date are fetched from the database when channel rate code with no channel rate category mapping is provided in QueryRate request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			WSClient.setData("{var_resort}", resort);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {

				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_10"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				/*************** OWS Query Rate *****************/

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_01");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", false)) {

					if (WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						WSClient.setData("{var_channel}", channel);
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						String query = WSClient.getQuery("QS_06");
						db = WSClient.getDBRow(query);

						String rateCode = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_ratePlanCode", XMLType.RESPONSE);
						String rateCategory = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_ratePlanCategory", XMLType.RESPONSE);
						String startDate = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_effectiveDate", XMLType.RESPONSE);
						String endDate = WSClient.getElementValue(QueryRateRes, "RateResponse_RateResult_expirationDate", XMLType.RESPONSE);

						/***
						 * Validating the values sent in the request against the
						 * database
						 *****/

						if (WSAssert.assertEquals(db.get("GDS_RATE_CODE"), rateCode, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Rate Code -> Expected     :  " + db.get("GDS_RATE_CODE") + " ,  Actual  	: " + rateCode);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Rate Code -> Expected     :  " + db.get("GDS_RATE_CODE") + " ,  Actual 	:" + rateCode);

						}
						if (WSAssert.assertEquals(db.get("RATE_CATEGORY"), rateCategory, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Rate Category -> Expected    " + db.get("RATE_CATEGORY") + " ,      Actual   : " + rateCategory);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Rate Category -> Expected    " + db.get("RATE_CATEGORY") + " ,      Actual    " + rateCategory);

						}
						if (WSAssert.assertEquals(db.get("BEGIN_BOOKING_DATE"), startDate, true)) {
							WSClient.writeToReport(LogStatus.PASS, " Start Date -> Expected     :  " + db.get("BEGIN_BOOKING_DATE") + " ,     Actual     : " + startDate);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, " Start Date -> Expected     :  " + db.get("BEGIN_BOOKING_DATE") + " ,     Actual     :  " + startDate);

						}
						if (WSAssert.assertEquals(db.get("END_BOOKING_DATE"), endDate, true)) {
							WSClient.writeToReport(LogStatus.PASS, "End date -> Expected        " + db.get("END_BOOKING_DATE") + " ,      Actual      :  " + endDate);

						} else {
							WSClient.writeToReport(LogStatus.FAIL, "End date -> Expected        " + db.get("END_BOOKING_DATE") + " ,      Actual      :  " + endDate);

						}

					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != " *null*")
						WSClient.writeToReport(LogStatus.INFO, "The error text populated on the response is : <b>" + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error code populated on the response is :<b> " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is :<b> " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "*** Blocked : Rate Code doesnot Exist! ****");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify that correct package details are fetched from the
	 * database when minimum required data in provided in QueryRate request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a rate code assigned to a
	 * channel.
	 *
	 *****/

	public void queryRate_38710() {
		try {

			String testName = "queryRate_38710";
			WSClient.startTest(testName, " Verify that correct package details are fetched from the database when minimum required data in provided in QueryRate request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			WSClient.setData("{var_resort}", resort);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {

				/*************** OWS Query Rate *****************/

				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_06"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_01");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", true)) {

					if (WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						WSClient.setData("{var_channel}", channel);
						List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
						String query = WSClient.getQuery("QS_04");
						db = WSClient.getDBRows(query);

						LinkedHashMap<String, String> ser = new LinkedHashMap<String, String>();
						ser.put("RateResult_AdditionalDetails_AdditionalDetail_detailType", "RateResult_AdditionalDetails_AdditionalDetail");
						ser.put("RateResult_AdditionalDetails_AdditionalDetail_otherDetailType", "RateResult_AdditionalDetails_AdditionalDetail");
						ser.put("AdditionalDetail_AdditionalDetailDescription_Text", "RateResult_AdditionalDetails_AdditionalDetail");

						List<LinkedHashMap<String, String>> service = new ArrayList<LinkedHashMap<String, String>>();
						service = WSClient.getMultipleNodeList(QueryRateRes, ser, false, XMLType.RESPONSE);
						List<LinkedHashMap<String, String>> servicePackage = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> serviceTax = new ArrayList<LinkedHashMap<String, String>>();

						System.out.println(service);

						for (int index = 0; index < service.size(); index++) {
							LinkedHashMap<String, String> rateInfo = service.get(index);
							if (rateInfo.containsValue("PackageOptions")) {
								rateInfo.remove("detailType1", "PackageOptions");
								servicePackage.add(rateInfo);
							}



						}

						/***
						 * Validating the values sent in the request against the
						 * database
						 *****/

						WSClient.writeToReport(LogStatus.INFO, "<b> Validating Package Details</b>");

						WSAssert.assertEquals(db, servicePackage, false);
					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "*** Blocked : Rate Code doesnot Exist! ****");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}


	@Test(groups = { "minimumRegression", "QueryRate", "OWS", "Information" })

	/*****
	 * Verify that correct tax details are fetched from the
	 * database when minimum required data in provided in QueryRate request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a rate code assigned to a
	 * channel.
	 *
	 *****/

	public void queryRate_42882() {
		try {

			String testName = "queryRate_42882";
			WSClient.startTest(testName, " Verify that tax details are fetched from the database when minimum required data in provided in QueryRate request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			WSClient.setData("{var_resort}", resort);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode" })) {

				/*************** OWS Query Rate *****************/

				WSClient.setData("{var_channelRateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_10"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resort, channel));

				String QueryRateReq = WSClient.createSOAPMessage("OWSQueryRate", "DS_01");
				String QueryRateRes = WSClient.processSOAPMessage(QueryRateReq);

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_resultStatusFlag", true)) {

					if (WSAssert.assertIfElementValueEquals(QueryRateRes, "RateResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						WSClient.setData("{var_channel}", channel);


						String query1 = WSClient.getQuery("QS_07");
						ArrayList<LinkedHashMap<String, String>> db1 = WSClient.getDBRows(query1);

						LinkedHashMap<String, String> ser = new LinkedHashMap<String, String>();
						ser.put("RateResult_AdditionalDetails_AdditionalDetail_detailType", "RateResult_AdditionalDetails_AdditionalDetail");
						ser.put("RateResult_AdditionalDetails_AdditionalDetail_otherDetailType", "RateResult_AdditionalDetails_AdditionalDetail");
						ser.put("AdditionalDetail_AdditionalDetailDescription_Text", "RateResult_AdditionalDetails_AdditionalDetail");

						List<LinkedHashMap<String, String>> service = new ArrayList<LinkedHashMap<String, String>>();
						service = WSClient.getMultipleNodeList(QueryRateRes, ser, false, XMLType.RESPONSE);
						List<LinkedHashMap<String, String>> servicePackage = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> serviceTax = new ArrayList<LinkedHashMap<String, String>>();

						System.out.println(service);

						for (int index = 0; index < service.size(); index++) {
							LinkedHashMap<String, String> rateInfo = service.get(index);


							if (rateInfo.containsValue("TaxInformation")) {

								rateInfo.remove("detailType1", "TaxInformation");
								serviceTax.add(rateInfo);

							}

						}

						/***
						 * Validating the values sent in the request against the
						 * database
						 *****/

						String str = serviceTax.get(0).get("ditionalDetailDescriptionText1").trim();
						System.out.println(str);
						LinkedHashMap<String, String> db2 = new LinkedHashMap<String, String>();
						db2.put("ditionalDetailDescriptionText1", str);
						serviceTax.clear();

						serviceTax.add(db2);

						WSClient.writeToReport(LogStatus.INFO, "<b> Validating Tax Information</b>");

						WSAssert.assertEquals(db1, serviceTax, false);

					}
				} else
					WSClient.writeToReport(LogStatus.FAIL, "The result flag is not populated on the response!");

				if (WSAssert.assertIfElementExists(QueryRateRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(QueryRateRes, "Result_Text_TextElement", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_GDSError", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}

				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(QueryRateRes, "RateResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}
				if (WSAssert.assertIfElementExists(QueryRateRes, "RateResponse_faultcode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSClient.getElementValue(QueryRateRes, "RateResponse_faultstring", XMLType.RESPONSE);
					if (message != "*null*")
						WSClient.writeToReport(LogStatus.INFO, "<b>The error populated on the response is : " + message + " </b>");
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "*** Blocked : Rate Code doesnot Exist! ****");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

}
