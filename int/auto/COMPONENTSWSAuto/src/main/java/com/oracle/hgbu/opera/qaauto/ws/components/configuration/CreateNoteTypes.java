
package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;


public class CreateNoteTypes extends WSSetUp {

	public boolean createNoteTypes(String dataset) {
		try {
//			String req_fetchNote = WSClient.createSOAPMessage("FetchNoteTypes", dataset);
//			String res_fetchNote = WSClient.processSOAPMessage(req_fetchNote);
//			if(WSAssert.assertIfElementExists(res_fetchNote, "FetchNoteTypesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchNote, "NoteTypes_NoteType_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Note code already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Note Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateNoteTypes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Note Type " + WSClient.getData("{var_NoteType}") +" already exists");
				return true;
			}

				else {
					WSClient.writeToReport(LogStatus.INFO, "Note Code doesnot exist!!");
					String req_createNote = WSClient.createSOAPMessage("CreateNoteTypes", dataset);
					String res_createNote = WSClient.processSOAPMessage(req_createNote);
					if(WSAssert.assertIfElementExists(res_createNote, "CreateNoteTypesRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateNoteTypesRS/Success exists on the response message");
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("CreateNoteTypes", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created Note Code");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "New Note Type not created");
							return false;
						}
					}
					else {
						if(WSAssert.assertIfElementExists(res_createNote, "CreateNoteTypesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createNote, "CreateNoteTypesRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
				}
					
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	@Test(groups= {"OperaConfig"})
	public void createMultiple_NoteTypes() {
		int i;
		boolean flag = true;
		String testName = "CreateNoteTypes";
		WSClient.startTest(testName, "Create Note Types", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("NoteType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("NoteType" , dataset);
			String val_note_group = OperaPropConfig.getDataSetForCode("NoteGroup" , dataset);
			WSClient.setData("{var_NoteType}", value);
			WSClient.setData("{var_NoteGroup}", val_note_group);
			flag = flag && createNoteTypes(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("NoteType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("NoteType", "N");
	}
}
