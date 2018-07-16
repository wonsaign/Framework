package demo.test;

import java.io.IOException;

import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import demo.impl.MessageProducerImpl;

/**
 *
 * @author zhensx
 * @date 2017年11月28日 上午10:49:29
 *
 */
public class Test {
	private ApplicationContext context = null;

	@Before
	public void setUp() throws Exception {
		context = new FileSystemXmlApplicationContext("config/test-mq-sender.xml");
	}

	@org.junit.Test
	public void test() throws IOException {
		
	MessageProducerImpl messageProducer = (MessageProducerImpl) context
				.getBean("messageProducer1");
	
		int a = 100;
	while (a > 0) {
			//messageProducer.sendMessage("a.01", "这是2");
			messageProducer.sendMessage("test.01", "I am amq sender num :" + a--);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		
	}

}
