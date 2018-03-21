package io.javaweb.community.web.controller;

import java.util.concurrent.TimeUnit;

import io.javaweb.community.utils.RegUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.common.Messages;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.mail.MailService;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;

//重置密码
@RequestMapping("/forgetpass")
@Controller
public class ForgetPassController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ForgetPassController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
    private MailService mailService;
	
	//发送重置密码的邮件
	@PostMapping
	@ResponseBody
	@IgnoreSession
	public Message<Void> forgetpass(@RequestParam("account")String account)throws Exception{
		
		UserEntity userEntity = this.userService.queryByAccount(account);
		
		if(userEntity == null) {
			return super.getErrorMessage("账户不存在");
		}
		
		String code = this.stringRedisTemplate.opsForValue().get(RedisKeys.FORGET_PASS + userEntity.getUserId());
		
		if(!GeneralUtils.isEmpty(code)) {
			return super.getErrorMessage("重置邮件已经发出,请检查邮箱。10分钟后可以再次发送");
		}
		
		code = GeneralUtils.getUUID() + GeneralUtils.getUUID();
		
		this.stringRedisTemplate.opsForValue().set(RedisKeys.FORGET_PASS + userEntity.getUserId(), code, 10, TimeUnit.MINUTES);
		
		this.mailService.sendHTMLMail(userEntity.getEmail(), "重置登录密码", 
				"<h1>警告<h1> " + 
				"您正在通过邮件重置 <a target=\"_blank\" href=\"http://www.javaweb.io\">Javaweb开发者社区</a> 的登录密码 <font color=\"red\">(如果不是您本人操作,请无视)。</font><br/>" +
		        "<a target=\"_blank\" href=\"http://www.javaweb.io/forgetpass/" + userEntity.getUserId() + "?key=" + GeneralUtils.getRandomLetter(15) + "&code=" + code + "\">点击这里重置密码</a>(验证链接有效时间10分钟) <br/>" + 
				"您也可以复制链接在浏览器中打开:http://www.javaweb.io/forgetpass/" + userEntity.getUserId() + "?key=" + GeneralUtils.getRandomLetter(15) + "&code=" + code);
		
		return Messages.SUCCESS;
	}
	
	//加载重置密码页面
	@GetMapping("/{userId}")
	@IgnoreSession
	public ModelAndView forgetPassValidate(@PathVariable("userId")String userId,
											@RequestParam("code")String code) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView("/forgetpass/forgetpass");
		
		LOGGER.debug("重置密码校验:userId={},code={}",userId,code);
		
		String validateCode = this.stringRedisTemplate.opsForValue().get(RedisKeys.FORGET_PASS + userId);
		
		if(GeneralUtils.isEmpty(validateCode)) {
			throw new ServiceException("密码重置邮件已经过期,请尝试重新发送");
		}else if(!validateCode.equals(code)) {
			throw new ServiceException("fuck off");
		}
		
		modelAndView.addObject("code", validateCode);
		modelAndView.addObject("userId", userId);
		return modelAndView;
	}


	@PostMapping("/reset")
	@IgnoreSession
	@ResponseBody
	public Message<Void> update(@RequestParam("userId")String userId,
                                @RequestParam("code")String code,
                                @RequestParam("password")String password)throws Exception{

	    String validateCode = this.stringRedisTemplate.opsForValue().get(RedisKeys.FORGET_PASS + userId);
	    if(GeneralUtils.isEmpty(validateCode) || !validateCode.equals(code)){
            return super.getErrorMessage("密码重置邮件已经过期,请尝试重新发送");
        }

        RegUtils.match(RegUtils.Regex.of("密码",password,RegUtils.REG_PASS));
	    UserEntity userEntity = new UserEntity();
	    userEntity.setUserId(userId);
        userEntity.setPass(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(password.getBytes()).getBytes()));
        this.userService.updateByPrimaryKeySelective(userEntity);

        this.stringRedisTemplate.delete(RedisKeys.FORGET_PASS + userId);
	    return Messages.SUCCESS;
    }
}








