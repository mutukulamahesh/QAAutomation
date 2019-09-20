package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.name;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.Setup;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;

public class InsertComment extends Setup {
	  

		/********   To Insert Comment and return operaID  ***************/
		public static String insertComment(String InsertCommentReq, HashMap<String, String> reqDetails) throws Exception{	
			String operaID = null;
			HashMap<String, String> resDetails;
			
			WSLib.lastRunData.clear();
			WSLib.lastRunData.put("productToInvoke", "ows");
			WSLib.lastRunData.put("service", "OWSProfileService");
			WSLib.lastRunData.put("operation", "InsertComment");
			
			resDetails = SOAPClient.processSOAPMessage(InsertCommentReq, "", reqDetails);
			String  insertEmailResponseXML= resDetails.get("resV5");
			if (WSAssert.assertIfElementValueEquals(insertEmailResponseXML, "InsertCommentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
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
