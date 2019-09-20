package com.oracle.hgbu.opera.qaauto.ui.crm.testcases.rates;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.aventstack.extentreports.model.Log;
import com.oracle.hgbu.opera.qaauto.ui.crm.component.rates.RatesPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

/**
 * <p>
 * <b> This Class provides the code for Rate page funtions.</b>
 * @author 
 * </p>
 */

public class Rates extends Utils {
	
	


		/*******************************************************************
	-  Description: creating a Reservation Using a restricted rate code
	- Input: Restricted rate code , room type and begin date for restricted code
	- Output: 
	- Author: Swati
	- Date: 20/12/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 1,groups = {"BAT"},alwaysRun = true)
	public void createReservationWithRestrictedRate() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to see the message when trying to create a reservation using restricted rate code </b>");

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_RateData"), "Reservation", "createReservationWithRestrictedRate");
			RatesPage.createReservationWithRestrictedRate(resvMap);
			
			Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created with restricted rate :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	/*******************************************************************
	-  Description: Verify search functionality for property availability screen<
	- Input: Room Type or Room Class
	- Output: 
	- Author: Swati
	- Date: 20/12/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	********************************************************************/
	@Test(priority = 2,groups = {"BAT"},alwaysRun = true)
	public void ValidatePropertyAvailabilityScreen() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify search functionality for property availability screen</b>");

			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_RateData"), "Reservation", methodName);
			RatesPage.ValidatePropertyAvailabilityScreen(resvMap, "RoomType");
			
			Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Property Availability Screen is not validated :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"SANITY"},priority = 3)
	public void abilityTochangeViewOptionsPropertyscreen() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to change view options in property screen </b>");
			//logger = report.startTest(methodName, "Verify able to Create Rate code with a restriction").assignCategory("acceptance", "Cloud.Configuration");
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Rooms", "Dataset_11");
			System.out.println("Config Map: " + configMap);
			
            //ProfilePage.createGuestProfile(profileMap, "SaveAdd");
			RatesPage.abilityTochangeViewOptionsPropertyscreen(configMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "user is able to search profiles using First and Last Names :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	@Test(groups = {"SANITY"},priority = 4)
	public void VerifyCloseRates() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify User is able to search for closed rates in inventory</b>");
			//logger = report.startTest(methodName, "Verify able to Create Rate code with a restriction").assignCategory("acceptance", "Cloud.Configuration");
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_RateData"), "Reservation", methodName);
			System.out.println("Config Map: " + configMap);
			
            //ProfilePage.createGuestProfile(profileMap, "SaveAdd");
			RatesPage.inventoryManageRestrictions(configMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Verify User is able to search for closed rates in inventory :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }

	
}


