package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateProfile {

	/********   To create a profile and return profileID  ***************/
	public static String createProfile(String dataset) throws Exception{
		String profileId="";
		boolean flag = true;
		String resort=WSClient.getData("{var_resort}");
		String schema=WSClient.getData("{var_schema}");
			if(dataset.equals("DS_00")){
				String query = "select name_id from name where "+schema+" resort_registered='"+resort+"' and name_id not in ( select name_id from  "+schema+" reservation_name where resort='"+resort+"' and resv_status in('RESERVED','PROSPECT','CHECKED IN','PROSPECT','WAITLIST', 'RESERVED', 'CHECKED OUT'))and active_yn='Y' and name_type='D' and inactive_date is null";
				String name=WSClient.getDBRow(query).get("NAME_ID");
				if(name!=null)
				{
				profileId=name;
				WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID :"+profileId+"</b>");
				flag=false;
				}
			
			}
			
			if(flag){
				String createProfileReq= WSClient.createSOAPMessage("CreateProfile", dataset);
				String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

				if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)){
					if (WSAssert.assertIfElementExists(createProfileResponseXML,"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
						profileId = WSClient.getElementValue(createProfileResponseXML,"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
						WSClient.setData("{var_profileId}", profileId);
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
						profileId="error";
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
					profileId="error";
				}
			}
			return profileId;
	}
	



}



