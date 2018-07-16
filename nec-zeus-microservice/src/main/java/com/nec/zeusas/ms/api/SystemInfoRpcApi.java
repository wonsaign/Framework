package com.nec.zeusas.ms.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

import com.nec.zeusas.ms.service.SystemInfoService;

/**
 * 本程序实现RPC的一个DEMO 
 *
 */
@Configuration
public class SystemInfoRpcApi {

	@Autowired
	private SystemInfoService systemInfoService;

	@Bean(name = "/systemInfoRpc")
	public HessianServiceExporter systemInfoRpc() {
		HessianServiceExporter exporter = new HessianServiceExporter();
		exporter.setService(systemInfoService);
		exporter.setServiceInterface(SystemInfoService.class);
		return exporter;
	}

}