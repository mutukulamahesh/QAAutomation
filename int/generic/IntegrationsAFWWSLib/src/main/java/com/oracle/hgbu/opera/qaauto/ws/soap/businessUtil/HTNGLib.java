package com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil;

import java.sql.SQLException;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.BusinessLib.Product;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;

public class HTNGLib extends WSLib {
	static BusinessLib businessLib = WSLib.businessLibHTNG;

	public static String getDefaultCode(String interfaceId, String paramGroup, String paramName) throws Exception {
		return businessLib.getDefaultCode(interfaceId, paramGroup, paramName);
	}

	public static String getExtResort(String resort, String interfaceID) throws Exception {
		return businessLib.getExtResort(resort, interfaceID);
	}

	public static String getPmsValue(String resort,  String interfaceID, String conversionCode, String extValue) throws Exception {
		return businessLib.getPmsValue(resort, interfaceID, conversionCode, extValue);
	}

	public static String getExtValue(String resort,  String interfaceID, String conversionCode, String pmsValue) throws Exception {
		return businessLib.getExtValue(resort, interfaceID, conversionCode, pmsValue);
	}

	public static String getRandomExtValue(String resort,  String interfaceID, String conversionCode, String...type) throws Exception {
		return businessLib.getRandomExtValue(resort, interfaceID, conversionCode,type);
	}

	public static String getRandomPMSValue(String resort,  String interfaceID, String conversionCode, String...type) throws Exception {
		return businessLib.getRandomPMSValue(resort, interfaceID, conversionCode,type);
	}

	public static String getExternalDatabase(String resort, String interfaceID) throws Exception {
		return businessLib.getExternalDatabase(resort, interfaceID);
	}

	public static String getHTNGInterface() {
		return businessLib.getHTNGInterface();
	}

	public static String getMasterValue(String resort,  String interfaceID, String conversionCode, String extValue) throws Exception {
		return businessLib.getMasterValue(resort, interfaceID, conversionCode, extValue);
	}

	public static String getInterfaceFromAddress() {
		return businessLib.getInterfaceFromAddress();
	}

	public static void setHTNGHeader(String uname, String pwd, String fromAddress) {
		businessLib.setHTNGHeader(uname, pwd, fromAddress);
	}

	public static String getUDFName(String udfType,String udfLabel,String moduleName) throws SQLException {
		return businessLib.getUDFName(udfType, udfLabel, moduleName);
	}

	public static String getUDFLabel(String udfType,String moduleName) throws SQLException {
		return businessLib.getUDFLabel(udfType, moduleName);
	}


}
