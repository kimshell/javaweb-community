package io.javaweb.community.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;

import io.javaweb.community.common.BaseInterceptor;
import io.javaweb.community.web.support.SessionHolder;
/**
 * 
 * 资源清理拦截器
 * @author Kevin
 *
 */
public class ResourceCleanInterceptor extends BaseInterceptor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCleanInterceptor.class);

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,String requestUri, Object handler) throws Exception {
		return true;
	}
	
	/**
	 * 
	 * 多个不相关请求,可能获取线程池中获取到同一个线程
	 * 必须要清理掉绑定当前线程的所有数据
	 * 
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)	throws Exception {
		//super.afterCompletion(request, response, handler, ex);
		SessionHolder.USER_SESSION.remove();
		SessionHolder.MANAGER_SESSION.remove();
		LOGGER.debug("Cleaning thread context resources.... ...");
	}
}
