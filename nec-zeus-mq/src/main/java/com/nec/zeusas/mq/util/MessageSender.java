package com.nec.zeusas.mq.util;

import org.springframework.amqp.core.AmqpTemplate;

import com.nec.zeusas.mq.bean.MQConfig;
import com.nec.zeusas.mq.bean.QMessage;
import com.nec.zeusas.mq.service.MQConfigManager;

/**
 * Spring 注入服务
 * @author zhensx
 * @date 2017年11月30日 下午9:01:50
 *
 */
public final class MessageSender {
	private AmqpTemplate amqpTemplate;

	private MQConfigManager mqConfigManager;

	public void send(Object object) {
		// 取得MQ配置项
		MQConfig config = mqConfigManager.get(object.getClass());
		String routingKey = config.getRouteKey();
		QMessage qmesg = new QMessage(routingKey, object);
		amqpTemplate.convertAndSend(config.getExchanger(), routingKey, qmesg);
	}

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void setMqConfigManager(MQConfigManager mqConfigManager) {
		this.mqConfigManager = mqConfigManager;
	}
}
