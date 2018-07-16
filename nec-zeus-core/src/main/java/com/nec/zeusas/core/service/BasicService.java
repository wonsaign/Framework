package com.nec.zeusas.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.entity.IEntity;
import com.nec.zeusas.exception.DaoException;
import com.nec.zeusas.exception.ServiceException;

public abstract class BasicService<T extends IEntity, PK extends Serializable> {

	protected abstract Dao<T, PK> getDao();

	@Transactional
	public void save(T entity) {
		getDao().save(entity);
	}

	@Transactional
	public T update(T entity) {
		return getDao().update(entity);
	}

	@Transactional
	public T get(PK id) {
		return getDao().get(id);
	}

	@Transactional
	public int delete(PK ids) {
		return getDao().delete(ids);
	}

	@Transactional
	public int delete(PK[] ids) {
		return getDao().delete(ids);
	}

	@Transactional(readOnly = true)
	public List<T> find(String jql, Object... args) throws ServiceException {
		try {
			if (args.length == 1 && (args[0] instanceof Map)) {
				return getDao().find(jql, args[0]);
			}
			return getDao().find(jql, args);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Transactional
	public int update(Map<String, Object> values, String cond, Object... args)
			throws ServiceException {
		try {
			return getDao().update(values, cond, args);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Transactional
	public int update(PK pk, Map<String, Object> values) throws ServiceException {
		try {
			return getDao().update(pk, values);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Transactional(readOnly = true)
	public List<T> getScrollData(int firstindex, int maxresult, String wheresql, String orderby,
			Object... queryParams) throws ServiceException {
		try {
			return getDao().getScrollData(firstindex, maxresult, wheresql, orderby, queryParams);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Transactional(readOnly = true)
	public List<T> findAll() {
		return this.getDao().findAll();
	}

	@Transactional
	public int execute(String hql, Object... params) throws ServiceException {
		try {
			return this.getDao().execute(hql, params);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Transactional
	public int executeNative(String sql, Object... params) throws ServiceException {
		try {
			return this.getDao().executeNative(sql, params);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	@Transactional(readOnly = true)
	public int count(String hqlCond, Object... args) throws ServiceException {
		return getDao().count(hqlCond, args);
	}

	@Transactional(readOnly = true)
	public List<T> find(PK[] ids) {
		return getDao().find(ids);
	}
	
	@Transactional(readOnly = true)
	public void clear() {
		getDao().clear();
	}
}
