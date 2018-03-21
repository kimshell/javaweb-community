package io.javaweb.community.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import io.javaweb.community.web.servlet.XssHttpServletRequest;

/**
 * 
 * xss过滤器
 * @author KevinBlandy
 *
 */
//@WebFilter(filterName = "xssFilter",urlPatterns = {
//			"/post/create","/post/update",
//			"/reply/create","/post/update"})
public class XssFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
		chain.doFilter(new XssHttpServletRequest((HttpServletRequest) request),response);
	}

	@Override
	public void destroy() {
		
	}
}
