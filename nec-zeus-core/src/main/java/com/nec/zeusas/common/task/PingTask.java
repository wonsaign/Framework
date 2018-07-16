package com.nec.zeusas.common.task;

import java.util.Date;
import java.util.Map;

/**
 * 任务访问多任务时的測試任务演示程序。
 *
 */
public final class PingTask extends CronTask {
	/**
	 * 任务执点
	 */
	public void exec() throws Exception {
		long x = System.currentTimeMillis();
		StringBuilder b = new StringBuilder();
		b.append(this.getName()).append(":")
			.append(new Date()).append('.').append(x%1000);
		for (Map.Entry<String,Object> k:super.data.entrySet()){
			b.append("<").append(k.getKey()).append(":").append(k.getValue()).append(">|");
		}
		b.append(" => [").append(x).append(']');
		System.out.println(b);
	}

	/**
	 * 是否准备就緒
	 */
	protected boolean ready() {
		return true;
	}
}
