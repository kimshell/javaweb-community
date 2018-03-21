package io.javaweb.community.common;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @author KevinBlandy
 *
 * @param <T>
 */
public class Message <T>{
	
	/**
	 * 状态枚举
	 * @author KevinBlandy
	 *
	 */
	public enum Status {
		
		//统一成功状态
		SUCCESS("OK"),	
		
		//统一客户端异常状态
		BAD_REQUEST("无效请求"),
		
			NO_AUTHENTICATION("身份认证失败"),
			
			BAD_REQUEST_BODY("数据不符合规范"),
			
			MISSING_REQUEST_PARAM("缺少必须参数"),
			
			NO_PERMISSION("无权操作"),
			
			BAD_PARAM("参数校验失败"),
			
			NOT_FOUND("数据不存在"),
			
			BAD_URL("请求路径未找到"),
			
			REQUEST_METHOD_NOT_SUPPORT("请求方法不支持"),
			
			ALREADY_EXIST("数据已经存在"),
			
			MISSING_VERIFY_CODE("缺少验证码参数"),
			
			VERIFY_CODE_FAIL("验证码错误"),
			
			MEDIA_TYPE_NOT_SUPPORTED("不支持的媒体类型"),
			
			NOT_LOGGED_IN("未登录"),
			
			LOGIN_FAILD("账户名或者密码错误"),
			
			POST_IS_DISABLED("帖子被屏蔽"),
			
			ACCOUNT_IS_DISABLED("账户被禁用"),
			
			EMAIL_NOT_VERIFY("邮箱未验证"),
	
		//统一服务端异常状态
		SERVER_ERROR("服务器异常");
		
		private String desc;
		
		Status (String desc){
			this.desc = desc;
		}
		
		public String getDesc() {
			return desc;
		}
		
		public void setDesc(String desc) {
			this.desc = desc;
		}
	}

	
	//是否是成功的操作
	private Boolean success;
	
	//提示消息
	private String message;
	
	//数据
	private T data;
	
	//时间
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date date;
	
	//状态码
	private Status status;
	
	public Message() {}
	
	public Message(Boolean success,Status status,T data) {
		this.success = success;
		this.status = status;
		this.data = data;
	}
	
	public Message(Boolean success,String message,Status status,T data) {
		this.success = success;
		this.message = message;
		this.status = status;
		this.data = data;
	}
	
	public Boolean getSuccess() {
		return success;
	}


	public void setSuccess(Boolean success) {
		this.success = success;
	}


	public String getMessage() {
		if(this.message == null && this.status != null) {
			return this.status.getDesc();
		}
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public T getData() {
		return data;
	}


	public void setData(T data) {
		this.data = data;
	}


	public Date getDate() {
//		if(this.date == null) {
//			this.date = new Date();
//		}
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}
}
