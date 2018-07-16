package com.nec.zeusas.mq.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;

/**
 *
 * @author zhensx
 * @date 2017年12月2日 上午10:02:18
 *
 */
public class ReturnCallBackListener implements ReturnCallback {

	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
			String routingKey) {
		// 处理失败后的情况
	}
}
