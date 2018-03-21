package io.javaweb.community.web.controller.manager;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.annotation.VerifyCode;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.enums.Role;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.web.support.UserSession;
/**
 * 
 * manager登录
 * @author Kevin
 *
 */
@RestController("managerLoginController")
@RequestMapping("/manager/login")
public class LoginController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * @param request
	 * @param response
	 * @param existsSession
	 * @param account
	 * @param password
	 * @return
	 * @throws Exception
	 */
	@PostMapping
	@VerifyCode(limit = 1000 * 2, max = 2,name = "manager_login")
	public Message<Void> login(HttpServletRequest request,
							HttpServletResponse response,
							@CookieValue(value = CookieKeys.MANAGER_SESSION,required = false)String existsSession,
							@RequestParam("account")String account,
							@RequestParam("password")String password)throws Exception{
		
		LOGGER.debug("执行登录:{},{}",account,password);
		
        UserEntity userEntity = this.userService.queryByAccount(account);
        
        if(userEntity == null || !userEntity.getPass().equals(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(password.getBytes()).getBytes()))){
            return Messages.LOGIN_FAILD;
        }
        
        if(userEntity.getRole().equals(Role.USER)) {
        	//非管理员无权登录
        	return Messages.NO_PERMISSION;
        }
        
        String sessionKey = GeneralUtils.getUUID();
        
        Cookie cookie = new Cookie(CookieKeys.MANAGER_SESSION,sessionKey);
        cookie.setPath("/");
        cookie.setDomain(GeneralUtils.isWindows() ? "127.0.0.1" : "javaweb.io");
        cookie.setMaxAge(-1);		//会话级别保存
        cookie.setHttpOnly(Boolean.TRUE);
        response.addCookie(cookie);

        if(!GeneralUtils.isEmpty(existsSession)) {
            //多次登录,删除上次会话
            this.stringRedisTemplate.delete(RedisKeys.SESSION_MANAGER + existsSession);
        }
        
        UserSession userSession = new UserSession();
        userSession.setRemenber(false);		//manager后台不允许记住密码
        userSession.setUser(userEntity);
        
        this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_MANAGER + sessionKey, JSON.toJSONString(userSession), 30,TimeUnit.MINUTES);
        
		return Messages.SUCCESS;
	}
}
