package io.javaweb.community.beetl.funcs;

import org.beetl.core.Context;
import org.beetl.core.Function;

import io.javaweb.community.enums.Role;
import io.javaweb.community.web.support.SessionHolder;
import io.javaweb.community.web.support.UserSession;
/**
 * 
 * 角色判断
 * @author Kevin
 *
 */
public class RoleValidateFunc implements Function{

	@Override
	public Object call(Object[] args, Context context) {
		UserSession userSession = SessionHolder.USER_SESSION.get();
		if(userSession != null && userSession.getUser().getRole().equals(Role.valueOf(((String) args[0]).toUpperCase()))) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
