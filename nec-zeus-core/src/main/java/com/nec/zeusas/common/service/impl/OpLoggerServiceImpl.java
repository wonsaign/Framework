package com.nec.zeusas.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.nec.zeusas.common.dao.OpLoggerDao;
import com.nec.zeusas.common.entity.OpLogger;
import com.nec.zeusas.common.service.OpLoggerService;
import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;

public class OpLoggerServiceImpl extends BasicService<OpLogger, Long>
		implements OpLoggerService {
	
	@Autowired
	private OpLoggerDao dao;

	@Override
	protected Dao<OpLogger, Long> getDao() {
		return dao;
	}
	
	public void setDao(OpLoggerDao dao){
		this.dao = dao;
	}

	@Override
	public void save(OpLogger entity) {
		dao.save(entity);
	}
}
