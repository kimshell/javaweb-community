package io.javaweb.community.entity;

import io.javaweb.community.common.BaseEntity;

/**
 * ip实体抽象
 * @author KevinBlandy
 *
 */
public abstract class AbsIpInfoEntity extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4449592657375359971L;

	private String ip;			//IP地址
	
	private String address;		//地区
	
	private String operator;	//运营商

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
