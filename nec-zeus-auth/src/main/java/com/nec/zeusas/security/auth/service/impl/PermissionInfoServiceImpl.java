package com.nec.zeusas.security.auth.service.impl;

import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;
import com.nec.zeusas.security.auth.dao.PermissionInfoDao;
import com.nec.zeusas.security.auth.entity.PermissionInfo;
import com.nec.zeusas.security.auth.service.PermissionInfoService;

public class PermissionInfoServiceImpl extends BasicService<PermissionInfo, Integer>
		implements PermissionInfoService {
	
	private PermissionInfoDao dao;
	
	@Override
	protected Dao<PermissionInfo, Integer> getDao() {
		return dao;
	}

	public void setDao(PermissionInfoDao dao){
		this.dao = dao;
	}
}
