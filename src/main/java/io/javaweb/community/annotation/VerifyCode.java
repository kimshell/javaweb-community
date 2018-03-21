package io.javaweb.community.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author KevinBlandy
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface VerifyCode {
	
	/**
	 * 接口安全限制,两次接口调用间隔超过该时间(单位是毫秒),则必须进行验证码校验
	 * @return
	 */
	long limit();
	

	/**
	 * 最多调用次数,超过该限定次数,则必须进行验证码校验
	 * @return
	 */
	int max() default -1;
	
	/**
	 * 需要验证码服务的接口名称	//TODO 应该枚举值,保持全局唯一
	 * @return
	 */
	String name();
	
	/**
	 * 默认时间是1小时
	 * @return
	 */
	int time() default 1;
	
	/**
	 * 默认单位是小时
	 * @return
	 */
	TimeUnit timeUnit() default TimeUnit.HOURS;
}
