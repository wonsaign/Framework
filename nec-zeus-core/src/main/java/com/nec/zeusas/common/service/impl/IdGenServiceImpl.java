package com.nec.zeusas.common.service.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.nec.zeusas.common.dao.IdGenDao;
import com.nec.zeusas.common.entity.IdGen;
import com.nec.zeusas.common.service.IdGenService;
import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;
import com.nec.zeusas.exception.ServiceException;

/**
 * Transaction: ==> begin transaction IdGenService.lock(SEQID); try { } catch
 * (ServiceException e) { throw e; } catch( Excetion e) { throw new
 * ServiceException(e); // rollBack(); } finally { IdGenService.unlock(SEQID); }
 * commit:
 */
public class IdGenServiceImpl extends BasicService<IdGen, String> //
		implements IdGenService {

	@Autowired
	private IdGenDao dao;

	private final Map<String, ReentrantLock> locks = new HashMap<String, ReentrantLock>();

	@Override
	protected Dao<IdGen, String> getDao() {
		return dao;
	}

	public void setDao(IdGenDao dao){
		this.dao = dao;
	}

	/**
	 * 生成数字ＩＤ
	 * @param seqId 序列号ID
	 * @return 返回Long型序列号
	 */
	@Override
	public Long generateNumericId(String seqId) throws ServiceException {
		IdGen idVal = generateId(seqId);
		return idVal.getLastValue();
	}
	
	@Override
	public Long generateAutoId(String seqId) throws ServiceException {
		lock(seqId);
		try {
			IdGen idVal = generateId(seqId);
			return idVal.getLastValue();
		} finally {
			unlock(seqId);
		}
	}
	
	/**
	 * 生成串类型序列号
	 * 如果设字格式，按给定的格式格式化。
	 * 
	 * @param seqId
	 * 
	 */
	@Override
	public String generateStringId(String seqId) throws ServiceException {
		IdGen idVal = generateId(seqId);
		String fmt = idVal.getIdFormat();
		if (Strings.isNullOrEmpty(fmt)) {
			return idVal.getLastValue().toString();
		}
		return MessageFormat.format(fmt, idVal.getLastValue());
	}

	@Override
	public String generateId(String seqId, Object ...args) throws ServiceException {
		IdGen idVal = generateId(seqId);
		String fmt = idVal.getIdFormat();
		return MessageFormat.format(fmt, idVal.getLastValue(), args);
	}

	@Override
	public String generateDateId(String seqId,Date date, Object ...args) throws ServiceException {
		IdGen idVal = generateId(seqId);
		String fmt = idVal.getIdFormat();
		return MessageFormat.format(fmt, idVal.getLastValue(), date, args);
	}
	
	@Override
	public String generateDateId(String seqId) throws ServiceException {
		IdGen idVal = generateId(seqId);
		String fmt = idVal.getIdFormat();
		return MessageFormat.format(fmt, idVal.getLastValue(), new Date());
	}
	
	@Override
	public void lock(String seqId) throws ServiceException {
		ReentrantLock lock = locks.get(seqId);
		if (lock == null) {
			lock = new ReentrantLock();
			locks.put(seqId, lock);
		}
		try {
			if (!lock.tryLock(500, TimeUnit.MILLISECONDS)) {
				throw new ServiceException("");
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void unlock(String seqId) {
		ReentrantLock lock = locks.get(seqId);
		if (lock != null && lock.isHeldByCurrentThread()) {
			lock.unlock();
		}
	}

	@Override
	public IdGen generateId(String seqId) throws ServiceException {
		return dao.generateId(seqId);
	}

}
