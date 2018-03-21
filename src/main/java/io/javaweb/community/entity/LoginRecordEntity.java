package io.javaweb.community.entity;

import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;

/**
 * 
 * 登录记录
 * @author Kevin
 *
 */
@Entity(table = "jw_login_record",mapper = "io.javaweb.community.mapper.LoginRecordMapper")
public class LoginRecordEntity extends AbsIpInfoEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6896415748855605944L;
	
	@Id
	private String recordId;
	
	private String userId;
	
	private String userAgent;

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
