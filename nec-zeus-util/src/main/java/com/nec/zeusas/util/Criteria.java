/**
 * 
 */
package com.nec.zeusas.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.nec.zeusas.lang.IJSON;

public class Criteria implements IJSON {

	public static final String NOT = "!";

	public static final String AND = "&&";

	public static final String OR = "||";

	public static final String EQ = "==";

	public static final String NE = "!=";

	public static final String GT = ">";

	public static final String GE = ">=";

	public static final String LT = "<";

	public static final String LE = "<=";

	public static final String IN = "(:";

	private String field;

	private String opr;

	private Object value;

	private List<Criteria> critera;

	public Criteria() {
	}

	private Criteria(String field, String operator, Object value) {
		this.field = field;
		this.opr = operator;
		this.value = value;
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Criteria equals(String field, Object value) {
		return new Criteria(field, EQ, value);
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Criteria notEqual(String field, Object value) {
		return new Criteria(field, NE, value);
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Criteria greaterThan(String field, Object value) {
		return new Criteria(field, GT, value);
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Criteria greaterOrEqual(String field, Object value) {
		return new Criteria(field, GE, value);
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Criteria lessThan(String field, Object value) {
		return new Criteria(field, LT, value);
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Criteria lessOrEqual(String field, Object value) {
		return new Criteria(field, LE, value);
	}

	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public static Criteria in(String field, Object... values) {
		Set<Object> list = new HashSet<Object>();
		list.addAll(Arrays.asList(values));
		return new Criteria(field, IN, list);
	}

	/**
	 * 
	 * @param subCriterions
	 * @return
	 */
	public static Criteria and(Criteria... subCriterions) {
		return build(AND, subCriterions);
	}

	/**
	 * 
	 * @param subCriterions
	 * @return
	 */
	public static Criteria or(Criteria... subCriterions) {
		return build(OR, subCriterions);
	}

	/**
	 * 
	 * @param criteria
	 * @return
	 */
	public static Criteria not(Criteria criteria) {
		return build(NOT, criteria);
	}

	private static Criteria build(String operator, Criteria... sub) {
		Criteria c = new Criteria();
		c.setOpr(operator);
		
		List<Criteria> list = new LinkedList<Criteria>();
		for (Criteria s : sub) {
			list.add(s);
		}
		c.setCritera(list);
		return c;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOpr() {
		return opr;
	}

	public void setOpr(String opr) {
		this.opr = opr;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the criterions
	 */
	public List<Criteria> getCritera() {
		return this.critera;
	}

	/**
	 * @param criterions
	 *            the criterions to set
	 */
	public void setCritera(List<Criteria> critera) {
		this.critera = critera;
	}
}
