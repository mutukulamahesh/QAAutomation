package com.oracle.hgbu.opera.qaauto.ws.owsMigration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeMethod;

import com.oracle.hgbu.opera.qaauto.ws.common.utils.ExcelUtil;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.DataGen;
import com.relevantcodes.customextentreports.ExtentReports;
import com.relevantcodes.customextentreports.NetworkMode;

public class testSetUp {
	
	public static List<HashMap<String, String>> data_EndPointURL = new ArrayList<HashMap<String, String>>();
//	public static HashMap<String, String> data_SOAPAction = new HashMap<>();
	public static HashMap<String, HashMap<String, String>> serviceSetUpData;
	private static String testName = null;
	private static String reqSOAP = null;
	private static String environtment = null;
	private static String reqPath = null;
	public static String v5ResponseFileName = null;
	public static String cloudResponseFileName = null;
	public static String[] testNGGroups;
	public static String wsMigrationHome=null;
	public static String wsMigrationResultPath = null;
	
	public static void beforesuite(){
		
		System.out.println(System.getProperty("user.dir"));
		// Setting up the folder paths
		wsMigrationHome = System.getProperty("user.dir");
		String serviceSetUpfilePath = wsMigrationHome +"\\resources\\config\\serviceSetUp.xlsx";
		
		String sheetName = "setUp";
		String timeStampStr = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_"
				+ new SimpleDateFormat("HHMMSS").format(new Date());
		
		try {
			data_EndPointURL = ExcelUtil.getAllRecords(serviceSetUpfilePath, sheetName);
			createUserDir(timeStampStr);
			OperationKey();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void processSOAPMessage(String data, String fileName ){
		
		createResponseFile(data, fileName);
		
//		responseMsg = out.get("responseMessage");
//		String resPath = lastRunData.get("responsePath");
//		FileWriter resWriter = new FileWriter(resPath);
//		BufferedWriter bufWrite = new BufferedWriter(resWriter);
//		bufWrite.write(responseMsg);
//		bufWrite.flush();
//		bufWrite.close();
	} 
	
	public static void createResponseFile(String data, String fileName)  {
	    FileOutputStream out;
		try {
			out = new FileOutputStream(wsMigrationResultPath+"\\"+fileName);
			out.write(data.getBytes());
		    out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void createUserDir(String dirName)  {
	    final File homeDir = new File( wsMigrationHome + "\\resources\\results");
	    final File dir = new File(homeDir, dirName);
	    if (!dir.exists() && !dir.mkdirs()) {
	        System.out.println("Unable to create folder");
	    }
	    wsMigrationResultPath = dir.getAbsolutePath();
	}

	public static void setTestMethodName(String name) {
		testName = name;
		v5ResponseFileName = "Res_"+name+"_V5.xml";
		cloudResponseFileName ="Res_"+name+"_Cloud.xml"; 
	}
	
	public static String getTestMethodName(){
		return testName;
	}
	public static String getV5ResponseFilePath(){
		return wsMigrationResultPath+"\\"+v5ResponseFileName;
	}
	public static String getCloudResponseFilepath(){
		return wsMigrationResultPath+"\\"+cloudResponseFileName;
	}
	
	private static void setSOAPReq(){
		String dataBankPath = wsMigrationHome+"\\resources\\dataBank\\";
		if (environtment.equalsIgnoreCase("Launchpad") || environtment.contains("Launchpad")) {
			reqPath = dataBankPath + "LaunchpadEnv\\Req";
			
		} else if (environtment.equalsIgnoreCase("Team") || environtment.contains("Team")) {
			reqPath= dataBankPath + "TeamEnv\\Req";
			
		} else if (environtment.equalsIgnoreCase("VAB") || environtment.contains("VAB")){
			reqPath= dataBankPath + "VABEnv\\Req";
			
		} 
		
	}
	public static String getSOAPReq(String varType){
		if (varType.equalsIgnoreCase("cloud")) {
			return reqPath+"\\"+getTestMethodName() + ".xml";
		} else {
			return reqPath+"\\"+getTestMethodName() + ".xml";
		}
	
	}
	
	public static void setTestEnv(String name) {
		environtment = name;
		setSOAPReq();
	}
	public static String getTestEnv(){
		return environtment;
	}
	
	private static void OperationKey(){
		HashMap<String, HashMap<String, String>> tmp = new HashMap<String, HashMap<String, String>>();
		for (HashMap<String, String> hashMap : data_EndPointURL) {
			tmp.put(hashMap.get("OperationKeyword"), hashMap);
		}
		serviceSetUpData = tmp;
	}
	
	public static HashMap<String, String> getServiceSetUpData(String OperationKeyword){
		
		return serviceSetUpData.get(OperationKeyword);
	}
	
	public static String soapV5ReqAsString(String... filePath) throws Exception {
		String soapReq = null;
		String defaultPath=null;
		if (filePath.length == 0) {
			defaultPath = getSOAPReq("v5");
		} else {
			defaultPath = reqPath+ "\\"+ filePath[0]+".xml";
		}
		try {
			soapReq = new String(Files.readAllBytes(Paths.get(defaultPath)));
			soapReq = soapReq.replaceAll("KEYWORD_GMT_TIMESTAMP", getGMTTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return soapReq;
	}
	
	public static String soapCloudReqAsString(String... filePath) throws Exception {
		String soapReq = null;
		String defaultPaht=null;
		if (filePath.length == 0) {
			defaultPaht = getSOAPReq("cloud");
		} else {
			defaultPaht =filePath[0];
		}
		try {
			soapReq = new String(Files.readAllBytes(Paths.get(defaultPaht)));
			soapReq = soapReq.replaceAll("KEYWORD_GMT_TIMESTAMP", getGMTTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return soapReq;
	}
	
	/*
	 * This method is to return the current GMT time
	 */
	public static String getGMTTime() {		
		Instant instant = Instant.now() ;
		String gmtTime = instant.toString();
		return gmtTime;
	}


}
