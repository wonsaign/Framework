package com.nec.zeusas.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.core.entity.IEntity;

@Entity
@Table(name = "core_dict")
public class Dictionary implements IEntity, Comparable<Dictionary> {
	/** serialVersionUID */
	private static final long serialVersionUID = 4625437606026498347L;

	@Id
	@Column(name = "DID", nullable = false)
	protected String did;
	@Column(name = "HARDCODE", nullable = false)
	protected String hardCode;
	@Column(name = "type")
	protected String type;
	@Column(name = "PID")
	protected String pid;
	@Column(name = "NAME")
	protected String name;
	@Column(name = "VALUE")
	protected String value;
	@Column(name = "SUBJECT")
	protected String subject;
	@Column(name = "SUMMARY")
	protected String summary;
	@Column(name = "URL")
	protected String url;
	@Column(name = "ACTIVE")
	protected Boolean active;
	@Column(name = "STATUS")
	protected Integer status;
	@Column(name = "SEQID")
	protected Integer seqid;
	@Column(name = "LASTUPDATE")
	protected Long lastUpdate;

	@Transient
	private transient final List<Dictionary> children = new ArrayList<>();

	public Dictionary() {
		this.active = Boolean.TRUE;
		this.name = "";
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		if (did != null) {
			this.did = did.intern();
		}
	}

	public String getHardCode() {
		return hardCode;
	}

	public void setHardCode(String hardCode) {
		this.hardCode = hardCode == null ? null : hardCode.intern();
	}

	public String getType() {
		return type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSeqid() {
		return seqid;
	}

	public void setSeqid(Integer seqid) {
		this.seqid = seqid;
	}

	public void setType(String type) {
		if (type != null) {
			this.type = type.intern();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null) {
			this.name = name.intern();
		}
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url != null) {
			this.url = url.intern();
		}
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		if (active != null) {
			this.active = active;
		}
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@java.beans.Transient
	public boolean isRoot() {
		return this.pid == null //
				|| this.pid.length() == 0 //
				|| "root".equalsIgnoreCase(pid);
	}

	@java.beans.Transient
	public List<Dictionary> getChildren() {
		return children;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		if (pid != null) {
			this.pid = pid.intern();
		}
	}

	public void addChild(Dictionary dict) {
		if (dict != null) {
			this.children.add(dict);
		}
	}

	@java.beans.Transient
	public void setChildren(List<Dictionary> children) {
		if (children != null && !children.isEmpty()) {
			this.children.clear();
			this.children.addAll(children);
		}
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	@Override
	public int hashCode() {
		return did == null ? 0 : did.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Dictionary)) {
			return false;
		}
		Dictionary a = (Dictionary) o;
		return Objects.equals(did, a.did)//
				&& Objects.equals(type, a.type) //
				&& Objects.equals(hardCode, a.hardCode) //
				&& Objects.equals(name, a.name)//
				&& Objects.equals(this.pid, a.pid);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public int compareTo(Dictionary o) {
		return this.getHardCode().compareTo(o.getHardCode());
	}
}
