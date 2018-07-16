package com.nec.zeusas.mq.bean;

import static com.nec.zeusas.util.Dom4jUtils.getText;

import org.dom4j.Element;

import com.google.common.base.Objects;
import com.nec.zeusas.lang.IJSON;
import com.nec.zeusas.mq.service.MessageConsumer;
import com.nec.zeusas.util.Dom4jUtils;
import com.nec.zeusas.util.QString;

/**
 *
 * @author zhensx
 * @date 2017年11月30日 下午8:04:45
 *
 */
@SuppressWarnings("rawtypes")
public class MQConfig implements IJSON {
	// <mq id="CONFIG-ID">
	// <pattern>test.*</<pattern>
	// <queue>MQ_ID</queue>
	// <exchanger>exchanger</exchanger>
	// <entityClass></entityClass>
	// <listener>true</listener>
	// </mq>
	
	/** ID of the MQ */
	private String id;
	/** MQ 通配模式 */
	private String pattern;
	/** 队列名 */
	private String queue;
	/** 交换机 */
	private String exchanger;
	/** 保存实体 */
	private Class<?> entityClass;
	/** 路由键值*/
	private String routeKey;
	/** 是否是服务(Yes/No) */
	private boolean listener;

	private Class<MessageConsumer> consumer;

	public MQConfig() {
	}

	@SuppressWarnings("unchecked")
	public MQConfig(Element e) {
		id = Dom4jUtils.getAttr(e, "id");
		pattern = getText(e.element("pattern"));
		queue = getText(e.element("queue"));
		exchanger = getText(e.element("exchanger"));
		routeKey = getText(e.element("routeKey"));
		entityClass = forName(getText(e.element("entityClass")));
		consumer = (Class<MessageConsumer>) forName(getText(e.element("consumer")));
		listener = QString.toBoolean(getText(e.element("listener")));
	}

	public boolean isListener() {
		return listener;
	}

	public String getRouteKey() {
		return routeKey;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getExchanger() {
		return exchanger;
	}

	public void setExchanger(String exchanger) {
		this.exchanger = exchanger;
	}

	public Class<MessageConsumer> getConsumer() {
		return consumer;
	}

	@SuppressWarnings("unchecked")
	Class<? extends MessageConsumer> forName(String nm) {
		try {
			return (Class<MessageConsumer>) Class.forName(nm);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode() {
		return this.id == null ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return Objects.equal(this.id, ((MQConfig) obj).id);
	}

	public String toString() {
		return toJSON();
	}
}
