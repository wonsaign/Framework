package com.nec.zeusas.util;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

/**
 * ID 静态池处理
 */
public final class ID {
	final static Charset UTF8C = Charset.forName("UTF-8");
	public final String value;
	
	private ID(String v) {
		this.value = (v == null) ? "" : v.intern();
	}
	
	public static String of(String s) {
		return s == null ? null : s.intern();
	}

	public static ID asID(Object... ids) {
		String s = id(ids);
		return new ID(s);
	}
	
	public String id(){return value;}
	
	public static String id(Object... s) {
		StringBuilder b = new StringBuilder();
		for (Object x : s) {
			if (x instanceof Class) {
				b.append(((Class<?>) x).getName());
			} else if (x instanceof Date) {
				b.append(((Date) x).getTime());
			} else {
				b.append(x);
			}
			b.append(':');
		}
		if (b.length() > 0) {
			b.setLength(b.length() - 1);
		}
		return b.toString();
	}

	public static String asUUID() {
		UUID id = UUID.randomUUID();
		return id.toString();
	}

	public static byte[] toBytes(Object obj) {
		if (obj == null) {
			return null;
		}
		return QString.toBytes(String.valueOf(obj));
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof ID)) {
			return false;
		}
		return this.value.equals((ID) obj);
	}
}
