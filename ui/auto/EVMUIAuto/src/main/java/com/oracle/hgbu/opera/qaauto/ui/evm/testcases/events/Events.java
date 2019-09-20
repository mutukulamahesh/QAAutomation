package com.oracle.hgbu.opera.qaauto.ui.evm.testcases.events;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.evm.component.events.EventPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @Description This class provides methods for creating events
 * @author cpsinha
 *
 */

public class Events extends Utils {
	/**
	 * ****************************************************
	 * @Preconditions:None
	 * @description This test case creates a sample block
	 * @Zephyr ID: NA
	 * @author cpsinha
	 * @throws Exception
	 * ****************************************************
	 */
	@Test(priority = 1,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void createBlockForEvent() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_1");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Create  Block For Event </b>");
		try {
			EventPage.createBlock();
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Master Event From Block With Sub Event is not Created "+e.getMessage());
			throw (e);
		}catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create  Block  is not Created "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 * *******************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
	 * 	
	 * OPERA Controls:
	 * Business Blocks= Y
	 * CATERING_EVENTS=Y
	 * 
	 * Tasks: 
	 * Manage Blocks= Grant
	 * New/Edit Block = Grant
	 * Manage Events = Grant
	 * New/Edit Catering Events= Grant
	 * 	
	 * Application Data:
	 * A block must Exist
	 * 
	 * @Description Create Event with space
	 * @ZephyrID:266245
	 * @author cpsinha
	 * @throws Exception
	 * *******************************************************
	 */
	
	@Test(priority = 2,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void createEventFromBlockWithSpace() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_1");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Create Event From Block WIth Space </b>");
		try {
			EventPage.createEventFromBlockWithSpace(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Master Event From Block With Sub Event is not Created "+e.getMessage());
			throw (e);
		}catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Event From Block WIth Space is not Created "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 ******************************************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
	 * 	
	 * OPERA Controls:
	 * Business Blocks= Y
	 * CATERING_EVENTS=Y
	 * SUB_EVENTS_ACTIVE=Y
	 * DEFAULT_SUB_EVENT_RATECODE=CUSTOM
	 * 
	 * Tasks: 
	 * Manage Blocks= Grant
	 * New/Edit Block = Grant
	 * Manage Events = Grant
	 * New/Edit Catering Events= Grant
	 * 	
	 * Application Data:
	 * A block must Exist
	 * 
	 * @description Create a master event from a block with block with sub event
	 * @author cpsinha
	 * @ZephyrID: 266246
	 * @throws Exception
	 * *******************************************************************************
	 */
	@Test(priority = 3,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void createMasterEventFromBlockWithSubEvent() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_2");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Create Master Event From Block With Sub Event </b>");
		try {
			EventPage.createMasterEventFromBlockWithSubEvent(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Master Event From Block With Sub Event is not Created "+e.getMessage());
			throw (e);
		}catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Master Event From Block With Sub Event is not Created "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 ********************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
	 * 	
	 * OPERA Controls:
	 * Business Blocks= Y
	 * CATERING_EVENTS=Y
	 * SUB_EVENTS_ACTIVE=Y
	 * DEFAULT_SUB_EVENT_RATECODE=[Not Populated]
	 * 
	 * Tasks: 
	 * Manage Blocks= Grant
	 * New/Edit Block = Grant
	 * Manage Events = Grant
	 * New/Edit Catering Events= Grant
	 * 	
	 * Application Data:
	 * A master or standalone event must exist
	 * 
	 * @description Create sub event from event search
	 * @ZephyrId: 266247
	 * @author cpsinha
	 * @throws Exception
	 * **********************************************************
	 */
	
	@Test(priority = 4,groups = { "BAT" }) //,groups = { "bat" }, enabled=false
	public void createSubEventFromEventSearch() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_3");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Create Sub Event from Event Search </b>");
		try {
			EventPage.createSubEventFromEventSearch(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Sub Event from Event Search is not Created "+e.getMessage());
			throw (e);
		}catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Create Sub Event from Event Search is not Created "+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 * ********************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
	 * 	
	 * OPERA Controls:
	 * CATERING_EVENTS=Y
	 * 
	 * Tasks: 
	 * Manage Events = Grant
	 * 	
	 * Application Data:
	 * Existing Event Should Exist
	 * 
	 * @Description: Search for Events by Date
	 * @Zephyr ID:266241
	 * @author cpsinha
	 * @throws Exception
	 * **********************************************************
	 */
	@Test(priority = 5,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void searchEventBasedOnDate() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_4");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Search Event using Date </b>");
		try {
			EventPage.searchEventFromDate(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		}catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Search Event from Date Failed"+e.getMessage());
			throw (e);
		} catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Search Event from Date Failed"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 *********************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
	 * 	
	 * OPERA Controls:
	 * CATERING_EVENTS=Y
	 * 
	 * Tasks: 
	 * Manage Events = Grant
	 * 	
	 * Application Data:
	 * A block with existing event should exist
	 * 
	 * @Description: Search for Events by Block ID
	 * @Zephyr ID:266242
	 * @author cpsinha
	 * @throws Exception
	 * ********************************************************
	 */
	@Test(priority = 6,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void searchEventBasedOnBlock() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_4");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Search Event using BlockId </b>");
		try {
			EventPage.searchEventFromBlockId(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Search Event from BlockId Failed"+e.getMessage());
			throw (e);
		} catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Search Event from BlockId Failed"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 ****************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
	 * 	
	 * OPERA Controls:
	 * Business Blocks= Y
	 * CATERING_EVENTS=Y
	 * 
	 * Tasks: 
	 * Manage Blocks= Grant
	 * New/Edit Block = Grant
	 * Manage Events = Grant
	 * New/Edit Catering Events= Grant
	 * 	
	 * Application Data:
	 * A block must Exist
	 * 
	 * @Description Create Event from Block
	 * @Zephyr Id:266244
	 * @author cpsinha
	 * @throws Exception
	 * ****************************************************
	 */
	@Test(priority = 7,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void createEventFromBlock() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_5");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Create Event from Block </b>");
		try {
			EventPage.createEventFromBlock(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Event could not be created from Block"+e.getMessage());
			throw (e);
		} catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Event Could not be created from Block"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 **********************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
	 * 	
	 * OPERA Controls:
	 * Business Blocks= Y
	 * CATERING_EVENTS=Y
	 * 
	 * Tasks: 
	 * Manage Events = Grant
	 * Manage Blocks= Grant
	 * New/Edit Block = Grant
	 * 	
	 * Application Data:
	 * A block with existing event should exist
	 * 
	 * @Desscription: Access Event Search via Block
	 * @Zephyr ID:266243
	 * @author cpsinha
	 * @throws Exception
	 * *********************************************************
	 */
	@Test(priority = 8,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void accessEventSeachFromViaBlock() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_5");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Access Event Search via Block </b>");
		try {
			EventPage.accessEventSeachFromViaBlock(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Access Event Search via Block failed "+e.getMessage());
			throw (e);
		} catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Access Event Search via Block failed"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 **********************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
		
	 * OPERA Controls:
	 * Business Blocks= Y
	 * CATERING_EVENTS=Y
	 *	
	 * Tasks: 
	 * Manage Blocks= Grant
	 * New/Edit Block = Grant
	 * Manage Events = Grant
	 * New/Edit Catering Events= Grant
	 *
	 * Application Data:
	 * A master or standalone event must exist
	 * 
	 * @Desscription: Access Event Presentation Screen
	 * @Zephyr ID:266248
	 * @author cpsinha
	 * @throws Exception
	 * *********************************************************
	 */
	@Test(priority = 9,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void accessEventPresentationScreen() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_5");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Access Event Presentation Screen </b>");
		try {
			EventPage.accessEventPresentationScreen(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Access Event Presentation Screen failed "+e.getMessage());
			throw (e);
		} catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Access Event Presentation Screen failed"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	/**
	 **********************************************************
	 * @Preconditions:
	 * License:
	 * OCS_900 OPERA Cloud S&C Standard should be active
		
	 * OPERA Controls:
	 * Business Blocks= Y
	 * CATERING_EVENTS=Y
	 *	
	 * Tasks: 
	 * Manage Blocks= Grant
	 * New/Edit Block = Grant
	 * Manage Events = Grant
	 * New/Edit Catering Events= Grant
	 *
	 * Application Data:
	 * A master or standalone event must exist
	 * 
	 * @Desscription: Access Event Presentation Screen
	 * @Zephyr ID:266248
	 * @author cpsinha
	 * @throws Exception
	 * *********************************************************
	 */
	@Test(priority = 10,groups = { "SANITY" }) //,groups = { "bat" }, enabled=false
	public void verifyFunctionSpaceResourceDetails() throws Exception {

		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_6");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify Function Space Resource details </b>");
		try {
			EventPage.verifyFunctionSpaceResourceDetails(eventMap);
			Utils.takeScreenshot(driver, testClassName);
						
		} catch (AssertionError e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Function Space Resource details failed "+e.getMessage());
			throw (e);
		} catch (Exception e) 
		{
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Function Space Resource details failed"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();

	}
	
	
	
	
}

