package com.oracle.hgbu.opera.qaauto.ui.fof.testcases.hsk;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;
import com.oracle.hgbu.opera.qaauto.ui.fof.component.hsk.HousekeepingPage;
import com.oracle.hgbu.opera.qaauto.ui.config.component.ConfigPage;
import com.oracle.hgbu.opera.qaauto.ui.generic.component.exports.GenericPage;
import com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation.ReservationPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.relevantcodes.extentreports.LogStatus;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;



public class Housekeeping extends Utils{
	/*******************************************************************
	-  Description: Verify Even Room
	- Input: From Room No, To Room No and Even Room Radio Button
	- Output: Should Result in Even Room number
	- Author: jasatis
	- Date: 18/12/18
	- Revision History:1.0
	 ********************************************************************/	
	@Test(groups = {"BAT"},priority = 1)	
	public void verifyEvenRooms() throws Exception {
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		try {
			
			logger.log(LogStatus.INFO, "<b> Verify if user given room is an even room </b>");
			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Rooms", "Dataset_2");
			HousekeepingPage.evenRoom(configMap);
			Thread.sleep(3000);
			Utils.tearDown();
		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, methodName+" not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	/*******************************************************************
	-  Description: Verify Odd Room
	- Input: From Room No, To Room No and Odd Room Radio Button
	- Output: Should Result in Odd Room number
	- Author: jasatis
	- Date: 18/12/18
	- Revision History:1.0
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 2)
	public void verifyOddRooms() throws Exception {
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		try {

			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user given room is an odd room </b>");
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Rooms", "Dataset_1");
			HousekeepingPage.oddRoom(configMap);
			Thread.sleep(3000);
			Utils.tearDown();

		} catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, methodName+" not created :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}

	/*******************************************************************
	-  Description: Create/Verify Out of order room
	- Input: Room No, Room reasons,Business Date & Return status
	- Output: Changed Room status to Out of order
	- Author: Girish
	- Date: 07/12/18
	- Revision History:1.0
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 3)
	public void createOutOfOrderRoom() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);		
		try {
			   Utils.takeScreenshot(driver, methodName);
			   logger.log(LogStatus.INFO, "<b> Verify user able to create out of order room </b>");
			   HashMap<String, String> configRoomReasonMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RoomReasons", "Dataset_1");
			   HashMap<String, String> configRoomMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Rooms", "Dataset_5");
				// Navigating to Main Menu
				click("Housekeeping.inventory_menu",100,"clickable");	
				// Navigating to Room Management link
				click("Housekeeping.menu_RoomManagement",100,"clickable");	
				// Navigating to Out Of Order link
				click(Utils.element("Housekeeping.lnk_OutOfOrder"));
				//Verify out of order screen
				boolean blnOrderMainPage = isExists("Housekeeping.win_OutOfOrder");
				if(blnOrderMainPage){
					logger.log(LogStatus.PASS, "Navigate to Out of order screen");
					//Click on New button
					click("Housekeeping.link_New");
					waitForSpinnerToDisappear(20);
				    boolean blnSetRoomOrderPage = isExists("Housekeeping.win_SetRoomOutOfOrder");
					     if(blnSetRoomOrderPage){
						     //Enter text on From Room
					    	 String strRoom = configRoomMap.get("ROOM");
					    	 //Integer intRoom = Integer.valueOf(strRoom);
                             textBox("Housekeeping.txt_FromRoom",strRoom);
                             Utils.tabKey("Housekeeping.txt_FromRoom");                                   
                             waitForSpinnerToDisappear(50);                                              
					       //Enter text on Reasons
                            String strRoomReasons = configRoomReasonMap.get("CONDITIONCODE");
					    	textBox("Housekeeping.txt_RoomReasons", strRoomReasons);
					    	Utils.tabKey("Housekeeping.txt_RoomReasons");					    	 
					    	 waitForSpinnerToDisappear(50);  
	 					    //Click on Clean radio box for Return Status
					    	WebElement CleanButton = Utils.element("Housekeeping.rad_Clean");
							Actions actions = new Actions(driver);
							actions.moveToElement(CleanButton).click().build().perform();    	 
							 waitForSpinnerToDisappear(50);  
					    	//Click on Clean radio box for Return Status
					    	click("Housekeeping.btn_SetOutOfOrder",100,"clickable");
					    	//Navigate back to Room order main page
					    	WebdriverWait(100, "Housekeeping.win_OutOfOrder","presence");
					    	//Verify room order updated in table
					    	ValidateGridData("Housekeeping.tbl_OutOfOrderRoom", configRoomReasonMap.get("CONDITIONCODE"));
					    	logger.log(LogStatus.PASS,"Set Room Order Page does verified successfully"); 
							 }else{
					        	logger.log(LogStatus.FAIL, "*****Set Room Order Page does not verified successfully*****"); 
					        }					
					     
					        //Delete the Out of order room & verify status  
					        //Select More Menu 
					        driver.findElement(By.xpath("//td[contains(.,'"+configRoomMap.get("ROOM")+"')]/following-sibling::td//*[contains(@data-ocid,'LINK_ACTIONS')]")).click();
					        waitForSpinnerToDisappear(50); 
					        driver.findElement(By.xpath("//td[contains(.,'"+configRoomMap.get("ROOM")+"')]/following-sibling::td//*[contains(@data-ocid,'LINK_ACTIONS')]")).click();
							//Select delete link 
					        click("Housekeeping.link_Delete",100,"clickable");
					        WebdriverWait(100, "Housekeeping.win_UpdateRoomOutOfOrder","presence");
					        //Navigate to update room window
					        boolean blnUpdateRoomOrderPage = isExists("Housekeeping.win_UpdateRoomOutOfOrder"); 
					         if(blnUpdateRoomOrderPage){
					        	 click("Housekeeping.btn_DeleteOutOfOrder",100,"clickable");
					        	 logger.log(LogStatus.PASS, "Clicks on update out of order button successfully");
					         }else{
					        	 logger.log(LogStatus.FAIL, "***Issue observed in clicks on update out of order button***");
					         }															
					
				    }else{
				    	logger.log(LogStatus.FAIL, "*****Room out of order Page does not verified successfully*****"); 					
				}				
			} catch (Exception e) {
				e.printStackTrace();
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
			}

			//Logout from Application
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}

	/*******************************************************************
	-  Description: Create/Verify Out of service room
	- Input: Room No, Room reasons,Business Date & Return status
	- Output: Changed Room status to Out of service
	- Author: Girish
	- Date: 09/12/18
	- Revision History:1.0
	 ********************************************************************/
	
	@Test(groups = {"BAT"},priority = 4)
	public void createOutOfServiceRoom() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);		
		try {
			   Utils.takeScreenshot(driver, methodName);
			   logger.log(LogStatus.INFO, "<b> Verify user able to create out of service room </b>");
			   HashMap<String, String> configRoomReasonMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RoomReasons", "Dataset_1");
			   HashMap<String, String> configRoomMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Rooms", "Dataset_5");				
				// Navigating to Main Menu
				click("Housekeeping.inventory_menu",100,"clickable");	
				// Navigating to Room Management link
				click("Housekeeping.menu_RoomManagement",100,"clickable");	
				// Navigating to Out Of service link
				click(Utils.element("Housekeeping.lnk_OutOfService"));
				waitForSpinnerToDisappear(50);
				//Verify out of Service screen
				boolean blnServiceMainPage = isExists("Housekeeping.win_OutOfService");
				if(blnServiceMainPage){
					logger.log(LogStatus.PASS, "Navigate to Out of Service screen");
					//Click on New button
					click("Housekeeping.link_New",1000,"clickable");
					waitForSpinnerToDisappear(50);
				    boolean blnSetRoomServicePage = isExists("Housekeeping.win_SetRoomOutOfService");
					     if(blnSetRoomServicePage){
						     //Enter text on From Room
					    	 String strRoom = configRoomMap.get("ROOM");
					    	 //Integer intRoom = Integer.valueOf(strRoom);
                             textBox("Housekeeping.txt_FromRoom",strRoom);
                             Utils.tabKey("Housekeeping.txt_FromRoom");
                             waitForSpinnerToDisappear(50);
                                                                         
					       //Enter text on Reasons
                            String strRoomReasons = configRoomReasonMap.get("CONDITIONCODE");
					    	textBox("Housekeeping.txt_RoomReasons", strRoomReasons);
					    	Utils.tabKey("Housekeeping.txt_RoomReasons");
					    	waitForSpinnerToDisappear(50);
					    	
	 					    //Click on Clean radio box for Return Status
					    	WebElement CleanButton = Utils.element("Housekeeping.rad_Clean");
							Actions actions = new Actions(driver);
							actions.moveToElement(CleanButton).click().build().perform();    	 
							waitForSpinnerToDisappear(50);
					    	//Click on Clean radio box for Return Status
					    	click("Housekeeping.btn_SetOutOfService",100,"clickable");
					    	//Navigate back to Room Service main page
					    	WebdriverWait(10000, "Housekeeping.win_OutOfService","presence");
					    	//Verify room Service updated in table
					    	ValidateGridData("Housekeeping.tbl_OutOfServiceRoom", configRoomReasonMap.get("CONDITIONCODE"));
					    	logger.log(LogStatus.PASS,"Set Room Service Page does verified successfully"); 
							 }else{
					        	logger.log(LogStatus.FAIL, "*****Set Room Service Page does not verified successfully*****"); 
					        }										     
					        // Delete the Out of Service room & verify status  
					        //Select More Menu 
					     	driver.findElement(By.xpath("//td[contains(.,'"+configRoomMap.get("ROOM")+"')]/following-sibling::td//*[contains(@data-ocid,'LINK_ACTIONS')]")).click();
					        waitForSpinnerToDisappear(50);
					        driver.findElement(By.xpath("//td[contains(.,'"+configRoomMap.get("ROOM")+"')]/following-sibling::td//*[contains(@data-ocid,'LINK_ACTIONS')]")).click();
							//Select delete link 
					        click("Housekeeping.link_Delete",100,"clickable");
					        WebdriverWait(100, "Housekeeping.win_UpdateRoomOutOfService","presence");
					        //Navigate to update room window
					        boolean blnUpdateRoomServicePage = isExists("Housekeeping.win_UpdateRoomOutOfService"); 
					         if(blnUpdateRoomServicePage){
					        	 click("Housekeeping.btn_DeleteOutOfService",100,"clickable");
					        	 logger.log(LogStatus.PASS, "Clicks on update out of Service button successfully");
					         }else{
					        	 logger.log(LogStatus.FAIL, "***Issue observed in clicks on update out of Service button***");
					         }															
					
				    }else{
				    	logger.log(LogStatus.FAIL, "*****Room out of Service Page does not verified successfully*****"); 					
				}				
			} catch (Exception e) {
				e.printStackTrace();
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
			}

			//Logout from Application
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();				
		}

	/*******************************************************************
	-  Description: Create room descripencies as Sleep
	- Input: Room No, Return status as Occupy
	- Output: Changed Room status to Sleep
	- Author: Girish
	- Date: 10/12/18
	- Revision History:1.0
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 5)
	public void createRoomDiscrepancySleep() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);	
	
		try {
			  logger.log(LogStatus.INFO, "<b> Verify User is able to Create Room Discrepancy sleep </b>");
			  //logger = report.startTest(methodName, "Create Room Discrepancy sleep").assignCategory("sanity", "Cloud.Housekeeping");
			  Utils.takeScreenshot(driver, methodName);
			  //Navigating to Main Menu
			  click("Housekeeping.inventory_menu",100,"clickable");	
			  // Navigating to Room Management link
			  click("Housekeeping.menu_RoomManagement",100,"clickable");	

			 //Clicking on Housekeeping board link
			 click(Utils.element("Housekeeping.lnk_HousekeepingBorad"));
			 waitForSpinnerToDisappear(50);						
			//Verify the text of the Housekeeping Board screen
			boolean bHousekeepingExists = isExists("Housekeeping.housekeeping_board_text");
			if(bHousekeepingExists){	
				logger.log(LogStatus.PASS, "Navigate to Housekeeping board screen");
				//unselected occupied checkbox
				waitForSpinnerToDisappear(50);		
				WebElement vacant = Utils.element("Housekeeping.housekeeping_OccupiedCheckbox");
				Actions actions = new Actions(driver);
				actions.moveToElement(vacant).click().build().perform();
				logger.log(LogStatus.PASS, "Unselecting FO occupied checkbox");
	
				//clicking on search button
				waitForSpinnerToDisappear(50);		
				click("Housekeeping.btn_Search",100,"clickable");
											
				//Saving the room number of the selected room
				String roomNumber = Utils.getText("Housekeeping.housekeeping_room_number_text");
				System.out.println("roomNumber" + roomNumber);
				Utils.WebdriverWait(100, "Housekeeping.housekeeping_room_number_text", "visible");
				click("Housekeeping.housekeeping_room_number_text");
				System.out.println("Selected a Room from Search Results");
				logger.log(LogStatus.INFO, "Selected a Room from Search Results->  " + roomNumber);
				Utils.Wait(5000);
				
				//Clicking on update room status
				click("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus",100,"clickable");
							
				//validating FO status
				Utils.WebdriverWait(20, "Housekeeping.SetRoomStatus_FOStatus", "presence");
				Utils.mouseHover("Housekeeping.SetRoomStatus_FOStatus");
				String status = Utils.element("Housekeeping.SetRoomStatus_FOStatus").getText();
				System.out.println("FOstatus" + status);
				if(status.equalsIgnoreCase("vacant"))
				{
					logger.log(LogStatus.INFO, "FO status is ->  " + status);
					System.out.println("FO status is vacant");
				}
				else {
					logger.log(LogStatus.FAIL, "FO status->  " + status);
					System.out.println("FO status is not vacant");
				}
				
				//Clicking on Housekeeping Status Plus Icon
				Utils.WebdriverWait(60, "Housekeeping.SetRoomStatus_HouseKeeping_plus", "clickable");
				Utils.mouseHover("Housekeeping.SetRoomStatus_HouseKeeping_plus");
				click("Housekeeping.SetRoomStatus_HouseKeeping_plus");
				System.out.println("Clicked on Housekeeping Status Plus Icon");
				logger.log(LogStatus.INFO, "Clicked on Housekeeping Status Plus Icon");
				waitForSpinnerToDisappear(50);		
				
				//clicking on vacant status
				WebElement vacant1 = Utils.element("Housekeeping.setRoomStatus_OccupiedCheckbox");
				Utils.WebdriverWait(60, "Housekeeping.setRoomStatus_OccupiedCheckbox", "clickable");
				Utils.mouseHover("Housekeeping.setRoomStatus_OccupiedCheckbox");
				Actions actions6 = new Actions(driver);
				actions6.moveToElement(vacant1).click().build().perform();
    			logger.log(LogStatus.INFO, "Changed Housekeeping Status to Occupied");
    			waitForSpinnerToDisappear(50);		
	    		
				//Closing the button		
				Utils.WebdriverWait(60, "Housekeeping.UpdateRoomStatus.linkClose", "clickable");
				Utils.mouseHover("Housekeeping.UpdateRoomStatus.linkClose");
				click("Housekeeping.UpdateRoomStatus.linkClose");
				System.out.println("Clicked on Close Button");
				logger.log(LogStatus.INFO, "Clicked on Close Button on update status screen");		
				waitForSpinnerToDisappear(50);		
				//Navigating to Main Menu
				click("Housekeeping.inventory_menu",100,"clickable");
				// Navigating to Room Management link
				 click("Housekeeping.menu_RoomManagement",100,"clickable");	

				
				//Clicking on Room descripencies Board sub-menu
				click("Housekeeping.lnk_RoomDescripencies",100,"clickable");		
				
				// Validating Set Room Status page
				logger.log(LogStatus.PASS, "Navigate to Room Descripencies screen");
				//De-selecting all the already selected checkboxes
				Utils.Wait(5000);
				java.util.List<WebElement> elements1 = Utils.elements("Housekeeping.housekeeping_checkbox_count");
				
				for(int i =0 ; i < elements1.size();i++) {
					
					    if(elements1.get(i).isSelected()) {
					    	Actions actions5 = new Actions(driver);
							 actions5.moveToElement(elements1.get(i)).click().build().perform();
					    }
					}
				
				//Enter from room num
				Utils.Wait(1000);
				textBox("Housekeeping.RM_RoomDiscRoomNum", roomNumber);
				
				//select skip checkbox
				waitForSpinnerToDisappear(50);		
				WebElement ele = Utils.element("Housekeeping.RM_RoomDiscSleep");
				if(!ele.isSelected())
				{
					Actions actions8 = new Actions(driver);
					 actions8.moveToElement(ele).click().build().perform();
				}
	            
				//Clicking on search
				waitForSpinnerToDisappear(50);	
				click("Housekeeping.RM_SearchButton");
				System.out.println("Click on search button");
				logger.log(LogStatus.INFO, "Click on search button");			
				
				//Clicking existence of room number				
				/*Utils.WebdriverWait(60, "Housekeeping.RM_RoomDiscRoomNumValidate", "clickable");
				Utils.mouseHover("Housekeeping.RM_RoomDiscRoomNumValidate");*/				
				waitForSpinnerToDisappear(50);				
				String val = Utils.element("Housekeeping.RM_RoomDiscRoomNumValidate").getText();
				System.out.println("value" + val);
				if(val.equals(roomNumber)) {
					logger.log(LogStatus.PASS,roomNumber + " is marked as Sleep. Hence Passed" );
				}else {
					logger.log(LogStatus.FAIL, roomNumber + " is not marked as Sleep. Hence FAILED");
				}
			}else{
				logger.log(LogStatus.FAIL, "**Issue observed to navigating to Housekeeping board screen**");
		     }
		}catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Create room descripencies as sleep not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		//Logout from Application
//		LoginPage.Logout();
//		Thread.sleep(3000);
		Utils.tearDown();				
	}
	
	/*******************************************************************
	-  Description: Verify user is able to update room status to dirty
	- Input: 
	- Output: room status should be updated
	- Author: @author vnadipal
	- Date: 12/04/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 6)
	public void updateRoomStatusToDirty() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Verify User is able to Update Room Status To Dirty </b>");
			//logger = report.startTest(methodName, "Update Room Status To Dirty").assignCategory("sanity", "Cloud.Housekeeping");
			Utils.takeScreenshot(driver, methodName);
			  
			// Clicked on Inventory Menu
			jsClick("Housekeeping.inventory_menu", 100, "presence");

			// Clicked on Room Management Menu
			jsClick("Housekeeping.menu_RoomManagement", 100, "presence");

			// Clicked on Housekeeping Board Menu Item
			jsClick("Housekeeping.UpdateRoomStatus.menuItemHousekeepingBoard", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			if(element("Housekeeping.UpdateRoomStatus.chkboxInspected").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxInspected", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxPickup").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxPickup", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxDirty").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxDirty", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOO").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOO", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOS").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOS", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(!element("Housekeeping.UpdateRoomStatus.chkboxClean").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxClean", 100, "presence");
			waitForSpinnerToDisappear(5);

			//clicking on search button
			click("Housekeeping.UpdateRoomStatus.btnSearch");
			
			waitForSpinnerToDisappear(5);
			int dirtyrows = elements("Housekeeping.UpdateRoomStatus.roomRowsCount").size();
			
			if(dirtyrows > 0) {	
				//Saving the room number of the selected room
				String roomNumber = getText("Housekeeping.UpdateRoomStatus.validationRoomText");
				System.out.println("roomNumber: " + roomNumber);
				
				click("Housekeeping.UpdateRoomStatus.chkboxSelectRoom");
			
				//Click on update room status
				if(isExists("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus"))
					jsClick("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus");
				
				waitForSpinnerToDisappear(30);
				//Clicking on dirty check box to change the status of the room to dirty
				WebElement test3 = element("Housekeeping.UpdateRoomStatus.radioDirty");
				Actions actions3 = new Actions(driver);
				actions3.moveToElement(test3).click().build().perform();
				//jsClick("Housekeeping.UpdateRoomStatus.radioDirty", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				click("Housekeeping.UpdateRoomStatus.linkClose");
			
				waitForSpinnerToDisappear(10);
				textBox("Housekeeping.UpdateRoomStatus.txtFromRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtFromRoom");
				
				waitForSpinnerToDisappear(5);
				textBox("Housekeeping.UpdateRoomStatus.txtToRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtToRoom");
				
				if(element("Housekeeping.UpdateRoomStatus.chkboxClean").isSelected())
					jsClick("Housekeeping.UpdateRoomStatus.chkboxClean", 100, "presence");
				waitForSpinnerToDisappear(5);
				
				//clicking on search button
				jsClick("Housekeeping.UpdateRoomStatus.btnSearch", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				String strStatus = getText("Housekeeping.UpdateRoomStatus.validationRoomStatusText");
				System.out.println("text: " + strStatus);
				if(strStatus.equalsIgnoreCase("Dirty")) {
					logger.log(LogStatus.PASS, "Room " + roomNumber +" Status is successfully changed to Dirty");
				}
				else {
					logger.log(LogStatus.FAIL, "Failed to change Room " + roomNumber +" Status to Dirty. Current Status is : "+strStatus);
				}
			}
			else {
				logger.log(LogStatus.WARNING, "There are no rooms available with clean status, hence exiting the script");
			}
		
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to update room status to clean
	- Input: 
	- Output: room status should be updated
	- Author: @author vnadipal
	- Date: 12/05/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 7)
	public void updateRoomStatusToClean() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Verify User is able to Update Room Status To Clean </b>");
			//logger = report.startTest(methodName, "Update Room Status To Clean").assignCategory("sanity", "Cloud.Housekeeping");
			Utils.takeScreenshot(driver, methodName);
			
			// Clicked on Inventory Menu
			jsClick("Housekeeping.inventory_menu", 100, "presence");

			// Clicked on Room Management Menu
			jsClick("Housekeeping.menu_RoomManagement", 100, "presence");

			// Clicked on Housekeeping Board Menu Item
			jsClick("Housekeeping.UpdateRoomStatus.menuItemHousekeepingBoard", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			if(element("Housekeeping.UpdateRoomStatus.chkboxInspected").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxInspected", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxPickup").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxPickup", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxClean").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxClean", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOO").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOO", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOS").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOS", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(!element("Housekeeping.UpdateRoomStatus.chkboxDirty").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxDirty", 100, "presence");
			waitForSpinnerToDisappear(5);

			//clicking on search button
			click("Housekeeping.UpdateRoomStatus.btnSearch");
			
			waitForSpinnerToDisappear(5);
			int dirtyrows = elements("Housekeeping.UpdateRoomStatus.roomRowsCount").size();
			
			if(dirtyrows > 0) {	
				//Saving the room number of the selected room
				String roomNumber = getText("Housekeeping.UpdateRoomStatus.validationRoomText");
				System.out.println("roomNumber: " + roomNumber);
				
				click("Housekeeping.UpdateRoomStatus.chkboxSelectRoom");
			
				//Click on update room status
				if(isExists("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus"))
					jsClick("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus");
				
				waitForSpinnerToDisappear(30);
				//Clicking on dirty check box to change the status of the room to Clean
				WebElement test3 = element("Housekeeping.UpdateRoomStatus.radioClean");
				Actions actions3 = new Actions(driver);
				actions3.moveToElement(test3).click().build().perform();
				//jsClick("Housekeeping.UpdateRoomStatus.radioDirty", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				click("Housekeeping.UpdateRoomStatus.linkClose");
			
				waitForSpinnerToDisappear(10);
				textBox("Housekeeping.UpdateRoomStatus.txtFromRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtFromRoom");
				
				waitForSpinnerToDisappear(5);
				textBox("Housekeeping.UpdateRoomStatus.txtToRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtToRoom");
				
				if(element("Housekeeping.UpdateRoomStatus.chkboxDirty").isSelected())
					jsClick("Housekeeping.UpdateRoomStatus.chkboxDirty", 100, "presence");
				waitForSpinnerToDisappear(5);
				
				//clicking on search button
				jsClick("Housekeeping.UpdateRoomStatus.btnSearch", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				String strStatus = getText("Housekeeping.UpdateRoomStatus.validationRoomStatusText");
				System.out.println("text: " + strStatus);
				if(strStatus.equalsIgnoreCase("clean")) {
					logger.log(LogStatus.PASS, "Room " + roomNumber +" Status is successfully changed to Clean");
				}
				else {
					logger.log(LogStatus.FAIL, "Failed to change Room " + roomNumber +" Status to Clean. Current Status is : "+strStatus);
				}
			}
			else {
				logger.log(LogStatus.WARNING, "There are no rooms available with dirty status, hence exiting the script");
			}
		
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to update room status to inspected
	- Input: 
	- Output: room status should be updated
	- Author: @author vnadipal
	- Date: 12/05/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 8)
	public void updateRoomStatusToInspected() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Verify User is able to Update Room Status To Inspected </b>");
			//logger = report.startTest(methodName, "Update Room Status To Inspected").assignCategory("sanity", "Cloud.Housekeeping");
			Utils.takeScreenshot(driver, methodName);
			
			// Clicked on Inventory Menu
			jsClick("Housekeeping.inventory_menu", 100, "presence");

			// Clicked on Room Management Menu
			jsClick("Housekeeping.menu_RoomManagement", 100, "presence");

			// Clicked on Housekeeping Board Menu Item
			jsClick("Housekeeping.UpdateRoomStatus.menuItemHousekeepingBoard", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			if(element("Housekeeping.UpdateRoomStatus.chkboxInspected").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxInspected", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxPickup").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxPickup", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxDirty").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxDirty", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOO").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOO", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOS").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOS", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(!element("Housekeeping.UpdateRoomStatus.chkboxClean").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxClean", 100, "presence");
			waitForSpinnerToDisappear(5);

			//clicking on search button
			click("Housekeeping.UpdateRoomStatus.btnSearch");
			
			Thread.sleep(3000);
			int dirtyrows = elements("Housekeeping.UpdateRoomStatus.roomRowsCount").size();
			
			if(dirtyrows > 0) {	
				//Saving the room number of the selected room
				String roomNumber = getText("Housekeeping.UpdateRoomStatus.validationRoomText");
				System.out.println("roomNumber: " + roomNumber);
				
				click("Housekeeping.UpdateRoomStatus.chkboxSelectRoom");
			
				//Click on update room status
				if(isExists("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus"))
					jsClick("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus");
			
				waitForSpinnerToDisappear(30);
				//Clicking on dirty check box to change the status of the room to Inspected
				WebElement test3 = element("Housekeeping.UpdateRoomStatus.radioInspected");
				Actions actions3 = new Actions(driver);
				actions3.moveToElement(test3).click().build().perform();
				//jsClick("Housekeeping.UpdateRoomStatus.radioDirty", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				click("Housekeeping.UpdateRoomStatus.linkClose");
			
				waitForSpinnerToDisappear(10);
				textBox("Housekeeping.UpdateRoomStatus.txtFromRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtFromRoom");
				
				waitForSpinnerToDisappear(5);
				textBox("Housekeeping.UpdateRoomStatus.txtToRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtToRoom");
				
				if(element("Housekeeping.UpdateRoomStatus.chkboxClean").isSelected())
					jsClick("Housekeeping.UpdateRoomStatus.chkboxClean", 100, "presence");
				waitForSpinnerToDisappear(5);
				
				//clicking on search button
				jsClick("Housekeeping.UpdateRoomStatus.btnSearch", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				String strStatus = getText("Housekeeping.UpdateRoomStatus.validationRoomStatusText");
				System.out.println("text: " + strStatus);
				if(strStatus.equalsIgnoreCase("Inspected")) {
					logger.log(LogStatus.PASS, "Room " + roomNumber +" Status is successfully changed to Inspected");
				}
				else {
					logger.log(LogStatus.FAIL, "Failed to change Room " + roomNumber +" Status to Inspected. Current Status is : "+strStatus);
				}
			}
			else {
				logger.log(LogStatus.WARNING, "There are no rooms available with clean status, hence exiting the script");
			}
		
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}
	}
	
	/*******************************************************************
	-  Description: Verify user is able to update room status to pickup
	- Input: 
	- Output: room status should be updated
	- Author: @author vnadipal
	- Date: 12/11/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 9)
	public void updateRoomStatusToPickup() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);	
	
		try {
			logger.log(LogStatus.INFO, "<b> Verify User is able to Update Room Status To Pickup </b>");
			//logger = report.startTest(methodName, "Update Room Status To Pickup").assignCategory("sanity", "Cloud.Housekeeping");
			Utils.takeScreenshot(driver, methodName);
			
			// Clicked on Inventory Menu
			jsClick("Housekeeping.inventory_menu", 100, "presence");

			// Clicked on Room Management Menu
			jsClick("Housekeeping.menu_RoomManagement", 100, "presence");

			// Clicked on Housekeeping Board Menu Item
			jsClick("Housekeeping.UpdateRoomStatus.menuItemHousekeepingBoard", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			if(element("Housekeeping.UpdateRoomStatus.chkboxInspected").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxInspected", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxPickup").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxPickup", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxClean").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxClean", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOO").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOO", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(element("Housekeeping.UpdateRoomStatus.chkboxOOS").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxOOS", 100, "presence");
			waitForSpinnerToDisappear(5);
			
			if(!element("Housekeeping.UpdateRoomStatus.chkboxDirty").isSelected())
				jsClick("Housekeeping.UpdateRoomStatus.chkboxDirty", 100, "presence");
			waitForSpinnerToDisappear(5);

			//clicking on search button
			click("Housekeeping.UpdateRoomStatus.btnSearch");
			
			waitForSpinnerToDisappear(5);
			int dirtyrows = elements("Housekeeping.UpdateRoomStatus.roomRowsCount").size();
			
			if(dirtyrows > 0) {	
				//Saving the room number of the selected room
				String roomNumber = getText("Housekeeping.UpdateRoomStatus.validationRoomText");
				System.out.println("roomNumber: " + roomNumber);
				
				click("Housekeeping.UpdateRoomStatus.chkboxSelectRoom");
			
				//Click on update room status
				if(isExists("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus"))
					jsClick("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus");
				
				waitForSpinnerToDisappear(30);
				//Clicking on dirty check box to change the status of the room to pickup
				WebElement test3 = element("Housekeeping.UpdateRoomStatus.radioPickup");
				Actions actions3 = new Actions(driver);
				actions3.moveToElement(test3).click().build().perform();
				//jsClick("Housekeeping.UpdateRoomStatus.radioDirty", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				click("Housekeeping.UpdateRoomStatus.linkClose");
			
				waitForSpinnerToDisappear(10);
				textBox("Housekeeping.UpdateRoomStatus.txtFromRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtFromRoom");
				
				waitForSpinnerToDisappear(5);
				textBox("Housekeeping.UpdateRoomStatus.txtToRoom", roomNumber);
				tabKey("Housekeeping.UpdateRoomStatus.txtToRoom");
				
				if(element("Housekeeping.UpdateRoomStatus.chkboxDirty").isSelected())
					jsClick("Housekeeping.UpdateRoomStatus.chkboxDirty", 100, "presence");
				waitForSpinnerToDisappear(5);
				
				//clicking on search button
				jsClick("Housekeeping.UpdateRoomStatus.btnSearch", 100, "presence");
				
				waitForSpinnerToDisappear(10);
				String strStatus = getText("Housekeeping.UpdateRoomStatus.validationRoomStatusText");
				System.out.println("text: " + strStatus);
				if(strStatus.equalsIgnoreCase("Pickup")) {
					logger.log(LogStatus.PASS, "Room " + roomNumber +" Status is successfully changed to Pickup");
				}
				else {
					logger.log(LogStatus.FAIL, "Failed to change Room " + roomNumber +" Status to Pickup. Current Status is : "+strStatus);
				}
			}
			else {
				logger.log(LogStatus.WARNING, "There are no rooms available with dirty status, hence exiting the script");
			}
		
			Utils.tearDown();
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}
	}

	/*******************************************************************
	-  Description: Verify user is able to create room maintenance
	- Input: 
	- Output: room status should be updated
	- Author: @author Girish
	- Date: 12/11/18
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(priority = 10,groups = {"BAT"})
	public void createRoomMaintenance() throws Exception {

		//This test covers both the functionality of creating a unresolved task and resolving the same
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);		
		try {
			   Utils.takeScreenshot(driver, methodName);
			   logger.log(LogStatus.INFO, "<b> Verify User is able to create Room Maintenance </b>");
			   //logger = report.startTest(methodName, "Verify User is able to create Room Maintenance").assignCategory("acceptance", "Cloud.Housekeeping");			
			   //Login Application
			   HashMap<String, String> housekeepingMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "RoomMaintenance", "Dataset_1");
			   HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "RoomMaintenance", "Dataset_1");				
				// Navigating to Main Menu
				click("Housekeeping.inventory_menu",100,"clickable");	
				// Navigating to Room Management link
				click("Housekeeping.menu_RoomManagement",100,"clickable");	
				Utils.waitForSpinnerToDisappear(10);
				// Navigating to Room Maintenance link
				click("Housekeeping.menu_RoomMaintenance",100,"clickable");	
				Utils.WebdriverWait(100, "Housekeeping.RoomMaintenanceTitle", "presence");			
				System.out.println("Landed in Room Maintenance Page" );
				logger.log(LogStatus.PASS, "Landed in Room Maintenance Page: ");
				//Click on New link 
				click("Housekeeping.NewButton",100,"clickable");
				Utils.waitForSpinnerToDisappear(10);
				Utils.WebdriverWait(100, "Housekeeping.NewRoomMaintenance_title", "presence");			
				System.out.println("Landed in Room Maintenance Page" );
				logger.log(LogStatus.PASS, "Landed in Room Maintenance Page: ");
				//select a room 
				click("Housekeeping.RoomSearchIcon",100,"clickable");
				Utils.waitForSpinnerToDisappear(10);
				click("Housekeeping.SelectRoomRow",100,"clickable");
				Utils.waitForSpinnerToDisappear(10);
				String roomNo = (Utils.getText("Housekeeping.RoomNumberSelected", 100, "presence")).trim();
				System.out.println("Selected Room Number:" + roomNo );
				logger.log(LogStatus.PASS, "Selected Room Number:" + roomNo);
				click("Housekeeping.RoomSelectButton",100,"clickable");
				Utils.waitForSpinnerToDisappear(10);
				textBox("Housekeeping.Maintenance_Reason", configMap.get("MAINTENANCECODE"), 400, "presence");
				Utils.tabKey("Housekeeping.Maintenance_Reason");
				Utils.waitForSpinnerToDisappear(10);
				click("Housekeeping.NewRoomMaintenance_Save",100,"clickable");
				Utils.Wait(4000);
				Utils.waitForSpinnerToDisappear(10);
				textBox("Housekeeping.RoomMaintenanceSearch_Room", roomNo , 400, "presence");
				Utils.tabKey("Housekeeping.RoomMaintenanceSearch_Room");
				Utils.waitForSpinnerToDisappear(10);
				textBox("Housekeeping.RoomMaintenanceSearch_Reason", configMap.get("MAINTENANCECODE") , 400, "presence");
				Utils.tabKey("Housekeeping.RoomMaintenanceSearch_Reason");
				Utils.waitForSpinnerToDisappear(10);
				Utils.selectBy("Housekeeping.RoomMaintenanceSearch_Show", "text", housekeepingMap.get("STATUS"));
				Utils.waitForSpinnerToDisappear(10);
				click("Housekeeping.RoomMaintenanceSearch_Search",100,"clickable");
				Utils.waitForSpinnerToDisappear(10);
				click("Housekeeping.RoomMaintenanceSearch_ExpandResults",100,"clickable");
				Utils.Wait(4000);
				Utils.waitForSpinnerToDisappear(10);				
				if (!isSelected("Housekeeping.RoomMaintenanceSearch_UnresolvedCheckbox", "Unresolved CheckBox")) {	
					System.out.println("New Room maintenance task created");
					logger.log(LogStatus.PASS, "New Room maintenance task created");
	                click("Housekeeping.RoomMaintenanceSearch_UnresolvedCheckbox",100,"clickable");
	                Utils.waitForSpinnerToDisappear(10);
	                Utils.Wait(4000);
	                Utils.selectBy("Housekeeping.RoomMaintenanceSearch_Show", "text", "Resolved");
					Utils.waitForSpinnerToDisappear(10);
					click("Housekeeping.RoomMaintenanceSearch_Search",100,"clickable");
					Utils.waitForSpinnerToDisappear(10);
	                if(isSelected("Housekeeping.RoomMaintenanceSearch_UnresolvedCheckbox", "Unresolved CheckBox")) {
	                	logger.log(LogStatus.PASS, "Room maintenance task is resolved");
	                }else {
	                	logger.log(LogStatus.FAIL, "Room maintenance task is NOT resolved");
	                }
	            } else {
	            	System.out.println("New Room maintenance task NOT created properly");
					logger.log(LogStatus.FAIL, "New Room maintenance task NOT created properly");	                
	            }

				
			} catch (Exception e) {
				e.printStackTrace();
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, "New Room maintenance task NOT created :: Failed " + e.getLocalizedMessage());
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
			}

			//Logout from Application
//			LoginPage.Logout();
//			Thread.sleep(3000);
			Utils.tearDown();
		}

	/*******************************************************************
	- Description: Create Auto Assign Room for reservation
	- Input: Reservation,Rooms
	- Output: Room auto assigned to reservation
	- Author: Girish
	- Date: 22/01/18
	- Revision History:1.0
	 ********************************************************************/
	
	@Test(groups = {"Sanity"},priority = 11)
	public void autoAssignRoom() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);		
		try {
			    Utils.takeScreenshot(driver, methodName);
			    logger.log(LogStatus.INFO, "<b> Verify user able to auto assign room from Room Assignment Screen </b>");		
			    //Precondition :- Create reservation
				HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
				String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
				ExcelUtils.setDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
				Thread.sleep(3000);
				
				//Navigating to Front desk Menu
				click("Housekeeping.menu_Frontdesk",100,"clickable");
				waitForSpinnerToDisappear(50);
				// Navigating to Front desk workspace link
				click("Housekeeping.menu_FrontdeskWorkspace",100,"clickable");	
				waitForSpinnerToDisappear(50);
				// Navigating to Room Assignment link
				click("Housekeeping.menu_RoomAssignment",100,"clickable");
				waitForSpinnerToDisappear(50);
								
				//Navigate to Room Assignment screen
				if(isExists("Housekeeping.RoomAssignment.txtHeader")){
					logger.log(LogStatus.PASS, "Navigate to Room Assignment screen");	
					//Click on Modify Criteria  link
					click("Housekeeping.RoomAssignment.linkModifySearchCriteria",100,"clickable");
					waitForSpinnerToDisappear(50);
					//Enter conf no 
					HashMap<String, String> resvNoMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
					textBox("Housekeeping.RoomAssignment.txtConfNo", resvNoMap.get("ConfirmationNum"));
				    Utils.tabKey("Housekeeping.RoomAssignment.txtConfNo");
				    waitForSpinnerToDisappear(50);
				    //Click on search button
					click("Housekeeping.RoomAssignment.btnSearch",100,"clickable");
					waitForSpinnerToDisappear(50);
					//Click on reservation check box
					click("Housekeeping.RoomAssignment.chkResvID",100,"clickable");
					waitForSpinnerToDisappear(50);
					
					//Click on Auto Room button
					click("Housekeeping.RoomAssignment.btnAutoAssign",100,"clickable");
					waitForSpinnerToDisappear(50);
					
					//Click Clean check box
					click("Housekeeping.RoomAssignment.chkboxCleanAuto",100,"clickable");
					waitForSpinnerToDisappear(10);
					//Click Dirty check box
					click("Housekeeping.RoomAssignment.chkboxDirtyAuto",100,"clickable");
					waitForSpinnerToDisappear(10);
					//Click Pickup check box
					click("Housekeeping.RoomAssignment.chkboxPickupAuto",100,"clickable");
					waitForSpinnerToDisappear(10);				
					
					//Click on Auto Room button
					click("Housekeeping.RoomAssignment.btnAssign",100,"clickable");
					waitForSpinnerToDisappear(50);
					
					//Verify the Room Assignment Status screen
					if(isExists("Housekeeping.RoomAssignment.txtRoomAssignmentStatusHeader")){
						logger.log(LogStatus.PASS, "Navigate to Room Assignment Status screen");	
						
						//Verify the status
						System.out.println(element("Housekeeping.RoomAssignment.txtRoomAssignmentStatus").getText());
						if(element("Housekeeping.RoomAssignment.txtRoomAssignmentStatus").getText().contains("Successfully processed")){
							logger.log(LogStatus.PASS, "Room is successfully process with auto assignment");
						}else{
							logger.log(LogStatus.FAIL, "***Issue observed in processing room auto assignment***");
						}
						//Click on Close button
						click("Housekeeping.RoomAssignment.btnClose",100,"clickable");
						waitForSpinnerToDisappear(50);
						}else{
							logger.log(LogStatus.FAIL, "***Issue observed to navigate Room Assignment Status screen***");
						}
				  }else{
								logger.log(LogStatus.FAIL, "***Failed to navigate to Room Assignment screen***");	
						}
				   //Clicking on Modify Search link
				    boolean blnExists = isExists("Reservation.search_confirmNum");
				   if(!blnExists){
					click("Housekeeping.RoomAssignment.linkModifySearchCriteria",100,"clickable");
					waitForSpinnerToDisappear(50);
				     }
					//Search the room assignment in manage reservation screen
					HashMap<String, String> resvSearchMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
					ReservationPage.reservationSearch(resvSearchMap.get("ConfirmationNum"));
					//Verify the room no column
					List<WebElement> grdHeader = elements("Reservation.manageResv_HdrName");
					List<WebElement> grdValue = elements("Reservation.manageResv_HdrValue");
					int grdSize = elements("Reservation.manageResv_HdrName").size();
					
					System.out.println(grdValue.size());
					for(int i=0;i<grdSize;i++)
						{				
							if(grdHeader.get(i).getText().equalsIgnoreCase("Room")){
								String strRoomNo = grdValue.get(i).getText();
								if(strRoomNo!=null){
									logger.log(LogStatus.PASS, "Rservation is updated with auto assigned room "+strRoomNo+" successfully");
								}else{
									logger.log(LogStatus.FAIL, "***Issue observed to assigned auto room to reservation***");
								}																
							}		
						  
						}
										
			}catch (Exception e) {
	    				e.printStackTrace();
	    				Utils.takeScreenshot(driver, methodName);
	    				logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
	    				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
	    				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
	    			}

    			//Logout from Application
    			Utils.tearDown();				
    		}
		
	/*******************************************************************
	- Description: Create Manual Assign Room for reservation
	- Input: Reservation,Rooms
	- Output: Room auto assigned to reservation
	- Author: Girish
	- Date: 24/01/18
	- Revision History:1.0
	 ********************************************************************/
	
	@Test(groups = {"Sanity"},priority = 12)
	public void manualAssignRoom() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);		
		try {
			    Utils.takeScreenshot(driver, methodName);
			    logger.log(LogStatus.INFO, "<b> Verify user able to Maunal assign room from Room Assignment Screen </b>");		
			    //Precondition :- Create reservation
				HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
				String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
				ExcelUtils.setDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
				Thread.sleep(3000);
				
				//Navigating to Front desk Menu
				click("Housekeeping.menu_Frontdesk",100,"clickable");
				waitForSpinnerToDisappear(50);
				// Navigating to Front desk workspace link
				click("Housekeeping.menu_FrontdeskWorkspace",100,"clickable");	
				waitForSpinnerToDisappear(50);
				// Navigating to Room Assignment link
				click("Housekeeping.menu_RoomAssignment",100,"clickable");
				waitForSpinnerToDisappear(50);
								
				//Navigate to Room Assignment screen
				if(isExists("Housekeeping.RoomAssignment.txtHeader")){
					logger.log(LogStatus.PASS, "Navigate to Room Assignment screen");	
					//Click on Modify Criteria  link
					click("Housekeeping.RoomAssignment.linkModifySearchCriteria",100,"clickable");
					waitForSpinnerToDisappear(50);
					//Enter conf no 
					HashMap<String, String> resvNoMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
					textBox("Housekeeping.RoomAssignment.txtConfNo", resvNoMap.get("ConfirmationNum"));
				    Utils.tabKey("Housekeeping.RoomAssignment.txtConfNo");
				    waitForSpinnerToDisappear(50);
				    //Click on search button
					click("Housekeeping.RoomAssignment.btnSearch",100,"clickable");
					waitForSpinnerToDisappear(50);
					//Click on reservation check box
					click("Housekeeping.RoomAssignment.chkResvID",100,"clickable");
					waitForSpinnerToDisappear(50);
					
					//Click on Auto Room button
					click("Housekeeping.RoomAssignment.btnManualAssign",100,"clickable");
					waitForSpinnerToDisappear(50);
									
					//Verify the Manual Room Assignment screen
					logger.log(LogStatus.PASS, "Navigate to Room Details screen");	
					
					//Click Clean check box
					click("Housekeeping.RoomAssignment.chkboxCleanManual",100,"clickable");
					waitForSpinnerToDisappear(10);
					//Click Dirty check box
					click("Housekeeping.RoomAssignment.chkboxDirtyManual",100,"clickable");
					waitForSpinnerToDisappear(10);
					//Click Pickup check box
					click("Housekeeping.RoomAssignment.chkboxPickupManual",100,"clickable");
					waitForSpinnerToDisappear(10);				
					
					//Click on search button
					click("Housekeeping.RoomAssignment.btnSearch",100,"clickable");
					waitForSpinnerToDisappear(50);
					
					//Select the Room 
					click("Housekeeping.RoomAssignment.linkFirstSelectRoom",100,"clickable");
					waitForSpinnerToDisappear(50);
					
					//Click on Assign Room button 
					if(isExists("Housekeeping.RoomAssignment.btnAssignRoom")){
					click("Housekeeping.RoomAssignment.btnAssignRoom",100,"clickable");
					waitForSpinnerToDisappear(50);
					}
					//Verify additional Details pop up & click on Accept Room Selection button
					if(isExists("Housekeeping.RoomAssignment.btnAcceptRoomSelection")){
					         click("Housekeeping.RoomAssignment.btnAcceptRoomSelection",100,"clickable");
					         waitForSpinnerToDisappear(50);
					   }
											
						//Verify pop up of final confirmation
						if(isExists("Housekeeping.RoomAssignment.txtFinalConfirmation")){
							logger.log(LogStatus.PASS, "System successfully verified the confirmation message "+element("Housekeeping.RoomAssignment.txtFinalConfirmation").getText());
							//Click on final confirmation OK button
							click("Housekeeping.RoomAssignment.btnOkFinalConfirmation",100,"clickable");
							waitForSpinnerToDisappear(50);
						}
												
					  }else{
								logger.log(LogStatus.FAIL, "***Failed to navigate to Room Details screen***");	
						}
					//Clicking on Modify Search link
					boolean blnExists = isExists("Reservation.search_confirmNum");
					if(!blnExists){
						click("Housekeeping.RoomAssignment.linkModifySearchCriteria",100,"clickable");
						waitForSpinnerToDisappear(50);
					   }
					//Search the room assignment in manage reservation screen
				     HashMap<String, String> resvSearchMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
					 ReservationPage.reservationSearch(resvSearchMap.get("ConfirmationNum"));
					//Verify the room no column
					List<WebElement> grdHeader = elements("Reservation.manageResv_HdrName");
					List<WebElement> grdValue = elements("Reservation.manageResv_HdrValue");
					int grdSize = elements("Reservation.manageResv_HdrName").size();
					
					System.out.println(grdValue.size());
					for(int i=0;i<grdSize;i++)
						{				
							if(grdHeader.get(i).getText().equalsIgnoreCase("Room")){
								String strRoomNo = grdValue.get(i).getText();
								if(strRoomNo!=null){
									logger.log(LogStatus.PASS, "Rservation is updated with auto assigned room "+strRoomNo+" successfully");
								}else{
									logger.log(LogStatus.FAIL, "***Issue observed to assigned auto room to reservation***");
								}																
							}		
						  
						}
										
			}catch (Exception e) {
	    				e.printStackTrace();
	    				Utils.takeScreenshot(driver, methodName);
	    				logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
	    				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
	    				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
	    			}

    			//Logout from Application
    			Utils.tearDown();				
    		}
	
	
	/*******************************************************************
	- Description: Create Manual Assign Room for reservation
	- Input: Reservation,Rooms
	- Output: Room auto assigned to reservation
	- Author: Girish
	- Date: 24/01/18
	- Revision History:1.0
	 ********************************************************************/
	
	@Test(groups = {"Sanity"},priority = 13)
	public void roomAssignmentFromResvSearch() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);		
		try {
			    Utils.takeScreenshot(driver, methodName);
			    logger.log(LogStatus.INFO, "<b> Verify user able to assign room from Reservation Search screen </b>");		
			    //Precondition :- Create reservation
				HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
				String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
				ExcelUtils.setDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
				Thread.sleep(3000);
										
				//Search the room assignment in manage reservation screen
				HashMap<String, String> resvSearchMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
				ReservationPage.reservationSearch(resvSearchMap.get("ConfirmationNum"));
				//Verify the room no column
				click("Reservation.manageResv_linkAssignRoom",100,"clickable");
					  
				//Verify the Manual Room Assignment screen
				logger.log(LogStatus.PASS, "Navigate to Room Details screen");	
				
				//Click Clean check box
				click("Housekeeping.RoomAssignment.chkboxCleanManual",100,"clickable");
				waitForSpinnerToDisappear(10);
				//Click Dirty check box
				click("Housekeeping.RoomAssignment.chkboxDirtyManual",100,"clickable");
				waitForSpinnerToDisappear(10);
				//Click Pickup check box
				click("Housekeeping.RoomAssignment.chkboxPickupManual",100,"clickable");
				waitForSpinnerToDisappear(10);				
				
				//Click on search button
				click("Housekeeping.RoomAssignment.btnSearch",100,"clickable");
				waitForSpinnerToDisappear(50);
				
				//Select the Room 
				click("Housekeeping.RoomAssignment.linkFirstSelectRoom",100,"clickable");
				waitForSpinnerToDisappear(50);
				
				//Click on Assign Room button 
				if(isExists("Housekeeping.RoomAssignment.btnAssignRoom")){
				click("Housekeeping.RoomAssignment.btnAssignRoom",100,"clickable");
				waitForSpinnerToDisappear(50);
				}
				//Verify additional Details pop up & click on Accept Room Selection button
				if(isExists("Housekeeping.RoomAssignment.btnAcceptRoomSelection")){
				         click("Housekeeping.RoomAssignment.btnAcceptRoomSelection",100,"clickable");
				         waitForSpinnerToDisappear(50);
				   }
										
					//Verify pop up of final confirmation
					if(isExists("Housekeeping.RoomAssignment.txtFinalConfirmation")){
						logger.log(LogStatus.PASS, "System successfully verified the confirmation message "+element("Housekeeping.RoomAssignment.txtFinalConfirmation").getText());
						//Click on final confirmation OK button
						click("Housekeeping.RoomAssignment.btnOkFinalConfirmation",100,"clickable");
						waitForSpinnerToDisappear(50);
					}
					//Verify the room no column
					List<WebElement> grdHeader = elements("Reservation.manageResv_HdrName");
					List<WebElement> grdValue = elements("Reservation.manageResv_HdrValue");
					int grdSize = elements("Reservation.manageResv_HdrName").size();
					
					System.out.println(grdValue.size());
					for(int i=0;i<grdSize;i++)
						{				
							if(grdHeader.get(i).getText().equalsIgnoreCase("Room")){
								String strRoomNo = grdValue.get(i).getText();
								if(strRoomNo!=null){
									logger.log(LogStatus.PASS, "Rservation is updated with Room from Manage Resv Screen"+strRoomNo+" successfully");
								}else{
									logger.log(LogStatus.FAIL, "***Issue observed with Room from Manage Resv Screen***");
								}																
							}		
						  
						}	
			}catch (Exception e) {
	    				e.printStackTrace();
	    				Utils.takeScreenshot(driver, methodName);
	    				logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
	    				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
	    				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
	    			}

    			//Logout from Application
    			Utils.tearDown();				
    		}	/*******************************************************************
	- Description: Verify Assign Room from check in screen
	- Input: Reservation,Rooms
	- Output: Room assigned to reservation at check in screen
	- Author: Girish
	- Date: 28/01/18
	- Revision History:1.0
	 ********************************************************************/
	
	@Test(groups = {"Sanity"},priority = 14)
	public void roomAssignmentFromCheckInScreen() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
	    try {
	    	HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
			Thread.sleep(3000);
			//Search the room assignment in manage reservation screen
			HashMap<String, String> resvSearchMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
			ReservationPage.reservationSearch(resvSearchMap.get("ConfirmationNum"));
			// Select IWanTo			
			Utils.jsClick("Reservation.Search_Reservation_IwantTo");			
			waitForSpinnerToDisappear(20);

			// Selecting go to res link in I want to link			
			Utils.jsClick("Checkin.link_Rsv_CheckIn");			
			waitForSpinnerToDisappear(20);

			// Validating Reservation Page			
			Utils.WebdriverWait(30, "Checkin.txt_ReservationCheckIn_PageValidation", "presence");
			Utils.verifyCurrentPage("Checkin.txt_ReservationCheckIn_PageValidation", "checkInPage_validation");
	
			// Selecting Clean check box
			Utils.WebdriverWait(30, "Checkin.checkbox_ReservationCheckIn_Clean", "clickable");
			if (!isSelected("Checkin.checkbox_ReservationCheckIn_Clean", "Clean CheckBox")) {
				Utils.jsClick("Checkin.checkbox_ReservationCheckIn_Clean");
				waitForSpinnerToDisappear(30);
				logger.log(LogStatus.PASS, "Click on Clean checkbox");
			} else {
				logger.log(LogStatus.FAIL, "***Issue observed on clicking on Clean checkbox***");
			}
			
			// Selecting Dirty check box
			Utils.WebdriverWait(30, "Checkin.checkbox_ReservationCheckIn_Dirty", "clickable");
			if (!isSelected("Checkin.checkbox_ReservationCheckIn_Dirty", "Clean CheckBox")) {
				Utils.jsClick("Checkin.checkbox_ReservationCheckIn_Dirty");
				waitForSpinnerToDisappear(30);
				logger.log(LogStatus.PASS, "Click on Dirty checkbox");
			} else {
				logger.log(LogStatus.FAIL, "***Issue observed on clicking on Dirty checkbox***");
			}
						
			// Clicking on Search button
			click("Checkin.btn_ReservationCheckIn_Search",100,"clicable");			
			waitForSpinnerToDisappear(40);
			
			if(isExists("Checkin.link_ReservationCheckIn_SelectRoom")) {
				// Clicking on Select Room link
				jsClick("Checkin.link_ReservationCheckIn_SelectRoom",100,"clicable");				
				waitForSpinnerToDisappear(20);
			}
			click("Checkin.link_ReservationCheckIn_SelectRoom", 100, "clickable");
			waitForSpinnerToDisappear(20);
			
			//Click on Assign Room button 
			if(isExists("Housekeeping.RoomAssignment.btnAssignRoom")){
			click("Housekeeping.RoomAssignment.btnAssignRoom",100,"clickable");
			waitForSpinnerToDisappear(50);
			}
			//Verify additional Details pop up & click on Accept Room Selection button
			if(isExists("Housekeeping.RoomAssignment.btnAcceptRoomSelection")){
		         click("Housekeeping.RoomAssignment.btnAcceptRoomSelection",100,"clickable");
		         waitForSpinnerToDisappear(50);
		         logger.log(LogStatus.PASS, "Room assigned to reservation");
			}		
			//Click on Complete Check In Link
			 waitForSpinnerToDisappear(100);
			jsClick("Checkin.CompleteCheckInLink");
			waitForSpinnerToDisappear(100);
			//Handle Registration Card pop up
			if(Utils.isExists("Checkin.popup_RegistrationCard")) {		
				Utils.Wait(3000);
				Utils.click("Checkin.link_PrintRegistrationNo", 100, "clickable");				
			}
			//Handle Registration Card pop up
			if(Utils.isExists("Checkin.popup_PrintRegistrationCard")) {		
				Utils.Wait(3000);
				Utils.click("Checkin.link_ClodePrintRegistrationCard", 100, "clickable");				
			}
			//Handle successful checkin pop up
			if(Utils.isExists("Checkin.txt_CheckInSuccessMessage", 100, "presence")) {
				Utils.click("Checkin.btn_ReservationCheckIn_PopupOk", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);
			}
			//Verify Create Room Key Window pop up
			if(Utils.isExists("Checkin.popup_CreateRoomKeysWindow")) {
				Utils.Wait(3000);
				Utils.click("Checkin.link_CreateRoomKeysCancel", 100, "clickable");
				Utils.waitForSpinnerToDisappear(100);				
			}
			//Navigate back to Reservation screen
			if(Utils.isExists("Checkin.btn_GoToReservation",100,"presence")){
                Utils.Wait(3000);
                Utils.jsClick("Checkin.btn_GoToReservation"); 
                Utils.waitForSpinnerToDisappear(100);
            }
			//Verify reservation status as Checked In
            if (getText("Checkin.txt_ReservationCheckIn_checkInstatus").contains("Checked In")){
                logger.log(LogStatus.PASS, "Reservation Successfully CheckedIn");
            }else {
                logger.log(LogStatus.FAIL, "Reservation NOT CheckedIn properly");
            }
			
		}
	    catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		//Logout from Application
		Utils.tearDown();				
	}
	
	/*******************************************************************
	- Description: Verify Payment Instruction With Routing
	- Input: Reservation Confirmation Number, Payment Method and window number
	- Output: Room assigned to reservation at check in screen
	- Author: Praneeth
	- Date: 29/01/19
	- Revision History:1.0
	 ********************************************************************/
	
	@Test(groups = {"Sanity"},priority = 15)
	public void verifyPaymentInstructionWithRouting() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
	    try {
	    	HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
	    	//Create new Reservation
			String ConfirmationNum = ReservationPage.createReservationFromLTB(resvMap);
			ExcelUtils.setDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation","ConfirmationNum", ConfirmationNum);
			Thread.sleep(3000);
			//Get Reservation Confirmation number
			HashMap<String, String> updateResvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "Reservation", "createReservation");
			String ConfNum = updateResvMap.get("ConfirmationNum");
			//Get Payment Method and Window number
			HashMap<String, String> paymentMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "reservationPaymentMethod");
			String WinNumber= paymentMap.get("WindowNumber");
			String PayMethod = paymentMap.get("PaymentMethod");
			
			//Add new Payment method in Payment Instruction screen
			ReservationPage.reservationPaymentMethod(PayMethod,WinNumber,ConfNum);	
			//Verify the menu item 'View Routing Instruction'
			Utils.jsClick("Reservation.PaymentInstructions_NewEditPaymentInstructionsEllipsis");
			Utils.waitForSpinnerToDisappear(10);
			if(Utils.isExists("Reservation.PaymentInstructions_ViewRoutingInstruction"))
			{
				logger.log(LogStatus.PASS,"Successfully verified that the menu item 'View Routing Instruction' exists in Payment Instructions ellipsis dropdown");
			}
			else
			{
				System.out.println("The menu item 'View Routing Instruction' does not exist in Payment Instructions ellipsis dropdown");
				logger.log(LogStatus.FAIL, "The menu item 'View Routing Instruction' does not exist in Payment Instructions ellipsis dropdown");
			}
		}
	    catch (Exception e) {
			e.printStackTrace();
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
		}

		//Logout from Application
		Utils.tearDown();				

	}
	


	/*******************************************************************
	-  Description: Verify generation of Vacant Rooms report
	- Input: ReportName, ReportGroup
	- Output: generate task sheet
	- Author: Praneeth
	- Date: 08/02/2019
	- Revision History:1.0
	********************************************************************/
	@Test(groups = {"Sanity"},priority = 16)
	public void verifyVacantRoomReport() throws Exception {
	                String methodName = Utils.getMethodName();
	                
            System.out.println("methodName: "+methodName);
            try 
            {
                            Utils.takeScreenshot(driver, methodName);
                            logger.log(LogStatus.INFO, "<b> Verify if user is able to run a Vacant Rooms report </b>");
                            HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_27");
                            GenericPage.RunReport(configMap);
                            
                            //Copy data to clip board
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            Transferable contents = clipboard.getContents(null);
                            String strContent = (String) contents.getTransferData(DataFlavor.stringFlavor);
                            
                            Matcher m = Pattern.compile("\r\n|\r|\n").matcher(strContent);
                            int lines = 1;
                            while (m.find())
                            {
                                lines ++;
                            }
                            
                            if(lines >= 10)
                            {
                                            logger.log(LogStatus.PASS, "Successfully verified the Vacant Rooms report");
                            }
                            else
                            {
                                            logger.log(LogStatus.FAIL, "Vacant Rooms Report is empty");
                            }
            } 
            catch (Exception e) 
            {
                            Utils.takeScreenshot(driver, methodName);
                            logger.log(LogStatus.FAIL, methodName+" not created :: Failed");
                            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
                            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
                            throw (e);
            }
            Utils.tearDown();
}


	
	/*******************************************************************
	- Prerequisite for create task sheet & other dependants test cases
	- Creating 3 Rooms under same room types
	- Creating 1 Tasks
	- Create 2 Attendants
	- Author: Girish
	- Date: 05/02/19
	- Revision History:1.0
	 ********************************************************************/
	

	@Test(groups = {"Sanity"},priority = 17)
	public void preRequisiteTaskSheet() throws Exception {
	
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);		
		try 
	      {				
			   //Create tasks
			   for(int i = 1;i<=3;i++){
				   HashMap<String, String> tasksMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "HousekeepingTasks",  "Dataset_"+i);
			       ConfigPage.createTasks(tasksMap);
			     }
			   
			   //Create attendants
			   for(int j = 1;j<=2;j++){
				   HashMap<String, String> attendantsMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Attendants",  "Dataset_"+j);
			       ConfigPage.createAttendants(attendantsMap);
			     }
			   
			   
			   //Create multiple rooms		  
			   for(int k = 1;k<=6;k++){
				    HashMap<String, String> roomMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms", "Dataset_"+k);
			        ConfigPage.createRoom(roomMap);
			   }		
			
		}catch (Exception e) {
				e.printStackTrace();
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, "Prerequisite for task sheet not created :: Failed " + e.getLocalizedMessage());
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
			}
		
				//Logout from Application
				Utils.tearDown();				
			}
	
	

/*******************************************************************
-  Description: Auto Create/Generate Task sheets
- Input: Task name,Attendants, Credits
- Output: generate task sheet
- Author: Girish
- Date: 08/01/18
- Revision History:1.0
 ********************************************************************/

@Test(groups = {"Sanity"},priority = 18)
public void generateTaskSheetManually() throws Exception {

	String methodName = Utils.getMethodName();
	System.out.println("methodName: "+methodName);		
	try 
	{
		Utils.takeScreenshot(driver, methodName);
		logger.log(LogStatus.INFO, "<b> Verify user able to generate task sheets manually </b>");			   		
		//Reading data from sheet		   
		HashMap<String, String> tasksMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "HousekeepingTasks", "Dataset_1");
		HashMap<String, String> taskSheetMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"TaskSheets", "Dataset_1");

		HashMap<String, String> roomNoMap1 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms","Dataset_1");			
		String strRoomNo1 = roomNoMap1.get("ROOM");
		HashMap<String, String> roomNoMap2 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms","Dataset_2");			
		String strRoomNo2 = roomNoMap2.get("ROOM");
		HashMap<String, String> roomNoMap3 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms","Dataset_3");			
		String strRoomNo3 = roomNoMap3.get("ROOM");
        //Navigating to Main Menu
		click("Housekeeping.inventory_menu",100,"clickable");	
		// Navigating to Room Management link
		click("Housekeeping.menu_RoomManagement",100,"clickable");	
        // Navigating to Out Of service link
		click(Utils.element("Housekeeping.UpdateRoomStatus.menuItemHousekeepingBoard"));
		waitForSpinnerToDisappear(50);
		//Verify out of Service screen
		boolean blnHousekeepingBoardPage = isExists("Housekeeping.housekeeping_board_text");
		if(blnHousekeepingBoardPage){
			logger.log(LogStatus.PASS, "Navigate to Housekeeping Board screen");
								
			//Verify the above created room status to Dirty
			//Enter From room		    	 
             textBox("Housekeeping.txt_fromRoom",ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms", "Dataset_1").get("ROOM"));
             Utils.tabKey("Housekeeping.txt_fromRoom");                                   
             waitForSpinnerToDisappear(50);                
			 //Enter To Room
             textBox("Housekeeping.txt_toRoom",ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms", "Dataset_3").get("ROOM"));
             Utils.tabKey("Housekeeping.txt_toRoom");                                   
             waitForSpinnerToDisappear(50);
			 //Click on Search button
         	 click("Housekeeping.btn_Search",100,"clickable");
         	 waitForSpinnerToDisappear(5);
			 int dirtyrows = elements("Housekeeping.UpdateRoomStatus.roomRowsCount").size();
			
			 if(dirtyrows > 0) {	
				//Saving the room number of the selected room
				String roomNumber = getText("Housekeeping.UpdateRoomStatus.validationRoomText");
				System.out.println("roomNumber: " + roomNumber);
				
				click("Housekeeping.btn_AllArrow",100,"clickable");
				Utils.waitForSpinnerToDisappear(10);
				//Click on update room status
				if(isExists("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus"))
					click("Housekeeping.UpdateRoomStatus.linkUpdateRoomStatus");
			
				//Clicking on dirty check box to change the status of the room to dirty
				click("Housekeeping.UpdateRoomStatus.radioDirty",100,"clickable");			
				waitForSpinnerToDisappear(50);
				click("Housekeeping.UpdateRoomStatus.linkClose",100,"clickable");
				logger.log(LogStatus.PASS, "Room status have changed to Dirty");
			}
       //*****Start crating Task Sheet from Room Management- Task Sheet menu****************		         			
		Thread.sleep(5000);
		// Navigating to Main Menu
		click("Housekeeping.inventory_menu",100,"clickable");	
		// Navigating to Room Management link
		click("Housekeeping.menu_RoomManagement",100,"clickable");	
		// Navigating to Out Of service link
		click("Housekeeping.TaskSheet.linkTaskSheet",100,"clickable");
		waitForSpinnerToDisappear(50);
		logger.log(LogStatus.PASS, "Navigate to Task Sheet screen");

		//Click on Task sheet LOV button
		click("Housekeeping.TaskSheet.lovTaskSheet",100,"clickable");
		waitForSpinnerToDisappear(50);

		//Verify table should contains the existing tasks
		int intTask = elements("Housekeeping.TaskSheet.tblTaskSheet").size();
		boolean flag = false;
		if(intTask > 0)
		{    
			boolean bExists = Utils.ValidateGridData("Housekeeping.TaskSheet.tblTaskSheetRoom", tasksMap.get("TASK_CODE"));
			if(bExists)
			{
				flag = true;       			        			        	   
				logger.log(LogStatus.PASS, "Tasks details " +tasksMap.get("TASK_CODE")+ " already exists in LOV");
				System.out.println("Tasks details" +tasksMap.get("TASK_CODE")+ "already exists in LOV");
			}
			else
			{
				logger.log(LogStatus.PASS, "***Tasks details does not exists in LOV, Hence system will create in next steps***");
			}        			            
		}        			              			   
		//Click on Cancel button
		click("Housekeeping.TaskSheet.btnCancel",100,"clickable");
		waitForSpinnerToDisappear(50);
		if(!flag)
		{
			//Click on New button
			click("Housekeeping.TaskSheet.btnNewTaskSheet",100,"clickable");
			waitForSpinnerToDisappear(50);
			boolean blnTaskSheet = isExists("Housekeeping.TaskSheet.txtTaskLOV");
			if(blnTaskSheet)
			{       							   
				//Enter Tasks 
				textBox("Housekeeping.TaskSheet.txtTaskLOV",tasksMap.get("TASK_CODE"));
				Utils.tabKey("Housekeeping.TaskSheet.txtRoomLOV");       
				waitForSpinnerToDisappear(80);

				//Enter text on Room LOV	                            
				String strRoomNo = strRoomNo1+","+strRoomNo2;
				System.out.println(strRoomNo);

				click("Housekeeping.TaskSheet.txtRoomLOV");
				textBox("Housekeeping.TaskSheet.txtRoomLOV", strRoomNo);
				Utils.tabKey("Housekeeping.TaskSheet.txtRoomLOV");
				waitForSpinnerToDisappear(80);                                         

				//Enter text on Credits
				textBox("Housekeeping.TaskSheet.txtAddRoomsCredit", taskSheetMap.get("CREDITS"));
				Utils.tabKey("Housekeeping.TaskSheet.txtAddRoomsCredit");
				waitForSpinnerToDisappear(50);

				//Enter text on Room Instruction
				textBox("Housekeeping.TaskSheet.txtRoomInstruction", taskSheetMap.get("ROOM_INSTRUCTION"));
				Utils.tabKey("Housekeeping.TaskSheet.txtRoomInstruction");
				waitForSpinnerToDisappear(50);

				//Click on Create button
				jsClick("Housekeeping.TaskSheet.btnCreateTaskSheet",100,"clickable");
				waitForSpinnerToDisappear(50);
			}
		}
		//Navigate back to task header page
		//Enter text on Task Sheet
		textBox("Housekeeping.TaskSheet.txtSearchTask", tasksMap.get("TASK_CODE"));
		Utils.tabKey("Housekeeping.TaskSheet.txtSearchTask");
		waitForSpinnerToDisappear(50);

		//Click on Search button
		click("Housekeeping.TaskSheet.btnSearch",100,"clickable");
		waitForSpinnerToDisappear(50);

		//Click on Manage button
		click("Housekeeping.TaskSheet.linkManage",100,"clickable");
		waitForSpinnerToDisappear(50);
		//Verify the task sheet table
		List<WebElement>  taskSheetTable = Utils.elements("Housekeeping.TaskSheet.tblTaskSheetRooms");
		if(taskSheetTable.size()== 2)
		{						    			  
			logger.log(LogStatus.PASS, "Task sheet (Manually) have created successfully");
		}
		else
		{
			logger.log(LogStatus.FAIL, "***Issue observed during Task sheet (Manually) creation***");
		}

		//Click on Action button
		click("Housekeeping.TaskSheet.btnActions",100,"clickable");
		waitForSpinnerToDisappear(10);
		//Click on Add Rooms button
		click("Housekeeping.TaskSheet.menuAddRooms",100,"clickable");
		waitForSpinnerToDisappear(20);
		
		boolean blnAddRooms = isExists("Housekeeping.TaskSheet.headerAddRooms");
		if(blnAddRooms)
		{
			logger.log(LogStatus.PASS, "Navigate to Add Rooms screen");
			//click on rooms lov
			click("Housekeeping.TaskSheet.lovRooms",100,"clickable");
			waitForSpinnerToDisappear(40);
			Utils.textBox("Housekeeping.TaskSheet.txtRoomSearch",strRoomNo3);
			Utils.tabKey("Housekeeping.TaskSheet.roomInstructions");
			Utils.click("Housekeeping.TaskSheet.btnRoomSearch");
			waitForSpinnerToDisappear(20);
			Utils.clickOnElementBasedOnTextContains("Housekeeping.TaskSheet.tblRoomSearch", strRoomNo3, "textContent");
			logger.log(LogStatus.PASS, "System displays the multiple rooms in LOV & able to select the correseponding rooms");
			waitForSpinnerToDisappear(20);
			click("Housekeeping.TaskSheet.btnSelect",100,"clickable");
			//Enter text in Add room credit fields
			textBox("Housekeeping.TaskSheet.txtAddRoomsCredit","2");
			tabKey("Housekeeping.TaskSheet.txtAddRoomsCredit");
			waitForSpinnerToDisappear(40);
			Utils.textBox("Housekeeping.TaskSheet.roomInstructions", "New Room Added to the Task Sheet");
			Utils.tabKey("Housekeeping.TaskSheet.roomInstructions");
			waitForSpinnerToDisappear(20);
			//Click on Add button
			click("Housekeeping.TaskSheet.btnAdd",100,"clickable");
			waitForSpinnerToDisappear(40);								    	  									    	  
		}

		clickOnElementBasedOnTextContains("Housekeeping.TaskSheet.tblTaskSheetRooms", strRoomNo3, "textContent");
		waitForSpinnerToDisappear(40);
		//Click on Action button
		click("Housekeeping.TaskSheet.btnActions",100,"clickable");
		waitForSpinnerToDisappear(10);
		Utils.click("Housekeeping.TaskSheet.linkUpdateRoomDetails");
		waitForSpinnerToDisappear(20);
		if(Utils.getText("Housekeeping.TaskSheet.roomInstructions").compareTo("New Room Added to the Task Sheet") == 0)
		{
			logger.log(LogStatus.PASS, "Room Instructions description matches with the description given while adding Room to Task Sheet");
		}
		else
		{
			logger.log(LogStatus.FAIL, "The Room Instructions given at the time of Adding the Room does not match with description after opening Update Room Details");
		}
		//Update Room Instructions description
		Utils.textBox("Housekeeping.TaskSheet.roomInstructions", "Existing Room Instruction Updated");
		Utils.tabKey("Housekeeping.TaskSheet.roomInstructions");
		waitForSpinnerToDisappear(20);
		textBox("Housekeeping.TaskSheet.txtAddRoomsCredit", "3");
		Utils.tabKey("Housekeeping.TaskSheet.txtAddRoomsCredit");
		waitForSpinnerToDisappear(10);
		click("Housekeeping.TaskSheet.btnUpdateRoomDetails");
		waitForSpinnerToDisappear(30);
		
		//Click on Action button
		click("Housekeeping.TaskSheet.btnActions",100,"clickable");
		waitForSpinnerToDisappear(10);
		//Click on Go To Task Sheet Companion
		click("Housekeeping.TaskSheet.menuGoToTaskSheetCompanion",100,"clickable");
		waitForSpinnerToDisappear(40);
		
		HousekeepingPage.startUpdateAndFinishRoomInTaskSheetCompanion(strRoomNo1,"Housekeeping.TaskSheet.lblInspected");
		HousekeepingPage.startUpdateAndFinishRoomInTaskSheetCompanion(strRoomNo2,"Housekeeping.TaskSheet.lblInspected");
		
		Utils.click("Housekeeping.TaskSheet.btnCloseTaskSheetCompanion");
		waitForSpinnerToDisappear(40);
		
		//Click on Action button
		jsClick("Housekeeping.TaskSheet.btnActions",100,"clickable");
		waitForSpinnerToDisappear(10);
		//Click on Delete Task Sheet
		click("Housekeeping.TaskSheet.menuDeleteTaskSheet",100,"clickable");
		waitForSpinnerToDisappear(10);
		//Click on Confirm to delete the task sheet
		click("Housekeeping.TaskSheet.btnDeleteTaskSheet",100,"clickable");
		waitForSpinnerToDisappear(20);
		
				}
	} catch (Exception e) {
		e.printStackTrace();
		Utils.takeScreenshot(driver, methodName);
		logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
	}

		//Logout from Application
		Utils.tearDown();				
	}



/*******************************************************************
-  Description: Auto Create/Generate Task sheets
- Input: Task name,Attendants, Credits
- Output: generate task sheet
- Author: Girish
- Date: 08/01/18
- Revision History:1.0
********************************************************************/

@Test(groups = {"Sanity"},priority = 19)
public void generateTaskSheetAutomatically() throws Exception {

String methodName = Utils.getMethodName();
System.out.println("methodName: "+methodName);		
try {
	   Utils.takeScreenshot(driver, methodName);
	   logger.log(LogStatus.INFO, "<b> Verify user able to generate task sheets automatically </b>");			   		
		   
	   HashMap<String, String> tasksMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "HousekeepingTasks", "Dataset_2");
	   HashMap<String, String> taskSheetMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"TaskSheets", "Dataset_1");
		click("Housekeeping.inventory_menu",100,"clickable");	
		// Navigating to Room Management link
		click("Housekeeping.menu_RoomManagement",100,"clickable");	
	
		// Navigating to Out Of service link
		click(Utils.element("Housekeeping.UpdateRoomStatus.menuItemHousekeepingBoard"));
		waitForSpinnerToDisappear(50);
			//Click on I Want To button
			waitForSpinnerToDisappear(80);
			click("Housekeeping.TaskSheet.arrowIWantTo",100,"clickable");
			waitForSpinnerToDisappear(80);
			click("Housekeeping.TaskSheet.linkGenerateTaskSheet",100,"clickable");
			waitForSpinnerToDisappear(80);
		    boolean blnTaskSheet = isExists("Housekeeping.TaskSheet.txtHeader");
			     if(blnTaskSheet){
					   
			    	   //Enter Tasks 
				    	textBox("Housekeeping.TaskSheet.txtTaskLOV",tasksMap.get("TASK_CODE"));
				    	Utils.tabKey("Housekeeping.TaskSheet.txtCredits");       
                        waitForSpinnerToDisappear(80);
                               
                        //Enter text on Attendants LOV	                            
                        HashMap<String, String> attendantMap1 = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"),"Attendants","Dataset_1");			
                        String strAttendants1 = attendantMap1.get("ATTENDANTS_CODE");
                        HashMap<String, String> attendantMap2 = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"),"Attendants","Dataset_2");			
                        String strAttendants2 = attendantMap2.get("ATTENDANTS_CODE");
                        String strAttendants = strAttendants1+","+strAttendants2;
                        System.out.println(strAttendants);
                        
                        click("Housekeeping.TaskSheet.txtAttendantsLOV");
                        textBox("Housekeeping.TaskSheet.txtAttendantsLOV", strAttendants);
                        Utils.tabKey("Housekeeping.TaskSheet.txtAttendantsLOV");
				    	waitForSpinnerToDisappear(80);
				    				    	
				    	 //Enter text on Credits
                        textBox("Housekeeping.TaskSheet.txtCredits", taskSheetMap.get("CREDITS"));
				    	Utils.tabKey("Housekeeping.TaskSheet.txtCredits");
				    	waitForSpinnerToDisappear(50);
				    	
 					  	//Click on Next button
				    	jsClick("Housekeeping.TaskSheet.btnNext",100,"clickable");
				    	waitForSpinnerToDisappear(50);
				    	
				    	//Verify that the Generate task sheet button is enabled
				    	boolean blnEnable = isExists("Housekeeping.TaskSheet.btnGenerateTaskSheet");						    	
				        if(blnEnable){
				        	      logger.log(LogStatus.PASS, "Generate Task Sheet button is enabled");
				        	      //Click on Generate Task Sheet
							      click("Housekeeping.TaskSheet.btnGenerateTaskSheet",100,"clickable");
							      waitForSpinnerToDisappear(50);
							      //Verify the task sheet table
								     List<WebElement>  taskSheetTable = Utils.elements("Housekeeping.TaskSheet.tblTaskSheetRooms");
								     if(taskSheetTable.size()!=0){						    			  
							    		  logger.log(LogStatus.PASS, "Task sheet (Automatically) have created successfully");
							    		}else{
							    		  logger.log(LogStatus.FAIL, "***Issue observed during Task sheet (Automatically) creation***");
							    		}
							
				        	}else{
				        		   logger.log(LogStatus.FAIL, "***Issue observed to verify Generate Task Sheet button in enabled state***");
				        	}
				        																										
			    }else{
			    	logger.log(LogStatus.FAIL, "*****Housekeeping Board Page does not verified successfully*****"); 					
			}				
	} catch (Exception e) {
		e.printStackTrace();
		Utils.takeScreenshot(driver, methodName);
		logger.log(LogStatus.FAIL, "Reservation not created :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));				
	}

	//Logout from Application
	Utils.tearDown();				
  }

/*******************************************************************
-  Description: Move Rooms from One Task Sheet to another
- Input: Task name,Attendants, Credits
- Output: generate task sheet
- Author: Praneeth
- Date: 21/02/19
- Revision History:1.0
********************************************************************/
@Test(groups = {"Sanity"},priority = 20)
public void moveRoomsToAnotherTaskSheet() throws Exception {
  String methodName = Utils.getMethodName();
  System.out.println("methodName: "+methodName);        
  try 
  {
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.INFO, "<b> Verify user able to move rooms to another task sheet </b>");                  
      //Reading data from sheet          
      HashMap<String, String> tasksRevMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "HousekeepingTasks", "Dataset_3");
      HashMap<String, String> taskSheetRevMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"TaskSheets", "Dataset_1");
      //Navigating to Main Menu
      click("Housekeeping.inventory_menu",100,"clickable");   
      // Navigating to Room Management link
      click("Housekeeping.menu_RoomManagement",100,"clickable");  
      // Navigating to Task Sheet link
      click("Housekeeping.TaskSheet.linkTaskSheet",100,"clickable");
      waitForSpinnerToDisappear(50);
      logger.log(LogStatus.PASS, "Navigate to Task Sheet screen");
      //Click on Task sheet LOV button
      click("Housekeeping.TaskSheet.lovTaskSheet",100,"clickable");
      waitForSpinnerToDisappear(50);
      
      boolean flag = false;
      
      if(Utils.isExists("Housekeeping.TaskSheet.noTaskSheetsMessage"))
      {
    	  logger.log(LogStatus.PASS, "***Tasks details does not exists in LOV, Hence system will create in next steps***");
          //Click on Cancel button
          click("Housekeeping.TaskSheet.btnCancel",100,"clickable");
          waitForSpinnerToDisappear(50);
      }
      else
      {
    	  if(Utils.ValidateGridData("Housekeeping.TaskSheet.tblTaskSheetLOVRoom", tasksRevMap.get("TASK_CODE")))
    	  {
    		  flag = true;                                                   
              logger.log(LogStatus.PASS, "Tasks details " +tasksRevMap.get("TASK_CODE")+ " already exists in LOV");
              System.out.println("Tasks details " +tasksRevMap.get("TASK_CODE")+ " already exists in LOV");             
              Utils.clickOnElementBasedOnTextContains("Housekeeping.TaskSheet.tblTaskSheetLOVRoom",tasksRevMap.get("TASK_CODE"),"textContent");
              waitForSpinnerToDisappear(50);
              Utils.click("Housekeeping.TaskSheet.btnSelect");
              waitForSpinnerToDisappear(20);
    	  }
    	  else
    	  {
    		  logger.log(LogStatus.PASS, "***Tasks details does not exists in LOV, Hence system will create in next steps***");
              //Click on Cancel button
              click("Housekeeping.TaskSheet.btnCancel",100,"clickable");
              waitForSpinnerToDisappear(50);
    	  }
      }                                             
      
      if(!flag)
      {
    	  /* Creating TaskSheet 1 */
          //Click on New button
          click("Housekeeping.TaskSheet.btnNewTaskSheet",100,"clickable");
          waitForSpinnerToDisappear(50);
          boolean blnTaskSheet = isExists("Housekeeping.TaskSheet.txtTaskLOV");
          if(blnTaskSheet)
          {                                      
              //Enter Tasks 
              textBox("Housekeeping.TaskSheet.txtTaskLOV",tasksRevMap.get("TASK_CODE"));
              Utils.tabKey("Housekeeping.TaskSheet.txtRoomLOV");       
              waitForSpinnerToDisappear(80);
              //Enter text on Room LOV                                
              HashMap<String, String> roomNoMap1 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms","Dataset_4");            
              String strRoomNo1 = roomNoMap1.get("ROOM");
              HashMap<String, String> roomNoMap2 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms","Dataset_5");            
              String strRoomNo2 = roomNoMap2.get("ROOM");
              String strRoomNo = strRoomNo1+","+strRoomNo2;
              System.out.println(strRoomNo);
              click("Housekeeping.TaskSheet.txtRoomLOV");
              textBox("Housekeeping.TaskSheet.txtRoomLOV", strRoomNo);
              Utils.tabKey("Housekeeping.TaskSheet.txtRoomLOV");
              waitForSpinnerToDisappear(80);                                         
              //Enter text on Credits
              textBox("Housekeeping.TaskSheet.txtAddRoomsCredit", taskSheetRevMap.get("CREDITS"));
              Utils.tabKey("Housekeeping.TaskSheet.txtAddRoomsCredit");
              waitForSpinnerToDisappear(50);
              //Enter text on Room Instruction
              textBox("Housekeeping.TaskSheet.txtRoomInstruction", taskSheetRevMap.get("ROOM_INSTRUCTION"));
              Utils.tabKey("Housekeeping.TaskSheet.txtRoomInstruction");
              waitForSpinnerToDisappear(50);
              //Click on Create button
              jsClick("Housekeeping.TaskSheet.btnCreateTaskSheet",100,"clickable");
              waitForSpinnerToDisappear(50);
          }
          /*Creating Task Sheet 2*/
          //Click on New button
          click("Housekeeping.TaskSheet.btnNewTaskSheet",100,"clickable");
          waitForSpinnerToDisappear(50);
          //Enter text on Room LOV                                
          HashMap<String, String> roomNoMap1 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"),"Rooms","Dataset_6");            
          String strRoomNo1 = roomNoMap1.get("ROOM");
          System.out.println(strRoomNo1);
          click("Housekeeping.TaskSheet.txtRoomLOV");
          textBox("Housekeeping.TaskSheet.txtRoomLOV", strRoomNo1);
          Utils.tabKey("Housekeeping.TaskSheet.txtRoomLOV");
          waitForSpinnerToDisappear(80);                                         
          //Enter text on Credits
          textBox("Housekeeping.TaskSheet.txtAddRoomsCredit", taskSheetRevMap.get("CREDITS"));
          Utils.tabKey("Housekeeping.TaskSheet.txtAddRoomsCredit");
          waitForSpinnerToDisappear(50);
          //Click on Create button
          jsClick("Housekeeping.TaskSheet.btnCreateTaskSheet",100,"clickable");
          waitForSpinnerToDisappear(50);
      }
      //Enter Task Name
      Utils.textBox("Housekeeping.TaskSheet.txtSearchTask", tasksRevMap.get("TASK_CODE"));
      Utils.tabKey("Housekeeping.TaskSheet.txtSearchTask");
      Utils.waitForSpinnerToDisappear(10);
      //Click on Search button
      click("Housekeeping.TaskSheet.btnSearch",100,"clickable");
      waitForSpinnerToDisappear(50);
          
      //Verify the task sheet table for two Task Sheets
      List<WebElement>  tableSummaryWithFirst = Utils.elements("Housekeeping.TaskSheet.tblTaskSheet");
      System.out.println("summary table size after 1st is: " +tableSummaryWithFirst.size());
      if(tableSummaryWithFirst.size()==2)
      {                                         
          logger.log(LogStatus.PASS, "Successfully created two task sheets (Manually)");
      }
      else
      {
          logger.log(LogStatus.FAIL, "***Issue observed during two Task sheets (Manually) creation***");
      }
      //Click on Manage link
      click("Housekeeping.TaskSheet.linkManage",100,"clickable");
      waitForSpinnerToDisappear(50);
      
      //Select the Source Task sheet to Move Rooms
      List<WebElement> taskSheetRooms = elements("Housekeeping.TaskSheet.tblTaskSheetRoom");
      String targetTaskSheet = "2";
      String sourceTaskSheet = "1";
      if(taskSheetRooms.size()!=2)
      {
    	  selectBy("Housekeeping.TaskSheet.dropDownTaskSheetSelection", "text", targetTaskSheet);
    	  Utils.waitForSpinnerToDisappear(10);
    	  Thread.sleep(3000);
    	  targetTaskSheet = "1";
    	  sourceTaskSheet = "2";
    	  taskSheetRooms = elements("Housekeeping.TaskSheet.tblTaskSheetRoom");
      }
      //Select the Room by clicking on the Row
      taskSheetRooms.get(0).click();
      Utils.waitForSpinnerToDisappear(10);
      Utils.click("Housekeeping.TaskSheet.btnActions");
      Utils.waitForSpinnerToDisappear(15);
      
      //Click on Target Task Sheet to Move Room
      Utils.mouseHover("Housekeeping.TaskSheet.linkMoveRoomToAnotherTaskSheet");
      Thread.sleep(3000);
      Utils.driver.findElement(By.xpath("(//tr[@data-ocid='MITEM_TASKSHEET"+targetTaskSheet+"'])[2]//descendant::*[text()='Task Sheet "+targetTaskSheet+"']")).click();
      Utils.waitForSpinnerToDisappear(10);
      Thread.sleep(3000);
      
      //Navigate to Target Task Sheet and get count of Rooms
      selectBy("Housekeeping.TaskSheet.dropDownTaskSheetSelection", "text", targetTaskSheet);
      Utils.waitForSpinnerToDisappear(10);
      Thread.sleep(3000);
      List<WebElement> targetTaskSheetRooms = elements("Housekeeping.TaskSheet.tblTaskSheetRoom");
      
      //Navigate to Source Task Sheet and get count of Rooms
      selectBy("Housekeeping.TaskSheet.dropDownTaskSheetSelection", "text", sourceTaskSheet);
      Utils.waitForSpinnerToDisappear(10);
      Thread.sleep(3000);
      List<WebElement> sourceTaskSheetRooms = elements("Housekeeping.TaskSheet.tblTaskSheetRoom");
      
      //Validate if the Room is successfully moved from one Task Sheet to another
      if(targetTaskSheetRooms.size() == 2 && sourceTaskSheetRooms.size() == 1)
      {
    	  logger.log(LogStatus.PASS, "Successfully moved one room from Task Sheet: " + sourceTaskSheet + "To Task Sheet: " + targetTaskSheet);
      }
      else
      {
    	  logger.log(LogStatus.FAIL, "Failed to move one room from Task Sheet: " + sourceTaskSheet + "To Task Sheet: " + targetTaskSheet);
      }
      
  } catch (Exception e) {
      e.printStackTrace();
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.FAIL, "Move room to another task sheet not created :: Failed " + e.getLocalizedMessage());
      logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
      logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));              
  }
      //Logout from Application
      Utils.tearDown();               
  }

/*******************************************************************
-  Description: Create Service Request Code in Administration
- Input: Service Request Code to create, Description and Department
- Output: Service Request Code created
- Author: Praneeth
- Date: 01/03/19
- Revision History:1.0
********************************************************************/
@Test(groups = {"Sanity"},priority = 21)
public void createServiceRequestCode() throws Exception {
  String methodName = Utils.getMethodName();
  System.out.println("methodName: "+methodName);        
  try 
  {
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.INFO, "<b> Verify user able to create Service Request Code </b>");  
      HashMap<String, String> srcMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ServiceRequestCode",  "Dataset_1");
      ConfigPage.createServiceRequestCode(srcMap);
  }
  catch(Exception ex)
  {
	  ex.printStackTrace();
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.FAIL, "Create Service Request Code :: Failed " + ex.getLocalizedMessage());
      logger.log(LogStatus.FAIL, "Exception occured in test due to:" + ex);
      logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName))); 
  }
//Logout from Application
  Utils.tearDown();  
}

/*******************************************************************
-  Description: Create Service Request Priority in Administration
- Input: Service Request Priority to create, Description
- Output: Service Request Priority created
- Author: Praneeth
- Date: 01/03/19
- Revision History:1.0
********************************************************************/
@Test(groups = {"Sanity"},priority = 22)
public void createServiceRequestPriority() throws Exception {
  String methodName = Utils.getMethodName();
  System.out.println("methodName: "+methodName);        
  try 
  {
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.INFO, "<b> Verify user able to create Service Request Priority </b>");  
      HashMap<String, String> srcPriorityMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "ServiceRequestPriority",  "Dataset_1");
      ConfigPage.createServiceRequestPriority(srcPriorityMap);
  }
  catch(Exception ex)
  {
	  ex.printStackTrace();
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.FAIL, "Create Service Request Priority :: Failed " + ex.getLocalizedMessage());
      logger.log(LogStatus.FAIL, "Exception occured in test due to:" + ex);
      logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName))); 
  }
//Logout from Application
  Utils.tearDown();  
}

/*******************************************************************
-  Description: Create, Edit and Complete Service Request
- Input: Service Request Details
- Output: Service Request Created, Edited and Completed
- Author: Praneeth
- Date: 07/03/19
- Revision History:1.0
********************************************************************/
@Test(groups = {"Sanity"},priority = 23)
public void createEditAndCompleteServiceRequest() throws Exception {
  String methodName = Utils.getMethodName();
  System.out.println("methodName: "+methodName);        
  try 
  {
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.INFO, "<b> Verify user able to create, edit and complete a Service Request </b>");  
      
      HashMap<String, String> srMap = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "ServiceRequests",  "Dataset_1");
      //Create Service Request
      HousekeepingPage.createServiceRequest(srMap);
      logger.log(LogStatus.INFO,"Created Service Request");
      
      //Edit Service Request
      HashMap<String, String> srMap1 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "ServiceRequests",  "Dataset_2");
      HousekeepingPage.editServiceRequest(srMap1);
      logger.log(LogStatus.INFO,"Edited Service Request");
      
      //Complete and Close Service Request
      HashMap<String, String> srMap2 = ExcelUtils.getDataByRow(OR.getConfig("Path_HousekeepingData"), "ServiceRequests",  "Dataset_3");
      HousekeepingPage.completeAndCloseServiceRequest(srMap2);
      logger.log(LogStatus.INFO,"Completed Service Request");
  }
  catch(Exception ex)
  {
	  ex.printStackTrace();
      Utils.takeScreenshot(driver, methodName);
      logger.log(LogStatus.FAIL, "Create Service Request Priority :: Failed " + ex.getLocalizedMessage());
      logger.log(LogStatus.FAIL, "Exception occured in test due to:" + ex);
      logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName))); 
  }
//Logout from Application
  Utils.tearDown();  
}

}





