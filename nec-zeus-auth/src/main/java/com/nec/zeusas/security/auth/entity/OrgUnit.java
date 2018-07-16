package com.nec.zeusas.security.auth.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.core.entity.IEntity;

@Entity
@Table(name = "core_orgunit")
public class OrgUnit implements IEntity, Comparable<OrgUnit> {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ORGID")
	protected Integer orgId;
	@Column(name = "PID")
	protected Integer pid;
	@Column(name = "ORGCODE")
	protected String orgCode;
	@Column(name = "COMMONNAME")
	protected String commonName;
	// 联系人
	@Column(name = "CONTACT")
	protected String contact;
	// 移动电话
	@Column(name = "MOBILE")
	protected String mobile;
	//
	@Column(name = "PHONE")
	protected String phone;
	@Column(name = "COUNTRY")
	protected String country;
	@Column(name = "PROVINCE")
	protected String province;
	@Column(name = "CITY")
	protected String city;
	@Column(name = "AREACOUNTY")
	protected String areaCounty;
	@Column(name = "ADDRESS")
	protected String address;
	@Column(name = "POSTCODE")
	protected String postCode;
	@Column(name = "EMAIL")
	protected String email;
	@Column(name = "LEVEL")
	protected Integer level;
	@Column(name = "STATUS")
	protected Integer status;
	@Column(name = "LASTUPDATE")
	protected Long lastUpdate;
	@Transient
	private String path;
	@Transient
	private Integer validFlag;
	@Transient
	private Integer testType;
	@Transient
	private String Type;

	/**
	 * 组织机构的角色集合
	 */
	@Column(name = "ROLESET")
	@Type(type = "com.nec.zeusas.core.entity.StringSetType")
	private Set<String> roleset;

	@Transient
	transient List<OrgUnit> children;

	@java.beans.Transient
	public List<OrgUnit> getChildren() {
		return children;
	}

	public void setChildren(List<OrgUnit> children) {
		this.children = children;
	}

	public Set<String> getRoleset() {
		return roleset;
	}

	public void setRoleset(Set<String> roleset) {
		this.roleset = roleset;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode == null ? null : orgCode.intern();
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName.intern();
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country == null ? null : country.intern();
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city == null ? null : city.intern();
	}

	public String getAreaCounty() {
		return areaCounty;
	}

	public void setAreaCounty(String areaCounty) {
		this.areaCounty = areaCounty == null ? null : areaCounty.intern();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode == null ? null : postCode.intern();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(Integer validFlag) {
		this.validFlag = validFlag;
	}

	public Integer getTestType() {
		return testType;
	}

	public void setTestType(Integer testType) {
		this.testType = testType;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	@Override
	public int compareTo(OrgUnit o) {
		String id = (pid == null) ? "0" : pid + ":" + getOrgCode();
		String o_id = (o.pid == null) ? "0" : o.pid + ":" + o.getOrgCode();

		return id.compareTo(o_id);
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
