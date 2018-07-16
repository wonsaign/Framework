package com.nec.zeusas.io.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringTrimAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String v) throws Exception {
		return v == null ? null : v.trim();
	}

	@Override
	public String marshal(String v) throws Exception {
		return v == null ? null : v.trim();
	}

}
