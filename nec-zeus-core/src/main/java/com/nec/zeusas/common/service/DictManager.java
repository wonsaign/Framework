package com.nec.zeusas.common.service;

import java.util.List;
import java.util.Map;

import com.nec.zeusas.common.entity.Dictionary;

/**
 * 业务字典管理工具
 *
 */
public interface DictManager {
	/**
	 * 重新加载，并刷新字典
	 */
	void reload();

	/**
	 * 按名称取得字典
	 * 
	 * @param name
	 *            输入名称
	 * @return 还会找到的所有自动项
	 */
	List<Dictionary> lookupByName(String name);

	/**
	 * 追加字典
	 * 
	 * @param dict
	 */
	void add(Dictionary dict);

	/**
	 * 更新字典项
	 * 
	 * @param dict
	 */
	void update(Dictionary dict);

	/**
	 * 取得字典项
	 * 
	 * @param id
	 * @return
	 */
	Dictionary get(String id);

	/**
	 * 按类型取得字典
	 * 
	 * @param type
	 *            类型
	 * @param hardCode
	 *            字典的编码
	 * @return
	 */
	Dictionary lookUpByCode(String type, String hardCode);
	/**
	 * 根据名字在指定的类别里查找
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	List<Dictionary> lookupByName(String type, String name);
	/**
	 * 按顺序ID字典序排序
	 * 
	 * @param dicts
	 *            字典表
	 * @return 排序后的字典表
	 */
	List<Dictionary> sortBySeqID(List<Dictionary> dicts);

	Map<String, Dictionary> findAll();

	long lastUpdate();

}
