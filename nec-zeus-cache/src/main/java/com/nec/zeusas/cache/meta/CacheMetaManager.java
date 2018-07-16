package com.nec.zeusas.cache.meta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nec.zeusas.exception.ServiceException;
import com.nec.zeusas.io.Codec;
import com.nec.zeusas.io.JsonCodec;
import com.nec.zeusas.util.Dom4jUtils;

/**
 * extends OnStartApplication
 * 
 * @author zhensx
 * @date 2017年11月25日 下午4:05:24
 *
 */
public class CacheMetaManager {

	final static String META_CONFIG_DB = "METAS_CONFIG#0";

	static Logger logger = LoggerFactory.getLogger(CacheMetaManager.class);

	/** Map:(ID,CacheMeta) */
	final Map<String, CacheMeta> metas = new ConcurrentHashMap<>();

	private JedisPool pool;

	private String configFile;

	/** Meta 编码器 */
	private final Codec codec = new JsonCodec();

	public CacheMetaManager() {
	}

	public void setConfigFile(String conf) {
		this.configFile = conf;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public void load(File f) throws ServiceException {
		List<CacheMeta> metas;
		if (f.getName().endsWith(".xml")) {
			metas = loadFromXML(f);
		} else {
			try {
				metas = loadFromJSON(f);
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		register(metas);

		if (pool != null) {
			Jedis jedis = pool.getResource();
			try {
				Pipeline pipe = jedis.pipelined();
				for (CacheMeta meta : metas) {
					pipe.hset(META_CONFIG_DB, meta.id(), meta.toJSON());
				}
				pipe.sync();
			} finally {
				jedis.close();
			}
		}
	}

	void register(List<CacheMeta> metas) {
		for (CacheMeta meta : metas) {
			this.metas.put(meta.id(), meta);
		}
	}

	List<CacheMeta> loadFromXML(File fxml) {
		Document doc = Dom4jUtils.getXmlDoc(fxml);
		Element root = doc.getRootElement();
		List<CacheMeta> metas = new ArrayList<>();
		if (!"cache-meta".equals(root.getName())) {
			logger.warn("文件{}非cache-meta配置文件。", fxml.getName());
			return metas;
		}

		@SuppressWarnings("unchecked")
		List<Element> eMetas = root.elements("meta");
		for (Element e : eMetas) {
			CacheMeta meta = new CacheMeta(e);
			if (meta.id() != null && meta.getEntityClass() != null) {
				metas.add(meta);
			}
		}
		return metas;
	}

	List<CacheMeta> loadFromJSON(File f) throws IOException {
		byte[] b = Files.readAllBytes(f.toPath());
		String json = new String(b, "UTF-8");
		JSONArray arr = JSON.parseArray(json);
		List<CacheMeta> metas = new ArrayList<>();
		for (Object obj : arr) {
			CacheMeta meta = new CacheMeta((JSONObject) obj);
			if (meta.id() == null //
					|| meta.getEntityClass() == null) {
				logger.warn("在配置文件:[{}]中, 出现空错误,请检查: ID:{},class:{},desc:{}", //
						f.getName(), //
						meta.getId(), meta.getEntityClass(), //
						meta.getDescription());
			} else {
				metas.add(meta);
			}
		}
		return metas;
	}

	public void load() throws ServiceException {
		// Unit test only
		if (pool == null) {
			return;
		}

		Jedis jedis = pool.getResource();
		try {
			Map<String, String> vm = jedis.hgetAll(META_CONFIG_DB);
			for (String s : vm.values()) {
				CacheMeta meta = codec.decode(s, CacheMeta.class);
				if (meta != null && meta.id() != null) {
					metas.put(meta.id(), meta);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public CacheMeta getCacheMeta(String id) {
		return metas.get(id);
	}

	public Jedis getJedis() {
		return this.pool.getResource();
	}

	public void init() {
		File fconfig;
		try {
			if (configFile != null && (fconfig = new File(configFile)).exists()) {
				load(fconfig);
			} else {
				load();
			}
		} catch (Exception e) {
			logger.error("CacheMeta 管理器初始化失败。", e);
		}
	}

	public CacheMeta getCacheMeta(Class<?> cls) {
		CacheMeta meta = metas.get(cls.getName());
		if (meta == null) {
			meta = new CacheMeta(cls);
			this.addCacheMeta(meta);
		}
		return meta;
	}

	public void addCacheMeta(CacheMeta meta) {
		if (meta.getKeyClass() == null) {
			meta.setKeyClass(String.class);
		}
		if (meta.getEntityClass() == null) {
			meta.setEntityClass(java.lang.String.class);
		}
		if (meta.getId() == null) {
			if (!meta.getEntityClass().getName().startsWith("java.")) {
				meta.setId(meta.getEntityClass().getName());
			} else {
				throw new ServiceException("Meta ID 不能为空！");
			}
		}
		this.metas.put(meta.getId(), meta);
	}
}
