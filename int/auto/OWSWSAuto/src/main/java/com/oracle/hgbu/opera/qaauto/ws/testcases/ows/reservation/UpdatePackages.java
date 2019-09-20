package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
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

public class UpdatePackages extends WSSetUp {

	String profileID = "", resvID = "", confirmationId = "";

	@Test(groups = { "sanity", "UpdatePackages", "Reservation", "OWS", "updatePackages_38705" })
	/**
	 * Method to check if the OWS Update Packages is working i.e to add/update
	 * the package to the reservation successfully.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate. ->Market Code to be assigned
	 * ->Source Code to be assigned
	 */

	public void updatePackages_38705() throws Exception {
		try {
			String testName = "updatePackages_38705";
			WSClient.startTest(testName, "Verify that the package is attached to the reservation successfully.",
					"sanity");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_extresort}", resort);

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_01");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						WSClient.setData("{var_hotelCode}", resortOperaValue);
						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String text = WSClient.getElementValue(updateResvRess, "Description_Text_TextElement",
										XMLType.RESPONSE);
								System.out.println(text);

								String startDate = WSClient.getElementValue(updateResvRess,
										"PackageCharge_ValidDates_StartDate_1", XMLType.RESPONSE);
								startDate = startDate.substring(0, startDate.indexOf("T"));
								System.out.println(startDate);
								// String
								// endDate=WSClient.getElementValue(updateResvRess,
								// "PackageCharge_ValidDates_EndDate",
								// XMLType.RESPONSE);
								// endDate=endDate.substring(0,
								// endDate.indexOf("T"));
								// System.out.println(endDate);
								String quantity = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_Quantity_1", XMLType.RESPONSE);
								System.out.println(quantity);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String query = WSClient.getQuery("QS_01");
								System.out.println(query);
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);
								System.out.println(db);
								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}
								if (WSAssert.assertEquals(db.get("DESCRIPTION"), text, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Description -> Expected :		"
											+ db.get("DESCRIPTION") + "		 Actual :	 	 " + text);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Description -> Expected :		"
											+ db.get("DESCRIPTION") + "		 Actual : 		 " + text);

								}
								if (WSAssert.assertEquals(db.get("BEGIN_DATE"), startDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Start Date -> 	Expected :		"
											+ db.get("BEGIN_DATE") + "		 Actual :	 " + startDate);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Start Date -> 	Expected :		"
											+ db.get("BEGIN_DATE") + "		 Actual :	 " + startDate);

								}

								if (WSAssert.assertEquals(quantity, db.get("QUANTITY"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
											+ "		 Actual :	 	 " + db.get("QUANTITY"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
											+ "		 Actual : 		 " + db.get("QUANTITY"));

								}

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"***The update packages operation has failed!***");
						} else
							WSClient.writeToReport(LogStatus.FAIL, "*** The update packages operation has failed! ***");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	public boolean checkOutReservation(String dataset) throws Exception {

		WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
		WSClient.writeToReport(LogStatus.INFO, "<b>" + "Preparing to Checkout the Reservation" + "</b>");

		// Apply Final Postings
		String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", "DS_01");
		String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);

		if (WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applied final postings" + "</b>");

			// Now Post Billing Payment
			String QS01 = WSClient.getQuery("PostBillingPayment", "QS_01");
			LinkedHashMap<String, String> resvMap = WSClient.getDBRow(QS01);
			WSClient.setData("{var_profileId}", resvMap.get("NAME_ID"));
			WSClient.setData("{var_payment}", resvMap.get("PAYMENT_METHOD"));
			WSClient.setData("{var_balance}", resvMap.get("BALANCE"));
			System.out.println("Initial Map: " + resvMap);

			// if Balance is non-zero then only PostBillingPayment
			if (!resvMap.get("BALANCE").equals("0")) {
				String postBillingPaymentReq = WSClient.createSOAPMessage("PostBillingPayment", "DS_01");
				String postBillingPaymentRes = WSClient.processSOAPMessage(postBillingPaymentReq);

				if (WSAssert.assertIfElementExists(postBillingPaymentRes, "PostBillingPaymentRS_Success", true)) {

					// Again fetch balance
					resvMap = WSClient.getDBRow(QS01);
					System.out.println("Final Map: " + resvMap);
					// If Balance is zero then only, Posted bill payment
					// successfully
					if (resvMap.get("BALANCE").equals("0")) {

						WSClient.writeToReport(LogStatus.INFO, "<b>" + "Posted bill payment successfully" + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.INFO, "<b>" + "Posting bill payment is unsuccessful" + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Posting bill payment failed" + "</b>");
				}

			} else {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Balance for Post Billing Payment is zero!!" + "</b>");

				// Folio cannot be generated when Post Billing Payment is
				// already zero, trying to directly checkout
				String checkOutResvReq = WSClient.createSOAPMessage("CheckoutReservation", dataset);
				String checkOutResvRes = WSClient.processSOAPMessage(checkOutResvReq);

				if (WSAssert.assertIfElementExists(checkOutResvRes, "CheckoutReservationRS_Success", true)) {
					return true;
				} else {
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Checkout Failed!!" + "</b>");
					return false;
				}
			}

			// Now Generate Folio
			String generateFolioReq = WSClient.createSOAPMessage("GenerateFolio", "DS_01");
			String generateFolioRes = WSClient.processSOAPMessage(generateFolioReq);

			if (WSAssert.assertIfElementExists(generateFolioRes, "GenerateFolioRS_Success", true)) {
				if (WSAssert.assertIfElementExists(generateFolioRes, "FolioWindow_Folios_Folio_InternalFolioWindowID",
						true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Folio generated successfully" + "</b>");

					// Now Checkout Reservation
					String checkOutResvReq = WSClient.createSOAPMessage("CheckoutReservation", dataset);
					String checkOutResvRes = WSClient.processSOAPMessage(checkOutResvReq);

					if (WSAssert.assertIfElementExists(checkOutResvRes, "CheckoutReservationRS_Success", true)) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Folio generation unsuccessful" + "</b>");
			}

		} else {
			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applying final postings failed" + "</b>");
		}

		return false;
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Method to check if the OWS Update Packages is working i.e to update the
	 * package quantity of a package attached to the reservation successfully.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate. ->Market Code to be assigned
	 * ->Source Code to be assigned
	 */

	public void updatePackages_41242() throws Exception {
		try {
			String testName = "updatePackages_41242";
			WSClient.startTest(testName,
					"Verify that UpdatePackages updates the package details to the reservation successfully.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_extresort}", resort);

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_01");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_02");
						String changeResvRes = WSClient.processSOAPMessage(changeResvReq);
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true)) {

							/*******************
							 * OWS Update Packages Operation
							 ************************/

							String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_04");
							String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

							if (WSAssert.assertIfElementExists(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(updateResvRess,
										"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									String startDate = WSClient.getElementValue(updateResvResq,
											"UpdatePackagesRequest_StayDateRange_StartDate", XMLType.REQUEST);
									System.out.println(startDate);
									String quantity = WSClient.getElementValue(updateResvResq,
											"UpdatePackagesRequest_Quantity", XMLType.REQUEST);
									System.out.println(quantity);
									String packageCodeAct = WSClient.getElementValue(updateResvResq,
											"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
									System.out.println(packageCodeAct);
									String query = WSClient.getQuery("QS_01");
									System.out.println(query);
									LinkedHashMap<String, String> db = WSClient.getDBRow(query);
									System.out.println(db);
									if (WSAssert.assertEquals(packageCodeAct, db.get("PRODUCT_ID"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
												+ packageCodeAct + "  Actual : " + db.get("PRODUCT_ID"));

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
												+ packageCodeAct + " Actual :	 " + db.get("PRODUCT_ID"));

									}

									if (WSAssert.assertEquals(quantity, db.get("QUANTITY"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
												+ "		 Actual :	 	 " + db.get("QUANTITY"));

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
												+ "		 Actual : 		 " + db.get("QUANTITY"));

									}

								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"***The update packages operation has failed!***");
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"*** The update packages operation has failed! ***");

							if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
										XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
									true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess,
										"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess,
										"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode",
									true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(updateResvRess,
										"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

						} else
							WSClient.writeToReport(LogStatus.PASS, "Prerequisite Blocked-> Change Reservation ");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed ");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that error message is populated on the response when packages are
	 * added/updated to a cancelled reservation.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created->
	 * Reservation should be cancelled. ->Package Code should be sell separate.
	 * ->Market Code to be assigned ->Source Code to be assigned
	 */

	public void updatePackages_39023() throws Exception {
		try {
			String testName = "updatePackages_39023";
			WSClient.startTest(testName,
					"Verify that error message is populated on the response when package is  added to a cancelled reservation.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_extresort}", resort);

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
			     	
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_01");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						/************
						 * Prerequisite 3: Cancel Reservation
						 ************/

						WSClient.setData("{var_reservation_id}", resvID);

						String cancelResReq = WSClient.createSOAPMessage("CancelReservation", "DS_01");
						String cancelResRes = WSClient.processSOAPMessage(cancelResReq);

						if (WSAssert.assertIfElementExists(cancelResRes, "CancelReservationRS_Success", false)) {

							WSClient.setData("{var_resvID}", resvID);
							WSClient.setData("{var_confirmationNo}", confirmationId);
							WSClient.setData("{var_packageCode}",
									OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*******************
							 * OWS Update Packages Operation
							 ************************/

							String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
							String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

							if (WSAssert.assertIfElementExists(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(updateResvRess,
										"UpdatePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

									WSClient.writeToReport(LogStatus.PASS,
											"***The update packages operation was Unsuccessful***");

								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"***The update packages operation was successful***");
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"*** The update packages operation has failed! ***");

							if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is :<b> " + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
									true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess,
										"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess,
										"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}
							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode",
									true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(updateResvRess,
										"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
							}

						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "*** Blocked : Cancel Reservation Failed ***");
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that error message is populated on the response when packages are
	 * added/updated to a checked out reservation.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created->
	 * Reservation should be checked in and then checked out. ->Package Code
	 * should be sell separate. ->Market Code to be assigned ->Source Code to be
	 * assigned
	 */

	public void updatePackages_39025() throws Exception {
		try {
			String testName = "updatePackages_39025";
			WSClient.startTest(testName,
					"Verify that error message is populated on the response when package is added to a checked out reservation.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_extresort}", resort);

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_13");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {

						String roomNumber = null;

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_roomNumber}", "OWS01");

						/******
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ******/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
								&& WSAssert.assertIfElementExists(fetchHotelRoomsRes,
										"FetchHotelRoomsRS_HotelRooms_Room", true)) {

							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
									"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

						} else {

							/*****
							 * Prerequisite 4: Creating a room to assign
							 *******/

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

								roomNumber = WSClient.getElementValue(createRoomReq,
										"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						/****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 *****/

						WSClient.setData("{var_roomNumber}", roomNumber);
						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
								"DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
								"SetHousekeepingRoomStatusRS_Success", true)) {

							/***** Prerequisite 6: Assign Room *****/

							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								/*****
								 * Prerequisite 7: CheckIn Reservation
								 *****/

								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									/***
									 * Prerequisite 8: Opera Checkout
									 * Reservation
									 *****/
									WSClient.setData("{var_cashierId}",
											OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
									// String cancelCheckReq =
									// WSClient.createSOAPMessage("CheckoutReservation",
									// "DS_05");
									// String cancelCheckRes =
									// WSClient.processSOAPMessage(cancelCheckReq);
									// if
									// (WSAssert.assertIfElementExists(cancelCheckRes,
									// "CheckoutReservationRS_Success", true)) {

									boolean checkOutStatus = checkOutReservation("DS_05");
									if (checkOutStatus) {

										/*******************
										 * OWS Update Packages Operation
										 ************************/

										WSClient.setData("{var_resvID}", resvID);
										WSClient.setData("{var_confirmationNo}", confirmationId);
										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

										String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages",
												"DS_01");
										String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

										if (WSAssert.assertIfElementExists(updateResvRess,
												"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
											if (WSAssert.assertIfElementValueEquals(updateResvRess,
													"UpdatePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"The update packages operation was successful");

											}
										} else
											WSClient.writeToReport(LogStatus.FAIL,
													"The update packages operation has failed! ");

										if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement",
												true)) {

											/****
											 * Verifying that the error message
											 * is populated on the response
											 ********/

											String message = WSAssert.getElementValue(updateResvRess,
													"Result_Text_TextElement", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO,
														"The error populated on the response is : <b>" + message
																+ "</b>");
										}

										if (WSAssert.assertIfElementExists(updateResvRess,
												"UpdatePackagesResponse_Result_GDSError", true)) {

											/****
											 * Verifying whether the error
											 * Message is populated on the
											 * response
											 ****/

											String message = WSAssert.getElementValue(updateResvRess,
													"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO,
														"The error populated on the response is : <b>" + message
																+ "</b>");
										} else if (WSAssert.assertIfElementExists(updateResvRess,
												"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

											/****
											 * Verifying whether the error
											 * Message is populated on the
											 * response
											 ****/

											String message = WSAssert.getElementValue(updateResvRess,
													"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO,
														"The error populated on the response is : <b>" + message
																+ "</b>");
										}

										if (WSAssert.assertIfElementExists(updateResvRess,
												"UpdatePackagesResponse_faultcode", true)) {

											/****
											 * Verifying whether the error
											 * Message is populated on the
											 * response
											 ****/

											String message = WSClient.getElementValue(updateResvRess,
													"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO,
														"The error populated on the response is : <b>" + message
																+ "</b>");
										}

									} else
										WSClient.writeToReport(LogStatus.WARNING,
												"****Blocked: Unable to checkout reservation*** ");

								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Blocked : Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to change the status of room to vacant and inspected");
						}

					}

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create reservation");
					}
					
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Blocked :Property config data not available");
			}
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that error message is populated on the response when packages are
	 * added/updated to a reservation which was created on rate code and room
	 * types which is not channel allowed.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created->
	 * Reservation should be checked in and then checked out. ->Package Code
	 * should be sell separate. ->Market Code to be assigned ->Source Code to be
	 * assigned-> Rate code and room type are not channel allowed
	 */

	public void updatePackages_39026() throws Exception {
		try {
			String testName = "updatePackages_39026";
			WSClient.startTest(testName,
					"Verify that error message is populated on the response when package is added to a reservation that is not created by a channel allowed rate code.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_extresort}", resort);

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_01");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error"))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_roomNumber}", "OWS01");

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));
						WSClient.setData("{var_confirmationNo}", confirmationId);

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

							} else {
								WSClient.writeToReport(LogStatus.FAIL, "The update packages operation was successful");

							}

						} else
							WSClient.writeToReport(LogStatus.FAIL, "The update packages operation has failed! ");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						/****** Cancel Reservation ******/

						WSClient.setData("{var_reservation_id}", resvID);
						String cancelResReq = WSClient.createSOAPMessage("CancelReservation", "DS_01");
						String cancelResRes = WSClient.processSOAPMessage(cancelResReq);

						if (WSAssert.assertIfElementExists(cancelResRes, "CancelReservationRS_Success", true)) {

						} else
							WSClient.writeToReport(LogStatus.FAIL, "Cancel Reservation failed!");

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create reservation");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Blocked :Property config data not available");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that error message is populated on the response when packages are
	 * added/updated are not sell separate.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created->
	 * Reservation should be checked in and then checked out. ->Package Code
	 * should not be sell separate. ->Market Code to be assigned ->Source Code
	 * to be assigned
	 */

	public void updatePackages_39028() throws Exception {
		try {
			String testName = "updatePackages_39028";
			WSClient.startTest(testName,
					"Verify that error message is populated on the response when package added is not sell separate.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_extresort}", resort);

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_01");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error"))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_roomNumber}", "OWS01");

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_01"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_02");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

							} else
								WSClient.writeToReport(LogStatus.FAIL, "The update packages operation was successful");
						} else
							WSClient.writeToReport(LogStatus.FAIL, " The update packages operation has failed! ");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}
					
					}
					
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING,"Profile Creation failed");
			}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Blocked :Property config data not available");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify package details when all required data is sent on the request.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate. ->Market Code to be assigned
	 * ->Source Code to be assigned
	 */

	public void updatePackages_39032() throws Exception {
		try {
			String testName = "updatePackages_39032";
			WSClient.startTest(testName,
					"Verify description,short description,package code,taxYn,Posting Rhythm,Calculation rule,quantity,start date on the response when all fields are sent on the request.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_extresort}", resort);

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_01");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error"))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String query1 = WSClient.getQuery("OWSUpdatePackages", "QS_03");
						System.out.println(query1);
						HashMap<String, String> db1 = WSClient.getDBRow(query1);

						WSClient.setData("{var_startDate}", db1.get("BEGIN_DATE"));
						WSClient.setData("{var_endDate}", db1.get("END_DATE"));

						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_03");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String text = WSClient.getElementValue(updateResvRess, "Description_Text_TextElement",
										XMLType.RESPONSE);
								System.out.println(text);
								String startDate = WSClient.getElementValue(updateResvRess,
										"PackageCharge_ValidDates_StartDate_1", XMLType.RESPONSE);
								startDate = startDate.substring(0, startDate.indexOf("T"));
								System.out.println(startDate);
								// String
								// endDate=WSClient.getElementValue(updateResvRess,
								// "PackageCharge_ValidDates_EndDate",
								// XMLType.RESPONSE);
								// endDate=endDate.substring(0,
								// endDate.indexOf("T"));
								// System.out.println(endDate);
								String quantity = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_Quantity_1", XMLType.RESPONSE);
								System.out.println(quantity);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String posting = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_postingRhythm", XMLType.RESPONSE);
								System.out.println(posting);
								String calculation = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_calculationRule",
										XMLType.RESPONSE);
								System.out.println(calculation);
								String shortDesc = WSClient.getElementValue(updateResvRess,
										"ShortDescription_Text_TextElement", XMLType.RESPONSE);
								System.out.println(shortDesc);
								String taxYn = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_taxIncluded", XMLType.RESPONSE);
								System.out.println(taxYn);

								String query = WSClient.getQuery("QS_02");
								System.out.println(query);
								LinkedHashMap<String, String> db = WSClient.getDBRow(query);
								System.out.println(db);
								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}
								if (WSAssert.assertEquals(db.get("DESCRIPTION"), text, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Description -> Expected :		"
											+ db.get("DESCRIPTION") + "		 Actual :	 	 " + text);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Description -> Expected :		"
											+ db.get("DESCRIPTION") + "		 Actual : 		 " + text);

								}
								if (WSAssert.assertEquals(db.get("BEGIN_DATE"), startDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Start Date -> 	Expected :		"
											+ db.get("BEGIN_DATE") + "		 Actual :	 " + startDate);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Start Date -> 	Expected :		"
											+ db.get("BEGIN_DATE") + "		 Actual :	 " + startDate);

								}

								if (WSAssert.assertEquals(quantity, db.get("QUANTITY"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
											+ "		 Actual :	 	 " + db.get("QUANTITY"));

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
											+ "		 Actual : 		 " + db.get("QUANTITY"));

								}

								if (WSAssert.assertEquals(db.get("CALCULATION_RULE"), calculation, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Calculation Rule -> Expected :		" + db.get("CALCULATION_RULE")
													+ "		 ,	 Actual :	 	 	 " + calculation);

								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Calculation Rule -> Expected :		" + db.get("CALCULATION_RULE")
													+ "		 ,	 Actual :	 	 	 " + calculation);

								}

								if (WSAssert.assertEquals(db.get("POSTING_RHYTHM"), posting, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Posting Rhythm -> Expected :		"
											+ db.get("POSTING_RHYTHM") + "		 ,	 Actual :	 	 	 " + posting);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Posting Rhythm -> Expected :		"
											+ db.get("POSTING_RHYTHM") + "		 ,	 Actual :	  		 " + posting);

								}

							} else
								WSClient.writeToReport(LogStatus.FAIL, "The update packages operation has failed! ");
						} else
							WSClient.writeToReport(LogStatus.FAIL, " The update packages operation has failed! ");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
				
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING,"Profile Creation failed");
			}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that error message is populated when invalid confirmation id is
	 * sent on the request. PreRequisites Required: ->Package Code should be
	 * sell separate.
	 */

	public void updatePackages_39034() throws Exception {
		try {
			String testName = "updatePackages_39034";
			WSClient.startTest(testName,
					" Verify that error message is populated on the response when invalid confirmation id is sent on the request.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			/*******************
			 * OWS Update Packages Operation
			 ************************/

			String query = WSClient.getQuery("OWSUpdatePackages", "QS_04");
			HashMap<String, String> db = WSClient.getDBRow(query);

			WSClient.setData("{var_confirmationNo}", db.get("CONFIRMATION"));
			WSClient.setData("{var_packageCode}", OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));
			WSClient.setData("{var_extresort}", resort);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
			String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

			if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_resultStatusFlag",
					false)) {
				if (WSAssert.assertIfElementValueEquals(updateResvRess,
						"UpdatePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

				} else
					WSClient.writeToReport(LogStatus.FAIL, "The update packages operation was successful");
			} else
				WSClient.writeToReport(LogStatus.FAIL, " The update packages operation has failed! ");

			if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
				if (message != "*null*")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
						XMLType.RESPONSE);
				if (message != "*null*")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_OperaErrorCode",
					true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(updateResvRess,
						"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if (message != "*null*")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSClient.getElementValue(updateResvRess, "UpdatePackagesResponse_faultstring",
						XMLType.RESPONSE);
				if (message != "*null*")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that error message is populated when invalid start Date and end
	 * date is sent on the request. PreRequisites Required: -->Profile is
	 * created -->Reservation is created-> Reservation should be checked in and
	 * then checked out. ->Package Code should be sell separate. ->Market Code
	 * to be assigned ->Source Code to be assigned
	 */

	public void updatePackages_39037() throws Exception {
		try {
			String testName = "updatePackages_39037";
			WSClient.startTest(testName,
					" Verify that error message is populated on the response when invalid start date and end date is sent on the request.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_01");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error"))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_extresort}", resort);
						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String query = WSClient.getQuery("OWSUpdatePackages", "QS_05");
						HashMap<String, String> db = WSClient.getDBRow(query);

						WSClient.setData("{var_startDate}", db.get("BEGIN_DATE"));
						WSClient.setData("{var_endDate}", db.get("END_DATE"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_03");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

							} else
								WSClient.writeToReport(LogStatus.FAIL, "The update packages operation was successful");
						} else
							WSClient.writeToReport(LogStatus.FAIL, " The update packages operation has failed! ");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						WSClient.setData("{var_reservation_id}", resvID);

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create reservation");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}

			} else
				WSClient.writeToReport(LogStatus.PASS, "*** Blocked : Property Config data is unavailable");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that posting rhythm details are correctly populated for 3 Day
	 * Reservation with posting rhythm as every night.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate and have posting rhythm as
	 * "EveryNight" ->Market Code to be assigned ->Source Code to be assigned->
	 * Inclusive Tax to the package
	 */

	public void updatePackages_42362() throws Exception {
		try {
			String testName = "updatePackages_42362";
			WSClient.startTest(testName,
					"Verify that the posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as every night and tax is included.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_24");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_extresort}", resort);
						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_13"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);
						Double total2 = 0.0;
						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String query = WSClient.getQuery("OWSUpdatePackages", "QS_06");
								String price = WSClient.getDBRow(query).get("PRICE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_07");
								String quantityDb = WSClient.getDBRow(query).get("QUANTITY");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_08");
								String percent = WSClient.getDBRow(query).get("PERCENTAGE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_09");
								String tax, total1;

								Double totalP = Double.parseDouble(price) * Double.parseDouble(quantityDb);
								Double a = 0.0;
								if (percent != null) {
									Double des = (double) (100 * totalP) / (100 + Integer.parseInt(percent));
									// Double des = (double) (totalP *
									// Integer.parseInt(percent));
									System.out.println(des);
									a = (double) (totalP - des);
									tax = String.valueOf(a);
									System.out.println(tax);

								} else {
									tax = "0";
								}

								System.out.println("TAX" + tax);
								Double packageRate = (double) (totalP - Double.parseDouble(tax));
								System.out.println(packageRate);
								String unitPrice = String.valueOf(packageRate);
								Double totalA = Double.parseDouble(unitPrice) + Double.parseDouble(tax);
								System.out.println(totalA);
								String total = String.valueOf(totalA);
								System.out.println(total);
								DecimalFormat f = new DecimalFormat("##.00");
								tax = f.format(a);
								unitPrice = f.format(packageRate);
								total = f.format(totalA);
								// System.out.println(f.format(d));
								if (a == Math.floor(a)) {
									Integer p = (int) Math.round(a);
									tax = p.toString();
									System.out.println(tax);
								}
								if (packageRate == Math.floor(packageRate)) {
									Integer p = (int) Math.round(packageRate);
									unitPrice = p.toString();
								}
								if (totalA == Math.floor(totalA)) {
									Integer p = (int) Math.round(totalA);
									total = p.toString();
								}

								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}

								for (int i = 1; i <= 3; i++) {
									String quantity = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Quantity_" + i, XMLType.RESPONSE);
									System.out.println(quantity);
									String totalAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_TotalAmount_" + i, XMLType.RESPONSE);
									System.out.println(totalAmount);
									String taxAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Tax_" + i, XMLType.RESPONSE);
									System.out.println(taxAmount);
									String startDate = WSClient.getElementValue(updateResvRess,
											"PackageCharge_ValidDates_StartDate_" + i, XMLType.RESPONSE);
									startDate = startDate.substring(0, startDate.indexOf("T"));
									System.out.println(startDate);
									String unitAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_UnitAmount_" + i, XMLType.RESPONSE);
									System.out.println(unitAmount);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating for the date " + startDate + "</b>");
									if (WSAssert.assertEquals(quantity, quantityDb, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
												+ "		 Actual :	 	 " + quantityDb);

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
												+ "		 Actual : 		 " + quantityDb);

									}

									if (WSAssert.assertEquals(unitPrice, unitAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Unit Price : Expected -> " + unitPrice + " Actual  -> " + unitAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Unit Price : Expected  -> " + unitPrice + " Actual -> " + unitAmount);

									if (WSAssert.assertEquals(tax, taxAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);

									if (WSAssert.assertEquals(total, totalAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Total  Amount : Expected -> " + total + " Actual -> " + totalAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Total Amount : Expected -> " + total + " Actual -> " + totalAmount);
									total2 = total2 + Double.parseDouble(total);

								}
								String total3 = WSClient.getElementValue(updateResvRess,
										"PackageDetails_PackageInfo_Amount", XMLType.RESPONSE);
								System.out.println(total3);
								if (total2 == Math.floor(total2)) {
									Integer p = (int) Math.round(total2);
									total1 = p.toString();
								} else
									total1 = total2.toString();
								if (WSAssert.assertEquals(total1, total3, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								total2 = total2 + Integer.parseInt(total);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"***The update packages operation has failed!***");
						} else
							WSClient.writeToReport(LogStatus.FAIL, "*** The update packages operation has failed! ***");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
					
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that posting rhythm details are correctly populated for 3 Day
	 * Reservation with posting rhythm as every night.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate and have posting rhythm as
	 * "EveryNight" ->Market Code to be assigned ->Source Code to be assigned->
	 * No Tax to the package
	 */

	public void updatePackages_42443() throws Exception {
		try {
			String testName = "updatePackages_42443";
			WSClient.startTest(testName,
					"Verify that posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as every night and NO Tax.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_24");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_extresort}", resort);
						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_05"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);
						Double total2 = 0.0;
						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String query = WSClient.getQuery("OWSUpdatePackages", "QS_06");
								String price = WSClient.getDBRow(query).get("PRICE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_07");
								String quantityDb = WSClient.getDBRow(query).get("QUANTITY");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_08");
								String percent = WSClient.getDBRow(query).get("PERCENTAGE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_09");
								String tax, total1;

								Double totalP = Double.parseDouble(price) * Double.parseDouble(quantityDb);
								Double a = 0.0;
								if (percent != null) {
									Double des = (double) (totalP * Integer.parseInt(percent));
									System.out.println(des);
									a = (double) (des / 100.0);
									tax = String.valueOf(a);
									System.out.println(tax);

								} else {
									tax = "0";
								}

								System.out.println(tax);
								Double packageRate = (double) (totalP);
								System.out.println(packageRate);
								String unitPrice = String.valueOf(packageRate);
								Double totalA = Double.parseDouble(unitPrice) + Double.parseDouble(tax);
								System.out.println(totalA);
								String total = String.valueOf(totalA);
								System.out.println(total);

								if (a == Math.floor(a)) {
									Integer p = (int) Math.round(a);
									tax = p.toString();
									System.out.println(tax);
								}
								if (packageRate == Math.floor(packageRate)) {
									Integer p = (int) Math.round(packageRate);
									unitPrice = p.toString();
								}
								if (totalA == Math.floor(totalA)) {
									Integer p = (int) Math.round(totalA);
									total = p.toString();
								}

								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}

								for (int i = 1; i <= 3; i++) {
									String quantity = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Quantity_" + i, XMLType.RESPONSE);
									System.out.println(quantity);
									String totalAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_TotalAmount_" + i, XMLType.RESPONSE);
									System.out.println(totalAmount);
									String taxAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Tax_" + i, XMLType.RESPONSE);
									System.out.println(taxAmount);
									String startDate = WSClient.getElementValue(updateResvRess,
											"PackageCharge_ValidDates_StartDate_" + i, XMLType.RESPONSE);
									startDate = startDate.substring(0, startDate.indexOf("T"));
									System.out.println(startDate);
									String unitAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_UnitAmount_" + i, XMLType.RESPONSE);
									System.out.println(unitAmount);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating for the date " + startDate + "</b>");
									if (WSAssert.assertEquals(quantity, quantityDb, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
												+ "		 Actual :	 	 " + quantityDb);

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
												+ "		 Actual : 		 " + quantityDb);

									}

									if (WSAssert.assertEquals(unitPrice, unitAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Unit Price : Expected -> " + unitPrice + " Actual  -> " + unitAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Unit Price : Expected  -> " + unitPrice + " Actual -> " + unitAmount);

									if (WSAssert.assertEquals(tax, taxAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);

									if (WSAssert.assertEquals(total, totalAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Total  Amount : Expected -> " + total + " Actual -> " + totalAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Total Amount : Expected -> " + total + " Actual -> " + totalAmount);
									total2 = total2 + Double.parseDouble(total);

								}
								String total3 = WSClient.getElementValue(updateResvRess,
										"PackageDetails_PackageInfo_Amount", XMLType.RESPONSE);
								System.out.println(total3);
								if (total2 == Math.floor(total2)) {
									Integer p = (int) Math.round(total2);
									total1 = p.toString();
								} else
									total1 = total2.toString();

								if (WSAssert.assertEquals(total1, total3, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								total2 = total2 + Double.parseDouble(total);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"***The update packages operation has failed!***");
						} else
							WSClient.writeToReport(LogStatus.FAIL, "*** The update packages operation has failed! ***");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS"})
	/**
	 * Verify that posting rhythm details are correctly populated for 3 Day
	 * Reservation with posting rhythm as arrival night.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate and have posting rhythm as
	 * "ArrivalNight" ->Market Code to be assigned ->Source Code to be
	 * assigned->Inclusive Tax
	 */

	public void updatePackages_42379() throws Exception {
		try {
			String testName = "updatePackages_42379";
			WSClient.startTest(testName,
					"Verify that posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as arrival night and tax is inclusive.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_24");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_extresort}", resort);
						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_15"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);
						Double total2 = 0.0;
						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String query = WSClient.getQuery("OWSUpdatePackages", "QS_06");
								String price = WSClient.getDBRow(query).get("PRICE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_07");
								String quantityDb = WSClient.getDBRow(query).get("QUANTITY");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_08");
								String percent = WSClient.getDBRow(query).get("PERCENTAGE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_09");

								/*** Tax Calculations *****/

								String tax, total1;
								Double totalP = Double.parseDouble(price) * Double.parseDouble(quantityDb);
								Double a = 0.0;

								if (percent != null) {
									Double des = (double) (100 * totalP) / (100 + Integer.parseInt(percent));
									// Double des = (double) (totalP *
									// Integer.parseInt(percent));
									System.out.println(des);
									a = (double) (totalP - des);
									tax = String.valueOf(a);
									System.out.println(tax);

								} else {
									tax = "0";
								}

								System.out.println("TAX" + tax);
								Double packageRate = (double) (totalP - Double.parseDouble(tax));
								System.out.println(packageRate);
								String unitPrice = String.valueOf(packageRate);
								Double totalA = Double.parseDouble(unitPrice) + Double.parseDouble(tax);
								System.out.println(totalA);
								String total = String.valueOf(totalA);
								System.out.println(total);
								DecimalFormat f = new DecimalFormat("##.00");
								tax = f.format(a);
								unitPrice = f.format(packageRate);
								total = f.format(totalA);


								if (a == Math.floor(a)) {
									Integer p = (int) Math.round(a);
									tax = p.toString();
									System.out.println(tax);
								}
								if (packageRate == Math.floor(packageRate)) {
									Integer p = (int) Math.round(packageRate);
									unitPrice = p.toString();
								}
								if (totalA == Math.floor(totalA)) {
									Integer p = (int) Math.round(totalA);
									total = p.toString();
								}

								/******** Validations *******/

								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}

								String startDate = WSClient.getElementValue(updateResvRess,
										"PackageCharge_ValidDates_StartDate_1", XMLType.RESPONSE);
								startDate = startDate.substring(0, startDate.indexOf("T"));
								System.out.println(startDate);
								String quantity = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_Quantity_1", XMLType.RESPONSE);
								System.out.println(quantity);
								String totalAmount = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_TotalAmount_1", XMLType.RESPONSE);
								System.out.println(totalAmount);
								String taxAmount = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_Tax_1", XMLType.RESPONSE);
								System.out.println(taxAmount);
								String unitAmount = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_UnitAmount_1", XMLType.RESPONSE);
								System.out.println(unitAmount);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating for the date " + startDate + "</b>");
								if (WSAssert.assertEquals(quantity, quantityDb, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
											+ "		 Actual :	 	 " + quantityDb);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
											+ "		 Actual : 		 " + quantityDb);

								}

								if (WSAssert.assertEquals(unitPrice, unitAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Unit Price : Expected -> " + unitPrice + " Actual  -> " + unitAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Unit Price : Expected  -> " + unitPrice + " Actual -> " + unitAmount);

								if (WSAssert.assertEquals(tax, taxAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Tax : Expected -> " + tax + " Actual -> " + taxAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Tax : Expected -> " + tax + " Actual -> " + taxAmount);

								if (WSAssert.assertEquals(total, totalAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total  Amount : Expected -> " + total + " Actual -> " + totalAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Amount : Expected -> " + total + " Actual -> " + totalAmount);

								if (WSAssert.assertIfElementExists(updateResvRess,
										"PackageCharge_ValidDates_StartDate_2", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The posting details of day 2 should not be present on the response as the posting rhythm is arrival night");
								} else
									WSClient.writeToReport(LogStatus.PASS,
											"The posting details of day 2  are not present on the response as the posting rhythm is arrival night");

								if (WSAssert.assertIfElementExists(updateResvRess,
										"PackageCharge_ValidDates_StartDate_3", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The posting details of day 3 should not be present on the response as the posting rhythm is arrival night");
								} else
									WSClient.writeToReport(LogStatus.PASS,
											"The posting details of day 3 are not present on the response as the posting rhythm is arrival night");

								total2 = total2 + Double.parseDouble(total);
								String total3 = WSClient.getElementValue(updateResvRess,
										"PackageDetails_PackageInfo_Amount", XMLType.RESPONSE);
								System.out.println(total3);
								if (total2 == Math.floor(total2)) {
									Integer p = (int) Math.round(total2);
									total1 = p.toString();
								} else
									total1 = total2.toString();
								if (WSAssert.assertEquals(total1, total3, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								total2 = total2 + Integer.parseInt(total);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"***The update packages operation has failed!***");
						} else
							WSClient.writeToReport(LogStatus.FAIL, "*** The update packages operation has failed! ***");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
					
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that posting rhythm details are correctly populated for 3 Day
	 * Reservation with posting rhythm as arrival night.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate and have posting rhythm as
	 * "ArrivalNight" ->Market Code to be assigned ->Source Code to be
	 * assigned->No Tax to the package
	 */

	public void updatePackages_42449() throws Exception {
		try {
			String testName = "updatePackages_42449";
			WSClient.startTest(testName,
					"Verify that posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as arrival night and no tax attached.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{var_extresort}", resort);
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_24");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_07"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);
						Integer total2 = 0;

						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String query = WSClient.getQuery("OWSUpdatePackages", "QS_06");
								String price = WSClient.getDBRow(query).get("PRICE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_07");
								String quantityDb = WSClient.getDBRow(query).get("QUANTITY");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_08");
								String percent = WSClient.getDBRow(query).get("PERCENTAGE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_09");

								/*** Tax Calculations *****/

								String tax, total1;
								Double totalP = Double.parseDouble(price) * Double.parseDouble(quantityDb);
								Double a = 0.0;
								if (percent != null) {
									Double des = (double) (totalP * Integer.parseInt(percent));
									System.out.println(des);
									a = (double) (des / 100.0);
									tax = String.valueOf(a);
									System.out.println(tax);

								} else {
									tax = "0";
								}

								/***** Total Charges Calculation ******/

								System.out.println(tax);
								Double packageRate = (double) (totalP);
								System.out.println(packageRate);
								String unitPrice = String.valueOf(packageRate);
								Double totalA = Double.parseDouble(unitPrice) + Double.parseDouble(tax);
								System.out.println(totalA);
								String total = String.valueOf(totalA);
								System.out.println(total);

								if (a == Math.floor(a)) {
									Integer p = (int) Math.round(a);
									tax = p.toString();
									System.out.println(tax);
								}
								if (packageRate == Math.floor(packageRate)) {
									Integer p = (int) Math.round(packageRate);
									unitPrice = p.toString();
								}
								if (totalA == Math.floor(totalA)) {
									Integer p = (int) Math.round(totalA);
									total = p.toString();
								}

								/***** Validations ******/

								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}

								String startDate = WSClient.getElementValue(updateResvRess,
										"PackageCharge_ValidDates_StartDate_1", XMLType.RESPONSE);
								startDate = startDate.substring(0, startDate.indexOf("T"));
								System.out.println(startDate);
								String quantity = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_Quantity_1", XMLType.RESPONSE);
								System.out.println(quantity);
								String totalAmount = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_TotalAmount_1", XMLType.RESPONSE);
								System.out.println(totalAmount);
								String taxAmount = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_Tax_1", XMLType.RESPONSE);
								System.out.println(taxAmount);
								String unitAmount = WSClient.getElementValue(updateResvRess,
										"ExpectedCharges_PackageCharge_UnitAmount_1", XMLType.RESPONSE);
								System.out.println(unitAmount);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating for the date " + startDate + "</b>");
								if (WSAssert.assertEquals(quantity, quantityDb, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
											+ "		 Actual :	 	 " + quantityDb);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
											+ "		 Actual : 		 " + quantityDb);

								}

								if (WSAssert.assertEquals(unitPrice, unitAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Unit Price : Expected -> " + unitPrice + " Actual  -> " + unitAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Unit Price : Expected  -> " + unitPrice + " Actual -> " + unitAmount);

								if (WSAssert.assertEquals(tax, taxAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Tax : Expected -> " + tax + " Actual -> " + taxAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Tax : Expected -> " + tax + " Actual -> " + taxAmount);

								if (WSAssert.assertEquals(total, totalAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total  Amount : Expected -> " + total + " Actual -> " + totalAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Amount : Expected -> " + total + " Actual -> " + totalAmount);

								if (WSAssert.assertIfElementExists(updateResvRess,
										"PackageCharge_ValidDates_StartDate_2", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The posting details of day 2 should not be present on the response as the posting rhythm is arrival night");
								} else
									WSClient.writeToReport(LogStatus.PASS,
											"The posting details of day 2  are not present on the response as the posting rhythm is arrival night");

								if (WSAssert.assertIfElementExists(updateResvRess,
										"PackageCharge_ValidDates_StartDate_3", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The posting details of day 3 should not be present on the response as the posting rhythm is arrival night");
								} else
									WSClient.writeToReport(LogStatus.PASS,
											"The posting details of day 3 are not present on the response as the posting rhythm is arrival night");

								total2 = total2 + Integer.parseInt(total);
								String total3 = WSClient.getElementValue(updateResvRess,
										"PackageDetails_PackageInfo_Amount", XMLType.RESPONSE);
								System.out.println(total3);
								if (total2 == Math.floor(total2)) {

								}
								total1 = total2.toString();
								if (WSAssert.assertEquals(total1, total3, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								total2 = total2 + Integer.parseInt(total);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"***The update packages operation has failed!***");
						} else
							WSClient.writeToReport(LogStatus.FAIL, "*** The update packages operation has failed! ***");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
					
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that posting rhythm details are correctly populated for 3 Day
	 * Reservation with posting rhythm as Every night except last night.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate and have posting rhythm as
	 * "EveryNightExceptNight" ->Market Code to be assigned ->Source Code to be
	 * assigned ->Inclusive Tax
	 */

	public void updatePackages_42441() throws Exception {
		try {
			String testName = "updatePackages_42441";
			WSClient.startTest(testName,
					"Verify that the posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as Every night except last night and tax is inclusive.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{var_extresort}", resort);
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_24");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_14"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);
						Double total2 = 0.0;
						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String query = WSClient.getQuery("OWSUpdatePackages", "QS_06");
								String price = WSClient.getDBRow(query).get("PRICE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_07");
								String quantityDb = WSClient.getDBRow(query).get("QUANTITY");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_08");
								String percent = WSClient.getDBRow(query).get("PERCENTAGE");

								/*** Tax Calculations *****/

								String tax, total1;
								Double totalP = Double.parseDouble(price) * Double.parseDouble(quantityDb);
								Double a = 0.0;
								
								if (percent != null) {
									Double des = (double) (100 * totalP) / (100 + Integer.parseInt(percent));
									// Double des = (double) (totalP *
									// Integer.parseInt(percent));
									System.out.println(des);
									a = (double) (totalP - des);
									tax = String.valueOf(a);
									System.out.println(tax);

								} else {
									tax = "0";
								}

								System.out.println("TAX" + tax);
								Double packageRate = (double) (totalP - Double.parseDouble(tax));
								System.out.println(packageRate);
								String unitPrice = String.valueOf(packageRate);
								Double totalA = Double.parseDouble(unitPrice) + Double.parseDouble(tax);
								System.out.println(totalA);
								String total = String.valueOf(totalA);
								System.out.println(total);
								DecimalFormat f = new DecimalFormat("##.00");
								tax = f.format(a);
								unitPrice = f.format(packageRate);
								total = f.format(totalA);

								if (a == Math.floor(a)) {
									Integer p = (int) Math.round(a);
									tax = p.toString();
									System.out.println(tax);
								}
								if (packageRate == Math.floor(packageRate)) {
									Integer p = (int) Math.round(packageRate);
									unitPrice = p.toString();
								}
								if (totalA == Math.floor(totalA)) {
									Integer p = (int) Math.round(totalA);
									total = p.toString();
								}

								/********* Validations ************/

								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}

								String startDate;
								for (int i = 1; i <= 2; i++) {
									String quantity = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Quantity_" + i, XMLType.RESPONSE);
									System.out.println(quantity);
									String totalAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_TotalAmount_" + i, XMLType.RESPONSE);
									System.out.println(totalAmount);
									String taxAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Tax_" + i, XMLType.RESPONSE);
									System.out.println(taxAmount);
									startDate = WSClient.getElementValue(updateResvRess,
											"PackageCharge_ValidDates_StartDate_" + i, XMLType.RESPONSE);
									startDate = startDate.substring(0, startDate.indexOf("T"));
									System.out.println(startDate);
									String unitAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_UnitAmount_" + i, XMLType.RESPONSE);
									System.out.println(unitAmount);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating for the date " + startDate + "</b>");
									if (WSAssert.assertEquals(quantity, quantityDb, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
												+ "		 Actual :	 	 " + quantityDb);

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
												+ "		 Actual : 		 " + quantityDb);

									}

									if (WSAssert.assertEquals(unitPrice, unitAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Unit Price : Expected -> " + unitPrice + " Actual  -> " + unitAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Unit Price : Expected  -> " + unitPrice + " Actual -> " + unitAmount);

									if (WSAssert.assertEquals(tax, taxAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);

									if (WSAssert.assertEquals(total, totalAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Total  Amount : Expected -> " + total + " Actual -> " + totalAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Total Amount : Expected -> " + total + " Actual -> " + totalAmount);
									total2 = total2 + Double.parseDouble(total);

								}

								if (WSAssert.assertIfElementExists(updateResvRess,
										"PackageCharge_ValidDates_StartDate_3", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The posting details of day 3 should not be present on the response as the posting rhythm is arrival night");
								} else
									WSClient.writeToReport(LogStatus.PASS,
											"The posting details of day 3 are not present on the response as the posting rhythm is arrival night");

								String total3 = WSClient.getElementValue(updateResvRess,
										"PackageDetails_PackageInfo_Amount", XMLType.RESPONSE);
								System.out.println(total3);
								if (total2 == Math.floor(total2)) {
									Integer p = (int) Math.round(total2);
									total1 = p.toString();
								} else
									total1 = total2.toString();
								if (WSAssert.assertEquals(total1, total3, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"***The update packages operation has failed!***");
						} else
							WSClient.writeToReport(LogStatus.FAIL, "*** The update packages operation has failed! ***");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
					
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePackages", "Reservation", "OWS" })
	/**
	 * Verify that posting rhythm details are correctly populated for 3 Day
	 * Reservation with posting rhythm as Every night except last night.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Package Code should be sell separate and have posting rhythm as
	 * "EveryNightExceptNight" ->Market Code to be assigned ->Source Code to be
	 * assigned ->NO Tax included to the package
	 */

	public void updatePackages_42446() throws Exception {
		try {
			String testName = "updatePackages_42446";
			WSClient.startTest(testName,
					"Verify that the posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as Every night except last night and NO tax is added.",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{var_extresort}", resort);
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					HashMap<String, String> res = CreateReservation.createReservation("DS_24");
					resvID = res.get("reservationId");
					confirmationId = res.get("confirmationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation ID : " + confirmationId + "</b>");

						WSClient.setData("{var_resvID}", resvID);
						WSClient.setData("{var_confirmationNo}", confirmationId);
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_06"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Update Packages Operation
						 ************************/

						String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePackages", "DS_01");
						String updateResvRess = WSClient.processSOAPMessage(updateResvResq);
						Double total2 = 0.0;
						if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(updateResvRess,
									"UpdatePackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String packageCodeAct = WSClient.getElementValue(updateResvRess,
										"BookedPackageList_PackageDetails_PackageInfo_packageCode", XMLType.RESPONSE);
								System.out.println(packageCodeAct);
								String packageCodeExp = WSClient.getElementValue(updateResvResq,
										"UpdatePackagesRequest_ProductCode", XMLType.REQUEST);
								System.out.println(packageCodeExp);
								String query = WSClient.getQuery("OWSUpdatePackages", "QS_06");
								String price = WSClient.getDBRow(query).get("PRICE");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_07");
								String quantityDb = WSClient.getDBRow(query).get("QUANTITY");
								query = WSClient.getQuery("OWSUpdatePackages", "QS_08");
								String percent = WSClient.getDBRow(query).get("PERCENTAGE");

								String tax, total1;
								Double totalP = Double.parseDouble(price) * Double.parseDouble(quantityDb);
								Double a = 0.0;
								if (percent != null) {
									Double des = (double) (totalP * Integer.parseInt(percent));
									System.out.println(des);
									a = (double) (des / 100.0);
									tax = String.valueOf(a);
									System.out.println(tax);

								} else {
									tax = "0";
								}

								System.out.println(tax);
								Double packageRate = (double) (totalP);
								System.out.println(packageRate);
								String unitPrice = String.valueOf(packageRate);
								Double totalA = Double.parseDouble(unitPrice) + Double.parseDouble(tax);
								System.out.println(totalA);
								String total = String.valueOf(totalA);
								System.out.println(total);

								if (a == Math.floor(a)) {
									Integer p = (int) Math.round(a);
									tax = p.toString();
									System.out.println(tax);
								}
								if (packageRate == Math.floor(packageRate)) {
									Integer p = (int) Math.round(packageRate);
									unitPrice = p.toString();
								}
								if (totalA == Math.floor(totalA)) {
									Integer p = (int) Math.round(totalA);
									total = p.toString();
								}
								if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Package Code -> Expected : 	"
											+ packageCodeExp + "  Actual : " + packageCodeAct);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Package Code -> Expected :	"
											+ packageCodeExp + " Actual :	 " + packageCodeAct);

								}

								String startDate;
								for (int i = 1; i <= 2; i++) {
									String quantity = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Quantity_" + i, XMLType.RESPONSE);
									System.out.println(quantity);
									String totalAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_TotalAmount_" + i, XMLType.RESPONSE);
									System.out.println(totalAmount);
									String taxAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_Tax_" + i, XMLType.RESPONSE);
									System.out.println(taxAmount);
									startDate = WSClient.getElementValue(updateResvRess,
											"PackageCharge_ValidDates_StartDate_" + i, XMLType.RESPONSE);
									startDate = startDate.substring(0, startDate.indexOf("T"));
									System.out.println(startDate);
									String unitAmount = WSClient.getElementValue(updateResvRess,
											"ExpectedCharges_PackageCharge_UnitAmount_" + i, XMLType.RESPONSE);
									System.out.println(unitAmount);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating for the date " + startDate + "</b>");
									if (WSAssert.assertEquals(quantity, quantityDb, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" + quantity
												+ "		 Actual :	 	 " + quantityDb);

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" + quantity
												+ "		 Actual : 		 " + quantityDb);

									}

									if (WSAssert.assertEquals(unitPrice, unitAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Unit Price : Expected -> " + unitPrice + " Actual  -> " + unitAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Unit Price : Expected  -> " + unitPrice + " Actual -> " + unitAmount);

									if (WSAssert.assertEquals(tax, taxAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Tax : Expected -> " + tax + " Actual -> " + taxAmount);

									if (WSAssert.assertEquals(total, totalAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Total  Amount : Expected -> " + total + " Actual -> " + totalAmount);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Total Amount : Expected -> " + total + " Actual -> " + totalAmount);
									total2 = total2 + Double.parseDouble(total);

								}

								if (WSAssert.assertIfElementExists(updateResvRess,
										"PackageCharge_ValidDates_StartDate_3", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"The posting details of day 3 should not be present on the response as the posting rhythm is arrival night");
								} else
									WSClient.writeToReport(LogStatus.PASS,
											"The posting details of day 3 are not present on the response as the posting rhythm is arrival night");

								String total3 = WSClient.getElementValue(updateResvRess,
										"PackageDetails_PackageInfo_Amount", XMLType.RESPONSE);
								System.out.println(total3);
								if (total2 == Math.floor(total2)) {
									Integer p = (int) Math.round(total2);
									total1 = p.toString();
								} else
									total1 = total2.toString();
								if (WSAssert.assertEquals(total1, total3, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Total Package Amount : Expected -> " + total1 + " Actual -> " + total3);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"***The update packages operation has failed!***");
						} else
							WSClient.writeToReport(LogStatus.FAIL, "*** The update packages operation has failed! ***");

						if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement",
									XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_Result_GDSError",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateResvRess,
								"UpdatePackagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateResvRess,
									"UpdatePackagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePackagesResponse_faultcode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateResvRess,
									"UpdatePackagesResponse_faultstring", XMLType.RESPONSE);
							if (message != "*null*")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

					}
					
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile Creation failed");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvID);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}
}
