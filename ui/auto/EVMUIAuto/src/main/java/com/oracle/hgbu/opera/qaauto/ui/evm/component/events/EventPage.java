package com.oracle.hgbu.opera.qaauto.ui.evm.component.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @Description This class provides the methods for creating events
 * @author cpsinha
 *
 */
public class EventPage extends Utils {
	/**
	 * This method creates a sample block
	 * @throws Exception
	 */
	public static void createBlock() throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();

		try {
			HashMap<String, String> blockMap = new HashMap<String, String>();
			blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata1");
			evntComp.createBlock(blockMap);
		} catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create an Block " + e.getMessage());
			throw new Exception(e.getMessage());
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create an Block " + e.getMessage());
			throw (e);
		}
	}

	/**
	 * This method creates an event from a block along with space
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void createEventFromBlockWithSpace(HashMap<String, String> eventMap) throws Exception ,AssertionError{
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();

		try {
			/**
			 * #1 Login to Application at Hub Level
			 */
			evntComp.selectHub(eventMap);
			Assert.assertTrue(getText("Events.HomeScreen", 100, "presence").contains("Hello,"));
			waitForSpinnerToDisappear(30);
			/**
			 * #2 Navigate to block Search and search for an existing block
			 */
			HashMap<String, String> blockDetails=evntComp.searchExistingBlock(eventMap);
			eventMap.put("PropertyName", blockDetails.get("property"));
			
			/**
			 * #3 Select Create Event and verify the default value details
			 */
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			click("Events.Bookings.CreateEvent",50,"presence");
			waitForSpinnerToDisappear(40);
			evntComp.verifyValuesOfBlock(eventMap);
			
			/**
			 * #4 Select Event Type
			 */
			evntComp.selectEventType(eventMap.get("EventType"), eventMap.get("EventName"), eventMap.get("EndTime"), eventMap.get("StartTime"));
			
			/**
			 * #5 Select Start Time
			 */
			textBox("Events.Bookings.Event.StartTime",eventMap.get("StartTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.StartTime");
			waitForSpinnerToDisappear(40);
			String startTime=verifyTimeFormat(eventMap.get("StartTimeValue"));
			AssertEquals(startTime,"Start Time  is not in correct format","Events.Bookings.Event.StartTime","value");
			/**
			 * #6 Select End Time
			 */
			textBox("Events.Bookings.Event.EndTime",eventMap.get("EndTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.EndTime");
			waitForSpinnerToDisappear(40);
			String endTime=verifyTimeFormat(eventMap.get("EndTimeValue"));
			AssertEquals(endTime,"End Time is not in correct format","Events.Bookings.Event.EndTime","title");
			/**
			 * #7 Select a Function Space
			 */
			textBox("Events.Bookings.Event.FunctionSpace",eventMap.get("Space"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.FunctionSpace");
			waitForSpinnerToDisappear(40);
			AssertEquals(eventMap.get("Space"),"Space value is not as per expectation","Events.Bookings.Event.FunctionSpace","value");
			AssertEquals("","The default value of Set Up Style is not blank","Events.Bookings.Event.SetupStyle","value");
			AssertEquals("","The default value of Rental Code is not blank","Events.Bookings.Event.RentalCode","value");
			AssertEquals("","The default value of TearDown is not blank","Events.Bookings.Event.TearDown","value");
			AssertEquals("","The default value of Setup time is not blank","Events.Bookings.Event.SetupTime","value");
			AssertEquals("","The default value of Rental Amount is not blank","Events.Bookings.Event.RentalAmount","value");
			AssertEquals("true","Rental Code is not marked as mandatory","Events.Bookings.Event.RentalCode","aria-required");
			AssertEquals("true","Setupstyle value is not marked as mandatory","Events.Bookings.Event.SetupStyle","aria-required");
			/**
			 * #8 Navigate to Rental Code and Select a value
			 */
			textBox("Events.Bookings.Event.RentalCode",eventMap.get("Rentalcode"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalCode");
			waitForSpinnerToDisappear(40);
			AssertEqualsIgnoreCase(eventMap.get("Rentalcode"),"Rental code could not be selected","Events.Bookings.Event.RentalCode","value");
			AssertEquals("","Rental Amount is not blank","Events.Bookings.Event.RentalAmount","value");
			AssertContains("Disabled","Discount percentage is not disabled","Events.Bookings.Event.DiscountPercentage","class");
			/**
			 * #9 Navigate to SetupStyle and select a value
			 */
			textBox("Events.Bookings.Event.SetupStyle",eventMap.get("Setupstyle"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.SetupStyle");
			waitForSpinnerToDisappear(40);
			AssertEquals(eventMap.get("Setupstyle"),"Setup Style is not as per expected value","Events.Bookings.Event.SetupStyle","value");
			AssertEquals(eventMap.get("TearDownTime"),"Setup time is not as per expected value","Events.Bookings.Event.TearDown","value");
			AssertEquals(eventMap.get("SetupTime"),"Tear Down time is not as per expected value","Events.Bookings.Event.SetupTime","value");
			
			/**
			 * #10 Navigate to rental amount and enter a value
			 */
			textBox("Events.Bookings.Event.RentalAmount",eventMap.get("RentalAmount"),0,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalAmount");
			waitForSpinnerToDisappear(40);
			WebdriverWait(20, "Events.Bookings.Event.RentalAmountCurrency", "visible");
			AssertEquals("USD","Setup Style could not be selected","Events.Bookings.Event.RentalAmountCurrency","text");
			/**
			 * #11 Select Save and create another event
			 */
			evntComp.saveEvent("SaveAndCreateAnotherEvent");
			AssertEquals(eventMap.get("StartDate"),"Start Date value is not as per expectation","Events.Bookings.Event.StartDate","value");
			logger.log(LogStatus.PASS, "Start Date value is displayed as per expectation"+eventMap.get("StartDate"));
			AssertEquals(eventMap.get("EndDate"),"End Date value is not as per expectation","Events.Bookings.Event.EndDate","text");
			logger.log(LogStatus.PASS, "End Date value is displayed as per expectation"+eventMap.get("EndDate"));
			AssertEquals(eventMap.get("Attendees"),"Attendee value is not as per expectation","Events.Bookings.Event.ExpectedAttendees","value");
			logger.log(LogStatus.PASS, "Attendees value is displayed as per expectation"+eventMap.get("Attendees"));
			AssertEquals(eventMap.get("Status"),"Status value is not as per expectation","Events.Bookings.Event.Status","value");
			logger.log(LogStatus.PASS, "Status value is displayed as per expectation"+eventMap.get("Status"));
			AssertEquals("","EventType value is not as per expectation","Events.Bookings.Event.EventType","value");
			logger.log(LogStatus.PASS, "EventType value is displayed as blank per expectation");
			AssertEquals("","EventName value is not as per expectation","Events.Bookings.Event.EventName","value");
			logger.log(LogStatus.PASS, "EventName value is displayed as blank per expectation");
			AssertEquals("","Setup Style is not as per expected value","Events.Bookings.Event.SetupStyle","value");
			logger.log(LogStatus.PASS, "Setup Style value is displayed as blank per expectation");
			AssertEquals("","Tear Down Time  is not as per expected value","Events.Bookings.Event.TearDown","value");
			logger.log(LogStatus.PASS, "Tear Down Time value is displayed as blank per expectation");
			AssertEquals("","Setup Time  is not as per expected value","Events.Bookings.Event.SetupTime","value");
			logger.log(LogStatus.PASS, "Setup Time value is displayed as blank per expectation");
			AssertEquals("","The default value of Rental Code is not blank","Events.Bookings.Event.RentalCode","value");
			logger.log(LogStatus.PASS, "Rental Code value is displayed as blank per expectation");
			AssertEquals("","The default value of Rental Amount is not blank","Events.Bookings.Event.RentalAmount","value");
			logger.log(LogStatus.PASS, "Rental Amount value is displayed as blank per expectation");
			AssertEquals("","The default Space value is not as per expectation","Events.Bookings.Event.FunctionSpace","value");
			logger.log(LogStatus.PASS, "Space  value is displayed as blank per expectation");
			 
		} catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create an Event " + e.getMessage());
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create an Event " + e.getMessage());
			throw (e);
		}
	}
	
	/**
	 * This method creates Master event along with Sub event
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void createMasterEventFromBlockWithSubEvent(HashMap<String, String> eventMap) throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			/**
			 * Precondition- Update DEFAULT_SUB_EVENT_RATECODE to CUSTOM
			 */
			evntComp.applicationFunctionsParametersSettings("CUSTOM");
			evntComp.redirectToCloud();
			/**
			 * #1 Login to Application at Property Level
			 */
			evntComp.selectHub(eventMap);
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			eventMap.put("BlockId", blockDetails.get("finalBlockId"));
			eventMap.put("BlockName", blockDetails.get("finalBlockCode"));
			eventMap.put("PropertyName", blockDetails.get("property"));
			eventMap.put("StartDate", blockDetails.get("finalStartDate"));
			eventMap.put("EndDate", blockDetails.get("finalStartDate"));
			eventMap.put("Status", blockDetails.get("finalBlockStatus"));
			eventMap.put("Attendees", blockDetails.get("finalEventAttendees"));
		
			/**
			 * #2 Navigate to block Search and search for an existing block
			 */
			evntComp.searchExistingBlockUsingBlockID(blockDetails.get("finalBlockId"));
			
			/**
			 * #3 Select Create Event and verify the default value details
			 */
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			click("Events.Bookings.CreateEvent",50,"presence");	
			waitForSpinnerToDisappear(40);
			evntComp.verifyValuesOfBlock(eventMap);
			
			/**
			 * #4 Select Event Type
			 */
			evntComp.selectEventType(eventMap.get("EventType"), eventMap.get("EventName"), eventMap.get("StartTime"), eventMap.get("EndTime"));
			
			textBox("Events.Bookings.Event.StartTime",eventMap.get("StartTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.StartTime");
			waitForSpinnerToDisappear(40);
			String startTime=verifyTimeFormat(eventMap.get("StartTimeValue"));
			Assert.assertTrue(element("Events.Bookings.Event.StartTime").getAttribute("value").contains(startTime),"Start Time  is not in correct format");
			textBox("Events.Bookings.Event.EndTime",eventMap.get("EndTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.EndTime");
			waitForSpinnerToDisappear(40);
			String endTime=verifyTimeFormat(eventMap.get("EndTimeValue"));
			Assert.assertTrue(element("Events.Bookings.Event.EndTime").getAttribute("value").contains(endTime),"End Time  is not in correct format");
			
			/**
			 * #5 Select a Function Space: Field should be populated with selected value 
 			 *    fields for Rental Code and Setup Style should be marked as mandatory, No Default values should be present
			 */
			textBox("Events.Bookings.Event.FunctionSpace",eventMap.get("Space"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.FunctionSpace");
			waitForSpinnerToDisappear(40);
			for (int i=0;i<30;i++){
				try{
					Assert.assertTrue(element("Events.Bookings.Event.RentalCode").getAttribute("aria-required").equals("true"));
					break;
				}catch(Exception e){
					Wait(1000);
				}
			}
			AssertEquals(eventMap.get("Space"),"Space value is not as per expectation","Events.Bookings.Event.FunctionSpace","value");
			AssertEquals("","The default value of Set Up Style is not blank","Events.Bookings.Event.SetupStyle","value");
			AssertEquals("","The default value of Rental Code is not blank","Events.Bookings.Event.RentalCode","value");
			AssertEquals("","The default value of TearDown is not blank","Events.Bookings.Event.TearDown","value");
			AssertEquals("","The default value of Setup time is not blank","Events.Bookings.Event.SetupTime","value");
			AssertEquals("","The default value of Rental Amount is not blank","Events.Bookings.Event.RentalAmount","value");
			AssertEquals("true","Rental Code is not marked as mandatory","Events.Bookings.Event.RentalCode","aria-required");
			AssertEquals("true","Setupstyle value is not marked as mandatory","Events.Bookings.Event.SetupStyle","aria-required");
			
			/**
			 * #6 Navigate to Rental Code and Select a value which is not Custom: The rental code field should be populated with the 
			 * value selected and rental amount should be populated with the configured amount.  
			 * The rental amount field should be disabled for editing and the Discount Percentage field should be enabled for editing.
			 */
			textBox("Events.Bookings.Event.RentalCode",eventMap.get("Rentalcode"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalCode");
			waitForSpinnerToDisappear(40);
			AssertEqualsIgnoreCase(eventMap.get("Rentalcode"),"Rental code could not be selected","Events.Bookings.Event.RentalCode","value");
			AssertContains("Disabled","Rental Amount is not disabled","Events.Bookings.Event.RentalAmountState","class");
			AssertContains(eventMap.get("RentalAmount"),"Rental Amount is not as per extected value","Events.Bookings.Event.RentalAmount","value");
			AssertEquals("","Discount Percentage is not blank","Events.Bookings.Event.DiscountPercentage","text");
			Assert.assertTrue(element("Events.Bookings.Event.DiscountPercentage").isEnabled(),"Discount Percentage Field is not enabled");
			/**
			 * #7 Navigate to SetupStyle and select a value
			 */
			textBox("Events.Bookings.Event.SetupStyle",eventMap.get("Setupstyle"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.SetupStyle");
			waitForSpinnerToDisappear(40);
			AssertEquals(eventMap.get("Setupstyle"),"Setup Style is not as per expected value","Events.Bookings.Event.SetupStyle","value");
			AssertEquals(eventMap.get("TearDownTime"),"Setup time is not as per expected value","Events.Bookings.Event.TearDown","value");
			AssertEquals(eventMap.get("SetupTime"),"Tear Down time is not as per expected value","Events.Bookings.Event.SetupTime","value");
			
			/**
			 * #8 Select Sub Event CheckBox
			 */
			click("Events.Bookings.Event.SubEventCheckbox",0,"presence");	
			waitForSpinnerToDisappear(30);
			AssertEquals("true","Save and Create Event is not disabled","Events.Bookings.Event.SaveAndCreateAnotherEvent","aria-disabled");
			AssertEquals("true","Save and Manage Event is not disabled","Events.Bookings.Event.SaveAndManageEvent","aria-disabled");
			
			/**
			 * #9 Click on Save
			 */
			Utils.scroll("down");
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(40);
			if((elements("Events.Bookings.Event.SavedEvent").size()==0) & element("Events.Bookings.Event.Save").isEnabled()){
				click("Events.Bookings.Event.Save",50,"presence");
			}
			
			waitForSpinnerToDisappear(40);
			Wait(7000);
			Assert.assertTrue(element("Events.Bookings.Event.CollapsedMasterEvent").isDisplayed(),"The Master event is not in collapsed format");
			WebdriverWait(30,"Events.Bookings.Event.CollapsedMasterEvent", "displayed");
			String savedEvent="Event:"+" "+eventMap.get("EventName")+" "+"|"+" "+eventMap.get("StartDate")+" "+"|"+" "+startTime+" "+"|"+" "+eventMap.get("EventType");
			AssertContains(savedEvent,"Event could not be created","Events.Bookings.Event.SavedEvent","title");
			
			AssertEquals(eventMap.get("StartDate"),"Start Date value is not inherited from Master event ","Events.Bookings.Event.StartDate","value");
			AssertEquals(eventMap.get("EndDate"),"End Date value is not inherited from Master event","Events.Bookings.Event.EndDate","text");
			AssertEquals(eventMap.get("Attendees"),"Attendee value is not is not inherited from Master event","Events.Bookings.Event.ExpectedAttendees","value");
			AssertEquals(eventMap.get("Status"),"Status value is not inherited from Master event","Events.Bookings.Event.Status","value");
			AssertEquals(eventMap.get("Space"),"The Space value is not inherited from Master event","Events.Bookings.Event.FunctionSpace","value");
			AssertEquals(eventMap.get("Setupstyle"),"Setup Style is not inherited from Master event","Events.Bookings.Event.SetupStyle","value");
			AssertEquals("CUSTOM","The Rental Code Custom is not set after creation of subEvent","Events.Bookings.Event.RentalCode","value");
			
			/**
			 * #10 Navigate to Event Type>select a value which has default start and end times configured
			 */
			evntComp.selectEventType(eventMap.get("SubEventType"), eventMap.get("SubEventName"), eventMap.get("StartTime"), eventMap.get("EndTime"));
			
			startTime=verifyTimeFormat(eventMap.get("SubEventStartTime"));
			AssertEquals(startTime,"Start Time  is not in correct format","Events.Bookings.Event.StartTime","value");
			
			endTime=verifyTimeFormat(eventMap.get("SubEventEndTime"));
			AssertEquals(endTime,"End Time is not in correct format","Events.Bookings.Event.EndTime","value");
			
			/**
			 * #11 Click Save 
			 */
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(40);
			Wait(8000);
			Assert.assertTrue(isExists("Events.Bookings.Event.EventSearchScreen")," The application is not redirected to Event Search screen");
			Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@data-ocid,'_LINK_')]/ancestor::tr/td[12]//*[contains(@title,'Master Event')]")).isDisplayed(),"The newly created event is not displayed");
			Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@data-ocid,'_LINK_')]/ancestor::tr/td[12]//*[contains(@title,'Sub Event')]")).isDisplayed(),"The newly created event is not displayed");
	
			click("Events.Bookings.Event.LinkToModifySearchCriteria",0,"");
			waitForSpinnerToDisappear(30);
			String actualBlockIdPrefilled=Utils.getAttributeOfElement("Events.Bookings.Event.BlockID", "value", 0, "");
			Assert.assertEquals(blockDetails.get("finalBlockId"), actualBlockIdPrefilled, "Expected Block ID is not prefilled in the Search criteria");						
			logger.log(LogStatus.PASS, "Created Master Event From Block With SubEvent successfully " );
			System.out.println(" Created Master Event From Block With SubEvent successfully ");
			 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create Master Event From Block With SubEvent " + e.getMessage());
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create Master Event From Block With SubEvent" + e.getMessage());
			throw (e);
		}
	}
	
	/**
	 * @Description Create sub event from event search
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void createSubEventFromEventSearch(HashMap<String, String> eventMap) throws Exception ,AssertionError{
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			
			/**
			 * # Precondition: Set DEFAULT_SUB_EVENT_RATECODE=[Not Populated]
			 */
			evntComp.applicationFunctionsParametersSettings("null");	
			evntComp.redirectToCloud();
			Assert.assertTrue(element("Events.Bookings.Events.HomePage").isDisplayed());
			
			/**
			 * #1 Login to Application at Property Level
			 * 
			 */
			evntComp.selectHub(eventMap);
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			eventMap.put("BlockId", blockDetails.get("finalBlockId"));
			eventMap.put("BlockName", blockDetails.get("finalBlockCode"));
			Wait(3000);
			Utils.click("Events.Bookings.SearchResults",0,"presence");
			waitForSpinnerToDisappear(30);
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			click("Events.Bookings.CreateEvent",50,"presence");	
			waitForSpinnerToDisappear(30);
			
			evntComp.selectEventType(eventMap.get("EventType"), eventMap.get("EventName"), eventMap.get("EndTime"), eventMap.get("StartTime"));
			textBox("Events.Bookings.Event.StartTime",eventMap.get("StartTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.StartTime");
			waitForSpinnerToDisappear(40);
			textBox("Events.Bookings.Event.EndTime",eventMap.get("EndTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.EndTime");
			waitForSpinnerToDisappear(40);
			textBox("Events.Bookings.Event.FunctionSpace",eventMap.get("Space"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.FunctionSpace");
			waitForSpinnerToDisappear(40);
			textBox("Events.Bookings.Event.RentalCode",eventMap.get("Rentalcode"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalCode");
			waitForSpinnerToDisappear(40);
			textBox("Events.Bookings.Event.SetupStyle",eventMap.get("Setupstyle"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.SetupStyle");
			waitForSpinnerToDisappear(40);
			textBox("Events.Bookings.Event.RentalAmount",eventMap.get("RentalAmount"),0,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalAmount");
			waitForSpinnerToDisappear(40);
			Utils.scroll("down");
			Wait(2000);
			element("Events.Bookings.Event.Save").click();
			waitForSpinnerToDisappear(40);
			
			Wait(2000);
			Utils.scroll("down");
			HashMap<String, String> eventDetails=evntComp.fetchEventDetails();
			Utils.click("Events.Bookings.Event.EventDetails",0, "presence");
			waitForSpinnerToDisappear(30);
			String rentalCode=Utils.getText("Events.Bookings.Event.RentalCode",0, "presence");
			for (int i=0;i<5;i++){
				if (rentalCode.equals("")){
					rentalCode=Utils.getText("Events.Bookings.Event.RentalCode",0, "presence");
					Wait(1000);
				}else{
					break;
				}
			}
			Utils.click("Events.Bookings.Event.closePopUp", 0, "");
			waitForSpinnerToDisappear(30);
			Utils.click("Events.Bookings.EventSearchResults",30, "presence");
			waitForSpinnerToDisappear(30);
			
			/**
			 * #4 Verify option for SubEvent is displayed
			 */
			Utils.isDisplayed("Events.Bookings.Event.SubEvent", "option For SubEvent");
			
			/**
			 * #5 Click for SubEvent is displayed
			 */
			Utils.click("Events.Bookings.Event.SubEvent",30, "clickable");
			waitForSpinnerToDisappear(30);
			String savedEvent="Event:"+" "+eventDetails.get("eventName")+" "+"|"+" "+eventDetails.get("startDate")+" "+"|"+" "+eventDetails.get("startTime")+" "+"|"+" "+eventDetails.get("eventType");
			AssertContains(savedEvent,"Event could not be created","Events.Bookings.Event.SavedEvent","title");
			
			AssertEquals(eventDetails.get("startDate"),"Start Date value is not inherited from Master event ","Events.Bookings.Event.StartDate","value");
			AssertEquals(eventDetails.get("endDate"),"End Date value is not inherited from Master event","Events.Bookings.Event.EndDate","text");
			AssertEquals(eventDetails.get("attendees"),"Attendee value is not is not inherited from Master event","Events.Bookings.Event.ExpectedAttendees","value");
			AssertEquals(eventDetails.get("eventStatus"),"Status value is not inherited from Master event","Events.Bookings.Event.Status","value");
			AssertEquals(eventDetails.get("space"),"The Space value is not inherited from Master event","Events.Bookings.Event.FunctionSpace","value");
			Assert.assertTrue(eventDetails.get("rentalAmount").contains(element("Events.Bookings.Event.RentalAmount").getAttribute("value")),"The Rental Amount value is not inherited from Master event");
			AssertEquals(eventDetails.get("setUpTime"),"The Setuptime value is not inherited from Master event","Events.Bookings.Event.SetupTime","value");
			AssertEquals(eventDetails.get("tearDownTime"),"The TearDown Time value is not inherited from Master event","Events.Bookings.Event.TearDown","value");
			AssertEqualsIgnoreCase(rentalCode,"The Rental Code is not inherited from Master event","Events.Bookings.Event.RentalCode","value");
			
			/**
			 * #6 Select Event Type
			 */
			evntComp.selectEventType(eventMap.get("EventType"), eventMap.get("EventName"), eventMap.get("StartTime"), eventMap.get("EndTime"));
			
			/**
			 * #7 Select Start Time
			 */
			textBox("Events.Bookings.Event.StartTime",eventMap.get("StartTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.StartTime");
			Utils.waitForSpinnerToDisappear(20);
			String startTimeValue=verifyTimeFormat(eventMap.get("StartTimeValue"));
			AssertEquals(startTimeValue,"Start Time  is not in correct format","Events.Bookings.Event.StartTime","value");
			
			/**
			 * #8 Select End Time
			 */
			textBox("Events.Bookings.Event.EndTime",eventMap.get("EndTimeValue"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.EndTime");
			Utils.waitForSpinnerToDisappear(20);
			String endTimeValue=verifyTimeFormat(eventMap.get("EndTimeValue"));
			AssertEquals(endTimeValue,"End Time is not in correct format","Events.Bookings.Event.EndTime","title");
			
			/**
			 * #9 Click on Save and verify that SubEvent is created
			 */
			click("Events.Bookings.Event.Save",0,"presence");
			waitForSpinnerToDisappear(40);
			Wait(3000);
			Assert.assertTrue(isExists("Events.Bookings.Event.EventSearchScreen")," The application is not redirected to Event Search screen");
			try{
				if (isExists("Events.Bookings.Event.LinkToModifySearchCriteria",0,"")){
				click("Events.Bookings.Event.LinkToModifySearchCriteria",0,"");
				}
			}catch(Exception e){
				
			}
			Utils.clear("Events.Bookings.Event.EventId");
			Utils.tabKey("Events.Bookings.Event.EventId");
			waitForSpinnerToDisappear(40);
			
			Utils.click("Events.Bookings.Event.EventSearch",100,"clickable");
			waitForSpinnerToDisappear(40);
			Assert.assertTrue(driver.findElements(By.xpath("//*[contains(@data-ocid,'_LINK_')]/ancestor::tr/td[12]//*[contains(@title,'Sub Event')]")).get(0).isDisplayed(),"The newly created event is not displayed");
			logger.log(LogStatus.PASS, " Created an Sub Event from Event Search successfully " );
			System.out.println(" Created an Sub Event from Event Search successfully ");
			 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create sub event from Event Search " + e.getMessage());
			throw new Exception(e.getMessage());
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create sub event from Event Search " + e.getMessage());
			throw (e);
		}
	}
	
	/**
	 * This method searches an event from Date
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void searchEventFromDate(HashMap<String, String> eventMap) throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			eventMap.put("BlockId", blockDetails.get("finalBlockId"));
			eventMap.put("BlockName", blockDetails.get("finalBlockCode"));
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(20);
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			evntComp.fillEventDetails(eventMap);
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(50);
			Assert.assertTrue(element("Events.Bookings.SearchResults").isDisplayed());
			Utils.click("Events.Bookings", 1000, "presence");
			if(elements("Events.Bookings.Event.EventMenu").size()==0){
				Utils.click("Events.Bookings", 0, "presence");
			}
			Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
			Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
			waitForSpinnerToDisappear(40);
			if (elements("Events.Bookings.Event.LinkToModifySearchCriteria").size()>0){
				element("Events.Bookings.Event.LinkToModifySearchCriteria").click();
				waitForSpinnerToDisappear(40);
			}
			Utils.clear("Events.Bookings.Event.BlockID");
			Utils.tabKey("Events.Bookings.Event.BlockID");
			waitForSpinnerToDisappear(40);
			Utils.clear("Events.Bookings.Event.FromDate");
			Utils.tabKey("Events.Bookings.Event.FromDate");
			waitForSpinnerToDisappear(40);
			Utils.clear("Events.Bookings.Event.ToDate");
			Utils.tabKey("Events.Bookings.Event.ToDate");
			waitForSpinnerToDisappear(40);
			System.out.println("blobkstartDate"+blockDetails.get("finalStartDate"));
			textBox("Events.Bookings.Event.FromDate", blockDetails.get("finalStartDate").toString(),100,"presence");
			Utils.tabKey("Events.Bookings.Event.FromDate");
			waitForSpinnerToDisappear(40);
			textBox("Events.Bookings.Event.ToDate", blockDetails.get("finalStartDate").toString(),100,"presence");
			Utils.tabKey("Events.Bookings.Event.ToDate");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.Event.EventSearch",100,"presence");
			waitForSpinnerToDisappear(40);
			List<String> allEvents=Utils.getAllValuesFromTableBasedOnColumnName("Event ID");
			Assert.assertTrue(allEvents.size()>0,"Event search from Date Failed");
			List<String> allDates=Utils.getAllValuesFromTableBasedOnColumnName("Date");
			for (String eachDate :allDates){
				Assert.assertTrue(eachDate.contains(blockDetails.get("finalStartDate")),"Events outside of search crieteria displayed, hence failing the test case");
			}
								
			logger.log(LogStatus.PASS, " Event search from Date Passed successfully " );
			System.out.println(" Event search from Date Passed successfully ");
		 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Event search from Date Failed " + e.getMessage());
			throw new Exception(e.getMessage()); 
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Event search from Date Failed " + e.getMessage());
			throw (e); 
		}
	}
	
	/**
	 * This method searches an event from BlockId
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void searchEventFromBlockId(HashMap<String, String> eventMap) throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			eventMap.put("BlockId", blockDetails.get("finalBlockId"));
			eventMap.put("BlockName", blockDetails.get("finalBlockCode"));
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(20);
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			evntComp.fillEventDetails(eventMap);
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(50);
			Assert.assertTrue(element("Events.Bookings.SearchResults").isDisplayed());
			Utils.click("Events.Bookings", 1000, "presence");
			if(elements("Events.Bookings.Event.EventMenu").size()==0){
				Utils.click("Events.Bookings", 0, "presence");
			}
			Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
			Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
			waitForSpinnerToDisappear(40);
			if (elements("Events.Bookings.Event.LinkToModifySearchCriteria").size()>0){
				element("Events.Bookings.Event.LinkToModifySearchCriteria").click();
				waitForSpinnerToDisappear(40);
			}
			Utils.clear("Events.Bookings.Event.BlockID");
			Utils.tabKey("Events.Bookings.Event.BlockID");
			waitForSpinnerToDisappear(40);
			Utils.clear("Events.Bookings.Event.FromDate");
			Utils.tabKey("Events.Bookings.Event.FromDate");
			waitForSpinnerToDisappear(40);
			Utils.clear("Events.Bookings.Event.ToDate");
			Utils.tabKey("Events.Bookings.Event.ToDate");
			waitForSpinnerToDisappear(40);
			textBox("Events.Bookings.Event.BlockID", eventMap.get("BlockId"),100,"presence");
			Utils.click("Events.Bookings.Event.EventSearch",100,"presence");
			evntComp.updateXpathsForSingleResult();
			waitForSpinnerToDisappear(40);
			List<WebElement> allEvents=driver.findElements(By.xpath(evntComp.eventIdFromTable));
			Assert.assertTrue(allEvents.size()>0,"Event search from BlockId Failed");
			List<String> allBlocks=Utils.getAllValuesFromTableBasedOnColumnName("Block ID");
			for (String eachBlock :allBlocks){
				Assert.assertTrue(eachBlock.contains(eventMap.get("BlockId")),"Events outside of search crieteria displayed, hence failing the test case");
			}
								
			logger.log(LogStatus.PASS, " Event search from BlockId Passed successfully " );
			System.out.println(" Event search from BlockId Passed successfully ");
		 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Event search from BlockId Failed " + e.getMessage());
			throw new Exception(e.getMessage());
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Event search from BlockId Failed " + e.getMessage());
			throw (e);
		}
	}
	
	
	/**
	 * This method creates an event from a block
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void createEventFromBlock(HashMap<String, String> eventMap) throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			eventMap.put("PropertyName", blockDetails.get("property"));
			eventMap.put("StartDate",blockDetails.get("finalStartDate"));
			eventMap.put("EndDate",blockDetails.get("finalStartDate"));
			eventMap.put("Attendees",blockDetails.get("finalEventAttendees"));
			eventMap.put("Status",blockDetails.get("finalBlockStatus"));
			Utils.click("Events.Bookings", 1000, "presence");
			if(elements("Events.Bookings.Blocks").size()==0){
				Utils.click("Events.Bookings",0, "presence");
			}
			Utils.click("Events.Bookings.Blocks", 100, "presence");
			Utils.click("Events.Bookings.ManageBlocks", 100, "presence");
			waitForSpinnerToDisappear(40);
			Utils.textBox("Events.Bookings.Event.BlockID",blockDetails.get("finalBlockId"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.BlockID");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchButton",50,"presence");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(20);
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			click("Events.Bookings.CreateEvent",50,"presence");
			evntComp.verifyValuesOfBlock(eventMap);
			evntComp.selectEventType(eventMap.get("EventType"), eventMap.get("EventName"), eventMap.get("StartTime"), eventMap.get("EndTime"));
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(40);
			String blockIDfromEvent=Utils.getValueFromTableBasedOnColumnName("Block ID");
			Assert.assertEquals(blockIDfromEvent, blockDetails.get("finalBlockId"),"The event creation for block is not successful");
			
			logger.log(LogStatus.PASS, " Event from Block created successfully  " );
			System.out.println(" Event from Block created successfully ");
		 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, "Event could not be created from Block " + e.getMessage());
			throw new Exception(e.getMessage()); 
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Event could not be created from Block " + e.getMessage());
			throw (e); 
		}
	}
	
	/**
	 * This method Accesses Event Search via Block
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void accessEventSeachFromViaBlock(HashMap<String, String> eventMap) throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			Utils.scroll("down");
			Utils.Wait(1000);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(30);
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			evntComp.fillEventDetails(eventMap);
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(50);
			Utils.Wait(1000);
			String eventIdToBeSearched= Utils.getValueFromTableBasedOnColumnName("Event ID");
			Utils.click("Events.Bookings", 1000, "presence");
			if(elements("Events.Bookings.Blocks").size()==0){
				Utils.click("Events.Bookings",0, "presence");
			}
			Utils.click("Events.Bookings.Blocks", 100, "presence");
			Utils.Wait(1000);
			Utils.click("Events.Bookings.ManageBlocks", 100, "presence");
			waitForSpinnerToDisappear(40);
			Utils.textBox("Events.Bookings.Event.BlockID",blockDetails.get("finalBlockId"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.BlockID");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchButton",50,"presence");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(20);
			List<WebElement> showMore= elements("Events.Bookings.Events.ShowMore");
			if (showMore.size()>0){
				for (WebElement eachElement:showMore){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			
			click("Events.Bookings.ManageEvent",50,"presence");
			waitForSpinnerToDisappear(30);
			String searchedEventId=Utils.getValueFromTableBasedOnColumnName("Event ID");
			Assert.assertEquals(eventIdToBeSearched, searchedEventId, "Expected evnt could not be searched from Block ID");
			String blockIDfromEvent=Utils.getValueFromTableBasedOnColumnName("Block ID");
			Assert.assertEquals(blockIDfromEvent, blockDetails.get("finalBlockId"),"The event creation for block is not successful");
			click("Events.Bookings.Event.LinkToModifySearchCriteria",0,"");
			waitForSpinnerToDisappear(30);
			Utils.Wait(1000);
			String actualBlockIdPrefilled=Utils.getAttributeOfElement("Events.Bookings.Event.BlockID", "value", 0, "");
			Assert.assertEquals(blockDetails.get("finalBlockId"), actualBlockIdPrefilled, "Expected Block ID is not prefilled in teh Search criteria");
			logger.log(LogStatus.PASS, " Event search via Block passed successfully " );
			System.out.println(" Event search via Block passed successfully ");
		 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, "Event search via Block could not be processed " + e.getMessage());
			throw new Exception(e.getMessage()); 
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Event search via Block could not be processed " + e.getMessage());
			throw (e); 
		}
	}
	
	/**
	 * This method Accesses Event Presentation Screen
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void accessEventPresentationScreen(HashMap<String, String> eventMap) throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			eventMap.put("StartDate",blockDetails.get("finalStartDate"));
			eventMap.put("EndDate",blockDetails.get("finalStartDate"));
			eventMap.put("Attendees",blockDetails.get("finalEventAttendees"));
			eventMap.put("Status",blockDetails.get("finalBlockStatus"));
			Utils.tabKey("Events.Bookings.Event.BlockID");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchButton",50,"presence");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(30);
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			evntComp.fillEventDetails(eventMap);
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(50);
			Assert.assertTrue(element("Events.Bookings.SearchResults").isDisplayed());
			String eventIdToBeSearched= Utils.getValueFromTableBasedOnColumnName("Event ID");
			Utils.click("Events.Bookings", 1000, "presence");
			if (elements("Events.Bookings.Blocks").size()==0){
				Utils.click("Events.Bookings", 0, "presence");
			}
			Utils.Wait(2000);
			Utils.click("Events.Bookings.Blocks", 100, "presence");
			Utils.click("Events.Bookings.ManageBlocks", 100, "presence");
			waitForSpinnerToDisappear(40);
			Utils.textBox("Events.Bookings.Event.BlockID",blockDetails.get("finalBlockId"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.BlockID");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchButton",50,"presence");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			click("Events.Bookings.ManageEvent",50,"presence");
			waitForSpinnerToDisappear(30);
			String searchedEventId=Utils.getValueFromTableBasedOnColumnName("Event ID");
			Assert.assertEquals(eventIdToBeSearched, searchedEventId, "Expected evnt could not be searched from Block ID");
			evntComp.getElementTableBasedOnColumnName("Event ID").click();
			waitForSpinnerToDisappear(30);
			
			Assert.assertEquals(eventMap.get("StartDate"),elements("Events.Bookings.Event.StartDate").get(0).getText());
			logger.log(LogStatus.PASS, "Start Date is displayed as expected in Block Business Screen" + eventMap.get("StartDate"));
			Assert.assertEquals(eventMap.get("EndDate"),elements("Events.Bookings.Event.EndDate").get(0).getText());
			logger.log(LogStatus.PASS, "End Date is displayed as expected in Block Business Screen" + eventMap.get("EndDate"));
			Assert.assertEquals(eventMap.get("Attendees"),elements("Events.Bookings.Event.ExpectedAttendees").get(0).getText(),"Attendees value is not as per expectation");
			logger.log(LogStatus.PASS, "Attendees is displayed as expected in Block Business Screen" + eventMap.get("Attendees"));
			Assert.assertEquals(eventMap.get("Status"),elements("Events.Bookings.Event.Status").get(0).getText(),"Status value is not as per expectation");
			logger.log(LogStatus.PASS, "Status is displayed as expected in Block Business Screen" + eventMap.get("Status"));
			Assert.assertEquals(eventMap.get("Doorcard"),elements("Events.Bookings.Event.Doorcard").get(0).getText(),"Doorcard value is not as per expectation");
			logger.log(LogStatus.PASS, "Doorcard is displayed as expected in Block Business Screen" + eventMap.get("Doorcard"));
			Assert.assertEquals(eventMap.get("StartDate"),elements("Events.Bookings.Event.StartDate").get(1).getText());
			logger.log(LogStatus.PASS, "Start Date is displayed as expected in Event Details Screen" + eventMap.get("StartDate"));
			Assert.assertEquals(eventMap.get("EndDate"),elements("Events.Bookings.Event.EndDate").get(1).getText());
			logger.log(LogStatus.PASS, "End Date is displayed as expected in Event Details Screen" + eventMap.get("EndDate"));
			Assert.assertEquals(eventMap.get("Attendees"),elements("Events.Bookings.Event.ExpectedAttendees").get(1).getText(),"Attendees value is not as per expectation");
			logger.log(LogStatus.PASS, "Attendees is displayed as expected in Event Details Screen" + eventMap.get("Attendees"));
			Assert.assertEquals(eventMap.get("Status"),elements("Events.Bookings.Event.Status").get(1).getText(),"Status value is not as per expectation");
			logger.log(LogStatus.PASS, "Status is displayed as expected in Event Details Screen" + eventMap.get("Status"));
			Assert.assertEquals(eventMap.get("Doorcard"),elements("Events.Bookings.Event.Doorcard").get(1).getText(),"Doorcard value is not as per expectation");
			logger.log(LogStatus.PASS, "Doorcard is displayed as expected in Event Details Screen" + eventMap.get("Doorcard"));
			Assert.assertEquals(eventMap.get("EventName"),elements("Events.Bookings.Event.EventName").get(0).getText(),"EventName value is not as per expectation");
			logger.log(LogStatus.PASS, "EventName is displayed as expected in Event Details Screen" + eventMap.get("EventName"));
			Assert.assertEquals(eventMap.get("EventType"),elements("Events.Bookings.Event.EventType").get(0).getText(),"EventType value is not as per expectation");
			logger.log(LogStatus.PASS, "EventType is displayed as expected in Event Details Screen" + eventMap.get("EventType"));
			logger.log(LogStatus.PASS, " Access Event Presentation Screen passed successfully " );
			System.out.println(" Access Event Presentation Screen passed successfully ");
		 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, "Access Event Presentation Screen could not be processed " + e.getMessage());
			throw new Exception(e.getMessage()); 
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Access Event Presentation Screen could not be processed " + e.getMessage());
			throw (e); 
		}
	}
	
	/**
	 * This method Accesses Event Search via Block
	 * @author cpsinha
	 * @param eventMap
	 * @throws Exception
	 */
	public static void verifyFunctionSpaceResourceDetails(HashMap<String, String> eventMap) throws Exception,AssertionError {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		EventComponents evntComp = new EventComponents();
		HashMap<String, String> blockMap = new HashMap<String, String>();
		blockMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EventsData"), "NewBlock", "Block_testdata3");

		try {
			HashMap<String, String> blockDetails=evntComp.createNewBlock(blockMap);
			Utils.scroll("down");
			Utils.Wait(1000);
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(30);
			List<WebElement> elements= elements("Events.Bookings.Events.ShowMore");
			if (elements.size()>0){
				for (WebElement eachElement:elements){
					eachElement.click();
					waitForSpinnerToDisappear(20);
				}
			}
			evntComp.fillEventDetails(eventMap);
			evntComp.fillFunctionSpaceDetails(eventMap);
			Utils.scroll("down");
			Utils.Wait(1000);
			click("Events.Bookings.Event.Save",50,"presence");
			waitForSpinnerToDisappear(50);
			Utils.Wait(1000);
			String eventIdToBeSearched= Utils.getValueFromTableBasedOnColumnName("Event ID");
			Utils.click("Events.Bookings", 1000, "presence");
			if(elements("Events.Bookings.Blocks").size()==0){
				Utils.click("Events.Bookings",0, "presence");
			}
			Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
			Utils.Wait(1000);
			Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
			waitForSpinnerToDisappear(40);
			if (elements("Events.Bookings.Event.LinkToModifySearchCriteria").size()>0){
				element("Events.Bookings.Event.LinkToModifySearchCriteria").click();
				waitForSpinnerToDisappear(40);
			}
			Utils.clear("Events.Bookings.Event.FromDate");
			Utils.tabKey("Events.Bookings.Event.FromDate");
			waitForSpinnerToDisappear(40);
			Utils.clear("Events.Bookings.Event.ToDate");
			Utils.tabKey("Events.Bookings.Event.ToDate");
			waitForSpinnerToDisappear(40);
			Utils.textBox("Events.Bookings.Event.BlockID",blockDetails.get("finalBlockId"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.BlockID");
			waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.SearchButton",50,"presence");
			waitForSpinnerToDisappear(40);
			Utils.scroll("down");
			Utils.click("Events.Bookings.SearchResults",20,"presence");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.Events.EventResources",50,"presence");
			waitForSpinnerToDisappear(30);
			String order=Utils.getValueFromTableBasedOnColumnName("Order");
			String resources=Utils.getValueFromTableBasedOnColumnName("Resources");
			String setup=Utils.getValueFromTableBasedOnColumnName("Setup");
			String quantity=Utils.getValueFromTableBasedOnColumnName("Quantity");
			String unitPrice=Utils.getValueFromTableBasedOnColumnName("Unit Price");
			String rateCode=Utils.getValueFromTableBasedOnColumnName("Rate Code");
			String revenue=Utils.getValueFromTableBasedOnColumnName("Revenue");
			String discount=Utils.getValueFromTableBasedOnColumnName("Discount %");
			
			if (eventMap.get("Order").equalsIgnoreCase(order)){
				logger.log(LogStatus.PASS, " Order is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " Order validation failed for the resource " );
			}
			if (eventMap.get("Resources").equalsIgnoreCase(resources)){
				logger.log(LogStatus.PASS, " Resources is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " Resources validation failed for the resource " );
			}
			if (eventMap.get("Quantity").equalsIgnoreCase(quantity)){
				logger.log(LogStatus.PASS, " Quantity is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " Quantity validation failed for the resource " );
			}
			if (eventMap.get("UnitPrice").equalsIgnoreCase(unitPrice)){
				logger.log(LogStatus.PASS, " UnitPrice is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " UnitPrice validation failed for the resource " );
			}
			if (eventMap.get("Revenue").equalsIgnoreCase(revenue)){
				logger.log(LogStatus.PASS, " Revenue is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " Revenue validation failed for the resource " );
			}
			if (eventMap.get("RateCode").equalsIgnoreCase(rateCode)){
				logger.log(LogStatus.PASS, " RateCode is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " RateCode validation failed for the resource " );
			}
			if (discount.length()==1){
				logger.log(LogStatus.PASS, " Discount is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " Discount validation failed for the resource " );
			}
			if (eventMap.get("Setupstyle").equalsIgnoreCase(setup)){
				logger.log(LogStatus.PASS, " Setup is validated Successfully for the resource " );
			}else{
				logger.log(LogStatus.FAIL, " Setup validation failed for the resource " );
			}
			
			logger.log(LogStatus.PASS, " Function Space Resource details verified successfully " );
			System.out.println("  Function Space Resource details verified successfully ");
		 
		}catch (AssertionError e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Function Space Resource details failed " + e.getMessage());
			throw new Exception(e.getMessage()); 
		}catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, "  Function Space Resource details failed " + e.getMessage());
			throw (e); 
		}
	}
	
	
}
