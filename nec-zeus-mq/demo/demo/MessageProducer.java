package demo;

import java.io.IOException;

/**
 *
 * @author zhensx
 * @date 2017年11月28日 上午11:22:07
 *
 */
public interface MessageProducer {
	/**
	 * 放置消息到消息队列
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String key, Object message) throws IOException;
}
