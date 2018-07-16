package com.nec.zeusas.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.nec.zeusas.exception.ServiceException;

public class Hessian2Codec implements Codec {

	@Override
	public Object decodeAsClassObject(byte[] bb) {
		ByteArrayInputStream in = new ByteArrayInputStream(bb);
		Hessian2Input input = new Hessian2Input(in);
		try {
			return input.readObject();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public <T> T decode(byte[] bb, Class<T> cls) {
		ByteArrayInputStream in = new ByteArrayInputStream(bb);
		Hessian2Input input = new Hessian2Input(in);
		try {
			return (T) input.readObject();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public <T> T decode(String input, Class<T> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] encodeToBytes(Object obj) {
		return encodeAsClassObject(obj);
	}

	@Override
	public byte[] encodeAsClassObject(Object object) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Hessian2Output ho = new Hessian2Output(os);
		try {
			ho.writeObject(object);
			ho.flush();
			return os.toByteArray();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void write(Object object, OutputStream out) throws IOException {
		Hessian2Output ho = new Hessian2Output(out);
		try {
			ho.writeObject(object);
			ho.flush();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public String encodeToString(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T read(InputStream in, Class<T> targetClass) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
