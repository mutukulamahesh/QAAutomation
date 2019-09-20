package com.oracle.hgbu.opera.qaauto.ui.crm.component.profile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.sikuli.script.Screen;
import org.testng.Assert;


import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class ProfilePage extends Utils {

	static Map<String, String> profileMap = new HashMap<String, String>();

	public static void testData()
	{

	}


	/**************************************************************************************************************************************
    - Description: To create a Guest profile using Save and Save and add more details option
	- Input:Profile Name , communication type , communication value, address, Option can be provided with two values . 
		Save to use the save button and SaveAdd to use the save and add more details option
	- Output: 
	- Author:Swatij
	- Date: 12/19/2018
	- Revision History:
	                - Change Date - 1/18/2019
	                - Change Reason - The method is made dynamic to accept all the values that can be provided to create a guest profile, 
	                				  because test cases require different values in the profile
	                - Changed Behavior - The method is made dynamic to accept all the values that can be provided to create a guest profile, 
	                				  according to the data provided in the excel sheet the method add the details for the profile
	                - Last Changed By - Swatij
	 *****************************************************************************************************************************************/

	public static void createGuestProfile(HashMap<String, String> profileMap,String option) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + profileMap);

		String ClientID="";
		String communicationValue="";

		String name = "";
		String FirstName = "";
		String Title = "";
		String Address1 = "";
		String Address2 = "";
		String Address3 = "";
		String Address4 = "";
		String Address5 = "";
		String Country = "";
		String City = "";
		String State = "";
		String PostalCode = "";
		String PostalCodeExt = "";
		String newT = "";

		try {

			// Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			// Click I Want To link 
			click("Profile.link_ManageProfiles_IWantTo", 100, "clickable");

			// Click Guest profile link
			click("Profile.create_guest_profile", 100, "clickable");

			waitForSpinnerToDisappear(50);
		
			// Entering the Name
			//Utils.WebdriverWait(100, "Profile.txt_ManageProfiles_Name", "stale");
			textBox("Profile.txt_ManageProfiles_Name",profileMap.get("Name"), 100, "presence");
			Utils.tabKey("Profile.txt_ManageProfiles_Name");
			waitForSpinnerToDisappear(30);

			//Providing the first name if present in the dataset
			if(profileMap.containsKey("FirstName")){

				textBox("Profile.ContactProfile.FirstName",profileMap.get("FirstName"), 100, "presence");
				Utils.tabKey("Profile.ContactProfile.FirstName");
				waitForSpinnerToDisappear(30);
			}

			//Providing the Middle Name if present in the dataset
			if(profileMap.containsKey("MiddleName")){

				textBox("Profile.ContactProfile.MiddleName",profileMap.get("MiddleName"), 100, "presence");
				Utils.tabKey("Profile.ContactProfile.MiddleName");
				waitForSpinnerToDisappear(30);
			}

			//Providing the Title if present in the dataset
			if(profileMap.containsKey("Title")){

				textBox("Profile.ContactProfile.Title",profileMap.get("Title"), 100, "presence");
				Utils.tabKey("Profile.ContactProfile.Title");
				waitForSpinnerToDisappear(30);
			}

			//Providing the Language if present in the dataset
			if(profileMap.containsKey("Language")){

				textBox("Profile.CompanyProfile.Language",profileMap.get("Language"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.Language");
				waitForSpinnerToDisappear(30);
			}

			waitForSpinnerToDisappear(50);

			// Clear the default value from Type
			//clear("Profile.txt_ManageProfiles_Type");

			// Entering the communication Type
			System.out.println("text" + Utils.getText("Profile.CompanyProfile.CommunicationType"));
			
			if(Utils.getText("Profile.CompanyProfile.CommunicationType").equals("")){
				Utils.element("Profile.CompanyProfile.CommunicationType").click();
				Utils.textBox("Profile.CompanyProfile.CommunicationType", profileMap.get("Type"));
			}
			
			Utils.tabKey("Profile.CompanyProfile.CommunicationType");
			waitForSpinnerToDisappear(50);
			
			// Entering the Communication Value
			communicationValue=profileMap.get("CommunicationValue");
			jsTextbox("Profile.txt_ManageProfiles_CommunicationValue",communicationValue);
			Utils.tabKey("Profile.txt_ManageProfiles_CommunicationValue");
			waitForSpinnerToDisappear(50);

			//textBox("Profile.txt_ManageProfiles_Type", profileMap.get("Type"), 0, "clickable");
			//Utils.WebdriverWait(150, "Profile.txt_ManageProfiles_Type", "stale");
			//jsTextbox("Profile.txt_ManageProfiles_Type",profileMap.get("Type"));
			//Utils.tabKey("Profile.txt_ManageProfiles_Type");
			//waitForSpinnerToDisappear(100);

			
			// Clear the default value from Address Type
			//clear("Profile.txt_ManageProfiles_Type");

			if(getText("Profile.CompanyProfile.AddressType",0, "clickable").equals("")){
				Utils.element("Profile.CompanyProfile.AddressType").click();
				Utils.textBox("Profile.CompanyProfile.AddressType", profileMap.get("Type"));
			}
			Utils.tabKey("Profile.CompanyProfile.AddressType");
			waitForSpinnerToDisappear(50);
			

			textBox("Profile.CompanyProfile.AddressOne",profileMap.get("Address1"), 100, "clickable");
			Utils.tabKey("Profile.CompanyProfile.AddressOne");
			waitForSpinnerToDisappear(100);

			
			//Utils.WebdriverWait(150, "Profile.CompanyProfile.AddressType", "stale");
			//jsTextbox("Profile.CompanyProfile.AddressType",profileMap.get("AddressType"));
			waitForSpinnerToDisappear(100);

			//Providing the Address2 if present in the dataset
			if(profileMap.containsKey("Address2")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.AddressTwo"));
				textBox("Profile.CompanyProfile.AddressTwo",profileMap.get("Address2"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.AddressTwo");
				waitForSpinnerToDisappear(30);

			}

			//Providing the Address3 if present in the dataset
			if(profileMap.containsKey("Address3")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.AddressThree"));
				textBox("Profile.CompanyProfile.AddressThree",profileMap.get("Address3"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.AddressThree");
				waitForSpinnerToDisappear(30);

			}

			//Providing the Address4 if present in the dataset
			if(profileMap.containsKey("Address4")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.AddressFour"));
				textBox("Profile.CompanyProfile.AddressFour",profileMap.get("Address4"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.AddressFour");
				waitForSpinnerToDisappear(30);

			}

			//Providing the City if present in the dataset
			if(profileMap.containsKey("City")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.City"));
				textBox("Profile.CompanyProfile.City",profileMap.get("City"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.City");
				waitForSpinnerToDisappear(30);

			}

			//Providing the Country if present in the dataset
			if(profileMap.containsKey("Country")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.Country"));
				textBox("Profile.CompanyProfile.Country",profileMap.get("Country"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.Country");
				waitForSpinnerToDisappear(30);	
			}

			//Providing the State if present in the dataset
			if(profileMap.containsKey("State")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.State"));
				textBox("Profile.CompanyProfile.State",profileMap.get("State"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.State");
				waitForSpinnerToDisappear(30);	
			}

			//Providing the PostalCode if present in the dataset
			if(profileMap.containsKey("PostalCode")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.PostalCode"));
				textBox("Profile.CompanyProfile.PostalCode",profileMap.get("PostalCode"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.PostalCode");
				waitForSpinnerToDisappear(30);	
			}

			//Providing the PostalCodeExt if present in the dataset
			if(profileMap.containsKey("PostalCodeExt")) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Utils.element("Profile.CompanyProfile.PostalCodeExtention"));
				textBox("Profile.CompanyProfile.PostalCodeExtention",profileMap.get("PostalCodeExt"), 100, "presence");
				Utils.tabKey("Profile.CompanyProfile.PostalCodeExtention");
				waitForSpinnerToDisappear(30);	
			}

			
			//Data from Excel

			name = profileMap.get("Name");
			if(name==null) {
				name = "";
			}

			FirstName = profileMap.get("FirstName");
			if(FirstName==null) {
				FirstName = "";
			}


			Title = profileMap.get("Title");
			if(Title==null) {
				Title = "";
			}
			else {
				newT = Title.toUpperCase();
			}

			//Validating address for the profile

			Address1 = profileMap.get("Address1");
			System.out.println("Address1" + Address1);
			if(Address1==null) {
				Address1 = "";
			}

			Address2 = profileMap.get("Address2");
			if(Address2==null) {
				Address2 = "";
			}

			Address3 = profileMap.get("Address3");
			if(Address3==null) {
				Address3 = "";
			}

			Address4 = profileMap.get("Address4");
			if(Address4==null) {
				Address4 = "";
			}

			Address5 = profileMap.get("Address5");
			if(Address5==null) {
				Address5 = "";
			}

			Country = profileMap.get("Country");
			if(Country==null) {
				Country = "";
			}

			City = profileMap.get("City");
			if(City==null) {
				City = "";
			}

			State = profileMap.get("State");
			if(State==null) {
				State = "";
			}

			PostalCode = profileMap.get("PostalCode");
			if(PostalCode==null) {
				PostalCode = "";
			}

			PostalCodeExt = profileMap.get("PostalCodeExt");
			if(PostalCodeExt==null) {
				PostalCodeExt = "";
			}


			if(option.equalsIgnoreCase("Save")) {

				click("Profile.profile_save_btn", 100, "clickable");
				logger.log(LogStatus.PASS,"Clicking on Save button");

				waitForSpinnerToDisappear(10);

				List<WebElement>  rows = Utils.elements("Profile.TableGrid"); 
				if(rows.size()>0) {
					driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).click();
				}
				else {
					logger.log(LogStatus.FAIL,"Profile is not shown on the manage profile screen :");
					tearDown();
				}
			}

			else {
				// Click Save and More Details
				jsClick("Profile.btn_ManageProfiles_SaveAndMoreDetails", 100, "clickable");
				logger.log(LogStatus.PASS,"Clicking on Save and more details button");
			}

			waitForSpinnerToDisappear(10);
			// Wait for Guest profile page to load		
			Utils.WebdriverWait(100, "Profile.label_ManageProfiles_SourceProfile", "presence");

			//Validate ClientID 			
			ClientID=Utils.getAttributeOfElement("Profile.manage_profile_nameID", "data-ocformvalue", 60, "presence");
			if (!ClientID.equals("")) {
				System.out.println("Client ID created successfully :"+ClientID);
				logger.log(LogStatus.PASS,"Client ID created successfully :"+ClientID);
			} else {
				System.out.println("Client ID not created successfully :"+ClientID);
				logger.log(LogStatus.FAIL,"Client ID not created successfully :"+ClientID);
				tearDown();
			}

			// Validating profile name
			String profilename="";
			List flag = driver.findElements(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")); 
			System.out.println(driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).getText());
			if(flag.size()>0) {
				profilename = driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).getText();
				logger.log(LogStatus.PASS,"Profile name is validated successfully :"+ profilename);
				System.out.println("profile name is validated");
			}
			else {
				logger.log(LogStatus.FAIL,"Profile name is not validated successfully :"+ profilename);
			}


			//Validating Address Field
            String AddressText = "";
            List flag1 = driver.findElements(By.xpath("//img[contains(@title,'Address')]/parent::span/following-sibling::span[contains(.,'"+Address1+"') and contains(.,'"+Address2+"') and contains(.,'"+Address3+"') and contains(.,'"+Address4+"') and contains(.,'"+Address5+"') and contains(.,'"+Country+"') and contains(.,'"+City+"') and contains(.,'"+State+"') and contains(.,'"+PostalCode+"') and contains(.,'"+PostalCodeExt+"')]")); 
            if(flag1.size()>0) {
                  AddressText = driver.findElement(By.xpath("//img[contains(@title,'Address')]/parent::span/following-sibling::span[contains(.,'"+Address1+"') and contains(.,'"+Address2+"') and contains(.,'"+Address3+"') and contains(.,'"+Address4+"') and contains(.,'"+Address5+"') and contains(.,'"+Country+"') and contains(.,'"+City+"') and contains(.,'"+State+"') and contains(.,'"+PostalCode+"') and contains(.,'"+PostalCodeExt+"')]")).getText();
                  logger.log(LogStatus.PASS,"Addresses in the profile is validated successfully :"+ AddressText);
                  System.out.println("profile name is validated");
            }
            else {
                  logger.log(LogStatus.FAIL,"Addresses in the profile is not validated successfully :"+ AddressText);
            }


			// Validate Communication Value / Phone No
			if (getText("Profile.label.ManageProfiles_HomeNumber").equals(profileMap.get("CommunicationValue"))) {
				System.out.println("Phone No created and validated successsfully :"+profileMap.get("CommunicationValue"));
				logger.log(LogStatus.PASS,"Phone No created and validated successsfully :"+profileMap.get("CommunicationValue"));
			} else {
				System.out.println("Phone No is not created successsfully :"+profileMap.get("CommunicationValue"));
				logger.log(LogStatus.FAIL,"Phone No is not created successsfully :"+profileMap.get("CommunicationValue"));
			}


			// Update ClientID in excel file			
			ExcelUtils.setDataByRow(OR.getConfig("Path_ProfileData"), "Profile", scriptName, "ClientID", ClientID);
			logger.log(LogStatus.PASS,"Update Corp ID in excel :"+ClientID);

			// Validate ClientID from excel file			
			if(ExcelUtils.getCellData(OR.getConfig("Path_ProfileData"), "Profile", scriptName, "ClientID").equals(ClientID)){

				System.out.println("ClientID updated successfully in excel file :"+ClientID);
				logger.log(LogStatus.PASS,"ClientID updated successfully in excel file :"+ClientID);
			}else{
				System.out.println("ClientID is not updated successfully in excel file :"+ClientID);
				logger.log(LogStatus.FAIL,"ClientID is not updated successfully in excel file :"+ClientID);
			}	


		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Guest Profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Guest Profile not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}


	/*******************************************************************
    - Description: To search a profile using advanced search option
	- Input:Client ID
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void profileAdvancedSearch(HashMap<String, String> profileMap) throws Exception {

		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String ClientID="";
		String communicationValue="";
		String name = "";
		String FirstName = "";
		String Title = "";
		String newT = "";

		name = profileMap.get("Name");
		if(name==null) {
			name = "";
		}

		FirstName = profileMap.get("FirstName");
		if(FirstName==null) {
			FirstName = "";
		}


		Title = profileMap.get("Title");
		if(Title==null) {
			Title = "";
		}
		else {
			newT = Title.toUpperCase();
		}



		System.out.println("profile Name" + profileMap.get("NAME"));

		System.out.println("Client ID" + profileMap.get("ClientID"));

		try {

			//Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			//Checking the presence of Manage profile page
			waitForSpinnerToDisappear(10);


			if(Utils.isExists("Profile.SearchAdvancedLink")) {
				Utils.element("Profile.SearchAdvancedLink").click();
			}

			//If the profile name contains clientId key, search needs to be performed according to that
			if(profileMap.containsKey("ClientID")) {
				Utils.textBox("Profile.ClientId", profileMap.get("ClientID"));
				Utils.tabKey("Profile.ClientId");
			}

			//If the profile name contains Last Name, search needs to be performed according to that
			if(profileMap.containsKey("Name")) {
				textBox("Profile.txt_ManageProfiles_Name",profileMap.get("Name"), 100, "presence");
				Utils.tabKey("Profile.txt_ManageProfiles_Name");
				waitForSpinnerToDisappear(30);
			}

			//If the profile name contains First Name, search needs to be performed according to that
			if(profileMap.containsKey("FirstName")){
				textBox("Profile.ContactProfile.FirstName",profileMap.get("FirstName"), 100, "presence");
				Utils.tabKey("Profile.ContactProfile.FirstName");
				waitForSpinnerToDisappear(30);
			}

			click("Profile.manage_profile_basicSearch_searchBtn",10,"clickable");
			waitForSpinnerToDisappear(10);

			List<WebElement> rows = Utils.elements("Profile.TableData"); 

			System.out.println("rows" + rows.size());

			if(rows.size()>0) {
				if(rows.size()>0) {
					driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).click();
				}
				else {
					logger.log(LogStatus.FAIL,"Profile is not shown on the manage profile screen :");
					tearDown();
				}
			}
			waitForSpinnerToDisappear(10);


			//Validate ClientID 			
			ClientID=Utils.getAttributeOfElement("Profile.manage_profile_nameID", "data-ocformvalue", 60, "presence");
			if (!ClientID.equals("")) {
				System.out.println("Client ID validated successfully :"+ClientID);
				logger.log(LogStatus.PASS,"Client ID validated successfully :"+ClientID);
			} else {
				System.out.println("Client ID not validated :"+ClientID);
				logger.log(LogStatus.FAIL,"Client ID not validated :"+ClientID);
				tearDown();
			}

			//Validate profile name
			String profilename="";
			List flag = driver.findElements(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")); 
			System.out.println(driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).getText());
			if(flag.size()>0) {
				profilename = driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).getText();
				logger.log(LogStatus.PASS,"Profile name is validated successfully :"+ profilename);
				System.out.println("profile name is validated");
			}
			else {
				logger.log(LogStatus.FAIL,"Profile name is not validated successfully :"+ profilename);
			}

			// Validate Communication Value / Phone No
			if (getText("Profile.label.ManageProfiles_HomeNumber").equals(profileMap.get("CommunicationValue"))) {
				System.out.println("Phone No created and validated successsfully :"+profileMap.get("CommunicationValue"));
				logger.log(LogStatus.PASS,"Phone No created and validated successsfully :"+profileMap.get("CommunicationValue"));
			} else {
				System.out.println("Phone No is not created successsfully :"+profileMap.get("CommunicationValue"));
				logger.log(LogStatus.FAIL,"Phone No is not created successsfully :"+profileMap.get("CommunicationValue"));
			}

		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, "Profile Super Search not done :: Failed " + e.getMessage());
			throw (e);
		}
	}


	/**********************************************************************
	- Description: To search a profile using basic search option
	- Input:Client ID
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	**************************************************************************/

	public static void profileBasicSearch(HashMap<String, String> profileMap) throws Exception {

		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String ClientID="";
		String communicationValue="";
		String name = "";
		String FirstName = "";
		String Title = "";
		String newT = "";

		name = profileMap.get("Name");
		if(name==null) {
			name = "";
		}

		FirstName = profileMap.get("FirstName");
		if(FirstName==null) {
			FirstName = "";
		}


		Title = profileMap.get("Title");
		if(Title==null) {
			Title = "";
		}
		else {
			newT = Title.toUpperCase();
		}


		System.out.println("profile Name" + profileMap.get("Name"));

		System.out.println("Client ID" + profileMap.get("ClientID"));

		try {

			// Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			//Checking the presence of Manage profile page
			waitForSpinnerToDisappear(10);


			if(Utils.isExists("Profile.SearchBasicLink")) {
				Utils.element("Profile.SearchBasicLink").click();
			}

			Utils.textBox("Profile.manage_profile_basicSearch_txt", profileMap.get("ClientID"));
			Utils.tabKey("Profile.manage_profile_basicSearch_txt");


			click("Profile.manage_profile_basicSearch_searchBtn",10,"clickable");
			waitForSpinnerToDisappear(10);

			//Checking if the table returns any data
			List<WebElement>  rows = Utils.elements("Profile.TableGrid"); 
			if(rows.size()>0) {
				driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).click();
			}
			else {
				logger.log(LogStatus.FAIL,"Profile is not shown on the manage profile screen :");
				tearDown();
			}

			waitForSpinnerToDisappear(10);
			//Validate ClientID 			
			ClientID=Utils.getAttributeOfElement("Profile.manage_profile_nameID", "data-ocformvalue", 60, "presence");
			if (!ClientID.equals("")) {
				System.out.println("Client ID created successfully :"+ClientID);
				logger.log(LogStatus.PASS,"Client ID created successfully :"+ClientID);
			} else {
				System.out.println("Client ID not created successfully :"+ClientID);
				logger.log(LogStatus.FAIL,"Client ID not created successfully :"+ClientID);
				tearDown();
			}


			// Validating profile name
			String profilename="";
			List flag = driver.findElements(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")); 
			System.out.println(driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).getText());
			if(flag.size()>0) {
				profilename = driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]")).getText();
				logger.log(LogStatus.PASS,"Profile name is validated successfully :"+ profilename);
				System.out.println("profile name is validated");
			}
			else {
				logger.log(LogStatus.FAIL,"Profile name is not validated successfully :"+ profilename);
			}

			// Validate Communication Value / Phone No
			if (getText("Profile.label.ManageProfiles_HomeNumber").equals(profileMap.get("CommunicationValue"))) {
				System.out.println("Phone No created and validated successsfully :"+profileMap.get("CommunicationValue"));
				logger.log(LogStatus.PASS,"Phone No validated successsfully :"+profileMap.get("CommunicationValue"));
			} else {
				System.out.println("Phone No is not created successsfully :"+profileMap.get("CommunicationValue"));
				logger.log(LogStatus.FAIL,"Phone No is not validated successsfully :"+profileMap.get("CommunicationValue"));
			}


		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, "Profile Super Search not done :: Failed " + e.getMessage());
			throw (e);
		}
	}
	
	/******************************************************************************************************************
- Description: To update profile and communication details for a profile from business card in profile overview section
- Input:Map containing all the mandatory fields required to create a profile
- Output:
- Author:Swati
- Date: 12/19/2018
- Revision History:
                - Change Date
                - Change Reason
                - Changed Behavior
                - Last Changed By
	 *****************************************************************************************************************/

	public static void updateProfileDetails(HashMap<String, String> profileMap) throws Exception {


		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String ClientID="";
		String communicationValue="";
		String newLName = "";
		String newFName = "";
		String existingName = "";
		String UpdateProfileName = "";


		try {

			//Calling profile basic search
			ProfilePage.createGuestProfile(profileMap, "SaveAdd");

			//ProfilePage.profileBasicSearch(profileMap);

			//Using Random class to generate a random number
			Random rand = new Random();   

			// Generate random integers in range 0 to 999 
			int rand_int1 = rand.nextInt(10);


				existingName = profileMap.get("Name");
				logger.log(LogStatus.PASS,"Current Profile name-> "+ existingName);
				
				
				//Saving old value of communication value

				String oldCom = Utils.getText("Profile.oldCommunicationValue");
				System.out.println("oldcom" + oldCom);

				String oldAddress = Utils.getText("Profile.oldAddressValue");
				System.out.println("oldAddress" + oldAddress);

				click("Profile.EditLink_profiledetailsPage");
				logger.log(LogStatus.INFO,"Editing profile details from Business Card");

				waitForSpinnerToDisappear(10);

				newLName = profileMap.get("Name")+rand_int1;
				newFName = profileMap.get("FirstName")+rand_int1;

				//New value in Last Name
				textBox("Profile.BusinessCard.FirstName", newLName);
				Utils.tabKey("Profile.BusinessCard.FirstName");
				waitForSpinnerToDisappear(30);
				logger.log(LogStatus.INFO,"Entered new name for the profile-> " + newLName);

				//New value in First Name
				textBox("Profile.BusinessCard.LastName", newFName);
				Utils.tabKey("Profile.BusinessCard.LastName");
				waitForSpinnerToDisappear(30);
				logger.log(LogStatus.INFO,"Entered new name for the profile-> " + newFName);

				waitForSpinnerToDisappear(10);
				
				//New value in communication
				textBox("Profile.profile_comm_type_second", rand_int1);
				Utils.tabKey("Profile.profile_comm_type_second");
				waitForSpinnerToDisappear(10);

				//Generate random string for address
				String generatedString = RandomStringUtils.randomAlphabetic(10);

				//New value in address
				textBox("Profile.CompanyProfile.AddressOne", generatedString);
				Utils.tabKey("Profile.CompanyProfile.AddressOne");
				waitForSpinnerToDisappear(10);

				click("Profile.profile_save_btn");

				waitForSpinnerToDisappear(40);


			List ll = driver.findElements(By.xpath("//a/span[contains(.,'"+newFName+"') and contains(.,'"+newLName+"')]"));
			if(ll.size()>0) {
				UpdateProfileName = driver.findElement(By.xpath("//a/span[contains(.,'"+newFName+"') and contains(.,'"+newLName+"')]")).getText();
				logger.log(LogStatus.PASS,"Profile name is updated successfully :"+ UpdateProfileName);
			}
			else {
				logger.log(LogStatus.FAIL,"Profile name is not updated successfully :"+ UpdateProfileName);
			}
			
			
			String newCom = Utils.getText("Profile.oldCommunicationValue");

			String newAddress = Utils.getText("Profile.oldAddressValue");

			if(!oldCom.equals(newCom)) {
				System.out.println("New communication details is updated successfully for the profile" +"old communication value -> " + oldCom + "new communication value -> " + newCom);
				logger.log(LogStatus.PASS,"New communication details is updated successfully for the profile" + "old communication value -> " + oldCom + "new communication value -> " + newCom);
			}
			else {
				System.out.println("New communication details is not updated successfully for the profile"  + "old communication value -> " + oldCom + "new communication value -> " + newCom);
				logger.log(LogStatus.FAIL,"New communication details is not updated successfully for the profile" + "old communication value -> " + oldCom + "new communication value -> " + newCom);
			}

			if(!oldAddress.equals(newAddress)) {
				System.out.println("New address details is updated successfully for the profile" +"old communication value -> " + oldAddress + "new communication value -> " + newAddress);
				logger.log(LogStatus.PASS,"New address details is updated successfully for the profile" + "old communication value -> " + oldAddress + "new communication value -> " + newAddress);
			}
			else {
				System.out.println("New address details is not updated successfully for the profile"  + "old communication value -> " + oldAddress + "new communication value -> " + newAddress);
				logger.log(LogStatus.FAIL,"New address details is not updated successfully for the profile" + "old communication value -> " + oldAddress + "new communication value -> " + newAddress);
			}

		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			Utils.takeScreenshot(driver, testClassName);
			LoginPage.Logout();
			throw (e);
		}

	}

	/*******************************************************************
    - Description: This method creates source profile
	- Input:Profile Name , communication type , communication value, address
	- Output:
	- Author:Dilip
	- Date: 1/06/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void createSourceProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + profileMap);

		String corpID="";
		String communicationValue="";


		try {

			// Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			// Click I Want To link 
			click("Profile.link_ManageProfiles_IWantTo", 100, "clickable");

			// Click Source profile link
			click("Profile.link_ManageProfiles_SourceProfile", 100, "clickable");

			// Entering the Name
			Utils.WebdriverWait(100, "Profile.txt_ManageProfiles_Name", "stale");
			textBox("Profile.txt_ManageProfiles_Name",profileMap.get("Name"), 100, "presence");

			// Clear the default value from Type
			clear("Profile.txt_ManageProfiles_Type");

			// Entering the Type
			Utils.WebdriverWait(100, "Profile.txt_ManageProfiles_Type", "stale");
			jsTextbox("Profile.txt_ManageProfiles_Type",profileMap.get("Type"));

			// Entering the Communication Value
			communicationValue=profileMap.get("CommunicationValue");
			jsTextbox("Profile.txt_ManageProfiles_CommunicationValue",communicationValue);

			// Entering the Address Type
			Utils.WebdriverWait(100, "Profile.txt_ManageProfiles_AddressType", "presence");
			jsTextbox("Profile.txt_ManageProfiles_AddressType",profileMap.get("Type"));

			// Entering the Address1
			jsTextbox("Profile.txt_ManageProfiles_Address1",profileMap.get("Address1"));

			// Click Save and More Details
			jsClick("Profile.btn_ManageProfiles_SaveAndMoreDetails", 100, "clickable");

			// Wait for Travel agent profile page to load		
			Utils.WebdriverWait(100, "Profile.label_ManageProfiles_SourceProfile", "presence");


			//Validate CorpID 			
			corpID=getText("Profile.label_ManageProfiles_CorpID");
			if (!corpID.equals("")) {

				logger.log(LogStatus.PASS,"Corp ID created successfully :"+corpID);
			} else {

				logger.log(LogStatus.FAIL,"Corp ID not created successfully :"+corpID);
			}



			// Validate Profile Name

			if (getText("Profile.label_ManageProfiles_Name").equals(profileMap.get("Name"))) {

				logger.log(LogStatus.PASS,"Profile Name created and validated successsfully :"+profileMap.get("Name"));
			} else {

				logger.log(LogStatus.FAIL,"Profile Name is not created successsfully :"+profileMap.get("Name"));
			}


			// Validate Communication Value / Phone No
			if (getText("Profile.label_ManageProfiles_PhoneNumber").equals(communicationValue)) {

				logger.log(LogStatus.PASS,"Phone No created and validated successsfully :"+communicationValue);
			} else {

				logger.log(LogStatus.FAIL,"Phone No is not created successsfully :"+communicationValue);
			}


			// Update CorpID in excel file			
			ExcelUtils.setDataByRow(OR.getConfig("Path_ProfileData"), "Profile", scriptName, "CorpID", corpID);
			logger.log(LogStatus.PASS,"Update Corp ID in excel :"+corpID);

			// Validate CorpID from excel file			
			if(ExcelUtils.getCellData(OR.getConfig("Path_ProfileData"), "Profile", scriptName, "CorpID").equals(corpID)){


				logger.log(LogStatus.PASS,"Cord ID updated successfully in excel file :"+corpID);
			}else{

				logger.log(LogStatus.FAIL,"Cord ID is not updated successfully in excel file :"+corpID);
			}	


		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Source Profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Source profile not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}


	/*******************************************************************
    - Description: This method creates Travel Agent profile
	- Input:Profile Name , communication type , communication value, address
	- Output:
	- Author:Dilip
	- Date: 1/06/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/



	public static void createTravelAgentProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + profileMap);

		String IATANumber="";
		String communicationValue="";

		try {

			// Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			// Click I Want To link 
			click("Profile.link_ManageProfiles_IWantTo", 100, "clickable");

			// Click Travel agent profile link
			click("Profile.link_ManageProfiles_TravelAgentProfile", 100, "clickable");

			// Entering the Name
			Utils.WebdriverWait(100, "Profile.txt_ManageProfiles_Name", "stale");
			textBox("Profile.txt_ManageProfiles_Name",profileMap.get("Name"), 100, "presence");

			// Clear the default value from Type
			clear("Profile.txt_ManageProfiles_Type");

			// Entering the Type
			Utils.WebdriverWait(100, "Profile.txt_ManageProfiles_Type", "stale");
			jsTextbox("Profile.txt_ManageProfiles_Type",profileMap.get("Type"));

			// Entering the Communication Value
			communicationValue=profileMap.get("CommunicationValue");
			jsTextbox("Profile.txt_ManageProfiles_CommunicationValue",communicationValue);

			// Entering the Address Type
			Utils.WebdriverWait(100, "Profile.txt_ManageProfiles_AddressType", "presence");
			jsTextbox("Profile.txt_ManageProfiles_AddressType",profileMap.get("Type"));

			// Entering the Address1
			jsTextbox("Profile.txt_ManageProfiles_Address1",profileMap.get("Address1"));

			// Click Save and More Details
			jsClick("Profile.btn_ManageProfiles_SaveAndMoreDetails", 100, "clickable");

			//Wait for Travel agent profile page to load		
			Utils.WebdriverWait(100, "Profile.link_ManageProfiles_TravelAgentProfile", "presence");

			//Validate IATANumber 			
			IATANumber=getText("Profile.label_ManageProfiles_IATANumber");
			if (!IATANumber.equals("")) {
				System.out.println("IATA Number created successfully :"+IATANumber);
				logger.log(LogStatus.PASS,"IATA Number created successfully :"+IATANumber);
			} else {
				System.out.println("IATA Number not created successfully :"+IATANumber);
				logger.log(LogStatus.FAIL,"IATA Number not created successfully :"+IATANumber);
			}

			// Validate Communication Value / Phone No
			if (getText("Profile.label_ManageProfiles_PhoneNumber").equals(communicationValue)) {
				System.out.println("Phone No created and validated successsfully :"+communicationValue);
				logger.log(LogStatus.PASS,"Phone No created and validated successsfully :"+communicationValue);
			} else {
				System.out.println("Phone No is not created successsfully :"+communicationValue);
				logger.log(LogStatus.FAIL,"Phone No is not created successsfully :"+communicationValue);
			}

			// Update IATA Number in excel file			
			ExcelUtils.setDataByRow(OR.getConfig("Path_ProfileData"), "Profile", scriptName, "IATANumber", IATANumber);
			logger.log(LogStatus.PASS,"Update IATA Number in profile excel sheet :"+IATANumber);

			// Validate IATA Number from excel file				
			String IATANumberFromExcel=ExcelUtils.getCellData(OR.getConfig("Path_ProfileData"), "Profile", scriptName, "IATANumber");			
			if(IATANumberFromExcel.equals(IATANumber)){

				System.out.println("IATANumber updated successfully in excel file :"+IATANumberFromExcel);
				logger.log(LogStatus.PASS,"IATANumber updated successfully in excel file :"+IATANumberFromExcel);

			}else{
				System.out.println("IATANumber is not updated successfully in excel file :"+IATANumberFromExcel);
				logger.log(LogStatus.FAIL,"IATANumber is not updated successfully in excel file :"+IATANumberFromExcel);
			}	


		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Create Travel Agent Profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Travel agent profile not created :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}

	}



	public static void profileMerge(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + profileMap);

		String ClientId2 = "";
		String communicationValue = "";

		try {
			// Creating first guest profile

			
			  ProfilePage.createGuestProfile(profileMap, "SaveAdd");
			/*
			 * // Navigating to Client Relations Thread.sleep(5000);
			 * click("Profile.OperaCloudMenu_ClientRelations", 200, "clickable");
			 * waitForSpinnerToDisappear(40); // Navigating to Profiles
			 * click("Profile.OperaCloudMenu_ClientRelations_Profiles", 200, "clickable");
			 * waitForSpinnerToDisappear(40); // Navigating to Manage Profile
			 * Thread.sleep(5000);
			 * click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile",
			 * 200,"clickable"); waitForSpinnerToDisappear(40);
			 */
			  
			  
			  click("Profile.backtocreateGuestProfile", 200, "clickable");
			  //page is in merge profile so below link is notworking
			  
			  // Click I Want To link 
			  click("Profile.link_ManageProfiles_IWantTo", 300,  "clickable");
			  
			  		  
			  // Click Guest profile link
			  click("Profile.create_guest_profile", 100,  "clickable");
			  
			  waitForSpinnerToDisappear(40);
			  
			  // Entering the Name
			  
			  // Utils.WebdriverWait(500, "Profile.txt_ManageProfiles_Name", "stale");
			  //commented as code is failing here
			  
			  textBox("Profile.txt_ManageProfiles_Name",profileMap.get("Name"), 100,
			  "presence");
			  
			  waitForSpinnerToDisappear(40);
			  
			  // Clear the default value from Type
			  clear("Profile.txt_ManageProfiles_Type");
			  
			  
			  // Entering the Type
			  
			Utils.WebdriverWait(200, "Profile.txt_ManageProfiles_Type", "stale");
			  //commented as code is failing here
			  
			  jsTextbox("Profile.txt_ManageProfiles_Type",profileMap.get("Type"));
			  
			  // Entering the Communication Value
			  communicationValue=profileMap.get("CommunicationValue");
			  jsTextbox("Profile.txt_ManageProfiles_CommunicationValue",communicationValue)
			  ;
			  
			  Thread.sleep(10);
			  driver.findElement(By.xpath("//*[@data-ocid='TXT_CNTNT_ADDRESS1']")).sendKeys
			  (profileMap.get("Address2"));
			  
			  waitForSpinnerToDisappear(40);
			  
			  
			  // Wait for Guest profile page to load 
			 // Utils.WebdriverWait(200,  "Profile.label_ManageProfiles_SourceProfile", "presence"); //commented as code is failing here
			  
			  jsClick("Profile.btn_ManageProfiles_SaveAndMoreDetails", 100, "clickable");
				logger.log(LogStatus.PASS, "Clicking on Save and more details button");
			  
			  //save butone is missing      /*********/
			  //Validate ClientID
			  ClientId2=Utils.getAttributeOfElement("Profile.manage_profile_nameID",
			  "data-ocformvalue", 60, "presence"); if (!ClientId2.equals("")) {
			  System.out.println("Client ID created successfully :"+ClientId2);
			  logger.log(LogStatus.PASS,"Client ID created successfully :"+ClientId2); }
			  else { System.out.println("Client ID not created successfully :"+ClientId2);
			  logger.log(LogStatus.FAIL,"Client ID not created successfully :"+ClientId2);
			  tearDown(); }
			  
			  // Validate Communication Value / Phone No
			  if
			  (getText("Profile.profile_presentation_page_name").equals(profileMap.get(
			  "Name"))) {
			  System.out.println("Profile created and validated successsfully :" +
			  profileMap.get("Name"));
			  logger.log(LogStatus.PASS,"Profile created and validated successsfully :" +
			  profileMap.get("Name")); } else {
			  System.out.println("Profile is not created successsfully :");
			  logger.log(LogStatus.FAIL,"Profile is not created successsfully :"); }
			  
			  // Update ClientID in excel file
			  ExcelUtils.setDataByRow(OR.getConfig("Path_ProfileData"), "Profile",
			  scriptName, "ClientId2", ClientId2);
			  logger.log(LogStatus.PASS,"Update Corp ID in excel :"+ClientId2);
			  
			  // Validate ClientID from excel file
			  if(ExcelUtils.getCellData(OR.getConfig("Path_ProfileData"), "Profile",
			  scriptName, "ClientId2").equals(ClientId2)){
			  
			  System.out.println("ClientID updated successfully in excel file :"+ClientId2)
			  ; logger.log(LogStatus.PASS,"ClientID updated successfully in excel file :"
			  +ClientId2); }else{
			  System.out.println("ClientID is not updated successfully in excel file :"
			  +ClientId2); logger.log(LogStatus.
			  FAIL,"ClientID is not updated successfully in excel file :"+ClientId2); }
			  
			  
			  System.out.println("map" + profileMap);
			  
			  profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"),
			  "Profile","ProfileMerge");
			  String clientId1 = ExcelUtils.getCellData(OR.getConfig("Path_ProfileData"), "Profile", "createGuestProfile", "ClientID");
			  System.out.println("clientId1: "+clientId1);
			  profileMap.put("ClientID", clientId1);
			  
			  ProfilePage.profileAdvancedSearch(profileMap);
			  
			  
			  
			  //click("Profile.MergeIwantto");
			  
			  click("Profile.link_IWantto");
			  
			  
			  waitForSpinnerToDisappear(40);
			  
			  
			  click("Profile.Merge_profile");
			  
			  
			  waitForSpinnerToDisappear(40);
			  
			  click("Profile.Merge_select_profile");
			  
			  Thread.sleep(10);
			  
			/*
			 * WebElement xpath = driver.findElement(By.xpath(
			 * "//iframe[starts-with(@src,'/OPERA9/o90_smoke12c/operacloud/faces')]"));
			 * driver.switchTo().frame(xpath);
			 */
			  
			  if(Utils.isExists("Profile.mergeframe", 50, "frame")) {
				  WebElement xpath = Utils.element("Profile.mergeframe");
				  driver.switchTo().frame(xpath);
			  }
			  
			  
			  textBox("Profile.ClientId", profileMap.get("ClientId2"));//*****CLIENT  ID2 IS GETTING NULL
			  Utils.tabKey("Profile.ClientId");
			  
			  waitForSpinnerToDisappear(40);
			  
			  click("Profile.manage_profile_basicSearch");//  Profile.manage_profile_basicSearch,SEARCH IS NOT HAPPENING Profile.EditLink_communicationTypePOPUPSearch=xpath&//*[@data-ocid='1_BTN_SEARCH']
			  
			  waitForSpinnerToDisappear(40);
			  Thread.sleep(10000);
			  click("Profile.EditLink_communicationTypePOPUPSelect"); //Profile.Merge_selectbuttonBEFORE SELECTING WE SHOULD CLICK ON THE PROFILE***SELECT BUTTON IS MISSING
			 // click("Profile.Merge_selectbutton");
			  waitForSpinnerToDisappear(40);
			  
			  click("Profile.Merge_button");
			  
			  waitForSpinnerToDisappear(40);
			  
			  Assert.assertEquals(getText("Profile.manage_profile_validationtxt", 100, "presence"), "Manage Profile");
			logger.log(LogStatus.PASS, "Landed in Manage Profile page");

			// Profile.profile_presentation_page_name

			List<WebElement> communication_rows = Utils.elements("Profile.TableCommunicationValue");

			for (int i = 1; i <= communication_rows.size(); i++) {

				if (driver
						.findElement(By.xpath("//*[@data-ocid='TBL_T2']//div[2]/table/tbody/tr[" + i
								+ "]/td[3]//table/tbody/tr/td[2]"))
						.getText().equals(profileMap.get("CommunicationValue"))) {
					System.out.println(i + " " + "Profile Communication Value is validated successsfully :"
							+ profileMap.get("CommunicationValue"));
					logger.log(LogStatus.PASS, i + " " + "Profile Communication Value is validated successsfully :"
							+ profileMap.get("CommunicationValue"));
				} else {
					System.out.println(i + " " + "Profile Communication Value is not validated successsfully :");
					logger.log(LogStatus.FAIL,
							i + " " + "Profile Communication Value is not validated successsfully :");
				}
			}
			List<WebElement> address_rows = Utils.elements("Profile.TableAddressValue");

			for (int j = 1; j <= address_rows.size(); j++) {

				if (driver.findElement(By.xpath(
						"//*[@data-ocid='TBL_T1']//div[2]/table/tbody/tr[" + j + "]/td[1]/div/table/tbody/tr[1]/td[3]"))
						.getText().contains(profileMap.get("Address"+j))) {
					System.out.println(j + " " + "Profile Address Value is validated successsfully :" + profileMap.get("Address"+j));
					logger.log(LogStatus.PASS,
							j + " " + "Profile Address Value is validated successsfully : " + profileMap.get("Address"+j));
				} else {
					System.out.println(j + " " + "Profile Address Value is not validated successsfully :"+profileMap.get("Address"+j));
					logger.log(LogStatus.FAIL, j + " " + "Profile Address Value is not validated successsfully :" +profileMap.get("Address"+j));
				}

			}

		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			// Utils.takeScreenshot(driver, testClassName);
			throw (e);
		}
	}

	/**
	 * @description This method creates company profile and returns the profile name
	 * @author cpsinha
	 * @Input companyProfileMap
	 * @Output companyName
	 */
	public static String createCompanyProfile(HashMap<String, String> companyProfileMap) throws Exception {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

		try {
			// Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			// Click I Want To link 
			click("Profile.link_ManageProfiles_IWantTo", 100, "clickable");

			// Click Guest profile link
			click("Profile.create_company_profile", 100, "clickable");

			waitForSpinnerToDisappear(10);
			String tempName= companyProfileMap.get("Name");
			Random random = new Random();
			Integer tempRandomNumber= random.nextInt(99999);
			String finalName=tempName+tempRandomNumber.toString();
			System.out.println(finalName);
			textBox("Profile.CompanyProfile.FirstName", finalName, 0, "clickable");

			textBox("Profile.CompanyProfile.SecondName", companyProfileMap.get("Name2"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.SecondName");
			waitForSpinnerToDisappear(20);
			textBox("Profile.CompanyProfile.ThirdName", companyProfileMap.get("Name3"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.ThirdName");
			waitForSpinnerToDisappear(20);
			click("Profile.CompanyProfile.AddressTypeLabel");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.CommunicationType", companyProfileMap.get("Type"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.CommunicationType");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.CommunicationValue", companyProfileMap.get("CommunicationValue"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.CommunicationValue");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.Sequence", companyProfileMap.get("Sequence"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.Sequence");
			waitForSpinnerToDisappear(30);
			if(getText("Profile.CompanyProfile.CommunicationType",0, "clickable").equals("")){
				textBox("Profile.CompanyProfile.CommunicationType", companyProfileMap.get("Type"), 0, "clickable");
			}
			waitForSpinnerToDisappear(30);
			click("Profile.CompanyProfile.AddressTypeLabel");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressType", companyProfileMap.get("AddressType"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.AddressType");
			waitForSpinnerToDisappear(30);
			click("Profile.CompanyProfile.AddressTypeLabel");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressOne", companyProfileMap.get("Address1"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.AddressOne");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressTwo", companyProfileMap.get("Address2"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.AddressTwo");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressThree", companyProfileMap.get("Address3"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.AddressThree");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressFour", companyProfileMap.get("Address4"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.AddressFour");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.City", companyProfileMap.get("City"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.City");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.Country", companyProfileMap.get("Country"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.Country");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.State", companyProfileMap.get("State"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.State");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.PostalCode", companyProfileMap.get("PostalCode"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.PostalCode");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.PostalCodeExtention", companyProfileMap.get("PostalCodeExt"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.PostalCodeExtention");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.Language", companyProfileMap.get("Language"), 0, "clickable");
			Utils.tabKey("Profile.CompanyProfile.Language");
			waitForSpinnerToDisappear(30);
			Assert.assertTrue(element("Profile.CompanyProfile.PrimaryAddressChecked").isDisplayed(),"The Primary address is not checked");
			click("Profile.CompanyProfile.SaveAndAddMoreDetails",  0, "clickable");
			waitForSpinnerToDisappear(50);
			Assert.assertTrue(element("Profile.CompanyProfile.PresentationScreen").isDisplayed());
			Assert.assertTrue(element("Profile.CompanyProfile.ProfileConfirmation").getText().equals(finalName));
			ExcelUtils.setDataByRow(OR.getConfig("Path_ProfileData"), "Profile", "createProfileCompany","FinalName", finalName);
			return finalName;

		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create a Company Profile " + e.getMessage());
			Assert.fail("Could not create Company Profile");
			tearDown();
			throw (e);			
		}
	}

	public static void createContactProfile(HashMap<String, String> contactProfileMap) throws Exception {
		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		System.out.println("testClassName: " + testClassName + " methodName: " + methodName);

		try {
			// Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			// Click I Want To link 
			click("Profile.link_ManageProfiles_IWantTo", 100, "clickable");

			// Click Guest profile link
			click("Profile.create_contact_profile", 100, "clickable");

			waitForSpinnerToDisappear(10);
			String tempName= contactProfileMap.get("Name");
			Random random = new Random();
			Integer tempRandomNumber= random.nextInt(99999);
			String finalName=tempName+tempRandomNumber.toString();
			textBox("Profile.CompanyProfile.FirstName", finalName, 0, "clickable");

			textBox("Profile.ContactProfile.FirstName", contactProfileMap.get("FirstName"), 0, "clickable");
			textBox("Profile.ContactProfile.MiddleName", contactProfileMap.get("MiddleName"), 0, "clickable");
			click("Profile.CompanyProfile.AddressTypeLabel");
			waitForSpinnerToDisappear(30);
			textBox("Profile.ContactProfile.Title", contactProfileMap.get("Title"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			elements("Profile.CompanyProfile.Language").get(1).sendKeys(contactProfileMap.get("Language"));
			textBox("Profile.CompanyProfile.CommunicationType", contactProfileMap.get("Type"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.CommunicationValue", contactProfileMap.get("CommunicationValue"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.Sequence", contactProfileMap.get("Sequence"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			if(getText("Profile.CompanyProfile.CommunicationType",0, "clickable").equals("")){
				textBox("Profile.CompanyProfile.CommunicationType", contactProfileMap.get("Type"), 0, "clickable");
			}
			waitForSpinnerToDisappear(30);
			click("Profile.CompanyProfile.AddressTypeLabel");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressType", contactProfileMap.get("AddressType"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			click("Profile.CompanyProfile.AddressTypeLabel");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressOne", contactProfileMap.get("Address1"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressTwo", contactProfileMap.get("Address2"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressThree", contactProfileMap.get("Address3"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.AddressFour", contactProfileMap.get("Address4"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.City", contactProfileMap.get("City"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.Country", contactProfileMap.get("Country"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.State", contactProfileMap.get("State"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.PostalCode", contactProfileMap.get("PostalCode"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.PostalCodeExtention", contactProfileMap.get("PostalCodeExt"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			textBox("Profile.CompanyProfile.Language", contactProfileMap.get("Language"), 0, "clickable");
			waitForSpinnerToDisappear(30);
			Assert.assertTrue(element("Profile.CompanyProfile.PrimaryAddressChecked").isDisplayed(),"The Primary address is not checked");
			click("Profile.CompanyProfile.SaveAndAddMoreDetails",  0, "clickable");
			waitForSpinnerToDisappear(50);
			Assert.assertTrue(element("Profile.CompanyProfile.PresentationScreen").isDisplayed());
			Assert.assertTrue(element("Profile.ContactProfile.ProfileConfirmation").getText().equals(finalName));


		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, " Could not create a Company Profile " + e.getMessage());
			Assert.fail("Could not create Company Profile");
			tearDown();
			throw (e);
		}
	}



	/*******************************************************************
    - Description: This method add preferences to a profile
	- Input:Profile Name , communication type , communication value, address , preference group , preference code
	- Output:
	- Swati
	- Date: 1/06/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/



	public static void AddPreferencesToProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);

		System.out.println("Map: " + profileMap);


		try {

			//Clicking on show all for all options in profile
			click("Profile.ShowAll");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on show all for all options in profile");

			String fontWeightB = Utils.element("Profile.LinkPreferences").getCssValue("font-weight");
			System.out.println("font" + fontWeightB);

			//Clicking on Preferences
			click("Profile.LinkPreferences");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on Preferences");

			//Adding preference to the profile
			click("Profile.AddNewPreference");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Adding preference to the profile");

			//Passing the configured preference group
			textBox("Profile.PreferencesAddFilter", profileMap.get("Preference_Group"));
			logger.log(LogStatus.PASS, "Passing the configured preference group -> " + profileMap.get("Preference_Group"));

			//Clicking search
			click("Configuration.Preferences.SearchFilter");
			waitForSpinnerToDisappear(20);

			//Looping through the preferences in the preference group and selecting the configured preference

			List li = Utils.elements("Profile.AvailablePreferences");
			if(li.size()>0) {

				for(int i =0 ; i<li.size();i++) {

					String text = driver.findElement(By.xpath("//*[@data-ocid='TBL_ODEC_SLOV_AVLB_TBL']/div[2]/table/tbody/tr["+(i+1)+"]/td[@data-ocid='"+i+"_ODEC_SLOV_AVLB_TBL_C2']")).getText();
					if(text.equals(profileMap.get("Preference_Code"))) {
						driver.findElement(By.xpath("//*[@data-ocid='TBL_ODEC_SLOV_AVLB_TBL']/div[2]/table/tbody/tr["+(i+1)+"]/td[@data-ocid='"+i+"_ODEC_SLOV_AVLB_TBL_C2']")).click();
						logger.log(LogStatus.PASS, "Looping through the preferences in the preference group and selecting the configured preference-> " + profileMap.get("Preference_Code"));
						break;
					}
				}
			}
			else {
				logger.log(LogStatus.FAIL, "No preference configured for selected preference group");
			}

			click("Profile.AddPreferenceButton");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Adding the selected preference");

			click("Profile.SavePreference");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Saving the selected preference");


			Boolean flag = Utils.ValidateGridData("Profile.PreferencesTable", profileMap.get("Preference_Code"));

			if(flag) {

				logger.log(LogStatus.PASS, "Preferences are added to the profile");

			}
			else {
				logger.log(LogStatus.FAIL, "Preferences are not added to the profile");
			}

			click("Profile.PreferencesClose");
			waitForSpinnerToDisappear(50);

			String fontWeight = Utils.element("Profile.LinkPreferences").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			assertTrue((fontWeight.equals("700") || fontWeight.equals("bold")), "Preferences are  Not coming in bold");
			logger.log(LogStatus.PASS, "Preferences are coming in bold");

		} 
		catch(AssertionError e ) {
			throw new Exception(e);


		}catch (Exception e) {try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			logger.log(LogStatus.FAIL, "Adding preferences to Profile :: Failed " + alertText);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			System.out.println("Alert data: " + alertText);
			alert.accept();
		} catch (NoAlertPresentException ex) {
			ex.printStackTrace();
		}
		Utils.takeScreenshot(driver, scriptName);
		logger.log(LogStatus.FAIL, "Adding preferences to Profile :: Failed " + e.getLocalizedMessage());
		logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
		logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
		throw (e);

		}
	}

	/*************************************************************************************************************
    - Description: This method add memberships to a profile
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



	public static void AddMembershipToProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + profileMap);


		try {

			//Clicking on show all for all options in profile
			click("Profile.ShowAll");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on show all for all options in profile");

			//Clicking on Preferences
			click("Profile.LinkMemberships");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on Memberships");

			//Adding preference to the profile
			click("Profile.AddNewMembership");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Adding Memberships to the profile");

			//Passing the configured membership type
			textBox("Profile.MembershipType", profileMap.get("Membership_Type"));
			Utils.tabKey("Profile.MembershipType");
			logger.log(LogStatus.PASS, "Passing the configured Membership Type -> " + profileMap.get("Membership_Type"));
			waitForSpinnerToDisappear(50);

			//Providing card number
			textBox("Profile.MembershipCardNumber", profileMap.get("Card_Num"));
			Utils.tabKey("Profile.MembershipCardNumber");
			waitForSpinnerToDisappear(50);

			//Clicking on save button for memberships
			click("Profile.MembershipSave");
			waitForSpinnerToDisappear(50);

			Boolean flag = Utils.ValidateGridData("Profile.MembershipTable", profileMap.get("Membership_Type"));
			if(flag) {

				logger.log(LogStatus.PASS, "Memberships are added to the profile");

			}

			else {
				logger.log(LogStatus.FAIL, "Memberships are not added to the profile");
			}


			//closing membership popup

			click("Profile.MembershipsClose");
			waitForSpinnerToDisappear(50);

			//Checking if the membership text is coming in bold after adding the memberships
			String fontWeight = Utils.element("Profile.LinkMemberships").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			assertTrue((fontWeight.equals("700") || fontWeight.equals("bold")), "Memberships are not coming in bold");
			logger.log(LogStatus.PASS, "Memberships are coming in bold");

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
	}

	/*************************************************************************************************************
    - Description: This method add Notes to a profile
	- Input:Profile Name , communication type , communication value, address , Notes type 
	- Output:
	- Swati
	- Date: 1/11/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 *****************************************************************************************************************/



	public static void AddNotesToProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + profileMap);


		try {
			//Clicking on show all for all options in profile
			click("Profile.ShowAll");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on show all for all options in profile");

			//Clicking on Notes
			click("Profile.LinkNotes");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on Notes");

			//Adding preference to the profile
			click("Profile.NewNotes");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Adding Notes to the profile");

			//Passing the configured Notes type
			textBox("Profile.NotesType", profileMap.get("NOTES_TYPE"));
			Utils.tabKey("Profile.NotesType");
			logger.log(LogStatus.PASS, "Passing the configured Notes Type -> " + profileMap.get("NOTES_TYPE"));
			waitForSpinnerToDisappear(100);
			//Providing comments
			textBox("Profile.NoteComment", "test");
			waitForSpinnerToDisappear(100);
			//Clicking on save button for notes
			click("Profile.NotesSave");


//			String noteTitle = Utils.getAttributeOfElement("Profile.NotesTitleValidation", "data-ocformvalue", 100, "presence");
//			assertEquals(noteTitle, profileMap.get("Notes_Title"), "Profile Notes title from datasheet and application does not match");
//			logger.log(LogStatus.PASS, "Profile Notes title from datasheet and application Matches");

			String noteType = Utils.getAttributeOfElement("Profile.NotesTypeValidation", "data-ocformvalue", 100, "presence");
			assertEquals(noteType, profileMap.get("NOTES_TYPE"), "Profile Notes type from datasheet and application does not match");
			logger.log(LogStatus.PASS, "Profile Notes type from datasheet and application Matches");


			//closing Notes popup

			click("Profile.NotesClose");
			waitForSpinnerToDisappear(30);

			//Checking if the notes text is coming in bold after adding the notes
			String fontWeight = Utils.element("Profile.LinkNotes").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			assertTrue((fontWeight.equals("700") || fontWeight.equals("bold")), "Notes are not coming in bold");
			logger.log(LogStatus.PASS, "Notes are coming in bold");

		} 
		catch(AssertionError e ) {
			throw new Exception(e);


		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding notes to Profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Adding notes to Profile :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}
	
	/*************************************************************************************************************
    - Description: This method add Notes to a profile
	- Input:Profile Name , communication type , communication value, address , Notes type 
	- Output:
	- Swati
	- Date: 1/11/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 *****************************************************************************************************************/



	public static void AddMembershipNotesToProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + profileMap);


		try {
			//Clicking on show all for all options in profile
			click("Profile.ShowAll");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on show all for all options in profile");

			//Clicking on Notes
			click("Profile.LinkNotes");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on Notes");

			//Adding preference to the profile
			click("Profile.NewNotes");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Adding Notes to the profile");

			//Passing the configured Notes type
			textBox("Profile.NotesType", profileMap.get("Notes_Type"));
			Utils.tabKey("Profile.NotesType");
			logger.log(LogStatus.PASS, "Passing the configured Notes Type -> " + profileMap.get("Notes_Type"));

			//Providing comments
			textBox("Profile.NoteComment", "test");

			//Clicking on save button for notes
			click("Profile.NotesSave");


			String noteTitle = Utils.getAttributeOfElement("Profile.NotesTitleValidation", "data-ocformvalue", 100, "presence");
			assertEquals(noteTitle, profileMap.get("Notes_Title"), "Profile Notes title from datasheet and application does not match");
			logger.log(LogStatus.PASS, "Profile Notes title from datasheet and application Matches");

			String noteType = Utils.getAttributeOfElement("Profile.NotesTypeValidation", "data-ocformvalue", 100, "presence");
			assertEquals(noteTitle, profileMap.get("Notes_Type"), "Profile Notes type from datasheet and application does not match");
			logger.log(LogStatus.PASS, "Profile Notes type from datasheet and application Matches");


			//closing Notes popup

			click("Profile.NotesClose");
			waitForSpinnerToDisappear(30);

			//Checking if the notes text is coming in bold after adding the notes
			String fontWeight = Utils.element("Profile.LinkNotes").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			assertTrue((fontWeight.equals("700") || fontWeight.equals("bold")), "Notes are not coming in bold");
			logger.log(LogStatus.PASS, "Notes are coming in bold");

		} 
		catch(AssertionError e ) {
			throw new Exception(e);


		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding notes to Profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Adding notes to Profile :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}

	
	/*************************************************************************************************************
    - Description: This method add Keyword to a profile
	- Input:KeywordType 
	- Output:
	- Dilip
	- Date: 1/11/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 *****************************************************************************************************************/

	public static void AddKeywordToProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("Name: " + scriptName);		 
		System.out.println("Map: " + profileMap);


		try {

			//Clicking on show all
			click("Profile.linkShowAll");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on show all for all options in profile");

			//Clicking on Keyword
			click("Profile.Keyword");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on Keyword");

			//Click New link
			click("Profile.LinkKeywordNew");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Click New link");

			//Entering KeywordType
			textBox("Profile.txtKeywordType", profileMap.get("Keyword_Type"));
			logger.log(LogStatus.PASS, "Entering KeywordType :" + profileMap.get("Keyword_Type"));

			//Entering Keyword
			Utils.WebdriverWait(100, "Profile.txtKeyword", "presence");
			jsTextbox("Profile.txtKeyword", profileMap.get("Keyword"));
			logger.log(LogStatus.PASS, "Entering Keyword :" + profileMap.get("Keyword"));


			//Clicking save button
			jsClick("Profile.btnSubscriptionsSave");
			waitForSpinnerToDisappear(20);
			logger.log(LogStatus.PASS, "Click save button");


			if (ValidateGridData("Profile.tableSubscription", profileMap.get("Keyword_Type"))){

				logger.log(LogStatus.PASS, "Keyword is added to the profile :"+profileMap.get("Keyword_Type"));
			} else {

				logger.log(LogStatus.FAIL,"Keyword is not added to the profile :"+profileMap.get("Keyword_Type"));
			}

			//Clicking close link
			click("Profile.linkKeywordClose");
			waitForSpinnerToDisappear(20);
			logger.log(LogStatus.PASS, "Click close link");


			// Validation of keyword font in bold			
			String fontWeight = Utils.element("Profile.Keyword").getCssValue("font-weight");
			System.out.println("font" + fontWeight);


			if (fontWeight.equals("700") || fontWeight.equals("bold")) {

				logger.log(LogStatus.PASS,"Keyword are coming in bold");

			} else {

				logger.log(LogStatus.FAIL,"Keyword are not coming in bold");
			}

			// Keyword no validation 

			if (getText("Profile.linkKeyword1").equals("Keyword (1)")) {

				logger.log(LogStatus.PASS,"Keyword(1) text validation success");

			} else {

				logger.log(LogStatus.FAIL,"Keyword(1) text validation unsuccess");
			}



		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Keyword not added to profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Keyword not added to profile :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}

	
	/*************************************************************************************************************
    - Description: This method issue ECertificate to a profile
	- Input:ECertificate
	- Output:
	- Dilip
	- Date: 1/11/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 *****************************************************************************************************************/

	public static void IssueECertificatesToProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("Name: " + scriptName);		 
		System.out.println("Map: " + profileMap);


		try {

			//Clicking on show all for all options in profile
			click("Profile.linkShowAll");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on show all for all options in profile");

			//Clicking on ECertificates
			click("Profile.ECertificates");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on ECertificates");

			//Click More link
			click("Profile.LinkEcertificatesMore");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Click More link");			

			//Click Issue link
			click("Profile.LinkEcertificatesIssue");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Click Issue link");


			//Entering EcertificatesCode
			Utils.WebdriverWait(100, "Profile.txtEcertificatesCode", "presence");
			jsTextbox("Profile.txtEcertificatesCode", profileMap.get("ECertificate_Code"));
			logger.log(LogStatus.PASS, "Entering EcertificatesCode :" + profileMap.get("ECertificate_Code"));


			//Clicking save button
			waitForSpinnerToDisappear(50);
			jsClick("Profile.SavePreference");
			logger.log(LogStatus.PASS, "Click save button");

			//Clicking Search button
			waitForSpinnerToDisappear(100);
			jsClick("Profile.btnEcertificatesSearch");
			logger.log(LogStatus.PASS, "Click search button");

			waitForSpinnerToDisappear(100);
			if (ValidateGridData("Profile.tableEcertificates", profileMap.get("ECertificate_Code"))){

				logger.log(LogStatus.PASS, "Ecertificates is created successfully :"+profileMap.get("ECertificate_Code"));
			} else {

				logger.log(LogStatus.FAIL, "Ecertificates is not created successfully :"+profileMap.get("ECertificate_Code"));
			}

			//Clicking close link
			waitForSpinnerToDisappear(100);
			jsClick("Profile.linkEcertificatesClose");
			logger.log(LogStatus.PASS, "Click close link");


			Utils.WebdriverWait(100, "Profile.linkEcertificates1", "presence");
			System.out.println(getText("Profile.linkEcertificates1"));
			if (getText("Profile.linkEcertificates1").equals("E-Certificates (1)")) {

				logger.log(LogStatus.PASS,"E-Certificates (1) text validation success");

			} else {

				logger.log(LogStatus.FAIL,"E-Certificates (1) text validation unsuccess");
			}

			// Validation of ECertificates font in bold			
			String fontWeight = Utils.element("Profile.ECertificates").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			if (fontWeight.equals("700") || fontWeight.equals("bold")) {

				logger.log(LogStatus.PASS,"ECertificates are coming in bold");

			} else {

				logger.log(LogStatus.FAIL,"ECertificates are not coming in bold");
			}



		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Ecertificates not issue to profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Ecertificates not issue to profile :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}

	

	/*******************************************************************
    - Description: This method add Subscriptions to a profile
	- Input:Database
	- Output:
	- Dilip
	- Date: 1/11/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/



	public static void AddSubscriptionsToaProfile(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("Name: " + scriptName);		 
		System.out.println("Map: " + profileMap);

		try {

			//Clicking on show all for all options in profile
			click("Profile.linkShowAll", 60, "clickable");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on show all for all options in profile");

			//Clicking on Subscriptions
			click("Profile.Subscriptions");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Clicking on Subscriptions");

			//Click Subscriptions link on popup
			click("Profile.linkSubscriptions");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.PASS, "Click Subscriptions link on popup");

			//Adding Database
			textBox("Profile.txtSubscriptionsDatabase", profileMap.get("Database"));
			logger.log(LogStatus.PASS, "Adding Database for Subscriptions :" + profileMap.get("Database"));

			//Clicking save button
			waitForSpinnerToDisappear(100);
			jsClick("Profile.btnSubscriptionsSave");
			logger.log(LogStatus.PASS, "Click save button");

			waitForSpinnerToDisappear(50);
			if(ValidateGridData("Profile.tableSubscription", profileMap.get("Database"))){

				logger.log(LogStatus.PASS, "Subscriptions are added to the profile");
			} else {
				System.out.println("Profile is not created successsfully :");
				logger.log(LogStatus.FAIL,"Subscriptions are not added to the profile");
			}

			//Clicking close link
			jsClick("Profile.linkSubscriptionClose");
			waitForSpinnerToDisappear(20);
			logger.log(LogStatus.PASS, "Click close link");

			//			if (getText("Profile.linkSubscriptionBold").equals("Subscriptions (1)")) {
			//			
			//				logger.log(LogStatus.PASS,"Subscriptions added successfully");
			//			} else {
			//				
			//				logger.log(LogStatus.FAIL,"Subscriptions not added successfully");
			//			}

			// Validation of Subscriptions font in bold			
			String fontWeight = Utils.element("Profile.Subscriptions").getCssValue("font-weight");
			System.out.println("font" + fontWeight);

			if (fontWeight.equals("700") || fontWeight.equals("bold")) {

				logger.log(LogStatus.PASS,"Subscriptions are coming in bold");

			} else {

				logger.log(LogStatus.FAIL,"Subscriptions are not coming in bold");
			}

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Subscriptions not added to profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Subscriptions not added to profile :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}
	/*******************************************************************
	- Description: To update communication details for a profile
	- Input:Client ID, new communication details
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	public static void updateCommunicationDetails_Link(HashMap<String, String> profileMap) throws Exception {


		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String ClientID="";
		String communicationValue="";


		try {

			//Calling profile basic search
			ProfilePage.createGuestProfile(profileMap, "SaveAdd");

			//Saving old value of communication value

			String oldCom = Utils.getText("Profile.oldCommunicationValue");
			System.out.println("oldcom" + oldCom);
			logger.log(LogStatus.PASS,"Current Communication value-> "+ oldCom);

			String oldAddress = Utils.getText("Profile.oldAddressValue");
			System.out.println("oldAddress" + oldAddress);
			logger.log(LogStatus.PASS,"Current address value-> "+ oldAddress);
			

			//Using Random class to generate a random number
			Random rand = new Random();   

			// Generate random integers in range 0 to 999 
			int rand_int1 = rand.nextInt(1000000);


				click("Profile.CommunicationLink");

				waitForSpinnerToDisappear(20);

				click("Profile.communicationLink_Edit");

				waitForSpinnerToDisappear(20);

				//New value in communication
				textBox("Profile.profile_comm_type_second", rand_int1);

				waitForSpinnerToDisappear(10);

				//Generate random string for address
				String generatedString1 = RandomStringUtils.randomAlphabetic(10);

				//New value in address
				textBox("Profile.CompanyProfile.AddressOne", generatedString1);
				Utils.tabKey("Profile.CompanyProfile.AddressOne");
				waitForSpinnerToDisappear(10);

				click("Profile.profile_save_btn");

				waitForSpinnerToDisappear(10);

				click("Profile.communicationLink_Close");

				waitForSpinnerToDisappear(200);

			String newCom = Utils.getText("Profile.oldCommunicationValue");
			logger.log(LogStatus.PASS,"new communication value-> "+ newCom);

			String newAddress = Utils.getText("Profile.oldAddressValue");
			logger.log(LogStatus.PASS,"new address value-> "+ newAddress);

			if(!oldCom.equals(newCom)) {
				System.out.println("New communication details is updated successfully for the profile" +"old communication value -> " + oldCom + "new communication value -> " + newCom);
				logger.log(LogStatus.PASS,"New communication details is updated successfully for the profile" + "old communication value -> " + oldCom + "new communication value -> " + newCom);
			}
			else {
				System.out.println("New communication details is not updated successfully for the profile"  + "old communication value -> " + oldCom + "new communication value -> " + newCom);
				logger.log(LogStatus.FAIL,"New communication details is not updated successfully for the profile" + "old communication value -> " + oldCom + "new communication value -> " + newCom);
			}

			if(!oldAddress.equals(newAddress)) {
				System.out.println("New communication details is updated successfully for the profile" +"old communication value -> " + oldAddress + "new communication value -> " + newAddress);
				logger.log(LogStatus.PASS,"New communication details is updated successfully for the profile" + "old communication value -> " + oldAddress + "new communication value -> " + newAddress);
			}
			else {
				System.out.println("New communication details is not updated successfully for the profile"  + "old communication value -> " + oldAddress + "new communication value -> " + newAddress);
				logger.log(LogStatus.FAIL,"New communication details is not updated successfully for the profile" + "old communication value -> " + oldAddress + "new communication value -> " + newAddress);
			}

		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			Utils.takeScreenshot(driver, testClassName);
			LoginPage.Logout();
			throw (e);
		}

	}
	
	
	/*******************************************************************
	- Description: To update communication details for a profile
	- Input:Client ID, new communication details
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
		 ********************************************************************/

		public static void updateProfileDetails_Link(HashMap<String, String> profileMap) throws Exception {


			String testClassName = Utils.getClassName();
			String methodName = Utils.getMethodName();
			String ClientID="";
			String communicationValue="";
			String newLName = "";
			String newFName = "";
			String existingName = "";
			String UpdateProfileName = "";


			try {

				//Calling profile basic search
				ProfilePage.createGuestProfile(profileMap, "SaveAdd");

				//ProfilePage.profileBasicSearch(profileMap);

				//Using Random class to generate a random number
				Random rand = new Random();   

				// Generate random integers in range 0 to 999 
				int rand_int1 = rand.nextInt(10);


					existingName = profileMap.get("Name");
					logger.log(LogStatus.PASS,"Current Last Profile name-> "+ existingName);

					
					//Clicking on profile details link
					click("Profile.ProfileDetails");
					waitForSpinnerToDisappear(20);
					
					existingName = profileMap.get("FirstName");
					logger.log(LogStatus.PASS,"Current First Profile name-> "+ existingName);

					click("Profile.ProfileDetailsEditLink");
					logger.log(LogStatus.INFO,"Editing profile details from Business Card");

					waitForSpinnerToDisappear(10);

					newLName = profileMap.get("Name")+rand_int1;
					newFName = profileMap.get("FirstName")+rand_int1;

					//New value in Last Name
					textBox("Profile.ProfideDetails_Link_Name", newLName);
					Utils.tabKey("Profile.ProfideDetails_Link_Name");
					waitForSpinnerToDisappear(30);
					logger.log(LogStatus.INFO,"Entered new Last name for the profile-> " + newLName);

					//New value in First Name
					textBox("Profile.ProfideDetails_Link_FirstName", newFName);
					Utils.tabKey("Profile.ProfideDetails_Link_FirstName");
					waitForSpinnerToDisappear(30);
					logger.log(LogStatus.INFO,"Entered new First name for the profile-> " + newFName);

					waitForSpinnerToDisappear(10);

					click("Profile.profile_save_btn");

					waitForSpinnerToDisappear(40);
					
					click("Profile.profileDetailsLink_Close");
					
					waitForSpinnerToDisappear(40);
				

				List ll = driver.findElements(By.xpath("//a/span[contains(.,'"+newFName+"') and contains(.,'"+newLName+"')]"));
				if(ll.size()>0) {
					UpdateProfileName = driver.findElement(By.xpath("//a/span[contains(.,'"+newFName+"') and contains(.,'"+newLName+"')]")).getText();
					logger.log(LogStatus.PASS,"Profile name is updated successfully :"+ UpdateProfileName);
				}
				else {
					logger.log(LogStatus.FAIL,"Profile name is not updated successfully :"+ UpdateProfileName);
				}

			} catch (Exception e) {
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				Utils.takeScreenshot(driver, testClassName);
				LoginPage.Logout();
				throw (e);
			}

		}
		
		/*******************************************************************
	    - Description: This method update the profile
		- Input:FirstName,Address,Country,VIP,Currency,Language
		- Output:
		- Dilip
		- Date: 1/11/2018
		- Revision History:
		                - Change Date
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By
		 ********************************************************************/
		
		public static void updateProfileDetailsFromEdit(HashMap<String, String> profileMap) throws Exception {

			String scriptName = Utils.getMethodName();
			System.out.println("Name: " + scriptName);		 
			System.out.println("Map: " + profileMap);


			try {
				
				
				//Clicking on Edit
				click("Profile.linkEdit");
				logger.log(LogStatus.PASS, "Clicking edit link");

				//Enter First Name
				waitForSpinnerToDisappear(50);
				textBox("Profile.txtFirstName", profileMap.get("FirstName"));
				logger.log(LogStatus.PASS, "Entering FirstName :" + profileMap.get("FirstName"));
				
				//Enter title
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtTitle", profileMap.get("Title"));
				logger.log(LogStatus.PASS, "Entering Title :" + profileMap.get("Title"));
						
				//Entering VIPStatus
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtVIPStatus", profileMap.get("VIP"));
				logger.log(LogStatus.PASS, "Entering VIP :" + profileMap.get("VIP"));
				
							
				//Entering Currency
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtCurrency", profileMap.get("Currency"));
				logger.log(LogStatus.PASS, "Entering Currency :" + profileMap.get("Currency"));
				
				//Entering Language
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtLanguage", profileMap.get("Language"));
				logger.log(LogStatus.PASS, "Entering Language :" + profileMap.get("Language"));
				
				//Entering AddressType
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtAddressType", profileMap.get("AddressType"));
				logger.log(LogStatus.PASS, "Entering AddressType :" + profileMap.get("AddressType"));
				
				//Entering Address1
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtAddress1", profileMap.get("Address1"));
				logger.log(LogStatus.PASS, "Entering Address1 :" + profileMap.get("Address1"));
				
				//Entering Address2
				jsTextbox("Profile.txtAddress2", profileMap.get("Address2"));
				logger.log(LogStatus.PASS, "Entering Address2 :" + profileMap.get("Address2"));
				
				//Entering Address3
				jsTextbox("Profile.txtAddress3", profileMap.get("Address3"));
				logger.log(LogStatus.PASS, "Entering Address3 :" + profileMap.get("Address3"));
				
				//Entering Address4
				jsTextbox("Profile.txtAddress4", profileMap.get("Address4"));
				logger.log(LogStatus.PASS, "Entering Address4 :" + profileMap.get("Address4"));
				
				//Entering City
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtCity", profileMap.get("City"));
				logger.log(LogStatus.PASS, "Entering City :" + profileMap.get("City"));
				
				//Entering Country
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtCountry", profileMap.get("Country"));
				logger.log(LogStatus.PASS, "Entering Country :" + profileMap.get("Country"));
				
				
				//Entering State
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtState", profileMap.get("State"));
				logger.log(LogStatus.PASS, "Entering State :" + profileMap.get("State"));
				
				//Entering PostalCode
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtPostalCode", profileMap.get("PostalCode"));
				logger.log(LogStatus.PASS, "Entering PostalCode :" + profileMap.get("PostalCode"));
				
				//Entering PostalCodeExt
				waitForSpinnerToDisappear(50);
				jsTextbox("Profile.txtPostalCodeExt", profileMap.get("PostalCodeExt"));
				logger.log(LogStatus.PASS, "Entering PostalCodeExt :" + profileMap.get("PostalCodeExt"));
						
				
				//Clicking save button
				jsClick("Profile.profile_save_btn");
				logger.log(LogStatus.PASS, "Click save button");
				
				
				waitForSpinnerToDisappear(100);
				if (getText("Profile.label_ManageProfiles_Name").contains(profileMap.get("FirstName"))) {
				
					logger.log(LogStatus.PASS,"FirstName is updated successfully Excepted >>"+profileMap.get("FirstName")+"<< Actual >>"+getText("Profile.label_ManageProfiles_Name")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"FirstName is not updated successfully Excepted >>"+profileMap.get("FirstName")+"<< Actual >>"+getText("Profile.label_ManageProfiles_Name")+"<<");
				}
				
				if (getText("Profile.labelTitle").contains(profileMap.get("Title"))) {
					
					logger.log(LogStatus.PASS,"Title is updated successfully Excepted >>"+profileMap.get("Title")+"<< Actual >>"+getText("Profile.labelTitle")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"Title is not updated successfully Excepted >>"+profileMap.get("Title")+"<< Actual >>"+getText("Profile.labelTitle")+"<<");
				}
				
				if (getText("Profile.labelLanguage").equals(profileMap.get("Language"))) {
					
					logger.log(LogStatus.PASS,"Language is updated successfully Excepted >>"+profileMap.get("Language")+"<< Actual >>"+getText("Profile.labelLanguage")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"Language is not updated successfully Excepted >>"+profileMap.get("Language")+"<< Actual >>"+getText("Profile.labelLanguage")+"<<");
				}
		
				if (getText("Profile.labelVIP").equals(profileMap.get("VIP"))) {
					
					logger.log(LogStatus.PASS,"VIP is updated successfully Excepted >>"+profileMap.get("VIP")+"<< Actual >>"+getText("Profile.labelVIP")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"VIP is not updated successfully Excepted >>"+profileMap.get("VIP")+"<< Actual >>"+getText("Profile.labelVIP")+"<<");
				}
				
				if (getText("Profile.labelCurrency").equals(profileMap.get("Currency"))) {
					
					logger.log(LogStatus.PASS,"Currency is updated successfully Excepted >>"+profileMap.get("Currency")+"<< Actual >>"+getText("Profile.labelCurrency")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"Currency is not updated successfully Excepted >>"+profileMap.get("Currency")+"<< Actual >>"+getText("Profile.labelCurrency")+"<<");
				}
				
				if (getText("Profile.labelAddressType").equals(profileMap.get("AddressType"))) {
					
					logger.log(LogStatus.PASS,"AddressType is updated successfully Excepted >>"+profileMap.get("AddressType")+"<< Actual >>"+getText("Profile.labelAddressType")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"AddressType is not updated successfully Excepted >>"+profileMap.get("AddressType")+"<< Actual >>"+getText("Profile.labelAddressType")+"<<");
				}
				
				String addressDetails=profileMap.get("Address1")+","+profileMap.get("Address2")+","+profileMap.get("Address3")+","+profileMap.get("Address4");
				//			logger.log(LogStatus.PASS,"Address details :"+addressDetails);
				
				if (getText("Profile.labelAddress").equals(addressDetails)) {
					
					logger.log(LogStatus.PASS,"Address details is updated successfully Excepted >>"+addressDetails+"<< Actual >>"+getText("Profile.labelAddress")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"Address deatils is not updated successfully Excepted >>"+addressDetails+"<< Actual >>"+getText("Profile.labelAddress")+"<<");
				}
				
				String countryDetails=profileMap.get("City")+","+profileMap.get("State")+","+profileMap.get("Country")+","+profileMap.get("PostalCode");
				//			logger.log(LogStatus.PASS,"country Details :"+countryDetails);
				
				if (getText("Profile.labelCountryDetails").equals(countryDetails)) {
					
					logger.log(LogStatus.PASS,"Country, State, City and Postal Code is updated successfully Excepted >>"+countryDetails+"<< Actual >>"+getText("Profile.labelCountryDetails")+"<<");
					
				} else {
					
					logger.log(LogStatus.FAIL,"Country, State, City and Postal Code is not updated successfully Excepted >>"+countryDetails+"<< Actual >>"+getText("Profile.labelCountryDetails")+"<<");
				}
				
			} catch (Exception e) {
				try {
					Alert alert = driver.switchTo().alert();
					String alertText = alert.getText();
					logger.log(LogStatus.FAIL, "Profile is not updated successfullty :: Failed " + alertText);
					logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
					System.out.println("Alert data: " + alertText);
					alert.accept();
				} catch (NoAlertPresentException ex) {
					ex.printStackTrace();
				}
				Utils.takeScreenshot(driver, scriptName);
				logger.log(LogStatus.FAIL, "Profile is not updated successfullty :: Failed " + e.getLocalizedMessage());
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				throw (e);

			}
		}
		
		
		
		/*******************************************************************
	    - Description: This method searchTravelAgentProfileFromCommissions
		- Input: Client ID
		- Output:
		- Author:Dilip
		- Date: 02/04/2018
		- Revision History:
		                - Change Date
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By
		 ********************************************************************/



		public static void searchTravelAgentProfileFromCommissions(HashMap<String, String> profileMap) throws Exception {

			String scriptName = Utils.getMethodName();
			System.out.println("name: " + scriptName);
			System.out.println("Map: " + profileMap);

		
			try {

				// Navigating to OperaCloudMenu_Financials
				click("Profile.OperaCloudMenu_Financials", 100, "clickable");

				// Navigating to OperaCloudMenu_Financials_Commission
				click("Profile.OperaCloudMenu_Financials_Commission", 100, "clickable");

				// Navigating to OperaCloudMenu_Financials_Commission_TravelAgent
				click("Profile.OperaCloudMenu_Financials_Commission_TravelAgent", 100, "clickable");
				
					
				// Entering the Client ID
				waitForSpinnerToDisappear(50);
				textBox("Profile.ClientId",profileMap.get("IATANumber"), 100, "presence");

				// Click search
				jsClick("Profile.manage_profile_basicSearch_searchBtn", 100, "clickable");

				//Validate Search 
				waitForSpinnerToDisappear(100);
				if (getText("Profile.labelIATANumber").equals(profileMap.get("IATANumber"))) {
					
					logger.log(LogStatus.PASS,"Search results found :: Actual >>"+getText("Profile.labelIATANumber")+"<< :: Excepted >>"+profileMap.get("IATANumber")+"<<");
				}else{
				
					logger.log(LogStatus.FAIL,"Search results not found :: Actual >>"+getText("Profile.labelIATANumber")+"<< :: Excepted >>"+profileMap.get("IATANumber")+"<<");
				}

				


			} catch (Exception e) {
				try {
					Alert alert = driver.switchTo().alert();
					String alertText = alert.getText();
					logger.log(LogStatus.FAIL, "Search Travel Agent Profile from Commission  :: Failed " + alertText);
					logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
					System.out.println("Alert data: " + alertText);
					alert.accept();
				} catch (NoAlertPresentException ex) {
					ex.printStackTrace();
				}
				Utils.takeScreenshot(driver, scriptName);
				logger.log(LogStatus.FAIL, "Travel agent profile not found :: Failed " + e.getLocalizedMessage());
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				throw (e);

			}

		}
	
		/*******************************************************************
		- Description: To Adding images for a profile
		- Input:Client ID, new communication details
		- Output:
		- Author:Swati
		- Date: 12/19/2018
		- Revision History:
		                - Change Date
		                - Change Reason
		                - Changed Behavior
		                - Last Changed By
	 ********************************************************************/

	public static void AddImageToAProfile(HashMap<String, String> profileMap) throws Exception {


		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String ClientID="";
		String communicationValue="";


		try {
			Screen screen = new Screen();
			
			//Code to Add Image to a Profile
			click("Profile.LinkMore");
			waitForSpinnerToDisappear(30);

			click("Profile.AddImage");
			waitForSpinnerToDisappear(30);
			logger.log(LogStatus.INFO, "Clicking on Add Image Link");

			click("Profile.ChooseImage");
			waitForSpinnerToDisappear(30);
			logger.log(LogStatus.INFO, "Clicking on choose Image Link");
			Thread.sleep(5000);

			screen.write(OR.getImage(profileMap.get("ImageTitle")));

			screen.click(OR.getImage("OpenButton.png"));
			waitForSpinnerToDisappear(30);

			click("Profile.SaveImage");
			
			waitForSpinnerToDisappear(30);
			
			System.out.println("xpath" + "//img[contains(@src,'"+profileMap.get("ImageTitle")+"')]");
			
			List<WebElement> ele = driver.findElements(By.xpath("//img[contains(@src,'"+profileMap.get("ImageTitle")+"')]"));
			
			if (!(ele.size()<1)) {
				logger.log(LogStatus.PASS, "Image is uploaded for the profile properly");
				System.out.println("Image is uploaded for the profile properly");
				
			} else {
				logger.log(LogStatus.FAIL, "Image is not uploaded for the profile properly");
				System.out.println("Image is not uploaded for the profile properly");
			}
		
//			if(Utils.isExists("//img[contains(@src,'"+profileMap.get("ImageTitle")+"')]")){
//				logger.log(LogStatus.PASS, "Image is uploaded for the profile properly");
//			}
//			
//			else {
//				logger.log(LogStatus.FAIL, "Image is uploaded for the profile properly");
//			}
//			
			
			//Code to Edit Image to a Profile
			
			click("Profile.LinkMore");
			waitForSpinnerToDisappear(30);

			jsClick("Profile.ChangeImage");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.INFO, "Clicking on Change Image Link");
			
			click("Profile.ChooseImage");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.INFO, "Clicking on choose Image Link");

			Thread.sleep(5000);
			screen.write(OR.getImage(profileMap.get("NewImage")));
		

			screen.click(OR.getImage("OpenButton.png"));
			waitForSpinnerToDisappear(30);

			click("Profile.SaveImage");
			waitForSpinnerToDisappear(30);
			
			List<WebElement> ele1 = driver.findElements(By.xpath("//img[contains(@src,'"+profileMap.get("NewImage")+"')]"));
			
			if (!(ele1.size()<1)) {
				logger.log(LogStatus.PASS, "Image is updated for the profile properly");
				
			} else {
				logger.log(LogStatus.FAIL, "Image is not updated for the profile properly");
			}
			
			//Code to Delete Image to a Profile
			
			click("Profile.LinkMore");
			waitForSpinnerToDisappear(30);
			
			click("Profile.DeleteImage");
			waitForSpinnerToDisappear(50);
			logger.log(LogStatus.INFO, "Clicking on Delete Image Link");
			
			
			if(Utils.isExists("Profile.ConfirmationPopUp")) {
				
				click("Profile.ButtonLinkDelete");
				waitForSpinnerToDisappear(50);
				logger.log(LogStatus.INFO, "Confirmation popup is displayed");
			}
			
			else {
				logger.log(LogStatus.FAIL, "Confirmation Pop Up is not displayed");
			}
			
			List<WebElement> ele3 = driver.findElements(By.xpath("//img[contains(@src,'"+profileMap.get("NewImage")+"')]"));
			
		
			if (!(ele3.size()<1)) {
				logger.log(LogStatus.FAIL, "Image is not deleted for the profile properly");
				
			} else {
				logger.log(LogStatus.PASS, "Image is deleted for the profile properly");
			}
			

		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			Utils.takeScreenshot(driver, testClassName);
			LoginPage.Logout();
			throw (e);
		}

	}
	
	public static void lastNamefirstNameadvanceprofileSearch(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + profileMap);

		String ClientId2 = "";
		String communicationValue = "";

		try {
			// Creating first guest profile
			  ProfilePage.createGuestProfile(profileMap, "SaveAdd");
			  click("Profile.backtocreateGuestProfile", 200, "clickable");
			  
			  // Click I Want To link 
			  click("Profile.link_ManageProfiles_IWantTo", 300,  "clickable");
			  		  
			  // Click Guest profile link
			  click("Profile.create_guest_profile", 100,  "clickable");
			  
			  waitForSpinnerToDisappear(60);
			  
			  // Entering the Name
			  textBox("Profile.txt_ManageProfiles_Name",profileMap.get("SecondprofileName"), 100,
			  "presence");
			  Utils.tabKey("Profile.txt_ManageProfiles_Name");
			  waitForSpinnerToDisappear(20);
			  textBox("Profile.ContactProfile.FirstName",profileMap.get("SecondprofileFirstName"), 100,
					  "presence");
				Utils.tabKey("Profile.ContactProfile.FirstName");
			  waitForSpinnerToDisappear(40);
			  
			  // Entering the Communication Value
			  communicationValue=profileMap.get("CommunicationValue");
			  jsTextbox("Profile.txt_ManageProfiles_CommunicationValue",communicationValue)
			  ;
			  
			  Thread.sleep(10);
			  driver.findElement(By.xpath("//*[@data-ocid='TXT_CNTNT_ADDRESS1']")).sendKeys
			  (profileMap.get("Address2"));
			  
			  waitForSpinnerToDisappear(40);
			  
			  jsClick("Profile.btn_ManageProfiles_SaveAndMoreDetails", 100, "clickable");
			  logger.log(LogStatus.PASS, "Clicking on Save and more details button");
			  ProfilePage.profilefirstLastNameSearch(profileMap);
		
		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			// Utils.takeScreenshot(driver, testClassName);
			throw (e);
		}
	}
	
	public static void profilefirstLastNameSearch(HashMap<String, String> profileMap) throws Exception {

		String testClassName = Utils.getClassName();
		String methodName = Utils.getMethodName();
		String ClientID="";
		String communicationValue="";
		String name = "";
		String FirstName = "";
		String Title = "";
		String newT = "";

		name = profileMap.get("Name");
		if(name==null) {
			name = "";
		}

		FirstName = profileMap.get("FirstName");
		if(FirstName==null) {
			FirstName = "";
		}


		Title = profileMap.get("Title");
		if(Title==null) {
			Title = "";
		}
		else {
			newT = Title.toUpperCase();
		}



		System.out.println("profile Name" + profileMap.get("Name"));

		System.out.println("Client ID" + profileMap.get("ClientID"));

		try {

			//Navigating to Client Relations
			click("Profile.OperaCloudMenu_ClientRelations", 100, "clickable");

			// Navigating to Profiles
			click("Profile.OperaCloudMenu_ClientRelations_Profiles", 100, "clickable");

			// Navigating to Manage Profile
			click("Profile.OperaCloudMenu_ClientRelations_Profiles_ManageProfile", 100, "clickable");

			//Checking the presence of Manage profile page
			waitForSpinnerToDisappear(10);


			if(Utils.isExists("Profile.SearchAdvancedLink")) {
				Utils.element("Profile.SearchAdvancedLink").click();
			}

			//If the profile name contains clientId key, search needs to be performed according to that
			if(profileMap.containsKey("ClientID")) {
				Utils.textBox("Profile.ClientId", profileMap.get("ClientID"));
				Utils.tabKey("Profile.ClientId");
			}

			//If the profile name contains Last Name, search needs to be performed according to that
			if(profileMap.containsKey("Name")) {
				textBox("Profile.txt_ManageProfiles_Name",profileMap.get("Name"), 100, "presence");
				Utils.tabKey("Profile.txt_ManageProfiles_Name");
				waitForSpinnerToDisappear(30);
			}

		
		  //If the profile name contains First Name, search needs to be performed according to that 
		  
		  if(profileMap.containsKey("FirstName")){
		  textBox("Profile.ContactProfile.FirstName",profileMap.get("FirstName"), 100,
		  "presence"); Utils.tabKey("Profile.ContactProfile.FirstName");
		  waitForSpinnerToDisappear(30); }
		 

			click("Profile.manage_profile_basicSearch_searchBtn",10,"clickable");
			waitForSpinnerToDisappear(10);

			List<WebElement> rows = Utils.elements("Profile.TableData"); 

			System.out.println("rows" + rows.size());
			
			
			
			for (int i = 1; i <= rows.size(); i++) {

				if (driver.findElement(By.xpath("//a/span[contains(.,'"+name+"') and contains(.,'"+FirstName+"') and contains(.,'"+newT+"')]"))
						.getText().equals(profileMap.get("Name")+","+" "+profileMap.get("FirstName"))) {
					System.out.println(i + " " + "Profile Name Value is validated successsfully :"
							+ profileMap.get("Name")+","+" "+profileMap.get("FirstName"));
					logger.log(LogStatus.PASS, i + " " + "Profile Name Value is validated successsfully :"
							+ profileMap.get("Name")+","+" "+profileMap.get("FirstName"));
				} else {
					System.out.println(i + " " + "Profile Name Value is not validated successsfully :"+profileMap.get("Name")+","+" "+profileMap.get("FirstName"));
					logger.log(LogStatus.FAIL,
							i + " " + "Profile Name Value is not validated successsfully :"+profileMap.get("Name")+","+" "+profileMap.get("FirstName"));
				}
			}

			

						waitForSpinnerToDisappear(10);
			
		} catch (Exception e) {
			Utils.takeScreenshot(driver, testClassName);
			logger.log(LogStatus.FAIL, "Profile Super Search not done :: Failed " + e.getMessage());
			throw (e);
		}
	}
	
	public static void abilityTochangeViewOptionsPropertyscreen(HashMap<String, String> configMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + profileMap);

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
			  mouseHover("Configuration.menu_Inventory");
			  click(Utils.element("Configuration.menu_Inventory"));
			  System.out.println("Clicked Inventory menu"); 
			  logger.log(LogStatus.PASS, "Selected Inventory Menu");
			  waitForSpinnerToDisappear(20);
			
			  mouseHover("Configuration.roomManagement");
			  waitForSpinnerToDisappear(10);
			  click(Utils.element("Configuration.roomManagement"));
			  waitForSpinnerToDisappear(20);
			  logger.log(LogStatus.PASS,"Clicking on Room Management from Inventory Menu");
			  
			  jsClick("Configuration.housekeepingBoard", 100, "clickable");
			  waitForSpinnerToDisappear(40);
			  logger.log(LogStatus.PASS,"Clicking On House Keeping Board");
			  
			//clear("Configuration.houseBoardingroomType");
			 textBox("Configuration.houseBoardingroomType",configMap.get("ROOM_CATEGORY"), 100,
					  "presence");
			 Utils.tabKey("Configuration.houseBoardingroomType");
			 waitForSpinnerToDisappear(20);
			 logger.log(LogStatus.PASS, "Provided Room TYpe - " + configMap.get("ROOM_CATEGORY"));
			 click(Utils.element("Configuration.searchButton"));	  
			 waitForSpinnerToDisappear(40);
			 
			List<WebElement>  houseKeepingBoardTable = Utils.elements("Configuration.houseBoardTable");
			int rowhouseKeepingBoardTable = houseKeepingBoardTable.size();
			System.out.println(rowhouseKeepingBoardTable);
			Map<String, Integer> houseKeepingMap = new HashMap<String, Integer>();
			
			houseKeepingMap.put("rownumberavailable", houseKeepingBoardTable.size());
			
			  boolean flaginspected = Utils.isSelected("Configuration.inspectedCheckBox","Inspected CheckBox is already selected");
			  if(flaginspected) {
			  click(Utils.element("Configuration.inspectedCheckBox"));
			  waitForSpinnerToDisappear(10); 
			  }
			  boolean flagClean = Utils.isSelected("Configuration.cleanCheckBox","Clean CheckBox is already selected");
			  if(flagClean) {
			  click(Utils.element("Configuration.cleanCheckBox"));
			  waitForSpinnerToDisappear(10);
			  }
			  boolean flagPickUp = Utils.isSelected("Configuration.pickupCheckBox","PickUp CheckBox is already selected");
			  if(flagPickUp) {
			  click(Utils.element("Configuration.pickupCheckBox"));
			  waitForSpinnerToDisappear(10); 
			  } 
			  boolean flagDirty = Utils.isSelected("Configuration.dirtyCheckBox", "Dirty CheckBox is already selected");
			  if(flagDirty) {
			  click(Utils.element("Configuration.dirtyCheckBox"));
			  waitForSpinnerToDisappear(10); }
			
			 boolean flagoutoforder = Utils.isSelected("Configuration.outoforderCheckBox", "OutOfOrder CheckBox is already selected");
			 if(!flagoutoforder) {
				 click(Utils.element("Configuration.outoforderCheckBox"));
				 waitForSpinnerToDisappear(10);
			 }
			 boolean flagoutofservice = Utils.isSelected("Configuration.outofserviceCheckBox", "OutOfService CheckBox is already selected");
			 if(!flagoutofservice) {
				 click(Utils.element("Configuration.outofserviceCheckBox"));
				 waitForSpinnerToDisappear(10);
			 }
			 
			 click(Utils.element("Configuration.searchButton"));	  
			 waitForSpinnerToDisappear(40);
			 
			 
			 	List<WebElement>  aftercheckhouseKeepingBoardTable = Utils.elements("Configuration.houseBoardTable");
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
				
			
			  // Navigating to Inventory menu 
			  //WebdriverWait(100, "Configuration.menu_Inventory", "clickable");
			  mouseHover("Configuration.menu_Inventory");
			  click(Utils.element("Configuration.menu_Inventory"));
			  System.out.println("Clicked Inventory menu"); 
			  logger.log(LogStatus.PASS, "Selected Inventory Menu");
			  waitForSpinnerToDisappear(20);
			
			  click(Utils.element("Configuration.propertyAvailability"));
			  waitForSpinnerToDisappear(20);
			  logger.log(LogStatus.PASS, "Clicked on Property Availabilty");
			  
			  textBox("Configuration.txt_SearchRoomTypes_RoomType",configMap.get("ROOM_CATEGORY"), 100,
					  "presence");
			 Utils.tabKey("Configuration.txt_SearchRoomTypes_RoomType");
			 waitForSpinnerToDisappear(20);
			 logger.log(LogStatus.PASS, "Provided Room TYpe - " + configMap.get("ROOM_CATEGORY"));
			 click(Utils.element("Configuration.searchButton"));	  
			 waitForSpinnerToDisappear(40);
			 
			// if(Utils.getTextValue("Configuration.roomType").equals(arg0))

			 if(getText("Configuration.roomType",0, "visible").equals(configMap.get("ROOM_CATEGORY"))){
				 Assert.assertEquals(getText("Configuration.roomType", 100, "presence"), "STDK");
				 logger.log(LogStatus.PASS, " Room type is present in below table:::"+configMap.get("ROOM_CATEGORY"));
				 
			 
			 click(Utils.element("Configuration.linkViewOptions"));	  
			 waitForSpinnerToDisappear(40);
			 
			boolean flagOutOfOrder = Utils.isSelected("Configuration.outOfOrderRooms", "Out of Order is already selected");
			if(!flagOutOfOrder) {
				click(Utils.element("Configuration.outOfOrderRooms"));
				waitForSpinnerToDisappear(10);
			}
			boolean flagOutOfService = Utils.isSelected("Configuration.outOfServiceRooms", "Out of Service is already selected");
			 if(!flagOutOfService) {
				 click(Utils.element("Configuration.outOfServiceRooms"));
				 waitForSpinnerToDisappear(10);
			 }
			 
			 click(Utils.element("Configuration.okButton"));	
			 waitForSpinnerToDisappear(40);
			 
			String ele = getText("Configuration.newOrder", 100, "presence");
			System.out.println(ele);
			
			String rownumberavailable = getText("Configuration.availableRoomCount", 100, "presence");
			System.out.println(rownumberavailable);
			
			String rownumberoutOfOrder = getText("Configuration.outOfOrderRoomCount", 100, "presence");
			System.out.println(rownumberoutOfOrder);
			
			String rownumberoutOfService = getAttributeOfElement("Configuration.outOfServiceRoomCount", "innerHTML", 100, "presence");
			System.out.println(rownumberoutOfService);
			
			String rownumberoutOfService2=getAttributeOfElement("Configuration.outOfServiceRoomCount", "value", 100, "presence");
			
			String row3 = driver.findElement(By.xpath("(//*[text()=\"Out of Service Rooms\"]//following::span/div/div[2]/span)[1]")).getAttribute("innerHTML");

			
			String rownumberoutOfService3=getAttributeOfElement("Configuration.outOfServiceRoomCount", "innerHTML", 100, "presence");
			
			//getAttributeOfElement("Configuration.outOfServiceRoomCount", attribute, timeout, option)
			
			//List ele2 = Utils.getAllValuesFromTableBasedOnColumnName("Configuration.columnName");
			
			List<WebElement> ele3 = Utils.elements("Configuration.outOfServiceRoomCount");
		
			System.out.println(ele3.get(0).getText());
			
			Map<String,Integer> inventoryMap = new HashMap<String, Integer>();
			inventoryMap.put("rownumberavailable", Integer.parseInt(rownumberavailable));
			inventoryMap.put("rownumberoutOfOrder", Integer.parseInt(rownumberoutOfOrder));
			inventoryMap.put("rownumberoutOfService", Integer.parseInt(rownumberoutOfService3));
			
			System.out.println(inventoryMap);
			
			
			//*[@data-ocid='SDH1']//*[text()='Available Rooms']
			 Assert.assertEquals(getText("Configuration.availableRooms", 100, "presence"), "Available Rooms");
			 logger.log(LogStatus.PASS, "Rooms are Avaialable in table");
			 Assert.assertEquals(getText("Configuration.rowoutofService", 100, "presence"), "Out of Service Rooms");
			 logger.log(LogStatus.PASS, "Out of Service Rooms are available");
			 Assert.assertEquals(getText("Configuration.rowoutofOrder", 100, "presence"), "Out of Order Rooms");
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
	
	
	public static void AddIcongnitoAlternateName(HashMap<String, String> profileMap) throws Exception {

		String scriptName = Utils.getMethodName();
		System.out.println("name: " + scriptName);
		System.out.println("Map: " + profileMap);


		try {
			
			//Editing profile from business card
			
			click("Profile.EditLink_profiledetailsPage");
			waitForSpinnerToDisappear(250);
			
			logger.log(LogStatus.INFO,"Editing profile details from Business Card");
			
			if(Utils.isExists("Profile.AlternateName_Link"))
			{
				click("Profile.AlternateName_Link");
				waitForSpinnerToDisappear(100);
				
				textBox("Profile.AlternateName", profileMap.get("Alternate_Name"));
				Utils.tabKey("Profile.AlternateName");
				waitForSpinnerToDisappear(100);
				
				click("Profile.AlternateNameSave");
				waitForSpinnerToDisappear(100);
				
			}
			else{
				logger.log(LogStatus.FAIL, "Alternate Name link is not present in the profile details section");
				
			}
			
			
			
			if(Utils.isExists("Profile.IncognitoName_Link"))
			{
				click("Profile.IncognitoName_Link");
				waitForSpinnerToDisappear(100);
				
				textBox("Profile.IncognitoName", profileMap.get("IncognitoName"));
				Utils.tabKey("Profile.IncognitoName");
				waitForSpinnerToDisappear(100);
				
				click("Profile.IncognitoSave");
				waitForSpinnerToDisappear(100);
				
			}
			else{
				logger.log(LogStatus.FAIL, "Incognito Name link is not present in the profile details section");
				
			}
			
			click("Profile.Save");
			waitForSpinnerToDisappear(100);
			
			
			// Validating profile name
	String profilename="";
	List flag = driver.findElements(By.xpath("//a/span[contains(.,'"+profileMap.get("Alternate_Name")+"')]")); 
	System.out.println(driver.findElement(By.xpath("//a/span[contains(.,'"+profileMap.get("Alternate_Name")+"')]")).getText());
	if(flag.size()>0) {
	profilename = driver.findElement(By.xpath("//a/span[contains(.,'"+profileMap.get("Alternate_Name")+"')]")).getText();
	logger.log(LogStatus.PASS,"Alternate name is validated successfully :"+ profilename);
	System.out.println("Alternate name  is validated");
	}
	else {
		logger.log(LogStatus.FAIL,"Alternate name is not validated successfully :"+ profilename);
		}
	
	
	
	System.out.println("" + "//span[contains(@title,'"+profileMap.get("IncognitoName")+"')]");
	String incog = "";
	
	try{
		System.out.println("in try");
		incog = driver.findElement(By.xpath("//span[contains(@title,'"+profileMap.get("IncognitoName")+"')]")).getAttribute("title");
		System.out.println("incognito" + incog);
		if(incog.equals(profileMap.get("IncognitoName"))){
			logger.log(LogStatus.PASS,"Incognito name is validated successfully :"+ incog);
		}
		else {
			logger.log(LogStatus.FAIL,"Incognito name is not validated successfully :"+ incog);
		}
	}
	catch(Exception e){
		logger.log(LogStatus.FAIL,"Incognito name does not exist :"+ profilename);
		incog = driver.findElement(By.xpath("//span[contains(@title,'"+profileMap.get("IncognitoName")+"')]")).getAttribute("innerHTML");
		System.out.println("incognito" + incog);
	}
		}
		catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				String alertText = alert.getText();
				logger.log(LogStatus.FAIL, "Adding notes to Profile  :: Failed " + alertText);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
				System.out.println("Alert data: " + alertText);
				alert.accept();
			} catch (NoAlertPresentException ex) {
				ex.printStackTrace();
			}
			Utils.takeScreenshot(driver, scriptName);
			logger.log(LogStatus.FAIL, "Adding notes to Profile :: Failed " + e.getLocalizedMessage());
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, scriptName)));
			throw (e);

		}
	}
	

	
}
