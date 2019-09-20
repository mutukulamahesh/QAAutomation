/*
* Copyright (c) 2015, Anshoo Arora (Relevant Codes).  All rights reserved.
* 
* Copyrights licensed under the New BSD License.
* 
* See the accompanying LICENSE file for terms.
*/

package com.relevantcodes.customextentreports;

import java.net.Inet4Address;
import java.net.InetAddress;

import java.util.Map;

import com.relevantcodes.customextentreports.model.SystemProperties;

class SystemInfo {
    private SystemProperties systemProperties;

    public Map<String, String> getInfo() {
        if (systemProperties == null) {
            return null;
        }
        
        return systemProperties.getSystemInfo();
    }
    
    public void setInfo(Map<String, String> info) {
        systemProperties.setSystemInfo(info);
    }
    
    public void setInfo(String param, String value) {
        systemProperties.setSystemInfo(param, value);
    }
    
    private void setInfo() {
        if (systemProperties == null) {
            systemProperties = new SystemProperties();
        }
        
        
        systemProperties.setSystemInfo("<b>System Details</b>", "");
        systemProperties.setSystemInfo("Operating System", System.getProperty("os.name"));
        systemProperties.setSystemInfo("Java Version", System.getProperty("java.version"));
        
        try {
            systemProperties.setSystemInfo("Host Name", InetAddress.getLocalHost().getHostName());
            systemProperties.setSystemInfo("IP Address", Inet4Address.getLocalHost().getHostAddress());
        } 
        catch(Exception e) { }

       /* systemProperties.setSystemInfo("<b>Envinorment Details</b>", "");
        systemProperties.setSystemInfo("Envinorment", StaticValues.globalEnvMap.get("environemnt"));
        systemProperties.setSystemInfo("Chain", StaticValues.globalEnvMap.get("chain"));
        systemProperties.setSystemInfo("<b>Database Details</b>", "");
        systemProperties.setSystemInfo("Hostname", StaticValues.globalEnvMap.get("dbHostname"));
        systemProperties.setSystemInfo("Port", StaticValues.globalEnvMap.get("dbPort"));
        systemProperties.setSystemInfo("ServiceName", StaticValues.globalEnvMap.get("dbServiceName"));
        systemProperties.setSystemInfo("UserName", StaticValues.globalEnvMap.get("dbUserName"));
        */
        
    }
    
    	
    
    public SystemInfo() { 
        setInfo();
    }
}
