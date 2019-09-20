package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchPromotionCode extends WSSetUp {
//	@Test(groups = { "minimumRegression", "FetchPromotionCode", "ResvAdvanced", "OWS" })
//	/** 
//	 * Verify that promotion code are being fetched for a resort with a channel when channel name and carrier name are different.
//	 */
//	public void fetchPromotionCode_60005() {
//		try {
//
//			String testName = "fetchPromotionCode_60005";
//			WSClient.startTest(testName, "Verify that promotion code are being fetched for a resort with a channel when channel name and carrier name are different.",
//					"minimumRegression");
//
//			String resortOperaValue = OPERALib.getResort();
//			String channel = OWSLib.getChannel(3);
//			System.out.println("channel: "+channel);
//
//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
//			// ********* Setting the OWS Header**************//
//			OPERALib.setOperaHeader(OPERALib.getUserName());
//			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
//					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));
//
//			WSClient.setData("{var_owsresort}", resort);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			// Invoking targeted service
//			String fetchPromotionCodeReq = WSClient.createSOAPMessage("OWSFetchPromotionCode", "DS_01");
//			String fetchPromotionCodeRes = WSClient.processSOAPMessage(fetchPromotionCodeReq);
//			if (WSAssert.assertIfElementValueEquals(fetchPromotionCodeRes,
//					"FetchResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//				
//				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
//				db = WSClient.getDBRows(WSClient.getQuery("QS_01"));
//				List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
//				LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
//				xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionCode",
//						"FetchResPromotionCodeResponse_PromotionCode"); 
//				xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionName",
//						"FetchResPromotionCodeResponse_PromotionCode");
//				res = WSClient.getMultipleNodeList(fetchPromotionCodeRes, xPath, false, XMLType.RESPONSE);
//				WSAssert.assertEquals(res, db, false);
//			}
//			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "Result_Text_TextElement", true)) {
//
//				/****
//				 * Verifying that the error message is populated on the response
//				 ********/
//
//				String message = WSAssert.getElementValue(fetchPromotionCodeRes, "Result_Text_TextElement",
//						XMLType.RESPONSE);
//				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//			}
//
//			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes,
//					"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {
//
//				/****
//				 * Verifying whether the error Message is populated on the response
//				 ****/
//
//				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
//						"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
//			}
//			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "FetchResPromotionCodeResponse_Result_GDSError",
//					true)) {
//
//				/****
//				 * Verifying whether the error Message is populated on the response
//				 ****/
//
//				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
//						"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
//				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
//			}
//
//		} catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		}
//	}
//    
	
//	@Test(groups = { "sanity", "FetchPromotionCode", "ResvAdvanced", "OWS" })
//	/** 
//	 * Verify that error message is being populated when the used channel is not configured
//	 */
//	public void fetchPromotionCode_4th() {
//		try {
//
//			String testName = "fetchPromotionCode_4th";
//			WSClient.startTest(testName, "Verify that error message is being populated when the used channel is not configured",
//					"sanity");
//
//			String resortOperaValue = OPERALib.getResort();
//			String channel = OWSLib.getChannel(4);
//			System.out.println("channel: "+channel);
//			System.out.println(OWSLib.getChannelCarier(resortOperaValue, channel));
//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
//			// ********* Setting the OWS Header**************//
//			OPERALib.setOperaHeader(OPERALib.getUserName());
//			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
//					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));
//
//			WSClient.setData("{var_owsresort}", resort);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			// Invoking targeted service
//			String fetchPromotionCodeReq = WSClient.createSOAPMessage("OWSFetchPromotionCode", "DS_01");
//			String fetchPromotionCodeRes = WSClient.processSOAPMessage(fetchPromotionCodeReq);
//			if (WSAssert.assertIfElementValueEquals(fetchPromotionCodeRes,
//					"FetchResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
//				
//			}
//			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "Result_Text_TextElement", true)) {
//
//				/****
//				 * Verifying that the error message is populated on the response
//				 ********/
//
//				String message = WSAssert.getElementValue(fetchPromotionCodeRes, "Result_Text_TextElement",
//						XMLType.RESPONSE);
//				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//			}
//
//			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes,
//					"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {
//
//				/****
//				 * Verifying whether the error Message is populated on the response
//				 ****/
//
//				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
//						"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
//			}
//			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "FetchResPromotionCodeResponse_Result_GDSError",
//					true)) {
//
//				/****
//				 * Verifying whether the error Message is populated on the response
//				 ****/
//
//				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
//						"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
//				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
//			}
//
//		} catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		}
//	}
	
	
	@Test(groups = { "sanity", "FetchPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if promotion code is fetched from the database when resort is provided
	 * in request.
	 *****/
	/*****
	 * * * Prerequisites Required: -->There should be a promotion code available
	 *****/
	public void fetchPromotionCode_38422() {
		try {

			String testName = "fetchPromotionCode_38422";
			WSClient.startTest(testName, "Verify that all the promotion codes are getting fetched for a resort",
					"sanity");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			System.out.println("channel: "+channel);

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_resort}", resortOperaValue);
			// Invoking targeted service
			String fetchPromotionCodeReq = WSClient.createSOAPMessage("OWSFetchPromotionCode", "DS_01");
			String fetchPromotionCodeRes = WSClient.processSOAPMessage(fetchPromotionCodeReq);
			if (WSAssert.assertIfElementValueEquals(fetchPromotionCodeRes,
					"FetchResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				db = WSClient.getDBRows(WSClient.getQuery("QS_01"));
				List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
				LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
				xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionCode",
						"FetchResPromotionCodeResponse_PromotionCode"); 
				xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionName",
						"FetchResPromotionCodeResponse_PromotionCode");
				res = WSClient.getMultipleNodeList(fetchPromotionCodeRes, xPath, false, XMLType.RESPONSE);
				WSAssert.assertEquals(res, db, false);
			}
			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(fetchPromotionCodeRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes,
					"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
						"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "FetchResPromotionCodeResponse_Result_GDSError",
					true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
						"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if proper error msg is received when invalid resort is provided in
	 * request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a promotion code available
	 *****/
	public void fetchPromotionCode_38454() {
		try {

			String testName = "fetchPromotionCode_38454";
			WSClient.startTest(testName,
					"Verify that proper error msg is received when invalid resort is provided in request",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsresort}", resort);

			// Invoking targeted service
			String fetchPromotionCodeReq = WSClient.createSOAPMessage("OWSFetchPromotionCode", "DS_02");
			String fetchPromotionCodeRes = WSClient.processSOAPMessage(fetchPromotionCodeReq);
			if (WSAssert.assertIfElementValueEquals(fetchPromotionCodeRes,
					"FetchResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
				// WSAssert.assertIfElementValueEquals(fetchPromotionCodeRes,
				// "FetchResPromotionCodeResponse_Result_OperaErrorCode", "INVALID_PROPERTY",
				// false);

			} else {
				if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the response
					 ********/

					String message = WSAssert.getElementValue(fetchPromotionCodeRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				if (WSAssert.assertIfElementExists(fetchPromotionCodeRes,
						"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(fetchPromotionCodeRes,
							"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(fetchPromotionCodeRes,
						"FetchResPromotionCodeResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(fetchPromotionCodeRes,
							"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if proper error msg is received when no resort is provided in request.
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a promotion code available
	 *****/
	public void fetchPromotionCode_38456() {
		try {

			String testName = "fetchPromotionCode_38456";
			WSClient.startTest(testName,
					"Verify that proper error msg is received when no resort is provided in request",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsresort}", resort);
			
			// Invoking targeted service
			String fetchPromotionCodeReq = WSClient.createSOAPMessage("OWSFetchPromotionCode", "DS_03");
			String fetchPromotionCodeRes = WSClient.processSOAPMessage(fetchPromotionCodeReq);
			if (WSAssert.assertIfElementValueEquals(fetchPromotionCodeRes,
					"FetchResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
				// WSAssert.assertIfElementValueEquals(fetchPromotionCodeRes,
				// "Result_Text_TextElement",
				// "Opera Resort is Missing in Request Message", false);

			}

			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes,
					"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
						"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(fetchPromotionCodeRes, "FetchResPromotionCodeResponse_Result_GDSError",
					true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(fetchPromotionCodeRes,
						"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
}
