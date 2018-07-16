package com.nec.zeusas.cache.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.cache.meta.CacheMeta;
import com.nec.zeusas.cache.meta.CacheMetaManager;
import com.nec.zeusas.cache.service.RedisService;
import com.nec.zeusas.exception.ServiceException;
import com.nec.zeusas.io.Codec;
import com.nec.zeusas.io.Hessian2Codec;
import com.nec.zeusas.util.ID;
import com.nec.zeusas.util.QString;
import com.nec.zeusas.util.TypeConverter;

/**
 *
 * @author zhensx
 * @date 2017年11月23日 上午9:00:25
 *
 */
public class RedisServiceImpl implements RedisService {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3393544619110258027L;

	final static Logger logger = LoggerFactory
			.getLogger(RedisServiceImpl.class);

	private CacheMetaManager cacheMetaManager;
	/** Thread local Jedis */
	private final ThreadLocal<Jedis> localRedis = new ThreadLocal<>();
	/** 序列化编码解码器 */
	private Codec codec;

	public RedisServiceImpl() {
		codec = new Hessian2Codec();
	}

	public void setCacheMetaManager(CacheMetaManager cacheMetaManager) {
		this.cacheMetaManager = cacheMetaManager;
	}

	@Override
	public Jedis getJedis() {
		Jedis jedis = localRedis.get();
		if (jedis != null && !jedis.isConnected()) {
			jedis = null;
		}
		if (jedis == null) {
			jedis = cacheMetaManager.getJedis();
			logger.debug("XXX: open redis connecting ...");
			localRedis.set(jedis);
		}
		return jedis;
	}

	/**
	 * 关闭打开的redis，并释放资源
	 */
	public void close() {
		Jedis jedis;
		if ((jedis = localRedis.get()) != null) {
			logger.debug("VVV: close redis connecting ... OK!");
			if (jedis.isConnected()) {
				jedis.close();
			}
			localRedis.remove();
		}
	}

	@Override
	public Object hgetObject(String id, Serializable key) {
		Jedis jedis = this.getJedis();
		byte bb[] = jedis.hget(id.getBytes(), ID.toBytes(key));
		return codec.decodeAsClassObject(bb);
	}

	@Override
	public Object hgetString(String id, Serializable key) {
		Jedis jedis = this.getJedis();
		return jedis.hget(id, String.valueOf(key));
	}

	/**
	 * 取得指定类的key对应的value.
	 * 
	 * @param clazz
	 *            指定类
	 * @param key
	 *            value
	 * @return value
	 */
	@Override
	public <T> T hget(Class<T> clazz, Serializable key) {
		Jedis jedis = this.getJedis();
		CacheMeta m = cacheMetaManager.getCacheMeta(clazz);
		byte[] b = jedis.hget(m.id().getBytes(), ID.toBytes(key));
		return b == null ? null : codec.decode(b, clazz);
	}

	private void checkEntityClass(Class<?> type, Object value) {
		Class<?> theClass = value.getClass();
		if (theClass.getName().startsWith("com.")) {
			if (!theClass.equals(type)) {
				logger.error("写入的类与定义的类不一致！{}->{} value:{}",
						theClass.getName(), type.getName(),
						JSON.toJSONString(value));
				throw new ServiceException("写入的类与定义的类不一致");
			}
		}
	}

	@Override
	public void hset(Serializable key, Object value) {
		if (value instanceof Map) {
			setMap(String.valueOf(key), (Map<?, ?>) value);
			return;
		}
		Jedis jedis = this.getJedis();
		CacheMeta m = cacheMetaManager.getCacheMeta(value.getClass());

		checkEntityClass(m.getEntityClass(), value.getClass());

		jedis.hset(m.getId().getBytes(), //
				ID.toBytes(key),//
				codec.encodeToBytes(value));
	}

	@Override
	public void hset(String type, Serializable key, Object value) {
		Jedis jedis = this.getJedis();
		CacheMeta m = cacheMetaManager.getCacheMeta(type);

		if (m == null) {
			if (value.getClass().getName().startsWith("java.")) {
				jedis.hset(type, String.valueOf(key), String.valueOf(value));
			} else {
				jedis.hset(m.getId().getBytes(), //
						ID.toBytes(key), //
						codec.encodeToBytes(value));
			}
		} else if (m.getEntityClass().getName().startsWith("java.")) {
			jedis.hset(type, String.valueOf(key), String.valueOf(value));
		} else {
			checkEntityClass(m.getEntityClass(), value.getClass());
			jedis.hset(m.getId().getBytes(), //
					ID.toBytes(key), //
					codec.encodeToBytes(value));
		}
	}

	@Override
	public <T> long hdel(Class<T> clazz, Serializable... keys) {
		Jedis jedis = this.getJedis();
		CacheMeta m = cacheMetaManager.getCacheMeta(clazz);
		String kk[] = new String[keys.length];
		for (int i = 0; i < kk.length; i++) {
			kk[i] = String.valueOf(keys[i]);
		}
		return jedis.hdel(m.id(), kk);
	}

	@Override
	public long hdel(String type, Serializable... keys) {
		Jedis jedis = this.getJedis();
		String kk[] = new String[keys.length];
		for (int i = 0; i < kk.length; i++) {
			kk[i] = String.valueOf(keys[i]);
		}
		return jedis.hdel(type, kk);
	}

	@Override
	public long del(String key) {
		Jedis jedis = this.getJedis();
		return jedis.del(key);
	}

	@Override
	public <T> List<T> getAll(Class<T> clazz) {
		Jedis jedis = this.getJedis();
		CacheMeta m = cacheMetaManager.getCacheMeta(clazz);
		Map<byte[], byte[]> vv = jedis.hgetAll(m.id().getBytes());
		List<T> result = new ArrayList<>();
		for (byte[] bb : vv.values()) {
			result.add(codec.decode(bb, clazz));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<?> getAll(String type) {
		Jedis jedis = this.getJedis();
		Map<byte[], byte[]> vv = jedis.hgetAll(type.getBytes());

		@SuppressWarnings("rawtypes")
		List result = new ArrayList<>();
		for (byte[] bb : vv.values()) {
			Object v = codec.decodeAsClassObject(bb);
			result.add(v);
		}
		return result;
	}

	private void setMap(String type, Map<?, ?> values) {
		if (values == null || values.size() == 0) {
			return;
		}
		Jedis jedis = this.getJedis();
		CacheMeta m = cacheMetaManager.getCacheMeta(type);
		assert m != null;
		Pipeline pipe = jedis.pipelined();

		boolean isString = m.getEntityClass().getName().startsWith("java.");
		if (isString) {
			for (Entry<?, ?> e : values.entrySet()) {
				Object value;
				if ((value = e.getValue()) == null) {
					continue;
				}
				pipe.hset(m.getId(), String.valueOf(e.getKey()),
						String.valueOf(value));
			}
		} else {
			for (Entry<?, ?> e : values.entrySet()) {
				Object value;
				if ((value = e.getValue()) == null) {
					continue;
				}

				checkEntityClass(value.getClass(), m.getEntityClass());
				pipe.hset(type.getBytes(), ID.toBytes(e.getKey()),
						codec.encodeToBytes(value));

			}
		}
		pipe.sync();
	}

	@Override
	public <T> void hset(Class<T> cls, Map<?, T> values) {
		if (values == null || values.size() == 0) {
			return;
		}
		Jedis jedis = this.getJedis();
		CacheMeta m = cacheMetaManager.getCacheMeta(cls);
		Pipeline pipe = jedis.pipelined();
		for (Entry<?, T> e : values.entrySet()) {
			Object value;
			if ((value = e.getValue()) == null) {
				continue;
			}
			checkEntityClass(m.getEntityClass(), value.getClass());
			byte bb[] = codec.encodeToBytes(value);
			pipe.hset(m.idBytes(), //
					ID.toBytes(e.getKey()),//
					bb);
		}
		pipe.sync();
	}

	@Override
	public long incr(Serializable vkey) {
		Jedis jedis = this.getJedis();
		String key = String.valueOf(vkey);
		return jedis.incr(QString.toBytes(key));
	}

	@Override
	public long incr(Serializable vkey, long val) {
		Jedis jedis = this.getJedis();
		String key = String.valueOf(vkey);
		return jedis.incrBy(QString.toBytes(key), val);
	}

	@Override
	public double incr(Serializable vkey, double val) {
		Jedis jedis = this.getJedis();
		String key = String.valueOf(vkey);
		return jedis.incrByFloat(QString.toBytes(key), val);
	}

	@Override
	public double decr(Serializable vkey, double val) {
		return incr(vkey, -val);
	}

	@Override
	public long decr(Serializable vkey, long val) {
		Jedis jedis = this.getJedis();
		String key = String.valueOf(vkey);
		return jedis.decrBy(QString.toBytes(key), val);
	}

	@Override
	public Object keyAsObject(String id, String key) {
		CacheMeta m = cacheMetaManager.getCacheMeta(id);
		return JSON.parseObject(key, m.getKeyClass());
	}

	@Override
	public Object hget(String id, Serializable vkey) {
		Jedis jedis = this.getJedis();
		String key = String.valueOf(vkey);
		CacheMeta m = cacheMetaManager.getCacheMeta(id);

		if (m == null) {
			return jedis.hget(id, key);
		} else if (m.getEntityClass().getName().startsWith("java.lang")) {
			String s = jedis.hget(id, key);
			return TypeConverter.toType(s, m.getEntityClass());
		}

		byte bb[] = jedis.hget(m.getId().getBytes(), ID.toBytes(key));
		return codec.decode(bb, m.getClass());
	}

	@Override
	public String getString(String key) {
		Jedis jedis = this.getJedis();
		return jedis.get(key);
	}

	@Override
	public Object getObject(String key) {
		Jedis jedis = this.getJedis();
		byte b[] = jedis.get(QString.toBytes(key));
		return codec.decodeAsClassObject(b);
	}

	@Override
	public void setObject(String key, Object value) {
		assert key != null;
		if (value == null) {
			return;
		}
		Jedis jedis = this.getJedis();
		byte v[] = codec.encodeAsClassObject(value);
		jedis.set(QString.toBytes(key), v);
	}

	@Override
	public Set<String> keys(String key) {
		Jedis jedis = this.getJedis();
		return jedis.hkeys(key);
	}

	@Override
	public void set(String key, String value) {
		Jedis jedis = this.getJedis();
		jedis.set(key, value);
	}

	@Override
	public void set(String key, Number value) {
		Jedis jedis = this.getJedis();
		jedis.set(key, String.valueOf(value));
	}
}
