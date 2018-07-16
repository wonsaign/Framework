package com.nec.zeusas.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.nec.zeusas.util.Dom4jUtils;
import com.nec.zeusas.util.IOUtils;

/**
 * DBMS Meta management class
 */
public class DBMS {
	static final Logger log = LoggerFactory.getLogger(DBMS.class);

	final static Map<String, DataDDL> ddls = new HashMap<String, DataDDL>();
	final static Map<String, Long> lastUpdate = new HashMap<String, Long>();

	static final ResourceBundle JDBC = ResourceBundle.getBundle("jdbc");

	public static Connection getConnection(DataSource ds) //
			throws SQLException {
		return ds.getConnection();
	}

	public static DataSource getJndiDataSource(String jndiName) //
			throws SQLException {
		Context initContext;
		try {
			initContext = new InitialContext();
			Context ctx = (Context) initContext.lookup("java:/comp/env");
			return (DataSource) ctx.lookup(jndiName);
		} catch (Exception e) {
			log.error("JNDI数据源:{}取得连接错误.", jndiName);
			throw new SQLException(e);
		}
	}

	/**
	 * 根据JDBC定义的屬性信息中取得。
	 * 
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public static Connection getJdbcConnection(String name) throws SQLException {
		String prefix;
		prefix = ("dataSource".equalsIgnoreCase(name) || Strings.isNullOrEmpty(name)) ? "" : name
				+ ".";

		String dev = JDBC.getString(prefix + "jdbc.driver");
		// 连接MySql数据库，用户名和密码都是root
		String url = JDBC.getString(prefix + "jdbc.url");
		String username = JDBC.getString(prefix + "jdbc.username");
		String password = JDBC.getString(prefix + "jdbc.password");
		try {
			log.debug("注册jdbc驱动{},Url={}, username={}", dev, url, username);
			Class.forName(dev);
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public static boolean load(File fxml) {
		Long t = lastUpdate.get(fxml.getName());
		if (t != null && t == fxml.lastModified()) {
			return false;
		}
		boolean success = false;
		lastUpdate.put(fxml.getName(), fxml.lastModified());
		InputStream input = null;
		try {
			input = new FileInputStream(fxml);
			load(input);
			input = null;
			success = true;
		} catch (IOException e) {
			log.error("Load file:{} error.", fxml.getName(), e);
		} finally {
			IOUtils.close(input);
		}
		return success;
	}

	static void load(InputStream input) throws IOException {
		try {
			Document doc = Dom4jUtils.getXmlDoc(input);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> nodes = root.elements(Meta.TAG_DDL);
			for (Element e : nodes) {
				DataDDL ddl = new DataDDL().init(e);
				;
				// 检查是否存在旧的项，如果不存在，加入
				DataDDL origDdl = ddls.get(ddl.getId());
				if (origDdl == null) {
					ddls.put(ddl.getId(), ddl);
				} else if (origDdl.dsName.equals(ddl.dsName) //
						&& origDdl.dsType.equals(ddl.dsType)) {
					origDdl.setMetas(ddl.getMetas());
					origDdl.setProcs(ddl.getProcs());
				} else {
					log.error("DDL ITEM 定义重复，并且不可合并！,DDL ID={}", ddl.getId());
				}
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	public static boolean load(String xmlResource) {
		log.info("DDL 装入资源文件：{}", xmlResource);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream input = null;
		boolean success = false;
		try {
			input = loader.getResourceAsStream(xmlResource);
			load(input);
			input = null;
			success = true;
		} catch (Exception e) {
			log.error("装入DDL资源错误{}", xmlResource, e);
		} finally {
			IOUtils.close(input);
		}
		return success;
	}

	public static DataDDL getItem(String id) {
		return ddls.get(id);
	}

	/**
	 * 取得定义的处理节点。
	 * 
	 * @param itmId
	 * @param procId
	 * @return
	 */
	public static Proc getProc(String itmId, String procId) {
		DataDDL itm = ddls.get(itmId);
		if (itm == null)
			return null;
		return itm.getProc(procId);
	}

	public static Collection<DataDDL> values() {
		return ddls.values();
	}
}
