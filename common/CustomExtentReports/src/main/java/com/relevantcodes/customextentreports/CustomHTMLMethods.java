package com.relevantcodes.customextentreports;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CustomHTMLMethods {

	private static String formatXMLString = "";
	private static int formatXMLLevel = 0;

	private static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		int start = str.indexOf(open);
		if (start != -1) {
			int end = str.indexOf(close, start + open.length());
			if (end != -1) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

	public static String multiLineLog(LogStatus logStatus, String heading, List<String> elements) {

		String str = "<ul class=\"collapsible\" data-collapsible=\"accordion\"><li><div class=\"collapsible-header\" >"
				+ heading + "</div><div class=\"collapsible-body\"><ul class=\"collection\">";
		for (int i = 0; i < elements.size(); i++) {
			str = str + "<li class=\"collection-item\">" + elements.get(i) + "</li>";
		}
		str = str + "</ul></div></li></ul>";

		if (logStatus == LogStatus.PASS) {
			return "<font color=\"green\"><b>" + str + "</b></font>";
		} else if (logStatus == LogStatus.FAIL) {
			return "<font color=\"red\"><b>" + str + "</b></font>";
		}

		else if (logStatus == LogStatus.WARNING) {
			return "<b>" + str + "</b>";
		}

		return "<font color=\"black\">" + str + "</font>";

	}

	public static String logsReport(String logName, List<String> elements) {

		DuplicateReportCode.logtext = "CustomHTMLMethods.logsReport(\"" + logName + "\",Arrays.asList(";

		String errorLogId = StaticValues.getError();

		String errorLog = "<div class=\"commented\" style=\"display:none;\" id=\"" + errorLogId + "\">";

		String modalId = StaticValues.getErrorModal();

		String str = "<ul class=\"collection\">";

		for (int i = 0; i < elements.size(); i++) {

			DuplicateReportCode.logtext = DuplicateReportCode.logtext + "\"" + elements.get(i).replace("\"", "\\\"")
					+ "\",";
			if (elements.get(i).length() != 0) {
				str = str + "<li class=\"collection-item\">" + elements.get(i) + "</li>";
				String elem = elements.get(i).replace("] : </b>", "] : ").replace("<br>", "\n").replace("<b>[", "\n[")
						.replace("&nbsp", " ");
				errorLog = errorLog + elem;
			}
		}
		DuplicateReportCode.logtext = DuplicateReportCode.logtext.substring(0, DuplicateReportCode.logtext.length() - 1)
				+ "))";

		str = str + "</ul>";

		StaticValues.addErrorModal("<div id=\"" + modalId
				+ "\" class=\"modal commented white\" style=\" width: 90%;height: 80%;top:5% !important;max-height:80%\">.<!--"
				+ "<div style=\"postion:relative;\"><h5 style=\"position:absolute;left:13px;\" >" + logName
				+ " Logs</h5><a href=\"#!\"><img src=\"./extentreports/images/close.png\" onclick=\"escapePress();\" class=\"modal-action modal-close tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Close\" style=\"width:15px;height:15px;right:20px;position:absolute;z-index: 100001;\" alt=\"Close\"></a><a href=\"#!\"><img src=\"./extentreports/images/download.png\" class=\"tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Download\""
				+ "onclick=\"downloadLog('" + logName + "','" + errorLogId
				+ "')\" style=\"width:15px;height:15px;right:45px;position:absolute;top:0px;z-index: 100001;\" alt=\"Copy To ClipBoard\"></a></div>"
				+ "<div class=\"modal-content\" style=\"width:100%;position:fixed;top:40px;max-height: 100%;overflow: scroll;max-width: 100%;height:94%;\"><p>"
				+ str.replaceAll("-->", "--&gt") + "</p></div>-->.</div>");

		errorLog = errorLog + "</div>";

		StaticValues.addError(errorLog);

		return modalId;

	}

	public static String printReqResp(String request, String response, String operation, String statusCode,
			String operationKeyword) {

		DuplicateReportCode.modalReq = request;
		DuplicateReportCode.modalResp = response;
		DuplicateReportCode.modalOperation = operation;
		DuplicateReportCode.modalRespCode = statusCode;
		DuplicateReportCode.modalOperationKey = operationKeyword;
		DuplicateReportCode.modals = true;

		String modalId = StaticValues.getModalId();
		String rawXmlId = StaticValues.getrawXmlId();

		if (statusCode.equals("404")) {

			response = "<div bgcolor=\"white\"><FONT FACE=Helvetica><BR CLEAR=all><TABLE border=0 cellspacing=5><TR><TD><BR CLEAR=all><FONT FACE=\"Helvetica\" COLOR=\"black\" SIZE=\"3\"><H2>Error 404--Not Found</H2></FONT></TD></TR></TABLE><TABLE border=0 width=100% cellpadding=10><TR><TD VALIGN=top WIDTH=100% BGCOLOR=white><FONT FACE=\"Courier New\"><FONT FACE=\"Helvetica\" SIZE=\"3\"><H3>From RFC 2068 <i>Hypertext Transfer Protocol -- HTTP/1.1</i>:</H3></FONT><FONT FACE=\"Helvetica\" SIZE=\"3\"><H4>10.4.5 404 Not Found</H4></FONT><P><FONT FACE=\"Courier New\">The server has not found anything matching the Request-URI. No indication is given of whether the condition is temporary or permanent.</p><p>If the server does not wish to make this information available to the client, the status code 403 (Forbidden) can be used instead. The 410 (Gone) status code SHOULD be used if the server knows, through some internally configurable mechanism, that an old resource is permanently unavailable and has no forwarding address.</FONT></P></FONT></TD></TR></TABLE></div>";
			StaticValues.addModal("<div id=\"" + modalId
					+ "\" class=\"modal commented\" style=\" width: 90%;height: 80%;top:5% !important;max-height:80%\">.<!--"
					+ "<div style=\"postion:relative;\"><h5 style=\"position:absolute;left:13px;\" >" + operation
					+ " Response Message</h5><a href=\"#!\"><img src=\"./extentreports/images/close.png\" onclick=\"escapePress();\" class=\"modal-action modal-close tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Close\" style=\"width:15px;height:15px;right:20px;position:absolute;z-index: 100001;\" alt=\"Close\"></a><a href=\"#!\"><img src=\"./extentreports/images/copy.png\" class=\"tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Copy To Clip Board\""
					+ "onclick=\"copyXml('" + rawXmlId
					+ "')\" style=\"width:15px;height:15px;right:45px;position:absolute;top:0px;z-index: 100001;\" alt=\"Copy To ClipBoard\"></a></div>"
					+ "<div class=\"modal-content\" style=\"width:100%;position:fixed;top:40px;max-height: 100%;overflow: scroll;max-width: 100%;height:94%;\">z<p>"
					+ response.replaceAll("-->", "--&gt") + "</p></div>-->.</div>");
			StaticValues.addrawXml(
					"<div class=\"commented\" style=\"display:none;\" id=\"" + rawXmlId + "\">" + response + "</div>");
		} else {
			StaticValues.addModal("<div id=\"" + modalId
					+ "\" class=\"modal commented\" style=\" width: 90%;height: 80%;top:5% !important;max-height:90%\">.<!--"
					+ "<div style=\"postion:relative;\"><h5 style=\"position:absolute;left:13px;\">" + operation
					+ " Response XML</h5><a href=\"#!\"><img src=\"./extentreports/images/close.png\"  onclick=\"escapePress();\" class=\"modal-action modal-close tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Close\" style=\"width:15px;height:15px;right:20px;position:absolute;z-index: 100001;\" alt=\"Close\"></a><a href=\"#!\"><img src=\"./extentreports/images/copy.png\" class=\"tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Copy To Clip Board\""
					+ "onclick=\"copyXml('" + rawXmlId
					+ "')\" style=\"width:15px;height:15px;right:45px;position:absolute;top:0px;z-index: 100001;\" alt=\"Copy To ClipBoard\"></a></div>"
					+ "<div class=\"modal-content\" style=\"width:100%;position:fixed;top:40px;max-height: 100%;overflow: scroll;max-width: 100%;height:94%;\"><p>"
					+ formatXML(response).replaceAll("-->", "--&gt") + "</p></div>-->.</div>");
			StaticValues.addrawXml(
					"<div class=\"commented\" style=\"display:none;\" id=\"" + rawXmlId + "\">" + response + "</div>");

		}
		String modalId1 = StaticValues.getModalId();
		String rawXmlId1 = StaticValues.getrawXmlId();

		OperationTime.insertReqResp(operationKeyword, modalId1, modalId);

		StaticValues.addModal("<div id=\"" + modalId1
				+ "\" class=\"modal commented\" style=\" width: 90%;height: 80%;top:5% !important;max-height:90%\">.<!--"
				+ "<div style=\"postion:relative;\"><h5 style=\"position:absolute;left:13px;\">" + operation
				+ " Request XML</h5><a href=\"#!\"><img src=\"./extentreports/images/close.png\" onclick=\"escapePress();\" class=\"modal-action modal-close tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Close\" style=\"width:15px;height:15px;right:20px;position:absolute;z-index: 100001;\" alt=\"Close\"></a><a href=\"#!\"><img src=\"./extentreports/images/copy.png\" class=\"tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"Copy To Clip Board\""
				+ "onclick=\"copyXml('" + rawXmlId1
				+ "')\" style=\"width:15px;height:15px;right:45px;position:absolute;top:0px;z-index: 100001;\" alt=\"Copy To ClipBoard\"></a></div>"
				+ "<div class=\"modal-content\" style=\"width:100%;position:fixed;top:40px;max-height: 100%;overflow: scroll;max-width: 100%;height:94%;\"><p>"
				+ formatXML(request).replaceAll("-->", "--&gt") + "</p></div>-->.</div>");

		StaticValues.addrawXml("<div class=\"commented\" style=\"display:none;\" id=\"" + rawXmlId1 + "\">"
				+ request.replaceAll("<", "&lt") + "</div>");

		return "Request Message >> <a href=\"#" + modalId1 + "\" onclick=\"openDialog('" + modalId1 + "')\">"
		+ "Click here" + "</a>" + " Response Message >> " + "<a href=\"#" + modalId
		+ "\" onclick=\"openDialog('" + modalId + "')\">" + "Click here" + "</a>";

	}

	private static String formatXML(String input) {

		formatXMLString = "";
		formatXMLLevel = 0;

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse(new InputSource(new StringReader(input)));
			recuriveFormat(document.getDocumentElement());
		} catch (Exception e) {

		}

		return formatXMLString;

	}

	private static String tabSpaces(int level1) {
		String sp = "";
		for (int i = 0; i < level1; i++) {
			sp = sp + "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
		}
		return sp;
	}

	private static void recuriveFormat(Node node) {

		if (!node.hasAttributes() && node.getTextContent().length() != 0) {
			formatXMLString = formatXMLString + tabSpaces(formatXMLLevel) + "<font color=\"#872170\">&lt"
					+ node.getNodeName() + "&gt</font><br>";
		} else if ((!node.hasAttributes()) && node.getTextContent().length() == 0) {
			if (node.hasChildNodes()) {
				formatXMLString = formatXMLString + tabSpaces(formatXMLLevel) + "<font color=\"#872170\">&lt"
						+ node.getNodeName() + "&gt</font><br>";
			} else {
				formatXMLString = formatXMLString + tabSpaces(formatXMLLevel) + "<font color=\"#872170\">&lt"
						+ node.getNodeName() + "/&gt</font><br>";
			}
		} else {
			formatXMLString = formatXMLString + tabSpaces(formatXMLLevel) + "<font color=\"#872170\">&lt"
					+ node.getNodeName() + "</font> ";
		}

		NamedNodeMap atributes = node.getAttributes();
		for (int i = 0; i < atributes.getLength(); i++) {
			Node atributeNode = atributes.item(i);
			formatXMLString = formatXMLString + "<font color=\"#A33403\">" + atributeNode.getNodeName()
			+ "</font><font color=\"#872170\">=\"</font><font color=\"#091B8B\">" + atributeNode.getNodeValue()
			+ "</font><font color=\"#872170\">\"</font> ";
		}

		if (node.hasAttributes() && node.getTextContent().length() != 0) {
			formatXMLString = formatXMLString.substring(0, formatXMLString.length() - 1);
			formatXMLString = formatXMLString + "<font color=\"#872170\">&gt</font><br>";
		} else if (node.hasAttributes() && node.hasChildNodes()) {
			formatXMLString = formatXMLString.substring(0, formatXMLString.length() - 1);
			formatXMLString = formatXMLString + "<font color=\"#872170\">&gt</font><br>";
		} else if (node.hasAttributes() && !node.hasChildNodes()) {
			formatXMLString = formatXMLString.substring(0, formatXMLString.length() - 1);
			formatXMLString = formatXMLString + "<font color=\"#872170\">/&gt</font><br>";
		}

		int childs = 0;
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentNode = childNodes.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				childs++;
				// calls this method for all the children which is Element
				formatXMLLevel++;
				recuriveFormat(currentNode);
				formatXMLLevel--;
			}
		}

		if (childs == 0 && node.getTextContent().length() != 0) {
			if (node.getTextContent().length() > 100) {
				formatXMLString = formatXMLString + tabSpaces(formatXMLLevel + 1) + node.getTextContent() + "<br>"
						+ tabSpaces(formatXMLLevel) + "<font color=\"#872170\">&lt/" + node.getNodeName()
						+ "&gt</font><br>";
			} else {
				formatXMLString = formatXMLString.substring(0, formatXMLString.length() - 4);
				formatXMLString = formatXMLString + node.getTextContent() + "<font color=\"#872170\">&lt/"
						+ node.getNodeName() + "&gt</font><br>";
			}
		}
		if (childs != 0) {
			formatXMLString = formatXMLString + tabSpaces(formatXMLLevel) + "<font color=\"#872170\">&lt/"
					+ node.getNodeName() + "&gt</font><br>";
		}

	}

}
