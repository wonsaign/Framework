package com.nec.zeusas.common.dao;

import com.nec.zeusas.common.entity.IdGen;
import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.exception.DaoException;

public interface IdGenDao extends Dao<IdGen, String> {

	public Long generateAutoId(String seqId) throws DaoException;

	public IdGen generateId(String seqId) throws DaoException;
	
}