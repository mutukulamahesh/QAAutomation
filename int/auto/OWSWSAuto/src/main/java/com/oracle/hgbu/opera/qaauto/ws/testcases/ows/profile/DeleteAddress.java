package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class DeleteAddress extends WSSetUp {
	String profileID="";

	@Test(groups = { "sanity", "DeleteAddress", "OWS" ,"Name" })
	public void deleteAddress_38383() {
		try {
			String testName = "deleteAddress_38383";
			WSClient.startTest(testName, "Verify that the address record is deleted","sanity");
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"AddressType"})){
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				HashMap<String, String> address = new HashMap<String, String>();
				address=OPERALib.fetchAddressLOV();
				WSClient.setData("{var_city}", address.get("City"));
				WSClient.setData("{var_state}", address.get("State"));
				WSClient.setData("{var_country}", address.get("Country"));
				WSClient.setData("{var_zip}", address.get("Zip"));

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);

				if(profileID.equals(""))
					profileID=CreateProfile.createProfile("DS_12");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileID}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					String query=WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> add = WSClient.getDBRow(query);
					String addressid = add.get("ADDRESS_ID");
					WSClient.setData("{var_addressID}", addressid);

					// OWS delete Address

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String deleteAddressReq = WSClient.createSOAPMessage("OWSDeleteAddress", "DS_01");
					String deleteAddressResponseXML = WSClient.processSOAPMessage(deleteAddressReq);

					if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_resultStatusFlag", false)){
						if (WSAssert.assertIfElementValueEquals(deleteAddressResponseXML,
								"DeleteAddressResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String query1=WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> db = WSClient.getDBRow(query1);
							if (WSAssert.assertEquals("0", db.get("COUNT"), true))
								WSClient.writeToReport(LogStatus.PASS, "Address Deleted successfully");
							else
								WSClient.writeToReport(LogStatus.FAIL, "Error in deleting phone record");
						}
					}

					if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_faultcode", true)){
						if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_faultstring", true)){

							String message=WSClient.getElementValue(deleteAddressResponseXML,"DeleteAddressResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
						}
					}

					if (WSAssert.assertIfElementExists(deleteAddressResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(deleteAddressResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message+"</b>");
					}

					if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_OperaErrorCode", true)){

						String code=WSClient.getElementValue(deleteAddressResponseXML, "DeleteAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
					}

					if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_GDSError",true)){

						String message=WSClient.getElementValue(deleteAddressResponseXML, "DeleteAddressResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
					}




				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "DeleteAddress", "OWS" ,"Name" })

	public void deleteAddress_38390() {
		try {
			String testName = "deleteAddress_38390";
			WSClient.startTest(testName, "verify that error text is coming in response of delete address when no opera id is passed in request.","minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// OWS Delete Address

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String deleteAddressReq = WSClient.createSOAPMessage("OWSDeleteAddress", "DS_02");
			String deleteAddressResponseXML = WSClient.processSOAPMessage(deleteAddressReq);

			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_resultStatusFlag", false))
				WSAssert.assertIfElementValueEquals(deleteAddressResponseXML,"DeleteAddressResponse_Result_resultStatusFlag", "FAIL", false);

			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_faultcode", true)){
				if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_faultstring", true)){

					String message=WSClient.getElementValue(deleteAddressResponseXML,"DeleteAddressResponse_faultstring", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
				}
			}

			if (WSAssert.assertIfElementExists(deleteAddressResponseXML, "Result_Text_TextElement", true)) {

				String message = WSClient.getElementValue(deleteAddressResponseXML, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_OperaErrorCode", true)){

				String code=WSClient.getElementValue(deleteAddressResponseXML, "DeleteAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
			}

			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_GDSError",true)){

				String message=WSClient.getElementValue(deleteAddressResponseXML, "DeleteAddressResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
			}



		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "DeleteAddress", "OWS" ,"Name" })

	public void deleteAddress_38391() {
		try {
			String testName = "deleteAddress_38391";
			WSClient.startTest(testName, "verify that error text is coming in response of delete address when an invalid opera id is passed","minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			// OWS Delete Address

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String deleteAddressReq = WSClient.createSOAPMessage("OWSDeleteAddress", "DS_03");
			String deleteAddressResponseXML = WSClient.processSOAPMessage(deleteAddressReq);


			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_resultStatusFlag", false))
				WSAssert.assertIfElementValueEquals(deleteAddressResponseXML,"DeleteAddressResponse_Result_resultStatusFlag", "FAIL", false);

			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_faultcode", true)){
				if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_faultstring", true)){

					String message=WSClient.getElementValue(deleteAddressResponseXML,"DeleteAddressResponse_faultstring", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
				}
			}

			if (WSAssert.assertIfElementExists(deleteAddressResponseXML, "Result_Text_TextElement", true)) {

				String message = WSClient.getElementValue(deleteAddressResponseXML, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_OperaErrorCode", true)){

				String code=WSClient.getElementValue(deleteAddressResponseXML, "DeleteAddressResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
			}

			if(WSAssert.assertIfElementExists(deleteAddressResponseXML,"DeleteAddressResponse_Result_GDSError",true)){

				String message=WSClient.getElementValue(deleteAddressResponseXML, "DeleteAddressResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
			}



		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

}
