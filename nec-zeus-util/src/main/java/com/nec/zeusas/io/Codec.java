package com.nec.zeusas.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Codec {
	public Object decodeAsClassObject(byte[] bb);

	public <T> T decode(byte[] bb, Class<T> cls);

	public <T> T decode(String input, Class<T> cls);

	public byte[] encodeToBytes(Object obj);

	public byte[] encodeAsClassObject(Object object);

	public void write(Object obj, OutputStream out) throws IOException;

	public String encodeToString(Object object);

	public <T> T read(InputStream in, Class<T> targetClass) throws IOException;
}
