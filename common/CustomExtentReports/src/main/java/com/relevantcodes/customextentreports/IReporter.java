/*
* Copyright (c) 2015, Anshoo Arora (Relevant Codes).  All rights reserved.
* 
* Copyrights licensed under the New BSD License.
* 
* See the accompanying LICENSE file for terms.
*/

package com.relevantcodes.customextentreports;

import com.relevantcodes.customextentreports.model.Test;

public interface IReporter {
    void start(Report report);
    void stop();
    void flush();
    void addTest(Test test);
    void setTestRunnerLogs();
}
