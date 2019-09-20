package com.oracle.hgbu.opera.qaauto.ui.OXI.component.OXIAuto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.ElementNotVisibleException;

import com.aventstack.extentreports.Status;
import com.itextpdf.text.log.Logger;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.relevantcodes.extentreports.LogStatus;

public class WeblogicandOSBPage extends Utils {
	
	static Map<String,String> configMap=new HashMap<String,String>();

	public static void validateWeblogiclogin(){
		
		Utils.waitForPageLoad(5000);
		try {
			Utils.WebdriverWait(5000, "weblogic.home", "visible");
			logger.log(LogStatus.PASS, "WebLogic home page landed");	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.log(LogStatus.ERROR, "Not able to launch WebLogic Home Page");
		}
		}
	
public static void validateOsbLogin() {
	Utils.waitForPageLoad(5000);
	try {
		Utils.WebdriverWait(5000, "osb.home", "visible");
		logger.log(LogStatus.PASS, "Service Bus home page landed");	
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		logger.log(LogStatus.ERROR, "Not able to launch Service Bus Home Page");
	}
}

public static void weblogicLogin() throws Exception {
	
	String testName=Utils.getMethodName();
	logger=report.startTest(testName);
	configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
	configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configMap.get("Set"));
	Utils.implicitWait(5);
	Utils.textBox("weblogic.username", configMap.get("weblogic_username"));
	logger.log(LogStatus.PASS, "Entered Username");
	
	Utils.textBox("weblogic.password", configMap.get("weblogic_password"));
	logger.log(LogStatus.PASS, "Entered Password");
	
	Utils.click("weblogic.login");
	logger.log(LogStatus.PASS, "Clicked on Login");
}
public static void osbLogin() throws Exception {
	String testName=Utils.getMethodName();
	logger=report.startTest(testName);
	configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
	configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configMap.get("Set"));
	Utils.implicitWait(5);
	Utils.textBox("osb.username", configMap.get("osb_username"));
	logger.log(LogStatus.PASS, "Entered Username ");
	
	Utils.textBox("osb.password", configMap.get("osb_password"));
	logger.log(LogStatus.PASS, "Entered Password ");
	
	Utils.click("osb.login");
	logger.log(LogStatus.PASS, "Clicked on Login");
}

public static void weblogicLogout() {
	try {
		Utils.click("weblogic.logout");
		logger.log(LogStatus.PASS, "Clicked on Logout");
		Utils.implicitWait(5);
		if(Utils.isExists("weblogic.login")){
			logger.log(LogStatus.PASS, "Logged out of the application successfully");
		}
		else {
			logger.log(LogStatus.FAIL, "Not able to log out of the application successfully");
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		logger.log(LogStatus.FAIL, "Not able to click on Logout"+e.getMessage());
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.log(LogStatus.FAIL, "Not able to click on Logout" + e.getMessage());
		e.printStackTrace();
	}
	try {
		driver.quit();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
public static void osbLogout() {
	try {
		Utils.implicitWait(5);
		Utils.click("osb.weblogic");
		Utils.click("osb.logout");
		logger.log(LogStatus.PASS, "Clicked on Logout sucessfully");
		Utils.implicitWait(15);
		if(Utils.isExists("osb.login")) {
			logger.log(LogStatus.PASS, "Logged out of the application successfully");
		}
		else {
			logger.log(LogStatus.FAIL, "Not able to log out of the application successfully");
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		logger.log(LogStatus.FAIL, "Not able to  Logout sucessfully"+ e.getMessage());
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.log(LogStatus.FAIL, "Not able to  Logout sucessfully"+ e.getMessage());
		e.printStackTrace();
	}
	driver.quit();
}
}
