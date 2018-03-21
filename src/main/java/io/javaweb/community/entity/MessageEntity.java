package io.javaweb.community.entity;

import io.javaweb.community.common.BaseEntity;
import io.javaweb.community.enums.MessageType;
import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;


/**
 * 
 * 消息通知
 * @author KevinBlandy
 *
 */
@Entity(table = "jw_message",mapper = "io.javaweb.community.mapper.MessageMapper")
public class MessageEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -868869439713292453L;

    @Id
	private String messageId;

    //类型
    private MessageType type;

    //主题id
    private String postId;

    //回复id
    private String replyId;

    //状态(读/未读)
    private Boolean isRead;

    //目标用户id
    private String userId;

    //@用户锚点
    private String atAnchor;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAtAnchor() {
        return atAnchor;
    }

    public void setAtAnchor(String atAnchor) {
        this.atAnchor = atAnchor;
    }
}
