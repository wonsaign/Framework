package com.nec.zeusas.io.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.nec.zeusas.util.DateTimeUtil;

public class DateAdapter extends XmlAdapter<String, Date> {
	private final SimpleDateFormat fmt = new SimpleDateFormat(DateTimeUtil.YYYY_MM_DD);

	@Override
	public Date unmarshal(String v) throws Exception {
		if (v == null || v.length() < 8) {
			return null;
		}
		return DateTimeUtil.toQDate(v);
	}

	@Override
	public String marshal(Date v) throws Exception {
		return v == null ? null : fmt.format(v);
	}

}
