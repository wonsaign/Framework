package com.nec.zeusas.mq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;

/** 
*
* @author zhensx 
* @date 2017年12月2日 上午10:00:49 
*
*/

public class ConfirmCallBackListener implements ConfirmCallback {
	final static Logger logger = LoggerFactory.getLogger(ConfirmCallBackListener.class);

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		// 只确认生产者消息发送成功，消费者是否处理成功不做保证
	
	}
}
 