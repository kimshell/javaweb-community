package io.javaweb.community.common;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.enums.Status;
import io.javaweb.community.generate.Ignore;

/**
 * Created by KevinBlandy on 2017/10/30 16:56
 */
public abstract class BaseEntity implements Serializable{

    private static final long serialVersionUID = -6026749715808202093L;
    
    //创建用户
    private String createUser;
    
    //创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    
    //最后一次修改时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyDate;
    
    //记录状态
    private Status status;
    
    //备注信息
    private String remark;
    
    //排序字段
    private Long sorted;

    //当前会话用户
    @Ignore
    private UserEntity sessionUser;
    
	public String getCreateUser() {
		return createUser;
	}
	
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public Date getModifyDate() {
		return modifyDate;
	}
	
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Long getSorted() {
		return sorted;
	}
	
	public void setSorted(Long sorted) {
		this.sorted = sorted;
	}

	public UserEntity getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(UserEntity sessionUser) {
		this.sessionUser = sessionUser;
	}
}
