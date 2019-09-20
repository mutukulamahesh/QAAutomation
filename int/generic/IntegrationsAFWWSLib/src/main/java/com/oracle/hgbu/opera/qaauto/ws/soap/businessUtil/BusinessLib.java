package com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.oracle.hgbu.opera.qaauto.ws.common.utils.DatabaseUtil;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class BusinessLib extends WSLib {
	public static String businessDate = "";
	private static String chain = null;
	private static String property = null;
	private static String interfaceName = null;
	private static String schema = null;
	private static ResultSet gds_hosts = null;
	private static ResultSet defaults = null;
	private static ResultSet extResorts = null;
	private static ResultSet extDatabases = null;
	private static ResultSet conversionCodes = null;
	private static ResultSet addressLov = null;
	private static ResultSet resortBusinessDates = null;
	private static ResultSet udfMappings = null;
	private static ResultSet phoneTypes = null;
	private static ResultSet emailTypes = null;

	public static enum Product {
		OPERA, HTNG, OWS
	}
	public BusinessLib(Product product, String version) {
		try {
			chain = configReader.getChain();
			schema = configReader.getSchema();
			property = configReader.getResort();
			interfaceName = configReader.getHTNGInterface();
			String businessDateQuery = "SELECT RESORT, MAX(BUSINESS_DATE)  as BUSSINESS_DATE FROM " + schema
					+ "BUSINESSDATE GROUP BY RESORT";
			String addressLOVQuery = "SELECT STATE_CODE,COUNTRY_CODE,POSTAL_CODE_FROM,CITY FROM "
					+ " (SELECT STATE_CODE, COUNTRY_CODE, POSTAL_CODE_FROM,CITY,ROW_NUMBER() OVER "
					+ " (PARTITION BY STATE_CODE ORDER BY STATE_CODE) AS ROW_NUMBER FROM " + schema + "POSTAL_CODES WHERE STATE_CODE IS NOT NULL AND COUNTRY_CODE IS NOT NULL AND CITY IS NOT NULL AND POSTAL_CODE_FROM IS NOT NULL)"
					+ "WHERE ROW_NUMBER<=10";
			String gdsHostsQuery="", htngDefaults="",htngExtResorts="",convCodes="",extDatabasesQuery="",UDFMappingQuery="",phoneTypesQuery="",emailTypesQuery="";
			
			if(version.equalsIgnoreCase("5.5")) {
				gdsHostsQuery = "SELECT GHC.GDS_HOST, GHC.GDS_CARRIER, GHC.INACTIVE_DATE, GCR.CHAIN_CODE AS GCR_CHAIN_CODE, GCR.RESORT, "
						+ "GDS_RESORT, GCR.BEGIN_DATE, GCR.END_DATE, GCR.ACTIVATE_YN, GH.NAME, GH.CHANNEL_TYPE,GH.RATE_TYPE, "
						+ "GH.SESSION_CONTROL,GH.ALTERNATE_RESORT, GH.RATE_CHANGE,GH.SELL_BY, GH.ACCEPT_LOWER_RATES_YN, "
						+ "GH.NO_NIGHT_LIMITS, GH.MAX_ROOM_LIMIT, GH.BOOKINGLIMIT_YN, GH.ADD_ON_LICENSE FROM " + schema
						+ "GDS_HOST_CARRIERS GHC INNER JOIN " + schema + "GDS_CONV_RESORTS GCR ON "
						+ " GHC.GDS_HOST = GCR.GDS_HOST INNER JOIN " + schema
						+ "GDS_HOSTS GH ON GHC.GDS_HOST = GH.GDS_HOST AND GDS_RESORT='"
						+ property + "'";
				System.out.println(gdsHostsQuery);
				htngDefaults = "SELECT PARAMETER_GROUP,PARAMETER_NAME,PARAMETER_VALUE,INTERFACE_ID FROM " + schema
						+ "INT_PARAMETERS WHERE INTERFACE_ID='" + interfaceName + "'";
				htngExtResorts = "SELECT INTERFACE_ID, PMS_RESORT, EXTERNAL_RESORT "
						+ "FROM " + schema + "INT_INTERFACE_SETUP WHERE INACTIVE_DATE IS NULL AND PMS_RESORT='" + property + "'";
				convCodes = "SELECT INTERFACE_ID, RESORT, CONVERSION_CODE, PMS_VALUE, EXT_VALUE, MASTER_VALUE "
						+ " FROM " +  schema + "INT_CC_DETAILS WHERE ACTIVE_YN='Y' AND RESORT='" + property + "'";
				extDatabasesQuery = "SELECT D.DATABASE_ID, INTERFACE_TYPE, RESORT FROM " + schema + "DATABASES D "
						+ "INNER JOIN " + schema + "DATABASE_RESORTS DR ON D.DATABASE_ID = DR.DATABASE_ID WHERE "
						+ "RESORT= '" + property + "'";
				UDFMappingQuery = "SELECT * FROM "+schema+"OWS_UDFS_MAPPING WHERE UPDATE_YN='Y' AND ACTIVE_YN='Y' ";
				phoneTypesQuery = "SELECT INTERFACE_ID, INTCC.RESORT, CONVERSION_CODE, PMS_VALUE, EXT_VALUE, MASTER_VALUE FROM PHONE_TYPES PT JOIN INT_CC_DETAILS INTCC ON PT.PHONE_TYPE = INTCC.PMS_VALUE "
						+ " WHERE CONVERSION_CODE='PHONE_TYPE' AND PT.PHONE_ROLE='PHONE' AND INTCC.ACTIVE_YN='Y' AND INTCC.RESORT='"+property + "'";
				emailTypesQuery = "SELECT INTERFACE_ID, INTCC.RESORT, CONVERSION_CODE, PMS_VALUE, EXT_VALUE, MASTER_VALUE FROM PHONE_TYPES PT JOIN INT_CC_DETAILS INTCC ON PT.PHONE_TYPE = INTCC.PMS_VALUE "
						+ " WHERE CONVERSION_CODE='PHONE_TYPE' AND PT.PHONE_ROLE='EMAIL' AND INTCC.ACTIVE_YN='Y' AND INTCC.RESORT='"+property + "'";
			}
			else {
				gdsHostsQuery = "SELECT GHC.GDS_HOST, GHC.GDS_CARRIER, GHC.INACTIVE_DATE, GHC.CHAIN_CODE, GCR.CHAIN_CODE AS GCR_CHAIN_CODE, GCR.RESORT, "
						+ "GDS_RESORT, GCR.BEGIN_DATE, GCR.END_DATE, GCR.ACTIVATE_YN, GH.NAME, GH.CHANNEL_TYPE,GH.RATE_TYPE, "
						+ "GH.SESSION_CONTROL,GH.ALTERNATE_RESORT, GH.RATE_CHANGE,GH.SELL_BY, GH.ACCEPT_LOWER_RATES_YN, "
						+ "GH.NO_NIGHT_LIMITS, GH.MAX_ROOM_LIMIT, GH.BOOKINGLIMIT_YN, GH.ADD_ON_LICENSE FROM " + schema
						+ "GDS_HOST_CARRIERS GHC INNER JOIN " + schema + "GDS_CONV_RESORTS GCR ON "
						+ " GHC.GDS_HOST = GCR.GDS_HOST INNER JOIN " + schema
						+ "GDS_HOSTS GH ON GHC.GDS_HOST = GH.GDS_HOST AND GHC.CHAIN_CODE = GH.CHAIN_CODE AND GH.CHAIN_CODE='"
						+ chain + "'";
				System.out.println(gdsHostsQuery);
				htngDefaults = "SELECT PARAMETER_GROUP,PARAMETER_NAME,PARAMETER_VALUE,INTERFACE_ID FROM " + schema
						+ "INT_PARAMETERS WHERE CHAIN_CODE='" + chain + "'";
				htngExtResorts = "SELECT INTERFACE_ID, PMS_RESORT, EXTERNAL_RESORT "
						+ "FROM " + schema + "INT_INTERFACE_SETUP WHERE INACTIVE_DATE IS NULL AND CHAIN_CODE='" + chain + "'";
				convCodes = "SELECT INTERFACE_ID, RESORT, CONVERSION_CODE, PMS_VALUE, EXT_VALUE, MASTER_VALUE,"
						+ "CHAIN_CODE FROM " +  schema + "INT_CC_DETAILS WHERE ACTIVE_YN='Y' AND CHAIN_CODE='" + chain + "'";
				extDatabasesQuery = "SELECT D.DATABASE_ID, INTERFACE_TYPE, RESORT FROM " + schema + "DATABASES D "
						+ "INNER JOIN " + schema + "DATABASE_RESORTS DR ON D.DATABASE_ID = DR.DATABASE_ID WHERE "
						+ "D.CHAIN_CODE= '" + chain + "'";
				UDFMappingQuery = "SELECT * FROM "+schema+"OWS_UDFS_MAPPING WHERE CHAIN_CODE='" + chain + "' AND UPDATE_YN='Y' AND ACTIVE_YN='Y' ";
				phoneTypesQuery = "SELECT INTERFACE_ID, INTCC.RESORT, CONVERSION_CODE, PMS_VALUE, EXT_VALUE, MASTER_VALUE FROM PHONE_TYPES PT JOIN INT_CC_DETAILS INTCC ON PT.PHONE_TYPE = INTCC.PMS_VALUE AND PT.CHAIN_CODE = INTCC.CHAIN_CODE"
						+ " WHERE CONVERSION_CODE='PHONE_TYPE' AND PT.PHONE_ROLE='PHONE' AND INTCC.ACTIVE_YN='Y' AND INTCC.CHAIN_CODE='"+chain + "'";
				emailTypesQuery = "SELECT INTERFACE_ID, INTCC.RESORT, CONVERSION_CODE, PMS_VALUE, EXT_VALUE, MASTER_VALUE FROM PHONE_TYPES PT JOIN INT_CC_DETAILS INTCC ON PT.PHONE_TYPE = INTCC.PMS_VALUE AND PT.CHAIN_CODE = INTCC.CHAIN_CODE"
						+ " WHERE CONVERSION_CODE='PHONE_TYPE' AND PT.PHONE_ROLE='EMAIL' AND INTCC.ACTIVE_YN='Y' AND INTCC.CHAIN_CODE='"+chain + "'";
				
			}
			
			if (product == Product.OWS) {
				gds_hosts = DatabaseUtil.executeQuery(gdsHostsQuery, dbConnection);
				udfMappings = DatabaseUtil.executeQuery(UDFMappingQuery, dbConnection);
			}

			if (product == Product.HTNG) {
				defaults = DatabaseUtil.executeQuery(htngDefaults, dbConnection);
				extResorts = DatabaseUtil.executeQuery(htngExtResorts, dbConnection);
				conversionCodes = DatabaseUtil.executeQuery(convCodes, dbConnection);
				extDatabases = DatabaseUtil.executeQuery(extDatabasesQuery, dbConnection);
				udfMappings = DatabaseUtil.executeQuery(UDFMappingQuery, dbConnection);
				phoneTypes = DatabaseUtil.executeQuery(phoneTypesQuery, dbConnection);
				emailTypes = DatabaseUtil.executeQuery(emailTypesQuery, dbConnection);
			}

			if (product == Product.OPERA) {
				addressLov = DatabaseUtil.executeQuery(addressLOVQuery, dbConnection);
				resortBusinessDates = DatabaseUtil.executeQuery(businessDateQuery, dbConnection);
				udfMappings = DatabaseUtil.executeQuery(UDFMappingQuery, dbConnection);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public BusinessLib() {
		
	}

	public String getChannelCarier(String resort, String channel) throws SQLException {
		String carrierCode = "";
		if (gds_hosts != null) {
			gds_hosts.beforeFirst();
			while (gds_hosts.next()) {
				if (resort.trim().equalsIgnoreCase(gds_hosts.getString("RESORT"))
						&& channel.trim().equalsIgnoreCase(gds_hosts.getString("GDS_HOST"))) {
					carrierCode = ((gds_hosts.getString("GDS_CARRIER") == null) ? ""
							: gds_hosts.getString("GDS_CARRIER"));
					System.out.println(carrierCode);
					break;
				}
			}
		}
		
		return carrierCode;
	}

	public String getGCRChainCode(String resort, String channel) throws SQLException {
		String gcrChainCode = "";
		if (gds_hosts != null) {
			gds_hosts.beforeFirst();
			while (gds_hosts.next()) {
				if (resort.trim().equalsIgnoreCase(gds_hosts.getString("RESORT"))
						&& channel.trim().equalsIgnoreCase(gds_hosts.getString("GDS_HOST"))) {
					gcrChainCode = ((gds_hosts.getString("GCR_CHAIN_CODE") == null) ? ""
							: gds_hosts.getString("GCR_CHAIN_CODE"));
					System.out.println(gcrChainCode);
					break;
				}
			}
		}
		
		return gcrChainCode;
	}
	
	public String getUDFMapping(String udfName,String moduleName) throws SQLException {
		String udf_label="";

		if(udfMappings !=null) {
			udfMappings.beforeFirst();
			while(udfMappings.next()) {

				if(udfName.trim().equalsIgnoreCase(udfMappings.getString("UDF_NAME").trim()) && moduleName.trim().equalsIgnoreCase(udfMappings.getString("MODULE_NAME").trim())) {

					udf_label=udfMappings.getString("UDF_LABEL");

					break;
				}
			}
		}
		return udf_label;
	}
	public String getUDFLabel(String udfType,String moduleName) throws SQLException {
		String udf_label="";

		if(udfMappings !=null) {
			udfMappings.beforeFirst();
			while(udfMappings.next()) {

				if(udfType.trim().equalsIgnoreCase(udfMappings.getString("UDF_TYPE").trim()) && moduleName.trim().equalsIgnoreCase(udfMappings.getString("MODULE_NAME").trim())) {
					udf_label=udfMappings.getString("UDF_LABEL");
					break;
				}
			}
		}
		return udf_label;
	}
	public String getUDFName(String udfType,String udfLabel,String moduleName) throws SQLException {
		String udf_Name="";

		if(udfMappings !=null) {
			udfMappings.beforeFirst();
			while(udfMappings.next()) {

				if(udfType.trim().equalsIgnoreCase(udfMappings.getString("UDF_TYPE").trim())&& udfLabel.trim().equalsIgnoreCase(udfMappings.getString("UDF_LABEL").trim()) && moduleName.trim().equalsIgnoreCase(udfMappings.getString("MODULE_NAME").trim())) {
					udf_Name=udfMappings.getString("UDF_NAME");
					break;
				}
			}
		}
		return udf_Name;
	}
	public String getChannelResort(String resort, String channel) throws SQLException {
		String channelResort = "";
		if (gds_hosts != null) {
			gds_hosts.beforeFirst();
			while (gds_hosts.next()) {
				if (resort.trim().equalsIgnoreCase(gds_hosts.getString("RESORT"))
						&& channel.trim().equalsIgnoreCase(gds_hosts.getString("GDS_HOST"))) {
					channelResort = ((gds_hosts.getString("GDS_RESORT") == null) ? ""
							: gds_hosts.getString("GDS_RESORT"));
					break;
				}
			}
		}
		return channelResort;
	}

	public String getChannelBeginDate(String resort, String channel) throws SQLException {
		String beginDate = "";
		if (gds_hosts != null) {
			gds_hosts.beforeFirst();
			while (gds_hosts.next()) {
				if (resort.trim().equalsIgnoreCase(gds_hosts.getString("RESORT"))
						&& channel.trim().equalsIgnoreCase(gds_hosts.getString("GDS_HOST"))) {
					beginDate = ((gds_hosts.getString("BEGIN_DATE") == null) ? "" : gds_hosts.getString("BEGIN_DATE"));
					break;
				}
			}
		}
		return beginDate;
	}

	public String getChannelEndDate(String resort, String channel) throws SQLException {
		String endDate = "";
		if (gds_hosts != null) {
			gds_hosts.beforeFirst();
			while (gds_hosts.next()) {
				if (resort.trim().equalsIgnoreCase(gds_hosts.getString("RESORT"))
						&& channel.trim().equalsIgnoreCase(gds_hosts.getString("GDS_HOST"))) {
					endDate = ((gds_hosts.getString("END_DATE") == null) ? "" : gds_hosts.getString("END_DATE"));
					break;
				}
			}
		}
		return endDate;
	}

	public String getChannelType(String channel) throws SQLException {
		String channelType = "";
		if (gds_hosts != null) {
			gds_hosts.beforeFirst();
			while (gds_hosts.next()) {
				if (channel.trim().equalsIgnoreCase(gds_hosts.getString("GDS_HOST"))) {
					channelType = ((gds_hosts.getString("CHANNEL_TYPE") == null) ? ""
							: gds_hosts.getString("CHANNEL_TYPE"));
					break;
				}
			}
		}
		return channelType;
	}

	public String getChannel() {
		String channel = configReader.getChannel();
		return channel;
	}

	public void setOWSHeader(String uname, String pwd, String resort, String channelType, String channelCarrier) {
		setData("{var_userName}", uname);
		setData("{var_password}", pwd);
		setData("{var_resort}", resort);
		setData("{var_systemType}", channelType);
		setData("{var_channelCarrier}", channelCarrier);
		setData("{var_msgID}",UUID.randomUUID().toString());
	}

	public void setOperaHeader(String uname) {
		setData("{var_userName}", uname);
		setData("{var_msgID}",UUID.randomUUID().toString());
	}

	public String getDefaultCode(String interfaceId, String paramGroup, String paramName) throws Exception {
		String defaultValue = "";
		if (defaults != null) {
			defaults.beforeFirst();
			while (defaults.next()) {
				if (paramGroup.trim().equalsIgnoreCase(defaults.getString("PARAMETER_GROUP"))
						&& paramName.trim().equalsIgnoreCase(defaults.getString("PARAMETER_NAME"))
						&& interfaceId.trim().equalsIgnoreCase(defaults.getString("INTERFACE_ID"))) {
					defaultValue = defaults.getString("PARAMETER_VALUE");
					break;
				}
			}
		}
		return defaultValue;
	}

	public String getExtResort(String resort, String interfaceID) throws Exception {
		String extResort = "";
		if (extResorts != null) {
			extResorts.beforeFirst();
			while (extResorts.next()) {
				if (interfaceID.trim().equalsIgnoreCase(extResorts.getString("INTERFACE_ID"))
						&& resort.trim().equalsIgnoreCase(extResorts.getString("PMS_RESORT"))) {
					extResort = extResorts.getString("EXTERNAL_RESORT");
					break;
				}
			}
		}
		return extResort;
	}

	public String getPmsValue(String resort, String interfaceID, String conversionCode, String extValue)
			throws Exception {
		String pmsValue = "";
		if (conversionCodes != null) {
			conversionCodes.beforeFirst();
			while (conversionCodes.next()) {
				if (interfaceID.trim().equalsIgnoreCase(conversionCodes.getString("INTERFACE_ID"))
						&& resort.trim().equalsIgnoreCase(conversionCodes.getString("RESORT"))
						&& conversionCode.trim().equalsIgnoreCase(conversionCodes.getString("CONVERSION_CODE"))
						&& extValue.trim().equalsIgnoreCase(conversionCodes.getString("EXT_VALUE"))) {
					pmsValue = conversionCodes.getString("PMS_VALUE");
					break;
				}
			}
		}
		return pmsValue;
	}

	public String getMasterValue(String resort, String interfaceID, String conversionCode, String extValue)
			throws Exception {
		String masterValue = "";
		if (conversionCodes != null) {
			conversionCodes.beforeFirst();
			while (conversionCodes.next()) {
				if (interfaceID.trim().equalsIgnoreCase(conversionCodes.getString("INTERFACE_ID"))
						&& resort.trim().equalsIgnoreCase(conversionCodes.getString("RESORT"))
						&& conversionCode.trim().equalsIgnoreCase(conversionCodes.getString("CONVERSION_CODE"))
						&& extValue.trim().equalsIgnoreCase(conversionCodes.getString("EXT_VALUE"))) {
					masterValue = conversionCodes.getString("MASTER_VALUE");
					break;
				}
			}
		}
		return masterValue;
	}

	public String getExtValue(String resort, String interfaceID, String conversionCode, String pmsValue)
			throws Exception {
		String extValue = "";
		if (conversionCodes != null) {
			System.out.println(conversionCodes.getFetchSize());
			conversionCodes.beforeFirst();
			while (conversionCodes.next()) {
				if (interfaceID.trim().equalsIgnoreCase(conversionCodes.getString("INTERFACE_ID"))
						&& resort.trim().equalsIgnoreCase(conversionCodes.getString("RESORT"))
						&& conversionCode.trim().equalsIgnoreCase(conversionCodes.getString("CONVERSION_CODE"))
						&& pmsValue.trim().equalsIgnoreCase(conversionCodes.getString("PMS_VALUE"))) {
					extValue = conversionCodes.getString("EXT_VALUE");
					break;
				}
			}
		}
		else {
			System.out.println("ConversionCodes is null");
		}
		return extValue;
	}

	public String getRandomExtValue(String resort, String interfaceID, String conversionCode, String...typeArr) throws Exception {
		String extValue = "", phoneType="";
		if(typeArr.length == 1) {
			if(conversionCode.equalsIgnoreCase("PHONE_TYPE")) {
				phoneType = typeArr[0];
			}
		}
		if(phoneType.equalsIgnoreCase("PHONE")) {
			if (phoneTypes != null) {
				phoneTypes.beforeFirst();
				while (phoneTypes.next()) {
					if (interfaceID.trim().equalsIgnoreCase(phoneTypes.getString("INTERFACE_ID"))
							&& resort.trim().equalsIgnoreCase(phoneTypes.getString("RESORT"))
							&& conversionCode.trim().equalsIgnoreCase(phoneTypes.getString("CONVERSION_CODE"))) {
						extValue = phoneTypes.getString("EXT_VALUE");
						break;
					}
				}
			}
		}
		else if(phoneType.equalsIgnoreCase("EMAIL")) {
			if (emailTypes != null) {
				emailTypes.beforeFirst();
				while (emailTypes.next()) {
					if (interfaceID.trim().equalsIgnoreCase(emailTypes.getString("INTERFACE_ID"))
							&& resort.trim().equalsIgnoreCase(emailTypes.getString("RESORT"))
							&& conversionCode.trim().equalsIgnoreCase(emailTypes.getString("CONVERSION_CODE"))) {
						extValue = emailTypes.getString("EXT_VALUE");
						break;
					}
				}
			}
		}
		else if (conversionCodes != null) {
			conversionCodes.beforeFirst();
			while (conversionCodes.next()) {
				if (interfaceID.trim().equalsIgnoreCase(conversionCodes.getString("INTERFACE_ID"))
						&& resort.trim().equalsIgnoreCase(conversionCodes.getString("RESORT"))
						&& conversionCode.trim().equalsIgnoreCase(conversionCodes.getString("CONVERSION_CODE"))) {
					extValue = conversionCodes.getString("EXT_VALUE");
					break;
				}
			}
		}
		return extValue;
	}

	public String getRandomPMSValue(String resort, String interfaceID, String conversionCode, String...typeArr) throws Exception {
		String pmsValue = "", phoneType="";

		if(typeArr.length == 1) {
			if(conversionCode.equalsIgnoreCase("PHONE_TYPE")) {
				phoneType = typeArr[0];
			}
		}

		if(phoneType.equalsIgnoreCase("PHONE")) {
			if (phoneTypes != null) {
				phoneTypes.beforeFirst();
				while (phoneTypes.next()) {
					if (interfaceID.trim().equalsIgnoreCase(phoneTypes.getString("INTERFACE_ID"))
							&& resort.trim().equalsIgnoreCase(phoneTypes.getString("RESORT"))
							&& conversionCode.trim().equalsIgnoreCase(phoneTypes.getString("CONVERSION_CODE"))) {
						pmsValue = phoneTypes.getString("PMS_VALUE");
						break;
					}
				}
			}
		}
		else if(phoneType.equalsIgnoreCase("EMAIL")) {
			if (emailTypes != null) {
				emailTypes.beforeFirst();
				while (emailTypes.next()) {
					if (interfaceID.trim().equalsIgnoreCase(emailTypes.getString("INTERFACE_ID"))
							&& resort.trim().equalsIgnoreCase(emailTypes.getString("RESORT"))
							&& conversionCode.trim().equalsIgnoreCase(emailTypes.getString("CONVERSION_CODE"))) {
						pmsValue = emailTypes.getString("PMS_VALUE");
						break;
					}
				}
			}
		}
		else if (conversionCodes != null) {
			conversionCodes.beforeFirst();
			while (conversionCodes.next()) {
				if (interfaceID.trim().equalsIgnoreCase(conversionCodes.getString("INTERFACE_ID"))
						&& resort.trim().equalsIgnoreCase(conversionCodes.getString("RESORT"))
						&& conversionCode.trim().equalsIgnoreCase(conversionCodes.getString("CONVERSION_CODE"))) {
					pmsValue = conversionCodes.getString("PMS_VALUE");
					break;
				}
			}
		}
		return pmsValue;
	}

	public String getExternalDatabase(String resort, String interfaceID) throws SQLException  {
		String extDb = "";
		if (extDatabases != null) {

			extDatabases.beforeFirst();

			while (extDatabases.next()) {
				if (interfaceID.trim().equalsIgnoreCase(extDatabases.getString("INTERFACE_TYPE"))
						&& resort.trim().equalsIgnoreCase(extDatabases.getString("RESORT"))) {
					extDb = extDatabases.getString("DATABASE_ID");
					break;
				}
			}

		}
		return extDb;
	}

	public String getHTNGInterface() {
		String interfaceName = configReader.getHTNGInterface();
		return interfaceName;
	}

	public String getInterfaceFromAddress() {
		String interfaceFromAddress = configReader.getHTNGInterfaceFromAddress();
		return interfaceFromAddress;
	}

	public void setHTNGHeader(String uname, String pwd, String fromAddress) {
		setData("{var_userName}", uname);
		setData("{var_password}", pwd);
		setData("{var_fromAddress}", fromAddress);
		setData("{var_msgID}",UUID.randomUUID().toString());
	}

	public String getResort() {
		String resortName = configReader.getResort();
		return resortName;
	}

	public String getChain() {
		return chain;
	}

	public HashMap<String, String> fetchAddressLOV(String stateCode, String country) throws Exception {
		List<HashMap<String, String>> addresses = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> address = new HashMap<String, String>();
		String city = "", zip = "", countryCd = "";
		if (addressLov != null) {
			addressLov.beforeFirst();
			while (addressLov.next()) {
				if (addressLov.getString("STATE_CODE").equalsIgnoreCase(stateCode)
						&& addressLov.getString("COUNTRY_CODE").equalsIgnoreCase(country)) {
					address = new HashMap<String, String>();
					zip = addressLov.getString("POSTAL_CODE_FROM");
					city = addressLov.getString("CITY");
					countryCd = addressLov.getString("COUNTRY_CODE");
					address.put("City", city);
					address.put("Zip", zip);
					address.put("Country", countryCd);
					addresses.add(address);

				}

			}
			int randomNo = 1 + (int) (Math.random() * ((addresses.size() - 1) + 1));
			address=addresses.get(randomNo - 1);
		}
		return address;
	}

	public HashMap<String, String> fetchAddressLOV() throws Exception {
		List<HashMap<String, String>> addresses = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> address = new HashMap<String, String>();
		String city = "", zip = "", country = "", state = "";
		if (addressLov != null) {
			addressLov.beforeFirst();
			while (addressLov.next()) {
				address = new HashMap<String, String>();
				zip = addressLov.getString("POSTAL_CODE_FROM");
				city = addressLov.getString("CITY");
				country = addressLov.getString("COUNTRY_CODE");
				state = addressLov.getString("STATE_CODE");
				address.put("City", city);
				address.put("Zip", zip);
				address.put("Country", country);
				address.put("State", state);
				addresses.add(address);
			}
			int randomNo = 1 + (int) (Math.random() * ((addresses.size() - 1) + 1));
			address=addresses.get(randomNo - 1);
		}
		return address;
	}

	public String getBusinessDate(String resort) throws SQLException {
		String businessDate = "";
		if (resortBusinessDates != null) {
			resortBusinessDates.beforeFirst();
			while (resortBusinessDates.next()) {
				if (resort.trim().equalsIgnoreCase(resortBusinessDates.getString("RESORT"))) {
					businessDate = resortBusinessDates.getString("BUSSINESS_DATE");
					break;
				}
			}
		}

		WSClient.writeToReport(LogStatus.INFO, " inside business header "+resort+businessDate);
		return businessDate;
	}

	public String getUserName() {
		return configReader.getUser();
	}

	public String getPassword() {
		return configReader.getPassword();
	}
}
