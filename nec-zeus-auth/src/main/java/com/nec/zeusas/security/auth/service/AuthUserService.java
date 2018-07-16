package com.nec.zeusas.security.auth.service;

import org.hibernate.service.spi.ServiceException;

import com.nec.zeusas.core.service.IService;
import com.nec.zeusas.security.auth.entity.AuthUser;

/**
 * 实现接口取得认证用户信息接口
 */
public interface AuthUserService extends IService<AuthUser, String>{
	AuthUser findByLoginName(String loginName)  throws ServiceException;
	void createAuthUser(AuthUser user, String passwd) throws ServiceException;
}
