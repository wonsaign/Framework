package com.nec.zeusas.security.auth.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.nec.zeusas.security.auth.entity.AuthUser;
import com.nec.zeusas.security.auth.entity.Group;
import com.nec.zeusas.security.auth.entity.OrgUnit;
import com.nec.zeusas.security.auth.entity.Role;

public interface AuthCenterManager {

	List<OrgUnit> findAllChildren(OrgUnit orgUnit);

	AuthUser verifyAuth(String username, String password);
	
	AuthUser getAuthUser(String name) throws ServiceException;

	OrgUnit getOrgUnitById(Integer id);

	OrgUnit getOrgUnitByCode(String cd);

	List<OrgUnit> getOrgUnitChildrenByCode(String cd);

	void updateOrgUnit(OrgUnit ou) throws ServiceException;

	void resetPassword(String loginId, String passwd) throws ServiceException, AuthentException;

	void changePassword(String loginId, String oldpwd, String passwd)
			throws ServiceException, AuthentException;

	void createAuthUser(AuthUser user, String passwd) throws ServiceException;

	List<Role> findAllRoles();

	Role getRole(String id);

	Group getGroup(Integer gid);

	List<Group> findAllGroups();

	List<AuthUser> getAuthUsersByOrg(Integer orgId);

	public void updateAuthUser(AuthUser u) throws ServiceException;

	public void updateRole(Role r) throws ServiceException;

	void loadGroup();

	void permSettings();

	void loadOrgUnit();

	void loadRole();

	void loadAuthUser();

	void loadAll();
	
	List<OrgUnit> findAllOrgUnit();
	
	Collection<AuthUser> findAllAuthUser();
}
