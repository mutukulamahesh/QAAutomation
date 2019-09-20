package com.relevantcodes.customextentreports;

import java.util.HashMap;

public class StaticValues {

	public static String xmlModal="";
	public static int xmlModalId=0;
	public static HashMap<String, String> globalEnvMap;
	
	
	public static void addModal(String element){
		xmlModal=xmlModal+element+"\n";
	}
	
	public static String getModalId(){
		xmlModalId++;
		return "xml"+xmlModalId;
	}
	
	public static String rawXmlModal="";
	public static int rawXmlModalId=0;
	public static void addrawXml(String element){
		rawXmlModal=rawXmlModal+element+"\n";
	}
	
	public static String getrawXmlId(){
		rawXmlModalId++;
		return "rawxml"+rawXmlModalId;
	}
	
	
	
	public static int rawErrorId=1;
	public static String rawErrorData="";
	public static void addError(String element){
		rawErrorData=rawErrorData+element+"\n";
	}
	
	public static String getError(){
		rawErrorId++;
		return "errorLogs"+rawErrorId;
	}
	
	
	
	
	public static int errorId=1;
	public static String errorData="";
	public static void addErrorModal(String element){
		errorData=errorData+element+"\n";
	}
	
	public static String getErrorModal(){
		errorId++;
		return "Log"+errorId;
	}
	
	
	
	public static void main(String args[]){
		
	}
}
