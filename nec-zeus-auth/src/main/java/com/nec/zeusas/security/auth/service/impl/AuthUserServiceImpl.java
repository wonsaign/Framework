package com.nec.zeusas.security.auth.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.nec.zeusas.common.service.IdGenService;
import com.nec.zeusas.core.dao.Dao;
import com.nec.zeusas.core.service.BasicService;
import com.nec.zeusas.exception.ServiceException;
import com.nec.zeusas.security.auth.dao.AuthUserDao;
import com.nec.zeusas.security.auth.entity.AuthUser;
import com.nec.zeusas.security.auth.service.AuthUserService;
import com.nec.zeusas.security.auth.utils.DigestEncoder;

public class AuthUserServiceImpl extends BasicService<AuthUser, String> implements AuthUserService {
	static Logger logger = LoggerFactory.getLogger(AuthUserServiceImpl.class);

	final static String IDGEN_AUTHUSERID = "AUTHUSERID";

	@Autowired
	private AuthUserDao dao;
	@Autowired
	private IdGenService idGenService;

	@Transactional(readOnly = true)
	public AuthUser findByLoginName(String loginName) throws ServiceException {
		List<AuthUser> users = super.find("WHERE loginName=?", loginName);
		if (users.isEmpty()) {
			return null;
		}
		return users.get(0);
	}

	public void setDao(AuthUserDao dao) {
		this.dao = dao;
	}

	public void setIdGenService(IdGenService idGen) {
		this.idGenService = idGen;
	}

	@Override
	protected Dao<AuthUser, String> getDao() {
		return dao;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createAuthUser(AuthUser user, String passwd) throws ServiceException {
		// 登录名查重
		if (findByLoginName(user.getLoginName()) != null) {
			throw new ServiceException("登录名已经存在！");
		}
		if (passwd == null || passwd.length() < 5) {
			throw new ServiceException("密码为空或太短（最新要求4）！");
		}

		idGenService.lock(IDGEN_AUTHUSERID);
		try {
			String epass = DigestEncoder.encodePassword( //
					user.getLoginName(), passwd);
			user.setPassword(epass);
			String uid = idGenService.generateStringId(IDGEN_AUTHUSERID);
			user.setUid(uid);
			user.setLastUpdate(System.currentTimeMillis());
			user.setCreateTime(System.currentTimeMillis());

			save(user);
		} catch (Exception e) {
			throw new com.nec.zeusas.exception.ServiceException(e);
		} finally {
			idGenService.unlock(IDGEN_AUTHUSERID);
		}
	}
}
