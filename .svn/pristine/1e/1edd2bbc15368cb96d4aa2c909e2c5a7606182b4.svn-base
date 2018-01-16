package com.sinosoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggableImpl implements Loggable {
	// protected static Logger logger;
	//
	// public Log getLogger() {
	// if (logger != null) return logger;
	// return logger = LogFactory.getLog(this.getClass());
	// }

	public LoggableImpl() {
		super();
		getLogger();
	}

	/** 日志记录对象 */
	protected final Log logger = LogFactory.getLog(getClass());

	// private static Map<Class<?>, Log> LOGGERS = new HashMap<Class<?>, Log>();

	/**
	 * 得到日志记录对象
	 * 
	 * @return
	 */
	public Log getLogger() {
		// if (logger == null) {
		// Class<?> cls = this.getClass();
		// logger = LOGGERS.get(cls);
		// if (logger == null) {
		// logger = LogFactory.getLog(this.getClass());
		// LOGGERS.put(cls, logger);
		// }
		// }
		return logger;
	}
}
