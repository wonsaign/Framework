package com.nec.zeusas.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nec.zeusas.ms.domian.SystemInfo;
import com.nec.zeusas.ms.service.SystemInfoService;

@RestController
public class SystemInfoController {
	@Autowired
	private SystemInfoService systemInfoService;
	
	@RequestMapping(value = "/getSystemInfo",method = RequestMethod.GET)
	public SystemInfo getSystemInfo(){
		return systemInfoService.get();
	}
}
