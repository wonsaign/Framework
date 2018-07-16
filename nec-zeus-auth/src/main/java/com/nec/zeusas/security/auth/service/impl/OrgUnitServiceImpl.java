package com.nec.zeusas.security.auth.service.impl;

import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;
import com.nec.zeusas.security.auth.dao.OrgUnitDao;
import com.nec.zeusas.security.auth.entity.OrgUnit;
import com.nec.zeusas.security.auth.service.OrgUnitService;

public class OrgUnitServiceImpl extends BasicService<OrgUnit, Integer> implements OrgUnitService {
	private OrgUnitDao dao;
	
	@Override
	protected Dao<OrgUnit, Integer> getDao() {
		return dao;
	}

	public void setDao(OrgUnitDao dao){
		this.dao = dao;
	}
}
