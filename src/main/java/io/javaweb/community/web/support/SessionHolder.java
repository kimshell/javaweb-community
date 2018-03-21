package io.javaweb.community.web.support;

public class SessionHolder {
	
	public static final ThreadLocal<UserSession> USER_SESSION = new ThreadLocal<>();
	
	public static final ThreadLocal<UserSession> MANAGER_SESSION = new ThreadLocal<>();
}
