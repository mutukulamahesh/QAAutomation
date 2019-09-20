package com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil;

import java.sql.SQLException;
import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.BusinessLib.Product;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;

public class OPERALib extends WSLib {
	
	static BusinessLib businessLib = WSLib.businessLibOPERA;
	public static String getResort() {
		return businessLib.getResort();
	}

	public static String getChain() {
		return businessLib.getChain();
	}

	public static String getUserName() {
		return businessLib.getUserName();
	}

	public static String getPassword() {
		return businessLib.getPassword();
	}

	public static HashMap<String, String> fetchAddressLOV(String stateCode,String country) throws Exception {
		return businessLib.fetchAddressLOV(stateCode, country);
	}

	public static HashMap<String, String> fetchAddressLOV() throws Exception {
		return businessLib.fetchAddressLOV();
	}

	public static void setOperaHeader(String uname) {
		businessLib.setOperaHeader(uname);
	}


	public static String getUDFName(String udfType,String udfLabel,String moduleName) throws SQLException {
		return businessLib.getUDFName(udfType, udfLabel, moduleName);
	}

	public static String getUDFLabel(String udfType,String moduleName) throws SQLException {
		return businessLib.getUDFLabel(udfType, moduleName);
	}
}
