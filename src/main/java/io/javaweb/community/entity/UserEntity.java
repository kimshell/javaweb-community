package io.javaweb.community.entity;

import com.alibaba.fastjson.annotation.JSONField;

import io.javaweb.community.common.BaseEntity;
import io.javaweb.community.enums.Gender;
import io.javaweb.community.enums.Role;
import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;

/**
 * 
 * @author KevinBlandy
 *
 */
@Entity(table = "jw_user",mapper = "io.javaweb.community.mapper.UserMapper")
public class UserEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6804972598563074356L;

	@Id
	private String userId;

	//邮箱
	private String email;

	//电话
	private String phone;
	
	//密码
	@JSONField(serialize = false)
	private String pass;
	
	//名字
	private String name;
	
	//年龄
	private Integer age;
	
	//性别
	private Gender gender;
	
	//头像
	private String portrait;
	
	//个人签名
	private String personality;
	
	//个人站点
	private String site;
	
	//github主页
	private String github;
	
	//角色
	private Role role;
	
	//邮箱验证
	private Boolean emailVerifi;
	
	//手机验证
	private Boolean phoneVerifi;
	
	//登录广播
	private Boolean loginRadio;
	//浏览广播
	private Boolean browseRadio;
	//回复广播
	private Boolean replyRadio;
	
	//会话id
	@JSONField(serialize = false)
	private String sessionId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getPersonality() {
		return personality;
	}

	public void setPersonality(String personality) {
		this.personality = personality;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getGithub() {
		return github;
	}

	public void setGithub(String github) {
		this.github = github;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean getEmailVerifi() {
		return emailVerifi;
	}

	public void setEmailVerifi(Boolean emailVerifi) {
		this.emailVerifi = emailVerifi;
	}

	public Boolean getPhoneVerifi() {
		return phoneVerifi;
	}

	public void setPhoneVerifi(Boolean phoneVerifi) {
		this.phoneVerifi = phoneVerifi;
	}
	
	public Boolean getLoginRadio() {
		return loginRadio;
	}

	public void setLoginRadio(Boolean loginRadio) {
		this.loginRadio = loginRadio;
	}

	public Boolean getBrowseRadio() {
		return browseRadio;
	}

	public void setBrowseRadio(Boolean browseRadio) {
		this.browseRadio = browseRadio;
	}

	public Boolean getReplyRadio() {
		return replyRadio;
	}

	public void setReplyRadio(Boolean replyRadio) {
		this.replyRadio = replyRadio;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "UserEntity [userId=" + userId + ", email=" + email + ", phone=" + phone + ", pass=" + pass + ", name="
				+ name + ", age=" + age + ", gender=" + gender + ", portrait=" + portrait + ", personality="
				+ personality + ", site=" + site + ", github=" + github + ", role=" + role + ", emailVerifi="
				+ emailVerifi + ", phoneVerifi=" + phoneVerifi + ", sessionId=" + sessionId + "]";
	}
}
