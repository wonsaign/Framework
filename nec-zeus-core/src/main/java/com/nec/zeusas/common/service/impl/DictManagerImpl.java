package com.nec.zeusas.common.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.nec.zeusas.common.entity.Dictionary;
import com.nec.zeusas.common.service.DictManager;
import com.nec.zeusas.common.service.DictionaryService;
import com.nec.zeusas.common.service.OnStartApplication;

public class DictManagerImpl extends OnStartApplication implements DictManager {

	@Autowired
	private DictionaryService dictionaryService;

	/** type as cluster index */
	private final Map<String, Map<String, Dictionary>> type_dicts;
	/** 字典基于DID 索引 */
	private final Map<String, Dictionary> dicts;

	private final List<Dictionary> root;

	private long lastUpdate;

	public DictManagerImpl() {
		type_dicts = new ConcurrentHashMap<>();
		dicts = new ConcurrentHashMap<>();
		root = new ArrayList<>();
		lastUpdate = System.currentTimeMillis();
	}

	public void setDictionaryService(DictionaryService s) {
		this.dictionaryService = s;
	}

	/**
	 * 装入时处理
	 */
	@Override
	public void reload() {
		type_dicts.clear();
		root.clear();
		dicts.clear();
		lastUpdate = System.currentTimeMillis();
		List<Dictionary> all = dictionaryService.findAll();

		for (Dictionary dict : all) {
			dicts.put(dict.getDid(), dict);

			if (!dict.isActive()) {
				continue;
			}
			String type = dict.getType();
			Map<String, Dictionary> dd = type_dicts.get(type);
			if (dd == null) {
				dd = new LinkedHashMap<String, Dictionary>();
				type_dicts.put(type, dd);
			}
			dd.put(dict.getHardCode(), dict);

			if (dict.isRoot()) {
				root.add(dict);
			}
		}

		for (Dictionary dict : all) {
			if (dict.isRoot() || !dict.isActive()) {
				continue;
			}
			Dictionary pdict = dicts.get(dict.getPid());
			if (pdict != null) {
				pdict.addChild(dict);
			}
		}

		all.stream().filter(e -> e.getChildren() != null)//
				.forEach(e -> {
					List<Dictionary> cld = e.getChildren();
					Collections.sort(cld);
					((ArrayList<?>) cld).trimToSize();
				});
	}

	@Override
	public Dictionary get(String id) {
		return dicts.get(id);
	}

	public Map<String, Dictionary> lookUpByType(String type) {
		return type_dicts.get(type);
	}

	@Override
	public Dictionary lookUpByCode(String type, String code) {
		Map<String, Dictionary> m = type_dicts.get(type);
		if (m != null) {
			return m.get(code);
		}
		return null;
	}

	@Override
	public List<Dictionary> lookupByName(String type, String name) {
		Map<String, Dictionary> m = type_dicts.get(type);
		return m == null ? new ArrayList<Dictionary>(0) : m.values()//
				.stream()//
				.filter(e -> e.getName() != null //
						&& e.getName().contains(name))//
				.collect(Collectors.toList());
	}

	@Override
	public List<Dictionary> lookupByName(String name) {
		return dicts.values() //
				.stream() //
				.filter(e -> e.getName() != null //
						&& e.getName().contains(name))//
				.collect(Collectors.toList());
	}

	@Override
	public List<Dictionary> sortBySeqID(List<Dictionary> dicts) {
		if (dicts == null || dicts.isEmpty()) {
			return dicts;
		}
		return dicts.stream() //
				.sorted(Comparator.comparing(Dictionary::getSeqid)) //
				.collect(Collectors.toList());
	}

	public List<Dictionary> getRootDictionary() {
		return this.root;
	}

	@Override
	public void add(Dictionary dict) {
		dict.setLastUpdate(System.currentTimeMillis());
		dictionaryService.save(dict);
		reload();
	}

	@Override
	public void update(Dictionary dict) {
		dict.setLastUpdate(System.currentTimeMillis());
		dictionaryService.update(dict);
		reload();
	}

	@Override
	public void onStartLoad() {
		reload();
	}

	@Override
	public long lastUpdate() {
		return lastUpdate;
	}

	@Override
	public Map<String, Dictionary> findAll() {
		Map<String, Dictionary> dd = new HashMap<>();
		dd.putAll(this.dicts);
		return dd;
	}
}
