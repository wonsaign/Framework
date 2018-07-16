package com.nec.zeusas.cache.service.impl;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.nec.zeusas.lang.IJSON;


/**
 * Cache locker information. 
 *
 */
public final class CacheLocker implements IJSON, Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public final static int DEFAULT_EXPIRED_TIME = 30000;
	
	/** the machine id*/
	private String systemId;
	/** the id of the locked thread */
	private long threadId;
	/** create time*/
	private long createTime;
	/** expired time */
	private long expiredTime;

	public CacheLocker() {
		this.expiredTime = DEFAULT_EXPIRED_TIME;
	}

	public CacheLocker(String systemId) {
		this();
		this.systemId = systemId;
		this.threadId = Thread.currentThread().getId();
		this.createTime = System.currentTimeMillis();
	}

	public CacheLocker(String systemId, long tm) {
		this(systemId);
		setExpiredTime(tm);
	}

	public static CacheLocker of(String systemId, long tm) {
		return new CacheLocker(systemId, tm);
	}
	
	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long time) {
		expiredTime = time;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId =  threadId;
	}

	@Override
	public int hashCode() {
		return (this.systemId + ":" + this.threadId).hashCode();
	}

	@Override
	public boolean equals(Object b) {
		if (this == b)
			return true;
		if (b == null || !(b instanceof CacheLocker)) {
			return false;
		}
		CacheLocker c = (CacheLocker) b;
		return Objects.equal(this.systemId, c.systemId) //
				&& this.threadId == c.threadId //
				&& this.createTime == c.createTime;
	}

	public String id() {
		return this.systemId + ":" + this.threadId;
	}

	/**
	 * 是否过期
	 * @return
	 */
	public boolean hasExpired() {
		long deltaT = System.currentTimeMillis() - this.expiredTime //
				- this.createTime;
		return deltaT > 0;
	}

	/**
	 * Which machine and thread has hold the locker.
	 * 
	 * @param systemId
	 * @return
	 */
	public boolean hasHold(String systemId) {
		return this.threadId == (int)Thread.currentThread().getId() //
				&& this.systemId.equals(systemId);
	}
	
	public String toString() {
		return this.toJSON();
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}
