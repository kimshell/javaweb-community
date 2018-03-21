package io.javaweb.community.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 会话忽略注解
 * @author KevinBlandy
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface IgnoreSession {
	
}
