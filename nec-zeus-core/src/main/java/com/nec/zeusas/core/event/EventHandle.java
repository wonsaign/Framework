package com.nec.zeusas.core.event;

/**
 * 事件处理器
 *
 */
public interface EventHandle {

	/**
	 * 是否准备就绪，如果就绪，返回为true。
	 * @return 是否就绪
	 */
	default boolean isReady() {
		return true;
	}

	void handleEvent();
}
