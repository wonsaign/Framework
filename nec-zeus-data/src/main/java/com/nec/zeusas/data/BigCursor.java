package com.nec.zeusas.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import com.nec.zeusas.exception.ServiceException;

/**
 * 数据库光标实现。<p>
 * 
 */
public class BigCursor implements Iterator<Record> {
	// 数据库的表对象
	private final Table table;
	// 数据库的结果集
	private ResultSet resultSet;
	private Statement pstmt;
	
	BigCursor(Meta meta, Connection conn) {
		table = new Table(meta, conn);
	}

	@Override
	public boolean hasNext() {
		try {
			return resultSet.next();
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public Record next() {
		try {
			return table.toRecord(resultSet);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}

	public void init(Object[] args) throws SQLException {
		Connection conn = table.getConnection();
		Meta meta = table.meta;

		// 检查是否有参数
		if (args == null || args.length == 0) {
			pstmt = conn.createStatement();
			resultSet = pstmt.executeQuery(meta.getStmt());
		} else {
			pstmt = QueryHelper.preparedStatement(conn, meta.getStmt(), args);
			resultSet = ((PreparedStatement) pstmt).executeQuery();
		}

		table.initMetaData(resultSet);
	}

	public void close() {
		QueryHelper.close(resultSet);
		resultSet = null;
		QueryHelper.close(pstmt);
		pstmt = null;
		table.close();
	}
}
