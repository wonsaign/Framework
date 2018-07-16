package com.nec.zeusas.core.http;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nec.zeusas.core.entity.TaskTrace;
import com.nec.zeusas.core.task.TaskManagerService;
import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.http.HttpUtil;

@WebListener
public class OnContextlistener implements HttpSessionListener, //
		ServletContextListener, //
		HttpSessionAttributeListener {
	static Logger logger = LoggerFactory.getLogger(OnContextlistener.class);

	int count = 0;
	private TaskManagerService taskService = null;

	/**
	 * 当容器销毁时，执行此方法。
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (taskService != null) {
			taskService.shutdown();
		}
		taskService = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Runnable run = () -> {
			while (taskService == null) {
				try {
					Thread.sleep(30000L);
					taskService = AppContext.getBean(TaskManagerService.class);
				} catch (Exception e) {
					// NOP
				}
			}
		};
		Thread t = new Thread(run);
		t.start();
	}

	/**
	 * 用户登录或访问，创建Session
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		count++;
	}

	/**
	 * 离开时，session消亡
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		count--;
		HttpSession sess = event.getSession();
		logger.info("destroyed session id={}", sess.getId());
		HttpUtil.removeAllAttr(sess);
	}

	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		HttpSession session = event.getSession();
		Object obj = session.getAttribute(event.getName());
		if (obj instanceof TaskTrace) {
			((TaskTrace) obj).start();
		}
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		HttpSession session = event.getSession();
		String nm = event.getName();
		try {
			Object obj = session.getAttribute(nm);
			if (obj instanceof TaskTrace) {
				((TaskTrace) obj).release();
			}
		} catch (Exception e) {
			logger.warn("Remove task trace: {}", nm, e);
		}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {

	}
}
