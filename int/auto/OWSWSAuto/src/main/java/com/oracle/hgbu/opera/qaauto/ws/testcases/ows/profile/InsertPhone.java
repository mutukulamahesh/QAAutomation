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

public class InsertPhone extends WSSetUp {

	@Test(groups = { "sanity", "InsertPhone", "Name","OWS" ,"in-QA"})

	public void insertPhone_31524() {
		try {
			String testName = "insertPhone_31524";
			WSClient.startTest(testName,
					"Verify that phone record of a profile is inserted", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationMethod", "CommunicationType" })) {
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

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				String profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID1+"</b>");
					WSClient.setData("{var_profileID}", profileID1);
					WSClient.setData("{var_phoneRole}",
							OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
					WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));

					// OWS insert phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_01");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
							true)) {

						/*
						 * Verifying that the error message is populated on
						 * the response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Insert phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_OperaErrorCode", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */
						String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Insert Phone response is :--</b> " + message1);



					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_GDSError", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */


						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);

					}

					if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "Result_IDs_IDPair_operaId",
								false)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>succcessfully inserted phone</b>");
							String phoneid = WSClient.getElementValue(insertPhoneResponseXML,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_phoneID}", phoneid);

							WSClient.writeToReport(LogStatus.INFO, "<b>validating phone details</b>");

							String query=WSClient.getQuery("QS_01");

							//getting values from database

							LinkedHashMap<String, String> db = WSClient.getDBRow(query);

							//getting elements from request

							LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();



							String phone_number = WSClient.getElementValue(insertPhoneReq,
									"InsertPhoneRequest_NamePhone_PhoneNumber", XMLType.REQUEST);
							String phone_type = WSClient.getElementValue(insertPhoneReq,
									"InsertPhoneRequest_NamePhone_phoneType", XMLType.REQUEST);
							String phone_role = WSClient.getElementValue(insertPhoneReq,
									"InsertPhoneRequest_NamePhone_phoneRole", XMLType.REQUEST);
							expected.put("PHONE_NUMBER", phone_number);
							expected.put("PHONE_TYPE", phone_type);
							expected.put("PHONE_ROLE", phone_role);

							//comparing  the phnNumber,phoneType,phoneRole of database and of request


							WSAssert.assertEquals(expected, db, false);
						}

					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	//MinimumRegression:case1--duplicate phone cant be attached

	@Test(groups = { "minimumRegression","Name", "InsertPhone", "OWS" ,"in-QA"})

	public void insertPhone_38260() {
		try {
			String testName = "insertPhone_38260";
			WSClient.startTest(testName,
					"Verify that error message is populated when previously linked phone number is provided in the request",
					"minimumRegression");
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
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType","CommunicationMethod"})){

				WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
				//WSClient.setData("{var_phoneType}", "HOME");



				// Prerequisite 1 - create profile


				OPERALib.setOperaHeader(uname);
				String profileID=CreateProfile.createProfile("DS_11");
				if(!profileID.equals("error")){

					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");

					WSClient.setData("{var_profileID}", profileID);
					String query=WSClient.getQuery("OWSInsertPhone","QS_03");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
					String phoneNumber = phn.get("PHONE_NUMBER");

					WSClient.setData("{var_phoneNumber}", phoneNumber);
					WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
					WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
					//WSClient.setData("{var_phoneRole}", "PHONE");
					//WSClient.setData("{var_phoneType}", "HOME");

					// OWS Insert Phone
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq1 = WSClient.createSOAPMessage("OWSInsertPhone", "DS_02");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq1);

					WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "FAIL", false) ;

					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
							true)) {

						/*
						 * Verifying that the error message is populated on
						 * the response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The TEXT displayed in the Insert phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_OperaErrorCode", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */
						String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Insert Phone response is :--</b> " + message1);



					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_GDSError", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */


						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);

					}







				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
				}

			} }catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
			}

	}

	//minimumRegression:case2---mandatory as well as optional fields


	@Test(groups = { "minimumRegression", "InsertPhone","Name", "OWS","in-QA" })

	public void insertPhone_38261() {
		try {
			String testName = "insertPhone_38261";
			WSClient.startTest(testName,
					"Verify that phone number is attached to profile when the mandatory and optional fields are provided in request",
					"minimumRegression");
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
			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType","CommunicationMethod"})){

				// Prerequisite 1 - create profile OPERALib.setOperaHeader(uname);
				OPERALib.setOperaHeader(uname);

				String profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error")){

					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
					WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
					WSClient.setData("{var_primary}","true");
					//WSClient.setData("{var_phoneRole}", "PHONE");
					//WSClient.setData("{var_phoneType}", "HOME");

					// OWS Insert Phone
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_03");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
							true)) {

						/*
						 * Verifying that the error message is populated on
						 * the response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The TEXT displayed in the Insert phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_OperaErrorCode", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */
						String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Insert Phone response is :---</b> " + message1);



					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_GDSError", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */


						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);

					}

					if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "Result_IDs_IDPair_operaId",
								false)) {
							String phoneid = WSClient.getElementValue(insertPhoneResponseXML,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);

							WSClient.writeToReport(LogStatus.PASS, "successfully inserted phone");
							WSClient.setData("{var_phoneID}", phoneid);

							WSClient.writeToReport(LogStatus.INFO, "<b>validating phone details</b>");

							String query=WSClient.getQuery("QS_02");

							//getting values from database

							LinkedHashMap<String, String> db = WSClient.getDBRow(query);

							//getting elements from request

							HashMap<String, String> xpath = new HashMap<String, String>();
							xpath.put("InsertPhoneRequest_NamePhone_PhoneNumber", "InsertPhoneRequest_NamePhone");
							xpath.put("InsertPhoneRequest_NamePhone_phoneType", "InsertPhoneRequest_NamePhone");
							xpath.put("InsertPhoneRequest_NamePhone_phoneRole", "InsertPhoneRequest_NamePhone");
							xpath.put("InsertPhoneRequest_NamePhone_displaySequence", "InsertPhoneRequest_NamePhone");
							xpath.put("InsertPhoneRequest_NamePhone_primary", "InsertPhoneRequest_NamePhone");
							LinkedHashMap<String, String> expected = WSClient.getSingleNodeList(insertPhoneReq, xpath,
									false, XMLType.REQUEST);
							//String primary_yn = WSClient.getElementValue(insertPhoneReq,"InsertPhoneRequest_NamePhone_primary", XMLType.REQUEST);

							//if (primary_yn.equalsIgnoreCase("true"))
							//expected.put("primary1", "Y");

							//if (primary_yn.equalsIgnoreCase("false"))
							//expected.put("primary1", "N");


							//comparing the values passed in request and database


							WSAssert.assertEquals(expected, db, false);
						}

					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	//MinimumRegression:case:3--phone number passed as empty

	@Test(groups = { "minimumRegression", "Name","InsertPhone", "OWS","in-QA" })

	public void insertPhone_38263() {
		try {
			String testName = "insertPhone_38263";
			WSClient.startTest(testName,
					"verify that error is coming in response when phone number is missing in InsertPhone request",
					"minimumRegression");
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

			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType","CommunicationMethod"})){

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);

				String profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error")){

					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
					WSClient.setData("{var_phoneType}",
							OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
					//WSClient.setData("{var_phoneRole}", "PHONE");
					//WSClient.setData("{var_phoneType}", "HOME");
					// OWS Insert Phone
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_04");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
							true)) {

						/*
						 * Verifying that the error message is populated on
						 * the response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The TEXT displayed in the Insert phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_OperaErrorCode", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */
						String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Insert Phone response is : ----  </b> " + message1);



					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_GDSError", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */


						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the Insert Phone response is : </b>" + message);

					}

					if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
						WSClient.writeToReport(LogStatus.PASS, "Insert Phone cannot be done without phone number");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked :  Profile not created");
				}

			}} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
			}

	}
	//minimumRegression:case:4--when NameID is missing in insertPhone request

	@Test(groups = { "minimumRegression", "InsertPhone","Name", "OWS","in-QA" })

	public void insertPhone_38264() {
		try {
			String testName = "insertPhone_38264";
			WSClient.startTest(testName,
					"Verify that error is coming in response  when name id is missing in insert phone request",
					"minimumRegression");
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

			WSClient.setData("{var_phoneRole}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
			WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
			//WSClient.setData("{var_phoneRole}", "PHONE");
			//WSClient.setData("{var_phoneType}", "HOME");




			// OWS Insert Phone
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_05");
			String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
			if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
					true)) {

				/*
				 * Verifying that the error message is populated on
				 * the response
				 */

				String message = WSAssert.getElementValue(insertPhoneResponseXML,
						"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the Insert phone response is----</b> :" + message);
			}
			if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
					"InsertPhoneResponse_Result_OperaErrorCode", true)) {

				/*
				 * Verifying whether the error Message is populated
				 * on the response
				 */
				String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
						"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OPERA error displayed in the Insert Phone response is :----</b> " + message1);



			}
			if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
					"InsertPhoneResponse_Result_GDSError", true)) {

				/*
				 * Verifying whether the error Message is populated
				 * on the response
				 */


				String message = WSAssert.getElementValue(insertPhoneResponseXML,
						"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The GDSerror displayed in the Insert Phone response is :---- </b> " + message);

			}

			if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
					"InsertPhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
				WSClient.writeToReport(LogStatus.PASS, "NameID cant be empty in InsertPhoneRequest");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	//minimumRegression:case5:--NameID passed as string other than numeric

	@Test(groups = { "minimumRegression", "InsertPhone","Name", "OWS" ,"in-QA"})

	public void insertPhone_38265() {
		try {
			String testName = "insertPhone_38265";
			WSClient.startTest(testName,
					"Verify that error is coming in response  when invalid name id is provided to request of insert phone",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel); //
			WSClient.setData("{var_phoneRole}", //
					OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase()); //
			WSClient.setData("{var_phoneType}", //
					OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
			//WSClient.setData("{var_phoneRole}", "PHONE");
			//WSClient.setData("{var_phoneType}", "HOME"); // OWS Insert Phone
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_06");
			String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
			if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
					true)) {

				/*
				 * Verifying that the error message is populated on
				 * the response
				 */

				String message = WSAssert.getElementValue(insertPhoneResponseXML,
						"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the Insert phone response is----</b> :" + message);
			}
			if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
					"InsertPhoneResponse_Result_OperaErrorCode", true)) {

				/*
				 * Verifying whether the error Message is populated
				 * on the response
				 */
				String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
						"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OPERA error displayed in the Insert Phone response is :---    </b> " + message1);



			}
			if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
					"InsertPhoneResponse_Result_GDSError", true)) {

				/*
				 * Verifying whether the error Message is populated
				 * on the response
				 */


				String message = WSAssert.getElementValue(insertPhoneResponseXML,
						"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The GDSerror displayed in the Insert Phone response is :---    </b>" + message);

			}

			if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
					"InsertPhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
				WSClient.writeToReport(LogStatus.PASS, "NameID should be only numeric");
			}
			else
				WSClient.writeToReport(LogStatus.FAIL, "Insert phone is successful!!!ERROR");


		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	//minimumRegression case:passing invalid phonetype

	@Test(groups = { "minimumRegression", "InsertPhone", "Name","OWS" ,"in-QA"})

	public void insertPhone_42205() {
		try {
			String testName = "insertPhone_42205";
			WSClient.startTest(testName,
					"Verify that error message is populated when inalid phoneType is passed", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationMethod"})) {
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

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				String profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID1+"</b>");
					WSClient.setData("{var_profileID}", profileID1);
					WSClient.setData("{var_phoneRole}",
							OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());



					WSClient.setData("{var_phoneType}",  WSClient.getKeywordData("{KEYWORD_RANDSTR_6}"));

					// OWS insert phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_01");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
							true)) {

						/*
						 * Verifying that the error message is populated on
						 * the response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Insert phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_OperaErrorCode", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */
						String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Insert Phone response is :--</b> " + message1);



					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_GDSError", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */


						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);

					}

					if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
						WSClient.writeToReport(LogStatus.PASS,"Can't insert phone with invalid phoneType");

					}
					else
						WSClient.writeToReport(LogStatus.FAIL,"Phone got inserted!ERROR!!!!");



				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}
	//	@Test(groups = { "minimumRegression", "InsertPhone", "Name","OWS" ,"in-QA"})
	//
	//	public void insertPhone_EMAIL() {
	//		try {
	//			String testName = "insertPhone_EMAIL";
	//			WSClient.startTest(testName,
	//					"Verify that phone record of a profile is inserted", "minimumRegression");
	//			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationMethod", "CommunicationType" })) {
	//				String resortOperaValue = OPERALib.getResort();
	//				String chain = OPERALib.getChain();
	//				WSClient.setData("{var_chain}", chain);
	//				WSClient.setData("{var_resort}", resortOperaValue);
	//
	//				String uname = OPERALib.getUserName();
	//				String pwd = OPERALib.getPassword();
	//				String channel = OWSLib.getChannel();
	//				String channelType = OWSLib.getChannelType(channel);
	//				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//
	//				// Prerequisite 1 - create profile
	//				OPERALib.setOperaHeader(uname);
	//				String profileID1 = CreateProfile.createProfile("DS_01");
	//				if (!profileID1.equals("error")) {
	//
	//					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID1+"</b>");
	//					WSClient.setData("{var_profileID}", profileID1);
	//						WSClient.setData("{var_phoneRole}",
	//								OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_02").toUpperCase());
	//
	//						WSClient.setData("{var_email}", "HYD.INDIA@ORACLE.COM");
	//
	//						WSClient.setData("{var_phoneType}",  OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02").toUpperCase());
	//
	//						// OWS insert phone
	//
	//						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//						String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_07");
	//						String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
	//						if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
	//								true)) {
	//
	//							/*
	//							 * Verifying that the error message is populated on
	//							 * the response
	//							 */
	//
	//							String message = WSAssert.getElementValue(insertPhoneResponseXML,
	//									"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
	//							WSClient.writeToReport(LogStatus.INFO,
	//									"<b>The error displayed in the Insert phone response is----</b> :" + message);
	//						}
	//						if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
	//								"InsertPhoneResponse_Result_OperaErrorCode", true)) {
	//
	//							/*
	//							 * Verifying whether the error Message is populated
	//							 * on the response
	//							 */
	//							String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
	//									"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//
	//								WSClient.writeToReport(LogStatus.INFO,
	//										"<b>The OPERA error displayed in the Insert Phone response is :--</b> " + message1);
	//
	//
	//
	//						}
	//						if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
	//								"InsertPhoneResponse_Result_GDSError", true)) {
	//
	//							/*
	//							 * Verifying whether the error Message is populated
	//							 * on the response
	//							 */
	//
	//
	//							String message = WSAssert.getElementValue(insertPhoneResponseXML,
	//									"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);
	//
	//								WSClient.writeToReport(LogStatus.INFO,
	//										"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);
	//
	//						}
	//
	//						if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
	//								"InsertPhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
	//							WSClient.writeToReport(LogStatus.PASS,"Can't insert phone with invalid phoneType");
	//
	//						}
	//						else
	//							WSClient.writeToReport(LogStatus.FAIL,"Phone got inserted!ERROR!!!!");
	//
	//
	//
	//				} else {
	//					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
	//				}
	//
	//			}
	//		} catch (Exception e) {
	//			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
	//		}
	//
	//	}
	//MinimumRegression:case:--invalid phoneRole passed

	@Test(groups = { "minimumRegression", "Name","InsertPhone", "OWS","in-QA" })

	public void insertPhone_42226() {
		try {
			String testName = "insertPhone_42226";
			WSClient.startTest(testName,
					"verify that error message is coming on response when invalid phonerole is passed",
					"minimumRegression");
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

			if(OperaPropConfig.getPropertyConfigResults(new String[]{"CommunicationType","CommunicationMethod"})){

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);

				String profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error")){

					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile created----"+profileID+"</b>");
					WSClient.setData("{var_profileID}", profileID);
					WSClient.setData("{var_phoneRole}", WSClient.getKeywordData("{KEYWORD_RANDSTR_6}"));
					WSClient.setData("{var_phoneType}",
							OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));

					// OWS Insert Phone
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_01");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
							true)) {

						/*
						 * Verifying that the error message is populated on
						 * the response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The TEXT displayed in the Insert phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_OperaErrorCode", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */
						String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Insert Phone response is : ----  </b> " + message1);



					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_GDSError", true)) {

						/*
						 * Verifying whether the error Message is populated
						 * on the response
						 */


						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the Insert Phone response is : </b>" + message);

					}

					if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "FAIL", false)) {
						WSClient.writeToReport(LogStatus.PASS, "InsertPhone can't be done without a valid phoneRole");
					}
					else
						WSClient.writeToReport(LogStatus.FAIL, "InsertPhone successful!!!ERROR");

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked :  Profile not created");
				}

			}} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
			}


	}
	@Test(groups = { "minimumRegression", "Name", "InsertPhone", "OWS"})

	public void insertPhone_145011() {
		try {
			String testName = "insertPhone_145011";
			WSClient.startTest(testName,
					"Verify that if a new Primary phone is inserted when there is an existing primary phone for a given profile, the new one preceeds the old one",
					"minimumRegression");
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
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

				WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
				// WSClient.setData("{var_phoneType}", "HOME");

				// Prerequisite 1 - create profile

				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_CreateProfile_52");
				if (!profileID.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile created----" + profileID + "</b>");

					WSClient.setData("{var_profileID}", profileID);
					String query = WSClient.getQuery("OWSInsertPhone", "QS_03");
					LinkedHashMap<String, String> phn = WSClient.getDBRow(query);
					String phoneNumber = phn.get("PHONE_NUMBER");

					WSClient.setData("{var_phoneNumber}", phoneNumber);
					WSClient.setData("{var_phoneRole}",
							OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
					WSClient.setData("{var_phoneType}",
							OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
					// WSClient.setData("{var_phoneRole}", "PHONE");
					// WSClient.setData("{var_phoneType}", "HOME");

					// OWS Insert Phone
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq1 = WSClient.createSOAPMessage("OWSInsertPhone", "DS_InsertPhone_09");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq1);

					if(WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "SUCCESS", true)){


						String phoneId = WSClient.getElementValue(insertPhoneResponseXML, "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
						System.out.println("phoneID= "+phoneId);
						WSClient.setData("{var_phoneId}", phoneId);
						String QS04 = WSClient.getQuery("QS_04");
						String pFlag = WSClient.getDBRow(QS04).get("PRIMARY_YN");

						if(WSAssert.assertEquals("Y", pFlag, true))
						{
							WSClient.writeToReport(LogStatus.PASS, "Second Phone Number is Made Primary Successfully!");
						}
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Second Phone Number is not made Primary!!");
						}


						if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
								true)) {

							/*
							 * Verifying that the error message is populated on the
							 * response
							 */

							String message = WSAssert.getElementValue(insertPhoneResponseXML,
									"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The TEXT displayed in the Insert phone response is----</b> :" + message);
						}
						if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", true)) {

							/*
							 * Verifying whether the error Message is populated on
							 * the response
							 */
							String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
									"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OPERA error displayed in the Insert Phone response is :--</b> " + message1);

						}
						if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_GDSError",
								true)) {

							/*
							 * Verifying whether the error Message is populated on
							 * the response
							 */

							String message = WSAssert.getElementValue(insertPhoneResponseXML,
									"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

							WSClient.writeToReport(LogStatus.INFO,
									"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);

						}

					}else{WSClient.writeToReport(LogStatus.FAIL, "SUCCESS Flag is not populated in the Response");}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}

	// minimumRegression:case2---mandatory as well as optional fields

	/*	@Test(groups = { "minimumRegression", "InsertPhone", "Name", "OWS", "in-QA" })

			public void HHOCM_145_02() {
				try {
					String testName = "HHOCM_145_02";
					WSClient.startTest(testName,
							"Verify that if display sequences are inserted properly along with the phone numbers(default value assigned is 0).",
							"minimumRegression");
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
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationType", "CommunicationMethod" })) {

						// Prerequisite 1 - create profile
						// OPERALib.setOperaHeader(uname);
						OPERALib.setOperaHeader(uname);

						String profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile created----" + profileID + "</b>");
							WSClient.setData("{var_profileID}", profileID);
							WSClient.setData("{var_phoneRole}",
									OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
							WSClient.setData("{var_phoneType}",
									OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
							WSClient.setData("{var_primary}", "true");
							// WSClient.setData("{var_phoneRole}", "PHONE");
							// WSClient.setData("{var_phoneType}", "HOME");

							// OWS Insert Phone
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_InsertPhone_10");
							String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
							if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
									true)) {


	 * Verifying that the error message is populated on the
	 * response


								String message = WSAssert.getElementValue(insertPhoneResponseXML,
										"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The TEXT displayed in the Insert phone response is----</b> :" + message);
							}
							if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
									"InsertPhoneResponse_Result_OperaErrorCode", true)) {


	 * Verifying whether the error Message is populated on
	 * the response

								String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
										"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OPERA error displayed in the Insert Phone response is :---</b> " + message1);

							}
							if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_GDSError",
									true)) {


	 * Verifying whether the error Message is populated on
	 * the response


								String message = WSAssert.getElementValue(insertPhoneResponseXML,
										"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);

							}

							if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
									"InsertPhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "Result_IDs_IDPair_operaId",
										false)) {
									String phoneid = WSClient.getElementValue(insertPhoneResponseXML,
											"Result_IDs_IDPair_operaId", XMLType.RESPONSE);

									WSClient.writeToReport(LogStatus.PASS, "successfully inserted phone");
									WSClient.setData("{var_phoneID}", phoneid);

									WSClient.writeToReport(LogStatus.INFO, "<b>validating phone details</b>");

									String query = WSClient.getQuery("QS_02");

									// getting values from database

									LinkedHashMap<String, String> db = WSClient.getDBRow(query);

									// getting elements from request

									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("InsertPhoneRequest_NamePhone_PhoneNumber", "InsertPhoneRequest_NamePhone");
									xpath.put("InsertPhoneRequest_NamePhone_phoneType", "InsertPhoneRequest_NamePhone");
									xpath.put("InsertPhoneRequest_NamePhone_phoneRole", "InsertPhoneRequest_NamePhone");
									xpath.put("InsertPhoneRequest_NamePhone_displaySequence", "InsertPhoneRequest_NamePhone");
									xpath.put("InsertPhoneRequest_NamePhone_primary", "InsertPhoneRequest_NamePhone");
									LinkedHashMap<String, String> expected = WSClient.getSingleNodeList(insertPhoneReq, xpath,
											false, XMLType.REQUEST);
									// String primary_yn =
									// WSClient.getElementValue(insertPhoneReq,"InsertPhoneRequest_NamePhone_primary",
									// XMLType.REQUEST);

									// if (primary_yn.equalsIgnoreCase("true"))
									// expected.put("primary1", "Y");

									// if (primary_yn.equalsIgnoreCase("false"))
									// expected.put("primary1", "N");

									// comparing the values passed in request and
									// database

									System.out.println("expected: "+expected);
									if(!(expected.containsKey("displaySequence1")))
									{
										expected.put("displaySequence1", "0");
									}

									WSAssert.assertEquals(expected, db, false);
								}

							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
						}
					}
				} catch (Exception e) {
					WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
				}

			}*/

	@Test(groups = { "minimumRegression", "InsertPhone", "Name", "OWS", "in-QA" })

	public void insertPhone_675765() {
		try {
			String testName = "insertPhone_675765";
			WSClient.startTest(testName, "Verify that if phoneFormat(TEXT/number) are inserted properly along with the phone", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "CommunicationMethod", "CommunicationType" })) {
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

				// Prerequisite 1 - create profile
				OPERALib.setOperaHeader(uname);
				String profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile created----" + profileID1 + "</b>");
					WSClient.setData("{var_profileID}", profileID1);
					WSClient.setData("{var_phoneRole}",
							OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
					WSClient.setData("{var_phoneType}",
							OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));

					// OWS insert phone

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String insertPhoneReq = WSClient.createSOAPMessage("OWSInsertPhone", "DS_InsertPhone_11");
					String insertPhoneResponseXML = WSClient.processSOAPMessage(insertPhoneReq);
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_Text",
							true)) {

						/*
						 * Verifying that the error message is populated on the
						 * response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_Text", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the Insert phone response is----</b> :" + message);
					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_OperaErrorCode", true)) {

						/*
						 * Verifying whether the error Message is populated on
						 * the response
						 */
						String message1 = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_OperaErrorCode", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OPERA error displayed in the Insert Phone response is :--</b> " + message1);

					}
					if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "InsertPhoneResponse_Result_GDSError",
							true)) {

						/*
						 * Verifying whether the error Message is populated on
						 * the response
						 */

						String message = WSAssert.getElementValue(insertPhoneResponseXML,
								"InsertPhoneResponse_Result_GDSError", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the Insert Phone response is :---</b> " + message);

					}

					if (WSAssert.assertIfElementValueEquals(insertPhoneResponseXML,
							"InsertPhoneResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(insertPhoneResponseXML, "Result_IDs_IDPair_operaId",
								false)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>succcessfully inserted phone</b>");
							String phoneid = WSClient.getElementValue(insertPhoneResponseXML,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_phoneID}", phoneid);

							WSClient.writeToReport(LogStatus.INFO, "<b>validating phone details</b>");

							String query = WSClient.getQuery("QS_01");

							// getting values from database

							LinkedHashMap<String, String> db = WSClient.getDBRow(query);

							// getting elements from request

							LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();

							String phone_number = WSClient.getElementValue(insertPhoneReq,
									"InsertPhoneRequest_NamePhone_PhoneNumber", XMLType.REQUEST);
							String phone_type = WSClient.getElementValue(insertPhoneReq,
									"InsertPhoneRequest_NamePhone_phoneType", XMLType.REQUEST);
							String phone_role = WSClient.getElementValue(insertPhoneReq,
									"InsertPhoneRequest_NamePhone_phoneRole", XMLType.REQUEST);
							expected.put("PHONE_NUMBER", phone_number);
							expected.put("PHONE_TYPE", phone_type);
							expected.put("PHONE_ROLE", phone_role);

							// comparing the phnNumber,phoneType,phoneRole of
							// database and of request

							WSAssert.assertEquals(expected, db, false);
						}

					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Profile not created");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}

	}


}
