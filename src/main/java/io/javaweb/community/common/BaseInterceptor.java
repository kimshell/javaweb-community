package io.javaweb.community.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author KevinBlandy
 *
 */
public abstract class BaseInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
		

		if(CorsUtils.isPreFlightRequest(request)) {
			return true;
		}
		
		String uri = request.getRequestURI();
		int index = uri.indexOf(request.getContextPath());
		if(index != -1){
			//request uri
		    uri = uri.substring(request.getContextPath().length());
        }
		
		HandlerMethod handlerMethod = null;
		if(handler instanceof HandlerMethod) {
			//method
			handlerMethod = (HandlerMethod) handler;
		}
		
		return this.preHandle(request, response, handlerMethod,uri,handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
		
	}
	
	/**
	 * 清理
	 * @param request
	 * @param response
	 * @param handler
	 * @param ex
	 * @throws Exception
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)throws Exception {
		//SessionHolder.USER_SESSION.remove();
	}
	
	/**
	 * @param request
	 * @param response
	 * @param handlerMethod
	 * @return
	 */
	abstract protected boolean preHandle(HttpServletRequest request,HttpServletResponse response,HandlerMethod handlerMethod,String requestUri,Object handler)throws Exception ;
}
