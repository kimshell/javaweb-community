package io.javaweb.community.web.support;

import java.io.Serializable;

import io.javaweb.community.entity.UserEntity;

public class UserSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3928228117131373283L;
	
	private UserEntity user;
	
	private Boolean remenber;

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Boolean getRemenber() {
		return remenber;
	}

	public void setRemenber(Boolean remenber) {
		this.remenber = remenber;
	}
}
