package com.nec.zeusas.data;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public enum DataType {
	string("string", Types.VARCHAR), //
	integer("integer", Types.INTEGER), //
	bigint("bigint", Types.BIGINT), //
	bool("bool", Types.BOOLEAN), //
	date("date", Types.DATE), //
	decimal("decimal", Types.DECIMAL), //
	array("array", Types.ARRAY), //
	blob("blob", Types.BLOB), //
	clob("text", Types.CLOB), //
	text("clob", Types.NCLOB), //
	varbinary("varbinary", Types.VARBINARY), //
	
	unkonwn("unkonwn", -1);

	public final String name;
	public final int value;

	private DataType(String nm, int val) {
		this.name = nm;
		this.value = val;
	}

	@Override
	public String toString() {
		return this.name;
	}

	private final static Map<String, DataType> values = new HashMap<String, DataType>();
	static {
		for (DataType t : DataType.values()) {
			values.put(t.name, t);
		}
	}

	public static DataType toValue(String s) {
		if (s == null) {
			return DataType.unkonwn;
		}
		DataType t = values.get(s);
		return (t == null) ? DataType.unkonwn : t;
	}
}
