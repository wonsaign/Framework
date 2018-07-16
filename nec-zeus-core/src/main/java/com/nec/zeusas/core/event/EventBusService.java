package com.nec.zeusas.core.event;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import com.google.common.eventbus.EventBus;

public class EventBusService implements InitializingBean {
	static Logger logger = LoggerFactory.getLogger(EventBusService.class);

	private String type = "";

	final EventBus eventBus = new EventBus();

	@Inject
	private ApplicationContext appContext;

	public void post(EventHandle event) {
		if (event.isReady()) {
			this.eventBus.post(event);
		}
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		// 通过扫描得到所有的 EventAdapter
		this.appContext.getBeansWithAnnotation(EventAdapter.class).forEach((name, bean) -> {
			logger.info("register event handle{} - {}", name, bean.getClass());
			EventAdapter eh = bean.getClass().getAnnotation(EventAdapter.class);
			if (this.type.equals(eh.type())) {
				
				this.eventBus.register(bean);
			}
		});
	}

	public void setType(String type) {
		this.type = type;
	}
}
