package com.nec.zeusas.cache.meta;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.nec.zeusas.bean.QBeanUtil;
import com.nec.zeusas.lang.IJSON;
import com.nec.zeusas.util.Dom4jUtils;
import com.nec.zeusas.util.ID;
import com.nec.zeusas.util.QString;

/**
 * 定义 Cache 元数据的信息<p>
 * 元数据使用JSON或XML定义。<p>
 * <pre>
 * &lt;cache-meta>
 * &lt;!-- if id is NULL, setting entityClass name as default -->
 * 	&lt;meta id="">
 * 		&lt;description>&lt;/description>
 * 		&lt;entityClass>bean.Student&lt;/entityClass>
 * 		&lt;keyClass>bean.Student&lt;/keyClass>
 * 		&lt;!-- UNIT: s -->
 * 		&lt;lockMaxTime>3&lt;/lockMaxTime>
 * 	&lt;/meta>
 * &lt;/cache-meta>
 * </pre>
 * 
 * @author zhensx
 * @date 2017年11月25日 下午3:33:49
 *
 */
public class CacheMeta implements IJSON {
	final static Logger logger = LoggerFactory.getLogger(CacheMeta.class);
	/** DB ID */
	private String id;
	/** 实体类 */
	private Class<?> entityClass;
	/** 实体KEY类 */
	private Class<?> keyClass;
	/** 元数据描述信息 */
	private String description;
	/** 最后更新时间 */
	private long lastUpdate;
	/** Create Cache Meta time of millisecond */
	private long createTime;
	/** 锁定最长时间 */
	private int lockMaxTime;

	public CacheMeta() {
		this.createTime= System.currentTimeMillis();
	}

	public CacheMeta(Element e) {
		this(Dom4jUtils.getAttr(e, "id"), //
				Dom4jUtils.getText(e.element("description")), //
				Dom4jUtils.getText(e.element("lockMaxTime")), //
				Dom4jUtils.getText(e.element("entityClass")), //
				Dom4jUtils.getText(e.element("keyClass")));
	}

	public CacheMeta(String id, Class<?> type) {
		this();
		this.id = id;
		this.description = "";
		this.lockMaxTime = 30 * 1000;
		this.entityClass = type;
		this.keyClass = String.class;
	}

	public CacheMeta(Class<?> type) {
		this(type.getName(), type);
	}

	public CacheMeta(String id, String desc, String lockMaxTime,
			String classNm, String keyClass) {
		this();
		this.id = id == null ? null : (id.endsWith(".class") ? id.substring(0,
				id.length() - 6) : id);
		this.description = desc;
		this.lockMaxTime = QString.toInt(lockMaxTime, 30*1000);
		this.entityClass = QBeanUtil.forName(classNm);
		this.keyClass = QBeanUtil.forName(keyClass);

		if (this.entityClass == null && id != null) {
			this.entityClass = QBeanUtil.forName(id);
		}

		if (this.lockMaxTime < 300) {
			this.lockMaxTime *= 1000;
		}
		
		if (Strings.isNullOrEmpty(this.id) && entityClass != null) {
			this.id = entityClass.getName();
		}
		if (this.entityClass == null) {
			this.entityClass = String.class;
		}
		if (this.keyClass == null) {
			this.keyClass = String.class;
		}
		if (Strings.isNullOrEmpty(this.id)) {
			throw new IllegalArgumentException("Cache Meta ID 不能为空！");
		}
		this.id = ID.of(this.id);
	}

	public CacheMeta(JSONObject json) {
		this(json.getString("id"), //
				json.getString("description"), //
				json.getString("lockMaxTime"), //
				json.getString("entityClass"), //
				json.getString("keyClass"));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLockMaxTime(int lockMaxTime) {
		this.lockMaxTime = lockMaxTime;
	}

	public int getLockMaxTime() {
		return lockMaxTime;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String id() {
		return this.id;
	}

	public byte[] idBytes() {
		return ID.toBytes(this.id);
	}
	
	public Class<?> getKeyClass() {
		return keyClass;
	}

	public void setKeyClass(Class<?> keyClass) {
		this.keyClass = keyClass;
	}

	public final long getCreateTime() {
		return createTime;
	}
}
