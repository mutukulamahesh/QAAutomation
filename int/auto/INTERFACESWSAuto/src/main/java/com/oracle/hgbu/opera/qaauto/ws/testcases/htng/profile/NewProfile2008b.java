package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.profile;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class NewProfile2008b extends WSSetUp {
	@Test(groups = {"BAT", "NewProfile", "HTNG2008B", "HTNG" })
	@Parameters({"schema", "version"})
	public void newProfileCreationHtng(String schema, String version) {
		WSClient.startTest("newProfileCreation","Verify New Profile Creation via HTNG Interface","bat");
		String intParametersQuery = "", intParametersQuerySetID ="", fromAddress ="", interfaceSetupQuerySetID="", interfaceSetupQuery ="", extResort="";
		String sUsername="", sPassword="", sChain="", sResort="", sInterface="";
		LinkedHashMap<String,String> intParametersMap = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> interfaceSetupMap = new LinkedHashMap<String,String>();
		try {
			
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			sChain = OPERALib.getChain();
			sResort = OPERALib.getResort();
			sInterface = HTNGLib.getHTNGInterface();
			
			//Parameters required for the query
			WSClient.setData("{var_interface}", sInterface);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_resort}", sResort);
			intParametersQuerySetID = schema.equalsIgnoreCase("ASP") && !version.equalsIgnoreCase("5.5")? "QS_24": "QS_25";
			//intParametersQuery = WSClient.getQuery("HTNG2008NewProfile", intParametersQuerySetID, false);
			intParametersQuery = WSClient.getQuery("HTNG2008NewProfile", intParametersQuerySetID);
			intParametersMap = WSClient.getDBRow(intParametersQuery);
			System.out.println(intParametersMap);
			interfaceSetupQuerySetID = schema.equalsIgnoreCase("ASP") && !version.equalsIgnoreCase("5.5") ? "QS_26": "QS_27";
			interfaceSetupQuery = WSClient.getQuery("HTNG2008NewProfile", interfaceSetupQuerySetID, false);
			interfaceSetupMap = WSClient.getDBRow(interfaceSetupQuery);
			System.out.println(interfaceSetupMap);
			if(intParametersMap.size() > 0 && intParametersMap.get("PARAMETER_VALUE") != null)  {
					fromAddress = intParametersMap.get("PARAMETER_VALUE");
					extResort = interfaceSetupMap.get("EXTERNAL_RESORT");
					//Parameters required for the HTNG Header
					WSClient.setData("{var_userName}", sUsername);
					WSClient.setData("{var_password}", sPassword);
					WSClient.setData("{var_fromAddress}", fromAddress);
					WSClient.setData("{var_msgID}",UUID.randomUUID().toString());
					//Parameters required for NewProfile Request
					WSClient.setData("{var_profileSource}", sInterface);
					WSClient.setData("{var_nameType}", "GUEST");
					WSClient.setData("{var_extResort}", extResort);
					//Construct HTNG New Profile Request and Post it
					String newProfileReq = WSClient.createSOAPMessage("HTNG2008NewProfile", "DS_11");
					String newProfileRes = WSClient.processSOAPMessage(newProfileReq);
					
					if (WSAssert.assertIfElementContains(newProfileRes, "NewProfileResponse_Result_resultStatusFlag","SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(newProfileRes, "Result_IDs_UniqueID", false)) {
							WSClient.writeToReport(LogStatus.INFO, "New Profile is successfully created. "+"Opera Profile ID :"+WSClient.getElementValueByAttribute(newProfileRes, "Result_IDs_UniqueID",
									"Result_IDs_UniqueID_source", "OPERA", XMLType.RESPONSE));							
						}
						else {
							WSClient.writeToReport(LogStatus.FAIL, "New Profile creation is failed");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.FAIL, "New Profile creation is failed");
				   }
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "fromAddress attribute is not configured for "+sInterface);
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
