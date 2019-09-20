package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.name;

import java.util.UUID;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class RegisterName extends WSSetUp {
	public boolean profileCreationOWS() {
		String sResort = "", sChannel = "", sUsername = "", sPassword = "", sChain = "", sChannelType = "", sChannelCarrier = "", sExtResort = "";
		boolean profileCreationFlag = false;
		try {
			sResort = OPERALib.getResort();
			sChannel = OWSLib.getChannel();
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			sChain = OPERALib.getChain();
			sChannelType = OWSLib.getChannelType(sChannel);
			sChannelCarrier = OWSLib.getChannelCarier(sResort, sChannel);
			sExtResort = OWSLib.getChannelResort(sResort, sChannel);

			// Set data for the Parameters required on the OWS SOAP header
			WSClient.setData("{var_userName}", sUsername);
			WSClient.setData("{var_password}", sPassword);
			WSClient.setData("{var_resort}", sExtResort);
			WSClient.setData("{var_systemType}", sChannelType);
			WSClient.setData("{var_channelCarrier}", sChannelCarrier);
			WSClient.setData("{var_msgID}", UUID.randomUUID().toString());

			// Create Profile using Register Name Operation
			String req_RegisterName = WSClient.createSOAPMessage("OWSRegisterName", "DS_01");
			String res_RegisterName = WSClient.processSOAPMessage(req_RegisterName);

			String operaNameId = "";
			if (WSAssert.assertIfElementValueEquals(res_RegisterName, "RegisterNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(res_RegisterName, "Result_IDs_IDPair_operaId", true)) {
					operaNameId = WSClient.getElementValue(res_RegisterName, "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					profileCreationFlag = true;
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Profile creation failed");
				}
			}
		} catch (Exception e) {
			System.out.println("Profile Creation failed " + e.getMessage());
			e.printStackTrace();
		}
		return profileCreationFlag;
	}
}
