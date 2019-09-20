package com.oracle.hgbu.opera.qaauto.ui.fof.component.checkout;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation.ReservationPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class CheckoutPage extends Utils{
	/*******************************************************************
	-  Description: This method helps us to check out a reservation with open folio
	- Input: confirmation number
	- Output: Check out a reservation with open folio
	- Author: Anil Pentam
	- Date: 12/22/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	public static void checkOutOpenFolio(String conf) throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {	
			Utils.takeScreenshot(driver, methodName);
			ReservationPage.reservationSearch(conf);
			Utils.click("Reservation.Search_Reservation_IwantTo", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			Utils.click("Checkout.link_Checkout", 100, "clickable");
			Utils.waitForSpinnerToDisappear(20);
			if(Utils.isExists("Checkin.txt_CashierLogin")) {
				Utils.textBox("Checkin.editbox_CashierLOV", OR.getConfig("CashierUserID"), 100, "presence");
				Utils.tabKey("Checkin.editbox_CashierLOV");		
				Utils.waitForSpinnerToDisappear(10);
				Utils.textBox("Checkin.editbox_CashierPassword", OR.getConfig("CashierPassword"), 100, "presence");
				Utils.Wait(3000);
				Utils.click("Checkin.btn_CashierLogin", 100, "presence");
				Utils.waitForSpinnerToDisappear(20);
			}
			scroll("down");
			Utils.Wait(3000);
			scroll("down");
			if(Utils.isExists("Checkout.btn_CheckoutWithOpenFolio")) {
				Utils.jsClick("Checkout.btn_CheckoutWithOpenFolio", 100, "clickable");
				Utils.waitForSpinnerToDisappear(20);
				if(Utils.isDisplayed("Checkout.txt_CheckoutCompleted", "Checkoutpopup")) {
					Utils.Wait(3000);					
					Utils.click("Checkout.btn_CheckOutCompletedOk", 100, "clickable");
					Utils.waitForSpinnerToDisappear(20);
					scroll("up");
					Utils.Wait(3000);
					scroll("up");
					if((Utils.getText("Checkout.txt_ReservationStatus", 100, "presence")).equals("Checked Out")) {							
						logger.log(LogStatus.PASS, "Reservation Checked out Successfully");
						System.out.println("Reservation Checked out Successfully");
					}
				}else {
					logger.log(LogStatus.FAIL, "Checkout completed popup NOT displayed");
					System.out.println("Checkout completed popup NOT displayed");
				}
			}else {
				logger.log(LogStatus.FAIL, "Checkout screen is NOT displayed");
				System.out.println("Checkout screen is NOT displayed");
			}
			}catch (Exception e) {
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



}
