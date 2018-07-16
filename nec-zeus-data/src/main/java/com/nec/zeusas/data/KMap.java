package com.nec.zeusas.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KMap<K> {
	private final List<K> keys = new ArrayList<>();
	private final Map<K, Integer> indexes = new ConcurrentHashMap<>();

	public KMap() {
	}

	public KMap(K[] kk) {
		for (K k : kk) {
			if (k != null) {
				add(k);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public KMap(List<K> kk) {
		for (Object k : kk.toArray()) {
			if (k != null) {
				add((K) k);
			}
		}
	}

	public Integer add(K key) {
		Integer idx = keys.size();
		keys.add(key);
		indexes.put(key, idx);
		return idx;
	}

	public Integer getIndex(K key) {
		Integer idx = indexes.get(key);
		if (idx == null) {
			return add(key);
		}
		return idx;
	}

	public K getKey(Integer idx) {
		return idx.intValue() < keys.size() ? keys.get(idx) : null;
	}

	public int size() {
		return keys.size();
	}
}
