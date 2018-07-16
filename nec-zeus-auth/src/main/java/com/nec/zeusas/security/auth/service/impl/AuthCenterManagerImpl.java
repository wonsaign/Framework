package com.nec.zeusas.security.auth.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.google.common.base.Strings;
import com.nec.zeusas.bean.BeanDup;
import com.nec.zeusas.common.service.OnStartApplication;
import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.http.QHttpClients;
import com.nec.zeusas.rpc.RpcRequest;
import com.nec.zeusas.rpc.RpcResponse;
import com.nec.zeusas.security.auth.entity.AuthUser;
import com.nec.zeusas.security.auth.entity.Group;
import com.nec.zeusas.security.auth.entity.OrgUnit;
import com.nec.zeusas.security.auth.entity.PermissionInfo;
import com.nec.zeusas.security.auth.entity.Role;
import com.nec.zeusas.security.auth.service.AuthCenterManager;
import com.nec.zeusas.security.auth.service.AuthUserService;
import com.nec.zeusas.security.auth.service.AuthentException;
import com.nec.zeusas.security.auth.service.GroupService;
import com.nec.zeusas.security.auth.service.OrgUnitService;
import com.nec.zeusas.security.auth.service.PermissionInfoService;
import com.nec.zeusas.security.auth.service.RoleService;
import com.nec.zeusas.security.auth.utils.DigestEncoder;
import com.nec.zeusas.util.AppConfig;

/**
 *  AuthCenterManager的一种实现。
 * 
 */
public class AuthCenterManagerImpl extends OnStartApplication implements AuthCenterManager {
	static Logger logger = LoggerFactory.getLogger(AuthCenterManagerImpl.class);

	private final Map<String, AuthUser> authUsers;
	private final Map<String, Role> roles;
	private final Map<Integer, Group> groups;
	private final Map<Integer, OrgUnit> orgUnitsById;
	private final Map<String, OrgUnit> orgUnitsByCode;
	private final Map<Integer, List<AuthUser>> authUserByOrg;
	
	@Autowired
	private GroupService groupService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private OrgUnitService orgUnitService;
	@Autowired
	private AuthUserService authUserService;
	@Autowired
	private PermissionInfoService permissionInfoService;

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public void setOrgUnitService(OrgUnitService orgUnitService) {
		this.orgUnitService = orgUnitService;
	}

	public void setAuthUserService(AuthUserService authUserService) {
		this.authUserService = authUserService;
	}

	public void setPermissionInfoService(PermissionInfoService permServ) {
		this.permissionInfoService = permServ;
	}

	public AuthCenterManagerImpl() {
		authUsers = new ConcurrentHashMap<>();
		groups = new ConcurrentHashMap<>();
		roles = new ConcurrentHashMap<>();
		orgUnitsById = new ConcurrentHashMap<>();
		orgUnitsByCode = new ConcurrentHashMap<>();
		authUserByOrg = new ConcurrentHashMap<>();
	}

	@Override
	public void loadGroup() {
		List<Group> gg = groupService.findAll();
		groupService.clear();
		groups.clear();
		for (Group g : gg) {
			groups.put(g.getGroupId(), g);
		}
	}

	@Override
	public Group getGroup(Integer gid) {
		return groups.get(gid);
	}

	@Override
	public List<Group> findAllGroups() {
		Collection<Group> gg = groups.values();
		List<Group> gl = new ArrayList<Group>(gg.size());
		gl.addAll(gg);
		return gl;
	}

	@Override
	public void loadRole() {
		List<Role> all = roleService.findAll();
		roleService.clear();
		roles.clear();
		for (Role r : all) {
			roles.put(r.getRid(), r);
		}
	}

	@Override
	public AuthUser verifyAuth(String username, String password) {
		if (username == null || password == null) {
			return null;
		}
		AuthUser user = null;
		try {
			user = getAuthUser(username);
			// 无密码为非法用户
			if (user == null || Strings.isNullOrEmpty(user.getPassword())) {
				return null;
			}

			String epass = DigestEncoder.encodePassword(username, password);
			if (user.getPassword().equals(epass)) {
				return user;
			}
			// FIXME: MAC地址现在不存密文
			if (user.getMacAddr() == null //
					|| (!user.getMacAddr().equals(password) && !user.getMacAddr().equals(epass))) {

				return null;
			}
		} catch (Exception e) {
			logger.error("verify user{}", username, e);
		}

		return user;
	}

	@Override
	public AuthUser getAuthUser(String name) throws ServiceException {
		AuthUser user = authUsers.get(name);
		if (user != null) {
			user.setLastUpdate(System.currentTimeMillis());
		}
		return user;
	}

	@Override
	public void loadOrgUnit() {
		// Clear all dirty data
		// Load all OrgUnit data to memory
		// 2017/14/04 Update DBFM009_层级关系同步 Start
//		 List<OrgUnit> all = orgUnitService.findAll();
		//FIXME:切换为从中台取 层级关系取全集 如果不取全集 树会断掉变成多棵树
		 List<OrgUnit> all = listOrgUnitFromMidplat();
		// 2017/14/04 Update DBFM009_层级关系同步 End
		orgUnitService.clear();
		orgUnitsById.clear();
		orgUnitsByCode.clear();

		for (OrgUnit u : all) {
			orgUnitsById.put(u.getOrgId(), u);
			orgUnitsByCode.put(u.getOrgCode(), u);
		}

		for (OrgUnit u : all) {
			Integer pid = u.getPid();
			if(pid==null){
				continue;
			}
			OrgUnit pOrg = orgUnitsById.get(pid);
			if (pOrg == null) {
				continue;
			}
			List<OrgUnit> children = pOrg.getChildren();
			if (children == null) {
				children = new ArrayList<OrgUnit>();
				pOrg.setChildren(children);
			}
			children.add(u);
		}

		for (OrgUnit u : all) {
			List<OrgUnit> c = u.getChildren();
			if (c != null) {
				((ArrayList<?>) c).trimToSize();
			}
		}
	}
	
	// 2017/14/04 ADD DBFM009_层级关系同步 Start
	private List<OrgUnit> listOrgUnitFromMidplat(){
		
		// FIXME：从中台取 更换为统一接口
		RpcRequest request = new RpcRequest("POS", "CORE_ORGUNIT");
		QHttpClients client = new QHttpClients();
		URI uri = QHttpClients.toURI(AppConfig.getString("DATA_API"), null);
		RpcResponse<?> rs = client.postAsObject(uri, request, RpcResponse.class);
		List<OrgUnit> allOrgUnit = rs.getArrayAs(OrgUnit.class);
		
		//原数据类型与level映射
		Map<Object, Integer> orgLevelMap = new HashMap<>();
		orgLevelMap.put("0", 10301);
		orgLevelMap.put("1", 10302);
		orgLevelMap.put("7", 10303);
		orgLevelMap.put("5", 10304);
		orgLevelMap.put("2", 10305);
		orgLevelMap.put("3", 10306);
		orgLevelMap.put("6", 10307);
		orgLevelMap.put("4", 10308);
		orgLevelMap.put("Z", 10309);
		
		Map<String, OrgUnit> pathMap=new HashMap<>(allOrgUnit.size()*4/3);
		//装配Map<路径, OrgUnit>
		for (OrgUnit orgUnit : allOrgUnit) {
			//装配map
			String path = orgUnit.getPath();
			pathMap.put(path, orgUnit);
			//level
			String type = orgUnit.getType();
			Integer level = orgLevelMap.get(type);
			orgUnit.setLevel(level);
			//status
			orgUnit.setStatus(isValid(orgUnit));
			orgUnit.setLastUpdate(System.currentTimeMillis());
		}
		//给pid赋值生成树结构
		for (OrgUnit orgUnit : allOrgUnit) {
			Integer orgId= orgUnit.getOrgId();
			//根节点
			if(1==orgId.intValue()){
				orgUnit.setPid(0);
				continue;
			}
			String parentPath=getParentPath(orgUnit.getPath());
			OrgUnit parentOrg = pathMap.get(parentPath);
			if(parentOrg==null){
				continue;
			}
			Integer pid=parentOrg.getOrgId();
			orgUnit.setPid(pid);
		}
		return allOrgUnit;
	} 
	
	/**
	 * 通过子路径算出父路径 不可修改
	 * 
	 * @param path
	 * @return
	 */
	private	static String getParentPath(String path) {
		int len = path.length() - 1;
		while ((path.charAt(--len)) != '/')
			;// NOP
		return path.substring(0, len + 1);
	}
	/**
	 * 装配数据时专用
	 * @param rec
	 * @return
	 */
	private static int isValid(OrgUnit org) {
		Integer status = org.getTestType();
		return ((org.getCommonName()).indexOf("撤") < 0)//
				&& "1".equals(org.getValidFlag()) //
				&& (status != null && status.intValue() == 0) ? 1 : 0;
	}
	// 2017/14/04 ADD DBFM009_层级关系同步 End

	private void findAll(OrgUnit orgUnit, List<OrgUnit> units) {
		List<OrgUnit> child = orgUnit.getChildren();
		if (child == null || child.isEmpty()) {
			return;
		}
		for (OrgUnit u : child) {
			units.add(u);
			findAll(u, units);
		}
	}

	@Override
	public List<OrgUnit> findAllChildren(OrgUnit orgUnit) {
		List<OrgUnit> units = new ArrayList<OrgUnit>();
		findAll(orgUnit, units);
		Collections.sort(units);
		return units;
	}

	/**
	 * 设定权限，并更新用户设定信息
	 */
	@Override
	public void permSettings() {
		List<PermissionInfo> pinfos = permissionInfoService.findAll();
		StringBuilder definitions = new StringBuilder(512);
		for (PermissionInfo pinfo : pinfos) {
			definitions.append(pinfo.toPerm());
		}
		definitions.append("/log* = anon\n");
		// XXX: 需要测试的的确认
		logger.debug(definitions.toString());

		ShiroFilterFactoryBean shiroBean;

		shiroBean = AppContext.getBean(ShiroFilterFactoryBean.class);
		shiroBean.setFilterChainDefinitions(definitions.toString());
	}

	@Override
	public OrgUnit getOrgUnitById(Integer id) {
		return orgUnitsById.get(id);
	}

	@Override
	public OrgUnit getOrgUnitByCode(String cd) {
		return orgUnitsByCode.get(cd);
	}

	@Override
	public List<OrgUnit> getOrgUnitChildrenByCode(String cd) {
		OrgUnit ou = orgUnitsByCode.get(cd);
		return ou == null ? null : ou.getChildren();
	}

	/**
	 * 业务更新
	 * 
	 * @param ou
	 */
	@Override
	public void updateOrgUnit(OrgUnit ou) throws ServiceException {
		// 1. update database
		OrgUnit orgUnit = orgUnitService.get(ou.getOrgId());
		Assert.notNull(orgUnit, "更新对象，必须存在！");
		// Update 更新字段
		orgUnit.setContact(ou.getContact());
		orgUnit.setMobile(ou.getMobile());
		orgUnit.setEmail(ou.getEmail());
		orgUnit.setAddress(ou.getAddress());
		orgUnit.setLastUpdate(System.currentTimeMillis());

		orgUnitService.update(orgUnit);

		// 2. update catch
		loadOrgUnit();
	}

	/**
	 * 业务更新
	 * 
	 * @param u
	 * @throws ServiceException
	 */
	@Override
	public void updateAuthUser(AuthUser u) throws ServiceException {
		AuthUser user = authUsers.get(u.getLoginName());
		AuthUser dbUser = authUserService.findByLoginName(u.getLoginName());

		AuthUser usertemp = new AuthUser();
		BeanDup.dup(user, usertemp);
		BeanDup.dup(u, usertemp);

		BeanDup.dup(usertemp, user, "commonName", "orgUnit", "groupId", "roles", "status", "type",
				"macAddr");
		BeanDup.dup(usertemp, dbUser, "commonName", "orgUnit", "groupId", "roles", "status",
				"type", "macAddr");

		user.setLastUpdate(System.currentTimeMillis());
		dbUser.setLastUpdate(System.currentTimeMillis());

		authUsers.put(user.getLoginName(), user);
		authUserService.update(dbUser);
		loadAuthUser();
	}

	/**
	 * 业务更新
	 * 
	 * @param u
	 * @throws ServiceException
	 */
	@Override
	public void updateRole(Role r) throws ServiceException {
		Role role = roles.get(r.getRid());
		Role DBrole = roleService.get(r.getRid());

		String summary = r.getSummary();
		if (summary != null) {
			role.setSummary(summary);
			DBrole.setSummary(summary);
		}
		String commonName = r.getCommonName();
		if (commonName != null) {
			role.setCommonName(commonName);
			DBrole.setCommonName(commonName);
		}
		String status = r.getStatus();
		if (status != null) {
			role.setStatus(status);
			DBrole.setStatus(status);
		}
		role.setLastUpdate(System.currentTimeMillis());
		DBrole.setLastUpdate(System.currentTimeMillis());

		roles.put(role.getRid(), role);
		roleService.update(DBrole);
	}

	/**
	 * 传入旧密码和新密码均为明文，加密后存储。
	 */
	@Override
	public void changePassword(String loginId, String oldpwd, String passwd)
			throws ServiceException, AuthentException {
		if (passwd == null || passwd.length() < 5) {
			throw new AuthentException("用户密码长度大于4位！");
		}

		AuthUser old = authUserService.findByLoginName(loginId);
		if (old == null) {
			throw new AuthentException("用户：【" + loginId + "】不存在！");
		}

		String encrypted = DigestEncoder.encodePassword(loginId, oldpwd);

		if (!old.getPassword().equals(encrypted)) {
			throw new AuthentException("旧密码 验证失败！");
		}

		String newEncrypted = DigestEncoder.encodePassword(loginId, passwd);
		old.setPassword(newEncrypted);
		old.setLastUpdate(System.currentTimeMillis());
		authUserService.update(old);

		loadAuthUser();
	}

	@Override
	public void resetPassword(String loginId, String passwd) throws ServiceException,
			AuthentException {
		AuthUser user = authUserService.findByLoginName(loginId);
		if (user == null) {
			throw new AuthentException("修正密码失败，用户未找到。longinID=" + loginId);
		}

		String newEncrypted = DigestEncoder.encodePassword(loginId, passwd);
		user.setPassword(newEncrypted);
		user.setLastUpdate(System.currentTimeMillis());
		authUserService.update(user);

		loadAuthUser();
	}

	@Override
	public void createAuthUser(AuthUser user, String passwd) throws ServiceException {
		if (getOrgUnitById(user.getOrgUnit()) == null) {
			throw new ServiceException("组织机构【" + user.getOrgUnit() + "】不存在！");
		}
		authUserService.createAuthUser(user, passwd);
		loadAuthUser();
	}

	@Override
	public Role getRole(String id) {
		return roles.get(id);
	}

	@Override
	public List<Role> findAllRoles() {
		List<Role> rr = new ArrayList<Role>(roles.size());
		rr.addAll(roles.values());
		return rr;
	}

	/**
	 * 取得组织机构下一级的节点用户。
	 * 
	 * @param orgId
	 *            组织机构ID
	 * @return
	 */
	@Override
	public List<AuthUser> getAuthUsersByOrg(Integer orgId) {
		return authUserByOrg.get(orgId);
	}

	/**
	 * 转入所有的用户，并按组织进行分组。
	 */
	@Override
	public void loadAuthUser() {
		List<AuthUser> all = authUserService.findAll();
		authUserService.clear();
		authUserByOrg.clear();
		for (AuthUser u : all) {
			authUsers.put(u.getLoginName(), u);
			Integer orgId = u.getOrgUnit();

			List<AuthUser> orgUsers = authUserByOrg.get(orgId);
			if (orgUsers == null) {
				orgUsers = new ArrayList<AuthUser>();
				authUserByOrg.put(orgId, orgUsers);
			}
			orgUsers.add(u);
		}

		for (List<?> u : authUserByOrg.values()) {
			((ArrayList<?>) u).trimToSize();
		}
	}

	@Override
	public void loadAll() {
		// 装入组织
		loadGroup();
		// 装入机构、单位
		loadOrgUnit();
		// 装入角色
		loadRole();
		// XXX： 设定权限
		permSettings();
		// 装入用户
		loadAuthUser();
	}

	@Override
	public void onStartLoad() {
		loadAll();
	}

	@Override
	public List<OrgUnit> findAllOrgUnit() {
		List<OrgUnit> orgs = new ArrayList<OrgUnit>(orgUnitsById.size());
		orgs.addAll(orgUnitsById.values());
		return orgs;
	}

	@Override
	public Collection<AuthUser> findAllAuthUser() {
		return this.authUsers.values();
	}
}
