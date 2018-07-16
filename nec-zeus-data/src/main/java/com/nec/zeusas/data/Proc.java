package com.nec.zeusas.data;

import static com.nec.zeusas.data.Meta.TAG_FIELD;
import static com.nec.zeusas.data.Meta.TAG_ID;
import static com.nec.zeusas.data.Meta.TAG_INSERT;
import static com.nec.zeusas.data.Meta.TAG_PROC;
import static com.nec.zeusas.data.Meta.TAG_PROPERTY;
import static com.nec.zeusas.data.Meta.TAG_SEQUENCE;
import static com.nec.zeusas.data.Meta.TAG_STMT;
import static com.nec.zeusas.data.Meta.TAG_TYPE;
import static com.nec.zeusas.data.Meta.TAG_UPDATE;
import static com.nec.zeusas.data.Meta.TAG_UPDATE_BYFIELD;
import static com.nec.zeusas.data.Meta.TAG_UPDATE_BYPROPERTY;
import static com.nec.zeusas.data.Meta.TYPE_INSERT;
import static com.nec.zeusas.data.Meta.TYPE_PROC;
import static com.nec.zeusas.data.Meta.TYPE_QUERY;
import static com.nec.zeusas.data.Meta.TYPE_UPDATE;
import static com.nec.zeusas.data.Meta.TYPE_UPDATE_BYFIELD;
import static com.nec.zeusas.data.Meta.TYPE_UPDATE_BYPROPERTY;
import static com.nec.zeusas.data.Meta.shifts;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nec.zeusas.util.Dom4jUtils;
import com.nec.zeusas.util.QString;

/**
 * 定义处理过程中的处理SQL描述
 * 
 */
public class Proc implements Comparable<Proc> {
	private static Logger log = LoggerFactory.getLogger(Proc.class);

	// the id number of a proc
	final String id;
	// the type of a PROC
	final int type;
	// SQL statement
	final String stmt;
	final int sequence;
	String field[];
	String property[];

	public Proc(String id, String stmt) {
		this.id = id;
		this.stmt = DataDDL.preprocess(stmt);
		this.type = TYPE_UPDATE;
		this.sequence = 0;
	}

	Proc(Element e) {
		id = Dom4jUtils.getAttr(e, TAG_ID);
		switch (Dom4jUtils.getText(e.element(TAG_TYPE))) {
		case TAG_UPDATE:
			type = TYPE_UPDATE;
			break;
		case TAG_INSERT:
			type = TYPE_INSERT;
			break;
		case TAG_PROC:
			type = TYPE_PROC;
			break;
		case TAG_UPDATE_BYFIELD:
			type = TYPE_UPDATE_BYFIELD;
			break;
		case TAG_UPDATE_BYPROPERTY:
			type = TYPE_UPDATE_BYPROPERTY;
			break;
		default:
			type = TYPE_QUERY;
		}
		stmt = DataDDL.preprocess(Dom4jUtils.getText(e.element(TAG_STMT)));
		sequence = QString.toInt(Dom4jUtils.getText(e.element(TAG_SEQUENCE)), 99);
		field = shifts(DataDDL.toArrayUpper(e.elementText(TAG_FIELD)));
		property = shifts(DataDDL.toArrayUpper(e.elementText(TAG_PROPERTY)));
		if (id == null || stmt == null) {
			throw new IllegalArgumentException("XXX: 处理结点必須有ID，且语句不能为空。");
		}
	}

	public String getStatement() {
		return stmt;
	}

	public boolean execUpdate(Connection conn, Object... args) throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug("执行：{},参数：{}", stmt, args);
		}
		return QueryHelper.execUpdate(conn, stmt, args);
	}

	public String id() {
		return id;
	}

	public int type() {
		return type;
	}

	public String getStmt() {
		return stmt;
	}

	public int getSequence() {
		return sequence;
	}

	@Override
	public int compareTo(Proc pe) {
		return id.compareTo(pe.id);
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Proc))
			return false;
		Proc a = (Proc) o;
		return Objects.equals(this.id, a.id)//
				&& Objects.equals(this.stmt, a.stmt)//
				&& Objects.equals(this.type, a.type) //
				&& Objects.equals(this.sequence, a.sequence);
	}

	@Override
	public String toString() {
		return stmt;
	}
}