package com.oracle.hgbu.opera.qaauto.ui.rsv.testcases.reservation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.crm.component.profile.ProfilePage;
import com.oracle.hgbu.opera.qaauto.ui.fof.component.checkin.CheckinPage;
import com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation.ReservationPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class Reservation extends Utils {
	
	/*******************************************************************
	- Description: Creates a Reservation from LTB Screen
	- Input: Profile, Source, Market Codes and Reservation Type, Payment Method
	- Output: Confirmation Num
	- Author: mahesh
	- Date: 6/12/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 1,groups = {"BAT"},alwaysRun = true)
	public void createReservation() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		 
	    System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create a new Reservation </b>");
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
			Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}
	
	
	/*******************************************************************
    -  Description: Search for a Reservation Using Advance Search
    - Input: Confirmation Num
    - Output: Confirmation Num
    - Author: mahesh
    - Date: 6/12/2018
    - Revision History:
                    - Change Date
                    - Change Reason
                    - Changed Behavior
                    - Last Changed By
//    ********************************************************************/
    @Test(priority = 2,groups = {"BAT"},alwaysRun = true) // , enabled=false , groups = { "bat" }
    public void reservationAdvanceSearch() throws Exception {

        String methodName = Utils.getMethodName();
        String testName = Utils.getClassName() + "@" + Utils.getMethodName();
        
        System.out.println("methodName: "+methodName);
        try {

            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.INFO, "<b> Verify able Search a reservation using advance search </b>");

            String ConfNum = ExcelUtils.getCellData(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation", "ConfirmationNum");
            System.out.println("ConfNum: "+ConfNum);
            ReservationPage.reservationAdvanceSearch(ConfNum);
            Thread.sleep(3000);
            
            Utils.tearDown();

        } catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Reservation Search not done :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
            throw (e);
        }

    }

	/*******************************************************************
	-  Description: Search for a Reservation Using Basic Search
	- Input: Confirmation Num
	- Output: Confirmation Num
	- Author: mahesh
	- Date: 6/12/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 3,groups = {"BAT"},alwaysRun = true) // , enabled=false
	public void reservationSuperSearch() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able Search a reservation using search </b>");

			String ConfNum = ExcelUtils.getCellData(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation", "ConfirmationNum");
			ReservationPage.reservationSuperSearch(ConfNum);
			Thread.sleep(3000);
			
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation Search not done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}

	}
	
	/*******************************************************************
	-  Description: Update a Reservation Using Basic Search
	- Input: Confirmation Num
	- Output: Confirmation Num
	- Author: mahesh
	- Date: 6/12/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 4,groups = {"BAT"},alwaysRun = true)
	public void reservationUpdate() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Update a Reservation </b>");
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			//ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			LoginPage.homePage();
			
			//String ConfNum = ExcelUtils.getCellData(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation", "ConfirmationNum");
			ReservationPage.reservationUpdate(ConfirmationNum);
			
			Utils.tearDown();
			Thread.sleep(3000);
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not updated :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	/*******************************************************************
	- Description: Cancels a Reservation from LTB Screen
	- Input: reservation Confirmation num
	- Output: Confirmation Num
	- Author: mahesh
	- Date: 24/12/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 5,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void cancelReservation() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Cancel a Reservation </b>");
			
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			//ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;//ExcelUtils.getCellData(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation", "ConfirmationNum");
			HashMap<String, String> cancelResvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "cancelReservation");
			cancelResvMap.put("ConfirmationNum", ConfNum);
			ReservationPage.cancelReservation(cancelResvMap);
			Thread.sleep(3000);
			
			//Logout from Application
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation Cancel not Done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	/*******************************************************************
	- Description: Copy a Reservation from LTB Screen
	- Input: reservation Confirmation num
	- Output: Confirmation Num
	- Author: Tulasi
	- Date: 05/02/2018
	
	********************************************************************/
	@Test(priority = 6,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void copyReservation() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Copy a Reservation </b>");
			
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ExcelUtils.getCellData(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation", "ConfirmationNum");
			HashMap<String, String> copyResvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "copyReservation");
			copyResvMap.put("ConfirmationNum", ConfNum);
			ReservationPage.copyReservation(copyResvMap);
			Thread.sleep(3000);
			
			//Logout from Application
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "copy reservation not Done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	/*******************************************************************
	- Description: Copy a Reservation from LTB Screen
	- Input: Profile
	- Output: Confirmation Num
	- Author: mahesh
	- Date: 24/12/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 7,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void createReservationfromProfile() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create a Reservation from Profile Screen </b>");
			
			//ProfilePage.createGuestProfileUsingSave();
			HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createGuestProfile");
            ProfilePage.createGuestProfile(profileMap, "Save");

            HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			Thread.sleep(3000);
			
			//Logout from Application
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation Cancel not Done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
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
	********************************************************************/
	@Test(priority = 8,groups = {"SANITY"},alwaysRun = true) // , enabled=false , groups = { "bat" }
	public void reservationPaymentMethod() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able Search a reservation using advance search </b>");
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "reservationPaymentMethod");

			String ConfNum = resvMap.get("ConfirmationNum");
			String PayMethod = resvMap.get("PaymentMethod");
			String WinNumber= resvMap.get("WindowNumber");
			
			//ReservationPage.reservationPaymentMethod(PayMethod,WinNumber,ConfNum);			
			
			Thread.sleep(3000);
			
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation Search not done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}

	}
	
	/*******************************************************************
	- Description: Creates a Reservation from From I want to and split reservation
	- Input: Profile, Source, Market Codes and Reservation Type, Payment Method
	- Output: Confirmation Num
	- Author: Dilip
	- Date: 01/24/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(priority = 9,groups = {"SANITY"},alwaysRun = true)
	public void createReservationFromIWantToAndSplitReservation() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();

		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create a new Reservation from I WANT TO</b>");

			// Navigating to Booking menu
			click("Reservation.booking_menu", 100, "clickable");

			// Navigating to Reservations menu
			click("Reservation.reservation_menu", 100, "clickable");

			// Navigating to Manage Reservation
			click("Reservation.manage_reservation", 100, "clickable");

			// Click on I want to link
			waitForSpinnerToDisappear(50);
			click("Reservation.manage_reservation_IWantto", 100, "clickable");

			// Click on Reservations link
			waitForSpinnerToDisappear(50);
			click("Reservation.create_new_resn", 100, "clickable");

			// Click on New Reservations link
			waitForSpinnerToDisappear(50);
			
			if (isExists("Reservation.New_Reservation_Button")){
				click("Reservation.New_Reservation_Button", 100, "clickable");
			}

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservation(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
			ReservationPage.splitReservation(resvMap);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation split :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}


	/*******************************************************************
	- Description: Link and unlink reservation
	- Input: Profile, Source, Market Codes and Reservation Type, Payment Method
	- Output: Confirmation Num
	- Author: Dilip
	- Date: 01/24/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(priority = 10,groups = {"SANITY"},alwaysRun = true)
	public void linkAndUnlinkReservation() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();

		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create a new Reservation from I WANT TO</b>");

			// Navigating to Booking menu
			click("Reservation.booking_menu", 100, "clickable");

			// Navigating to Reservations menu
			click("Reservation.reservation_menu", 100, "clickable");

			// Navigating to Manage Reservation
			click("Reservation.manage_reservation", 100, "clickable");

			// Click on I want to link
			waitForSpinnerToDisappear(50);
			click("Reservation.manage_reservation_IWantto", 100, "clickable");

			// Click on Reservations link
			waitForSpinnerToDisappear(50);
			click("Reservation.create_new_resn", 100, "clickable");

			// Click on New Reservations link
			waitForSpinnerToDisappear(50);
			if (isExists("Reservation.New_Reservation_Button")) {
				click("Reservation.New_Reservation_Button", 100, "clickable");
			}

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservation(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			HashMap<String, String> linkResvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "linkAndUnlinkReservation");
			ReservationPage.linkReservation(linkResvMap);
			ReservationPage.unlinkReservation(linkResvMap);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "link and unlink reservation:: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}

	/*******************************************************************
	-  Description: Extending the stay dates for a Reservation 
	- Input: Stay Dates, Window Number, Confirmation Number
	- Output: 
	- Author:Tulasi
	- Date: 5/2/2019
	
	********************************************************************/
	
	@Test(priority = 11,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void stayDates() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to extend stay dates a Reservation </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> staydatesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "stayDates");
			staydatesMap.put("ConfirmationNum", ConfNum);
			ReservationPage.stayDates(staydatesMap);
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Extend stay dates is not Done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	////////////////////////////////////////
	/*******************************************************************
	-  Description: reinstate a Reservation for a cancelled Reservation 
	- Input: cancel reason, Confirmation Number
	- Output: 
	- Author:Tulasi
	- Date: 5/2/2019
	
	********************************************************************/
	@Test(priority = 12,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void reinstateReservation() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Reinstate a Reservation </b>");
			
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> cancelResvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "cancelReservation");
			cancelResvMap.put("ConfirmationNum", ConfNum);
			ReservationPage.cancelReservation(cancelResvMap);
			
			jsClick("Reservation.header_ReservationCancel_popupclose");
			System.out.println("Cancel reservation popup is closed");
			
				
			ReservationPage.reinstateReservation(ConfirmationNum);
			
			
			//Thread.sleep(3000);
			
			//Logout from Application
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reinstate  Reservation  not Done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	//////////////////////////////////////////////////////////////////// adding packages
	/*******************************************************************
	-  Description: Adding packages while creating a Reservation 
	- Input: package code, Confirmation Number
	- Output: 
	- Author:Tulasi
	- Date: 6/2/2019
	
	********************************************************************/
	@Test(priority = 13,groups = {"SANITY"},alwaysRun = true)
	public void createPackageFromLTB() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		 
	    System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to add the packages </b>");
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "addingpackage");
			String ConfirmationNum = ReservationPage.createPackageFromLTB(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "addingpackage","ConfirmationNum", ConfirmationNum);
			Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}
	/*******************************************************************
	-  Description: Extending the stay details for a Reservation 
	- Input: stay dates,Confirmation Number
	- Output: 
	- Author:Tulasi
	- Date: 7/2/2019
	
	********************************************************************/
	@Test(priority = 14,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void extendstayDetails() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to extend stay deatils a Reservation </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> staydatesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "extendstayDetails");
			staydatesMap.put("ConfirmationNum", ConfNum);
			ReservationPage.extendstayDetails(staydatesMap);
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Extend stay dates is not Done :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(priority = 15,groups = {"SANITY"},alwaysRun = true)
	public void checkInFromReservationPresentation() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();

		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Checkin a Reservation from Reservation presentation screen </b>");

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			
			LoginPage.homePage();
			//ReservationPage.reservationSearch(ConfirmationNum);
			
			CheckinPage.checkInReservation(ConfirmationNum);
			
			Thread.sleep(3000);
			
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}
	
	@Test(priority = 16,groups = {"SANITY"},alwaysRun = true)
	public void validateSearchViews() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();

		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to validate Search views </b>");

			String ConfNum = ExcelUtils.getCellData(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation", "ConfirmationNum");
			ReservationPage.reservationAdvanceSearch(ConfNum);
			Thread.sleep(3000);
			
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}
	
	@Test(priority = 17,groups = {"SANITY"},alwaysRun = true)
	public void createReservationwithClosingScript() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();

		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Checkin a Reservation from Reservation presentation screen </b>");

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationWithClosingScript(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			Thread.sleep(3000);
			
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}
	
	@Test(priority = 18,groups = {"SANITY"},alwaysRun = true)
	public void  addReservationInTripComposer() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();

		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Checkin a Reservation from Reservation presentation screen </b>");

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.tripComposer(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
			Thread.sleep(3000);
			
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);
		}
	}
	
	@Test(priority = 19,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void ReservationwithTraces() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Reservation with Traces  </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> addTraceMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "ReservationwithTraces");
		    addTraceMap.put("ConfirmationNum", ConfNum);
			ReservationPage.addingTraces(addTraceMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("UpdateTraces");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"UpdateTraces","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.updatingTraces(addTraceMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("DeleteTraces");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"DeleteTraces","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.deletingTraces(addTraceMap);
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "ReservationwithTraces:: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	@Test(priority = 20,groups = {"SANITY"},alwaysRun = true)
	public void ReservationwithAlerts() throws Exception {
		
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
	    try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Reservation with Alerts </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> addAlertMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "ReservationwithAlerts");
		    addAlertMap.put("ConfirmationNum", ConfNum);
			ReservationPage.addingAlerts(addAlertMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("UpdateAlerts");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"UpdateAlerts","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.updatingAlerts(addAlertMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("DeleteAlerts");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"DeleteAlerts","Verify user is able To Delete Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			ReservationPage.deletingAlerts(addAlertMap);
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "ReservationwithAlerts:: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(priority = 21,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void ReservationwithNotes() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Reservation with Notes  </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> addNoteMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "ReservationwithNotes");
		    addNoteMap.put("ConfirmationNum", ConfNum);
			ReservationPage.addingNotes(addNoteMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("UpdateNotes");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"UpdateNotes","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.updatingNotes(addNoteMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("DeleteNotes");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"DeleteNotes","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.deletingNotes(addNoteMap);
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "ReservationwithNotes:: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	@Test(priority = 22,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void ReservationwithPackages() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Reservation with Packages  </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> addpackMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "ReservationwithPackages");
		    addpackMap.put("ConfirmationNum", ConfNum);
			ReservationPage.addingPackages(addpackMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("UpdatePackages");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"UpdatePackages","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.updatingPackages(addpackMap);	
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("DeletePackages");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"DeletePackages","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.deletingPackages(addpackMap);
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "ReservationwithPackages :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(priority = 23,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void Reservationwithstaydetails() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Reservation with stay details  </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> addsatyMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "Reservationwithstaydetails");
		    addsatyMap.put("ConfirmationNum", ConfNum);
			ReservationPage.updatingStaydetails(addsatyMap);
			
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservationwithstaydetails :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(priority = 24,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void Reservationwithconfirmationletter() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Reservation with confirmation letter  </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> addletterMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "Reservationwithconfirmationletter");
		    addletterMap.put("ConfirmationNum", ConfNum);
			ReservationPage.addingConfirmationletter(addletterMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("UpdateConfirmationLetter");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"UpdateConfirmationLetter","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.updatingConfirmationletter(addletterMap);
			
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservationwithconfirmationletter :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	@Test(priority = 25,groups = {"SANITY"},alwaysRun = true)//, enabled = false
	public void ReservationwithItemInventory() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Reservation with Item inventory  </b>");
	
		
		HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");
		String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);

			String ConfNum = ConfirmationNum;
		    HashMap<String, String> addItemMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "ReservationwithItemInventory");
		    addItemMap.put("ConfirmationNum", ConfNum);
			ReservationPage.addingItemInventory(addItemMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("UpdateItemInventory");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"UpdateItemInventory","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.updatingItemInventory(addItemMap);
			EndResultsLogging();

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("DeleteItemInventory");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"DeleteItemInventory","Verify user is able To Update Alerts for Reservation","Reservation","OPERA Cloud","","",true);
			
			ReservationPage.deletingItemInventory(addItemMap);
			
						
			//Logout from Application
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "ReservationwithItemInventory :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
}
