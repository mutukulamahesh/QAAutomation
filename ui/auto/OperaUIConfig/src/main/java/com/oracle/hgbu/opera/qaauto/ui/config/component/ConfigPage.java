package com.oracle.hgbu.opera.qaauto.ui.config.component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import com.oracle.hgbu.opera.qaauto.ui.utilities.BaseClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class ConfigPage extends Utils {

	/*******************************************************************
	-  Description: Creating Source Code as the part of Configuration 
	- Input: SOURCE_CODE, DESCRIPTION, SOURCE_GROUP,  DISPLAY_SEQ 


	- Output: SOURCE_CODE
	- Author: mahesh
	- Date:05/12/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: mahesh

	 ********************************************************************/
	public static void sourceCode(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Navigating to Booking menu
			click("Configuration.AdminMenu_Booking", 100, "clickable");

			// Navigating to Marketing Management menu
			click("Configuration.menu_MarketingManagement", 100, "clickable");

			// Navigating to Source Code

			click("Configuration.menu_SourceCode", 100, "clickable");

			waitForPageLoad(100);
			// Validating Source Codes Page
			WebdriverWait(100, "Configuration.header_SourceCodes", "presence");
			verifyCurrentPage("Configuration.header_SourceCodes", "header_SourceCodes");
			logger.log(LogStatus.PASS, "Landed in Source Code page and Validated");

			List<WebElement>  rows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("SOURCE_CODE")))
					{
						System.out.println("Source Group "+rows.get(i).getText()+" is already existing");
						flag = true;
						logger.log(LogStatus.PASS, "Source Group "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
						System.out.println("Source Group is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}


			if(!flag)
			{
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Select Template Tab
				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				Utils.jsClick("Configuration.tab_Template");
				System.out.println("clicked Teplate Tab");
				logger.log(LogStatus.PASS, "Selected Template tab");

				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				String Temp = getText("Configuration.tab_Template");
				System.out.println(Temp);

				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				mouseHover("Configuration.tab_Template");

				/*Thread.sleep(3000);
				waitForPageLoad(100);
				//Search in Template Tab
				WebdriverWait(100, "Configuration.txt_SearchSourceCode_SourceCode", "clickable");
				jsTextbox("Configuration.txt_SearchSourceCode_SourceCode",configMap.get("SOURCE_CODE"));
				System.out.println("Provided source group to search in Teplate Tab");
				logger.log(LogStatus.PASS, "Provided source group to search  tab");

				//Search button
				Utils.WebdriverWait(100, "Configuration.btn_Template_Search", "clickable");
				Utils.click("Configuration.btn_Template_Search");
				System.out.println("clicked searcsh in Teplate Tab");
				logger.log(LogStatus.PASS, "Selected Search in Template tab");*/

				Thread.sleep(3000);
				//Fetch Search Grid Column Data and validate
				rows = Utils.elements("Configuration.grd_TemplateSourceCodeSearch_ColData"); 
				System.out.println("No of rows are : " + rows.size());
				flag = false;
				if(rows.size() > 0)
				{
					for(int i=0;i<rows.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
						if(rows.get(i).getText().equalsIgnoreCase(configMap.get("SOURCE_CODE")))
						{
							System.out.println("Source Group "+rows.get(i).getText()+" Template is already existing");
							flag = true;
							logger.log(LogStatus.PASS, "Source Code "+rows.get(i).getText()+" Template is already existing");
							break;
						}
						else
						{
							System.out.println("Source Code Template is not present in the system:: "+rows.get(i).getText());						
						}
					}
				}
				if(!flag){
					waitForPageLoad(100);
					Thread.sleep(3000);
					//Select More Menu for New
					Utils.WebdriverWait(100, "Configuration.menu_CreateMarketCode_More", "clickable");
					Utils.jsClick("Configuration.menu_CreateMarketCode_More");
					System.out.println("clicked More menu for new");
					logger.log(LogStatus.PASS, "Selected More Menu for New");

					//Select New
					Utils.WebdriverWait(100, "Configuration.link_CreateMarketCode_New", "clickable");
					Utils.jsClick("Configuration.link_CreateMarketCode_New");
					System.out.println("clicked new");
					logger.log(LogStatus.PASS, "Selected New in the Menu");



					waitForPageLoad(100);
					// Validating Source Code Page
					Utils.WebdriverWait(100, "Configuration.header_CreateSourceCode_validation", "presence");
					Utils.verifyCurrentPage("Configuration.header_CreateSourceCode_validation", "header_CreateSourceCode_validation");
					logger.log(LogStatus.PASS, "Landed in Source Code Creation page and Validated");

					waitForPageLoad(100);
					Thread.sleep(3000);
					//Provide SourceCode
					Utils.WebdriverWait(100, "Configuration.txt_CreateSourceCode_SourceCode", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateSourceCode_SourceCode",configMap.get("SOURCE_CODE"));   // SourceCode
					System.out.println("Provide SourceCode");
					logger.log(LogStatus.PASS, "Provided Source Code");

					waitForPageLoad(100);
					//Provided Description
					Utils.WebdriverWait(100, "Configuration.txt_CreateSourceCode_Desc", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateSourceCode_Desc",configMap.get("DESCRIPTION"));   // Description
					System.out.println("Provide Description for Source Code");
					logger.log(LogStatus.PASS, "Provided Description for Source Group");

					waitForPageLoad(100);
					//Provided Source Group
					Utils.WebdriverWait(100, "Configuration.txt_CreateSourceCode_SourceGroup", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateSourceCode_SourceGroup",configMap.get("SOURCE_GROUP"));   // SourceGroup
					System.out.println("Provide Source Group for Source Code");
					logger.log(LogStatus.PASS, "Provided Source Group for Source Code");

					waitForPageLoad(100);
					//Provided Sequence
					Utils.WebdriverWait(100, "Configuration.txt_CreateMarketCode_Sequence", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateMarketCode_Sequence",configMap.get("DISPLAY_SEQ"));   // Sequence
					System.out.println("Provide Sequence for Source Code");
					logger.log(LogStatus.PASS, "Provided Sequence for Source Code");



					waitForPageLoad(100);
					//Select Save
					Utils.WebdriverWait(100, "Configuration.btn_CreateMarketCode_Save", "clickable");
					Utils.jsClick("Configuration.btn_CreateMarketCode_Save");
					System.out.println("clicked Save");
					logger.log(LogStatus.PASS, "Selected Save");
					waitForPageLoad(100);

					Thread.sleep(3000);
					//Search in Template Tab
					WebdriverWait(100, "Configuration.txt_SearchSourceCode_SourceCode", "clickable");
					jsTextbox("Configuration.txt_SearchSourceCode_SourceCode",configMap.get("SOURCE_CODE"));
					System.out.println("Provided source group to search in Teplate Tab");
					logger.log(LogStatus.PASS, "Provided source code to search  tab");

					//Search button
					Utils.WebdriverWait(100, "Configuration.btn_Template_Search", "clickable");

					Utils.click("Configuration.btn_Template_Search");
					System.out.println("clicked searcsh in Teplate Tab");
					logger.log(LogStatus.PASS, "Selected Search in Template tab");
				}

				//Page Validation
				waitForPageLoad(100);
				// Validating Source Group Page
				Utils.WebdriverWait(100, "Configuration.header_SearchValidation", "presence");
				Utils.verifyCurrentPage("Configuration.header_SearchValidation", "header_SearchValidation");
				System.out.println("Landed in Source code Search and Validated");
				logger.log(LogStatus.PASS, "Landed in Source Group Search and Validated");

				//Search in Template Tab
				WebdriverWait(100, "Configuration.txt_SearchSourceCode_SourceCode", "clickable");
				jsTextbox("Configuration.txt_SearchSourceCode_SourceCode",configMap.get("SOURCE_CODE"));
				System.out.println("Provided source group to search in Teplate Tab");
				logger.log(LogStatus.PASS, "Provided source group to search  tab");

				//Search button
				//Utils.WebdriverWait(100, "Configuration.btn_Template_Search", "clickable");
				Utils.Wait(2000);
				jsClick("Configuration.btn_Template_Search");
				//Utils.click("Configuration.btn_Template_Search");


				waitForPageLoad(100);
				//Select More Menu 
				Utils.WebdriverWait(100, "Configuration.menu_SearchMarketCode_More", "clickable");
				Utils.jsClick("Configuration.menu_SearchMarketCode_More");
				System.out.println("clicked More menu ");
				logger.log(LogStatus.PASS, "Selected More Menu");

				//Select Copy link 
				Utils.WebdriverWait(100, "Configuration.link_CopyMarketCode_Copy", "clickable");
				Utils.jsClick("Configuration.link_CopyMarketCode_Copy");
				System.out.println("clicked Copy link ");
				logger.log(LogStatus.PASS, "Selected Copy Link");

				//Copy Page Validation
				waitForPageLoad(100);
				// Validating Market Codes Page
				Utils.WebdriverWait(100, "Configuration.header_CopySourceCode_Validation", "presence");
				Utils.verifyCurrentPage("Configuration.header_CopySourceCode_Validation", "header_CopySourceCode_Validation");
				logger.log(LogStatus.PASS, "Landed in SourceCode Copy Page and Validated");

				Thread.sleep(3000);
				//Provide Market Group
				Utils.WebdriverWait(100, "Configuration.txt_CopySourceCode_SourceCode", "clickable");
				Utils.jsTextbox("Configuration.txt_CopySourceCode_SourceCode",configMap.get("SOURCE_CODE"));   // SourceCode
				System.out.println("Provide Source Code to Copy");
				logger.log(LogStatus.PASS, "Provided Source Code for Copy");

				//Select Save
				Utils.WebdriverWait(100, "Configuration.btn_CopyMarketCode_Save", "clickable");
				Utils.jsClick("Configuration.btn_CopyMarketCode_Save");
				System.out.println("clicked Save to Copy");
				logger.log(LogStatus.PASS, "Selected Save to Copy");

				waitForPageLoad(100);

				//Select Copy and Continue
				Utils.WebdriverWait(100, "Configuration.btn_CopyMarketCode_CopyAndContinue", "clickable");
				Utils.jsClick("Configuration.btn_CopyMarketCode_CopyAndContinue");
				System.out.println("clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Selected Copy and Continue button");

				//Validation for Copying Market Code 

				if (getText("Configuration.txt_CopyMarketCode_StatusMessage").contains(OR.getTestData("txt_CopyMarketCode_StatusMessage"))) {
					System.out.println("Source Code Created and Copied Successfully " );
					logger.log(LogStatus.PASS,
							"Source Code Created and Copied Successfully " );
				} else {
					System.out.println("Source Code Not Created and Copied Successfully ");
					logger.log(LogStatus.FAIL, "Source Code Not Created and Copied Successfully ");
				}
			}



		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Source Code  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Source Code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);


		}
	}

	/*******************************************************************
	-  Description: Creating Source Group as the part of Configuration 
	- Input: GROUP_CODE, DESCRIPTION, DISPLAY_SEQ 
	- Output: SOURCE GROUP CODE
	- Author: mahesh
	- Date:04/12/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: mahesh
	 ********************************************************************/
	public static void sourceGroup(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Navigating to Booking menu
			click("Configuration.AdminMenu_Booking", 100, "clickable");

			// Navigating to Marketing Management menu
			click("Configuration.menu_MarketingManagement", 100, "clickable");

			// Navigating to Source Group
			click("Configuration.menu_SourceGroup", 100, "clickable");


			waitForPageLoad(100);
			// Validating Source Group Page

			WebdriverWait(100, "Configuration.header_SourceGroups", "presence");
			verifyCurrentPage("Configuration.header_SourceGroups", "header_SourceGroups");
			logger.log(LogStatus.PASS, "Landed in Source Group page and Validated");


			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.grd_SourceGroupSearch_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("GROUP_CODE")))
					{
						System.out.println("Source Group "+rows.get(i).getText()+" is already existing");
						flag = true;
						logger.log(LogStatus.PASS, "Source Group "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
						System.out.println("Source Group is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}
			if(!flag)
			{
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Select Template Tab
				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				Utils.jsClick("Configuration.tab_Template");
				System.out.println("clicked Teplate Tab");
				logger.log(LogStatus.PASS, "Selected Template tab");

				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				String Temp = getText("Configuration.tab_Template");
				System.out.println(Temp);

				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				mouseHover("Configuration.tab_Template");

				Thread.sleep(3000);
				//Fetch Search Grid Column Data and validate
				rows = Utils.elements("Configuration.grd_TemplateCodeSearch_ColData"); 
				System.out.println("No of rows are : " + rows.size());
				flag = false;
				if(rows.size() > 0)
				{
					for(int i=0;i<rows.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
						if(rows.get(i).getText().equalsIgnoreCase(configMap.get("GROUP_CODE")))
						{
							System.out.println("Source Group "+rows.get(i).getText()+" Template is already existing");
							flag = true;
							logger.log(LogStatus.PASS, "Source Group "+rows.get(i).getText()+" Template is already existing");
							break;
						}
						else
						{
							System.out.println("Source Group Template is not present in the system:: "+rows.get(i).getText());						
						}
					}
				}
				if(!flag){
					waitForPageLoad(100);
					Thread.sleep(3000);
					//Select More Menu for New
					Utils.WebdriverWait(100, "Configuration.menu_More", "clickable");
					Utils.jsClick("Configuration.menu_More");
					System.out.println("clicked More menu for new");
					logger.log(LogStatus.PASS, "Selected More Menu for New");

					//Select New
					Utils.WebdriverWait(100, "Configuration.link_New", "clickable");
					Utils.jsClick("Configuration.link_New");
					System.out.println("clicked new");
					logger.log(LogStatus.PASS, "Selected New in the Menu");

					/*waitForPageLoad(100);
					// Validating Source Code Page
					Utils.WebdriverWait(100, "Configuration.header_validation", "presence");
					Utils.verifyCurrentPage("Configuration.header_validation", "header_validation");
					logger.log(LogStatus.PASS, "Landed in Source Group Creation page and Validated");*/

					waitForPageLoad(100);
					Thread.sleep(3000);
					//Provide Source Group
					Utils.WebdriverWait(100, "Configuration.txt_CopySourceGroup_SourceCode", "clickable");
					Utils.jsTextbox("Configuration.txt_CopySourceGroup_SourceCode",configMap.get("GROUP_CODE"));   // SourceGroup
					System.out.println("Provided Source Group");
					logger.log(LogStatus.PASS, "Provided Source Group");

					waitForPageLoad(100);
					//Provided Description
					Utils.WebdriverWait(100, "Configuration.txt_CreateSourceGroup_Desc", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateSourceGroup_Desc",configMap.get("DESCRIPTION"));   // Description
					System.out.println("Provide Description for Source Group");
					logger.log(LogStatus.PASS, "Provided Description for Source Group");			

					waitForPageLoad(100);
					//Provided Sequence
					Utils.WebdriverWait(100, "Configuration.txt_Sequence", "clickable");
					Utils.jsTextbox("Configuration.txt_Sequence",configMap.get("DISPLAY_SEQ"));   // Sequence
					System.out.println("Provide Sequence for Source Code");
					logger.log(LogStatus.PASS, "Provided Sequence for Source Group");

					waitForPageLoad(100);
					//Select Save
					Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
					Utils.jsClick("Configuration.btn_Save");
					System.out.println("clicked Save");
					logger.log(LogStatus.PASS, "Selected Save");
					waitForPageLoad(100);

					Thread.sleep(3000);

					//					waitForPageLoad(100);
					//					// Validating Source Group Page
					//					Utils.WebdriverWait(100, "Configuration.header_SearchValidation", "presence");
					//					Utils.verifyCurrentPage("Configuration.header_SearchValidation", "header_SearchValidation");
					//					logger.log(LogStatus.PASS, "Landed in Source Group Search and Validated");
					//					
					//					//Search in Template Tab
					//					WebdriverWait(100, "Configuration.txt_Template_Search", "clickable");
					//					jsTextbox("Configuration.txt_Template_Search",configMap.get("GROUP_CODE"));
					//					System.out.println("Provided source group to search in Teplate Tab");
					//					logger.log(LogStatus.PASS, "Provided source group to search  tab");
					//					
					//					//Search button
					//
					//					//Utils.WebdriverWait(100, "Configuration.btn_Template_Search", "clickable");
					//					Utils.click("Configuration.btn_Template_Search");
					//					System.out.println("clicked searcsh in Teplate Tab");
					//					logger.log(LogStatus.PASS, "Selected Search in Template tab");
				}
				//Page Validation
				waitForPageLoad(100);
				// Validating Source Group Page
				Utils.WebdriverWait(100, "Configuration.header_SearchValidation", "presence");
				Utils.verifyCurrentPage("Configuration.header_SearchValidation", "header_SearchValidation");
				logger.log(LogStatus.PASS, "Landed in Source Group Search and Validated");

				waitForPageLoad(100);
				//Provide Source Group  
				//Utils.WebdriverWait(100, "Configuration.txt_Template_Search", "clickable");

				Utils.jsTextbox("Configuration.txt_Template_Search",configMap.get("GROUP_CODE"));   // SourceGroup
				System.out.println("Provided Source Group");
				logger.log(LogStatus.PASS, "Provided Source Group");

				//Select Search
				Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");

				Utils.jsClick("Configuration.btn_Search");
				System.out.println("clicked Search");
				logger.log(LogStatus.PASS, "Selected Search");

				waitForPageLoad(100);
				//Select More Menu 

				Utils.WebdriverWait(100, "Configuration.menu_More", "clickable");
				Utils.jsClick("Configuration.menu_More");
				System.out.println("clicked More menu ");
				logger.log(LogStatus.PASS, "Selected More Menu");

				//Select Copy link 
				Utils.WebdriverWait(100, "Configuration.link_Copy", "clickable");

				Utils.jsClick("Configuration.link_Copy");
				System.out.println("clicked Copy link ");
				logger.log(LogStatus.PASS, "Selected Copy Link");

				//Copy Page Validation
				waitForPageLoad(100);
				// Validating Source Group Page
				Utils.WebdriverWait(100, "Configuration.header_CopySourceGroup_Validation", "presence");
				Utils.verifyCurrentPage("Configuration.header_CopySourceGroup_Validation", "header_CopySourceGroup_Validation");
				logger.log(LogStatus.PASS, "Landed in Source Group Copy Page and Validated");

				Thread.sleep(3000);
				//Provide Source Group
				Utils.WebdriverWait(100, "Configuration.txt_CopySourceGroup_SourceCode", "clickable");
				Utils.jsTextbox("Configuration.txt_CopySourceGroup_SourceCode",configMap.get("GROUP_CODE"));   // SourceGroup
				System.out.println("Provide Source Group to Copy");

				logger.log(LogStatus.PASS, "Provided Source Group for Copy");

				//Select Save
				Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
				Utils.jsClick("Configuration.btn_Save");
				System.out.println("clicked Save to Copy");

				logger.log(LogStatus.PASS, "Selected Save to Copy");

				waitForPageLoad(100);

				//Select Copy and Continue
				Utils.WebdriverWait(100, "Configuration.btn_CopyAndContinue", "clickable");
				Utils.jsClick("Configuration.btn_CopyAndContinue");
				System.out.println("clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Selected Copy and Continue button");

				//Validation for Copying Source Group 

				if (getText("Configuration.txt_StatusMessage").contains(OR.getTestData("txt_StatusMessage"))) {
					System.out.println("Source Group Created and Copied Successfully " );

					logger.log(LogStatus.PASS,
							"Source Group Created and Copied Successfully " );
				} else {
					System.out.println("Source Group Not Created and Copied Successfully ");
					logger.log(LogStatus.FAIL, "Source Group Not Created and Copied Successfully ");
				}
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Source Group  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Source Group not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}


	public static void configNavigation() throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");
			waitForSpinnerToDisappear(100);
			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");
			waitForSpinnerToDisappear(100);
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	- Description: This method updates the Owner code for a particular user
	- Input: configMap- Test Data row for Create Owner
	- Output:
	- Author:Chittranjan
	- Date:11/28/2018
	- Revision History:0.1
	 ********************************************************************/
	public static void createOwner(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Navigating to Role manager
			click("Configuration.RoleManagerMenu",100, "clickable");

			// Navigating to Manage Users
			click("Configuration.RoleManagerMenu.ManageUsers", 100, "clickable");
			waitForSpinnerToDisappear(50);

			// Entering the User ID
			textBox("Configuration.RoleManagerMenu.UserId", configMap.get("UserName"), 100, "clickable");

			// click on Search Button
			click("Configuration.RoleManagerMenu.SearchButton", 100, "clickable");
			waitForSpinnerToDisappear(50);

			// Validating if User is loaded
			Assert.assertTrue(getText("Configuration.RoleManagerMenu.SearchedUserId", 100, "presence").equalsIgnoreCase(configMap.get("UserName")));

			// Clicking on Action Button
			click("Configuration.RoleManagerMenu.ActionButton", 100, "presence");
			waitForSpinnerToDisappear(10);

			// Clicking on Edit Button
			click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
			waitForSpinnerToDisappear(50);

			// Verifying Owners code
			String ownersCode=getAttributeOfElement("Configuration.RoleManagerMenu.OwnerCode", "value", 100, "presence");
			if (ownersCode.equals("")){
				String userName=  configMap.get("UserName").contains(".")?configMap.get("UserName").replace(".",""):configMap.get("UserName");
				textBox("Configuration.RoleManagerMenu.OwnerCode",userName, 100, "presence");
				click("Configuration.RoleManagerMenu.SaveButton", 100, "presence");
				WebdriverWait(100, "Configuration.RoleManagerMenu.ActionButton", "presence");
			}else{
				System.out.println("Owners code was updated with "+ownersCode);
				logger.log(LogStatus.PASS, "Owners code was updated with "+ownersCode);
			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Owner  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Owner not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}


	/*******************************************************************
	- Description: This method creates Event Types
	- Input: configMap- Test Data row for Create Owner
	- Output:
	- Author:Chittranjan
	- Date:11/28/2018
	- Revision History:0.1
	 ********************************************************************/
	public static void createEventType(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");
			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on EventType Button
			click("Configuration.FunctionSpaceMngt.EventType", 100, "clickable");
			waitForSpinnerToDisappear(30);

			//Entering the EventCode to search it
			List<WebElement> allEventType=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
			if (allEventType.size()==0){
				throw new Exception();
			}
			boolean elementExist=false;
			for (WebElement eachEvent:allEventType ){
				if(eachEvent.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("EventCode"))){
					elementExist=true;
					break;
				}
			}
			// Verifying if event already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for event already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new Event
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(30);

				// Entering the EventCode
				textBox("Configuration.FunctionSpaceMngt.EventType.EventCode", configMap.get("EventCode"), 0,
						"presence");
				click("Configuration.FunctionSpaceMngt.EventType.LabelRequiredField");
				waitForSpinnerToDisappear(30);

				// Entering the EventCode
				textBox("Configuration.FunctionSpaceMngt.EventType.EventDescriptionField",
						configMap.get("EventDescription"), 0, "presence");

				click("Configuration.FunctionSpaceMngt.EventType.LabelRequiredField");
				waitForSpinnerToDisappear(30);

				// Entering the EventCode
				if (!configMap.get("EventStartTime").equals("null")){
					textBox("Configuration.FunctionSpaceMngt.EventType.EventStartTimeField",
							configMap.get("EventStartTime"), 0, "presence");
				}

				// Entering the EventCode
				if (!configMap.get("EventEndTime").equals("null")){
					textBox("Configuration.FunctionSpaceMngt.EventType.EventEndTimeField", configMap.get("EventEndTime"),
							100, "presence");
				}

				if (!configMap.get("EventSequence").equals("null")){
					// Entering the EventCode
					textBox("Configuration.FunctionSpaceMngt.EventType.EventSeqField", configMap.get("EventSequence"), 100,
							"presence");
				}

				click("Configuration.FunctionSpaceMngt.EventType.SaveEvent", 100, "clickable");
				waitForSpinnerToDisappear(40);

				logger.log(LogStatus.PASS, "The configuration event has been created :: Passed");
			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create marketGroup  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "marketGroup not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}



	/*******************************************************************
	-  Description: Create/Verify Out of service/order room reasons
	- Input:Room reasons code,Description & sequence
	- Output: Room reasons code
	- Author: Girish
	- Date: 29/11/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void outOfOrderReason(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Inventory menu
			WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Configuration.menu_Inventory");
			click(Utils.element("Configuration.menu_Inventory"));
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_AccommodationManagement", "clickable");
			mouseHover("Configuration.menu_AccommodationManagement");
			jsClick("Configuration.menu_AccommodationManagement");
			System.out.println("clicked Accommodation Management menu");
			logger.log(LogStatus.PASS, "Selected Accommodation Management Menu");

			// Navigating to Room Class  
			WebdriverWait(100, "Configuration.lnk_OutOfOrderServiceReason", "clickable");
			jsClick("Configuration.lnk_OutOfOrderServiceReason");
			System.out.println("clicked Out of Order / Service Reasons menu");
			logger.log(LogStatus.PASS, "clicked Out of Order / Service Reasons Menu");
			waitForPageLoad(500);

			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.tbl_Property"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if((rows.get(i).getText().trim()).equalsIgnoreCase(configMap.get("CONDITIONCODE")))
					{					    
						flag = true;
						logger.log(LogStatus.PASS, "Out of Order / Service Code "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
						System.out.println("Out of Order / Service Code is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}
			if(!flag)
			{
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Select Template Tab
				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				Utils.jsClick("Configuration.tab_Template");
				System.out.println("clicked Teplate Tab");
				logger.log(LogStatus.PASS, "Selected Template tab");


				//Fetch Search Grid Column Data and validate
				waitForPageLoad(100);
				Thread.sleep(3000);				
				java.util.List<WebElement>  rowData = Utils.elements("Configuration.tbl_Template"); 
				System.out.println("No of rows are : " + rowData.size());
				flag = false;
				if(rowData.size() > 0)
				{
					for(int i=0;i<rowData.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rowData.get(i));
						if((rowData.get(i).getText().trim()).equalsIgnoreCase(configMap.get("CONDITIONCODE")))
						{
							System.out.println("Out of Order / Service Code "+rowData.get(i).getText()+" Template is already existing");
							flag = true;
							logger.log(LogStatus.PASS, "Out of Order / Service Code "+rowData.get(i).getText()+" Template is already existing");
							break;
						}
						else
						{
							System.out.println("Out of Order / Service Code Template is not present in the system:: "+rowData.get(i).getText());						
						}
					}
				}
				if(!flag){
					waitForPageLoad(5000);			
					//Select New
					Utils.WebdriverWait(5000, "Configuration.link_New", "clickable");
					Utils.click("Configuration.link_New");
					System.out.println("clicked new");
					logger.log(LogStatus.PASS, "Clicked on New");	
					Thread.sleep(5000);

					boolean bExists = Utils.isExists("Configuration.win_OutOfOrderServiceReason");
					if(bExists){
						//Provide Room Class
						Utils.WebdriverWait(10000, "Configuration.txt_Code", "clickable");
						Utils.textBox("Configuration.txt_Code",configMap.get("CONDITIONCODE"));   // Reasons code
						System.out.println("Provided Out of Order / Service Code");
						logger.log(LogStatus.PASS, "Provided Out of Order / Service Code");

						waitForPageLoad(1000);
						//Provided Description
						Utils.WebdriverWait(5000, "Configuration.txt_Description", "clickable");
						Utils.jsTextbox("Configuration.txt_Description",configMap.get("DESCRIPTION"));   // Description
						System.out.println("Provide Description for Out of Order / Service Code");
						logger.log(LogStatus.PASS, "Provided Description for Out of Order / Service Code");			

						waitForPageLoad(1000);
						//Provided Sequence
						Utils.WebdriverWait(100, "Configuration.txt_Seqn", "clickable");
						Utils.jsTextbox("Configuration.txt_Seqn",configMap.get("SEQUENCENO"));   // Sequence
						System.out.println("Provide Sequence for Room Reasons");
						logger.log(LogStatus.PASS, "Provided Sequence for Out of Order / Service Code");

						waitForPageLoad(1000);
						//Select Save
						Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
						Utils.jsClick("Configuration.btn_Save");
						System.out.println("clicked Save");
						logger.log(LogStatus.PASS, "Selected Save");
						waitForPageLoad(5000);
					}else{
						logger.log(LogStatus.FAIL, "Not able to navigate to new reasons code screen");
					}

					//Provide Room Reasons
					waitForPageLoad(5000);
					boolean bCopyExists = Utils.isExists("Configuration.win_OutOfOrderServiceReason");
					if(bCopyExists){
						Utils.WebdriverWait(1000, "Configuration.txt_Code", "clickable");
						Utils.jsTextbox("Configuration.txt_Code",configMap.get("CONDITIONCODE"));   // Reasons code
						System.out.println("Provided Out of Order / Service Code");
						logger.log(LogStatus.PASS, "Provided Out of Order / Service Code");

						//Select Search
						Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");
						Utils.jsClick("Configuration.btn_Search");
						System.out.println("clicked Search");
						logger.log(LogStatus.PASS, "Selected Search");
					}else{
						logger.log(LogStatus.FAIL, "Not able to search out of reasons code screen");
					}
				}
				waitForPageLoad(1000);
				//Select More Menu 
				Utils.WebdriverWait(1000, "Configuration.menu_More", "clickable");
				Utils.jsClick("Configuration.menu_More");
				System.out.println("clicked More menu ");
				logger.log(LogStatus.PASS, "Selected More Menu");

				//Select Copy link 
				Utils.WebdriverWait(1000, "Configuration.link_Copy", "clickable");
				Utils.jsClick("Configuration.link_Copy");
				System.out.println("clicked Copy link ");
				logger.log(LogStatus.PASS, "clicked Copy Link");
				waitForSpinnerToDisappear(80);

				//Provide condition code
				Utils.WebdriverWait(5000, "Configuration.txt_Code", "clickable");
				Utils.jsTextbox("Configuration.txt_Code",configMap.get("CONDITIONCODE"));   // Reasons code
				System.out.println("Provide Out of Order / Service Code for Copy");
				logger.log(LogStatus.PASS, "Provided Out of Order / Service Code for Copy");

				//Select Save
				Utils.WebdriverWait(1000, "Configuration.btn_Save", "clickable");
				Utils.jsClick("Configuration.btn_Save");
				System.out.println("clicked Save to Copy");
				logger.log(LogStatus.PASS, "Selected Save to Copy");
				waitForPageLoad(5000);

				//Select Copy and Continue
				Utils.WebdriverWait(1000, "Configuration.btn_CopyAndContinue", "clickable");
				Utils.jsClick("Configuration.btn_CopyAndContinue");
				System.out.println("Clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Clicked Copy and Continue button");

				//Validation for Copying Room Class 
				if (getText("Configuration.txt_StatusMessage").contains(OR.getTestData("OutOfOrder__StatusMessage"))) {
					System.out.println("Out of Order / Service Code Created and Copied Successfully " );
					logger.log(LogStatus.PASS,
							"Out of Order / Service Code Created and Copied Successfully " );
				} else {
					System.out.println("Out of Order / Service Code Not Created and Copied Successfully ");
					logger.log(LogStatus.FAIL, "Out of Order / Service Code Not Created and Copied Successfully ");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Out of Order / Service Code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}


	/*******************************************************************
	-  Description: Create/Verify Room Maintaince Code
	- Input:Room reasons code,Description & sequence
	- Output: Room reasons code
	- Author: Girish
	- Date: 29/11/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void roomMaintainceCode(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Inventory menu
			WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Configuration.menu_Inventory");
			click(Utils.element("Configuration.menu_Inventory"));
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_AccommodationManagement", "clickable");
			mouseHover("Configuration.menu_AccommodationManagement");
			jsClick("Configuration.menu_AccommodationManagement");
			System.out.println("clicked Accommodation Management menu");
			logger.log(LogStatus.PASS, "Selected Accommodation Management Menu");

			// Navigating to Room Class  
			WebdriverWait(100, "Configuration.lnk_RoomMaintaince", "presence");
			Thread.sleep(1000);
			jsClick("Configuration.lnk_RoomMaintaince");
			WebdriverWait(1000, "Configuration.win_RoomMaintaince", "presence");
			System.out.println("clicked Room Maintaince Code menu");
			logger.log(LogStatus.PASS, "clicked Room Maintaince Code Menu");
			waitForPageLoad(500);

			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.tbl_Property"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if((rows.get(i).getText().trim()).equalsIgnoreCase(configMap.get("MAINTENANCECODE")))
					{					    
						flag = true;
						logger.log(LogStatus.PASS, "Room Reasons "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
						System.out.println("Room Reasons is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}
			if(!flag)
			{
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Select Template Tab
				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				Utils.jsClick("Configuration.tab_Template");
				System.out.println("clicked Teplate Tab");
				logger.log(LogStatus.PASS, "Selected Template tab");


				//Fetch Search Grid Column Data and validate
				waitForPageLoad(100);
				Thread.sleep(3000);				
				java.util.List<WebElement>  rowData = Utils.elements("Configuration.tbl_Template"); 
				System.out.println("No of rows are : " + rowData.size());
				flag = false;
				if(rowData.size() > 0)
				{
					for(int i=0;i<rowData.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rowData.get(i));
						if((rowData.get(i).getText().trim()).equalsIgnoreCase(configMap.get("MAINTENANCECODE")))
						{
							System.out.println("Room Reasons "+rowData.get(i).getText()+" Template is already existing");
							flag = true;
							logger.log(LogStatus.PASS, "Room Reasons "+rowData.get(i).getText()+" Template is already existing");
							break;
						}
						else
						{
							System.out.println("Room Reasons Template is not present in the system:: "+rowData.get(i).getText());						
						}
					}
				}
				if(!flag){
					waitForPageLoad(5000);			
					//Select New
					Utils.WebdriverWait(5000, "Configuration.link_New", "clickable");
					Utils.click("Configuration.link_New");
					System.out.println("clicked new");
					logger.log(LogStatus.PASS, "Clicked on New");	
					Thread.sleep(5000);

					boolean bExists = Utils.isExists("Configuration.win_RoomMaintaince");
					if(bExists){
						//Provide Room Class
						Utils.WebdriverWait(10000, "Configuration.txt_Code", "clickable");
						Utils.textBox("Configuration.txt_Code",configMap.get("MAINTENANCECODE"));   // Reasons code
						System.out.println("Provided Room Reasons Code");
						logger.log(LogStatus.PASS, "Provided Room Reasons Code");

						waitForPageLoad(1000);
						//Provided Description
						Utils.WebdriverWait(5000, "Configuration.txt_Description", "clickable");
						Utils.jsTextbox("Configuration.txt_Description",configMap.get("DESCRIPTION"));   // Description
						System.out.println("Provide Description for Room Reasons");
						logger.log(LogStatus.PASS, "Provided Description for Room Reasons");			

						waitForPageLoad(1000);
						//Provided Sequence
						Utils.WebdriverWait(100, "Configuration.txt_Seqn", "clickable");
						Utils.jsTextbox("Configuration.txt_Seqn",configMap.get("SEQUENCENO"));   // Sequence
						System.out.println("Provide Sequence for Room Reasons");
						logger.log(LogStatus.PASS, "Provided Sequence for Room Reasons");

						waitForPageLoad(1000);
						//Select Save
						Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
						Utils.jsClick("Configuration.btn_Save");
						System.out.println("clicked Save");
						logger.log(LogStatus.PASS, "Selected Save");
						waitForPageLoad(5000);
					}else{
						logger.log(LogStatus.FAIL, "Not able to navigate to new reasons code screen");
					}

					//Provide Room Reasons
					waitForPageLoad(10000);
					boolean bCopyExists = Utils.isExists("Configuration.win_RoomMaintaince");
					if(bCopyExists){
						Utils.WebdriverWait(1000, "Configuration.txt_Code", "clickable");
						Utils.jsTextbox("Configuration.txt_Code",configMap.get("MAINTENANCECODE"));   // Reasons code
						System.out.println("Provided Room Reasons");
						logger.log(LogStatus.PASS, "Provided Room Reasons");

						//Select Search
						Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");
						Utils.jsClick("Configuration.btn_Search");
						System.out.println("clicked Search");
						logger.log(LogStatus.PASS, "Selected Search");
					}else{
						logger.log(LogStatus.FAIL, "Not able to search out of reasons code screen");
					}
				}
				waitForPageLoad(1000);
				//Select More Menu 
				Utils.WebdriverWait(1000, "Configuration.menu_More", "clickable");
				Utils.jsClick("Configuration.menu_More");
				System.out.println("clicked More menu ");
				logger.log(LogStatus.PASS, "Selected More Menu");

				//Select Copy link 
				Utils.WebdriverWait(100, "Configuration.link_Copy", "clickable");
				Utils.jsClick("Configuration.link_Copy");
				System.out.println("clicked Copy link ");
				logger.log(LogStatus.PASS, "Selected Copy Link");
				waitForSpinnerToDisappear(80);

				//Provide Room Class
				Utils.WebdriverWait(1000, "Configuration.txt_Code", "clickable");
				Utils.jsTextbox("Configuration.txt_Code",configMap.get("MAINTENANCECODE"));   // Reasons code
				System.out.println("Provide Room Class to Copy");
				logger.log(LogStatus.PASS, "Provided Room Reasons for Copy");

				//Select Save
				Utils.WebdriverWait(1000, "Configuration.btn_Save", "clickable");
				Utils.jsClick("Configuration.btn_Save");
				System.out.println("clicked Save to Copy");
				logger.log(LogStatus.PASS, "Selected Save to Copy");

				waitForPageLoad(1000);

				//Select Copy and Continue
				Utils.WebdriverWait(100, "Configuration.btn_CopyAndContinue", "clickable");
				Utils.jsClick("Configuration.btn_CopyAndContinue");
				System.out.println("Clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Clicked Copy and Continue button");

				//Validation for Copying Room Class 

				if (getText("Configuration.txt_StatusMessage").contains(OR.getTestData("RoomMaintenance__StatusMessage"))) {
					System.out.println("Room Reasons Created and Copied Successfully " );
					logger.log(LogStatus.PASS,
							"Room Reasons Created and Copied Successfully " );
				} else {
					System.out.println("Room Reasons Not Created and Copied Successfully ");
					logger.log(LogStatus.FAIL, "Room Reasons Not Created and Copied Successfully ");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Room Reasons not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}


	/*******************************************************************
	-  Description: Create/Verify Article  Code
	- Input:Room reasons code,Description & sequence
	- Output: Room reasons code
	- Author: Girish
	- Date: 29/11/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void createArticle(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Financial menu
			WebdriverWait(100, "Configuration.menu_Financial", "clickable");
			mouseHover("Configuration.menu_Financial");
			click(Utils.element("Configuration.menu_Financial"));
			System.out.println("Clicked Financial menu");
			logger.log(LogStatus.PASS, "Selected Financial Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_TransactionManagement", "clickable");
			mouseHover("Configuration.menu_TransactionManagement");
			jsClick("Configuration.menu_TransactionManagement");
			System.out.println("clicked Transaction Management menu");
			logger.log(LogStatus.PASS, "Selected Transaction Management Menu");

			// Navigating to Articles  
			WebdriverWait(100, "Configuration.lnk_Articles", "clickable");
			jsClick("Configuration.lnk_Articles");
			System.out.println("clicked Articles menu");
			logger.log(LogStatus.PASS, "Selected Articles Menu");
			waitForPageLoad(100);

			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.tbl_Articles"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("ARTICLE_CODE")))
					{
						System.out.println("Articles "+rows.get(i).getText()+" is already existing");
						flag = true;
						logger.log(LogStatus.PASS, "Articles "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
						System.out.println("Articles is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}
			if(!flag)
			{
				waitForPageLoad(300);
				//Select New
				Utils.WebdriverWait(5000, "Configuration.link_New", "clickable");
				Utils.click("Configuration.link_New");
				System.out.println("clicked new");
				logger.log(LogStatus.PASS, "Clicked on New");	
				waitForPageLoad(300);

				//Provide Articles Code
				Utils.WebdriverWait(100, "Configuration.txt_Code", "clickable");
				Utils.jsTextbox("Configuration.txt_Code",configMap.get("ARTICLE_CODE"));   // Articles Code
				System.out.println("Provided Articles Code");
				logger.log(LogStatus.PASS, "Provided Articles Code");

				waitForPageLoad(100);
				//Provided Description
				Utils.WebdriverWait(100, "Configuration.txt_Description", "clickable");
				Utils.jsTextbox("Configuration.txt_Description",configMap.get("ARTICLE_DESC"));   // Description
				System.out.println("Provide Description for Articles");
				logger.log(LogStatus.PASS, "Provided Description for Articles");			

				waitForPageLoad(100);
				//Provided  Transaction Code
				Utils.WebdriverWait(100, "Configuration.txt_TransactionCode", "clickable");
				Utils.jsTextbox("Configuration.txt_TransactionCode",configMap.get("TRANSACTION_CODE"));   // Transaction Code
				System.out.println("Provide Transaction Code");
				logger.log(LogStatus.PASS, "Provided Transaction Code");

				waitForPageLoad(100);
				//Provided Sequence
				Utils.WebdriverWait(100, "Configuration.txt_DefaultPrice", "clickable");
				Utils.jsTextbox("Configuration.txt_DefaultPrice",configMap.get("ARTICLE_PRICE"));   // Article Code
				System.out.println("Provided Article Code");
				logger.log(LogStatus.PASS, "Provided Article Code");

				waitForPageLoad(100);

				//Select Save
				Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
				Utils.jsClick("Configuration.btn_Save");
				System.out.println("clicked Save");
				logger.log(LogStatus.PASS, "Selected Save");
				waitForPageLoad(100);

			}
			//Verify the created article is search
			Utils.WebdriverWait(500, "Configuration.win_Articles", "presence");
			java.util.List<WebElement>  valRows = Utils.elements("Configuration.tbl_Articles"); 
			System.out.println("No of rows are : " + valRows.size());
			if(valRows.size() > 0)
			{
				for(int i=0;i<valRows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", valRows.get(i));
					if(valRows.get(i).getText().equalsIgnoreCase(configMap.get("ARTICLE_CODE")))
					{
						System.out.println("Articles "+valRows.get(i).getText()+" is Created/Searched existing");
						logger.log(LogStatus.PASS, "Articles "+valRows.get(i).getText()+" is Created/Searched successfully");
						break;
					}
					else
					{
						System.out.println("Articles code is not Created/Searched in the system:: "+valRows.get(i).getText());						
					}					  

				}
			}	    				
		}catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Articles code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: This method helps us to create a new transaction group in Opera Cloud Administration
	- Input: Transaction group type, Group code, description, Sequence
	- Output: creates a transaction group
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void newTransactionGroup(HashMap<String, String> configMap)throws Exception {
		//Click on New button				
		Utils.waitForSpinnerToDisappear(10);
		Utils.click("Configuration.TransactionGroupNewLink", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);

		Utils.WebdriverWait(100, "Configuration.menu_TransactionGroupsTitle", "presence");			
		System.out.println("Landed in Copy Transaction Group Template Page" );
		logger.log(LogStatus.PASS, "Landed in Copy Transaction Group Template Page");

		//Selecting Transaction group type
		String transactionGroupType = configMap.get("TRANSACTION_GROUP_TYPE");

		if (transactionGroupType!="") {
			if(transactionGroupType.equalsIgnoreCase("Revenue")) 
				Utils.click("Configuration.NewTransactionGroup_RevenueType", 100, "clickable");
			else if(transactionGroupType.equalsIgnoreCase("Payment"))
				Utils.click("Configuration.NewTransactionGroup_PaymentType", 100, "clickable");
			else if(transactionGroupType.equalsIgnoreCase("Wrapper"))
				Utils.click("Configuration.NewTransactionGroup_WrapperType", 100, "clickable");
			else {
				System.out.println("Please enter a valid Transaction Group Type" );
				logger.log(LogStatus.FAIL, "Please enter a valid Transaction Group Type");
			}
		}
		else {
			System.out.println("Transaction Group Type value is empty" );
			logger.log(LogStatus.INFO, "Transaction Group Type value is empty");
		}

		//Enter the value for transaction group code
		Utils.textBox("Configuration.NewTransactionGroup_Code", configMap.get("TRANSACTION_GROUP"), 100, "presence");
		//Enter the value for sequence
		Utils.textBox("Configuration.NewTransactionGroup_Sequence", configMap.get("DISPLAY_SEQ"), 100, "presence");
		//Enter value for Description
		Utils.textBox("Configuration.NewTransactionGroup_Description", configMap.get("DESCRIPTION"), 100, "presence");
		//Click on Save button
		Utils.click("Configuration.NewTransactionGroup_Save", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
	}

	/*******************************************************************
	-  Description: This method helps us to create a transaction group template and copy it to any property in Opera Cloud Administration
	- Input: Transaction group type, Group code, description, Sequence
	- Output: creates a transaction group template
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void templateTransactionGroup(HashMap<String, String> configMap) throws Exception{
		//Click on Template tab		
		Utils.click("Configuration.TemplateTab", 100, "clickable");			
		Utils.waitForSpinnerToDisappear(10);

		// Click on Transaction Groups Code Search icon
		Utils.click("Configuration.TransactionGroupSearchIcon", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);

		// enter transaction group code in search window
		Utils.textBox("Configuration.TransactionGroupSearchText", configMap.get("TRANSACTION_GROUP"), 100, "presence");
		Utils.waitForSpinnerToDisappear(10);
		//Click on Search button
		Utils.click("Configuration.TransactionGroupSearchButton", 100, "clickable");

		//verifying if no results are displayed
		if(Utils.isExists("Configuration.TransactionGroupSearch_NOResults")) {
			System.out.println("No Results are displayed");
			logger.log(LogStatus.PASS, "No Results are displayed");

			//Click on Cancel link
			Utils.click("Configuration.TransactionGroupSearch_CancelLink");
			Utils.waitForSpinnerToDisappear(10);
			newTransactionGroup(configMap);	
		}
		else {
			if(Utils.isExists("Configuration.NewTransactionSubGroup_SearchResult")) {
				//Fetch Search Grid Column Data and validate
				List<WebElement>  resultRowCount = Utils.elements("Configuration.NewTransactionSubGroup_SearchResult");
				ListIterator<WebElement> itr = null;
				boolean recordFound = false;
				itr = resultRowCount.listIterator();					
				while(itr.hasNext()) {								
					WebElement record = itr.next();
					String code = record.getText().trim();
					System.out.println("Comparing with code:" + code);
					if(code.equalsIgnoreCase(configMap.get("TRANSACTION_GROUP"))) {
						recordFound = true;
						System.out.println("The transaction group "+configMap.get("TRANSACTION_GROUP")+"is already available in the Template" );
						logger.log(LogStatus.PASS, "The transaction group "+configMap.get("TRANSACTION_GROUP")+"is already available in the Template");
						//Click on Cancel link
						Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						break;
					}				
				}
				if(!recordFound) {						
					//Click on Cancel link
					Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					newTransactionGroup(configMap);						
				}
			}			
		}	

		//Click on More options button
		Utils.click("Configuration.TransactionGroupMoreLink", 100, "clickable");
		//Click on Copy link
		Utils.click("Configuration.TransactionGroupCopyLink", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
		Utils.WebdriverWait(100, "Configuration.CopyTransationGroupTemplateTitle", "presence");			
		System.out.println("Landed in Copy Transaction Group Template Page" );
		logger.log(LogStatus.PASS, "Landed in Copy Transaction Group Template Page");

		//Enter the value for transaction group code
		Utils.textBox("Configuration.CopyTransationGroup_GroupName", configMap.get("TRANSACTION_GROUP"), 100, "presence");
		Utils.tabKey("Configuration.CopyTransationGroup_TargetProperties");
		Utils.waitForSpinnerToDisappear(10);
		//Enter the value for target properties
		//Utils.textBox("Configuration.CopyTransationGroup_TargetProperties", configMap.get("TARGET_PROPERTY"), 100, "presence");
		Utils.tabKey("Configuration.CopyTransationGroup_TargetProperties");
		Utils.waitForSpinnerToDisappear(10);
		//Click on save button
		Utils.click("Configuration.CopyTransationGroup_SaveButton", 100, "clickable");
		Utils.waitForSpinnerToDisappear(20);
		Utils.Wait(3000);
		//Click on Copy and Continue button
		Utils.click("Configuration.CopyTransationGroupCopyAndContinue", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
		Utils.Wait(4000);
		//Verifying that the copy transaction group to property is complete
		if(Utils.isDisplayed("Configuration.TransactionGroupCompleteStatus","Copy Transaction Group is Complete")) {
			Utils.click("Configuration.BackToTransactionGroupLink", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			Utils.click("Configuration.PropertyTab", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Configuration.NewTransactionGroup_Code", configMap.get("TRANSACTION_GROUP"), 100, "presence");
			Utils.tabKey("Configuration.NewTransactionGroup_Code");
			Utils.waitForSpinnerToDisappear(10);
			Utils.click("Configuration.PropertyTab_SearchButton", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			if (Utils.getText("Configuration.TransactionGroupResultCode").equals(configMap.get("TRANSACTION_GROUP"))) {
				System.out.println("Transaction Group is created successfully");

			} else {
				System.out.println("Transaction Group is NOT Displayed in search results");						
			}
		}
	}

	/*******************************************************************
	-  Description: This method helps us to create a new transaction group in Opera Cloud Administration
	- Input: Transaction group type, Group code, description, Sequence
	- Output: creates a transaction group template
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void transactionGroups(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {

			// Navigating to Financial Menu
			Utils.click("Configuration.menu_Financial", 100, "clickable");
			// Click on Transaction Management
			Utils.click("Configuration.menu_TransactionManagement", 100, "clickable");
			// Click on Transaction Groups
			Utils.click("Configuration.menu_TransactionGroups", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);

			Utils.WebdriverWait(100, "Configuration.menu_TransactionGroupsTitle", "presence");			
			System.out.println("Landed in Transaction groups Page" );
			logger.log(LogStatus.PASS, "Landed in Transaction groups Page: ");

			Utils.click("Configuration.PropertyTab", 100, "clickable");
			waitForPageLoad(100);
			Utils.click("Configuration.TransactionGroupSearchIcon", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			Utils.textBox("Configuration.TransactionGroupSearchText", configMap.get("TRANSACTION_GROUP"), 100, "presence");
			Utils.waitForSpinnerToDisappear(100);		
			Utils.click("Configuration.TransactionGroupSearchButton", 100, "clickable");
			waitForPageLoad(10);

			if (Utils.isExists("Configuration.TransactionGroupSearch_NOResults")) {

				System.out.println("Transaction Group is NOT Displayed in search results");
				//Click on Cancel link
				Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
				templateTransactionGroup(configMap);

			} else {

				if(Utils.isExists("Configuration.NewTransactionSubGroup_SearchResult")) {
					//Fetch Search Grid Column Data and validate
					List<WebElement>  resultRowCount = Utils.elements("Configuration.NewTransactionSubGroup_SearchResult");
					ListIterator<WebElement> itr = null;
					boolean recordFound = false;
					itr = resultRowCount.listIterator();					
					while(itr.hasNext()) {								
						WebElement record = itr.next();
						String code = record.getText().trim();
						System.out.println("Comparing with code:" + code);
						if(code.equalsIgnoreCase(configMap.get("TRANSACTION_GROUP"))) {
							recordFound = true;
							System.out.println("Transaction Group is already available in the property");
							//Click on Cancel link
							Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
							Utils.waitForSpinnerToDisappear(10);
							break;
						}				
					}
					if(!recordFound) {						
						//Click on Cancel link
						Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						templateTransactionGroup(configMap);						
					}
				}

			}


		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: This method helps us to create a new transaction subgroup in Opera Cloud Administration
	- Input: Description, Subgroup code, Group code, Sequence
	- Output: creates a transaction Subgroup
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void newTransactionSubGroup(HashMap<String, String> configMap) throws Exception{
		//Click on New button
		Utils.click("Configuration.TransactionGroupNewLink", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);

		Utils.WebdriverWait(100, "Configuration.TransactionSubGroupTitle", "presence");			
		System.out.println("Landed in Copy Transaction Group Template Page" );
		logger.log(LogStatus.PASS, "Landed in Copy Transaction Group Template Page");

		//Enter the value for transaction group code
		Utils.textBox("Configuration.NewTransactionGroup_Code", configMap.get("TRANSACTION_SUBGROUP"), 100, "presence");
		Utils.waitForSpinnerToDisappear(100);
		//Enter the value for sequence
		Utils.textBox("Configuration.NewTransactionGroup_Sequence", configMap.get("DISPLAY_SEQ"), 100, "presence");
		Utils.waitForSpinnerToDisappear(100);
		//Enter value for Description
		Utils.textBox("Configuration.NewTransactionGroup_Description", configMap.get("DESCRIPTION"), 100, "presence");
		Utils.waitForSpinnerToDisappear(100);
		//		//Select transaction group
		//		Utils.click("Configuration.NewTransactionSubgroup_GroupSearchIcon", 100, "clickable");
		//		Utils.waitForSpinnerToDisappear(10);
		Utils.textBox("Configuration.GroupSearchField", configMap.get("TRANSACTION_GROUP"), 100, "presence");
		Utils.tabKey("Configuration.GroupSearchField");
		Utils.waitForSpinnerToDisappear(100);
		//		//Click on Search button
		//		Utils.click("Configuration.TransactionGroupSearchButton", 100, "clickable");
		//		Utils.waitForSpinnerToDisappear(10);
		//		Utils.click("Configuration.NewTransactionSubGroup_SearchResult", 100, "clickable");
		//		Utils.waitForSpinnerToDisappear(10);
		//		Utils.click("Configuration.NewTransactionSubGroup_SearchSelectButton", 100, "clickable");
		//		Utils.waitForSpinnerToDisappear(10);

		//Click on Save button
		Utils.click("Configuration.NewTransactionGroup_Save", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);
	}

	/*******************************************************************
	-  Description: This method helps us to create a transaction subgroup template and copy it to any property in Opera Cloud Administration
	- Input: Description, Subgroup code, Group code, Sequence
	- Output: creates a transaction Subgroup template
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void templateTransactionSubGroup(HashMap<String, String> configMap) throws Exception{
		//Click on Template tab		
		Utils.click("Configuration.TemplateTab", 100, "clickable");			
		Utils.waitForSpinnerToDisappear(100);

		// enter transaction group code in search window
		Utils.textBox("Configuration.GroupSearchField", configMap.get("TRANSACTION_GROUP"), 400, "presence");
		Utils.waitForSpinnerToDisappear(100);
		//Tab out of the group code field
		Utils.tabKey("Configuration.GroupSearchField");
		Utils.waitForSpinnerToDisappear(100);
		// Click on Transaction Groups Code Search icon
		Utils.click("Configuration.SubgroupSearchIcon", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);

		// enter transaction group code in search window
		Utils.textBox("Configuration.TransactionGroupSearchText", configMap.get("TRANSACTION_SUBGROUP"), 100, "presence");
		Utils.waitForSpinnerToDisappear(100);
		//Click on Search button
		Utils.click("Configuration.TransactionSubGroupSearchButton", 100, "clickable");

		//verifying if no results are displayed
		if(Utils.isExists("Configuration.TransactionGroupSearch_NOResults")) {
			System.out.println("No Results are displayed");
			logger.log(LogStatus.PASS, "No Results are displayed");

			//Click on Cancel link
			Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			newTransactionSubGroup(configMap);

		}
		else {
			if(Utils.isExists("Configuration.NewTransactionSubGroup_SearchResult")) {
				//Fetch Search Grid Column Data and validate
				List<WebElement>  resultRowCount = Utils.elements("Configuration.NewTransactionSubGroup_SearchResult");
				ListIterator<WebElement> itr = null;
				boolean recordFound = false;
				itr = resultRowCount.listIterator();					
				while(itr.hasNext()) {								
					WebElement record = itr.next();
					String code = record.getText().trim();
					System.out.println("Comparing with code:" + code);
					if(code.equalsIgnoreCase(configMap.get("TRANSACTION_GROUP"))) {
						recordFound = true;
						System.out.println("The transaction group "+configMap.get("TRANSACTION_SUBGROUP")+"is already available in the Template" );
						logger.log(LogStatus.PASS, "The transaction group "+configMap.get("TRANSACTION_SUBGROUP")+"is already available in the Template");
						//Click on Cancel link
						Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						break;
					}				
				}
				if(!recordFound) {						
					//Click on Cancel link
					Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
					Utils.waitForSpinnerToDisappear(100);
					newTransactionSubGroup(configMap);						
				}
			}
		}

		//Click on More options button
		Utils.click("Configuration.TransactionGroupMoreLink", 100, "clickable");
		//Click on Copy link
		Utils.click("Configuration.TransactionGroupCopyLink", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);
		//Select a transaction subgroup to copy 
		Utils.textBox("Configuration.TransactionSubGroupCopyFilterAvailable", configMap.get("TRANSACTION_SUBGROUP"), 100, "presence");
		Utils.click("Configuration.TransactionSubGroupCopy_GoAvailable", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);

		List<WebElement> resultRowCount = Utils.elements("Configuration.TransactionSubGroupCopy_AvailableFilterResult");					
		ListIterator<WebElement> itr = null;
		itr = resultRowCount.listIterator();

		boolean recordFound = false;
		while(itr.hasNext()) {			
			WebElement record = itr.next();
			String code = record.getText().trim();
			if(code.equalsIgnoreCase(configMap.get("TRANSACTION_SUBGROUP"))) {
				recordFound = true;
				record.click();
				Utils.waitForSpinnerToDisappear(100);
				System.out.println("***** Clicked on "+ code + "*****");
				logger.log(LogStatus.PASS, "Clicked on " + code);
				Utils.click("Configuration.TransactionSubGroupCopy_AddButton", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);			
				break;

			}			
		}
		if(recordFound==false) {
			System.out.println("***** No records found for Subgroup: "+ configMap.get("TRANSACTION_SUBGROUP") + "*****");
			logger.log(LogStatus.FAIL, "No records found for Subgroup: " + configMap.get("TRANSACTION_SUBGROUP"));
		}

		//verifying if the selected subgroup is added successfully
		List<WebElement> resultsRowCount = Utils.elements("Configuration.TransactionSubGroupCopy_SelectedFilterResult");					
		ListIterator<WebElement> iter = null;
		iter = resultsRowCount.listIterator();
		String code = configMap.get("TRANSACTION_SUBGROUP");
		while(iter.hasNext()) {
			if(iter.next().getText().trim().equalsIgnoreCase(code)) {
				System.out.println("***** Added subgroup: "+ code + "*****");
				logger.log(LogStatus.PASS, "Added subgroup: " + code);
				break;

			}
			else {
				System.out.println("***** NOT Added subgroup: "+ code + "*****");
				logger.log(LogStatus.FAIL, "NOT Added subgroup: " + code);
			}
		}

		//Click on Save button
		Utils.click("Configuration.CopyTransationGroup_SaveButton", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);
		Utils.Wait(3000);
		//Click on Copy and Continue button
		Utils.click("Configuration.CopyTransationGroupCopyAndContinue", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100); 
		Utils.Wait(4000);
		if(Utils.isDisplayed("Configuration.TransactionGroupCompleteStatus","Copy Transaction Group is Complete")) {
			Utils.click("Configuration.BackToTransactionSubGroupLink", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			Utils.click("Configuration.PropertyTab", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			Utils.textBox("Configuration.PropertyTab_GroupSearch", configMap.get("TRANSACTION_GROUP"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_GroupSearch");
			Utils.waitForSpinnerToDisappear(100);
			Utils.textBox("Configuration.PropertyTab_SubGroupSearch", configMap.get("TRANSACTION_SUBGROUP"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_SubGroupSearch");
			Utils.waitForSpinnerToDisappear(100);
			Utils.click("Configuration.PropertyTab_SearchButton", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			if ((Utils.getText("Configuration.TransactionGroupResultCode").trim()).equals(configMap.get("TRANSACTION_SUBGROUP"))) {
				System.out.println("Transaction SubGroup is created successfully");
				logger.log(LogStatus.PASS, "Transaction SubGroup: "+code+" is created successfully");

			} else {
				System.out.println("Transaction SubGroup "+code+" is NOT Displayed in search results");
				logger.log(LogStatus.FAIL, "NOT Added subgroup: " + code);

			}
		}
	}

	/*******************************************************************
	-  Description: This method helps us to create a transaction subgroup in Opera Cloud Administration
	- Input: Description, Subgroup code, Group code, Sequence
	- Output: creates a transaction Subgroup
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void transactionSubGroups(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		try {
			// Navigating to Financial Menu
			Utils.click("Configuration.menu_Financial", 100, "clickable");
			// Click on Transaction Management
			Utils.click("Configuration.menu_TransactionManagement", 100, "clickable");
			// Click on Transaction Groups
			Utils.click("Configuration.menu_TransactionSubGroup", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);

			Utils.WebdriverWait(100, "Configuration.TransactionSubGroupTitle", "presence");			
			System.out.println("Landed in Transaction subgroups Page" );
			logger.log(LogStatus.PASS, "Landed in Transaction subgroups Page: ");

			Utils.click("Configuration.PropertyTab", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			Utils.textBox("Configuration.PropertyTab_GroupSearch", configMap.get("TRANSACTION_GROUP"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_GroupSearch");
			Utils.waitForSpinnerToDisappear(100);
			Utils.click("Configuration.SubgroupSearchIcon", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			Utils.textBox("Configuration.TransactionGroupSearchText", configMap.get("TRANSACTION_SUBGROUP"), 100, "presence");			
			Utils.waitForSpinnerToDisappear(100);
			Utils.jsClick("Configuration.TransactionSubGroupSearchButton", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);

			//verifying if no results are displayed
			if(Utils.isExists("Configuration.TransactionGroupSearch_NOResults")) {
				System.out.println("No Results are displayed");
				logger.log(LogStatus.PASS, "No Results are displayed");

				//Click on Cancel link
				Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
				templateTransactionSubGroup(configMap);

			}
			else {
				if(Utils.isExists("Configuration.NewTransactionSubGroup_SearchResult")) {
					//Fetch Search Grid Column Data and validate
					List<WebElement>  resultRowCount = Utils.elements("Configuration.NewTransactionSubGroup_SearchResult");
					ListIterator<WebElement> itr = null;
					boolean recordFound = false;
					itr = resultRowCount.listIterator();					
					while(itr.hasNext()) {								
						WebElement record = itr.next();
						String code = record.getText().trim();
						System.out.println("Comparing with code:" + code);
						if(code.equalsIgnoreCase(configMap.get("TRANSACTION_SUBGROUP"))) {
							recordFound = true;
							System.out.println("Transaction Group "+configMap.get("TRANSACTION_SUBGROUP")+"is already available in the property");
							logger.log(LogStatus.PASS, "The transaction group "+configMap.get("TRANSACTION_SUBGROUP")+"is already available in the property");
							//Click on Cancel link
							Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
							Utils.waitForSpinnerToDisappear(100);
							break;
						}				
					}
					if(!recordFound) {						
						//Click on Cancel link
						Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(100);
						templateTransactionSubGroup(configMap);						
					}
				}				
			}					
		}catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}

	}

	/*******************************************************************
	-  Description: This method helps us to create a new transaction codes in Opera Cloud Administration
	- Input: Description, transaction code, subgroup code, transaction type, Max amount, Min amount, Payment type
	- Output: creates a transaction code
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void newTransactionCode(HashMap<String, String> configMap) throws Exception{

		//Click on New button
		Utils.Wait(3000);
		Utils.click("Configuration.TransactionGroupNewLink", 100, "clickable");
		Utils.waitForSpinnerToDisappear(45);

		Utils.WebdriverWait(100, "Configuration.TransactionCodeTitle", "presence");			
		System.out.println("Landed in New Transaction Code Template Page" );
		logger.log(LogStatus.PASS, "Landed in New Transaction Code Template Page");
		Utils.waitForSpinnerToDisappear(45);
		//Enter the value for transaction group code
		Utils.textBox("Configuration.NewTransactionGroup_Code", configMap.get("TRANSACTION_CODE"), 100, "presence");
		Utils.waitForSpinnerToDisappear(30);

		System.out.println("TRANSACTION_SUBGROUP :: "+configMap.get("TRANSACTION_SUBGROUP"));

		Utils.textBox("Configuration.TransactionSubGroupSearchField", configMap.get("TRANSACTION_SUBGROUP"), 50, "stale");
		Utils.waitForSpinnerToDisappear(30);
		if(!getText("Configuration.TransactionSubGroupSearchField").equalsIgnoreCase(configMap.get("TRANSACTION_SUBGROUP")))
			Utils.textBox("Configuration.TransactionSubGroupSearchField", configMap.get("TRANSACTION_SUBGROUP"), 50, "stale");
		Utils.tabKey("Configuration.TransactionSubGroupSearchField");
		Utils.waitForSpinnerToDisappear(30);
		//Enter value for Description
		Utils.textBox("Configuration.NewTransactionGroup_Description", configMap.get("DESCRIPTION"), 100, "presence");
		Utils.waitForSpinnerToDisappear(30);
		if(configMap.get("TRANSACTION_TYPE")!=null) {
			Utils.selectBy("Configuration.NewTransactionCode_TransactionType", "text", configMap.get("TRANSACTION_TYPE"));
			Utils.waitForSpinnerToDisappear(30);
		}
		if(configMap.get("MIN_AMOUNT")!=null) {
			Utils.selectBy("Configuration.NewTransactionCode_TransactionType", "text", configMap.get("TRANSACTION_TYPE"));
			Utils.waitForSpinnerToDisappear(10);
		}

		//Select PaidOut CheckBox
		if((configMap.get("PAIDOUT_CHECKBOX")!=null)&&(configMap.get("PAIDOUT_CHECKBOX").equalsIgnoreCase("Y"))) {
			if (!isSelected("Configuration.NewTransactionCode_PaidOutCheckBox", "PaidOut CheckBox")) {
				Utils.jsClick("Configuration.NewTransactionCode_PaidOutCheckBox");
				Utils.waitForSpinnerToDisappear(10);
			} else {
				System.out.println("PaidOut CheckBox is Selected");
			}
		}
		//Select Membership CheckBox
		if((configMap.get("MEMBERSHIP_CHECKBOX")!=null)&&(configMap.get("MEMBERSHIP_CHECKBOX").equalsIgnoreCase("Y"))) {
			if (!isSelected("Configuration.NewTransactionCode_MembershipCheckBox", "Membership CheckBox")) {
				Utils.jsClick("Configuration.NewTransactionCode_MembershipCheckBox");
				Utils.waitForSpinnerToDisappear(10);
			} else {
				System.out.println("Membership CheckBox is Selected");
			}
		}
		//Select CheckNumberMandatory CheckBox
		if((configMap.get("CHECKNUMBER_MANDATORY_CHECKBOX")!=null)&&(configMap.get("CHECKNUMBER_MANDATORY_CHECKBOX").equalsIgnoreCase("Y"))) {
			if (!isSelected("Configuration.NewTransactionCode_CheckNumberCheckBox", "CheckNumberMandatory CheckBox")) {
				Utils.jsClick("Configuration.NewTransactionCode_CheckNumberCheckBox");
				Utils.waitForSpinnerToDisappear(10);
			} else {
				System.out.println("CheckNumberMandatory CheckBox is Selected");
			}
		}
		//Select PostCovers CheckBox
		if((configMap.get("POST_COVERS_CHECKBOX")!=null)&&(configMap.get("POST_COVERS_CHECKBOX").equalsIgnoreCase("Y"))) {
			if (!isSelected("Configuration.NewTransactionCode_PostCoversCheckBox", "PostCovers CheckBox")) {
				Utils.jsClick("Configuration.NewTransactionCode_PostCoversCheckBox");
				Utils.waitForSpinnerToDisappear(10);
			} else {
				System.out.println("PostCovers CheckBox is Selected");
			}
		}

		if((Utils.getText("Configuration.NewTransactionCode_GroupType")).equalsIgnoreCase("Revenue")) {
			//Select Revenue CheckBox
			if((configMap.get("REVENUE_GROUP_CHECKBOX")!=null)&&(configMap.get("REVENUE_GROUP_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_RevenueCheckBox", "Revenue CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_RevenueCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("Revenue CheckBox is Selected");
				}
			}

			//Select IncludeDepositCXLRule CheckBox
			if((configMap.get("DEPOSIT_CXL_CHECKBOX")!=null)&&(configMap.get("DEPOSIT_CXL_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_DepositCXLCheckBox", "IncludeDepositCXLRule CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_DepositCXLCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("IncludeDepositCXLRule CheckBox is Selected");
				}
			}

			//Select Generates CheckBox
			if((configMap.get("GENERATES_CHECKBOX")!=null)&&(configMap.get("GENERATES_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_GeneratesCheckBox", "Generates CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_GeneratesCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("Generates CheckBox is Selected");
				}
			}

			//Select Manual Posting CheckBox
			if((configMap.get("MANUALPOST_ALLOWED_CHECKBOX")!=null)&&(configMap.get("MANUALPOST_ALLOWED_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_ManualPostingCheckBox", "Manual Posting CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_ManualPostingCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("Manual Posting CheckBox is Selected");
				}
			}
		}
		else if((Utils.getText("Configuration.NewTransactionCode_GroupType")).equalsIgnoreCase("PAYMENT")) {
			if(configMap.get("PAYMENT_TYPE").equalsIgnoreCase("Cash")) {
				Utils.click("Configuration.NewTransactionCode_CashPaymentType", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
			}
			else if(configMap.get("PAYMENT_TYPE").equalsIgnoreCase("Credit Card")) {
				Utils.click("Configuration.NewTransactionCode_CreditCardPaymentType", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
				if(configMap.get("PAYMENT_TYPE").equalsIgnoreCase("EFT")) {
					Utils.click("Configuration.NewTransactionCode_CreditCardPaymentType", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);	
				}
				else if(configMap.get("PAYMENT_TYPE").equalsIgnoreCase("Manual")) {
					Utils.click("Configuration.NewTransactionCode_ManualProcessingType", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
				}
				else {
					System.out.println("Please enter a valid processing type");
					logger.log(LogStatus.FAIL, "Please enter a valid processing type");
				}						

			}
			else {
				Utils.click("Configuration.NewTransactionCode_OthersPaymentType", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
			}

			//Select Cashier Payments CheckBox
			if((configMap.get("CASHIER_PAYMENTS_CHECKBOX")!=null)&&(configMap.get("CASHIER_PAYMENTS_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_CashierCheckBox", "Cashier Payments CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_CashierCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("Cashier Payments CheckBox is Selected");
				}
			}

			//Select Cashier Payments CheckBox
			if((configMap.get("ROUNDFACTOR_CHECKBOX")!=null)&&(configMap.get("ROUNDFACTOR_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_RoundFactorCheckBox", "Round Factor CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_RoundFactorCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("Round Factor CheckBox is Selected");
				}
			}

			//Select Cashier Payments CheckBox
			if((configMap.get("AR_PAYMENTS_CHECKBOX")!=null)&&(configMap.get("AR_PAYMENTS_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_ARPaymentsCheckBox", "AR Payments CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_ARPaymentsCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("AR Payments CheckBox is Selected");
				}
			}

			//Select Cashier Payments CheckBox
			if((configMap.get("DEPOSIT_PAYMENT_CHECKBOX")!=null)&&(configMap.get("DEPOSIT_PAYMENT_CHECKBOX").equalsIgnoreCase("Y"))) {
				if (!isSelected("Configuration.NewTransactionCode_DepositPaymentsCheckBox", "Deposit Payments CheckBox")) {
					Utils.jsClick("Configuration.NewTransactionCode_DepositPaymentsCheckBox");
					Utils.waitForSpinnerToDisappear(10);
				} else {
					System.out.println("Deposit Payments CheckBox is Selected");
				}
			}
		}

		//Click on Save button
		Utils.click("Configuration.NewTransactionGroup_Save", 100, "clickable");
		Utils.waitForSpinnerToDisappear(40);
	}

	/*******************************************************************
	-  Description: This method helps us to create a transaction codes template and copy it to any property in Opera Cloud Administration
	- Input: Description, transaction code, subgroup code, transaction type, Max amount, Min amount, Payment type
	- Output: creates a transaction code template
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void templateTransactionCode(HashMap<String, String> configMap) throws Exception{
		//Click on Template tab		
		Utils.click("Configuration.TemplateTab", 100, "clickable");			
		Utils.waitForSpinnerToDisappear(10);

		// enter transaction group code in search window
		Utils.textBox("Configuration.GroupSearchField", configMap.get("TRANSACTION_GROUP"), 400, "presence");
		Utils.waitForSpinnerToDisappear(10);
		//Tab out of the group code field
		Utils.tabKey("Configuration.GroupSearchField");
		Utils.waitForSpinnerToDisappear(10);
		//enter transaction subgroup in search window
		Utils.textBox("Configuration.TransactionSubGroupSearchField", configMap.get("TRANSACTION_SUBGROUP"), 400, "presence");
		Utils.waitForSpinnerToDisappear(10);
		//Tab out of the sub group code field
		Utils.tabKey("Configuration.TransactionSubGroupSearchField");
		Utils.waitForSpinnerToDisappear(10);
		// Click on Transaction Code Search icon
		Utils.click("Configuration.TransactionGroupSearchIcon", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);

		// enter transaction code in search window
		Utils.textBox("Configuration.TransactionGroupSearchText", configMap.get("TRANSACTION_CODE"), 100, "presence");
		Utils.waitForSpinnerToDisappear(10);
		//Click on Search button
		Utils.jsClick("Configuration.TransactionCodeSearchButton", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
		//verifying if no results are displayed
		if(Utils.isExists("Configuration.TransactionGroupSearch_NOResults")) {
			System.out.println("No Results are displayed");
			logger.log(LogStatus.PASS, "No Results are displayed");

			//Click on Cancel link
			Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			newTransactionCode(configMap);


		}else {
			if(Utils.isExists("Configuration.NewTransactionSubGroup_SearchResult")) {
				//Fetch Search Grid Column Data and validate
				List<WebElement>  resultRowCount = Utils.elements("Configuration.NewTransactionSubGroup_SearchResult");
				ListIterator<WebElement> itr = null;
				boolean recordFound = false;
				itr = resultRowCount.listIterator();					
				while(itr.hasNext()) {								
					WebElement record = itr.next();
					String code = record.getText().trim();
					System.out.println("Comparing with code:" + code);
					if(code.equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
						recordFound = true;
						System.out.println("The transaction code "+configMap.get("TRANSACTION_CODE")+"is already available in the Template" );
						logger.log(LogStatus.PASS, "The transaction code "+configMap.get("TRANSACTION_CODE")+"is already available in the Template");
						//Click on Cancel link
						Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						break;
					}				
				}
				if(!recordFound) {						
					//Click on Cancel link
					Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					newTransactionCode(configMap);						
				}
			}			
		}

		//Click on More options button
		Utils.click("Configuration.TransactionGroupMoreLink", 100, "clickable");
		//Click on Copy link
		Utils.click("Configuration.TransactionGroupCopyLink", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
		//Select a transaction subgroup to copy 
		Utils.textBox("Configuration.CopyTransactionCode_TransactionCode", configMap.get("TRANSACTION_CODE"), 100, "presence");
		Utils.click("Configuration.CopyTransactionCode_TransactionCode", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
		Utils.tabKey("Configuration.CopyTransactionCode_TransactionCode");
		Utils.waitForSpinnerToDisappear(10);

		//Click on save button
		Utils.click("Configuration.CopyTransationGroup_SaveButton", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
		Utils.Wait(3000);
		//Click on Copy and Continue button
		Utils.click("Configuration.CopyTransationGroupCopyAndContinue", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);
		Utils.Wait(4000);
		//Verifying that the copy transaction group to property is complete
		if(Utils.isDisplayed("Configuration.TransactionGroupCompleteStatus","Copy Transaction Group is Complete")) {
			Utils.click("Configuration.CopyTransactionCode_BackToTransactionCodeLink", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			Utils.click("Configuration.PropertyTab", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Configuration.PropertyTab_GroupSearch", configMap.get("TRANSACTION_GROUP"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_GroupSearch");
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Configuration.PropertyTab_SubGroupSearch", configMap.get("TRANSACTION_SUBGROUP"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_SubGroupSearch");
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Configuration.PropertyTab_TransactionCodeSearch", configMap.get("TRANSACTION_CODE"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_TransactionCodeSearch");
			Utils.waitForSpinnerToDisappear(10);
			Utils.click("Configuration.PropertyTab_SearchButton", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			if ((Utils.getText("Configuration.TransactionGroupResultCode").trim()).equals(configMap.get("TRANSACTION_CODE"))) {
				System.out.println("Transaction Code is created successfully");

			} else {
				System.out.println("Transaction Code is NOT Displayed in search results");

			}
		}
	}

	/*******************************************************************
	-  Description: This method helps us to create a transaction codes in Opera Cloud Administration
	- Input: Description, transaction code, subgroup code, transaction type, Max amount, Min amount, Payment type
	- Output: creates a transaction code
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void transactionCodes(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		try {
			// Navigating to Financial Menu
			Utils.click("Configuration.menu_Financial", 100, "clickable");
			// Click on Transaction Management
			Utils.click("Configuration.menu_TransactionManagement", 100, "clickable");
			// Click on Transaction Groups
			Utils.click("Configuration.menu_TransactionCode", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);

			Utils.WebdriverWait(100, "Configuration.TransactionCodeTitle", "presence");			
			System.out.println("Landed in Transaction Codes Page" );
			logger.log(LogStatus.PASS, "Landed in Transaction Codes Page: ");

			Utils.click("Configuration.PropertyTab", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			Utils.click("Configuration.TransactionGroupSearchIcon", 100, "clickable");			
			Utils.waitForSpinnerToDisappear(100);
			Utils.textBox("Configuration.TransactionGroupSearchText", configMap.get("TRANSACTION_CODE"), 100, "presence");
			Utils.waitForSpinnerToDisappear(100);
			Utils.jsClick("Configuration.TransactionCodeSearchButton", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);

			if(Utils.isExists("Configuration.TransactionGroupSearch_NOResults")) {
				System.out.println("No Results are displayed");
				logger.log(LogStatus.PASS, "No Results are displayed");

				//Click on Cancel link
				Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
				templateTransactionCode(configMap);
			}
			else {
				if(Utils.isExists("Configuration.NewTransactionSubGroup_SearchResult")) {
					//Fetch Search Grid Column Data and validate
					List<WebElement>  resultRowCount = Utils.elements("Configuration.NewTransactionSubGroup_SearchResult");
					ListIterator<WebElement> itr = null;
					boolean recordFound = false;
					itr = resultRowCount.listIterator();					
					while(itr.hasNext()) {								
						WebElement record = itr.next();
						String code = record.getText().trim();
						System.out.println("Comparing with code:" + code);
						if(code.equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
							recordFound = true;
							System.out.println("The transaction code "+configMap.get("TRANSACTION_CODE")+"is already available in the property" );
							logger.log(LogStatus.PASS, "The transaction code "+configMap.get("TRANSACTION_CODE")+"is already available in the property");
							//Click on Cancel link
							Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
							Utils.waitForSpinnerToDisappear(10);
							break;
						}				
					}
					if(!recordFound) {						
						//Click on Cancel link
						Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(100);
						templateTransactionCode(configMap);						
					}
				}	

			}				


		}catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}

	}

	/*******************************************************************
    - Description: This method creates Payment Methods
	- Input:PaymentTypes
	- Output:
	- Author:Dilip
	- Date: 11/30/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createPaymentMethods(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Financial
			click("Configuration.AdminMenu_Financial", 100, "clickable");

			// Navigating to Transaction Management
			click("Configuration.AdminMenu_Financial_TransactionManagement", 100, "clickable");

			// Navigating to Payment Methods
			click("Configuration.AdminMenu_Financial_TransactionManagement_PaymentMethods", 100, "clickable");

			// Clicking on search button
			click("Configuration.btn_CreatePaymentMethods_Search", 100, "clickable");

			// Verifying if payment methods exists
			if (ValidateGridData("Configuration.table_SearchGrid", configMap.get("CODE"))){

				System.out.println("Payment Method already exists");
				logger.log(LogStatus.PASS, "Payment Method already exists :" +configMap.get("CODE"));

			}else{

				// wait for New link to load	
				//				Utils.WebdriverWait(100, "Configuration.link_PaymentMethods_New", "stale");

				// Clicking on New Button
				waitForSpinnerToDisappear(50);
				jsClick("Configuration.link_PaymentMethods_New", 100, "clickable");				

				// Entering the Code
				textBox("Configuration.txt_CreatePaymentMethods_Code",configMap.get("CODE"), 100, "presence");

				// Entering the Description
				textBox("Configuration.txt_CreatePaymentMethods_Desc",configMap.get("DESCRIPTION"), 100, "presence");            

				// Entering the Transaction Code
				textBox("Configuration.txt_CreatePaymentMethods_TransactionCode",configMap.get("TRANSACTION_CODE"), 100, "presence");

				// Clicking on checkbox_Reservation
				jsClick("Configuration.checkbox_Reservation", 100, "presence");
				waitForSpinnerToDisappear(50);

				// Clicking on Save Button
				jsClick("Configuration.btn_CreatePaymentMethods_Save", 100, "presence");
				waitForSpinnerToDisappear(50);

				// Verify if payment methods is created    				
				if (ValidateGridData("Configuration.table_SearchGrid", configMap.get("CODE"))){
					System.out.println("Payment Methods is created successfully :"+configMap.get("CODE"));
					logger.log(LogStatus.PASS, "Payment Methods is created successfully :"+configMap.get("CODE"));
				} else {
					System.out.println("Payment Methods not is created successfully :"+configMap.get("CODE"));
					logger.log(LogStatus.FAIL, "Payment Methods not is created successfully :"+configMap.get("CODE"));
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Payment Methods  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Payment Methods not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}


	/*******************************************************************
    - Description: This method Create Reservation Types
	- Input:Reservation Types
	- Output:
	- Author:Dilip
	- Date: 11/30/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createReservationTypes(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Booking
			click("Configuration.AdminMenu_Booking", 100, "clickable");

			// Navigating to BookingRulesSchedules
			click("Configuration.AdminMenu_Booking_BookingRulesSchedules", 100, "clickable");

			// Navigating to ReservationTypes
			mouseHover("Configuration.AdminMenu_Booking_BookingRulesSchedules_ReservationTypes", 100, "clickable");
			jsClick("Configuration.AdminMenu_Booking_BookingRulesSchedules_ReservationTypes", 100, "clickable");


			// Validate RESERVATION TYPE exist

			if (ValidateGridData("Configuration.table_ReservationTypesSearchGrid", configMap.get("RESERVATION_TYPE"))){

				System.out.println("RESERVATION TYPE already exists :"+configMap.get("RESERVATION_TYPE"));
				logger.log(LogStatus.PASS, "RESERVATION TYPE already exists :"+configMap.get("RESERVATION_TYPE"));

			}else{

				// Clicking on Tab Template
				jsClick("Configuration.tab_ReservationTypes_Template", 100, "clickable");				

				// Both Property and Reservation types has same tables including element	
				waitForSpinnerToDisappear(50);

				// Validate Reservation Type Template exist 
				if (ValidateGridData("Configuration.table_ReservationTypesSearchGrid", configMap.get("RESERVATION_TYPE"))){

					System.out.println("RESERVATION TYPE Template already exists :"+configMap.get("RESERVATION_TYPE"));
					logger.log(LogStatus.PASS, "RESERVATION TYPE Template already exists :"+configMap.get("RESERVATION_TYPE"));


				}else{					

					// Clicking on New Button
					mouseHover("Configuration.link_ReservationTypes_New", 100, "clickable");
					jsClick("Configuration.link_ReservationTypes_New", 100, "clickable");

					// Entering the Reservation Type
					waitForSpinnerToDisappear(50);
					//					Utils.WebdriverWait(100, "Configuration.txt_ReservationTypes_TemplateReservationType", "stale");
					jsTextbox("Configuration.txt_ReservationTypes_TemplateReservationType",configMap.get("RESERVATION_TYPE"));

					// Entering the Description
					textBox("Configuration.txt_ReservationTypes_TemplateDesc",configMap.get("DESCRIPTION"), 100, "presence");

					// Clicking on Save Button
					click("Configuration.btn_ReservationTypes_Save", 100, "clickable");

				}


				waitForSpinnerToDisappear(50);
				// Clicking on More Button
				jsClick("Configuration.More_ResevationTypes", 100, "clickable");


				waitForSpinnerToDisappear(50);
				// Clicking on Copy Button
				jsClick("Configuration.Copy_ResevationTypes", 100, "clickable");


				// Wait for Save button to load	
				Utils.WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "clickable");

				// Entering the Reservation Type
				textBox("Configuration.txt_ReservationTypes_TemplateReservationType",configMap.get("RESERVATION_TYPE"), 100, "presence");

				// Clicking on Copy Button
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "clickable");

				// Wait for Copy Button to load
				Utils.WebdriverWait(100,"Configuration.btn_ResevationTypes_CopyContinue", "presence");

				// Clicking on Copy and Continue
				jsClick("Configuration.btn_ResevationTypes_CopyContinue", 100, "clickable");


				// Validate Reservation Type is copied successfully			
				if(Utils.getText("Configuration.label_ResevationTypes_complete").equals(OR.getTestData("label_ReservationTypes_complete"))){
					logger.log(LogStatus.PASS, "Actual Value "+Utils.getText("Configuration.label_ResevationTypes_complete")+" :Excepted value "+OR.getTestData("label_ReservationTypes_complete")+"");
				}else{
					logger.log(LogStatus.FAIL, "Actual Value "+Utils.getText("Configuration.label_ResevationTypes_complete")+" :Excepted value "+OR.getTestData("label_ReservationTypes_complete")+"");
				}

				if(Utils.getText("Configuration.label_ResevationTypes_copytemplate").equals(OR.getTestData("label_ReservationTypes_copytemplatecode"))){
					logger.log(LogStatus.PASS, "Actual Value "+Utils.getText("Configuration.label_ResevationTypes_copytemplate")+" :Excepted value "+OR.getTestData("label_ReservationTypes_copytemplatecode")+"");
				}else{
					logger.log(LogStatus.FAIL, "Actual Value "+Utils.getText("Configuration.label_ResevationTypes_copytemplate")+" :Excepted value "+OR.getTestData("label_ReservationTypes_copytemplatecode")+"");
				}


				// Clicking on back to reservation types
				Utils.WebdriverWait(100,"Configuration.link_ResevationTypes_back", "presence");
				jsClick("Configuration.link_ResevationTypes_back", 100, "clickable");			

				// Clicking on Tab Property
				jsClick("Configuration.tab_ReservationTypes_Property", 100, "clickable");

				// wait for New Button to load
				Utils.WebdriverWait(100, "Configuration.link_ReservationTypes_New", "clickable");

				// Validate Reservation Type is copied successfully and available in property
				if(ValidateGridData("Configuration.table_ReservationTypesSearchGrid", configMap.get("RESERVATION_TYPE"))){
					System.out.println("*************Reservation Type is created successfully***************");
					logger.log(LogStatus.PASS, "Reservation Type is created successfully :"+configMap.get("RESERVATION_TYPE"));	
				}else{
					System.out.println("*************Reservation Type is not created successfully***************");
					logger.log(LogStatus.FAIL, "Reservation Type is not created successfully :"+configMap.get("RESERVATION_TYPE"));
				}


			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation Types  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Reservation Types not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			tearDown();
			throw (e);

		}

	}


	/*******************************************************************
    - Description: This method Create Reservation Type Schedule
	- Input:Reservation Type Schedule
	- Output:
	- Author:Dilip
	- Date: 11/30/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createReservationTypeSchedule(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);		 
		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Booking
			click("Configuration.AdminMenu_Booking", 100, "clickable");

			// Navigating to BookingRulesSchedules
			click("Configuration.AdminMenu_Booking_BookingRulesSchedules", 100, "clickable");

			// Navigating to ReservationTypeSchedule
			mouseHover("Configuration.link_ResevationTypeSchedule", 100, "clickable");
			jsClick("Configuration.link_ResevationTypeSchedule", 100, "clickable");

			// Validate RESERVATION TYPE exist
			System.out.println("Reservation Type :"+configMap.get("RESERVATION_TYPE"));
			if (ValidateGridData("Configuration.grid_ResevationTypeSchedule", configMap.get("RESERVATION_TYPE"))){

				System.out.println("RESERVATION TYPE already exists :"+configMap.get("RESERVATION_TYPE"));
				logger.log(LogStatus.PASS, "RESERVATION TYPE already exists :"+configMap.get("RESERVATION_TYPE"));

			}else{

				// Clicking on Tab Template
				jsClick("Configuration.link_ReservationTypes_New", 100, "clickable");				

				//Wait for element to load				
				Utils.WebdriverWait(100, "Configuration.btn_CreatePaymentMethods_Save", "clickable");

				// Entering the End date
				textBox("Configuration.txt_ResevationTypeSchedule_enddate",configMap.get("END_DATE"), 100, "presence");

				// Entering the Reservation Type
				textBox("Configuration.txt_ReservationTypes_TemplateReservationType",configMap.get("RESERVATION_TYPE"), 100, "presence");

				// Clicking on Copy Button
				Utils.WebdriverWait(100, "Configuration.btn_CreatePaymentMethods_Save", "clickable");
				jsClick("Configuration.btn_CreatePaymentMethods_Save", 100, "clickable");


				// Validate Reservation Type is copied successfully and available in property
				if(ValidateGridData("Configuration.grid_ResevationTypeSchedule", configMap.get("RESERVATION_TYPE"))){
					System.out.println("*************Reservation Type Schedule is created successfully***************");
					logger.log(LogStatus.PASS, "Reservation Type Schedule is created successfully :"+configMap.get("RESERVATION_TYPE"));	
				}else{
					System.out.println("*************Reservation Type Schedule is not created successfully***************");
					logger.log(LogStatus.FAIL, "Reservation Type Schedule is not created successfully :"+configMap.get("RESERVATION_TYPE"));
				}


			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Reservation Type Schedule  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Reservation Type Schedule not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			tearDown();
			throw (e);

		}

	}	
	/**
	 * @author uaviti
	 * @description This Method is to navigate to Tool Boc Menu
	 * @throws Exception
	 * @Date: 11/29/2018
	 * @Revision History - Initial version
	 *  
	 */
	public static void navigateToToolBox() throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {

			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu",100,"clickable");
			click("Configuration.mainMenu",100,"clickable");

			// Navigating to ToolBox Menu
			mouseHover("Configuration.ToolBoxMenu",100,"clickable");
			click("Configuration.ToolBoxMenu",100,"clickable");

			//Validate if the control is on ToolBox page
			isDisplayed("Configuration.ToolBoxPageHeader", "Tool Box Page Header");

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Navigation to ToolBox Menu  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Navigation to ToolBox Menu :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/**
	 * @author uaviti
	 * @description - This method is to activate or deactivate the licenses
	 * @param configMap - Will contain the test data required to activate the license
	 * @Date: 11/30/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */
	public static void activateLicenses(HashMap<String, String> configMap) throws Exception{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {

			// Navigating to System Setup
			mouseHover("Configuration.ToolBox.menuSystemSetup",100,"clickable");
			click("Configuration.ToolBox.menuSystemSetup",100,"clickable");

			// Navigating to OPERA Licenses
			click("Configuration.ToolBox.SystemSetup.subMenuOperaLicenses",100,"clickable");
			waitForSpinnerToDisappear(100);
			//Validate if the control in on the correct page
			isDisplayed("Configuration.opera_licenses.radBtnProperty", "Property Radio Button");

			//Select the property radio button
			//click("Configuration.opera_licenses.radBtnProperty", 100,"presence");
			click("Configuration.opera_licenses.radBtnProperty",100,"clickable");
			waitForSpinnerToDisappear(100);
			//getting the property value from the data sheet
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
			propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
			System.out.println("Property Config: " + propertyMap);

			String property = propertyMap.get("Property");
			System.out.println("prop: " + property);

			//Set the property data
			//textBox("Configuration.opera_licenses.lovProperty", property, 100, "presence");

			//Click on Search Button
			click("Configuration.opera_licesnses.btnSearch",100,"clickable");
			waitForSpinnerToDisappear(100);
			//Verify if the Licenses list is displayed
			isDisplayed("Configuration.opera_licesnses.tblAvailableLicenses", "Licenses List");

			//Fetch Licenses table Column Data and validate


			java.util.List<WebElement>  rows = Utils.elements("Configuration.opera_licesnses.tblAvailableLicenses"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;

			if(rows.size() > 0)
			{
				for(int i = 0; i < rows.size(); i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().contains(configMap.get("APPLICATION")))
					{
						System.out.println("License "+rows.get(i).getText()+" is listed");
						flag = true;
						logger.log(LogStatus.PASS, "License "+rows.get(i).getText()+" is listed");

						// click on the license
						//click(rows.get(i));
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", rows.get(i));						
						System.out.println("clicked on Licence");
						logger.log(LogStatus.PASS, "clicked on License");
						waitForSpinnerToDisappear(100);

						//dynamically constructing the Xpath to validate the Activate checkbox																		 
						String strXPath = "((//*[@data-ocid='TBL_TS1']/div[2]/table/tbody/tr/td[1])["+ (i+1) +"]/following::*[@data-ocid[contains(.,'CHK_CNTNT_SBC1')]]/*/img)[1]";
						if(driver.findElement(By.xpath(strXPath)).getAttribute("title").equalsIgnoreCase("Checked"))
						{
							waitForSpinnerToDisappear(100);
							if(configMap.get("ADDON_LICENSE_NAMES")!=null){
								String[] strAddonLic = configMap.get("ADDON_LICENSE_NAMES").split(";");
								System.out.println("Lenth of the array :: "+strAddonLic.length);
								for(int k =0; k < strAddonLic.length; k++){

									//Enter the Add on license in Filter text box
									textBox("Configuration.AddonLicense.txtBoxSearch", strAddonLic[k], 100, "presence");

									//Click on Filter Button
									click("Configuration.AddonLicense.btnFilter",100,"clickable");
									waitForSpinnerToDisappear(100);

									//Constructing the XPath dynamically to locate the add on license
									strXPath = "//*[@data-ocformvalue[contains(.,"+strAddonLic[k]+")]]";
									if(driver.findElement(By.xpath(strXPath)).isDisplayed())
									{
										String strFlag = getAttributeOfElement("Configuration.AddonLicense.toggleAddOnLicense", "checked", 100, "presence");
										if(strFlag == null){

											//Click on enabling the license toggle
											jsClick("Configuration.AddonLicense.toggleAddOnLicense",100,"clickable");
											waitForSpinnerToDisappear(100);

											//Verify if the confirmation Poup is displayed
											isDisplayed("Configuration.AddonLicense.popupConfirmation", "Confirmation Popup");

											//Click on yes on the Confirmation popup
											click("Configuration.AddonLicense.popupConfirmation.btnYes",100,"clickable");
											waitForSpinnerToDisappear(100);
											strFlag = getAttributeOfElement("Configuration.AddonLicense.toggleAddOnLicense", "checked", 100, "presence");
											if(strFlag != null)
												logger.log(LogStatus.PASS, "AddOn License "+rows.get(i).getText()+" is enabled");
											else
												logger.log(LogStatus.FAIL, "Failed to enable AddOn License "+rows.get(i).getText());
										}
										else
										{
											logger.log(LogStatus.PASS, "AddOn License "+rows.get(i).getText()+" is already enabled");
										}
									}
									else
									{
										logger.log(LogStatus.FAIL, "AddOn License "+strAddonLic[k]+" is Not listed");
									}
								}
							}
						}
						else{
							System.out.println("in else");
							if(isDisplayed("Configuration.LicenseDetails.btnActivate", "Actvate button")){
								System.out.println("in else1");
								waitForSpinnerToDisappear(100);
								Thread.sleep(5000);
								jsTextbox("Configuration.LicenseDetails.txtProductCode", configMap.get("LICENSE_NAME"));
								System.out.println("***** Set the value of  Product code as " + configMap.get("LICENSE_NAME")+ "*****");
								logger.log(LogStatus.PASS, "Set the value of  Product code as " + configMap.get("LICENSE_NAME"));

								waitForSpinnerToDisappear(100);
								textBox("Configuration.LicenseDetails..txtNumberOfRooms", configMap.get("NO_OF_ROOMS"), 100, "clickable");
								waitForSpinnerToDisappear(100);

								String strDateFormat = getAttributeOfElement("Configuration.LicenseDetails.txtExpiryDate","placeholder",100, "presence");
								strDateFormat = strDateFormat.replace("DD","dd");
								System.out.println("strdate format :: "+strDateFormat);
								strDateFormat = strDateFormat.replace("YYYY","yyyy");
								System.out.println("strdate format :: "+strDateFormat);

								SimpleDateFormat sdfdate = new SimpleDateFormat(strDateFormat);
								String timeZone = "IST";
								Date date = new Date();
								sdfdate.setTimeZone(TimeZone.getTimeZone(timeZone));
								sdfdate.format(date);

								Date newbusinessDate=new SimpleDateFormat(strDateFormat).parse(sdfdate.format(date));								
								Calendar c = Calendar.getInstance();
								c.setTime(newbusinessDate); 
								c.add(Calendar.YEAR, 30 );
								String output = sdfdate.format(c.getTime());

								textBox("Configuration.LicenseDetails.txtExpiryDate", output, 100, "presence");
								waitForSpinnerToDisappear(100);			

								jsClick("Configuration.LicenseDetails.btnActivate", 100, "clickable");
								waitForSpinnerToDisappear(100);

								if(configMap.get("ADDON_LICENSE_NAMES")!=null){
									String[] strAddonLic = configMap.get("ADDON_LICENSE_NAMES").split(";");
									System.out.println("Lenth of the array :: "+strAddonLic.length);
									for(int k =0; k < strAddonLic.length; k++){

										//Enter the Add on license in Filter text box
										textBox("Configuration.AddonLicense.txtBoxSearch", strAddonLic[i], 100, "presence");

										//Click on Filter Button
										click("Configuration.AddonLicense.btnFilter",100,"clickable");
										waitForSpinnerToDisappear(100);

										//Constructing the XPath dynamically to locate the add on license
										strXPath = "//*[@data-ocformvalue[contains(.,"+strAddonLic[i]+")]]";
										if(driver.findElement(By.xpath(strXPath)).isDisplayed())
										{
											String strFlag = getAttributeOfElement("Configuration.AddonLicense.toggleAddOnLicense", "checked", 100, "presence");
											if(strFlag == null){

												//Click on enabling the license toggle
												jsClick("Configuration.AddonLicense.toggleAddOnLicense",100,"clickable");
												waitForSpinnerToDisappear(100);

												//Verify if the confirmation Poup is displayed
												isDisplayed("Configuration.AddonLicense.popupConfirmation", "Confirmation Popup");

												//Click on yes on the Confirmation popup
												click("Configuration.AddonLicense.popupConfirmation.btnYes",100,"clickable");
												waitForSpinnerToDisappear(100);
												strFlag = getAttributeOfElement("Configuration.AddonLicense.toggleAddOnLicense", "checked", 100, "presence");
												if(strFlag != null)
													logger.log(LogStatus.PASS, "AddOn License "+rows.get(i).getText()+" is enabled");
												else
													logger.log(LogStatus.FAIL, "Failed to enable AddOn License "+rows.get(i).getText());
											}
											else
											{
												logger.log(LogStatus.PASS, "AddOn License "+rows.get(i).getText()+" is already enabled");
											}
										}
										else
										{
											logger.log(LogStatus.FAIL, "AddOn License "+strAddonLic[i]+" is Not listed");
										}
									}
								}
							}
						}
						break;
					}
					else
					{
						System.out.println("Given License is not found and looing through the grid :: "+rows.get(i).getText());
						//logger.log(LogStatus.FAIL, "Given License is not present in the system:: "+rows.get(i).getText());
					}
				}
				if(!flag){
					System.out.println("Given Group is not listed");
					logger.log(LogStatus.FAIL, "Given Group is not listed");
				}

			}


		} catch (Exception e) {
			try {

				logger.log(LogStatus.FAIL, "activateLicenses  :: Failed " + e.getMessage());
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));			
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();				
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "activateLicenses :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			tearDown();
			throw (e);

		}
	}

	/**
	 * @author vamsiM
	 * @description - This method is to activate or deactivate the Application Functions and Parameters.based on the value set against each function/parameter in test data this method will activate or deactivate a particular function.
	 * @param configMap - Will contain the test data required to activate or deactivate the application functions 
	 * @Date: 24/01/19
	 * @Revision History - Initial version
	 * @throws Exception
	 */
	public static void applicationFunctionsParametersSettings(Map<String, String> configMap) throws Exception{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		int intNumOfFunctions = 0;
		try {

			System.out.println("configmap:: "+configMap);
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu",100,"clickable");
			//click("Configuration.mainMenu",100,"clickable");
			jsClick("Configuration.mainMenu");
			System.out.println("***** Clicked on Main Menu *****");
			logger.log(LogStatus.PASS, "Clicked on Main Menu");

			// Navigating to Administration Menu
			mouseHover("Configuration.AdminstrationMenu",100,"clickable");
			click("Configuration.AdminstrationMenu",100,"clickable");

			// Navigating to Enterprise Menu
			//WebdriverWait(100,"Configuration.administration.MenuEnterprise","presence");
			mouseHover("Configuration.administration.MenuEnterprise",100,"clickable");
			click("Configuration.administration.MenuEnterprise",100,"clickable");

			// Navigating to OPERA Controls
			//mouseHover("Configuration.administration.MenuEnterprise.OperaControls",100,"clickable");
			jsClick("Configuration.administration.MenuEnterprise.OperaControls");
			System.out.println("***** Clicked on OperaControls Menu *****");
			logger.log(LogStatus.PASS, "Clicked on OperaControls Menu");
			waitForSpinnerToDisappear(100);
			//Validate if the control in on the correct page
			isDisplayed("Configuration.OperaControls.groupSelection", "Opera Controls Group");

			//Select the group Corresponding to the Application Function
			java.util.List<WebElement>  lstGroups = Utils.elements("Configuration.OperaControls.groupSelection"); 
			System.out.println("No of groups are : " + lstGroups.size());
			boolean flag = false;

			if(lstGroups.size() > 0)
			{
				for(int i = 0; i < lstGroups.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", lstGroups.get(i));

					if(lstGroups.get(i).getText().equalsIgnoreCase(configMap.get("GROUP")))
					{
						System.out.println("Group "+lstGroups.get(i).getText()+" is listed");
						flag = true;
						logger.log(LogStatus.PASS, "Group "+lstGroups.get(i).getText()+" is listed");

						//Select the Group
						JavascriptExecutor executor = (JavascriptExecutor) driver;
						executor.executeScript("arguments[0].click();", lstGroups.get(i));
						System.out.println("***** Clicked on "+ lstGroups.get(i).getText() + "*****");
						logger.log(LogStatus.PASS, "Clicked on " + lstGroups.get(i).getText());
						waitForSpinnerToDisappear(100);
						if(configMap.get("GROUP_DISPLAY_NAME") !=null){								
							//Verify if the corresponding group is active or not by constructing the Xpath dynamically
							System.out.println("***** Getting the Group "+ lstGroups.get(i).getText() + " Status *****");
							logger.log(LogStatus.PASS, "Getting the Group "+ lstGroups.get(i).getText() + " Status");
							String strStatus = driver.findElement(By.xpath("//h3[contains(.,'"+lstGroups.get(i).getText()+"')]/following::*[text()='"+configMap.get("GROUP_DISPLAY_NAME")+"']/preceding::button[1]/span")).getText();
							System.out.println("***** Group "+ configMap.get("GROUP_DISPLAY_NAME") + " Status is "+strStatus +"*****");
							logger.log(LogStatus.PASS, "Group "+ configMap.get("GROUP_DISPLAY_NAME") + " Status is "+strStatus);

							//Activating the Group
							if(!strStatus.equalsIgnoreCase("active")){
								System.out.println("***** Activating the Group "+ lstGroups.get(i).getText() + "*****");
								logger.log(LogStatus.PASS, "Activating the Group " + lstGroups.get(i).getText());
								((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+lstGroups.get(i).getText()+"')]/following::*[text()='"+configMap.get("GROUP_DISPLAY_NAME")+"']/preceding::button[1]")));
								driver.findElement(By.xpath("//h3[contains(.,'"+lstGroups.get(i).getText()+"')]/following::*[text()='"+configMap.get("GROUP_DISPLAY_NAME")+"']/preceding::button[1]")).click();
								System.out.println("***** Clicked on Activate Button *****");
								logger.log(LogStatus.PASS, "Clicked on Activate Button");
								waitForSpinnerToDisappear(100);
								//Verifying if the Conform functionality Popup is displayed
								isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality", "Confirm Functionality Popup");

								//Click on Activate Button in the Popup
								jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate",100,"clickbale");
								waitForSpinnerToDisappear(100);
							}
						}

						for (Map.Entry<String, String> entry : configMap.entrySet()) {
							System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
							if (intNumOfFunctions > 0) {
								if (configMap.containsKey("FUNTION_" + intNumOfFunctions)
										&& configMap.get("FUNTION_" + intNumOfFunctions) != null) {
									setApplicationFunctionsParameters(configMap.get("GROUP"),"Functions", configMap.get("FUNTION_" + intNumOfFunctions));
								}
								else if (configMap.containsKey("PARAM_" + intNumOfFunctions)
										&& configMap.get("PARAM_" + intNumOfFunctions) != null) {
									setApplicationFunctionsParameters(configMap.get("GROUP"),"Parameters", configMap.get("PARAM_" + intNumOfFunctions));
								}
								else if (configMap.containsKey("SETTING_" + intNumOfFunctions)
										&& configMap.get("SETTING_" + intNumOfFunctions) != null) {
									setApplicationSettings(configMap.get("GROUP"),"Settings", configMap.get("SETTING_" + intNumOfFunctions));
								}								
								else if (configMap.containsKey("ATTRIBUTE_" + intNumOfFunctions)
										&& configMap.get("ATTRIBUTE_" + intNumOfFunctions) != null) {
									setFunctionAttributes(configMap.get("GROUP"),"Functions", configMap.get("FUNCTION"),configMap.get("ATTRIBUTE_" + intNumOfFunctions));
								}
								else if (configMap.containsKey("PAR_ATTRIBUTE_" + intNumOfFunctions)
										&& configMap.get("PAR_ATTRIBUTE_" + intNumOfFunctions) != null) {
									setParameterAttributes(configMap.get("GROUP"),"Parameters", configMap.get("PARAMETER"),configMap.get("PAR_ATTRIBUTE_" + intNumOfFunctions));
								}
							}
							intNumOfFunctions = intNumOfFunctions + 1;
						}
						break;
					}
				}

				if(!flag){
					System.out.println("Given Group is not listed");
					logger.log(LogStatus.FAIL, "Given Group is not listed");
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "applicationFunctionsParameters  :: Failed ");
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "applicationFunctionsParameters :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			tearDown();
			throw (e);

		}
	}


	/**
	 * @author vamsiM
	 * @description - This private method is to activate/deactivate or set the data for application Parameter attributes
	 * @param strGroup - Application Group
	 * @param strParamType - Specifies if it is a Function or Parameter
	 * @param strFuncValue - Specifies the Function Name
	 * @param strAttrValue - In the format of <value>;<flag> -> value is which attribute specific to a function and flag (Y or N) or any other value to set
	 * @Date: 24/01/19 
	 * @Revision History - Initial version
	 * @throws Exception
	 */

	private static void setParameterAttributes(String strGroup, String  strParamType, String strFuncValue, String strAttrValue) throws Exception{

		String[] strAttributeArr = strAttrValue.split(":");
		String strAttribute = strAttributeArr[0];
		String strFlag = strAttributeArr[1];

		//Check the status of the Given Function before activating or deactivating it by constructing the XPath dynamically
		System.out.println("***** Getting the function " +strFuncValue + " Status *****");
		logger.log(LogStatus.PASS, "Getting the "+strParamType.substring(0, strParamType.length() - 1)+ " " +strFuncValue + " Status");
		String strStatus = driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/preceding::button[1]/span")).getText();
		System.out.println("*****"+ strParamType.substring(0, strParamType.length() - 1)+ strFuncValue + " Status is "+strStatus +" *****");
		logger.log(LogStatus.PASS, "Function "+ strFuncValue + " Status is "+strStatus);

		strStatus = strStatus.toLowerCase();

		//Activating the Function
		if(!strStatus.equalsIgnoreCase("on") ){
			System.out.println("***** Activating the Function "+ strFuncValue + "*****");
			logger.log(LogStatus.PASS, "Activating the Group" + strFuncValue);
			driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/preceding::button[1]")).click();
			System.out.println("***** Clicked on Activate Button *****");
			logger.log(LogStatus.PASS, "Clicked on Activate Button");
			waitForSpinnerToDisappear(100);

			//Verifying if the Conform functionality Popup is displayed
			isDisplayed("Configuration.PMSFunctions.popUpConfigureSetting", "Confirm Functionality Popup");

			//Click on Activate Button in the Popup
			jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnTurnOn",100,"clickbale");
			waitForSpinnerToDisappear(100);
		}


		if(strFlag.equalsIgnoreCase("y") || strFlag.equalsIgnoreCase("n")){

			//Check the status of the Given Attribute before activating/deactivating it by constructing the XPath dynamically
			System.out.println("***** Getting the status of the attribute "+ strAttribute+ " for the Function " +strFuncValue +" *****");
			logger.log(LogStatus.PASS, "Getting the status of the attribute "+ strAttribute+ " for the Function " +strFuncValue);
			String strAttrStatus = driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]/span")).getText();
			System.out.println("***** Status of the attribute "+ strAttribute+ " for the Function " +strFuncValue+" is "+strStatus +" *****");
			logger.log(LogStatus.PASS, "Status of the attribute "+ strAttribute+ " for the Function " +strFuncValue+" is "+strStatus);

			strAttrStatus = strAttrStatus.toLowerCase();

			switch(strAttrStatus){
			case "on":
				if(strFlag.equalsIgnoreCase("y")){
					System.out.println("***** Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned on *****");
					logger.log(LogStatus.PASS, "Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned on");
				}
				else{
					System.out.println("***** Turning Off the Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " *****");
					logger.log(LogStatus.PASS, "Turning Off the Attribute "+ strAttribute+ " for the Function " +strFuncValue );
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]")));
					System.out.println("***** Clicked on Turning Off Button *****");
					logger.log(LogStatus.PASS, "Clicked on Turning Off Button");
					waitForSpinnerToDisappear(100);
					if(strParamType.equalsIgnoreCase("functions")){
						//Verifying if the Confirm setting Popup is displayed
						isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirm Parameter Popup");
						jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnTurnOff",100,"clickable");
						waitForSpinnerToDisappear(100);
					}
				}
				break;
			case "off":
				if(strFlag.equalsIgnoreCase("y")){
					System.out.println("***** Turning On the Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " *****");
					logger.log(LogStatus.PASS, "Turning On the Attribute "+ strAttribute+ " for the Function " +strFuncValue );
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]")));
					System.out.println("***** Clicked on Turning On Button *****");
					logger.log(LogStatus.PASS, "Clicked on Turning On Button");
					waitForSpinnerToDisappear(100);
					if(strParamType.equalsIgnoreCase("functions")){
						//Verifying if the Confirm Setting Popup is displayed
						isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirm Parameter Popup");
						jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnTurnOn",100,"clickable");
						waitForSpinnerToDisappear(100);
					}
				}
				else{
					System.out.println("***** Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned off *****");
					logger.log(LogStatus.PASS, "Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned off ");
				}				
				break;
			default:
				System.out.println("***** Invalid Status *****");
				logger.log(LogStatus.FAIL, "Invalid Status:: "+strAttrStatus);
			}
		}
		else{
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			switch(strAttribute){

			case "Template Pattern":					

				//Constructing the XPath dynamically to set the value for this attribute				
				executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/following::a[1]")));
				System.out.println("***** Clicked on Attribute's Edit Button *****");
				logger.log(LogStatus.PASS, "Clicked on Attribute's Edit Button");				
				waitForSpinnerToDisappear(100);

				//Verifying if the Confirm Setting Popup is displayed
				isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

				//Set the data for the function attribute
				textBox("Configuration.PMSSettings.txtAutoRollDateMinutes", strFlag);

				//Click on Save button
				jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnSave",100,"clickable");	
				waitForSpinnerToDisappear(100);
				break;	
			}			
		}

	}

	/**
	 * @author uaviti
	 * @description - This private method is to activate or deactivate the Application Functions and Parameters
	 * @param strGroup - Application Group
	 * @param strParamType - Specifies if it is a Function or Parameter
	 * @param strFuncValue - In the format of <value>:<flag> -> value is which function or which parameter and flag is Y or N to activate or deactivate
	 * @Date: 11/30/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */

	private static void setApplicationFunctionsParameters(String strGroup, String strParamType, String strFuncValue) throws Exception{

		String[] strFuncArr = strFuncValue.split(":");
		String strFunction = strFuncArr[0];
		String strFlag = strFuncArr[1];

		//Check the status of the Given Function/Parameter before activating or deactivating it by constructing the XPath dynamically
		System.out.println("***** Getting the "+strParamType.substring(0, strParamType.length() - 1)+ " " +strFunction + " Status *****");
		logger.log(LogStatus.PASS, "Getting the "+strParamType.substring(0, strParamType.length() - 1)+ " " +strFunction + " Status");
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFunction+"']/preceding::button[1]/span")));
		String strStatus = driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFunction+"']/preceding::button[1]/span")).getText();
		System.out.println("*****"+ strParamType.substring(0, strParamType.length() - 1)+ strFunction + " Status is "+strStatus +" *****");
		logger.log(LogStatus.PASS, "Function "+ strFunction + " Status is "+strStatus);

		strStatus = strStatus.toLowerCase();

		switch(strStatus){
		case "active":
		case "on":
			if(strFlag.equalsIgnoreCase("y")){
				System.out.println("***** "+strParamType.substring(0, strParamType.length() - 1)+" :: " + strFunction +" is already activated *****");
				logger.log(LogStatus.PASS, strParamType.substring(0, strParamType.length()-1)+" :: "+ strFunction +" is already activated");
			}
			else{
				System.out.println("***** Deactivating the "+strParamType.substring(0, strParamType.length()-1)+" "+ strFunction + "*****");
				logger.log(LogStatus.PASS, "Deactivating the "+strParamType.substring(0, strParamType.length()-1)+" " + strFunction );
				System.out.println("xpath is: "+"//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFunction+"']/preceding::button[1]");
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFunction+"']/preceding::button[1]")));
				System.out.println("***** Clicked on Inactive Button *****");
				logger.log(LogStatus.PASS, "Clicked on Inactive Button");
				waitForSpinnerToDisappear(100);
				//Click on Inactive/off Button in the Popup
				if(strParamType.equalsIgnoreCase("functions")){
					//Verifying if the Conform functionality Popup is displayed
					isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality", "Confirm Functionality Popup");
					jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnDeactivate",100,"clickable");
					waitForSpinnerToDisappear(100);
					if(isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality.btnDeactivate2", "Activate button")){
						jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnDeactivate2",100,"clickbale");
						waitForSpinnerToDisappear(100);
					}
				}
				else if(strParamType.equalsIgnoreCase("parameters")){
					//Verifying if the Conform functionality Popup is displayed
					isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirm Parameter Popup");
					jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnTurnOff",100,"clickable");
					waitForSpinnerToDisappear(100);
				}
			}
			break;
		case "inactive":
		case "off":
			if(strFlag.equalsIgnoreCase("y")){
				System.out.println("***** Activating the "+strParamType.substring(0, strParamType.length()-1)+ " " +strFunction + "*****");
				logger.log(LogStatus.PASS, "Activating the "+strParamType.substring(0, strParamType.length()-1)+ " "+strFunction );
				System.out.println("xpath is: "+"//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFunction+"']/preceding::button[1]");
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFunction+"']/preceding::button[1]")));
				System.out.println("***** Clicked on Activate Button *****");
				logger.log(LogStatus.PASS, "Clicked on Activate Button");
				waitForSpinnerToDisappear(100);
				//Click on Activate/On Button in the Popup
				if(strParamType.equalsIgnoreCase("functions")){					
					//Verifying if the Conform functionality Popup is displayed
					isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality", "Confirm Functionality Popup");
					jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate",100,"clickable");
					waitForSpinnerToDisappear(100);					
					if(isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate2", "Activate button")){
						jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate2",100,"clickbale");
						waitForSpinnerToDisappear(100);
					}
				}
				else if(strParamType.equalsIgnoreCase("parameters")){
					//Verifying if the Conform functionality Popup is displayed
					isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirm Parameter Popup");
					jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnTurnOn",100,"clickable");
					waitForSpinnerToDisappear(100);
				}
			}
			else{
				System.out.println("*****"+ strParamType.substring(0, strParamType.length()-1)+" :: "+ strFunction +" is already deactivated *****");
				logger.log(LogStatus.PASS, strParamType.substring(0, strParamType.length()-1)+" :: "+ strFunction +" is already deactivated ");
			}				
			break;
		default:
			System.out.println("***** Invalid Status *****");
			logger.log(LogStatus.FAIL, "Invalid Status:: "+strStatus);
		}
	}


	/**
	 * @author uaviti
	 * @description - This private method is to set the data for application settings
	 * @param strGroup - Application Group
	 * @param strParamType - Specifies if it is a Function or parameter or setting
	 * @param strAppSettingValue - In the format of <Setting name>:<Value> -> value is Application setting name: value to set
	 * @Date: 11/30/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 * Note: Settings have to be handled case by case as the Xpaths and number of values to be set are not same for each setting. This switch cases would grow as and when there is a new setting to be added
	 */

	private static void setApplicationSettings(String strGroup, String strParamType, String strAppSettingValue) throws Exception{

		String[] strSettingArr = strAppSettingValue.split(":");
		String strSetting = strSettingArr[0];
		String strValue = strSettingArr[1];
		System.out.println("strSetting:: "+strSetting);
		System.out.println("StrValue:: "+strValue);
		waitForSpinnerToDisappear(100);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));

		switch(strSetting){
		case "Cash Shift Drop":
			//Constructing the XPath dynamically to set the value for this attribute
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);

			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting

			//			driver.findElement(OR.getLocator("Configuration.PMSSettings.txtCashShiftDrop")).clear();
			//			driver.findElement(OR.getLocator("Configuration.PMSSettings.txtCashShiftDrop")).sendKeys(String.valueOf(strValue));
			textBox("Configuration.PMSSettings.txtCashShiftDrop", strValue,100, "visible");

			//Click on Save button
			jsClick("Configuration.PMSSettings.btnSave",100,"clickable");
			waitForSpinnerToDisappear(100);
			if(isDisplayed("Configuration.PMSParameters.btnSelectCashShiftDropCancel", "Cancel Button"))
				jsClick("Configuration.PMSParameters.btnSelectCashShiftDropCancel",100,"clickable");
			waitForSpinnerToDisappear(100);
			break;
		case "Check Shift Drop":
			System.out.println("in Check shift drop");
			//Constructing the XPath dynamically to set the value for this attribute
			System.out.println("Xpath ::    //h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]" );
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			//			driver.findElement(OR.getLocator("Configuration.PMSSettings.txtCheckShiftDrop")).clear();
			//			driver.findElement(OR.getLocator("Configuration.PMSSettings.txtCheckShiftDrop")).sendKeys(String.valueOf(strValue));
			textBox("Configuration.PMSSettings.txtCheckShiftDrop", strValue,100, "visible");

			//Click on Save button
			jsClick("Configuration.PMSSettings.btnSave",100,"clickable");
			waitForSpinnerToDisappear(100);		
			if(isDisplayed("Configuration.PMSParameters.btnSelectCashShiftDropCancel", "Cancel Button"))
				jsClick("Configuration.PMSParameters.btnSelectCashShiftDropCancel",100,"clickable");
			waitForSpinnerToDisappear(100);
			break;
		case "Direct Bill Settlement Code":
			//Constructing the XPath dynamically to set the value for this attribute
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			//			driver.findElement(OR.getLocator("Configuration.PMSSettings.txtDrectBillSettlement")).clear();
			//			driver.findElement(OR.getLocator("Configuration.PMSSettings.txtDrectBillSettlement")).sendKeys(String.valueOf(strValue));
			textBox("Configuration.PMSSettings.txtDrectBillSettlement", strValue,100, "visible");

			//Click on Save button
			jsClick("Configuration.PMSSettings.btnSave",100,"clickable");
			waitForSpinnerToDisappear(100);	
			if(isDisplayed("Configuration.PMSParameters.btnSelectCashShiftDropCancel", "Cancel Button"))
				jsClick("Configuration.PMSParameters.btnSelectCashShiftDropCancel",100,"clickable");
			waitForSpinnerToDisappear(100);
			break;
		case "Auto Roll Date Minutes":
			//Constructing the XPath dynamically to set the value for this attribute
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			Thread.sleep(10000);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			textBox("Configuration.PMSSettings.txtAutoRollDateMinutes", strValue,100, "presence");

			//Click on Save button
			jsClick("Configuration.PMSSettings.txtAutoRollDateMinutesSave",100,"clickable");
			waitForSpinnerToDisappear(100);		
			break;
		case "Country Check":
			//Constructing the XPath dynamically to set the value for this attribute
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			textBox("Configuration.PMSSettings.txtCountryCheck", strValue,100, "presence");

			//Click on Save button
			jsClick("Configuration.PMSSettings.btnSave",100,"clickable");
			waitForSpinnerToDisappear(100);	
			break;
		case "Maximum Number Of Open Business Dates":
			//Constructing the XPath dynamically to set the value for this attribute
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			textBox("Configuration.PMSSettings.txtMaxNoOfOpenBusinessDates", strValue, 100, "presence");

			//Click on Save button
			jsClick("Configuration.PMSSettings.txtAutoRollDateMinutesSave",100,"clickable");
			waitForSpinnerToDisappear(100);	
			break;
		case "Night Audit Cashier":
			//Constructing the XPath dynamically to set the value for this attribute
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			textBox("Configuration.PMSSettings.txtNightAuditCashier", strValue, 100, "presence");

			//Click on Save button
			jsClick("Configuration.PMSSettings.btnSave",100,"clickable");
			waitForSpinnerToDisappear(100);	
			break;
		case "Update Vacant Room Status":
			//Constructing the XPath dynamically to set the value for this attribute
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			textBox("Configuration.PMSSettings.txtUpdateVacantRoomStatus", strValue, 100, "presence");

			//Click on Save button
			jsClick("Configuration.PMSSettings.btnSave",100,"clickable");
			waitForSpinnerToDisappear(100);	
			break;

		case "Default Search Mode":
			//Constructing the XPath dynamically to set the value for this attribute			
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")));
			//driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strSetting+"']/following::a[1]")).click();
			System.out.println("***** Clicked on Setting's Edit Button *****");
			logger.log(LogStatus.PASS, "Clicked on Setting's Edit Button");
			waitForSpinnerToDisappear(100);
			//Verifying if the Confirm Setting Popup is displayed
			isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

			//Set the data for the function attribute/Setting
			textBox("Configuration.PMSSettings.txtDefaultSearchMode", strValue, 100, "presence");

			//Click on Save button
			jsClick("Configuration.PMSSettings.btnSave",100,"clickable");
			waitForSpinnerToDisappear(100);	
			break;	
		default:
			System.out.println("***** Invalid Setting Value *****");
			logger.log(LogStatus.WARNING, "Invalid Setting Value:: "+strSetting);
		}


	}

	/**
	 * @author uaviti
	 * @description - This private method is to activate/deactivate or set the data for application function attributes
	 * @param strGroup - Application Group
	 * @param strParamType - Specifies if it is a Function or Parameter
	 * @param strFuncValue - Specifies the Function Name
	 * @param strAttrValue - In the format of <value>;<flag> -> value is which attribute specific to a function and flag (Y or N) or any other value to set
	 * @Date: 11/30/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */

	private static void setFunctionAttributes(String strGroup, String  strParamType, String strFuncValue, String strAttrValue) throws Exception{

		String[] strAttributeArr = strAttrValue.split(":");
		String strAttribute = strAttributeArr[0];
		String strFlag = strAttributeArr[1];

		//Check the status of the Given Function before activating or deactivating it by constructing the XPath dynamically
		System.out.println("***** Getting the function " +strFuncValue + " Status *****");
		logger.log(LogStatus.PASS, "Getting the "+strParamType.substring(0, strParamType.length() - 1)+ " " +strFuncValue + " Status");
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/preceding::button[1]/span")));
		String strStatus = driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/preceding::button[1]/span")).getText();
		System.out.println("*****"+ strParamType.substring(0, strParamType.length() - 1)+ strFuncValue + " Status is "+strStatus +" *****");
		logger.log(LogStatus.PASS, "Function "+ strFuncValue + " Status is "+strStatus);

		strStatus = strStatus.toLowerCase();

		//Activating the Function
		if(!strStatus.equalsIgnoreCase("active")){
			System.out.println("***** Activating the Function "+ strFuncValue + "*****");
			logger.log(LogStatus.PASS, "Activating the Group" + strFuncValue);
			String strXpath = "//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/preceding::button[1]";
			System.out.println("Xpath:: "+strXpath);
			//((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/preceding::button[1]")));
					
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/preceding::button[1]")));
			
			System.out.println("***** Clicked on Activate Button *****");
			logger.log(LogStatus.PASS, "Clicked on Activate Button");
			waitForSpinnerToDisappear(100);

			//Verifying if the Conform functionality Popup is displayed
			isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality", "Confirm Functionality Popup");

			//Click on Activate Button in the Popup
			jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate",100,"clickbale");
			waitForSpinnerToDisappear(100);
			if(isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate2", "Activate button")){
				jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate2",100,"clickbale");
				waitForSpinnerToDisappear(100);
			}
		}


		if(strFlag.equalsIgnoreCase("y") || strFlag.equalsIgnoreCase("n")){

			//Check the status of the Given Attribute before activating/deactivating it by constructing the XPath dynamically
			System.out.println("***** Getting the status of the attribute "+ strAttribute+ " for the Function " +strFuncValue +" *****");
			logger.log(LogStatus.PASS, "Getting the status of the attribute "+ strAttribute+ " for the Function " +strFuncValue);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]/span")));
			String strAttrStatus = driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]/span")).getText();
			System.out.println("***** Status of the attribute "+ strAttribute+ " for the Function " +strFuncValue+" is "+strStatus +" *****");
			logger.log(LogStatus.PASS, "Status of the attribute "+ strAttribute+ " for the Function " +strFuncValue+" is "+strStatus);

			strAttrStatus = strAttrStatus.toLowerCase();

			switch(strAttrStatus){
			case "active":
			case "on":
				if(strFlag.equalsIgnoreCase("y")){
					System.out.println("***** Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned on *****");
					logger.log(LogStatus.PASS, "Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned on");
				}
				else{
					System.out.println("***** Turning Off the Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " *****");
					logger.log(LogStatus.PASS, "Turning Off the Attribute "+ strAttribute+ " for the Function " +strFuncValue );
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]/span")));
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]")));
					System.out.println("***** Clicked on Turning Off Button *****");
					logger.log(LogStatus.PASS, "Clicked on Turning Off Button");
					waitForSpinnerToDisappear(100);

					if(strParamType.equalsIgnoreCase("functions")){
						//Verifying if the Confirm setting Popup is displayed
						if(isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirm Parameter Popup"))
							jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnTurnOff",100,"clickable");						
						else if(isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality", "Confirm Functionality Popup"))
							jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnDeactivate",100,"clickable");			
						else if(isDisplayed("Configuration.PMSParameters.popUppostStayChargesFunction", "Confirm Parameter Popup"))
							jsClick("Configuration.PMSParameters.popUppostStayChargesFunction.btnApplyChange",100,"clickable");
						waitForSpinnerToDisappear(100);
					}
				}
				break;
			case "inactive":
			case "off":
				if(strFlag.equalsIgnoreCase("y")){
					System.out.println("***** Turning On the Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " *****");
					logger.log(LogStatus.PASS, "Turning On the Attribute "+ strAttribute+ " for the Function " +strFuncValue );
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]")));
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/preceding::button[1]")));
					System.out.println("***** Clicked on Turning On Button *****");
					logger.log(LogStatus.PASS, "Clicked on Turning On Button");
					waitForSpinnerToDisappear(100);

					if(strParamType.equalsIgnoreCase("functions")){
						//Verifying if the Confirm Setting Popup is displayed
						if(isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirm Parameter Popup"))
							jsClick("Configuration.PMSParameters.popUpConfigureSetting.btnTurnOn",100,"clickable");
						else if(isDisplayed("Configuration.PMSFunctions.popUpConfigureFunctionality", "Confirm Functionality Popup"))
							jsClick("Configuration.PMSFunctions.popUpConfigureFunctionality.btnActivate",100,"clickable");			
						else if(isDisplayed("Configuration.PMSParameters.popUppostStayChargesFunction", "Confirm Parameter Popup"))
							jsClick("Configuration.PMSParameters.popUppostStayChargesFunction.btnApplyChange",100,"clickable");
						waitForSpinnerToDisappear(100);
					}
				}
				else{
					System.out.println("***** Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned off *****");
					logger.log(LogStatus.PASS, "Attribute "+ strAttribute+ " for the Function " +strFuncValue+ " is already Turned off ");
				}				
				break;
			default:
				System.out.println("***** Invalid Status *****");
				logger.log(LogStatus.FAIL, "Invalid Status:: "+strAttrStatus);
			}

		}
		else{
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			switch(strAttribute){
			case "Zero Balance Open Folio Close Days":			

				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/following::a[1]")));
				//Constructing the XPath dynamically to set the value for this attribute				
				executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/following::a[1]")));							
				System.out.println("***** Clicked on Attribute's Edit Button *****");
				logger.log(LogStatus.PASS, "Clicked on Attribute's Edit Button");
				waitForSpinnerToDisappear(100);

				//Verifying if the Confirm Setting Popup is displayed
				isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

				//Set the data for the function attribute
				textBox("Configuration.PMSFunctions.txtZeroBalanceOpenFolioCloseDays", strFlag);

				//Click on Save button
				jsClick("Configuration.PMSFunctions.btnZeroBalanceOpenFolioCloseDaysSave",100,"clickable");
				waitForSpinnerToDisappear(100);
				break;

			case "Default Sub Event Rate Code":					

				//Constructing the XPath dynamically to set the value for this attribute
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/following::a[1]")));
				executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//h3[contains(.,'"+strGroup+"')]/following::*[text()='"+strParamType+"']/following::*[text()='"+strFuncValue+"']/following::*[text()='"+strAttribute+"']/following::a[1]")));
				System.out.println("***** Clicked on Attribute's Edit Button *****");
				logger.log(LogStatus.PASS, "Clicked on Attribute's Edit Button");				
				waitForSpinnerToDisappear(30);

				//Verifying if the Confirm Setting Popup is displayed
				isDisplayed("Configuration.PMSParameters.popUpConfigureSetting", "Confirmation Popup");

				//Set the data for the function attribute
				textBox("Configuration.PMSFunctions.txtDefaultSubEventRateCode", strFlag);

				//Click on Save button
				jsClick("Configuration.PMSFunctions.btnDefaultSubEventRateCodeSave",100,"clickable");
				waitForSpinnerToDisappear(100);
				break;				
			}			
		}

	}

	/**
	 * @author uaviti
	 * @description - This method is to Clear application Cache from Settings Menu 
	 * @Date: 11/30/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */
	public static void clearCache() throws Exception{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		try {

			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu",100,"clickable");
			//			click("Configuration.mainMenu",100,"clickable");
			jsClick("Configuration.mainMenu");
			System.out.println("***** Clicked on Main Menu *****");
			logger.log(LogStatus.PASS, "Clicked on Main Menu");

			// Navigating to Settings Menu
			mouseHover("Configuration.mainMenu.MenuSettings",100,"clickable");
			click("Configuration.mainMenu.MenuSettings",100,"clickable");

			//Validate if the Settings popup is displayed
			isDisplayed("Configuration.MenuSettings.popUpSettings", "Settings popup");

			//Click on  Clear Cache link
			WebElement element = driver.findElement(OR.getLocator("Configuration.MenuSettings.popUpSettings.lnkClearCache"));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			System.out.println("***** Clicked on Clear Cache *****");
			logger.log(LogStatus.PASS, "Clicked on Clear Cache");

			waitForSpinnerToDisappear(100);
			//Validate if the confirmation popup is displayed
			isDisplayed("Configuration.MenuSettings.ClearCache.popUpCofirmation", "Settings popup");

			//Click on ClearCache button in the popup
			click("Configuration.MenuSettings.ClearCache.popUpCofirmation.btnClearCache",100,"clickable");
			waitForSpinnerToDisappear(100);			

			/*//Click on Cancel button in the Settings popup
			List<WebElement> webElement = BaseClass.driver.findElements(OR.getLocator("Configuration.MenuSettings.ClearCache.popUpCofirmation.btnCancel"));
			System.out.println(" size :: "+ webElement.size());			
			executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", webElement.get(webElement.size()-1));

			//			jsClick("Configuration.MenuSettings.ClearCache.popUpCofirmation.btnCancel");
			System.out.println("***** Clicked on Cancel Button *****");
			logger.log(LogStatus.PASS, "Clicked on Cancel Button");
			waitForSpinnerToDisappear(100);*/

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Clear Cache  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Clear Cache :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}

	/**
	 * @author jasatis
	 * @description - This method is to create a RoomClass from Room Class Menu
	 * @Date: 12/10/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */
	public static void roomClass(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
		System.out.println("Property Config: " + propertyMap);
		String property = propertyMap.get("Property");
		System.out.println("prop: " + property);
		configMap.put("PROPERTY", property);
		System.out.println("Map: " + configMap);
		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Inventory menu
			WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Configuration.menu_Inventory");
			click(Utils.element("Configuration.menu_Inventory"));
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_AccommodationManagement", "clickable");
			mouseHover("Configuration.menu_AccommodationManagement");
			jsClick("Configuration.menu_AccommodationManagement");
			System.out.println("clicked Accommodation Management menu");
			logger.log(LogStatus.PASS, "Selected Accommodation Management Menu");

			// Navigating to Room Class  
			WebdriverWait(100, "Configuration.menu_RoomClass", "presence");
			jsClick("Configuration.menu_RoomClass");
			System.out.println("clicked Room Class menu");
			logger.log(LogStatus.PASS, "Selected Room Class Menu");
			Thread.sleep(5000);
			Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");
			Utils.jsClick("Configuration.btn_Search");
			System.out.println("Clicked on Search Button ");
			waitForSpinnerToDisappear(30);
			while(isExists("Configuration.lnk_loadmoreelements"))
			{
				System.out.println("ConfigPage.roomClass() inside the while loop");
				Utils.WebdriverWait(100, "Configuration.lnk_loadmoreelements", "clickable");
				Utils.jsClick("Configuration.lnk_loadmoreelements");
				waitForSpinnerToDisappear(30);
			}
			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.grd_SearchRoomClass_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("ROOM_CLASS")))
					{
						System.out.println("Room Class "+rows.get(i).getText()+" is already existing");
						flag = true;
						logger.log(LogStatus.PASS, "Room Class "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
						System.out.println("Room Class is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}
			if(!flag)
			{
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Select Template Tab
				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				Utils.jsClick("Configuration.tab_Template");
				System.out.println("clicked Template Tab");
				logger.log(LogStatus.PASS, "Selected Template tab");
				//Fetch Search Grid Column Data and validate
				waitForSpinnerToDisappear(30);			
				List<WebElement>  colData = Utils.elements("Configuration.grd_TemplateClassCodeSearch_ColData"); 
				System.out.println("No of rows are : " + colData.size());
				flag = false;
				if(colData.size() > 0)
				{
					for(int i=0;i<colData.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", colData.get(i));
						if(colData.get(i).getText().equalsIgnoreCase(configMap.get("ROOM_CLASS")))
						{
							System.out.println("Room Class "+colData.get(i).getText()+" Template is already existing");
							flag = true;
							logger.log(LogStatus.PASS, "Room Class "+colData.get(i).getText()+" Template is already existing");
							break;
						}
						else
						{
							System.out.println("Room Class Template is not present in the system:: "+colData.get(i).getText());						
						}
					}
				}
				if(!flag){
					waitForPageLoad(100);
					Thread.sleep(3000);
					//Select New
					Utils.WebdriverWait(100, "Configuration.link_New", "clickable");
					Utils.jsClick("Configuration.link_New");
					System.out.println("clicked new");
					logger.log(LogStatus.PASS, "Clicked on New");
					waitForSpinnerToDisappear(30);
					//Provide Room Class
					Utils.WebdriverWait(100, "Configuration.txt_CreateRoomClass_RoomClass", "presence");
					Utils.jsTextbox("Configuration.txt_CreateRoomClass_RoomClass",configMap.get("ROOM_CLASS"));   // Room Class
					System.out.println("Provided Room Class");
					logger.log(LogStatus.PASS, "Provided Room Class");

					waitForPageLoad(100);
					//Provided Description
					Utils.WebdriverWait(100, "Configuration.txt_CreateRoomClass_Desc", "presence");
					Utils.jsTextbox("Configuration.txt_CreateRoomClass_Desc",configMap.get("SHORT_DESCRIPTION"));   // Description
					System.out.println("Provide Description for Room Class");
					logger.log(LogStatus.PASS, "Provided Description for Room Class");			

					waitForPageLoad(100);
					//Provided Sequence
					Utils.WebdriverWait(100, "Configuration.txt_Sequence", "presence");
					Utils.jsTextbox("Configuration.txt_Sequence",configMap.get("Sequence"));   // Sequence
					System.out.println("Provide Sequence for Room Class");
					logger.log(LogStatus.PASS, "Provided Sequence for Room Class");

					waitForPageLoad(100);
					//Select Save
					Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
					Utils.jsClick("Configuration.btn_Save");
					System.out.println("clicked Save");
					logger.log(LogStatus.PASS, "Selected Save");
					waitForSpinnerToDisappear(30);
				}
				//Page Validation
				waitForPageLoad(100);
				//Provide Room Class
				Utils.WebdriverWait(100, "Configuration.txt_CreateRoomClass_RoomClass", "presence");
				Utils.jsTextbox("Configuration.txt_CreateRoomClass_RoomClass",configMap.get("ROOM_CLASS"));   
				System.out.println("Provided Room Class");
				logger.log(LogStatus.PASS, "Provided Room Class");

				//Select Search
				Utils.WebdriverWait(100, "Configuration.btn_Search", "presence");
				Utils.jsClick("Configuration.btn_Search");
				System.out.println("clicked Search");
				logger.log(LogStatus.PASS, "Selected Search");

				waitForSpinnerToDisappear(30);
				//Select More Menu 
				Utils.WebdriverWait(100, "Configuration.menu_More", "clickable");
				Utils.jsClick("Configuration.menu_More");
				System.out.println("clicked More menu ");
				logger.log(LogStatus.PASS, "Selected More Menu");

				//Select Copy link 
				Utils.WebdriverWait(100, "Configuration.link_Copy", "clickable");
				Utils.jsClick("Configuration.link_Copy");
				System.out.println("clicked Copy link ");
				logger.log(LogStatus.PASS, "Selected Copy Link");

				//Copy Page Validation
				Thread.sleep(3000);
				//Provide Room Class
				Utils.WebdriverWait(100, "Configuration.txt_CreateRoomClass_RoomClass", "presence");
				Utils.jsTextbox("Configuration.txt_CreateRoomClass_RoomClass",configMap.get("ROOM_CLASS"));   
				System.out.println("Provide Room Class to Copy");
				logger.log(LogStatus.PASS, "Provided Room Class for Copy");

				Utils.WebdriverWait(100, "Configuration.txt_copyproperty", "presence");
				Utils.jsTextbox("Configuration.txt_copyproperty",configMap.get("PROPERTY"));   
				System.out.println("Set the Property");
				logger.log(LogStatus.PASS, "Provided Room Class for Copy");

				//Select Save
				Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
				Utils.jsClick("Configuration.btn_Save");
				System.out.println("clicked Save to Copy");
				logger.log(LogStatus.PASS, "Selected Save to Copy");
				waitForSpinnerToDisappear(30);
				//Select Copy and Continue
				Utils.WebdriverWait(100, "Configuration.btn_CopyAndContinue", "clickable");
				Utils.jsClick("Configuration.btn_CopyAndContinue");
				System.out.println("clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Selected Copy and Continue button");

				Utils.WebdriverWait(100, "Configuration.linkBackToRoomClasses", "clickable");
				Utils.jsClick("Configuration.linkBackToRoomClasses");
				System.out.println("Back to Room Classes");
				logger.log(LogStatus.PASS, "Selected Back to Room Classes");

				Utils.WebdriverWait(100, "Configuration.link_property", "clickable");
				Utils.jsClick("Configuration.link_property");
				System.out.println("Click on Property link");
				logger.log(LogStatus.PASS, "Selected the Property Link");
				waitForSpinnerToDisappear(30);
				List<WebElement>  copiedclassinrows = Utils.elements("Configuration.grd_SearchRoomClass_ColData"); 
				System.out.println("No of Rows in Room Class for a property are : " + copiedclassinrows.size());
				boolean copiedFlag = false;
				if(copiedclassinrows.size() > 0)
				{
					for(int i=0;i<copiedclassinrows.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", copiedclassinrows.get(i));
						if(copiedclassinrows.get(i).getText().equalsIgnoreCase(configMap.get("ROOM_CLASS")))
						{
							System.out.println("Room Class "+copiedclassinrows.get(i).getText()+" is copied successfully");
							copiedFlag = true;
							logger.log(LogStatus.PASS, "Room Class "+copiedclassinrows.get(i).getText()+" is copied successfully");
							break;
						}
						else
						{
							System.out.println("Room Class "+copiedclassinrows.get(i).getText()+" is not copied successfully");
						}
					}
				}
				if(copiedFlag)
				{
					logger.log(LogStatus.PASS, "Room Class is created and copied successfully");
				}
				else
				{
					logger.log(LogStatus.FAIL, "Issue While Copying the Room Class to Property");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Room Class not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}
	/**
	 * @author jasatis
	 * @description - This method is to create a Room Types from Room Type Menu
	 * @Date: 12/10/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */
	public static void roomTypes(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
		System.out.println("Property Config: " + propertyMap);
		String property = propertyMap.get("Property");
		System.out.println("prop: " + property);
		configMap.put("PROPERTY", property);
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Inventory menu
			WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Configuration.menu_Inventory");
			click(Utils.element("Configuration.menu_Inventory"));
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_AccommodationManagement", "clickable");
			mouseHover("Configuration.menu_AccommodationManagement");
			jsClick("Configuration.menu_AccommodationManagement");
			System.out.println("clicked Accommodation Management menu");
			logger.log(LogStatus.PASS, "Selected Accommodation Management Menu");

			// Navigating to Room Types  
			WebdriverWait(100, "Configuration.menu_RoomTypes", "presence");
			jsClick("Configuration.menu_RoomTypes");
			System.out.println("clicked Room Types menu");
			logger.log(LogStatus.PASS, "Selected Room Types Menu");
			Thread.sleep(5000);
			Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");
			Utils.jsClick("Configuration.btn_Search");
			System.out.println("Clicked on Search Button ");
			waitForSpinnerToDisappear(30);
			while(isExists("Configuration.lnk_loadmoreelements"))
			{
				System.out.println("in while");
				System.out.println("ConfigPage.roomTypes() inside the while loop");
				Utils.WebdriverWait(100, "Configuration.lnk_loadmoreelements", "clickable");
				Utils.jsClick("Configuration.lnk_loadmoreelements");
				waitForSpinnerToDisappear(30);
			}
			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.grd_SearchRoomTypes_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("LABEL")))
					{
						System.out.println("Room Type "+rows.get(i).getText()+" is already existing");
						flag = true;
						logger.log(LogStatus.PASS, "Room Type "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
						System.out.println("Room Type is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}
			if(!flag)
			{
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Select Template Tab
				Utils.WebdriverWait(100, "Configuration.tab_Template", "clickable");
				Utils.jsClick("Configuration.tab_Template");
				System.out.println("clicked on Template Tab");
				logger.log(LogStatus.PASS, "Selected Template tab");
				//Fetch Search Grid Column Data and validate
				waitForSpinnerToDisappear(30);				
				List<WebElement>  colData = Utils.elements("Configuration.grd_TemplateClassTypeSearch_ColData"); 
				System.out.println("No of rows are : " + colData.size());
				flag = false;
				if(colData.size() > 0)
				{
					for(int i=0;i<colData.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", colData.get(i));
						if(colData.get(i).getText().equalsIgnoreCase(configMap.get("LABEL")))
						{
							System.out.println("Room Type "+colData.get(i).getText()+" Template is already existing");
							flag = true;
							logger.log(LogStatus.PASS, "Room Type "+colData.get(i).getText()+" Template is already existing");
							break;
						}
						else
						{
							System.out.println("Room Type Template is not present in the system:: "+colData.get(i).getText());						
						}
					}
				}
				if(!flag){
					waitForPageLoad(100);
					Thread.sleep(3000);
					//Select New
					Utils.WebdriverWait(100, "Configuration.link_New", "clickable");
					Utils.jsClick("Configuration.link_New");
					System.out.println("clicked new");
					logger.log(LogStatus.PASS, "Clicked on New");

					waitForSpinnerToDisappear(30);
					//Provide Room Type
					Utils.WebdriverWait(100, "Configuration.txt_CreateRoomTypes_RoomType", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateRoomTypes_RoomType",configMap.get("LABEL"));
					System.out.println("Provided Room Type");
					logger.log(LogStatus.PASS, "Provided Room Type");

					waitForPageLoad(100);
					Thread.sleep(3000);
					//Provide Room Class
					Utils.WebdriverWait(100, "Configuration.txt_CreateRoomTypes_RoomClass", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateRoomTypes_RoomClass",configMap.get("ROOM_CLASS"));
					System.out.println("Provided Room Class");
					logger.log(LogStatus.PASS, "Provided Room Class");

					waitForPageLoad(100);
					//Provided Description
					Utils.WebdriverWait(100, "Configuration.txt_CreateRoomTypes_Desc", "clickable");
					Utils.jsTextbox("Configuration.txt_CreateRoomTypes_Desc",configMap.get("SHORT_DESCRIPTION"));
					System.out.println("Provide Description for Room Type");
					logger.log(LogStatus.PASS, "Provided Description for Room Type");			
					Utils.Wait(3000);
					if(!element("Configuration.Roomtype.chkboxMeeting").isSelected())
						jsClick("Configuration.Roomtype.chkboxMeeting", 100, "presence");
					Utils.Wait(3000);
					if(!element("Configuration.Roomtype.chkboxHousekeeping").isSelected())
						jsClick("Configuration.Roomtype.chkboxHousekeeping", 100, "presence");
					Utils.Wait(3000);
					if(!element("Configuration.Roomtype.chkboxSendtointerface").isSelected())
						jsClick("Configuration.Roomtype.chkboxSendtointerface", 100, "presence");
					Utils.Wait(3000);
					if(!element("Configuration.Roomtype.chkboxMaintenance").isSelected())
						jsClick("Configuration.Roomtype.chkboxMaintenance", 100, "presence");
					Utils.Wait(3000);
					waitForPageLoad(100);
					//Select Save
					Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
					Utils.doubleClick("Configuration.btn_Save");
					System.out.println("clicked Save");
					logger.log(LogStatus.PASS, "Selected Save");
					waitForSpinnerToDisappear(30);
				}
				//Page Validation
				waitForPageLoad(100);
				//Provide Room Class
				Utils.WebdriverWait(100, "Configuration.txt_SearchRoomTypes_RoomClass", "presence");
				Utils.jsTextbox("Configuration.txt_SearchRoomTypes_RoomClass",configMap.get("ROOM_CLASS"));
				System.out.println("Provided Room Type");
				logger.log(LogStatus.PASS, "Provided Room Type");

				//Select Search
				Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");
				Utils.jsClick("Configuration.btn_Search");
				System.out.println("clicked Search");
				logger.log(LogStatus.PASS, "Selected Search");

				waitForPageLoad(100);
				//Select More Menu 
				Utils.WebdriverWait(100, "Configuration.menu_More", "clickable");
				Utils.jsClick("Configuration.menu_More");
				System.out.println("clicked More menu ");
				logger.log(LogStatus.PASS, "Selected More Menu");

				//Select Copy link 
				Utils.WebdriverWait(100, "Configuration.link_Copy", "clickable");
				Utils.jsClick("Configuration.link_Copy");
				System.out.println("clicked Copy link ");
				logger.log(LogStatus.PASS, "Selected Copy Link");

				//Copy Page Validation
				waitForSpinnerToDisappear(30);
				//Provide Room Type
				Utils.WebdriverWait(100, "Configuration.txt_CreateRoomTypes_RoomType", "presence");
				Utils.jsTextbox("Configuration.txt_CreateRoomTypes_RoomType",configMap.get("LABEL"));
				System.out.println("Provide Room Type to Copy");
				logger.log(LogStatus.PASS, "Provided Room Type for Copy");

				//Select Save
				Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
				Utils.jsClick("Configuration.btn_Save");
				System.out.println("clicked Save to Copy");
				logger.log(LogStatus.PASS, "Selected Save to Copy");

				waitForSpinnerToDisappear(30);
				//Select Copy and Continue
				Utils.WebdriverWait(100, "Configuration.btn_CopyAndContinue", "clickable");
				Utils.jsClick("Configuration.btn_CopyAndContinue");
				System.out.println("clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Selected Copy and Continue button");
				waitForSpinnerToDisappear(30);
				Utils.WebdriverWait(100, "Configuration.linkBackToRoomTypes", "clickable");
				Utils.jsClick("Configuration.linkBackToRoomTypes");
				System.out.println("Back to Room Types");
				logger.log(LogStatus.PASS, "Selected Back to Room Types");

				Utils.WebdriverWait(100, "Configuration.link_property", "clickable");
				Utils.jsClick("Configuration.link_property");
				System.out.println("Click on Property link");
				logger.log(LogStatus.PASS, "Selected the property link");
				waitForSpinnerToDisappear(30);

				List<WebElement>  copiedtypeinrows= Utils.elements("Configuration.grd_SearchRoomTypes_ColData"); 
				System.out.println("No of Rows in Room Types for a property are : " + copiedtypeinrows.size());
				boolean copiedFlag = false;
				if(copiedtypeinrows.size() > 0)
				{
					for(int i=0;i<copiedtypeinrows.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", copiedtypeinrows.get(i));
						if(copiedtypeinrows.get(i).getText().equalsIgnoreCase(configMap.get("LABEL")))
						{
							System.out.println("Room Type "+copiedtypeinrows.get(i).getText()+" is copied successfully");
							copiedFlag = true;
							logger.log(LogStatus.PASS, "Room Type "+copiedtypeinrows.get(i).getText()+" is copied successfully");
							break;
						}
						else
						{
							System.out.println("Room Type "+copiedtypeinrows.get(i).getText()+" is not copied successfully");
						}
					}
				}
				if(copiedFlag)
				{
					logger.log(LogStatus.PASS, "Room Type is created and copied successfully");
				}
				else
				{
					logger.log(LogStatus.FAIL, "Issue Observed while copying the Room Type to Property");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Room Type not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}
	/**
	 * @author jasatis
	 * @description - This method is to create a Room from Rooms Menu
	 * @Date: 12/10/2018 
	 * @Revision History - Initial version
	 * @throws Exception
	 */
	public static void createRoom(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
		System.out.println("Property Config: " + propertyMap);
		String property = propertyMap.get("Property");
		System.out.println("prop: " + property);
		configMap.put("PROPERTY", property);
		System.out.println("Map: " + configMap);

		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Inventory menu
			WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Configuration.menu_Inventory");
			click(Utils.element("Configuration.menu_Inventory"));
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_AccommodationManagement", "clickable");
			mouseHover("Configuration.menu_AccommodationManagement");
			jsClick("Configuration.menu_AccommodationManagement");
			System.out.println("clicked Accommodation Management menu");
			logger.log(LogStatus.PASS, "Selected Accommodation Management Menu");

			// Navigating to Room Class  
			WebdriverWait(100, "Configuration.header_Rooms", "presence");
			jsClick("Configuration.header_Rooms");
			System.out.println("clicked Rooms");
			logger.log(LogStatus.PASS, "Selected Rooms  Menu");
			waitForSpinnerToDisappear(30);
			Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");
			Utils.jsClick("Configuration.btn_Search");
			System.out.println("Clicked on Search Button ");
			waitForSpinnerToDisappear(30);
			while(isExists("Configuration.lnk_loadmoreelements"))
			{
				System.out.println("ConfigPage.rooms() inside the while loop");
				Utils.WebdriverWait(100, "Configuration.lnk_loadmoreelements", "clickable");
				Utils.jsClick("Configuration.lnk_loadmoreelements");
				waitForSpinnerToDisappear(30);
			}
			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.grd_SearchRooms_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("ROOM")))
					{
						System.out.println("Room"+rows.get(i).getText()+" is already existing");
						flag = true;
						logger.log(LogStatus.PASS, "Room "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{

						System.out.println("Room  is not present in the system:: "+rows.get(i).getText());						
					}
				}
			}
			if(!flag){
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Select New
				Utils.WebdriverWait(100, "Configuration.link_New", "clickable");
				Utils.jsClick("Configuration.link_New");
				System.out.println("clicked new");
				logger.log(LogStatus.PASS, "Clicked on New");

				//waitForPageLoad(100);
				//Utils.Wait(5000);
				waitForSpinnerToDisappear(30);
				//Provide property
				Utils.WebdriverWait(100, "Configuration.txt_Rooms_Property", "clickable");
				Utils.jsTextbox("Configuration.txt_Rooms_Property",configMap.get("PROPERTY"));
				System.out.println("Provided the Property ");
				logger.log(LogStatus.PASS, "Provided property");
				waitForPageLoad(100);
				Thread.sleep(3000);
				//Provide Room type 
				Utils.WebdriverWait(100, "Configuration.txt_Rooms_RoomType", "clickable");
				Utils.jsTextbox("Configuration.txt_Rooms_RoomType",configMap.get("ROOM_CATEGORY"));
				System.out.println("Provided the Room Type ");
				logger.log(LogStatus.PASS, "Provided Room Type");

				waitForPageLoad(100);
				//Provided Description
				Utils.WebdriverWait(100, "Configuration.txt_Rooms_Room", "clickable");
				Utils.jsTextbox("Configuration.txt_Rooms_Room",configMap.get("ROOM"));
				System.out.println("Provide Room number for Room Type");
				logger.log(LogStatus.PASS, "Provided Room number for Room Type");			

				waitForPageLoad(100);
				//Provided Description
				Utils.WebdriverWait(100, "Configuration.txt_Rooms_Desc", "clickable");
				Utils.jsTextbox("Configuration.txt_Rooms_Desc",configMap.get("DESCRIPTION"));
				System.out.println("Provide Room number description");
				logger.log(LogStatus.PASS, "Provided Room number Description");

				waitForPageLoad(100);
				//Select Save
				Utils.WebdriverWait(100, "Configuration.btn_Save", "clickable");
				Utils.doubleClick("Configuration.btn_Save");
				System.out.println("clicked Save");
				logger.log(LogStatus.PASS, "Selected Save");
				waitForSpinnerToDisappear(30);
				//Provide Room number
				Utils.WebdriverWait(100, "Configuration.txt_Rooms_Room", "clickable");
				Utils.jsTextbox("Configuration.txt_Rooms_Room",configMap.get("ROOM"));
				System.out.println("Provide Room number for Room Type");
				logger.log(LogStatus.PASS, "Provided Room number for Room Type");
				Utils.Wait(5000);
				//Select Search
				Utils.WebdriverWait(100, "Configuration.btn_Search", "clickable");
				Utils.doubleClick("Configuration.btn_Search");
				System.out.println("clicked Search");
				logger.log(LogStatus.PASS, "Selected Search");
				waitForSpinnerToDisappear(30);
				List<WebElement>  noofrows = Utils.elements("Configuration.grd_SearchRooms_ColData"); 
				System.out.println("No of rows are : " + noofrows.size());
				boolean isRoomCreated = false;
				if(noofrows.size() > 0)
				{
					for(int i=0;i<noofrows.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", noofrows.get(i));
						if(noofrows.get(i).getText().equalsIgnoreCase(configMap.get("ROOM")))
						{
							System.out.println("Room "+noofrows.get(i).getText()+" is Created Successfully");
							isRoomCreated = true;
							logger.log(LogStatus.PASS, "Room "+noofrows.get(i).getText()+" is available");
							break;
						}
						else
						{
							System.out.println("Room  is not present in the system:: "+noofrows.get(i).getText());						
						}
					}
				}
				if(isRoomCreated)
				{
					logger.log(LogStatus.PASS, "Room is Created Successfully");
				}
				else
				{
					logger.log(LogStatus.FAIL, "issue observed while creating room");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Room not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in Room Creation due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}

	/*******************************************************************
	-  Description: Verify user is able to create a Rate Category
	- Input: RATE_CATEGORY, DESCRIPTION, RATE_CLASS, BEGIN_DATE, END_DATE
	- Output: Rate Category should be created
	- Author: @author vnadipal
	- Date: 11/29/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void createRateCategory(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			jsClick("Configuration.mainMenu", 100, "clickable");

			// Clicking on Administration Button
			jsClick("Configuration.AdminstrationMenu", 100, "clickable");

			// Clicked on Financial Menu
			jsClick("Configuration.RateCategory.menuFinancial", 100,"presence");

			// Clicked on Rate Management Menu
			jsClick("Configuration.RateCategory.menuRateManagement", 100,"presence");

			// Clicked on Rate Categories Menu Item
			jsClick("Configuration.RateCategory.menuItemRateCategories", 100,"presence");

			//Enter Rate Class
			jsTextbox("Configuration.RateCategory.txtRateCategory", configMap.get("RATE_CATEGORY"));

			// Clicked on Search button
			jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

			if(isExists("Configuration.RateClass.popupLinkCancel")) {
				jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
				element("Configuration.RateCategory.txtRateCategoryWithValue").clear();
			}

			List<WebElement>  rows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("RATE_CATEGORY"))) {
						System.out.println("Rate Category "+rows.get(i).getText()+" is already existing in Property");
						flag = true;
						logger.log(LogStatus.PASS, "Rate Category "+rows.get(i).getText()+" is already existing in Property");
						break;
					}
					else {
						logger.log(LogStatus.PASS, "Rate Category "+rows.get(i).getText()+" is not existing in Property");
						System.out.println("Rate Category is not present in Property:: "+rows.get(i).getText());						
					}
				}
			}

			if(!flag) {
				// Clicked on Template Tab
				jsClick("Configuration.RateCategory.tabTemplate", 100,"presence");

				//Enter Rate Class
				jsTextbox("Configuration.RateCategory.lovCode", configMap.get("RATE_CATEGORY"));

				// Clicked on Search button
				jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

				if(isExists("Configuration.RateClass.popupLinkCancel")) {
					jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
					element("Configuration.RateCategory.lovCodeWithValue").clear();
				}
				List<WebElement>  trows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
				System.out.println("No of rows are : " + trows.size());
				boolean tflag = false;
				if(trows.size() > 0) {
					for(int i=0;i<trows.size();i++) {
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trows.get(i));
						if(trows.get(i).getText().equalsIgnoreCase(configMap.get("RATE_CATEGORY"))) {
							System.out.println("Rate Category "+trows.get(i).getText()+" is already existing in Template");
							tflag = true;
							logger.log(LogStatus.PASS, "Rate Category "+trows.get(i).getText()+" is already existing in Template");
							break;
						}
						else {
							logger.log(LogStatus.PASS, "Rate Category "+trows.get(i).getText()+" is not existing in Template");
							System.out.println("Rate Category is not present in Template:: "+trows.get(i).getText());						
						}
					}
				}

				if(!tflag) {
					waitForPageLoad(1000);
					Thread.sleep(3000);
					// Clicked on New button
					jsClick("Configuration.RateCategory.linkNew", 100,"presence");

					Thread.sleep(5000);
					//Enter Rate Class
					jsTextbox("Configuration.RateClass.lovRateClass", configMap.get("RATE_CLASS"));

					//Enter Rate Category
					jsTextbox("Configuration.RateCategory.lovCode", configMap.get("RATE_CATEGORY"));

					//Enter Description
					jsTextbox("Configuration.RateCategory.txtDescription", configMap.get("DESCRIPTION"));

					//Enter Begin Date
					jsTextbox("Configuration.RateCategory.txtBeginDate", configMap.get("BEGIN_DATE"));

					//Enter End Date
					jsTextbox("Configuration.RateCategory.txtEndDate", configMap.get("END_DATE"));

					// Clicked on Save button
					jsClick("Configuration.RateCategory.btnSave", 100,"presence");

					Thread.sleep(5000);
					//Enter Rate Class
					jsTextbox("Configuration.RateCategory.lovCode", configMap.get("RATE_CATEGORY"));

					// Clicked on Search button
					jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

					Thread.sleep(8000);
					if(getText("Configuration.RateCategory.validationTemplateDiv").contains(configMap.get("RATE_CATEGORY"))) {
						System.out.println("Rate Category " + configMap.get("RATE_CATEGORY") + " is successfully created in Template");
						logger.log(LogStatus.PASS,"Rate Category " + configMap.get("RATE_CATEGORY") + " is successfully created in Template");
					} 
					else {
						System.out.println("Rate Category " + configMap.get("RATE_CATEGORY") + " is not created in Template");
						logger.log(LogStatus.FAIL,"Rate Category " + configMap.get("RATE_CATEGORY") + " is not created in Template");
					}
				}
				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Link More
				jsClick("Configuration.RateCategory.linkLinkMore", 100,"presence");

				waitForPageLoad(100);
				// Clicked on Copy
				jsClick("Configuration.RateCategory.linkCopy", 100,"presence");

				Thread.sleep(3000);
				//Enter Rate Category
				jsTextbox("Configuration.RateCategory.txtRateCategory", configMap.get("RATE_CATEGORY"));

				//Enter Target Properties
				jsTextbox("Configuration.RateCategory.lovTargetProperties", configMap.get("PROPERTY"));

				// Clicked on Save button
				jsClick("Configuration.RateCategory.btnSave", 100,"presence");

				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Copy And Continue button
				jsClick("Configuration.RateCategory.btnCopyAndContinue", 100,"presence");

				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Back To Rate Categories link
				jsClick("Configuration.RateCategory.linkBackToRateCategories", 100,"presence");

				// Clicked on Property Tab
				jsClick("Configuration.RateCategory.tabProperty", 100,"presence");

				Thread.sleep(3000);
				//Enter Rate Class
				jsTextbox("Configuration.RateCategory.txtRateCategory", configMap.get("RATE_CATEGORY"));

				// Clicked on Search button
				jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

				Thread.sleep(8000);
				if(getText("Configuration.RateCategory.validationTemplateDiv").contains(configMap.get("RATE_CATEGORY"))) {
					System.out.println("Rate Category " + configMap.get("RATE_CATEGORY") + " is successfully copied to Property");
					logger.log(LogStatus.PASS,"Rate Category " + configMap.get("RATE_CATEGORY") + " is successfully copied to Property");
				} 
				else {
					System.out.println("Rate Category " + configMap.get("RATE_CATEGORY") + " is not copied to Property");
					logger.log(LogStatus.FAIL,"Rate Category " + configMap.get("RATE_CATEGORY") + " is not copied to Property");
				}
			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: Verify user is able to create a Rate Class
	- Input: RATE_CLASS, DESCRIPTION
	- Output: Rate Class should be created
	- Author: @author vnadipal
	- Date: 11/30/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void createRateClass(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			jsClick("Configuration.mainMenu", 100, "clickable");

			// Clicking on Administration Button
			jsClick("Configuration.AdminstrationMenu", 100, "clickable");

			// Clicked on Financial Menu
			jsClick("Configuration.RateCategory.menuFinancial", 100, "presence");

			// Clicked on Rate Management Menu
			jsClick("Configuration.RateCategory.menuRateManagement", 100, "presence");

			// Clicked on Rate Classes Menu Item
			jsClick("Configuration.RateClass.menuItemRateClasses", 100, "presence");

			//Enter Rate Class
			jsTextbox("Configuration.RateClass.lovRateClass", configMap.get("RATE_CLASS"));

			// Clicked on Search button
			jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

			if(isExists("Configuration.RateClass.popupLinkCancel")) {
				jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
				element("Configuration.RateClass.lovRateClassWithText").clear();
			}

			List<WebElement>  rows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("RATE_CLASS"))) {
						System.out.println("Rate Class "+rows.get(i).getText()+" is already existing in Property");
						flag = true;
						logger.log(LogStatus.PASS, "Rate Class "+rows.get(i).getText()+" is already existing in Property");
						break;
					}
					else {
						logger.log(LogStatus.PASS, "Rate Class "+rows.get(i).getText()+" is not existing in Property");
						System.out.println("Rate Class is not present in Property:: "+rows.get(i).getText());						
					}
				}
			}

			if(!flag) {
				// Clicked on Template Tab
				jsClick("Configuration.RateCategory.tabTemplate", 100,"presence");

				//Enter Rate Class
				jsTextbox("Configuration.RateCategory.lovCode", configMap.get("RATE_CLASS"));

				// Clicked on Search button
				jsClick("Configuration.RateCategory.btnSearch", 100,"presence");

				Thread.sleep(3000);
				if(isExists("Configuration.RateClass.popupLinkCancel")) {
					jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
					element("Configuration.RateClass.lovRateClassCodeWithText").clear();
				}

				List<WebElement>  trows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
				System.out.println("No of rows are : " + trows.size());
				boolean tflag = false;
				if(trows.size() > 0) {
					for(int i=0;i<trows.size();i++) {
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trows.get(i));
						if(trows.get(i).getText().equalsIgnoreCase(configMap.get("RATE_CLASS"))) {
							System.out.println("Rate Class "+trows.get(i).getText()+" is already existing in Template");
							tflag = true;
							logger.log(LogStatus.PASS, "Rate Class "+trows.get(i).getText()+" is already existing in Template");
							break;
						}
						else {
							logger.log(LogStatus.PASS, "Rate Class "+trows.get(i).getText()+" is not existing in Template");
							System.out.println("Rate Class is not present in Template:: "+trows.get(i).getText());						
						}
					}
				}

				if(!tflag) {
					waitForPageLoad(1000);
					Thread.sleep(3000);
					// Clicked on New button
					jsClick("Configuration.RateCategory.linkNew", 1000, "clickable");

					//Enter Rate Class
					WebdriverWait(200, "Configuration.RateCategory.txtDescription", "visible");
					Thread.sleep(5000);
					textBox("Configuration.RateCategory.lovCode", configMap.get("RATE_CLASS"), 1000, "presence");

					//Enter Description
					textBox("Configuration.RateCategory.txtDescription", configMap.get("DESCRIPTION"), 100, "presence");

					// Clicked on Save button
					jsClick("Configuration.RateCategory.btnSave", 100,"presence");

					Thread.sleep(7000);
					//Enter Rate Class
					textBox("Configuration.RateCategory.lovCode", configMap.get("RATE_CLASS"), 100, "presence");

					// Clicked on Search button
					jsClick("Configuration.RateCategory.btnSearch", 100,"presence");

					Thread.sleep(8000);
					System.out.println("text:" + getText("Configuration.RateCategory.validationTemplateDiv"));
					if(getText("Configuration.RateCategory.validationTemplateDiv").contains(configMap.get("RATE_CLASS"))) {
						System.out.println("Rate Class " + configMap.get("RATE_CLASS") + "is successfully created in Template");
						logger.log(LogStatus.PASS,"Rate Class " + configMap.get("RATE_CLASS") + "is successfully created in Template");
					} 
					else {
						System.out.println("Rate Class " + configMap.get("RATE_CLASS") + "is not created in Template");
						logger.log(LogStatus.FAIL,"Rate Class " + configMap.get("RATE_CLASS") + "is not created in Template");
					}
				}

				Thread.sleep(5000);
				waitForPageLoad(100);
				// Clicked on Link More
				jsClick("Configuration.RateCategory.linkLinkMore", 100, "presence");

				waitForPageLoad(100);
				// Clicked on Copy
				jsClick("Configuration.RateCategory.linkCopy", 100, "presence");

				Thread.sleep(3000);
				waitForPageLoad(100);
				//Enter Rate Class
				jsTextbox("Configuration.RateClass.lovRateClass", configMap.get("RATE_CLASS"));

				//Enter Target Properties
				jsTextbox("Configuration.RateCategory.lovTargetProperties", configMap.get("PROPERTY"));

				// Clicked on Save button
				jsClick("Configuration.RateCategory.btnSave", 100, "presence");

				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Copy And Continue button
				jsClick("Configuration.RateCategory.btnCopyAndContinue", 100, "presence");

				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Back To Rate Categories link
				jsClick("Configuration.RateClass.linkBackToRateClasses", 100, "presence");

				Thread.sleep(3000);
				// Clicked on Property Tab
				jsClick("Configuration.RateCategory.tabProperty", 100, "presence");

				//Enter Rate Class
				jsTextbox("Configuration.RateClass.lovRateClass", configMap.get("RATE_CLASS"));

				// Clicked on Search button
				jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

				Thread.sleep(8000);
				System.out.println("Prop text: " + getText("Configuration.RateCategory.validationTemplateDiv"));
				if(getText("Configuration.RateCategory.validationTemplateDiv").contains(configMap.get("RATE_CLASS"))) {
					System.out.println("Rate Class " + configMap.get("RATE_CLASS") + "is successfully copied to Property");
					logger.log(LogStatus.PASS,"Rate Class " + configMap.get("RATE_CLASS") + "is successfully copied to Property");
				} 
				else {
					System.out.println("Rate Class " + configMap.get("RATE_CLASS") + "is not copied to Property");
					logger.log(LogStatus.FAIL,"Rate Class " + configMap.get("RATE_CLASS") + "is not copied to Property");
				}
			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: Verify user is able to create a Rate Code
	- Input: RATE_CODE, DESCRIPTION, RATE_CATEGORY, BEGIN_DATE, END_DATE, ROOM_TYPES, TRANSACTION_CODE, MARKET_CODE, DISPLAY_SET, AMOUNT
	- Output: Rate Code should be created
	- Author: @author vnadipal
	- Date: 12/03/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	/**
	 * @param configMap
	 * @throws Exception
	 */
	//	public static void createRateCode(HashMap<String, String> configMap) throws Exception {
	//
	//		String ScriptName = Utils.getMethodName();
	//		System.out.println("name: " + ScriptName);
	//		System.out.println("Map: " + configMap);
	//
	//		try {
	//			// Navigating to Main Menu
	//			jsClick("Configuration.mainMenu", 100, "presence");
	//
	//			// Navigating to Administration
	//			jsClick("Configuration.AdminstrationMenu", 100, "presence");
	//			waitForSpinnerToDisappear(10);
	//
	//			// Clicked on Financial Menu
	//			jsClick("Configuration.RateCategory.menuFinancial", 100, "presence");
	//
	//			// Clicked on Rate Management Menu
	//			jsClick("Configuration.RateCategory.menuRateManagement", 100, "presence");
	//
	//			// Clicked on Rate Codes Menu Item
	//			jsClick("Configuration.RateCode.menuItemRateCodes", 100, "presence");
	//
	//			//Enter Rate Code
	//			jsTextbox("Configuration.RateCode.txtRateCode", configMap.get("RATE_CODE"));
	//
	//			// Clicked on Search button
	//			jsClick("Configuration.RateCategory.btnSearch", 100, "presence");
	//
	//			Thread.sleep(3000);
	//			if(isExists("Configuration.RateClass.popupLinkCancel")) {
	//				jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
	//				element("Configuration.RateCode.txtRateCodeWithText").clear();
	//			}
	//
	//			List<WebElement>  rows = Utils.elements("Configuration.RateCode.searchRates"); 
	//			System.out.println("No of rows are : " + rows.size());
	//			boolean flag = false;
	//			if(rows.size() > 0) {
	//				for(int i=0;i<rows.size();i++) {
	//					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
	//					System.out.println("" + rows.get(i).getText());
	//					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("RATE_CODE"))) {
	//						System.out.println("Rate Code "+rows.get(i).getText()+" is already existing in Property");
	//						flag = true;
	//						logger.log(LogStatus.PASS, "Rate Code "+rows.get(i).getText()+" is already existing in Property");
	//						break;
	//					}
	//					else {
	//						System.out.println("Rate Code is not present in Property:: "+rows.get(i).getText());						
	//					}
	//				}
	//			}
	//
	//			if(!flag) {
	//				// Clicked on New button
	//				jsClick("Configuration.RateCategory.linkNew", 100, "presence");
	//
	//				Thread.sleep(3000);
	//				//Enter Rate Code
	//				jsTextbox("Configuration.RateCode.txtRateCode", configMap.get("RATE_CODE"));
	//				logger.log(LogStatus.INFO, "Entered rate code: " + configMap.get("RATE_CODE"));
	//
	//				Thread.sleep(3000);
	//				//Enter Description
	//				jsTextbox("Configuration.RateCode.txtDescription", configMap.get("DESCRIPTION"));
	//				logger.log(LogStatus.INFO, "Entered description: " + configMap.get("DESCRIPTION"));
	//
	//				Thread.sleep(3000);
	//				//Enter Start Sell Date
	//				jsTextbox("Configuration.RateCode.txtStartSellDate", configMap.get("BEGIN_DATE"));
	//				logger.log(LogStatus.INFO, "Entered Start Sell Date: " + configMap.get("BEGIN_DATE"));
	//
	//				Thread.sleep(3000);
	//				//Enter End Sell Date
	//				jsTextbox("Configuration.RateCode.txtEndSellDate", configMap.get("END_DATE"));
	//				logger.log(LogStatus.INFO, "Entered End Sell Date: " + configMap.get("END_DATE"));
	//
	//				Thread.sleep(3000);
	//				//Enter Rooms Types
	//				jsTextbox("Configuration.RateCode.txtRoomTypes", configMap.get("ROOM_TYPES"));
	//				logger.log(LogStatus.INFO, "Entered Room Type: " + configMap.get("ROOM_TYPES"));
	//
	//				Thread.sleep(3000);
	//				//Enter Rate Category
	//				jsTextbox("Configuration.RateCode.txtRateCategory", configMap.get("RATE_CATEGORY"));
	//				logger.log(LogStatus.INFO, "Entered Rate Category: " + configMap.get("RATE_CATEGORY"));
	//
	//				Thread.sleep(3000);
	//				//Enter Market Code
	//				jsTextbox("Configuration.RateCode.txtMarketCode", configMap.get("MARKET_CODE"));
	//				logger.log(LogStatus.INFO, "Entered Market Code: " + configMap.get("MARKET_CODE"));
	//
	//				Thread.sleep(3000);
	//				//Enter Transaction Code
	//				jsTextbox("Configuration.RateCode.txtTransactionCode", configMap.get("TRANSACTION_CODE"));
	//				logger.log(LogStatus.INFO, "Entered Transaction Code: " + configMap.get("TRANSACTION_CODE"));
	//
	//				Thread.sleep(3000);
	//				//Enter Display Set
	//				jsTextbox("Configuration.RateCode.txtDisplaySet", configMap.get("DISPLAY_SET"));
	//				logger.log(LogStatus.INFO, "Entered Display Set: " + configMap.get("DISPLAY_SET"));
	//
	//				Thread.sleep(5000);
	//				if(!element("Configuration.RateCode.chkboxPrintRate").isSelected())
	//					jsClick("Configuration.RateCode.chkboxPrintRate", 100, "presence");
	//				Thread.sleep(3000);
	//				/*Thread.sleep(3000);
	//				WebElement test3 = element("Configuration.RateCode.chkboxPrintRate");
	//				Actions actions3 = new Actions(driver);
	//				actions3.moveToElement(test3).click().build().perform();*/
	//
	//				Thread.sleep(3000);
	//				// Clicked on Save and Go To Presentation Screen button
	//				jsClick("Configuration.RateCode.btnSavePresentationScreen", 100, "presence");
	//
	//				Thread.sleep(15000);
	//				// Clicked on Pricing Schedules link
	//				jsClick("Configuration.RateCode.linkPricingSchedules", 100, "presence");
	//
	//				Thread.sleep(3000);
	//				// Clicked on New link
	////				jsClick("Configuration.RateCode.PricingSchedule.linkNew", 100, "presence");
	//				//Click on Cancel button in the Settings popup
	//				JavascriptExecutor executor = (JavascriptExecutor) driver;
	//				List<WebElement> webElement = BaseClass.driver.findElements(OR.getLocator("Configuration.RateCode.PricingSchedule.linkNew"));
	//				System.out.println(" size :: "+ webElement.size());			
	//				executor = (JavascriptExecutor) driver;
	//				executor.executeScript("arguments[0].click();", webElement.get(webElement.size()-1));
	//				logger.log(LogStatus.INFO, "Clicked on Link New");
	//				
	//				Thread.sleep(5000);
	//				//Enter Start Sell Date
	//				//jsTextbox("Configuration.RateCode.PricingSchedule.txtStartSellDate", configMap.get("BEGIN_DATE"));
	//				webElement = BaseClass.driver.findElements(OR.getLocator("Configuration.RateCode.PricingSchedule.txtStartSellDate"));
	//				System.out.println(" size :: "+ webElement.size());			
	//				executor = (JavascriptExecutor) driver;
	//				executor.executeScript("arguments[0].value='" + configMap.get("BEGIN_DATE") + "';", webElement.get(webElement.size()-1));				
	////				tabKey("Configuration.RateCode.PricingSchedule.txtStartSellDate");
	//				logger.log(LogStatus.INFO, "Entered Start Sell Date: " + configMap.get("BEGIN_DATE"));
	//
	//				Thread.sleep(5000);
	//				//Enter End Sell Date
	//				//jsTextbox("Configuration.RateCode.PricingSchedule.txtEndSellDate", configMap.get("END_DATE"));
	//				//tabKey("Configuration.RateCode.PricingSchedule.txtEndSellDate");
	//				webElement = BaseClass.driver.findElements(OR.getLocator("Configuration.RateCode.PricingSchedule.txtEndSellDate"));
	//				System.out.println(" size :: "+ webElement.size());			
	//				executor = (JavascriptExecutor) driver;
	//				executor.executeScript("arguments[0].value='" + configMap.get("END_DATE") + "';", webElement.get(webElement.size()-1));			
	//				logger.log(LogStatus.INFO, "Entered End Sell Date: " + configMap.get("END_DATE"));
	//
	//				Thread.sleep(5000);
	//				//Enter Amount
	//				jsTextbox("Configuration.RateCode.PricingSchedule.txtAdult", configMap.get("AMOUNT"));
	//				logger.log(LogStatus.INFO, "Entered Adult Amount: " + configMap.get("AMOUNT"));
	//
	//				Thread.sleep(3000);
	//				//Enter Rooms Types
	//				//jsTextbox("Configuration.RateCode.PricingSchedule.txtRoomTypes", configMap.get("ROOM_TYPES"));
	//				webElement = BaseClass.driver.findElements(OR.getLocator("Configuration.RateCode.PricingSchedule.txtRoomTypes"));
	//				System.out.println(" size :: "+ webElement.size());			
	//				executor = (JavascriptExecutor) driver;
	//				executor.executeScript("arguments[0].value='" + configMap.get("ROOM_TYPES") + "';", webElement.get(webElement.size()-1));	
	//				logger.log(LogStatus.INFO, "Entered Room Type: " + configMap.get("ROOM_TYPES"));
	//
	//				Thread.sleep(3000);
	//				// Clicked on Save button
	//				jsClick("Configuration.RateCode.PricingSchedule.btnSave", 100, "presence");
	//
	//				Thread.sleep(10000);
	//				// Clicked on Close button
	//				if(isExists("Configuration.RateCode.PricingSchedule.btnClose"))
	//					jsClick("Configuration.RateCode.PricingSchedule.btnClose", 100, "presence");
	//
	//				Thread.sleep(10000);
	//				// Clicked on Back To Rate Codes button
	//				if(isExists("Configuration.RateCode.linkBackToRateCodes"))
	//					jsClick("Configuration.RateCode.linkBackToRateCodes", 100, "presence");
	//
	//				Thread.sleep(10000);
	//				// Clicked on Back To Create New Rate Codes button
	//				if(isExists("Configuration.RateCode.linkBackToCreateNewRateCodes"))
	//					jsClick("Configuration.RateCode.linkBackToCreateNewRateCodes", 100, "presence");
	//
	//				Thread.sleep(8000);
	//				//Enter Rate Code
	//				jsTextbox("Configuration.RateCode.txtRateCode", configMap.get("RATE_CODE"));
	//				tabKey("Configuration.RateCode.txtRateCode");
	//				logger.log(LogStatus.INFO, "Entered Rate Code: " + configMap.get("RATE_CODE"));
	//
	//				Thread.sleep(8000);
	//				// Clicked on Search button
	//				jsClick("Configuration.RateCode.btnSearch", 100, "presence");
	//
	//				//Thread.sleep(8000);
	//				Thread.sleep(8000);
	//				if(isExists("Configuration.RateCode.validationRateCode")) {
	//					System.out.println("Prop text: " + getText("Configuration.RateCode.validationRateCode"));
	//					if(getText("Configuration.RateCode.validationRateCode").contains(configMap.get("RATE_CODE"))) {
	//						System.out.println("Rate Code " + configMap.get("RATE_CODE") + " is successfully created");
	//						logger.log(LogStatus.PASS,"Rate Code " + configMap.get("RATE_CODE") + " is successfully created");
	//					} 
	//					else {
	//						System.out.println("Rate Code " + configMap.get("RATE_CODE") + " is not created");
	//						logger.log(LogStatus.FAIL,"Rate Code " + configMap.get("RATE_CODE") + " is not created");
	//					}
	//				}
	//			}
	//		} catch (Exception e) {
	//			try {
	//				Alert alert = driver.switchTo().alert();
	//				String alertText = alert.getText();
	//				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
	//				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
	//				System.out.println("Alert data: " + alertText);
	//				alert.accept();
	//			} catch (NoAlertPresentException ex) {
	//				ex.printStackTrace();
	//			}
	//			Utils.takeScreenshot(driver, ScriptName);
	//			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
	//			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
	//			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
	//			throw (e);
	//
	//		}
	//	}


	public static void createRateCode(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			jsClick("Configuration.mainMenu", 100, "presence");

			// Navigating to Administration
			jsClick("Configuration.AdminstrationMenu", 100, "presence");
			waitForSpinnerToDisappear(10);

			// Clicked on Financial Menu
			jsClick("Configuration.RateCategory.menuFinancial", 100, "presence");

			// Clicked on Rate Management Menu
			jsClick("Configuration.RateCategory.menuRateManagement", 100, "presence");

			// Clicked on Rate Codes Menu Item
			jsClick("Configuration.RateCode.menuItemRateCodes", 100, "presence");

			//			//Enter Rate Code
			jsTextbox("Configuration.RateCode.txtRateCode", configMap.get("RATE_CODE"));

			// Clicked on Search button
			jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

			Thread.sleep(3000);
			if(isExists("Configuration.RateClass.popupLinkCancel")) {
				jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
				element("Configuration.RateCode.txtRateCodeWithText").clear();
			}

			//List<WebElement>  rows = Utils.elements("Configuration.RateCode.searchRates"); 

			List<String>  rows = Utils.getAllValuesFromTableBasedOnColumnName("Code");
			System.out.println("No of rows are : " + rows.size());
			System.out.println("rows: " + rows);
			boolean flag = false;
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					System.out.println("" + rows.get(i));
					if(rows.get(i).equalsIgnoreCase(configMap.get("RATE_CODE"))) {
						System.out.println("Rate Code "+rows.get(i)+" is already existing in Property");
						flag = true;
						logger.log(LogStatus.PASS, "Rate Code "+rows.get(i)+" is already existing in Property");
						break;
					}
					else {
						System.out.println("Rate Code is not present in Property:: "+rows.get(i));						
					}
				}
			}
			if(!flag) {
				// Clicked on New button
				jsClick("Configuration.RateCategory.linkNew", 100, "presence");

				Thread.sleep(3000);
				//Enter Rate Code
				jsTextbox("Configuration.RateCode.txtRateCode", configMap.get("RATE_CODE"));
				logger.log(LogStatus.INFO, "Entered rate code: " + configMap.get("RATE_CODE"));

				Thread.sleep(3000);
				//Enter Description
				jsTextbox("Configuration.RateCode.txtDescription", configMap.get("DESCRIPTION"));
				logger.log(LogStatus.INFO, "Entered description: " + configMap.get("DESCRIPTION"));

				Thread.sleep(3000);
				//Enter Start Sell Date
				jsTextbox("Configuration.RateCode.txtStartSellDate", configMap.get("BEGIN_DATE"));
				logger.log(LogStatus.INFO, "Entered Start Sell Date: " + configMap.get("BEGIN_DATE"));

				Thread.sleep(3000);
				//Enter End Sell Date
				jsTextbox("Configuration.RateCode.txtEndSellDate", configMap.get("END_DATE"));
				logger.log(LogStatus.INFO, "Entered End Sell Date: " + configMap.get("END_DATE"));

				Thread.sleep(3000);
				//Enter Rooms Types
				jsTextbox("Configuration.RateCode.txtRoomTypes", configMap.get("ROOM_TYPES"));
				logger.log(LogStatus.INFO, "Entered Room Type: " + configMap.get("ROOM_TYPES"));

				Thread.sleep(3000);
				//Enter Rate Category
				jsTextbox("Configuration.RateCode.txtRateCategory", configMap.get("RATE_CATEGORY"));
				logger.log(LogStatus.INFO, "Entered Rate Category: " + configMap.get("RATE_CATEGORY"));

				Thread.sleep(3000);
				//Enter Market Code
				jsTextbox("Configuration.RateCode.txtMarketCode", configMap.get("MARKET_CODE"));
				logger.log(LogStatus.INFO, "Entered Market Code: " + configMap.get("MARKET_CODE"));

				Thread.sleep(3000);
				//Enter Transaction Code
				jsTextbox("Configuration.RateCode.txtTransactionCode", configMap.get("TRANSACTION_CODE"));
				logger.log(LogStatus.INFO, "Entered Transaction Code: " + configMap.get("TRANSACTION_CODE"));

				Thread.sleep(3000);
				//Enter Display Set
				jsTextbox("Configuration.RateCode.txtDisplaySet", configMap.get("DISPLAY_SET"));
				logger.log(LogStatus.INFO, "Entered Display Set: " + configMap.get("DISPLAY_SET"));

				Thread.sleep(5000);
				if(!element("Configuration.RateCode.chkboxPrintRate").isSelected())
					jsClick("Configuration.RateCode.chkboxPrintRate", 100, "presence");
				Thread.sleep(3000);
				/*Thread.sleep(3000);
				WebElement test3 = element("Configuration.RateCode.chkboxPrintRate");
				Actions actions3 = new Actions(driver);
				actions3.moveToElement(test3).click().build().perform();*/

				Thread.sleep(3000);
				// Clicked on Save and Go To Presentation Screen button
				jsClick("Configuration.RateCode.btnSavePresentationScreen", 100, "presence");

				Thread.sleep(15000);
				// Clicked on Pricing Schedules link
				jsClick("Configuration.RateCode.linkPricingSchedules", 100, "presence");

				Thread.sleep(3000);
				// Clicked on New link
				jsClick("Configuration.RateCode.PricingSchedule.linkNew", 100, "presence");

				Thread.sleep(5000);
				//Enter Start Sell Date
				jsTextbox("Configuration.RateCode.PricingSchedule.txtStartSellDate", configMap.get("BEGIN_DATE"));
				tabKey("Configuration.RateCode.PricingSchedule.txtStartSellDate");
				logger.log(LogStatus.INFO, "Entered Start Sell Date: " + configMap.get("BEGIN_DATE"));

				Thread.sleep(5000);
				//Enter End Sell Date
				jsTextbox("Configuration.RateCode.PricingSchedule.txtEndSellDate", configMap.get("END_DATE"));
				tabKey("Configuration.RateCode.PricingSchedule.txtEndSellDate");
				logger.log(LogStatus.INFO, "Entered End Sell Date: " + configMap.get("END_DATE"));

				Thread.sleep(5000);
				//Enter Amount
				jsTextbox("Configuration.RateCode.PricingSchedule.txtAdult", configMap.get("AMOUNT"));
				logger.log(LogStatus.INFO, "Entered Adult Amount: " + configMap.get("AMOUNT"));

				Thread.sleep(3000);
				//Enter Rooms Types
				jsTextbox("Configuration.RateCode.PricingSchedule.txtRoomTypes", configMap.get("ROOM_TYPES"));
				logger.log(LogStatus.INFO, "Entered Room Type: " + configMap.get("ROOM_TYPES"));

				Thread.sleep(3000);
				// Clicked on Save button
				jsClick("Configuration.RateCode.PricingSchedule.btnSave", 100, "presence");

				Thread.sleep(10000);
				// Clicked on Close button
				if(isExists("Configuration.RateCode.PricingSchedule.btnClose"))
					jsClick("Configuration.RateCode.PricingSchedule.btnClose", 100, "presence");

				Thread.sleep(10000);
				// Clicked on Back To Rate Codes button
				if(isExists("Configuration.RateCode.linkBackToRateCodes"))
					jsClick("Configuration.RateCode.linkBackToRateCodes", 100, "presence");

				Thread.sleep(10000);
				// Clicked on Back To Create New Rate Codes button
				if(isExists("Configuration.RateCode.linkBackToCreateNewRateCodes"))
					jsClick("Configuration.RateCode.linkBackToCreateNewRateCodes", 100, "presence");

				Thread.sleep(8000);
				//Enter Rate Code
				jsTextbox("Configuration.RateCode.txtRateCode", configMap.get("RATE_CODE"));
				tabKey("Configuration.RateCode.txtRateCode");
				logger.log(LogStatus.INFO, "Entered Rate Code: " + configMap.get("RATE_CODE"));

				Thread.sleep(8000);
				// Clicked on Search button
				jsClick("Configuration.RateCode.btnSearch", 100, "presence");

				//Thread.sleep(8000);
				Thread.sleep(8000);

				List<String>  rows1 = Utils.getAllValuesFromTableBasedOnColumnName("Code");
				System.out.println("No of rows are : " + rows1.size());

				if(rows1.size() > 0) {
					for(int i=0;i<rows1.size();i++) {
						System.out.println("" + rows1.get(i));
						if(rows1.get(i).equalsIgnoreCase(configMap.get("RATE_CODE"))) {
							System.out.println("Rate Code " + configMap.get("RATE_CODE") + " is successfully created");
							logger.log(LogStatus.PASS,"Rate Code " + configMap.get("RATE_CODE") + " is successfully created");
							break;
						}
						else {
							System.out.println("Rate Code " + configMap.get("RATE_CODE") + " is not created");
							logger.log(LogStatus.FAIL,"Rate Code " + configMap.get("RATE_CODE") + " is not created");					
						}
					}
				}


			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}



	/*******************************************************************
	-  Description: Creating market Code as the part of Configuration 
	- Input: MARKET_CODE, DESCRIPTION , MARKET_GROUP , COLOR , SEQUENCE


	- Output: MARKET_CODE
	- Author: swati
	- Date:10/12/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: swati

	 ********************************************************************/


	public static void marketCode(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Navigating to Booking menu
			click("Configuration.AdminMenu_Booking", 100, "clickable");

			// Navigating to Marketing Management menu
			click("Configuration.menu_MarketingManagement", 100, "clickable");

			// Navigating to Market Codes
			click("Configuration.menu_MarketCodes", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketCode.Heading", 100, "presence"), "Market Codes");
			logger.log(LogStatus.PASS, "Landed in Market Codes Page");
			Thread.sleep(5000);


			List<WebElement>  rows = Utils.elements("Configuration.MarketCode.TableGrid"); 
			System.out.println("No of rows are : " + rows.size());

			Boolean flag = false;
			if(rows.size() > 0)
			{

				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.MarketCode.TableGrid", configMap.get("MARKET_CODE"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Market code "+configMap.get("MARKET_CODE")+"is already available in the property");
					logger.log(LogStatus.PASS, "The Market code "+configMap.get("MARKET_CODE")+"is already available in the property");

				}

			}	

			if(!flag)
			{

				//Navigate to Template screen
				mouseHover("Configuration.tab_Template",100,"presence");
				jsClick("Configuration.tab_Template",100,"presence");
				Thread.sleep(10000);

				WebdriverWait(100, "Configuration.MarketCode.TableGrid", "visible");


				//Select Search
				mouseHover("Configuration.MarketGroup.TemplateSearchButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateSearchButton",100,"presence");


				//Checking if the market group is present in the template or not
				flag = Utils.ValidateGridData("Configuration.MarketCode.TableGrid", configMap.get("MARKET_CODE"));
				System.out.println("flag2" + flag);

				if(!flag)
				{

					logger.log(LogStatus.PASS, "Market code is not present in Template , Creating a new one");
					Utils.Wait(10000);

					//Create the new template
					mouseHover("Configuration.MarketGroup.TemplateNew",100,"presence");
					jsClick("Configuration.MarketGroup.TemplateNew",100,"presence");

					Assert.assertEquals(getText("Configuration.MarketGroup.TemplateDescriptionLable", 100, "presence"), "*Description");	
					logger.log(LogStatus.PASS, "Landed in new template creation page");

					//Provide Market code Text
					textBox("Configuration.MarketCode.Text", configMap.get("MARKET_CODE"), 100, "presence");
					Utils.tabKey("Configuration.MarketCode.Text");

					//Provide Market group
					textBox("Configuration.MarketGroup.TemplateMargetGroupTextNew" , configMap.get("MARKET_GROUP"),100,"presence");
					Utils.tabKey("Configuration.MarketGroup.TemplateMargetGroupTextNew");

					Utils.Wait(2000);

					//Provide Market group Description
					textBox("Configuration.MarketGroup.TemplateMargetGroupDescription", configMap.get("DESCRIPTION"), 100, "presence");
					Utils.tabKey("Configuration.MarketGroup.TemplateMargetGroupDescription");
					Utils.Wait(2000);
					//Provide color


					Utils.selectBy("Configuration.MarketCode.color", "value", configMap.get("DISPLAY_COLOR"));
					Utils.tabKey("Configuration.MarketCode.color");

					Utils.Wait(2000);

					//Provide sequence
					textBox("Configuration.MarketCode.sequence",configMap.get("DISPLAY_SEQ"),100,"presence");
					Utils.tabKey("Configuration.MarketCode.sequence");
					Utils.Wait(2000);
					//Clicking on Save button

					mouseHover("Configuration.MarketGroup.TemplateSaveButton",100,"presence");
					jsClick("Configuration.MarketGroup.TemplateSaveButton",100,"presence");

				}	
				Utils.Wait(5000);

				textBox("Configuration.MarketCode.Text", configMap.get("MARKET_CODE"), 100, "presence");
				Utils.tabKey("Configuration.MarketCode.Text");

				mouseHover("Configuration.MarketGroup.TemplateSearchButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateSearchButton",100,"presence");

				Utils.Wait(10000);

				//Select More Menu 
				mouseHover("Configuration.MarketGroup.TemplateMoreButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateMoreButton",100,"presence");

				//Select Copy link 
				mouseHover("Configuration.MarketGroup.TemplateCopyButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateCopyButton",100,"presence");

				//Copy Page Validation
				//WebdriverWait(100, "Configuration.MarketGroup.TemplateMargetGroupText", "stale");

				//Thread.sleep(3000);

				//Provide Market Group
				Assert.assertEquals(getText("Configuration.MarketGroup.CopyHeading", 100, "presence"), "Copy Market Codes Template(s)");	
				logger.log(LogStatus.PASS, "Landed in page for copying template to the property");

				Utils.WebdriverWait(100, "Configuration.MarketCode.Text", "visible");
				Utils.jsTextbox("Configuration.MarketCode.Text",configMap.get("MARKET_CODE"));   // MarketGroup
				System.out.println("Provide Market Code to Copy");
				logger.log(LogStatus.PASS, "Provided Market Code for Copy");

				//String prop = Utils.element("Configuration.MarketGroup.CopyScreenTargetProperty").getText();
				//System.out.println("property" + prop);
				//if(prop.equals(envMap.get("Property"))) {
				//Select Save
				Utils.WebdriverWait(100, "Configuration.MarketGroup.CopyScreenSave", "clickable");
				Utils.jsClick("Configuration.MarketGroup.CopyScreenSave");
				System.out.println("clicked Save to Copy");
				logger.log(LogStatus.PASS, "Selected Save to Copy");

				waitForPageLoad(100);

				//Select Copy and Continue
				Utils.WebdriverWait(100, "Configuration.MarketGroup.Copy&Continue", "clickable");
				Utils.jsClick("Configuration.MarketGroup.Copy&Continue");
				System.out.println("clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Selected Copy and Continue button");

				//Validation for Copying Market Code 

				Assert.assertEquals(getText("Configuration.MarketGroup.ConfirmationMessage", 100, "presence"), "Copy Template Code(s) was successful");

				//Going back to market groups page
				mouseHover("Configuration.MarketGroup.BackLink",100,"presence");
				jsClick("Configuration.MarketGroup.BackLink",100,"presence");

				waitForPageLoad(100);

				mouseHover("Configuration.MarketGroup.tabProperty",100,"presence");
				jsClick("Configuration.MarketGroup.tabProperty",100,"presence");

				Assert.assertEquals(getText("Configuration.MarketGroup.Property", 100, "presence"), "*Property");
				logger.log(LogStatus.PASS, "Landed back in Property page");

				//Provide MarketGroup
				textBox("Configuration.MarketCode.Text", configMap.get("MARKET_CODE"), 100, "visible");
				Utils.tabKey("Configuration.MarketCode.Text");

				//Select Search
				mouseHover("Configuration.MarketGroup.TemplateSearchButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateSearchButton",100,"presence");

				Thread.sleep(10000);
				//Checking if the market group is present in the template or not
				flag = Utils.ValidateGridData("Configuration.MarketCode.TableGrid", configMap.get("MARKET_CODE"));
				System.out.println("flag3" + flag);

				if(flag) {
					System.out.println("Market code is configured for the property" + configMap.get("MARKET_CODE"));
					logger.log(LogStatus.PASS, "Market code is configured for the property->  " + configMap.get("MARKET_CODE"));

				}
				else {
					logger.log(LogStatus.FAIL, "Market code is not configured for the property");
				}

			}

		}				
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: Creating market group as the part of Configuration 
	- Input: MARKET_GROUP, DESCRIPTION


	- Output: MARKET_GROUP
	- Author: swati
	- Date:10/12/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: swati

	 ********************************************************************/


	public static void marketGroup(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			// Navigating to Main Menu
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Navigating to Booking menu
			click("Configuration.AdminMenu_Booking", 100, "clickable");

			// Navigating to Marketing Management menu
			click("Configuration.menu_MarketingManagement", 100, "clickable");

			// Navigating to Market Groups
			click("Configuration.menu_MarketGroups", 100, "clickable");
			
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			List<WebElement> webElement = BaseClass.driver.findElements(OR.getLocator("Configuration.menu_MarketGroups"));
			System.out.println(" size :: "+ webElement.size());			
			executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", webElement.get(webElement.size()-1));

			Assert.assertEquals(getText("Configuration.MarketGroup.Heading", 100, "presence"), "Market Groups");
			logger.log(LogStatus.PASS, "Landed in Market Groups Page");
			Thread.sleep(5000);

			List<WebElement>  rows = Utils.elements("Configuration.MarketGroup.TableGrid"); 
			System.out.println("No of rows are : " + rows.size());

			Boolean flag = false;
			if(rows.size() > 0)
			{

				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.MarketGroup.TableGrid", configMap.get("MARKET_GROUP"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Market group "+configMap.get("MARKET_GROUP")+"is already available in the property");
					logger.log(LogStatus.PASS, "The Market group "+configMap.get("MARKET_GROUP")+"is already available in the property");

				}

			}	

			if(!flag)
			{
				//Navigate to Template screen
				mouseHover("Configuration.tab_Template",100,"presence");
				jsClick("Configuration.tab_Template",100,"presence");
				Thread.sleep(10000);

				WebdriverWait(100, "Configuration.MarketGroup.TableGrid", "visible");

				//Select Search
				mouseHover("Configuration.MarketGroup.TemplateSearchButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateSearchButton",100,"presence");


				//Checking if the market group is present in the template or not
				flag = Utils.ValidateGridData("Configuration.MarketGroup.TableGrid", configMap.get("MARKET_GROUP"));
				System.out.println("flag2" + flag);

				if(!flag)
				{

					logger.log(LogStatus.PASS, "Market code is not present in Template , Creating a new one");

					//Create the new template
					mouseHover("Configuration.MarketGroup.TemplateNew",100,"presence");
					jsClick("Configuration.MarketGroup.TemplateNew",100,"presence");

					Assert.assertEquals(getText("Configuration.MarketGroup.TemplateDescriptionLable", 100, "presence"), "*Description");	
					logger.log(LogStatus.PASS, "Landed in new template creation page");

					//Provide Market group Text
					textBox("Configuration.MarketGroup.TemplateMargetGroupTextNew", configMap.get("MARKET_GROUP"), 100, "presence");

					//Provide Market group Description
					textBox("Configuration.MarketGroup.TemplateMargetGroupDescription", configMap.get("MARKET_GROUP"), 100, "presence");

					//Clicking on Save button

					mouseHover("Configuration.MarketGroup.TemplateSaveButton",100,"presence");
					jsClick("Configuration.MarketGroup.TemplateSaveButton",100,"presence");

					waitForPageLoad(100);

				}	
				Utils.Wait(5000);

				textBox("Configuration.MarketGroup.TemplateMargetGroupText", configMap.get("MARKET_GROUP"), 100, "presence");
				Utils.tabKey("Configuration.MarketGroup.TemplateMargetGroupText");

				mouseHover("Configuration.MarketGroup.TemplateSearchButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateSearchButton",100,"presence");

				Utils.Wait(10000);

				//Select More Menu 
				mouseHover("Configuration.MarketGroup.TemplateMoreButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateMoreButton",100,"presence");

				//Select Copy link 
				mouseHover("Configuration.MarketGroup.TemplateCopyButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateCopyButton",100,"presence");

				//Copy Page Validation
				//WebdriverWait(100, "Configuration.MarketGroup.TemplateMargetGroupText", "stale");

				//Thread.sleep(3000);

				//Provide Market Group
				Assert.assertEquals(getText("Configuration.MarketCode.CopyHeading", 100, "presence"), "Copy Market Groups Template(s)");	
				logger.log(LogStatus.PASS, "Landed in page for copying template to the property");

				Utils.WebdriverWait(100, "Configuration.MarketGroup.TemplateMargetGroupText", "visible");
				Utils.jsTextbox("Configuration.MarketGroup.TemplateMargetGroupText",configMap.get("MARKET_GROUP"));   // MarketGroup
				System.out.println("Provide MarketGroup to Copy");
				logger.log(LogStatus.PASS, "Provided Market Group for Copy");

				//String prop = Utils.element("Configuration.MarketGroup.CopyScreenTargetProperty").getText();
				//System.out.println("property" + prop);
				//if(prop.equals(envMap.get("Property"))) {
				//Select Save
				Utils.WebdriverWait(100, "Configuration.MarketGroup.CopyScreenSave", "clickable");
				Utils.jsClick("Configuration.MarketGroup.CopyScreenSave");
				System.out.println("clicked Save to Copy");
				logger.log(LogStatus.PASS, "Selected Save to Copy");

				waitForPageLoad(100);

				//Select Copy and Continue
				Utils.WebdriverWait(100, "Configuration.MarketGroup.Copy&Continue", "clickable");
				Utils.jsClick("Configuration.MarketGroup.Copy&Continue");
				System.out.println("clicked Copy and Continue button");
				logger.log(LogStatus.PASS, "Selected Copy and Continue button");

				//Validation for Copying Market Code 

				Assert.assertEquals(getText("Configuration.MarketGroup.ConfirmationMessage", 100, "presence"), "Copy Template Code(s) was successful");

				//Going back to market groups page
				mouseHover("Configuration.MarketGroup.BackLink",100,"presence");
				jsClick("Configuration.MarketGroup.BackLink",100,"presence");

				waitForPageLoad(100);

				mouseHover("Configuration.MarketGroup.tabProperty",100,"presence");
				jsClick("Configuration.MarketGroup.tabProperty",100,"presence");

				Assert.assertEquals(getText("Configuration.MarketGroup.Property", 100, "presence"), "*Property");
				logger.log(LogStatus.PASS, "Landed back in Property page");

				//Provide MarketGroup
				textBox("Configuration.MarketGroup.TemplateMargetGroupText", configMap.get("MARKET_GROUP"), 100, "visible");
				Utils.tabKey("Configuration.MarketGroup.TemplateMargetGroupText");

				//Select Search
				mouseHover("Configuration.MarketGroup.TemplateSearchButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateSearchButton",100,"presence");

				Thread.sleep(10000);
				//Checking if the market group is present in the template or not
				flag = Utils.ValidateGridData("Configuration.MarketGroup.TableGrid", configMap.get("MARKET_GROUP"));
				System.out.println("flag3" + flag);

				if(flag) {
					System.out.println("Market group is configured for the property" + configMap.get("MARKET_GROUP"));
					logger.log(LogStatus.PASS, "Market group is configured for the property->  " + configMap.get("MARKET_GROUP"));

				}
				else {
					logger.log(LogStatus.FAIL, "Market group is not configured for the property");
				}

			}


		}				
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}


	}



	/*******************************************************************
		- Description: Creating Report Group as the part of Configuration 
		- Input: REPORT_GROUP, DESCRIPTION
		- Output: 
		- Author: srikanth konda
		- Date:12/12/2018
		- Revision History:
		                - Change Date 
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By: 

	 ********************************************************************/


	public static void createReportGroup(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			// Navigating to Reports menu
			Utils.WebdriverWait(100, "Configuration.menu_Reports", "clickable");
			Utils.mouseHover("Configuration.menu_Reports");
			Utils.click(Utils.element("Configuration.menu_Reports"));
			System.out.println("clicked Reports menu");
			logger.log(LogStatus.PASS, "Selected Reports Menu");
			Thread.sleep(5000);

			// Navigating to configure reports
			mouseHover("Configuration.menu_ConfigureReports",100,"clickable");
			Thread.sleep(5000);

			// Navigating to Reports Groups
			mouseHover("Configuration.menu_ReportGroups",100,"presence");
			jsClick("Configuration.menu_ReportGroups",100,"presence");
			Assert.assertEquals(getText("Configuration.headerReportGroup", 100, "presence"), "Report Groups");
			logger.log(LogStatus.PASS, "Landed in Report Groups Page");
			Thread.sleep(5000);

			// search for report group to see if its already present
			Boolean flag = false;
			textBox("Configuration.txt_ReportGroup", configMap.get("ReportGroup"), 100, "presence");
			Utils.tabKey("Configuration.txt_ReportGroup");
			mouseHover("Configuration.btnSearch",100,"presence");
			jsClick("Configuration.btnSearch");

			Utils.Wait(5000);
			flag = driver.findElements(By.xpath("//span[text()='"+configMap.get("ReportGroup")+"']")).size() > 0;

			// create a new report group and validate the record
			if(!flag)
			{

				jsClick("Configuration.lnk_New");
				Utils.WebdriverWait(100, "Configuration.txt_GroupName", "presence");
				textBox("Configuration.txt_GroupName", configMap.get("ReportGroup"), 100, "presence");
				textBox("Configuration.text_Group_Description", configMap.get("ReportGroup"), 100, "presence");
				jsClick("Configuration.chk_Internal");	

				jsClick("Configuration.btnSave");	
				Utils.Wait(5000);

				try {

					flag = driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportGroup")+"']")).isDisplayed();
					logger.log(LogStatus.PASS, "Report Group" + configMap.get("ReportGroup") + "created successfully");
					System.out.println("Report Group" + configMap.get("ReportGroup") + "created successfully");

				} catch(Exception e) {
					logger.log(LogStatus.PASS, "Report Group" + configMap.get("ReportGroup") + " not created ");
					System.out.println("Report Group" + configMap.get("ReportGroup") + " not created");
				}
			}
			else{
				logger.log(LogStatus.PASS, "Report Group" + configMap.get("ReportGroup") + " already exists for the property");
				System.out.println("Report Group" + configMap.get("ReportGroup") + " already exists for the property");

			}
		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Report Group  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Report Group not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}

	}



	/*******************************************************************
		- Description: Creating Report  as the part of Configuration 
		- Input: REPORT_GROUP, REPORT_NAME
		- Output: 
		- Author: srikanth konda
		- Date:17/12/2018
		- Revision History:
		                - Change Date 
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By: 

	 ********************************************************************/


	public static void ConfigureReport(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			// Navigating to Reports menu
			Utils.WebdriverWait(100, "Configuration.menu_Reports", "clickable");
			Utils.mouseHover("Configuration.menu_Reports");
			Utils.click(Utils.element("Configuration.menu_Reports"));
			System.out.println("Selected Reports menu");
			logger.log(LogStatus.PASS, "Selected Reports Menu");
			Thread.sleep(5000);

			// Navigating to configure reports
			mouseHover("Configuration.menu_ConfigureReports",100,"clickable");
			Thread.sleep(5000);

			// search for the report to see if its already present
			mouseHover("Configuration.menu_CreateReports",100,"presence");
			jsClick("Configuration.menu_CreateReports",100,"presence");
			Assert.assertEquals(getText("Configuration.headerCreateReport", 100, "presence"), "Create Reports");
			logger.log(LogStatus.PASS, "Landed in Create Reports Page");
			Thread.sleep(5000);



			// search for the report to see if its already present
			Boolean flag = false;
			textBox("Configuration.txt_ReportName", configMap.get("FileName"), 100, "presence");
			Utils.tabKey("Configuration.txt_ReportName");
			Utils.Wait(4000);
			selectBy("Configuration.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
			//Utils.Wait(4000);
			Utils.tabKey("Configuration.drpdwn_ReportGroup");
			mouseHover("Configuration.txt_DisplayName",100,"presence");
			Utils.Wait(3000);
			textBox("Configuration.txt_DisplayName", configMap.get("ReportName"), 100, "presence");
			Utils.tabKey("Configuration.txt_DisplayName");
			Utils.Wait(3000);
			mouseHover("Configuration.btnSave",100,"presence");
			jsClick("Configuration.btnSave");

			try {

				flag = Utils.isExists("Configuration.alert_ReportExists");
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			if (!flag) {

				// Navigating to Reports menu
				Utils.WebdriverWait(100, "Configuration.menu_Reports", "clickable");
				Utils.mouseHover("Configuration.menu_Reports");
				Utils.click(Utils.element("Configuration.menu_Reports"));
				System.out.println("Selected Reports menu");
				logger.log(LogStatus.PASS, "Selected Reports Menu");
				Thread.sleep(5000);

				// Navigating to configure reports
				mouseHover("Configuration.menu_ConfigureReports",100,"clickable");
				Thread.sleep(5000);

				// search for the report to see if its already present
				mouseHover("Configuration.menu_ManageReports",100,"presence");
				jsClick("Configuration.menu_ManageReports",100,"presence");
				Assert.assertEquals(getText("Configuration.headerManageReports", 100, "presence"), "Manage Reports");
				logger.log(LogStatus.PASS, "Landed in Manage Reports Page");
				Thread.sleep(5000);	

				textBox("Configuration.txt_ReportName", configMap.get("ReportName"), 100, "presence");
				Utils.mouseHover("Configuration.Reports.chk_ShowInternal");
				Utils.Wait(3000);
				Utils.click(Utils.element("Configuration.Reports.chk_ShowInternal"));
				Utils.Wait(3000);
				selectBy("Configuration.ManageRes.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
				Utils.tabKey("Configuration.ManageRes.drpdwn_ReportGroup");
				Utils.Wait(3000);
				Utils.mouseHover("Configuration.btnSearch");
				Utils.click(Utils.element("Configuration.btnSearch"));
				Utils.Wait(2000);
				try {

					driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportName")+"']")).isDisplayed();
					logger.log(LogStatus.PASS, "Report Name: " + configMap.get("ReportName") + " created successfully");
					System.out.println("Report Name: " + configMap.get("ReportName") + " created successfully");

				} catch(Exception e) {
					logger.log(LogStatus.PASS, "Report Name: " + configMap.get("ReportName") + " not created ");
					System.out.println("Report Name: " + configMap.get("ReportName") + " not created");
				}




			} else {
				logger.log(LogStatus.PASS, "Report Name: " + configMap.get("ReportName") + " already exists");
				System.out.println("Report Name: " + configMap.get("ReportName") + " already exists");

			}

		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Report Group  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Report Group not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}

	}


	/*******************************************************************
	    - Description: To create Cashier
		- Input:Cashier
		- Output:
		- Author:Shireesha
		- Date: 12/13/2018
		- Revision History:
		                - Change Date
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By
	 ********************************************************************/

	public static void createCashier(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");


			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Financial
			click("Configuration.AdminMenu_Financial", 100, "clickable");

			// Navigating to Cashiering  Management
			click("Configuration.AdminMenu_Financial_CashieringManagement", 100, "clickable");

			// Navigating to Cashiers page 
			click("Configuration.AdminMenu_Financial_CashieringManagement_Cashiers", 100, "clickable");
			System.out.println("In Cashiers page");
			logger.log(LogStatus.PASS, "Landed in Cashiers page ");


			WebdriverWait(30, "Configuration.txt_Cashier_Name", "presence");
			mouseHover("Configuration.txt_Cashier_Name");
			textBox("Configuration.txt_Cashier_Name",configMap.get("Name"));
			System.out.println("Cashier Name  is Entered for general Exports");
			logger.log(LogStatus.PASS, "Searching if cashier exist"+configMap.get("Name"));



			// Clicking on search button
			click("Configuration.btn_Cashier_Search", 100, "clickable");
			System.out.println("Clicking on the Search button");
			Wait(2000);

			// Verifying if Cashier exist
			//Fetch Search Grid Column Data and validate
			List<WebElement>  rows = Utils.elements("Configuration.table_Cashier_SearchGrid"); 
			System.out.println("No of rows are : " + rows.size());

			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					//((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("Name")))
					{
						System.out.println("CashierName"+rows.get(i).getText()+" is already existing");

						logger.log(LogStatus.PASS, "Cashier Name"+rows.get(i).getText()+" is already existing");
						break;
					}
				}
			}
			else
			{

				if(configMap.equals("Dataset_1"))


				{
					// wait for New link to load	
					Utils.WebdriverWait(100, "Configuration.link_Cashiers_New", "presence");

					// Clicking on New Button
					jsClick("Configuration.link_Cashiers_New", 100, "clickable");				

					// Entering the Description
					textBox("Configuration.txt_CreateCashier_StartingAmount",configMap.get("STARTAMOUNT"), 100, "presence");            
					// Entering the CashierName
					mouseHover("Configuration.txt_CreateCashier_Name");
					textBox("Configuration.txt_CreateCashier_Name",configMap.get("Name"), 100, "presence");
					// Entering the Transaction Code
					textBox("Configuration.txt_CreateCashier_MaximumDailyuses",configMap.get("MAXUSES"), 100, "presence");

					// Clicking on Save Button
					jsClick("Configuration.btn_CreateCashier_Save", 100, "presence");
					Wait(200);

				}

				else
				{
					// wait for New link to load	
					Utils.WebdriverWait(100, "Configuration.link_Cashiers_New", "presence");

					// Clicking on New Button
					jsClick("Configuration.link_Cashiers_New", 100, "clickable");				

					// Entering the Description
					textBox("Configuration.txt_CreateCashier_StartingAmount",configMap.get("STARTAMOUNT"), 100, "presence");            
					// Entering the CashierName
					mouseHover("Configuration.txt_CreateCashier_Name");
					textBox("Configuration.txt_CreateCashier_Name",configMap.get("Name"), 100, "presence");
					// Entering the Transaction Code
					textBox("Configuration.txt_CreateCashier_MaximumDailyuses",configMap.get("MAXUSES"), 100, "presence");

					jsClick("Configuration.btn_InterfaceCashier",100,"clickable");

					// Clicking on Save Button
					jsClick("Configuration.btn_CreateCashier_Save", 100, "presence");
					Wait(200);

				}



				// Verify if create Cashier is created    				
				if (ValidateGridData("Configuration.table_Cashier_SearchGrid", configMap.get("Name"))){
					System.out.println("Cashier is created successfully :"+configMap.get("Name"));
					logger.log(LogStatus.PASS, "Cashier is created successfully :"+configMap.get("Name"));
				} else {
					System.out.println("Cashier not is created successfully :"+configMap.get("Name"));
					logger.log(LogStatus.FAIL, "Cashier is not created successfully :"+configMap.get("Name"));
				}

			}


		}

		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Cashier :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Cashier not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}

	/*******************************************************************
	    - Description: To create a restricted rate code
		- Input:Market code , display set , room type
		- Output:
		- Author:Shireesha
		- Date: 12/19/2018
		- Revision History:
		                - Change Date
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By
	 ********************************************************************/




	/*public static void createRateRestriction(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {

			ConfigPage.createRateCode(configMap);

			click("Configuration.RateCode.LinkMore");
			waitForSpinnerToDisappear(30);

			click("Configuration.RateCode.Edit");
			waitForSpinnerToDisappear(30);

			//Checking if the pricing schedules exist for the rate code or not , if not then create one

			click("Configuration.RateCode.linkPricingSchedules");
			waitForSpinnerToDisappear(30);

			List ll = Utils.elements("Configuration.RateCode.CheckPricing");
			if(ll.size()==0)
			{    
				System.out.println("in pricing");

				logger.log(LogStatus.INFO, "Pricing is not present for the rate code , adding the display set now");

				click("Configuration.RateCode.PricingNew");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.RateCode.PricingSchedule.txtStartSellDate", configMap.get("BEGIN_DATE"));
				Utils.tabKey("Configuration.RateCode.PricingSchedule.txtStartSellDate");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.RateCode.PricingSchedule.txtEndSellDate", configMap.get("END_DATE"));
				Utils.tabKey("Configuration.RateCode.PricingSchedule.txtEndSellDate");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.RateCode.PricingSchedule.txtAdult", configMap.get("AMOUNT"));
				Utils.tabKey("Configuration.RateCode.PricingSchedule.txtAdult");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.RateCode.PricingSchedule.txtRoomTypes", configMap.get("ROOM_TYPES"));
				Utils.tabKey("Configuration.RateCode.PricingSchedule.txtRoomTypes");
				waitForSpinnerToDisappear(30);

				click("Configuration.RateCode.PricingSchedule.btnSave");
				waitForSpinnerToDisappear(30);

				if(Utils.isExists("Configuration.RateCode.CheckPricing")) {
					logger.log(LogStatus.PASS, "Pricing is added to the rate code");
				}
				else {
					logger.log(LogStatus.FAIL, "Pricing is not added to the rate code");
				}
			}
			else {
				logger.log(LogStatus.PASS, "Pricing is already attached to the rate code");

			}

			click("Configuration.RateCode.PricingSchedule.btnClose");
			waitForSpinnerToDisappear(100);

			//Validating if the rate code has a display set or not

			String displaySet = Utils.getText("Configuration.RateCode.DisplaySetPresence");
			if(displaySet.isEmpty()) {


				logger.log(LogStatus.INFO, "Display set is not present for the rate code , adding the display set now");
				click("Configuration.RateCode.DisplaySetEdit");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.RateCode.txtDisplaySet", configMap.get("DISPLAY_SET"));
				Utils.tabKey("Configuration.RateCode.txtDisplaySet");
				waitForSpinnerToDisappear(30);

				click("Configuration.RateCode.btnSave");
				waitForSpinnerToDisappear(30);

				logger.log(LogStatus.PASS, "Display Set is attached to the rate code");

			}

			else {
				logger.log(LogStatus.PASS, "Display Set is already attached to the rate code");
				System.out.println("display sey is already configured");
			}


			//((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Configuration.RateCode.linkPricingSchedules"));
			// ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("(//*[@data-ocid='LINK_RESTRICTIONS'])[1]")));
			click("Configuration.RateCode.linkRestrictions");
			waitForSpinnerToDisappear(30);

			click("Configuration.RateCode.restrictionsPopNew");
			waitForSpinnerToDisappear(30);
			textBox("Configuration.RateCode.RestrictionsStartDate", configMap.get("BEGIN_DATE"));
			Utils.tabKey("Configuration.RateCode.RestrictionsStartDate");
			textBox("Configuration.RateCode.RestrictionsEndDate", configMap.get("END_DATE"));
			Utils.tabKey("Configuration.RateCode.RestrictionsEndDate");
			Utils.selectBy("Configuration.RateCode.NewRestrictionType", "text", "Closed");
			waitForSpinnerToDisappear(30);
			click("Configuration.RateCode.btnSave");
			waitForSpinnerToDisappear(30);

			click("Configuration.RateCode.Restrictions");
			waitForSpinnerToDisappear(30);
			click("Configuration.RateCode.ChangesLog");
			waitForSpinnerToDisappear(30);

			click("Configuration.RateCode.ChangesLogBeginDate");
			Utils.tabKey("Configuration.RateCode.ChangesLogBeginDate");

			click("Configuration.RateCode.SearchRestrictions");
			waitForSpinnerToDisappear(30);

			List<WebElement> ab = Utils.elements("Configuration.RateCode.ChangesLogTable");
			System.out.println("no of rows" + ab.size());
			if(ab.size()>0) {
				System.out.println("Restrictions are created for the rate code");
				logger.log(LogStatus.PASS, "Restrictions are created for the rate code");
			}
			else {
				System.out.println("Restrictions are created for the rate code");
				logger.log(LogStatus.FAIL, "Restrictions are not created for the rate code");
			}

			click("Configuration.RateCode.PopupRestrictionClose");
			waitForSpinnerToDisappear(20);


		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Rate Code Restriction  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot

						(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Rate Code Restriction :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver,ScriptName)));
			tearDown();
			throw (e);

		}
	}*/


	/*******************************************************************
    - Description: To create a restricted rate code
	- Input:Rate Code Name , description , start date , end date , pricing , display set , room type
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void createRateRestriction(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		System.out.println("Map: " + configMap);

		try {

			ConfigPage.createRateCode(configMap);

			click("Configuration.RateCode.LinkMore");
			waitForSpinnerToDisappear(100);

			click("Configuration.RateCode.Edit");
			waitForSpinnerToDisappear(100);


			//Calling the getbusinessDate method to get the business date in enviornment  file.

			Utils.getBusinessDate();

			Map<String,String> envMap = new HashMap<String,String>(); 
			envMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EnvironmentDetails"),
					"Configuration","BusinessDate");

			String envname = ExcelUtils.getCellData(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "Set", "VALUE");

			String businessDate = ExcelUtils.getCellData(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "BusinessDate",envname);

			String endDate = Utils.AdddaysToBusinessdate(730);

			String endDateRest = Utils.AdddaysToBusinessdate(1);

			//Checking if the pricing schedules exist for the rate code or not , if not then create one

			click("Configuration.RateCode.linkPricingSchedules");
			waitForSpinnerToDisappear(100);

			List ll = Utils.elements("Configuration.RateCode.CheckPricing");

			if(ll.size()==0)
			{    
				System.out.println("in pricing");

				logger.log(LogStatus.INFO, "Pricing is not present for the rate code , adding the pricing now");

				click("Configuration.RateCode.PricingNew");
				waitForSpinnerToDisappear(100);

				textBox("Configuration.Pricing.StartDate", businessDate);
				Utils.tabKey("Configuration.Pricing.StartDate");
				waitForSpinnerToDisappear(100);

				textBox("Configuration.Pricing.EndDate", endDate);
				Utils.tabKey("Configuration.Pricing.EndDate");
				waitForSpinnerToDisappear(100);

				textBox("Configuration.Pricing.Adult", configMap.get("AMOUNT"));
				Utils.tabKey("Configuration.Pricing.Adult");
				waitForSpinnerToDisappear(100);

				textBox("Configuration.Pricing.RoomTypes", configMap.get("ROOM_TYPES"));
				Utils.tabKey("Configuration.Pricing.RoomTypes");
				waitForSpinnerToDisappear(100);

				click("Configuration.Pricing.Save");
				waitForSpinnerToDisappear(100);

				if(Utils.isExists("Configuration.RateCode.CheckPricing")) {
					logger.log(LogStatus.PASS, "Pricing is added to the rate code");
				}
				else {
					logger.log(LogStatus.FAIL, "Pricing is not added to the rate code");
				}
			}
			else {
				logger.log(LogStatus.PASS, "Pricing is already attached to the rate code");

			}

			click("Configuration.RateCode.PricingSchedule.btnClose");
			waitForSpinnerToDisappear(100);


			//Validating if the rate code has a display set or not

			click("Configuration.RateCode.LinkRateCodeDefination");
			waitForSpinnerToDisappear(100);

			String displaySet = Utils.getAttributeOfElement("Configuration.RateCode.DisplaySet", "data-ocformvalue", 30, "Presence");
			if(!displaySet.contains(configMap.get("DISPLAY_SET"))) {

				logger.log(LogStatus.INFO, "Display set is not present for the rate code , adding the display set now");

				click("Configuration.RateCode.LinkRateCodeDefinationEdit");
				waitForSpinnerToDisappear(100);

				textBox("Configuration.RateCode.DisplaySetText", configMap.get("DISPLAY_SET"));
				Utils.tabKey("Configuration.RateCode.DisplaySetText");
				waitForSpinnerToDisappear(100);	

				click("Configuration.RateCode.DisplaySetSave");
				waitForSpinnerToDisappear(100);	

				String displaySet1 = Utils.getAttributeOfElement("Configuration.RateCode.DisplaySet", "data-ocformvalue", 30, "Presence");

				if(displaySet1.contains(configMap.get("DISPLAY_SET"))) {

					logger.log(LogStatus.PASS, "Display set added for the rate code");
				}

				else {
					logger.log(LogStatus.FAIL, "Display set not added for the rate code");
				}
			}

			else {

				logger.log(LogStatus.PASS, "Display Set is already attached to the rate code");
			}

			click("Configuration.RateCode.DisplaySetClose");
			waitForSpinnerToDisappear(100);	


			//Clicking on creating restrictions
			click("Configuration.RateCode.linkRestrictions");
			waitForSpinnerToDisappear(150);

			//Checking the earlier created restrictions

			click("Configuration.RateCode.Restrictions");
			waitForSpinnerToDisappear(100);
			click("Configuration.RateCode.ChangesLog");
			waitForSpinnerToDisappear(100);

			click("Configuration.RateCode.ChangesLogBeginDate");
			Utils.tabKey("Configuration.RateCode.ChangesLogBeginDate");

			click("Configuration.RateCode.SearchRestrictions");
			waitForSpinnerToDisappear(100);

			List<WebElement> ab = Utils.elements("Configuration.RateCode.ChangesLogTable");
			System.out.println("no of rows" + ab.size());

			int oldRows = ab.size();

			click("Configuration.RateCode.PopupRestrictionClose");

			jsClick("Configuration.RateCode.restrictionsPopNew");
			waitForSpinnerToDisappear(100);
			textBox("Configuration.RateCode.RestrictionsStartDate", businessDate);
			Utils.tabKey("Configuration.RateCode.RestrictionsStartDate");
			textBox("Configuration.RateCode.RestrictionsEndDate", endDateRest);
			Utils.tabKey("Configuration.RateCode.RestrictionsEndDate");
			Utils.selectBy("Configuration.RateCode.NewRestrictionType", "text", "Closed");
			waitForSpinnerToDisappear(100);
			click("Configuration.RateCode.btnSave");
			waitForSpinnerToDisappear(100);

			click("Configuration.RateCode.Restrictions");
			waitForSpinnerToDisappear(100);
			click("Configuration.RateCode.ChangesLog");
			waitForSpinnerToDisappear(100);

			click("Configuration.RateCode.ChangesLogBeginDate");
			Utils.tabKey("Configuration.RateCode.ChangesLogBeginDate");

			click("Configuration.RateCode.SearchRestrictions");
			waitForSpinnerToDisappear(100);

			List<WebElement> ab1 = Utils.elements("Configuration.RateCode.ChangesLogTable");
			System.out.println("no of rows" + ab1.size());

			int newRows = ab1.size();

			if(newRows==oldRows+2) {
				System.out.println("Restrictions are created for the rate code");
				logger.log(LogStatus.PASS, "Restrictions are created for the rate code");
			}
			else {
				System.out.println("Restrictions are created for the rate code");
				logger.log(LogStatus.FAIL, "Restrictions are not created for the rate code");
			}

			click("Configuration.RateCode.PopupRestrictionClose");
			waitForSpinnerToDisappear(100);


		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot

						(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, 

					ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
		- Description: This method creates SetupStyle for Functions Space
		- Input: configMap- Test Data row for Create Owner
		- Output:
		- Author:Chittranjan
		- Date:12/26/2018
		- Revision History:0.1
	 ********************************************************************/
	public static void createSetUpStyle(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on EventType Button
			click("Configuration.FunctionSpaceMngt.SetUpStyle", 100, "clickable");
			waitForSpinnerToDisappear(50);

			//Entering the EventCode to search it
			List<WebElement> allSetUpStyle=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
			if (allSetUpStyle.size()==0){
				throw new Exception();
			}
			boolean elementExist=false;
			for (WebElement eachSetupStyle:allSetUpStyle ){
				if(eachSetupStyle.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("SetUpCode"))){
					elementExist=true;
					break;
				}
			}
			// Verifying if event already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for SetupStyle already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the SetupStyleCode
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", configMap.get("SetUpCode"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");

				// Entering the SetupDescription
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						configMap.get("SetUpDescription"), 0, "presence");

				waitForSpinnerToDisappear(30);

				// Entering the SetupStyle Sequence
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
						configMap.get("SetUpSequence"), 0, "presence");
				waitForSpinnerToDisappear(30);

				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);

				logger.log(LogStatus.PASS, "The configuration SetupStyle has been created :: Passed");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Setup Style  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create SetUpStyle not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}		

	/*******************************************************************
		- Description: setup printer as part of Configuration 
		- Input: REPORT_GROUP, REPORT_NAME
		- Output: 
		- Author: srikanth konda
		- Date:24/12/2018
		- Revision History:
		                - Change Date 
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By: 

	 ********************************************************************/


	public static void PrinterConfiguration(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			// Navigating to main menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");


			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");
			Utils.WebdriverWait(100, "Configuration.menu_Interfaces", "clickable");
			System.out.println("Selected Administration menu");
			logger.log(LogStatus.PASS, "Selected Administration Menu");
			waitForSpinnerToDisappear(30);

			// Navigating to Printer Configuration screen
			mouseHover("Configuration.menu_Interfaces",100,"clickable");
			waitForSpinnerToDisappear(30);
			click("Configuration.menu_Interfaces",100,"clickable");
			mouseHover("Configuration.menu_PrinterConfiguration",100,"clickable");
			jsClick("Configuration.menu_PrinterConfiguration",100,"presence");
			Thread.sleep(2000);

			if (driver.findElements(By.xpath("//span[text()='"+configMap.get("PrinterName")+"']")).size() > 0) {
				logger.log(LogStatus.PASS, "Printer : " + configMap.get("PrinterName") + " configuration exists");
				System.out.println("Printer : " + configMap.get("PrinterName") + " configuration exists");
			} else {
				// create new printer configuration
				mouseHover("Configuration.link_New",100,"presence");
				jsClick("Configuration.link_New",100,"presence");
				Utils.WebdriverWait(100, "Configuration.txtPrinter", "clickable");
				textBox("Configuration.txtPrinter", configMap.get("PrinterName"), 100, "presence");
				textBox("Configuration.txtPrinterLocation", configMap.get("PrinterLocation"), 100, "presence");
				Utils.Wait(1000);
				textBox("Configuration.txtDesc", configMap.get("Description"), 100, "presence");
				mouseHover("Configuration.btnAddAll",100,"presence");
				jsClick("Configuration.btnAddAll",100,"presence");
				mouseHover("Configuration.radio_Print",100,"presence");
				jsClick("Configuration.radio_Print",100,"presence");
				mouseHover("Configuration.btn_Save",100,"presence");
				jsClick("Configuration.btn_Save",100,"presence");
				waitForSpinnerToDisappear(30);
				//verify the new printer present in search results
				if (driver.findElements(By.xpath("//span[text()='"+configMap.get("PrinterName")+"']")).size() > 0) {
					logger.log(LogStatus.PASS, "Printer : " + configMap.get("PrinterName") + " configuration created successfully");
					System.out.println("Printer : " + configMap.get("PrinterName") + " configuration created successfully");
				} else {
					logger.log(LogStatus.PASS, "Printer : " + configMap.get("PrinterName") + " configuration not present in the search results");
					System.out.println("Printer : " + configMap.get("PrinterName") + " configuration not present in the search results");

				}

			}

		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Report Group  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Report Group not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}

	}

	/*******************************************************************
- Description: This method verifies the block status
- Input: configMap- Test Data row for Create block
- Output:
- Author: Vamsi
- Date:12/27/2018
- Revision History:0.1
	 ********************************************************************/
	public static void statusCheckBlockAdministration(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");
			waitForSpinnerToDisappear(30);
			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");
			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");
			// Clicking on Bookings Button
			click("Configuration.BookingMenu", 100, "clickable");
			// Clicking on Function Space Management Button
			click("Configuration.BookingMenu.BlockStatus");
			//		click("Configuration.BookingMenu.BlockStatus", 100, "clickable");
			// Clicking on EventType Button
			click("Configuration.BookingMenu.Statuscodes");
			//		click("Configuration.BookingMenu.Statuscodes", 100, "clickable");
			waitForSpinnerToDisappear(50);
			//Entering the EventCode to search it
			List<WebElement> allSetUpStyle=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
			if (allSetUpStyle.size()==0){
				throw new Exception();
			}
			boolean elementExist=false;
			for (WebElement eachSetupStyle:allSetUpStyle ){
				if(eachSetupStyle.getAttribute("data-occellvalue").equalsIgnoreCase("INQ")){
					elementExist=true;
					break;
				}
			}
			// Verifying if event already exists
			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for SetupStyle INQ already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);
				// Entering the SetupStyleCode
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", "INQ", 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");
				// Entering the SetupDescription
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						"INQUIRY", 0, "presence");
				waitForSpinnerToDisappear(30);
				// Entering the SetupStyle Sequence
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
						configMap.get("SetUpSequence"), 0, "presence");
				waitForSpinnerToDisappear(30);
				textBox("Configuration.BookingMenu.RoomStatusType ","INQUIRY", 0, "presence");
				waitForSpinnerToDisappear(30);
				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);
				logger.log(LogStatus.PASS, "The configuration SetupStyle has been created :: Passed");
			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Setup Style  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create SetUpStyle not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}	


	/*******************************************************************
		- Description: This method checks and Enables the Manage Blocks and Edit/New block in Role manager
		- Input: configMap- Test Data row for CreateBlock
		- Output:
		- Author:Vamsi
		- Date:12/27/2018
		- Revision History:0.1
	 ********************************************************************/

	public static void blocksConfigRoleManagerSetUp(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);
		Map<String, String> configMapEvn = new HashMap<String, String>();
		configMapEvn = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		configMapEvn = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configMapEvn.get("Set"));

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Role manager
			click("Configuration.RoleManagerMenu",100, "clickable");

			// Navigating to Manage Users
			click("Configuration.RoleManagerMenu.ManageUsers", 100, "clickable");

			// Entering the User ID
			textBox("Configuration.RoleManagerMenu.UserId", configMapEvn.get("UserName"), 100, "clickable");

			// click on Search Button
			click("Configuration.RoleManagerMenu.SearchButton", 100, "clickable");

			// Validating if User is loaded
			Assert.assertTrue(getText("Configuration.RoleManagerMenu.SearchedUserId", 100, "presence").equalsIgnoreCase(configMapEvn.get("UserName")));

			// Clicking on Action Button
			click("Configuration.RoleManagerMenu.ActionButton", 100, "presence");

			// Clicking on Edit Button
			click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
			waitForSpinnerToDisappear(20);
			// Verifying Owners code
			java.util.List<WebElement> rolecode = driver.findElements(By.xpath("//*[@data-ocid='TXT_CNTNT_ROLECODE']"));
			Iterator<WebElement> i = rolecode.iterator();
			String roleCOde = null;
			while(i.hasNext()) {
				WebElement anchor = i.next();
				if(anchor.getText().equalsIgnoreCase("ALLTASKS") || anchor.getText().equalsIgnoreCase("QAALL")) {
					roleCOde = anchor.getText();
					System.out.println("Role found");
					break;
				}
			}
			java.util.List<WebElement> manageChainRole = driver.findElements(By.xpath("//*[@data-ocid='MITEM_ODEC_DRPMN_MB_ITM']/div/table/tbody/tr/td/a"));
			Iterator<WebElement> im = manageChainRole.iterator();
			while(im.hasNext()) {
				WebElement anchor = im.next();
				if(anchor.getText().equalsIgnoreCase("Manage Chain Roles")) {
					anchor.click();
					System.out.println("Manage Chain Roles found and selected");
					break;
				}
			}

			waitForSpinnerToDisappear(20);
			textBox("Configuration.RoleManagerMenu.edtRoleOfUser", roleCOde, 100, "clickable");
			click("Configuration.RoleManagerMenu.roleSearchOfUser", 100, "presence");
			waitForSpinnerToDisappear(20);
			String searchedRoleResult=getAttributeOfElement("Configuration.RoleManagerMenu.SearchedRoleResult", "value", 100, "presence");
			click("Configuration.RoleManagerMenu.SearchedRoleActionBtn", 100, "presence");
			waitForSpinnerToDisappear(20);
			click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
			waitForSpinnerToDisappear(20);
			textBox("Configuration.RoleManagerMenu.edtSearchblock", "manage block", 100, "clickable");
			click("Configuration.RoleManagerMenu.btnLinkFilter", 100, "presence");

			waitForSpinnerToDisappear(20);

			boolean flag = driver.findElement(By.xpath("//*[contains(@data-ocid,'MANAGEBLOCKSMANAGEBLOCKS')]")).isEnabled();
			if (flag){
				System.out.println("Manage blocks checkbox is already checked "+flag);
				logger.log(LogStatus.PASS, "Manage blocks checkbox is already checked "+flag);
			}else{
				System.out.println("Manage blocks check box is enabled, "+flag);
				driver.findElement(By.xpath("//*[contains(@data-ocid,'MANAGEBLOCKSMANAGEBLOCKS')]")).click();
				waitForSpinnerToDisappear(20);
				logger.log(LogStatus.PASS, "Manage blocks check box is enabled, "+flag);
			}
			textBox("Configuration.RoleManagerMenu.edtSearchblock", "NEW/EDIT BLOCK", 100, "clickable");
			click("Configuration.RoleManagerMenu.btnLinkFilter", 100, "presence");

			waitForSpinnerToDisappear(20);
			boolean flag1 = driver.findElement(By.xpath("//*[contains(@data-ocid,'CHK_CNTNT_NEW/EDITBLOCK')]")).isEnabled();
			if (flag1){
				System.out.println("NEW/EDIT blocks checkbox is already checked "+flag1);
				logger.log(LogStatus.PASS, "NEW/EDIT blocks checkbox is already checked "+flag1);
			}else{
				System.out.println("NEW/EDIT blocks check box is enabled, "+flag1);
				driver.findElement(By.xpath("//*[contains(@data-ocid,'CHK_CNTNT_NEW/EDITBLOCK')]")).click();
				waitForSpinnerToDisappear(20);
				logger.log(LogStatus.PASS, "NEW/EDIT blocks check box is enabled, "+flag1);
			}
			if (flag && flag1)
			{
				System.out.println("Roles are set for the property of user");
			}else
			{
				click("Configuration.btn_Save", 100, "presence");
				System.out.println("Roles are set for the property of user");
			}
			waitForSpinnerToDisappear(20);

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create marketGroup  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "marketGroup not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}

	}


	/*******************************************************************
- Description: This method verifies the block status
- Input: configMap- Test Data row for Create block
- Output:
- Author: Vamsi
- Date:12/27/2018
- Revision History:0.1
	 ********************************************************************/
public static void blocksStatusCodesFlowsConfig(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");
			waitForSpinnerToDisappear(30);
			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");
			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");
			
			
			statusCodeCreateSetSequence(configMap, "CAN", "31", "CANCEL");
			statusCodeCreateSetSequence(configMap, "DEF", "20", "DEFINITE");
			statusCodeCreateSetSequence(configMap, "TEN", "15", "TENTATIVE");
			statusCodeCreateSetSequence(configMap, "INQ", "5", "INQUIRY");
//			
			// Clicking on Bookings Button
			click("Configuration.BookingMenu", 100, "clickable");
			// Clicking on Function Space Management Button
			click("Configuration.BookingMenu.BlockStatus");
			//		click("Configuration.BookingMenu.BlockStatus", 100, "clickable");
			// Clicking on EventType Button
			click("Configuration.BookingMenu.Statuscodes");
			//		click("Configuration.BookingMenu.Statuscodes", 100, "clickable");
			waitForSpinnerToDisappear(50);
			
			//Entering the EventCode to search it
			List<WebElement> allSetUpStyle=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
			if (allSetUpStyle.size()==0){
				throw new Exception();
			}
			
			boolean elementExist=false;
			boolean elementStatusFlowTEN=false;
			for (WebElement eachSetupStyle:allSetUpStyle ){
				if(eachSetupStyle.getAttribute("data-occellvalue").equalsIgnoreCase("INQ")){
					elementExist=true;
					textBox("Configuration.BookingMenu.StatuscodeSearch", "INQ", 0,"presence");
					Utils.tabKey("Configuration.BookingMenu.StatuscodeSearch");
					waitForSpinnerToDisappear(40);
					click("Configuration.RoleManagerMenu.SearchButton", 100, "presence");
					waitForSpinnerToDisappear(20);
					click("Configuration.BookingMenu.edtOptionsSingleSearch", 100, "presence");
					waitForSpinnerToDisappear(20);
					click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
					waitForSpinnerToDisappear(20);
					if (!isSelected("Configuration.BookingMenu.chkStartingStatus", "StartingStatusCheckBoxINQ")){
						click("Configuration.BookingMenu.chkStartingStatus", 100, "presence");
						waitForSpinnerToDisappear(20);
					}
					click("Configuration.btnSave", 100, "presence");
					waitForSpinnerToDisappear(20);
					break;
				}
			}
			click("Configuration.BookingMenu", 100, "clickable");
			// Clicking on Function Space Management Button
			click("Configuration.BookingMenu.BlockStatus");
			//		click("Configuration.BookingMenu.BlockStatus", 100, "clickable");
			// Clicking on EventType Button
			click("Configuration.BookingMenu.Statuscodes");
			waitForSpinnerToDisappear(40);
			List<WebElement> allSetUpStyles=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
			if (allSetUpStyles.size()==0){
				throw new Exception();
			}
			for (WebElement eachSetupStyle:allSetUpStyles ){
				if(eachSetupStyle.getAttribute("data-occellvalue").equalsIgnoreCase("TEN")){
					elementStatusFlowTEN=true;
					textBox("Configuration.BookingMenu.StatuscodeSearch", "TEN", 0,"presence");
					Utils.tabKey("Configuration.BookingMenu.StatuscodeSearch");
					waitForSpinnerToDisappear(40);
					click("Configuration.RoleManagerMenu.SearchButton", 100, "presence");
					waitForSpinnerToDisappear(20);
					click("Configuration.BookingMenu.edtOptionsSingleSearch", 100, "presence");
					waitForSpinnerToDisappear(20);
					click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
					waitForSpinnerToDisappear(20);
					if (!isSelected("Configuration.BookingMenu.chkStartingStatus", "StartingStatusCheckBoxTEN")){
						click("Configuration.BookingMenu.chkStartingStatus", 100, "presence");
						waitForSpinnerToDisappear(20);
					}
					click("Configuration.btnSave", 100, "presence");
					waitForSpinnerToDisappear(20);
					break;
				}
			}
			// Verifying if event already exists
			if (elementStatusFlowTEN) {
				logger.log(LogStatus.PASS, "The configuration for SetupStyle TEN already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);
				// Entering the SetupStyleCode
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", "TEN", 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");
				// Entering the SetupDescription
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						"TENTATIVE", 0, "presence");
				waitForSpinnerToDisappear(30);
				// Entering the SetupStyle Sequence
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
						configMap.get("SetUpSequence")+3, 0, "presence");
				waitForSpinnerToDisappear(30);
				textBox("Configuration.BookingMenu.RoomStatusType ","TENTATIVE", 0, "presence");
				waitForSpinnerToDisappear(30);
				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);
				logger.log(LogStatus.PASS, "The configuration SetupStyle has been created :: Passed");
			}
			checkStatusCodeFlow(configMap, "TEN", "DEF - Definite");
			checkStatusCodeFlow(configMap, "INQ", "TEN - Tentative");
			checkStatusCodeFlow(configMap, "DEF", "CAN - Cancel");
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Setup Style  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create SetUpStyle not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}
	public static void statusCodeCreateSetSequence (HashMap<String, String> configMap, String StatustoCheck,String sequenceNumber, String StatusName) throws Exception {
		
		click("Configuration.BookingMenu", 100, "clickable");
		// Clicking on Function Space Management Button
		click("Configuration.BookingMenu.BlockStatus");
		// Clicking on EventType Button
		click("Configuration.BookingMenu.Statuscodes");
		//		click("Configuration.BookingMenu.Statuscodes", 100, "clickable");
		waitForSpinnerToDisappear(50);
		boolean StatusCodeSequesnce=false;
		List<WebElement> allSetUpStyleVerifyStatus=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
		if (allSetUpStyleVerifyStatus.size()==0){
			throw new Exception();
		}
		for (WebElement eachSetupStyle:allSetUpStyleVerifyStatus ){
			if(eachSetupStyle.getAttribute("data-occellvalue").equalsIgnoreCase(StatustoCheck)){
				textBox("Configuration.BookingMenu.StatuscodeSearch", StatustoCheck, 0,"presence");
				Utils.tabKey("Configuration.BookingMenu.StatuscodeSearch");
				waitForSpinnerToDisappear(40);
				click("Configuration.RoleManagerMenu.SearchButton", 100, "presence");
				waitForSpinnerToDisappear(20);
				click("Configuration.BookingMenu.edtOptionsSingleSearch", 100, "presence");
				waitForSpinnerToDisappear(20);
				click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
				waitForSpinnerToDisappear(20);
				textBox("Configuration.txt_Sequence", sequenceNumber, 0,"presence");
				click("Configuration.btnSave", 100, "presence");
				waitForSpinnerToDisappear(20);
				StatusCodeSequesnce=true;
				break;
			}
		}
		if(!StatusCodeSequesnce){
			click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
			waitForSpinnerToDisappear(40);
			// Entering the SetupStyleCode
			textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", StatustoCheck, 0,
					"presence");
			Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");
			// Entering the SetupDescription
			textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description", StatusName, 0,
					"presence");
			Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Description");
			waitForSpinnerToDisappear(30);
			// Entering the SetupStyle Sequence
			textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
					sequenceNumber, 0, "presence");
			Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence");
			waitForSpinnerToDisappear(30);
			if (StatusName.equalsIgnoreCase("CANCEL")){
				textBox("Configuration.BookingMenu.RoomStatusType","DED INV", 0, "presence");
				Utils.tabKey("Configuration.BookingMenu.RoomStatusType");
				waitForSpinnerToDisappear(30);
			}
			if (StatusName.equalsIgnoreCase("DEFINITE") || StatusName.equalsIgnoreCase("TENTATIVE") || StatusName.equalsIgnoreCase("INQUIRY")){
				textBox("Configuration.BookingMenu.RoomStatusType","DED INV", 0, "presence");
				Utils.tabKey("Configuration.BookingMenu.RoomStatusType");
				waitForSpinnerToDisappear(30);
				textBox("Configuration.BookingMenu.txtCateringStatusType","DED INV", 0, "presence");
				Utils.tabKey("Configuration.BookingMenu.txtCateringStatusType");
				waitForSpinnerToDisappear(30);
				if (StatusName.equalsIgnoreCase("TENTATIVE") || StatusName.equalsIgnoreCase("INQUIRY")){
				if (!isSelected("Configuration.BookingMenu.chkStartingStatus", "StartingStatusCheckBoxTEN")){
					click("Configuration.BookingMenu.chkStartingStatus", 100, "presence");
					waitForSpinnerToDisappear(20);
				}
				}
			}
			
			if ( isExists("Configuration.BookingMenu.txtReasonType")){
			if ( isEnabled("Configuration.BookingMenu.txtReasonType", "REASON TYPE")){
				textBox("Configuration.BookingMenu.txtReasonType",StatusName, 0, "presence");
				Utils.tabKey("Configuration.BookingMenu.txtReasonType");
				waitForSpinnerToDisappear(30);
			}
			}
			click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "The configuration SetupStyle has been created :: Passed");
		}
	}
	public static void checkStatusCodeFlow (HashMap<String, String> configMap, String StatustoCheck,String NextStatusAvailable) throws Exception {
		click("Configuration.BookingMenu", 100, "clickable");
		// Clicking on Function Space Management Button
		click("Configuration.BookingMenu.BlockStatus");
		//		click("Configuration.BookingMenu.BlockStatus", 100, "clickable");
		// Clicking on EventType Button
		click("Configuration.BookingMenu.StatusCodeFlow");
		//		click("Configuration.BookingMenu.Statuscodes", 100, "clickable");
		waitForSpinnerToDisappear(50);
		textBox("Configuration.BookingMenu.StatuscodeSearch", StatustoCheck, 0,"presence");
		Utils.tabKey("Configuration.BookingMenu.StatuscodeSearch");
		waitForSpinnerToDisappear(40);
		click("Configuration.RoleManagerMenu.SearchButton", 100, "presence");
		waitForSpinnerToDisappear(20);
		click("Configuration.BookingMenu.edtOptionsSingleSearch", 100, "presence");
		waitForSpinnerToDisappear(20);
		click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
		waitForSpinnerToDisappear(20);
		java.util.List<WebElement> SelectednextAvailableCodes  = Utils.elements("Configuration.BookingMenu.SelectednextAvailableCodes"); 
		System.out.println("No of groups are : " + SelectednextAvailableCodes.size());
		boolean SelectednextAvailableCodesStat = false;
		for (WebElement eachGroup:SelectednextAvailableCodes){
			if(eachGroup.getText().equalsIgnoreCase(NextStatusAvailable)){
				//((JavascriptExecutor) Utils.driver).executeScript("arguments[0].scrollIntoView(true);", eachGroup);
				logger.log(LogStatus.PASS, "The configuration SetupStyle has been created for "+NextStatusAvailable+"  :: Passed");
				System.out.println("The configuration SetupStyle has been created for "+NextStatusAvailable+"  :: Passed");
				SelectednextAvailableCodesStat = true;
				click("Configuration.btnSave", 100, "presence");
				waitForSpinnerToDisappear(20);
				break;
			}
		}
		if (!SelectednextAvailableCodesStat){
		java.util.List<WebElement> nextAvailableCodes  = Utils.elements("Configuration.BookingMenu.nextAvailableCodes"); 
		System.out.println("No of groups are : " + nextAvailableCodes.size());
		
		for (WebElement eachGroup:nextAvailableCodes){
			if(eachGroup.getText().equalsIgnoreCase(NextStatusAvailable)){
				//((JavascriptExecutor) Utils.driver).executeScript("arguments[0].scrollIntoView(true);", eachGroup);
				eachGroup.click();
				Utils.waitForSpinnerToDisappear(20);
				click("Configuration.BookingMenu.dragArrowSelectNextAvailableCode", 100, "presence");
				waitForSpinnerToDisappear(20);
				click("Configuration.btnSave", 100, "presence");
				logger.log(LogStatus.PASS, "The configuration SetupStyle has been created for "+NextStatusAvailable+"  :: Passed");
				System.out.println("The configuration SetupStyle has been created for "+NextStatusAvailable+"  :: Passed");
				waitForSpinnerToDisappear(20);
				break;
			}
		}
		}
	}
	
	/*******************************************************************
- Description: This method creates SetupStyle for Functions Space
- Input: configMap- Test Data row for Create Owner
- Output:
- Author:Chittranjan
- Date:12/26/2018
- Revision History:0.1
	 ********************************************************************/

	public static void createFunctionSpaceTypes(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.FunctionSpaceType", 100, "clickable");
			waitForSpinnerToDisappear(50);
			boolean elementExist=false;
			//Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
			if (allFunctionSpaceCode.size()==0){

			}else{			
				for (WebElement eachFunctionCode:allFunctionSpaceCode ){
					if(eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("SetUpCode"))){
						elementExist=true;
						break;
					}
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for Function Space Type already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", configMap.get("SetUpCode"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");

				// Entering the Description
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						configMap.get("SetUpDescription"), 0, "presence");

				waitForSpinnerToDisappear(30);

				// Entering the Sequence
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
						configMap.get("SetUpSequence"), 0, "presence");
				waitForSpinnerToDisappear(30);

				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);

				logger.log(LogStatus.PASS, "The configuration SetupStyle has been created :: Passed");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create FunctionSpaceType  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Function SpaceType not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}

	/*public static void createFunctionSpaceTypes(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.FunctionSpaceType", 100, "clickable");
			waitForSpinnerToDisappear(50);

			//Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode=elements("Configuration.FunctionSpaceMngt.EventType.EventCodes");
			if (allFunctionSpaceCode.size()==0){
				throw new Exception();
			}
			boolean elementExist=false;
			for (WebElement eachFunctionCode:allFunctionSpaceCode ){
				if(eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("spaceTypeCode"))){
					elementExist=true;
					break;
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for Function Space Type already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", configMap.get("SetUpCode"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");

				// Entering the Description
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						configMap.get("SetUpDescription"), 0, "presence");

				waitForSpinnerToDisappear(30);

				// Entering the Sequence
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
						configMap.get("SetUpSequence"), 0, "presence");
				waitForSpinnerToDisappear(30);

				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);

				logger.log(LogStatus.PASS, "The configuration SetupStyle has been created :: Passed");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create FunctionSpaceType  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Function SpaceType not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}*/


	/*******************************************************************
- Description: This method creates Locations
- Input: configMap- Test Data row for Create Location
- Output:
- Author:Chittranjan
- Date:12/26/2018
- Revision History:0.1
	 ********************************************************************/

	public static void createLocation(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.Locations", 100, "clickable");
			waitForSpinnerToDisappear(50);
			boolean elementExist=false;
			//Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode=elements("Configuration.FunctionSpaceMngt.Locations.LocationCode");
			if (allFunctionSpaceCode.size()==0){

			}else{			
				for (WebElement eachFunctionCode:allFunctionSpaceCode ){
					if(eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("spaceTypeCode"))){
						elementExist=true;
						break;
					}
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for Location already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", configMap.get("SetUpCode"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");

				// Entering the Description
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						configMap.get("SetUpDescription"), 0, "presence");

				waitForSpinnerToDisappear(30);


				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);

				Assert.assertTrue(element("Configuration.FunctionSpaceMngt.SetUpStyle.Code").isDisplayed());

				logger.log(LogStatus.PASS, "The configuration Location has been created :: Passed");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Location  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Location not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}

	/*public static void createLocation(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.Locations", 100, "clickable");
			waitForSpinnerToDisappear(50);

			//Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode=elements("Configuration.FunctionSpaceMngt.Locations.LocationCode");
			if (allFunctionSpaceCode.size()==0){
				throw new Exception();
			}
			boolean elementExist=false;
			for (WebElement eachFunctionCode:allFunctionSpaceCode ){
				if(eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("spaceTypeCode"))){
					elementExist=true;
					break;
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for Location already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", configMap.get("SetUpCode"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");

				// Entering the Description
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						configMap.get("SetUpDescription"), 0, "presence");

				waitForSpinnerToDisappear(30);


				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);

				Assert.assertTrue(element("Configuration.FunctionSpaceMngt.SetUpStyle.Code").isDisplayed());

				logger.log(LogStatus.PASS, "The configuration Location has been created :: Passed");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Location  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Location not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}*/

	/*******************************************************************
- Description: This method creates Rental Codes
- Input: configMap- Test Data row for Create Rental Code
- Output:
- Author:Chittranjan
- Date:12/26/2018
- Revision History:0.1
	 ********************************************************************/


	public static void createRentalCode(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.RentalCodes", 100, "clickable");
			waitForSpinnerToDisappear(50);
			boolean elementExist=false;
			//Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode=elements("Configuration.FunctionSpaceMngt.Locations.RentalCodes");
			if (allFunctionSpaceCode.size()==0){

			}else{			
				for (WebElement eachFunctionCode:allFunctionSpaceCode ){
					if(eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("spaceTypeCode"))){
						elementExist=true;
						break;
					}
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for Rental Code already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", configMap.get("SetUpCode"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");

				// Entering the Description
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						configMap.get("SetUpDescription"), 0, "presence");

				waitForSpinnerToDisappear(30);

				// Entering the Sequence
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
						configMap.get("SetUpSequence"), 0, "presence");
				waitForSpinnerToDisappear(30);


				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);

				Assert.assertTrue(element("Configuration.FunctionSpaceMngt.SetUpStyle.Code").isDisplayed());

				logger.log(LogStatus.PASS, "The configuration Location has been created :: Passed");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Rental Code  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Rental Code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}


	/*public static void createRentalCode(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.RentalCodes", 100, "clickable");
			waitForSpinnerToDisappear(50);

			//Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode=elements("Configuration.FunctionSpaceMngt.Locations.RentalCodes");
			if (allFunctionSpaceCode.size()==0){
				throw new Exception();
			}
			boolean elementExist=false;
			for (WebElement eachFunctionCode:allFunctionSpaceCode ){
				if(eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("spaceTypeCode"))){
					elementExist=true;
					break;
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS, "The configuration for Rental Code already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new SetUP Style
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Code", configMap.get("SetUpCode"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.SetUpStyle.Code");

				// Entering the Description
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Description",
						configMap.get("SetUpDescription"), 0, "presence");

				waitForSpinnerToDisappear(30);

				// Entering the Sequence
				textBox("Configuration.FunctionSpaceMngt.SetUpStyle.Sequence",
						configMap.get("SetUpSequence"), 0, "presence");
				waitForSpinnerToDisappear(30);


				click("Configuration.FunctionSpaceMngt.SetUpStyle.Save",0,"clickable");
				waitForSpinnerToDisappear(50);

				Assert.assertTrue(element("Configuration.FunctionSpaceMngt.SetUpStyle.Code").isDisplayed());

				logger.log(LogStatus.PASS, "The configuration Location has been created :: Passed");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Rental Code  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Rental Code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}*/

	/*******************************************************************
- Description: This method creates Function Space
- Input: configMap- Test Data row for Create Function Space
- Output:
- Author:Chittranjan
- Date:12/26/2018
- Revision History:0.1
	 ********************************************************************/

	public static void createFunctionSpace(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"),
					"OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.FunctionSpace", 100, "clickable");
			waitForSpinnerToDisappear(50);
			boolean elementExist = false;
			// Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode = elements(
					"Configuration.FunctionSpaceMngt.Locations.FunctionSpaceName");
			if (allFunctionSpaceCode.size() == 0) {

			}else{			
				for (WebElement eachFunctionCode : allFunctionSpaceCode) {
					if (eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("SpaceCode"))) {
						elementExist = true;
						break;
					}
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS,
						"The configuration for FunctionSpace already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new Function Space
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RoomType", configMap.get("RoomType"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RoomType");
				waitForSpinnerToDisappear(50);

				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceName", configMap.get("SpaceName"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceName");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.Room", configMap.get("Room"), 0, "presence");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceType", configMap.get("SpaceType"), 0,
						"clickable");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceType");
				waitForSpinnerToDisappear(50);
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MinimumCapacity",
						configMap.get("MinimumCapacity"), 0, "clickable");
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MaximumCapacity",
						configMap.get("MaximumCapacity"), 0, "clickable");
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.ShortName", configMap.get("ShortName"), 0,
						"presence");
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.Location", configMap.get("Location"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.Location");
				waitForSpinnerToDisappear(50);

				click("Configuration.FunctionSpaceMngt.FunctionSpace.Next", 0, "presence");
				waitForSpinnerToDisappear(50);

				List<WebElement> allSetUpStyleCodes = elements(
						"Configuration.FunctionSpaceMngt.FunctionSpace.allSetupStyleCode");
				boolean elementsetUpStyleExist = false;
				if (allSetUpStyleCodes.size() > 0) {
					for (WebElement eachRentalCode : allSetUpStyleCodes) {
						if (eachRentalCode.getAttribute("data-occellvalue")
								.equalsIgnoreCase(configMap.get("SetupStyleCode"))) {
							elementsetUpStyleExist = true;
							break;
						}
					}
				}
				// Verifying if element already exists

				if (elementsetUpStyleExist) {
					logger.log(LogStatus.PASS,
							"The configuration for SetUpStyle Code already exists hence breaking out of Execution :: Passed");
				} else {
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewSetupStyle", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SetupStyleCode",
							configMap.get("SetupStyleCode"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.SetupStyleCode");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MinimumCapacitySetUpStyle",
							configMap.get("MinimumCapacitySetUpStyle"), 0, "clickable");
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MaximumCapacitySetUpStyle",
							configMap.get("MaximumCapacitySetUpStyle"), 0, "clickable");
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SetupTime", configMap.get("SetUpTime"), 0,
							"presence");
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SetDownTime", configMap.get("SetDownTime"),
							0, "presence");
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				}
				List<WebElement> allRentalCodesRental = null;
				try{
					allRentalCodesRental = elements(
							"Configuration.FunctionSpaceMngt.Locations.RentalCodesCustom");
				}catch(Exception e){

				}
				boolean elementRentalExist = false;
				if (allRentalCodesRental.size() > 0) {
					for (WebElement eachRentalCode : allRentalCodesRental) {
						if (eachRentalCode.getAttribute("data-occellvalue")
								.equalsIgnoreCase(configMap.get("Rentalcode"))) {
							elementRentalExist = true;
							break;
						}
					}
				}
				// Verifying if element already exists

				if (elementRentalExist) {
					logger.log(LogStatus.PASS,
							"The configuration for Rental Code type Rental already exists hence breaking out of Execution :: Passed");
				} else {
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewRentalCode", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry",
							configMap.get("Rentalcode"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType", configMap.get("RevenueType"),
							0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount", configMap.get("RentalAmount"),
							0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				}

				List<WebElement> allRentalCodesCustom = elements(
						"Configuration.FunctionSpaceMngt.Locations.RentalCodesCustom");
				boolean elementCustomExist = false;
				int rowCustom = 0;
				if (allRentalCodesCustom.size() > 0) {
					for (WebElement eachCustomCode : allRentalCodesCustom) {
						if (eachCustomCode.getAttribute("data-occellvalue")
								.equalsIgnoreCase(configMap.get("RentalcodeCustom"))) {
							rowCustom = Integer.parseInt(eachCustomCode.getAttribute("data-ocid").split("_")[0]);
							elementCustomExist = true;
							break;
						}
					}
				}
				// Verifying if element already exists

				if (elementCustomExist) {
					driver.findElement(By.xpath("//*[@data-ocid='" + rowCustom + "_C8']")).click();
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Delete", 0, "presence");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.ButtonDelete", 0, "presence");
					waitForSpinnerToDisappear(50);
					Utils.scroll("up");
					Wait(4000);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewRentalCode", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry",
							configMap.get("RentalcodeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType",
							configMap.get("RevenueTypeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				} else {
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewRentalCode", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry",
							configMap.get("RentalcodeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType",
							configMap.get("RevenueTypeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount", configMap.get("RentalAmount"),
							0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				}
				click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
				waitForSpinnerToDisappear(50);
				List<WebElement> allFunctionSpaceCreated = elements(
						"Configuration.FunctionSpaceMngt.Locations.FunctionSpaceName");
				if (allFunctionSpaceCreated.size() == 0) {
					throw new Exception();
				}
				boolean elementNewSpaceExist = false;
				for (WebElement eachFunctionCode : allFunctionSpaceCreated) {
					if (eachFunctionCode.getAttribute("data-occellvalue")
							.equalsIgnoreCase(configMap.get("SpaceCode"))) {
						elementNewSpaceExist = true;
						break;
					}
				}
				// Verifying if element already exists

				if (elementNewSpaceExist) {
					logger.log(LogStatus.PASS,
							"The configuration for FunctionSpace has been created Successfully :: Passed");
				}
			}

		} catch (Exception e) {
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Function Space not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);
		}
	}

	/*public static void createFunctionSpace(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			waitForSpinnerToDisappear(30);

			// Clicking on Administration Button
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"),
					"OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Clicking on Inventory Button
			click("Configuration.AdminMenu.Inventory", 100, "clickable");

			// Clicking on Function Space Management Button
			click("Configuration.AdminMenu.Inventory.FunctionSpaceMngt", 100, "clickable");

			// Clicking on Function Space type
			click("Configuration.FunctionSpaceMngt.FunctionSpace", 100, "clickable");
			waitForSpinnerToDisappear(50);

			// Entering the EventCode to search it
			List<WebElement> allFunctionSpaceCode = elements(
					"Configuration.FunctionSpaceMngt.Locations.FunctionSpaceName");
			if (allFunctionSpaceCode.size() == 0) {
				throw new Exception();
			}
			boolean elementExist = false;
			for (WebElement eachFunctionCode : allFunctionSpaceCode) {
				if (eachFunctionCode.getAttribute("data-occellvalue").equalsIgnoreCase(configMap.get("SpaceCode"))) {
					elementExist = true;
					break;
				}
			}
			// Verifying if element already exists

			if (elementExist) {
				logger.log(LogStatus.PASS,
						"The configuration for FunctionSpace already exists hence breaking out of Execution :: Passed");
			} else {
				// Create a new Function Space
				click("Configuration.FunctionSpaceMngt.EventType.NewEvent", 100, "presence");
				waitForSpinnerToDisappear(40);

				// Entering the Code
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RoomType", configMap.get("RoomType"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RoomType");
				waitForSpinnerToDisappear(50);

				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceName", configMap.get("SpaceName"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceName");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.Room", configMap.get("Room"), 0, "presence");
				waitForSpinnerToDisappear(30);

				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceType", configMap.get("SpaceType"), 0,
						"clickable");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.SpaceType");
				waitForSpinnerToDisappear(50);
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MinimumCapacity",
						configMap.get("MinimumCapacity"), 0, "clickable");
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MaximumCapacity",
						configMap.get("MaximumCapacity"), 0, "clickable");
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.ShortName", configMap.get("ShortName"), 0,
						"presence");
				textBox("Configuration.FunctionSpaceMngt.FunctionSpace.Location", configMap.get("Location"), 0,
						"presence");
				Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.Location");
				waitForSpinnerToDisappear(50);

				click("Configuration.FunctionSpaceMngt.FunctionSpace.Next", 0, "presence");
				waitForSpinnerToDisappear(50);

				List<WebElement> allSetUpStyleCodes = elements(
						"Configuration.FunctionSpaceMngt.FunctionSpace.allSetupStyleCode");
				boolean elementsetUpStyleExist = false;
				if (allSetUpStyleCodes.size() > 0) {
					for (WebElement eachRentalCode : allSetUpStyleCodes) {
						if (eachRentalCode.getAttribute("data-occellvalue")
								.equalsIgnoreCase(configMap.get("SetupStyleCode"))) {
							elementsetUpStyleExist = true;
							break;
						}
					}
				}
				// Verifying if element already exists

				if (elementsetUpStyleExist) {
					logger.log(LogStatus.PASS,
							"The configuration for SetUpStyle Code already exists hence breaking out of Execution :: Passed");
				} else {
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewSetupStyle", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SetupStyleCode",
							configMap.get("SetupStyleCode"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.SetupStyleCode");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MinimumCapacitySetUpStyle",
							configMap.get("MinimumCapacitySetUpStyle"), 0, "clickable");
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.MaximumCapacitySetUpStyle",
							configMap.get("MaximumCapacitySetUpStyle"), 0, "clickable");
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SetupTime", configMap.get("SetUpTime"), 0,
							"presence");
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.SetDownTime", configMap.get("SetDownTime"),
							0, "presence");
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				}

				List<WebElement> allRentalCodesRental = elements(
						"Configuration.FunctionSpaceMngt.Locations.RentalCodesCustom");
				boolean elementRentalExist = false;
				if (allRentalCodesRental.size() > 0) {
					for (WebElement eachRentalCode : allRentalCodesRental) {
						if (eachRentalCode.getAttribute("data-occellvalue")
								.equalsIgnoreCase(configMap.get("Rentalcode"))) {
							elementRentalExist = true;
							break;
						}
					}
				}
				// Verifying if element already exists

				if (elementRentalExist) {
					logger.log(LogStatus.PASS,
							"The configuration for Rental Code type Rental already exists hence breaking out of Execution :: Passed");
				} else {
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewRentalCode", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry",
							configMap.get("Rentalcode"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType", configMap.get("RevenueType"),
							0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount", configMap.get("RentalAmount"),
							0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				}

				List<WebElement> allRentalCodesCustom = elements(
						"Configuration.FunctionSpaceMngt.Locations.RentalCodesCustom");
				boolean elementCustomExist = false;
				int rowCustom = 0;
				if (allRentalCodesCustom.size() > 0) {
					for (WebElement eachCustomCode : allRentalCodesCustom) {
						if (eachCustomCode.getAttribute("data-occellvalue")
								.equalsIgnoreCase(configMap.get("RentalcodeCustom"))) {
							rowCustom = Integer.parseInt(eachCustomCode.getAttribute("data-ocid").split("_")[0]);
							elementCustomExist = true;
							break;
						}
					}
				}
				// Verifying if element already exists

				if (elementCustomExist) {
					driver.findElement(By.xpath("//*[@data-ocid='" + rowCustom + "_C8']")).click();
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Delete", 0, "presence");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.ButtonDelete", 0, "presence");
					waitForSpinnerToDisappear(50);
					Utils.scroll("up");
					Wait(4000);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewRentalCode", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry",
							configMap.get("RentalcodeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType",
							configMap.get("RevenueTypeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				} else {
					click("Configuration.FunctionSpaceMngt.FunctionSpace.NewRentalCode", 0, "presence");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry",
							configMap.get("RentalcodeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalCodeEntry");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType",
							configMap.get("RevenueTypeCustom"), 0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RevenueType");
					waitForSpinnerToDisappear(50);
					textBox("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount", configMap.get("RentalAmount"),
							0, "presence");
					Utils.tabKey("Configuration.FunctionSpaceMngt.FunctionSpace.RentalAmount");
					waitForSpinnerToDisappear(50);
					click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
					waitForSpinnerToDisappear(50);
				}
				click("Configuration.FunctionSpaceMngt.FunctionSpace.Save", 0, "presence");
				waitForSpinnerToDisappear(50);
				List<WebElement> allFunctionSpaceCreated = elements(
						"Configuration.FunctionSpaceMngt.Locations.FunctionSpaceName");
				if (allFunctionSpaceCreated.size() == 0) {
					throw new Exception();
				}
				boolean elementNewSpaceExist = false;
				for (WebElement eachFunctionCode : allFunctionSpaceCreated) {
					if (eachFunctionCode.getAttribute("data-occellvalue")
							.equalsIgnoreCase(configMap.get("SpaceCode"))) {
						elementNewSpaceExist = true;
						break;
					}
				}
				// Verifying if element already exists

				if (elementNewSpaceExist) {
					logger.log(LogStatus.PASS,
							"The configuration for FunctionSpace has been created Successfully :: Passed");
				}
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Function Space  :: Failed " + alertText);
				logger.log(LogStatus.FAIL,
						extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create Function Space not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			tearDown();
			throw (e);
		}
	}*/

	/*******************************************************************
	-  Description: Creating preferences as the part of Configuration 
	- Input: Preference_Group,Preference_Code,DESCRIPTION

	- Output: Preference_Code
	- Author: swati
	- Date:01/03/2019
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: swati
	 ********************************************************************/


	public static void Preferences(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Navigating to Client relations menu
			click("Configuration.AdminMenu.ClientRelations", 100, "clickable");

			// Navigating to Profile Management menu
			click("Configuration.AdminMenu.Profilemanagement", 100, "clickable");

			// Navigating to Preferences
			click("Configuration.AdminMenu.Preferences", 100, "clickable");

			waitForSpinnerToDisappear(20);

			Assert.assertEquals(getText("Configuration.Preferences.Heading", 100, "presence"), "Preferences");
			logger.log(LogStatus.PASS, "Landed in Preferences Page");

			textBox("Configuration.Preferences.PreferenceGroup", configMap.get("Preference_Group"));
			Utils.tabKey("Configuration.Preferences.PreferenceGroup");
			logger.log(LogStatus.PASS, "Added preferences group");

			waitForSpinnerToDisappear(20);

			click("Configuration.Preferences.search");
			logger.log(LogStatus.PASS, "Clicked on Preferences code search");

			waitForSpinnerToDisappear(20);

			List<WebElement>  rows = Utils.elements("Configuration.Preferences.table"); 
			System.out.println("No of rows are : " + rows.size());

			Boolean flag = false;
			if(rows.size() > 0)
			{

				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.Preference.PreferencesCodetable", configMap.get("Preference_Code"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Preference code "+configMap.get("Preference_Code")+"is already available in the property");
					logger.log(LogStatus.PASS, "The Preference code "+configMap.get("Preference_Code")+"is already available in the property");

				}

			}	

			if(!flag)
			{

				//Navigate to Template screen
				mouseHover("Configuration.tab_Template",100,"presence");
				jsClick("Configuration.tab_Template",100,"presence");
				waitForSpinnerToDisappear(20);

				textBox("Configuration.Preferences.PreferenceGroup", configMap.get("Preference_Group"));
				Utils.tabKey("Configuration.Preferences.PreferenceGroup");
				waitForSpinnerToDisappear(20);

				//Select Search
				mouseHover("Configuration.MarketGroup.TemplateSearchButton",100,"presence");
				jsClick("Configuration.MarketGroup.TemplateSearchButton",100,"presence");

				waitForSpinnerToDisappear(20);

				//Checking if the preference code is present in the template or not
				flag = Utils.ValidateGridData("Configuration.Preference.PreferencesTemplateTable", configMap.get("Preference_Code"));
				System.out.println("flag2" + flag);

				if(!flag)
				{

					logger.log(LogStatus.PASS, "Preference code is not present in Template , Creating a new one");

					//Create the new template
					jsClick("Configuration.MarketGroup.TemplateNew");
					waitForSpinnerToDisappear(20);

					Assert.assertEquals(getText("Configuration.MarketGroup.TemplateDescriptionLable", 100, "presence"), "*Description");	
					logger.log(LogStatus.PASS, "Landed in new template creation page");

					//Provide preference code Text
					textBox("Configuration.Preferences.PreferenceGroupTemplate", configMap.get("Preference_Group"));
					Utils.tabKey("Configuration.Preferences.PreferenceGroupTemplate");
					waitForSpinnerToDisappear(20);

					//Provide Description
					textBox("Configuration.MarketGroup.TemplateMargetGroupDescription", configMap.get("DESCRIPTION"), 100, "presence");

					//Provide preference code
					textBox("Configuration.txt_Code", configMap.get("Preference_Code"));

					//Clicking on Save button
					mouseHover("Configuration.MarketGroup.TemplateSaveButton",100,"presence");
					jsClick("Configuration.MarketGroup.TemplateSaveButton",100,"presence");

				}	
				waitForSpinnerToDisappear(20);


				//Provide preference code
				textBox("Configuration.Preferences.PrefernceCodeTemplate", configMap.get("Preference_Code"));
				Utils.tabKey("Configuration.Preferences.PrefernceCodeTemplate");
				waitForSpinnerToDisappear(20);

				//Select Search
				click("Configuration.MarketGroup.TemplateSearchButton");
				waitForSpinnerToDisappear(20);

				//Select More Menu 
				click("Configuration.MarketGroup.TemplateMoreButton");
				waitForSpinnerToDisappear(20);

				//Select Copy link 
				click("Configuration.MarketGroup.TemplateCopyButton");
				waitForSpinnerToDisappear(20);


				//add Available preferences
				textBox("Configuration.Preferences.AddFilter", configMap.get("Preference_Group"));
				click("Configuration.Preferences.SearchFilter");
				waitForSpinnerToDisappear(20);

				//Selecting all the preferences code for the available group
				click("Configuration.Preferences.selectAll");
				waitForSpinnerToDisappear(20);

				//Select all the codes
				click("Configuration.Preferences.AddAll");
				waitForSpinnerToDisappear(20);

				//Select Save
				click("Configuration.MarketGroup.CopyScreenSave");
				waitForSpinnerToDisappear(20);

				//Select Copy and Continue
				click("Configuration.MarketGroup.Copy&Continue");
				waitForSpinnerToDisappear(20);

				//Validation for Copying preferences Code
				//String text = "";
				List<WebElement>  rows1 = Utils.elements("Configuration.Preferences.ConfirmationTable"); 
				System.out.println("rows1.size" + rows1.size());
				if(rows1.size()>0) {

					for(int i =0 ; i<rows1.size();i++) {
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//*[@data-ocid='TBL_T1' and@data-occntnrid='OCPNL_CONFIRMATION']/div[2]/table/tbody/tr["+(i+1)+"]")));
						String text = driver.findElement(By.xpath("//*[@data-ocid='TBL_T1' and@data-occntnrid='OCPNL_CONFIRMATION']/div[2]/table/tbody/tr["+(i+1)+"]/td[1]")).getText();
						System.out.println(text);
						if(text.equals("Complete")){
							logger.log(LogStatus.PASS, configMap.get("Preference_Code") + "Preference code has been copied successfully");
						}
					}
				}
				else {
					logger.log(LogStatus.FAIL, "Confirmation message is not available");
				}

				//Going back to market groups page
				click("Configuration.Preference.BackToPreference");
				waitForSpinnerToDisappear(20);

				mouseHover("Configuration.MarketGroup.tabProperty",100,"presence");
				jsClick("Configuration.MarketGroup.tabProperty",100,"presence");

				Assert.assertEquals(getText("Configuration.MarketGroup.Property", 100, "presence"), "*Property");
				logger.log(LogStatus.PASS, "Landed back in Property page");

				//Provide preference group Text
				textBox("Configuration.Preferences.PreferenceGroup", configMap.get("Preference_Group"));
				Utils.tabKey("Configuration.Preferences.PreferenceGroup");
				waitForSpinnerToDisappear(20);


				//Provide preference code
				textBox("Configuration.Preferences.PrefernceCodeTemplate", configMap.get("Preference_Code"));
				Utils.tabKey("Configuration.Preferences.PrefernceCodeTemplate");
				waitForSpinnerToDisappear(20);

				//Select Search
				click("Configuration.MarketGroup.TemplateSearchButton");
				waitForSpinnerToDisappear(20);

				//Checking if the market group is present in the template or not
				flag = Utils.ValidateGridData("Configuration.Preference.PreferencesCodetable", configMap.get("Preference_Code"));
				System.out.println("flag3" + flag);

				if(flag) {
					System.out.println("Preference code is configured for the property" + configMap.get("Preference_Code"));
					logger.log(LogStatus.PASS, "Preference code is configured for the property->  " + configMap.get("Preference_Code"));

				}
				else {
					logger.log(LogStatus.FAIL, "Preference code is not configured for the property");
				}

			}

		}				
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}


	/*******************************************************************
		-  Description: Create/Verify Attendants
		- Input:Attendant code,Description
		- Output: Attendants code
		- Author: Girish
		- Date: 03/01/19
		- Revision History:
	 ********************************************************************/
	public static void createAttendants(HashMap<String, String> configMap) throws Exception {
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		System.out.println("Map: " + configMap);
		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Inventory menu
			WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Configuration.menu_Inventory");
			click(Utils.element("Configuration.menu_Inventory"));
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_AccommodationManagement", "clickable");
			mouseHover("Configuration.menu_AccommodationManagement");
			jsClick("Configuration.menu_AccommodationManagement");
			System.out.println("clicked Accommodation Management menu");
			logger.log(LogStatus.PASS, "Selected Accommodation Management Menu");

			// Navigating to Housekeeping attendants link
			click("Configuration.Attandants.menu_HousekeepingAttandants", 100, "clickable");
			waitForSpinnerToDisappear(20);
			//Verify Housekeeping attendants page
			boolean blnPageExists = isExists("Configuration.Attandants.header_Attandants");
			if(blnPageExists){
				logger.log(LogStatus.PASS, "Navigate to Attendants screen");

				//Verify if the data already exists 
				boolean blnDataExists = Utils.ValidateGridData("Configuration.Attandants.grd_AttandantsSearch_ColData", configMap.get("ATTENDANTS_CODE"));
				if(blnDataExists){
					logger.log(LogStatus.PASS, "Attendants data already exists on screen");
				}
				else
				{
					//Select New
					jsClick("Configuration.Attandants.link_New",100, "clickable");
					waitForSpinnerToDisappear(50);

					//Provide Attendants Code
					click("Configuration.Attandants.txt_Code",100,"clickable");
					waitForSpinnerToDisappear(10);
					textBox("Configuration.Attandants.txt_Code",configMap.get("ATTENDANTS_CODE"));   //Attendants Code
					waitForSpinnerToDisappear(50);

					//Provided Description
					click("Configuration.Attandants.txt_Description",100,"clickable");
					waitForSpinnerToDisappear(10);
					textBox("Configuration.Attandants.txt_Description",configMap.get("ATTENDANTS_DESC"));   //Attendants Description
					waitForSpinnerToDisappear(50);

					//Select Save
					click("Configuration.btnSave",100,"clickable");
					waitForSpinnerToDisappear(50);

					//Verify the created article is search
					boolean blnDataExistsAfterCreate = Utils.ValidateGridData("Configuration.Attandants.grd_AttandantsSearch_ColData", configMap.get("ATTENDANTS_CODE"));
					if(blnDataExistsAfterCreate){
						logger.log(LogStatus.PASS, "Attendants data exists on screen after creation");
					}else{
						logger.log(LogStatus.FAIL, "****Issue observed on Attendants data creation****");
					}
				}
			}else{
				logger.log(LogStatus.FAIL, "***Issue observed on navigation to Attendants screen***");
			}

		}catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Attendants code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}


	/*******************************************************************
		-  Description: Create/Verify Housekeeping Tasks
		- Input:Tasks code,Description
		- Output: Tasks code
		- Author: Girish
		- Date: 04/01/19
		- Revision History:
	 ********************************************************************/
	public static void createTasks(HashMap<String, String> configMap) throws Exception {
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		System.out.println("Map: " + configMap);
		try {

			// Navigating to Main Menu
			WebdriverWait(100, "Configuration.mainMenu", "clickable");
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu");
			System.out.println("clicked Main menu");
			logger.log(LogStatus.PASS, "Selected Main Menu");

			// Navigating to Administration
			WebdriverWait(100, "Configuration.AdminstrationMenu", "clickable");
			mouseHover("Configuration.AdminstrationMenu");
			jsClick("Configuration.AdminstrationMenu");
			System.out.println("clicked Adminstration Menu");
			logger.log(LogStatus.PASS, "Selected Adminstration Menu");

			// Navigating to Inventory menu
			WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Configuration.menu_Inventory");
			click(Utils.element("Configuration.menu_Inventory"));
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");

			// Navigating to Accommodation Management menu
			WebdriverWait(100, "Configuration.menu_TaskSheets", "clickable");
			mouseHover("Configuration.menu_TaskSheets");
			jsClick("Configuration.menu_TaskSheets");
			System.out.println("clicked Task Sheets menu");
			logger.log(LogStatus.PASS, "Selected Task Sheets Menu");

			// Navigating to Housekeeping Tasks link
			click("Configuration.Tasks.menu_HousekeepingTasks", 100, "clickable");
			waitForSpinnerToDisappear(10);
			//Verify Housekeeping Tasks page
			boolean blnPageExists = isExists("Configuration.Tasks.header_Tasks");
			if(blnPageExists){
				logger.log(LogStatus.PASS, "Navigate to Tasks screen");

				//Verify if the data already exists 
				boolean blnDataExists = Utils.ValidateGridData("Configuration.Tasks.grd_TasksSearch_ColData", configMap.get("TASK_CODE"));
				if(blnDataExists){
					logger.log(LogStatus.PASS, "Attendants data already exists on screen");
				}
				else
				{
					waitForPageLoad(300);
					//Select New
					click("Configuration.Tasks.link_New",100, "clickable");
					waitForSpinnerToDisappear(50);

					//Provide Attendants Code
					click("Configuration.Tasks.txt_Code",100,"clickable");
					waitForSpinnerToDisappear(10);
					textBox("Configuration.Tasks.txt_Code",configMap.get("TASK_CODE"));   //Tasks Code
					waitForSpinnerToDisappear(50);

					//Provided Description
					click("Configuration.Tasks.txt_Description",100,"clickable");
					waitForSpinnerToDisappear(10);
					textBox("Configuration.Tasks.txt_Description",configMap.get("TASK_DESC"));   //Tasks Description
					waitForSpinnerToDisappear(50);

					//Click on Departure Room Default Task check box
					if(configMap.get("DEPARUTRE_ROOM_DEFAULT_TASKS").equalsIgnoreCase("Y")){
						click("Configuration.Tasks.chk_DepartureRoomDefaultTask",100,"clickable");
						waitForSpinnerToDisappear(20);
					}
					//Click on Guest Requested Task check box
					if(configMap.get("GUEST_REQUESTED_TASK").equalsIgnoreCase("Y")){
						click("Configuration.Tasks.chk_GuestRequestedTask",100,"clickable");
						waitForSpinnerToDisappear(20);
					}
					//Click on Linen Change check box
					if(configMap.get("LINEN_CHANGE").equalsIgnoreCase("Y")){
						click("Configuration.Tasks.chk_LinenChange",100,"clickable");
						waitForSpinnerToDisappear(20);
					}

					//Select Save
					click("Configuration.btnSave",100,"clickable");
					waitForSpinnerToDisappear(50);

					//Verify the created article is search
					boolean blnDataExistsAfterCreate = Utils.ValidateGridData("Configuration.Tasks.grd_TasksSearch_ColData", configMap.get("TASK_CODE"));
					if(blnDataExistsAfterCreate){
						logger.log(LogStatus.PASS, "Tasks data exists on screen after creation");
					}else{
						logger.log(LogStatus.FAIL, "****Issue observed on Tasks data creation****");
					}
				}
			}else{
				logger.log(LogStatus.FAIL, "***Issue observed on navigation to Tasks screen***");
			}

		}catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Tasks code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}


	/*******************************************************************
		-  Description: Creating Membership class as the part of Configuration 
		- Input: Membership_class , Description
		- Output: Membership class
		- Author: swati
		- Date:01/03/2019
		- Revision History:
		                - Change Date 
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By: swati
	 ********************************************************************/


	public static void CreateMembershipClass(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Navigating to Client relations menu
			click("Configuration.AdminMenu.ClientRelations", 100, "clickable");

			// Navigating to Membership Management menu
			click("Configuration.AdminMenu.MembershipManagement", 100, "clickable");

			// Navigating to Membership class
			click("Configuration.AdminMenu.MembershipClass", 100, "clickable");

			waitForSpinnerToDisappear(20);

			Assert.assertEquals(getText("Configuration.Membership.Heading", 100, "presence"), "Membership Class");
			logger.log(LogStatus.PASS, "Landed in Membership class Page");


			List<WebElement>  rows = Utils.elements("Configuration.MembershipClass.TableGrid"); 
			System.out.println("No of rows are : " + rows.size());

			Boolean flag = false;
			if(rows.size() > 0)
			{

				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.MembershipClass.tableData", configMap.get("Membership_Class"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Membership Class "+configMap.get("Membership_Class")+"is already available in the property");
					logger.log(LogStatus.PASS, "Membership Class "+configMap.get("Membership_Class")+"is already available in the property");
					flag = true;
				}

			}	

			if(!flag)
			{

				logger.log(LogStatus.PASS, "Membership Class is not present, Creating a new one");

				//Create the new template
				jsClick("Configuration.Preference.TemplateNew");
				waitForSpinnerToDisappear(20);

				//Provide Membership class Text
				textBox("Configuration.MembershipClass.MembershipClass", configMap.get("Membership_Class"));
				Utils.tabKey("Configuration.MembershipClass.MembershipClass");
				waitForSpinnerToDisappear(20);

				//Provide Description
				textBox("Configuration.MembershipClass.MembershipClassDescriptionField", configMap.get("Membership_Description"), 100, "presence");
				Utils.tabKey("Configuration.MembershipClass.MembershipClassDescriptionField");
				waitForSpinnerToDisappear(20);

				//Clicking on Save button
				mouseHover("Configuration.MembershipClass.Save",100,"presence");
				jsClick("Configuration.MembershipClass.Save",100,"presence");
				waitForSpinnerToDisappear(20);

				//Providing the created membership class
				//Provide Membership class Text
				textBox("Configuration.MembershipClass.MembershipClass", configMap.get("Membership_Class"));
				Utils.tabKey("Configuration.MembershipClass.MembershipClass");
				waitForSpinnerToDisappear(20);

				click("Configuration.MembershipClass.SearchButton");
				waitForSpinnerToDisappear(20);

				String MembershipClass = driver.findElement(By.xpath("//*[@data-occellvalue='"+configMap.get("Membership_Class")+"']")).getText();
				if(MembershipClass.equals(configMap.get("Membership_Class"))){
					logger.log(LogStatus.PASS, "Membership Class is created successfully-> " + configMap.get("Membership_Class") );
				}
				else {
					logger.log(LogStatus.PASS, "Membership Class is not created successfully");
				}

			}	

		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}


	/*******************************************************************
		-  Description: Creating Membership Type as the part of Configuration 
		- Input: Membership_class , Description
		- Output: Membership class
		- Author: swati
		- Date:01/03/2019
		- Revision History:
		                - Change Date 
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By: swati
	 ********************************************************************/


	public static void CreateMembershipType(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			// Navigating to Client relations menu
			click("Configuration.AdminMenu.ClientRelations", 100, "clickable");

			// Navigating to Membership Management menu
			click("Configuration.AdminMenu.MembershipManagement", 100, "clickable");

			// Navigating to Membership Type
			click("Configuration.AdminMenu.MembershipType", 100, "clickable");

			waitForSpinnerToDisappear(20);


			Assert.assertEquals(getText("Configuration.MembershipType.Heading", 100,
					"presence"), "Membership Types"); logger.log(LogStatus.PASS,
							"Landed in Membership Types Page");


					List<WebElement>  rows = Utils.elements("Configuration.MembershipType.TableGrid"); 
					System.out.println("No of rows are : " + rows.size());

					Boolean flag = false;
					if(rows.size() > 0)
					{

						//Checking if the market group is present in the property or not
						flag = Utils.ValidateGridData("Configuration.MembershipType.tableData", configMap.get("Membership_Type"));
						System.out.println("flag1" + flag);

						if(flag) {
							System.out.println("The Membership Type "+configMap.get("Membership_Type")+"is already available in the property");
							logger.log(LogStatus.PASS, "Membership Type "+configMap.get("Membership_Type")+"is already available in the property");
							flag = true;
						}

					}	

					if(!flag)
					{

						logger.log(LogStatus.PASS, "Membership Type is not present, Creating a new one");

						//Create the new template
						jsClick("Configuration.Preference.TemplateNew");
						waitForSpinnerToDisappear(20);

						//Provide Membership Type Text
						textBox("Configuration.MembershipType.MembershipType", configMap.get("Membership_Type"));
						Utils.tabKey("Configuration.MembershipType.MembershipType");
						waitForSpinnerToDisappear(20);

						//Provide Description
						textBox("Configuration.MembershipType.MembershipTypeDescriptionField", configMap.get("Membership_TypeDes"), 100, "presence");
						Utils.tabKey("Configuration.MembershipType.MembershipTypeDescriptionField");
						waitForSpinnerToDisappear(20);

						//Provide Class
						textBox("Configuration.MembershipType.MembershipTypeClass", configMap.get("Membership_Class"), 100, "presence");
						Utils.tabKey("Configuration.MembershipType.MembershipTypeClass");
						waitForSpinnerToDisappear(20);

						//Clicking on Save button
						mouseHover("Configuration.MembershipType.Save",100,"presence");
						jsClick("Configuration.MembershipType.Save",100,"presence");
						waitForSpinnerToDisappear(20);

						//Providing the created membership class
						//Provide Membership Type Text
						textBox("Configuration.MembershipType.MembershipType", configMap.get("Membership_Type"));
						Utils.tabKey("Configuration.MembershipType.MembershipType");
						waitForSpinnerToDisappear(20);

						//Provide Membership Type Class
						textBox("Configuration.MembershipType.MembershipTypeClass", configMap.get("Membership_Class")); //taking member ship class value.
						Utils.tabKey("Configuration.MembershipType.MembershipTypeClass");
						waitForSpinnerToDisappear(20);

						click("Configuration.MembershipType.SearchButton");
						waitForSpinnerToDisappear(20);

						String MembershipType = driver.findElement(By.xpath("//*[@data-occellvalue='"+configMap.get("Membership_Type")+"']")).getText();
						if(MembershipType.equals(configMap.get("Membership_Type"))){
							logger.log(LogStatus.PASS, "Membership Type is created successfully-> " + configMap.get("Membership_Type") );
						}
						else {
							logger.log(LogStatus.FAIL, "Membership Type is not created successfully");
						}

					}	

		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}
	/*******************************************************************
- Description: This method creates KeywordTypes
- Input:KeywordTypes
- Output:
- Author:Dilip
- Date: 11/30/2018
- Revision History:
                - Change Date
                - Change Reason
                - Changed Behavior
                - Last Changed By
	 ********************************************************************/

	public static void createKeywordTypes(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to ClientRelations
			waitForSpinnerToDisappear(10);
			click("Configuration.AdminMenu.ClientRelations", 100, "clickable");

			// Navigating to ProfileManagement
			click("Configuration.AdminMenu_ClinetRelations_ProfileManagement", 100, "clickable");

			// Navigating to KeywordTypes
			click("Configuration.AdminMenu_ClinetRelations_ProfileManagement_KeywordTypes", 100, "clickable");

			// Verifying if KeywordTypes exists
			if (ValidateGridData("Configuration.table_KeywordTypes_Grid", configMap.get("CODE"))){

				System.out.println("KeywordType already exists :"+configMap.get("CODE"));
				logger.log(LogStatus.PASS, "KeywordType already exists :" +configMap.get("CODE"));

			}else{


				// Clicking on New Button
				waitForSpinnerToDisappear(50);
				jsClick("Configuration.link_PaymentMethods_New", 100, "clickable");				

				WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "clickable");

				// Entering the Code
				textBox("Configuration.txt_KeywordTypes_Code",configMap.get("CODE"), 100, "presence");


				// Entering the Description
				waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_KeywordTypes_Desc",configMap.get("DESCRIPTION"), 100, "presence");            


				// Clicking on Save Button
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "presence");


				// Verify if KeywordTypes is created  
				WebdriverWait(100, "Configuration.link_PaymentMethods_New", "clickable");
				if (ValidateGridData("Configuration.table_KeywordTypes_Grid", configMap.get("CODE"))){

					System.out.println("KeywordType is created successfully :"+configMap.get("CODE"));
					logger.log(LogStatus.PASS, "KeywordType is created successfully :" +configMap.get("CODE"));
				} else {
					System.out.println("KeywordType is not created successfully :"+configMap.get("CODE"));
					logger.log(LogStatus.FAIL, "KeywordType is not created successfully :"+configMap.get("CODE"));
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create keywordTypes  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "KeywordType is not created successfully :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}

	/*******************************************************************
	-  Description: Verify user is able to create a Deposit Rule
	- Input: DEPOSIT_RULE, DESCRIPTION, AMOUNT, TYPE
	- Output: Deposit Rule should be created
	- Author: @author vnadipal
	- Date: 01/07/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void createDepositRule(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			jsClick("Configuration.mainMenu", 100, "clickable");

			// Clicking on Administration Button
			jsClick("Configuration.AdminstrationMenu", 100, "clickable");

			// Clicked on Bookings Menu
			jsClick("Configuration.menu_Booking", 100,"presence");

			// Clicked on Booking Rules and Schedules Menu
			jsClick("Configuration.DepositRules.menuItemBookingRulesSchedules", 100,"presence");

			// Clicked on Deposit Rules Menu
			jsClick("Configuration.DepositRules.subMenuItemDepositRules", 100,"presence");

			//Enter Deposit Rule
			jsTextbox("Configuration.DepositRules.txtDepositRule", configMap.get("DEPOSIT_RULE"));
			logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

			// Clicked on Search button
			jsClick("Configuration.DepositRules.btnSearch", 100, "presence");

			if(isExists("Configuration.RateClass.popupLinkCancel")) {
				jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
				element("Configuration.DepositRules.txtDepositRuleWithText").clear();
			}

			List<WebElement>  rows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("DEPOSIT_RULE"))) {
						System.out.println("Deposit Rule "+rows.get(i).getText()+" is already existing in Property");
						flag = true;
						logger.log(LogStatus.PASS, "Deposit Rule "+rows.get(i).getText()+" is already existing in Property");
						break;
					}
					else {
						System.out.println("Deposit Rule is not present in Property:: "+rows.get(i).getText());						
					}
				}
			}

			if(!flag) {
				// Clicked on Template Tab
				jsClick("Configuration.DepositRules.tabTemplate", 100,"presence");

				//Enter Deposit Rule
				jsTextbox("Configuration.DepositRules.txtDepositRule", configMap.get("DEPOSIT_RULE"));
				logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

				// Clicked on Search button
				jsClick("Configuration.DepositRules.btnSearch", 100, "presence");

				if(isExists("Configuration.RateClass.popupLinkCancel")) {
					jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
					element("Configuration.DepositRules.txtDepositRuleWithText").clear();
				}

				waitForSpinnerToDisappear(10);
				List<WebElement> trows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
				System.out.println("No of rows are : " + trows.size());
				boolean tflag = false;
				if(trows.size() > 0) {
					for(int i=0;i<trows.size();i++) {
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trows.get(i));
						if(trows.get(i).getText().equalsIgnoreCase(configMap.get("DEPOSIT_RULE"))) {
							System.out.println("Deposit Rule "+trows.get(i).getText()+" is already existing in Template");
							tflag = true;
							logger.log(LogStatus.PASS, "Deposit Rule "+trows.get(i).getText()+" is already existing in Template");
							break;
						}
						else {
							System.out.println("Deposit Rule is not present in Template:: "+trows.get(i).getText());						
						}
					}
				}
				waitForSpinnerToDisappear(15);

				if(!tflag) {
					waitForPageLoad(1000);
					Thread.sleep(3000);
					// Clicked on New button
					jsClick("Configuration.DepositRules.linkNew", 100,"presence");

					waitForSpinnerToDisappear(10);
					//Enter Deposit Rule
					jsTextbox("Configuration.DepositRules.TemplateTab.txtCode", configMap.get("DEPOSIT_RULE"));
					waitForSpinnerToDisappear(5);
					logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

					//Enter Description
					jsTextbox("Configuration.DepositRules.TemplateTab.txtDescription", configMap.get("DESCRIPTION"));
					logger.log(LogStatus.INFO, "Entered Description: " + configMap.get("DESCRIPTION"));

					//Enter Type
					selectBy("Configuration.DepositRules.TemplateTab.dropdownType", "text", configMap.get("TYPE"));
					logger.log(LogStatus.INFO, "Selected Type as: " + configMap.get("TYPE"));

					//Enter Days Before Arrival
					jsTextbox("Configuration.DepositRules.TemplateTab.txtDaysBeforeArrival", configMap.get("DAYS_BEFORE_ARRIVAL"));
					logger.log(LogStatus.INFO, "Entered Days Before Arrival: " + configMap.get("DAYS_BEFORE_ARRIVAL"));

					//Enter Amount
					Utils.WebdriverWait(100, "Configuration.DepositRules.TemplateTab.txtAmount", "stale");
					jsTextbox("Configuration.DepositRules.TemplateTab.txtAmount", configMap.get("DEPOSIT_AMOUNT"));
					logger.log(LogStatus.INFO, "Entered Amount: " + configMap.get("DEPOSIT_AMOUNT"));
					Thread.sleep(5000);

					// Clicked on Save button
					jsClick("Configuration.DepositRules.TemplateTab.btnSave", 100,"presence");

					waitForSpinnerToDisappear(15);
					//Enter Deposit Rule
					jsTextbox("Configuration.DepositRules.txtDepositRule", configMap.get("DEPOSIT_RULE"));
					logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

					// Clicked on Search button
					jsClick("Configuration.DepositRules.btnSearch", 100, "presence");

					Thread.sleep(8000);
					if(getText("Configuration.RateCategory.validationTemplateDiv").contains(configMap.get("DEPOSIT_RULE"))) {
						System.out.println("Deposit Rule " + configMap.get("DEPOSIT_RULE") + " is successfully created in Template");
						logger.log(LogStatus.PASS,"Deposit Rule " + configMap.get("DEPOSIT_RULE") + " is successfully created in Template");
					} 
					else {
						System.out.println("Deposit Rule " + configMap.get("DEPOSIT_RULE") + " is not created in Template");
						logger.log(LogStatus.FAIL,"Deposit Rule " + configMap.get("DEPOSIT_RULE") + " is not created in Template");
					}
				}

				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Link More
				jsClick("Configuration.RateCategory.linkLinkMore", 100,"presence");

				waitForPageLoad(100);
				// Clicked on Copy
				jsClick("Configuration.RateCategory.linkCopy", 100,"presence");

				Thread.sleep(3000);
				//Enter Deposit Rule
				jsTextbox("Configuration.DepositRules.txtDepositRule", configMap.get("DEPOSIT_RULE"));
				logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

				//Enter Target Properties
				jsTextbox("Configuration.RateCategory.lovTargetProperties", configMap.get("PROPERTY"));
				logger.log(LogStatus.INFO, "Entered Target Properties: " + configMap.get("PROPERTY"));

				// Clicked on Save button
				jsClick("Configuration.RateCategory.btnSave", 100,"presence");

				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Copy And Continue button
				jsClick("Configuration.RateCategory.btnCopyAndContinue", 100,"presence");

				Thread.sleep(3000);
				waitForPageLoad(100);
				// Clicked on Back To Deposit Rules link
				jsClick("Configuration.DepositRules.TemplateTab.linkBackToDepositRules", 100,"presence");

				// Clicked on Property Tab
				jsClick("Configuration.RateCategory.tabProperty", 100,"presence");

				Thread.sleep(3000);
				//Enter Deposit Rule
				jsTextbox("Configuration.DepositRules.txtDepositRule", configMap.get("DEPOSIT_RULE"));
				logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

				// Clicked on Search button
				jsClick("Configuration.RateCategory.btnSearch", 100, "presence");

				Thread.sleep(8000);
				if(getText("Configuration.RateCategory.validationTemplateDiv").contains(configMap.get("DEPOSIT_RULE"))) {
					System.out.println("Rate Category " + configMap.get("DEPOSIT_RULE") + " is successfully copied to Property");
					logger.log(LogStatus.PASS,"Deposit Rule " + configMap.get("DEPOSIT_RULE") + " is successfully copied to Property");
				} 
				else {
					System.out.println("Deposit Rule " + configMap.get("DEPOSIT_RULE") + " is not copied to Property");
					logger.log(LogStatus.FAIL,"Deposit Rule " + configMap.get("DEPOSIT_RULE") + " is not copied to Property");
				}
			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: Verify user is able to create a Deposit Rules Schedule
	- Input: DEPOSIT_RULE, DESCRIPTION, RATEC_CODE, BEGIN_DATE, END_DATE
	- Output: Deposit Rules Schedule should be created
	- Author: @author vnadipal
	- Date: 01/08/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void createDepositRulesSchedule(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			jsClick("Configuration.mainMenu", 100, "clickable");

			// Clicking on Administration Button
			jsClick("Configuration.AdminstrationMenu", 100, "clickable");

			// Clicked on Bookings Menu
			jsClick("Configuration.menu_Booking", 100,"presence");

			// Clicked on Booking Rules and Schedules Menu
			jsClick("Configuration.DepositRuleSchedule.menuItemBookingRulesSchedules", 100,"presence");

			// Clicked on Deposit Rules Schedules Menu
			jsClick("Configuration.DepositRuleSchedule.subMenuItemDepositRulesSchedules", 100,"presence");

			//Enter Deposit Rule Schedule
			jsTextbox("Configuration.DepositRuleSchedule.txtRule", configMap.get("DEPOSIT_RULE"));
			logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

			// Clicked on Search button
			jsClick("Configuration.DepositRuleSchedule.btnSearch", 100, "presence");

			if(isExists("Configuration.RateClass.popupLinkCancel")) {
				jsClick("Configuration.RateClass.popupLinkCancel", 100, "presence");
				element("Configuration.DepositRuleSchedule.txtRule").clear();
			}

			List<WebElement>  rows = Utils.elements("Configuration.grd_SourceCodeSearch_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0) {
				for(int i=0;i<rows.size();i++) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("DEPOSIT_RULE"))) {
						System.out.println("Deposit Rule "+rows.get(i).getText()+" is already existing in Property");
						flag = true;
						logger.log(LogStatus.PASS, "Deposit Rule Schedule "+rows.get(i).getText()+" is already existing in Property");
						break;
					}
					else {
						System.out.println("Deposit Rule Schedule "+rows.get(i).getText()+" is not present in the Property");						
					}
				}
			}

			if(!flag) {
				waitForPageLoad(1000);
				Thread.sleep(3000);
				// Clicked on New button
				jsClick("Configuration.DepositRules.linkNew", 100,"presence");

				waitForSpinnerToDisappear(10);

				//Enter Deposit Rule
				jsTextbox("Configuration.DepositRuleSchedule.New.txtRule", configMap.get("DEPOSIT_RULE"));
				waitForSpinnerToDisappear(5);
				logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

				//Enter Begin Date
				jsTextbox("Configuration.DepositRuleSchedule.New.txtBeginDate", configMap.get("BEGIN_DATE"));
				logger.log(LogStatus.INFO, "Entered Begin Date: " + configMap.get("BEGIN_DATE"));

				//Enter End Date
				jsTextbox("Configuration.DepositRuleSchedule.New.txtEndDate", configMap.get("END_DATE"));
				logger.log(LogStatus.INFO, "Entered End Date: " + configMap.get("END_DATE"));

				//Enter Sequence
				jsTextbox("Configuration.DepositRuleSchedule.New.txtSequence", configMap.get("SEQUENCE"));
				logger.log(LogStatus.INFO, "Entered Sequence: " + configMap.get("SEQUENCE"));

				//Enter Rate Code
				jsTextbox("Configuration.DepositRuleSchedule.New.lovRateCode", configMap.get("RATE_CODE"));
				logger.log(LogStatus.INFO, "Entered Rate Code: " + configMap.get("RATE_CODE"));

				// Clicked on Save button
				jsClick("Configuration.DepositRuleSchedule.New.btnSave", 100,"presence");

				waitForSpinnerToDisappear(15);
				//Enter Deposit Rule Schedule
				jsTextbox("Configuration.DepositRuleSchedule.txtRule", configMap.get("DEPOSIT_RULE"));
				logger.log(LogStatus.INFO, "Entered Deposit Rule: " + configMap.get("DEPOSIT_RULE"));

				// Clicked on Search button
				jsClick("Configuration.DepositRuleSchedule.btnSearch", 100, "presence");

				Thread.sleep(8000);
				if(getText("Configuration.RateCategory.validationTemplateDiv").contains(configMap.get("DEPOSIT_RULE"))) {
					System.out.println("Deposit Rule Schedule " + configMap.get("DEPOSIT_RULE") + " is successfully created for " + configMap.get("RATE_CODE") + " rate code");
					logger.log(LogStatus.PASS, "Deposit Rule Schedule " + configMap.get("DEPOSIT_RULE") + " is successfully created for " + configMap.get("RATE_CODE") + " rate code");
				} 
				else {
					System.out.println("Deposit Rule Schedule " + configMap.get("DEPOSIT_RULE") + " is not created for " + configMap.get("RATE_CODE") + " rate code");
					logger.log(LogStatus.FAIL, "Deposit Rule Schedule " + configMap.get("DEPOSIT_RULE") + " is not created for " + configMap.get("RATE_CODE") + " rate code");
				}
			}
		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}

	/*******************************************************************
	-  Description: Creating Notes Type as the part of Configuration 
	- Input: Notes group , code and description
	- Output: Notes type
	- Author: swati
	- Date:01/21/2019
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: 
	 ********************************************************************/


	public static void CreateNotesType(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			//Navigating to enterprise
			click("Configuration.AdminMenu.Enterprise");

			// Navigating to Chain and Property Management menu
			click("Configuration.AdminMenu.ChainandPropertyManagement", 100, "clickable");

			// Navigating to Note Types menu
			click("Configuration.AdminMenu.NoteTypes", 100, "clickable");


			waitForSpinnerToDisappear(20);

			Assert.assertEquals(getText("Configuration.AdminMenu.NoteTypesHeading", 100, "presence"), "Note Types");
			logger.log(LogStatus.PASS, "Landed in Note Types Page");

			//Providing the notes group
			textBox("Configuration.AdminMenu.NotesGroup", configMap.get("NOTES_GROUP"));
			Utils.tabKey("Configuration.AdminMenu.NotesGroup");
			waitForSpinnerToDisappear(30);

			click("Configuration.AdminMenu.SearchButton");
			waitForSpinnerToDisappear(30);

			List<WebElement>  rows = Utils.elements("Configuration.NoteTypes.TableGrid"); 
			System.out.println("No of rows are : " + rows.size());

			Boolean flag = false;
			if(rows.size() > 0)
			{

				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.NoteTypes.TableGrid", configMap.get("NOTES_TYPE"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Note type "+configMap.get("NOTES_TYPE")+"is already available in the property");
					logger.log(LogStatus.PASS, "The Note type "+configMap.get("NOTES_TYPE")+"is already available in the property");
					flag = true;
				}

			}	

			if(!flag)
			{

				logger.log(LogStatus.PASS, "The Note type is not present, Creating a new one");

				//Create the new Notes type
				jsClick("Configuration.NoteTypes.LinkNew");
				waitForSpinnerToDisappear(20);

				//Provide Notes group
				textBox("Configuration.AdminMenu.NotesGroup", configMap.get("NOTES_GROUP"));
				Utils.tabKey("Configuration.AdminMenu.NotesGroup");
				waitForSpinnerToDisappear(20);

				//Provide Notes Code
				textBox("Configuration.NoteTypes.NotesCode", configMap.get("NOTES_TYPE"));
				Utils.tabKey("Configuration.NoteTypes.NotesCode");
				waitForSpinnerToDisappear(20);

				//Provide Description
				textBox("Configuration.NoteTypes.Description", configMap.get("Notes_Description"), 100, "presence");
				Utils.tabKey("Configuration.NoteTypes.Description");
				waitForSpinnerToDisappear(20);

				// Uncheck all the checkboxes on the webpage


				List<WebElement> elements = Utils.elements("Configuration.NoteTypes.CheckBox");

				for(int i =0 ; i < elements.size();i++) {

					if(elements.get(i).isSelected()) {
						Actions actions = new Actions(driver);
						actions.moveToElement(elements.get(i)).click().build().perform();
					}
				}

				//				if(configMap.containsKey("CHK_INTERNAL")) {
				//					click("Configuration.NoteTypes.InternalCheckBox");
				//					waitForSpinnerToDisappear(50);
				//				}

				if(configMap.containsKey("CHK_OVERINTERNAL")) {
					click(element("Configuration.NoteTypes.InternalOverCheckBox"));
					waitForSpinnerToDisappear(50);
				}

				if(configMap.containsKey("CHK_DEFAULTNOTE")) {
					click(element("Configuration.NoteTypes.DefaultNoteCheckBox"));
					waitForSpinnerToDisappear(50);
				}

				if(configMap.containsKey("CHK_DEFAULTDESC")) {
					click(element("Configuration.NoteTypes.DefaultNoteTextCheckBox"));
					waitForSpinnerToDisappear(50);
				}

				if(configMap.containsKey("CHK_DEPARTMENT")) {
					click(element("Configuration.NoteTypes.DepartmentNoteCheckBox"));
					waitForSpinnerToDisappear(50);
				}

				//Clicking on Save button
				mouseHover("Configuration.NoteTypes.Save",100,"presence");
				jsClick("Configuration.NoteTypes.Save",100,"presence");
				waitForSpinnerToDisappear(20);


				//Provide Notes group
				textBox("Configuration.AdminMenu.NotesGroup", configMap.get("NOTES_GROUP"));
				Utils.tabKey("Configuration.AdminMenu.NotesGroup");
				waitForSpinnerToDisappear(20);

				click("Configuration.AdminMenu.SearchButton");
				waitForSpinnerToDisappear(30);

				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.NoteTypes.TableGrid", configMap.get("NOTES_TYPE"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Note type "+configMap.get("NOTES_TYPE")+"is configured in the property");
					logger.log(LogStatus.PASS, "The Note type "+configMap.get("NOTES_TYPE")+"is already available in the property");
					flag = true;
				}

			}	

		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}
	/*******************************************************************
	- Description: This method checks and set the Act as and AT in the edit user in Role manager for create blocks
	- Input: configMap- Test Data row for CreateBlock
	- Output:
	- Author:Vamsi
	- Date:01/17/2019
	- Revision History:0.1
	 ********************************************************************/

	public static void blocksConfigureRoleManagerActasAt(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);
		Map<String, String> configMapEvn = new HashMap<String, String>();
		configMapEvn = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		configMapEvn = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configMapEvn.get("Set"));

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Role manager
			click("Configuration.RoleManagerMenu",100, "clickable");
			waitForSpinnerToDisappear(100);

			// Navigating to Manage Users
			click("Configuration.RoleManagerMenu.ManageUsers", 100, "clickable");
			waitForSpinnerToDisappear(100);

			// Entering the User ID
			textBox("Configuration.RoleManagerMenu.UserId", configMapEvn.get("UserName"), 100, "clickable");

			// click on Search Button
			click("Configuration.RoleManagerMenu.SearchButton", 100, "clickable");
			waitForSpinnerToDisappear(100);

			// Validating if User is loaded
			Assert.assertTrue(getText("Configuration.RoleManagerMenu.SearchedUserId", 100, "presence").equalsIgnoreCase(configMapEvn.get("UserName")));

			// Clicking on Action Button
			click("Configuration.RoleManagerMenu.ActionButton", 100, "presence");
			waitForSpinnerToDisappear(100);

			// Clicking on Edit Button
			click("Configuration.RoleManagerMenu.EditButton", 100, "presence");
			waitForSpinnerToDisappear(100);

			Utils.selectBy("Configuration.BookingMenu.ActAsDropdown", "text", configMap.get("ActAs_RoleUserEdit"));
			//		textBox("Configuration.BookingMenu.ActAsDropdown", configMap.get("ActAs_RoleUserEdit"), 100, "clickable");
			click("Configuration.BookingMenu.AtDropdownEditUser", 100, "presence");
			waitForSpinnerToDisappear(100);
			Utils.selectBy("Configuration.BookingMenu.AtDropdownEditUser", "text", configMap.get("At_RoleUserEdit"));
			//		textBox("Configuration.BookingMenu.AtDropdownEditUser", configMap.get("At_RoleUserEdit"), 100, "clickable");
			waitForSpinnerToDisappear(100);
			click("Configuration.btn_Save", 100, "clickable");
			waitForSpinnerToDisappear(100);

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create marketGroup  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "marketGroup not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			tearDown();
			throw (e);
		}

	}

	/*******************************************************************
-  Description: Creating package code as the part of Configuration 
- Input: Package_Code,Package_Description,Transaction_Code,Package_Profit,Package_Loss,Calculation_Rule,Posting_Rythm

- Output: Package_Code
- Author: jasatis
- Date:01/03/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: jasatis
	 ********************************************************************/


	public static void PackageCodes(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {
			click("Configuration.mainMenu", 100, "clickable");
			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");
			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");
			// Navigating to Financials 
			click("Configuration.Financials", 100, "clickable");
			// Navigating to RateManagement 
			click("Configuration.Financials.RateMangement", 100, "clickable");
			// Navigating to Package Codes relations menu
			click("Configuration.Ratemanagement.Packagecodes", 100, "clickable");
			waitForSpinnerToDisappear(20);
			Assert.assertEquals(getText("Configuration.Packagecodes.Heading", 100, "presence"), "Package Codes");
			logger.log(LogStatus.PASS, "Landed in Package Codes Page");
			textBox("Configuration.Ratemanagement.Packagecode", configMap.get("PACKAGE_CODE"));
			Utils.tabKey("Configuration.Ratemanagement.Packagecode");
			logger.log(LogStatus.PASS, "Entered the Package Code");
			click("Configuration.Packagecodes.searchbutton");
			logger.log(LogStatus.PASS, "Clicked on Packagecode code search");
			waitForSpinnerToDisappear(20);
			List<WebElement>  rows = Utils.elements("Configuration.Packagecodes.table"); 
			System.out.println("No of rows are : " + rows.size());
			Boolean flag = false;
			if(rows.size() > 0)
			{
				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.Packagecodes.Packagecodecolumn", configMap.get("PACKAGE_CODE"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Package code "+configMap.get("PACKAGE_CODE")+"is already available in the property");
					logger.log(LogStatus.PASS, "The Package code "+configMap.get("PACKAGE_CODE")+"is already available in the property");
				}
			}	
			if(!flag)
			{
				logger.log(LogStatus.PASS, "Package code is not present in Property , Creating a new one");
				//Click on the new link
				jsClick("Configuration.Packagecodes.linknew");
				waitForSpinnerToDisappear(20);
				//Provide Package code Text
				textBox("Configuration.Ratemanagement.Packagecode", configMap.get("PACKAGE_CODE"));
				Utils.tabKey("Configuration.Ratemanagement.Packagecode");
				waitForSpinnerToDisappear(20);
				//Provide Description
				Utils.click("Configuration.Packagecodes.description");
				textBox("Configuration.Packagecodes.description", configMap.get("DESC"), 100, "presence");
				Utils.tabKey("Configuration.Packagecodes.description");
				//Clicking on Save button
				mouseHover("Configuration.Packagecodes.savebutton",100,"presence");
				jsClick("Configuration.Packagecodes.savebutton",100,"presence");
				waitForSpinnerToDisappear(20);
				Utils.Wait(3000);
				textBox("Configuration.Packagecodes.transactioncodetxt", configMap.get("TRANSACTION_CODE"));
				Utils.tabKey("Configuration.Packagecodes.transactioncodetxt");
				waitForSpinnerToDisappear(20);
				if(!element("Configuration.Packagecodes.packageallowancecheckbox").isSelected()) {
					click("Configuration.Packagecodes.packageallowancecheckbox", 100, "presence");
					waitForSpinnerToDisappear(20);
				}

				textBox("Configuration.Packagecodes.packagprofittxt", configMap.get("PACKAGE_PROFIT"), 100, "presence");
				Utils.tabKey("Configuration.Packagecodes.packagprofittxt");
				waitForSpinnerToDisappear(20);

				textBox("Configuration.Packagecodes.packagelosstxt", configMap.get("PACKAGE_LOSS"), 100, "presence");
				Utils.tabKey("Configuration.Packagecodes.packagelosstxt");
				waitForSpinnerToDisappear(20);

				mouseHover("Configuration.Packagecodes.transactioncode.savebutton",100,"presence");
				jsClick("Configuration.Packagecodes.transactioncode.savebutton",100,"presence");
				waitForSpinnerToDisappear(20);

				if(configMap.get("IncludeInRate").equalsIgnoreCase("Y"))
				{					
					click("Configuration.Packagecodes.radBtnIncludeinrate",100,"clickable");
				}else if(configMap.get("Addtoseperateline").equalsIgnoreCase("Y")){				
					click("Configuration.Packagecodes.radBtnAddtoseperateline",100,"clickable");
				}else if(configMap.get("Addtocombinedline").equalsIgnoreCase("Y")){				
					click("Configuration.Packagecodes.radBtnAddtocombinedline",100,"clickable");
				}
				//configMap.get("Calculation_Rule")
				selectBy("Configuration.Packagecodes.calculatioruleSelectbox", "text", configMap.get("CALCULATION_RULE"));
				//configMap.get("Posting_Rythm")
				selectBy("Configuration.Packagecodes.postingrythmSelectbox", "text", configMap.get("POSTING_RHYTHM"));
				//Configuration.Packagecodes.postingattributes.savebutton
				Utils.scrolltoElement("Configuration.Packagecodes.postingattributes.savebutton");
				jsClick("Configuration.Packagecodes.postingattributes.savebutton",100,"presence");
				waitForSpinnerToDisappear(20);

				textBox("Configuration.Packagecodes.pricetxt", configMap.get("PRICE"), 100, "presence");
				Utils.tabKey("Configuration.Packagecodes.pricetxt");
				waitForSpinnerToDisappear(20);

				textBox("Configuration.Packagecodes.allowancetxt", configMap.get("ALLOWANCE"), 100, "presence");
				Utils.tabKey("Configuration.Packagecodes.allowancetxt");
				waitForSpinnerToDisappear(20);

				//mouseHover("Configuration.Packagecodes.packagepricing.savebutton",100,"presence");
				jsClick("Configuration.Packagecodes.packagepricing.savebutton",100,"presence");
				waitForSpinnerToDisappear(20);

				mouseHover("Configuration.Packagecodes.finalsavebutton",100,"presence");
				jsClick("Configuration.Packagecodes.finalsavebutton",100,"presence");
				waitForSpinnerToDisappear(20);
			}	
			textBox("Configuration.Ratemanagement.Packagecode", configMap.get("PACKAGE_CODE"));
			Utils.tabKey("Configuration.Ratemanagement.Packagecode");
			logger.log(LogStatus.PASS, "Entered the Package Code");

			click("Configuration.Packagecodes.searchbutton");
			logger.log(LogStatus.PASS, "Clicked on Package code search");
			waitForSpinnerToDisappear(20);
			//Checking if the Package Code is created or not
			flag = Utils.ValidateGridData("Configuration.Packagecodes.Packagecodecolumn", configMap.get("PACKAGE_CODE"));
			System.out.println("flag3" + flag);

			if(flag) {
				System.out.println("Package code is configured for the property" + configMap.get("PACKAGE_CODE"));
				logger.log(LogStatus.PASS, "Package code is configured for the property->  " + configMap.get("PACKAGE_CODE"));

			}
			else {
				logger.log(LogStatus.FAIL, "Package code is not configured for the property");
			}
		}

		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*******************************************************************
-  Description: Attach the Package Code to the Rate Codes 
- Input: Package_Code,Rate_Code
- Author: jasatis
- Date:01/28/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: jasatis
	 ********************************************************************/
	public static void attachPackageCodestoRateCode(HashMap<String, String> configMap) throws Exception
	{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		System.out.println("Map: " + configMap);
		try {
			// Navigating to Main Menu
			jsClick("Configuration.mainMenu", 100, "presence");
			// Navigating to Administration
			jsClick("Configuration.AdminstrationMenu", 100, "presence");
			// Clicked on Financial Menu
			jsClick("Configuration.RateCategory.menuFinancial", 100, "presence");
			// Clicked on Rate Management Menu
			jsClick("Configuration.RateCategory.menuRateManagement", 100, "presence");
			// Clicked on Rate Codes Menu Item
			jsClick("Configuration.RateCode.menuItemRateCodes", 100, "presence");
			//Enter Rate Code
			jsTextbox("Configuration.RateCode.txtRateCode", configMap.get("RATE_CODE"));
			// Clicked on Search button
			jsClick("Configuration.RateCategory.btnSearch", 100, "presence");
			waitForSpinnerToDisappear(20);
			// Clicked on actions dotted link
			jsClick("Configuration.Ratecode.actionsdottedlink", 100, "presence");
			waitForSpinnerToDisappear(20);
			// Clicked on edit link
			jsClick("Configuration.Ratecode.actions.editlink", 100, "presence");
			waitForSpinnerToDisappear(20);
			jsClick("Configuration.Ratecode.ratecodepackages", 100, "presence");
			waitForSpinnerToDisappear(20);
			jsClick("Configuration.Ratecode.ratecodepackages.newlink", 100, "presence");
			waitForSpinnerToDisappear(20);
			textBox("Configuration.Ratecode.packagecodetxt", configMap.get("PACKAGE_CODE"), 100, "presence");
			jsClick("Configuration.Ratecode.packagecode.searchlink", 100, "presence");
			waitForSpinnerToDisappear(20);
			if(!element("Configuration.Ratecode.packagecode.chkpackagecode").isSelected())
				jsClick("Configuration.Ratecode.packagecode.chkpackagecode", 100, "presence");
			waitForSpinnerToDisappear(20);
			jsClick("Configuration.Ratecode.packagecode.attachbtn", 100, "presence");
			waitForSpinnerToDisappear(20);
			jsClick("Configuration.Ratecode.packagecode.closebtn", 100, "presence");
			waitForSpinnerToDisappear(20);
		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*******************************************************************
-  Description: This method helps us to configure a transaction generate for an existing transaction code in Opera Cloud Administration
- Input: transaction code, transaction generate code, 
- Output: Configures a transaction generate for existing code
- Author: Anil Pentam
- Date: 01/17/2019
- Revision History:
                - Change Date
                - Change Reason
                - Changed Behavior
                - Last Changed By
	 ********************************************************************/
	public static void transactionGenerates(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);		

		try {
			// Navigating to Financial Menu
			Utils.click("Configuration.menu_Financial", 100, "clickable");
			// Click on Transaction Management
			Utils.click("Configuration.menu_TransactionManagement", 100, "clickable");
			// Click on Transaction Groups
			Utils.click("Configuration.menu_TransactionCode", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);

			Utils.WebdriverWait(100, "Configuration.TransactionCodeTitle", "presence");			
			System.out.println("Landed in Transaction Codes Page" );
			logger.log(LogStatus.PASS, "Landed in Transaction Codes Page: ");

			Utils.click("Configuration.PropertyTab", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Configuration.PropertyTab_GroupSearch", configMap.get("TRANSACTION_GROUP"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_GroupSearch");
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Configuration.PropertyTab_SubGroupSearch", configMap.get("TRANSACTION_SUBGROUP"), 100, "presence");
			Utils.tabKey("Configuration.PropertyTab_SubGroupSearch");
			Utils.waitForSpinnerToDisappear(10);
			Utils.click("Configuration.TransactionGroupSearchIcon", 100, "clickable");			
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Configuration.TransactionGroupSearchText", configMap.get("TRANSACTION_CODE"), 100, "presence");
			Utils.waitForSpinnerToDisappear(10);
			Utils.jsClick("Configuration.TransactionCodeSearchButton", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);

			if(Utils.isExists("Configuration.TransactionGroupSearch_NOResults")) {
				System.out.println("No Results are displayed");
				logger.log(LogStatus.FAIL, "No Results are displayed");

				//Click on Cancel link
				Utils.click("Configuration.TransactionGroupSearch_CancelLink", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);				
			}
			else {
				if(Utils.isExists("Configuration.NewTransactionSubGroup_SearchResult")) {
					//Fetch Search Grid Column Data and validate
					List<WebElement>  resultRowCount = Utils.elements("Configuration.NewTransactionSubGroup_SearchResult");
					ListIterator<WebElement> itr = null;
					boolean recordFound = false;
					itr = resultRowCount.listIterator();					
					while(itr.hasNext()) {								
						WebElement record = itr.next();
						String code = record.getText().trim();
						System.out.println("Comparing with code:" + code);
						if(code.equalsIgnoreCase(configMap.get("TRANSACTION_CODE"))) {
							recordFound = true;
							Utils.click(record);
							//Click on Select button
							Utils.click("Configuration.NewTransactionSubGroup_SearchSelectButton", 100, "clickable");
							Utils.waitForSpinnerToDisappear(10);
							break;
						}
					}
				}
				Utils.click("Configuration.btn_PropertyTab_Search", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
				Utils.click("Configuration.PropertyTab_TransactionCodeActions", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
				Utils.click("Configuration.PropertyTab_TransactionCodeViewLink", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
				if(Utils.isExists("Configuration.TransactionCode_GeneratesSection", 100, "presence")) {
					//Delete existing generates 
					List <WebElement>  resultRowCount = Utils.elements("Configuration.Generates_ExistingGenerates");
					ListIterator<WebElement> itr = resultRowCount.listIterator();					
					while(itr.hasNext()) {	
						itr.next();
						Utils.click("Configuration.Generates_Actions", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						Utils.click("Configuration.Generates_Delete", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						Utils.click("Configuration.Generates_DeleteButton", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
					}
					Utils.click("Configuration.Generates_NewLink", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					Utils.textBox("Configuration.Generates_Generates_Code", configMap.get("GENERATE_CODE"), 100, "presence");
					Utils.tabKey("Configuration.Generates_Generates_Code");
					Utils.waitForSpinnerToDisappear(10);
					if(configMap.get("NON_TAX_TYPE").equalsIgnoreCase("Y")) {
						if(Utils.isExists("Configuration.Generates_NonTaxTypeRadio"))
							Utils.click("Configuration.Generates_NonTaxTypeRadio", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						if(configMap.get("PERCENTAGE_RADIO").equalsIgnoreCase("Y")) {						
							Utils.click("Configuration.Generates_PercentageRadio", 100, "clickable");
							Utils.waitForSpinnerToDisappear(10);
							Utils.textBox("Configuration.Generates_PercentageEditbox", configMap.get("PERCENTAGE_AMOUNT"), 100, "presence");
							Utils.tabKey("Configuration.Generates_PercentageEditbox");
							Utils.waitForSpinnerToDisappear(10);
							Utils.selectBy("Configuration.Generates_CalculationOn", "text", configMap.get("CALCULATION_ON"));
							Utils.waitForSpinnerToDisappear(10);
						}else if(configMap.get("AMOUNT_RADIO").equalsIgnoreCase("Y")) {
							Utils.click("Configuration.Generates_AmountRadio", 100, "clickable");
							Utils.waitForSpinnerToDisappear(10);
							Utils.textBox("Configuration.Generates_AmountEditbox", configMap.get("AMOUNT_VALUE"), 100, "presence");
							Utils.tabKey("Configuration.Generates_AmountEditbox");
							Utils.waitForSpinnerToDisappear(10);
						}
					}

					Utils.scrolltoElement("Configuration.Generates_SaveButton");
					Utils.click("Configuration.Generates_SaveButton", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
				}else {
					System.out.println("GENERATES section is NOT displayed");
					logger.log(LogStatus.FAIL, "GENERATES section is NOT displayed");
				}
			}


		}catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}

	}

	/*******************************************************************
    - Description: This method create External DataBase
	- Input:Code and Interface
	- Output:
	- Author:Dilip
	- Date: 11/30/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createExternalDataBase(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to ToolBoxMenu
			click("Configuration.ToolBoxMenu",100, "clickable");

			// Navigating to menuSystemSetup
			click("Configuration.ToolBox.menuSystemSetup", 100, "clickable");

			// Navigating to subMenuExternalDatabases
			click("Configuration.ToolBox.SystemSetup.subMenuExternalDatabases", 100, "clickable");

			// Verifying if External Database exists
			waitForSpinnerToDisappear(100);
			//			Utils.verifyCurrentPage("Configuration.header_CopySourceGroup_Validation", "header_CopySourceGroup_Validation");
			Utils.WebdriverWait(100, "Configuration.link_PaymentMethods_New", "presence");
			if (ValidateGridData("Configuration.tableCodeGrid", configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "External Database exists");

			}else{


				// Clicking on New Button
				waitForSpinnerToDisappear(50);
				jsClick("Configuration.link_PaymentMethods_New", 100, "clickable");				

				// Entering the Code

				waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_KeywordTypes_Code",configMap.get("CODE"));

				// Entering the Description
				textBox("Configuration.txt_InterfaceType",configMap.get("INTERFACE_TYPE"), 100, "presence");            
				Utils.tabKey("Configuration.txt_InterfaceType");

				//Wait for element				
				Utils.WebdriverWait(100, "Configuration.label_PropertyCodeLeft", "presence");	
				jsClick("Configuration.label_PropertyCodeLeft", 100, "presence");

				// Clicking on Move arror >
				jsClick("Configuration.link_MoveArrowLTOR", 100, "presence");


				Utils.WebdriverWait(100, "Configuration.label_PropertyCodeRight", "presence");	

				// Clicking on Save Button
				jsClick("Configuration.btn_CreatePaymentMethods_Save", 100, "presence");


				// Verify if External Database is created    
				waitForSpinnerToDisappear(100);
				if (ValidateGridData("Configuration.tableCodeGrid", configMap.get("CODE"))){

					logger.log(LogStatus.PASS, "External Database created");
				} else {

					logger.log(LogStatus.FAIL, "External Database not created");
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create External Database :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
				tearDown();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Create External Database :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}


	/*******************************************************************
    - Description: This method create VIP level
	- Input:Code and Desc
	- Output:
	- Author:Dilip
	- Date: 11/30/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createVIPLevel(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to ClientRelations
			waitForSpinnerToDisappear(10);
			click("Configuration.AdminMenu.ClientRelations", 100, "clickable");

			// Navigating to ProfileManagement
			click("Configuration.AdminMenu_ClinetRelations_ProfileManagement", 100, "clickable");

			// Navigating to VIPLEVEL
			click("Configuration.AdminMenu_ClinetRelations_ProfileManagement_VIPLevel", 100, "clickable");

			// Verifying if VIPLevel exists
			if (ValidateGridData("Configuration.table_VIPLevel_Grid", configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "VIPLevel already exist :" +configMap.get("CODE"));

			}else{

				// Clicking on New Button
				waitForSpinnerToDisappear(50);
				jsClick("Configuration.link_PaymentMethods_New", 100, "clickable");				

				WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "clickable");

				// Entering the Code
				textBox("Configuration.txt_KeywordTypes_Code",configMap.get("CODE"), 100, "presence");

				// Entering the Description
				waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_KeywordTypes_Desc",configMap.get("DESCRIPTION"), 100, "presence");         

				// Clicking on Save Button
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "presence");

				// Verify if VIPLevel is created  
				WebdriverWait(100, "Configuration.link_PaymentMethods_New", "clickable");

				if (ValidateGridData("Configuration.table_VIPLevel_Grid", configMap.get("CODE"))){

					logger.log(LogStatus.PASS, "VIPLevel is created successfully :" +configMap.get("CODE"));
				} else {

					logger.log(LogStatus.FAIL, "VIPLevel is not created successfully :"+configMap.get("CODE"));
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create VIPLevel  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "VIPLevel is not created successfully :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}


	/*******************************************************************
    - Description: This method create title
	- Input:code language, title Number and salutation
	- Output:
	- Author:Dilip
	- Date: 11/30/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createTitle(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to ClientRelations
			waitForSpinnerToDisappear(10);
			click("Configuration.AdminMenu.ClientRelations", 100, "clickable");

			// Navigating to ProfileManagement
			click("Configuration.AdminMenu_ClinetRelations_ProfileManagement", 100, "clickable");

			// Navigating to titles
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
					Utils.element("Configuration.AdminMenu_ClinetRelations_ProfileManagement_Titles"));
			click("Configuration.AdminMenu_ClinetRelations_ProfileManagement_Titles", 100, "clickable");

			// Verifying if title exists
			if (ValidateGridData("Configuration.table_Titles_Grid", configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "Title already exist :" +configMap.get("CODE"));

			}else{

				// Clicking on New Button
				waitForSpinnerToDisappear(50);
				jsClick("Configuration.link_PaymentMethods_New", 100, "clickable");				

				WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "clickable");

				// Entering the Code
				textBox("Configuration.txt_KeywordTypes_Code",configMap.get("CODE"), 100, "presence");

				// Entering the LANGUAGE
				waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_title_language",configMap.get("LANGUAGE"), 100, "presence");   

				// Entering the TITLENUMBER
				waitForSpinnerToDisappear(50);
				Utils.jsTextbox("Configuration.txt_title_Number", configMap.get("TITLENUMBER"));
				logger.log(LogStatus.PASS, "txt title number : " + configMap.get("TITLENUMBER"));

				// Entering the SALUTATION
				waitForSpinnerToDisappear(50);
				Utils.jsTextbox("Configuration.txt_title_Salutation", configMap.get("SALUTATION"));
				logger.log(LogStatus.PASS, "txt_title_Salutation : " + configMap.get("SALUTATION"));


				// Clicking on Save Button
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "presence");

				// Verify if Title is created  
				WebdriverWait(100, "Configuration.link_PaymentMethods_New", "clickable");

				if (ValidateGridData("Configuration.table_Titles_Grid", configMap.get("CODE"))){

					logger.log(LogStatus.PASS, "Title is created successfully :" +configMap.get("CODE"));
				} else {

					logger.log(LogStatus.FAIL, "Title is not created successfully :"+configMap.get("CODE"));
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Title  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Title is not created successfully :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}

	//////////////////////////////////copy reservation active
	public static void copyreservationactive(Map<String, String> configMap) throws Exception{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {

			System.out.println("configmap:: "+configMap);
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu",100,"clickable");
			//click("Configuration.mainMenu",100,"clickable");
			jsClick("Configuration.mainMenu");
			System.out.println("***** Clicked on Main Menu *****");
			logger.log(LogStatus.PASS, "Clicked on Main Menu");

			// Navigating to Administration Menu
			mouseHover("Configuration.AdminstrationMenu",100,"clickable");
			click("Configuration.AdminstrationMenu",100,"clickable");

			// Navigating to Enterprise Menu
			//WebdriverWait(100,"Configuration.administration.MenuEnterprise","presence");
			mouseHover("Configuration.administration.MenuEnterprise",100,"clickable");
			click("Configuration.administration.MenuEnterprise",100,"clickable");

			// Navigating to OPERA Controls
			//mouseHover("Configuration.administration.MenuEnterprise.OperaControls",100,"clickable");
			jsClick("Configuration.administration.MenuEnterprise.OperaControls");
			System.out.println("***** Clicked on OperaControls Menu *****");
			logger.log(LogStatus.PASS, "Clicked on OperaControls Menu");
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Configuration.OperaControls.pageheader", "presence");			
			System.out.println("Landed in opera controls Page" );
			logger.log(LogStatus.PASS, "Landed in opera controls Page");

			// Enter the copy reservation in search text box

			Utils.WebdriverWait(100, "Configuration.copyreservation_lookingfor", "presence");
			Utils.mouseHover("Configuration.copyreservation_lookingfor");

			Utils.textBox("Configuration.copyreservation_lookingfor", configMap.get("Copy_Reservation"));
			System.out.println("provided serach field :" + configMap.get("Copy_Reservation"));
			logger.log(LogStatus.PASS, "Provided search field : " + configMap.get("Copy_Reservation"));
			waitForSpinnerToDisappear(100);
			waitForPageLoad(100);

			//CLICKING ON search button
			Utils.WebdriverWait(100, "Configuration.copyreservation_search", "clickable");
			Utils.jsClick("Configuration.copyreservation_search");
			System.out.println("clicked on search button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Configuration.copyreservation_inactive"))				
			{
				String status=getText("Configuration.copyreservation_inactive");
				//CLICKING ON activate button
				Utils.WebdriverWait(100, "Configuration.copyreservation_inactive", "clickable");
				Utils.jsClick("Configuration.copyreservation_inactive");
				System.out.println("Copy reservation status :"+status);
				logger.log(LogStatus.PASS, "Copy reservation status:" +status);
				System.out.println("clicked on inactive button ");
				Utils.waitForSpinnerToDisappear(100);

				//CLICKING ON activate button
				Utils.WebdriverWait(100, "Configuration.copyreservation_activebutton", "clickable");
				Utils.jsClick("Configuration.copyreservation_activebutton");
				System.out.println("clicked on activate button");
				Utils.waitForSpinnerToDisappear(100);

				if (isExists("Configuration.copyreservation_active"))		
				{
					String status1=getText("Configuration.copyreservation_active");
					Utils.WebdriverWait(100, "Configuration.copyreservation_active", "presence");			
					System.out.println("Now Copy reservation status changed to :"+status1);
					logger.log(LogStatus.PASS, "Now Copy reservation status changed to:" +status1);
				}

			}
			else
			{
				if (isExists("Configuration.copyreservation_active"))		
				{
					String status=getText("Configuration.copyreservation_active");
					Utils.WebdriverWait(100, "Configuration.copyreservation_active", "presence");			
					System.out.println("Copy reservation status :"+status);
					logger.log(LogStatus.PASS, "Copy reservation status:" +status);
				}
			}


		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "copy reservation is not activated :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "copy reservation is not activated :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	public static void createPackageCodes(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {


			//Navigating to Main Menu
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			//Utils.getBusinessDate();

			// Navigating to Financial Menu
			Utils.click("Configuration.menu_Financial", 100, "clickable");

			// Click on Rate Management
			Utils.click("Configuration.RateCategory.menuRateManagement", 100, "clickable");

			// Click on package codes
			Utils.click("Configuration.packages.menuPackageCode", 100, "clickable");
			Utils.waitForSpinnerToDisappear(10);

			Utils.WebdriverWait(100, "Configuration.packages_packagecodeTitle", "presence");			
			System.out.println("Landed in Package Codes Page" );
			logger.log(LogStatus.PASS, "Landed in Package Codes Page");


			//Enter the value for package code		


			waitForPageLoad(100);
			Utils.WebdriverWait(100, "Configuration.packages_code", "presence");
			Utils.mouseHover("Configuration.packages_code");
			System.out.println("Package Code field is avaliable");

			Utils.textBox("Configuration.packages_code", configMap.get("PACKAGE_CODE"));
			System.out.println("Package Code Entered in the field : " + configMap.get("PACKAGE_CODE"));
			logger.log(LogStatus.PASS, "Provided package Code : " + configMap.get("PACKAGE_CODE"));


			//CLICKING ON search button
			Utils.WebdriverWait(100, "Configuration.packages_SearchButton", "clickable");
			Utils.jsClick("Configuration.packages_SearchButton");
			System.out.println("clicked on search button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Configuration.packages_verifypackage"))				
			{
				String packagecode=getText("Configuration.packages_verifypackage");

				Utils.WebdriverWait(100, "Configuration.packages_verifypackage", "presence");			
				System.out.println("Package Code is alraedy avaliable : " + packagecode);
				logger.log(LogStatus.PASS, "Package Code is alraedy avaliable : " + packagecode);

			}

			else
			{
				System.out.println("Package Code is not avaliable : " + configMap.get("PACKAGE_CODE"));
				System.out.println("*******Creation of new package code *********");

				//clicking on new
				Utils.WebdriverWait(100, "Configuration.packages_new", "clickable");
				Utils.jsClick("Configuration.packages_new");
				System.out.println("clicked on NEW button");
				Utils.waitForSpinnerToDisappear(10);


				Utils.WebdriverWait(100, "Configuration.packages_packagecodenew", "presence");			
				System.out.println("Landed in create a new Package Codes Page" );
				logger.log(LogStatus.PASS, "Landed in create a new Package Codes Page");


				//Enter the value for package code		


				waitForPageLoad(100);
				Utils.WebdriverWait(100, "Configuration.packages_code", "presence");
				Utils.mouseHover("Configuration.packages_code");
				System.out.println("Package Code field is avaliable");

				Utils.textBox("Configuration.packages_code", configMap.get("PACKAGE_CODE"));
				System.out.println("Package Code Entered in the field : " + configMap.get("PACKAGE_CODE"));
				logger.log(LogStatus.PASS, "Provided package Code : " + configMap.get("PACKAGE_CODE"));

				//Enter the value for package code DESCRIPTION
				waitForPageLoad(100);
				Utils.WebdriverWait(100, "Configuration.packages_packagecodeDefDescription", "presence");
				Utils.mouseHover("Configuration.packages_packagecodeDefDescription");
				System.out.println("Package Code description field is avaliable");

				Utils.textBox("Configuration.packages_packagecodeDefDescription", configMap.get("PACKAGECODE_DESCRIPTION"));
				System.out.println("Package Code description entered in the field : " + configMap.get("PACKAGECODE_DESCRIPTION"));
				logger.log(LogStatus.PASS, "Provided package Code description" + configMap.get("PACKAGECODE_DESCRIPTION"));

				//Enter the value BEGIN SELL DATE
				waitForPageLoad(100);
				Utils.WebdriverWait(100, "Configuration.packages_beginSelldate", "presence");
				Utils.mouseHover("Configuration.packages_beginSelldate");
				System.out.println("Package Code begin sell date field is avaliable");

				Utils.textBox("Configuration.packages_beginSelldate", configMap.get("BeginSell_Date"));
				System.out.println("Package Code begin sell dtae entered in the field :" + configMap.get("BeginSell_Date"));
				logger.log(LogStatus.PASS, "Provided package Code begin sell dtae " + configMap.get("BeginSell_Date"));

				//Enter the value END SELL DATE
				waitForPageLoad(100);
				Utils.WebdriverWait(100, "Configuration.packages_endSelldate", "presence");
				Utils.mouseHover("Configuration.packages_endSelldate");
				System.out.println("Package Code end sell date field is avaliable");

				Utils.textBox("Configuration.packages_endSelldate", configMap.get("EndSell_Date"));
				System.out.println("Package Code end sell dtae entered in the field : " + configMap.get("EndSell_Date"));
				logger.log(LogStatus.PASS, "Provided package Code end sell date " + configMap.get("EndSell_Date"));


				//Click on Save button
				Utils.WebdriverWait(100, "Configuration.packages_packagecodeDefSave", "clickable");
				Utils.jsClick("Configuration.packages_packagecodeDefSave");
				System.out.println("Clicked on save of package code definetion");
				Utils.waitForSpinnerToDisappear(100);

				//CLICKING ON minimize the package codes definition
				Utils.WebdriverWait(100, "Configuration.packages_packageMiniclick", "clickable");
				Utils.jsClick("Configuration.packages_packageMiniclick");
				System.out.println("minimised the package code definetion");
				Utils.waitForSpinnerToDisappear(100);


				//CLICKING ON TRANSACTION SEARCH
				Utils.WebdriverWait(100, "Configuration.packages_TransactionCode", "clickable");
				Utils.jsClick("Configuration.packages_TransactionCode");
				System.out.println("Clicked on save of transaction code search");
				Utils.waitForSpinnerToDisappear(100);

				// Enter the value for transaction  codes search
				waitForSpinnerToDisappear(100);
				waitForPageLoad(100);
				Utils.WebdriverWait(30, "Configuration.packages_TransactionSearch", "presence");
				Utils.mouseHover("Configuration.packages_TransactionSearch");

				Utils.textBox("Configuration.packages_TransactionSearch", configMap.get("Transactioncode"));
				System.out.println("Transaction Code Entered in transaction search field");
				logger.log(LogStatus.PASS, "Provided transaction Code " + configMap.get("Transactioncode"));

				waitForPageLoad(100);


				//CLICKING ON search button
				Utils.WebdriverWait(100, "Configuration.packages_SearchButton", "clickable");
				Utils.jsClick("Configuration.packages_SearchButton");
				System.out.println("clicked on search button");
				Utils.waitForSpinnerToDisappear(100);

				//selecting the transaction code
				Utils.WebdriverWait(100, "Configuration.packages_transactSelect", "clickable");
				Utils.jsClick("Configuration.packages_transactSelect");
				System.out.println("clicked on selected transaction code");
				Utils.waitForSpinnerToDisappear(100);


				//CLICKING ON select button
				Utils.WebdriverWait(100, "Configuration.packages_SelectButton", "clickable");
				Utils.jsClick("Configuration.packages_SelectButton");
				System.out.println("clicked on select button");
				Utils.waitForSpinnerToDisappear(100);

				//Click on Save button
				Utils.WebdriverWait(100, "Configuration.packages_packagecodeDefSave", "clickable");
				Utils.jsClick("Configuration.packages_packagecodeDefSave");
				System.out.println("Clicked on save of transaction details ");
				Utils.waitForSpinnerToDisappear(100);


				//CLICKING ON minimize the transaction codes 			
				Utils.WebdriverWait(100, "Configuration.packages_transactionMiniclick", "clickable");
				Utils.jsClick("Configuration.packages_transactionMiniclick");
				System.out.println("minimised the transaction details");
				Utils.waitForSpinnerToDisappear(100);


				//CLICKING ON sell separate			
				Utils.WebdriverWait(100, "Configuration.packages_sellSeperate", "clickable");
				Utils.jsClick("Configuration.packages_sellSeperate");
				System.out.println("clicked on sell seperate");
				Utils.waitForSpinnerToDisappear(100);

				//Click on Save button
				Utils.WebdriverWait(100, "Configuration.packages_packagecodeDefSave", "clickable");
				Utils.jsClick("Configuration.packages_packagecodeDefSave");
				System.out.println("Clicked on save of posting attributes ");
				Utils.waitForSpinnerToDisappear(100);

				//CLICKING ON minimize the package codes definition
				Utils.WebdriverWait(100, "Configuration.packages_postingMiniclick", "clickable");
				Utils.jsClick("Configuration.packages_postingMiniclick");
				System.out.println("minimised the posting attributes");
				Utils.waitForSpinnerToDisappear(100);

				// Provide price
				//Utils.WebdriverWait(30, "Configuration.packages_price", "presence");
				//Utils.mouseHover("Configuration.packages_price");
				//System.out.println("Package Price field is avaliable");

				tabKey("Configuration.packages_price");
				Utils.jsClick("Configuration.packages_price");
				Thread.sleep(3000);
				//WebdriverWait(100, "Configuration.packages_price", "stale");
				Utils.jsTextbox("Configuration.packages_price", configMap.get("packagePrice"));
				System.out.println("Provided package Code price :"  + configMap.get("packagePrice"));
				Thread.sleep(3000);
				logger.log(LogStatus.PASS, "Provided package Code price : " + configMap.get("packagePrice"));

				waitForPageLoad(100);

				//Click on Save button
				Utils.WebdriverWait(100, "Configuration.packages_packagecodeDefSave", "clickable");
				Utils.jsClick("Configuration.packages_packagecodeDefSave");
				System.out.println("Clicked on save of package price ");
				Utils.waitForSpinnerToDisappear(100);

				//Click on Save button
				Utils.WebdriverWait(100, "Configuration.packages_packagecodeDefSave", "clickable");
				Utils.jsClick("Configuration.packages_packagecodeDefSave");
				System.out.println("Clicked on save of package code");
				Utils.waitForSpinnerToDisappear(100);

				//verify the package code is created or not

				waitForPageLoad(100);
				Utils.WebdriverWait(100, "Configuration.packages_code", "presence");
				Utils.mouseHover("Configuration.packages_code");
				System.out.println("Package Code field is avaliable");

				Utils.textBox("Configuration.packages_code", configMap.get("PACKAGE_CODE"));
				System.out.println("Package Code Entered in the field : " + configMap.get("PACKAGE_CODE"));
				logger.log(LogStatus.PASS, "Provided package Code : " + configMap.get("PACKAGE_CODE"));


				//CLICKING ON search button
				Utils.WebdriverWait(100, "Configuration.packages_SearchButton", "clickable");
				Utils.jsClick("Configuration.packages_SearchButton");
				System.out.println("clicked on search button");
				Utils.waitForSpinnerToDisappear(100);

				if (isExists("Configuration.packages_verifypackage"))				
				{
					String packagecode=getText("Configuration.packages_verifypackage");

					Utils.WebdriverWait(100, "Configuration.packages_verifypackage", "presence");			
					System.out.println("New Package Code is avaliable : " + packagecode);
					logger.log(LogStatus.PASS, "New Package Code is avaliable : " + packagecode);

				}
				else
				{
					System.out.println("New Package Code is not created ");
					logger.log(LogStatus.PASS, "New Package Code is not cretaed" );
				}

			}
		}

		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create pacakge Code  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "package Code not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);


		}
	}
	/*******************************************************************
	-  Description: Creating Account Type as the part of Configuration 
	- Input: Notes group , code and description
	- Output: Notes type
	- Author: swati
	- Date:01/21/2019
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: 
	 ********************************************************************/


	public static void CreateAccountType(HashMap<String, String> configMap) throws Exception {

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);


		try {

			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration
			click("Configuration.AdminstrationMenu", 100, "clickable");

			Assert.assertEquals(getText("Configuration.MarketGroup.Configuration", 100, "presence"), "OPERA Cloud Administration");
			logger.log(LogStatus.PASS, "Landed in OPERA Cloud Administration page");

			//Navigating to Financial 
			click("Configuration.AdminMenu_Financial");

			//Click Account Receivables Management 
			click("Configuration.AccountRecievable");

			// Navigating to Account Types
			click("Configuration.AccountTypes", 100, "clickable");
			waitForSpinnerToDisappear(50);

			//Validating Account types page
			Assert.assertEquals(getText("Configuration.AccountTypesHeading", 100, "presence"), "Account Types");
			logger.log(LogStatus.PASS, "Landed in Account Types Page");

			//Clicking on search on Account Types page
			click("Configuration.AccountTypesSearch");
			waitForSpinnerToDisappear(50);

			List<WebElement>  rows = Utils.elements("Configuration.AccountTypeTableData"); 
			System.out.println("No of rows are : " + rows.size());

			Boolean flag = false;
			if(rows.size() > 0)
			{

				//Checking if the market group is present in the property or not
				flag = Utils.ValidateGridData("Configuration.AccountTypeTableData", configMap.get("ACCOUNT_TYPE"));
				System.out.println("flag1" + flag);

				if(flag) {
					System.out.println("The Account type "+configMap.get("ACCOUNT_TYPE")+"is already available in the property");
					logger.log(LogStatus.PASS, "The Account type "+configMap.get("ACCOUNT_TYPE")+"is already available in the property");
					flag = true;
				}

			}	

			if(!flag)
			{

				logger.log(LogStatus.PASS, "The Account type is not present, Creating a new one");

				//Create the new Account type
				jsClick("Configuration.AccountTypesNewLink");
				waitForSpinnerToDisappear(20);

				//Provide Account Type
				textBox("Configuration.AccountTypes.Type", configMap.get("ACCOUNT_TYPE"));
				Utils.tabKey("Configuration.AccountTypes.Type");
				waitForSpinnerToDisappear(20);

				//Provide Description
				textBox("Configuration.AccountTypes.Description", configMap.get("Description"));
				Utils.tabKey("Configuration.AccountTypes.Description");
				waitForSpinnerToDisappear(20);


				if(configMap.containsKey("CREDIT_LIMIT")) {
					textBox("Configuration.AccountTypes.CreditLimit", configMap.get("CREDIT_LIMIT"));
					Utils.tabKey("Configuration.AccountTypes.CreditLimit");
					waitForSpinnerToDisappear(20);

				}

				if(configMap.containsKey("REMINDER_CYCLE")) {

					Utils.selectBy("Configuration.AccountTypes.ReminderCycle", "value", configMap.get("REMINDER_CYCLE"));
					Utils.tabKey("Configuration.AccountTypes.ReminderCycle");
					waitForSpinnerToDisappear(20);
				}

				click("Configuration.AccountTypes.StatementLOV");
				//Utils.tabKey("Configuration.AccountTypes.StatementLOV");
				waitForSpinnerToDisappear(20);

				if(Utils.isExists("Configuration.AccountType.StatementLOVTable", 30, "Presence")) {
					click("Configuration.AccountTypes.StatementName");

				}
				else {
					logger.log(LogStatus.FAIL, "Statement name is not configured");
				}
				click("Configuration.AccountTypes.StatementNameSelect");

				waitForSpinnerToDisappear(20);
				Utils.tabKey("Configuration.AccountTypes.StatementLOV");
				waitForSpinnerToDisappear(20);


				if(configMap.containsKey("PRINT_OPTION")) {
					List ll = Utils.elements("Configuration.AccountTypes.PrintingOptions");
					for(int i =0;i<ll.size();i++) {
						String text = Utils.element("Configuration.AccountTypes.PrintingOptions").getText();
						if(text.equals(configMap.get("PRINT_OPTION")))
							Utils.element("Configuration.AccountTypes.PrintingOptions").click();
						break;
					}
				}


				click("Configuration.AccountTypes.SaveButton");
				waitForSpinnerToDisappear(50);

				textBox("Configuration.AccountTypes.AccountType", configMap.get("ACCOUNT_TYPE"));
				Utils.tabKey("Configuration.AccountTypes.AccountType");
				waitForSpinnerToDisappear(20);


				//Clicking on search on Account Types page
				click("Configuration.AccountTypesSearch");
				waitForSpinnerToDisappear(50);

				flag = Utils.ValidateGridData("Configuration.AccountTypeTableData", configMap.get("ACCOUNT_TYPE"));
				System.out.println("flag3" + flag);

				if(flag) {
					System.out.println("Account Type is configured for the property" + configMap.get("ACCOUNT_TYPE"));
					logger.log(LogStatus.PASS, "Account Type is configured for the property->  " + configMap.get("ACCOUNT_TYPE"));

				}
				else {
					logger.log(LogStatus.FAIL, "Account Type is not configured for the property");
				}

			}

		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "configNavigation  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "configNavigation :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}

	/*******************************************************************
	- Description: This method create CommissionCode
	- Input:Code, Description and sequence
	- Output:
	- Author:Dilip
	- Date: 02/02/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createCommissionCode(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Configuration.AdminMenu_Financial
			click("Configuration.AdminMenu_Financial", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement
			click("Configuration.AdminMenu_Financial_CommissionManagement", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement_CommissionCodes
			click("Configuration.AdminMenu_Financial_CommissionManagement_CommissionCodes", 100, "clickable");

			// click on search
			Utils.waitForSpinnerToDisappear(50);
			click("Configuration.btn_Search", 100, "clickable");

			// Verifying if Commission code exists
			Utils.waitForSpinnerToDisappear(50);
			if (ValidateGridData("Configuration.comm_table_Grid", configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "Commission code already exists :" +configMap.get("CODE"));

			}else{

				// Clicking on New Button
				waitForSpinnerToDisappear(50);
				jsClick("Configuration.link_PaymentMethods_New", 100, "clickable");				

				WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "clickable");

				// Entering the Code
				textBox("Configuration.txt_KeywordTypes_Code",configMap.get("CODE"), 100, "presence");


				// Entering the Description
				textBox("Configuration.txt_KeywordTypes_Desc",configMap.get("DESCRIPTION"), 100, "presence");            

				// Entering the Sequence 
				jsTextbox("Configuration.txt_sequence",configMap.get("SEQUENCE")); 

				// Clicking on Save Button
				Utils.waitForSpinnerToDisappear(50);
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "presence");


				// click on search
				Utils.waitForSpinnerToDisappear(50);
				jsClick("Configuration.btn_Search", 100, "clickable");

				if (ValidateGridData("Configuration.comm_table_Grid", configMap.get("CODE"))){


					logger.log(LogStatus.PASS, "Commission code is created successfully :" +configMap.get("CODE"));
				} else {

					logger.log(LogStatus.FAIL, "Commission code is not created successfully :"+configMap.get("CODE"));
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Commission code  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Commission code is not created successfully :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}


	/*******************************************************************
		- Description: This method update CommissionCode
		- Input:Code, Description, Sequence, Amount, FlatAmount, Tax
		- Output:
		- Author:Dilip
		- Date: 02/08/2018
		- Revision History:
		                - Change Date
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By
	 ********************************************************************/

	public static void updateCommissionCode(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Configuration.AdminMenu_Financial
			Utils.waitForSpinnerToDisappear(50);
			click("Configuration.AdminMenu_Financial", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement
			click("Configuration.AdminMenu_Financial_CommissionManagement", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement_CommissionCodes
			click("Configuration.AdminMenu_Financial_CommissionManagement_CommissionCodes", 100, "clickable");

			// Entering the Code
			Utils.waitForSpinnerToDisappear(50);
			Utils.WebdriverWait(100, "Configuration.txt_CreatePaymentMethods_Code", "presence");
			textBox("Configuration.txt_CreatePaymentMethods_Code",configMap.get("CODE"), 100, "presence");

			// click on search
			Utils.waitForSpinnerToDisappear(50);
			jsClick("Configuration.btn_Search", 100, "clickable");	

			// Verifying search results
			Utils.waitForSpinnerToDisappear(150);
			if (getText("Configuration.comm_table_Grid").equals(configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "Search result found before update  :" +configMap.get("CODE"));

				// click on actions
				click("Configuration.RateCode.LinkMore", 100, "clickable");		

				// click on Edit
				click("Configuration.RateCode.Edit", 100, "clickable");	

				Utils.waitForSpinnerToDisappear(50);
				Utils.WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "presence");

				// Entering the Description
				textBox("Configuration.txt_KeywordTypes_Desc",configMap.get("DESCRIPTION"), 100, "presence");            

				// Entering the Sequence 
				Utils.waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_sequence",configMap.get("SEQUENCE")); 
				logger.log(LogStatus.PASS, "txt_sequence " +configMap.get("SEQUENCE"));

				// Entering the Amount
				Utils.waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_amount",configMap.get("FLATAMOUNT"), 100, "presence");            

				// Entering the amount
				textBox("Configuration.txt_flatAmount",configMap.get("AMOUNT"), 100, "presence"); 

				// Entering the Sequence
				Utils.waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_tax",configMap.get("TAX")); 
				logger.log(LogStatus.PASS, "Configuration.txt_tax " +configMap.get("TAX"));

				// Clicking on Save Button
				Utils.waitForSpinnerToDisappear(50);
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "presence");

				// Entering the Code
				Utils.waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_CreatePaymentMethods_Code",configMap.get("CODE"), 100, "presence");

				// click on search
				jsClick("Configuration.btn_Search", 100, "clickable");	

				// Verifying search results
				Utils.waitForSpinnerToDisappear(150);
				if (getText("Configuration.comm_table_Grid").equals(configMap.get("CODE"))){

					logger.log(LogStatus.PASS, "Search result found after update :" +configMap.get("CODE"));
				}else{

					logger.log(LogStatus.FAIL, "Search result not found after update  :" +configMap.get("CODE"));
				}

				// click on actions
				click("Configuration.RateCode.LinkMore", 100, "clickable");		

				// click on Edit
				click("Configuration.RateCode.Edit", 100, "clickable");	

				//	Validate Description				
				Utils.WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "presence");

				if (getTextValue("Configuration.txt_KeywordTypes_Desc").equals(configMap.get("DESCRIPTION"))) {

					logger.log(LogStatus.PASS,"Description updated successfully Excepted >>"+configMap.get("DESCRIPTION")+"<< Actual >>"+getTextValue("Configuration.txt_KeywordTypes_Desc")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"Description is not updated successfully Excepted >>"+configMap.get("DESCRIPTION")+"<< Actual >>"+getTextValue("Configuration.txt_KeywordTypes_Desc")+"<<");
				}

				//	Validate Sequence
				if (getTextValue("Configuration.txt_sequence").equals(configMap.get("SEQUENCE"))) {

					logger.log(LogStatus.PASS,"Sequence updated successfully Excepted >>"+configMap.get("SEQUENCE")+"<< Actual >>"+getTextValue("Configuration.txt_sequence")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"Sequence is not updated successfully Excepted >>"+configMap.get("SEQUENCE")+"<< Actual >>"+getTextValue("Configuration.txt_sequence")+"<<");
				}

				//	Validate Amount
				if (getTextValue("Configuration.txt_amount").equals(configMap.get("AMOUNT"))) {

					logger.log(LogStatus.PASS,"Amount updated successfully Excepted >>"+configMap.get("AMOUNT")+"<< Actual >>"+getTextValue("Configuration.txt_amount")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"Amount is not updated successfully Excepted >>"+configMap.get("AMOUNT")+"<< Actual >>"+getTextValue("Configuration.txt_amount")+"<<");
				}

				//	Validate FLATAMOUNT
				if (getTextValue("Configuration.txt_flatAmount").equals(configMap.get("FLATAMOUNT"))) {

					logger.log(LogStatus.PASS,"FlatAmount updated successfully Excepted >>"+configMap.get("FLATAMOUNT")+"<< Actual >>"+getTextValue("Configuration.txt_flatAmount")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"FlatAmount is not updated successfully Excepted >>"+configMap.get("FLATAMOUNT")+"<< Actual >>"+getTextValue("Configuration.txt_flatAmount")+"<<");
				}

				//	Validate TAXT
				if (getTextValue("Configuration.txt_tax").equals(configMap.get("TAX"))) {

					logger.log(LogStatus.PASS,"Tax updated successfully Excepted >>"+configMap.get("TAX")+"<< Actual >>"+getTextValue("Configuration.txt_tax")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"Tax is not updated successfully Excepted >>"+configMap.get("TAX")+"<< Actual >>"+getTextValue("Configuration.txt_tax")+"<<");
				}


			}else{

				logger.log(LogStatus.FAIL, "Search result found before update  :" +configMap.get("CODE"));
			}



		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Update Commission code  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "update Commission code is not success :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}


	/*******************************************************************
	- Description: This method delete CommissionCode
	- Input:Code
	- Output:
	- Author:Dilip
	- Date: 02/08/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void deleteCommissionCode(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Configuration.AdminMenu_Financial
			Utils.waitForSpinnerToDisappear(50);
			click("Configuration.AdminMenu_Financial", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement
			click("Configuration.AdminMenu_Financial_CommissionManagement", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement_CommissionCodes
			click("Configuration.AdminMenu_Financial_CommissionManagement_CommissionCodes", 100, "clickable");

			// Entering the Code
			Utils.waitForSpinnerToDisappear(50);
			Utils.WebdriverWait(100, "Configuration.txt_CreatePaymentMethods_Code", "presence");
			textBox("Configuration.txt_CreatePaymentMethods_Code",configMap.get("CODE"), 100, "presence");

			// click on search
			Utils.waitForSpinnerToDisappear(50);
			jsClick("Configuration.btn_Search", 100, "clickable");	

			// Verifying search results
			Utils.waitForSpinnerToDisappear(150);
			if (getText("Configuration.comm_table_Grid").equals(configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "Search result found before update  :" +configMap.get("CODE"));

				// click on actions
				click("Configuration.RateCode.LinkMore", 100, "clickable");		

				// click on delete
				click("Configuration.Generates_Delete", 100, "clickable");	

				Utils.waitForSpinnerToDisappear(100);
				if(getText("Configuration.confirmationMsg").equals("Are you sure you want to Delete this record?")){

					logger.log(LogStatus.PASS, "Confirmation validated");
				}else{

					logger.log(LogStatus.FAIL, "Confirmation not validated");
				}

				// click on delete
				click("Configuration.btn_delete", 100, "clickable");	

				//	Validate record deleted or not
				Utils.waitForSpinnerToDisappear(100);
				if(getText("Configuration.label_nodata").equals("No data to display")){

					logger.log(LogStatus.PASS, "Record deleted successfully :"+configMap.get("CODE"));
				}else{

					logger.log(LogStatus.FAIL, "Record deleted not successfully :"+configMap.get("CODE"));
				}


			}else{

				logger.log(LogStatus.FAIL, "Search result found not found  :" +configMap.get("CODE"));
			}



		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Delete Commission code  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Delete Commission code is not success :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}




	/*******************************************************************
		- Description: This method create bank account form commission
		- Input:Code, Description, BankCode, Account No, RoutingNo, PaymentMethod, CheckReport,Currency and Next CheckNo
		- Output:
		- Author:Dilip
		- Date: 02/07/2018
		- Revision History:
		                - Change Date
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By
	 ********************************************************************/

	public static void createBankAccount(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Configuration.AdminMenu_Financial
			click("Configuration.AdminMenu_Financial", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement
			click("Configuration.AdminMenu_Financial_CommissionManagement", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement_BankAccount
			click("Configuration.AdminMenu_Financial_CommissionManagement_BankAccount", 100, "clickable");


			// Verifying if bank account exists
			Utils.waitForSpinnerToDisappear(50);
			if (ValidateGridData("Configuration.comm_table_Grid", configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "Bank account already exists :" +configMap.get("CODE"));

			}else{

				// Clicking on New Button
				waitForSpinnerToDisappear(50);
				jsClick("Configuration.link_PaymentMethods_New", 100, "clickable");				

				// wait for page to load						
				WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "clickable");

				// Entering the Code
				textBox("Configuration.txt_KeywordTypes_Code",configMap.get("CODE"), 100, "presence");

				// Entering the Description
				textBox("Configuration.txt_KeywordTypes_Desc",configMap.get("DESCRIPTION"), 100, "presence");            

				// Entering the bank code
				waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_BankBranchCode",configMap.get("BANKCODE"), 100, "presence");

				// Entering the bank account no
				waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_BankAccountNo",configMap.get("ACCOUNTNO"), 100, "presence");

				// Entering the Routing No
				waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_BankRoutingNo",configMap.get("ROUTINGNO"), 100, "presence");

				// Entering the Payment Method
				waitForSpinnerToDisappear(50);
				textBox("Configuration.txt_BankPaymentMethod",configMap.get("PAYMENTMETHOD"), 100, "presence");
				Utils.tabKey("Configuration.txt_BankPaymentMethod");		

				// Entering the check report
				waitForSpinnerToDisappear(100);
				jsTextbox("Configuration.txt_BankCheckReport",configMap.get("CHECKREPORT"));
				logger.log(LogStatus.PASS, "txt_BankCheckReport :" +configMap.get("CHECKREPORT"));

				// Entering the Currency
				waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_BankCurrency",configMap.get("CURRENCY"));
				logger.log(LogStatus.PASS, "txt_BankCheckReport :" +configMap.get("CURRENCY"));

				// Entering the Next Check Number 
				Utils.waitForSpinnerToDisappear(100);
				jsTextbox("Configuration.txt_NextNumberCheck",configMap.get("NEXTCHECKNUMBER"));
				logger.log(LogStatus.PASS, "txt_NextNumberCheck :" +configMap.get("NEXTCHECKNUMBER"));

				// Clicking on Save Button
				Utils.waitForSpinnerToDisappear(50);
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "presence");

				// Validate bank account
				Utils.waitForSpinnerToDisappear(100);						
				if (ValidateGridData("Configuration.comm_table_Grid", configMap.get("CODE"))){

					logger.log(LogStatus.PASS, "Bank account is created successfully :" +configMap.get("CODE"));
				} else {

					logger.log(LogStatus.FAIL, "Bank account  is not created successfully :"+configMap.get("CODE"));
				}

			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create bank account  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "bank account is not created successfully :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}

	/*******************************************************************
	- Description: This method update bank account form commission
	- Input:Code, Description, BankCode, Account No, RoutingNo, PaymentMethod, CheckReport,Currency and Next CheckNo
	- Output:
	- Author:Dilip
	- Date: 02/07/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void updateBankAccount(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);	 
		System.out.println("Map: " + configMap);

		try {
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu", 100, "clickable");
			click("Configuration.mainMenu", 100, "clickable");

			// Navigating to Administration Menu
			click("Configuration.AdminstrationMenu",100, "clickable");

			// Navigating to Configuration.AdminMenu_Financial
			Utils.waitForSpinnerToDisappear(50);
			click("Configuration.AdminMenu_Financial", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement
			click("Configuration.AdminMenu_Financial_CommissionManagement", 100, "clickable");

			// Navigating to AdminMenu_Financial_CommissionManagement_BankAccount
			click("Configuration.AdminMenu_Financial_CommissionManagement_BankAccount", 100, "clickable");

			// Entering the Code
			Utils.waitForSpinnerToDisappear(50);
			Utils.WebdriverWait(100, "Configuration.txt_CreatePaymentMethods_Code", "presence");
			jsTextbox("Configuration.txt_CreatePaymentMethods_Code",configMap.get("CODE"));
			logger.log(LogStatus.PASS, "txt_CreatePaymentMethods_Code :" +configMap.get("CODE"));


			// click on search
			Utils.waitForSpinnerToDisappear(50);
			jsClick("Configuration.btn_Search", 100, "clickable");	

			// Verifying search results
			Utils.waitForSpinnerToDisappear(150);
			if (getText("Configuration.comm_table_Grid").equals(configMap.get("CODE"))){

				logger.log(LogStatus.PASS, "Search result found before update  :" +configMap.get("CODE"));

				// click on actions
				click("Configuration.RateCode.LinkMore", 100, "clickable");		

				// click on Edit
				click("Configuration.RateCode.Edit", 100, "clickable");	

				// wait for page to load						
				WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "clickable");

				// Entering the Description
				waitForSpinnerToDisappear(100);
				jsTextbox("Configuration.txt_KeywordTypes_Desc",configMap.get("DESCRIPTION"));
				logger.log(LogStatus.PASS, "txt_KeywordTypes_Desc :" +configMap.get("DESCRIPTION"));	      

				// Entering the bank account no
				waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_BankAccountNo",configMap.get("ACCOUNTNO"));
				logger.log(LogStatus.PASS, "txt_BankAccountNo :" +configMap.get("ACCOUNTNO"));	 

				// Entering the Routing No
				waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_BankRoutingNo",configMap.get("ROUTINGNO"));
				logger.log(LogStatus.PASS, "txt_BankRoutingNo :" +configMap.get("ROUTINGNO"));	 

				// Entering the Payment Method
				waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_BankPaymentMethod",configMap.get("PAYMENTMETHOD"));
				logger.log(LogStatus.PASS, "txt_BankPaymentMethod :" +configMap.get("PAYMENTMETHOD"));	
				Utils.tabKey("Configuration.txt_BankPaymentMethod");		

				// Entering the check report
				waitForSpinnerToDisappear(100);
				jsTextbox("Configuration.txt_BankCheckReport",configMap.get("CHECKREPORT"));
				logger.log(LogStatus.PASS, "txt_BankCheckReport :" +configMap.get("CHECKREPORT"));

				// Entering the Currency
				waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_BankCurrency",configMap.get("CURRENCY"));
				logger.log(LogStatus.PASS, "txt_BankCheckReport :" +configMap.get("CURRENCY"));

				// Entering the Next Check Number 
				Utils.waitForSpinnerToDisappear(100);
				jsTextbox("Configuration.txt_NextNumberCheck",configMap.get("NEXTCHECKNUMBER"));
				logger.log(LogStatus.PASS, "txt_NextNumberCheck :" +configMap.get("NEXTCHECKNUMBER"));

				// Clicking on Save Button
				Utils.waitForSpinnerToDisappear(50);
				jsClick("Configuration.btn_ResevationTypes_Save", 100, "presence");

				// Entering the Code
				Utils.waitForSpinnerToDisappear(50);
				jsTextbox("Configuration.txt_CreatePaymentMethods_Code",configMap.get("CODE"));
				logger.log(LogStatus.PASS, "txt_CreatePaymentMethods_Code :" +configMap.get("CODE"));


				// click on search
				jsClick("Configuration.btn_Search", 100, "clickable");	

				// Verifying search results
				Utils.waitForSpinnerToDisappear(150);
				if (getText("Configuration.comm_table_Grid").equals(configMap.get("CODE"))){

					logger.log(LogStatus.PASS, "Search result found after update :" +configMap.get("CODE"));
				}else{

					logger.log(LogStatus.FAIL, "Search result not found after update  :" +configMap.get("CODE"));
				}

				// click on actions
				click("Configuration.RateCode.LinkMore", 100, "clickable");		

				// click on Edit
				click("Configuration.RateCode.Edit", 100, "clickable");	

				//	Validate Description				
				Utils.WebdriverWait(100, "Configuration.btn_ResevationTypes_Save", "presence");


				//Validate Code label				
				if (getText("Configuration.txt_KeywordTypes_Code").equals(configMap.get("CODE"))) {

					logger.log(LogStatus.PASS,"Code validation Excepted >>"+configMap.get("CODE")+"<< Actual >>"+getText("Configuration.txt_KeywordTypes_Code")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"Code validation Excepted >>"+configMap.get("CODE")+"<< Actual >>"+getText("Configuration.txt_KeywordTypes_Code")+"<<");
				}


				//Validate Description	
				if (getTextValue("Configuration.txt_KeywordTypes_Desc").equals(configMap.get("DESCRIPTION"))) {

					logger.log(LogStatus.PASS,"Description updated successfully Excepted >>"+configMap.get("DESCRIPTION")+"<< Actual >>"+getTextValue("Configuration.txt_KeywordTypes_Desc")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"Description is not updated successfully Excepted >>"+configMap.get("DESCRIPTION")+"<< Actual >>"+getTextValue("Configuration.txt_KeywordTypes_Desc")+"<<");
				}

				//	Validate BankAccountNo
				System.out.println("ACCOUNTNO updated successfully Excepted >>"+configMap.get("ACCOUNTNO")+"<< Actual >>"+getTextValue("Configuration.txt_BankAccountNo")+"<<");
				if (getTextValue("Configuration.txt_BankAccountNo").equals(configMap.get("ACCOUNTNO"))) {

					logger.log(LogStatus.PASS,"ACCOUNTNO updated successfully Excepted >>"+configMap.get("ACCOUNTNO")+"<< Actual >>"+getTextValue("Configuration.txt_BankAccountNo")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"ACCOUNTNO is not updated successfully Excepted >>"+configMap.get("ACCOUNTNO")+"<< Actual >>"+getTextValue("Configuration.txt_BankAccountNo")+"<<");
				}

				//	Validate BankRoutingNo
				if (getTextValue("Configuration.txt_BankRoutingNo").equals(configMap.get("ROUTINGNO"))) {

					logger.log(LogStatus.PASS,"ROUTINGNO updated successfully Excepted >>"+configMap.get("ROUTINGNO")+"<< Actual >>"+getTextValue("Configuration.txt_BankRoutingNo")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"ROUTINGNO is not updated successfully Excepted >>"+configMap.get("ROUTINGNO")+"<< Actual >>"+getTextValue("Configuration.txt_BankRoutingNo")+"<<");
				}

				//	Validate PAYMENT METHOD
				if (getTextValue("Configuration.txt_BankPaymentMethod").equals(configMap.get("PAYMENTMETHOD"))) {

					logger.log(LogStatus.PASS,"PAYMENTMETHOD updated successfully Excepted >>"+configMap.get("PAYMENTMETHOD")+"<< Actual >>"+getTextValue("Configuration.txt_BankPaymentMethod")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"PAYMENTMETHOD is not updated successfully Excepted >>"+configMap.get("PAYMENTMETHOD")+"<< Actual >>"+getTextValue("Configuration.txt_BankPaymentMethod")+"<<");
				}

				//	Validate NEXTCHECKNUMBER
				if (getTextValue("Configuration.txt_NextNumberCheck").equals(configMap.get("NEXTCHECKNUMBER"))) {

					logger.log(LogStatus.PASS,"NEXTCHECKNUMBER updated successfully Excepted >>"+configMap.get("NEXTCHECKNUMBER")+"<< Actual >>"+getTextValue("Configuration.txt_NextNumberCheck")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"NEXTCHECKNUMBER is not updated successfully Excepted >>"+configMap.get("NEXTCHECKNUMBER")+"<< Actual >>"+getTextValue("Configuration.txt_NextNumberCheck")+"<<");
				}

				//	Validate CURRENCY
				if (getTextValue("Configuration.txt_BankCurrency").equals(configMap.get("CURRENCY"))) {

					logger.log(LogStatus.PASS,"CURRENCY updated successfully Excepted >>"+configMap.get("CURRENCY")+"<< Actual >>"+getTextValue("Configuration.txt_BankCurrency")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"CURRENCY is not updated successfully Excepted >>"+configMap.get("CURRENCY")+"<< Actual >>"+getTextValue("Configuration.txt_BankCurrency")+"<<");
				}

				//	Validate CHECKREPORT
				if (getTextValue("Configuration.txt_BankCheckReport").equals(configMap.get("CHECKREPORT"))) {

					logger.log(LogStatus.PASS,"CHECKREPORT updated successfully Excepted >>"+configMap.get("CHECKREPORT")+"<< Actual >>"+getTextValue("Configuration.txt_BankCheckReport")+"<<");

				} else {

					logger.log(LogStatus.FAIL,"CHECKREPORT is not updated successfully Excepted >>"+configMap.get("CHECKREPORT")+"<< Actual >>"+getTextValue("Configuration.txt_BankCheckReport")+"<<");
				}


			}else{

				logger.log(LogStatus.FAIL, "Search result not found before update  :" +configMap.get("CODE"));
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "update bank account  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "bank account is not updated successfully :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}
	
/*******************************************************************
	-  Description: Create Service Request Code in Administration
	- Input:Service Request Code, Description and Department
	- Output: New Service Request Code created
	- Author: Praneeth
	- Date: 01/03/19
	- Revision History:
********************************************************************/
public static void createServiceRequestCode(HashMap<String, String> configMap) throws Exception {
	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	System.out.println("Map: " + configMap);
	try {

		// Navigating to Main Menu
		mouseHover("Configuration.mainMenu");
		jsClick("Configuration.mainMenu",100,"Clickable");

		// Navigating to Administration
		mouseHover("Configuration.AdminstrationMenu");
		jsClick("Configuration.AdminstrationMenu",100,"Clickable");

		// Navigating to Enterprise menu
		mouseHover("Configuration.AdminMenu.Enterprise");
		click("Configuration.AdminMenu.Enterprise",100,"Clickable");

		// Navigating to Chain & Property Management
		mouseHover("Configuration.AdminMenu.ChainandPropertyManagement");
		click("Configuration.AdminMenu.ChainandPropertyManagement",100,"clickable");

		// Navigating to Service Request Codes menu
		Utils.scrolltoElement("Configuration.menu_ServiceRequestCodes");
		click("Configuration.menu_ServiceRequestCodes", 100, "clickable");
		waitForSpinnerToDisappear(10);
		
		//Check if Application is navigated to Service Request Codes Screen
		boolean blnPageExists = isExists("Configuration.hdr_ServiceRequestCodes");
		if(blnPageExists){
			logger.log(LogStatus.PASS, "Navigate to Service Request Codes screen");

			//Verify if the data already exists 
			boolean blnDataExists = Utils.ValidateGridData("Configuration.grid_ServiceRequestCodes", configMap.get("SRC_CODE"));
			if(blnDataExists){
				logger.log(LogStatus.PASS, "Service Request Code already exists on screen");
			}
			else
			{
				waitForPageLoad(300);
				//Click on New
				click("Configuration.ServiceRequestCodes.link_New",100, "clickable");
				waitForSpinnerToDisappear(50);

				//Provide SRC Code
				click("Configuration.ServiceRequestCodes.txt_Code",100,"clickable");
				waitForSpinnerToDisappear(10);
				textBox("Configuration.ServiceRequestCodes.txt_Code",configMap.get("SRC_CODE"));   //SRC Code
				Utils.tabKey("Configuration.ServiceRequestCodes.txt_Code");
				waitForSpinnerToDisappear(50);

				//Provided Description
				click("Configuration.ServiceRequestCodes.txt_Description",100,"clickable");
				waitForSpinnerToDisappear(10);
				textBox("Configuration.ServiceRequestCodes.txt_Description",configMap.get("SRC_DESCRIPTION"));   //SRC Description
				Utils.tabKey("Configuration.ServiceRequestCodes.txt_Description");
				waitForSpinnerToDisappear(50);
				
				//Provided Department
				click("Configuration.ServiceRequestCodes.txt_Department",100,"clickable");
				waitForSpinnerToDisappear(10);
				textBox("Configuration.ServiceRequestCodes.txt_Department",configMap.get("SRC_DEPARTMENT"));   //SRC Description
				Utils.tabKey("Configuration.ServiceRequestCodes.txt_Description");
				waitForSpinnerToDisappear(50);

				//Select Save
				click("Configuration.btnSave",100,"clickable");
				waitForSpinnerToDisappear(50);

				//Verify if the created Service Request Code Exists
				boolean blnDataExistsAfterCreate = Utils.ValidateGridData("Configuration.grid_ServiceRequestCodes", configMap.get("SRC_CODE"));
				if(blnDataExistsAfterCreate){
					logger.log(LogStatus.PASS, "Service Request Code exists on screen after creation");
				}else{
					logger.log(LogStatus.FAIL, "****Issue observed on Service Request Codes creation****");
				}
			}
		}else{
			logger.log(LogStatus.FAIL, "***Issue observed on navigation to Service Request Codes screen***");
		}

	}catch (Exception e) {
		e.printStackTrace();
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Tasks code not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}
}	

/*******************************************************************
-  Description: Create Service Request Priority in Administration
- Input:Service Request Priority, Description
- Output: New Service Request Priority created
- Author: Praneeth
- Date: 06/03/19
- Revision History:
********************************************************************/
public static void createServiceRequestPriority(HashMap<String, String> configMap) throws Exception {
String ScriptName = Utils.getMethodName();
System.out.println("name: " + ScriptName);
System.out.println("Map: " + configMap);
try {

	// Navigating to Main Menu
	mouseHover("Configuration.mainMenu");
	jsClick("Configuration.mainMenu",100,"Clickable");

	// Navigating to Administration
	mouseHover("Configuration.AdminstrationMenu");
	jsClick("Configuration.AdminstrationMenu",100,"Clickable");

	// Navigating to Enterprise menu
	mouseHover("Configuration.AdminMenu.Enterprise");
	click("Configuration.AdminMenu.Enterprise",100,"Clickable");

	// Navigating to Chain & Property Management
	mouseHover("Configuration.AdminMenu.ChainandPropertyManagement");
	click("Configuration.AdminMenu.ChainandPropertyManagement",100,"clickable");

	// Navigating to Service Request Priorities menu
	Utils.scrolltoElement("Configuration.menu_ServiceRequestPriorities");
	click("Configuration.menu_ServiceRequestPriorities", 100, "clickable");
	waitForSpinnerToDisappear(10);
	
	//Check if Application is navigated to Service Request Priorities Screen
	boolean blnPageExists = isExists("Configuration.hdr_ServiceRequestPriorities");
	if(blnPageExists){
		logger.log(LogStatus.PASS, "Navigate to Service Request Priorities screen");

		//Verify if the data already exists 
		boolean blnDataExists = Utils.ValidateGridData("Configuration.grid_ServiceRequestPriorities", configMap.get("SRP_CODE"));
		if(blnDataExists){
			logger.log(LogStatus.PASS, "Service Request Priority already exists on screen");
		}
		else
		{
			waitForPageLoad(300);
			//Click on New
			click("Configuration.ServiceRequestPriorities.link_New",100, "clickable");
			waitForSpinnerToDisappear(50);

			//Provide SRP Code
			click("Configuration.ServiceRequestPriorities.txt_Code",100,"clickable");
			waitForSpinnerToDisappear(10);
			textBox("Configuration.ServiceRequestPriorities.txt_Code",configMap.get("SRP_CODE"));   //SRP Code
			Utils.tabKey("Configuration.ServiceRequestPriorities.txt_Code");
			waitForSpinnerToDisappear(50);

			//Provided Description
			click("Configuration.ServiceRequestPriorities.txt_Description",100,"clickable");
			waitForSpinnerToDisappear(10);
			textBox("Configuration.ServiceRequestPriorities.txt_Description",configMap.get("SRP_DESCRIPTION"));   //SRP Description
			Utils.tabKey("Configuration.ServiceRequestPriorities.txt_Description");
			waitForSpinnerToDisappear(50);

			//Select Save
			click("Configuration.btnSave",100,"clickable");
			waitForSpinnerToDisappear(50);

			//Verify if the created Service Request Code Exists
			boolean blnDataExistsAfterCreate = Utils.ValidateGridData("Configuration.grid_ServiceRequestPriorities", configMap.get("SRP_CODE"));
			if(blnDataExistsAfterCreate){
				logger.log(LogStatus.PASS, "Service Request Priority exists on screen after creation");
			}else{
				logger.log(LogStatus.FAIL, "****Issue observed on Service Request Priority creation****");
			}
		}
	}else{
		logger.log(LogStatus.FAIL, "***Issue observed on navigation to Service Request Priority screen***");
	}

}catch (Exception e) {
	e.printStackTrace();
	Utils.takeScreenshot(driver, ScriptName);
	logger.log(LogStatus.FAIL, "Tasks code not created :: Failed " + e.getLocalizedMessage());
	logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
	logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
	throw (e);

}
}

public static void cancellationReason(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {


		//Navigating to Main Menu
		click("Configuration.mainMenu", 100, "clickable");

		// Navigating to Administration
		click("Configuration.AdminstrationMenu", 100, "clickable");
		
		//Utils.getBusinessDate();
		
		// Navigating to booking Menu
		Utils.click("Configuration.menu_Booking", 100, "clickable");

		// Click on reservation Management
		Utils.click("Configuration.menu_BookingReservationmanagment", 100, "clickable");

		// Click on cancel reason 
		Utils.click("Configuration.Booking_cancelreason", 100, "clickable");
		Utils.waitForSpinnerToDisappear(10);

		Utils.WebdriverWait(100, "Configuration.cancelreason_title", "presence");			
		System.out.println("Landed in Cancellation reason  Page" );
		logger.log(LogStatus.PASS, "Landed in Cancellation reason Page");

		
		//Enter the value for cancel reason code		
		
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.cancelreason_code", "presence");
		Utils.mouseHover("Configuration.cancelreason_code");
		System.out.println("Package Code field is avaliable");
		
		Utils.textBox("Configuration.cancelreason_code", configMap.get("CANCEL_CODE"));
		System.out.println("Cancel reason Code Entered in the field : " + configMap.get("CANCEL_CODE"));
		logger.log(LogStatus.PASS, "Cancel reason  Code : " + configMap.get("CANCEL_CODE"));
		

		//CLICKING ON search button
		Utils.WebdriverWait(100, "Configuration.packages_SearchButton", "clickable");
		Utils.jsClick("Configuration.packages_SearchButton");
		System.out.println("clicked on search button");
		Utils.waitForSpinnerToDisappear(100);
		
		if (isExists("Configuration.cancelreason_verifycancelcode"))				
		{
			String cancelreason=getText("Configuration.cancelreason_verifycancelcode");
			
			Utils.WebdriverWait(100, "Configuration.cancelreason_verifycancelcode", "presence");			
			System.out.println("Cancelation reason is alraedy avaliable : " + cancelreason);
			logger.log(LogStatus.PASS, "Cancelation reason is alraedy avaliable : " + cancelreason);
			
		}
		
		else
		{
			System.out.println("Cancelation reason is not avaliable : " + configMap.get("CANCEL_CODE"));
			System.out.println("*******Creation of new cancel reasion *********");
			
		//clicking on new
		Utils.WebdriverWait(100, "Configuration.packages_new", "clickable");
		Utils.jsClick("Configuration.packages_new");
		System.out.println("clicked on NEW button");
		Utils.waitForSpinnerToDisappear(10);
		

		Utils.WebdriverWait(100, "Configuration.cancelreason_cancelpage", "presence");			
		System.out.println("Landed in managecancellation reason Page" );
		logger.log(LogStatus.PASS, "Landed in managecancellation reason  Page");
		
		
		//Enter the value for cancel reason
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.cancelreason_cancelcode", "presence");
		Utils.mouseHover("Configuration.cancelreason_cancelcode");
		System.out.println("cancel reason Code field is avaliable");
		
		Utils.textBox("Configuration.cancelreason_cancelcode", configMap.get("CANCEL_CODE"));
		System.out.println("Package Code Entered in the field : " + configMap.get("CANCEL_CODE"));
		logger.log(LogStatus.PASS, "Provided cancel reason Code : " + configMap.get("CANCEL_CODE"));

		//Enter the value for cancel reason DESCRIPTION
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.cancelreason_cancelcodedesc", "presence");
		Utils.mouseHover("Configuration.cancelreason_cancelcodedesc");
		System.out.println("Cancel reason  description field is avaliable");
		
		Utils.textBox("Configuration.cancelreason_cancelcodedesc", configMap.get("CancelReason_Description"));
		System.out.println("Cancel reason description entered in the field : " + configMap.get("CancelReason_Description"));
		logger.log(LogStatus.PASS, "Cancel reason  description" + configMap.get("CancelReason_Description"));
		
		//Enter the value BEGIN SELL DATE
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.packages_beginSelldate", "presence");
		Utils.mouseHover("Configuration.packages_beginSelldate");
		System.out.println("Package Code begin sell date field is avaliable");
		
		Utils.textBox("Configuration.packages_beginSelldate", configMap.get("BeginSell_Date"));
		System.out.println("Package Code begin sell dtae entered in the field :" + configMap.get("BeginSell_Date"));
		logger.log(LogStatus.PASS, "Provided package Code begin sell dtae " + configMap.get("BeginSell_Date"));
		
		//Click on Save button
		Utils.WebdriverWait(100, "Configuration.packages_packagecodeDefSave", "clickable");
		Utils.jsClick("Configuration.packages_packagecodeDefSave");
		System.out.println("Clicked on save of package code definetion");
		Utils.waitForSpinnerToDisappear(100);

		
		
		if (isExists("Configuration.cancelreason_verifycancelcode"))				
		{
			String packagecode=getText("Configuration.cancelreason_verifycancelcode");
			
			Utils.WebdriverWait(100, "Configuration.cancelreason_verifycancelcode", "presence");			
			System.out.println("New cancel reason Code is avaliable : " + packagecode);
			logger.log(LogStatus.PASS, "New cancel reason Code is avaliable : " + packagecode);
			
		}
		else
		{
			System.out.println("New cancel reason Code is not created ");
			logger.log(LogStatus.PASS, "New cancel reason Code is not cretaed" );
		}
		
	}
	}
	
	catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Create cancel reason Code  :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "cancel reason Code not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);
	

	}
}




public static void CreateDepartment(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {


		//Navigating to Main Menu
		click("Configuration.mainMenu", 100, "clickable");

		// Navigating to Administration
		click("Configuration.AdminstrationMenu", 100, "clickable");
		waitForPageLoad(100);
//		Navigating to enterprise
		Utils.click("Configuration.AdminMenu.Enterprise");
		
//					// Navigating to Chain and Property Management Menu
		Utils.click("Configuration.AdminMenu.ChainandPropertyManagement", 100, "clickable");

		// Click on Department
		Utils.mouseHover("Configuration.Department");
		Utils.jsClick("Configuration.Department", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);
		
		//Validating Departments page
		Utils.WebdriverWait(100, "Configuration.DepartmentHeading", "presence");	
		logger.log(LogStatus.PASS, "Landed in Departments Page");
		System.out.println("Landed in Departments Page");
		
		//Enter the value for Department		
		
		
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.Department_LOV", "presence");
		Utils.mouseHover("Configuration.Department_LOV");
		Utils.jsClick("Configuration.Department_LOV");
		System.out.println("Clicked on Department LOV");
		Utils.waitForSpinnerToDisappear(100);
		
		Utils.textBox("Configuration.Department_value", configMap.get("DEPARTMENT_CODE"));
		System.out.println("Department Code Entered in the field : " + configMap.get("DEPARTMENT_CODE"));
		logger.log(LogStatus.PASS, "Provided Department Code : " + configMap.get("DEPARTMENT_CODE"));
		

		//CLICKING ON search button
		Utils.WebdriverWait(100, "Configuration.Department_search", "clickable");
		Utils.jsClick("Configuration.Department_search");
		System.out.println("clicked on search button");
		Utils.waitForSpinnerToDisappear(50);
		
		if (isExists("Configuration.Department_exists"))				
		{
			String Department=getText("Configuration.Department_exists");
			
			Utils.WebdriverWait(100, "Configuration.Department_exists", "presence");			
			System.out.println("Department  is alraedy avaliable : " + Department);
			logger.log(LogStatus.PASS, "Departmnet is alraedy avaliable : " + Department);
			
			Utils.jsClick("Configuration.Department_cancel", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
		}
		
		else
		{
			Utils.jsClick("Configuration.Department_cancel", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			
			System.out.println("Department Code is not avaliable : " + configMap.get("DEPARTMENT_CODE"));
			System.out.println("*******Creation of new Department code *********");
			
		//clicking on new
		Utils.WebdriverWait(100, "Configuration.packages_new", "clickable");
		Utils.jsClick("Configuration.packages_new");
		System.out.println("clicked on NEW button");
		Utils.waitForSpinnerToDisappear(10);
		
		Utils.WebdriverWait(100, "Configuration.DepartmentHeading", "presence");			
		System.out.println("Landed in Departments Codes Page" );
		logger.log(LogStatus.PASS, "Landed in Departments Codes Page");
		
		
		//Enter the value for Department		
		
		
	
		Utils.WebdriverWait(100, "Configuration.Department_code", "presence");
		Utils.mouseHover("Configuration.Department_code");
		System.out.println("Department Code field is avaliable");
		
		Utils.textBox("Configuration.Department_code", configMap.get("DEPARTMENT_CODE"));
		System.out.println("Department Code Entered in the field : " + configMap.get("DEPARTMENT_CODE"));
		logger.log(LogStatus.PASS, "Provided Department Code : " + configMap.get("DEPARTMENT_CODE"));

		//Enter the value for Department code DESCRIPTION
		
		Utils.WebdriverWait(100, "Configuration.Department_Description", "presence");
		Utils.mouseHover("Configuration.Department_Description");
		System.out.println("Department description field is avaliable");
		
		Utils.textBox("Configuration.Department_Description", configMap.get("DEPARTMENT_DESCRIPTION"));
		System.out.println("Departmnet description entered in the field : " + configMap.get("DEPARTMENT_DESCRIPTION"));
		logger.log(LogStatus.PASS, "Provided Departmnet Code description" + configMap.get("DEPARTMENT_DESCRIPTION"));
		
		//Save
		Utils.click("Configuration.Department_save",100,"clickable");
		Utils.waitForSpinnerToDisappear(100);
		

		//verify the department is created or not
		
		Utils.WebdriverWait(100, "Configuration.Departmentsearch", "presence");
		Utils.mouseHover("Configuration.Departmentsearch");
		System.out.println("Department Code field is avaliable");
		
		Utils.textBox("Configuration.Departmentsearch", configMap.get("DEPARTMENT_CODE"));
		System.out.println("Department Code Entered in the field : " + configMap.get("DEPARTMENT_CODE"));
		logger.log(LogStatus.PASS, "Provided Department Code : " + configMap.get("DEPARTMENT_CODE"));

		//CLICKING ON search button
		Utils.WebdriverWait(100, "Configuration.packages_SearchButton", "clickable");
		Utils.jsClick("Configuration.packages_SearchButton");
		System.out.println("clicked on search button");
		Utils.waitForSpinnerToDisappear(100);
		
		if (isExists("Configuration.Department_verify"))				
		{
			Utils.WebdriverWait(100, "Configuration.Department_verify", "presence");			
			System.out.println("New Department is created");
			logger.log(LogStatus.PASS, "New Department is created" );
			
		}
		else
		{
			System.out.println("New Department is not created ");
			logger.log(LogStatus.PASS, "New Department is not cretaed" );
		}
		
	}
	}
	
	catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Create Department   :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Department not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);
	}

	}



//////////////////////////////////Traces active
public static void tracesActive(Map<String, String> configMap) throws Exception{
	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {

		System.out.println("configmap:: "+configMap);
		// Navigating to Main Menu
		mouseHover("Configuration.mainMenu",100,"clickable");
		//click("Configuration.mainMenu",100,"clickable");
		jsClick("Configuration.mainMenu");
		System.out.println("***** Clicked on Main Menu *****");
		logger.log(LogStatus.PASS, "Clicked on Main Menu");

		// Navigating to Administration Menu
		mouseHover("Configuration.AdminstrationMenu",100,"clickable");
		click("Configuration.AdminstrationMenu",100,"clickable");

		// Navigating to Enterprise Menu
		//WebdriverWait(100,"Configuration.administration.MenuEnterprise","presence");
		mouseHover("Configuration.administration.MenuEnterprise",100,"clickable");
		click("Configuration.administration.MenuEnterprise",100,"clickable");

		// Navigating to OPERA Controls
		//mouseHover("Configuration.administration.MenuEnterprise.OperaControls",100,"clickable");
		jsClick("Configuration.administration.MenuEnterprise.OperaControls");
		System.out.println("***** Clicked on OperaControls Menu *****");
		logger.log(LogStatus.PASS, "Clicked on OperaControls Menu");
		waitForSpinnerToDisappear(100);
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.OperaControls.pageheader", "presence");			
		System.out.println("Landed in opera controls Page" );
		logger.log(LogStatus.PASS, "Landed in opera controls Page");

		// Enter the copy reservation in search text box

		Utils.WebdriverWait(100, "Configuration.Traces_lookingfor", "presence");
		Utils.mouseHover("Configuration.Traces_lookingfor");

		Utils.textBox("Configuration.Traces_lookingfor", configMap.get("Trace_Name"));
		System.out.println("provided serach field :" + configMap.get("Trace_Name"));
		logger.log(LogStatus.PASS, "Provided search field : " + configMap.get("Trace_Name"));
		waitForSpinnerToDisappear(100);
		waitForPageLoad(100);

		//CLICKING ON search button
		Utils.WebdriverWait(100, "Configuration.Traces_search", "clickable");
		Utils.jsClick("Configuration.Traces_search");
		System.out.println("clicked on search button");
		Utils.waitForSpinnerToDisappear(100);

		if (isExists("Configuration.Traces_inactive"))				
		{
			String status=getText("Configuration.Traces_inactive");
			//CLICKING ON activate button
			Utils.WebdriverWait(100, "Configuration.Traces_inactive", "clickable");
			Utils.jsClick("CConfiguration.Traces_inactive");
			System.out.println("Traces status :"+status);
			logger.log(LogStatus.PASS, "Traces status:" +status);
			System.out.println("clicked on inactive button ");
			Utils.waitForSpinnerToDisappear(100);

			//CLICKING ON activate button
			Utils.WebdriverWait(100, "Configuration.Traces_activebutton", "clickable");
			Utils.jsClick("CConfiguration.Traces_activebutton");
			System.out.println("clicked on activate button");
			Utils.waitForSpinnerToDisappear(100);

			if (isExists("Configuration.Traces_active"))		
			{
				String status1=getText("Configuration.Traces_active");
				Utils.WebdriverWait(100, "Configuration.Traces_active", "presence");			
				System.out.println("Now Traces status changed to :"+status1);
				logger.log(LogStatus.PASS, "Now Traces status changed to:" +status1);
			}

		}
		else
		{
			if (isExists("Configuration.Traces_active"))		
			{
				String status=getText("Configuration.Traces_active");
				Utils.WebdriverWait(100, "Configuration.Traces_active", "presence");			
				System.out.println("Traces :"+status);
				logger.log(LogStatus.PASS, "Traces status:" +status);
			}
		}


	}
	catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Traces are not activated :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Traces are not activated :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}
}

public static void createAlertMessage(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	try {
		// Navigating to Main Menu
		mouseHover("Configuration.mainMenu", 100, "clickable");
		jsClick("Configuration.mainMenu", 100, "clickable");

		// Clicking on Administration Button
		jsClick("Configuration.AdminstrationMenu", 100, "clickable");

		// Clicked on Bookings Menu
		jsClick("Configuration.menu_Booking", 100,"presence");

		// Clicked on Alert
		jsClick("Configuration.alert", 100,"presence");

		// Clicked on Alert messages
		jsClick("Configuration.alert_messages", 100,"presence");
		
		//clicking on template
		Utils.WebdriverWait(100, "Configuration.alert_template", "clickable");
		Utils.jsClick("Configuration.alert_template");
		System.out.println("clicked on template");
		Utils.waitForSpinnerToDisappear(10);
		
		//CLICKING ON code LOV
		Utils.WebdriverWait(100, "Configuration.code_LOV", "clickable");
		Utils.jsClick("Configuration.code_LOV");
		System.out.println("clicked on code LOV");
		Utils.waitForSpinnerToDisappear(100);
						
		//Enter alert message code
		jsTextbox("Configuration.code_value", configMap.get("ALERT_MESSAGE"));
		logger.log(LogStatus.PASS, "Entered alert code: " + configMap.get("ALERT_MESSAGE"));
		System.out.println("Entered alert code: " + configMap.get("ALERT_MESSAGE"));

		//CLICKING ON SEARCH
		Utils.WebdriverWait(100, "Configuration.code_search", "clickable");
		Utils.jsClick("Configuration.code_search");
		System.out.println("clicked on Search");
		Utils.waitForSpinnerToDisappear(100);
		
		if (isExists("Configuration.alert_code_select"))				
		{
			Utils.WebdriverWait(100, "Configuration.alert_code_select", "presence");			
			System.out.println("seleted the alert code which is alraedy avaliable ");
			logger.log(LogStatus.PASS, "seleted the alert code which is alraedy avaliable");
			
			Utils.jsClick("Configuration.code_cancel", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
		}
		else
		{
		
			Utils.jsClick("Configuration.code_cancel", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);

			System.out.println("Alert code is not avaliable : " + configMap.get("ALERT_MESSAGE"));
			System.out.println("*******Creation of new alert code*********");

			//clicking on new
			Utils.WebdriverWait(100, "Configuration.packages_new", "clickable");
			Utils.jsClick("Configuration.packages_new");
			System.out.println("clicked on NEW button");
			Utils.waitForSpinnerToDisappear(10);

			//Enter alert message code in template
			jsTextbox("Configuration.alert_template_code", configMap.get("ALERT_MESSAGE"));
			logger.log(LogStatus.PASS, "Entered alert code in template: " + configMap.get("ALERT_MESSAGE"));
			System.out.println("Entered alert code in template: " + configMap.get("ALERT_MESSAGE"));

			//Enter alert message code description in template
			jsTextbox("Configuration.alert_template_description", configMap.get("ALERT_MSG_DESCRIPTION"));
			logger.log(LogStatus.PASS, "Entered alert code in template: " + configMap.get("ALERT_MSG_DESCRIPTION"));
			System.out.println("Entered alert code in template: " + configMap.get("ALERT_MSG_DESCRIPTION"));

			//clicking on save
			Utils.WebdriverWait(100, "Configuration.alert_save", "clickable");
			Utils.jsClick("Configuration.alert_save");
			System.out.println("clicked on save in Template");
			Utils.waitForSpinnerToDisappear(10);
		}
		
		//CLICKING ON property tab
		Utils.WebdriverWait(100, "Configuration.alert_property", "clickable");
		Utils.jsClick("Configuration.alert_property");
		System.out.println("clicked on property tab");
		Utils.waitForSpinnerToDisappear(100);
		
		//CLICKING ON code LOV
		Utils.WebdriverWait(100, "Configuration.code_LOV", "clickable");
		Utils.jsClick("Configuration.code_LOV");
		System.out.println("clicked on code LOV");
		Utils.waitForSpinnerToDisappear(100);
		
		
		//Enter alert code value
		jsTextbox("Configuration.code_value", configMap.get("ALERT_MESSAGE"));
		logger.log(LogStatus.PASS, "Entered alert code: " + configMap.get("ALERT_MESSAGE"));
		System.out.println("Entered alert code: " + configMap.get("ALERT_MESSAGE"));

		//CLICKING ON SEARCH
		Utils.WebdriverWait(100, "Configuration.code_search", "clickable");
		Utils.jsClick("Configuration.code_search");
		System.out.println("clicked on Search");
		Utils.waitForSpinnerToDisappear(100);
		
		if (isExists("Configuration.code_exists"))				
		{
			String AlertMsg=getText("Configuration.code_exists");
			
			Utils.WebdriverWait(100, "Configuration.code_exists", "presence");			
			System.out.println("Alert is alraedy avaliable : " + AlertMsg);
			logger.log(LogStatus.PASS, "Alert is alraedy avaliable : " + AlertMsg);
			
			Utils.jsClick("Configuration.code_cancel", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
		}
		
		else
		{
			Utils.jsClick("Configuration.code_cancel", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			
			System.out.println("Alert message is not avaliable in property tab : " + configMap.get("ALERT_MESSAGE"));
			System.out.println("*******Creation of new alert Message in property tab *********");
			
			
			//clicking on new
			Utils.WebdriverWait(100, "Configuration.packages_new", "clickable");
			Utils.jsClick("Configuration.packages_new");
			System.out.println("clicked on NEW button");
			Utils.waitForSpinnerToDisappear(10);
			
			//CLICKING ON code LOV
			Utils.WebdriverWait(100, "Configuration.code_LOV", "clickable");
			Utils.jsClick("Configuration.code_LOV");
			System.out.println("clicked on code LOV");
			Utils.waitForSpinnerToDisappear(100);
			
			//Enter alert message code
			jsTextbox("Configuration.code_value", configMap.get("ALERT_MESSAGE"));
			logger.log(LogStatus.PASS, "Entered alert code: " + configMap.get("ALERT_MESSAGE"));
			System.out.println("Entered alert code: " + configMap.get("ALERT_MESSAGE"));
			
			//CLICKING ON SEARCH
			Utils.WebdriverWait(100, "Configuration.code_search", "clickable");
			Utils.jsClick("Configuration.code_search");
			System.out.println("clicked on Search");
			Utils.waitForSpinnerToDisappear(100);
			
			if (isExists("Configuration.alert_propertycode_verify"))				
			{
				Utils.WebdriverWait(100, "Configuration.alert_code_select", "presence");	
				Utils.jsClick("Configuration.alert_code_select");
				System.out.println("seleted the alert code which is alraedy avaliable ");
				logger.log(LogStatus.PASS, "seleted the alert code which is alraedy avaliable");
				
				Utils.jsClick("Configuration.alert_propertycode_select", 100, "clickable");
				System.out.println("clicked on select button");
				Utils.waitForSpinnerToDisappear(100);
				
				//Enter alert message code description in property
				jsTextbox("Configuration.alert_template_description", configMap.get("ALERT_MSG_DESCRIPTION"));
				logger.log(LogStatus.PASS, "Entered alert description in property: " + configMap.get("ALERT_MSG_DESCRIPTION"));
				System.out.println("Entered alert description in property: " + configMap.get("ALERT_MSG_DESCRIPTION"));
				
				//clicking on save
				Utils.WebdriverWait(100, "Configuration.alert_save", "clickable");
				Utils.jsClick("Configuration.alert_save");
				System.out.println("clicked on save in property");
				Utils.waitForSpinnerToDisappear(10);
			}
			else
			{
				Utils.jsClick("Configuration.code_cancel", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
				
				System.out.println("Alert message is not created in property tab : " + configMap.get("ALERT_MESSAGE"));
				logger.log(LogStatus.PASS, "Alert message is not created in property tab : " + configMap.get("ALERT_MESSAGE"));
				
								
			}
			
		}
		
	}
	
			catch (Exception e) {
				try {
					Alert alert = driver.switchTo().alert();
					String alertText = alert.getText();
					logger.log(LogStatus.FAIL, "Alert Messages are not created :: Failed " + alertText);
					logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
					System.out.println("Alert data: " + alertText);
					alert.accept();
				} catch (NoAlertPresentException ex) {
					ex.printStackTrace();
				}
				Utils.takeScreenshot(driver, ScriptName);
				logger.log(LogStatus.FAIL, "Alert Messages are not created :: Failed " + e.getLocalizedMessage());
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				throw (e);

			}

}



public static void createGlobalAlertDefinitions(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	try {
		// Navigating to Main Menu
		mouseHover("Configuration.mainMenu", 100, "clickable");
		jsClick("Configuration.mainMenu", 100, "clickable");

		// Clicking on Administration Button
		jsClick("Configuration.AdminstrationMenu", 100, "clickable");

		// Clicked on Bookings Menu
		jsClick("Configuration.menu_Booking", 100,"presence");

		// Clicked on Alert
		jsClick("Configuration.alert", 100,"presence");

		// Clicked on Alert messages
		jsClick("Configuration.global_alert", 100,"presence");
		
		//landed in global alert definition page
		Utils.WebdriverWait(100, "Configuration.global_alert_Heading", "presence");
		System.out.println("landed in global alert definition page");
		logger.log(LogStatus.PASS, "landed in global alert definition page");
					
		//clicked on alert  lov
		Utils.WebdriverWait(100, "Configuration.global_alert_LOV", "clickable");
		Utils.jsClick("Configuration.global_alert_LOV");
		System.out.println("clicked on alert LOV");
		logger.log(LogStatus.PASS, "clicked on alert LOV");
		Utils.waitForSpinnerToDisappear(10);
					
		//enter the value in search field
		jsTextbox("Configuration.global_alert_code_value", configMap.get("ALERT_MESSAGE"));
		logger.log(LogStatus.PASS, "Entered alert code: " + configMap.get("ALERT_MESSAGE"));
		System.out.println("Entered alert code: " + configMap.get("ALERT_MESSAGE"));
		
		//clicked on search button
		Utils.WebdriverWait(100, "Configuration.global_alert_code_search", "clickable");
		Utils.jsClick("Configuration.global_alert_code_search");
		System.out.println("clicked on search button");
		logger.log(LogStatus.PASS, "clicked on search button");
		Utils.waitForSpinnerToDisappear(10);	
		

		if (isExists("Configuration.global_alert_exists"))				
		{
			String AlertMsg=getText("Configuration.global_alert_exists");
			
			Utils.WebdriverWait(100, "Configuration.global_alert_exists", "presence");		
			Utils.jsClick("Configuration.global_alert_exists");
			System.out.println("selected alert code : " + AlertMsg);
			logger.log(LogStatus.PASS, "selected alert code : " + AlertMsg);
			
			//click on select button
			Utils.jsClick("Configuration.global_alert_code_selectbutton", 100, "clickable");
			System.out.println("clicked on select button");
			Utils.waitForSpinnerToDisappear(100);
			
			//click on search button
			Utils.jsClick("Configuration.global_alert_search", 100, "clickable");
			System.out.println("clicked on search button");
			logger.log(LogStatus.PASS, "clicked on search button");
			Utils.waitForSpinnerToDisappear(100);
			
			if (isExists("Configuration.global_alert_verify"))				
			{
				Utils.WebdriverWait(100, "Configuration.global_alert_verify", "presence");	
				logger.log(LogStatus.PASS, "global alert definition is already avaliable: " + configMap.get("ALERT_MESSAGE"));
				System.out.println("global alert definition is already avaliable: " + configMap.get("ALERT_MESSAGE"));
			}
			else
			{
				
				System.out.println("global alert definition is not avaliable");
				System.out.println("**** Creating global alert definition *****");
				logger.log(LogStatus.PASS, "global alert definition is not avaliable ");
				logger.log(LogStatus.PASS, "**** Creating global alert definition *****");
				
				//clicked on new button
				Utils.WebdriverWait(100, "Configuration.packages_new", "clickable");
				Utils.jsClick("Configuration.packages_new");
				System.out.println("clicked on NEW button");
				logger.log(LogStatus.PASS, "clicked on NEW button");
				Utils.waitForSpinnerToDisappear(10);
				
				//clicked on alert code LOV
				Utils.WebdriverWait(100, "Configuration.global_alert_code_LOV", "clickable");
				Utils.jsClick("Configuration.global_alert_code_LOV");
				System.out.println("clicked on alert code LOV");
				logger.log(LogStatus.PASS, "clicked on alert code LOV");
				Utils.waitForSpinnerToDisappear(10);
				
				//enter the value in search field of code
				jsTextbox("Configuration.global_alert_code_value", configMap.get("ALERT_MESSAGE"));
				logger.log(LogStatus.PASS, "Entered alert code: " + configMap.get("ALERT_MESSAGE"));
				System.out.println("Entered alert code: " + configMap.get("ALERT_MESSAGE"));
				
				//click on search button of code
				Utils.jsClick("Configuration.global_alert_code_search", 100, "clickable");
				System.out.println("clicked on global alert code search button");
				logger.log(LogStatus.PASS, "clicked on global alert code search button");
				Utils.waitForSpinnerToDisappear(100);
				
				if (isExists("Configuration.global_alert_code_exists"))				
				{
					Utils.WebdriverWait(100, "Configuration.global_alert_code_exists", "presence");		
					Utils.jsClick("Configuration.global_alert_code_exists");
					System.out.println("selected alert definition code ");
					logger.log(LogStatus.PASS, "selected alert definition code");
					
					//click on select button
					Utils.jsClick("Configuration.global_alert_code_selectbutton", 100, "clickable");
					System.out.println("clicked on select button");
					Utils.waitForSpinnerToDisappear(100);
					
						
					//clicked on alert code LOV
					Utils.WebdriverWait(100, "Configuration.global_alert_area_LOV", "clickable");
					Utils.jsClick("Configuration.global_alert_area_LOV");
					System.out.println("clicked on alert area LOV");
					logger.log(LogStatus.PASS, "clicked on alert area LOV");
					Utils.waitForSpinnerToDisappear(10);
					
					//enter the value in search field of area
					jsTextbox("Configuration.global_alert_area_value", configMap.get("ALERT_AREA"));
					logger.log(LogStatus.PASS, "Entered alert area: " + configMap.get("ALERT_AREA"));
					System.out.println("Entered alert area: " + configMap.get("ALERT_AREA"));
					
					//click on search button of area
					Utils.jsClick("Configuration.global_alert_area_search", 100, "clickable");
					System.out.println("clicked on global alert area search button");
					logger.log(LogStatus.PASS, "clicked on global alert area search button");
					Utils.waitForSpinnerToDisappear(100);
					
					if (isExists("Configuration.global_alert_area_exists"))				
					{
						Utils.WebdriverWait(100, "Configuration.global_alert_area_exists", "presence");		
						Utils.jsClick("Configuration.global_alert_area_exists");
						System.out.println("selected alert definition area ");
						logger.log(LogStatus.PASS, "selected alert definition area");
						
						//click on select button
						Utils.jsClick("Configuration.global_alert_area_selectbutton", 100, "clickable");
						System.out.println("clicked on select button of area");
						Utils.waitForSpinnerToDisappear(100);
						
						//clicked on add
						Utils.WebdriverWait(100, "Configuration.global_alert_add", "clickable");
						Utils.jsClick("Configuration.global_alert_add");
						System.out.println("clicked on add to set condition");
						logger.log(LogStatus.PASS, "clicked on add to set condition");
						Utils.waitForSpinnerToDisappear(10);
						
						//clicked on attribute
						new Actions(driver).moveToElement(Utils.element("Configuration.global_alert_attribute")).perform();
						Utils.selectBy("Configuration.global_alert_attribute", "text", configMap.get("ALERT_ATTRIBUTE"));
						System.out.println("Attribute is Selected as : Adults");
						logger.log(LogStatus.PASS, "Attribute is Selected as:" + configMap.get("ALERT_ATTRIBUTE"));
						waitForSpinnerToDisappear(100);
						waitForPageLoad(100);
						
						//clicked on operator
						new Actions(driver).moveToElement(Utils.element("Configuration.global_alert_operator")).perform();
						Utils.selectBy("Configuration.global_alert_operator", "text", configMap.get("ALERT_OPERATOR"));
						System.out.println("operator is Selected as : Greater than or Equal To");
						logger.log(LogStatus.PASS, "operator is Selected as:" + configMap.get("ALERT_OPERATOR"));
						waitForSpinnerToDisappear(100);
						Thread.sleep(1000);
						//enter first value
						jsTextbox("Configuration.global_alert_firstvalue", configMap.get("ALERT_FIRST_VALUE"));
						logger.log(LogStatus.PASS, "Entered first value: " + configMap.get("ALERT_FIRST_VALUE"));
						System.out.println("Entered first value: " + configMap.get("ALERT_FIRST_VALUE"));
						
						//clicked on ok
						Utils.WebdriverWait(100, "Configuration.global_alert_ok", "clickable");
						Utils.jsClick("Configuration.global_alert_ok");
						System.out.println("clicked on ok");
						logger.log(LogStatus.PASS, "clicked on ok");
						Utils.waitForSpinnerToDisappear(10);
					
						//clicked on save
						Utils.WebdriverWait(100, "Configuration.global_alert_save", "clickable");
						Utils.jsClick("Configuration.global_alert_save");
						System.out.println("clicked on save");
						logger.log(LogStatus.PASS, "clicked on save");
						Utils.waitForSpinnerToDisappear(10);
				}
					else
					{
						System.out.println("alert area is not found");
						logger.log(LogStatus.FAIL, "alert area is not found");
						
						Utils.jsClick("Configuration.global_alert_area_cancel", 100, "clickable");
						//Utils.jsClick("Configuration.global_alert_code_cancel", 100, "clickable");
						System.out.println("clicked on Cancel button of alert area");
					}
				}
				else{
					
					System.out.println("alert code is not found");
					logger.log(LogStatus.FAIL, "alert code is not found");
					
					Utils.jsClick("Configuration.global_alert_code_cancel", 100, "clickable");
					System.out.println("clicked on Cancel button of alert code");
					
				}
				
			}
		}
		else
		{
			Utils.jsClick("Configuration.global_alert_cancel", 100, "clickable");
			Utils.waitForSpinnerToDisappear(100);
			
			System.out.println("Alert code is not avaliable : " + configMap.get("ALERT_MESSAGE"));
			System.out.println("Given alert code is not avaliable");
			System.out.println("please create the alert code under alert messages");
			logger.log(LogStatus.FAIL, "please create the alert code under alert messages");
			
		}
		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Alert Messages are not created :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Alert Messages are not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
}



//////////////////////////////////active Alerts
public static void activateAlerts(Map<String, String> configMap) throws Exception{
	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {

		System.out.println("configmap:: "+configMap);
		// Navigating to Main Menu
		mouseHover("Configuration.mainMenu",100,"clickable");
		//click("Configuration.mainMenu",100,"clickable");
		jsClick("Configuration.mainMenu");
		System.out.println("***** Clicked on Main Menu *****");
		logger.log(LogStatus.PASS, "Clicked on Main Menu");

		// Navigating to Administration Menu
		mouseHover("Configuration.AdminstrationMenu",100,"clickable");
		click("Configuration.AdminstrationMenu",100,"clickable");

		// Navigating to Enterprise Menu
		//WebdriverWait(100,"Configuration.administration.MenuEnterprise","presence");
		mouseHover("Configuration.administration.MenuEnterprise",100,"clickable");
		click("Configuration.administration.MenuEnterprise",100,"clickable");

		// Navigating to OPERA Controls
		//mouseHover("Configuration.administration.MenuEnterprise.OperaControls",100,"clickable");
		jsClick("Configuration.administration.MenuEnterprise.OperaControls");
		System.out.println("***** Clicked on OperaControls Menu *****");
		logger.log(LogStatus.PASS, "Clicked on OperaControls Menu");
		waitForSpinnerToDisappear(100);
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.OperaControls.pageheader", "presence");			
		System.out.println("Landed in opera controls Page" );
		logger.log(LogStatus.PASS, "Landed in opera controls Page");

		// Enter the copy reservation in search text box

		Utils.WebdriverWait(100, "Configuration.Alerts_lookingfor", "presence");
		Utils.mouseHover("Configuration.Alerts_lookingfor");

		Utils.textBox("Configuration.Alerts_lookingfor", configMap.get("Alert_Name"));
		System.out.println("provided serach field :" + configMap.get("Alert_Name"));
		logger.log(LogStatus.PASS, "Provided search field : " + configMap.get("Alert_Name"));
		waitForSpinnerToDisappear(100);
		waitForPageLoad(100);

		//CLICKING ON search button
		Utils.WebdriverWait(100, "Configuration.Alerts_search", "clickable");
		Utils.jsClick("Configuration.Alerts_search");
		System.out.println("clicked on search button");
		Utils.waitForSpinnerToDisappear(100);

		if (isExists("Configuration.Alerts_inactive"))				
		{
			String status=getText("Configuration.Alerts_inactive");
			//CLICKING ON activate button
			Utils.WebdriverWait(100, "Configuration.Alerts_inactive", "clickable");
			Utils.jsClick("Configuration.Alerts_inactive");
			System.out.println("Alerts status :"+status);
			logger.log(LogStatus.PASS, "Alerts status:" +status);
			System.out.println("clicked on inactive button ");
			Utils.waitForSpinnerToDisappear(100);

			//CLICKING ON activate button
			Utils.WebdriverWait(100, "Configuration.Alerts_activebutton", "clickable");
			Utils.jsClick("Configuration.Alerts_activebutton");
			System.out.println("clicked on activate button");
			Utils.waitForSpinnerToDisappear(100);
			
			
			if (isExists("Configuration.Alerts_active"))		
			{
				String status1=getText("Configuration.active");
				Utils.WebdriverWait(100, "Configuration.active", "presence");			
				System.out.println("Now Alerts status changed to :"+status1);
				logger.log(LogStatus.PASS, "Now Alerts status changed to:" +status1);
			}

		}
		else
		{
			if (isExists("Configuration.Alerts_active"))		
			{
				String status=getText("Configuration.active");
				Utils.WebdriverWait(100, "Configuration.active", "presence");			
				System.out.println("Alerts :"+status);
				logger.log(LogStatus.PASS, "Alerts status:" +status);
			}
		}
		if (isExists("Configuration.Alerts_active_child_inactivate"))				
		{
			// clicking on alert pop up inactive
			Utils.WebdriverWait(100, "Configuration.Alerts_active_child_inactivate", "clickable");
			Utils.jsClick("Configuration.Alerts_active_child_inactivate");
			System.out.println("clicked on inactive button of pop-up alerts");
			Utils.waitForSpinnerToDisappear(100);

			// clicking on alert pop up active
			Utils.WebdriverWait(100, "Configuration.Alerts_active_child_turnon", "clickable");
			Utils.jsClick("Configuration.Alerts_active_child_turnon");
			System.out.println("Now pop-up Alerts status changed to : Active");
			logger.log(LogStatus.PASS, "Now pop-up Alerts status changed to : Active");
			Utils.waitForSpinnerToDisappear(100);
		}
		else
		{
			Utils.WebdriverWait(100, "Configuration.Alerts_active_child_activate", "presence");			
			System.out.println("pop-up Alerts status is Active");
			logger.log(LogStatus.PASS, "pop-up Alerts status is Active");
		}

	}
	catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Alerts are not activated :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Alerts are not activated :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}
}


//////////////////////////////////active pop-up Alerts
public static void deactivatepopupAlerts(Map<String, String> configMap) throws Exception{
	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {

		System.out.println("configmap:: "+configMap);
		// Navigating to Main Menu
		mouseHover("Configuration.mainMenu",100,"clickable");
		//click("Configuration.mainMenu",100,"clickable");
		jsClick("Configuration.mainMenu");
		System.out.println("***** Clicked on Main Menu *****");
		logger.log(LogStatus.PASS, "Clicked on Main Menu");

		// Navigating to Administration Menu
		mouseHover("Configuration.AdminstrationMenu",100,"clickable");
		click("Configuration.AdminstrationMenu",100,"clickable");

		// Navigating to Enterprise Menu
		//WebdriverWait(100,"Configuration.administration.MenuEnterprise","presence");
		mouseHover("Configuration.administration.MenuEnterprise",100,"clickable");
		click("Configuration.administration.MenuEnterprise",100,"clickable");

		// Navigating to OPERA Controls
		//mouseHover("Configuration.administration.MenuEnterprise.OperaControls",100,"clickable");
		jsClick("Configuration.administration.MenuEnterprise.OperaControls");
		System.out.println("***** Clicked on OperaControls Menu *****");
		logger.log(LogStatus.PASS, "Clicked on OperaControls Menu");
		waitForSpinnerToDisappear(100);
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Configuration.OperaControls.pageheader", "presence");			
		System.out.println("Landed in opera controls Page" );
		logger.log(LogStatus.PASS, "Landed in opera controls Page");

		// Enter the copy reservation in search text box

		Utils.WebdriverWait(100, "Configuration.Alerts_lookingfor", "presence");
		Utils.mouseHover("Configuration.Alerts_lookingfor");

		Utils.textBox("Configuration.Alerts_lookingfor", configMap.get("Alert_Name"));
		System.out.println("provided serach field :" + configMap.get("Alert_Name"));
		logger.log(LogStatus.PASS, "Provided search field : " + configMap.get("Alert_Name"));
		waitForSpinnerToDisappear(100);
		waitForPageLoad(100);

		//CLICKING ON search button
		Utils.WebdriverWait(100, "Configuration.Alerts_search", "clickable");
		Utils.jsClick("Configuration.Alerts_search");
		System.out.println("clicked on search button");
		Utils.waitForSpinnerToDisappear(100);

		if (isExists("Configuration.Alerts_active"))				
		{
			
		if(isExists("Configuration.Alerts_active_child_activate"))			
		{
			String status=getText("Configuration.Alerts_active_child_activate");
			//CLICKING ON activate button
			Utils.WebdriverWait(100, "Configuration.Alerts_active_child_activate", "clickable");
			Utils.jsClick("Configuration.Alerts_active_child_activate");
			System.out.println("Alerts status :"+status);
			logger.log(LogStatus.PASS, "Pop-up Alerts status:" +status);
			System.out.println("clicked on turn-on button ");
			Utils.waitForSpinnerToDisappear(100);

			//CLICKING ON deactivate button
			Utils.WebdriverWait(100, "Configuration.Alerts_active_child_turnoff", "clickable");
			Utils.jsClick("Configuration.Alerts_active_child_turnoff");
			System.out.println("clicked on turn-off button");
			Utils.waitForSpinnerToDisappear(100);
		}				
		}
		else
		{
			
			Utils.WebdriverWait(100, "Configuration.Alerts_active_child_turnoff", "presence");			
			System.out.println("Pop-up Alerts status is :Trun Off");
			logger.log(LogStatus.PASS, "Pop-up Alerts status is: Trun Off");
		}
	}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Alerts are not activated :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Alerts are not activated :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
		
	}

/////////////////////////////////////
public static void createNoteTypes(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {


		//Navigating to Main Menu
		click("Configuration.mainMenu", 100, "clickable");

		// Navigating to Administration
		click("Configuration.AdminstrationMenu", 100, "clickable");
		waitForPageLoad(100);
//		Navigating to enterprise
		Utils.click("Configuration.AdminMenu.Enterprise");
		
//					// Navigating to Chain and Property Management Menu
		Utils.click("Configuration.AdminMenu.ChainandPropertyManagement", 100, "clickable");

		// Click on Note type
		Utils.mouseHover("Configuration.Notes");
		Utils.jsClick("Configuration.Notes", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);
		
		//Validating Note type page
		Utils.WebdriverWait(100, "Configuration.Notes_Heading", "presence");	
		logger.log(LogStatus.PASS, "Landed in Note Types Page");
		System.out.println("Landed in Note Types Page");
		
		//CLICKING ON LOV of Notes group
		Utils.WebdriverWait(100, "Configuration.Notes_LOV", "clickable");
		Utils.jsClick("Configuration.Notes_LOV");
		System.out.println("clicked on note Group LOV");
		Utils.waitForSpinnerToDisappear(100);
		
		//enter the value in search field
		jsTextbox("Configuration.Notes_value", configMap.get("Notes_Group_Name"));
		logger.log(LogStatus.PASS, "Entered Note Group Name: " + configMap.get("Notes_Group_Name"));
		System.out.println("Entered Note Group Name: " + configMap.get("Notes_Group_Name"));
		
		//clicked on search button
		Utils.WebdriverWait(100, "Configuration.Notes_searchvalue", "clickable");
		Utils.jsClick("Configuration.Notes_searchvalue");
		System.out.println("clicked on search button");
		logger.log(LogStatus.PASS, "clicked on search button");
		Utils.waitForSpinnerToDisappear(100);	
		
		if (isExists("Configuration.Notes_selectvalue"))				
		{
			String NoteGrp=getText("Configuration.Notes_selectvalue");
			
			Utils.WebdriverWait(100, "Configuration.Notes_selectvalue", "presence");		
			Utils.jsClick("Configuration.Notes_selectvalue");
			System.out.println("selected Note Group Name : " + NoteGrp);
			logger.log(LogStatus.PASS, "selected Note Group Name : " + NoteGrp);
			
			//click on select button
			Utils.jsClick("Configuration.Notes_selectbutton", 100, "clickable");
			System.out.println("clicked on select button");
			Utils.waitForSpinnerToDisappear(100);
		
			//clicked on search button
			Utils.WebdriverWait(100, "Configuration.Notes_searchvalue", "clickable");
			Utils.jsClick("Configuration.Notes_searchvalue");
			System.out.println("clicked on search button");
			logger.log(LogStatus.PASS, "clicked on search button");
			Utils.waitForSpinnerToDisappear(10);
			
			List<WebElement>  rows = Utils.elements("Configuration.Notes_verifyreservation"); 
			System.out.println("No of rows are : " + rows.size());
			System.out.println("Verifying the Note type under reservation note group : Note1");
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("Notes_Code")))
					{
						System.out.println("Notes Type "+rows.get(i).getText()+" is already existing");
						flag = true;
						logger.log(LogStatus.PASS, "Notes Type "+rows.get(i).getText()+" is already existing");
						break;
					}
					else
					{
					   
						System.out.println("Notes Type is : "+rows.get(i).getText());
						
					}
				
				}
				
				if(!flag){
					
					System.out.println("Note type under reservation note group is not found");
					System.out.println("Creting the Notes Type");
				//CLICKING ON New button
						Utils.WebdriverWait(100, "Configuration.Notes_New", "clickable");
						Utils.jsClick("Configuration.Notes_New");
						System.out.println("clicked on New button");
						Utils.waitForSpinnerToDisappear(100);


						//CLICKING ON LOV of Notes group
						Utils.WebdriverWait(100, "Configuration.Notes_LOV", "clickable");
						Utils.jsClick("Configuration.Notes_LOV");
						System.out.println("clicked on note Group LOV");
						Utils.waitForSpinnerToDisappear(100);

						//enter the value in search field
						jsTextbox("Configuration.Notes_value", configMap.get("Notes_Group_Name"));
						logger.log(LogStatus.PASS, "Entered Note Group Name: " + configMap.get("Notes_Group_Name"));
						System.out.println("Entered Note Group Name: " + configMap.get("Notes_Group_Name"));

						//clicked on search button
						Utils.WebdriverWait(100, "Configuration.Notes_searchGroup", "clickable");
						Utils.jsClick("Configuration.Notes_searchGroup");
						System.out.println("clicked on search button");
						logger.log(LogStatus.PASS, "clicked on search button");
						Utils.waitForSpinnerToDisappear(10);	
		
	
						if (isExists("Configuration.Notes_selectvalue"))				
						{
							String NoteGroup=getText("Configuration.Notes_selectvalue");

							Utils.WebdriverWait(100, "Configuration.Notes_selectvalue", "presence");		
							Utils.jsClick("Configuration.Notes_selectvalue");
							System.out.println("selected Note Group Name : " + NoteGroup);
							logger.log(LogStatus.PASS, "selected Note Group Name : " + NoteGroup);

							//click on select button
							Utils.jsClick("Configuration.Notes_selectbutton", 100, "clickable");
							System.out.println("clicked on select button");
							Utils.waitForSpinnerToDisappear(100);

							//enter the value in Note code
							jsTextbox("Configuration.Notes_Code", configMap.get("Notes_Code"));
							logger.log(LogStatus.PASS, "Entered Note Code: " + configMap.get("Notes_Code"));
							System.out.println("Entered Note Code: " + configMap.get("Notes_Code"));

							//enter the value in Note Description
							jsTextbox("Configuration.Notes_description", configMap.get("Notes_Description"));
							logger.log(LogStatus.PASS, "Entered Note Description: " + configMap.get("Notes_Description"));
							System.out.println("Entered Note Description: " + configMap.get("Notes_Description"));

							//click on Save
							Utils.jsClick("Configuration.Notes_save", 100, "clickable");
							System.out.println("clicked on Save button");
							Utils.waitForSpinnerToDisappear(100);
							
							logger.log(LogStatus.PASS, "Created the Note type: " + configMap.get("Notes_Code"));
							System.out.println("Created the Note type: " + configMap.get("Notes_Code"));
			
						}

						else
						{
							System.out.println("Note Group is not avaliable");
							logger.log(LogStatus.PASS, "Note Group is not avaliable");

							//click on Save
							Utils.jsClick("Configuration.Notes_Group_Cancel ", 100, "clickable");
							System.out.println("clicked on Cancel button");
							Utils.waitForSpinnerToDisappear(100);
						}
					}	
				}
			}
		
	}
	catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Note Type is not created :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Note Type is not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);
	}


}


public static void createItemInventory(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {


		//Navigating to Main Menu
		click("Configuration.mainMenu", 100, "clickable");

		// Navigating to Administration
		click("Configuration.AdminstrationMenu", 100, "clickable");
		waitForPageLoad(100);
		//			Navigating to enterprise
		Utils.click("Configuration.BookingMenu");

		//						// Navigating to Chain and Property Management Menu
		Utils.click("Configuration.resource_managment", 100, "clickable");

		// Click on item inventory 
		Utils.mouseHover("Configuration.ItemInventory");
		Utils.jsClick("Configuration.ItemInventory", 100, "clickable");
		Utils.waitForSpinnerToDisappear(100);

		//clicked on item inventory class Lov
		Utils.WebdriverWait(100, "Configuration.ItemInventory_class_LOV", "clickable");
		Utils.jsClick("Configuration.ItemInventory_class_LOV");
		System.out.println("clicked on item inventory class Lov");
		logger.log(LogStatus.PASS, "clicked on item inventory class Lov");
		Utils.waitForSpinnerToDisappear(10);	

		//enter the value in item inventory class
		jsTextbox("Configuration.ItemInventory_class_value", configMap.get("Item_Class"));
		logger.log(LogStatus.PASS, "Entered Item Class: " + configMap.get("Item_Class"));
		System.out.println("Entered Item Class: " + configMap.get("Item_Class"));

		//clicked on item inventory class search
		Utils.WebdriverWait(100, "Configuration.ItemInventory_class_search", "clickable");
		Utils.jsClick("Configuration.ItemInventory_class_search");
		System.out.println("clicked on item inventory class search");
		logger.log(LogStatus.PASS, "clicked on item inventory class search");
		Utils.waitForSpinnerToDisappear(10);

		if(isExists("Configuration.ItemInventory_class_selectvalue"))
		{

			//clicked on item inventory class select value
			Utils.WebdriverWait(100, "Configuration.ItemInventory_class_selectvalue", "clickable");
			Utils.jsClick("Configuration.ItemInventory_class_selectvalue");
			System.out.println("selected item inventory class  value");
			logger.log(LogStatus.PASS, "selected item inventory class  value");
			Utils.waitForSpinnerToDisappear(10);

			//clicked on item inventory class select 
			Utils.WebdriverWait(100, "Configuration.ItemInventory_class_select", "clickable");
			Utils.jsClick("Configuration.ItemInventory_class_select");
			System.out.println("clicked on select");
			logger.log(LogStatus.PASS, "clicked on select");
			Utils.waitForSpinnerToDisappear(10);


			//enter the value in item inventory NAME
			jsTextbox("Configuration.ItemInventory_Name", configMap.get("Item_Name"));
			logger.log(LogStatus.PASS, "Entered Item Name: " + configMap.get("Item_Name"));
			System.out.println("Entered Item Name: " + configMap.get("Item_Name"));


			//clicked on item inventory class search
			Utils.WebdriverWait(100, "Configuration.ItemInventory_search", "clickable");
			Utils.jsClick("Configuration.ItemInventory_search");
			System.out.println("clicked on item inventory search");
			logger.log(LogStatus.PASS, "clicked on item inventory search");
			Utils.waitForSpinnerToDisappear(10);


			if(isExists("Configuration.ItemInventory_verify"))
			{

				System.out.println("Item inventory is already avaliable");
				logger.log(LogStatus.PASS, "Item inventory is already avaliable");
				Utils.waitForSpinnerToDisappear(10);


			}
			else
			{
				System.out.println("Item inventory is not avaliable");
				logger.log(LogStatus.PASS, "Item inventory is not avaliable");

				System.out.println("***Creating the Item inventory***");
				//clicked on item inventory new
				Utils.WebdriverWait(100, "Configuration.ItemInventory_New", "clickable");
				Utils.jsClick("Configuration.ItemInventory_New");
				System.out.println("clicked on New");
				logger.log(LogStatus.PASS, "clicked on New");
				Utils.waitForSpinnerToDisappear(10);

				//clicked on item inventory class Lov
				Utils.WebdriverWait(100, "Configuration.ItemInventory_class_LOV", "clickable");
				Utils.jsClick("Configuration.ItemInventory_class_LOV");
				System.out.println("clicked on item inventory class Lov");
				logger.log(LogStatus.PASS, "clicked on item inventory class Lov");
				Utils.waitForSpinnerToDisappear(10);	

				//enter the value in item inventory class
				jsTextbox("Configuration.ItemInventory_class_value", configMap.get("Item_Class"));
				logger.log(LogStatus.PASS, "Entered Item class: " + configMap.get("Item_Class"));
				System.out.println("Entered Item class: " + configMap.get("Item_Class"));

				//clicked on item inventory class search
				Utils.WebdriverWait(100, "Configuration.ItemInventory_new_serach", "clickable");
				Utils.jsClick("Configuration.ItemInventory_new_serach");
				System.out.println("clicked on item inventory search");
				logger.log(LogStatus.PASS, "clicked on item inventory search");
				Utils.waitForSpinnerToDisappear(10);

				//clicked on item inventory class select value
				Utils.WebdriverWait(100, "Configuration.ItemInventory_selectvalue", "clickable");
				Utils.jsClick("Configuration.ItemInventory_selectvalue");
				System.out.println("Clicked on select");
				logger.log(LogStatus.PASS, "Clicked on select");
				Utils.waitForSpinnerToDisappear(10);

				//clicked on item inventory class select 
				Utils.WebdriverWait(100, "Configuration.ItemInventory_class_select", "clickable");
				Utils.jsClick("Configuration.ItemInventory_class_select");
				System.out.println("clicked on select");
				logger.log(LogStatus.PASS, "clicked on select");
				Utils.waitForSpinnerToDisappear(10);

				//enter the value in item inventory quick insert
				jsTextbox("Configuration.ItemInventory_quickinsert", configMap.get("Quick_Insert"));
				logger.log(LogStatus.PASS, "Entered Quick insert : " + configMap.get("Quick_Insert"));
				System.out.println("Entered Quick insert : " + configMap.get("Quick_Insert"));

				//enter the value in item inventory Quantity stock
				jsTextbox("Configuration.ItemInventory_quickinsert", configMap.get("Quantity_Stock"));
				logger.log(LogStatus.PASS, "Entered Quantity stock value: " + configMap.get("Quantity_Stock"));
				System.out.println("Entered Quantity stock value: " + configMap.get("Quantity_Stock"));


				//clicked on item inventory save
				Utils.WebdriverWait(100, "Configuration.ItemInventory_save ", "clickable");
				Utils.jsClick("Configuration.ItemInventory_save ");
				System.out.println("clicked on save");
				logger.log(LogStatus.PASS, "clicked on save");
				Utils.waitForSpinnerToDisappear(10);


				//clicked on item inventory class Lov
				Utils.WebdriverWait(100, "Configuration.ItemInventory_class_LOV", "clickable");
				Utils.jsClick("Configuration.ItemInventory_class_LOV");
				System.out.println("clicked on item inventory class Lov");
				logger.log(LogStatus.PASS, "clicked on item inventory class Lov");
				Utils.waitForSpinnerToDisappear(10);	

				//enter the value in item inventory class
				jsTextbox("Configuration.ItemInventory_class_value", configMap.get("Item_Class"));
				logger.log(LogStatus.PASS, "Entered Item Class: " + configMap.get("Item_Class"));
				System.out.println("Entered Item Class: " + configMap.get("Item_Class"));

				//clicked on item inventory class search
				Utils.WebdriverWait(100, "Configuration.ItemInventory_class_search", "clickable");
				Utils.jsClick("Configuration.ItemInventory_class_search");
				System.out.println("clicked on item inventory class search");
				logger.log(LogStatus.PASS, "clicked on item inventory class search");
				Utils.waitForSpinnerToDisappear(10);

				if(isExists("Configuration.ItemInventory_class_selectvalue"))
				{

					//clicked on item inventory class select value
					Utils.WebdriverWait(100, "Configuration.ItemInventory_class_selectvalue", "clickable");
					Utils.jsClick("Configuration.ItemInventory_class_selectvalue");
					System.out.println("selected item inventory class  value");
					logger.log(LogStatus.PASS, "selected item inventory class  value");
					Utils.waitForSpinnerToDisappear(10);

					//clicked on item inventory class select 
					Utils.WebdriverWait(100, "Configuration.ItemInventory_class_select", "clickable");
					Utils.jsClick("Configuration.ItemInventory_class_select");
					System.out.println("clicked on select");
					logger.log(LogStatus.PASS, "clicked on select");
					Utils.waitForSpinnerToDisappear(10);


					//enter the value in item inventory NAME
					jsTextbox("Configuration.ItemInventory_Name", configMap.get("Item_Name"));
					logger.log(LogStatus.PASS, "Entered Item Name: " + configMap.get("Item_Name"));
					System.out.println("Entered Item Name: " + configMap.get("Item_Name"));


					//clicked on item inventory class search
					Utils.WebdriverWait(100, "Configuration.ItemInventory_search", "clickable");
					Utils.jsClick("Configuration.ItemInventory_search");
					System.out.println("clicked on item inventory search");
					logger.log(LogStatus.PASS, "clicked on item inventory search");
					Utils.waitForSpinnerToDisappear(10);


					if(isExists("Configuration.ItemInventory_verify"))
					{

						System.out.println("Item inventory is Created successfully");
						logger.log(LogStatus.PASS, "IItem inventory is Created successfully");
						Utils.waitForSpinnerToDisappear(10);


					}
					else
					{
						System.out.println("Item inventory is not Created ");
						logger.log(LogStatus.FAIL, "IItem inventory is not Created ");
					}
				}

				else
				{
					//clicked on item inventory class cancel
					Utils.WebdriverWait(100, "Configuration.ItemInventory_class_cancel", "clickable");
					Utils.jsClick("Configuration.ItemInventory_class_cancel");
					System.out.println("clicked on cancel");
					logger.log(LogStatus.PASS, "clicked on cancel");
					Utils.waitForSpinnerToDisappear(10);
					System.out.println("item inventory class  value is not found");
					logger.log(LogStatus.FAIL, "item inventory class  value is not found");
				}
			}
		}
	}

	catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Note Type is not created :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Note Type is not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);
	}
}
}









