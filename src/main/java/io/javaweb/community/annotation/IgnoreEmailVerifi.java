package io.javaweb.community.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 
 * 忽略邮箱校验
 * @author Kevin
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface IgnoreEmailVerifi {

}
