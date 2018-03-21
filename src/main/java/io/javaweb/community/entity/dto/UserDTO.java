package io.javaweb.community.entity.dto;

import java.util.List;

import io.javaweb.community.entity.UserEntity;

public class UserDTO extends UserEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6777191074223206201L;
	
	//模糊关键字name
	private String likeName;
	
	private List<PostDTO> posts;
	
	private List<PostReplyDTO> replys;

	public String getLikeName() {
		return likeName;
	}

	public void setLikeName(String likeName) {
		this.likeName = likeName;
	}

	public List<PostDTO> getPosts() {
		return posts;
	}

	public void setPosts(List<PostDTO> posts) {
		this.posts = posts;
	}

	public List<PostReplyDTO> getReplys() {
		return replys;
	}

	public void setReplys(List<PostReplyDTO> replys) {
		this.replys = replys;
	}
}
