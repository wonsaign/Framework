package com.nec.zeusas.security.auth.service;

public class AuthentException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public AuthentException() {
	}

	public AuthentException(String message) {
		super(message);
	}

	public AuthentException(Throwable cause) {
		super(cause);
	}

	public AuthentException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
