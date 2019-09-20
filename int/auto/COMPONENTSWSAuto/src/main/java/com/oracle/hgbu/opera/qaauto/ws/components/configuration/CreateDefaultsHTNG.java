package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateDefaultsHTNG extends WSSetUp {
	
	   public int changeDefaults(String dataset)
	   {   
		   int created=1;
		   try{
		    String changeInterfaceControlReq=WSClient.createSOAPMessage("ChangeInterfaceControls", dataset);
			String changeInterfaceControlRes=WSClient.processSOAPMessage(changeInterfaceControlReq);
			if(WSAssert.assertIfElementExists(changeInterfaceControlRes, "ChangeInterfaceControlsRS_Success", false)){
				WSClient.writeToReport(LogStatus.INFO, "Successfully Created Default Value");
				//DB Validation -> INT_CC_DEFAULTS or int_parameter
			}
			
				created=0;
				return created;
		   }
		   catch(Exception e)
		   {
			   WSClient.writeToReport(LogStatus.FAIL, "Exception ocuured due to :" +e);
				return 0;
		   }
				
	   }
		public boolean createDefaults(String dataset){
			try{
				
				String fetchInterfaceControlReq=WSClient.createSOAPMessage("FetchInterfaceControls",dataset);
				String fetchInterfaceControlRes=WSClient.processSOAPMessage(fetchInterfaceControlReq);
				if(WSAssert.assertIfElementExists(fetchInterfaceControlRes, "FetchInterfaceControlsRS_Success", false)) {
					if(WSAssert.assertIfElementExists(fetchInterfaceControlRes,"InterfaceControlGroup_InterfaceControls_InterfaceControl_Name",true))
					{   
						String name=WSClient.getElementValue(fetchInterfaceControlRes,"InterfaceControlGroup_InterfaceControls_InterfaceControl_Name", XMLType.RESPONSE);
						String value=WSClient.getElementValue(fetchInterfaceControlRes,"InterfaceControlGroup_InterfaceControls_InterfaceControl_Value", XMLType.RESPONSE);
						if(value.equals(OperaPropConfig.getDataSetForCode("HTNG_Defaults" , dataset)))
						{
							WSClient.writeToReport(LogStatus.INFO, "Default already exists for "+ name);
							return true;
						}
						else
						{
							WSClient.writeToReport(LogStatus.INFO,"Default value for "+name+ " doesnot exist!!");
							
							if(changeDefaults(dataset)==1)
								return true;
							else
							    return false;
							
							
							
						}
						
					}
					else
					{
						WSClient.writeToReport(LogStatus.INFO,"Default value  doesnot exist!!");
						if(changeDefaults(dataset)==1)
							return true;
						else
							return false;
						
					
					}
					}
				else
				{
					return false;
				}
				
					
			}
			catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
				return false;
			}
			
			
		}

		@Test(groups= {"OperaConfig"})
		public void createMultiple_Defaults() {
			int i;
			boolean flag = true;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_interface}", HTNGLib.getHTNGInterface());
			WSClient.setData("{var_Resort}",OPERALib.getResort());
			String testName="CreateDefaults";
			WSClient.startTest(testName, "Create HTNG Defaults", "OperaConfig");
			int length = OperaPropConfig.getLengthForCode("HTNG_Defaults") - 1;
			String dataset;
			for(i=1;i<=length;i++) {
				if(i<=9)
					dataset = "DS_0" + i; 
				else
					dataset = "DS_" + i;
				String value = OperaPropConfig.getDataSetForCode("HTNG_Defaults" , dataset);
				WSClient.setData("{var_defaultValue}", value);
				flag = flag && createDefaults(dataset);
			}
			
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("HTNG_Defaults", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("HTNG_Defaults", "N");
	}
	}
