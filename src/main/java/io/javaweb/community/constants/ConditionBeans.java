package io.javaweb.community.constants;

import io.javaweb.community.entity.FriendLinkEntity;
import io.javaweb.community.entity.dto.PostDTO;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.utils.PageUtils;

public class ConditionBeans {
	
	//置顶帖条件bean
	public static final PostDTO TOP_POST = new PostDTO();
	
	//置顶条件PageBounds
	public static final PageBounds TOP_PAGE_BOUNDS = PageUtils.getPageBoundsNoneTotalCount(1, 10, PageUtils.getOrderDesc("createDate"));
	
	//友情连接
	public static final FriendLinkEntity FRIEND_LINK_ENTITY = new FriendLinkEntity();
	
	//置顶条件PageBounds
	public static final PageBounds FRIEND_LINK_BOUNDS = PageUtils.getPageBoundsNoneTotalCount(1, 100, PageUtils.getOrderDesc("sorted"));
	
	static {
		TOP_POST.setTop(Boolean.TRUE);
	}
}
