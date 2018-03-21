package io.javaweb.community.web.controller;

import io.javaweb.community.annotation.IgnoreEmailVerifi;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.mail.MailService;
import io.javaweb.community.service.UserService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.web.support.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Created by KevinBlandy on 2018/2/5 17:23
 */
@Controller
@RequestMapping("/activation")
public class ActivationController extends BaseController{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MailService mailService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/send")
    @IgnoreEmailVerifi
    public ModelAndView modelAndView(HttpServletRequest request)throws Exception{

        ModelAndView modelAndView = new ModelAndView();

        UserEntity userEntity = SessionHolder.USER_SESSION.get().getUser();

        if(userEntity.getEmailVerifi()){
            //用户已经激活,重定向到主页
            modelAndView.setViewName("redirect:" + request.getContextPath() + "/index");
            return modelAndView;
        }

        modelAndView.setViewName("/activation/activation");

        String activationCode = this.stringRedisTemplate.opsForValue().get(RedisKeys.EMAIL_ACTIVATION + userEntity.getUserId());

        if(!GeneralUtils.isEmpty(activationCode)){
            //邮件已经发送
            modelAndView.addObject("success",false);
            modelAndView.addObject("message", "邮件已经发送过了,请检查邮箱,10分钟后可以再次发送");
            return modelAndView;
        }

        //邮件未发送,执行发送,有效时间10分钟
        activationCode = GeneralUtils.getUUID() + GeneralUtils.getUUID();
        
        this.stringRedisTemplate.opsForValue().set(RedisKeys.EMAIL_ACTIVATION + userEntity.getUserId(),activationCode,10, TimeUnit.MINUTES);

        
        /**
         * 通知邮件,后面应该用beetl渲染html模版
         */
        try {
            this.mailService.sendHTMLMail(userEntity.getEmail(),
                    "欢迎加入javaweb开发者社区",
                    "<h3>欢迎加入javaweb开发者社区<h3>伟大的javaweb开发者:" + userEntity.getName() + " 您好,等您很久了(如果不是您本人操作,请无视)。" +
                            "<a target=\"_blank\" href=\"http://www.javaweb.io/activation/validate?key=" + GeneralUtils.getRandomLetter(15) + "&code=" + activationCode + "\">点击这里完成验证</a>(验证链接有效时间10分钟)<br/>" +
                            "您也可以复制链接在浏览器中打开:http://www.javaweb.io/activation/validate?key=" + GeneralUtils.getRandomLetter(15) + "&code=" + activationCode);
        }catch (Exception exception){
            //邮件发送异常
            exception.printStackTrace();
            modelAndView.addObject("success",false);
            modelAndView.addObject("message","邮件发送异常,请检查地址合法性");
            //删除记录
            this.stringRedisTemplate.delete(RedisKeys.EMAIL_ACTIVATION + userEntity.getUserId());
            return modelAndView;
        }
        
        modelAndView.addObject("success",true);
        modelAndView.addObject("message","验证邮件已经发送至您邮箱,请您点击邮箱中的链接完成验证");
        return modelAndView;
    }

    //完成校验,必须是在登录的情况下进行校验
    @GetMapping("/validate")
    @IgnoreEmailVerifi
    private ModelAndView validate(HttpServletRequest request,
                                  @RequestParam("code")String code)throws Exception{
    	
    	ModelAndView modelAndView = new ModelAndView();
    	
        UserEntity userEntity = SessionHolder.USER_SESSION.get().getUser();
        
        if(!userEntity.getEmailVerifi()){
        	modelAndView.setViewName("/activation/validate");
        	String activationCode = this.stringRedisTemplate.opsForValue().get(RedisKeys.EMAIL_ACTIVATION + userEntity.getUserId());
        	if(activationCode != null && activationCode.equals(code)) {
        		/**
        		 * 账户激活ok,修改用户账户认证状态
        		 */
        		modelAndView.addObject("success", true);
        		UserEntity updateUser = new UserEntity();
        		updateUser.setUserId(userEntity.getUserId());
        		updateUser.setEmailVerifi(true);
        		this.userService.updateByPrimaryKeySelective(updateUser);
        			
        		this.stringRedisTemplate.delete(RedisKeys.EMAIL_ACTIVATION + userEntity.getUserId());
        	}else {
        		/**
        		 * 账户激活失败
        		 */
        		modelAndView.addObject("success", false);
        	}
        	return modelAndView;
        }
        /**
         * 用户已经激活的状态下,重定向到主页
         */
        modelAndView.setViewName("redirect:" + request.getContextPath() + "/index");
        return modelAndView;
    }
}
