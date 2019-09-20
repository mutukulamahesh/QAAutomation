package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.name;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.Setup;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;

public class InsertPhone extends Setup {
	  
	

		/********   To create a Insert Phone and return operaID  ***************/
		public static String insertPhone(String InsertPhoneReq, HashMap<String, String> reqDetails) throws Exception{	
			String operaID = null;
			HashMap<String, String> resDetails;
			
			WSLib.lastRunData.clear();
			WSLib.lastRunData.put("productToInvoke", "ows");
			WSLib.lastRunData.put("service", "OWSProfileService");
			WSLib.lastRunData.put("operation", "InsertPhone");
			
			resDetails = SOAPClient.processSOAPMessage(InsertPhoneReq, "", reqDetails);
			String  ResponseXML= resDetails.get("resV5");
			if (WSAssert.assertIfElementValueEquals(ResponseXML, "InsertPhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(ResponseXML, "Result_IDs_IDPair_operaId", false)) {
					operaID = WSClient.getElementValue(ResponseXML, "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
				}
			}
			System.out.println(operaID);
			return operaID;
		}
		
		
		@Test 
  public void f() {
  }
}
