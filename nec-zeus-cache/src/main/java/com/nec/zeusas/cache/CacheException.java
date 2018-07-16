package com.nec.zeusas.cache;

/*
 * Copyright 2017, Bright Grand Technologies Co., Ltd.
 * 
 * 版权所有，明弘科技(北京)有限公司，2017
 * 
 */

@SuppressWarnings("serial")
public class CacheException extends Exception {

	public CacheException() {
	}

	public CacheException(String message) {
		super(message);
	}

	public CacheException(Throwable cause) {
		super(cause);
	}

	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}

	public CacheException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
