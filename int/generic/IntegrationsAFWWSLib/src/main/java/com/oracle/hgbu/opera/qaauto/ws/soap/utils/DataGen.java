package com.oracle.hgbu.opera.qaauto.ws.soap.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import com.oracle.hgbu.opera.qaauto.ws.common.utils.DatabaseUtil;

public class DataGen {
	static DatabaseUtil dbObj = new DatabaseUtil();
	ConfigReader configReader = null;
	String businessDate = "";
	String businessDateTFromat = "";
	String systemDate = "";
	String systemDateTFromat = "";
	static String db = "abcdefghijklmnopqrstuvwxyz";
	static String dbCap = "ABCDEFGHIJKLMKNOPRSTUVWXYZ";
	static String dbNum = "123456789";
	Connection con = null;
	static String schema="";

	public DataGen(Connection con, ConfigReader configReader) throws Exception {
		this.configReader = configReader;
		this.con = con;
	}

	public DataGen() {
	}

	public String getDynamicData(String keyword) throws Exception {
		String dataStr = "";
		int numToAdd = 0;
		int numToMinus = 0;
		
		String simpleFormat = "yyyy-MM-dd";
		String timeZoneFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";
		SimpleDateFormat sdf = new SimpleDateFormat(simpleFormat);
		SimpleDateFormat timeZoneDateFormat = new SimpleDateFormat(
				timeZoneFormat);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		
		String dt = getBusinessDate();	
		businessDate = dt;
		String sdt = getSystemDate();
		systemDate = sdt;

		cal.setTime(sdf.parse(dt));
		businessDateTFromat = timeZoneDateFormat.format(cal.getTime());
		cal.setTime(sdf.parse(sdt));
		systemDateTFromat = timeZoneDateFormat.format(cal.getTime());


		if (keyword.startsWith("{KEYWORD_BUSINESSDATE_ADD")) {
			numToAdd = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_ADD}";
		} else if (keyword.startsWith("{KEYWORD_BUSINESSDATE_MINUS")) {
			numToMinus = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_MINUS}";
		} else if (keyword.startsWith("{KEYWORD_BUSINESSDATE_TFORMAT_ADD")) {
			numToAdd = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_TFORMAT_ADD}";
		} else if (keyword.startsWith("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS")) {
			numToMinus = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_TFORMAT_MINUS}";
		} else if (keyword.startsWith("{KEYWORD_RANDSTR")) {
			numToAdd = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_RANDSTR}";
		} else if (keyword.startsWith("{KEYWORD_RANDNUM")) {
			numToAdd = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_RANDNUM}";
		}
		else if (keyword.startsWith("{KEYWORD_RANDALPHANUM")) {
			numToAdd = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_RANDALPHANUM}";
		}
		else if (keyword.startsWith("{KEYWORD_SYSTEMDATE_TFORMAT_ADD")) {
			numToAdd = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_SYSTEMDATE_TFORMAT_ADD}";
		}
		else if (keyword.startsWith("{KEYWORD_SYSTEMDATE_ADD")) {
			numToAdd = Integer.parseInt(keyword.substring(
					keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_SYSTEMDATE_ADD}";
		} 
		
		switch (keyword) {
		case "{KEYWORD_ROOM_NUMBER}":
			dataStr = "A" + getNumber(5);
			break;
		case "{KEYWORD_ID}":
			dataStr = getNumber(8);
			break;
		case "{KEYWORD_PHONE}":
			dataStr = getNumber(10);
			break;
		case "{KEYWORD_STR}":
			dataStr = getString(6, false);
			break;
		case "{KEYWORD_RANDSTR}":                          // Ex: {KEYWORD_RANDSTR_5}           Random String
			dataStr = getString(numToAdd, false);
			break;
		case "{KEYWORD_RANDNUM}":                          // Ex: {KEYWORD_RANDNUM_5}           Random Number
			dataStr = getNumber(numToAdd);
			break;
		case "{KEYWORD_RANDALPHANUM}":                     // Ex: {KEYWORD_RANDALPHANUM_5}      Random Alpha Numeric
			dataStr = getString(numToAdd, true);
			break;
		case "{KEYWORD_ANSTR}":
			dataStr = getString(10, true);
			break;
		case "{KEYWORD_FNAME}":
			dataStr = getName("FIRST");
			break;
		case "{KEYWORD_LNAME}":
			dataStr = getName("LAST");
			break;
		case "{KEYWORD_BUSINESSDATE}":
			dataStr = businessDate.substring(0,10);
			break;
		case "{KEYWORD_SYSTEMDATE}":
			dataStr = systemDate;
			break;
		case "{KEYWORD_BUSINESSDATE_ADD}":
			dataStr = addDaysToDate(businessDate, numToAdd, simpleFormat);
			break;
		case "{KEYWORD_SYSTEMDATE_ADD}":
			dataStr = addDaysToDate(systemDate, numToAdd, simpleFormat);
			break;
		case "{KEYWORD_BUSINESSDATE_MINUS}":
			dataStr = addDaysToDate(businessDate, -numToMinus, simpleFormat);
			break;
		case "{KEYWORD_SYSTEMDATE_MINUS}":
			dataStr = addDaysToDate(systemDate, -numToMinus, simpleFormat);
			break;
		case "{KEYWORD_BUSINESSDATE_TFORMAT}":
			dataStr = businessDateTFromat;
			break;
		case "{KEYWORD_BUSINESSDATE_TFORMAT_ADD}":
			dataStr = addDaysToDate(businessDateTFromat, numToAdd,
					timeZoneFormat);
			break;
		case "{KEYWORD_BUSINESSDATE_TFORMAT_MINUS}":
			dataStr = addDaysToDate(businessDateTFromat, -numToMinus,
					timeZoneFormat);
			break;
		case "{KEYWORD_SPACE}":
			dataStr = " ";
			break;

		case "{KEYWORD_CURRENCY_CODE}":
			dataStr = "USD" + GetRandomNumber(4);
			break;

		case "{KEYWORD_GMT_TIMESTAMP}":
			dataStr = getGMTTime();
			break;
		case "{KEYWORD_SYSTEMDATE_TFORMAT_ADD}":
			dataStr = addDaysToDate(systemDateTFromat, numToAdd,
					timeZoneFormat);
			break;
		case "{KEYWORD_SYSTEMDATE_TFORMAT}":
			dataStr = systemDateTFromat;
			break;
		}
		return dataStr;
	}

	private String addDaysToDate(String dt, int days, String timeFormat)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		Calendar c;
		GregorianCalendar cal;

		if (timeFormat.equals("yyyy-MM-dd")) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(dt));
			c.add(Calendar.DATE, days); // number of days to modify
			dt = sdf.format(c.getTime());
		} else if (timeFormat.equals("yyyy-MM-dd'T'HH:mm:ssXXX")) {
			cal = new GregorianCalendar();
			cal.setTime(sdf.parse(dt));
			cal.add(GregorianCalendar.HOUR, days); // number of days to modify
			dt = sdf.format(cal.getTime());
		}
		return dt;
	}
	
	
	private String getSystemDate() {
		String timeZoneFormat = "yyyy-MM-dd";
		SimpleDateFormat timeZoneDateFormat = new SimpleDateFormat(
				timeZoneFormat);
		GregorianCalendar cal = new GregorianCalendar();
		return timeZoneDateFormat.format(cal.getTime());	
	}

	private String getSystemDateTFormat() {
		String timeZoneFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";
		SimpleDateFormat timeZoneDateFormat = new SimpleDateFormat(
				timeZoneFormat);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		return timeZoneDateFormat.format(cal.getTime());	
	}


	private static String getString(int length, boolean alphaNumeric) {
		String chars = "";
		String randomStr = "";
		Random rand = new Random();
		StringBuilder buf = new StringBuilder();

		if (alphaNumeric)
			chars = "abcdefghijklmnopqrstuvwxyz0123456789";
		else
			chars = "abcdefghijklmnopqrstuvwxyz";

		for (int i = 0; i < length; i++) {
			buf.append(chars.charAt(rand.nextInt(chars.length())));
		}
		randomStr = buf.toString();
		if (alphaNumeric)
			randomStr = randomStr.toUpperCase();
		else
			randomStr = randomStr.substring(0, 1).toUpperCase()
			+ randomStr.substring(1);
		return randomStr;
	}

	public String getNumber(int length) {
		return String.valueOf(length < 1 ? 0 : new Random()
				.nextInt((9 * (int) Math.pow(10, length - 1)) - 1)
				+ (int) Math.pow(10, length - 1));
	}

	public String getName(String nameType) throws Exception {
		HashMap<String, String> names = new HashMap<String, String>();
		int randomNumber = getNumberWithinGivenRange(910, 2);
		names = configReader.getRandomName(randomNumber);
		String name = "";
		if (nameType.equalsIgnoreCase("FIRST"))
			name = names.get("FIRSTNAME");
		else if (nameType.equalsIgnoreCase("LAST"))
			name = names.get("LASTNAME");
		return name;
	}

	public int getNumberWithinGivenRange(int upperBound, int lowerBound) {
		Random random = new Random();
		int randomNumber = random.nextInt(upperBound - lowerBound) + lowerBound;
		return randomNumber;
	}

	public String getDate(String dateType, String format) {
		String date = "";
		if (dateType != "" && format == null) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int yearBegin = 1990;
			int yearEnd = (year - yearBegin);
			if (dateType.equalsIgnoreCase("F")) {
				date = "" + (year + 1) + "-" + (1 + (int) (Math.random() * 12))
						+ "-" + (1 + 1 + (int) (Math.random() * 31));
			} else if (dateType.equalsIgnoreCase("P")) {
				date = ""
						+ (1 + (int) (Math.random() * 31) + "/" + (1
								+ (int) (Math.random() * 12) + "/" + (yearBegin + (int) (Math
										.random() * yearEnd))));
				date = "" + (yearBegin + (int) (Math.random() * yearEnd)) + "-"
						+ (1 + (int) (Math.random() * 12)) + "-"
						+ (1 + 1 + (int) (Math.random() * 31));
			}
			String[] tmpStr = date.split("-");
			if (tmpStr[1].length() < 2)
				tmpStr[1] = "0" + tmpStr[1];
			if (tmpStr[2].length() < 2)
				tmpStr[2] = "0" + tmpStr[2];
			date = tmpStr[0] + "-" + tmpStr[1] + "-" + tmpStr[2];
		}
		return date;
	}

	/*
	 * This method generates a random past/future date based on the given
	 * dateType. DateType should be P or F, where P is to get random past date
	 * and F is to get the future date
	 */
	public String getBusinessDate() throws Exception {
		String businessDate = "";
		String businessDateQuery;
		businessDateQuery = "SELECT TRUNC(MAX(BUSINESS_DATE)) AS BUSINESS_DATE FROM BUSINESSDATE WHERE RESORT='"
				+ configReader.getResort() + "'";
		//System.out.println("businessDateQuery: " + businessDateQuery);
		businessDate = DatabaseUtil.getDBRow(businessDateQuery).get("BUSINESS_DATE");
		return businessDate;
	}

	public void flushConnections() throws SQLException {
		if (!con.isClosed())
			con.close();
	}

	public static String GetRandomCapName(int length) {
		String name = "";
		Random r = new Random();
		int charIndex = 0;
		for (int i = 0; i < length; i++) {
			charIndex = r.nextInt(dbCap.length() - 1);
			name += dbCap.charAt(charIndex);
		}
		return name;
	}

	public static String GetRandomNumber(int length) {

		String no = "";
		Random r = new Random();
		int charIndex = 0;
		for (int i = 0; i < length; i++) {
			charIndex = r.nextInt(dbNum.length() - 1);
			no += dbNum.charAt(charIndex);
		}

		return no;
	}

	/*
	 * This method is to return the current GMT time
	 */
	public String getGMTTime() {		
		Instant instant = Instant.now() ;
		String gmtTime = instant.toString();
		return gmtTime;
	}

	public static void main(String[] args) throws Exception {
		DataGen d = new DataGen();
		System.out.println(d.getGMTTime());
	}

}
