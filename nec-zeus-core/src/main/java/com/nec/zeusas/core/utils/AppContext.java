package com.nec.zeusas.core.utils;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class AppContext implements InitializingBean, ApplicationContextAware {
	@Inject
	protected static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext aware) throws BeansException {
		ctx = aware;
	}

	public static <T> T getBean(Class<T> cls) {
		return ctx.getBean(cls);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName, Class<T> cls) {
		return (T) ctx.getBean(beanName);
	}

	public static Object getBean(String beanName) {
		return ctx.getBean(beanName);
	}

	/**
	 * 本部分在加载Properties后执行。
	 * 
	 * @return void
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}
