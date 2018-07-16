package com.nec.zeusas.common.service;

import java.util.Date;

import com.nec.zeusas.common.entity.IdGen;
import com.nec.zeusas.core.service.IService;
import com.nec.zeusas.exception.ServiceException;

public interface IdGenService extends IService<IdGen, String> {

	public Long generateNumericId(String seqId) throws ServiceException;

	public Long generateAutoId(String seqId) throws ServiceException;

	public String generateStringId(String seqId) throws ServiceException;

	public IdGen generateId(String seqId) throws ServiceException;

	public void lock(String seqId) throws ServiceException;

	public void unlock(String seqId) throws ServiceException;

	public String generateId(String seqId, Object... args) throws ServiceException;

	public String generateDateId(String seqId, Date date, Object... args) throws ServiceException;

	public String generateDateId(String seqId) throws ServiceException;
}
