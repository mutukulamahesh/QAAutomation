/*
* Copyright (c) 2015, Anshoo Arora (Relevant Codes).  All rights reserved.
* 
* Copyrights licensed under the New BSD License.
* 
* See the accompanying LICENSE file for terms.
*/

package com.relevantcodes.customextentreports;

import java.io.File;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import com.relevantcodes.customextentreports.model.Author;
import com.relevantcodes.customextentreports.model.Category;
import com.relevantcodes.customextentreports.model.ExceptionInfo;
import com.relevantcodes.customextentreports.model.ITest;
import com.relevantcodes.customextentreports.model.Log;
import com.relevantcodes.customextentreports.model.ScreenCapture;
import com.relevantcodes.customextentreports.model.Screencast;
import com.relevantcodes.customextentreports.model.Test;
import com.relevantcodes.customextentreports.model.TestAttribute;
import com.relevantcodes.customextentreports.utils.ExceptionUtil;
import com.relevantcodes.customextentreports.view.ScreencastHtml;
import com.relevantcodes.customextentreports.view.ScreenshotHtml;

/** 
 * <p>
 * Defines a node in the report file.
 * 
 * <p>
 * By default, each started node is top-level. If <code>appendChild</code> method
 * is used on any test, it automatically becomes a child-node. When this happens:
 * 
 * <ul>
 *  <li>parent test: <code>hasChildNodes = true</code></li>
 *  <li>child test: <code>isChildNode = true</code></li>
 * </ul>
 * 
 * @author Anshoo
 */
public class ExtentTest implements IExtentTestClass, Serializable {
    
    private static final long serialVersionUID = 6551590667557434115L;
    
    private LogStatus runStatus = LogStatus.UNKNOWN;
    private Test test;
    
    /**
     * <p>
     * Creates a test node as a top-most level test
     * 
     * @param testName 
     *      Test name
     * @param description 
     *      A short description of the test
     */
    
    public void setWSLog(String id){
    	DuplicateReportCode.currMethod=DuplicateReportCode.currMethod+"WSLib.logger.setWSLog("+DuplicateReportCode.logtext+");\n";
    	test.setWsLogs(id);
    }
    
    public void setSBLog(String id){
    	DuplicateReportCode.currMethod=DuplicateReportCode.currMethod+"WSLib.logger.setSBLog("+DuplicateReportCode.logtext+");\n";
    	test.setSbLogs(id);
    }
    
    public ExtentTest(String testName, String description) {
        
    	test = new Test();
    	DuplicateReportCode.priority=DuplicateReportCode.priority+1;
 	    DuplicateReportCode.currMethod="@Test(priority="+DuplicateReportCode.priority+")\npublic void "+testName+"(){\nWSLib.logger = WSLib.report.startTest(rTimeInc("+DuplicateReportCode.lastOperationDiff+"),\""+testName.replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\",\""+description.replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\").assignCategory(";
    	
    	DuplicateReportCode.mainMethod=DuplicateReportCode.mainMethod+testName+"();\n";
   
        test.setName(testName == null ? "" : testName.trim()); 
        test.setDescription(description.trim());
    }
    
    
    
 public ExtentTest(long time,String testName, String description) {
        
    	test = new Test(time);
    	DuplicateReportCode.currMethod="@Test()\npublic void "+testName+"(){\nfun1(rTimeInc("+DuplicateReportCode.lastOperationDiff+"),\""+testName+"\",\""+description+"\").assignCategory(";
    	DuplicateReportCode.mainMethod=DuplicateReportCode.mainMethod+testName+"();\n";
        test.setName(testName == null ? "" : testName.trim()); 
        test.setDescription(description.trim());
    }
    
    
    /**
     * <p>
     * Logs events for the test
     * 
     * <p>
     * Log event is shown in the report with 4 columns:
     * 
     * <ul>
     *  <li>Timestamp</li>
     *  <li>Status</li>
     *  <li>StepName</li>
     *  <li>Details</li>
     * </ul>
     * 
     * @param logStatus 
     *      Status (see {@link LogStatus})
     * 
     * @param stepName 
     *      Name of the step
     * 
     * @param details 
     *      Details of the step
     */
    public void log(LogStatus logStatus, String stepName, String details) {
        Log evt = new Log();
        
        String status1="LogStatus.INFO";
        
        if(logStatus==LogStatus.INFO){
        	status1="LogStatus.INFO";
        }
        else if(logStatus==LogStatus.PASS){
        	status1="LogStatus.PASS";
        }
        else if(logStatus==LogStatus.FAIL){
        	status1="LogStatus.FAIL";
        }
        else if(logStatus==LogStatus.ERROR){
        	status1="LogStatus.ERROR";
        }
        else if(logStatus==LogStatus.WARNING){
        	status1="LogStatus.WARNING";
        }
        if(DuplicateReportCode.modals){
        	DuplicateReportCode.currMethod=DuplicateReportCode.currMethod+"WSLib.logger.log(rTimeInc("+String.valueOf(DuplicateReportCode.lastOperationDiff)+"),"+status1+",CustomHTMLMethods.printReqResp(\""+DuplicateReportCode.modalReq.replaceAll("\r","").replaceAll("\n", "").replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\","
        			+ "\""+DuplicateReportCode.modalResp.replaceAll("\r","").replaceAll("\n", "").replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\",\""+DuplicateReportCode.modalOperation.replaceAll("\r","").replaceAll("\n", "").replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\",\""+DuplicateReportCode.modalRespCode+"\","
        					+ "\""+DuplicateReportCode.modalOperationKey.replaceAll("\r","").replaceAll("\n", "").replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\"));\n";
        	DuplicateReportCode.modals=false;
        }
        else{
        	DuplicateReportCode.currMethod=DuplicateReportCode.currMethod+"WSLib.logger.log(rTimeInc("+String.valueOf(DuplicateReportCode.lastOperationDiff)+"),"+status1+",\""+details.replaceAll("\r","").replaceAll("\n", "").replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\");\n";
        }
        evt.setLogStatus(logStatus);
        evt.setStepName(stepName == null ? null : stepName.trim()); 
        evt.setDetails(details == null ? "" : details.trim());

        test.setLog(evt);
        
        test.trackLastRunStatus();
        runStatus = test.getStatus();
    }
    
    
    
    public void log(long time,LogStatus logStatus, String stepName, String details) {
        Log evt = new Log(time);
        
        String status1="LogStatus.INFO";
        
        if(logStatus==LogStatus.INFO){
        	status1="LogStatus.INFO";
        }
        else if(logStatus==LogStatus.PASS){
        	status1="LogStatus.PASS";
        }
        else if(logStatus==LogStatus.FAIL){
        	status1="LogStatus.FAIL";
        }
        else if(logStatus==LogStatus.ERROR){
        	status1="LogStatus.ERROR";
        }
        else if(logStatus==LogStatus.WARNING){
        	status1="LogStatus.WARNING";
        }
        if(DuplicateReportCode.modals){
        	DuplicateReportCode.currMethod=DuplicateReportCode.currMethod+"fun2(rTimeInc("+String.valueOf(DuplicateReportCode.lastOperationDiff)+"),"+status1+",CustomHTMLMethods.printReqResp(\""+DuplicateReportCode.modalReq.replace("\"", "\\\"")+"\","
        			+ "\""+DuplicateReportCode.modalResp.replace("\"", "\\\"")+"\",\""+DuplicateReportCode.modalOperation.replace("\"", "\\\"")+"\",\""+DuplicateReportCode.modalRespCode+"\"));\n";
        	DuplicateReportCode.modals=false;
        }
        else{
        	DuplicateReportCode.currMethod=DuplicateReportCode.currMethod+"fun2(rTimeInc("+String.valueOf(DuplicateReportCode.lastOperationDiff)+"),"+status1+",\""+details.replaceAll("\"", "ls")+"\");\n";
        }
        evt.setLogStatus(logStatus);
        evt.setStepName(stepName == null ? null : stepName.trim()); 
        evt.setDetails(details == null ? "" : details.trim());

        test.setLog(evt);
        
        test.trackLastRunStatus();
        runStatus = test.getStatus();
    }
    
    
    /**
     * <p>
     * Logs events for the test
     * 
     * <p>
     * Log event is shown in the report with 4 columns:
     * 
     * <ul>
     *  <li>Timestamp</li>
     *  <li>Status</li>
     *  <li>StepName</li>
     *  <li>Details</li>
     * </ul>
     * 
     * @param logStatus 
     *      Status (see {@link LogStatus})
     * 
     * @param stepName 
     *      Name of the step
     * 
     * @param t 
     *      Exception
     */
    public void log(LogStatus logStatus, String stepName, Throwable t) {
        ExceptionInfo exceptionInfo = ExceptionUtil.createExceptionInfo(t, (Test) getTest());
        getInternalTest().setException(exceptionInfo);
        
        System.out.println(exceptionInfo.getStackTrace());
        String tag = "pre";
        String s = exceptionInfo.getStackTrace();
        if (s.contains("<") || s.contains(">"))
            tag = "textarea";
        
        log(logStatus, stepName, "<" + tag + ">" + exceptionInfo.getStackTrace() + "</" + tag + ">");
    }
    
    /**
     * <p>
     * Logs events for the test
     * 
     * <p>
     * Log event is shown in the report with 3 columns:
     * 
     * <ul>
     *  <li>Timestamp</li>
     *  <li>Status</li>
     *  <li>Details</li>
     * </ul>
     * 
     * @param logStatus 
     *      Status (see {@link LogStatus})
     * 
     * @param t 
     *      Exception
     */
   
    public void log(LogStatus logStatus, Throwable t) {
        log(logStatus, null, t);
    }
    
    /**
     * <p>
     * Logs events for the test
     * 
     * <p>
     * Log event is shown in the report with 3 columns:
     * 
     * <ul>
     *  <li>Timestamp</li>
     *  <li>Status</li>
     *  <li>Details</li>
     * </ul>
     * 
     * @param logStatus 
     *      Status (see {@link LogStatus})
     * 
     * @param details 
     *      Details of the step
     */
    
    
    public void log(LogStatus logStatus, String heading,List<String> elements) {
    	

        log(logStatus, null, CustomHTMLMethods.multiLineLog(logStatus,heading, elements));
    }
    
    

    
    public void log(LogStatus logStatus, String details) {
        log(logStatus, null, details);
    }
    
    
    
 public void log(long time,LogStatus logStatus, String heading,List<String> elements) {
    	

        log(time,logStatus, null, CustomHTMLMethods.multiLineLog(logStatus,heading, elements));
    }
    
    public void log(long time,LogStatus logStatus, String details) {
        log(time,logStatus, null, details);
    }
    
    /**
     * <p>
     * Sets the current test description
     * 
     * @param description
     *      Description of the test
     */
    
    public void setDescription(String description) {
        test.setDescription(description);        
    }

    
    
    
    
    
    /**
     * <p>
     * Gets the current test description
     */
    
    public String getDescription() {
        return test.getDescription();
    }
    
    /**
     * <p>
     * Provides the start time of the current test
     */
    
    public Date getStartedTime() {
        return test.getStartedTime();
    }
    
    /**
     * <p>
     * Allows overriding the default start time of the test
     * 
     * <p>
     * Note: when a test is started using <code>extent.startTest(testName)</code>,
     * the value for started time is created automatically. This method allows 
     * overriding the start time in cases where the actual test had already been 
     * run prior to extent logging the test details in the report. An example of
     * this scenario is while using TestNG's <code>IReporter</code> listener and
     * creating the report after the tests have already executed.
     * 
     * @param startedTime
     *      Test's start time
     */
    
    public void setStartedTime(Date startedTime) {
        test.setStartedTime(startedTime);
    }

    /**
     * <p>
     * Provides the end time of the current test
     */
   
    public Date getEndedTime() {
        return test.getEndedTime();
    }
    
    /**
     * <p>
     * Allows overriding the default end time of the test
     * 
     * <p>
     * Note: when a test is ended using <code>extent.endTest(extentTest)</code>,
     * the value for ended time is created automatically. This method allows 
     * overriding the end time in cases where the actual test had already been 
     * run prior to extent logging the test details in the report. An example of
     * this scenario is while using TestNG's <code>IReporter</code> listener and
     * creating the report after the tests have already executed.
     * 
     * @param endedTime
     *      Test's end time
     */
   
    public void setEndedTime(Date endedTime) {
        test.setEndedTime(endedTime);
    }

    /**
     * <p>
     * Adds a snapshot to the log event details
     * 
     * <p>
     * Note: this method does not create the screen-capture for the report, it only
     * sets the path of the image file in the report. The user is responsible for
     * capturing the screen and for constructing the path to the image file.
     * 
     * @param imagePath
     *      Path of the image in relation to where your report resides
     *
     * @return
     *      A formed HTML img tag
     */
   
    public String addScreenCapture(String imagePath) {
        String screenCaptureHtml = isPathRelative(imagePath)
                ? ScreenshotHtml.getSource(imagePath).replace("file:///", "")
                        : ScreenshotHtml.getSource(imagePath);
        
        ScreenCapture img = new ScreenCapture();
        img.setSource(screenCaptureHtml);
        img.setTestName(test.getName());
        img.setTestId(test.getId());
        
        test.setScreenCapture(img);

        return screenCaptureHtml;
    }

    /**
     * <p>
     * Adds a snapshot to the log event details using a Base64 string
     * 
     * @param base64
     *      Base64 image string
     *
     * @return
     *      A formed HTML img tag
     */
    public String addBase64ScreenShot(String base64) {
        String screenCaptureHtml = ScreenshotHtml.getBase64Source(base64);
        ScreenCapture img = new ScreenCapture();
        img.setSource(screenCaptureHtml);
        img.setTestName(test.getName());
        img.setTestId(test.getId());

        test.setScreenCapture(img);

        return screenCaptureHtml;
    }
    
    /**
     * <p>
     * Adds a screen cast to the log event details
     * 
     * <p>
     * Note: this method does not attach the screen-cast to the report, it only
     * links to the path
     * 
     * @param screencastPath 
     *      Path of the screencast
     * 
     * @return 
     *      A formed HTML video tag with the supplied path
     */
    
    public String addScreencast(String screencastPath) {
        String screencastHtml = isPathRelative(screencastPath) 
                ? ScreencastHtml.getSource(screencastPath).replace("file:///", "") 
                        : ScreencastHtml.getSource(screencastPath);
        
        Screencast sc = new Screencast();
        sc.setSource(screencastHtml);
        sc.setTestName(test.getName());
        sc.setTestId(test.getId());
        
        test.setScreencast(sc);
        
        return screencastHtml;
    }
    
    /**
     * <p>
     * Assigns category to test
     * 
     * <p>
     * Usage: <code>test.assignCategory("ExtentAPI");</code>
     * <br>
     * Usage: <code>test.assignCategory("ExtentAPI", "Regression", ...);</code>
     * 
     * @param categories 
     *      Category name
     * 
     * @return 
     *      A {@link ExtentTest} object
     */
    
    public ExtentTest assignCategory(String... categories) {
        List<String> list = new ArrayList<String>();
        
        for (String c : categories) {
            if (!c.trim().equals("") && !list.contains(c)) {
                test.setCategory(new Category(c));
                DuplicateReportCode.currMethod=DuplicateReportCode.currMethod+"\""+c.replaceAll("\r","").replaceAll("\n", "").replaceAll("\"", Matcher.quoteReplacement("\\\""))+"\",";
            }
            list.add(c);
        }
        DuplicateReportCode.currMethod = DuplicateReportCode.currMethod.substring(0, DuplicateReportCode.currMethod.length() - 1);
        DuplicateReportCode.currMethod = DuplicateReportCode.currMethod+");\n";
        
        return this;
    }
    
    /**
     * <p>
     * Assigns author(s) to test
     * 
     * <p>
     * Usage: <code>test.assignAuthor("AuthorName");</code>
     * <br>
     * Usage: <code>test.assignAuthor("Author1", "Author2", ...);</code>
     * 
     * @param authors Author name
     * @return {@link ExtentTest}
     */
    
    public ExtentTest assignAuthor(String... authors) {
        List<String> list = new ArrayList<String>();
        
        for (String author : authors) {
            if (!author.trim().equals("") && !list.contains(author)) {
                test.setAuthor(new Author(author));
            }
            
            list.add(author);
        }

        return this;
    }
    
    /**
     * <p>
     * Appends a child test (another {@link ExtentTest}) to the current test
     * 
     * @param node 
     *      An {@link ExtentTest} object. Test that is added as the node.
     * 
     * @return 
     *      An {@link ExtentTest} object. Parent test which adds the node as its child.
     */
    
    public ExtentTest appendChild(ExtentTest node) {
        Test internalNode = node.getInternalTest();
        
        internalNode.setEndedTime(Calendar.getInstance().getTime());
        internalNode.isChildNode = true;
        internalNode.trackLastRunStatus();
        internalNode.setParentTest(getInternalTest());
        
        test.hasChildNodes = true;

        List<String> list = new ArrayList<String>();
        
        // categories to strings
        for (TestAttribute attr : this.test.getCategoryList()) {
            if (!list.contains(attr.getName())) {            
                list.add(attr.getName());
            }
        }
        
        // add all categories to parent-test
        for (TestAttribute attr : node.getInternalTest().getCategoryList()) {
            if (!list.contains(attr.getName())) {
                this.test.setCategory(attr);
            }
        }

        test.setNode(internalNode);
                
        return this;
    }
    
    /**
     * <p>
     * Provides the current run status of the test
     * 
     * @return 
     *      {@link LogStatus}
     */
    
    public LogStatus getRunStatus() {
        return runStatus;
    }
    
    /**
     * <p>
     * Returns the interface that exposes some important methods of the underlying test
     * 
     * @return 
     *      A {@link ITest} object
     */
    
    public ITest getTest() {
        return test;
    }
    
    /**
     * <p>
     * Returns the underlying test which controls the internal model
     * 
     * <p>
     * Allows manipulating the test instance by accessing the internal methods 
     * and properties of the test
     * 
     * @return 
     *      A {@link Test} object
     */
    Test getInternalTest() {        
        return test;
    }
    
    /**
     * <p>
     * Determines if path of the file is relative or absolute
     * 
     * @param path
     *      Path of the file
     * 
     * @return
     *      Boolean
     */
    private Boolean isPathRelative(String path) {
        if (path.indexOf("http") == 0 || !new File(path).isAbsolute()) {
            return true;
        }
        
        return false;
    }
}
