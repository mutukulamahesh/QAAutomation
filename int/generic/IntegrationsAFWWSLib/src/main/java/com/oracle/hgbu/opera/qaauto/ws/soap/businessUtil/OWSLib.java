package com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil;
import java.sql.SQLException;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.BusinessLib.Product;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;

public class OWSLib extends WSLib {
	static BusinessLib businessLib = WSLib.businessLibOWS;

	public static String getChannelCarier(String resort, String channel) throws SQLException {
		return businessLib.getChannelCarier(resort, channel);
	}
	
	public static String getGCRChainCode(String resort, String channel) throws SQLException {
		return businessLib.getGCRChainCode(resort, channel);
	}

	public static String getChannelResort(String resort, String channel) throws SQLException {
		return businessLib.getChannelResort(resort, channel);
	}

	public static String getChannelBeginDate(String resort, String channel) throws SQLException {
		return businessLib.getChannelBeginDate(resort, channel);
	}

	public static String getChannelEndDate(String resort, String channel) throws SQLException {
		return businessLib.getChannelEndDate(resort, channel);
	}

	public static String getChannelType(String channel) throws SQLException {
		return businessLib.getChannelType(channel);
	}

	public static String getChannel() {
		return businessLib.getChannel();
	}

	public static String getBusinessDate(String resort) throws Exception {
		return businessLib.getBusinessDate(resort);
	}

	public static void setOWSHeader(String uname, String pwd, String resort, String channelType, String channelCarrier) {
		businessLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}

	public static String getUDFMapping(String udfName,String moduleName) throws SQLException {
		return businessLib.getUDFMapping(udfName,moduleName);
	}
}
