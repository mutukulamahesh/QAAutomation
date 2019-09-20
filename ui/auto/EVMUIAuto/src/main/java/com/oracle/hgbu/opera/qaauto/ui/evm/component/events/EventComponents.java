package com.oracle.hgbu.opera.qaauto.ui.evm.component.events;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.itextpdf.text.Element;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;
import com.thoughtworks.selenium.webdriven.commands.GetTable;

public class EventComponents {
	String eventIdFromTable="";
	String selectedEventStartDate="";
	String selectedEventStatus="";
	String selectedEventStartTime="";
	String selectedEventEndTime="";
	String selectedEventEndDate="";
	String selectedEventAttendees="";
	String selectedEventSpace="";
	String selectedEventRentalAmount="";
	String selectedEventSetUpTime="";
	String selectedEventTearDownTime="";
	String selectedEventType="";
	String selectedEventName="";
	String selectedBlockId="";
	
	public void selectHub(HashMap<String, String> eventMap) throws IOException, Exception{
		if (eventMap.get("PropertyLevel").equals("Hub")) {
			Utils.click("Events.ShowUserOption", 0, "clickable");
			Utils.Wait(3000);
			Utils.click("Events.SelectLocation", 20, "visible");
			Utils.click("Events.PropertyRadioButton", 100, "clickable");
			Utils.isExists("Events.SelectPropertyButton");
			Utils.click("Events.SelectPropertyButton", 0, "presence");
			Utils.waitForPageLoad(100);
		}
	}
	
	public HashMap<String, String> searchExistingBlock(HashMap<String, String> eventMap) throws IOException, Exception{
		Utils.Wait(4000);
		Utils.click("Events.Bookings", 1000, "presence");
		Utils.click("Events.Bookings.Blocks", 100, "presence");
		Utils.click("Events.Bookings.ManageBlocks", 100, "presence");
		for (int i = 0; i < 5; i++) {
			if (!Utils.isExists(("Events.Bookings.BlockCode"))) {
				Utils.click("Events.Bookings", 1000, "presence");
				Utils.click("Events.Bookings.Blocks", 100, "presence");
				Utils.click("Events.Bookings.ManageBlocks", 100, "presence");
			}else{
				break;
			}
		}
		Utils.textBox("Events.Bookings.BlockCode",eventMap.get("BlockName"),50,"presence");
		Utils.tabKey("Events.Bookings.BlockCode");
		Utils.waitForSpinnerToDisappear(20);
		Utils.click("Events.Bookings.SearchButton",50,"presence");
		Utils.waitForSpinnerToDisappear(30);
		HashMap<String, String> blockDetails= fetchBlockDetails();
		Utils.Wait(2000);
		Utils.click("Events.Bookings.SearchResults",20,"presence");
		Utils.waitForSpinnerToDisappear(20);
		return blockDetails;
	}
	
	public HashMap<String, String> searchExistingBlockUsingBlockID(String blockID) throws IOException, Exception{
		Utils.Wait(4000);
		Utils.click("Events.Bookings", 1000, "presence");
		Utils.click("Events.Bookings.Blocks", 100, "presence");
		Utils.click("Events.Bookings.ManageBlocks", 100, "presence");
		Utils.waitForSpinnerToDisappear(40);
		for (int i = 0; i < 5; i++) {
			if (!Utils.isExists(("Events.Bookings.BlockCode"))) {
				Utils.click("Events.Bookings", 1000, "presence");
				Utils.click("Events.Bookings.Blocks", 100, "presence");
				Utils.click("Events.Bookings.ManageBlocks", 100, "presence");
			}else{
				break;
			}
		}
		Utils.textBox("Events.Bookings.Event.BlockID",blockID,50,"presence");
		Utils.tabKey("Events.Bookings.Event.BlockID");
		Utils.waitForSpinnerToDisappear(20);
		Utils.click("Events.Bookings.SearchButton",50,"presence");
		Utils.waitForSpinnerToDisappear(30);
		HashMap<String, String> blockDetails= fetchBlockDetails();
		Utils.Wait(2000);
		Utils.click("Events.Bookings.SearchResults",20,"presence");
		Utils.waitForSpinnerToDisappear(20);
		return blockDetails;
	}
	
	public void verifyValuesOfBlock(HashMap<String, String> eventMap) throws IOException, Exception, AssertionError{
		Assert.assertEquals(eventMap.get("StartDate"),Utils.getAttributeOfElement("Events.Bookings.Event.StartDate","value",0,"presence"));
		logger.log(LogStatus.PASS, "Start Date is displayed as expected" + eventMap.get("StartDate"));
		Utils.AssertEquals(eventMap.get("StartDate"),"Start Date value is not as per expectation","Events.Bookings.Event.StartDate","value");
		Utils.AssertEquals(eventMap.get("EndDate"),"End Date value is not as per expectation","Events.Bookings.Event.EndDate","text");
		logger.log(LogStatus.PASS, "End Date is displayed as expected" + eventMap.get("EndDate"));
		Utils.AssertEquals(eventMap.get("Attendees"),"Attendee value is not as per expectation","Events.Bookings.Event.ExpectedAttendees","value");
		logger.log(LogStatus.PASS, "Attendees is displayed as expected" + eventMap.get("Attendees"));
		Utils.AssertEquals(eventMap.get("Status"),"Status value is not as per expectation","Events.Bookings.Event.Status","value");
		logger.log(LogStatus.PASS, "Status is displayed as expected" + eventMap.get("Status"));
		Utils.AssertEquals(eventMap.get("Doorcard"),"Door value is not as per expectation","Events.Bookings.Event.Doorcard","value");
		logger.log(LogStatus.PASS, "Doorcard is displayed as expected" + eventMap.get("Doorcard"));
		Utils.AssertEquals(eventMap.get("PropertyName"),"Propertyname is not as per expectation","Events.Bookings.Event.PropertyName","value");
		logger.log(LogStatus.PASS, "PropertyName is displayed as expected" + eventMap.get("PropertyName"));
	}
	
	public void selectEventType(String eventType,String eventName, String startTime,String endTime) throws IOException, Exception,AssertionError{
		Utils.textBox("Events.Bookings.Event.EventType",eventType,50,"presence");
		Utils.click("Events.Bookings.Event.EventNameLabel",20,"visible");
		Utils.waitForSpinnerToDisappear(40);
		Utils.click("Events.Bookings.Event.EventNameLabel",20,"visible");
		Utils.AssertContains(eventName,"EventName value is not as per expectation","Events.Bookings.Event.EventName","value");
		Assert.assertTrue((Utils.element("Events.Bookings.Event.EndTime").getAttribute("placeholder")).contains(endTime),"EndTime value is not as per expectation");
		Assert.assertTrue((Utils.element("Events.Bookings.Event.StartTime").getAttribute("placeholder")).contains(startTime),"End Time value is not as per expectation");
	}
	
	public void clickManageEvent(String option) throws IOException, Exception,AssertionError{
		if(option.equalsIgnoreCase("exists")){
		for (int i = 0; i < 5; i++) {
			try{
			if (!Utils.isExists(("Events.Bookings.Event.EventId"))) {
				Utils.click("Events.Bookings", 1000, "presence");
				Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
				Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
				Utils.waitForSpinnerToDisappear(30);
			}else{
				break;
			}
			}catch(NoSuchElementException e){
				Utils.click("Events.Bookings", 1000, "presence");
				Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
				Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
				Utils.waitForSpinnerToDisappear(20);
			}catch(Exception e){
				Utils.click("Events.Bookings", 1000, "presence");
				Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
				Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
				Utils.waitForSpinnerToDisappear(30);
			}
			
		}
		}else if (option.equalsIgnoreCase("displayed")){
			Utils.Wait(4000);
			Utils.click("Events.Bookings", 1000, "presence");
			Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
			Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
			Utils.waitForSpinnerToDisappear(20);
			/*for (int i=0; i<3; i++){
				try{
				if (!Utils.element("Events.Bookings.Event.EventId").isDisplayed()){
					Utils.click("Events.Bookings", 1000, "presence");
					Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
					Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
					Utils.waitForSpinnerToDisappear(20);
				}else{
					break;
				}
				}catch(NoSuchElementException e){
					Utils.click("Events.Bookings", 1000, "presence");
					Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
					Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
					Utils.waitForSpinnerToDisappear(20);					
				}catch(Exception e){
					Utils.click("Events.Bookings", 1000, "presence");
					Utils.click("Events.Bookings.Event.EventMenu",100,"presence");
					Utils.click("Events.Bookings.Event.ManageEvents",100,"presence");
					Utils.waitForSpinnerToDisappear(20);
					
				}
			}*/
		}
	}
	
	public Integer getLatestEventIDNonMasterNonSubEvent() throws IOException, Exception,AssertionError{
		List<String> allEvents=Utils.getAllValuesFromTableBasedOnColumnName("Event ID");
		if (allEvents.size()>0){
		List<Integer> allEventIds= new ArrayList<Integer>();
		for (String event:allEvents){
			Integer eventID=Integer.parseInt(event);
			if (Utils.driver.findElements(By.xpath("//*[contains(@data-ocid,'_LINK_"+eventID+"')]/ancestor::tr/td[12]/span//img")).size()==0){
				allEventIds.add(eventID);
				break;
			}
		}
		Collections.sort(allEventIds);
		Integer eventID=allEventIds.get(allEventIds.size()-1);
		return eventID;
		}else{
			return null;
		}
		
	}
	public Integer getLatestEventID() throws IOException, Exception,AssertionError{
		List<String> allEvents=Utils.getAllValuesFromTableBasedOnColumnName("Event ID");
		if (allEvents.size()>0){
		List<Integer> allEventIds= new ArrayList<Integer>();
		for (String event:allEvents){
			Integer eventID=Integer.parseInt(event);
			allEventIds.add(eventID);
		}
		if (allEventIds.size()==0){
			throw new Exception("No events found");
		}
		Collections.sort(allEventIds);
		Integer eventID=allEventIds.get(allEventIds.size()-1);
		return eventID;
		}else{
			return null;
		}
	}
	
	public void updateXpathsForSingleResult() throws Exception, AssertionError{
		eventIdFromTable="//*[contains(@data-ocid,'_LINK_')]/ancestor::tr/td[3]";
		selectedEventStartDate="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[5]";
		selectedEventStatus="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[6]";
		selectedEventStartTime="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[9]";
		selectedEventEndTime="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[10]";
		selectedEventEndDate="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[19]";
		selectedEventAttendees="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[18]";
		selectedEventSpace="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[13]";
		selectedEventRentalAmount="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[14]";
		selectedEventSetUpTime="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[16]";
		selectedEventTearDownTime="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[17]";
		selectedEventType="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[8]";
		selectedEventName="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[12]";
		selectedBlockId="//*[contains(@data-ocid,'_TXT_CNTNT_ODEC_DT_IT')]/ancestor::tr/td[24]";
		
	}
	
	public void createBlock(HashMap<String, String> blockMap) throws Exception {
		try {
			HashMap<String, String> blockDetails=createNewBlock(blockMap);
			updateExcelSheet(blockDetails);

		}catch (AssertionError e) {
			logger.log(LogStatus.FAIL, " Could not create an Block " + e.getMessage());
			Assert.fail("Block could not be selected");
			throw (e);
		} 
		catch (Exception e) {
			logger.log(LogStatus.FAIL, " Could not create an Block " + e.getMessage());
			Assert.fail("Block could not be selected");
			throw (e);
		}
	}
	
	/**
	 * This function fills the details for events section while creating an event
	 * @throws Exception 
	 */
	public void fillEventDetails(HashMap<String, String> eventMap) throws Exception, AssertionError{
		try{
		
		/**
		 * Select Create Event 
		 */
		Utils.click("Events.Bookings.CreateEvent",50,"presence");
		Utils.waitForSpinnerToDisappear(40);

		/**
		 * Select Event Type
		 */
		selectEventType(eventMap.get("EventType"), eventMap.get("EventName"), eventMap.get("EndTime"), eventMap.get("StartTime"));
		
		/**
		 * Select Start Time
		 */
		Utils.textBox("Events.Bookings.Event.StartTime",eventMap.get("StartTimeValue"),50,"presence");
		Utils.click("Events.Bookings.Event.EventNameLabel",20,"visible");
		Utils.waitForSpinnerToDisappear(40);

		/**
		 * Select End Time
		 */
		Utils.textBox("Events.Bookings.Event.EndTime",eventMap.get("EndTimeValue"),50,"presence");
		Utils.click("Events.Bookings.Event.EventNameLabel",20,"visible");
		Utils.waitForSpinnerToDisappear(40);
		
	}catch(AssertionError e){
		logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
		Assert.fail("Event details could not be filled");
		throw (e);
	}catch(Exception e){
		logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
		Assert.fail("Event details could not be filled");
		throw (e);
	}
}
	
	/**
	 * This function fills the details for events section while creating an event
	 * @throws Exception 
	 */
	public void fillFunctionSpaceDetails(HashMap<String, String> eventMap) throws Exception, AssertionError{
		try{
		
			Utils.textBox("Events.Bookings.Event.FunctionSpace",eventMap.get("Space"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.FunctionSpace");
			Utils.waitForSpinnerToDisappear(40);
			/**
			 * Navigate to Rental Code and Select a value
			 */
			Utils.textBox("Events.Bookings.Event.RentalCode",eventMap.get("Rentalcode"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalCode");
			Utils.waitForSpinnerToDisappear(40);

			/**
			 *  Navigate to SetupStyle and select a value
			 */
			Utils.textBox("Events.Bookings.Event.SetupStyle",eventMap.get("Setupstyle"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.SetupStyle");
			Utils.waitForSpinnerToDisappear(40);			
			/**
			 *  Navigate to rental amount and enter a value
			 */
			Utils.textBox("Events.Bookings.Event.RentalAmount",eventMap.get("RentalAmount"),0,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalAmount");
			Utils.waitForSpinnerToDisappear(40);
			
					
	}catch(AssertionError e){
		logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
		Assert.fail("Event details could not be filled");
		throw (e);
	}catch(Exception e){
		logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
		Assert.fail("Event details could not be filled");
		throw (e);
	}
}
	
	public HashMap<String, String> createNewBlock(HashMap<String, String> blockMap) throws Exception, AssertionError{
		try{
			/**
			 * #1 Navigating to Block Creation page
			 */

			// Navigating to Bookings menu
			Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
			Utils.mouseHover("Events.Bookings");
			Utils.click(Utils.element("Events.Bookings"));
			System.out.println("clicked Booking Menu");
			logger.log(LogStatus.PASS, "Selected Booking Menu");
			if (Utils.elements("Events.Bookings.Blockslink").size()==0){
				Utils.click(Utils.element("Events.Bookings"));
			}

			// Navigating to Blocks menu
			Utils.WebdriverWait(100, "Events.Bookings.Blockslink", "clickable");
			Utils.click(Utils.element("Events.Bookings.Blockslink"));
			System.out.println("clicked Blocks menu");
			logger.log(LogStatus.PASS, "Selected Blocks menu");

			// Navigating to Manage Blocks menu
			Utils.WebdriverWait(100, "Events.Bookings.ManageBlocks", "clickable");
			Utils.click(Utils.element("Events.Bookings.ManageBlocks"));
			System.out.println("clicked manage Blocks");
			logger.log(LogStatus.PASS, "Selected Manage Blocks Menu");

			// Selecting I want to link in Manage Blocks Screen
			Utils.WebdriverWait(100, "Events.Bookings.IWantToCreateBlock", "clickable");
			Utils.click(Utils.element("Events.Bookings.IWantToCreateBlock"));
			System.out.println("Selected I Want to in manage Blocks..");
			logger.log(LogStatus.PASS, "Selected I Want to Link in Manage Blocks Page");

			// Selecting create Block link in I want to link
			Utils.WebdriverWait(100, "Events.Bookings.BlockslinkIwant", "clickable");
			Utils.click(Utils.element("Events.Bookings.BlockslinkIwant"), false);
			System.out.println("Selected create Block..");
			logger.log(LogStatus.PASS, "Selected Create Block in Manage Block Page");

			String dateformat = Utils.getAttributeOfElement("Events.Bookings.BlocksStartDate", "placeholder", 20,
					"visible");
			char ch[] = dateformat.toCharArray();
			for (int i = 0; i < dateformat.length(); i++) {
				if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') {
					if (ch[i] >= 'a' && ch[i] <= 'z') {
						ch[i] = (char) (ch[i] - 'a' + 'A');
					}
				} else if (ch[i] == 'D')
					ch[i] = (char) (ch[i] + 'a' - 'A');
			}
			String st = new String(ch);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
			String fromdate = simpleDateFormat.format(new Date());
			Utils.textBox("Events.Bookings.BlocksStartDate", fromdate, 50, "presence");
			Utils.click("Events.Bookings.Event.EndDate", 20, "visible");
			Utils.waitForSpinnerToDisappear(20);

			Utils.click("Events.Bookings.BlockName", 20, "visible");
			Utils.waitForSpinnerToDisappear(20);
			Utils.textBox("Events.Bookings.BlockName", blockMap.get("BlockName"), 50, "presence");

			Utils.click("Events.Bookings.Property", 20, "visible");
			Utils.waitForSpinnerToDisappear(20);
			String pattern = "HHmm";
			simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			Utils.clear("Events.Bookings.BlockCodeValue");
			String templateBlockCode = BlockPage.templateBlockCode(blockMap);
			Utils.textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
			Utils.waitForSpinnerToDisappear(20);
			Utils.click("Events.Bookings.Market", 20, "visible");
			Utils.waitForSpinnerToDisappear(20);
			String blockStatus="";
			if (Utils.isExists("Events.Bookings.RoomStatus")){
	            Utils.textBox("Events.Bookings.RoomStatus",blockMap.get("Status"),50,"presence");
				blockStatus="room";
			}
	        else if( Utils.isExists("Events.Bookings.BlockStatus")){
	            Utils.textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
				blockStatus="block";
	        }

			Utils.click("Events.Bookings.Market", 20, "visible");
			String blockCode = Utils.getAttributeOfElement("Events.Bookings.BlockCodeValue", "value", 20, "visible");
			System.out.println(blockCode);
			Utils.waitForSpinnerToDisappear(20);

			Utils.textBox("Events.Bookings.Market", blockMap.get("MarketType"), 50, "presence");
			Utils.click("Events.Bookings.Source", 20, "visible");
			Utils.waitForSpinnerToDisappear(20);
			Utils.textBox("Events.Bookings.Source", blockMap.get("Source"), 50, "presence");
			Utils.click("Events.Bookings.RateCode", 20, "visible");
			Utils.waitForSpinnerToDisappear(20);
			Utils.textBox("Events.Bookings.RateCode", blockMap.get("RateCode"), 50, "presence");
			Utils.click("Events.Bookings.EveAttendees", 20, "visible");
			Utils.waitForSpinnerToDisappear(20);
			Utils.textBox("Events.Bookings.EveAttendees", blockMap.get("EveAttendees"), 50, "presence");
			String attndNumber = Utils.getAttributeOfElement("Events.Bookings.EveAttendees", "value", 20, "visible");
			if (attndNumber == "")
				Utils.textBox("Events.Bookings.EveAttendees", blockMap.get("EveAttendees"), 50, "presence");
			Utils.waitForSpinnerToDisappear(20);
			String blockCodeval = Utils.getAttributeOfElement("Events.Bookings.BlockCodeValue", "value", 20, "visible");
			if (blockCodeval != blockCode) {
				Utils.clear("Events.Bookings.BlockCodeValue");
				Utils.waitForSpinnerToDisappear(20);
				Utils.textBox("Events.Bookings.BlockCodeValue", templateBlockCode, 50, "presence");
			}
			Utils.waitForSpinnerToDisappear(20);
			String uniquedate = "ddMMyyHHmmss";
			SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
			String screenshotval = uniquedateval1.format(new Date());
			Utils.driver.findElement(By.xpath("//*[@data-ocid='TXT_CNTNT_POSTAS']")).sendKeys("NewRoom");
			Utils.waitForSpinnerToDisappear(20);

			Utils.takeScreenshot("Events.Bookings.BlockSave" + screenshotval);

			String finalBlockCode = Utils.getAttributeOfElement("Events.Bookings.BlockCodeValue", "value", 0, "");
			String finalStartDate = Utils.getAttributeOfElement("Events.Bookings.BlocksStartDate", "value", 0, "");
			String finalEventAttendees = Utils.getAttributeOfElement("Events.Bookings.EveAttendees", "value", 0, "");
			String finalBlockStatus="";
			if (blockStatus.equals("room")){
				finalBlockStatus = Utils.getAttributeOfElement("Events.Bookings.RoomStatus", "value", 0, "");
			}else if(blockStatus.equals("block")){
				finalBlockStatus = Utils.getAttributeOfElement("Events.Bookings.BlockStatus", "value", 0, "");
			}

			Utils.click("Events.Bookings.BlockSave", 20, "visible");
			Utils.waitForSpinnerToDisappear(40);
			Utils.click("Events.Bookings.BlockCOdeHeader", 20, "visible");
			Utils.WebdriverWait(1000, "Events.Bookings.BlockCodeResults", "clickable");
			Utils.takeScreenshot("BlockCreated" + screenshotval);
			String finalBlockId = Utils.getValueFromTableBasedOnColumnName("Block ID");
			String property = Utils.getValueFromTableBasedOnColumnName("Property");
			HashMap<String, String> blockDetails= new HashMap<String,String>();
			blockDetails.put("finalBlockCode", finalBlockCode);
			blockDetails.put("finalStartDate", finalStartDate.trim().toString()); 
			blockDetails.put("finalEventAttendees", finalEventAttendees);
			blockDetails.put("finalBlockStatus", finalBlockStatus);
			blockDetails.put("finalBlockId", finalBlockId);
			blockDetails.put("property", property);
			
			return blockDetails;			
			
		}catch(AssertionError e){
			logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
			Assert.fail("Event details could not be filled");
			throw (e);
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
			Assert.fail("Event details could not be filled");
			throw (e);
		}
	}
	
	public void updateExcelSheet(HashMap<String, String> blockDetails) throws Exception, AssertionError{
		try{
			for (int i=1; i<=3;i++){
			ExcelUtils.setDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_"+i, "StartDate",
					blockDetails.get("finalStartDate"));
			ExcelUtils.setDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_"+i, "EndDate",
					blockDetails.get("finalStartDate"));
			ExcelUtils.setDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_"+i, "Attendees",
					blockDetails.get("finalEventAttendees"));
			ExcelUtils.setDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_"+i, "Status",
					blockDetails.get("finalBlockStatus"));
			ExcelUtils.setDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_"+i, "BlockName",
					blockDetails.get("finalBlockCode"));
			ExcelUtils.setDataByRow(OR.getConfig("Path_EventsData"), "NewEvent", "Dataset_"+i, "BlockId", blockDetails.get("finalBlockId"));
			}
			
		}catch(AssertionError e){
			logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
			Assert.fail("Event details could not be filled");
			throw (e);
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not fill event Details " + e.getMessage());
			Assert.fail("Event details could not be filled");
			throw (e);
		}
	}
	
	/**
	 * @author Chittranjan
	 * @description - This method is to activate or deactivate the Application Functions and Parameters.based on the value set against each function/parameter in test data this method will activate or deactivate a particular function.
	 * @param configMap - Will contain the test data required to activate or deactivate the application functions 
	 * @Date: 11/30/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */
	public void applicationFunctionsParametersSettings(String parameterType) throws Exception, AssertionError{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {

			// Navigating to Main Menu
			Utils.mouseHover("Configuration.mainMenu",100,"clickable");
			//click("Configuration.mainMenu",100,"clickable");
			Utils.jsClick("Configuration.mainMenu");
			System.out.println("***** Clicked on Main Menu *****");
			logger.log(LogStatus.PASS, "Clicked on Main Menu");

			// Navigating to Administration Menu
			Utils.mouseHover("Configuration.AdminstrationMenu",100,"clickable");
			Utils.click("Configuration.AdminstrationMenu",100,"clickable");

			// Navigating to Enterprise Menu
			//WebdriverWait(100,"Configuration.administration.MenuEnterprise","presence");
			Utils.mouseHover("Configuration.administration.MenuEnterprise",100,"clickable");
			Utils.click("Configuration.administration.MenuEnterprise",100,"clickable");
			
			// Navigating to OPERA Controls
			//mouseHover("Configuration.administration.MenuEnterprise.OperaControls",100,"clickable");
			Utils.jsClick("Configuration.administration.MenuEnterprise.OperaControls");
			System.out.println("***** Clicked on OperaControls Menu *****");
			logger.log(LogStatus.PASS, "Clicked on OperaControls Menu");

			//Validate if the control in on the correct page
			Utils.isDisplayed("Configuration.OperaControls.groupSelection", "Opera Controls Group");

			//Select the group Corresponding to the Application Function
			java.util.List<WebElement>  lstGroups = Utils.elements("Configuration.OperaControls.groupSelection"); 
			System.out.println("No of groups are : " + lstGroups.size());
			
			for (WebElement eachGroup:lstGroups){
				if(eachGroup.getText().equalsIgnoreCase("Events")){
					//((JavascriptExecutor) Utils.driver).executeScript("arguments[0].scrollIntoView(true);", eachGroup);
					Utils.scroll("down");
					Utils.Wait(2000);
					eachGroup.click();
					Utils.waitForSpinnerToDisappear(20);
					break;
				}
			}
			Utils.click("Events.Configuration.PMSFunctions.EditDefaultSubEventCode", 0, "");		
			Utils.waitForSpinnerToDisappear(20);
			//Set the data for the function attribute
			if (parameterType.equalsIgnoreCase("null")){
				Utils.clear("Events.Configuration.PMSFunctions.txtDefaultSubEventRateCode");
			}else if(parameterType.equalsIgnoreCase("Custom")){
				Utils.clear("Events.Configuration.PMSFunctions.txtDefaultSubEventRateCode");
				Utils.textBox("Events.Configuration.PMSFunctions.txtDefaultSubEventRateCode", parameterType.toUpperCase());
			}
			Utils.tabKey("Events.Configuration.PMSFunctions.txtDefaultSubEventRateCode");
			Utils.waitForSpinnerToDisappear(30);
			Utils.click("Events.Configuration.PMSFunctions.btnDefaultSubEventRateCodeSave");
			Utils.waitForSpinnerToDisappear(20);
			//Click on Save button
		}catch(AssertionError e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}
	
	/**
	 * This method redirects to Opera Cloud
	 * @throws Exception
	 */
	public void redirectToCloud() throws Exception, AssertionError{
		Utils.mouseHover("Configuration.mainMenu",100,"clickable");
		Utils.jsClick("Configuration.mainMenu");
		Utils.mouseHover("Events.Configuration.OperaCloud",100,"clickable");
		Utils.click("Events.Configuration.OperaCloud",100,"clickable");
		Utils.waitForSpinnerToDisappear(45);
		Utils.Wait(3000);
	}
	
	/**
	 * This method searches for an existing event.
	 * @throws Exception 
	 */
	public void searchEventsBasedOnBlock(HashMap<String,String> eventMap) throws Exception, AssertionError{
		Utils.textBox("Events.Bookings.Event.BlockID", eventMap.get("BlockId"),100,"presence");
		Utils.clear("Events.Bookings.Event.FromDate");
		Utils.tabKey("Events.Bookings.Event.FromDate");
		Utils.waitForSpinnerToDisappear(40);
		Utils.clear("Events.Bookings.Event.ToDate");
		Utils.tabKey("Events.Bookings.Event.ToDate");
		Utils.waitForSpinnerToDisappear(40);
		Utils.click("Events.Bookings.Event.EventSearch",100,"presence");
		Utils.waitForSpinnerToDisappear(30);
	}
	
	/**
	 * This method searches for events based on EventId
	 * @throws Exception 
	 */
	public void searchEventsBasedOnEventId(Integer eventID) throws Exception, AssertionError{
		Utils.WebdriverWait(100, "Events.Bookings.Event.EventId", "clickable");
		Utils.textBox("Events.Bookings.Event.EventId", eventID.toString(), 0, "clickable");
		Utils.clear("Events.Bookings.Event.FromDate");
		Utils.tabKey("Events.Bookings.Event.FromDate");
		Utils.waitForSpinnerToDisappear(40);
		Utils.clear("Events.Bookings.Event.ToDate");
		Utils.tabKey("Events.Bookings.Event.ToDate");
		Utils.waitForSpinnerToDisappear(40);
		Utils.click("Events.Bookings.Event.EventSearch",100,"clickable");
		Utils.waitForSpinnerToDisappear(20);
	}
	
	/**
	 * This method finds out the latest event ID for the search results
	 * @throws Exception 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public Integer getRecentEventId() throws IOException, InterruptedException, Exception, AssertionError{
		Integer latestEventID=0;
		if (Utils.elements("Events.ManageEvents.EnterOrModifySearchCriteria").size() >0) {
			latestEventID=0;
		} else {
			for (int i = 0; i < 20; i++) {
				try {
					Assert.assertTrue(Utils.element("Events.Bookings.EventSearchResults").isDisplayed(), "The results for Events is not displayed");
					break;
				} catch (AssertionError e) {
					Utils.Wait(1000);
				}

			}
			try {
				latestEventID = getLatestEventID();
			} catch (Exception e) {

			}
		}
		return latestEventID;
	}
	
	/**
	 * This method fetches the Event and details for an event
	 * @author cpsinha
	 * @throws Exception
	 */
	public HashMap<String, String> fetchEventDetails()throws Exception{
		String startDate= Utils.getValueFromTableBasedOnColumnName("Date");
		String eventStatus= Utils.getValueFromTableBasedOnColumnName("Status");
		String endDate=Utils.getValueFromTableBasedOnColumnName("End Date");
		String attendees= Utils.getValueFromTableBasedOnColumnName("Attendees");
		String space= Utils.getValueFromTableBasedOnColumnName("Space");
		String startTime= Utils.getValueFromTableBasedOnColumnName("Start Time");
		String rentalAmount= Utils.getValueFromTableBasedOnColumnName("Rental");
		String setUpTime= Utils.getValueFromTableBasedOnColumnName("Setup Time");
		String tearDownTime= Utils.getValueFromTableBasedOnColumnName("Tear Down Time");
		String eventType= Utils.getValueFromTableBasedOnColumnName("Type");
		String eventName= Utils.getValueFromTableBasedOnColumnName("Name");
		String eventID= Utils.getValueFromTableBasedOnColumnName("Event ID");
		HashMap<String, String> eventDetails= new HashMap<String, String>();
		eventDetails.put("startDate",startDate);
		eventDetails.put("eventStatus",eventStatus);
		eventDetails.put("endDate",endDate);
		eventDetails.put("space",space);
		eventDetails.put("startTime",startTime);
		eventDetails.put("rentalAmount",rentalAmount);
		eventDetails.put("tearDownTime",tearDownTime);
		eventDetails.put("eventType",eventType);
		eventDetails.put("eventName",eventName);
		eventDetails.put("eventID",eventID);
		eventDetails.put("attendees",attendees);
		eventDetails.put("setUpTime",setUpTime);
		return eventDetails;
		
	}
	
	/**
	 * This method fetches all details of a block
	 * @author cpsinha
	 * @throws Exception
	 */
	public HashMap<String, String> fetchBlockDetails() throws Exception{
		HashMap<String, String> blockDetails= new HashMap<String, String>();
		String property= Utils.getValueFromTableBasedOnColumnName("Property");
		String blockCode= Utils.getValueFromTableBasedOnColumnName("Block Code");
		String blockName=Utils.getValueFromTableBasedOnColumnName("Block Name");
		String startDate= Utils.getValueFromTableBasedOnColumnName("Start Date");
		String endDate= Utils.getValueFromTableBasedOnColumnName("End Date");
		String blockId= Utils.getValueFromTableBasedOnColumnName("Block ID");
		String blockStatus= Utils.getValueFromTableBasedOnColumnName("Status");
		blockDetails.put("property",property);
		blockDetails.put("blockCode",blockCode);
		blockDetails.put("blockName",blockName);
		blockDetails.put("startDate",startDate);
		blockDetails.put("endDate",endDate);
		blockDetails.put("blockId",blockId);
		blockDetails.put("blockStatus",blockStatus);
		return blockDetails;
	}
	
	/**
	 * This method clicks on Save Event
	 * @param option
	 * @throws Exception 
	 * @throws IOException 
	 */
	public void saveEvent(String option) throws IOException, Exception{
		if (option.equalsIgnoreCase("SaveAndCreateAnotherEvent")){
		Utils.click("Events.Bookings.Event.SaveAndCreateAnotherEvent",50,"presence");	
		Utils.Wait(2000);
		Utils.waitForSpinnerToDisappear(60);
		for (int i = 0; i < 20; i++) {
			try {
				Assert.assertTrue(Utils.element("Events.Bookings.Event.EventType").getAttribute("value").equals(""));
			} catch (AssertionError e) {
				Utils.Wait(1000);
			}catch(Exception j){
				Utils.Wait(1000);
			}
		}
		}
	}
	
	/**
	 * Description This method fetches PropertyDate
	 * @return propertyDate
	 * @throws Exception 
	 * @throws IOException 
	 * @throws ParseException, Exception 
	 */
	public String getPropertyDate() throws IOException, Exception{
		String[] getBusinessDateText=Utils.element("Events.Bookings.Event.BusinessDate").getText().split(",");
		String tempDate= getBusinessDateText[1].trim()+","+getBusinessDateText[2];
		String businessDate=Utils.convertDateInReqiuredFormat("MMMM dd, yyyy","MM-dd-yyyy", tempDate);
		return businessDate;
	}
	
	public String generateCharacter(){
        String chars = "ABCDEFGHIJKLMNPQRSTUVWXYZ";
        Random rnd = new Random();
        char c = chars.charAt(rnd.nextInt(chars.length()));
        String cString= Character.toString(c);
        return cString;
    }
    
    public String generateBlockCode(){
        String blockCode= generateCharacter()+generateCharacter()+generateCharacter()+generateCharacter()+generateCharacter()+generateCharacter();
        return blockCode;
    }
    
	/**
	 * This method returns table value based on column name
	 * @author cpsinha
	 */

	public WebElement getElementTableBasedOnColumnName(String columnName)throws Exception{
		String columnNumber= Utils.driver.findElement(By.xpath("//span[text()='"+columnName+"']/ancestor::th")).getAttribute("data-ocid");
		WebElement element=Utils.driver.findElements(By.xpath("//td[contains(@data-ocid,'"+columnNumber+"')]")).get(0);
		return element;		
	}
	
	public void fillSpaceDetails(HashMap<String, String> eventMap) throws Exception{
			Utils.textBox("Events.Bookings.Event.FunctionSpace",eventMap.get("Space"),50,"presence");		
			Utils.tabKey("Events.Bookings.Event.FunctionSpace");
			Utils.waitForSpinnerToDisappear(40);
			Utils.textBox("Events.Bookings.Event.RentalCode",eventMap.get("Rentalcode"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalCode");
			Utils.waitForSpinnerToDisappear(40);
			Utils.textBox("Events.Bookings.Event.SetupStyle",eventMap.get("Setupstyle"),50,"presence");
			Utils.tabKey("Events.Bookings.Event.SetupStyle");
			Utils.waitForSpinnerToDisappear(40);
			Utils.textBox("Events.Bookings.Event.RentalAmount",eventMap.get("RentalAmount"),0,"presence");
			Utils.tabKey("Events.Bookings.Event.RentalAmount");
			Utils.waitForSpinnerToDisappear(40);
		}
}
