package com.nec.zeusas.lang;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.nec.zeusas.util.Dom4jUtils;

public interface IXML {
	
	default String toXML() {
		StringWriter sw = new StringWriter(512);
		JAXBContext context;
		Marshaller marshaller;
		try {
			context = JAXBContext.newInstance(getClass());
			marshaller = context.createMarshaller();
			// 格式化xml输出的格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			marshaller.marshal(this, sw);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return sw.toString();
	}

	@SafeVarargs
	static String element(String tag, String text, Entry<String, String>... args) {
		StringBuilder b = new StringBuilder(128);
		b.append('<').append(tag);
		for (Entry<String, String> e : args) {
			b.append(' ').append(e.key())//
					.append('=')//
					.append('\"').append(e.value())//
					.append('\"');
		}
		b.append('>')//
				.append(Dom4jUtils.xmlEscape(text))//
				.append('<').append('/').append(tag)//
				.append('>');
		return b.toString();
	}

	default IXML parseJaxbXML(String xml) {
		IXML xmlObject = null;
		JAXBContext jctx;
		Unmarshaller unmarshaller;
		try {
			jctx = JAXBContext.newInstance(getClass());
			// 进行将Xml转成对象的核心接口
			unmarshaller = jctx.createUnmarshaller();
			StringReader sr = new StringReader(xml);
			xmlObject = (IXML) unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return xmlObject;
	}
}
