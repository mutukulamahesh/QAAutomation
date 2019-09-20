package com.oracle.hgbu.opera.qaauto.ui.crm.component.rates;

import static org.testng.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.oracle.hgbu.opera.qaauto.ui.utilities.BaseClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.Assert;

public class RatesPage extends Utils {

	
	/***************************************************************************************************************
	 * - Description: Creating reservation with a restricted rate for a property
	 * - Input: Hasp map with required details like Source code Market code
	 * Reservation Types ,restricted rate code etc - Output: - Author: swati -
	 * Date: 6/12/2018 - Revision History: - Change Date - Change Reason -
	 * Changed Behavior - Last Changed By
	 *****************************************************************************************************************/

	public static void createReservationWithRestrictedRate(HashMap<String, String> resvMapp) throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);

		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {
			Utils.takeScreenshot(driver, testName);

			// Navigating to Booking menu
			Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			Utils.mouseHover("Reservation.booking_menu");
			Utils.click(Utils.element("Reservation.booking_menu"));
			System.out.println("clicked booking");
			logger.log(LogStatus.PASS, "Selected Bookings Menu");

			// Navigating to Reservations menu
			Utils.WebdriverWait(100, "Reservation.reservation_menu", "clickable");
			Utils.click(Utils.element("Reservation.reservation_menu"));
			System.out.println("clicked reservation");
			logger.log(LogStatus.PASS, "Selected Reservations Menu");

			// Navigating to LTB menu
			Utils.WebdriverWait(100, "Reservation.LookToBook_Menu", "clickable");
			Utils.jsClick("Reservation.LookToBook_Menu");
			System.out.println("clicked LTB");
			logger.log(LogStatus.PASS, "Selected LTB Menu");
			waitForSpinnerToDisappear(50);

			
			
			
			
			if (isExists("Reservation.New_Reservation_Button")) {
				// clicking on New Reservation Button
				WebdriverWait(100, "Reservation.New_Reservation_Button", "presence");
				mouseHover("Reservation.New_Reservation_Button");
				jsClick("Reservation.New_Reservation_Button");
				System.out.println("Look to Book Sales Screen is Displayed");
				logger.log(LogStatus.PASS, "Selected Create New reservation Button in ltb Page");
			}

			
			//Calling the getbusinessDate method to get the business date in enviornment file.
			
			Utils.getBusinessDate();
			
			Map<String,String> envMap = new HashMap<String,String>(); 
			envMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EnvironmentDetails"),
			  "Configuration","BusinessDate");
			
			String envname = ExcelUtils.getCellData(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "Set", "VALUE");
			
			String businessDate = ExcelUtils.getCellData(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "BusinessDate",envname);
			
			//String businessDates =  	envMap.get("VAB");
		
			//Date newbusinessDate=new SimpleDateFormat("MM-dd-yyyy").parse(businessDate);
			
			textBox("Rates.txtArrivalDate", businessDate);
			Utils.tabKey("Rates.txtArrivalDate");
			waitForSpinnerToDisappear(50);
			// Room and Rate options tab

			String endDate = Utils.AdddaysToBusinessdate(1);
			
			textBox("Rates.txtDepartureDate", endDate);
			Utils.tabKey("Rates.txtDepartureDate");
			waitForSpinnerToDisappear(50);
			
			click("Rates.RoomRateOptionsTab");
			waitForSpinnerToDisappear(50);

			textBox("Rates.RoomRateOptionsTabRateCode", resvMap.get("RATE_CODE"));
			Utils.tabKey("Rates.RoomRateOptionsTabRateCode");
			
			//Selecting the include close rates checkbox
			
			if(!Utils.isSelected("Rates.RateCodeClosedRatesCheckbox", "Include Check Box")) {
				jsClick("Rates.RateCodeClosedRatesCheckbox");
				waitForSpinnerToDisappear(100);
				logger.log(LogStatus.INFO, "Include closed rate codes in the LTB screen is selected");
				
			}

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
					Utils.element("Rates.manage_reservation_searchBtn"));
			Utils.WebdriverWait(100, "Rates.manage_reservation_searchBtn", "clickable");
			Utils.mouseHover("Rates.manage_reservation_searchBtn");
			Utils.jsClick("Rates.manage_reservation_searchBtn");

			// mouseHover("Reservation.manage_reservation_searchBtn");
			// click("Reservation.manage_reservation_searchBtn");
			waitForSpinnerToDisappear(100);

			List ele = driver.findElements(
					By.xpath("//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']"));

			System.out.println("//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']");
			System.out.println(ele.size());

			if (ele.size() > 0) {
				Actions action = new Actions(driver);
                //Find the targeted element
                WebElement webElement = driver.findElement(By.xpath("//*[@data-ocid='0_" + resvMap.get("ROOM_TYPE") + "_" + resvMap.get("RATE_CODE") + "']"));
                               //Here I used JavascriptExecutor interface to scroll down to the targeted element
               // ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", ele);
                               //used doubleClick(element) method to do double click action
               action.doubleClick(webElement).build().perform();
               
              // WebElement webElement = BaseClass.driver.findElement(ele1);
   			JavascriptExecutor executor = (JavascriptExecutor) driver;
   			executor.executeScript("arguments[0].click();", webElement);
   			

			// Selecting Select Button in Availabilty
			Utils.WebdriverWait(100, "Reservation.ltb_manage_profile_select", "clickable");
			Utils.jsClick("Reservation.ltb_manage_profile_select");
			System.out.println("Selected select btn of Availability in LTB page.....");
			logger.log(LogStatus.PASS, "Selected Select button of Availability in LTB page..");



			
				waitForSpinnerToDisappear(200);

				//String text = Utils.getText("Rates.RateCodeScreenRestrictionText");

				//validating begin date
				try {
				//String begindate = driver.findElement(By.xpath("//span[contains(@data-ocid,'TXT_CNTNT_ODEC_DT_IT') and contains(text(),"+"\'"+resvMap.get("BEGIN_DATE")+"\'"+")]")).getText();
				String begindate = driver.findElement(By.xpath("//span[contains(@data-ocid,'TXT_CNTNT_ODEC_DT_IT') and contains(text(),"+"\'"+businessDate+"\'"+")]")).getText();
                assertEquals(begindate, businessDate, "Restriction start date from datasheet and application does not match");
                logger.log(LogStatus.PASS, "Restriction start date is correct" + "Date from reservation screen -> "
						+ begindate + "Date to be verified->  " + businessDate);
				
				System.out.println("begindate" + begindate);
				

				String enddate = driver.findElement(By.xpath("//span[contains(@data-ocid,'TXT_CNTNT_ODEC_DT_IT') and contains(text(),"+"\'"+endDate+"\'"+")]")).getText();
                assertEquals(enddate, endDate, "Restriction end date from datasheet and application does not match");
                logger.log(LogStatus.PASS, "Restriction end date is correct" + "Date from reservation screen -> "
						+ enddate + "Date to be verified->  " + endDate);
				
				System.out.println("enddate" + enddate);
				
				//System.out.println("text" + text);
				List<WebElement> tableData = driver.findElements(By.xpath("//*[@data-ocid='TT1']//tr//span[contains(@data-ocid,'TXT_CNTNT_ODEC_IT_IT')]"));
				boolean flag=false;
				if(tableData.size()!=0) {
					for (int i=0;i<=tableData.size()-1;i++) {
						//String rowText = tableData.get(i).getText().equals("The Rate/Room Type is Closed for this date");
						if (tableData.get(i).getText().equals("The Rate/Room Type is Closed for this date")) {
							flag=true;
							logger.log(LogStatus.PASS, "Rate restriction is validated successfully->  " + tableData.get(i).getText());
							break;
						} 
						
					}
				}
				
				if(!flag) {
					logger.log(LogStatus.FAIL, "Rate restriction is not validated successfully-> " + "");
				}
				}		
			catch(Exception e) {
				e.printStackTrace();
			}
		} 
			else {
				System.out.println("Restricted Rate code is not found in the search results");
				logger.log(LogStatus.FAIL, "Restricted Rate code is not found in the search results");
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

	/***************************************************************************************************************
	 * - Description: Validating search functionality on property availability
	 * screen - Input: Hasp map with required details like Source code Market
	 * code Reservation Types ,restricted rate code etc - Output: - Author:
	 * swati - Date: 6/12/2018 - Revision History: - Change Date - Change Reason
	 * - Changed Behavior - Last Changed By
	 ****************************************************************************************************************/
	public static void ValidatePropertyAvailabilityScreen(HashMap<String, String> resvMapp, String SearchOption)
			throws Exception {
		String testName = Utils.getMethodName();
		System.out.println("name: " + testName);

		HashMap<String, String> resvMap = resvMapp;
		System.out.println("Map: " + resvMap);

		try {
			Utils.takeScreenshot(driver, testName);

			click("Housekeeping.inventory_menu");
			click("Rates.PropertyAvail");

			waitForSpinnerToDisappear(20);

			if (SearchOption.equals("RoomType")) {
				Utils.selectBy("Rates.PropertyAvail.RoomTypeDropdown", "text", "Room Type");
				Utils.tabKey("Rates.PropertyAvail.RoomTypeDropdown");

				textBox("Rates.PropertyAvail.RoomTypeText", resvMap.get("ROOM_TYPE"));
				Utils.tabKey("Rates.PropertyAvail.RoomTypeText");

				jsClick("Rates.manage_reservation_searchBtn");

				waitForSpinnerToDisappear(50);

				if (Utils.isExists("Rates.PropertyAvail.TableValidation")) {

					String roomType = driver
							.findElement(By.xpath("//*[@data-ocid='LINK_" + resvMap.get("ROOM_TYPE") + "']")).getText();

					System.out.println("roomtype" + roomType);

					if (!roomType.equals(resvMap.get("ROOM_TYPE"))) {

						logger.log(LogStatus.FAIL, "Room Type search is not working as expected ");
					} else {
						logger.log(LogStatus.PASS, "Room Type search is working as expected ");
					}

				} else {
					logger.log(LogStatus.FAIL, "There are no rows in the table");
				}

			}

			else if (SearchOption.equals("RoomClass")) {

				Utils.selectBy("Rates.PropertyAvail.RoomTypeDropdown", "text", "Room Class");
				Utils.tabKey("Rates.PropertyAvail.RoomTypeDropdown");

				textBox("Rates.PropertyAvail.RoomClassText", resvMap.get("ROOM_TYPE"));
				Utils.tabKey("Rates.PropertyAvail.RoomClassText");

				jsClick("Rates.manage_reservation_searchBtn");

				waitForSpinnerToDisappear(50);

				if (Utils.isExists("Rates.PropertyAvail.TableValidation")) {

					String roomType = driver
							.findElement(By.xpath("//*[@data-ocid='LINK_" + resvMap.get("ROOM_CLASS") + "']"))
							.getText();

					if (!roomType.equals(resvMap.get("ROOM_CLASS"))) {

						logger.log(LogStatus.FAIL, "Room Class search is not working as expected ");
					} else {
						logger.log(LogStatus.PASS, "Room Class search is working as expected ");
					}

				} else {
					logger.log(LogStatus.FAIL, "There are no rows in the table");
				}

			}

			else {
				logger.log(LogStatus.FAIL, "Invalid search option provided");
			}

		} catch (Exception e) {
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

	public static void abilityTochangeViewOptionsPropertyscreen(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + configMap);

		String ClientId2 = "";
		String communicationValue = "";

		try {
			
			 /*********
			  * 
			  * houseboard page coding starts here.
			  * 
			  */

			  // Navigating to Inventory menu 
			  //WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			  mouseHover("Rates.menu_Inventory");
			  click(Utils.element("Rates.menu_Inventory"));
			  System.out.println("Clicked Inventory menu"); 
			  logger.log(LogStatus.PASS, "Selected Inventory Menu");
			  waitForSpinnerToDisappear(20);
			
			  mouseHover("Rates.roomManagement");
			  waitForSpinnerToDisappear(10);
			  click(Utils.element("Rates.roomManagement"));
			  waitForSpinnerToDisappear(20);
			  logger.log(LogStatus.PASS,"Clicking on Room Management from Inventory Menu");
			  
			  jsClick("Rates.housekeepingBoard", 100, "clickable");
			  waitForSpinnerToDisappear(40);
			  logger.log(LogStatus.PASS,"Clicking On House Keeping Board");
			  
			//clear("Configuration.houseBoardingroomType");
			 textBox("Rates.houseBoardingroomType",configMap.get("ROOM_CATEGORY"), 100,
					  "presence");
			 Utils.tabKey("Rates.houseBoardingroomType");
			 waitForSpinnerToDisappear(20);
			 logger.log(LogStatus.PASS, "Provided Room TYpe - " + configMap.get("ROOM_CATEGORY"));
			 click(Utils.element("Rates.searchButton"));	  
			 waitForSpinnerToDisappear(40);
			 
			List<WebElement>  houseKeepingBoardTable = Utils.elements("Rates.houseBoardTable");
			int rowhouseKeepingBoardTable = houseKeepingBoardTable.size();
			System.out.println(rowhouseKeepingBoardTable);
			Map<String, Integer> houseKeepingMap = new HashMap<String, Integer>();
			
			//houseKeepingMap.put("rownumberavailable", houseKeepingBoardTable.size());
			
			  boolean flaginspected = Utils.isSelected("Rates.inspectedCheckBox","Inspected CheckBox is already selected");
			  if(flaginspected) {
			  click(Utils.element("Rates.inspectedCheckBox"));
			  waitForSpinnerToDisappear(10); 
			  }
			  boolean flagClean = Utils.isSelected("Rates.cleanCheckBox","Clean CheckBox is already selected");
			  if(flagClean) {
			  click(Utils.element("Rates.cleanCheckBox"));
			  waitForSpinnerToDisappear(10);
			  }
			  boolean flagPickUp = Utils.isSelected("Rates.pickupCheckBox","PickUp CheckBox is already selected");
			  if(flagPickUp) {
			  click(Utils.element("Rates.pickupCheckBox"));
			  waitForSpinnerToDisappear(10); 
			  } 
			  boolean flagDirty = Utils.isSelected("Rates.dirtyCheckBox", "Dirty CheckBox is already selected");
			  if(flagDirty) {
			  click(Utils.element("Rates.dirtyCheckBox"));
			  waitForSpinnerToDisappear(10); }
			
			 boolean flagoutoforder = Utils.isSelected("Rates.outoforderCheckBox", "OutOfOrder CheckBox is already selected");
			 if(!flagoutoforder) {
				 click(Utils.element("Rates.outoforderCheckBox"));
				 waitForSpinnerToDisappear(10);
			 }
			 boolean flagoutofservice = Utils.isSelected("Rates.outofserviceCheckBox", "OutOfService CheckBox is already selected");
			 if(!flagoutofservice) {
				 click(Utils.element("Rates.outofserviceCheckBox"));
				 waitForSpinnerToDisappear(10);
			 }
			 
			 click(Utils.element("Rates.searchButton"));	  
			 waitForSpinnerToDisappear(40);
			 
			 
			 	List<WebElement>  aftercheckhouseKeepingBoardTable = Utils.elements("Rates.houseBoardTable");
				int aftercheckrowhouseKeepingBoardTable = aftercheckhouseKeepingBoardTable.size();
				System.out.println(aftercheckrowhouseKeepingBoardTable);
				if(aftercheckhouseKeepingBoardTable.size()==0) {
				
					houseKeepingMap.put("rownumberoutOfOrder",0);
					houseKeepingMap.put("rownumberoutOfService",0);
				}
				else {
					houseKeepingMap.put("rownumberoutOfOrder",aftercheckhouseKeepingBoardTable.size());
					houseKeepingMap.put("rownumberoutOfService",aftercheckhouseKeepingBoardTable.size());
				}
				
				houseKeepingMap.put("rownumberavailable", propertyAvailabilityCountfromLTB());
			  // Navigating to Inventory menu 
			  //WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			  mouseHover("Rates.menu_Inventory");
			  click(Utils.element("Rates.menu_Inventory"));
			  System.out.println("Clicked Inventory menu"); 
			  logger.log(LogStatus.PASS, "Selected Inventory Menu");
			  waitForSpinnerToDisappear(20);
			
			  click(Utils.element("Rates.propertyAvailability"));
			  waitForSpinnerToDisappear(20);
			  logger.log(LogStatus.PASS, "Clicked on Property Availabilty");
			  
			  textBox("Rates.txt_SearchRoomTypes_RoomType",configMap.get("ROOM_CATEGORY"), 100,
					  "presence");
			 Utils.tabKey("Rates.txt_SearchRoomTypes_RoomType");
			 waitForSpinnerToDisappear(20);
			 logger.log(LogStatus.PASS, "Provided Room TYpe - " + configMap.get("ROOM_CATEGORY"));
			 click(Utils.element("Rates.searchButton"));	  
			 waitForSpinnerToDisappear(40);
			 
			// if(Utils.getTextValue("Configuration.roomType").equals(arg0))

			 if(getText("Rates.roomType",0, "visible").equals(configMap.get("ROOM_CATEGORY"))){
				 Assert.assertEquals(getText("Rates.roomType", 100, "presence"), "STDK");
				 logger.log(LogStatus.PASS, " Room type is present in below table:::"+configMap.get("ROOM_CATEGORY"));
				 
			 
			 click(Utils.element("Rates.linkViewOptions"));	  
			 waitForSpinnerToDisappear(40);
			 
			boolean flagOutOfOrder = Utils.isSelected("Rates.outOfOrderRooms", "Out of Order is already selected");
			if(!flagOutOfOrder) {
				click(Utils.element("Rates.outOfOrderRooms"));
				waitForSpinnerToDisappear(10);
			}
			boolean flagOutOfService = Utils.isSelected("Rates.outOfServiceRooms", "Out of Service is already selected");
			 if(!flagOutOfService) {
				 click(Utils.element("Rates.outOfServiceRooms"));
				 waitForSpinnerToDisappear(10);
			 }
			 
			 click(Utils.element("Rates.okButton"));	
			 waitForSpinnerToDisappear(40);
			 
			String ele = getText("Rates.newOrder", 100, "presence");
			System.out.println(ele);
			
			String rownumberavailable = getText("Rates.availableRoomCount", 100, "presence");
			System.out.println(rownumberavailable);
			
			String rownumberoutOfOrder = getText("Rates.outOfOrderRoomCount", 100, "presence");
			System.out.println(rownumberoutOfOrder);
			
			String rownumberoutOfService = getAttributeOfElement("Rates.outOfServiceRoomCount", "innerHTML", 100, "presence");
			System.out.println(rownumberoutOfService);
			
			String rownumberoutOfService2=getAttributeOfElement("Rates.outOfServiceRoomCount", "value", 100, "presence");
			
			String row3 = driver.findElement(By.xpath("(//*[text()=\"Out of Service Rooms\"]//following::span/div/div[2]/span)[1]")).getAttribute("innerHTML");

			
			String rownumberoutOfService3=getAttributeOfElement("Rates.outOfServiceRoomCount", "innerHTML", 100, "presence");
			
			//getAttributeOfElement("Configuration.outOfServiceRoomCount", attribute, timeout, option)
			
			//List ele2 = Utils.getAllValuesFromTableBasedOnColumnName("Configuration.columnName");
			
			List<WebElement> ele3 = Utils.elements("Rates.outOfServiceRoomCount");
		
			System.out.println(ele3.get(0).getText());
			
			Map<String,Integer> inventoryMap = new HashMap<String, Integer>();
			inventoryMap.put("rownumberavailable", Integer.parseInt(rownumberavailable));   
			//int roomAvailabilityCount =  propertyAvailabilityCountfromLTB();
			//inventoryMap.put("rownumberavailable", propertyAvailabilityCountfromLTB()); 
			inventoryMap.put("rownumberoutOfOrder", Integer.parseInt(rownumberoutOfOrder));
			inventoryMap.put("rownumberoutOfService", Integer.parseInt(rownumberoutOfService3));
			
			System.out.println(inventoryMap);
			
			
			//*[@data-ocid='SDH1']//*[text()='Available Rooms']
			 Assert.assertEquals(getText("Rates.availableRooms", 100, "presence"), "Available Rooms");
			 logger.log(LogStatus.PASS, "Rooms are Avaialable in table");
			 Assert.assertEquals(getText("Rates.rowoutofService", 100, "presence"), "Out of Service Rooms");
			 logger.log(LogStatus.PASS, "Out of Service Rooms are available");
			 Assert.assertEquals(getText("Rates.rowoutofOrder", 100, "presence"), "Out of Order Rooms");
			 logger.log(LogStatus.PASS, "Out of Order Rooms are available");
			 
			 
			
				
				for (Map.Entry<String, Integer> entry1 : inventoryMap.entrySet()) {
					  String key = entry1.getKey();
					  int value1 = entry1.getValue();
					  int value2 =  houseKeepingMap.get(key); 
					  if(value1==value2) {
						  System.out.println("Number of"+key+"Rooms"+"are validated sucessfully Count is " + value1+","+value2);
						  logger.log(LogStatus.PASS, "Number of"+key+"Rooms"+"are validated sucessfully Count is " + value1+","+value2);
					  }
					  else {
						  System.out.println("Number of"+key+"Rooms"+"are not validated sucessfully Count is " + value1+","+value2);
						  logger.log(LogStatus.FAIL, "Number of"+key+"Rooms"+"are not validated sucessfully Count is " + value1+","+value2);
					  }
					}
				
			 }
			 else {
				 logger.log(LogStatus.PASS, "ROOM TYPE IS NOT PRESENT" +configMap.get("Room_Types"));
			 }
			  
			
		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			// Utils.takeScreenshot(driver, testClassName);
			throw (e);
		}
	}


	/*************************************************************************************************************
    - Description: This method is to return property availability number from LTB Screen
	- Input:Profile Name , communication type , communication value, address , preference group , preference code
	- Output:
	- Swati
	- Date: 1/06/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 *****************************************************************************************************************/



	public static int propertyAvailabilityCountfromLTB() throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		

		int count;
		try {
			
			
			
			  Utils.takeScreenshot(driver, scriptName);
			  
			  // Navigating to Booking menu
			  Utils.WebdriverWait(100, "Reservation.booking_menu", "clickable");
			  Utils.mouseHover("Reservation.booking_menu");
			  Utils.click(Utils.element("Reservation.booking_menu"));
			  System.out.println("clicked booking"); logger.log(LogStatus.PASS,
			  "Selected Bookings Menu");
			  
			  // Navigating to Reservations menu
			  Utils.WebdriverWait(100,"Reservation.reservation_menu", "clickable");
			  Utils.click(Utils.element("Reservation.reservation_menu"));
			  System.out.println("clicked reservation"); logger.log(LogStatus.PASS,
			  "Selected Reservations Menu");
			  
			  // Navigating to LTB menu 
			  Utils.WebdriverWait(100,"Reservation.LookToBook_Menu", "clickable");
			  Utils.jsClick("Reservation.LookToBook_Menu");
			  System.out.println("clicked LTB"); logger.log(LogStatus.PASS,
			  "Selected LTB Menu");
			  

				click("Rates.RoomRateOptionsTab");
				waitForSpinnerToDisappear(50);
				
				textBox("Rates.RateCode.txtRoomTypes", "STDK");
				Utils.tabKey("Rates.RateCode.txtRoomTypes");
			 
				click(Utils.element("Rates.btn_Search"));	
				 waitForSpinnerToDisappear(40);

				 String stdkcount = Utils.getText("Rates.roomtypevalue");
				 count =  Integer.parseInt(stdkcount);

			
		} 
		catch(AssertionError e ) {
			throw new Exception(e);


		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding memberships to Profile   :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Adding memberships to Profile   :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
		return count;
	}
	public static void inventoryManageRestrictions(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + configMap);

		String ClientId2 = "";
		String communicationValue = "";
		List<WebElement>  aftercheckhouseKeepingBoardTable;

		try {


			// Navigating to Inventory menu 
			//WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			mouseHover("Rates.menu_Inventory");
			click(Utils.element("Rates.menu_Inventory"));
			System.out.println("Clicked Inventory menu"); 
			logger.log(LogStatus.PASS, "Selected Inventory Menu");
			waitForSpinnerToDisappear(20);

			mouseHover("Rates.manageRestrictions");
			waitForSpinnerToDisappear(10);
			click(Utils.element("Rates.manageRestrictions"));
			waitForSpinnerToDisappear(20);
			logger.log(LogStatus.PASS,"Clicking on Manage Restrictions from Inventory Menu");

			Utils.getBusinessDate();
			Map<String,String> envMap = new HashMap<String,String>(); 
			envMap = ExcelUtils.getDataByRow(OR.getConfig("Path_EnvironmentDetails"),
					"Configuration","BusinessDate");
			String envname = ExcelUtils.getCellData(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "Set", "VALUE");
			String businessDate = ExcelUtils.getCellData(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "BusinessDate",envname);
			String endDate = AdddaysToBusinessdate(1);
			textBox("Rates.startDate", businessDate);
			Utils.tabKey("Rates.startDate");
			waitForSpinnerToDisappear(50);
			
			String modifiedBuisnessDate = businessDate.replace("-", "/");
		
			click(Utils.element("Rates.rateCodesearchboxbutton"));       
			waitForSpinnerToDisappear(100);

			if(Utils.isExists("Rates.rateCodeCalendar")) {

			
				List rateCode = Utils.elements("Rates.RateCode");

				if(rateCode.size()>0) 
				{
					//Clicking the begin date for rate restriction

					//driver.findElement(By.xpath("//*[@_adfdycl[contains(.,"+"\'"+modifiedBuisnessDate+"\'"+")]]")).click();
					
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//*[@_adfdycl[contains(.,"+"\'"+modifiedBuisnessDate+"\'"+")]]")));
					
					
					//Utils.element("//*[@_adfdycl[contains(.,"+"\'"+modifiedBuisnessDate+"\'"+")]]").click();
					waitForSpinnerToDisappear(100);

					List rateCodes = driver.findElements(By.xpath("//*[@id[contains(.,'CLOSED')]]/span[contains(.,'"+configMap.get("RATE_CODE")+"')]"));
					
					if(rateCodes.size()>0)
					{
						logger.log(LogStatus.INFO, "Rate Code is displaying correctly - " + configMap.get("RATE_CODE"));

						
						String NoOfdays = driver.findElement(By.xpath("(//*[@id[contains(.,'CLOSED')]]/span[contains(.,'"+configMap.get("RATE_CODE")+"')]/span[contains(.,'Days')])[1]")).getAttribute("innerHTML");

						System.out.println("" + "(//*[@id[contains(.,'CLOSED')]]/span[contains(.,'"+configMap.get("RATE_CODE")+"')]/span[contains(.,'Days')])[1]");
						System.out.println("Noofdays" + NoOfdays);

						String NoOfdaysF= NoOfdays.replaceAll("[^0-9]", "");

						System.out.println("" + NoOfdaysF);

						if(NoOfdaysF.equals("2")){

							logger.log(LogStatus.PASS, "Restricted rate code is present for correct no of days" + "Correct No of dates -> " + "2" + "No of days from inventory -> " + NoOfdaysF);

						}
						else{
							logger.log(LogStatus.FAIL, "Restricted rate code is not present for correct no of days" + "Correct No of dates -> " + "2" + "No of days from inventory -> " + NoOfdaysF);
						}
					}
				
				else{

					logger.log(LogStatus.FAIL, "Restricted rate code is not present in the calender" + configMap.get("RATE_CODE"));

				}

			
			
			if(Utils.isExists("Rates.ActiveDate"))
			{
				String StartDate = Utils.getAttributeOfElement("Rates.ActiveDate", "_adfdycl", 100, "presence");

				if(StartDate.equals(modifiedBuisnessDate)){

					logger.log(LogStatus.PASS, "Begin Date for Restricted rate code is correctly populated" + "Actual begin date-> " + businessDate + " begin date from calender->  " + StartDate);
				}
				else{
					logger.log(LogStatus.FAIL, "Begin Date for Restricted rate code is not correctly populated" + "Actual begin date-> " + businessDate + " begin date from calender->  " + StartDate);
				}

			}

			else{
				logger.log(LogStatus.FAIL, "Active date is not present");       
			}

		
				}
		else{

			logger.log(LogStatus.FAIL, "Restricted rate code is not present in the calender");
		}

			}

	else{

		logger.log(LogStatus.FAIL, "Calender is not visible");
	}      

		
	}	catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			// Utils.takeScreenshot(driver, testClassName);
			throw (e);
		}

	}

}
