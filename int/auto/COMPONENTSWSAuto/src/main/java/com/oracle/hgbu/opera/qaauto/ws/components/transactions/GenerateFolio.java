package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class GenerateFolio {

	public static boolean generateFolio(String dataset) throws Exception {
		boolean resultFlag = false;
		String generateFolioReq = WSClient.createSOAPMessage("GenerateFolio", dataset);
		String generateFolioRes = WSClient.processSOAPMessage(generateFolioReq);

		if(WSAssert.assertIfElementExists(generateFolioRes, "GenerateFolioRS_Success", true)) {
			if(WSAssert.assertIfElementExists(generateFolioRes, "FolioWindow_Folios_Folio_InternalFolioWindowID", true)) {
				resultFlag = true;
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Folio generated successfully"+"</b>");
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Folio generation is unsuccessful"+"</b>");
			}
		}
		else {
			WSClient.writeToReport(LogStatus.INFO, "<b>"+"Folio generation is unsuccessful"+"</b>");
		}
		return resultFlag;
	}
}
