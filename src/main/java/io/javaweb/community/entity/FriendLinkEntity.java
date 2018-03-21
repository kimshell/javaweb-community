package io.javaweb.community.entity;

import io.javaweb.community.common.BaseEntity;
import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;
/**
 * 
 * 友情连接
 * @author Kevin
 *
 */
@Entity(table = "jw_friendlink", mapper = "io.javaweb.community.mapper.FriendLinkMapper")
public class FriendLinkEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2603026127841045047L;
	
	@Id
	private String frendLinkId;

	//站点名称
	private String name;
	
	//url地址
	private String url;
	
	//logo
	private String logo;

	public String getFrendLinkId() {
		return frendLinkId;
	}

	public void setFrendLinkId(String frendLinkId) {
		this.frendLinkId = frendLinkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
}
