package com.nec.zeusas.io.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.nec.zeusas.util.DateTimeUtil;

/**
 * 解析yyyyMMdd生成日期格式
 * 
 * @author apple
 *
 */
public class DateYmdAdapter extends XmlAdapter<String, Date> {

	@Override
	public Date unmarshal(String v) throws Exception {
		if (v == null || v.length() != 8) {
			return null;
		}
		return DateTimeUtil.toQDate(v);
	}

	@Override
	public String marshal(Date v) throws Exception {
		if (v == null) {
			return null;
		}
		return String.valueOf(DateTimeUtil.toYMD(v));
	}
}
