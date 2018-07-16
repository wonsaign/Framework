package com.nec.zeusas.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.nec.zeusas.common.dao.DictionaryDao;
import com.nec.zeusas.common.entity.Dictionary;
import com.nec.zeusas.common.service.DictionaryService;
import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;
import com.nec.zeusas.exception.ServiceException;

public class DictionaryServiceImpl //
		extends BasicService<Dictionary, String>//
		implements DictionaryService {

	@Autowired
	DictionaryDao dao;

	@Override
	protected Dao<Dictionary, String> getDao() {
		return dao;
	}

	public void setDao(DictionaryDao dao) {
		this.dao = dao;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Dictionary> getChildren(String DID) throws ServiceException {
		String Where = "WHERE pid=?";
		List<Dictionary> children = getDao().find(Where, DID);
		return children == null ? new ArrayList<Dictionary>() : children;
	}
}
