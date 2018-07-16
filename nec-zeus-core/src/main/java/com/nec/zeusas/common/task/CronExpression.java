package com.nec.zeusas.common.task;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.*;

/**
 * <p>
 * 【自定义Cron表达式解析】<br>
 * 顺序: s m H D W M Q Y [0|1]
 * </p>
 */
final class CronExpression {
	private static final Logger log = LoggerFactory
			.getLogger(CronExpression.class);

	private final String cronExp;

	private final static int CRONEXPR_LEN = 9;
	// Field descriptor
	private final static String SPLIT_CHAR = "\\s+";
	private final static int SECOND = 0;
	private final static int MINUTE = 1;
	private final static int HOUR = 2;
	private final static int DAY = 3;
	private final static int WEEK = 4;
	private final static int MONTH = 5;
	private final static int QUARTER = 6;
	private final static int YEAR = 7;
	private final static int UNDEF = 100;

	private int asterisk = UNDEF;
	private int cross = UNDEF;

	private boolean cycle = false;
	private final boolean valid;

	/** 最后执行的时间 */
	private long lastUpdate;
	/** 下一次执行时间 */
	private final GregorianCalendar next = new GregorianCalendar();
	/** 系统当前时间 */
	private final GregorianCalendar now = new GregorianCalendar();
	/** cron 表达式 */
	private final int []tv = { 0, 0, 0, 0, 0, 0, 0, 0, UNDEF };

	/**
	 * Cron表达式构造函数，参数为cron表达式
	 * 
	 * @param cron
	 *            Cron表达式
	 */
	CronExpression(String cron) {
		cronExp = cron.trim();
		valid = parse();
		lastUpdate = (System.currentTimeMillis() - 1000);
	}

	boolean isValid() {
		return valid;
	}

	/**
	 * 解析表达Cron表达式
	 * 
	 * @return
	 */
	private boolean parse() {
		log.debug("分析任定表达式:" + cronExp);
		String[] cronArr = cronExp.split(SPLIT_CHAR);
		// 如果不为9，为非法表达式
		if (cronArr.length != CRONEXPR_LEN) {
			log.warn("Cron Express{} 位数不正确，{}==9?", cronExp, cronArr);
			return false;
		}
		for (int i = 0; i < CRONEXPR_LEN; i++) {
			if (cronArr[i].charAt(0) == '*' && asterisk == UNDEF) {
				asterisk = i;
			} else if (cronArr[i].charAt(0) == '+' && cross == UNDEF) {
				cross = i;
			}
			tv[i] = toInt(cronArr[i], 0);
		}
		if (cross == UNDEF && asterisk > DAY) {
			cross = asterisk;
		}
		if (cross < WEEK) {
			asterisk = (cross < asterisk) ? cross : asterisk;
			cross = UNDEF;
		}
		// 是否循环？
		cycle = tv[CRONEXPR_LEN - 1] != 0;
		// FIXME:有效性检查
		if (cycle) {
			int sum = 0;
			for (int i = 0; i < 4; i++) {
				sum += tv[i];
			}
			if (sum == 0) {
				log.error("表达式如何为周期性，必須大于0. 表达式为：" + cronExp);
				return false;
			}
		}
		return (cross != UNDEF || asterisk != UNDEF);
	}

	long next() {
		return valid ? next(lastUpdate) : Long.MAX_VALUE;
	}

	/**
	 * <p>
	 * 下一次执行等待的时间（ms）
	 * </p>
	 * 
	 * @return
	 * @data: Create on Apr 12, 2013 12:42:28 AM
	 */
	long next(long lastExec) {
		if (!valid) {
			return Long.MAX_VALUE;
		}
		// 设定最后执行的时间点。
		lastUpdate = lastExec;
		// 设定秒 30 * * * * * * * 0
		int ss = tv[0];
		int mm = tv[1];
		int hh = tv[2];
		int dd = tv[3];
		// 计算周期性任务 deltaT
		if (cycle) {
			long deltaT = System.currentTimeMillis() - lastExec;
			long t = 1000L * (ss + 60L * (mm + 60 * (hh + dd * 24)));
			return t - deltaT;
		}
		next.setTimeInMillis(lastExec);
		now.setTimeInMillis(System.currentTimeMillis());

		// 置下一次先固定到上一个时标
		next.setTimeInMillis(lastUpdate);
		next.set(Calendar.MILLISECOND, 0);
		next.set(Calendar.SECOND, tv[SECOND]);
		// 处理带加号标识
		switch (cross) {
		case YEAR:
			return Y();
		case QUARTER:
			return Q();
		case MONTH:
			return M();
		case WEEK:
			return W();
		default:
			;// NOP
		}

		// 计算lastUpdate下一次对应的位置
		switch (asterisk) {
		// 如果全是星的情况，假设第一个分为0 * * ...
		case 0:
		case 1:
			// 设定分钟的情况
			if (next.getTimeInMillis() <= lastUpdate) {
				next.add(Calendar.MINUTE, 1);
			}
			break;
		case 2:
			// 设定分钟的情况
			next.set(Calendar.MINUTE, mm);
			if (next.getTimeInMillis() <= lastUpdate) {
				next.add(Calendar.HOUR_OF_DAY, 1);
			}
			break;
		case 3:
			// 设定小时/天
			next.set(Calendar.MINUTE, mm);
			next.set(Calendar.HOUR_OF_DAY, hh);
			if (next.getTimeInMillis() <= lastUpdate) {
				next.add(Calendar.DATE, 1);
			}
			break;
		case 4:
			// 设定日期
			next.set(Calendar.MINUTE, mm);
			next.set(Calendar.HOUR_OF_DAY, hh);
			next.set(Calendar.DATE, dd);
			if (next.getTimeInMillis() <= lastUpdate) {
				next.add(Calendar.MONTH, 1);
			}
		default:
			;// NOP
		}
		return next.getTimeInMillis() - System.currentTimeMillis();
	}

	private long Y() {
		next.set(Calendar.MONTH, tv[MONTH] - 1);
		next.set(Calendar.DATE, tv[DAY]);
		next.set(Calendar.HOUR_OF_DAY, tv[HOUR]);
		next.set(Calendar.MINUTE, tv[MINUTE]);
		if (next.getTimeInMillis() <= lastUpdate) {
			next.add(Calendar.YEAR, 1);
		}
		return next.getTimeInMillis() - System.currentTimeMillis();
	}

	private long Q() {
		Calendar lastExec = Calendar.getInstance();
		lastExec.setTimeInMillis(lastUpdate);
		next.set(Calendar.DATE, tv[DAY]);
		next.set(Calendar.HOUR_OF_DAY, tv[HOUR]);
		next.set(Calendar.MINUTE, tv[MINUTE]);

		// 移动到下一个 Quarter:Q=1,2,3,4
		next.set(Calendar.MONTH, (lastExec.get(Calendar.MONTH) / 3) * 3
				+ tv[MONTH]);
		if (next.getTimeInMillis() <= lastExec.getTimeInMillis()) {
			next.add(Calendar.MONTH, 3);
		}
		// 如果有月存在，加上月份，默認为0.
		next.add(Calendar.MONTH, tv[QUARTER]);
		return next.getTimeInMillis() - System.currentTimeMillis();
	}

	private long M() {
		next.set(Calendar.DAY_OF_MONTH, tv[DAY]);
		next.set(Calendar.HOUR_OF_DAY, tv[HOUR]);
		next.set(Calendar.MINUTE, tv[MINUTE]);

		if (next.getTimeInMillis() <= lastUpdate) {
			next.add(Calendar.MONTH, 1);
		}
		return next.getTimeInMillis() - System.currentTimeMillis();
	}

	private long W() {
		next.set(Calendar.HOUR_OF_DAY, tv[HOUR]);
		next.set(Calendar.MINUTE, tv[MINUTE]);
		// 修正，周一对应的 日期 为 1
		next.set(Calendar.DAY_OF_WEEK, tv[DAY] + 1);
		// 如果初次执行，当前时间与lastUpdate超过一周时，立即执行任务。
		if (next.getTimeInMillis() <= lastUpdate) {
			next.add(Calendar.WEEK_OF_YEAR, 1);
		}
		return next.getTimeInMillis() - System.currentTimeMillis();
	}

	public static int toInt(String s, int val) {
		if (s == null || s.length() == 0)
			return val;
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			// NOP
		}
		return val;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < CRONEXPR_LEN - 1; i++) {
			b.append(' ');
			if (i == asterisk && cross==UNDEF) {
				b.append(i > WEEK ? '+' : '*');
			} else if (i == cross) {
				b.append('+');
			} else if (i > cross) {
				b.append('*');
			} else if (cross == UNDEF && i > asterisk || i > asterisk
					&& tv[i] == 0) {
				b.append('*');
			} else {
				b.append(tv[i]);
			}
		}
		b.append(' ').append(this.cycle ? 1 : 0);
		if (!valid) b.append(" [X]");
		return b.substring(1);
	}
}
