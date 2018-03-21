package io.javaweb.community.entity.dto;

import io.javaweb.community.entity.MessageEntity;
import io.javaweb.community.entity.UserEntity;

/**
 *
 * Created by KevinBlandy on 2018/2/2 12:44
 *
 */
public class MessageDTO extends MessageEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = -8769376531357653995L;

	//创建用户
    private UserEntity author;

    //帖子
    private PostDTO post;

    //回复
    private PostReplyDTO reply;

    public PostDTO getPost() {
        return post;
    }

    public void setPost(PostDTO post) {
        this.post = post;
    }

    public PostReplyDTO getReply() {
        return reply;
    }

    public void setReply(PostReplyDTO reply) {
        this.reply = reply;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }
}
