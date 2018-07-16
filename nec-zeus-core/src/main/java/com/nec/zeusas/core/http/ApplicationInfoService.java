package com.nec.zeusas.core.http;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.google.common.io.Files;
import com.nec.zeusas.core.utils.AppContext;
import com.nec.zeusas.util.AppConfig;

@WebServlet(name = "applicationInfo", urlPatterns = { "/appinfo" }, loadOnStartup = 9)
public class ApplicationInfoService extends HttpServlet {
	static final Logger log = LoggerFactory.getLogger(ApplicationInfoService.class);

	final static ResourceBundle bundleConfig = ResourceBundle.getBundle("config");
	/* serialVersionUID */
	private static final long serialVersionUID = 1436938489714630323L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// 向所有Bean发送通知
		log.info("Send message to start all OnStartup");
		try {
			ApplicationContext ctx = AppContext.getApplicationContext();
			((AbstractApplicationContext) ctx).start();

			String pid = AppConfig.getPID();
			Files.write(pid.getBytes(),
					new File(System.getProperty("java.io.tmpdir"), ".pid"));
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("<b>It works!</b><br>");
		for (String key : bundleConfig.keySet()) {
			out.println(key + "=" + bundleConfig.getString(key) + "<br/>");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void destroy() {
		log.warn("ApplicationInfoService destory!");
	}
}
