package com.oracle.hgbu.opera.qaauto.ui.evm.component.events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;
import com.thoughtworks.selenium.webdriven.commands.IsElementPresent;

import bsh.ParseException;

/**
 * @Description This class provides the methods for creating events
 * @author MVamsi
 *
 */
public class BlockPage extends Utils {

	public static void createBusinessBlock(HashMap<String, String> blockMap) throws Exception {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		
		try {
			/**
			 * #1 Navigating to Block Creation page
			 */
			
			
			// Navigating to Bookings menu
			Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
			Utils.mouseHover("Events.Bookings");
			Utils.click(Utils.element("Events.Bookings"));
			System.out.println("clicked Booking Menu");
			logger.log(LogStatus.PASS, "Selected Booking Menu");

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

			Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
//			clear("Events.Bookings.Property");
//			textBox("Events.Bookings.Property",blockMap.get("PropertyName"),50,"presence");
			click("Events.Bookings.BlocksStartDate", 20, "visible");
			String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
			char ch[] = dateformat.toCharArray(); 
			for (int i = 0; i < dateformat.length(); i++) {
				if (ch[i] == 'D')  
	                ch[i] = (char)(ch[i] + 'a' - 'A');             
	        
			else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
		                if (ch[i] >= 'a' && ch[i] <= 'z') { 
		                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
		                } 
		            } 
			}
		        String st = new String(ch); 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
			String fromdate = simpleDateFormat.format(new Date());
			textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
			click("Events.Bookings.Event.EndDate", 20, "visible");
			Calendar cal = Calendar.getInstance();
	    	System.out.println("Current Date: "+simpleDateFormat.format(cal.getTime()));
	    	//Adding 1 Day to the current date
	    	cal.add(Calendar.DAY_OF_MONTH, 1);  
	    	//Date after adding one day to the current date
	    	String newDate = simpleDateFormat.format(cal.getTime());  
	    	//Displaying the new Date after addition of 1 Day
	    	System.out.println("Incremnted current date by one: "+newDate);
	    	textBox("Events.Bookings.Event.EndDate",newDate,50,"presence");
			waitForSpinnerToDisappear(20);
			
			click("Events.Bookings.BlockName", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
			
			click("Events.Bookings.Property", 20, "visible");
			waitForSpinnerToDisappear(20);
			String pattern = "HHmm";
			simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			clear("Events.Bookings.BlockCodeValue");
			String templateBlockCode = templateBlockCode(blockMap);
			textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.Market", 20, "visible");
			waitForSpinnerToDisappear(20);
			
			if (isExists("Events.Bookings.RoomStatus"))
				textBox("Events.Bookings.RoomStatus",blockMap.get("Status"),50,"presence");
			else if( isExists("Events.Bookings.BlockStatus"))
				textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
			
			click("Events.Bookings.Market", 20, "visible");
			String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			System.out.println(blockCode);
			waitForSpinnerToDisappear(20);
			
			textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
			click("Events.Bookings.Source", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
			click("Events.Bookings.RateCode", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
			click("Events.Bookings.EveAttendees", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
			String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
			if (attndNumber=="")
				textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
			waitForSpinnerToDisappear(20);
			String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			if (blockCodeval!=blockCode){
				clear("Events.Bookings.BlockCodeValue");
				waitForSpinnerToDisappear(20);
				textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
			}
			waitForSpinnerToDisappear(20);
			String uniquedate = "ddMMyyHHmmss";
			SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
			String screenshotval = uniquedateval1.format(new Date());
			
//			Utils.takeScreenshot("Events.Bookings.BlockSave" + screenshotval);
			String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			click("Events.Bookings.BlockSave", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.BlockCOdeHeader", 20, "visible");
			Utils.WebdriverWait(1000, "Events.Bookings.BlockCodeResults", "clickable");
//			Utils.takeScreenshot("BlockCreated"+ screenshotval);
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
			Assert.fail("Block could not be selected");
			throw (e);
		}
	}

	public static void editBusinessBlockDetails(HashMap<String, String> blockMap) throws Exception {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		
		try {
			/**
			 * #1 Navigating to Block Creation page
			 */
			// Navigating to Bookings menu
			Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
			Utils.mouseHover("Events.Bookings");
			Utils.click(Utils.element("Events.Bookings"));
			System.out.println("clicked Booking Menu");
			logger.log(LogStatus.PASS, "Selected Booking Menu");

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

			// Selecting the required block from search results
			
			click("Events.Bookings.BlockName", 20, "visible");
			click("Events.Bookings.BlockCodeValue", 20, "visible");
			
			waitForSpinnerToDisappear(20);
			clear("Events.Bookings.EndDateFrom");
			click("Events.Bookings.BlockCodeValue", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
			waitForSpinnerToDisappear(20);
			if (isExists("Events.Bookings.RoomStatus"))
				textBox("Events.Bookings.RoomStatus","INQ",50,"presence");
			else if( isExists("Events.Bookings.BlockStatus"))
				textBox("Events.Bookings.BlockStatus","INQ",50,"presence");
			click("Events.Bookings.BlockCodeValue", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.SearchButton", 20, "visible");
			waitForSpinnerToDisappear(20);
			Utils.WebdriverWait(100, "Events.Bookings.BlockCOdeHeader", "clickable");
			clickOnElementBasedOnTextContains("Events.Bookings.BlockCodeResults", StringUtils.substring( blockMap.get("BlockCode"), 0, 3),"data-ocid");
			click("Events.Bookings.linkEditBlock", 20, "visible");
			System.out.println("Done");
			
			Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
//			textBox("Events.Bookings.Property",blockMap.get("PropertyName"),50,"presence");
			click("Events.Bookings.BlocksStartDate", 20, "visible");
			String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
			char ch[] = dateformat.toCharArray(); 
			for (int i = 0; i < dateformat.length(); i++) {
				if (ch[i] == 'D')  
	                ch[i] = (char)(ch[i] + 'a' - 'A');             
	        
			else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
		                if (ch[i] >= 'a' && ch[i] <= 'z') { 
		                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
		                } 
		            } 
			}
		        String st = new String(ch); 
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		        
		        Calendar cal = Calendar.getInstance();
		    	System.out.println("Current Date: "+simpleDateFormat.format(cal.getTime()));
		    	//Adding 1 Day to the current date
		    	cal.add(Calendar.DAY_OF_MONTH, 1);  
		    	//Date after adding one day to the current date
		    	String newDate = simpleDateFormat.format(cal.getTime());  
		    	//Displaying the new Date after addition of 1 Day
		    	System.out.println("Incremnted current date by one: "+newDate);
		    	
			textBox("Events.Bookings.BlocksStartDate",newDate,50,"presence");
			click("Events.Bookings.Event.EndDate", 20, "visible");
			
			waitForSpinnerToDisappear(2000);
			
			String nightCont = getAttributeOfElement("Events.Bookings.TxtNightCounts","value",20,"visible");
			String endDateVal = getText("Events.Bookings.Event.EndDate");
			clear("Events.Bookings.TxtNightCounts");
			String str  = String.valueOf(Integer.parseInt(nightCont)+1);
			 
			textBox("Events.Bookings.TxtNightCounts",str,50,"presence");
			click("Events.Bookings.Event.EndDate", 20, "visible");
			waitForSpinnerToDisappear(20);
			String endDateValNew = getText("Events.Bookings.Event.EndDate");
			if (endDateValNew.equalsIgnoreCase(endDateVal)){
				logger.log(LogStatus.FAIL, "End Date is NOT changed successfully");
			}
			else{
				logger.log(LogStatus.PASS, "End Date is changed successfully");
			}
			click("Events.Bookings.lnkSaveEdit", 20, "visible");
			waitForSpinnerToDisappear(20);
			Assert.assertTrue(driver.getPageSource().contains(endDateValNew));
			System.out.println("End Date Verified");
		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
			Assert.fail("Block could not be selected");
			throw (e);
		}
	}

public static void blocksRoomRateCodesEditAdd(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	
	try {
		/**
		 * #1 Navigating to Block Creation page
		 */
		// Navigating to Bookings menu
		Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
		Utils.mouseHover("Events.Bookings");
		Utils.click(Utils.element("Events.Bookings"));
		System.out.println("clicked Booking Menu");
		logger.log(LogStatus.PASS, "Selected Booking Menu");

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

		Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
//		clear("Events.Bookings.Property");
//		textBox("Events.Bookings.Property",blockMap.get("PropertyName"),50,"presence");
		click("Events.Bookings.BlocksStartDate", 20, "visible");
		String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
		char ch[] = dateformat.toCharArray(); 
		for (int i = 0; i < dateformat.length(); i++) {
			if (ch[i] == 'D')  
                ch[i] = (char)(ch[i] + 'a' - 'A');             
        
		else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
	                if (ch[i] >= 'a' && ch[i] <= 'z') { 
	                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
	                } 
	            } 
		}
	        String st = new String(ch); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		String fromdate = simpleDateFormat.format(new Date());
		textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);

		String nightCont = getAttributeOfElement("Events.Bookings.TxtNightCounts","value",20,"visible");
//		String endDateVal = getText("Events.Bookings.Event.EndDate");
		clear("Events.Bookings.TxtNightCounts");
		String str  = String.valueOf(Integer.parseInt(nightCont)+1);
		 
		textBox("Events.Bookings.TxtNightCounts",str,50,"presence");
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		
		click("Events.Bookings.Property", 20, "visible");
		waitForSpinnerToDisappear(20);
		String pattern = "HHmm";
		simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String templateBlockCode = templateBlockCode(blockMap);
		textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.Market", 20, "visible");
		waitForSpinnerToDisappear(20);
//		textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
		if (isExists("Events.Bookings.RoomStatus"))
			textBox("Events.Bookings.RoomStatus",blockMap.get("Status"),50,"presence");
		else if( isExists("Events.Bookings.BlockStatus"))
			textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
		
		click("Events.Bookings.Market", 20, "visible");
		String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		System.out.println(blockCode);
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
		click("Events.Bookings.Source", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
		click("Events.Bookings.RateCode", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
		if (attndNumber=="")
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		waitForSpinnerToDisappear(20);
		String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		if (blockCodeval!=blockCode){
			clear("Events.Bookings.BlockCodeValue");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		}
		waitForSpinnerToDisappear(20);
		String uniquedate = "ddMMyyHHmmss";
		SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
		String screenshotval = uniquedateval1.format(new Date());
		
//		Utils.takeScreenshot("Events.Bookings.lnkSaveandRoomGrid" + screenshotval);
		String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");

		click("Events.Bookings.lnkSaveandRoomGrid", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(100, "Events.Bookings.linkEditBlock", "clickable");
		click("Events.Bookings.linkEditBlock", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(100, "Events.Bookings.dateRadiobtn", "clickable");
		click("Events.Bookings.dateRadiobtn", 20, "visible");
		waitForSpinnerToDisappear(20);
		scroll("down");
		Utils.WebdriverWait(100, "Events.Bookings.detailsStampLink", "clickable");
		click("Events.Bookings.detailsStampLink", 20, "clickable");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.edtOccupancyRooms",blockMap.get("OccupRooms"),50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkSaveEdit", 20, "visible");
		waitForSpinnerToDisappear(20);
		Assert.assertTrue(driver.getPageSource().contains("The changes are saved successfully"));

	} catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}

public static void blocksStatusChangeTenDefCan(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	
	try {
		/**
		 * #1 Navigating to Block Creation page
		 */
		// Navigating to Bookings menu
		Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
		Utils.mouseHover("Events.Bookings");
		Utils.click(Utils.element("Events.Bookings"));
		System.out.println("clicked Booking Menu");
		logger.log(LogStatus.PASS, "Selected Booking Menu");

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
	
		
		String blockCodefn = blockCodeCreate(blockMap);
		
		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
		Utils.WebdriverWait(1000, "Events.Bookings.BlockCodeResults", "clickable");
//		Utils.takeScreenshot("BlockCreated"+ screenshotval);
		click("Events.Bookings.BlockName", 20, "visible");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		
		waitForSpinnerToDisappear(20);
		clear("Events.Bookings.EndDateFrom");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.BlockCode",blockCodefn,50,"presence");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.SearchButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(100, "Events.Bookings.BlockCOdeHeader", "clickable");
		clickOnElementBasedOnTextContains("Events.Bookings.BlockCodeResults", StringUtils.substring( blockMap.get("BlockCode"), 0, 3),"data-ocid");
		waitForPageLoad(20);
		
		Utils.WebdriverWait(100, "Events.Bookings.IWantToCreateBlock", "clickable");
		
		String roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
		String cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
		if (roomStatus.equalsIgnoreCase("INQ") && cateringStatus.equalsIgnoreCase("INQ")  ){
			logger.log(LogStatus.PASS, "Selected a Block with INQ as status for Rooms as well as catering");
		}else{
			logger.log(LogStatus.FAIL, "NOT Selected a Block with INQ as status for Rooms as well as catering");
		}
		click("Events.Bookings.IWantToCreateBlock", 20, "visible");
		waitForSpinnerToDisappear(200);
		click("Events.Bookings.changeBlockStatus", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickOnElementBasedOnText("Events.Bookings.statusRadioBtn",blockMap.get("ChangeStatusVal1"));
		waitForSpinnerToDisappear(20);
		clickWaitforSpinner("Events.Bookings.lovNameReservationStatusChange", 20, "visible");
		textBox("Events.Bookings.edtSearchResStatus","4PM",50,"presence");
		clickWaitforSpinner("Events.Bookings.SearchButton", 20, "visible");
		
		clickWaitforSpinner("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
		Utils.tabKey("Events.Bookings.lnkLOVOriginCodeSelect");
		Utils.WebdriverWait(100, "Events.SelectPropertyButton", "clickable");
		clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
		Utils.tabKey("Events.Bookings.lnkSaveEdit");
		Utils.WebdriverWait(100, "Events.Bookings.lnkSaveEdit", "presence");
		click("Events.Bookings.lnkSaveEdit", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.btnYes", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(100, "Events.Bookings.flemRoomStatus", "presence");
		waitForPageLoad(20);
		roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
		cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
		if (roomStatus.equalsIgnoreCase("TEN") && cateringStatus.equalsIgnoreCase("TEN")  ){
			logger.log(LogStatus.PASS, "Block with TEN as status for Rooms as well as catering");
			System.out.println("Block with TEN as status for Rooms as well as catering");
		}else{
			logger.log(LogStatus.FAIL, "NOT Selected a Block with TEN as status for Rooms as well as catering");
			System.out.println("NOT Selected a Block with TEN as status for Rooms as well as catering");
		}
		click("Events.Bookings.IWantToCreateBlock", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.changeBlockStatus", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickOnElementBasedOnText("Events.Bookings.statusRadioBtn",blockMap.get("ChangeStatusVal2"));
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkSaveEdit", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.btnYes", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(100, "Events.Bookings.flemRoomStatus", "presence");
		waitForPageLoad(20);
		roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
		cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
		if (roomStatus.equalsIgnoreCase("DEF") && cateringStatus.equalsIgnoreCase("DEF")  ){
			logger.log(LogStatus.PASS, "Block with DEF as status for Rooms as well as catering");
			System.out.println("Block with DEF as status for Rooms as well as catering");
		}else{
			logger.log(LogStatus.FAIL, "NOT Selected a Block with DEF as status for Rooms as well as catering");
			System.out.println("NOT Selected a Block with DEF as status for Rooms as well as catering");
		}
		click("Events.Bookings.IWantToCreateBlock", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.changeBlockStatus", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickOnElementBasedOnText("Events.Bookings.statusRadioBtn",blockMap.get("ChangeStatusVal3"));
		waitForSpinnerToDisappear(20);
		
//		textBox("Events.Bookings.blockCancelReason",blockMap.get("blockCancelReason"),50,"presence");
		
		click("Events.Bookings.lovBlockCancelCode", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickWaitforSpinner("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
		clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
		
		click("Events.Bookings.blockCancelDdescription", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.blockCancelDdescription",blockMap.get("blockCancelDdescription"),50,"presence");
		Utils.WebdriverWait(100, "Events.Bookings.chkboxcancelPMReservation", "presence");
		
		if (isSelected("Events.Bookings.chkboxcancelPMReservation","PM reservations"))
			click("Events.Bookings.chkboxcancelPMReservation", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.lnkSaveEdit", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.btnYes", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(100, "Events.Bookings.flemRoomStatus", "presence");
		waitForPageLoad(20);
		waitForSpinnerToDisappear(20);
		roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
		cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
		if (roomStatus.contains("CAN") && cateringStatus.contains("CAN")  ){
			logger.log(LogStatus.PASS, "Block with CAN as status for Rooms as well as catering");
			System.out.println("Block with CAN as status for Rooms as well as catering");
		}else{
			logger.log(LogStatus.FAIL, "NOT Selected a Block with CAN as status for Rooms as well as catering");
			System.out.println("NOT Selected a Block with CAN as status for Rooms as well as catering");
		}
		
	} catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}

public static void createBlockWithTraceCodeLOV(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	
	try {
		/**
		 * #1 Navigating to Block Creation page
		 */
		// Navigating to Bookings menu
		Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
		Utils.mouseHover("Events.Bookings");
		Utils.click(Utils.element("Events.Bookings"));
		System.out.println("clicked Booking Menu");
		logger.log(LogStatus.PASS, "Selected Booking Menu");

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

		Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
//		clear("Events.Bookings.Property");
//		textBox("Events.Bookings.Property",blockMap.get("PropertyName"),50,"presence");
		click("Events.Bookings.BlocksStartDate", 20, "visible");
		String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
		char ch[] = dateformat.toCharArray(); 
		for (int i = 0; i < dateformat.length(); i++) {
			if (ch[i] == 'D')  
                ch[i] = (char)(ch[i] + 'a' - 'A');             
        
		else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
	                if (ch[i] >= 'a' && ch[i] <= 'z') { 
	                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
	                } 
	            } 
		}
	        String st = new String(ch); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		String fromdate = simpleDateFormat.format(new Date());
		textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
		click("Events.Bookings.Event.EndDate", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		
		click("Events.Bookings.Property", 20, "visible");
		waitForSpinnerToDisappear(20);
		String pattern = "HHmm";
		simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String templateBlockCode = templateBlockCode(blockMap);
		textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.Market", 20, "visible");
		waitForSpinnerToDisappear(20);
//		textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
		if (isExists("Events.Bookings.RoomStatus"))
			textBox("Events.Bookings.RoomStatus",blockMap.get("Status"),50,"presence");
		else if( isExists("Events.Bookings.BlockStatus"))
			textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
		
		click("Events.Bookings.Market", 20, "visible");
		String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		System.out.println(blockCode);
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
		click("Events.Bookings.Source", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
		click("Events.Bookings.RateCode", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.lnkLOVTraceCode", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickOnElementBasedOnText("Events.Bookings.searchSelectTraceCOde",blockMap.get("TraceCode"));
		waitForSpinnerToDisappear(20);
		click("Events.SelectPropertyButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
		if (attndNumber=="")
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		waitForSpinnerToDisappear(20);
		String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		if (blockCodeval!=blockCode){
			clear("Events.Bookings.BlockCodeValue");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		}
		waitForSpinnerToDisappear(20);
		String uniquedate = "ddMMyyHHmmss";
		SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
		String screenshotval = uniquedateval1.format(new Date());
		
//		Utils.takeScreenshot("Events.Bookings.BlockSave" + screenshotval);
		String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		click("Events.Bookings.BlockSave", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
		Utils.WebdriverWait(1000, "Events.Bookings.BlockCodeResults", "clickable");
//		Utils.takeScreenshot("BlockCreated"+ screenshotval);
		
	} catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}


public static String createBlockMandatoryNonMandatoryFields(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	
	try {
		/**
		 * #1 Navigating to Block Creation page
		 */
		// Navigating to Bookings menu
		Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
		Utils.mouseHover("Events.Bookings");
		Utils.click(Utils.element("Events.Bookings"));
		System.out.println("clicked Booking Menu");
		logger.log(LogStatus.PASS, "Selected Booking Menu");

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
		Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
		click("Events.Bookings.BlocksStartDate", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkLOVAccount", 20, "visible");
		waitForSpinnerToDisappear(20);
		driver.switchTo().frame(3);
		click("Events.Bookings.btnSearch");
		waitForSpinnerToDisappear(20);
		JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
		String currentFrame = (String) jsExecutor.executeScript("return self.name");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.btnSelectAccountContact", 20, "clickable");
		waitForSpinnerToDisappear(20);
		String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
		char ch[] = dateformat.toCharArray(); 
		for (int i = 0; i < dateformat.length(); i++) {
			if (ch[i] == 'D')  
                ch[i] = (char)(ch[i] + 'a' - 'A');             
        
		else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
	                if (ch[i] >= 'a' && ch[i] <= 'z') { 
	                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
	                } 
	            } 
		}
	        String st = new String(ch); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		String fromdate = simpleDateFormat.format(new Date());
		textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.DecisionDate",fromdate,50,"presence");
		click("Events.Bookings.FollowUpDate", 20, "visible");
		waitForSpinnerToDisappear(20);
		String dateformat1 = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
		char ch1[] = dateformat1.toCharArray(); 
		for (int i = 0; i < dateformat1.length(); i++) {
	            if (i == 0 && ch1[i] != ' ' || ch1[i] != ' ' && ch1[i - 1] == ' ') { 
	                if (ch1[i] >= 'a' && ch1[i] <= 'z') { 
	                    ch1[i] = (char)(ch1[i] - 'a' + 'A'); 
	                } 
	            } 
	            else if (ch1[i] == 'D')  
	                ch1[i] = (char)(ch1[i] + 'a' - 'A');             
	        } 
	        String st1 = new String(ch1); 
	        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(st);
	        
	        Calendar cal1 = Calendar.getInstance();
	        Calendar cal2 = Calendar.getInstance();
	    	System.out.println("Current Date: "+simpleDateFormat1.format(cal1.getTime()));
	    	//Adding 1 Day to the current date
	    	cal1.add(Calendar.DAY_OF_MONTH, 2);
	    	cal2.add(Calendar.DAY_OF_MONTH, 1);  
	    	//Date after adding one day to the current date
	    	String newDate1 = simpleDateFormat1.format(cal1.getTime());
	    	String newDate2 = simpleDateFormat1.format(cal2.getTime());  
	    	//Displaying the new Date after addition of 1 Day
	    	System.out.println("Incremnted current date by one: "+newDate1);
		textBox("Events.Bookings.FollowUpDate",newDate1,50,"presence");
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		click("Events.Bookings.Property", 20, "visible");
		waitForSpinnerToDisappear(20);
		String pattern = "HHmm";
		simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String templateBlockCode = templateBlockCode(blockMap);
		textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.Market", 20, "visible");
		waitForSpinnerToDisappear(20);
		if (isExists("Events.Bookings.RoomStatus"))
			textBox("Events.Bookings.RoomStatus",blockMap.get("Status"),50,"presence");
		else if( isExists("Events.Bookings.BlockStatus"))
			textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
		click("Events.Bookings.Market", 20, "visible");
		String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		System.out.println(blockCode);
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkLOVOrigin", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.SelectPropertyButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		if (isExists("Events.Bookings.lnkLOVType")){
			click("Events.Bookings.lnkLOVType", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.SelectPropertyButton", 20, "visible");
			waitForSpinnerToDisappear(20);
		}
		textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
		click("Events.Bookings.Source", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
		click("Events.Bookings.RateCode", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.txtCutOffDate",newDate2,50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		scroll("up");
		click("Events.Bookings.lnkLOVTraceCode", 20, "clickable");
		waitForSpinnerToDisappear(20);
		clickOnElementBasedOnText("Events.Bookings.searchSelectTraceCOde",blockMap.get("TraceCode"));
		waitForSpinnerToDisappear(20);
		click("Events.SelectPropertyButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.onsiteName",blockMap.get("BlockName"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.functionType",blockMap.get("BlockName"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.txtpostAs",blockMap.get("BlockName"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
		if (attndNumber=="")
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		waitForSpinnerToDisappear(20);
		String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		if (blockCodeval!=blockCode){
			clear("Events.Bookings.BlockCodeValue");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		}
		waitForSpinnerToDisappear(20);
		String uniquedate = "ddMMyyHHmmss";
		SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
		String screenshotval = uniquedateval1.format(new Date());
//		Utils.takeScreenshot("Events.Bookings.BlockSave" + screenshotval);
		String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		click("Events.Bookings.BlockSave", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(1000, "Events.Bookings.BlockCodeResults", "clickable");
		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
		scroll("down");
//		Utils.takeScreenshot("BlockCreated"+ screenshotval);
		return blockCodefn;
		
	} catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}


public static void searchSelectBlockFeature(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	String blockCode=createBlockMandatoryNonMandatoryFields(blockMap);
	try {
		/**
		 * #1 Navigating to Block Creation page
		 */
		// Navigating to Bookings menu
		Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
		Utils.mouseHover("Events.Bookings");
		Utils.click(Utils.element("Events.Bookings"));
		System.out.println("clicked Booking Menu");
		logger.log(LogStatus.PASS, "Selected Booking Menu");

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

		// Selecting the required block from search results
		
		click("Events.Bookings.BlockName", 20, "visible");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		
		waitForSpinnerToDisappear(20);
		clear("Events.Bookings.EndDateFrom");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockCodeValue",blockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.SearchButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickWaitforSpinner("Events.Bookings.lnkViewTable", 20, "visible");
		Utils.WebdriverWait(100, "Events.Bookings.BlockCOdeHeader", "clickable");
		clickOnElementBasedOnTextContains("Events.Bookings.BlockCodeResults", blockCode,"data-ocid");
		Utils.WebdriverWait(100, "Events.Bookings.linkEditBlock", "visible");
		Assert.assertTrue(driver.getPageSource().contains(blockCode));
		
	} catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}


public static void accountProfileAttachCreateBlock(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	try {
		/**
		 * #1 Navigating to Block Creation page
		 */
		// Navigating to Bookings menu
		Utils.WebdriverWait(1000, "Events.Bookings", "clickable");
		Utils.mouseHover("Events.Bookings");
		Utils.click(Utils.element("Events.Bookings"));
		System.out.println("clicked Booking Menu");
		logger.log(LogStatus.PASS, "Selected Booking Menu");

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
		Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
		click("Events.Bookings.BlocksStartDate", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkLOVAccount", 20, "visible");
		waitForSpinnerToDisappear(20);
		driver.switchTo().frame(3);
		click("Events.Bookings.btnSearch");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.btnSelectAccountOnly", 20, "clickable");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkLOVContact", 20, "visible");
		waitForSpinnerToDisappear(20);
		driver.switchTo().frame(3);
		click("Events.Bookings.SearchButton", 20, "visible");
		JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
		String currentFrame = (String) jsExecutor.executeScript("return self.name");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.btnSelectContactOnly", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
		char ch[] = dateformat.toCharArray(); 
		for (int i = 0; i < dateformat.length(); i++) {
			if (ch[i] == 'D')  
                ch[i] = (char)(ch[i] + 'a' - 'A');             
			 else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
	                if (ch[i] >= 'a' && ch[i] <= 'z') { 
	                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
	                } 
	            } 
		}
	        String st = new String(ch); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		String fromdate = simpleDateFormat.format(new Date());
		textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
		click("Events.Bookings.Event.EndDate", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		
		click("Events.Bookings.Property", 20, "visible");
		waitForSpinnerToDisappear(20);
		String pattern = "HHmm";
		simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String templateBlockCode = templateBlockCode(blockMap);
		textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.Market", 20, "visible");
		waitForSpinnerToDisappear(20);
		if (isExists("Events.Bookings.RoomStatus"))
			textBox("Events.Bookings.RoomStatus",blockMap.get("Status"),50,"presence");
		else if( isExists("Events.Bookings.BlockStatus"))
			textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
		
//		textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
		
		click("Events.Bookings.Market", 20, "visible");
		String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		System.out.println(blockCode);
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
		click("Events.Bookings.Source", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
		click("Events.Bookings.RateCode", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
		if (attndNumber=="")
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		waitForSpinnerToDisappear(20);
		String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		if (blockCodeval!=blockCode){
			clear("Events.Bookings.BlockCodeValue");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		}
		waitForSpinnerToDisappear(20);
		String uniquedate = "ddMMyyHHmmss";
		SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
		String screenshotval = uniquedateval1.format(new Date());
		String txtContact = getAttributeOfElement("Events.Bookings.txtContact","value",20,"visible");
		String txtAccount = getAttributeOfElement("Events.Bookings.txtAccount","value",20,"visible");
		
//		Utils.takeScreenshot("Events.Bookings.SaveAndManageBlock" + screenshotval);
		String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		click("Events.Bookings.SaveAndManageBlock", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.WebdriverWait(100, "Events.Bookings.linkEditBlock", "clickable");
		
		Assert.assertTrue(driver.getPageSource().contains(txtAccount));
		Assert.assertTrue(driver.getPageSource().contains(blockCodefn));
//		Utils.takeScreenshot("BlockCreated"+ screenshotval);
		
	} catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}

public static String createMasterSubBlock(HashMap<String, String> blockMap, String SyncCheckBox) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	
	try {
		/**
		 * #1 Navigating to Block Creation page
		 */
		System.out.println("In the metod");
		// Navigating to Bookings menu
		click("Events.Bookings", 20, "clickable");
		// Navigating to Blocks menu
		click("Events.Bookings.Blockslink", 20, "clickable");

		// Navigating to Manage Blocks menu
		click("Events.Bookings.ManageBlocks", 20, "clickable");

		// Selecting I want to link in Manage Blocks Screen
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.IWantToCreateBlock", 20, "clickable");

		// Selecting master block link in I want to link
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.MasterBlocklink", 20, "clickable");

		// Providing email in Profile creating screen Thread.sleep(2000);
		Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");

		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		Utils.isSelected("Events.Bookings.MasterSubSynchronize", "Synchronize checkbox by default");
//		String SyncCheckBox = "ON";
		if (SyncCheckBox.equalsIgnoreCase("ON")){
			click("Events.Bookings.MasterSubSynchronize", 20, "visible");
			click("Events.Bookings.BlockName", 20, "visible");
			waitForSpinnerToDisappear(20);
		}
		click("Events.Bookings.BlocksStartDate", 20, "visible");
		String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
		char ch[] = dateformat.toCharArray(); 
		for (int i = 0; i < dateformat.length(); i++) {
			if (ch[i] == 'D')  
                ch[i] = (char)(ch[i] + 'a' - 'A');             
        
			else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
	                if (ch[i] >= 'a' && ch[i] <= 'z') { 
	                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
	                } 
	            } 
		}
	        String st = new String(ch); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		String fromdate = simpleDateFormat.format(new Date());
		textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
		click("Events.Bookings.Event.EndDate", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		
		click("Events.Bookings.Property", 20, "visible");
		waitForSpinnerToDisappear(20);
		String pattern = "HHmm";
		simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String templateBlockCode = templateBlockCode(blockMap);
		textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		
		clear("Events.Bookings.MasterSubBlockCode");
		textBox("Events.Bookings.MasterSubBlockCode",blockMap.get("SubBlockCode")+date,50,"presence");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.Market", 20, "visible");
		String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		System.out.println(blockCode);
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
		click("Events.Bookings.Source", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
		if (attndNumber=="")
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		
		String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		if (blockCodeval!=blockCode){
			clear("Events.Bookings.BlockCodeValue");
			textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
			waitForSpinnerToDisappear(20);
			clear("Events.Bookings.MasterSubBlockCode");
			textBox("Events.Bookings.MasterSubBlockCode",blockMap.get("SubBlockCode")+date,50,"presence");
		}
		waitForSpinnerToDisappear(20);
		String uniquedate = "ddMMyyHHmmss";
		SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
		String screenshotval = uniquedateval1.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		String subBlockCodefn = blockMap.get("SubBlockCode")+date;
		
//		Utils.takeScreenshot("Events.Bookings.SaveAndManageBlock" + screenshotval);
//		Save and manage block selected
		click("Events.Bookings.SaveAndManageBlock", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlocksoverView", 20, "visible");
//		Utils.takeScreenshot("Block Presentation Screen" + screenshotval);
		isExists("Events.Bookings.MasterBlockLabel");
		
		click("Events.Bookings.MasterSubBlockLink", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
//		Utils.takeScreenshot("Master Sub Block Page"+ screenshotval);
		clickOnElementBasedOnText("Events.Bookings.SubBlockCodeResults", subBlockCodefn);
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlocksoverView", 20, "visible");
//		Utils.takeScreenshot("Block Presentation Screen" + screenshotval);
		isExists("Events.Bookings.SubBlockLabel");
		if (!SyncCheckBox.equalsIgnoreCase("ON")){
			isNotDisplayed("Events.Bookings.linkEditBlock","Edit in sub block");
		}
		click("Events.Bookings.MasterSubBlockLink", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
//		Utils.takeScreenshot("Master Sub Block Page"+ screenshotval);
		return blockCodefn;
//		return subBlockCodefn;
		
	} catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}


public static void createMasterAllocationBlockMain(HashMap<String, String> blockMap) throws Exception {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
		
		try {
			/**
			 * #1 Navigating to Block Creation page
			 */
			System.out.println("In the metod");
			// Navigating to Bookings menu
			click("Events.Bookings", 20, "clickable");
			// Navigating to Blocks menu
			click("Events.Bookings.Blockslink", 20, "clickable");
		
			// Navigating to Manage Blocks menu
			click("Events.Bookings.ManageBlocks", 20, "clickable");
		
			// Selecting I want to link in Manage Blocks Screen
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.IWantToCreateBlock", 20, "clickable");
		
			// Selecting master block link in I want to link
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.lnkMasterAllocationBlock", 20, "clickable");
		
			// Providing email in Profile creating screen Thread.sleep(2000);
			Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
		
			click("Events.Bookings.BlockName", 20, "visible");
			waitForSpinnerToDisappear(20);
			
			click("Events.Bookings.BlocksStartDate", 20, "visible");
			String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
			char ch[] = dateformat.toCharArray(); 
			for (int i = 0; i < dateformat.length(); i++) {
				if (ch[i] == 'D')  
		            ch[i] = (char)(ch[i] + 'a' - 'A');             
		    
				else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
		                if (ch[i] >= 'a' && ch[i] <= 'z') { 
		                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
		                } 
		            } 
			}
		        String st = new String(ch); 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
			String fromdate = simpleDateFormat.format(new Date());
			textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
			click("Events.Bookings.Event.EndDate", 20, "visible");
			waitForSpinnerToDisappear(20);
			
			click("Events.Bookings.BlockName", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
			
			click("Events.Bookings.Property", 20, "visible");
			waitForSpinnerToDisappear(20);
			String pattern = "HHmm";
			simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			clear("Events.Bookings.BlockCodeValue");
			String templateBlockCode = templateBlockCode(blockMap);
			textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.Market", 20, "visible");
			String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			System.out.println(blockCode);
			waitForSpinnerToDisappear(20);
			
			textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
			click("Events.Bookings.Source", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
			
			String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			if (blockCodeval!=blockCode){
				clear("Events.Bookings.BlockCodeValue");
				waitForSpinnerToDisappear(20);
				textBox("Events.Bookings.BlockCodeValue",templateBlockCode,50,"presence");
			}
			waitForSpinnerToDisappear(20);
			String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			click("Events.Bookings.BlockSave", 20, "visible");
			waitForSpinnerToDisappear(200);
			clickOnElementBasedOnTextContains("Events.Bookings.BlockCodeResults", StringUtils.substring( blockMap.get("BlockCode"), 0, 3),"data-ocid");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.BlocksoverView", 20, "visible");
			isExists("Events.Bookings.MasterSubBlockLink");
			
			click("Events.Bookings.IWantToCreateBlock", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.lnkSubAllocation", 20, "visible");
			
			creSubAlloBlock(blockMap);
			
			String blockCodefna = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			click("Events.Bookings.SaveAndManageBlock", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.MasterSubBlockLink", 20, "visible");
			waitForSpinnerToDisappear(20);
			
			if (driver.getPageSource().contains(blockCodefna)){ 
					System.out.println("***** Element "+ blockCodefna + " is present on the screen "+ "*****");
					logger.log(LogStatus.PASS, "Element "+ blockCodefna + " is present on the screen ");
				} else {
					System.out.println("***** Element "+ blockCodefna + " is NOT present on the screen "+ "*****");
					logger.log(LogStatus.FAIL, "Element "+ blockCodefna + " is NOT present on the screen ");
				}
			if (driver.getPageSource().contains(blockCodefn)){ 
				System.out.println("***** Element "+ blockCodefn + " is present on the screen "+ "*****");
				logger.log(LogStatus.PASS, "Element "+ blockCodefn + " is present on the screen ");
			} else {
				System.out.println("***** Element "+ blockCodefn + " is NOT present on the screen "+ "*****");
				logger.log(LogStatus.FAIL, "Element "+ blockCodefn + " is NOT present on the screen ");
			}
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create a block" + e.getMessage());
			Assert.fail("Block could not be selected");
			throw (e);
		}
}

public static void ChangeBlock_CancelReason(HashMap<String, String> blockMap) throws Exception {
String testClassName = Utils.getClassName();
String methodName = Utils.getMethodName();
System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

try {
	/**
	 * #1 Navigating to Block Creation page
	 */
	System.out.println("In the metod");
	// Navigating to Bookings menu
	click("Events.Bookings", 20, "clickable");
	// Navigating to Blocks menu
	click("Events.Bookings.Blockslink", 20, "clickable");
	// Navigating to Manage Blocks menu
	click("Events.Bookings.ManageBlocks", 20, "clickable");
	// Selecting I want to link in Manage Blocks Screen
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.IWantToCreateBlock", 20, "clickable");
	// Selecting create Block link in I want to link
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.BlockslinkIwant", 20, "clickable");
	Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
	click("Events.Bookings.BlocksStartDate", 20, "visible");
	String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
	char ch[] = dateformat.toCharArray(); 
	for (int i = 0; i < dateformat.length(); i++) {
		if (ch[i] == 'D')  
            ch[i] = (char)(ch[i] + 'a' - 'A');             
    
	else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
                if (ch[i] >= 'a' && ch[i] <= 'z') { 
                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
                } 
            } 
	}
    String st = new String(ch); 
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
	String fromdate = simpleDateFormat.format(new Date());
	textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
	click("Events.Bookings.Event.EndDate", 20, "visible");
	Calendar cal = Calendar.getInstance();
	System.out.println("Current Date: "+simpleDateFormat.format(cal.getTime()));
	//Adding 1 Day to the current date
	cal.add(Calendar.DAY_OF_MONTH, 1);  
	//Date after adding one day to the current date
	String newDate = simpleDateFormat.format(cal.getTime());  
	//Displaying the new Date after addition of 1 Day
	System.out.println("Incremnted current date by one: "+newDate);
	textBox("Events.Bookings.Event.EndDate",newDate,50,"presence");
	waitForSpinnerToDisappear(20);
	
	click("Events.Bookings.BlockName", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
	
	click("Events.Bookings.Property", 20, "visible");
	waitForSpinnerToDisappear(20);
	String pattern = "MMdd";
	simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	clear("Events.Bookings.BlockCodeValue");
	String ranstr = generateBlockCode();
	String blockCodevalran = StringUtils.substring( blockMap.get("BlockCode"), 0, 3)+ranstr+date;
	textBox("Events.Bookings.BlockCodeValue",blockCodevalran,50,"presence");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.Market", 20, "visible");
	waitForSpinnerToDisappear(20);
	
	if (isExists("Events.Bookings.RoomStatus"))
		textBox("Events.Bookings.RoomStatus","TEN",50,"presence");
	else if( isExists("Events.Bookings.BlockStatus"))
		textBox("Events.Bookings.BlockStatus","TEN",50,"presence");
	
	click("Events.Bookings.Market", 20, "visible");
	String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
	System.out.println(blockCode);
	waitForSpinnerToDisappear(20);
	
	textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
	click("Events.Bookings.Source", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
	click("Events.Bookings.RateCode", 20, "visible");
	waitForSpinnerToDisappear(20);
	
	
	textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
	click("Events.Bookings.EveAttendees", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
	scroll("up");
		click("Events.Bookings.LovReservationType", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.searchSelectTraceCOde", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.SelectPropertyButton", 20, "visible");
		waitForSpinnerToDisappear(20);
	String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
	if (attndNumber=="")
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
	waitForSpinnerToDisappear(20);
	String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
	if (blockCodeval!=blockCode){
		clear("Events.Bookings.BlockCodeValue");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockCodeValue",blockCodevalran,50,"presence");
	}
	waitForSpinnerToDisappear(20);
	String uniquedate = "ddMMyyHHmmss";
	SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
	String screenshotval = uniquedateval1.format(new Date());
	
//	Utils.takeScreenshot("Events.Bookings.BlockSave" + screenshotval);
	String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
	click("Events.Bookings.BlockSave", 20, "visible");
	waitForSpinnerToDisappear(30);
	click("Events.Bookings.BlockCOdeHeader", 20, "visible");
	Utils.WebdriverWait(1000, "Events.Bookings.BlockCodeResults", "clickable");
//	Utils.takeScreenshot("BlockCreated"+ screenshotval);
	click("Events.Bookings.BlockName", 20, "visible");
	click("Events.Bookings.BlockCodeValue", 20, "visible");
	
	waitForSpinnerToDisappear(20);
	clear("Events.Bookings.EndDateFrom");
	click("Events.Bookings.BlockCodeValue", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.BlockCode",blockCodefn,50,"presence");
	waitForSpinnerToDisappear(20);
	
	click("Events.Bookings.SearchButton", 20, "visible");
	waitForSpinnerToDisappear(20);
	Utils.WebdriverWait(100, "Events.Bookings.BlockCOdeHeader", "clickable");
	clickOnElementBasedOnTextContains("Events.Bookings.BlockCodeResults", StringUtils.substring( blockMap.get("BlockCode"), 0, 3),"data-ocid");
	waitForPageLoad(20);
	String roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
	String cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
	if (roomStatus.equalsIgnoreCase("TEN") && cateringStatus.equalsIgnoreCase("TEN")  ){
		logger.log(LogStatus.PASS, "Block with TEN as status for Rooms as well as catering");
		System.out.println("Block with TEN as status for Rooms as well as catering");
	}else{
		logger.log(LogStatus.FAIL, "NOT Selected a Block with TEN as status for Rooms as well as catering");
		System.out.println("NOT Selected a Block with TEN as status for Rooms as well as catering");
	}
	click("Events.Bookings.IWantToCreateBlock", 20, "visible");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.changeBlockStatus", 20, "visible");
	waitForSpinnerToDisappear(20);
	clickOnElementBasedOnText("Events.Bookings.statusRadioBtn",blockMap.get("ChangeStatusVal2"));
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.lnkSaveEdit", 20, "visible");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.btnYes", 20, "visible");
	waitForSpinnerToDisappear(20);
	Utils.WebdriverWait(100, "Events.Bookings.flemRoomStatus", "presence");
	waitForPageLoad(20);
	
	roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
	cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
	if (roomStatus.equalsIgnoreCase("DEF") && cateringStatus.equalsIgnoreCase("DEF")  ){
		logger.log(LogStatus.PASS, "Block with DEF as status for Rooms as well as catering");
		System.out.println("Block with DEF as status for Rooms as well as catering");
	}else{
		logger.log(LogStatus.FAIL, "NOT Selected a Block with DEF as status for Rooms as well as catering");
		System.out.println("NOT Selected a Block with DEF as status for Rooms as well as catering");
	}
	click("Events.Bookings.IWantToCreateBlock", 20, "visible");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.changeBlockStatus", 20, "visible");
	waitForSpinnerToDisappear(20);
	clickOnElementBasedOnText("Events.Bookings.statusRadioBtn",blockMap.get("ChangeStatusVal3"));
	waitForSpinnerToDisappear(20);
	
//	textBox("Events.Bookings.blockCancelReason",blockMap.get("blockCancelReason"),50,"presence");
	click("Events.Bookings.lovBlockCancelCode", 20, "visible");
	waitForSpinnerToDisappear(20);
	clickWaitforSpinner("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
	clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
	
	click("Events.Bookings.blockCancelDdescription", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.blockCancelDdescription",blockMap.get("blockCancelDdescription"),50,"presence");
	if (isSelected("Events.Bookings.chkboxcancelPMReservation","PM reservations"))
		click("Events.Bookings.chkboxcancelPMReservation", 20, "visible");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.lnkSaveEdit", 20, "visible");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.btnYes", 20, "visible");
	waitForSpinnerToDisappear(20);
	Utils.WebdriverWait(100, "Events.Bookings.flemRoomStatus", "presence");
	waitForPageLoad(20);
	waitForSpinnerToDisappear(20);
	roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
	cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
	if (roomStatus.contains("CAN") && cateringStatus.contains("CAN")  ){
		logger.log(LogStatus.PASS, "Block with CAN as status for Rooms as well as catering");
		System.out.println("Block with CAN as status for Rooms as well as catering");
	}else{
		logger.log(LogStatus.FAIL, "NOT Selected a Block with CAN as status for Rooms as well as catering");
		System.out.println("NOT Selected a Block with CAN as status for Rooms as well as catering");
	}
	
} catch (Exception e) {
	Utils.takeScreenshot(driver, testClassName);
	logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
	Assert.fail("Block could not be selected");
	throw (e);
}
}

public static void creSubAlloBlock(HashMap<String, String> blockMap) throws IOException, Exception{
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

	try {Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
	click("Events.Bookings.BlocksStartDate", 20, "visible");
	String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
	char ch[] = dateformat.toCharArray(); 
	for (int i = 0; i < dateformat.length(); i++) {
		if (ch[i] == 'D')  
            ch[i] = (char)(ch[i] + 'a' - 'A');             
    
	else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
                if (ch[i] >= 'a' && ch[i] <= 'z') { 
                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
                } 
            } 
	}
        String st = new String(ch); 
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
	String fromdate = simpleDateFormat.format(new Date());
	textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
	click("Events.Bookings.Event.EndDate", 20, "visible");
	Calendar cal = Calendar.getInstance();
	System.out.println("Current Date: "+simpleDateFormat.format(cal.getTime()));
	//Adding 1 Day to the current date
	cal.add(Calendar.DAY_OF_MONTH, 1);  
	//Date after adding one day to the current date
	String newDate = simpleDateFormat.format(cal.getTime());  
	//Displaying the new Date after addition of 1 Day
	System.out.println("Incremnted current date by one: "+newDate);
	textBox("Events.Bookings.Event.EndDate",newDate,50,"presence");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.BlockName", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
	click("Events.Bookings.Property", 20, "visible");
	waitForSpinnerToDisappear(20);
	String pattern = "HHmm";
	simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	clear("Events.Bookings.BlockCodeValue");
	textBox("Events.Bookings.BlockCodeValue",StringUtils.substring( blockMap.get("BlockCode"), 0, 3)+"SUB"+date,50,"presence");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.Market", 20, "visible");
	waitForSpinnerToDisappear(20);
	
	if (isExists("Events.Bookings.RoomStatus"))
		textBox("Events.Bookings.RoomStatus",blockMap.get("Status"),50,"presence");
	else if( isExists("Events.Bookings.BlockStatus"))
		textBox("Events.Bookings.BlockStatus",blockMap.get("Status"),50,"presence");
	
	click("Events.Bookings.Market", 20, "visible");
	String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
	System.out.println(blockCode);
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
	click("Events.Bookings.Source", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
	click("Events.Bookings.RateCode", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
	click("Events.Bookings.EveAttendees", 20, "visible");
	waitForSpinnerToDisappear(20);
	textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
	String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
	if (attndNumber=="")
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
	waitForSpinnerToDisappear(20);
	String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
	if (blockCodeval!=blockCode){
		clear("Events.Bookings.BlockCodeValue");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockCodeValue",StringUtils.substring( blockMap.get("BlockCode"), 0, 3)+"SUB"+date,50,"presence");
	}
	}catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
}
public static String generateCharacter() throws Exception{
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

	try {String chars = "ABCDEFGHIJKLMNPQRSTUVWXYZ";
	Random rnd = new Random();
	char c = chars.charAt(rnd.nextInt(chars.length()));
	String cString= Character.toString(c);
	return cString;}
	catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
	}

public static String generateBlockCode() throws Exception{
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

	try {
	String blockCode= generateCharacter()+generateCharacter()+generateCharacter();
	return blockCode;}
	catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
	}

public static String blockCodeCreate(HashMap<String, String> blockMap) throws IOException, Exception{
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

	try {
		String blockCodefn = blockCodeCreateWithoutSave(blockMap);
		waitForSpinnerToDisappear(30);
		click("Events.Bookings.BlockSave", 20, "visible");
		waitForSpinnerToDisappear(30);
		return blockCodefn;
	}catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
	}

public static String blockCodeCreateWithoutSave(HashMap<String, String> blockMap) throws IOException, Exception{
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

	try {// Navigating to Bookings menu
		click("Events.Bookings", 20, "clickable");
		// Navigating to Blocks menu
		click("Events.Bookings.Blockslink", 20, "clickable");
		// Navigating to Manage Blocks menu
		click("Events.Bookings.ManageBlocks", 20, "clickable");
		// Selecting I want to link in Manage Blocks Screen
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.IWantToCreateBlock", 20, "clickable");
		// Selecting create Block link in I want to link
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockslinkIwant", 20, "clickable");
		Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
		click("Events.Bookings.BlocksStartDate", 20, "visible");
		String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
		char ch[] = dateformat.toCharArray(); 
		for (int i = 0; i < dateformat.length(); i++) {
			if (ch[i] == 'D')  
	            ch[i] = (char)(ch[i] + 'a' - 'A');             
	    
		else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
	                if (ch[i] >= 'a' && ch[i] <= 'z') { 
	                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
	                } 
	            } 
		}
	    String st = new String(ch); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		String fromdate = simpleDateFormat.format(new Date());
		textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
		click("Events.Bookings.Event.EndDate", 20, "visible");
		Calendar cal = Calendar.getInstance();
		System.out.println("Current Date: "+simpleDateFormat.format(cal.getTime()));
		//Adding 1 Day to the current date
		cal.add(Calendar.DAY_OF_MONTH, 1);  
		//Date after adding one day to the current date
		String newDate = simpleDateFormat.format(cal.getTime());  
		//Displaying the new Date after addition of 1 Day
		System.out.println("Incremnted current date by one: "+newDate);
		textBox("Events.Bookings.Event.EndDate",newDate,50,"presence");
		waitForSpinnerToDisappear(20);
		
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		
		click("Events.Bookings.Property", 20, "visible");
		waitForSpinnerToDisappear(20);
		String pattern = "MMdd";
		simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String ranstr = generateBlockCode();
		String blockCodevalran = StringUtils.substring( blockMap.get("BlockCode"), 0, 3)+ranstr+date;
		textBox("Events.Bookings.BlockCodeValue",blockCodevalran,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.Market", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		if (isExists("Events.Bookings.RoomStatus"))
			textBox("Events.Bookings.RoomStatus","INQ",50,"presence");
		else if( isExists("Events.Bookings.BlockStatus"))
			textBox("Events.Bookings.BlockStatus","INQ",50,"presence");
		
		click("Events.Bookings.Market", 20, "visible");
		String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		System.out.println(blockCode);
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
		click("Events.Bookings.Source", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
		Utils.tabKey("Events.Bookings.Source");
		waitForSpinnerToDisappear(20);
		String reserType = getAttributeOfElement("Events.Bookings.LovReservationType","value",20,"visible");
		if (reserType==""){
			click("Events.Bookings.LovReservationType", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.searchSelectTraceCOde", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.SelectPropertyButton", 20, "visible");
			waitForSpinnerToDisappear(20);
		}
		click("Events.Bookings.RateCode", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
		click("Events.Bookings.EveAttendees", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
		if (attndNumber=="")
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
		waitForSpinnerToDisappear(20);
		String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		if (blockCodeval!=blockCode){
			clear("Events.Bookings.BlockCodeValue");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockCodeValue",blockCodevalran,50,"presence");
		}
		waitForSpinnerToDisappear(20);
		String uniquedate = "ddMMyyHHmmss";
		SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
		String screenshotval = uniquedateval1.format(new Date());
		
//		Utils.takeScreenshot("Events.Bookings.BlockSave" + screenshotval);
		String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
		
		waitForSpinnerToDisappear(30);
		return blockCodefn;
	}catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
	}


public static String templateBlockCode(HashMap<String, String> blockMap) throws Exception{
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	String blockCodevalran = null;
	try {
		String pattern = "MMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String ranstr = generateBlockCode();
		blockCodevalran = StringUtils.substring( blockMap.get("BlockCode"), 0, 3)+ranstr+date;
	}
	catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
	
	return blockCodevalran;
	}

public static String templateSubBlockCode(HashMap<String, String> blockMap) throws Exception{
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	String blockCodevalran = null;
	try {
		String pattern = "MMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		clear("Events.Bookings.BlockCodeValue");
		String ranstr = generateBlockCode();
		blockCodevalran = "SUB"+ranstr+date;
	}
	catch (Exception e) {
		Utils.takeScreenshot(driver, testClassName);
		logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
		Assert.fail("Block could not be selected");
		throw (e);
	}
	
	return blockCodevalran;
	}


public static void defaultCateringStatusBlockCreation(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

	try {
//		String parameterType = "INQ";
		setDefaultCateringStatus("INQ");
		retunToOperaCloud();
		Assert.assertTrue(element("Events.Bookings").isDisplayed());
		/**
		 * #1 Navigating to Block Creation page
		 */
		navigateManageBlocksPage();
		// Selecting I want to link in Manage Blocks Screen
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.IWantToCreateBlock", 20, "clickable");
		// Selecting create Block link in I want to link
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockslinkIwant", 20, "clickable");
		Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
		
		if(getAttributeOfElement("Events.Bookings.cateringStatus","value",20,"visible").equalsIgnoreCase("INQ")){
			System.out.println("***** Catering status contains the default status as expected *****");
			logger.log(LogStatus.PASS, " Catering status contains the default status as expected ");
		}else{
			System.out.println("***** Catering status Does not contains the default status as expected *****");
			logger.log(LogStatus.FAIL, " Catering status Does not contains the default status as expected ");
		}
		
		setDefaultCateringStatus("null");	
		}catch(AssertionError e){
			setDefaultCateringStatus("null");	
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}catch(Exception e){
			setDefaultCateringStatus("null");	
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}

public static void setDefaultCateringStatus(String parameterType) throws Exception{
	try{
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
				if(eachGroup.getText().equalsIgnoreCase("Blocks")){
					//((JavascriptExecutor) Utils.driver).executeScript("arguments[0].scrollIntoView(true);", eachGroup);
					eachGroup.click();
					Utils.waitForSpinnerToDisappear(20);
					break;
				}
			}
			
			Utils.click("Events.Configuration.PMSFunctions.EditDefaultCateringStatus", 0, "clickable");		
			Utils.waitForSpinnerToDisappear(20);
			//Set the data for the function attribute
			
			if (parameterType.equalsIgnoreCase("null")){
				Utils.clear("Events.Configuration.PMSFunctions.txtDefaultcCateringEvent");
			}else if(parameterType.equalsIgnoreCase("INQ")){
				Utils.clear("Events.Configuration.PMSFunctions.txtDefaultcCateringEvent");
				Utils.textBox("Events.Configuration.PMSFunctions.txtDefaultcCateringEvent", parameterType.toUpperCase());
			}
			Utils.tabKey("Events.Configuration.PMSFunctions.txtDefaultcCateringEvent");
			Utils.waitForSpinnerToDisappear(30);
			Utils.click("Events.Configuration.PMSFunctions.btnDefaultSubEventRateCodeSave");
			Utils.waitForSpinnerToDisappear(20);
			//Click on Save button
			}catch(Exception e){
				logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
				Assert.fail(" Could not update the configuration");
				throw (e);
			}
		
}
	public static void retunToOperaCloud() throws Exception, AssertionError{
		Utils.mouseHover("Configuration.mainMenu",100,"clickable");
		Utils.jsClick("Configuration.mainMenu");
		Utils.mouseHover("Events.Configuration.OperaCloud",100,"clickable");
		Utils.click("Events.Configuration.OperaCloud",100,"clickable");
		Utils.waitForSpinnerToDisappear(45);
		Utils.Wait(3000);
	}
	public static void navigateManageBlocksPage() throws Exception{
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		
		// Navigating to Bookings menu
			try {
				click("Events.Bookings", 20, "clickable");
				waitForSpinnerToDisappear(20);
				// Navigating to Blocks menu
				click("Events.Bookings.Blockslink", 20, "clickable");
				waitForSpinnerToDisappear(20);
				// Navigating to Manage Blocks menu
				click("Events.Bookings.ManageBlocks", 20, "clickable");
				// Selecting I want to link in Manage Blocks Screen
				waitForSpinnerToDisappear(20);
			} catch (Exception e) {
				Utils.takeScreenshot(driver, testClassName);
				logger.log(LogStatus.FAIL, " Could not create a block " + e.getMessage());
				Assert.fail("Block could not be selected");
				throw (e);
			}
}

	
	
public static void createBlockSaveGotoRoomGrid(HashMap<String, String> blockMap) throws Exception {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

		try {
			String blockCodefn = blockCodeCreateWithoutSave(blockMap);
			waitForSpinnerToDisappear(30);
			click("Events.Bookings.lnkSaveandRoomGrid", 20, "visible");
			System.out.println("Block is created with block code :: "+ blockCodefn);
			waitForSpinnerToDisappear(20);
			scroll("down");
			Utils.WebdriverWait(100, "Events.Bookings.detailsStampLink", "clickable");
			if (isExists("Events.Bookings.detailsStampLink")){
				logger.log(LogStatus.PASS, "Block created and successfully navigated to the Room and rate grid");
				System.out.println("Block created and successfully navigated to the Room and rate grid");
				click("Events.Bookings.detailsStampLink", 20, "clickable");
				}
			else 
				logger.log(LogStatus.FAIL, "Block NOT created and successfully not navigated to the Room and rate grid");
			waitForSpinnerToDisappear(20);
			}catch(Exception e){
				logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
				Assert.fail(" Could not update the configuration");
				throw (e);
			}
		}



public static void createBlockMultirateCOdeSuperScript(HashMap<String, String> blockMap) throws Exception {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

		try {
			click("Events.Bookings", 20, "clickable");
			// Navigating to Blocks menu
			click("Events.Bookings.Blockslink", 20, "clickable");
			// Navigating to Manage Blocks menu
			click("Events.Bookings.ManageBlocks", 20, "clickable");
			// Selecting I want to link in Manage Blocks Screen
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.IWantToCreateBlock", 20, "clickable");
			// Selecting create Block link in I want to link
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.BlockslinkIwant", 20, "clickable");
			Utils.WebdriverWait(100, "Events.Bookings.BlocksStartDate", "presence");
			click("Events.Bookings.BlocksStartDate", 20, "visible");
			String dateformat = getAttributeOfElement("Events.Bookings.BlocksStartDate","placeholder",20,"visible");
			char ch[] = dateformat.toCharArray(); 
			for (int i = 0; i < dateformat.length(); i++) {
				if (ch[i] == 'D')  
		            ch[i] = (char)(ch[i] + 'a' - 'A');             
		    
			else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
		                if (ch[i] >= 'a' && ch[i] <= 'z') { 
		                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
		                } 
		            } 
			}
		    String st = new String(ch); 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
			String fromdate = simpleDateFormat.format(new Date());
			textBox("Events.Bookings.BlocksStartDate",fromdate,50,"presence");
			click("Events.Bookings.Event.EndDate", 20, "visible");
			Calendar cal = Calendar.getInstance();
			System.out.println("Current Date: "+simpleDateFormat.format(cal.getTime()));
			//Adding 1 Day to the current date
			cal.add(Calendar.DAY_OF_MONTH, 1);  
			//Date after adding one day to the current date
			String newDate = simpleDateFormat.format(cal.getTime());  
			//Displaying the new Date after addition of 1 Day
			System.out.println("Incremnted current date by one: "+newDate);
			textBox("Events.Bookings.Event.EndDate",newDate,50,"presence");
			waitForSpinnerToDisappear(20);
			
			click("Events.Bookings.BlockName", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
			
			click("Events.Bookings.Property", 20, "visible");
			waitForSpinnerToDisappear(20);
			String pattern = "MMdd";
			simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			clear("Events.Bookings.BlockCodeValue");
			String ranstr = generateBlockCode();
			String blockCodevalran = StringUtils.substring( blockMap.get("BlockCode"), 0, 3)+ranstr+date;
			textBox("Events.Bookings.BlockCodeValue",blockCodevalran,50,"presence");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.Market", 20, "visible");
			waitForSpinnerToDisappear(20);
			
			if (isExists("Events.Bookings.RoomStatus"))
				textBox("Events.Bookings.RoomStatus","INQ",50,"presence");
			else if( isExists("Events.Bookings.BlockStatus"))
				textBox("Events.Bookings.BlockStatus","INQ",50,"presence");
			
			click("Events.Bookings.Market", 20, "visible");
			String blockCode = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			System.out.println(blockCode);
			waitForSpinnerToDisappear(20);
			
			textBox("Events.Bookings.Market",blockMap.get("MarketType"),50,"presence");
			click("Events.Bookings.Source", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.Source",blockMap.get("Source"),50,"presence");
			Utils.tabKey("Events.Bookings.Source");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.LovReservationType", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.SelectPropertyButton", 20, "visible");
			waitForSpinnerToDisappear(20);
			click("Events.Bookings.RateCode", 20, "visible");
			waitForSpinnerToDisappear(20);
			textBox("Events.Bookings.RateCode",blockMap.get("RateCode"),50,"presence");
			Utils.tabKey("Events.Bookings.RateCode");
			clickWaitforSpinner("Events.Bookings.lovMulRateCodes", 20, "visible");
			clickWaitforSpinner("Events.Bookings.lknADDPopUp", 20, "visible");
			clickWaitforSpinner("Events.Bookings.lovRateCOdeMulSel", 20, "visible");
			
			textBox("Events.Bookings.rateCodeSearchCntnt",blockMap.get("RateCode_2"),50,"presence");
			Utils.tabKey("Events.Bookings.rateCodeSearchCntnt");
			clickWaitforSpinner("Events.Bookings.rateCodeSearchLink", 20, "visible");
			clickWaitforSpinner("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
			clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
			
			clickWaitforSpinner("Events.Bookings.btnLinkOK", 20, "visible");
			
			textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
			String rateCodeCount = getText("Events.Bookings.multiRateCodeCount");
			if (rateCodeCount.equalsIgnoreCase("2")){
					logger.log(LogStatus.PASS, "Multiple Rate Code Added and displayed the count of the added rate codes");
					System.out.println("Multiple Rate Code Added and displayed the count of the added rate codes");
				}else{
					logger.log(LogStatus.FAIL, "Multiple Rate Code are NOT Added and does not displayed the count of the added rate codes");
					System.out.println("Multiple Rate Code are NOT Added and does not displayed the count of the added rate codes");
			}
			String primaryRateCodeVal = getAttributeOfElement("Events.Bookings.RateCode","value",20,"visible");
			clickWaitforSpinner("Events.Bookings.lovMulRateCodes", 20, "visible");
			clickWaitforSpinner("Events.Bookings.chkPrimaryRatecodetwo", 20, "visible");
			String chnagedRateCode = getAttributeOfElement("Events.Bookings.edtContentRateCOde","value",20,"visible");
			
			clickWaitforSpinner("Events.Bookings.btnLinkOK", 20, "visible");
			clickWaitforSpinner("Events.Bookings.EveAttendees", 20, "visible");
			
			String chnagedRateCodeDis =  getAttributeOfElement("Events.Bookings.RateCode","value",20,"visible");
			
			if (!primaryRateCodeVal.equalsIgnoreCase(chnagedRateCodeDis)){
				if (chnagedRateCode.equalsIgnoreCase(chnagedRateCodeDis)){
				logger.log(LogStatus.PASS, "Changed Primary Rate Code is getting displayed.");
				System.out.println("PASS:: Changed Primary Rate Code is getting displayed.");
				}
			}else{
				logger.log(LogStatus.FAIL, "Changed Primary Rate Code is NOT getting displayed.");
				System.out.println("FAIL:: Changed Primary Rate Code is NOT getting displayed.");
			}
			
			String attndNumber = getAttributeOfElement("Events.Bookings.EveAttendees","value",20,"visible");
			if (attndNumber=="")
				textBox("Events.Bookings.EveAttendees",blockMap.get("EveAttendees"),50,"presence");
			waitForSpinnerToDisappear(20);
			String blockCodeval = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			if (blockCodeval!=blockCode){
				clear("Events.Bookings.BlockCodeValue");
				waitForSpinnerToDisappear(20);
				textBox("Events.Bookings.BlockCodeValue",blockCodevalran,50,"presence");
			}
			waitForSpinnerToDisappear(20);
			String uniquedate = "ddMMyyHHmmss";
			SimpleDateFormat uniquedateval1 = new SimpleDateFormat(uniquedate);
			String screenshotval = uniquedateval1.format(new Date());
			
//			Utils.takeScreenshot("Events.Bookings.BlockSave" + screenshotval);
			String blockCodefn = getAttributeOfElement("Events.Bookings.BlockCodeValue","value",20,"visible");
			
			waitForSpinnerToDisappear(30);
			click("Events.Bookings.lnkSaveandRoomGrid", 20, "visible");
			System.out.println("Block is created with block code :: "+ blockCodefn);
			waitForSpinnerToDisappear(20);
			scroll("down");
			Utils.WebdriverWait(100, "Events.Bookings.detailsStampLink", "clickable");
			if (isExists("Events.Bookings.detailsStampLink")){
				logger.log(LogStatus.PASS, "Block created and successfully navigated to the Room and ate grid");
				System.out.println("Block created and successfully navigated to the Room and ate grid");
				click("Events.Bookings.detailsStampLink", 20, "clickable");
				}
			else 
				logger.log(LogStatus.FAIL, "Block NOT created and successfully not navigated to the Room and ate grid");
			waitForSpinnerToDisappear(20);
			}catch(Exception e){
				logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
				Assert.fail(" Could not update the configuration");
				throw (e);
			}
		}


public static void clickWaitforSpinner(String locatorName,int timeout, String option){
	try {
		waitForSpinnerToDisappear(30);
		click(locatorName, timeout, option);
		waitForSpinnerToDisappear(30);

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

public static void blockSearchMultipleNavigationViews(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName); 
	EventComponents evComp = new EventComponents();
	try {
		blockSearchSingleView(blockMap,"Events.Bookings.lnkViewTable");
		blockSearchSingleView(blockMap,"Events.Bookings.lnkViewList");
		blockSearchSingleView(blockMap,"Events.Bookings.lnkViewCard");
		blockSearchSingleView(blockMap,"Events.Bookings.lnkViewConsole");
		blockSearchSingleView(blockMap,"Events.Bookings.lnkViewTable");
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}

public static void blockSearchSingleView(HashMap<String, String> blockMap, String searchViewTypeLocator) throws Exception{
	navigateManageBlocksPage();
	navigateAdvanceSearch();
	searchAdvBlock(blockMap);
	clickWaitforSpinner(searchViewTypeLocator, 20, "visible");
	clickOnElementBasedOnTextContains("Events.Bookings.lnkMultipleViewBlockID", StringUtils.substring( blockMap.get("BlockCode"), 0, 3),"data-ocid");
	waitForSpinnerToDisappear(20);
	if (isExists("Events.Bookings.BlocksoverView")){
		logger.log(LogStatus.PASS, "Block is Searched and navigated to the Block over view page");
		System.out.println("PASS:: Block is Searched and navigated to the Block over view page");
	}
	else 
		logger.log(LogStatus.FAIL, "Block is not Searched and navigated to the Block over view page");
}

public static void searchAdvBlock(HashMap<String, String> blockMap) throws Exception{
	try{
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		clear("Events.Bookings.EndDateFrom");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.SearchButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
}


public static void navigateAdvanceSearch() throws Exception{
	if (isExists("Events.Bookings.lnkBasicSearch")){
		logger.log(LogStatus.PASS, "Advance Search screen is populated");
		System.out.println("PASS:: Advance Search screen is populated");
	}else{
		clickWaitforSpinner("Events.Bookings.lnkAdvanceSearch", 20, "visible");
		logger.log(LogStatus.PASS, "Advance Search screen is populated");
		System.out.println("PASS:: Advance Search screen is populated");
	}
}
public static void subBlockCreateReservationLink(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName); 
	EventComponents evComp = new EventComponents();
	try {
		
//		navigateManageBlocksPage();
//		String masterBlockCode = "CREGPZ0304";
//		String subblockCode = "MSUB1726";
//		navigateAdvanceSearch();
//		click("Events.Bookings.BlockName", 20, "visible");
//		waitForSpinnerToDisappear(20);
//		clear("Events.Bookings.EndDateFrom");
//		click("Events.Bookings.BlockCodeValue", 20, "visible");
//		waitForSpinnerToDisappear(20);
//		textBox("Events.Bookings.BlockCodeValue",masterBlockCode,50,"presence");
//		waitForSpinnerToDisappear(20);
//		click("Events.Bookings.SearchButton", 20, "visible");
//		waitForSpinnerToDisappear(20);
//		evComp.getElementTableBasedOnColumnName("Block Code").click();
//		waitForSpinnerToDisappear(20);
//		click("Events.Bookings.MasterSubBlockLink", 20, "visible");
//		waitForSpinnerToDisappear(20);
//		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
//		waitForSpinnerToDisappear(20);
//		clickOnElementBasedOnText("Events.Bookings.SubBlockCodeResults", subblockCode);
//		waitForSpinnerToDisappear(20);
		
		
//		
		navigateManageBlocksPage();
		waitForSpinnerToDisappear(20);
		String masterBlockCode = createMasterSubBlock(blockMap, "OFF");
		masterBlockCode = Utils.getAllValuesFromTableBasedOnColumnName("Block Code").get(0);
		String subblockCode = Utils.getAllValuesFromTableBasedOnColumnName("Block Code").get(1);
		navigateManageBlocksPage();
		waitForSpinnerToDisappear(20);
		navigateAdvanceSearch();
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		clear("Events.Bookings.EndDateFrom");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockCodeValue",masterBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.SearchButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		evComp.getElementTableBasedOnColumnName("Block Code").click();
		waitForSpinnerToDisappear(20);
		changeOfStatusDynamic(blockMap, "INQ","TEN");
		changeOfStatusDynamic(blockMap, "TEN","DEF");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.MasterSubBlockLink", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickOnElementBasedOnText("Events.Bookings.SubBlockCodeResults", subblockCode);
		waitForSpinnerToDisappear(20);
		changeOfStatusDynamic(blockMap, "INQ","TEN");
		changeOfStatusDynamic(blockMap, "TEN","DEF");
		
		click("Events.Bookings.IWantToCreateBlock", 20, "visible");
		waitForSpinnerToDisappear(200);
		click("Events.Bookings.lnkReservation", 20, "visible");
		waitForSpinnerToDisappear(200);
		scroll("down");
		click("Events.Bookings.SearchButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		scroll("down");
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(By.xpath("//*[@data-ocid='CNTNT_PH1']//following::span[text()='STDK']"))).doubleClick().build().perform();
		waitForSpinnerToDisappear(20);
		action.moveToElement(driver.findElement(By.xpath("//*[@data-ocid='CNTNT_PH1']//following::span[text()='STDK']"))).doubleClick().build().perform();
		waitForSpinnerToDisappear(20);
		if (isExists("Events.Bookings.lnkBorrow"))
			clickWaitforSpinner("Events.Bookings.lnkBorrow", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnksrcDummyLink", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkSearchAfterReset", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkSearchAfterReset", 20, "visible");
		evComp.getElementTableBasedOnColumnName("Profile Type").click();
		clickWaitforSpinner("Events.SelectPropertyButton", 200, "visible");
		
		clickWaitforSpinner("Events.Bookings.lnkLOVResTypeAdv", 200, "visible");
		evComp.getElementTableBasedOnColumnName("Reservation Type").click();
		clickWaitforSpinner("Events.SelectPropertyButton", 200, "visible");
		clickWaitforSpinner("Events.Bookings.lnkBookNow", 200, "visible");
		clickWaitforSpinner("Events.Bookings.lnkConfirm", 200, "visible");
//		java.util.List<WebElement> strSearchelem = elements("Events.Bookings.SearchButton");
//		
//		for (int i = 0; i <= strSearchelem.size(); i++) {
//			int DefVa = 1;
//			if (i == DefVa)
////				Utils.jsClick("Configuration.mainMenu");
//			strSearchelem.get(i).click();
//		}
		//Working fine till above 
//		driver.findElement(By.xpath("//*[@data-ocid='LINK_RESET']/ancestor::span[1]/following::span/div/a")).isDisplayed();
		
		driver.findElement(By.xpath("//*[@data-ocid='CNTNT_PH1']//following::span[text()='STDK']")).click();
		click("Events.Bookings.IWantToCreateBlock", 20, "visible");
		driver.findElement(By.xpath("//*[@data-ocid='CNTNT_PH1']//following::span[text()='STDK']")).click();
		waitForSpinnerToDisappear(200);
		clickWaitforSpinner("Events.Bookings.lnkReservationIWant", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickWaitforSpinner("Events.Bookings.SearchButton", 20, "visible");
		scroll("down");
		clickWaitforSpinner("Events.Bookings.lnkBlockRoomsReserve", 20, "visible");
		scroll("down");
		if (isDisplayed("Events.Bookings.lnkBlockRoomsReserve", "Block Rooms Reserve")){
			clickWaitforSpinner("Events.Bookings.lnkBlockRoomsReserve", 20, "visible");
		}
		clickWaitforSpinner("Events.Bookings.lovNameReservation", 20, "visible");
		java.util.List<WebElement> strelem = elements("Events.Bookings.SearchButton");
		for (int i = 0; i < strelem.size(); i++) {
			strelem.get(i).click();
		}
		clickWaitforSpinner("Events.Bookings.lnkSearchManageProfile", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}

public static void changeOfStatusDynamic(HashMap<String, String> blockMap, String prevoiusStatus, String statusToBeChanged) throws Exception{
	Utils.WebdriverWait(100, "Events.Bookings.IWantToCreateBlock", "clickable");
	
	String roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
	String cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
	if (roomStatus.equalsIgnoreCase(prevoiusStatus) && cateringStatus.equalsIgnoreCase(prevoiusStatus)  ){
		logger.log(LogStatus.PASS, "Selected a Block with "+prevoiusStatus+" as status for Rooms as well as catering");
	}else{
		logger.log(LogStatus.FAIL, "NOT Selected a Block with "+prevoiusStatus+" as status for Rooms as well as catering");
	}
	click("Events.Bookings.IWantToCreateBlock", 20, "visible");
	waitForSpinnerToDisappear(200);
	click("Events.Bookings.changeBlockStatus", 20, "visible");
	waitForSpinnerToDisappear(20);
	String statusToBeChangedfull = null;
	if (statusToBeChanged.equalsIgnoreCase("TEN"))
			statusToBeChangedfull = "Tentative";
	else if (statusToBeChanged.equalsIgnoreCase("DEF"))
		statusToBeChangedfull = "Definite";
	clickOnElementBasedOnText("Events.Bookings.statusRadioBtn",statusToBeChangedfull);
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.lnkSaveEdit", 20, "visible");
	waitForSpinnerToDisappear(20);
	click("Events.Bookings.btnYes", 20, "visible");
	waitForSpinnerToDisappear(20);
	Utils.WebdriverWait(100, "Events.Bookings.flemRoomStatus", "presence");
	waitForPageLoad(20);
	roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
	cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
	if (roomStatus.equalsIgnoreCase(statusToBeChanged) && cateringStatus.equalsIgnoreCase(statusToBeChanged)  ){
		logger.log(LogStatus.PASS, "Block with "+statusToBeChanged+" as status for Rooms as well as catering");
		System.out.println("Block with "+statusToBeChanged+" as status for Rooms as well as catering");
	}else{
		logger.log(LogStatus.FAIL, "NOT Selected a Block with "+statusToBeChanged+" as status for Rooms as well as catering");
		System.out.println("NOT Selected a Block with "+statusToBeChanged+" as status for Rooms as well as catering");
	}
}

public static void createSubBlockSyncCheckoxSelected(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName); 
	EventComponents evComp = new EventComponents();
	try {
		navigateManageBlocksPage();
		waitForSpinnerToDisappear(20);
		String masterBlockCode = createMasterSubBlock(blockMap, "ON");
		masterBlockCode = Utils.getAllValuesFromTableBasedOnColumnName("Block Code").get(0);
		String subblockCode = Utils.getAllValuesFromTableBasedOnColumnName("Block Code").get(1);
		
//		clickOnElementBasedOnText("Events.Bookings.lnkMasterSubBlockCode", masterBlockCode);
		navigateManageBlocksPage();
		waitForSpinnerToDisappear(20);
		navigateAdvanceSearch();
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		clear("Events.Bookings.EndDateFrom");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		waitForSpinnerToDisappear(20);
		textBox("Events.Bookings.BlockCodeValue",masterBlockCode,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.SearchButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		evComp.getElementTableBasedOnColumnName("Block Code").click();
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.IWantToCreateBlock", 20, "visible");
		waitForSpinnerToDisappear(200);
		click("Events.Bookings.lnkCreateSubBlockIWantTo", 20, "visible");
		waitForSpinnerToDisappear(200);

		textBox("Events.Bookings.BlockName",blockMap.get("BlockName"),50,"presence");
		Utils.tabKey("Events.Bookings.BlockName");
		waitForSpinnerToDisappear(20);
		String SubBlockCOdeVal= templateSubBlockCode(blockMap);
		textBox("Events.Bookings.BlockCodeValue",SubBlockCOdeVal,50,"presence");
		Utils.tabKey("Events.Bookings.BlockCodeValue");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.btnLnkSaveGoToPresentation", 20, "visible");
		waitForSpinnerToDisappear(200);
		click("Events.Bookings.MasterSubBlockLink", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.BlockCOdeHeader", 20, "visible");
		waitForSpinnerToDisappear(20);
		Assert.assertTrue(driver.getPageSource().contains(SubBlockCOdeVal));
		
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}

public static void CreateBlockLoadRoomGrid(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName);
	EventComponents evComp = new EventComponents();
	try {
		
		
		
		clickWaitforSpinner("Events.Bookings.menuInventory", 20, "visible");
		clickWaitforSpinner("Events.Bookings.menuInventoryPropAvailability", 20, "visible");
		String dateformat = getAttributeOfElement("Events.Bookings.txtDateInventoryPropAvail","placeholder",20,"visible");
		char ch[] = dateformat.toCharArray(); 
		for (int i = 0; i < dateformat.length(); i++) {
			if (ch[i] == 'D')  
	            ch[i] = (char)(ch[i] + 'a' - 'A');             
	    
		else if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') { 
	                if (ch[i] >= 'a' && ch[i] <= 'z') { 
	                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
	                } 
	            } 
		}
	    String st = new String(ch); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(st);
		String fromdate = simpleDateFormat.format(new Date());
		textBox("Events.Bookings.txtDateInventoryPropAvail",fromdate,50,"presence");
		Utils.tabKey("Events.Bookings.txtDateInventoryPropAvail");
		clickWaitforSpinner("Events.Bookings.SearchButton", 20, "visible");
		String endDateVal = getText("Events.Bookings.eleAvailableRoomsSTDK");
		System.out.println("The available rooms for the date "+fromdate+ " is ::"+endDateVal);
		
		String blockCodefn = blockCodeCreateWithoutSave(blockMap);
		waitForSpinnerToDisappear(30);
		click("Events.Bookings.lnkSaveandRoomGrid", 20, "visible");
		System.out.println("Block is created with block code :: "+ blockCodefn);
		waitForSpinnerToDisappear(20);
		scroll("down");
		Utils.WebdriverWait(100, "Events.Bookings.detailsStampLink", "clickable");
		if (isExists("Events.Bookings.detailsStampLink")){
			logger.log(LogStatus.PASS, "Block created and successfully navigated to the Room and rate grid");
			System.out.println("Block created and successfully navigated to the Room and rate grid");
			click("Events.Bookings.detailsStampLink", 20, "clickable");
			}
		else 
			logger.log(LogStatus.FAIL, "Block NOT created and successfully not navigated to the Room and rate grid");
		waitForSpinnerToDisappear(20);
		clickWaitforSpinner("Events.Bookings.lnkActionsLoadGrid", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkLoadRoomGrid", 20, "visible");
		java.util.List<WebElement> strSearchelem = elements("Events.Bookings.chkRoomTypeSelecion");
		for (int i = 0; i <= strSearchelem.size(); i++) {
			int DefVa = 2;
			if (i == DefVa)
				break;
			strSearchelem.get(i).click();
		}
		clickWaitforSpinner("Events.Bookings.lnkLoadOccupancy2", 20, "visible");
		textBox("Events.Bookings.lnkLoadOccupancy1",blockMap.get("OccupRooms"),50,"presence");
		clickWaitforSpinner("Events.Bookings.lnkSaveAndContinue", 20, "visible");
		java.util.List<WebElement> strSuitRoomes = elements("Events.Bookings.chkRoomTypeSelecion");
		int count = 0;
		for (int i = 0; i <= strSuitRoomes.size(); i++) {
			String roomTypes = strSuitRoomes.get(i).getText();
			System.out.println("Room type is "+roomTypes);
			if (roomTypes.contains("SUIT")){
				strSuitRoomes.get(i).click();
				count = count +1;
				if (count == 2)
						break;
			}
		}
		clickWaitforSpinner("Events.Bookings.lnkLoadOccupancy1", 20, "visible");
		textBox("Events.Bookings.lnkLoadOccupancy2", "2" ,50,"presence");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");
		java.util.List<WebElement> roomTypeOccupancy = elements("Events.Bookings.roomTypeOccupancy");
		int occupancy = 0;
		for (int i = 0; i <= roomTypeOccupancy.size(); i++) {
			String roomTypes = roomTypeOccupancy.get(i).getText();
			if (roomTypes.equalsIgnoreCase(blockMap.get("OccupRooms")) ){
				System.out.println("***** Occupancy is updated with "+ roomTypes + "*****");
				logger.log(LogStatus.PASS, "Occupancy is updated with " + roomTypes );
				occupancy = occupancy +1;
				if (occupancy == 2)
						break;
			}
		}
		clickWaitforSpinner("Events.Bookings.menuInventory", 20, "visible");
		clickWaitforSpinner("Events.Bookings.menuInventoryPropAvailability", 20, "visible");
		
		textBox("Events.Bookings.txtDateInventoryPropAvail",fromdate,50,"presence");
		Utils.tabKey("Events.Bookings.txtDateInventoryPropAvail");
		clickWaitforSpinner("Events.Bookings.SearchButton", 20, "visible");
		String availableRoomsAfterBlockCreation = getText("Events.Bookings.eleAvailableRoomsSTDK");
		System.out.println("The available rooms after creation of block for the date "+fromdate+ " is ::"+availableRoomsAfterBlockCreation);
		
		if (availableRoomsAfterBlockCreation.equalsIgnoreCase(endDateVal)){
			logger.log(LogStatus.PASS, "The available rooms after creation and before creation of block for the date "+fromdate+ " is same and Occupancies are ::"+availableRoomsAfterBlockCreation);
		}else {
			logger.log(LogStatus.FAIL, "The available rooms after creation and before creation of block for the date "+fromdate+ " is NOT SAME");
		}
		navigateManageBlocksPage();
		waitForSpinnerToDisappear(20);
		navigateAdvanceSearch();
		click("Events.Bookings.BlockName", 20, "visible");
		waitForSpinnerToDisappear(20);
		clear("Events.Bookings.EndDateFrom");
		click("Events.Bookings.BlockCodeValue", 20, "visible");
		waitForSpinnerToDisappear(20);
		
		textBox("Events.Bookings.BlockCodeValue",blockCodefn,50,"presence");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.SearchButton", 20, "visible");
		waitForSpinnerToDisappear(20);
		evComp.getElementTableBasedOnColumnName("Block Code").click();
		waitForSpinnerToDisappear(20);
		
		
		Utils.WebdriverWait(100, "Events.Bookings.IWantToCreateBlock", "clickable");
		
		String roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
		String cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
		if (roomStatus.equalsIgnoreCase("INQ") && cateringStatus.equalsIgnoreCase("INQ")  ){
			logger.log(LogStatus.PASS, "Selected a Block with "+"INQ"+" as status for Rooms as well as catering");
		}else{
			logger.log(LogStatus.FAIL, "NOT Selected a Block with "+"INQ"+" as status for Rooms as well as catering");
		}
		click("Events.Bookings.IWantToCreateBlock", 20, "visible");
		waitForSpinnerToDisappear(200);
		click("Events.Bookings.changeBlockStatus", 20, "visible");
		waitForSpinnerToDisappear(20);
		String statusToBeChangedfull = null;
		clickOnElementBasedOnText("Events.Bookings.statusRadioBtn","Definite");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.lnkSaveEdit", 20, "visible");
		waitForSpinnerToDisappear(20);
		click("Events.Bookings.btnYes", 20, "visible");
		waitForSpinnerToDisappear(20);
		clickWaitforSpinner("Events.Bookings.lnkOverBookAll", 20, "visible");
		Utils.WebdriverWait(100, "Events.Bookings.flemRoomStatus", "presence");
		waitForPageLoad(20);
		roomStatus = getAttributeOfElement("Events.Bookings.flemRoomStatus","data-ocformvalue",20,"visible");
		cateringStatus = getAttributeOfElement("Events.Bookings.flemCateringStatus","data-ocformvalue",20,"visible");
		if (roomStatus.equalsIgnoreCase("DEF") && cateringStatus.equalsIgnoreCase("DEF")  ){
			logger.log(LogStatus.PASS, "Block with "+"DEF"+" as status for Rooms as well as catering");
			System.out.println("Block with "+"DEF"+" as status for Rooms as well as catering");
		}else{
			logger.log(LogStatus.FAIL, "NOT Selected a Block with "+"DEF"+" as status for Rooms as well as catering");
			System.out.println("NOT Selected a Block with "+"DEF"+" as status for Rooms as well as catering");
		}
		clickWaitforSpinner("Events.Bookings.menuInventory", 20, "visible");
		clickWaitforSpinner("Events.Bookings.menuInventoryPropAvailability", 20, "visible");
		textBox("Events.Bookings.txtDateInventoryPropAvail",fromdate,50,"presence");
		Utils.tabKey("Events.Bookings.txtDateInventoryPropAvail");
		clickWaitforSpinner("Events.Bookings.SearchButton", 20, "visible");
		String availableRoomsAfterBlockCreations = getText("Events.Bookings.eleAvailableRoomsSTDK");
		System.out.println("The available rooms after creation of block for the date "+fromdate+ " is ::"+availableRoomsAfterBlockCreations);
		
		if (!availableRoomsAfterBlockCreations.equalsIgnoreCase(endDateVal)){
			logger.log(LogStatus.PASS, "The available rooms after status change to Deduct is NOT SAME");
		}else {
			logger.log(LogStatus.FAIL, "The available rooms after creation and before creation of block for the date "+fromdate+ " is same and Occupancies are ::"+availableRoomsAfterBlockCreations);
		}
		
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}

public static void createBlockNotesCreateEditDelete(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName); 
	EventComponents evComp = new EventComponents();
	try {
		
		String blockCodefn = blockCodeCreateWithoutSave(blockMap);
		waitForSpinnerToDisappear(30);
		clickWaitforSpinner("Events.Bookings.SaveAndManageBlock", 20, "visible");
		System.out.println("Block is created with block code :: "+ blockCodefn);
		waitForSpinnerToDisappear(20);
		scroll("down");
		waitForSpinnerToDisappear(30);
		Utils.jsClick("Events.Bookings.lnkShowAll");
		waitForSpinnerToDisappear(30);
		clickWaitforSpinner("Events.Bookings.lnkBlockNotes", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkBlockNotesNew", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkLOVType", 20, "clickable");
		textBox("Events.Bookings.edtSearchResStatus","General",50,"presence");
		Utils.elements("Events.Bookings.SearchButton").get(1).click();; 
		clickWaitforSpinner("Events.Bookings.lnkLOVOriginCodeSelect", 20, "visible");
		clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
		textBox("Events.Bookings.txtCommentNotes","General",50,"presence");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");
		String strNoteBlock = getText("Events.Bookings.txtBlockContentNote");
		clickWaitforSpinner("Events.Bookings.lnkActionsLBlockNotes", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkActionsEditLBlockNotes", 20, "visible");
		
		textBox("Events.Bookings.txtCommentNotes","General Edited",50,"presence");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");
		
		String strNoteEdited = getText("Events.Bookings.txtBlockContentNote");
		
		if (!strNoteEdited.equalsIgnoreCase(strNoteBlock)){
			System.out.println("******The Note comments :: "+strNoteBlock+" and the Edited note comments:: "+strNoteEdited+"are NOT same. Note edited Successfully");
			logger.log(LogStatus.PASS, "The Note comments :: "+strNoteBlock+" and the Edited note comments:: "+strNoteEdited+"are NOT same. Note edited Successfully");
		}
		clickWaitforSpinner("Events.Bookings.lnkActionsLBlockNotes", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkDeleteNote", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkBtnDeleteNote", 20, "visible");
		if (!isExists( "Events.Bookings.txtBlockContentNote")){
			System.out.println("******Note deleted Successfully******");
			logger.log(LogStatus.PASS, "The Note is deletd Successfully");
		}
		
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}


public static void verifyDepositRequestDeleteRequest(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName); 
	EventComponents evComp = new EventComponents();
	try {
		
		String blockCodefn = blockCodeCreateWithoutSave(blockMap);
		waitForSpinnerToDisappear(30);
		clickWaitforSpinner("Events.Bookings.SaveAndManageBlock", 20, "visible");
		System.out.println("Block is created with block code :: "+ blockCodefn);
		waitForSpinnerToDisappear(20);
		scroll("down");
		waitForSpinnerToDisappear(30);
		Utils.jsClick("Events.Bookings.lnkShowAll");
		waitForSpinnerToDisappear(30);
		clickWaitforSpinner("Events.Bookings.lnkBtnDepositCancellation", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkBlockNotesNew", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkLOVResTypeAdv", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.depositRule", 20, "clickable");
		clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");
		clickWaitforSpinner("Events.Bookings.BlockCodeResults", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkDeleteNote", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkBtnDeleteNote", 20, "visible");
		
		if (isExists( "Events.Bookings.BlockCodeResults")){
			System.out.println("******Deposit NOT deleted Successfully******");
			logger.log(LogStatus.PASS, "The Deposit is NOT deletd Successfully");
		}else{
			System.out.println("******Deposit deleted Successfully******");
			logger.log(LogStatus.PASS, "The Deposit is deletd Successfully");
		}
		
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}

public static void verifyCancellationRuletDeleteRule(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName); 
	EventComponents evComp = new EventComponents();
	try {
		String blockCodefn = blockCodeCreateWithoutSave(blockMap);
		waitForSpinnerToDisappear(30);
		clickWaitforSpinner("Events.Bookings.SaveAndManageBlock", 20, "visible");
		System.out.println("Block is created with block code :: "+ blockCodefn);
		waitForSpinnerToDisappear(20);
		scroll("down");
		waitForSpinnerToDisappear(30);
		Utils.jsClick("Events.Bookings.lnkShowAll");
		waitForSpinnerToDisappear(30);
		clickWaitforSpinner("Events.Bookings.lnkBtnDepositCancellation", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.cancellationExpland", 20, "clickable");
		java.util.List<WebElement> strSearchelem = elements("Events.Bookings.lnkBlockNotesNew");
		for (int i = 0; i <= strSearchelem.size(); i++) {
			if (i == 1)
				strSearchelem.get(i).click();
			System.out.println("Clicked on New button");
		}
		clickWaitforSpinner("Events.Bookings.lnkLOVResTypeAdv", 20, "clickable");
		boolean flag = false;
		java.util.List<WebElement> rulesText = elements("Events.Bookings.ruleSectionTableCancellation");
		for (int i = 0; i <= rulesText.size(); i++) {
			if(rulesText.get(i).getText().contains(blockMap.get("CancellationRules"))){
				rulesText.get(i).click();
				flag = true;
				break;
			}
		}
		if (!flag ){
			clickWaitforSpinner("Events.Bookings.ruleSectionTableCancellation", 20, "clickable");
		}
		clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");
		clickWaitforSpinner("Events.Bookings.cancellationExpland", 20, "clickable");
		java.util.List<WebElement> strSearchelemCancel = elements("Events.Bookings.lnkActionsLBlockNotes");
		for (int i = 0; i <= strSearchelemCancel.size(); i++) {
			if (i == 1){
				strSearchelemCancel.get(i).click();
			System.out.println("Clicked on CancellationActions button");
			}
		}
		clickWaitforSpinner("Events.Bookings.lnkActionsEditLBlockNotes", 20, "visible");
		clear("Events.Bookings.txtCancelAmount");
		textBox("Events.Bookings.txtCancelAmount","10",50,"presence");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");

		clickWaitforSpinner("Events.Bookings.cancellationExpland", 20, "clickable");
		java.util.List<WebElement> strSearchelemCan = elements("Events.Bookings.lnkActionsLBlockNotes");
		for (int i = 0; i <= strSearchelemCan.size(); i++) {
			if (i == 1){
				strSearchelemCan.get(i).click();
			System.out.println("Clicked on CancellationActions button");
			}
		}
		clickWaitforSpinner("Events.Bookings.lnkDeleteNote", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkBtnDeleteNote", 20, "visible");
		java.util.List<WebElement> strSearchelemCanVerify = elements("Events.Bookings.lnkActionsLBlockNotes");
		if (strSearchelemCanVerify.size()==strSearchelemCan.size()){
			System.out.println("******Cancellation NOT deleted Successfully******");
			logger.log(LogStatus.PASS, "The Cancellation is NOT deletd Successfully");
		}else{
			System.out.println("******Cancellation deleted Successfully******");
			logger.log(LogStatus.PASS, "The Cancellation is deletd Successfully");
		}
		
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}

public static void verifyInventoryItemsEditDelete(HashMap<String, String> blockMap) throws Exception {
	String testClassName = Utils.getClassName();
	String methodName = Utils.getMethodName();
	System.out.println("testClassName: " + testClassName + " methodName: " + methodName); 
	EventComponents evComp = new EventComponents();
	try {
		clickWaitforSpinner("Events.Bookings.lnkInvenItems", 20, "clickable");
		
		clickWaitforSpinner("Events.Bookings.lnkItems", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkBlockNotesNew", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkInvenItems", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkSelectItem", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.lnkRemoveInven", 20, "clickable");
		if (isExists("Events.Bookings.lnkRemoveInven")){
			System.out.println("******Inventory NOT Removed Successfully******");
			logger.log(LogStatus.PASS, "The Inventory is NOT Removed Successfully");
		}else{
			System.out.println("******Inventory Removed Successfully******");
			logger.log(LogStatus.PASS, "The Inventory is Removed Successfully");
		}
		clickWaitforSpinner("Events.Bookings.lnkInvenItems", 20, "clickable");
		
		String blockCodefn = blockCodeCreateWithoutSave(blockMap);
		waitForSpinnerToDisappear(30);
		clickWaitforSpinner("Events.Bookings.SaveAndManageBlock", 20, "visible");
		System.out.println("Block is created with block code :: "+ blockCodefn);
		waitForSpinnerToDisappear(20);
		scroll("down");
		waitForSpinnerToDisappear(30);
		Utils.jsClick("Events.Bookings.lnkShowAll");
		waitForSpinnerToDisappear(30);
		
		
		clickWaitforSpinner("Events.Bookings.lnkBtnDepositCancellation", 20, "clickable");
		clickWaitforSpinner("Events.Bookings.cancellationExpland", 20, "clickable");
		java.util.List<WebElement> strSearchelem = elements("Events.Bookings.lnkBlockNotesNew");
		for (int i = 0; i <= strSearchelem.size(); i++) {
			if (i == 1)
				strSearchelem.get(i).click();
			System.out.println("Clicked on New button");
		}
		clickWaitforSpinner("Events.Bookings.lnkLOVResTypeAdv", 20, "clickable");
		boolean flag = false;
		java.util.List<WebElement> rulesText = elements("Events.Bookings.ruleSectionTableCancellation");
		for (int i = 0; i <= rulesText.size(); i++) {
			if(rulesText.get(i).getText().contains(blockMap.get("CancellationRules"))){
				rulesText.get(i).click();
				flag = true;
				break;
			}
		}
		if (!flag ){
			clickWaitforSpinner("Events.Bookings.ruleSectionTableCancellation", 20, "clickable");
		}
		clickWaitforSpinner("Events.SelectPropertyButton", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");
		clickWaitforSpinner("Events.Bookings.cancellationExpland", 20, "clickable");
		java.util.List<WebElement> strSearchelemCancel = elements("Events.Bookings.lnkActionsLBlockNotes");
		for (int i = 0; i <= strSearchelemCancel.size(); i++) {
			if (i == 1){
				strSearchelemCancel.get(i).click();
			System.out.println("Clicked on CancellationActions button");
			}
		}
		clickWaitforSpinner("Events.Bookings.lnkActionsEditLBlockNotes", 20, "visible");
		clear("Events.Bookings.txtCancelAmount");
		textBox("Events.Bookings.txtCancelAmount","10",50,"presence");
		clickWaitforSpinner("Events.Bookings.lnkSaveLoadGrid", 20, "visible");

		clickWaitforSpinner("Events.Bookings.cancellationExpland", 20, "clickable");
		java.util.List<WebElement> strSearchelemCan = elements("Events.Bookings.lnkActionsLBlockNotes");
		for (int i = 0; i <= strSearchelemCan.size(); i++) {
			if (i == 1){
				strSearchelemCan.get(i).click();
			System.out.println("Clicked on CancellationActions button");
			}
		}
		clickWaitforSpinner("Events.Bookings.lnkDeleteNote", 20, "visible");
		clickWaitforSpinner("Events.Bookings.lnkBtnDeleteNote", 20, "visible");
		java.util.List<WebElement> strSearchelemCanVerify = elements("Events.Bookings.lnkActionsLBlockNotes");
		if (strSearchelemCanVerify.size()==strSearchelemCan.size()){
			System.out.println("******Cancellation NOT deleted Successfully******");
			logger.log(LogStatus.PASS, "The Cancellation is NOT deletd Successfully");
		}else{
			System.out.println("******Cancellation deleted Successfully******");
			logger.log(LogStatus.PASS, "The Cancellation is deletd Successfully");
		}
		
		}catch(Exception e){
			logger.log(LogStatus.FAIL, " Could not update the configuration " + e.getMessage());
			Assert.fail(" Could not update the configuration");
			throw (e);
		}
	}


}