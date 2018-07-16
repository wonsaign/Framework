package com.nec.zeusas.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 数据库对应的实体接口
 * <p>
 * 要求所有数据库实体继承。 
 */
public interface IEntity extends Serializable {
	/**
	 * 默认函数 name()，根据注解，取得数据库表的本地名。
	 * @return 表名
	 */
	default String name() {
		// 优先看表的注解，查看name=""
		Table tb = getClass().getAnnotation(Table.class);
		String tbname = tb == null ? null : tb.name();
		if (tbname != null) {
			return tbname;
		}
		// 在查看Entity注解，查看name=""
		Entity e = getClass().getAnnotation(Entity.class);
		return e == null ? null : e.name();
	}
}
