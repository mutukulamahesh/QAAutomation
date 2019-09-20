package com.oracle.hgbu.opera.qaauto.ws.custom;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import com.oracle.hgbu.opera.qaauto.ws.common.DatabaseUtil;
import com.oracle.hgbu.opera.qaauto.ws.common.Logs;

public class DataGen {
	static DatabaseUtil dbObj = new DatabaseUtil();
	ConfigReader configReader = null;
	String businessDate = "", businessDateTFromat = "";
	static String db = "abcdefghijklmnopqrstuvwxyz";
	static String dbCap = "ABCDEFGHIJKLMKNOPRSTUVWXYZ";
	static String dbNum = "123456789";
	static String schema = "";
	static String resortEntry = "";
	Connection con = null;

	public DataGen(Connection con, ConfigReader configReader) throws Exception {
		this.configReader = configReader;
		this.con = con;
	}

	public DataGen() {
	}

	public void setResortEntry(String resortEntryValue) {
		resortEntry = resortEntryValue;
	}

	/*
	 * This method generates data dynamically based on the keyword;
	 */
	public String getDynamicData(String keyword) throws Exception {
		String dataStr = "";
		int numToAdd = 0, numToMinus = 0;
		String dt = getBusinessDate();

		String simpleFormat = "yyyy-MM-dd";
		String timeZoneFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";

		SimpleDateFormat sdf = new SimpleDateFormat(simpleFormat);
		SimpleDateFormat timeZoneDateFormat = new SimpleDateFormat(timeZoneFormat);

		Calendar c = Calendar.getInstance();
		GregorianCalendar cal = new GregorianCalendar();

		c.setTime(sdf.parse(dt));
		cal.setTime(sdf.parse(dt));

		businessDate = sdf.format(c.getTime());
		businessDateTFromat = timeZoneDateFormat.format(cal.getTime());

		if (keyword.startsWith("{KEYWORD_BUSINESSDATE_ADD")) {
			numToAdd = Integer.parseInt(keyword.substring(keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_ADD}";
		} else if (keyword.startsWith("{KEYWORD_BUSINESSDATE_MINUS")) {
			numToMinus = Integer.parseInt(keyword.substring(keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_MINUS}";
		} else if (keyword.startsWith("{KEYWORD_BUSINESSDATE_TFORMAT_ADD")) {
			numToAdd = Integer.parseInt(keyword.substring(keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_TFORMAT_ADD}";
		} else if (keyword.startsWith("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS")) {
			numToMinus = Integer.parseInt(keyword.substring(keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_BUSINESSDATE_TFORMAT_MINUS}";
		} else if (keyword.startsWith("{KEYWORD_RANDSTR")) {
			numToAdd = Integer.parseInt(keyword.substring(keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_RANDSTR}";
		} else if (keyword.startsWith("{KEYWORD_RANDNUM")) {
			numToAdd = Integer.parseInt(keyword.substring(keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_RANDNUM}";
		} else if (keyword.startsWith("{KEYWORD_RANDALPHANUM")) {
			numToAdd = Integer.parseInt(keyword.substring(keyword.lastIndexOf("_") + 1, keyword.indexOf("}")));
			keyword = "{KEYWORD_RANDALPHANUM}";
		}

		switch (keyword) {

		// This keyword generates a random string with the given length; Example
		// {KEYWORD_RANDSTR_5} KEYWORD generates the following >> RTYUI
		case "{KEYWORD_RANDSTR}":
			dataStr = getString(numToAdd, false);
			break;
			// This keyword generates a random number with the given length; Example
			// {KEYWORD_RANDNUM_10} KEYWORD generates the following >> 8154345671
		case "{KEYWORD_RANDNUM}":
			dataStr = getNumber(numToAdd);
			break;
			// This keyword generates a random alpha-numeric with the given length;
			// Example {KEYWORD_RANDALPHANUM_8} KEYWORD generates the following >>
			// 8A789A99
		case "{KEYWORD_RANDALPHANUM}":
			dataStr = getString(numToAdd, true);
			break;
			// This keyword generates a random first name (It picks up a random
			// first name from the available excel sheet)
		case "{KEYWORD_FNAME}":
			dataStr = getName("FIRST");
			break;
			// This keyword generates a random last name (It picks up a random first
			// name from the available excel sheet)
		case "{KEYWORD_LNAME}":
			dataStr = getName("LAST");
			break;
			// This keyword returns the business date of the property against which
			// the tests are to be executed
		case "{KEYWORD_BUSINESSDATE}":
			dataStr = businessDate;
			break;
			// This keyword returns the date that is generated after adding the
			// given number of days to the business date of the property against
			// which the tests are to be executed. Ex: {KEYWORD_BUSINESSDATE_ADD_1}
		case "{KEYWORD_BUSINESSDATE_ADD}":
			dataStr = addDaysToDate(businessDate, numToAdd, simpleFormat);
			break;
			// This keyword returns the date that is generated after subtracting the
			// given number of days from the business date of the property against
			// which the tests are to be executed. Ex:
			// {KEYWORD_BUSINESSDATE_MINUS_1}
		case "{KEYWORD_BUSINESSDATE_MINUS}":
			dataStr = addDaysToDate(businessDate, -numToMinus, simpleFormat);
			break;
			// This keyword returns the business date in TFORMAT
		case "{KEYWORD_BUSINESSDATE_TFORMAT}":
			dataStr = businessDateTFromat;
			break;
			// This keyword returns the business date in TFORMAT after being added
			// with the given number of days
		case "{KEYWORD_BUSINESSDATE_TFORMAT_ADD}":
			dataStr = addDaysToDate(businessDateTFromat, numToAdd, timeZoneFormat);
			break;
			// This keyword returns the business date in TFORMAT after being
			// subtracted with the given number of days
		case "{KEYWORD_BUSINESSDATE_TFORMAT_MINUS}":
			dataStr = addDaysToDate(businessDateTFromat, -numToMinus, timeZoneFormat);
			break;
			// This keyword returns a space. This is used when the element has to be
			// present on the request payload without data because the
			// implementation is in such a way that the element wouldn't be taken
			// into
			// consideration while building the request XML if the data bank has no
			// value for an element
		case "{KEYWORD_SPACE}":
			dataStr = " ";
			break;

		}
		return dataStr;
	}

	/*
	 * This method adds days to the given date
	 */
	private String addDaysToDate(String dt, int days, String timeFormat) throws ParseException {
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

	/*
	 * This method generates the string/alpha-numeric string with the given
	 * length.
	 */
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
			randomStr = randomStr.substring(0, 1).toUpperCase() + randomStr.substring(1);
		return randomStr;
	}

	/*
	 * This method generates the number with the given length.
	 */
	public String getNumber(int length) {
		return String.valueOf(length < 1 ? 0
				: new Random().nextInt((9 * (int) Math.pow(10, length - 1)) - 1) + (int) Math.pow(10, length - 1));
	}

	/*
	 * This method retrieves a random name from the excel workbook where in
	 * collection of first/last names are captured. NameType indicates
	 * FIRST/LAST
	 */
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

	/*
	 * This method generates the number between the given range
	 */
	public int getNumberWithinGivenRange(int upperBound, int lowerBound) {
		Random random = new Random();
		int randomNumber = random.nextInt(upperBound - lowerBound) + lowerBound;
		return randomNumber;
	}

	/*
	 * This method generates a random past/future date based on the given
	 * dateType. DateType should be P or F, where P is to get random past date
	 * and F is to get the future date
	 */
	public String getDate(String dateType) {
		String date = "";
		if (dateType != "") {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int yearBegin = 1990;
			int yearEnd = (year - yearBegin);
			if (dateType.equalsIgnoreCase("F")) {
				date = "" + (year + 1) + "-" + (1 + (int) (Math.random() * 12)) + "-"
						+ (1 + 1 + (int) (Math.random() * 31));
			} else if (dateType.equalsIgnoreCase("P")) {
				date = "" + (1 + (int) (Math.random() * 31) + "/"
						+ (1 + (int) (Math.random() * 12) + "/" + (yearBegin + (int) (Math.random() * yearEnd))));
				date = "" + (yearBegin + (int) (Math.random() * yearEnd)) + "-" + (1 + (int) (Math.random() * 12)) + "-"
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
		businessDateQuery = "SELECT MAX(BUSINESS_DATE) AS BUSINESS_DATE FROM BUSINESSDATE WHERE RESORT='"
				+ configReader.getResort(resortEntry) + "'";
		//System.out.println("businessDateQuery: " + businessDateQuery);
		Logs.logger.info(businessDateQuery);
		businessDate = DatabaseUtil.getDBRow(businessDateQuery).get("BUSINESS_DATE");
		return businessDate;
	}

	/*
	 * This method is to close the open database connections
	 */
	public void flushConnections() throws SQLException {
		if (!con.isClosed())
			con.close();
	}

	/*
	 * This method is to return the current GMT time
	 */
	public String getGMTTime() {
		/*Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 19800000);
		Date date = new Date(timestamp.getTime());
		String gmtTime= new SimpleDateFormat("yyyy-MM-dd").format(date) + "T"
				+ new SimpleDateFormat("HH:mm:ss").format(date) + "."
				+ timestamp.toString().substring(timestamp.toString().length() - 3, timestamp.toString().length())
				+ "Z";*/
		Instant instant = Instant.now() ;
		String gmtTime = instant.toString();
		return gmtTime;
	}

	public static void main(String[] args) throws Exception {
		DataGen d = new DataGen();
		System.out.println(d.getGMTTime());
	}

}
