package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.Setup;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.XMLutil;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.testSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchBookedPackages extends Setup {
	@Test(groups = { "sanity", "OWS", "FetchBookedPackages_TC001" })
	public void FetchBookedPackages_TC001() throws Exception {
		String resvId;
		String confrId;
		HashMap<String, String> resDetails;
		List resDifferences;
		ArrayList<String> actualDifferencess;
		
		String testName = "FetchBookedPackages_TC001";
		WSClient.startTest(testName, "Verify FetchBookedPackages RQ & RS parameters",
				"minimumRegression");
		
		String resortOperaValue = OPERALib.getResort();
		String chain = OPERALib.getChain();
		WSClient.setData("{var_chain}", chain);
		WSClient.setData("{var_resort}", resortOperaValue);
		String pwd = OPERALib.getPassword();
		String channel = OWSLib.getChannel();
		String channelType = OWSLib.getChannelType(channel);
		String resort = OWSLib.getChannelResort(resortOperaValue, channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);

		String[] preReq = { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode", "PackageGroup" };
		if (OperaPropConfig.getPropertyConfigResults(preReq)) {

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			/** Prerequisite 1: Create Profile *****/

			String profileId = CreateProfile.createProfile("DS_01");

			if (!profileId.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileId + "</b>");

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{var_profileId}", profileId);
				WSClient.setData("{var_bussinessDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
				WSClient.setData("{var_price}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_qunatity}", WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
				WSClient.setData("{var_packageCode}", OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

				/** Prerequisite 2: Create Reservation *****/

				HashMap<String, String> resvIDs = CreateReservation.createReservation("DS_01");
				resvId = resvIDs.get("reservationId");
				confrId = resvIDs.get("confirmationId");

				if (resvId != "error") {

					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation  ID :" + resvId + "</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID :" + confrId + "</b>");
					WSClient.setData("{var_resvId}", resvId);
					WSClient.setData("{var_confirmationId}", confrId);

					/** Prerequisite 3: Change Reservation *****/

					String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_02");
					String changeResvRes = WSClient.processSOAPMessage(changeResvReq);
					String query1 = WSClient.getQuery("QS_01");
					String count1 = WSClient.getDBRow(query1).get("COUNT(*)");
					WSClient.writeToReport(LogStatus.INFO, "<b>Total Packages attached: " + count1);
					if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true)
							&& count1.trim().contains("2")) {
						
						
						
						/*******************
						 * Create a FetchBookedPackages
						 ************************/
						System.out.println("FetchBookedPackages");
						
						HashMap<String, String> reqDetails = testSetUp.getServiceSetUpData("Reservation_FetchBookedPackages");
						String v5Req = testSetUp.soapV5ReqAsString();
						String cloudReq = testSetUp.soapCloudReqAsString();
						
						//Replacing all the variables with respective values.
						v5Req = v5Req.replaceAll("Var_ConfirmationNumber", confrId);
						cloudReq = cloudReq.replaceAll("Var_ConfirmationNumber", confrId);
						
						
						resDetails = SOAPClient.processSOAPMessage(v5Req, cloudReq, reqDetails);

						testSetUp.createResponseFile(resDetails.get("resV5"), testSetUp.v5ResponseFileName);
						testSetUp.createResponseFile(resDetails.get("resCloud"), testSetUp.cloudResponseFileName);

						resDifferences = XMLutil.compareXML();
						actualDifferencess = XMLutil.differencesByExcludingExceptions(resDifferences);
						if (actualDifferencess.size() == 0) {
							WSClient.writeToReport(LogStatus.PASS,
									"<b>Validating Reservation_FetchBookedPackages RS</b>");
							Assert.assertTrue(true, actualDifferencess.toString());

						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b>Validating Reservation_FetchBookedPackages RS</b>");
							Assert.fail(actualDifferencess.toString());
						}

					}
				}
			}
		}
	}
}
