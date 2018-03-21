package io.javaweb.community.service;

import io.javaweb.community.entity.MessageEntity;
import io.javaweb.community.entity.dto.MessageDTO;
import io.javaweb.community.mapper.MessageMapper;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;
import io.javaweb.community.service.general.GeneralService;
import io.javaweb.community.utils.GeneralUtils;
import io.javaweb.community.web.support.SessionHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by KevinBlandy on 2018/1/29 12:58
 */
@Service
public class MessageService extends GeneralService<MessageEntity> {
	
	private MessageMapper getMapper() {
		return (MessageMapper) super.getBaseMapper();
	}
	
	//检索用户的消息通知
	@Transactional(rollbackFor = Exception.class)
	public PageList<MessageDTO> queryMessagesByUserId(MessageDTO message,PageBounds pageBounds)throws Exception{
		return this.getMapper().queryMessagesByUserId(message,pageBounds);
	}

    //根据用户id检索其未读消息数量
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public Integer queryUnreadMessageCountByUserId(String userId) throws Exception{
        return this.getMapper().queryUnreadMessageCountByUserId(userId);
    }

    //删除
    @Transactional(readOnly = false,rollbackFor = Exception.class)
	public void batchDeleteMessage(String[] messageIds) throws Exception{
		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setUserId(SessionHolder.USER_SESSION.get().getUser().getUserId());
		for(String messageId : messageIds) {
			if(GeneralUtils.isEmpty(messageId)) {
				continue;
			}
			messageEntity.setMessageId(messageId);
			super.deleteByParamSelective(messageEntity);
		}
	}

    //批量已读消息
	@Transactional(rollbackFor = Exception.class)
	public void batchRedingMessage(String[] messageIds) throws Exception{
		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setIsRead(true);
		for(String messageId : messageIds) {
			if(GeneralUtils.isEmpty(messageId)) {
				continue;
			}
			/**
			 * 没有校验,消息是不是当前登录用户的
			 */
			messageEntity.setMessageId(messageId);
			super.updateByPrimaryKeySelective(messageEntity);
		}
	}
}
