package com.oracle.hgbu.opera.qaauto.ws.testcases.gaming.profile;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.oracle.hgbu.opera.qaauto.ws.testcases.gaming.profile.NewProfile.wsAttributes;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchProfile extends WSSetUp{
	@Test(groups = {"BAT", "FetchProfile", "Profile", "Gaming" })
	@Parameters({"schema", "version"})
	public void fetchProfileGaming(String schema, String version) {
		WSClient.startTest("fetchProfileGaming","Verify profile retrival via Gaming Interface","bat");
		String sUsername="", sPassword="", sChain="", sResort="";
		HashMap<wsAttributes, String> newProfileMap = new HashMap<wsAttributes, String>();
		try {
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			sChain = OPERALib.getChain();
			sResort = OPERALib.getResort();
			
			//Parameters required for the Gaming Header
			WSClient.setData("{var_userName}", sUsername);
			WSClient.setData("{var_password}", sPassword);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_resort}", sResort);
			WSClient.setData("{var_msgID}",UUID.randomUUID().toString());
			
			//Parameters required for NewProfile Request
			WSClient.setData("{var_nameType}", "GUEST");
			
			//Construct HTNG New Profile Request and Post it
			newProfileMap = NewProfile.newProfileCreationGaming(schema, version, "DS_01");
			if(newProfileMap.get(wsAttributes.RESULT).equalsIgnoreCase("SUCCESS")) {
					String newProfileReq = newProfileMap.get(wsAttributes.REQUEST);
					String newProfileRes = newProfileMap.get(wsAttributes.RESPONSE);
					String profileID = WSClient.getElementValueByAttribute(newProfileRes, "Result_IDs_IDPair_operaId","Result_IDs_IDPair_idType", "PROFILE", XMLType.RESPONSE);
					WSClient.setData("{var_operaProfileID}", profileID);
					String np_nameType = WSClient.getElementValue(newProfileReq, "NewProfileRequest_Profile_nameType", XMLType.REQUEST);
					String np_extProfileID = WSClient.getElementValue(newProfileReq, "Profile_ProfileIDs_UniqueID",XMLType.REQUEST);
					String np_firstName = WSClient.getElementValue(newProfileReq, "Customer_PersonName_firstName", XMLType.REQUEST);
					String np_lastName = WSClient.getElementValue(newProfileReq, "Customer_PersonName_lastName", XMLType.REQUEST);
								
					//Construct HTNG Fetch Profile Request and Post it
					String fetchProfileReq = WSClient.createSOAPMessage("GamingFetchProfile", "DS_01");
					String fetchProfileRes = WSClient.processSOAPMessage(fetchProfileReq);
						
					if (WSAssert.assertIfElementContains(fetchProfileRes, "FetchProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {
							
						String fp_extProfileID = WSClient.getElementValueByAttribute(fetchProfileRes, "Profile_ProfileIDs_UniqueID","Profile_ProfileIDs_UniqueID_type", "EXTERNAL", XMLType.RESPONSE);
						String fp_intProfileID = WSClient.getElementValueByAttribute(fetchProfileRes, "Profile_ProfileIDs_UniqueID","Profile_ProfileIDs_UniqueID_type", "INTERNAL", XMLType.RESPONSE);
									
						WSAssert.assertIfElementValueEquals(fetchProfileRes, "FetchProfileResponse_Profile_nameType", np_nameType, false);
						if(WSAssert.assertEquals(np_extProfileID, fp_extProfileID, true)) 
							WSClient.writeToReport(LogStatus.PASS, "@UniqueID@Type='EXTERNAL'"+" Expected: "+np_extProfileID+ " Actual: "+fp_extProfileID);
						else 
							WSClient.writeToReport(LogStatus.FAIL, "@UniqueID@Type='EXTERNAL'"+" Expected: "+np_extProfileID+ " Actual: "+fp_extProfileID);						
						
						if(WSAssert.assertEquals(profileID, fp_intProfileID, true)) 
							WSClient.writeToReport(LogStatus.PASS, "@UniqueID@Type='INTERNAL'"+" Expected: "+profileID+ " Actual: "+fp_intProfileID);
						else 
							WSClient.writeToReport(LogStatus.FAIL, "@UniqueID@Type='INTERNAL'"+" Expected: "+profileID+ " Actual: "+fp_intProfileID);
						WSAssert.assertIfElementValueEquals(fetchProfileRes, "Customer_PersonName_firstName", np_firstName, false);
						
						WSAssert.assertIfElementValueEquals(fetchProfileRes, "Customer_PersonName_lastName", np_lastName, false);
						WSAssert.assertIfElementValueEquals(fetchProfileRes, "Memberships_NameMembership_membershipType", "PTS", false);
						WSAssert.assertIfElementValueEquals(fetchProfileRes, "Memberships_NameMembership_membershipNumber", np_extProfileID, false);
					}
					else {
									WSClient.writeToReport(LogStatus.FAIL, "Fetch Profile Operation is failed to retrieve the profile information");
					}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL Error! "+e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
