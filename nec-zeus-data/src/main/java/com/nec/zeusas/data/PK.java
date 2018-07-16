package com.nec.zeusas.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 定义主键类，假定数据库所有表中，最多只有2个主键的限制.
 * 
 * @author admin
 *
 */
@SuppressWarnings("serial")
public final class PK implements Comparable<PK>, Serializable {
	private Object[] key;

	public PK() {
		key = new Object[1];
	}

	public PK(Object[] args) {
		key = new Object[args.length > 4 ? 4 : args.length];
		System.arraycopy(args, 0, key, 0, key.length);
	}

	public PK(Object a) {
		key = new Object[1];
		key[0] = a == null ? "" : a;
	}

	public PK(Object a, Object b) {
		key = new Object[2];
		key[0] = a == null ? "" : a;
		key[1] = b == null ? "" : b;
	}

	public PK(Object a, Object b, Object c) {
		key = new Object[3];
		key[0] = a == null ? "" : a;
		key[1] = b == null ? "" : b;
		key[2] = c == null ? "" : c;
	}

	public PK(Object a, Object b, Object c,Object d) {
		key = new Object[4];
		key[0] = a == null ? "" : a;
		key[1] = b == null ? "" : b;
		key[2] = c == null ? "" : c;
		key[3] = d == null ? "" : d;
	}
	
	public Object[] getKey() {
		return key;
	}

	public Object getKey(int i) {
		return key[i];
	}
	
	public void setKey(Object[] key) {
		if (key != null && key.length > 0) {
			this.key = new Object[key.length];
			System.arraycopy(key, 0, this.key, 0, key.length);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null //
				|| !obj.getClass().equals(PK.class)) {
			return false;
		}
		return this.toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		if (key.length == 1) {
			return key[0].toString().hashCode();
		}
		return toString().hashCode();
	}

	public final String id() {
		String v = toString();
		StringBuilder b = new StringBuilder(v.length());
		for (int i = 0; i < v.length(); i++) {
			char c = v.charAt(i);
			if (c == '|' //
					|| c == ':' //
					|| c == '\\' //
					|| c == '/' //
					|| c == ',' //
					|| c == ';') {
				b.append('_');
			} else {
				b.append(c);
			}
		}
		return b.toString();
	}

	/**
	 * XXX: WARN 此方法已经被使用，不能随意修正！！
	 */
	@Override
	public String toString() {
		if (key == null) {
			return "~";
		}
		StringBuilder v = new StringBuilder(64);
		switch (key.length) {
		case 1:
			v.append(key[0]);
			break;
		case 2:
			v.append(key[0]).append('~')//
					.append(key[1]).toString();
			break;
		case 3:
			v.append(key[0]).append('~')//
			.append(key[1]).append('~')//
			.append(key[2])//
			.toString();
			break;
		default:
			v.append(key[0]).append('~')//
			.append(key[1]).append('~')//
			.append(key[2]).append('~')//
			.append(key[3])//
			.toString();
		}
		return v.toString();
	}

	@Override
	public int compareTo(PK aPk) {
		int eq = 0;
		if (aPk == null || key.length != aPk.key.length) {
			throw new IllegalArgumentException("不能比较不同类型的PK");
		}
		for (int i = 0; i < key.length; i++) {
			Object a = key[i];
			Object b = aPk.key[i];
			if (a == null) {
				a = "";
			}
			if (aPk.key[i] == null) {
				b = "";
			}
			if (!a.getClass().equals(b.getClass())) {
				throw new IllegalArgumentException("不能比较不同类型的PK");
			}
			if (a instanceof String) {
				eq = ((String) a).compareTo((String) b);
			} else if (a instanceof Date) {
				eq = ((Date) a).compareTo((Date) b);
			} else if (a instanceof Long) {
				eq = Long.compare((Long) a, (Long) b);
			} else if (a instanceof Long) {
				eq = Integer.compare((Integer) a, (Integer) b);
			} else if (a instanceof Double) {
				eq = Double.compare((Double) a, (Double) b);
			} else if (a instanceof Float) {
				eq = Float.compare((Float) a, (Float) b);
			} else if (a instanceof Short) {
				eq = Short.compare((Short) a, (Short) b);
			} else if (a instanceof Byte) {
				eq = Byte.compare((Byte) a, (Byte) b);
			} else if (a instanceof BigInteger) {
				eq = ((BigInteger) a).compareTo((BigInteger) b);
			} else if (a instanceof BigDecimal) {
				eq = ((BigDecimal) a).compareTo((BigDecimal) b);
			} else {
				eq = a.toString().compareTo(b.toString());
			}
			if (eq != 0) {
				return eq;
			}
		}
		return 0;
	}
}
