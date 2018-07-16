package com.nec.zeusas.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

public abstract class OnStartApplication implements
		ApplicationListener<ContextStartedEvent> {
	
	static Logger logger = LoggerFactory.getLogger(OnStartApplication.class);

	@Override
	public final void onApplicationEvent(ContextStartedEvent event) {
		try {
			onStartLoad();
		} catch (Exception e) {
			logger.error("启动初始化错误！",e);
		}
	}

	public abstract void onStartLoad();
}
