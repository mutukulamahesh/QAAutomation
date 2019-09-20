package com.oracle.hgbu.opera.qaauto.ui.fof.testcases.checkout;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.EmptyFileException;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.fof.component.checkin.CheckinPage;
import com.oracle.hgbu.opera.qaauto.ui.fof.component.checkout.CheckoutPage;
import com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation.ReservationPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class Cashiering extends Utils{
	
	
	/*******************************************************************
	-  Description: Verify user is able to checkout
	- Input: All mandatory details to post the charges
	- Output: Check Out Status for Reservation
	- Author: jasatis
	- Date: 28/12/18
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 1)
	public  void checkoutReservation() throws Exception {

		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		try {
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "CheckInReservationfromArrivals");  
			Utils.takeScreenshot(driver, methodName);
			String confirmationNum = ReservationPage.createReservationFromLTB(resvMap);	
			ExcelUtils.setDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges", "CheckoutReservation","CONFIRMATION_NUMBER", confirmationNum);			
			HashMap<String, String> chargesMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges", "CheckoutReservation");
			CheckinPage.checkInReservation(confirmationNum);
			CheckinPage.postCharges(chargesMap);
			Utils.WebdriverWait(100, "Checkout.Iwanttolink", "presence");
			Utils.mouseHover("Checkout.Iwanttolink");
			Utils.jsClick("Checkout.Iwanttolink");
			System.out.println("Selected I Want to click on link..");
			logger.log(LogStatus.PASS, "Selected I Want to click on link");
			// Selecting go to res link in I want to link
			Utils.WebdriverWait(100, "Checkout.earlyCheckout", "clickable");
			Utils.jsClick("Checkout.earlyCheckout");
			System.out.println("Selected Early CheckOut..");
			logger.log(LogStatus.PASS, "Selected Early CheckOut Link in Res Page");
			waitForSpinnerToDisappear(40);
			if(isExists("Checkin.breaklockconfirmationpopup"))
			{
				click("Checkin.breaklockbutton");
				waitForSpinnerToDisappear(40);
			}
			// Continue with early departure
			if (isExists("Checkout.checkout_early_continueBtn")) {
				Utils.WebdriverWait(30, "Checkout.checkout_early_continueBtn", "clickable");
				Utils.jsClick("Checkout.checkout_early_continueBtn");
				waitForSpinnerToDisappear(40);
				System.out.println("Selected continue early checkout button..");
				logger.log(LogStatus.PASS, "Selected continue early checkout Button..");
			}
			Utils.WebdriverWait(100, "Reservation.Foliosettlement_Heading", "presence");
			Utils.WebdriverWait(100, "Checkout.settleandsendfoliolink", "presence");
			Utils.mouseHover("Checkout.settleandsendfoliolink");
			Utils.jsClick("Checkout.settleandsendfoliolink");
			System.out.println("Selected Folio btn");
			logger.log(LogStatus.PASS, "Selected Folio btn");
			while(isExists("Checkout.Reportheader"))
			{
				jsClick("Checkout.processBtn", 100, "presence");
				waitForSpinnerToDisappear(40);
			}
			jsClick("Checkout.Checkoutnowbtn", 100, "presence");
			System.out.println("Selected Check-out btn");
			waitForSpinnerToDisappear(40);
			logger.log(LogStatus.PASS, "Selected Check-out btn");
			if(isExists("Checkout.popupdialog"))
			{
				System.out.println("inside the dialog");
				if(Utils.getText("Checkout.popup_checkoutmessage").contains("Check Out"))
				{
					System.out.println("Inside the check out dialog message");
					jsClick("Checkout.popupdialogok", 100, "presence");
				}
				else if(Utils.getText("Checkin.textondialogforoccupied").contains("The room being requested is currently occupied by"))
				{
					jsClick("Checkin.closelink", 100, "presence");
				}
				waitForSpinnerToDisappear(40);
			}
			jsClick("Checkout.donelink", 100, "presence");
			waitForSpinnerToDisappear(40);
			 if (getText("Checkout.txt_ReservationCheckout_checkOutstatus").contains("Checked Out")){
	                logger.log(LogStatus.PASS, "Reservation Successfully Checkedout");
	            }else {
	                logger.log(LogStatus.FAIL, "Reservation NOT Checkedout properly");
	            }
		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		}
		//Logout from Application
		Utils.tearDown();
	}
	
	/*******************************************************************
	-  Description: Verify user is able to checkout with a open folio
	- Input: All mandatory details to post the charges
	- Output: Checks Out a Reservation with open folio
	- Author: apentam
	- Date: 12/22/2018
	 ********************************************************************/	
	@Test(priority = 2,groups = {"BAT"})
	public void checkoutOpenFolio() throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
	    System.out.println("methodName: "+methodName);
	    try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify that use is able to checkout reservation with open folio </b>");
			//logger = report.startTest(methodName, "Verify that use is able to checkout reservation with open folio").assignCategory("acceptance", "Cloud.Checkin");
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "checkoutOpenFolio");			
			String confirmationNum = ReservationPage.createReservationFromLTB(resvMap);				
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "checkoutOpenFolio",
					"ConfirmationNum", confirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges", "checkoutOpenFolio",
					"CONFIRMATION_NUMBER", confirmationNum);
			HashMap<String, String> checkinMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges", "checkoutOpenFolio");
			CheckinPage.checkInReservation(confirmationNum);
			CheckinPage.postCharges(checkinMap);
			CheckoutPage.checkOutOpenFolio(confirmationNum);
	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		//Logout from Application
//		LoginPage.Logout();
//		Thread.sleep(3000);
		Utils.tearDown();
	}

	
	/*******************************************************************
	-  Description: Verify user is able to generate information folio
	- Input: All mandatory details to create reservation
	- Output: Generate information folio
	- Author: Girish
	- Date: 27/12/18
	 ********************************************************************/
	@Test(priority = 3,groups = {"BAT"})
	public void generateInformationFolios() throws Exception {
		
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
	    try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify that user is able to generate information folio </b>");
			
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "checkoutOpenFolio");
			HashMap<String, String> checkinMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges", "checkoutOpenFolio");
			String confirmationNum = ReservationPage.createReservationFromLTB(resvMap);				
			ExcelUtils.setDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "checkoutOpenFolio",
					"ConfirmationNum", confirmationNum);
			ExcelUtils.setDataByRow(OR.getConfig("Path_CheckinData"), "PostCharges", "checkoutOpenFolio",
					"CONFIRMATION_NUMBER", confirmationNum);
			CheckinPage.checkInReservation(confirmationNum);
			CheckinPage.postCharges(checkinMap);
			
			// Clicking information folio link
			click("Checkin.link_InformationFolio",80,"clickable");
			waitForSpinnerToDisappear(40);
			// Validating information folio page
			boolean blnInfoFolioPage = isExists("Checkin.txt_InformationFolio");
			if(blnInfoFolioPage){		
				logger.log(LogStatus.PASS,"Navigated to Information Folio Page");
				//Click on process button letter Menu
				click("Checkin.btn_InformationFolioProcess",80,"clickable");
				Thread.sleep(10000);
				ArrayList<String> windowTab = new ArrayList<String>(driver.getWindowHandles());				
				driver.switchTo().window(windowTab.get(1));   
				Thread.sleep(10000);
				
				//Open the confirmation letter in other browser
			    boolean blnStats = driver.findElement(By.xpath("//*[@type ='application/pdf']")).isEnabled();
				    if(blnStats){		        
				            logger.log(LogStatus.PASS,"Information Folio have open successfully");
				           }
				    else{
				    	logger.log(LogStatus.FAIL,"***Issue observed during Information Folio launch***");
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
	            if(strContent.contains("Folio")){
	                logger.log(LogStatus.PASS,"Information Folio content have verified successfully");
	            }else{
	            	logger.log(LogStatus.FAIL,"***Issue observed during Information Folio content verification***");
	            }	*/            
			}			
	    }catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Issue observed to verify that user is able to generate information folio :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		//Logout from Application
//		LoginPage.Logout();
//		Thread.sleep(3000);
		Utils.tearDown();
	}

	
}
