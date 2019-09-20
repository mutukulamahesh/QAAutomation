package com.oracle.hgbu.opera.qaauto.ui.generic.component.exports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.server.handler.CaptureScreenshot;

import com.aventstack.extentreports.model.Log;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;



public class GenericPage extends Utils {
	
	private static final boolean True = false;



	/*******************************************************************
	-  Description: Creating New Export 

	- Output: New Export will be created
	- Author: Shireesha
	- Date:05/16/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: Shireesha

	********************************************************************/
	
	public static void createExport() throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);

	
		try {
			Utils.takeScreenshot(driver, testName);
			Thread.sleep(1000);
			
			click("Exports.create_new", 100, "clickable");
			/*mouseHover("Exports.Property_LOV");
			jsClick("Exports.Property_LOV");
			logger.log(LogStatus.INFO, "Property LOV is selected");
			

			// passing property value
	        Wait(3000);
	        WebdriverWait(30, "Exports.Property_text", "presence");
			mouseHover("Exports.Property_text");
		    clear("Exports.Property_text");
			 textBox("Exports.Property_text", "Prop1");
			System.out.println("property value is Entered for general Exports");	
			logger.log(LogStatus.INFO, "Property value is Entered for export"+ "Prop1");
            jsClick("Exports.Search_property_searchBtn"); 
           Wait(2000);
           mouseHover("Exports.Search_property_select");
           System.out.println("FOund");
          jsClick(""
          		+ "Exports.Search_property_select");
           Wait(1000);*/
        
          
          //passing Export file name 
          mouseHover("Exports.FileType_LOV");
         jsClick("Exports.FileType_LOV");  
          Wait(3000);
          mouseHover("Exports.filetype_LOVTable");
         click("Exports.filetype_LOVTable"); 
         Wait(1000);
         System.out.println("fileName is Entered for general Exports");
         logger.log(LogStatus.INFO, "fileName value is Entered for general Exports");
        jsClick("Exports.Search_FileType_select");
         Wait(2000);
         Utils.WebdriverWait(100,"Exports.NewExport_continueBtn", "clickable");
          jsClick("Exports.NewExport_continueBtn"); 
          logger.log(LogStatus.INFO, "Continue Button is clicked in New Export screen");
          System.out.println("New Export is created");
          logger.log(LogStatus.INFO, "New Export is Created");
			
	          //Verifying Export is created 
				WebdriverWait(100, "Exports.EditExport_validationtxt", "presence");
				verifyCurrentPage("Exports.EditExport_validationtxt", "EditExport_validationtxt");
					Utils.log("newly created Export is in Edit state ", LogStatus.PASS);
					logger.log(LogStatus.PASS, "Landed in New Export Edit page");		
				
	          	} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Export  :: Failed " + alertText);
				
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Export not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: Generate Export

	- Output:To Run Export and generate file
	- Author: Hima
	- Date:05/16/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: Hima

	********************************************************************/
	public static void GenerateExport() throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);



		try {
			Utils.takeScreenshot(driver, testName);
			
			Wait(2000);
			
			//Search for an Export Dailystat
			//WebdriverWait(30, "Exports.txt_Description", "presence");
		/*	mouseHover("Exports.txt_Description");
		    textBox("Exports.txt_Description", "Dailystat");
			System.out.println("Filename is Entered for general Exports");
			logger.log(LogStatus.PASS, "Provided Exportname" +"DailyStat");
			click("Exports.button_Search");*/
	          

			// Running Export
	        Wait(3000);
	  	  Utils.WebdriverWait(100,"Exports.Action_link", "clickable");
		  Utils.jsClick("Exports.Action_link");
		  System.out.println("Selected Action Link...");
		  logger.log(LogStatus.PASS, "Selected Action Link"); 
		  
		  Utils.WebdriverWait(100,"Exports.Exportdata_link", "clickable");
		  Utils.jsClick("Exports.Exportdata_link");
		  System.out.println("Selected Export Data Link...");
		  logger.log(LogStatus.PASS, "Selected Export Data link");
	       
	             
	        if(isExists("Exports.Information_OK"))
			{
			
				  Thread.sleep(3000);
			  
			  // Select OK button from information
			  
			  Utils.WebdriverWait(100,"Exports.Information_OK", "clickable");
			  Utils.jsClick("Exports.Information_OK");
			  System.out.println("Selected Ok Btn...");
			  logger.log(LogStatus.PASS, "Selected Ok button in Information Screen"); //
			 // driver.switchTo().defaultContent(); 
			  //Thread.sleep(10000);
			}
			
	        Utils.WebdriverWait(100,"Exports.Action_link", "clickable");
			  Utils.jsClick("Exports.Action_link");
			  System.out.println("Selected Action Link...");
			  logger.log(LogStatus.PASS, "Selected Action Link"); 
			  
			  Utils.WebdriverWait(100,"Exports.ViewExportData_link", "clickable");
			  Utils.jsClick("Exports.ViewExportData_link");
			  System.out.println("Selected View Exports Link...");
			  logger.log(LogStatus.PASS, "Selected View Exports Link"); 
			  
			  Wait(2000);
			
	        //verify Export is generated 
	        
	    	//WebdriverWait(100, "Exports.Generated Exports_validationtxt", "presence");
	    	System.out.println("Validation present");
	    		
	    	
	    /*	if (isExists("Exports.Generated_Exports_validationtxt")) {
	    		Utils.log("Export is generated ", LogStatus.PASS);
	    		System.out.println("Export is generated ");
	    		
	    	}*/
			verifyCurrentPage("Exports.Generated_Exports_validationtxt", "GeneratedExports_validationtxt");
				
				Wait(1000);
			
				
				/*//entering values for Export File name and Extension
				WebdriverWait(30, "Exports.ExportFilename_text", "presence");
				mouseHover("Exports.ExportFilename_text");
			    textBox("Exports.ExportFilename_text", ExpMap.get("Filename"));
				System.out.println("File name is Entered for Exports generated");
				logger.log(LogStatus.PASS, "File Name is entered for generated Exports " +ExpMap.get("Filename"));
				
				WebdriverWait(30, "Exports.FileNameExtension_text", "presence");
				mouseHover("Exports.FileNameExtension_text");
			    textBox("Exports.FileNameExtension_text", ExpMap.get("Extension"));
				System.out.println("Extension is Entered to generate Export");
				logger.log(LogStatus.PASS, "Extension is Entered to generate Export " +);	
				
				click("Exports.button_GenerateFile");
	        */
	        
	      
				
	          	} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Genrate Export  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Export not generated :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
	-  Description: To navigate to Exports Menu

	- Output: User will be landed in Export menu page
	- Author: Shireesha
	- Date:05/16/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: Shireesha

	********************************************************************/
	
	
	public static void ExportsMenu() throws Exception {
		Wait(1000);
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);
		
		
		try {
			
			Utils.takeScreenshot(driver, testName);

			// Navigating to Exports menu
			click("Exports.Miscellaneous_menu");
		logger.log(LogStatus.PASS, "Selected Miscellaneous Menu");
			mouseHover("Exports.Exports_menu");	
			click("Exports.Exports_menu");
			System.out.println("clicked Exports");
			logger.log(LogStatus.PASS, "Selected Exports Menu");
			click("Exports.manage_Exports");
				logger.log(LogStatus.PASS, "Selected General Exports menu");
				
				WebdriverWait(100, "Exports.General_validationtxt", "presence");
			verifyCurrentPage("Exports.General_validationtxt", "General_validationtxt");
				Utils.log("Landed in General Exports page and Validated", LogStatus.PASS);
			
			

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Exports menu  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, testName);
			logger.log(LogStatus.FAIL, "Exports not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			throw (e);

		}
	}

	/*******************************************************************
	- Description: Run report and preview
	- Input: REPORT_GROUP, REPORT_NAME
	- Output: 
	- Author: srikanth konda
	- Date:20/12/2018
	- Revision History:
	                - Change Date 
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By: 

	********************************************************************/
	

	public static void RunReport(HashMap<String, String> configMap) throws Exception {

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
			
			// Navigating to Run Reports
			mouseHover("Configuration.menu_RunReports",100,"clickable");
			Thread.sleep(5000);
			jsClick("Configuration.menu_RunReports",100,"presence");
			
			org.testng.Assert.assertEquals(getText("Configuration.headerRunReports", 100, "presence"), "Run Reports");
			logger.log(LogStatus.PASS, "Landed in Run Reports Page");
			Thread.sleep(5000);
			
			
			
			// search for the report to see if its already present
			Boolean flag = false;
			textBox("Configuration.txt_ReportName", configMap.get("ReportName"), 100, "presence");
			Utils.tabKey("Configuration.txt_ReportName");
	
			mouseHover("Configuration.Reports.chk_ShowInternal",100,"clickable");
			Utils.click(Utils.element("Configuration.Reports.chk_ShowInternal"));
			Utils.Wait(3000);
			
			mouseHover("Configuration.ManageRes.drpdwn_ReportGroup",100,"clickable");
			selectBy("Configuration.ManageRes.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
		
			Utils.tabKey("Configuration.ManageRes.drpdwn_ReportGroup");
			Utils.mouseHover("Configuration.btnSearch");
			Utils.click(Utils.element("Configuration.btnSearch"));
			Utils.Wait(2000);
		
	
			
			try 
			{
				if(Utils.isExists("Configuration.Reports.btnExpand"))
				{
					Utils.click("Configuration.Reports.btnExpand");
					Utils.waitForSpinnerToDisappear(20);
				}
				flag = driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportName")+"']")).isDisplayed();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			if (flag) {
				
				driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportName")+"']")).click();
				
				// Navigating to Reports menu
				Utils.WebdriverWait(100, "Configuration.Reports.btnSelect", "clickable");
				Utils.mouseHover("Configuration.Reports.btnSelect");
				Utils.click(Utils.element("Configuration.Reports.btnSelect"));
				System.out.println("Clicked on select button");
				logger.log(LogStatus.PASS, "Clicked on select button");
				Thread.sleep(5000);
				
				Utils.WebdriverWait(100, "Configuration.Reports.btnNext", "clickable");
				Utils.mouseHover("Configuration.Reports.btnNext");
				Utils.click(Utils.element("Configuration.Reports.btnNext"));
				System.out.println("Clicked on Next button");
				logger.log(LogStatus.PASS, "Clicked on Next button");
				Thread.sleep(5000);
				Utils.WebdriverWait(100, "Configuration.Reports.radioPreviewToPrint", "clickable");
				Utils.mouseHover("Configuration.Reports.radioPreviewToPrint");
				Utils.click(Utils.element("Configuration.Reports.radioPreviewToPrint"));
				
				Utils.WebdriverWait(100, "Configuration.Reports.btnProcess", "clickable");
				Utils.mouseHover("Configuration.Reports.btnProcess");
				Utils.click(Utils.element("Configuration.Reports.btnProcess"));
				System.out.println("Clicked on Process button");
				logger.log(LogStatus.PASS, "Clicked on Process button");
				
				Utils.Wait(30000);
							String parentWindow = driver.getWindowHandle();
								Set<String> windows = driver.getWindowHandles();	
								
								for (String window:windows) {
									
									driver.switchTo().window(window);
									
								}
								
					if (driver.getPageSource().contains("reportviewer")) {
						System.out.println("Report preview successful");
						Actions action = new Actions(driver);
						action.sendKeys(driver.findElement(By.xpath("//embed")),Keys.chord(Keys.CONTROL,"a"),Keys.chord(Keys.CONTROL,"c")).perform();
						logger.log(LogStatus.PASS, "Report preview successful");
					} else {
						System.out.println("Report preview unsuccessful");
						logger.log(LogStatus.PASS, "Report preview unsuccessful");
					}
				
								
					driver.close();
				driver.switchTo().window(parentWindow);
				
				
			} else {
				logger.log(LogStatus.PASS, "Report: " + configMap.get("ReportName") + " not found in search");
				System.out.println("Report: " + configMap.get("ReportName") + " not found in search");
				
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
- Description: Add New tile to the Existing page 
- Input: Quantity
- Output: New tile should be added 
- Author: Dachepalli Shireesha 
- Date:03/01/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void AddNewTile(HashMap<String, String> configMap)  throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	
	try {
		
		//Verify user is Landed in OPERACloud home page 
		Utils.WebdriverWait(100, "Exports.OperaHome_validationtxt", "presence");			
		System.out.println("Landed in OPERACloud home Page " );
		logger.log(LogStatus.PASS, "Landed in OPERACloud home Page");
		
		//Verify if the Newly Added Page is present 
		
		Utils.WebdriverWait(100, "Exports.AddPage_validationtxt", "clickable");			
		System.out.println("Newly Added page is present" );
		logger.log(LogStatus.PASS, "Newly Added page is present");
		mouseHover("Exports.AddPage_validationtxt");
        click("Exports.AddPage_validationtxt");
        
		//Verify Add New Tiles link is present 
		Utils.WebdriverWait(100, "Exports.AddNewTiles_link", "presence");			
		System.out.println("Add newTiles link is present " );
		logger.log(LogStatus.PASS, "Add newTiles link is present");
		mouseHover("Exports.AddNewTiles_link");
		Utils.waitForSpinnerToDisappear(100);
		jsClick("Exports.AddNewTiles_link");
		logger.log(LogStatus.INFO, "Clicked on Add New Tiles");
		
		//verify Add new link page is present 
		WebdriverWait(100, "Exports.AddNewTiles_Validationtxt", "presence");
		verifyCurrentPage("Exports.AddNewTiles_Validationtxt", "AddNewTiles_Validationtxt");
		System.out.println("Add New  Tile page is opened");
			logger.log(LogStatus.PASS, "Add New  Tile page is opened");	
			
			//Enter Quantity for tiles
			Utils.textBox("Exports.Quantity_text", configMap.get("Quantity"), 100, "presence");
			System.out.println("Entered Quantity for tile");
			logger.log(LogStatus.PASS, "Entered Quantity for tile");
			
			//Click on the Add to DashBoard button 
			click("Exports.Add_button", 100, "clickable");
			System.out.println("Clicked on the  Add to Dashboard");
			logger.log(LogStatus.PASS, "Clicked on the  Add to Dashboard");
			
			//Verify Newly created Tile is added to Dashboard 
			
			Utils.WebdriverWait(100, "Exports.tile_validation", "presence");
			System.out.println("Newly Created Tile is Added to Dashboard Successfully " );
			logger.log(LogStatus.PASS, "Newly Created Tile is Added to Dashboard Successfully");

	}
		catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Add New Tile  :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "New Tile is not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}


}

/*******************************************************************
- Description: USer Should be able to Cancel and Close tiles from Dashboard 	
- Input: 
- Output: 
- Author: Dachepalli Shireesha 
- Date:08/01/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void CancelTile() throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	
	try {
		
		//Verify user is Landed in OPERACloud home page 
		Utils.WebdriverWait(100, "Exports.OperaHome_validationtxt", "presence");			
		System.out.println("Landed in OPERACloud home Page " );
		logger.log(LogStatus.PASS, "Landed in OPERACloud home Page");
		
		//Verify if the Newly Added Page is present 
		
		Utils.WebdriverWait(100, "Exports.AddPage_validationtxt", "clickable");			
		System.out.println("Newly Added page is present" );
		logger.log(LogStatus.PASS, "Newly Added page is present");			
        click("Exports.AddPage_validationtxt");
        
		//Verify Add New Tiles link is present 
        Utils.waitForPageLoad(500);
		Utils.WebdriverWait(400, "Exports.AddNewTiles_link", "presence");			
		System.out.println("Add newTiles link is present " );
		logger.log(LogStatus.PASS, "Add newTiles link is present");
		
		 
		//Utils.implicitWait(300);
		mouseHover("Exports.AddNewTiles_link");
		click("Exports.AddNewTiles_link");
		logger.log(LogStatus.INFO, "Clicked on Add New Tiles");
		
		//verify Add new link page is present 
		WebdriverWait(100, "Exports.AddNewTiles_Validationtxt", "presence");
		verifyCurrentPage("Exports.AddNewTiles_Validationtxt", "AddNewTiles_Validationtxt");
		System.out.println("Add New  Tile page is opened");
			logger.log(LogStatus.PASS, "Add New  Tile page is opened");	
			
			//To verify  cancel button ;
			Utils.waitForPageLoad(100);
			mouseHover("Exports.Cancel_btn");
			click("Exports.Cancel_btn", 100, "clickable");
			System.out.println("Cancel Button Is working Fine");
			logger.log(LogStatus.PASS, "Cancel Button Is working Fine");
			Wait(800);
			
			//To verify Close Button functionality
			Utils.waitForPageLoad(100);		
			mouseHover("Exports.AddNewTiles_link");
			click("Exports.AddNewTiles_link");
			logger.log(LogStatus.INFO, "Clicked on Add New Tiles");
			
			click("Exports.Close_link", 100, "clickable");
			System.out.println("Close button is working fine");
			logger.log(LogStatus.PASS, "Close button is working fine");
			
	}
		catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Cancel tile :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Cancel tile  is  :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}


}


/*******************************************************************
- Description: Remove Newly Added Tile from Dashboard 
- Input: Quantity
- Output: New tile should be added 
- Author: Dachepalli Shireesha 
- Date:07/01/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void RemoveNewTile() throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	
	try {
		
		//Verify user is Landed in OPERACloud home page 
		Utils.WebdriverWait(100, "Exports.OperaHome_validationtxt", "presence");			
		System.out.println("Landed in OPERACloud home Page " );
		logger.log(LogStatus.PASS, "Landed in OPERACloud home Page");

         //Verify if the Newly Added Page is present 
		
		Utils.WebdriverWait(100, "Exports.AddPage_validationtxt", "clickable");			
		System.out.println("Newly Added page is present" );
		logger.log(LogStatus.PASS, "Newly Added page is present");			
        click("Exports.AddPage_validationtxt");
		 
		
		
		//Verify if the Newly Added Tile already Exist 	
		   Utils.WebdriverWait(100, "Exports.tile_validation", "presence");
			System.out.println("Newly added Tile is Present in the Dashboard" );
			logger.log(LogStatus.PASS, "Newly added Tile is Present in the Dashboard" );
			
		
			//Delete Tile from Dashboard 
			Utils.WebdriverWait(100, "Exports.RemoveTile_link", "presence");
			System.out.println("Delete Icon is present in Dashboard " );
			mouseHover ("Exports.RemoveTile_link");
			click("Exports.RemoveTile_link");
			System.out.println("Tile is removed from Dashboard Successsfully" );
			logger.log(LogStatus.PASS, "Tile is removed from Dashboard Successsfully" );
			
					

	}
		catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Delete New Tile :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Delete New Tile:: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}
}


/*******************************************************************
-  Description: AddPage to Dashboard
- Input: PageName
- Author: Hima
- Date:01/04/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: Hima
********************************************************************/


public static void AddPage(HashMap<String, String> configMap) throws Exception {
    Wait(1000);
    String testName = Utils.getMethodName();
    System.out.println("name: " + testName);
    
    
    try 
    {
        //Verify user is Landed in OPERACloud home page 
        Utils.WebdriverWait(100, "Exports.OperaHome_validationtxt", "presence");            
        System.out.println("Landed in OPERACloud home Page " );
      
       
        //Verify Add Page link is present 
        Utils.WebdriverWait(100, "Exports.Addpage_link", "clickable");          
        System.out.println("Add Page link is present " );
        logger.log(LogStatus.PASS, "Add Page link is present");
        click("Exports.Addpage_link");
        logger.log(LogStatus.INFO, "Clicked on Add New Page");
        
        //verify Add Page is Present 
        WebdriverWait(100,"Exports.AddPageTitle_validationtxt", "presence");
        verifyCurrentPage("Exports.AddPageTitle_validationtxt", "AddPage_Validationtxt");
        System.out.println("Add page is opened");
        logger.log(LogStatus.PASS, "Add New  Tile page is opened");   
            
            //Enter Page Name
            Utils.textBox("Exports.txt_PageName", configMap.get("PageName"), 100, "presence");
            System.out.println("Entered Add Page Name");
            logger.log(LogStatus.PASS, "Entered Add Page Name");
            
            //Click on the Save button 
            click("Exports.button_SavePage", 100, "clickable");
            System.out.println("Clicked on the Save Button to Add Page");
            logger.log(LogStatus.PASS, "Clicked on the  Save Button to Add Page");
            
            //Verify Newly created Page added
            
            Utils.WebdriverWait(100, "Exports.AddPage_validationtxt", "presence");
            System.out.println("Newly Page Added Successfully " );
            logger.log(LogStatus.PASS, "Newly Page Added Successfully");
        
    }
    catch (Exception e)
    {
        try 
        {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            logger.log(LogStatus.FAIL, "AddPage  :: Failed " + alertText);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
            System.out.println("Alert data: " + alertText);
            alert.accept();
        } 
        catch (NoAlertPresentException ex) 
        {
            ex.printStackTrace();
        }
        Utils.takeScreenshot(driver, testName);
        logger.log(LogStatus.FAIL, "Exports not AddPage :: Failed " + e.getLocalizedMessage());
        logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
        logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
        throw (e);
    }
}

/*******************************************************************
-  Description: EditPage to Dashboard
- Input: PageName
- Author: Hima
- Date:01/04/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: Hima
********************************************************************/


public static void EditPage(HashMap<String, String> configMap) throws Exception 
{
    Wait(1000);
    String testName = Utils.getMethodName();
    System.out.println("name: " + testName);
    
    
    try 
    {
        //Verify user is Landed in OPERACloud home page 
        Utils.WebdriverWait(100, "Exports.OperaHome_validationtxt", "presence");            
        System.out.println("Landed in OPERACloud home Page " );
        logger.log(LogStatus.PASS, "Landed in OPERACloud home Page");
        
        //Verify More Options link is present
        //Utils.WebdriverWait(100,"Exports.AddPage_validationtxt", "clickable");    
        //Utils.mouseHover("Exports.AddPage_validationtxt");
        //click("Exports.AddPage_validationtxt");
        //logger.log(LogStatus.PASS, "Page is present present");
        
        //Utils.mouseHover("Exports.More_link");
        
        Utils.WebdriverWait(100, "Exports.More_Editlink", "clickable"); 
        Utils.mouseHover("Exports.More_Editlink");
        click("Exports.More_Editlink");
        System.out.println("More Options link is present " );
        logger.log(LogStatus.PASS, "More Options link is present");
        logger.log(LogStatus.INFO, "Clicked on More Options Link");
        
        //Enter Edit Link is present
        Utils.WebdriverWait(100, "Exports.Edit_link", "clickable"); 
        Utils.mouseHover("Exports.Edit_link");
        click("Exports.Edit_link");
        System.out.println("Edit page is opened");
        logger.log(LogStatus.PASS, "Edit Tile page is opened");   
        
        //Verify Edit Page is present
        WebdriverWait(100,"Exports.EditPageTitle_validationtxt","presence");
        Utils.mouseHover("Exports.EditPageTitle_validationtxt");
        verifyCurrentPage("Exports.EditPageTitle_validationtxt","EditPage_Validationtxt");
        System.out.println("Edit page is opened");
        logger.log(LogStatus.PASS, "Edit Page is opened");    
        
        //Enter Edit Page
        Utils.mouseHover("Exports.txt_EditPageName");
        click("Exports.txt_EditPageName");
        clear("Exports.txt_EditPageName");
        System.out.println("Clear Edit Page Text Field");
        Utils.textBox("Exports.txt_EditPageName", configMap.get("PageName"), 100, "presence");
        System.out.println("Entered Edit Page Name");
        logger.log(LogStatus.PASS, "Entered Edit Page Name");
            
        //Click on the Save button 
        click("Exports.button_SavePage", 100, "clickable");
        System.out.println("Clicked on the Save Button to Add Page");
        logger.log(LogStatus.PASS, "Clicked on the  Save Button to Add Page");
        
        //Verify Newly Edit Page added
        
        Utils.WebdriverWait(100, "Exports.EditPage_validationtxt", "presence");
        System.out.println("Edit Page Added Successfully " );
        logger.log(LogStatus.PASS, "Edit Page Added Successfully");
            
    } 
    catch (Exception e)
    {
        try 
        {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            logger.log(LogStatus.FAIL, "AddPage  :: Failed " + alertText);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
            System.out.println("Alert data: " + alertText);
            alert.accept();
        } 
        catch (NoAlertPresentException ex) 
        {
            ex.printStackTrace();
        }
        Utils.takeScreenshot(driver, testName);
        logger.log(LogStatus.FAIL, "Exports not AddPage :: Failed " + e.getLocalizedMessage());
        logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
        logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
        throw (e);
    }
}

/*******************************************************************
-  Description: Delete Page from a Dashboard
- Input: PageName
- Author: Hima
- Date:01/04/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: Hima
********************************************************************/
public static void DeletePage() throws Exception {
    Wait(1000);
    String testName = Utils.getMethodName();
    System.out.println("name: " + testName);
    
    
    try 
    {
        //Verify user is Landed in OPERACloud home page 
        Utils.WebdriverWait(100, "Exports.OperaHome_validationtxt", "presence");            
        System.out.println("Landed in OPERACloud home Page " );
        logger.log(LogStatus.PASS, "Landed in OPERACloud home Page");
        
        Utils.WebdriverWait(100, "Exports.More_Deletelink", "clickable");   
        Utils.mouseHover("Exports.More_Deletelink");
        click("Exports.More_Deletelink");
        System.out.println("More Options link is present " );
        logger.log(LogStatus.PASS, "More Options link is present");
        logger.log(LogStatus.INFO, "Clicked on More Options Link");
        
        //Enter Delete Link is present
        Utils.WebdriverWait(100, "Exports.Delete_link", "clickable");   
        Utils.mouseHover("Exports.Edit_link");
        click("Exports.Delete_link");
        System.out.println("Delete Confirmation Page is present");
        logger.log(LogStatus.PASS, "Delete Confirmation Page is present");
        
        //Deleted Button clicked
        Utils.WebdriverWait(100, "Exports.button_Delete", "clickable"); 
        Utils.mouseHover("Exports.button_Delete");
        click("Exports.button_Delete");
        
    } 
    catch (Exception e)
    {
        try 
        {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            logger.log(LogStatus.FAIL, "DeletePage  :: Failed " + alertText);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
            System.out.println("Alert data: " + alertText);
            alert.accept();
        } 
        catch (NoAlertPresentException ex) 
        {
            ex.printStackTrace();
        }
        Utils.takeScreenshot(driver, testName);
        logger.log(LogStatus.FAIL, "DeletePage is :: Failed " + e.getLocalizedMessage());
        logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
        logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
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


public static void Create_RTF_Report(HashMap<String, String> configMap) throws Exception {

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
		org.testng.Assert.assertEquals(getText("Configuration.headerCreateReport", 100, "presence"), "Create Reports");
		logger.log(LogStatus.PASS, "Landed in Create Reports Page");
		Thread.sleep(5000);
		
		
		mouseHover("Configuration.radio_RTF",100,"presence");
		jsClick("Configuration.radio_RTF",100,"presence");
		Utils.Wait(6000);
		
		Boolean flag = false;
	
		jsClick("Configuration.Reports_LOV_rtfFile");
		Utils.WebdriverWait(100, "Configuration.select_rtf_firstRecord", "clickable");
		Utils.mouseHover("Configuration.select_rtf_firstRecord");
		jsClick("Configuration.select_rtf_firstRecord");
		waitForSpinnerToDisappear(10);			
		Utils.mouseHover("Configuration.searchSelectButton");
		jsClick("Configuration.searchSelectButton");
		waitForSpinnerToDisappear(10);
		Utils.Wait(4000);
		Utils.WebdriverWait(100, "Configuration.Reports_LOV_odtfile", "clickable");
		Utils.mouseHover("Configuration.Reports_LOV_odtfile");
		jsClick("Configuration.Reports_LOV_odtfile");
		Utils.WebdriverWait(100, "Configuration.select_odt_firstRecord", "clickable");
		jsClick("Configuration.select_odt_firstRecord");
		Utils.Wait(3000);
		Utils.mouseHover("Configuration.odt_searchSelectButton");
		jsClick("Configuration.odt_searchSelectButton");
		waitForSpinnerToDisappear(10);	
		Utils.Wait(6000);
		
		
		selectBy("Configuration.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
		//Utils.Wait(4000);
		Utils.tabKey("Configuration.drpdwn_ReportGroup");
		mouseHover("Configuration.txt_DisplayName",100,"presence");
		Utils.Wait(3000);
		textBox("Configuration.txt_DisplayName", configMap.get("RTF_ReportName"), 100, "presence");
		Utils.tabKey("Configuration.txt_DisplayName");
		Utils.Wait(3000);
		mouseHover("Configuration.btnSave",100,"presence");
		jsClick("Configuration.btnSave");
		
		try {
		
		flag = Utils.isDisplayed("Configuration.alert_ReportExists","Report exists alert");
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
			org.testng.Assert.assertEquals(getText("Configuration.headerManageReports", 100, "presence"), "Manage Reports");
			logger.log(LogStatus.PASS, "Landed in Manage Reports Page");
			Thread.sleep(5000);	
			
			textBox("Configuration.txt_ReportName", configMap.get("RTF_ReportName"), 100, "presence");
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
				
				driver.findElement(By.xpath("//span[text()='"+configMap.get("RTF_ReportName")+"']")).isDisplayed();
				logger.log(LogStatus.PASS, "Report Name: " + configMap.get("RTF_ReportName") + " created successfully");
				System.out.println("Report Name: " + configMap.get("RTF_ReportName") + " created successfully");
				
				} catch(Exception e) {
					logger.log(LogStatus.PASS, "Report Name: " + configMap.get("RTF_ReportName") + " not created ");
					System.out.println("Report Name: " + configMap.get("RTF_ReportName") + " not created");
				}
			
			
			
			
		} else {
			logger.log(LogStatus.PASS, "Report Name: " + configMap.get("RTF_ReportName") + " already exists");
			System.out.println("Report Name: " + configMap.get("RTF_ReportName") + " already exists");
			
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
- Description: Run report and preview
- Input: REPORT_GROUP, REPORT_NAME
- Output: 
- Author: srikanth konda
- Date:20/12/2018
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void RunBIPReport(HashMap<String, String> configMap) throws Exception {

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
		
		// Navigating to Run Reports
		mouseHover("Configuration.menu_RunReports",100,"clickable");
		Thread.sleep(5000);
		jsClick("Configuration.menu_RunReports",100,"presence");
		
		org.testng.Assert.assertEquals(getText("Configuration.headerRunReports", 100, "presence"), "Run Reports");
		logger.log(LogStatus.PASS, "Landed in Run Reports Page");
		Thread.sleep(5000);
		
		
		
		// search for the report to see if its already present
		Boolean flag = false;
		textBox("Configuration.txt_ReportName", configMap.get("RTF_ReportName"), 100, "presence");
		Utils.tabKey("Configuration.txt_ReportName");

		mouseHover("Configuration.Reports.chk_ShowInternal",100,"clickable");
		Utils.click(Utils.element("Configuration.Reports.chk_ShowInternal"));
		Utils.Wait(3000);
		
		mouseHover("Configuration.ManageRes.drpdwn_ReportGroup",100,"clickable");
		selectBy("Configuration.ManageRes.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
	
		Utils.tabKey("Configuration.ManageRes.drpdwn_ReportGroup");
		Utils.mouseHover("Configuration.btnSearch");
		Utils.click(Utils.element("Configuration.btnSearch"));
		Utils.Wait(2000);
	

		
		try {
		
		flag = driver.findElement(By.xpath("//span[text()='"+configMap.get("RTF_ReportName")+"']")).isDisplayed();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if (flag) {
			
			driver.findElement(By.xpath("//span[text()='"+configMap.get("RTF_ReportName")+"']")).click();
			
			// Navigating to Reports menu
			Utils.WebdriverWait(100, "Configuration.Reports.btnSelect", "clickable");
			Utils.mouseHover("Configuration.Reports.btnSelect");
			Utils.click(Utils.element("Configuration.Reports.btnSelect"));
			System.out.println("Clicked on select button");
			logger.log(LogStatus.PASS, "Clicked on select button");
			Thread.sleep(5000);
			
			Utils.WebdriverWait(100, "Configuration.Reports.btnNext", "clickable");
			Utils.mouseHover("Configuration.Reports.btnNext");
			Utils.click(Utils.element("Configuration.Reports.btnNext"));
			System.out.println("Clicked on Next button");
			logger.log(LogStatus.PASS, "Clicked on Next button");
			Thread.sleep(5000);
			Utils.WebdriverWait(100, "Configuration.Reports.radioPreviewToPrint", "clickable");
			Utils.mouseHover("Configuration.Reports.radioPreviewToPrint");
			Utils.click(Utils.element("Configuration.Reports.radioPreviewToPrint"));
			
			Utils.WebdriverWait(100, "Configuration.Reports.btnProcess", "clickable");
			Utils.mouseHover("Configuration.Reports.btnProcess");
			Utils.click(Utils.element("Configuration.Reports.btnProcess"));
			System.out.println("Clicked on Process button");
			logger.log(LogStatus.PASS, "Clicked on Process button");
			
			Utils.Wait(30000);
						String parentWindow = driver.getWindowHandle();
							Set<String> windows = driver.getWindowHandles();	
							
							for (String window:windows) {
								
								driver.switchTo().window(window);
								
							}
							
				if (driver.getPageSource().contains("reportviewer")) {
					System.out.println("Report preview successful");
					logger.log(LogStatus.PASS, "Report preview successful");
				} else {
					System.out.println("Report preview unsuccessful");
					logger.log(LogStatus.PASS, "Report preview unsuccessful");
				}
			
							
				driver.close();
			driver.switchTo().window(parentWindow);
			
			
		} else {
			logger.log(LogStatus.PASS, "Report: " + configMap.get("ReportName") + " not found in search");
			System.out.println("Report: " + configMap.get("ReportName") + " not found in search");
			
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
-  Description: Creating Membership Export 

- Output: New Export will be created
- Author: Shireesha
- Date:01/16/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: Shireesha

********************************************************************/

public static void createMembershipExport() throws Exception {
	String testName = Utils.getMethodName();
	System.out.println("name: " + testName);


	try {
		Utils.takeScreenshot(driver, testName);
		// Navigating to Exports menu
				click("Exports.Miscellaneous_menu");
				logger.log(LogStatus.PASS, "Selected Miscellaneous Menu");
				mouseHover("Exports.Exports_menu");	
				click("Exports.Exports_menu");
				System.out.println("clicked Exports");
				logger.log(LogStatus.PASS, "Selected Exports Menu");
				click("Exports.membership_Exports");
				logger.log(LogStatus.PASS, "Selected Membership Exports menu");
				WebdriverWait(100, "Exports.membership_validationtxt", "presence");
				verifyCurrentPage("Exports.membership_validationtxt", "membership_validationtxt");
			    Utils.log("Landed in Membership Exports page and Validated", LogStatus.PASS);
					
		           Utils.waitForPageLoad(100);
		click("Exports.create_new", 100, "clickable");
		
      
      //passing Export file name 
      mouseHover("Exports.FileType_LOV");
     jsClick("Exports.FileType_LOV");  
      Wait(3000);
      mouseHover("Exports.filetype_LOVTable");
     click("Exports.filetype_LOVTable"); 
     Wait(1000);
     System.out.println("fileName is Entered for Membership Exports");
     logger.log(LogStatus.INFO, "fileName value is Entered for Membership Exports");
    jsClick("Exports.Search_FileType_select");
     Wait(2000);
     Utils.WebdriverWait(100,"Exports.NewExport_continueBtn", "clickable");
      jsClick("Exports.NewExport_continueBtn"); 
      logger.log(LogStatus.INFO, "Continue Button is clicked in New Export screen");
      System.out.println("New Export is created");
      logger.log(LogStatus.INFO, "New Export is Created");
		
          //Verifying Export is created 
			WebdriverWait(100, "Exports.EditExport_validationtxt", "presence");
			verifyCurrentPage("Exports.EditExport_validationtxt", "EditExport_validationtxt");
				Utils.log("newly created Export is in Edit state ", LogStatus.PASS);
				logger.log(LogStatus.PASS, "Landed in New Export Edit page");		
			
          	} catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Create Membership Export  :: Failed " + alertText);
			
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, testName);
		logger.log(LogStatus.FAIL, "Membership Export not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
		throw (e);

	}
}


/*******************************************************************
-  Description: Creating Country Export 

- Output: New Export will be created
- Author: Shireesha
- Date:01/16/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: Shireesha

********************************************************************/

public static void createCountryExport() throws Exception {
	String testName = Utils.getMethodName();
	System.out.println("name: " + testName);


	try {
		Utils.takeScreenshot(driver, testName);
		// Navigating to Exports menu
				click("Exports.Miscellaneous_menu");
				logger.log(LogStatus.PASS, "Selected Miscellaneous Menu");
				mouseHover("Exports.Exports_menu");	
				click("Exports.Exports_menu");
				System.out.println("clicked Exports");
				logger.log(LogStatus.PASS, "Selected Exports Menu");
				click("Exports.Country_Exports");
				logger.log(LogStatus.PASS, "Selected Country Exports menu");
				WebdriverWait(100, "Exports.Country_validationtxt", "presence");
				verifyCurrentPage("Exports.Country_validationtxt", "Country_validationtxt");
			    Utils.log("Landed in Country Exports page and Validated", LogStatus.PASS);
					
		           Utils.waitForPageLoad(100);
		click("Exports.create_new", 100, "clickable");
		
      
      //passing Export file name 
      mouseHover("Exports.FileType_LOV");
     jsClick("Exports.FileType_LOV");  
      Wait(3000);
      mouseHover("Exports.filetype_LOVTable");
     click("Exports.filetype_LOVTable"); 
     Wait(1000);
     System.out.println("fileName is Entered for CountryExports");
     logger.log(LogStatus.INFO, "fileName value is Entered for Country Exports");
    jsClick("Exports.Search_FileType_select");
     Wait(2000);
     Utils.WebdriverWait(100,"Exports.NewExport_continueBtn", "clickable");
      jsClick("Exports.NewExport_continueBtn"); 
      logger.log(LogStatus.INFO, "Continue Button is clicked in Country Export screen");
      System.out.println("New Export is created");
      logger.log(LogStatus.INFO, "New country Export is Created");
		
          //Verifying Export is created 
			WebdriverWait(100, "Exports.EditExport_validationtxt", "presence");
			verifyCurrentPage("Exports.EditExport_validationtxt", "EditExport_validationtxt");
				Utils.log("newly created  Export is in Edit state ", LogStatus.PASS);
				logger.log(LogStatus.PASS, "Landed in New Export Edit page");		
			
          	} catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Create country Export  :: Failed " + alertText);
			
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, testName);
		logger.log(LogStatus.FAIL, "Country Export not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, testName)));
		throw (e);

	}
}


/*******************************************************************
- Description: To Create New mapping Codes
- Input: 
- Output: New Mapping codes  will be created 
- Author: Dachepalli Shireesha 
- Date:01/02/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void CreateMappingTypes(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	
	try {
		          // Navigating to Main Menu
		Utils.waitForPageLoad(100);
					mouseHover("Exports.Mapping_mainMenu", 100, "clickable");
					click("Exports.Mapping_mainMenu", 100, "clickable");

					// Navigating to Administration Menu
					click("Exports.Mapping_AdminstrationMenu",200, "clickable");
					
					//to scroll bottom of page 
					
					JavascriptExecutor jse = (JavascriptExecutor)driver;
					jse.executeScript("window.scrollBy(0,250)", "");
					
					// Navigating to Interfaces
					click("Exports.menu_Interfaces", 200, "clickable");
					
					click("Exports.Exportmapping",200,"clickable");
					
					//Navigating to MappingTypes/Codes Screen 
				click("Exports.ExportTypes", 200, "clickable");	
				Utils.waitForPageLoad(500);
				
				//Entering value for Lov_Mapped_to  and CLick on Search 
			     click("Exports.LOV_Mapped_To",300,"clickable");
			    // Utils.waitForSpinnerToDisappear(100);
			     mouseHover("Exports.Property_text");
			    textBox("Exports.Property_text","FINTRXCODES" );
			    click("Exports.LOV_SearchButton",100,"clickable");
			    Utils.waitForSpinnerToDisappear(100);
			    click("Exports.LOV_table",100,"clickable");
			    click("Exports.Search_property_select",100,"clickable");
			    
			    //Clicking on the Search button 
			    Utils.waitForPageLoad(200);
			    mouseHover("Exports.MappingCode_Search");
			    jsClick("Exports.MappingCode_Search",200,"clickable");
			    Utils.waitForSpinnerToDisappear(200);
			    
				//To verify Mapping Code Already Exist 
				//Fetch Search Grid Column Data and validate
				List<WebElement>  rows = Utils.elements("Exports.tbl_MappingTypes"); 
				System.out.println("No of rows are :" + rows.size());


				if(rows.size() > 0)
				{
					for(int i=0;i<rows.size();i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
						if(rows.get(i).getText().equalsIgnoreCase(configMap.get("Type")))
						{
							System.out.println("Mapping Code "+rows.get(i).getText()+" is already existing");

							logger.log(LogStatus.PASS, "Mapping Code "+rows.get(i).getText()+" is already existing");
							boolean flag=false;
						}
						
						else 
						{
							System.out.println("Mapping Code "+rows.get(i).getText()+" is  not  existing");
							boolean flag= true;
							logger.log(LogStatus.PASS, "Mapping Code "+rows.get(i).getText()+" is not  existing, creating New one");
							// wait for New link to load
							mouseHover("Exports.New_MappingTypes");
							Utils.WebdriverWait(100, "Exports.New_MappingTypes", "presence");

							// Clicking on New Button
							Utils.click("Exports.New_MappingTypes", 100, "clickable");
							
							// Entering the Description
							mouseHover("Exports.New_MappingTypes_description");
							textBox("Exports.New_MappingTypes_description",configMap.get("Description"), 100, "presence");
                            System.out.println(configMap.get("Type"));
							// Entering the type
							textBox("Exports.New_MappingTypes_Type",configMap.get("Type"), 100, "presence");            
							
							//clicking on Save button 
							click("Exports.New_MappingTypes_Save", 100, "clickable");
							Utils.waitForSpinnerToDisappear(100);
							Utils.waitForPageLoad(100);
							
							//Associating Mapping Code, clicking on edit Action Next to created mapping type 
							
							click("Exports.New_MappingTypes_Actions",100,"clickable");
							click("Exports.New_MappingTypes_Edit",100,"clickable");
							
							//Expanding + icon 
							click("Exports.New_MappingTypes_expand",100,"clickable");
							Utils.waitForSpinnerToDisappear(200);
							Utils.click("Exports.New_MappingTypes", 100, "clickable");	
							
							//Enter values in Mapping code Screen 
							
							textBox("Exports.New_MappingTypes_MappingCode",configMap.get("MappingCode"), 100, "presence");
							textBox("Exports.New_MappingTypes_description",configMap.get("Description"), 100, "presence");
							textBox("Exports.New_MappingTypes_Sequence",configMap.get("Sequence"), 100, "presence");
						
							click("Exports.New_MappingTypes_btnsave",100,"clickable");
							click("Exports.New_MappingTypes_Save", 100, "clickable");
							Utils.waitForSpinnerToDisappear(100);
							Utils.waitForPageLoad(100);
							logger.log(LogStatus.PASS, "Mapping Code is created");
						
						
						}
					}
					
				}
				/*else
				{					
							// wait for New link to load	
							Utils.WebdriverWait(100, "Exports.New_MappingTypes", "presence");

							// Clicking on New Button
							Utils.click("Exports.New_MappingTypes", 100, "clickable");	

							// Entering the type
							textBox("Exports.New_MappingTypes_Type",configMap.get("Type"), 100, "presence");            
							// Entering the Description
							mouseHover("Exports.New_MappingTypes_description");
							textBox("Exports.New_MappingTypes_description",configMap.get("Description"), 100, "presence");
							//clicking on Save button 
							click("Exports.New_MappingTypes_Save", 100, "clickable");
							Utils.waitForSpinnerToDisappear(100);
							Utils.waitForPageLoad(100);
							
							//Associating Mapping Code, clicking on edit Action Next to created mapping type 
							
							click("Exports.New_MappingTypes_Actions",100,"clickable");
							click("Exports.New_MappingTypes_Edit",100,"clickable");
							
							//Expanding + icon 
							click("Exports.New_MappingTypes_expand",100,"clickable");
							Utils.waitForSpinnerToDisappear(200);
							Utils.click("Exports.New_MappingTypes", 100, "clickable");	
							
							//Enter values in Mapping code Screen 
							
							textBox("Exports.New_MappingTypes_MappingCode",configMap.get("MappingCode"), 100, "presence");
							textBox("Exports.New_MappingTypes_description",configMap.get("Description"), 100, "presence");
							textBox("Exports.New_MappingTypes_Sequence",configMap.get("Sequence"), 100, "presence");
							click("Exports.New_MappingTypes_btnsave",100,"clickable");
							click("Exports.New_MappingTypes_Save", 100, "clickable");
							
							logger.log(LogStatus.PASS, "Mapping Code is created");
							
				}		*/



					//Verify Newly created code Exist			
					if (ValidateGridData("Exports.tbl_MappingTypes", configMap.get("Type"))){
						System.out.println("Mapping Code is created successfully :"+configMap.get("Type"));
						logger.log(LogStatus.PASS, "Mapping Code is created successfully:"+configMap.get("Type"));
					} else {
						System.out.println("Mapping Code is not created successfully :"+configMap.get("Type"));
						logger.log(LogStatus.FAIL, "Mapping Code is not created successfully :"+configMap.get("Type"));
					}

				}
	
		catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "creation of Export Mapping  :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Create Mapping Types :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}
}



/*******************************************************************
-  Description: Property Level License Activate

- Output: License will be activated at property level for OPERA MOBILE
- Author: Shireesha
- Date:01/02/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: Shireesha

********************************************************************/
public static void property_ActivateLicense(HashMap<String, String> configMap) throws Exception{
	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {
		
		//navigating to tool Box 
		Utils.waitForPageLoad(100);
		click("Exports.Mapping_mainMenu",100,"clickable");
		Utils.waitForSpinnerToDisappear(100);
		click("Exports.ToolBoxMenu",100,"clickable");
		Utils.waitForPageLoad(300);
		
		// Navigation to System Setup
		click("Exports.ToolBoxsystemSetup",100,"clickable");

		// Navigating to OPERA Licenses
		click("Exports.ToolBoxOperaLicenses",100,"clickable");

			//Select the property radio button
		click("Exports.OperaLicense_radiobtn",100,"clickable");
		waitForSpinnerToDisappear(30);
		
		//getting the property value from the data sheet
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
		System.out.println("Property Config: " + propertyMap);

		String property = propertyMap.get("Property");
		System.out.println("prop: " + property);

		//Click on Search Button
		click("Exports.OperaLicense_searchbtn",100,"clickable");
		waitForSpinnerToDisappear(500);
		
		//Fetch Licenses table Column Data and validate
				java.util.List<WebElement>  rows = Utils.elements("Exports.OperaLicense_tblAvailableLicenses"); 
				System.out.println("No of rows are : " + rows.size());
				boolean flag = true;	
				if(rows.size() > 0)
				{
					for(int i = 0; i < rows.size(); i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
						if(rows.get(i).getText().contains(configMap.get("APPLICATION")))
						{
							System.out.println("License "+rows.get(i).getText()+" is listed");
							
							flag = false;
							logger.log(LogStatus.PASS, "License "+rows.get(i).getText()+" is listed");
							// click on the license
							click(rows.get(i));
						
							waitForSpinnerToDisappear(30);
							//dynamically constructing the Xpath to validate the Activate checkbox																		 
								String strXPath = "((//*[@data-ocid='TBL_TS1']/div[2]/table/tbody/tr/td[1])["+ (i+1) +"]/following::*[@data-ocid[contains(.,'CHK_CNTNT_SBC1')]]/*/img)[1]";
							if(driver.findElement(By.xpath(strXPath)).getAttribute("title").equalsIgnoreCase("Checked"))
								{
								System.out.println("License "+rows.get(i).getText()+" is already activated");
								
									logger.log(LogStatus.PASS, "License "+rows.get(i).getText()+" is already activated");
								}
								else{
								textBox("Exports.LicenseDetails_txtProductCode",configMap.get("LICENSE_NAME"), 100, "presence");
									textBox("Exports.LicenseDetails_txtNumberOfRooms",configMap.get("NO_OF_ROOMS"), 100, "presence");
				                    textBox("Exports.LicenseDetails_txtExpiryDate","02-28-2025", 100, "presence");
									  jsClick("Exports.LicenseDetails_btnActivate",100,"clickable");
									  
									  Utils.waitForSpinnerToDisappear(600);
                                       boolean active=  isExists("Exports.LicenseDetails_btnActivate");	
                                       System.out.println(active);
                                                                            
                                       if(active)
                                       { 	System.out.println("License is not enabled successfully");
                                    	 
                                    	   logger.log(LogStatus.PASS, "License is not  enabled successfully");
                                       }
										else{
											System.out.println("License is enabled successfully");
											logger.log(LogStatus.PASS, "License is enabled  successfully");
										}
                                       }
							break;
						}
					}	
					
				}
				  else
			          {
				      System.out.println("Given License is not present in the system");	
				      logger.log(LogStatus.FAIL, "Given License is not present in the system");
			
			           }
	}
	
	 catch (Exception e) {
		try {

			logger.log(LogStatus.FAIL, "activateLicenses  :: Failed " + e.getMessage());
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));			
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
			tearDown();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "activateLicenses :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);
	 }
}



/*******************************************************************
-  Description: Property Level License DeActivate

- Output: License will be activated at property level for OPERA MOBILE
- Author: Shireesha
- Date:01/02/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: Shireesha

********************************************************************/
public static void property_DeactivateLicense(HashMap<String, String> configMap) throws Exception{
	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	try {
		
		//navigating to tool Box 
		Utils.waitForPageLoad(100);
		click("Exports.Mapping_mainMenu",100,"clickable");
		Utils.waitForSpinnerToDisappear(100);
		click("Exports.ToolBoxMenu",100,"clickable");
		Utils.waitForPageLoad(300);
		
		// Navigation to System Setup
		click("Exports.ToolBoxsystemSetup",100,"clickable");

		// Navigating to OPERA Licenses
		click("Exports.ToolBoxOperaLicenses",100,"clickable");

			//Select the property radio button
		click("Exports.OperaLicense_radiobtn",100,"clickable");
		waitForSpinnerToDisappear(30);
		
		//getting the property value from the data sheet
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		propertyMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY", propertyMap.get("Set"));
		System.out.println("Property Config: " + propertyMap);

		String property = propertyMap.get("Property");
		System.out.println("prop: " + property);

		//Click on Search Button
		click("Exports.OperaLicense_searchbtn",100,"clickable");
		waitForSpinnerToDisappear(500);
		
		//Fetch Licenses table Column Data and validate
				java.util.List<WebElement>  rows = Utils.elements("Exports.OperaLicense_tblAvailableLicenses"); 
				System.out.println("No of rows are : " + rows.size());
				boolean flag = true;	
				if(rows.size() > 0)
				{
					for(int i = 0; i < rows.size(); i++)
					{
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
						if(rows.get(i).getText().contains(configMap.get("APPLICATION")))
						{
							System.out.println("License "+rows.get(i).getText()+" is listed");
							
							flag = false;
							logger.log(LogStatus.PASS, "License "+rows.get(i).getText()+" is listed");
							// click on the license
							click(rows.get(i));
						
							waitForSpinnerToDisappear(30);
							//dynamically constructing the Xpath to validate the Activate checkbox																		 
								String strXPath = "((//*[@data-ocid='TBL_TS1']/div[2]/table/tbody/tr/td[1])["+ (i+1) +"]/following::*[@data-ocid[contains(.,'CHK_CNTNT_SBC1')]]/*/img)[1]";
							if(driver.findElement(By.xpath(strXPath)).getAttribute("title").equals("Checked"))
								{
								System.out.println("License "+rows.get(i).getText()+" started license Deactivation");
									logger.log(LogStatus.PASS, "License "+rows.get(i).getText()+" started license Deactivation");
									
									//Disabling license 
									mouseHover("Exports.licenseDetails_Switch");
									
									jsClick("Exports.licenseDetails_Switch");
									Utils.waitForSpinnerToDisappear(300);

							
									/*isExists("Exports.popupConfirmation");
									{

									//Click on yes on the Confirmation popup
									click("Exports.popupConfirmation.btnYes",100,"clickable");
									Utils.waitForSpinnerToDisappear(100);	
									}*/
									
									boolean strflag = isDisplayed("Exports.LicenseDetails_btnActivate", "Activate");
									System.out.println (strflag);

									if(strflag == true)
										logger.log(LogStatus.PASS, "Property Level license "+rows.get(i).getText()+" is disabled successfully");
									else
										logger.log(LogStatus.FAIL, "Failed to disable property level license "+rows.get(i).getText());
								}
								
								else{
									System.out.println("License "+rows.get(i).getText()+" is already Deactivated");
									logger.log(LogStatus.PASS, "License "+rows.get(i).getText()+" is already Deactivated");
									  }
							break;
						}
					}	
					
				}
	else
			          {
				      System.out.println("Given License is not present in the system");	
				      logger.log(LogStatus.FAIL, "Given License is not present in the system");
			
			           }
	}
	
	 catch (Exception e) {
		try {

			logger.log(LogStatus.FAIL, "activateLicenses  :: Failed " + e.getMessage());
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));			
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
			tearDown();
		}
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "activateLicenses :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);
	 }
}

/*******************************************************************
- Description: Run and RDF report and preview
- Input: REPORT_GROUP, REPORT_NAME
- Output: 
- Author: srikanth konda
- Date:20/12/2018
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void RunRDFReportFromHub(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	try {
	
	/* Switch to Hub */
	Map<String, String> configDataMap = new HashMap<String, String>();
	configDataMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
	configDataMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configDataMap.get("Set"));
	System.out.println("Config: " + configDataMap);
	String hub = configDataMap.get("HUB");
	
	String greeting = Utils.getText("Reports.homepage_UserOptions");
	System.out.println(greeting);
	Thread.sleep(2000);

	if (greeting.contains(hub)) {// OR.getConfig("property")
		System.out.println("Landed in Hub mode: " + greeting);
		System.out.println("*******************************************************************");
		Utils.takeScreenshot("Homepage");
		logger.log(LogStatus.PASS, "Landed in Hub mode:: " + greeting);
		
	} else {

		// Select header
		Utils.implicitWait(30);
		Utils.WebdriverWait(100, "Reports.homepage_UserOptions", "presence");
		Utils.click(Utils.element("Reports.homepage_UserOptions"), false);
		System.out.println("Clicked on header....");
		logger.log(LogStatus.PASS, "Clicked on header....");
		
		

		Utils.WebdriverWait(100, "Reports.homepage_selectLocation", "presence");
		Utils.jsClick("Reports.homepage_selectLocation");
		System.out.println("Location Dropdown selected");
		logger.log(LogStatus.PASS, "Location Dropdown selected");
		waitForSpinnerToDisappear(45);
		
		Utils.WebdriverWait(100, "Reports.homepage_Hub", "presence");
		Utils.jsClick("Reports.homepage_Hub");
		System.out.println("Clicked on hub radio button");
		
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Reports.homepage_SearchText", "presence");
		Utils.textBox("Reports.homepage_SearchText", hub);
		//Thread.sleep(2000);
		System.out.println("Provided hub in textbox....");
		logger.log(LogStatus.PASS, "Provided desired hub in textbox....");

		Utils.WebdriverWait(100, "Reports.homepage_Search", "clickable");
		Utils.jsClick("Reports.homepage_Search");
		waitForSpinnerToDisappear(45);
		System.out.println("Selected search btn....");

		Utils.WebdriverWait(100, "Reports.homepage_Select_btn", "clickable");
		Utils.jsClick("Reports.homepage_Select_btn");
		System.out.println("Select select btn for hub..." + hub);
		logger.log(LogStatus.PASS, "Select select btn for hub..." + hub);
		waitForSpinnerToDisappear(100);
		
	}
		
		// Navigating to Reports menu
				Utils.WebdriverWait(100, "Reports.menu_Reports", "clickable");
				Utils.mouseHover("Configuration.menu_Reports");
				Utils.click(Utils.element("Reports.menu_Reports"));
				System.out.println("Selected Reports menu");
				logger.log(LogStatus.PASS, "Selected Reports Menu");
				Thread.sleep(5000);
				
				// Navigating to configure reports
				mouseHover("Reports.menu_ConfigureReports",100,"clickable");
				Thread.sleep(5000);
				

				// search for the report to see if its already present
				mouseHover("Reports.menu_CreateReports",100,"presence");
				jsClick("Reports.menu_CreateReports",100,"presence");
				org.testng.Assert.assertEquals(getText("Reports.headerCreateReport", 100, "presence"), "Create Reports");
				logger.log(LogStatus.PASS, "Landed in Create Reports Page");
				Thread.sleep(5000);
				
				textBox("Reports.txt_ReportName", configMap.get("FileName"), 100, "presence");
				Utils.tabKey("Reports.txt_ReportName");
				waitForSpinnerToDisappear(100);
				
				selectBy("Reports.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
				//Utils.Wait(4000);
				Utils.tabKey("Reports.drpdwn_ReportGroup");
				mouseHover("Configuration.txt_DisplayName",100,"presence");
				waitForSpinnerToDisappear(100);
				textBox("Reports.txt_DisplayName", configMap.get("ReportName"), 100, "presence");
				
				Utils.tabKey("Configuration.txt_DisplayName");
				waitForSpinnerToDisappear(100);
				mouseHover("Reports.btnSave",100,"presence");
				jsClick("Configuration.btnSave");
				waitForSpinnerToDisappear(100);
				
				try {
				
			boolean	flag = Utils.isDisplayed("Reports.alert_ReportExists","Report exists alert");
			if (flag) {
			mouseHover("Reports.ok_alert",100,"presence");
			jsClick("Reports.ok_alert");
			}
			}
				catch(Exception e) {
					e.printStackTrace();
				}
				
	
		
		// Navigating to Reports menu
		Utils.WebdriverWait(100, "Reports.menu_Reports", "clickable");
		Utils.mouseHover("Reports.menu_Reports");
		Utils.click(Utils.element("Reports.menu_Reports"));
		System.out.println("Selected Reports menu");
		logger.log(LogStatus.PASS, "Selected Reports Menu");
		Thread.sleep(5000);
		
		// Navigating to Run Reports
		mouseHover("Reports.menu_RunReports",100,"clickable");
		Thread.sleep(5000);
		jsClick("Reports.menu_RunReports",100,"presence");
		
		org.testng.Assert.assertEquals(getText("Reports.headerRunReports", 100, "presence"), "Run Reports");
		logger.log(LogStatus.PASS, "Landed in Run Reports Page");
		Thread.sleep(5000);
		
		
		
		// search for the report to see if its already present
		Boolean flag = false;
		textBox("Reports.txt_ReportName", configMap.get("ReportName"), 100, "presence");
		Utils.tabKey("Reports.txt_ReportName");
		// select the Report Group form drop-down
		mouseHover("Reports.drpdwn_ReportGroup",100,"clickable");
		selectBy("Reports.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
		waitForSpinnerToDisappear(100);
		Thread.sleep(4000);
		
		Utils.WebdriverWait(30, "Reports.btnSearch","clickable");
		Utils.click(Utils.element("Reports.btnSearch"));
		Utils.Wait(4000);
	

		
		try {
		
		flag = driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportName")+"']")).isDisplayed();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if (flag) {
			
			driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportName")+"']")).click();
			
			
			//mouseHover("Reports.btnSelect",100,"clickable");
			Thread.sleep(5000);
			jsClick("Reports.btnSelect",100,"presence");
			System.out.println("Clicked on select button");
			logger.log(LogStatus.PASS, "Clicked on select button");
			Thread.sleep(5000);
			
			Utils.WebdriverWait(100, "Reports.btnNext", "clickable");
			Utils.mouseHover("Reports.btnNext");
			Utils.click(Utils.element("Reports.btnNext"));
			System.out.println("Clicked on Next button");
			logger.log(LogStatus.PASS, "Clicked on Next button");
			Thread.sleep(5000);
			Utils.WebdriverWait(100, "Reports.radioPreviewToPrint", "clickable");
			Utils.mouseHover("Reports.radioPreviewToPrint");
			Utils.click(Utils.element("Reports.radioPreviewToPrint"));
			
			Utils.WebdriverWait(100, "Reports.btnProcess", "clickable");
			Utils.mouseHover("Reports.btnProcess");
			Utils.click(Utils.element("Reports.btnProcess"));
			System.out.println("Clicked on Process button");
			logger.log(LogStatus.PASS, "Clicked on Process button");
			
			Utils.Wait(30000);
						String parentWindow = driver.getWindowHandle();
							Set<String> windows = driver.getWindowHandles();	
							
							for (String window:windows) {
								
								driver.switchTo().window(window);
								
							}
							
				if (driver.getPageSource().contains("reportviewer")) {
					System.out.println("Report preview successful");
					logger.log(LogStatus.PASS, "Report preview successful");
				} else {
					System.out.println("Report preview unsuccessful");
					logger.log(LogStatus.PASS, "Report preview unsuccessful");
				}
			
							
				driver.close();
			driver.switchTo().window(parentWindow);
			
			
		} else {
			logger.log(LogStatus.PASS, "Report: " + configMap.get("ReportName") + " not found in search");
			System.out.println("Report: " + configMap.get("ReportName") + " not found in search");
			
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
- Description: Run and RDF report and preview
- Input: REPORT_GROUP, REPORT_NAME
- Output: 
- Author: srikanth konda
- Date:05/02/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void RunBIPReportFromHub(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	try {
	
	/* Switch to Hub */
	Map<String, String> configDataMap = new HashMap<String, String>();
	configDataMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
	configDataMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configDataMap.get("Set"));
	System.out.println("Config: " + configDataMap);
	String hub = configDataMap.get("HUB");
	
	String greeting = Utils.getText("Reports.homepage_UserOptions");
	System.out.println(greeting);
	Thread.sleep(2000);

	if (greeting.contains(hub)) {// OR.getConfig("property")
		System.out.println("Landed in Hub mode: " + greeting);
		System.out.println("*******************************************************************");
		Utils.takeScreenshot("Homepage");
		logger.log(LogStatus.PASS, "Landed in Hub mode:: " + greeting);
		
	} else {

		// Select header
		Utils.implicitWait(30);
		Utils.WebdriverWait(100, "Reports.homepage_UserOptions", "presence");
		Utils.click(Utils.element("Reports.homepage_UserOptions"), false);
		System.out.println("Clicked on header....");
		logger.log(LogStatus.PASS, "Clicked on header....");
		
		

		Utils.WebdriverWait(100, "Reports.homepage_selectLocation", "presence");
		Utils.jsClick("Reports.homepage_selectLocation");
		System.out.println("Location Dropdown selected");
		logger.log(LogStatus.PASS, "Location Dropdown selected");
		waitForSpinnerToDisappear(45);
		
		Utils.WebdriverWait(100, "Reports.homepage_Hub", "presence");
		Utils.jsClick("Reports.homepage_Hub");
		System.out.println("Clicked on hub radio button");
		
		waitForPageLoad(100);
		Utils.WebdriverWait(100, "Reports.homepage_SearchText", "presence");
		Utils.textBox("Reports.homepage_SearchText", hub);
		//Thread.sleep(2000);
		System.out.println("Provided hub in textbox....");
		logger.log(LogStatus.PASS, "Provided desired hub in textbox....");

		Utils.WebdriverWait(100, "Reports.homepage_Search", "clickable");
		Utils.jsClick("Reports.homepage_Search");
		waitForSpinnerToDisappear(45);
		System.out.println("Selected search btn....");

		Utils.WebdriverWait(100, "Reports.homepage_Select_btn", "clickable");
		Utils.jsClick("Reports.homepage_Select_btn");
		System.out.println("Select select btn for hub..." + hub);
		logger.log(LogStatus.PASS, "Select select btn for hub..." + hub);
		waitForSpinnerToDisappear(100);
		
	}
		
		// Navigating to Reports menu
				Utils.WebdriverWait(100, "Reports.menu_Reports", "clickable");
				Utils.mouseHover("Configuration.menu_Reports");
				Utils.click(Utils.element("Reports.menu_Reports"));
				System.out.println("Selected Reports menu");
				logger.log(LogStatus.PASS, "Selected Reports Menu");
				Thread.sleep(5000);
				
				// Navigating to configure reports
				mouseHover("Reports.menu_ConfigureReports",100,"clickable");
				Thread.sleep(5000);
				

				// search for the report to see if its already present
				mouseHover("Reports.menu_CreateReports",100,"presence");
				jsClick("Reports.menu_CreateReports",100,"presence");
				org.testng.Assert.assertEquals(getText("Reports.headerCreateReport", 100, "presence"), "Create Reports");
				logger.log(LogStatus.PASS, "Landed in Create Reports Page");
				Thread.sleep(5000);
				

				mouseHover("Reports.radio_RTF",100,"presence");
				jsClick("Reports.radio_RTF",100,"presence");
				Utils.Wait(6000);
				
				Boolean flag = false;
			
				jsClick("Reports.Reports_LOV_rtfFile");
				Utils.WebdriverWait(100, "Reports.select_rtf_firstRecord", "clickable");
				Utils.mouseHover("Reports.select_rtf_firstRecord");
				jsClick("Reports.select_rtf_firstRecord");
				waitForSpinnerToDisappear(10);			
				Utils.mouseHover("Reports.searchSelectButton");
				jsClick("Reports.searchSelectButton");
				waitForSpinnerToDisappear(10);
				Utils.Wait(4000);
				Utils.WebdriverWait(100, "Reports.Reports_LOV_odtfile", "clickable");
				Utils.mouseHover("Reports.Reports_LOV_odtfile");
				jsClick("Reports.Reports_LOV_odtfile");
				Utils.WebdriverWait(100, "Reports.select_odt_firstRecord", "clickable");
				jsClick("Reports.select_odt_firstRecord");
				Utils.Wait(3000);
				Utils.mouseHover("Reports.odt_searchSelectButton");
				jsClick("Reports.odt_searchSelectButton");
				waitForSpinnerToDisappear(10);	
				Utils.Wait(6000);
				
				
				selectBy("Reports.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
				//Utils.Wait(4000);
				Utils.tabKey("Reports.drpdwn_ReportGroup");
				mouseHover("Reports.txt_DisplayName",100,"presence");
				Utils.Wait(3000);
				textBox("Reports.txt_DisplayName", configMap.get("RTF_ReportName"), 100, "presence");
				Utils.tabKey("Reports.txt_DisplayName");
				Utils.Wait(3000);
				mouseHover("Reports.btnSave",100,"presence");
				jsClick("Reports.btnSave");
				waitForSpinnerToDisappear(100);
				
					try {
				
			flag = Utils.isDisplayed("Reports.alert_ReportExists","Report exists alert");
			if (flag) {
			mouseHover("Reports.ok_alert",100,"presence");
			jsClick("Reports.ok_alert");
			}
			}
				catch(Exception e) {
					e.printStackTrace();
				}
				
	
		
		// Navigating to Reports menu
		Utils.WebdriverWait(100, "Reports.menu_Reports", "clickable");
		Utils.mouseHover("Reports.menu_Reports");
		Utils.click(Utils.element("Reports.menu_Reports"));
		System.out.println("Selected Reports menu");
		logger.log(LogStatus.PASS, "Selected Reports Menu");
		Thread.sleep(5000);
		
		// Navigating to Run Reports
		mouseHover("Reports.menu_RunReports",100,"clickable");
		Thread.sleep(5000);
		jsClick("Reports.menu_RunReports",100,"presence");
		
		org.testng.Assert.assertEquals(getText("Reports.headerRunReports", 100, "presence"), "Run Reports");
		logger.log(LogStatus.PASS, "Landed in Run Reports Page");
		Thread.sleep(5000);
		
		
		
		// search for the report to see if its already present
		flag = false;
		textBox("Reports.txt_ReportName", configMap.get("RTF_ReportName"), 100, "presence");
		Utils.tabKey("Reports.txt_ReportName");
		// select the Report Group form drop-down
		mouseHover("Reports.drpdwn_ReportGroup",100,"clickable");
		selectBy("Reports.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
		waitForSpinnerToDisappear(100);
		Thread.sleep(4000);
		
		Utils.WebdriverWait(30, "Reports.btnSearch","clickable");
		Utils.click(Utils.element("Reports.btnSearch"));
		Utils.Wait(4000);
		waitForSpinnerToDisappear(100);
	

		
		try {
		
		flag = driver.findElement(By.xpath("//span[text()='"+configMap.get("RTF_ReportName")+"']")).isDisplayed();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if (flag) {
			
			driver.findElement(By.xpath("//span[text()='"+configMap.get("RTF_ReportName")+"']")).click();
			
			
			//mouseHover("Reports.btnSelect",100,"clickable");
			Thread.sleep(5000);
			jsClick("Reports.btnSelect",100,"presence");
			System.out.println("Clicked on select button");
			logger.log(LogStatus.PASS, "Clicked on select button");
			Thread.sleep(5000);
			
			Utils.WebdriverWait(100, "Reports.btnNext", "clickable");
			Utils.mouseHover("Reports.btnNext");
			Utils.click(Utils.element("Reports.btnNext"));
			System.out.println("Clicked on Next button");
			logger.log(LogStatus.PASS, "Clicked on Next button");
			Thread.sleep(5000);
			Utils.WebdriverWait(100, "Reports.radioPreviewToPrint", "clickable");
			Utils.mouseHover("Reports.radioPreviewToPrint");
			Utils.click(Utils.element("Reports.radioPreviewToPrint"));
			
			Utils.WebdriverWait(100, "Reports.btnProcess", "clickable");
			Utils.mouseHover("Reports.btnProcess");
			Utils.click(Utils.element("Reports.btnProcess"));
			System.out.println("Clicked on Process button");
			logger.log(LogStatus.PASS, "Clicked on Process button");
			
			Utils.Wait(30000);
						String parentWindow = driver.getWindowHandle();
							Set<String> windows = driver.getWindowHandles();	
							
							for (String window:windows) {
								
								driver.switchTo().window(window);
								
							}
							
				if (driver.getPageSource().contains("reportviewer")) {
					System.out.println("Report preview successful");
					logger.log(LogStatus.PASS, "Report preview successful");
				} else {
					System.out.println("Report preview unsuccessful");
					logger.log(LogStatus.PASS, "Report preview unsuccessful");
				}
			
							
				driver.close();
			driver.switchTo().window(parentWindow);
			
			
		} else {
			logger.log(LogStatus.PASS, "Report: " + configMap.get("RTF_ReportName") + " not found in search");
			System.out.println("Report: " + configMap.get("RTF_ReportName") + " not found in search");
			
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
- Description: OPERA Help is accessible
- Input: 
- Output: 
- Author: srikanth konda
- Date:20/12/2018
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void OperaHelpAccess(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	
	try {
		
		// Navigating to Reports menu
		Utils.WebdriverWait(100, "Configuration.mainMenu", "clickable");
		Utils.mouseHover("Configuration.mainMenu");
		Utils.click(Utils.element("Configuration.mainMenu"));
		System.out.println("Selected main menu");
		logger.log(LogStatus.PASS, "Selected main Menu");
		
		// Navigating to help screen
		Utils.WebdriverWait(100, "Configuration.Help", "clickable");
		Utils.mouseHover("Configuration.Help");
		Utils.click(Utils.element("Configuration.Help"));
		System.out.println("Selected Help link");
		logger.log(LogStatus.PASS, "Selected Help link");
		
		Wait(5000);
		Set<String> windows = driver.getWindowHandles();
		for (String window: windows) {
			driver.switchTo().window(window);
		}			
			
		boolean flag = false;
		try {
			
		 flag = driver.findElement(By.xpath("//*[text()='Hospitality OPERA Cloud Services User Guide']")).isDisplayed();

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
				if (flag) {
					System.out.println("OPERA Help screen launched successfully");
					logger.log(LogStatus.PASS, "OPERA Help screen launched successfully");
				} else {
					System.out.println("OPERA Help screen not launched");
					logger.log(LogStatus.PASS, "OPERA Help screen not launched");
				}
			
							

	}
		catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			/*logger.log(LogStatus.FAIL, "Create Report Group  :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));*/
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		/*logger.log(LogStatus.FAIL, "Report Group not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));*/
		throw (e);

	}


}
/*******************************************************************
- Description: View logs through Settings jump
- Input: 
- Output: 
- Author: srikanth konda
- Date:03/08/2019
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void ViewLogsViaSettings(HashMap<String, String> configMap) throws Exception {

	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);

	
	try {
		
		// Navigating to Reports menu
		Utils.WebdriverWait(100, "Configuration.mainMenu", "clickable");
		Utils.mouseHover("Configuration.mainMenu");
		Utils.click(Utils.element("Configuration.mainMenu"));
		System.out.println("Selected main menu");
		logger.log(LogStatus.PASS, "Selected main Menu");
		
		// Navigating to Settings screen
		Utils.WebdriverWait(100, "Configuration.mainMenu.MenuSettings", "clickable");
		Utils.mouseHover("Configuration.mainMenu.MenuSettings");
		Utils.click(Utils.element("Configuration.mainMenu.MenuSettings"));
		System.out.println("Selected Settings link");
		logger.log(LogStatus.PASS, "Selected Settings link");
		
		// Navigating to View Logs screen
		Utils.WebdriverWait(100, "Configuration.mainMenu.ViewLogs", "clickable");
		Utils.mouseHover("Configuration.mainMenu.ViewLogs");
		Utils.click(Utils.element("Configuration.mainMenu.ViewLogs"));
		System.out.println("Selected View Logs link");
		logger.log(LogStatus.PASS, "Selected View Logs link");
		
		
		Wait(5000);
		Set<String> windows = driver.getWindowHandles();
		for (String window: windows) {
			driver.switchTo().window(window);
		}			
			
		boolean flag = false;
		try {
			
		 flag = driver.findElement(By.xpath("//*[text()='In-Memory Log Viewer']")).isDisplayed();
		 

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
				if (flag) {
					System.out.println("In-Memory Log Viewer screen launched successfully");
					logger.log(LogStatus.PASS, "In-Memory Log Viewer screen launched successfully");
				} else {
					System.out.println("In-Memory Log Viewer screen not launched");
					logger.log(LogStatus.PASS, "In-Memory Log Viewer screen not launched");
				}
			
							

	}
		catch (Exception e) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			/*logger.log(LogStatus.FAIL, "Create Report Group  :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));*/
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, ScriptName);
		/*logger.log(LogStatus.FAIL, "Report Group not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));*/
		throw (e);

	}


}
/*******************************************************************
- Description: Run report and download
- Input: REPORT_GROUP, REPORT_NAME
- Output: 
- Author: srikanth konda
- Date:20/12/2018
- Revision History:
                - Change Date 
                - Change Reason
                - Changed Behavior
                - Last Changed By: 

********************************************************************/


public static void DownloadReport(HashMap<String, String> configMap) throws Exception {

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
		
		// Navigating to Run Reports
		mouseHover("Configuration.menu_RunReports",100,"clickable");
		Thread.sleep(5000);
		jsClick("Configuration.menu_RunReports",100,"presence");
		
		org.testng.Assert.assertEquals(getText("Configuration.headerRunReports", 100, "presence"), "Run Reports");
		logger.log(LogStatus.PASS, "Landed in Run Reports Page");
		Thread.sleep(5000);
		
		
		
		// search for the report to see if its already present
		Boolean flag = false;
		textBox("Configuration.txt_ReportName", configMap.get("ReportName"), 100, "presence");
		Utils.tabKey("Configuration.txt_ReportName");

		mouseHover("Configuration.Reports.chk_ShowInternal",100,"clickable");
		Utils.click(Utils.element("Configuration.Reports.chk_ShowInternal"));
		Utils.Wait(3000);
		
		mouseHover("Configuration.ManageRes.drpdwn_ReportGroup",100,"clickable");
		selectBy("Configuration.ManageRes.drpdwn_ReportGroup", "text", configMap.get("ReportGroup"));
	
		Utils.tabKey("Configuration.ManageRes.drpdwn_ReportGroup");
		Utils.mouseHover("Configuration.btnSearch");
		Utils.click(Utils.element("Configuration.btnSearch"));
		Utils.Wait(2000);
	

		
		try 
		{
			if(Utils.isExists("Configuration.Reports.btnExpand"))
			{
				Utils.click("Configuration.Reports.btnExpand");
				Utils.waitForSpinnerToDisappear(20);
			}
			flag = driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportName")+"']")).isDisplayed();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if (flag) {
			
			driver.findElement(By.xpath("//span[text()='"+configMap.get("ReportName")+"']")).click();
			
			// Navigating to Reports menu
			Utils.WebdriverWait(100, "Configuration.Reports.btnSelect", "clickable");
			Utils.mouseHover("Configuration.Reports.btnSelect");
			Utils.click(Utils.element("Configuration.Reports.btnSelect"));
			System.out.println("Clicked on select button");
			logger.log(LogStatus.PASS, "Clicked on select button");
			Thread.sleep(5000);
			
			Utils.WebdriverWait(100, "Configuration.Reports.btnNext", "clickable");
			Utils.mouseHover("Configuration.Reports.btnNext");
			Utils.click(Utils.element("Configuration.Reports.btnNext"));
			System.out.println("Clicked on Next button");
			logger.log(LogStatus.PASS, "Clicked on Next button");
			Thread.sleep(5000);
			Utils.WebdriverWait(100, "Configuration.Reports.Download", "clickable");
			Utils.mouseHover("Configuration.Reports.Download");
			Utils.click(Utils.element("Configuration.Reports.Download"));
			System.out.println("Clicked on Download radio button");
			logger.log(LogStatus.PASS, "Clicked on  Download radio button");
			waitForSpinnerToDisappear(10);
			Utils.WebdriverWait(100, "Configuration.Reports.btnProcess", "clickable");
			Utils.mouseHover("Configuration.Reports.btnProcess");
			Utils.click(Utils.element("Configuration.Reports.btnProcess"));
			System.out.println("Clicked on Process button");
			logger.log(LogStatus.PASS, "Clicked on Process button");
			
			Utils.Wait(30000);
						/*String parentWindow = driver.getWindowHandle();
							Set<String> windows = driver.getWindowHandles();	
							
							for (String window:windows) {
								
								driver.switchTo().window(window);
								
							}
							
				if (driver.getPageSource().contains("reportviewer")) {
					System.out.println("Report preview successful");
					Actions action = new Actions(driver);
					action.sendKeys(driver.findElement(By.xpath("//embed")),Keys.chord(Keys.CONTROL,"a"),Keys.chord(Keys.CONTROL,"c")).perform();
					logger.log(LogStatus.PASS, "Report preview successful");
				} else {
					System.out.println("Report preview unsuccessful");
					logger.log(LogStatus.PASS, "Report preview unsuccessful");
				}*/
			
							
				//driver.close();
			//driver.switchTo().window(parentWindow);
			
			
		} else {
			logger.log(LogStatus.PASS, "Report: " + configMap.get("ReportName") + " not found in search");
			System.out.println("Report: " + configMap.get("ReportName") + " not found in search");
			
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

}














