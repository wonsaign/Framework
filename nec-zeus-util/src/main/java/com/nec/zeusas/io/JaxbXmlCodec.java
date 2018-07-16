package com.nec.zeusas.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 未测试
 * 
 */
public class JaxbXmlCodec implements Codec {
	private static Logger logger = LoggerFactory.getLogger(JaxbXmlCodec.class);

	public JaxbXmlCodec() {
	}

	@Override
	public <T> T decode(byte[] bb, Class<T> cls) {
		ByteArrayInputStream in = new ByteArrayInputStream(bb);
		return read(in, cls);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(String input, Class<T> cls) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(cls);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			StringReader reader = new StringReader(input);
			return (T) unmarshaller.unmarshal(reader);
		} catch (Exception e) {
			logger.error("解析{}错误", input, e);
		}
		return null;
	}

	@Override
	public byte[] encodeToBytes(Object obj) {
		assert obj != null;
		ByteArrayOutputStream out = new ByteArrayOutputStream(512);
		try {
			write(obj, out);
			return out.toByteArray();
		} catch (Exception e) {
			logger.error("{}", e.getMessage());
		}
		return null;
	}

	@Override
	public void write(Object obj, OutputStream out) {
		try {
			JAXBContext jc = JAXBContext.newInstance(obj.getClass());

			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(obj, out);
			out.flush();
		} catch (Exception e) {
			logger.error("{}", e.getMessage());
		}
	}

	@Override
	public String encodeToString(Object object) {
		assert object != null;
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());

			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			StringWriter out = new StringWriter();
			marshaller.marshal(object, out);
			return out.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(InputStream in, Class<T> targetClass) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(targetClass);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			return (T) unmarshaller.unmarshal(in);
		} catch (Exception e) {
			// NOP
		}
		return null;
	}

	@Override
	public Object decodeAsClassObject(byte[] bb) {
		throw new UnsupportedOperationException(
				"JaxbXmlCodec MUST specified target class.");
	}

	@Override
	public byte[] encodeAsClassObject(Object object) {
		throw new UnsupportedOperationException(
				"JaxbXmlCodec MUST specified target class.");
	}
}
