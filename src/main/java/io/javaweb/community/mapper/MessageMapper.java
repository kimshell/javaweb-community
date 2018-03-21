package io.javaweb.community.mapper;

import org.springframework.stereotype.Repository;

import io.javaweb.community.common.BaseMapper;
import io.javaweb.community.entity.MessageEntity;
import io.javaweb.community.entity.dto.MessageDTO;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;

@Repository
public interface MessageMapper extends BaseMapper<MessageEntity> {
	
	//检索用户的消息通知
	PageList<MessageDTO> queryMessagesByUserId(MessageDTO message,PageBounds pageBounds)throws Exception;
	
	//根据用户id,检索其未读消息数量
	Integer queryUnreadMessageCountByUserId(String userId)throws Exception;
}
