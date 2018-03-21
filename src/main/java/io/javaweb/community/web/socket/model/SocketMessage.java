package io.javaweb.community.web.socket.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by KevinBlandy on 2018/1/18 16:28
 */
public class SocketMessage implements Serializable{

    private static final long serialVersionUID = -2390645939054358096L;

    public enum Type{

        REGISTER,

        LOGIN,

        POST,

        REPLY,

        READING,

        READ,
    }

    public SocketMessage(){}

    public SocketMessage(Type type,String content){
        this.type = type;
        this.content = content;
    }

    private Type type;

    private String content;

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
        if(this.date == null){
            this.date = new Date();
        }
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
