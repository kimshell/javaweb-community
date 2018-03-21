package io.javaweb.community.web.servlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.alibaba.druid.support.http.StatViewServlet;

/**
 * druid监控servlet
 * @author KevinBlandy
 */
@WebServlet(urlPatterns = "/druid/*",initParams={@WebInitParam(name="loginUsername",value="KevinBlandy"), @WebInitParam(name="loginPassword",value="123456")})
public class DruidStatViewServlet extends StatViewServlet {
	private static final long serialVersionUID = -1625661402500921518L;
}