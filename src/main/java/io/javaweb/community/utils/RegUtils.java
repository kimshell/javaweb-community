package io.javaweb.community.utils;

import java.io.Serializable;
import java.util.regex.Pattern;


import io.javaweb.community.common.Message;
import io.javaweb.community.exception.ServiceException;

/**
 * 
 * 
 * 正则
 * @author KevinBlandy
 * java的正则引擎, \w 对汉字匹配不友好
 *
 */
public class RegUtils {
	
	private RegUtils() {
		throw new AssertionError("No com.tedi.community.utils.RegUtils instances for you!");
	}

	// QQ
	public static final Pattern REG_QQ = Pattern.compile("^[1-9]\\d{4,11}$");

	// 手机
	public static final Pattern REG_MOBILE = Pattern.compile("^1[3-9]{11}$");

	// 用户名 只能是英文,数字,下划线 2-14长度
	public static final Pattern REG_NAME = Pattern.compile("^[0-9a-zA-Z_\\u4e00-\\u9fa5]{2,14}$");

	// 邮箱													
	public static final Pattern REG_EMAIL = Pattern.compile("^[0-9a-zA-Z][-_0-9a-zA-Z\\.]{2,20}[0-9a-zA-Z]@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}$");

	// 密码不能有汉字或者空格 6-16位
	public static final Pattern REG_PASS = Pattern.compile("^[^\\u4e00-\\u9fa5\\s]{6,16}$");
	
	//ipv4
	public static final Pattern IP_V4 = Pattern.compile("^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$");

	public static Boolean match(String input, Pattern pattern) {
		if (GeneralUtils.isEmpty(input) || pattern == null) {
			return Boolean.FALSE;
		}
		return pattern.matcher(input).matches();
	}

	public static Boolean match(String input, String pattern) {
		return match(input, Pattern.compile(pattern));
	}

	public static void match(Regex regex) throws ServiceException {
		if (!(!regex.getRequired() && GeneralUtils.isEmpty(regex.getvalue()))) {
			if (!match(regex.getvalue(), regex.getPattern())) {
				throw new ServiceException("参数[" + regex.getName() + "],校验失败", Message.Status.BAD_REQUEST);
			}
		}
	}

	public static void match(Regex... regexs) throws ServiceException {
		for (Regex regex : regexs) {
			match(regex);
		}
	}

	// regex
	public static class Regex implements Serializable {

		private static final long serialVersionUID = 9195592262930379089L;
		// 是否必须校验
		private Boolean required = Boolean.TRUE; 
		// 名称
		private String name; 
		// 值
		private String value; 
		// 正则
		private Pattern pattern; 

		public Regex() {
		}

		public Regex(String name, String value, Pattern pattern) {
			this.name = name;
			this.value = value;
			this.pattern = pattern;
		}

		public Regex(String name, String value, Pattern pattern, Boolean required) {
			this(name, value, pattern);
			this.required = required;
		}

		public Boolean getRequired() {
			return required;
		}

		public void setRequired(Boolean required) {
			this.required = required;
		}

		public String getvalue() {
			return value;
		}

		public void setvalue(String value) {
			this.value = value;
		}

		public Pattern getPattern() {
			return pattern;
		}

		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public static Regex of(String name, String value, Pattern pattern) {
			return new Regex(name, value, pattern);
		}

		public static Regex of(String name, String value, Pattern pattern, Boolean required) {
			return new Regex(name, value, pattern, required);
		}
	}
}
