package com.nec.zeusas.common.task;

import static com.nec.zeusas.core.task.ScheduleTask.executorService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import com.nec.zeusas.common.service.OnStartApplication;
import com.nec.zeusas.core.task.TaskManagerService;
import com.nec.zeusas.core.task.TaskScheduleBean;
import com.nec.zeusas.util.DateTimeUtil;

/**
 * <p>
 * 【任务调度主管理类】
 * </p>
 * 文件名：TaskManagerService.java
 */
public final class TaskManagerServiceImpl extends OnStartApplication //
		implements TaskManagerService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(TaskManagerService.class);
	// 停止标志
	private boolean stop = false;
	// 暂停标志
	private volatile boolean pause = false;

	// 任务Beans
	final List<TaskBean> taskBeans = new ArrayList<TaskBean>();

	@Inject
	private ApplicationContext appContext;

	/** 任务调度周期 */
	private final static long interval = 2000;

	// Task schedule thread
	private Thread thread;

	/**
	 * <p>
	 * 【任务调度方法】
	 * </p>
	 * 
	 * @throws
	 * 
	 * @date: Create on 2013-4-10 下午03:34:01
	 */
	protected final void scheduler() {
		if (this.taskBeans.isEmpty()) {
			this.stop = true;
			return;
		}

		// 设定当前任务时标，标定为当前时间的前一秒为最后更新时间
		for (TaskBean tb : this.taskBeans) {
			tb.setExecTime();
		}
		this.taskBeans.sort(Comparator.comparingLong(TaskBean::next));

		while (!this.stop) {
			if (this.taskBeans.isEmpty()) {
				break;
			}
			if (!this.pause) {
				for (TaskBean tb : this.taskBeans) {
					// 如果任务不可用，继续
					if (!tb.isValid()) {
						continue;
					}
					// 如果下一次执行时间大于0，退出执行
					if (tb.next() > 0) {
						break;
					}
					if (!tb.isRunning()) {
						doTask(tb);
					}
				}
			}
			// 执行了一轮后，重新排序
			this.taskBeans.sort(Comparator.comparingLong(TaskBean::next));
			TaskBean bean = this.taskBeans.get(0);
			try {
				// 取出第一个需要处理, 最长1M执行一次调度
				long waitTime = bean.next();
				if (waitTime > 0) {
					Thread.sleep(waitTime < interval ? waitTime : interval);
				}
			} catch (InterruptedException e) {
				logger.warn("TODO: 定时任务关闭！");
				this.stop = true;
				break;
			}
		}
	}

	private void doTask(final TaskBean tb) {
		tb.setExecTime();
		executorService.execute(() -> {
			Future<?> future;
			try {
				tb.setRunning(true);
				future = executorService.submit(tb.getCronTask(), new Integer(1000));
				if (future != null && tb.getWaitTime() != null) {
					future.get(tb.getWaitTime(), TimeUnit.MILLISECONDS);
				}
			} catch (Exception e) {
				logger.error("execture tesk: {}", tb, e);
			} finally {
				tb.setRunning(false);
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 通过扫描得到所有的认务
		this.appContext.getBeansWithAnnotation(TaskScheduleBean.class).forEach((name, bean) -> {
			if (bean != null && ((TaskBean) bean).getCronTask() != null) {
				logger.debug("初始化Bean:{}{}", name, bean);
				this.taskBeans.add((TaskBean) bean);
			}
		});
		((ArrayList<?>) this.taskBeans).trimToSize();
	}

	@Override
	public void onStartLoad() {
		logger.info("Start task scheduler...");
		thread = new Thread(() -> {
			scheduler();
		});
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void setPause(CronTask task, boolean pause) {
		taskBeans.stream()//
				.filter(e -> e.getCronTask().equals(task))//
				.forEach(e -> {
					e.getCronTask().setPause(pause);
				});
	}

	@Override
	public void shutdown() {
		if (!this.stop) {
			this.stop = true;
			this.taskBeans.clear();
			DateTimeUtil.waitMillis(2000);
		}
	}

	@Override
	public void setStop(boolean val) {
		stop = val;
	}

	@Override
	public void setPause(boolean p) {
		this.pause = p;
	}

	@Override
	public List<TaskBean> getTaskBeans() {
		return taskBeans;
	}
}
