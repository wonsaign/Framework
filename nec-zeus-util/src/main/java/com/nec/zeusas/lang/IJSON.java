package com.nec.zeusas.lang;

import com.alibaba.fastjson.JSON;

public interface IJSON {

	default String toJSON() {
		return JSON.toJSONString(this);
	}
}
