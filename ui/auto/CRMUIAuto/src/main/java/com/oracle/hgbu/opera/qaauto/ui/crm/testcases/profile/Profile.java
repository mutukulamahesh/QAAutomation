package com.oracle.hgbu.opera.qaauto.ui.crm.testcases.profile;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.aventstack.extentreports.model.Log;
import com.oracle.hgbu.opera.qaauto.ui.crm.component.profile.ProfilePage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

/**
 * <p>
 * <b> This Class provides the code for Profile page funtions.</b>
 * @author mmutukul
 * </p>
 */

public class Profile extends Utils {
	
	
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
	
	@Test(groups = {"SANITY"},priority = 1)
    public void createSourceProfile() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            ProfilePage.createSourceProfile(profileMap);
          
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Source Profile not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
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


	@Test(groups = {"SANITY"},priority = 2)
    public void createTravelAgentProfile() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            ProfilePage.createTravelAgentProfile(profileMap);
           
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Travel Agent Profile not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/**************************************************************************************
    - Description: To create a Guest profile using Save option
	- Input:Profile Name , communication type , communication value, address, Option as Save
	- Output: 
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 *****************************************************************************************/

	@Test(groups = {"BAT"},priority = 3)
    public void createGuestProfileUsingSave() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create guest profile using save button </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createGuestProfile");
            ProfilePage.createGuestProfile(profileMap,"Save");

            
            	Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Guest Profile not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/**************************************************************************************
    - Description: To create a Guest profile using Save option
	- Input:Profile Name , communication type , communication value, address, Option as SaveandAdd
	- Output: 
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 *****************************************************************************************/
	
	@Test(groups = {"BAT"},priority = 4)
	public void createGuestProfileUsingSaveAdd() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create guest profile using save and add more details button </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createGuestProfile");
            ProfilePage.createGuestProfile(profileMap,"SaveAdd");

            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Guest Profile not created through save and add option:: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
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
	
	@Test(groups = {"BAT"},priority = 5)
	public void searchProfileUsingAdvancedsearch() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create search profile using advanced search </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createGuestProfile");
            ProfilePage.profileAdvancedSearch(profileMap);

            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Profile not searched through advanced search :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/*******************************************************************
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
	 ********************************************************************/

	
	@Test(groups = {"BAT"},priority = 6)
	public void searchProfileUsingBasicsearch() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create search profile using basic search </b>");
			//logger = report.startTest(methodName, "Verify able to Create search profile using basic search").assignCategory("acceptance", "Cloud.Profile");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createGuestProfile");
            ProfilePage.profileBasicSearch(profileMap);

            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Profile not searched through basic search :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	/*******************************************************************
	- Description: To update profile and communication details for a profile from business card 
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
	@Test(groups = {"BAT"},priority = 7)
	public void UpdateProfile() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to update communication details for a profile </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
           
            ProfilePage.updateProfileDetails(profileMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Profile details and communication not updated from business card:: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	/*******************************************************************
    - Description: To create Company Profile 
	- Input:Client ID
	- Output:
	- Author:Chittranjan
	- Date: 12/27/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 8)
    public void createCompanyProfileUsingSaveAndAddDetails() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create Company profile </b>");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createProfileCompany");
            ProfilePage.createCompanyProfile(profileMap);
           	Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Company Profile not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/*******************************************************************
    - Description: To create ContactProfile 
	- Input:Client ID
	- Output:
	- Author:Chittranjan
	- Date: 12/27/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"BAT"},priority = 9)
    public void createContactProfileUsingSaveAndAddDetails() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify able to Create Contact profile </b>");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createContactProfile");
            ProfilePage.createContactProfile(profileMap);
           	Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Contact Profile not created :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/*******************************************************************
	- Description: To add preferences for a profile
	- Input:Preferences
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"},priority = 10)
	public void AddPreferencesToaProfile() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to update communication details for a profile </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            
            ProfilePage.createGuestProfile(profileMap, "SaveAndAdd");
            ProfilePage.AddPreferencesToProfile(profileMap);
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Preferences not added :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }

	/*******************************************************************
	- Description: To add Memberships for a profile
	- Input:Preferences
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"},priority = 11)
	public void AddMembershipToProfile() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to add memberships for a profile </b>");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            
            ProfilePage.createGuestProfile(profileMap, "SaveAndAdd");
            ProfilePage.AddMembershipToProfile(profileMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Memberships not added :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }

	
	/*******************************************************************
	- Description: Verify Merging two profiles
	- Input:Two guest profiles
	- Output:
	- Author:Swati
	- Date: 01/08/2019
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"},priority = 12)
	public void profileMerge() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to merge two profiles </b>");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            
            ProfilePage.profileMerge(profileMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Profiles are not merged :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/*******************************************************************
	- Description: Verify adding notes to profiles
	- Input:Two guest profiles
	- Output:
	- Author:Swati
	- Date: 01/08/2019
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"},priority = 13)
	public void AddNotesToProfile() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to add notes to profiles </b>");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            
            ProfilePage.createGuestProfile(profileMap, "SaveAndAdd");
            ProfilePage.AddNotesToProfile(profileMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Notes are  not added :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/*******************************************************************
	- Description: To add keyword for a profile
	- Input:KeywordType
	- Output:
	- Author:Dilip
	- Date: 01/11/2019
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	@Test(groups = {"SANITY"},priority = 14)
	public void AddKeywordToaProfile() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user is able to add Keyword for a profile </b>");
			//logger = report.startTest(methodName, "Verify user is able to update communication details for a profile").assignCategory("acceptance", "Cloud.Profile");
			HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);

			ProfilePage.createSourceProfile(profileMap);
			ProfilePage.AddKeywordToProfile(profileMap);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Keyword not updated to Profile :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	@Test(groups = {"SANITY"},priority = 15)
	public void IssueECertificateToaProfile() throws Exception {
		String methodName = Utils.getMethodName();
		System.out.println("methodName: " + methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user is able to issue ECertificates for a profile </b>");
			//logger = report.startTest(methodName, "Verify user is able to update communication details for a profile").assignCategory("acceptance", "Cloud.Profile");
			HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);

			ProfilePage.createGuestProfile(profileMap, "SaveAndAdd");
			ProfilePage.IssueECertificatesToProfile(profileMap);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "ECertificates is not issue to profile :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}


	


	/*******************************************************************
	- Description: To add Subscriptions for a profile
	- Input:Database
	- Output:
	- Author:Dilip
	- Date: 01/11/2019
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	@Test(groups = {"SANITY"},priority = 16)
	public void AddSubscriptionsToaProfile() throws Exception {
		String methodName = Utils.getMethodName();
		
		System.out.println("methodName: " + methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user is able to add Subscriptions for a profile </b>");
			
			HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);

			ProfilePage.createSourceProfile(profileMap);
			ProfilePage.AddSubscriptionsToaProfile(profileMap);
			Utils.tearDown();
		}catch (Exception e) {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.FAIL, "Profile not updated :: Failed");
			logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
			logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
			throw (e);
		}
	}
	
	
	/*******************************************************************
	- Description: To update Profile details for a profile through profile details link
	- Input:Client ID, Last name , First Name
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"},priority = 17)
	public void UpdateProfileDetailsLink() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to update communication details for a profile </b>");
			//logger = report.startTest(methodName, "Verify user is able to update communication details for a profile").assignCategory("acceptance", "Cloud.Profile");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","UpdateProfile");
           
            ProfilePage.updateProfileDetails_Link(profileMap);          
            

            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Profile not updated through Profile details link:: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/*******************************************************************
	- Description: To update communication details for a profile through communication details link
	- Input:Client ID, Last name , First Name
	- Output:
	- Author:Swati
	- Date: 12/19/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	@Test(groups = {"SANITY"},priority = 18)
	public void UpdateCommunicationDetailsLink() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to update communication details for a profile </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","UpdateProfile");
            ProfilePage.updateCommunicationDetails_Link(profileMap);

            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "communication details not updated through communication link:: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
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

	@Test(groups = {"SANITY"},priority = 19)
	public void UpdateProfileDetailsFromEdit() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to update profile details from edit </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            ProfilePage.createGuestProfile(profileMap, "SaveAndAdd");
            ProfilePage.updateProfileDetailsFromEdit(profileMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Update profile details from edit:: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	/*******************************************************************
    - Description: This method search travel agent profile from commission
	- Input:Profile Name , communication type , communication value, address and ITATA ID
	- Output:
	- Author:Dilip
	- Date: 02/04/2018
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/
	
	@Test(groups = {"SANITY"},priority = 20)
    public void searchTravelAgentProfileFromCommissions() throws Exception {
        String methodName = Utils.getMethodName();
         
        System.out.println("methodName: " + methodName);
        try {
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createTravelAgentProfile");
            ProfilePage.createTravelAgentProfile(profileMap);            
            String IATANumberFromExcel=ExcelUtils.getCellData(OR.getConfig("Path_ProfileData"), "Profile", "createTravelAgentProfile", "IATANumber");	
            profileMap.put("IATANumber", IATANumberFromExcel);            
            ProfilePage.searchTravelAgentProfileFromCommissions(profileMap);
            Utils.tearDown();
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "searchTravelAgentProfileFromCommissions :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }

	/*******************************************************************
    - Description: This method is used to verify attaching/Editing/deleting images for a profile
	- Input:guest profile,image
	- Output:
	- swati
	- Date: 2/7/2019
	- Revision History:
	                - Change Date
	                - Change Reason
	                - Changed Behavior
	                - Last Changed By
	 ********************************************************************/

	@Test(groups = {"SANITY"},priority = 21)
	public void AddImagetoProfile() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to Adding/Editing/Deleting image is working for a profile </b>");
			
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile","createGuestProfile");
            HashMap<String, String> profileMap1 = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile",methodName);
            
            
            System.out.println("First Map" + profileMap);
            
            System.out.println("Second Map" + profileMap1);
            ProfilePage.createGuestProfile(profileMap, "SaveAdd");
            
            //ProfilePage.profileAdvancedSearch(profileMap);
           
            ProfilePage.AddImageToAProfile(profileMap1);
           
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "Adding/Editing/Deleting an image to a profile is failed:: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	
	@Test(groups = {"SANITY"},priority = 22)
	public void lastNamefirstNameadvanceprofileSearch() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
        	logger.log(LogStatus.INFO, "<b> Verify user is able to search profiles using First and Last Names </b>");
            HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"),"Profile",methodName);
            
            //ProfilePage.createGuestProfile(profileMap, "SaveAdd");
            ProfilePage.lastNamefirstNameadvanceprofileSearch(profileMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "user is able to search profiles using First and Last Names :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	
	@Test(groups = {"SANITY"},priority = 23)
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
            ProfilePage.abilityTochangeViewOptionsPropertyscreen(configMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "user is able to search profiles using First and Last Names :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }
	@Test(groups = {"SANITY"},priority = 24)
	public void AddIcongnitoAlternateName() throws Exception {
        String methodName = Utils.getMethodName();
         
       
        System.out.println("methodName: " + methodName);
        try {
        	Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify user is able to add incognito and alterate name to a profile </b>");
			//logger = report.startTest(methodName, "Verify able to Create Rate code with a restriction").assignCategory("acceptance", "Cloud.Configuration");
			HashMap<String, String> profileMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ProfileData"), "Profile", methodName);

            //ProfilePage.createGuestProfile(profileMap, "SaveAdd");
			ProfilePage.profileBasicSearch(profileMap);
            ProfilePage.AddIcongnitoAlternateName(profileMap);
            Utils.tearDown();
            
        }catch (Exception e) {
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.FAIL, "user is able to search profiles using First and Last Names :: Failed");
            logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
            logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
            throw (e);
        }
    }


	
}

