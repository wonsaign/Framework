package com.nec.zeusas.core.task;

import java.util.List;

import com.nec.zeusas.common.task.CronTask;
import com.nec.zeusas.common.task.TaskBean;

public interface TaskManagerService {
	/**
	 * 关闭作务管理器，停止作伤。
	 */
	void shutdown();
	/**
	 * 取得所有的任务配置
	 * @return
	 */
	List<TaskBean> getTaskBeans();
	/**
	 * 设定暂停，如果设定了暂停状态，任务管理器不执行任何任务。
	 * @param pause 是否暂停
	 */
	void setPause(boolean pause);

	/**
	 * 针对某个任务，设定为暂停。
	 * @param task 指定任务
	 * @param pause 是否暂停状态
	 */
	void setPause(CronTask task, boolean pause);
	void setStop(boolean stop);
}
