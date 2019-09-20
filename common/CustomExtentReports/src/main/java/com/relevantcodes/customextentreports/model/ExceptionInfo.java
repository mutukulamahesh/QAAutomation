package com.relevantcodes.customextentreports.model;

import java.io.Serializable;

import com.relevantcodes.customextentreports.model.Test;

public class ExceptionInfo implements Serializable {

	private static final long serialVersionUID = -7676305793226138602L;

	private String exceptionName;
    private String stackTrace;
    private Test test;

    public String getExceptionName() {
        return exceptionName;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
