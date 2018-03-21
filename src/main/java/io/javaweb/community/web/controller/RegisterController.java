package io.javaweb.community.web.controller;

import com.alibaba.fastjson.JSON;
import io.javaweb.community.constants.CookieKeys;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.enums.Role;
import io.javaweb.community.enums.Status;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.RegUtils;
import io.javaweb.community.web.socket.endpoint.NowEndpoint;
import io.javaweb.community.web.socket.model.SocketMessage;
import io.javaweb.community.web.support.UserSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import io.javaweb.community.annotation.VerifyCode;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 注册
 * @author KevinBlandy
 *
 */
@Controller
@RequestMapping("/register")
public class RegisterController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);
	
	private static final List<String> DEFAULT_PORTRAITS = new ArrayList<>(); 
	
	private static final ModelAndView MODEL_AND_VIEW = new ModelAndView("/register/register");

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${now.template.register}")
    private String registerNotify;
    
//    @Value("${user.default.portrait}")
//    private String defaultPortrait;
    
    @GetMapping
    public ModelAndView registerPage() {
    	return MODEL_AND_VIEW;
    }
	
	@PostMapping
	@ResponseBody
	@VerifyCode(limit = (1000 * 60 * 60) * 1, max = 2,name = "register")		//最高1H注册一个用户,连续24H不超过2个,免验证码
	public Message<Void> register(HttpServletResponse response,
                                  @RequestParam("email")String email,
                                  @RequestParam("name")String name,
                                  @RequestParam("password")String password)throws Exception{
		
		if(name.trim().equals("匿名用户")) {
			throw new ServiceException("用户名已经存在", Message.Status.ALREADY_EXIST);
		}

		RegUtils.match(RegUtils.Regex.of("邮箱地址",email,RegUtils.REG_EMAIL),
						RegUtils.Regex.of("用户名",name,RegUtils.REG_NAME),
						RegUtils.Regex.of("登录密码",password,RegUtils.REG_PASS));
		
		LOGGER.info("新用户注册:{},{},{}",email,name,password);

		UserEntity userEntity = new UserEntity();

		userEntity.setUserId(GeneralUtils.getUUID());

		userEntity.setEmail(email);
		userEntity.setName(name);
		userEntity.setPass(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(password.getBytes()).getBytes()));

		userEntity.setEmailVerifi(Boolean.FALSE);
		//随机默认头像
		userEntity.setPortrait(GeneralUtils.choose(DEFAULT_PORTRAITS));
		userEntity.setPhoneVerifi(Boolean.FALSE);
		userEntity.setRole(Role.USER);
		userEntity.setStatus(Status.NORMAL);
		userEntity.setCreateDate(new Date());
		//隐私设置,默认允许广播所有消息
		userEntity.setLoginRadio(Boolean.TRUE);
    	userEntity.setBrowseRadio(Boolean.TRUE);
    	userEntity.setReplyRadio(Boolean.TRUE);

		//预创建session id
		userEntity.setSessionId(GeneralUtils.getUUID());

		this.userService.register(userEntity);

		// session 刷入
        Cookie cookie = new Cookie(CookieKeys.USER_SESSION,userEntity.getSessionId());
        cookie.setPath("/");
        cookie.setDomain(GeneralUtils.isWindows() ? "127.0.0.1" : "javaweb.io");
        cookie.setMaxAge(-1);
        cookie.setHttpOnly(Boolean.TRUE);

        response.addCookie(cookie);

        UserSession userSesssion = new UserSession();
        userSesssion.setRemenber(Boolean.FALSE);
        userSesssion.setUser(userEntity);

        this.stringRedisTemplate.opsForValue().set(RedisKeys.SESSION_USER + userEntity.getSessionId(), JSON.toJSONString(userSesssion), 30, TimeUnit.MINUTES);

        //广播注册信息
        NowEndpoint.broadCast(new SocketMessage(SocketMessage.Type.REGISTER,MessageFormat.format(this.registerNotify,userEntity.getUserId(),userEntity.getPortrait(),userEntity.getName())));

		return Messages.SUCCESS;
	}
	
	static {
		DEFAULT_PORTRAITS.add("http://www.javaweb.io/static/image/default_portrait_01.png");
		DEFAULT_PORTRAITS.add("http://www.javaweb.io/static/image/default_portrait_02.png");
		DEFAULT_PORTRAITS.add("http://www.javaweb.io/static/image/default_portrait_03.png");
		DEFAULT_PORTRAITS.add("http://www.javaweb.io/static/image/default_portrait_04.png");
		DEFAULT_PORTRAITS.add("http://www.javaweb.io/static/image/default_portrait_05.png");
		DEFAULT_PORTRAITS.add("http://www.javaweb.io/static/image/default_portrait_06.png");
	}
}
