package com.oracle.hgbu.opera.qaauto.ws.custom;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.oracle.hgbu.opera.qaauto.ws.common.ExcelUtil;

/*
 * This is a wrapper class that contains the methods to expose to test classes for interacting with Excel files
 */

public class DSReader {

	public HashMap<String, String> getQuery(String dataSource, String querySetID) throws Exception {
		return ExcelUtil.getRowDataByKey(dataSource, "Queries", querySetID);
	}

	public HashMap<String, String> getServiceMap(String dataSource) throws Exception {
		return ExcelUtil.getDataFromGivenColumns(dataSource, "serviceMap", 0, 1);
	}

	public HashMap<String, String> getTestData(String file, String sheet, String dataSetID) throws Exception {
		return ExcelUtil.getColumnDataByKey(file, sheet, dataSetID);
	}

	public HashMap<String, String> getXPaths(String file, String sheet) throws Exception {
		return ExcelUtil.getDataFromGivenColumns(file, sheet, 0, 1);
	}

	public List<LinkedHashMap<String, String>> getPropertyConfigData(String file, String sheet) throws Exception {
		return ExcelUtil.getAllRecords(file, sheet);
	}

	public void writePropertyResults(String path, String workbookName, String worksheetName,
			HashMap<String, String> data) {
		ExcelUtil.createExcelFile(path, workbookName, worksheetName, data);
	}

	public HashMap<String, String> getConfigResults(String file, String sheet) throws Exception {
		return ExcelUtil.getDataFromGivenColumns(file, sheet, 0, 1);
	}

	public int getRowIndex(String file, String sheet, String key) {
		return ExcelUtil.getRowIndex(file, sheet, key);
	}

	public String getCellComment(String file, String sheet, int rowIndex, int colIndex) {
		return ExcelUtil.getCellComment(file, sheet, rowIndex, colIndex);
	}

	public String getAbsoluteXPath(String file, String sheet, String variable, String messageType) throws Exception {
		HashMap<String, String> xPaths = ExcelUtil.getRowDataByKey(file, sheet, variable);
		String xPath = "";
		if (messageType.equals("Request"))
			xPath = xPaths.get("reqXPath");
		else
			xPath = xPaths.get("resXPath");
		xPaths.clear();
		return xPath;
	}

}
