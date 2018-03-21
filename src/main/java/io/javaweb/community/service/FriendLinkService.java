package io.javaweb.community.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.javaweb.community.entity.FriendLinkEntity;
import io.javaweb.community.service.general.GeneralService;

@Service
public class FriendLinkService extends GeneralService<FriendLinkEntity>{

	//根据id批量的删除记录
	@Transactional(rollbackFor = Exception.class)
	public void batchDeleteById(String[] firendLinkIds) throws Exception {
		for(String friendLinkId : firendLinkIds) {
			super.deleteByPrimaryKey(friendLinkId);
		}
	}
}
