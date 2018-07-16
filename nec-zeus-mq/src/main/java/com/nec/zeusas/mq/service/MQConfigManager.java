package com.nec.zeusas.mq.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.PublisherCallbackChannelImpl;

import com.nec.zeusas.common.service.OnStartApplication;
import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.exception.ServiceException;
import com.nec.zeusas.mq.bean.MQConfig;
import com.nec.zeusas.util.Dom4jUtils;
import com.nec.zeusas.util.IOUtils;
import com.rabbitmq.client.Channel;

/**
 *
 * ＭＱ配置管理工具<p>
 * 
 * @author zhensx
 * @date 2017年11月30日 下午8:04:29
 *
 */
public final class MQConfigManager extends OnStartApplication {

	final static Logger logger = LoggerFactory.getLogger(MQConfigManager.class);

	private final Map<String,   MQConfig> configs = new ConcurrentHashMap<>();
	private final Map<Class<?>, MQConfig> configClass = new ConcurrentHashMap<>();
	private final Map<String,   MQConfig> configRouting = new ConcurrentHashMap<>();

	private String config;

	public MQConfigManager() {
	}

	public MQConfigManager(String conf) {
		this.config = conf;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void load(File fconf) {
		try {
			InputStream in = new FileInputStream(fconf);
			load(in);
			in.close();
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * 启动加载
	 */
	public void load() {
		File fconf = new File(config);
		// 优先加载文件
		if (fconf.exists()) {
			logger.debug("Load MQ Configure file: {}", config);
			load(fconf);
			logger.debug("Load MQ Configure...OK!");
		} else {
			logger.warn("配置文件:{} 未发现, 尝试从 classpath 中加载....", config);
			logger.debug("从 classpath 中加载{}:...", config);
			InputStream is = IOUtils.getResourceAsStream(config);
			if (is != null) {
				load(is);
				IOUtils.close(is);
				logger.debug("Load MQ Configure...OK!");
			} else {
				logger.error("Not found configure file from classpath:{}", config);
			}
		}
	}

	final static String MQ_CONFIG = "mq-config";
	final static String MQ = "mq";

	@SuppressWarnings("unchecked")
	private void load(InputStream in) {
		Document doc = Dom4jUtils.getXmlDoc(in);

		Element root = doc.getRootElement();

		if (!MQ_CONFIG.equals(root.getName())) {
			throw new ServiceException("根节点必须是:" + MQ_CONFIG);
		}

		List<Element> mqconfigs = root.elements(MQ);
		for (Element e : mqconfigs) {
			MQConfig conf = new MQConfig(e);
			if (conf.getConsumer() == null) {
				// LOG WARNING
				continue;
			}
			configs.put(conf.getId(), conf);
			configClass.put(conf.getEntityClass(), conf);
			configRouting.put(conf.getRouteKey(), conf);
		}

	}

	public MQConfig getByRoutingKey(String key) {
		return configRouting.get(key);
	}

	public MQConfig get(String id) {
		return configs.get(id);
	}

	public MQConfig get(Class<?> entityClass) {
		return this.configClass.get(entityClass);
	}

	public Collection<MQConfig> values() {
		return configs.values();
	}

	@Override
	public void onStartLoad() {
		try {
			setup();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void setup() throws IOException, TimeoutException {
		// 创建连接和频道
		ConnectionFactory factory = AppContext.getBean(ConnectionFactory.class);

		Connection connection = factory.createConnection();

		Channel ch = connection.createChannel(false);
		PublisherCallbackChannelImpl channel = new PublisherCallbackChannelImpl(ch);

		for (MQConfig conf : values()) {
			channel.exchangeDeclare(conf.getExchanger(), "topic");
			channel.queueDeclare(conf.getQueue(), true, false, false, null);
			channel.queueBind(conf.getQueue(), conf.getExchanger(), conf.getPattern());

			if (conf.isListener()) {
				listening(conf, factory);
			}
		}
	}

	private void listening(MQConfig conf, ConnectionFactory factory) {
		try {
			SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
			container.setQueueNames(conf.getQueue());
			container.setConnectionFactory(factory);
			container.setMessageListener(conf.getConsumer().newInstance());
			container.setExposeListenerChannel(true);
			container.setMaxConcurrentConsumers(1);
			container.setConcurrentConsumers(1);
			container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
			container.start();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
