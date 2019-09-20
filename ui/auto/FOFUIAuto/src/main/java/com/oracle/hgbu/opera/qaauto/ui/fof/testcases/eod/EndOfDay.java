package com.oracle.hgbu.opera.qaauto.ui.fof.testcases.eod;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.config.component.ConfigPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.BaseClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class EndOfDay extends Utils{


	@Test(groups = {"BAT"},priority = 1)
	public void runEndOfDay() throws Exception {

		//Create Transaction Groups required for EOD
		try {
			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("TransactionGroupsForEOD");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"TransactionGroupsForEOD","Verify user is able To create Transaction Groups required for EOD","EndOfDay","OPERA Cloud","","",true);

			Utils.takeScreenshot(driver, "TransactionGroups");
			logger.log(LogStatus.INFO, "<b> Create Transaction Groups required for EOD </b>");
			ConfigPage.configNavigation();
			ExcelUtils.setExcelFile(OR.getConfig("Path_EODData"));
			int rows = ExcelUtils.getRowCount("TransactionGroups");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EODData"), "TransactionGroups", strIndex);
				ConfigPage.transactionGroups(configMap);			
			}
		} catch (Exception e) {	
			Utils.takeScreenshot(driver, "TransactionGroups");
			logger.log(LogStatus.FAIL, "Transaction Group not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, "TransactionGroups")));
			throw (e);
		}
		EndResultsLogging();

		//Create Transaction Subgroups required for EOD		
		try {
			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("TransactionSubGroupsForEOD");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"TransactionSubGroupsForEOD","Verify user is able To create Transaction SubGroups required for EOD","EndOfDay","OPERA Cloud","","",true);

			Utils.takeScreenshot(driver, "TransactionSubgroups");
			logger.log(LogStatus.INFO, "<b> Create Transaction Subgroups required for EOD </b>");			
			ConfigPage.configNavigation();
			ExcelUtils.setExcelFile(OR.getConfig("Path_EODData"));
			int rows = ExcelUtils.getRowCount("TransactionSubgroups");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EODData"), "TransactionSubgroups", strIndex);
				ConfigPage.transactionSubGroups(configMap);			
			}
		} catch (Exception e) {
			Utils.takeScreenshot(driver, "TransactionSubgroups");
			logger.log(LogStatus.FAIL, "Transaction Sub Groups not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, "TransactionSubgroups")));
			throw (e);
		}
		EndResultsLogging();

		//Create Transaction codes required for EOD		
		try {
			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("TransactionCodesForEOD");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"TransactionCodesForEOD","Verify user is able To create Transaction Codes required for EOD","EndOfDay","OPERA Cloud","","",true);

			Utils.takeScreenshot(driver, "TransactionCodes");
			logger.log(LogStatus.INFO, "<b> Create Transaction codes required for EOD </b>");
			ConfigPage.configNavigation();
			ExcelUtils.setExcelFile(OR.getConfig("Path_EODData"));
			int rows = ExcelUtils.getRowCount("TransactionCodes");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EODData"), "TransactionCodes", strIndex);
				ConfigPage.transactionCodes(configMap);			
			}
		} catch (Exception e) {
			Utils.takeScreenshot(driver, "TransactionCodes");
			logger.log(LogStatus.FAIL, "Transaction Code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occurred in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, "TransactionCodes")));
			throw (e);
		}
		EndResultsLogging();

		//Setting Application Functions required for EOD
		try {
			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("ApplicationFunctionsForEOD");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"ApplicationFunctionsForEOD","Verify user is able To set Application Functions required for EOD","EndOfDay","OPERA Cloud","","",true);

			Utils.takeScreenshot(driver, "EODApplicationFunctions");
			logger.log(LogStatus.INFO, "<b> Setting Application Functions required for EOD </b>");
			ExcelUtils.setExcelFile(OR.getConfig("Path_EODData"));
			int rows = ExcelUtils.getRowCount("ApplicationFunctions");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EODData"), "ApplicationFunctions", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after settings the application Functions
			ConfigPage.clearCache();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, "EODApplicationFunctions");
			logger.log(LogStatus.FAIL, "Application Parameters :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, "EODApplicationFunctions")));
			throw (e);
		}
		EndResultsLogging();

		//Setting Application Parameters required for EOD
		try {
			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("ApplicationParametersForEOD");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"ApplicationParametersForEOD","Verify user is able To set Application Parameters required for EOD","EndOfDay","OPERA Cloud","","",true);

			Utils.takeScreenshot(driver, "EODApplicationParameters");
			logger.log(LogStatus.INFO, "<b> Setting Application Parameters required for EOD </b>");
			ExcelUtils.setExcelFile(OR.getConfig("Path_EODData"));
			int rows = ExcelUtils.getRowCount("ApplicationParameters");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EODData"), "ApplicationParameters", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after settings the application Parameters
			ConfigPage.clearCache();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, "EODApplicationParameters");
			logger.log(LogStatus.FAIL, "Application Parameters :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, "EODApplicationParameters")));
			throw (e);
		}
		EndResultsLogging();

		//Setting Application Settings required for EOD
		try {
			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("ApplicationSettingsForEOD");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"ApplicationSettingsForEOD","Verify user is able To set Application Settings required for EOD","EndOfDay","OPERA Cloud","","",true);

			Utils.takeScreenshot(driver, "EODApplicationSettings");
			logger.log(LogStatus.INFO, "<b> Setting Application Settings required for EOD </b>");
			ExcelUtils.setExcelFile(OR.getConfig("Path_EODData"));
			int rows = ExcelUtils.getRowCount("ApplicationSettings");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EODData"), "ApplicationSettings", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after settings the application Parameters
			ConfigPage.clearCache();			
		}catch (Exception e) {
			Utils.takeScreenshot(driver, "EODApplicationSettings");
			logger.log(LogStatus.FAIL, "Application Parameters :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, "EODApplicationSettings")));
			throw (e);
		}
		EndResultsLogging();

		strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
		InitializeScenarioLog("runEndOfDay");
		LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"runEndOfDay","Verify user is able To run End of Day","EndOfDay","OPERA Cloud","","",true);

		//	Navigate to EOD
		logger.log(LogStatus.INFO, "<b> Navigating to EOD page</b>");

		// Navigating to Main Menu
		mouseHover("Configuration.mainMenu", 100, "clickable");
		click("Configuration.mainMenu", 100, "clickable");

		// Navigating to Administration Menu
		click("Configuration.OperaCloudMenu",100, "clickable");
		waitForSpinnerToDisappear(100);

		// Navigating to Financial
		mouseHover("EndOfDay.FinancialsMenu", 100, "presence");
		click("EndOfDay.FinancialsMenu", 100, "presence");

		//Navigate to End Of Day Menu
		click("EndOfDay.EndOfDayMenu", 100, "clickable");

		//Navigate to Manage End Of Day
		click("EndOfDay.ManageEndOfDaySubMenu", 100, "clickable");
		waitForSpinnerToDisappear(100);

		//Click on Manage button
		click("EndOfDay.btnManage", 100, "clickable");
		waitForSpinnerToDisappear(100);		

		//Click on Start button
		click("EndOfDay.manageEndOfDay.btnStart", 100, "clickable");
		waitForSpinnerToDisappear(100);	

		//Validating the pop up if EOD is running multiple times with in 24 hrs and proceeding
		//if(isExists("EndOfDay.manageEndOfDay.popUpEODConfirmation")){
			if(isDisplayed("EndOfDay.manageEndOfDay.popUpEODConfirmation","EOD Confirmation Popup")){
				logger.log(LogStatus.INFO, "EOD Confirmation popup is displayed");
				click("EndOfDay.manageEndOfDay.popUpEODConfirmation.btnStart", 100, "clickable");
				waitForSpinnerToDisappear(100);	
			}

			System.out.println("Step: Preparing End Of Day is in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Preparing End Of Day is in Progress... </b>");
			boolean flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusPreparingEndofDay", 300);
			if(flag){
				System.out.println("Step: Preparing End Of Day is Completed");
				logger.log(LogStatus.PASS, "<b> Step: Preparing End Of Day is Completed </b>");
			}
			else{
				System.out.println("Step: Preparing End Of Day is Failed");
				logger.log(LogStatus.FAIL, "<b> Step: Preparing End Of Day is Failed </b>");
			}

			System.out.println("Step: Arrivals not Checked In is in Progress... ");
			logger.log(LogStatus.INFO, "<b> Step: Arrivals not Checked In is in Progress... </b>");
			//		flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusArrivalsnotCheckedIn", 300);
			/*if(flag){
			System.out.println("Step: Arrivals not Checked In is Completed");
			logger.log(LogStatus.PASS, "<b> Step: Arrivals not Checked In is Completed </b>");
		}
		else{
			System.out.println("Step: Arrivals not Checked In is Failed");
			logger.log(LogStatus.FAIL, "<b> Step: Arrivals not Checked In is Failed </b>");
		}*/

			System.out.println("Step: Departures not Checked Out is in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Departures not Checked Out is in Progress... </b>");

			if(isExists("EndOfDay.manageEndOfDay.btnDepartures")){ 
				System.out.println("Step: Arrivals not Checked In is Completed");
				logger.log(LogStatus.PASS, "<b> Step: Arrivals not Checked In is Completed </b>");
				//		if(isDisplayed("EndOfDay.manageEndOfDay.btnDepartures","Departures Button")){
				jsClick("EndOfDay.manageEndOfDay.btnDepartures", 100, "clickable");
				waitForSpinnerToDisappear(100);	
				if(isExists("EndOfDay.manageEndOfDay.hdrQuickCheckOut")){
					logger.log(LogStatus.PASS, "Landed in Quick Checkout Page");
				}

				String strNoOfResv = getText("EndOfDay.manageEndOfDay.quickCheckOut.txtNumberOfResv", 100, "clickable");
				strNoOfResv = strNoOfResv.split("\\s+")[0];
				System.out.println("No Of Reservations are :: "+ strNoOfResv);
				int iNoOfResv = 2;
				if (strNoOfResv.contains("Only"))
					iNoOfResv = 1;
				else if(strNoOfResv != null)
					iNoOfResv = Integer.parseInt(strNoOfResv);

				System.out.println("No Of Reservations are :: "+ iNoOfResv);
				click("EndOfDay.manageEndOfDay.quickCheckout.chkAllReservations", 100, "clickable");
				waitForSpinnerToDisappear(100);

				click("EndOfDay.manageEndOfDay.quickCheckout.btnChkoutZeroBalanceDepartures", 100, "clickable");
				waitForSpinnerToDisappear(300);

				cashierLogin();

				click("EndOfDay.manageEndOfDay.quickCheckout.chkAllReservations", 100, "clickable");
				waitForSpinnerToDisappear(100);

				if(iNoOfResv > 1){
					click("EndOfDay.manageEndOfDay.quickCheckout.btnMassCheckout", 100, "clickable");
					waitForSpinnerToDisappear(300);
				}
				else{
					click("EndOfDay.manageEndOfDay.quickCheckout.btnCheckout", 100, "clickable");
					waitForSpinnerToDisappear(300);				
				}

				cashierLogin();

				while(isExists("EndOfDay.manageEndOfDay.btnSettleAndSendFolio") ){
					jsClick("EndOfDay.manageEndOfDay.btnSettleAndSendFolio", 100, "clickable");
					waitForSpinnerToDisappear(100);

					if(isExists("EndOfDay.manageEndOfDay.popUpReportDest")){
						logger.log(LogStatus.PASS, "Report Destination Popup is displayed");

						jsClick("EndOfDay.reportDestination.btnProcess", 100, "clickable");
						waitForSpinnerToDisappear(100);

						validateReportProcessed();

						jsClick("EndOfDay.checkoutReservation.btnCheckoutNow", 100, "clickable");
						waitForSpinnerToDisappear(100);

						if(isExists("EndOfDay.checkoutReservation.popUpCheckout")){	
							jsClick("EndOfDay.checkoutReservation.popUpCheckout.btnOK", 100, "clickable");
							waitForSpinnerToDisappear(100);
						}
						jsClick("EndOfDay.checkoutReservation.btnDone", 100, "clickable");
						waitForSpinnerToDisappear(200);

					}
					/*else
					{
						logger.log(LogStatus.FAIL, "Report Destination Popup is NOT displayed");
					}*/
				}

				//			if(isExists("EndOfDay.manageEndOfDay.linkBackToEOD")){
				if(isDisplayed("EndOfDay.manageEndOfDay.linkBackToEOD","Back To EOD Link")){
					jsClick("EndOfDay.manageEndOfDay.linkBackToEOD", 100, "clickable");
					waitForSpinnerToDisappear(300);
				}			
			}

			Thread.sleep(300000);
			flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusDeparturesnotCheckedOut", 300);
			if(flag){
				System.out.println("Step: Departures not Checked Out is Completed ");
				logger.log(LogStatus.PASS, "<b> Step: Departures not Checked Out is Completed </b>");
			}
			else{
				System.out.println("Step: Departures not Checked Out is Failed ");
				logger.log(LogStatus.FAIL, "<b> Step: Departures not Checked Out is Failed </b>");
			}

			if(isExists("EndOfDay.openFolios.btnReservation")){

				jsClick("EndOfDay.openFolios.btnReservation", 100, "clickable");
				waitForSpinnerToDisappear(100);

				cashierLogin();

				if(isExists("EndOfDay.manageEndOfDay.hdrOpenFolios")){
					System.out.println("Landed in Open Folios Page");
					logger.log(LogStatus.PASS, "Landed in Open Folios Page");
				}

				while(isExists("EndOfDay.manageEndOfDay.openFolios.linkIWantTo")){

					jsClick("EndOfDay.manageEndOfDay.openFolios.linkIWantTo", 100, "clickable");
					waitForSpinnerToDisappear(100);

					jsClick("EndOfDay.manageEndOfDay.openFolios.linkBilling", 100, "clickable");
					waitForSpinnerToDisappear(100);

					if(isExists("EndOfDay.manageEndOfDay.hdrBilling")){
						logger.log(LogStatus.PASS, "Landed in Billing Page");
					}				

					jsClick("EndOfDay.manageEndOfDay.openFolios.btnPostPayment", 100, "clickable");
					waitForSpinnerToDisappear(100);

					jsClick("EndOfDay.manageEndOfDay.openFolios.btnApplyPayment", 100, "clickable");
					waitForSpinnerToDisappear(100);

					jsClick("EndOfDay.manageEndOfDay.openFolios.btnClosePaymentWindow", 100, "clickable");
					waitForSpinnerToDisappear(100);				
				}

				if(isExists("EndOfDay.manageEndOfDay.openFolios.linkBackToOpenFolios")){	
					jsClick("EndOfDay.manageEndOfDay.openFolios.linkBackToOpenFolios", 100, "clickable");
					waitForSpinnerToDisappear(300);
				}			
				if(isExists("EndOfDay.manageEndOfDay.linkBackToEOD")){	
					jsClick("EndOfDay.manageEndOfDay.linkBackToEOD", 100, "clickable");
					waitForSpinnerToDisappear(300);
				}
				Thread.sleep(300000);
			}

			System.out.println("Step: Open Folios in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Open Folios in Progress... </b>");
			flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusOpenFolios", 300);
			if(flag){
				System.out.println("Step: Open Folios is Completed");
				logger.log(LogStatus.PASS, "<b> Step: Open Folios is Completed </b>");
			}
			else{
				System.out.println("Step: Open Folios is Failed");
				logger.log(LogStatus.FAIL, "<b> Step: Open Folios is Failed </b>");
			}

			System.out.println("Step: Cashier Closure in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Cashier Closure in Progress... </b>");
			flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusCashierClosure", 300);
			if(flag){
				System.out.println("Step: Cashier Closure is Completed");
				logger.log(LogStatus.PASS, "<b> Step: Cashier Closure is Completed </b>");
			}
			else{
				System.out.println("Step: Cashier Closure is Failed");
				logger.log(LogStatus.FAIL, "<b> Step: Cashier Closure is Failed</b>");
			}

			System.out.println("Step: Roll the Business Date is in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Roll the Business Date is in Progress... </b>");
			flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusRolltheBusinessDate", 300);
			if(flag){
				System.out.println("Step: Roll the Business Date is Completed");
				logger.log(LogStatus.PASS, "<b> Step: Roll the Business Date is Completed </b>");
			}
			else{
				System.out.println("Step: Roll the Business Date is Failed");
				logger.log(LogStatus.FAIL, "<b> Step: Roll the Business Date is Failed </b>");
			}

			System.out.println("Step: Posting Room and Tax is in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Posting Room and Tax is in Progress... </b>");
			flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusPostingRoomandTax", 300);
			if(flag){
				System.out.println("Step: Posting Room and Tax is Completed");
				logger.log(LogStatus.PASS, "<b> Step: Posting Room and Tax is Completed </b>");
			}
			else{
				System.out.println("Step: Posting Room and Tax is Failed");
				logger.log(LogStatus.FAIL, "<b> Step: Posting Room and Tax is Failed </b>");
			}

			System.out.println("Step: Run Additional Procedures is in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Run Additional Procedures is in Progress... </b>");
			flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusRunAdditionalProcedures", 300);
			if(flag){
				System.out.println("Step: Run Additional Procedures is Completed");
				logger.log(LogStatus.PASS, "<b> Step: Run Additional Procedures is Completed </b>");
			}
			else{
				System.out.println("Step: Run Additional Procedures is Failed");
				logger.log(LogStatus.FAIL, "<b> Step: Run Additional Procedures is Failed </b>");
			}		

			System.out.println("Step: Print Final Reports is in Progress...");
			logger.log(LogStatus.INFO, "<b> Step: Print Final Reports is in Progress... </b>");

			waitForSpinnerToDisappear(300);		
			if(isExists("EndOfDay.manageEndOfDay.printFinalReports.btnProcess")){
				jsClick("EndOfDay.manageEndOfDay.printFinalReports.btnProcess", 100, "clickable");
				waitForSpinnerToDisappear(300);	
			}
			/*flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusPrintFinalReports", 300);
		if(flag){
			logger.log(LogStatus.PASS, "<b> Step: Print Final Reports is Completed </b>");
		}
		else{
			logger.log(LogStatus.FAIL, "<b> Step: Print Final Reports is Failed </b>");
		}

		logger.log(LogStatus.INFO, "<b> Step: Finalizing End of Day is in Progress... </b>");
		flag = checkEODStepStatus("EndOfDay.manageEndOfDay.statusFinalizingEndofDay", 300);
		if(flag){
			logger.log(LogStatus.PASS, "<b> Step: Finalizing End of Day is Completed </b>");
		}
		else{
			logger.log(LogStatus.FAIL, "<b> Step: Finalizing End of Day is Failed </b>");
		}*/
			Thread.sleep(300000);

			if(isExists("EndOfDay.btnManage")){	
				logger.log(LogStatus.PASS, "<b> Step: Print Final Reports is Completed </b>");
				logger.log(LogStatus.PASS, "<b> Step: Finalizing End of Day is Completed </b>");

				System.out.println("End of Day is completed and Navigated back to manage End of Day page");
				logger.log(LogStatus.PASS, "<b> End of Day is completed and Navigated to manage End of Day page </b>");			
			}
			else{
				System.out.println("End of Day is Not completed and Not Navigated back to manage End of Day page");
				logger.log(LogStatus.FAIL, "<b> Step: Print Final Reports is Not Completed </b>");
				logger.log(LogStatus.FAIL, "<b> Step: Finalizing End of Day is Not Completed </b>");
				logger.log(LogStatus.FAIL, "<b> End of Day is Not completed and Not Navigated back to manage End of Day page </b>");
			}
			EndResultsLogging();

			validateReportProcessed();		

			strStartTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
			InitializeScenarioLog("regenerateEODReport");
			LogResultsOutputForMailReport(lStrScenarioOutputFilePath,"regenerateEODReport","Verify user is able to Regenerate EOD Report","EndOfDay","OPERA Cloud","","",true);

			logger.log(LogStatus.INFO, "<b> Navigating to EOD page</b>");

			// Navigating to End Of Day Reports

			// Navigating to Financial
			mouseHover("EndOfDay.FinancialsMenu", 100, "presence");
			click("EndOfDay.FinancialsMenu", 100, "presence");

			//Navigate to End Of Day Menu
			click("EndOfDay.EndOfDayReportsSubMenu", 100, "clickable");
			waitForSpinnerToDisappear(100);

			if(isExists("EndOfDay.EndOfDayReports.hdrEODReports")){
				logger.log(LogStatus.PASS, "Landed in EOD Reports Page");
			}

			click("EndOfDay.EODReports.btnSearch", 100, "clickable");
			waitForSpinnerToDisappear(100);

			click("EndOfDay.EODReports.chkBoxSelectAllReports", 100, "clickable");
			waitForSpinnerToDisappear(100);

			click("EndOfDay.EODReports.linkMore", 100, "clickable");
			waitForSpinnerToDisappear(100);

			click("EndOfDay.EODReports.linkRegenerateAndSendTo", 100, "clickable");
			waitForSpinnerToDisappear(100);

			if(isExists("EndOfDay.EndOfDayReports.hdrBatchReports")){
				logger.log(LogStatus.PASS, "Landed in Batch Reports Page");
			}

			click("EndOfDay.EndOfDayReports.BatchReports.btnProcess", 100, "clickable");
			waitForSpinnerToDisappear(300);

			validateReportProcessed();
			tearDown();
		
	}

	public boolean checkEODStepStatus(String strElement, int iSeconds) throws Exception{
		boolean flag = false;
		try{
			WebDriverWait waitForSpinner = new WebDriverWait(BaseClass.driver, iSeconds);
			List<WebElement> webElement = BaseClass.driver.findElements(OR.getLocator(strElement));
			System.out.println(" size :: "+ webElement.size());		
			flag = waitForSpinner.until(ExpectedConditions.attributeContains(webElement.get(webElement.size()-1),"title","Completed"));
			return flag;
		}catch(TimeoutException e){

		}
		catch(Exception e){
			logger.log(LogStatus.FAIL, "Class Utils | Method checkEODStepStatus | Exception Des :"+e.getMessage()+extentLogger.addScreenCapture(Utils.getScreenshot(driver,Utils.getMethodName())));			
			System.out.println("Browser Exception:" + e.getMessage() +extentLogger.addScreenCapture(Utils.getScreenshot(driver,Utils.getMethodName())));
			e.printStackTrace();
		}
		return flag;
	}

	public void cashierLogin() throws IOException, Exception{

		if(Utils.isExists("Checkin.txt_CashierLogin")) {
			
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
			System.out.println("Property Config: " + propertyMap);
			String CashierId = propertyMap.get("CashierId");
			System.out.println("CashierId: " + CashierId);
			
			Utils.textBox("Checkin.editbox_CashierLOV", CashierId, 100, "presence");
			Utils.tabKey("Checkin.editbox_CashierLOV");		
			Utils.waitForSpinnerToDisappear(100);
			
			String strPassword = propertyMap.get("Password");
			System.out.println("strPassword: " + strPassword);

			Utils.textBox("Checkin.editbox_CashierPassword", strPassword, 100, "presence");
			Utils.waitForSpinnerToDisappear(100);
			Utils.jsClick("Checkin.btn_CashierLogin", 100, "presence");
			Utils.waitForSpinnerToDisappear(100);
			if(isDisplayed("Checkin.btn_CashierLogin", "Cashier Login Button")){
				Utils.jsClick("Checkin.btn_CashierLogin", 100, "presence");
				Utils.waitForSpinnerToDisappear(100);
			}
		}
	}

	public void validateReportProcessed() throws InterruptedException{
		ArrayList<String> windowTab = new ArrayList<String>(driver.getWindowHandles());				
		driver.switchTo().window(windowTab.get(1));   
		Thread.sleep(15000);

		boolean blnStats = false;
		try{
			blnStats = driver.findElement(By.xpath("//*[@type ='application/pdf']")).isEnabled();
		}catch(Exception e){
			logger.log(LogStatus.FAIL,"Unable to access report");
		}
		if(blnStats){		        
			logger.log(LogStatus.PASS,"Report is opened in a new browser window");
		}
		else{
			logger.log(LogStatus.FAIL,"***Issue observed while opening the report in a new browser window***");
		}   	
		driver.switchTo().window(windowTab.get(1)).close();
		driver.switchTo().window(windowTab.get(0));
	}

}
