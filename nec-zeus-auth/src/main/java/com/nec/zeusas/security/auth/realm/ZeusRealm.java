package com.nec.zeusas.security.auth.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.security.auth.entity.AuthUser;
import com.nec.zeusas.security.auth.entity.OrgUnit;
import com.nec.zeusas.security.auth.service.AuthCenterManager;

/**
 * http://shiro.apache.org/spring.html
 */
public class ZeusRealm extends AuthorizingRealm {

	static Logger logger = LoggerFactory.getLogger(ZeusRealm.class);
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		String username = (String) getAvailablePrincipal(principals);
		SimpleAuthorizationInfo authInfo = null;
		
		AuthCenterManager authCenterManager = AppContext.getBean(AuthCenterManager.class);
		logger.debug("User name:{} Login...", username);
		try {
			AuthUser user = authCenterManager.getAuthUser(username);
			if (user == null) {
				logger.debug("User name:{} not found!", username);
				return authInfo;
			}
			authInfo = new SimpleAuthorizationInfo();
			if (user.getRoles() != null) {
				authInfo.addRoles(user.getRoles());
			}
			logger.debug("role:{}",user.getRoles());
		} catch (Exception e) {
			logger.error("认证错误！User:{},error:{}",username, e.getMessage(), e);
		}
		return authInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		if(token==null){
			throw new UnsupportedTokenException("身份令牌为空");
		}
		String username = (String) token.getPrincipal();
		Object credentials = token.getCredentials();
		if(credentials==null){
			throw new IncorrectCredentialsException("凭证为空");
		}
		String password = new String((char[]) credentials);
		AuthCenterManager acm = AppContext.getBean(AuthCenterManager.class);
		AuthUser authUser = acm.verifyAuth(username, password);
		if (authUser==null) {
			throw new IncorrectCredentialsException("");
		}
		SimpleAuthenticationInfo authInfo;
		authInfo = new SimpleAuthenticationInfo(username, password, username);

		OrgUnit ounit = acm.getOrgUnitById(authUser.getOrgUnit());
		AuthcUtils.setSession(AuthcUtils.SEC_ORGUNIT, ounit);
		AuthcUtils.setSession(AuthcUtils.SEC_AUTHUSER, authUser);
		
		return authInfo;
	}
}
