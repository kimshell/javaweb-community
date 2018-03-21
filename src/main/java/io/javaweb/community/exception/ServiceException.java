package io.javaweb.community.exception;

import io.javaweb.community.common.Message;
import io.javaweb.community.common.Message.Status;

/**
 * 服务异常
 * @author KevinBlandy
 *
 */
public class ServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2804390381900229831L;
	
	private Message<Void> message;
	
	public ServiceException() {
		
	}
	
	public ServiceException(String message) {
		this(message,Status.BAD_REQUEST);
	}
	
	public ServiceException(Status status) {
		this(status.getDesc(),status);
	}
	
	public ServiceException(String message,Status status) {
		super(message);
		this.message = new Message<>();
		this.message.setMessage(message);
		this.message.setStatus(status);
		this.message.setSuccess(Boolean.FALSE);
	}
	
	public Message<Void> getErrorMessage(){
		return this.message;
	}
}
