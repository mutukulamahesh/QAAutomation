package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.Setup;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.XMLutil;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.testSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class ReInstateReservation extends Setup {
	String profileID1 = "";
	HashMap<String, String> resvID1 = new HashMap<>();
	HashMap<String, String> resvID2 = new HashMap<>();

	@Test(groups = { "sanity", "OWS", "ReInstateReservation_TC001" })
	public void ReInstateReservation_TC001() {
		HashMap<String, String> resDetails;
		List resDifferences;
		ArrayList<String> actualDifferencess;

		try {

			String testName = "ReInstateReservation_TC001";
			WSClient.startTest(testName, "Verify ReInstateReservation RQ & RS parameters",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				WSClient.writeToReport(LogStatus.INFO, "<b>Creating reservations-One For Payee </b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/
					resvID1 = CreateReservation.createReservation("DS_01");
					resvID2 = CreateReservation.createReservation("DS_01");

					if (!resvID1.equals("error") && !resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
						WSClient.setData("{var_cancelReasonCode}",
								OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
						
						
						if (!resvID1.containsValue("error") || !resvID1.isEmpty())
							CancelReservation.cancelReservation("DS_02");
						
							WSClient.setData("{var_resvId}", resvID2.get("reservationId"));
							WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
							CancelReservation.cancelReservation("DS_02");

						/*******************
						 * Create a Reservation-ReInstateReservation
						 ************************/
						System.out.println("ReInstateReservation");
						
						HashMap<String, String> reqDetails = testSetUp.getServiceSetUpData("Reservation_ReInstateReservation");
						String v5Req = testSetUp.soapV5ReqAsString();
						String cloudReq = testSetUp.soapCloudReqAsString();
						
						//Replacing all the variables with respective values.
						v5Req = v5Req.replaceAll("Var_ConfirmationNumber", resvID1.get("confirmationId"));
						cloudReq = cloudReq.replaceAll("Var_ConfirmationNumber", resvID2.get("confirmationId"));
						
						System.out.println("ReInstateReservation - v5Req:"+ v5Req);
						System.out.println("ReInstateReservation - v5Req:"+ cloudReq);
						
						resDetails = SOAPClient.processSOAPMessage(v5Req, cloudReq, reqDetails);

						testSetUp.createResponseFile(resDetails.get("resV5"), testSetUp.v5ResponseFileName);
						testSetUp.createResponseFile(resDetails.get("resCloud"), testSetUp.cloudResponseFileName);

						resDifferences = XMLutil.compareXML();
						actualDifferencess = XMLutil.differencesByExcludingExceptions(resDifferences);
						if (actualDifferencess.size() == 0) {
							WSClient.writeToReport(LogStatus.PASS,
									"<b>Validating Reservation_ReInstateReservation</b>");
							Assert.assertTrue(true, actualDifferencess.toString());

						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b>Validating Reservation_ReInstateReservation</b>");
							Assert.fail(actualDifferencess.toString());
						}
					}

				}

			}

		} catch (

		Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}

	}
}
