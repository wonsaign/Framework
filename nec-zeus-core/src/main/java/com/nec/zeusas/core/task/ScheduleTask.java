package com.nec.zeusas.core.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface ScheduleTask {
	int MAX_THREAD = 20;
	ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD);
	void exec() throws Exception;
}
