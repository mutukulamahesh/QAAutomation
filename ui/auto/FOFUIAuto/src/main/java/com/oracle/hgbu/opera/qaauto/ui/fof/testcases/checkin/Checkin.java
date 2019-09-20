package com.oracle.hgbu.opera.qaauto.ui.fof.testcases.checkin;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.fof.component.checkin.CheckinPage;
import com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation.ReservationPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class Checkin extends Utils {
		
	/*******************************************************************
	-  Description: Verify user is able to create room routing
	- Input: CONFIRMATION_NUMBER, NAME, TRANSACTION_CODE
	- Output: room routing should be created
	- Author: @author vnadipal
	- Date: 12/12/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"}, priority = 1)
	public void createRoomRouting() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Create Room Routing </b>");
			//logger = report.startTest(methodName, "Create Room Routing").assignCategory("sanity", "Cloud.Cashiering");
			Utils.takeScreenshot(driver, methodName);
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "createRoomRouting");
			System.out.println("Config Map: " + configMap);
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String confNumber = ReservationPage.createReservationFromLTB(resvMap);
			//String confNumber = ReservationPage.createReservationFromLTB();
			CheckinPage.checkInReservation(confNumber);
			waitForSpinnerToDisappear(5);
			//String confNumber = "371812";
			
			//Thread.sleep(3000);
			// Clicked on Front Desk Menu
			//jsClick("Checkin.Cashiering.menuFrontDesk", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Bookings Menu
			jsClick("Checkin.Cashiering.menuBookings", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Reservations Menu Item
			jsClick("Checkin.Cashiering.menuReservations", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Manage Reservation Menu Item
			jsClick("Checkin.Cashiering.menuItemManageRservation", 100, "presence");
			
			waitForSpinnerToDisappear(10);
			textBox("Checkin.Cashiering.ManageReservation.txtConfirmationNumber", confNumber);
			
			waitForSpinnerToDisappear(5);
			// Clicked on Search button
			jsClick("Checkin.Cashiering.ManageReservation.btnSearch", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on View Table
			if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
				jsClick("Checkin.Cashiering.RoomRouting.linkViewTable", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Show All link
			mouseHover("Checkin.Cashiering.RoomRouting.linkShowAll");
			jsClick("Checkin.Cashiering.RoomRouting.linkShowAll", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Routing Instructions link
			jsClick("Checkin.Cashiering.RoomRouting.linkRoutingInstructions", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on New link
			jsClick("Checkin.Cashiering.RoomRouting.linkNew", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			WebElement test3 = element("Checkin.Cashiering.RoomRouting.radioRoom");
			Actions actions3 = new Actions(driver);
			actions3.moveToElement(test3).click().build().perform();
			waitForSpinnerToDisappear(5);
			
			// Clicked on Payee LOV
			jsClick("Checkin.Cashiering.RoomRouting.lovPayee", 100, "presence");
			Thread.sleep(10000);
			
			driver.switchTo().frame(3);
			Thread.sleep(8000);
			textBox("Checkin.Cashiering.RoomRouting.PayeeSearch.txtName", configMap.get("NAME"));
			Thread.sleep(5000);

			// Clicked on Search button
			jsClick("Checkin.Cashiering.RoomRouting.PayeeSearch.btnSearch", 100, "presence");
			waitForSpinnerToDisappear(10);
			
			// Clicked on Select button
			jsClick("Checkin.Cashiering.RoomRouting.PayeeSearch.btnSelect", 100, "presence");
			Thread.sleep(10000);
			
			textBox("Checkin.Cashiering.RoomRouting.txtFilter", configMap.get("TRANSACTION_CODE"));
			waitForSpinnerToDisappear(5);
			
			// Clicked on Go button
			jsClick("Checkin.Cashiering.RoomRouting.btnFilterGo", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			// Clicked on first row
			jsClick("Checkin.Cashiering.RoomRouting.rowSelectRecord", 100, "presence");
			waitForSpinnerToDisappear(5);
						
			// Clicked on Add button
			jsClick("Checkin.Cashiering.RoomRouting.btnAdd", 100, "presence");
			waitForSpinnerToDisappear(10);
			
			// Clicked on Save button
			jsClick("Checkin.Cashiering.RoomRouting.btnSave", 100, "presence");
			
			waitForSpinnerToDisappear(15);
			System.out.println("text: " + getText("Checkin.Cashiering.RoomRouting.validationDiv"));
			if(getText("Checkin.Cashiering.RoomRouting.validationDiv").equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
				logger.log(LogStatus.PASS, "Room Routing for code " + configMap.get("TRANSACTION_CODE") +" is successfully created");
			}
			else {
				logger.log(LogStatus.FAIL, "Failed to create Room Routing");
			}
			
			Thread.sleep(5000);
			// Clicked on Close button
			jsClick("Checkin.Cashiering.RoomRouting.linkClose", 100, "presence");		
			
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			LoginPage.Logout();
			Thread.sleep(3000);
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to checkin the reservation
	- Input: CONFIRMATION_NUMBER,Room Type, Room Number, Payment Type 
	- Output: Reservation must be checked in
	- Author: @author jasatis
	- Date: 12/24/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 3)
	public void CheckInReservationfromArrivals() throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CheckInReservationfromArrivals");            
            //Create a reservation and store the confirmation number
			String confirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CheckInReservationfromArrivals","ConfirmationNum", confirmationNum);
			// Clicked on FrontDesk Menu
			jsClick("Checkin.FrontDesk_menu", 100, "presence");
			// Clicked on Arrivals Menu
			jsClick("Checkin.Arrivals_menu", 100, "presence");
			waitForSpinnerToDisappear(30);
			if(isExists("Checkin.ModifySearchCriteria"))
			{
				jsClick("Checkin.ModifySearchCriteria",100,"presence");
				waitForSpinnerToDisappear(30);
			}
			// Enter the Confirmation number
			textBox("Checkin.Confirmationnumbertxt", confirmationNum);
			tabKey("Checkin.Confirmationnumbertxt");
			waitForSpinnerToDisappear(30);
			jsClick("Checkin.LinkSearch", 100, "presence");
			waitForSpinnerToDisappear(40);
			jsClick("Checkin.LinkListView", 100, "presence");
			waitForSpinnerToDisappear(40);
			System.out.println(element("Checkin.Chkboxforselectingreservation").isSelected());
			if(!element("Checkin.Chkboxforselectingreservation").isSelected())
				click("Checkin.Chkboxforselectingreservation");
			waitForSpinnerToDisappear(40);
			click("Checkin.btnCheckIn");
			waitForSpinnerToDisappear(40);
			if(isExists("Checkin.breaklockconfirmationpopup"))
			{
				click("Checkin.breaklockbutton");
				waitForSpinnerToDisappear(40);
			}
			Utils.WebdriverWait(30, "Checkin.checkbox_ReservationCheckIn_Clean", "clickable");
            if (!isSelected("Checkin.checkbox_ReservationCheckIn_Clean", "Clean CheckBox")) {
                Utils.jsClick("Checkin.checkbox_ReservationCheckIn_Clean");
            } else {
                System.out.println("Clean CheckBox is Selected");
            }
            logger.log(LogStatus.PASS, "Selecting Clean checkbox");
            Utils.waitForSpinnerToDisappear(40);
            // Clicking on Search button
            Utils.WebdriverWait(50, "Checkin.btn_ReservationCheckIn_Search", "clickable");
            Utils.click("Checkin.btn_ReservationCheckIn_Search");
            logger.log(LogStatus.PASS, "Clicking on Search button");
            System.out.println("Clicked on Search button");          
            Utils.waitForSpinnerToDisappear(100);
            if(Utils.isExists("Checkin.link_ReservationCheckIn_SelectRoom")) {
                // Clicking on Select Room link
                Utils.WebdriverWait(50, "Checkin.link_ReservationCheckIn_SelectRoom", "clickable");
                Utils.jsClick("Checkin.link_ReservationCheckIn_SelectRoom");
                logger.log(LogStatus.PASS, "Clicking on Select Room link");
                System.out.println("Clicking on Select Room link");
                Utils.waitForSpinnerToDisappear(100);
            }else {            	
                Utils.jsClick("Checkin.link_RoomTypeSearchIcon", 100, "clickable");
                Utils.waitForSpinnerToDisappear(100);
                Utils.click("Checkin.link_SelectAllLink", 100, "clickable");
                Utils.waitForSpinnerToDisappear(100);
                Utils.click("Checkin.btn_RoomTypeSelect", 100, "clickable");
                Utils.waitForSpinnerToDisappear(100);
                Utils.click("Checkin.SearchButton");
                Utils.waitForSpinnerToDisappear(100);
                Utils.click("Checkin.link_ReservationCheckIn_SelectRoom", 100, "clickable");
                Utils.waitForSpinnerToDisappear(100);
                if(Utils.isExists("Checkin.checkbox_UpdateRoomType")){
                    Utils.click("Checkin.checkbox_UpdateRoomType", 100, "clickable");
                    Utils.waitForSpinnerToDisappear(100);
                    Utils.click("Checkin.btn_AssignRoom", 100, "clickable");
                    Utils.waitForSpinnerToDisappear(100);
                }
            }
            waitForPageLoad(100); 
            // Clicking on Accept RoomSelection Button
            if (isExists("Checkin.btn_ReservationCheckIn_AcceptRoomSelection")) {
                Utils.WebdriverWait(30, "Checkin.btn_ReservationCheckIn_AcceptRoomSelection", "clickable");
                Utils.jsClick("Checkin.btn_ReservationCheckIn_AcceptRoomSelection");
                System.out.println("Selected Accept Room Selection..");
                logger.log(LogStatus.PASS, "Selected Accept Room Selection Button");
            }
            waitForSpinnerToDisappear(100);
			jsClick("Checkin.CompleteCheckInLink");
			waitForSpinnerToDisappear(100);
			if(Utils.isExists("Checkin.popup_RegistrationCard")) {		
				Utils.Wait(3000);
				Utils.click("Checkin.link_PrintRegistrationNo", 100, "clickable");				
			}
			
			if(Utils.isExists("Checkin.popup_PrintRegistrationCard")) {		
				Utils.Wait(3000);
				Utils.click("Checkin.link_ClodePrintRegistrationCard", 100, "clickable");				
			}

			if(Utils.isExists("Checkin.txt_CheckInSuccessMessage", 100, "presence")) {
				Utils.click("Checkin.btn_ReservationCheckIn_PopupOk", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
			}
			if(Utils.isExists("Checkin.popup_CreateRoomKeysWindow")) {
				Utils.Wait(3000);
				Utils.click("Checkin.link_CreateRoomKeysCancel", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);				
			}
			if(Utils.isExists("Checkin.btn_GoToReservation",100,"presence")){
                Utils.Wait(3000);
                Utils.jsClick("Checkin.btn_GoToReservation"); 
                Utils.waitForSpinnerToDisappear(100);
            }
            if (getText("Checkin.txt_ReservationCheckIn_checkInstatus").contains("Checked In")){
                logger.log(LogStatus.PASS, "Reservation Successfully CheckedIn");
            }else {
                logger.log(LogStatus.FAIL, "Reservation NOT CheckedIn properly");
            }
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		}
			//Logout from Application
				Thread.sleep(3000);
				Utils.tearDown();
	}



	/*******************************************************************
	-  Description: Verify user is able to generate confirmation letter
	- Input: All mandatory details to create reservation
	- Output: Generate confirmation letter
	- Author: Girish
	- Date: 18/12/18
	 ********************************************************************/
	@Test(groups = {"BAT"}, priority = 4)
	public void confirmationLetterOfReservation() throws Exception {
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
			
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Confirmation letter of reservations </b>");
			Utils.takeScreenshot(driver, methodName);
			ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "confirmationLetterOfReservation");						
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String confNumber = ReservationPage.createReservationFromLTB(resvMap);
			waitForPageLoad(5000);
			ReservationPage.reservationSearch(confNumber);
			waitForSpinnerToDisappear(40);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo",100,"clickable");
			waitForSpinnerToDisappear(20);
			//Verify i want to page
			boolean blnIWantPage = isExists("Checkin.Option.txt_ConfirmationLetters_IWantToPage");
			if(blnIWantPage);
			{
				logger.log(LogStatus.PASS,"Navigated to I Want To Page");
				waitForSpinnerToDisappear(10);		
				// Clicked on View Table
				if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
					jsClick("Checkin.Cashiering.RoomRouting.linkViewTable",50, "presence");		
				
				// Clicked on Show All link
				jsClick("Checkin.Option.link_ShowAll",80, "presence");
				waitForSpinnerToDisappear(40);
				// Selecting Confirmation Letter link in Reservation Overview
				click("Checkin.Option.link_Reservation_ConfirmationLetters",80,"clickable");
				waitForSpinnerToDisappear(40);
				// Validating Confirmation Letter page
				boolean blnConfirmationPage = isExists("Checkin.Option.txt_ConfirmationLetters_ConfirmationPage");
				if(blnConfirmationPage){		
					logger.log(LogStatus.PASS,"navigated to Confirmation Page**");
					//Confirmation letter Menu
					click("Checkin.Option.link_ConfirmationLetters_Menu",80,"clickable");
					waitForSpinnerToDisappear(40);
					//Select Print/Preview link
					click("Checkin.Option.link_ConfirmationLetters_PrintPreview",80,"clickable");
					waitForSpinnerToDisappear(40);
		            //Verify confirmation letter drop down have values
			     	java.util.List<WebElement> allOptions = selectByAllOptions("Checkin.Option.txt_ConfirmationLetters_New_Select");
					int size = allOptions.size();
					if (size <= 1) {
						logger.log(LogStatus.FAIL,"**No values in Confirmation letter dropdown to select**");
					}else {
					//Select confirmation letter
					selectBy("Checkin.Option.txt_ConfirmationLetters_New_Select", "text", "ConfirmationLetter");
					logger.log(LogStatus.PASS, "Selected confirmation letter from drop down");
					waitForSpinnerToDisappear(10);
					//Click on Send button
					click("Checkin.Option.btn_ConfirmationLetters_Send",100,"clickable");
					Thread.sleep(15000);
					
					ArrayList<String> windowTab = new ArrayList<String>(driver.getWindowHandles());				
					driver.switchTo().window(windowTab.get(1));   
					Thread.sleep(15000);
					
					//Open the confirmation letter in other browser
				    boolean blnStats = driver.findElement(By.xpath("//*[@type ='application/pdf']")).isEnabled();
					    if(blnStats){		        
					            logger.log(LogStatus.PASS,"Confirmation letter have open successfully");
					           }
					    else{
					    	logger.log(LogStatus.FAIL,"***Issue observed during confirmation letter launch***");
					    }   	
					//Create object of Action class
					/*Actions action = new Actions(driver);				 
					// Send multiple keys using action class
					WebElement ele = driver.findElement(By.xpath("//*[@name='plugin']"));
					action.sendKeys(ele,Keys.chord(Keys.CONTROL,"a"),Keys.chord(Keys.CONTROL,"c")).perform();							
					//Copy data to clip board
		            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		            Transferable contents = clipboard.getContents(null);
		            String strContent = (String) contents.getTransferData(DataFlavor.stringFlavor);
		            System.out.println(strContent);	            
		            clipboard=null;
		            //Folio content validation    	
		            if(strContent.contains("Thank you for making your reservation")){
		                logger.log(LogStatus.PASS,"Confirmation letter content have verified successfully");
		            }else{
		            	logger.log(LogStatus.FAIL,"***Issue observed during confirmation letter content verification***");
		            }*/   				
				  }
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver,methodName);
			logger.log(LogStatus.FAIL, "Confirmation letter for reservation have not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		//Logout from Application
		LoginPage.Logout();
		Thread.sleep(3000);
		Utils.tearDown();
	}
	
	/*******************************************************************
	-  Description: Verify user is able to post a deposit to a reservation
	- Input: All mandatory details to post deposit
	- Output: Post deposit to a reservation
	- Author: apentam
	- Date: 07/01/19
	 ********************************************************************/
	
	@Test(priority = 4,groups = {"SANITY"})
	public void postDeposit() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify that user is able to post a deposit to the reservation </b>");			
			HashMap<String, String> depositMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "PostDeposit", "postDeposit");
			// Navigating to FrontDesk Menu
			click("Checkin.FrontDesk_menu",100,"clickable");
			// Navigating to Arrivals Menu
			click("Checkin.Arrivals_menu",100,"clickable");
			click("Checkin.btn_Search",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_FirstIwantTo",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_ShowAll",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_DepositCancellation",100,"clickable");
			Utils.waitForSpinnerToDisappear(20);
			CheckinPage.postDeposit(depositMap);

	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}		
		Utils.tearDown();
	}
	
	/*******************************************************************
	-  Description: Verify user is able to attach fixed charges to post on daily basis
	- Input: All mandatory details to create reservation and post the charges
	- Output: Post fixed charges to a reservation daily
	- Author: apentam
	- Date: 01/09/2019
	 ********************************************************************/	
	@Test(priority = 5,groups = {"SANITY"})
	public void postFixedChargesDaily() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify that user is able to attach fixed charges to post on daily basis </b>");			
			HashMap<String, String> fixedChargesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "FixedCharges", "postFixedChargesDaily");

			// Navigating to FrontDesk Menu
			click("Checkin.FrontDesk_menu",100,"clickable");
			// Navigating to Arrivals Menu
			click("Checkin.Arrivals_menu",100,"clickable");
			click("Checkin.btn_Search",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_FirstIwantTo",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_ShowAll",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_FixedCharges",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);			
			CheckinPage.postFixedCharges(fixedChargesMap);
	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		Utils.tearDown();
	}
	
	/*******************************************************************
	-  Description: Verify user is able to attach fixed charges to post on daily basis
	- Input: All mandatory details to create reservation and post the charges
	- Output: Post fixed charges to a reservation only once
	- Author: apentam
	- Date: 01/09/2019
	 ********************************************************************/	
	@Test(priority = 6,groups = {"SANITY"})
	public void postFixedChargesOnce() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify that user is able to attach fixed charges to post only once </b>");			
			HashMap<String, String> fixedChargesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "FixedCharges", "postFixedChargesOnce");

			// Navigating to FrontDesk Menu
			click("Checkin.FrontDesk_menu",100,"clickable");
			// Navigating to Arrivals Menu
			click("Checkin.Arrivals_menu",100,"clickable");
			click("Checkin.btn_Search",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_FirstIwantTo",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_ShowAll",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_FixedCharges",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);			
			CheckinPage.postFixedCharges(fixedChargesMap);
	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		Utils.tearDown();
	}
	
	/*******************************************************************
	-  Description: Verify user is able to create folio routing
	- Input: CONFIRMATION_NUMBER, WINDOW, TRANSACTION_CODES
	- Output: folio routing should be created
	- Author: @author vnadipal
	- Date: 14/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"}, priority = 7)
	public void createFolioRouting() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Create Folio Routing </b>");
			//logger = report.startTest(methodName, "Create Room Routing").assignCategory("sanity", "Cloud.Cashiering");
			Utils.takeScreenshot(driver, methodName);
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "createFolioRouting");
			System.out.println("Config Map: " + configMap);
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String confNumber = ReservationPage.createReservationFromLTB(resvMap);
			//String confNumber = ReservationPage.createReservationFromLTB();
			CheckinPage.checkInReservation(confNumber);
			waitForSpinnerToDisappear(5);
			//String confNumber = "384296";
			
			waitForSpinnerToDisappear(5);
			// Clicked on Bookings Menu
			jsClick("Checkin.Cashiering.menuBookings", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Reservations Menu Item
			jsClick("Checkin.Cashiering.menuReservations", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Manage Reservation Menu Item
			jsClick("Checkin.Cashiering.menuItemManageRservation", 100, "presence");
			
			waitForSpinnerToDisappear(10);
			textBox("Checkin.Cashiering.ManageReservation.txtConfirmationNumber", confNumber);
			
			waitForSpinnerToDisappear(5);
			// Clicked on Search button
			jsClick("Checkin.Cashiering.ManageReservation.btnSearch", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on View Table
			if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
				jsClick("Checkin.Cashiering.RoomRouting.linkViewTable", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Show All link
			jsClick("Checkin.Cashiering.FolioRouting.linkShowAll", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Routing Instructions link
			jsClick("Checkin.Cashiering.RoomRouting.linkRoutingInstructions", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on New link
			jsClick("Checkin.Cashiering.RoomRouting.linkNew", 100, "presence");
			
			// Entered Folio
			selectBy("Checkin.Cashiering.FolioRouting.dropdownFolio", "text", configMap.get("WINDOW"));
			logger.log(LogStatus.INFO, "Selected Folio as: " + configMap.get("WINDOW"));
			
			if(configMap.get("TRANSACTION_CODE").contains(",")) {
				String[] trxCodesList = configMap.get("TRANSACTION_CODE").split(",");
				
				for(String code : trxCodesList) {
					textBox("Checkin.Cashiering.RoomRouting.txtFilter", code);
					waitForSpinnerToDisappear(5);
					
					// Clicked on Go button
					jsClick("Checkin.Cashiering.RoomRouting.btnFilterGo", 100, "presence");
					waitForSpinnerToDisappear(5);
					
					// Clicked on first row
					jsClick("Checkin.Cashiering.RoomRouting.rowSelectRecord", 100, "presence");
					waitForSpinnerToDisappear(5);
								
					// Clicked on Add button
					jsClick("Checkin.Cashiering.RoomRouting.btnAdd", 100, "presence");
					waitForSpinnerToDisappear(10);
				}
			}
			
			// Clicked on Save button
			jsClick("Checkin.Cashiering.RoomRouting.btnSave", 100, "presence");
			
			waitForSpinnerToDisappear(15);
			System.out.println("text: " + getText("Checkin.Cashiering.FolioRouting.validationDiv"));
			
			if(configMap.get("TRANSACTION_CODE").contains(",")) {
				String[] trxCodesList = configMap.get("TRANSACTION_CODE").split(",");
				
				for(String code : trxCodesList) {
					if(getText("Checkin.Cashiering.FolioRouting.validationDiv").contains(code)) {
						logger.log(LogStatus.PASS, "Folio Routing for code " + code +" is successfully created");
					}
					else {
						logger.log(LogStatus.FAIL, "Failed to create Folio Routing for " + code);
					}
				}
			}
			
			Thread.sleep(5000);
			// Clicked on Close button
			jsClick("Checkin.Cashiering.RoomRouting.linkClose", 100, "presence");		
			
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to split transaction by amount
	- Input: CONFIRMATION_NUMBER, AMOUNT, TRANSACTION_CODE
	- Output: transaction should be split by amount
	- Author: @author vnadipal
	- Date: 16/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"}, priority = 8)
	public void splitTransactionByAmount() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Split Transaction By Amount </b>");
			//logger = report.startTest(methodName, "Create Room Routing").assignCategory("sanity", "Cloud.Cashiering");
			Utils.takeScreenshot(driver, methodName);
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "splitTransactionByAmount");
			System.out.println("Config Map: " + configMap);
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String confNumber = ReservationPage.createReservationFromLTB(resvMap);
			//String confNumber = "389930";
			configMap.put("CONFIRMATION_NUMBER", confNumber);
			
			CheckinPage.checkInReservation(confNumber);
			waitForSpinnerToDisappear(5);
			//String confNumber = "383158";
			
			CheckinPage.createFolioRouting(configMap);
			waitForSpinnerToDisappear(5);
			
			CheckinPage.postCharges(configMap);
			waitForSpinnerToDisappear(5);
			
			// Clicked on Bookings Menu
			jsClick("Checkin.Cashiering.menuBookings", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Reservations Menu Item
			jsClick("Checkin.Cashiering.menuReservations", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Manage Reservation Menu Item
			jsClick("Checkin.Cashiering.menuItemManageRservation", 100, "presence");
			
			waitForSpinnerToDisappear(10);
			// Entered Confirmation Number
			textBox("Checkin.Cashiering.ManageReservation.txtConfirmationNumber", confNumber);
			
			waitForSpinnerToDisappear(5);
			// Clicked on Search button
			jsClick("Checkin.Cashiering.ManageReservation.btnSearch", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on View Table
			if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
				jsClick("Checkin.Cashiering.RoomRouting.linkViewTable", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Billing link
			jsClick("Checkin.Cashiering.SplitTransactions.linkGoToBilling", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(isExists("Checkin.Cashiering.SplitTransactions.txtCashierId")) {
				//Entered Cashier ID
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierId", OR.getConfig("CashierUserID"));
				tabKey("Checkin.Cashiering.SplitTransactions.txtCashierId");	
				waitForSpinnerToDisappear(10);
				
				//Entered Cashier Password
				//WebdriverWait(100, "Checkin.Cashiering.SplitTransactions.txtCashierPassword", "stale");
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", OR.getConfig("CashierPassword"));
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", OR.getConfig("CashierPassword"), 100, "presence");
				waitForSpinnerToDisappear(5);
				
				// Clicked on login button
				jsClick("Checkin.Cashiering.SplitTransactions.btnCashierLogin", 100, "presence");
				waitForSpinnerToDisappear(5);
			}
			
			// Select Window
			selectBy("Checkin.Cashiering.SplitTransactions.dropdownFolio", "text", configMap.get("WINDOW"));
			waitForSpinnerToDisappear(5);
			
			// Clicked on transaction code button
			jsClick("Checkin.Cashiering.SplitTransactions.selectTransactionCodeRow", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			// Clicked on Link More link
			jsClick("Checkin.Cashiering.SplitTransactions.linkLinkMore", 100, "presence");
			
			// Clicked on Quick Split menu
			jsClick("Checkin.Cashiering.SplitTransactions.linkQuickSplit", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			// Select Amount radio button
			if(!element("Checkin.Cashiering.SplitTransactions.radioSplitByAmount").isSelected())
				jsClick("Checkin.Cashiering.SplitTransactions.radioSplitByAmount", 100, "presence");
			
			//Entered Cashier ID
			textBox("Checkin.Cashiering.SplitTransactions.txtAmount", configMap.get("SPLIT_AMOUNT"));
			waitForSpinnerToDisappear(5);
			
			// Clicked on Link More link
			jsClick("Checkin.Cashiering.SplitTransactions.btnSplit", 100, "presence");
			waitForSpinnerToDisappear(15);
			
			List<WebElement>  rows = Utils.elements("Checkin.Cashiering.SplitTransactions.validationCodeColumn"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					System.out.println("text: " + rows.get(i).getText());
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
						String amount = getText("Checkin.Cashiering.SplitTransactions.validationAmountColumn");
						System.out.println("Amount: " + amount);
						if(amount.equalsIgnoreCase(configMap.get("SPLIT_AMOUNT"))) {
							logger.log(LogStatus.PASS, "Row " + i + ": Amount for Transaction code " + configMap.get("TRANSACTION_CODE") + " after split is " + configMap.get("SPLIT_AMOUNT"));
							flag = true;
						}
					}
					else {
						System.out.println("Transaction " + configMap.get("TRANSACTION_CODE") + "is not split by amount " + configMap.get("SPLIT_AMOUNT"));						
					}
				}
			}
			
			if(flag) {
				logger.log(LogStatus.PASS, "Transaction code " + configMap.get("TRANSACTION_CODE") + " with amount " + configMap.get("PRICE") + " is successfully split by amount " + configMap.get("SPLIT_AMOUNT"));
			}
			
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to split transaction by percentage
	- Input: CONFIRMATION_NUMBER, AMOUNT, TRANSACTION_CODE
	- Output: transaction should be split by amount
	- Author: @author vnadipal
	- Date: 16/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"}, priority = 9)
	public void splitTransactionByPercentage() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Split Transaction By Percentage </b>");
			Utils.takeScreenshot(driver, methodName);
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "splitTransactionByPercentage");
			System.out.println("Config Map: " + configMap);
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String confNumber = ReservationPage.createReservationFromLTB(resvMap);
			
			CheckinPage.checkInReservation(confNumber);
			waitForSpinnerToDisappear(5);
			//String confNumber = "389981";
			configMap.put("CONFIRMATION_NUMBER", confNumber);
			
			CheckinPage.createFolioRouting(configMap);
			waitForSpinnerToDisappear(5);
			
			CheckinPage.postCharges(configMap);
			waitForSpinnerToDisappear(5);
			
			int percentage = Integer.parseInt(configMap.get("SPLIT_PERCENTAGE"));
			System.out.println("percentage: " + percentage);
			
			float price = Float.parseFloat(configMap.get("PRICE"));
			System.out.println("price: " + price);
			
			float finalAmount1 = (percentage * price)/100;
			System.out.println("Final Amount1: " + finalAmount1);

			float finalAmount2 = (price - finalAmount1);
			System.out.println("Final Amount2: " + finalAmount2);
			
			// Clicked on Bookings Menu
			jsClick("Checkin.Cashiering.menuBookings", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Reservations Menu Item
			jsClick("Checkin.Cashiering.menuReservations", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Manage Reservation Menu Item
			jsClick("Checkin.Cashiering.menuItemManageRservation", 100, "presence");
			
			waitForSpinnerToDisappear(10);
			// Entered Confirmation Number
			textBox("Checkin.Cashiering.ManageReservation.txtConfirmationNumber", confNumber);
			
			waitForSpinnerToDisappear(5);
			// Clicked on Search button
			jsClick("Checkin.Cashiering.ManageReservation.btnSearch", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on View Table
			if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
				jsClick("Checkin.Cashiering.RoomRouting.linkViewTable", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Billing link
			jsClick("Checkin.Cashiering.SplitTransactions.linkGoToBilling", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(isExists("Checkin.Cashiering.SplitTransactions.txtCashierId")) {
				//Entered Cashier ID
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierId", OR.getConfig("CashierUserID"));
				tabKey("Checkin.Cashiering.SplitTransactions.txtCashierId");	
				waitForSpinnerToDisappear(10);
				
				//Entered Cashier Password
				//WebdriverWait(100, "Checkin.Cashiering.SplitTransactions.txtCashierPassword", "stale");
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", OR.getConfig("CashierPassword"));
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", OR.getConfig("CashierPassword"), 100, "presence");
				waitForSpinnerToDisappear(5);
				
				// Clicked on login button
				jsClick("Checkin.Cashiering.SplitTransactions.btnCashierLogin", 100, "presence");
				waitForSpinnerToDisappear(5);
			}
			
			// Select Window
			selectBy("Checkin.Cashiering.SplitTransactions.dropdownFolio", "text", configMap.get("WINDOW"));
			waitForSpinnerToDisappear(5);
			
			// Clicked on transaction code button
			jsClick("Checkin.Cashiering.SplitTransactions.selectTransactionCodeRow", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			// Clicked on Link More link
			jsClick("Checkin.Cashiering.SplitTransactions.linkLinkMore", 100, "presence");
			
			// Clicked on Quick Split menu
			jsClick("Checkin.Cashiering.SplitTransactions.linkQuickSplit", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			// Select Amount radio button
			if(!element("Checkin.Cashiering.SplitTransactions.radioSplitByPercentage").isSelected())
				jsClick("Checkin.Cashiering.SplitTransactions.radioSplitByPercentage", 100, "presence");
			
			//Entered Cashier ID
			textBox("Checkin.Cashiering.SplitTransactions.txtPercentage", configMap.get("SPLIT_PERCENTAGE"));
			waitForSpinnerToDisappear(5);
			
			// Clicked on Link More link
			jsClick("Checkin.Cashiering.SplitTransactions.btnSplit", 100, "presence");
			waitForSpinnerToDisappear(10);
			
			List<WebElement>  rows = Utils.elements("Checkin.Cashiering.SplitTransactions.validationCodeColumn"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					System.out.println("text: " + rows.get(i).getText());
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
						String amount = getText("Checkin.Cashiering.SplitTransactions.validationAmountColumn");
						float amt = Float.parseFloat(amount);
						System.out.println("Amount: " + amount);
						System.out.println("final amount1: " + finalAmount1);
						System.out.println("final amount2: " + finalAmount2);
						if((amt == finalAmount1) || (amt == finalAmount2)) {
							if(amt == finalAmount1)
								logger.log(LogStatus.PASS, "Row " + i + ": Amount for Transaction code " + configMap.get("TRANSACTION_CODE") + " after " + configMap.get("SPLIT_PERCENTAGE") + "% split is " + finalAmount1);
							if(amt == finalAmount2)
								logger.log(LogStatus.PASS, "Row " + i + ": Amount for Transaction code " + configMap.get("TRANSACTION_CODE") + " after " + configMap.get("SPLIT_PERCENTAGE") + "% split is " + finalAmount2);
							flag = true;
						}
					}
					else {
						System.out.println("Transaction " + configMap.get("TRANSACTION_CODE") + "is not split by amount " + configMap.get("SPLIT_PERCENTAGE"));						
					}
				}
			}
			
			/*if(flag) {
				logger.log(LogStatus.PASS, "Transaction code " + configMap.get("TRANSACTION_CODE") + " with amount " + configMap.get("PRICE") + " is successfully split by amount " + configMap.get("SPLIT_PERCENTAGE"));
			}*/
			
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to split transaction by quantity
	- Input: CONFIRMATION_NUMBER, AMOUNT, QUANTITY, TRANSACTION_CODE
	- Output: transaction should be split by quantity
	- Author: @author vnadipal
	- Date: 25/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"}, priority = 8)
	public void splitTransactionByQuantity() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Split Transaction By Quantity </b>");
			//logger = report.startTest(methodName, "Create Room Routing").assignCategory("sanity", "Cloud.Cashiering");
			Utils.takeScreenshot(driver, methodName);
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "splitTransactionByQuantity");
			System.out.println("Config Map: " + configMap);
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String confNumber = ReservationPage.createReservationFromLTB(resvMap);
			//String confNumber = "398895";
			configMap.put("CONFIRMATION_NUMBER", confNumber);
			
			CheckinPage.checkInReservation(confNumber);
			waitForSpinnerToDisappear(5);
			//String confNumber = "383158";
			
			CheckinPage.createFolioRouting(configMap);
			waitForSpinnerToDisappear(5);
			
			CheckinPage.postCharges(configMap);
			waitForSpinnerToDisappear(5);
			
			// Clicked on Bookings Menu
			jsClick("Checkin.Cashiering.menuBookings", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Reservations Menu Item
			jsClick("Checkin.Cashiering.menuReservations", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Manage Reservation Menu Item
			jsClick("Checkin.Cashiering.menuItemManageRservation", 100, "presence");
			
			waitForSpinnerToDisappear(10);
			// Entered Confirmation Number
			textBox("Checkin.Cashiering.ManageReservation.txtConfirmationNumber", confNumber);
			
			waitForSpinnerToDisappear(5);
			// Clicked on Search button
			jsClick("Checkin.Cashiering.ManageReservation.btnSearch", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on View Table
			if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
				jsClick("Checkin.Cashiering.RoomRouting.linkViewTable", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Billing link
			jsClick("Checkin.Cashiering.SplitTransactions.linkGoToBilling", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(isExists("Checkin.Cashiering.SplitTransactions.txtCashierId")) {
				//Entered Cashier ID
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierId", OR.getConfig("CashierUserID"));
				tabKey("Checkin.Cashiering.SplitTransactions.txtCashierId");	
				waitForSpinnerToDisappear(10);
				
				//Entered Cashier Password
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", OR.getConfig("CashierPassword"));
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", OR.getConfig("CashierPassword"), 100, "presence");
				waitForSpinnerToDisappear(5);
				
				// Clicked on login button
				jsClick("Checkin.Cashiering.SplitTransactions.btnCashierLogin", 100, "presence");
				waitForSpinnerToDisappear(5);
			}
			
			// Select Window
			selectBy("Checkin.Cashiering.SplitTransactions.dropdownFolio", "text", configMap.get("WINDOW"));
			waitForSpinnerToDisappear(5);
			
			// Clicked on transaction code button
			jsClick("Checkin.Cashiering.SplitTransactions.selectTransactionCodeRow", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			// Clicked on Link More link
			jsClick("Checkin.Cashiering.SplitTransactions.linkLinkMore", 100, "presence");
			
			// Clicked on Quick Split menu
			jsClick("Checkin.Cashiering.SplitTransactions.linkQuickSplit", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			// Select Quantity radio button
			if(!element("Checkin.Cashiering.SplitTransactions.radioSplitByQuantity").isSelected())
				jsClick("Checkin.Cashiering.SplitTransactions.radioSplitByQuantity", 100, "presence");
			
			//Entered Split Quantity
			textBox("Checkin.Cashiering.SplitTransactions.txtQuantity", configMap.get("SPLIT_QUANTITY"));
			waitForSpinnerToDisappear(5);
			
			// Clicked on Split button
			jsClick("Checkin.Cashiering.SplitTransactions.btnSplit", 100, "presence");
			waitForSpinnerToDisappear(10);
			
			float price = Float.parseFloat(configMap.get("PRICE"));
			float splitAmount = (price * Integer.parseInt(configMap.get("QUANTITY")));
			System.out.println("Split Amount: " + splitAmount);
			
			List<WebElement>  rows = Utils.elements("Checkin.Cashiering.SplitTransactions.validationCodeColumn"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					System.out.println("text: " + rows.get(i).getText());
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
						String amount = getText("Checkin.Cashiering.SplitTransactions.validationAmountColumn");
						float amt = Float.parseFloat(amount);
						System.out.println("Amount: " + amt);
						if(amt == splitAmount) {
							logger.log(LogStatus.PASS, "Row " + i + ": Transaction code " + configMap.get("TRANSACTION_CODE") + " is split by " + configMap.get("SPLIT_QUANTITY") + "quantity");
							flag = true;
						}
					}
					else {
						System.out.println("Transaction " + configMap.get("TRANSACTION_CODE") + "is not split by " + configMap.get("SPLIT_QUANTITY") + " quantity");						
					}
				}
			}
			
			if(flag) {
				logger.log(LogStatus.PASS, "Transaction code " + configMap.get("TRANSACTION_CODE") + " with amount " + configMap.get("PRICE") + " is successfully split by " + configMap.get("SPLIT_QUANTITY") + " quantity");
			}
			
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to split transaction by percentage
	- Input: CONFIRMATION_NUMBER, AMOUNT, TRANSACTION_CODE
	- Output: transaction should be split by amount
	- Author: @author vnadipal
	- Date: 16/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"}, priority = 10)
	public void verifyRoutingAfterPostCharges() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Verify Routing After Post Charges </b>");
			Utils.takeScreenshot(driver, methodName);
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "verifyRoutingAfterPostCharges");
			System.out.println("Config Map: " + configMap);
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String confNumber = ReservationPage.createReservationFromLTB(resvMap);
			configMap.put("CONFIRMATION_NUMBER", confNumber);
			
			CheckinPage.checkInReservation(confNumber);
			waitForSpinnerToDisappear(5);
			//String confNumber = "383158";
			
			CheckinPage.createFolioRouting(configMap);
			waitForSpinnerToDisappear(5);
			
			CheckinPage.postCharges(configMap);
			waitForSpinnerToDisappear(5);
			
			// Clicked on Bookings Menu
			jsClick("Checkin.Cashiering.menuBookings", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Reservations Menu Item
			jsClick("Checkin.Cashiering.menuReservations", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Manage Reservation Menu Item
			jsClick("Checkin.Cashiering.menuItemManageRservation", 100, "presence");
			
			waitForSpinnerToDisappear(10);
			// Entered Confirmation Number
			textBox("Checkin.Cashiering.ManageReservation.txtConfirmationNumber", confNumber);
			
			waitForSpinnerToDisappear(5);
			// Clicked on Search button
			jsClick("Checkin.Cashiering.ManageReservation.btnSearch", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on View Table
			if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
				jsClick("Checkin.Cashiering.RoomRouting.linkViewTable", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Billing link
			jsClick("Checkin.Cashiering.SplitTransactions.linkGoToBilling", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(isExists("Checkin.Cashiering.SplitTransactions.txtCashierId")) {
				//Entered Cashier ID
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierId", "2");
				tabKey("Checkin.Cashiering.SplitTransactions.txtCashierId");	
				waitForSpinnerToDisappear(10);
				
				//Entered Cashier Password
				//WebdriverWait(100, "Checkin.Cashiering.SplitTransactions.txtCashierPassword", "stale");
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", "Welcome!23");
				textBox("Checkin.Cashiering.SplitTransactions.txtCashierPassword", "Welcome!23", 100, "presence");
				waitForSpinnerToDisappear(5);
				
				// Clicked on login button
				jsClick("Checkin.Cashiering.SplitTransactions.btnCashierLogin", 100, "presence");
				waitForSpinnerToDisappear(5);
			}
			
			// Select Window
			selectBy("Checkin.Cashiering.SplitTransactions.dropdownFolio", "text", configMap.get("WINDOW"));
			waitForSpinnerToDisappear(5);
			
			List<WebElement>  rows = Utils.elements("Checkin.Cashiering.SplitTransactions.validationCodeColumn"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			
			if(rows.size() > 0) {
				for(int i=0; i < rows.size(); i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					System.out.println("text: " + rows.get(i).getText());
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
						logger.log(LogStatus.PASS, "Transaction code " + configMap.get("TRANSACTION_CODE") + " is successfully routed to " + configMap.get("WINDOW"));
					}
					else {
						System.out.println("Transaction code " + configMap.get("TRANSACTION_CODE") + " is not routed to " + configMap.get("WINDOW"));						
					}
				}
			}
			
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			Utils.tearDown();
		}
	}

	/*******************************************************************
	-  Description: Verify user is able to transfer a deposit to another reservation
	- Input: Deposit should have a payment 
	- Output: Transfer deposit to another reservation
	- Author: Chandan
	- Date: 22/01/19
	 ********************************************************************/
	
	@Test(priority = 8,groups = {"SANITY"})
	public void transferDeposit() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
	    	
			HashMap<String, String> transferMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "TransferDeposit", "transferDeposit");
			CheckinPage.transferDeposit(transferMap);

	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}		
		Utils.tearDown();
	}
	
	
	/*******************************************************************
	-  Description: Verify user is able to transfer a deposit to another reservation
	- Input: Deposit should have a payment 
	- Output: Transfer deposit to another reservation
	- Author: Chandan
	- Date: 22/01/19
	 ********************************************************************/
	@Test(priority = 9,groups = {"SANITY"})
	public void distributeTransferDeposit() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
	    	
			HashMap<String, String> transferMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "TransferDeposit", "distributeTransferDeposit");
			CheckinPage.transferDeposit(transferMap);

	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}		
		Utils.tearDown();
	}
	
	/*******************************************************************
	-  Description: Verify user is able to post some charges manually having exclusive tax
	- Input: All mandatory details to create reservation and post the charges
	- Output: Post charges with exclusive tax to a reservation 
	- Author: apentam
	- Date: 01/24/2019
	 ********************************************************************/	
	@Test(priority = 10,groups = {"SANITY"})
	public void postChargesWithExclusiveTax() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify that user is able to post some charges manually having exclusive tax </b>");			
			HashMap<String, String> generatesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Generates","Dataset_1");
			HashMap<String, String> postChargesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges","chargesWithExclusiveTax");
			// Navigating to FrontDesk Menu
			click("Checkin.FrontDesk_menu",100,"clickable");
			// Navigating to Arrivals Menu
			click("Checkin.Cashiering.menuItemInHouse",100,"clickable");
			click("Checkin.btn_Search",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			String confirmationNum = Utils.getText("Checkin.txt_FirstConfirmationNum", 100, "presence");
			postChargesMap.put("CONFIRMATION_NUMBER", confirmationNum);
			CheckinPage.postCharges(postChargesMap);
			CheckinPage.calculateGenerates(generatesMap, postChargesMap.get("PRICE"));
			Utils.tearDown();
			
	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to post some charges manually having inclusive tax
	- Input: All mandatory details to create reservation and post the charges
	- Output: Post charges with inclusive tax to a reservation 
	- Author: apentam
	- Date: 01/24/2019
	 ********************************************************************/	
	
	@Test(priority = 11,groups = {"SANITY"})
	public void postChargesWithInclusiveTax() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify that user is able to post some charges manually having exclusive tax </b>");					
			HashMap<String, String> postChargesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges","chargesWithInclusiveTax");
			// Navigating to FrontDesk Menu
			click("Checkin.FrontDesk_menu",100,"clickable");
			// Navigating to InHouse Menu
			click("Checkin.Cashiering.menuItemInHouse",100,"clickable");
			click("Checkin.btn_Search",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			String confirmationNum = Utils.getText("Checkin.txt_FirstConfirmationNum", 100, "presence");
			postChargesMap.put("CONFIRMATION_NUMBER", confirmationNum);
			CheckinPage.postCharges(postChargesMap);			
			Utils.tearDown();
			
	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}
	}


}

