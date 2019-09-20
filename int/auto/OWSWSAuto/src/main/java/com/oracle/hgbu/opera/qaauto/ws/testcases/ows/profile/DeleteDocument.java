package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

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

public class DeleteDocument extends WSSetUp {



	public void setOwsHeader() throws Exception{
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}


	@Test(groups = { "sanity","OWS","DeleteDocument","Name" })
	public void deleteDocument38322() {
		try {
			String testName = "deleteDocument_38322";
			WSClient.startTest(testName, "Verify that the Document has been Deleted for the requested profileID", "sanity");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);
				String documentNo ="IN121456";

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {


					String documentId=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("DOCUMENT_ID");

					WSClient.setData("{var_docId}", documentId);
					setOwsHeader();

					String req_deleteDoc = WSClient.createSOAPMessage("OWSDeleteDocument", "DS_01");
					String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

					WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag","SUCCESS", false);
					String inactiveDate=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("INACTIVE_DATE");

					String todayDate=WSClient.getDBRow(WSClient.getQuery("QS_02")).get("CURRDATE");




					if(WSAssert.assertEquals(inactiveDate,todayDate, true)){
						WSClient.writeToReport(LogStatus.PASS, "Inactive Date is populated as expected(Document is Inactivated)");

					}
					else{
						WSClient.writeToReport(LogStatus.FAIL, "Inactive Date is not populated as expected");

					}

					WSClient.writeToReport(LogStatus.INFO, "Expected: "+todayDate+", Actual: " + inactiveDate);


				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "Change Profile Pre requisite failed");
				}

			}


		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression","OWS","DeleteDocument","Name" })
	public void deleteDocument38321() {
		try {


			String testName = "deleteDocument383122";
			WSClient.startTest(testName, "Verify that the error-message is present in the response when invalid document id is passed", "minimumRegression");



			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = "IN1211232";

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);


				String documentId=WSClient.getDBRow(WSClient.getQuery("OWSDeleteDocument","QS_03")).get("DOCUMENT_ID");

				WSClient.setData("{var_docId}", documentId);
				setOwsHeader();

				String req_deleteDoc = WSClient.createSOAPMessage("OWSDeleteDocument", "DS_01");
				String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

				if(WSAssert.assertIfElementExists(res_deleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag", true)){
					if(WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag","FAIL", false)){
						if(WSAssert.assertIfElementExists(res_deleteDoc,"Result_Text_TextElement", true))
						{
							WSClient.writeToReport(LogStatus.PASS,"<b>"+WSClient.getElementValue(res_deleteDoc,"Result_Text_TextElement" , XMLType.RESPONSE)+"</b>" );

						}

					}
				}
				else{
					WSClient.writeToReport(LogStatus.FAIL, "Wrong Schema populated in the response");
				}

			}


		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression","OWS","DeleteDocument","Name" })
	public void deleteDocument_109114() {
		try {


			String testName = "deleteDocument_109114";
			WSClient.startTest(testName, "Verify that the documents are not deleted when  valid profile id is passed in the request", "minimumRegression");



			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = "IN1211232";

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);


				//String documentId=WSClient.getDBRow(WSClient.getQuery("OWSDeleteDocument","QS_03")).get("DOCUMENT_ID");

				//WSClient.setData("{var_docId}", documentId);
				setOwsHeader();

				String req_deleteDoc = WSClient.createSOAPMessage("OWSDeleteDocument", "DS_02");
				String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

				if(WSAssert.assertIfElementExists(res_deleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag", true)){
					if(WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag","FAIL", false)){
						if(WSAssert.assertIfElementExists(res_deleteDoc,"Result_Text_TextElement", true))
						{
							WSClient.writeToReport(LogStatus.PASS,"<b>"+WSClient.getElementValue(res_deleteDoc,"Result_Text_TextElement" , XMLType.RESPONSE)+"</b>" );

						}

					}
				}
				else{
					WSClient.writeToReport(LogStatus.FAIL, "Wrong Schema populated in the response");
				}

			}


		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}



	@Test(groups = { "minimumRegression","OWS","DeleteDocument","Name" })
	public void deleteDocument_114021() {
		try {
			String testName = "deleteDocument_114021";
			WSClient.startTest(testName, "Verify that the error-message is displayed in the response when inactive document id is passed", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);
				String documentNo ="IN121456";

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {


					String documentId=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("DOCUMENT_ID");

					WSClient.setData("{var_docId}", documentId);
					setOwsHeader();

					String req_deleteDoc = WSClient.createSOAPMessage("OWSDeleteDocument", "DS_01");
					String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

					WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag","SUCCESS", false);
					String inactiveDate=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("INACTIVE_DATE");

					String todayDate=WSClient.getDBRow(WSClient.getQuery("QS_02")).get("CURRDATE");


					//////////////
					String RqdeleteDoc = WSClient.createSOAPMessage("OWSDeleteDocument", "DS_01");
					String RsdeleteDoc = WSClient.processSOAPMessage(RqdeleteDoc);

					WSAssert.assertIfElementValueEquals(RsdeleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag","FAIL", false);

					if(WSAssert.assertIfElementExists(RsdeleteDoc,"Result_Text_TextElement", true))
					{
						WSClient.writeToReport(LogStatus.PASS,"<b>"+WSClient.getElementValue(res_deleteDoc,"Result_Text_TextElement" , XMLType.RESPONSE)+"</b>" );

					}
					//////////////////


					if(WSAssert.assertEquals(inactiveDate,todayDate, true)){
						WSClient.writeToReport(LogStatus.PASS, "Inactive Date is populated as expected(Document is Inactivated)");

					}
					else{
						WSClient.writeToReport(LogStatus.FAIL, "Inactive Date is not populated as expected");

					}

					WSClient.writeToReport(LogStatus.INFO, "Expected: "+todayDate+", Actual: " + inactiveDate);


				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "Change Profile Pre requisite failed");
				}

			}


		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

	@Test(groups = { "minimumRegression","OWS","DeleteDocument","Name" })
	public void deleteDocument_114003() {
		try {
			String testName = "deleteDocument_114003";
			WSClient.startTest(testName, "Verify that the Document has been Deleted when only Document ID is given in the request", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String operaProfileID = CreateProfile.createProfile("DS_01");
			if(!operaProfileID.equals("error")){
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.setData("{var_profileID}", operaProfileID);
				String documentNo ="IN121456";

				WSClient.setData("{var_docnum}", documentNo);

				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// Prerequisite : Updating a profile with document details
				String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", true)) {


					String documentId=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("DOCUMENT_ID");

					WSClient.setData("{var_docId}", documentId);
					setOwsHeader();

					String req_deleteDoc = WSClient.createSOAPMessage("OWSDeleteDocument", "DS_03");
					String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

					WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeleteDocumentResponse_Result_resultStatusFlag","SUCCESS", false);
					String inactiveDate=WSClient.getDBRow(WSClient.getQuery("QS_01")).get("INACTIVE_DATE");

					String todayDate=WSClient.getDBRow(WSClient.getQuery("QS_02")).get("CURRDATE");




					if(WSAssert.assertEquals(inactiveDate,todayDate, true)){
						WSClient.writeToReport(LogStatus.PASS, "Inactive Date is populated as expected(Document is Inactivated)");

					}
					else{
						WSClient.writeToReport(LogStatus.FAIL, "Inactive Date is not populated as expected");

					}

					WSClient.writeToReport(LogStatus.INFO, "Expected: "+todayDate+", Actual: " + inactiveDate);


				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "Change Profile Pre requisite failed");
				}

			}


		} catch (Exception e) {WSClient.writeToReport(LogStatus.ERROR,"Exception Occured Due to "+e);
		}
	}

}
