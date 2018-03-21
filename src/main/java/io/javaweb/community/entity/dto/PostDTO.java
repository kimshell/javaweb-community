package io.javaweb.community.entity.dto;

import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.mybatis.domain.PageList;

/**
 * Created by KevinBlandy on 2018/1/24 17:48
 */
public class PostDTO extends PostEntity {

    private static final long serialVersionUID = 3290657097710337703L;
    
    //回复
    private PageList<PostReplyDTO> postReplys;
    
    //回复数量
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
    
    //页码
    private Integer page;
    
    //是否已经收藏
    private Boolean isCollection;
    
	public PageList<PostReplyDTO> getPostReplys() {
		return postReplys;
	}

	public void setPostReplys(PageList<PostReplyDTO> postReplys) {
		this.postReplys = postReplys;
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

	public Boolean getAgree() {
		return isAgree;
	}

	public void setAgree(Boolean agree) {
		isAgree = agree;
	}

	public Boolean getDisAgree() {
		return isDisAgree;
	}

	public void setDisAgree(Boolean disAgree) {
		isDisAgree = disAgree;
	}

    public void setAuthor(Boolean author) {
        isAuthor = author;
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

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Boolean getIsCollection() {
		return isCollection;
	}

	public void setIsCollection(Boolean isCollection) {
		this.isCollection = isCollection;
	}
}
