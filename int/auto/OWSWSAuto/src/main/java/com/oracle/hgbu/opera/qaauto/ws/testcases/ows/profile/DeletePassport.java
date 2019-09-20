package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;


public class DeletePassport extends WSSetUp {
	
//	@Test(groups = { "sanity","OWS","DeletePassport","Name" })
//	public void deletePassport38329() {
//		try {
//			
//			
//			String testName = "deletePassport_38322";
//			WSClient.startTest(testName, "Verify that the passport has been Deleted for the requested profileID", "sanity");
//			String interfaceName = OWSLib.getChannel();
//			String resortOperaValue = OPERALib.getResort();
//			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
//			WSClient.setData("{var_profileSource}", interfaceName);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			WSClient.setData("{var_extResort}", resortExtValue);
//
//			OPERALib.setOperaHeader(OPERALib.getUserName());
//			/****** Prerequisite : Creating a Profile with basic details*****/
//			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
//			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
//
//			System.out.println("Profile Creation is successful 1");
//			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
//				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
//			} else {
//				if (WSAssert.assertIfElementExists(createProfileResponseXML,
//						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {
//
//					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
//							"CreateProfileRS_ProfileIDList_UniqueID_ID",
//							com.oracle.hgbu.qa.ws.soap.client.WSClient.XMLType.RESPONSE);
//					WSClient.setData("{var_profileId}", operaProfileID);
//					WSClient.setData("{var_profileID}", operaProfileID);
//					
//					String documentNo ="IN121456";
//
//					WSClient.setData("{var_docnum}", documentNo);
//
//					String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
//					WSClient.setData("{var_docType}", docType);
//					OPERALib.setOperaHeader(OPERALib.getUserName());
//					// Prerequisite : Updating a profile with document details
//					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
//					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
//
//					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", false)) {
//
//						String query=WSClient.getQuery("QS_01");
//						String documentId=WSClient.getDBRow(query).get("DOCUMENT_ID");
//						
//						WSClient.setData("{var_docId}", documentId);
//						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortExtValue,
//								OWSLib.getChannelType(interfaceName),
//								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
//						
//						String req_deleteDoc = WSClient.createSOAPMessage("OWSDeletePassport", "DS_01");
//						String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);
//
//						WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeletePassportResponse_Result_resultStatusFlag","SUCCESS", false);
//							 query =WSClient.getQuery("QS_01");
//							String inactiveDate=WSClient.getDBRow(query).get("INACTIVE_DATE");
//							query=WSClient.getQuery("QS_02");
//							String todayDate=WSClient.getDBRow(query).get("CURRDATE");
//							
//							
//							
//							WSClient.writeToReport(LogStatus.INFO, "<b>Expected: "+todayDate+", Actual: " + inactiveDate+"</b>");	
//							
//						if(WSAssert.assertEquals(inactiveDate,todayDate, true)){
//							WSClient.writeToReport(LogStatus.PASS, "Inactive Date is populated as expected");
//					
//							}
//							else{
//								WSClient.writeToReport(LogStatus.FAIL, "Inactive Date is not populated");
//							
//							}
//					
//					}
//					else
//					{
//						WSClient.writeToReport(LogStatus.WARNING, "Insert Document Pre requisite failed");
//					}
//
//				}
//			}
//
//		} catch (Exception e) {
//		}
//	}
//	
//	
//	
//	@Test(groups = { "minimumRegression","OWS","DeletePassport","Name" })
//	public void deletePassport38322() {
//		try {
//			
//			
//			String testName = "deletePassport_38311";
//			WSClient.startTest(testName, "Verify that an error is populated when invalid profile ID is given", "minimumRegression");
//			String interfaceName = OWSLib.getChannel();
//			String resortOperaValue = OPERALib.getResort();
//			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
//
//			WSClient.setData("{var_profileSource}", interfaceName);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			WSClient.setData("{var_extResort}", resortExtValue);
//
//			OPERALib.setOperaHeader(OPERALib.getUserName());
//			/****** Prerequisite : Creating a Profile with basic details*****/
//			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
//			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
//
//			System.out.println("Profile Creation is successful 1");
//			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
//				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
//			} else {
//				if (WSAssert.assertIfElementExists(createProfileResponseXML,
//						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {
//
//					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
//							"CreateProfileRS_ProfileIDList_UniqueID_ID",
//							com.oracle.hgbu.qa.ws.soap.client.WSClient.XMLType.RESPONSE);
//					WSClient.setData("{var_profileId}", operaProfileID);
//					WSClient.setData("{var_profileID}", operaProfileID);
//					String documentNo ="IN121456";
//
//					WSClient.setData("{var_docnum}", documentNo);
//
//					String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
//					WSClient.setData("{var_docType}", docType);
//					OPERALib.setOperaHeader(OPERALib.getUserName());
//					// Prerequisite : Updating a profile with document details
//					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_01");
//					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
//
//					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", false)) {
//
//						String query=WSClient.getQuery("QS_01");
//						String documentId=WSClient.getDBRow(query).get("DOCUMENT_ID");
//						query=WSClient.getQuery("OWSUpdatePassport", "QS_02");
//						 LinkedHashMap<String, String> profileDetails = WSClient.getDBRow(query);
//							
//
//
//							operaProfileID = profileDetails.get("PROFILEID");
//							WSClient.writeToReport(LogStatus.INFO, "Testing with Profile Id : "+operaProfileID);
//							WSClient.setData("{var_profileId}", operaProfileID);
//						WSClient.setData("{var_docId}", documentId);
//						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortExtValue,
//								OWSLib.getChannelType(interfaceName),
//								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
//						WSClient.writeToReport(LogStatus.INFO, "*************VALIDATION WHEN INVALID PROFILE IS GIVEN***************");
//
//						String req_deleteDoc = WSClient.createSOAPMessage("OWSDeletePassport", "DS_01");
//						String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);
//						//DeletePassportResponse_Result_GDSError
//						if(WSAssert.assertIfElementExists(res_deleteDoc,"Result_Text_TextElement",true)){
//							WSClient.writeToReport(LogStatus.PASS, "Error Message : "+WSClient.getElementValue(res_deleteDoc,"Result_Text_TextElement",XMLType.RESPONSE));
//
//						}
//						if(WSAssert.assertIfElementExists(res_deleteDoc,"DeletePassportResponse_Result_GDSError",true)){
//							WSClient.writeToReport(LogStatus.PASS, "Error Message : "+WSClient.getElementValue(res_deleteDoc,"DeletePassportResponse_Result_GDSError",XMLType.RESPONSE));
//						}
//						if(WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeletePassportResponse_Result_resultStatusFlag","SUCCESS", true)){
//							WSClient.writeToReport(LogStatus.FAIL, "No Error Message is displayed even when Profile is invalid");
//
//						}	
//					
//					
//					}
//					else
//					{
//						WSClient.writeToReport(LogStatus.WARNING, "Insert Document Pre requisite failed");
//					}
//
//				}
//			}
//
//		} catch (Exception e) {
//		}
//	}
}
	
	
	
//	@Test(groups = { "minimumRegression", "updatePassport", "OWS","Name","in-QA"})
//	public void updatePassport_38691() {
//		try {
//			String testName = "deletePassport_38691";
//			WSClient.startTest(testName, "Verify the operation when all the fields are on the request", "minimumRegression");
//
//			
//			
//			
//			if(OperaPropConfig.getPropertyConfigResults(new String[] {"IdentificationType"})){
//			
//			String interfaceName=OWSLib.getChannel();
//			String resortOperaValue = OPERALib.getResort();
//			String resortExtValue=OWSLib.getChannelResort(resortOperaValue, interfaceName);
//			
//			WSClient.setData("{var_profileSource}", interfaceName);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			WSClient.setData("{var_extResort}", resortExtValue);
//			
//			String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
//
//			
//
//
//			OPERALib.setOperaHeader(OPERALib.getUserName());
//			
//			
//			
//			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
//			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
//
//			System.out.println("Profile Creation is successful");
//			// Prerequisite: Creating a Profile
//			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
//				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
//			} else {
//				if (WSAssert.assertIfElementExists(createProfileResponseXML,
//						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {
//
//					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
//							"CreateProfileRS_ProfileIDList_UniqueID_ID",
//							com.oracle.hgbu.qa.ws.soap.client.WSClient.XMLType.RESPONSE);
//					WSClient.setData("{var_profileId}", operaProfileID);
//					String documentNo = "IN1211233";
//					WSClient.setData("{var_docnum}", documentNo);
//					WSClient.setData("{var_docType}",docType);
//					WSClient.setData("{var_countryOfIssue}","US");
//					WSClient.setData("{var_placeOfIssue}","Texas");
//					WSClient.setData("{var_effectiveDate}","2017-11-04");
//					WSClient.setData("{var_primary}","false");
//
//					String updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_04");
//					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
//					
//					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", false)) {
//						documentNo = "IN1211234";
//						WSClient.setData("{var_docnum}", documentNo);
//						WSClient.setData("{var_docType}",docType);
//						WSClient.setData("{var_countryOfIssue}","US");
//						WSClient.setData("{var_placeOfIssue}","Texas");
//						WSClient.setData("{var_effectiveDate}","2017-11-04");
//						WSClient.setData("{var_primary}","false");
//
//						updateProfileReq = WSClient.createSOAPMessage("ChangeProfile", "DS_04");
//						updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
//						
//						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ChangeProfileRS_Success", false)) {
//						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortExtValue,
//								OWSLib.getChannelType(interfaceName),
//								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
//						
//						// Updating the document number for the corresponding document
//						
//					WSClient.setData("{var_profileId}", operaProfileID);
//					WSClient.setData("{var_profileID}", operaProfileID);
//
//					documentNo = "IN1211233";
//					WSClient.setData("{var_docnum}", documentNo);
//					WSClient.setData("{var_docType}", docType);
//					
//					
//					WSClient.setData("{var_countryOfIssue}","India");
//					WSClient.setData("{var_placeOfIssue}","Hyderabad");
//					WSClient.setData("{var_effectiveDate}","2017-11-12");
//					WSClient.setData("{var_primary}","true");
//					
//					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortExtValue, OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
//					
//					WSClient.writeToReport(LogStatus.INFO,"<b>"+"*********Validating when all the fields are given*********</b>");
//					String query=WSClient.getQuery("QS_01");
//					String documentId=WSClient.getDBRow(query).get("DOCUMENT_ID");
//					
//					WSClient.setData("{var_docId}", documentId);
//					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortExtValue,
//							OWSLib.getChannelType(interfaceName),
//							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
//					
//					String req_deleteDoc = WSClient.createSOAPMessage("OWSDeletePassport", "DS_01");
//					String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);
//						WSClient.writeToReport(LogStatus.INFO, "BRO");
//					WSAssert.assertIfElementValueEquals(res_deleteDoc, "DeletePassportResponse_Result_resultStatusFlag","SUCCESS", false);
//						 query =WSClient.getQuery("QS_01");
//						String inactiveDate=WSClient.getDBRow(query).get("INACTIVE_DATE");
//						query=WSClient.getQuery("QS_02");
//						String todayDate=WSClient.getDBRow(query).get("CURRDATE");
//						WSClient.writeToReport(LogStatus.INFO, "Expected: "+todayDate+", Actual: " + inactiveDate);	
//						
//					if(WSAssert.assertEquals(inactiveDate,todayDate, true)){
//						WSClient.writeToReport(LogStatus.PASS, "Inactive Date is populated as expected");
//				
//						}
//						else{
//							WSClient.writeToReport(LogStatus.FAIL, "Inactive Date is not populated as expected");
//						
//						}
//				
//				}
//				else
//				{
//					WSClient.writeToReport(LogStatus.WARNING, "Insert Document Pre requisite failed");
//				}
//
//					}
//				}
//			}}
//		}
//		
//			
//	catch (Exception e) {
//		}
//	}
//}

