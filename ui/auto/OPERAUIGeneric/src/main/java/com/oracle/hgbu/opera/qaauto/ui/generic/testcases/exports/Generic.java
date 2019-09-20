package com.oracle.hgbu.opera.qaauto.ui.generic.testcases.exports;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.aventstack.extentreports.model.Log;
import com.oracle.hgbu.opera.qaauto.ui.config.component.ConfigPage;
import com.oracle.hgbu.opera.qaauto.ui.generic.component.exports.GenericPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ReportsClass;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.logger;
import com.relevantcodes.extentreports.LogStatus;

public class Generic extends Utils {
	
	
	@Test(priority = 1,groups = {"BAT"})
	public void createExport() throws Exception {

		
		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Create a Export </b>");
			//logger = report.startTest(methodName, "Verify able to Create a Export").assignCategory("acceptance", "Cloud.Exports");
			GenericPage.ExportsMenu();
				GenericPage.createExport();
			Utils.tearDown();	
		}
		 catch (Exception e) {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, "New Export not created :: Failed");
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
				throw (e);
	}

	
}

	
	@Test(priority = 2,groups = {"BAT"})
	public void GenerateExport() throws Exception {

		String methodName = Utils.getMethodName();
		System.out.println("methodName: "+methodName);
		
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to Generate Export </b>");
			//logger = report.startTest(methodName, "Verify able to Generate Export").assignCategory("acceptance", "Cloud.Exports");
			Wait(2000);
			GenericPage.ExportsMenu();
			GenericPage.GenerateExport();
			//LoginPage.Logout();
			Utils.tearDown();	
		}
		 catch (Exception e) {
				Utils.takeScreenshot(driver, methodName);				
				logger.log(LogStatus.FAIL, "Generate Export is :: Failed");
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
				throw (e);
	}

	
}

	@Test(groups = {"BAT"},priority = 3)
	public void RunReport() throws Exception {
		String methodName = Utils.getMethodName();
		
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
			//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
			GenericPage.RunReport(configMap);
			Thread.sleep(3000);
			//LoginPage.Logout();
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
	
	@Test(priority = 1,groups = {"Sanity"})
    public void AddPage() throws Exception 
    {
        String methodName = Utils.getMethodName();
        System.out.println("methodName: "+methodName);
        
        try 
        {
            
            Utils.takeScreenshot(driver, methodName);
            logger.log(LogStatus.INFO, "<b> Verify user is able to add new Page </b>");
            HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ExportsData"), "AddPage" ,"Dataset_1");
             GenericPage.AddPage(configMap);
            Thread.sleep(3000); 
            //Logout
            Utils.tearDown();
         }
        catch (Exception e)
        {
                Utils.takeScreenshot(driver, methodName);               
                logger.log(LogStatus.FAIL, "Add Page :: Failed");
                logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
                logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
                throw (e);
        }
     }
		
	@Test(priority = 2,groups = {"Sanity"})
	public void AddNewTile() throws Exception {
		 String methodName = Utils.getMethodName();
         
	        System.out.println("methodName: " + methodName);
	        try {
	        	Utils.takeScreenshot(driver, methodName);
	        	logger.log(LogStatus.INFO, "<b> Verify user is able to add new Tile to Dashboard </b>");
	        	HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ExportsData"), "AddTile" ,"Dataset_1");
			     GenericPage.AddNewTile(configMap);
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
	
	@Test(priority = 3,groups = {"Sanity"})
	public void CancelTile() throws Exception {
		 String methodName = Utils.getMethodName();
      
	        System.out.println("methodName: " + methodName);
	        try {
	        	Utils.takeScreenshot(driver, methodName);
	        	logger.log(LogStatus.INFO, "<b> Verify user is able to Cancel Tile from Dashboard </b>");
			     GenericPage.CancelTile();
				Thread.sleep(3000);
				Utils.tearDown();
			} catch (Exception e) {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, methodName+" not success :: Failed");
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
				throw (e);
			}
	 }
	 
	 
	@Test(priority = 4,groups = {"Sanity"})
	public void RemoveNewTile() throws Exception {
			 String methodName = Utils.getMethodName();
	         
		        System.out.println("methodName: " + methodName);
		        try {
		        	Utils.takeScreenshot(driver, methodName);
		        	logger.log(LogStatus.INFO, "<b> Verify user is able to remove Tile from Dashboard </b>");
				     GenericPage.RemoveNewTile();
					Thread.sleep(3000);
					Utils.tearDown();
				} catch (Exception e) {
					Utils.takeScreenshot(driver, methodName);
					logger.log(LogStatus.FAIL, methodName+" not success :: Failed");
					logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
					logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
					throw (e);
				}	        
		       
	}
	        
	@Test(priority = 5,groups = {"Sanity"})
    public void EditPage() throws Exception 
	        {
	            String methodName = Utils.getMethodName();
	            System.out.println("methodName: "+methodName);
	            
	            try 
	            {
	                
	                Utils.takeScreenshot(driver, methodName);
	                logger.log(LogStatus.INFO, "<b> Verify user is able to Edit Page Name </b>");
	                HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ExportsData"), "EditPage" ,"Dataset_1");
	                GenericPage.EditPage(configMap);
	                Thread.sleep(3000); 
	                //Logout
	                Utils.tearDown();
	            }
	            catch (Exception e) 
	            {
	             Utils.takeScreenshot(driver, methodName);              
	             logger.log(LogStatus.FAIL, "Edit Page :: Failed");
	             logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
	             logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
	             throw (e);
	            }
	         }
	        
	@Test(priority = 6,groups = {"Sanity"})
     public void DeletePage() throws Exception 
	        {
	            String methodName = Utils.getMethodName();
	            System.out.println("methodName: "+methodName);
	            
	            try 
	            {
	                
	                Utils.takeScreenshot(driver, methodName);
	                logger.log(LogStatus.INFO, "<b> Verify user is able to Edit Page Name </b>");
	                //HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ExportsData"), "EditPage" ,"Dataset_1");
	                GenericPage.DeletePage();
	                Thread.sleep(3000); 
	                //Logout
	                Utils.tearDown();
	            }
	            catch (Exception e) 
	            {
	             Utils.takeScreenshot(driver, methodName);              
	             logger.log(LogStatus.FAIL, "Delete Page :: Failed");
	             logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
	             logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
	             throw (e);
	            }
	         }
	
	@Test(groups = {"Sanity"},priority = 8)
	public void RunBIPReport() throws Exception {
		String methodName = Utils.getMethodName();
		
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
			//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
			GenericPage.RunBIPReport(configMap);
			Thread.sleep(3000);
			//LoginPage.Logout();
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
	
	@Test(groups = {"Sanity"},priority = 7)
	public void create_RTF_Report() throws Exception {
		String methodName = Utils.getMethodName();
		
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
			//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
			GenericPage.Create_RTF_Report(configMap);
			Thread.sleep(3000);
			//LoginPage.Logout();
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
		
		@Test(priority = 9,groups = {"Sanity"})
		public void createMembershipExport() throws Exception {

			
			String methodName = Utils.getMethodName();
			System.out.println("methodName: "+methodName);
			
			try {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.INFO, "<b> Verify if user is able to Create MembershipExport() </b>");
				GenericPage.createMembershipExport();
				Utils.tearDown();	
			}
			 catch (Exception e) {
					Utils.takeScreenshot(driver, methodName);
					logger.log(LogStatus.FAIL, "New Membership  Export not created :: Failed");
					logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
					logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
					throw (e);
					}
	
		}
		@Test(priority = 10,groups = {"Sanity"})
		public void createCountryExport() throws Exception {

			
			String methodName = Utils.getMethodName();
			System.out.println("methodName: "+methodName);
			
			try {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.INFO, "<b> Verify if user is able to Create Country Export() </b>");
				GenericPage.createCountryExport();
				Utils.tearDown();	
			}
			 catch (Exception e) {
					Utils.takeScreenshot(driver, methodName);
					logger.log(LogStatus.FAIL, "New Country  Export not created :: Failed");
					logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
					logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
					throw (e);
					}
	
		}
		
		@Test
		public void CreateMappingTypes() throws Exception {
			String methodName = Utils.getMethodName();
			System.out.println("methodName: "+methodName);
			
			try {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.INFO, "<b> Verify Export Mapping Types are Created </b>");
	        	HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ExportsData"), "CreateMappingTypes" ,"Dataset_1");
				GenericPage.CreateMappingTypes(configMap);
				Utils.tearDown();	
			}
			 catch (Exception e) {
					Utils.takeScreenshot(driver, methodName);
					logger.log(LogStatus.FAIL, "Export Mapping Types are not created :: Failed");
					logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
					logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
					throw (e);
					}
		}			

		@Test(groups = {"Sanity"},priority = 12)
		public void property_ActivateLicense() throws Exception {
			String methodName = Utils.getMethodName();
			 
			System.out.println("methodName: " + methodName);
			try {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.INFO, "<b> Verify if user is able to Activate Property Level License </b>");
				HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ExportsData"), "Property_ActivateLicense" ,"Dataset_1");
				GenericPage.property_ActivateLicense(configMap);
				Utils.tearDown();

			}catch (Exception e) {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, "Failed to activate license under Property Level :: Failed");
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
				throw (e);
			}
		}
		


		@Test(groups = {"Sanity"},priority = 13)
		public void property_DeactivateLicense() throws Exception {
			String methodName = Utils.getMethodName();
			 
			System.out.println("methodName: " + methodName);
			try {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.INFO, "<b> Verify if user is able to DeActivate Property Level License </b>");
				HashMap<String,String>  configMap =ExcelUtils.getDataByRow(OR.getConfig("Path_ExportsData"), "Property_ActivateLicense" ,"Dataset_1");
				GenericPage.property_DeactivateLicense(configMap);
				Utils.tearDown();

			}catch (Exception e) {
				Utils.takeScreenshot(driver, methodName);
				logger.log(LogStatus.FAIL, "Failed to Deactivate license under Property Level :: Failed");
				logger.log(LogStatus.FAIL, "Exception occured in test due to:" + e);
				logger.log(LogStatus.FAIL, extentLogger.addScreenCapture(ReportsClass.getScreenshot(driver, methodName)));
				throw (e);
			}
		}



@Test(groups = {"Sanity"},priority = 11)
	public void RunRDFReportFromHub() throws Exception {
		String methodName = Utils.getMethodName();
		
		System.out.println("methodName: "+methodName);
		try {
			Utils.takeScreenshot(driver, methodName);
			logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
			//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
			HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
			GenericPage.RunRDFReportFromHub(configMap);
			Thread.sleep(3000);
			//LoginPage.Logout();
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

@Test(groups = {"Sanity"},priority = 9)
public void RunBIPReportFromHub() throws Exception {
	String methodName = Utils.getMethodName();
	
	System.out.println("methodName: "+methodName);
	try {
		Utils.takeScreenshot(driver, methodName);
		logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
		//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
		GenericPage.RunBIPReportFromHub(configMap);
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


@Test(groups = {"Sanity"},priority = 10)
public void OPERAHelpAccess() throws Exception {
	String methodName = Utils.getMethodName();
	
	System.out.println("methodName: "+methodName);
	try {
		Utils.takeScreenshot(driver, methodName);
		logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
		//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
		GenericPage.OperaHelpAccess(configMap);
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

@Test(groups = {"Sanity"},priority = 11)
public void ViewLogsViaSettings() throws Exception {
	String methodName = Utils.getMethodName();
	
	System.out.println("methodName: "+methodName);
	try {
		Utils.takeScreenshot(driver, methodName);
		logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
		//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
		GenericPage.ViewLogsViaSettings(configMap);
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

@Test(groups = {"Sanity"},priority = 4)
public void DownloadReport() throws Exception {
	String methodName = Utils.getMethodName();
	
	System.out.println("methodName: "+methodName);
	try {
		Utils.takeScreenshot(driver, methodName);
		logger.log(LogStatus.INFO, "<b> Verify if user is able to run a report </b>");
		//ExcelUtils.setExcelFile(OR.getConfig("Path_ConfigData"));
		HashMap<String, String> configMap = ExcelUtils.getDataByRow(OR.getConfig("Path_ConfigData"), "Reports", "Dataset_1");
		GenericPage.DownloadReport(configMap);
		Thread.sleep(3000);
		//LoginPage.Logout();
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
}




	
