package com.nec.zeusas.security.auth.service.impl;

import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;
import com.nec.zeusas.security.auth.dao.GroupDao;
import com.nec.zeusas.security.auth.entity.Group;
import com.nec.zeusas.security.auth.service.GroupService;

public class GroupServiceImpl extends BasicService<Group, Integer> implements GroupService {
	
	private GroupDao dao;

	@Override
	protected Dao<Group, Integer> getDao() {
		return dao;
	}
	
	public void setDao(GroupDao dao){
		this.dao = dao;
	}
}
