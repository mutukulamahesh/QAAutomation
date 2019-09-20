package com.oracle.hgbu.opera.qaauto.ui.fof.testcases.checkout;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.config.component.ConfigPage;
import com.oracle.hgbu.opera.qaauto.ui.crm.component.profile.ProfilePage;
import com.oracle.hgbu.opera.qaauto.ui.fof.component.checkin.CheckinPage;
import com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation.ReservationPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class SanityFlow extends Utils{
	
	/*******************************************************************
	- Description: Creates a Reservation and attach a company profile
	- Input: Profile, Source, Market Codes and Reservation Type, Payment Method
	- Output: Confirmation Number
	- Author: jasatis
	- Date: 09/01/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(priority = 1,groups = {"SANITY"})
	public void createReservationandattachCompanyProfile() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create a new Reservation </b>");

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			System.out.println("ConfirmationNum: "+ConfirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile","ConfirmationNum", ConfirmationNum);
			Thread.sleep(3000);
			HashMap<String, String> companyProfileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile", "createProfileCompany");
			ProfilePage.createCompanyProfile(companyProfileMap);
			ReservationPage.reservationSearch(ConfirmationNum);
			//click on list view 
			click("Reservation.listview", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.showAllLink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.linkedprofiles", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.associatedprofileslink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.editlink",100,"clickable");
			waitForSpinnerToDisappear(30);
			HashMap<String, String> companyProfileMap2 = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile", "createProfileCompany");
			System.out.println("second map" + companyProfileMap2);
			textBox("Reservation.companyprofileName", companyProfileMap2.get("FinalName"));
			Utils.tabKey("Reservation.companyprofileName");
			waitForSpinnerToDisappear(30);
			driver.switchTo().frame(Utils.element("Reservation.frameselectionforprofile"));
			click("Reservation.selectbutton", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.savebutton", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.closeusingXmark", 100, "clickable");
			waitForSpinnerToDisappear(30);
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
	- Description: Assign Rate Codes for Different Nights
	- Input: Confirmation number, rate codes
	- Output:Individual Nights with new rate code
	- Author: jasatis
	- Date: 28/01/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	
	@Test(priority = 2,groups = {"SANITY"})
	public void assignRateCodesforeachIndividualNight() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Search the Reservation </b>");
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");
			HashMap<String, String> configMap =  ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RateCodes", "Dataset_5");
			ReservationPage.reservationSearch(resvMap.get("ConfirmationNum"));
			//click on list view 
			click("Reservation.listview", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Click on Stay Details
			click("Reservation.staydetailslink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Click on Stay Daily Details
			click("Reservation.dailydetailslink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Click on actions link
			click("Reservation.dailydetails.actionslink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Click on edit link
			click("Reservation.dailydetails.actionslink.editlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Click on rate code lov 
			click("Reservation.dailydetails.ratecodeLov", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Enter the rate code in search text 
			textBox("Reservation.dailydetails.ratecodesearchtxt", configMap.get("RATE_CODE"), 100, "presence");
			tabKey("Reservation.dailydetails.ratecodesearchtxt");
			//Click on search link
			click("Reservation.dailydetails.ratecode.searchlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//select the rate code value
			click("Reservation.dailydetails.ratecodeclickinlist", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//click on the select link
			click("Reservation.dailydetails.ratecode.selectlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//click on the save link
			click("Reservation.dailydetails.ratecode.savelink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//click on the actions link
			click("Reservation.dailydetails.actionslink1", 100, "clickable");
			//click on the edit link
			waitForSpinnerToDisappear(30);
			click("Reservation.dailydetails.actionslink1.editlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//click on the rate code lov 
			click("Reservation.dailydetails.ratecodeLov", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Enter the rate code in search text 
			textBox("Reservation.dailydetails.ratecodesearchtxt", configMap.get("RATE_CODE2"),100,"presence");
			tabKey("Reservation.dailydetails.ratecodesearchtxt");
			//Click on search link
			click("Reservation.dailydetails.ratecode.searchlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.dailydetails.ratecodeclickinlist1", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.dailydetails.ratecode.selectlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Reservation.dailydetails.ratecode.savelink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//click on the close link
			click("Reservation.dailydetails.ratecode.closeusingXmark", 100, "clickable");
			waitForSpinnerToDisappear(30);
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
	- Description: Attach fixed charges to post on Daily Basis
	- Input: Confirmation number
	- Output:Post Fixed Charges on Daily Basis
	- Author: jasatis
	- Date: 28/01/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 3,groups = {"SANITY"})
	public void attachFixedChargesToPostonDailyBasis() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Search the Reservation </b>");
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");
			HashMap<String, String> fixedChargesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "FixedCharges", "postFixedChargesDaily");
			ReservationPage.reservationSearch(resvMap.get("ConfirmationNum"));
			//click on list view 
			click("Reservation.listview", 100, "clickable");
			waitForSpinnerToDisappear(30);
			click("Checkin.link_FirstIwantTo",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_ShowAll",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_FixedCharges",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);			
			CheckinPage.postFixedCharges(fixedChargesMap);
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
	@Test(priority = 4,groups = {"SANITY"})
	public void CheckInReservationfromArrivals() throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");            
            //Create a reservation and store the confirmation number
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
			textBox("Checkin.Confirmationnumbertxt", resvMap.get("ConfirmationNum"));
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
	-  Description: Create a routing instruction and post charges to window 2
	- Input: CONFIRMATION_NUMBER,Transaction Code and Window number 
	- Output: Routing must be created
	- Author: @author jasatis
	- Date: 12/24/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(priority = 5,groups = {"SANITY"})
	public void createRoutingInstructionstoWindow2() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Search the Reservation </b>");
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "Cashiering", "createFolioRouting");
			HashMap<String, String> combinedMap = new HashMap<String, String>();
			combinedMap.putAll(configMap);
			combinedMap.putAll(resvMap);
			System.out.println("combinedMap ::: "+combinedMap);
			CheckinPage.createFolioRouting(combinedMap);
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
	-  Description: Creating package code as the part of Configuration 
	- Input: Package_Code,Package_Description,Transaction_Code,Package_Profit,Package_Loss,Calculation_Rule,Posting_Rythm

	- Output: Package_Code
	- Author: Anil
	- Date:01/03/2019
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: Anil
	********************************************************************/
	@Test(priority = 6,groups = {"SANITY"})
    public void createPackageCode() throws Exception
    {
        String methodName = Utils.getMethodName();
        String testName = Utils.getClassName() + "@" + Utils.getMethodName();
        System.out.println("methodName: "+methodName);
        try {
        	ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("PackageCode");
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.INFO, "<b>Verify the Package Code is Created</b>");
            System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "PackageCode", strIndex);
				ConfigPage.PackageCodes(configMap);
			}
			
			//Logout from Application
			Utils.tearDown();
        }
         catch (Exception e)
        {
 			Utils.takeScreenshot(driver, methodName);
 			logger.log(LogStatus.FAIL, "Reservation not created :: Failed");
 			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
 			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
 			throw (e);
        }
    }
	/*******************************************************************
	-  Description: Verify Allowance Posted Upon Checkin for package on Night 1 
	- Input: Confirmation Number
	- Output: Allowance Posted Amount
	- Author: sindhoora
	- Date:01/03/2019
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: jasatis
	********************************************************************/
	@Test(priority = 7,groups = {"SANITY"})
    public  void verifyAllowancePostedoncheckinfornight1() throws Exception {

        String methodName = Utils.getMethodName();
        String testName = Utils.getClassName() + "@" + Utils.getMethodName();
        System.out.println("methodName: " + methodName);
        try {
            HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");
            HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "PackageCode", "Dataset_1");
            Utils.takeScreenshot(driver, methodName);
            ReservationPage.reservationSearch(resvMap.get("ConfirmationNum"));
            System.out.println("Click on Confirmation number link");
            Utils.WebdriverWait(100, "Reservation.Search.ConfNo.click", "presence");
            System.out.println("Confirmation number link found");
            Utils.jsClick("Reservation.Search.ConfNo.click");
            waitForSpinnerToDisappear(20);
            System.out.println("Clicked on Confirmation number link");
            Utils.WebdriverWait(100, "Reservation.page.display", "presence");
            waitForSpinnerToDisappear(20);
            System.out.println("Reservation page loaded succesfully");
            jsClick("Reservation.CustomizeView.link", 100, "presence");
            waitForSpinnerToDisappear(20);
            jsClick("Reservation.CustomizeView.Packages.chckbx", 100, "presence");
            waitForSpinnerToDisappear(20);
            jsClick("Reservation.CustomizeView.Apply.btn", 100, "presence");
            waitForSpinnerToDisappear(20);
            System.out.println("Packages selected");
            jsClick("Reservation.menu.Packages.link", 100, "presence");
            Utils.WebdriverWait(100, "Reservation.Pkg&Item.display", "presence");
            jsClick("Reservation.Pkg&Item.Pkg&Postings.tab", 100, "presence");
            waitForSpinnerToDisappear(20);
            String allowance = Utils.getText("Reservation.Pkg&Item.Pkg&Postings.Allowance.gt.txt");
            System.out.println("Allowance is ::: "+allowance);
            if(allowance.equalsIgnoreCase(configMap.get("ALLOWANCE")))
            {
            	logger.log(LogStatus.PASS,"Allowance Verified Successfully");
            	 System.out.println("Allowance Verified Successfully");
            }
            else
            {
            	logger.log(LogStatus.FAIL,"Allowance did not match");
            }
            Utils.tearDown();
	}
        catch (Exception e)
        {
 			Utils.takeScreenshot(driver, methodName);
 			logger.log(LogStatus.FAIL, "Allowance Verification is :: Failed");
 			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
 			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
 			throw (e);
        }
}
	/*******************************************************************
	-  Description: Post Charges to Consume less than allowance 
	- Input: Confirmation Number,Price
	- Output: Verify Charges Posted less than allowance
	- Author: jasatis
	- Date:01/03/2019
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: jasatis
	********************************************************************/
	
	@Test(priority = 8,groups = {"SANITY"})
    public  void postChargetoconsumelessthanallowance() throws Exception {

        String methodName = Utils.getMethodName();
        String testName = Utils.getClassName() + "@" + Utils.getMethodName();
        System.out.println("methodName: " + methodName);
        try {
            HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");
            HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "TransactionCodes", "Dataset_1");
            Utils.takeScreenshot(driver, methodName);
            ReservationPage.reservationSearch(resvMap.get("ConfirmationNum"));
            Thread.sleep(3000);
            //waitForSpinnerToDisappear(20);
            jsClick("Reservation.Iwanttoclick", 100, "clickable");
            waitForSpinnerToDisappear(20);
            jsClick("Reservation.Iwanttoclick.Billing", 100, "clickable");
            waitForSpinnerToDisappear(20);
            if(Utils.isExists("Checkin.txt_CashierLogin")) {
				Utils.textBox("Checkin.editbox_CashierLOV", OR.getConfig("CashierUserID"), 100, "presence");
				Utils.tabKey("Checkin.editbox_CashierLOV");		
				Utils.waitForSpinnerToDisappear(10);
				Utils.textBox("Checkin.editbox_CashierPassword", OR.getConfig("CashierPassword"), 100, "presence");
				Utils.Wait(3000);
				Utils.click("Checkin.btn_CashierLogin", 100, "presence");
				Utils.waitForSpinnerToDisappear(20);
			}
            jsClick("Reservation.Billing.postchargelink", 100, "clickable");
            waitForSpinnerToDisappear(20);
            jsClick("Reservation.postcharges.lovTransactioncode", 100, "clickable");
            waitForSpinnerToDisappear(20);
            textBox("Reservation.postcharges.txtSearchCode", configMap.get("TRANSACTION_CODE"), 100, "presence");
			tabKey("Reservation.postcharges.txtSearchCode");
			click("Reservation.postcharges.transcode.searchlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//Enter the transaction code value
			click("Reservation.postcharges.transcodeclickinlist", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//click on the select link
			click("Reservation.postcharges.transcode.selectlink", 100, "clickable");
			waitForSpinnerToDisappear(30);
			//configMap.get("PRICE")
			textBox("Reservation.postcharges.txtprice", "20.00", 100, "presence");
			tabKey("Reservation.postcharges.txtprice");
			waitForSpinnerToDisappear(20);
			jsClick("Reservation.postcharges.btnapplycharges", 100, "clickable");
			waitForSpinnerToDisappear(20);
			jsClick("Reservation.postcharges.closeusingXmark", 100, "clickable");
            Utils.tearDown();
	}
        catch (Exception e)
        {
 			Utils.takeScreenshot(driver, methodName);
 			logger.log(LogStatus.FAIL, "Charges Consumed Less than Allowance is :: Failed");
 			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
 			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
 			throw (e);
        }
}
	
	@Test(priority = 9,groups = {"SANITY"})
    public  void verifyRoomandtaxpostedwithwrappertransaction() throws Exception {
        String methodName = Utils.getMethodName();
        String testName = Utils.getClassName() + "@" + Utils.getMethodName();
        System.out.println("methodName: " + methodName);
        try {
            HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CreateReservationandattachcompanyprofile");
            HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "TransactionCodes", "Dataset_1");
            Utils.takeScreenshot(driver, methodName);
            ReservationPage.reservationSearch(resvMap.get("ConfirmationNum"));
            Thread.sleep(3000);
            //waitForSpinnerToDisappear(20);
            jsClick("Reservation.Iwanttoclick", 100, "clickable");
            waitForSpinnerToDisappear(20);
            jsClick("Reservation.Iwanttoclick.Billing", 100, "clickable");
            waitForSpinnerToDisappear(20);
            scroll("down");
            Utils.tearDown();
	}
        catch (Exception e)
        {
 			Utils.takeScreenshot(driver, methodName);
 			logger.log(LogStatus.FAIL, "Room and Tax Posted with Wrapper Transaction is :: Failed");
 			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
 			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
 			throw (e);
        }
}
	
}
