package com.nec.zeusas.mq.exception;

public class MQException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5109234312205055741L;

	public MQException() {
	}

	public MQException(String message) {
		super(message);
	}

	public MQException(Throwable cause) {
		super(cause);
	}

	public MQException(String message, Throwable cause) {
		super(message, cause);
	}

	public MQException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
