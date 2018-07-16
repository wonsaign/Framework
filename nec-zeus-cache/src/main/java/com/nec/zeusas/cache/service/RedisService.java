package com.nec.zeusas.cache.service;

/*
 * Copyright 2017, Bright Grand Technologies Co., Ltd.
 * 
 * 版权所有，明弘科技(北京)有限公司，2017
 */
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 *
 * @author zhensx
 * @date 2017年11月23日 上午8:59:44
 *
 */
public interface RedisService extends Serializable {

	/**
	 * 不加锁，获取单个对象.
	 * 
	 * @param key
	 * @return Redis中对象
	 */
	<T> T hget(Class<T> clazz, Serializable key);

	Object getObject(String key);

	Set<String> keys(String type);

	/**
	 * 不使用锁,存取单个对象
	 * 
	 * @param key
	 * @param value
	 * @return OK or False
	 */
	void hset(Serializable key, Object value);

	void setObject(String key, Object value);

	void set(String key, String value);

	void set(String key, Number value);

	/**
	 * 
	 * @param type
	 * @param key
	 * @param value
	 */
	void hset(String type, Serializable key, Object value);

	/**
	 * 删除keys对应的记录,
	 * 
	 * @param String
	 *            key
	 * @return 删除的记录数
	 */
	<T> long hdel(Class<T> clazz, Serializable... key);

	long hdel(String clazz, Serializable... key);

	long del(String key);

	/**
	 * 根据类型获取所有
	 * 
	 * @param clazz
	 * @return
	 */
	<T> List<T> getAll(Class<T> clazz);

	List<?> getAll(String clazz);

	void close();

	long incr(Serializable key);

	long incr(Serializable key, long val);

	double incr(Serializable key, double val);

	double decr(Serializable key, double val);

	long decr(Serializable key, long val);

	Object hget(String type, Serializable key);

	Object keyAsObject(String type, String key);

	<T> void hset(Class<T> cls, Map<?, T> values);

	String getString(String key);

	Jedis getJedis();

	Object hgetObject(String id, Serializable key);

	Object hgetString(String id, Serializable key);

}
