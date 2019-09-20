package com.relevantcodes.customextentreports.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.relevantcodes.customextentreports.LogStatus;
import com.relevantcodes.customextentreports.model.ExceptionInfo;
import com.relevantcodes.customextentreports.model.Log;
import com.relevantcodes.customextentreports.model.Test;
import com.relevantcodes.customextentreports.model.TestAttribute;

public interface ITest {
    void setStartedTime(Date startedTime);
    
    Date getStartedTime();
    
    String getRunDuration();
    
    void setEndedTime(Date endedTime);
    
    Date getEndedTime();

    LogStatus getStatus();

    void setStatus(LogStatus logStatus);
    
    void setDescription(String description);
    
    String getDescription();
    
    void setName(String name);
    
    String getName();
    
    UUID getId();

    void setCategory(TestAttribute category);
    
    List<TestAttribute> getCategoryList();

    void setAuthor(TestAttribute author);
    
    List<TestAttribute> getAuthorsList();

    List<Log> getLogList();
    
    void setLog(List<Log> logList);
    
    void hasChildNodes(boolean val);
    
    List<Test> getNodeList();
    
    void setNodeList(List<Test> nodeList);

    void setException(ExceptionInfo exceptionInfo);

    void setUUID(UUID id);
}
