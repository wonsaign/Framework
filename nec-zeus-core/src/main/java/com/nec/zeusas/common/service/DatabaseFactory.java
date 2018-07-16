package com.nec.zeusas.common.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.data.DBMS;
import com.nec.zeusas.data.DataDDL;
import com.nec.zeusas.data.Database;
import com.nec.zeusas.exception.ServiceException;

public class DatabaseFactory {
	static Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);
	
	static Map<String,Database> databases = new HashMap<>();
	
	public static Database getDatabase(String ddlName) {
		Database db = databases.get(ddlName);
		if (db != null) {
			return db;
		}
		DataDDL ddl = DBMS.getItem(ddlName);
		if (ddl == null) {
			logger.error("Get ddl error, ddl: {} Not exist",ddlName);
			throw new ServiceException("DDL:" + ddlName + " Not exist.");
		}
		return getDatabase(ddl);
	}

	public static Database getDatabase(DataDDL ddl) {
		Database db = databases.get(ddl.getId());
		if (db != null) {
			return db;
		}
		db = createDatabase(ddl);
		databases.put(ddl.getId(), db);
		return db;
	}
	
	public static Database createDatabase(DataDDL ddl) {
		logger.debug("Create database: DDL id {}", ddl.getId());
		if (DataDDL.DS_DATASOURCE.equalsIgnoreCase(ddl.getDsType())) {
			DataSource ds = AppContext.getBean(ddl.getDsName(), DataSource.class);
			return new Database(ddl, ds);
		}
		return new Database(ddl);
	}
}
