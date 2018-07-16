package com.nec.zeusas.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.nec.zeusas.core.entity.IEntity;
import com.nec.zeusas.exception.DaoException;

public interface Dao<T extends IEntity, PK extends Serializable> {

	T get(PK key);

	void save(T t);

	T update(T t);

	int update(Map<String, Object> values, String cond, Object... args) throws DaoException;

	int update(PK pk, Map<String, Object> values) throws DaoException;

	int delete(PK id);

	int delete(PK[] ids);

	List<T> findAll();

	List<T> find(PK[] ids);

	List<T> find(String where, Object... args) throws DaoException;

	int count(String hqlCond, Object... args) throws DaoException;

	int execute(String hql, Object... params) throws DaoException;

	int executeNative(String sql, Object... params) throws DaoException;

	List<T> getScrollData(int firstindex, int maxresult, //
			String wheresql, String orderby, //
			Object... queryParams) //
			throws DaoException;

	void clear();

	void flush();

	String getTableName();
}