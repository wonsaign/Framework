package com.nec.zeusas.mq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.alibaba.fastjson.JSON;
import com.nec.zeusas.mq.bean.MQConfig;
import com.nec.zeusas.mq.bean.MQResponse;
import com.rabbitmq.client.Channel;
import com.nec.zeusas.common.entity.OpLogger;
import com.nec.zeusas.common.service.OpLoggerService;
import com.nec.zeusas.common.service.impl.OpLoggerServiceImpl;
import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.exception.ServiceException;
import com.nec.zeusas.util.QString;

/**
 * 消费消息服务处理类
 * 
 * @author zhensx
 *
 */
public abstract class MessageConsumer<T> implements ChannelAwareMessageListener {

	final static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
	
	public MessageConsumer() {
	}

	@Override
	public final void onMessage(Message message, Channel channel) throws Exception {
		MQConfigManager manager = AppContext.getBean(MQConfigManager.class);
		String routingkey = message.getMessageProperties().getReceivedRoutingKey();
		MQConfig config = manager.getByRoutingKey(routingkey);
		String mesgObj = null;
		try {
			byte[] body = message.getBody();
			mesgObj = new String(body, QString.UTF8);
			@SuppressWarnings("unchecked")
			T Object = (T) JSON.parseObject(mesgObj, config.getEntityClass());
			recv(Object);

			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			// TODO LOG
		} catch (Exception e) {
			// 不放回队列？
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
			// TODO LOG?
			OpLogger op = new OpLogger("MQ", config.getEntityClass().getName(), routingkey, mesgObj);
			OpLoggerService opLoggerService = AppContext.getBean(OpLoggerServiceImpl.class);
			opLoggerService.save(op);

			logger.error("", e);
			throw new ServiceException(e);
		}
	}

	public abstract MQResponse recv(T message) throws Exception;
}
