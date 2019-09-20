package com.oracle.hgbu.opera.qaauto.ws.owsMigration;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.oracle.hgbu.opera.qaauto.ws.common.utils.EmailUtil;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;


public class Setup {
	
	@BeforeSuite(alwaysRun = true)
	public void startSuite() throws Exception {
		WSLib.beforeSuite();
		System.out.println("----------------- Before before suite -----------------------");
		testSetUp.beforesuite();
		System.out.println("----------------- After before suite -----------------------");
	}

	@AfterSuite(alwaysRun = true)
	@Parameters({"emailFrom", "emailTo", "emailCC", "smtpHost", "smtpPort"})
	public synchronized void endSuite(String emailFrom, String emailTo, String emailCC, String smtpHost, String smtpPort) throws Exception {
		WSLib.afterSuite();
		System.out.println(WSLib.OUTPUT_FOLDER);
		String emailBody=WSLib.emailBody();
//		String zipDir = EmailUtil.zip(WSLib.OUTPUT_FOLDER.substring(0,WSLib.OUTPUT_FOLDER.length()-1));
//		EmailUtil.SendMail(emailTo, emailCC, WSLib.strTestSuiteName, emailBody, zipDir, emailFrom, smtpHost, smtpPort);

	}
	
	 @Parameters({ "environment", "testGroup", "runOnEntry", "wsLayer", "version"})
	 @BeforeTest(alwaysRun = true)
	 public void beforeTest(String environment, String testGroup, ITestContext testContext, String runOnEntry, String wsLayer, String version) throws Exception{
		 System.out.println("----------------- Before before Test -----------------------");
		 WSLib.beforeTest(environment, testGroup, testContext.getName(), runOnEntry,wsLayer, version);
		 WSLib.strTestSuiteName = testContext.getCurrentXmlTest().getSuite().getName();
		 testSetUp.setTestEnv(environment);
		 System.out.println("----------------- After before Test -----------------------");
	 }
	
	
	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		System.out.println("----------------- Before before class -----------------------");
		String className = this.getClass().getName();
		if(className.contains("com.oracle.hgbu.opera.qaauto.ws.testcases."))
		{
			className=className.replace("com.oracle.hgbu.opera.qaauto.ws.testcases.", "").replace(".", " ");

			String[] classes = className.split(" ");
			String[] categories=new String[3];
			if(classes.length==4){
				categories[0]=classes[0]+classes[1];
				categories[1]=classes[0]+classes[1]+" "+classes[2];
				categories[2]=classes[0]+classes[1]+" "+classes[2]+" "+classes[3];
			}
			else if(classes.length==3){
				categories[0]=classes[0];
				categories[1]=classes[0]+" "+classes[1];
				categories[2]=classes[0]+" "+classes[1]+" "+classes[2];
			}

			WSLib.beforeClass(categories);
		}
		else{
			String[] categories=new String[0];
			WSLib.beforeClass(categories);
		}
		System.out.println("----------------- After before class -----------------------");
	}

	@BeforeMethod(alwaysRun = true)
	@Parameters({"runOnEntry","wsLayer"})
	public synchronized void beforeMethod(Method method, String runOnEntry, String wsLayer) {
		System.out.println("----------------- Before before method -----------------------");
		WSLib.beforeMethod(runOnEntry,wsLayer);
		testSetUp.setTestMethodName(method.getName());
		System.out.println("----------------- After before method -----------------------");
	}

	@AfterMethod(alwaysRun = true)
	public synchronized void afterMethod(ITestResult result) {
		System.out.println("----------- Before After Method-----------");
		String time = ((result.getEndMillis() - result.getStartMillis())/1000 ) + " Sec";
		WSLib.afterMethod(time);
		System.out.println("----------- After After Method-----------");
	}


	
	

}
