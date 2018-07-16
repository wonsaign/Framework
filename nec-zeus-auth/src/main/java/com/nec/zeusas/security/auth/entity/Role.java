package com.nec.zeusas.security.auth.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.core.entity.IEntity;

@Entity
@Table(name = "core_role")
public class Role implements IEntity {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "rid")
	protected String rid;
	@Column(name = "COMMONNAME")
	protected String commonName;
	@Column(name="type")
	protected Integer type;
	@Column(name = "SUMMARY")
	protected String summary;
	@Column(name = "STATUS")
	protected String status;
	@Column(name = "LASTUPDATE")
	protected Long lastUpdate;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid.intern();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName.intern();
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
		this.lastUpdate = lastUpdate;
	}

	@Override
	public int hashCode() {
		return rid == null ? 0 : rid.hashCode();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object anObj) {
		if (this == anObj)
			return true;
		if (anObj == null || !(anObj instanceof Role)) {
			return false;
		}
		Role other = (Role) anObj;
		return Objects.equals(rid, other.rid)//
				&& Objects.equals(commonName, other.commonName);
	}
	
	@Override
	public String toString(){
		return JSON.toJSONString(this);
	}
}
