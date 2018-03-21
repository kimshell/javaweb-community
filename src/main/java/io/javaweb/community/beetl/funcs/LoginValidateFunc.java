package io.javaweb.community.beetl.funcs;

import org.beetl.core.Context;
import org.beetl.core.Function;

import io.javaweb.community.web.support.SessionHolder;
/**
 * 
 * 判断用户是否登录
 * @author KevinBlandy
 *
 */
public class LoginValidateFunc implements Function {

	@Override
	public Object call(Object[] arg0, Context arg1) {
		if(SessionHolder.USER_SESSION.get() == null) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
