package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import java.util.LinkedHashMap;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class CheckoutReservation {

	/*******  To check out a reservation and return the status of check out
	 * @throws Exception ***********/
	public static boolean checkOutReservation(String dataset) throws Exception {

		String dataSetNumber = dataset.substring(3, dataset.length());
		System.out.println("dataSetNumber: "+dataSetNumber);
		WSClient.setData("{var_resort}",OPERALib.getResort());
		WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));

		WSClient.writeToReport(LogStatus.INFO, "<b>"+"Preparing to Checkout the Reservation"+"</b>");

		//Apply Final Postings
		String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", dataset);
		String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);

		if(WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {

			WSClient.writeToReport(LogStatus.INFO, "<b>"+"Applied final postings"+"</b>");

			//Now Post Billing Payment
			String QS01 = WSClient.getQuery("PostBillingPayment", "QS_"+dataSetNumber);
			LinkedHashMap<String, String> resvMap = WSClient.getDBRow(QS01);
			WSClient.setData("{var_profileId}", resvMap.get("NAME_ID"));
			WSClient.setData("{var_payment}", resvMap.get("PAYMENT_METHOD"));
			WSClient.setData("{var_balance}", resvMap.get("BALANCE"));
			System.out.println("Initial Map: "+resvMap);

			//if Balance is non-zero then only PostBillingPayment
			if(!resvMap.get("BALANCE").equals("0")){
				String postBillingPaymentReq = WSClient.createSOAPMessage("PostBillingPayment", dataset);
				String postBillingPaymentRes = WSClient.processSOAPMessage(postBillingPaymentReq);

				if(WSAssert.assertIfElementExists(postBillingPaymentRes, "PostBillingPaymentRS_Success", true)) {

					//Again fetch balance
					resvMap = WSClient.getDBRow(QS01);
					System.out.println("Final Map: "+resvMap);
					//If Balance is zero then only, Posted bill payment successfully
					if(resvMap.get("BALANCE").equals("0")) {

						WSClient.writeToReport(LogStatus.INFO, "<b>"+"Posted bill payment successfully"+"</b>");
					}
					else {
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"Posting bill payment is unsuccessful"+"</b>");
					}

				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Posting bill payment failed"+"</b>");
				}

			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Balance for Post Billing Payment is zero!!"+"</b>");

				// Folio cannot be generated when Post Billing Payment is already zero, trying to directly checkout
				String checkOutResvReq = WSClient.createSOAPMessage("CheckoutReservation", dataset);
				String checkOutResvRes = WSClient.processSOAPMessage(checkOutResvReq);

				if (WSAssert.assertIfElementExists(checkOutResvRes, "CheckoutReservationRS_Success", true))
				{
					return true;
				}
				else
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Checkout Failed!!"+"</b>");
					return false;
				}
			}

			//Now Generate Folio
			String generateFolioReq = WSClient.createSOAPMessage("GenerateFolio", dataset);
			String generateFolioRes = WSClient.processSOAPMessage(generateFolioReq);

			if(WSAssert.assertIfElementExists(generateFolioRes, "GenerateFolioRS_Success", true)) {
				if(WSAssert.assertIfElementExists(generateFolioRes, "FolioWindow_Folios_Folio_InternalFolioWindowID", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Folio generated successfully"+"</b>");

					// Now Checkout Reservation
					String checkOutResvReq = WSClient.createSOAPMessage("CheckoutReservation", dataset);
					String checkOutResvRes = WSClient.processSOAPMessage(checkOutResvReq);

					if (WSAssert.assertIfElementExists(checkOutResvRes, "CheckoutReservationRS_Success", true))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Folio generation unsuccessful"+"</b>");
			}

		}
		else {
			WSClient.writeToReport(LogStatus.INFO, "<b>"+"Applying final postings failed"+"</b>");
		}

		return false;
	}

	/*******  To check out a reservation and return the status of check out
	 * @throws Exception ***********/
	public static boolean checkOut(String dataset) throws Exception {
		boolean resultFlag = false;
		WSClient.writeToReport(LogStatus.INFO, "<b>"+"Preparing to Checkout the Reservation"+"</b>");

		// Now Checkout Reservation
		String checkOutResvReq = WSClient.createSOAPMessage("CheckoutReservation", dataset);
		String checkOutResvRes = WSClient.processSOAPMessage(checkOutResvReq);

		if (WSAssert.assertIfElementExists(checkOutResvRes, "CheckoutReservationRS_Success", true)) {
			WSClient.writeToReport(LogStatus.PASS, "Checkout is successful");
			resultFlag = true;
		}
		else {
			resultFlag = false;
			WSClient.writeToReport(LogStatus.FAIL, "Checkout is failed");
		}
		return resultFlag;

	}

}
