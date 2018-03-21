package io.javaweb.community.beetl.funcs;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.ext.web.WebVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.WebUtils;
import io.javaweb.community.web.support.SessionHolder;
import io.javaweb.community.web.support.UserSession;
/**
 * 
 * @author Kevin
 *
 */
public class UserSessionFunc implements Function{

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public Object call(Object[] paras, Context ctx) {
		
		UserSession userSession =  SessionHolder.USER_SESSION.get();
		
		//当前holder不存在,尝试读取cookie后从redis获取
		if(userSession == null) {
			HttpServletRequest request = (HttpServletRequest)ctx.getGlobal(WebVariable.REQUEST);
			Cookie cookie = WebUtils.getCookie(request, CookieKeys.USER_SESSION);
			if(cookie != null) {
				String json = this.stringRedisTemplate.opsForValue().get(RedisKeys.SESSION_USER + cookie.getValue());
				if(!GeneralUtils.isEmpty(json)) {
					userSession = JSON.parseObject(json, UserSession.class);
				}
			}
		}
		
		return userSession;
	}
}
