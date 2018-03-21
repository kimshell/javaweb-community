package io.javaweb.community.web.model;

import java.io.Serializable;

/**
 * 
 * 验证码
 * @author KevinBlandy
 *
 */
public class VerifyCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7113787321681985479L;
	
	private String code;
	
	private String cokeKey;

	public VerifyCode() {}
	
	public VerifyCode(String code,String codeKey) {
		this.code = code;
		this.cokeKey = codeKey;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCokeKey() {
		return cokeKey;
	}

	public void setCokeKey(String cokeKey) {
		this.cokeKey = cokeKey;
	}
}
