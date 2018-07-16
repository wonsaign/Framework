package com.nec.zeusas.common.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.nec.zeusas.core.task.TaskScheduleBean;

/**
 * <p>
 * 【任务Bean】
 * </p>
 */
@TaskScheduleBean
public final class TaskBean implements Comparable<TaskBean> {

	static final Logger log = LoggerFactory.getLogger(TaskBean.class);

	/** 任务名称 */
	private String name;
	/** 调度任务Cron表达式 */
	final ArrayList<CronExpression> cronExpresion = new ArrayList<>();

	private CronTask cronTask;
	private Integer waitTime;
	private boolean valid;
	private boolean running;
	
	/** 最后更新时标 */
	volatile long lastUpdate;

	public TaskBean() {
		valid = true;
		waitTime = null;
		running = false;
		lastUpdate = System.currentTimeMillis()-500;
	}

	public Integer getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCronExpresion(List<String> cronExpresion) {
		for (String s : cronExpresion) {
			CronExpression c = new CronExpression(s);
			if (c.isValid()) {
				this.cronExpresion.add(c);
			}
		}
		this.cronExpresion.trimToSize();
		this.setValid(!this.cronExpresion.isEmpty());
	}

	public List<CronExpression> getCronExpression() {
		return this.cronExpresion;
	}

	public void setExecTime() {
		lastUpdate = System.currentTimeMillis();
		cronExpresion.forEach(s -> s.next(lastUpdate));
	}

	public long next() {
		if (cronExpresion.isEmpty()) {
			return Long.MAX_VALUE;
		}

		// 如果Bean定义为不可用、任务不可用、暂停、或正运行
		if (!valid //
				|| !cronTask.isValid() //
				|| cronTask.isPause() //
				|| cronTask.isRunning()) {
			return Long.MAX_VALUE;
		}
		
		long time = Long.MAX_VALUE;
		for (CronExpression s : cronExpresion) {
			long t0 = s.next();
			if (t0 < time) {
				time = t0;
			}
		}
		return time;
	}

	public CronTask getCronTask() {
		return cronTask;
	}

	public void setCronTask(CronTask cronTask) {
		this.cronTask = cronTask;
	}

	public long lastUpdate() {
		return lastUpdate;
	}

	public int hashCode(){
		return name==null?0:name.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof TaskBean)) {
			return false;
		}
		TaskBean b = (TaskBean) obj;

		return Objects.equal(this.cronTask, b.cronTask)//
				&& Objects.equal(this.cronExpresion, b.cronExpresion)//
				&& Objects.equal(this.name, b.name);
	}
	
	@Override
	public int compareTo(TaskBean o) {
		if (Objects.equal(this, o)) {
			return 0;
		}
		return (next() - o.next()) > 0 ? 1 : -1;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}

	public void setValid(boolean b) {
		this.valid = this.valid && b;
	}

	public boolean isValid() {
		return this.valid //
				&& cronTask != null //
				&& cronTask.isValid();
	}
}