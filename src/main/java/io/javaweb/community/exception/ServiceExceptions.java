package io.javaweb.community.exception;

import io.javaweb.community.common.Message;
/**
 * 
 * 预定义异常常量
 * @author KevinBlandy
 *
 */
public class ServiceExceptions {
	
	public static final ServiceException MISSING_VERIFY_CODE = new ServiceException(Message.Status.MISSING_VERIFY_CODE);
	
	public static final ServiceException VERIFY_CODE_FAIL = new ServiceException(Message.Status.VERIFY_CODE_FAIL);
	
	public static final ServiceException NOT_LOGGED_IN = new ServiceException(Message.Status.NOT_LOGGED_IN);

	public static final ServiceException BAD_PARAM = new ServiceException(Message.Status.BAD_PARAM);
}
