package io.javaweb.community.web.socket.model;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class ChatMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -984269010492978614L;
	
	public static enum Type {
		
		//消息
		MESSAGE,
		
		//退出
		QUIT,
		
		//加入
		JOIN,
		
		//资料更新(头像)
		DATA_UPDATE,
		
		//通知
		NOTIFY,
		
		//在线用户信息
		USERS,
	}
	
	public ChatMessage() {}
	
	public ChatMessage(Type type,String content,Object attach) {
		this.type = type;
		this.content = content;
		this.attach = attach;
	}
	
	private Type type;
	
	private String content;
	
	private Object attach;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date date;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Object getAttach() {
		return attach;
	}

	public void setAttach(Object attach) {
		this.attach = attach;
	}
}
