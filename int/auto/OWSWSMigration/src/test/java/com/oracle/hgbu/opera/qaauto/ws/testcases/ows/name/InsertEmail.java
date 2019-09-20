package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.name;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.Setup;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.*;

public class InsertEmail extends Setup {
  
	

	/********   To create a Email and return operaID  ***************/
	public static String createEmail(String InsertEmailReq, HashMap<String, String> reqDetails) throws Exception{	
		String operaID = null;
		HashMap<String, String> resDetails;
		
		WSLib.lastRunData.clear();
		WSLib.lastRunData.put("productToInvoke", "ows");
		WSLib.lastRunData.put("service", "OWSProfileService");
		WSLib.lastRunData.put("operation", "InsertEmail");
		
		resDetails = SOAPClient.processSOAPMessage(InsertEmailReq, "", reqDetails);
//		String emailOperaID = XMLutil.getElementValues(resDetails.get("resV5"), "//InsertEmailResponse/Result/c:IDs/c:IDPair", "operaId").get(0);
		String  insertEmailResponseXML= resDetails.get("resV5");
		if (WSAssert.assertIfElementValueEquals(insertEmailResponseXML, "InsertEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {
			if (WSAssert.assertIfElementExists(insertEmailResponseXML, "Result_IDs_IDPair_operaId", false)) {
				operaID = WSClient.getElementValue(insertEmailResponseXML, "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
			}
		}
		System.out.println(operaID);
		return operaID;
	}
	
  @Test
  public void f() {
  }
  
  
  
}
