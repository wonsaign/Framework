package demo;

import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

/**
 * 
 * @author zhensx
 *
 */
public interface MessageConsumer extends ChannelAwareMessageListener{
    
}
