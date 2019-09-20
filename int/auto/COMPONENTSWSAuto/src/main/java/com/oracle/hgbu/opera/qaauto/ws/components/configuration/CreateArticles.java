package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateArticles extends WSSetUp  {
	public boolean createArticles(String dataset) {
		try {

			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Article</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateArticles", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if (WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Article ID " + WSClient.getData("{var_articleCode}") + " already exists");
				return true;
			} else {
				String req_createArticles = WSClient.createSOAPMessage("CreateArticles", dataset);
				String res_createArticles = WSClient.processSOAPMessage(req_createArticles);
				if (WSAssert.assertIfElementExists(res_createArticles, "CreateArticlesRS_Success", true)) {
					WSClient.writeToReport(LogStatus.PASS, "//CreateArticlesRS/Success exists on the response message");
					dbResult = new LinkedHashMap<String, String>();
					query = WSClient.getQuery("CreateArticles", "QS_01");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if (WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "New ArticlesID has been created");
						return true;
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "New ArticlesID not created");
						return false;
					}

				} else {
					if (WSAssert.assertIfElementExists(res_createArticles, "CreateArticlesRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createArticles, "CreateArticlesRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}

	@Test(groups = { "OperaConfig" })
	public void createMultipleArticles() {
		try {

			int i;
			boolean flag = true;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_resort}", OPERALib.getResort());
			WSClient.setData("{var_chain}", OPERALib.getChain());
			String testName = "CreateArticles";
			WSClient.startTest(testName, "Create Articles", "OperaConfig");
			WSClient.setData("{var_parameter}", "ARTICLES");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter ARTICLES is enabled</b>");

			/***********
			 * Prerequisite : Verify if the parameter ARTICLES is enabled
			 ****************/
			String fetchArticle = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (fetchArticle.equals("N")) {
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter ARTICLES</b>");
				fetchArticle = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (fetchArticle.equals("Y")) {
				WSClient.setData("{var_transCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

				String dataset = "";
				int length = OperaPropConfig.getLengthForCode("ArticleCode") - 1;
				for (i = 1; i <= length; i++) {
					if (i <= 9)
						dataset = "DS_0" + i;
					else
						dataset = "DS_" + i;
					Integer value = (int) Float.parseFloat(OperaPropConfig.getDataSetForCode("ArticleCode", dataset));

					WSClient.setData("{var_articleCode}", value.toString());
					flag = flag && createArticles(dataset);
				}

				if (flag == true)
					OperaPropConfig.setPropertyConfigResults("ArticleCode", "Y");
				else
					OperaPropConfig.setPropertyConfigResults("ArticleCode", "N");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
		}
	}
}