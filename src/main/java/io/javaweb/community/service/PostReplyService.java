package io.javaweb.community.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.javaweb.community.entity.MessageEntity;
import io.javaweb.community.enums.MessageType;
import io.javaweb.community.utils.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.javaweb.community.entity.PostEntity;
import io.javaweb.community.entity.PostReplyEntity;
import io.javaweb.community.entity.UserEntity;
import io.javaweb.community.entity.dto.PostReplyDTO;
import io.javaweb.community.enums.ReplyControl;
import io.javaweb.community.enums.Status;
import io.javaweb.community.exception.ServiceException;
import io.javaweb.community.mapper.PostReplyMapper;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;
import io.javaweb.community.service.general.GeneralService;
import io.javaweb.community.utils.MentionUtils;
import io.javaweb.community.web.support.SessionHolder;

@Service
public class PostReplyService extends GeneralService<PostReplyEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostReplyService.class);
	
	@Autowired
	private PostService postService;

	@Autowired
    private MessageService messageService;
	
	private PostReplyMapper getMapper() {
		return (PostReplyMapper) super.getBaseMapper();
	}
	
	//根据帖子id分页检索回复信息
	@Transactional(readOnly = true,rollbackFor = Exception.class)
	public PageList<PostReplyDTO> queryReplysByPostId(PostReplyDTO postReplyDTO,PageBounds pageBounds)throws Exception{
		return this.getMapper().queryReplysByPostId(postReplyDTO,pageBounds);
	}
	
	//根据帖子id检索回复记录数
	@Transactional(readOnly = true,rollbackFor = Exception.class)
	public Integer queryReplyCountByPostId(String postId) throws Exception{
		return this.getMapper().queryReplyCountByPostId(postId);
	}
	
	//创建新回复,返回该回复存在的页码
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> createReply(PostReplyEntity postReplyEntity) throws Exception{
		
		Map<String,Object> map = new HashMap<>();
		
		PostEntity postEntity = this.postService.queryByPrimaryKey(postReplyEntity.getPostId());

		if(postEntity == null || postEntity.getStatus().equals(Status.DELETED)) {
			throw new ServiceException("帖子信息不存在");
		}
		if(postEntity.getStatus().equals(Status.DISABLE)) {
			throw new ServiceException(io.javaweb.community.common.Message.Status.POST_IS_DISABLED);
		}
		
		map.put("post", postEntity);
		
		UserEntity sessionUser = SessionHolder.USER_SESSION.get().getUser();


		if(postEntity.getReplyControl().equals(ReplyControl.DISALLOW)) {
			throw new ServiceException("该贴不允许回复");
		}else if(postEntity.getReplyControl().equals(ReplyControl.DISALLOW_ANONYMOUS)) {
			if(postEntity.getAnonymous()) {
				throw new ServiceException("该贴不允许匿名回复");
			}
		}else if(postEntity.getReplyControl().equals(ReplyControl.DISALLOW_OTHER)) {
			if(!postReplyEntity.getCreateUser().equals(postEntity.getCreateUser())) {
				throw new ServiceException("该贴不允许回复");
			}
		}
		
		PostReplyEntity parentReply = null;
		
		if(!postReplyEntity.getParentId().equals(PostReplyEntity.DEFAULT_PARENT_ID)) {
			parentReply = super.queryByPrimaryKey(postReplyEntity.getParentId());
			if(parentReply == null || !parentReply.getPostId().equals(postReplyEntity.getPostId())) {
				throw new ServiceException("回复信息不存在");
			}
			if(!parentReply.getIsParent()) {
				parentReply.setIsParent(Boolean.TRUE);
                super.updateByPrimaryKeySelective(parentReply);
			}

            LOGGER.debug("回复父级:{}",parentReply);

            /**
             * 新的回复,通知父级评论作者
             */
            if(!parentReply.getCreateUser().equals(sessionUser.getUserId())){
                //父级评论,不是自己发布的
                MessageEntity replyMessage = new MessageEntity();
                replyMessage.setMessageId(GeneralUtils.getUUID());
                replyMessage.setPostId(postEntity.getPostId());
                replyMessage.setReplyId(postReplyEntity.getReplyId());
                replyMessage.setCreateDate(new Date());
                replyMessage.setIsRead(false);
                replyMessage.setCreateUser(sessionUser.getUserId());
                replyMessage.setType(MessageType.REPLY_REPLY);
                replyMessage.setUserId(parentReply.getCreateUser());
                this.messageService.create(replyMessage);
            }
		}
		
		//提取@用户,渲染正文
    	Map<String,UserEntity> userMap = new HashMap<>();
    	
    	postReplyEntity.setContent(MentionUtils.mentionParse(postReplyEntity.getContent(), userMap));
    	
    	//页码检索
		Integer page = 1;
		Integer totalReplyCount = this.queryReplyCountByPostId(postReplyEntity.getPostId()) + 1;
		if(totalReplyCount > 20) {
			if((totalReplyCount % 20) == 0) {
				page =  totalReplyCount / 20;
			}else {
				page =  (totalReplyCount / 20) + 1;
			}
		}
		postReplyEntity.setPage(page);
		
		//新的回复创建
		super.create(postReplyEntity);

        /**
         * 存在@用户,存入通知
         */
        if(!userMap.isEmpty()) {

            LOGGER.debug("存在@信息");

            MessageEntity atNotifyMessage = new MessageEntity();
            atNotifyMessage.setPostId(postEntity.getPostId());
            atNotifyMessage.setReplyId(postReplyEntity.getReplyId());
            atNotifyMessage.setIsRead(false);
            atNotifyMessage.setCreateDate(new Date());
            atNotifyMessage.setCreateUser(sessionUser.getUserId());
            atNotifyMessage.setType(MessageType.REPLY_AT);

            for(Map.Entry<String, UserEntity> entry : userMap.entrySet()) {
                UserEntity user = entry.getValue();
                //@到的人不是自己
                if(!user.getUserId().equals(sessionUser.getUserId())) {
                	if(parentReply != null) {
                	    //@ 到的人是父级评论楼主 - continue
                		if(parentReply.getCreateUser().equals(user.getUserId())) {
                			continue;
                		}
                	}
                    atNotifyMessage.setUserId(user.getUserId());
                    atNotifyMessage.setAtAnchor(entry.getKey());
                    atNotifyMessage.setMessageId(GeneralUtils.getUUID());
                    LOGGER.debug("@用户:{}",atNotifyMessage);
                    this.messageService.create(atNotifyMessage);
                }
            }
        }
        map.put("page", page);

        if(parentReply == null && !postEntity.getCreateUser().equals(sessionUser.getUserId())){
            LOGGER.debug("通知楼主");
            /**
             * 通知帖主
             * 帖主不是自己,并且是一级回复
             */
            MessageEntity replyMessage = new MessageEntity();
            replyMessage.setMessageId(GeneralUtils.getUUID());
            replyMessage.setPostId(postEntity.getPostId());
            replyMessage.setReplyId(postReplyEntity.getReplyId());
            replyMessage.setCreateDate(new Date());
            replyMessage.setIsRead(false);
            replyMessage.setCreateUser(sessionUser.getUserId());
            replyMessage.setType(MessageType.POST_REPLY);
            replyMessage.setUserId(postEntity.getCreateUser());
            this.messageService.create(replyMessage);
        }
		return map;
	}
}
