package com.oracle.hgbu.opera.qaauto.ws.wrapper;

import com.oracle.hgbu.opera.qaauto.ws.custom.WSLib;

public class WSConfig extends WSLib {
	/*
	 * This class contains methods to access the Opera resort information such
	 * as Chain, Resort, User and Password mapped to the selected entry in the
	 * given environment. This is used across the test suite to execute the
	 * tests against the entry that was parameterized
	 */
	public static String getResort(String EntryKey) throws Exception {
		return configReader.getResort(EntryKey);
	}

	public static String getChain(String EntryKey) throws Exception {
		return configReader.getChain(EntryKey);
	}

	public static String getUser(String EntryKey) throws Exception {
		return configReader.getUser(EntryKey);
	}

	public static String getPassword(String EntryKey) throws Exception {
		return configReader.getPassword(EntryKey);
	}

	public static String getInterface(String EntryKey) throws Exception {
		return configReader.getInterface(EntryKey);
	}
}
