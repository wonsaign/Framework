package demo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import com.rabbitmq.client.Channel;

import demo.MessageConsumer;

/** 
*
* @author zhensx 
* @date 2017年11月28日 上午10:36:59 
*
*/
public class MessageConsumerImpl implements MessageConsumer{
	private Logger logger = LoggerFactory.getLogger(MessageConsumer.class); 
	
	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		try {
			System.err.println("收到"+message);
			logger.info("shijj receive message------->:{}", message);  
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			//退回消息
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
			//扔掉消息
			//channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
			//批量退回或删除,中间的参数 是否批量 true是/false否 (也就是只一条)
			//channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
			//补发消息 true退回到queue中,false只补发给当前queue
			//channel.basicRecover(true);
			e.printStackTrace();
		}
		
	}

	
}
 