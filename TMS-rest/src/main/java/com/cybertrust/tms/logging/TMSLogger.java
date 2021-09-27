package com.cybertrust.tms.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//...
public class TMSLogger {
	final static Logger logger = LoggerFactory.getLogger("TMS REST");
	
	public static Logger getLoggger() {
		return logger;
	}

}
