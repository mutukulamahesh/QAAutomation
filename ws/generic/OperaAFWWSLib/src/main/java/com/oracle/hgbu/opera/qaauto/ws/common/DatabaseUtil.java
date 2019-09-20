package com.oracle.hgbu.opera.qaauto.ws.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.oracle.hgbu.opera.qaauto.ws.custom.WSLib;

public class DatabaseUtil {

	public static Connection connection = null;

	/*
	 * This method establishes the DB Connection using the given connection string parameters
	 */
	public static void setDBConnection(String host, String port, String sid, String uname, String pwd) throws Exception {
		String conString = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
		Logs.logger.info("Connection String: "+conString +" , "+"User/Password: "+uname+"/"+pwd);

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(conString, uname, pwd);
		}
		catch (SQLException e) {
			System.out.println("Connection Failed! "+conString);
			Logs.logger.info("DB Connection failed!! **>> Method: setDBConnection");
			e.printStackTrace();
		}
	}

	/*
	 * This method executes the given SQL Query and returns the result set
	 */
	public static ResultSet executeQuery(String queryString, Connection con) throws Exception {
		String queryHeader = WSLib.getQueryHeaderFromOperationKeyword();
		ResultSet rs = null;
		Statement stmt;
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(queryHeader + queryString);
		}
		catch (SQLException e) {
			System.out.println("Error occurred while executing the query "+queryString + " **>> Method: executeQuery");
			Logs.logger.error("Error occurred while executing the query "+queryString + " **>> Method: executeQuery");
			e.printStackTrace();
		}
		return rs;
	}

	/*
	 * This method closes the Database connection
	 */
	public static void closeDatabase() throws Exception {
		connection.close();
		try {
			connection.close();
			Logs.logger.error("Connection Closed");
		}
		catch(Exception e) {
			System.out.println("Error occurred while attempting to close the connection. **>> Method: closeDatabase");
			Logs.logger.error("Error occurred while attempting to close the connection **>> Method: closeDatabase");
		}
	}

	/*
	 * This method returns the record that is resulted upon executing the given SQL
	 */
	public synchronized static LinkedHashMap<String, String> getDBRow(String query) throws Exception {
		LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			String queryHeader = WSLib.getQueryHeaderFromOperationKeyword();
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(queryHeader + query);
			rsmd = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (rs.getString(i) != null)
						dbResult.put(rsmd.getColumnName(i), rs.getString(i));
					else
						dbResult.put(rsmd.getColumnName(i), "");
				}
				break;
			}
			Logs.logger.error("*******************************************************************************");
			Logs.logger.error("Query: "+query);
			Logs.logger.error("Resulted records from DB: "+dbResult);
			Logs.logger.error("*******************************************************************************");
		}
		catch(Exception e) {
			Logs.logger.error("Error while retrieving the data from DB. **>> Method: getDBRow");
			System.out.println("Error while retrieving the data from DB. **>> Method: getDBRow");
		}
		finally {
			rs.close();
			stmt.close();
		}
		return dbResult;
	}

	/*
	 * This method returns multiple records that are resulted upon executing the given SQL
	 */
	public synchronized static ArrayList<LinkedHashMap<String, String>> getDBRows(String query) throws Exception {
		ArrayList<LinkedHashMap<String, String>> dbResult = new ArrayList<LinkedHashMap<String, String>>();
		Statement stmt=null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String queryHeader = WSLib.getQueryHeaderFromOperationKeyword();
		try {
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(queryHeader + query);
			rsmd = rs.getMetaData();
			int record = 1;
			while (rs.next()) {
				LinkedHashMap<String, String> tmpMap = new LinkedHashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (rs.getString(i) != null)
						tmpMap.put(rsmd.getColumnName(i), rs.getString(i));
					else
						tmpMap.put(rsmd.getColumnName(i), "");
				}
				if (tmpMap.size() > 0)
					dbResult.add(tmpMap);
				record = record + 1;
			}
			Logs.logger.error("*******************************************************************************");
			Logs.logger.error("Query: "+query);
			Logs.logger.error("Resulted records from DB: "+dbResult);
			Logs.logger.error("*******************************************************************************");
		}
		catch(Exception e) {
			Logs.logger.error("Error while retrieving the data from DB. **>> Method: getDBRows");
			System.out.println("Error while retrieving the data from DB. **>> Method: getDBRows");
		}
		finally {
			rs.close();
			stmt.close();
		}

		return dbResult;
	}
}
