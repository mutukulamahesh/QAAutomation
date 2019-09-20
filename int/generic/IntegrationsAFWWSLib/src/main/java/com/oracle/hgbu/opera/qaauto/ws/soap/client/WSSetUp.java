package com.oracle.hgbu.opera.qaauto.ws.soap.client;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.oracle.hgbu.opera.qaauto.ws.common.utils.EmailUtil;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.ConfigReader;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;

public class WSSetUp {

	@BeforeSuite(alwaysRun = true)
	public void startSuite() throws Exception {
		WSLib.beforeSuite();

	}

	/**
	 * Moving the environment parameter from Suite level to Test level
	 * 
	 * @param environment
	 * @throws Exception
	 */

	@BeforeTest(alwaysRun = true)
	@Parameters({ "environment", "testGroup", "runOnEntry", "wsLayer", "version"})
	public void startTest(String environment, String testGroup, ITestContext testContext, String runOnEntry, String wsLayer, String version) throws Exception {
		WSLib.beforeTest(environment, testGroup, testContext.getName(), runOnEntry,wsLayer, version);
		WSLib.strTestSuiteName = testContext.getCurrentXmlTest().getSuite().getName();
	}

	@AfterSuite(alwaysRun = true)
	@Parameters({"emailFrom", "emailTo", "emailCC", "smtpHost", "smtpPort"})
	public synchronized void endSuite(String emailFrom, String emailTo, String emailCC, String smtpHost, String smtpPort) throws Exception {
		WSLib.afterSuite();
		System.out.println(WSLib.OUTPUT_FOLDER);
		String zipDir = EmailUtil.zip(WSLib.OUTPUT_FOLDER.substring(0,WSLib.OUTPUT_FOLDER.length()-1));
		EmailUtil.SendMail(emailTo, emailCC, WSLib.strTestSuiteName, WSLib.emailBody(), zipDir, emailFrom, smtpHost, smtpPort);
	}
	
	@AfterTest(alwaysRun = true)
	public synchronized void endTest() throws Exception {

		// WSLib.afterTest();

	}

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		String className = this.getClass().getName();
		if (className.contains("com.oracle.hgbu.opera.qaauto.ws.testcases.")) {
			className = className.replace("com.oracle.hgbu.opera.qaauto.ws.testcases.", "").replace(".", " ");

			String[] classes = className.split(" ");
			String[] categories = new String[3];
			if (classes.length == 4) {
				categories[0] = classes[0] + classes[1];
				categories[1] = classes[0] + classes[1] + " " + classes[2];
				categories[2] = classes[0] + classes[1] + " " + classes[2] + " " + classes[3];
			} else if (classes.length == 3) {
				categories[0] = classes[0];
				categories[1] = classes[0] + " " + classes[1];
				categories[2] = classes[0] + " " + classes[1] + " " + classes[2];
			}

			WSLib.beforeClass(categories);
		} else {
			String[] categories = new String[0];
			WSLib.beforeClass(categories);
		}

	}

	@BeforeMethod(alwaysRun = true)
	@Parameters({ "runOnEntry", "wsLayer" })
	public synchronized void beforeMethod(Method method, String runOnEntry, String wsLayer) {
		WSLib.beforeMethod(runOnEntry, wsLayer);
	}

	@AfterMethod(alwaysRun = true)
	public synchronized void afterMethod(ITestResult result) {
		String time = ((result.getEndMillis() - result.getStartMillis())/1000 ) + " Sec";
		WSLib.afterMethod(time);
	}
}
