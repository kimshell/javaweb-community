package io.javaweb.community.web.interceptor;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.javaweb.community.annotation.IgnoreAccountStatus;
import io.javaweb.community.annotation.IgnoreEmailVerifi;
import io.javaweb.community.annotation.IgnoreSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.common.BaseInterceptor;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.enums.Status;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.WebUtils;
import io.javaweb.community.web.support.SessionHolder;
import io.javaweb.community.web.support.UserSession;
/**
 * 
 * 用户会话拦截器
 * @author KevinBlandy
 *
 */
public class UserSessionInterceptor extends BaseInterceptor {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,String requestUri, Object handler) throws Exception {
		if(handlerMethod.hasMethodAnnotation(IgnoreSession.class)) {
			return true;
		}
		String cookieValue = WebUtils.getCookieValue(request, CookieKeys.USER_SESSION);
		if(GeneralUtils.isEmpty(cookieValue)) {
			//未登录
			this.sessionOut(request, response, Messages.NOT_LOGGED_IN);
			return false;
		}
		String jsonValue = this.stringRedisTemplate.opsForValue().get(RedisKeys.SESSION_USER + cookieValue);
		if(GeneralUtils.isEmpty(jsonValue)) {
			//会话过期
			this.sessionOut(request, response, Messages.NOT_LOGGED_IN);
			return false;
		}
		
		UserSession userSession = JSON.parseObject(jsonValue, UserSession.class);

		
		//尝试从db检索用户信息
		UserEntity userEntity = this.userService.queryByPrimaryKey(userSession.getUser().getUserId());
		if(userEntity == null) {
			// 用户未持久化,删除cookie
			
			this.stringRedisTemplate.delete(RedisKeys.SESSION_USER + cookieValue);
			Cookie cookie = new Cookie(CookieKeys.USER_SESSION,cookieValue);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
			
			this.sessionOut(request, response, Messages.NOT_LOGGED_IN);
			return false;
		}
		
		if(!handlerMethod.hasMethodAnnotation(IgnoreAccountStatus.class)) {
			if(!userEntity.getStatus().equals(Status.NORMAL)) {
				//账户被禁用
				throw new ServiceException("无法完成操作,账户被禁用",Message.Status.ACCOUNT_IS_DISABLED);
			}
		}
		if(!handlerMethod.hasMethodAnnotation(IgnoreEmailVerifi.class)) {
			if(!userEntity.getEmailVerifi()) {
				//没有经过邮箱认证
				throw new ServiceException("无法完成操作,邮箱未认证",Message.Status.EMAIL_NOT_VERIFY);
			}
		}
	
		userSession.setUser(userEntity);
		
		if(userSession.getRemenber()) {
        	 // redis 缓存session 7 天
            this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_USER + cookieValue, JSON.toJSONString(userSession), 7,TimeUnit.DAYS);
        }else {
        	// redis 缓存session 30分钟
        	 this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_USER + cookieValue, JSON.toJSONString(userSession), 30,TimeUnit.MINUTES);
        }
		
		SessionHolder.USER_SESSION.set(userSession);
		return true;
	}
	
	private void sessionOut(HttpServletRequest request,HttpServletResponse response,Message<?> message)throws Exception {
		if(WebUtils.isAjaxRequest(request)) {
			WebUtils.responseToJson(message, response);
		}else {
			String url = request.getContextPath()  + "/login?from=";
			String requestURL = request.getRequestURL().toString();
			String queryString = request.getQueryString();
			if(!GeneralUtils.isEmpty(queryString)) {
				requestURL += ("?" + queryString);
			}
			response.sendRedirect(url + URLEncoder.encode(requestURL, "UTF-8"));
			//WebUtils.jsRedirect();
		}
	}
}
