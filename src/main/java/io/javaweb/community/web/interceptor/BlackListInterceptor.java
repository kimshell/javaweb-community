package io.javaweb.community.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;

import io.javaweb.community.common.BaseInterceptor;

/**
 * 
 * 黑名单过滤
 * @author KevinBlandy
 *
 */
public class BlackListInterceptor extends BaseInterceptor{

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,String requestUri, Object handler) throws Exception {
		return true;
	}

}
