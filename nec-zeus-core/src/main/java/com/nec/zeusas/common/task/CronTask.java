package com.nec.zeusas.common.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.nec.zeusas.core.task.ScheduleTask;

/**
 * <p>
 * 【任务抽象类】
 * </p>
 */
public abstract class CronTask implements ScheduleTask, Runnable {
	/**
	 * 任务的执行体。
	 * <p>
	 * 
	 * @throws Exception
	 */
	static final Logger log = LoggerFactory.getLogger(CronTask.class);
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * 任务名称
	 */
	protected String name = getClass().getSimpleName();

	// 开始时间
	protected volatile long startTime;

	protected int waitTime = -1;
	protected long threadId;
	protected boolean valid;
	protected boolean pause;
	private boolean running;
	/**
	 * 传递参数值
	 */
	protected final Map<String, Object> data = new HashMap<String, Object>();

	public CronTask() {
		valid = true;
		pause = false;
		running = false;
	}

	public long getTheadId() {
		return threadId;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param taskName
	 */
	public void setName(String taskName) {
		if (taskName != null) {
			this.name = taskName;
		}
	}

	public Object getData(String key) {
		return data.get(key);
	}

	public void addData(String key, Object value) {
		data.put(key, value);
	}

	/**
	 * 将data的所有数据，放入任务对象中。
	 * 
	 * @param data
	 */
	public void addData(Map<String, Object> param) {
		this.data.putAll(param);
	}

	/**
	 * 任务是否准备就绪，如果准备就绪，返回true,如果返回为false,将不执行exec
	 * 
	 * @return
	 */
	protected boolean ready() {
		return isValid();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = this.valid && valid;
	}

	public boolean isRunning() {
		return running;
	}

	public final void run() {
		// 如果有效的情况, 并且不是暂停状态
		running = true;
		if (valid && !pause) {
			try {
				if (ready() && lock.tryLock(5, TimeUnit.SECONDS)) {
					// 如果准备就绪的话，就执行。
					threadId = Thread.currentThread().getId();
					startTime = System.currentTimeMillis();
					this.exec();
					startTime = 0;
				}
			} catch (Exception e) {
				log.error("执行任务：{}失败。", name, e);
			} finally {
				// 如果是当前线程锁定
				if (lock.isHeldByCurrentThread()) {
					lock.unlock();
				}
			}
		}
		running = false;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof CronTask)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		CronTask ano = (CronTask) obj;
		return Objects.equal(name, ano.name) && Objects.equal(waitTime, ano.waitTime)
				&& Objects.equal(data, ano.data);
	}
}
