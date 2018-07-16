package com.nec.zeusas.common.dao.impl;

import java.util.Calendar;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nec.zeusas.common.dao.HibernateCoreBasicDao;
import com.nec.zeusas.common.dao.IdGenDao;
import com.nec.zeusas.common.entity.IdGen;
import com.nec.zeusas.exception.DaoException;

public class IdGenDaoImpl extends HibernateCoreBasicDao<IdGen, String> //
		implements IdGenDao {
	static Logger log = LoggerFactory.getLogger(IdGenDaoImpl.class);

	@Override
	public Long generateAutoId(String seqId) throws DaoException {
		Session sess = super.openSession();
		Transaction trans = null;

		try {
			trans = sess.beginTransaction();
			IdGen idGen = sess.get(IdGen.class, seqId);
			if (idGen == null) {
				throw new DaoException("生成主键错误，seqid＝" + seqId);
			}
			Long id = idGen.getLastValue() + idGen.getStep();
			idGen.setLastValue(id);
			idGen.setLastUpdate(System.currentTimeMillis());
			sess.update(idGen);
			trans.commit();
			return id;
		} catch (Exception e) {
			if (trans != null) {
				trans.rollback();
			}
			throw new DaoException(e);
		} finally {
			if (sess != null) {
				sess.close();
			}
		}
	}

	@Override
	public IdGen generateId(String seqId) throws DaoException {
		Session sess = super.openSession();
		Transaction trans = null;
		try {
			trans = sess.beginTransaction();
			IdGen idgen = sess.get(IdGen.class, seqId);
			checkResetCycyle(idgen);

			long sno = idgen.getLastValue();
			sno += idgen.getStep();

			idgen.setLastUpdate(System.currentTimeMillis());
			idgen.setLastValue(sno);
			sess.update(idgen);
			trans.commit();
			return idgen.clone();
		} catch (Exception e) {
			if (trans!=null){
				trans.rollback();
			}
			log.error("主键{}生成错误。",seqId);
			throw new DaoException(e);
		} finally {
			if (sess != null) {
				sess.close();
			}
		}
	}

	private void checkResetCycyle(IdGen idgen) {
		Integer resetCycle = idgen.getResetCycle();
		if (resetCycle == null || resetCycle == 0) {
			return;
		}
		Calendar today = Calendar.getInstance();
		Calendar last = Calendar.getInstance();
		last.setTimeInMillis(idgen.getLastUpdate());
		boolean needReset = false;
		switch (resetCycle.intValue()) {
		case Calendar.YEAR:
			needReset = today.get(Calendar.YEAR) != last.get(Calendar.YEAR);
			break;
		case Calendar.MONTH:
			int YM0 = today.get(Calendar.YEAR) * 100//
					+ today.get(Calendar.MONTH);
			int YM1 = last.get(Calendar.YEAR) * 100 //
					+ last.get(Calendar.MONTH);
			needReset = YM0 != YM1;
			break;
		case Calendar.WEEK_OF_YEAR:
			int W1 = today.get(Calendar.YEAR) * 100 //
					+ today.get(Calendar.WEEK_OF_YEAR);
			int W2 = last.get(Calendar.YEAR) * 100 //
					+ last.get(Calendar.WEEK_OF_YEAR);
			needReset = W1 != W2;
			break;
		case Calendar.DAY_OF_YEAR:
		case Calendar.DATE:
		default:
			int YMD0 = today.get(Calendar.YEAR) * 10000//
					+ today.get(Calendar.MONTH) * 100//
					+ today.get(Calendar.DATE);
			int YMD1 = last.get(Calendar.YEAR) * 10000//
					+ last.get(Calendar.MONTH) * 100 //
					+ last.get(Calendar.DATE);
			needReset = YMD0 != YMD1;
		}
		// 如果需要复位？
		if (needReset) {
			idgen.setLastValue(idgen.getStart() - idgen.getStep());
		}
	}
}
