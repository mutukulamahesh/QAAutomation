package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchProductItems extends WSSetUp {

	@Test(groups = { "sanity", "FetchProductItems", "HTNG2008BExt", "HTNG" })
	public void fetchProductItems_Ext_4534() {
		try {

			String testName = "fetchProductItems_Ext_4534";
			WSClient.startTest(testName, "Verify that the Package Groups configured for a Product Group associated to the requested Resort and Time span are correctly returned by the FetchProductItems call ", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "PackageGroup", "PackageCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// fetching opera Resort and external Resort values

				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				System.out.println(WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
				WSClient.setData("{var_businessDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

				// Fetching the Product Items
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchProductItemsReq = WSClient.createSOAPMessage("HTNGExtFetchProductItems", "DS_01");
				String fetchProductItemsRes = WSClient.processSOAPMessage(fetchProductItemsReq);

				// validation of Response
				if (WSAssert.assertIfElementValueEquals(fetchProductItemsRes, "FetchProductItemsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					HashMap<String, String> xpaths = new HashMap<>();
					ArrayList<LinkedHashMap<String, String>> resGroupCodesandDes = new ArrayList<>();
					ArrayList<LinkedHashMap<String, String>> dbGroupCodesandDes = new ArrayList<>();

					ArrayList<LinkedHashMap<String, String>> resGroupPackageCodes = new ArrayList<>();
					ArrayList<LinkedHashMap<String, String>> dbGroupPackageCodes = new ArrayList<>();

					LinkedHashMap<String, String> dbGrpPackageCode = new LinkedHashMap<>();

					// Verifying the Group Codes and Descriptions are Correctly
					// populated
					WSClient.writeToReport(LogStatus.INFO, "Verifying the Package Groups and their Description");

					// xpaths for Group codes and Description
					xpaths.put("FetchProductItemsResponse_ProductGroups_PackageGroup_groupCode", "FetchProductItemsResponse_ProductGroups_PackageGroup");
					xpaths.put("Description_Text_TextElement", "FetchProductItemsResponse_ProductGroups_PackageGroup");

					// Fetching the values from Response and DB
					resGroupCodesandDes = (ArrayList<LinkedHashMap<String, String>>) WSClient.getMultipleNodeList(fetchProductItemsRes, xpaths, false, XMLType.RESPONSE);
					System.out.println(resGroupCodesandDes);
					String query1 = WSClient.getQuery("QS_01");
					dbGroupCodesandDes = WSClient.getDBRows(query1);
					System.out.println(dbGroupCodesandDes);

					// Verifying the fetched values
					if (WSAssert.assertEquals(resGroupCodesandDes, dbGroupCodesandDes, false))
						WSClient.writeToReport(LogStatus.INFO, "<b>Package Groups and their Descriptions are Properly Fetched</b>");

					// Verifying the Package Codes Inside the Package Groups in
					// DS_01 // QA_BANQUET_GRP
					String packageGroup = OperaPropConfig.getDataSetForCode("PackageGroup", "DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>Verifying the package Codes inside: '" + packageGroup + "'</b>");
					xpaths.clear();
					WSClient.setData("{var_packageCode}", packageGroup);
					int index = WSClient.getNodeIndex(fetchProductItemsRes, packageGroup, "FetchProductItemsResponse_ProductGroups_PackageGroup", "FetchProductItemsResponse_ProductGroups_PackageGroup_groupCode", XMLType.RESPONSE);
					System.out.println("Index:" + index);

					if (index != 0) {
						xpaths.put("ProductGroups_PackageGroup_Packages_packageCode", "FetchProductItemsResponse_ProductGroups_PackageGroup");
						xpaths.put("Description_Text_TextElement", "FetchProductItemsResponse_ProductGroups_PackageGroup");
//						xpaths.put("ProductGroups_PackageGroup_Packages_packageCode", "FetchProductItemsResponse_ProductGroups_PackageGroup");
						resGroupPackageCodes = (ArrayList<LinkedHashMap<String, String>>) WSClient.getMultipleNodeList(fetchProductItemsRes, xpaths, false, XMLType.RESPONSE);
						String query2 = WSClient.getQuery("QS_02");
						dbGroupPackageCodes = WSClient.getDBRows(query2);

						for (int i = 0; i < dbGroupPackageCodes.size(); i++) {
							dbGrpPackageCode.put("PackagespackageCode" + (i + 1), dbGroupPackageCodes.get(i).get("PRODUCT_MEMBER"));
							dbGrpPackageCode.put("DescriptionTextTextElement" + (i + 1), dbGroupPackageCodes.get(i).get("DESCRIPTION"));
						}

						WSClient.writeToReport(LogStatus.INFO, "Veryfing the response for Group '" + packageGroup + "'. ");
						// Validating Packages
						WSAssert.assertEquals(dbGrpPackageCode, resGroupPackageCodes.get(index - 1), false);

					} else
						WSClient.writeToReport(LogStatus.FAIL, "Cannot Find the package Group :" + packageGroup + " in the response.");
				}

				/********** Error Text Element *************/
				if (WSAssert.assertIfElementExists(fetchProductItemsRes, "Result_Text_TextElement", true)) {
					String message = WSAssert.getElementValue(fetchProductItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchProductItems", "HTNG2008BExt", "HTNG" })
	public void fetchProductItems_Ext_3804() {

		try {

			String testName = "fetchProductItems_Ext_3082";
			WSClient.startTest(testName, "Verify that all the packages and their Details are fetched under Products", "minimumRegression");
			String resort = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			// fetching opera Resort and external Resort values
			String resortExtValue = HTNGLib.getExtResort(resort, interfaceName);

			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resort);

			// Fetching the Product Items
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String fetchProductItemsReq = WSClient.createSOAPMessage("HTNGExtFetchProductItems", "DS_01");
			String fetchProductItemsRes = WSClient.processSOAPMessage(fetchProductItemsReq);

			// validation of Response
			if (WSAssert.assertIfElementValueEquals(fetchProductItemsRes, "FetchProductItemsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

				HashMap<String, String> xpaths = new HashMap<>();
				List<LinkedHashMap<String, String>> packageDetailsRes = new ArrayList<>();
				List<LinkedHashMap<String, String>> packageDetailsDB = new ArrayList<>();
				xpaths.put("FetchProductItemsResponse_Products_Package_packageCode", "FetchProductItemsResponse_Products_Package");
				xpaths.put("Description_Text_TextElement_3", "FetchProductItemsResponse_Products_Package");
				xpaths.put("Products_Package_StartDate", "FetchProductItemsResponse_Products_Package");
				xpaths.put("Products_Package_EndDate", "FetchProductItemsResponse_Products_Package");
				xpaths.put("FetchProductItemsResponse_Products_Package_calculationRule", "FetchProductItemsResponse_Products_Package");
				// xpaths.put("FetchProductItemsResponse_Products_Package_postingRhythm",
				// "FetchProductItemsResponse_Products_Package");
				xpaths.put("FetchProductItemsResponse_Products_Package_sellSeparate", "FetchProductItemsResponse_Products_Package");
				xpaths.put("FetchProductItemsResponse_Products_Package_includedInRate", "FetchProductItemsResponse_Products_Package");
				packageDetailsRes = WSClient.getMultipleNodeList(fetchProductItemsRes, xpaths, false, XMLType.RESPONSE);
				System.out.println("\nPackageDetailsRes" + packageDetailsRes + "\n\n\n\n\n\n\n");

				String query = WSClient.getQuery("QS_03");
				System.out.println(query);
				packageDetailsDB = WSClient.getDBRows(query);
				System.out.println("\n Package DB deatails" + packageDetailsDB + "\n\n");
				WSAssert.assertEquals(packageDetailsRes, packageDetailsDB, false);

			}
			/********** Error Text Element *************/
			if (WSAssert.assertIfElementExists(fetchProductItemsRes, "Result_Text_TextElement", true)) {
				String message = WSAssert.getElementValue(fetchProductItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "FetchProductItems", "HTNG2008BExt", "HTNG" })
	public void fetchProductItems_Ext_3085() {

		try {

			String testName = "fetchProductItems_Ext_3085";
			WSClient.startTest(testName, "Verify that all Item groups and their Items are correctly fetched.", "minimumRegression");
			String resort = OPERALib.getResort();
			String interfaceName = HTNGLib.getHTNGInterface();
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			// fetching opera Resort and external Resort values
			String resortExtValue = HTNGLib.getExtResort(resort, interfaceName);

			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resort}", resort);

			// Fetching the Product Items
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String fetchProductItemsReq = WSClient.createSOAPMessage("HTNGExtFetchProductItems", "DS_01");
			String fetchProductItemsRes = WSClient.processSOAPMessage(fetchProductItemsReq);

			// validation of Response
			if (WSAssert.assertIfElementValueEquals(fetchProductItemsRes, "FetchProductItemsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

				HashMap<String, String> xpaths = new HashMap<>();

				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Item GroupCodes</b>");
				List<LinkedHashMap<String, String>> itemGroupCodes = new ArrayList<>();
				List<LinkedHashMap<String, String>> codesDb = new ArrayList<>();
				xpaths.put("FetchProductItemsResponse_Items_InventoryItemGroup_groupCode", "FetchProductItemsResponse_Items_InventoryItemGroup");
				itemGroupCodes = WSClient.getMultipleNodeList(fetchProductItemsRes, xpaths, false, XMLType.RESPONSE);
				String query = WSClient.getQuery("QS_04");
				System.out.println(query);
				codesDb = WSClient.getDBRows(query);
				System.out.println("\n Package DB deatails" + codesDb + "\n\n");
				WSAssert.assertEquals(itemGroupCodes, codesDb, false);

				// Verifying the Single ItemGroup
				String itemGroupCode = OperaPropConfig.getDataSetForCode("ItemClass", "DS_01"); // add After Config is complete //99999 in dev ENV
				WSClient.writeToReport(LogStatus.INFO, "<b> Validating Items in the Group : " + itemGroupCode + "</b>");
				xpaths.put("InventoryItemGroup_InventoryItem_ItemCode", "FetchProductItemsResponse_Items_InventoryItemGroup");
				xpaths.put("Description_Text_TextElement_4", "FetchProductItemsResponse_Items_InventoryItemGroup");
				xpaths.put("InventoryItemGroup_InventoryItem_ItemName", "FetchProductItemsResponse_Items_InventoryItemGroup");
				// Fetching From Response
				itemGroupCodes = WSClient.getMultipleNodeList(fetchProductItemsRes, xpaths, false, XMLType.RESPONSE);

				// fetching From DB
				WSClient.setData("{var_itemGroup}", itemGroupCode);
				String query1 = WSClient.getQuery("QS_05");
				codesDb = WSClient.getDBRows(query1);

				int index = WSClient.getNodeIndex(fetchProductItemsRes, itemGroupCode, "FetchProductItemsResponse_Items_InventoryItemGroup", "FetchProductItemsResponse_Items_InventoryItemGroup_groupCode", XMLType.RESPONSE);
				if (index != 0) {
					WSAssert.assertEquals(codesDb.get(0), itemGroupCodes.get(index - 1), false);
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Could not find the Item  Group:" + itemGroupCode);
				}

			}
			/********** Error Text Element *************/
			if (WSAssert.assertIfElementExists(fetchProductItemsRes, "Result_Text_TextElement", true)) {
				String message = WSAssert.getElementValue(fetchProductItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	@Test(groups = { "fullRegression", "FetchProductItems", "HTNG2008BExt", "HTNG" })
	public void fetchProductItems_Ext_3087() {
		try {

			String testName = "fetchProductItems_Ext_3087";
			WSClient.startTest(testName, "Verify error message should be displayed on the response when invalid resortID is passed on the FetchProductItems Request.", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "PackageGroup", "PackageCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// fetching opera Resort and external Resort values

//				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String resortExtValue = WSClient.getKeywordData("{KEYWORD_RANDSTR_5}");
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				System.out.println(WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
				WSClient.setData("{var_businessDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

				// Fetching the Product Items
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchProductItemsReq = WSClient.createSOAPMessage("HTNGExtFetchProductItems", "DS_01");
				String fetchProductItemsRes = WSClient.processSOAPMessage(fetchProductItemsReq);

				// validation of Response
				if (WSAssert.assertIfElementValueEquals(fetchProductItemsRes, "FetchProductItemsResponse_Result_resultStatusFlag", "FAIL", false)) {				
					
				}

				/********** Error Text Element *************/
				if (WSAssert.assertIfElementExists(fetchProductItemsRes, "Result_Text_TextElement", true)) {
					String message = WSAssert.getElementValue(fetchProductItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "targetedRegression", "FetchProductItems", "HTNG2008BExt", "HTNG" })
	public void fetchProductItems_Ext_3088() {
		try {

			String testName = "fetchProductItems_Ext_3088";
			WSClient.startTest(testName, "Verify error message should be displayed on the response when resortID is missed on the FetchProductItems Request.", "targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "PackageGroup", "PackageCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				// fetching opera Resort and external Resort values

//				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String resortExtValue ="";
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				System.out.println(WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
				WSClient.setData("{var_businessDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

				// Fetching the Product Items
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchProductItemsReq = WSClient.createSOAPMessage("HTNGExtFetchProductItems", "DS_01");
				String fetchProductItemsRes = WSClient.processSOAPMessage(fetchProductItemsReq);

				// validation of Response
				if (WSAssert.assertIfElementValueEquals(fetchProductItemsRes, "FetchProductItemsResponse_Result_resultStatusFlag", "FAIL", false)) {				
					
				}

				/********** Error Text Element *************/
				if (WSAssert.assertIfElementExists(fetchProductItemsRes, "Result_Text_TextElement", true)) {
					String message = WSAssert.getElementValue(fetchProductItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}
}
