package com.oracle.hgbu.opera.qaauto.ws.owsMigration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.serial.SerialArray;
import javax.xml.soap.*;
import javax.xml.ws.Service;

import org.apache.commons.io.IOUtils;

import com.oracle.hgbu.opera.qaauto.ws.common.utils.ReportFormatter;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.TrustModifier;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class SOAPClient {

	public static void main(String args[]) {

        String soapEndpointUrl = "http://den02qoj.us.oracle.com:3066/ows_ws_51/Name.asmx";
        String soapActionPath = "D://testTarget.xml";
        String soapAction; 
        soapAction = "http://webservices.micros.com/ows/5.1/Name.wsdl#FetchName";
		try {
			soapActionPath = new String(Files.readAllBytes(Paths.get(soapActionPath)));
			HashMap<String, String> out = postTemp(soapActionPath, soapEndpointUrl, soapAction);
			System.out.println(out);
			System.out.println(out.get("responseMessage"));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
	
	
	
	public static HashMap<String, String> processSOAPMessage(String v5Req, String cloudReq, HashMap<String, String> reqDetails){
		HashMap<String, String> responseSoap = new HashMap<String, String>();
		
		String soapEndpointUrl = reqDetails.get("SOAP_EndPointURL");
		String soapAction = reqDetails.get("SOAP_Action");
		String soapCloudEndpointUrl = reqDetails.get("SOAP_Cloud_EndPointURL");
		HashMap<String, String> out;
		HashMap<String, String> outCloud;
		
		WSLib.writeToReport(LogStatus.INFO, "Endpoint >> " + soapEndpointUrl);
		out = postTemp(v5Req, soapEndpointUrl, soapAction);
		
		if (!out.get("responseCode").equals("200")) {
			if (out.get("responseCode").equals("500")) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				out = postTemp(v5Req, soapEndpointUrl, soapAction);
			} else if (out.get("responseCode").equals("302")) {
				out = postTemp(v5Req, soapEndpointUrl, soapAction);
				out = postTemp(v5Req, out.get("redirectedUrl"), soapAction);
			}
		}
		responseSoap.put("resV5", out.get("responseMessage"));
		WSLib.writeToReport(LogStatus.INFO, " V5 Request and Response");
		logResults(out, v5Req, out.get("responseMessage"), soapAction);
		
		if (!cloudReq.isEmpty()) {
			
		
		WSLib.writeToReport(LogStatus.INFO, "Endpoint >> " + soapCloudEndpointUrl);
		outCloud = postTemp(cloudReq, soapCloudEndpointUrl, soapAction);
		if (!outCloud.get("responseCode").equals("200")) {
			if (outCloud.get("responseCode").equals("500")) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				outCloud = postTemp(v5Req, soapCloudEndpointUrl, soapAction);
			} else if (outCloud.get("responseCode").equals("302")) {
				outCloud = postTemp(v5Req, soapCloudEndpointUrl, soapAction);
				outCloud = postTemp(v5Req, out.get("redirectedUrl"), soapAction);
			}
		}		
		responseSoap.put("resCloud", outCloud.get("responseMessage"));
		WSLib.writeToReport(LogStatus.INFO, " V5 Request and Response");
		logResults(outCloud, cloudReq, outCloud.get("responseMessage"), soapAction);
		}
		
		
		return responseSoap;
		
	}
	
	private static void logResults(HashMap<String, String> out, String strToPost, String responseMsg, String soapAction) {
		WSLib.writeToReport(LogStatus.INFO, ReportFormatter.printReqResp(strToPost, responseMsg,
				soapAction, out.get("responseCode"), soapAction));
		long seconds = Long.parseLong(out.get("timeTaken_seconds"));
		long remainingMilliSeconds = (Long.parseLong(out.get("timeTaken_milliseconds")))%1000;
		if(seconds > 0)
			WSLib.writeToReport(LogStatus.INFO, "Time Taken >> " + out.get("timeTaken_milliseconds") +" Milliseconds ("+seconds+ " Second(s) "+remainingMilliSeconds+" Milliseconds)");
		else
			WSLib.writeToReport(LogStatus.INFO, "Time Taken >> " + out.get("timeTaken_milliseconds") +" Milliseconds ( < 1 Second )");
		WSLib.writeToReport(LogStatus.INFO, "Response status code that is received: " + out.get("responseCode"));
		
	}

	
	public static void sampleTest() {
		// TODO Auto-generated method stub
		String endPointUrl = "https://nplrposb1.us.oracle.com:9015/OPERAOSB/OPERA_OWS/OWS_WS_51/Reservation?wsdl"; 
		URL oURL;
		Map<String, List<String>> output;
		try {
			oURL = new URL(endPointUrl);
			HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
			long startTime = Calendar.getInstance().getTime().getTime();
			TrustModifier.relaxHostChecking(con);
			con.getInputStream();
			
			output = con.getRequestProperties();
//			con.get
			for(Map.Entry<String, List<String>> pair: output.entrySet()){
				System.out.println("Key: " + pair.getKey());
				System.out.println(pair.getValue().toString());
			}
			
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
    private static HashMap<String, String> postTemp(String xml, String endPointUrl, String soapAction) {
		HashMap<String, String> output = new HashMap<String, String>();
		try {
			URL oURL = new URL(endPointUrl);
			HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
			long startTime = Calendar.getInstance().getTime().getTime();
			TrustModifier.relaxHostChecking(con);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
			con.setRequestProperty("SOAPAction", soapAction);
			con.setDoOutput(true);
			String response = "";
			OutputStream reqStream = con.getOutputStream();
//			System.out.println("reqStream -- "+ reqStream);
			reqStream.write(xml.getBytes());
//			System.out.println(reqStream);
			int result = con.getResponseCode();
			long endTime = Calendar.getInstance().getTime().getTime();
			if (result == 200) {
				InputStream resStream = con.getInputStream();
				StringWriter responseWriter = new StringWriter();
				IOUtils.copy(resStream, responseWriter, "UTF-8");
				String reSXmlStr = responseWriter.getBuffer().toString().replaceAll("\n|\r", "");
				 System.out.println("Response Message: " + reSXmlStr);
				response = reSXmlStr;
			} else if (result == 302) {

//				writeToReport(LogStatus.INFO, "Response status code that is received: " + result);
//				writeToReport(LogStatus.INFO, "Url redirected to >> " + con.getHeaderField("Location"));
				output.put("redirectedUrl", con.getHeaderField("Location"));
			}

			else {
				InputStream resStream = con.getErrorStream();
				StringWriter responseWriter = new StringWriter();
				IOUtils.copy(resStream, responseWriter, "UTF-8");
				String reSXmlStr = responseWriter.getBuffer().toString().replaceAll("\n|\r", "");
				// System.out.println("Response Message: " + reSXmlStr);
				response = reSXmlStr;
			}
			output.put("responseCode", String.valueOf(result));
			output.put("responseMessage", response);
//			OperationTime.insertTime(lastRunData.get("operationKey"), startTime,
//					endTime - startTime, result);

			long timeInMilliseconds = endTime - startTime;
			long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds);
			long timeInminutes = TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds);

			output.put("timeTaken_milliseconds", String.valueOf(timeInMilliseconds));
			output.put("timeTaken_seconds", String.valueOf(timeInSeconds));
			output.put("timeTaken_minutes", String.valueOf(timeInminutes));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return output;

	}
}
