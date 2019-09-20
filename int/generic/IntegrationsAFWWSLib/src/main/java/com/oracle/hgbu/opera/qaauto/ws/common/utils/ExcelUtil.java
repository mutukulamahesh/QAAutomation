package com.oracle.hgbu.opera.qaauto.ws.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	public static Sheet getSheet(String excelFilePath, String sheetName) {
		Workbook workBook = null;
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			if (excelFilePath.endsWith("xlsx")) {
				workBook = new XSSFWorkbook(inputStream);
			} else if (excelFilePath.endsWith("xls")) {
				workBook = new HSSFWorkbook(inputStream);
			} else {
				inputStream.close();
				throw new IllegalArgumentException("The specified file " + excelFilePath + " is not Excel file");
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workBook.getSheet(sheetName);
	}

	public static boolean checkIfSheetExists(String excelFilePath, String sheetName) {
		Workbook workBook = null;
		FileInputStream inputStream;
		boolean flag = false;

		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			if (excelFilePath.endsWith("xlsx")) {
				workBook = new XSSFWorkbook(inputStream);
			} else if (excelFilePath.endsWith("xls")) {
				workBook = new HSSFWorkbook(inputStream);
			} else {
				inputStream.close();
				throw new IllegalArgumentException("The specified file " + excelFilePath + " is not Excel file");
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (workBook.getNumberOfSheets() != 0) {
			for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
				if (workBook.getSheetName(i).equals(sheetName)) {
					flag = true;
					break;
				}
			}
		}

		return flag;
	}


	public static int getColumnIndexByHeader(String excelFilePath, String sheetName, String headerValue) {
		Sheet sheet;
		int index = -1;
		try {
			sheet = getSheet(excelFilePath, sheetName);
			Row headerRow = sheet.getRow(0);
			Iterator<Cell> cellIterator = headerRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cell.getStringCellValue().equals(headerValue)) {
					index = cell.getColumnIndex();
					break;
				}
			}
		} catch (Exception e) {
			Logs.logger.error("Error reading " + excelFilePath);
			e.printStackTrace();
		}

		return index;

	}

	// Below method is to retrieve all records from the given sheet
	public static List<HashMap<String, String>> getAllRecords(String excelFilePath, String sheetName) throws Exception {
		List<HashMap<String, String>> listOfRecordsInSheet = new ArrayList<HashMap<String, String>>();
		try {
			Sheet sheet = getSheet(excelFilePath, sheetName);
			Iterator<Row> rowIterator = sheet.iterator();
			Row headerRow = sheet.getRow(0);
			while (rowIterator.hasNext()) {
				Row nextRow = rowIterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// This is to deal with all types of Cell Data.
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						record.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
								cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						record.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
								String.valueOf(cell.getBooleanCellValue()));
						break;
					case Cell.CELL_TYPE_NUMERIC:
						record.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
								String.valueOf(cell.getNumericCellValue()));
						break;
					case Cell.CELL_TYPE_BLANK:
						record.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
								String.valueOf(cell.getNumericCellValue()));
						break;
					}
				}
				listOfRecordsInSheet.add(record);
			}
			// This is to remove the first header->header combination we get
			// when looping the entire records in the sheet.
			listOfRecordsInSheet.remove(0);
		} catch (Exception e) {
			throw new RuntimeException("getAllRecordsInSheet : Exception '" + e.toString() + "' occurred");
		}
		return listOfRecordsInSheet;
	}

	public static String getCellData(String workbook, String worksheet, int rowIndex, int colIndex) throws IOException {
		String cellData = "";
		Sheet sheet = getSheet(workbook, worksheet);
		Row row = sheet.getRow(rowIndex);
		Cell cell = row.getCell(colIndex);
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			cellData = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cellData = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			cellData = String.valueOf(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			cellData = String.valueOf(cell.getNumericCellValue());
			break;
		}
		return cellData;
	}

	public static HashMap<String, String> getRecordInSheetByRowIndex(String excelFilePath, String sheetName,
			Integer index) {
		HashMap<String, String> cellData = new HashMap<String, String>();

		Sheet sheet = getSheet(excelFilePath, sheetName);
		Row headerRow = sheet.getRow(0);
		Row row = sheet.getRow(index);
		if (row == null) {
			throw new NullPointerException("Specified Row Index Does not Exist in the Sheet");
		}
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			// This is to deal with all types of Cell Data.
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				cellData.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(), cell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				cellData.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
						String.valueOf(cell.getBooleanCellValue()));
				break;
			case Cell.CELL_TYPE_NUMERIC:
				cellData.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
						String.valueOf(cell.getNumericCellValue()));
				break;
			case Cell.CELL_TYPE_BLANK:
				cellData.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
						String.valueOf(cell.getNumericCellValue()));
				break;
			}
		}

		return cellData;

	}

	// This method is to retrieve data from the two given columns
	public static HashMap<String, String> getDataFromGivenColumns(String excelFilePath, String worksheetName,
			int col1Index, int col2Index) throws IOException {
		HashMap<String, String> excelData = new HashMap<String, String>();
		Sheet sheet = getSheet(excelFilePath, worksheetName);
		DataFormatter fmt = new DataFormatter();
		Iterator<?> rows = sheet.rowIterator();
		String key = "";
		String value = "";
		XSSFRow row;

		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			String[] var = null;
			//			for (int i = 0; i < row.getLastCellNum(); i++) {
			key = fmt.formatCellValue(row.getCell(col1Index)).trim();
			value = fmt.formatCellValue(row.getCell(col2Index)).trim();
			value = (value.equalsIgnoreCase("FALSE") || value.equalsIgnoreCase("TRUE"))? value.toLowerCase():value;

			if (value.contains(",")) {
				var = value.split(",");
			}
			//			}

			if (!excelData.containsKey(key))
				if(value != ""){
					if (value.contains(",")) {
						for (int i = 0; i < var.length; i++) {
							excelData.put(key + i, var[i]);
						}
					} else {
						excelData.put(key, value);
					}
				}
			key = "";
			value = "";
		}
		Logs.logger.info("Data read from excel " + excelData);
		return excelData;

	}

	public static HashMap<String, String> getColumnDataByKey(String excelFilePath, String worksheetName, String key)
			throws Exception {
		HashMap<String, String> cellData = new HashMap<String, String>();
		int columnKeyIndex = getColumnIndexByHeader(excelFilePath, worksheetName, key);

		Logs.logger.info("Data Set Column Number from data sheet: " + columnKeyIndex + 1);
		cellData = getDataFromGivenColumns(excelFilePath, worksheetName, 0, columnKeyIndex);
		return cellData;
	}

	// Returns the entire row data where the given key is matched with the row
	// header
	public static HashMap<String, String> getRowDataByKey(String excelFilePath, String worksheetName, String key)
			throws Exception {
		HashMap<String, String> cellData = new HashMap<String, String>();
		Sheet sheet = getSheet(excelFilePath, worksheetName);
		Iterator<?> rows = sheet.rowIterator();
		String value = "";
		int rowIndex = 0;
		XSSFRow row;

		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			value = row.getCell(0).getStringCellValue();
			if (value.equalsIgnoreCase(key)) {
				rowIndex = row.getRowNum();
				break;
			}
		}

		cellData = getRecordInSheetByRowIndex(excelFilePath, worksheetName, rowIndex);
		return cellData;
	}

	/**
	 * @author heegupta Description: Method to get the row index of a key
	 *
	 */

	public static int getRowIndex(String excelFilePath, String worksheetName,String key) {
		Sheet sheet = getSheet(excelFilePath, worksheetName);
		Iterator<?> rows = sheet.rowIterator();
		int rowIndex = 0;
		String val = "";
		XSSFRow row;
		while(rows.hasNext()) {
			row = (XSSFRow) rows.next();
			val = row.getCell(0).getStringCellValue();
			if(val.equalsIgnoreCase(key)) {
				rowIndex = row.getRowNum();
				break;
			}
		}
		return rowIndex;

	}

	/**
	 * @author heegupta Description : Method to read comments of a cell
	 */

	public static String getCellComment(String excelFilePath, String worksheetName, int rowIndex, int colIndex) {
		Sheet sheet = getSheet(excelFilePath, worksheetName);
		Comment comment = sheet.getRow(rowIndex).getCell(colIndex).getCellComment();
		String comm = "";
		if(comment == null) {
			return null;
		}
		else {
			comm = comment.getString().toString();
			if(comm.contains("$")) {
				comm = comm.substring(comm.indexOf("$") + 1);
				comm = comm.substring(0, comm.indexOf("$"));
				return comm;
			}
			else
				return null;
		}
	}


	/**
	 * @author shasrira Description: Method to fetch the record which matches
	 *         the key and which is present in the column having the columnIndex
	 */
	public static HashMap<String, String> getRowDataByValueWithColumnIndex(String excelFilePath, String worksheetName,
			String key, int columnIndex) {
		HashMap<String, String> cellData = new HashMap<String, String>();
		Sheet sheet = getSheet(excelFilePath, worksheetName);
		Iterator<?> rows = sheet.rowIterator();
		String value = "";
		int rowIndex = 0;
		XSSFRow row;

		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			value = row.getCell(columnIndex).getStringCellValue();
			if (value.equalsIgnoreCase(key)) {
				rowIndex = row.getRowNum();
				break;
			}
		}

		cellData = getRecordInSheetByRowIndex(excelFilePath, worksheetName, rowIndex);
		return cellData;
	}

	public static void createExcelFile(String path, String workbookName, String worksheet,
			HashMap<String, String> data) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			int length = (worksheet.length() < 25) ? worksheet.length() : 25;
			XSSFSheet results_sheet = workbook.createSheet(worksheet.substring(0, length));

			String[] header = { "Variable_Name", "Created_YN" };
			XSSFRow row = null;
			Cell cell = null;

			Set<?> set = data.entrySet();
			Iterator<?> it = set.iterator();

			int rowid = 0;
			row = results_sheet.createRow(rowid);

			for (int i = 0; i < header.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(header[i]);
			}

			while (it.hasNext()) {
				rowid = rowid + 1;
				@SuppressWarnings("rawtypes")
				Map.Entry me = (Map.Entry) it.next();
				String variableName = (String) me.getKey();
				String created = data.get(variableName);
				row = results_sheet.createRow(rowid);
				cell = row.createCell(0);
				cell.setCellValue(variableName);
				cell = row.createCell(1);
				cell.setCellValue(created);
			}

			Path filePath = Paths.get(path + "\\" + workbookName + ".xlsx");
			if (Files.notExists(filePath)) {
				FileOutputStream out = new FileOutputStream(new File(path + "\\" + workbookName + ".xlsx"));
				workbook.write(out);
				out.close();
				Logs.logger.info("Data source file for " + workbookName + " has been generated");
			} else {
				Logs.logger.info("Data source file for  " + workbookName + " is alreday available");
			}
		} catch (FileAlreadyExistsException e) {
			Logs.logger.info("File is already available");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
