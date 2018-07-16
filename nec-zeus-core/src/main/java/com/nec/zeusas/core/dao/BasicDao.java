package com.nec.zeusas.core.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;

import com.nec.zeusas.core.entity.IEntity;

public abstract class BasicDao <T extends IEntity, PK extends Serializable>{

	/** 数据源DataSource */
	protected DataSource dataSource;
	/** 数据库表对象的实体 */
	protected Class<T> entityClass;
	/** 主键字段名 */
	protected String pkFieldName;
	protected String pkPropertyName;
	/** 表名 */
	protected String tableName;
	
	public BasicDao() {
		entityClass = getGenericClass(getClass(), 0);
		classPkFieldName();
		tableName = getTableName(entityClass);
	}
	
	public BasicDao(Class<T> entityClass) {
		this.entityClass = entityClass;
		this.tableName = getTableName(entityClass);
		classPkFieldName();
	}
	
	public BasicDao(Class<T> entityClass, String pkFieldName) {
		this(entityClass);
	}

	protected String getTableName(Class<?> entityClass) {
		// 如果对象为空，换回空
		if (entityClass == null) {
			return null;
		}
		// 优先看表的注解，查看name=""
		Table tb = entityClass.getAnnotation(Table.class);
		String tbname = (tb == null) ? null : tb.name();
		if (tbname != null) {
			return tbname;
		}
		// 在查看Entity注解，查看name=""
		Entity e = entityClass.getAnnotation(Entity.class);
		return (e == null) ? null : e.name();
	}
	
	protected String classPkFieldName() {
		if (entityClass == null) {
			return null;
		}
		Field[] declaredFields = entityClass.getDeclaredFields();
		for (Field f : declaredFields) {
			f.setAccessible(true);
			Id id = f.getAnnotation(Id.class);
			if (id != null) {
				this.pkPropertyName = f.getName();
				this.pkFieldName = f.getName();
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T> Class<T> getGenericClass(Class<?> clazz, int index) {
		Type genType = clazz.getGenericSuperclass();
		// 如果未定义类型
		if (genType != null && genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			if ((params != null) && (params.length >= (index - 1))) {
				return (Class<T>) params[index];
			}
		}
		return null;
	}
	
	protected DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public Class<T> getEntityClass() {
		return entityClass;
	}
	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	public String getPkFieldName() {
		return pkFieldName;
	}
	public void setPkFieldName(String pkFieldName) {
		this.pkFieldName = pkFieldName;
	}
	public String getPkPropertyName() {
		return pkPropertyName;
	}
	public void setPkPropertyName(String pkPropertyName) {
		this.pkPropertyName = pkPropertyName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	
}
