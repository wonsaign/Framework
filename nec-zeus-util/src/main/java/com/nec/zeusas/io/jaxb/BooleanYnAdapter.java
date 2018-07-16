package com.nec.zeusas.io.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.base.Strings;

public class BooleanYnAdapter extends XmlAdapter<String, Boolean> {

	@Override
	public String marshal(Boolean b) throws Exception {
		return b == null ? "" : (b) ? "Y" : "N";
	}

	@Override
	public Boolean unmarshal(String b) throws Exception {
		return Strings.isNullOrEmpty(b) ? null //
				: ("Y".equals(b.trim()) ? Boolean.TRUE : Boolean.FALSE);
	}

}
