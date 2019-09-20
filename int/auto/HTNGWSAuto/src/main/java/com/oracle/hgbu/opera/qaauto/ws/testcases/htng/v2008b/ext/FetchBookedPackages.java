package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

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

public class FetchBookedPackages extends WSSetUp {

	String profileID = "";

	@Test(groups = { "sanity", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_4533() {
		try {
			String testName = "fetchBookedPackages_Ext_4533";
			WSClient.startTest(testName,
					"Verify  packageCode, calculationRule & postingRhythm, displayed on the response when reservationID having a single package is provided on the FetchBookedPackagesRequest.",
					"sanity");

			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// *************Prerequisite 1 : Create Profile*************//

				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

							db = WSClient.getDBRow(WSClient.getQuery("QS_02"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("BookedPackageList_PackageDetail_PackageInfo_packageCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("BookedPackageList_PackageDetail_PackageInfo_postingRhythm",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("BookedPackageList_PackageDetail_PackageInfo_calculationRule",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							LinkedHashMap<String, String> actual = WSClient.getSingleNodeList(fetchBookedPackres, res,
									false, XMLType.RESPONSE);

							WSAssert.assertEquals(db, actual, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}
				}
			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1993() {
		try {
			String testName = "fetchBookedPackages_Ext_1993";
			WSClient.startTest(testName,
					"Verify multiple packageCode, calculationRule & postingRhythm, displayed on the response when reservationID having multiple packages is provided on the FetchBookedPackagesRequest.",
					"minimumRegression");

			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// *************Prerequisite 1 : Create Profile*************//

				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

							db = WSClient.getDBRows(WSClient.getQuery("QS_02"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("BookedPackageList_PackageDetail_PackageInfo_packageCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("BookedPackageList_PackageDetail_PackageInfo_postingRhythm",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("BookedPackageList_PackageDetail_PackageInfo_calculationRule",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							List<LinkedHashMap<String, String>> actual = WSClient
									.getMultipleNodeList(fetchBookedPackres, res, false, XMLType.RESPONSE);

							WSAssert.assertEquals(actual, db, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}
				}
			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

//	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
//	public void fetchBookedPackages_Ext_1991() {
//		try {
//			String testName = "fetchBookedPackages_Ext_1991";
//			WSClient.startTest(testName,
//					"Verify  packageCode and ValidDates should be displayed on the response when reservationID having a package is passed on the request.",
//					"minimumRegression");
//
//			if (OperaPropConfig
//					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
//				String interfaceName = HTNGLib.getHTNGInterface();
//				String resortOperaValue = OPERALib.getResort();
//				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
//
//				WSClient.setData("{var_profileSource}", interfaceName);
//
//				WSClient.setData("{var_resort}", resortOperaValue);
//				WSClient.setData("{var_extResort}", resortExtValue);
//
//				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
//
//				// *************Prerequisite 1 : Create Profile*************//
//
//				OPERALib.setOperaHeader(OPERALib.getUserName());
//				if (profileID.equals(""))
//					profileID = CreateProfile.createProfile("DS_01");
//				if (!profileID.equals("error")) {
//					WSClient.setData("{var_profileId}", profileID);
//
//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//
//					// *************Prerequisite 2 : Create Reservation with
//					// rate code which have packages*************//
//
//					HashMap<String, String> resv = CreateReservation.createReservation("DS_14");
//					String resvID = resv.get("reservationId");
//
//					if (!resvID.equals("error")) {
//
//						WSClient.setData("{var_resvId}", resvID);
//
//						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
//								HTNGLib.getInterfaceFromAddress());
//						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
//						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
//						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
//								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//
//							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
//
//							db = WSClient.getDBRows(WSClient.getQuery("QS_03"));
//
//							HashMap<String, String> res = new HashMap<String, String>();
//							res.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
//							res.put("PackageCharge_ValidDates_End", "PackageDetail_ExpectedCharges_PackageCharge");
//
//							List<LinkedHashMap<String, String>> actual = WSClient
//									.getMultipleNodeList(fetchBookedPackres, res, false, XMLType.RESPONSE);
//
//							WSAssert.assertEquals(actual, db, false);
//
//						}
//					}
//
//				}
//			}
//		}
//
//		catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		} finally {
//			try {
//				if(!WSClient.getData("{var_resvId}").equals(""))
//			        CancelReservation.cancelReservation("DS_02");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1990() {
		try {
			String testName = "fetchBookedPackages_Ext_1990";
			WSClient.startTest(testName,
					" Verify Description and short description of a package should be displayed on the response when reservationID is passed on the request",
					"minimumRegression");

			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

							db = WSClient.getDBRow(WSClient.getQuery("QS_04"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("BookedPackageList_PackageDetail_PackageInfo_packageCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("Description_Text_TextElement",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("ShortDescription_Text_TextElement",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							LinkedHashMap<String, String> actual = WSClient.getSingleNodeList(fetchBookedPackres, res,
									true, XMLType.RESPONSE);
							WSAssert.assertEquals(db, actual, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}

				}

			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1989() {
		try {
			String testName = "fetchBookedPackages_Ext_1989";
			WSClient.startTest(testName,
					"Verify AmountCurrencyCode, Amount & Allowance should be displayed on the FetchBookedPackagesResponse when ReservationID having package is passed on the FetchBookedPackagesRequest",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

							db = WSClient.getDBRow(WSClient.getQuery("QS_05"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("BookedPackageList_PackageDetail_PackageInfo_packageCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("PackageDetail_PackageInfo_Amount_currencyCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("PackageDetail_PackageInfo_Amount",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("PackageDetail_PackageInfo_Allowance",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							LinkedHashMap<String, String> actual = WSClient.getSingleNodeList(fetchBookedPackres, res,
									false, XMLType.RESPONSE);

							WSAssert.assertEquals(db, actual, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}

				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1992() {
		try {
			String testName = "fetchBookedPackages_Ext_1992";
			WSClient.startTest(testName,
					"Verify Rate Code,Quantity of package attached to a reservation is displayed correctly in fetch booked packages response.",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

				// *************Prerequisite 1 : Create Profile*************//

				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

							db = WSClient.getDBRow(WSClient.getQuery("QS_06"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("ExpectedCharges_PackageCharge_PackageCode",
									"BookedPackageList_PackageDetail_ExpectedCharges");
							res.put("ExpectedCharges_PackageCharge_Quantity",
									"BookedPackageList_PackageDetail_ExpectedCharges");
							res.put("ExpectedCharges_PackageCharge_RateCode",
									"BookedPackageList_PackageDetail_ExpectedCharges");

							LinkedHashMap<String, String> actual = WSClient.getSingleNodeList(fetchBookedPackres, res,
									false, XMLType.RESPONSE);

							WSAssert.assertEquals(db, actual, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_38841() {
		try {
			String testName = "fetchBookedPackages_Ext_38841";
			WSClient.startTest(testName,
					"Verify Amount & Allowance should be displayed on the FetchBookedPackagesResponse with multi night reservation having package",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

				// *************Prerequisite 1 : Create Profile*************//

				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_14");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

							db = WSClient.getDBRow(WSClient.getQuery("QS_07"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("PackageDetail_PackageInfo_Amount",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("PackageDetail_PackageInfo_Allowance",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							LinkedHashMap<String, String> actual = WSClient.getSingleNodeList(fetchBookedPackres, res,
									false, XMLType.RESPONSE);

							WSAssert.assertEquals(db, actual, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_2006() {
		try {
			String testName = "fetchBookedPackages_Ext_2006";
			WSClient.startTest(testName,
					"Verify amount & allowance are fetching correctly for the input reservation only when the input reservation is shared by another reservation",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);
            
				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

				// *************Prerequisite 1 : Create Profile*************//

				OPERALib.setOperaHeader(OPERALib.getUserName());

				String profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID1: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID1: "+resvID+"</b>");
						String profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID2: "+profileID2+"</b>");
							WSClient.setData("{var_profileId}", profileID2);

							HashMap<String, String> resv1 = CreateReservation.createReservation("DS_04");
							String resvID1 = resv1.get("reservationId");

							if (!resvID1.equals("error")) {

								WSClient.setData("{var_resvId1}", resvID1);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID2: "+resvID1+"</b>");
								// Prerequisite 4 : combining Reservation

								String combineReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
								String combineRes = WSClient.processSOAPMessage(combineReq);

								if (WSAssert.assertIfElementExists(combineRes, "CombineShareReservationsRS_Success",
										true)) {

									String combineResID = WSClient.getElementValue(combineRes,
											"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
									WSClient.setData("{var_resvId}", combineResID);

									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
											HTNGLib.getInterfaceFromAddress());
									String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages",
											"DS_01");
									String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
									if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
											"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

										db = WSClient.getDBRow(WSClient.getQuery("QS_08"));

										HashMap<String, String> res = new HashMap<String, String>();
										res.put("PackageDetail_PackageInfo_Amount",
												"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
										res.put("PackageDetail_PackageInfo_Allowance",
												"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

										LinkedHashMap<String, String> actual = WSClient
												.getSingleNodeList(fetchBookedPackres, res, false, XMLType.RESPONSE);

										WSAssert.assertEquals(db, actual, false);

									}
									if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
										String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
										}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"*************Blocked : Unable to combine reservation*************");
								}
							}

						}
					}

				}
			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
			        CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	

	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1991() {
		try {
			String testName = "fetchBookedPackages_Ext_1991";
			WSClient.startTest(testName,
					"verify expected charges as per Flat Rate for 3 day reservation are displayed correctly in fetchbookedpackages response.",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

				// *************Prerequisite 1 : Create Profile*************//

				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_28");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_resvID}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_05");
						WSClient.setData("{var_packageCode}",pkg);
						String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_07");
						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
						
						//******OWS Fetch Booking*******//
						  
						if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
							
                         WSClient.writeToReport(LogStatus.INFO,"<b>Successfully added the package to reservation</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackReq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackReq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							
							Double total2=0.0;
							Double totaltax=0.0;
							Double totalunit=0.0;
							String packageCodeAct=WSClient.getElementValue(fetchBookedPackRes,"BookedPackageList_PackageDetail_PackageInfo_packageCode",XMLType.RESPONSE);
							System.out.println(packageCodeAct);
							
							String packageCodeExp=WSClient.getDBRow(WSClient.getQuery("QS_10")).get("PRODUCT_ID");
							System.out.println(packageCodeExp);
							String query = WSClient.getQuery("QS_09");
							String price = WSClient.getDBRow(query).get("PRICE");
							query=WSClient.getQuery("QS_10");
							String quantityDb= WSClient.getDBRow(query).get("QUANTITY");
							query=WSClient.getQuery("QS_11");
							String percent= WSClient.getDBRow(query).get("PERCENTAGE");
							query=WSClient.getQuery("QS_12");
							String tax;
							
							Double totalP=Double.parseDouble(price)*Double.parseDouble(quantityDb);
							Double a = 0.0;
							if(percent!=null){
								Double des=(double) (totalP*Integer.parseInt(percent));
								System.out.println(des);
								 a= (double) (des/100.0);
								tax=String.valueOf(a);
								System.out.println(tax);
								
								}
								else{
									tax="0.00";
								}
								
							System.out.println(tax);
							Double packageRate=(double) (totalP-Double.parseDouble(tax));
							System.out.println(packageRate);
							Double unitRate=packageRate;
							System.out.println(unitRate);
							String unitPrice=String.valueOf(unitRate);
							String totalPrice=String.valueOf(packageRate);
							Double totalA=Double.parseDouble(totalPrice)+Double.parseDouble(tax);
							System.out.println(totalA);
							String total=String.valueOf(totalA);
							System.out.println(total);
							
//							if(a==Math.floor(a)){
//								Integer p=(int) Math.round(a);
//								tax=p.toString();
//								System.out.println(tax);
//							}
							if(unitRate==Math.floor(unitRate) ){
								Integer p=(int) Math.round(unitRate);
								unitPrice=p.toString();
							}
							if(totalA==Math.floor(totalA) ){
								Integer p=(int) Math.round(totalA);
								total=p.toString();
							}
							
							if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Package Code -> Expected : 	" + packageCodeExp + "  Actual : " + packageCodeAct);

							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Package Code -> Expected :	" +packageCodeExp + " Actual :	 " + packageCodeAct);

							}
							 List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
							  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
							  db1.put("ExpectedCharges_PackageCharge_Quantity", "PackageDetail_ExpectedCharges_PackageCharge");
							  db1.put("ExpectedCharges_PackageCharge_TotalAmount", "PackageDetail_ExpectedCharges_PackageCharge");
							  db1.put("ExpectedCharges_PackageCharge_Tax", "PackageDetail_ExpectedCharges_PackageCharge");
							  db1.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
							  db1.put("ExpectedCharges_PackageCharge_UnitAmount", "PackageDetail_ExpectedCharges_PackageCharge");
							  db=WSClient.getMultipleNodeList(fetchBookedPackRes,db1,false,XMLType.RESPONSE);
							   
							   List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
							   db2=WSClient.getDBRows(WSClient.getQuery("QS_03"));
						   	   int dates = WSClient.getDBRows(WSClient.getQuery("QS_03")).size();
						   		
							  System.out.println(db2);
							  int i;
							  for(i=0;i<dates;++i)
							  {
								  LinkedHashMap<String,String>  values=db.get(i);
								  
									String quantity=values.get("Quantity1");
								  
								    String unitAmount=values.get("UnitAmount1");
								  String actDate= db2.get(i).get("CONSUMPTION_DATE");
								   
								   
								    String totalAmount=values.get("TotalAmount1");
								    String taxAmount=values.get("Tax1");
								    String startDate=values.get("ValidDatesStart1");
								    int day=i+1;
								    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for day "+day+"</b>");
								    if (WSAssert.assertEquals(actDate,startDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Valid Date -> Expected :		" +actDate
												+ "		 Actual :	 	 " + startDate);
		
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Valid Date -> Expected :		" +actDate
												+ "		 Actual : 		 " + startDate);
		
									}
		
								    if (WSAssert.assertEquals(quantity,quantityDb, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" +quantity
												+ "		 Actual :	 	 " + quantityDb);
		
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" +quantity
												+ "		 Actual : 		 " + quantityDb);
		
									}
		
									
									if(WSAssert.assertEquals(unitPrice,unitAmount, true)){
										WSClient.writeToReport(LogStatus.PASS, "Unit Price : Expected -> "+ unitPrice+" Actual  -> "+unitAmount);
									}
									else
										WSClient.writeToReport(LogStatus.FAIL, "Unit Price : Expected  -> "+ unitPrice+" Actual -> "+unitAmount);
									
									
									
										if(WSAssert.assertEquals(tax,taxAmount, true)){
											WSClient.writeToReport(LogStatus.PASS, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
										}
										else
											WSClient.writeToReport(LogStatus.FAIL, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
									
									if(WSAssert.assertEquals(total,totalAmount, true)){
										WSClient.writeToReport(LogStatus.PASS, "Total  Amount : Expected -> "+ total+" Actual -> "+totalAmount);
									}
									else
										WSClient.writeToReport(LogStatus.FAIL, "Total Amount : Expected -> "+ total+" Actual -> "+totalAmount);
									total2=total2+Double.parseDouble(total);
									totaltax=totaltax+Double.parseDouble(tax);
									totalunit=totalunit+Double.parseDouble(unitPrice);
                                    								
									}
							  
							    String unitAmount=db.get(i).get("UnitAmount1");							   							   
							   
							    String totalAmount=db.get(i).get("TotalAmount1");
							    String taxAmount=db.get(i).get("Tax1");
							    
							      
							      String totalAm=totalunit.toString();
								  String tax3=totaltax.toString();
								  String totalAmm=total2.toString();
								  
									if(totalunit==Math.floor(totalunit)){
										Integer p=(int) Math.round(totalunit);
										totalAm=p.toString();
										System.out.println(totalAm);
									}
									if(totaltax==Math.floor(totaltax) ){
										Integer p=(int) Math.round(totaltax);
										tax3=p.toString();
									}
									if(total2==Math.floor(total2) ){
										Integer p=(int) Math.round(total2);
										totalAmm=p.toString();
									}
									
							  
                              WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
                              
                              if(WSAssert.assertEquals(totalAm,unitAmount, true)){
   							   WSClient.writeToReport(LogStatus.PASS, "Unit Package Amount : Expected -> "+totalAm+" Actual -> "+unitAmount);
   							  }
   							  else
   							   WSClient.writeToReport(LogStatus.FAIL, "Unit Package Amount : Expected -> "+ totalAm+" Actual -> "+unitAmount);
   							  
   								    
							  if(WSAssert.assertEquals(tax3,taxAmount, true)){
							   WSClient.writeToReport(LogStatus.PASS, " Total Tax : Expected -> "+ tax3+" Actual -> "+taxAmount);
							  }
							  else
							   WSClient.writeToReport(LogStatus.FAIL, " Total Tax: Expected -> "+tax3+" Actual -> "+taxAmount);
							  
							  
							  if(WSAssert.assertEquals(totalAmm,totalAmount ,true)){
							   WSClient.writeToReport(LogStatus.PASS, "Total Package Amount : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
							  }
							  else
							   WSClient.writeToReport(LogStatus.FAIL, "Total Package Amount  : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
							  
							  
							  
							  
								   
							  }
						if(WSAssert.assertIfElementExists(fetchBookedPackRes,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackRes,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}
					}
					else
					{
						//added package
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
//	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
//	public void fetchBookedPackages_Ext_2() {
//		try {
//			String testName = "fetchBookedPackages_Ext_2";
//			WSClient.startTest(testName,
//					"Verify that the posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as arrival night and tax is included.",
//					"minimumRegression");
//			if (OperaPropConfig
//					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
//				String interfaceName = HTNGLib.getHTNGInterface();
//				String resortOperaValue = OPERALib.getResort();
//				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
//
//				String chainValue = OPERALib.getChain();
//
//				WSClient.setData("{var_profileSource}", interfaceName);
//
//				WSClient.setData("{var_resort}", resortOperaValue);
//				WSClient.setData("{var_chain}", chainValue);
//				WSClient.setData("{var_extResort}", resortExtValue);
//
//				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
//
//				// *************Prerequisite 1 : Create Profile*************//
//
//				OPERALib.setOperaHeader(OPERALib.getUserName());
//				if (profileID.equals(""))
//					profileID = CreateProfile.createProfile("DS_01");
//				if (!profileID.equals("error")) {
//					WSClient.setData("{var_profileId}", profileID);
//
//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
//
//					// *************Prerequisite 2 : Create Reservation with
//					// rate code which have packages*************//
//
//					HashMap<String, String> resv = CreateReservation.createReservation("DS_27");
//					String resvID = resv.get("reservationId");
//
//					if (!resvID.equals("error")) {
//
//						WSClient.setData("{var_resvId}", resvID);
//						WSClient.setData("{var_resvID}", resvID);
//						String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_07");
//						WSClient.setData("{var_packageCode}","CHOCOLATES");
//						
//
//						String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_08");
//						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
//						
//						//******OWS Fetch Booking*******//
//						  
//						if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
//
//						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
//								HTNGLib.getInterfaceFromAddress());
//						String fetchBookedPackReq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
//						String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackReq);
//						if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
//								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//							
//							Double total2=0.0;
//							Double totaltax=0.0;
//							Double totalunit=0.0;
//							String packageCodeAct=WSClient.getElementValue(fetchBookedPackRes,"BookedPackageList_PackageDetail_PackageInfo_packageCode",XMLType.RESPONSE);
//							System.out.println(packageCodeAct);
//							String packageCodeExp=WSClient.getDBRow(WSClient.getQuery("QS_10")).get("PRODUCT_ID");
//							System.out.println(packageCodeExp);
//							String query = WSClient.getQuery("QS_09");
//							String price = WSClient.getDBRow(query).get("PRICE");
//							query=WSClient.getQuery("QS_10");
//							String quantityDb= WSClient.getDBRow(query).get("QUANTITY");
//							query=WSClient.getQuery("QS_11");
//							String percent= WSClient.getDBRow(query).get("PERCENTAGE");
//							query=WSClient.getQuery("QS_12");
//							String tax,total1;
//							
//							Double totalP=Double.parseDouble(price)*Double.parseDouble(quantityDb);
//							Double a = 0.0;
//							if(percent!=null){
//								Double des=(double) (totalP*Integer.parseInt(percent));
//								System.out.println(des);
//								 a= (double) (des/100.0);
//								tax=String.valueOf(a);
//								System.out.println(tax);
//								
//								}
//								else{
//									tax="0";
//								}
//								
//							System.out.println(tax);
//							Double packageRate=(double) (totalP-Double.parseDouble(tax));
//							System.out.println(packageRate);
//							Double unitRate=packageRate/Integer.parseInt(quantityDb);
//							System.out.println(unitRate);
//							String unitPrice=String.valueOf(unitRate);
//							String totalPrice=String.valueOf(packageRate);
//							Double totalA=Double.parseDouble(totalPrice)+Double.parseDouble(tax);
//							System.out.println(totalA);
//							String total=String.valueOf(totalA);
//							System.out.println(total);
//							
//							if(a==Math.floor(a)){
//								Integer p=(int) Math.round(a);
//								tax=p.toString();
//								System.out.println(tax);
//							}
//							if(unitRate==Math.floor(unitRate) ){
//								Integer p=(int) Math.round(unitRate);
//								unitPrice=p.toString();
//							}
//							if(totalA==Math.floor(totalA) ){
//								Integer p=(int) Math.round(totalA);
//								total=p.toString();
//							}
//							
//							if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
//								WSClient.writeToReport(LogStatus.PASS,
//										"Package Code -> Expected : 	" + packageCodeExp + "  Actual : " + packageCodeAct);
//
//							} else {
//								WSClient.writeToReport(LogStatus.FAIL,
//										"Package Code -> Expected :	" +packageCodeExp + " Actual :	 " + packageCodeAct);
//
//							}
//							 List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
//							  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
//							  db1.put("ExpectedCharges_PackageCharge_Quantity", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("ExpectedCharges_PackageCharge_TotalAmount", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("ExpectedCharges_PackageCharge_Tax", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("ExpectedCharges_PackageCharge_UnitAmount", "PackageDetail_ExpectedCharges_PackageCharge");
//							   db=WSClient.getMultipleNodeList(fetchBookedPackRes,db1,false,XMLType.RESPONSE);
//							  System.out.println(db);
//							  int date=0;
//							int i;
//							  for(i=0;i<db.size()-1;++i)
//							  {
//								  ++date;
//								  LinkedHashMap<String,String>  values=db.get(i);
//								  
//									  String quantity=values.get("Quantity1");
//								  
//								    String unitAmount=values.get("UnitAmount1");
//								   
//								   
//								   
//								    String totalAmount=values.get("TotalAmount1");
//								    String taxAmount=values.get("Tax1");
//								    String startDate=values.get("ValidDatesStart1");
//								    if(date==1){
//								    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
//								    
//								    if (WSAssert.assertEquals(quantity,quantityDb, true)) {
//										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" +quantity
//												+ "		 Actual :	 	 " + quantityDb);
//		
//									} else {
//										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" +quantity
//												+ "		 Actual : 		 " + quantityDb);
//		
//									}
//		
//									
//									if(WSAssert.assertEquals(unitPrice,unitAmount, true)){
//										WSClient.writeToReport(LogStatus.PASS, "Unit Price : Expected -> "+ unitPrice+" Actual  -> "+unitAmount);
//									}
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Unit Price : Expected  -> "+ unitPrice+" Actual -> "+unitAmount);
//									
//									
//									
//										if(WSAssert.assertEquals(tax,taxAmount, true)){
//											WSClient.writeToReport(LogStatus.PASS, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//										}
//										else
//											WSClient.writeToReport(LogStatus.FAIL, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//									
//									if(WSAssert.assertEquals(total,totalAmount, true)){
//										WSClient.writeToReport(LogStatus.PASS, "Total  Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//									}
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Total Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//									
//									total2=total2+Double.parseDouble(total);
//									totaltax=totaltax+Double.parseDouble(tax);
//									totalunit=totalunit+Double.parseDouble(unitPrice);
//								
//									}
//								    else
//								    {     if(startDate==null){
//								    	 WSClient.writeToReport(LogStatus.PASS, "The posting details for "+startDate+" should not be present on the response as the posting rhythm is arrival night");
//								    }
//								    else
//								    {
//								    	WSClient.writeToReport(LogStatus.FAIL,  "The posting details for "+startDate+" should not be present on the response as the posting rhythm is arrival night");
//								    }
//								    }
//							  }
//							  
//							   String unitAmount=db.get(i).get("UnitAmount1");							   							   
//							   
//							    String totalAmount=db.get(i).get("TotalAmount1");
//							    String taxAmount=db.get(i).get("Tax1");
//							    
//							    String totalAm=totalunit.toString();
//								  String tax3=totaltax.toString();
//								  String totalAmm=total2.toString();
//							  
//                             WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
//                             
//                             if(WSAssert.assertEquals(totalAm,unitAmount, true)){
//  							   WSClient.writeToReport(LogStatus.PASS, "Unit Package Amount : Expected -> "+totalAm+" Actual -> "+unitAmount);
//  							  }
//  							  else
//  							   WSClient.writeToReport(LogStatus.FAIL, "Unit Package Amount : Expected -> "+ totalAm+" Actual -> "+unitAmount);
//  							  
//  								    
//							  if(WSAssert.assertEquals(tax3,taxAmount, true)){
//							   WSClient.writeToReport(LogStatus.PASS, " Total Package Amout : Expected -> "+ tax3+" Actual -> "+taxAmount);
//							  }
//							  else
//							   WSClient.writeToReport(LogStatus.FAIL, " Total Package Amount : Expected -> "+tax3+" Actual -> "+taxAmount);
//							  
//							  
//							  if(WSAssert.assertEquals(totalAmm,totalAmount ,true)){
//							   WSClient.writeToReport(LogStatus.PASS, "Total Tax : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//							  }
//							  else
//							   WSClient.writeToReport(LogStatus.FAIL, "Total Tax  : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//							  
//							
//						
//						}
//					}
//					}
//					else
//					{
//						//added package
//					}
//				}
//			}
//		} catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		} finally {
//			try {
//				if(!WSClient.getData("{var_resvId}").equals(""))
//			        CancelReservation.cancelReservation("DS_02");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}
//	
//	@Test(groups = { "minimumRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
//	public void fetchBookedPackages_Ext_3() {
//		try {
//			String testName = "fetchBookedPackages_Ext_3";
//			WSClient.startTest(testName,
//					"Verify that the posting rhythm details are correctly populated for 3 Day Reservation with posting rhythm as every night except last night and tax is included.",
//					"minimumRegression");
//			if (OperaPropConfig
//					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
//				String interfaceName = HTNGLib.getHTNGInterface();
//				String resortOperaValue = OPERALib.getResort();
//				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
//
//				String chainValue = OPERALib.getChain();
//
//				WSClient.setData("{var_profileSource}", interfaceName);
//
//				WSClient.setData("{var_resort}", resortOperaValue);
//				WSClient.setData("{var_chain}", chainValue);
//				WSClient.setData("{var_extResort}", resortExtValue);
//
//				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
//
//				// *************Prerequisite 1 : Create Profile*************//
//
//				OPERALib.setOperaHeader(OPERALib.getUserName());
//				if (profileID.equals(""))
//					profileID = CreateProfile.createProfile("DS_01");
//				if (!profileID.equals("error")) {
//					WSClient.setData("{var_profileId}", profileID);
//
//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
//
//					// *************Prerequisite 2 : Create Reservation with
//					// rate code which have packages*************//
//
//					HashMap<String, String> resv = CreateReservation.createReservation("DS_27");
//					String resvID = resv.get("reservationId");
//
//					if (!resvID.equals("error")) {
//
//						WSClient.setData("{var_resvId}", resvID);
//						String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_06");
//						WSClient.setData("{var_packageCode}",pkg);
//						
//
//						String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_08");
//						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
//						
//						//******OWS Fetch Booking*******//
//						  
//						if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
//
//						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
//								HTNGLib.getInterfaceFromAddress());
//						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
//						String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackreq);
//						if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
//								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//							
//							Double total2=0.0;
//							Double totaltax=0.0;
//							Double totalunit=0.0;
//							String packageCodeAct=WSClient.getElementValue(fetchBookedPackRes,"BookedPackageList_PackageDetail_PackageInfo_packageCode",XMLType.RESPONSE);
//							System.out.println(packageCodeAct);
//							String packageCodeExp=WSClient.getDBRow(WSClient.getQuery("QS_10")).get("PRODUCT_ID");
//							System.out.println(packageCodeExp);
//							String query = WSClient.getQuery("QS_09");
//							String price = WSClient.getDBRow(query).get("PRICE");
//							query=WSClient.getQuery("QS_10");
//							String quantityDb= WSClient.getDBRow(query).get("QUANTITY");
//							query=WSClient.getQuery("QS_11");
//							String percent= WSClient.getDBRow(query).get("PERCENTAGE");
//							query=WSClient.getQuery("QS_12");
//							String tax,total1;
//							
//							Double totalP=Double.parseDouble(price)*Double.parseDouble(quantityDb);
//							Double a = 0.0;
//							if(percent!=null){
//								Double des=(double) (totalP*Integer.parseInt(percent));
//								System.out.println(des);
//								 a= (double) (des/100.0);
//								tax=String.valueOf(a);
//								System.out.println(tax);
//								
//								}
//								else{
//									tax="0";
//								}
//								
//							System.out.println(tax);
//							Double packageRate=(double) (totalP-Double.parseDouble(tax));
//							System.out.println(packageRate);
//							Double unitRate=packageRate/Integer.parseInt(quantityDb);
//							System.out.println(unitRate);
//							String unitPrice=String.valueOf(unitRate);
//							String totalPrice=String.valueOf(packageRate);
//							Double totalA=Double.parseDouble(totalPrice)+Double.parseDouble(tax);
//							System.out.println(totalA);
//							String total=String.valueOf(totalA);
//							System.out.println(total);
//							
//							if(a==Math.floor(a)){
//								Integer p=(int) Math.round(a);
//								tax=p.toString();
//								System.out.println(tax);
//							}
//							if(unitRate==Math.floor(unitRate) ){
//								Integer p=(int) Math.round(unitRate);
//								unitPrice=p.toString();
//							}
//							if(totalA==Math.floor(totalA) ){
//								Integer p=(int) Math.round(totalA);
//								total=p.toString();
//							}
//							if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
//								WSClient.writeToReport(LogStatus.PASS,
//										"Package Code -> Expected : 	" + packageCodeExp + "  Actual : " + packageCodeAct);
//
//							} else {
//								WSClient.writeToReport(LogStatus.FAIL,
//										"Package Code -> Expected :	" +packageCodeExp + " Actual :	 " + packageCodeAct);
//
//							}
//							 List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
//							  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
//							  db1.put("ExpectedCharges_PackageCharge_Quantity", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("ExpectedCharges_PackageCharge_TotalAmount", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("ExpectedCharges_PackageCharge_Tax", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
//							  db1.put("ExpectedCharges_PackageCharge_UnitAmount", "PackageDetail_ExpectedCharges_PackageCharge");
//							   db=WSClient.getMultipleNodeList(fetchBookedPackRes,db1,false,XMLType.RESPONSE);
//							  System.out.println(db);
//							  int date=0;
//							 int duration=db.size();
//							 int i;
//							  for( i=0;i<db.size()-1;++i)
//							  {
//								  ++date;
//								  LinkedHashMap<String,String>  values=db.get(i);
//								  
//									  String quantity=values.get("Quantity1");
//								  
//								    String unitAmount=values.get("UnitAmount1");
//								   
//								   
//								   
//								    String totalAmount=values.get("TotalAmount1");
//								    String taxAmount=values.get("Tax1");
//								    String startDate=values.get("ValidDatesStart1");
//								    if(date<duration){
//								    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
//								    
//								    if (WSAssert.assertEquals(quantity,quantityDb, true)) {
//										WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" +quantity
//												+ "		 Actual :	 	 " + quantityDb);
//		
//									} else {
//										WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" +quantity
//												+ "		 Actual : 		 " + quantityDb);
//		
//									}
//		
//									
//									if(WSAssert.assertEquals(unitPrice,unitAmount, true)){
//										WSClient.writeToReport(LogStatus.PASS, "Unit Price : Expected -> "+ unitPrice+" Actual  -> "+unitAmount);
//									}
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Unit Price : Expected  -> "+ unitPrice+" Actual -> "+unitAmount);
//									
//									
//									
//										if(WSAssert.assertEquals(tax,taxAmount, true)){
//											WSClient.writeToReport(LogStatus.PASS, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//										}
//										else
//											WSClient.writeToReport(LogStatus.FAIL, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//									
//									if(WSAssert.assertEquals(total,totalAmount, true)){
//										WSClient.writeToReport(LogStatus.PASS, "Total  Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//									}
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Total Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//									total2=total2+Double.parseDouble(total);
//									totaltax=totaltax+Double.parseDouble(tax);
//									totalunit=totalunit+Double.parseDouble(unitPrice);
//								
//									}
//								    else
//								    {     if(startDate==null){
//								    	 WSClient.writeToReport(LogStatus.PASS, "The posting details for "+startDate+" should not be present on the response as the posting rhythm is every night except last");
//								    }
//								    else
//								    {
//								    	WSClient.writeToReport(LogStatus.FAIL,  "The posting details for "+startDate+" should not be present on the response as the posting rhythm is every night except last");
//								    }
//								    }
//							  }
//								    
//							   String unitAmount=db.get(i).get("UnitAmount1");							   							   
//							   
//							    String totalAmount=db.get(i).get("TotalAmount1");
//							    String taxAmount=db.get(i).get("Tax1");
//							    
//							    String totalAm=totalunit.toString();
//								  String tax3=totaltax.toString();
//								  String totalAmm=total2.toString();
//							  
//                             WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
//                             
//                             if(WSAssert.assertEquals(totalAm,unitAmount, true)){
//  							   WSClient.writeToReport(LogStatus.PASS, "Unit Package Amount : Expected -> "+totalAm+" Actual -> "+unitAmount);
//  							  }
//  							  else
//  							   WSClient.writeToReport(LogStatus.FAIL, "Unit Package Amount : Expected -> "+ totalAm+" Actual -> "+unitAmount);
//  							  
//  								    
//							  if(WSAssert.assertEquals(tax3,taxAmount, true)){
//							   WSClient.writeToReport(LogStatus.PASS, " Total Package Amout : Expected -> "+ tax3+" Actual -> "+taxAmount);
//							  }
//							  else
//							   WSClient.writeToReport(LogStatus.FAIL, " Total Package Amount : Expected -> "+tax3+" Actual -> "+taxAmount);
//							  
//							  
//							  if(WSAssert.assertEquals(totalAmm,totalAmount ,true)){
//							   WSClient.writeToReport(LogStatus.PASS, "Total Tax : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//							  }
//							  else
//							   WSClient.writeToReport(LogStatus.FAIL, "Total Tax  : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//							  
//						
//
//						}
//					}
//					}
//					else
//					{
//						//added package
//					}
//				}
//			}
//		} catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		} finally {
//			try {
//				if(!WSClient.getData("{var_resvId}").equals(""))
//			        CancelReservation.cancelReservation("DS_02");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}
//	
	
	@Test(groups = { "targetedRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1994() {
		try {
			String testName = "fetchBookedPackages_Ext_1994";
			WSClient.startTest(testName,
					"Verify AmountCurrencyCode, Amount & Allowance for multiple packages should be displayed on the FetchBookedPackagesResponse when ReservationID having package is passed on the FetchBookedPackagesRequest",
					"targetedRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

							db = WSClient.getDBRows(WSClient.getQuery("QS_05"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("BookedPackageList_PackageDetail_PackageInfo_packageCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("PackageDetail_PackageInfo_Amount_currencyCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("PackageDetail_PackageInfo_Amount",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("PackageDetail_PackageInfo_Allowance",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							List<LinkedHashMap<String, String>> actual = WSClient.getMultipleNodeList(fetchBookedPackres, res,	false, XMLType.RESPONSE);

							WSAssert.assertEquals(actual,db, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}

				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	@Test(groups = { "targetedRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1995() {
		try {
			String testName = "fetchBookedPackages_Ext_1995";
			WSClient.startTest(testName,
					" Verify Description and short description of multiple package should be displayed on the response when reservationID is passed on the request",
					"targetedRegression");

			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

							db = WSClient.getDBRows(WSClient.getQuery("QS_04"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("BookedPackageList_PackageDetail_PackageInfo_packageCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("Description_Text_TextElement",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("ShortDescription_Text_TextElement",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							List<LinkedHashMap<String, String>> actual = WSClient.getMultipleNodeList(fetchBookedPackres, res,
									true, XMLType.RESPONSE);
							WSAssert.assertEquals(actual,db, false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}

				}

			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	@Test(groups = { "targetedRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_1997() {
		try {
			String testName = "fetchBookedPackages_Ext_1997";
			WSClient.startTest(testName,
					"Verify Rate Code,Quantity of package attached to a reservation is displayed correctly in fetch booked packages response.",
					"targetedRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

				// *************Prerequisite 1 : Create Profile*************//

				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

							db = WSClient.getDBRows(WSClient.getQuery("QS_14"));

							HashMap<String, String> res = new HashMap<String, String>();
							res.put("ExpectedCharges_PackageCharge_PackageCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("ExpectedCharges_PackageCharge_Quantity",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");
							res.put("ExpectedCharges_PackageCharge_RateCode",
									"FetchBookedPackagesResponse_BookedPackageList_PackageDetail");

							List<LinkedHashMap<String, String>> actual = WSClient.getMultipleNodeList(fetchBookedPackres, res,
									true, XMLType.RESPONSE);

							WSAssert.assertEquals(actual, db,false);

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	@Test(groups = { "fullRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
	public void fetchBookedPackages_Ext_2003() {
		try {
			String testName = "fetchBookedPackages_Ext_2003";
			WSClient.startTest(testName,
					"Verify error message should be displayed when a reservationID with no package assigned is passed on the FetchBookedPackagesRequest. ",
					"fullRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String chainValue = OPERALib.getChain();

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chainValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					// *************Prerequisite 2 : Create Reservation with
					// rate code which have packages*************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
						String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
						if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
								"FetchBookedPackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

						}
						if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
							String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
							}
					}

				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
			        CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


@Test(groups = { "fullRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
public void fetchBookedPackages_Ext_2004() {
	try {
		String testName = "fetchBookedPackages_Ext_2004";
		WSClient.startTest(testName,
				"Verify error message should be displayed when a reservationID is not passed on the FetchBookedPackagesRequest. ",
				"fullRegression");
		if (OperaPropConfig
				.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chainValue = OPERALib.getChain();

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chainValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_02");
					String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
					if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
							"FetchBookedPackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

					}
					if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
						String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
						}
				
			

		}
	} catch (Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	} finally {
		try {
			if(!WSClient.getData("{var_resvId}").equals(""))
		        CancelReservation.cancelReservation("DS_02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

@Test(groups = { "fullRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
public void fetchBookedPackages_Ext_2005() {
	try {
		String testName = "fetchBookedPackages_Ext_2005";
		WSClient.startTest(testName,
				"Verify error message should be displayed when a invalid reservationID is  passed on the FetchBookedPackagesRequest. ",
				"fullRegression");
		if (OperaPropConfig
				.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chainValue = OPERALib.getChain();

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chainValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			    WSClient.setData("{var_resvId}", WSClient.getKeywordData("{KEYWORD_ID}"));
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String fetchBookedPackreq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
					String fetchBookedPackres = WSClient.processSOAPMessage(fetchBookedPackreq);
					if (WSAssert.assertIfElementValueEquals(fetchBookedPackres,
							"FetchBookedPackagesResponse_Result_resultStatusFlag", "FAIL", false)) {

					}
					if(WSAssert.assertIfElementExists(fetchBookedPackres,"Result_Text_TextElement", true)){
						String message=WSClient.getElementValue(fetchBookedPackres,"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
						}
				
			

		}
	} catch (Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	} finally {
		try {
			if(!WSClient.getData("{var_resvId}").equals(""))
		        CancelReservation.cancelReservation("DS_02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

@Test(groups = { "fullRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
public void fetchBookedPackages_Ext_2007() {
	try {
		String testName = "fetchBookedPackages_Ext_2007";
		WSClient.startTest(testName,
				"Verify correct currency code should be displayed in the response when the package with different currency code is attached to a reservation.",
				"fullRegression");
		if (OperaPropConfig
				.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chainValue = OPERALib.getChain();

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chainValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

			// *************Prerequisite 1 : Create Profile*************//

			OPERALib.setOperaHeader(OPERALib.getUserName());
			if (profileID.equals(""))
				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				// *************Prerequisite 2 : Create Reservation with
				// rate code which have packages*************//

				HashMap<String, String> resv = CreateReservation.createReservation("DS_28");
				String resvID = resv.get("reservationId");

				if (!resvID.equals("error")) {

					WSClient.setData("{var_resvId}", resvID);
					WSClient.setData("{var_resvID}", resvID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
					String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_08");
					WSClient.setData("{var_packageCode}",pkg);
					String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_07");
					String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
					
					//******OWS Fetch Booking*******//
					  
					if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
						
                     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully added the package to reservation</b>");
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String fetchBookedPackReq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
					String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackReq);
					if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
							"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String cc=WSClient.getElementValue(fetchBookedPackRes, "PackageDetail_PackageInfo_Amount_currencyCode", XMLType.RESPONSE);
						String db=WSClient.getDBRow(WSClient.getQuery("QS_13")).get("CURRENCY_CODE");
						WSAssert.assertEquals(db,cc,false);
					}
					if(WSAssert.assertIfElementExists(fetchBookedPackRes,"Result_Text_TextElement", true)){
						String message=WSClient.getElementValue(fetchBookedPackRes,"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
						}
				}
				}
				else
				{
					//added package
				}
			}
		}
	} catch (Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	} finally {
		try {
			if(!WSClient.getData("{var_resvId}").equals(""))
		        CancelReservation.cancelReservation("DS_02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


@Test(groups = { "targetedRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
public void fetchBookedPackages_Ext_1999() {
	try {
		String testName = "fetchBookedPackages_Ext_1999";
		WSClient.startTest(testName,
				"verify expected charges as per Per Adult for 3 day reservation are displayed correctly in fetchbookedpackages response.",
				"targetedRegression");
		if (OperaPropConfig
				.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chainValue = OPERALib.getChain();

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chainValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

			// *************Prerequisite 1 : Create Profile*************//

			OPERALib.setOperaHeader(OPERALib.getUserName());
			if (profileID.equals(""))
				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				String adult="3";
				String child="2";
				WSClient.setData("{var_adult}",adult);
				WSClient.setData("{var_child}",child);
				// *************Prerequisite 2 : Create Reservation with
				// rate code which have packages*************//

				HashMap<String, String> resv = CreateReservation.createReservation("DS_30");
				String resvID = resv.get("reservationId");

				if (!resvID.equals("error")) {

					WSClient.setData("{var_resvId}", resvID);
					WSClient.setData("{var_resvID}", resvID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
					String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_09");
					WSClient.setData("{var_packageCode}",pkg);
					String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_08");
					String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
					
					//******OWS Fetch Booking*******//
					  
					if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
						
                     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully added the package to reservation</b>");
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String fetchBookedPackReq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
					String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackReq);
					if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
							"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						Integer person=Integer.parseInt(adult);
						Double total2=0.0;
						Double totaltax=0.0;
						Double totalunit=0.0;
						String packageCodeAct=WSClient.getElementValue(fetchBookedPackRes,"BookedPackageList_PackageDetail_PackageInfo_packageCode",XMLType.RESPONSE);
						System.out.println(packageCodeAct);
						
						String packageCodeExp=WSClient.getDBRow(WSClient.getQuery("QS_10")).get("PRODUCT_ID");
						System.out.println(packageCodeExp);
						String query = WSClient.getQuery("QS_09");
						String price = WSClient.getDBRow(query).get("PRICE");
						query=WSClient.getQuery("QS_10");
						String quantityDb= (Integer.parseInt(WSClient.getDBRow(query).get("QUANTITY"))*person)+"";
						query=WSClient.getQuery("QS_11");
						String percent= WSClient.getDBRow(query).get("PERCENTAGE");
						query=WSClient.getQuery("QS_12");
						String tax;
						
						Double totalP=Double.parseDouble(price)*Double.parseDouble(quantityDb);
						Double a = 0.0;
						if(percent!=null){
							Double des=(double) (totalP*Integer.parseInt(percent));
							System.out.println(des);
							 a= (double) (des/100.0);
							tax=String.valueOf(a);
							System.out.println(tax);
							
							}
							else{
								tax="0.00";
							}
							
						System.out.println(tax);
						Double packageRate=(double) (totalP-Double.parseDouble(tax));
						System.out.println(packageRate);
						Double unitRate=packageRate;
						System.out.println(unitRate);
						String unitPrice=String.valueOf(unitRate);
						String totalPrice=String.valueOf(packageRate);
						Double totalA=Double.parseDouble(totalPrice)+Double.parseDouble(tax);
						System.out.println(totalA);
						String total=String.valueOf(totalA);
						System.out.println(total);
						
//						if(a==Math.floor(a)){
//							Integer p=(int) Math.round(a);
//							tax=p.toString();
//							System.out.println(tax);
//						}
						if(unitRate==Math.floor(unitRate) ){
							Integer p=(int) Math.round(unitRate);
							unitPrice=p.toString();
						}
						if(totalA==Math.floor(totalA) ){
							Integer p=(int) Math.round(totalA);
							total=p.toString();
						}
						
						if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Package Code -> Expected : 	" + packageCodeExp + "  Actual : " + packageCodeAct);

						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Package Code -> Expected :	" +packageCodeExp + " Actual :	 " + packageCodeAct);

						}
						 List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
						  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
						  db1.put("ExpectedCharges_PackageCharge_Quantity", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_TotalAmount", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_Tax", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_UnitAmount", "PackageDetail_ExpectedCharges_PackageCharge");
						  db=WSClient.getMultipleNodeList(fetchBookedPackRes,db1,false,XMLType.RESPONSE);
						   
						   List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
						   db2=WSClient.getDBRows(WSClient.getQuery("QS_03"));
					   	   int dates = WSClient.getDBRows(WSClient.getQuery("QS_03")).size();
					   		
						  System.out.println(db2);
						  int i;
						  for(i=0;i<dates;++i)
						  {
							  LinkedHashMap<String,String>  values=db.get(i);
							  
								String quantity=values.get("Quantity1");
							  
							    String unitAmount=values.get("UnitAmount1");
							  String actDate= db2.get(i).get("CONSUMPTION_DATE");
							   
							   
							    String totalAmount=values.get("TotalAmount1");
							    String taxAmount=values.get("Tax1");
							    String startDate=values.get("ValidDatesStart1");
							    int day=i+1;
							    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for day "+day+"</b>");
							    if (WSAssert.assertEquals(actDate,startDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Valid Date -> Expected :		" +actDate
											+ "		 Actual :	 	 " + startDate);
	
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Valid Date -> Expected :		" +actDate
											+ "		 Actual : 		 " + startDate);
	
								}
	
							    if (WSAssert.assertEquals(quantity,quantityDb, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" +quantityDb
											+ "		 Actual :	 	 " + quantity);
	
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" +quantityDb
											+ "		 Actual : 		 " + quantity);
	
								}
	
								
								if(WSAssert.assertEquals(unitPrice,unitAmount, true)){
									WSClient.writeToReport(LogStatus.PASS, "Unit Price : Expected -> "+ unitPrice+" Actual  -> "+unitAmount);
								}
								else
									WSClient.writeToReport(LogStatus.FAIL, "Unit Price : Expected  -> "+ unitPrice+" Actual -> "+unitAmount);
								
								
								
									if(WSAssert.assertEquals(tax,taxAmount, true)){
										WSClient.writeToReport(LogStatus.PASS, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
									}
									else
										WSClient.writeToReport(LogStatus.FAIL, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
								
								if(WSAssert.assertEquals(total,totalAmount, true)){
									WSClient.writeToReport(LogStatus.PASS, "Total  Amount : Expected -> "+ total+" Actual -> "+totalAmount);
								}
								else
									WSClient.writeToReport(LogStatus.FAIL, "Total Amount : Expected -> "+ total+" Actual -> "+totalAmount);
								total2=total2+Double.parseDouble(total);
								totaltax=totaltax+Double.parseDouble(tax);
								totalunit=totalunit+Double.parseDouble(unitPrice);
                                								
								}
						  
						    String unitAmount=db.get(i).get("UnitAmount1");							   							   
						   
						    String totalAmount=db.get(i).get("TotalAmount1");
						    String taxAmount=db.get(i).get("Tax1");
						    
						      
						      String totalAm=totalunit.toString();
							  String tax3=totaltax.toString();
							  String totalAmm=total2.toString();
							  
								if(totalunit==Math.floor(totalunit)){
									Integer p=(int) Math.round(totalunit);
									totalAm=p.toString();
									System.out.println(totalAm);
								}
								if(totaltax==Math.floor(totaltax) ){
									Integer p=(int) Math.round(totaltax);
									tax3=p.toString();
								}
								if(total2==Math.floor(total2) ){
									Integer p=(int) Math.round(total2);
									totalAmm=p.toString();
								}
								
						  
                          WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
                          
                          if(WSAssert.assertEquals(totalAm,unitAmount, true)){
							   WSClient.writeToReport(LogStatus.PASS, "Unit Package Amount : Expected -> "+totalAm+" Actual -> "+unitAmount);
							  }
							  else
							   WSClient.writeToReport(LogStatus.FAIL, "Unit Package Amount : Expected -> "+ totalAm+" Actual -> "+unitAmount);
							  
								    
						  if(WSAssert.assertEquals(tax3,taxAmount, true)){
						   WSClient.writeToReport(LogStatus.PASS, " Total Tax : Expected -> "+ tax3+" Actual -> "+taxAmount);
						  }
						  else
						   WSClient.writeToReport(LogStatus.FAIL, " Total Tax : Expected -> "+tax3+" Actual -> "+taxAmount);
						  
						  
						  if(WSAssert.assertEquals(totalAmm,totalAmount ,true)){
						   WSClient.writeToReport(LogStatus.PASS, "Total Package Amount : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
						  }
						  else
						   WSClient.writeToReport(LogStatus.FAIL, "Total Package Amount  : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
						    
						  
							   
						  }
					if(WSAssert.assertIfElementExists(fetchBookedPackRes,"Result_Text_TextElement", true)){
						String message=WSClient.getElementValue(fetchBookedPackRes,"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
						}
				}
				}
				else
				{
					//added package
				}
			}
		}
	} catch (Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	} finally {
		try {
			if(!WSClient.getData("{var_resvId}").equals(""))
		        CancelReservation.cancelReservation("DS_02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

@Test(groups = { "targetedRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
public void fetchBookedPackages_Ext_2000() {
	try {
		String testName = "fetchBookedPackages_Ext_2000";
		WSClient.startTest(testName,
				"verify expected charges as per Per Child for 3 day reservation are displayed correctly in fetchbookedpackages response.",
				"targetedRegression");
		if (OperaPropConfig
				.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chainValue = OPERALib.getChain();

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chainValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

			// *************Prerequisite 1 : Create Profile*************//

			OPERALib.setOperaHeader(OPERALib.getUserName());
			if (profileID.equals(""))
				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				String adult="3";
				String child="2";
				WSClient.setData("{var_adult}",adult);
				WSClient.setData("{var_child}",child);
				// *************Prerequisite 2 : Create Reservation with
				// rate code which have packages*************//

				HashMap<String, String> resv = CreateReservation.createReservation("DS_30");
				String resvID = resv.get("reservationId");

				if (!resvID.equals("error")) {

					WSClient.setData("{var_resvId}", resvID);
					WSClient.setData("{var_resvID}", resvID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
					String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_10");
					WSClient.setData("{var_packageCode}",pkg);
					String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_08");
					String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
					
					//******OWS Fetch Booking*******//
					  
					if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
						
                     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully added the package to reservation</b>");
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String fetchBookedPackReq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
					String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackReq);
					if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
							"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						Integer person=Integer.parseInt(child);
						Double total2=0.0;
						Double totaltax=0.0;
						Double totalunit=0.0;
						String packageCodeAct=WSClient.getElementValue(fetchBookedPackRes,"BookedPackageList_PackageDetail_PackageInfo_packageCode",XMLType.RESPONSE);
						System.out.println(packageCodeAct);
						
						String packageCodeExp=WSClient.getDBRow(WSClient.getQuery("QS_10")).get("PRODUCT_ID");
						System.out.println(packageCodeExp);
						String query = WSClient.getQuery("QS_09");
						String price = WSClient.getDBRow(query).get("PRICE");
						query=WSClient.getQuery("QS_10");
						String quantityDb= (Integer.parseInt(WSClient.getDBRow(query).get("QUANTITY"))*person)+"";
						query=WSClient.getQuery("QS_11");
						String percent= WSClient.getDBRow(query).get("PERCENTAGE");
						query=WSClient.getQuery("QS_12");
						String tax;
						
						Double totalP=Double.parseDouble(price)*Double.parseDouble(quantityDb);
						Double a = 0.0;
						if(percent!=null){
							Double des=(double) (totalP*Integer.parseInt(percent));
							System.out.println(des);
							 a= (double) (des/100.0);
							tax=String.valueOf(a);
							System.out.println(tax);
							
							}
							else{
								tax="0.00";
							}
							
						System.out.println(tax);
						Double packageRate=(double) (totalP-Double.parseDouble(tax));
						System.out.println(packageRate);
						Double unitRate=packageRate;
						System.out.println(unitRate);
						String unitPrice=String.valueOf(unitRate);
						String totalPrice=String.valueOf(packageRate);
						Double totalA=Double.parseDouble(totalPrice)+Double.parseDouble(tax);
						System.out.println(totalA);
						String total=String.valueOf(totalA);
						System.out.println(total);
						
//						if(a==Math.floor(a)){
//							Integer p=(int) Math.round(a);
//							tax=p.toString();
//							System.out.println(tax);
//						}
						if(unitRate==Math.floor(unitRate) ){
							Integer p=(int) Math.round(unitRate);
							unitPrice=p.toString();
						}
						if(totalA==Math.floor(totalA) ){
							Integer p=(int) Math.round(totalA);
							total=p.toString();
						}
						
						if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Package Code -> Expected : 	" + packageCodeExp + "  Actual : " + packageCodeAct);

						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Package Code -> Expected :	" +packageCodeExp + " Actual :	 " + packageCodeAct);

						}
						 List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
						  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
						  db1.put("ExpectedCharges_PackageCharge_Quantity", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_TotalAmount", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_Tax", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_UnitAmount", "PackageDetail_ExpectedCharges_PackageCharge");
						  db=WSClient.getMultipleNodeList(fetchBookedPackRes,db1,false,XMLType.RESPONSE);
						   
						   List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
						   db2=WSClient.getDBRows(WSClient.getQuery("QS_03"));
					   	   int dates = WSClient.getDBRows(WSClient.getQuery("QS_03")).size();
					   		
						  System.out.println(db2);
						  int i;
						  for(i=0;i<dates;++i)
						  {
							  LinkedHashMap<String,String>  values=db.get(i);
							  
								String quantity=values.get("Quantity1");
							  
							    String unitAmount=values.get("UnitAmount1");
							  String actDate= db2.get(i).get("CONSUMPTION_DATE");
							   
							   
							    String totalAmount=values.get("TotalAmount1");
							    String taxAmount=values.get("Tax1");
							    String startDate=values.get("ValidDatesStart1");
							    int day=i+1;
							    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for day "+day+"</b>");
							    if (WSAssert.assertEquals(actDate,startDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Valid Date -> Expected :		" +actDate
											+ "		 Actual :	 	 " + startDate);
	
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Valid Date -> Expected :		" +actDate
											+ "		 Actual : 		 " + startDate);
	
								}
	
							    if (WSAssert.assertEquals(quantity,quantityDb, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" +quantityDb
											+ "		 Actual :	 	 " + quantity);
	
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" +quantityDb
											+ "		 Actual : 		 " + quantity);
	
								}
	
//								
//								if(WSAssert.assertEquals(unitPrice,unitAmount, true)){
//									WSClient.writeToReport(LogStatus.PASS, "Unit Price : Expected -> "+ unitPrice+" Actual  -> "+unitAmount);
//								}
//								else
//									WSClient.writeToReport(LogStatus.FAIL, "Unit Price : Expected  -> "+ unitPrice+" Actual -> "+unitAmount);
//								
//								
//								
//									if(WSAssert.assertEquals(tax,taxAmount, true)){
//										WSClient.writeToReport(LogStatus.PASS, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//									}
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//								
//								if(WSAssert.assertEquals(total,totalAmount, true)){
//									WSClient.writeToReport(LogStatus.PASS, "Total  Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//								}
//								else
//									WSClient.writeToReport(LogStatus.FAIL, "Total Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//								total2=total2+Double.parseDouble(total);
//								totaltax=totaltax+Double.parseDouble(tax);
//								totalunit=totalunit+Double.parseDouble(unitPrice);
//                                								
								}
						  
//						    String unitAmount=db.get(i).get("UnitAmount1");							   							   
//						   
//						    String totalAmount=db.get(i).get("TotalAmount1");
//						    String taxAmount=db.get(i).get("Tax1");
//						    
//						      
//						      String totalAm=totalunit.toString();
//							  String tax3=totaltax.toString();
//							  String totalAmm=total2.toString();
//							  
//								if(totalunit==Math.floor(totalunit)){
//									Integer p=(int) Math.round(totalunit);
//									totalAm=p.toString();
//									System.out.println(totalAm);
//								}
//								if(totaltax==Math.floor(totaltax) ){
//									Integer p=(int) Math.round(totaltax);
//									tax3=p.toString();
//								}
//								if(total2==Math.floor(total2) ){
//									Integer p=(int) Math.round(total2);
//									totalAmm=p.toString();
//								}
//								
//						  
//                          WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
//                          
//                          if(WSAssert.assertEquals(totalAm,unitAmount, true)){
//							   WSClient.writeToReport(LogStatus.PASS, "Unit Package Amount : Expected -> "+totalAm+" Actual -> "+unitAmount);
//							  }
//							  else
//							   WSClient.writeToReport(LogStatus.FAIL, "Unit Package Amount : Expected -> "+ totalAm+" Actual -> "+unitAmount);
//							  
//								    
//						  if(WSAssert.assertEquals(tax3,taxAmount, true)){
//						   WSClient.writeToReport(LogStatus.PASS, " Total Tax : Expected -> "+ tax3+" Actual -> "+taxAmount);
//						  }
//						  else
//						   WSClient.writeToReport(LogStatus.FAIL, " Total Tax : Expected -> "+tax3+" Actual -> "+taxAmount);
//						  
//						  
//						  if(WSAssert.assertEquals(totalAmm,totalAmount ,true)){
//						   WSClient.writeToReport(LogStatus.PASS, "Total Package Amount : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//						  }
//						  else
//						   WSClient.writeToReport(LogStatus.FAIL, "Total Package Amount  : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//						    
						  
							   
						  }
					if(WSAssert.assertIfElementExists(fetchBookedPackRes,"Result_Text_TextElement", true)){
						String message=WSClient.getElementValue(fetchBookedPackRes,"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
						}
				}
				}
				else
				{
					//added package
				}
			}
		}
	} catch (Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	} finally {
		try {
			if(!WSClient.getData("{var_resvId}").equals(""))
		        CancelReservation.cancelReservation("DS_02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


@Test(groups = { "targetedRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
public void fetchBookedPackages_Ext_2001() {
	try {
		String testName = "fetchBookedPackages_Ext_2001";
		WSClient.startTest(testName,
				"verify expected charges as per Per Person for 3 day reservation are displayed correctly in fetchbookedpackages response.",
				"targetedRegression");
		if (OperaPropConfig
				.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chainValue = OPERALib.getChain();

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chainValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));

			// *************Prerequisite 1 : Create Profile*************//

			OPERALib.setOperaHeader(OPERALib.getUserName());
			if (profileID.equals(""))
				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				String adult="3";
				String child="2";
				WSClient.setData("{var_adult}",adult);
				WSClient.setData("{var_child}",child);
				// *************Prerequisite 2 : Create Reservation with
				// rate code which have packages*************//

				HashMap<String, String> resv = CreateReservation.createReservation("DS_30");
				String resvID = resv.get("reservationId");

				if (!resvID.equals("error")) {

					WSClient.setData("{var_resvId}", resvID);
					WSClient.setData("{var_resvID}", resvID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
					String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_11");
					WSClient.setData("{var_packageCode}",pkg);
					String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_08");
					String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
					
					//******OWS Fetch Booking*******//
					  
					if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
						
                     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully added the package to reservation</b>");
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String fetchBookedPackReq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
					String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackReq);
					if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
							"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						Integer person=Integer.parseInt(child)+Integer.parseInt(adult);
						Double total2=0.0;
						Double totaltax=0.0;
						Double totalunit=0.0;
						String packageCodeAct=WSClient.getElementValue(fetchBookedPackRes,"BookedPackageList_PackageDetail_PackageInfo_packageCode",XMLType.RESPONSE);
						System.out.println(packageCodeAct);
						
						String packageCodeExp=WSClient.getDBRow(WSClient.getQuery("QS_10")).get("PRODUCT_ID");
						System.out.println(packageCodeExp);
						String query = WSClient.getQuery("QS_09");
						String price = WSClient.getDBRow(query).get("PRICE");
						query=WSClient.getQuery("QS_10");
						String quantityDb= (Integer.parseInt(WSClient.getDBRow(query).get("QUANTITY"))*person)+"";
						query=WSClient.getQuery("QS_11");
						String percent= WSClient.getDBRow(query).get("PERCENTAGE");
						query=WSClient.getQuery("QS_12");
						String tax;
						
						Double totalP=Double.parseDouble(price)*Double.parseDouble(quantityDb);
						Double a = 0.0;
						if(percent!=null){
							Double des=(double) (totalP*Integer.parseInt(percent));
							System.out.println(des);
							 a= (double) (des/100.0);
							tax=String.valueOf(a);
							System.out.println(tax);
							
							}
							else{
								tax="0.00";
							}
							
						System.out.println(tax);
						Double packageRate=(double) (totalP-Double.parseDouble(tax));
						System.out.println(packageRate);
						Double unitRate=packageRate;
						System.out.println(unitRate);
						String unitPrice=String.valueOf(unitRate);
						String totalPrice=String.valueOf(packageRate);
						Double totalA=Double.parseDouble(totalPrice)+Double.parseDouble(tax);
						System.out.println(totalA);
						String total=String.valueOf(totalA);
						System.out.println(total);
						
//						if(a==Math.floor(a)){
//							Integer p=(int) Math.round(a);
//							tax=p.toString();
//							System.out.println(tax);
//						}
						if(unitRate==Math.floor(unitRate) ){
							Integer p=(int) Math.round(unitRate);
							unitPrice=p.toString();
						}
						if(totalA==Math.floor(totalA) ){
							Integer p=(int) Math.round(totalA);
							total=p.toString();
						}
						
						if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Package Code -> Expected : 	" + packageCodeExp + "  Actual : " + packageCodeAct);

						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Package Code -> Expected :	" +packageCodeExp + " Actual :	 " + packageCodeAct);

						}
						 List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
						  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
						  db1.put("ExpectedCharges_PackageCharge_Quantity", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_TotalAmount", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_Tax", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
						  db1.put("ExpectedCharges_PackageCharge_UnitAmount", "PackageDetail_ExpectedCharges_PackageCharge");
						  db=WSClient.getMultipleNodeList(fetchBookedPackRes,db1,false,XMLType.RESPONSE);
						   
						   List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
						   db2=WSClient.getDBRows(WSClient.getQuery("QS_03"));
					   	   int dates = WSClient.getDBRows(WSClient.getQuery("QS_03")).size();
					   		
						  System.out.println(db2);
						  int i;
						  for(i=0;i<dates;++i)
						  {
							  LinkedHashMap<String,String>  values=db.get(i);
							  
								String quantity=values.get("Quantity1");
							  
							    String unitAmount=values.get("UnitAmount1");
							  String actDate= db2.get(i).get("CONSUMPTION_DATE");
							   
							   
							    String totalAmount=values.get("TotalAmount1");
							    String taxAmount=values.get("Tax1");
							    String startDate=values.get("ValidDatesStart1");
							    int day=i+1;
							    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for day "+day+"</b>");
							    if (WSAssert.assertEquals(actDate,startDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Valid Date -> Expected :		" +actDate
											+ "		 Actual :	 	 " + startDate);
	
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Valid Date -> Expected :		" +actDate
											+ "		 Actual : 		 " + startDate);
	
								}
	
							    if (WSAssert.assertEquals(quantity,quantityDb, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" +quantityDb
											+ "		 Actual :	 	 " + quantity);
	
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" +quantityDb
											+ "		 Actual : 		 " + quantity);
	
								}
	
								
								if(WSAssert.assertEquals(unitPrice,unitAmount, true)){
									WSClient.writeToReport(LogStatus.PASS, "Unit Price : Expected -> "+ unitPrice+" Actual  -> "+unitAmount);
								}
								else
									WSClient.writeToReport(LogStatus.FAIL, "Unit Price : Expected  -> "+ unitPrice+" Actual -> "+unitAmount);
								
								
								
									if(WSAssert.assertEquals(tax,taxAmount, true)){
										WSClient.writeToReport(LogStatus.PASS, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
									}
									else
										WSClient.writeToReport(LogStatus.FAIL, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
								
								if(WSAssert.assertEquals(total,totalAmount, true)){
									WSClient.writeToReport(LogStatus.PASS, "Total  Amount : Expected -> "+ total+" Actual -> "+totalAmount);
								}
								else
									WSClient.writeToReport(LogStatus.FAIL, "Total Amount : Expected -> "+ total+" Actual -> "+totalAmount);
								total2=total2+Double.parseDouble(total);
								totaltax=totaltax+Double.parseDouble(tax);
								totalunit=totalunit+Double.parseDouble(unitPrice);
                                								
								}
						  
						    String unitAmount=db.get(i).get("UnitAmount1");							   							   
						   
						    String totalAmount=db.get(i).get("TotalAmount1");
						    String taxAmount=db.get(i).get("Tax1");
						    
						      
						      String totalAm=totalunit.toString();
							  String tax3=totaltax.toString();
							  String totalAmm=total2.toString();
							  
								if(totalunit==Math.floor(totalunit)){
									Integer p=(int) Math.round(totalunit);
									totalAm=p.toString();
									System.out.println(totalAm);
								}
								if(totaltax==Math.floor(totaltax) ){
									Integer p=(int) Math.round(totaltax);
									tax3=p.toString();
								}
								if(total2==Math.floor(total2) ){
									Integer p=(int) Math.round(total2);
									totalAmm=p.toString();
								}
								
						  
                          WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
                          
                          if(WSAssert.assertEquals(totalAm,unitAmount, true)){
							   WSClient.writeToReport(LogStatus.PASS, "Unit Package Amount : Expected -> "+totalAm+" Actual -> "+unitAmount);
							  }
							  else
							   WSClient.writeToReport(LogStatus.FAIL, "Unit Package Amount : Expected -> "+ totalAm+" Actual -> "+unitAmount);
							  
								    
						  if(WSAssert.assertEquals(tax3,taxAmount, true)){
						   WSClient.writeToReport(LogStatus.PASS, " Total Tax : Expected -> "+ tax3+" Actual -> "+taxAmount);
						  }
						  else
						   WSClient.writeToReport(LogStatus.FAIL, " Total Tax : Expected -> "+tax3+" Actual -> "+taxAmount);
						  
						  
						  if(WSAssert.assertEquals(totalAmm,totalAmount ,true)){
						   WSClient.writeToReport(LogStatus.PASS, "Total Package Amount : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
						  }
						  else
						   WSClient.writeToReport(LogStatus.FAIL, "Total Package Amount  : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
						    
						  
							   
						  }
					if(WSAssert.assertIfElementExists(fetchBookedPackRes,"Result_Text_TextElement", true)){
						String message=WSClient.getElementValue(fetchBookedPackRes,"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
						}
				}
				}
				else
				{
					//added package
				}
			}
		}
	} catch (Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	} finally {
		try {
			if(!WSClient.getData("{var_resvId}").equals(""))
		        CancelReservation.cancelReservation("DS_02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

//@Test(groups = { "targetedRegression", "FetchBookedPackages", "HTNG2008BExt", "HTNG" })
//public void fetchBookedPackages_Ext_2002() {
//	try {
//		String testName = "fetchBookedPackages_Ext_2002";
//		WSClient.startTest(testName,
//				"verify expected charges as per Per Room for 3 day reservation are displayed correctly in fetchbookedpackages response.",
//				"targetedRegression");
//		if (OperaPropConfig
//				.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
//			String interfaceName = HTNGLib.getHTNGInterface();
//			String resortOperaValue = OPERALib.getResort();
//			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
//
//			String chainValue = OPERALib.getChain();
//
//			WSClient.setData("{var_profileSource}", interfaceName);
//
//			WSClient.setData("{var_resort}", resortOperaValue);
//			WSClient.setData("{var_chain}", chainValue);
//			WSClient.setData("{var_extResort}", resortExtValue);
//
//			// WSClient.setData("{var_currencyCode}",HTNGLib.getRandomPMSValue(resortOperaValue,interfaceName,"CURRENCY_CODE"));
//
//			// *************Prerequisite 1 : Create Profile*************//
//
//			OPERALib.setOperaHeader(OPERALib.getUserName());
//			if (profileID.equals(""))
//				profileID = CreateProfile.createProfile("DS_01");
//			if (!profileID.equals("error")) {
//				WSClient.setData("{var_profileId}", profileID);
//				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
//				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
//				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
//				String adult="3";
//				String child="2";
//				WSClient.setData("{var_adult}",adult);
//				WSClient.setData("{var_child}",child);
//				String room="2";
//				WSClient.setData("{var_room}",room);
//				// *************Prerequisite 2 : Create Reservation with
//				// rate code which have packages*************//
//
//				HashMap<String, String> resv = CreateReservation.createReservation("DS_31");
//				String resvID = resv.get("reservationId");
//
//				if (!resvID.equals("error")) {
//
//					WSClient.setData("{var_resvId}", resvID);
//					WSClient.setData("{var_resvID}", resvID);
//					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID+"</b>");
//					String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_12");
//					WSClient.setData("{var_packageCode}",pkg);
//					String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_08");
//					String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
//					
//					//******OWS Fetch Booking*******//
//					  
//					if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
//						
//                     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully added the package to reservation</b>");
//					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
//							HTNGLib.getInterfaceFromAddress());
//					String fetchBookedPackReq = WSClient.createSOAPMessage("HTNGExtFetchBookedPackages", "DS_01");
//					String fetchBookedPackRes = WSClient.processSOAPMessage(fetchBookedPackReq);
//					if (WSAssert.assertIfElementValueEquals(fetchBookedPackRes,
//							"FetchBookedPackagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//						Integer person=Integer.parseInt(room);
//						Double total2=0.0;
//						Double totaltax=0.0;
//						Double totalunit=0.0;
//						String packageCodeAct=WSClient.getElementValue(fetchBookedPackRes,"BookedPackageList_PackageDetail_PackageInfo_packageCode",XMLType.RESPONSE);
//						System.out.println(packageCodeAct);
//						
//						String packageCodeExp=WSClient.getDBRow(WSClient.getQuery("QS_10")).get("PRODUCT_ID");
//						System.out.println(packageCodeExp);
//						String query = WSClient.getQuery("QS_09");
//						String price = WSClient.getDBRow(query).get("PRICE");
//						query=WSClient.getQuery("QS_10");
//						String quantityDb= (Integer.parseInt(WSClient.getDBRow(query).get("QUANTITY"))*person)+"";
//						query=WSClient.getQuery("QS_11");
//						String percent= WSClient.getDBRow(query).get("PERCENTAGE");
//						query=WSClient.getQuery("QS_12");
//						String tax;
//						
//						Double totalP=Double.parseDouble(price)*Double.parseDouble(quantityDb);
//						Double a = 0.0;
//						if(percent!=null){
//							Double des=(double) (totalP*Integer.parseInt(percent));
//							System.out.println(des);
//							 a= (double) (des/100.0);
//							tax=String.valueOf(a);
//							System.out.println(tax);
//							
//							}
//							else{
//								tax="0.00";
//							}
//							
//						System.out.println(tax);
//						Double packageRate=(double) (totalP-Double.parseDouble(tax));
//						System.out.println(packageRate);
//						Double unitRate=packageRate;
//						System.out.println(unitRate);
//						String unitPrice=String.valueOf(unitRate);
//						String totalPrice=String.valueOf(packageRate);
//						Double totalA=Double.parseDouble(totalPrice)+Double.parseDouble(tax);
//						System.out.println(totalA);
//						String total=String.valueOf(totalA);
//						System.out.println(total);
//						
////						if(a==Math.floor(a)){
////							Integer p=(int) Math.round(a);
////							tax=p.toString();
////							System.out.println(tax);
////						}
//						if(unitRate==Math.floor(unitRate) ){
//							Integer p=(int) Math.round(unitRate);
//							unitPrice=p.toString();
//						}
//						if(totalA==Math.floor(totalA) ){
//							Integer p=(int) Math.round(totalA);
//							total=p.toString();
//						}
//						
//						if (WSAssert.assertEquals(packageCodeExp, packageCodeAct, true)) {
//							WSClient.writeToReport(LogStatus.PASS,
//									"Package Code -> Expected : 	" + packageCodeExp + "  Actual : " + packageCodeAct);
//
//						} else {
//							WSClient.writeToReport(LogStatus.FAIL,
//									"Package Code -> Expected :	" +packageCodeExp + " Actual :	 " + packageCodeAct);
//
//						}
//						 List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
//						  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
//						  db1.put("ExpectedCharges_PackageCharge_Quantity", "PackageDetail_ExpectedCharges_PackageCharge");
//						  db1.put("ExpectedCharges_PackageCharge_TotalAmount", "PackageDetail_ExpectedCharges_PackageCharge");
//						  db1.put("ExpectedCharges_PackageCharge_Tax", "PackageDetail_ExpectedCharges_PackageCharge");
//						  db1.put("PackageCharge_ValidDates_Start", "PackageDetail_ExpectedCharges_PackageCharge");
//						  db1.put("ExpectedCharges_PackageCharge_UnitAmount", "PackageDetail_ExpectedCharges_PackageCharge");
//						  db=WSClient.getMultipleNodeList(fetchBookedPackRes,db1,false,XMLType.RESPONSE);
//						   
//						   List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
//						   db2=WSClient.getDBRows(WSClient.getQuery("QS_03"));
//					   	   int dates = WSClient.getDBRows(WSClient.getQuery("QS_03")).size();
//					   		
//						  System.out.println(db2);
//						  int i;
//						  for(i=0;i<dates;++i)
//						  {
//							  LinkedHashMap<String,String>  values=db.get(i);
//							  
//								String quantity=values.get("Quantity1");
//							  
//							    String unitAmount=values.get("UnitAmount1");
//							  String actDate= db2.get(i).get("CONSUMPTION_DATE");
//							   
//							   
//							    String totalAmount=values.get("TotalAmount1");
//							    String taxAmount=values.get("Tax1");
//							    String startDate=values.get("ValidDatesStart1");
//							    int day=i+1;
//							    WSClient.writeToReport(LogStatus.INFO, "<b>Validating for day "+day+"</b>");
//							    if (WSAssert.assertEquals(actDate,startDate, true)) {
//									WSClient.writeToReport(LogStatus.PASS, "Valid Date -> Expected :		" +actDate
//											+ "		 Actual :	 	 " + startDate);
//	
//								} else {
//									WSClient.writeToReport(LogStatus.FAIL, "Valid Date -> Expected :		" +actDate
//											+ "		 Actual : 		 " + startDate);
//	
//								}
//	
//							    if (WSAssert.assertEquals(quantity,quantityDb, true)) {
//									WSClient.writeToReport(LogStatus.PASS, "Quantity -> Expected :		" +quantityDb
//											+ "		 Actual :	 	 " + quantity);
//	
//								} else {
//									WSClient.writeToReport(LogStatus.FAIL, "Quantity -> Expected :		" +quantityDb
//											+ "		 Actual : 		 " + quantity);
//	
//								}
//	
//								
//								if(WSAssert.assertEquals(unitPrice,unitAmount, true)){
//									WSClient.writeToReport(LogStatus.PASS, "Unit Price : Expected -> "+ unitPrice+" Actual  -> "+unitAmount);
//								}
//								else
//									WSClient.writeToReport(LogStatus.FAIL, "Unit Price : Expected  -> "+ unitPrice+" Actual -> "+unitAmount);
//								
//								
//								
//									if(WSAssert.assertEquals(tax,taxAmount, true)){
//										WSClient.writeToReport(LogStatus.PASS, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//									}
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Tax : Expected -> "+ tax+" Actual -> "+taxAmount);
//								
//								if(WSAssert.assertEquals(total,totalAmount, true)){
//									WSClient.writeToReport(LogStatus.PASS, "Total  Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//								}
//								else
//									WSClient.writeToReport(LogStatus.FAIL, "Total Amount : Expected -> "+ total+" Actual -> "+totalAmount);
//								total2=total2+Double.parseDouble(total);
//								totaltax=totaltax+Double.parseDouble(tax);
//								totalunit=totalunit+Double.parseDouble(unitPrice);
//                                								
//								}
//						  
//						    String unitAmount=db.get(i).get("UnitAmount1");							   							   
//						   
//						    String totalAmount=db.get(i).get("TotalAmount1");
//						    String taxAmount=db.get(i).get("Tax1");
//						    
//						      
//						      String totalAm=totalunit.toString();
//							  String tax3=totaltax.toString();
//							  String totalAmm=total2.toString();
//							  
//								if(totalunit==Math.floor(totalunit)){
//									Integer p=(int) Math.round(totalunit);
//									totalAm=p.toString();
//									System.out.println(totalAm);
//								}
//								if(totaltax==Math.floor(totaltax) ){
//									Integer p=(int) Math.round(totaltax);
//									tax3=p.toString();
//								}
//								if(total2==Math.floor(total2) ){
//									Integer p=(int) Math.round(total2);
//									totalAmm=p.toString();
//								}
//								
//						  
//                          WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
//                          
//                          if(WSAssert.assertEquals(totalAm,unitAmount, true)){
//							   WSClient.writeToReport(LogStatus.PASS, "Unit Package Amount : Expected -> "+totalAm+" Actual -> "+unitAmount);
//							  }
//							  else
//							   WSClient.writeToReport(LogStatus.FAIL, "Unit Package Amount : Expected -> "+ totalAm+" Actual -> "+unitAmount);
//							  
//								    
//						  if(WSAssert.assertEquals(tax3,taxAmount, true)){
//						   WSClient.writeToReport(LogStatus.PASS, " Total Tax : Expected -> "+ tax3+" Actual -> "+taxAmount);
//						  }
//						  else
//						   WSClient.writeToReport(LogStatus.FAIL, " Total Tax : Expected -> "+tax3+" Actual -> "+taxAmount);
//						  
//						  
//						  if(WSAssert.assertEquals(totalAmm,totalAmount ,true)){
//						   WSClient.writeToReport(LogStatus.PASS, "Total Package Amount : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//						  }
//						  else
//						   WSClient.writeToReport(LogStatus.FAIL, "Total Package Amount  : Expected -> "+ totalAmm+" Actual -> "+totalAmount);
//						    
//						  
//							   
//						  }
//					if(WSAssert.assertIfElementExists(fetchBookedPackRes,"Result_Text_TextElement", true)){
//						String message=WSClient.getElementValue(fetchBookedPackRes,"Result_Text_TextElement", XMLType.RESPONSE);
//						WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is    :     " + message+"</b>");
//						}
//				}
//				}
//				else
//				{
//					//added package
//				}
//			}
//		}
//	} catch (Exception e) {
//		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//	} finally {
//		try {
//			if(!WSClient.getData("{var_resvId}").equals(""))
//		        CancelReservation.cancelReservation("DS_02");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
}

