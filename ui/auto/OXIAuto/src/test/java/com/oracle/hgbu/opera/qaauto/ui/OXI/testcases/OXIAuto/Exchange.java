package com.oracle.hgbu.opera.qaauto.ui.OXI.testcases.OXIAuto;

import com.oracle.hgbu.opera.qaauto.ui.OXI.component.OXIAuto.WeblogicandOSBPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.ExcelUtils;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Log;
import com.oracle.hgbu.opera.qaauto.ui.utilities.LoginPage;
import com.oracle.hgbu.opera.qaauto.ui.utilities.OR;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;
import com.relevantcodes.extentreports.LogStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Exchange extends  Utils {
	static Map<String, String> OxiMap = new HashMap<String, String>();
	static Map<String,String> configMap=new HashMap<String,String>();
	String  browser =null;
	String weblogic_url=null;
	String osb_url=null;
	

	
	@DataProvider(name="getWLData")
    public Object[][] getWLData() throws Exception {
		
		configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configMap.get("Set"));
		
		 browser=configMap.get("Browser");
		 weblogic_url=configMap.get("weblogic_url");
        return new Object[][]{
                {browser,weblogic_url}	                
        };
    }
	@DataProvider(name="getSBData")
	public Object[][] getSBData() throws Exception{
		configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "RunConfig", "KEY");
		configMap = ExcelUtils.getDataByColoumn(OR.getConfig("Path_EnvironmentDetails"), "Configuration", "KEY",configMap.get("Set"));
		
		 browser=configMap.get("Browser");
		 osb_url=configMap.get("osb_url");
		return new Object[][]{
            {browser,osb_url}	                
    };
	}
  @Test(enabled=true,dataProvider="getWLData",groups= {"BAT"})
  public void validateExchangeUIDeployed(String browserName, String url) throws Exception {
	  
	  	String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		Log.startTestCase(testClassName);
		System.out.println("Test Name: "+testName);
		
		Utils.takeScreenshot(driver, testClassName);
		WeblogicandOSBPage.weblogicLogin();
		logger = report.startTest(testName, "Validate Exchange ear is deployed").assignCategory("acceptance", "Cloud.Profile");
		WeblogicandOSBPage.validateWeblogiclogin();	
		Utils.click("weblogic.deployments");
		Utils.takeScreenshot("Deployments");
		logger.log(LogStatus.INFO, "Navigated to Deployments");		
		System.out.println(Utils.getText("weblogic.exchangeEar"));
		try {
			Utils.scrolltoElement("weblogic.exchangeEar");
			logger.log(LogStatus.PASS, "Exchange ear is deployed ");
		}
			// get the state Exchange EAR
			
		
		catch(ElementNotVisibleException e) {
			logger.log(LogStatus.INFO, "Exchange ear is deployed ");
		}try {
			String state=Utils.getText("weblogic.exchangeState");
			if (state.trim().equals("Active")){
				System.out.println("The State of the Exchange ear is "+state);
				logger.log(LogStatus.PASS,"The State of the Exchange ear is "+state);
			}
			else
				{
				System.out.println("The State of the Exchange ear is not Active currently . The current status is  "+state);
				
				logger.log(LogStatus.ERROR,"The State of the Exchange ear is not Active currently . The current status is"+state);
				}
		}
		catch(ElementNotVisibleException e){
			logger.log(LogStatus.INFO, "Could not retrieve State of Exchange ear");
		}
		// get the health of Exchange ear
		try {
			String health=Utils.getText("weblogic.exchangeHealth");
			if (health.trim().equals("OK"))
			{
				System.out.println("The Health of the Exchange ear is "+health);
				logger.log(LogStatus.PASS,"The Health of the Exchange ear is "+health);
			}
			else
			{
			System.out.println("The Health of the Exchange ear is not OK currently . The current status is  "+health);
			
			logger.log(LogStatus.ERROR,"The Health of the Exchange ear is not OK currently . The current status is"+health);
			}
		}
		catch(ElementNotVisibleException e) {
			logger.log(LogStatus.INFO, "Could not retrieve Health of Exchnage Ear");
		}
		Utils.takeScreenshot("Exchange ear");
		
		
		
		WeblogicandOSBPage.weblogicLogout();
  }
  
 
  
  
  @Test(enabled=true,dataProvider="getWLData",groups= {"BAT"})
  public void validateOXIProcessorStatus(String browserName, String url) throws Exception {
	  
		String testClassName = Utils.getClassName();
		String testName = Utils.getMethodName();
		Log.startTestCase(testClassName);
		System.out.println("Test Name: "+testName);
		
		Utils.takeScreenshot(driver, testClassName);
		WeblogicandOSBPage.weblogicLogin();
		logger = report.startTest(testName, "Validate the OXI Procesors").assignCategory("acceptance", "Cloud.Profile");
		OxiMap=ExcelUtils.getDataByRow(OR.getConfig("Path_Oxi"), "Weblogic", testName);
		System.out.println(OxiMap.get("InterfaceName"));
		WeblogicandOSBPage.validateWeblogiclogin();
		Utils.click("weblogic.deployments");
		Utils.takeScreenshot("Deployments");
		logger.log(LogStatus.INFO, "Navigated to Deployments");		
		
		String processor=OxiMap.get("InterfaceName");
		//validate whether Outbound and Inbound processors are present
		
		java.util.List<WebElement> elements=Utils.elements("weblogic.processor");
		
		int count=0;
		for (WebElement ele:elements) {
			
			String content=ele.getText();
			String pattern=".*"+processor+".*";
			if (Pattern.matches(pattern, content))
				{
					if(content.contains("InboundProcessor"))
					{	
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("arguments[0].scrollIntoView(true);",ele);
						Utils.takeScreenshot("InboundProcessor");
						count++;
						logger.log(LogStatus.PASS,"InboundProcessor of "+processor+"is deployed");
						String locator=OR.getORpropvalue("weblogic.processor");
						System.out.println("Locator is "+ locator+ "text is "+ele.getText());
						// verify the  state of the Inbound Processor
						locator=locator.replaceAll("Processor", ele.getText());
						String state =locator+"/../following-sibling::td/a";
						try {
						if(driver.findElement(By.xpath(state)).getText().trim().equals("Active"))
							{
							System.out.println("The State of the Inbound "+processor+" processor is Active");
							logger.log(LogStatus.PASS,"The State of the Inbound "+processor+" processor is Active");
							}
						else {
							System.out.println("The State of the Inbound "+processor+" processor is not Active but the current state is "+driver.findElement(By.xpath(state)).getText());
							logger.log(LogStatus.ERROR,"The State of the Inbound "+processor+" processor is not Active but the current state is "+driver.findElement(By.xpath(state)).getText());
						}
						}
						catch(ElementNotVisibleException e) {
							logger.log(LogStatus.INFO, "Could not retrieve State of Inbound "+processor+" Processor");
						}
						//Verify the HEalth of Inbound Processor
						String health=locator+"/../following-sibling::td[2]";
						try {
							if(driver.findElement(By.xpath(health)).getText().trim().equals("OK"))
							{
							System.out.println("The Health of the Inbound "+processor+" processor is OK");
							logger.log(LogStatus.PASS,"The Health of the Inbound "+processor+" processor is OK");
							}
						else {
							System.out.println("The Health of the Inbound "+processor+" processor is not OK but the current state is "+driver.findElement(By.xpath(health)).getText());
							logger.log(LogStatus.ERROR,"The Health of the Inbound "+processor+" processor is not OK but the current health is "+driver.findElement(By.xpath(health)).getText());
						}
							
						}
						catch(ElementNotVisibleException e) {
							logger.log(LogStatus.INFO, "Could not retrieve Health of Inbound"+processor+"  Processor");
						}
					}
									
			
					else if(content.contains("OutboundProcessor")) {
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("arguments[0].scrollIntoView(true);",ele);
						Utils.takeScreenshot("OutboundProcessor");
						count++;
						logger.log(LogStatus.PASS,"OutboundProcessor of "+processor+"is deployed");
						String locator=OR.getORpropvalue("weblogic.processor");
						System.out.println("Locator is "+ locator+ "text is "+ele.getText());
						//verify the state of Outbound Processor
						locator=locator.replaceAll("Processor", ele.getText());
						String state =locator+"/../following-sibling::td/a";
						try {
						if(driver.findElement(By.xpath(state)).getText().trim().equals("Active"))
							{
							System.out.println("The State of the Outbound "+processor+" processor is Active");
							logger.log(LogStatus.PASS,"The State of the Outbound "+processor+" processor is Active");
							}
						else {
							System.out.println("The State of the Outbound "+processor+" processor is not Active but the current state is "+driver.findElement(By.xpath(state)).getText());
							logger.log(LogStatus.ERROR,"The State of the Outbound "+processor+" processor is not Active but the current state is "+driver.findElement(By.xpath(state)).getText());
						}
						}
						catch(ElementNotVisibleException e) {
							logger.log(LogStatus.INFO, "Could not retrieve State of Outbound "+processor+" Processor");
						}
						//Verify the HEalth of Outbound Processor
						String health=locator+"/../following-sibling::td[2]";
						try {
							if(driver.findElement(By.xpath(health)).getText().trim().equals("OK"))
							{
							System.out.println("The Health of the Outbound "+processor+" processor is OK");
							logger.log(LogStatus.PASS,"The Health of the Outbound "+processor+" processor is OK");
							}
						else {
							System.out.println("The Health of the Outbound "+processor+" processor is not OK but the current state is "+driver.findElement(By.xpath(health)).getText());
							logger.log(LogStatus.ERROR,"The Health of the Outbound "+processor+" processor is not OK but the current health is "+driver.findElement(By.xpath(health)).getText());
						}
							
						}
						catch(ElementNotVisibleException e) {
							logger.log(LogStatus.INFO, "Could not retrieve Health of Outbound"+processor+"  Processor");
						}
						}
							
						
					
					else{
				logger.log(LogStatus.WARNING,"Neither Outbound or Inbound Processor deployed, something else! Please verify "+processor);
			}
				}
		}
		if(count==0)
		{
			logger.log(LogStatus.INFO, "The processor "+ processor+" is not deployed");
			System.out.println("The processor "+ processor+" is not deployed");
		}
		WeblogicandOSBPage.weblogicLogout();
  }



@Test(enabled=true,dataProvider="getWLData",groups= {"BAT"})
public  void validateJMSModules(String browserName, String url) throws IOException, Exception  {
	String testClassName =null;
	String testName=null;
	try {
		testClassName = Utils.getClassName();
	} catch (Exception e4) {
		// TODO Auto-generated catch block
		e4.printStackTrace();
	}
	
	try {
		testName = Utils.getMethodName();
	} catch (Exception e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
	}
	Log.startTestCase(testClassName);
	System.out.println("Test Name: "+testName);
	
	try {
		Utils.takeScreenshot(driver, testClassName);
	} catch (Exception e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	try {
		WeblogicandOSBPage.weblogicLogin();
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	logger = report.startTest(testName, "Validate the JMS Module of the processor").assignCategory("acceptance", "Cloud.Profile");
	try {
		OxiMap=ExcelUtils.getDataByRow(OR.getConfig("Path_Oxi"), "Weblogic", testName);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(OxiMap.get("InterfaceName"));
	WeblogicandOSBPage.validateWeblogiclogin();
	String processor=OxiMap.get("InterfaceName");
	try{
		Utils.click("weblogic.jms");
		logger.log(LogStatus.PASS, "JMS Modules Launched");
		Utils.takeScreenshot("JMS Modules Page");
	}
	catch(ElementNotVisibleException e) {
		logger.log(LogStatus.ERROR, "Element JMS Modules Not found");
		
	}
	try {
		Utils.click("weblogic.oxijms");
		logger.log(LogStatus.PASS, "OXI JMS Modules Loaded");
		Utils.takeScreenshot("OXI JMS Modules Page");
	}
	catch(ElementNotVisibleException e) {
		logger.log(LogStatus.ERROR, "Element OXI JMS Modules Not found");
		
	}
	java.util.List<WebElement> eles =Utils.elements("weblogic.reset");
	if(eles.size()==1)
	{
		Utils.click("weblogic.reset");
	}
	
		Utils.click("weblogic.table");
		Utils.clear("weblogic.criteria");
		Utils.textBox("weblogic.criteria", processor);
		Utils.takeScreenshot("Criteria");
		Utils.click("weblogic.apply");
		//Validate if results exists
		eles=Utils.elements("weblogic.data");
		if (eles.size()>0) {
			
			Boolean flag_1=false;
			logger.log(LogStatus.INFO, "Procesor JMS Modules exists");
			//validate if BE_JMS_QUEUE exists
			java.util.List<WebElement> bejms=Utils.elements("weblogic.be");
			if(bejms.size()>0) {
				//logger.log(LogStatus.INFO, "Procesor BE JMS Queue exists");
				System.out.println(" BE JMS Queue for the processor "+processor+ " exists");
				//String be_st=OR.getORpropvalue("weblogic.be");
				//be_st=be_st.replaceAll("BE_JMS_QUEUE", processor+"_BE_JMS_QUEUE");
				
				for(WebElement be_ele:bejms) {
					
					if(be_ele.getText().equals(processor+"_BE_JMS_QUEUE")) {
						
						flag_1=true;
						break;
					}
					
					}
				if(flag_1) {
					System.out.println("BE JMS QUEUE for "+processor+ " exists");
					logger.log(LogStatus.PASS, "BE JMS QUEUE for "+processor+ " exists");
					Utils.takeScreenshot(processor+"_BE_JMS_QUEUE");
					
				}
				else {
					System.out.println("BE JMS QUEUE for "+processor+ " does not exist");
					logger.log(LogStatus.FAIL, "BE JMS QUEUE for "+processor+ " does not exist");
				}
				
				}
				
			
			else {
				logger.log(LogStatus.ERROR, "Procesor BE JMS Queue does not exist");
				System.out.println("Procesor BE JMS Queue does not exist");
			}
			//Validate OUT JMS QUEUE for the processor
			java.util.List<WebElement> outjms=Utils.elements("weblogic.out");
			if(outjms.size()>0) {
				
				System.out.println(" OUT JMS Queue for the processor exists");
				
				for(WebElement out_ele:outjms) {
					flag_1=false;
					
					if(out_ele.getText().equals(processor+"_OUT_JMS_QUEUE")) {
						
						flag_1=true;
						break;
					}
					
					
					}
				if(flag_1) {
					System.out.println("OUT JMS QUEUE for "+processor+ " exists");
					logger.log(LogStatus.PASS, "OUT JMS QUEUE for "+processor+ " exists");
					Utils.takeScreenshot(processor+"_OUT_JMS_QUEUE");
				}
				else {
					System.out.println("OUT JMS QUEUE for "+processor+ " does not exist");
					logger.log(LogStatus.FAIL, "OUT JMS QUEUE for "+processor+ "does not exist");
				}
			}
			else {
				System.out.println("OUT JMS QUEUE for "+processor+ " does not exist");
				logger.log(LogStatus.ERROR, "OUT JMS QUEUE for "+processor+ "does not exist");
			}
			//Validate IN JMS QUEUE for the processor
			java.util.List<WebElement> injms=Utils.elements("weblogic.in");
			if(injms.size()>0) {
				
				System.out.println(" IN JMS Queue for the processor exists");
				
				for(WebElement in_ele:injms) {
					flag_1=false;
					
					if(in_ele.getText().equals(processor+"_IN_JMS_QUEUE")) {
						
						flag_1=true;
						break;
					}
					
					
					}
				if(flag_1) {
					System.out.println("IN JMS QUEUE for "+processor+ " exists");
					logger.log(LogStatus.PASS, "IN JMS QUEUE for "+processor+ " exists");
					Utils.takeScreenshot(processor+"_IN_JMS_QUEUE");
				}
				else {
					System.out.println("IN JMS QUEUE for "+processor+ " does not exist");
					logger.log(LogStatus.FAIL, "IN JMS QUEUE for "+processor+ "does not exist");
					
				}
			}
			else {
				System.out.println("IN JMS QUEUE for "+processor+ " does not exist");
				logger.log(LogStatus.ERROR, "IN JMS QUEUE for "+processor+ "does not exist");
			}
		}
		else
			logger.log(LogStatus.ERROR, "Procesor JMS Modules does not exist");
		WeblogicandOSBPage.weblogicLogout();
}

@Test(enabled=true,dataProvider="getSBData",groups= {"BAT"})
public  void validateOsbOxijar(String browserName, String url) throws Exception {
	String testClassName = Utils.getClassName();
	String testName = Utils.getMethodName();
	Log.startTestCase(testClassName);
	System.out.println("Test Name: "+testName);
	
	Utils.takeScreenshot(driver, testClassName);
	WeblogicandOSBPage.osbLogin();
	logger = report.startTest(testName, "Validate OXI jar is deployed in OSB").assignCategory("acceptance", "Cloud.Profile");
	WeblogicandOSBPage.validateOsbLogin();
	Actions act=new Actions(driver);
	try {
		act.contextClick(driver.findElement(By.xpath("//span[text()='OXIGenServices']"))).build().perform();
	
		Utils.click("osb.open");
	
		Thread.sleep(2000);
	
	
		logger.log(LogStatus.PASS, " OXIGenServices are deployed");
		Utils.takeScreenshot("OXIGenService jar");
		//Utils.scrolltoElement("osb.service");
		//Utils.click("osb.service");
		Utils.click("osb.proxy");
		Utils.click("osb.outbound");
		Utils.click("osb.service");
		Utils.click("osb.transport");
		java.util.List<WebElement> ele_img=Utils.elements("osb.checkbox");
		for(WebElement we:ele_img) {
			System.out.println("The text is "+we.getAttribute("title"));
			if(we.getAttribute("title").contains("HTTPS connection")) {
				System.out.println("Inside if");
				int index=we.getAttribute("title").indexOf("(");
				String check=we.getAttribute("title").substring(index,we.getAttribute("title").length() );
				logger.log(LogStatus.INFO, "The current HTTPS connection is "+ check);
				Utils.takeScreenshot("Transport Details");
				break;
				
			}
		}
		logger.log(LogStatus.PASS, "OXIGenService jar deployed");
	}
	catch(ElementNotVisibleException e) {
		logger.log(LogStatus.FAIL, "OXIGenService jar not deployed");
	}
		WeblogicandOSBPage.osbLogout();
	}
	

}
