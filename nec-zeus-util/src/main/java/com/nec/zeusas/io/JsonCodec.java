package com.nec.zeusas.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;

public class JsonCodec implements Codec {

	@Override
	public <T> T decode(byte[] bb, Class<T> cls) {
		return (bb == null || bb.length == 0) ? null : //
				JSON.parseObject(bb, cls);
	}

	@Override
	public <T> T decode(String input, Class<T> cls) {
		return Strings.isNullOrEmpty(input) ? null : //
				JSON.parseObject(input, cls);
	}

	@Override
	public byte[] encodeToBytes(Object obj) {
		return JSON.toJSONBytes(obj);
	}

	@Override
	public void write(Object obj, OutputStream out) throws IOException {
		JSON.writeJSONString(out, obj);
	}

	@Override
	public String encodeToString(Object object) {
		return JSON.toJSONString(object);
	}

	@Override
	public <T> T read(InputStream in, Class<T> targetClass) throws IOException {
		return JSON.parseObject(in, targetClass);
	}

	@Override
	public Object decodeAsClassObject(byte[] bb) {
		return JSON.parse(bb);
	}

	@Override
	public byte[] encodeAsClassObject(Object object) {
		return encodeToBytes(object);
	}
}
