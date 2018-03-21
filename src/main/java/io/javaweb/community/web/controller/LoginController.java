package io.javaweb.community.web.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;

import io.javaweb.community.common.Messages;
import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.LoginRecordEntity;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.service.LoginRecordService;
import io.javaweb.community.service.UserService;
import io.javaweb.community.web.socket.model.SocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import io.javaweb.community.annotation.IgnoreAccountStatus;
import io.javaweb.community.annotation.IgnoreEmailVerifi;
import io.javaweb.community.annotation.VerifyCode;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.WebUtils;
import io.javaweb.community.web.socket.endpoint.ChatEndpoint;
//import io.javaweb.community.utils.WebUtils;
import io.javaweb.community.web.socket.endpoint.NowEndpoint;
import io.javaweb.community.web.support.SessionHolder;
import io.javaweb.community.web.support.UserSession;
import io.javaweb.community.web.support.Views;

/**
 * 
 * 登录
 * @author Kevin
 *
 */
@Controller 
//@RequestMapping("/login")
public class LoginController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	private static ModelAndView REDIRECT_VIEW = new ModelAndView("redirect:/index");

	@Autowired
	private UserService userService;

	@Autowired
    private StringRedisTemplate stringRedisTemplate;
	
	@Value("${now.template.login}")
	private String loginNoitfy;
	
	@Autowired
	private ExecutorService executorService;
	
	@Autowired
	private LoginRecordService loginRecordService;
	
	/**
	 * 登录页面
	 * @param from
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/login")
	public ModelAndView login(@RequestParam(value = "from",required = false)String from) throws Exception{
		LOGGER.info("未登录,form = {}",from);
		if(GeneralUtils.isEmpty(from)) {
			return Views.LOGIN_VIEW;
		}
		ModelAndView modelAndView = new ModelAndView(Views.LOGIN_VIEW.getViewName());
		modelAndView.addObject("from", URLDecoder.decode(from,"UTF-8"));
		return modelAndView;
	}
	
	/**
	 * 执行登录
	 * @param reques
	 * @param response
	 * @param existsSession
	 * @param account
	 * @param password
	 * @param remenber
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/login")
	@ResponseBody
	@VerifyCode(limit = 2000,max = 10,name = "login")
	public Message<Void> login(HttpServletRequest request,
                                HttpServletResponse response,
                                @CookieValue(value = CookieKeys.USER_SESSION,required = false)String existsSession,
                                @RequestParam("account")String account,
                                @RequestParam("password")String password,
                                @RequestParam(value = "remenber",defaultValue = "false")Boolean remenber)throws Exception{

		LOGGER.debug("执行登录:{},{}",account,password);
		
        UserEntity userEntity = this.userService.queryByAccount(account);
        
        if(userEntity == null || !userEntity.getPass().equals(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(password.getBytes()).getBytes()))){
            return Messages.LOGIN_FAILD;
        }

        String sessionKey = GeneralUtils.getUUID();

        Cookie cookie = new Cookie(CookieKeys.USER_SESSION,sessionKey);
        cookie.setPath("/");
        //Emmmmmmm windows下肯定是测试环境了
        cookie.setDomain(GeneralUtils.isWindows() ? "127.0.0.1" : "javaweb.io");
        //cookie 7天
        cookie.setMaxAge(remenber ? (60 * 60 * 24 * 7) : -1);
        cookie.setHttpOnly(Boolean.TRUE);

        response.addCookie(cookie);

        if(!GeneralUtils.isEmpty(existsSession)) {
            //多次登录,删除上次会话
            this.stringRedisTemplate.delete(RedisKeys.SESSION_USER + existsSession);
        }
        
        if(!GeneralUtils.isEmpty(userEntity.getSessionId())) {
        	//禁止用户重复登录 
        	//TODO 别直接删除会话记录,设置session别处登录 flag
        	this.stringRedisTemplate.delete(RedisKeys.SESSION_USER + userEntity.getSessionId());
        }
        
        UserSession userSession = new UserSession();
        userSession.setRemenber(remenber);
        userSession.setUser(userEntity);
        
        if(remenber) {
        	 // redis 缓存session 7 天
            this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_USER + sessionKey, JSON.toJSONString(userSession), 7,TimeUnit.DAYS);
        }else {
        	// redis 缓存session 30分钟
        	 this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_USER + sessionKey, JSON.toJSONString(userSession), 30,TimeUnit.MINUTES);
        }

        //广播登录信息
        if(userEntity.getLoginRadio()) {
        	NowEndpoint.broadCast(new SocketMessage(SocketMessage.Type.LOGIN, MessageFormat.format(this.loginNoitfy,userEntity.getUserId(),userEntity.getPortrait(),userEntity.getName())));
        }
        
        //更新session id
        userEntity.setSessionId(sessionKey);
        this.userService.updateByPrimaryKeySelective(userEntity);

        String clientIp = WebUtils.getClientIP(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);

        //登录日志
        this.executorService.submit(() -> {
        	LOGGER.debug("登录日志记录");
        	LoginRecordEntity loginRecordEntity = new LoginRecordEntity();
        	loginRecordEntity.setRecordId(GeneralUtils.getUUID());
        	loginRecordEntity.setIp(clientIp);
        	loginRecordEntity.setUserAgent(userAgent);
        	loginRecordEntity.setUserId(userEntity.getUserId());
        	loginRecordEntity.setCreateDate(new Date());
        	try {
				this.loginRecordService.createRecord(loginRecordEntity);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("登录日志记录异常:{}",e);
			}
        });
		return Messages.SUCCESS;
	}
	
	/**
	 * 退出登录
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/logout")
	@IgnoreEmailVerifi
	@IgnoreAccountStatus
	public ModelAndView logOut(HttpServletRequest request,
			 					@CookieValue(CookieKeys.USER_SESSION) String session,
								HttpServletResponse response) throws IOException {
		
		//WebUtils.jsRedirect(response, request.getContextPath() + "/index");

		UserEntity userEntity = SessionHolder.USER_SESSION.get().getUser();
		
		this.stringRedisTemplate.delete(RedisKeys.SESSION_USER + session);
		
		Cookie cookie = new Cookie(CookieKeys.USER_SESSION,session);
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
		
//		if(REDIRECT_VIEW == null) {
//			REDIRECT_VIEW = new ModelAndView("redirect:" + request.getContextPath() + "/index");
//		}
		//尝试踢出在线聊天室
		ChatEndpoint.close(chatEndpoint -> {
			if(chatEndpoint.getUser().getUserId().equals(userEntity.getUserId())) {
				return true;
			}
			return false;
		},new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "您已经退出登录,连接已经断开"));
		return REDIRECT_VIEW;
	}
}
