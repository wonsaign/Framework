package com.nec.zeusas.mq.demo;

import com.nec.zeusas.mq.bean.MQResponse;
import com.nec.zeusas.mq.demo.bean.MyBean;
import com.nec.zeusas.mq.service.MessageConsumer;

/**
 *
 * @author zhensx
 * @date 2017年11月30日 下午8:40:32
 *
 */
public class DemoConsumer extends MessageConsumer<MyBean> {

	@Override
	public MQResponse recv(MyBean obj) {
		System.err.println("Consumer消费>>>" + obj.toString());
		return MQResponse.OK;
	}
}
