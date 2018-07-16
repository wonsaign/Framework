package com.nec.zeusas.common.dao;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;

import com.nec.zeusas.common.hibernate.HibernateBasicDao;
import com.nec.zeusas.core.entity.IEntity;

/**
 * 核心基本数据存储处理类<p>
 * 基本类实现存储中的数据源的绑定。定义：
 * <li> sessionFactory
 * <li> DataSource 
 * 基本系统指定的特定数据源，基础部分，是否可以采用微服务处理并提供。
 * 
 */
public abstract class HibernateCoreBasicDao<T extends IEntity, PK extends Serializable> extends HibernateBasicDao<T, PK>{
	
	public HibernateCoreBasicDao(){
		super();
	}
	
	public HibernateCoreBasicDao(Class<T> entityClass, String pkFieldName) {
		super(entityClass,pkFieldName);
	}
	
	/**
	 * 使用系统默认的sessionFactory设定
	 */
	@Override
 	@Resource(name="sessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.sessionFactory = sessionFactory;
	}
	
	/**
	 * 设定为系统默认系统数据源。
	 */
	@Override
	@Resource(name="dataSource")
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public DataSource getDataSource() {
		return dataSource;
	}
}
