package com.oracle.hgbu.opera.qaauto.ws.testcases.gaming.profile;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class NewProfile extends WSSetUp {
	public enum wsAttributes {
		REQUEST, RESPONSE, RESULT;
	}
	
	public static HashMap<wsAttributes,String> newProfileCreationGaming(String schema, String version, String dataSetID) {
		String sUsername = "", sPassword = "", sChain = "", sResort = "", profileID = "";
		HashMap<wsAttributes, String> resultMap = new HashMap<wsAttributes, String>();
		try {

			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			sChain = OPERALib.getChain();
			sResort = OPERALib.getResort();

			// Parameters required for the Gaming Header
			WSClient.setData("{var_userName}", sUsername);
			WSClient.setData("{var_password}", sPassword);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_resort}", sResort);
			WSClient.setData("{var_msgID}", UUID.randomUUID().toString());

			// Parameters required for NewProfile Request
			WSClient.setData("{var_nameType}", "GUEST");

			// Construct HTNG New Profile Request and Post it
			String newProfileReq = WSClient.createSOAPMessage("GamingNewProfile", dataSetID);
			String newProfileRes = WSClient.processSOAPMessage(newProfileReq);
			
			resultMap.put(wsAttributes.REQUEST, newProfileReq);
			resultMap.put(wsAttributes.RESPONSE, newProfileRes);
			
			if (WSAssert.assertIfElementContains(newProfileRes, "NewProfileResponse_Result_resultStatusFlag", "SUCCESS",
					false)) {
				if (WSAssert.assertIfElementExists(newProfileRes, "Result_IDs_IDPair_operaId", true)) {
					resultMap.put(wsAttributes.RESULT, "SUCCESS");
					profileID = WSClient.getElementValueByAttribute(newProfileRes, "Result_IDs_IDPair_operaId",
							"Result_IDs_IDPair_idType", "PROFILE", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"New Profile is successfully created. " + "Opera Profile ID :" + profileID);
				} else {
					resultMap.put(wsAttributes.RESULT, "FAIL");
					WSClient.writeToReport(LogStatus.WARNING,
							"New Profile creation is failed, Profile ID is not populated on response");
				}
			} else {
				resultMap.put(wsAttributes.RESULT, "FAIL");
				WSClient.writeToReport(LogStatus.WARNING,
						"New Profile creation is failed, SUCCESS flag is not found on response");
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL Error! " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
}
