package com.oracle.hgbu.opera.qaauto.ws.wrapper;

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

import com.oracle.hgbu.opera.qaauto.ws.common.EmailUtil;
import com.oracle.hgbu.opera.qaauto.ws.custom.WSLib;

public class WSSetUp {

	/*
	 * This class contains methods that are executed according to the
	 * annotations mapped to them and this class must be inherited in every test
	 * class where in the test cases are written
	 */

	@BeforeSuite(alwaysRun = true)
	public void startSuite() throws Exception {
		WSLib.beforeSuite();
	}
	
	@BeforeTest(alwaysRun = true)
	@Parameters({ "environment", "testGroup", "runOnEntry", "wsLayer"})
	public void startTest(String environment, String testGroup, ITestContext testContext, String runOnEntry, String wsLayer) throws Exception {
		WSLib.beforeTest(environment, testGroup, testContext.getName(), runOnEntry,wsLayer);
		WSLib.strTestSuiteName = testContext.getCurrentXmlTest().getSuite().getName();
	}

	@AfterSuite(alwaysRun = true)
	@Parameters({"emailFrom", "emailTo", "emailCC", "smtpHost", "smtpPort"})
	public synchronized void endSuite(String emailFrom, String emailTo, String emailCC, String smtpHost, String smtpPort) throws Exception {
//		String suiteName = (ctx.getSuite().getName() == null) ? "No Subject":ctx.getSuite().getName();
		WSLib.afterSuite();
		String zipDir = EmailUtil.zip(WSLib.OUTPUT_FOLDER.substring(0,WSLib.OUTPUT_FOLDER.length()-1));
		EmailUtil.SendMail(emailTo, emailCC, WSLib.strTestSuiteName, WSLib.emailBody(), zipDir, emailFrom, smtpHost, smtpPort);
	}

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		String className = this.getClass().getName();
		if (className.contains("com.oracle.ws.soap.")) {
			className = className.replace("com.oracle.ws.soap.", "").replace(".", " ");

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
	public synchronized void beforeMethod(Method method) {
		WSLib.beforeMethod();
	}

	@AfterMethod(alwaysRun = true)
	public synchronized void afterMethod(ITestResult result) {
		String time = ((result.getEndMillis() - result.getStartMillis())/1000 ) + " Sec";
		WSLib.afterMethod(time);
	}
}
