package com.nec.zeusas.util;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.nec.zeusas.bean.QBeanUtil;

public final class QString {
	public final static String[] EMPTYS = new String[0];
	public static final Charset GBK = Charset.forName("GBK");
	public static final Charset UTF8 = Charset.forName("UTF-8");
	private static Charset DEFAULT_CHARSET;
	static {
		String default_charset = System.getProperty("file.encoding");
		DEFAULT_CHARSET = Charset.forName(default_charset);
	}
	
	private QString() {
	}

	public static String nullToEmpty(Object s) {
		if (s == null) {
			return "";
		}

		if (s instanceof CharSequence) {
			return (s.toString()).trim();
		}

		if (s instanceof Date) {
			Date d = (Date) s;
			long t = d.getTime();
			// 如果精确到小时，认为是YMD
			return (t % (1000 * 60 * 60) == 0) ? DateTimeUtil.format(DateTimeUtil.YYYY_MM_DD, d) : //
					DateTimeUtil.format(DateTimeUtil.YYYY_MM_DD_HMS, d);
		}

		if (s instanceof Double //
				|| s instanceof Float) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			decimalFormat.format(s);
		}

		return s.toString();
	}

	public static String trim(String s) {
		return s == null ? "" : s.trim();
	}

	public static boolean isNullOrEmpty(Object object) {
		return QBeanUtil.isEmpty(object);
	}
	
	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean contains(String s, String d) {
		if (Strings.isNullOrEmpty(d))
			return true;
		if (Strings.isNullOrEmpty(s))
			return false;
		return s.contains(d);
	}

	/**
	 * 装一个串转为整数
	 * 
	 * @param s
	 *            字符串
	 * @return 整数 或 NULL
	 */
	public static Integer toInt(CharSequence s) {
		if (s == null || s.length() == 0) {
			return null;
		}

		int v = 0;

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}

		for (int i = 1; i < b.length; i++) {
			v = v * 10 + b[i];
		}

		return (b[0] == -1) ? -v : v;
	}

	public static Byte toByte(String s) {
		if (s == null || (s = s.trim()).isEmpty()) {
			return null;
		}
		byte v = 0;

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}

		for (int i = 1; i < b.length; i++) {
			v = (byte) (v * 10 + b[i]);
		}

		return (byte) ((b[0] == -1) ? -v : v);
	}

	public static Short toShort(CharSequence s) {
		if (s == null || (s.length()==0)){
			return null;
		}

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}
		short v = 0;

		for (int i = 1; i < b.length; i++) {
			v = (short) (v * 10 + b[i]);
		}

		return (short) ((b[0] == -1) ? -v : v);
	}

	public static int toInt(CharSequence s, int def) {
		Integer val = toInt(s);
		return val == null ? def : val.intValue();
	}

	public static Float toFloat(CharSequence s) {
		if (s == null || (s.length()==0))
			return null;
		byte[] b = preprocFloat(s);
		if (b == null)
			return null;

		float v1 = 0;
		float v2 = 0;
		int i = 1;
		for (; i < b.length; i++) {
			if (b[i] == '.') {
				i++;
				break;
			}
			v1 = 10 * v1 + b[i];
		}

		if (i == b.length) {
			return b[0] == -1 ? -v1 : v1;
		}
		float pp = 0.1F;
		for (; i < b.length; i++) {
			v2 = v2 + pp * b[i];
			pp = 0.1F * pp;
		}
		float v = v1 + v2;
		return b[0] == -1 ? -v : v;
	}

	public static boolean toBoolean(String s) {
		if (s == null || (s = s.trim()).isEmpty()) {
			return false;
		}
		return ("true".equalsIgnoreCase(s)) //
				|| "yes".equalsIgnoreCase(s)//
				|| "1".equals(s);
	}

	public static Double toDouble(CharSequence s) {
		if (s == null)
			return null;

		byte[] b = preprocFloat(s);
		if (b == null)
			return null;

		double v1 = 0;
		double v2 = 0;
		int i = 1;
		for (; i < b.length; i++) {
			if (b[i] == '.') {
				i++;
				break;
			}
			v1 = 10 * v1 + b[i];
		}

		if (i == b.length) {
			return b[0] == -1 ? -v1 : v1;
		}
		double pp = 0.1;
		for (; i < b.length; i++) {
			v2 = v2 + pp * b[i];
			pp = 0.1 * pp;
		}
		double v = v1 + v2;
		return b[0] == -1 ? -v : v;
	}

	public static Long toLong(CharSequence s) {
		if (s == null) {
			return null;
		}

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}

		long v = 0;

		for (int i = 1; i < b.length; i++) {
			v = v * 10L + b[i];
		}

		return (b[0] == -1) ? -v : v;
	}

	public static long toLong(CharSequence s, long def) {
		Long val = toLong(s);
		return val == null ? def : val.longValue();
	}

	public static String[] split(String src, String regex) {
		if (src == null //
				|| (src = src.trim()).isEmpty()) {
			return new String[0];
		}
		return Splitter.onPattern(regex)//
				.omitEmptyStrings()//
				.trimResults()//
				.splitToList(src)//
				.toArray(EMPTYS);
	}

	/**
	 * 使用空格，:，|，;, 分隔为一个字符串数组
	 * 
	 * @param src
	 * @return
	 */
	public static String[] split(String src) {
		return split(src, "\\:|\\||\\,|\\s+");
	}

	public static boolean isNumeric(CharSequence charseq) {
		if (charseq == null || charseq.length() == 0) {
			return false;
		}

		for (int i = 0; i < charseq.length(); i++) {
			char c = charseq.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	/***************************************************************************
	 * 判断是否是中文字符
	 * 
	 * @version: July 13, 2012 11:05:58 AM
	 * @param c
	 * @return
	 */
	public static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		return (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS //
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS //
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A //
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION //
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION //
		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
	}

	/***************************************************************************
	 * 判断是否是中文
	 * 
	 * @Name: PinyinUtil.java
	 * @version: July 13, 2012 11:05:58 AM
	 * @param strName
	 * @return
	 */
	public static final boolean isChinese(String strName) {
		for (int i = 0; i < strName.length(); i++) {
			if (!isChinese(strName.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * 将串分拆，组合成Regular表达式。
	 * 
	 * @param target
	 * @return 正则表达式
	 */
	public static String toRegexpr(String target) {
		StringBuilder b = new StringBuilder(target.length());
		for (int i = 0; i < target.length(); i++) {
			char c = target.charAt(i);
			if (c == '\\' || c == '/' //
					|| c == ',' //
					|| c == ';' //
					|| c == '+' //
					|| c == '*') {
				b.append(' ');
			} else if (c == '.') {
				b.append("\\.");
			} else {
				b.append(c);
			}
		}
		String[] vv = b.toString().split("\\s+");
		b.setLength(0);
		for (String v : vv) {
			if (v.length() > 0)
				b.append('|').append(v);
		}
		return b.length() > 0 ? b.substring(1) : "";
	}

	/**
	 * 匹配任何一个的判断
	 * 
	 * @param src
	 * @param expr
	 * @return
	 */
	public static boolean matchAny(String src, Pattern expr) {
		return expr.matcher(src).find();
	}

	/**
	 * 最多分3
	 * 
	 * @param src
	 * @param ww
	 * @return
	 */
	public static boolean matchAll(String src, String... ww) {
		boolean b = true;
		switch (ww.length) {
		case 0:
			break;
		case 1:
			b = src.indexOf(ww[0]) > -1;
			break;
		case 2:
			b = src.indexOf(ww[0]) > -1 && src.indexOf(ww[1]) > -1;
			break;
		default:
			b = src.indexOf(ww[0]) > -1 && src.indexOf(ww[1]) > -1 && src.indexOf(ww[2]) > -1;
		}
		return b;
	}

	static byte[] preprocInt(CharSequence s) {
		assert s != null;
		int len;
		if (s == null || (len = s.length()) == 0) {
			return null;
		}
		byte[] b = new byte[20];

		int p = 1;
		complete: for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '+':
				if (b[0] != 0) {
					break complete;
				}
				b[0] = 1;
				break;
			case '-':
				if (b[0] != 0) {
					break complete;
				}
				b[0] = -1;
				break;
			case ' ':
				if (p > 1) {
					break complete;
				}
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				b[p++] = (byte) (0x0F & c);
				break;
			default:
				break complete;
			}
		}
		if (p == 1) {
			return null;
		}
		return Arrays.copyOf(b, p);
	}

	static byte[] preprocFloat(CharSequence s) {
		int len;
		if (s == null || (len = s.length()) == 0) {
			return null;
		}
		byte[] b = new byte[30];

		int p = 1;
		int point = 0;
		complete: for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '+':
				if (b[0] != 0) {
					break complete;
				}
				b[0] = 1;
				break;
			case '-':
				if (b[0] != 0) {
					break complete;
				}
				b[0] = -1;
				break;
			case ' ':
				if (p > 1) {
					break complete;
				}
				break;
			case '.':
				if (point == 1) {
					break complete;
				}
				b[p++] = '.';
				point = 1;
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				b[p++] = (byte) (0x0F & c);
				break;
			default:
				break complete;
			}
		}
		if (p == 1) {
			return null;
		}
		return Arrays.copyOf(b, p);
	}

	public static String format(String temp, Object... args) {
		if (args == null || args.length < 2)
			return temp;
		int idx = 0;
		StringBuilder b = new StringBuilder(temp.length() + 16);
		int i = 0;
		do_loop: while (i < temp.length() - 1) {
			char c = temp.charAt(i);
			if (c == '{') {
				do {
					if (idx < args.length) {
						if (temp.charAt(i + 1) == '}') {
							Object obj = args[idx++];
							if (obj instanceof Date) {
								b.append(DateTimeUtil.format((Date) obj));
							} else {
								b.append(obj);
							}
							i += 2;
							continue do_loop;
						} else if (temp.charAt(i + 1) == ' ') {
							i++;
						} else {
							break;
						}
					}
				} while (i < temp.length());
			}
			b.append(c);
			i++;
		}
		if (i < temp.length()) {
			b.append(temp.charAt(i));
		}
		for (i = idx; i < args.length; i++) {
			b.append(',').append(args[i]);
		}
		return b.toString();
	}
	
	public static byte[] toBytes(CharSequence cs) {
		String s = String.valueOf(cs);
		if (DEFAULT_CHARSET == UTF8) {
			return s.getBytes();
		}
		return s.getBytes(UTF8);
	}
	
	public static byte[] toBytes(CharSequence cs,Charset charSet) {
		String s = String.valueOf(cs);
		if (DEFAULT_CHARSET == charSet) {
			return s.getBytes();
		}
		return s.getBytes(charSet);
	}
}
