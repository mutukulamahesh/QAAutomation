package com.relevantcodes.customextentreports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OperationTime {

	public static HashMap<String, String> soapProjects = new HashMap<String, String>();
	public static HashMap<String, String> soapServices = new HashMap<String, String>();
	public static HashMap<String, String> soapOperations = new HashMap<String, String>();

	private static HashMap<String, List<Long>> operations = new HashMap<String, List<Long>>();

	private static HashMap<String, List<Long>> operationStart = new HashMap<String, List<Long>>();

	private static HashMap<String, List<String>> operationReq = new HashMap<String, List<String>>();

	private static HashMap<String, List<String>> operationTestCaseName = new HashMap<String, List<String>>();

	private static HashMap<String, List<String>> operationResp = new HashMap<String, List<String>>();

	private static HashMap<String, List<Integer>> operationRespCode = new HashMap<String, List<Integer>>();

	public static void insertReqResp(String operationKeyword, String req1, String resp1) {

		List<String> req = new ArrayList<String>();

		List<String> reqs = new ArrayList<String>();

		if (operationReq.containsKey(operationKeyword)) {

			req = operationReq.get(operationKeyword);
		}

		for (int i = 0; i < req.size() - 1; i++) {
			reqs.add(req.get(i));
		}
		reqs.add(req1);

		operationReq.put(operationKeyword, reqs);

		List<String> resp = new ArrayList<String>();

		List<String> resps = new ArrayList<String>();

		if (operationResp.containsKey(operationKeyword)) {

			resp = operationResp.get(operationKeyword);
		}

		for (int i = 0; i < resp.size() - 1; i++) {
			resps.add(resp.get(i));
		}
		resps.add(resp1);

		operationResp.put(operationKeyword, resps);

	}

	public static void insertTime(String operationKeyword, long startTime, long time, int responseCode) {

		String testcase = "";

		DuplicateReportCode.currMethod = DuplicateReportCode.currMethod + "OperationTime.insertTime(\""
				+ operationKeyword + "\"," + startTime + "L," + time + "L," + responseCode + ");\n";

		List<Long> times = new ArrayList<Long>();

		if (operations.containsKey(operationKeyword)) {

			times = operations.get(operationKeyword);
		}

		times.add(time);
		operations.put(operationKeyword, times);

		List<Long> starttimes = new ArrayList<Long>();

		if (operationStart.containsKey(operationKeyword)) {

			starttimes = operationStart.get(operationKeyword);
		}

		starttimes.add(startTime);

		operationStart.put(operationKeyword, starttimes);

		List<String> req = new ArrayList<String>();

		if (operationReq.containsKey(operationKeyword)) {

			req = operationReq.get(operationKeyword);
		}

		req.add(String.valueOf(-1));

		operationReq.put(operationKeyword, req);

		List<String> resp = new ArrayList<String>();

		if (operationResp.containsKey(operationKeyword)) {

			resp = operationResp.get(operationKeyword);
		}

		resp.add(String.valueOf(-1));

		operationResp.put(operationKeyword, resp);

		List<Integer> respc = new ArrayList<Integer>();

		if (operationRespCode.containsKey(operationKeyword)) {

			respc = operationRespCode.get(operationKeyword);
		}

		respc.add(responseCode);

		operationRespCode.put(operationKeyword, respc);

		List<String> testcaseName = new ArrayList<String>();

		if (operationTestCaseName.containsKey(operationKeyword)) {

			testcaseName = operationTestCaseName.get(operationKeyword);
		}

		testcaseName.add(testcase);

		operationTestCaseName.put(operationKeyword, testcaseName);

	}

	private static long calculateAverage(List<Long> marks) {
		Long sum = 0L;
		if (!marks.isEmpty()) {
			for (Long mark : marks) {
				sum += mark;
			}
			return sum / marks.size();
		}
		return sum;
	}

	public static HashMap<String, String> generateOperationTime() {

		HashMap<String, String> returnStr = new HashMap<String, String>();

		String htmlTime = "";

		long max, min;
		double avrg;
		int count;

		int id = 1;

		String timeModal = "";

		for (HashMap.Entry<String, List<Long>> entry : operations.entrySet()) {

			String operationKeyword = entry.getKey();

			List<Long> times = new ArrayList<Long>();

			times = entry.getValue();

			List<Long> startTimes = new ArrayList<Long>();
			startTimes = operationStart.get(operationKeyword);

			List<Integer> respCode = new ArrayList<Integer>();
			respCode = operationRespCode.get(operationKeyword);

			List<String> reqs1 = new ArrayList<String>();
			reqs1 = operationReq.get(operationKeyword);

			List<String> resps1 = new ArrayList<String>();
			resps1 = operationResp.get(operationKeyword);

			List<String> TestCase1 = new ArrayList<String>();
			TestCase1 = operationTestCaseName.get(operationKeyword);

			String tabDetails = "";

			tabDetails = tabDetails
					+ "<table class=\"striped\"><thead><tr><th><br>Sl No.</br></th><th><br>Start Time</br></th><th><br>End Time</br></th>"
					+ "<th><br>Time Taken(S)</br></th><th><br>Response Code</br></th>"
					+ "<th><br>Request</br></th><th><br>Response</br></th></tr></thead><tbody>";

			count = times.size();
			if (count == 1) {
				max = times.get(0);
				min = times.get(0);
				avrg = times.get(0);
			} else {
				max = Collections.max(times);
				min = Collections.min(times);
				avrg = calculateAverage(times);
			}

			for (int i = 0; i < times.size(); i++) {

				DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");

				tabDetails = tabDetails + "<tr><td>" + (i + 1) + "</td><td>" + df.format(new Date(startTimes.get(i)))
				+ "</td><td>" + df.format(new Date((startTimes.get(i) + times.get(i)))) + "</td><td>"
				+ (float) times.get(i) / 1000 + "</td><td>" + respCode.get(i) + "</td>";

				if (!reqs1.get(i).equals("-1")) {
					tabDetails = tabDetails + "<td> <a href=\"#" + reqs1.get(i) + "\" onclick=\"openDialog('"
							+ reqs1.get(i) + "')\">" + "Request" + "</a></td>";
				}

				if (!resps1.get(i).equals("-1")) {
					tabDetails = tabDetails + "<td> <a href=\"#" + resps1.get(i) + "\" onclick=\"openDialog('"
							+ resps1.get(i) + "')\">" + "Response" + "</a></td>";

				}

				tabDetails = tabDetails + "</tr>";

			}

			tabDetails = tabDetails + "</tbody></table>";

			timeModal = timeModal + "<div id=\"time" + id
					+ "\" class=\"modal\" style=\" width: 90%;height: 80%;top:5% !important;max-height:90%\">"
					+ "<div style=\"postion:relative;\"><h5 style=\"position:absolute;left:13px;\">"
					+ soapOperations.get(operationKeyword)
					+ " </h5><a href=\"#!\"><img src=\"./extentreports/images/close.png\"   class=\"modal-action modal-close tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Close\" style=\"width:15px;height:15px;right:20px;position:absolute;z-index: 100001;\" alt=\"Close\"></a></div>"
					+ "<div class=\"modal-content\" style=\"width:100%;position:fixed;top:40px;max-height: 100%;overflow: scroll;max-width: 100%;height:94%;\"><p>"
					+ tabDetails + "</p></div></div>";

			htmlTime = htmlTime + "<tr onclick=\"openDialog('time" + id + "')\"><td>" + id + "</td><td>"
					+ soapProjects.get(operationKeyword) + "</td>" + "<td>" + soapServices.get(operationKeyword)
					+ "</td>" + "<td>" + soapOperations.get(operationKeyword) + "</td>" + "<td>" + count + "</td>"
					+ "<td>" + (float) min / 1000 + "</td>" + "<td>" + (float) max / 1000 + "</td></tr>";

			id++;
		}

		returnStr.put("html", htmlTime);
		returnStr.put("timeModal", timeModal);

		return returnStr;
	}

}
