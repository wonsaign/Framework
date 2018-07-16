package com.nec.zeusas.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.nec.zeusas.core.entity.IEntity;
import com.nec.zeusas.exception.ServiceException;

public interface IService<T extends IEntity, PK extends Serializable> {

	T get(PK key);

	void save(T t);

	T update(T t);

	int delete(PK ids);

	int delete(PK[] ids);

	List<T> find(PK[] ids);

	int update(PK pk, Map<String, Object> values) throws ServiceException;

	int update(Map<String, Object> values, String cond, Object... args) throws ServiceException;

	List<T> findAll();

	List<T> find(String where, Object... args) throws ServiceException;

	int count(String cond, Object... args) throws ServiceException;

	int execute(String hql, Object... params) throws ServiceException;

	int executeNative(String sql, Object... params) throws ServiceException;

	List<T> getScrollData(int firstindex, int maxresult, //
			String wheresql, String orderby, //
			Object... params) //
			throws ServiceException;

	void clear();
}
