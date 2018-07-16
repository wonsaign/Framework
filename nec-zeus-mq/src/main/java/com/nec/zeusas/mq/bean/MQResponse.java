package com.nec.zeusas.mq.bean;

import java.io.Serializable;

import com.nec.zeusas.lang.IJSON;

/**
 * 统一返回值,可描述失败细节
 */
public class MQResponse implements IJSON, Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private int status;
	private String message;
	private long lastUpdate;

	public final static MQResponse OK = new MQResponse(0, "OK");
	public final static MQResponse FAILURE = new MQResponse(0, "Unknown error.");

	public MQResponse() {
		this(0, "OK");
	}

	public MQResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.lastUpdate = System.currentTimeMillis();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
