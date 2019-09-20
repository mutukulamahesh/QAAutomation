package com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class ReservationPage extends Utils {

	/*******************************************************************
	 * - Description: Search for a Reservation Using Basic Search - Input: Hasp
	 * map with required details like Source code Market code Reservation Types
	 * etc - Output: Confirmation Num - Author: mahesh - Date: 6/12/2018 -
	 * Revision History:
	 * Change Date 
	 * Change Reason - 
	 * Changed Behavior - Last
	 * Updated By 
	 ********************************************************************/
	public static String createReservationFromLTB(HashMap<String, String> resvMapp) throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);
		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {

			Utils.takeScreenshot(driver, testName);

			// Navigating to Booking menu
			Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			System.out.println("clicked booking");
			logger.log(LogStatus.PASS, "Selected Bookings Menu");

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			System.out.println("clicked reservation");
			logger.log(LogStatus.PASS, "Selected Reservations Menu");

			// Navigating to LTB menu
			Utils.WebdriverWait(100, "Reservation.LookToBook_Menu", "clickable");
			Utils.jsClick("Reservation.LookToBook_Menu");
			System.out.println("clicked LTB");
			logger.log(LogStatus.PASS, "Selected LTB Menu");

			waitForSpinnerToDisappear(50);
			if (isExists("Reservation.New_Reservation_Button")) {
				// clicking on New Reservation Button
				WebdriverWait(100, "Reservation.New_Reservation_Button", "presence");
				mouseHover("Reservation.New_Reservation_Button");
				jsClick("Reservation.New_Reservation_Button");
				System.out.println("Look to Book Sales Screen is Displayed");
				logger.log(LogStatus.PASS, "Selected Create New reservation Button in ltb Page");
			}

			waitForPageLoad(100);
			// Validating Ltb page
			String resname = resvMap.get("ProfileName");
			WebdriverWait(100, "Reservation.ltb_valid", "presence");
			String text = Utils.getText("Reservation.ltb_valid");
			System.out.println("ltb text is " + text);
			logger.log(LogStatus.PASS, "Landed in Look To Book Page");
			// Utils.Wait(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (resvMap.get("Nights") != null) {
				Utils.textBox("Reservation.txt_ReservationEdit_Nights", resvMap.get("Nights"));
				Utils.tabKey("Reservation.txt_ReservationEdit_Nights");
				waitForSpinnerToDisappear(10);
			}

			/*
			 * // passing profile name Utils.WebdriverWait(100,
			 * "Reservation.ltb_name", "presence");
			 * Utils.textBox("Reservation.ltb_name", resname); //
			 * OR.getTestData("manage_reservation_name") System.out.println(
			 * "name entered in ltb.."); logger.log(LogStatus.PASS,
			 * "Provided Profile Name for Reservation");
			 * 
			 * if
			 * (isExists("Reservation.createReservation_frameProfileSelection"))
			 * {
			 * 
			 * driver.switchTo().frame(Utils.element(
			 * "Reservation.createReservation_frameProfileSelection")); //
			 * Thread.sleep(3000); // waitForSpinnerToDisappear(100);
			 * waitForPageLoad(100); // Select btn for profile
			 * Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select",
			 * "clickable");
			 * Utils.jsClick("Reservation.ltb_manage_profile_select");
			 * System.out.println("Selected select btn...");
			 * logger.log(LogStatus.PASS,
			 * "Selected Profile and Select btn in profile search page.."); //
			 * driver.switchTo().defaultContent(); // Thread.sleep(10000);
			 * waitForSpinnerToDisappear(100); waitForPageLoad(100); }
			 * waitForSpinnerToDisappear(100); waitForPageLoad(100); //
			 * Thread.sleep(3000); driver.switchTo().defaultContent();
			 * 
			 * // Thread.sleep(5000); waitForSpinnerToDisappear(100);
			 * waitForPageLoad(100); String titile = driver.getTitle();
			 * System.out.println("current titile :" + titile);
			 */

			// Rate and Room Options tab
			Utils.WebdriverWait(100, "Reservation.tab_RateAndRoom", "clickable");
			jsClick("Reservation.tab_RateAndRoom");
			System.out.println("selected RateAndRoom Tab in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn tab_RateAndRoomin ltb page..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			// waitForSpinnerToDisappear(100);

			// passing Room Type
			Utils.WebdriverWait(100, "Reservation.txt_RoomTypes", "presence");
			System.out.println("Room_Types: " + resvMap.get("Room_Types"));
			Utils.textBox("Reservation.txt_RoomTypes", resvMap.get("Room_Types")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Room Type..");
			logger.log(LogStatus.PASS, "Provided Room TYpe for Reservation - " + resvMap.get("Room_Types"));
			clear("Reservation.txt_RateCodes");
			waitForPageLoad(100);

			// passing Rate Codes
			Utils.WebdriverWait(100, "Reservation.txt_RateCodes", "presence");
			System.out.println("Rate_Code: " + resvMap.get("Rate_Code"));
			// clear("Reservation.txt_RateCodes");
			jsTextbox("Reservation.txt_RateCodes", resvMap.get("Rate_Code")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Rate Code..");
			logger.log(LogStatus.PASS, "Provided Rate Code for Reservation - " + resvMap.get("Rate_Code"));
			waitForPageLoad(100);
			clear("Reservation.txt_RateCategory");
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if ((isExists("Reservation.lov_RateCodeLOV"))) {
				System.out.println("Rate Code Available");
				WebdriverWait(100, "Reservation.lov_RateCodeCheckBox", "clickable");
				jsClick("Reservation.lov_RateCodeCheckBox");
				System.out.println("selected Rate Code CheckBox..");
				logger.log(LogStatus.PASS, "Selected Rate Code CheckBox..");

				WebdriverWait(100, "Reservation.btn_RateCodelovOK", "clickable");
				jsClick("Reservation.btn_RateCodelovOK");
				System.out.println("selected OK button.");
				logger.log(LogStatus.PASS, "Selected OK button.");
				waitForPageLoad(100);
			}

			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);
			// profile search btn
			Utils.WebdriverWait(100, "Reservation.ltb_search_btn", "clickable");
			jsClick("Reservation.ltb_search_btn");
			System.out.println("selected search btn in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn in ltb page for profile..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (!(isExists("Reservation.New_Reservation_Availability_suite"))) {
				System.out.println("No availability Available");
				logger.log(LogStatus.FAIL, "No availability Available..");
				// LoginPage.Logout();
				tearDown();
			}

			// Selecting Rate Code in Availabilty
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");

			// Selecting Continue Booking Butoon if exists
			waitForPageLoad(100);
			// Thread.sleep(3000);
			if (isExists("Reservation.btn_ContinueBooking")) {
				Utils.WebdriverWait(100, "Reservation.btn_ContinueBooking", "clickable");
				Utils.jsClick("Reservation.btn_ContinueBooking");
				System.out.println("Selected Continue Booking btn...");
				logger.log(LogStatus.PASS, "Selected Continue Booking button of Availability in LTB page..");
			}

			// Validating Reservation Page
			waitForPageLoad(100);
			// Thread.sleep(5000);
			String te = driver.findElement(By.xpath("//div[text() = 'Stay Information']")).getText();
			System.out.println("Stay: " + te);
			logger.log(LogStatus.PASS, "Landed and validated reservation create page..");

			// passing profile name
			Utils.WebdriverWait(100, "Reservation.txt_ProfileName", "presence");
			Utils.textBox("Reservation.txt_ProfileName", resname); // OR.getTestData("manage_reservation_name")
			System.out.println("name entered in ltb..");
			logger.log(LogStatus.PASS, "Provided Profile Name for Reservation");
			waitForPageLoad(100);

			mouseHover("Reservation.New_Reservation_Reservation_Type");
			click("Reservation.New_Reservation_Reservation_Type");

			if (isExists("Reservation.createReservation_frameProfileSelection")) {

				driver.switchTo().frame(Utils.element("Reservation.createReservation_frameProfileSelection"));
				// Thread.sleep(3000);
				// waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
				// Select btn for profile
				Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
				Utils.jsClick("Reservation.ltb_manage_profile_select");
				System.out.println("Selected select btn...");
				logger.log(LogStatus.PASS, "Selected Profile and Select btn in profile search page.."); //
				driver.switchTo().defaultContent();
				// Thread.sleep(10000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// Thread.sleep(3000);
			driver.switchTo().defaultContent();

			// Thread.sleep(5000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			String titile = driver.getTitle();
			System.out.println("current titile :" + titile);
			waitForPageLoad(100);

			// Provide Resv type
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Reservation_Type", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Reservation_Type");
			Utils.clear("Reservation.New_Reservation_Reservation_Type");
			Utils.textBox("Reservation.New_Reservation_Reservation_Type", resvMap.get("ReservationType"));
			System.out.println("Resv Type Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Reservation Type " + resvMap.get("ReservationType"));

			waitForPageLoad(100);

			// Provide Resv Market code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			//
			waitForPageLoad(100);
			// Provide Time
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Source code
			Utils.Wait(3000);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Source", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Source");
			Utils.jsTextbox("Reservation.New_Reservation_Source", resvMap.get("SourceCode"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			System.out.println("Resv Source Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Source Code " + resvMap.get("SourceCode"));

			// Provide // Time
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Market Code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			// Selecting Reservation Method -CASH
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.New_Reservation_Method")).perform();
			Utils.selectBy("Reservation.New_Reservation_Method", "text", resvMap.get("PaymentMethod"));
			System.out.println("CASH-Reservation Method is Selected");
			logger.log(LogStatus.PASS, "Payment method is selected " + resvMap.get("PaymentMethod"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Book nOw Button
			Utils.WebdriverWait(100, "Reservation.New_Reservation_BookNowButton", "clickable");
			Utils.jsClick("Reservation.New_Reservation_BookNowButton");
			logger.log(LogStatus.PASS, "Selected Book Now Button ");
			// Thread.sleep(10000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Confirm Button
			if (isExists("Reservation.New_Reservation_Confirm_button")) {
				Utils.WebdriverWait(100, "Reservation.New_Reservation_Confirm_button", "clickable");
				Utils.click(Utils.element("Reservation.New_Reservation_Confirm_button"));
				logger.log(LogStatus.PASS, "Selected Confirm Button ");
				// Thread.sleep(5000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.create_res_confirmationNum", "presence");
			String ConfirmationNum = Utils.getText("Reservation.create_res_confirmationNum");
			System.out.println("Confirmation Num : " + ConfirmationNum);
			logger.log(LogStatus.PASS, "Landed in Resv Presentation Page: ");
			logger.log(LogStatus.PASS, "Confirmation Num : " + ConfirmationNum);
			// ExcelUtils.setCellData("Reservation", "Confirmation_Num",
			// iTestCaseRow, ConfirmationNum);
			/*
			 * ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"),
			 * "Reservation", "createReservation", "ConfirmationNum",
			 * ConfirmationNum);
			 */

			if (Utils.isExists("Reservation.create_res_confirmationNum")) {
				System.out.println("Reservation Created Successfully with Confirmation Num: " + ConfirmationNum);
				logger.log(LogStatus.PASS,
						"Reservation Created Successfully.with Confirmation Num: " + ConfirmationNum);
			} else {
				System.out.println("Reservation Not Created Successfully ");
				logger.log(LogStatus.FAIL, "Reservation Not Created Successfully");
			}

			/*
			 * if (!writeDataPropFile("ConfirmationNum", ConfirmationNum)) {
			 * logger.log(LogStatus.FAIL,
			 * "Failed to write value into the properties file");
			 * System.out.println(
			 * "Failed to write value into the properties file"); }
			 */

			logger.log(LogStatus.PASS, "Reservation created and Validated");
			return ConfirmationNum;
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
	 * - Description: Search for a Reservation Using Advance Search - Input:
	 * Confirmation Num - Output: Confirmation Num - Author: mahesh - Date:
	 * 6/12/2018 - Revision History: - Change Date - Change Reason - Changed
	 * Behavior - Last Changed By
	 ********************************************************************/
	public static void reservationAdvanceSearch(String ConfNum) throws Exception {

		String Scriptname = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println(Scriptname);

		String ConfirmationNum = ConfNum;
		System.out.println(ConfirmationNum);

		try {

			Utils.takeScreenshot(driver, Scriptname);

			// Navigating to Booking menu
			Utils.WebdriverWait(1000, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			// System.out.println("clicked booking");
			// logger.log(LogStatus.PASS, "Selected Bookings Menu");
			// Reporter.log("Selected Bookings Menu");
			Utils.log("Selected Bookings Menu", LogStatus.PASS);

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			// System.out.println("clicked reservation");
			// logger.log(LogStatus.PASS, "Selected Reservations Menu");
			Utils.log("Selected Reservations Menu", LogStatus.PASS);

			// Navigating to Manage Reservations menu
			Utils.WebdriverWait(100, "Reservation.manage_reservation", "clickable");
			Utils.click(Utils.element("Reservation.manage_reservation"));
			// System.out.println("clicked manage res");
			// logger.log(LogStatus.PASS, "Selected Manage Reservations Menu");
			Utils.log("Selected Manage Reservations Menu", LogStatus.PASS);

			// Validating Manage Reservation Page
			Utils.WebdriverWait(100, "Reservation.manage_reservation_validationtxt", "presence");
			Utils.verifyCurrentPage("Reservation.manage_reservation_validationtxt", "manage_reservation_validationtxt");
			// logger.log(LogStatus.PASS, "Landed in Manage Reservations page
			// and Validated");
			Utils.log("Landed in Manage Reservations page and Validated", LogStatus.PASS);

			//If landed in Super Search - coming back to Adv Search
			if(isExists("Reservation.lnk_Search_GoToAdv")){
				Utils.WebdriverWait(100, "Reservation.lnk_Search_GoToAdv", "clickable");
				Utils.click(Utils.element("Reservation.lnk_Search_GoToAdv"));
				Utils.log("Selected GO to Advance Link", LogStatus.PASS);
			}
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			if (ConfirmationNum != null && !ConfirmationNum.isEmpty()) {
				System.out.println("Conf is empty");

				Utils.WebdriverWait(100, "Reservation.search_confirmNum", "presence");
				Utils.textBox("Reservation.search_confirmNum", ConfirmationNum); // OR.getTestData("manage_reservation_confirmnum")
				// System.out.println("Entered Conf Num in manage reservation");
				// logger.log(LogStatus.PASS, "Provided Conf Num in Manage
				// reservations page :" + ConfirmationNum);
				Utils.log("Provided Conf Num in Manage reservations page :" + ConfirmationNum, LogStatus.PASS);

			} else {
				// Search Type
				Utils.WebdriverWait(100, "Reservation.Search_SearchTypeLov", "presence");
				Utils.selectBy("Reservation.Search_SearchTypeLov", "text", "Arrivals");
				Utils.log("Selected Manage Reservation as Search Type", LogStatus.PASS);
				Thread.sleep(3000);
			}

			// Selecting Search button on Manage Reservation page
			Utils.WebdriverWait(100, "Reservation.managereservation_searchBtn", "clickable");
			Utils.click("Reservation.managereservation_searchBtn");
			System.out.println("Clicked Search button in manage Reservation");
			// logger.log(LogStatus.PASS, "Selected Search Button in Manage
			// Reservation page");
			Utils.log("Selected Search Button in Manage Reservation page", LogStatus.PASS);
			Thread.sleep(3000);

			Thread.sleep(3000);
			Utils.WebdriverWait(100, "Reservation.Search_Reservation_IwantTo", "clickable");
			Utils.mouseHover("Reservation.Search_Reservation_IwantTo");
			Utils.jsClick("Reservation.Search_Reservation_IwantTo");
			// System.out.println("Selected I Want to in manage Reservation..");
			// logger.log(LogStatus.PASS, "Selected I Want to Link in Search
			// Profile Page");
			Utils.log("Selected I Want to Link in Search Profile Page", LogStatus.PASS);

			// Selecting go to res link in I want to link
			// Utils.WebdriverWait(100,
			// "Reservation.Search_Iwantto_gotoReservationv", "clickable");
			Utils.jsClick("Reservation.Search_Iwantto_gotoReservation");
			// System.out.println("Selected Go to Reservation..");
			// logger.log(LogStatus.PASS, "Selected Go to Reservation Link in
			// Res Page");
			Utils.log("Selected Go to Reservation Link in Res Page", LogStatus.PASS);
			Thread.sleep(5000);
			Thread.sleep(5000);

			// Validating Reservation Page
			Thread.sleep(3000);
			Utils.WebdriverWait(100, "Reservation.reservationPage_validation", "presence");
			Utils.verifyCurrentPage("Reservation.reservationPage_validation", "m_reservationPage_validation");
			// logger.log(LogStatus.PASS, "Landed in Reservations page and
			// Validated");
			Utils.log("Landed in Reservations page and Validated", LogStatus.PASS);

			// Validating Confirmation Number
			Utils.WebdriverWait(30, "Reservation.reservationpage_confno", "presence");
			String Confirmation = Utils.getText("Reservation.reservationpage_confno");
			System.out.println("Confirmation :" + Confirmation);
			Utils.log("Confirmation :" + Confirmation, LogStatus.PASS);

			/*
			 * if (ConfirmationNum.equalsIgnoreCase(Confirmation)) { //
			 * logger.log(LogStatus.PASS, // "Validated Search Results in Search
			 * reservation page with // Confirmation Num " + Confirmation);
			 * Utils.log(
			 * "Validated Search Results in Search reservation page with Confirmation Num "
			 * + Confirmation, LogStatus.PASS); } else {
			 * logger.log(LogStatus.FAIL,
			 * "Validated Search Results in Search reservation page is failed "
			 * + Confirmation); Utils.log(
			 * "Validated Search Results in Search reservation page is failed "
			 * + Confirmation, LogStatus.FAIL); }
			 */

			// logger.log(LogStatus.PASS, "Reservation- Advance Search and
			// Validated");
			Utils.log("Reservation- Advance  Search  and Validated", LogStatus.PASS);

			Thread.sleep(3000);

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}

			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}

	/*******************************************************************
	 * - Description: Search for a Reservation Using Basic Search - Input:
	 * Confirmation Num - Output: Confirmation Num - Author: mahesh - Date:
	 * 6/12/2018 - Revision History: - Change Date - Change Reason - Changed
	 * Behavior - Last Changed By
	 ********************************************************************/
	public static void reservationSuperSearch(String ConfNum) throws Exception {

		// String testClassName = Utils.getClassName();
		String Scriptname = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println(Scriptname);

		String ConfirmationNum = ConfNum;
		System.out.println(ConfirmationNum);

		try {
			Utils.takeScreenshot(driver, Scriptname);
			// logger = report.startTest(Scriptname, "Verify able Search a
			// reservation using search");

			// Navigating to Booking menu
			Utils.WebdriverWait(1000, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			// System.out.println("clicked booking");
			// logger.log(LogStatus.PASS, "Selected Bookings Menu");
			// Reporter.log("Selected Bookings Menu");
			Utils.log("Selected Bookings Menu", LogStatus.PASS);

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			// System.out.println("clicked reservation");
			// logger.log(LogStatus.PASS, "Selected Reservations Menu");
			Utils.log("Selected Reservations Menu", LogStatus.PASS);

			// Navigating to Manage Reservations menu
			Utils.WebdriverWait(100, "Reservation.manage_reservation", "clickable");
			Utils.click(Utils.element("Reservation.manage_reservation"));
			// System.out.println("clicked manage res");
			// logger.log(LogStatus.PASS, "Selected Manage Reservations Menu");
			Utils.log("Selected Manage Reservations Menu", LogStatus.PASS);

			// Validating Manage Reservation Page
			Utils.WebdriverWait(100, "Reservation.manage_reservation_validationtxt", "presence");
			Utils.verifyCurrentPage("Reservation.manage_reservation_validationtxt", "manage_reservation_validationtxt");
			// logger.log(LogStatus.PASS, "Landed in Manage Reservations page
			// and Validated");
			Utils.log("Landed in Manage Reservations page and Validated", LogStatus.PASS);

			// Select Back to basic Link
			if(isExists("Reservation.managereservation_basicSearchlink")){
				Utils.WebdriverWait(100, "Reservation.managereservation_basicSearchlink", "clickable");
				Utils.jsClick("Reservation.managereservation_basicSearchlink");
				System.out.println("clicked Back to Basic search link");
				logger.log(LogStatus.PASS, "Selected Back to Basic link in Manage Reservation page");
				waitForPageLoad(100);
				//Thread.sleep(10000);
			}

			waitForPageLoad(100);
			// Validating Manage Reservation Page
			Utils.WebdriverWait(100, "Reservation.manage_reservation_validationtxt", "presence");
			Utils.verifyCurrentPage("Reservation.manage_reservation_validationtxt", "manage_reservation_validationtxt");
			// logger.log(LogStatus.PASS, "Landed in Manage Reservations page
			// and Validated");
			Utils.log("Landed in Manage Reservations page and Validated", LogStatus.PASS);

			// String connum = resvMap.get("ConfirmationNum");

			if (ConfirmationNum != null && !ConfirmationNum.isEmpty()) {
				System.out.println("Conf is empty");

				Utils.WebdriverWait(100, "Reservation.Search_basicSearch_txt", "presence");
				Utils.textBox("Reservation.Search_basicSearch_txt", ConfirmationNum);
				System.out.println("Entered Conf Num in  manage reservation");
				logger.log(LogStatus.PASS, "Provided Conf Num in Manage reservations page :" + ConfirmationNum);

			} else {
				// Search Type
				Utils.WebdriverWait(100, "Reservation.Search_SearchTypeLov", "presence");
				Utils.selectBy("Reservation.Search_SearchTypeLov", "text", "Arrivals");
				Utils.log("Selected Manage Reservation as Search Type", LogStatus.PASS);
				Thread.sleep(3000);
			}

			// Selecting Search button on Manage Reservation page
			WebdriverWait(100, "Reservation.Search_basicSearch_searchBtn", "clickable");
			mouseHover("Reservation.Search_basicSearch_searchBtn");
			jsClick("Reservation.Search_basicSearch_searchBtn");
			System.out.println("Clicked Search button in  manage profile");
			logger.log(LogStatus.PASS, "Selected Search Button in Manage Reservation page");

			waitForPageLoad(100);
			Wait(5000);

			/*
			 * if(isExists("Reservation.Search_basicSearch_Error")) {
			 * System.out.println("Error occured"); Utils.log(
			 * "Error - An unexpected error has occurred, please contact your administrator."
			 * , LogStatus.FAIL); tearDown(); } Wait(3000);
			 * if(!isExists("Reservation.Search_Reservation_IwantTo")) {
			 * System.out.println("No Data Displayed"); Utils.log(
			 * "No Records available", LogStatus.FAIL); tearDown(); }
			 */

			Thread.sleep(3000);
			Utils.WebdriverWait(100, "Reservation.Search_Reservation_IwantTo", "clickable");
			Utils.mouseHover("Reservation.Search_Reservation_IwantTo");
			Utils.jsClick("Reservation.Search_Reservation_IwantTo");
			// System.out.println("Selected I Want to in manage Reservation..");
			// logger.log(LogStatus.PASS, "Selected I Want to Link in Search
			// Profile Page");
			Utils.log("Selected I Want to Link in Search Profile Page", LogStatus.PASS);

			// Selecting go to res link in I want to link
			// Utils.WebdriverWait(100,
			// "Reservation.Search_Iwantto_gotoReservationv", "clickable");
			Utils.jsClick("Reservation.Search_Iwantto_gotoReservation");
			// System.out.println("Selected Go to Reservation..");
			// logger.log(LogStatus.PASS, "Selected Go to Reservation Link in
			// Res Page");
			Utils.log("Selected Go to Reservation Link in Res Page", LogStatus.PASS);
			Thread.sleep(5000);
			Thread.sleep(5000);

			// Validating Reservation Page
			Thread.sleep(3000);
			Utils.WebdriverWait(100, "Reservation.reservationPage_validation", "presence");
			Utils.verifyCurrentPage("Reservation.reservationPage_validation", "m_reservationPage_validation");
			// logger.log(LogStatus.PASS, "Landed in Reservations page and
			// Validated");
			Utils.log("Landed in Reservations page and Validated", LogStatus.PASS);

			// Validating Confirmation Number
			Utils.WebdriverWait(30, "Reservation.reservationpage_confno", "presence");
			String Confirmation = Utils.getText("Reservation.reservationpage_confno");
			System.out.println("Confirmation :" + Confirmation);
			Utils.log("Confirmation :" + Confirmation, LogStatus.PASS);

			/*
			 * if (ConfirmationNum.equalsIgnoreCase(Confirmation)) { //
			 * logger.log(LogStatus.PASS, // "Validated Search Results in Search
			 * reservation page with // Confirmation Num " + Confirmation);
			 * Utils.log(
			 * "Validated Search Results in Search reservation page with Confirmation Num "
			 * + Confirmation, LogStatus.PASS); } else {
			 * logger.log(LogStatus.FAIL,
			 * "Validated Search Results in Search reservation page is failed "
			 * + Confirmation); Utils.log(
			 * "Validated Search Results in Search reservation page is failed "
			 * + Confirmation, LogStatus.FAIL); }
			 */

			logger.log(LogStatus.PASS, "Reservation- Super  Search  and Validated");

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}

	}

	public static void reservationSearch(String ConfNum) throws Exception {

		String ConfirmationNum = ConfNum;
		System.out.println(ConfirmationNum);
		try {

			// Navigating to Booking menu
			Utils.WebdriverWait(1000, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			// System.out.println("clicked booking");
			// logger.log(LogStatus.PASS, "Selected Bookings Menu");
			// Reporter.log("Selected Bookings Menu");
			Utils.log("Selected Bookings Menu", LogStatus.PASS);

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			// System.out.println("clicked reservation");
			// logger.log(LogStatus.PASS, "Selected Reservations Menu");
			Utils.log("Selected Reservations Menu", LogStatus.PASS);

			// Navigating to Manage Reservations menu
			Utils.WebdriverWait(100, "Reservation.manage_reservation", "clickable");
			Utils.click(Utils.element("Reservation.manage_reservation"));
			// System.out.println("clicked manage res");
			// logger.log(LogStatus.PASS, "Selected Manage Reservations Menu");
			Utils.log("Selected Manage Reservations Menu", LogStatus.PASS);

			// Validating Manage Reservation Page
			Utils.WebdriverWait(100, "Reservation.manage_reservation_validationtxt", "presence");
			Utils.verifyCurrentPage("Reservation.manage_reservation_validationtxt", "manage_reservation_validationtxt");
			// logger.log(LogStatus.PASS, "Landed in Manage Reservations page
			// and Validated");
			Utils.log("Landed in Manage Reservations page and Validated", LogStatus.PASS);

			// Passing name to Name fileld in Manage Reservation page
			// String connum = resvMap.get("ConfirmationNum");

			if (ConfirmationNum != null && !ConfirmationNum.isEmpty()) {
				System.out.println("Conf is empty");

				Utils.WebdriverWait(100, "Reservation.search_confirmNum", "presence");
				Utils.textBox("Reservation.search_confirmNum", ConfirmationNum); // OR.getTestData("manage_reservation_confirmnum")
				// System.out.println("Entered Conf Num in manage reservation");
				// logger.log(LogStatus.PASS, "Provided Conf Num in Manage
				// reservations page :" + ConfirmationNum);
				Utils.log("Provided Conf Num in Manage reservations page :" + ConfirmationNum, LogStatus.PASS);

			} else {
				// Search Type
				Utils.WebdriverWait(100, "Reservation.Search_SearchTypeLov", "presence");
				Utils.selectBy("Reservation.Search_SearchTypeLov", "text", "Arrivals");
				Utils.log("Selected Manage Reservation as Search Type", LogStatus.PASS);
				Thread.sleep(3000);
			}

			// Selecting Search button on Manage Reservation page
			Utils.WebdriverWait(100, "Reservation.managereservation_searchBtn", "clickable");
			Utils.click("Reservation.managereservation_searchBtn");
			System.out.println("Clicked Search button in manage Reservation");
			// logger.log(LogStatus.PASS, "Selected Search Button in Manage
			// Reservation page");
			Utils.log("Selected Search Button in Manage Reservation page", LogStatus.PASS);
			Thread.sleep(3000);
			Thread.sleep(3000);

		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Reservation Advance Search not Done :: Failed " + e.getMessage());
			throw (e);
		}
	}

	public static void reservationUpdate(String ConfNum) throws Exception {

		// String testClassName = Utils.getClassName();
		String Scriptname = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println(Scriptname);

		Map<String, String> resvUpdateMap = new HashMap<String, String>();
		resvUpdateMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation",
				"reservationUpdate");
		System.out.println("reservationUpdate: " + resvUpdateMap);
		String nights = resvUpdateMap.get("Nights");

		System.out.println(OR.getTransData("ConfirmationNum"));
		String ConfirmationNum = ConfNum;
		System.out.println(ConfirmationNum);
		try {
			// logger = report.startTest(Scriptname, "Verify able to Update a
			// Reservation").assignCategory("", "Cloud.Reservation");
			Utils.takeScreenshot(driver, Scriptname);

			ReservationPage.reservationAdvanceSearch(ConfirmationNum);

			// Selecting Edit
			Utils.WebdriverWait(30, "Reservation.btn_ReservationEdit_Edit", "clickable");
			Utils.jsClick("Reservation.btn_ReservationEdit_Edit");
			System.out.println("Selected Edit ");
			logger.log(LogStatus.PASS, "Selected Edit in Reservation Presentation page : ");
			// Thread.sleep(2000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			// Passing value to reference number text box
			// String nights = resvUpdateMap.get("Nights");
			System.out.println("Nights: " + nights);
			Utils.WebdriverWait(30, "Reservation.txt_ReservationEdit_Nights", "presence");
			Utils.mouseHover("Reservation.txt_ReservationEdit_Nights");
			Utils.clear("Reservation.txt_ReservationEdit_Nights");
			Utils.textBox("Reservation.txt_ReservationEdit_Nights", nights);
			System.out.println("Provided value to reference number text box");
			logger.log(LogStatus.PASS, "Provided the Nights in Search Reservation page : " + nights);

			waitForPageLoad(100);
			// Selecting Save button
			Utils.WebdriverWait(100, "Reservation.btn_ReservationEdit_Save", "clickable");
			Utils.click("Reservation.btn_ReservationEdit_Save");
			System.out.println("Clicked on save button");
			logger.log(LogStatus.PASS, "Selected save Button in edit reservation page  ");
			// Thread.sleep(5000);
			// Thread.sleep(5000);
			// Thread.sleep(5000);

			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			// Selecting Stay Details Link
			if(isExists("Reservation.lnk_ReservationEdit_StayDetails")){
				Utils.WebdriverWait(100, "Reservation.lnk_ReservationEdit_StayDetails", "clickable");
				Utils.click("Reservation.lnk_ReservationEdit_StayDetails");
				System.out.println("Clicked on Stay Details Link");
				logger.log(LogStatus.PASS, "Selected Stay Details Link in reservation page  ");
			}

			// Validating Updated Data in Additional Details
			Utils.WebdriverWait(100, "Reservation.txt_ReservationEdit_NigthsValue", "presence");
			mouseHover("Reservation.txt_ReservationEdit_NigthsValue");
			// scroll("down");
			//scroll("down");
			//mouseHover("Reservation.txt_ReservationEdit_DetailsTab");
			waitForSpinnerToDisappear(100);
			mouseHover("Reservation.txt_ReservationEdit_NigthsValue");
			System.out.println("Testtttt:  " + driver.findElement(By.xpath("//*[@data-ocid='LBL_NIGHTS']")).getText());
			String nightsValue = getText("Reservation.txt_ReservationEdit_NigthsValue");
			System.out.println("Nights after Edit : " + nightsValue);
			System.out.println("Nights : " + nights);
			waitForPageLoad(100);

			/*if (isExists("Reservation.btn_ReservationEdit_Save")) {
				// Selecting Save button
				Utils.WebdriverWait(100, "Reservation.btn_ReservationEdit_Save", "clickable");
				Utils.click("Reservation.btn_ReservationEdit_Save");
				System.out.println("Clicked on save button");
				logger.log(LogStatus.PASS, "Selected save Button in edit reservation page  ");

			}*/
			if (nights.equalsIgnoreCase(nightsValue)) // OR.getTestData("reservation_edit_additionalDetails_refnum")
			{
				System.out.println("Reservation Updated Successfully ");
				logger.log(LogStatus.PASS, "Reservation Updated Successfully... ");
			} else {
				System.out.println("Reservation Not Updated Successfully ");
				logger.log(LogStatus.FAIL, "Reservation Not Updated Successfully");
			}

			Thread.sleep(10000);
			logger.log(LogStatus.PASS, "Update Reservation Details :: Passed");

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Update Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}

	public static void ReservationMenu() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);

		/*
		 * List<String> resvMenuList = XpathEngine.link(); String Booking =
		 * XpathEngine.createElement(resvMenuList, "Bookings","booking_menu",
		 * "Resv"); System.out.println("booking_menu "+Booking);
		 */

		try {
			// logger = report.startTest("", "").assignCategory("",
			// "Cloud.Reservation");
			Utils.takeScreenshot(driver, testClassName);

			// Navigating to Booking menu
			Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			System.out.println("clicked booking");
			logger.log(LogStatus.PASS, "Selected Bookings Menu");

			// Thread.sleep(3000);
			/*
			 * List<String> resvMenutData = XpathEngine.tData(); String Resv =
			 * XpathEngine.createElement(resvMenutData,
			 * "Reservations","reservation_menu", "Resv"); System.out.println(
			 * "reservation_menu "+Resv);
			 */

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			System.out.println("clicked reservation");
			logger.log(LogStatus.PASS, "Selected Reservations Menu");

			// Thread.sleep(5000);
			// String ManageResv = XpathEngine.createElement(resvMenutData,
			// "Manage","manage_reservation", "Resv");
			// System.out.println("manage_reservation "+ManageResv);

			// Navigating to Manage Reservations menu
			Utils.WebdriverWait(100, "Reservation.manage_reservation", "clickable");
			Utils.click(Utils.element("Reservation.manage_reservation"));
			System.out.println("clicked manage res");
			logger.log(LogStatus.PASS, "Selected Manage Reservations Menu");

			// Validating Manage Reservation Page
			Utils.WebdriverWait(100, "Reservation.manage_reservation_validationtxt", "presence");
			Utils.verifyCurrentPage("Reservation.manage_reservation_validationtxt", "manage_reservation_validationtxt");
			logger.log(LogStatus.PASS, "Landed in Manage Reservations page and Validated");

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/***************************************************************************************************************
	 * - Description: Creating reservation with a restricted rate for a property
	 * - Input: Hasp map with required details like Source code Market code
	 * Reservation Types ,restricted rate code etc - Output: - Author: swati -
	 * Date: 6/12/2018 - Revision History: - Change Date - Change Reason -
	 * Changed Behavior - Last Changed By
	 *****************************************************************************************************************/

	public static void createReservationWithRestrictedRate(HashMap<String, String> resvMapp) throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);

		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {
			Utils.takeScreenshot(driver, testName);

			// Navigating to Booking menu
			Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			System.out.println("clicked booking");
			logger.log(LogStatus.PASS, "Selected Bookings Menu");

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			System.out.println("clicked reservation");
			logger.log(LogStatus.PASS, "Selected Reservations Menu");

			// Navigating to LTB menu
			Utils.WebdriverWait(100, "Reservation.LookToBook_Menu", "clickable");
			Utils.jsClick("Reservation.LookToBook_Menu");
			System.out.println("clicked LTB");
			logger.log(LogStatus.PASS, "Selected LTB Menu");
			waitForSpinnerToDisappear(20);

			if (isExists("Reservation.New_Reservation_Button")) {
				// clicking on New Reservation Button
				WebdriverWait(100, "Reservation.New_Reservation_Button", "presence");
				mouseHover("Reservation.New_Reservation_Button");
				jsClick("Reservation.New_Reservation_Button");
				System.out.println("Look to Book Sales Screen is Displayed");
				logger.log(LogStatus.PASS, "Selected Create New reservation Button in ltb Page");
			}

			// Room and Rate options tab

			click("Reservation.RoomRateOptionsTab");
			waitForSpinnerToDisappear(50);

			textBox("Reservation.RoomRateOptionsTabRateCode", resvMap.get("RATE_CODE"));
			Utils.tabKey("Reservation.RoomRateOptionsTabRateCode");

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
					Utils.element("Reservation.manage_reservation_searchBtn"));
			Utils.WebdriverWait(100, "Reservation.manage_reservation_searchBtn", "clickable");
			Utils.mouseHover("Reservation.manage_reservation_searchBtn");
			Utils.jsClick("Reservation.manage_reservation_searchBtn");

			// mouseHover("Reservation.manage_reservation_searchBtn");
			// click("Reservation.manage_reservation_searchBtn");
			waitForSpinnerToDisappear(50);

			List ele = driver.findElements(
					By.xpath("//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']"));

			System.out.println("//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']");
			System.out.println(ele.size());

			if (ele.size() > 0) {

				Actions builder = new Actions(driver);
				builder.moveToElement(driver.findElement(By.xpath(
						"//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']")))
				.build().perform();

				driver.findElement(By
						.xpath("//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']"))
				.click();
				driver.findElement(By
						.xpath("//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']"))
				.click();

				waitForSpinnerToDisappear(100);

				String begindate = Utils.getText("Reservation.RateCodeRestrictionBeginDate");
				String text = Utils.getText("Reservation.RateCodeScreenRestrictionText");

				System.out.println("begindate" + begindate);

				if (begindate.equals(resvMap.get("BEGIN_DATE"))) {
					System.out.println("Restriction start date is correct" + begindate);
					logger.log(LogStatus.PASS, "Restriction start date is correct" + "Date from reservation screen -> "
							+ begindate + "Date to be verified->  " + resvMap.get("BEGIN_DATE"));
				} else {
					logger.log(LogStatus.PASS,
							"Restriction start date is not correct" + "Date from reservation screen -> " + begindate
							+ "Date to be verified->  " + resvMap.get("BEGIN_DATE"));
				}

				System.out.println("text" + text);
				if (text.equals("The Rate/Room Type is Closed for this date")) {
					logger.log(LogStatus.PASS, "Rate restriction is validated successfully->  " + text);
				} else {
					logger.log(LogStatus.FAIL, "Rate restriction is not validated successfully-> " + text);
				}
			} else {
				System.out.println("Restricted rate code is not visible in the availablity screen");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/***************************************************************************************************************
	 * - Description: Validating search functionality on property availability
	 * screen - Input: Hasp map with required details like Source code Market
	 * code Reservation Types ,restricted rate code etc - Output: - Author:
	 * swati - Date: 6/12/2018 - Revision History: - Change Date - Change Reason
	 * - Changed Behavior - Last Changed By
	 ****************************************************************************************************************/
	public static void ValidatePropertyAvailabilityScreen(HashMap<String, String> resvMapp, String SearchOption)
			throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);

		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {
			Utils.takeScreenshot(driver, testName);

			click("Housekeeping.inventory_menu");
			click("Reservation.PropertyAvail");

			waitForSpinnerToDisappear(20);

			if (SearchOption.equals("RoomType")) {
				Utils.selectBy("Reservation.PropertyAvail.RoomTypeDropdown", "text", "Room Type");
				Utils.tabKey("Reservation.PropertyAvail.RoomTypeDropdown");

				textBox("Reservation.PropertyAvail.RoomTypeText", resvMap.get("ROOM_TYPE"));
				Utils.tabKey("Reservation.PropertyAvail.RoomTypeText");

				jsClick("Reservation.manage_reservation_searchBtn");

				waitForSpinnerToDisappear(50);

				if (Utils.isExists("Reservation.PropertyAvail.TableValidation")) {

					String roomType = driver
							.findElement(By.xpath("//*[@data-ocid='LINK_" + resvMap.get("ROOM_TYPE") + "']")).getText();

					System.out.println("roomtype" + roomType);

					if (!roomType.equals(resvMap.get("ROOM_TYPE"))) {

						logger.log(LogStatus.FAIL, "Room Type search is not working as expected ");
					} else {
						logger.log(LogStatus.PASS, "Room Type search is working as expected ");
					}

				} else {
					logger.log(LogStatus.FAIL, "There are no rows in the table");
				}

			}

			else if (SearchOption.equals("RoomClass")) {

				Utils.selectBy("Reservation.PropertyAvail.RoomTypeDropdown", "text", "Room Class");
				Utils.tabKey("Reservation.PropertyAvail.RoomTypeDropdown");

				textBox("Reservation.PropertyAvail.RoomClassText", resvMap.get("ROOM_TYPE"));
				Utils.tabKey("Reservation.PropertyAvail.RoomClassText");

				jsClick("Reservation.manage_reservation_searchBtn");

				waitForSpinnerToDisappear(50);

				if (Utils.isExists("Reservation.PropertyAvail.TableValidation")) {

					String roomType = driver
							.findElement(By.xpath("//*[@data-ocid='LINK_" + resvMap.get("ROOM_CLASS") + "']"))
							.getText();

					if (!roomType.equals(resvMap.get("ROOM_CLASS"))) {

						logger.log(LogStatus.FAIL, "Room Class search is not working as expected ");
					} else {
						logger.log(LogStatus.PASS, "Room Class search is working as expected ");
					}

				} else {
					logger.log(LogStatus.FAIL, "There are no rows in the table");
				}

			}

			else {
				logger.log(LogStatus.FAIL, "Invalid search option provided");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
	 * - Description: Cancels a Reservation from LTB Screen - Input: reservation
	 * Confirmation num - Output: Confirmation Num - Author: mahesh - Date:
	 * 24/12/2018 - Revision History: - Change Date - Change Reason - Changed
	 * Behavior - Last Changed By
	 ********************************************************************/
	public static void cancelReservation(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);

			// Select IWanTo
			Thread.sleep(3000);
			Utils.WebdriverWait(100, "Reservation.Search_Reservation_IwantTo", "presence");
			Utils.mouseHover("Reservation.Search_Reservation_IwantTo");
			Utils.jsClick("Reservation.Search_Reservation_IwantTo");
			System.out.println("Selected I Want to in manage profile..");
			logger.log(LogStatus.PASS, "Selected I Want to Link in Search Profile Page");

			// Clicking on Cancel Reservation link
			Utils.WebdriverWait(1000, "Reservation.link_Reservation_Cancel", "clickable");
			Utils.jsClick("Reservation.link_Reservation_Cancel");
			logger.log(LogStatus.PASS, "Clicking on Cancel Reservation link");
			System.out.println("Clicking on Cancel Reservation link");
			Thread.sleep(3000);
			Thread.sleep(3000);

			// Sending Keys to Cancel Reason dropdown
			String CancelReason = resvMapp.get("Cancel_Reason");

			System.out.println("CancelReason: " + CancelReason);
			Utils.WebdriverWait(100, "Reservation.txt_ReservationCancel_Reason", "presence");
			Utils.jsTextbox("Reservation.txt_ReservationCancel_Reason", CancelReason);
			System.out.println("Sending Keys to Cancel Reason dropdown:  " + CancelReason);
			logger.log(LogStatus.PASS, "Sending Reason to Cancel Reason dropdown:  " + CancelReason);
			Thread.sleep(2000);

			// waitForPageLoad(100);
			// Sending Keys to Cancel Desc
			String CancelReasonDesc = resvMapp.get("Cancel_ReasonDesc");

			System.out.println("CancelReasonDesc: " + CancelReasonDesc);
			Utils.WebdriverWait(100, "Reservation.txt_ReservationCancel_ReasonDesc", "presence");
			Utils.jsTextbox("Reservation.txt_ReservationCancel_ReasonDesc", CancelReasonDesc);
			System.out.println("Sending Keys to Cancel Reason Desc: " + CancelReasonDesc);
			logger.log(LogStatus.PASS, "Sending Desc to Cancel Reason Desc: " + CancelReasonDesc);

			// waitForPageLoad(100);
			Thread.sleep(2000);
			// Clicking on Cancel Now button
			Utils.WebdriverWait(100, "Reservation.btn_ReservationCancel_CancelResv", "clickable");
			mouseHover("Reservation.btn_ReservationCancel_CancelResv");
			Utils.click("Reservation.btn_ReservationCancel_CancelResv");
			System.out.println("Clicking on Cancel Now button");
			logger.log(LogStatus.PASS, "Clicking on Cancel Now button");
			Thread.sleep(3000);

			waitForPageLoad(100);

			if (isExists("Reservation.header_ReservationCancel_popup")) {

				if (getText("Reservation.txt_ReservationCancel_Status")
						.contains("Reservation has been cancelled successfully"))

				{
					logger.log(LogStatus.PASS, "Cancelled the reservation successfully and Validated");
					System.out.println("Cancelled the reservation successfully and Validated");
				} else {
					logger.log(LogStatus.FAIL, "Reservation not Cancelled ");
				}
				System.out.println("Reservation Cancelled");
			}
			/*
			 * else { logger.log(LogStatus.FAIL,"Reservation not Cancelled ");
			 * System.out.println("Reservation not Cancelled"); }
			 */

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}


	/*******************************************************************
	-  Description: Add Payment Methods for a Reservation Using
	- Input: Payment method, Window Number, Confirmation Number
	- Output: 
	- Author: Chandan
	- Date: 8/1/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ******************************************************************************/
	public static void reservationPaymentMethod(String PayMethod, String WinNumber, String ConfNum) throws Exception {

		String Scriptname = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println(Scriptname);		
		String[] PaymentMethod = PayMethod.split("\\|");
		String[] WindowNumber = WinNumber.split("\\|");
		String ConfirmationNum = ConfNum;
		System.out.println(ConfirmationNum);

		try {
			reservationSearch(ConfirmationNum);
			Utils.waitForSpinnerToDisappear(10);
			Utils.WebdriverWait(100, "Reservation.Search_Reservation_IwantTo", "clickable");
			Utils.mouseHover("Reservation.Search_Reservation_IwantTo");
			Utils.jsClick("Reservation.Search_Reservation_IwantTo");
			// System.out.println("Selected I Want to in manage Reservation..");
			// logger.log(LogStatus.PASS, "Selected I Want to Link in Search
			// Profile Page");
			Utils.log("Selected I Want to Link", LogStatus.PASS);
			Utils.waitForSpinnerToDisappear(10);
			// Selecting go to res link in I want to link
			// Utils.WebdriverWait(100,
			// "Reservation.Search_Iwantto_gotoReservationv", "clickable");
			Utils.jsClick("Reservation.manage_reservation_PaymentsInstruction");
			// System.out.println("Selected Go to Reservation..");
			// logger.log(LogStatus.PASS, "Selected Go to Reservation Link in
			// Res Page");
			Utils.log("Selected Go to PaymentsInstructions Link in", LogStatus.PASS);
			Utils.waitForSpinnerToDisappear(10);

			for(int i=0; i< PaymentMethod.length; i++){


				// Validating NewEditPaymentInstructions Page
				// Providing option to Add multiple payment methods
				Utils.waitForSpinnerToDisappear(20);
				if (Utils.isExists("Reservation.PaymentInstructions_popUp", 100, "presence")){
					Utils.log("Opened PaymentInstructions Page ", LogStatus.PASS);
					Utils.click("Reservation.PaymentInstructions_NewEditPaymentInstructionsLink");
					// Clicking on New/EditPaymentInstructionsLink
					Utils.WebdriverWait(100, "Reservation.PaymentInstructions_WindowSelect"+WindowNumber[i], "presence");
					Utils.log("Reservation.PaymentInstructions_WindowSelect"+WindowNumber[i],LogStatus.PASS);
					Utils.jsClick("Reservation.PaymentInstructions_WindowSelect"+WindowNumber[i]);
					// Clicking on Window as per the selection provided in Data sheet

					Utils.log("Payment Window :" + WindowNumber +" is selected", LogStatus.PASS);
					Utils.waitForSpinnerToDisappear(10);
					new Actions(driver).moveToElement(Utils.element("Reservation.PaymentInstructions_MethodLov")).perform();
					Utils.selectBy("Reservation.PaymentInstructions_MethodLov", "value",PaymentMethod[i]);
					//Selecting the payment method from LOV
					System.out.println(PaymentMethod[i] + "Reservation Method is Selected");
					logger.log(LogStatus.PASS, "Payment method is selected " + PaymentMethod[i]);
					Utils.waitForSpinnerToDisappear(10);
					Utils.click("Reservation.PaymentInstructions_saveBtn");
					//Clicked on save button
					Utils.waitForSpinnerToDisappear(10);
					System.out.println("Save Button is Clicked");
					logger.log(LogStatus.PASS, "Save Button is Clicked");

					Utils.WebdriverWait(100, "Reservation.PaymentInstructions_NewEditPaymentInstructionsLink", "presence");
					//validation of saved payments
					String  savedRecord = driver.findElement(By.xpath("//span[text()='"+ WindowNumber[i]+"']/parent::td/following-sibling::td[1]//label")).getText();
					if(savedRecord.equals(PaymentMethod[i])){
						System.out.println(PaymentMethod[i] + "has been created successfully for window" + WindowNumber[i]);
						logger.log(LogStatus.PASS, PaymentMethod[i] + "has been created successfully for window" + WindowNumber[i]);
					}else{
						System.out.println("Failed to Create" + PaymentMethod[i] + "for window" + WindowNumber[i]);
						logger.log(LogStatus.FAIL, "Failed to Create" + PaymentMethod[i] + "for window" + WindowNumber[i]);
					}
				}else{
					System.out.println("Failed to Load the Payment Instructions Page");
					logger.log(LogStatus.FAIL, "Failed to Load the Payment Instructions Popup");
				}


			}



		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}

			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}

	public static String createReservation(HashMap<String, String> resvMapp) throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);
		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {
			Utils.takeScreenshot(driver, testName);

			/*if (isExists("Reservation.New_Reservation_Button")) {
				// clicking on New Reservation Button
				WebdriverWait(100, "Reservation.New_Reservation_Button", "presence");
				mouseHover("Reservation.New_Reservation_Button");
				jsClick("Reservation.New_Reservation_Button");
				System.out.println("Look to Book Sales Screen is Displayed");
				logger.log(LogStatus.PASS, "Selected Create New reservation Button in ltb Page");
			}*/

			waitForPageLoad(100);
			// Validating Ltb page
			String resname = resvMap.get("ProfileName");
			WebdriverWait(100, "Reservation.ltb_valid", "presence");
			String text = Utils.getText("Reservation.ltb_valid");
			System.out.println("ltb text is " + text);
			logger.log(LogStatus.PASS, "Landed in Look To Book Page");
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (resvMap.get("Nights") != null) {
				Utils.textBox("Reservation.txt_ReservationEdit_Nights", resvMap.get("Nights"));
				Utils.tabKey("Reservation.txt_ReservationEdit_Nights");
				waitForSpinnerToDisappear(10);
			}

			// Rate and Room Options tab
			Utils.WebdriverWait(100, "Reservation.tab_RateAndRoom", "clickable");
			jsClick("Reservation.tab_RateAndRoom");
			System.out.println("selected RateAndRoom Tab in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn tab_RateAndRoomin ltb page..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			// waitForSpinnerToDisappear(100);

			// passing Room Type
			Utils.WebdriverWait(100, "Reservation.txt_RoomTypes", "presence");
			System.out.println("Room_Types: " + resvMap.get("Room_Types"));
			Utils.textBox("Reservation.txt_RoomTypes", resvMap.get("Room_Types")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Room Type..");
			logger.log(LogStatus.PASS, "Provided Room TYpe for Reservation - " + resvMap.get("Room_Types"));
			clear("Reservation.txt_RateCodes");
			waitForPageLoad(100);

			// passing Rate Codes
			Utils.WebdriverWait(100, "Reservation.txt_RateCodes", "presence");
			System.out.println("Rate_Code: " + resvMap.get("Rate_Code"));
			// clear("Reservation.txt_RateCodes");
			jsTextbox("Reservation.txt_RateCodes", resvMap.get("Rate_Code")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Rate Code..");
			logger.log(LogStatus.PASS, "Provided Rate Code for Reservation - " + resvMap.get("Rate_Code"));
			waitForPageLoad(100);
			clear("Reservation.txt_RateCategory");
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if ((isExists("Reservation.lov_RateCodeLOV"))) {
				System.out.println("Rate Code Available");
				WebdriverWait(100, "Reservation.lov_RateCodeCheckBox", "clickable");
				jsClick("Reservation.lov_RateCodeCheckBox");
				System.out.println("selected Rate Code CheckBox..");
				logger.log(LogStatus.PASS, "Selected Rate Code CheckBox..");

				WebdriverWait(100, "Reservation.btn_RateCodelovOK", "clickable");
				jsClick("Reservation.btn_RateCodelovOK");
				System.out.println("selected OK button.");
				logger.log(LogStatus.PASS, "Selected OK button.");
				waitForPageLoad(100);
			}

			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);
			// profile search btn
			Utils.WebdriverWait(100, "Reservation.ltb_search_btn", "clickable");
			jsClick("Reservation.ltb_search_btn");
			System.out.println("selected search btn in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn in ltb page for profile..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (!(isExists("Reservation.New_Reservation_Availability_suite"))) {
				System.out.println("No availability Available");
				logger.log(LogStatus.FAIL, "No availability Available..");
				// LoginPage.Logout();
				tearDown();
			}

			// Selecting Rate Code in Availabilty
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");

			// Selecting Continue Booking Butoon if exists
			waitForPageLoad(100);
			// Thread.sleep(3000);
			if (isExists("Reservation.btn_ContinueBooking")) {
				Utils.WebdriverWait(100, "Reservation.btn_ContinueBooking", "clickable");
				Utils.jsClick("Reservation.btn_ContinueBooking");
				System.out.println("Selected Continue Booking btn...");
				logger.log(LogStatus.PASS, "Selected Continue Booking button of Availability in LTB page..");
			}

			// Validating Reservation Page
			waitForPageLoad(100);
			// Thread.sleep(5000);
			String te = driver.findElement(By.xpath("//div[text() = 'Stay Information']")).getText();
			System.out.println("Stay: " + te);
			logger.log(LogStatus.PASS, "Landed and validated reservation create page..");

			// passing profile name
			Utils.WebdriverWait(100, "Reservation.txt_ProfileName", "presence");
			Utils.textBox("Reservation.txt_ProfileName", resname); // OR.getTestData("manage_reservation_name")
			System.out.println("name entered in ltb..");
			logger.log(LogStatus.PASS, "Provided Profile Name for Reservation");
			waitForPageLoad(100);

			mouseHover("Reservation.New_Reservation_Reservation_Type");
			click("Reservation.New_Reservation_Reservation_Type");

			if (isExists("Reservation.createReservation_frameProfileSelection")) {

				driver.switchTo().frame(Utils.element("Reservation.createReservation_frameProfileSelection"));
				// Thread.sleep(3000);
				// waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
				// Select btn for profile
				Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
				Utils.jsClick("Reservation.ltb_manage_profile_select");
				System.out.println("Selected select btn...");
				logger.log(LogStatus.PASS, "Selected Profile and Select btn in profile search page.."); //
				driver.switchTo().defaultContent();
				// Thread.sleep(10000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// Thread.sleep(3000);
			driver.switchTo().defaultContent();

			// Thread.sleep(5000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			String titile = driver.getTitle();
			System.out.println("current titile :" + titile);
			waitForPageLoad(100);

			// Provide Resv type
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Reservation_Type", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Reservation_Type");
			Utils.clear("Reservation.New_Reservation_Reservation_Type");
			Utils.textBox("Reservation.New_Reservation_Reservation_Type", resvMap.get("ReservationType"));
			System.out.println("Resv Type Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Reservation Type " + resvMap.get("ReservationType"));

			waitForPageLoad(100);

			// Provide Resv Market code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			//
			waitForPageLoad(100);
			// Provide Time
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Source code
			Utils.Wait(3000);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Source", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Source");
			Utils.jsTextbox("Reservation.New_Reservation_Source", resvMap.get("SourceCode"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			System.out.println("Resv Source Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Source Code " + resvMap.get("SourceCode"));

			// Provide // Time
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Market Code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			// Selecting Reservation Method -CASH
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.New_Reservation_Method")).perform();
			Utils.selectBy("Reservation.New_Reservation_Method", "text", resvMap.get("PaymentMethod"));
			System.out.println("CASH-Reservation Method is Selected");
			logger.log(LogStatus.PASS, "Payment method is selected " + resvMap.get("PaymentMethod"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Book nOw Button
			Utils.WebdriverWait(100, "Reservation.New_Reservation_BookNowButton", "clickable");
			Utils.jsClick("Reservation.New_Reservation_BookNowButton");
			logger.log(LogStatus.PASS, "Selected Book Now Button ");
			// Thread.sleep(10000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Confirm Button
			if (isExists("Reservation.New_Reservation_Confirm_button")) {
				Utils.WebdriverWait(100, "Reservation.New_Reservation_Confirm_button", "clickable");
				Utils.click(Utils.element("Reservation.New_Reservation_Confirm_button"));
				logger.log(LogStatus.PASS, "Selected Confirm Button ");
				// Thread.sleep(5000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.create_res_confirmationNum", "presence");
			String ConfirmationNum = Utils.getText("Reservation.create_res_confirmationNum");
			System.out.println("Confirmation Num : " + ConfirmationNum);
			logger.log(LogStatus.PASS, "Landed in Resv Presentation Page: ");
			logger.log(LogStatus.PASS, "Confirmation Num : " + ConfirmationNum);
			// ExcelUtils.setCellData("Reservation", "Confirmation_Num",
			// iTestCaseRow, ConfirmationNum);
			/*
			 * ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"),
			 * "Reservation", "createReservation", "ConfirmationNum",
			 * ConfirmationNum);
			 */

			if (Utils.isExists("Reservation.create_res_confirmationNum")) {
				System.out.println("Reservation Created Successfully with Confirmation Num: " + ConfirmationNum);
				logger.log(LogStatus.PASS,
						"Reservation Created Successfully.with Confirmation Num: " + ConfirmationNum);
			} else {
				System.out.println("Reservation Not Created Successfully ");
				logger.log(LogStatus.FAIL, "Reservation Not Created Successfully");
			}

			/*
			 * if (!writeDataPropFile("ConfirmationNum", ConfirmationNum)) {
			 * logger.log(LogStatus.FAIL,
			 * "Failed to write value into the properties file");
			 * System.out.println(
			 * "Failed to write value into the properties file"); }
			 */

			logger.log(LogStatus.PASS, "Reservation created and Validated");
			return ConfirmationNum;
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
    - Description: This method Split the reservation
	- Input:
	- Output:
	- Dilip
	- Date: 1/26/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void splitReservation(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {

			if (Integer.parseInt(getText("Reservation.labelNoOfRooms"))<2) {

				// Navigating to Booking menu
				click("Reservation.linkEditReservation", 100, "clickable");

				// Click on add rooms + icon
				jsClick("Reservation.linkAddRoomPlus", 100, "clickable");

				// Click on save button				
				waitForSpinnerToDisappear(100);
				jsClick("Reservation.PaymentInstructions_saveBtn", 100, "clickable");


			}else{
				logger.log(LogStatus.PASS,"Enough rooms are avilable for split :: Excepted ::greather than 1:: Actual ::"+(Integer.parseInt(getText("Reservation.labelNoOfRooms")))+"::");
			}

			//In LP  rooms updating in same page			
			waitForSpinnerToDisappear(100);
//			if(isExists("PaymentInstructions_saveBtn")){
//				System.out.println("in");
//				jsClick("Reservation.linkBackToReservation", 100, "clickable");
//			}

			//	Click on I Want to link
			jsClick("Reservation.manage_reservation_IWantto", 100, "clickable");

			//	Click on Split reservation
			jsClick("Reservation.linkSplitReservation", 100, "clickable");

			//	Btn on Split reservation
			jsClick("Reservation.btnSplitReservation", 100, "clickable");

			//	Validation of no of room after split
			waitForSpinnerToDisappear(50);
			if (Integer.parseInt(getText("Reservation.labelNoOfRooms"))==1) {

				logger.log(LogStatus.PASS,"Rooms Excepted 1 :::Actual >> "+(Integer.parseInt(getText("Reservation.labelNoOfRooms")))+" <<");

			}else{

				logger.log(LogStatus.FAIL,"Rooms Excepted 1 :::Actual >> "+(Integer.parseInt(getText("Reservation.labelNoOfRooms")))+" <<");
			}

			//	click on linkShowAll reservation
			jsClick("Reservation.linkShowAll", 100, "clickable");
			waitForSpinnerToDisappear(50);

			//	click on linklined reservation
			jsClick("Reservation.linkLinkedReservation", 100, "clickable");
			waitForSpinnerToDisappear(50);
			System.out.println(getText("Reservation.labelName"));
			System.out.println(resvMapp.get("ProfileName"));

			if(getText("Reservation.labelName").contains(resvMapp.get("ProfileName"))){
			
				logger.log(LogStatus.PASS, "Linked Reservation is success");
			} else {

				logger.log(LogStatus.FAIL,"Linked Reservation is unsuccess");
			}

			//	click on close link
			jsClick("Reservation.linkGridClose", 100, "clickable");

			//	linked text coming in bold after page refresh		
			if(!isExists("Reservation.linkLinkedReservation1")){

				jsClick("Reservation.linkRefreshReservationPage", 100, "clickable");

			}

			// Validation of linked reservation font in bold			
			String fontWeight = Utils.element("Reservation.linkLinkedReservation1").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			if (fontWeight.equals("700") || fontWeight.equals("bold")) {

				logger.log(LogStatus.PASS,"linked reservation  are coming in bold");

			} else {

				logger.log(LogStatus.FAIL,"linked reservation  are not coming in bold");
			}




		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Split Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Split Reservation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
    - Description: This method link the reservation
	- Input:ProfileName,Source code Market code Reservation Types etc
	- Output:
	- Dilip
	- Date: 1/26/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void linkReservation(HashMap<String, String> resvMap) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {


			//	Click on I Want to link
			jsClick("Reservation.manage_reservation_IWantto", 100, "clickable");

			//	Click on linkLinkToAReservation
			jsClick("Reservation.linkLinkReservation", 100, "clickable");

			waitForPageLoad(100);
			// Validating Ltb page
			String resname = resvMap.get("ProfileName");
			WebdriverWait(100, "Reservation.ltb_valid", "presence");
			String text = Utils.getText("Reservation.ltb_valid");
			System.out.println("ltb text is " + text);
			logger.log(LogStatus.PASS, "Landed in Look To Book Page");
			// Utils.Wait(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (resvMap.get("Nights") != null) {
				Utils.textBox("Reservation.txt_ReservationEdit_Nights", resvMap.get("Nights"));
				Utils.tabKey("Reservation.txt_ReservationEdit_Nights");
				waitForSpinnerToDisappear(10);
			}

			// Rate and Room Options tab
			Utils.WebdriverWait(100, "Reservation.tab_RateAndRoom", "clickable");
			jsClick("Reservation.tab_RateAndRoom");
			System.out.println("selected RateAndRoom Tab in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn tab_RateAndRoomin ltb page..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			// waitForSpinnerToDisappear(100);

			// passing Room Type
			Utils.WebdriverWait(100, "Reservation.txt_RoomTypes", "presence");
			System.out.println("Room_Types: " + resvMap.get("Room_Types"));
			Utils.textBox("Reservation.txt_RoomTypes", resvMap.get("Room_Types")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Room Type..");
			logger.log(LogStatus.PASS, "Provided Room TYpe for Reservation - " + resvMap.get("Room_Types"));
			clear("Reservation.txt_RateCodes");
			waitForPageLoad(100);

			// passing Rate Codes
			Utils.WebdriverWait(100, "Reservation.txt_RateCodes", "presence");
			System.out.println("Rate_Code: " + resvMap.get("Rate_Code"));
			// clear("Reservation.txt_RateCodes");
			jsTextbox("Reservation.txt_RateCodes", resvMap.get("Rate_Code")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Rate Code..");
			logger.log(LogStatus.PASS, "Provided Rate Code for Reservation - " + resvMap.get("Rate_Code"));
			waitForPageLoad(100);
			clear("Reservation.txt_RateCategory");
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if ((isExists("Reservation.lov_RateCodeLOV"))) {
				System.out.println("Rate Code Available");
				WebdriverWait(100, "Reservation.lov_RateCodeCheckBox", "clickable");
				jsClick("Reservation.lov_RateCodeCheckBox");
				System.out.println("selected Rate Code CheckBox..");
				logger.log(LogStatus.PASS, "Selected Rate Code CheckBox..");

				WebdriverWait(100, "Reservation.btn_RateCodelovOK", "clickable");
				jsClick("Reservation.btn_RateCodelovOK");
				System.out.println("selected OK button.");
				logger.log(LogStatus.PASS, "Selected OK button.");
				waitForPageLoad(100);
			}

			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);
			// profile search btn
			Utils.WebdriverWait(100, "Reservation.ltb_search_btn", "clickable");
			jsClick("Reservation.ltb_search_btn");
			System.out.println("selected search btn in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn in ltb page for profile..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (!(isExists("Reservation.New_Reservation_Availability_suite"))) {
				System.out.println("No availability Available");
				logger.log(LogStatus.FAIL, "No availability Available..");
				// LoginPage.Logout();
				tearDown();
			}

			// Selecting Rate Code in Availabilty
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");

			// Selecting Continue Booking Butoon if exists
			waitForPageLoad(100);
			// Thread.sleep(3000);
			if (isExists("Reservation.btn_ContinueBooking")) {
				Utils.WebdriverWait(100, "Reservation.btn_ContinueBooking", "clickable");
				Utils.jsClick("Reservation.btn_ContinueBooking");
				System.out.println("Selected Continue Booking btn...");
				logger.log(LogStatus.PASS, "Selected Continue Booking button of Availability in LTB page..");
			}

			// Validating Reservation Page
			waitForPageLoad(100);
			// Thread.sleep(5000);
			String te = driver.findElement(By.xpath("//div[text() = 'Stay Information']")).getText();
			System.out.println("Stay: " + te);
			logger.log(LogStatus.PASS, "Landed and validated reservation create page..");

			// passing profile name
			Utils.WebdriverWait(100, "Reservation.txt_ProfileName", "presence");
			Utils.textBox("Reservation.txt_ProfileName", resname); // OR.getTestData("manage_reservation_name")
			System.out.println("name entered in ltb..");
			logger.log(LogStatus.PASS, "Provided Profile Name for Reservation");
			waitForPageLoad(100);

			mouseHover("Reservation.New_Reservation_Reservation_Type");
			click("Reservation.New_Reservation_Reservation_Type");

			if (isExists("Reservation.createReservation_frameProfileSelection")) {

				driver.switchTo().frame(Utils.element("Reservation.createReservation_frameProfileSelection"));
				// Thread.sleep(3000);
				// waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
				// Select btn for profile
				Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
				Utils.jsClick("Reservation.ltb_manage_profile_select");
				System.out.println("Selected select btn...");
				logger.log(LogStatus.PASS, "Selected Profile and Select btn in profile search page.."); //
				driver.switchTo().defaultContent();
				// Thread.sleep(10000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// Thread.sleep(3000);
			driver.switchTo().defaultContent();

			// Thread.sleep(5000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			String titile = driver.getTitle();
			System.out.println("current titile :" + titile);
			waitForPageLoad(100);

			// Provide Resv type
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Reservation_Type", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Reservation_Type");
			Utils.clear("Reservation.New_Reservation_Reservation_Type");
			Utils.textBox("Reservation.New_Reservation_Reservation_Type", resvMap.get("ReservationType"));
			System.out.println("Resv Type Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Reservation Type " + resvMap.get("ReservationType"));

			waitForPageLoad(100);

			// Provide Resv Market code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			//
			waitForPageLoad(100);
			// Provide Time
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Source code
			Utils.Wait(3000);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Source", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Source");
			Utils.jsTextbox("Reservation.New_Reservation_Source", resvMap.get("SourceCode"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			System.out.println("Resv Source Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Source Code " + resvMap.get("SourceCode"));

			// Provide // Time
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Market Code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			// Selecting Reservation Method -CASH
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.New_Reservation_Method")).perform();
			Utils.selectBy("Reservation.New_Reservation_Method", "text", resvMap.get("PaymentMethod"));
			System.out.println("CASH-Reservation Method is Selected");
			logger.log(LogStatus.PASS, "Payment method is selected " + resvMap.get("PaymentMethod"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Click on add to trip composer
			jsClick("Reservation.btnAddToTripComposer", 100, "clickable");

			waitForSpinnerToDisappear(50);
			if(isExists("Reservation.PaymentInstructions_saveBtn")){

				jsClick("Reservation.PaymentInstructions_saveBtn", 100, "clickable");
			}


			// Selecting Book nOw Button
			//			waitForSpinnerToDisappear(100);
			Utils.WebdriverWait(100, "Reservation.New_Reservation_BookNowButton", "clickable");
			Utils.jsClick("Reservation.New_Reservation_BookNowButton");
			logger.log(LogStatus.PASS, "Selected Book Now Button ");
			// Thread.sleep(10000);

			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			if(isExists("Reservation.New_Reservation_Confirm_button")){

				jsClick("Reservation.New_Reservation_Confirm_button", 100, "clickable");
			}

			waitForSpinnerToDisappear(100);
			System.out.println(getText("Reservation.labelName"));
			System.out.println(resvMap.get("ProfileName"));

			if(getText("Reservation.labelName").contains(resvMap.get("ProfileName"))){

				logger.log(LogStatus.PASS, "Linked Reservation is success");
			} else {

				logger.log(LogStatus.FAIL,"Linked Reservation is unsuccess");
			}


			// Click on Reservation Overview
			jsClick("Reservation.linkReservationOverview", 100, "clickable");


			// Click on Show All
			jsClick("Reservation.linkShowAll", 100, "clickable");			


			// Validation of linked reservation font in bold			
			String fontWeight = Utils.element("Reservation.linkLinkedReservation1").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			if (fontWeight.equals("700") || fontWeight.equals("bold")) {

				logger.log(LogStatus.PASS,"linked reservation  are coming in bold");

			} else {

				logger.log(LogStatus.FAIL,"linked reservation  are not coming in bold");
			}


			// Click on linked reservation			
			waitForSpinnerToDisappear(50);
			jsClick("Reservation.linkLinkedReservation1", 100, "clickable");

			waitForSpinnerToDisappear(100);
			System.out.println(getText("Reservation.labelName"));
			System.out.println(resvMap.get("ProfileName"));
			if(getText("Reservation.labelName").contains(resvMap.get("ProfileName"))){

				logger.log(LogStatus.PASS,"Linked Reservation is success validation from panel links");
			} else {

				logger.log(LogStatus.FAIL,"Linked Reservation is unsuccess validation from panel links");
			}

			// Click Close link linked
			jsClick("Reservation.linkLinkedReservationClose", 100, "clickable");




		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Link reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Link reservation  :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
    - Description: This method unlink the reservation
	- Input:ProfileName
	- Output:
	- Dilip
	- Date: 1/26/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void unlinkReservation(HashMap<String, String> resvMap) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {


			// Click on linked reservation
			jsClick("Reservation.linkLinkedReservation1", 100, "clickable");

			// Click on actions
			jsClick("Reservation.linkActionsReservation", 100, "clickable");

			// Click on unlink
			jsClick("Reservation.linkUnlinkReservation", 100, "clickable");

			// Click on Show All
			jsClick("Reservation.linkShowAll", 100, "clickable");

			// Click on linkLined Reservation
			jsClick("Reservation.linkLinkedReservation", 100, "clickable");

			waitForSpinnerToDisappear(100);
			if (getText("Reservation.labelNoResultsFound").equals("No results found.")) {

				logger.log(LogStatus.PASS,"Unlink is succes");

			}else{

				logger.log(LogStatus.FAIL,"Unlink is unsucces");
			}


			// Click Close link linked
			waitForSpinnerToDisappear(50);
			jsClick("Reservation.linkLinkedReservationClose", 100, "clickable");


		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "UnLink reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Unlink reservation  :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}



	public static void copyReservation(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);


			Thread.sleep(3000);
			// Clicking on confirmation number  link
			Utils.WebdriverWait(200, "Reservation.CopyReservation_ConfirmationNumber", "clickable");
			Utils.jsClick("Reservation.CopyReservation_ConfirmationNumber");
			logger.log(LogStatus.PASS, "Clicking on ConfirmationNumber  link");
			System.out.println("Clicked on ConfirmationNumber  link");
			Utils.waitForSpinnerToDisappear(100);

			String confnumber1=getText("Reservation.Search_CopyReservation_Confirmationnumber");
			logger.log(LogStatus.PASS, "Copying the confirmation number from" +confnumber1);
			System.out.println("Copying the confirmation number from : " +confnumber1);
			Utils.waitForSpinnerToDisappear(100);
			
			// Select I_want_To
			Utils.WebdriverWait(100, "Reservation.CopyReservation_IwantTo", "clickable");
			Utils.jsClick("Reservation.CopyReservation_IwantTo");
			logger.log(LogStatus.PASS, "Selected I Want to Link in  manage Page");
			System.out.println("Selected I Want to in manage profile..");		
			Utils.waitForSpinnerToDisappear(100);

			// Select Copy reservation
			Utils.WebdriverWait(100, "Reservation.link_CopyReservation", "clickable");
			Utils.jsClick("Reservation.link_CopyReservation");
			logger.log(LogStatus.PASS, "Selected copy reservation Link");
			System.out.println("Selected copy reservation Link");		
			Utils.waitForSpinnerToDisappear(100);
			
			// Confirm the Copy reservation page
			if (isExists("Reservation.header_CopyReservation_popup")) 
			{

				if (getText("Reservation.header_CopyReservation_popup")
						.contains("Select Reservation Information to be copied"))

				{
					logger.log(LogStatus.PASS, "Select Reservation Information to be copied page is displayed");
					System.out.println("Select Reservation Information to be copied page is displayed");

					// Select Copy reservation button
					Utils.WebdriverWait(100, "Reservation.btn_CopyReservation", "clickable");
					Utils.jsClick("Reservation.btn_CopyReservation");
					logger.log(LogStatus.PASS, "Clicked on the copy reservation button");
					System.out.println("Clicked on the copy reservation button");	

					// Confirm the House Posting Account page under Copy reservation
					if (isExists("Reservation.header_CopyReservation_Housepopup")) 
					{

						//Select Payment method 
						Utils.WebdriverWait(100, "Reservation.Search_CopyReservation_method", "presence");
						Utils.selectBy("Reservation.Search_CopyReservation_method", "text", "Cash");
						Utils.log("Selected payment method as cash", LogStatus.PASS);
						System.out.println("Selected payment method as cash");	

						//Save
						Utils.jsClick("Reservation.btn_CopyReservation_Save");
						logger.log(LogStatus.PASS, "Copy Reservation payment method is saved");
						System.out.println("Copy Reservation payment method is saved");


						String confnumber2=getText("Reservation.Search_CopyReservation_Confirmationnumber");
						if (confnumber2!= confnumber1)
						{
							logger.log(LogStatus.PASS, "New  confirmation number " +confnumber2);
							System.out.println("New  confirmation number :" +confnumber2);
						}


					} 

					else 
					{
						Thread.sleep(3000);
						// clicking on search button
						Utils.WebdriverWait(100, "Reservation.btn_CopyReservation_Search", "clickable");
						Utils.jsClick("Reservation.btn_CopyReservation_Search");
						logger.log(LogStatus.PASS, "Clicking on search button");
						System.out.println("Clicked on Search button");


						//  clicking on room button
						Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
						scroll("down");
						mouseHover("Reservation.New_Reservation_Availability_suite");
						mouseHover("Reservation.New_Reservation_Availability_suite");
						Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
						// Utils.Wait(3000);
						waitForSpinnerToDisappear(100);
						waitForPageLoad(100);
						Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
						Utils.jsClick("Reservation.New_Reservation_Availability_suite");
						Utils.jsClick("Reservation.New_Reservation_Availability_suite");
						Utils.waitForSpinnerToDisappear(100);
						logger.log(LogStatus.PASS, "Selected Availability in LTB page..");
						System.out.println("Selected Availability in LTB page..");
						

						// bOOK NOW
						Utils.WebdriverWait(100, "Reservation.select_CopyReservation_booknow", "clickable");
						Utils.jsClick("Reservation.select_CopyReservation_booknow");


						//if error exists while book now
						if (isExists("Reservation.select_CopyReservation_booknow_error")) 
						{

							if (getText("Reservation.select_CopyReservation_booknow_error")
									.contains("An unexpected error has occurred, please contact your administrator."))
							{
								logger.log(LogStatus.FAIL, "An unexpected error has occurred, please contact your administrator.");
								System.out.println("An unexpected error has occurred, please contact your administrator."); 

								logger.log(LogStatus.FAIL, "book now was not completed");
								System.out.println("book now was not completed"); 
							}

						}
						else
						{

							logger.log(LogStatus.PASS, "book now was completed");
							System.out.println("book now was completed"); 

							//compare of two confirmation numbers
							String confnumber2=getText("Reservation.Search_CopyReservation_Confirmationnumber");
							if (confnumber2!= confnumber1)
							{
								logger.log(LogStatus.PASS, "New  confirmation number " +confnumber2);
								System.out.println("New  confirmation number " +confnumber2);
								logger.log(LogStatus.PASS, "Selected Reservation " +confnumber1 +"Information is copied to:" +confnumber2);
								System.out.println("Selected Reservation " +confnumber1 +" Information is copied to:" +confnumber2);
							}
						}

					}


				}
				else 
				{
					logger.log(LogStatus.FAIL, "Select Reservation Information to be copied page is not displayed ");
					System.out.println("Select Reservation Information to be copied page is not displayed");
				}

			}



		} catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}
	//	


	//////////////////////////////////////// extend stay dates


	public static void stayDates(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);


			// Clicking on view table
			Utils.WebdriverWait(100, "Reservation.staydates_viewTable", "clickable");
			Utils.jsClick("Reservation.staydates_viewTable");
			logger.log(LogStatus.PASS, "Clicking on View table");
			System.out.println("Clicked on view table");
			Utils.waitForSpinnerToDisappear(100);
			
			// Clicking on confirmation number  link
			Utils.WebdriverWait(200, "Reservation.CopyReservation_ConfirmationNumber", "clickable");
			Utils.jsClick("Reservation.CopyReservation_ConfirmationNumber");
			logger.log(LogStatus.PASS, "Clicking on ConfirmationNumber  link");
			System.out.println("Clicked on ConfirmationNumber  link");
			Utils.waitForSpinnerToDisappear(100);
			// getting the nights text

			String nights= driver.findElement(By.xpath("//*[@data-ocid='OTEXT_OCC_STYDT_NGHTS_TXT']")).getAttribute("innerText");
			logger.log(LogStatus.PASS, "Nights value "+nights);
			System.out.println("Nights value "+nights);	
			Thread.sleep(3000);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.staydates_showall", "clickable");
			Utils.jsClick("Reservation.staydates_showall");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Thread.sleep(3000);
			// Select stay details
			Utils.WebdriverWait(200, "Reservation.staydates_staydetails", "clickable");
			Utils.jsClick("Reservation.staydates_staydetails");
			logger.log(LogStatus.PASS, "Selected stay details  Link");
			System.out.println("Selected stay details  Link");		
			Thread.sleep(3000);	
			// Select change stay details
			Utils.WebdriverWait(200, "Reservation.staydates_chanegstaydetails", "clickable");
			Utils.jsClick("Reservation.staydates_chanegstaydetails");
			logger.log(LogStatus.PASS, "Selected change stay details  Link");
			System.out.println("Selected change stay details  Link");	
			Thread.sleep(3000);	

			// clicking on nights

			Utils.WebdriverWait(200, "Reservation.staydates_nights", "clickable");
			Utils.jsClick("Reservation.staydates_nights");
			logger.log(LogStatus.PASS, "Clicking on + button of nights");
			System.out.println("Clicking on + button of nights");

			Thread.sleep(3000);
			// clicking on search button
			Utils.WebdriverWait(100, "Reservation.btn_CopyReservation_Search", "clickable");
			Utils.jsClick("Reservation.btn_CopyReservation_Search");
			logger.log(LogStatus.PASS, "Clicking on search button");
			System.out.println("Clicked on Search button");

			//  clicking on room button
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");
			System.out.println("Selected Availability in LTB page..");
			// confirmation of stay details page

			if (isExists("Reservation.staydates_resvpopup")) 
			{


				logger.log(LogStatus.PASS, "Reservation page is displayed");
				System.out.println("Reservation page is displayed");

				// clicking on change reservation now  button
				Utils.WebdriverWait(100, "Reservation.staydates_changereservation", "clickable");
				Utils.jsClick("Reservation.staydates_changereservation");
				logger.log(LogStatus.PASS, "Clicking on change reservation button");
				System.out.println("Clicked on change reservation button");

				if (isExists("Reservation.New_Reservation_Confirm_button")) 
				{
					Utils.WebdriverWait(100, "Reservation.New_Reservation_Confirm_button", "clickable");
					Utils.jsClick("Reservation.New_Reservation_Confirm_button");
					logger.log(LogStatus.PASS, "Clicking on confirm button");
					System.out.println("Clicked on confirm button");
				}

			}

			String extendnights= driver.findElement(By.xpath("//*[@data-ocid='OTEXT_OCC_STYDT_NGHTS_TXT']")).getAttribute("innerText");
			if (extendnights!= nights)
			{
				logger.log(LogStatus.PASS, "No.of nights:"+extendnights);
				System.out.println("No.of nights:"+extendnights);	
				logger.log(LogStatus.PASS, "stay details information is extended ");
				System.out.println("stay details information is extended");
			}

			else
			{
				logger.log(LogStatus.FAIL, "stay details information is not extended ");
				System.out.println("stay details information is not extended");
			}

		} 
		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}
	//////////////////////////////////////////////////////////////////// 

	public static void reinstateReservation(String ConfirmationNum) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {
			Utils.takeScreenshot(driver, methodName);

			String conf = ConfirmationNum;//resvMapp.get("ConfirmationNum");
			//ReservationPage.reservationSearch(conf);

			// Clicking on view table
						Utils.WebdriverWait(100, "Reservation.staydates_viewTable", "clickable");
						Utils.jsClick("Reservation.staydates_viewTable");
						logger.log(LogStatus.PASS, "Clicking on View table");
						System.out.println("Clicked on view table");
						Utils.waitForSpinnerToDisappear(100);
						
			// Clicking on confirmation number  link
			Utils.WebdriverWait(1000, "Reservation.Reinstate_ConfirmationNumber", "clickable");
			Utils.jsClick("Reservation.Reinstate_ConfirmationNumber");
			logger.log(LogStatus.PASS, "Clicking on ConfirmationNumber  link");
			System.out.println("Clicked on ConfirmationNumber  link");
			Utils.waitForSpinnerToDisappear(100);

			// Select I_want_To
			Utils.WebdriverWait(1000, "Reservation.Reinstate_IwantTo", "clickable");
			Utils.jsClick("Reservation.Reinstate_IwantTo");
			logger.log(LogStatus.PASS, "Selected I Want to Link in  manage Page");
			System.out.println("Selected I Want to in manage profile..");
			Utils.waitForSpinnerToDisappear(100);	
			
			// Clicking on Reinstate  link
			Utils.WebdriverWait(100, "Reservation.link_Reservation_Reinstate", "clickable");
			Utils.jsClick("Reservation.link_Reservation_Reinstate");
			logger.log(LogStatus.PASS, "Clicked on Reinstate  link");
			System.out.println("Clicked on Reinstate  link");
			Utils.waitForSpinnerToDisappear(10);
			

			if (isExists("Reservation.header_ReservationReinstate_popup"))				
			{

				String ReinstateStatus=getText("Reservation.txt_ReservationReinstate_Status");
				System.out.println("Reinstate Status :" +ReinstateStatus);
			
				if(ReinstateStatus.contains("Reinstate Completed Successfully"))

				{		
					logger.log(LogStatus.PASS,"Reinstate Status:" +ReinstateStatus);
					System.out.println("Reinstate Status is completed:" +ReinstateStatus);

				}

				else
				{
					logger.log(LogStatus.FAIL,"Reinstate Status:" +ReinstateStatus);
					logger.log(LogStatus.FAIL, "Reinstate Reservation is not completed");
					System.out.println("Reinstate Status:" +ReinstateStatus);
					System.out.println("Reinstate Reservation is not completed");
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	private static String get(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	///////////////////////////////// Adding packages


	public static String createPackageFromLTB(HashMap<String, String> resvMapp) throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);
		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {
			Utils.takeScreenshot(driver, testName);

			// Navigating to Booking menu
			Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			System.out.println("clicked booking");
			logger.log(LogStatus.PASS, "Selected Bookings Menu");

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			System.out.println("clicked reservation");
			logger.log(LogStatus.PASS, "Selected Reservations Menu");

			// Navigating to LTB menu
			Utils.WebdriverWait(100, "Reservation.LookToBook_Menu", "clickable");
			Utils.jsClick("Reservation.LookToBook_Menu");
			System.out.println("clicked LTB");
			logger.log(LogStatus.PASS, "Selected LTB Menu");

			if (isExists("Reservation.New_Reservation_Button")) {
				// clicking on New Reservation Button
				WebdriverWait(100, "Reservation.New_Reservation_Button", "presence");
				mouseHover("Reservation.New_Reservation_Button");
				jsClick("Reservation.New_Reservation_Button");
				System.out.println("Look to Book Sales Screen is Displayed");
				logger.log(LogStatus.PASS, "Selected Create New reservation Button in ltb Page");
			}

			waitForPageLoad(100);
			// Validating Ltb page
			String resname = resvMap.get("ProfileName");
			WebdriverWait(100, "Reservation.ltb_valid", "presence");
			String text = Utils.getText("Reservation.ltb_valid");
			System.out.println("ltb text is " + text);
			logger.log(LogStatus.PASS, "Landed in Look To Book Page");
			// Utils.Wait(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (resvMap.get("Nights") != null) {
				Utils.textBox("Reservation.txt_ReservationEdit_Nights", resvMap.get("Nights"));
				Utils.tabKey("Reservation.txt_ReservationEdit_Nights");
				waitForSpinnerToDisappear(10);
			}

			/*
			 * // passing profile name Utils.WebdriverWait(100,
			 * "Reservation.ltb_name", "presence");
			 * Utils.textBox("Reservation.ltb_name", resname); //
			 * OR.getTestData("manage_reservation_name") System.out.println(
			 * "name entered in ltb.."); logger.log(LogStatus.PASS,
			 * "Provided Profile Name for Reservation");
			 * 
			 * if
			 * (isExists("Reservation.createReservation_frameProfileSelection"))
			 * {
			 * 
			 * driver.switchTo().frame(Utils.element(
			 * "Reservation.createReservation_frameProfileSelection")); //
			 * Thread.sleep(3000); // waitForSpinnerToDisappear(100);
			 * waitForPageLoad(100); // Select btn for profile
			 * Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select",
			 * "clickable");
			 * Utils.jsClick("Reservation.ltb_manage_profile_select");
			 * System.out.println("Selected select btn...");
			 * logger.log(LogStatus.PASS,
			 * "Selected Profile and Select btn in profile search page.."); //
			 * driver.switchTo().defaultContent(); // Thread.sleep(10000);
			 * waitForSpinnerToDisappear(100); waitForPageLoad(100); }
			 * waitForSpinnerToDisappear(100); waitForPageLoad(100); //
			 * Thread.sleep(3000); driver.switchTo().defaultContent();
			 * 
			 * // Thread.sleep(5000); waitForSpinnerToDisappear(100);
			 * waitForPageLoad(100); String titile = driver.getTitle();
			 * System.out.println("current titile :" + titile);
			 */

			// Rate and Room Options tab
			Utils.WebdriverWait(100, "Reservation.tab_RateAndRoom", "clickable");
			jsClick("Reservation.tab_RateAndRoom");
			System.out.println("selected RateAndRoom Tab in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn tab_RateAndRoomin ltb page..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			// waitForSpinnerToDisappear(100);

			// passing Room Type
			Utils.WebdriverWait(100, "Reservation.txt_RoomTypes", "presence");
			System.out.println("Room_Types: " + resvMap.get("Room_Types"));
			Utils.textBox("Reservation.txt_RoomTypes", resvMap.get("Room_Types")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Room Type..");
			logger.log(LogStatus.PASS, "Provided Room TYpe for Reservation - " + resvMap.get("Room_Types"));
			clear("Reservation.txt_RateCodes");
			waitForPageLoad(100);

			// passing Rate Codes
			Utils.WebdriverWait(100, "Reservation.txt_RateCodes", "presence");
			System.out.println("Rate_Code: " + resvMap.get("Rate_Code"));
			// clear("Reservation.txt_RateCodes");
			jsTextbox("Reservation.txt_RateCodes", resvMap.get("Rate_Code")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Rate Code..");
			logger.log(LogStatus.PASS, "Provided Rate Code for Reservation - " + resvMap.get("Rate_Code"));
			waitForPageLoad(100);
			clear("Reservation.txt_RateCategory");
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if ((isExists("Reservation.lov_RateCodeLOV"))) {
				System.out.println("Rate Code Available");
				WebdriverWait(100, "Reservation.lov_RateCodeCheckBox", "clickable");
				jsClick("Reservation.lov_RateCodeCheckBox");
				System.out.println("selected Rate Code CheckBox..");
				logger.log(LogStatus.PASS, "Selected Rate Code CheckBox..");

				WebdriverWait(100, "Reservation.btn_RateCodelovOK", "clickable");
				jsClick("Reservation.btn_RateCodelovOK");
				System.out.println("selected OK button.");
				logger.log(LogStatus.PASS, "Selected OK button.");
				waitForPageLoad(100);
			}

			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);
			// profile search btn
			Utils.WebdriverWait(100, "Reservation.ltb_search_btn", "clickable");
			jsClick("Reservation.ltb_search_btn");
			System.out.println("selected search btn in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn in ltb page for profile..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (!(isExists("Reservation.New_Reservation_Availability_suite"))) {
				System.out.println("No availability Available");
				logger.log(LogStatus.FAIL, "No availability Available..");
				// LoginPage.Logout();
				tearDown();
			}

			// Selecting Rate Code in Availabilty
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");

			// Selecting Continue Booking Butoon if exists
			waitForPageLoad(100);
			// Thread.sleep(3000);
			if (isExists("Reservation.btn_ContinueBooking")) {
				Utils.WebdriverWait(100, "Reservation.btn_ContinueBooking", "clickable");
				Utils.jsClick("Reservation.btn_ContinueBooking");
				System.out.println("Selected Continue Booking btn...");
				logger.log(LogStatus.PASS, "Selected Continue Booking button of Availability in LTB page..");
			}

			// Validating Reservation Page
			waitForPageLoad(100);
			// Thread.sleep(5000);
			String te = driver.findElement(By.xpath("//div[text() = 'Stay Information']")).getText();
			System.out.println("Stay: " + te);
			logger.log(LogStatus.PASS, "Landed and validated reservation create page..");

			// passing profile name
			Utils.WebdriverWait(100, "Reservation.txt_ProfileName", "presence");
			Utils.textBox("Reservation.txt_ProfileName", resname); // OR.getTestData("manage_reservation_name")
			System.out.println("name entered in ltb..");
			logger.log(LogStatus.PASS, "Provided Profile Name for Reservation");
			waitForPageLoad(100);

			mouseHover("Reservation.New_Reservation_Reservation_Type");
			click("Reservation.New_Reservation_Reservation_Type");

			if (isExists("Reservation.createReservation_frameProfileSelection")) {

				driver.switchTo().frame(Utils.element("Reservation.createReservation_frameProfileSelection"));
				// Thread.sleep(3000);
				// waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
				// Select btn for profile
				Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
				Utils.jsClick("Reservation.ltb_manage_profile_select");
				System.out.println("Selected select btn...");
				logger.log(LogStatus.PASS, "Selected Profile and Select btn in profile search page.."); //
				driver.switchTo().defaultContent();
				// Thread.sleep(10000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// Thread.sleep(3000);
			driver.switchTo().defaultContent();

			// Thread.sleep(5000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			String titile = driver.getTitle();
			System.out.println("current titile :" + titile);
			waitForPageLoad(100);
			/////////////////////////////////////////////////////




			// Provide Resv type
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Reservation_Type", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Reservation_Type");
			Utils.clear("Reservation.New_Reservation_Reservation_Type");
			Utils.textBox("Reservation.New_Reservation_Reservation_Type", resvMap.get("ReservationType"));
			System.out.println("Resv Type Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Reservation Type " + resvMap.get("ReservationType"));

			waitForPageLoad(100);

			// Provide Resv Market code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			//
			waitForPageLoad(100);
			// Provide Time
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Source code
			Utils.Wait(3000);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Source", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Source");
			Utils.jsTextbox("Reservation.New_Reservation_Source", resvMap.get("SourceCode"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			System.out.println("Resv Source Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Source Code " + resvMap.get("SourceCode"));

			// Provide // Time
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Market Code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			// Selecting Reservation Method -CASH
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.New_Reservation_Method")).perform();
			Utils.selectBy("Reservation.New_Reservation_Method", "text", resvMap.get("PaymentMethod"));
			System.out.println("CASH-Reservation Method is Selected");
			logger.log(LogStatus.PASS, "Payment method is selected " + resvMap.get("PaymentMethod"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);


			// clicking on the package link
			Utils.WebdriverWait(100, "Reservation.packages_LTB", "clickable");
			Utils.jsClick("Reservation.packages_LTB");
			logger.log(LogStatus.PASS, "Clicking on packages link");
			System.out.println("Clicked on packages link");
			Utils.waitForSpinnerToDisappear(100);


			// clicking on New button
			Utils.WebdriverWait(100, "Reservation.packages_New_LTB", "clickable");
			Utils.jsClick("Reservation.packages_New_LTB");
			logger.log(LogStatus.PASS, "Clicking on New button");
			System.out.println("Clicked on New  button");
			Utils.waitForSpinnerToDisappear(100);
			// Sending Keys to filter 
						String packageName = resvMapp.get("Package_Name");

						Utils.WebdriverWait(100, "Reservation.packages_Filter_LTB", "presence");
						Utils.mouseHover("Reservation.packages_Filter_LTB");
						Utils.jsTextbox("Reservation.packages_Filter_LTB", packageName);
						System.out.println("Sending Keys to Package filters:  " + packageName);
						Thread.sleep(2000);
						logger.log(LogStatus.PASS, "selecting the package:  " + packageName);
						Utils.waitForSpinnerToDisappear(100);
			
						
						// searching the package
						Utils.WebdriverWait(100, "Reservation.packages_Search", "clickable");
						Utils.jsClick("Reservation.packages_Search");
						logger.log(LogStatus.PASS, "searching the package");
						System.out.println("searching the package");
						Utils.waitForSpinnerToDisappear(100);
			
			// selecting the package
			Utils.WebdriverWait(100, "Reservation.packages_Select_LTB", "clickable");
			Utils.jsClick("Reservation.packages_Select_LTB");
			logger.log(LogStatus.PASS, "selecting  on package");
			System.out.println("selecting  the package");
			Utils.waitForSpinnerToDisappear(100);
			
			// clicking on Add
			Utils.WebdriverWait(100, "Reservation.packages_Add_LTB", "clickable");
			Utils.jsClick("Reservation.packages_Add_LTB");
			logger.log(LogStatus.PASS, "Clicking on ADD");
			System.out.println("Clicking on ADD");
			Utils.waitForSpinnerToDisappear(100);
			// Clicking on save
			Utils.WebdriverWait(100, "Reservation.packages_Svae_LTB", "clickable");
			Utils.jsClick("Reservation.packages_Svae_LTB");
			logger.log(LogStatus.PASS, "Clicking on Save");
			System.out.println("Clicking on Save");	
			Utils.waitForSpinnerToDisappear(100);
			
			// verify the added package													
//			waitForSpinnerToDisappear(50);
//			if (isExists("Reservation.packages_Tab_LTB")) 
//			{
//
//				//WebdriverWait(100, "Reservation.packages_Tab_LTB", "presence");
//				String package1 = getText("Reservation.packages_Added_LTB");
//				System.out.println("Added package " +package1);
//				logger.log(LogStatus.PASS, "Added package " +package1);
//			}

			// Clicking on continue booking
			Utils.WebdriverWait(100, "Reservation.packages_continue_booking_LTB", "clickable");
			Utils.jsClick("Reservation.packages_continue_booking_LTB");
			logger.log(LogStatus.PASS, "Clicking on continue booking");
			System.out.println("Clicking on continue booking");	


			// Selecting Book nOw Button
			Utils.WebdriverWait(100, "Reservation.New_Reservation_BookNowButton", "clickable");
			Utils.jsClick("Reservation.New_Reservation_BookNowButton");
			logger.log(LogStatus.PASS, "Selected Book Now Button ");
			// Thread.sleep(10000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Confirm Button
			if (isExists("Reservation.New_Reservation_Confirm_button")) {
				Utils.WebdriverWait(100, "Reservation.New_Reservation_Confirm_button", "clickable");
				Utils.click(Utils.element("Reservation.New_Reservation_Confirm_button"));
				logger.log(LogStatus.PASS, "Selected Confirm Button ");
				// Thread.sleep(5000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.create_res_confirmationNum", "presence");
			String ConfirmationNum = Utils.getText("Reservation.create_res_confirmationNum");
			System.out.println("Confirmation Num : " + ConfirmationNum);
			logger.log(LogStatus.PASS, "Landed in Resv Presentation Page: ");
			logger.log(LogStatus.PASS, "Confirmation Num : " + ConfirmationNum);
			// ExcelUtils.setCellData("Reservation", "Confirmation_Num",
			// iTestCaseRow, ConfirmationNum);
			/*
			 * ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"),
			 * "Reservation", "createReservation", "ConfirmationNum",
			 * ConfirmationNum);
			 */

			if (Utils.isExists("Reservation.create_res_confirmationNum")) {
				System.out.println("Package added for the new Reservation Successfully with confirmation Num: " + ConfirmationNum);
				logger.log(LogStatus.PASS,
						"Package added for the new Reservation Successfully with confirmation Num : " + ConfirmationNum);
			} else {
				System.out.println("Package not added for the new Reservation Successfully ");
				logger.log(LogStatus.FAIL, "Package not added for the new Reservation Successfully");
			}

			/*
			 * if (!writeDataPropFile("ConfirmationNum", ConfirmationNum)) {
			 * logger.log(LogStatus.FAIL,
			 * "Failed to write value into the properties file");
			 * System.out.println(
			 * "Failed to write value into the properties file"); }
			 */

			logger.log(LogStatus.PASS, "Reservation created and Validated");
			return ConfirmationNum;
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: Extending the stay details for a Reservation 
	- Input: stay dates,Confirmation Number
	- Author:Tulasi
	- Date: 7/2/2019
	
	********************************************************************/
	public static void extendstayDetails(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);


			// Clicking on view table
			Utils.WebdriverWait(100, "Reservation.staydates_viewTable", "clickable");
			Utils.jsClick("Reservation.staydates_viewTable");
			logger.log(LogStatus.PASS, "Clicking on View table");
			System.out.println("Clicked on view table");
			Utils.waitForSpinnerToDisappear(100);
			
			// Clicking on confirmation number  link
			Utils.WebdriverWait(200, "Reservation.CopyReservation_ConfirmationNumber", "clickable");
			Utils.jsClick("Reservation.CopyReservation_ConfirmationNumber");
			logger.log(LogStatus.PASS, "Clicking on ConfirmationNumber  link");
			System.out.println("Clicked on ConfirmationNumber  link");
			Utils.waitForSpinnerToDisappear(100);
			// getting the nights text

			String nights= driver.findElement(By.xpath("//*[@data-ocid='OTEXT_OCC_STYDT_NGHTS_TXT']")).getAttribute("innerText");
			logger.log(LogStatus.PASS, "Nights value "+nights);
			System.out.println("Nights value "+nights);	
			Thread.sleep(3000);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.staydates_showall", "clickable");
			Utils.jsClick("Reservation.staydates_showall");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Thread.sleep(3000);
			// Select stay details
			Utils.WebdriverWait(200, "Reservation.staydates_staydetails", "clickable");
			Utils.jsClick("Reservation.staydates_staydetails");
			logger.log(LogStatus.PASS, "Selected stay details  Link");
			System.out.println("Selected stay details  Link");		
			Thread.sleep(3000);	
			// Select change stay details
			Utils.WebdriverWait(200, "Reservation.staydates_chanegstaydetails", "clickable");
			Utils.jsClick("Reservation.staydates_chanegstaydetails");
			logger.log(LogStatus.PASS, "Selected change stay details  Link");
			System.out.println("Selected change stay details  Link");	
			Thread.sleep(3000);	

			// clicking on nights

			Utils.WebdriverWait(200, "Reservation.staydates_nights", "clickable");
			Utils.jsClick("Reservation.staydates_nights");
			logger.log(LogStatus.PASS, "Clicking on + button of nights");
			System.out.println("Clicking on + button of nights");

			// clicking on children

			Utils.WebdriverWait(200, "Reservation.staydates_child", "clickable");
			Utils.jsClick("Reservation.staydates_child");
			logger.log(LogStatus.PASS, "Clicking on + button of children");
			System.out.println("Clicking on + button of children");

			String child=getTextValue("Reservation.staydates_numchild");
			logger.log(LogStatus.PASS, "No.of childs added :" +child);
			System.out.println("No.of childs added :"+child);

			//Thread.sleep(3000);
			// clicking on search button
			Utils.WebdriverWait(100, "Reservation.btn_CopyReservation_Search", "clickable");
			Utils.jsClick("Reservation.btn_CopyReservation_Search");
			logger.log(LogStatus.PASS, "Clicking on search button");
			System.out.println("Clicked on Search button");

			//  clicking on room button
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");
			System.out.println("Selected Availability in LTB page..");
			// confirmation of stay details page

			if (isExists("Reservation.staydates_resvpopup")) 
			{


				logger.log(LogStatus.PASS, "Reservation page is displayed");
				System.out.println("Reservation page is displayed");

				// clicking on change reservation now  button
				Utils.WebdriverWait(100, "Reservation.staydates_changereservation", "clickable");
				Utils.jsClick("Reservation.staydates_changereservation");
				logger.log(LogStatus.PASS, "Clicking on change reservation button");
				System.out.println("Clicked on change reservation button");

				if (isExists("Reservation.New_Reservation_Confirm_button")) 
				{
					Utils.WebdriverWait(100, "Reservation.New_Reservation_Confirm_button", "clickable");
					Utils.jsClick("Reservation.New_Reservation_Confirm_button");
					logger.log(LogStatus.PASS, "Clicking on confirm button");
					System.out.println("Clicked on confirm button");
				}

			}

			String extendnights= driver.findElement(By.xpath("//*[@data-ocid='OTEXT_OCC_STYDT_NGHTS_TXT']")).getAttribute("innerText");
			if (extendnights!= nights)
			{
				logger.log(LogStatus.PASS, "No.of nights:"+extendnights);
				System.out.println("No.of nights:"+extendnights);	
				logger.log(LogStatus.PASS, "stay details information is extended ");
				System.out.println("stay details information is extended");
			}

			else
			{
				logger.log(LogStatus.FAIL, "stay details information is not extended ");
				System.out.println("stay details information is not extended");
			}

		} 
		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}


 
	public static String createReservationWithClosingScript(HashMap<String, String> resvMapp) throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);
		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {

			Utils.takeScreenshot(driver, testName);

			// Navigating to Booking menu
			Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			System.out.println("clicked booking");
			logger.log(LogStatus.PASS, "Selected Bookings Menu");

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			System.out.println("clicked reservation");
			logger.log(LogStatus.PASS, "Selected Reservations Menu");

			// Navigating to LTB menu
			Utils.WebdriverWait(100, "Reservation.LookToBook_Menu", "clickable");
			Utils.jsClick("Reservation.LookToBook_Menu");
			System.out.println("clicked LTB");
			logger.log(LogStatus.PASS, "Selected LTB Menu");

			waitForSpinnerToDisappear(50);
			if (isExists("Reservation.New_Reservation_Button")) {
				// clicking on New Reservation Button
				WebdriverWait(100, "Reservation.New_Reservation_Button", "presence");
				mouseHover("Reservation.New_Reservation_Button");
				jsClick("Reservation.New_Reservation_Button");
				System.out.println("Look to Book Sales Screen is Displayed");
				logger.log(LogStatus.PASS, "Selected Create New reservation Button in ltb Page");
			}

			waitForPageLoad(100);
			// Validating Ltb page
			String resname = resvMap.get("ProfileName");
			WebdriverWait(100, "Reservation.ltb_valid", "presence");
			String text = Utils.getText("Reservation.ltb_valid");
			System.out.println("ltb text is " + text);
			logger.log(LogStatus.PASS, "Landed in Look To Book Page");
			// Utils.Wait(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (resvMap.get("Nights") != null) {
				Utils.textBox("Reservation.txt_ReservationEdit_Nights", resvMap.get("Nights"));
				Utils.tabKey("Reservation.txt_ReservationEdit_Nights");
				waitForSpinnerToDisappear(10);
			}

			
			// Rate and Room Options tab
			Utils.WebdriverWait(100, "Reservation.tab_RateAndRoom", "clickable");
			jsClick("Reservation.tab_RateAndRoom");
			System.out.println("selected RateAndRoom Tab in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn tab_RateAndRoomin ltb page..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			// waitForSpinnerToDisappear(100);

			// passing Room Type
			Utils.WebdriverWait(100, "Reservation.txt_RoomTypes", "presence");
			System.out.println("Room_Types: " + resvMap.get("Room_Types"));
			Utils.textBox("Reservation.txt_RoomTypes", resvMap.get("Room_Types")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Room Type..");
			logger.log(LogStatus.PASS, "Provided Room TYpe for Reservation - " + resvMap.get("Room_Types"));
			clear("Reservation.txt_RateCodes");
			waitForPageLoad(100);

			// passing Rate Codes
			Utils.WebdriverWait(100, "Reservation.txt_RateCodes", "presence");
			System.out.println("Rate_Code: " + resvMap.get("Rate_Code"));
			// clear("Reservation.txt_RateCodes");
			jsTextbox("Reservation.txt_RateCodes", resvMap.get("Rate_Code")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Rate Code..");
			logger.log(LogStatus.PASS, "Provided Rate Code for Reservation - " + resvMap.get("Rate_Code"));
			waitForPageLoad(100);
			clear("Reservation.txt_RateCategory");
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if ((isExists("Reservation.lov_RateCodeLOV"))) {
				System.out.println("Rate Code Available");
				WebdriverWait(100, "Reservation.lov_RateCodeCheckBox", "clickable");
				jsClick("Reservation.lov_RateCodeCheckBox");
				System.out.println("selected Rate Code CheckBox..");
				logger.log(LogStatus.PASS, "Selected Rate Code CheckBox..");

				WebdriverWait(100, "Reservation.btn_RateCodelovOK", "clickable");
				jsClick("Reservation.btn_RateCodelovOK");
				System.out.println("selected OK button.");
				logger.log(LogStatus.PASS, "Selected OK button.");
				waitForPageLoad(100);
			}

			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);
			// profile search btn
			Utils.WebdriverWait(100, "Reservation.ltb_search_btn", "clickable");
			jsClick("Reservation.ltb_search_btn");
			System.out.println("selected search btn in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn in ltb page for profile..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (!(isExists("Reservation.New_Reservation_Availability_suite"))) {
				System.out.println("No availability Available");
				logger.log(LogStatus.FAIL, "No availability Available..");
				// LoginPage.Logout();
				tearDown();
			}

			// Selecting Rate Code in Availabilty
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");

			// Selecting Continue Booking Butoon if exists
			waitForPageLoad(100);
			// Thread.sleep(3000);
			if (isExists("Reservation.btn_ContinueBooking")) {
				Utils.WebdriverWait(100, "Reservation.btn_ContinueBooking", "clickable");
				Utils.jsClick("Reservation.btn_ContinueBooking");
				System.out.println("Selected Continue Booking btn...");
				logger.log(LogStatus.PASS, "Selected Continue Booking button of Availability in LTB page..");
			}

			// Validating Reservation Page
			waitForPageLoad(100);
			// Thread.sleep(5000);
			String te = driver.findElement(By.xpath("//div[text() = 'Stay Information']")).getText();
			System.out.println("Stay: " + te);
			logger.log(LogStatus.PASS, "Landed and validated reservation create page..");

			// passing profile name
			Utils.WebdriverWait(100, "Reservation.txt_ProfileName", "presence");
			Utils.textBox("Reservation.txt_ProfileName", resname); // OR.getTestData("manage_reservation_name")
			System.out.println("name entered in ltb..");
			logger.log(LogStatus.PASS, "Provided Profile Name for Reservation");
			waitForPageLoad(100);

			mouseHover("Reservation.New_Reservation_Reservation_Type");
			click("Reservation.New_Reservation_Reservation_Type");

			if (isExists("Reservation.createReservation_frameProfileSelection")) {

				driver.switchTo().frame(Utils.element("Reservation.createReservation_frameProfileSelection"));
				// Thread.sleep(3000);
				// waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
				// Select btn for profile
				Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
				Utils.jsClick("Reservation.ltb_manage_profile_select");
				System.out.println("Selected select btn...");
				logger.log(LogStatus.PASS, "Selected Profile and Select btn in profile search page.."); //
				driver.switchTo().defaultContent();
				// Thread.sleep(10000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// Thread.sleep(3000);
			driver.switchTo().defaultContent();

			// Thread.sleep(5000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			String titile = driver.getTitle();
			System.out.println("current titile :" + titile);
			waitForPageLoad(100);

			// Provide Resv type
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Reservation_Type", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Reservation_Type");
			Utils.clear("Reservation.New_Reservation_Reservation_Type");
			Utils.textBox("Reservation.New_Reservation_Reservation_Type", resvMap.get("ReservationType"));
			System.out.println("Resv Type Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Reservation Type " + resvMap.get("ReservationType"));

			waitForPageLoad(100);

			// Provide Resv Market code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			//
			waitForPageLoad(100);
			// Provide Time
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Source code
			Utils.Wait(3000);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Source", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Source");
			Utils.jsTextbox("Reservation.New_Reservation_Source", resvMap.get("SourceCode"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			System.out.println("Resv Source Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Source Code " + resvMap.get("SourceCode"));

			// Provide // Time
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Market Code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			// Selecting Reservation Method -CASH
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.New_Reservation_Method")).perform();
			Utils.selectBy("Reservation.New_Reservation_Method", "text", resvMap.get("PaymentMethod"));
			System.out.println("CASH-Reservation Method is Selected");
			logger.log(LogStatus.PASS, "Payment method is selected " + resvMap.get("PaymentMethod"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Book nOw Button
			Utils.WebdriverWait(100, "Reservation.New_Reservation_BookNowButton", "clickable");
			Utils.jsClick("Reservation.New_Reservation_BookNowButton");
			logger.log(LogStatus.PASS, "Selected Book Now Button ");
			// Thread.sleep(10000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Confirm Button
			if (isExists("Reservation.New_Reservation_Confirm_button")) {
				WebElement CScript = driver.findElement(By.xpath("//*[@data-ocid='OCPNL_CLOSINGSCRIPT']/div[2]/div/div/div[5]/span/span[2]/span/div/div[4]/span/span/span/span/span"));
				String ClosingScript = CScript.getText();
				System.out.println("ClosingScript: "+ClosingScript);
				if(ClosingScript.contains("No closing script"))
				{
					System.out.println("No Closing Script");
					logger.log(LogStatus.FAIL, "No Closing Script available for the reservation ");
				}
				
				Utils.WebdriverWait(100, "Reservation.New_Reservation_Confirm_button", "clickable");
				Utils.click(Utils.element("Reservation.New_Reservation_Confirm_button"));
				logger.log(LogStatus.PASS, "Selected Confirm Button ");
				// Thread.sleep(5000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.create_res_confirmationNum", "presence");
			String ConfirmationNum = Utils.getText("Reservation.create_res_confirmationNum");
			System.out.println("Confirmation Num : " + ConfirmationNum);
			logger.log(LogStatus.PASS, "Landed in Resv Presentation Page: ");
			logger.log(LogStatus.PASS, "Confirmation Num : " + ConfirmationNum);
			// ExcelUtils.setCellData("Reservation", "Confirmation_Num",
			// iTestCaseRow, ConfirmationNum);
			/*
			 * ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"),
			 * "Reservation", "createReservation", "ConfirmationNum",
			 * ConfirmationNum);
			 */

			if (Utils.isExists("Reservation.create_res_confirmationNum")) {
				System.out.println("Reservation Created Successfully with Confirmation Num: " + ConfirmationNum);
				logger.log(LogStatus.PASS,
						"Reservation Created Successfully.with Confirmation Num: " + ConfirmationNum);
			} else {
				System.out.println("Reservation Not Created Successfully ");
				logger.log(LogStatus.FAIL, "Reservation Not Created Successfully");
			}

			/*
			 * if (!writeDataPropFile("ConfirmationNum", ConfirmationNum)) {
			 * logger.log(LogStatus.FAIL,
			 * "Failed to write value into the properties file");
			 * System.out.println(
			 * "Failed to write value into the properties file"); }
			 */

			logger.log(LogStatus.PASS, "Reservation created and Validated");
			return ConfirmationNum;
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	///////////////////////// trip composer
	public static String tripComposer(HashMap<String, String> resvMapp) throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);
		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {

			Utils.takeScreenshot(driver, testName);

			// Navigating to Booking menu
			Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			System.out.println("clicked booking");
			logger.log(LogStatus.PASS, "Selected Bookings Menu");

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			System.out.println("clicked reservation");
			logger.log(LogStatus.PASS, "Selected Reservations Menu");

			// Navigating to LTB menu
			Utils.WebdriverWait(100, "Reservation.LookToBook_Menu", "clickable");
			Utils.jsClick("Reservation.LookToBook_Menu");
			System.out.println("clicked LTB");
			logger.log(LogStatus.PASS, "Selected LTB Menu");

			waitForSpinnerToDisappear(50);
			if (isExists("Reservation.New_Reservation_Button")) {
				// clicking on New Reservation Button
				WebdriverWait(100, "Reservation.New_Reservation_Button", "presence");
				mouseHover("Reservation.New_Reservation_Button");
				jsClick("Reservation.New_Reservation_Button");
				System.out.println("Look to Book Sales Screen is Displayed");
				logger.log(LogStatus.PASS, "Selected Create New reservation Button in ltb Page");
			}

			waitForPageLoad(100);
			// Validating Ltb page
			String resname = resvMap.get("ProfileName");
			WebdriverWait(100, "Reservation.ltb_valid", "presence");
			String text = Utils.getText("Reservation.ltb_valid");
			System.out.println("ltb text is " + text);
			logger.log(LogStatus.PASS, "Landed in Look To Book Page");
			// Utils.Wait(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);


			// Clicking on multy segment
			Utils.WebdriverWait(100, "Reservation.tripComposer_Mutlisegment", "clickable");
			Utils.jsClick("Reservation.tripComposer_Mutlisegment");
			System.out.println("clicked on Multi segment");
			logger.log(LogStatus.PASS, "clicked on Multi segment");
			waitForSpinnerToDisappear(100);


			if (resvMap.get("Nights") != null) {
				Utils.textBox("Reservation.txt_ReservationEdit_Nights", resvMap.get("Nights"));
				Utils.tabKey("Reservation.txt_ReservationEdit_Nights");
				waitForSpinnerToDisappear(10);
			}


			// Rate and Room Options tab
			Utils.WebdriverWait(100, "Reservation.tab_RateAndRoom", "clickable");
			jsClick("Reservation.tab_RateAndRoom");
			System.out.println("selected RateAndRoom Tab in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn tab_RateAndRoomin ltb page..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			// waitForSpinnerToDisappear(100);

			// passing Room Type
			Utils.WebdriverWait(100, "Reservation.txt_RoomTypes", "presence");
			System.out.println("Room_Types: " + resvMap.get("Room_Types"));
			Utils.textBox("Reservation.txt_RoomTypes", resvMap.get("Room_Types")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Room Type..");
			logger.log(LogStatus.PASS, "Provided Room TYpe for Reservation - " + resvMap.get("Room_Types"));
			clear("Reservation.txt_RateCodes");
			waitForPageLoad(100);

			// passing Rate Codes
			Utils.WebdriverWait(100, "Reservation.txt_RateCodes", "presence");
			System.out.println("Rate_Code: " + resvMap.get("Rate_Code"));
			// clear("Reservation.txt_RateCodes");
			jsTextbox("Reservation.txt_RateCodes", resvMap.get("Rate_Code")); // OR.getTestData("manage_reservation_name")
			System.out.println("Passing Rate Code..");
			logger.log(LogStatus.PASS, "Provided Rate Code for Reservation - " + resvMap.get("Rate_Code"));
			waitForPageLoad(100);
			clear("Reservation.txt_RateCategory");
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if ((isExists("Reservation.lov_RateCodeLOV"))) {
				System.out.println("Rate Code Available");
				WebdriverWait(100, "Reservation.lov_RateCodeCheckBox", "clickable");
				jsClick("Reservation.lov_RateCodeCheckBox");
				System.out.println("selected Rate Code CheckBox..");
				logger.log(LogStatus.PASS, "Selected Rate Code CheckBox..");

				WebdriverWait(100, "Reservation.btn_RateCodelovOK", "clickable");
				jsClick("Reservation.btn_RateCodelovOK");
				System.out.println("selected OK button.");
				logger.log(LogStatus.PASS, "Selected OK button.");
				waitForPageLoad(100);
			}

			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);
			// profile search btn
			Utils.WebdriverWait(100, "Reservation.ltb_search_btn", "clickable");
			jsClick("Reservation.ltb_search_btn");
			System.out.println("selected search btn in ltb..");
			logger.log(LogStatus.PASS, "Selected search btn in ltb page for profile..");
			// Thread.sleep(5000);
			waitForPageLoad(100);
			waitForSpinnerToDisappear(100);

			if (!(isExists("Reservation.New_Reservation_Availability_suite"))) {
				System.out.println("No availability Available");
				logger.log(LogStatus.FAIL, "No availability Available..");
				// LoginPage.Logout();
				tearDown();
			}

			// Selecting Rate Code in Availabilty
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");

			// Selecting Continue Booking Butoon if exists
			waitForPageLoad(100);
			// Thread.sleep(3000);
			if (isExists("Reservation.btn_ContinueBooking")) {
				Utils.WebdriverWait(100, "Reservation.btn_ContinueBooking", "clickable");
				Utils.jsClick("Reservation.btn_ContinueBooking");
				System.out.println("Selected Continue Booking btn...");
				logger.log(LogStatus.PASS, "Selected Continue Booking button of Availability in LTB page..");
			}

			// Validating Reservation Page
			waitForPageLoad(100);
			// Thread.sleep(5000);
			String te = driver.findElement(By.xpath("//div[text() = 'Stay Information']")).getText();
			System.out.println("Stay: " + te);
			logger.log(LogStatus.PASS, "Landed and validated reservation create page..");

			// passing profile name
			Utils.WebdriverWait(100, "Reservation.txt_ProfileName", "presence");
			Utils.textBox("Reservation.txt_ProfileName", resname); // OR.getTestData("manage_reservation_name")
			System.out.println("name entered in ltb..");
			logger.log(LogStatus.PASS, "Provided Profile Name for Reservation");
			waitForPageLoad(100);

			mouseHover("Reservation.New_Reservation_Reservation_Type");
			click("Reservation.New_Reservation_Reservation_Type");

			if (isExists("Reservation.createReservation_frameProfileSelection")) {

				driver.switchTo().frame(Utils.element("Reservation.createReservation_frameProfileSelection"));
				// Thread.sleep(3000);
				// waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
				// Select btn for profile
				Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
				Utils.jsClick("Reservation.ltb_manage_profile_select");
				System.out.println("Selected select btn...");
				logger.log(LogStatus.PASS, "Selected Profile and Select btn in profile search page.."); //
				driver.switchTo().defaultContent();
				// Thread.sleep(10000);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// Thread.sleep(3000);
			driver.switchTo().defaultContent();

			// Thread.sleep(5000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			String titile = driver.getTitle();
			System.out.println("current titile :" + titile);
			waitForPageLoad(100);

			// Provide Resv type
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Reservation_Type", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Reservation_Type");
			Utils.clear("Reservation.New_Reservation_Reservation_Type");
			Utils.textBox("Reservation.New_Reservation_Reservation_Type", resvMap.get("ReservationType"));
			System.out.println("Resv Type Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Reservation Type " + resvMap.get("ReservationType"));

			waitForPageLoad(100);

			// Provide Resv Market code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			//
			waitForPageLoad(100);
			// Provide Time
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Source code
			Utils.Wait(3000);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Source", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Source");
			Utils.jsTextbox("Reservation.New_Reservation_Source", resvMap.get("SourceCode"));
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			System.out.println("Resv Source Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Source Code " + resvMap.get("SourceCode"));

			// Provide // Time
			waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Arrival", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Arrival");
			Utils.jsClick("Reservation.New_Reservation_Arrival");
			System.out.println("Resv time Entered in Reservation name field");

			// Provide Resv Market Code
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			// waitForPageLoad(100);
			Utils.WebdriverWait(30, "Reservation.New_Reservation_Market", "presence");
			Utils.mouseHover("Reservation.New_Reservation_Market");
			Utils.clear("Reservation.New_Reservation_Market");
			Utils.jsTextbox("Reservation.New_Reservation_Market", resvMap.get("MarketCode"));
			System.out.println("Resv Market Entered in Reservation name field");
			logger.log(LogStatus.PASS, "Provided Market Code " + resvMap.get("MarketCode"));

			// Selecting Reservation Method -CASH
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.New_Reservation_Method")).perform();
			Utils.selectBy("Reservation.New_Reservation_Method", "text", resvMap.get("PaymentMethod"));
			System.out.println("CASH-Reservation Method is Selected");
			logger.log(LogStatus.PASS, "Payment method is selected " + resvMap.get("PaymentMethod"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// clicking on add trip composer
			Utils.WebdriverWait(100, "Reservation.tripComposer_addtripcomposer", "clickable");
			Utils.jsClick("Reservation.tripComposer_addtripcomposer");
			logger.log(LogStatus.PASS, "clicked on add trip composer Button ");
			System.out.println("clicked on add trip composer Button ");

			// Selecting Rate Code in Availabilty
			Utils.WebdriverWait(100, "Reservation.New_Reservation_Availability_suite", "clickable");
			scroll("down");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			mouseHover("Reservation.New_Reservation_Availability_suite");
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			System.out.println("Selected room in LTB page..");
			// Utils.Wait(3000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.click(Utils.element("Reservation.New_Reservation_Availability_suite"));
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			Utils.jsClick("Reservation.New_Reservation_Availability_suite");
			logger.log(LogStatus.PASS, "Selected Availability in LTB page..");
			System.out.println("Selected room in LTB page..");

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");
			waitForSpinnerToDisappear(100);

			// clicking on update trip composer
			Utils.WebdriverWait(100, "Reservation.tripComposer_updatetripcomposer", "clickable");
			Utils.jsClick("Reservation.tripComposer_updatetripcomposer");
			logger.log(LogStatus.PASS, "clicked on add trip composer Button ");
			System.out.println("clicked on update trip composer Button ");
			waitForSpinnerToDisappear(100);

			// Selecting Book nOw Button
			Utils.WebdriverWait(100, "Reservation.New_Reservation_BookNowButton", "clickable");
			Utils.jsClick("Reservation.New_Reservation_BookNowButton");
			logger.log(LogStatus.PASS, "Selected Book Now Button ");
			// Thread.sleep(10000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// Selecting Confirm Button
			if (isExists("Reservation.New_Reservation_Confirm_button")) {
				Utils.WebdriverWait(100, "Reservation.New_Reservation_Confirm_button", "clickable");
				Utils.click(Utils.element("Reservation.New_Reservation_Confirm_button"));
				logger.log(LogStatus.PASS, "Selected Confirm Button ");
				// Thread.sleep(5000);
				waitForSpinnerToDisappear(100);
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
			}
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.create_res_confirmationNum", "presence");
			String ConfirmationNum = Utils.getText("Reservation.create_res_confirmationNum");
			System.out.println("Confirmation Num : " + ConfirmationNum);
			logger.log(LogStatus.PASS, "Landed in Resv Presentation Page: ");
			logger.log(LogStatus.PASS, "Confirmation Num : " + ConfirmationNum);
			// ExcelUtils.setCellData("Reservation", "Confirmation_Num",
			// iTestCaseRow, ConfirmationNum);
			/*
			 * ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"),
			 * "Reservation", "createReservation", "ConfirmationNum",
			 * ConfirmationNum);
			 */

			if (Utils.isExists("Reservation.create_res_confirmationNum")) {
				System.out.println("Reservation Created Successfully with Confirmation Num: " + ConfirmationNum);
				logger.log(LogStatus.PASS,
						"Reservation Created Successfully.with Confirmation Num: " + ConfirmationNum);
			} else {
				System.out.println("Reservation Not Created Successfully ");
				logger.log(LogStatus.FAIL, "Reservation Not Created Successfully");
			}

			/*
			 * if (!writeDataPropFile("ConfirmationNum", ConfirmationNum)) {
			 * logger.log(LogStatus.FAIL,
			 * "Failed to write value into the properties file");
			 * System.out.println(
			 * "Failed to write value into the properties file"); }
			 */

			logger.log(LogStatus.PASS, "Reservation created and Validated");
			return ConfirmationNum;
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}
	
	public static void addingTraces(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);




			// Select I want to
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);


			System.out.println("*** Adding the Traces ***");	
			// Select Traces
			Utils.WebdriverWait(100, "Reservation.Traces_Link", "clickable");
			Utils.jsClick("Reservation.Traces_Link");
			logger.log(LogStatus.PASS, "Selected Traces  Link");
			System.out.println("Selected Traces  Link");		
			Utils.waitForSpinnerToDisappear(100);

			// Select New
			Utils.WebdriverWait(100, "Reservation.Traces_New", "clickable");
			Utils.jsClick("Reservation.Traces_New");
			logger.log(LogStatus.PASS, "Selected new  ");
			System.out.println("Selected new");	
			Utils.waitForSpinnerToDisappear(100);

			// Select Traces LOV
			Utils.WebdriverWait(100, "Reservation.Department_LOV", "clickable");
			Utils.jsClick("Reservation.Department_LOV");
			logger.log(LogStatus.PASS, "Selected Department LOv  ");
			System.out.println("Selected Department LOv ");	
			Utils.waitForSpinnerToDisappear(100);		

			// Sending Keys to filter 
			String DeptName = resvMapp.get("Department_Name");

			Utils.WebdriverWait(100, "Reservation.Department_value", "presence");
			Utils.mouseHover("Reservation.Department_value");
			Utils.jsTextbox("Reservation.Department_value", DeptName);
			System.out.println("Sending Keys to Department filters:  " + DeptName);
			logger.log(LogStatus.PASS, "selecting the Department:  " + DeptName);
			Utils.waitForSpinnerToDisappear(100);


			// searching the Department
			Utils.WebdriverWait(100, "Reservation.Department_search", "clickable");
			Utils.jsClick("Reservation.Department_search");
			logger.log(LogStatus.PASS, "searching the Department");
			System.out.println("searching the Department");
			Utils.waitForSpinnerToDisappear(100);

			// selecting the Department
			Utils.WebdriverWait(100, "Reservation.Department_selectvalue", "clickable");
			Utils.jsClick("Reservation.Department_selectvalue");
			logger.log(LogStatus.PASS, "selecting  on Department");
			System.out.println("selecting  the Department");
			Utils.waitForSpinnerToDisappear(100);

			// selecting the select button
			Utils.WebdriverWait(100, "Reservation.Department_select", "clickable");
			Utils.jsClick("Reservation.Department_select");
			logger.log(LogStatus.PASS, "clicked  on select");
			System.out.println("clicked  on select");
			Utils.waitForSpinnerToDisappear(100);

			// Sending TraceText
			String TraceText = resvMapp.get("Trace_Text");

			Utils.WebdriverWait(100, "Reservation.Trace_Text", "presence");
			Utils.mouseHover("Reservation.Trace_Text");
			Utils.jsTextbox("Reservation.Trace_Text", TraceText);
			System.out.println("Sending data to Trace text:  " + TraceText);
			logger.log(LogStatus.PASS, "Sending data to Trace text:  " + TraceText);

			// selecting the save button
			Utils.WebdriverWait(100, "Reservation.Trace_Save", "clickable");
			Utils.jsClick("Reservation.Trace_Save");
			logger.log(LogStatus.PASS, "clicked  on save");
			System.out.println("clicked  on save");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Reservation.Trace_verify"))				
			{

				Utils.WebdriverWait(100, "Reservation.Trace_verify", "presence");			
				System.out.println("Added the Traces" );
				logger.log(LogStatus.PASS, "Added the Traces");

				click("Reservation.Trace_Close", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
			}
			else
			{
				System.out.println("Traces are not added" );
				logger.log(LogStatus.FAIL, "Traces are not added");
			}
		}
		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding Traces :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	///////////////////// updating the traces
	public static void updatingTraces(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Updating the Traces ***");	

			System.out.println("Updating the Traces for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Updating the Traces for the confirmation number:" + resvMapp.get("ConfirmationNum"));

			// Select I want to
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);


			// Select Traces
			Utils.WebdriverWait(100, "Reservation.Traces_Link", "clickable");
			Utils.jsClick("Reservation.Traces_Link");
			logger.log(LogStatus.PASS, "Selected Traces  Link");
			System.out.println("Selected Traces  Link");		
			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.Trace_Actions", "clickable");
			Utils.jsClick("Reservation.Trace_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on edit
			Utils.WebdriverWait(100, "Reservation.Trace_Edit", "clickable");
			Utils.jsClick("Reservation.Trace_Edit");
			logger.log(LogStatus.PASS, "clicked  on Edit");
			System.out.println("clicked  on Edit");
			Utils.waitForSpinnerToDisappear(100);

			String TraceText=getText("Reservation.Trace_Text");
			System.out.println("Old Trace text:  " + TraceText);
			logger.log(LogStatus.PASS, "old Trace text:  " + TraceText);

			// updating the text of trace
			String EditTraceText = resvMapp.get("EditTrace_Text");

			Utils.WebdriverWait(100, "Reservation.Trace_Text", "presence");
			Utils.mouseHover("Reservation.Trace_Text");
			Utils.jsTextbox("Reservation.Trace_Text", EditTraceText);
			System.out.println("Sending data to Trace text:  " + EditTraceText);

			logger.log(LogStatus.PASS, "Sending data to Trace text:  " + EditTraceText);

			// selecting the save button
			Utils.WebdriverWait(100, "Reservation.Trace_Save", "clickable");
			Utils.jsClick("Reservation.Trace_Save");
			logger.log(LogStatus.PASS, "clicked  on save");
			System.out.println("clicked  on save");
			Utils.waitForSpinnerToDisappear(100);


			if (isExists("Reservation.Trace_Editverify"))				
			{
				String updtedText=getText("Reservation.Trace_Editverify");
				Utils.WebdriverWait(100, "Reservation.Trace_Editverify", "presence");	
				if(TraceText!=updtedText)	
				{
					System.out.println("updated the Traces text to: "+ updtedText);
					logger.log(LogStatus.PASS, "updated the Traces text to: " +updtedText);
				}
				else
				{
					System.out.println("Traces are not updated" );
					logger.log(LogStatus.FAIL, "Traces are not updated");
				}
				click("Reservation.Trace_Close", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
			}
			else
			{
				System.out.println("Traces text is not avaliable" );
				logger.log(LogStatus.FAIL, "Traces text is not avaliable");
			}

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Upadting Traces  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created:: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	///////////////////// deleting the traces
	public static void deletingTraces(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Deleting the Traces ***");	

			System.out.println("Deleting the Traces for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Deleting the Traces for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			// Select I want to
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);

			// Select Traces
			Utils.WebdriverWait(100, "Reservation.Traces_Link", "clickable");
			Utils.jsClick("Reservation.Traces_Link");
			logger.log(LogStatus.PASS, "Selected Traces  Link");
			System.out.println("Selected Traces  Link");		
			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.Trace_Actions", "clickable");
			Utils.jsClick("Reservation.Trace_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete
			Utils.WebdriverWait(100, "Reservation.Trace_Delete", "clickable");
			Utils.jsClick("Reservation.Trace_Delete");
			logger.log(LogStatus.PASS, "clicked  on delete action");
			System.out.println("clicked  on delete action");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete button
			Utils.WebdriverWait(100, "Reservation.Trace_Deletebutton", "clickable");
			Utils.jsClick("Reservation.Trace_Deletebutton");
			logger.log(LogStatus.PASS, "clicked  on delete button");
			System.out.println("clicked  on delete button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Reservation.Trace_verify"))				
			{

				Utils.WebdriverWait(100, "Reservation.Trace_verify", "presence");			
				System.out.println("Traces are not deleted" );
				logger.log(LogStatus.FAIL, "Traces are not deleted");

			}
			else
			{
				System.out.println("Traces are deleted" );
				logger.log(LogStatus.PASS, "Traces are deleted");
			}
			//close
			click("Reservation.Trace_Close", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Deleting Traces  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created:: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}



	///////////////// adding the Alerts

	public static void addingAlerts(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);


			// Select I want to
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);

			System.out.println("*** Adding the Alerts ***");	
			// Select Alerts link
			Utils.WebdriverWait(100, "Reservation.Alerts_Link", "clickable");
			Utils.jsClick("Reservation.Alerts_Link");
			logger.log(LogStatus.PASS, "Selected Alerts  Link");
			System.out.println("Selected Alerts  Link");		
			Utils.waitForSpinnerToDisappear(100);

			// Select New
			Utils.WebdriverWait(100, "Reservation.Alerts_New", "clickable");
			Utils.jsClick("Reservation.Alerts_New");
			logger.log(LogStatus.PASS, "Selected new  ");
			System.out.println("Selected new");	
			Utils.waitForSpinnerToDisappear(100);

			// Select Alerts LOV
			Utils.WebdriverWait(100, "Reservation.Alerts_Code_LOV", "clickable");
			Utils.jsClick("Reservation.Alerts_Code_LOV");
			logger.log(LogStatus.PASS, "Selected Alerts LOv  ");
			System.out.println("Selected Alerts LOv ");	
			Utils.waitForSpinnerToDisappear(100);		

			// Sending Keys to filter 
			Utils.WebdriverWait(100, "Reservation.Alerts_value", "presence");
			Utils.mouseHover("Reservation.Alerts_value");

			Utils.textBox("Reservation.Alerts_value", resvMapp.get("Alert_Name"));
			System.out.println("Sending keys to alert filters :" + resvMapp.get("Alert_Name"));
			logger.log(LogStatus.PASS, "Sending kaeys to alert filters: " + resvMapp.get("Alert_Name"));
			Utils.waitForSpinnerToDisappear(100);


			// searching the Alert
			Utils.WebdriverWait(100, "Reservation.Alerts_search", "clickable");
			Utils.jsClick("Reservation.Alerts_search");
			logger.log(LogStatus.PASS, "searching the Alert");
			System.out.println("searching the Alert");
			Utils.waitForSpinnerToDisappear(100);


			if (isExists("Reservation.Alerts_selectvalue"))				
			{

				// selecting the Alerts
				Utils.WebdriverWait(100, "Reservation.Alerts_selectvalue", "clickable");
				Utils.jsClick("Reservation.Alerts_selectvalue");
				logger.log(LogStatus.PASS, "selecting  on Alerts");
				System.out.println("selecting  the Alerts");
				Utils.waitForSpinnerToDisappear(100);

				// selecting the select button
				Utils.WebdriverWait(100, "Reservation.Alerts_select", "clickable");
				Utils.jsClick("Reservation.Alerts_select");
				logger.log(LogStatus.PASS, "clicked  on select");
				System.out.println("clicked  on select");
				Utils.waitForSpinnerToDisappear(100);

				// selecting the save
				Utils.WebdriverWait(100, "Reservation.Alerts_save", "clickable");
				Utils.jsClick("Reservation.Alerts_save");
				logger.log(LogStatus.PASS, "clicked  on save");
				System.out.println("clicked  on save");
				Utils.waitForSpinnerToDisappear(100);
			}
			else
			{

				// selecting the select button
				Utils.WebdriverWait(100, "Reservation.Alerts_cancel", "clickable");
				Utils.jsClick("Reservation.Alerts_cancel");
				logger.log(LogStatus.PASS, "Alert is not found - Please create the alert");
				System.out.println("Alert is not found - Please create the alert");
				logger.log(LogStatus.PASS, "clicked  on Cancel");
				System.out.println("clicked  on Cancel");
				Utils.waitForSpinnerToDisappear(100);

			}

			//close
			//click("Reservation.Alerts_Close", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "adding Alerts  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	///////////////////// deleting the Alerts
	public static void deletingAlerts(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Deleting the Alerts ***");	


			System.out.println("Deleting the Alerts for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Deleting the Alerts for the confirmation number:" + resvMapp.get("ConfirmationNum"));

			// Select I want to
			//			waitForPageLoad(100);
			//			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			//			Utils.jsClick("Reservation.Iwanttoclick");
			//			logger.log(LogStatus.PASS, "Selected I want to");
			//			System.out.println("Selected I want to");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//			// Select show all
			//
			//			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			//			Utils.jsClick("Reservation.showAllLink");
			//			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			//			System.out.println("Selected show all manage profile..");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//			// Select Alerts
			//			Utils.WebdriverWait(100, "Reservation.Alerts_Link", "clickable");
			//			Utils.jsClick("Reservation.Alerts_Link");
			//			logger.log(LogStatus.PASS, "Selected Alerts  Link");
			//			System.out.println("Selected Alerts  Link");		
			//			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.Alerts_Actions", "clickable");
			Utils.jsClick("Reservation.Alerts_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete
			Utils.WebdriverWait(100, "Reservation.Alerts_Delete", "clickable");
			Utils.jsClick("Reservation.Alerts_Delete");
			logger.log(LogStatus.PASS, "clicked  on delete action");
			System.out.println("clicked  on delete action");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete button
			Utils.WebdriverWait(100, "Reservation.Alerts_Deletebutton", "clickable");
			Utils.jsClick("Reservation.Alerts_Deletebutton");
			logger.log(LogStatus.PASS, "clicked  on delete button");
			System.out.println("clicked  on delete button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Reservation.Alerts_verify"))				
			{

				Utils.WebdriverWait(100, "Reservation.Alerts_verify", "presence");			
				System.out.println("Alerts are not deleted" );
				logger.log(LogStatus.FAIL, "Alerts are not deleted");

			}
			else
			{
				System.out.println("Alerts are deleted" );
				logger.log(LogStatus.PASS, "Alerts are deleted");
			}
			//close
			click("Reservation.Alerts_Close", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Deleting Alerts  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created:: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	///////////////////// updating the Alerts
	public static void updatingAlerts(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Upadting the Alerts ***");	

			System.out.println("Upadting the Alerts for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Upadting the Alerts for the confirmation number:" + resvMapp.get("ConfirmationNum"));

			//			// Select I want to
			//			waitForPageLoad(100);
			//			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			//			Utils.jsClick("Reservation.Iwanttoclick");
			//			logger.log(LogStatus.PASS, "Selected I want to");
			//			System.out.println("Selected I want to");		
			//			Utils.waitForSpinnerToDisappear(100);
			//			
			//			// Select show all
			//
			//			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			//			Utils.jsClick("Reservation.showAllLink");
			//			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			//			System.out.println("Selected show all manage profile..");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//
			//			// Select Alerts
			//			Utils.WebdriverWait(100, "Reservation.Alerts_Link", "clickable");
			//			Utils.jsClick("Reservation.Alerts_Link");
			//			logger.log(LogStatus.PASS, "Selected Alerts  Link");
			//			System.out.println("Selected Alerts  Link");		
			//			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.Alerts_Actions", "clickable");
			Utils.jsClick("Reservation.Alerts_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on edit
			Utils.WebdriverWait(100, "Reservation.Alert_Edit", "clickable");
			Utils.jsClick("Reservation.Alert_Edit");
			logger.log(LogStatus.PASS, "clicked  on Edit");
			System.out.println("clicked  on Edit");
			Utils.waitForSpinnerToDisappear(100);
			Thread.sleep(1000);
			String AlertText=getText("Reservation.Alerts_Text");
			System.out.println("Old Trace text:  " + AlertText);
			logger.log(LogStatus.PASS, "old Trace text:  " + AlertText);

			// updating the text of trace
			String EditAlertText = resvMapp.get("EditAlert_Text");

			Utils.WebdriverWait(100, "Reservation.Alerts_Text", "presence");
			Utils.mouseHover("Reservation.Alerts_Text");
			Utils.jsTextbox("Reservation.Alerts_Text", EditAlertText);
			System.out.println("Sending data to Trace text:  " + EditAlertText);

			logger.log(LogStatus.PASS, "Sending data to Alert text:  " + EditAlertText);

			// selecting the save button
			Utils.WebdriverWait(100, "Reservation.Alerts_save", "clickable");
			Utils.jsClick("Reservation.Alerts_save");
			logger.log(LogStatus.PASS, "clicked  on save");
			System.out.println("clicked  on save");
			Utils.waitForSpinnerToDisappear(100);


			if (isExists("Reservation.Alerts_Editverify"))				
			{
				String updtedText=getText("Reservation.Alerts_Editverify");
				Utils.WebdriverWait(100, "Reservation.Alerts_Editverify", "presence");	
				if(AlertText!=updtedText)	
				{
					System.out.println("updated the Alert text to: "+ updtedText);
					logger.log(LogStatus.PASS, "updated the Alerts text to: " +updtedText);
				}
				else
				{
					System.out.println("Alerts are not updated" );
					logger.log(LogStatus.FAIL, "alerts are not updated");
				}
				//click("Reservation.Alerts_Close", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
			}
			else
			{
				System.out.println("Alerts text is not avaliable" );
				logger.log(LogStatus.FAIL, "Alerts text is not avaliable");
			}

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Upadting Alerts  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created:: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	///////////////// adding the NOtes

	public static void addingNotes(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);


			// Select I want to

			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);



			System.out.println("*** Adding the Notes ***");	
			// Select Notes link
			Utils.WebdriverWait(100, "Reservation.Notes_link", "clickable");
			Utils.jsClick("Reservation.Notes_link");
			logger.log(LogStatus.PASS, "Selected Reservation Notes  Link");
			System.out.println("Selected Reservation Notes  Link");		
			Utils.waitForSpinnerToDisappear(100);

			// Select New
			Utils.WebdriverWait(100, "Reservation.Notes_New", "clickable");
			Utils.jsClick("Reservation.Notes_New");
			logger.log(LogStatus.PASS, "Selected new  ");
			System.out.println("Selected new");	
			Utils.waitForSpinnerToDisappear(100);

			// Select NOte LOV
			Utils.WebdriverWait(100, "Reservation.Notes_LOV", "clickable");
			Utils.jsClick("Reservation.Notes_LOV");
			logger.log(LogStatus.PASS, "Selected Notes LOv  ");
			System.out.println("Selected Notes LOv ");	
			Utils.waitForSpinnerToDisappear(100);		

			// Sending Keys to filter 
			Utils.WebdriverWait(100, "Reservation.Notes_value", "presence");
			Utils.mouseHover("Reservation.Notes_value");

			Utils.textBox("Reservation.Notes_value", resvMapp.get("Note_Name"));
			System.out.println("Sending keys to Note filters :" + resvMapp.get("Note_Name"));
			logger.log(LogStatus.PASS, "Sending keys to Note filters: " + resvMapp.get("Note_Name"));
			Utils.waitForSpinnerToDisappear(100);


			// searching the Alert
			Utils.WebdriverWait(100, "Reservation.Notes_search", "clickable");
			Utils.jsClick("Reservation.Notes_search");
			logger.log(LogStatus.PASS, "searching the Note group");
			System.out.println("searching the Note Group");
			Utils.waitForSpinnerToDisappear(100);


			if (isExists("Reservation.Notes_selectvalue"))				
			{

				// selecting the Group NOte
				Utils.WebdriverWait(100, "Reservation.Notes_selectvalue", "clickable");
				Utils.jsClick("Reservation.Notes_selectvalue");
				logger.log(LogStatus.PASS, "selecting the Group Note");
				System.out.println("selecting the Group Note");
				Utils.waitForSpinnerToDisappear(100);

				// selecting the select button
				Utils.WebdriverWait(100, "Reservation.Notes_select", "clickable");
				Utils.jsClick("Reservation.Notes_select");
				logger.log(LogStatus.PASS, "clicked  on select");
				System.out.println("clicked  on select");
				Utils.waitForSpinnerToDisappear(100);

				Utils.textBox("Reservation.Notes_Comment", resvMapp.get("Note_Comment"));
				System.out.println("Provided Note Comment :" + resvMapp.get("Note_Comment"));
				logger.log(LogStatus.PASS, "Provided Note Comment: " + resvMapp.get("Note_Comment"));
				Utils.waitForSpinnerToDisappear(100);

				// selecting the save
				Utils.WebdriverWait(100, "Reservation.Notes_save", "clickable");
				Utils.jsClick("Reservation.Notes_save");
				logger.log(LogStatus.PASS, "clicked  on save");
				System.out.println("clicked  on save");
				Utils.waitForSpinnerToDisappear(100);
			}
			else
			{

				// selecting the select button
				Utils.WebdriverWait(100, "Reservation.Notes_cancel", "clickable");
				Utils.jsClick("Reservation.Notes_cancel");
				logger.log(LogStatus.PASS, "Note Type is not found - Please create the Note Type");
				System.out.println("Note Type is not found - Please create the Note Type");
				logger.log(LogStatus.PASS, "clicked  on Cancel");
				System.out.println("clicked  on note type Cancel");
				Utils.jsClick("Reservation.Notes_cancel");
				System.out.println("clicked  on Resrvation Notes Cancel");
				Utils.waitForSpinnerToDisappear(100);

			}

			//close
			//click("Reservation.Notes_close", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding Reservation Notes  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	///////////////////// updating the Reservation nOtes
	public static void updatingNotes(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Upadting the Reservation Notes ***");	

			System.out.println("Updating the Reservation Notes for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Updating the Reservation Notes for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			// Select I want to
			//			waitForPageLoad(100);
			//			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			//			Utils.jsClick("Reservation.Iwanttoclick");
			//			logger.log(LogStatus.PASS, "Selected I want to");
			//			System.out.println("Selected I want to");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//			// Select show all
			//
			//			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			//			Utils.jsClick("Reservation.showAllLink");
			//			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			//			System.out.println("Selected show all manage profile..");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//
			//			// Select reservation Notes
			//			
			//			Utils.WebdriverWait(100, "Reservation.Notes_link", "clickable");
			//			Utils.jsClick("Reservation.Notes_link");
			//			logger.log(LogStatus.PASS, "Selected reservation notes  Link");
			//			System.out.println("Selected reservation notes  Link");		
			//			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.Notes_Actions", "clickable");
			Utils.jsClick("Reservation.Notes_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on edit
			Utils.WebdriverWait(100, "Reservation.Notes_Edit", "clickable");
			Utils.jsClick("Reservation.Notes_Edit");
			logger.log(LogStatus.PASS, "clicked  on Edit");
			System.out.println("clicked  on Edit");
			Utils.waitForSpinnerToDisappear(100);

			String NoteText=getText("Reservation.Notes_Comment");
			System.out.println("Old Reservation Note Comment:  " + NoteText);
			logger.log(LogStatus.PASS, "old Reservation Note Comment:  " + NoteText);

			// updating the text of trace
			String EditNoteText = resvMapp.get("Edit_Note_Comment");

			Utils.WebdriverWait(100, "Reservation.Notes_Comment", "presence");
			Utils.mouseHover("Reservation.Notes_Comment");
			Utils.jsTextbox("Reservation.Notes_Comment", EditNoteText);
			System.out.println("Sending data to Reservation notes  Comment:  " + EditNoteText);

			logger.log(LogStatus.PASS, "Sending data to Reservation note Comment:  " + EditNoteText);

			// selecting the save button
			Utils.WebdriverWait(100, "Reservation.Notes_save", "clickable");
			Utils.jsClick("Reservation.Notes_save");
			logger.log(LogStatus.PASS, "clicked  on save");
			System.out.println("clicked  on save");
			Utils.waitForSpinnerToDisappear(100);


			if (isExists("Reservation.Notes_Editverify"))				
			{
				String updtedText=getText("Reservation.Notes_Editverify");
				Utils.WebdriverWait(100, "Reservation.Notes_Editverify", "presence");	
				if(NoteText!=updtedText)	
				{
					System.out.println("updated the Reservation Note to: "+ updtedText);
					logger.log(LogStatus.PASS, "updated the Reservation Note to: " +updtedText);
				}
				else
				{
					System.out.println("Reservation Note is not updated" );
					logger.log(LogStatus.FAIL, "Reservation Note is not updated");
				}
				//				click("Reservation.Notes_close", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
			}
			else
			{
				System.out.println("Note type is not avaliable" );
				logger.log(LogStatus.PASS, "Note type  is not avaliable");
			}

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Upadting Reservation Notes  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}


	///////////////////// deleting the Reservation  Notes
	public static void deletingNotes(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Deleting the Reservation NOtes ***");	

			System.out.println("Deleting the Reservation Notes for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Deleting the Reservation Notes for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			//			// Select I want to
			//			waitForPageLoad(100);
			//			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			//			Utils.jsClick("Reservation.Iwanttoclick");
			//			logger.log(LogStatus.PASS, "Selected I want to");
			//			System.out.println("Selected I want to");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//			// Select show all
			//
			//			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			//			Utils.jsClick("Reservation.showAllLink");
			//			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			//			System.out.println("Selected show all manage profile..");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//			
			//			// Select Notes
			//			Utils.WebdriverWait(100, "Reservation.Notes_deletelink", "clickable");
			//			Utils.jsClick("Reservation.Notes_deletelink");
			//			logger.log(LogStatus.PASS, "Selected Reservation Notes  Link");
			//			System.out.println("Selected Reservation Notes  Link");		
			//			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.Notes_Actions", "clickable");
			Utils.jsClick("Reservation.Notes_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete
			Utils.WebdriverWait(100, "Reservation.Notes_Delete", "clickable");
			Utils.jsClick("Reservation.Notes_Delete");
			logger.log(LogStatus.PASS, "clicked  on delete action");
			System.out.println("clicked  on delete action");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete button
			Utils.WebdriverWait(100, "Reservation.Notes_Deletebutton", "clickable");
			Utils.jsClick("Reservation.Notes_Deletebutton");
			logger.log(LogStatus.PASS, "clicked  on delete button");
			System.out.println("clicked  on delete button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Reservation.Notes_Deleteverify"))				
			{

				Utils.WebdriverWait(100, "Reservation.Notes_Deleteverify", "presence");			
				System.out.println("Reservation Notes is not deleted" );
				logger.log(LogStatus.FAIL, "Reservation Notes is not deleted");

			}
			else
			{
				System.out.println("Reservation Notes is deleted" );
				logger.log(LogStatus.PASS, "Reservation Notes is deleted");
			}
			//close
			click("Reservation.Alerts_Close", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Deleting Reservation Notes  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}


	///////////////// adding the packages

	public static void addingPackages(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);


			// Select I want to

			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);



			System.out.println("*** Adding the Packages ***");	
			// Select Notes link
			Utils.WebdriverWait(100, "Reservation.packages_Link", "presence");
			Utils.jsClick("Reservation.packages_Link");
			logger.log(LogStatus.PASS, "Selected packages  Link");
			System.out.println("Selected packages  Link");		
			Utils.waitForSpinnerToDisappear(100);

			// Select New
			Utils.WebdriverWait(100, "Reservation.packages_New", "presence");
			Utils.jsClick("Reservation.packages_New");
			logger.log(LogStatus.PASS, "Selected new  ");
			System.out.println("Selected new");	
			Utils.waitForSpinnerToDisappear(100);

			// Sending Keys to filter 

			Utils.jsClick("Reservation.packages_Filter");
			Utils.jsTextbox("Reservation.packages_Filter", resvMapp.get("Package_Filter"));
			System.out.println("Sending keys to packages filters :" + resvMapp.get("Package_Filter"));
			logger.log(LogStatus.PASS, "Sending keys to packages filters: " + resvMapp.get("Package_Filter"));
			Utils.waitForSpinnerToDisappear(100);


			// searching the package
			Utils.WebdriverWait(100, "Reservation.packages_Search", "presence");
			Utils.jsClick("Reservation.packages_Search");
			logger.log(LogStatus.PASS, "searching the package");
			System.out.println("searching the  package");
			Utils.waitForSpinnerToDisappear(100);


			if (isExists("Reservation.packages_Selectvalue"))				
			{

				// selecting the package
				Utils.WebdriverWait(100, "Reservation.packages_Selectvalue", "presence");
				Utils.jsClick("Reservation.packages_Selectvalue");
				logger.log(LogStatus.PASS, "selecting the package");
				System.out.println("selecting the Package");
				Utils.waitForSpinnerToDisappear(100);

				// selecting the Add button
				Utils.WebdriverWait(100, "Reservation.packages_Add", "presence");
				Utils.jsClick("Reservation.packages_Add");
				logger.log(LogStatus.PASS, "clicked  on Add");
				System.out.println("clicked  on Add");
				Utils.waitForSpinnerToDisappear(100);


				// selecting the save button
				Utils.WebdriverWait(100, "Reservation.packages_save", "presence");
				Utils.jsClick("Reservation.packages_save");
				logger.log(LogStatus.PASS, "clicked  on save");
				System.out.println("clicked  on Save");
				Utils.waitForSpinnerToDisappear(100);

				if (isExists("Reservation.packages_verify"))				
				{
					System.out.println("Created package:" + resvMapp.get("Package_Filter"));
					logger.log(LogStatus.PASS, "Created Package: " + resvMapp.get("Package_Filter"));
				}
				else
				{
					logger.log(LogStatus.FAIL, "Package is not created");
					System.out.println("Package is not created");
				}
			}
			else
			{

				// selecting the cancel button
				Utils.WebdriverWait(100, "Reservation.packages_Cancel", "presence");
				Utils.jsClick("Reservation.packages_Cancel");
				logger.log(LogStatus.PASS, "Package is not found - Please create the package in admin");
				System.out.println("Package is not found - Please create the packege in admin");
				logger.log(LogStatus.PASS, "clicked  on Cancel");
				System.out.println("clicked  on  Cancel");
				Utils.waitForSpinnerToDisappear(100);

			}

			//close
			//			click("Reservation.packages_close", 100, "clickable");
			//			System.out.println("clicked  on packages close");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding packages  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	///////////////////// updating the Packages
	public static void updatingPackages(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Upadting the packages ***");	
			System.out.println("Upadting the packages for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Upadting the packages  for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			// Select I want to
			//
			//			waitForPageLoad(100);
			//			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			//			Utils.mouseHover("Reservation.Iwanttoclick");
			//			Utils.jsClick("Reservation.Iwanttoclick");
			//			logger.log(LogStatus.PASS, "Selected I want to");
			//			System.out.println("Selected I want to");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//			// Select show all
			//			
			//			Utils.WebdriverWait(100, "Reservation.showAllLink", "presence");
			//			Utils.jsClick("Reservation.showAllLink");
			//			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			//			System.out.println("Selected show all manage profile..");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//
			//			// Select packages
			//
			//			Utils.WebdriverWait(100, "Reservation.packages_Link", "presence");
			//			Utils.jsClick("Reservation.packages_Link");
			//			logger.log(LogStatus.PASS, "Selected packages Link");
			//			System.out.println("Selected packages Link");		
			//			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.packages_Actions", "presence");
			Utils.jsClick("Reservation.packages_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on edit
			Utils.WebdriverWait(100, "Reservation.packages_Edit", "presence");
			Utils.jsClick("Reservation.packages_Edit");
			logger.log(LogStatus.PASS, "clicked  on Edit");
			System.out.println("clicked  on Edit");
			Utils.waitForSpinnerToDisappear(100);


			// clicking on excluded Quantity

			Utils.WebdriverWait(100, "Reservation.packages_Quantity", "presence");
			Utils.mouseHover("Reservation.packages_Quantity");

			Utils.textBox("Reservation.packages_Quantity", resvMapp.get("Package_Excluded_Quantity"));
			System.out.println("Updating the packages excluded quantity to:" + resvMapp.get("Package_Excluded_Quantity"));
			logger.log(LogStatus.PASS, "Updating the packages excluded quantity to: " + resvMapp.get("Package_Excluded_Quantity"));
			Utils.waitForSpinnerToDisappear(100);


			// selecting the save button
			Utils.WebdriverWait(100, "Reservation.packages_save", "presence");
			Utils.jsClick("Reservation.packages_save");
			logger.log(LogStatus.PASS, "clicked  on save");
			System.out.println("clicked  on save");
			Utils.waitForSpinnerToDisappear(100);

			//close
			//			click("Reservation.packages_closing", 100, "clickable");
			//			System.out.println("clicked  on packages close");


		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Upadting Packages  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	///////////////////// deleting the packages
	public static void deletingPackages(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Deleting the packages ***");	
			System.out.println("Deleting the packages for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Deleting the packages  for the confirmation number:" + resvMapp.get("ConfirmationNum"));

			// Select I want to

			//			waitForPageLoad(100);
			//			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			//			Utils.mouseHover("Reservation.Iwanttoclick");
			//			Utils.jsClick("Reservation.Iwanttoclick");
			//			logger.log(LogStatus.PASS, "Selected I want to");
			//			System.out.println("Selected I want to");		
			//			Utils.waitForSpinnerToDisappear(100);
			//			
			//			// Select show all
			//			
			//			Utils.WebdriverWait(100, "Reservation.showAllLink", "presence");
			//			Utils.jsClick("Reservation.showAllLink");
			//			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			//			System.out.println("Selected show all manage profile..");		
			//			Utils.waitForSpinnerToDisappear(100);
			//
			//
			//			// Select packages
			//			Utils.WebdriverWait(100, "Reservation.packages_Link", "presence");
			//			Utils.jsClick("Reservation.packages_Link");
			//			logger.log(LogStatus.PASS, "Selected packages  Link");
			//			System.out.println("Selected packages  Link");		
			//			Utils.waitForSpinnerToDisappear(100);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.packages_Actions", "presence");
			Utils.jsClick("Reservation.packages_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete
			Utils.WebdriverWait(100, "Reservation.packages_delete", "presence");
			Utils.jsClick("Reservation.packages_delete");
			logger.log(LogStatus.PASS, "clicked  on delete action");
			System.out.println("clicked  on delete action");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete button
			Utils.WebdriverWait(100, "Reservation.packages_deletebutton", "presence");
			Utils.jsClick("Reservation.packages_deletebutton");
			logger.log(LogStatus.PASS, "clicked  on delete button");
			System.out.println("clicked  on delete button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Reservation.packages_verify"))				
			{

				Utils.WebdriverWait(100, "Reservation.packages_verify", "presence");			
				System.out.println("Package is not deleted" );
				logger.log(LogStatus.FAIL, "Package is not deleted");

			}
			else
			{
				System.out.println("Package is deleted" );
				logger.log(LogStatus.PASS, "Package is deleted");
			}
			//close
			click("Reservation.packages_closing", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Deleting packages  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	///////////////////// updating the stay details
	public static void updatingStaydetails(HashMap<String, String> resvMapp) throws Exception {


		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);

			System.out.println("*** Upadting the Stay details ***");	


			// Select I want to
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);


			// Select stay details

			Utils.WebdriverWait(100, "Reservation.staydetails_Link", "clickable");
			Utils.jsClick("Reservation.staydetails_Link");
			logger.log(LogStatus.PASS, "Selected stay details Link");
			System.out.println("Selected stay details Link");		
			Utils.waitForSpinnerToDisappear(100);

			// getting the nights text

			String nights= Utils.getText("Reservation.staydetails_nightvalue");
			logger.log(LogStatus.PASS, "Nights value "+nights);
			System.out.println("Nights value :"+nights);	


			// getting the Adults text

			String adults= Utils.getText("Reservation.staydetails_adultvalue");
			logger.log(LogStatus.PASS, "adults value :"+adults);
			System.out.println("adults value :"+adults);	


			// getting the child text

			//			String child= Utils.getText("Reservation.staydetails_numchild");
			//			logger.log(LogStatus.PASS, "child value :"+child);
			//			System.out.println("child value :"+child);	


			// clicking on edit
			Utils.WebdriverWait(100, "Reservation.staydetails_Edit", "presence");
			Utils.jsClick("Reservation.staydetails_Edit");
			logger.log(LogStatus.PASS, "clicked  on Edit");
			System.out.println("clicked  on Edit");
			Utils.waitForSpinnerToDisappear(100);


			// clicking on nights

			Utils.WebdriverWait(200, "Reservation.staydetails_nights", "presence");
			Utils.jsClick("Reservation.staydetails_nights");
			logger.log(LogStatus.PASS, "Clicking on + button of nights");
			System.out.println("Clicking on + button of nights");


			// clicking on Adults

			Utils.WebdriverWait(200, "Reservation.staydetails_adults", "presence");
			Utils.jsClick("Reservation.staydetails_adults");
			logger.log(LogStatus.PASS, "Clicking on + button of adults");
			System.out.println("Clicking on + button of adults");

			//			// clicking on child
			//
			//			Utils.WebdriverWait(200, "Reservation.staydetails_child", "presence");
			//			Utils.jsClick("Reservation.staydetails_child");
			//			logger.log(LogStatus.PASS, "Clicking on + button of child");
			//			System.out.println("Clicking on + button of child");
			//			
			//			waitForPageLoad(100);
			//			Utils.waitForSpinnerToDisappear(100);
			//			Thread.sleep(3000);
			//			
			//			Utils.textBox("Reservation.staydetails_childage", resvMapp.get("Child_age"));
			//			System.out.println("Updating the packages excluded quantity to:" + resvMapp.get("Child_age"));
			//			logger.log(LogStatus.PASS, "Updating the packages excluded quantity to: " + resvMapp.get("Child_age"));
			//			Utils.waitForSpinnerToDisappear(100);


			// selecting the save button
			Utils.WebdriverWait(100, "Reservation.staydetails_save", "presence");
			Utils.jsClick("Reservation.staydetails_save");
			logger.log(LogStatus.PASS, "clicked  on save");
			System.out.println("clicked  on save");
			Utils.waitForSpinnerToDisappear(100);

			// getting the nights text

			String editnights= Utils.getText("Reservation.staydetails_nightvalue");
			logger.log(LogStatus.PASS, "Nights value "+nights);
			System.out.println("Nights value :"+nights);	
			if(nights!=editnights)
			{
				System.out.println("updated the Nights to: "+ editnights);
				logger.log(LogStatus.PASS, "updated the Nights to: " +editnights);
			}
			else
			{
				System.out.println("Nights is not updated" );
				logger.log(LogStatus.PASS, "Nights is not updated");
			}


			// getting the Adults text

			String editadults= Utils.getText("Reservation.staydetails_adultvalue");
			logger.log(LogStatus.PASS, "adults value :"+adults);
			System.out.println("adults value :"+adults);	
			if(nights!=editnights)
			{
				System.out.println("updated the Adults to: "+ editadults);
				logger.log(LogStatus.PASS, "updated the Adults to: " +editadults);
			}
			else
			{
				System.out.println("Adults is not updated" );
				logger.log(LogStatus.PASS, "Adults is not updated");
			}

			// getting the child text

			//			String editchild= Utils.getText("Reservation.staydetails_numchild");
			//			logger.log(LogStatus.PASS, "child value :"+child);
			//			System.out.println("child value :"+child);	
			//			if(nights!=editnights)
			//			{
			//				System.out.println("updated the Child to: "+ editchild);
			//				logger.log(LogStatus.PASS, "updated the Child to: " +editchild);
			//			}
			//			else
			//			{
			//				System.out.println("Child is not updated" );
			//				logger.log(LogStatus.PASS, "Child is not updated");
			//			}

			//close
			click("Reservation.staydetails_close", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Upadting stay details  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	///////////////// adding the confirmation letter

	public static void addingConfirmationletter(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);




			// Select I want to
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);


			System.out.println("*** Adding the confirmation letter ***");	
			// Select confirmation letter
			Utils.WebdriverWait(100, "Reservation.confirmation_link", "clickable");
			Utils.jsClick("Reservation.confirmation_link");
			logger.log(LogStatus.PASS, "Selected confirmation letter  Link");
			System.out.println("Selected confirmation letter  Link");		
			Utils.waitForSpinnerToDisappear(100);

			// Select Email
			Utils.WebdriverWait(100, "Reservation.confirmation_Email", "clickable");
			Utils.jsClick("Reservation.confirmation_Email");
			logger.log(LogStatus.PASS, "Selected Email  ");
			System.out.println("Selected Email ");	
			Utils.waitForSpinnerToDisappear(100);		

			// Selecting confirmation drop down
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.confirmation_letter_dropdwn")).perform();
			Utils.selectBy("Reservation.confirmation_letter_dropdwn", "text", resvMapp.get("Confirm_Letter"));
			System.out.println("confirmation letter is Selected");
			logger.log(LogStatus.PASS, "selected " + resvMapp.get("Confirm_Letter"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			// searching the save
			Utils.WebdriverWait(100, "Reservation.confirmation_save", "clickable");
			Utils.jsClick("Reservation.confirmation_save");
			logger.log(LogStatus.PASS, "clicked on save");
			System.out.println("clicked on save");
			Utils.waitForSpinnerToDisappear(100);

			// selecting the actions
			Utils.WebdriverWait(100, "Reservation.confirmation_actions", "clickable");
			Utils.jsClick("Reservation.confirmation_actions");
			logger.log(LogStatus.PASS, "selecting  on actions");
			System.out.println("selecting  the more");
			Utils.waitForSpinnerToDisappear(100);

			// selecting the select preview
			Utils.WebdriverWait(100, "Reservation.confirmation_preview", "clickable");
			Utils.jsClick("Reservation.confirmation_preview");
			logger.log(LogStatus.PASS, "clicked  on preview");
			System.out.println("clicked  on preview");
			Utils.waitForSpinnerToDisappear(100);

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


		}
		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding Confirmation letter :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}


	///////////////// updating the confirmation letter

	public static void updatingConfirmationletter(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** updating confirmation letter ***");
			System.out.println("Upadting the confirmation letter for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Upadting the confirmation letter for the confirmation number:" + resvMapp.get("ConfirmationNum"));


			// selecting the actions
			Utils.WebdriverWait(100, "Reservation.confirmation_actions", "clickable");
			Utils.jsClick("Reservation.confirmation_actions");
			logger.log(LogStatus.PASS, "selecting  on actions");
			System.out.println("selecting  the more");
			Utils.waitForSpinnerToDisappear(100);

			// selecting the edit
			Utils.WebdriverWait(100, "Reservation.confirmation_edit", "clickable");
			Utils.jsClick("Reservation.confirmation_edit");
			logger.log(LogStatus.PASS, "selecting  on edit");
			Utils.waitForSpinnerToDisappear(100);


			String status= Utils.getText("Reservation.confirmation_letter_dropdwn");
			logger.log(LogStatus.PASS, "old : "+status);
			System.out.println("old :"+status);


			// Selecting confirmation drop down
			waitForPageLoad(100);
			new Actions(driver).moveToElement(Utils.element("Reservation.confirmation_letter_dropdwn")).perform();
			Utils.selectBy("Reservation.confirmation_letter_dropdwn", "text", resvMapp.get("Edit_Confirm_Letter"));
			System.out.println("confirmation letter is Selected as :"  + resvMapp.get("Edit_Confirm_Letter"));
			logger.log(LogStatus.PASS, "selected " + resvMapp.get("Edit_Confirm_Letter"));
			// Thread.sleep(2000);
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			String editstatus= Utils.getText("Reservation.confirmation_letter_dropdwn");
			logger.log(LogStatus.PASS, "updated to :"+editstatus);
			System.out.println("updated to:"+editstatus);

			// searching the save
			Utils.WebdriverWait(100, "Reservation.confirmation_save", "clickable");
			Utils.jsClick("Reservation.confirmation_save");
			logger.log(LogStatus.PASS, "clicked on save");
			System.out.println("clicked on save");
			Utils.waitForSpinnerToDisappear(100);

			// selecting the actions
			Utils.WebdriverWait(100, "Reservation.confirmation_actions", "clickable");
			Utils.jsClick("Reservation.confirmation_actions");
			logger.log(LogStatus.PASS, "selecting  on actions");
			System.out.println("selecting  the more");
			Utils.waitForSpinnerToDisappear(100);

			// selecting the select preview
			Utils.WebdriverWait(100, "Reservation.confirmation_preview", "clickable");
			Utils.jsClick("Reservation.confirmation_preview");
			logger.log(LogStatus.PASS, "clicked  on preview");
			System.out.println("clicked  on preview");
			Utils.waitForSpinnerToDisappear(100);

			
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


		}
		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "updating Confirmation letter :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}
	///////////////////////adding Item Inventory
	public static void addingItemInventory(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			Utils.takeScreenshot(driver, methodName);

			String conf = resvMapp.get("ConfirmationNum");
			ReservationPage.reservationSearch(conf);




			// Select I want to
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Reservation.Iwanttoclick", "clickable");
			Utils.jsClick("Reservation.Iwanttoclick");
			logger.log(LogStatus.PASS, "Selected I want to");
			System.out.println("Selected I want to");		
			Utils.waitForSpinnerToDisappear(100);

			// Select show all

			Utils.WebdriverWait(100, "Reservation.showAllLink", "clickable");
			Utils.jsClick("Reservation.showAllLink");
			logger.log(LogStatus.PASS, "Selected show all Link in  manage Page");
			System.out.println("Selected show all manage profile..");		
			Utils.waitForSpinnerToDisappear(100);

			// Select item inventory link

			Utils.WebdriverWait(100, "Reservation.ItemInventory_Link", "clickable");
			Utils.jsClick("Reservation.ItemInventory_Link");
			logger.log(LogStatus.PASS, "Selected item inventory Link  ");
			System.out.println("Selected item inventory Link");		
			Utils.waitForSpinnerToDisappear(100);

			// Select item inventory new

			Utils.WebdriverWait(100, "Reservation.ItemInventory_New", "clickable");
			Utils.jsClick("Reservation.ItemInventory_New");
			logger.log(LogStatus.PASS, "Selected new  ");
			System.out.println("Selected new");		
			Utils.waitForSpinnerToDisappear(100);

			// entered item inventory code

			Utils.textBox("Reservation.ItemInventory_code", resvMapp.get("Item_code"));
			System.out.println("Entering the item code:" + resvMapp.get("Item_code"));
			logger.log(LogStatus.PASS, "Entering the item code: " + resvMapp.get("Item_code"));
			Utils.waitForSpinnerToDisappear(100);

			// Select item inventory SERACH

			Utils.WebdriverWait(100, "Reservation.ItemInventory_search", "clickable");
			Utils.jsClick("Reservation.ItemInventory_search");
			logger.log(LogStatus.PASS, "Selected search  ");
			System.out.println("Selected search");		
			Utils.waitForSpinnerToDisappear(100);

			// Select item inventory select value
			if(isExists("Reservation.ItemInventory_selectvalue"))
			{
				Utils.WebdriverWait(100, "Reservation.ItemInventory_selectvalue", "clickable");
				Utils.jsClick("Reservation.ItemInventory_selectvalue");
				logger.log(LogStatus.PASS, "Selected search  ");
				System.out.println("Selected search");		
				Utils.waitForSpinnerToDisappear(100);

				// Select item inventory add

				Utils.WebdriverWait(100, "Reservation.ItemInventory_add", "clickable");
				Utils.jsClick("Reservation.ItemInventory_add");
				logger.log(LogStatus.PASS, "Selected add  ");
				System.out.println("Selected add");		
				Utils.waitForSpinnerToDisappear(100);

				// Select item inventory save

				Utils.WebdriverWait(100, "Reservation.ItemInventory_save", "clickable");
				Utils.jsClick("Reservation.ItemInventory_save");
				logger.log(LogStatus.PASS, "Selected save  ");
				System.out.println("selected save");		
				Utils.waitForSpinnerToDisappear(100);

				if(isExists("Reservation.ItemInventory_verify"))
				{

					Utils.WebdriverWait(100, "Reservation.ItemInventory_verify", "clickable");
					logger.log(LogStatus.PASS, "item inventory is created ");
					System.out.println("item inventory is created");

				}
				else
				{
					logger.log(LogStatus.FAIL, "item inventory is not created ");
					System.out.println("item inventory is not created");
				}
			}
			else
			{
				logger.log(LogStatus.FAIL, "Item code is not found-- please cretae the Item code ");
				System.out.println("Item code is not found-- please cretae the Item code ");	
			}
		}
		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding Item inventory :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created:: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}


	///////////////////// deleting Item Inventory
	public static void deletingItemInventory(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** Deleting the Item Inventory ***");	

			System.out.println("Deleting the Item Inventory  for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "Deleting the Item Inventory  for the confirmation number:" + resvMapp.get("ConfirmationNum"));


			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.ItemInventory_Actions", "presence");
			Utils.jsClick("Reservation.ItemInventory_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete
			Utils.WebdriverWait(100, "Reservation.ItemInventory_delete", "presence");
			Utils.jsClick("Reservation.ItemInventory_delete");
			logger.log(LogStatus.PASS, "clicked  on delete action");
			System.out.println("clicked  on delete action");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on delete button
			Utils.WebdriverWait(100, "Reservation.ItemInventory_deletebutton", "presence");
			Utils.jsClick("Reservation.ItemInventory_deletebutton");
			logger.log(LogStatus.PASS, "clicked  on delete button");
			System.out.println("clicked  on delete button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Reservation.ItemInventory_verify"))				
			{

				Utils.WebdriverWait(100, "Reservation.ItemInventory_verify", "presence");			
				System.out.println("Item Inventory is not deleted" );
				logger.log(LogStatus.FAIL, "Item Inventory is not deleted");

			}
			else
			{
				System.out.println("Item Inventory is deleted" );
				logger.log(LogStatus.PASS, "Item Inventory is deleted");
			}
			//close
			click("Reservation.ItemInventory_close", 100, "clickable");

		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Deleting Item Inventory  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created:: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	///////////////////// updating Item Inventory
	public static void updatingItemInventory(HashMap<String, String> resvMapp) throws Exception {

		// String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try 
		{
			System.out.println("*** updating the Item Inventory ***");	

			System.out.println("updating the Item Inventory  for the confirmation number:" + resvMapp.get("ConfirmationNum"));
			logger.log(LogStatus.PASS, "updating the Item Inventory  for the confirmation number:" + resvMapp.get("ConfirmationNum"));


			String Quantity= Utils.getText("Reservation.ItemInventory_quantityvalue");
			logger.log(LogStatus.PASS, "Quantity value "+Quantity);
			System.out.println("Quantity value :"+Quantity);

			// clicking on actions
			Utils.WebdriverWait(100, "Reservation.ItemInventory_Actions", "presence");
			Utils.jsClick("Reservation.ItemInventory_Actions");
			logger.log(LogStatus.PASS, "clicked  on Actions");
			System.out.println("clicked  on Actions");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on Edit
			Utils.WebdriverWait(100, "Reservation.ItemInventory_Edit", "presence");
			Utils.jsClick("Reservation.ItemInventory_Edit");
			logger.log(LogStatus.PASS, "clicked  on edit action");
			System.out.println("clicked  on edit action");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on quantity
			Utils.WebdriverWait(100, "Reservation.ItemInventory_quantity", "presence");
			Utils.jsClick("Reservation.ItemInventory_quantity");
			logger.log(LogStatus.PASS, "clicked  on + button of Quantity ");
			System.out.println("clicked on + button of Quantity");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on save
			Utils.WebdriverWait(100, "Reservation.ItemInventory_save", "presence");
			Utils.jsClick("Reservation.ItemInventory_save");
			logger.log(LogStatus.PASS, "clicked  on save");
			System.out.println("clicked  on save");
			Utils.waitForSpinnerToDisappear(100);

			String editQuantity= Utils.getText("Reservation.ItemInventory_quantityvalue");
			logger.log(LogStatus.PASS, "Quantity value "+editQuantity);
			System.out.println("Quantity value :"+editQuantity);

			if(Quantity!=editQuantity)
			{
				System.out.println("Item Inventory Quantity is updated to:" +editQuantity);
				logger.log(LogStatus.FAIL, "Item Inventory Quantity is updated to:" +editQuantity);

			}
			else
			{
				System.out.println("Item Inventory is not updated " );
				logger.log(LogStatus.FAIL, "Item Inventory is not updated" );
			}


		}

		catch (Exception e) 
		{
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Deleting Item Inventory  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}

	}

	
}
