package com.nec.zeusas.util;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ISO 8601 Year: <br>
 * YYYY (eg 1997)<br>
 * Year and month:<br>
 * YYYY-MM (eg 1997-07)<br>
 * Complete date:<br>
 * YYYY-MM-DD (eg 1997-07-16)<br>
 * Complete date plus hours and minutes:<br>
 * YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)<br>
 * Complete date plus hours, minutes and seconds:<br>
 * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)<br>
 * YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00) *
 *
 */
public final class DateTimeUtil {
	/** 年年月月格式 */
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYYYMM = "yyyyMM";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY_MM_DD_HMS = "yyyy-MM-dd HH:mm:ss";
	/** 中文格式年月日表示 */
	public static final String YYYY_MM_DD_CN = "yyyy'年'MM'月'dd'日'";

	final static TimeZone LOCAL_ZONE = TimeZone.getTimeZone("GMT+08:00");
	final static int ZONE_OFFSET = LOCAL_ZONE.getOffset(0);

	private DateTimeUtil() {
	}

	/**
	 * 按指定的格式化串格式日期，并输出日期串。
	 * 
	 * @param format
	 *            格式
	 * @param date
	 *            日期
	 * @return 格式化后的串
	 */
	public static String format(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
		return sdf.format(date);
	}

	public static String format(String format, Long date) {
		return (date == null) ? "" : format(format, new Date(date));
	}

	/**
	 * 格式化日期，如果日期是以小时的整数倍，格式化为yyyy-MM-dd,如果不是小时的整数倍，格式化为带 时间的格式 XXX: 不推荐使用
	 * 
	 * @param date
	 *            日期
	 * @return 格式化后的串
	 */
	public static String format(Date date) {
		if (date == null)
			return null;
		return (date.getTime() % (1000 * 60 * 60) == 0) ? //
		format(YYYY_MM_DD, date)
				: format(YYYY_MM_DD_HMS, date);
	}

	public static Date asDate(int y, int m, int d, int h, int mm, int ss) {
		LocalDateTime dd = LocalDateTime.of(y, m, d, h, mm, ss);
		return Date.from(dd.atZone(LOCAL_ZONE.toZoneId()).toInstant());
	}

	public static Date asDate(int y, int m, int d) {
		LocalDate dd = LocalDate.of(y, m, d);
		return Date.from(dd.atStartOfDay().atZone(LOCAL_ZONE.toZoneId()).toInstant());
	}

	/**
	 * 解析一个串按指定的格式，返回时间值。如果失败，返回当前时间.
	 * 
	 * @param fmt
	 *            格式
	 * @param date
	 *            日期时间串 如 2001-01-01或"2001-01-01 00:05"
	 * @return
	 */
	public static Date toDate(String fmt, String date) {
		return toDate(fmt, date, null);
	}

	final static int QD_LEN[] = { 4, 2, 2, 2, 2, 2, 3 };

	static void parseDD(String s, int[] dd) {
		int len = s.length();
		int stack = 0;
		int v = 0;
		int vlen = 0;
		for (int i = 0; i < len && stack < dd.length; i++) {
			char ch = s.charAt(i);
			if (ch > '9' || ch < '0') {
				if (vlen > 0) {
					dd[stack] = v;
					stack++;
					v = 0;
					vlen = 0;
				}
			} else {
				if (vlen < QD_LEN[stack]) {
					v = 10 * v + (ch & 0x0f);
					vlen++;
				} else {
					dd[stack] = v;
					stack++;
					vlen = 1;
					v = (ch & 0x0f);
				}
			}
		}

		if (v > 0) {
			dd[stack] = v;
		}
		if (dd[2] == 0)
			dd[2] = 1;
	}

	public static Date toQDate(String s) {
		int dd[] = { 0, 0, 0 };
		parseDD(s, dd);
		return asDate(dd[0], dd[1], dd[2]);
	}

	public static Date toQDatetime(String s) {
		int dd[] = { 0, 0, 0, 0, 0, 0, 0 };
		parseDD(s, dd);
		return asDate(dd[0], dd[1], dd[2], dd[3], dd[4], dd[5]);
	}

	/**
	 * <b>按指定格式解析时间</b>
	 * <p>
	 * 
	 * 解析一个串按指定的格式，返回时间值。如果失败，返回为职.
	 * 
	 * @param fmt
	 *            格式
	 * @param date
	 *            日期时间串 如 2001-01-01或"2001-01-01 00:05"
	 * @return
	 */
	public static Date toDate(String fmt, String date, Date defDate) {
		// 为空的情况下，返回默认值
		if (date == null || date.length() < 0) {
			return defDate;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.ENGLISH);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			if (date.indexOf('年') > 0) {
				return toDate(YYYY_MM_DD_CN, date, defDate);
			}
		}
		return defDate;
	}

	/**
	 * 判断该年份是不是闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return ((year % 100 == 0) //
				&& (year % 400 == 0) //
		|| (year % 100 != 0) && (year % 4 == 0));
	}

	/**
	 * 取得两个时间相差的天数
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getDateDiff(Date from, Date to) {
		long t1 = from.getTime();
		long t2 = to.getTime();
		long d1 = (t1 + ZONE_OFFSET) / (1000L * 60 * 60 * 24);
		long d2 = (t2 + ZONE_OFFSET) / (1000L * 60 * 60 * 24);
		return (int) (d2 - d1);
	}

	public static int getMonthDiff(Date from, Date to) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(from);
		int yy1 = cal1.get(Calendar.YEAR);
		int mm1 = cal1.get(Calendar.MONTH);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(to);
		int yy2 = cal2.get(Calendar.YEAR);
		int mm2 = cal2.get(Calendar.MONTH);

		return 12 * (yy2 - yy1) + mm2 - mm1;
	}

	/**
	 * 计算指定日期增加几年或减少几年对应的日期。
	 * 
	 * @param date
	 *            日期函数
	 * @param n
	 *            年数
	 * @return 计算结果日期
	 */
	public static Date addYear(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());

		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DATE);

		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.YEAR, year + n);

		int lastDate = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DATE, (lastDate < day) ? lastDate : day);
		return cal.getTime();
	}

	public static Date addMonth(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());

		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);

		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, month + n);
		int lastDate = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DATE, (lastDate < day) ? lastDate : day);
		return cal.getTime();
	}

	/**
	 * 对当前日期增加或减少几天的日期。参数正为向后的天数，负数为前多少天。
	 * 
	 * @param date
	 *            操作日期
	 * @param n
	 *            加减操作天数，可以为负数。
	 * @return 加/减后的日期
	 */
	public static Date addDay(Date date, int days) {
		long tm = date.getTime();
		tm += days * 24 * 60 * 60 * 1000L;
		return new Date(tm);
	}

	public static Date toDate(Object obj, Date defltV) {
		Date v = toDate(obj);
		return v == null ? defltV : v;
	}

	/**
	 * 将一个未知的对象转化为日期格式。 本方法仅限于Action中Bean对应的日期处理。
	 * 
	 * @param obj
	 *            需转为日期的对象，对象可能是：串、日期、长整形数组等
	 * @param defltV
	 *            默认日期
	 * @return 解析后的日期，如果失败，反回默认日期
	 */
	public static Date toDate(Object obj) {
		// 1. 如果是空的情况
		if (obj == null) {
			return null;
		}
		String param = null;
		if ((obj instanceof String[]) //
				&& Array.getLength(obj) >= 1) {
			// 如果是数组，取第一个
			param = (String) Array.get(obj, 0);
		} else if (obj instanceof CharSequence) {
			// 如果是串？
			param = obj.toString();
		} else if (obj instanceof java.util.Date) {
			// 如果是日期，返回
			return (Date) obj;
		} else if (obj instanceof java.sql.Date) {
			long tm = ((java.sql.Date) obj).getTime();
			return new Date(tm);
		} else if (obj instanceof java.sql.Timestamp) {
			long tm = ((java.sql.Timestamp) obj).getTime();
			return new Date(tm);
		} else if (obj instanceof Long) {
			// 如果是长型:YMDHHMMSS?
			return new Date(((Long) obj).longValue());
		} else if (obj instanceof Object[]) {
			Object dataObj = Array.get(obj, 0);
			if (dataObj == null)
				return null;
			return toDate(dataObj, null);
		} else {
			param = obj.toString();
		}
		// 如果长度小于8，不做处理
		if (param == null || param.length() < 8) {
			return null;
		}
		// 2012-01-01 00:00:00.12
		Date val = null;
		SimpleDateFormat fmt = new SimpleDateFormat();
		if (param.charAt(4) == '-') {
			int idx = param.indexOf(' ');
			if (idx > 0)
				param = param.substring(0, idx);
			fmt.applyPattern(YYYY_MM_DD);
			try {
				val = fmt.parse(param);
			} catch (Exception e) {
				// NOP
			}
		}
		if (val != null) {
			return val;
		}
		if (param.charAt(4) == '年') {
			fmt.applyPattern(YYYY_MM_DD);
			return toDate(YYYY_MM_DD_CN, param, null);
		}

		if (QString.isNumeric(param)) {
			if (param.length() == 8) {
				return toDate(YYYYMMDD, param, null);
			}
			long tm = QString.toLong(param, 0);
			return tm == 0 ? null : new Date(tm);
		}
		return null;
	}

	public static void waitMillis(long tm) {
		try {
			Thread.sleep(tm);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据输入数据，检查是否在当前周期。
	 * 
	 * @param period
	 *            Calendar.Date...
	 * @param millis
	 *            时间
	 * @return
	 */
	public static boolean checkPeriod(int period, long millis) {
		Calendar cal0 = Calendar.getInstance();
		int P0 = cal0.get(period);
		cal0.setTimeInMillis(millis);
		int P1 = cal0.get(period);
		return P0 == P1;
	}

	/**
	 * 将一个整数转换为日期。
	 * 
	 * @param v
	 * @return
	 */
	public static Date toDate(final Integer v) {
		if (v == null) {
			return null;
		}
		int t = v.intValue();
		int dd = t % 100;
		t /= 100;
		int mm = t % 100;
		int yy = t / 100;
		// 检查是否是合法日期
		if (yy < 1900 || yy > 3000 //
				|| mm > 12 || mm == 0 //
				|| dd == 0 || dd > 31) {
			return new Date(v * 1000L);
		}
		return asDate(yy, mm, dd);
	}

	/**
	 * 转换成YMD的整型
	 * 
	 * @param date
	 * @return
	 */
	public static Integer toYMD(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new Integer(cal.get(Calendar.YEAR) * 10000 //
				+ (cal.get(Calendar.MONTH) + 1) * 100//
				+ cal.get(Calendar.DATE));
	}

	public static boolean contains(Date now, Date from, Date to) {
		int d0 = toYMD(now);
		int d1 = toYMD(from);
		int d2 = toYMD(to);

		return d0 >= d1 && d0 <= d2;
	}
}
