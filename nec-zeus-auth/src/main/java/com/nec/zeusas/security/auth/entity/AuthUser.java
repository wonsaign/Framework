package com.nec.zeusas.security.auth.entity;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.bean.BeanDup;
import com.nec.zeusas.core.entity.IEntity;

/**
 * 授权认证用户实体类 
 *
 */
@Entity
@Table(name = "core_authuser")
public class AuthUser implements Cloneable, IEntity {
	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** 用户ID */
	@Id
	@Column(name = "UID", nullable = false)
	protected String uid;
	/** Login Name */
	@Column(name = "LOGINNAME", nullable = false, unique = true)
	protected String loginName;
	@Column(name = "COMMONNAME")
	protected String commonName;
	@Column(name = "PASSWORD")
	protected String password;
	@Column(name = "ORGUNIT")
	protected Integer orgUnit;
	@Column(name = "GROUPID")
	protected Integer groupId;
	@Column(name = "ROLES")
	@Type(type = "com.nec.zeusas.core.entity.StringSetType")
	private Set<String> roles = new LinkedHashSet<>();
	@Column(name = "GRANTSTART")
	private Long grantStart;
	@Column(name = "GRANTEND")
	private Long grantEnd;
	@Column(name = "CREATETIME")
	private Long createTime;
	@Column(name = "EXPIRETIME")
	private Long expireTime;
	@Column(name = "MACADDR")
	private String macAddr;
	@Column(name = "STATUS")
	private Integer status;
	/**
	 * 用户类型 表示属于组织树、客户树
	 */
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "LASTUPDATE")
	private Long lastUpdate;

	@Transient
	private transient int failCount;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid == null ? null : uid.intern();
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getGrantStart() {
		return grantStart;
	}

	public void setGrantStart(Long grantStart) {
		this.grantStart = grantStart;
	}

	public Long getGrantEnd() {
		return grantEnd;
	}

	public void setGrantEnd(Long grantEnd) {
		this.grantEnd = grantEnd;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName == null ? null : commonName.intern();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(Integer orgUnit) {
		this.orgUnit = orgUnit;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	@java.beans.Transient
	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		if (roles != null) {
			this.roles = roles;
		}
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public int hashCode() {
		return uid == null ? 0 : uid.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof AuthUser)) {
			return false;
		}
		AuthUser u = (AuthUser) obj;
		return Objects.equals(this.uid, u.uid) //
				&& Objects.equals(this.roles, u.roles) //
				&& Objects.equals(this.status, u.status) //
				&& Objects.equals(this.password, u.password) //
				&& Objects.equals(this.orgUnit, u.orgUnit) //
				&& Objects.equals(this.loginName, u.loginName) //
				&& Objects.equals(this.groupId, u.groupId) //
				&& Objects.equals(this.commonName, u.commonName);
	}
	
	public String toString(){
		return JSON.toJSONString(this);
	}
	
	public AuthUser clone() {
		try {
			return (AuthUser) super.clone();
		} catch (Exception e) {

		}
		return (AuthUser) BeanDup.dup(this, new AuthUser());
	}
}
