package com.oracle.hgbu.opera.qaauto.ui.config.testcases;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ui.config.component.ConfigPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class Config extends Utils {
	
	@Test(groups = {"BAT"},priority = 1)	
	public void activateLicenses() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to activate Licenses </b>");
			//logger = report.startTest(methodName, "Verify if user is able to activate Licenses").assignCategory("acceptance", "Cloud.Configuration");
			ConfigPage.navigateToToolBox();
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("Licenses");
			System.out.println("Rows:: "+ rows);
			for(int i = 1; i <= rows-1; i++){
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Licenses", "Dataset_"+i);
				ConfigPage.activateLicenses(configMap);				
			}
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Failed to activate license :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}

	@Test(groups = {"BAT"},priority = 2)	  
	public void applicationFunctions() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		try {
			
			//ConfigPage.clearCache();			
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to enable/disable application functions </b>");
			//logger = report.startTest(methodName, "Verify if user is able to enable/disable application functions").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("ApplicationFunctions");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ApplicationFunctions", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after setting the application functions
			ConfigPage.clearCache();

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Application Functions :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}

	@Test(groups = {"BAT"},priority = 3)
	public void applicationFunctionAttributes() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to set,enable/disable application functions attributes </b>");
			//logger = report.startTest(methodName, "Verify if user is able to set,enable/disable application functions attributes").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("ApplicationFunctionAttributes");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ApplicationFunctionAttributes", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after setting the application functions
			ConfigPage.clearCache();

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Application Functions :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 4)
	public void applicationParameters() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to enable/disable application parameters </b>");
			//logger = report.startTest(methodName, "Verify if user is able to enable/disable application parameters").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("ApplicationParameters");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ApplicationParameters", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after settings the application Parameters
			ConfigPage.clearCache();

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Application Parameters :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 5)
	public void applicationSettings() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to set the application settings values </b>");
			//logger = report.startTest(methodName, "Verify if user is able to set the application settings values").assignCategory("acceptance", "Cloud.Configuration");
			
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("ApplicationSettings");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ApplicationSettings", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after settings the application Parameters
			ConfigPage.clearCache();

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Application Parameters :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 6)
	public void createOwner() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to update the owner for a user </b>");
			//logger = report.startTest(methodName, "Verify if user is able to update the owner for a user").assignCategory("acceptance", "Cloud.Configuration");
			HashMap<String, String> configMap = new HashMap<String, String>();
			configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configMap.get("Set"));
			ConfigPage.createOwner(configMap);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Owner not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 7)
	public void createRoomClass() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Room Class </b>");
			//logger = report.startTest(methodName, "Verify if user is able to Create Room Class").assignCategory("acceptance", "Cloud.Configuration");
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RoomClasses", "Dataset_1");
			ConfigPage.roomClass(configMap);
			
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, methodName+" not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 8)
	public void createRoomType() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Room Type </b>");
			//logger = report.startTest(methodName, "Verify if user is able to Create Room Type").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("RoomTypes");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= 4; i++){
				String strIndex =  "Dataset_"+(i-1);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RoomTypes", strIndex);
			ConfigPage.roomTypes(configMap);
			}
			//Thread.sleep(3000);
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, methodName+" not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	@Test(groups = {"BAT"},priority = 9)
	public void createRoom() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Room </b>");
			//logger = report.startTest(methodName, "Verify if user is able to Create Room").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("Rooms");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= 11; i++){
				String strIndex =  "Dataset_"+(i-1);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Rooms", strIndex);
			ConfigPage.createRoom(configMap);
			}
			//Thread.sleep(3000);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, methodName+" not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 10)
	public void createMarketGroup() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify market group is getting created for a property </b>");
			//logger = report.startTest(methodName, "Verify market group is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");			
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "MarketGroups","Dataset_1");
			ConfigPage.marketGroup(configMap);

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Market Group is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 11)
	public void createMarketCode() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify market code is getting created for a property </b>");
			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "MarketCodes","Dataset_1");
			
			System.out.println("configMap" + configMap);
			
			ConfigPage.marketCode(configMap);

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Market Group is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 12)
	public void sourceGroup() throws Exception {

		//String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create source groups in a property </b>");
			//logger = report.startTest(methodName, "Verify able to Create a Reservation From Profile Screen").assignCategory("acceptance", "Cloud.Configuration");			
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "SourceGroups", "Dataset_2");
			ConfigPage.sourceGroup(configMap);
//			//LoginPage.Logout();
//            //Thread.sleep(3000);
            Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Source Group not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 13)
	public void sourceCode() throws Exception {

		//String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create source codes in a property </b>");
			//logger = report.startTest(methodName, "Verify able to Create source codes in a property").assignCategory("acceptance", "Cloud.Configuration");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "SourceCodes", "Dataset_2");
			ConfigPage.sourceCode(configMap);
//			//LoginPage.Logout();
//            //Thread.sleep(3000);
            Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "SourceCode not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}	
	
	@Test(groups = {"BAT"},priority = 14)
    public void createReservationTypes() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create  Reservation types in a property </b>");
			//logger = report.startTest(methodName, "Verify able to Create  Reservation types in a property").assignCategory("acceptance", "Cloud.Configuration");
            HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ReservationTypes","Dataset_1");
            ConfigPage.createReservationTypes(configMap);
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Reservation Types not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	@Test(groups = {"BAT"},priority = 15)
	public void createTransactionGroup() throws Exception {		
		
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user is able to Create Transaction Group </b>");
			//logger = report.startTest(methodName, "Verify user is able to Create Transaction Group").assignCategory("acceptance", "Cloud.Configuration");		
			
			ConfigPage.configNavigation();
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("TransactionGroups");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "TransactionGroups", strIndex);
				ConfigPage.transactionGroups(configMap);			
			}		
			//Thread.sleep(3000);		

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Transaction Group not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	@Test(groups = {"BAT"},priority = 16)
	public void createTransactionSubGroup() throws Exception {		
		
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user is able to Create Transaction SubGroup </b>");
			//logger = report.startTest(methodName, "Verify user is able to Create Transaction SubGroup").assignCategory("acceptance", "Cloud.Configuration");			

			ConfigPage.configNavigation();
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("TransactionSubgroups");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "TransactionSubgroups", strIndex);
				ConfigPage.transactionSubGroups(configMap);			
			}							
			//Thread.sleep(3000);
			
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Transaction Sub Groups not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 17)
	public void createTransactionCode() throws Exception {		
		
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user is able to Create Transaction Code </b>");
			//logger = report.startTest(methodName, "Verify user is able to Create Transaction Code").assignCategory("acceptance", "Cloud.Configuration");			
			ConfigPage.configNavigation();
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("TransactionCodes");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "TransactionCodes", strIndex);
				ConfigPage.transactionCodes(configMap);			
			}
			//Thread.sleep(3000);
			
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Transaction Code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 18)
    public void createPaymentMethods() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to Create Payment Types </b>");
			//logger = report.startTest(methodName, "Verify user is able to Create Payment Types").assignCategory("acceptance", "Cloud.Configuration");	
			
            HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "PaymentTypes","Dataset_1");
            ConfigPage.createPaymentMethods(configMap);
            //LoginPage.Logout();
            //Thread.sleep(3000);
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Payment Methods not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	@Test(groups = {"BAT"},priority = 19)
	public void createRateClass() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Rate Classes </b>");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RateClasses", "Dataset_1");
			System.out.println("Config Map: " + configMap);
			
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
			System.out.println("Property Config: " + propertyMap);
			
			String property = propertyMap.get("Property");
			System.out.println("prop: " + property);
			
			configMap.put("PROPERTY", property);
			
			ConfigPage.createRateClass(configMap);
			
			Utils.tearDown();
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Rate Class not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 20)
	public void createRateCategory() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Rate Categories </b>");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RateCategories", "Dataset_1");
			System.out.println("Config Map: " + configMap);
			
			// read property from excel
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
			System.out.println("Property Config: " + propertyMap);
			
			String property = propertyMap.get("Property");
			System.out.println("prop: " + property);
			
			configMap.put("PROPERTY", property);
			
			// read business date from excel
			Utils.getBusinessDate();
			Map<String, String> dateMap = new HashMap<String, String>();
			dateMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			dateMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", dateMap.get("Set"));
			System.out.println("Date Config: " + dateMap);
			
			String beginDate = dateMap.get("BusinessDate");
			System.out.println("begin date: " + beginDate);
			
			String endDate = addYearsToBusinessDate(1);
			System.out.println("end date: " + endDate);
			
			configMap.put("BEGIN_DATE", beginDate);
			configMap.put("END_DATE", endDate);
			
			ConfigPage.createRateCategory(configMap);
			
			Utils.tearDown();
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Rate Category not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
		
	@Test(groups = {"BAT"},priority = 21)
	public void createRateCode() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Rate Codes </b>");
			
			// read business date from excel
			Utils.getBusinessDate();
			Map<String, String> dateMap = new HashMap<String, String>();
			dateMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			dateMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", dateMap.get("Set"));
			System.out.println("Date Config: " + dateMap);
			
			String beginDate = dateMap.get("BusinessDate");
			System.out.println("begin date: " + beginDate);
			
			String endDate = addYearsToBusinessDate(1);
			System.out.println("end date: " + endDate);
						
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("RateCodes");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++) {
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RateCodes", strIndex);
				
				Map<String, String> propertyMap = new HashMap<String, String>();
				propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
				propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
				System.out.println("Property Config: " + propertyMap);
				
				String property = propertyMap.get("Property");
				System.out.println("prop: " + property);
				
				configMap.put("PROPERTY", property);
				configMap.put("BEGIN_DATE", beginDate);
				configMap.put("END_DATE", endDate);
				
				ConfigPage.createRateCode(configMap);				
			}
			
			Utils.tearDown();
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Rate Code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	@Test(groups = {"BAT"},priority = 22)
	public void createOutOfOrderReasonsCode() throws Exception {

		//String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user able to create out of order/reason code </b>");
			//logger = report.startTest(methodName, "Verify user able to create out of order/reason code").assignCategory("acceptance", "Cloud.Configuration");
			
			//Login Application
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RoomReasons", "Dataset_1");
			ConfigPage.outOfOrderReason(configMap);
			//Thread.sleep(3000);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Out of order/reason code :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 23)
	public void createRoomMaintainceCode() throws Exception {

		//String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user able to create room maintenance code </b>");
			//logger = report.startTest(methodName, "Verify user able to create room maintaince code").assignCategory("acceptance", "Cloud.Configuration");
			
			//Login Application
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RoomMaintenance", "Dataset_1");
			ConfigPage.roomMaintainceCode(configMap);
			//Thread.sleep(3000);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Room maintenance code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}

	@Test(groups = {"BAT"},priority = 24)
	public void createArticleCode() throws Exception {

		//String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user able to create article code </b>");
			//logger = report.startTest(methodName, "Verify user able to create article code").assignCategory("acceptance", "Cloud.Configuration");
			
			//Login Application			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Articles", "Dataset_1");
			ConfigPage.createArticle(configMap);
			//Thread.sleep(3000);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Article code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 25)
	public void createEventWithTime() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to create EventType With Time</b>");
		try {
			Utils.takeScreenshot(driver, methodName);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "EventType",
					"Dataset_1");
			ConfigPage.createEventType(configMap);
			HashMap<String, String> configMapKof = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "EventType",
					"Dataset_3");
			ConfigPage.createEventType(configMapKof);
					//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Event with time not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 26)
	public void createEventWithoutTime() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to create EventType Without Time </b>");
		try {
			Utils.takeScreenshot(driver, methodName);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "EventType",
					"Dataset_2");
			ConfigPage.createEventType(configMap);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Event without time not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}		
		
	}
	
	
	@Test(groups = {"BAT"},priority = 27)
    public void  createCashier() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to Create Cashier </b>");
			//logger = report.startTest(methodName, "Verify user is able to Create Cashier").assignCategory("acceptance", "Cloud.Configuration");	
			HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"),"Cashier","Dataset_1");
            ConfigPage.createCashier(configMap);
            //LoginPage.Logout();
            //Thread.sleep(3000);
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Cashier not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	
	@Test(groups = {"BAT"},priority = 28)
	public void createReportGroup() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Report Group </b>");
			//logger = report.startTest(methodName, "Verify if user is able to Create Report Group").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
			ConfigPage.createReportGroup(configMap);
			//Thread.sleep(3000);
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Report Group not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}	
	
	
	@Test(groups = {"BAT"},priority = 29)
	public void configureReport() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Configure Report </b>");
			//logger = report.startTest(methodName, "Verify if user is able to Create Report Group").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
			ConfigPage.ConfigureReport(configMap);
			//Thread.sleep(3000);
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Configuring Report Group not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 30)
	public void createRateCodeRestriction() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);

		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify able to Create Rate code with a restriction </b>");
			//logger = report.startTest(methodName, "Verify able to Create Rate code with a restriction").assignCategory("acceptance", "Cloud.Configuration");
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RateCodes", "Dataset_2");
			System.out.println("Config Map: " + configMap);
			ConfigPage.createRateRestriction(configMap);
			
			//Logout from Application

			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Rate Code with restriction not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 31)
	public void createSetupStyle() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to create SetUpStyle</b>");
		try {
			Utils.takeScreenshot(driver, methodName);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "FunctionSpace",
					"createSetUpStyle");
			ConfigPage.createSetUpStyle(configMap);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Setup Style not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 32) //,groups = { "bat" }, enabled=false
	public void blocksConfigRoleManager() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CreateBlock", "Dataset_1");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify and Enables the Manage Blocks and Edit/New block in Role manager </b>");
		try {
			ConfigPage.blocksConfigRoleManagerSetUp(configMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify and Enables the Manage Blocks and Edit/New block in Role manager is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(groups = {"BAT"},priority = 33) //,groups = { "bat" }, enabled=false
	public void statusCheckBlockAdmin() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CreateBlock", "Dataset_1");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify block status INQ is Created as part of config </b>");
		try {
			ConfigPage.statusCheckBlockAdministration(configMap);			
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify block status INQ is Created as part of config is not validated"+e.getMessage());
			throw (e);
		}
		Utils.tearDown();
	}
	
	@Test(groups = {"BAT"},priority = 34)
	public void PrinterConfiguration() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Configure Printer </b>");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Printers", "Dataset_1");
			ConfigPage.PrinterConfiguration(configMap);
			//Thread.sleep(3000);
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, methodName+" not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 35)
	public void createFunctionSpacetype() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to create Function Space Type</b>");
		try {
			Utils.takeScreenshot(driver, methodName);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "FunctionSpace",
					"createFunctionSpaceType");
			ConfigPage.createFunctionSpaceTypes(configMap);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Function Space Type not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 36)
	public void createLocations() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to create Function Space Type</b>");
		try {
			Utils.takeScreenshot(driver, methodName);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "FunctionSpace",
					"createFunctionSpaceType");
			ConfigPage.createFunctionSpaceTypes(configMap);
			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Function Space Type not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	

	@Test(groups = {"BAT"},priority = 37)
	public void createRentalCodes() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to create rental code</b>");
		try {
			Utils.takeScreenshot(driver, methodName);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "FunctionSpace",
					"createLRentalCode");
			ConfigPage.createRentalCode(configMap);
			//Logout from Application
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Rental Code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 38)
	public void createFunctionSpace() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to create Function Space</b>");
		try {
			Utils.takeScreenshot(driver, methodName);
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CreateFunctionSpace",
					"createFunctionSpace");
			ConfigPage.createFunctionSpace(configMap);
			//Logout from Application
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Function Space not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			Utils.tearDown();
			throw (e);
		}
	}
	
	@Test(groups = {"BAT"},priority = 39)
    public void createReservationTypeSchedule() throws Exception {
		
        String methodName = Utils.getMethodName();
        System.out.println("methodName: " + methodName);
        
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create Reservation type schedule in a property </b>");
			//logger = report.startTest(methodName, "Verify able to Create  Reservation types in a property").assignCategory("acceptance", "Cloud.Configuration");
            HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ReservationTypeSchedules","Dataset_1");
            
            ConfigPage.createReservationTypeSchedule(configMap);
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Reservation Type Schedule not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	
	@Test(groups = {"SANITY"},priority = 39)
	public void createPreferenceCode() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Preference Code is getting created for a property </b>");
			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Preferences","Dataset_1");
			
			System.out.println("configMap" + configMap);
			
			ConfigPage.Preferences(configMap);

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Preference Code is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	@Test(groups = {"Sanity"},priority = 40)
	public void createAttendants() throws Exception {
		String methodName = Utils.getMethodName();		 
		System.out.println("methodName: "+methodName);		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user able to create Attendants code </b>");
				
			//Login Application		
			for(int i =1;i<=2;i++){
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Attendants", "Dataset_"+i);
			ConfigPage.createAttendants(configMap);
			}
			Thread.sleep(3000);
			//Logout from Application
			LoginPage.Logout();
			Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Attendants code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}


	@Test(groups = {"Sanity"},priority = 41)
	public void createTasks() throws Exception {
		String methodName = Utils.getMethodName();		 
		System.out.println("methodName: "+methodName);		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user able to Create Tasks code </b>");
					
			//Login Application			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "HousekeepingTasks", "Dataset_1");
			ConfigPage.createTasks(configMap);
			Thread.sleep(3000);
			//Logout from Application
			LoginPage.Logout();
			Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Tasks code not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"SANITY"},priority = 42)
	public void CreateMembershipClass() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Membership Class is getting created for a property </b>");
			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Membership","Dataset_1");
			
			System.out.println("configMap" + configMap);
			
			ConfigPage.CreateMembershipClass(configMap);

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Membership Class is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"SANITY"},priority = 43)
	public void CreateMembershipType() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Membership Type is getting created for a property </b>");
			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Membership","Dataset_1");
			
			System.out.println("configMap" + configMap);
			
			ConfigPage.CreateMembershipType(configMap);

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Membership Type is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	@Test(groups = {"SANITY"},priority = 44)
	public void createKeywordTypes() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify create keyword types is getting created for a property </b>");
			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "KeywordTypes","Dataset_1");
			System.out.println("configMap" + configMap);
			ConfigPage.createKeywordTypes(configMap);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Keyword Type is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"SANITY"}, priority = 45)
	public void createDepositRule() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a Deposit Rule </b>");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "DepositRule", "Dataset_1");
			System.out.println("Config Map: " + configMap);
			
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
			System.out.println("Property Config: " + propertyMap);
			
			String property = propertyMap.get("Property");
			System.out.println("prop: " + property);
			
			configMap.put("PROPERTY", property);
			
			ConfigPage.createDepositRule(configMap);
			
			Utils.tearDown();
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Deposit Rule not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"SANITY"}, priority = 46)
	public void createDepositRulesSchedule() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a Deposit Rule Schedule </b>");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "DepositRuleSchedule", "Dataset_1");
			System.out.println("Config Map: " + configMap);
			
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
			System.out.println("Property Config: " + propertyMap);
			
			String property = propertyMap.get("Property");
			System.out.println("prop: " + property);
			
			configMap.put("PROPERTY", property);
			
			// read business date from excel
			Utils.getBusinessDate();
			Map<String, String> dateMap = new HashMap<String, String>();
			dateMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			dateMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", dateMap.get("Set"));
			System.out.println("Date Config: " + dateMap);
			
			String beginDate = dateMap.get("BusinessDate");
			System.out.println("begin date: " + beginDate);
			
			String endDate = addYearsToBusinessDate(1);
			System.out.println("end date: " + endDate);
			
			configMap.put("BEGIN_DATE", beginDate);
			configMap.put("END_DATE", endDate);
			
			ConfigPage.createDepositRulesSchedule(configMap);
			
			Utils.tearDown();
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Deposit Rule Schedule not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}

	
	@Test(groups = {"SANITY"},priority = 47)
	public void CreateNoteTypes() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Note Types is getting created for a property </b>");
			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "NOTES","Dataset_1");
			
			System.out.println("configMap" + configMap);
			
			ConfigPage.CreateNotesType(configMap);

			//Logout from Application
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Notes Type is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}

	@Test(groups = {"BAT"},priority = 48) //,groups = { "bat" }, enabled=false
	public void blocksRoleManagerActasAt() throws Exception {
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CreateBlock", "Dataset_1");
		System.out.println("Test Name: "+testName);
		logger.log(LogStatus.INFO, "<b> Verify and set the Act as and AT in the edit user in Role manager for create blocks </b>");
		try {
			ConfigPage.blocksConfigureRoleManagerActasAt(configMap);
			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
			Utils.takeScreenshot(driver, testClassName);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.tearDown();
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL," Verify and set the Act as and AT in the edit user in Role manager for create blocks is not validated"+e.getMessage());
			Utils.tearDown();
			throw (e);
		}
	}	
	
	
	@Test(groups = {"BAT"},priority = 49)
	public void applicationParameterAttributes() throws Exception {
		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to set,enable/disable application functions attributes </b>");
			//logger = report.startTest(methodName, "Verify if user is able to set,enable/disable application paramater attributes").assignCategory("acceptance", "Cloud.Configuration");
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("ApplicationParameterAttributes");

			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ApplicationParameterAttributes", strIndex);
				ConfigPage.applicationFunctionsParametersSettings(configMap);				
			}

			//Clear Cache after setting the application functions
			ConfigPage.clearCache();

			//Logout from Application
			//LoginPage.Logout();
			//Thread.sleep(3000);
			Utils.tearDown();

		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Application Parameters :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"SANITY"},priority = 50)
	public void CreateGenerates() throws Exception {

		String methodName = Utils.getMethodName();
		 
		System.out.println("methodName: "+methodName);
		
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify Generates are created for the transaction codes </b>");
			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
			ConfigPage.configNavigation();
			ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			int rows = ExcelUtils.getRowCount("Generates");
			System.out.println("Rows:: "+ rows);
			for(int i = 2; i <= rows; i++){
				String strIndex =  "Dataset_"+(i-1);
				HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Generates", strIndex);			
				ConfigPage.transactionGenerates(configMap);
			}

			//Logout from Application
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Generates is not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(priority = 51,groups = {"SANITY"})
    public void createPackageCode() throws Exception
    {
        String methodName = Utils.getMethodName();
//        String testName = Utils.getClassName() + "@" + Utils.getMethodName();
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
 			logger.log(LogStatus.FAIL, "package Code is not created :: Failed");
 			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
 			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
 			throw (e);
        }
    }       
    	
        @Test(priority = 52,groups = {"SANITY"})
    	public void copyreservationactive() throws Exception {		
    		
    		String methodName = Utils.getMethodName();
    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.INFO, "<b> Verify if user is able to activate the copy reservation</b>");
    			
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CopyReservation", "Dataset_1");
    			System.out.println("Config Map: " + configMap);
    			
    			Map<String, String> propertyMap = new HashMap<String, String>();
    			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
    			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
    			System.out.println("Property Config: " + propertyMap);
    			
    			String property = propertyMap.get("Property");
    			System.out.println("prop: " + property);
    			
    			configMap.put("PROPERTY", property);
    			
    			ConfigPage.copyreservationactive(configMap);
    			
    			Utils.tearDown();

    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "copy reservatoin status is not activated :: Failed");
    			logger.log(LogStatus.FAIL, "Package occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}

        @Test(groups = {"SANITY"},priority = 50)
    	public void createVIPLevel() throws Exception {

    		String methodName = Utils.getMethodName();		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.INFO, "<b> Verify VIPLevel is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "VIP","Dataset_1");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createVIPLevel(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "VIPLevel is not created :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
    	
    	
    	@Test(groups = {"SANITY"},priority = 51)
    	public void createExternalDatabase() throws Exception {

    		String methodName = Utils.getMethodName();
    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.INFO, "<b> Verify External Database is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ExternalDatabase","Dataset_1");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createExternalDataBase(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "External Database is not created :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
    	
    	@Test(groups = {"SANITY"},priority = 51)
    	public void createTitle() throws Exception {

    		String methodName = Utils.getMethodName();    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.INFO, "<b> Verify title is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Title","Dataset_1");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createTitle(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "Title is not created :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
    	
	
    	@Test(groups = {"SANITY"},priority = 52)
    	public void createAccountTypes() throws Exception {

    		String methodName = Utils.getMethodName();
    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.INFO, "<b> Verify Account type is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "AccountTypes","Dataset_1");
    			
    			System.out.println("configMap" + configMap);
    			
    			ConfigPage.CreateAccountType(configMap);

    			//Logout from Application
    			//LoginPage.Logout();
    			//Thread.sleep(3000);
    			Utils.tearDown();

    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "Account Type is not created :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
    	
    	
    	
    	@Test(groups = {"SANITY"},priority = 53)
    	public void createCommissionCode() throws Exception {

    		String methodName = Utils.getMethodName();    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName); 
    			logger.log(LogStatus.INFO, "<b> Verify Commission Code is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CommissionCode","Dataset_1");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createCommissionCode(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "Commission Code is not created :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
	
    	
    	@Test(groups = {"SANITY"},priority = 54)
    	public void updateCommissionCode() throws Exception {

    		String methodName = Utils.getMethodName();    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName); 
    			logger.log(LogStatus.INFO, "<b> Verify Commission Code is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CommissionCode","Dataset_1");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createCommissionCode(configMap);
    			configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CommissionCode","Dataset_2");
    			System.out.println("configMap" + configMap);
    			ConfigPage.updateCommissionCode(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "Commission Code is not updated :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
    	
    	@Test(groups = {"SANITY"},priority = 55)
    	public void deleteCommissionCode() throws Exception {
    		
    		String methodName = Utils.getMethodName();    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName); 
    			logger.log(LogStatus.INFO, "<b> Verify Commission Code is getting deleted for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CommissionCode","Dataset_3");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createCommissionCode(configMap);
    			ConfigPage.deleteCommissionCode(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "Commission Code is not deleted :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
	
    	
    	
    	@Test(groups = {"SANITY"},priority = 56)
    	public void createBankAccount() throws Exception {

    		String methodName = Utils.getMethodName();    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName); 
    			logger.log(LogStatus.INFO, "<b> Verify Bank Account is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "BankAccount","Dataset_1");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createBankAccount(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "Bank account is not created :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
    	
    	
    	
    	@Test(groups = {"SANITY"},priority = 57)
    	public void updateBankAccount() throws Exception {

    		String methodName = Utils.getMethodName();    		 
    		System.out.println("methodName: "+methodName);
    		
    		try {

    			Utils.takeScreenshot(driver, methodName); 
    			logger.log(LogStatus.INFO, "<b> Verify Commission Code is getting created for a property </b>");
    			//logger = report.startTest(methodName, "Verify market code is getting created for a property").assignCategory("acceptance", "Cloud.Configuration");
    			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "BankAccount","Dataset_1");
    			System.out.println("configMap" + configMap);
    			ConfigPage.createBankAccount(configMap);
    			configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "BankAccount","Dataset_2");
    			System.out.println("configMap" + configMap);
    			ConfigPage.updateBankAccount(configMap);
    			Utils.tearDown();
    		} catch (Exception e) {
    			Utils.takeScreenshot(driver, methodName);
    			logger.log(LogStatus.FAIL, "bank Code is not updated :: Failed");
    			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
    			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
    			throw (e);
    		}
    	}
    	@Test(groups = {"BAT"},priority = 58) //,groups = { "bat" }, enabled=false
    	public void blocksStatusCodesConfig() throws Exception {
    		String testClassName = Utils.getClassName();
    		String testName = Utils.getMethodName();
    		ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
    		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CreateBlock", "Dataset_1");
    		System.out.println("Test Name: "+testName);
    		logger.log(LogStatus.INFO, "<b> verify ther status codes are having starting status and status flows in sequence </b>");
    		try {
    			ConfigPage.blocksStatusCodesFlowsConfig(configMap);
    			//logger = report.startTest(testName, "Verify Business Block can be edited").assignCategory("acceptance", "Cloud.Profile");
    			Utils.takeScreenshot(driver, testClassName);
    		} catch (Exception e) {
    			Utils.tearDown();
    			Utils.takeScreenshot(driver, testClassName);
    			logger.log(LogStatus.FAIL," verify ther status codes are having starting status and status flows in sequence is not validated"+e.getMessage());
    			throw (e);
    		}
    		Utils.tearDown();
    	}
    	 @Test(priority = 62,groups = {"SANITY"})
     	public void createPackageCodes() throws Exception {		
     		
     		String methodName = Utils.getMethodName();
     		 
     		System.out.println("methodName: "+methodName);
     		
     		try {

     			Utils.takeScreenshot(driver, methodName);
     			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a package code </b>");
     			
     			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "PackageCodes", "Dataset_1");
     			System.out.println("Config Map: " + configMap);
     			
     			Map<String, String> propertyMap = new HashMap<String, String>();
     			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
     			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
     			System.out.println("Property Config: " + propertyMap);
     			
     			String property = propertyMap.get("Property");
     			System.out.println("prop: " + property);
     			
     			configMap.put("PROPERTY", property);
     			
     			ConfigPage.createPackageCodes(configMap);
     			
     			Utils.tearDown();

     		} catch (Exception e) {
     			Utils.takeScreenshot(driver, methodName);
     			logger.log(LogStatus.FAIL, "Package Code not created :: Failed");
     			logger.log(LogStatus.FAIL, "Package occured in test due to:" + e);
     			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
     			throw (e);
     		}
     	}
    	 
    	 @Test(priority = 63,groups = {"SANITY"})
       	public void cancellationReason() throws Exception {		
       		
       		String methodName = Utils.getMethodName();
       		 
       		System.out.println("methodName: "+methodName);
       		
       		try {

       			Utils.takeScreenshot(driver, methodName);
       			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a cancellation Reason code </b>");
       			
       			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "CancelReason", "Dataset_1");
       			System.out.println("Config Map: " + configMap);
       			
       			Map<String, String> propertyMap = new HashMap<String, String>();
       			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
       			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
       			System.out.println("Property Config: " + propertyMap);
       			
       			String property = propertyMap.get("Property");
       			System.out.println("prop: " + property);
       			
       			configMap.put("PROPERTY", property);
       			
       			ConfigPage.cancellationReason(configMap);
       			
       			Utils.tearDown();

       		} catch (Exception e) {
       			Utils.takeScreenshot(driver, methodName);
       			logger.log(LogStatus.FAIL, "cancellation Reason Code not created :: Failed");
       			logger.log(LogStatus.FAIL, "cancellation Reason occured in test due to:" + e);
       			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
       			throw (e);
       		}
       	}
     	 
     	 
     	 @Test(priority = 64,groups = {"SANITY"})
       	public void CreateDepartment() throws Exception {		
       		
       		String methodName = Utils.getMethodName();
       		 
       		System.out.println("methodName: "+methodName);
       		
       		try {

       			Utils.takeScreenshot(driver, methodName);
       			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a Department  </b>");
       			
       			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Department", "Dataset_1");
       			System.out.println("Config Map: " + configMap);
       			
       			Map<String, String> propertyMap = new HashMap<String, String>();
       			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
       			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
       			System.out.println("Property Config: " + propertyMap);
       			
       			String property = propertyMap.get("Property");
       			System.out.println("prop: " + property);
       			
       			configMap.put("PROPERTY", property);
       			
       			ConfigPage.CreateDepartment(configMap);
       			
       			Utils.tearDown();

       		} catch (Exception e) {
       			Utils.takeScreenshot(driver, methodName);
       			logger.log(LogStatus.FAIL, "CreateDepartment:: Failed");
       			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
       			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
       			throw (e);
       		}
       	}
     	 
     	 
     	 @Test(priority = 65,groups = {"SANITY"})
      	public void tracesActive() throws Exception {		
      		
      		String methodName = Utils.getMethodName();
      		 
      		System.out.println("methodName: "+methodName);
      		
      		try {

      			Utils.takeScreenshot(driver, methodName);
      			logger.log(LogStatus.INFO, "<b> Verify if user is able to activate the Traces</b>");
      			
      			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Traces", "Dataset_1");
      			System.out.println("Config Map: " + configMap);
      			
      			Map<String, String> propertyMap = new HashMap<String, String>();
      			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
      			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
      			System.out.println("Property Config: " + propertyMap);
      			
      			String property = propertyMap.get("Property");
      			System.out.println("prop: " + property);
      			
      			configMap.put("PROPERTY", property);
      			
      			ConfigPage.tracesActive(configMap);
      			
      			Utils.tearDown();

      		} catch (Exception e) {
      			Utils.takeScreenshot(driver, methodName);
      			logger.log(LogStatus.FAIL, "tracesActive:: Failed");
      			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
      			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
      			throw (e);
      		}
      	}
     	 
     	 @Test(priority = 66,groups = {"SANITY"})
        	public void createAlertMessage() throws Exception {		
        		
        		String methodName = Utils.getMethodName();
        		 
        		System.out.println("methodName: "+methodName);
        		
        		try {

        			Utils.takeScreenshot(driver, methodName);
        			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a Alert messages  </b>");
        			
        			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reservation_Alerts", "Dataset_1");
        			System.out.println("Config Map: " + configMap);
        			
        			Map<String, String> propertyMap = new HashMap<String, String>();
        			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
        			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
        			System.out.println("Property Config: " + propertyMap);
        			
        			String property = propertyMap.get("Property");
        			System.out.println("prop: " + property);
        			
        			configMap.put("PROPERTY", property);
        			
        			ConfigPage.createAlertMessage(configMap);
        			
        			Utils.tearDown();

        		} catch (Exception e) {
        			Utils.takeScreenshot(driver, methodName);
        			logger.log(LogStatus.FAIL, "createAlertMessage:: Failed");
        			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
        			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
        			throw (e);
        		}
        	}
     	 @Test(priority = 67,groups = {"SANITY"}, enabled=false)
         	public void createGlobalAlertDefinitions() throws Exception {		
         		
         		String methodName = Utils.getMethodName();
         		 
         		System.out.println("methodName: "+methodName);
         		
         		try {

         			Utils.takeScreenshot(driver, methodName);
         			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a global alert definations  </b>");
         			
         			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "GlobalAlertDefinitions", "Dataset_1");
         			System.out.println("Config Map: " + configMap);
         			
         			Map<String, String> propertyMap = new HashMap<String, String>();
         			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
         			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
         			System.out.println("Property Config: " + propertyMap);
         			
         			String property = propertyMap.get("Property");
         			System.out.println("prop: " + property);
         			
         			configMap.put("PROPERTY", property);
         			
         			ConfigPage.createGlobalAlertDefinitions(configMap);
         			
         			Utils.tearDown();

         		} catch (Exception e) {
         			Utils.takeScreenshot(driver, methodName);
         			logger.log(LogStatus.FAIL, "createGlobalAlertDefinitions :: Failed");
         			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
         			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
         			throw (e);
         		}
         	}
     	 @Test(priority = 68,groups = {"SANITY"})
       	public void activateAlerts() throws Exception {		
       		
       		String methodName = Utils.getMethodName();
       		 
       		System.out.println("methodName: "+methodName);
       		
       		try {

       			Utils.takeScreenshot(driver, methodName);
       			logger.log(LogStatus.INFO, "<b> Verify if user is able to activate the Alerts</b>");
       			
       			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reservation_Alerts", "Dataset_1");
       			System.out.println("Config Map: " + configMap);
       			
       			Map<String, String> propertyMap = new HashMap<String, String>();
       			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
       			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
       			System.out.println("Property Config: " + propertyMap);
       			
       			String property = propertyMap.get("Property");
       			System.out.println("prop: " + property);
       			
       			configMap.put("PROPERTY", property);
       			
       			ConfigPage.activateAlerts(configMap);
       			
       			Utils.tearDown();

       		} catch (Exception e) {
       			Utils.takeScreenshot(driver, methodName);
       			logger.log(LogStatus.FAIL, "activateAlerts:: Failed");
       			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
       			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
       			throw (e);
       		}
       	}
     	 
     	 @Test(priority = 69,groups = {"SANITY"})
         	public void createNoteTypes() throws Exception {		
         		
         		String methodName = Utils.getMethodName();
         		 
         		System.out.println("methodName: "+methodName);
         		
         		try {

         			Utils.takeScreenshot(driver, methodName);
         			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a Reservation with Note TYpe  </b>");
         			
         			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "NoteTypes", "Dataset_1");
         			System.out.println("Config Map: " + configMap);
         			
         			Map<String, String> propertyMap = new HashMap<String, String>();
         			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
         			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
         			System.out.println("Property Config: " + propertyMap);
         			
         			String property = propertyMap.get("Property");
         			System.out.println("prop: " + property);
         			
         			configMap.put("PROPERTY", property);
         			
         			ConfigPage.createNoteTypes(configMap);
         			
         			Utils.tearDown();

         		} catch (Exception e) {
         			Utils.takeScreenshot(driver, methodName);
         			logger.log(LogStatus.FAIL, "createNoteTypes :: Failed");
         			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
         			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
         			throw (e);
         		}
         	}
     	 
     	 @Test(priority = 70,groups = {"SANITY"})
      	public void createItemInventory() throws Exception {		
      		
      		String methodName = Utils.getMethodName();
      		 
      		System.out.println("methodName: "+methodName);
      		
      		try {

      			Utils.takeScreenshot(driver, methodName);
      			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a Reservation with Item Inventory  </b>");
      			
      			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ItemInventory", "Dataset_1");
      			System.out.println("Config Map: " + configMap);
      			
      			Map<String, String> propertyMap = new HashMap<String, String>();
      			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
      			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
      			System.out.println("Property Config: " + propertyMap);
      			
      			String property = propertyMap.get("Property");
      			System.out.println("prop: " + property);
      			
      			configMap.put("PROPERTY", property);
      			
      			ConfigPage.createItemInventory(configMap);
      			
      			Utils.tearDown();

      		} catch (Exception e) {
      			Utils.takeScreenshot(driver, methodName);
      			logger.log(LogStatus.FAIL, "createItemInventory :: Failed");
      			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
      			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
      			throw (e);
      		}
      	}
    	 
    	
	/**************************************************************************************************************************************************************************************************/

	/*
	 * Below is the Dummy method for the reference to send the browserName and URL from the test method to the beforeMethod
	 * This can be used only when we have another URL or browser to be accesses other than the Target Test Could environment
	 * Example given in the DataProvider method getdData is sending browser name as Chrome and url as google.com 
	 * IF user send URL and BrowserName using DataProvider, They have to call the Login method in their test Method. 
	 * BeforeMethod will just navigate to the URL in the provided browser as the Xpaths for Login could be different in other environments (Apart from OPERA Cloud URLs)
	 */
	@Test(enabled = false,dataProvider = "getData")
	public void dummy(String strBrowserName, String strURL) {
		
		//Login Method should be called
		//Test validation Logic
	}
	
	@DataProvider
	    public Object[][] getData() {
	        return new Object[][]{
	                {"Chrome", "http://google.com"}	                
	        };
	    }
	
}
