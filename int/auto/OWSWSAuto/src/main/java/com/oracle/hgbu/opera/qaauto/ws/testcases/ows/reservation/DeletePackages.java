package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
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

public class DeletePackages extends WSSetUp {

	@Test(groups = { "sanity", "Deletepackages", "Reservation", "OWS" })
	/*Verify that the given package is deleted from the reservation 
	  Prerequisites : ->Profile is created ->Reservation is created ->Packages are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 */
	public void deletePackages_13425() {

		String resvId = "";
		try {

			String testCase = "deletePackages_13425";
			WSClient.startTest(testCase, "Verify that package is deleted for the Reservation.", "sanity");

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
					String confrId = resvIDs.get("confirmationId");

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
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true) && count1.trim().contains("2")) {
							/*** OWS Delete Packages ****/
							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
							String deletePackageReq = WSClient.createSOAPMessage("OWSDeletePackages", "DS_01");
							String deletePackageRes = WSClient.processSOAPMessage(deletePackageReq);

							if (WSAssert.assertIfElementValueEquals(deletePackageRes, "DeletePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								/***** Validation ******/

								String query = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> count = WSClient.getDBRow(query);
								System.out.println(count);
								if (count.get("COUNT(*)").trim().contains("1")) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Total Packages attached: " + count.get("COUNT(*)"));
									WSClient.writeToReport(LogStatus.PASS, "<b> The package has been deleted from DB");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> The package has NOT been deleted from DB");
								}
							}

							//error Codes
							if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", true)) {
								String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
							}
							if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", true)) {
								String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
							}
							if (WSAssert.assertIfElementExists(deletePackageRes, "Result_Text_TextElement", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deletePackageRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Change Reservation");
						}

					} else WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Create Reservation");
				} else WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Create Profile");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvId.equals("error"))
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

	@Test(groups = { "minimumRegression", "Deletepackages", "Reservation", "OWS" })
	/*	Verify that the Error message is populated in the Response
	 *  when trying to delete a package which is not attached to the reservation
	 *  
	 * Prerequisites : ->Profile is created ->Reservation is created ->Packages are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 */
	public void deletePackages_2() {

		String resvId = "";
		try {

			String testCase = "deletePackages_2356";
			WSClient.startTest(testCase, "Verify that no package is deleted when trying to delete a package which is not attached to reservation is deleted.", "minimumRegression");

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
					WSClient.setData("{var_resvId}", resvId);
					String confrId = resvIDs.get("confirmationId");
					String query = WSClient.getQuery("QS_03");
					String count1 = WSClient.getDBRow(query).get("COUNT(*)");
					System.out.println(count1);

					if (resvId != "error" && count1.trim().contains("1")) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation  ID :" + resvId + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID :" + confrId + "</b>");

						WSClient.setData("{var_confirmationId}", confrId);

						/*** OWS Delete Packages ****/
						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						String deletePackageReq = WSClient.createSOAPMessage("OWSDeletePackages", "DS_01");
						String deletePackageRes = WSClient.processSOAPMessage(deletePackageReq);
						if (WSAssert.assertIfElementValueEquals(deletePackageRes, "DeletePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String newCount = WSClient.getDBRow(query).get("COUNT(*)");
							if (newCount.trim().contains("1")) {
								WSClient.writeToReport(LogStatus.PASS, "No record is Deleted");
							} else if (newCount.trim().contains("0")) {
								WSClient.writeToReport(LogStatus.FAIL, "Primary record has been Deleted");
							}
						}

						//						error codes
						if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", true)) {
							String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", true)) {
							String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(deletePackageRes, "Result_Text_TextElement", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deletePackageRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
						}

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			//Clear the reservation.
			try {
				if (!resvId.contentEquals("error")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Failed to Cancel Reservation");
			}
		}
	}

	@Test(groups = { "minimumRegression", "Deletepackages", "Reservation", "OWS" })
	/*	Verify that the Error message is populated in the Response
	 *  when invalid reservation ID is passed in the request. 
	 *  
	 * Prerequisites : ->Profile is created ->Reservation is created ->Packages are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 */
	public void deletePackages_3() {

		String resvId = "";
		try {

			String testCase = "deletePackages_39878";
			WSClient.startTest(testCase, "Verify Error message when invalid reservationID passed.", "minimumRegression");
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

					resvId = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
					WSClient.setData("{var_confirmationId}", resvId);
					WSClient.setData("{var_packageCode}", OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

					if (resvId != null) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation  ID :" + resvId + "</b>");

						/*** OWS Delete Packages ****/
						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						String deletePackageReq = WSClient.createSOAPMessage("OWSDeletePackages", "DS_01");
						String deletePackageRes = WSClient.processSOAPMessage(deletePackageReq);
						if (WSAssert.assertIfElementValueEquals(deletePackageRes, "DeletePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

							
						}
						//						Error Codes
						if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", true)) {
							String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", true)) {
							String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(deletePackageRes, "Result_Text_TextElement", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deletePackageRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			//Clear the reservation.
		}
	}

	@Test(groups = { "minimumRegression", "Deletepackages", "Reservation", "OWS" })
	/*Verify Error message is populated in the response when Package code is missed in the request.
	  Prerequisites : ->Profile is created ->Reservation is created ->Packages are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 */
	public void deletePackages_4() {

		String resvId = "";
		try {

			String testCase = "deletePackages_48975";
			WSClient.startTest(testCase, "Verify ERROR message when no package is sent in the request.", "minimumRegression");

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
					String confrId = resvIDs.get("confirmationId");

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
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true) && count1.trim().contains("2")) {
							/*** OWS Delete Packages ****/
							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
							WSClient.setData("{var_packageCode}", "");
							String deletePackageReq = WSClient.createSOAPMessage("OWSDeletePackages", "DS_01");
							String deletePackageRes = WSClient.processSOAPMessage(deletePackageReq);

							if (WSAssert.assertIfElementValueEquals(deletePackageRes, "DeletePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {
								/***** Validation ******/
								
							}
							//Error Codes 
							if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", true)) {
								String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_OperaError_errorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
							}
							if (WSAssert.assertIfElementExists(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", true)) {
								String message = WSAssert.getElementValue(deletePackageRes, "DeletePackagesResponse_Result_GDSError_errorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
							}
							if (WSAssert.assertIfElementExists(deletePackageRes, "Result_Text_TextElement", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deletePackageRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Change Reservation");
						}

					} else WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Create Reservation");
				} else WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Create Profile");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvId.equals("error"))
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}
}
