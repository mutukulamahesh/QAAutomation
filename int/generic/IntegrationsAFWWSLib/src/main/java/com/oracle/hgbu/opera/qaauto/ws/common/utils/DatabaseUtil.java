package com.oracle.hgbu.opera.qaauto.ws.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;

public class DatabaseUtil  {

	public static Connection connection = null;

	public static void setDBConnection(String host, String port, String sid, String uname, String pwd) throws Exception {
		String conString = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
		System.out.println("conString: "+conString);
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(conString, uname, pwd);
		} catch (SQLException e) {
			Logs.logger.error("Connection Failed!");
			e.printStackTrace();
		}
	}

	public static ResultSet executeQuery(String queryString, Connection con) throws Exception {
		String queryHeader=WSLib.getQueryHeaderFromOperationKeyword();
		System.out.println(queryHeader+queryString);
		ResultSet rs = null;
		Statement stmt;
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(queryHeader+queryString);			
		} catch (SQLException e) {
			Logs.logger.error("Connection Failed! Check output console");
			e.printStackTrace();
			//con.close();
		}
		return rs;
	}

	public static void closeDatabase() throws Exception {
		connection.close();
	}

	public synchronized static LinkedHashMap<String, String> getDBRow(String query) throws Exception {

		String queryHeader=WSLib.getQueryHeaderFromOperationKeyword();

		LinkedHashMap<String, String> tmpMap = new LinkedHashMap<String, String>();
		Statement stmt;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		rs = stmt.executeQuery(queryHeader+query);
		rsmd = rs.getMetaData();
		while (rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (rs.getString(i) != null)
					tmpMap.put(rsmd.getColumnName(i), rs.getString(i));
			}
			break;
		}
		rs.close();
		stmt.close();
		return tmpMap;
	}

	public synchronized static ArrayList<LinkedHashMap<String, String>> getDBRows(String query) throws Exception {
		String queryHeader=WSLib.getQueryHeaderFromOperationKeyword();

		ArrayList<LinkedHashMap<String, String>> columnsData = new ArrayList<LinkedHashMap<String, String>>();
		Statement stmt;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		rs = stmt.executeQuery(queryHeader+query);
		rsmd = rs.getMetaData();
		int record = 1;
		while (rs.next()) {
			LinkedHashMap<String, String> tmpMap = new LinkedHashMap<String, String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (rs.getString(i) != null)
					tmpMap.put(rsmd.getColumnName(i), rs.getString(i));
			}
			if (tmpMap.size() > 0)
				columnsData.add(tmpMap);
			record = record + 1;
		}
		rs.close();
		stmt.close();
		return columnsData;
	}

	public  synchronized static LinkedHashMap<String, String>  getDBRowWithNulls(String query) throws Exception{

		String queryHeader=WSLib.getQueryHeaderFromOperationKeyword();

		LinkedHashMap<String, String> tmpMap = new LinkedHashMap<String, String>();
		Statement stmt;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		rs = stmt.executeQuery(queryHeader+query);
		rsmd = rs.getMetaData();
		while (rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				tmpMap.put(rsmd.getColumnName(i), rs.getString(i));
			}
			break;
		}
		rs.close();
		stmt.close();
		return tmpMap;
	}


}
