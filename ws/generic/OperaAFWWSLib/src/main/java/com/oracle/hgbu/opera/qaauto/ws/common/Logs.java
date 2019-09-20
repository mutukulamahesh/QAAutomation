package com.oracle.hgbu.opera.qaauto.ws.common;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class Logs {
	public static Logger logger = Logger.getLogger(Logs.class.getName());
	public static void start(String fileName) throws IOException, SQLException {
		SimpleLayout layout = new SimpleLayout();
		FileAppender appender = new FileAppender(layout, fileName, false);
		logger.addAppender(appender);
	}

}