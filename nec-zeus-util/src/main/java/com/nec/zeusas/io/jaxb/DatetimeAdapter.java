package com.nec.zeusas.io.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.nec.zeusas.util.DateTimeUtil;

public class DatetimeAdapter extends XmlAdapter<String, Date> {
	private final SimpleDateFormat fmt = new SimpleDateFormat(DateTimeUtil.YYYY_MM_DD_HMS);
	
	@Override
	public Date unmarshal(String v) throws Exception {
		if (v == null || v.length() < 10) {
			return null;
		}
		return DateTimeUtil.toQDatetime(v);
	}

	@Override
	public String marshal(Date v) throws Exception {
		return v==null?null:fmt.format(v);
	}
	
}
