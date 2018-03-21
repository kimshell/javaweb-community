package io.javaweb.community.web.interceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.annotation.Admin;
import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.common.BaseInterceptor;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.enums.Role;
import io.javaweb.community.enums.Status;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.WebUtils;
import io.javaweb.community.web.support.SessionHolder;
import io.javaweb.community.web.support.UserSession;
/**
 * 
 * MANAGER会话拦截
 * @author Kevin
 *
 */
public class ManagerSessionInterceptor extends BaseInterceptor {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
			String requestUri, Object handler) throws Exception {
		if(handlerMethod.hasMethodAnnotation(IgnoreSession.class)) {
			return true;
		}
		String cookieValue = WebUtils.getCookieValue(request, CookieKeys.MANAGER_SESSION);
		if(GeneralUtils.isEmpty(cookieValue)) {
			//未登录
			this.sessionOut(request, response, Messages.NOT_LOGGED_IN);
			return false;
		}
		String jsonValue = this.stringRedisTemplate.opsForValue().get(RedisKeys.SESSION_MANAGER + cookieValue);
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
			this.stringRedisTemplate.delete(RedisKeys.SESSION_MANAGER + cookieValue);
			Cookie cookie = new Cookie(CookieKeys.MANAGER_SESSION,cookieValue);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
			this.sessionOut(request, response, Messages.NOT_LOGGED_IN);
			return false;
		}
		
		if(!userEntity.getStatus().equals(Status.NORMAL)) {
			//账户被禁用
			throw new ServiceException(Message.Status.ACCOUNT_IS_DISABLED);
		}
		if(!userEntity.getEmailVerifi()) {
			//没有经过邮箱认证
			throw new ServiceException(Message.Status.EMAIL_NOT_VERIFY);
		}
		if(userEntity.getRole().equals(Role.USER)) {
			//普通用户无权操作接口
			throw new ServiceException(Message.Status.NO_PERMISSION);
		}
		if(handlerMethod.hasMethodAnnotation(Admin.class) && !userEntity.getRole().equals(Role.ADMIN)) {
			//非admin用户无权操作接口
			throw new ServiceException(Message.Status.NO_PERMISSION);
		}
		
		userSession.setUser(userEntity);
		
		if(userSession.getRemenber()) {
			this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_MANAGER + cookieValue, JSON.toJSONString(userSession), 7,TimeUnit.DAYS);
		}else {
			this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_MANAGER + cookieValue, JSON.toJSONString(userSession), 30,TimeUnit.MINUTES);
		}
	
		SessionHolder.MANAGER_SESSION.set(userSession);
		return true;
	}

	private void sessionOut(HttpServletRequest request, HttpServletResponse response, Message<Void> message) throws IOException {
		if(WebUtils.isAjaxRequest(request)) {
			WebUtils.responseToJson(message, response);
		}else {
			WebUtils.jsRedirect(response, "http://manager.javaweb.io/login");
		}		
	}
}
