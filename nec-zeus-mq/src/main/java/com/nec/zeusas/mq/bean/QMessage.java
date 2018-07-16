package com.nec.zeusas.mq.bean;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.alibaba.fastjson.JSON;

/**
 *
 * @author zhensx
 * @date 2017年11月30日 下午4:23:40
 *
 */
public class QMessage extends Message {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	static MessageProperties messageProperties = new MessageProperties();
	
	static {
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
	}

	private String routingKey;

	public QMessage(String routingKey, Object obj) {
		this(routingKey, JSON.toJSONBytes(obj));
	}

	public QMessage(String routingKey, byte[] body) {
		super(body, messageProperties);
		this.routingKey = routingKey;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
}
