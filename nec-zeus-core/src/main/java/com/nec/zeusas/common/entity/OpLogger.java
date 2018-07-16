package com.nec.zeusas.common.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.core.entity.IEntity;
import com.nec.zeusas.lang.IJSON;

@Entity(name = "core_bizlog")
public class OpLogger implements IEntity, IJSON {
	/** serialVersionUID */
	private static final long serialVersionUID = 4725660381334623327L;

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "ID", nullable = false)
	private Long id;
	@Column(name = "OPERATOR")
	private String operator;
	@Column(name = "BTYPE")
	protected String btype;
	@Column(name = "NAME")
	private String name;
	@Column(name = "SUMMARY")
	private String summary;
	@Column(name = "FROMADDR")
	private String fromAddr;
	@Column(name = "SIGNATURE")
	private String signature;
	@Column(name = "LASTUPDATE")
	private long lastUpdate;

	public OpLogger() {
		this.operator = "SYS";
		this.name = "SYS";
		this.lastUpdate = System.currentTimeMillis();
	}

	public OpLogger(String operator, String btype, String name, String summary) {
		this.operator = operator;
		this.btype = btype;
		this.name = name;
		this.summary = summary;
		this.fromAddr = "local";
		this.lastUpdate = System.currentTimeMillis();
	}

	public OpLogger addOperator(String s) {
		this.operator = s;
		return this;
	}

	public OpLogger addName(String s) {
		this.name = s;
		return this;
	}

	public OpLogger addBtype(String s) {
		this.btype = s;
		return this;
	}

	public OpLogger addSummary(String s) {
		this.summary = s;
		return this;
	}

	public OpLogger addSignature(String s) {
		this.signature = s;
		return this;
	}

	public String getSignature() {
		return this.signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getFromAddr() {
		return this.fromAddr;
	}

	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getBtype() {
		return btype;
	}

	public void setBtype(String btype) {
		this.btype = btype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate == null ? 0 : lastUpdate;
	}

	@Override
	public int hashCode() {
		return this.id == null ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof OpLogger)) {
			return false;
		}
		OpLogger a = (OpLogger) o;
		return Objects.equals(id, a.id)//
				&& Objects.equals(operator, a.operator)//
				&& Objects.equals(name, a.name);
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	@Override
	public String toJSON() {
		return null;
	}
}
