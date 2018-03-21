package io.javaweb.community.entity;

import io.javaweb.community.enums.PostType;
import io.javaweb.community.enums.ReplyControl;
import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;

/**
 * 
 * 文章
 * @author KevinBlandy
 *
 */
@Entity(table = "jw_post",mapper = "io.javaweb.community.mapper.PostMapper")
public class PostEntity extends AbsArticleEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1594379179814265242L;
	
	@Id
	private String postId;			//PK
	
	private String title;			//标题
	
	private PostType type;			//类型
	
	private Integer browse;			//浏览量
	
	private Boolean essence ;		//是否是精品帖
	
	private Boolean top;			//是否置顶
	
	private ReplyControl replyControl;	//回复控制

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PostType getType() {
		return type;
	}

	public void setType(PostType type) {
		this.type = type;
	}

	public Integer getBrowse() {
		return browse;
	}

	public void setBrowse(Integer browse) {
		this.browse = browse;
	}

	public Boolean getEssence() {
		return essence;
	}

	public void setEssence(Boolean essence) {
		this.essence = essence;
	}

	public Boolean getTop() {
		return top;
	}

	public void setTop(Boolean top) {
		this.top = top;
	}

	public ReplyControl getReplyControl() {
		return replyControl;
	}

	public void setReplyControl(ReplyControl replyControl) {
		this.replyControl = replyControl;
	}

	@Override
	public String toString() {
		return "PostEntity [postId=" + postId + ", title=" + title + ", type=" + type + ", browse=" + browse
				+ ", essence=" + essence + ", top=" + top + ", replyControl=" + replyControl + "]";
	}
}
