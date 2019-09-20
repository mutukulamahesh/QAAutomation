package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
public class WriteResults extends WSSetUp {
	
	@Test(groups = {"WriteResults"}, priority=2000, dependsOnGroups = {"OperaConfig","createConversionCodes","OWS"})
	public void writeResults() {
		String testName = "WriteResults";
		//WSClient.startTest(testName, "Write Config Results to Excel", "WriteResults");
		OperaPropConfig.writeResults();
	}

}
