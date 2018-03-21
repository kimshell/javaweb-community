package io.javaweb.community.common;

import java.util.Date;



/**
 * Created by KevinBlandy on 2017/10/30 12:26
 */
public class BaseController {
	
	
	public Message<Void> getSuccessMessage () {
		return this.getSuccessMessage(null);
	}
	
	public <T> Message<T> getSuccessMessage (T data) {
		Message<T> message = new Message<T>();
		message.setData(data);
		message.setSuccess(Boolean.TRUE);
		message.setStatus(Message.Status.SUCCESS);
		message.setDate(new Date());
		return message;
	}
	
	public <T> Message<T> getSuccessMessage (T data,String message) {
		Message<T> successMessage = this.getSuccessMessage(data);
		successMessage.setMessage(message);
		return successMessage;
	}
	
	public Message<Void> getErrorMessage(String message){
		Message<Void> errorMessage = new Message<>();
		errorMessage.setSuccess(Boolean.FALSE);
		errorMessage.setStatus(Message.Status.BAD_REQUEST);
		errorMessage.setDate(new Date());
		//errorMessage.setData(null);
		errorMessage.setMessage(message);
		return errorMessage;
	}
	
//	public Message<Void> getSessionUser(HttpServletRequest request){
//		String cookie = WebUtils.getCookieValue(request, CookieKeys.SESSION_COOKIE_NAME);
//		if(!GeneralUtils.isEmpty(cookie)) {
//			
//		}
//		return null;
//	}
}
