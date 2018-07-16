package com.nec.zeusas.ms.service.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import com.nec.zeusas.ms.domian.SystemInfo;
import com.nec.zeusas.ms.service.SystemInfoService;


@Service
public class SystemInfoServiceImpl implements SystemInfoService{

	private final SystemInfo info = new SystemInfo();

	public SystemInfo get() {
		build();
		return info;
	}

	private void build() {
		Runtime runtime = Runtime.getRuntime();

		info.setAvailableProcessors(runtime.availableProcessors());
		info.setMaxMemory(runtime.maxMemory());
		info.setTotalMemory(runtime.totalMemory());
		info.setFreeMemory(runtime.freeMemory());

		File fs = new File(".");
		info.setTotalSpace(fs.getTotalSpace());
		info.setUsableSpace(fs.getUsableSpace());

	}
}
