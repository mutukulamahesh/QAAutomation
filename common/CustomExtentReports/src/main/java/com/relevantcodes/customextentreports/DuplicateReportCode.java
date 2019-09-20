package com.relevantcodes.customextentreports;

public class DuplicateReportCode {

	
	public static long startSuite=0;
	
	public static long endSuite=0;
	
	public static long lastOperationDiff=0;
	
	public static long lastOperation=0;
	
	public static long priority=0;
	
	
	public static boolean modals=false;
	
	public static String modalReq;
	
	public static String modalResp;
	
	public static String modalRespCode;
	
	public static String modalOperation;
	public static String modalOperationKey;
	
	public static String logtext="";
	
	public static String assignCategory="";
	
	public static StringBuffer methods=new StringBuffer(); 
	
	public static String mainMethod="public static void main(String args[]){\nrTime=";
	
	
	
	
	public static String mainClass="import com.relevantcodes.extentreports.CustomHTMLMethods;\nimport java.util.Arrays;\nimport com.relevantcodes.extentreports.CustomHTMLMethods;\nimport org.testng.annotations.BeforeSuite;\nimport org.testng.annotations.Test;\nimport com.oracle.hgbu.qa.ws.soap.utils.WSLib;\n"
			+ "import com.relevantcodes.extentreports.LogStatus;\npublic class TimeMachine{\n"
			+ "@BeforeSuite(alwaysRun = true)\npublic void startSuite() throws Exception {\nWSLib.beforeSuite();\n}\n"
			+ "public static long rTimeInc(int time){\nrTime=rTime+time;\nreturn rTime;}\n";
	
	
	
	public static String currMethod="public static void ";
	
	public static long endTest=-1;
	
}
