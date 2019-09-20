package com.oracle.hgbu.opera.qaauto.ui.evm.testcases.events;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.evm.component.events.BlockPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @Description This class provides methods for creating events
 * @author MVamsi
 *
 */

public class Blocks extends Utils {
	
	@Test(priority = 1,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void createNewBusinessBlock() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata1");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Business Block can be created when values entered for only mandatory fields </b>");
		try {
			BlockPage.createBusinessBlock(blockMap);
			//logger = report.startTest(testName, "Create Event From Block WIth Space").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Event From Block WIth Space is not Created "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(priority = 2,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void editBusinessBlock() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Business Block can be edited </b>");
		try {
			BlockPage.editBusinessBlockDetails(blockMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Business Block can be edited is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(priority = 3,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void blocksRoomRateCodesEdit() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Rooms and Rates can be added for a block using Edit link in Room&Rate Grid is validated</b>");
		try {
			BlockPage.blocksRoomRateCodesEditAdd(blockMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Rooms and Rates can be added for a block using Edit link in Room&Rate Grid is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 4,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void ChangeBlockStatus_CancelReason() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Block can be cancelled with the cancel reason provided </b>");
		try {
			BlockPage.ChangeBlock_CancelReason(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Block can be cancelled with the cancel reason provided is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 5,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void blocksStatusChange() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Block status can be changed for a complete status flow from Inquiry to Definite is validated</b>");
		try {
			BlockPage.blocksStatusChangeTenDefCan(blockMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Block status can be changed for a complete status flow from Inquiry to Definite is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 6,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void createBlockWithTraceCode() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify trace code values for block is validated</b>");
		try {
			BlockPage.createBlockWithTraceCodeLOV(blockMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify trace code values for block is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}


	@Test(priority = 7,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createBlockMandatoryNonMandatory() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Business Block can be created when values entered for both mandatory as well as non mandatory fields </b>");
		try {
			BlockPage.createBlockMandatoryNonMandatoryFields(blockMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Business Block can be created when values entered for both mandatory as well as non mandatory fields is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 8,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void searchSelectBlock() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify User is able to search and select Blocks using Block Search feature </b>");
		try {
			BlockPage.searchSelectBlockFeature(blockMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify User is able to search and select Blocks using Block Search feature is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 9,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false 258173
	public void accountProfileAttachBlock() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Account and Contact Profiles can be attached in Create Block screen </b>");
		try {
			BlockPage.accountProfileAttachCreateBlock(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Account and Contact Profiles can be attached in Create Block screen is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 10,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createMasterSubBlockmain() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Master - Sub Blocks can be created when sync checkbox is selected </b>");
		try {
			BlockPage.createMasterSubBlock(blockMap, "OFF");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Master - Sub Blocks can be created when sync checkbox is selected is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(priority = 11,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createMasterAllocationBlock() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Master - Sub Blocks can be created when sync checkbox is selected </b>");
		try {
			BlockPage.createMasterAllocationBlockMain(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Master - Sub Blocks can be created when sync checkbox is selected is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}

	@Test(priority = 12,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void defaultCateringStatus() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Default catering status displays when default status is set in the administration is validated</b>");
		try {
			BlockPage.defaultCateringStatusBlockCreation(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Default catering status displays when default status is set in the administration is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(priority = 13,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createBlockRoomGrid() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Block created and Room Grid displayed when Save and Go To Room Grid selected in Block Create screen is validated</b>");
		try {
			BlockPage.createBlockSaveGotoRoomGrid(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Block created and Room Grid displayed when Save and Go To Room Grid selected in Block Create screen is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(priority = 14,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createBlockMultipleRatecodes() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify multiple Rate Codes can be added in Create Block screen is validated</b>");
		try {
			BlockPage.createBlockMultirateCOdeSuperScript(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify multiple Rate Codes can be added in Create Block screen is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(priority = 15,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void blockSearchMultipleViews() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify of viewing and navigating from the 4 search views is validated</b>");
		try {
			BlockPage.blockSearchMultipleNavigationViews(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify of viewing and navigating from the 4 search views is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
//	//@Test(priority = 16,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createReservaionSubBlock() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Reservations can be created for Sub Blocks via Create Reservation link </b>");
		try {
			BlockPage.subBlockCreateReservationLink(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify Reservations can be created for Sub Blocks via Create Reservation link is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 17,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createSubBlockSyncCheckox() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify New Sub Block can be created via Master Block when sync checkbox is selected </b>");
		try {
			BlockPage.createSubBlockSyncCheckoxSelected(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify New Sub Block can be created via Master Block when sync checkbox is selected is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 18,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void CreateBlockLoadGrid() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify that Load Grid on Inquiry or Non Deduct status does not affect House Inventory and Verify that status change to Deduct updates the inventory availability correctly </b>");
		try {
			BlockPage.CreateBlockLoadRoomGrid(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL,"  Verify that Load Grid on Inquiry or Non Deduct status does not affect House Inventory and Verify that status change to Deduct updates the inventory availability correctly is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 19,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createBlockNotes() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Very that Block Notes can be created, edited and deleted </b>");
		try {
			BlockPage.createBlockNotesCreateEditDelete(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Very that Block Notes can be created, edited and deleted is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 20,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void verifyDepositRequestDelete() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify that Deposit requests can be entered for a business block </b>");
		try {
			BlockPage.verifyDepositRequestDeleteRequest(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify that Deposit requests can be entered for a business block "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(priority = 21,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void verifyCancelRuletDelete() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify that Cancellation Rules can be entered for a business block </b>");
		try {
			BlockPage.verifyCancellationRuletDeleteRule(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify that Cancellation Rules can be entered for a business block "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	@Test(priority = 21,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void verifyInventoryEditDelete() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify that Inventory Items can be added to a block, can be edited and deleted </b>");
		try {
			BlockPage.verifyInventoryItemsEditDelete(blockMap);
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify that Inventory Items can be added to a block, can be edited and deleted "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
}

