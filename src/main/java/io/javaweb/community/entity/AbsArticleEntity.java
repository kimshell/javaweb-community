package io.javaweb.community.entity;

import io.javaweb.community.common.BaseEntity;
/**
 * 
 * 文章抽象
 * @author KevinBlandy
 *
 */
public abstract class AbsArticleEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6781270724699133374L;
	
	private String content;			//正文
	
	private Boolean anonymous;		//是否是匿名

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(Boolean anonymous) {
		this.anonymous = anonymous;
	}
}
