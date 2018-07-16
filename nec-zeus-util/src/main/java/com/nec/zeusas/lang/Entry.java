package com.nec.zeusas.lang;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;

public class Entry<K, V> implements Serializable {
	
	/** serialVersionUID */
	private static final long serialVersionUID = -7872168596805729520L;
	
	private K key;
	private V value;

	public Entry() {
	}

	public Entry(K k, V v) {
		this.key = k;
		this.value = v;
	}

	public K key() {
		return key;
	}

	public V value() {
		return value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return key==null?0:key.hashCode();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof Entry)) {
			return false;
		}

		return Objects.equal(this.key, ((Entry) obj).key) //
				&& Objects.equal(this.value, ((Entry) obj).value);
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
