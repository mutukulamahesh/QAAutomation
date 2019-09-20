package com.oracle.hgbu.opera.qaauto.ui.fof.component.checkin;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.oracle.hgbu.opera.qaauto.ui.rsv.component.reservation.ReservationPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class CheckinPage extends Utils{
	
	
	/*******************************************************************
	-  Description: This method helps us to Check in a reservation whose confirmation number is passed as argument
	- Input: confirmation number
	- Output: Checks in the reservation 
	- Author: Anil Pentam
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	public static void checkInReservation(String ConfNum) throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {			
			Utils.takeScreenshot(driver, methodName);
			ReservationPage.reservationSearch(ConfNum);

			// Select IWanTo
			Thread.sleep(3000);
			Utils.WebdriverWait(100, "Reservation.Search_Reservation_IwantTo", "presence");
			Utils.mouseHover("Reservation.Search_Reservation_IwantTo");
			Utils.jsClick("Reservation.Search_Reservation_IwantTo");
			System.out.println("Selected I Want to in manage profile..");
			logger.log(LogStatus.PASS, "Selected I Want to Link in Search Profile Page");
			Utils.waitForSpinnerToDisappear(10);

			// Selecting go to res link in I want to link
			Utils.WebdriverWait(100, "Checkin.link_Rsv_CheckIn", "clickable");
			Utils.jsClick("Checkin.link_Rsv_CheckIn");
			System.out.println("Selected CheckIn..");
			logger.log(LogStatus.PASS, "Selected CheckIn Link in Res Page");	
			Utils.Wait(5000);
			Utils.waitForSpinnerToDisappear(20);

			// Validating Reservation Page			
			Utils.WebdriverWait(30, "Checkin.txt_ReservationCheckIn_PageValidation", "presence");
			Utils.verifyCurrentPage("Checkin.txt_ReservationCheckIn_PageValidation", "checkInPage_validation");
			logger.log(LogStatus.PASS, "Landed in CheckIn page and Validated");
			System.out.println("Landed in CheckIn page");			

			// Selecting Clean checkbox
			Utils.WebdriverWait(30, "Checkin.checkbox_ReservationCheckIn_Clean", "clickable");
			if (!isSelected("Checkin.checkbox_ReservationCheckIn_Clean", "Clean CheckBox")) {
				Utils.jsClick("Checkin.checkbox_ReservationCheckIn_Clean");
			} else {
				System.out.println("Clean CheckBox is Selected");
			}
			logger.log(LogStatus.PASS, "Selecting Clean checkbox");
			Utils.waitForSpinnerToDisappear(10);
			Thread.sleep(3000);	

			// Clicking on Search button
			Utils.WebdriverWait(50, "Checkin.btn_ReservationCheckIn_Search", "clickable");
			Utils.click("Checkin.btn_ReservationCheckIn_Search");
			logger.log(LogStatus.PASS, "Clicking on Search button");
			System.out.println("Clicking on Search button");			
			Utils.waitForSpinnerToDisappear(20);
			
			if(Utils.isExists("Checkin.link_ReservationCheckIn_SelectRoom")) {
				// Clicking on Select Room link
				Utils.WebdriverWait(50, "Checkin.link_ReservationCheckIn_SelectRoom", "clickable");
				Utils.jsClick("Checkin.link_ReservationCheckIn_SelectRoom");
				logger.log(LogStatus.PASS, "Clicking on Select Room link");
				System.out.println("Clicking on Select Room link");
				Utils.waitForSpinnerToDisappear(10);
			}else {
				//scroll("up");
				Utils.jsClick("Checkin.link_RoomTypeSearchIcon");
				Utils.waitForSpinnerToDisappear(10);
				Utils.click("Checkin.link_SelectAllLink", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
				Utils.click("Checkin.btn_RoomTypeSelect", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
				Utils.Wait(4000);
				Utils.click("Checkin.SearchButton");
				Utils.waitForSpinnerToDisappear(10);
				
				Utils.click("Checkin.link_ReservationCheckIn_SelectRoom", 100, "clickable");
				Utils.waitForSpinnerToDisappear(20);
				if(Utils.isExists("Checkin.checkbox_UpdateRoomType")) {
					Utils.click("Checkin.checkbox_UpdateRoomType", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					Utils.click("Checkin.btn_AssignRoom", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
				}
			}

			

			waitForPageLoad(100); // Reservation.btn_ReservationCheckIn_AcceptRoomSelection
			// Clicking on Accept roomselection Button
			if (isExists("Checkin.btn_ReservationCheckIn_AcceptRoomSelection")) {
				Utils.WebdriverWait(30, "Checkin.btn_ReservationCheckIn_AcceptRoomSelection", "clickable");
				Utils.jsClick("Checkin.btn_ReservationCheckIn_AcceptRoomSelection");
				System.out.println("Selected Accept Room Selection..");
				logger.log(LogStatus.PASS, "Selected Accept Room Selection Button ");
			}

			Utils.Wait(4000);
			Utils.waitForSpinnerToDisappear(10);

			waitForPageLoad(100);
			// Clicking on Complete CheckIn button
			Utils.WebdriverWait(100, "Checkin.btn_ReservationCheckIn_CompleteCheckin", "clickable");
			scroll("down");
			mouseHover("Checkin.txt_ReservationCheckIn_PageValidation");
			mouseHover("Checkin.btn_ReservationCheckIn_CompleteCheckin");
			jsClick("Checkin.btn_ReservationCheckIn_CompleteCheckin");
			logger.log(LogStatus.PASS, "Clicking on Complete CheckIn button");
			
			Utils.Wait(5000);
			Utils.waitForSpinnerToDisappear(10);
			
			if(Utils.isExists("Checkin.popup_RegistrationCard")) {		
				Utils.Wait(3000);
				Utils.click("Checkin.link_PrintRegistrationNo", 100, "clickable");				
			}
			
			if(Utils.isExists("Checkin.popup_PrintRegistrationCard")) {		
				Utils.Wait(3000);
				Utils.click("Checkin.link_ClodePrintRegistrationCard", 100, "clickable");				
			}

			if(Utils.isExists("Checkin.txt_CheckInSuccessMessage")) {
				Utils.click("Checkin.btn_ReservationCheckIn_PopupOk", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
			}
			if(Utils.isExists("Checkin.popup_CreateRoomKeysWindow")) {
				Utils.Wait(3000);
				Utils.click("Checkin.link_CreateRoomKeysCancel", 100, "clickable");
				Utils.waitForSpinnerToDisappear(20);				
			}
			if(Utils.isExists("Checkin.btn_GoToReservation")) {
				Utils.Wait(3000);
				Utils.click("Checkin.btn_GoToReservation", 100, "clickable");	
				Utils.waitForSpinnerToDisappear(20);
			}

			if (getText("Checkin.txt_ReservationCheckIn_checkInstatus").contains("Checked In") || getText("Checkin.txt_ReservationCheckIn_checkInstatus").contains("Departure")) {
				logger.log(LogStatus.PASS, "Reservation Successfully CheckedIn");
			}else {
				logger.log(LogStatus.FAIL, "Reservation NOT CheckedIn properly");
			}
				
			Thread.sleep(3000);
			logger.log(LogStatus.PASS, "CheckIn Reservation :: Passed");

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
	
	/*******************************************************************
	-  Description: This method helps us to post charges to a reservation
	- Input: confirmation number
	- Output: Post charges to a reservation
	- Author: Anil Pentam
	- Date: 11/29/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
public static void postCharges(HashMap<String, String> chargesMap) throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {			
			Utils.takeScreenshot(driver, methodName);
			ReservationPage.reservationSearch(chargesMap.get("CONFIRMATION_NUMBER"));
			Utils.waitForSpinnerToDisappear(20);	
			Utils.click("Reservation.Search_Reservation_IwantTo", 100, "clickable");
			Utils.waitForSpinnerToDisappear(20);			
			Utils.click("Checkin.link_Billing", 100, "clickable");
			Utils.waitForSpinnerToDisappear(20);
			if(Utils.isExists("Checkin.txt_CashierLogin")) {
				Utils.textBox("Checkin.editbox_CashierLOV", OR.getConfig("CashierUserID"), 100, "presence");
				Utils.tabKey("Checkin.editbox_CashierLOV");		
				Utils.waitForSpinnerToDisappear(10);
				Utils.textBox("Checkin.editbox_CashierPassword", OR.getConfig("CashierPassword"), 100, "presence");
				Utils.Wait(3000);
				Utils.click("Checkin.btn_CashierLogin", 100, "presence");
				Utils.waitForSpinnerToDisappear(20);
			}
			if(Utils.isExists("Checkin.txt_BillingTitle")) {
				String strBalnceAmt = Utils.getText("Checkin.txt_BalanceAmount", 100, "presence");
				String strArr[] = strBalnceAmt.split("\\s+");
				double balance =0.00;
				if(strArr.length > 1)
					balance = Double.parseDouble(strArr[0]);
				else					
					balance = Double.parseDouble(strArr[0].substring(1));
				
				//double balance = Double.parseDouble(Utils.getText("Checkin.txt_BalanceAmount", 100, "presence").substring(1));
				Utils.click("Checkin.btn_PostCharge", 100, "clickable");
				Utils.waitForSpinnerToDisappear(20);
				if(Utils.isExists("Checkin.txt_ChargeInformation")) {
					Utils.click("Checkin.btn_PostChargeCode", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					if(chargesMap.get("CHARGE_CODE")!="") {
						Utils.textBox("Checkin.editbox_PostChargeSearch", chargesMap.get("CHARGE_CODE"), 100, "presence");
						Utils.click("Checkin.btn_PostChargeCodeSearch", 100, "clickable");
						Utils.waitForSpinnerToDisappear(20);
						if (Utils.isExists("Checkin.txt_NoResultsDisplayed")) {							
							logger.log(LogStatus.FAIL, "No Results are displayed for the code:"+ chargesMap.get("CHARGE_CODE"));
							System.out.println("No Results are displayed for the code:"+ chargesMap.get("CHARGE_CODE"));
							
						}else {
							List<WebElement>  resultRowCount = Utils.elements("Checkin.txt_PostChargeCodeResult");
							ListIterator<WebElement> itr = null;
							boolean recordFound = false;
							itr = resultRowCount.listIterator();					
							while(itr.hasNext()) {								
								WebElement record = itr.next();
								String code = record.getText().trim();
								System.out.println("Comparing with code:" + code);
								if(code.equalsIgnoreCase(chargesMap.get("CHARGE_CODE"))) {
									recordFound = true;	
									Utils.click(record);									
									Utils.click("Checkin.btn_Select", 100, "clickable");
									Utils.waitForSpinnerToDisappear(10);
									break;
								}				
							}
							if(!recordFound) {						
								logger.log(LogStatus.FAIL, "No Results are displayed for the code:"+ chargesMap.get("CHARGE_CODE"));
								System.out.println("No Results are displayed for the code:"+ chargesMap.get("CHARGE_CODE"));						
							}
						}
						
					}else {
						Utils.click("Checkin.btn_PostChargeFirstSearchResult", 100, "clickable");
						Utils.click("Checkin.btn_Select", 100, "clickable");
						Utils.waitForSpinnerToDisappear(20);
					}
					Utils.Wait(10000);
					Utils.textBox("Checkin.txt_Price", chargesMap.get("PRICE"), 100, "stale");
					Utils.tabKey("Checkin.txt_Price");
					Thread.sleep(5000);
					Utils.jsTextbox("Checkin.txt_Quantity", chargesMap.get("QUANTITY"));
					//("Checkin.txt_Quantity", chargesMap.get("QUANTITY"), 100, "stale");
					Utils.Wait(10000);					
					Utils.click("Checkin.btn_ApplyCharge", 100, "clickable");
					Utils.Wait(6000);
					if(chargesMap.get("GENERATES_EXCLUSIVE")!= null && chargesMap.get("GENERATES_EXCLUSIVE").equalsIgnoreCase("Y")) {						
						return;
					}
					if(Utils.isExists("Checkin.link_ClosePostCharges")) {
						Utils.click("Checkin.link_ClosePostCharges", 100, "clickable");
						//Utils.waitForSpinnerToDisappear(10);
						Thread.sleep(10000);
					}
					
					Thread.sleep(15000);
					
					String currentBalance = Utils.getText("Checkin.txt_BalanceAmount", 100, "presence").substring(1);
					System.out.println("Bal: " + currentBalance);
					if(currentBalance.contains(","))
						currentBalance = currentBalance.replace(",", "");
					
					double newBalance = Double.parseDouble(currentBalance);
					System.out.println("New bal: " + newBalance);
					
					int qty = Integer.parseInt(chargesMap.get("QUANTITY"));
					double price = Double.parseDouble(chargesMap.get("PRICE"));
					double finalBalance = price * qty;
					System.out.println("Final Bal: " + finalBalance);
					
					if(qty > 1) {
						if(newBalance == finalBalance) {
							logger.log(LogStatus.PASS, "Charges Posted Successfully");
							System.out.println("Charges Posted Successfully");
						} else {
							logger.log(LogStatus.FAIL, "Balance Amount is NOT correct");
							System.out.println("Balance Amount is NOT correct");
						}
					}
					else {
						if(newBalance == (balance+ Double.parseDouble(chargesMap.get("PRICE")))) {
							logger.log(LogStatus.PASS, "Charges Posted Successfully");
							System.out.println("Charges Posted Successfully");
						} else {
							logger.log(LogStatus.FAIL, "Balance Amount is NOT correct");
							System.out.println("Balance Amount is NOT correct");
						}
					}
				}else {
					logger.log(LogStatus.FAIL, "Post Charges window is NOT displayed");
					System.out.println("Post Charges window is NOT displayed");
				}
			}else {
				logger.log(LogStatus.FAIL, "Billing Screen is NOT displayed");
				System.out.println("Billing Screen is NOT displayed");
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
	
	/*******************************************************************
	-  Description: This method helps us to post deposit to a reservation
	- Input: Deposit Amount, Deposit Rule, Due Date
	- Output: Post deposit to a reservation
	- Author: Anil Pentam
	- Date: 07/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	public static void postDeposit(HashMap<String, String> depositMap) throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {
				if(Utils.isExists("Checkin.txt_DepositCancellationPopup")) {
					Utils.click("Checkin.link_New", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					Utils.textBox("Checkin.editbox_DepositRule", depositMap.get("DEPOSIT_RULE"), 100, "presence");
					Utils.tabKey("Checkin.editbox_DepositRule");
					Utils.waitForSpinnerToDisappear(10);
					if(Utils.isExists("Checkin.txt_RuleSelectionPopup")) {
						Utils.click("Checkin.link_RuleSelectionPopupClose", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
					}
					Utils.textBox("Checkin.editbox_DepositAmount", depositMap.get("DEPOSIT_AMOUNT"), 100, "presence");	
					Utils.tabKey("Checkin.editbox_DepositAmount");
					Utils.waitForSpinnerToDisappear(10);
					Utils.textBox("Checkin.editbox_DueDate", depositMap.get("DUE_DATE"), 100, "presence");
					Utils.tabKey("Checkin.editbox_DueDate");
					Utils.waitForSpinnerToDisappear(10);
					Utils.click("Checkin.btn_SaveDeposit", 100, "clickable");
					Utils.waitForSpinnerToDisappear(20);					
					List<WebElement>  depositRows = Utils.elements("Checkin.txt_depositPosted");
					int depositRowsCount = depositRows.size()-2;
					boolean recordFound = false;
					String amount="";
					for(int i=0; i<depositRowsCount; i++) {
						String code = driver.findElement(By.xpath("//td[@data-ocid='"+i+"_CDEP']")).getText();
						if(code.equals(depositMap.get("DEPOSIT_RULE"))) {
							recordFound = true;
							amount = driver.findElement(By.xpath("//td[@data-ocid='"+i+"_CAR']")).getText();
							break;
						}
					}
					
					if(recordFound) {
						if(depositMap.get("DEPOSIT_AMOUNT").equals(amount.substring(1))) {
							logger.log(LogStatus.PASS, "Deposit Posted successfully");
							System.out.println("Deposit Posted successfully");
						}else {
							logger.log(LogStatus.FAIL, "Deposit Amount has mismatch");
							System.out.println("Deposit Amount has mismatch");
						}
					}else {
						logger.log(LogStatus.FAIL, "Deposit Amount is NOT posted successfully");
						System.out.println("Deposit Amount is NOT posted successfully");
					}
					
					Utils.click("Checkin.btn_CloseDepositPopup", 100, "clickable");
					Utils.waitForSpinnerToDisappear(20);

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
	
	/*******************************************************************
	-  Description: This method helps us to post deposit to a reservation
	- Input: Deposit Amount, Deposit Rule, Due Date
	- Output: Post fixed charges to a reservation
	- Author: Anil Pentam
	- Date: 09/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	public static void postFixedCharges(HashMap<String, String> fixedChargesMap) throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {
				if(Utils.isExists("Checkin.popup_FixedCharges", 100, "presence")) {
					Utils.click("Checkin.link_NewFixedCharges", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					if(Utils.isExists("Checkin.popup_FixedCharges", 100, "presence")) {
						if(fixedChargesMap.get("FREQUENCY")!="") {
							Utils.selectBy("Checkin.select_Frequency", "text", fixedChargesMap.get("FREQUENCY"));
							Utils.waitForSpinnerToDisappear(10);
							//When frequency is selected as 'Weekly' Day to execute should be selected
							if(fixedChargesMap.get("FREQUENCY").equalsIgnoreCase("Weekly")) {
								Utils.textBox("Checkin.editbox_DayToExecute", fixedChargesMap.get("DAY_TO_EXECUTE"), 100, "presence");
								Utils.tabKey("Checkin.editbox_DayToExecute");
								Utils.waitForSpinnerToDisappear(10);
							}
							Utils.textBox("Checkin.editbox_FixedChargesBeginDate", fixedChargesMap.get("BEGIN_DATE"), 100, "presence");
							Utils.tabKey("Checkin.editbox_FixedChargesBeginDate");
							Utils.waitForSpinnerToDisappear(10);
							//When frequency is selected as 'Once' No End date is needed
							if(!(fixedChargesMap.get("FREQUENCY").equalsIgnoreCase("Once"))) {
								Utils.textBox("Checkin.editbox_FixedChargesEndDate", fixedChargesMap.get("END_DATE"), 100, "presence");
								Utils.tabKey("Checkin.editbox_FixedChargesEndDate");
								Utils.waitForSpinnerToDisappear(10);
							}
						}
						if(fixedChargesMap.get("AMOUNT_RADIO").equalsIgnoreCase("Y")) {
							Utils.click("Checkin.radio_Amount", 100, "clickable");
							Utils.waitForSpinnerToDisappear(10);
							
							Utils.textBox("Checkin.editbox_FixedChargeAmount", fixedChargesMap.get("AMOUNT"), 100, "presence");
							Utils.tabKey("Checkin.editbox_FixedChargeAmount");
							Utils.waitForSpinnerToDisappear(10);
						}else if(fixedChargesMap.get("PERCENTAGE_OF_ROOM_RATE_RADIO").equalsIgnoreCase("Y")) {
							Utils.click("Checkin.radio_PercentageOfRoomRate", 100, "clickable");
							Utils.waitForSpinnerToDisappear(10);
							
							Utils.textBox("Checkin.editbox_PercentageOfRoomRate", fixedChargesMap.get("PERCENTAGE_OF_ROOM_RATE"), 100, "presence");
							Utils.tabKey("Checkin.editbox_PercentageOfRoomRate");
							Utils.waitForSpinnerToDisappear(10);
						}else {
							System.out.println("NO proper data for amount radio selection is provided");
							logger.log(LogStatus.FAIL, "NO proper data for amount radio selection is provided");
						}
						Utils.textBox("Checkin.editbox_FixedChargeTransaction", fixedChargesMap.get("TRANSACTION"), 100, "presence");
						Utils.tabKey("Checkin.editbox_FixedChargeTransaction");
						Utils.waitForSpinnerToDisappear(10);
						String transactionDesc = Utils.getText("Checkin.txt_TransactionCode", 100, "presence");
						Utils.textBox("Checkin.editbox_FixedChargeQuantity", fixedChargesMap.get("QUANTITY"), 100, "presence");
						Utils.tabKey("Checkin.editbox_FixedChargeQuantity");
						Utils.waitForSpinnerToDisappear(10);
						Utils.click("Checkin.btn_FixedChargeSave", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						
						//validating posted fixed charges
						List<WebElement>  resultRowCount = Utils.elements("Checkin.txt_FixedChargesPosted");
						boolean flag = false;
						//iterating with all the fixed charges posted for the reservation
						for(int i=1;i<=resultRowCount.size();i++) {							
							String beginDate = driver.findElement(By.xpath("//*[@data-ocid='"+(i-1)+"_TXT_CNTNT_BEGINDATE']")).getText();
							String endDate = driver.findElement(By.xpath("//*[@data-ocid='"+(i-1)+"_TXT_CNTNT_ENDDATE']")).getText();
							String transactionDescValue = driver.findElement(By.xpath("(//*[contains(@data-ocid,'FLEM_TRANSACTIONDESC')]/span/span[2])["+i+"]")).getText();
							String freqValue = driver.findElement(By.xpath("(//*[contains(@data-ocid,'FLEM_FREQUENCY')]/span/span[2])["+i+"]")).getText();
							if(fixedChargesMap.get("AMOUNT_RADIO").equalsIgnoreCase("Y")) {
								// if only the amount value exist for the record
								List<WebElement> ele = driver.findElements(By.xpath("(//*[contains(@data-ocid,'FLEM_AMOUNT')]/span/span[2])["+i+"]"));
								String amountValue="";
								if(!ele.isEmpty()) {
									amountValue = driver.findElement(By.xpath("(//*[contains(@data-ocid,'FLEM_AMOUNT')]/span/span[2])["+i+"]")).getText().substring(1);
								}								
								if((beginDate.equals(fixedChargesMap.get("BEGIN_DATE")))&&(endDate.equals(fixedChargesMap.get("END_DATE")))&&transactionDescValue.equalsIgnoreCase(transactionDesc)
										&&(freqValue.equalsIgnoreCase(fixedChargesMap.get("FREQUENCY")))&&(amountValue.equals(fixedChargesMap.get("AMOUNT")))) {
									flag = true;
									driver.findElement(By.xpath("//*[@data-ocid='"+(i-1)+"_LINK_ACTIONS']")).click();
									Utils.waitForSpinnerToDisappear(10);
									Utils.click("Checkin.link_DeleteFixedCharges", 100, "clickable");
									Utils.waitForSpinnerToDisappear(10);
									Utils.click("Checkin.btn_DeleteFixedCharges", 100, "clickable");
									Utils.waitForSpinnerToDisappear(10);
									break;
								}
							}else if(fixedChargesMap.get("PERCENTAGE_OF_ROOM_RATE_RADIO").equalsIgnoreCase("Y")) {
								// if only the percentage value exist for the record
								List<WebElement> ele = driver.findElements(By.xpath("//*[@data-ocid='"+(i-1)+"_TXT_CNTNT_PERCENTAGEOFROOMRATE']"));
								String percentageRoomRate="";
								if(!ele.isEmpty()) {
									percentageRoomRate = driver.findElement(By.xpath("//*[@data-ocid='"+(i-1)+"_TXT_CNTNT_PERCENTAGEOFROOMRATE']")).getText();
									percentageRoomRate = percentageRoomRate.substring(0, percentageRoomRate.length()-1);
								}								
								
								if((beginDate.equals(fixedChargesMap.get("BEGIN_DATE")))&&(endDate.equals(fixedChargesMap.get("END_DATE")))&&transactionDescValue.equalsIgnoreCase(transactionDesc)
										&&(freqValue.equalsIgnoreCase(fixedChargesMap.get("FREQUENCY")))&&(percentageRoomRate.equals(fixedChargesMap.get("PERCENTAGE_OF_ROOM_RATE")))) {
									flag = true;
									driver.findElement(By.xpath("//*[@data-ocid='"+(i-1)+"_LINK_ACTIONS']")).click();
									Utils.waitForSpinnerToDisappear(10);
									Utils.click("Checkin.link_DeleteFixedCharges", 100, "clickable");
									Utils.waitForSpinnerToDisappear(10);
									Utils.click("Checkin.btn_DeleteFixedCharges", 100, "clickable");
									Utils.waitForSpinnerToDisappear(10);
									break;
								}
							}
							
						}											
						
						if(flag) {
							System.out.println("Fixed charges are posted successfully");
							logger.log(LogStatus.PASS, "Fixed charges are posted successfully");
						}else {
							System.out.println("Fixed charges are NOT posted properly");
							logger.log(LogStatus.FAIL, "Fixed charges are NOT posted properly");
						}
						
						Utils.click("Checkin.btn_CloseFixedChargesPopup", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
					}

				}else {
					System.out.println("Fixed charges popup is NOT displayed");
					logger.log(LogStatus.FAIL, "Fixed charges popup is NOT displayed");
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
	
	/*******************************************************************
	- Description: Verify user is able to create folio routing
	- Input: CONFIRMATION_NUMBER, WINDOW, TRANSACTION_CODES
	- Output: folio routing should be created
	- Author: @author vnadipal
	- Date: 14/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void createFolioRouting(HashMap<String, String> configMap) throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);	
	
		try {
			waitForSpinnerToDisappear(5);
			// Clicked on Bookings Menu
			jsClick("Checkin.Cashiering.menuBookings", 100, "presence");

			waitForSpinnerToDisappear(5);
			// Clicked on Reservations Menu Item
			jsClick("Checkin.Cashiering.menuReservations", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Manage Reservation Menu Item
			jsClick("Checkin.Cashiering.menuItemManageRservation", 100, "presence");
			
			waitForSpinnerToDisappear(10);
			textBox("Checkin.Cashiering.ManageReservation.txtConfirmationNumber", configMap.get("CONFIRMATION_NUMBER"));
			
			waitForSpinnerToDisappear(5);
			// Clicked on Search button
			jsClick("Checkin.Cashiering.ManageReservation.btnSearch", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on I Want To arrow
			jsClick("Checkin.Cashiering.RoomRouting.arrowIWantTo", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on View Table
			if(!element("Checkin.Cashiering.RoomRouting.linkViewTable").isEnabled())
				jsClick("Checkin.Cashiering.RoomRouting.linkViewTable", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Show All link
			jsClick("Checkin.Cashiering.FolioRouting.linkShowAll", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on Routing Instructions link
			jsClick("Checkin.Cashiering.RoomRouting.linkRoutingInstructions", 100, "presence");
			
			waitForSpinnerToDisappear(5);
			// Clicked on New link
			jsClick("Checkin.Cashiering.RoomRouting.linkNew", 100, "presence");
			
			// Entered Folio
			selectBy("Checkin.Cashiering.FolioRouting.dropdownFolio", "text", configMap.get("WINDOW"));
			logger.log(LogStatus.INFO, "Selected Folio as: " + configMap.get("WINDOW"));
			
			if(configMap.get("TRANSACTION_CODE").contains(",")) {
				String[] trxCodesList = configMap.get("TRANSACTION_CODE").split(",");
				
				for(String code : trxCodesList) {
					textBox("Checkin.Cashiering.RoomRouting.txtFilter", code);
					waitForSpinnerToDisappear(5);
					
					// Clicked on Go button
					jsClick("Checkin.Cashiering.RoomRouting.btnFilterGo", 100, "presence");
					waitForSpinnerToDisappear(5);
					
					// Clicked on first row
					jsClick("Checkin.Cashiering.RoomRouting.rowSelectRecord", 100, "presence");
					waitForSpinnerToDisappear(5);
								
					// Clicked on Add button
					jsClick("Checkin.Cashiering.RoomRouting.btnAdd", 100, "presence");
					waitForSpinnerToDisappear(10);
				}
			}
			else {
				Thread.sleep(5000);
				textBox("Checkin.Cashiering.RoomRouting.txtFilter", configMap.get("TRANSACTION_CODE"));
				waitForSpinnerToDisappear(5);
				
				// Clicked on Go button
				jsClick("Checkin.Cashiering.RoomRouting.btnFilterGo", 100, "presence");
				waitForSpinnerToDisappear(5);
				
				// Clicked on first row
				jsClick("Checkin.Cashiering.RoomRouting.rowSelectRecord", 100, "presence");
				waitForSpinnerToDisappear(5);
							
				// Clicked on Add button
				jsClick("Checkin.Cashiering.RoomRouting.btnAdd", 100, "presence");
				waitForSpinnerToDisappear(10);
			}
			
			// Clicked on Save button
			jsClick("Checkin.Cashiering.RoomRouting.btnSave", 100, "presence");
			
			waitForSpinnerToDisappear(15);
			System.out.println("text: " + getText("Checkin.Cashiering.FolioRouting.validationDiv"));
			
			if(configMap.get("TRANSACTION_CODE").contains(",")) {
				String[] trxCodesList = configMap.get("TRANSACTION_CODE").split(",");
				
				for(String code : trxCodesList) {
					if(getText("Checkin.Cashiering.FolioRouting.validationDiv").contains(code)) {
						logger.log(LogStatus.PASS, "Folio Routing for code " + code +" is successfully created");
					}
					else {
						logger.log(LogStatus.FAIL, "Failed to create Folio Routing for " + code);
					}
				}
			}
			else {
				if(getText("Checkin.Cashiering.FolioRouting.validationDiv").contains(configMap.get("TRANSACTION_CODE"))) {
					logger.log(LogStatus.PASS, "Folio Routing for code " + configMap.get("TRANSACTION_CODE") +" is successfully created");
				}
				else {
					logger.log(LogStatus.FAIL, "Failed to create Folio Routing for " + configMap.get("TRANSACTION_CODE"));
				}
			}
			
			Thread.sleep(5000);
			// Clicked on Close button
			jsClick("Checkin.Cashiering.RoomRouting.linkClose", 100, "presence");		
		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			Utils.tearDown();
		}
	}
	

	/*******************************************************************
	-  Description: This method helps us to transfer deposit to a reservation
	- Input: Deposit Amount, Transfer amount
	- Output: Transfer deposit to another reservation
	- Author: Chandan Chitiki
	- Date: 22/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	public static void transferDeposit(HashMap<String, String> transferMap) throws Exception {
		
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {
			HashMap<String, String> resvMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ReservationData"), "Reservation", "createReservation");			
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "PaymentTypes", "Dataset_1");
			
			String confirmationNum = ReservationPage.createReservationFromLTB(resvMap);	
			System.out.println("confirmationNumber : " + confirmationNum);
			Utils.waitForSpinnerToDisappear(10);
			String confirmationNum2 = ReservationPage.createReservationFromLTB(resvMap);	
			System.out.println("confirmationNumber : " + confirmationNum2);
			//String confirmationNum3 = ReservationPage.createReservationFromLTB(resvMap);	
			//String confirmationNum = "366982";
			//String confirmationNum2="369566"; 
			//ReservationPage.reservationAdvanceSearch(confirmationNum);
			
			System.out.println("confirmationNumber : " + confirmationNum +"  and  " + "confirmationNumber2 : " +confirmationNum2);
			
			click("Checkin.FrontDesk_menu",100,"clickable");
			// Navigating to Arrivals Menu
			click("Checkin.Arrivals_menu",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			Utils.textBox("Checkin.DepositPayment.confirmationNumber",confirmationNum, 100, "presence");
			Utils.waitForSpinnerToDisappear(10);
			
			click("Checkin.btn_Search",100,"clickable");	
			Utils.waitForSpinnerToDisappear(10);
			
			//driver.findElement(By.xpath("//*[contains(@data-ocid,'LINK_"+confirmationNum+"']")).click();
			if (Utils.isExists("Checkin.link_DepositCancellation")) 
			{
				click("Checkin.link_DepositCancellation",100,"clickable");
				Utils.waitForSpinnerToDisappear(20);
			}
			else
			{
				
			click("Checkin.link_FirstIwantTo",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_ShowAll",100,"clickable");
			Utils.waitForSpinnerToDisappear(10);
			click("Checkin.link_DepositCancellation",100,"clickable");
			Utils.waitForSpinnerToDisappear(20);
			}
			
			
			//HashMap<String, String> depositMap = ExcelUtils.getDataByRow(OR.getConfig("Path_CheckinData"), "PostDeposit", "postDeposit");
			
			//CheckinPage.postDeposit(depositMap);	-----------------
				if(Utils.isExists("Checkin.txt_DepositCancellationPopup")) {
					Utils.click("Checkin.DepositCancel.morelink", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					Utils.click("Checkin.DepositCancel.postUnalloctdDeposit", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					new Actions(driver).moveToElement(Utils.element("Reservation.PaymentInstructions_MethodLov")).perform();
					Utils.selectBy("Reservation.PaymentInstructions_MethodLov", "value",configMap.get("CODE"));
					Utils.waitForSpinnerToDisappear(10);
					Utils.click("Checkin.DepositPayment.changePaymentCheckbox", 100, "clickable");
					Utils.waitForSpinnerToDisappear(10);
					System.out.println("Check box unselected");
					Utils.textBox("Checkin.DepositPayment.paymentAmount", transferMap.get("PAYMENT_AMOUNT"), 100, "presence");
					Utils.waitForSpinnerToDisappear(10);
					Utils.click("Checkin.DepositPayment.postPaymentBtn",100, "clickable");
					System.out.println("Post payment button clicked");
					Utils.waitForSpinnerToDisappear(10);
					if(Utils.isExists("Checkin.DepositPayment.paymentTable")) {
						System.out.println("Deposit Payment Sucessfull");

						Utils.click("Checkin.DepositPayment.closePaymentPopup",100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						Utils.isExists("Checkin.txt_DepositCancellationPopup");
						System.out.println("Navigated back to deposit cancellation screen ");					
						Utils.click("Checkin.DepositCancel.morelink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						Utils.click("Checkin.DepositPayment.transferDepositLink", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						Utils.click("Checkin.DepositPayment.roomLovSearch", 100, "clickable");
						Utils.waitForSpinnerToDisappear(10);
						if(Utils.isExists("Checkin.DepositPayment.searchPopup"))
						{ 
							Utils.waitForSpinnerToDisappear(10);
							
							if (isExists("Checkin.DepositPayment.searchPopup.frame")) {
							driver.switchTo().frame(Utils.element("Checkin.DepositPayment.searchPopup.frame"));
							Utils.waitForSpinnerToDisappear(10);
							}
							Utils.waitForSpinnerToDisappear(10);
							Utils.textBox("Checkin.DepositPayment.confirmationNumber",confirmationNum2, 100, "presence");
							Utils.waitForSpinnerToDisappear(10);
							Utils.click("Checkin.DepositPayment.searchpopup.searchbutton", 100, "clickable");
							Utils.waitForSpinnerToDisappear(15);
							
							if (Utils.isExists("Checkin.DepositPayment.searchpopup.selectButton"))
							{
								Utils.click("Checkin.DepositPayment.searchpopup.selectButton", 100, "clickable");
								Utils.waitForSpinnerToDisappear(10);
								driver.switchTo().defaultContent();
								Utils.isExists("Checkin.DepositPayment.transferDepositPopup");
								System.out.println("Navigated back to deposit transfer screen ");
								
								if (transferMap.get("Deposit").contains("N")) 
								{
									System.out.println("Deposit transfer Type");
									Utils.textBox("Checkin.DepositPayment.transferAmountfield",transferMap.get("TRANSFER_AMOUNT"), 100, "presence");
									Utils.click("Checkin.DepositPayment.transferBtn", 100, "clickable");
									Utils.waitForSpinnerToDisappear(15);
									Utils.click("Checkin.DepositPayment.transferTo.expand", 100, "clickable");
									Utils.waitForSpinnerToDisappear(10);
									String verifyTransfer = Utils.getText("Checkin.DepositPayment.transferTo.comments");
										if (verifyTransfer.contains(transferMap.get("TRANSFER_AMOUNT")))
											
										{
											logger.log(LogStatus.PASS, "Deposit transfer is successfull ");
											System.out.println("Deposit transfer for Amount :" + transferMap.get("TRANSFER_AMOUNT") +" is successfull"  );
											
										}
										
										else{
											logger.log(LogStatus.FAIL, "Deposit transfer is unsuccessfull");
											System.out.println("Deposit transfer is unsuccessfull");
											}
								}
								if (transferMap.get("Deposit").contains("Y")) 
								{
									System.out.println("Deposit distribute and transfer Type");
									Utils.click("Checkin.DepositPayment.depositTransferBtn", 100, "clickable");
									Utils.waitForSpinnerToDisappear(10);									
									String verifyDepositTransferField = Utils.getAttributeOfElement("Checkin.DepositPayment.transferAmountfield", "value", 100, "presence");
									System.out.println("Deposit distribute field got value as : " + verifyDepositTransferField );
										
										if (verifyDepositTransferField.contains(transferMap.get("PAYMENT_AMOUNT")))
										{								
											Utils.click("Checkin.DepositPayment.transferBtn", 100, "clickable");
											Utils.waitForSpinnerToDisappear(15);
											Thread.sleep(5000);
												if (Utils.isExists("Checkin.DepositPayment.transferTo.expand"))
												{
													System.out.println("Transfer to expand button clicked");
													Utils.click("Checkin.DepositPayment.transferTo.expand", 100, "clickable");
												}
												Utils.waitForSpinnerToDisappear(10);
												if (Utils.isExists("Checkin.DepositPayment.expandButton"))
												{
													System.out.println("Deposit Payment expand button clicked");
													Utils.click("Checkin.DepositPayment.expandButton", 100, "clickable");
												}
												String verifyDepositTransfer = Utils.getText("Checkin.DepositPayment.transferTo.comments");
												if (verifyDepositTransfer.contains(transferMap.get("PAYMENT_AMOUNT")))
												{
													logger.log(LogStatus.PASS, "Deposit distribute transfer is successfull ");
													System.out.println("Deposit distribute transfer for Amount :" + transferMap.get("PAYMENT_AMOUNT") +" is successfull"  );
												}
												else{
													logger.log(LogStatus.FAIL, "Deposit distribute transfer is unsuccessfull");
													System.out.println("Deposit distribute transfer is unsuccessfull");
												}
										}
										else
										{
											logger.log(LogStatus.FAIL, "Deposit distribute transfer Field has different value");
											System.out.println("Deposit distribute transfer Field has different value");
										}
											}
								}
							}					
							else{
								logger.log(LogStatus.FAIL, "Reservation not found");
								System.out.println("Reservation not found");
								}
						}
					
						else{
							logger.log(LogStatus.FAIL, "Search box not loaded");
							System.out.println("Search box not loaded");
							}
					}
					else{
						logger.log(LogStatus.FAIL, "Deposit Payment Unsucessfull");
						System.out.println("Deposit Payment Unsucessfull");
					
						
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
	
	/*******************************************************************
	-  Description: This method helps us to generates for a transaction code applied
	- Input: GENERATE_CODE, TAX_TYPE, NON_TAX_TYPE, PERCENTAGE_RADIO, PERCENTAGE_AMOUNT, CALCULATION_ON, AMOUNT_VALUE
	- Output: Calculates the generates of a transaction applied
	- Author: Anil Pentam
	- Date: 22/01/19
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	public static void calculateGenerates(HashMap<String, String> generatesMap, String amount) throws Exception {
		String methodName = Utils.getMethodName();
		String testName = Utils.getClassName() + "@" + Utils.getMethodName();
		System.out.println("methodName: " + methodName);

		try {			
			Utils.takeScreenshot(driver, methodName);			
			String taxAmount = Utils.getText("Checkin.Generates_TaxAmount", 100, "presence");
			double tax = 0.00;
			if(generatesMap.get("PERCENTAGE_RADIO").equalsIgnoreCase("Y")) {
				tax = (Double.parseDouble(generatesMap.get("PERCENTAGE_AMOUNT"))*(Double.parseDouble(amount)))/100;
			}else if(generatesMap.get("AMOUNT_RADIO").equalsIgnoreCase("Y")) {
				tax = Double.parseDouble(generatesMap.get("AMOUNT_VALUE"));
			}
			if(tax == Double.parseDouble((taxAmount.trim()).substring(1))) {
				System.out.println("Generates are displayed and calculated properly");
				logger.log(LogStatus.PASS, "Generates are displayed and calculated properly");
			}else {
				System.out.println("Generates are NOT calculated properly");
				logger.log(LogStatus.FAIL, "Generates are NOT calculated properly");
			}
			if(Utils.isExists("Checkin.link_ClosePostCharges")) {
				Utils.click("Checkin.link_ClosePostCharges", 100, "clickable");
				Utils.waitForSpinnerToDisappear(10);
			}
				
		}catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);			
			Utils.tearDown();
		}
	}
}
