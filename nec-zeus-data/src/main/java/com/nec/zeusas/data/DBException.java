package com.nec.zeusas.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DBException extends Exception {
	static final Logger LOG = LoggerFactory.getLogger(DBException.class);

	public DBException() {
		super("");
	}

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable cause) {
		super(cause);
	}

	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	@Override
	public String getMessage(){
		if (!LOG.isDebugEnabled()){
			return super.getMessage();
		}
		StringBuilder bb = new StringBuilder(1024);
		if (LOG.isDebugEnabled()) {
			bb.append(super.getMessage());
			// 仅当DEBUG时，才跟踪
			bb.append("\r\n");
			StackTraceElement []stack = getStackTrace();
			for (StackTraceElement ee : stack) {
				bb.append("  at ");
				bb.append(ee.toString()).append("\r\n");
			}
		}
		return bb.toString();
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
