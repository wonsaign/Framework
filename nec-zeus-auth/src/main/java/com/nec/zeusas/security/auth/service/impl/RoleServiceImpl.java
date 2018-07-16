package com.nec.zeusas.security.auth.service.impl;

import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;
import com.nec.zeusas.security.auth.dao.RoleDao;
import com.nec.zeusas.security.auth.entity.Role;
import com.nec.zeusas.security.auth.service.RoleService;


public class RoleServiceImpl extends BasicService<Role, String> implements RoleService {

	private RoleDao dao;

	@Override
	protected Dao<Role, String> getDao() {
		return dao;
	}

	public void setDao(RoleDao dao){
		this.dao = dao;
	}
}
