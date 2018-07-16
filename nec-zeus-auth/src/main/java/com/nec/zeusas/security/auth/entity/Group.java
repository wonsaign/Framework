package com.nec.zeusas.security.auth.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.core.entity.IEntity;

@Entity
@Table(name = "core_group")
public class Group implements IEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 728982365276620875L;
	@Id
	@Column(name = "GROUPID", nullable = false)
	protected Integer groupId;
	@Column(name = "name")
	protected String name;
	@Column(name = "description")
	protected String description;
	@Column(name = "roles")
	@Type(type = "com.nec.zeusas.core.entity.StringSetType")
	protected Set<String> roles;
	@Column(name = "LASTUPDATE")
	protected Long lastUpdate;

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name==null?null:name.intern();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Long getLastUpdate() {
		return lastUpdate == null ? new Long(0) : lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void add(Role r) {
		if (this.roles == null) {
			roles = new LinkedHashSet<String>();
		}
		if (r != null) {
			roles.add(r.getRid());
		}
	}

	public void remove(Role r) {
		if (this.roles == null) {
			return;
		}
		if (r != null) {
			roles.remove(r.getRid());
		}
	}
	
	@Override
	public String toString(){
		return JSON.toJSONString(this);
	}
}
