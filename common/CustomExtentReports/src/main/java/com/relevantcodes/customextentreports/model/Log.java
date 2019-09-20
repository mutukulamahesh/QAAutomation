/*
* Copyright (c) 2015, Anshoo Arora (Relevant Codes).  All rights reserved.
* 
* Copyrights licensed under the New BSD License.
* 
* See the accompanying LICENSE file for terms.
*/

package com.relevantcodes.customextentreports.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.relevantcodes.customextentreports.DuplicateReportCode;
import com.relevantcodes.customextentreports.LogStatus;

public class Log implements Serializable {

	private static final long serialVersionUID = 889252425952501333L;
	
	private Date timestamp;
    private LogStatus logStatus;
    private String stepName;
    private String details;
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
    	this.timestamp = timestamp;
    }
    
    public void setLogStatus(LogStatus logStatus) {
        this.logStatus = logStatus;
    }
    
    public LogStatus getLogStatus() {
        return logStatus;
    }
    
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    
    public String getStepName() {
        return stepName;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getDetails() {
        return details;
    }
    
    public Log() {
    	timestamp = Calendar.getInstance().getTime();
    	DuplicateReportCode.lastOperationDiff=Calendar.getInstance().getTime().getTime()-DuplicateReportCode.lastOperation;
    	DuplicateReportCode.lastOperation=Calendar.getInstance().getTime().getTime();
    }
    
    public Log(long time) {
    	Calendar cal=Calendar.getInstance();
    	cal.setTimeInMillis(time);
    	timestamp = cal.getTime();
    	DuplicateReportCode.lastOperationDiff=Calendar.getInstance().getTime().getTime()-DuplicateReportCode.lastOperation;
    	DuplicateReportCode.lastOperation=Calendar.getInstance().getTime().getTime();
    }
    
}
