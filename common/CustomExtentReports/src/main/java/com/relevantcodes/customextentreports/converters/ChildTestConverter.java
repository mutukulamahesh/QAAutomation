package com.relevantcodes.customextentreports.converters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.relevantcodes.customextentreports.LogSettings;
import com.relevantcodes.customextentreports.model.Log;
import com.relevantcodes.customextentreports.model.Test;
import com.relevantcodes.customextentreports.utils.DateTimeUtil;
import com.relevantcodes.customextentreports.utils.ExtentUtil;

public class ChildTestConverter extends LogSettings {
	private Element test;
	
	public List<Test> getNodeList() {
		Elements allNodes = test.select(".node-list > li");
        Test extentNode;        
        
        Map<Integer, Test> testLevelsMap = new LinkedHashMap<Integer, Test>();
        
        if (allNodes != null && allNodes.size() > 0) {
            List<Test> extentNodeList = new ArrayList<Test>();
            
            for (Element node : allNodes) {
                extentNode = new Test();
                
                extentNode.isChildNode = true;
                
                extentNode.setName(node.select(".test-node-name").first().html());

                Element description = node.select(".test-node-desc").first();
                if (description != null) {
                    extentNode.setDescription(description.html());
                }
                
                extentNode.setStatus(
                        ExtentUtil.toLogStatus(
                                node.select(".test-status").first().text()
                            )
                );

                extentNode.setStartedTime(
                        DateTimeUtil.getDate(
                                node.select(".test-started-time").first().text(), 
                                getLogDateTimeFormat()
                        )
                );
                
                extentNode.setEndedTime(
                        DateTimeUtil.getDate(
                                node.select(".test-ended-time").first().text(), 
                                getLogDateTimeFormat()
                        )
                );
                
                LogConverter logConverter = new LogConverter(node, true);
                List<Log> logList = logConverter.getLogList();
                
                extentNode.setLog(logList);

                Integer level = Integer.valueOf(node.attr("class").replaceAll("\\D+",""));
                testLevelsMap.put(Integer.valueOf(level), extentNode);
                
                if (level != 1) {
                	testLevelsMap.get(level - 1).setNode(extentNode);
                }
                else {
                	extentNodeList.add(extentNode);
                }
            }
            
            return extentNodeList;
        }
        
        return null;
	}
	
	public ChildTestConverter(Element test) {
		this.test = test;
	}
}
