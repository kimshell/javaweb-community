package io.javaweb.community.web.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.javaweb.community.annotation.IgnoreSession;
import io.javaweb.community.common.BaseController;
import io.javaweb.community.common.Message;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.web.model.VerifyCode;

/**
 * 
 * 验证码
 * @author KevinBlandy
 *
 */
@RestController
@RequestMapping("verifyCode")
public class VerifyCodeController extends BaseController {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyCodeController.class);
	
	private static final String IMG_PREFIX = "data:image/png;base64,";
	
	private static final Integer MAX_EXPIRE = 20;		//second
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 获取验证码
	 * @param request
	 * @param response
	 * @param width
	 * @param height
	 * @param withPrefix
	 * @return
	 * @throws IOException
	 */
	@GetMapping
	@IgnoreSession
	public Message<VerifyCode> verifyCode(HttpServletRequest request,
							            HttpServletResponse response,
							            @RequestParam(value = "width" ,defaultValue = "70") Integer width,
							            @RequestParam(value = "height" ,defaultValue = "35") Integer height,
							            @RequestParam(value = "with_prefix",defaultValue = "true") Boolean withPrefix) throws IOException{
		
		io.javaweb.community.web.support.VerifyCode verifyCode = new io.javaweb.community.web.support.VerifyCode(width,height);
		
		BufferedImage image = verifyCode.getImage();
		
		String text = verifyCode.getText();
		
		String validateCode = GeneralUtils.getUUID();
		
		LOGGER.info("获取验证码:文本 = {},校验码 = {}",text,validateCode);
		
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
			
			ImageIO.write(image, "png", byteArrayOutputStream);
			
			String base64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
			
			if(withPrefix) {
				base64 = IMG_PREFIX + base64;
			}
			
			this.stringRedisTemplate.opsForValue().set(RedisKeys.VERIFY_CODE + validateCode, text, MAX_EXPIRE, TimeUnit.SECONDS);
			
			return super.getSuccessMessage(new VerifyCode(base64,validateCode));
		}
	}
}
