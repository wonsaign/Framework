package com.nec.zeusas.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.nec.zeusas.exception.ServiceException;
import com.nec.zeusas.util.IOUtils;
import com.nec.zeusas.util.TypeConverter;

public class JavaObjectCodec implements Codec {

	@Override
	public Object decodeAsClassObject(byte[] bb) {
		if (bb == null || bb.length == 0) {
			return null;
		}
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new ByteArrayInputStream(bb));
			return input.readObject();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			IOUtils.close(input);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] bb, Class<T> cls) {
		if (bb == null || bb.length == 0) {
			return null;
		}
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new ByteArrayInputStream(bb));
			return (T) input.readObject();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			IOUtils.close(input);
		}
	}

	@Override
	public <T> T decode(String input, Class<T> cls) {
		return TypeConverter.toType(input, cls);
	}

	@Override
	public byte[] encodeToBytes(Object obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(out);
			output.writeObject(obj);
			output.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public byte[] encodeAsClassObject(Object object) {
		return encodeToBytes(object);
	}

	@Override
	public void write(Object obj, OutputStream out) throws IOException {
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(out);
			output.writeObject(obj);
			output.close();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public String encodeToString(Object object) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(InputStream in, Class<T> targetClass) throws IOException {
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(in);
			return (T) input.readObject();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			IOUtils.close(input);
		}
	}

}
