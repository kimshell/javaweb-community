package io.javaweb.community.entity;

import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;

/**
 * 
 * @author KevinBlandy
 *
 */
@Entity(table = "jw_post_reply",mapper = "io.javaweb.community.mapper.PostReplyMapper")
public class PostReplyEntity extends AbsArticleEntity {
	
	public static final String DEFAULT_PARENT_ID = "0";

	/**
	 * 
	 */
	private static final long serialVersionUID = -3184767895905959444L;
	
	//回复ID
	@Id
	private String replyId;
	
	//帖子ID
	private String postId;
	
	//父级回复id
	private String parentId;
	
	//是否是父级回复
	private Boolean isParent;

	//页码
	private Integer page;

	public String getReplyId() {
		return replyId;
	}

	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "PostReplyEntity{" +
				"replyId='" + replyId + '\'' +
				", postId='" + postId + '\'' +
				", parentId='" + parentId + '\'' +
				", isParent=" + isParent +
				", page=" + page +
				'}';
	}
}
