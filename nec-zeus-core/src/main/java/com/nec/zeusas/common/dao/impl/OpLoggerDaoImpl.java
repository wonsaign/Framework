package com.nec.zeusas.common.dao.impl;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nec.zeusas.common.dao.HibernateCoreBasicDao;
import com.nec.zeusas.common.dao.OpLoggerDao;
import com.nec.zeusas.common.entity.OpLogger;
import com.nec.zeusas.exception.DaoException;

public class OpLoggerDaoImpl extends HibernateCoreBasicDao<OpLogger, Long> //
		implements OpLoggerDao {
	static final Logger log = LoggerFactory.getLogger(OpLoggerDao.class);

	@Override
	public void save(OpLogger logger) {
		Session sess = super.openSession();
		Transaction t = null;
		try {
			t = sess.beginTransaction();
			sess.save(logger);
			t.commit();
		} catch (Exception e) {
			log.error("write business log error: info={}", logger, e);
			if (t != null) {
				t.rollback();
			}
		} finally {
			if (sess != null) {
				sess.close();
			}
		}
	}

	@Override
	public OpLogger update(OpLogger logger) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Long pkey, Map<String, Object> values)
			throws DaoException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Long[] ids) {
		throw new UnsupportedOperationException();
	}

}
