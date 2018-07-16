package com.nec.zeusas.core.event;

/**
 * POSIX 信号处理接口
 * 
 * @author apple
 *
 */
public interface BGSignalHandle {
	String INT = "INT"; //$NON-NLS-1$
	String TERM = "TERM"; //$NON-NLS-1$
	String HUP = "HUP"; //$NON-NLS-1$
	String ABRT ="ABRT";

	/**
	 * 当接收到POSIX信号
	 */
	void catchSignal();

	/**
	 * 处理理信号
	 */
	void handle();

	/**
	 * 后续处理
	 */
	void postHandle();

	/**
	 * 接受到信息更新处理（未处理？）
	 */
	void update();
}
