package com.nec.zeusas.mq.demo.bean;

import java.util.Date;

/** 
*
* @author zhensx 
* @date 2017年12月1日 下午6:44:18 
*
*/
public class MyBean {
	private int age = 100;
	private String name ="dasda";
	private Date date =new Date();
	@Override
	public String toString() {
		return "MyBean [age=" + age + ", name=" + name + ", date=" + date + "]";
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
 