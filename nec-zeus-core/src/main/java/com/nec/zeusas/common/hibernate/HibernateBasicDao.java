package com.nec.zeusas.common.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.nec.zeusas.core.dao.BasicDao;
import com.nec.zeusas.core.entity.IEntity;
import com.nec.zeusas.data.PK;
import com.nec.zeusas.data.QueryHelper;
import com.nec.zeusas.exception.DaoException;
import com.nec.zeusas.util.TypeConverter;

/**
 * BasicDao 基本数据存取类
 * <p>
 * 本类处理常用的数据库操作基类实现。
 */
@SuppressWarnings("unchecked")
public abstract class HibernateBasicDao <T extends IEntity, PK extends Serializable> extends BasicDao<T,PK> {
	final static Logger logger = LoggerFactory.getLogger(HibernateBasicDao.class);
	/** Hibernat SessionFactory */
	protected SessionFactory sessionFactory;


	/** Inject setting sessionFactory */
	public abstract void setSessionFactory(SessionFactory sessionFactory);

	/** Inject setting dataSource */
	public abstract void setDataSource(DataSource dataSource);


	public HibernateBasicDao() {
		super();
	}
	
	public HibernateBasicDao(Class<T> entityClass) {
		super(entityClass);
	}
	
	public HibernateBasicDao(Class<T> entityClass, String pkFieldName) {
		super(entityClass,pkFieldName);
	}

	public String getTableName() {
		return tableName;
	}

	public int delete(PK id) {
		StringBuilder hql = new StringBuilder(128);
		Query<T> query = null;

		hql.append("DELETE FROM ").append(entityClass.getName())//
				.append(" WHERE ")//
				.append(pkFieldName).append("= :ids");
		try {
			query = getCurrentSession().createQuery(hql.toString());
			query.setParameter("ids", id);
			return query.executeUpdate();
		} catch (Exception e) {
			logger.error("HQL={}, id={}", hql, id);
			throw new DaoException(e);
		}
	}

	public int delete(PK[] ids) {
		if (ids == null || ids.length == 0) {
			return 0;
		}

		if (ids.length == 1) {
			return delete(ids[0]);
		}
		StringBuilder hql = new StringBuilder(64);

		hql.append("DELETE FROM ").append(entityClass.getName())//
				.append(" WHERE ").append(pkFieldName)//
				.append(" IN (:ids)");
		Query<T> query;
		try {
			query = getCurrentSession().createQuery(hql.toString());
			query.setParameterList("ids", ids);
			logger.debug("SQL{}", hql);
			return query.executeUpdate();
		} catch (Exception e) {
			logger.error("HQL={}", hql);
			throw new DaoException(e);
		}
	}

	public List<T> find(PK[] ids) {
		if (ids == null || ids.length == 0) {
			return null;
		}
		StringBuilder hql = new StringBuilder(128);
		hql.append("FROM ").append(entityClass.getName()) //
				.append(" WHERE ") //
				.append(pkPropertyName).append(" IN (:ids)");
		Session sess = getCurrentSession();
		try {
			Query<T> query = sess.createQuery(hql.toString());

			query.setParameterList("ids", ids);
			return query.getResultList();
		} catch (Exception e) {
			logger.error("HQL={}", hql);
			throw new DaoException(e);
		}

	}

	List<T> findByName(String where, Map<String, Object> params) throws DaoException {
		StringBuilder hql = new StringBuilder(128);
		hql.append("FROM ") //
				.append(entityClass.getName()).append(' ');
		if (where.toUpperCase().contains("WHERE")) {
			hql.append(where);
		} else {
			hql.append(" WHERE ").append(where);
		}
		try {
			Session sess = getCurrentSession();
			Query<T> query = sess.createQuery(hql.toString());
			for (Map.Entry<String, Object> e : params.entrySet()) {
				String key = e.getKey();
				Object val = params.get(e.getKey());
				if (val == null) {
					query.setParameter(key, null);
				} else if (val.getClass().isArray()) {
					query.setParameterList(key, (Object[]) val);
				} else if (val instanceof Collection) {
					query.setParameterList(key, (Collection<?>) val);
				} else {
					query.setParameter(key, val);
				}
			}
			return query.getResultList();
		} catch (Exception e) {
			logger.error("HQL={} values={}", hql, JSON.toJSONString(params));
			throw new DaoException(e);
		}
	}

	/**
	 * HQL statement Execute
	 * 
	 * @param hql
	 *            HQL script
	 * @param params
	 *            parameters
	 * @return Update number.
	 * @throws DaoException
	 */
	public int execute(String hql, Object... params) throws DaoException {
		Session sess = getCurrentSession();
		try {
			Query<T> query = sess.createQuery(hql);
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
			return query.executeUpdate();
		} catch (Exception e) {
			logger.info("HQL={}", hql);
			throw new DaoException(e);
		}
	}

	public int executeNative(String sql, Object... params) throws DaoException {
		Session sess = getCurrentSession();
		try {
			NativeQuery<?> query = sess.createNativeQuery(sql);
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i + 1, params[i]);
			}
			return query.executeUpdate();
		} catch (Exception e) {
			logger.info("SQL:= {}", sql);
			throw new DaoException(e);
		}
	}

	public List<T> findAll() {
		String HQL = "FROM " + entityClass.getName();

		Session sess = getCurrentSession();
		Query<T> query = sess.createQuery(HQL.toString());
		query.setMaxResults(10000);
		return query.getResultList();
	}

	public T get(PK key) {
		return (T) getCurrentSession().get(entityClass, key);
	}

	public void save(T t) {
		getCurrentSession().save(t);
	}

	public void saveOrUpdate(Object t) {
		getCurrentSession().saveOrUpdate(t);
	}

	public T update(T t) {
		getCurrentSession().update(t);
		return t;
	}

	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}



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

	public int update(Map<String, Object> values, String cond, Object... args) throws DaoException {
		QueryHelper qh = new QueryHelper();
		qh.append("UPDATE ").append(tableName).append(" SET ");

		for (Map.Entry<String, Object> e : values.entrySet()) {
			qh.append(e.getKey()).append(" = ?,");
			qh.addParameter(e.getValue());
		}
		qh.setLength(-1);
		if (cond.toUpperCase().indexOf("WHERE") >= 0) {
			qh.append(" ").append(cond);
		} else {
			qh.append(" WHERE ").append(cond);
		}
		qh.addParameter(args);

		Session sess = this.getCurrentSession();
		try {
			NativeQuery<?> query = sess.createNativeQuery(qh.getScript());
			this.asmbleQueryParameters(query, qh);
			return query.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public int update(PK pk, Map<String, Object> values) throws DaoException {
		QueryHelper qh = new QueryHelper();
		qh.append("UPDATE ").append(tableName).append(" SET ");

		for (Map.Entry<String, Object> e : values.entrySet()) {
			qh.append(e.getKey()).append(" = ?,");
			qh.addParameter(e.getValue());
		}
		qh.setLength(-1);
		qh.append(" WHERE ").append(this.pkFieldName).append(" = ?");
		qh.addParameter(pk);
		Session sess = this.getCurrentSession();
		try {
			NativeQuery<?> query = sess.createNativeQuery(qh.getScript());
			this.asmbleQueryParameters(query, qh);
			return query.executeUpdate();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public List<T> find(String where, Object... args) throws DaoException {
		if (args.length == 1 && (args[0] instanceof Map)) {
			return findByName(where, (Map) args[0]);
		}
		Session sess = this.getCurrentSession();
		StringBuilder hql = new StringBuilder(128);
		hql.append("FROM ").append(entityClass.getName()).append(' ');
		if (where.toUpperCase().indexOf("WHERE") >= 0) {
			hql.append(where);
		} else {
			hql.append("WHERE ").append(where);
		}

		try {
			Query<T> query = sess.createQuery(hql.toString());
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i, args[i]);
			}
			return query.getResultList();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public List<T> getScrollData(int firstindex, //
			int maxresult, //
			String where, //
			String orderby, //
			Object... queryParams) throws DaoException {
		StringBuilder hql = new StringBuilder(256);
		hql.append("FROM ").append(this.entityClass.getName());
		if (where != null)
			hql.append(' ').append(where);
		if (orderby != null)
			hql.append(' ').append(orderby);
		Session sess = this.getCurrentSession();
		try {
			Query<T> query = sess.createQuery(hql.toString());
			for (int i = 0; i < queryParams.length; i++) {
				query.setParameter(i, queryParams[i]);
			}
			query.setFirstResult(firstindex);
			query.setMaxResults(maxresult);
			return query.getResultList();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public int count(String cond, Object... args) throws DaoException {
		StringBuilder hql = new StringBuilder(256);
		hql.append("SELECT COUNT(*) FROM ").append(this.entityClass.getName());
		if (Strings.isNullOrEmpty(cond)) {
			// 当条件为空时，不论后面情况，参数表均为0个元素！
			args = new Object[0];
		} else {
			String w = cond.toUpperCase().contains("WHERE") ? " " : " WHERE ";
			hql.append(w).append(cond);
		}
		try {
			Query<?> query = getCurrentSession().createQuery(hql.toString());
			int idx = 0;
			for (Object arg : args) {
				query.setParameter(idx++, arg);
			}
			return TypeConverter.toInteger(query.getSingleResult(), 0);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public void clear() {
		getCurrentSession().clear();
	}

	public void flush() {
		getCurrentSession().flush();
	}
	
	private Query<?> asmbleQueryParameters(Query<?> query, QueryHelper qh) {
		List<?>params= qh.getParameters();
		for (int i = 1; i <= params.size(); i++) {
			query.setParameter(i, params.get(i - 1));
		}
		Map<String,Object>namedParams = qh.getNamedParaters();
		for (Map.Entry<String, Object> e : namedParams.entrySet()) {
			String key = e.getKey();
			Object val = e.getValue();
			if (val == null) {
				query.setParameter(key, null);
			} else if (val instanceof Collection) {
				query.setParameterList(key, (Collection<?>) val);
			} else if (val.getClass().isArray()) {
				query.setParameterList(key, Arrays.asList(val));
			} else {
				query.setParameter(key, val);
			}
		}
		return query;
	}
}
