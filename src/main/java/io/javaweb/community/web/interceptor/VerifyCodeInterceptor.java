package io.javaweb.community.web.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.javaweb.community.annotation.VerifyCode;
import io.javaweb.community.common.BaseInterceptor;
import io.javaweb.community.constants.RedisKeys;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.exception.ServiceExceptions;
import io.javaweb.community.utils.DateUtils;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.utils.WebUtils;

/**
 * 
 * 验证码处理器
 * @author KevinBlandy
 *
 */
public class VerifyCodeInterceptor extends BaseInterceptor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyCodeInterceptor.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 
	 * 验证码处理
	 * @throws ServiceException 
	 * 
	 */
	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,String requestUri, Object handler) throws ServiceException {
		if(handlerMethod == null) {
			return true;
		}
		
		VerifyCode verifyCodeAnnotation = handlerMethod.getMethodAnnotation(VerifyCode.class);
		if(verifyCodeAnnotation == null) {
			//木有注解,不拦截
			return true;
		}
		
		String clientIp = WebUtils.getClientIP(request);
		
//		if(!RegUtils.match(clientIp, RegUtils.IP_V4)) {
//			return true;
//		}
		
		String redisKey = RedisKeys.REQUEST_LIMIT + verifyCodeAnnotation.name() + "_" + clientIp;
		
		String record = this.stringRedisTemplate.opsForValue().get(redisKey);
		
		if(GeneralUtils.isEmpty(record)) {	
			//该ip首次调用
			JSONObject jsonObject = new JSONObject();
			//接口调用时间
			jsonObject.put("timestamp", DateUtils.timestamp());
			//调用次数
			jsonObject.put("count", 1);
			this.stringRedisTemplate.opsForValue().set(redisKey, jsonObject.toJSONString(),verifyCodeAnnotation.time(),verifyCodeAnnotation.timeUnit() );
			return true;
		}
		
		JSONObject jsonObject = JSON.parseObject(record);
		
		int count = jsonObject.getIntValue("count");
		
		long timestamp = jsonObject.getLongValue("timestamp");
		
		//刷新调用时间 & 次数
		jsonObject.put("timestamp", DateUtils.timestamp());
		jsonObject.put("count", (count >= 99999 ) ? 99999 : count + 1);
		this.stringRedisTemplate.opsForValue().set(redisKey, jsonObject.toJSONString(),verifyCodeAnnotation.time(),verifyCodeAnnotation.timeUnit() );
		
		/**
		 * 
		 * 两次调用超过间隔时间,或者是调用次数超过限制,必须进行验证码校验
		 * 
		 */
		
		boolean flag = false;
		
		if(verifyCodeAnnotation.limit() > 0 && (DateUtils.timestamp() - timestamp) < verifyCodeAnnotation.limit()){
			flag = true;
		}
		
		if(verifyCodeAnnotation.max() > 0 && (verifyCodeAnnotation.max() < count)) {
			flag = true;
		}
			
		
		if(flag) {
			
			LOGGER.debug("验证码校验:{}",verifyCodeAnnotation.name());
			
			//超过限制,需要进行验证码校验
			String verifyCode = request.getParameter("verifyCode");
			
			String validateCode = request.getParameter("codeKey");
			
			if(GeneralUtils.isEmpty(verifyCode) || GeneralUtils.isEmpty(validateCode)) {
				//缺少验证码
				throw ServiceExceptions.MISSING_VERIFY_CODE;
			}
			
			String code = this.stringRedisTemplate.opsForValue().get(RedisKeys.VERIFY_CODE + validateCode);
			
			if(code != null) {
				//一码一验证
				this.stringRedisTemplate.delete(RedisKeys.VERIFY_CODE + validateCode);
				if(!code.equalsIgnoreCase(verifyCode)) {
					throw ServiceExceptions.VERIFY_CODE_FAIL;
				}
			}else {
				throw ServiceExceptions.VERIFY_CODE_FAIL;
			}
		}
		return true;
	}
}