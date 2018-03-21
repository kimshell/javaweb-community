package io.javaweb.community.entity;

import io.javaweb.community.common.BaseEntity;
import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;
/**
 * 
 * @author KevinBlandy
 *
 */
@Entity(table = "jw_user_setting",mapper = "io.javaweb.community.mapper.UserSettingMapper")
public class UserSettingEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3401379413967763256L;
	
	@Id
	private String userId;
	
	//隐藏回帖记录
	private Boolean hideReply;
	
	//隐藏发帖记录
	private Boolean hidePost;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Boolean getHideReply() {
		return hideReply;
	}
	public void setHideReply(Boolean hideReply) {
		this.hideReply = hideReply;
	}
	public Boolean getHidePost() {
		return hidePost;
	}
	public void setHidePost(Boolean hidePost) {
		this.hidePost = hidePost;
	}
}