package io.javaweb.community.entity.dto;

import io.javaweb.community.entity.PostReplyEntity;
import io.javaweb.community.entity.UserEntity;

public class PostReplyDTO extends PostReplyEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9176227343362530529L;
	
	//回复数量
	@Deprecated
    private Integer replyCount;
    
    //点赞数量
    private Integer agreeCount;
    
    //反对数量
    private Integer disAgreeCount;
    
    //作者
    private UserEntity author;

    //已经同意
    private Boolean isAgree;

    //已经反对
    private Boolean isDisAgree;

    //是否是作者
    private Boolean isAuthor;
    
    //主题
    private PostDTO post;

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	public Integer getAgreeCount() {
		return agreeCount;
	}

	public void setAgreeCount(Integer agreeCount) {
		this.agreeCount = agreeCount;
	}

	public Integer getDisAgreeCount() {
		return disAgreeCount;
	}

	public void setDisAgreeCount(Integer disAgreeCount) {
		this.disAgreeCount = disAgreeCount;
	}

	public UserEntity getAuthor() {
		return author;
	}

	public void setAuthor(UserEntity author) {
		this.author = author;
	}

	public Boolean getIsAgree() {
		return isAgree;
	}

	public void setIsAgree(Boolean isAgree) {
		this.isAgree = isAgree;
	}

	public Boolean getIsDisAgree() {
		return isDisAgree;
	}

	public void setIsDisAgree(Boolean isDisAgree) {
		this.isDisAgree = isDisAgree;
	}

	public Boolean getIsAuthor() {
		return isAuthor;
	}

	public void setIsAuthor(Boolean isAuthor) {
		this.isAuthor = isAuthor;
	}

	public PostDTO getPost() {
		return post;
	}

	public void setPost(PostDTO post) {
		this.post = post;
	}
}
