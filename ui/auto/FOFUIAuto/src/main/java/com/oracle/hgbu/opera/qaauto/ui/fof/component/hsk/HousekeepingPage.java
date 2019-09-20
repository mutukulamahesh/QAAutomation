package com.oracle.hgbu.opera.qaauto.ui.fof.component.hsk;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;
public class HousekeepingPage extends Utils{
	
	/*******************************************************************
	-  Description: Check if the searched room is an even room
	- Input:From, To Room and Even room radio button
	- Output: Even Room should be displayed
	- Author: jasatis
	- Date: 12/10/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void evenRoom(HashMap<String, String> configMap) throws Exception {

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
			// Navigating to Inventory menu
			Utils.click("Housekeeping.menu_Inventory", 100, "clickable");
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");
			// Navigating to Accommodation Management menu
			Utils.click("Housekeeping.menu_RoomManagement", 100, "clickable");
			System.out.println("clicked Room Management menu");
			logger.log(LogStatus.PASS, "Selected Room Management Menu");
			// Navigating to Room Class  
			Utils.click("Housekeeping.menu_housekeepingboard", 100, "clickable");
			System.out.println("clicked House Keeping Board Menu");
			logger.log(LogStatus.PASS, "Selected House Keeping Board Menu");
			waitForPageLoad(100);
			Thread.sleep(5000);
			boolean isBoardExists = isExists("Housekeeping.housekeepingboard");
			if(isBoardExists)
			{
				textBox("Housekeeping.txt_fromRoom",configMap.get("ROOM"));
				tabKey("Housekeeping.txt_fromRoom");	
				System.out.println("Provided the From Room ");
				logger.log(LogStatus.PASS, "Provided From Room");
				waitForSpinnerToDisappear(30);
				
				textBox("Housekeeping.txt_toRoom",configMap.get("ROOM"));
				tabKey("Housekeeping.txt_toRoom");	
				System.out.println("Provided the To Room ");
				logger.log(LogStatus.PASS, "Provided To Room");
				waitForSpinnerToDisappear(30);
			
				if(isExists("Housekeeping.rad_evenroom"))
				{
					if(!element("Housekeeping.rad_evenroom").isSelected())
					{
						jsClick("Housekeeping.rad_evenroom");
						logger.log(LogStatus.PASS, "Selected the Even Room Radio button");
					}
				}
				jsClick("Housekeeping.btn_Searchbutton", 100, "presence");
				System.out.println("Clicked on Search Button ");
				waitForSpinnerToDisappear(30);
			//Fetch Search Grid Column Data and Validate
			List<WebElement>  rows = Utils.elements("Housekeeping.grd_SearchRooms_ColData"); 
			System.out.println("No of rows are : " + rows.size());
			boolean flag = false;
			if(rows.size() > 0)
			{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("ROOM")))
					{
						System.out.println("Room"+rows.get(i).getText()+" is an Even number");
						flag = true;
						logger.log(LogStatus.PASS, "Room "+rows.get(i).getText()+" is an Even number");
						break;
					}
					else
					{
						System.out.println("Room  is not present in the system:: "+rows.get(i).getText());	
						logger.log(LogStatus.FAIL, "Room "+rows.get(i).getText()+" is not an Even number");
					}
				}
			}
		}
			else
			{
				logger.log(LogStatus.FAIL, "Issue observed while searching an Even number");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Room search is :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}
	/*******************************************************************
	-  Description: Check if the searched room is an odd room
	- Input:From, To Room and Odd room radio button
	- Output: Odd Room should be displayed
	- Author: jasatis
	- Date: 12/10/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void oddRoom(HashMap<String, String> configMap) throws Exception {

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

			Utils.click("Housekeeping.menu_Inventory", 100, "clickable");
			System.out.println("Clicked Inventory menu");
			logger.log(LogStatus.PASS, "Selected Inventory Menu");
			// Navigating to Accommodation Management menu
			Utils.click("Housekeeping.menu_RoomManagement", 100, "clickable");
			System.out.println("clicked Room Management menu");
			logger.log(LogStatus.PASS, "Selected Room Management Menu");
			// Navigating to Room Class 
			Utils.click("Housekeeping.menu_housekeepingboard", 100, "clickable");
			System.out.println("clicked House Keeping Board Menu");
			logger.log(LogStatus.PASS, "Selected House Keeping Board Menu");
			waitForPageLoad(100);
			Utils.Wait(5000);
			boolean isBoardExists = isExists("Housekeeping.housekeepingboard");
			if(isBoardExists)
			{
				textBox("Housekeeping.txt_fromRoom",configMap.get("ROOM"));
				tabKey("Housekeeping.txt_fromRoom");	
				System.out.println("Provided the From Room ");
				logger.log(LogStatus.PASS, "Provided From Room");
				waitForSpinnerToDisappear(30);
				textBox("Housekeeping.txt_toRoom",configMap.get("ROOM"));
				tabKey("Housekeeping.txt_toRoom");	
				System.out.println("Provided the To Room ");
				logger.log(LogStatus.PASS, "Provided To Room");
				waitForSpinnerToDisappear(30);
				if(isExists("Housekeeping.rad_oddroom"))
				{
					if(!element("Housekeeping.rad_oddroom").isSelected())
					{
						jsClick("Housekeeping.rad_oddroom");
						logger.log(LogStatus.PASS, "Selected the Odd Room Radio button");
					}
				}
				jsClick("Housekeeping.btn_Searchbutton", 100, "presence");
				System.out.println("Clicked on Search Button ");
				waitForSpinnerToDisappear(30);
				//Fetch Search Grid Column Data and validate
				List<WebElement>  rows = Utils.elements("Housekeeping.grd_SearchRooms_ColData"); 
				System.out.println("No of rows are : " + rows.size());
				boolean flag = false;
				if(rows.size() > 0)
				{
				for(int i=0;i<rows.size();i++)
				{
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rows.get(i));
					if(rows.get(i).getText().equalsIgnoreCase(configMap.get("ROOM")))
					{
						System.out.println("Room "+rows.get(i).getText()+" is an Odd Room");
						flag = true;
						logger.log(LogStatus.PASS, "Room "+rows.get(i).getText()+" is an Odd Room");
						break;
					}
					else
					{
						System.out.println("Room  is not present in the system:: "+rows.get(i).getText());
						logger.log(LogStatus.FAIL, "Room "+rows.get(i).getText()+" is not an Odd Room");
					}
				}
			}
		}
			else
			{
				logger.log(LogStatus.FAIL, "Issue Observed while searching the odd room");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Room search is :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}
	
	/*******************************************************************
	- Description: Start the Room from Task Sheet Companion, Update Room Status and Finish
	- Input: Room Number, Room Status to update
	- Output: 
	- Author: Praneeth
	- Date: 06/02/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void startUpdateAndFinishRoomInTaskSheetCompanion(String roomNumber, String roomStatusLocator) throws Exception 
	{

		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		try {
			// Select the Room From Task Sheet Companion
			Utils.clickOnElementBasedOnTextContains("Housekeeping.TaskSheet.tblTaskSheetCompanionRoomsList", roomNumber, "textContent");
			Utils.waitForSpinnerToDisappear(20);
			// Click on Start to Start the Room in Task Sheet Companion
			System.out.println("Starting the Room: '"+roomNumber+"'");
			Utils.click("Housekeeping.TaskSheet.btnStartRoom");
			Utils.waitForSpinnerToDisappear(30);
			//Update Room Status
			System.out.println("Updating the Room Status to : '"+roomStatusLocator+"'");
			Utils.click(roomStatusLocator);
			Utils.waitForSpinnerToDisappear(10);
			//Click on Finish Button
			System.out.println("Finishing the Room: '"+roomNumber+"'");
			Utils.click("Housekeeping.TaskSheet.btnFinishRoom");
			Utils.waitForSpinnerToDisappear(50);
			if(Utils.isExists("Housekeeping.TaskSheet.tblTaskSheetCompanionRoomsList", roomNumber, "textContent"))
			{
				logger.log(LogStatus.FAIL, "Room: '" + roomNumber + "' still exists in the Task Sheet Companion after finishing");
			}
			else
			{
				logger.log(LogStatus.PASS, "Successfully started, updated and Finished the Room: '" + roomNumber + "' from Task Sheet Companion");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Failed in performing Room operations from Task Sheet Companion :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);
		}
	}
	
	/*******************************************************************
	- Description: Create Service Request (Inventory - Room Management - Service Requests)
	- Input:Room, Service Request Code, Service Request Priority, Remarks
	- Output: New Service Request created
	- Author: Praneeth
	- Date: 08/03/19
	- Revision History:
	********************************************************************/
	public static void createServiceRequest(HashMap<String, String> srMap) throws Exception {
	String ScriptName = Utils.getMethodName();
	System.out.println("name: " + ScriptName);
	System.out.println("Map: " + srMap);
	try 
	{
		// Navigating to Inventory menu
		mouseHover("Housekeeping.inventory_menu");
		click("Housekeeping.inventory_menu",100,"Clickable");

		// Navigating to Room Management
		mouseHover("Housekeeping.menu_RoomManagement");
		click("Housekeeping.menu_RoomManagement",100,"clickable");

		// Navigating to Service Requests
		click("Housekeeping.menu_ServiceRequests", 100, "clickable");
		waitForSpinnerToDisappear(10);
		
		//Check if Application is navigated to Service Requests Screen
		boolean blnPageExists = isExists("Housekeeping.ServiceRequests.hdr_ServiceRequests");
		if(blnPageExists)
		{
			logger.log(LogStatus.PASS, "Successfully navigated to Service Requests screen");
			
			//Click on New
			click("Housekeeping.ServiceRequests.link_New", 100, "clickable");
			waitForSpinnerToDisappear(20);
			
			//Enter Room
			Utils.textBox("Housekeeping.ServiceRequests.txt_Room", srMap.get("ROOM"));
			Utils.tabKey("Housekeeping.ServiceRequests.txt_Room");
			waitForSpinnerToDisappear(10);
			
			//Enter Service Request Code
			Utils.textBox("Housekeeping.ServiceRequests.txt_Code", srMap.get("SERVICE_REQUEST_CODE"));
			Utils.tabKey("Housekeeping.ServiceRequests.txt_Code");
			waitForSpinnerToDisappear(10);
			
			//Enter Service Request Priority
			Utils.textBox("Housekeeping.ServiceRequests.txt_Priority", srMap.get("SERVICE_REQUEST_PRIORITY"));
			Utils.tabKey("Housekeeping.ServiceRequests.txt_Priority");
			waitForSpinnerToDisappear(10);
			
			//Enter Remarks
			Utils.textBox("Housekeeping.ServiceRequests.txt_Remarks", srMap.get("REMARKS"));
			Utils.tabKey("Housekeeping.ServiceRequests.txt_Remarks");
			waitForSpinnerToDisappear(10);
			
			//Click on Save
			click("Housekeeping.ServiceRequests.btn_Save", 100, "clickable");
			waitForSpinnerToDisappear(10);
		}
		else
		{
			logger.log(LogStatus.FAIL, "***Issue observed in navigation to Service Requests screen***");
		}

	}catch (Exception e) {
		e.printStackTrace();
		Utils.takeScreenshot(driver, ScriptName);
		logger.log(LogStatus.FAIL, "Service Request not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
		throw (e);

	}
	}
	
	/*******************************************************************
	- Description: Edit Service Request (Inventory - Room Management - Service Requests)
	- Input:New Service Request Details
	- Output: Existing Service Request Edited
	- Author: Praneeth
	- Date: 08/03/19
	- Revision History:
	********************************************************************/
	public static void editServiceRequest(HashMap<String, String> srMap) throws Exception 
	{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		System.out.println("Map: " + srMap);
		try 
		{
			logger.log(LogStatus.INFO, "Editing the Service Request");
			
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu",100,"Clickable");
			
			// Navigating to Opera Cloud
			mouseHover("Configuration.OperaCloudMenu");
			jsClick("Configuration.OperaCloudMenu",100,"Clickable");
			waitForSpinnerToDisappear(30);
			
			// Navigating to Inventory menu
			mouseHover("Housekeeping.inventory_menu");
			click("Housekeeping.inventory_menu",100,"Clickable");

			// Navigating to Room Management
			mouseHover("Housekeeping.menu_RoomManagement");
			click("Housekeeping.menu_RoomManagement",100,"clickable");

			// Navigating to Service Requests
			click("Housekeeping.menu_ServiceRequests", 100, "clickable");
			waitForSpinnerToDisappear(10);

			//Check if Application is navigated to Service Requests Screen
			boolean blnPageExists = isExists("Housekeeping.ServiceRequests.hdr_ServiceRequests");
			if(blnPageExists)
			{
				logger.log(LogStatus.PASS, "Successfully navigated to Service Requests screen");

				//Enter Status
				Utils.textBox("Housekeeping.ServiceRequests.txt_Status", srMap.get("STATUS"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_Status");
				waitForSpinnerToDisappear(10);

				//Click on Search
				click("Housekeeping.ServiceRequests.btn_Search", 100, "clickable");
				waitForSpinnerToDisappear(10);

				//click on Actions
				click("Housekeeping.ServiceRequests.link_Actions", 100, "clickable");
				waitForSpinnerToDisappear(10);

				//click Edit
				click("Housekeeping.ServiceRequests.link_Edit", 100, "clickable");
				waitForSpinnerToDisappear(20);

				//Change Room
				Utils.textBox("Housekeeping.ServiceRequests.txt_Room", srMap.get("ROOM"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_Room");
				waitForSpinnerToDisappear(10);
				
				String updatedRemarks = srMap.get("REMARKS")+ " " + new Random().nextInt(1000);
				//Change Remarks
				Utils.textBox("Housekeeping.ServiceRequests.txt_Remarks", updatedRemarks);
				Utils.tabKey("Housekeeping.ServiceRequests.txt_Remarks");
				waitForSpinnerToDisappear(10);
				
				ExcelUtils.setDataByRow(OR.getConfig("Path_HousekeepingData"), "ServiceRequests", "Dataset_3", "REMARKS", updatedRemarks);

				//Click on Save
				click("Housekeeping.ServiceRequests.btn_Save", 100, "clickable");
				waitForSpinnerToDisappear(10);
			}
			else
			{
				logger.log(LogStatus.FAIL, "***Issue observed in navigation to Service Requests screen***");
			}

		}catch (Exception e) 
		{
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Failed to Edit Service Request" + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}
	
	/*******************************************************************
	- Description: Complete Service Request (Inventory - Room Management - Service Requests)
	- Input:New Service Request Details
	- Output: Existing Service Request Completed and Closed
	- Author: Praneeth
	- Date: 08/03/19
	- Revision History:
	********************************************************************/
	public static void completeAndCloseServiceRequest(HashMap<String, String> srMap) throws Exception 
	{
		String ScriptName = Utils.getMethodName();
		System.out.println("name: " + ScriptName);
		System.out.println("Map: " + srMap);
		try 
		{
			logger.log(LogStatus.INFO, "Completing and Closing the Service Request");
			
			// Navigating to Main Menu
			mouseHover("Configuration.mainMenu");
			jsClick("Configuration.mainMenu",100,"Clickable");

			// Navigating to Opera Cloud
			mouseHover("Configuration.OperaCloudMenu");
			jsClick("Configuration.OperaCloudMenu",100,"Clickable");
			waitForSpinnerToDisappear(30);
			
			// Navigating to Inventory menu
			mouseHover("Housekeeping.inventory_menu");
			click("Housekeeping.inventory_menu",100,"Clickable");

			// Navigating to Room Management
			mouseHover("Housekeeping.menu_RoomManagement");
			click("Housekeeping.menu_RoomManagement",100,"clickable");

			// Navigating to Service Requests
			click("Housekeeping.menu_ServiceRequests", 100, "clickable");
			waitForSpinnerToDisappear(10);

			//Check if Application is navigated to Service Requests Screen
			boolean blnPageExists = isExists("Housekeeping.ServiceRequests.hdr_ServiceRequests");
			if(blnPageExists)
			{
				logger.log(LogStatus.PASS, "Successfully navigated to Service Requests screen");

				//Enter Status
				Utils.textBox("Housekeeping.ServiceRequests.txt_Status", srMap.get("STATUS"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_Status");
				waitForSpinnerToDisappear(10);

				//Click on Search
				click("Housekeeping.ServiceRequests.btn_Search", 100, "clickable");
				waitForSpinnerToDisappear(10);

				//click on Actions
				click("Housekeeping.ServiceRequests.link_Actions", 100, "clickable");
				waitForSpinnerToDisappear(10);

				//click Complete
				click("Housekeeping.ServiceRequests.link_Complete", 100, "clickable");
				waitForSpinnerToDisappear(20);

				//Enter Contact Method
				Utils.textBox("Housekeeping.ServiceRequests.txt_ContactMethod", srMap.get("CONTACT_METHOD"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_ContactMethod");
				waitForSpinnerToDisappear(10);
				
				//Enter Contacted By
				Utils.textBox("Housekeeping.ServiceRequests.txt_ContactedBy", srMap.get("CONTACTED_BY"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_ContactedBy");
				waitForSpinnerToDisappear(10);
				
				//Enter Action Taken to Complete Service Request
				Utils.textBox("Housekeeping.ServiceRequests.txt_ActionTaken", srMap.get("COMPLETION_ACTION_TAKEN"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_ActionTaken");
				waitForSpinnerToDisappear(10);
				
				//Enter Date Completed
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				Date date = new Date();
				String dateToday = dateFormat.format(date);
				Calendar c = Calendar.getInstance();
				c.setTime(dateFormat.parse(dateToday));
				c.add(Calendar.DAY_OF_MONTH, 1);
				
				Utils.textBox("Housekeeping.ServiceRequests.txt_DateCompleted", dateFormat.format(c.getTime()));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_DateCompleted");
				waitForSpinnerToDisappear(10);
				

				//Click on Save
				click("Housekeeping.ServiceRequests.btn_Save", 100, "clickable");
				waitForSpinnerToDisappear(10);
				
				//Enter Status as pending follow up
				Utils.textBox("Housekeeping.ServiceRequests.txt_Status", "PENDING FOLLOW UP");
				Utils.tabKey("Housekeeping.ServiceRequests.txt_Status");
				waitForSpinnerToDisappear(10);

				//Click on Search
				click("Housekeeping.ServiceRequests.btn_Search", 100, "clickable");
				waitForSpinnerToDisappear(10);
				
				//click on Actions
				click("Housekeeping.ServiceRequests.link_Actions", 100, "clickable");
				waitForSpinnerToDisappear(10);

				//click Follow Up
				click("Housekeeping.ServiceRequests.link_FollowUp", 100, "clickable");
				waitForSpinnerToDisappear(20);
				
				//Enter Closed By
				Utils.textBox("Housekeeping.ServiceRequests.txt_ClosedBy", srMap.get("CLOSED_BY"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_ClosedBy");
				waitForSpinnerToDisappear(10);
				
				//Enter Action Taken to Close Service Request
				Utils.textBox("Housekeeping.ServiceRequests.txt_ClosureActionTaken", srMap.get("CLOSURE_ACTION_TAKEN"));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_ClosureActionTaken");
				waitForSpinnerToDisappear(10);
				
				//Enter Date Closed
				Utils.textBox("Housekeeping.ServiceRequests.txt_DateClosed", dateFormat.format(c.getTime()));
				Utils.tabKey("Housekeeping.ServiceRequests.txt_DateClosed");
				waitForSpinnerToDisappear(10);
				
				//Click on Save
				click("Housekeeping.ServiceRequests.btn_Save", 100, "clickable");
				waitForSpinnerToDisappear(10);
				
				//Enter Status CLOSED
				Utils.textBox("Housekeeping.ServiceRequests.txt_Status", "CLOSED");
				Utils.tabKey("Housekeeping.ServiceRequests.txt_Status");
				waitForSpinnerToDisappear(10);

				//Click on Search
				click("Housekeeping.ServiceRequests.btn_Search", 100, "clickable");
				waitForSpinnerToDisappear(10);
				
				List<WebElement> elements = Utils.elements("Housekeeping.ServiceRequests.row_ServiceRequests");
				boolean flag = false;
				for(int i=0;i< elements.size();i++)
				{
					WebElement e = elements.get(i).findElement(By.xpath("descendant::div[@data-ocid='" + i + "_OCC_NTLYT']"));
					if(e.getText().contains(srMap.get("REMARKS")))
					{
						logger.log(LogStatus.PASS, "Successfully verified the Service Request Status as Closed and also verified the corresponding Remarks");
						flag=true;
						break;
					}	
				}
				if(!flag)
				{
					logger.log(LogStatus.FAIL, "Failed to Verify the Remarks of the Service Request after closing");
				}
 			}
			else
			{
				logger.log(LogStatus.FAIL, "***Issue observed in navigation to Service Requests screen***");
			}

		}catch (Exception e) 
		{
			e.printStackTrace();
			Utils.takeScreenshot(driver, ScriptName);
			logger.log(LogStatus.FAIL, "Failed to Complete the Service Request" + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, ScriptName)));
			throw (e);

		}
	}
}
