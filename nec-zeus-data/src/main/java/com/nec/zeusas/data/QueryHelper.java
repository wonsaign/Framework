package com.nec.zeusas.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查询辅助类
 */
public class QueryHelper {
	private static Logger logger = LoggerFactory.getLogger(QueryHelper.class);
	// 参数表
	final List<Object> params = new ArrayList<Object>();
	// 命名参数表
	final Map<String, Object> namedParams = new LinkedHashMap<String, Object>();
	// SQL 脚本
	final StringBuilder script = new StringBuilder(256);

	public QueryHelper() {
	}

	/**
	 * 带脚本的构造器
	 * 
	 * @param SQL脚本
	 */
	public QueryHelper(CharSequence sc) {
		script.append(sc);
	}

	public int length() {
		return script.length();
	}

	/**
	 * 设定长度。如果是负数，从当前的长度中减去其值，如果是非负数，设定为期长度。
	 * 
	 * @param len
	 */
	public void setLength(int len) {
		if (len < 0) {
			int newLen = script.length() + len;
			script.setLength(newLen < 0 ? 0 : newLen);
		} else if (len < script.length()) {
			script.setLength(len);
		}
	}

	/**
	 * 追加一个HQL的类名
	 * 
	 * @param cls
	 *            类名
	 * @return
	 */
	public QueryHelper append(Class<?> cls) {
		script.append(cls.getName());
		return this;
	}

	/**
	 * 追加脚本（语句或条件）
	 * 
	 * @param sc
	 *            SQL脚本
	 * @return
	 */
	public QueryHelper append(CharSequence sc) {
		script.append(sc);
		return this;
	}

	/**
	 * 追加条件和参数
	 * 
	 * @param sc
	 *            条件部分脚本
	 * @param values
	 *            参数表
	 * @return
	 */
	public QueryHelper addParameters(CharSequence sc, Object... values) {
		script.append(sc);
		addParameter(values);
		return this;
	}

	/**
	 * 追加参数表, 参数表中参数，替换SQL中的？
	 * 
	 * @param values
	 *            参数表
	 */
	public QueryHelper addParameter(Object... values) {
		for (Object val : values) {
			this.params.add(val);
		}
		return this;
	}

	/**
	 * 设置以命名参数的参数表，如 select ... from ... where aaa=:aaa
	 * 
	 * @param name
	 *            参数名称
	 * @param value
	 *            参数对应的值
	 */
	public void setNamedParameter(String name, Object value) {
		this.namedParams.put(name, value);
	}

	/**
	 * 设置以命名参数的参数表，如 select ... from ... where aaa=:aaa
	 * 
	 * @param sc
	 *            SQL或HQL脚本
	 * @param name
	 *            参数名称
	 * @param value
	 *            参数对应的值
	 */
	public void setNamedParameter(String sc, String name, Object value) {
		this.script.append(' ').append(sc);
		this.namedParams.put(name, value);
	}

	public String getScript() {
		return qulifier(script);
	}

	public List<Object> getParameters() {
		return this.params;
	}

	public Map<String, Object> getNamedParaters() {
		return this.namedParams;
	}

	public String toString() {
		return script.toString();
	}

	static boolean isSpace(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '　';
	}

	public static String qulifier(CharSequence psql) {
		assert (psql != null);

		int len = psql.length();
		int idx = 0;

		if (len <= 5) {
			return psql.toString().trim();
		}

		final StringBuilder bb = new StringBuilder(len);
		boolean comment = false;
		boolean lineComment = false;
		bb.append(psql.charAt(idx++));
		for (int i = idx; i < len; i++) {
			char c = psql.charAt(i);
			switch (c) {
			case '\n':
				if (lineComment) {
					lineComment = false;
				}
			case '\r':
			case '\t':
			case '\b':
			case ' ':
			case '　':// 全角空格也要滤掉
				// 如果是行注释，或块注释
				if (lineComment || comment) {
					break;
				}
				// 如果长度为0，不在前面加空格
				if (bb.length() == 0) {
					break;
				}
				// 前面一个不是空格的性况
				if (bb.charAt(bb.length() - 1) != ' ') {
					bb.append(' ');
				}
				break;
			case ',':
				if (!comment && !lineComment) {
					// 如果前面有空，
					if (bb.charAt(bb.length() - 1) == ' ') {
						bb.setLength(bb.length() - 1);
					}
					bb.append(c).append(' ');
				}
				break;
			case '*':
				if (psql.charAt(i - 1) == '/') {
					bb.setLength(bb.length() - 1);
					comment = true;
				} else if (!comment) {
					bb.append(c);
				}
				break;
			case '-':
				// 已经是注释的情况
				if (comment || lineComment) {
					break;
				}
				// 前一个是-,显然是注释
				if (psql.charAt(i - 1) == '-') {
					lineComment = true;
					bb.setLength(bb.length() - 1);
				} else {
					bb.append(c);
				}
				break;
			case '/':
				if (psql.charAt(i - 1) == '*') {
					comment = false;
					if (bb.charAt(bb.length() - 1) != ' ') {
						bb.append(' ');
					}
				} else if (psql.charAt(i - 1) == '/') {
					if (lineComment) {
						break;
					}
					// 当前一个为一时，且当前不是块注释，设为行注释
					if (!comment) {
						lineComment = true;
						bb.setLength(bb.length() - 1);
					}
				} else if (!comment) {
					bb.append(c);
				}
				break;
			default:
				if (!lineComment && !comment) {
					bb.append(c);
				}
			}
		}
		return bb.toString().trim();
	}

	public final static void close(Connection c) {
		if (c != null) {
			try {
				if (!c.isClosed()) {
					logger.debug("VVV: 关闭连接:{}", c);
					c.close();
				}
			} catch (Exception e) {
				// NOP
			}
		}
	}

	public final static void close(Statement c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				// NOP
			}
		}
	}

	public final static void close(PreparedStatement c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				// NOP
			}
		}
	}

	public final static void close(ResultSet c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				// NOP
			}
		}
	}

	/**
	 * 根据参数列表，取回结果。
	 * 
	 * @param conn
	 *            JDBC链接
	 * @param sql
	 *            SQL文本
	 * @param args
	 *            参数表
	 * @return ResultSet 结果集
	 * @throws SQLException
	 */
	public static PreparedStatement preparedStatement(Connection conn, String SQL, Object... args) throws SQLException {
		assert SQL != null;
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		try {
			int idx = 1;
			for (Object arg : args) {
				// FIXME: 如果是NULL，可能查不出记录
				if (arg == null) {
					pstmt.setNull(idx++, Types.JAVA_OBJECT);
				} else if (arg instanceof String) {
					pstmt.setString(idx++, (String) arg);
				} else if (arg instanceof Integer) {
					pstmt.setInt(idx++, (Integer) arg);
				} else if (arg instanceof Long) {
					pstmt.setLong(idx++, (Long) arg);
				} else if (arg instanceof Float) {
					pstmt.setFloat(idx++, (Float) arg);
				} else if (arg instanceof Double) {
					pstmt.setDouble(idx++, (Double) arg);
				} else if (arg instanceof Date) {
					pstmt.setTimestamp(idx++, new Timestamp(((Date) arg).getTime()));
				} else {
					pstmt.setObject(idx++, arg);
				}
			}
		} catch (Exception e) {
			close(pstmt);
			throw new SQLException(e);
		}
		return pstmt;
	}

	public static boolean execUpdate(Connection conn, String SQL) throws SQLException {
		Statement stmt = conn.createStatement();
		boolean v;
		try {
			v = stmt.execute(SQL);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return v;
	}

	static Predicate<String> FMTARGS = Pattern.compile("[%]s|[%]\\d+").asPredicate();

	public static boolean execUpdate(Connection conn, String SQL, Object... args) throws SQLException {
		// 使用带%号的格式，处理参数，XXX:不推荐！
		if (FMTARGS.test(SQL)) {
			return execUpdate(conn, String.format(SQL, args));
		}
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		int idx = 1;
		try {
			for (Object arg : args) {
				if (arg == null) {
					// FIXME: SQL Server NOT SUPPORT this feature
					pstmt.setNull(idx++, Types.VARCHAR);
				} else if (arg instanceof String) {
					pstmt.setString(idx++, (String) arg);
				} else if (arg instanceof Integer) {
					pstmt.setInt(idx++, (Integer) arg);
				} else if (arg instanceof Long) {
					pstmt.setLong(idx++, (Long) arg);
				} else if (arg instanceof Float) {
					pstmt.setFloat(idx++, (Float) arg);
				} else if (arg instanceof Double) {
					pstmt.setDouble(idx++, (Double) arg);
				} else if (arg instanceof Date) {
					Timestamp d = new Timestamp(((Date) arg).getTime());
					pstmt.setTimestamp(idx++, d);
				} else {
					pstmt.setObject(idx++, arg);
				}
			}
			return pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}
}
