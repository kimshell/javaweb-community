package io.javaweb.community.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.javaweb.community.entity.AttitudeEntity;
import io.javaweb.community.entity.MessageEntity;
import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.PostReplyEntity;
import io.javaweb.community.enums.AttitudeTarget;
import io.javaweb.community.enums.AttitudeType;
import io.javaweb.community.enums.MessageType;
import io.javaweb.community.enums.Status;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.mapper.AttitudeMapper;
import io.javaweb.community.service.general.GeneralService;
import io.javaweb.community.utils.GeneralUtils;

@Service
public class AttitudeService extends GeneralService<AttitudeEntity> {
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private PostReplyService postReplyService;
	
	@Autowired
	private MessageService messageService;
	
	private AttitudeMapper getMapper() {
		return (AttitudeMapper) super.getBaseMapper();
	}

	//发表态度
	@Transactional(rollbackFor = Exception.class)
	public void createAttitude(AttitudeEntity attitudeEntity) throws Exception {
		
		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setMessageId(GeneralUtils.getUUID());
		messageEntity.setCreateUser(attitudeEntity.getCreateUser());
		messageEntity.setCreateDate(new Date());
		messageEntity.setIsRead(false);
		
		if(attitudeEntity.getTarget().equals(AttitudeTarget.POST)) {
			PostEntity postEntity = this.postService.queryByPrimaryKey(attitudeEntity.getTargetId());
			if(postEntity == null || postEntity.getStatus().equals(Status.DELETED)) {
				throw new ServiceException("帖子信息不存在");
			}
			if(postEntity.getStatus().equals(Status.DISABLE)) {
				throw new ServiceException(io.javaweb.community.common.Message.Status.POST_IS_DISABLED);
			}
			
			if(!postEntity.getCreateUser().equals(attitudeEntity.getCreateUser())) {
				messageEntity.setUserId(postEntity.getCreateUser());
				messageEntity.setPostId(postEntity.getPostId());
				messageEntity.setType(attitudeEntity.getType().equals(AttitudeType.AGREE) ? MessageType.POST_AGREE : MessageType.POST_DIS_AGREE);
				this.messageService.create(messageEntity);
			}
			
		}else {
			PostReplyEntity postReplyEntity = this.postReplyService.queryByPrimaryKey(attitudeEntity.getTargetId());
			if(postReplyEntity == null || postReplyEntity.getStatus().equals(Status.DELETED)) {
				throw new ServiceException("回复信息不存在");
			}
			if(postReplyEntity.getStatus().equals(Status.DISABLE)) {
				throw new ServiceException("该回复已经被删除");
			}
			if(!postReplyEntity.getCreateUser().equals(attitudeEntity.getCreateUser())) {
				messageEntity.setUserId(postReplyEntity.getCreateUser());
				//该回复的帖子id
				messageEntity.setPostId(postReplyEntity.getPostId());
				messageEntity.setReplyId(postReplyEntity.getReplyId());
				messageEntity.setType(attitudeEntity.getType().equals(AttitudeType.AGREE) ? MessageType.REPLY_AGREE : MessageType.REPLY_DIS_AGREE);
				this.messageService.create(messageEntity);
			}
		}
		
		
		
		if(super.queryByParamSelectiveSole(attitudeEntity) != null) {
			throw new ServiceException("重复操作,已经发表过态度");
		}
		
		attitudeEntity.setCreateDate(new Date());
		attitudeEntity.setAttitudeId(GeneralUtils.getUUID());
		
		super.create(attitudeEntity);
	}

	//删除态度 
	@Transactional(rollbackFor = Exception.class)
	public void deleteAttiude(AttitudeEntity attitudeEntity) throws Exception{
		
		MessageEntity messageEntity = new MessageEntity();
		
		messageEntity.setCreateUser(attitudeEntity.getCreateUser());
		
		switch (attitudeEntity.getTarget()) {
			case POST:{
				if(attitudeEntity.getType().equals(AttitudeType.AGREE)) {
					messageEntity.setType(MessageType.POST_AGREE);
				}else {
					messageEntity.setType(MessageType.POST_DIS_AGREE);
				}
				messageEntity.setPostId(attitudeEntity.getTargetId());
				break;
			}
			case POST_REPLY:{
				if(attitudeEntity.getType().equals(AttitudeType.AGREE)) {
					messageEntity.setType(MessageType.REPLY_AGREE);
				}else {
					messageEntity.setType(MessageType.REPLY_DIS_AGREE);
				}
				messageEntity.setReplyId(attitudeEntity.getTargetId());
				break;
			}
		}
		
		this.messageService.deleteByParamSelective(messageEntity);
		super.deleteByParamSelective(attitudeEntity);
	}

	//检索数量
	@Transactional(readOnly = true,rollbackFor = Exception.class)
	public int queryCount(String targetId, AttitudeTarget target, AttitudeType type) throws Exception {
		return this.getMapper().queryCount(targetId,target,type);
	}
}




