package io.javaweb.community.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.common.BaseInterceptor;
import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.WebUtils;
import io.javaweb.community.web.support.SessionHolder;
import io.javaweb.community.web.support.UserSession;
/**
 * 
 * 加载当前登录的用户
 * @author Kevin
 *
 */
public class UserSessionLoadInterceptor extends BaseInterceptor {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private UserService userService;

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,String requestUri, Object handler) throws Exception {
		if(SessionHolder.USER_SESSION.get() == null) {
			String cookie = WebUtils.getCookieValue(request, CookieKeys.USER_SESSION);
			if(!GeneralUtils.isEmpty(cookie)) {
				String json = this.stringRedisTemplate.opsForValue().get(RedisKeys.SESSION_USER + cookie);
				if(!GeneralUtils.isEmpty(json)) {
					//直接反序列化JSON,不用检索db
					UserSession userSession = JSON.parseObject(json, UserSession.class);
					UserEntity userEntity = this.userService.queryByPrimaryKey(userSession.getUser().getUserId());
					if(userEntity != null) {
						userSession.setUser(userEntity);
						SessionHolder.USER_SESSION.set(userSession);
					}
				}
			}
		}
		return Boolean.TRUE;
	}
}
