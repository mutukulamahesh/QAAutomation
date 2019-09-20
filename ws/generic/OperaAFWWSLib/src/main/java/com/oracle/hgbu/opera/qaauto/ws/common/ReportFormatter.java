package com.oracle.hgbu.opera.qaauto.ws.common;

import java.util.ArrayList;
import java.util.List;

import com.relevantcodes.customextentreports.CustomHTMLMethods;
import com.relevantcodes.customextentreports.LogStatus;

public class ReportFormatter {

	/*
	 * This method formats the messages (PASS, FAIL, WARNING etc.,) printed on
	 * the report
	 */
	public static String formatMessage(LogStatus status, String message) {
		if (status == LogStatus.PASS) {
			return "<font color=\"green\">" + message + "</font>";
		} else if (status == LogStatus.FAIL) {
			return "<font color=\"red\">" + message + "</font>";
		} else if (status == LogStatus.WARNING) {
			return "<font color=\"orange\">" + message + "</font>";
		}
		return "<font color=\"black\">" + message + "</font>";
	}

	/*
	 * This method formats the payloads on the report
	 */
	public static String printReqResp(String request, String response, String operation, String statusCode,
			String operationKeyword) {
		return CustomHTMLMethods.printReqResp(request, response, operation, statusCode, operationKeyword);
	}

	/*
	 * This method formats the error log on the report
	 */
	public static List<String> formatErrorLogs(String log) {
		List<String> logs = new ArrayList<String>();
		log = log.replace("<", "&lt");
		log = log.replace(" ", "&nbsp&nbsp");
		String[] logSplit = log.split("\n");
		String subLog = "";
		int first = 0;
		for (int i = 0; i < logSplit.length; i++) {
			if (logSplit[i].length() != 0) {
				if (logSplit[i].charAt(0) == '[') {
					if (i > 0) {
						if (first == 1) {
							logs.add(subLog);
						}
						first = 1;
						subLog = logSplit[i];
						subLog = "<b>" + subLog.substring(0, subLog.indexOf("]") + 1) + " : </b>"
								+ subLog.substring(subLog.indexOf("]") + 1);
					}
					if (i == 0) {
						first = 1;
					}
				} else {
					subLog = subLog + "<br>" + logSplit[i];
				}
			}
		}
		return logs;
	}
}
