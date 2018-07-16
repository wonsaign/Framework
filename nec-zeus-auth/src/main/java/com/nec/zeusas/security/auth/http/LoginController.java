package com.nec.zeusas.security.auth.http;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.common.base.Strings;
import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.http.HttpUtil;
import com.nec.zeusas.security.auth.entity.AuthUser;
import com.nec.zeusas.security.auth.entity.OrgUnit;
import com.nec.zeusas.security.auth.realm.AuthcUtils;
import com.nec.zeusas.security.auth.service.AuthCenterManager;
import com.nec.zeusas.security.auth.utils.VerifyCode;

public abstract class LoginController {
	static Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	final static String VERIFYCODE = "_VCODE_";
	final static String RESP_MESS = "message_login";
	
	/**
	 * 显示图片
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	protected void getVerifyCodeImage(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		// 设置成每次不变，不缓存的图片流
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);

		String vc = VerifyCode.random(VerifyCode.TYPE_NUM_ONLY, 4, null);
		req.getSession().setAttribute(VERIFYCODE, vc);

		resp.setContentType("image/jpeg");
		BufferedImage img = VerifyCode.getImageCode(vc, 90, 30, 3, true,
				Color.WHITE, Color.BLACK, null);
		OutputStream out = resp.getOutputStream();
		ImageIO.write(img, "JPEG", out);
		out.flush();
	}
	
	public boolean authUserLogin(HttpServletRequest request) {
		//已经登录
		if(!Strings.isNullOrEmpty(request.getRemoteUser())){
			return true;
		}
		//未登录
		String username = request.getParameter("username");
		String pwd = request.getParameter("password");
		// 通过MAC地址认证登陆实现
		String mac =request.getParameter("macaddr");
		if (pwd == null && mac == null) {
			request.setAttribute(RESP_MESS, "密码为空");
			return false;
		}
		pwd=pwd==null?mac:pwd;

		UsernamePasswordToken token;
		token = new UsernamePasswordToken(username, pwd);
		token.setRememberMe(false);

		boolean result = false;
		// 获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		try {
			currentUser.login(token);
			result = true;
			AuthCenterManager acm = AppContext.getBean(AuthCenterManager.class);
			AuthUser authUser = acm.getAuthUser(username);
			OrgUnit orgUnit = acm.getOrgUnitById(authUser.getOrgUnit());
			HttpSession sess = request.getSession();
			sess.setAttribute(AuthcUtils.SEC_AUTHUSER, authUser);
			sess.setAttribute(AuthcUtils.SEC_ORGUNIT, orgUnit);
		} catch (UnknownAccountException uae) {
			request.setAttribute(RESP_MESS, "未知账户");
		} catch (IncorrectCredentialsException ice) {
			request.setAttribute(RESP_MESS, "密码不正确");
		} catch (LockedAccountException lae) {
			request.setAttribute(RESP_MESS, "账户已锁定");
		} catch (ExcessiveAttemptsException eae) {
			request.setAttribute(RESP_MESS, "用户名或密码错误次数过多");
		} catch (AuthenticationException ae) {
			request.setAttribute(RESP_MESS, "用户名或密码不正确");
		} catch (Exception e){
			request.setAttribute(RESP_MESS, "未知账户");
		}
		// 验证是否登录成功
		if (!currentUser.isAuthenticated()) {
			token.clear();
		}
		
		return result;
	}

	/**
	 * 注销退出<p>
	 * 注销时，使用线程处理，实际发现阻塞
	 * @param request
	 * @return
	 */
	protected String logout(HttpServletRequest request) {
		final Subject subject = SecurityUtils.getSubject();
		// Remove Session
		HttpUtil.removeAllAttr(request.getSession());
		try {
			Future<Integer> future = Executors.newSingleThreadExecutor()
					.submit(() -> {
						subject.logout();
					}, new Integer(0));
			future.get(88, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.warn("Logout error {}", e.getMessage());
		}
		return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/";
	}
	
}
