package io.javaweb.community.common;

/**
 * 
 * 预定义HTTP消息常量
 * @author KevinBlandy
 *
 */
public final class Messages {
	
	public static final Message<Void> SUCCESS = new Message<>(Boolean.TRUE,Message.Status.SUCCESS,null);
	
	public static final Message<Void> MISSING_REQUEST_PARAM = new Message<>(Boolean.FALSE,Message.Status.MISSING_REQUEST_PARAM,null);
	
	public static final Message<Void> BAD_REQUEST_BODY = new Message<>(Boolean.FALSE,Message.Status.BAD_REQUEST_BODY,null);
	
	public static final Message<Void> BAD_PARAM = new Message<>(Boolean.FALSE,Message.Status.BAD_PARAM,null);
	
	public static final Message<Void> SERVER_ERROR = new Message<>(Boolean.FALSE,Message.Status.SERVER_ERROR,null);
	
	public static final Message<Void> REQUEST_METHOD_NOT_SUPPORT = new Message<>(Boolean.FALSE,Message.Status.REQUEST_METHOD_NOT_SUPPORT,null);
	
	public static final Message<Void> MEDIA_TYPE_NOT_SUPPORTED = new Message<>(Boolean.FALSE,Message.Status.MEDIA_TYPE_NOT_SUPPORTED,null);
	
	public static final Message<Void> LOGIN_FAILD = new Message<>(Boolean.FALSE,Message.Status.LOGIN_FAILD,null);
	
	public static final Message<Void> ACCOUNT_IS_DISABLED = new Message<>(Boolean.FALSE,Message.Status.ACCOUNT_IS_DISABLED,null);
	
	public static final Message<Void> NOT_LOGGED_IN = new Message<>(Boolean.FALSE,Message.Status.NOT_LOGGED_IN,null);
	
	public static final Message<Void> BAD_URL = new Message<>(Boolean.FALSE,Message.Status.BAD_URL,null);
	
	public static final Message<Void> NOT_FOUND = new Message<>(Boolean.FALSE,Message.Status.NOT_FOUND,null);

	public static final Message<Void> NO_PERMISSION = new Message<>(Boolean.FALSE,Message.Status.NO_PERMISSION,null);
}