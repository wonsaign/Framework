package demo.impl;

import java.io.IOException;

import org.springframework.amqp.core.AmqpTemplate;

import demo.MessageProducer;

/**
 *
 * @author zhensx
 * @date 2017年11月28日 上午10:35:17
 *
 */

public class MessageProducerImpl implements MessageProducer {

	private AmqpTemplate amqpTemplate;

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	@Override
	public void sendMessage(String key, Object message) throws IOException {
		amqpTemplate.convertAndSend(key, message);
	}
}
