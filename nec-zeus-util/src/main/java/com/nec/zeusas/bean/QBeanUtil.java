package com.nec.zeusas.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nec.zeusas.util.TypeConverter;

public final class QBeanUtil {
	static Logger logger = LoggerFactory.getLogger(QBeanUtil.class);

	private QBeanUtil() {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setQField(Object target, Field field, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		// 空值也处理？
		if (value == null) {
			if (field.getType().isPrimitive() //
					|| Modifier.isFinal(field.getModifiers())) {
				return;
			}
			field.set(target, value);
			return;
		}

		// 如果是final
		if (Modifier.isFinal(field.getModifiers())) {
			Object targetVal = field.get(target);
			if (Collection.class.isAssignableFrom(field.getType())) {
				((Collection) targetVal).addAll((Collection) value);
			} else if (Map.class.isAssignableFrom(field.getType())) {
				((Map) targetVal).putAll((Map) value);
			} else {
				logger.warn("无法复制的类型。{}", field.getType());
			}
		} else {
			field.set(target, value);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setField(Object target, //
			Field field, //
			Object value) throws IllegalArgumentException, IllegalAccessException {
		if (value == null) {
			if (field.getType().isPrimitive() //
					|| Modifier.isFinal(field.getModifiers())) {
				return;
			}

			field.set(target, value);
		} else if (Modifier.isFinal(field.getModifiers())) {
			Object targetVal = field.get(target);
			if (Collection.class.isAssignableFrom(field.getType())) {
				((Collection) targetVal).addAll((Collection) value);
			} else if (Map.class.isAssignableFrom(field.getType())) {
				((Map) targetVal).putAll((Map) value);
			}
		} else {
			Class<?> type = field.getType();
			if (type.isAssignableFrom(value.getClass())) {
				field.set(target, value);
			} else {
				field.set(target, TypeConverter.toType(value, type));
			}
		}
	}

	/**
	 * 设用setter，设置属性的方法
	 * 
	 * @param m
	 *            method
	 * @param target
	 *            目标类
	 * @param value
	 *            值
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static void setProperty(Method m, Object target, Object value)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		assert m != null;
		Class<?> type = m.getParameterTypes()[0];

		if (value == null) {
			if (!type.isPrimitive()) {
				m.invoke(target, new Object[] { null });
			}
			return;
		}
		if (!type.isAssignableFrom(value.getClass())) {
			value = TypeConverter.toType(value, type);
		}
		if (value != null) {
			m.invoke(target, value);
		}
	}

	public static Class<?> forName(String className) {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			logger.warn("不存在Java类：{}", className);
			return null;
		}
	}

	public static <T> T newInstance(Class<T> type) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static void toUpperCase(String s[]) {
		for (int i = 0; i < s.length; i++)
			s[i] = s[i].toUpperCase();
	}

	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).isEmpty();
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).isEmpty();
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		return true;
	}

	/**
	 * 将数据转化为数组对象。
	 * @param data
	 * @param type
	 * @return
	 */
	public static <T> List<T> toRpcArray(Object data, Class<T> type) {
		List<T> array = new ArrayList<>();
		if (data instanceof JSONArray) {
			return ((JSONArray) data).toJavaList(type);
		}
		if (List.class.isAssignableFrom(data.getClass())) {
			for (Object v : (List<?>) data) {
				array.add(toRpcBean(v, type));
			}
		} else if (data.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(data); i++) {
				array.add(toRpcBean(Array.get(data, i), type));
			}
		} 
		return array;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T toRpcBean(Object v, //
			Class<T> type) {
		if (v == null || type.isAssignableFrom(v.getClass())) {
			return (T) v;
		} else if (v instanceof Map || v instanceof JSONObject) {
			return ((T) BeanMap.mapToBean((Map) v, type));
		}
		return (TypeConverter.toType(v, type));
	}
}
